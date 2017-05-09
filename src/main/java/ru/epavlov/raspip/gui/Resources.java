package ru.epavlov.raspip.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.epavlov.raspip.serial.GUI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Eugene on 22.01.2017.
 */
public class Resources {

    private static final String path = new File(GUI.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().toString()+"/settings.json";
    private String x;
    private String y;
    private String z;
    private Boolean debug;
    public Resources(){
        try {
            JsonParser parser = new JsonParser();
            String file = new String(Files.readAllBytes(new File(path).toPath()));
            JsonObject json =  parser.parse(file).getAsJsonObject();
            x= json.get("x").getAsString().trim();
            y= json.get("y").getAsString().trim();
            z= json.get("z").getAsString().trim();
            debug = json.get("debug").getAsBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getPath() {
        return path;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public Boolean getDebug() {
        return debug;
    }
}
