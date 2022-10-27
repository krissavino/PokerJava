package Server;

import Commands.Enums.CommandEnum;
import Commands.Interfaces.ICommandManager;
import Commands.Model.CommandModel;
import PokerJava.Player;
import PokerJava.Poker;
import Server.Commands.Empty;
import Server.Commands.GiveAwayCards;
import Server.Commands.Stop;
import Server.Interfaces.IServer;
import Server.Interfaces.IThreadController;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements IServer
{
    private ServerSocket serverSocket;
    private final LinkedHashMap<Socket, ClientSocket> clients = new LinkedHashMap<Socket, ClientSocket>();
    private final Map<ClientSocket, Player> playersInGame = new HashMap<ClientSocket, Player>();
    private final Map<ClientSocket, Player> playersInQueue = new HashMap<ClientSocket, Player>();
    private final LinkedHashMap<CommandEnum, ICommandManager> commands = new LinkedHashMap<CommandEnum, ICommandManager>();

    private Timer gameTimer;
    private boolean isGameStarted = false;
    private int maxClients = 1;
    private int clientsCount = 0;
    private int port = 2121;
    private final IThreadController threadController = new ThreadController();

    private Poker pokerGame;

    public static void main(String[] args)
    {
        ServerContainer.getServer().start();
    }

    Server(int maxClients, int port)
    {
        if(maxClients < 1)
            this.maxClients =1;
        else
            this.maxClients = maxClients;

        this.port = port;

        commands.put(CommandEnum.Stop,new Stop());
        commands.put(CommandEnum.Empty,new Empty());
        commands.put(CommandEnum.GiveAwayCards,new GiveAwayCards());

        pokerGame = new Poker(0);
    }

    public void start()
    {
        threadController.reset();

        try
        {
            InetAddress localHostAddress = InetAddress.getLocalHost();

            serverSocket = new ServerSocket(2121,maxClients,localHostAddress);

            Thread threadConnectClients = new Thread(()->waitForClient());
            threadConnectClients.start();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void stop()
    {
        try
        {
            threadController.Cancel();
            serverSocket.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        System.out.println("Server stopped");
    }

    public void removeClient(Socket socket)
    {
        for(Map.Entry<ClientSocket, Player> entry: playersInQueue.entrySet()) {
            if(entry.getKey().getSocket() == socket)
                playersInQueue.remove(entry.getKey());
        }
        Iterator<Map.Entry<ClientSocket,Player>> iterator = playersInGame.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ClientSocket,Player> entry = iterator.next();
            if(entry.getKey().getSocket() == socket){
               /* pokerGame.setPlayerLeft(playersInGame.get(entry.getKey()).getPlace());
                if(pokerGame.getWinnerIndex() > -1) {
                    isGameStarted = false;
                    startGameTimer(0);
                }
                sendUpdateToPlayers();*/
                iterator.remove();
            }
        }

        clients.remove(socket);
        clientsCount--;
    }

    private void waitForClient()
    {
        while(!threadController.isCancellationRequested())
        {
            try
            {
                Socket client = serverSocket.accept();
                var serverClient = new ClientSocket(client);
                clients.put(client,serverClient);

                new Thread(()->ListenForMessages(serverClient, threadController)).start();

                System.out.println("Server Connected a Client");
                clientsCount++;

                playersInQueue.put(serverClient, new Player(0));
                System.out.println("SERVER QUEUE: " + playersInQueue.size());
                if((playersInQueue.size() >= 2) && !isGameStarted)
                    checkForStartGame();
            }
            catch (Exception e)
            {
                if(e.getMessage().equals("Socket closed"))
                    return;

                throw new RuntimeException(e);
            }
        }
    }

    public int getMaxClients() {
        return maxClients;
    }

    public int getClientsCount() {
        return clientsCount;
    }

    public LinkedHashMap<Socket, ClientSocket> getClients()
    {
        return clients;
    }

    private void ListenForMessages(ClientSocket clientSocket, IThreadController threadController)
    {
        String message;
        try
        {
            while (!threadController.isCancellationRequested())
            {
                message =  clientSocket.getBufferedReader().readLine();
                executeCommandFromBufferedReader(message);
            }
        }
        catch (IOException e)
        {
            if(e.getMessage().equals("Connection reset"))
            {
                removeClient(clientSocket.getSocket());
                System.out.println("Client removed");
            }
        }
    }

    public void executeCommandFromBufferedReader(String command) {
        Gson gson = new Gson();
        System.out.println(command);
        String[] info = gson.fromJson(command, String[].class);
        info[0] = gson.fromJson(info[0], String.class);
        String commandName = info[0];
        if(commandName.equals("SendPlayerMoveToServer")) {
            if(pokerGame.getWinnerIndex() > -1) return;
            int playerWhoMovedPlace = gson.fromJson(info[1], int.class);
            String playerMoveType = gson.fromJson(info[2], String.class);
            int bet = gson.fromJson(info[3], int.class);
            pokerGame.move(playerWhoMovedPlace, playerMoveType, bet);
            gameTimer.cancel();
            gameTimer.purge();
            System.out.println("Execute move, winner: " + pokerGame.getWinnerIndex());
            if(pokerGame.getWinnerIndex() > -1) {
                startGameTimer(5000);
            } else {
                startGameTimer(20000);
            }
            sendUpdateToPlayers();
        }
    }
    public void sendUpdateToPlayers() {
        CommandModel commandModel = new CommandModel();
        commandModel.set(CommandEnum.SendUpdateInfoToClient.toString(), pokerGame);
        for(Map.Entry<ClientSocket, Player> p : playersInGame.entrySet()) {
            p.getKey().sendMessage(commandModel.getString());
        }
    }
    public void sendPokerInfoToPlayers() {
        CommandModel commandModel = new CommandModel();
        commandModel.set(CommandEnum.SendPokerInfoToClient.toString(), pokerGame);
        for(Map.Entry<ClientSocket, Player> p : playersInGame.entrySet()) {
            p.getKey().sendMessage(commandModel.getString());
        }
    }

    public void startGameTimer(int delay) {
        gameTimer = new Timer();
        System.out.println("Timer started. Winner: " + pokerGame.getWinnerIndex());
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(pokerGame.getWinnerIndex() > -1) {
                    checkForStartGame();
                }
                else {
                    pokerGame.move(pokerGame.getPlayerIndexTurn(), "FOLD", 0);
                    if(pokerGame.getWinnerIndex() > -1) {
                        startGameTimer(5000);
                    } else {
                        startGameTimer(20000);
                    }
                    sendUpdateToPlayers();
                }
            }
        }, delay);
    }

    public void startGame() {
        pokerGame.setPlayersAtTheTable(playersInGame.size());
        pokerGame.startGame();

        ArrayList<Player> pokerPlayers = pokerGame.getPlayers();
        int counter = 0;
        CommandModel commandModel = new CommandModel();
        for(Map.Entry<ClientSocket, Player> p : playersInGame.entrySet()) {
            playersInGame.put(p.getKey(), pokerPlayers.get(counter));
            commandModel.set(CommandEnum.SendPlaceOnTableToClient.toString(), pokerPlayers.get(counter++).getPlace());
            p.getKey().sendMessage(commandModel.getString());
        }
        sendPokerInfoToPlayers();
        startGameTimer(20000);
    }

    public void checkForStartGame() {
        System.out.println("CHECK FOR START");
        if(getClientsCount() < 2) return;
        for(Map.Entry<ClientSocket, Player> p : playersInQueue.entrySet()) {
            playersInGame.put(p.getKey(), new Player(0));
            System.out.println("New Player added to game!");
        }
        playersInQueue.clear();
        if(playersInGame.size() > 1) {
            startGame();
            isGameStarted = true;
        }
    }

}
