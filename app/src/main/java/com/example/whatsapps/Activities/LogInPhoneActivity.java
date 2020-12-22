package com.example.whatsapps.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsapps.MainActivity;
import com.example.whatsapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInPhoneActivity extends AppCompatActivity
{

    @BindView(R.id.loginPhoneToolBar)
    Toolbar loginPhoneToolBar;
    @BindView(R.id.editTextMobile)
    EditText editTextMobile;
    @BindView(R.id.buttonContinue)
    Button buttonContinue;

    FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_phone);

        initializeFields();
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phoneNumber=editTextMobile.getText().toString();
                if (phoneNumber.isEmpty())
                {
                    Toast.makeText(LogInPhoneActivity.this, R.string.please_enter_phone, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60,TimeUnit.SECONDS, LogInPhoneActivity.this,mCallbacks);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential)
            {
              //  signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {

                Toast.makeText(LogInPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                Toast.makeText(LogInPhoneActivity.this, R.string.code_sent_massage, Toast.LENGTH_LONG).show();
                Intent VerifyCode=new Intent(LogInPhoneActivity.this,VerfiyPhoneActivity.class);
                VerifyCode.putExtra("code",verificationId);
                startActivity(VerifyCode);

            }
        };
    }
    private void initializeFields() {

        ButterKnife.bind(this);

        setSupportActionBar(loginPhoneToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mAuth=FirebaseAuth.getInstance();
    }

}