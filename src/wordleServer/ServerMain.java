package wordleServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain 
{
	
	public static void main(String[] args) 
	{
		ServerSocket server = null;
		try 
		{
			ExecutorService service = Executors.newCachedThreadPool(); //crea una thread per ogni client che andrà a connettersi, non imponiamo limite sul numero di client
			File fileSettings = new File("wordleServer\\settings.txt"); //lettura dei settaggi
			BufferedReader br = new BufferedReader(new FileReader(fileSettings));
			String settaggio;
			settaggio = br.readLine();
			br.close();
			//fine lettura settaggi
			String[]settaggi=settaggio.split("-");
			
			
			server = new ServerSocket(Integer.parseInt(settaggi[0])); //porta
            server.setReuseAddress(Boolean.parseBoolean(settaggi[1])); //true
            int timeNewWord=Integer.parseInt(settaggi[2]); //in ms
            System.out.println("== Server attivo ==\n\n");
            
            ArrayList<String> lista_parole = new ArrayList<String>(); //lista parole estratte da words.txt
            String fileParole="wordleServer\\words.txt";
            BufferedReader br2 = new BufferedReader(new FileReader(fileParole));
            String st;
			while((st=br2.readLine())!=null)
					lista_parole.add(st);
			br2.close();
			//fine estrazione parole da words.txt
			ChangeWord secret = new ChangeWord(lista_parole,timeNewWord);
			secret.start(); //thread che genera una nuova parola ogni timeNewWord estraendola da lista_parole
			Multicast multi = new Multicast();
            while (true) 
            {
            	 
                Socket client = server.accept();
                System.out.println("== Client connesso -> "+ client.getInetAddress().getHostAddress()+" ==\n\n");
                ServerHandler handler = new ServerHandler(client,secret,lista_parole,multi,settaggi[3],Integer.parseInt(settaggi[4])); //connessione, parola segreta, lista parole, mutlicast, ipMulti e portaMulti
                service.execute(handler); //threadpool che avvia un thread per ogni client connesso
            }
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
            if (server != null) 
            {
                try 
                {
                    server.close();
                }
                
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
		}	
	}
	

	

}
