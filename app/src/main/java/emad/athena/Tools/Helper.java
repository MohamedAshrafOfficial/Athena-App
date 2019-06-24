package emad.athena.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import emad.athena.Model.Recent;
import emad.athena.R;

public class Helper extends AppCompatActivity {
    public String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public String setHI() {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.AM_PM) == Calendar.AM) {
           return "Good Morning";
        } else {
            return "Good Evening";
        }
    }

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
    public void sendsms(String phoneNumber, String message) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    public void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (1) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        // TODO Whatever you want to do with the selected contact name.
                    }
                }
                break;
        }
    }

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
                                setTimer(Integer.valueOf(length.getText().toString()),context);

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
    public void setTimer(int length, Context context) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Set Timer")
                .putExtra(AlarmClock.EXTRA_LENGTH, length)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }


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
                                addEvent(title.getText().toString(), country.getText().toString(), Integer.valueOf(begin.getText().toString()), Integer.valueOf(end.getText().toString()),context);

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
    public void addEvent(String Title, String Country, int beginTime, int endTime,Context context) {
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
                                addReminder(title.getText().toString(), Integer.valueOf(hours.getText().toString()), Integer.valueOf(min.getText().toString()),context);

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
    public void addReminder(String alarmName, int hours, int miniutes, Context context) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, alarmName)
                .putExtra(AlarmClock.EXTRA_HOUR, hours)
                .putExtra(AlarmClock.EXTRA_MINUTES, miniutes);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }

    }

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

                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd-MM-EEEE");
                    String date=simpleDateFormat.format(calendar.getTime());

                    T_date.setText(date);

                    double temp_int=Double.parseDouble(temp);
                    double centi=(temp_int-32)/1.8000;
                    centi=Math.round(centi);
                    int i =(int)centi;
                    T_temp.setText(String.valueOf(i));

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

    public void playMusic() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
        startActivity(intent);
    }

    public void openYoutube() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")));

    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void playGame() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?client=opera&q=play+a+game&sourceid=opera&ie=UTF-8&oe=UTF-8")));

    }


}