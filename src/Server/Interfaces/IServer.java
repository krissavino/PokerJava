package Server.Interfaces;

import Server.ClientSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

public interface IServer
{

    void start();

    void stop() throws IOException;

    int getMaxClients();

    int getClientsCount();

    LinkedHashMap<Socket, ClientSocket> getClients();
}
