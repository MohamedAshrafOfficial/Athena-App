package emad.athena;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileImageActivity extends AppCompatActivity {

    TextView helloName;
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    int SELECT_IMAGE_PROFILE = 202;
    Bitmap bitmap;

    FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private StorageReference storageRef;

    private static final String TAG = "ProfileImageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        initViews();


    }

    public void initViews(){
        helloName = findViewById(R.id.helloName);
        profile_image = findViewById(R.id.profile_image);
        helloName.setText("Hi There");
    }
    public void selectProfileImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,SELECT_IMAGE_PROFILE);
    }

    public void updateInfo(View view) {
        addImageToFirebase();
    }

    public void skip(View view) {
        startActivity(new Intent(ProfileImageActivity.this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == SELECT_IMAGE_PROFILE){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                    profile_image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addImageToFirebase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image.... \n it depends on your internet connection");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        storageRef = FirebaseStorage.getInstance().getReference().child("users");
        final UploadTask uploadTask = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: FAILED ************************************" );
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.hide();
                Log.d(TAG, "onSuccess: SUCCESS *****************************");
                storageRef = FirebaseStorage.getInstance().getReference().child("users/").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageURL = uri.toString();
                        Log.d(TAG, "onSuccess: " + imageURL);
                        // update profile image to this user
                        usersReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pictureURL").setValue(imageURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: url updated");
                                startActivity(new Intent(ProfileImageActivity.this, MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.hide();
                                Log.d(TAG, "onFailure: failed update url");
                            }
                        });
                    }
                });
            }
        });


    }

}
