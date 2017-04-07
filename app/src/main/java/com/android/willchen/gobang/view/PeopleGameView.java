package com.android.willchen.gobang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.willchen.gobang.ui.BleGameActivity;

import java.util.Stack;

/**
 * Time:2017.3.31 21:23
 * Created By:ThatNight
 */

public class PeopleGameView extends View {
    private Paint mPaint;
    private int mPanelWidth;                             //画布宽度
    private int mChessFlag = 0;                          //记录上一次下棋颜色，1为黑色，2为白色，0为刚开始
    private static final int CHESS_BLACK = 1;
    private static final int CHESS_WHITE = 2;
    private static final int MAX_LINE = 15;             //最大行数
    private static final float SIZE = 3 * 1.0f / 4;     //棋子的大小为棋盘格子的3/4
    private float mSingleHeight;                         //格子高度
    public boolean isWin = false;                       //是否已出结果
    private boolean isMe = false;                       //谁先出手
    private boolean isStart = false;                    //谁先开始
    private int[][] mChess;                              //棋盘
    private Stack<String> mChessArray;                   //悔棋的栈
    private BleGameActivity mBleGameActivity;
    private String mAdress;
    private int mUpChessX;                              //上一个下的棋子x,y
    private int mUpChessY;
    private TextView mTvWin;                            //输赢的TextView

    public PeopleGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化
    private void init() {
        isWin = false;
        mUpChessX = mUpChessY = -1;
        mChess = new int[MAX_LINE][MAX_LINE];
        mChessArray = new Stack<>();
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        isMe = isStart;
        if (isStart) {
            mChessFlag = CHESS_WHITE;
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
        mPanelWidth = width;
        mSingleHeight = mPanelWidth * 1.0f / MAX_LINE;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
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
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < MAX_LINE; i++) {
            canvas.drawLine(mSingleHeight / 2, mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2 + (MAX_LINE - 1) * mSingleHeight, mSingleHeight / 2 + i * mSingleHeight, mPaint);
            canvas.drawLine(mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2, mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2 + (MAX_LINE - 1) * mSingleHeight, mPaint);
        }
    }

    /**
     * 画棋子
     *
     * @param canvas
     */
    private void drawPiece(Canvas canvas) {
        int side = 0;
        if ((side = isWin(mChess)) != 0) {
            mTvWin.setVisibility(View.VISIBLE);
            if (side == CHESS_BLACK) {
                mBleGameActivity.onCommand("black_win");
                mTvWin.setText("黑棋胜利!");
            } else if (side == CHESS_WHITE) {
                mBleGameActivity.onCommand("white_win");
                mTvWin.setText("白棋胜利!");
            }
            mChessFlag = 0;
            isWin = true;
        }
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                if (mChess[i][j] == CHESS_BLACK) {
                    mPaint.setColor(Color.BLACK);
                    canvas.drawCircle(mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2 + j * mSingleHeight, mSingleHeight / 2 - 5, mPaint);
                }
                if (mChess[i][j] == CHESS_WHITE) {
                    mPaint.setColor(Color.WHITE);
                    canvas.drawCircle(mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2 + j * mSingleHeight, mSingleHeight / 2 - 5, mPaint);
                }
                //画上一个棋子的圆环
                if (i == mUpChessX && j == mUpChessY) {
                    mPaint.setColor(Color.RED);
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeWidth(4);
                    canvas.drawCircle(mSingleHeight / 2 + i * mSingleHeight, mSingleHeight / 2 + j * mSingleHeight, mSingleHeight / 2 - 2, mPaint);
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setStrokeWidth(0);
                }
            }
        }
    }

    //下棋子,并发送给对方
    private void putChess(int x, int y, int chessFlag) {
        mChess[x][y] = chessFlag;
        String command = "";
        String temp = x + ";" + y + ";" + chessFlag;
        mChessArray.push(temp);
        command += mAdress + ";" + temp;
        mBleGameActivity.onCommand(command + ";" + chessFlag);
    }

    //接收对方传送信息
    public void getCommand(String command) {
        if ("msg;".equals(command.substring(0, 4))) {
            String finalCommand = command.substring(4);
            Toast.makeText(mBleGameActivity, finalCommand, Toast.LENGTH_SHORT).show();
        } else if ("white_win".equals(command)) {
            mTvWin.setText("白棋胜利!");
        } else if ("black_win".equals(command)) {
            mTvWin.setText("黑棋胜利!");
        } else if ("return".equals(command)) {
            Toast.makeText(mBleGameActivity, "对方悔棋!", Toast.LENGTH_SHORT).show();
            returnUp();
        } else if ("restart".equals(command)) {
            Toast.makeText(mBleGameActivity, "对方重玩游戏!", Toast.LENGTH_SHORT).show();
            restartGame();
        } else {
            isMe = false;
            String[] data = command.split(";");
            int x = Integer.parseInt(data[1]);
            int y = Integer.parseInt(data[2]);
            int flag = Integer.parseInt(data[3]);
            mUpChessX = x;
            mUpChessY = y;
            mChess[x][y] = flag;
            mChessFlag = flag;
            String temp = x + ";" + y + ";" + flag;
            mChessArray.push(temp);
            invalidate();
            isMe = true;
        }
    }

    //下棋触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isWin) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x < -mSingleHeight / 2 || x > mPanelWidth - mSingleHeight / 2 || y < 0 || y > mPanelWidth - mSingleHeight / 2) {

                } else {
                    int indexX = (int) (x / mSingleHeight);
                    int indexY = (int) (y / mSingleHeight);
                    mUpChessX = indexX;
                    mUpChessY = indexY;
                    if (mChessFlag == CHESS_WHITE && isMe && mChess[indexX][indexY] == 0) {
                        putChess(indexX, indexY, CHESS_BLACK);
                        mChessFlag = CHESS_BLACK;
                        isMe = false;
                        invalidate();
                    } else if (mChessFlag == CHESS_BLACK && isMe && mChess[indexX][indexY] == 0) {
                        putChess(indexX, indexY, CHESS_WHITE);
                        mChessFlag = CHESS_WHITE;
                        isMe = false;
                        invalidate();
                    }
                }
            }
        }
        return true;
    }

    //判断是否已有结果
    public int isWin(int[][] chess) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                int side = chess[i][j];
                if (side == 0)
                    continue;
                for (int k = 0; k < 5; k++) {
                    //check from vertical
                    if ((i - k >= 0 && i + 4 - k < 15) &&
                            (chess[i - k][j] == side &&
                                    chess[i + 1 - k][j] == side &&
                                    chess[i + 2 - k][j] == side &&
                                    chess[i + 3 - k][j] == side &&
                                    chess[i + 4 - k][j] == side)) {
                        return side;
                    }
                    //check from horizontal
                    if ((j - k >= 0 && j + 4 - k < 15) &&
                            (chess[i][j - k] == side &&
                                    chess[i][j + 1 - k] == side &&
                                    chess[i][j + 2 - k] == side &&
                                    chess[i][j + 3 - k] == side &&
                                    chess[i][j + 4 - k] == side)) {
                        return side;
                    }
                    //check from leftbevel
                    if ((i - k >= 0 && j - k >= 0 && i + 4 - k < 15 && j + 4 - k < 15) &&
                            (chess[i - k][j - k] == side &&
                                    chess[i + 1 - k][j + 1 - k] == side &&
                                    chess[i + 2 - k][j + 2 - k] == side &&
                                    chess[i + 3 - k][j + 3 - k] == side &&
                                    chess[i + 4 - k][j + 4 - k] == side)) {
                        return side;
                    }
                    //check from rightbevel
                    if ((i - k >= 0 && j + k < 15 && i + 4 - k < 15 && j - 4 + k >= 0) &&
                            (chess[i - k][j + k] == side &&
                                    chess[i + 1 - k][j - 1 + k] == side &&
                                    chess[i + 2 - k][j - 2 + k] == side &&
                                    chess[i + 3 - k][j - 3 + k] == side &&
                                    chess[i + 4 - k][j - 4 + k] == side)) {
                        return side;
                    }

                }
            }
        }
        return 0;
    }

    public void setCallBack(BleGameActivity bleGameActivity) {
        this.mBleGameActivity = bleGameActivity;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
        isMe = isStart;
        if (isStart) {
            mChessFlag = CHESS_WHITE;
        }
    }

    public void setAdress(String adress) {
        mAdress = adress;
    }

    //重新开始
    public void restartGame() {
        mTvWin.setVisibility(View.INVISIBLE);
        init();
        invalidate();
    }

    //悔棋
    public void returnUp() {
        if (!isWin) {
            if (mChessArray.size() == 0) {

            } else {
                String[] point = mChessArray.pop().split(";");
                int x = Integer.parseInt(point[0]);
                int y = Integer.parseInt(point[1]);
                int flag = Integer.parseInt(point[2]);
                mChess[x][y] = 0;
                if (mChessArray.size() == 0) {
                    isMe = isStart;
                    mChessFlag = CHESS_WHITE;
                    mUpChessX = -1;
                    mUpChessY = -1;
                } else {
                    String[] upChess = mChessArray.get(mChessArray.size() - 1).split(";");
                    mUpChessX = Integer.parseInt(upChess[0]);
                    mUpChessY = Integer.parseInt(upChess[1]);
                    if (flag == CHESS_WHITE) {
                        mChessFlag = CHESS_BLACK;
                    } else {
                        mChessFlag = CHESS_WHITE;
                    }
                    isMe = !isMe;
                }
                invalidate();
            }
        } else {
            Toast.makeText(mBleGameActivity, "请重新开始!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isMe() {
        return isMe;
    }

    public interface onBluetoothListener {
        void onCommand(String temp);
    }

    public void setTvWin(TextView tvWin) {
        mTvWin = tvWin;
    }
}
