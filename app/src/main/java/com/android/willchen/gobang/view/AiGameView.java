package com.android.willchen.gobang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.willchen.gobang.ai.Ai;
import com.android.willchen.gobang.bean.BestPos;

/**
 * Time:2016/8/31 12:05
 * Created By:ThatNight
 */
public class AiGameView extends View {

    private static final String TAG = "GobangView";
    private static final int CHESS_BLACK = 1;
    private static final int CHESS_WHITE = 2;
    private Paint paint;
    //机器人难度
    private int AI_DIFFICULT = 2;
    //是否已经结束
    public boolean isWin = false;
    //画布宽度
    private int panelWidth;
    //最大行数
    private static final int MAX_LINE = 15;
    //棋子的大小为棋盘格子的3/4
    private static final float size = 3 * 1.0f / 4;
    //格子高度
    private float singleHeight;
    //谁先出手
    private boolean isWhite = true;
    private int[][] chess;
    private int chessFlag = 0;  //记录上一次下棋颜色，1为黑色，2为白色，0为刚开始
    private BestPos bs;
    Ai ai = new Ai();
    private TextView textView;
    private int mUpChessX;
    private int mUpChessY;

    public AiGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AiGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        chess = new int[MAX_LINE][MAX_LINE];
        mUpChessX = mUpChessY = MAX_LINE / 2;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        if (chessFlag == 0) {        //黑棋先下
            chess[Math.round(MAX_LINE / 2)][Math.round(MAX_LINE / 2)] = CHESS_BLACK;
            chessFlag = CHESS_BLACK;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        panelWidth = width;
        singleHeight = panelWidth * 1.0f / MAX_LINE;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int onlyWidth = (int) (singleHeight * size);
    }

    protected void onDraw(Canvas canvas) {
        drawBoard(canvas);
        drawPiece(canvas);
    }

    /**
     * 画棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < MAX_LINE; i++) {
            /*int startX = (int) singleHeight / 2;
            int endX = (int) (panelWidth - singleHeight / 2);
			int y = (int) ((0.5 + i) * singleHeight);
			canvas.drawLine(startX, y, endX, y, paint); //横线
			canvas.drawLine(y, startX, y, endX, paint);*/
            canvas.drawLine(singleHeight / 2, singleHeight / 2 + i * singleHeight, singleHeight / 2 + (MAX_LINE - 1) * singleHeight, singleHeight / 2 + i * singleHeight, paint);
            canvas.drawLine(singleHeight / 2 + i * singleHeight, singleHeight / 2, singleHeight / 2 + i * singleHeight, singleHeight / 2 + (MAX_LINE - 1) * singleHeight, paint);
        }
    }

    /**
     * 画棋子
     *
     * @param canvas
     */
    private void drawPiece(Canvas canvas) {
        int side = 0;
        if ((side = ai.isWin(chess)) != 0) {
            textView.setVisibility(View.VISIBLE);
            if (side == CHESS_BLACK) {
                textView.setText("你输了哦!");
            } else if (side == CHESS_WHITE) {
                textView.setText("你赢了哦!");
            }
            chessFlag = 0;
            isWin = true;
        }
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                if (chess[i][j] == CHESS_BLACK) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(singleHeight / 2 + i * singleHeight, singleHeight / 2 + j * singleHeight, singleHeight / 2 - 5, paint);
                }
                if (chess[i][j] == CHESS_WHITE) {
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(singleHeight / 2 + i * singleHeight, singleHeight / 2 + j * singleHeight, singleHeight / 2 - 5, paint);
                }
                if (i == mUpChessX && j == mUpChessY) {
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(4);
                    canvas.drawCircle(singleHeight / 2 + i * singleHeight, singleHeight / 2 + j * singleHeight, singleHeight / 2 - 2, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(0);
                }
            }
        }
    }


    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isWin) {

            } else {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Log.i(TAG, x + "\n" + y);
                if (x < -singleHeight / 2 || x > panelWidth - singleHeight / 2 || y < 0 || y > panelWidth - singleHeight / 2) {
                    Log.d(TAG, panelWidth - singleHeight / 2 + "");
                } else {
                    int indexX = (int) (x / singleHeight);
                    int indexY = (int) (y / singleHeight);
                    if (chessFlag == CHESS_BLACK && chess[indexX][indexY] == 0) {
                        chess[indexX][indexY] = CHESS_WHITE;
                        chessFlag = CHESS_WHITE;
                        invalidate();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(30);
                                    bs = ai.action(chess, AI_DIFFICULT);
                                    Log.d(TAG, "run: ");
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = bs;
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    BestPos ps = (BestPos) msg.obj;
                    chess[ps.getBestX()][ps.getBestY()] = CHESS_BLACK;
                    mUpChessX = ps.getBestX();
                    mUpChessY = ps.getBestY();
                    chessFlag = CHESS_BLACK;
                    invalidate();
                    break;
            }
        }
    };

    public void restartGame() {
        textView.setVisibility(View.INVISIBLE);
        chess = new int[MAX_LINE][MAX_LINE];
        chessFlag = 0;
        init();
        invalidate();
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public int getAI_DIFFICULT() {
        return AI_DIFFICULT;
    }

    public void setAI_DIFFICULT(int AI_DIFFICULT) {
        this.AI_DIFFICULT = AI_DIFFICULT;
    }
}
