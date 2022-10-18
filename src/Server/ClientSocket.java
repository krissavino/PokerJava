package Server;

import Server.Interfaces.IThreadController;

import java.io.*;
import java.net.Socket;

public class ClientSocket
{
    private final Socket clientSocket;
    private final BufferedReader bufferedReader;
    private final PrintWriter bufferedWriter;

    ClientSocket(Socket socket)
    {
        clientSocket = socket;

        try
        {
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            bufferedWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket()
    {
        return clientSocket;
    }

    public void sendMessage(String message)
    {
        bufferedWriter.println(message);
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }
}
