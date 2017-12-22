var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');
var details = document.getElementById('popup-details');
var buttons = document.getElementById('popup-buttons');
var vectorLine;
var itineraryLine;
var addLawyer= false;

function setPathToInternalStorage(path){
    pathToInternalStorage = path;
}

function setAddLawyer(value){
    addLawyer = value;
    if(!addLawyer){
        overlay.setPosition(undefined);
        closer.blur();
    }
}

function sendDataToServer(serverAddress, serverPort, longitude, latitude, name, forename, address, phone, profession){
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "http://" + serverAddress + ":" + serverPort, true);
    xhttp.send("&longitude=" + longitude + "&latitude=" + latitude + "&nom=" + name
            + "&prenom=" + forename + "&adresse=" + address + "&telephone=" + phone
            + "&profession=" + profession);
    overlay.setPosition(undefined);
    closer.blur();
}

var overlay = new ol.Overlay({
    element:container,
    autopan:true,
    autoPanAnimation: {
        duration:250
    }
});

map.addOverlay(overlay);

closer.onclick = function(){
    details.innerHTML = "";
    overlay.setPosition(undefined);
    map.removeLayer(vectorLine);
    closer.blur();
    return false;
};

map.on('click', function(event){

    var coordinate = event.coordinate;
    //var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
    var layerUrl;
    var feature = map.forEachFeatureAtPixel(event.pixel, function(feature, layer){
        layerUrl = layer.getSource().getUrl();
        return feature;
    }, 125);
    if(addLawyer){
        coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
        content.innerHTML = '<h3>Adding a lawyer</h3>';
        buttons.innerHTML = '<button type="button" id="buttonAddLawyer">Add a lawyer</button>';
        document.getElementById('buttonAddLawyer').onclick = function(){
            JSInterface.addLawyer(+coordProj[0], +coordProj[1]);
            //sendDataToServer();
        }
        overlay.setPosition(coordinate);
    }else{
        if(feature){
            var properties = feature.getProperties();
            var coord = feature.getGeometry().getCoordinates();
            switch(layerUrl){
                case pathToInternalStorage+"/annuaire_lieux_justice.json":
                case pathToInternalStorage+"/annuaire_tgi.json":
                case pathToInternalStorage+"/annuaire_ti.json":
                    annuaire(properties, coord);
                    break;
                case pathToInternalStorage+"/liste_des_greffes.json":
                    listeGreffes(properties, coord);
                    break;
                default:
                    content.innerHTML = '<h3>Could not retrieve data for this point</h3>';
                    break;
            }
            overlay.setPosition(coord);
        }else{
                details.innerHTML = "";
                overlay.setPosition(undefined);
                map.removeLayer(vectorLine);
            }
    }

    function annuaire(properties, coord){
        content.innerHTML = '<h3>' + properties.INTITULE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.COMMUNE + '<br>' +
                                properties.TELEPHONE + '<br>' +
                                properties.HORAIRES_O +
                            '</p>';
        buttons.innerHTML = '<button type="button" id="buttonLoadSite">Website</button>' +
                            '<button type="button" id="buttonDetails">Details</button>' +
                            '<button type="button" id="buttonCall">Call</button>' +
                            '<button type="button" id="buttonDistance">Distance</button>' +
                            '<button type="button" id="buttonItinerary">Itinerary</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonDetails').onclick = function(){
            details.innerHTML = 'show details';
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall('0624766258');
        }
        document.getElementById('buttonDistance').onclick = function(){
            //JSInterface.calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
            calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
        }
        document.getElementById('buttonItinerary').onclick = function(){
            calcItinerary(+properties.LONGITUDE, +properties.LATITUDE);
        }
    }
    function listeGreffes(properties, coord){
        content.innerHTML = '<h3>Greffe de ' + properties.GREFFE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.VILLE + '<br>' +
                                '0' + properties.TELEPHONE + '<br>' +
                                properties.HORAIRES +
                            '</p>';
        buttons.innerHTML = '<button type="button" id="buttonLoadSite">Website</button>' +
                                    '<button type="button" id="buttonDetails">Details</button>' +
                                    '<button type="button" id="buttonCall">Call</button>' +
                                    '<button type="button" id="buttonDistance">Distance</button>' +
                                    '<button type="button" id="buttonItinerary">Itinerary</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonDetails').onclick = function(){
            details.innerHTML = 'view details';
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall('0624766258');
        }
        document.getElementById('buttonDistance').onclick = function(){
            calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
        }
        document.getElementById('buttonItinerary').onclick = function(){
            calcItinerary(+properties.LONGITUDE, +properties.LATITUDE);
        }
    }
    function calcDistance(destLongitude, destLatitude){
        var lat1 = latitude * Math.PI / 180;
        var lat2 = destLatitude * Math.PI / 180;
        var long1 = longitude * Math.PI / 180;
        var long2 = destLongitude * Math.PI / 180;
        var rayon = 6371;
        var distance = rayon * Math.acos(Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - long1) + Math.sin(lat1) * Math.sin(lat2));
        details.innerHTML += '<p>Distance au point : ' + distance.toFixed(2) + ' Km</p>';

        var userPos = ol.proj.transform([+longitude,+latitude], 'EPSG:4326','EPSG:3857');
        var destPos = ol.proj.transform([+destLongitude,+destLatitude], 'EPSG:4326','EPSG:3857')
        var points = [userPos, destPos];
        var featureLine = new ol.Feature({
            geometry:new ol.geom.LineString(points)
        });
        var sourceVector = new ol.source.Vector({
            features:[featureLine]
        });
        vectorLine = new ol.layer.Vector({
            source:sourceVector,
            style:new ol.style.Style({
                stroke:new ol.style.Stroke({
                    color:'#33ccff',
                    width:3
                })
            }),
            zIndex:5
        });
        map.addLayer(vectorLine);

    }
    function calcItinerary(destLongitude, destLatitude){
        map.removeLayer(itineraryLine);
        const url = 'http://router.project-osrm.org/route/v1/driving/';
        const urlOptions = '?steps=true&geometries=geojson&overview=simplified';
        var finalUrl = url + longitude + ',' + latitude + ';' + destLongitude + ',' + destLatitude + urlOptions;
        fetch(finalUrl)
        .then((resp) => resp.json())
        .then(function(data){
            var coordinates = data.routes[0].geometry.coordinates;
            var coordProj = [];
            for(i = 0; i < coordinates.length; i++){
                var coordTemp = ol.proj.transform(coordinates[i], 'EPSG:4326', 'EPSG:3857');
                coordProj.push(coordTemp);
            }
            var featureLine = new ol.Feature({
                geometry:new ol.geom.LineString(coordProj)
            });
            var sourceVector = new ol.source.Vector({
                features:[featureLine]
            });
            itineraryLine = new ol.layer.Vector({
                source:sourceVector,
                style:new ol.style.Style({
                    stroke:new ol.style.Stroke({
                        color:'#7300e6',
                        width:5
                    })
                }),
                zIndex:5
            });
            map.addLayer(itineraryLine);
        });
    }
});




