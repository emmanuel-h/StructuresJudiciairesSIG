//the map of OpenLayers
var map;
//the longitude and the latitude of th user
var longitude;
var latitude;
//the different layers
var vectorLayerTi;
var vectorLayerTgi;
var vectorLayerListeGreffes;
var vectorLayerLieuxJustice;
var vectorLayerPersonne;
//the path to access to the different GeoJSON
var pathToInternalStorage;

//update the location
function updateLocation(long, lat){
    longitude = long;
    latitude = lat;
}

//center the map on the current position of the user
function centerMap(){
   map.getView().setCenter(ol.proj.transform([+longitude, +latitude], 'EPSG:4326', 'EPSG:3857'));
}

//clear the current itinerary
function clearItinerary(){
    map.removeLayer(itineraryLine);
}

//display or undisplay the selected layer
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
        case "vectorLayerPersonne":
            vectorLayerPersonne.setVisible(display);
        default:
            break;
    }
}

//global function which create the map with layers
function afficherMap(longitude, latitude, path_to_internal_storage){
  //pathToInternalStorage = 'http://172.20.10.4:8080/structuresJudiciaires';
  //create the 5 layers with their GeoJSON file and different properties
  vectorLayerTi = createLayer(path_to_internal_storage+'/annuaire_ti.json','#ff0000',3,0.1,2500);
  vectorLayerTgi = createLayer(path_to_internal_storage+'/annuaire_tgi.json','#00ff00',4,0.1,5000);
  vectorLayerListeGreffes = createLayer(path_to_internal_storage+'/liste_des_greffes.json','#0000ff',2,0.1,3000);
  vectorLayerLieuxJustice = createLayer(path_to_internal_storage+'/annuaire_lieux_justice.json','#ffff00',1,0.1,500);
  vectorLayerPersonne = createLayer(path_to_internal_storage+'/personne.json','#00ffff',1,0.1,500);
  pathToInternalStorage = path_to_internal_storage;

//create the OSM map and layers
  map = new ol.Map({
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM(),
          strategy: ol.loadingstrategy.bbox
        }),
        vectorLayerTi, vectorLayerLieuxJustice, vectorLayerListeGreffes, vectorLayerTgi, vectorLayerPersonne
      ],
      target: 'map',
      view: new ol.View({
        center: ol.proj.transform([+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
        zoom: 11
      })
  });


//catch the move event and change the style of layers
map.on('moveend',changeStyle);

//change the style of layers depending on the zoom level
function changeStyle(){
  var newZoom = map.getView().getZoom();
  newZoom > 10 ? vectorLayerTgi.setStyle(styleFunction("#00ff00",12)) : vectorLayerTgi.setStyle(styleFunction('#00ff00',5)) ;
  newZoom > 12 ? vectorLayerTi.setStyle(styleFunction("#ff0000",8)) : vectorLayerTi.setStyle(styleFunction('#ff0000',5)) ;
  newZoom > 10 ? vectorLayerListeGreffes.setStyle(styleFunction("#0000ff",12)) : vectorLayerListeGreffes.setStyle(styleFunction('#0000ff',5)) ;
  newZoom > 12 ? vectorLayerLieuxJustice.setStyle(styleFunction("#ffff00",8)) : vectorLayerLieuxJustice.setStyle(styleFunction('#ffff00',5)) ;
  newZoom > 12 ? vectorLayerPersonne.setStyle(styleFunction("#00ffff",8)) : vectorLayerPersonne.setStyle(styleFunction('#00ffff',5)) ;
};

//return a style with a specific color and radius of circle
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

//return a layer with a file path, color of point, index of display, min/max definiton to display
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
