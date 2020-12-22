package com.example.whatsapps.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.FirebaseInstallationsApi;
import com.google.firebase.installations.FirebaseInstallationsRegistrar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LogInActivity extends AppCompatActivity
{

    @BindView(R.id.signInEmail)
    EditText EmailText;
    @BindView(R.id.signInPassword)
    EditText PasswordText;
    @BindView(R.id.signIn)
    Button LogInEmail;
    @BindView(R.id.goToreg)
    TextView GoToRegister;
    @BindView(R.id.LogInUsingPhone)
    CircleImageView LogInPhone;
    @BindView(R.id.showOrhidePasswordIn)
    ImageView showOrHidePasswordIn;
    @BindView(R.id.ForgotPass)
    TextView GoToResetAccount;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initialViewsMethod();
        showAndHidePasswordMethod();
        GoToRegisterMethod();
        AllowUserToSignIn();
        GotoResetPassword();
    }
    private void initialViewsMethod()
    {
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        LogInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LogInActivity.this,LogInPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    private void GotoResetPassword()
    {
       GoToResetAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               SendUserToResetPassword();
           }
       });
    }

    private void SendUserToResetPassword()
    {
        Intent intent=new Intent(this,ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void showAndHidePasswordMethod()
    {

        showOrHidePasswordIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageForgotPasswordMethod();
            }
        });
    }

    private void imageForgotPasswordMethod()
    {
        if (PasswordText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
        {
            showOrHidePasswordIn.setImageResource(R.drawable.ic_invisible);
            //Show Password
            PasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            showOrHidePasswordIn.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            //Hide Password
            PasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void GoToRegisterMethod()
    {
        GoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendUserToCreateAccountMethod();
            }
        });
    }

    private void sendUserToCreateAccountMethod()
    {
        Intent LogInIntent = new Intent(this, RegisterActivity.class);
        startActivity(LogInIntent);
    }

    private void AllowUserToSignIn()
    {
        LogInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String Email = EmailText.getText().toString();
                String Password = PasswordText.getText().toString();
                if (TextUtils.isEmpty(Email))
                {
                    Toast.makeText(LogInActivity.this, R.string.please_enter_email, Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(Password))
                {
                    Toast.makeText(LogInActivity.this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog.setTitle(R.string.sign_in);
                    progressDialog.setMessage(getString(R.string.Please_wait));
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                verifyEmailAddress();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Error Log in: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void verifyEmailAddress()
    {
        if (auth.getCurrentUser().isEmailVerified())
        {
            Toast.makeText(LogInActivity.this, R.string.Logged_success, Toast.LENGTH_LONG).show();
            sendUserToMainActivity();
        }
        else
        {
            Toast.makeText(this, R.string.verviy_account_massage, Toast.LENGTH_LONG).show();
        }
    }
}