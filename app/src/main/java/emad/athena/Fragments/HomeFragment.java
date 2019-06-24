package emad.athena.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.AuthResult;
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
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import emad.athena.Adapters.ChatAdapter;
import emad.athena.LoginActivity;
import emad.athena.Model.App;
import emad.athena.Model.ArabicOCR;
import emad.athena.Model.Chat;
import emad.athena.Model.ChatbotResponse;
import emad.athena.Model.Question;
import emad.athena.Model.Recent;
import emad.athena.Model.User;
import emad.athena.R;
import emad.athena.Tools.Helper;
import emad.athena.VolleyUtils.MySingleton;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Helper helper = new Helper();
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private DatabaseReference questionsReference;
    private DatabaseReference stopWordsReference;
    public ArrayList<String> stopWordsList = new ArrayList<>();
    private DatabaseReference presavedQuestionsRef;
    private User user;
    Recent recentQuestion;
    private String profilePic = "https://firebasestorage.googleapis.com/v0/b/intellij-4dd19.appspot.com/o/users%2Fdefault.png?alt=media&token=04243d9a-c714-46a4-98bd-6a58f585e13d";
    private ChatAdapter chatAdapter;

    RecyclerView chatRecycler;
    EditText voiceEditText;
    RelativeLayout homePageFragment;
    String voice;
    ImageView speechIcon;

    SpeechRecognizer speechRecognizer;
    Intent intentSpeech;


    //constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int READ_IN_ARABIC = 1005;
    String cameraPermission[];
    String storagePermission[];

    //ocr variables
    Uri image_uri;
    ImageView mPreviewIv;
    Bitmap bitmapArabicOcr;

    // image recognition vars
    StorageReference storageRef;
    int SEND_RECOGNIZE_IMAGE_REQUEST = 1;
    int SEND_ADD_IMAGE_REQUEST = 0;
    Bitmap bitmapRecognition;
    String imageNameRecognition;
    String imgURLRecognition;
    String personName = null;
    String modifiedPersonName = "";
    int flagImage; // if 0 add  , if 1 recognize

    // Lists
    ArrayList<Chat> chatList = new ArrayList<>();
    ArrayList<Question> presavedQuestions = new ArrayList(); // get Data from class PresavedQuestions
    ArrayList<App> allApps;
    boolean isConnected;
    int flagMicOrText = 1;// 1 for mic , 0 for text input

    ChatbotResponse chatbotResponse = new ChatbotResponse();

    private static final String TAG = "HomeFragment";
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        startVoiceInput();
        initRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            startActivity(new Intent(getContext(), LoginActivity.class));
        else
            getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        initLists();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public void getCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onDataChange: *********************************");
                Log.d(TAG, "onDataChange: " + user.getFirebaseID());
                Log.d(TAG, "onDataChange: " + user.getName());
                Log.d(TAG, "onDataChange: " + user.getMail());
                Log.d(TAG, "onDataChange: " + user.getPictureURL());
                profilePic = user.getPictureURL();
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void initViews(View view){
        mPreviewIv = view.findViewById(R.id.mPreviewIv);
        chatRecycler = view.findViewById(R.id.chatRecycler);
        speechIcon = view.findViewById(R.id.speechIcon);
        voiceEditText = view.findViewById(R.id.voiceEditText);

        voiceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(voiceEditText.getText().toString().trim().equals("")){
                    speechIcon.setImageResource(R.drawable.ic_mic_black_24dp);
                    flagMicOrText = 1;
                }else {
                    speechIcon.setImageResource(R.drawable.ic_send_black_24dp);
                    flagMicOrText = 0;
                }
            }
        });

        speechIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for input voice and make action in onActivityResult func
                if (flagMicOrText == 1) {
                    listen();
                } else if (flagMicOrText == 0) {
                    voice = voiceEditText.getText().toString().trim();
                    if (!voice.equals("")) {
                        chatList.add(new Chat(voice, 1, null, profilePic));
                        String answer = getNlpAnswerTest(filterQuestion(voice), allApps, isConnected);
                        if(!answer.equals("wait")){

                            chatList.add(new Chat(answer, 0, null, profilePic));
                            Log.d(TAG, "onResults VOICE : " + voice);
                            Log.d(TAG, "onResults Answer : " + answer);
                            chatAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(chatList.size()-1);
                        }
                        voiceEditText.setText("");

                        recentQuestion = new Recent(voice,answer,helper.getDate(),false);
                        SendQuetionToFirebase(recentQuestion);
                    }
                }
            }
        });
    }

    public void initLists(){
        presavedQuestions.clear();
        presavedQuestionsRef = FirebaseDatabase.getInstance().getReference().child("presavedQuestions");
        presavedQuestionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                   presavedQuestions.add(snapshot.getValue(Question.class));
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        stopWordsReference = FirebaseDatabase.getInstance().getReference().child("stopwords");
        stopWordsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: " + snapshot);
                    stopWordsList.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                allApps = getAllApps();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ArrayList<App> getAllApps() {
        ArrayList<App> names = new ArrayList<App>();

        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            names.add(new App(p.applicationInfo.loadLabel(getContext().getPackageManager()).toString(), p.packageName));

        }
        return names;
    }

    public void initRecyclerView() {
        chatList = new ArrayList<>();

        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(getActivity(), chatList);
        chatRecycler.setAdapter(chatAdapter);
        chatRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                voiceEditText.clearFocus();
                flagMicOrText=1;
                return true;
            }
        });
    }

    // speech Recognizer
    private void startVoiceInput() {
        checkAudioPermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext().getApplicationContext());
        intentSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                chatList.add(new Chat("Sorry , i can`t hear you", 0, null, profilePic));
                chatAdapter.notifyDataSetChanged();
                chatRecycler.smoothScrollToPosition(chatList.size()-1);
            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> list = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (list != null) {

                    if (!list.get(0).trim().equals("")) {
                        chatList.add(new Chat(list.get(0), 1, null, profilePic));
                        voice = list.get(0);
                        String answer = getNlpAnswerTest(filterQuestion(voice), allApps, isConnected);
                        if (answer.equals("wait")){

                            chatList.add(new Chat("Connecting To Server ...", 0, null, profilePic));
                            chatAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(chatList.size()-1);                            recentQuestion = new Recent(voice,answer,helper.getDate(),false);
                            SendQuetionToFirebase(recentQuestion);
                        }else {
                            chatList.add(new Chat(answer, 0, null, profilePic));
                            Log.d(TAG, "onResults: VOICE " + voice);
                            Log.d(TAG, "onResults: Answer " + answer);
                            chatAdapter.notifyDataSetChanged();
                            chatRecycler.smoothScrollToPosition(chatList.size()-1);
                            recentQuestion = new Recent(voice,answer,helper.getDate(),false);
                            SendQuetionToFirebase(recentQuestion);
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    public void listen() {
        Log.d(TAG, "listen: " + "LISTEN METHOD");
        speechIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //  voiceInput.setText("You will see text here");
                        //  voiceInput.setTextColor(Color.GRAY);
                        speechRecognizer.stopListening();

                        break;
                    case MotionEvent.ACTION_DOWN:
                        //   voiceInput.setText("Listening");
                        //  voiceInput.setTextColor(Color.GRAY);
                        speechRecognizer.startListening(intentSpeech);
                        break;
                }

                return false;
            }
        });
    }

    private void checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {

                    new AlertDialog.Builder(getContext())
                            .setTitle("Title")
                            .setMessage("This message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 101);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 101);


                }

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Not Granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeStorageAccepted) {
                    pickCamera();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickGallry();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                readEnglishPage(data.getData());

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                readEnglishPage(image_uri);

            } else if (requestCode==READ_IN_ARABIC){
                try {
                    bitmapArabicOcr = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData() );
                    uploadOcrImage(data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == SEND_ADD_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                flagImage = 0;
                final Uri uri = data.getData();
                Log.d(TAG, "onActivityResult: " + getContext().getContentResolver().getType(uri));

                final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.person_name_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

                final EditText editTextPersonName = dialog.findViewById(R.id.editTextPersonName);
                Button btnOK = dialog.findViewById(R.id.okDialog);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        personName = editTextPersonName.getText().toString();
                        dialog.dismiss();
                        try {
                            bitmapRecognition = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            addPersonToFirebase();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();


            } else if (requestCode == SEND_RECOGNIZE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                flagImage = 1;
                Uri uri = data.getData();
                Log.d(TAG, "onActivityResult: " + getContext().getContentResolver().getType(uri));
                try {
                    bitmapRecognition = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    addPersonToFirebase();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    // ocr methods
    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickGallry() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getContext());
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallry();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public void readEnglishPage(Uri uri) {
        StringBuilder sb = new StringBuilder();
        mPreviewIv.setImageURI(uri);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        TextRecognizer recognizer = new TextRecognizer.Builder(getContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);

            for (int i = 0; i < items.size(); i++) {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }
            Log.d(TAG, "onActivityResult sb : " + sb.toString());
            chatList.add(new Chat("read page", 1, uri, profilePic));
            chatList.add(new Chat(sb.toString(), 0, null, profilePic));
            chatAdapter.notifyDataSetChanged();
            chatRecycler.smoothScrollToPosition(chatList.size()-1);
        }
    }

    // arabic ocr
    private void uploadOcrImage(final Uri imgUri) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapArabicOcr.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        imageNameRecognition = timeStamp + ".jpg";

        storageRef = FirebaseStorage.getInstance().getReference().child("ocr");
        final UploadTask uploadTask = storageRef.child(imageNameRecognition).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: FAILED ************************************");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d(TAG, "onSuccess: SUCCESS *****************************");
                storageRef = FirebaseStorage.getInstance().getReference().child("ocr/").child(imageNameRecognition);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: --------------" + uri.toString());
                        imgURLRecognition = uri.toString();
                        chatList.add(new Chat("read page",1,imgUri,profilePic));
                        chatAdapter.notifyDataSetChanged();
                        chatRecycler.smoothScrollToPosition(chatList.size()-1);                        readArabicPage(imgURLRecognition);
                    }
                });
            }
        });


    }

    public void readArabicPage(final String imgUrl) {

        Log.d(TAG, "readArabicPage: ");
        chatbotResponse = new ChatbotResponse();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://102927.c.time4vps.cloud/assistant/ocr",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArabicOCR arabicOCR = new Gson().fromJson(response, ArabicOCR.class);
                        String res = "";
                        for (int x = 0; x <arabicOCR.getText().length ; x++) {
                            res+=arabicOCR.getText()[x];
                        }
                        chatList.add(new Chat(res, 0, null, profilePic));
                        chatAdapter.notifyDataSetChanged();
                        chatRecycler.smoothScrollToPosition(chatList.size()-1);                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getChatbotAnswer: " + error.getMessage());
                Log.d(TAG, "getChatbotAnswer: " + error);
                Log.d(TAG, "getChatbotAnswer: " + error.networkResponse);
                Log.d(TAG, "getChatbotAnswer: " + error.getLocalizedMessage());
                Log.d(TAG, "getChatbotAnswer: " + error.getNetworkTimeMs());
                chatList.add(new Chat("try later", 0, null, profilePic));
                chatAdapter.notifyDataSetChanged();
                chatRecycler.smoothScrollToPosition(chatList.size()-1);            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("img", imgUrl);
                params.put("auth", "GxsQXvHY5XMo@4%");
                params.put("lang", "ara");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInsance(getContext()).addToRequestQueue(stringRequest);
    }

    // NLP
    public String filterQuestion(String question) {
        String filteredQuestion = "";
        if (question.startsWith("who is") || question.startsWith("what is")
                || question.startsWith("when") || question.startsWith("how")) {
            return question;
        }

        String[] tokens = question.split(" ");
        for (int x = 0; x < tokens.length; x++) {
            for (int y = 0; y < stopWordsList.size(); y++) {
                if (!(stopWordsList.get(y).equals(tokens[x]))) {
                    filteredQuestion += tokens[x] + " ";
                    break;
                }
            }
        }
        Log.d(TAG, "filterQuestion: " + filteredQuestion);
        return filteredQuestion;
    }

    public String getNlpAnswerTest(String filterdQuestion, ArrayList<App> allApplicationNames, boolean isConnected) {
        Intent intentLauncher;
        String answer = "";

        // first check if question in preserved questions like how are you
        //get Data from class PresavedQuestions
        for (int x = 0; x < presavedQuestions.size(); x++) {
            if (presavedQuestions.get(x).getQuestion().toLowerCase().contains(filterdQuestion.toLowerCase())
                    || presavedQuestions.get(x).getQuestion().toLowerCase().equals(filterdQuestion.toLowerCase())
                    || filterdQuestion.toLowerCase().contains(presavedQuestions.get(x).getQuestion().toLowerCase())) {
                answer = presavedQuestions.get(x).getAnswer();
                return answer;
            }
        }

        // check if he want to add image to db
        if (filterdQuestion.contains("add person")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SEND_ADD_IMAGE_REQUEST);
            return "select image";
        }

        // check if he want to recognize image from db
        else if (filterdQuestion.contains("recognize person")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SEND_RECOGNIZE_IMAGE_REQUEST);
            return "wait for moments";
        }

        else if ((filterdQuestion.startsWith("play")) || (filterdQuestion.startsWith("listen")) || (filterdQuestion.startsWith("hear"))) {
            answer = "Okay, we are working";
            try {
                intentLauncher = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + filterdQuestion.split(" ")[1]));
                startActivity(intentLauncher);
            } catch (Exception e) {
                answer = "This app isn`t on your phone /n , give me more Details";
            }
        } else if (filterdQuestion.startsWith("open") || filterdQuestion.startsWith("go to") || filterdQuestion.startsWith("start")) {
            try {
                String appName = filterdQuestion.split(" ")[1];
                Log.d(TAG, "getNlpAnswerTest: App Name " + appName);
                App app = null;
                for (int x = 0; x < allApplicationNames.size(); x++) {
                    if (allApplicationNames.get(x).getAppName().toLowerCase().equals(appName.toLowerCase())) {
                        app = allApplicationNames.get(x);
                    }
                }

                if (app.getPackageName() != null) {
                    intentLauncher = getActivity().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                    startActivity(intentLauncher);
                    answer = "Okay, we are working";
                } else {
                    Log.d(TAG, "getNlpAnswer: " + "Problem " + appName);
                    Log.d(TAG, "getNlpAnswerTest: " + allApps.size());
                    answer = "I Can`t recognize this app";
                }
                // intent to open app object (you can get package name from app object) line 190
            } catch (Exception ex) {
                answer = "I Can`t recognize this app";
            }
        } else if (filterdQuestion.startsWith("read page") || filterdQuestion.startsWith("read image") || filterdQuestion.contains("convert image") || filterdQuestion.contains("translate image")) {
            int flagFoundAr =0;
            String[] filterdList = filterdQuestion.split(" ");
            for (int ar = 0; ar <filterdList.length ; ar++) {
                if (filterdList[ar].equals("arab") || filterdList[ar].equals("arabic")|| filterdList[ar].equals("ar")){

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, READ_IN_ARABIC);
                    flagFoundAr = 1;
                }
            }
            if (flagFoundAr==0)
                showImageImportDialog();

            answer = "Select Image";

        } else if ((filterdQuestion.startsWith("search")) || (filterdQuestion.startsWith("find")) || (filterdQuestion.startsWith("show"))) {
            String filteredQuery = "";
            String[] tokens = filterdQuestion.split(" ");
            for (int x = 1; x < tokens.length; x++) {
                filteredQuery += tokens[x] + " ";
            }
            answer = "Okay, sir I`m Working";
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, filteredQuery);
            startActivity(intent);

        } else {

            getChatbotAnswer(filterdQuestion);
            answer = "wait";
        }

        Log.d(TAG, "getNlpAnswerTest: ANSWER " + answer);
        return answer;
    }

    public void getChatbotAnswer(final String question) {
        chatbotResponse = new ChatbotResponse();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://102927.c.time4vps.cloud/assistant/chat",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        chatbotResponse =new Gson().fromJson(response, ChatbotResponse.class);
                        chatList.add(new Chat(chatbotResponse.getReply(), 0, null, profilePic));
                        chatAdapter.notifyDataSetChanged();
                        chatRecycler.smoothScrollToPosition(chatList.size()-1);                        recentQuestion = new Recent(voice,chatbotResponse.getReply(),helper.getDate(),false);
                        SendQuetionToFirebase(recentQuestion);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getChatbotAnswer: " + error.getMessage());
                Log.d(TAG, "getChatbotAnswer: " + error);
                Log.d(TAG, "getChatbotAnswer: " + error.networkResponse);
                Log.d(TAG, "getChatbotAnswer: " + error.getLocalizedMessage());
                Log.d(TAG, "getChatbotAnswer: " + error.getNetworkTimeMs());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("msg", question);
                params.put("auth", "GxsQXvHY5XMo@4%");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInsance(getContext()).addToRequestQueue(stringRequest);
    }

    public void SendQuetionToFirebase(Recent rQuestion){
        questionsReference = FirebaseDatabase.getInstance().getReference().child("Recent").child(mAuth.getCurrentUser().getUid()).push();
        rQuestion.setFirebaseid(questionsReference.getKey());
        questionsReference.setValue(rQuestion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: question added to firebase" );
                }else {
                    Log.d(TAG, "onComplete: task failed" );
                }
            }
        });
    }

    // Face Recognition
    private void addPersonToFirebase() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapRecognition.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        imageNameRecognition = timeStamp + ".jpg";

        storageRef = FirebaseStorage.getInstance().getReference().child("add");
        final UploadTask uploadTask = storageRef.child(imageNameRecognition).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: FAILED ************************************");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d(TAG, "onSuccess: SUCCESS *****************************");
                storageRef = FirebaseStorage.getInstance().getReference().child("add/").child(imageNameRecognition);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: --------------" + uri.toString());
                        imgURLRecognition = uri.toString();
                        if (flagImage == 0) {
                            addImageToServer(personName);
                        } else if (flagImage == 1) {
                            recognizeImage(imgURLRecognition);
                        }
                    }
                });
            }
        });


    }

    public void addImageToServer(String personName) {
        // modify person name
        String[] name = personName.split(" ");
        for (int i = 0; i < name.length; i++) {
            modifiedPersonName += name[i].substring(0, 1).toUpperCase() + name[i].substring(1) + " ";
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://athena-assistant.herokuapp.com/assistant/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: RESPONSE ADD " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: " + error);
                Log.d(TAG, "onErrorResponse: " + error.networkResponse);
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                Log.d(TAG, "onErrorResponse: " + error.getNetworkTimeMs());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", modifiedPersonName);
                params.put("imgUrl", imgURLRecognition);
                params.put("auth", "GxsQXvHY5XMo@4%");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInsance(getContext()).addToRequestQueue(stringRequest);
    }

    public String recognizeImage(final String mImageURL) {
        final String[] requestResponse = {null};
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://athena-assistant.herokuapp.com/assistant/recognize",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: RESPONSE Recognize " + response);
//                        responseV.setText(response);
                        requestResponse[0] = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: " + error);
                Log.d(TAG, "onErrorResponse: " + error.networkResponse);
                Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                Log.d(TAG, "onErrorResponse: " + error.getNetworkTimeMs());
                requestResponse[0] = error.getMessage();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("imgUrl", mImageURL);
                params.put("auth", "GxsQXvHY5XMo@4%");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInsance(getContext()).addToRequestQueue(stringRequest);
        return requestResponse[0];
    }
}
