package wordleServer;

public class TimeSimulator extends Thread
{
	private int time;
	
	public TimeSimulator(int ms) 
	{
		this.time=ms;
	}
	public void run() 
	{
		try 
		{
			Thread.sleep(time); //attende x MS prima di terminare, la sua terminazione farà generare una nuova parola
		} catch (InterruptedException e) 
		{

			e.printStackTrace();
		}
		
	}

}
