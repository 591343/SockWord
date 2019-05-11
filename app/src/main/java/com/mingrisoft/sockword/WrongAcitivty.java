package com.mingrisoft.sockword;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.mingrisoft.greendao.entity.greendao.CET4Entity;
import com.mingrisoft.greendao.entity.greendao.CET4EntityDao;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;
import com.mingrisoft.sockword.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/7/23 0023.
 */
public class WrongAcitivty extends AppCompatActivity implements View.OnClickListener, SynthesizerListener {

    private TextView chinaText, wordText, englishText;      //用来显示单词和音标的
    private SharedPreferences sharedPreferences;            //定义轻量级数据库
    private SharedPreferences.Editor editor;                //数据库编辑器
    private ImageView playVioce, backBtn;                 //播放声音
    private Button iKnowBtn;                                  //“我会了”按钮


    private SpeechSynthesizer speechSynthesizer;    //合成对象

    private SQLiteDatabase db;      //定义数据库

    private DaoMaster mDaoMaster;    // 数据库管理者

    private DaoSession mDaoSession;    // 与数据库进行会话
    // 对应的表,由java代码生成的,对数据库内相应的表操作使用此对象
    private CET4EntityDao questionDao;

    /**
     * 手指按下的点为（x1,y1）
     * 手指离开屏幕的点为（x2,y2）
     */
    float x1 = 0;
    float y1 = 0;
    float x2 = 0;
    float y2 = 0;

    List<CET4Entity> datas;
    List<CET4Entity> wrongData;         //定义一个list泛型为CET4Entity
    int wrongNum = 0;                   //定义一个int型数据
    int[] wrongArry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wrong_layout);      //绑定布局文件
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);        //初始化数据库
        editor = sharedPreferences.edit();                          //初始化编辑器
        chinaText = (TextView) findViewById(R.id.china_text);           //汉语绑定id
        englishText = (TextView) findViewById(R.id.english_text);           //音标绑定id
        wordText = (TextView) findViewById(R.id.word_text);             //单词绑定id
        playVioce = (ImageView) findViewById(R.id.play_vioce);          //播放声音按钮绑定id
        playVioce.setOnClickListener(this);                             // 播放声音设置监听事件
        iKnowBtn = (Button) findViewById(R.id.i_know_btn);              //“我会了”按钮绑定id
        iKnowBtn.setOnClickListener(this);                              //“我会了”按钮设置监听事件
        backBtn = (ImageView) findViewById(R.id.back_btn);                  //返回按钮绑定id
        backBtn.setOnClickListener(this);                               //返回按钮设置监听事件
        // 通过管理对象获取数据库
        // 对数据库进行操作
        // 此DevOpenHelper类继承自SQLiteOpenHelper,第一个参数Context,第二个参数数据库名字,第三个参数CursorFactory
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wrong.db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();
        setData(wrongNum);      //设置错题
//        setWrongData();
        setParam();             //初始化语音播报
        SpeechUser.getUser().login(WrongAcitivty.this,null,null,"appid=5cb7368a",listener);
    }

//    /**
//     * 从数据库里面取出数据
//     * 并设置到数组里面
//     * */
//    private void setWrongData() {
//        wrongData = new ArrayList<>();
//        String str = sharedPreferences.getString("wrongId", "").substring(1);
//        String[] strArray = str.split(",");
//        wrongArry = new int[strArray.length];
//        for (int i = 0; i < strArray.length; i++) {
//            try {
//                wrongArry[i] = Integer.parseInt(strArray[i]);
//            }catch (Exception e){
//            }
//
//
//        }
//        for (int i = 0; i < wrongArry.length; i++) {
//            wrongData.add(i, datas.get(wrongArry[i]));
//        }
//        setData(wrongNum);
//
//    }


    /**
     * 初始化语音播报
     */
    public void setParam() {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "John");
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "70");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
    }

    /**
     * 设置错题
     */
    private void setData(int j) {
        iKnowBtn.setVisibility(View.VISIBLE);       //“我会了”按钮显示出来
        wrongData = new ArrayList<>();              //初始化list
        if (questionDao.queryBuilder().list() != null
                && questionDao.queryBuilder().list().size() > 0
                && j < questionDao.queryBuilder().list().size()
                && j >= 0) {            //判断如果数据库不为空

            for (int i = 0; i < questionDao.queryBuilder().list().size(); i++) {
                wrongData.add(i, questionDao.queryBuilder().list().get(i));         //把数据循环加到list里面
            }

            /**
             * 分别将list里面的数据取出第j条数据设置单词音标以及汉语
             * */
            wordText.setText(wrongData.get(j).getWord());
            englishText.setText(wrongData.get(j).getEnglish());
            chinaText.setText(wrongData.get(j).getChina());

        } else {
            /**
             * 如果数据库为空
             * 隐藏“我会了”按钮
             * */
            wordText.setText("好厉害");
            englishText.setText("[没有]");
            chinaText.setText("不会的单词了");
            iKnowBtn.setVisibility(View.GONE);
        }

    }

    /**
     * 复写activity的onTouch方法
     * 监听滑动事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if (x1 - x2 > 200) {                            //向左滑
                if (wrongNum + 2 > questionDao.queryBuilder().list().size()) { //判断如果数据库里面没有数据了
                    Toast.makeText(this, "没有更多的数据了", Toast.LENGTH_SHORT).show();
                } else {        //否则有数据进行数据设置
                    wrongNum++;         //定义的int行数据+1
                    setData(wrongNum);      //设置数据

                }

            } else if (x2 - x1 > 200) {                    //向右滑
                if (wrongNum - 1 < 0) {                     //判断是不是第一条数据
                    Toast.makeText(this, "没有更多的数据了", Toast.LENGTH_SHORT).show();
                } else {       // 如果不是
                    wrongNum--;     //定义的int数据-1
                    setData(wrongNum);      //设置数据

                }
            }
        }
        return super.onTouchEvent(event);
    }


    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.play_vioce:                   //播放单词声音
                String text = wordText.getText().toString();    //获取文本
                speechSynthesizer.startSpeaking(text, this);        //传给后台
                break;
            case R.id.i_know_btn:           //我会了  按钮的点击操作
                if (wrongNum == 0) {
                    questionDao.deleteByKey(questionDao.queryBuilder().list().get(wrongNum).getId());       //从数据库删除该条数据
                    setData(wrongNum++);        //刷新数据
                } else if (wrongNum == questionDao.queryBuilder().list().size()) {
                    questionDao.deleteByKey(questionDao.queryBuilder().list().get(wrongNum).getId());       //从数据库删除该条数据
                    setData(wrongNum--);        //刷新数据
                } else {
                    questionDao.deleteByKey(questionDao.queryBuilder().list().get(wrongNum).getId());       //从数据库删除该条数据
                    setData(wrongNum--);            //刷新数据
                }
                break;
            case R.id.back_btn:                 //返回按钮
                finish();                       //返回上一个页面
                break;

        }
    }

    //缓冲进度回调通知
    @Override
    public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
        // TODO Auto-generated method stub
    }

    //结束回调
    @Override
    public void onCompleted(SpeechError arg0) {
        // TODO Auto-generated method stub

    }

    //开始播放
    @Override
    public void onSpeakBegin() {
        // TODO Auto-generated method stub

    }

    //暂停播放
    @Override
    public void onSpeakPaused() {
        // TODO Auto-generated method stub

    }

    //播放进度
    @Override
    public void onSpeakProgress(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //继续播放
    @Override
    public void onSpeakResumed() {
        // TODO Auto-generated method stub

    }

    /**
     * 通用回调接口
     */
    private SpeechListener listener = new SpeechListener() {

        //消息回调
        @Override
        public void onEvent(int arg0, Bundle arg1) {
            // TODO Auto-generated method stub

        }

        //数据回调
        @Override
        public void onData(byte[] arg0) {
            // TODO Auto-generated method stub

        }

        //结束回调（没有错误）
        @Override
        public void onCompleted(SpeechError arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }
}
