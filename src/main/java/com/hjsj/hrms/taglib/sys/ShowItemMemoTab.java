package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.PubFunc;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ShowItemMemoTab extends BodyTagSupport {

	private String setname="";
	private String itemtype="";
	private String showtext="";
	private String text="";
	private String tiptext="";
	public String getTiptext() {
		return tiptext;
	}
	public void setTiptext(String tiptext) {
		this.tiptext = tiptext;
	}
	public int doEndTag() throws JspException 
	{
		 if(setname==null||setname.length()<=0)
		 {
			 setname="";
		 }  
		 if("A01".equalsIgnoreCase(setname))
		 {
			 pageContext.setAttribute(showtext, text);
			 pageContext.setAttribute(tiptext, "");
			 return SKIP_BODY;
		 } 
		 if(text==null||text.length()<=0)
		 {
			 pageContext.setAttribute(showtext, text);
			 pageContext.setAttribute(tiptext, "");
			 return SKIP_BODY;
		 } 
		 if(itemtype==null||!"M".equalsIgnoreCase(itemtype))
		 {
			 pageContext.setAttribute(showtext, text);
			 pageContext.setAttribute(tiptext, "");
			 return SKIP_BODY;
		 } 
		 try
		 {
			 String tt=text.replaceAll("&quot;", "").replaceAll("<br>", "");
			 if(tt!=null&&tt.length()>10&&tt.length()<19)
			 {
				
				 //text=text.substring(0,10);
				 String str=PubFunc.getTagStr(text);
				 str=str.replaceAll("\\\\\"", "“");
				 str=str.replaceAll("\r\n", "<br>");//zhaogd 2013-11-7 将备注型指标内内容中的回车键替换成换行符
				 str=str.replaceAll("\r", "<br>");
				 str=str.replaceAll("\n", "<br>");
				 String tipstr="onmouseout=\"UnTip()\" onmouseover=\"Tip('"+str+"',STICKY ,true)\"";
				 pageContext.setAttribute(tiptext, tipstr.replaceAll("\r\n", "<br>"));
				 //onmouseout="UnTip()" onmouseover="Tip('标识工作岗位发生变化的种类。',STICKY ,true)"
				 String strT=text;
				 pageContext.setAttribute(showtext, strT);
			 }else if(tt.length()>19){
				 //text=text.substring(0,10);
				 String str=PubFunc.getTagStr(text);
				 str=str.replaceAll("\\\\\"", "“");
				 str=str.replaceAll("\r\n", "<br>");//zhaogd 2013-11-7 将备注型指标内内容中的回车键替换成换行符
				 str=str.replaceAll("\r", "<br>");
				 str=str.replaceAll("\n", "<br>");
				 String tipstr="onmouseout=\"UnTip()\" onmouseover=\"Tip('"+str+"',STICKY ,true)\"";
				 
				 pageContext.setAttribute(tiptext, tipstr.replaceAll("\r\n", "<br>"));
				 //onmouseout="UnTip()" onmouseover="Tip('标识工作岗位发生变化的种类。',STICKY ,true)"
				 String strT=text.substring(0,10);
				 if(strT.indexOf("<")==9)
					 strT=strT.replaceAll("<", "");
				 pageContext.setAttribute(showtext, strT+"...");
			 }
			 else
			 {
				 pageContext.setAttribute(showtext, text);
				 pageContext.setAttribute(tiptext, "");
			 }
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 return SKIP_BODY;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getShowtext() {
		return showtext;
	}
	public void setShowtext(String showtext) {
		this.showtext = showtext;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
