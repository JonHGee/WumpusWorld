
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================

public class MyAI extends Agent
{
    String[][] WompWorld = new String[7][7];
    int[] CurrentPos = new int[2];
    int Facing = 1;
    String[] Direction = {"Up", "Right", "Down", "Left"};
    boolean GoHome = false;
    boolean Killed = false;
    boolean WFound = false;
    boolean Gold = false;
    TreeSet<Node> BFS = new TreeSet<>();
    List<Point> Travelled = new ArrayList<>();
    
    
    
    public MyAI ( )
    {
        // ======================================================================
        // YOUR CODE BEGINS
        // ======================================================================
        for(int i=0;i<7;i++) {
            for(int j=0;j<7;j++) {
                WompWorld[j][i] = "U";
            }
        }
        // ======================================================================
        // YOUR CODE ENDS
        // ======================================================================
    }

public Action getAction
(
        boolean stench,
        boolean breeze,
        boolean glitter,
        boolean bump,
        boolean scream
)
{
        // ======================================================================
        // YOUR CODE BEGINS
        // ======================================================================
        if(WompWorld[CurrentPos[0]][CurrentPos[1]].contains("U"))
            WompWorld[CurrentPos[0]][CurrentPos[1]] = WompWorld[CurrentPos[0]][CurrentPos[1]].substring(1);
    
        if(bump) {
            if(Facing == 1) {
                CurrentPos[0]--;
                bound(true, CurrentPos[0]); //right boundary
            } else if (Facing == 0) {
                CurrentPos[1]--;
                bound(false, CurrentPos[1]); //top boundaary
            }
        }
        
        if(glitter) {
            Gold = true;
            GoHome = true;
            return Action.GRAB;
        }
        
        if(!WFound && stench && !WompWorld[CurrentPos[0]][CurrentPos[1]].contains("S")) {   
            if (Killed && !breeze) WompWorld[CurrentPos[0]][CurrentPos[1]] = "E";
            else if (Killed) WompWorld[CurrentPos[0]][CurrentPos[1]] = "B";
            else WompWorld[CurrentPos[0]][CurrentPos[1]] = "S";
        }
        
        if(breeze && !WompWorld[CurrentPos[0]][CurrentPos[1]].contains("B")) {
            WompWorld[CurrentPos[0]][CurrentPos[1]] = "B";
        } 
        
        if (breeze && stench && !Killed && !WFound) {
           WompWorld[CurrentPos[0]][CurrentPos[1]] = "SB";
        }
        
        if(!stench && !breeze) {
            WompWorld[CurrentPos[0]][CurrentPos[1]] = "E";
        }
        
        if(scream) {
            
            Killed = true;
            dead();
        }
        
        update();
        
        int a = firstAction(CurrentPos[0], CurrentPos[1], Facing, 0, Facing);
        
        
        BFS.clear();
        Travelled.clear();
        if (a < 0) {
            GoHome = true;
            a = firstAction(CurrentPos[0], CurrentPos[1], Facing, 0, Facing);
        }
        
        
        
        
        

        switch (a) {
            case 0:
                switch(Facing) {
                    case 0: CurrentPos[1]++;
                        break;
                    case 1: CurrentPos[0]++;
                        break;
                    case 2: if (CurrentPos[1] > 0) CurrentPos[1]--;
                        break;
                    case 3: if (CurrentPos[0] > 0) CurrentPos[0]--;
                }
                return Action.FORWARD;
            case 1:
                Facing = (Facing+1)%4;
                return Action.TURN_RIGHT;
            case 2:
                Facing = (Facing-1)%4;
                if (Facing < 0) Facing += 4;
                return Action.TURN_LEFT;
            case 3:
                return Action.CLIMB;
            default:
                return Action.CLIMB;
        }
        // ======================================================================
        // YOUR CODE ENDS
        // ======================================================================
    }

    // ======================================================================
    // YOUR CODE BEGINS
    // ======================================================================

    void update() {
        
        if(WompWorld[CurrentPos[0]][CurrentPos[1]].contains("E")) {
            safe(CurrentPos[0],CurrentPos[1]);
        }
        
            if(WompWorld[CurrentPos[0]][CurrentPos[1]].contains("S")) {
                womp(CurrentPos[0],CurrentPos[1]);
            }
            int count = 0;
            for(int i=0;i<7;i++) {
                for(int j=0;j<7;j++) {
                    if (WompWorld[i][j].contains("S")) count++;
                }
            }
            if (count > 1) {
                WFound = true;
                found();
            }
        
        if(WompWorld[CurrentPos[0]][CurrentPos[1]].contains("B")) {
            pit(CurrentPos[0],CurrentPos[1]);
        }         
    }
    
    void bound(boolean right, int x) {
        if (right) {
            for(int i=0;i<7;i++) {
                for(int j=x+1;j<7;j++) {
                    WompWorld[j][i] = "O";
                }
            }
        } else {
            for(int i=0;i<7;i++) {
                for(int j=x+1;j<7;j++) {
                    WompWorld[i][j] = "O";
                }
            }
        }
    }
    
    void dead() {
        for(int i=0;i<7;i++) {
            for(int j=0;j<7;j++) {
                if (WompWorld[i][j].contains("S"))
                    if(WompWorld[i][j].contains("B")) WompWorld[i][j] = "B";
                    else WompWorld[i][j] = "E";
                if (WompWorld[i][j].contains("W"))
                    if(WompWorld[i][j].contains("P")) WompWorld[i][j] = "P";
                    else WompWorld[i][j] = "E";
            }
        }
    }
    
    void found() {
        boolean f = true;
        int x, y, x1=0, y1=0, x2=0, y2=0;
        outer:
        for(int i=0;i<7;i++) {
            for(int j=0;j<7;j++) {
                if (WompWorld[i][j].contains("S")) {
                    if (f) {
                        x1 = i;
                        y1 = j;
                        f = false;
                    } else {
                        x2 = i;
                        y2 = j;
                        break outer;
                    }
                }
            }
        }
        if (x1 == x2) {
            x = x1;
            y = (y1+y2)/2;
        } else if (y1 == y2) {
            x = (x1+x2)/2;
            y = y1;
        } else {
            if (WompWorld[x1][y2].contains("W") && WompWorld[x2][y1].contains("W")) {
                WFound = false;
                for(int i=0;i<7;i++) {
                    for(int j=0;j<7;j++) {
                        if(WompWorld[i][j].contains("W")) {
                            if(WompWorld[i][j].contains("P")) WompWorld[i][j] = "P";
                            else WompWorld[i][j] = "A";
                        }
                    }
                }
                if (WompWorld[x1][y2].contains("P")) WompWorld[x1][y2] = "WP";
                else WompWorld[x1][y2] = "W";
                if (WompWorld[x2][y1].contains("P")) WompWorld[x2][y1] = "WP";
                else WompWorld[x2][y1] = "W";
                return;
            } else if (WompWorld[x1][y2].contains("W")) {
                x = x1;
                y = y2;
            } else {
                x = x2;
                y = y1;
            }
        }
        for(int i=0;i<7;i++) {
            for(int j=0;j<7;j++) {
                if(WompWorld[i][j].contains("W")) {
                    if(WompWorld[i][j].contains("P")) WompWorld[i][j] = "P";
                    else WompWorld[i][j] = "A";
                }
            }
        }
        if (WompWorld[x][y].contains("P")) WompWorld[x][y] = "WP";
        else WompWorld[x][y] = "W";
    }
    
    void safe(int x, int y) {
        if (x<6 && (WompWorld[x+1][y].contains("U") || WompWorld[x+1][y].contains("W")
                || WompWorld[x+1][y].contains("P"))) WompWorld[x+1][y] = "A";        
        if (x>0 && (WompWorld[x-1][y].contains("U") || WompWorld[x-1][y].contains("W")
                || WompWorld[x-1][y].contains("P"))) WompWorld[x-1][y] = "A";
        if (y<6 && (WompWorld[x][y+1].contains("U") || WompWorld[x][y+1].contains("W")
                || WompWorld[x][y+1].contains("P"))) WompWorld[x][y+1] = "A";
        if (y>0 && (WompWorld[x][y-1].contains("U") || WompWorld[x][y-1].contains("W")
                || WompWorld[x][y-1].contains("P"))) WompWorld[x][y-1] = "A";
    }
    
    void womp(int x, int y) {
        if (x<6) check(x+1,y, "W", "P", "S", "B");        
        if (x>0) check(x-1,y, "W", "P", "S", "B");
        if (y<6) check(x,y+1, "W", "P", "S", "B");
        if (y>0) check(x,y-1, "W", "P", "S", "B");
    }
    
    void check(int x, int y, String c, String o, String b, String s) {
        if (x<6) 
            if (WompWorld[x+1][y].contains("E") 
                    || (WompWorld[x+1][y].contains(s) && !WompWorld[x+1][y].contains(b)))
                if (WompWorld[x][y].contains("U") || 
                        (WompWorld[x][y].contains(c) && !WompWorld[x][y].contains(o))) {
                    WompWorld[x][y] = "A";
                    return;
                } else { return; }
        if (x>0)  
            if (WompWorld[x-1][y].contains("E") 
                    || (WompWorld[x-1][y].contains(s) && !WompWorld[x-1][y].contains(b)))
                if (WompWorld[x][y].contains("U") || 
                        (WompWorld[x][y].contains(c) && !WompWorld[x][y].contains(o))) {
                    WompWorld[x][y] = "A";
                    return;
                } else { return; }
        if (y<6)
            if (WompWorld[x][y+1].contains("E") 
                    || (WompWorld[x][y+1].contains(s) && !WompWorld[x][y+1].contains(b)))
                if (WompWorld[x][y].contains("U") || 
                        (WompWorld[x][y].contains(c) && !WompWorld[x][y].contains(o))) {
                    WompWorld[x][y] = "A";
                    return;
                } else { return; }
        if (y>0)
            if (WompWorld[x][y-1].contains("E") 
                    || (WompWorld[x][y-1].contains(s) && !WompWorld[x][y-1].contains(b)))
                if (WompWorld[x][y].contains("U") || 
                        (WompWorld[x][y].contains(c) && !WompWorld[x][y].contains(o))) {
                    WompWorld[x][y] = "A";
                    return;
                } else { return; }
        if (WompWorld[x][y].equals("W") && c.equals("P")) {
            WompWorld[x][y] = "WP";
            return;
        }
        if (!WompWorld[x][y].equals("O") && !WompWorld[x][y].equals("E")
                && !WompWorld[x][y].contains("S") && !WompWorld[x][y].contains("B"))
            WompWorld[x][y] = c;
    }
    
    void pit (int x, int y) {
        if (x<6) check(x+1,y, "P", "W", "B", "S");        
        if (x>0) check(x-1,y, "P", "W", "B", "S");
        if (y<6) check(x,y+1, "P", "W", "B", "S");
        if (y>0) check(x,y-1, "P", "W", "B", "S");
    }
    
    class Node implements Comparable<Node> {
        int firstmove, count, x, y, dir;
        
        Node(int x, int y, int firstmove, int count, int dir) {
            this.x = x;
            this.y = y;
            this.firstmove = firstmove;
            this.count = count;
            this.dir = dir;
        }
        
        @Override
        public int compareTo(Node o) {
            if (this.x == o.x && this.y == o.y) return 0;
            if (this.count < o.count) return -1;
            else return 1;
        }
       
        public String toString() {
            return ("("+this.x+","+this.y+") count:"+this.count);
        }
    }
    
    int firstAction(int x, int y, int firstmove, int count, int dir) {
        Travelled.add(new Point(x,y));
        Node Current = new Node(x,y, firstmove, count, dir);
        if (GoHome && x == 0 && y == 0) return 3;
        int r;
        // 0:Forward
        // 1:Turn right
        // 2:Turn left
        switch(dir) {
                case 0:
                    if (y<6) BFS.add(new Node(x, y+1, 0, count+1, 0));
                    if (y>0) BFS.add(new Node(x, y-1, 1, count+3, 2));
                    if (x<6) BFS.add(new Node(x+1, y, 1, count+2, 1));
                    if (x>0) BFS.add(new Node(x-1, y, 2, count+2, 3));
                    break;
                case 1:
                    if (x<6) BFS.add(new Node(x+1, y, 0, count+1, 1));
                    if (y<6) BFS.add(new Node(x, y+1, 2, count+2, 0));
                    if (y>0) BFS.add(new Node(x, y-1, 1, count+2, 2));                    
                    if (x>0) BFS.add(new Node(x-1, y, 1, count+3, 3));
                    break;
                case 2:
                    if (y<6) BFS.add(new Node(x, y+1, 1, count+3, 0));
                    if (y>0) BFS.add(new Node(x, y-1, 0, count+1, 2));
                    if (x<6) BFS.add(new Node(x+1, y, 2, count+2, 1));
                    if (x>0) BFS.add(new Node(x-1, y, 1, count+2, 3));
                    break;
                case 3:
                    if (y<6) BFS.add(new Node(x, y+1, 1, count+2, 0));
                    if (y>0) BFS.add(new Node(x, y-1, 2, count+2, 2));
                    if (x<6) BFS.add(new Node(x+1, y, 1, count+3, 1));
                    if (x>0) BFS.add(new Node(x-1, y, 0, count+1, 3));
                    break;
            }
        
        
        while (BFS.size() >0) {
            Current = BFS.pollFirst();            
            r = makeAction(Current);            
            if (r >= 0) {return r;}            
        }
        return -1;
    }
    
    int makeAction(Node n) {
        if (!Travelled.contains(new Point(n.x,n.y))) Travelled.add(new Point(n.x,n.y));
        if (GoHome) {
            if (n.x == 0 && n.y == 0) return n.firstmove;
        } else if (WompWorld[n.x][n.y].contains("A"))
            return n.firstmove;
        if (WompWorld[n.x][n.y].contains("P") || WompWorld[n.x][n.y].contains("W") 
                || WompWorld[n.x][n.y].contains("U") || WompWorld[n.x][n.y].contains("O"))
            return -1;        
        switch(n.dir) {
            case 0:
                if (n.y<6 && !Travelled.contains(new Point(n.x,n.y+1)))
                    BFS.add(new Node(n.x, n.y+1, n.firstmove, n.count+1, 0));
                if (n.y>0 && !Travelled.contains(new Point(n.x,n.y-1)))
                    BFS.add(new Node(n.x, n.y-1, n.firstmove, n.count+3, 2));
                if (n.x<6 && !Travelled.contains(new Point(n.x+1,n.y)))
                    BFS.add(new Node(n.x+1, n.y, n.firstmove, n.count+2, 1));
                if (n.x>0 && !Travelled.contains(new Point(n.x-1,n.y)))
                    BFS.add(new Node(n.x-1, n.y, n.firstmove, n.count+2, 3));
                break;
            case 1:
                if (n.y<6 && !Travelled.contains(new Point(n.x,n.y+1)))
                    BFS.add(new Node(n.x, n.y+1, n.firstmove, n.count+2, 0));
                if (n.y>0 && !Travelled.contains(new Point(n.x,n.y-1)))
                    BFS.add(new Node(n.x, n.y-1, n.firstmove, n.count+2, 2));
                if (n.x<6 && !Travelled.contains(new Point(n.x+1,n.y)))
                    BFS.add(new Node(n.x+1, n.y, n.firstmove, n.count+1, 1));
                if (n.x>0 && !Travelled.contains(new Point(n.x-1,n.y)))
                    BFS.add(new Node(n.x-1, n.y, n.firstmove, n.count+3, 3));
                break;
            case 2:
                if (n.y<6 && !Travelled.contains(new Point(n.x,n.y+1)))
                    BFS.add(new Node(n.x, n.y+1, n.firstmove, n.count+3, 0));
                if (n.y>0 && !Travelled.contains(new Point(n.x,n.y-1)))
                    BFS.add(new Node(n.x, n.y-1, n.firstmove, n.count+1, 2));
                if (n.x<6 && !Travelled.contains(new Point(n.x+1,n.y)))
                    BFS.add(new Node(n.x+1, n.y, n.firstmove, n.count+2, 1));
                if (n.x>0 && !Travelled.contains(new Point(n.x-1,n.y)))
                    BFS.add(new Node(n.x-1, n.y, n.firstmove, n.count+2, 3));
                break;
            case 3:
                if (n.y<6 && !Travelled.contains(new Point(n.x,n.y+1)))
                    BFS.add(new Node(n.x, n.y+1, n.firstmove, n.count+2, 0));
                if (n.y>0 && !Travelled.contains(new Point(n.x,n.y-1)))
                    BFS.add(new Node(n.x, n.y-1, n.firstmove, n.count+2, 2));
                if (n.x<6 && !Travelled.contains(new Point(n.x+1,n.y)))
                    BFS.add(new Node(n.x+1, n.y, n.firstmove, n.count+3, 1));
                if (n.x>0 && !Travelled.contains(new Point(n.x-1,n.y)))
                    BFS.add(new Node(n.x-1, n.y, n.firstmove, n.count+1, 3));
                break;
        }   
        return -1;
    }
    // ======================================================================
    // YOUR CODE ENDS
    // ======================================================================
}