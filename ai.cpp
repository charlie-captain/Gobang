#include "ai.h"
#include <QDebug>
#include <QVector>

AI::AI()
{

}

/*
 * According to the current situation,calculate and search the best point
 * 1-AI,2-player,0-unoccupied
 */
QString AI::action(int checkb[15][15], int depth)
{
    QVector<BestPos> points;//particalpoints container
    QVector<BestPos> bestpoints;//bestpoints container
    int tbest;//temporary best value
    int best = MIN;
    int deep = depth;//search deepth
    ABcuts = total = 0;//reset 0
    points = Generate(checkb,deep);//get the possible points
    for(int i = 0; i < points.size(); i++)
    {
        BestPos bp;
        bp.best_x = points.at(i).best_x;
        bp.best_y = points.at(i).best_y;
        qDebug()<<"particalPos:"<<bp.best_x<<bp.best_y;
        checkb[bp.best_x][bp.best_y] = 1;//try it
        tbest = Min(checkb,deep-1,MAX,best>MIN?best:MIN);
        if(tbest == best)
        {
            bestpoints.push_back(bp);
        }
        if(tbest > best)
        {
            best = tbest;
            bestpoints.clear();
            bestpoints.push_back(bp);
        }
        qDebug()<<"particalValue"<<tbest;
        checkb[bp.best_x][bp.best_y] = 0;//renew it
    }
    //choose one randomly
    QTime time;
    time = QTime::currentTime();
    qsrand(time.msec() + time.second()*1000);
    int size = qrand()%bestpoints.size();
    checkb[bestpoints.at(size).best_x][bestpoints.at(size).best_y] = 1;
    qDebug()<<"The best choice Pos: ("<<bestpoints.at(size).best_x<<","<<bestpoints.at(size).best_y<<")";
    qDebug()<<"The best choice value: <"<<best<<">";
    qDebug()<<"total searching points: "<<total;
    qDebug()<<"alpha-beta cutting points: "<<ABcuts;
    return QString::number(bestpoints.at(size).best_x) + " " + QString::number(bestpoints.at(size).best_y);
}

/*
 * search for max value
 * imitate AI's action
 */
int AI::Max(int checkb[15][15], int deep, int alpha, int beta)
{
    total++;//total nodes
    int tbest = evaluate(checkb);
    if(deep<=0 || IsWin(checkb))
        return tbest;
    int best = MIN;
    QVector<BestPos> points = Generate(checkb,deep);
    for(int k = 0; k < points.size(); k++)
    {
        int x = points.at(k).best_x;
        int y = points.at(k).best_y;
        checkb[x][y] = 1;//try it
        tbest = Min(checkb,deep-1,alpha,best>beta?best:beta);
        checkb[x][y] = 0;//renew it
        if(tbest > best)
            best = tbest;
        if(tbest > alpha)//AB cutting
        {
            ABcuts++;
            break;
        }
    }
    return best;
}

/*
 * search for min value
 * imitate player's action
 */
int AI::Min(int checkb[15][15], int deep, int alpha, int beta)
{
    total++;
    int tbest = evaluate(checkb);
    if(deep<=0 || IsWin(checkb))
        return tbest;
    int best = MAX;
    QVector<BestPos> points = Generate(checkb,deep);
    for(int k = 0; k < points.size(); k++)
    {

        int x = points.at(k).best_x;
        int y = points.at(k).best_y;
        checkb[x][y] = 2;
        tbest = Max(checkb,deep-1,best<alpha?best:alpha,beta);
        checkb[x][y] = 0;
        if(tbest < best)
            best = tbest;
        if(tbest<beta)//AB cutting
        {
            ABcuts++;
            break;
        }
    }
    return best;
}

/*
 * evaluate current situation
 * add up every point's value
 */
int AI::evaluate(int checkb[15][15])
{
    int value = 0;
    for(int i = 0; i < 15; i++)
    {
        for(int j = 0; j < 15; j++)
        {
            if(checkb[i][j])
            {
                for(int k = 1; k <= 8; k++)
                {
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==1)//*1111
                    {
                        value += combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,-1,checkb)==1)//1*111
                    {
                        value += combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,-1,checkb)==1&&
                            GetLine(i,j,k,-2,checkb)==1)//11*11
                    {
                        value += combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*1110
                    {
                        value += alivefour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==1&&GetLine(i,j,k,-2,checkb)==0)//01*110
                    {
                        value += alivefour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==0&&(GetLine(i,j,k,-1,checkb)==2||GetLine(i,j,k,-1,checkb)==-1))//2*1110 or -1*1110
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==1&&(GetLine(i,j,k,-2,checkb)==2||GetLine(i,j,k,-2,checkb)==-1))//21*110 or -11*110
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==1&&
                            GetLine(i,j,k,-2,checkb)==1&&(GetLine(i,j,k,-3,checkb)==2||GetLine(i,j,k,-3,checkb)==-1))//211*10 or -111*10
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,-1,checkb)==1&&GetLine(i,j,k,-2,checkb)==1&&
                            GetLine(i,j,k,-3,checkb)==1&&(GetLine(i,j,k,-4,checkb)==2||GetLine(i,j,k,-4,checkb)==-1))//2111*0 or -1111*0
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,4,checkb)==1)//*1101
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,-1,checkb)==1)//1*101
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,-1,checkb)==1&&
                            GetLine(i,j,k,-2,checkb)==1)//11*01
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&
                            GetLine(i,j,k,3,checkb)==1)//1110*
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==1)//*1011
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,-1,checkb)==1)//1*011
                    {
                        value += rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==0)//0*110
                    {
                        value += alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==1&&
                            GetLine(i,j,k,-2,checkb)==0)//01*10
                    {
                        value += alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*1010
                    {
                        value += alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==1&&GetLine(i,j,k,-2,checkb)==0)//01*010
                    {
                        value += alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*0110
                    {
                        value += alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==0&&
                            (GetLine(i,j,k,-1,checkb)==2&&GetLine(i,j,k,-1,checkb)==-1))//2*110 or -11110
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==1&&
                            (GetLine(i,j,k,-2,checkb)==2&&GetLine(i,j,k,-2,checkb)==-1))//21*10 or -11110
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,-1,checkb)==0&&GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==1&&
                            (GetLine(i,j,k,3,checkb)==2&&GetLine(i,j,k,3,checkb)==-1))//211*0 or -11110
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==1&&
                            (GetLine(i,j,k,4,checkb)==0||GetLine(i,j,k,-1,checkb)==0))//*101
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,-1,checkb)==1&&
                            (GetLine(i,j,k,3,checkb)==0||GetLine(i,j,k,-2,checkb)==0))//1*01
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&GetLine(i,j,k,3,checkb)==1&&
                            (GetLine(i,j,k,4,checkb)==0||GetLine(i,j,k,-1,checkb)==0))//110*
                    {
                        value += rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*10
                    {
                        value += alivetwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,2,checkb)==0&&(GetLine(i,j,k,-1,checkb)==2
                            ||GetLine(i,j,k,-1,checkb)==-1))//2*10 or -1110
                    {
                        value += rushtwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==1&&GetLine(i,j,k,-1,checkb)==0&&(GetLine(i,j,k,2,checkb)==2
                            ||GetLine(i,j,k,2,checkb)==-1))//21*0 or -1110
                    {
                        value += rushtwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==1&&(GetLine(i,j,k,3,checkb)==0
                            ||GetLine(i,j,k,-1,checkb)==0))//*01
                    {
                        value += rushtwo;
                            continue;
                    }
                }
                for(int k = 1; k <= 8; k++)
                {
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,4,checkb)==2)//*1111
                    {
                        value -= combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,-1,checkb)==2)//1*111
                    {
                        value -= combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,-1,checkb)==2&&
                            GetLine(i,j,k,-2,checkb)==2)//11*11
                    {
                        value -= combofive;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*1110
                    {
                        value -= alivefour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==2&&GetLine(i,j,k,-2,checkb)==0)//01*110
                    {
                        value -= alivefour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,4,checkb)==0&&(GetLine(i,j,k,-1,checkb)==1||GetLine(i,j,k,-1,checkb)==-1))//2*1110 or -1*1110
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==2&&(GetLine(i,j,k,-2,checkb)==1||GetLine(i,j,k,-2,checkb)==-1))//21*110 or -11*110
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==2&&
                            GetLine(i,j,k,-2,checkb)==2&&(GetLine(i,j,k,-3,checkb)==1||GetLine(i,j,k,-3,checkb)==-1))//211*10 or -111*10
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,-1,checkb)==2&&GetLine(i,j,k,-2,checkb)==2&&
                            GetLine(i,j,k,-3,checkb)==2&&(GetLine(i,j,k,-4,checkb)==1||GetLine(i,j,k,-4,checkb)==-1))//2111*0 or -1111*0
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,4,checkb)==2)//*1101
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,-1,checkb)==2)//1*101
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,-1,checkb)==2&&
                            GetLine(i,j,k,-2,checkb)==2)//11*01
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&
                            GetLine(i,j,k,3,checkb)==2)//1110*
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,4,checkb)==2)//*1011
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            GetLine(i,j,k,-1,checkb)==2)//1*011
                    {
                        value -= rushfour;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==0&&
                            GetLine(i,j,k,-1,checkb)==0)//0*110
                    {
                        value -= alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==2&&
                            GetLine(i,j,k,-2,checkb)==0)//01*10
                    {
                        value -= alivethree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==0&&
                            (GetLine(i,j,k,-1,checkb)==1&&GetLine(i,j,k,-1,checkb)==-1))//2*110 or -11110
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==2&&
                            (GetLine(i,j,k,-2,checkb)==1&&GetLine(i,j,k,-2,checkb)==-1))//21*10 or -11110
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,-1,checkb)==0&&GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==2&&
                            (GetLine(i,j,k,3,checkb)==1&&GetLine(i,j,k,3,checkb)==-1))//211*0 or -11110
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==2&&
                            (GetLine(i,j,k,4,checkb)==0||GetLine(i,j,k,-1,checkb)==0))//*101
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,-1,checkb)==2&&
                            (GetLine(i,j,k,3,checkb)==0||GetLine(i,j,k,-2,checkb)==0))//1*01
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&GetLine(i,j,k,3,checkb)==2&&
                            (GetLine(i,j,k,4,checkb)==0||GetLine(i,j,k,-1,checkb)==0))//110*
                    {
                        value -= rushthree;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*10
                    {
                        value -= alivetwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,2,checkb)==0&&(GetLine(i,j,k,-1,checkb)==1
                            ||GetLine(i,j,k,-1,checkb)==-1))//2*10 or -1110
                    {
                        value -= rushtwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==2&&GetLine(i,j,k,-1,checkb)==0&&(GetLine(i,j,k,2,checkb)==1
                            ||GetLine(i,j,k,2,checkb)==-1))//21*0 or -1110
                    {
                        value -= rushtwo;
                        continue;
                    }
                    if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==2&&(GetLine(i,j,k,3,checkb)==0
                            ||GetLine(i,j,k,-1,checkb)==0))//*01
                    {
                        value -= rushtwo;
                            continue;
                    }
                }
            }
        }
    }
    return value;
}

/*
 *  2d array to 1d array
 */
int AI::GetLine(int i, int j, int dir, int relapos, int checkb[15][15])
{
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
    return checkb[i][j];
}

/*
 * get the possible points
 */
QVector<BestPos> AI::Generate(int checkb[15][15], int deep)
{
    QVector<BestPos> points;
    QVector<BestPos> tpoints;
    QVector<BestPos> five,fourthree,four,twofour,twothree,three,two;
    for(int i = 0; i < 15; i++)
    {
        for(int j = 0; j < 15; j++)
        {//search unoccupied position
            if(checkb[i][j])
                continue;
            BestPos bp;
            bp.best_x = i;
            bp.best_y = j;
            //must have neighbor
            if(HasNeighbor(checkb,i,j,1))
            {
                int AIscore = PointValue(checkb,i,j,1);//evaluate current point's value of AI
                int Playerscore = PointValue(checkb,i,j,2);//the same
                if(AIscore >= combofive)
                {
                    five.push_front(bp);
                    return five;
                }
                else if(Playerscore >= combofive)
                {
                    five.push_back(bp);
                }
                else if(AIscore >= 2*alivefour)
                {
                    twofour.push_front(bp);
                }
                else if(Playerscore >= 2*alivefour)
                {
                    twofour.push_back(bp);
                }
                else if(AIscore >= alivethree+rushfour)
                {
                    fourthree.push_front(bp);
                }
                else if(Playerscore >= alivethree+rushfour)
                {
                    fourthree.push_back(bp);
                }
                else if(AIscore >= 2*alivethree)
                {
                    twothree.push_front(bp);
                }
                else if(Playerscore >= 2*alivethree)
                {
                    twothree.push_back(bp);
                }
                else if(AIscore >= alivefour)
                {
                    four.push_front(bp);
                }
                else if(Playerscore >= alivefour)
                {
                    four.push_back(bp);
                }
                else if(AIscore >= alivethree)
                {
                    three.push_front(bp);
                }
                else if(Playerscore >= alivethree)
                {
                    three.push_back(bp);
                }
                else if(AIscore >= alivetwo)
                {
                    two.push_front(bp);
                }
                else if(Playerscore >= alivetwo)
                {
                    two.push_back(bp);
                }
                else
                {
                    points.push_back(bp);
                }
            }
            else if(deep>=2 && HasNeighbor(checkb,i,j,2))
            {
                tpoints.push_back(bp);
            }
        }
    }
    if(!five.isEmpty())
    {
        BestPos bp = five.at(0);
        five.clear();
        five.push_back(bp);
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
    return three + two + points + tpoints;
}

/*
 * get a point's value of AI
 * 1-AI,2-player,*-next step
 */
int AI::PointValue(int checkb[15][15], int i, int j, int side)
{
    int value = 0;
    for(int k = 1; k <= 8; k++)
    {
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,4,checkb)==side)//*1111
        {
            value += combofive;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,-1,checkb)==side)//1*111
        {
            value += combofive;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&
                GetLine(i,j,k,-2,checkb)==side)//11*11
        {
            value += combofive;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*1110
        {
            value += alivefour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&
                GetLine(i,j,k,3,checkb)==0&&GetLine(i,j,k,-2,checkb)==0)//01*110
        {
            value += alivefour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                (GetLine(i,j,k,-1,checkb)==0||GetLine(i,j,k,4,checkb)==0))//0*111 or *1110
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&
                (GetLine(i,j,k,3,checkb)==0||GetLine(i,j,k,-2,checkb)==0))//01*11 or 1*110
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==side&&
                GetLine(i,j,k,4,checkb)==side)//*1011
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,-1,checkb)==side)//1*011
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,-1,checkb)==side)//1*011
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,-1,checkb)==side)//*0111
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&
                GetLine(i,j,k,-2,checkb)==side)//10*11
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,-1,checkb)==side)//101*1
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==0&&
                GetLine(i,j,k,4,checkb)==side)//1011*
        {
            value += rushfour;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&GetLine(i,j,k,2,checkb)==0&&
                GetLine(i,j,k,-2,checkb)==0)//01*10
        {
            value += alivethree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==0&&
                GetLine(i,j,k,-1,checkb)==0)//0*110
        {
            value += alivethree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==0&&
                GetLine(i,j,k,-2,checkb)==side&&GetLine(i,j,k,-3,checkb)==0)//01*010
        {
            value += alivethree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==side&&
                GetLine(i,j,k,4,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*0110
        {
            value += alivethree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==0&&GetLine(i,j,k,2,checkb)==side&&GetLine(i,j,k,3,checkb)==0&&
                GetLine(i,j,k,-1,checkb)==side&&GetLine(i,j,k,-2,checkb)==0)//01*010
        {
            value += alivethree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,-1,checkb)==side&&(GetLine(i,j,k,2,checkb)==0||
                GetLine(i,j,k,-2,checkb)==0))//01*1 or 1*10
        {
            value += rushthree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==side&&(GetLine(i,j,k,3,checkb)==0||
                GetLine(i,j,k,-1,checkb)==0))//0*11 or *110
        {
            value += rushthree;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&GetLine(i,j,k,2,checkb)==0&&GetLine(i,j,k,-1,checkb)==0)//0*10
        {
            value += alivetwo;
            continue;
        }
        if(GetLine(i,j,k,1,checkb)==side&&(GetLine(i,j,k,2,checkb)==0||GetLine(i,j,k,-1,checkb)==0))//0*1 or *10
        {
            value += rushtwo;
            continue;
        }
    }
    return value;
}

/*
 * check winning from four direction
 */
bool AI::IsWin(int checkb[15][15])
{
    for(int i = 0; i < 15; i++)
    {
        for(int j = 0; j < 15; j++)
        {
            int side = checkb[i][j];
            if(side == 0)
                continue;
            for(int k = 0; k < 5; k++)
            {
                //check from vertical
                if((i-k>=0 && i+4-k<15) &&
                      (checkb[i-k][j] == side &&
                       checkb[i+1-k][j] == side &&
                       checkb[i+2-k][j] == side &&
                       checkb[i+3-k][j] == side &&
                       checkb[i+4-k][j] == side))
                {
                    return true;
                }
                //check from horizontal
                if((j-k>=0 && j+4-k<15) &&
                      (checkb[i][j-k] == side &&
                       checkb[i][j+1-k] == side &&
                       checkb[i][j+2-k] == side &&
                       checkb[i][j+3-k] == side &&
                       checkb[i][j+4-k] == side))
                {
                    return true;
                }
                //check from leftbevel
                if((i-k>=0 && j-k>=0 && i+4-k<15 && j+4-k<15) &&
                      (checkb[i-k][j-k] == side &&
                       checkb[i+1-k][j+1-k] == side &&
                       checkb[i+2-k][j+2-k] == side &&
                       checkb[i+3-k][j+3-k] == side &&
                       checkb[i+4-k][j+4-k] == side))
                {
                    return true;
                }
                //check from rightbevel
                if((i-k>=0 && j+k<15 && i+4-k<15 && j-4+k>=0) &&
                      (checkb[i-k][j+k] == side &&
                       checkb[i+1-k][j-1+k] == side &&
                       checkb[i+2-k][j-2+k] == side &&
                       checkb[i+3-k][j-3+k] == side &&
                       checkb[i+4-k][j-4+k] == side))
                {
                    return true;
                }

            }
        }
    }
    return false;
}

/*
 * to check whether a point has its neighbors
 */
bool AI::HasNeighbor(int checkb[15][15], int i, int j, int d)
{
    if(checkb[i-d][j] && i-d>=0 && i-d<15)
        return true;
    if(checkb[i+d][j] && i+d>=0 && i+d<15)
        return true;
    if(checkb[i][j-d] && j-d>=0 && j-d<15)
        return true;
    if(checkb[i][j+d] && j+d>=0 && j+d<15)
        return true;
    if(checkb[i-d][j-d] && i-d>=0 && i-d<15 && j-d>=0 && j-d<15)
        return true;
    if(checkb[i-d][j+d] && i-d>=0 && i-d<15 && j+d>=0 && j+d<15)
        return true;
    if(checkb[i+d][j-d] && i+d>=0 && i+d<15 && j-d>=0 && j-d<15)
        return true;
    if(checkb[i+d][j+d] && i+d>=0 && i+d<15 && j+d>=0 && j+d<15)
        return true;
    return false;
}

//unfinished

/*
 * create the 64-random number
 */
__int64 AI::rand64()
{
    return rand()^((__int64)rand()<<15)^((__int64)rand()<<30)^
    ((__int64)rand()<<45)^((__int64)rand()<<60);
}

void AI::InitZobrist()
{
    for(int i = 0; i < 2; i++)
    {
        for(int j = 0; j < 225; j++)
        {
            zobrist[i][j] = rand64();
        }
    }
}

__int64 AI::ZobristKey()
{
    int zobristkey = 0;
    for(int i = 0; i < 2; i++)
    {
        for(int j = 0; j < 225; j++)
        {
            zobristkey = zobristkey^zobrist[i][j];
        }
    }
    return zobristkey;
}

int AI::ProbeHash(int depth, int alpha, int beta)
{
    HASHE *phashe = &hash_table[ZobristKey() % (TableSize-1)];
    if (phashe->key == ZobristKey())
    {
        if (phashe->depth >= depth)
        {
            if (phashe->flags == hashfEXACT)
            {
                return phashe->value;
            }
            if ((phashe->flags == hashfALPHA) && (phashe->value <= alpha))
            {
                return alpha;
            }
            if ((phashe->flags == hashfBETA) && (phashe->value >= beta))
            {
                return beta;
            }
        }
        //RememberBestMove();
    }
    return -666666;
}

void AI::RecordHash(int depth, int value, int hashf,BestPos bp)
{
    HASHE *phashe = &hash_table[ZobristKey() % (TableSize-1)];
    if (phashe->flags != Empty && phashe->depth > depth)
    {
        return;
    }
    phashe->key = ZobristKey();
    phashe->bp = bp;
    phashe->value = value;
    phashe->flags = hashf;
    phashe->depth = depth;
}

void AI::RememberBestMove()
{

}
