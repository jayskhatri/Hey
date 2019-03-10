package com.jayskhatri.hey.hey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {

    Button proceed;
    EditText emailField;
    View viewLayout;
    RelativeLayout rlForgotPasswordActivity;
    ImageView logoInForgotPassword;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logoInForgotPassword || view.getId() == R.id.rlForgotPasswordActivity) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        else if(view.getId()==R.id.proceed){
            forgotPassword();
        }
    }

    public void forgotPassword(){
        ParseUser.requestPasswordResetInBackground(String.valueOf(emailField.getText()),
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast toast1=Toast.makeText(getApplicationContext(),"Toast:Gravity.TOP",Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER,0,0);
                            toast1.setView(viewLayout);
                            toast1.show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Enter Valid Email Id!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        proceed= findViewById(R.id.proceed);
        rlForgotPasswordActivity=findViewById(R.id.rlForgotPasswordActivity);
        emailField=findViewById(R.id.emailinf);
        logoInForgotPassword=findViewById(R.id.logoInForgotPassword);

        LayoutInflater layoutInflator=getLayoutInflater();
        viewLayout=layoutInflator.inflate(R.layout.successfully_sent_link,(ViewGroup)findViewById(R.id.custom_layout));

        proceed.setOnClickListener(this);
        rlForgotPasswordActivity.setOnClickListener(this);
        logoInForgotPassword.setOnClickListener(this);
    }
}
