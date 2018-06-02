package tl.pojul.com.fastim.net.Entity;

import tl.pojul.com.fastim.net.SocketRequest;

/**
 * Created by gqb on 2018/5/31.
 */

public class TimeOutTask {

    public long timeout;
    public SocketRequest.IRequest iRequest;

    public TimeOutTask(long timeout, SocketRequest.IRequest iRequest) {
        this.timeout = timeout;
        this.iRequest = iRequest;
    }

}
