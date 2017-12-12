function showAnnuaireLieuxJustice(){
	window.alert("lol");
    var annuaire = new ol.layer.Tile({
            source: new ol.source.TileWMS(({
              url: 'http://192.168.1.38:8080/geoserver/structuresjudiciaires/wms',
              params: {'LAYERS': 'structuresjudiciaires:annuaire_lieux_justice'},
              serverType: 'geoserver'
            }))
          });
    map.addLayer(annuaire);
}
