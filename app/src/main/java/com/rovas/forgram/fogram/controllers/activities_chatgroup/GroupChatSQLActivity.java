package com.rovas.forgram.fogram.controllers.activities_chatgroup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Views.CircularImageView;
import com.rovas.forgram.fogram.Utils.Emojj.EmojIconActions;
import com.rovas.forgram.fogram.Utils.Emojj.Helper.EmojiconEditText;
import com.rovas.forgram.fogram.Utils.Message_Status;
import com.rovas.forgram.fogram.Utils.download.BasicImageDownloader;
import com.rovas.forgram.fogram.Utils.download.BasicImageUploader;
import com.rovas.forgram.fogram.Utils.download.BasicSoundDownloader;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.managers.chat_DB.DB_SqLite_Chat;
import com.rovas.forgram.fogram.managers.chat_DB.DB_Utils;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.models.Conversation;
import com.rovas.forgram.fogram.models.MessageGroup;
import com.rovas.forgram.fogram.controllers.activities_editor.SingleGroupEditorActivity;
import com.rovas.forgram.fogram.interfaces.OnUploadedCallback;
import com.rovas.forgram.fogram.storage.StorageHandler;
import com.rovas.forgram.fogram.views.MessageGroupSQLAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
/**
 * Created by Mohamed El Sayed
 */
public class GroupChatSQLActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener , View.OnClickListener , TextWatcher , View.OnLayoutChangeListener{

    private static final String TAG = "ChatGroupSQLActivity" ;
    private List<MessageGroup> list_message = new ArrayList<>();
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout text_input;
    private RecyclerView mMessagesList;
    private LinearLayoutManager mLinearLayout;
    private ImageButton btnSend;
    private RecordButton recordButton;
    private EmojiconEditText inputMsg;
    private ImageView image_picker_btn;
    private ImageView emoj_btn;
    EmojIconActions emojIcon;
    private LinearLayout typing_panel;
    View rootView;
    //
    private Toolbar mChatToolbar;
    /*
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    */
    private CircularImageView mPictureView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    //
    private String mChatUser;
    private MessageGroupSQLAdapter mAdapter;
    private String dbName;
    private File yourFile;
    private DB_Utils DBUtils;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private static String mFileImgName = null;
    private StorageReference storageReference;
    //
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String mCurrentUserId;
    private String userName;
    public static DB_SqLite_Chat db_chat;
    private int mCurrentPage = 1;
    private String media_path;
    private ChatGroup chatgroup;
    private String current_user_name;
    private String current_thumb_image;
    //
    public void AddMessages(List<MessageGroup> list) {
        for(int i = 0 ; i < list.size() ; i++) {
            this.list_message.add(db_chat.getMessageGroup(userName,i));
            DBUtils.copyDatabase(dbName);
            mAdapter.notifyDataSetChanged();
        }

    }
    public void AddMessage(long insert_mess) {
            this.list_message.add(db_chat.getMessageGroup(userName , insert_mess));
            DBUtils.copyDatabase(dbName);
            mAdapter.notifyDataSetChanged();


    }
    public void AddImage(String Database_name , String from_name , String message , String position , String FilePath , long Time_stamp , boolean Seen)
    {
        long insert_id = db_chat.insertDataGroup(userName,from_name, message, position, FilePath, null ,Time_stamp , Seen);
        //Database();
        Show_Last_Message(insert_id , Database_name);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgroup_sql);
        // =========================== Firebase ===========================
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        mChatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");
        current_user_name = UserWiazrd.getInstance().getTempUser().getUsername();
        current_thumb_image = UserWiazrd.getInstance().getTempUser().getThumb_image();
        //chatgroup = (ChatGroup) getIntent().getExtras().getSerializable(ChatUI.BUNDLE_GROUP_ID);
        // =========================== Toolbar ===========================
        chatToolbar();
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);

        /*
        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        */
        //
        mPictureView = (CircularImageView) findViewById(R.id.toolbar_picture);
        mTitleTextView = (TextView) findViewById(R.id.toolbar_title);
        mSubTitleTextView = (TextView) findViewById(R.id.toolbar_subtitle);
        // =========================== Firebase =========================
        Query f_query = fStore.collection("groups").whereEqualTo("groupId" , mChatUser);
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        setChatgroup(doc.getDocument().toObject(ChatGroup.class).withid(groupID));
                        initGroupToolbar(getChatgroup());
                    }
                }
            }
        });
        // =========================== Views ===========================
        //typing_panel
        typing_panel = findViewById(R.id.typing_panel);
        text_input = findViewById(R.id.text_input);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        recordButton = (RecordButton) findViewById(R.id.record_button_);
        inputMsg = (EmojiconEditText) findViewById(R.id.inputMsg);
        image_picker_btn = (ImageView) findViewById(R.id.iv_camera);
        rootView = findViewById(R.id.root_view);
        emoj_btn = (ImageView) findViewById(R.id.iv_emoji);
        //Database_Conv();//SYStEM_REMOVED
        //SQLite_Conv();
        // =========================== SQlite ===========================
        Database();
        // =========================== Methods ===========================
        updateRecycleView();
        //getChatFromDatabase();
        getChatFromDatabaseMomery();
        Firebase();
        permissons();
        cFolders();
        record();
        emojs();
        // =========================== Interfaces ===========================
        btnSend.setOnClickListener(this);
        image_picker_btn.setOnClickListener(this);
        inputMsg.addTextChangedListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mMessagesList.addOnLayoutChangeListener(this);//Smooth
    }
    private Map<String , String > initCurrent_SeenMap()
    {
        Map<String , String> seen_Map = new HashMap<>();
        seen_Map.put(mCurrentUserId , "seen");
        return seen_Map;
    }
    private Map<String , String> initSeenMap(ChatGroup chatgroup) {
        Map<String , String> seen_Map = new HashMap<>();
        Iterator it = chatgroup.getMembers().entrySet().iterator();
        for ( Map.Entry<String, String> entry : chatgroup.getMembers().entrySet()) {
            String userId = entry.getKey();
            seen_Map.put(userId , "not_seen");
            // do something with key and/or tab
        }
        return seen_Map;
    }

    private void SQLite_Conv(String UserName ,String  From_ID) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long insert_id = db_chat.insertDataConv( "conversations", UserName, From_ID, "","" ,  Message_Status.DIRECT_CHANNEL_TYPE, timestamp.getTime()  , false , true);
    }

    private void Database() {
        dbName = "msgstore.db";
        DBUtils = new DB_Utils(GroupChatSQLActivity.this);
        yourFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "FutureAcademy"
                + File.separator + "Databases"
                + File.separator + dbName);
        db_chat = new DB_SqLite_Chat(this, dbName, null, 1);
        //db_chat.queryData(Conversation.CREATE_TABLE_EXIST("conversations"));
        db_chat.queryData(MessageGroup.CREATE_TABLE_EXIST(userName));
        FirebaseMessaging.getInstance().subscribeToTopic("future_academy");
        //SQLite_Conv(userName , mChatUser);
    }

    private void permissons() {
        String [] permissions = {Manifest.permission.RECORD_AUDIO , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.CAMERA    };
        int REQUEST_RECORD_AUDIO_PERMISSION = 200;
        int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 201;
        int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 202;
        int REQUEST_CAMERA_PERMISSION = 203;
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
    }

    private void cFolders() {
        File folder = new File(Environment.getExternalStorageDirectory().getPath() +
                File.separator + "FutureAcademy" +
                File.separator + "Media"
        );
        if(!folder.exists()){
            folder.mkdirs();
        }

    }

    private void emojs() {
        emojIcon = new EmojIconActions(this, rootView, inputMsg, emoj_btn);
        emojIcon.ShowEmojIcon();
        emojIcon.setUseSystemEmoji(true);
        inputMsg.setUseSystemDefault(true);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        emojIcon.addEmojiconEditTextList(inputMsg);
    }
    public void scrollToBottom(){
        //mMessagesList.scrollVerticallyTo(0);
    }
    private void updateRecycleView() {
        //RecycleView
        mMessagesList = (RecyclerView) findViewById(R.id.chat_recyclerview);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);

        //mLinearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayout.setStackFromEnd(true);
        //mLinearLayout.setReverseLayout(true);
        //mLinearLayout.setSmoothScrollbarEnabled(true);
        //mLinearLayout.setStackFromEnd(true);
        // linear layout manager stack from last. This can make the task much more easier
        //mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        //mMessagesList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MessageGroupSQLAdapter(list_message);


        mMessagesList.setAdapter(mAdapter);
    }

    private void sendMessage() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final String message = inputMsg.getText().toString();
        if(message.trim().length()==0)
            return;

        if(!TextUtils.isEmpty(message)) {
            String from_name = current_user_name;
            long insert_id = db_chat.insertDataGroup(userName, from_name, message, "0", "", null ,timestamp.getTime() , false);

            //long insert_id = db.insertData(from_name , message , itself , color ,  imageViewToByte(image_picker_btn));
            Show_Last_Message(insert_id);

            HashMap<String, Object> message_map_rec = new HashMap<>();
            message_map_rec.put("message", message);
            message_map_rec.put("group_name", userName);
            message_map_rec.put("seen", false);
            message_map_rec.put("position", "1");
            message_map_rec.put("from_name", current_user_name);
            message_map_rec.put("media_path", "");
            message_map_rec.put("time_stamp", timestamp.getTime());
            message_map_rec.put("members_seen" , initCurrent_SeenMap());
            message_map_rec.put("from", mCurrentUserId);
            fStore.collection("groups").document(mChatUser).collection("messages").add(message_map_rec);
            inputMsg.setText("");
            //updateRecycleView();
            //
        }
    }

    private void chatToolbar() {
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);

        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_group_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

    }

    private void Load_ChatGroup() {
        Query f_query = fStore.collection("groups").whereEqualTo("groupId" , mChatUser);
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        ChatGroup sGroup = doc.getDocument().toObject(ChatGroup.class).withid(groupID);

                    }
                }
            }
        });

    }
    private void initGroupToolbar(final ChatGroup chatGroup ) {
        // toolbar picture
        setPicture(chatGroup.getIconURL(), R.drawable.ic_profile);
        /* //TODO UNDER_MAINF
        if (chatGroup.getMembersList().contains(user)) {
            typing_panel.setVisibility(View.VISIBLE);
        } else {
            typing_panel.setVisibility(View.GONE);
        }
        */
            // group name
            mTitleTextView.setText(chatGroup.getName());

        // toolbar group members
        String groupMembers;
        if ( chatGroup.getMembersList() != null &&
                chatGroup.getMembersList().size() > 0) {
            groupMembers = chatGroup.printMembersListWithSeparator(", ");//TODO
        } else {
            // if there are no members show the logged user as "you"
            groupMembers = getString(R.string.activity_message_list_group_info_you_label);
        }
            mSubTitleTextView.setText(groupMembers);

        mChatToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatSQLActivity.this,
                        GroupAdminPanelActivity.class);
                intent.putExtra(ChatUI.BUNDLE_GROUP_ID, chatGroup);
                startActivity(intent);
            }
        });

    }

    private void setPicture(String pictureUrl, @DrawableRes int placeholder) {
            Glide.with(getApplicationContext())
                    .load(StringUtils.isValid(pictureUrl) ? pictureUrl : "")
                    .into(mPictureView);
    }

    private void loadMessages() {
        //messagesList.clear();//no need to clear . it's only load last 10
        if (mAuth.getCurrentUser() != null) {
            /*
            mMessagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            */
            Query f_query = fStore.collection("groups").document(mChatUser).collection("messages")
                   .whereEqualTo("seen" , false);//limit error [limittofirst]
            //orderBy("time_stamp" , Query.Direction.DESCENDING).
            f_query.addSnapshotListener(GroupChatSQLActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            if(!doc.getDocument().toObject(MessageGroup.class).getFrom().equals(mCurrentUserId)) {
                                String MessagesId = doc.getDocument().getId();
                                Map<String , String > members_seen = doc.getDocument().toObject(MessageGroup.class).getMembers_seen();
                                if (!members_seen.containsKey(mCurrentUserId))
                                {


                                    String from_name = doc.getDocument().toObject(MessageGroup.class).getFrom_name();
                                    String message = doc.getDocument().toObject(MessageGroup.class).getMessage();
                                    String position = doc.getDocument().toObject(MessageGroup.class).getPosition();
                                    String media_path = doc.getDocument().toObject(MessageGroup.class).getMedia_path();
                                    long timestamp = doc.getDocument().toObject(MessageGroup.class).getTime_stamp();

                                    if (!position.equals("1") || !position.equals("0")) {
                                        if (position.equals("7")) {

                                            mFileImgName = Environment.getExternalStorageDirectory().getPath()
                                                    + "/FutureAcademy"
                                                    + "/Media"
                                                    + "/FutureRecords";

                                            mFileImgName += "/" + timestamp + ".3gp";
                                            final BasicSoundDownloader downloader = new BasicSoundDownloader(new BasicSoundDownloader.OnSoundLoaderListener() {
                                                @Override
                                                public void onError(BasicSoundDownloader.SoundError error) {
                                                /*
                                                    Toast.makeText(GroupChatSQLActivity.this, "Error code " + error.getErrorCode() + ": " +
                                                            error.getMessage(), Toast.LENGTH_LONG).show();
                                                    error.printStackTrace();
                                                    */

                                                }

                                                @Override
                                                public void onProgressChange(int percent) {

                                                }

                                                @Override
                                                public void onComplete(String result) {
                                                    //Toast.makeText(GroupChatSQLActivity.this, "Voice Downloaded", Toast.LENGTH_SHORT).show();
                                                    long insert_id = db_chat.insertDataGroup(userName ,from_name, message, position, mFileImgName, null, timestamp, false);
                                                    Show_Last_Message(insert_id);
                                                }


                                            });

                                            downloader.download(media_path, mFileImgName, false);
                                        } else {
                                            final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                                                @Override
                                                public void onError(BasicImageDownloader.ImageError error) {
                                                  /*
                                                    Toast.makeText(GroupChatSQLActivity.this, "Error code " + error.getErrorCode() + ": " +
                                                            error.getMessage(), Toast.LENGTH_LONG).show();
                                                    error.printStackTrace();
                                                    */
                                                }

                                                @Override
                                                public void onProgressChange(int percent) {

                                                }

                                                @Override
                                                public void onComplete(Bitmap result) {
                                                    /* save the image - I'm gonna use JPEG */
                                                    final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                                                    /* don't forget to include the extension into the file name */

                                                    final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                                            + File.separator + "FutureAcademy"
                                                            + File.separator + "Media"
                                                            + File.separator + "FutureImages"
                                                            + File.separator + timestamp + "." + mFormat.name().toLowerCase());

                                                    //

                                                    //mFileName += "/"+ timestamp.getTime()+".3gp";
                                                    // final File myImageFile = new File(Environment.getExternalStorageDirectory().getPath() + "/FutureAcademy" + "/Media"
                                                    //       + File.separator + "FutureImages" + "/"+ File.separator + timestamp + "." + mFormat.name().toLowerCase());

                                                    mFileImgName = Environment.getExternalStorageDirectory().getPath()
                                                            + "/FutureAcademy"
                                                            + "/Media"
                                                            + "/FutureImages";

                                                    mFileImgName += "/" + timestamp + "." + mFormat.name().toLowerCase();
                                                    //
                                                    BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                                                        @Override
                                                        public void onBitmapSaved() {
                                                            //Toast.makeText(GroupChatSQLActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                                                          /*
                                                            Toast.makeText(GroupChatSQLActivity.this, "Error code " + error.getErrorCode() + ": " +
                                                                    error.getMessage(), Toast.LENGTH_LONG).show();
                                                            error.printStackTrace();
                                                            */
                                                        }


                                                    }, mFormat, false);


                                                }
                                            });

                                            downloader.download(media_path, false);
                                            long insert_id = db_chat.insertDataGroup(userName , from_name, message, position, mFileImgName, null, timestamp, false);
                                            Show_Last_Message(insert_id);
                                        }
                                    } else {
                                        long insert_id = db_chat.insertDataGroup(userName ,from_name, message, position, "", null, timestamp, false);
                                        Show_Last_Message(insert_id);
                                    }
                                    members_seen.put(mCurrentUserId , "seen");
                                    HashMap<String, Object> MessageMap_ = new HashMap<>();
                                    MessageMap_.put("members_seen", members_seen);
                                    fStore.collection("groups").document(mChatUser).collection("messages").document(MessagesId).update(MessageMap_);
                                    //playBeep();
                                }

                            }
                        }
                    }
                }
            });

        }


    }

    public void playBeep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected enum ListPosition {
        Top, Current, Bottom
    }
    public void scrollListTo(final int position, final boolean animated) {
        listPos = position;

        if (animated) {
            mMessagesList.smoothScrollToPosition(listPos);
        }
        else {
            mMessagesList.getLayoutManager().scrollToPosition(listPos);
        }
    }
    protected int listPos = -1;
    public void scrollListTo(final ListPosition position, final boolean animated) {

        int pos = 0;

        switch (position) {
            case Top:
                pos = 0;
                break;
            case Current:
                pos = listPos == -1 ? mAdapter.getItemCount() - 1 : listPos;
                break;
            case Bottom:
                pos = mAdapter.getItemCount() - 1;
                break;
        }

        scrollListTo(pos, animated);
    }

    public void Show_Last_Message(long id)
    {
        /*
        MessageGroup firstMessage = null;
        if(list_message.size() > 0) {
            firstMessage = list_message.get(0);
        }
        int oldCount = mAdapter.getItemCount();
        */
        list_message.add( 0 , db_chat.getMessageGroup(userName,id)); // Fake Position
        DBUtils.copyDatabase(dbName);
        mAdapter.notifyDataSetChanged();

        mMessagesList.scrollToPosition(-1);


    }
    public void Show_Last_Image(long id)
    {
        list_message.add(0 , db_chat.getMessageGroup(userName,id));
        try {
            DBUtils.copyDatabase(dbName);
            mAdapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Show_Last_Image: " + e);
        }



    }
    public void Show_Last_Message(long id , String Database_name)
    {
        list_message.add(db_chat.getMessageGroup(userName,id));
        //DB_Utils utils_;
        //utils_ = new DB_Utils(ChatSQLActivity.this);
        //DBUtils.copyDatabase(Database_name);
        //mAdapter.notifyDataSetChanged();


    }
    public void getChatFromDatabase()
    {

            list_message.addAll(db_chat.getAllrecordGroup(userName));
            mAdapter.notifyDataSetChanged();
            loadMessages();


    }
    public void getChatFromDatabaseMomery()
    {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "FutureAcademy"
                + File.separator + "Databases"
                + File.separator + dbName);
        if(file.exists()) {
            if(db_chat.getAllrecordFromMomeryGroup(this,userName ,yourFile).size() > 0)
            {
                list_message.addAll(db_chat.getAllrecordFromMomeryGroup(this,userName ,yourFile));
                mAdapter.notifyDataSetChanged();
                loadMessages();
                //Toast.makeText(this, "Load Done", Toast.LENGTH_SHORT).show();
            }
            else
            {
                getChatFromDatabase();
            }
        }
        else
        {
            getChatFromDatabase();
        }



    }
    public void Show_More_Messages(int CurrentPage)
    {
        list_message.addAll(db_chat.getMorerecordGroup(userName,CurrentPage));
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.setRefreshing(false);


    }

    @Override
    public void onRefresh() {
        mCurrentPage++;
        Show_More_Messages(mCurrentPage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(GroupChatSQLActivity.this, 100, 5);
                } else {
                    //Toast.makeText(ChatActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    //TODO
    private void Firebase() {
        // ----------------------------
        fStore.collection("chat").document(mCurrentUserId).collection("with").document(mChatUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        fStore.collection("groups").document(mChatUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String user_name = task.getResult().getString("name");
                                String thumb_image = task.getResult().getString("iconURL");
                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                // ------------------------------------------------------
                                HashMap<String, Object> online_other_user = new HashMap<>();
                                online_other_user.put("seen", false);
                                online_other_user.put("time_stamp", timestamp.getTime());
                                online_other_user.put("message" , "");
                                online_other_user.put("channelType" , Message_Status.GROUP_CHANNEL_TYPE);
                                online_other_user.put("thumb_image" , thumb_image);
                                online_other_user.put("from_id", mChatUser);
                                online_other_user.put("members_seen" , initCurrent_SeenMap());
                                online_other_user.put("from_name", user_name);
                                // -------------------------------------------------------
                                fStore.collection("chat").document(mCurrentUserId).collection("with").document(mChatUser).set(online_other_user);

                            }
                        });

                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_camera:
            {
                Intent intent1 = new Intent(GroupChatSQLActivity.this, SingleGroupEditorActivity.class);
                intent1.putExtra("to_user_id", mChatUser);
                intent1.putExtra("database_name", dbName);
                //startActivity(intent1);
                startActivityForResult(intent1, ChatUI.REQUEST_CODE_EDITED_IMAGE);
                break;
            }
            case R.id.btnSend:
            {
                sendMessage();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        //mChatSendBtn.goToState(text.length() == 0 ? AnimButton.FIRST_STATE : AnimButton.SECOND_STATE);
        //Toast.makeText(ChatActivity.this, "After", Toast.LENGTH_SHORT).show();
        if(text.length() != 0)
        {
            recordButton.setVisibility(GONE);
        }
        else
        {
            recordButton.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(view == mMessagesList) {
            if (bottom < oldBottom) {
                mMessagesList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMessagesList.smoothScrollToPosition(0);
                    }
                }, 100);
            }
        }
    }


    private void record() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //mFileName = getExternalCacheDir().getAbsolutePath();
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/FutureAcademy" + "/Media"
                + File.separator + "FutureRecords"
        );
        mFileName = Environment.getExternalStorageDirectory().getPath()
                + "/FutureAcademy"
                + "/Media"
                +"/FutureRecords";
        if(!folder.exists()){
            folder.mkdirs();
        }
        mFileName += "/"+ timestamp.getTime()+".3gp";

        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        //IMPORTANT
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button_);
        recordButton.setRecordView(recordView);

        // if you want to click the button (in case if you want to make the record button a Send Button for example..)
//        recordButton.setListenForRecord(false);



        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChatActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });


        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 25
        recordView.setCancelBounds(30);


        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);


        recordView.setSlideToCancelText("Slide To Cancel");


        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                Log.d("RecordView", "onStart");
                text_input.setVisibility(INVISIBLE);
                startRecording();
                //Toast.makeText(ChatActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                //Toast.makeText(ChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();

                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime) {

                String time = getHumanTimeText(recordTime);
                //Toast.makeText(ChatSQLActivity.this, "onFinishRecord - Recorded Time is: " + time, Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onFinish");
                stopRecording();
                uploadAudio();
                Log.d("RecordTime", time);
                text_input.setVisibility(VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                //Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                stopRecording();
                text_input.setVisibility(VISIBLE);
                Log.d("RecordView", "onLessThanSecond");
            }
        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });
    }

    private void uploadAudio() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String from_name = current_user_name;
        long insert_id = db_chat.insertDataGroup(userName,from_name, "Audio", "6", mFileName, null ,timestamp.getTime() , false);
        Show_Last_Message(insert_id);
        final String randomname = UUID.randomUUID().toString();
        StorageReference filePath = storageReference.child("Audio").child(randomname+".3gp");
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();
        Uri uri = Uri.fromFile(new File(mFileName));
        filePath.putFile(uri , metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String firebaseUrl = taskSnapshot.getDownloadUrl().toString();
                HashMap<String , Object> message_map_rec = new HashMap<>();
                message_map_rec.put("message", "");
                message_map_rec.put("media_path", firebaseUrl);
                message_map_rec.put("group_name", userName);
                message_map_rec.put("seen", false);
                message_map_rec.put("type" , "record");
                message_map_rec.put("position" , "7");
                message_map_rec.put("from_name", current_user_name);
                message_map_rec.put("time_stamp" , timestamp.getTime());
                message_map_rec.put("members_seen" , initCurrent_SeenMap());
                message_map_rec.put("from", mCurrentUserId);
                fStore.collection("groups").document(mChatUser).collection("messages").add(message_map_rec);
            }
        });
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public String getMedia_path() {
        return media_path;
    }

    public void setMedia_path(String media_path) {
        this.media_path = media_path;
    }


    public void AddImage(String filePath, byte[] thumb_data, StorageReference imageRef, String user_id, String to_user_id , long time_stamp) {
        final BasicImageUploader uploader = new BasicImageUploader(new BasicImageUploader.OnImageLoaderListener() {


            @Override
            public void onError(BasicImageUploader.ImageError error) {
               /*
                Toast.makeText(GroupChatSQLActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                */
            }

            @Override
            public void onProgressChange(int percent) {
                //finish();
            }

            @Override
            public void onComplete(String result) {
                //Toast.makeText(GroupChatSQLActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                //Done
            }


        });

        uploader.upload(GroupChatSQLActivity.this , thumb_data , imageRef , user_id , to_user_id , false);
        long insert_id = db_chat.insertDataGroup(userName,current_user_name, "", "2", filePath, null ,time_stamp , false);
        //Database();
        Show_Last_Image(insert_id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DBUtils.copyDatabase(dbName);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBUtils.copyDatabase(dbName);
    }
    public void uploadFile(File file) {
        Log.d(TAG, "uploadFile");

        // bugfix Issue #45
        final ProgressDialog progressDialog = new ProgressDialog(GroupChatSQLActivity.this);
        progressDialog.setMessage(getString(R.string.activity_message_list_progress_dialog_upload));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageHandler.uploadFile(this, file, new OnUploadedCallback() {
            @Override
            public void onUploadSuccess(final String uid, final Uri downloadUrl, final String type) {
                Log.d(TAG, "uploadFile.onUploadSuccess - downloadUrl: " + downloadUrl);

                progressDialog.dismiss(); // bugfix Issue #45
                // ================== ==================
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                // ========================================================================================
                HashMap<String, Object> message_map_rec = new HashMap<>();
                message_map_rec.put("message", "image");
                message_map_rec.put("media_path", downloadUrl.toString());
                message_map_rec.put("group_name", userName);
                message_map_rec.put("seen", false);
                message_map_rec.put("type", "image");
                message_map_rec.put("position", "3");
                message_map_rec.put("from_name", current_user_name);
                message_map_rec.put("time_stamp", timestamp.getTime());
                message_map_rec.put("members_seen" , initCurrent_SeenMap());
                message_map_rec.put("from", mCurrentUserId);
                fStore.collection("groups").document(mChatUser).collection("messages").add(message_map_rec);
            }

            @Override
            public void onProgress(double progress) {
                Log.d(TAG, "uploadFile.onProgress - progress: " + progress);

                // bugfix Issue #45
                progressDialog.setProgress((int) progress);

                // TODO: 06/09/17 progress within viewholder
            }

            @Override
            public void onUploadFailed(Exception e) {
                Log.e(TAG, "uploadFile.onUploadFailed: " + e.getMessage());

                progressDialog.dismiss(); // bugfix Issue #45

                Toast.makeText(GroupChatSQLActivity.this,
                        getString(R.string.activity_message_list_progress_dialog_upload_failed),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ChatUI.REQUEST_CODE_EDITED_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra(ChatUI.BUNDLE_IMAGE_EDIT);
                Uri file = Uri.fromFile(new File(result));
                File new_image_file = new File(file.getPath());
                Timestamp time_stamp = new Timestamp(System.currentTimeMillis());
                long insert_id = db_chat.insertDataGroup(userName,current_user_name, "", "2", result, null ,time_stamp.getTime() , false);
                Show_Last_Image(insert_id);
                uploadFile(new_image_file);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public ChatGroup getChatgroup() {
        return chatgroup;
    }

    public void setChatgroup(ChatGroup chatgroup) {
        this.chatgroup = chatgroup;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
