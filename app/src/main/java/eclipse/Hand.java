package eclipse;


import java.util.ArrayList;


public class Hand {


    private ArrayList<Card> mCards;


    // dealer hand

    public Hand(ArrayList<Card> cards) {

        setCards(cards);


    }


    public ArrayList<Integer> getValues() {

        ArrayList<Integer> values = new ArrayList<Integer>();


        for (Card c : getCards()) {

            values.add(c.getValue());

        }


        sortAscending(values);

        return values;

    }


    public ArrayList<Card.Suit> getSuites() {

        ArrayList<Card.Suit> suites = new ArrayList<Card.Suit>();


        for (Card c : getCards()) {

            suites.add(c.getSuit());

        }

        return suites;

    }


    public void setValues(int[] values) {


        for (int i = 0; i < 5; i++) {

            mCards.get(i).setValue(values[i]);

        }

    }


    public void setSuits(char[] suits) {

        for (int i = 0; i < 5; i++) {

            mCards.get(i).setValue(suits[i]);

        }

    }


    public ArrayList<Card> getCards() {

        return mCards;

    }


    public void setCards(ArrayList<Card> cards) {

        mCards = cards;

    }


    public void sortAscending(ArrayList<Integer> list) {


        int temp;

        for (int i = 0; i < list.size(); i++) {

            for (int j = i + 1; j < list.size(); j++) {

                if (list.get(i) > list.get(j)) {

                    temp = list.get(i);

                    list.set(i, list.get(j));

                    list.set(j, temp);

                }

            }

        }


    }


}