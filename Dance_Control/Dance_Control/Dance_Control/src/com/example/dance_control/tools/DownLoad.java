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
	
	//���ڱ������صļ�¼
	private SharedPreferences DownLoadDate;
	//�ļ��ĵ�·��
	private String urlpath;
	//���е��߳�//��������Ϊ3��;
	private SaveThread[] Threads = new SaveThread[3];
	//���̵߳���Ϣѭ�����ڸ���ui����
	private Handler handler;
	//һ����ʾλ���ڱ�ʾ�Ƿ��Ѿ���ͣ.
	private boolean isStop = false;
	//һ����ʾ��ʾ�Ƿ��������
	private boolean isFinish = false;
	//������
	private Context context;
/*	//�������ʾ��ʾҪ������������ص�pos
	private int Symbol;*/
	//��ǰ���ص����ֵ�id
	private String musicid;
	//���ֵ�����
	private String musicname;
	//������ļ�
	private File Savefile; 
	//�ļ��Ĵ�С
	private int filesize;//��ȫ�㹻��С
	//ÿһ���߳����صĿ�Ĵ�С
	private int block;
	//�������ݿ����
	private DownLoad_Data data;
	//����������ֵı��,�����ִ���ʱ������ύ��ui
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
	
	//�������ص�·��
	public void Set_Path(String path)
	{
		this.urlpath = path;
	}
	
	/**
	 * @author nieyihe
	 * @ ����ֹͣ����,��������Ҳ��������StopΪfalse
	 * */
	public void SetStop()
	{
		this.isStop = true;
	}
	
	/**
	 * @author nieyihe
	 * @ ��ʼ����
	 * */
	public void Start_DownLoad() throws IOException
	{
		new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				isStop = false;//����Ϊ����ͣ
				
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
	 * @ ��ʼ�� ÿһ���߳�Ҫ���ص��ļ��Ĵ�С;
	 * */
	public boolean Ini_DownLoad() throws IOException
	{
		//����Ƿ����sd�� �������ھ��˳�����.
		if(!Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(context, "�ڴ濨������,�밲װ�ڴ濨�ڵ������.", Toast.LENGTH_LONG).show();
			return false;
		}
			
		File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
			if(!file.exists()) 
				file.mkdirs();
		//����music��Ŀ¼�ڸ�Ŀ¼��
		Savefile = new File(file,musicname);
	try
	{	
		URL url = new URL(this.urlpath);//��������url
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();//����������.
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language","zh-CN");
		conn.setRequestProperty("Referer", urlpath);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Connection", "Keep-Alive");
        conn.connect();
		//����http�ײ� ����ʵʱ������
		
        if (conn.getResponseCode()==200)
        {
            this.filesize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
           
            if (this.filesize <= 0) 
            {
            	Toast.makeText(context, "���ļ�������",Toast.LENGTH_LONG).show();
            	return false;
            }
            
            //��ȡ���ļ���СС����,���˳�����
            
            this.block = (this.filesize % this.Threads.length) == 0? this.filesize / this.Threads.length : this.filesize / this.Threads.length + 1;
            //��ȡÿһ����Ĵ�С
        }
        else
        {
        	Toast.makeText(context, "���Ӵ���������.",Toast.LENGTH_LONG).show();
        }
        
	}catch(IOException e)
	{
		Toast.makeText(context, "����������", Toast.LENGTH_SHORT).show();	
	}
		
		return true;
	}
	
	
	/**
	 * @author nieyihe
	 * @throws IOException 
	 * @ ��ʼ����
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
			//�����ļ�����.
			
			UrlPath = new URL(this.urlpath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SendMaxLength(filesize);
		//����bar�ĳ�����Ϣ
		for(int i = 0 ;i<3;i++)
		{
			data.Save_Thread_Info(musicid,i,0,filesize);
			//��ʼ��ÿһ���߳����صĳ��ȶ�Ϊ0--���浽���ݿ�����
			Threads[i] = new SaveThread(block,i,UrlPath,Savefile,context,0);
			//��ʼ��ÿһ���߳�
			Threads[i].setPriority(7);
			//�������ȼ�
			Threads[i].start();
			//�߳̿�ʼ����,��������.
		}
		
		new ListenerThread(Threads).start();
		//�������е��̵߳��Ƿ��������
	}
	
	/**
	 *@author nieyihe
	 *@ ���ü������ص���Ϣ 
	 *@ �˴�Ϊ�����������
	 *@ �������ͣ�� ���� �´ε�¼��
	 *@param threadinfo ��ʾ�����ֵ��߳����ص�״̬
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
					//����Ϊ����ͣ
			
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
	 * @ ���ú�ÿһ���̵߳�״̬.
	 * */
	public void Go_On_Download(HashMap<String,String> threadinfo) throws IOException
	{
		URL UrlPath = null;
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.Savefile, "rwd");
			if(filesize > 0) 
				randOut.setLength(filesize);
			randOut.close();
			//�����ļ�����.
			
			UrlPath = new URL(this.urlpath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0 ;i < 3 ; i++)
		{
			String str = threadinfo.get(i+"");
			if(str.equals("") || str == null)  continue;//����Ϊ��,��ֱ�ӽ�����һ��ѭ����
			
			Integer downloadlength = Integer.parseInt(str);
			//��ʼ��ÿһ���߳����صĳ���--ͨ��Hashmap
			Threads[i] = new SaveThread(block,i,UrlPath,Savefile,context,downloadlength);
			//��ʼ��ÿһ���߳�
			Threads[i].setPriority(7);
			//�������ȼ�
			Threads[i].start();
			//�߳̿�ʼ����,��������.
		}
		
		new ListenerThread(Threads).start();
		//�������е��̵߳��Ƿ��������
	}
	
	
	/**
	 * @author nieyihe
	 * @���� ��������
	 * */
	private class SaveThread extends Thread
	{
		private int block;//ÿ�����صĿ�
		private URL path;//���ص�URL·��
		private int Id = -1;//ÿһ���̵߳�id
		private File savefile;//���ر�����ļ�
		private Context context;//������
		private int DownLoadLength;//�Ѿ����صĳ���
		private boolean finish;//���浱ǰ�߳��Ƿ��������
		
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
		 * @ ��ȡ���
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
			
			
			//���ҽ����ص��ļ�¼�������µ�ui��������
			//�����س��ȼ�¼���浽�ڴ濨��
			//��⵽��ͣ�ľ��˳�����
			
			if(this.DownLoadLength < block)//��û��������Ͼͼ������ء�
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
                
                int startPos = block * Id + DownLoadLength;//��ʼλ��
                int endPos = (block * (Id + 1)) - 1;//����λ��
                if(endPos > filesize)
                	endPos = filesize - 1;
                http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//���û�ȡʵ�����ݵķ�Χ
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
                	//�򱾵��ļ�д������
                	DownLoadLength += offset;
                	synchronized(data)
                	{
	                	//�����Ѿ����ص���
	                	data.Update_Thread_Info(musicid, Id+"", DownLoadLength);
                	}
                	//��ÿһ���̵߳����ص�״̬���±��浽���ݿ���
                	
                	SendMessage(musicid, offset + "");
                	
                	//�˺����ȷ���һ��MusicUpdate��ʾ��һ��music����
                	//�ٷ���һ��offset�����ø��µľ���ֵ
                	//offset���������ӵ���
                	
                	if(isStop){
                			threadfile.close();
                         	in.close();
                			return ;
                		}
                	//������ͣ���صĻ����˳�����
                	
                }
                
                threadfile.close();
                in.close();
                
                this.finish = true;
    			//����ɶ�Ϊ100%ʱ�����ô˱�ʾΪfinish
                
                //���ñ��߳��Ѿ����
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
	 * @ �˴�Ϊ�����߳����ڼ�����ǰ���ص������߳������Ƿ����
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
						//������ɱ�־
						//��ɱ�ʾ���Ҳ���

						SendFinishMessage();
						//������ɵ���Ϣ
						synchronized(data)
	                	{
							data.Delete_Music_Info(musicid);
	                	}
						//ɾ�����ݿ���Ϣ
						break;
					}

					if(isStop) break;//������ͣ���ؾ��˳�����
					
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
	 * @ ������Ϣˢ��ui����
	 * @param key ��������
	 * @param values �������ӵ���ֵ
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
	 * @ ������Ϣˢ��ui����
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
	 * ����һ������ʱ,���ý��������ֵ
	 * */
	public synchronized void SendMaxLength(Integer max)
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("BarMax", musicid);//�����Ǹ����ֵ�bar
		b.putInt(musicid, max);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	/***
	 * @author nieyihe
	 * �����쳣��ͣ����
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
