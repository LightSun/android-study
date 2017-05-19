package com.tt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cj on 2016/12/27.
 */

public class SkEgnManager {

    private static final String TAG = "SkEgnManager";

    public static final String SERVER_TYPE_CLOUD = "cloud";
    public static final String SERVER_TYPE_NATIVE = "native";

    public static final int CODE_CREATE_ENGINE_FAIL = 0;

    public static final int CODE_RESULT_OK = 1;

    public static final int STATUS_START_CREATE_ENGINE = 0;
    public static final int STATUS_CREATE_ENGINE_SUCCESS = 1;
    public static final int STATUS_CREATE_ENGINE_FAIL = 2;
    public static final int STATUS_ENGINE_ALREADY_EXISTS = 3;

    public enum engine_status{
        IDLE,
        RECORDING,
        STOP
    };
    engine_status status1 = engine_status.IDLE;

    private AIRecorder recorder = null;
    private long engine = 0;
    private String currentEngine;//当前引擎
    private JSONObject cfg = null;
    JSONObject params = null;

    private static SkEgnManager mSkEgnManager;

    private Context mContext;

    private String native_res_path2 = "%s/native.res";

    private SkEgnManager(Context context) {
        mContext = context;
    }

    public static SkEgnManager getInstance(Context context) {
        return mSkEgnManager == null ? mSkEgnManager = new SkEgnManager(context) : mSkEgnManager;
    }

    private Handler mHandler;

    private SkEgn.skegn_callback callback = new SkEgn.skegn_callback() {
        public int run(byte[] id, int type, byte[] data, int size) {
            if (type == SkEgn.SKEGN_MESSAGE_TYPE_JSON) {
                String result = new String(data, 0, size).trim();
                Message message = new Message();
                message.what = CODE_RESULT_OK;
                message.obj = result;
                mHandler.sendMessage(message);
            }
            return 0;
        }
    };

    //初始化引擎
    public void initEngine(String serverType, Handler handler){
        mHandler = handler;
        if(currentEngine==null || !currentEngine.equals(serverType)){
            if(currentEngine != null){
                SkEgn.skegn_delete(engine);
            }
            //开始初始化引擎
            mHandler.sendEmptyMessage(STATUS_START_CREATE_ENGINE);
            /* 初始化cfg */
            cfg = new JSONObject();
            try {
                cfg.put("appKey", AppConfig.appkey);
                cfg.put("secretKey", AppConfig.secretkey);

                InputStream is = null;
                if(serverType.equals(SERVER_TYPE_CLOUD)){
                    //云端引擎
                    cfg.put("cloud", new JSONObject("{\"server\": \"" + AppConfig.cloudServer_release
                            + "\", serverList:\"\"}"));
                }else{
                    //离线版
                    JSONObject sdkLogObj = new JSONObject();
                    sdkLogObj.put("enable", 1);
                    sdkLogObj.put("output", AiUtil.externalFilesDir(mContext) + "/sdklog.txt");
                    cfg.put("sdkLog", sdkLogObj);
                    String res_path = new String();
                    String resourceDir = new String(AiUtil.unzipFile(mContext,
                            "native.zip").toString());
                    res_path = String.format(native_res_path2, resourceDir);
                    cfg.put("native", res_path); // native_res_path2
                }

                is = mContext.getAssets().open(AppConfig.provision);

                File provisionFile = new File(
                        AiUtil.externalFilesDir(mContext),
                        "skegn.provision");
                AiUtil.writeToFile(provisionFile, is);
                is.close();
                cfg.put("provision", provisionFile.getAbsolutePath());
                System.out.println(cfg.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            engine = SkEgn.skegn_new(cfg.toString(), mContext);

            if(engine != CODE_CREATE_ENGINE_FAIL){
                //初始化引擎成功
                currentEngine = serverType;
                mHandler.sendEmptyMessage(STATUS_CREATE_ENGINE_SUCCESS);
            } else {
                //初始化引擎失败
                mHandler.sendEmptyMessage(STATUS_CREATE_ENGINE_FAIL);
            }

            recorder = AIRecorder.getInstance();
        }else{
            //引擎已存在
            mHandler.sendEmptyMessage(STATUS_ENGINE_ALREADY_EXISTS);
        }
    }

    /**
     * 初始化参数
     * @param coreType
     * @param refText
     * @param qType
     */
    public void initParams(String coreType, String refText, String qType) {
        params = new JSONObject();
        try {
            JSONObject audio = new JSONObject(
                    "{\"audioType\": \"wav\",\"sampleBytes\": 2,\"sampleRate\": 16000,\"channel\": 1,\"compress\": \"speex\"}");
            params.put("app", new JSONObject(
                    "{\"userId\":\"userId0\"}"));
            params.put("coreProvideType", currentEngine);
            JSONObject request = new JSONObject();
            if (coreType.equals(CoreType.EN_WORD_EVAL)){
                request.put("dict_type", "KK");
            }
            request.put("coreType", coreType);
            request.put("attachAudioUrl", 1);
            request.put("getParam", 0);
            if(refText != null){
                if (coreType.contains("open")) {
                    request.put("qClass", 2);
                    request.put("qType", Integer.parseInt(qType));
                }
                request.put("refText", refText);
            }
            params.put("audio", audio);
            params.put("request", request);
            System.out.println(params.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e("sss", "上传参数params===>" + params.toString());
    }

    //开始录制
    public void startRecord(String coreType, String refText, String qType, Handler handler){
        //取消之前引擎所有操作, 相当于reset操作
        SkEgn.skegn_cancel(engine);
        //初始化参数
        initParams(coreType, refText, qType);

        mHandler = handler;
        byte[] id = new byte[64];
        //即使同一个句子。每次id也不一样。
        int rv = SkEgn.skegn_start(engine, params.toString(), id,
                callback, mContext);
        if(rv!=0){
            setResult("skegn_start failed", coreType);
            return;
        }

        status1 = engine_status.RECORDING;

        String wavPath = AiUtil.getFilesDir(mContext).getPath()
                + "/record/" + new String(id).trim() + ".wav";
        recorder.start(wavPath, new AIRecorder.Callback() {
            public void run(byte[] data, int size) {
                SkEgn.skegn_feed(engine, data, size);
            }
        });
    }

    //停止录制
    public void stopRecord(){
        SkEgn.skegn_stop(engine);
        if(recorder != null){
            recorder.stop();
        }
        status1 = engine_status.STOP;
    }

    //回放
    public void playback(){
        if(recorder != null){
            recorder.playback();
        }
    }

    //引擎回收(停止)
    public void recycle(){
        currentEngine = null;
        if (engine != CODE_CREATE_ENGINE_FAIL) {
            SkEgn.skegn_delete(engine);
            status1 = engine_status.STOP;
        }
        if (recorder != null) {
            recorder.stop();
//            recorder.finalize();
            recorder = null;
        }
    }

    public String setResult(String r, String coreType)
    {
        StringBuilder keyValue = new StringBuilder();
        JSONObject json;
        JSONObject resultjson;
        try {
            json = new JSONObject(r);
            if(json != null && json.has("result")){
                resultjson = json.getJSONObject("result");
                if(resultjson.has("overall")){
                    keyValue.append("总    分: " + resultjson.getString("overall") + "\n");
                }
                if(resultjson.has("integrity")){
                    keyValue.append("完整度: " + resultjson.getString("integrity") + "\n");
                }
                if(resultjson.has("recognition")){
                    keyValue.append("识别结果: " + resultjson.getString("recognition") + "\n");
                }
                if(resultjson.has("confidence")){
                    keyValue.append("匹配度: " + resultjson.getString("confidence") + "\n");
                }
                if(resultjson.has("fluency")){
                    keyValue.append("流利度: " + resultjson.getString("fluency") + "\n");
                }
                if(resultjson.has("pronunciation")){
                    keyValue.append("发音得分：" + resultjson.getString("pronunciation") + "\n");
                }
                if(resultjson.has("speed")){
                    keyValue.append("语速：" + resultjson.getString("speed") + " 词/分\n");
                }
                if(resultjson.has("rear_tone") && coreType.equals(CoreType.EN_SENT_EVAL) && resultjson.getInt("overall") > 0){
                    keyValue.append("句末语调：" + resultjson.getString("rear_tone") + "\n");
                }
                if(coreType.equals(CoreType.EN_WORD_EVAL)){
                    keyValue.append("音素得分：/");
                    JSONArray wjsono = resultjson.getJSONArray("words");
                    JSONArray wjson = wjsono.getJSONObject(0).getJSONArray("phonemes");
                    for(int i=0; i<wjson.length(); i++)
                    {
                        keyValue.append(wjson.getJSONObject(i).getString("phoneme") + ":" + wjson.getJSONObject(i).getString("pronunciation") + " /");
                    }
                    keyValue.append("\n");
                }else if(coreType.equals(CoreType.EN_SENT_EVAL)){
                    keyValue.append("单词得分：\n");
                    JSONArray wjsono = resultjson.getJSONArray("words");
                    for(int i=0; i<wjsono.length(); i++){
                        String word = wjsono.getJSONObject(i).getString("word").replaceAll("\\.|\\,|\\!|\\;|\\?|\"", "");
                        if(word.startsWith("\'") || word.endsWith("\'")){
                            word = word.replace("\'", "");
                        }
                        keyValue.append(word + ": ");
                        keyValue.append(wjsono.getJSONObject(i).getJSONObject("scores").getString("overall") + "  ");
                    }
                    keyValue.append("\n");
                }
//                keyValue.append("\n结果详情:\n" + resultjson.toString(4));
                keyValue.append("\n结果详情:\n" + json.toString(4));
                r = "";
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return keyValue.append(r).toString();
    }

}
