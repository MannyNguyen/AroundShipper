package vn.nip.aroundshipper.Fragment.Dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDialogFragment extends BaseDialogFragment {

    private Runnable runnable;
    private String message;

    public MessageDialogFragment() {

    }

    public static MessageDialogFragment newInstance() {

        Bundle args = new Bundle();

        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            MessageDialogFragment.this.setCancelable(false);
            TextView msg = (TextView) getView().findViewById(R.id.message);
            msg.setText(getMessage());

            Button confirm = (Button) getView().findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageDialogFragment.this.dismiss();
                    if (getRunnable() != null) {
                        getRunnable().run();
                    }
                }
            });
            Window window = getDialog().getWindow();
            if (window != null) {
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }

        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
