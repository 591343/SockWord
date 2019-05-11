package com.mingrisoft.sockword;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.mingrisoft.util.AndroidStateDetection;
import com.mingrisoft.util.TranslationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单词句子查询页面
 */
public class TranslateActivity extends AppCompatActivity implements View.OnClickListener {
    //变量声明
    public static final String TAG = "TranslateActivity";
    private ImageView bakcBtn, clearBtn;                   //返回按钮,清除按钮
    private EditText editText;                  //单词句子编辑框
    private Button translationBtn;            //翻译按钮
    private WordDetailFrament wordDetailFragment;          //单词详情碎片
    private HistoryWordFragment historyWordFragment;         //历史记录碎片
    private FragmentTransaction transaction;      //碎片事务
    private SharedPreferences sharedPreferences;      //数据库操作
    private SharedPreferences.Editor editor;          //编辑者


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation_layout);
        //初始化控件
        init();

        historyWordFragment = new HistoryWordFragment();
        wordDetailFragment = new WordDetailFrament();
        setFragment(historyWordFragment);

    }


    /**
     * 初始化控件
     */
    private void init() {

        bakcBtn = (ImageView) findViewById(R.id.translate_back_btn);
        bakcBtn.setOnClickListener(this);
        clearBtn = (ImageView) findViewById(R.id.tran_clear);
        clearBtn.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.tran_edit);
        translationBtn = (Button) findViewById(R.id.tran_btn);
        translationBtn.setOnClickListener(this);

        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //界面返回
            case R.id.translate_back_btn:
                finish();
                break;

            //执行翻译
            case R.id.tran_btn:
                //设置更换碎片并且执行翻译
                translationWordFromEditText();

                break;
            case R.id.tran_clear:
                if (!editText.getText().toString().equals("")) {
                    editText.setText("");
                }
                historyWord(historyWordFragment);

                break;
        }
    }

    /**
     * 设置碎片(也就是进行碎片的更换)
     *
     * @param fragment
     */
    private void setFragment(Fragment fragment) {
        //开启碎片交易

        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.tran_frame_layout, fragment);
        transaction.commit();


    }

    //进入单词详情页面
    private void wordDetail(WordDetailFrament wordDetailFragment) {
        if (wordDetailFragment == null) {
            wordDetailFragment = new WordDetailFrament();
        }

        setFragment(wordDetailFragment);

    }

    //单击进入设置界面
    private void historyWord(HistoryWordFragment historyWordFragment) {
        if (historyWordFragment == null) {
            historyWordFragment = new HistoryWordFragment();
        }
        setFragment(historyWordFragment);
    }

    //核心方法，在线程中执行翻译方法
    private void translationWordFromEditText() {
        //首先判断是否有wifi或者移动数据
        if(AndroidStateDetection.isMobile(this)||AndroidStateDetection.isNetworkAvailable(this))
        {
            wordDetail(wordDetailFragment);
            if (wordDetailFragment != null && (AndroidStateDetection.isMobile(this) || AndroidStateDetection.isNetworkAvailable(this))) {
                //开启翻译线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wordDetailFragment.translationStrat(editText.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else{
            Toast.makeText(this,"请打开wifi或移动数据",Toast.LENGTH_SHORT).show();
        }
    }

    //核心方法，从里一个碎片传来得数据来执行方法
    public void translationWordFromAnotherFragment(final String word) {

        //首先判断是否有wifi或者移动数据
        if (AndroidStateDetection.isMobile(this) || AndroidStateDetection.isNetworkAvailable(this)) {
            wordDetail(wordDetailFragment);
            editText.setText(word);
            editText.setSelection(word.length());

            if (wordDetailFragment != null) {

                //开启翻译线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wordDetailFragment.translationStrat(word);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else{
            Toast.makeText(this,"请打开wifi或移动数据",Toast.LENGTH_SHORT).show();
        }
    }


}

