package com.kernel.androidipctest;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;

public class MyService extends Service {
    public MyService() {
        System.out.println("MyService");
    }

    Messenger m = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgfromClient) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (msgfromClient.what == 1) {
                System.out.println(msgfromClient.arg1 + "客户说 " + msgfromClient.getData().get("say"));
                //客服回复
                Message msgToClient = new Message();
                msgToClient.what = 2;
                msgToClient.arg1 = Process.myPid();

                try {
                    if (msgfromClient.replyTo != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("say", "客服收到信息了");
                        msgToClient.setData(bundle);
                        msgfromClient.replyTo.send(msgToClient);

                    } else {
                        System.out.println("客户无法应答 ");
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
            super.handleMessage(msgfromClient);
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return m.getBinder();
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground();
        System.out.println(intent.getAction() + "-- " + flags + " onStartCommand " + startId);
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
    }

}
