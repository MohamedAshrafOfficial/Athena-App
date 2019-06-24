package emad.athena.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import emad.athena.Model.Recent;
import emad.athena.R;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder>{

    Context mContext;
    List<Recent> recentList;
    DatabaseReference recentReference;
    FirebaseAuth mAuth;
    private static final String TAG = "RecentAdapter";
    public RecentAdapter(Context context, List<Recent> recentList) {
        this.mContext = context;
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recent_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.recentQuetionItem.setText(recentList.get(position).getQuestion());
        holder.recentAnswerItem.setText(recentList.get(position).getResponse());
        holder.viewAnswerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewResponse(holder,position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewResponse(holder,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);

                LayoutInflater inflater = LayoutInflater.from(mContext);
                View parentView = inflater.inflate(R.layout.more_recent, (ViewGroup) holder.itemView, false);
                bottomSheetDialog.setContentView(parentView);
                bottomSheetDialog.show();

                TextView copy = bottomSheetDialog.findViewById(R.id.copyDialog);
                TextView remove = bottomSheetDialog.findViewById(R.id.removeDialog);

                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = recentList.get(position).getQuestion() + " \n "+ recentList.get(position).getResponse() + " \n ";
                        addTextToClipboard(text);
                        Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: "+recentList.get(position).getFirebaseid());
                        removeItemFromFirebase(recentList.get(position).getFirebaseid());
                        Toast.makeText(mContext, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public void viewResponse(final MyViewHolder holder, final int position){
        if (!recentList.get(position).getExpanded()){
            holder.recentAnswerItem.setVisibility(View.VISIBLE);
            recentList.get(position).setExpanded(true);
            holder.viewAnswerIcon.setImageResource(R.drawable.epand_less);
        }else {
            holder.recentAnswerItem.setVisibility(View.GONE);
            recentList.get(position).setExpanded(false);
            holder.viewAnswerIcon.setImageResource(R.drawable.epand_more);
        }
    }

    public void addTextToClipboard(String text){
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CopiedText", text);
        clipboard.setPrimaryClip(clip);
    }

    public void removeItemFromFirebase(String firebaseID){
        mAuth = FirebaseAuth.getInstance();
        recentReference = FirebaseDatabase.getInstance().getReference().child("Recent").child(mAuth.getCurrentUser().getUid()).child(firebaseID);
        recentReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Item Deleted ");
                    notifyDataSetChanged();
                } else
                    Log.d(TAG, "onComplete: Failed ");
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView recentQuetionItem;
        public TextView recentAnswerItem;
        public ImageView viewAnswerIcon;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            recentQuetionItem = itemView.findViewById(R.id.recentQuetionItem);
            recentAnswerItem = itemView.findViewById(R.id.recentAnswerItem);
            viewAnswerIcon = itemView.findViewById(R.id.viewAnswerIcon);
        }
    }
}
