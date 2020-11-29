package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.businessobject.general.muster.hmuster.CustomReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.dataview.businessobject.DataViewBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class GzReportTree {
	String flag="0";  // 0: 薪资发放  1:福利管理
	String rsid="";   // 报表种类编号
	String salaryid="";  //薪资类别id
	UserView view=null;
	
	public GzReportTree(String flag,String rsid,String salaryid,UserView view)
	{
		this.rsid=rsid;
		this.flag=flag;
		this.salaryid=salaryid;
		this.view=view;
	}
	
	
	
	public String outPutXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		Connection conn=null;
		try
		{
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
	//	root.setAttribute("href", "javascript:hid()");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		ArrayList list =getChildList();
		
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String id = (String) abean.get("id");
			String name = (String) abean.get("name");
			if(!"".equals(this.rsid))
			{
				
			//	child.setAttribute("defaultInput","1");
				/**报表*/
				if(id.indexOf("m")==-1)
				{
					child.setAttribute("id", id+"#1");
					child.setAttribute("text",name);
					child.setAttribute("title",name);
				      	child.setAttribute("href", "javascript:showEidtButton()");
					
				}
				/**名册*/
				else
				{
					child.setAttribute("id", id.substring(1)+"#1");
					child.setAttribute("text",name);
					child.setAttribute("title",name);
					child.setAttribute("href", "javascript:showOpenButton()");
				}
					
					child.setAttribute("target", "_self"); 
					child.setAttribute("icon","/images/table.gif");
					
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}
			/**父表类*/
			else
			{
				
			//	child.setAttribute("defaultInput","1");
				child.setAttribute("id",id+"#0");
				child.setAttribute("text", name);
				child.setAttribute("title", name);
				if(!"4".equals(id)&&!"0".equals(id))
				{
						child.setAttribute("href", "javascript:showNewButton()");
				}
				else
				{
					if("4".equals(id))
						child.setAttribute("href", "javascript:showOpenButton()");
					else 
						child.setAttribute("href", "javascript:showOpenButton2()");
				}
				/**有新建权限时全部显示，否则当有下级时才显示*/
				if(!"0".equals(id) && !"4".equals(id))
				{
					if(!this.view.isSuper_admin()&&!"1".equals(this.view.getGroupId()))
					{
						if(!this.view.hasTheFunction("32713080201")&&
							!this.view.hasTheFunction("32712050301")&&
							!this.view.hasTheFunction("32703090201")&&
							!this.view.hasTheFunction("32702050301")&&
							!this.view.hasTheFunction("32503100201")&&
							!this.view.hasTheFunction("32502050301")&&
							!this.view.hasTheFunction("32403100101")&&
							!this.view.hasTheFunction("32402050301")&&!this.isHaveChild(id,this.view.getUserName()))
						{
							continue;
						}					
					}
					
				}
				child.setAttribute("target", "_self"); 
				child.setAttribute("icon","/images/prop_ps.gif");	
				String a_xml="/gz/gz_accounting/report/gzReportTree.jsp?flag="+this.flag+"&rsid="+id+"&salaryid="+this.salaryid;
				if(!"4".equals(id))
					child.setAttribute("xml", a_xml);
			
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}
		}
		if(this.rsid!=null&& "0".equals(this.rsid))
		{
			String nmodule="34";
			//conn = AdminDb.getConnection();
			//保险
			if("1".equals(this.flag))
			{
				nmodule="39";
			}
			conn = AdminDb.getConnection();
			CustomReportBo bo = new CustomReportBo(conn,this.view,nmodule);
			DataViewBo dataViewBo = new DataViewBo(conn,this.view,nmodule);
			ArrayList<LazyDynaBean> crlist = bo.getCustomReportList();
			crlist.addAll(dataViewBo.createDataUrl());
			for(int j=0;j<crlist.size();j++)
			{
				LazyDynaBean bean = (LazyDynaBean)crlist.get(j);
				String report_type=(String)bean.get("report_type");
				String urlLink=(String)bean.get("url");
				String ntype="0";//区分自定义表，还是花名册
				if("3".equals(report_type))
				{
					RecordVo vo = (RecordVo)bean.get("vo");
					int muster_nmodule=vo.getInt("nmodule");
					if(muster_nmodule==3)//人员名册
					{
						ntype="3";
					}
					else if(muster_nmodule==21)//机构名册
					{
						ntype="21";
					}else if(muster_nmodule==41)//职位
					{
						ntype="41";
					}
				}else if("4".equals(report_type)) {
					if(StringUtils.isNotBlank(urlLink))//通过判断url是否存在输出简单报表
						ntype="51";//简单名册
					else
						continue;
				}
				String link_tabid=(String)bean.get("link_tabid");
				String id=(String)bean.get("id");
				String ext=(String)bean.get("ext");
				String name=(String)bean.get("name");
				String description=(String)bean.get("description");
				Element child = new Element("TreeNode");
				if("0".equals(ntype))
				{
				    child.setAttribute("id", id);
				}
				else if("51".equals(ntype))
				{
					child.setAttribute("id", urlLink);
				}
				else
				{
					 child.setAttribute("id", link_tabid);
				}
				child.setAttribute("text",name);
				child.setAttribute("title",name);
				if("0".equals(ntype)){
					if(".html".equalsIgnoreCase(ext)|| ".htm".equalsIgnoreCase(ext)){
						child.setAttribute("href", "javascript:showOpenCustomButton()");
					}
					else if(".xls".equalsIgnoreCase(ext)|| ".xlsx".equalsIgnoreCase(ext)|| ".xlt".equalsIgnoreCase(ext)|| ".xltx".equalsIgnoreCase(ext))
					{
						child.setAttribute("href", "javascript:showOpenCustomXLSButton()");
					}
				}else if("3".equals(ntype)){
					child.setAttribute("href", "javascript:showOpenMusterOneButton()");
				}else if("21".equals(ntype)){
					child.setAttribute("href", "javascript:showOpenMusterTwoButton()");
				}else if("41".equals(ntype)){
					child.setAttribute("href", "javascript:showOpenMusterThreeButton()");
				}else if("51".equals(ntype)) {
					child.setAttribute("href", "javascript:showSimpleMusterButton()");
				}
				child.setAttribute("target", "_self"); 
				child.setAttribute("icon","/images/table.gif");
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}
		}

		XMLOutputter outputter = new XMLOutputter();
		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn!=null)
			{
			   try
			   {
				   conn.close();
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
			}
		}
		return xmls.toString();
	}
	
	
	private ArrayList getChildList()
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			String sql="";
			if("".equals(this.rsid))
			{
				if("0".equals(this.flag))
					sql="select * from reportstyle where rsid in (1,2,3,4) order by rsid";
				else
					sql="select * from reportstyle where rsid in (12,13)";
			}
			else{
				if(!"0".equals(this.rsid))
					sql="select * from reportdetail where rsid="+this.rsid+" and stid="+this.salaryid;
				else
				{
					if("0".equals(this.flag))
						sql="select * from muster_name where nModule=14 and (nPrint=-1 or nPrint="+this.salaryid+")  order by tabid";
					else if("1".equals(this.flag))
						sql="select * from muster_name where nModule=11 and (nPrint=-1 or nPrint="+this.salaryid+")  order by tabid";
				}
			}
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean aBean=new LazyDynaBean();
				if("".equals(this.rsid))
				{
					/*if(rs.getString("rsid").equals("4")||this.isHaveChild(rs.getString("rsid")))
					{*/
				    	aBean.set("id",rs.getString("rsid"));
				    	aBean.set("name",rs.getString("rsname"));
					    list.add(aBean);
					/*}*/
				}
				else
				{
					if(!"0".equals(this.rsid)){
						String xml = Sql_switcher.readMemo(rs, "ctrlParam");
						boolean  flag= this.analyseXML(xml, this.view.getUserName());
						if(this.view.isSuper_admin()|| "1".equals(this.view.getGroupId())||flag)
						{
					    	aBean.set("id",rs.getString("rsdtlid"));
					    	aBean.set("name",rs.getString("rsdtlname"));
					    	list.add(aBean);
						}
					}
					else
					{
						if(this.view.isSuper_admin()|| "1".equals(this.view.getGroupId())||this.view.isHaveResource(IResourceConstant.HIGHMUSTER, rs.getString("tabid")))//用户自定义表，加上权限限制
						{
				    		aBean.set("id","m"+rs.getString("tabid"));
			    			aBean.set("name",rs.getString("cname"));
			    			list.add(aBean);
						}
					}
				}
				
			}
			
			if("".equals(this.rsid))
			{
				LazyDynaBean aBean=new LazyDynaBean();
				aBean.set("id","0");
				aBean.set("name","用户自定义表");
				list.add(aBean);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	public boolean isHaveChild(String id,String userName)
	{
		boolean isHave=false;
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			String sql="select * from reportdetail where rsid="+id+" and stid="+this.salaryid;
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				String xml=Sql_switcher.readMemo(rs,"ctrlParam");
				boolean flag = this.analyseXML(xml, userName);
				if(flag)
				{
					isHave=true;
					break;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isHave;
	}
    public boolean analyseXML(String xml,String userName)
    {
    	boolean flag = true;
    	try
    	{
    		if(xml==null|| "".equals(xml))
    		{
    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
    		}
    		Document doc = PubFunc.generateDom(xml);
			Element root=doc.getRootElement();
	    	XPath xpath=XPath.newInstance("/"+root.getName()+"/owner");
    		Element element=(Element)xpath.selectSingleNode(doc);
    		if(element==null)
    			return flag;
    		else{
    			String type = element.getAttributeValue("type");
    			if("1".equals(type))
    			{
    				String text = element.getText();
    				if(text.equalsIgnoreCase(userName))
    					flag=true;
    				else
    					flag=false;
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return flag;
    }
}
