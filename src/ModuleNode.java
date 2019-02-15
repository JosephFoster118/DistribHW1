

import java.util.ArrayList;
import java.util.List;

public class ModuleNode implements Runnable {
    private static boolean searching = false;
    private static boolean rootWaiting = false;
    private static boolean messagesReady = false;

    public static final Object lock1 = new Object();
    public static final Object lock2 = new Object();

    private boolean marked;
    private int id;
    private List<Integer> connections;
    private List<Integer> parents;
    private List<Integer> children;

    public ModuleNode(int pid, int[] row) {
        id = pid;

        connections = new ArrayList<>();
        for(int i = 0; i < row.length; i++){
            if(row[i] == 1 && i != id){
                connections.add(i);
            }
        }

        parents = new ArrayList<Integer>(0);
        children = new ArrayList<Integer>(0);

        marked = false;
    }

    public boolean isMarked(){ return marked; }

    public String toString(){
        String out = "ID: " + id + "\n";
        out += "Parents: " + parents.toString() + "\n";
        out += "Children: " + children.toString() + "\n";
        out += "\n";

        return out;
    }

    public void run() {
        try {
            if(DistribHW1.searchQueue.peek() == id && !searching) {
                //Root nodes
                synchronized (lock2) {
                    searching = true;

                    //Send messages to neighbors
                    for (int i = 0; i < connections.size(); i++) {
                        DistribHW1.messages.get(connections.get(i)).add(id);
                    }

                    synchronized (lock1){
                        messagesReady = true;
                        lock1.notifyAll();
                    }


                    while (!DistribHW1.roundOver) {
                        rootWaiting = true;

                        synchronized (lock1){
                            lock1.notifyAll();
                        }

                        lock2.wait();

                        //If there is a message add child else no child
                        if (!DistribHW1.messages.get(id).isEmpty()) {
                            int cid = DistribHW1.messages.get(id).get(0);
                            if(cid != -1){
                                children.add(cid);
                            }
                            DistribHW1.messages.get(id).remove(0);
                        }
                        synchronized (lock1){
                            lock1.notifyAll();
                        }
                    }

                    messagesReady = false;
                    marked = true;
                    searching = false;
                }

            } else {
                synchronized (lock1){
                    //Other Nodes
                    while(!messagesReady) {
                        lock1.wait();//Wait for the root to send messages
                    }

                    //Receive messages
                    if (!DistribHW1.messages.get(id).isEmpty()) {
                        int parent = DistribHW1.messages.get(id).get(0);
                        DistribHW1.messages.get(id).remove(0);
                        if(!marked){
                            parents.add(parent);

                            DistribHW1.messages.get(parent).add(id);//Is a child

                            DistribHW1.searchQueue.add(id);

                            marked = true;
                        }else{
                            DistribHW1.messages.get(parent).add(-1);//Not a child
                        }

                    }else{
                        System.out.println(id + " done");
                        return;
                    }

                    while(!rootWaiting) {
                        lock1.wait();//Make sure that root is waiting on you
                    }

                    rootWaiting = false;

                    synchronized (lock2){
                        lock2.notify();
                    }

                    if(DistribHW1.roundOver){
                        synchronized (lock2){
                            lock2.notify();
                        }
                    }

                    //Exit process once you have checked for messages from root
                    System.out.println(id + " done");

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
