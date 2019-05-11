package com.mingrisoft.sockword;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryWordFragment extends Fragment {
    //变量定义
    public static  final String  TAG="HistoryWordFragment";

    private List<String> historyWord;             //用于保存历史查询过的单词
    private String [] data=new String[20];
    private HistoryWordAdapter adapter;
    private ListView listView;
    private   View view;
    private SharedPreferences sharedPreferences;  //数据库
    private Set<String>  wordRecord;              //历史记录表
    private LayoutInflater inflater;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //初始化布局
        view=inflater.inflate(R.layout.history_word_fragment,null);
        listView=(ListView) view.findViewById(R.id.history_word_list);
        inflater=getActivity().getLayoutInflater();

        for(int i=0;i<data.length;i++) {
          data[i]="";
        }
        adapter=new HistoryWordAdapter(inflater,data);
        init();
        listView.setAdapter(adapter);

        //给ListView的Item设置点击事件
        //TODO：点击单词记录后直接转入搜索该单词
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView wordText=(TextView) view.findViewById(R.id.history_word);
                if(!wordText.getText().toString().equals("")&&position<wordRecord.size()) {
                    //碎片与活动间的通信
                    //1.通过getActivity()获得与碎片相关联的活动注意要强制转换
                    //2.通过活动调用方法
                    TranslateActivity activity = (TranslateActivity) getActivity();
                    activity.translationWordFromAnotherFragment(wordText.getText().toString());
                }
            }
        });



        return view;
    }



    private void init(){
        sharedPreferences=getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);

        if(sharedPreferences.getStringSet("wordrecord",null)!=null) {

            //TODO:取出来数据顺序不一致
            wordRecord=new LinkedHashSet<>(sharedPreferences.getStringSet("wordrecord",null));

            Iterator<String> iterator = wordRecord.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                //if (i < 4)

                    data[i++] = iterator.next();
            }
        }


        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }
}
