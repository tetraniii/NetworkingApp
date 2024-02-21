package com.example.networkingapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileEditActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    String imageURL;
    ImageButton closeBtn, saveBtn;
    TextInputEditText editTextName, editTextDesc, linksNbrTI, linksNbrAdtnlTI, linksWebsiteTI, linksEmailTI, editWebsiteLink,
    editPhoneNum;
    MaterialButton btnEditUserPic, btnAddLink;
    ImageView userPicIv;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        closeBtn = findViewById(R.id.btnCloseProfileEdit);
        saveBtn = findViewById(R.id.btnSaveProfileEdit);
        editTextName = findViewById(R.id.editTextName);
        editTextDesc = findViewById(R.id.editTextDesc);
        editWebsiteLink = findViewById(R.id.editWebsiteLink);
        editPhoneNum = findViewById(R.id.editPhoneNum);
        /*
        linksNbrTI = findViewById(R.id.linksNbrTI);
        linksNbrAdtnlTI = findViewById(R.id.linksNbrAdtnlTI);
        linksWebsiteTI = findViewById(R.id.linksWebsiteTI);
        linksEmailTI = findViewById(R.id.linksEmailTI);*/
        btnEditUserPic = findViewById(R.id.btnEditUserPic);
        userPicIv = findViewById(R.id.userPicIv);
        btnAddLink = findViewById(R.id.btnAddContact);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        Query query = reference.orderByChild("id").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    editTextName.setText("" + ds.child("name").getValue());
                    editTextDesc.setText("" + ds.child("description").getValue());
                    String imageURL = ""+ ds.child("image").getValue();
                    //linksNbrTI.setText(""+ ds.child("number").getValue());
                    //linksNbrAdtnlTI.setText(""+ ds.child("additionalNumber").getValue());
                    //linksWebsiteTI.setText(""+ ds.child("websiteLink").getValue());
                    //linksEmailTI.setText(""+ ds.child("contactEmail").getValue());

                    try {
                        Picasso.get().load(imageURL).into(userPicIv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.user_picture_default).into(userPicIv);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileEditActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            userPicIv.setImageURI(uri);
                        } else {
                            Toast.makeText(ProfileEditActivity.this, "Не выбрано изображение", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnEditUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
                    builder.setMessage(R.string.changePicDialog)
                            .setPositiveButton(R.string.gotItRu, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                                    photoPicker.setType("image/*");
                                    activityResultLauncher.launch(photoPicker);
                                }
                            });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();

                Intent intent = new Intent(getApplicationContext(), StartupDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileEditActivity.this, StartupDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void saveData() {
        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.saving_layout);
        AlertDialog dialogSave = builder.create();
        dialogSave.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialogSave.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileEditActivity.this, "Не удалось сохранить данные", Toast.LENGTH_SHORT).show();
                dialogSave.dismiss();
            }
        });
    }

    public void uploadData() {

        String name = Objects.requireNonNull(editTextName.getText()).toString().trim();
        String desc = Objects.requireNonNull(editTextDesc.getText()).toString().trim();
        //String number = Objects.requireNonNull(linksNbrTI.getText()).toString().trim();
        //String addNumber = Objects.requireNonNull(linksNbrAdtnlTI.getText()).toString().trim();
        //String website = Objects.requireNonNull(linksWebsiteTI.getText()).toString().trim();
        //String email = Objects.requireNonNull(linksEmailTI.getText()).toString().trim();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        reference.child(user.getUid()).child("name").setValue(name);
        reference.child(user.getUid()).child("description").setValue(desc);
        reference.child(user.getUid()).child("image").setValue(imageURL);
        /*reference.child(user.getUid()).child("number").setValue(number);
        reference.child(user.getUid()).child("additionalNumber").setValue(addNumber);
        reference.child(user.getUid()).child("websiteLink").setValue(website);
        reference.child(user.getUid()).child("contactEmail").setValue(email);*/
    }
}