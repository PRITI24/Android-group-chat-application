package com.example.chatplease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private Button Loginbutton,phonelogin;
    private EditText email,password;
    private TextView forgetpassword,Register;
    private FirebaseAuth firebasing;
    private ProgressDialog Loading;
    private DatabaseReference referroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initializingfields();
        firebasing=FirebaseAuth.getInstance();
        referroot= FirebaseDatabase.getInstance().getReference();
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registeractivity();
            }
        });
        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loginsuccess();
            }

            private void Loginsuccess() {
                String emails=email.getText().toString();
                String passwords=password.getText().toString();
                if(TextUtils.isEmpty(emails)){
                    Toast.makeText(Login.this,"Please Enter email...",Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(passwords)){
                    Toast.makeText(Login.this,"Please Enter Password...",Toast.LENGTH_LONG).show();
                }
                else {

                    Loading.setTitle("Login...");
                    Loading.setMessage("Logging in,Please Wait...");
                    Loading.setCanceledOnTouchOutside(true);
                        firebasing.signInWithEmailAndPassword(emails,passwords)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            String idofcurrentuser=firebasing.getCurrentUser().getUid();
                                            referroot.child("Users").child(idofcurrentuser).setValue("");
                                            Mainloginactivity();
                                            Toast.makeText(Login.this,"Login succesful...",Toast.LENGTH_LONG);
                                            Loading.dismiss();
                                        }
                                        else
                                        {

                                            String message=task.getException().toString();
                                            Toast.makeText(Login.this,"Error "+message,Toast.LENGTH_LONG).show();
                                            Loading.dismiss();
                                        }
                                    }
                                });
                }
            }
        });

    }
    private void Initializingfields()
    {
        Loginbutton=(Button)findViewById(R.id.Login_button);
        email=(EditText)findViewById(R.id.Login_email);
        password=(EditText)findViewById(R.id.Login_password);
        Register=(TextView)findViewById(R.id.Register);
        forgetpassword=(TextView)findViewById(R.id.forget_password);
        Loading=new ProgressDialog(Login.this);
    }

    private void Mainloginactivity() {

        Intent LoginIntent=new Intent(Login.this,MainActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }
    private void Registeractivity() {

        Intent registerIntent=new Intent(Login.this,Register.class);
        startActivity(registerIntent);
    }

}
