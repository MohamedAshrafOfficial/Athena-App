package emad.athena.Fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import emad.athena.Adapters.NewsAdapter;
import emad.athena.Model.Chat;
import emad.athena.Model.ChatbotResponse;
import emad.athena.Model.News;
import emad.athena.Model.Recent;
import emad.athena.R;
import emad.athena.Tools.Helper;
import emad.athena.VolleyUtils.MySingleton;

public class FeautresFragment extends Fragment {

    TextView hiUser;
    RelativeLayout LMessage;
    LinearLayout  Lphonecall, Ltimer, Lnews, Levent, Lremender, LWeather, Lgame, Lyoutube, Lmusic, Lgallery;
    Helper helper = new Helper();

    News newsResponse = new News();
    RecyclerView newsRecycler;
    NewsAdapter newsAdapter;

    ArrayList<News> newsList = new ArrayList<>();
    private static final String TAG = "FeautresFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feautres, container, false);
        setUpViews(view);
        handleViews();
        initRecyclerView(view);
        showNews();
        return view;
    }

    public void initRecyclerView(View view){
        newsRecycler = view.findViewById(R.id.newsRecycler);
        newsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsAdapter = new NewsAdapter(newsList, getActivity());
        newsRecycler.setNestedScrollingEnabled(false);
        newsRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        newsRecycler.setAdapter(newsAdapter);
    }
    public void setUpViews(View view){
        hiUser = view.findViewById(R.id.hiUser);
        LMessage = view.findViewById(R.id.LMessage);
        Lphonecall = view.findViewById(R.id.Lphonecall);
        Ltimer = view.findViewById(R.id.Ltimer);
        Levent = view.findViewById(R.id.Levent);
        Lremender = view.findViewById(R.id.Lremender);
        Levent = view.findViewById(R.id.Levent);
        Lnews = view.findViewById(R.id.Lnews);
        LWeather = view.findViewById(R.id.LWeather);
        Lgame = view.findViewById(R.id.lgame);
        Lyoutube = view.findViewById(R.id.Lvideo);
        Lmusic = view.findViewById(R.id.Lmusic);
        Lgallery = view.findViewById(R.id.Lphotos);
    }

    public void handleViews(){
        //for message goodmorning
        hiUser.setText(helper.setHI());

        LMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.setMessage(getActivity());
            }
        });

        Lphonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               helper.phoneCall();
            }
        });

        Ltimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.startTimer(getActivity());
            }
        });

        Levent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.startEvent(getActivity());
            }
        });

        Lremender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { helper.startRemender(getActivity());
            }
        });

        Lnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNews();
            }
        });

        LWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.getWeather(getActivity());
            }
        });

        Lyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.openYoutube();
            }
        });

        Lgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.playGame();
            }
        });

        Lmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.playMusic();
            }
        });

        Lgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.openGallery();
            }
        });
    }

    public void showNews(){
        String url = "https://newsapi.org/v2/top-headlines?country=eg&apiKey=f6117a26eb70433b91c323fa3ac07e56";
         StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonData = jsonObject.getJSONArray("articles");
                            newsList.clear();
                            for (int x = 0; x <jsonData.length() ; x++) {

                                newsResponse = new News();
                                    newsResponse.setName(jsonData.getJSONObject(x).getJSONObject("source").getString("name"));
                                    newsResponse.setDescription(jsonData.getJSONObject(x).getString("description"));
                                    newsResponse.setPublishedAt(jsonData.getJSONObject(x).getString("publishedAt"));
                                    newsResponse.setTitle(jsonData.getJSONObject(x).getString("title"));
                                    newsResponse.setUrl(jsonData.getJSONObject(x).getString("url"));
                                    newsResponse.setUrlToImage(jsonData.getJSONObject(x).getString("urlToImage"));

                                    newsList.add(newsResponse);
                            }

                            newsAdapter.notifyDataSetChanged();

                            Log.d(TAG, "onResponse: SIZE " + newsList.size());
                        }catch (Exception ex){
                            Log.d(TAG, "onResponse:ERROR " + ex.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "showNews: " + error.getMessage());
                Log.d(TAG, "showNews: " + error);
                Log.d(TAG, "showNews: " + error.networkResponse);
                Log.d(TAG, "showNews: " + error.getLocalizedMessage());
                Log.d(TAG, "showNews: " + error.getNetworkTimeMs());

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInsance(getContext()).addToRequestQueue(stringRequest);
    }
}