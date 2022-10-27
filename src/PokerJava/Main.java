package PokerJava;

import Client.Client;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main
{
    public static void main(String[] args)
    {
        ClientWindow frame = new ClientWindow(new Client());
        Player player = new Player(0);
        player.setRole("Dealer");
        player.AddCard(new Card(CardColor.Diamonds,CardName.Card_A));
        player.AddCard(new Card(CardColor.Clubs,CardName.Card_A));
        ArrayList<Player> arrayList = new ArrayList<>();
        arrayList.add(player);
        frame.addPlayer(player);
        frame.showPlayersCards(arrayList);
        player.setRole("Small Blind");
        player.place = 2;
        frame.addPlayer(player);
        player.place = 3;
        player.setRole("Big Blind");
        frame.addPlayer(player);
        frame.startGame();
        player.place = 1;
        player.setRole("Player");
        frame.addPlayer(player);
        frame.showMyCards(player);
        frame.startProgressBar();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.setPlayerTurnIndex(1);
                frame.startProgressBar();
            }
        }, 20000, 20000);
    }

}
