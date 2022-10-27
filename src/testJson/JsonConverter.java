package testJson;


import PokerJava.Poker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonConverter
{

    public static void main(String[] args)
    {
        //test1
        /*Gson gson = new Gson();
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        players.add(new Player(0));
        cards.add(new Card(CardColor.Diamonds, CardName.Card_A));

        String[] str = new String[2];
        str[0] = gson.toJson(players, new TypeToken<ArrayList<Player>>(){}.getType());
        str[1] = gson.toJson(cards, new TypeToken<ArrayList<Card>>(){}.getType());
        String str2 = gson.toJson(str, new TypeToken<String[]>(){}.getType());

        String[] str12 = gson.fromJson(str2, new TypeToken<String[]>(){}.getType());
        ArrayList<Player> players2 = gson.fromJson(str12[0], new TypeToken<ArrayList<Player>>(){}.getType());
        ArrayList<Card> cards2 = gson.fromJson(str12[1], new TypeToken<ArrayList<Card>>(){}.getType());

        System.out.println(players2.get(0).getPlace());
        System.out.println(cards2.get(0).GetName());*/
        //test1

        //test2
        /*ArrayList<Player> players = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        players.add(new Player(0));
        cards.add(new Card(CardColor.Diamonds, CardName.Card_A));
        foo(players, cards);*/
        //test2

        //test3
        Poker poker = new Poker(2);
        poker.startGame();
        Gson gson = new Gson();
        String message = gson.toJson(poker);
        Poker poker2 = gson.fromJson(message, new TypeToken<Poker>(){}.getType());
        System.out.println(poker2.getCardsOnTable());
        //test3
    }

    //test2
    /*static void foo(Object ... obj) {
        Gson gson = new Gson();
        String[] str = new String[2];
        str[0] = gson.toJson(obj[0]);
        str[1] = gson.toJson(obj[1]);
        String str2 = gson.toJson(str);
        String[] str12 = gson.fromJson(str2, new TypeToken<String[]>(){}.getType());
        ArrayList<Player> players2 = gson.fromJson(str12[0], new TypeToken<ArrayList<Player>>(){}.getType());
        ArrayList<Card> cards2 = gson.fromJson(str12[1], new TypeToken<ArrayList<Card>>(){}.getType());

        System.out.println(players2.get(0).getPlace());
        System.out.println(cards2.get(0).GetName());
    }*/
    //test2

}
