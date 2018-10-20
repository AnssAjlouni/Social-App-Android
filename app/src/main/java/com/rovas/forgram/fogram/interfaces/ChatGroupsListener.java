package com.rovas.forgram.fogram.interfaces;

import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.exception.ChatRuntimeException;


/**
 * Created by Mohamed El Sayed
 */

public interface ChatGroupsListener {

    void onGroupAdded(ChatGroup chatGroup, ChatRuntimeException e);

    void onGroupChanged(ChatGroup chatGroup, ChatRuntimeException e);

    void onGroupRemoved(ChatRuntimeException e);
}
