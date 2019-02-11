package utd.distributed;

public class ModuleNode implements Runnable
{
    private boolean running;

    public ModuleNode()
    {
        running = false;
    }

    public void kill()
    {
        running = false;
    }

    public void run()
    {
        running = true;
        while(running)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.printf("Tick\n");
        }
    }
}
