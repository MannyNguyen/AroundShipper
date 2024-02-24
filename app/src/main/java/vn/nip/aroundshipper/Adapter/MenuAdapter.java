package vn.nip.aroundshipper.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.aroundshipper.Bean.BeanMenu;
import vn.nip.aroundshipper.Class.CmmFunc;
import vn.nip.aroundshipper.Class.CmmVariable;
import vn.nip.aroundshipper.Class.GlobalClass;
import vn.nip.aroundshipper.Fragment.FullOrder.FullOrderFragment;
import vn.nip.aroundshipper.Fragment.IncomeFragment;
import vn.nip.aroundshipper.Fragment.OrderHistoryFragment;
import vn.nip.aroundshipper.Fragment.Partner.OrderPartnerFragment;
import vn.nip.aroundshipper.Fragment.ProfileFragment;
import vn.nip.aroundshipper.Fragment.RatingInfoFragment;
import vn.nip.aroundshipper.Fragment.ScheduleFragment;
import vn.nip.aroundshipper.Fragment.AroundWalletFragment;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Interface.ICallback;
import vn.nip.aroundshipper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> implements View.OnClickListener {

    //region Constance
    final String INFO = "INFO";
    final String WALLET = "WALLET";
    final String HISTORY = "HISTORY";
    final String ALL_SCHEDULE = "ALL_SCHEDULE";
    final String MY_SCHEDULE = "MY_SCHEDULE";
    final String INCOME = "INCOME";
    final String ORDER_PARTNER = "ORDER_PARTNER";
    final String HELP = "HELP";
    final String RATING_INFO = "RATING_INFO";
    //endregion

    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanMenu> list;

    public MenuAdapter() {
        loadMenu();
    }

    public MenuAdapter(FragmentActivity activity, RecyclerView recycler) {
        loadMenu();
        this.activity = activity;
        this.recycler = recycler;
    }

    private void loadMenu() {
        list = new ArrayList<>();
        list.add(new BeanMenu(INFO, GlobalClass.getActivity().getString(R.string.info), R.drawable.ic_info));
        list.add(new BeanMenu(WALLET, GlobalClass.getActivity().getString(R.string.around_wallet), R.drawable.leftmenu_around_wallet));
        //list.add(new BeanMenu(ALL_SCHEDULE, GlobalClass.getActivity().getString(R.string.schedule_list), R.drawable.ic_scheduled));
        list.add(new BeanMenu(MY_SCHEDULE, GlobalClass.getActivity().getString(R.string.my_schedule_list), R.drawable.ic_myscheduled));
        list.add(new BeanMenu(ORDER_PARTNER, GlobalClass.getActivity().getString(R.string.order_partner), R.drawable.ic_partner));
        list.add(new BeanMenu(HISTORY, GlobalClass.getActivity().getString(R.string.history), R.drawable.ic_history));
        list.add(new BeanMenu(INCOME, GlobalClass.getActivity().getString(R.string.income), R.drawable.ic_income));
        list.add(new BeanMenu(RATING_INFO, GlobalClass.getActivity().getString(R.string.rating_info), R.drawable.ic_ratinginfo));
        list.add(new BeanMenu(HELP, GlobalClass.getActivity().getString(R.string.help), R.drawable.ic_help));
        // list.add(new BeanMenu(LOGOUT, "Logout", R.drawable.ic_logout));

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanMenu item = list.get(position);
        holder.title.setText(item.getTitle());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.thumb.setImageDrawable(activity.getDrawable(item.getIcon()));
        } else {
            holder.thumb.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
        }

        if (position == list.size() - 1) {
            holder.line.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(final View view) {
        try {
            final int itemPosition = recycler.getChildLayoutPosition(view);
            DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            CmmFunc.setDelay(350, new ICallback() {
                @Override
                public void excute() {

                    BeanMenu item = list.get(itemPosition);
                    if (item != null) {
                        switch (item.getId()) {
                            case INFO:
                                FragmentHelper.addFragment(activity, R.id.home_content, new ProfileFragment());
                                break;
                            case WALLET:
                                FragmentHelper.addFragment(activity, R.id.home_content, new AroundWalletFragment());
                                break;
                            case ALL_SCHEDULE:
                                FragmentHelper.addFragment(activity, R.id.home_content, ScheduleFragment.newInstance(false));
                                break;
                            case MY_SCHEDULE:
                                FragmentHelper.addFragment(activity, R.id.home_content, ScheduleFragment.newInstance(true));
                                break;
                            case HISTORY:
                                FragmentHelper.addFragment(activity, R.id.home_content, new OrderHistoryFragment());
//                                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, FullOrderFragment.newInstance());
                                break;
                            case INCOME:
                                FragmentHelper.addFragment(activity, R.id.home_content, new IncomeFragment());
                                break;
                            case RATING_INFO:
                                FragmentHelper.addFragment(activity, R.id.home_content, new RatingInfoFragment());
                                break;
                            case HELP:
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", CmmVariable.phoneService, null));
                                activity.startActivity(intent);
                                break;
                            case ORDER_PARTNER:
                                FragmentHelper.replaceFragment(activity, R.id.home_content, OrderPartnerFragment.newInstance());
                                break;
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView thumb;
        private ImageView line;

        public MenuViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumb = (ImageView) view.findViewById(R.id.thumbnail);
            line = (ImageView) view.findViewById(R.id.line);
        }
    }
}