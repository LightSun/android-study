package study.heaven7.com.android_study.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by heaven7 on 2017/3/3.
 */
//每当设备电池电量不足或退出不足状态时，便会触发该接收器。
public class BatteryLevelReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //see ChargeHelper
    }
}
