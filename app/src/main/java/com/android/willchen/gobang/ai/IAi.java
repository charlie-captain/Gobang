package com.android.willchen.gobang.ai;

import com.android.willchen.gobang.bean.BestPos;

import java.util.List;

/**
 * Time:2016/8/31 19:03
 * Created By:ThatNight
 */
public interface IAi {

	BestPos action(int chess[][],int depth);
	int getLine(int i,int j,int dir,int relapos,int chess[][]);
	int evaluate(int chess[][]);
	int max(int chess[][],int deep,int alpha,int beta);
	int min(int chess[][],int deep,int alpha,int beta);
	int pointValue(int chess[][],int i,int j,int side);
	boolean hasNeighbor(int chess[][],int i ,int j,int d);
	int isWin(int chess[][]);
	List<BestPos> generate(int chess[][],int deep);

}
