package com.example.chatplease;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


import org.checkerframework.checker.nullness.qual.*;

public class groupchatactivity extends AppCompatActivity {

    private Toolbar m;
    private ImageButton sendmessagebutton;
    @Nullable private EditText usermessageinput;
    private ScrollView scroll;
    private TextView displaytextmessage;
    private  FirebaseAuth mAuth;
    @Nullable private DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;

    @Nullable private String currentGroupName,currentUserID, currentUserName, currentDate,currentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_groupchatactivity);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(groupchatactivity.this,"",Toast.LENGTH_SHORT).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group").child(currentGroupName);


        Initializefields();
        GetUserInfo();

        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDatabase();

                usermessageinput.setText("");
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }



    private void SaveMessageInfoToDatabase()
    {
       @Nullable String message = usermessageinput.getText().toString();
       @Nullable String messageKey = GroupNameRef.push().getKey();


        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"Please write message first...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForData = Calendar.getInstance();
            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd ,YYYY");
            currentDate = currentDataFormat.format(calForData.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentDate = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap =new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }

    private void GetUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initializefields() {
        m=(Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(m);
        getSupportActionBar().setTitle(currentGroupName);
        sendmessagebutton=(ImageButton)findViewById(R.id.sendmessagebutton);
        usermessageinput=(EditText)findViewById(R.id.inputgroupmessage);
        displaytextmessage=(TextView)findViewById(R.id.groupchattextdisplay);
        scroll=(ScrollView)findViewById(R.id.myscrollview);
        
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();


            displaytextmessage.append(chatName + ":\n" +chatMessage +"\n" +chatDate + "\n\n\n");
            scroll.fullScroll(ScrollView.FOCUS_DOWN);


        }
    }


}
