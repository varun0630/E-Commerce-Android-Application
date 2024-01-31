package com.example.BuyOrganic;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {


    int[] cartPageImages={R.drawable.apple, R.drawable.banana, R.drawable.broccoli,R.drawable.cucumber,R.drawable.orange,R.drawable.tomatoescart};
    List<String> itemNames = new ArrayList<>();

    List<String> quantity = new ArrayList<>();
    List<String> cost = new ArrayList<>();
    String[][] itemDetails = new String[6][6];
    String email;
    FirebaseFirestore db;
    int count=0;
    int cartStatus;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        itemNames.add("Apple");
        itemNames.add("Banana");
        itemNames.add("Broccoli");
        itemNames.add("Cucumber");
        itemNames.add("Orange");
        itemNames.add("Tomatoes");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();




        setCartPage();




    }

    public void setCartPage() {
        //retrieves the appropriate values from the carts collection to set in the carts page for each quantity
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
                        //retrieves appropriate values from Items collection
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
                                    visibility();
                                    sorting();
                                    setRecyclerView();

                                }

                            }
                        });



                    }
                });
    }


    //sets the recycler view for the carts page
    public void setRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);
        AdapterCart myAdapter = new AdapterCart(cartPageImages,Cart.this,itemDetails,quantity,cost,email);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));


    }


    //sets visibility of items with a quantity=0 to gone and hence removes them from cart
    public void visibility() {

        for (int x = 0; x < 5; x++) {

            if (!quantity.get(x).equals("0")) {

                cartStatus=1;
            }


        }

        if (cartStatus==1) {
            findViewById(R.id.checkoutBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyCartText).setVisibility(View.GONE);
        }


    }


    //method to redirect user to Checkout page
    public void checkoutPage(View v) {

        startActivity(new Intent(getApplicationContext(), Checkout.class));
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

    //method to redirect user to Items page
    public void accountsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Accounts.class));
        finish();


    }

    //sorts the items in order of their average rating
    public void sorting() {
        for (int i=0;i<5;i++) {
            int maxIndex = i;
            for (int j=i+1; j<6;j++) {


                if (Double.parseDouble(itemDetails[j][3])>Double.parseDouble(itemDetails[maxIndex][3])) {
                    maxIndex = j;
                }
            }

            if (maxIndex!=i) {
                swapIntArray(cartPageImages,i,maxIndex);
                swapStringList(itemNames,i,maxIndex);
                swapRows(itemDetails,i,maxIndex);

            }
        }
        db.collection("Cart").document(email)
                .update("Item Name", itemNames, "Cost",cost,"Quantity",quantity);



    }



    //swaps 2 elements in an Integer array
    public void swapIntArray(int[] A,int i, int max) {
        int temp = A[max];
        A[max] = A[i];
        A[i] = temp;
    }


    //swaps 2 elements of a String ArrarList
    public void swapStringList(List<String> A, int i, int max) {
        String temp = A.get(max);
        A.set(max,A.get(i));
        A.set(i,temp);


    }

    //swaps 2 rows in a 2D array
    public void swapRows(String[][] A , int row1, int row2) {
        for (int j=0; j<5;j++) {
            String temp = A[row1][j];
            A[row1][j]= A[row2][j];
            A[row2][j] = temp;
        }

    }



}
