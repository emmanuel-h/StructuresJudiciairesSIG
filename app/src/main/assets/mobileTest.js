var map;

function afficherMap(longitude, latitude){

var vectorLayerTi = createLayer('annuaire_ti.json','#ff0000',3,10,2500);
var vectorLayerTgi = createLayer('annuaire_tgi.json','#00ff00',4,10,5000);
var vectorLayerListeGreffes = createLayer('liste-des-greffes.json','#0000ff',2,10,3000);
var vectorLayerLieuxJustice = createLayer('annuaire_lieux_justice.json','#ffff00',1,10,500);


    var map = new ol.Map({
            layers: [
              new ol.layer.Tile({
                source: new ol.source.OSM()
              }),
              vectorLayerTi, vectorLayerLieuxJustice, vectorLayerListeGreffes, vectorLayerTgi
            ],
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