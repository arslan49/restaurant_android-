package com.example.foodmanagment.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodmanagment.R;


public class SystemUtils {
    private static Activity activityData;
    private static boolean status;



    public static void setActivity(Activity activity){
        activityData = activity;
    }
    public  static Activity getActivity(){
        return activityData;
    }
    public static boolean isActivityVisible(){
        return status;
    }
    public static void setActivityResume(){
        status = true;
    }
    public static void setActivityPause(){

//        PackageManager packageManager = SystemUtils.getActivity().getPackageManager();
//        try {
//            ActivityInfo info = packageManager.getActivityInfo(SystemUtils.getActivity().getComponentName(), 0);
//            Log.e("ActivitynamePaused:",   info.name);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        status = false;
    }



    /**
     * Hide Keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static  void vibrateMoible(Activity activity) {
        AudioManager am = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        Vibrator vibe;
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                vibe = (Vibrator)activity. getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                vibe = (Vibrator)activity. getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                break;
        }
    }

//    public static void showSuccessToast(String message,Activity activity){
//        Log.d("statusValue", String.valueOf(status));
//        if (isActivityVisible()) {
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View layout = inflater.inflate(R.layout.success_toast_layout, null);
//            TextView msg = layout.findViewById(R.id.toast_message);
//            msg.setText(message);
//            Toast toast = new Toast(activity);
//            toast.setDuration(Toast.LENGTH_LONG);
//            toast.setView(layout);
//            toast.show();
//        }
//    }
//    public static void showErrorToast(String message,Activity activity){
//        if (isActivityVisible()) {
//            LayoutInflater inflater = activity.getLayoutInflater();
//            View layout = inflater.inflate(R.layout.error_toast_layout,
//                    null);
//            TextView msg = layout.findViewById(R.id.toast_message);
//            msg.setText(message);
//            Toast toast = new Toast(activity);
//            toast.setDuration(Toast.LENGTH_LONG);
//            toast.setView(layout);
//            toast.show();
//        }
//    }
}
