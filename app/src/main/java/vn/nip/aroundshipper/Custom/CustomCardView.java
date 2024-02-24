package vn.nip.aroundshipper.Custom;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by HOME on 8/18/2017.
 */

public class CustomCardView extends CardView {
    public CustomCardView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          this.setCardElevation(0);
        }
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.setCardElevation(0);
        }
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.setCardElevation(0);
        }
    }
}
