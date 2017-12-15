var map;
var overlay = new ol.Overlay({
    element:container,
    autopan:true,
    autoPanAnimation: {
        duration:250
    }
});

var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');

closer.onclick = function(){
    overlay.setPosition(undefined);
    closer.blur();
    return false;
};

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

var vectorLayerTi = createLayer('annuaire_ti.json','#ff0000',3,0.1,2500);
var vectorLayerTgi = createLayer('annuaire_tgi.json','#00ff00',4,0.1,5000);
var vectorLayerListeGreffes = createLayer('liste-des-greffes.json','#0000ff',2,0.1,3000);
var vectorLayerLieuxJustice = createLayer('annuaire_lieux_justice.json','#ffff00',1,0.1,500);

vectorLayerTi.on('singleclick', function(event){
    var coordinate = event.coordinate;
    content.innerHTML = '<p>Click click TI</p>';
    overlay.setPosition(coordinate);
});
vectorLayerTgi.on('singleclick', function(event){
    var coordinate = event.coordinate;
    content.innerHTML = '<p>Click click TGI</p>';
    overlay.setPosition(coordinate);
});
vectorLayerListeGreffes.on('singleclick', function(event){
    var coordinate = event.coordinate;
    content.innerHTML = '<p>Click click GREFFES</p>';
    overlay.setPosition(coordinate);
});
vectorLayerLieuxJustice.on('singleclick', function(event){
    var coordinate = event.coordinate;
    content.innerHTML = '<p>Click click JUSTICE</p>';
    overlay.setPosition(coordinate);
});

    var map = new ol.Map({
            layers: [
              new ol.layer.Tile({
                source: new ol.source.OSM()
              }),
              vectorLayerTi, vectorLayerLieuxJustice, vectorLayerListeGreffes, vectorLayerTgi
            ],
            overlays: [overlay],
            target: 'map',
            view: new ol.View({
              center: ol.proj.transform(
                [+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
              zoom: 11
            })
          });

}


function createLayer(file,colorPoint,zIndex,minR,maxR){
    var vectorLayer = new ol.layer.Vector({
            source: new ol.source.Vector({
                format: new ol.format.GeoJSON({
                    defaultDataProjection:'EPSG:4326',
                    projection:'EPSG:3857'
                }),
                url: file
            }),
            style: new ol.style.Style({
                image: new ol.style.Circle(({
                       radius: 5,
                       fill: new ol.style.Fill({
                         color: colorPoint
                       })
                }))
             }),
            minResolution: minR,
            maxResolution: maxR
        })
        vectorLayer.setZIndex(zIndex);
        return vectorLayer
}