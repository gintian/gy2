package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.businessobject.general.muster.hmuster.CustomReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.dataview.businessobject.DataViewBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
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
import java.util.ArrayList;
import java.util.Iterator;

public class GzAnalyseTree {
	String rsid;
	String gz_module;
	UserView view;
	public GzAnalyseTree(String rsid,String gz_module,UserView view)
	{
		this.rsid=rsid;
		this.gz_module = gz_module;
		this.view=view;
	}
	public String outPutXmlStr()
	{
		StringBuffer xml = new StringBuffer();
		Connection conn=null;
		try
		{
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "organization");
			// 创建xml文档自身
			Document myDocument = new Document(root);
			
			ArrayList list =getChildList();
			for(Iterator t = list.iterator(); t.hasNext();) 
			{
				LazyDynaBean bean = (LazyDynaBean)t.next();
				Element child = new Element("TreeNode");
				String id=(String)bean.get("id");
				String name=(String)bean.get("name");
				if(!"".equals(this.rsid))
				{
					child.setAttribute("id", id);
					child.setAttribute("text",name);
					child.setAttribute("title",name);
				    if("12".equals(this.rsid))
					{
						child.setAttribute("href", "javascript:openBbButton()");
					}
					else
					{
    					child.setAttribute("href", "javascript:showEidtButton()");
					}
					child.setAttribute("target", "_self"); 
					child.setAttribute("icon","/images/table.gif");
					// 将子元素作为内容添加到根元素
					root.addContent(child);
				}
				else
				{
					child.setAttribute("id",id);
					child.setAttribute("text", name);
					child.setAttribute("title", name);
					if("8".equals(id)|| "9".equals(id)|| "12".equals(id)|| "17".equals(id))
					{
						child.setAttribute("href", "javascript:closeButton()");
					}
					else
					{
			    		child.setAttribute("href", "javascript:showNewButton()");
					}
					child.setAttribute("target", "_self"); 
					child.setAttribute("icon","/images/prop_ps.gif");	
					//---------------------------------------------------
					String a_xml="/gz/gz_analyse/gzAnalyseTree.jsp?&rsid="+id+"&gz_module="+this.gz_module;
					child.setAttribute("xml", a_xml);
				
					// 将子元素作为内容添加到根元素
					root.addContent(child);
					
				}			
			}
			if("12".equalsIgnoreCase(this.rsid))
    		{
    			String nmodule="35";
    			//保险
    			if("1".equals(gz_module))
    			{
    				nmodule="42";
    			}
    			conn = AdminDb.getConnection();
    			CustomReportBo bo = new CustomReportBo(conn,this.view,nmodule);
    			DataViewBo dataViewBo = new DataViewBo(conn,this.view,nmodule);
    			ArrayList<LazyDynaBean> crlist = bo.getCustomReportList();
    			crlist.addAll(dataViewBo.createDataUrl());
    			for(int j=0;j<crlist.size();j++)
    			{
    				LazyDynaBean bean = (LazyDynaBean)crlist.get(j);
    				String ext=(String)bean.get("ext");
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
    		
			/**工资进展分析单提出来了**/
			/*if(this.rsid.equals("")&&this.gz_module.equals("0"))
			{
				Element child = new Element("TreeNode");
				child.setAttribute("id", "id");
				child.setAttribute("text","工资发放进展表");
				child.setAttribute("title","工资发放进展表");
				child.setAttribute("href", "javascript:openGzFareAnalyse()");
				child.setAttribute("target", "_self"); 
				child.setAttribute("icon","/images/prop_ps.gif");
				root.addContent(child);
			}*/
			
			XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			// 将生成的XML文件作为字符串形式
			xml.append(outputter.outputString(myDocument));

		}
		catch(Exception e)
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
		return xml.toString();
	}
	private ArrayList getChildList()
	{
		ArrayList list = new ArrayList();
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			/*if(this.rsid==null||this.rsid.equals("")&&this.gz_module.equals("1"))
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id","12");
				bean.set("name","用户自定义表");
				list.add(bean);
			}
			else
			{*/
	    		String sql="";
		    	if(this.rsid==null|| "".equals(this.rsid))
		    	{
		    		if("0".equals(this.gz_module))
		    	    	sql="select * from reportstyle where rsid>=5 and rsid<12";
		    		else
		    			sql="select * from reportstyle where rsid=14  or rsid=15 or rsid=16 or rsid=17";
		    	}
		    	else
	    		{
	    			if("12".equalsIgnoreCase(this.rsid))
	    			{
		    			if("0".equals(this.gz_module))
		     	     		sql = "select tabid,cname from muster_name where nModule=6 and nPrint='-1' order by norder";
		    			else
		    				sql = "select tabid,cname from muster_name where nModule=8 and nPrint='-1' order by norder";
					
		    		}else
		    		{
			        	sql="select * from reportdetail where stid=0 and rsid="+this.rsid;
		    		}
		    	}
		    	conn = AdminDb.getConnection();
		    	ContentDAO dao = new ContentDAO(conn);
	    		rs=dao.search(sql);
	    		while(rs.next())
	    		{
		    		LazyDynaBean bean = new LazyDynaBean();
		    		if(this.rsid==null|| "".equals(this.rsid))
		    		{
		    			
		    			if(!view.isSuper_admin()&&!view.isHaveResource(IResourceConstant.GZ_REPORT_STYLE, rs.getString("rsid")))
		    				continue;
		    			bean.set("id",rs.getString("rsid"));
		    			bean.set("name",rs.getString("rsname"));
		    			list.add(bean);
		    		} 
		    		else if("12".equalsIgnoreCase(this.rsid))
		    		{
		    			if(this.view.isHaveResource(IResourceConstant.HIGHMUSTER, rs.getString("tabid")))//用户自定义表，加上权限限制
		    			{	
		    		    	bean.set("id",rs.getString("tabid"));
		    		    	bean.set("name",rs.getString("cname"));
		    		    	list.add(bean);
		    			}
		    		}
		    		else
		    		{
		    			String xml = Sql_switcher.readMemo(rs, "ctrlParam");
						boolean  flag= this.analyseXML(xml, this.view.getUserName());
						if(this.view.isSuper_admin()|| "1".equals(this.view.getGroupId())||flag)
						{
		    		    	bean.set("id",rs.getString("rsdtlid"));
		    		    	bean.set("name",rs.getString("rsdtlname"));
		    		    	list.add(bean);
						}
	    			}
				
	    		}
	    		if(this.rsid==null|| "".equals(this.rsid))
	    		{
	        		LazyDynaBean bean = new LazyDynaBean();
	         		bean.set("id","12");
	        		bean.set("name","用户自定义表");
	        		list.add(bean);
	    		}
			/*}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		
		return list;
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
