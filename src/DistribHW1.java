package utd.distributed;


public class DistribHW1
{
    public static void main(String args[])
    {
        System.out.printf("Start\n");

        ModuleNode node = new ModuleNode();

        Thread t = new Thread(node);
        t.start();

        try
        {
            Thread.sleep(500);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        node.kill();

    }
}
