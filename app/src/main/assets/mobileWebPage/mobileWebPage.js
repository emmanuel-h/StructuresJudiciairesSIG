var map;
var longitude;
var latitude;
var vectorLayerTi;
var vectorLayerTgi;
var vectorLayerListeGreffes;
var vectorLayerLieuxJustice;

var pathToInternalStorage;

function updateLocation(long, lat){
    longitude = long;
    latitude = lat;
}

function centerMap(){
   map.getView().setCenter(ol.proj.transform([longitude, latitude], 'EPSG:4326', 'EPSG:3857'));
}

function clearItinerary(){
    map.removeLayer(itineraryLine);
}

function dispLayer(layer, display){
    switch(layer){
        case "vectorLayerTi":
            vectorLayerTi.setVisible(display);
            break;
        case "vectorLayerTgi":
            vectorLayerTgi.setVisible(display);
            break;
        case "vectorLayerListeGreffes":
            vectorLayerListeGreffes.setVisible(display);
            break;
        case "vectorLayerLieuxJustice":
            vectorLayerLieuxJustice.setVisible(display);
            break;
        default:
            break;
    }
}

function afficherMap(longitude, latitude, path_to_internal_storage){
  pathToInternalStorage = 'http://172.20.10.4:8080/structuresJudiciaires';
  vectorLayerTi = createLayer(path_to_internal_storage+'/annuaire_ti.json','#ff0000',3,0.1,2500);
  vectorLayerTgi = createLayer(path_to_internal_storage+'/annuaire_tgi.json','#00ff00',4,0.1,5000);
  vectorLayerListeGreffes = createLayer(path_to_internal_storage+'/liste-des-greffes.json','#0000ff',2,0.1,3000);
  vectorLayerLieuxJustice = createLayer(path_to_internal_storage+'/annuaire_lieux_justice.json','#ffff00',1,0.1,500);

  pathToInternalStorage = path_to_internal_storage;

//création de la map avec OSM et les differents layers
  map = new ol.Map({
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM(),
          strategy: ol.loadingstrategy.bbox
        }),
        vectorLayerTi, vectorLayerLieuxJustice, vectorLayerListeGreffes, vectorLayerTgi
      ],
      target: 'map',
      view: new ol.View({
        center: ol.proj.transform([+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
        zoom: 11
      })
  });


//catch de l'event de mouvement de la map et application de la fonction de changement de style des layers
map.on('moveend',changeStyle);

//fonction pour changer le style des layers en fonction du niveau de zoom
function changeStyle(){
  var newZoom = map.getView().getZoom();
  newZoom > 10 ? vectorLayerTgi.setStyle(styleFunction("#00ff00",12)) : vectorLayerTgi.setStyle(styleFunction('#00ff00',5)) ;
  newZoom > 12 ? vectorLayerTi.setStyle(styleFunction("#ff0000",8)) : vectorLayerTi.setStyle(styleFunction('#ff0000',5)) ;
  newZoom > 10 ? vectorLayerListeGreffes.setStyle(styleFunction("#0000ff",12)) : vectorLayerListeGreffes.setStyle(styleFunction('#0000ff',5)) ;
  newZoom > 12 ? vectorLayerLieuxJustice.setStyle(styleFunction("#ffff00",8)) : vectorLayerLieuxJustice.setStyle(styleFunction('#ffff00',5)) ;
};

//fonction pour recuperer un style de point avec une couleur et un rayon
function styleFunction(colorP,rad){
  var style = new ol.style.Style({
    image: new ol.style.Circle(({
      radius: rad,
      fill: new ol.style.Fill({
        color: colorP
      })
    }))
  });
  return style;
}

//fonction pour creer les layers en fonction du fichier, de la couleur des points, du positionnement en Z et l'intervalle d'affichage(min/max)
function createLayer(file,colorPoint,index,minR,maxR){
  var vectorLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      format: new ol.format.GeoJSON({
        defaultDataProjection:'EPSG:4326',
        projection:'EPSG:3857'
      }),
      url: file,
      strategy: ol.loadingstrategy.bbox
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
    maxResolution: maxR,
    zIndex: index
  })
  return vectorLayer;
}

}
