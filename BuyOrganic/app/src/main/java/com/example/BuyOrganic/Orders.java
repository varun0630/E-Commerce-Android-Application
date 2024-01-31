package com.example.BuyOrganic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Orders extends AppCompatActivity {

    List<String> orderIDs = new ArrayList<>();
    List<String> totalCosts = new ArrayList<>();
    List<String> status = new ArrayList<>();
    List<String> itemPrices = new ArrayList<>();
    int count;


    FirebaseFirestore db;
    String email;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail(); //gets current users email
        }
        db = FirebaseFirestore.getInstance();



        setOrdersPage();



    }

    public void setOrdersPage() {

        //retrieves the order details from the Orders collection
        db.collection("Orders")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) { // all documents in the collection are looped through
                        if (document.getString("Email").equals(email)) {

                            orderIDs.add(document.getId());
                            totalCosts.add(document.getString("Total Cost"));
                            status.add(document.getString("Status"));
                            //respective fields are added to the ArraysLists
                        }
                    }
                    //Item prices are obtained from Items collection
                    db.collection("Items")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document: task.getResult()) {

                                    itemPrices.add(document.getString("Price"));
                                }

                                if (!orderIDs.isEmpty()) {//checks if there are any orders for the user
                                    setRecyclerView(); //calls the setRecyclerView method only if user has orders
                                }


                            }


                        }
                    });


                }

            }
        });

    }




    //method which directes user to accounts page
    public void accountsPage(View v) {

        startActivity(new Intent(getApplicationContext(), Accounts.class));
        finish();

    }

    //sets the recycler view for the Orders page
    public void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        AdapterOrders myAdapter = new AdapterOrders(orderIDs,totalCosts,status,Orders.this,itemPrices);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Orders.this));
    }




}