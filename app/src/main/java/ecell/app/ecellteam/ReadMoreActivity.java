package ecell.app.ecellteam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMoreActivity extends AppCompatActivity {

    ImageView imageView;
    TextView titleView;
    TextView descView;
    String imageUrl;
    String title;
    String desc;
    File localFile = null;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more);

        getPermissions();


        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Poster poster = (Poster) intent.getSerializableExtra("data");

        title = poster.getTitle();
        imageUrl = poster.getImageUrl();
        desc = poster.getMessage();

        imageView = findViewById(R.id.readMoreImage);
        titleView = findViewById(R.id.readMoreTitle);
        descView = findViewById(R.id.readMoreDesc);

        titleView.setText(title);
        descView.setText(desc+"\n\n\n\n\n\n\n\n\n\n\n\n");
        Glide.with(getApplicationContext()).load(imageUrl).into(imageView);

        MaterialButton button = findViewById(R.id.shareButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog dialog = ProgressDialog.show(ReadMoreActivity.this, "",
                        "Loading. Please wait...", true);

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference httpsReference = storage.getReferenceFromUrl(imageUrl);



                String suffix = imageUrl;
                Pattern pattern = Pattern.compile("%2F(.*?)\\?", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(suffix);
                while (matcher.find()) {
                    //System.out(matcher.group(1));
                    suffix = matcher.group(1);
                }
                suffix = suffix.split("\\.")[1];
                //Log.i("akldfadsf", suffix);


                localFile = new File(getExternalFilesDir(null), "yoo."+suffix);

                final String finalSuffix = suffix;
                httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        // Local temp file has been created

                        //Uri uri = Uri.fromFile(localFile);

                        Uri imageUri = FileProvider.getUriForFile(ReadMoreActivity.this,
                                ReadMoreActivity.this.getApplicationContext().getPackageName()+".provider", localFile);

                        //Log.i(";ajldfajd", imageUri.toString());
                        dialog.dismiss();


                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(Intent.EXTRA_TEXT, desc);
                        startActivity(Intent.createChooser(intent, "Share Poster"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            mAuth.signInWithEmailAndPassword("demo@gmail.com", "pra@123");
        }
    }



    public void shareImage(View view) throws IOException {



//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//        sendIntent.setType("text/plain");
//
//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        startActivity(shareIntent);

    }




    private  void  getPermissions(){
        String externalReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE.toString();
        String externalWritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE.toString();

        if((ContextCompat.checkSelfPermission(this, externalReadPermission) != PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, externalWritePermission) != PackageManager.PERMISSION_GRANTED)){

            if (Build.VERSION.SDK_INT >= 23) {

                requestPermissions(new String[]{externalReadPermission, externalWritePermission}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101 && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please allow permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
