package com.jayskhatri.hey.hey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {

    EditText usernameField,passwordField,emailField;
    RelativeLayout rlSignUpActivity;
    Button signUpBtn;
    ImageView logoInSignUp;


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.logoInSignUp || view.getId()==R.id.rlsignUpActivity){
            InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

        else if(view.getId()==R.id.signUpBtn){
            try{
                attemptRegister();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.getId()==R.id.email ){
            emailField.setCursorVisible(true);
        }
        else if(view.getId()==R.id.password){
            passwordField.setCursorVisible(true);
        }
        else if(view.getId()==R.id.username){
            usernameField.setCursorVisible(true);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            attemptRegister();
        }
        return false;
    }

    public void alertDisplayer(String title,String message){
        Toast.makeText(getApplicationContext(),title + " " + message,Toast.LENGTH_LONG).show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void attemptRegister(){
        // Reset errors.
        usernameField.setError(null);
        emailField.setError(null);
        passwordField.setError(null);

        // Store values at the time of the login attempt.
        String username=usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            focusView = passwordField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.error_field_required));
            focusView = emailField;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            final ProgressDialog dia=ProgressDialog.show(this,null,getString(R.string.alert_wait));

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    dia.dismiss();
                    if (e == null) {
                        final String title = "Account Created Successfully!";
                        final String message = "Please verify your email before Login";

                        alertDisplayer(title, message);

                        Intent i=new Intent(SignUpActivity.this,LogInActivity.class);
                        startActivity(i);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        final String title = "Error Account Creation failed";
                        final String message = "Account could not be created";

                        alertDisplayer(title, message + " :" + e.getMessage());
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameField=findViewById(R.id.username);
        passwordField=findViewById(R.id.password);
        emailField=findViewById(R.id.email);
        signUpBtn=findViewById(R.id.signUpBtn);
        rlSignUpActivity=findViewById(R.id.rlsignUpActivity);
        logoInSignUp=findViewById(R.id.logoInSignUp);

        logoInSignUp.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        rlSignUpActivity.setOnClickListener(this);
        usernameField.setOnClickListener(this);
        emailField.setOnClickListener(this);
        usernameField.setOnClickListener(this);
        usernameField.setOnKeyListener(this);
        emailField.setOnKeyListener(this);
        passwordField.setOnKeyListener(this);

    }
}
