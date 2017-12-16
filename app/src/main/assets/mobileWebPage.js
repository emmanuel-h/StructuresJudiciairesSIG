var map;

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

  /*
  vectorLayerTi.on('click', function(event){
  var coordinate = event.coordinate;
  var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
  content.innerHTML = '<p>Click click TI on ' + ol.coordinate.toStringXY(coordProj, 3) + '</p>';
  overlay.setPosition(coordinate);
});
vectorLayerTgi.on('click', function(event){
var coordinate = event.coordinate;
var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
content.innerHTML = '<p>Click click TGI on ' + ol.coordinate.toStringXY(coordProj, 3) + '</p>';
overlay.setPosition(coordinate);
});
vectorLayerListeGreffes.on('click', function(event){
var coordinate = event.coordinate;
var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
content.innerHTML = '<p>Click click GREFFES on ' + ol.coordinate.toStringXY(coordProj, 3) + '</p>';
overlay.setPosition(coordinate);
});
vectorLayerLieuxJustice.on('click', function(event){
var coordinate = event.coordinate;
var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
content.innerHTML = '<p>Click click LIEUX on ' + ol.coordinate.toStringXY(coordProj, 3) + '</p>';
overlay.setPosition(coordinate);
});
*/

//création de la map avec OSM et les differents layers
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
    center: ol.proj.transform([+longitude,+latitude], 'EPSG:4326','EPSG:3857'),
    zoom: 11
  })
});

map.on('click', function(event){
  var coordinate = event.coordinate;
  var coordProj = ol.proj.transform(coordinate, 'EPSG:3857', 'EPSG:4326');
  content.innerHTML = '<p>Click click on ' + ol.coordinate.toStringXY(coordProj, 3) + '</p>';
  overlay.setPosition(coordinate);
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
    maxResolution: maxR,
    zIndex: index
  })
  return vectorLayer;
}

}
