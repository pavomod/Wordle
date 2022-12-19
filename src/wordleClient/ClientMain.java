package wordleClient;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientMain 
{
	//out per inviare stringhe al server, in per ricevere stringhe dal server, bf per lettura file, comando è l'input da tastiera del client, 
	//in risposta salvo la stringa ricevuta dal server, globali è una lista in cui salvo tutti i messaggi UDP ricevuti dal thread STATISTICHE
	
	
	static PrintWriter out = null;
	static BufferedReader in = null;
	static BufferedReader bf = null;
	static String comando="";
	static String risposta="";
	static ArrayList<String> globali = new ArrayList<String>();
	
	public static void main(String[]args) throws IOException 
	{
		File fileSettings = new File("wordleClient\\settings.txt"); //leggo i settaggi del server, pos. 0 -> ip, pos 1 -> porta, pos 2-> ip multicast, pos 3 porta multi
		BufferedReader br = new BufferedReader(new FileReader(fileSettings));
		String settaggio;
		settaggio = br.readLine(); //unica riga, splitto i parametri separati con "-" successivamente
		br.close();
		String[]settaggi=settaggio.split("-");
		//fine lettura settaggi
		try (Socket socket = new Socket(settaggi[0], Integer.parseInt(settaggi[1])))  //creo la socket e instauro la connessione TCP con il server
		{
			
			
			out = new PrintWriter(socket.getOutputStream(), true);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			bf = new BufferedReader (new InputStreamReader(System.in));
			Statistiche stat = new Statistiche(settaggi[2],Integer.parseInt(settaggi[3]),globali); //thread che attende messaggi udp
			
			stat.start();
			boolean loop=true; //primo loop riguardante il menu (registrazione, login,esci)
			boolean loop2; //secondo loop riguardante il menu di gioco (gioca,statistiche,logout)
			while(loop) //fase di login e sign in
			{
				loop2=true;
				System.out.printf("1 -> Registrazione\n2 -> Login\n3 -> Esci\n\n->");
				comando = bf.readLine();
				out.println(comando);
				out.flush();
	
				switch(comando) 
				{
					case "1":
						if(registrazione()<1) //se la registrazione non ha avuto successo l'utente deve rieffettuare la scelta, altrimenti entro nel case 2
							break;
	
					
					case "2":
						if(login()<1) //se il login non ha avuto successo l'utente deve ritorna al menu, altrimenti entra nella fase di gioco
							break;
						
							while(loop2) //fase di gioco
							{
								System.out.printf("1 -> Gioca\n2 -> Statistiche personali\n3 -> Condividi statistiche\n4 -> Statistiche globali\n5 -> Logout\n\n->");
								comando = bf.readLine();
								out.println(comando);
								out.flush();
								switch(comando) 
								{
									case "1":
										gioca(); //fase in cui il client deve indovinare la parola
										break;
									case "2":
										risposta=in.readLine();
										System.out.println(risposta); //in attesa di ricevere le statistiche personali
									break;
									case "4":
										synchronized(globali)
										{
											for(String s : globali) //stampo tutti i messaggi udp ricevuti fino adesso nel multicast
												System.out.println(s);
										}
										break;
									case "5": //logout
										loop2=false;
										break;
									default: break;
								}
							}
							
						break;
					case "3"://chiusura connessione
						loop=false;
						break;
					default:break;
				}
			}
			stat.fermo(); //termino statistiche
			if(stat.getState()==Thread.State.TERMINATED)
				System.out.print("=bye bye=");
		}
		
		catch (IOException e) 
		{
            e.printStackTrace();
		}
		
		
	}
	
	static int registrazione() throws IOException //fase di registrazione
	{
		System.out.printf("Registrazione\n\nUsername->");
		comando = bf.readLine();
		out.println(comando);
		out.flush();
		System.out.print("Password->");
		comando = bf.readLine();
		out.println(comando);
		out.flush();
		risposta=in.readLine(); //invio username e password
		if(!risposta.equals("ok"))  //in caso di fallimento
		{
			System.out.println(risposta);
			return -1; //ritorno errore
		}	
		System.out.println(risposta);
		return 1; //succcesso
	}
	
	static int login() throws IOException //fase di login
	{
		System.out.printf("\nLogin\n\nUsername->");
		comando = bf.readLine();
		out.println(comando);
		out.flush();
		System.out.print("Password->");
		comando = bf.readLine();
		out.println(comando);
		out.flush();
		risposta=in.readLine();
		if(!risposta.equals("ok")) //dopo l'invio di email e password se il login fallisce
		{
			System.out.println(risposta);
			return -1;//ritorno errore
		}
		return 1;//successo
	}
	
	static int gioca() throws IOException //fase di gioco
	{
		String guess="";
		String risp="";
		risp=in.readLine();
		if(!risp.equals("start")) //verifico se mi è consentito giocare con questa parola
		{
			System.out.println("Hai gia giocato con questa parola.");
			return -1; // ritorno errore se non mi è consentito
		}
		while(true) 
		{
			
			System.out.print("indovina la parola -> ");
			guess=bf.readLine(); 

			out.println(guess);
			out.flush();//invio la mia guessworld
			if(guess.equals("exit")) //comando che mi permette di interrompere il gioco prima di finire i tentativi
				break;
			
			risp=in.readLine();
			
			if(risp.equals("not ok"))  //se ho terminato i tentativi
			{
				System.out.println("tentativi terminati");
				break;
			}
			
			
			else if(risp.equals("again"))  //tentativo non valido, non spreco tentativi
			{
				System.out.println("parola non valida");
			}
			else if(risp.equals("ok")) //parola indovinata
			{
				System.out.println("Parola scoperta! -> "+guess);
				break;
			}
			else //suggerimento, tentativo sprecato
			{
				System.out.println("sueggerimento -> "+risp);
			}
		}
		return 1; //non vi sono errori
	}
}
