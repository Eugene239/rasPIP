package ru.epavlov.raspip.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.log4j.Logger;
import ru.epavlov.raspip.serial.Reader;
import rx.subjects.BehaviorSubject;

/**
 * Created by Eugene on 21.01.2017.
 */
public class Presenter implements Reader.ReaderListener {
    private static final Logger log = Logger.getLogger(Presenter.class);
    private static final String TAG = "[" + Presenter.class.getSimpleName() + "]: ";
    private BehaviorSubject<Background> background = BehaviorSubject.create();
    private BehaviorSubject<String> status = BehaviorSubject.create("задание координат");//new ImageView(Images.getImage("status_koord_new.png")));
    private BehaviorSubject<ImageView> centerImage = BehaviorSubject.create(new ImageView(Images.getImage("x.png")));
    private BehaviorSubject<String> coords = BehaviorSubject.create("");
    private Resources resources = new Resources();

    public BehaviorSubject<String> getCoords() {
        return coords;
    }

    //    private SerialPort usb;
//    private SerialPort arduino;
    private BehaviorSubject<String> error = BehaviorSubject.create("");
    private boolean debug;
    private int cnt = 0;

    public Presenter() {
        Image backg = Images.getImage("back.png");
        background.onNext(new Background(new BackgroundImage(backg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        //SerialReader serialReader = new SerialReader();
        //Resources resources = new Resources();
        debug = resources.getDebug();
        if (debug) {
            error.onNext("Режим отладки");
        }
        Reader.getInstance().addListener(this);
        status.onNext("задание координат");


    }

    private void sendArduino(int x) {
        error.onNext("sending: "+ x);
        Reader.getInstance().send(x);
//        if (arduino!=null){
//            try {
//                arduino.writeInt(x);
//            } catch (SerialPortException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public BehaviorSubject<Background> getBackground() {
        return background;
    }

    public BehaviorSubject<String> getStatus() {
        return status;
    }

    public BehaviorSubject<ImageView> getCenterImage() {
        return centerImage;
    }

    public void accept() {
        try {
            cnt++;
            centerImage.onNext(new ImageView(Images.getImage("right.png")));
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void decline() {
        try {

            ImageView imageView = centerImage.getValue();
            centerImage.onNext(new ImageView(Images.getImage("wrong.png")));
            sendArduino(50);
            Thread.sleep(2000);
            centerImage.onNext(imageView);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reset() {
        cnt = 0;
        centerImage.onNext(new ImageView(Images.getImage("x.png")));
        status.onNext("задание координат");
        coords.onNext("");
        //status.onNext(new ImageView(Images.getImage("status_koord_new.png")));
    }

    private void startTeleport() {
        status.onNext("телепортация");
        centerImage.onNext(new ImageView());
    }

    private void stopTeleport() {
        centerImage.onNext(new ImageView(Images.getImage("end.png")));
    }

    public BehaviorSubject<String> getError() {
        return error;
    }

    private void parseCommand(String command) {
        //String add = "";
        //  if (portName.equals("")) add="";

        switch (cnt) {
            case 0:
                if (command.equals(resources.getX())) {
                    sendArduino(10);
                    accept();
                    centerImage.onNext(new ImageView(Images.getImage("y.png")));
                    coords.onNext("координата х: " + resources.getX());
                    return;
                }
            case 1:
                if (command.equals(resources.getY())) {
                    sendArduino(20);
                    accept();
                    centerImage.onNext(new ImageView(Images.getImage("z.png")));
                    coords.onNext(coords.getValue() + "\nкоордината y: " + resources.getY());
                    return;
                }
            case 2:
                if (command.equals(resources.getZ())) {
                    sendArduino(30);
                    accept();
                    coords.onNext(coords.getValue() + "\nкоордината z: " + resources.getZ());
                    centerImage.onNext(new ImageView(Images.getImage("take_sit.png")));
                    return;
                }
        }
        decline();
//        if (cnt>2) return;
//        if (command.equals(resources.getX().trim()) && cnt == 0) {
//            sendArduino(10);
//            accept();
//            centerImage.onNext(new ImageView(Images.getImage("y.png")));
//            coords.onNext("координата х: "+resources.getX());
//            return;
//        }
//        if (command.equals(resources.getY().trim()) && cnt == 1) {
//            sendArduino(20);
//            accept();
//            centerImage.onNext(new ImageView(Images.getImage("z.png")));
//            coords.onNext(coords.getValue()+"\nкоордината y: "+resources.getY());
//            retqurn;
//        }
//        if (command.equals(resources.getZ().trim()) && cnt == 2) {
//            sendArduino(30);
//            accept();
//            coords.onNext(coords.getValue()+"\nкоордината z: "+resources.getZ());
//            centerImage.onNext(new ImageView(Images.getImage("take_sit.png")));
//            return;
//        }
//        decline();
        log.warn(TAG + "parseCommand()::" + command);
    }

    @Override
    public void onSerial(String text) {
        if (debug) error.onNext(text);
        log.warn(TAG + "onSerial()::" + text);
        try {
            switch (text.trim()) {
                case "90":
                    reset();
                    return;
                case "91":
                    startTeleport();
                    return;
                case "92":
                    stopTeleport();
                    return;
                case "98":
                    log.warn("shutdown");
                    Runtime.getRuntime().exec("sudo shutdown");
                    System.exit(1);
                    return;
                case "99":
                    log.warn("reboot");
                    Runtime.getRuntime().exec("sudo reboot");
                    System.exit(1);
                    return;
                default:
                    if(text.trim().length()>7) parseCommand(text.trim());
            }
        } catch (Exception e) {
            log.error(text + e);
        }

    }
}
