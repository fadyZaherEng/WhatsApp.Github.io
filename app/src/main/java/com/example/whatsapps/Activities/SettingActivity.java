package com.example.whatsapps.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.whatsapps.MainActivity;
import com.example.whatsapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity
{

    @BindView(R.id.profile_image)
    CircleImageView SettingImage;
    @BindView(R.id.edit_profile)
    EditText Name;
    @BindView(R.id.edit_status)
    EditText Status;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.edit_profileDetails)
    CircleImageView  edit_profileDetails;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    DatabaseReference RootRef;
    StorageReference storageReference;
    String CurrentUserId="";
    private final static int GalleryPick = 1;
    private final static int CAMERA_PERMISION=8;
    @BindView(R.id.settingToolBar)
    androidx.appcompat.widget.Toolbar settingToolBar;
    String profileImage=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeFields();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        edit_profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Name.setEnabled(true);
                Status.setEnabled(true);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateSettingsMethod();
                Name.setEnabled(false);
                Status.setEnabled(false);
            }
        });
        SettingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) Build.VERSION.SDK_INT>=23){
                    if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                    {
                      //     if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                      //  {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISION);
                        //}

                        return;
                    }
                }
                OpenGalleryForSelectImage();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (CAMERA_PERMISION == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            OpenGalleryForSelectImage();
        }
    }

    private void initializeFields()
    {
        ButterKnife.bind(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        RootRef = firebaseDatabase.getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        setSupportActionBar(settingToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setTitle(R.string.setting_account_toolbar);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Name.setEnabled(false);
        Status.setEnabled(false);

        storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileImage = uri.toString();
            }
        });
        RetrieveUserInformation();
    }

    private void RetrieveUserInformation()
    {
        RootRef.child(CurrentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    //read image
                    if (snapshot.child("Name").exists())
                    {
                        String name = snapshot.child("Name").getValue().toString();
                        Name.setText(name);
                    }
                    if (snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(SettingImage);
                    }
                    if (snapshot.child("Status").exists())
                    {
                        String status = snapshot.child("Status").getValue().toString();
                        Status.setText(status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(SettingActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void OpenGalleryForSelectImage()
    {
        Intent intentGallery = new Intent();
        intentGallery.setAction(Intent.ACTION_PICK);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK)
        {
            //  Uri image = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                profileImage=resultUri.toString();
                storageReference.child(CurrentUserId).putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingActivity.this, R.string.profile_success, Toast.LENGTH_SHORT).show();
                            storageReference.child(CurrentUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String image = uri.toString();
                                    Picasso.get().load(image).into(SettingImage);
                                    profileImage=image;
                                }
                            });
                        }
                        else
                        {
                            String massage = task.getException().toString();
                            Toast.makeText(SettingActivity.this, "Error: " + massage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            }

        }
    }

    private void UpdateSettingsMethod()
    {
        String name = Name.getText().toString();
        String status = Status.getText().toString();
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(status))
        {
            Toast.makeText(this, R.string.enter_status, Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> SettingMap = new HashMap<>();
            SettingMap.put("uID", CurrentUserId);
            SettingMap.put("Name", name);
            SettingMap.put("Status", status);
            if (profileImage!=null)
            {
                SettingMap.put("image",profileImage);
            }
            RootRef.child(CurrentUserId).setValue(SettingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(SettingActivity.this, R.string.update_massage, Toast.LENGTH_LONG).show();
                        sendUserToMainActivity();
                    }
                    else
                    {
                        Toast.makeText(SettingActivity.this, "Error : " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

}