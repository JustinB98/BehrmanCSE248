package behrman.justin.financialmanager.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import behrman.justin.financialmanager.R;
import behrman.justin.financialmanager.model.Card;
import behrman.justin.financialmanager.model.Transaction;
import behrman.justin.financialmanager.utils.StringConstants;

public class AddManualTransactionActivity extends AppCompatActivity {

    public final static String LOG_TAG = AddManualTransactionActivity.class.getSimpleName() + "debug";

    private View transactionContainer;
    private Button addTransactionBtn;
    private EditText placeField, amountField, currencyField;
    private CalendarView calendarView;

    private int month, day, year;

    private Card originalCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_transaction);
        originalCard = (Card) getIntent().getSerializableExtra(StringConstants.CARD_KEY);
        extractViews();
        initButton();
        initCalendarListener();
    }

    private void initCalendarListener() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                AddManualTransactionActivity.this.month = month;
                AddManualTransactionActivity.this.year = year;
                AddManualTransactionActivity.this.day = dayOfMonth;
            }
        });
    }

    private void initButton() {
        addTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "button was clicked");
                addTransaction();
            }
        });
    }

    private void addTransaction() {
        if (checkForValidFields()) {
            Log.i(LOG_TAG, "transaction was valid");
            Transaction transaction = getTransaction();
            Log.i(LOG_TAG, "transaction: " + transaction);
            saveTransaction(transaction);
        }
    }

    private void saveTransaction(Transaction transaction) {
        HashMap<String, Object> params = getParams(transaction);
        Log.i(LOG_TAG, "calling function with " + params);
        ParseCloud.callFunctionInBackground(StringConstants.PARSE_CLOUD_FUNCTION_ADD_MANUAL_TRANSACTION, params, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                Log.i(LOG_TAG, "returned with " + object);
                if (e == null) {
                    if (object.trim().toLowerCase().equals("success")) {
                        Toast.makeText(AddManualTransactionActivity.this, "saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.i(LOG_TAG, "e: " + e.toString() + ", code: " + e.getCode());
                }
            }
        });
    }

    private HashMap<String, Object> getParams(Transaction t) {
        HashMap<String, Object> params = new HashMap<>(5);
        params.put(StringConstants.MANUAL_CARD_TRANSACTIONS_PLACE, t.getPlace());
        params.put(StringConstants.MANUAL_CARD_TRANSACTIONS_AMOUNT, t.getAmount());
        params.put(StringConstants.MANUAL_CARD_TRANSACTIONS_CURRENCY_CODE, t.getCurrencyCode());
        params.put(StringConstants.MANUAL_CARD_TRANSACTIONS_DATE, t.getDate());
        params.put(StringConstants.CARD_NAME, originalCard.getCardName());
        return params;
    }

    private Transaction getTransaction() {
        String place = placeField.getText().toString();
        double amount = Double.parseDouble(amountField.getText().toString());
        String currencyCode = currencyField.getText().toString();
        Date date = new GregorianCalendar(year, month, day).getTime();
        return new Transaction(place, amount, date, currencyCode);
    }

    private boolean checkForValidFields() {
        if (TextUtils.isEmpty(placeField.getText())) {
            Toast.makeText(this, "place cannot be empty", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "place is empty");
            return false;
        } else if (TextUtils.isEmpty(amountField.getText())) {
            Toast.makeText(this, "amount cannot be empty", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "amount is empty");
            return false;
        } else if (TextUtils.isEmpty(currencyField.getText())) {
            Log.i(LOG_TAG, "currencyfield is empty");
            Toast.makeText(this, "currency cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void extractViews() {
        transactionContainer = findViewById(R.id.transaction_container);
        addTransactionBtn = findViewById(R.id.add_manual_transaction_btn);
        placeField = findViewById(R.id.place_input);
        amountField = findViewById(R.id.amount_input);
        currencyField = findViewById(R.id.currency_input);
        calendarView = findViewById(R.id.calendar_input);
    }

}