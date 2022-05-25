package bgu.spl.net.messages;
import java.util.List;
import java.util.Queue;

public interface ServerToClientMsg extends Message{

    public String getContent();

    public Short getMessageOpcode();
    public List<Short> getData();
}
