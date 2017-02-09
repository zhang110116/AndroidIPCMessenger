package com.kernel.androidipctest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * kernel
 * Demo进程通信  Messenger
 */
public class MainActivity extends AppCompatActivity {
    TextView tv_status;
    LinearLayout ll;
    ServiceConn mServiceConn;
    boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_status = (TextView) findViewById(R.id.tv_status);
        ll = (LinearLayout) findViewById(R.id.ll);
        Intent in = new Intent();
        in.setAction("com.kernel.androidipctest.MyService");
        //startService(in);
        System.out.println("----start service----");
        mServiceConn = new ServiceConn();
        mIsBound = true;
        //BIND_AUTO_CREATE:这样就会在service不存在时创建一个
        bindService(in, mServiceConn, BIND_AUTO_CREATE);
    }

    IBinder mIBinder;

    class ServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected");
            tv_status.setText("服务连接状态：Service Connected");
            mIBinder = service;
            Messenger m = new Messenger(service);
            Message mes = new Message();
            mes.what = 1;
            mes.arg1 = Process.myPid();
            Bundle bundle = new Bundle();
            bundle.putString("say", "你好，信息收到吗？");
            mes.setData(bundle);



            TextView tv_step = new TextView(MainActivity.this);
            tv_step.setText(mes.arg1 + "客户说: " + mes.getData().getString("say"));
            ll.addView(tv_step);
            mes.replyTo = client;//设置回应的mes 如果没有则服务器无法发送信心到客户端
            try {
                m.send(mes);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

        Messenger client = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mIsBound) {
                    System.out.println("正在运行");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (msg.what == 2) {
                        System.out.println(msg.arg1 + "客服说: " + msg.getData().getString("say"));
                        TextView tv_step = new TextView(MainActivity.this);
                        tv_step.setText(msg.arg1 + "客服说: " + msg.getData().getString("say"));
                        ll.addView(tv_step);


                        //客户第二次发送信息
                        Messenger m = new Messenger(mIBinder);
                        Message mes = new Message();
                        mes.what = 1;
                        mes.arg1 = Process.myPid();
                        Bundle bundle = new Bundle();
                        bundle.putString("say", "你好，新信息收到吗？");
                        mes.setData(bundle);
                        TextView tv_step2 = new TextView(MainActivity.this);
                        tv_step2.setText(mes.arg1 + "客户说: " + mes.getData().getString("say"));
                        ll.addView(tv_step2);
                        mes.replyTo = client;//设置回应的mes 如果没有则服务器无法发送信心到客户端
                        try {
                            m.send(mes);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        try {
                            unbindService(mServiceConn);
                            this.removeMessages(1);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        mIsBound = false;
                    }
                } else {
                    System.out.println("已经解绑");
                    this.removeMessages(1);
                }
                super.handleMessage(msg);
            }
        });

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("onServiceDisconnected");
            tv_status.setText("服务连接状态：Service Disconnected");
            mIBinder = null;

        }

    }
}
