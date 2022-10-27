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
    private boolean canBigBlindBet = true;
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
        blindMoves();
        canBigBlindBet = true;
    }

    public void move(int playerPlace, String moveType, int moveBet) {
        if(moveType.equals("BET")) {
            for(Player p : players) {
                if(playerPlace == p.place)
                {
                    p.chips -= moveBet;
                    if(p.bet == -1)
                        p.bet = moveBet;
                    else
                        p.bet += moveBet;
                    p.lastMove = moveType;
                }
            }
            this.pot += moveBet;
            this.bet = moveBet;
        }
        if(moveType.equals("FOLD")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.isFold = true;
                    p.bet = -1;
                    p.lastMove = moveType;
                }
            }
        }
        if(moveType.equals("CHECK")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.lastMove = moveType;
                    if(p.bet == -1)
                        p.bet = 0;
                }
            }
        }
        if(moveType.equals("RAISE")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    p.chips -= moveBet;
                    if(p.bet == -1)
                        p.bet = moveBet;
                    else
                        p.bet += moveBet;
                    p.lastMove = moveType;
                    if(this.bet == 0)
                        this.bet = moveBet;
                    else
                        this.bet += moveBet;
                    this.pot += moveBet;
                }
            }
        }
        if(moveType.equals("CALL")) {
            for (Player p : players) {
                if(p.getPlace() == playerPlace) {
                    if(p.bet > 0) {
                        p.chips -= (this.bet - p.bet);
                        this.pot += (this.bet - p.bet);
                    }
                    else {
                        p.chips -= this.bet;
                        this.pot += this.bet;
                    }
                    p.bet = this.bet;
                    p.lastMove = moveType;
                }
            }
        }
        playerIndexTurn = (playerIndexTurn + 1) % playersAtTheTable;
        checkForStage();
        checkForFoldWinner();
    }
    public void blindMoves() {
        this.move(playerIndexTurn, "BET", 5);
        this.move(playerIndexTurn, "BET", 10);
    }
    public void checkForStage() {
        boolean nextGameStage = true;
        for(Player p : players) { // IF ALL PLAYERS HAVE CHECKED
            if(p.bet == 0 && p.bet == this.bet);
            else nextGameStage = false;
        }
        if(!nextGameStage) {
            nextGameStage = true;
            for (Player p : players) { // IF SOMEONE CALLED AFTER BIG BLIND BET
                if (p.bet == this.bet) ;
                else nextGameStage = false;
            }
            if(canBigBlindBet) {
                int prefix = (playersAtTheTable > 2 ? 2 : 0);
                for(Player p : players) {
                    if(p.getPlace() == ((dealerIndexTurn+prefix)%playersAtTheTable)) {
                        if(!p.getLastMove().equals("CHECK")) {
                            nextGameStage = false;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Is next stage : " + nextGameStage);
        if(nextGameStage) {
            if(gameStage == GameStage.River) {
                checkForWinner();
            } else {
                gameStage = gameStage.next();
                canBigBlindBet = false;
                playerIndexTurn = (dealerIndexTurn+1)%playersAtTheTable;
                int howMuchCardsToOpen = gameStage.ordinal() + 2;
                for (int i = 0; i < howMuchCardsToOpen; i++)
                    cardsOnTable.get(i).setOpened(true);
                for (Player p : players)
                    p.bet = -1;
                this.bet = 0;
            }
        }
    }

    public void checkForFoldWinner() {
        int foldCounter = 0;
        for (Player p : players) {
            if(p.isFold()) foldCounter++;
        }
        if(foldCounter == playersAtTheTable) {
            System.out.println("All players gone...");
            winnerIndex = 0;
        }
        if(foldCounter == playersAtTheTable - 1) {
            for (Player p : players) {
                if(!p.isFold())
                {
                    System.out.println("Winner index: " + winnerIndex);
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

    public void setPlayerLeft(int place) {
        for(Player p : players)
            if(p.getPlace() == place)
                p.setFold(true);
        checkForFoldWinner();
    }

    private void CreatePlayers(int howManyPlayers)
    {
        if(!players.isEmpty()) {
            players.clear();
            dealerIndexTurn = (dealerIndexTurn+1) % howManyPlayers;
        }
        if(howManyPlayers == 2) {
            playerIndexTurn = (dealerIndexTurn+1) % howManyPlayers;
            players.add(new Player(0));
            players.add(new Player(1));
            players.get(dealerIndexTurn).setRole("Big Blind");
            players.get(playerIndexTurn).setRole("Small Blind");
        } else {
            playerIndexTurn = (dealerIndexTurn+1) % howManyPlayers;
            for (int counter = 0; counter < howManyPlayers; counter++) {
                players.add(new Player(counter));
                players.get(counter).setRole("Player");
                if (counter == dealerIndexTurn)
                    players.get(counter).setRole("Dealer");
                if (counter == (dealerIndexTurn + 1) % howManyPlayers)
                    players.get(counter).setRole("Small Blind");
                if (howManyPlayers > 2)
                    if (counter == (dealerIndexTurn + 2) % howManyPlayers)
                        players.get(counter).setRole("Big Blind");
            }
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
