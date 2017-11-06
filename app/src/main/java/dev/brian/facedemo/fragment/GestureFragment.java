package dev.brian.facedemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.brian.facedemo.R;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/11/6
 * 描述:
 */

public class GestureFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gesture,container,false);
        return view;
    }
}
