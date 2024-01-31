package com.example.BuyOrganic;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Arrays;
import java.util.Map;

public class Registration extends AppCompatActivity {


    String username;
    ImageButton registerButton;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    // variables defined as private for security
    private EditText mname,memail,mmobileNo,
            mcommunityName,mvillaNo,mpassword;

    private String name,email,mobileNo,
            communityName,villaNo,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //createUser method is called when the Registration activity is opened
        createUser();


    }


    // method used to create the user
    public void createUser() {

        // the widgets are located in the xml filed so that operations can be performed on them
        mname = findViewById(R.id.name);
        memail = findViewById(R.id.email);
        mmobileNo = findViewById(R.id.mobilaNo);
        mcommunityName = findViewById(R.id.communityName);
        mvillaNo = findViewById(R.id.villaNo);
        mpassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        db = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance(); // an instance of FirebaseAuth is created
        if (fAuth.getCurrentUser() != null) { //checks whether user is logged in
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }



        registerButton.setOnClickListener(new View.OnClickListener() { //checks whether register button is clicked
            @Override

            //following method executed if register button clicked
            public void onClick(View v) {

                //gets the details of the user from the fields in the registration page
                name = mname.getText().toString().trim();
                email = memail.getText().toString().trim();
                mobileNo = mmobileNo.getText().toString().trim();
                communityName = mcommunityName.getText().toString().trim();
                villaNo = mvillaNo.getText().toString().trim();
                password = mpassword.getText().toString().trim();



                //checks whether fields are empty and appropriate error message displayed
                if (TextUtils.isEmpty(name)) {
                    mname.setError("Name is required");
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    memail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(mobileNo)) {
                    mmobileNo.setError("Phone number is required");
                    return;
                }
                if (TextUtils.isEmpty(communityName)) {
                    mcommunityName.setError("Community Name is required");
                    return;
                }
                if (TextUtils.isEmpty(villaNo)) {
                    mvillaNo.setError("Villa number is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mpassword.setError("Password is required");
                    return;
                }

                if (mobileNo.length()!=10) { //checks whether mobile number has a length=10
                    mmobileNo.setError("Invalid number");
                    return;
                }


                //adds user details to HashMap
                Map<String,Object> userDetails = new HashMap<>();
                userDetails.put("Name",name);
                userDetails.put("Email",email);
                userDetails.put("Phone Number",mobileNo);
                userDetails.put("Community Name", communityName);
                userDetails.put("Villa No",villaNo);


                //HashMap created for the purposes of creating an empty user cart
                Map<String, Object> user_Cart = new HashMap<>();
                user_Cart.put("Item Name", Arrays.asList("Apple","Banana","Broccoli","Cucumber","Orange","Tomatoes")); // add a list or array
                user_Cart.put("Cost", Arrays.asList("0","0","0","0","0","0"));
                user_Cart.put("Quantity",Arrays.asList("0","0","0","0","0","0"));




                // creates user with email and password
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { //checks if task was successful
                            Toast.makeText(Registration.this, "Registration Succesful", Toast.LENGTH_LONG).show();
                            fAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), Login.class)); //redirects user to login page



                            //adds user details to User Details collection with the document name based on user's email
                            db.collection("User Details").document(email)
                                    .set(userDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registration.this, "Registration Succesful", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                            // an empty cart is created for ths user with document name as user's email
                            db.collection("Cart").document(email)
                                    .set(user_Cart)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                        else {
                            Toast.makeText(Registration.this,"Registration Unsuccesful"+task.getException().getMessage(), Toast.LENGTH_LONG).show(); //error message if registration wasn't successful

                        }

                    }
                });

            }

        });

    }

    //method is used to redirect user to login page. Will be used to click sign up button when user has to create account
    public void loginPage(View v) {
        startActivity(new Intent(getApplicationContext(), Login.class));

        finish();

    }







}