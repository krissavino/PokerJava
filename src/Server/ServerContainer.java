package Server;

import Server.Interfaces.IServerContainer;

public class ServerContainer implements IServerContainer
{
    private static Server server;

    public static Server getServer()
    {
        if(server == null)
            server = new Server(2,2121);

        return server;
    }
}
