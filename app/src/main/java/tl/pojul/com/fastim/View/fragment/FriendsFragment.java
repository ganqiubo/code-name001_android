package tl.pojul.com.fastim.View.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.FriendsAdapter;

public class FriendsFragment extends Fragment {

    @BindView(R.id.friends_list)
    RecyclerView friendsList;
    private Unbinder unbinder;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFriends();
    }

    private void getFriends() {
        List list = new ArrayList(){{
            add(1);
            add(1);
            add(1);
            add(1);
        }};
        FriendsAdapter friendsAdapter = new FriendsAdapter(getActivity(), list);
        friendsList.setAdapter(friendsAdapter);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        friendsList.setLayoutManager(layoutmanager);
        friendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
