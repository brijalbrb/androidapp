package s.com.videoapp.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CTextView extends android.support.v7.widget.AppCompatTextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public CTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        setTypeface(selectTypeface(context, textStyle));
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        switch (textStyle) {
            case Typeface.BOLD:
                return Typeface.createFromAsset(context.getAssets(), "Myriad Web Pro Regular.ttf");
            case Typeface.NORMAL:
                return Typeface.createFromAsset(context.getAssets(), "Myriad Web Pro Regular.ttf");// regular
            default:
                return Typeface.createFromAsset(context.getAssets(), "Myriad Web Pro Regular.ttf");// regular
        }
    }
}