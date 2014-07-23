package maze;
import java.util.*;
import java.util.List;
import java.awt.*;

import maze.MazeModule;

import javax.swing.*;

public class Position {
	public int row=0;
	public int col=0;
	static public int l=40;
	List<Integer> dir_list;
	
	int wall[]={1,1,1,1}; //右下左上
	
	Position(){}
	Position(int r, int c)
	{
		row=r;
		col=c;
		dir_list=new ArrayList<Integer>(); //生成随机方向链表dir_list
		Random ran=new Random();
		for(int i=3;i>=0;i--)
		{
			int random=ran.nextInt(4-i);
			dir_list.add(random,i);
		}
			
	}
	
	public void setWidth(int w)
	{
		l=w;
	}
	
	public boolean breakDir(int dir)
	{
		wall[dir]=0;
		return true;
	}
	

	
	public int nextRandomDir(){ //下一个随机方向
		int i=0;
		if(dir_list.isEmpty())
			return 4;
		i=dir_list.remove(0);
		return i;
	}
	
	
	public boolean hasNext()
	{
		if(dir_list.isEmpty())
			return false;
		return true;
	}
	
	public String toString() //(row, col)
	{
		String s="";
		s="("+row+", "+col+")";
		return s;
	}
	//0底 1边 2墙 3返  

	Color c0=new Color(255,250,250);
//	Color c0=new Color(255,166,135);
	Color c1=new Color(235,235,235);
	Color c2=new Color(80,80,80);
	Color c3=new Color(220,220,220);
	Color c5=new Color(190,190,190);
	Color c6=new Color(210,210,210);
	Color c4=c1;
	int d=2;
	public void draw(Graphics g)
	{
		if(maze.MazeModule.maze_arr[row][col]==1){
			g.setColor(c0);	
			g.fillRect(l*(col-1), l*(row-1), l, l);	
//			c4=c6;
		}
		if(maze.MazeModule.maze_arr[row][col]==2){
			g.setColor(c3);	
			g.fillRect(l*(col-1), l*(row-1), l, l);	
			c4=c5;
		}
		g.setColor(c4);
		g.fillRect(l*col-d, l*(row-1), d, l);
		g.fillRect(l*(col-1), l*row-d, l, d);
		g.fillRect(l*(col-1), l*(row-1), d, l);
		g.fillRect(l*(col-1), l*(row-1), l, d);
		c4=c1;
		g.setColor(c2);
		if(wall[0]==1) //右
			g.fillRect(l*col-d, l*(row-1), d, l);
		if(wall[1]==1) //下
			g.fillRect(l*(col-1), l*row-d, l, d);
		if(wall[2]==1) //左
			g.fillRect(l*(col-1), l*(row-1), d, l);
		if(wall[3]==1) //上
			g.fillRect(l*(col-1), l*(row-1), l, d);
		
		g.fillRect(l*col-d, l*(row-1), d, d);
		g.fillRect(l*col-d, l*row-d, d, d);
		g.fillRect(l*(col-1), l*row-d, d, d);
		g.fillRect(l*(col-1), l*(row-1), d, d);
	}
	
	
	
	
}
