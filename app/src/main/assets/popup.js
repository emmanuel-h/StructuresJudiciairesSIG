var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');

var overlay = new ol.Overlay({
    element:container,
    autopan:true,
    autoPanAnimation: {
        duration:250
    }
});

map.addOverlay(overlay);

closer.onclick = function(){
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
    }, 75);
    if(feature){
        var properties = feature.getProperties();
        var coord = feature.getGeometry().getCoordinates();
        switch(layerUrl){
            case "annuaire_lieux_justice.json":
            case "annuaire_tgi.json":
            case "annuaire_ti.json":
                annuaire(properties, coord);
                break;
            case "liste-des-greffes.json":
                listeGreffes(properties, coord);
                break;
            default:
                break;
        }
        overlay.setPosition(coord);
        /*
        var properties = feature.getProperties();
        var coordFeature = ol.proj.transform([properties.LONGITUDE, properties.LATITUDE], 'EPSG:4326', 'EPSG:3857');
        var geom = feature.getGeometry().getCoordinates();
        content.innerHTML = '<h3>' + properties.INTITULE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.COMMUNE + '<br>' +
                                properties.TELEPHONE
                            '</p>';
        overlay.setPosition(geom);
        */
    }

    function annuaire(properties, coord){
        //var properties = feature.getProperties();
        //var coordFeature = feature.getGeometry().getCoordinates();
        content.innerHTML = '<h3>' + properties.INTITULE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.COMMUNE + '<br>' +
                                properties.TELEPHONE +
                            '</p>' +
                            '<button type="button">Website</button>';
    }
    function listeGreffes(properties, coord){
        content.innerHTML = '<h3>Greffe de' + properties.GREFFE + '</h3>' +
                            '<p>' +
                                properties.ADRESSE + '<br>' +
                                properties.CODE_POSTA + '<br>' +
                                properties.VILLE + '<br>' +
                                '0' + properties.TELEPHONE +
                            '</p>' +
                            '<button type="button">Website</button>';
    }
});


