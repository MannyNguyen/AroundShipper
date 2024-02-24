package vn.nip.aroundshipper;

import android.app.Activity;
import android.os.Bundle;

import com.smartfoxserver.v2.exceptions.SFSException;
import vn.nip.aroundshipper.R;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;

public class Main2Activity extends Activity implements IEventListener {
    SmartFox sfsClient = new SmartFox();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sfsClient.addEventListener(SFSEvent.CONNECTION, this);
        sfsClient.addEventListener(SFSEvent.SOCKET_ERROR, this);
        sfsClient.connect("app.around.vn", 9933);
    }

    @Override
    public void dispatch(final BaseEvent event) throws SFSException {
        if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
            if (event.getArguments().get("success").equals(true)) {
                // Login as guest in current zone

            } else {

            }
        } else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {

        } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {

        } else if (event.getType().equalsIgnoreCase(SFSEvent.SOCKET_ERROR)) {

        }
    }
}
