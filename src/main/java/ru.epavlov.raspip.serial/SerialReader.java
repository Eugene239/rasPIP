package ru.epavlov.raspip.serial;


import jssc.SerialPort;
import jssc.SerialPortList;
import org.apache.log4j.Logger;

//import gnu.io.SerialPort;

/**
 * Created by Eugene on 27.12.2016.
 */
public class SerialReader {
    private static final Logger log = Logger.getLogger(SerialReader.class);

    private SerialPort usbPort;
    private SerialPort arduinoPort;

    private void init() {
        String[] s = SerialPortList.getPortNames();
        System.out.println("---Available ports:---");
        for (String str : s) {
            if (str.contains("COM") || str.contains("USB") && (usbPort==null)) {
                usbPort = new SerialPort(str);
            }
            if (str.equals("/dev/ttyS0")) {
                arduinoPort = new SerialPort(str);
            }
            System.out.println(str);
        }
        System.out.println("----------------------");
    }

    public static void main(String[] args) {
        SerialReader serialReader = new SerialReader();
       // serialReader.init();
    }
    public SerialReader()  {
        init();
        if (usbPort != null) {
            try {
                usbPort.openPort(); //открываем
                usbPort.setParams(9600, 8, 1, 0);// задаем параметры
            }catch (Exception e){
                log.error(e);
                usbPort =null;
            }
        }
        if (arduinoPort != null) {
            try {
                arduinoPort.openPort();
                arduinoPort.setParams(9600, 8, 1, 0);
            }catch (Exception e){
                log.error(e);
                arduinoPort=null;
            }
        }
    }

    public SerialPort getUsbPort() {
        return usbPort;
    }

    public SerialPort getArduinoPort() {
        return arduinoPort;
    }

//    public static void main(String[] args) throws SerialPortException {
//        String[] s = SerialPortList.getPortNames();
//        String portName = "";
//        System.out.println("---Available ports:---");
//        SerialPort[] ports = new SerialPort[s.length];
//        for (int i = 0; i < s.length; i++) {
//            ports[i] = new SerialPort(s[i]);
//            ports[i].openPort();
//            ports[i].setParams(9600, 8, 1, 0);// задаем параметры
//            final int finalI = i;
//            ports[i].addEventListener(l -> {
//                try {
//                    int[] message = ports[finalI].readIntArray(2);
//                    if ((l.getPortName().equals("/dev/ttyS0"))) {
//                        ports[finalI].writeString("kuku\n");
//                    }
//                    System.out.println(l.getPortName() + "    " + ports[finalI].readString());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
//            });
//            System.out.println(s[i]);
//        }
//        System.out.println("----------------------");
//
//    }

}
