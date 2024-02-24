package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSObject;

import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CustomDialog;
import vn.nip.aroundshipper.Fragment.Dialog.VerifyCodeDialogFragment;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.Interface.ICallbackValue;
import vn.nip.aroundshipper.R;

import sfs2x.client.requests.ExtensionRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptFragment extends BaseFragment implements View.OnClickListener {

    public ReceiptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_receipt, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(getActivity().getString(R.string.receipt));

            getReceipt();
            getView().findViewById(R.id.finish).setOnClickListener(ReceiptFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish:
                VerifyCodeDialogFragment fragment = VerifyCodeDialogFragment.newInstance();
                fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getName());


                break;
        }
    }

    //region Methods
    private void getReceipt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_RECEIPT");
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }
    //endregion
}
