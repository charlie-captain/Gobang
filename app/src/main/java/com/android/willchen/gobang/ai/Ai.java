package com.android.willchen.gobang.ai;

import android.util.Log;

import com.android.willchen.gobang.bean.BestPos;
import com.android.willchen.gobang.config.ConfigData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Time:2016/8/31 18:54
 * Created By:ThatNight
 */
public class Ai implements IAi{

	private static final String TAG = "Ai";

	@Override
	public BestPos action(int[][] chess, int depth) {
		List<BestPos> points;//particalpoints container
		List<BestPos> bestpoints=new ArrayList<>();//bestpoints container
		int tbest;//temporary best value
		int best = ConfigData.MIN;
		int deep = depth;//search deepth
			ConfigData.ABCUTS= ConfigData.TOTAL = 0;//reset 0
		points =generate(chess,deep);//get the possible points
		for(int i = 0; i < points.size(); i++)
		{
			BestPos bp=new BestPos();
			bp.setBestX(points.get(i).getBestX());
			bp.setBestY(points.get(i).getBestY());
			Log.d(TAG, "particalPos:"+bp.getBestX()+" "+bp.getBestY());
			chess[bp.getBestX()][bp.getBestY()] = 1;//try it
			tbest = min(chess,deep-1,ConfigData.MAX,best>ConfigData.MIN?best:ConfigData.MIN);
			if(tbest == best)
			{
				bestpoints.add(bp);
			}
			if(tbest > best)
			{
				best = tbest;
				bestpoints.clear();
				bestpoints.add(bp);
			}
			Log.d(TAG, "action: "+"value"+tbest);
			chess[bp.getBestX()][bp.getBestY()] = 0;//renew it
		}
		//choose one randomly
		long t=System.currentTimeMillis();
		Random random=new Random();
		int size = random.nextInt(1000)%bestpoints.size();
		chess[bestpoints.get(size).getBestX()][bestpoints.get(size).getBestY()] = 1;
		Log.d(TAG, "bestChoice "+bestpoints.get(size).getBestX()+" "+bestpoints.get(size).getBestY());
		Log.d(TAG, "bestChoice Value "+best);
		Log.d(TAG, "total"+ ConfigData.TOTAL);
		Log.d(TAG, "cutting points"+ConfigData.ABCUTS);
		BestPos bs=new BestPos();
		bs.setBestX(bestpoints.get(size).getBestX());
		bs.setBestY(bestpoints.get(size).getBestY());
		return bs;
	}

	@Override
	public int getLine(int i, int j, int dir, int relapos, int[][] chess) {
		switch(dir)
		{
			case 1://up
				i = i - relapos;
				break;
			case 2://down
				i = i +relapos;
				break;
			case 3://right
				j = j - relapos;
				break;
			case 4://left
				j = j + relapos;
				break;
			case 5://leftupbevel
				i = i - relapos;
				j = j - relapos;
				break;
			case 6://rightupbevel
				i = i - relapos;
				j = j + relapos;
				break;
			case 7://leftdownbevel
				i = i + relapos;
				j = j - relapos;
				break;
			case 8://rightdownbevel
				i = i + relapos;
				j = j + relapos;
				break;
			default:
				break;
		}
		if(i<0||j<0||i>14||j>14)
			return -1;//-1 presents out of boundary
		return chess[i][j];
	}

	@Override
	public int evaluate(int[][] chess) {
		int value = 0;
		for(int i = 0; i < 15; i++)
		{
			for(int j = 0; j < 15; j++)
			{
				if(chess[i][j]!=0)
				{
					for(int k = 1; k <= 8; k++)
					{
						if(getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==1))))//*1111
						{
							value += ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,-1,chess)==1)))))//1*111
						{
							value += ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,-1,chess)==1&&
								(getLine(i,j,k,-2,chess)==1)))))//11*11
						{
							value += ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==0&&(getLine(i,j,k,-1,chess)==0))))))//0*1110
						{
							value += ConfigData.ALIVEFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==1&&(getLine(i,j,k,-2,chess)==0))))))//01*110
						{
							value += ConfigData.ALIVEFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==0&&((getLine(i,j,k,-1,chess)==2||(getLine(i,j,k,-1,chess)==-1))))))))//2*1110 or -1*1110
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==1&&((getLine(i,j,k,-2,chess)==2||(getLine(i,j,k,-2,chess)==-1))))))))//21*110 or -11*110
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==1&&
								(getLine(i,j,k,-2,chess)==1&&((getLine(i,j,k,-3,chess)==2||(getLine(i,j,k,-3,chess)==-1))))))))//211*10 or -111*10
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,-1,chess)==1&&(getLine(i,j,k,-2,chess)==1&&
								(getLine(i,j,k,-3,chess)==1&&((getLine(i,j,k,-4,chess)==2||(getLine(i,j,k,-4,chess)==-1))))))))//2111*0 or -1111*0
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,4,chess)==1)))))//*1101
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,-1,chess)==1)))))//1*101
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,-1,chess)==1&&
								(getLine(i,j,k,-2,chess)==1)))))//11*01
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,2,chess)==1&&
								(getLine(i,j,k,3,chess)==1)))))//1110*
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==1)))))//*1011
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,-1,chess)==1)))))//1*011
						{
							value += ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==0)))))//0*110
						{
							value += ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==1&&
								(getLine(i,j,k,-2,chess)==0)))))//01*10
						{
							value += ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==0&&(getLine(i,j,k,-1,chess)==0))))))//0*1010
						{
							value += ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==1&&(getLine(i,j,k,-2,chess)==0))))))//01*010
						{
							value += ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								(getLine(i,j,k,4,chess)==0&&(getLine(i,j,k,-1,chess)==0))))))//0*0110
						{
							value += ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==0&&
								((getLine(i,j,k,-1,chess)==2&&(getLine(i,j,k,-1,chess)==-1)))))))//2*110 or -11110
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==1&&
								((getLine(i,j,k,-2,chess)==2&&(getLine(i,j,k,-2,chess)==-1)))))))//21*10 or -11110
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,-1,chess)==0&&(getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==1&&
								((getLine(i,j,k,3,chess)==2&&(getLine(i,j,k,3,chess)==-1)))))))//211*0 or -11110
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==1&&
								((getLine(i,j,k,4,chess)==0||(getLine(i,j,k,-1,chess)==0)))))))//*101
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,-1,chess)==1&&
								((getLine(i,j,k,3,chess)==0||(getLine(i,j,k,-2,chess)==0)))))))//1*01
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&(getLine(i,j,k,3,chess)==1&&
								((getLine(i,j,k,4,chess)==0||(getLine(i,j,k,-1,chess)==0)))))))//110*
						{
							value += ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==0))))//0*10
						{
							value += ConfigData.ALIVETWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,2,chess)==0&&((getLine(i,j,k,-1,chess)==2
								||(getLine(i,j,k,-1,chess)==-1))))))//2*10 or -1110
						{
							value += ConfigData.RUSHTWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==1&&(getLine(i,j,k,-1,chess)==0&&((getLine(i,j,k,2,chess)==2
								||(getLine(i,j,k,2,chess)==-1))))))//21*0 or -1110
						{
							value += ConfigData.RUSHTWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==1&&((getLine(i,j,k,3,chess)==0
								||(getLine(i,j,k,-1,chess)==0))))))//*01
						{
							value += ConfigData.RUSHTWO;
							continue;
						}
					}
					for(int k = 1; k <= 8; k++)
					{
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,4,chess)==2)))))//*1111
						{
							value -= ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,-1,chess)==2)))))//1*111
						{
							value -= ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,-1,chess)==2&&
								(getLine(i,j,k,-2,chess)==2)))))//11*11
						{
							value -= ConfigData.COMBOFIVE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,4,chess)==0&&(getLine(i,j,k,-1,chess)==0))))))//0*1110
						{
							value -= ConfigData.ALIVEFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==2&&(getLine(i,j,k,-2,chess)==0))))))//01*110
						{
							value -= ConfigData.ALIVEFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,4,chess)==0&&((getLine(i,j,k,-1,chess)==1||(getLine(i,j,k,-1,chess)==-1))))))))//2*1110 or -1*1110
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==2&&((getLine(i,j,k,-2,chess)==1||(getLine(i,j,k,-2,chess)==-1))))))))//21*110 or -11*110
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==2&&
								(getLine(i,j,k,-2,chess)==2&&((getLine(i,j,k,-3,chess)==1||(getLine(i,j,k,-3,chess)==-1))))))))//211*10 or -111*10
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,-1,chess)==2&&(getLine(i,j,k,-2,chess)==2&&
								(getLine(i,j,k,-3,chess)==2&&((getLine(i,j,k,-4,chess)==1||(getLine(i,j,k,-4,chess)==-1))))))))
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,4,chess)==2)))))//*1101
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,-1,chess)==2)))))//1*101
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,-1,chess)==2&&
								(getLine(i,j,k,-2,chess)==2)))))//11*01
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,2,chess)==2&&
								(getLine(i,j,k,3,chess)==2)))))//1110*
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,4,chess)==2)))))//*1011
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								(getLine(i,j,k,-1,chess)==2)))))//1*011
						{
							value -= ConfigData.RUSHFOUR;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==0&&
								(getLine(i,j,k,-1,chess)==0)))))//0*110
						{
							value -= ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==2&&
								(getLine(i,j,k,-2,chess)==0)))))//01*10
						{
							value -= ConfigData.ALIVETHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==0&&
								((getLine(i,j,k,-1,chess)==1&&(getLine(i,j,k,-1,chess)==-1)))))))//2*110 or -11110
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==2&&
								((getLine(i,j,k,-2,chess)==1&&(getLine(i,j,k,-2,chess)==-1)))))))//21*10 or -11110
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,-1,chess)==0&&(getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==2&&
								((getLine(i,j,k,3,chess)==1&&(getLine(i,j,k,3,chess)==-1)))))))//211*0 or -11110
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,3,chess)==2&&
								((getLine(i,j,k,4,chess)==0||(getLine(i,j,k,-1,chess)==0)))))))//*101
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,-1,chess)==2&&
								((getLine(i,j,k,3,chess)==0||(getLine(i,j,k,-2,chess)==0)))))))//1*01
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&(getLine(i,j,k,3,chess)==2&&
								((getLine(i,j,k,4,chess)==0||(getLine(i,j,k,-1,chess)==0)))))))//110*
						{
							value -= ConfigData.RUSHTHREE;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&(getLine(i,j,k,-1,chess)==0))))//0*10
						{
							value -= ConfigData.ALIVETWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,2,chess)==0&&((getLine(i,j,k,-1,chess)==1
								||(getLine(i,j,k,-1,chess)==-1))))))//2*10 or -1110
						{
							value -= ConfigData.RUSHTWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==2&&(getLine(i,j,k,-1,chess)==0&&((getLine(i,j,k,2,chess)==1
								||(getLine(i,j,k,2,chess)==-1))))))//21*0 or -1110
						{
							value -= ConfigData.RUSHTWO;
							continue;
						}
						if((getLine(i,j,k,1,chess)==0&&(getLine(i,j,k,2,chess)==2&&((getLine(i,j,k,3,chess)==0
								||(getLine(i,j,k,-1,chess)==0))))))//*01
						{
							value -= ConfigData.RUSHTWO;
							continue;
						}
					}
				}
			}
		}
		return value;
	}

	@Override
	public int max(final int[][] chess, final int deep, final int alpha, final int beta) {
		ConfigData.TOTAL++;
		int tbest = evaluate(chess);
		if(deep<=0 || isWin(chess)!=0)
			return tbest;
		 int best =ConfigData.MIN;
		final List<BestPos> points = generate(chess,deep);
		for(int k = 0; k < points.size(); k++)
		{
			int x = points.get(k).getBestX();
			int y = points.get(k).getBestY();
			chess[x][y] = 1;//try it
			tbest = min(chess,deep-1,alpha, best >beta? best :beta);
			chess[x][y] = 0;//renew it
			if(tbest > best)
				best = tbest;
			if(tbest > alpha)//AB cutting
			{
				ConfigData.ABCUTS++;
				break;
			}
		}

		return best;
	}

	@Override
	public int min(final int[][] chess, final int deep, final int alpha, final int beta) {
		ConfigData.TOTAL++;
		int tbest = evaluate(chess);
		if(deep<=0 || isWin(chess)!=0)
			return tbest;
		int best = ConfigData.MAX;
		final List<BestPos> points = generate(chess,deep);
		for(int k = 0; k < points.size(); k++)
		{
			int x = points.get(k).getBestX();
			int y = points.get(k).getBestY();
			chess[x][y] = 2;
			tbest =max(chess,deep-1, best <alpha? best :alpha,beta);
			chess[x][y] = 0;
			if(tbest < best)
				best = tbest;
			if(tbest <beta)//AB cutting
			{
				ConfigData.ABCUTS++;
				break;
			}
		}
		return best;
	}

	@Override
	public int pointValue(int[][] chess, int i, int j, int side) {
		int value = 0;
		for(int k = 1; k <= 8; k++)
		{
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,4,chess)==side)//*1111
			{
				value += ConfigData.COMBOFIVE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,-1,chess)==side)//1*111
			{
				value += ConfigData.COMBOFIVE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,-1,chess)==side&&
					getLine(i,j,k,-2,chess)==side)//11*11
			{
				value += ConfigData.COMBOFIVE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,4,chess)==0&&getLine(i,j,k,-1,chess)==0)//0*1110
			{
				value += ConfigData.ALIVEFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,-1,chess)==side&&
					getLine(i,j,k,3,chess)==0&&getLine(i,j,k,-2,chess)==0)//01*110
			{
				value += ConfigData.ALIVEFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					(getLine(i,j,k,-1,chess)==0||getLine(i,j,k,4,chess)==0))//0*111 or *1110
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,-1,chess)==side&&
					(getLine(i,j,k,3,chess)==0||getLine(i,j,k,-2,chess)==0))//01*11 or 1*110
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==0&&getLine(i,j,k,-1,chess)==side&&
					getLine(i,j,k,4,chess)==side)//*1011
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,-1,chess)==side)//1*011
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,-1,chess)==side)//1*011
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,-1,chess)==side)//*0111
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,-1,chess)==side&&
					getLine(i,j,k,-2,chess)==side)//10*11
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==0&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,-1,chess)==side)//101*1
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==0&&
					getLine(i,j,k,4,chess)==side)//1011*
			{
				value += ConfigData.RUSHFOUR;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,-1,chess)==side&&getLine(i,j,k,2,chess)==0&&
					getLine(i,j,k,-2,chess)==0)//01*10
			{
				value += ConfigData.ALIVETHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==0&&
					getLine(i,j,k,-1,chess)==0)//0*110
			{
				value += ConfigData.ALIVETHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==0&&getLine(i,j,k,-1,chess)==0&&
					getLine(i,j,k,-2,chess)==side&&getLine(i,j,k,-3,chess)==0)//01*010
			{
				value += ConfigData.ALIVETHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==side&&
					getLine(i,j,k,4,chess)==0&&getLine(i,j,k,-1,chess)==0)//0*0110
			{
				value += ConfigData.ALIVETHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==0&&getLine(i,j,k,2,chess)==side&&getLine(i,j,k,3,chess)==0&&
					getLine(i,j,k,-1,chess)==side&&getLine(i,j,k,-2,chess)==0)//01*010
			{
				value += ConfigData.ALIVETHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,-1,chess)==side&&(getLine(i,j,k,2,chess)==0||
					getLine(i,j,k,-2,chess)==0))//01*1 or 1*10
			{
				value += ConfigData.RUSHTHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==side&&(getLine(i,j,k,3,chess)==0||
					getLine(i,j,k,-1,chess)==0))//0*11 or *110
			{
				value += ConfigData.RUSHTHREE;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&getLine(i,j,k,2,chess)==0&&getLine(i,j,k,-1,chess)==0)//0*10
			{
				value += ConfigData.ALIVETWO;
				continue;
			}
			if(getLine(i,j,k,1,chess)==side&&(getLine(i,j,k,2,chess)==0||getLine(i,j,k,-1,chess)==0))//0*1 or *10
			{
				value += ConfigData.RUSHTWO;
				continue;
			}
		}
		return value;
	}

	@Override
	public boolean hasNeighbor(int[][] chess, int i, int j, int d) {
		if( i-d>=0 && i-d<15&&chess[i-d][j]!=0)
			return true;
		if(i+d>=0 && i+d<15&&chess[i+d][j]!=0)
			return true;
		if(j-d>=0 && j-d<15&&chess[i][j-d]!=0 )
			return true;
		if(j+d>=0 && j+d<15&&chess[i][j+d] !=0)
			return true;
		if(i-d>=0 && i-d<15 && j-d>=0 && j-d<15&&chess[i-d][j-d]!=0)
			return true;
		if(i-d>=0 && i-d<15 && j+d>=0 && j+d<15&&chess[i-d][j+d]!=0)
			return true;
		if( i+d>=0 && i+d<15 && j-d>=0 && j-d<15&&chess[i+d][j-d]!=0)
			return true;
		if(i+d>=0 && i+d<15 && j+d>=0 && j+d<15&&chess[i+d][j+d]!=0)
			return true;
		return false;
	}

	@Override
	public int isWin(int[][] chess) {
		for(int i = 0; i < 15; i++)
		{
			for(int j = 0; j < 15; j++)
			{
				int side = chess[i][j];
				if(side == 0)
					continue;
				for(int k = 0; k < 5; k++)
				{
					//check from vertical
					if((i-k>=0 && i+4-k<15) &&
							(chess[i-k][j] == side &&
									chess[i+1-k][j] == side &&
									chess[i+2-k][j] == side &&
									chess[i+3-k][j] == side &&
									chess[i+4-k][j] == side))
					{
						return side;
					}
					//check from horizontal
					if((j-k>=0 && j+4-k<15) &&
							(chess[i][j-k] == side &&
									chess[i][j+1-k] == side &&
									chess[i][j+2-k] == side &&
									chess[i][j+3-k] == side &&
									chess[i][j+4-k] == side))
					{
						return side;
					}
					//check from leftbevel
					if((i-k>=0 && j-k>=0 && i+4-k<15 && j+4-k<15) &&
							(chess[i-k][j-k] == side &&
									chess[i+1-k][j+1-k] == side &&
									chess[i+2-k][j+2-k] == side &&
									chess[i+3-k][j+3-k] == side &&
									chess[i+4-k][j+4-k] == side))
					{
						return side;
					}
					//check from rightbevel
					if((i-k>=0 && j+k<15 && i+4-k<15 && j-4+k>=0) &&
							(chess[i-k][j+k] == side &&
									chess[i+1-k][j-1+k] == side &&
									chess[i+2-k][j-2+k] == side &&
									chess[i+3-k][j-3+k] == side &&
									chess[i+4-k][j-4+k] == side))
					{
						return side;
					}

				}
			}
		}
		return 0;
	}

	@Override
	public List<BestPos> generate(int[][] chess, int deep) {
		List<BestPos> points=new ArrayList<>();
		List<BestPos> tpoints=new ArrayList<>();
		List<BestPos> five=new ArrayList<>(),
				fourthree=new ArrayList<>(),
				four=new ArrayList<>(),
				twofour=new ArrayList<>(),
				twothree=new ArrayList<>(),
				three=new ArrayList<>(),
				two=new ArrayList<>();


		for(int i = 0; i < 15; i++)
		{
			for(int j = 0; j < 15; j++)
			{//search unoccupied position
				if(chess[i][j]!=0)
					continue;
				BestPos bp=new BestPos();
				bp.setBestX(i);
				bp.setBestY(j);
				//must have neighbor
				if(hasNeighbor(chess,i,j,1))
				{
					int AIscore = pointValue(chess,i,j,1);//evaluate current point's value of AI
					int Playerscore = pointValue(chess,i,j,2);//the same
					if(AIscore >= ConfigData.COMBOFIVE)
					{
						five.add(bp);
						return five;
					}
					else if(Playerscore >= ConfigData.COMBOFIVE)
					{
						five.add(bp);
					}
					else if(AIscore >= 2*ConfigData.ALIVEFOUR)
					{
						twofour.add(0,bp);
					}
					else if(Playerscore >= 2*ConfigData.ALIVEFOUR)
					{
						twofour.add(bp);
					}
					else if(AIscore >= ConfigData.ALIVETHREE+ConfigData.RUSHFOUR)
					{
						fourthree.add(0,bp);
					}
					else if(Playerscore >= ConfigData.ALIVETHREE+ConfigData.RUSHFOUR)
					{
						fourthree.add(bp);
					}
					else if(AIscore >= 2*ConfigData.ALIVETHREE)
					{
						twothree.add(0,bp);
					}
					else if(Playerscore >= 2*ConfigData.ALIVETHREE)
					{
						twothree.add(bp);
					}
					else if(AIscore >= ConfigData.ALIVEFOUR)
					{
						four.add(0,bp);
					}
					else if(Playerscore >= ConfigData.ALIVEFOUR)
					{
						four.add(bp);
					}
					else if(AIscore >= ConfigData.ALIVETHREE)
					{
						three.add(0,bp);
					}
					else if(Playerscore >= ConfigData.ALIVETHREE)
					{
						three.add(bp);
					}
					else if(AIscore >= ConfigData.ALIVETWO)
					{
						two.add(0,bp);
					}
					else if(Playerscore >= ConfigData.ALIVETWO)
					{
						two.add(bp);
					}
					else
					{
						points.add(bp);
					}
				}
				else if(deep>=2 && hasNeighbor(chess,i,j,2))
				{
					tpoints.add(bp);
				}
			}
		}
		if(!five.isEmpty())
		{
			BestPos bp = five.get(0);
			five.clear();
			five.add(bp);
			return five;
		}
		if(!twofour.isEmpty())
			return twofour;
		if(!fourthree.isEmpty())
			return fourthree;
		if(!twothree.isEmpty())
			return twothree;
		if(!four.isEmpty())
			return four;

		three.addAll(two);
		three.addAll(points);
		three.addAll(tpoints);

		return three;


	}
}
