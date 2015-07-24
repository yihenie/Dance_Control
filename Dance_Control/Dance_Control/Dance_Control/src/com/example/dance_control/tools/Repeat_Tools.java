package com.example.dance_control.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.dance_control.R;
import com.example.dance_control.fragment.Dance_My_Movie;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Repeat_Tools {

	private ArrayList<Integer> ids;
	private HashMap<Integer,String> id_path;//保存当前id所对应的图片路径
	private EditText text ;
	private GridView grid;
	private ImageButton exit;
	private ImageButton ali;
	private ImageButton bear;
	private ImageButton emotion;
	private LinearLayout emotion_parent;
	
	public Repeat_Tools(EditText text,GridView grid,ImageButton exit,ImageButton ali,ImageButton bear,ImageButton emotion,LinearLayout emotion_parent)
	{
		this.text = text;
		this.grid = grid;
		this.exit = exit;
		this.ali = ali;
		this.bear = bear;
		this.emotion = emotion;
		this.emotion_parent = emotion_parent;
	}
	
	public Repeat_Tools(){}
	
	private SpannableString Get_SpanString(String text)
	{
		return new SpannableString(text);
	}
	
	private void  Pattern_Repeat(Context context,SpannableString word,Pattern patter,int start) throws NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		Matcher match = patter.matcher(word);
		while(match.find())
		{
			
			String key = match.group();//获取匹配结果
			if (match.start() < start) {
                continue;
            }
			
			Field field = R.drawable.class.getDeclaredField(key);
			
			int real_id = Integer.parseInt(field.get(null).toString());
			
			if(real_id != 0)
				{
					Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), real_id);
					//通过id得到图片
					
					ImageSpan image = new ImageSpan(context,bitmap);
					int end = match.start() + key.length();
					
					word.setSpan(image, match.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					if(end < word.length())
						{
						//如果没有到达尽头,就继续匹配直至结束.
							Pattern_Repeat(context,word,patter,end);
							
						}
					break;
					
				}
		}
		
	}
	
	public SpannableString deal_Repeat(Context context,String repeat_word) throws NumberFormatException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
	{
		SpannableString word = Get_SpanString(repeat_word);
		String S_ali = "ali_05[1-9]|ali_06[0-9]|ali_070";
		String S_bear = "b_[0-1][0-9]";
		String S_emotion = "image_emoticon0[1-9]|image_emoticon[1-4][0-9]|image_emoticon50";
		Pattern patter_ali = Pattern.compile(S_ali,Pattern.CASE_INSENSITIVE);
		Pattern patter_bear = Pattern.compile(S_bear,Pattern.CASE_INSENSITIVE);
		Pattern patter_emotion = Pattern.compile(S_emotion,Pattern.CASE_INSENSITIVE);
		
		Pattern_Repeat(context,word,patter_ali,0);
		//先通过函数匹配ali,得到匹配完的字符串,再去匹配bear,再去匹配emotion;
		Pattern_Repeat(context,word,patter_bear,0);
		Pattern_Repeat(context,word,patter_emotion,0);
		
		return word;
		
	}
	
	public void Gray_View_Show(final Context context) throws NumberFormatException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
	{
		//Dialog dialog = new Dialog(context);
		ids = Get_Ali();
		Myadapter ada = new Myadapter(context);
		
		grid.setAdapter(ada);
		grid.setNumColumns(6);
		grid.setBackgroundColor(Color.rgb(255, 255, 255));
		grid.setHorizontalSpacing(1);
		grid.setVerticalSpacing(1);
		grid.setGravity(Gravity.CENTER);
		
		grid.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), ids.get(pos));
				ImageSpan span = new ImageSpan(context,bitmap);
				//通过位置与ids列表来找到对应的id,再通过id与id_path来找到path;
				SpannableString str = new SpannableString(id_path.get(ids.get(pos)));
				str.setSpan(span, 0, id_path.get(ids.get(pos)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.append(str);
			}
			
		});
		
		/*dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);//设置dialog在不点击退出时不消失
*/		exit.setOnClickListener(new Onclick(emotion_parent,null));
		
		//设置监听器
		ali.setOnClickListener(new Onclick(null,ada));
		bear.setOnClickListener(new Onclick(null,ada));
		emotion.setOnClickListener(new Onclick(null,ada));
		
		
	}
	
	
	private class Onclick implements View.OnClickListener
	{
		LinearLayout parent;
		BaseAdapter ada;
		public Onclick(LinearLayout parent,Myadapter ada)
		{
			this.parent = parent;
			 this.ada = ada;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.emotion_select_exit:
				{
					parent.setVisibility(View.GONE);
					break;
				}
				case R.id.emotion_select_ali:
				{
					try {
						ids = Get_Ali();
						ada.notifyDataSetChanged();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				
				case R.id.emotion_select_bear:
				{
					try {
						Log.v("emotion", "点击");
						
						ids = Get_Bear();
						ada.notifyDataSetChanged();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				
				case R.id.emotion_select_emotion:
				{
					try {
						
						ids = Get_Emotion();
						ada.notifyDataSetChanged();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
	}
	
	
	private ArrayList<Integer> Get_Ali() throws NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Integer> ali = new ArrayList<Integer>();
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		for(int i = 51; i < 71;i ++)
		{
			Field field = R.drawable.class.getDeclaredField("ali_0" + i); 
			int id = Integer.parseInt(field.get(null).toString());
			ali.add(id);
			map.put(id, "ali_0" +i);
		}
		this.id_path = map;
		return ali;
	}
	
	private ArrayList<Integer> Get_Bear() throws NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Integer> bear = new ArrayList<Integer>();
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		for(int i = 0; i < 20;i ++)
		{
			if(i<10)
			{
				Field field = R.drawable.class.getDeclaredField("b_0" + i); 
				int id = Integer.parseInt(field.get(null).toString());
				bear.add(id);
				map.put(id, "b_0" + i);
			}
			else if(i>9)
			{
				Field field = R.drawable.class.getDeclaredField("b_" + i); 
				int id = Integer.parseInt(field.get(null).toString());
				bear.add(id);
				map.put(id, "b_" + i);
			}
		}
		this.id_path = map;
		return bear;
	} 
	
	private ArrayList<Integer> Get_Emotion () throws NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Integer> emotions = new ArrayList<Integer>();
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		for(int i = 1;i <51 ;i++)
		{
			if(i<10)
			{
				Field field = R.drawable.class.getDeclaredField("image_emoticon0" + i);
				int id = Integer.parseInt(field.get(null).toString());
				emotions.add(id);
				map.put(id, "image_emoticon0" + i);
			}
			else if(i>=10)
			{
				Field field = R.drawable.class.getDeclaredField("image_emoticon" + i);
				int id = Integer.parseInt(field.get(null).toString());
				emotions.add(id);
				map.put(id, "image_emoticon" + i);
			}
		}
		this.id_path = map;
		return emotions;
	}
	
	private class Myadapter extends BaseAdapter
	{

		private Context context;
		private LayoutInflater in;
		
		public Myadapter(Context context)
		{
			this.context = context;
			in = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ids.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int pos, View contentview, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Bind bind ;
			if(contentview == null)
			{
				contentview = in.inflate(R.layout.grid_emotion, null);
				bind = new Bind();
				bind.emotion = (ImageView) contentview.findViewById(R.id.grid_emotion_item);
				contentview.setTag(bind);
			}
			else
			{
				bind = (Bind) contentview.getTag();
			}
			
			Integer id = ids.get(pos);
			
			bind.emotion.setImageResource(id);
			
			return contentview;
		}
		
	}
	
	private class Bind
	{
		ImageView emotion;
	}
	 
}
