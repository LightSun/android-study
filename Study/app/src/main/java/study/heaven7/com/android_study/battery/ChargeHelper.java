package study.heaven7.com.android_study.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by heaven7 on 2017/3/3.
 */
public class ChargeHelper {

    /**
     * 通常，如果设备连接了交流充电器，您应该最大限度提高后台更新的频率；而如果设备是通过 USB 充电，
     * 则应降低更新频率，如果电池正在放电，则应进一步降低更新频率。监控充电状态
     * @param context
     */
    public void testCharge(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        //您可以提取当前充电状态，并且如果设备正在充电，则还可以提取设备是通过 USB 还是交流充电器进行充电。
// Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
// How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        //确定当前电池电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;

    }


}
