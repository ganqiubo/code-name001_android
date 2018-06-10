package tl.pojul.com.fastim.View.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    public BaseFragment() {
        // Required empty public constructor
    }

    public void showShortToas(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

}
