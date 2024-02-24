package vn.nip.aroundshipper.Adapter;

import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.nip.aroundshipper.Bean.BeanFullOrderButton;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

public class FullOrderButtonAdapter extends RecyclerView.Adapter<FullOrderButtonAdapter.MyViewHolder> implements View.OnClickListener {

    final String POINT_1 = "POINT_1";
    final String POINT_2 = "POINT_2";
    final String POINT_3 = "POINT_3";
    final String POINT_4 = "POINT_4";
    final String POINT_5 = "POINT_5";
    final String POINT_6 = "POINT_6";
    final String POINT_7 = "POINT_7";
    final String POINT_8 = "POINT_8";
    final String POINT_9 = "POINT_9";

    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanFullOrderButton> list;

    public FullOrderButtonAdapter() {
        loadButton();
    }

    public FullOrderButtonAdapter(FragmentActivity activity, RecyclerView recycler) {
        loadButton();
        this.activity = activity;
        this.recycler = recycler;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_fullorder_button, parent, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BeanFullOrderButton item = list.get(position);
        holder.textLocation.setText(item.getTextLocation());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void loadButton() {
        list = new ArrayList<>();
        list.add(new BeanFullOrderButton(POINT_1, GlobalClass.getActivity().getString(R.string.point_1)));
        list.add(new BeanFullOrderButton(POINT_2, GlobalClass.getActivity().getString(R.string.point_2)));
        list.add(new BeanFullOrderButton(POINT_3, GlobalClass.getActivity().getString(R.string.point_3)));
        list.add(new BeanFullOrderButton(POINT_4, GlobalClass.getActivity().getString(R.string.point_4)));
        list.add(new BeanFullOrderButton(POINT_5, GlobalClass.getActivity().getString(R.string.point_5)));
        list.add(new BeanFullOrderButton(POINT_6, GlobalClass.getActivity().getString(R.string.point_6)));
        list.add(new BeanFullOrderButton(POINT_7, GlobalClass.getActivity().getString(R.string.point_7)));
        list.add(new BeanFullOrderButton(POINT_8, GlobalClass.getActivity().getString(R.string.point_8)));
        list.add(new BeanFullOrderButton(POINT_9, GlobalClass.getActivity().getString(R.string.point_9)));
    }

    @Override
    public void onClick(View view) {
//        try {
//            final int itemPosition = recycler.getChildLayoutPosition(view);
//            CmmFunc.setDelay(350, new ICallback() {
//                @Override
//                public void excute() {
//                    BeanFullOrderButton item = list.get(itemPosition);
//                    if (item != null) {
//                        switch (item.getId()) {
//                            case POINT_1:
//                                break;
//                            case POINT_2:
//                                break;
//                            case POINT_3:
//                                break;
//                            case POINT_4:
//                                break;
//                            case POINT_5:
//                                break;
//                            case POINT_6:
//                                break;
//                            case POINT_7:
//                                break;
//                            case POINT_8:
//                                break;
//                            case POINT_9:
//                                break;
//                        }
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            final MyViewHolder holder = new MyViewHolder(view);
            holder.framePoint.setBackgroundResource(R.color.main);
            return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textLocation;
        private FrameLayout framePoint;

        public MyViewHolder(View itemView) {
            super(itemView);
            textLocation = (TextView) itemView.findViewById(R.id.txt_location);
            framePoint = (FrameLayout) itemView.findViewById(R.id.frame_point);
        }
    }
}
