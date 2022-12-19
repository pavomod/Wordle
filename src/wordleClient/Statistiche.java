package wordleClient;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class Statistiche extends Thread
{
	//ip del multicast, porta del multicast, array in cui salvo lo storico dei messaggi udp ricevuti
	private String ip;
	private int port;
	private ArrayList<String> globali;
	public Statistiche(String ip, int port,ArrayList<String>globali) 
	{
		this.ip=ip;
		this.port=port;
		this.globali=globali;
	}
	
	public void run() 
	{
		try 
		{
		   receiveUDPMessage();
		}
		catch(IOException ex)
		{
		    ex.printStackTrace();
		}
	
	}
	
	@SuppressWarnings("deprecation") //il thread resta sempre attivo in attesa di ricevere un messaggio UDP (se riceve un doppio non lo salva), per terminare riceve un interrupt
	public void receiveUDPMessage() throws IOException {
		 byte[] buffer=new byte[1024]; 
		 @SuppressWarnings("resource")
		MulticastSocket socket=new MulticastSocket(port); 
		 InetAddress group=InetAddress.getByName(ip);
		 socket.joinGroup(group); //creo la socket e il gruppo, successivamente mi unisco al gruppo (deprecato bisogna avere java 8 per utilizzarlo)
		 while(true) 
		 {
		    DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
		    socket.receive(packet);//resto in attesa di messaggi UDP, la chiamata è bloccante
		    String msg=new String(packet.getData(),
		    packet.getOffset(),packet.getLength());
		    System.out.println("\nMessaggio UDP -> "+msg);
		    synchronized(globali) //sincronizzo lettura e scrittura della lista
			{
		    	if(!globali.contains(msg)) // se non è già stato salvato lo salvo
		    		globali.add(msg);
			}
		 }
		 
		 //socket.leaveGroup(group);
		// socket.close();
	}
	
	public void fermo() 
	{
		System.exit(1);
	}
	



}
