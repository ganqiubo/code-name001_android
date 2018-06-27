package tl.pojul.com.fastim.View.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    public BaseFragment() {
        // Required empty public constructor
    }

    public void showShortToas(String msg){
        try{
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }catch(Exception e){
        }
    }

    public void showLongToas(String msg){
        try{
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }catch(Exception e){
        }
    }

}
