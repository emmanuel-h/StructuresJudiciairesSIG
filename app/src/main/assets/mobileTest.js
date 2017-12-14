var map;

function afficherMap(longitude, latitude){

          var vectorLayer = new ol.layer.Vector({
              source: new ol.source.Vector({
                  format: new ol.format.GeoJSON({

    			defaultDataProjection:'EPSG:4326',
    			projection:'EPSG:3857'
                  }),
                  url: 'annuaire_ti.json'
              }),
              style: new ol.style.Style({
                  image: new ol.style.Circle(({
                      radius: 20,
                      fill: new ol.style.Fill({
                          color: '#ffff00'
                      })
                  }))
              })
          });

    var map = new ol.Map({
            layers: [
              new ol.layer.Tile({
                source: new ol.source.OSM()
              }),
              vectorLayer
            ],
            target: 'map',
            view: new ol.View({
              center: ol.proj.transform(
                [+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
              zoom: 11
            })
          });
}
