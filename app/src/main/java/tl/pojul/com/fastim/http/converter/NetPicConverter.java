package tl.pojul.com.fastim.http.converter;

import com.pojul.fastIM.message.chat.NetPicMessage;

import java.util.ArrayList;

public abstract class NetPicConverter {

    public abstract ArrayList<NetPicMessage> converter(String responseStr);

}
