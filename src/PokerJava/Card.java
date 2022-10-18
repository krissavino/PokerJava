package PokerJava;

public class Card
{
    private CardColor color = CardColor.values()[0];
    private CardName name = CardName.values()[0];

    private boolean isOpened = false;
    public Card(CardColor color, CardName name)
    {
        this.color = color;
        this.name = name;
    }
    public CardName GetName() {
        return name;
    }

    public CardColor GetColor() {
        return color;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
