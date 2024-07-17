package com.example.BuyOrganic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.MyViewHolder> {

    int[] cartPageImages;
    Context context;
    String[][] fruit_Details;
    List<String> quantity;
    List<String> cost;
    FirebaseFirestore db;
    String email;




    //constructor method to initialize respective variables
    public AdapterCart(int[] cartPageImages,Context context, String[][] fruit_Details, List<String> quantity, List<String> cost, String email) {
        this.cartPageImages = cartPageImages;
        this.context = context;
        this.fruit_Details = fruit_Details;
        this.quantity = quantity;
        this.cost = cost;
        this.email = email;

    }

    @NonNull
    @Override
    public AdapterCart.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_design,parent,false);//adapter utilizes cart_designt layout
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.MyViewHolder holder, int position) {


        //sets the items in the cart if their quantity is not 0

        if (!quantity.get(position).equals("0")) {

            holder.itemBox.setImageResource(cartPageImages[position]);
            holder.plusBtn.setImageResource(R.drawable.plus);
            holder.minusBtn.setImageResource(R.drawable.minus);
            holder.quantityTxt.setText(quantity.get(position));
            holder.priceTxt.setText(cost.get(position));
        }

        else {
            holder.itemBox.setVisibility(View.GONE);

        }

        db = FirebaseFirestore.getInstance();



        // checks whether plus button is clicked
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(holder,position);

            }
        });


        //checks whether minus button is clicked
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(holder,position);
            }
        });






    }

    //increases the item's quantity
    public void increaseQuantity(AdapterCart.MyViewHolder holder, int position) {

        int item_Availability = Integer.parseInt(fruit_Details[position][2]);
        int Quantity = Integer.parseInt(quantity.get(position));

        if(item_Availability > 0 && Quantity < item_Availability) {
            int total_Item_Cost = Integer.parseInt(cost.get(position));
            total_Item_Cost = total_Item_Cost+Integer.parseInt(fruit_Details[position][1]);
            Quantity = Quantity + 1;
            holder.quantityTxt.setText(String.valueOf(Quantity));
            holder.priceTxt.setText(String.valueOf(total_Item_Cost));
            quantity.set(position,String.valueOf(Quantity));
            cost.set(position,String.valueOf(total_Item_Cost));


            db.collection("Cart").document(email)
                    .update("Quantity", quantity, "Cost", cost)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                }
            });

        }

        else {
            Toast.makeText(context, "No more Available", Toast.LENGTH_SHORT).show();
        }
    }



    //decreases the item's quantity
    public void decreaseQuantity(AdapterCart.MyViewHolder holder, int position) {
        int Quantity = Integer.parseInt(quantity.get(position));

        if(Quantity>0) {

            if (Quantity==1) {
                holder.itemBox.setVisibility(View.GONE);

                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_cart,null);
                ImageView checkout_Btn = view.findViewById(R.id.checkoutBtn);
                checkout_Btn.setVisibility(View.GONE);
            }

            int total_Item_Cost = Integer.parseInt(cost.get(position));
            total_Item_Cost = total_Item_Cost-Integer.parseInt(fruit_Details[position][1]);
            Quantity = Quantity - 1;
            holder.quantityTxt.setText(String.valueOf(Quantity));
            holder.priceTxt.setText(String.valueOf(total_Item_Cost));
            quantity.set(position,String.valueOf(Quantity));
            cost.set(position,String.valueOf(total_Item_Cost));


            db.collection("Cart").document(email)
                    .update("Quantity", quantity, "Cost", cost)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return cartPageImages.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView itemBox,plusBtn, minusBtn;
        TextView quantityTxt, priceTxt;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBox = itemView.findViewById(R.id.imageView1);
            plusBtn = itemView.findViewById(R.id.imageView2);
            minusBtn = itemView.findViewById(R.id.imageView3);
            quantityTxt = itemView.findViewById(R.id.textView1);
            priceTxt = itemView.findViewById(R.id.textView2);
        }
    }
}


