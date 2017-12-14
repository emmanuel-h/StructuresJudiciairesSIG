var map;

function afficherMap(latitude, longitude){
    map = new ol.Map({
            layers: [
              new ol.layer.Tile({
                source: new ol.source.OSM()
              })
            ],
            target: 'map',
            view: new ol.View({
              center: ol.proj.transform(
                [+latitude,+longitude], 'EPSG:4326','EPSG:3857'),
              zoom: 11
            })
          });
}