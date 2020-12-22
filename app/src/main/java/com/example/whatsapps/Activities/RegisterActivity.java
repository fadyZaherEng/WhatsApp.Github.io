package com.example.whatsapps.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity
{

    @BindView(R.id.signUpEmail)
    EditText TextEmail;
    @BindView(R.id.signUpPassword)
    EditText TextPassword;
    @BindView(R.id.signUp)
    Button createAccountButton;
    @BindView(R.id.showOrhidePasswordUp)
    ImageView showOrhidePasswordUp;
    @BindView(R.id.reg_massages)
    TextView GoToLogIn;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initialViewsMethod();

        showAndHidePasswordMethod();
        GoToLogInAction();
        CreateAccountMethod();
    }

    private void initialViewsMethod()
    {
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        RootRef = firebaseDatabase.getReference();
    }

    private void CreateAccountMethod()
    {
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String Email = TextEmail.getText().toString();
                String Password = TextPassword.getText().toString();
                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(RegisterActivity.this, R.string.please_enter_email, Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(RegisterActivity.this,R.string.please_enter_password, Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.setTitle(getString(R.string.create_account));
                    progressDialog.setMessage(getString(R.string.wait_create_account));
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                sendEmailVerification();
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, R.string.created_account, Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendEmailVerification()
    {

        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    sendUserToMainActivity();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendUserToMainActivity()
    {

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void GoToLogInAction()
    {
        GoToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                GoToLogInMethod();
            }
        });
    }

    private void GoToLogInMethod()
    {
        Intent LogInIntent = new Intent(this, LogInActivity.class);
        startActivity(LogInIntent);
    }

    private void showAndHidePasswordMethod()
    {

        showOrhidePasswordUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageForgotPasswordMethod();
            }
        });
    }

    private void imageForgotPasswordMethod()
    {
        if (TextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
        {
            showOrhidePasswordUp.setImageResource(R.drawable.ic_invisible);
            //Show Password
            TextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            showOrhidePasswordUp.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            //Hide Password
            TextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}