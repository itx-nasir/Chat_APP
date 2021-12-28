/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Uzair
 */
public class TestVoiceChat {

    String mes1, mes;
    /*
	 * This class add many Buttons, Textarea for the Client/ Server User Interface
     */
    Thread thread;
    MulticastSocket socket;
    InetAddress add;
    // for voice
    // define socket
    DatagramSocket server_socket;
    // audio
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    SourceDataLine sourceDataLine;
    AudioInputStream audioInputStream;

    public TestVoiceChat() {

        new StartServer();
        captureAudio();
        thread.interrupt();
        new SendRequest();
    }

    public void stopServer() {
        thread.interrupt();
        socket.close();
    }

    public class StartServer implements Runnable {

        StartServer() {
            thread = new Thread(this);

            thread.start();
        }

        public void run() {
            // Server side that receive the message
            try {
                socket = new MulticastSocket(6789);
                add = InetAddress.getByName("227.0.0.1");
                socket.joinGroup(add);
                while (true) {
                    try {
                        // Receive request from client
                        byte[] buffer = new byte[65535];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, add, 6789);
                        socket.receive(packet);
                        String addressclint = packet.getAddress().toString();
                        // String
                        mes1 = new String(buffer).trim();
                        // String
                    } catch (UnknownHostException ue) {
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    public class SendRequest { // inclass

        SendRequest() {
            // client side that send the packets
            try {
                // add = InetAddress.getByName(area3.getText()); //224.0.0.0
                add = InetAddress.getByName("227.0.0.1");
                MulticastSocket socket = new MulticastSocket();
                socket.joinGroup(add);
                byte[] buffer = new byte[65535];
                String mess = "";
                buffer = mess.getBytes();
                DatagramPacket packet = new // DatagramPacket(buffer, buffer.length, add, Integer.valueOf(area4.getText()));
                        // //6789
                        DatagramPacket(buffer, buffer.length, add, 6789);
                socket.send(packet);

                socket.close();
            } catch (IOException io) {
            }
        }
    }

/////////////////////////////////////////////////////
///////////////
// voice Classes
// first function called by main
    public void captureAudio() {
        try {
            // print audio devices informatioin
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            System.out.println("Available mixers:");
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                System.out.println(mixerInfo[cnt].getName());
            }

            // get audio from mic X
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);
            targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            // call thread to send audio
            Thread captureThread = new CaptureThread();
            captureThread.start();
            // send audio to speaker X
            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            // call thread to recive audio
            Thread playThread = new PlayThread();
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

// sending audio to server
    class CaptureThread extends Thread {

        byte tempBuffer[] = new byte[1024];

        public void run() {
            try {
                DatagramSocket client_socket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
                while (true) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    DatagramPacket send_packet = new DatagramPacket(tempBuffer, tempBuffer.length, IPAddress,
                            Integer.valueOf(5000));
                    client_socket.send(send_packet);
                }
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

// coding and format audio
    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

// recieving audio from server and play it
    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[1024];

        public void run() {
            try {
                DatagramSocket server_socket = new DatagramSocket(Integer.valueOf(5000));
                while (true) {
                    DatagramPacket receive_packet = new DatagramPacket(tempBuffer, tempBuffer.length);
                    server_socket.receive(receive_packet);
                    sourceDataLine.write(receive_packet.getData(), 0, tempBuffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
