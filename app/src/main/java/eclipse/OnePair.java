package eclipse;


public class OnePair {


    private boolean mIsOnePair;

    private int mPairValue;


    public OnePair(boolean isOnePair, int pairValue) {

        mIsOnePair = isOnePair;

        mPairValue = pairValue;

    }


    public boolean isOnePair() {

        return mIsOnePair;

    }


    public void setIsOnePair(boolean isOnePair) {

        mIsOnePair = isOnePair;

    }


    public int getPairValue() {

        return mPairValue;

    }


    public void setPairValue(int pairValue) {

        mPairValue = pairValue;

    }


}