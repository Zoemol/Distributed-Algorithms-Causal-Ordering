import java.io.Serializable;
import java.util.ArrayList;

public class Time_stamp implements Serializable{
    public int vec_len;
    public ArrayList<Integer> time_vec = new ArrayList<>();
    
    public Time_stamp(int len){
        this.vec_len = len;
        for(int i = 0; i < len; i++){
            this.time_vec.add(0);
        }
    }
    public void inc(int index){
        this.time_vec.set(index, this.time_vec.get(index) + 1);
    }
}

