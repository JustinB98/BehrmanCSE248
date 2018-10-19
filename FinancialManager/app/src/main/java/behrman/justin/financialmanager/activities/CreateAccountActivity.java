package behrman.justin.financialmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import behrman.justin.financialmanager.R;
import behrman.justin.financialmanager.utils.ProjectUtils;

public class CreateAccountActivity extends AppCompatActivity {

    public final static String LOG_TAG = CreateAccountActivity.class.getSimpleName();

    private EditText usernameField, passwordField, confirmPasswordField;
    private Button createAccountBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        extractViews();
        initBtn();
    }

    private void initBtn() {
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        if (fieldsAreValid(username, password)) {
            signUp0(username, password);
        }
    }

    private void switchToMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    private void signUp0(String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        ProjectUtils.hideKeyboard(this);
        progressBar.setVisibility(View.VISIBLE);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    Log.i(LOG_TAG, "createUserWithEmail:success");
                    switchToMenuActivity();
                } else {
                    Log.i(LOG_TAG, "createUserWithEmail:failure", e);
                    showErrorMsg(e.getCode());
                }
            }
        });
    }

    private void showErrorMsg(int errorCode) {
        int msgId;
        switch(errorCode) {
            case ParseException.EMAIL_TAKEN:
            case ParseException.USERNAME_TAKEN: {
                msgId = R.string.username_taken;
                break;
            }
            default: {
                msgId = R.string.server_issue;
            }
        }
        Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
    }

    private boolean fieldsAreValid(String username, String password) {
        if (!isValidEmail(username)) {
            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidPassword(password)) {
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!passwordsEqual(password)) {
            Toast.makeText(this, R.string.passwords_not_equal, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
     }

     private boolean passwordsEqual(String password) {
        String confirmPasswordText = confirmPasswordField.getText().toString();
        return confirmPasswordText.equals(password);
     }

    private boolean isValidEmail(String email) {
        return email.matches("[\\w.]+@\\w+\\.(net|com|edu)");
    }

    // https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
    private boolean isValidPassword(String password) {
        // 5 characters, at least one uppercase and one number
        return password.matches("^(?=.*?[A-Z])(?=.*?[0-9]).{5,}$");
    }

    private void extractViews() {
        usernameField = (EditText) findViewById(R.id.username_input);
        passwordField = (EditText) findViewById(R.id.password_input);
        confirmPasswordField = (EditText) findViewById(R.id.confirm_password_input);
        createAccountBtn = (Button) findViewById(R.id.create_account_btn);
        progressBar = (ProgressBar) findViewById(R.id.signup_progress_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

}
