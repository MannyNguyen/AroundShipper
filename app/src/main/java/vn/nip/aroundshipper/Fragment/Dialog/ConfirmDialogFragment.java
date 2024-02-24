package vn.nip.aroundshipper.Fragment.Dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
public class ConfirmDialogFragment extends DialogFragment {

    private Runnable okCallback;
    private Runnable cancelCallback;
    private String message;

    public ConfirmDialogFragment() {

    }

    public static ConfirmDialogFragment newInstance() {

        Bundle args = new Bundle();

        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            TextView msg = (TextView) getView().findViewById(R.id.message);
            msg.setText(getMessage());
            Button confirm = (Button) getView().findViewById(R.id.confirm);
            final Button cancel = (Button) getView().findViewById(R.id.cancel);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConfirmDialogFragment.this.dismiss();
                    if (getOkCallback() != null) {
                        getOkCallback().run();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConfirmDialogFragment.this.dismiss();
                    if (getCancelCallback() != null) {
                        getCancelCallback().run();
                    }

                }
            });
            ConfirmDialogFragment.this.setCancelable(false);
            Window window = getDialog().getWindow();
            if(window!=null){
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }

        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    public Runnable getOkCallback() {
        return okCallback;
    }

    public void setOkCallback(Runnable okCallback) {
        this.okCallback = okCallback;
    }

    public Runnable getCancelCallback() {
        return cancelCallback;
    }

    public void setCancelCallback(Runnable cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
