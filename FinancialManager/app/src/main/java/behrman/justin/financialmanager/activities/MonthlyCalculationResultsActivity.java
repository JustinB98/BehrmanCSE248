package behrman.justin.financialmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import behrman.justin.financialmanager.R;
import behrman.justin.financialmanager.model.Card;
import behrman.justin.financialmanager.utils.StringConstants;

public class MonthlyCalculationResultsActivity extends AppCompatActivity {

    public final static String LOG_TAG = MonthlyCalculationResultsActivity.class.getSimpleName() + "debug";

    private ListView listView;
    private TextView dateView, numberView;
    private ProgressBar progressBar;
    private View container;

    private int month, year;
    private List<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_calculation_results);
        getExtras();
        extractView();
        setToLoadView();
        update();
    }

    private void update() {
        ParseCloud.callFunctionInBackground(StringConstants.PARSE_CLOUD_FUNCTION_CALCULATE_MONTHLY, params(), new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                Log.i(LOG_TAG, "returned with: " + object + ", e: " + e);
                if (e == null) {
                    Log.i(LOG_TAG, "object: " + object);
                } else {
                    Log.i(LOG_TAG, "e: " + e.toString() + ", code: " + e.getCode());
                }
            }
        });
    }

    private HashMap<String, Object> params() {
        HashMap<String, Object> params = new HashMap<>(4);
        ArrayList<String> cardNames = new ArrayList<>(cards.size());
        ArrayList<String> cardTypes = new ArrayList<>(cards.size());
        separateCardList(cardNames, cardTypes);
        params.put(StringConstants.PARSE_CLOUD_PARAMETER_CARD_NAME_LIST, cardNames);
        params.put(StringConstants.PARSE_CLOUD_PARAMETER_CARD_TYPE_LIST, cardTypes);
        params.put(StringConstants.PARSE_CLOUD_PARAMETER_MONTH, month);
        params.put(StringConstants.PARSE_CLOUD_PARAMETER_YEAR, year);
        return params;
    }

    private void separateCardList(List<String> cardNames, List<String> cardTypes) {
        for (Card card : cards) {
            cardNames.add(card.getCardName());
            cardTypes.add(card.getCardType().toString());
        }
    }

    private void getExtras() {
        cards = (List<Card>) getIntent().getSerializableExtra(StringConstants.SELECTED_CARDS_KEY);
        year = getIntent().getIntExtra(StringConstants.YEAR_KEY, -1);
        month = getIntent().getIntExtra(StringConstants.MONTH_SPINNER_POSITION_KEY, -1);
        ++month; // was 0-11, need to change 1-12
    }

    private void setToLoadView() {
        container.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setToReady() {
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    private void extractView() {
        listView = findViewById(R.id.list_view);
        dateView = findViewById(R.id.date_view);
        numberView = findViewById(R.id.number_view);
        progressBar = findViewById(R.id.progress_bar);
        container = findViewById(R.id.view_container);
    }

}