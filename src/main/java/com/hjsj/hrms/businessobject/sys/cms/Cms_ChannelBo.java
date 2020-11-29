/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.cms;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.FastHashMap;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *<p>Title:Cms_ChannelBo</p> 
 *<p>Description:内容管理业务类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-7:上午11:24:35</p> 
 *@author cmq
 *@version 4.0
 */
public class Cms_ChannelBo {
	/**数据库连接*/
	private Connection conn;
	/**数据操作dao对象*/
	private ContentDAO dao;
	/**频道对象列表*/
	static ArrayList chl_list;
	/**频道父子关系*/
	static FastHashMap chl_hm_link;
	private String type;
	private String chl_no;
	private String chl_id;//当前节点id用来判断 哪个频道该 突出显示
	private String showtye="";//主要区分老版和新版 即5.0锁以前的版本不会带有 这个属性，5.0后的版本带有这个属性。
	public String getShowtye() {
		return showtye;
	}
	public void setShowtye(String showtye) {
		this.showtye = showtye;
	}
	public String getChl_id() {
		return chl_id;
	}
	public void setChl_id(String chl_id) {
		this.chl_id = chl_id;
	}
	public Cms_ChannelBo(Connection conn) {
		this.conn=conn;
		dao=new ContentDAO(conn);
	}
	/**
	 * 查询所有定义的频道
	 * channel_id           int NOT NULL,
       parent_id            int NULL,
       name                 varchar(250) NULL,
       visible              int NULL,
       function_id          varchar(50) NULL,
       visible_type         int NULL,
       icon_url             varchar(250) NULL,
       icon_width           int NULL,
       icon_height          int NULL
	 * @throws GeneralException
	 */
	private void search_allchannel()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select * from t_cms_channel where visible=1 order by chl_sort");
		RowSet rset=null;
		try
		{
			rset=dao.search(buf.toString());
			LazyDynaBean dynabean=null;
			while(rset.next())
			{
				dynabean=new LazyDynaBean();
				dynabean.set("channel_id", rset.getString("channel_id"));
				dynabean.set("parent_id", rset.getString("parent_id"));
				String temp=rset.getString("name")==null?"":rset.getString("name");
				//temp=PubFunc.toHtml(temp);
				dynabean.set("name", temp);
				dynabean.set("visible", rset.getString("visible"));
				dynabean.set("function_id", rset.getString("function_id"));
				dynabean.set("visible_type", rset.getString("visible_type"));
				dynabean.set("icon_url", rset.getString("icon_url")==null?"":rset.getString("icon_url"));	
				dynabean.set("icon_width", rset.getString("icon_width")==null?"60":rset.getString("icon_width"));
				dynabean.set("icon_height", rset.getString("icon_height")==null?"":rset.getString("icon_height"));
				dynabean.set("menu_width", rset.getString("menu_width")==null?"":rset.getString("menu_width"));
				chl_list.add(dynabean);
			}
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			if(rset!=null)
			{
				try
				{
					rset.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	/**
       content_id           int NOT NULL,
       channel_id           int NULL,
       title                varchar(250) NULL,
       content              text NULL,
       out_url              varchar(250) NULL,
       params               varchar(200) NULL,
       target               varchar(20) NULL,
       news_date            datetime NULL,
       create_user          varchar(50) NULL,
       visible              int NULL,    0|1 (不可见｜可见)
       content_type         int NULL     0|1（内容｜超链）
     * @throws GeneralException
	 */
	private void search_allContent()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		buf.append("select * from t_cms_content where visible=1 order by channel_id, content_sort");
		RowSet rset=null;
		try
		{
			rset=dao.search(buf.toString());
			LazyDynaBean dynabean=null;
			String chl_no=null;
			ArrayList list=null;
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			while(rset.next())
			{
				dynabean=new LazyDynaBean();
				chl_no=rset.getString("channel_id");
				dynabean.set("channel_id", rset.getString("channel_id"));
				dynabean.set("content_id", rset.getString("content_id"));
				dynabean.set("title", rset.getString("title")==null?"":rset.getString("title"));
				dynabean.set("out_url", rset.getString("out_url")==null?"":rset.getString("out_url"));
				dynabean.set("content", Sql_switcher.readMemo(rset, "content"));
				dynabean.set("params", rset.getString("params")==null?"": rset.getString("params"));
				dynabean.set("target", rset.getString("target")==null?"_self":rset.getString("target"));
				//Constant v = null;
			    dynabean.set("news_date",  PubFunc.DoFormatDate((rset.getDate("news_date")==null?"":rset.getDate("news_date").toString())));	
				dynabean.set("create_user", rset.getString("create_user"));
				dynabean.set("content_type", rset.getString("content_type"));
				if(chl_hm_link.get(chl_no)==null) {
                    list=new ArrayList();
                } else {
                    list=(ArrayList)chl_hm_link.get(chl_no);
                }
				list.add(dynabean);		
				chl_hm_link.put(chl_no, list);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally{
			if(rset!=null)
			{
				try
				{
					rset.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 取得每个频道的内容列表
	 * @param chl_no 
	 * @return
	 */
	public ArrayList getContentList(String chl_no)
	{
		ArrayList list=null;
		try
		{
			if(chl_hm_link==null)
			{
				chl_hm_link=new FastHashMap();
				search_allContent();
			}
			list=(ArrayList)chl_hm_link.get(chl_no);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 同步方法：防止同时出现多个请求时频道重复生成的问题
	 * @param conn 数据库连接
	 * @throws GeneralException 如果发生了数据库访问错误
	 * @author 刘蒙
	 */
	public static synchronized void refData(Connection conn) throws GeneralException {
		StringBuffer buf = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rset = null;
		
		try {
			// 频道对象列表
			chl_list = new ArrayList();
			chl_list.clear();
			
			buf.append("select ")
			.append(" cms.channel_id,cms.parent_id,cms.name,cms.visible,cms.function_id,cms.visible_type,cms.icon_url")
			.append(" ,cms.icon_width,cms.icon_height,cms.menu_width, con.content_type ")
			.append(" from t_cms_channel cms left join t_cms_content con on cms.channel_id = con.channel_id")
			.append(" where cms.visible=1 order by cms.chl_sort ");
			rset = dao.search(buf.toString());
			while (rset.next()) {
				LazyDynaBean dynabean = new LazyDynaBean();
				dynabean.set("channel_id", rset.getString("channel_id"));
				dynabean.set("parent_id", rset.getString("parent_id"));
				String temp = rset.getString("name") == null ? "" : rset.getString("name");
				dynabean.set("name", temp);
				dynabean.set("visible", rset.getString("visible"));
				dynabean.set("function_id", rset.getString("function_id"));
				dynabean.set("visible_type", rset.getString("visible_type"));
				dynabean.set("icon_url", rset.getString("icon_url") == null ? "" : rset.getString("icon_url"));
				dynabean.set("icon_width", rset.getString("icon_width") == null ? "60" : rset.getString("icon_width"));
				dynabean.set("icon_height", rset.getString("icon_height") == null ? "" : rset.getString("icon_height"));
				dynabean.set("menu_width", rset.getString("menu_width") == null ? "" : rset.getString("menu_width"));
				dynabean.set("content_type", rset.getString("content_type") == null ? "" : rset.getString("content_type"));
				chl_list.add(dynabean);
			}

			// 频道父子关系
			chl_hm_link = new FastHashMap();
			chl_hm_link.clear();
			
			buf.setLength(0);
			buf.append("select * from t_cms_content where visible=1 order by channel_id, content_sort");
			rset = dao.search(buf.toString());
			while (rset.next()) {
				LazyDynaBean dynabean = new LazyDynaBean();
				String chl_no = rset.getString("channel_id");
				dynabean.set("channel_id", rset.getString("channel_id"));
				dynabean.set("content_id", rset.getString("content_id"));
				dynabean.set("title", rset.getString("title") == null ? "" : rset.getString("title"));
				dynabean.set("out_url", rset.getString("out_url") == null ? "" : rset.getString("out_url"));
				dynabean.set("content", Sql_switcher.readMemo(rset, "content"));
				dynabean.set("params", rset.getString("params") == null ? "" : rset.getString("params"));
				dynabean.set("target", rset.getString("target") == null ? "_self" : rset.getString("target"));
				dynabean.set("news_date", PubFunc.DoFormatDate((rset.getDate("news_date") == null ? "" : rset.getDate("news_date").toString())));
				dynabean.set("create_user", rset.getString("create_user"));
				dynabean.set("content_type", rset.getString("content_type"));
				ArrayList list = chl_hm_link.get(chl_no) == null ? new ArrayList() : (ArrayList) chl_hm_link.get(chl_no);
				list.add(dynabean);
				chl_hm_link.put(chl_no, list);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rset != null) {
				try {
					rset.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 取当前的频道子节点
	 * @param chl_no 频道
	 * @return
	 */
	public ArrayList getChildList(String chl_no)
	{
		ArrayList list=new ArrayList();
		String curr_no=null;
		String parent_id=null;
		try
		{
			if (chl_list == null || chl_list.size() == 0 || chl_hm_link == null) {
				refData(this.conn);
			}
			for(int i=0;i<chl_list.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)chl_list.get(i);
				curr_no=(String)dynabean.get("channel_id");
				parent_id=(String)dynabean.get("parent_id");
				if(parent_id.equalsIgnoreCase(chl_no)&&(!curr_no.equalsIgnoreCase(parent_id))) {
                    list.add(dynabean);
                }
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得当前频道内容
	 * @param chl_no
	 * @return
	 */
	private LazyDynaBean getTheChannel(String chl_no)
	{
		LazyDynaBean dynabean=null;
		for(int i=0;i<chl_list.size();i++)
		{
			LazyDynaBean temp=(LazyDynaBean)chl_list.get(i);
			if(temp.get("channel_id").equals(chl_no))
			{
				dynabean=temp;
				break;
			}
		}
		return dynabean;
	}
	/*
	function window.onload()
	{
		var d1 = new Array(Layer1,Layer2);
		for( var m = 0;m < d1.length;m++)
		{
			var shtml=d1[m].innerHTML;
			var ifm=document.createElement("<iframe src=javascript:false; frameborder=0 marginheight=0 marginwidth=0 hspace=0 vspace=0 scrolling=no></iframe>");
			ifm.style.width=d1[m].offsetWidth;
			ifm.style.height=d1[m].offsetHeight;
			ifm.name=ifm.uniqueID;
			d1[m].innerHTML="";
			d1[m].appendChild(ifm);
			window.frames[ifm.name].document.write(iframeCss.outerHTML+"<body leftmargin=0 topmargin=0>"+shtml+"</body>");
		}
	}
	*/
	/**
	 * 输出上述javascript代码
	 */
	private String outJavascriptCode(ArrayList childlist)
	{
		/**所有一级频道*/
		StringBuffer layer=new StringBuffer();
		for(int i=0;i<childlist.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)childlist.get(i);
			String chl_no=(String)dynabean.get("channel_id");
			
			ArrayList list=getChildList(chl_no);
			if(list.size()==0) {
                continue;
            }
			
			ArrayList  alist = (ArrayList)chl_hm_link.get(chl_no);
			if(alist!=null&&alist.size()>0)
			{
				LazyDynaBean b = (LazyDynaBean)alist.get(0);
				String content_type=(String)b.get("content_type");
				if("1".equals(this.chl_no)&&!"1".equals(content_type)&&this.type!=null&& "1".equals(this.type))//新版，第一层超链接的，不现实下级菜单
                {
                    continue;
                }
					
			}
			layer.append("Layer");
			layer.append(i+1);
			layer.append(",");
		}
		if(layer.length()==0) {
            return "";
        }
		layer.setLength(layer.length()-1);
		
		StringBuffer buf=new StringBuffer();
		buf.append("<script language=\"javascript\">");
		buf.append("function window.onload()");
		buf.append("{");
		buf.append("var d1=new Array(");
		buf.append(layer.toString());
		buf.append(");");
		buf.append("for( var m = 0;m < d1.length;m++)");
		buf.append("{");
		buf.append("var shtml=d1[m].innerHTML;");
		buf.append("var ifm=document.createElement(\"<iframe src=javascript:false; frameborder=0 marginheight=0 marginwidth=0 hspace=0 vspace=0 scrolling=no allowTransparency='true'></iframe>\");");
		buf.append("ifm.style.width=d1[m].offsetWidth;");
		buf.append("ifm.style.height=d1[m].offsetHeight;");
		buf.append("ifm.name=ifm.uniqueID;");
		buf.append("d1[m].innerHTML=\"\";");
		buf.append("d1[m].appendChild(ifm);");
		buf.append("window.frames[ifm.name].document.write(iframeCss.outerHTML+\"<body leftmargin=0 topmargin=0 style='background-color:#7DC7FF'>\"+shtml+\"</body>\");");
		
		buf.append("}");
		buf.append("}");	

		buf.append("</script>");		
		return buf.toString();
	}
	/**
	 * 输出内容页面
	 * @param chl_no 需要输出的频道
	 * @return
	 */
	public String outCmsHtml(String chl_no)
	{
		StringBuffer buf=new StringBuffer();
        this.chl_no=chl_no;
		/**取得chl_no频道下的一级频道(子频道)*/		
		ArrayList childlist=getChildList(chl_no);
		if(childlist==null||childlist.size()==0) {
            return "";
        }
		/**当前频道*/
		LazyDynaBean dynabean=getTheChannel(chl_no);
		if(dynabean==null) {
            return "";
        }
		/**输出javascript代码*/
		//if(this.type==null||this.type.equals("0")||(this.type.equals("1")&&!chl_no.equals("1")))
	   // buf.append(outJavascriptCode(childlist));
		buf.append("\r\n");
		/**菜单显示方式
		 * =0 平铺
		 * =1 下拉
		 * */
		int visible_type=Integer.parseInt((String)dynabean.get("visible_type"));
		
		if(this.showtye==null|| "1".equalsIgnoreCase(this.showtye)){
			buf.append(outJavascriptCode(childlist));
			buf.append("\r\n");
			buf.append(outFirstChannel(visible_type,childlist));
			buf.append("\r\n");
			buf.append(outSecondChannel(childlist));
			buf.append("\r\n");
		}
		if(this.showtye!=null&& "2".equalsIgnoreCase(this.showtye)){
			buf.append(this.out_cmsAllChanel(visible_type, childlist));
			
		}
		
		//if(this.type==null||this.type.equals("0")||(this.type.equals("1")&&!chl_no.equals("1")))
		{
//		   buf.append("\r\n");
//		   buf.append(outSecondChannel(childlist));
//		   buf.append("\r\n");
		}	
		return buf.toString();
	}
	/*
	<div id=Layer1 onmouseover="style.visibility=''" onmouseout="style.visibility='hidden'" style="visibility:hidden;HEIGHT: 11px; POSITION: absolute; WIDTH: 160px; Z-INDEX: 1; top: -1px; left:0pt">
	  <table width="100%"  border="0" cellpadding="1" cellspacing="1" bgcolor="#0B4326">
	    <tr>
	      <td bgcolor="#EFEFEF">
		    <table width="100%" border="0" cellpadding="０" cellspacing="0">
	          <tr>
	            <td height="20">&nbsp;&nbsp;<a href="/wssb/wstysb.do?a_edit=true&a_sbfs=01" target="_parent" class="a1">企业简介</a></td>
	          </tr>
	          <tr>
	            <td height="20">&nbsp;&nbsp;<a href="/wssb/wstysb.do?a_edit=true&a_sbfs=02"  target="_parent" class="a1">我们的业务</a></td>
	          </tr>
	        </table>
		</td>
	    </tr>
	  </table>
	</div>
	<div id=Layer2 onmouseover="style.visibility=''" onmouseout="style.visibility='hidden'"  style="visibility:hidden;HEIGHT: 11px; POSITION: absolute; WIDTH: 450px; Z-INDEX: 2; top: -1px; left: 45pt">
	  <table width="100%"  border="0" cellpadding="1" cellspacing="1" bgcolor="#0B4326">
	    <tr>
	      <td bgcolor="#EFEFEF">
	       <table width="100%" border="0" cellpadding="0" cellspacing="0">
	          <tr>
	            <td height="20">&nbsp;&nbsp;<a href="/cwbb/cwzl.do"  target="_parent" class="a1">新中粮 新标志</a></td>
	            <td height="20">&nbsp;&nbsp;<a href="/cwbb/cwbb/cwbbSearch.do"  target="_parent" class="a1">自然之源 重塑你我</a></td>
	            <td height="20">&nbsp;&nbsp;<a href="/cwbb/cwbb/cwbbSearch.do"  target="_parent" class="a1">自然之源 重塑你我</a></td>
	            <td height="20">&nbsp;&nbsp;<a href="/cwbb/cwbb/cwbbSearch.do"  target="_parent" class="a1">中粮文化</a></td>
	            <td height="20">&nbsp;&nbsp;<a href="/cwbb/cwbb/cwbbSearch.do"  target="_parent" class="a1">员工感言</a></td>
	          </tr>
	        </table>
	      </td>
	    </tr>
	  </table>
	</div>	
	*/
	/**
	 * 创建下拉菜单
	 */
	private Element createSecondTable(int visible_type,LazyDynaBean dynabean)
	{
		Element table=new Element("table");
		table.setAttribute("width","100%");
		table.setAttribute("border","0");		
		if(visible_type==1)
		{
			table.setAttribute("cellpadding","0");
			table.setAttribute("cellspacing","0");				
			//table.setAttribute("bgcolor","#22A6AB");
		}
		else
		{
			table.setAttribute("cellpadding","0");
			table.setAttribute("cellspacing","0");
		}
		Element tr=new Element("tr");
		table.addContent(tr);
		Element td=new Element("td");
		tr.addContent(td);
		/**=1菜单*/
		if(visible_type==1)
		{
			td.setAttribute("class","MenuRow");
		}
		Element ctable=new Element("table");		
		ctable.setAttribute("width","100%");
		ctable.setAttribute("border","0");		
		ctable.setAttribute("cellpadding","0");
		ctable.setAttribute("cellspacing","0");	
		Element ctr=null;
		Element c_td=null;
		/**平铺*/
		if(visible_type==0)
		{
			ctr=new Element("tr");
			ctable.addContent(ctr);
			c_td=new Element("td");
			c_td.setAttribute("width","1");
			c_td.setAttribute("align","left");
			
			/*Element c_img=new Element("img");
			c_img.setAttribute("src","../../images/nav-n_03.gif");
			c_img.setAttribute("border","0");
			c_img.setAttribute("width","10");*/
			//c_img.setAttribute("height","23");
			//c_td.addContent(c_img);	
			//c_td.setAttribute("class","MenuLeftHead");
			ctr.addContent(c_td);
			
			/**c_td*/
			c_td=new Element("td");
			c_td.setAttribute("height","20");
			c_td.setAttribute("valign","middle");
			c_td.setAttribute("class","MenuRow");
			//c_td.setAttribute("bgcolor","");			
		}	
		String chl_no=(String)dynabean.get("channel_id");
		ArrayList list=getChildList(chl_no);	
		String c_chl_no=null;
		
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean c_dynabean=(LazyDynaBean)list.get(i);
			c_chl_no=(String)c_dynabean.get("channel_id");
			if(visible_type==1)
			{
				ctr=new Element("tr");
				ctable.addContent(ctr);
				
				c_td=new Element("td");
				c_td.setAttribute("height","25");
				c_td.setAttribute("valign","middle");	
				c_td.setAttribute("class","MenuRow_1");
				//c_td.setAttribute("onMouseOver","SETTDCOLOR(this,'#A2D9DC');");
				//c_td.setAttribute("onMouseOut","SETTDCOLOR(this,'#22A6AB');");
			}	

			String icon_url=(String)c_dynabean.get("icon_url");
			ArrayList contentlist=getContentList(c_chl_no);
			if(icon_url==null|| "".equalsIgnoreCase(icon_url))
			{
				if(contentlist==null||contentlist.size()==0)
				{
					/**&nbsp;&在xml中保留词*/
					Element font = new Element("font");
					font.setAttribute("class","MenuRowFont");
					font.setText(";blank;"+(String)c_dynabean.get("name"));	
					c_td.addContent(font);
					//c_td.setText(";blank;"+(String)c_dynabean.get("name"));
				}
				else
				{
					StringBuffer target=new StringBuffer();					
					String out_url=getFirstUrl(contentlist,target,c_chl_no);					
					Element a=new Element("a");
					if(out_url.length()>0)
					{
						a.setAttribute("href",out_url);	
						a.setAttribute("target",target.toString());
					}
					else
					{
						a.setAttribute("href","javascript:parent.getcontent('"+c_chl_no+"');");
					}
					Element font = new Element("font");
					font.setAttribute("class","MenuRowFont");
					font.setText((String)c_dynabean.get("name"));	
					a.addContent(font);
					c_td.addContent(a);	
					if(visible_type==0)
					{
						a.setAttribute("class","f12white");
						if(i<list.size()-1)
						{	
							Element span=new Element("span");
							span.setAttribute("class","f12white");
							span.setAttribute("width","20");
							span.setText(";blank;|;blank;");
							c_td.addContent(span);							
						}
					}
				}
			}
			else
			{
				if(contentlist==null||contentlist.size()==0)
				{
					Element img=new Element("img");
					img.setAttribute("src",icon_url.toString());
					img.setAttribute("border","0");
					c_td.addContent(img);
				}
				else
				{
					/**
					 * 按第一个节点的内容来定，如果第一个节点的内容为按超链显示的话，
					 * 则仅显示第一个网页的内容，其它网页的内容不显示。
					 */
						StringBuffer target=new StringBuffer();							
						String out_url=getFirstUrl(contentlist,target,c_chl_no);
						Element a=new Element("a");
						if(out_url.length()>0)
						{
							a.setAttribute("href",out_url);		
							a.setAttribute("target",target.toString());
						}
						else
						{
							a.setAttribute("href","javascript:parent.getcontent('"+c_chl_no+"');");
						}
						if(visible_type==0) {
                            a.setAttribute("class","f12white");
                        }
						Element img=new Element("img");
						img.setAttribute("src",icon_url.toString());
						img.setAttribute("border","0");
						a.addContent(img);
						c_td.addContent(a);								
				}					
			}
			if(visible_type==1) {
                ctr.addContent(c_td);
            }
		}
		/**平铺*/
		if(visible_type==0)
		{
			ctr.addContent(c_td);
			c_td=new Element("td");
			c_td.setAttribute("width","1");
			c_td.setAttribute("align","left");
			
			/*Element c_img=new Element("img");
			c_img.setAttribute("src","../../images/nav-n_05.gif");
			c_img.setAttribute("border","0");
			c_img.setAttribute("width","10");
			//c_img.setAttribute("height","23");
			c_td.addContent(c_img);		*/	
			//c_td.setAttribute("class", "MenuRightHead");
			ctr.addContent(c_td);
		}			
		
		td.addContent(ctable);
		return table;
	}
	/**
	 * 输出二级菜单频道
	 * @param childlist
	 * @return
	 */
	private String outSecondChannel(ArrayList childlist)
	{
		StringBuffer buf=new StringBuffer();	
		int width=0;
		String menu_width=null;
		for(int i=0;i<childlist.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)childlist.get(i);
			String chl_no=(String)dynabean.get("channel_id");
			ArrayList list=getChildList(chl_no);
			if(list==null||list.size()==0) {
                continue;
            }
			ArrayList  alist = (ArrayList)chl_hm_link.get(chl_no);
			if(alist!=null&&alist.size()>0)
			{
				LazyDynaBean b = (LazyDynaBean)alist.get(0);
				String content_type=(String)b.get("content_type");
				if("1".equals(this.chl_no)&&!"1".equals(content_type)&&this.type!=null&& "1".equals(this.type))//第一层，超链接的，不现实下级菜单
                {
                    continue;
                }
					
			}
			/**菜单显示方式
			 * =0 平铺
			 * =1 下拉
			 */		
			menu_width=(String)dynabean.get("menu_width");
			if(menu_width==null|| "".equalsIgnoreCase(menu_width)) {
                width=list.size()*90;
            } else {
                width=Integer.parseInt(menu_width);
            }
			String replace="WIDTH:"+width+"px";
			int visible_type=Integer.parseInt((String)dynabean.get("visible_type"));			
			Element div=new Element("div");
			String layname="Layer"+(i+1);
			div.setAttribute("id",layname);
			div.setAttribute("onmouseover","style.visibility=''");
			div.setAttribute("onmouseout","style.visibility='hidden'");
			String style="";
			if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
				style="visibility:hidden;HEIGHT: 55px; POSITION: absolute; WIDTH: 100px; Z-INDEX: 2; top: -1px; left: 45pt";
			}else{
				style="visibility:hidden;HEIGHT: 11px; POSITION: absolute; WIDTH: 100px; Z-INDEX: 2; top: -1px; left: 45pt";
			}
			//if(visible_type==0)
			style=style.replaceAll("WIDTH: 100px", replace);
			div.setAttribute("style",style);
			/**下拉菜单*/
			Element table=createSecondTable(visible_type,dynabean);
			div.addContent(table);
			
			XMLOutputter outputter=new XMLOutputter();
			Format format=Format.getCompactFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String temp=outputter.outputString(div);
			temp=temp.replaceAll(";blank;", "&nbsp;&nbsp;");
			//&lt; 或&gt;
			temp=temp.replaceAll("&lt;", "<");
			temp=temp.replaceAll("&gt;", ">");
			buf.append(temp);	
			buf.append("\r\n");
		}
		
		return buf.toString();		
	}
	
		
	public Element getChildChanels(LazyDynaBean dynabean){
		Element ul=null;
		int width=0;
		int length=0;
		String menu_width=null;
	
			String chl_no=(String)dynabean.get("channel_id");
			ArrayList list=getChildList(chl_no);
			if(list==null||list.size()==0) {
                return ul;
            }
			ArrayList  alist = (ArrayList)chl_hm_link.get(chl_no);
			if(alist!=null&&alist.size()>0)
			{
				LazyDynaBean b = (LazyDynaBean)alist.get(0);
				String content_type=(String)b.get("content_type");
				if("1".equals(this.chl_no)&&!"1".equals(content_type)&&this.type!=null&& "1".equals(this.type))//第一层，超链接的，不现实下级菜单
                {
                    return ul;
                }
					
			}
			menu_width=(String)dynabean.get("menu_width");
			if(menu_width==null|| "".equalsIgnoreCase(menu_width)) {
                width=list.size()*90;
            } else {
                width=Integer.parseInt(menu_width);
            }
			int visible_type=Integer.parseInt((String)dynabean.get("visible_type"));	
			String c_chl_no=null;	
			ul=new Element("ul");
			/**菜单显示方式
			 * =0 平铺
			 * =1 下拉
			 */	
			
			int cont=0;
			for(int k=0;k<list.size();k++)
			{
				LazyDynaBean c_dynabean=(LazyDynaBean)list.get(k);
				if(visible_type==0){
					menu_width=(String)dynabean.get("menu_width");
					if(menu_width!=null){
						cont++;
						length+=Integer.parseInt(menu_width);
					}else{
						menu_width="110";
					}
				}
				c_chl_no=(String)c_dynabean.get("channel_id");
				String icon_url=(String)c_dynabean.get("icon_url");
				ArrayList contentlist=getContentList(c_chl_no);
				Element li=new Element("li");
				if(icon_url==null|| "".equalsIgnoreCase(icon_url))
				{
					if(contentlist==null||contentlist.size()==0)
					{
					
						Element font = new Element("font");
						font.setAttribute("class","MenuRowFont");
						Element b=new Element("a");
						font.setText("   "+(String)c_dynabean.get("name"));	
						b.addContent(font);
						li.addContent(b);
					}else{
						StringBuffer target=new StringBuffer();					
						String out_url=getFirstUrl(contentlist,target,c_chl_no);					
						Element a=new Element("a");
						if(out_url.length()>0)
						{
							a.setAttribute("href",out_url);	
							a.setAttribute("target",target.toString());
						}
						else
						{
							a.setAttribute("href","javascript:parent.getcontent('"+c_chl_no+"');");
						}
						Element font = new Element("font");
						font.setAttribute("class","MenuRowFont");
						font.setText((String)c_dynabean.get("name"));	
						a.addContent(font);
						li.addContent(a);
					}
				}else{
					if(contentlist==null||contentlist.size()==0)
					{
						Element img=new Element("img");
						img.setAttribute("src",icon_url.toString());
						img.setAttribute("border","0");
						li.addContent(img);
					}
					else
					{
						/**
						 * 按第一个节点的内容来定，如果第一个节点的内容为按超链显示的话，
						 * 则仅显示第一个网页的内容，其它网页的内容不显示。
						 */
							StringBuffer target=new StringBuffer();							
							String out_url=getFirstUrl(contentlist,target,c_chl_no);
							Element a=new Element("a");
							if(out_url.length()>0)
							{
								a.setAttribute("href",out_url);		
								a.setAttribute("target",target.toString());
							}
							else
							{
								a.setAttribute("href","javascript:parent.getcontent('"+c_chl_no+"');");
							}
							if(visible_type==0) {
                                a.setAttribute("class","f12white");
                            }
							Element img=new Element("img");
							img.setAttribute("src",icon_url.toString());
							img.setAttribute("border","0");
							a.addContent(img);
							li.addContent(a);
					}
				}
//				li.setAttribute("class","havo");
//				int mar=(length-Integer.parseInt(menu_width))/2;
//				if(mar>1000){
//					mar=200;
//					
//				}
				
				ul.addContent(li);
			}
			if(visible_type==0)
			{	
//				if(length>0){
//					if(cont<list.size()){
//						//length+=(list.size()-cont)*90;
//					}
//				}else{
//					length=list.size()*90;
//				}
				length=list.size()*110;
				ul.setAttribute("style","width: "+String.valueOf(length)+"px;position:absolute;");
				ul.setAttribute("id","ul"+chl_no);
			}else{
				ul.setAttribute("style","width: 110px;position:absolute;margin-left:"+0+"px");
				ul.setAttribute("id","ul"+chl_no);
			}
		return ul;
		
	} 
		

	/**
	 * 求当前频道第一个节点是否为超链
	 */
	private String getFirstUrl(ArrayList contentlist,StringBuffer target,String chl_no)
	{
		DynaBean bean=(DynaBean)contentlist.get(0);
		String content_type=(String)bean.get("content_type");
		if("1".equals(content_type)) {
            return "";
        }
		String out_url=(String)bean.get("out_url");
		String param=((String)(bean.get("params")==null?"":bean.get("params")));
		if(out_url.toUpperCase().indexOf("HIRE")!=-1&&this.chl_no!=null&& "1".equals(this.chl_no))//第一层菜单才加如下参数
        {
            param+="&cms_chl_no="+chl_no+"&menuType=0";
        }
		target.append((String)bean.get("target"));
		return "".equals(param)?out_url:(out_url+"?"+param);
	}
	/**
	 * 输出一级频道菜单
	 * @param visible_type
	 * @param childlist
	 * @return
	 */
	private String outFirstChannel(int visible_type,ArrayList childlist)
	{
		StringBuffer buf=new StringBuffer();
		Element table=new Element("table");
		table.setAttribute("height","40");
		table.setAttribute("border","0");		
		table.setAttribute("cellpadding","0");
		table.setAttribute("cellspacing","0");	
		table.setAttribute("class","mBanner1");	
		Element tr=null;
		/**平铺*/
		if(visible_type==0)
		{
			tr=new Element("tr");
			table.addContent(tr);
		}
		for(int i=0;i<childlist.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)childlist.get(i);
			String chl_no=(String)dynabean.get("channel_id");
			String width=(String)dynabean.get("icon_width");
			ArrayList list=getChildList(chl_no);
			ArrayList contentlist=getContentList(chl_no);
			if(((list==null||list.size()==0)&&(contentlist==null||contentlist.size()==0)&&(this.chl_no!=null&& "1".equals(this.chl_no))))//第一层频道，如果没有内容，并且没有子频道的，不显示
            {
                continue;
            }
			if(visible_type==1)
			{
				tr=new Element("tr");
				table.addContent(tr);
			}
			if(width==null) {
                width="60";
            }
			Element td=new Element("td");
			td.setAttribute("width",width);
			td.setAttribute("class","firstMenuRow");	
			tr.addContent(td);			
			
			Element div=new Element("div");
			td.addContent(div);
			div.setAttribute("class","a2");
			if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
				div.setAttribute("id","div"+chl_no);
			}
			String layname="Layer"+(i+1);
			if(!(list==null||list.size()==0))
			{
				div.setAttribute("onmouseover","show(this,document.all."+layname+")");
				if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
					div.setAttribute("onmouseout","hide1(this,document.all."+layname+")");
				}else{
					div.setAttribute("onmouseout","hide(document.all."+layname+")");
				}
			}else{
				if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
					div.setAttribute("onmouseover","show1(this)");
					div.setAttribute("onmouseout","hide2(this)");
				}
				
			}
			String icon_url=(String)dynabean.get("icon_url");
			
			if(icon_url==null|| "".equalsIgnoreCase(icon_url))
			{
				if(contentlist==null||contentlist.size()==0)
				{
					Element font = new Element("font");
					font.setAttribute("class", "MenuRowFont");
					font.setText((String)dynabean.get("name"));
					//div.setText((String)dynabean.get("name"));
					div.addContent(font);
				}
				else
				{
						Element a=new Element("a");
						/**
						 * 按第一个节点的内容来定，如果第一个节点的内容为按超链显示的话，
						 * 则仅显示第一个网页的内容，其它网页的内容不显示。
						 */
						StringBuffer target=new StringBuffer();
						String url=getFirstUrl(contentlist,target,chl_no);	
						if(url.length()>0)
						{
							if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
								if(url.indexOf("/hireNetPortal/")!=-1){					
									a.setAttribute("onclick","javascript:changebg('"+url+"',"+chl_no+")");
									a.setAttribute("target",target.toString());
								}else{
									a.setAttribute("href",url);
									a.setAttribute("target",target.toString());
								}
							}else{
								a.setAttribute("href",url);
								a.setAttribute("target",target.toString());
							}
						}
						else
					 	{
							a.setAttribute("href","javascript:parent.getcontent('"+chl_no+"');");
					 	}
						Element font = new Element("font");
						font.setAttribute("class", "MenuRowFont");
						font.setText((String)dynabean.get("name"));
						a.addContent(font);
						//a.setText((String)dynabean.get("name"));
						div.addContent(a);						
				}
			}
			else
			{
				if(contentlist==null||contentlist.size()==0)
				{
					Element img=new Element("img");
					if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
						if(this.chl_id!=null&&this.chl_id.length()!=0&&this.chl_id.equalsIgnoreCase(chl_no)){
							String tt=icon_url.substring(0,icon_url.indexOf(".gif"));
							icon_url=tt+"_1.gif";
							div.removeAttribute("onmouseout");
						}
						img.setAttribute("id","img"+chl_no);
					}
					img.setAttribute("src",icon_url.toString());
					img.setAttribute("border","0");
					div.addContent(img);
				}
				else
				{
						Element a=new Element("a");
						StringBuffer target=new StringBuffer();
						String url=getFirstUrl(contentlist,target,chl_no);	
						if(url.length()>0)
						{	
							if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
								if(url.indexOf("/hire/")!=-1){					
									a.setAttribute("onclick","javascript:changebg('"+url+"',"+chl_no+")");
									a.setAttribute("target",target.toString());
								}else{
									a.setAttribute("href",url);
									a.setAttribute("target",target.toString());
								}
							}else{
								a.setAttribute("href",url);
								a.setAttribute("target",target.toString());
							}
						}
						else
						{
							a.setAttribute("href","javascript:parent.getcontent('"+chl_no+"');");							
						}
						Element img=new Element("img");
						if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
						if(this.chl_id!=null&&this.chl_id.length()!=0&&this.chl_id.equalsIgnoreCase(chl_no)){
							String tt=icon_url.substring(0,icon_url.indexOf(".gif"));
							icon_url=tt+"_1.gif";
							
							div.removeAttribute("onmouseout");
						}
						if(chl_id==null&&i==0){
							String tt=icon_url.substring(0,icon_url.indexOf(".gif"));
							icon_url=tt+"_1.gif";
							
							div.removeAttribute("onmouseout");
						}
						img.setAttribute("id","img"+chl_no);
						}
						img.setAttribute("src",icon_url.toString());
						img.setAttribute("border","0");
						a.addContent(img);						
						div.addContent(a);
				}					
			}
		}//for loop en
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		buf.append(outputter.outputString(table));
		return buf.toString();
	}
	/**
	 * 频道有变化时刷新列表
	 */
	public void refreshChildlist()
	{
		if(chl_list==null) {
            chl_list=new ArrayList();
        }
		chl_list.clear();
		if(chl_hm_link==null) {
            chl_hm_link=new FastHashMap();
        }
		chl_hm_link.clear();		
		try
		{
			// search_allchannel();
			// search_allContent();
			// 为防止后台发布（加载）频道同时用户在外网加载频道引发同步问题
			// 将刷新的操作改为调用同步方法 by 刘蒙
			refData(this.conn);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	
	/**
	 * dml 输出所有频道一级及下级菜单！
	 * 新方法 用ul li 加css样式实现，通过showtype判断不同版本。
	 * 兼容不同格式的图片
	 * */
	public String out_cmsAllChanel(int visible_type,ArrayList childlist){
		StringBuffer buf=new StringBuffer();
		
		/* visible_type 0 横铺 1 纵铺*/
		Element div=new Element("div");
		div.setAttribute("id","navcont");
		Element div1=new Element("div");
		if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
			div1.setAttribute("id","nav1");
		}else{
			div1.setAttribute("id","nav2");
		}

		Element ul=new Element("ul");
		int length=1000;
		int alllenth=0;
		for(int i=0;i<childlist.size();i++)
		{
			
			LazyDynaBean dynabean=(LazyDynaBean)childlist.get(i);
			String chl_no=(String)dynabean.get("channel_id");
			String width=(String)dynabean.get("icon_width");
			ArrayList list=getChildList(chl_no);
			ArrayList contentlist=getContentList(chl_no);
			if(((list==null||list.size()==0)&&(contentlist==null||contentlist.size()==0)&&(this.chl_no!=null&& "1".equals(this.chl_no))))//第一层频道，如果没有内容，并且没有子频道的，不显示
            {
                continue;
            }
		
			
			if(visible_type==0)
			{
				
			}else{
				ul.setAttribute("id","nav");
			}
			Element li=new Element("li");
			if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
				li.setAttribute("id","div"+chl_no);
			}
			String icon_url=(String)dynabean.get("icon_url");
			String type=icon_url.substring(icon_url.lastIndexOf(".")+1);
			if(icon_url==null|| "".equalsIgnoreCase(icon_url))
			{
				li.setAttribute("onmouseover","show1(this)");
				li.setAttribute("onmouseout","hide2(this)");
				if(contentlist==null||contentlist.size()==0)
				{
					Element font = new Element("font");
					font.setAttribute("class", "MenuRowFont");
					font.setText((String)dynabean.get("name"));
					//div.setText((String)dynabean.get("name"));
					li.addContent(font);
					width=String.valueOf(((String)dynabean.get("name")).length()*13);
				}else{
					Element a=new Element("a");
					StringBuffer target=new StringBuffer();
					String url=getFirstUrl(contentlist,target,chl_no);	
					if(url.length()>0)
					{
						if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
							if(url.indexOf("/hire/")!=-1){					
								a.setAttribute("onclick","javascript:changebg('"+url+"',"+chl_no+");");
								a.setAttribute("target",target.toString());
								//a.setAttribute("href","javascript:void(0);");招聘前台新增自定义列点击弹出多余页面
							}else{
								a.setAttribute("href",url);
								a.setAttribute("target",target.toString());
							}
						}else{
							a.setAttribute("href",url);
							a.setAttribute("target",target.toString());
						}
					}else{
						a.setAttribute("href","javascript:parent.getcontent('"+chl_no+"');");
				 	}
					a.setAttribute("class","yes2");
					Element font = new Element("font");
					font.setAttribute("class", "MenuRowFont");
					width=String.valueOf(((String)dynabean.get("name")).length()*13);
					font.setText((String)dynabean.get("name"));
					a.addContent(font);
					li.addContent(a);
				}
				
			}else{
				li.setAttribute("onmouseover","show1(this)");
				li.setAttribute("onmouseout","hide2(this)");
				if(contentlist==null||contentlist.size()==0)
				{
					Element a=new Element("a");
					Element img=new Element("img");
					if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
						if(this.chl_id!=null&&this.chl_id.length()!=0&&this.chl_id.equalsIgnoreCase(chl_no)){
							String tt=icon_url.substring(0,icon_url.lastIndexOf("."));
							icon_url=tt+"_1."+type;
							//div.removeAttribute("onmouseout");
						}
						img.setAttribute("id","img"+chl_no);
					}
					img.setAttribute("src",icon_url.toString());
					img.setAttribute("border","0");
					a.addContent(img);
					a.setAttribute("id","a"+chl_no);
					a.setAttribute("class","yes");
					li.addContent(a);
				}else{
					Element a=new Element("a");
					a.setAttribute("id","a"+chl_no);
					StringBuffer target=new StringBuffer();
					String url=getFirstUrl(contentlist,target,chl_no);	
					if(url.length()>0)
					{	
						if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
							if(url.indexOf("/hire/")!=-1){					
								a.setAttribute("onclick","javascript:changebg('"+url+"',"+chl_no+");");
								a.setAttribute("target",target.toString());
								//a.setAttribute("href","javascript:void(0);");招聘前台新增自定义列点击弹出多余页面
								
							}else{
								a.setAttribute("href",url);
								a.setAttribute("target",target.toString());
							}
						}else{
							a.setAttribute("href",url);
							a.setAttribute("target",target.toString());
						}
					}
					else
					{
						a.setAttribute("href","javascript:parent.getcontent('"+chl_no+"');");							
					}
					a.setAttribute("class","yes");
					Element img=new Element("img");
					if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
						if(this.chl_id!=null&&this.chl_id.length()!=0&&this.chl_id.equalsIgnoreCase(chl_no)){
							String tt=icon_url.substring(0,icon_url.lastIndexOf(".")+1);
							if(tt.indexOf("_1.")==-1){
								icon_url=tt.substring(0,tt.length() - 1)+"_1."+type;
							}
							
							a.setAttribute("class","els");
//							div.removeAttribute("onmouseout");
							// linbz 20160909 选中某个招聘时，保持被选中状态（主要为区分各个招聘渠道，当是链接时有效）
//							li.removeAttribute("onmouseout");   
						}
						if(chl_id==null&&i==0){
							String tt=icon_url.substring(0,icon_url.lastIndexOf("."));
							icon_url=tt+"_1."+type;
							a.setAttribute("class","els");
							//div.removeAttribute("onmouseout");
//							li.removeAttribute("onmouseout");
						}
						img.setAttribute("id","img"+chl_no);
					}
					img.setAttribute("src",icon_url.toString());
					img.setAttribute("border","0");
					a.addContent(img);						
					li.addContent(a);//div.addContent(a);
				}
				
			}
			Element chiul=this.getChildChanels(dynabean);
			if(chiul!=null){
				li.addContent(chiul);
			}
			
			li.setAttribute("style", "width:"+width+"px");
			ul.addContent(li);
			alllenth+=Integer.parseInt(width);
		}
		int tem=length-alllenth;
		if(this.showtye!=null&&this.showtye.trim().length()!=0&& "2".equalsIgnoreCase(this.showtye)){
			ul.setAttribute("style","padding-left:"+(tem/2)+"px;");
		}else{
			
			ul.setAttribute("style","padding-left:0px;");
		}
		
		div1.addContent(ul);
		div.addContent(div1);
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		buf.append(outputter.outputString(div));
		return buf.toString();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
}
