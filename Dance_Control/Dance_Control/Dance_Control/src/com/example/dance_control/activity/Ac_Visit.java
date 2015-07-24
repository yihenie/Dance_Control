package com.example.dance_control.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.example.dance_control.R;
import com.example.dance_control.bmob_table.regiment_member;
import com.example.dance_control.utils.Act_MyRigment;
public class Ac_Visit extends Activity {
	
	private EditText member_host;
	private EditText member_password;
	private Button insert;
	private Button exit;
	private Button pass;
	private String regiment_id = "";
	private String member_id = "";
	private String power = "visiter";
	private ProgressDialog waiting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.visit);
		Get_Bundle();
		member_host = (EditText)this.findViewById(R.id.visit_edit_host);
		member_password = (EditText)this.findViewById(R.id.visit_edit_password);
		insert = (Button)this.findViewById(R.id.visit_button_insert);
		exit = (Button)this.findViewById(R.id.visit_button_exit);
		pass = (Button)this.findViewById(R.id.visit_button_pass);
		
		insert.setOnClickListener(new Onclick());
		exit.setOnClickListener(new Onclick());
		pass.setOnClickListener(new Onclick());
	}
	
	public void Get_Bundle()
	{
		Bundle s = this.getIntent().getExtras();
		regiment_id = s.getString("regiment_id");
	}
	
	public class Onclick implements View.OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			switch(arg0.getId())
			{
				case R.id.visit_button_insert:
				{
					// TODO Auto-generated method stub
					if(member_host.getText().toString().trim().equals(""))
					{
						Toast.makeText(Ac_Visit.this, "«Î ‰»Î’À∫≈!",Toast.LENGTH_LONG).show();
						return ;
					}
					if(member_password.getText().toString().trim().equals(""))
					{
						Toast.makeText(Ac_Visit.this, "«Î ‰»Î√‹¬Î!" , Toast.LENGTH_LONG).show();
						return ;
					}
					if(waiting == null)
						waiting = ProgressDialog.show(Ac_Visit.this,"","µ«¬º÷–",true,false);
					else if(waiting != null)
					{
						waiting.show();
					}
					Ac_Visit.this.Insert();
				
					break;
				}
				
				case R.id.visit_button_exit:
				{
					Ac_Visit.this.finish();
					break;
				}
				case R.id.visit_button_pass:
				{
					Intent i = new Intent();
					i.setClass(Ac_Visit.this, Act_MyRigment.class);
					Bundle b = new Bundle();
					b.putString("member_id", member_id);
					b.putString("regiment_id", regiment_id);
					b.putString("member_power",power);
					i.putExtras(b);
					Ac_Visit.this.startActivity(i);
					finish();
					break;
				}
			}
		
		}
	}
	/**
	 * @author nieyihe
	 * @◊˜”√ £∫—È÷§µ«¬º
	 * */
	public void Insert()
	{
		String hostString = member_host.getText().toString().trim();
		String passwordString = member_password.getText().toString().trim();
		
		BmobQuery<regiment_member> query_1 = new BmobQuery<regiment_member>();
		BmobQuery<regiment_member> query_2 = new BmobQuery<regiment_member>();
		BmobQuery<regiment_member> query_3 = new BmobQuery<regiment_member>();
		query_1.addWhereEqualTo("member_host", hostString);
		query_2.addWhereEqualTo("member_password", passwordString);
		query_3.addWhereEqualTo("regiment_id", Ac_Visit.this.regiment_id);
		
		List<BmobQuery<regiment_member>> queries = new ArrayList<BmobQuery<regiment_member>>();
		queries.add(query_1);
		queries.add(query_2);
		queries.add(query_3);
		
		BmobQuery<regiment_member> query = new BmobQuery<regiment_member>();
		query.and(queries);
		query.findObjects(Ac_Visit.this, new FindListener<regiment_member>()
				{

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(Ac_Visit.this, "∂œÕ¯¡À?",Toast.LENGTH_SHORT).show();
						waiting.dismiss();
					}

					@Override
					public void onSuccess(List<regiment_member> arg0) {
						// TODO Auto-generated method stub
						for(regiment_member temp : arg0)
						{
							String id = temp.getObjectId();
							member_id = id == null ? "": id;
							power = temp.member_power;
						}
						
						if(waiting != null) 
							waiting.dismiss();
						
						if(member_id.equals("")) 
						{
							Toast.makeText(Ac_Visit.this, "’À∫≈ªÚ’ﬂ√‹¬Î¥ÌŒÛ", Toast.LENGTH_LONG).show();
							return ;
						}
						
						Intent i = new Intent();
						i.setClass(Ac_Visit.this, Act_MyRigment.class);
						Bundle b = new Bundle();
						b.putString("member_id", member_id);
						b.putString("regiment_id", regiment_id);
						b.putString("member_power",power);
						i.putExtras(b);
						Ac_Visit.this.startActivity(i);
						finish();
					}
			
				});
	}
}
