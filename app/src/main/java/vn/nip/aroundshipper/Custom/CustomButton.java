package vn.nip.aroundshipper.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import vn.nip.aroundshipper.R;


/**
 * Created by viminh on 2/27/2017.
 */

public class CustomButton extends Button {
    public CustomButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomFontTextView);
        int cf = a.getInteger(R.styleable.CustomFontTextView_fontName, 0);
        int fontName = 0;
        switch (cf) {
            case 1:
                fontName = R.string.OpenSans_Light;
                break;
            case 2:
                fontName = R.string.OpenSans_Regular;
                break;
            case 3:
                fontName = R.string.OpenSans_Semibold;
                break;
            case 4:
                fontName = R.string.OpenSans_Bold;
                break;
            case 5:
                fontName = R.string.OpenSans_Italic;
                break;

            default:
                fontName = R.string.OpenSans_Regular;
                break;
        }

        String customFont = getResources().getString(fontName);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), customFont + ".ttf");
        setTypeface(tf);
        a.recycle();
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        setTypeface(customFont);
    }
}