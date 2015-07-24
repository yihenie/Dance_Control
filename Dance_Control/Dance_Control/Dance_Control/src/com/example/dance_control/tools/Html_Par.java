package com.example.dance_control.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import android.util.Log;

public class Html_Par {

	private final String YeWuSky = "http://www.kagneen.com";
	public  ArrayList<String> Hrefs = new  ArrayList<String>();
	public ArrayList<String> Names = new  ArrayList<String>();

	public void Praser_Html() throws ParserException, MalformedURLException, IOException
	{
		HttpURLConnection open = (HttpURLConnection)(new URL(YeWuSky)).openConnection();
		Parser parser = new Parser(open);
		parser.setEncoding("gbk");
		
		NodeFilter filter = new StringFilter("本周视频点播榜");//,本站推荐视频
		NodeList nodes = parser.extractAllNodesThatMatch(filter);
	
			if(nodes != null)
			{
	                Node textnode = (Node) nodes.elementAt(0);
	                Node p = textnode.getParent();//.getParent().getParent();
	                Node TuiJian = p.getNextSibling().getNextSibling();//
	                //获取当前
	                Praser_Tr(TuiJian.toHtml());
	        } 
	}
	
	private void Praser_Tr(String html) throws ParserException, UnsupportedEncodingException
	{
		Parser pra = Parser.createParser(html, "gbk");
		NodeFilter innerFilter  = new TagNameFilter("tr");
		NodeList nodes = pra.extractAllNodesThatMatch(innerFilter);
		for(int i = 0; i < nodes.size(); i ++)
		{
			Node child_child = null;
			Node node = nodes.elementAt(i);
			//print(node.toHtml());
			Node child = node.getFirstChild();
			if(child == null)
				break;
			else
			{
				child_child = child.getFirstChild();
			}
			//print(child_child.toHtml());
			Praser_Href_Title(child_child.toHtml());
		}
	}
	
	
	
	private void Praser_Href_Title(String html) throws ParserException, UnsupportedEncodingException
	{
		Parser pra = Parser.createParser(html, "gbk");
		NodeFilter innerFilter = new HasAttributeFilter("href");
		NodeList nodes = pra.extractAllNodesThatMatch(innerFilter);
		LinkTag node = (LinkTag)nodes.elementAt(0);
		
		Hrefs.add(YeWuSky + node.getAttribute("href"));
		Names.add(node.getAttribute("title").replaceAll("&nbsp", "").replaceAll(";", ""));
	}
	
	public String Praser_Movie(String url) throws ParserException, MalformedURLException, IOException
	{
		try
		{
			
		Parser Firstpar = new Parser((HttpURLConnection)(new URL(url)).openConnection());
		Firstpar.setEncoding("gbk");
		//NodeFilter innerFilter = new HasAttributeFilter("id","movie_player");
		NodeFilter innerFilter  = new TagNameFilter("embed");
		NodeList Firstnodes = Firstpar.extractAllNodesThatMatch(innerFilter);
		
		if(Firstnodes == null)
			return "";

		String word = Firstnodes.elementAt(0).toHtml();
		
		String result = (String) word.subSequence(word.indexOf("src=") + 4, word.lastIndexOf("swf") + 3);
		
		return result;
		
		}catch(EncodingChangeException s)
		{
			return "";
		}
	}

}
