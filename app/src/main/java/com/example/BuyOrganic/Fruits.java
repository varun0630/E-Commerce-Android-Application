package com.example.BuyOrganic;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fruits extends AppCompatActivity  {

    String[][] itemDetails = new String[6][5];
    FirebaseFirestore db;
    String email;
    List<String> quantity = new ArrayList<>();
    List<String> cost = new ArrayList<>();
    RecyclerView recyclerView;
    int count = 0;
    List<String> itemNames= new ArrayList<>();


    //1D arrays which contains various images required in the Fruits page
    int[] itemsPageImages = {R.drawable.appleitem,R.drawable.bananaitem,0,0,R.drawable.orangeitem,0};
    int[] itemImages = {R.drawable.appleimage, R.drawable.bananaimage,0,0,R.drawable.orangeimage,0};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruits);


        itemNames.add("Apple");
        itemNames.add("Banana");
        itemNames.add("Broccoli");
        itemNames.add("Cucumber");
        itemNames.add("Orange");
        itemNames.add("Tomatoes");


        db = FirebaseFirestore.getInstance();
        //gets the email of the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        setFruitsPage();



    }

    public void setFruitsPage() {

        //accesses the user's cart from Cart collection
        db.collection("Cart").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            quantity = (List<String>) document.get("Quantity");
                            cost = (List<String>) document.get("Cost");

                        }
                        //accesses the Items collection
                        db.collection("Items")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document: task.getResult()) {
                                        itemDetails[count][0] = document.getString("Name");
                                        itemDetails[count][1] = document.getString("Price");
                                        itemDetails[count][2] = document.getString("Availability");
                                        itemDetails[count][3] = document.getString("Average Rating");
                                        itemDetails[count][4] = document.getString("Number of Ratings");


                                        count+=1;



                                    }
                                    sorting(); //sorts the items in order of their average rating
                                    setRecyclerView(); //sets the recycler view



                                }


                            }
                        });



                    }
                });
    }


    //method to set the RecyclerView for Items page
    public void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        AdapterItems myAdapter = new AdapterItems(itemsPageImages,itemImages,this,itemDetails,quantity,cost, email);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Fruits.this));
    }



    //method to redirect user to Vegetables page
    public void vegetablesPage(View v) {

        startActivity(new Intent(getApplicationContext(), Vegetables.class));
        finish();


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



    public void sorting() {
        for (int i=0;i<5;i++) {
            int maxIndex = i;
            for (int j=i+1; j<6;j++) {


                if (Double.parseDouble(itemDetails[j][3])>Double.parseDouble(itemDetails[maxIndex][3])) {
                    maxIndex = j;
                }
            }

            if (maxIndex!=i) {
                swapIntArray(itemsPageImages,i,maxIndex);
                swapIntArray(itemImages,i,maxIndex);
                swapStringList(itemNames,i,maxIndex);
                swapRows(itemDetails,i,maxIndex);

            }
        }


        db.collection("Cart").document(email)
                .update("Item Name", itemNames, "Cost",cost,"Quantity",quantity);



    }





    public void swapIntArray(int[] A,int i, int max) {
        int temp = A[max];
        A[max] = A[i];
        A[i] = temp;
    }


    public void swapStringList(List<String> A, int i, int max) {
        String temp = A.get(max);
        A.set(max,A.get(i));
        A.set(i,temp);


    }

    public void swapRows(String[][] A , int row1, int row2) {
        for (int j=0; j<5;j++) {
            String temp = A[row1][j];
            A[row1][j]= A[row2][j];
            A[row2][j] = temp;
        }

    }



}

