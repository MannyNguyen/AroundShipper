package vn.nip.aroundshipper.Fragment.Dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;

import sfs2x.client.requests.ExtensionRequest;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.ICallbackValue;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyCodeDialogFragment extends DialogFragment {

    public ICallbackValue callBack;

    public VerifyCodeDialogFragment() {
        // Required empty public constructor
    }

    public static VerifyCodeDialogFragment newInstance() {

        Bundle args = new Bundle();

        VerifyCodeDialogFragment fragment = new VerifyCodeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_code_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            final EditText text = (EditText) getView().findViewById(R.id.text);
            final TextView error = (TextView) getView().findViewById(R.id.error);
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    error.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            text.removeTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    error.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            Button confirm = (Button) getView().findViewById(R.id.confirm);
            final Button cancel = (Button) getView().findViewById(R.id.cancel);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (text.getText().toString().equals("")) {
                        error.setVisibility(View.VISIBLE);
                        return;
                    }
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "VERIFY_ORDER_CODE");
                    sfsObject.putUtfString("verify_code", text.getText().toString());
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VerifyCodeDialogFragment.this.dismiss();


                }
            });
            VerifyCodeDialogFragment.this.setCancelable(false);
            Window window = getDialog().getWindow();
            if (window != null) {
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }

        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

    }
}
