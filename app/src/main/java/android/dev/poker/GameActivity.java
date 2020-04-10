package android.dev.poker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import eclipse.Card;
import eclipse.Hand;
import eclipse.HandController;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    public static final Card.Suit CLUBS = Card.Suit.CLUBS;
    public static final Card.Suit DIAMONDS = Card.Suit.DIAMONDS;
    public static final Card.Suit HEARTS = Card.Suit.HEARTS;
    public static final Card.Suit SPADES = Card.Suit.SPADES;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private TypedArray CARD_RESOURCE_IMAGES;
    private ArrayList<Card> mPlayerHand = new ArrayList<>();
    private ArrayList<Card> mDealerHand = new ArrayList<>();
    private final HandController CONTROLLER = new HandController();
    private HashMap<Integer, Card> CARD_IMAGE_MAP;
    private ArrayList<Integer> cardIndices;
    private float mPlayerBalance;
    private float mPot = 0;
    private float mDealerBalance;
    private TextView playerBalance;
    private TextView dealerBalance;
    private int raise;

    private DecimalFormat formatter = new DecimalFormat("'$'0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        CARD_RESOURCE_IMAGES = getResources().obtainTypedArray(R.array.card_images);


        Log.d("CARD_RESOURCE_IMAGES", "null");

        setContentView(R.layout.activity_game);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mContentView = findViewById(R.id.image_table);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        String username = getIntent().getStringExtra(Intent.EXTRA_USER);

        TextView playerNameView = findViewById(R.id.text_player_name);
        playerNameView.setText(username);
        playerBalance = findViewById(R.id.text_player_balance);
        dealerBalance = findViewById(R.id.text_dealer_balance);
        CARD_IMAGE_MAP = generateCardImageMap();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);


        mPlayerBalance = prefs.getFloat(getString(R.string.player_balance), 100);


        mDealerBalance = prefs.getFloat(getString(R.string.dealer_balance), 100);

//        mPlayerBalance = 110;
//        mDealerBalance = 110;
//        updatePreferences();


        if (mPlayerBalance < 10) {
            Toast.makeText(this, "You LOST :(. You do not have enough money to continue playing!", Toast.LENGTH_LONG).show();
        } else if (mDealerBalance < 10) {
            Toast.makeText(this, "You WON :))!! I do not have enough money to continue playing.", Toast.LENGTH_LONG).show();

        } else {
            startRound();
        }

        // Upon interacting with UI controls, delay any scheduled hide()s
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    private void updateBalance() {
        playerBalance.setText(formatter.format(mPlayerBalance));
        dealerBalance.setText(formatter.format(mDealerBalance));
    }

    private void startRound() {
        cardIndices = new ArrayList<>();
        cardIndices.clear();


        mPlayerBalance -= 10;

        mDealerBalance -= 10;

        mPot += 20;

        dealPlayerHand();


        updateBalance();
    }


    private void dealDealerHand() {

        LinearLayout dealerHand = findViewById(R.id.linear_cards_dealer_hand);

        int cardIndex;

        for (int i = 0; i < 2; i++) {
            cardIndex = getRandomCard();
            ((ImageView) dealerHand.getChildAt(i)).setImageDrawable(drawNewCard(cardIndex));
            mDealerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(cardIndex, -1)));
        }

    }

    private void dealPlayerHand() {

        LinearLayout playerHand = findViewById(R.id.linear_cards_player_hand);

        int cardIndex;

        for (int i = 0; i < 2; i++) {
            cardIndex = getRandomCard();
            ((ImageView) playerHand.getChildAt(i)).setImageDrawable(drawNewCard(cardIndex));
            mPlayerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(cardIndex, -1)));
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private HashMap<Integer, Card> generateCardImageMap() {

        HashMap<Integer, Card> retVal = new HashMap<>();


        ArrayList<Card.Suit> suits = new ArrayList<>();

        suits.add(CLUBS);

        suits.add(DIAMONDS);

        suits.add(HEARTS);

        suits.add(SPADES);


        for (int i = 8; i < 60; i++) {


            int key = CARD_RESOURCE_IMAGES.getResourceId(i - 8, -1);

            Card value = new Card(i / 4, suits.get(i % 4));
            retVal.put(key, value);

        }


        return retVal;

    }


    public void onClickDealCard(View view) {


        LinearLayout flop = findViewById(R.id.linear_cards_flop);
        LinearLayout betButtons = findViewById(R.id.linear_bet);

        ImageView turn = findViewById(R.id.image_card_turn);
        ImageView river = findViewById(R.id.image_card_river);


        if (isInvisible(flop)) {

            showFlop(flop);


        } else if (isInvisible(turn)) {
            showCard(turn);


        } else if (isInvisible(river)) {
            showCard(river);
            view.setVisibility(View.GONE);


        }

        betButtons.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        revertLinearLayoutBet();


    }

    private void revertLinearLayoutBet() {

        findViewById(R.id.button_bet).setVisibility(View.VISIBLE);
        findViewById(R.id.button_check).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_bet).setVisibility(View.VISIBLE);
        findViewById(R.id.button_match).setVisibility(View.GONE);

    }

    private void showCard(LinearLayout inFlop, int inIndex) {

        int card = getRandomCard();


        ((ImageView) inFlop.getChildAt(inIndex)).setImageDrawable(drawNewCard(card));

        mPlayerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(card, -1)));
        mDealerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(card, -1)));


    }

    private void showCard(ImageView inView) {

        int card = getRandomCard();
        inView.setVisibility(View.VISIBLE);
        inView.setImageDrawable(drawNewCard(card));
        mPlayerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(card, -1)));
        mDealerHand.add(CARD_IMAGE_MAP.get(CARD_RESOURCE_IMAGES.getResourceId(card, -1)));
        Toast.makeText(getApplicationContext(), "TURN", Toast.LENGTH_SHORT).show();


    }

    private void showFlop(LinearLayout inFlop) {

        for (int i = 0; i < 3; i++) {


            showCard(inFlop, i);

        }


        Toast.makeText(getApplicationContext(), "FLOP", Toast.LENGTH_SHORT).show();

        inFlop.setVisibility(View.VISIBLE);
    }


    public void onClickDeclare(View view) {

        findViewById(R.id.button_next_hand).setVisibility(View.VISIBLE);
        view.setVisibility(View.INVISIBLE);


        if (mPlayerHand.size() == 7 && mDealerHand.size() == 5) {

            dealDealerHand();

            final String[] RANKS = {"High Card", "One Pair", "Two Pair", "Three of A Kind", "Straight", "Flush",

                    "Full House", "4 of a Kind", "Straight Flush", "Royal Flush"};


            Hand playerHand = new Hand(mPlayerHand);
            Hand dealerHand = new Hand(mDealerHand);


            double playerRank = CONTROLLER.findDealerRank(playerHand);
            double dealerRank = CONTROLLER.findDealerRank(dealerHand);


            int result = CONTROLLER.compareHands(playerHand, dealerHand);

            String toastString = "";

            switch (result) {
                case 1:
                    toastString = "You won!! \nYou have a "

                            + RANKS[(int) (playerRank / 1000)] + "\nThe dealer has a "

                            + RANKS[(int) (dealerRank / 1000)];
                    mPlayerBalance += mPot;

                    break;
                case -1:
                    toastString = "You lost :( \nYou have a "

                            + RANKS[(int) (playerRank / 1000)] + "\nThe dealer has a "

                            + RANKS[(int) (dealerRank / 1000)];
                    mDealerBalance += mPot;
                    break;
                case 0:
                    toastString = "You tied :| Crazy\nYou both have a "

                            + RANKS[(int) (playerRank / 1000)];
                    mPlayerBalance += (mPot / 2);
                    break;
                default:
                    toastString = "sus";

            }

            findViewById(R.id.image_card_dealer_first).setVisibility(View.VISIBLE);
            findViewById(R.id.image_card_dealer_second).setVisibility(View.VISIBLE);

            Log.i("BET INFORMATION", "The pot had " + mPot);
            Log.i("BET INFORMATION", "Your balance is " + mPlayerBalance);
            Log.i("BET INFORMATION", "The dealer's balance is " + mDealerBalance);
            mPot = 0;
            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();

            updatePreferences();


            updateBalance();


        } else {

            Toast.makeText(getApplicationContext(), "You must deal all of your cards first!", Toast.LENGTH_SHORT).show();
        }

    }

    private void updatePreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putFloat(getString(R.string.player_balance), mPlayerBalance);
        editor.putFloat(getString(R.string.dealer_balance), mDealerBalance);
        editor.apply();
    }

    private boolean isInvisible(View view) {
        return view.getVisibility() == View.INVISIBLE;
    }


    private Drawable drawNewCard(int index) {
        return CARD_RESOURCE_IMAGES.getDrawable(index);
    }

    private int getRandomCard() {

        int card = (int) (Math.random() * 52);

        while (cardIndices.contains(card)) {
            card = (int) (Math.random() * 52);
        }

        cardIndices.add(card);

        return card;
    }

    public void onClickBet(View view) {


        EditText editText = findViewById(R.id.edit_bet);
        String betString = String.valueOf(editText.getText());

        if (Float.parseFloat(betString) < 1) {
            Toast.makeText(getApplicationContext(), "The bet must be at least 1 dollar - cents are allowed", Toast.LENGTH_SHORT).show();
        } else if (betString.isEmpty()) {
            Toast.makeText(this, "You must bet something, or check", Toast.LENGTH_SHORT).show();
        } else {
            Button button = findViewById(R.id.button_next_card);


            float bet = Float.parseFloat(betString);
            if (checkBalance(bet)) {

                if (bet > mDealerBalance) {
                    Toast.makeText(this, "I am going all in.", Toast.LENGTH_SHORT).show();

                    mPot += (bet + mDealerBalance);

                    mDealerBalance = 0;

                    Log.i("BET INFORMATION", "Your balance is " + mPlayerBalance);

                    ((LinearLayout) view.getParent()).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.image_card_river).getVisibility() == View.INVISIBLE) {
                        button.setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
                    }
                    updateBalance();

                    Toast.makeText(this, "I match you " + formatter.format(bet), Toast.LENGTH_SHORT).show();

                    findViewById(R.id.linear_bet).setVisibility(View.GONE);

                    view.setVisibility(View.VISIBLE);
                    onClickDealCard(findViewById(R.id.button_next_card));
                    onClickDealCard(findViewById(R.id.button_next_card));
                    onClickDealCard(findViewById(R.id.button_next_card));

                    findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
                    findViewById(R.id.linear_bet).setVisibility(View.GONE);

                } else {
                    mPlayerBalance -= bet;
                    mDealerBalance -= bet;

                    mPot += (bet * 2);

                    Log.i("BET INFORMATION", "Your balance is " + mPlayerBalance);

                    ((LinearLayout) view.getParent()).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.image_card_river).getVisibility() == View.INVISIBLE) {
                        button.setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
                    }
                    updateBalance();

                    Toast.makeText(this, "I match you " + formatter.format(bet), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You do not have enough money. All in?", Toast.LENGTH_LONG).show();
            }

        }

    }

    private boolean checkBalance(float inBet) {
        return (inBet < mPlayerBalance);


    }

    public void onClickCheck(View view) {

        raise = (int) (Math.random() * 11);
        Toast.makeText(getApplicationContext(), "ha I raise you " + formatter.format(raise), Toast.LENGTH_LONG).show();
        Button match = findViewById(R.id.button_match);
        match.setText("Match $" + raise);
        changeLinearLayoutBet();


    }

    private void changeLinearLayoutBet() {
        findViewById(R.id.button_bet).setVisibility(View.GONE);
        findViewById(R.id.button_check).setVisibility(View.GONE);
        findViewById(R.id.edit_bet).setVisibility(View.GONE);
        findViewById(R.id.button_match).setVisibility(View.VISIBLE);

    }

    public void onClickNextHand(View view) {

        super.recreate();


    }

    public void onClickFold(View view) {
        mDealerBalance += mPot;
        mPot = 0;
        updateBalance();
        updatePreferences();
        super.recreate();
    }

    public void onClickMatch(View view) {


        Button button = findViewById(R.id.button_next_card);


        mPlayerBalance -= raise;
        mDealerBalance -= raise;

        mPot += (raise * 2);

        Log.i("BET INFORMATION", "Your balance is " + mPlayerBalance);

        ((LinearLayout) view.getParent()).setVisibility(View.INVISIBLE);
        if (findViewById(R.id.image_card_river).getVisibility() == View.INVISIBLE) {
            button.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
        }
        updateBalance();
    }

    public void onClickAllIn(View view) {

        float bet = mPlayerBalance;
        mDealerBalance -= bet;

        mPot += (bet * 2);

        mPlayerBalance = 0;

        Log.i("BET INFORMATION", "Your balance is " + mPlayerBalance);

        Button button = findViewById(R.id.button_next_card);

        ((LinearLayout) view.getParent()).setVisibility(View.INVISIBLE);
        if (findViewById(R.id.image_card_river).getVisibility() == View.INVISIBLE) {
            button.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
        }
        updateBalance();

        Toast.makeText(this, "I match you " + formatter.format(bet), Toast.LENGTH_SHORT).show();

        findViewById(R.id.linear_bet).setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
        onClickDealCard(findViewById(R.id.button_next_card));
        onClickDealCard(findViewById(R.id.button_next_card));
        onClickDealCard(findViewById(R.id.button_next_card));

        findViewById(R.id.button_declare).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_bet).setVisibility(View.GONE);

    }
}







