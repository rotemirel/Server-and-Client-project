package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.api.bidi.EncoderDecoder;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        Server.threadPerClient(
                7777, //port
                BidiMessagingProtocolImp::new, //protocol factory
                EncoderDecoder::new).serve();
    }
}
