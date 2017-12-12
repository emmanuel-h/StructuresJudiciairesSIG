package com.sig.galherret.structuresjudiciairessig.builder;

import android.content.Context;

import com.sig.galherret.structuresjudiciairessig.R;

/**
 * Created by benoit on 12/12/17.
 */

public class HtmlBuilder {

    private Context ctx;

    private float posX;
    private float posY;

    private String data;

    public HtmlBuilder(Context ctx){
        this.ctx = ctx;
    }

    public HtmlBuilder(Context ctx, float posX, float posY){
        this.ctx = ctx;
        this.posX = posX;
        this.posY = posY;
    }

    public String buildHtml(){
        String head = buildHead();
        String body = buildBody();
        String end = buildClosingHtml();

        return head + body + end;
    }

    public String buildHead(){
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html>\n");
        builder.append("<head>\n");
        builder.append("<meta charset=\"utf-8\">\n");
        builder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\">\n");
        builder.append("<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no, width=device-width\">\n");
        builder.append("<title>"+ ctx.getString(R.string.project_name) +"</title>\n");
        builder.append(buildCss());
        builder.append(buildHeadScript());
        builder.append("</head>\n");
        return builder.toString();
    }

    public String buildBody(){
        StringBuilder builder = new StringBuilder();
        builder.append("<div id='map' class='map'>\n");
        builder.append("</div>\n");
        ScriptBuilder scriptBuilder = new ScriptBuilder(posX, posY);
        builder.append(scriptBuilder.buildBaseMapLayer());
        return builder.toString();
    }

    public String buildClosingHtml(){
        StringBuilder builder = new StringBuilder();
        builder.append("</body>\n");
        builder.append("</html>\n");
        return builder.toString();
    }

    public String buildCss(){
        StringBuilder builder = new StringBuilder();
        builder.append("<link rel='stylesheet' type='text/css' " +
                "href='http://" + ctx.getString(R.string.serverAddress) + ":" +
                ctx.getString(R.string.serverPort) + ctx.getString(R.string.style) + "'>\n");
        return builder.toString();
    }

    public String buildHeadScript(){
        StringBuilder builder = new StringBuilder();
        builder.append("<script src='http://" + ctx.getString(R.string.serverAddress) + ":" + ctx.getString(R.string.serverPort)
                + ctx.getString(R.string.olLocalisation) + "' type='text/javascript'>\n");
        builder.append("</script>\n");
        builder.append("<script src='http://" + ctx.getString(R.string.serverAddress) + ":" +
                ctx.getString(R.string.serverPort) + "/structuresJudiciaires/scripts/function.js' type='text/javascript'>\n");
        builder.append("</script>\n");
        return builder.toString();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
