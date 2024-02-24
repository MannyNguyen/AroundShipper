package vn.nip.aroundshipper.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by viminh on 2/27/2017.
 */

public class CustomRadioButton extends RadioButton {
    public CustomRadioButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        if (this.getTypeface().getStyle() == Typeface.BOLD) {
            customFont = Typeface.createFromAsset(context.getAssets(), "OpenSans-Bold.ttf");
        } else if (this.getTypeface().getStyle() == Typeface.ITALIC) {
            customFont = Typeface.createFromAsset(context.getAssets(), "OpenSans-Italic.ttf");
        }
        setTypeface(customFont);
    }
}
