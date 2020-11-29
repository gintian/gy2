package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ItemMemoTab extends BodyTagSupport {
  private String setname="";
  private String itemid="";  
  private String id="";
  private String img="";
  public String getImg() {
	return img;
}
public void setImg(String img) {
	this.img = img;
}
public int doEndTag() throws JspException 
  {
	  if(setname==null||setname.length()<=0)
		  return SKIP_BODY;
	  //（田野注释仅此两行代码）客户要求主集指标支持显示20130726
	  //if(setname.equalsIgnoreCase("A01"))//高法提，主集不显示
		//  return SKIP_BODY;
	  if(itemid==null||itemid.length()<=0)
		  return SKIP_BODY;
	  //String sql="select itemmemo  FROM fielditem WHERE (Upper(fieldsetid) = '"+setname.toUpperCase() +"' and Upper(itemid)='"+itemid.toUpperCase()+"')";
	  //RowSet rs=null;
	  JspWriter out=pageContext.getOut();
	  //Connection conn=null;		
	  try
	  {
		  String memo="";		  
		  /*
		  conn=AdminDb.getConnection();
		  ContentDAO dao=new ContentDAO(conn);
		  rs=dao.search(sql);
		  if(rs.next())
			  memo=Sql_switcher.readMemo(rs, "itemmemo");
		  */
		  FieldItem fielditem=DataDictionary.getFieldItem(itemid.toLowerCase());
		  if(fielditem==null)
			  memo="";
		  else
			  memo=fielditem.getExplain();
				  
		  StringBuffer onmouse=new StringBuffer();
		  if(memo!=null&&memo.length()>0)
		  {
				/*memo=memo!=null&&memo.length()>0?memo.replaceAll("\n","<br>"):"";
			    
				//pageContext.getOut().println("<img src=\"/images/hint_1.gif\"  border=\"0\">");
			   
			    onmouse.append("  onmouseout=\"nd(); return true;\"");				
			    onmouse.append("onmouseover=\"drs('"+SafeCode.encode(memo)+"','');return true;\"");
				//pageContext.getOut().println(memo);
				//pageContext.setAttribute(id, "title=\""+memo+"\"");
			    pageContext.setAttribute(id, onmouse.toString());
				pageContext.setAttribute(img, "<img src=\"/images/hint_1.gif\"  border=\"0\">");*/	
			    String str=PubFunc.getTagStr(memo);
				str=str.replaceAll("\\\\\"", "“");
				if(!"\r\n".equals(str)){
				    //解决如果只有/n，前台报错的问题，wangrd 2013-12-5
				    str=str.replaceAll("\r\n", "<br>");
				    str=str.replaceAll("\n", "<br>");				        
				
				    //str ="xxxx \r\n xxxx";
    				String tipstr="onmouseout=\"UnTip()\" onmouseover=\"Tip('"+str+"',STICKY ,true)\"";
    				pageContext.setAttribute(id, tipstr.replaceAll("\r\n", "<br>"));
    				pageContext.setAttribute(img, "<img src=\"/images/hint_1.gif\"  border=\"0\">");
				}else{
					pageContext.setAttribute(id, "");
					pageContext.setAttribute(img, "");
				}
		  }else
		  {
			  pageContext.setAttribute(id, "");	
			  pageContext.setAttribute(img, "");
		  }
		  
		  
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally
		{
//			try
//			{
//			 if (conn != null)
//	             conn.close();
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//			}
	          
		}
	  return SKIP_BODY;
  }
public String getItemid() {
	return itemid;
}
public void setItemid(String itemid) {
	this.itemid = itemid;
}
public String getSetname() {
	return setname;
}
public void setSetname(String setname) {
	this.setname = setname;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
	
}
