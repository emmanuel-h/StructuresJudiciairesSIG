package com.sig.galherret.structuresjudiciairessig.builder;

/**
 * Created by benoit on 12/12/17.
 */

public class ScriptBuilder {

    private float posX;
    private float posY;

    public ScriptBuilder(float posX, float posY){
        this.posX = posX;
        this.posY = posY;
    }

    public String buildScript(){
        return "";
    }

    public String buildBaseMapLayer(){
        StringBuilder builder = new StringBuilder();
        builder.append("<script>\n");
        builder.append("var map = new ol.Map({\n" +
                "        layers: [\n" +
                "          new ol.layer.Tile({\n" +
                "           source: new ol.source.OSM()\n" +
                "          })\n" +
                "        ],\n" +
                "        target: 'map',\n" +
                "        view: new ol.View({\n" +
                "          center: ol.proj.transform(\n" +
                "            ["+posX+","+posY+"], 'EPSG:4326','EPSG:3857'),\n" +
                "          zoom: 11\n" +
                "        })\n" +
                "      });\n" +
                "      </script>\n");
        return builder.toString();
    }
}
