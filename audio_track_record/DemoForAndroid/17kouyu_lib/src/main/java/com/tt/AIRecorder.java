/*******************************************************************************
 * Copyright 17kouyu
 * 录音机类
 ******************************************************************************/
package com.tt;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

//WAVE音频文件格式: http://www.2cto.com/os/201312/267541.html
public class AIRecorder {

    private static String TAG = "AIRecorder";

    private static int CHANNELS = 1; //单声道
    private static int BITS = 16; //采样大小
    private static int FREQUENCY = 16000; //音频采样率
    private static int INTERVAL = 50; // callback interval

    private static AIRecorder instance = null;
    private AudioRecord recorder = null;
    private AudioTrack player = null;

    private byte[] buffer = null;
    private String path = null; // wave file path

    private volatile boolean running = false;

    private Callback mCallback;
    private RecordTask mRecordTask;

    private PlayBackTask mPlayBackTask;

    public interface Callback {
        void run(byte[] data, int size);
    }

    public static AIRecorder getInstance() {
        return instance == null ? instance = new AIRecorder() : instance;
    }

    private AIRecorder() {
        int bufferSize = CHANNELS * FREQUENCY * BITS * INTERVAL / 1000 / 8;
        int minBufferSize = AudioRecord.getMinBufferSize(FREQUENCY, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (minBufferSize > bufferSize)
            bufferSize = minBufferSize;

        buffer = new byte[bufferSize];
        recorder = new AudioRecord(AudioSource.DEFAULT, FREQUENCY, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        player = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffer.length, AudioTrack.MODE_STREAM);
    }

    @Override
    protected void finalize() throws Throwable {
        recorder.release();
        player.release();
        super.finalize();
        Log.d(TAG, "released");
    }

    private int doubleCalculateVolume(byte[] buffer){
        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;
        for(int i = 0; i < buffer.length; i+=2){
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1] & 0xFF;
            int temp = v1 + (v2 << 8);// 小端
            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }
            sumVolume += Math.abs(temp);
        }
        avgVolume = sumVolume / buffer.length / 2;
        volume = Math.log10(1 + avgVolume) /** 10*/;
        return (int) volume;
    }

    public void start(final String path, final Callback callback) {
        this.path = path;
        this.mCallback = callback;
        mRecordTask = new RecordTask();

        Log.d(TAG, "start record");

        running = true;
        mRecordTask.execute(path);
    }

    public void stop() {
        if(running){
            running = false;
        }
    }

    public void playback() {
        stop();
        mPlayBackTask = new PlayBackTask();

        Log.d(TAG, "start playback");

        running = true;
        mPlayBackTask.execute();
    }

    private RandomAccessFile fopen(String path) throws IOException {
        File f = new File(path);

        if (f.exists()) {
            f.delete();
        } else {
            File parentDir = f.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
        }

        RandomAccessFile file = new RandomAccessFile(f, "rw");

        /* RIFF header */
        file.writeBytes("RIFF"); // riff id
        file.writeInt(0); // riff chunk size *PLACEHOLDER*
        file.writeBytes("WAVE"); // wave type

        /* fmt chunk */
        file.writeBytes("fmt "); // fmt id . 4 bytes
        file.writeInt(Integer.reverseBytes(16)); // fmt chunk size
        file.writeShort(Short.reverseBytes((short) 1)); // format: 1(PCM)
        file.writeShort(Short.reverseBytes((short) CHANNELS)); // channels: 1
        file.writeInt(Integer.reverseBytes(FREQUENCY)); // samples per second
        file.writeInt(Integer.reverseBytes((int) (CHANNELS * FREQUENCY * BITS / 8))); // BPSecond
        file.writeShort(Short.reverseBytes((short) (CHANNELS * BITS / 8))); // BPSample
        file.writeShort(Short.reverseBytes((short) (CHANNELS * BITS))); // bPSample

        /* data chunk */
        file.writeBytes("data"); // data id
        file.writeInt(0); // data chunk size *PLACEHOLDER*

        Log.i(TAG, "wav path: " + path + ", file size before record = " + file.length());//44
        return file;
    }

    private void fwrite(RandomAccessFile file, byte[] data, int offset, int size) throws IOException {
        file.write(data, offset, size);
    }

    private void fclose(RandomAccessFile file) throws IOException {
        try {
            file.seek(4); // riff chunk size
            file.writeInt(Integer.reverseBytes((int) (file.length() - 8)));

            file.seek(40); // data chunk size
            file.writeInt(Integer.reverseBytes((int) (file.length() - 44)));

            Log.d(TAG, "wav size: " + file.length());

        } finally {
            file.close();
        }
    }

    private class RecordTask extends AsyncTask<String, Integer, Void>{

        @Override
        protected Void doInBackground(String... params) {
            RandomAccessFile file = null;

            try {
                running = true;
                recorder.startRecording();

                if (path != null) {
                    file = fopen(path);
                }
                Log.d(TAG, "started");

                int discardBytes = CHANNELS * FREQUENCY * BITS * 100 / 1000 / 8;
                while (discardBytes > 0) {
                    int requestBytes = buffer.length < discardBytes ? buffer.length : discardBytes;
                    int readBytes = recorder.read(buffer, 0, requestBytes);
                    //音量有问题。Logger.i("RecordTask","doInBackground","volume : " + doubleCalculateVolume(buffer));
                    if (readBytes > 0) {
                        discardBytes -= readBytes;
                        Log.d(TAG, "discard: " + readBytes);
                    } else {
                        break;
                    }
                }

                while (true) {
                    if (!running || recorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                        recorder.stop();
                        break;
                    }

                    int size = recorder.read(buffer, 0, buffer.length);
                    if (size > 0) {
                        if (mCallback != null) {
                            mCallback.run(buffer, size);
                        }
                        if (file != null) {
                            fwrite(file, buffer, 0, size);
                        }
                    }

                    if (!running) {
                        break;
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    running = false;
                    if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                        recorder.stop();
                    }

                    Log.d(TAG, "stoped");

                    if (file != null) {
                        fclose(file);
                    }

                } catch (IOException e) {
                    // ignore
                }
            }
            return null;
        }

    }

    private class PlayBackTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(path, "r");
                file.seek(44);

                running = true;
                player.play();

                Log.d(TAG, "playback started");

                while (running) {
                    int size = file.read(buffer, 0, buffer.length);
                    if (size == -1) {
                        break;
                    }
                    player.write(buffer, 0, size);
                }

                player.flush();

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    running = false;
                    if (player.getPlayState() != AudioTrack.PLAYSTATE_STOPPED)
                        player.stop();

                    Log.d(TAG, "playback stoped");

                    if (file != null)
                        file.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(player != null){
                player.stop();
            }
        }
    }

}