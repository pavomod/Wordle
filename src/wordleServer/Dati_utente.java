package wordleServer;



public class Dati_utente 
{
	//chiavi del file json
	String username; 
	String password;
	boolean haGiocato; //se un utente ha già giocato con la parola corrente
	int tentativi; //numero di tentativi con la parola corrente
	boolean login; //se l'utente è già loggato
	int paroleIndovinate; //numero di parole indovinate fino adesso
	String lastWord=""; //ultima parola con cui l'utente ha giocato
	//Boolean share; // 
	int[] storico={0,0,0,0,0,0,0,0,0,0,0,0}; //ogni pos identifica il numero di tentativi, il valore il numero di partite indoviante
	
	public Dati_utente(String us,String pwd) 
	{
		this.username=us;
		this.password=pwd;
		this.haGiocato=false;
		this.tentativi=0;
		this.login=false;
		this.paroleIndovinate=0;
		//this.share=false;
	}
	
	
	public String getUsername() 
	{
		return this.username;
	}
	
	public void setUsername(String username) 
	{
		this.username=username;
	}
	
	public String getPwd() 
	{
		return this.password;
	}
	
	public void setPwd(String pwd) 
	{
		this.password=pwd;
	}
	
	public boolean getHaGiocato() 
	{
		return this.haGiocato;
	}
	
	public void setHaGiocato(boolean stato) 
	{
		this.haGiocato=stato;
	}
	
	public int getTentativi() 
	{
		return this.tentativi;
	}
	
	public void setTentativi(int tentativi) 
	{
		this.tentativi=tentativi;
	}
	
	public boolean getLogin() 
	{
		return this.login;
	}
	
	public void setLogin(boolean stato) 
	{
		this.login=stato;
	}
	
	public int getParoleIndovinate() 
	{
		return this.paroleIndovinate;
	}
	
	public void setParoleIndovinate(int paroleIndovinate) 
	{
		this.paroleIndovinate=paroleIndovinate;
	}
	
	public String getLastWord() 
	{
		return this.lastWord;
	}
	
	public void setLastWord(String lw) 
	{
		this.lastWord=lw;
	}
	
	/*public boolean getShare() 
	{
		return this.share;
	}
	
	public void setShare(boolean stato) 
	{
		this.share=stato;
	}*/
	
	public void setStorico(int pos) 
	{
		this.storico[pos-1]++;
	}
	
	public String getStorico() //formattazione dello storico
	{
		String s="[ ";
		for (int i=1;i<=12;i++) 
		{
			s+=" ( "+i+") = "+this.storico[i-1]+" -- ";
		}
		
		s+="]";
		return s;
	}
	
}
