package ru.epavlov.raspip.gui;

import javafx.scene.image.Image;
import org.apache.log4j.Logger;
import ru.epavlov.raspip.serial.GUI;

import java.io.File;

/**
 * Created by Eugene on 30.12.2016.
 */
public class Images {
    private static final Logger log = Logger.getLogger(Images.class);

    /*
        back -
        background
        end -
        right
        status_coord
        status_teleport
        take_sit
        wrong
        x,y,z

     */
    public static final String path = new File(GUI.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().toString()+"/images/";
    public static Image getImage(String name) {
        File f = new File(path+name);
        if (f.exists()){
            return new Image("file:///"+path+name);
        }
        else {
            log.error("Images::Cant find image file");
            return null;
        }
    }
}
