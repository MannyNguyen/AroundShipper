package vn.nip.aroundshipper.Fragment;


import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import vn.nip.aroundshipper.Adapter.RatingAdapter;
import vn.nip.aroundshipper.Bean.BeanRate;
import vn.nip.aroundshipper.Helper.FragmentHelper;
import vn.nip.aroundshipper.Helper.SmartFoxHelper;
import vn.nip.aroundshipper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingInfoFragment extends BaseFragment {

    public int page = 1;
    public RecyclerView recycler;
    public List<BeanRate> rates;

    //region contructors
    public RatingInfoFragment() {
        // Required empty public constructor
    }
    //endregion


    //region init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_rating_info, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               FragmentHelper.pop(getActivity());
            }
        });
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(getString(R.string.rating_info));
        RatingBar rating = (RatingBar)getView().findViewById(R.id.full_rating);
        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.main), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.gray_400), PorterDuff.Mode.SRC_ATOP);
        rates = new ArrayList<>();
        recycler = (RecyclerView) getView().findViewById(R.id.recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RatingAdapter adapter = new RatingAdapter(getActivity(),  RatingInfoFragment.this, recycler, rates);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findLastCompletelyVisibleItemPosition() == rates.size() - 1 && rates.size() > 0) {
                    showProgress();
                    page++;
                    SmartFoxHelper.getRatingInfo(page);
                }
            }
        });
        showProgress();
        SmartFoxHelper.getRatingInfo(page);
    }

    //endregion


}
