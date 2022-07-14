package com.example.timerhishtalmut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int CHANGE_STATE = 0;
    private static final int TICK = 2;
    private TimerState timerState = TimerState.STOP;
    private Handler handler;
    private Button btnStartTimer;
    private Button btnStopTimer;
    private TextView lblTimer;
    private int counterVal = 0;
    enum TimerState{
        RUN,
        STOP,
        PAUSE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        createHandler();
    }

    private void initViews() {
        btnStartTimer = (Button) findViewById(R.id.btnStartTimer);
        btnStopTimer = (Button) findViewById(R.id.btnStopTimer);
        lblTimer = (TextView) findViewById(R.id.lblTimerTxt);

        btnStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain();
                message.what = CHANGE_STATE;

                switch(timerState){
                    case STOP:
                        message.obj = "PAUSE";
                        timerState = TimerState.RUN;
                        createTimerThread().start();
                        break;
                    case RUN:
                        message.obj = "RUN TIMER";
                        timerState = TimerState.PAUSE;
                        break;
                    case PAUSE:
                        message.obj = "RESUME";
                        timerState = TimerState.RUN;
                }
                handler.sendMessage(message);
            }//end onClick
        });

        btnStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerState = TimerState.STOP;

                Message message = Message.obtain();
                message.what = CHANGE_STATE;
                message.obj = "START TIMER";
                handler.sendMessage(message);
            }//end onClick
        });
    }

    private Thread createTimerThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message message;
                while (timerState != TimerState.STOP) {
                    SystemClock.sleep(1000);
                    if (timerState == TimerState.RUN) {
                        message = Message.obtain();
                        message.what = TICK;
                        message.arg1 = counterVal++;
                        handler.sendMessage(message);
                    }
                }//end while
                message = Message.obtain();
                message.what = TICK;
                message.arg1 = counterVal = 0;
                handler.sendMessage(message);
            }//end run
        });
    }

    private void createHandler() {
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == TICK) {
                    lblTimer.setText(Integer.toString(msg.arg1));
                }else { //message CHANGE_STATE
                    btnStartTimer.setText(msg.obj.toString());
                    }
                }
        };
    }
}