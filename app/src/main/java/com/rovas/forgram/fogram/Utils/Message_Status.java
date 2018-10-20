package com.rovas.forgram.fogram.Utils;
/**
 * Created by Mohamed El Sayed
 */
public class Message_Status {
    public static final String STATUS_FIELD_KEY = "status";
    public static final String TIMESTAMP_FIELD_KEY = "timestamp";

    // message status
    public static final long STATUS_FAILED = -100;
    public static final long STATUS_SENDING = 0;
    public static final long STATUS_SENT = 100; //(SALVATO SULLA TIMELINE DEL MITTENTE)
    public static final long STATUS_DELIVERED_TO_RECIPIENT_TIMELINE = 150; //(SALVATO SULLA TIMELINE DEL DESTINATARIO)

    public static final long STATUS_RECEIVED_FROM_RECIPIENT_CLIENT = 200;
    public static final long STATUS_RETURN_RECEIPT = 250;  // from the recipient client app)
    public static final long STATUS_SEEN = 300; // message read from contact

    public static final String DIRECT_CHANNEL_TYPE = "direct";
    public static final String GROUP_CHANNEL_TYPE = "group";

    // message type
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_FILE = "file";
}
