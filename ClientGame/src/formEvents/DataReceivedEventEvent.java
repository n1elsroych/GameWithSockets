package formEvents;

import java.util.EventObject;

public class DataReceivedEventEvent extends EventObject{
    private String data;
    
    public DataReceivedEventEvent(Object source, String data) {
        super(source);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
