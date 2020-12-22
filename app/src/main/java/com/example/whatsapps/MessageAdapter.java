package com.example.whatsapps;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    ArrayList<MessageDetails> Messages;
    DatabaseReference userRef;
    AlertDialog Dialog;
    public MessageAdapter(ArrayList<MessageDetails> messages) {
        Messages = messages;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,null,false);
        MessageViewHolder holder=new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String messageSenderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MessageDetails messages = Messages.get(position);
        String FromUserId = messages.getFrom();
        String fromMessageType = messages.getType();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FromUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("image")) {
                    Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(holder.ImgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.ReceiverMessage.setVisibility(View.GONE);
        holder.ImgProfile.setVisibility(View.GONE);
        holder.SenderMessage.setVisibility(View.GONE);
        holder.SenderPicture.setVisibility(View.GONE);
        holder.ReceiverPicture.setVisibility(View.GONE);
        holder.SenderPictureFile.setVisibility(View.GONE);
        holder.ReceiverPictureFile.setVisibility(View.GONE);
        if (fromMessageType.equals("text"))
        {

            if (FromUserId.equals(messageSenderID))
            {

                holder.SenderMessage.setVisibility(View.VISIBLE);
                holder.SenderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                holder.SenderMessage.setTextColor(Color.BLACK);
                holder.SenderMessage.setText(messages.getText() + "\n\n" + messages.getTime() + " - " + messages.getDate());

            }
            else
            {
                holder.ReceiverMessage.setVisibility(View.VISIBLE);
                holder.ImgProfile.setVisibility(View.VISIBLE);
                holder.ReceiverMessage.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.ReceiverMessage.setTextColor(Color.BLACK);
                holder.ReceiverMessage.setText(messages.getText() + "\n\n" + messages.getTime() + " - " + messages.getDate());

            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (FromUserId.equals(messageSenderID))
            {
                holder.SenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getText()).into(holder.SenderPicture);

            }
            else
            {
                holder.ImgProfile.setVisibility(View.VISIBLE);
                holder.ReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getText()).into(holder.ReceiverPicture);

            }
        }
        else if (fromMessageType.equals("pdf")||fromMessageType.equals("docx"))
        {
            if (FromUserId.equals(messageSenderID))
            {
                holder.SenderPictureFile.setVisibility(View.VISIBLE);
                holder.SenderPictureFile.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Messages.get(position).getText()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                holder.ImgProfile.setVisibility(View.VISIBLE);
                holder.ReceiverPictureFile.setVisibility(View.VISIBLE);
                holder.ReceiverPictureFile.setBackgroundResource(R.drawable.file);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Messages.get(position).getText()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }


        if (FromUserId.equals(messageSenderID))
        {

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (Messages.get(position).getType().equals("pdf")||Messages.get(position).getType().equals("docx")) {
                        CharSequence sequence[] = new CharSequence[]{
                                "Delete For me",
                                "Download and View This Document",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteSendMessage(position, holder);
                                }
                                if (which == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Messages.get(position).getText()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (which == 2) {
                                    Dialog.dismiss();
                                }
                                if (which == 3) {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog = builder.show();
                    }

                    else if (Messages.get(position).getType().equals("text"))
                    {
                        CharSequence sequence[]=new CharSequence[]{
                                "Delete For me",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    deleteSendMessage(position,holder);
                                }
                                if (which==1)
                                {
                                    Dialog.dismiss();
                                }
                                if (which==2)
                                {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog= builder.show();
                    }


                    else if (Messages.get(position).getType().equals("image"))
                    {
                        CharSequence sequence[]=new CharSequence[]{
                                "Delete For me",
                                "View This Image",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    deleteSendMessage(position,holder);
                                }
                                if (which==1)
                                {
                                    Intent intent=new Intent(holder.itemView.getContext(), ProfileImageShow.class);
                                    intent.putExtra("image",Messages.get(position).getText());
                                    intent.putExtra("name","   ");
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (which==2)
                                {
                                    Dialog.dismiss();
                                }
                                if (which==3)
                                {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog= builder.show();
                    }


                    return true;
                }
            });
        }

        else
        {


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (Messages.get(position).getType().equals("pdf")||Messages.get(position).getType().equals("docx")) {
                        CharSequence sequence[] = new CharSequence[]{
                                "Delete For me",
                                "Download and View This Document",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteReceiveMessage(position, holder);
                                }
                                if (which == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Messages.get(position).getText()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (which == 2) {
                                    Dialog.dismiss();
                                }
                                if (which == 3) {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog = builder.show();
                    }

                    else if (Messages.get(position).getType().equals("text"))
                    {
                        CharSequence sequence[]=new CharSequence[]{
                                "Delete For me",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    deleteReceiveMessage(position,holder);
                                }
                                if (which==1)
                                {
                                    Dialog.dismiss();
                                }
                                if (which==2)
                                {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog= builder.show();
                    }


                    else if (Messages.get(position).getType().equals("image"))
                    {
                        CharSequence sequence[]=new CharSequence[]{
                                "Delete For me",
                                "View This Image",
                                "Cancel",
                                "Delete for Everyone"
                        };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle(R.string.dMsg);
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    deleteReceiveMessage(position,holder);
                                }
                                if (which==1)
                                {
                                    Intent intent=new Intent(holder.itemView.getContext(), ProfileImageShow.class);
                                    intent.putExtra("image",Messages.get(position).getText());
                                    intent.putExtra("name","   ");
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if (which==2)
                                {
                                    Dialog.dismiss();
                                }
                                if (which==3)
                                {
                                    deleteSendMessage(position,holder);
                                    deleteReceiveMessage(position,holder);
                                }
                            }
                        });
                        Dialog= builder.show();
                    }

                    return true;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }

    private void deleteSendMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Messages").child(Messages.get(position).getFrom()).child(Messages.get(position).getTo()).child(Messages.get(position).messageID);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully..", Toast.LENGTH_SHORT).show();
                }
                if (!task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteReceiveMessage(final int position,final MessageViewHolder holder)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Messages").child(Messages.get(position).getTo()).child(Messages.get(position).getFrom()).child(Messages.get(position).messageID);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully..", Toast.LENGTH_SHORT).show();
                }
                if (!task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clearList()
    {
        Messages.clear();
        notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView SenderMessage,ReceiverMessage;
        CircleImageView ImgProfile;
        ImageView SenderPicture,ReceiverPicture;
        ImageView SenderPictureFile,ReceiverPictureFile;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            SenderMessage=itemView.findViewById(R.id.Sender_message_text);
            ReceiverMessage=itemView.findViewById(R.id.receiver_message_text);
            ImgProfile=itemView.findViewById(R.id.message_image);
            SenderPicture=itemView.findViewById(R.id.SenderImageView);
            ReceiverPicture=itemView.findViewById(R.id.receiverImageView);
            ReceiverPictureFile=itemView.findViewById(R.id.receiverImageViewFile);
            SenderPictureFile=itemView.findViewById(R.id.SenderImageViewFile);


        }
    }
}
