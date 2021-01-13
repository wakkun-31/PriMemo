package com.wata.primemo.view.viewparts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.wata.primemo.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TopHelpFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TopHelpFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() start");

        View layoutView = inflater.inflate(R.layout.fragment_top_help, container, false);

        ImageButton btnCancel = layoutView.findViewById(R.id.btn_close_help);
        btnCancel.setOnClickListener(this);

        ArrayList<String> infoText = new ArrayList<>();
        InputStream inputStream = null;
        if(savedInstanceState == null) {
            // Assetsフォルダのヘルプ文章を、1行ごとのリスト形式で取得する
            try {
                inputStream = getResources().getAssets().open("quick_guide_info.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String strInfo = null;

                while ((strInfo = reader.readLine()) != null) {
                    infoText.add(strInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onCreateView() failed");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // このフラグメントのリストビューに文章をセットする
            ListView listView = layoutView.findViewById(R.id.list_help_info);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.line_top_help, infoText);
            listView.setAdapter(adapter);
        }

        Log.d(TAG, "onCreateView() end");
        // 文章をセットしたフラグメントを表示する
        return layoutView;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() start");
        Log.d(TAG, "onClick() button: " + v.toString());

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        switch(v.getId()) {
            case R.id.btn_close_help:
                // ヘルプフラグメントが全面に出ているとき、後面アクティビティ(MainTopActivity)に
                // あるボタンが反応してしまうため、ボタンエリアをなくしていた
                // ヘルプ画面を閉じる際は、ボタンエリアを戻す
                getActivity().findViewById(R.id.fl_top_main).setVisibility(View.VISIBLE);

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.remove(this).commit();
                break;
            default:
                break;
        }
        Log.d(TAG, "onClick() end");
    }
}
