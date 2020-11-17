package com.koddev.authenticatorapp.fragment.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.koddev.authenticatorapp.AddPostActivity;
import com.koddev.authenticatorapp.AgentActivity;
import com.koddev.authenticatorapp.Login.MainActivity;
import com.koddev.authenticatorapp.R;
import com.squareup.picasso.Picasso;

import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.AdapterPosts;
import models.ModelPost;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;

    String storagePath = "Users_Profile_Cover_Imgs/";

    ImageView avatarIv, coverIv;
    TextView nameTv, emailTv, genderTv,ageTv,birthdayTv,constellationTv,positionTv, mailboxTv;
    FloatingActionButton fab;
    RecyclerView postsRecyclerView;


    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    Uri image_uri;
    String profileOrCoverPhoto;

    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.EmailTv);
        genderTv = view.findViewById(R.id.genderTv);
        ageTv=view.findViewById(R.id.ageTv);
        birthdayTv=view.findViewById(R.id.birthdayTv);
        constellationTv=view.findViewById(R.id.constellationTv);
        positionTv=view.findViewById(R.id.positionTv);
        mailboxTv=view.findViewById(R.id. mailboxTv);
        fab = view.findViewById(R.id.fab);
        postsRecyclerView = view.findViewById(R.id.recyclerView_posts);

        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String gender = "" + ds.child("gender").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    String age = "" + ds.child("age").getValue();
                    String birthday=""+ds.child("birthday").getValue();
                    String constellation=""+ds.child("constellation").getValue();
                    String position=""+ds.child("position").getValue();
                    String mailbox=""+ds.child("mailbox").getValue();


                    nameTv.setText(name);
                    emailTv.setText(email);
                    genderTv.setText(gender);
                    ageTv.setText(age);
                    birthdayTv.setText(birthday);
                    constellationTv.setText(constellation);
                    positionTv.setText(position);
                    mailboxTv.setText(mailbox);

                    try {
                        Picasso.get().load(image).into(avatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                    try {
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        postList = new ArrayList<>();

        checkUserStatus();
        loadMyPosts();


        return view;

    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    postList.add(myPosts);

                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searcgMyPosts(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if (myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPosts);
                    }

                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {

        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {

        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {


        String options[] = {"修改個人照片", "修改封面照片", "修改名稱","修改性別","修改年齡","修改生日","修改星座","居住地","留言板","密碼"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("個人資料修改");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                } else if (which == 1) {
                    pd.setMessage("Updating Cover Picture");
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();

                } else if (which == 2) {
                    pd.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");

                } else if (which == 3) {
                    pd.setMessage("Updating gender");
                    showNamePhoneUpdateDialog("gender");

                } else if (which == 4) {
                    pd.setMessage("Updating age");
                    showNamePhoneUpdateDialog("age");

                } else if (which == 5) {
                    pd.setMessage("Updating birthday");
                    showNamePhoneUpdateDialog("birthday");

                } else if (which == 6) {
                    pd.setMessage("Updating constellation");
                    showNamePhoneUpdateDialog("constellation");

                } else if (which == 7) {
                    pd.setMessage("Updating position");
                    showNamePhoneUpdateDialog("position");

                } else if (which == 8) {
                    pd.setMessage("Updating mailbox");
                    showNamePhoneUpdateDialog("mailbox");

                }else if (which == 9) {
                    pd.setMessage("Changing Password");
                    showNamePhoneUpdateDialog();

                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password,null);
        final EditText passwordEt = view.findViewById(R.id.passwordEt);
        final EditText newpasswordEt = view.findViewById(R.id.newPasswordEt);
        Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String oldPassword = passwordEt.getText().toString().trim();
                String newPassword = newpasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(getActivity(),"Enter your current password...",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length()<6){
                    Toast.makeText(getActivity(),"Password length must atleast 6 characters...",Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oldPassword,newPassword);
            }


        });
    }

    private void updatePassword(String oldPassword, final String newPassword) {
        pd.show();

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
                                        }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showNamePhoneUpdateDialog(final String key) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update"+key);

        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter"+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            final String value=editText.getText().toString().trim();

            if(!TextUtils.isEmpty(value)){
               pd.show();
               HashMap<String,Object>result=new HashMap<>();
               result.put(key,value);

               databaseReference.child(user.getUid()).updateChildren(result)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                           pd.dismiss();
                           Toast.makeText(getActivity(),"Updated...",Toast.LENGTH_SHORT).show();
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               pd.dismiss();
                               Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                           }
                       });
               if (key.equals("name")){
                   final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                   Query query = ref.orderByChild("uid").equalTo(uid);
                   query.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           for (DataSnapshot ds: dataSnapshot.getChildren()){
                               String child = ds.getKey();
                               dataSnapshot.getRef().child(child).child("uName").setValue(value);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

                   ref.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           for (DataSnapshot ds: dataSnapshot.getChildren()){
                               String child = ds.getKey();
                               if (dataSnapshot.child(child).hasChild("Comments")) {
                                   String child1 = "" + dataSnapshot.child(child).getKey();
                                   Query child2 = FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                   child2.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           for (DataSnapshot ds:dataSnapshot.getChildren()){
                                               String child=ds.getKey();
                                               dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });

                               }




                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


               }

            }
            else{
                Toast.makeText(getActivity(),"Please enter"+key,Toast.LENGTH_SHORT).show();

            }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });

        builder.create().show();


    }

    private void showImagePicDialog() {

        String options[] ={"相機","圖庫"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("取得圖片從");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0) {

                   if(!checkCameraPermission()){
                       requestCameraPermission();
                   }
                   else{
                       pickFromCamera();
                   }
                }
                else  if(which==1){
                  if (!checkStoragePermission()){
                      requestStoragePermission();
                  }
                  else {
                      pickFromGallery();
                  }
                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case  CAMERA_REQUEST_CODE:{

                if(grantResults.length>0){
                    boolean cameraAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted ){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(),"Please enable camera & storage permission",Toast.LENGTH_SHORT).show();

                    }
                }

            }
            break;
            case  STORAGE_REQUEST_CODE:{


                if(grantResults.length>0){

                    boolean writeStorageAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted ){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(),"Please enable storage permission",Toast.LENGTH_SHORT).show();

                    }
                }

            }
            break;
        }





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK){

            if(requestCode==IMAGE_PICK_GALLERY_CODE){

                image_uri=data.getData();

                uploadProfileCoverPhoto(image_uri);

            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){

                uploadProfileCoverPhoto(image_uri);

            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(final Uri uri) {
    pd.show();


     String filePathandName=storagePath+""+profileOrCoverPhoto+"_"+user.getUid();

     StorageReference storageReference2nd =storageReference.child(filePathandName);
     storageReference2nd.putFile(uri)
             .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                     while (!uriTask.isSuccessful());
                     final Uri downloadUri=uriTask.getResult();


                     if(uriTask.isSuccessful()){
                         HashMap<String,Object>results=new HashMap<>();
                         results.put(profileOrCoverPhoto,downloadUri.toString());

                         databaseReference.child(user.getUid()).updateChildren(results)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {

                                         pd.dismiss();
                                         Toast.makeText(getActivity(),"Image Updated",Toast.LENGTH_SHORT).show();
                                     }
                                 })
                                 .addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {

                                         pd.dismiss();
                                         Toast.makeText(getActivity(),"Erro Updating Image...",Toast.LENGTH_SHORT).show();

                                     }
                                 });

                         if (profileOrCoverPhoto.equals("image")){
                             DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                             Query query = ref.orderByChild("uid").equalTo(uid);
                             query.addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     for (DataSnapshot ds: dataSnapshot.getChildren()){
                                         String child = ds.getKey();
                                         dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });

                             ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                         String child = ds.getKey();
                                         if (dataSnapshot.child(child).hasChild("Comments")) {
                                             String child1 = "" + dataSnapshot.child(child).getKey();
                                             Query child2 = FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                             child2.addValueEventListener(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                     for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        String child=ds.getKey();
                                                        dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                                     }
                                                 }

                                                 @Override
                                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                                 }
                                             });
                                         }
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });


                         }

                     }
                     else{
                         pd.dismiss();
                         Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();

                     }


                 }
             })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     pd.dismiss();
                     Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                 }
             });

    }


///30:23
    private void pickFromCamera() {


        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {

        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);


    }
    private void  checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s)){
                    searcgMyPosts(s);
                }
                else {
                    loadMyPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)){
                    searcgMyPosts(s);
                }
                else {
                    loadMyPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }


        if(id==R.id.action_add_post){
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        }

        if (item.getItemId()==R.id.action_Service)
        {
            startActivity(new Intent(getActivity(), AgentActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }





}
