package com.example.BuyOrganic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.MyViewHolder> {

    List<String> orderIDs = new ArrayList<>();
    List<String> totalCosts = new ArrayList<>();
    List<String> status = new ArrayList<>();
    List<String> itemPrices = new ArrayList<>();
    Context context;
    FirebaseFirestore db;

    int[] ordersPageImages = new int[6];
    List<String> quantity = new ArrayList<>();
    List<String> item_Names = new ArrayList<>();
    List<String> item_Cost = new ArrayList<>();

    public AdapterOrders(List<String> orderIDs, List<String> totalCosts, List<String> status, Context context,  List<String> itemPrices) {
        this.orderIDs = orderIDs;
        this.totalCosts = totalCosts;
        this.status = status;
        this.context = context;
        this.itemPrices = itemPrices;

    }

    @NonNull
    @Override
    public AdapterOrders.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.orders_design,parent,false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrders.MyViewHolder holder, int position) {


        holder.orderID.setText(orderIDs.get(position));
        holder.totalCost.setText(totalCosts.get(position));
        holder.status.setText(status.get(position));

        holder.orderDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrderDetails(position);
            }
        });


    }


    //opens an alert dialog when the details button is clicked
    private void openOrderDetails(int position) {

        db=FirebaseFirestore.getInstance();

        //retrieves data from Orders collection
        db.collection("Orders")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if (document.getId().equals(orderIDs.get(position))) {
                            quantity = (List<String>) document.get("Quantity");
                            item_Names = (List<String>) document.get("Item Name");
                            item_Cost = (List<String>) document.get("Item Cost");
                            break;

                        }



                    }
                    setImages();



                    //creates an Alert Dialog
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    View view = inflater.inflate(R.layout.order_details_design,null); //alert dialog linked to order_details_design


                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView); //reuses the recyclerview of the checkout page
                    AdapterCheckout myAdapter = new AdapterCheckout(ordersPageImages,context, quantity,item_Cost);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    alert.setView(view);
                    alert.setCancelable(false);

                    AlertDialog dialog = alert.create();

                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();

                    ImageView back_Btn = view.findViewById(R.id.back_Btn);
                    back_Btn.setOnClickListener(new View.OnClickListener()

                    {
                        @Override
                        public void onClick(View v) {

                            dialog.cancel();

                        }
                    });




                }
            }
        });








    }

    //sets the appropriate value in the ordersPageImages array
    public void setImages() {
        int imageName=0;
        //checks what the name of the item in the particular position is and sets the appropriate image
        for(int x=0;x<6;x++) {
            if(item_Names.get(x).equals("Apple")) {
                imageName = R.drawable.appleordersummary;
            }
            if(item_Names.get(x).equals("Banana")) {
                imageName = R.drawable.bananaordersummary;
            }
            if(item_Names.get(x).equals("Broccoli")) {
                imageName = R.drawable.broccoliordersummary;
            }
            if(item_Names.get(x).equals("Cucumber")) {
                imageName = R.drawable.cucumberordersummary;
            }
            if(item_Names.get(x).equals("Orange")) {
                imageName = R.drawable.orangeordersummary;
            }
            if(item_Names.get(x).equals("Tomaotes")) {
                imageName = R.drawable.tomatoesordersummary;
            }

            ordersPageImages[x] = imageName;
        }
    }

    @Override
    public int getItemCount() {

        return orderIDs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView totalCost, status, orderID;
        ImageView orderDetailsBtn;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            totalCost = itemView.findViewById(R.id.totalCost);
            status = itemView.findViewById(R.id.status);
            orderID = itemView.findViewById(R.id.orderID);
            orderDetailsBtn = itemView.findViewById(R.id.orderDetailsBtn);
        }
    }

}


