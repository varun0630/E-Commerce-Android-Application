package com.example.BuyOrganic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Checkout extends AppCompatActivity {


    int[] checkoutPageImages={R.drawable.appleordersummary,R.drawable.bananaordersummary,R.drawable.broccoliordersummary,R.drawable.cucumberordersummary,R.drawable.orangeordersummary,R.drawable.tomatoesordersummary};
    List<String> quantity = new ArrayList<>();
    List<String> cost = new ArrayList<>();
    String[][] itemDetails = new String[6][6];
    List<String> itemNames = new ArrayList<>();
    String email;
    FirebaseFirestore db;
    int count;
    String communityName;
    String villaNo;
    int totalCost;
    String personName;


    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();


        //retrieves appropriate values from carts and Items collection
        db.collection("Cart").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            quantity = (List<String>) document.get("Quantity");
                            cost = (List<String>) document.get("Cost");
                            itemNames = (List<String>) document.get("Item Name");

                            TextView total_Cost_Txt = findViewById(R.id.totalCostTxt);

                            int totalCost =0;
                            for (int x=0;x<cost.size();x++) {

                                totalCost+= Integer.parseInt(cost.get(x));
                            }

                            total_Cost_Txt.setText(String.valueOf("Rs "+totalCost));



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
                                    sorting();
                                    setRecyclerView(); //sets the recycler view for the checkout page

                                }

                            }
                        });

                    }
                });

        //retrieves user's details so that the user can be linked to the particular order
        db.collection("User Details").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            communityName = document.get("Community Name").toString();
                            villaNo = document.get("Villa No").toString();
                            personName = document.getString("Name");

                            TextView community_Name = findViewById(R.id.communityName);
                            community_Name.setText(communityName);

                            TextView villa_No = findViewById(R.id.villaNo);
                            villa_No.setText(villaNo);
                        }
                    }
                });



    }

    public void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        AdapterCheckout myAdapter = new AdapterCheckout(checkoutPageImages,this, quantity,cost);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Checkout.this));
    }

    public void visibility() {

        for (int x = 0; x < 6; x++) {

            if (quantity.get(x).equals("0")) {
                checkoutPageImages[x] = 0;
            }


        }


    }

    //following method executes when the confirm order button is clicked
    public void confirmOrder(View v) {

        //finds total cost of order by looping through cost ArrayList
        totalCost=0;
        for(String x :cost) {
            totalCost = totalCost + Integer.parseInt(x);
        }

        email();

        //creates a new HashMap with order details
        Map<String,Object> orderDetails = new HashMap<>();
        orderDetails.put("Email",email);
        orderDetails.put("Item Name",itemNames);
        orderDetails.put("Item Cost", cost);
        orderDetails.put("Total Cost",String.valueOf(totalCost));
        orderDetails.put("Quantity",quantity);
        orderDetails.put("Community Name",communityName);
        orderDetails.put("Villa No.",villaNo);
        orderDetails.put("Status", "Pending");

        //hashmap added to Order collection. Document ID is autogenerated
        db.collection("Orders")
                .add(orderDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Checkout.this, "Your Order Has Been Placed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Home.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Checkout.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        //For loop is used to loop through the quantity array
        for (int x = 0; x < quantity.size(); x++) {
            if (!quantity.get(x).equals("0")) {
                int finalX = x;
                db.collection("Items").document(itemNames.get(x)) //details of items are changed
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            int availability = Integer.parseInt(document.getString("Availability")); //old availability of item found
                            int numSold = Integer.parseInt(document.getString("Number Sold")); //old number sold found
                            numSold = numSold+Integer.parseInt(quantity.get(finalX)); //availability of item changed
                            availability = availability-Integer.parseInt(quantity.get(finalX)); // number sold of item changed
                            db.collection("Items").document(itemNames.get(finalX)) //database updated with new values
                                    .update("Availability", String.valueOf(availability),"Number Sold", String.valueOf(numSold));

                            quantity.set(finalX,"0");
                            cost.set(finalX,"0");
                            //User's cart is resest by making quantity and cost as 0
                        }
                        //cart is then updated
                        db.collection("Cart").document(email)
                                .update("Quantity",quantity,"Cost",cost);

                    }
                });
            }
        }
    }

    public void cartPage(View v) {

        startActivity(new Intent(getApplicationContext(), Cart.class));
        finish();


    }

    //email sent to client
    public void email() {

        int count =0;

        //sender's email details
        String sEmail = "buyorganic.client@gmail.com";
        String sPassword = "client123";
        String emailBody ="This mail is being sent to inform you that a new order has been placed. Below are the details of the order\n\nItems: ";

        for (int x=0;x<quantity.size();x++) {

            if (!quantity.get(x).equals("0")) {

                if (count==0) {

                    emailBody = emailBody + itemNames.get(x);
                }
                else {
                    emailBody = emailBody+", "+ itemNames.get(x);

                }
            }
            count+=1;

        }
        count=0;

        //email body
        emailBody = emailBody+"\nQuantity: ";

        for (int x=0;x<quantity.size();x++) {

            if (!quantity.get(x).equals("0")) {

                if (count==0) {

                    emailBody = emailBody + quantity.get(x);
                }
                else {
                    emailBody = emailBody+", "+ quantity.get(x);

                }
            }
            count+=1;

        }
        count=0;

        emailBody = emailBody+"\nIndividual Costs: ";

        for (int x=0;x<quantity.size();x++) {

            if (!quantity.get(x).equals("0")) {

                if (count==0) {

                    emailBody = emailBody + cost.get(x);
                }
                else {
                    emailBody = emailBody+", "+ cost.get(x);

                }
            }
            count+=1;

        }

        emailBody = emailBody+"\nTotal Cost: "+totalCost+" Rs";

        emailBody = emailBody+"\n\nName of Person: "+personName;

        emailBody = emailBody+"\n\nCommunity Name: "+communityName;

        emailBody = emailBody+"\n\nVilla No: "+villaNo;





        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail,sPassword);
            }
        });



        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sEmail));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("buyorganic.client@gmail.com"));

            message.setSubject("New Order Placed");

            message.setText(emailBody);

            new SendMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }



    }


    private class SendMail extends AsyncTask<Message, String, String> {



        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Sucess";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }

        }
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
                swapIntArray(checkoutPageImages,i,maxIndex);
                swapRows(itemDetails,i,maxIndex);

            }
        }
        db.collection("Cart").document(email)
                .update("Item Name", itemNames, "Cost",cost,"Quantity",quantity);



    }



    public void swapIntArray(int[] A,int i, int min) {
        int temp = A[min];
        A[min] = A[i];
        A[i] = temp;
    }

    public void swapRows(String[][] A , int row1, int row2) {
        for (int j=0; j<5;j++) {
            String temp = A[row1][j];
            A[row1][j]= A[row2][j];
            A[row2][j] = temp;
        }

    }

}
