package com.kkw.logger;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 软键盘相关工具
 * @author kkw
 * @date 2023/11/14
 */
public class KeyboardUtil {

    /**
     * 显示键盘
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Fragment fragment) {
        FragmentActivity activity = fragment == null ? null : fragment.getActivity();
        if (activity == null) {
            return;
        }

        View currentFocus = activity.getCurrentFocus();
        if (currentFocus instanceof EditText) {
            currentFocus.clearFocus();
            hideKeyboard(currentFocus);
        }
    }

    /**
     * 切换键盘的显隐状态
     */
    public static void toggleSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, 0);
        }
    }

//    /**
//     * 解决点击EditText点击外部区域软键盘隐藏
//     */
//    public boolean dispatchTouchEvent(Activity hostActivity, MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = hostActivity.getCurrentFocus();
//            if (isShouldHideInput(v, ev)) { //需要隐藏软键盘
//                hideKeyboard(v);
////                InputMethodManager imm = (InputMethodManager) hostActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
////                if (imm != null) {
////                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
////                }
////                mEditText.setCursorVisible(false);
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//        // 必不可少，否则所有的组件都不会有TouchEvent了
//        if (getWindow().superDispatchTouchEvent(ev)) {
//            return true;
//        }
//        return onTouchEvent(ev);
//    }

    /**
     * 判断当前点击的位置是否为EditText
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if ((v instanceof EditText)) { //如果点击的view是EditText
                int[] leftTop = {0, 0};
                //获取输入框当前的location位置
                v.getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + v.getHeight();
                int right = left + v.getWidth();
                // 点击的是输入框区域，保留点击EditText的事件
                return !(event.getX() > left) || !(event.getX() < right)
                        || !(event.getY() > top) || !(event.getY() < bottom);
            }
        }
        return false;
    }
}
