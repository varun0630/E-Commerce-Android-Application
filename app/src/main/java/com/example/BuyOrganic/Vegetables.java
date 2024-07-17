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

public class Vegetables extends AppCompatActivity {


    String[][] itemDetails = new String[6][5]; //2D arrays initialized with 6 rows, 5 columns
    FirebaseFirestore db;
    String email;
    List<String> quantity = new ArrayList<>();//String ArrayList initialized
    List<String> cost = new ArrayList<>();//String ArrayList initialized
    RecyclerView recyclerView;
    int count = 0;
    String type = "Vegetable";
    List<String> itemNames= new ArrayList<>();

    //1D arrays which contains various images required in the Vegetables page
    int[] itemsPageImages = {0,0,R.drawable.broccoliitem,R.drawable.cucumberitem,0,R.drawable.tomatoeitem};
    int[] itemImages = {0,0,R.drawable.broccoliimage,R.drawable.cucumberimage,0,R.drawable.tomatoeimage};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetables);

        db = FirebaseFirestore.getInstance();

        //gets the email of the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        itemNames.add("Apple");
        itemNames.add("Banana");
        itemNames.add("Broccoli");
        itemNames.add("Cucumber");
        itemNames.add("Orange");
        itemNames.add("Tomatoes");



        setVegetablesPage();



    }

    public void setVegetablesPage() {

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
                                    sorting(); //sorts the Vegetables in order of their average rating
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
        AdapterItems myAdapter = new AdapterItems(itemsPageImages,itemImages,this, itemDetails, quantity, cost, email);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Vegetables.this));
    }

    //use of selection sort to sort the vegetables in order of average rating
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




    //swaps 2 elements in an integer array
    public void swapIntArray(int[] A,int i, int max) {
        int temp = A[max];
        A[max] = A[i];
        A[i] = temp;
    }

    // swaps 2 elements in a String ArrayList
    public void swapStringList(List<String> A, int i, int max) {
        String temp = A.get(max);
        A.set(max,A.get(i));
        A.set(i,temp);


    }

    //swaps 2 rows of a 2D array
    public void swapRows(String[][] A , int row1, int row2) {
        for (int j=0; j<5;j++) {
            String temp = A[row1][j];
            A[row1][j]= A[row2][j];
            A[row2][j] = temp;
        }

    }


    //method to redirect user to Fruits page
    public void fruitsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Fruits.class));
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


}