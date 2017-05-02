package ru.epavlov.raspip.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;
import ru.epavlov.raspip.serial.SerialReader;
import rx.subjects.BehaviorSubject;

import java.io.IOException;

/**
 * Created by Eugene on 21.01.2017.
 */
public class Presenter {
    private static final Logger log = Logger.getLogger(Presenter.class);
    private BehaviorSubject<Background> background = BehaviorSubject.create();
    private BehaviorSubject<String> status = BehaviorSubject.create("задание координат");//new ImageView(Images.getImage("status_koord_new.png")));
    private BehaviorSubject<ImageView> centerImage = BehaviorSubject.create(new ImageView(Images.getImage("x.png")));
    private BehaviorSubject<String> coords = BehaviorSubject.create("");
    private Resources resources = new Resources();

    public BehaviorSubject<String> getCoords() {
        return coords;
    }

    private SerialPort usb;
    private SerialPort arduino;
    private BehaviorSubject<String> error = BehaviorSubject.create("");
    private boolean debug;
    private int cnt = 0;
    public Presenter() {
        Image backg = Images.getImage("back.png");
        background.onNext(new Background(new BackgroundImage(backg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        SerialReader serialReader = new SerialReader();
        //Resources resources = new Resources();
        debug = resources.getDebug();
        if (debug){
            error.onNext("Режим отладки");
        }
//        new Thread(()->{
//            while (true){
//            Scanner scanner = new Scanner(System.in);
//            parseCommand(scanner.next(),"");
//            }
//        }).start();



        usb = serialReader.getUsbPort();
        arduino = serialReader.getArduinoPort();
        if (usb!=null){
            try{
                usb.addEventListener(l->{
                    try {
                        String command = usb.readString();
                        //command = scanner.next();   //usb.readString(); System
                        //решили первый
                        parseCommand(command , l.getPortName());


                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                });
            }catch (Exception ignored){

            }
        }
        if (arduino!=null)
        try {
            arduino.addEventListener(l->{
                try {
                    int command = Integer.parseInt(arduino.readString(2,1000));
                    switch (command){
                        case 90: reset(); break;
                        case 91: startTeleport(); break;
                        case 92: stopTeleport(); break;
                        case 98:
                            Runtime.getRuntime().exec("sudo shutdown");
                            System.exit(1);
                            log.warn("shutdown");
                            break;
                        case 99:
                            Runtime.getRuntime().exec("sudo reboot");
                            System.exit(1);
                            log.warn("reboot");
                            break;
                    }
                    if (debug) error.onNext("ARDUINO::"+command);
                    log.warn("ARDUINO::"+command);
                } catch (SerialPortException | SerialPortTimeoutException | IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

          status.onNext("задание координат");


    }
    private void sendArduino(int x){
        if (arduino!=null){
            try {
                arduino.writeInt(x);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
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
    public void accept(){
        try {
            centerImage.onNext(new ImageView(Images.getImage("right.png")));
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void decline(){
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
    private void reset(){
        cnt=0;
        centerImage.onNext(new ImageView(Images.getImage("x.png")));
        status.onNext("задание координат");
        coords.onNext("");
        //status.onNext(new ImageView(Images.getImage("status_koord_new.png")));
    }
    private void startTeleport(){
        status.onNext("телепортация");
        centerImage.onNext(new ImageView());
    }
    private void stopTeleport(){
        centerImage.onNext(new ImageView(Images.getImage("end.png")));
    }

    public BehaviorSubject<String> getError() {
        return error;
    }

    private void parseCommand(String command, String portName){
        String add= "\r";
        if (portName.equals("")) add="";
        if (command.equals(resources.getX()+add) && cnt == 0) {
            sendArduino(10);
            accept();
            centerImage.onNext(new ImageView(Images.getImage("y.png")));
            coords.onNext("координата х: "+resources.getX());
            cnt++;
        } else
        if (command.equals(resources.getY()+add) && cnt == 1) {
            sendArduino(20);
            accept();
            centerImage.onNext(new ImageView(Images.getImage("z.png")));
            coords.onNext(coords.getValue()+"\nкоордината y: "+resources.getY());
            cnt++;
        } else
        if (command.equals(resources.getZ()+add) && cnt == 2) {
            sendArduino(30);
            accept();
            coords.onNext(coords.getValue()+"\nкоордината z: "+resources.getZ());
            centerImage.onNext(new ImageView(Images.getImage("take_sit.png")));
            //status.onNext(new ImageView(Images.getImage("status_teleport_new.png")));
            cnt++;
        } else {
            decline();
        }

        if (debug) error.onNext(portName+"::"+command);
        log.warn(portName+"::"+command);
    }
}
