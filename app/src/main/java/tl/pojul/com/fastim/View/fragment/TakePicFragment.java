package tl.pojul.com.fastim.View.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tl.pojul.com.fastim.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakePicFragment extends Fragment {


    public TakePicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_take_pic, container, false);
    }

}