package eclipse;


public class Card {


    private int mValue;

    private Suit mSuit;


    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES, NA

    }


    public Card(int value, Suit suit) {

        mValue = value;

        mSuit = suit;

    }


    public int getValue() {

        return mValue;

    }


    public void setValue(int inValue) {

        mValue = inValue;

    }


    public Suit getSuit() {

        return mSuit;

    }


    public void setSuit(Suit inSuit) {

        mSuit = inSuit;

    }

    public String toString() {
        String retVal = "";

        retVal += "Your card is a " + getValue() + " of " + getSuit();

        retVal = retVal.replace("11", "JACK");
        retVal = retVal.replace("12", "QUEEN");
        retVal = retVal.replace("13", "KING");
        retVal = retVal.replace("14", "ACE");
        return retVal;
    }


}





