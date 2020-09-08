package foodie.app.rubikkube.foodie.fonts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SFProDisplayLight  extends TextView {
    public SFProDisplayLight(Context context) {
        super(context);
        setFont();
    }
    public SFProDisplayLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public SFProDisplayLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/SFProDisplay-Light.ttf");
        setTypeface(font, Typeface.BOLD);
//		setTextSize(EndPointsConstants.fontsize);
//		setLineSpacing(5,2.5f);
//		setTextColor(getResources().getColor(R.color.priwhite));
    }


    public void updateTextSize(Context context) {
        getTypeface();
        float currentTextSize = getTextSize();
        SharedPreferences otherSettings = context.getSharedPreferences("settings", 0);
        float newScale = otherSettings.getFloat("key_scaling", 1f);
        setTextSize(newScale * currentTextSize);
    }
}