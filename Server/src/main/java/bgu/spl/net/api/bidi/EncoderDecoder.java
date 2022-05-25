package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.messages.*;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class EncoderDecoder implements MessageEncoderDecoder<Message>{

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == ';') { // changed
            return popString();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message message) {

        ServerToClientMsg msg = (ServerToClientMsg)message;
        short opcode = msg.getOpcode();
        Short messageOpcode = msg.getMessageOpcode();
        String content = msg.getContent();
        List<Short> data = msg.getData();


        if(opcode == 9){ //notification
            byte[] bytes1 = shortToBytes(opcode);
            byte[] bytes2 = (content).getBytes();

            return merge(bytes1, bytes2);
        }

       else { //ack/error

            byte[] bytes1 = listToArray(data);

            if(content == null){
                return bytes1;
            }
            else {
                byte[] strBytes = (content).getBytes();
                return merge(bytes1, strBytes);
            }
        }
    }

    private byte[] merge(byte[] bytes1,byte[] bytes2) {

        int len1 = bytes1.length;
        int len2 = bytes2.length;
        byte[] bytes = new byte[len1+len2];
        System.arraycopy(bytes1,0,bytes,0,len1);
        System.arraycopy(bytes2,0,bytes,len1,len2);

        return bytes;
    }

    private byte[] listToArray (List<Short> data){

        int size = 2*data.size();
        int index = 0;
        byte[] bytes = new byte[size];
        byte[] tempBytes;

        for(Short s: data){
            tempBytes = shortToBytes(s);
            bytes[index++] = tempBytes[0];
            bytes[index++] = tempBytes[1];
        }

        return bytes;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private Message popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 2, len-2, StandardCharsets.UTF_8);
        byte[] opcodeByte = { bytes[0],bytes[1]};
        short Opcode = bytesToShort(opcodeByte);
        len = 0;

        switch (Opcode){

            case (1):
                return new Register(result);

            case (2):
                return new Login(result);

            case (3):
                return new Logout();

            case (4):
                return new FollowUnfollow(result);

            case (5):
                return new Post(result);

            case (6):
                return new PM(result);

            case (7):
                return new LoggedInStatus();

            case (8):
                return new Status(result);

            case (12):
                return new Block(result);

        }
       return null;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
}
