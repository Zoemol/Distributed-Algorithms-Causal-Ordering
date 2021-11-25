import java.util.ArrayList;
import java.util.LinkedList;

public class Message {
    public String msg;

    public ArrayList<Integer> buffer_id = new ArrayList<Integer>();
    public ArrayList<Integer> buffer_time = new ArrayList<Integer>();
    public LinkedList<Integer> local_time = new LinkedList<Integer>();

    public Message(){}
}
