package scut.carson_ho.george;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import scut.carson_ho.search_layout.R;

/**
 * Created by Administrator on 2018/1/17.
 */

public class WeiView extends LinearLayout {

    /**
     * 初始化成员变量
     * @param context
     */
    private Context context;

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
}
