package com.android.willchen.gobang.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.willchen.gobang.R;
import com.android.willchen.gobang.view.AiGameView;

public class AiActivity extends Activity {

    private Button btnAgain, btnDifficult;
    private TextView tvWin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ai);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        init();

    }

    private void init() {
        final AiGameView mAigameView = (AiGameView) findViewById(R.id.vw_gobang);

        tvWin = (TextView) findViewById(R.id.tv_win);
        mAigameView.setTextView(tvWin);
        btnAgain = (Button) findViewById(R.id.btn_again);
        btnDifficult = (Button) findViewById(R.id.btn_difficult);

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAigameView.isWin = false;
                mAigameView.restartGame();
            }
        });

        btnDifficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mAigameView.getAI_DIFFICULT()) {
                    case 2:
                        mAigameView.setAI_DIFFICULT(3);
                        btnDifficult.setText("难度:中等");
                        break;
                    case 3:
                        mAigameView.setAI_DIFFICULT(4);
                        btnDifficult.setText("难度:困难");
                        break;
                    case 4:
                        mAigameView.setAI_DIFFICULT(2);
                        btnDifficult.setText("难度:简单");
                        break;

                }
                mAigameView.isWin = false;
                mAigameView.restartGame();
            }
        });
    }
}
