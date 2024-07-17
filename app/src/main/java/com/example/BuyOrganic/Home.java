package com.example.BuyOrganic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }



    //method to redirect user to Home page
    public void homePage(View v) {

        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
    }


    //method to redirect user to Items page
    public void itemsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Items.class));
        finish();


    }
    //method to redirect user to Cart page
    public void cartPage(View v) {

        startActivity(new Intent(getApplicationContext(), Cart.class));
        finish();


    }
    //method to redirect user to Accounts page
    public void accountsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Accounts.class));
        finish();


    }
}