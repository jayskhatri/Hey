package com.jayskhatri.hey.hey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {


    public void gotoSignUpActivity(View view){
        Intent i=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(i);
    }


    public void gotoLoginActivity(View view){
        Intent i=new Intent(getApplicationContext(),LogInActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();


        if(ParseUser.getCurrentUser()!=null){
            Intent i=new Intent(HomeActivity.this,UserList.class);
            startActivity(i);
            finish();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}
