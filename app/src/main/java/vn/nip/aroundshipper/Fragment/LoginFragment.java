package vn.nip.aroundshipper.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText username = (EditText) view.findViewById(R.id.username);
        final EditText password = (EditText) view.findViewById(R.id.password);
        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    StorageHelper.resetPhone();
                    String name = username.getText().toString();
                    String pass = password.getText().toString();
                    if (name.equals("")) {
                        username.setError(getString(R.string.error));
                        return;
                    }
                    if (pass.equals("")) {
                        password.setError(getString(R.string.error));
                        return;
                    }
                    SmartFoxHelper.ActionLogin(name, pass);
                } catch (Exception e) {

                }
            }
        });

        return view;
    }

}
