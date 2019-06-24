package emad.athena.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comix.rounded.RoundedCornerImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import emad.athena.Model.News;
import emad.athena.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{

    ArrayList<News> newsArrayList;
    Context context;

    public NewsAdapter(ArrayList<News> newsArrayList, Context context) {
        this.newsArrayList = newsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.news_item_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        Picasso.get().load(newsArrayList.get(i).getUrlToImage()).placeholder(R.drawable.placeholder).into(holder.newsImageItem);
        holder.title.setText(newsArrayList.get(i).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayItem(newsArrayList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedCornerImageView newsImageItem;
        TextView title;
        Intent intent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            newsImageItem = itemView.findViewById(R.id.newsImageItem);
            title = itemView.findViewById(R.id.title);
        }
    }

    public void displayItem(News news){
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.display_news, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        alertDialogBuilder.setView(view);

         TextView source = view.findViewById(R.id.source);
         ImageView displayImageNews = view.findViewById(R.id.displayImageNews);
         TextView displayNewsTitle = view.findViewById(R.id.displayNewsTitle);
         TextView displayNewsDesc = view.findViewById(R.id.displayNewsDesc);

         source.setText(news.getName());
         Picasso.get().load(news.getUrlToImage()).placeholder(R.drawable.placeholder).into(displayImageNews);
         displayNewsTitle.setText(news.getTitle());
         displayNewsDesc.setText(news.getDescription());



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
