/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queuesys;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Paweł
 */
public class Result {
    private List<Record> results;
    private long startMillis;
    
    public Result(){
        startMillis = System.currentTimeMillis();
        results = new LinkedList<>();
    }

    private static class Record {
        public int value;
        public long millisec;
        public Record(int value, long millisec) {
            this.value = value;
            this.millisec = millisec;
        }
    }
    
    public void add(int value){
        results.add(new Record(value, System.currentTimeMillis() - this.startMillis));
    }
    
    public long size(){
        return results.size();
    }
    
    public int getValue(int i){
        return results.get(i).value;
    }
    
    public long getTimeOffset(int i){
        return results.get(i).millisec;
    }
}
