package com.example.BuyOrganic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterCheckout extends RecyclerView.Adapter<AdapterCheckout.MyViewHolder> {

    int image[];
    Context context;
    List<String> quantity = new ArrayList<>();
    List<String> cost = new ArrayList<>();
    FirebaseFirestore db;
    String email;


    //constructor method to initlialize variables
    public AdapterCheckout(int[] image, Context context, List<String> quantity, List<String> cost) {
        this.image = image;
        this.context = context;
        this.quantity = quantity;
        this.cost = cost;

    }

    @NonNull
    @Override
    public AdapterCheckout.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.checkout_design,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCheckout.MyViewHolder holder, int position) {


        //sets the different widgets in the holder to the appropriate
        holder.a.setImageResource(image[position]);
        holder.quantity.setText(quantity.get(position));
        holder.price_Total.setText("Rs "+ cost.get(position));

        if (quantity.get(position).equals("0")) {
            holder.a.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return image.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView a;
        TextView quantity, price_Total;

        ConstraintLayout constraintLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            a = itemView.findViewById(R.id.image);
            quantity= itemView.findViewById(R.id.quantity);
            price_Total = itemView.findViewById(R.id.price_Total);


        }
    }
}


