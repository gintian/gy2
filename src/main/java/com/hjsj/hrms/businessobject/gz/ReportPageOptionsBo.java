package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:ReportPageOptionsBo.java</p>
 * <p>Description>:工资报表和工资分析表页面设置业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 8, 2011  5:16:55 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ReportPageOptionsBo {

	private Connection conn;
	private UserView userView;
	private String rsid;
	private String rsdtlid;
	Document doc;
	public ReportPageOptionsBo()
	{
	}
	public ReportPageOptionsBo(Connection conn,UserView userView,String rsid,String rsdtlid)
	{
		this.conn=conn;
		this.userView=userView;
		this.rsid=rsid;
		this.rsdtlid=rsdtlid;
		init();
	}
	/**
	 * 得到参数，如果有自己的，得到自己的，如果自己没有，在输出excel时找系统管理员的，在设置页面的时候不找管理员的，
	 */
	public ReportParseVo analyse(int type)
	{
		ReportParseVo rpv = new ReportParseVo();
		try
		{
			 String path="/param/report/r_user[@name='"+this.userView.getUserName().toLowerCase()+"']";
			 XPath xpath=XPath.newInstance(path);
			 Element element = (Element)xpath.selectSingleNode(doc);
			 if(element==null&&type==2)//当输出excel时，如果自己未设置过，找管理员的设置
			 {
				 path="/param/report/r_user[@super='1']";
				 xpath=XPath.newInstance(path);
				 //element = (Element)xpath.selectSingleNode(doc);
				 ArrayList list = (ArrayList)xpath.selectNodes(doc);
				 if(list!=null&&list.size()>0)
					 element=(Element)list.get(0);//多个设置取第一个
			 }
			 /**
			  * page_type 纸张大小
			  * page_width 纸张宽mm
			  * page_height 纸张高mm
			  * page_range 排列方式
			  * 
			  * pagemargin_top 页面边距 上
			  * pagemargin_left
			  * pagemargin_bottom
			  * pagemargin__right
			  */
			 String page_type="";
			 String page_width="";
			 String page_height="";
			 String page_range="";
			 String pagemargin_top="";
			 String pagemargin_left="";
			 String pagemargin_bottom="";
			 String pagemargin_right="";
			 /**
			  * title_content 标题内容
			  * title_fontface 字体
			  * title_fontsize 字体大小
			  * title_fontblob 粗体
			  * title_underline 下划线
			  * title_fontitalic 斜体
			  * title_delline 删除线
			  * title_color 颜色
			  */
			 String title_content = "";
			 String title_fontface = "";
			 String title_fontsize="";
			 String title_fontblob = "";
			 String title_underline = "";
			 String title_fontitalic="";
			 String title_delline = "";
			 String title_color="";
			 
			 /**
			  * head_left 上左
			  * head_center 上中
			  * head_right 上右
			  * head_fontface 字体
			  * head_fontsize 字体大小
			  * head_fontblod 粗体
			  * head_underline 下划线
			  * head_fontitalic 斜体
			  * head_delline 删除线
			  * head_color 颜色
			  * head_flw_hs 上左内容仅首页显示
			  *	head_fmw_hs 上中内容仅首页显示
			  *	head_frw_hs 上右内容仅首页显示
			  */
			 String head_left ="";
			 String head_center="";
			 String head_right="";
			 String head_fontface = "";
			 String head_fontsize = "";
			 String head_fontblod = "";
			 String head_underline  = "";
			 String head_fontitalic = "";
			 String head_delline = "";
			 String head_color = "";
			 String head_flw_hs = "";
			 String head_fmw_hs = "";
			 String head_frw_hs = "";
			 /**
			  * tail_left 下左
			  * tail_center 下中
			  * tail_right 下右
			  * tail_fontface 字体
			  * tail_fontsize 字体大小
			  * tail_fontblod 粗体
			  * tail_underline 下划线
			  * tail_fontitalic 斜体
			  * tail_delline 删除线
			  * tail_color 颜色
			  * tail_flw_hs 下左内容仅首页显示
			  *	tail_fmw_hs 下中内容仅首页显示
			  *	tail_frw_hs 下右内容仅首页显示
			  */
			 String tail_left = "";
			 String tail_center = "";
			 String tail_right = "";
			 String tail_fontface = "";
			 String tail_fontsize = "";
			 String tail_fontblob = "";
			 String tail_underline = "";
			 String tail_fontitalic = "";
			 String tail_delline = "";
			 String tail_color = "";
			 String tail_flw_hs = "";
			 String tail_fmw_hs = "";
			 String tail_frw_hs = "";
			 /**
			  * main_fontface 字体
			  * main_fontsize 字体大小
			  * main_fontblod 粗体
			  * main_underline 下划线
			  * main_fontitalic 斜体
			  * main_color 颜色
			  */
			 String main_fontface = "";
			 String main_fontsize = "";
			 String main_fontblob = "";
			 String main_underline="";
			 String main_fontitalic = "";
			 String main_color = "";
			 
			 String thead_fn="";
			 String thead_fz="";
			 String thead_fb="";
			 String thead_fu="";
			 String thead_fi="";
			 String thead_fc="";
			 
			 if(element!=null)
			 {
				 Element page = element.getChild("page");
				 if(page!=null)
				 {
				    page_type=page.getAttributeValue("type");
				    page_width=page.getAttributeValue("width");
				    page_height=page.getAttributeValue("height");
				    page_range=page.getAttributeValue("range");
				    pagemargin_top=page.getAttributeValue("margin_top");
				    pagemargin_left=page.getAttributeValue("margin_left");
				    pagemargin_bottom=page.getAttributeValue("margin_bottom");
				    pagemargin_right=page.getAttributeValue("margin_right");
				 }
				 Element title = element.getChild("title");
				 if(title!=null)
				 {
					 title_content = title.getAttributeValue("content");
					 title_fontface = title.getAttributeValue("fontface");
					 title_fontsize=title.getAttributeValue("fontsize");
					 title_fontblob = title.getAttributeValue("fontblob");
					 title_underline = title.getAttributeValue("underline");
					 title_fontitalic=title.getAttributeValue("fontitalic");
					 title_delline = title.getAttributeValue("dline");
					 title_color=title.getAttributeValue("color");
				 }
				 
				 Element page_head = element.getChild("page_head");
				 if(page_head!=null)
				 {
					 head_left = page_head.getAttributeValue("left");
					 head_center=page_head.getAttributeValue("center");
					 head_right=page_head.getAttributeValue("right");
					 head_fontface = page_head.getAttributeValue("fontface");
					 head_fontsize = page_head.getAttributeValue("fontsize");
					 head_fontblod = page_head.getAttributeValue("fontblob");
					 head_underline  = page_head.getAttributeValue("underline");
					 head_fontitalic = page_head.getAttributeValue("fontitalic");
					 head_delline = page_head.getAttributeValue("dline");
					 head_color = page_head.getAttributeValue("color");
					 head_flw_hs = page_head.getAttributeValue("lHeadHomeShow");
					 head_fmw_hs = page_head.getAttributeValue("mHeadHomeShow");
					 head_frw_hs = page_head.getAttributeValue("rHeadHomeShow");
				 }
				 Element page_tail = element.getChild("page_tail");
				 if(page_tail!=null)
				 {
					 tail_left = page_tail.getAttributeValue("left");
					 tail_center = page_tail.getAttributeValue("center");
					 tail_right = page_tail.getAttributeValue("right");
					 tail_fontface = page_tail.getAttributeValue("fontface");
					 tail_fontsize = page_tail.getAttributeValue("fontsize");
					 tail_fontblob = page_tail.getAttributeValue("fontblob");
					 tail_underline = page_tail.getAttributeValue("underline");
					 tail_fontitalic = page_tail.getAttributeValue("fontitalic");
					 tail_delline = page_tail.getAttributeValue("dline");
					 tail_color = page_tail.getAttributeValue("color");
					 tail_flw_hs = page_tail.getAttributeValue("lFootHomeShow");
					 tail_fmw_hs = page_tail.getAttributeValue("mFootHomeShow");
					 tail_frw_hs = page_tail.getAttributeValue("rFootHomeShow");
				 }
				 Element page_main = element.getChild("page_main");
				 if(page_main!=null)
				 {
					 main_fontface = page_main.getAttributeValue("fontface");
					 main_fontsize = page_main.getAttributeValue("fontsize");
					 main_fontblob = page_main.getAttributeValue("fontblob");
					 main_underline=page_main.getAttributeValue("underline");
					 main_fontitalic = page_main.getAttributeValue("fontitalic");
					 main_color = page_main.getAttributeValue("color");
				 }
				 Element page_thead = element.getChild("page_thead");
				 if(page_thead!=null)
				 {
					 thead_fn = page_thead.getAttributeValue("thead_fn");
					 thead_fz = page_thead.getAttributeValue("thead_fz");
					 thead_fb = page_thead.getAttributeValue("thead_fb");
					 thead_fu = page_thead.getAttributeValue("thead_fu");
					 thead_fi = page_thead.getAttributeValue("thead_fi");
					 thead_fc = page_thead.getAttributeValue("thead_fc");
				 }
			 }
			 rpv.setThead_fb(thead_fb);
			 rpv.setThead_fc(thead_fc);
			 rpv.setThead_fi(thead_fi);
			 rpv.setThead_fn(thead_fn);
			 rpv.setThead_fu(thead_fu);
			 rpv.setThead_fz(thead_fz);
			 rpv.setPagetype(page_type);
			 rpv.setWidth(page_width);
			 rpv.setHeight(page_height);
			 rpv.setOrientation(page_range);
			 rpv.setTop(pagemargin_top);
			 rpv.setLeft(pagemargin_left);
			 rpv.setBottom(pagemargin_bottom);
			 rpv.setRight(pagemargin_right);
			 rpv.setTitle_fw(title_content);
			 rpv.setTitle_fn(title_fontface);
			 rpv.setTitle_fz(title_fontsize);
			 rpv.setTitle_fb(title_fontblob);
			 rpv.setTitle_fu(title_underline);
			 rpv.setTitle_fi(title_fontitalic);
			 rpv.setTitle_fs(title_delline);
			 rpv.setTitle_fc(title_color);
			 rpv.setHead_flw(head_left);
			 rpv.setHead_fmw(head_center);
			 rpv.setHead_frw(head_right);
			 rpv.setHead_fn(head_fontface);
			 rpv.setHead_fz(head_fontsize);
			 rpv.setHead_fb(head_fontblod);
			 rpv.setHead_fu(head_underline);
			 rpv.setHead_fi(head_fontitalic);
			 rpv.setHead_fs(head_delline);
			 rpv.setHead_fc(head_color);
			 rpv.setHead_flw_hs(head_flw_hs);
			 rpv.setHead_fmw_hs(head_fmw_hs);
			 rpv.setHead_frw_hs(head_frw_hs);
			 rpv.setTile_flw(tail_left);
			 rpv.setTile_fmw(tail_center);
			 rpv.setTile_frw(tail_right);
			 rpv.setTile_fn(tail_fontface);
			 rpv.setTile_fz(tail_fontsize);
			 rpv.setTile_fb(tail_fontblob);
			 rpv.setTile_fi(tail_fontitalic);
			 rpv.setTile_fu(tail_underline);
			 rpv.setTile_fs(tail_delline);
			 rpv.setTile_fc(tail_color);
			 rpv.setTile_flw_hs(tail_flw_hs);
			 rpv.setTile_fmw_hs(tail_fmw_hs);
			 rpv.setTile_frw_hs(tail_frw_hs);
			 rpv.setBody_fn(main_fontface);
			 rpv.setBody_fz(main_fontsize);
			 rpv.setBody_fb(main_fontblob);
			 rpv.setBody_fu(main_underline);
			 rpv.setBody_fi(main_fontitalic);
			 rpv.setBody_fc(main_color);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rpv;
	}
	/**
	 * 根据新参数对象，生成新参数串
	 * @param rpv
	 * @param type=0设置参数=1为初始化
	 * @return
	 */
	public String createXML(ReportParseVo rpv, int type)
	{
		String newXML = "";
		RowSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			String xml = "";
			ContentDAO dao  = new ContentDAO(this.conn);
			String sql = "select ctrlparam from reportdetail where rsid=?" ;
			list.add(this.rsid);
			if(!"4".equals(this.rsid)) {
				sql+=" and rsdtlid=?";
				list.add(this.rsdtlid);
			}
			
			rs = dao.search(sql,list);
			while(rs.next())
			{
				xml = Sql_switcher.readMemo(rs,"ctrlparam");
			}
			if(xml==null|| "".equals(xml))
    		{
    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
    		}
			Document doc =PubFunc.generateDom(xml);
			Element report = doc.getRootElement().getChild("report");
			if(type==1)//初始化
			{
				if(report==null)
					return xml;
				else
				{
					String path="/param/report/r_user[@name='"+this.userView.getUserName().toLowerCase()+"']";
			    	XPath xpath=XPath.newInstance(path);
			    	Element element = (Element)xpath.selectSingleNode(doc);
			    	if(element==null)
			    		return xml;
			    	else
			    	{
			    		report.removeContent(element);
			    		XMLOutputter outputter = new XMLOutputter();
			        	Format format=Format.getPrettyFormat();
			        	format.setEncoding("UTF-8");
			        	outputter.setFormat(format);
			        	newXML=outputter.outputString(doc);
			        	return newXML;
			    	}
				}
			}
			if(report==null)
			{
				report = new Element("report");
				Element r_user = new Element("r_user");
				r_user.setAttribute("name", this.userView.getUserName().toLowerCase());
				r_user.setAttribute("super", this.userView.isSuper_admin()?"1":"0");
				
				Element page = new Element("page");
				page.setAttribute("type", rpv.getPagetype()==null?"":rpv.getPagetype());
				page.setAttribute("width", rpv.getWidth()==null?"":rpv.getWidth());
				page.setAttribute("height", rpv.getHeight()==null?"": rpv.getHeight());
				page.setAttribute("range",rpv.getOrientation()==null?"":rpv.getOrientation());
				page.setAttribute("margin_top",rpv.getTop()==null?"":rpv.getTop());
				page.setAttribute("margin_left", rpv.getLeft()==null?"":rpv.getLeft());
				page.setAttribute("margin_bottom", rpv.getBottom()==null?"":rpv.getBottom());
				page.setAttribute("margin_right",rpv.getRight()==null?"":rpv.getRight());
				
				Element title = new Element("title");
				title.setAttribute("content", rpv.getTitle_fw()==null?"":rpv.getTitle_fw());
				title.setAttribute("fontface", rpv.getTitle_fn()==null?"":rpv.getTitle_fn());
				title.setAttribute("fontsize", rpv.getTitle_fz()==null?"":rpv.getTitle_fz());
				title.setAttribute("fontblob", rpv.getTitle_fb()==null?"":rpv.getTitle_fb());
				title.setAttribute("underline", rpv.getTitle_fu()==null?"":rpv.getTitle_fu());
				title.setAttribute("fontitalic", rpv.getTitle_fi()==null?"":rpv.getTitle_fi());
				title.setAttribute("dline",rpv.getTitle_fs()==null?"":rpv.getTitle_fs());
				title.setAttribute("color", rpv.getTitle_fc()==null?"":rpv.getTitle_fc());
				
				Element page_head = new Element("page_head");
				page_head.setAttribute("left", rpv.getHead_flw()==null?"":rpv.getHead_flw());
				page_head.setAttribute("center", rpv.getHead_fmw()==null?"":rpv.getHead_fmw());
				page_head.setAttribute("right",rpv.getHead_frw()==null?"":rpv.getHead_frw());
				page_head.setAttribute("fontface", rpv.getHead_fn()==null?"":rpv.getHead_fn());
				page_head.setAttribute("fontsize", rpv.getHead_fz()==null?"": rpv.getHead_fz());
				page_head.setAttribute("fontblob", rpv.getHead_fb()==null?"":rpv.getHead_fb());
				page_head.setAttribute("underline", rpv.getHead_fu()==null?"":rpv.getHead_fu());
				page_head.setAttribute("fontitalic", rpv.getHead_fi()==null?"":rpv.getHead_fi());
				page_head.setAttribute("dline", rpv.getHead_fu()==null?"":rpv.getHead_fu());
				page_head.setAttribute("color", rpv.getHead_fc()==null?"":rpv.getHead_fc());
				page_head.setAttribute("lHeadHomeShow", rpv.getHead_flw_hs()==null?"":rpv.getHead_flw_hs());
				page_head.setAttribute("mHeadHomeShow", rpv.getHead_fmw_hs()==null?"":rpv.getHead_fmw_hs());
				page_head.setAttribute("rHeadHomeShow", rpv.getHead_frw_hs()==null?"":rpv.getHead_frw_hs());
				
				Element page_tail = new Element("page_tail");
				page_tail.setAttribute("left", rpv.getTile_flw()==null?"":rpv.getTile_flw());
				page_tail.setAttribute("center", rpv.getTile_fmw()==null?"":rpv.getTile_fmw());
				page_tail.setAttribute("right",rpv.getTile_frw()==null?"":rpv.getTile_frw());
				page_tail.setAttribute("fontface", rpv.getTile_fn()==null?"":rpv.getTile_fn());
				page_tail.setAttribute("fontsize", rpv.getTile_fz()==null?"":rpv.getTile_fz());
				page_tail.setAttribute("fontblob", rpv.getTile_fb()==null?"":rpv.getTile_fb());
				page_tail.setAttribute("underline", rpv.getTile_fu()==null?"":rpv.getTile_fu());
				page_tail.setAttribute("fontitalic", rpv.getTile_fi()==null?"":rpv.getTile_fi());
				page_tail.setAttribute("dline", rpv.getTile_fs()==null?"":rpv.getTile_fs());
				page_tail.setAttribute("color", rpv.getTile_fc()==null?"":rpv.getTile_fc());
				page_head.setAttribute("lFootHomeShow", rpv.getTile_flw_hs()==null?"":rpv.getTile_flw_hs());
				page_head.setAttribute("mFootHomeShow", rpv.getTile_fmw_hs()==null?"":rpv.getTile_fmw_hs());
				page_head.setAttribute("rFootHomeShow", rpv.getTile_frw_hs()==null?"":rpv.getTile_frw_hs());
				
				Element page_main = new Element("page_main");
				page_main.setAttribute("fontface", rpv.getBody_fn()==null?"":rpv.getBody_fn());
				page_main.setAttribute("fontsize", rpv.getBody_fz()==null?"":rpv.getBody_fz());
				page_main.setAttribute("fontblob", rpv.getBody_fb()==null?"":rpv.getBody_fb());
				page_main.setAttribute("underline", rpv.getBody_fu()==null?"":rpv.getBody_fu());
				page_main.setAttribute("fontitalic", rpv.getBody_fi()==null?"":rpv.getBody_fi());
				page_main.setAttribute("color", rpv.getBody_fc()==null?"":rpv.getBody_fc());
				
				Element page_thead = new Element("page_thead");
				page_thead.setAttribute("thead_fn", rpv.getThead_fn()==null?"":rpv.getThead_fn());
				page_thead.setAttribute("thead_fz", rpv.getThead_fz()==null?"":rpv.getThead_fz());
				page_thead.setAttribute("thead_fb", rpv.getThead_fb()==null?"":rpv.getThead_fb());
				page_thead.setAttribute("thead_fu", rpv.getThead_fu()==null?"":rpv.getThead_fu());
				page_thead.setAttribute("thead_fi", rpv.getThead_fi()==null?"":rpv.getThead_fi());
				page_thead.setAttribute("thead_fc", rpv.getThead_fc()==null?"":rpv.getThead_fc());
				r_user.addContent(page_thead);
				r_user.addContent(page);
				r_user.addContent(title);
				r_user.addContent(page_head);
				r_user.addContent(page_tail);
				r_user.addContent(page_main);
				report.addContent(r_user);
				doc.getRootElement().addContent(report);
			}
			else
			{
		    	String path="/param/report/r_user[@name='"+this.userView.getUserName().toLowerCase()+"']";
		    	XPath xpath=XPath.newInstance(path);
		    	Element element = (Element)xpath.selectSingleNode(doc);
			    boolean isR_user = false;
			    boolean isPage = false;
			    boolean isTitle = false;
			    boolean isPage_head = false;
			    boolean isPage_tail = false;
			    boolean isPage_main = false;
			    boolean isPage_thead=false;
		    	if(element==null)
			   {
		    		element = new Element("r_user");
		    		isR_user=true;
		    	}
		     	element.setAttribute("name", this.userView.getUserName().toLowerCase());
		    	element.setAttribute("super", this.userView.isSuper_admin()?"1":"0");
		    	
		    	Element page = element.getChild("page");
		    	if(page==null)
		    	{
		    		page = new Element("page");
		    		isPage=true;
		    	}
		    	page.setAttribute("type", rpv.getPagetype()==null?"":rpv.getPagetype());
				page.setAttribute("width", rpv.getWidth()==null?"":rpv.getWidth());
				page.setAttribute("height", rpv.getHeight()==null?"":rpv.getHeight());
				page.setAttribute("range",rpv.getOrientation()==null?"":rpv.getOrientation());
				page.setAttribute("margin_top",rpv.getTop()==null?"":rpv.getTop());
				page.setAttribute("margin_left", rpv.getLeft()==null?"":rpv.getLeft());
				page.setAttribute("margin_bottom", rpv.getBottom()==null?"":rpv.getBottom());
				page.setAttribute("margin_right",rpv.getRight()==null?"":rpv.getRight());
				Element title = element.getChild("title");
				if(title==null)
				{
					title = new Element("title");
					isTitle = true;
				}
				title.setAttribute("content", rpv.getTitle_fw()==null?"":rpv.getTitle_fw());
				title.setAttribute("fontface", rpv.getTitle_fn()==null?"":rpv.getTitle_fn());
				title.setAttribute("fontsize", rpv.getTitle_fz()==null?"": rpv.getTitle_fz());
				title.setAttribute("fontblob", rpv.getTitle_fb()==null?"":rpv.getTitle_fb());
				title.setAttribute("underline", rpv.getTitle_fu()==null?"":rpv.getTitle_fu());
				title.setAttribute("fontitalic", rpv.getTitle_fi()==null?"":rpv.getTitle_fi());
				title.setAttribute("dline",rpv.getTitle_fs()==null?"":rpv.getTitle_fs());
				title.setAttribute("color", rpv.getTitle_fc()==null?"":rpv.getTitle_fc());
				
				Element page_head = element.getChild("page_head");
				if(page_head==null)
				{
					page_head = new Element("page_head");
					isPage_head=true;
				}
				page_head.setAttribute("left", rpv.getHead_flw()==null?"":rpv.getHead_flw());
				page_head.setAttribute("center", rpv.getHead_fmw()==null?"":rpv.getHead_fmw());
				page_head.setAttribute("right",rpv.getHead_frw()==null?"":rpv.getHead_frw());
				page_head.setAttribute("fontface", rpv.getHead_fn()==null?"":rpv.getHead_fn());
				page_head.setAttribute("fontsize", rpv.getHead_fz()==null?"":rpv.getHead_fz());
				page_head.setAttribute("fontblob", rpv.getHead_fb()==null?"":rpv.getHead_fb());
				page_head.setAttribute("underline", rpv.getHead_fu()==null?"":rpv.getHead_fu());
				page_head.setAttribute("fontitalic", rpv.getHead_fi()==null?"":rpv.getHead_fi());
				page_head.setAttribute("dline", rpv.getHead_fu()==null?"":rpv.getHead_fu());
				page_head.setAttribute("color", rpv.getHead_fc()==null?"": rpv.getHead_fc());
				page_head.setAttribute("lHeadHomeShow", rpv.getHead_flw_hs()==null?"":rpv.getHead_flw_hs());
				page_head.setAttribute("mHeadHomeShow", rpv.getHead_fmw_hs()==null?"":rpv.getHead_fmw_hs());
				page_head.setAttribute("rHeadHomeShow", rpv.getHead_frw_hs()==null?"":rpv.getHead_frw_hs());
				
				Element page_tail=element.getChild("page_tail");
				if(page_tail==null)
				{
					page_tail = new Element("page_tail");
					isPage_tail = true;
				}
				page_tail.setAttribute("left", rpv.getTile_flw()==null?"":rpv.getTile_flw());
				page_tail.setAttribute("center", rpv.getTile_fmw()==null?"":rpv.getTile_fmw());
				page_tail.setAttribute("right",rpv.getTile_frw()==null?"":rpv.getTile_frw());
				page_tail.setAttribute("fontface", rpv.getTile_fn()==null?"":rpv.getTile_fn());
				page_tail.setAttribute("fontsize", rpv.getTile_fz()==null?"":rpv.getTile_fz());
				page_tail.setAttribute("fontblob", rpv.getTile_fb()==null?"":rpv.getTile_fb());
				page_tail.setAttribute("underline", rpv.getTile_fu()==null?"":rpv.getTile_fu());
				page_tail.setAttribute("fontitalic", rpv.getTile_fi()==null?"":rpv.getTile_fi());
				page_tail.setAttribute("dline", rpv.getTile_fs()==null?"":rpv.getTile_fs());
				page_tail.setAttribute("color", rpv.getTile_fc()==null?"":rpv.getTile_fc());
				page_tail.setAttribute("lFootHomeShow", rpv.getTile_flw_hs()==null?"":rpv.getTile_flw_hs());
				page_tail.setAttribute("mFootHomeShow", rpv.getTile_fmw_hs()==null?"":rpv.getTile_fmw_hs());
				page_tail.setAttribute("rFootHomeShow", rpv.getTile_frw_hs()==null?"":rpv.getTile_frw_hs());
				Element page_main = element.getChild("page_main");
				if(page_main==null)
				{
					page_main = new Element("page_main");
					isPage_main=true;
				}
				page_main.setAttribute("fontface", rpv.getBody_fn()==null?"":rpv.getBody_fn());
				page_main.setAttribute("fontsize", rpv.getBody_fz()==null?"":rpv.getBody_fz());
				page_main.setAttribute("fontblob", rpv.getBody_fb()==null?"":rpv.getBody_fb());
				page_main.setAttribute("underline", rpv.getBody_fu()==null?"":rpv.getBody_fu());
				page_main.setAttribute("fontitalic", rpv.getBody_fi()==null?"":rpv.getBody_fi());
				page_main.setAttribute("color", rpv.getBody_fc()==null?"":rpv.getBody_fc());
				Element page_thead = element.getChild("page_thead");
				if(page_thead==null)
				{
					page_thead = new Element("page_thead");
					isPage_thead=true;
				}
				page_thead.setAttribute("thead_fn", rpv.getThead_fn()==null?"":rpv.getThead_fn());
				page_thead.setAttribute("thead_fz", rpv.getThead_fz()==null?"":rpv.getThead_fz());
				page_thead.setAttribute("thead_fb", rpv.getThead_fb()==null?"":rpv.getThead_fb());
				page_thead.setAttribute("thead_fu", rpv.getThead_fu()==null?"":rpv.getThead_fu());
				page_thead.setAttribute("thead_fi", rpv.getThead_fi()==null?"":rpv.getThead_fi());
				page_thead.setAttribute("thead_fc", rpv.getThead_fc()==null?"":rpv.getThead_fc());
				if(isPage_main)
					element.addContent(page_main);
				if(isPage_tail)
					element.addContent(page_tail);
				if(isPage_head)
					element.addContent(page_head);
				if(isTitle)
					element.addContent(title);
				if(isPage)
					element.addContent(page);
				if(isPage_thead)
					element.addContent(page_thead);
				if(isR_user)
					report.addContent(element);
			}
			XMLOutputter outputter = new XMLOutputter();
        	Format format=Format.getPrettyFormat();
        	format.setEncoding("UTF-8");
        	outputter.setFormat(format);
        	newXML=outputter.outputString(doc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return newXML;
	}
	
	/**
	 * 持久化新参数
	 * @param xml
	 */
	public void saveXML(String xml){
		PreparedStatement pstmt = null;		
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer strsql=new StringBuffer();
		try{
			{
				strsql.append("update reportdetail set ctrlparam=? where rsid=? and rsdtlid=?");
				pstmt = this.conn.prepareStatement(strsql.toString());	
				switch(Sql_switcher.searchDbServer())
				{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, xml);
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xml.getBytes())), xml.length());
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(xml.getBytes())), xml.length());
					  break;
				  }
				}
				pstmt.setString(2,this.rsid);
				pstmt.setString(3,this.rsdtlid);
			}
			
			// 打开Wallet
			dbS.open(conn, strsql.toString());
			pstmt.executeUpdate();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				// 关闭Wallet
				dbS.close(conn);
				if(pstmt!=null)
				{
					pstmt.close();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 持久化新参数,reportdetail的所有数据
	 * @param xml
	 */
	public void saveXML(String xml,ReportParseVo reportParseVo){
		PreparedStatement pstmt = null;		
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo reportdetailvo=new RecordVo("reportdetail");
			reportdetailvo.setInt("rsid",Integer.parseInt(rsid));
			reportdetailvo.setInt("rsdtlid",Integer.parseInt(rsdtlid));
			reportdetailvo.setString("papername", reportParseVo.getPagetype());
			reportdetailvo.setInt("paperwidth", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getWidth())?reportParseVo.getWidth():"0"));
			reportdetailvo.setInt("paperheight", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getHeight())?reportParseVo.getHeight():"0"));
			reportdetailvo.setInt("orientation", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getOrientation())?reportParseVo.getOrientation():"0"));
			reportdetailvo.setString("title", reportParseVo.getTitle_fw());
			reportdetailvo.setString("titlefontname", reportParseVo.getTitle_fn());
			reportdetailvo.setString("lhead", reportParseVo.getHead_flw());
			reportdetailvo.setString("mhead", reportParseVo.getHead_fmw());
			reportdetailvo.setString("rhead", reportParseVo.getHead_frw());
			reportdetailvo.setString("lfoot", reportParseVo.getTile_flw());
			reportdetailvo.setString("mfoot", reportParseVo.getTile_fmw());
			reportdetailvo.setString("rfoot", reportParseVo.getTile_frw());
			reportdetailvo.setString("headfontname", reportParseVo.getHead_fn());
			reportdetailvo.setString("bodyfontname", reportParseVo.getBody_fn());
			reportdetailvo.setInt("leftmargin", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getLeft())?reportParseVo.getLeft():"0"));
			reportdetailvo.setInt("rightmargin", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getRight())?reportParseVo.getRight():"0"));
			reportdetailvo.setInt("topmargin", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getTop())?reportParseVo.getTop():"0"));
			reportdetailvo.setInt("bottommargin", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getBottom())?reportParseVo.getBottom():"0"));
			reportdetailvo.setInt("titlefontsize", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getThead_fz())?reportParseVo.getThead_fz():"0"));
			reportdetailvo.setString("ctrlparam", xml);
			int titleFontStyle = 0;//标题样式
			if(StringUtils.isNotBlank(reportParseVo.getTitle_fi()))
				titleFontStyle = 1;//斜体
			
			if(StringUtils.isNotBlank(reportParseVo.getTitle_fi())) {
				if(titleFontStyle == 1)
					titleFontStyle = 3;//粗斜体
				else
					titleFontStyle = 2;//粗体
			}
			int HeadFontStyle = 0;//表头样式
			if(StringUtils.isNotBlank(reportParseVo.getThead_fi()))
				HeadFontStyle = 1;//斜体
			
			if(StringUtils.isNotBlank(reportParseVo.getThead_fi())) {
				if(HeadFontStyle == 1)
					HeadFontStyle = 3;//粗斜体
				else
					HeadFontStyle = 2;//粗体
			}
			int BodyFontStyle = 0;//内容样式
			if(StringUtils.isNotBlank(reportParseVo.getBody_fi()))
				BodyFontStyle = 1;//斜体
			
			if(StringUtils.isNotBlank(reportParseVo.getBody_fi())) {
				if(BodyFontStyle == 1)
					BodyFontStyle = 3;//粗斜体
				else
					BodyFontStyle = 2;//粗体
			}
			
			reportdetailvo.setInt("titlefontstyle", titleFontStyle);
			reportdetailvo.setInt("headfontstyle", HeadFontStyle);
			reportdetailvo.setInt("bodyfontstyle", BodyFontStyle);
			reportdetailvo.setInt("headfontsize", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getHead_fz())?reportParseVo.getHead_fz():"0"));
			reportdetailvo.setInt("bodyfontsize", Integer.parseInt(StringUtils.isNotBlank(reportParseVo.getBody_fz())?reportParseVo.getBody_fz():"0"));
			
			dao.updateValueObject(reportdetailvo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				// 关闭Wallet
				dbS.close(conn);
				if(pstmt!=null)
				{
					pstmt.close();
				}
			}catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 判断是否设置了参数
	 * @param type 区别是在页面设置，还是输出excel
	 * @return
	 */
	public boolean isHavePageOptions(int type)
	{
		boolean flag=false;
		try
		{
			if(doc!=null)
			{
				 String path="/param/report/r_user[@name='"+this.userView.getUserName().toLowerCase()+"']";
				 XPath xpath=XPath.newInstance(path);
				 Element element = (Element)xpath.selectSingleNode(doc);
				 if(element==null&&type==2)
				 {
					 path="/param/report/r_user[@super='1']";
					 xpath=XPath.newInstance(path);
					 //element = (Element)xpath.selectSingleNode(doc);
					 ArrayList list = (ArrayList)xpath.selectNodes(doc);
					 if(list!=null&&list.size()>0)
						 element=(Element)list.get(0);//多个设置取第一个
				 }
				 if(element!=null)
					 flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 初始化Document
	 */
	public void init()
	{
		RowSet rs = null;
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			String xml = "";
			ContentDAO dao  = new ContentDAO(this.conn);
			String sql = "select ctrlparam from reportdetail where rsid=?";
			list.add(this.rsid);
			if(!"4".equals(this.rsid)) {
				sql+=" and rsdtlid=?";
				list.add(this.rsdtlid);
			}
			rs = dao.search(sql,list);
			while(rs.next())
			{
				xml = Sql_switcher.readMemo(rs,"ctrlparam");
			}
			if(xml==null|| "".equals(xml))
    		{
    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
    		}
			doc =PubFunc.generateDom(xml);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 判断是否设置了分组分页打印
	 * @param rsid
	 * @param rsdtlid
	 * @param type
	 * @return
	 */
	public boolean isGroupPrint(String rsid,String rsdtlid,int type)
	{
		boolean flag = false;
		RowSet rs = null;
		try
		{
			StringBuffer buf  = new StringBuffer();
			if(type==0)//report
			{
				buf.append("select bgroup,ctrlparam from reportdetail where bgroup='1' and rsid="+rsid);
				if(!"4".equals(rsid))
					buf.append(" and rsdtlid = '"+rsdtlid+"'");
			}
			else//analyse
			{
				buf.append("select bgroup from reportdetail where rsid='"+rsid+"' and rsdtlid='"+rsdtlid+"'");
				
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				if(type==0)
				{
					String xml = Sql_switcher.readMemo(rs,"ctrlparam");
					if(!"".equals(xml.trim()))
					{
						Document doc = null;
						doc =PubFunc.generateDom(xml);
						Element group =doc.getRootElement().getChild("group");
						if(group!=null)
						{
							String pagebreak = group.getAttributeValue("pagebreak");
							if("true".equalsIgnoreCase(pagebreak))
								flag = true;
						}
					}
				}
				else
				{
					String bgroup = rs.getString("bgroup");
					if(bgroup!=null&& "1".equals(bgroup))
						flag=true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
