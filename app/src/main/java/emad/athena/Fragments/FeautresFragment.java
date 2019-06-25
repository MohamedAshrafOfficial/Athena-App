package emad.athena.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    LinearLayout Lphonecall, Ltimer, Lnews, Levent, Lremender, LWeather, Lgame, Lyoutube, Lmusic, Lgallery;
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
        View view = inflater.inflate(R.layout.fragment_feautres, container, false);
        setUpViews(view);
        handleViews();
        initRecyclerView(view);
        showNews();
        return view;
    }

    public void initRecyclerView(View view) {
        newsRecycler = view.findViewById(R.id.newsRecycler);
        newsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsAdapter = new NewsAdapter(newsList, getActivity());
        newsRecycler.setNestedScrollingEnabled(false);
        newsRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        newsRecycler.setAdapter(newsAdapter);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setUpViews(View view) {
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
    //////////////////////////////////////////////////////////////////////////////////////////////

    public void handleViews() {
        //for message goodmorning
        hiUser.setText(setHI());

        LMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMessage(getActivity());
            }
        });

        Lphonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startphonecall(getActivity());
            }
        });

        Ltimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer(getActivity());
            }
        });

        Levent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEvent(getActivity());
            }
        });

        Lremender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRemender(getActivity());
            }
        });

        Lnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNews();
            }
        });

        LWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather(getActivity());
            }
        });

        Lyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYoutube();
            }
        });

        Lgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame();
            }
        });

        Lmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        Lgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void showNews() {
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
                            for (int x = 0; x < jsonData.length(); x++) {

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
                        } catch (Exception ex) {
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
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void displayNews() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://news.google.com/?hl=en-US&gl=US&ceid=US:en")));

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setMessage(Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.msg_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        final EditText to = view.findViewById(R.id.to);
        final EditText message = view.findViewById(R.id.msg);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String phone = to.getText().toString();
                                String msg = message.getText().toString();
                                sendsms(phone, msg);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void sendsms(String phoneNumber, String message) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
     public String setHI() {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.AM_PM) == Calendar.AM) {
            return "Good Morning";
        } else {
            return "Good Evening";
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void phoneCall(int length, Context context) {
        Intent intent =new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+length));
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void startphonecall(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.phonecall_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        final EditText phone = view.findViewById(R.id.call);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                phoneCall(Integer.valueOf(phone.getText().toString()), context);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void startTimer(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.timer_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        final EditText length = view.findViewById(R.id.length);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setTimer(Integer.valueOf(length.getText().toString()), context);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void setTimer(int length, Context context) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Set Timer")
                .putExtra(AlarmClock.EXTRA_LENGTH, length)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void startEvent(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.event_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        final EditText begin = view.findViewById(R.id.begin);
        final EditText end = view.findViewById(R.id.end);
        final EditText title = view.findViewById(R.id.title);
        final EditText country = view.findViewById(R.id.country);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addEvent(title.getText().toString(), country.getText().toString(), Integer.valueOf(begin.getText().toString()), Integer.valueOf(end.getText().toString()), context);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void addEvent(String Title, String Country, int beginTime, int endTime, Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, Title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, Country)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void startRemender(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.remainder_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        final EditText title = view.findViewById(R.id.rTitle);
        final EditText hours = view.findViewById(R.id.rHours);
        final EditText min = view.findViewById(R.id.rMin);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addReminder(title.getText().toString(), Integer.valueOf(hours.getText().toString()), Integer.valueOf(min.getText().toString()), context);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void addReminder(String alarmName, int hours, int miniutes, Context context) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, alarmName)
                .putExtra(AlarmClock.EXTRA_HOUR, hours)
                .putExtra(AlarmClock.EXTRA_MINUTES, miniutes);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void getWeather(final Context context) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Tanta,EG&appid=f5610e8a5c1e5d842551edbc6e5226f8&units=Imperial";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    LayoutInflater li = LayoutInflater.from(context);
                    View view = li.inflate(R.layout.weather_layout, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(view);

                    final TextView T_temp = view.findViewById(R.id.temp);
                    final TextView T_city = view.findViewById(R.id.city);
                    final TextView T_description = view.findViewById(R.id.description);
                    final TextView T_date = view.findViewById(R.id.date);

//                    T_temp.setText(temp);
                    T_city.setText(city);
                    T_description.setText(description);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-EEEE");
                    String date = simpleDateFormat.format(calendar.getTime());

                    T_date.setText(date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) / 1.8000;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    T_temp.setText(String.valueOf(i)+" °");

                    alertDialogBuilder
                            .setCancelable(true)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void playMusic() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public void openYoutube() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")));

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void openGallery() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void playGame() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?client=opera&q=play+a+game&sourceid=opera&ie=UTF-8&oe=UTF-8")));

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
//    @Override
//    public void onActivityResult(int reqCode, int resultCode, Intent data) {
//        super.onActivityResult(reqCode, resultCode, data);
//
//        switch (reqCode) {
//            case (1):
//                if (resultCode == Activity.RESULT_OK) {
//                    Uri contactData = data.getData();
//                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
//                    if (c.moveToFirst()) {
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        // TODO Whatever you want to do with the selected contact name.
//                    }
//                }
//                break;
//        }
//    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
}