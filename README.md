# 前言

我之前做了个人机对战的五子棋, AI算法很垃圾, 然后各种逻辑很糟糕, 已经很久没有维护了, 今天看了篇文章, 是用了蓝牙和Wifi的五子棋对战, 我觉得很有意思, 毕竟自己没做过蓝牙连接这方面的项目, 然后我就把以前做的五子棋搬了出来.

---

# 蓝牙的基础知识

我在前面就有写过一篇比较详细的文章, 兄弟们可以到前面看一看, [Android 蓝牙五子棋[可人机对战] —— 蓝牙通信篇](http://blog.csdn.net/williamchew/article/details/68925920)

---

# 项目解析

- ## 文件结构
	
	![这里写图片描述](http://img.blog.csdn.net/20170407150037619?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2lsbGlhbWNoZXc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

- ## 人机对战算法

    这个算法呢, 就是一个朋友写的, 使用了**博弈树**来进行判断对战, 其实原理很简单: **通过计算各个点的权重**.
    有兴趣的朋友可以下载项目来研究下,这个算法不是很好,计算得比较慢, 一共写了6棵树, 我这里只用了2,3,4, 所以比较快.哪天有空,我也来研究一下ai算法.
    
- ## 主要的模块

    - ### 蓝牙游戏模块
        BleGameActivity.java
        ```
        public class BleGameActivity extends Activity implements PeopleGameView.onBluetoothListener {

            @InjectView(R.id.tv_pgv_win)
            TextView mIsWin;
            @InjectView(R.id.view_pgv)
            PeopleGameView mPeopleGameView;
            @InjectView(R.id.btn_pgv_return)
            Button mBtnPgvReturn;
            @InjectView(R.id.btn_pgv_again)
            Button mBtnPgvAgain;
            @InjectView(R.id.btn_pgv_msg)
            Button mBtnPgvMsg;
            private ConnectedThread mConnectedThread;
            private BluetoothSocket mSocket;
            private ListPopupWindow mWindowCompat;
            private List<String> mMessage;
        
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_ble_game);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                ButterKnife.inject(this);
                init();
            }
        
            //初始化
            private void init() {
                String[] message = getResources().getStringArray(R.array.array_message);
                mMessage = Arrays.asList(message);
        
                mPeopleGameView.setTextView(mIsWin);
                mPeopleGameView.setCallBack(this);
        
                //获取信息
                Intent intent = getIntent();
                String adress = intent.getStringExtra("adress");
                boolean isStart = intent.getBooleanExtra("isStart", false);
                if (adress != null) {
                    mSocket = SocketManager.getmBleSocketHm(adress);
                    manageClientSocket(isStart);
                    mPeopleGameView.setAdress(adress);
                    mPeopleGameView.setIsStart(isStart);
                }
                popupWindow();
            }
            
            //开启连接线程
            public void manageClientSocket(boolean isStart) {
                mConnectedThread = new ConnectedThread(mSocket, mPeopleGameView, isStart);
                mConnectedThread.start();
            }
            
            //发送信息
            @Override
            public void onCommand(String temp) {
                mConnectedThread.write(temp.getBytes());
            }
        
            @OnClick({R.id.btn_pgv_return, R.id.btn_pgv_again, R.id.btn_pgv_msg})
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_pgv_msg:
                        mWindowCompat.show();
                        break;
                    case R.id.btn_pgv_return:
                        String command = "return";
                        mConnectedThread.write(command.getBytes());
                        mPeopleGameView.returnUp();
                        break;
                    case R.id.btn_pgv_again:
                        String restartCommand = "restart";
                        mConnectedThread.write(restartCommand.getBytes());
                        mPeopleGameView.restartGame();
                        break;
                }
            }
            
            //弹出快捷回复窗口
            private void popupWindow() {
                mWindowCompat = new ListPopupWindow(this);
                mWindowCompat.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMessage));
                mWindowCompat.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                mWindowCompat.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                mWindowCompat.setAnchorView(mBtnPgvMsg);
                mWindowCompat.setModal(true);
                mWindowCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String msg = "msg;" + mMessage.get(position);
                        mConnectedThread.write(msg.getBytes());
                        mWindowCompat.dismiss();
                    }
                });
            }
        
            @Override
            protected void onDestroy() {
                super.onDestroy();
                mConnectedThread.cancel();
            }
        }
        ```
        PeopleGameView.java
        ```
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
        ```
    
- # 总结
    
    - #### 主要还是一些细节我自己处理得不是很好, 比如说内存占用啊, 很多流没关啊, 但是基本的一个五子棋的功能已经做得比较完美了, 看以后如果还有时间的话, 我会继续维护的, 可我觉得五子棋没什么意思, 想做点比较流行的啊, 比如说王者荣耀啊, 或者把五子棋联网对战啊.
    
    - #### 但是好像最近学习比较忙,就没什么时间来做这些个人项目了,主要还是以学习为主,这个项目让我学到了蓝牙的使用啊,之前一直想玩玩蓝牙\WiFi什么的连接类, 如今算是实现了.

    - #### 最终效果图发一下吧,没图说个..
	![这里写图片描述](http://img.blog.csdn.net/20170407153803201?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2lsbGlhbWNoZXc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
	
