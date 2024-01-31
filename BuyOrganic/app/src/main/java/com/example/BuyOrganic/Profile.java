package com.example.BuyOrganic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Profile extends AppCompatActivity {

    FirebaseFirestore db;
    String email;
    String name;
    String phoneNo;
    String communityName;
    String villaNo;
    String password;

    TextView mname;
    TextView memail;
    TextView mmobileNo;
    TextView mcommunityName;
    TextView mvillaNo;
    TextView mpassword;

    FirebaseUser user;

    TextView newPassword;
    ImageView updatePasssordBtn;
    ImageView backBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mname = findViewById(R.id.name);
        memail = findViewById(R.id.email);
        mmobileNo = findViewById(R.id.mobilaNo);
        mcommunityName = findViewById(R.id.communityName);
        mvillaNo = findViewById(R.id.villaNo);
        mpassword = findViewById(R.id.password);



        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        //getCustomerInfo method called
        getCustomerInfo();
    }


    //gets customer info from User Details collections
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

                            //fields in the page are set to the user details
                            mname.setText(name);
                            mmobileNo.setText(phoneNo);
                            memail.setText(email);
                            mcommunityName.setText(communityName);
                            mvillaNo.setText(villaNo);

                        }
                    }
                });


    }


    public void updateProfilePage(View v) {
        startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
        finish();


    }


    public void accountsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Accounts.class));
        finish();

    }

    // called when Update Password button is clicked
    public void changePassword(View v) {


        //alert dialog created to allow user to enter new password

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_password_dialog,null);
        updatePasssordBtn = view.findViewById(R.id.update_Password_Btn);
        newPassword = view.findViewById(R.id.new_Password);
        backBtn = view.findViewById(R.id.back_Btn);



        alert.setView(view);
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();
        dialog.getWindow().setLayout(900,300);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        //checks whether update password button is clicked
        updatePasssordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = newPassword.getText().toString(); //new password is obtained
                user.updatePassword(password) //password of the user is updated
                        .addOnSuccessListener(new OnSuccessListener<Void>() {//checks whether the process happens successfully
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this,"Password Updated", Toast.LENGTH_SHORT).show(); //following text appears if password updated successfully
                                dialog.cancel();//alert dialog is exited


                                FirebaseAuth.getInstance().signOut();//user is signed out for security purposes
                                startActivity(new Intent(getApplicationContext(), Login.class));//login page opened
                                Toast.makeText(Profile.this,"Please Re-login with new Password", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this,e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });





    }




}