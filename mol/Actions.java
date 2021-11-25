// Step 1
// Defining the Remote Interface
// (1) extend the predefined interface Remote
// (2) declare all the business methods that can be invoked by the client in this interface
// (3) throw RemoteException: the name of a network issue during remote calls


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.ArrayList;

public interface Actions extends Remote{
    void printMsg() throws RemoteException;
    void send(String msg, int receiver_id, int delay) throws RemoteException,  NotBoundException;
    void receive(String msg, int sender_id, Time_stamp sender_clock,  ArrayList<Integer> idBuffer, ArrayList<Time_stamp> timeBuffer) throws RemoteException,  NotBoundException;
    void deliver(String msg, int sender_id, Time_stamp sender_clock, ArrayList<Integer> idBuffer, ArrayList<Time_stamp> timeBuffer) throws RemoteException;
    // void inc_time();
    // Boolean could_deliver(int[] sender_time);
}
