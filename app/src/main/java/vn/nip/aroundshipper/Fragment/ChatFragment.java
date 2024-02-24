package vn.nip.aroundshipper.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import vn.nip.aroundshipper.Adapter.ChatAdapter;
import vn.nip.aroundshipper.Bean.BeanChat;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.Helper.StorageHelper;
import vn.nip.aroundshipper.R;

import java.io.BufferedInputStream;
import java.io.InputStream;

import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.PublicMessageRequest;

public class ChatFragment extends BaseFragment implements View.OnClickListener {
    ChatAdapter adapter;
    //region Private variables
    RecyclerView recycler;
    EditText content;

    //endregion

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(int orderID) {

        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            CmmVariable.chats.clear();
            CmmVariable.numberChat = 0;
            StorageHelper.set("numberChat", "");
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            adapter = new ChatAdapter(getActivity(), recycler, CmmVariable.chats);
            ImageButton send = (ImageButton) view.findViewById(R.id.send);
            ImageButton pickup = (ImageButton) view.findViewById(R.id.pick_img);
            ImageButton shoot = (ImageButton) view.findViewById(R.id.shoot);
            content = (EditText) view.findViewById(R.id.content);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            layoutManager.setStackFromEnd(true);
            recycler.setLayoutManager(layoutManager);
            recycler.setItemViewCacheSize(100);
            recycler.setAdapter(adapter);

            send.setOnClickListener(this);
            pickup.setOnClickListener(this);
            shoot.setOnClickListener(this);


            getChatHistory();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText("Chat");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1999 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            new ActionSendImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }

        if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            new ActionSendImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                sendText();
                break;
            case R.id.pick_img:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                this.startActivityForResult(intent, 1999);
                break;
            case R.id.shoot:
                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                this.startActivityForResult(intent1, 2000);
                break;

        }
    }

    //endregion

    //region Actions
    void sendText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanChat bean = new BeanChat();
                    if (content.getText().toString().trim().length() == 0)
                        return;
                    bean.setChat_description(content.getText() + "");
                    bean.setMessage("TEXT_TYPE");
                    CmmVariable.chats.add(bean);

                    adapter.notifyItemInserted(CmmVariable.chats.size());
                    recycler.scrollToPosition(CmmVariable.chats.size() - 1);

                    SFSObject data = new SFSObject();
                    data.putUtfString("chat_description", content.getText() + "");
                    data.putInt("order_id_chat", getArguments().getInt("order_id"));
                    SmartFoxHelper.getInstance().send(new PublicMessageRequest(bean.getMessage(), data));

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "send", e.getMessage());
                } finally {
                    content.setText("");
                }
            }
        });

    }


    void getChatHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "GET_CHAT_HISTORY");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("shipper", sfsObject));
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "getChatHistory", e.getMessage());
                }
            }
        }).start();

    }

    class ActionSendImage extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            try {
                Intent data = (Intent) objects[0];
                Bitmap bmp = null;

                if (data.getData() == null) {
                    bmp = (Bitmap) data.getExtras().get("data");
                } else {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    bmp = BitmapFactory.decodeStream(bufferedInputStream);
                }
                bmp = CmmFunc.resizeBitmap(bmp, 400);
                byte[] arr = CmmFunc.bitmapToByteArray(bmp);
                BeanChat bean = new BeanChat();
                bean.setImage(arr);
                bean.setMessage("IMAGE_TYPE");
                bean.setBitmap(bmp);
                bean.setChat_description("");
                CmmVariable.chats.add(bean);


                ISFSObject sfsObject = new SFSObject();
                sfsObject.putByteArray("chat_description", arr);
                sfsObject.putInt("order_id_chat", getArguments().getInt("order_id"));
                SmartFoxHelper.getInstance().send(new PublicMessageRequest(bean.getMessage(), sfsObject));
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionSendImage", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(CmmVariable.chats.size());
                    recycler.scrollToPosition(CmmVariable.chats.size() - 1);
                }
            });

        }
    }
    //endregion


}
