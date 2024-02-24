package vn.nip.aroundshipper.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

/**
 * Created by viminh on 10/5/2016.
 */

public class CustomDialog {
//    public static void Dialog1Button(Activity activity, String title, String btnTitle, final ICallback callBack) {
//        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialog_1_button, null);
//            builder.setView(dialogView);
//            Button yes = (Button) dialogView.findViewById(R.id.yes);
//            yes.setText(btnTitle);
//            TextView titleView = (TextView) dialogView.findViewById(R.id.title);
//            titleView.setText(title);
//            final AlertDialog dialog = builder.create();
//            dialog.show();
//            yes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                    if (callBack != null) {
//                        callBack.excute();
//                    }
//
//                }
//            });
//        } catch (Exception e) {
//            Log.e("CustomDialog", "ViMT - Dialog1Button: " + e.getMessage());
//        }
//    }



    public static void Dialog2Button(final Activity activity, final String titleText, final String messageText, final String titleOk, final String titleCancle, final ICallback okCallback, final ICallback cancleCallback) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);

            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            final CardView ok = (CardView) dialogView.findViewById(R.id.ok);
            final CardView cancel = (CardView) dialogView.findViewById(R.id.cancel);
            TextView cancleText = (TextView) dialogView.findViewById(R.id.cancleText);
            if (titleCancle == null) {
                cancleText.setText(activity.getString(R.string.cancel));
            } else {
                cancleText.setText(titleCancle);
            }

            TextView okText = (TextView) dialogView.findViewById(R.id.okText);
            if (titleOk == null) {
                okText.setText(activity.getString(R.string.ok));
            } else {
                okText.setText(titleOk);
            }

            final AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);


            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.5f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
//
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (okCallback != null) {
                        okCallback.excute();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    if (cancleCallback != null) {
                        cancleCallback.excute();
                    }

                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }


    }



    public static void showMessage(Activity activity, String titleText, String messageText) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_message, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);

            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            CardView ok = (CardView) dialogView.findViewById(R.id.ok);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogMessage: " + e.getMessage());
        }
    }


    public static void showMessage(Activity activity, String titleText, String messageText, final ICallback callback) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_message, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);
            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            CardView ok = (CardView) dialogView.findViewById(R.id.ok);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    callback.excute();
                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

}
