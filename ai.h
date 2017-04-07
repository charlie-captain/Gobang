#ifndef AI_H
#define AI_H
#include <QVector>
#include <QTime>
#include <iostream>
#include <time.h>

using namespace std;

#define hashfEXACT 0
#define hashfALPHA 1
#define hashfBETA 2
#define Empty 3
#define TableSize 1000000

class BestPos
{
public:
    int best_x;
    int best_y;

private:

};

typedef struct tagHASHE
{
    __int64 key;
    int depth;
    int flags;
    int value;
    BestPos bp;
}HASHE;

class AI
{
public:
    AI();
    QString action(int checkb[15][15], int depth);//make move
    int GetLine(int i, int j, int dir, int relapos, int checkb[15][15]);//2d array to 1d array
    int evaluate(int checkb[15][15]);//evaluate current situation
    int Max(int checkb[15][15], int deep, int alpha, int beta);//search for max value
    int Min(int checkb[15][15], int deep, int alpha, int beta);//search for min value
    int PointValue(int checkb[15][15], int i, int j, int side);//get a point's value of AI
    bool HasNeighbor(int checkb[15][15],int i,int j,int d);//to check whether a point has its neighbors
    bool IsWin(int checkb[15][15]);//check winning
    QVector<BestPos> Generate(int check[15][15], int deep);//get the possible points

    //unfinished
    __int64 rand64(void);
    __int64 ZobristKey();
    void InitZobrist();
    int ProbeHash(int depth, int alpha, int beta);
    void RecordHash(int depth, int value, int hashf, BestPos bp);
    void RememberBestMove();

private:
    const int alivetwo = 10;
    const int rushtwo = 1;
    const int alivethree = 100;
    const int rushthree = 10;
    const int alivefour = 1000;
    const int rushfour = 100;
    const int combofive = 10000;
    const int MAX = 999999;
    const int MIN = -999999;
    int ABcuts;//cutting times
    int total;//total searching points

    //unfinished
    __int64 zobrist[2][225];//0-upoccupied,1-black,2-white
    HASHE hash_table[TableSize];
};

#endif // AI_H
