package com.hengda.blemanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

public class CarPortSpinner extends PopupWindow implements OnItemClickListener {

    private String[] mItems;
    private CarPortSpinner mWindow;
    private SpinnerListAdapter.onItemClickListener mListener;

    public CarPortSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarPortSpinner(Activity activity, int width, String[] items) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.myspinner_list, null);
        // 设置PopupWindow的View
        this.setContentView(contentView);
        // 设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置PopupWindow弹出窗体的高
        this.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //让pop可以点击外面消失掉
        this.setBackgroundDrawable(new ColorDrawable(0));
        // 刷新状态
        this.update();
        /*// 实例化一个ColorDrawable颜色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        this.setBackgroundDrawable(dw);*/

        this.mItems = items;
        ListView listView = (ListView) contentView.findViewById(R.id.lv_list);
        mWindow = this;
        SpinnerListAdapter adapter = new SpinnerListAdapter(mWindow, activity, mItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        CarPortSpinner.this.dismiss();
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void close() {
        this.dismiss();
    }

    public int position() {
        return 0;
    }

    public void setOnItemClickListener(SpinnerListAdapter.onItemClickListener listener) {
        this.mListener = listener;
    }

    public SpinnerListAdapter.onItemClickListener getListener() {
        //可以通过this的实例来获取设置好的listener
        return mListener;
    }
}
