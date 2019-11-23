package com.example.chatplease;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Toolbar m;
    private ViewPager myviewpager;
    private TabLayout t;
    private Tsbsaccessor tabs;
    private FirebaseUser currentuser;
    private FirebaseAuth mauth;
    private DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth=FirebaseAuth.getInstance();
        currentuser=mauth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();

        m=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(m);
        getSupportActionBar().setTitle("Gupshup");

        myviewpager=(ViewPager)findViewById(R.id.main_tabs_pager);
        tabs=new Tsbsaccessor(getSupportFragmentManager());
        myviewpager.setAdapter(tabs);

        t=(TabLayout)findViewById(R.id.main_tabs);
        t.setupWithViewPager(myviewpager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentuser==null)
        {
            loginactivity();
        }
        else
        {
            VerifyUserExistence();
        }
    }
    private void VerifyUserExistence()
    {
       String UserId=mauth.getCurrentUser().getUid();
       ref.child("Users").child("name").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if((dataSnapshot.child("name").exists()))
               {
                   Toast.makeText(MainActivity.this,"Welcome!!!",Toast.LENGTH_LONG).show();     //User is previous user
               }
//               else
//               {
//                   settingsactivity();//new user
//               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_friends)
        {
            mauth.signOut();
            loginactivity();
        }
        if(item.getItemId()==R.id.main_setting_friends)
        {
            settingsactivity();

        }
        if(item.getItemId()==R.id.main_create_group)
        {
            Requestnewgroup();
        }

        return true;

    }

    private void Requestnewgroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group name:-");
        final EditText groupnamefield=new EditText(MainActivity.this);
        builder.setView(groupnamefield);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            String groupname=groupnamefield.getText().toString();
            if(TextUtils.isEmpty(groupname))
            {
                Toast.makeText(MainActivity.this,"Please write group name",Toast.LENGTH_LONG).show();
            }
            else
            {
             createnewgroup(groupname);
            }
            }


        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();


    }

    private void createnewgroup(final String groupname) {
        ref.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,groupname+"group is created successfully",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginactivity() {

        Intent LoginIntent=new Intent(MainActivity.this,Login.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void settingsactivity() {

        Intent sIntent=new Intent(MainActivity.this,settings.class);
        sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sIntent);
        finish();
    }



}
