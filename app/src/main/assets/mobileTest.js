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
          /*
          vectorLayer.on('click', function(event){
              var coordinate = ol.proj.transform(event.coordinate, 'EPSG:3857', 'EPSG:4326');
              window.alert(coordinate[0] + ", " + coordinate[1]);
          });
          */
          vectorLayer.on("singleclick", function(event){
              var feature = vectorLayer.getFeaturesAtPixel(event.pixel);
              if(null != feature){
                  window.alert("there are features :)");
                  e.preventDefault();
              }else{
                  window.alert("there are no features :(");
                  e.preventDefault();
              }
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
