package com.example.BuyOrganic;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    //variables defined as private for security
    private EditText memail, mpassword;
    ImageView loginButton;
    FirebaseAuth fauth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login();



    }

    public void login() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            startActivity(new Intent(getApplicationContext(), Home.class));
        }


        // the widgets are located in the xml filed so that operations can be performed on them
        memail = findViewById(R.id.loginEmail);
        mpassword = findViewById(R.id.loginPassword);
        fauth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {//checks whether login button is clicked


            @Override
            //following method executed if login button clicked
            public void onClick(View v) {


                //retrieves text from respective fields in login page
                String email = memail.getText().toString().trim();
                String password = mpassword.getText().toString().trim();

                //checks whether fields are left empty
                if (TextUtils.isEmpty(email)) {
                    memail.setError("email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mpassword.setError("password is required");
                    return;
                }

                //signs the user in
                fauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//checks if task is successful
                            Toast.makeText(Login.this,"Login Succesful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Home.class)); //if user logs in they are directed to home page
                        }

                        else {
                            Toast.makeText(Login.this,"Login Unsuccesful"+task.getException().getMessage(), Toast.LENGTH_LONG).show(); // error message if login unsuccessful
                        }
                    }
                });

            }
        });
    }

    //method to redirect user to registration page. Will be used for clikcing the sign in button in registration page for if user already has account
    public void registrationPage(View v) {
        startActivity(new Intent(getApplicationContext(), Registration.class));
        finish();


    }


}
