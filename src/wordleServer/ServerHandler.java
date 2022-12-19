package wordleServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class ServerHandler extends Thread
{
	
	private Socket client;
	private PrintWriter out = null;
    private BufferedReader in = null;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Dati_utente globalUser;
    private String fileNameJson="wordleServer\\registrazione.json";
	private String secretWord;
	private boolean haGiocato=false;
	private ArrayList<String> lista_parole;
	private ChangeWord secret;
	private Multicast multi;
	private String ipMulti;
	private int portMulti;
	private boolean haVinto=false;
	String partitaInfo="";
    public ServerHandler(Socket client,ChangeWord secret,ArrayList<String> lista_parole,Multicast multi,String ipMulti,int portMulti) 
	{
		this.client=client; //connessione con il clinet
		this.secretWord=secret.getSecret(); //get ultima parola generata
		this.lista_parole=lista_parole; //lista delle parole consentite
		this.secret=secret; //thread che mi permette di ottenere la nuova parola 
		this.multi=multi; //multicast
		this.ipMulti=ipMulti; //inidirizzo multicast
		this.portMulti=portMulti; //porta multicast
	}
	
	public void run() 	
	{
		
		  try 
		  {
			  out = new PrintWriter(client.getOutputStream(), true);           //inviare messaggi   
			  in = new BufferedReader(new InputStreamReader(client.getInputStream())); //ricevere messaggi
			  String username;
			  String pwd;
			  String richiesta;
			  int retValue;
			  boolean loop=true; //fase di login, registrazione ed exit
			  boolean loop2; //fase di gioco, statistiche e logout
			  while(loop) 
			  {
				  globalUser=null;//nessun utente loggato
				  loop2=true;
				  richiesta="";
				  richiesta = in.readLine();
				  switch(richiesta) 
				  {
					  
				  	case "1":
						username = in.readLine();
						pwd = in.readLine();
				  		retValue=registrazione(username,pwd); //fase di registrazione
				  		if(retValue>0) //se la registrazione ha avuto successo e passo al case 2 (login)
				  		{
				  			out.println("ok"); //segnalo i lsuccesso
				  			out.flush();
				  		}
				  		else if(retValue==0) //se vi è già un username nel json
				  		{
				  			out.println("utente gia registrato"); //segnalo il clonflitto
				  			out.flush();
				  			break;
				  		}
				  		else //password vuota
				  		{
				  			out.println("Errore generico"); //segnalo
				  			out.flush();
				  			break;
				  		}
				  		
				  		
				  	case "2":
				  		username = in.readLine();
						pwd = in.readLine();
				  		retValue=login(username,pwd); //fase di login
				  		if(retValue>0) //se la registrazione ha successo
				  		{
				  			out.println("ok"); //segnalo il successo
				  			out.flush();
				  			
				  		}
				  		else if(retValue==0) //se l'utente risulta già loggato
				  		{
				  			out.println("Utente online"); //segnalo 
				  			out.flush();
				  			break;
				  		}
				  		else //nessun riscontro
				  		{
				  			out.println("Utente non trovato");
				  			out.flush();
				  			break;
				  		}
				  		
				  
				  	while(loop2) //loop del menu di gioco
				  	{
				  		richiesta = in.readLine();
				  		
				  		switch(richiesta) 
				  		{
					  		case "1":

					  			retValue=gioca(); //fase di gioco
	 
					  			break;
					  			
					  			
					  		case "2": //richiesta statistiche personali
					  			inviaStatistiche();
					  			break;
					  		case "3": //invio udp sul multicast delle statistiche di gioco personali
					  			//modifica(5);
					  			if(haVinto) 
					  			{
					  				haVinto=false;
					  				sendMulti();
					  			}
					  			break;

					  		case "5": //logout
					  			if(haGiocato&&(!globalUser.getHaGiocato())) //al prossimo login non potrà giocare con la stessa parola
								{
									modifica(2);
								}
					  			loop2=false;
					  			modifica(0); //login =false
					  			
					  			System.out.println(globalUser.getUsername()+" Logout");
					  			break;
					  			
					  		default:break;
				  		
				  		}
				  		
	
				 	}
				  	
				  break;
				  	case "3": //chiusura
				  		loop=false;
				  		break;
			  		
				  	default:break;
				  }
				  
			  }

			  
		  }
		  
		  
		  catch (IOException e) 
		  {
			  e.printStackTrace();
		  }
		  finally 
		  {
			  
			  try 
			  {
				 
				  if (out != null) 
					  out.close();
	               
				  if (in != null) 
				  {
					  in.close();
					  client.close();
				  }
				  
	           }
	           catch (IOException e) 
	           {
	               e.printStackTrace();
	           }
		  }
	}
	synchronized int registrazione(String username,String pwd) throws IOException  //fase di registrazione
	{
		try 
		{
			
			if(pwd.equals("")) //se la password è vuota ritorno errore
			{
				return -1; 
			}

			
			JsonReader reader = new JsonReader(new InputStreamReader (new FileInputStream(fileNameJson)));
			reader.beginArray();
			List<Dati_utente> lista = new ArrayList<Dati_utente>(); //carico in ram tutti gli utenti 
			while (reader.hasNext()) 
		    {
				Type tipo = new TypeToken <Dati_utente>(){}.getType();
				Dati_utente user = gson.fromJson(reader, tipo);
				lista.add(user);
				if(user.getUsername().equals(username))  //se l'utetnte è già registato lo segnalo
				{
					return 0;
				}
		    }
			
			Dati_utente utente = new Dati_utente(username,pwd); //se l'utente non è registrato, creo un nuovo utente e riscrivo il file json
			lista.add(utente);
			String toWriteJson=gson.toJson(lista);
			Writer writer = new FileWriter(fileNameJson);
			writer.write(toWriteJson);
			writer.close();
			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		return 1; //successo
	
	}
	synchronized int login(String username,String pwd) throws IOException //fase di login
	{

		JsonReader reader = new JsonReader(new InputStreamReader (new FileInputStream(fileNameJson)));
		reader.beginArray();
		while (reader.hasNext()) //scorro tutti gli utenti registrati
	    {
			Type tipo = new TypeToken <Dati_utente>(){}.getType();
			Dati_utente user = gson.fromJson(reader, tipo);

			if(user.getUsername().equals(username)&&user.getPwd().equals(pwd)) //se ho corrispondenza
			{
				if(user.getLogin()) //e l'utente è già online non permetto il login
					return 0;
				globalUser=user; //salvo le su info,cambio il suo stato in online e segnalo il successo
				modifica(0);
				return 1;
			}
	    }
		
		return -1; //errore login, nessuna corrispondenza
	}
	synchronized private void modifica(int cambio) throws IOException //modifiche nel file json
	{
		boolean done=false; //segnala se la modifica è stata effettuata con successo
		JsonReader reader = new JsonReader(new InputStreamReader (new FileInputStream(fileNameJson)));
		reader.beginArray();
		List<Dati_utente> lista = new ArrayList<Dati_utente>();
		while (reader.hasNext()) //mi carico tutti gli utenti
	    {
			Type tipo = new TypeToken <Dati_utente>(){}.getType();
			Dati_utente user = gson.fromJson(reader, tipo);

			/*if(cambio==3)
				user.setShare(false);
			*/
			if(user.getUsername().equals(globalUser.getUsername()))  //se trovo l'utente da modificiare
			{				
				if(cambio==0) //setto lo stato di login del client in base al suo stato attuale
				{
					if(!globalUser.getLogin())
					{
						user.setLogin(true);
					}
					else 
					{
						user.setLogin(false);		
					}
				}
				else if(cambio==1)  //incremento i tentativi effettuati dall'utente
				{
					user.setTentativi(user.getTentativi()+1);
				}
				else if(cambio==2) //l'utente ha già giocato con la parola corrente
				{
					if(!globalUser.getHaGiocato())
					{
						user.setHaGiocato(true);
					}

				}
				else if(cambio==3) //reset del gioco e delle statistiche con l'uscita della nuova parola
				{
					secretWord=secret.getSecret();
					user.setTentativi(0);
					user.setLastWord(secretWord);
					if(globalUser.getHaGiocato())
					{
						user.setHaGiocato(false);
					}
					
				}
				else if(cambio==4)  //parola indovinata.
				{
					user.setTentativi(user.getTentativi()+1);
					user.setStorico(user.getTentativi());
					user.setParoleIndovinate(user.getParoleIndovinate()+1);
					user.setHaGiocato(true);
					haVinto=true;
					user.setLastWord(secretWord);
					partitaInfo+="++++++++++++";
				}
				
				/*else if(cambio==5) 
				{
					user.setShare(true);
				}*/
				
				globalUser=user;
				done=true;
			}
			lista.add(user);//riscrivo il file json se la modifica ha avuto successo.
			
	    }
		if(done) 
		{
			String toWriteJson=gson.toJson(lista);
			Writer writer = new FileWriter(fileNameJson);
			writer.write(toWriteJson);
			writer.close();
		}

	}
	synchronized private void inviaStatistiche() //invio statistiche personali
	{
		String send="Tentativi: "+globalUser.getTentativi()+" - Parole indovinate: "+globalUser.getParoleIndovinate()+" - Storico partite: "+globalUser.getStorico();
		out.println(send);
		out.flush();
	}
	/*synchronized private void inviaGlobali() throws IOException 
	{
		String invio="Nessun giocatore ha condiviso i risultati";
		JsonReader reader = new JsonReader(new InputStreamReader (new FileInputStream(fileNameJson)));
		reader.beginArray();
		while (reader.hasNext()) 
	    {	
			Type tipo = new TypeToken <Dati_utente>(){}.getType();
			Dati_utente user = gson.fromJson(reader, tipo);
			if(user.getShare()) 
			{
				invio=" Username: "+user.getUsername()+", Tentativi: "+user.getTentativi()+" Parole indovinate: "+user.getParoleIndovinate()+"Storico: "+user.getStorico()+" -";
			}
	    }
		out.println(invio);
		out.flush();
	}*/
	int gioca() throws IOException //fase di gioco
	{
		if(((globalUser.getLastWord().equals(secret.getSecret())==false))) //se l'utente non ha già giocato con la parola corrente 
		{
			partitaInfo="";
			haVinto=false;
			//System.out.println("L'utente gioca con la parola: "+secret.getSecret());
			modifica(3);
		}
		else //l'utente ha già giocato con questa parola
		{
			haVinto=true;
			out.println("stop");
			out.flush();
			return 1;
		}
			
		if(globalUser.getHaGiocato()) //se l'utente ha effettuato il logout e la parola non è cambiata
		{
			haVinto=true;
			out.println("stop");
			out.flush();
		}
		
		out.println("start");
		out.flush();
		haGiocato=true;
		String parola="";
		String hint="";
		while(globalUser.getTentativi()<12)  //finchè non termina i tentativi
		{
			
			parola = in.readLine(); //parola dell'utente

			if(parola.equals("exit"))  //richiesta di uscita anticipata dal gioco
			{
				//globalUser.setHaGiocato(false);
				globalUser.setLastWord(""); //in questo modo può giocare di nuovo se non effettua il logout
				return 1;
			}
			else if(parola.length()!=10||!(lista_parole.contains(parola))) //tenativo non valido se non rispetta i caratteri o non è nella lista_parole
			{	
				out.println("again");
				out.flush();
			}
			
			else if(!parola.equals(secretWord)) //generazione dell'indizio
			{
				modifica(1); //tentativo+1
				hint="";
				for(int i=0;i<secretWord.length();i++) //scorro la parola segreta carattere per carattere
				{
					if(secretWord.charAt(i)==parola.charAt(i)) //se il carattere della parola utente si trova nella pos corretta invio +
					{
						hint+="+";
					}
					else if(secretWord.contains(""+parola.charAt(i))) //se il carattere della parola utente si trova nella parola invio ?
					{
						hint+="?";
					}
					else //se il carattere della parola utente non si trova nella parola invio x
					{
						hint+="X";
					}
						
				}
				partitaInfo+=hint+" , ";
				out.println(hint);
				out.flush();
			}
			else //parola indovinata
			{
				modifica(4);
				out.println("ok");
				out.flush();
				return 1;
			}
		}
		out.println("not ok");
		out.flush();
		haVinto=true;
		return 1; //fine tentativi
	}
	synchronized void sendMulti() throws IOException //invia il messaggio in multicast generando una stringa contenente le statistiche dell'utente
	{
		String msg="Username: "+globalUser.getUsername()+", Tentativi ultima partita: "+partitaInfo;
		multi.sendUDPMessage(msg, ipMulti, portMulti);
		partitaInfo="";
	}
	
}
