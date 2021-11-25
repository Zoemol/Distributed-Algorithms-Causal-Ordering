// Step 4 : Developing the Client program
// fetch the remote object and invoke the required method using this object
// (1) creat a client class from where your intended to invoke the remote object
// (2) get the RMI registry
// (3) fetch the object from the registry (using the bind name "Hello")
// (4) the lookup() returns an object of type REMOTE, down cast it to the type Hello
// (5) invoke the required method using the obtained remote object

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Test {
    private Test(){}
    
    public static void main(String[] args){
        try{
            //Getting the registry
            Registry registry = LocateRegistry.getRegistry(); // get the registry of 1099
            //Looking up for the remote object
            Actions stub2 = (Actions) registry.lookup("process" + 1);

            //Calling the remote method using the obtained object
            stub2.printMsg();  // Just like stub is a local object
            System.out.println("Remote method invoked by proc1");

        } catch (Exception e){
            System.err.println("Client exception" + e.toString());
            e.printStackTrace();
        }
    }
}