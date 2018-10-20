package com.rovas.forgram.fogram.Utils;

/**
 * Created by Mohamed El Sayed
 */
public class Constants {

    public static class Profile {
        public static final int MAX_AVATAR_SIZE = 1280; //px, side of square
        public static final int MIN_AVATAR_SIZE = 100; //px, side of square
        public static final int MAX_NAME_LENGTH = 120;
    }

    public static class Post {
        public static final int MAX_TEXT_LENGTH_IN_LIST = 300; //characters
        public static final int MAX_POST_TITLE_LENGTH = 255; //characters
        public static final int POST_AMOUNT_ON_PAGE = 10;
    }

    public static class Database {
        public static final int MAX_UPLOAD_RETRY_MILLIS = 60000; //1 minute
    }

    public static class PushNotification {
        public static final int LARGE_ICONE_SIZE = 256; //px
    }


    public static class General {
        public static final long DOUBLE_CLICK_TO_EXIT_INTERVAL = 3000; // in milliseconds
    }
}