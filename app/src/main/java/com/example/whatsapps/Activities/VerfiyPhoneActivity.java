package com.example.whatsapps.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsapps.MainActivity;
import com.example.whatsapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerfiyPhoneActivity extends AppCompatActivity
{

    @BindView(R.id.verfiyPhoneToolBar)
    Toolbar verfiyPhoneToolBar;
    @BindView(R.id.editTextCode)
    EditText editTextCode;
    @BindView(R.id.buttonSignIn)
    Button buttonSignIn;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfiy_phone);

        initializeFields();

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            loadingBar.setTitle(getString(R.string.verfiy));
            loadingBar.setMessage(getString(R.string.wait_verfiy));
            loadingBar.show();

            String code=editTextCode.getText().toString();
            String verificationId=getIntent().getStringExtra("code");
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);

            }
        });

    }

    private void initializeFields() {

        ButterKnife.bind(this);

        setSupportActionBar(verfiyPhoneToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(VerfiyPhoneActivity.this, R.string.congrat_massage, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(VerfiyPhoneActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            String Error=task.getException().toString();
                            Toast.makeText(VerfiyPhoneActivity.this, Error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}