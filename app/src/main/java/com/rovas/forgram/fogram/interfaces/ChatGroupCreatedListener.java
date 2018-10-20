package com.rovas.forgram.fogram.interfaces;

import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.exception.ChatRuntimeException;


/**
 * Created by Mohamed El Sayed
 */

public interface ChatGroupCreatedListener {
    void onChatGroupCreated(ChatGroup chatGroup, ChatRuntimeException chatException);
}
