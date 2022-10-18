package PokerJava;

import java.util.ArrayList;
import java.util.Collections;

public class Poker
{
    private ArrayList<Card> cardsInPokerHand = new ArrayList<Card>();
    private ArrayList<Card> cardsOnTable = new ArrayList<Card>();
    private ArrayList<Player> players = new ArrayList<Player>();

    private int pot = 0;
    private int bet = 0;
    private int playerIndexTurn = 0;
    private int dealerIndexTurn = 0;
    private int playersAtTheTable = 0;
    private int winnerIndex = -1;
    private GameStage gameStage = GameStage.Preflop;

    private void ShuffleCards()
    {
        Collections.shuffle(cardsInPokerHand);
    }

    public Poker(int playersAtTheTable) {
        this.playersAtTheTable = playersAtTheTable;
    }

    public void startGame()
    {
        CreatePlayers(playersAtTheTable);
        GenerateNewPokerHand();
        ShuffleCards();
        HandOutCardsToPlayers();
        PlaceCardsOnTable();
        winnerIndex = -1;
        pot = 0;
        for(Player p : players)
            p.bet = -1;
        gameStage = GameStage.Preflop;
    }

    public void move(int playerPlace, String moveType, int bet) {
        if(moveType.equals("BET")) {
            for(Player p : players) {
                if(playerPlace == p.place)
                {
                    p.chips -= bet;
                    p.bet = bet;
                }
            }
            pot += bet;
            this.bet = bet;
        }
        if(moveType.equals("FOLD")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.isFold = true;
                    p.bet = -1;
                }
            }
        }
        if(moveType.equals("CHECK")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.bet = 0;
                }
            }
        }
        if(moveType.equals("RAISE")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.chips -= bet;
                    p.bet += bet;
                }
            }
        }
        if(moveType.equals("CALL")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.chips -= (this.bet - p.bet);
                    p.bet = this.bet;
                }
            }
        }
        playerIndexTurn = (playerIndexTurn + 1) % playersAtTheTable;
        checkForStage();
        checkForFoldWinner();
    }

    public void checkForStage() {
        boolean nextGameStage = true;
        int equalBet = 0;
        for(Player p : players) {
             if(!p.isFold())
             {
                 equalBet = p.bet;
                 break;
             }
        }
        for(Player p : players) {
            if(p.bet > -1 && p.bet == equalBet);
            else nextGameStage = false;
        }
        if(nextGameStage) {
            if(gameStage == GameStage.River) {
                checkForWinner();
            } else {
                gameStage = gameStage.next();
                int howMuchCardsToOpen = gameStage.ordinal() + 2;
                for (int i = 0; i < howMuchCardsToOpen; i++)
                    cardsOnTable.get(i).setOpened(true);
                for (Player p : players)
                    p.bet = -1;
            }
        }
    }

    public void checkForFoldWinner() {
        int foldCounter = 0;
        for (Player p : players) {
            if(p.isFold()) foldCounter++;
        }
        if(foldCounter == playersAtTheTable - 1) {
            for (Player p : players) {
                if(!p.isFold())
                {
                    winnerIndex = p.getPlace();
                }
            }
        }
    }

    public void checkForWinner() {
        for(Player p : players) {
            //high card
            if(p.hand.get(0).GetName().ordinal() >= p.hand.get(1).GetName().ordinal())
                p.score = p.hand.get(0).GetName().ordinal();
            else
                p.score = p.hand.get(1).GetName().ordinal();
            //high card
            /*//pairs
            int pairs = 0;
            for(int i = 0; i < 5; i++)
            {
                if(p.hand.get(0).GetName().toString().equals(cardsOnTable.get(i).GetName().toString()))
                    pairs++;
                if(p.hand.get(1).GetName().toString().equals(cardsOnTable.get(i).GetName().toString()))
                    pairs++;
            }
            if(p.hand.get(0).GetName().toString().equals(p.hand.get(1).GetName().toString())) pairs++;
            if(pairs == 1)
                p.setScore(15);
            if(pairs > 1)
                p.setScore(16);
            //pairs*/
        }
        int maxScore = 0;
        int maxScorePlace = 0;
        for(Player p : players) {
            if(p.score > maxScore) {
                maxScore = p.score;
                maxScorePlace = p.place;
            }
        }
        winnerIndex = maxScorePlace;
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public int getPot() {
        return pot;
    }

    public int getBet() {
        return bet;
    }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public ArrayList<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayersAtTheTable(int playersAtTheTable) {
        this.playersAtTheTable = playersAtTheTable;
    }

    public int getPlayerIndexTurn() {
        return playerIndexTurn;
    }

    private void CreatePlayers(int howManyPlayers)
    {
        if(!players.isEmpty()) {
            players.clear();
            dealerIndexTurn = (dealerIndexTurn+1) % howManyPlayers;
        }
        playerIndexTurn = (dealerIndexTurn+1) % howManyPlayers;

        for (int counter = 0; counter < howManyPlayers; counter++)
        {
            players.add(new Player(counter));
            players.get(counter).setRole("Player");
            if(counter == dealerIndexTurn)
                players.get(counter).setRole("Dealer");
            if(counter == (dealerIndexTurn+1)%howManyPlayers)
                players.get(counter).setRole("Small Blind");
            if(howManyPlayers > 2)
                if(counter == (dealerIndexTurn+2)%howManyPlayers)
                    players.get(counter).setRole("Big Blind");
        }
    }

    private void GenerateNewPokerHand()
    {
        for(CardColor cardColor : CardColor.values())
        {
            ArrayList<Card> newCards = CreateCards(cardColor);
            cardsInPokerHand = SummCards(newCards, cardsInPokerHand);
        }
        cardsOnTable = new ArrayList<Card>();
    }

    private void HandOutCardsToPlayers() {
        for(Player p : players) {
            p.AddCard(cardsInPokerHand.get(0));
            p.AddCard(cardsInPokerHand.get(1));
            cardsInPokerHand.remove(0);
            cardsInPokerHand.remove(0);
        }
    }

    private void PlaceCardsOnTable() {
        for(int i = 0; i < 5; i++) {
            cardsOnTable.add(cardsInPokerHand.get(0));
            cardsInPokerHand.remove(0);
        }
    }

    private ArrayList<Card> CreateCards(CardColor cardColor)
    {
        ArrayList<Card> cards = new ArrayList<Card>();

        for (int counter = 0; counter < CardName.values().length; counter++)
        {
            Card card = new Card(cardColor,CardName.values()[counter]);
            cards.add(card);
        }

        return cards;
    }

    private ArrayList<Card> SummCards(ArrayList<Card> firstArray, ArrayList<Card> secondArray)
    {
        ArrayList<Card> newArrayOfCards = new ArrayList<Card>();

        for (Card card: firstArray)
        {
            newArrayOfCards.add(card);
        }

        for (Card card: secondArray)
        {
            newArrayOfCards.add(card);
        }

        return newArrayOfCards;
    }

    
}
