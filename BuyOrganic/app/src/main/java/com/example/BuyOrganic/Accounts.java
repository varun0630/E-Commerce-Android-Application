package com.example.BuyOrganic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Accounts extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
    }


    //method to redirect user to Home Page
    public void homePage(View v) {

        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();


    }

    //method to redirected user to items page
    public void itemsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Items.class));
        finish();


    }
    //method to redirect user to cart page
    public void cartPage(View v) {

        startActivity(new Intent(getApplicationContext(), Cart.class));
        finish();


    }
    //method to redirect user to accounts page
    public void accountsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Accounts.class));
        finish();


    }

    //method to redirect user to profile page
    public void profilePage(View v) {

        startActivity(new Intent(getApplicationContext(), Profile.class));
        finish();


    }

    //method to redirect user to orders page
    public void ordersPage(View v) {

        startActivity(new Intent(getApplicationContext(), Orders.class));
        finish();


    }

    //method to log user out
    public void logout(View v) {
        FirebaseAuth.getInstance().signOut(); //signs user out
        startActivity(new Intent(getApplicationContext(), Login.class));//redirects user to Login page
        Toast.makeText(Accounts.this, "Logged Out", Toast.LENGTH_SHORT).show();//logged out message is displayed

    }





}