package com.example.dance_control.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class DownLoad {
	
	//用于保存下载的记录
	private SharedPreferences DownLoadDate;
	//文件的的路径
	private String urlpath;
	//所有的线程//这里设置为3个;
	private SaveThread[] Threads = new SaveThread[3];
	//主线程的消息循环用于更新ui界面
	private Handler handler;
	//一个标示位用于表示是否已经暂停.
	private boolean isStop = false;
	//一个标示表示是否完成下载
	private boolean isFinish = false;
	//上下文
	private Context context;
/*	//用这个标示表示要保存的音乐下载的pos
	private int Symbol;*/
	//当前下载的音乐的id
	private String musicid;
	//音乐的名字
	private String musicname;
	//保存的文件
	private File Savefile; 
	//文件的大小
	private int filesize;//完全足够大小
	//每一个线程下载的块的大小
	private int block;
	//保存数据库的类
	private DownLoad_Data data;
	//保存这个音乐的编号,当出现错误时将编号提交给ui
	private Integer Postion;
	
	private Integer DownLoadThreadSize = 1;
	public DownLoad(Handler handler,String musicid,String name,Context context,Integer pos)
	{
		this.handler = handler;
		this.musicid = musicid;
		this.musicname = name;
		this.context = context ;
		this.Postion = pos;
		data = new DownLoad_Data(context);
	}
	
	//设置下载的路径
	public void Set_Path(String path)
	{
		this.urlpath = path;
	}
	
	/**
	 * @author nieyihe
	 * @ 设置停止下载,继续下载也必须设置Stop为false
	 * */
	public void SetStop()
	{
		this.isStop = true;
	}
	
	/**
	 * @author nieyihe
	 * @ 开始下载
	 * */
	public void Start_DownLoad() throws IOException
	{
		new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				isStop = false;//设置为不暂停
				
				try {
					if(Ini_DownLoad()) 
						Start_On();
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}.start();
		
	}
	
	
	/**
	 * @throws IOException 
	 * @ 初始化 每一个线程要下载的文件的大小;
	 * */
	public boolean Ini_DownLoad() throws IOException
	{
		//检查是否存在sd卡 若不存在就退出下载.
		if(!Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(context, "内存卡不存在,请安装内存卡在点击下载.", Toast.LENGTH_LONG).show();
			return false;
		}
			
		File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
			if(!file.exists()) 
				file.mkdirs();
		//创建music的目录在根目录下
		Savefile = new File(file,musicname);
	try
	{	
		URL url = new URL(this.urlpath);//创建网络url
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//打开网络链接.
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language","zh-CN");
		conn.setRequestProperty("Referer", urlpath);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
        conn.connect();
		//设置http首部 并且实时连接上
		
        if (conn.getResponseCode()==200)
        {
            this.filesize = conn.getContentLength();//根据响应获取文件大小
           
            if (this.filesize <= 0) 
            {
            	Toast.makeText(context, "此文件不存在",Toast.LENGTH_LONG).show();
            	return false;
            }
            
            //获取的文件大小小于零,就退出下载
            
            this.block = (this.filesize % this.Threads.length) == 0? this.filesize / this.Threads.length : this.filesize / this.Threads.length + 1;
            //获取每一个块的大小
        }
        else
        {
        	Toast.makeText(context, "链接错误，请重试.",Toast.LENGTH_LONG).show();
        }
        
	}catch(IOException e)
	{
		Toast.makeText(context, "链接有问题", Toast.LENGTH_SHORT).show();	
	}
		
		return true;
	}
	
	
	/**
	 * @author nieyihe
	 * @throws IOException 
	 * @ 开始下载
	 * 
	 * */
	public void Start_On() throws IOException
	{
		URL UrlPath = null;
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.Savefile, "rwd");
			if(filesize > 0) 
				randOut.setLength(filesize);
			randOut.close();
			//设置文件长度.
			
			UrlPath = new URL(this.urlpath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SendMaxLength(filesize);
		//发送bar的长度消息
		for(int i = 0 ;i<3;i++)
		{
			data.Save_Thread_Info(musicid,i,0,filesize);
			//初始化每一个线程下载的长度都为0--保存到数据库里面
			Threads[i] = new SaveThread(block,i,UrlPath,Savefile,context,0);
			//初始化每一个线程
			Threads[i].setPriority(7);
			//设置优先级
			Threads[i].start();
			//线程开始运行,进行下载.
		}
		
		new ListenerThread(Threads).start();
		//监听所有的线程的是否都下载完成
	}
	
	/**
	 *@author nieyihe
	 *@ 设置继续下载的信息 
	 *@ 此处为点击继续下载
	 *@ 当点击暂停后 或者 下次登录后
	 *@param threadinfo 表示此音乐的线程下载的状态
	 * @throws IOException 
	 * */
	public void Go_On(final HashMap<String,String> threadinfo) throws IOException
	{
		new Thread()
		{
			@Override
			public void run()
			{
		
					isStop = false;
					//设置为不暂停
			
					try {
						if(Ini_DownLoad())
						{
							Go_On_Download(threadinfo);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
			}
		}.start();
	}
	
	
	/**
	 * @author nieyihe
	 * @ 设置好每一个线程的状态.
	 * */
	public void Go_On_Download(HashMap<String,String> threadinfo) throws IOException
	{
		URL UrlPath = null;
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.Savefile, "rwd");
			if(filesize > 0) 
				randOut.setLength(filesize);
			randOut.close();
			//设置文件长度.
			
			UrlPath = new URL(this.urlpath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0 ;i < 3 ; i++)
		{
			String str = threadinfo.get(i+"");
			if(str.equals("") || str == null)  continue;//假如为空,则直接进入下一个循环中
			
			Integer downloadlength = Integer.parseInt(str);
			//初始化每一个线程下载的长度--通过Hashmap
			Threads[i] = new SaveThread(block,i,UrlPath,Savefile,context,downloadlength);
			//初始化每一个线程
			Threads[i].setPriority(7);
			//设置优先级
			Threads[i].start();
			//线程开始运行,进行下载.
		}
		
		new ListenerThread(Threads).start();
		//监听所有的线程的是否都下载完成
	}
	
	
	/**
	 * @author nieyihe
	 * @作用 设置下载
	 * */
	private class SaveThread extends Thread
	{
		private int block;//每个下载的块
		private URL path;//下载的URL路径
		private int Id = -1;//每一个线程的id
		private File savefile;//本地保存的文件
		private Context context;//上下文
		private int DownLoadLength;//已经下载的长度
		private boolean finish;//保存当前线程是否完成下载
		
		public SaveThread(int block,int id,URL path,File file,Context context,int DownLoadLength)
		{
			this.block = block;
			this.Id = id;
			this.path = path;
			this.savefile = file;
			this.context = context;
			this.DownLoadLength = DownLoadLength;
		}
		
		/**
		 * @author nieyihe
		 * @ 获取这个
		 * */
		public boolean Get_Finish()
		{
			return finish;
		}
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			super.run();
			
			
			//并且将下载到的记录持续更新到ui界面里面
			//将下载长度记录保存到内存卡上
			//检测到暂停的就退出下载
			
			if(this.DownLoadLength < block)//当没有下载完毕就继续下载。
			{
				try {
					
				HttpURLConnection http = null;
				
				http = (HttpURLConnection) path.openConnection();
                http.setConnectTimeout(5 * 1000);
                http.setRequestMethod("GET");
                http.setRequestProperty("Accept", "*/*");
                http.setRequestProperty("Accept-Language", "zh-CN");
                /*http.setRequestProperty("Referer", path.toString()); */
                http.setRequestProperty("Charset", "UTF-8");
                
                int startPos = block * Id + DownLoadLength;//开始位置
                int endPos = (block * (Id + 1)) - 1;//结束位置
                if(endPos > filesize)
                	endPos = filesize - 1;
                http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围
                http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                http.setRequestProperty("Connection", "Keep-Alive");
                
                
                InputStream in = http.getInputStream();
                
                byte buffer[] = new byte[1024];
                int offset = 0;
                
                RandomAccessFile threadfile = new RandomAccessFile(this.savefile, "rwd");
                threadfile.seek(startPos);
                
                while((offset = in.read(buffer)) != -1)
                {
                	
                	threadfile.write(buffer, 0 , offset);
                	//向本地文件写入数据
                	DownLoadLength += offset;
                	synchronized(data)
                	{
	                	//增加已经下载的量
	                	data.Update_Thread_Info(musicid, Id+"", DownLoadLength);
                	}
                	//将每一个线程的下载的状态更新保存到数据库里
                	
                	SendMessage(musicid, offset + "");
                	
                	//此函数先发送一个MusicUpdate标示哪一个music更新
                	//再发送一个offset来设置更新的具体值
                	//offset代表着增加的量
                	
                	if(isStop){
                			threadfile.close();
                         	in.close();
                			return ;
                		}
                	//假设暂停下载的话就退出下载
                	
                }
                
                threadfile.close();
                in.close();
                
                this.finish = true;
    			//当完成度为100%时则设置此标示为finish
                
                //设置本线程已经完毕
				} catch (IOException e) {
					// TODO Auto-generated catch block
					isStop = true;
					SendStop();
					e.printStackTrace();
				}
			}
			
			
			
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @ 此处为监听线程用于监听当前下载的所有线程下载是否都完成
	 * */
	private class ListenerThread extends Thread
	{

		private SaveThread[] thread;
		
		public ListenerThread(SaveThread[] thread)
		{
			this.thread = thread;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(true)
			{
					if(thread[0].Get_Finish() && thread[1].Get_Finish() && thread[2].Get_Finish())
					{
						isFinish = true;
						//设置完成标志
						//完成标示暂且不用

						SendFinishMessage();
						//发送完成的消息
						synchronized(data)
	                	{
							data.Delete_Music_Info(musicid);
	                	}
						//删除数据库信息
						break;
					}

					if(isStop) break;//假设暂停下载就退出监听
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
	}
	
	/**
	 * @author nieyihe
	 * @ 发送消息刷新ui界面
	 * @param key 音乐名字
	 * @param values 更新增加的数值
	 * */
	public synchronized void SendMessage(String key,String values)
	{
		
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("MusicUpdate", key);
		b.putString(key,values);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/**
	 * @author nieyihe
	 * @ 发送消息刷新ui界面
	 * */
	public synchronized void SendFinishMessage()
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("MusicFinish", musicname);
		b.putString(musicname, musicid);
		msg.setData(b);
		handler.sendMessage(msg);
    	
	}
	
	
	/***
	 * @author nieyihe
	 * 当第一次下载时,设置进度条最大值
	 * */
	public synchronized void SendMaxLength(Integer max)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("BarMax", musicid);//设置那个音乐的bar
		b.putInt(musicid, max);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/***
	 * @author nieyihe
	 * 设置异常暂停下载
	 * */
	public synchronized void SendStop()
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("DownloadStop", musicid);
		b.putString(musicid,Postion +"");
		msg.setData(b);
		handler.sendMessage(msg);
	}
}
