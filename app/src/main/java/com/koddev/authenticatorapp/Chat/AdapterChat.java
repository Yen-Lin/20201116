package com.koddev.authenticatorapp.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koddev.authenticatorapp.R;
import com.koddev.authenticatorapp.ThereProfileActivity;
import com.koddev.authenticatorapp.Chat.ChatActivity;
import com.koddev.authenticatorapp.Chat.util.Define;
import com.koddev.authenticatorapp.users.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    Context context;
    List<ModelUser>userList;

    public AdapterChat(Context context, List<ModelUser>userList){
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        String userImage = userList.get(i).getImage();
        final String userName = userList.get(i).getName();
        String userGender=userList.get(i).getGender();
        final String userUUID = userList.get(i).getUid();

        final String userAge = userList.get(i).getAge();

        myHolder.mNameTv.setText(userName);
        myHolder.mAgeTv.setText(userAge);
        myHolder.mGenderTv.setText(userGender);


        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_img_white)
                    .into(myHolder.mAvatarIv);
        }
        catch (Exception e){

        }

        myHolder.itemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(context, ""+ userAge , Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.mItems.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Define.UserUUID,userUUID);
                bundle.putString(Define.UserName,userName);
                intent.putExtras(bundle);

                Intent intentChat = new Intent(context, ChatActivity.class);
                intentChat.putExtra(Define.UserUUID, userUUID);
                context.startActivity(intentChat);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mAgeTv,mGenderTv;
        LinearLayout mItems;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mItems = itemView.findViewById(R.id.linearlayout_items);
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mAgeTv = itemView.findViewById(R.id.ageTv);
            mGenderTv=itemView.findViewById(R.id.genderTv);
        }
    }
}
