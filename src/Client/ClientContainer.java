package Client;


import Client.Interfaces.IClientContainer;

public class ClientContainer implements IClientContainer
{
    private static Client client;

    public static Client getClient()
    {
        if(client == null)
            client = new Client();

        return client;
    }
}
