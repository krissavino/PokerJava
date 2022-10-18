package PokerJava;

import PokerJava.Card;

import java.util.ArrayList;
import java.util.List;

public class PlayerFromServer
{
    private int id = 0;
    private List<Card> hand = new ArrayList<>();

    public PlayerFromServer(int id)
    {
        this.id = id;
    }

    public boolean AddCard(Card card)
    {
        if(hand.size() < 6)
        {
            hand.add(card);
            return true;
        }
        return false;
    }

    public String GiveAwayCards()
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Player " + id+": ");
        for (Card card: hand)
        {
            stringBuffer.append(String.format("(%s %s) ",card.GetColor(),card.GetName()));
        }
        return stringBuffer.toString();
    }
}
