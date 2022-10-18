package PokerJava;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String nickName;
    int place;
    List<Card> hand = new ArrayList<>();
    int chips;
    boolean isFold = false;
    String role = "Player";
    int bet = -1;
    int score;

    public Player(int place) {
        this.place = place;
        chips = 1000;
    }

    public boolean isFold() {
        return isFold;
    }

    public void setFold(boolean fold) {
        isFold = fold;
    }

    public int getPlace() {
        return place;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
