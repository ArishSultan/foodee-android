package foodie.app.rubikkube.foodie;

import android.content.Context;
import androidx.annotation.IdRes;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUtils {

    public static void showBadge(Context context, BottomNavigationView bottomNavigationView, @IdRes int itemId, String value) {
        removeBadge(bottomNavigationView, itemId);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.chat_badge, bottomNavigationView, false);

        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);
    }

//    public static void showBadge(Context context, Toolbar toolbar, @IdRes int itemId, String value) {
////        removeBadge(bottomNavigationView, itemId);
//        MenuItem itemView = toolbar.findViewById(itemId);
//        View badge = LayoutInflater.from(context).inflate(R.layout.chat_badge, bottomNavigationView, false);
//
//        TextView text = badge.findViewById(R.id.badge_text_view);
//        text.setText(value);
//        itemView.ad
//        itemView.addView(badge);
//    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
    }

    public static void showDetailDialog(Context context,String title,String details) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(details)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String checkUserId(String txt) {

        String url="";

        String re1=".*?";	// Non-greedy match on filler
        String re2="(h)";	// Any Single Word Character (Not Whitespace) 1
        String re3="(t)";	// Any Single Word Character (Not Whitespace) 2
        String re4="(t)";	// Any Single Word Character (Not Whitespace) 3
        String re5="(p)";	// Any Single Word Character (Not Whitespace) 4
        String re6="(:)";	// Any Single Character 1
        String re7="(\\/)";	// Any Single Character 2
        String re8="(\\/)";	// Any Single Character 3
        String re9="((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])";	// IPv4 IP Address 1
        String re10="(\\/)";	// Any Single Character 4
        String re11="(u)";	// Any Single Character 5
        String re12="(s)";	// Any Single Character 6
        String re13="(e)";	// Any Single Character 7
        String re14="(r)";	// Any Single Character 8
        String re15="(\\/)";	// Any Single Character 9
        String re16="(\\d+)";	// Integer Number 1

        Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12+re13+re14+re15+re16,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);
        if (m.find())
        {
            String w1=m.group(1);
            String w2=m.group(2);
            String w3=m.group(3);
            String w4=m.group(4);
            String c1=m.group(5);
            String c2=m.group(6);
            String c3=m.group(7);
            String ipaddress1=m.group(8);
            String c4=m.group(9);
            String c5=m.group(10);
            String c6=m.group(11);
            String c7=m.group(12);
            String c8=m.group(13);
            String c9=m.group(14);
            String int1=m.group(15);

             url = w1+w2+w3+w4+c1+c2+c3+ipaddress1+c4+c5+c6+c7+c8+c9+int1;
        }
        return url;
    }
}
