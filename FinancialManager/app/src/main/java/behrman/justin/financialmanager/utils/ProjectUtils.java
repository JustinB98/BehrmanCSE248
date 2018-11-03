package behrman.justin.financialmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import behrman.justin.financialmanager.R;
import behrman.justin.financialmanager.model.CardType;

public class ProjectUtils {

    public final static String LOG_TAG = ProjectUtils.class.getSimpleName();

    private ProjectUtils() {}

    /**
     *
     * @param date YYYY-MM-DD
     * @return
     */
    public static Date convertToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Trouble parsing date string: " + date);
            return null;
        }
    }

    public static String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String formatNumber(double d) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(d);
    }

    /**
     *
     * @param month 1-12
     * @return
     */
    public static int maxDaysInMonth(int year, int month) {
        // calendar object deals with 0-11, need to offset
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String convertToJSON(String name, Object obj) {
        return "\"" + name + "\":" + "\"" + obj + "\"";
    }

    public static CardType convertToCardType(String cardType) {
        if (cardType.equals(StringConstants.AUTO_CARD_CLASS)) {
            return CardType.AUTO;
        } else if (cardType.equals(StringConstants.MANUAL_CARD_CLASS)) {
            return CardType.MANUAL;
        } else {
            return null;
        }
    }

    /**
     * 1-12
     * @return
     */
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String turnToJSONObject(String... objects) {
        // looked at Arrays.toString();
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; ; ++i) {
            sb.append(objects[i]);
            if (i == sb.length() - 1) {
                return sb.append("}").toString();
            }
            sb.append(",");
        }
    }

    public static String getFullDate(Date date) {
        String formattedString = convertDateToString(date);
        String[] dateTokens = formattedString.split("-");
        int month = Integer.parseInt(dateTokens[1]);
        String strMonth = getMonthName(month);
        return strMonth + " " + dateTokens[2] + ", " + dateTokens[0];
    }

    /**
     * 1-12
     * @param month
     * @return
     */
    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }

    public static boolean deepEquals(@Nullable String s1, @Nullable String s2) {
        if (s1 == s2) { // same object?
            return true;
        } else if (s1 == null || s2 == null) { // one is null and other is not?
            return false;
        } else if (s1.trim().toLowerCase().equals(s2.trim().toLowerCase())) { // contents equal?
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO fix this method because it's using sequential search, maybe binary search? Maybe I shouldn't worry because it's not like there
     * @param currencyCode
     * @param context
     * @return
     */
    public static int convertCurrencyCodeStringToIndex(String currencyCode, Context context) {
        String[] currencyCodes = context.getResources().getStringArray(R.array.currency_codes);
        for (int i = 0; i < currencyCodes.length; ++i) {
            if (currencyCode.equals(currencyCodes[i])) {
                return i;
            }
        }
        return -1;
    }

}
