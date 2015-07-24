package com.example.dance_control.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.dance_control.R;

public class OptionDialog extends Dialog {

	private BtuClicking leftBtuClicking;
	private BtuClicking rightBtuClicking;
	private final int id = R.layout.tip_message_dialog;
	private View leftView;
	private View rightView;
	private View middleShowView;
	private View headHintView;
	
	public OptionDialog(Context context)
	{
		super(context, R.style.Item_Dialog);
	}

	/**
	 * @author nieyihe
	 * 设置第一个监听器
	 * */
	public void SetFirstOnlick(BtuClicking click)
	{
		leftBtuClicking = click;
	}
	
	/**
	 * @author nieyihe
	 * 设置第二个监听器
	 * */
	public void SetSecondOnclick(BtuClicking click)
	{
		rightBtuClicking = click;
	}
	
	/**
	 * @author nieyihe
	 * 创建Dialog
	 * */
	public void PrepareDialog()
	{
		this.setContentView(id);
		leftView = this.findViewById(R.id.tip_message_dialog_sure);
		rightView = this.findViewById(R.id.tip_message_dialog_cancel);
		middleShowView = this.findViewById(R.id.tip_message_dialog_message);
		View head = this.findViewById(R.id.tip_message_dialog_head);
		headHintView = head.findViewById(R.id.tip_dialog_head_tiptext);
		leftView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(leftBtuClicking != null)
					leftBtuClicking.OnClick();
			}
		});
		
		rightView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(rightBtuClicking != null)
					 rightBtuClicking.OnClick();
			}
		});
	}
	
	/**
	 * @author nieyihe
	 * 是否可以点击关闭
	 * */
	public void SetCanceledOnTouchOutside(boolean is)
	{
		this.setCanceledOnTouchOutside(is);
	}
	
	/**
	 * @author nieyihe
	 * 获取左边的按钮
	 * */
	public View GetLeftView()
	{
		return leftView;
	}
	
	/**
	 * @author nieyihe
	 * 获取右边的按钮
	 * */
	public View GetRightView()
	{
		return rightView;
	}
	
	/**
	 * @author nieyihe
	 * 设置中间显示的内容
	 * */
	public void SetMiddleShow(String str)
	{
		((TextView)middleShowView).setText(str);
	}
	
	/**
	 * @author nieyihe
	 * 设置中间的提示信息
	 * */
	public void SetHintTextShow(String str)
	{
		((TextView)headHintView).setText(str);
	}
	
	/**
	 * @author nieyihe
	 * 显示Dialog
	 * */
	public void Show()
	{
		this.show();
	}
	
	/**
	 * @author nieyihe
	 * 不显示Dialog
	 * */
	public void DisMiss()
	{
		this.dismiss();
	}
}
