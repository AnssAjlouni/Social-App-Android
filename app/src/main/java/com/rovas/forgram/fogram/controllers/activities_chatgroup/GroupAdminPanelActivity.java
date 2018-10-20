package com.rovas.forgram.fogram.controllers.activities_chatgroup;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.File.FilePaths;
import com.rovas.forgram.fogram.Utils.ItemDecoration;
import com.rovas.forgram.fogram.Utils.MediaSelector;
import com.rovas.forgram.fogram.Utils.TimeUtils;
import com.rovas.forgram.fogram.controllers.activities_chat.ProfileImageDetailsActivity;
import com.rovas.forgram.fogram.interfaces.ChatGroupsListener;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.views.GroupMembersListAdapter;
import com.rovas.forgram.fogram.interfaces.OnGroupMemberClickListener;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.connectivity.AbstractNetworkReceiver;
import com.rovas.forgram.fogram.exception.ChatRuntimeException;
import com.rovas.forgram.fogram.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import id.zelory.compressor.Compressor;

import static com.rovas.forgram.fogram.Utils.UI.ChatUI.BUNDLE_CHAT_GROUP;

/**
 * Created by Mohamed El Sayed
 */
public class GroupAdminPanelActivity extends AppCompatActivity implements
        OnGroupMemberClickListener, ChatGroupsListener {
    private static final String TAG = GroupAdminPanelActivity.class.getName();
    //private CopyOnWriteArrayList<User> contacts = new CopyOnWriteArrayList<>(); // contacts in memory
    protected MediaSelector mediaSelector = new MediaSelector();
    private Bitmap compressedImageFile;
    private File new_image_file;
    private RecyclerView mMemberList;
    private GroupMembersListAdapter mGroupMembersListAdapter;
    private ImageView mGroupImage;
    private LinearLayout mBoxAddMember;
    private LinearLayout mBoxChangeImage;
    private LinearLayout mBoxMembers;
    private LinearLayout mBoxUnavailableMembers;
    private ChatGroup chatGroup;
    private List<User> groupAdmins;
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;
    private String current_user_id;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    //private User loggedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_group_admin_panel);
        registerViews();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user = mAuth.getCurrentUser();
        current_user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        //TODO IMPORTANT chatGroup
        chatGroup = (ChatGroup) getIntent().getExtras().getSerializable(ChatUI.BUNDLE_GROUP_ID);

        groupAdmins = getGroupAdmins();

        setToolbar();
        setCreatedBy();
        setCreatedOn();
        setImage();
        //setGroupId();
        initRecyclerViewMembers();
        toggleAddMemberButton();
    }

    private void setImage() {
        mGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupAdminPanelActivity.this, ProfileImageDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ChatUI.BUNDLE_CHAT_PROFILE_PIC, chatGroup.getIconURL());
                intent.putExtra(ChatUI.BUNDLE_CHAT_USERNAME, chatGroup.getName());
                startActivity(intent);
            }
        });
    }

    private void registerViews() {
        Log.d(TAG, "registerViews");

        mMemberList = (RecyclerView) findViewById(R.id.members);
        mBoxAddMember = (LinearLayout) findViewById(R.id.box_add_member);
        mBoxChangeImage = (LinearLayout)findViewById(R.id.box_change_icon);
        mBoxMembers = (LinearLayout) findViewById(R.id.box_members);
        mBoxUnavailableMembers = (LinearLayout) findViewById(R.id.box_unavailable_members);
    }

    private List<User> getGroupAdmins() {
        List<User> admins = new ArrayList<>();

        String owner = chatGroup.getOwner(); // it always exists
        //TODO

        for (User member : chatGroup.getMembersList()) {
            Log.d(TAG, "getGroupAdmins: for (User member : chatGroup.getMembersList()) {");
            if (member.getUser_id().equals(owner)) {
                Log.d(TAG, "getGroupAdmins: getGroupAdmins " + member.getUser_id());
                admins.add(member);
                break;
            }
            else if(UserWiazrd.getInstance().getTempUser().getRole() == 1 ||UserWiazrd.getInstance().getTempUser().getRole() == 2)
            {
                admins.add(member);
                break;
            }
        }

        return admins;
    }

    private void setToolbar() {
        Log.d(TAG, "GroupAdminPanelActivity.setToolbar");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mGroupImage = (ImageView) findViewById(R.id.group_image);
        // chatGroup name
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(chatGroup.getName());

//        TextView toolbarSubTitle = findViewById(R.id.toolbar_subtitle);
//        toolbarSubTitle.setText("");

        // chatGroup picture
        if(chatGroup.getIconURL() != null || !chatGroup.getIconURL().equals("NOICON")) {
            Glide.with(getApplicationContext())
                    .load(chatGroup.getIconURL())
                    .into(mGroupImage);
        }
        // minimal settings
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setCreatedBy() {
        Log.d(TAG, "GroupAdminPanelActivity.setCreatedByOn");

        TextView createdByView = (TextView) findViewById(R.id.created_by);

        // if the creator of the chatGroup is the logged user set it
        // otherwise retrieve the chatGroup creator from the chatGroup member list
        String creator = current_user_id
                .equals(chatGroup.getOwner()) ? "Future-Manager" : "Error"; //TODO GET_USER_NAME OR NAME
        //TODO

        for (User member : chatGroup.getMembersList()) {
            if (member.getUser_id().equals(chatGroup.getOwner())) {
                Log.d(TAG, "getGroupAdmins: SetCreatedBY " + member.getUser_id());
                creator = member.getUsername();
                break;
            }
        }
//        String createdBy = getString(R.string.activity_group_admin_panel_formatted_created_by_label, creator);
        createdByView.setText(creator);
    }

    private void setCreatedOn() {
        TextView createdOnView = (TextView) findViewById(R.id.created_on);

        // retrieve the creation date
        String timestamp = TimeUtils.getFormattedTimestamp(this, chatGroup.getCreatedOnLong());

        // format the user creator and creating date string
//        String createOn = getString(R.string.activity_group_admin_panel_formatted_created_on_label, timestamp);

        // show the text
        createdOnView.setText(timestamp);
    }

    private void setGroupId() {
        /*
        TextView groupIdView = findViewById(R.id.group_id);
        String groupId = getString(R.string.activity_group_admin_panel_formatted_group_id_label, chatGroup.getGroupId());
        groupIdView.setText(chatGroup.getGroupId());
        */
    }

    private void initRecyclerViewMembers() {
        Log.d(TAG, "initRecyclerViewMembers");
        mMemberList.addItemDecoration(new ItemDecoration(this,
                DividerItemDecoration.VERTICAL,
                getResources().getDrawable(R.drawable.decorator_activity_group_admin_panel_members_list)));
        mMemberList.setLayoutManager(new LinearLayoutManager(this));
        //TODO
        updateGroupMemberListAdapter(chatGroup.getMembersList());
    }

    private void toggleGroupMembersVisibility() {
        Log.d(TAG, "initCardViewMembers");

        if (chatGroup.getMembersList() != null && chatGroup.getMembersList().size() > 0) {
            mBoxUnavailableMembers.setVisibility(View.GONE);
            mBoxMembers.setVisibility(View.VISIBLE);

        } else {
            Log.e(TAG, "GroupAdminPanelActivity.toggleCardViewMembers: " +
                    "groupMembers is not valid");
            mBoxMembers.setVisibility(View.GONE);
            mBoxUnavailableMembers.setVisibility(View.VISIBLE);
        }
    }

    private void updateGroupMemberListAdapter(List<User> members) {
        Log.d(TAG, "updateGroupMemberListAdapter");

        if (mGroupMembersListAdapter == null) {
            mGroupMembersListAdapter = new GroupMembersListAdapter(this, members);
            mGroupMembersListAdapter.setOnGroupMemberClickListener(this);
            mMemberList.setAdapter(mGroupMembersListAdapter);
        } else {
            mGroupMembersListAdapter.setList(members);
            mGroupMembersListAdapter.notifyDataSetChanged();
        }

        for (User admin : groupAdmins) {
            Log.d(TAG, "getGroupAdmins:  for (User admin : groupAdmins) {");
            mGroupMembersListAdapter.addAdmin(admin);
        }

        toggleGroupMembersVisibility();
    }

    private void toggleAddMemberButton() {
        Log.d(TAG, "toggleAddMemberButton");
        // check if the current user is an admin and a member of the group
        //if (groupAdmins.contains(current_user) && chatGroup.getMembersList().contains(current_user))//OLDCHECK //MULTI_ADMINS//TODO
        if(chatGroup.getOwner().equals(current_user_id)) {
            Log.d(TAG, "toggleAddMemberButton:  showAddMember");

            showAddMember();
            ShowChangeImage();


        } else {
            if(UserWiazrd.getInstance().getTempUser().getRole() == 1 ||UserWiazrd.getInstance().getTempUser().getRole() == 2)
            {
                showAddMember();
                ShowChangeImage();
            }
            else {
                hideAddMember();
                hideChangeImage();
            }
        }

    }

    private void hideChangeImage() {
        Log.d(TAG, "GroupAdminPanelActivity.hideChangeImage");

        // hides the add member box
        mBoxChangeImage.setVisibility(View.GONE);

        // unset the click listener
        mBoxChangeImage.setOnClickListener(null);
    }

    private void ShowChangeImage() {
        Log.d(TAG, "GroupAdminPanelActivity.ShowChangeImage");

        // shows the add member box
        mBoxChangeImage.setVisibility(View.VISIBLE);

        // set the click listener
        mBoxChangeImage.setOnClickListener(view -> mediaSelector.startChooseImageActivity(GroupAdminPanelActivity.this, MediaSelector.CropType.Circle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                uploadFile(getNew_image_file());
            }
            catch (Exception e) {
                Toast.makeText(GroupAdminPanelActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void showAddMember() {
        Log.d(TAG, "GroupAdminPanelActivity.showAddMember");

        // shows the add member box
        mBoxAddMember.setVisibility(View.VISIBLE);

        // set the click listener
        mBoxAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AbstractNetworkReceiver.isConnected(getApplicationContext())) {
                    startAddMemberActivity();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.menu_activity_group_admin_panel_activity_cannot_add_member_offline),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideAddMember() {
        Log.d(TAG, "GroupAdminPanelActivity.hideAddMember");

        // hides the add member box
        mBoxAddMember.setVisibility(View.GONE);

        // unset the click listener
        mBoxAddMember.setOnClickListener(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void startAddMemberActivity() {
        Log.d(TAG, "startAddMemberActivity");

//        Intent intent = new Intent(this, AddMembersToGroupActivity.class);
        Intent intent = new Intent(this, AddMemberToChatGroupActivity.class);
        intent.putExtra(BUNDLE_CHAT_GROUP, chatGroup);
        startActivity(intent);
    }

    // handles the click on a member
    @Override
    public void onGroupMemberClicked(User member, int position) {
        Log.i(TAG, "onGroupMemberClicked");

        showOnMemberClickedBottomSheet(member, chatGroup);
    }

    private void showOnMemberClickedBottomSheet(User groupMember, ChatGroup chatGroup) {
        Log.d(TAG, "showOnMemberClickedBottomSheet");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BottomSheetGroupAdminPanelMember dialog = BottomSheetGroupAdminPanelMember
                .newInstance(groupMember, chatGroup);
        dialog.show(ft, BottomSheetGroupAdminPanelMember.TAG);
    }

    @Override
    public void onGroupAdded(ChatGroup chatGroup, ChatRuntimeException e) {
        if (e == null) {

            if (chatGroup.getGroupId().equals(chatGroup.getGroupId())) {
                Log.d(TAG, "GroupAdminPanelActivity.onGroupAdded.chatGroup:" +
                        " chatGroup == " + chatGroup.toString());

                this.chatGroup = chatGroup;
                //TODO
                updateGroupMemberListAdapter(chatGroup.getMembersList()); // update members
            }
        } else {
            Log.e(TAG, "GroupAdminPanelActivity.onGroupAdded: " + e.toString());
        }
    }

    @Override
    public void onGroupChanged(ChatGroup chatGroup, ChatRuntimeException e) {
        if (e == null) {

            this.chatGroup = chatGroup;

            if (chatGroup.getGroupId().equals(chatGroup.getGroupId())) {
                Log.d(TAG, "GroupAdminPanelActivity.onGroupChanged.chatGroup: " +
                        "chatGroup == " + chatGroup.toString());
                //TODO
                updateGroupMemberListAdapter(chatGroup.getMembersList()); // update members
            }
        } else {
            Log.e(TAG, "GroupAdminPanelActivity.onGroupChanged: " + e.toString());
        }
    }

    @Override
    public void onGroupRemoved(ChatRuntimeException e) {
        if (e == null) {
            Log.d(TAG, "GroupAdminPanelActivity.onGroupRemoved");
        } else {
            Log.e(TAG, "GroupAdminPanelActivity.onGroupRemoved: " + e.toString());
        }
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
    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }
    public void uploadFile(File file) {
        Log.d(TAG, "uploadFile");

        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(GroupAdminPanelActivity.this)//Compressor Library
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
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + current_user_id + "/group_photo/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String download_thumb_uri = task.getResult().getDownloadUrl().toString();
                HashMap<String , Object> group_image = new HashMap<>();
                group_image.put("iconURL" , download_thumb_uri);
                fStore.collection("groups").document(chatGroup.getGroupId()).update(group_image).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mGroupImage.setImageURI(Uri.fromFile(getNew_image_file()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupAdminPanelActivity.this, "Failed To Upload Group Image Please Chech Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}