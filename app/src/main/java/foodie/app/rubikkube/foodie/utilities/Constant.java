package foodie.app.rubikkube.foodie.utilities;

public class Constant {

    public static final String  TOKEN = "token";
    public static final String  IS_LOGIN = "is_login";
    public static final String  IS_PIN_CODE = "pin_code";
    public static final String  USERID = "id";
    public static final String  NAME = "name";
    public static final String  EMAIL = "email";
    public static final String  PHONE = "phone";
    public static final String  EMAIL_CONFIRM = "email_confirm";
    public static final String  USER_TYPE = "user_type";
    public static final String  KID_ID = "kid_id";
    public static final String  KID_AVATRA = "kid_avatar";
    public static final String  KID_NAME = "kid_name";
    public static final String  KID_CURRENT_BAL = "kid_current_balance";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

}
