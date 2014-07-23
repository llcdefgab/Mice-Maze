package maze;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.*;
import java.net.URI;

public class MazeModule {

	/*
	int[][] maze=
		{
		{1,1,1,1,1,1,1},
		{1,0,0,0,1,1,1},
		{1,1,1,0,1,1,1},
		{1,0,0,0,1,1,1},
		{1,0,1,1,1,1,1},
		{1,0,0,0,0,0,1},
		{1,1,1,1,1,1,1}
		}; //迷宫初始化
*/
	static int a=0,b=0; //迷宫长和宽
	static int l=0;
	static int sleep_time=1000;//maze show 间隔时间 
	
	List<Position> allpath; //创建迷宫_所有走过的位置
	List<Position> tempath; //创建迷宫_找到且有方向可走
	static List<Position> path; //解迷宫_路径

	public static int[][] arr; //迷宫数组
	public static int[][] maze_arr; //解迷宫数组
	

	
	static Position[][] pos;//迷宫位置数组 
	
	static MazeFrame mazeFrame;
	static MazeModule maze;
	static TextPanel p4;
	static StackPanel p3;
	static JPanel all; 
	static ButtonPanel p1;
	static MazePanel p2;
	static JTextArea textArea;
	
	static int needStop=0;

	
	//欢迎模块
 	int Welcome()
	{
		return 0;
	}
	
	//输入迷宫
	void InputMaze()
	{	
		
	}
	

	//方法_创建随机迷宫
	public boolean createRandomMaze(int aa,int bb)

	{
		a=aa;
		b=bb;
		if(a>=b){
			l=400/a;
		}
		else{
			l=400/b;
		}
		Position p=new Position();
		p.setWidth(l);
//		System.out.println("l: "+l);
		tempath = new ArrayList<Position>();
		allpath = new ArrayList<Position>();
		arr=new int[a+2][b+2];
		maze_arr=new int[a+2][b+2];
		pos=new Position[a+2][b+2];
		//给迷宫加一圈障碍物
		for(int i=0;i<=b+1;i++){
			arr[0][i]=1;//顶
			arr[a+1][i]=1;//底
			maze_arr[0][i]=1;//顶
			maze_arr[a+1][i]=1;//底
		}
		for(int i=0;i<=a+1;i++){
			arr[i][0]=1;//左
			arr[i][b+1]=1;//右
			maze_arr[i][0]=1;//左
			maze_arr[i][b+1]=1;//右
		}
		
		for(int i=1;i<=a;i++)
		{
			for(int j=1;j<=b;j++)
			{
				pos[i][j]=new Position(i,j);//迷宫初始化
				arr[i][j]=0;
				maze_arr[i][j]=0;
			}
		}
		Position here=pos[1][1];//从(1,1)开始
		tempath.add(0,here);
		allpath.add(0,here);
		arr[1][1]=1;
		int r=0,c=0,d=0,result=0,count=2;
		while(!tempath.isEmpty())//路径不为空
		{ 
			r=here.row;
			c=here.col;
			//arr[r][c]=1;//1为走过的
			d=here.nextRandomDir();//有墙的方向
			while(d!=4)//有方向
			{
				switch(d){ //1234,右下左上
				case 0:
					r=here.row;
					c=here.col+1;
					break;
				case 1:
					r=here.row+1;
					c=here.col;
					break;
				case 2:
					r=here.row;
					c=here.col-1;
					break;
				case 3:
					r=here.row-1;
					c=here.col;
					break;
				default:
					System.out.println("wrong dir: "+d);
					break;
				}
				result=arr[r][c];//下一个位置为0
				if(result==0)
					break; //找到下一个合适的位置
				d=here.nextRandomDir();	//继续找	
			}
			if(d!=4) //找到下一个位置
			{
				here.wall[d]=0;//去掉该方向的墙
				here=pos[r][c];
				arr[r][c]=count;
				count++;
				d=(d+2)%4;
				here.wall[d]=0;//去掉下一位置反方向的墙
				tempath.add(0,here);
				allpath.add(0,here);
			}
			else{ //未找到下一个位置
				//arr[here.row][here.col]=2;//2代表尽头，需回溯
				if(!tempath.isEmpty()){
				here=tempath.remove(0);
				}
			}
		}
//		System.out.println("finish create");
		return true;
	}

	//寻找路径
	public Boolean FindPath()
	{		
//		Position[] offset=  //右下左上的移动方向
//			{
//				new Position(0,1),
//				new Position(1,0),
//				new Position(0,-1),
//				new Position(-1,0)
//			};
		path = new ArrayList<Position>();

		Position here=pos[1][1];
		//path.add(here);
		maze_arr[1][1]=1;//防止返回入口
		int option=0;
		int LastOption=3;
		//寻找一条路径
		int r=0,c=0;
		while(!(here.row==a && here.col==b))//不是出口
		{
			//寻找并移动到一个相邻的位置
			while(option<=LastOption)
			{
				r=here.row+offset[option].row;
				c=here.col+offset[option].col;
				if(here.wall[option]==0&&maze_arr[r][c]==0){ //该方向没有墙
						break; //该方向有下一个位置
					}
				option++;
			}
			//找到一个相邻的位置了么
			if(option<=LastOption)//移动到maze[r][c]
			{
				path.add(0,here);//List头部插入
				here = pos[r][c];
				//设置障碍物以防止再次访问
				maze_arr[r][c]=1;
				option=0;
			}
			else//没有可用的相邻的位置，回溯
			{
				if(path.isEmpty())
				{
					return false;
				}
				Position last;
				last=path.remove(0);
				if(last.row==here.row)
					option = 2 + last.col - here.col;
				else option = 3 + last.row - here.row;
				here=last;
			}	
		}
		System.out.println("finish find");
		return true;
	}
	
	public boolean ResetPath(){
		path = new ArrayList<Position>();//重设path
		here=pos[1][1];
		path.add(0,here);
		
		for(int i=1;i<=a;i++)
		{
			for(int j=1;j<=b;j++)
			{
				maze_arr[i][j]=0;
			}
		}	
		maze_arr[1][1]=1;
		option=0;
		LastOption=3;
		r=1;c=1;
		find=0;notWall=0;isBlank=0;
		text="";	
		System.out.println("reseted Path");
		return true;
	}
	
	//nextStep变量声明
	//-----------------------------------------------------------------
	static int resetPath=1; 
	Position[] offset=  //右下左上的移动方向
		{
			new Position(0,1),
			new Position(1,0),
			new Position(0,-1),
			new Position(-1,0)
		};
	static Position here;
	int option=0;
	int LastOption=3;
	int r=0,c=0;
	int find=0,notWall=0,isBlank=0;
	String text="",line="--------------------\n";
	//------------------------------------------------------------------
	public boolean nextSetp(){
		text="";
		if(!(here.row==a && here.col==b))//不是出口
		{
			System.out.println("not out");
			while(option<=LastOption)
			{
				//-----------------------------------------------
				text=text+"起始位置: ("+here.row+","+here.col+")"+"\n";
				text=text+"下一方向: "+option+"\n";
				//-----------------------------------------------

				r=here.row+offset[option].row;
				c=here.col+offset[option].col;
	
				if(here.wall[option]==0)
					{
						notWall=1;
						if(maze_arr[r][c]==0){
							isBlank=1;
							find=1;
							break;
						}else{isBlank=0;option++;}
					}
				else{
					notWall=0;
					option++;
				}
				//-----------------------------------------------
				text=text+"下一位置: ("+r+","+c+")"+"\n";
				text=text+"没有墙: "+notWall+"\n";
				text=text+"没走过: "+isBlank+"\n";
				//-----------------------------------------------
			}
//			System.out.println(text);
			if(find==1)
			{
				//-----------------------------------------------
				text=text+"下一位置: ("+r+","+c+")"+"\n";
				text=text+"没有墙: "+notWall+"\n";
				text=text+"没走过: "+isBlank+"\n";
				text=text+"找到合适的位置!"+"\n";
				//-----------------------------------------------
				here = pos[r][c];
				path.add(0,here);
				maze_arr[r][c]=1;
				option=0;			
				find=0;
				//-----------------------------------------------
				text=text+"移动到下一位置("+r+","+c+")"+"\n";	
				text=text+"把该位置压入堆栈"+"\n";		
				//-----------------------------------------------
			}
			else
			{
		
				if(path.isEmpty())
				{
					return false;
				}
				Position last;
				maze_arr[here.row][here.col]=2;
				path.remove(0);
				last=path.get(0);
				if(last.row==here.row)
					option = 2 + last.col - here.col;
				else option = 3 + last.row - here.row;
				here=last;
				//-----------------------------------------------
				text=text+"未找到合适位置!需回溯!"+"\n";
				text=text+"从堆栈弹出上一位置"+"\n";			
				text=text+"移动到上一位置("+here.row+","+here.col+")"+"\n";			
				//-----------------------------------------------
			}	
		}
		text=text+line;
//		System.out.println(text);
		p2.repaint();
		p3.repaint();
		textArea.append(text);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		return true;
	}
	
	
	
	
	//输出路径
	String OutputPath(List<Position> p)
	{
		
		String s="";
		for(int i=p.size()-1;i>=0;i--)
		s=s+p.get(i).toString()+"\t";
		return s;
	}

	
	

	public static class ButtonPanel extends JPanel implements ActionListener,ChangeListener
	{
		public JButton start,step,show,pause;
		public JSlider delay;
		int MIN = 0;
	    int MAX = 1500;
	    int INIT = 200; 
		public ButtonPanel()
		{
		    sleep_time=INIT;
			start=new JButton("create maze");
			start.setActionCommand("start");
			start.addActionListener(this);	
			add(start);
			

			show=new JButton("maze show");
			show.setActionCommand("show");
			show.addActionListener(this);		
			add(show);

			
			pause=new JButton("pause show");
			pause.setActionCommand("pause");
			pause.addActionListener(this);
			add(pause);
			
			step=new JButton(" next step ");
			step.setActionCommand("step");
			step.addActionListener(this);
			add(step);
			
			delay=new JSlider(JSlider.HORIZONTAL,MIN,MAX,INIT);
			delay.addChangeListener(this);
			
	        delay.setMajorTickSpacing(700);
	        delay.setMinorTickSpacing(350);
//	        delay.setPaintTicks(true);
	        delay.setPaintLabels(true);
	        delay.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	        Font font = new Font("Serif", Font.ITALIC, 14);
	        delay.setFont(font);
//	        delay.setForeground(new Color(255,255,255));
	        delay.setPreferredSize(new Dimension(160,50));
//	        System.out.println("delay.WIDTH: "+delay.WIDTH);
			add(delay);
			
		}
		
		public void actionPerformed(ActionEvent e) {
			//start
			if("start".equals(e.getActionCommand())){
				maze.createRandomMaze(a, b);
				//maze.FindPath();
				maze.ResetPath();
				needStop=1;
				p2.resetI();
				p3.repaint();
				textArea.setText("~~~~~~~~maze~~~~~~~~~\n");
				p4.repaint();
			}
			
			//show
			if("show".equals(e.getActionCommand())){
				System.out.println("show pressed");
				//maze.ResetPath();		
				needStop=0;
				new Thread(new Runnable(){
		            public void run(){
						System.out.println("Thread start");
		            	while(!(here.row==a && here.col==b)){
		            		if(needStop==1){
		            			break;	            			
		            		}
		            		maze.nextSetp();
			                 try{
			                     Thread.sleep(sleep_time);
			                 }catch(Exception ex){
			                	System.out.println("Thread error"); 
			                 }
		            	}
		            	needStop=0;
		            }
				}).start();
			}
			
			//stop
			if("pause".equals(e.getActionCommand())){
				needStop=1;
			}
			
			//step
			if("step".equals(e.getActionCommand())){
				System.out.println("path.size: "+path.size());
				maze.nextSetp();
			}
		}

		public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            int time = (int)source.getValue();
	                sleep_time=time;
	        }
		}
		
	}


	public static class MazePanel extends JPanel 
	{
		Graphics g;
		int i=path.size()-1, x=0,y=0;
		
		public void paintComponent(Graphics gg)
		{
			g=gg;
			super.paintComponent(g);
			setLayout(null);
			//setBounds(20,20,500,500);
			//setBorder(BorderFactory.createLineBorder(Color.black));
			
//			JLabel icon=new JLabel("1231231231231231");
//			icon.setBackground(Color.black);
//			icon.setOpaque(true);
//			icon.setBounds(0, 0, 400, 400);
//			add(icon);
			
			 ImageIcon icon =createImageIcon("images/icon.jpg","a pretty but meaningless splat");;
			 // 图片随窗体大小而变化
			 g.drawImage(icon.getImage(), 0, 0,null);
			
			Color c=g.getColor();
			g.setColor(Color.black);
			this.drawPos(g);
			
			//paint mice
			if(i>=0){	
			x=path.get(0).row;
			y=path.get(0).col;	
			g.setColor(new Color(249,67,19));
			g.fillOval((y-1)*l+l/2-7, (x-1)*l+l/2-7, 5, 5);
			g.fillOval((y-1)*l+l/2+2, (x-1)*l+l/2-7, 5, 5);
			g.fillOval((y-1)*l+l/2-5, (x-1)*l+l/2-5, 10, 10);
			}
			
			g.setColor(c);
			setOpaque(true);
			
		}
		protected ImageIcon createImageIcon(String path, String description) {
			java.net.URL imgURL = getClass().getResource(path);
			if (imgURL != null) {
			return new ImageIcon(imgURL, description);
			} else {
			System.err.println("Couldn't find file: " + path);
			return null;
			}
		}
		public void update(Graphics g){
			System.out.println("updated");
			super.update(g);

		}

		public void drawPos(Graphics g){
			for(int i=1;i<=a;i++){
				for(int j=1;j<=b;j++){
					pos[i][j].draw(g);
				}		
			}
		}
		
		public void solveMaze(){
			i--;
		}
		public void resetI(){
			i=path.size()-1;
			repaint();
		}
	}
	
	
	public static class StackPanel extends JPanel
	{
		public JLabel[] labels;
		int n=0;
		int a=0,b=0;
		String r="",c="",pa="",pb="";
		public StackPanel(){
			labels=new JLabel[40];
			setLayout(new GridLayout(8,5,3,3));
			for(int i=0;i<40;i++){
				labels[i]=new JLabel();
			}
			
			System.out.println("path.length: "+path.size());
			
			add(labels[0]);
		}
			
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			n=path.size()-1;
//			System.out.println("StackPanel repainted, n="+n+", labelwidth="+labels[0].getWidth()+", labelheight="+labels[0].getHeight());
			if(n>39){
				n=39;
			}
			for(int i=0;i<40;i++){
				if(i<=n){
				a=path.get(i).row;
				b=path.get(i).col;
				if(a<10)
					pa="0";
				else pa="";
				if(b<10)
					pb="0";
				else pb="";
				r=pa+a;
				c=pb+b;
				labels[i].setText(r+","+c);
				labels[i].setHorizontalAlignment(JLabel.CENTER);
				labels[i].setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
				labels[i].setForeground(new Color(255,255,255));//字体颜色
				labels[i].setBackground(new Color(185,228,255));//背景颜色
				if(i==0){
//					labels[i].setBackground(new Color(249,67,19));
					labels[i].setBackground(new Color(146,218,255));
				}
				//labels[i].setSize(97,22);
				labels[i].setOpaque(true);
				add(labels[i]);
				}else{
					labels[i].setOpaque(false);
					labels[i].setText("");
					add(labels[i]);
				}
			}
			setOpaque(true);	
		}
		
		public void refresh()
		{
			System.out.println("refreshed");
			int n=path.size();
			if(n>39)
				n=39;
			for(int i=0;i<=n;i++){
				a=path.get(i).row;
				b=path.get(i).col;
				if(a<10)
					pa="0";
				else pa="";
				if(b<10)
					pb="0";
				else pb="";
				r=pa+a;
				c=pb+b;
				labels[i].setText(r+","+c);
			}
			
			repaint();	
		}
		
		
	}
	public static class TextPanel extends JPanel
	{
		public TextPanel(){
			setLayout(null);
			String s="This is an editable JTextArea. " +
				    "A text area is a \"plain\" text component, " +
				    "which means that although it can display text " +
				    "in any font, all of the text is in the same font."+
				    "This is an editable JTextArea. " ;
			s=s+s+s+s;
			textArea = new JTextArea("~~~~~~~~maze~~~~~~~~~\n\n",2000,15);	
//			textArea.setFont(new Font("Serif", Font.ITALIC, 13));
			textArea.setFont(new Font("Dialog", Font.PLAIN, 13));
			textArea.setForeground(new Color(255,255,255));
//			textArea.setForeground(new Color(60,60,60));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setBackground(new Color(197,198,199));
//			textArea.setBackground(new Color(180,180,180));
//			textArea.setBackground(Color.black);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.setEditable(false);

			JScrollPane scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize(new Dimension(190, 1000));
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			scrollPane.setLocation(10, 20);
			scrollPane.setBounds(5,0,195,600);
			
			scrollPane.setOpaque(true);
			add(scrollPane);
			//add(textArea);
		}
	}
	
	public static class MazeFrame extends JFrame
	{

		//int x=10,y=10;
		public MazeFrame()
		{
			super("Hello Maze");
			setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//setSize(800,622);

			
			all=new JPanel(null);
			//new
			p1 = new ButtonPanel();
			p2 = new MazePanel();
			p3 = new StackPanel();
			p4 = new TextPanel();
			
			//bounds
			p1.setBounds(0,0,200,600);
			p2.setBounds(200,0,400,400);
			p3.setBounds(200,400,400,200);
			p4.setBounds(600,0,200,600);
			all.setPreferredSize(new Dimension(800,600));
			//.setSize(800,600);
			
			
			//background color
			p1.setOpaque(true);
			p1.setBackground(Color.black);
			p2.setOpaque(true);
			p2.setBackground(Color.white);
			p3.setOpaque(true);
			p3.setBackground(new Color(238,241,251));
			p4.setOpaque(true);
			p4.setBackground(new Color(197,198,199));
			
			all.add(p1);
			all.add(p2);
			all.add(p3);
			all.add(p4);
			
			
			add(all, BorderLayout.CENTER);
			pack();
			
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
		}
		
	}

	public static void main (String[] args)throws Exception

	{
		//创建迷宫
		maze=new MazeModule();

		int a=20,b=20;
		if(maze.createRandomMaze(a, b))
			System.out.println(maze.OutputPath(maze.allpath));
		else System.out.println("Can't create path");
		
		for(int i=0;i<=a+1;i++){
			for(int j=0;j<=b+1;j++){
				System.out.print(maze.arr[i][j]+"\t");
			}
			System.out.println();
		}
		
		if(maze.FindPath())
			System.out.println(maze.OutputPath(maze.path));
		else System.out.println("Can't find path");
		
		
		for(int i=0;i<=a+1;i++){
			for(int j=0;j<=b+1;j++){
				System.out.print(maze.maze_arr[i][j]+"\t");
			}
			System.out.println();
		}
		maze.ResetPath();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){	
				mazeFrame=new MazeFrame();
				System.out.println(mazeFrame.getContentPane().getSize());
				System.out.println(mazeFrame.getSize());
			}
		});
		
		
	}
	
	
	
}
