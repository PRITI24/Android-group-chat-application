package com.example.chatplease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private Button createaccount;
    private EditText email,password;
    private FirebaseAuth firebases;
    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        intializefields();
        firebases=FirebaseAuth.getInstance();

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Createnewaccount();
            }

            private void Createnewaccount() {
                String emails=email.getText().toString();
                String passwords=password.getText().toString();
                if(TextUtils.isEmpty(emails)){
                    Toast.makeText(Register.this,"Please Enter email...",Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(passwords)){
                    Toast.makeText(Register.this,"Please Enter Password...",Toast.LENGTH_LONG).show();
                }
                else {
                    Loadingbar.setTitle("Creating new Account...");
                    Loadingbar.setMessage("Creating new Account,Please Wait...");
                    Loadingbar.setCanceledOnTouchOutside(true);
                    Loadingbar.show();
                    firebases.createUserWithEmailAndPassword(emails,passwords)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Mainactivity();
                                      Toast.makeText(Register.this,"Account Created successfully",Toast.LENGTH_LONG).show();
                                      Loadingbar.dismiss();
                                      settingsactivity();
                                    }
                                    else
                                    {
                                        String message=task.getException().toString();
                                        Toast.makeText(Register.this,"Error "+message,Toast.LENGTH_LONG).show();
                                        Loadingbar.dismiss();
                                    }
                                }

                                private void Mainactivity() {

                                    Intent LoginIntent=new Intent(Register.this,MainActivity.class);
                                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(LoginIntent);
                                    finish();
                                }
                            });
                }
            }
        });

    }

    private void intializefields() {
        createaccount=(Button)findViewById(R.id.register_button);
        email=(EditText)findViewById(R.id.register_email);
        password=(EditText)findViewById(R.id.register_password);
        Loadingbar=new ProgressDialog(Register.this);

    }
    private void settingsactivity()
    {
        Intent settingIntent=new Intent(Register.this,settings.class);
        startActivity(settingIntent);

    }
    private void Mainloginactivity() {

        Intent LoginIntent=new Intent(Register.this,Login.class);
        startActivity(LoginIntent);
    }

}
