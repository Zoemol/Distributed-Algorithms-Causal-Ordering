import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class Causal_ordering {
    public static void main(String[] args) throws NotBoundException, RemoteException{
        Process[] processes = Process.create_n_process(3);
        processes[0].send("02", 2, 0);
        processes[0].send("01", 1, 0);
        processes[1].send("12", 2, 0);

    }
}
