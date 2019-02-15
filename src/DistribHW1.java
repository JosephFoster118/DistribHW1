

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DistribHW1
{
    public static boolean roundOver = false;//Tells root node that all processes finished

    //Member variables
    public static int rootID;
    public static ArrayList<ArrayList<Integer>> messages;
    public static Queue<Integer> searchQueue = new LinkedList<>();

    public static void main(String args[])
    {
        //Read in input file in same folder as project
        File file = new File("input.dat");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String st;
            int n = Integer.parseInt(br.readLine().substring(3));

            messages = new ArrayList<ArrayList<Integer>>(n);

            for(int i = 0; i < n; i++){
                ArrayList<Integer> list = new ArrayList<Integer>(0);
                messages.add(list);
            }

            br.skip(5);
            String pidString = br.readLine();

            int[] pids = new int[n];

            for(int i = 0; i < n; i++){
                pids[i] = Integer.parseInt(pidString.substring(0,1));
                if(pidString.length() > 1)
                    pidString = pidString.substring(2);
            }

            br.skip(6);
            rootID = Integer.parseInt(br.readLine());
            searchQueue.add(rootID);

            br.readLine();

            ModuleNode[] nodes = new ModuleNode[n];
            Thread[] threads = new Thread[n - 1];
            Thread pThread = null;

            for(int i = 0; i < n; i++) {
                String line = br.readLine();
                int[] row = new int[n];
                for (int j = 0; j < n; j++) {
                    row[j] = Integer.parseInt(line.substring(0, 1));
                    if (line.length() > 1)
                        line = line.substring(2);
                }

                nodes[i] = new ModuleNode(pids[i], row);
            }

            //Run processes k rounds until all nodes marked
            boolean done = false;
            int round = 1;
            while(!done){
                System.out.println("Round " + round++);
                int j = 0;
                for(int i = 0; i < n; i++){
                    if(searchQueue.peek() == i){
                        pThread = new Thread(nodes[i]);
                        pThread.start();
                    }else {
                        threads[j] = new Thread(nodes[i]);
                        threads[j].start();
                        j++;
                    }
                }

                for(int i = 0; i < n - 1; i++){
                    //Joins regular threads
                    threads[i].join();
                }

                roundOver = true;
                synchronized (ModuleNode.lock2){
                    ModuleNode.lock2.notify();
                }
                //Joins root thread
                pThread.join();
                System.out.println("Round over\n");
                roundOver = false;

                DistribHW1.searchQueue.remove();

                if(searchQueue.isEmpty()){
                    done = true;
                }
            }

            //Output children and parents
            for(int i = 0; i < n; i++){
                System.out.println(nodes[i].toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
