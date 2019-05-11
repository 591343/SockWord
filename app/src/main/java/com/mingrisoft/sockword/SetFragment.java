package com.mingrisoft.sockword;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * 设置界面的逻辑
 */
public class SetFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private  SwitchButton switchButton;             //开关按钮
    private Spinner spinnerDifficulty;              //定义选择难度的下拉框
    private Spinner spinnerALLNum;                  //定义选择解锁题目的下拉框
    private Spinner spinnerNewNum;                  //定义新题目的下拉框
    private Spinner spinnerReviewNum;               //定义复习题的下拉框
    private ArrayAdapter<String> adapterDifficulty,adapterAllNum,
                                adapterNewNum,adapterReviewNum;    //定义下拉框的适配器

    //选择难度的下拉框内容
    String [] difficulty =new String[]{"小学","初中","高中","四级","六级"};

    //选择解锁题目的下拉框
    String [] allNum=new String[]{"2道","4道","6道","8道"};

    //新题目下拉框的选项内容
    String [] newNum=new String []{"10","30","50","100"};

    //复习题的下拉框
    String [] revicwNum= new String[] {"10","30","50","100"};

    SharedPreferences.Editor editor=null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.set_fragment_layout,null);
        init(view);
        return view;
    }


    /**
     * 初始化控件
     */

    private  void init(View view){
        sharedPreferences=getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        switchButton=(SwitchButton) view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);

        spinnerDifficulty=(Spinner) view.findViewById(R.id.spinner_difficulty);
        spinnerALLNum=(Spinner) view.findViewById(R.id.spinner_all_number);
        spinnerNewNum=(Spinner) view.findViewById(R.id.spinner_new_number);
        spinnerReviewNum=(Spinner) view.findViewById(R.id.spinner_revise_number);

        //初始化选择难度下拉框的适配器
        adapterDifficulty=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,difficulty);
        //给下拉框设置适配器
        spinnerDifficulty.setAdapter(adapterDifficulty);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        //设置选择难度下拉框的监听事件
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("difficulty",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * 解锁题个数的选项框，同上面的选择难度的选项框的原理一样
         */

        //初始化解锁题目下拉框的适配器
        adapterAllNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,allNum);
        //给下拉框设置适配器
        spinnerALLNum.setAdapter(adapterAllNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerALLNum,sharedPreferences.getInt("allNum",2)+"道");
        //设置选择难度下拉框的监听事件
        this.spinnerALLNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                int ii=Integer.parseInt(msg);
                editor.putInt("allNum",ii);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * 每日新题个数的选项框，同上面的选择难度的选项框的原理一样
         */

        //初始化每日新题个数下拉框的适配器
        adapterNewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,newNum);
        //给下拉框设置适配器
        spinnerNewNum.setAdapter(adapterNewNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerNewNum,sharedPreferences.getString("newNum","10"));
        //设置选择难度下拉框的监听事件
        this.spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("newNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /**
         *每日复习题个数的选项框，同上面的选择难度的选项框原理一样
         */

        //初始化选择难度下拉框的适配器
        adapterReviewNum=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,revicwNum);
        //给下拉框设置适配器
        spinnerReviewNum.setAdapter(adapterReviewNum);
        //定义选择难度下拉框的默认选项
        setSpinnerItemSelectedByValue(spinnerReviewNum,sharedPreferences.getString("reviewNum","10"));
        //设置选择难度下拉框的监听事件
        this.spinnerReviewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //获取选择的内容
                String msg=adapterView.getItemAtPosition(i).toString();
                editor.putString("reviewNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    /**
     * 每次进入设置页面时下拉框显示最新选择的数据
     * @param spinner
     * @param value
     */
    public  void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter spinnerAdapter=spinner.getAdapter();
        int k=spinnerAdapter.getCount();
        for (int i=0;i<k;i++){
            if(spinnerAdapter.getItem(i).toString().equals(value)){
                //默认选中项
                spinner.setSelection(i,true);
            }
        }
    }


    /**
     * 给开关设置了点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_btn:
                if(switchButton.isSwitchOpen()){
                    switchButton.closeSwitch();
                    editor.putBoolean("btnTf",false);
                }else{
                    switchButton.openSwitch();
                    editor.putBoolean("btnTf",true);
                }

                editor.commit();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /**
         * 从数据获取开关按钮的状态
         */
        //如果返回为true则将解锁开关打开,否则关闭
        if(sharedPreferences.getBoolean("btnTf",false)){
            switchButton.openSwitch();
        }else {
            switchButton.closeSwitch();
        }
    }
}
