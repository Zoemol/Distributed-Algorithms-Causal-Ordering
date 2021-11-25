// Step 3 : Developing the Server program
// Note: it should implement or extend the implementation class.
// (1) create a client class form where you want invoke the remote object
// (2) creat a remote object by instantiating the implementation class
// (3) export the remote object
// (4) get the RMI registry 
// (5) bind the remote object created to the registry


import java.rmi.registry.Registry; 
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Process implements Actions, Runnable{
    public int id;
    public Time_stamp clock;
    public int n;  // total number of processes

    //buffer for unrdelivered messages
    public ArrayList<String> undelv_msg = new ArrayList<>();    
    public ArrayList<Integer> undelv_sender = new ArrayList<>();
    public ArrayList<Time_stamp> undelv_time = new ArrayList<>();

    // global buffer
    public ArrayList<Integer> idBuffer = new ArrayList<>();
    public ArrayList<Time_stamp> timeBuffer = new ArrayList<>();

    public Process(int pid){
        id = pid;
    }

    public static Process[] create_n_process(int n){ 
        Process[] result = new Process[n];
        Actions[] stub = new Actions[n];
        Time_stamp initial_clock = new Time_stamp(n);

        for(int i = 0; i < n; i++){
            try{
                // instantiating the implementateion class
                result[i] = new Process(i);
                result[i].n = n;
                result[i].clock = initial_clock;
                //export obj
                stub[i] = (Actions) UnicastRemoteObject.exportObject(result[i], 0);
                //binding the remote object(stub) in the registry
                Registry registry = LocateRegistry.getRegistry();
                //binding the remote object(stub) in the registry
                registry.bind("process" + i, stub[i]); // clients can look up for "Hello" tp use the service
                System.err.println("processe" + i +" created");
            } catch (Exception e){
                System.err.println("Server exception:" + e.toString());
                e.printStackTrace();
            }
        }
        return result;
    }


    // // create n processes and initialize their time vector
    // public static Process[] create_n_process(int n){
    //     Process[] result = new Process[3];
    //     Time_stamp initial_clock = new Time_stamp(n);
    //     ArrayList<Integer> initial_buffer_pid = new ArrayList<>();
    //     ArrayList<Time_stamp> initial_buffer_time = new ArrayList<>();

    //     for(int i = 0;i < n; i++){
    //         result[i] = new Process(i);
    //         result[i].clock = initial_clock;
    //     }
    //     return result;
    // }



    public void inc_clock(){
        this.clock.inc(this.id);
    } 

    public Time_stamp get_local_clock(){
        return this.clock;
    }

    // implements actions
    public void printMsg() throws RemoteException{
        System.out.println("this is a message :)");
    }

    // delay is used to assumping the the sending is slow
    public void send(String msg, int receiver_id, int delay) throws RemoteException, NotBoundException{
        // increase time and buffer the msg
        this.inc_clock(); 
        this.idBuffer.add(receiver_id);
        this.timeBuffer.add(this.clock);

        //Getting the registry
        Registry registry = LocateRegistry.getRegistry(); // get the registry of 1099
        //Looking up for the remote object
        Actions receiver = (Actions) registry.lookup("process" + receiver_id);
        //Calling the remote method using the obtained object
        System.out.println(this.id + " sends message" + msg + " to " + receiver_id);

        if (delay > 0) {
            Thread thread = new Thread(() -> run(receiver, msg, this.id, this.clock, this.idBuffer,this.timeBuffer, delay));
            thread.start();
        } else {    
            receiver.receive(msg, this.id, this.clock, this.idBuffer, this.timeBuffer);  // Just like stub is a local object
        }   
    }

    public void receive(String msg, int sender_id, Time_stamp sender_clock,  ArrayList<Integer> idBuffer, ArrayList<Time_stamp> timeBuffer){
        // check if itself is in the buffer
        Boolean delv = false; //flag = true -> can be delivered
        for(int i = 0; i < idBuffer.size(); i++){
            if(this.id == idBuffer.get(i)){
                for(int j = 0; j < n; j++){
                    // local time is smaller than that in the buffer -> not able to deliver
                    if(this.clock.time_vec.get(j) <= timeBuffer.get(i).time_vec.get(j)){
                        break;
                    }
                    // local time is greater than that in the buffer -> deliver
                    delv = true;
                }
            }

        }
        if(delv){
            System.out.println("message " + msg + " can be delivered!");
            deliver(msg, sender_id, sender_clock, idBuffer, timeBuffer);
        }
        else{  // add it to undelivered buffer
            this.undelv_msg.add(msg);
            this.undelv_sender.add(sender_id);
            this.undelv_time.add(sender_clock);
        }
    }

    public void deliver(String msg, int sender_id, Time_stamp sender_clock,  ArrayList<Integer> idBuffer, ArrayList<Time_stamp> timeBuffer){
        System.out.println(this.id + " has delivered message from " + sender_id + " at local time " + this.clock.toString());
        this.inc_clock();
        // merge the received buffer with the local buffer and delete itself
        int del = this.undelv_time.indexOf(sender_clock);
        this.undelv_msg.remove(del);
        this.undelv_sender.remove(del);
        this.undelv_time.remove(del);

        this.idBuffer.addAll(idBuffer);
        this.timeBuffer.addAll(timeBuffer);
        // update the local clock
        for(int i = 0; i < this.clock.vec_len; i++){
            if(i != this.id){
                this.clock.time_vec.set(i, Integer.max(this.clock.time_vec.get(i), sender_clock.time_vec.get(i)));
            }
        }

        // check for other undelevered messages -> receive again 
        for(int i = 0; i < undelv_msg.size(); i++){
            receive(undelv_msg.get(i), undelv_sender.get(i), undelv_time.get(i), this.idBuffer, this.timeBuffer);
        }

    }

    @Override
    public void run() {
        System.out.println("delayed receiver started!");
    }

    public void run(Actions receiver,String msg, int sender_id, Time_stamp sender_clock,  ArrayList<Integer> idBuffer, ArrayList<Time_stamp> timeBuffer, int delay){
        try {
            Thread.sleep(delay);
            System.out.println("I have slept that long!");            
            receiver.receive(msg, sender_id, sender_clock, idBuffer, timeBuffer);
        } catch (InterruptedException | RemoteException | NotBoundException e){
            e.printStackTrace();
        }        
    }
}