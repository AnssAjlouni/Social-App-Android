package com.rovas.forgram.fogram.controllers.activities_chatgroup;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.activities_chat.PublicProfileActivity;
import com.rovas.forgram.fogram.managers.GroupBuilder;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.models.User;

import java.io.Serializable;

import static com.rovas.forgram.fogram.Utils.UI.ChatUI.BUNDLE_RECIPIENT;


/**
 * Created by Mohamed El Sayed
 */
public class BottomSheetGroupAdminPanelMember extends BottomSheetDialogFragment {
    public static final String TAG = BottomSheetGroupAdminPanelMember.class.getName();

    private static final String PRIVATE_BUNDLE_GROUP_MEMBER = "PRIVATE_BUNDLE_GROUP_MEMBER";
    private static final String PRIVATE_BUNDLE_GROUP = "PRIVATE_BUNDLE_GROUP";

    private User groupMember;
    private ChatGroup chatGroup;
    private Button removeMember;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private FirebaseFirestore fStore;
    private GroupBuilder groupBuilder;
    public static BottomSheetGroupAdminPanelMember newInstance(User groupMember, ChatGroup chatGroup) {
        Log.i(TAG, "newInstance");

        BottomSheetGroupAdminPanelMember f =
                new BottomSheetGroupAdminPanelMember();
        Bundle args = new Bundle();
        args.putSerializable(PRIVATE_BUNDLE_GROUP_MEMBER, groupMember);
        args.putSerializable(PRIVATE_BUNDLE_GROUP, chatGroup);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //current_user = mAuth.getCurrentUser();
        current_user_id = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        // retrieves the username from newInstance params
        groupMember = (User) getArguments().getSerializable(PRIVATE_BUNDLE_GROUP_MEMBER);
        groupBuilder = new GroupBuilder(current_user_id);
        // retrieves the groupId from newInstance params
        chatGroup = (ChatGroup) getArguments().getSerializable(PRIVATE_BUNDLE_GROUP);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.t_fragment_bottom_sheet_group_admin_panel_member,
                        container, false);

        registerViews(rootView);
        initRemoveMemberButton();

        return rootView;
    }


    private void registerViews(View rootView) {
        Log.i(TAG, "registerViews");

        // contact username
        TextView username = (TextView) rootView.findViewById(R.id.username);
        username.setText(groupMember.getName());

        // remove member
        removeMember = (Button) rootView.findViewById(R.id.btn_remove_member);
        removeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveMemberAlertDialog();
            }
        });

        // see profile
        Button seeProfile = (Button) rootView.findViewById(R.id.btn_see_profile);
        if (!groupMember.getUser_id().equals(current_user_id)) {
            seeProfile.setVisibility(View.VISIBLE);
            seeProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO

                    Intent intent = new Intent(getActivity()
                            .getApplicationContext(), PublicGroupProfileActivity.class);
                    //intent.putExtra(BUNDLE_RECIPIENT, (Serializable) groupMember);
                    intent.putExtra(ChatUI.BUNDLE_CHAT_ID, groupMember.getUser_id());
                    intent.putExtra(ChatUI.BUNDLE_CHAT_NAME, groupMember.getUsername());
                    startActivity(intent);

                    // dismiss the bottomsheet
                    getDialog().dismiss();

                }
            });
        } else {
            seeProfile.setVisibility(View.GONE);
        }

        // send direct message
        Button sendMessage = (Button) rootView.findViewById(R.id.btn_send_message);
        if (!groupMember.getUser_id().equals(current_user_id)) {//TODO
            sendMessage.setVisibility(View.VISIBLE);
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                    /*
                    Intent intent = new Intent(getActivity(), MessageListActivity.class);
                    intent.putExtra(BUNDLE_RECIPIENT, groupMember);
                    intent.putExtra(BUNDLE_CHANNEL_TYPE, Message.DIRECT_CHANNEL_TYPE);
                    getActivity().startActivity(intent);

                    // dismiss the bottomsheet
                    getDialog().dismiss();
                    */
                }
            });
        } else {
            sendMessage.setVisibility(View.GONE);
        }

        // cancel
        Button cancel = (Button) rootView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss the bottomsheet
                getDialog().dismiss();
            }
        });
    }

    private void initRemoveMemberButton() {
        Log.d(TAG, "initRemoveMemberButton");

        // check logged user is the admin of the group
        if (chatGroup.getOwner().equals(current_user_id)) {//TODO
            // the clicked user is an admin
            if (groupMember.getUser_id().equals(chatGroup.getOwner())) {
                // cannot delete and admin
                removeMember.setVisibility(View.GONE);
            } else {
                removeMember.setVisibility(View.VISIBLE);
            }
        } else {
            removeMember.setVisibility(View.GONE);
        }

        // allows the logged user to leave the chatGroup
        if (groupMember.getUser_id().equals(current_user_id)) {//TODO
            removeMember.setText(getString(
                    R.string.bottom_sheet_group_admin_panel_member_leave_group_btn_label));
            removeMember.setVisibility(View.VISIBLE);
        }
    }

    private void showRemoveMemberAlertDialog() {
        Log.d(TAG, "showRemoveMemberAlertDialog");

        String message, positiveClickMessage;

        // allows the logged user to leave the chatGroup
        if (groupMember.getUser_id().equals(current_user_id)) {//TODO
            message = getString(R.string.bottom_sheet_group_admin_panel_member_leave_group_alert_message);
            positiveClickMessage = getString(R.string.bottom_sheet_group_admin_panel_member_leave_group_alert_positive_click);
        } else {
            message = getString(R.string.bottom_sheet_group_admin_panel_member_remove_member_alert_message, groupMember.getName());
            positiveClickMessage = getString(R.string.bottom_sheet_group_admin_panel_member_remove_member_alert_positive_click);
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.bottom_sheet_group_admin_panel_member_remove_member_alert_title))
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(positiveClickMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                        groupBuilder.removeMemberFromChatGroup(chatGroup , chatGroup.getGroupId(), groupMember.getUser_id());
                        // dismiss the dialog
                        dialog.dismiss();

                        // dismiss the bottomsheet
                        getDialog().dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.bottom_sheet_group_admin_panel_member_remove_member_alert_negative_click), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        groupBuilder.removeMemberFromChatGroup(chatGroup , chatGroup.getGroupId(), current_user_id);
                        // dismiss the dialog
                        dialogInterface.dismiss();

                        // dismiss the bottomsheet
                        getDialog().dismiss();
                    }
                }).show();
    }
}