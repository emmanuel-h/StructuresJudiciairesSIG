var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');
var details = document.getElementById('popup-details');
var buttons = document.getElementById('popup-buttons');

function setPathToInternalStorage(path){
    pathToInternalStorage = path;
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
    if(feature){
        var properties = feature.getProperties();
        var coord = feature.getGeometry().getCoordinates();
        switch(layerUrl){
            case pathToInternalStorage+"/annuaire_lieux_justice.json":
            case pathToInternalStorage+"/annuaire_tgi.json":
            case pathToInternalStorage+"/annuaire_ti.json":
                annuaire(properties, coord);
                break;
            case pathToInternalStorage+"/liste-des-greffes.json":
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
                            '<button type="button" id="buttonDistance">Calculate Distance</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonDetails').onclick = function(){
            details.innerHTML = 'lol';
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall('0624766258');
        }
        document.getElementById('buttonDistance').onclick = function(){
            JSInterface.calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
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
                                    '<button type="button" id="buttonDistance">Calculate Distance</button>';
        document.getElementById('buttonLoadSite').onclick = function(){
            JSInterface.loadWebsite(properties.URL);
        }
        document.getElementById('buttonDetails').onclick = function(){
            details.innerHTML = 'lol';
        }
        document.getElementById('buttonCall').onclick = function(){
            JSInterface.makeCall('0624766258');
        }
        document.getElementById('buttonDistance').onclick = function(){
            JSInterface.calcDistance(+properties.LONGITUDE, +properties.LATITUDE);
        }
    }
});




