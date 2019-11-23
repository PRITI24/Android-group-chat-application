package com.example.chatplease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {
    private Button save;
    private EditText status,name;
    private CircleImageView image;
    private FirebaseAuth mauth;
    private DatabaseReference ref;
    private FirebaseUser currentuser;
    private String userId;
    private ProgressDialog Loadingbar;
    private StorageReference userprofileimagereference;
    private static final int gallerypic=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mauth= FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();
        currentuser=mauth.getCurrentUser();
        userId=currentuser.getUid();
        userprofileimagereference= FirebaseStorage.getInstance().getReference().child("Profile Images");
        Intializefields();

//        name.setVisibility(View.INVISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked();
            }

        });
        RetrieveUserInfo();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Galleryintent=new Intent();
                Galleryintent.setType("image/*");
                startActivityForResult(Galleryintent,gallerypic);
            }
        });
    }

    private void RetrieveUserInfo() {
        ref.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                {
                    String retrieveusername=dataSnapshot.child("name").getValue().toString();
                    String retrievestatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveimage=dataSnapshot.child("image").getValue().toString();
                    name.setText(retrieveusername);
                    status.setText(retrievestatus);
                    Picasso.get().load(retrieveimage).into(image);


                }
                else if(dataSnapshot.exists() && (dataSnapshot.hasChild("name")))
                {
                    String retrieveusername=dataSnapshot.child("name").getValue().toString();
                    String retrievestatus=dataSnapshot.child("status").getValue().toString();
                    name.setText(retrieveusername);
                    status.setText(retrievestatus);
                }
                else
                {
//                    name.setVisibility(View.VISIBLE);
                    Toast.makeText(settings.this,"Please set and update your profile picture!!!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clicked()
    {
        String user=name.getText().toString();
        String stat=status.getText().toString();
        if(TextUtils.isEmpty(user))
        {
            Toast.makeText(settings.this,"Please Enter name...",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(stat))
        {
            Toast.makeText(settings.this,"Please Enter statis...",Toast.LENGTH_LONG).show();
        }
        else
        {
            HashMap<String,String> h=new HashMap<>();
                h.put("uid",userId);
                h.put("name",user);
                h.put("status",stat);

            ref.child("Users").child(userId).setValue(h)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        gotomain();
                        Toast.makeText(settings.this,"Updated Successfully...",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String error=task.getException().toString();
                        Toast.makeText(settings.this,"Error "+error,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypic && resultCode==RESULT_OK && data!=null)
        {
            Uri imageuri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Loadingbar.setTitle("Set Profile Image");
                Loadingbar.setMessage("Please Wait,your profile image is updating...");
                Loadingbar.setCanceledOnTouchOutside(false);
                Loadingbar.show();

                Uri resulturi=result.getUri();

                StorageReference file=userprofileimagereference.child(userId+".jpg");
                file.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(settings.this,"Profile Image uploaded Successfully",Toast.LENGTH_LONG).show();
                                final String downloadurl=task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                ref.child("Users").child(userId).child("image").setValue(downloadurl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(settings.this,"Image saved in database,successfully...",Toast.LENGTH_LONG).show();
                                                        Loadingbar.dismiss();
                                                    }
                                                    else
                                                    {
                                                        String message=task.getException().toString();
                                                        Toast.makeText(settings.this,"Error : "+message,Toast.LENGTH_LONG).show();
                                                        Loadingbar.dismiss();
                                                    }
                                            }
                                        });
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(settings.this,"Error : "+message,Toast.LENGTH_LONG).show();
                                Loadingbar.dismiss();
                            }
                    }
                });
            }
        }
    }

    private void gotomain()
    {
        Intent  main=new Intent(settings.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }
    private void Intializefields()
    {
        save=(Button)findViewById(R.id.setting_button);
        status=(EditText)findViewById(R.id.set_profile_status);
        name=(EditText)findViewById(R.id.set_user_name);
        image=(CircleImageView)findViewById(R.id.set_profile_image);
        Loadingbar=new ProgressDialog(settings.this);
    }

}
