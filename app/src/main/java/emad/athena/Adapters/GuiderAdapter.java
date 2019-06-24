package emad.athena.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import emad.athena.Model.Guider;
import emad.athena.R;

public class GuiderAdapter extends RecyclerView.Adapter<GuiderAdapter.MyViewHolder>{

    ArrayList<Guider> guiderArrayList;
    boolean[] expandedArrayList;

    Context context;

    public GuiderAdapter(ArrayList<Guider> guiderArrayList, Context context) {
        expandedArrayList = new boolean[6];
        this.guiderArrayList = guiderArrayList;
        this.context = context;
        for (int x = 0; x < 6; x++) {
            expandedArrayList[x] = true;
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.guide_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {

        holder.recentQuetionItem.setText(guiderArrayList.get(i).getAction());
        holder.recentAnswerItem.setText(guiderArrayList.get(i).getExplain());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedArrayList[i]==true){
                    expandedArrayList[i] = false;
                    holder.viewAnswerIcon.setImageResource(R.drawable.epand_less);
                    holder.recentAnswerItem.setVisibility(View.VISIBLE);
                }else {
                    expandedArrayList[i] = true;
                    holder.viewAnswerIcon.setImageResource(R.drawable.epand_more);
                    holder.recentAnswerItem.setVisibility(View.GONE);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return guiderArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView recentQuetionItem;
        TextView recentAnswerItem;
        ImageView viewAnswerIcon;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            recentQuetionItem = itemView.findViewById(R.id.recentQuetionItem);
            recentAnswerItem = itemView.findViewById(R.id.recentAnswerItem);
            viewAnswerIcon = itemView.findViewById(R.id.viewAnswerIcon);
        }
    }
}