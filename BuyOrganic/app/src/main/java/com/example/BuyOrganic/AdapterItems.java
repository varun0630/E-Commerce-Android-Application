package com.example.BuyOrganic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.MyViewHolder> {

    int[] itemsPageImages;
    int[] itemsImages;
    Context context;
    String[][] itemDetails;
    List<String> quantity;
    List<String> cost;
    int rating = 0;
    String email;


    ImageView star1,star2,star3,star4,star5, rateBtn, infoImage, backBtn, ratingStarImg;
    Button itemPrice;
    TextView avgRating, noRatingTxt;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public AdapterItems(int[] itemsPageImages, int[] itemsImages, Context context, String[][] itemDetails, List<String> quantity,List<String> cost, String email) {
        this.itemsPageImages = itemsPageImages;
        this.itemsImages = itemsImages;
        this.context = context;
        this.itemDetails = itemDetails;
        this.quantity = quantity;
        this.email = email;
        this.cost = cost;
    }

    @NonNull
    @Override
    public AdapterItems.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fruits_design,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override



    public void onBindViewHolder(@NonNull AdapterItems.MyViewHolder holder, int position) {
        //sets the various images in the design to respective values based on position
        holder.itemImage.setImageResource(itemsPageImages[position]);
        holder.plusBtn.setImageResource(R.drawable.plus);
        holder.minusBtn.setImageResource(R.drawable.minus);
        holder.quantityTxt.setText(quantity.get(position));
        holder.infoBtn.setImageResource(R.drawable.info);
        holder.addCartBtn.setImageResource(R.drawable.addtocartbtn);
        holder.itemAvailabilityTxt.setText("Availability: "+itemDetails[position][2]);


        //if quantity is 0, addCartBtn becomes GONE and plus,minus buttons appear
        if (!quantity.get(position).equals("0")) {
            holder.addCartBtn.setVisibility(View.GONE);
            holder.itemButtons.setVisibility(View.VISIBLE);
        }

        //when the array contains a 0 value, it is set to GONE. This is useful for the Fruits and Vegetables pages since not all items are present
        if (itemsPageImages[position]==0) {
            holder.itemImage.setVisibility(View.GONE);
            holder.addCartBtn.setVisibility(View.GONE);
            holder.itemButtons.setVisibility(View.GONE);
        }




        //checks if into button is clicked
        holder.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int test = holder.getAdapterPosition();

                //openInfo method is called
                openInfo(test);
            }
        });

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(holder,position);
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(holder,position);
            }
        });

        holder.addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(holder,position);
            }
        });

    }

    //method to increase item's quantity
    public void increaseQuantity(AdapterItems.MyViewHolder holder, int position) {
        int itemAvailability = Integer.parseInt(itemDetails[position][2]); //availability obtained
        int itemQuantity = Integer.parseInt(quantity.get(position)); //quantity obtained
        if(itemQuantity < itemAvailability) { //checks whether quantity less than availability
            itemQuantity = itemQuantity + 1;//quantity incremented
            holder.quantityTxt.setText(String.valueOf(itemQuantity));
            quantity.set(position,String.valueOf(itemQuantity));
            int totalItemCost = Integer.parseInt(cost.get(position));
            totalItemCost = totalItemCost+Integer.parseInt(itemDetails[position][1]);
            cost.set(position,String.valueOf(totalItemCost));
            //new values set to TextViews and ArrayLists


            //Cart collection updated with changes in the user cart
            db.collection("Cart").document(email)
                    .update("Quantity", quantity, "Cost",cost)
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

    //decreaes the quantity in the cart
    public void decreaseQuantity(AdapterItems.MyViewHolder holder, int position) {
        int Quantity = Integer.parseInt(quantity.get(position));

        if(Quantity>0) {//checks whether quantity is greater than 0
            if (Quantity==1) {//if quantity equals one and minus button clicked, add to cart button reappears
                holder.itemButtons.setVisibility(View.GONE);
                holder.addCartBtn.setVisibility(View.VISIBLE);
            }


            Quantity = Quantity - 1;//quantity decremented
            holder.quantityTxt.setText(String.valueOf(Quantity));
            quantity.set(position,String.valueOf(Quantity));

            int total_Item_Cost = Integer.parseInt(cost.get(position));
            total_Item_Cost = total_Item_Cost-Integer.parseInt(itemDetails[position][1]);
            cost.set(position,String.valueOf(total_Item_Cost));

            //appropriate values updated

            db.collection("Cart").document(email) //database updated
                    .update("Quantity", quantity,"Cost",cost)
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


    public void addToCart(AdapterItems.MyViewHolder holder, int position) {
        String availability = itemDetails[position][2];
        if (!availability.equals("0")) {
            holder.addCartBtn.setVisibility(View.GONE);
            holder.itemButtons.setVisibility(View.VISIBLE);
            increaseQuantity(holder, position);
        }
        else {
            Toast.makeText(context, "Item Out of Stock", Toast.LENGTH_SHORT).show();
        }


    }

    @SuppressLint("SetTextI18n")
    private void openInfo(int position) {

        //An alert dialog builder is being used to create an Alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View view = inflater.inflate(R.layout.item_information_dialog_design,null);//alert dialog is linked to the design of a particular page


        //respective widgets are obtained from xml files to perform operations on
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
        backBtn = view.findViewById(R.id.backBtn);

        itemPrice = view.findViewById(R.id.itemPrice);
        rateBtn = view.findViewById(R.id.rateBtn);
        avgRating = view.findViewById(R.id.avgRating);
        noRatingTxt = view.findViewById(R.id.noRatingText);
        ratingStarImg = view.findViewById(R.id.ratingStarImg);

        if (itemDetails[position][4].equals("0")) {
            avgRating.setVisibility(View.INVISIBLE);
            ratingStarImg.setVisibility(View.INVISIBLE);
            noRatingTxt.setVisibility(View.VISIBLE);
        }

        //Obtains average rating from 2D array, rounds it to 1 dp nd sets it to the TextView in the oage
        else {

            double A = (double) Math.round(Double.parseDouble(itemDetails[position][3])*10)/10;
            avgRating.setText(String.valueOf(A));

        }

        infoImage = view.findViewById(R.id.infoImage);
        infoImage.setImageResource(itemsImages[position]);
        String price_Txt_Input = "Rs "+itemDetails[position][1];
        itemPrice.setText(price_Txt_Input);

        //checks whether each star is clicked and its image is changed from shaded to unshaded or vice versa depending on current state
        star1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (star1.getDrawable().getConstantState()==
                        ((Activity)context).getResources().getDrawable(R.drawable.starunshaded).getConstantState()) {
                    star1.setImageResource(R.drawable.star);
                    rating = 1; //Since first star is clicked, rating is set to 1
                }
                else {
                    star1.setImageResource(R.drawable.starunshaded);
                    rating=0;
                }
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (star2.getDrawable().getConstantState()==
                        ((Activity)context).getResources().getDrawable(R.drawable.starunshaded).getConstantState()) {
                    star2.setImageResource(R.drawable.star);
                    rating = 2; //since second star is clicked rating is set to 2
                }
                else {
                    star2.setImageResource(R.drawable.starunshaded);
                    rating=1;
                }
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (star3.getDrawable().getConstantState()== ((Activity)context).getResources().getDrawable(R.drawable.starunshaded).getConstantState()) {
                    star3.setImageResource(R.drawable.star);
                    rating = 3;//since 3rd star is clicked, rating is set to 3
                }
                else {
                    star3.setImageResource(R.drawable.starunshaded);
                    rating=2;
                }
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (star4.getDrawable().getConstantState()== ((Activity)context).getResources().getDrawable(R.drawable.starunshaded).getConstantState()) {
                    star4.setImageResource(R.drawable.star);
                    rating = 4;
                }
                else {
                    star4.setImageResource(R.drawable.starunshaded);
                    rating=3;
                }
            }
        });

        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (star5.getDrawable().getConstantState()== ((Activity)context).getResources().getDrawable(R.drawable.starunshaded).getConstantState()) {
                    star5.setImageResource(R.drawable.star);
                    rating = 5;
                }
                else {
                    star5.setImageResource(R.drawable.starunshaded);
                    rating=4;
                }
            }
        });

        //checks whether rate button is clicked
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //values obtained from 2D array
                String numberOfRatings = itemDetails[position][4];
                String oldAvgRating = itemDetails[position][3];
                double newAvgRating = (Double.parseDouble(oldAvgRating)*Double.parseDouble(numberOfRatings) + rating)/(Double.parseDouble(numberOfRatings)+1.0); //new average rating obtained
                String newNumberOfRatings = String.valueOf(Integer.parseInt(numberOfRatings)+1); //number of rater incremented
                itemDetails[position][3] = String.valueOf(newAvgRating);
                itemDetails[position][4] = newNumberOfRatings;
                //itemDetails array updated with new values

                String itemName = itemDetails[position][0]; //item name found

                //database updated with new values of average rating and number of raters.
                db.collection("Items").document(itemName)
                        .update("Average Rating",String.valueOf(newAvgRating), "Number of Ratings",newNumberOfRatings)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Your Rating Has Been Registered", Toast.LENGTH_SHORT).show();
                                star1.setImageResource(R.drawable.starunshaded);
                                star2.setImageResource(R.drawable.starunshaded);
                                star3.setImageResource(R.drawable.starunshaded);
                                star4.setImageResource(R.drawable.starunshaded);
                                star5.setImageResource(R.drawable.starunshaded);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Your Rating Could Not Be Registered. Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });






        alert.setView(view);
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();//alert dialog closes when back button is pressed
            }
        });











    }

    @Override
    public int getItemCount() {

        //the number of elements in the recyclerview is the length of the itemsPageImages array
        return itemsPageImages.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage,plusBtn, minusBtn, infoBtn, addCartBtn;
        TextView quantityTxt, itemAvailabilityTxt;
        LinearLayout itemButtons;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //following are obtained from xml file to perform operations one
            itemImage = itemView.findViewById(R.id.imageView1);
            plusBtn = itemView.findViewById(R.id.imageView2);
            minusBtn = itemView.findViewById(R.id.imageView3);
            infoBtn = itemView.findViewById(R.id.imageView4);
            quantityTxt = itemView.findViewById(R.id.textView1);
            addCartBtn = itemView.findViewById(R.id.add_Cart_Btn);
            itemButtons = itemView.findViewById(R.id.item_Buttons);
            itemAvailabilityTxt = itemView.findViewById(R.id.item_Availability);

        }
    }
}


