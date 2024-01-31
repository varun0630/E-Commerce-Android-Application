package com.example.BuyOrganic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateProfile extends AppCompatActivity {

    FirebaseFirestore db;
    String email;
    String newEmail;
    String name;
    String phoneNo;
    String communityName;
    String villaNo;
    String password;


    EditText mname;
    EditText memail;
    EditText mmobileNo;
    EditText mcommunityName;
    EditText mvillaNo;


    FirebaseAuth fauth;

    FirebaseUser user;

    List<String> quantity = new ArrayList<>();
    List<String> cost = new ArrayList<>();
    List<String> itemNames = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        mname = findViewById(R.id.name);
        mmobileNo = findViewById(R.id.mobilaNo);
        mcommunityName = findViewById(R.id.communityName);
        mvillaNo = findViewById(R.id.villaNo);
        memail = findViewById(R.id.email);



        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        getCustomerInfo();
    }

    public void getCustomerInfo() {

        db.collection("User Details").document(email)

                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            name = document.getString("Name");
                            phoneNo = document.getString("Phone Number");
                            communityName = document.getString("Community Name");
                            villaNo = document.getString("Villa No");

                            mname.setText(name);
                            mmobileNo.setText(phoneNo);
                            mcommunityName.setText(communityName);
                            mvillaNo.setText(villaNo);
                            memail.setText(email);

                        }
                    }
                });
    }



    //method called when Save Changes button is clicked
    public void updateCustomerInfo(View v) {
        // fields entered are obtained
        name = mname.getText().toString().trim();
        phoneNo = mmobileNo.getText().toString().trim();
        communityName = mcommunityName.getText().toString().trim();
        villaNo = mvillaNo.getText().toString().trim();
        newEmail = memail.getText().toString().trim();

        //error messages are displayed if inaccurate details are given
        if (TextUtils.isEmpty(name)) {
            mname.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            memail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(phoneNo)) {
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

        if (phoneNo.length()!=10) {
            mmobileNo.setError("Invalid number");
            return;
        }

        //HashMap created to update user details
        Map<String,Object> userDetails = new HashMap<>();
        userDetails.put("Name",name);
        userDetails.put("Email",newEmail);
        userDetails.put("Phone Number",phoneNo);
        userDetails.put("Community Name", communityName);
        userDetails.put("Villa No",villaNo);

        //
        db.collection("Cart").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            quantity = (List<String>) document.get("Quantity");
                            cost = (List<String>) document.get("Cost");
                            itemNames = (List<String>) document.get("Item Name");
                        }
                    }
                });



        Map<String,Object> new_Cart = new HashMap<>();
        new_Cart.put("Item Name",itemNames );
        new_Cart.put("Quantity",quantity);
        new_Cart.put("Cost",cost);





        //checks whether email is being changed
        if (!newEmail.equals(email)) {
            user.updateEmail(newEmail) //user email is updated
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), Profile.class)); //takes user back to profile page
                            Toast.makeText(UpdateProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();


                            FirebaseAuth.getInstance().signOut();//user is signed out for security purposes
                            startActivity(new Intent(getApplicationContext(), Login.class));//login page opened
                            Toast.makeText(UpdateProfile.this, "Please Re-login with new Email", Toast.LENGTH_SHORT).show();

                            db.collection("User Details").document(email).delete(); //deletes old document with user's email

                            db.collection("Cart").document(newEmail)
                                    .set(new_Cart); //creates new empty cart with new email

                            db.collection("Cart").document(newEmail)
                                    .update("Item Name",itemNames,"Cost",cost,"Quantity",quantity); //updates the Cart with old cart

                            db.collection("Cart").document(email).delete(); //deletes old email cart

                            db.collection("User Details").document(newEmail)
                                    .set(userDetails); //creates new document named after new email



                            //changes the email linked to each order to the new email
                            db.collection("Orders")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            if (document.getString("Email").equals(email)) {

                                                String documentId = document.getId();

                                                db.collection("Orders")
                                                        .document(documentId).update("Email",newEmail);

                                            }
                                        }

                                    }

                                }
                            });




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        else {



            // if email is same only the remaining User Details are changed in User Details collection
            db.collection("User Details").document(email)
                    .set(userDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), Profile.class));
                            Toast.makeText(UpdateProfile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
        }






    }







    public void profilePage(View v) {

        startActivity(new Intent(getApplicationContext(), Profile.class));
        finish();

    }

}
