package ru.epavlov.raspip.serial;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eugene on 02.05.2017.
 */
public class Reader {
    private final static String TAG ="["+ Reader.class.getSimpleName()+"]: ";
    private static final Logger log = Logger.getLogger(Reader.class);
    private ReaderListener readerListener;
    private static Reader instance = new Reader();
    public interface ReaderListener{
      void onSerial(String  text);
    }
    private HashMap<String, SerialPort> ports = new HashMap<>();

    private Reader() {
        String[] portList = SerialPortList.getPortNames();
        System.out.println("---Available ports:---");
        for (String str : portList) {
            ports.put(str, new SerialPort(str));
            System.out.println(str);
        }
        System.out.println("----------------------");
        openPorts();
    }
    public static Reader getInstance(){
        return instance;
    }

    public void addListener(ReaderListener readerListner){
        this.readerListener = readerListner;
    }
    public static void main(String[] args) {
//        Reader reader = new Reader();
//        reader.addListener(System.out::println);
    }

    private void openPorts() {
        ArrayList<String> list =new ArrayList<>();
        ports.forEach((s, serial) -> {
            try {
                serial.openPort();
                serial.setParams(9600, 8, 1, 0);
                serial.addEventListener(listener);
            } catch (SerialPortException e) {
                list.add(s);
                //ports.remove(s);
                log.error(TAG+ e.toString());
                e.printStackTrace();
            }
        });
        list.forEach(s->ports.remove(s));
        System.out.println("---Opened ports:---");
        ports.forEach((s, serial)-> System.out.println(s));
        System.out.println("----------------------");
        if (ports.size()==0) log.error(TAG+"no available ports");
    }

    private SerialPortEventListener listener = serialPortEvent -> {
        try {
            if (serialPortEvent.getEventValue()>0){
                String s =ports.get(serialPortEvent.getPortName()).readString().trim();
              //  System.out.println(serialPortEvent.getEventValue()+":"+ Arrays.toString(ports.get(serialPortEvent.getPortName()).readString().trim().getBytes()));
             if (readerListener!=null) readerListener.onSerial(s);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
            log.error(TAG+e.toString());
        }
    };
    public void send(int x){
        ports.forEach(((s, serialPort) -> {
            try {
                serialPort.writeString(String.valueOf(x));
            } catch (SerialPortException e) {
                log.error(TAG+"send()::"+e);
                e.printStackTrace();
            }
        }));
    }
}
