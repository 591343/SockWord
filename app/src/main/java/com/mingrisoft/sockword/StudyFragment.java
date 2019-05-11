package com.mingrisoft.sockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;
import com.mingrisoft.greendao.entity.greendao.WisdomEntity;
import com.mingrisoft.greendao.entity.greendao.WisdomEntityDao;

import java.util.List;
import java.util.Random;

public class StudyFragment extends Fragment {
    //依此是学习难度，名言(英语),名言(汉语),总共学习,掌握单词，答错题数
    private TextView difficultyTv, wisdomEnglish, wisdomChina,
            alreadyStudyText, alreadyMasteredText, wrongText;

    private SharedPreferences sharedPreferences;     //定义轻量级数据库
    private DaoMaster mDaoMaster;                    //数据库管理者
    private DaoSession mDaoSession;                   //与数据库进行会话

    private WisdomEntityDao questionDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //绑定布局文件
        View view = inflater.inflate(R.layout.study_fragment, null);
        //初始化数据库
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        difficultyTv = (TextView) view.findViewById(R.id.difficulty_text);
        wisdomEnglish = (TextView) view.findViewById(R.id.wisdom_english);
        wisdomChina = (TextView) view.findViewById(R.id.wisdom_china);
        alreadyStudyText = (TextView) view.findViewById(R.id.already_study);
        alreadyMasteredText = (TextView) view.findViewById(R.id.already_mastered);
        wrongText = (TextView) view.findViewById(R.id.wrong_text);

        //初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getActivity());

        //获取管理对象，因为数据库需要通过管理对象才能获取

        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        //获取数据
        questionDao = mDaoSession.getWisdomEntityDao();
        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty","四级")+"英语");
        List<WisdomEntity> datas=questionDao.queryBuilder().list();
        Random random=new Random();
        int i=random.nextInt(10);
        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();
    }

    /**
     * 设置十字内的各个单词书(从轻量级数据库中获取数据)
     */
    private void setText(){
        alreadyMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }
}
