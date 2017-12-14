var map;

function afficherMap(longitude, latitude){
    map = new ol.Map({
            layers: [
              new ol.layer.Tile({
                source: new ol.source.OSM()
              })
            ],
            target: 'map',
            view: new ol.View({
              center: ol.proj.transform(
                [+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
              zoom: 11
            })
          });
}