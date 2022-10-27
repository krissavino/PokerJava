package Client;

import Client.Interfaces.IClient;
import PokerJava.ClientWindow;
import PokerJava.Player;
import PokerJava.Poker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements IClient
{
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private PrintWriter bufferedWriter;
    private int port = 2121;
    private boolean isConnected = false;
    private int timeOut = 10;

    private ClientWindow clientWindow;
    private Poker pokerGame;
    private Player myPlayer;
    private int myPlace = 0;

    public Client() {
        clientWindow = new ClientWindow(this);
    }

    public static void main(String[] args)
    {
        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        client.connect(2121);
        client2.connect(2121);
        client3.connect(2121);
        /*Client client = ClientContainer.getClient();
        client.connect(2121);
        client.sendMessage(CommandEnum.Empty.toString());*/
        //client.sendMessage(CommandEnum.GiveAwayCards.toString());
        //client.sendMessage(CommandEnum.Stop.toString());
    }

    public void disconnect()
    {
        try
        {
            bufferedReader.close();
            bufferedWriter.close();
            clientSocket.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException();
        }
    }

    /**
     * @param port порт, по которому будет стучаться и общаться клиент с сервером
     */
    public boolean connect(int port)
    {
        this.port = port;
        while(!isConnected)
        {
            try
            {
                findAndConnectToServer();
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                bufferedWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                isConnected = true;
                new Thread(()-> listen()).start();
            }
            catch (Exception exception)
            {
                throw new RuntimeException(exception);
            }
        }
        return true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * @param milliseconds устанавливает время проверки ответа от предполагаемого ip сервера, чем больше, тем дольше будет искать сервер (минимальное значение 10)
     */
    public void setIpScanTimeOut(int milliseconds) {
        if(milliseconds < 10)
            timeOut = 10;
        else
            timeOut = milliseconds;
    }

    private void listen()
    {
        String message = null;

        while (true)
        {
            try
            {
                message = bufferedReader.readLine();
                executeCommandFromBufferedReader(message);
            }
            catch (IOException e)
            {
                if(e.getMessage() == "Connection reset" == true)
                {
                    System.out.println("Сервер принудительно отключил связ");
                    return;
                }
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param message отправка сообщения в виде текста серверу
     */

    public void sendMessage(String message)
    {
        try
        {
            if(clientSocket.isConnected() == false)
                throw new UnknownHostException();
            bufferedWriter.println(message);
        }
        catch (Exception e)
        {
            new RuntimeException(e);
        }
    }

    private void findAndConnectToServer()
    {
        clientSocket = new Socket();
        for (int counter = 0; counter < 256; counter++)
        {
            try
            {
                byte[] localAdress = InetAddress.getLocalHost().getAddress();
                localAdress[3] = (byte) counter;
                clientSocket.connect(new InetSocketAddress(InetAddress.getByAddress(localAdress), port), 10);

                if (clientSocket.isConnected())
                    return;
            }
            catch(IOException e){}

            clientSocket = new Socket();

            if(counter == 255)
                counter = 0;
        }
    }

    public void executeCommandFromBufferedReader(String command) {
        Gson gson = new Gson();
        String[] info = gson.fromJson(command, String[].class);
        info[0] = gson.fromJson(info[0], String.class);
        String commandName = info[0];
        System.out.println(command);
        if(commandName.equals("SendPlaceOnTableToClient")) {
            myPlace = (int)(Double.parseDouble(info[1]));
        }
        if(commandName.equals("SendPokerInfoToClient")||commandName.equals("SendUpdateInfoToClient")) {
            pokerGame = gson.fromJson(info[1], new TypeToken<Poker>(){}.getType());
            clientWindow.setPlayerTurnIndex(pokerGame.getPlayerIndexTurn());
            clientWindow.setTableCards(pokerGame.getCardsOnTable());
            clientWindow.setPot(pokerGame.getPot());
            clientWindow.setCurrentBet(pokerGame.getBet());
            for(Player p : pokerGame.getPlayers()) {
                if(p.getPlace() == myPlace) {
                    myPlayer = p;
                    clientWindow.setMyPlayer(myPlayer);
                    clientWindow.showMyCards(myPlayer);
                }
                clientWindow.addPlayer(p);
            }
            clientWindow.setPlayersBets(pokerGame.getPlayers());
            clientWindow.showPlayersCards(pokerGame.getPlayers());
            clientWindow.setMoveButtons();
        }
        if(commandName.equals("SendPokerInfoToClient")) {
            clientWindow.startGame();
        }
        if(commandName.equals("SendUpdateInfoToClient")) {
            if(pokerGame.getWinnerIndex() > -1) {
                clientWindow.setWinner(pokerGame.getWinnerIndex());
                clientWindow.showWinner();
            } else {
                clientWindow.setPlayerTurnIndex(pokerGame.getPlayerIndexTurn());
                clientWindow.startProgressBar();
                for (Player p : pokerGame.getPlayers()) {
                    if (p.isFold())
                        clientWindow.setPlayerFold(p.getPlace());
                }
            }
        }
    }
}
