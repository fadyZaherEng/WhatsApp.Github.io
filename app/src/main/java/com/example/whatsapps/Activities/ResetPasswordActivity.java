package com.example.whatsapps.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity
{

    @BindView(R.id.resetEmail)
    EditText resetEmail;
    @BindView(R.id.btn_resetPass)
    Button btnResetPass;
    @BindView(R.id.goToReg)
    TextView goToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
    }

    private void ResetPasswordUsingEmail(String Email)
    {
        if (Email.isEmpty())
        {
            Toast.makeText(getApplicationContext(),R.string.please_enter_email, Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), R.string.send_reset_email, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.send_reset_email_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick({R.id.btn_resetPass, R.id.goToReg})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_resetPass:
                btnResetPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        ResetPasswordUsingEmail(resetEmail.getText().toString());
                    }
                });
                break;
            case R.id.goToReg:
                goToReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        SendUserToRegister();
                    }
                });
                break;
        }
    }

    private void SendUserToRegister()
    {
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
}