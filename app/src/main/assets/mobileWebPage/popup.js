var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');
var details = document.getElementById('popup-details');
var buttons = document.getElementById('popup-buttons');
var vectorLine;
var itineraryLine;
var addLawyer= false;

// Gives the path to the internal storage so we can access it from JavaScript
function setPathToInternalStorage(path){
    pathToInternalStorage = path;
}

// Change the value of addLawyer : true if the user wants to add one, false otherwise
function setAddLawyer(value){
    addLawyer = value;
    overlay.setPosition(undefined);
    closer.blur();
    details.innerHTML = "";
    if(addLawyer){
        map.removeLayer(vectorLine);
    }
}
// Send datas from AddLawyerActivity to a python server with AJAX
function sendDataToServer(serverAddress, serverPort, longitude, latitude, name, forename, address, phone, profession){
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "http://" + serverAddress + ":" + serverPort, true);
    xhttp.send("&" + longitude + "&" + latitude + "&" + name
            + "&" + forename + "&" + address + "&" + phone
            + "&" + profession);
    overlay.setPosition(undefined);
    closer.blur();
}

// Create a new overlay that will be displayed on the map
var overlay = new ol.Overlay({
    element:container,
    autopan:true,
    autoPanAnimation: {
        duration:250
    }
});

// Adding the overlay to the map
map.addOverlay(overlay);

// Adding a function when clicking on the cross of the overlay
closer.onclick = function(){
    details.innerHTML = "";
    overlay.setPosition(undefined);
    map.removeLayer(vectorLine);
    closer.blur();
    return false;
};

// Setting function when user clicks on the map
map.on('click', function(event){

    // We get the input's coordinate
    var coordinate = event.coordinate;
    var layerUrl;
    // For each feature where user clicked, we get a possible layer. HitTolerance is set to 10 pixels,
    // so it's easier for the user to click on a point
    var feature = map.forEachFeatureAtPixel(event.pixel, function(feature, layer){
        layerUrl = layer.getSource().getUrl();
        return feature;
    }, {hitTolerance:5});
    // If the user clicks on the map and has clicked on the 'add lawyer' button
    if(addLawyer){
        coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
        // We set the content of the overlay to display that the user can add a lawyer where he clicked
        content.innerHTML = '<h3>Adding a lawyer</h3>';
        buttons.innerHTML = '<button type="button" id="buttonAddLawyer">Add a lawyer</button>';
        document.getElementById('buttonAddLawyer').onclick = function(){
            // If the user clicks on the button to finally add a lawyer, we call a Java function,
            // and we give coordinates of the future lawyer
            JSInterface.addLawyer(+coordProj[0], +coordProj[1]);
        }
        // We set the overlay where the user clicked
        overlay.setPosition(coordinate);
    // Else, the user is not willing to add a lawyer
    }else{
        // If there is any feature where the user clicked, e.g. if he clicked on a point
        if(feature){
            map.removeLayer(vectorLine);
            details.innerHTML = "";
            // We get the properties of the point
            var properties = feature.getProperties();
            var coord = feature.getGeometry().getCoordinates();
            switch(layerUrl){
                // If it's a 'lieux de justice', a 'TGI' or a 'TI', we execute the same function
                case pathToInternalStorage+"/annuaire_lieux_justice.json":
                case pathToInternalStorage+"/annuaire_tgi.json":
                case pathToInternalStorage+"/annuaire_ti.json":
                    annuaire(properties, coord);
                    break;
                // If it's a 'greffe'
                case pathToInternalStorage+"/liste_des_greffes.json":
                    listeGreffes(properties, coord);
                    break;
                // If it's a person (lawyer, notary, bailiff, ...)
                case pathToInternalStorage+"/personne.json":
                    personne(properties, coord);
                    break;
                // If we clicked on a point different from one of these types
                default:
                    content.innerHTML = '<h3>Could not retrieve data for this point</h3>';
                    break;
            }
            // We set the overlay where the user clicked
            overlay.setPosition(coord);
        // If there is no feature : the user clicked anywhere else on the map
        }else{
            // So we close the overlay, and remove the possible line showing raw distance between user and point
            details.innerHTML = "";
            overlay.setPosition(undefined);
            map.removeLayer(vectorLine);
        }
    }

    // Function to display the overlay of a 'personne'
    function personne(properties, coord){
            // We show his profession, name, forename, address, and phone number
            content.innerHTML = '<h3>' + properties.PROFESSION + '</h3>' +
                                '<p>' +
                                    properties.NOM + '<br>' +
                                    properties.PRENOM + '<br>' +
                                    properties.ADRESSE + '<br>' +
                                    properties.TELEPHONE
                                '</p>';
            // Buttons to call him, calculate the raw distance, or calculate an itinerary
            buttons.innerHTML = '<button type="button" id="buttonCall">Call</button>' +
                                '<button type="button" id="buttonDistance">Distance</button>' +
                                '<button type="button" id="buttonItinerary">Itinerary</button>';
            document.getElementById('buttonCall').onclick = function(){
                JSInterface.makeCall(properties.TELEPHONE);
            }
            document.getElementById('buttonDistance').onclick = function(){
                //JSInterface.calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
                calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
            }
            document.getElementById('buttonItinerary').onclick = function(){
                calcItinerary(+properties.LONGITUDE, +properties.LATITUDE);
            }
    }

    // Function to display the overlay of a 'lieux de justice', 'TGI', or 'TI'
    function annuaire(properties, coord){
        // We show the name of the building, the address, the zip code, the city,
        //  the phone number, and openning hours
        content.innerHTML = '<h3>' + properties.INTITULE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.COMMUNE + '<br>' +
                                properties.TELEPHONE + '<br>' +
                                properties.HORAIRES_O +
                            '</p>';
        // Buttons to go to the website (within the application), to call it, calculate the raw
        //  distance to it, or calculate an itinerary to it
        buttons.innerHTML = '<button type="button" id="buttonLoadSite">Website</button>' +
                            '<button type="button" id="buttonCall">Call</button>' +
                            '<button type="button" id="buttonDistance">Distance</button>' +
                            '<button type="button" id="buttonItinerary">Itinerary</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall(properties.TELEPHONE);
        }
        document.getElementById('buttonDistance').onclick = function(){
            calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
        }
        document.getElementById('buttonItinerary').onclick = function(){
            calcItinerary(+properties.LONGITUDE, +properties.LATITUDE);
        }
    }

    // Function to display the overlay of 'greffes'
    function listeGreffes(properties, coord){
        // We show its name, the address, the zip code, the city, the phone number,
        //  and the openning hours
        content.innerHTML = '<h3>Greffe de ' + properties.GREFFE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.VILLE + '<br>' +
                                '0' + properties.TELEPHONE + '<br>' +
                                properties.HORAIRES +
                            '</p>';
        // Buttons to go to their website (within the application), to call them, to calculate
        //  the raw distance to it, or calculate an itinerary
        buttons.innerHTML = '<button type="button" id="buttonLoadSite">Website</button>' +
                                    '<button type="button" id="buttonCall">Call</button>' +
                                    '<button type="button" id="buttonDistance">Distance</button>' +
                                    '<button type="button" id="buttonItinerary">Itinerary</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall(properties.TELEPHONE);
        }
        document.getElementById('buttonDistance').onclick = function(){
            calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
        }
        document.getElementById('buttonItinerary').onclick = function(){
            calcItinerary(+properties.LONGITUDE, +properties.LATITUDE);
        }
    }
    // Calculate the distance between the user's position (lon, lat) and a point (lon, lat)
    function calcDistance(destLongitude, destLatitude){
        var lat1 = latitude * Math.PI / 180;
        var lat2 = destLatitude * Math.PI / 180;
        var long1 = longitude * Math.PI / 180;
        var long2 = destLongitude * Math.PI / 180;
        var rayon = 6371;
        var distance = rayon * Math.acos(Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - long1) + Math.sin(lat1) * Math.sin(lat2));
        details.innerHTML = '<p>Distance to the point : ' + distance.toFixed(2) + ' Km</p>';

        var userPos = ol.proj.transform([+longitude,+latitude], 'EPSG:4326','EPSG:3857');
        var destPos = ol.proj.transform([+destLongitude,+destLatitude], 'EPSG:4326','EPSG:3857')
        var points = [userPos, destPos];
        var featureLine = new ol.Feature({
            geometry:new ol.geom.LineString(points)
        });
        var sourceVector = new ol.source.Vector({
            features:[featureLine]
        });
        // We add a light blue line from the user's position to the point
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

    // Function to calculate an itinerary between user's position (lon, lat) to the point (lon, lat)
    function calcItinerary(destLongitude, destLatitude){
        // We remove the possible previous itinerary to just show one at a time
        map.removeLayer(itineraryLine);
        // Webpage that gives us points between the two locations
        const url = 'http://router.project-osrm.org/route/v1/driving/';
        const urlOptions = '?steps=true&geometries=geojson&overview=simplified';
        var finalUrl = url + longitude + ',' + latitude + ';' + destLongitude + ',' + destLatitude + urlOptions;
        // We fetch datas from the site
        fetch(finalUrl)
        .then((resp) => resp.json())
        .then(function(data){
            // We get different locations between user and point, and transform them
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
            // We show a purple line, representing the itinerary between the user and the target
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




