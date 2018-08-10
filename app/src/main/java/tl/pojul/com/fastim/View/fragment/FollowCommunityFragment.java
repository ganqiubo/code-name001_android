package tl.pojul.com.fastim.View.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import tl.pojul.com.fastim.R;

public class FollowCommunityFragment extends BaseFragment {

    public ArrayList<Fragment> fragments = new ArrayList<>();
    private View view;


    public FollowCommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_follow_community, container, false);
        return view;
    }

}
