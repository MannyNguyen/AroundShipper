package vn.nip.aroundshipper.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.smartfoxserver.v2.entities.data.SFSObject;

import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.CustomDialog;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.Interface.ICallback;

import vn.nip.aroundshipper.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Locale;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    byte[] avatarValue;
    //endregion

    //region Contructors
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            try {
                ImageButton updateAvatar = (ImageButton) getView().findViewById(R.id.update_avatar);
                updateAvatar.setOnClickListener(ProfileFragment.this);

                CardView save = (CardView) getView().findViewById(R.id.save);
                save.setOnClickListener(ProfileFragment.this);
                getProfile();

                String[] arrPay = {"English", "Tiếng Việt"};
                Spinner language = (Spinner) getView().findViewById(R.id.language);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_spinner, arrPay);
                arrayAdapter.setDropDownViewResource
                        (android.R.layout.simple_list_item_single_choice);

                language.setAdapter(arrayAdapter);

                if (StorageHelper.getLanguage().equals("vi")) {
                    language.setSelection(1);
                }
                final int[] j = {0};
                language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                        if (j[0] != 0) {
                            CustomDialog.Dialog2Button(getActivity(), getString(R.string.confirm), getString(R.string.change_language), getString(R.string.ok),
                                    getString(R.string.cancel), new ICallback() {
                                        @Override
                                        public void excute() {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (i == 0) {
                                                            StorageHelper.saveLanguage("en");
                                                            CmmVariable.jsonError = new JSONArray(StorageHelper.getContentError());
                                                        } else {
                                                            StorageHelper.saveLanguage("vi");
                                                            CmmVariable.jsonError = new JSONArray(StorageHelper.getContentVNError());
                                                        }

                                                        Locale locale = new Locale(StorageHelper.getLanguage());
                                                        Locale.setDefault(locale);
                                                        Configuration config = new Configuration();
                                                        config.locale = locale;
                                                        getContext().getResources().updateConfiguration(config,
                                                                getContext().getResources().getDisplayMetrics());

                                                        //SmartFoxHelper.getInstance().disconnect();

                                                    } catch (Exception e) {

                                                    }
                                                }
                                            }).start();

                                        }
                                    }, null);
                        }
                        j[0] = 1;

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                isLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1999 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                bmp = CmmFunc.resizeBitmap(bmp, 256);
                ImageView avatarView = (ImageView) getView().findViewById(R.id.avatar);
                avatarView.setImageBitmap(bmp);
                avatarValue = CmmFunc.bitmapToByteArray(bmp);
                //avatarValue = Base64.encodeToString(arr, Base64.DEFAULT);
            } catch (Exception e) {

            }
        }
    }

//endregion

    //region Methods
    private void getProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_PROFILE");
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }

    private void updateProfile(final byte[] avatar, final String phone, final String fullname, final String idNo, final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "UPDATE_PROFILE");
                if (avatar != null) {
                    sfsObject.putByteArray("avatar", avatar);
                } else {
                    sfsObject.putByteArray("avatar", new byte[]{});
                }

                sfsObject.putUtfString("fullname", fullname);
                sfsObject.putUtfString("phone", phone);
                sfsObject.putUtfString("id_no", idNo);
                sfsObject.putUtfString("address", address);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
            }
        }).start();
    }
    //endregion

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_avatar:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1999);
                break;
            case R.id.save:
                EditText phone = (EditText) getView().findViewById(R.id.phone);
                EditText fullname = (EditText) getView().findViewById(R.id.full_name);
                EditText idNo = (EditText) getView().findViewById(R.id.id_number);
                EditText address = (EditText) getView().findViewById(R.id.address);
                if (!requestFocus(phone) || !requestFocus(fullname) || !requestFocus(idNo) || !requestFocus(address)) {
                    return;
                }
                updateProfile(avatarValue, phone.getText().toString(), fullname.getText().toString(), idNo.getText().toString(), address.getText().toString());

        }
    }

    private boolean requestFocus(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.requestFocus();
            editText.setFocusable(true);
            editText.setError("require");
            return false;
        }
        return true;
    }
    //endregion
}
