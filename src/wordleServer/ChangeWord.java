package wordleServer;

import java.util.ArrayList;
import java.util.Random;

public class ChangeWord extends Thread
{
	private ArrayList<String> lista_parole; //lista di parole estratte da words.txt
	private String secretWord; //parola segreta attuale
	private int timeMS; //dopo ogni quanto tempo va cambiata la parola
	private Random ran = new Random();
	private ArrayList<Integer> generati = new ArrayList<Integer>();  //lista di tutte le parole generate fino adesso
	public ChangeWord(ArrayList<String> lista_parole,int timeMS) 
	{
		this.lista_parole=lista_parole;
		this.timeMS=timeMS;
	}
	
	public void run() 
	{
		int generato;
		while(true)
		{
			generato=ran.nextInt(lista_parole.size());//estraggo una parola dalla lista di parole
			if(!generati.contains(generato)) //se non è già stata generata 
			{
				generati.add(generato); //la segno come già generata
	        	secretWord=lista_parole.get(generato); //setto la parola segreta
	        	System.out.println("== PAROLA SEGRETA -> "+secretWord+" ==");
	        	
				TimeSimulator time= new TimeSimulator(timeMS); //avvio un thread e attendo la sua terminazione prima di generare un'altra parola
				time.start();
				while(time.getState()!=Thread.State.TERMINATED) 
				{
					try 
					{
						Thread.sleep(this.timeMS); //ogni timeMS ms verifico se il thread ha terminato
					} catch (InterruptedException e) 
					{
						
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public String getSecret() //metodo che permette ai thread di ottenere la parola segreta
	{
		return this.secretWord;
	}
}
