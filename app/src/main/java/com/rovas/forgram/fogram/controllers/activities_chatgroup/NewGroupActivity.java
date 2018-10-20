package com.rovas.forgram.fogram.controllers.activities_chatgroup;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.File.FilePaths;
import com.rovas.forgram.fogram.Utils.MediaSelector;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.Utils.Views.CircularImageView;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.interfaces.ChatGroupCreatedListener;
import com.rovas.forgram.fogram.managers.chat_DB.DB_SqLite_Chat;
import com.rovas.forgram.fogram.managers.chat_DB.DB_Utils;
import com.rovas.forgram.fogram.models.Conversation;
import com.rovas.forgram.fogram.managers.GroupBuilder;
import com.rovas.forgram.fogram.Utils.Message_Status;
import com.rovas.forgram.fogram.managers.WizardNewGroup;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.exception.ChatRuntimeException;
import com.rovas.forgram.fogram.models.MessageGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static com.rovas.forgram.fogram.Utils.DebugConstants.DEBUG_GROUPS;


/**
 * Created by Mohamed El Sayed
 */

public class NewGroupActivity extends BaseActivity {
    private GroupBuilder groupBuilder;
    private EditText groupNameView;
    private CircularImageView mGroupImage;
    private MenuItem actionNextMenuItem;
    //ImageChooser
    protected MediaSelector mediaSelector = new MediaSelector();
    private Bitmap compressedImageFile;
    private File new_image_file;
    //FireBase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private String current_user_id;
    //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_new_group);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupBuilder = new GroupBuilder(current_user_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Database();
        groupNameView = findViewById(R.id.group_name);
        mGroupImage = (CircularImageView) findViewById(R.id.group_icon);
        groupNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (actionNextMenuItem != null) {
                    if (isValidGroupName() && actionNextMenuItem != null) {
                        WizardNewGroup.getInstance().getTempChatGroup().setName(groupNameView.getText().toString());
                        actionNextMenuItem.setVisible(true);
                    } else {
                        actionNextMenuItem.setVisible(false);
                    }
                }
            }
        });
        mGroupImage.setOnClickListener(view -> mediaSelector.startChooseImageActivity(NewGroupActivity.this, MediaSelector.CropType.Circle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                uploadFile(getNew_image_file());
            }
            catch (Exception e) {
                Toast.makeText(NewGroupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }
    private File yourFile;
    private DB_Utils DBUtils;
    public static DB_SqLite_Chat db_chat;
    private void Database() {
        DBUtils = new DB_Utils(NewGroupActivity.this);
        yourFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "FutureAcademy"
                + File.separator + "Databases"
                + File.separator + "msgstore.db");
        db_chat = new DB_SqLite_Chat(this, "msgstore.db", null, 1);
        //SQLite_Conv(userName , mChatUser);
    }
    // check if the group name is valid
    // if yes show the "next" button, hide it otherwise
    private boolean isValidGroupName() {

        String groupName = groupNameView.getText().toString();
        return StringUtils.isValid(groupName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_new_group, menu);

        actionNextMenuItem = menu.findItem(R.id.action_next);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_next) {
            onActionNextClicked();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void onActionNextClicked() {
        showProgressDialog();
        final String chatGroupName = WizardNewGroup.getInstance().getTempChatGroup().getName();
        final String chatGroupImg = WizardNewGroup.getInstance().getTempChatGroup().getIconURL();
        Map<String, String> chatGroupMembers = WizardNewGroup.getInstance().getTempChatGroup().getMembers();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //TODO
        long current_time_stamp = timestamp.getTime();
        groupBuilder.createChatGroup(chatGroupName, chatGroupMembers , current_time_stamp, chatGroupImg,  new ChatGroupCreatedListener() {
            @Override
            public void onChatGroupCreated(ChatGroup chatGroup, ChatRuntimeException chatException) {

                // clear the wizard
                WizardNewGroup.getInstance().dispose();

                if (chatException == null) {

                    Log.d(DEBUG_GROUPS, "NewGroupActivity.onActionNextClicked" +
                            ".onChatGroupCreated: chatGroup == " + chatGroup.toString());

                    // create a conversation on the fly
                    //Conversation conversation = createConversationForAdapter(chatGroup);

                    // add the conversation to the conversation adapter
                    //ChatManager.getInstance().getConversationsHandler().addConversation(conversation);
                    //TODO
                    //db_chat.queryData(Conversation.CREATE_TABLE_EXIST("conversations"));
                    db_chat.queryData(MessageGroup.CREATE_TABLE_EXIST(chatGroupName +"_"+ current_time_stamp));
                    //==============================S-CheckChatAvalibality=====================================
                    //long insert_chat_id = db_chat.insertDataConv( "conversations", chatGroupName, ""+current_time_stamp, "Future System Created " + chatGroupName , chatGroupImg ,  Message_Status.GROUP_CHANNEL_TYPE, current_time_stamp  , false , true);
                    //==============================E-CheckChatAvalibality=====================================
                    setResult(RESULT_OK);
                    hideProgressDialog();
                    finish();
                } else {
                    Log.e(DEBUG_GROUPS, "NewGroupActivity.onActionNextClicked" +
                            ".onChatGroupCreated: " + chatException.getLocalizedMessage());
                    // TODO: 29/01/18
                    setResult(RESULT_CANCELED);
                    return;
                }
            }
        });

    }

    // create a conversation on the fly
    private Conversation createConversationForAdapter(ChatGroup chatGroup) {
        Conversation conversation = new Conversation();
        conversation.setChannelType(Message_Status.GROUP_CHANNEL_TYPE);
        conversation.setTime_stamp(chatGroup.getCreatedOnLong());
        conversation.setFrom_name(chatGroup.getName());
        conversation.setFrom_id(chatGroup.getGroupId());
        conversation.setFrom_name(chatGroup.getName());
        return conversation;
    }

    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }
    public void uploadFile(File file) {

        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(NewGroupActivity.this)//Compressor Library
                    .setMaxWidth(200)
                    .setMaxHeight(300)
                    .setQuality(2)

                    .compressToBitmap(new_image_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePaths filePaths = new FilePaths();
        final String randomname = UUID.randomUUID().toString();//generic randomname
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumb_data = baos.toByteArray();
        showProgressDialog();
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + current_user_id + "/group_photo/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String download_thumb_uri = task.getResult().getDownloadUrl().toString();
                mGroupImage.setImageURI(Uri.fromFile(getNew_image_file()));
                WizardNewGroup.getInstance().getTempChatGroup().setIconURL(download_thumb_uri);
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            mediaSelector.handleResult(this, requestCode, resultCode, data);
        }
        catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

