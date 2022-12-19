package wordleServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Multicast
{
	
	 public synchronized void sendUDPMessage(String message,String ipAddress, int port) throws IOException  //messaggio da inviare, ip del multicast, porta del multicast
	 {
		 DatagramSocket socket = new DatagramSocket();
		 InetAddress group = InetAddress.getByName(ipAddress);
		 byte[] msg = message.getBytes(); 
		 DatagramPacket packet = new DatagramPacket(msg, msg.length,group, port);//connessione instaura e creazione del datagramma
		 socket.send(packet); //invio in multiscast
		 socket.close();
	 }
}
