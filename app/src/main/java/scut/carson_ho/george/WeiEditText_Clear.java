package scut.carson_ho.george;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import scut.carson_ho.search_layout.R;

/**
 * Created by Administrator on 2018/1/18.
 */

public class WeiEditText_Clear extends AppCompatEditText {


    /**
     * 步骤1：定义左侧搜索图标 & 一键删除图标
     */
    private Drawable clearDrawable, searchDrawable;

    public WeiEditText_Clear(Context context) {
        super(context);
    }

    public WeiEditText_Clear(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiEditText_Clear(Context context, AttributeSet attrs, int defSyleAttr) {
        super(context, attrs, defSyleAttr);
    }

    /**
     * 步骤2：初始化 图标资源
     */

    private void init() {
        clearDrawable = getResources().getDrawable(R.drawable.clear);
        searchDrawable = getResources().getDrawable(R.drawable.search);

        //设置左侧搜索图标
        setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null , null,null);
    }

    /**
     * 步骤3： 通过监听复写EditText本身的方法来确定是否显示删除图标
     * 监听方法： onTextChanged() & onFocusChanged()
     * 调用时刻： 当输入框内容变化时 & 焦点发生变化时
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setClearIconVisible(hasFocus() && text.length() > 0);
        // hasFocus() 返回是否获得EditText的焦点， 即是否选中
        // setClearIconVisible（）
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setClearIconVisible(focused && length() > 0);
    }

    /**
     * 关注1
     * 作用：判断是否显示删除图标
     */
    private void setClearIconVisible(boolean visible){
        /**
         *  ? : 表达式
         *  visible ? clearDrawable: null
         *  当visible 为true时 clearDrawable显示
         *  当visible 为false时 删除图标为null
         */
        setCompoundDrawablesWithIntrinsicBounds(searchDrawable,null,
                visible ? clearDrawable : null, null);
    }

    /**
     * 步骤4：对删除图标区域设置点击事件，即“点击 = 清空搜索框内容”
     * 原理：当手指抬起的位置在删除图标的区域，即视为点击了删除图标 = 清空搜索框内容
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                Drawable drawable = clearDrawable;
                if (drawable !=null && event.getX() <=(getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() -drawable.getBounds().width())){
                    setText("");
                }
        }
        return super.onTouchEvent(event);
    }
}
