package utd.distributed;

public class Node implements Runnable
{
    private boolean running;

    public Node()
    {
        running = false;
    }

    public void run()
    {
        running = true;
        while(running)
        {
            
        }
    }
}
