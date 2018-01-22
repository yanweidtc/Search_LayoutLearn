package scut.carson_ho.george;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import scut.carson_ho.search_layout.R;
import scut.carson_ho.searchview.SearchListView;

/**
 * Created by Administrator on 2018/1/17.
 */

public class WeiView extends LinearLayout {

    /**
     * 初始化成员变量
     * @param context
     */
    private Context context;

    // 搜索框组件
    private EditText et_wei_search; //搜索按键
    private TextView tv_wei_clear;  //删除搜索记录按键
    private LinearLayout wei_block; //搜索框布局
    private ImageView iv_wei_searchBack; //返回按键

    // ListView列表 & 适配器
    private SearchListView listView;
    private BaseAdapter adapter;


    // 数据库变量
    // 用于存放历史搜索记录
    private WeiSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    // 回调接口
    private WeiICallBack mCallBack;
    private WeiBackCallBack bCallBack;


    //自定义属性设置
    // 1. 搜索字体属性设置：大小、颜色 & 默认提示
    private Float textSizeSearch;
    private int textColorSearch;
    private String textHintSearch;

    //2. 搜索框设置： 高度&颜色
    private int searchBlockHeight;
    private int searchBlockColor;

    public WeiView(Context context){
        super(context);
        this.context = context;
    }

    public WeiView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
    }

    public WeiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     */

    private void initAttrs(Context context, AttributeSet attrs) {
        // 控件资源名称
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeiView) ;

        // 搜索框字体大小（dp）
        textSizeSearch = typedArray.getDimension(R.styleable.WeiView_textSizeWei, 20);

        // 搜索框字体颜色
        int defaultColor = context.getResources().getColor(R.color.colorText);// 默认颜色 = 灰色
        textColorSearch = typedArray.getColor(R.styleable.WeiView_textColorWei, defaultColor);

        // 搜索框提示内容（String）
        textHintSearch = typedArray.getString(R.styleable.WeiView_textHintWei);

        // 搜索框高度
        searchBlockHeight = typedArray.getInteger(R.styleable.Search_View_searchBlockHeight,150);

        // 搜索框颜色
        int defaultColor2 = context.getResources().getColor(R.color.colorDefault);// 默认颜色 = 白色
        searchBlockColor = typedArray.getColor(R.styleable.Search_View_searchBlockColor,defaultColor2);

        // 释放资源
        typedArray.recycle();

    }

    /**
     * 关注b
     * 初始化搜索框
     */

    private void init() {

        // 1. 初始化UI组件->>关注c
        initView();

        // 2. 实例化数据库SQLiteOpenHelper子类对象
        helper = new WeiSQLiteOpenHelper(context);

        // 3. 第1次进入时查询所有的历史搜索记录
        queryData("");

        /**
         *  清空搜索历史 按钮
         */
        tv_wei_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 清空数据库->>关注2
                deleteData();
                // 模糊搜索空字符 = 显示所有的搜索历史 （此时是没有搜索记录的）
                queryData("");
            }
        });

        /**
         * 监听输入键盘更换后的搜索按键
         * 调用时刻：点击键盘上的搜索键时
         */
        et_wei_search.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    //1. 点击搜素按键后， 根据输入的搜索字段进行查询
                    // 注：由于此处需求会根据自身情况不同而不同
                    if (!(mCallBack == null)){
                        mCallBack.SearchAction(et_wei_search.getText().toString());
                    }
                    Toast.makeText(context, "需要搜索的是" + et_wei_search.getText(), Toast.LENGTH_SHORT);
                    // 2. 点击搜索键后， 对该搜索字段在数据库是否存在进行检查（查询） ->> 关注1
                    boolean hasData = hasData(et_wei_search.getText().toString().trim());
                    // 3. 若存在，则不保存； 若不存在，则将该搜索字段保存（插入）到数据库，并作为历史搜索记录
                    if (!hasData){
                        insertData(et_wei_search.getText().toString().trim());
                        queryData("");
                    }
                }
                return false;
            }
        });

        /**
         * 搜索框的文本变化实时监听
         *
         */
        et_wei_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //
            @Override
            public void afterTextChanged(Editable s) {
                // 每次输入后，模糊查询数据库 & 显示
                // 注：若搜索框为空，则模糊搜索空字符 = 显示所有的搜索历史
                String tempName = et_wei_search.getText().toString();
                queryData(tempName); // ->关注1
            }
        });

        /**
         * 搜索记录列表（ListView）监听
         * 即当用户点击搜索历史里的字段后，会直接将结果当做搜索字段进行搜索
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获取用户点击列表里的文字，并自动填充到搜索框内
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String name = textView.getText().toString();
                et_wei_search.setText(name);
            }
        });

    }

    /**
     * 关注c:
     * 绑定搜索框xml视图
     */
    private void initView() {

        // 1. 绑定R.layout.search
        LayoutInflater.from(context).inflate(R.layout.wei_layout,this);

        // 2. 绑定搜索框EditText
        et_wei_search = (EditText) findViewById(R.id.et_Wei_search);
        et_wei_search.setTextSize(textSizeSearch);
        et_wei_search.setTextColor(textColorSearch);
        et_wei_search.setHint(textHintSearch);

        // 3. 搜索框背景颜色
        wei_block = (LinearLayout) findViewById(R.id.wei_block);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) wei_block.getLayoutParams();
        params.height = searchBlockHeight;
        wei_block.setBackgroundColor(searchBlockColor);
        wei_block.setLayoutParams(params);

        // 4. 历史搜索记录
        listView = (SearchListView) findViewById(R.id.wei_listView);

        // 5. 删除历史搜索记录 按钮
        tv_wei_clear = (TextView) findViewById(R.id.wei_tv_clear);
        tv_wei_clear.setVisibility(INVISIBLE);

        //6. 返回按键
        iv_wei_searchBack  = (ImageView) findViewById(R.id.wei_back);

    }

    /**
     * 关注1
     * 模糊查询数据 & 显示到ListView列表上
     */
    private void queryData(String tempName){
        // 1. 模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 2. 创建adapter适配器对象 & 装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1,cursor,new String[] { "name" },
                new int[] { android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 3. 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        System.out.println(cursor.getCount());
        // 当输入框为空 & 数据库中有搜索记录时， 显示 “删除搜索记录按钮”按钮
        if (tempName.equals("") && cursor.getCount() != 0){
            tv_wei_clear.setVisibility(VISIBLE);
        }
        else {
            tv_wei_clear.setVisibility(INVISIBLE);
        };

    }

    /**
     * 关注2：清空数据库
     */
    private void deleteData() {

        db = helper.getWritableDatabase();
        db.execSQL("delete from weirecords");
        db.close();
        tv_wei_clear.setVisibility(INVISIBLE);

    }

    /**
     * 关注3
     * 检查数据库中是否已经有该搜索记录
     */
    private boolean hasData(String tempName) {
        // 从数据库中Record表里找到
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from weirecords where name =?", new String[]{tempName}
        );
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 关注4
     * 插入数据到数据库，即写入搜索字段到历史搜索记录
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 点击键盘中搜索键后的操作，用于接口回调
     */
    public void setOClickSearch(WeiICallBack mCallBack){
        this.mCallBack = mCallBack;
    }

    /**
     * 点击返回后的操作，用于接口回调
     */
    public void setOnClickBack(WeiBackCallBack bCallBack) {
        this.bCallBack = bCallBack;
    }
}
