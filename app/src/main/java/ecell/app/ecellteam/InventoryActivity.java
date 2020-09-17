package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InventoryActivity extends AppCompatActivity {

    ImageView imageView;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    Query query;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

       // imageView = findViewById(R.id.imageView);


        FirebaseDatabase db = FirebaseDatabase.getInstance();


        databaseReference = db.getReference().child("Posters");
        databaseReference.keepSynced(true);
        query = databaseReference;


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        
        FirebaseRecyclerOptions<Poster> options = new FirebaseRecyclerOptions.Builder<Poster>()
                .setQuery(query, Poster.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Poster, PosterHolder>(options){

            @NonNull
            @Override
            public PosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poster_view, parent, false);

                return new PosterHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final PosterHolder holder, int position, @NonNull final Poster model) {

                holder.setTitle(model.getTitle());
                holder.setDesc(model.getMessage());
                holder.setImage(getApplicationContext(), model.getImageUrl());

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(InventoryActivity.this, ReadMoreActivity.class).putExtra("data", model));
                    }
                });

            }

        };

        adapter.startListening();

        recyclerView.setAdapter(adapter);
    }

    public static  class PosterHolder extends RecyclerView.ViewHolder{

        View mView;
        MaterialButton button;

        public PosterHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            button = mView.findViewById(R.id.readMoreButton);
        }

        public void setTitle(String title){
            TextView card_title = (TextView) mView.findViewById(R.id.card_title);
            card_title.setText(title);
        }

        public void setDesc(String desc){
            TextView card_desc  = (TextView) mView.findViewById(R.id.card_desc);
            if(desc.length() > 100)
                desc = desc.substring(0,100) + ".........";
            card_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){

            //Log.i("Image Utl", image);
            ImageView imageView = (ImageView) mView.findViewById(R.id.card_image);
            Glide.with(ctx).load(image).into(imageView);
        }






    }

}


//test