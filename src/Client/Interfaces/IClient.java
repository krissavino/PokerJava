package Client.Interfaces;

import java.io.IOException;

public interface IClient
{
    void sendMessage(String msg);

    void disconnect() throws IOException;

    boolean connect(int port);

    boolean isConnected();

    void setIpScanTimeOut(int milliseconds);
}
