package com.rovas.forgram.fogram.controllers.activities_chatgroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.fragments_chat.ContactsListFragment;
import com.rovas.forgram.fogram.managers.GroupBuilder;
import com.rovas.forgram.fogram.views.SelectedContactListAdapter;
import com.rovas.forgram.fogram.managers.WizardNewGroup;
import com.rovas.forgram.fogram.interfaces.OnContactClickListener;
import com.rovas.forgram.fogram.interfaces.OnRemoveClickListener;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.models.Following;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rovas.forgram.fogram.Utils.UI.ChatUI.BUNDLE_CHAT_GROUP;


/**
 * Created by Mohamed El Sayed
 */

public class AddMemberToChatGroupActivity extends AppCompatActivity implements OnContactClickListener, OnRemoveClickListener {

    private static final String TAG = AddMemberToChatGroupActivity.class.getName();

    private ContactsListFragment contactsListFragment;
    public static final int REQUEST_CODE_CREATE_GROUP = 100;
    private List<Following> selectedList;
    private CardView cvSelectedContacts;
    private RecyclerView rvSelectedList;
    private SelectedContactListAdapter selectedContactsListAdapter;

    private MenuItem actionNextMenuItem;
    private String current_user_id;
    private String current_user_name;
    private ChatGroup chatGroup;
    private GroupBuilder groupBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_add_member_to_chat_group);

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        current_user_name = getIntent().getStringExtra(ChatUI.BUNDLE_CURRENT_USERNAME);
        selectedList = new ArrayList<>();
        cvSelectedContacts = findViewById(R.id.cardview_selected_contacts);
        rvSelectedList = findViewById(R.id.selected_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
        rvSelectedList.setLayoutManager(layoutManager);
        rvSelectedList.setItemAnimator(new DefaultItemAnimator());
        updateSelectedContactListAdapter(selectedList, 0);

        contactsListFragment = new ContactsListFragment();
        contactsListFragment.setOnContactClickListener(this);

        // #### BEGIN TOOLBAR ####
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // #### END  TOOLBAR ####

        // #### BEGIN CONTAINER ####
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, contactsListFragment)
                .commit();
        // #### BEGIN CONTAINER ####
    }

    @Override
    protected void onStart() {
        long startTime = System.currentTimeMillis();

        // retrieve the chatGroup if exists
        //TODO retrieve the chatGroup if exists

        if (getIntent().hasExtra(BUNDLE_CHAT_GROUP)) {
            chatGroup = (ChatGroup) getIntent().getSerializableExtra(BUNDLE_CHAT_GROUP);
        }
        groupBuilder = new GroupBuilder(current_user_id);

        super.onStart();
    }

    private void updateSelectedContactListAdapter(List<Following> list, int position) {

        if (selectedContactsListAdapter == null) {
            selectedContactsListAdapter = new SelectedContactListAdapter(this, list);
            selectedContactsListAdapter.setOnRemoveClickListener(this);
            rvSelectedList.setAdapter(selectedContactsListAdapter);
        } else {
            selectedContactsListAdapter.setList(list);
            selectedContactsListAdapter.notifyDataSetChanged();
        }

        if (selectedContactsListAdapter.getItemCount() > 0) {
            cvSelectedContacts.setVisibility(View.VISIBLE);
            if (actionNextMenuItem != null) {
                actionNextMenuItem.setVisible(true);
            }// show next action
        } else {
            cvSelectedContacts.setVisibility(View.GONE);
            if (actionNextMenuItem != null) {
                actionNextMenuItem.setVisible(false); // hide next action
            }
        }

        rvSelectedList.smoothScrollToPosition(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_add_members, menu);

        actionNextMenuItem = menu.findItem(R.id.action_next);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "ChooseContactActivity.onOptionsItemSelected");

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_next) {
            onActionNextClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onActionNextClicked() {
        // convert the members list to a sanified format
        Map<String, String> membersMap = convertListToMap(selectedList);

        if (chatGroup == null) {
            WizardNewGroup.getInstance().getTempChatGroup().addMembers(membersMap);
            Intent intent = new Intent(this, NewGroupActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_GROUP);
        }
        else if (chatGroup != null)
        {
            groupBuilder.addMembersToChatGroup(chatGroup , chatGroup.getGroupId() , current_user_name, membersMap);
            finish(); // back to previous activity
        }
    }

    // convert the list of contact to a map of members
    private Map<String, String> convertListToMap(List<Following> contacts) {
        Map<String, String> members = new HashMap<>();
        for (Following contact : contacts) {
            // the value "1" is a default value with no usage
            members.put(contact.getUser_id(), contact.getUsername());
            //TODO
        }
        //TODO LOAD_USERNAME
        // add the current user to members list in background
        if(chatGroup ==null) {
            members.put(current_user_id, current_user_name);
        }


        return members;
    }

    @Override
    public void onContactClicked(Following contact, int position) {
        // add a contact only if it not exists
        addMemberToGroup(contact, selectedList, position);
    }

    private void addMemberToGroup(Following contact, List<Following> contactList, int position) {
        // add a contact only if it not exists
        if (!isContactAlreadyAdded(contact, contactList)) {
            // add the contact to the contact list and update the adapter
            contactList.add(contact);
        }

//        contactsListAdapter.addToAlreadyAddedList(contact, position);

        updateSelectedContactListAdapter(contactList, position);
    }

    // check if a contact is already added to a list
    private boolean isContactAlreadyAdded(Following toCheck, List<Following> mlist) {
        boolean exists = false;
        for (Following contact : mlist) {
            String contactId = contact.getUser_id();

            if (contactId.equals(toCheck.getUser_id())) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    @Override
    public void onRemoveClickListener(int position) {

        Following contact = selectedList.get(position);
        // remove the contact only if it exists
        if (isContactAlreadyAdded(contact, selectedList)) {
            // remove the item at position from the contacts list and update the adapter
            selectedList.remove(position);

//            contactsListAdapter.removeFromAlreadyAddedList(contact);

            updateSelectedContactListAdapter(selectedList, position);
        } else {
            Snackbar.make(findViewById(R.id.coordinator),
                    getString(R.string.add_members_activity_contact_not_added_label),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        contactsListFragment.onBackPressed();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_GROUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
