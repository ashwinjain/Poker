package eclipse;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


public class HandController {


    // returns true if you win

    // returns false if you loose


    public int compareHands(Hand playerHand, Hand dealerHand) {

        double playerRank = findDealerRank(playerHand);
        double dealerRank = findDealerRank(dealerHand);


        if (playerRank > dealerRank) {

            return 1;
        } else if (playerRank == dealerRank) {
            return 0;
        } else {
            return -1;
        }

    }


    /*

     * High card : 0 - 999 Pair : 1009 - 1999 2 pair : 2000 - 2999 3 of a kind :

     * 3000 - 3999 straight : 4000 - 4999 flush : 5000 - 5999 full house : 6000

     * - 6999 4 of a kind : 7000 - 7999 straight flush : 8000 - 8999 royal flush

     * : 9000

     */


    public double findRank(Hand inHand) {


        double retVal = 0;


        // royal flush - suits same and value between 10 & 14

        if (isRoyalFlush(inHand)) {

            retVal = 9999.9;

        }

        // straight flush - if the suits are the same and the cardValues are

        // consecutive

        else if (isStraightFlush(inHand)) {

            retVal = 8000 + sumValues(inHand);


        }

        // four of a kind - if there are 4 of the same value

        else if (isFourOfAKind(inHand)) {

            int quad = findQuad(inHand);

            int kicker = findFourOfAKindKicker(inHand);

            retVal = 7000 + ((quad * 14) + kicker);

        }

        // full house - a trio and a duo

        else if (isFullHouse(inHand)) {

            int trio = findTrio(inHand);

            int duo = findDuo(inHand);

            retVal = 6000 + ((trio * 14) + duo);

        }


        // flush - all suits are the same

        // TODO: check for high card

        // TODO: check for ace

        else if (isFlush(inHand)) {

            for (int i = 4; i >= 0; i--) {

                retVal += (Math.pow(14, i) * inHand.getValues().get(i));

            }

            retVal /= 1000;

            retVal += 5000;

        }


        // straight - all cardValues are consecutive

        else if (isStraight(inHand)) {

            retVal = 4000 + sumValues(inHand);

        }


        // three of a kind - three cardValues are the same and the other two

        // cardValues

        // are different

        else if (isThreeOfAKind(inHand)) {

            int threeOfAKind = findThreeOfAKind(inHand);

            int highKicker = findHighKicker(inHand);

            int lowKicker = findLowKicker(inHand);

            double total = (threeOfAKind * 196) + (highKicker * 14) + lowKicker;

            retVal = 3000 + (total / 10);

        }


        // two pair - a pair of pairs

        else if (isTwoPair(inHand)) {

            int highPair = findHighPair(inHand);

            int lowPair = findLowPair(inHand);

            int kicker = findTwoPairKicker(inHand);

            double total = (highPair * 196) + (lowPair * 14) + kicker;

            retVal = 2000 + (total / 10);

        }


        // one pair - a pair

        else if (isOnePair(inHand).isOnePair()) {

            int pair = findOnePair(inHand);

            ArrayList<Integer> kickerArray = findKickerArray(inHand);

            double total = (pair * 2744) + (kickerArray.get(2) * 196) + (kickerArray.get(1) * 14)

                    + (kickerArray.get(0));


            // 8, 8, 14, 13, 11

            retVal = 1000 + (total / 100);

        }


        // high card - nota

        else {


            for (int i = 4; i >= 0; i--) {

                retVal += (Math.pow(14, i) * inHand.getValues().get(i));

            }


            retVal /= 1000;

        }


        return retVal;

    }


    private ArrayList<Integer> findKickerArray(Hand inHand) {

        ArrayList<Integer> retVal = inHand.getValues();

        int pairValue = findOnePair(inHand);

        for (int i = 0; i < retVal.size(); i++) {

            if (retVal.get(i) == pairValue) {

                retVal.remove(i);

                i--;

            }


        }

        return retVal;

    }


    private int findOnePair(Hand inHand) {


        return isOnePair(inHand).getPairValue();

    }


    // 22, 3, 4, 5

    // 2, 33, 4, 5

    // 2, 3, 44, 5

    // 2, 3, 4, 55

    private OnePair isOnePair(Hand inHand) {

        OnePair retVal;

        ArrayList<Integer> cardValues = inHand.getValues();


        if (cardValues.get(0) == cardValues.get(1)) {


            retVal = new OnePair(true, cardValues.get(0));


        } else if (cardValues.get(1) == cardValues.get(2)) {


            retVal = new OnePair(true, cardValues.get(1));


        } else if (cardValues.get(2) == cardValues.get(3)) {


            retVal = new OnePair(true, cardValues.get(2));


        } else if (cardValues.get(3) == cardValues.get(4)) {


            retVal = new OnePair(true, cardValues.get(3));


        } else {

            retVal = new OnePair(false, 0);

        }


        return retVal;

    }


    private int findTwoPairKicker(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();


        if (cardValues.get(3) != cardValues.get(4)) {

            retVal = cardValues.get(4);

        } else if (cardValues.get(1) != cardValues.get(2)) {

            retVal = cardValues.get(2);

        } else if (cardValues.get(0) != cardValues.get(1)) {

            retVal = cardValues.get(0);

        }

        return retVal;

    }


    private int findLowPair(Hand inHand) {

        ArrayList<Integer> cardValues = inHand.getValues();

        return cardValues.get(1);

    }


    private int findHighPair(Hand inHand) {

        ArrayList<Integer> cardValues = inHand.getValues();

        return cardValues.get(3);

    }


    // 2, 2, 3, 3, 4 - 2, 3, 4

    // 2, 2, 4, 5, 5 - 2, 5, 4

    // 2, 3, 3, 4, 4 - 3, 4, 2

    public boolean isTwoPair(Hand inHand) {

        HashMap<Integer, Integer> map = findFrequency(inHand);


        return map.containsValue(2) && map.size() == 3;

    }


    private int findLowKicker(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();


        int threeOfAKind = findThreeOfAKind(inHand);

        if (threeOfAKind == cardValues.get(0)) {

            retVal = cardValues.get(3);

        } else if (threeOfAKind == cardValues.get(1)) {

            retVal = cardValues.get(0);

        } else if (threeOfAKind == cardValues.get(2)) {

            retVal = cardValues.get(0);

        }

        return retVal;

    }


    private int findHighKicker(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();


        int threeOfAKind = findThreeOfAKind(inHand);

        if (threeOfAKind == cardValues.get(0)) {

            retVal = cardValues.get(4);

        } else if (threeOfAKind == cardValues.get(1)) {

            retVal = cardValues.get(4);

        } else if (threeOfAKind == cardValues.get(2)) {

            retVal = cardValues.get(1);

        }

        return retVal;

    }


    private int findThreeOfAKind(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();

        if (cardValues.get(0) == cardValues.get(2)) {

            retVal = cardValues.get(0);

        } else if (cardValues.get(1) == cardValues.get(3)) {

            retVal = cardValues.get(1);

        } else if (cardValues.get(2) == cardValues.get(4)) {

            retVal = cardValues.get(2);

        }

        return retVal;

    }


    private boolean isThreeOfAKind(Hand inHand) {


        HashMap<Integer, Integer> map = findFrequency(inHand);


        return map.containsValue(3) && map.containsValue(1) && map.size() == 3;


    }


    private int sumValues(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();

        for (int v : cardValues) {

            retVal += v;

        }

        return retVal;

    }


    private int findDuo(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();

        if (cardValues.get(0) == cardValues.get(2)) {

            retVal = cardValues.get(3);

        } else {

            retVal = cardValues.get(0);

        }

        return retVal;


    }


    private int findTrio(Hand inHand) {

        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();

        if (cardValues.get(0) == cardValues.get(2)) {

            retVal = cardValues.get(0);

        } else {

            retVal = cardValues.get(2);

        }

        return retVal;


    }


    public boolean isFullHouse(Hand inHand) {

        // one value is 3 and one is 2

        HashMap<Integer, Integer> map = findFrequency(inHand);


        return map.containsValue(3) && map.containsValue(2) && map.size() == 2;

    }


    private int findFourOfAKindKicker(Hand inHand) {


        int retVal = 0;

        ArrayList<Integer> cardValues = inHand.getValues();

        if (cardValues.get(0) == findQuad(inHand)) {

            retVal = cardValues.get(4);

        } else {

            retVal = cardValues.get(0);

        }

        return retVal;

    }


    private int findQuad(Hand inHand) {

        return inHand.getValues().get(1);

    }


    public boolean isFourOfAKind(Hand inHand) {

        HashMap<Integer, Integer> map = findFrequency(inHand);


        return map.containsValue(4) && map.size() == 2;


    }


    private boolean isStraight(Hand inHand) {

        boolean retVal = true;

        ArrayList<Integer> cardValues = inHand.getValues();

        for (int i = 0; i < cardValues.size() - 1; i++) {

            if ((cardValues.get(i) + 1) != cardValues.get(i + 1)) {

                retVal = false;

            }

        }

        return retVal;

    }


    private boolean isStraightFlush(Hand inHand) {

        boolean retVal = false;


        if (isStraight(inHand) && isFlush(inHand)) {

            retVal = true;

        }

        return retVal;

    }


    private boolean isRoyalFlush(Hand inHand) {

        boolean retVal = false;

        ArrayList<Integer> cardValues = inHand.getValues();

        if ((cardValues.get(0) == 10 && isStraight(inHand)) && isFlush(inHand)) {

            retVal = true;

        }

        return retVal;

    }


    public boolean isFlush(Hand inHand) {


        boolean retVal = true;

        ArrayList<eclipse.Card.Suit> suits = inHand.getSuites();


        eclipse.Card.Suit first = suits.get(0);


        for (eclipse.Card.Suit s : suits) {

            if (first != s) {

                retVal = false;

            }

        }


        return retVal;

    }


    public double findDealerRank(Hand dealerHand) {

        double retVal = 0.0;


        // retVal = findRank(dealerHand);


        for (int i = 0; i < 6; i++) {

            for (int j = i; j < 6; j++) {

                ArrayList<eclipse.Card> copyCards = new ArrayList<eclipse.Card>(dealerHand.getCards());


                copyCards.remove(i);

                copyCards.remove(j);

                Hand fiveCardHand = new Hand(copyCards);

                double currentHand = findRank(fiveCardHand);

                if (currentHand > retVal) {

                    retVal = currentHand;

                }

            }

        }


        return retVal;

    }


    public HashMap<Integer, Integer> findFrequency(Hand inHand) {

        HashMap<Integer, Integer> retVal = new HashMap<Integer, Integer>();

        ArrayList<Integer> cardValues = inHand.getValues();

        HashSet<Integer> uniqueCardValues = new HashSet<Integer>(cardValues);


        for (int key : uniqueCardValues) {


            int value = Collections.frequency(cardValues, key);

            retVal.put(key, value);


        }

        return retVal;

    }
}


