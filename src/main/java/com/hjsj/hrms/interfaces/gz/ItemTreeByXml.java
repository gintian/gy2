package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class ItemTreeByXml {
	String type;      // 0:横向栏目 1:横向子栏目  2:纵向栏目  3:纵向子栏目  4:结果指标   5:代码树 6：指标熟
	String flag;	  // 0:库 1：指标集 2：指标  3：代码  4：UN  5:UM  6@K  7:数字  8: 日期
	String id;
	
	public ItemTreeByXml(String flag,String id,String type)
	{
		this.type=type;
		this.flag=flag;
		/* 安全问题：薪资标准/新建薪资表：新建横、纵向栏目时，选择关联UM、UN指标代码时，系统显示错误 xiaoyun 2014-10-16 start */
		//this.id=id;
		this.id = PubFunc.keyWord_reback(id);
		/* 安全问题：薪资标准/新建薪资表：新建横、纵向栏目时，选择关联UM、UN指标代码时，系统显示错误 xiaoyun 2014-10-16 end */
	}
	

	
	public String outPutXml() throws GeneralException {

		// 生成的XML文件
		StringBuffer xmls = new StringBuffer();
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		// 设置跳转字符串
	//	String theaction = "javascript:dddd()";
	//	String theaction = "javascript:reloadNode()";
		ArrayList list =new ArrayList();
		if(!"5".equals(type)&&!"6".equals(type))
			list=getInfoList();
		else 
		{
			list=getCodeInfoList();
		}
		
		
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			if(!"5".equals(type)&&!"6".equals(type))
			{
				String hasChild=(String)abean.get("hasChild");
				String sub_flag=(String)abean.get("sub_flag");
				String subNode=(String)abean.get("subNode");
				String itemtype=(String)abean.get("itemtype");
				child.setAttribute("defaultInput","1");
				child.setAttribute("id", this.flag+"~"+codeitemid);
				child.setAttribute("text", codeitemdesc);
				child.setAttribute("title", codeitemdesc);
				
				if("N".equals(itemtype)|| "D".equals(itemtype))
					child.setAttribute("href", "javascript:showEidtButton1()");
				else if("E".equals(itemtype))
					child.setAttribute("href", "javascript:showEidtButton2()");
				else 
					child.setAttribute("href", "javascript:hideButton()");
				
				child.setAttribute("target", "_self"); 
				if("1".equals(subNode))
					child.setAttribute("icon","/images/admin.gif");
				else
					child.setAttribute("icon","/images/close.png");
					
				String a_xml="/gz/templateset/standard/select_item_tree.jsp?type="+this.type+"&flag="+sub_flag+"&id="+codeitemid;
				if("1".equals(hasChild))
		        	child.setAttribute("xml", a_xml);
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}
			else
			{
				String isSub=(String)abean.get("isSub");
				child.setAttribute("defaultInput","1");
				child.setAttribute("id",codeitemid);
				child.setAttribute("text", codeitemdesc);
				child.setAttribute("title", codeitemdesc);
				child.setAttribute("target", "_self"); 
				child.setAttribute("icon","/images/admin.gif");				
				String a_xml="/gz/templateset/standard/select_item_tree.jsp?type="+this.type+"&flag="+this.flag+"&id="+codeitemid;
				if("1".equals(isSub))
		        	child.setAttribute("xml", a_xml);
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
		return xmls.toString();
	}
	
	
	public ArrayList getCodeInfoList()
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			if("6".equals(type))
			{
				rs=dao.search("select * from gz_stand_date where item='"+this.id+"'  order by cast(item_id as int)");
				while(rs.next())
				{
					LazyDynaBean aBean=new LazyDynaBean();
					aBean.set("codeitemid","#"+rs.getString("item_id"));
					aBean.set("codeitemdesc",rs.getString("description"));
					aBean.set("isSub","0");
					list.add(aBean);
				}
			}
			else if("5".equals(type))
			{
				//3：代码  4：UN  5:UM  6@K
				if("3".equals(this.flag))
				{
				    String itemid="";
					if(id.indexOf("#")==-1)
					{
						rs=dao.search("select * from codeitem where codesetid='"+this.id+"' and codeitemid=parentid  order by a0000,codeitemid");
						itemid=this.id;
					}
					else
					{
						String[] temps=this.id.split("#");				
						itemid=temps[0];
						rs=dao.search("select * from codeitem where  codesetid='"+temps[0]+"' and parentid='"+temps[1]+"' and parentid<>codeitemid   order by a0000,codeitemid");
					}
					while (rs.next()) {
						String  id=rs.getString("codeitemid");
						String codesetid=rs.getString("codesetid");
						String  desc=rs.getString("codeitemdesc");
						String childid=rs.getString("childid");
						
						id=codesetid+"#"+id;
							
						LazyDynaBean aBean=new LazyDynaBean();
						aBean.set("codeitemid",id);
						aBean.set("codeitemdesc",desc);
						if(childid.equals(rs.getString("codeitemid")))
							aBean.set("isSub","0");
						else
							aBean.set("isSub","1");
						
						list.add(aBean);
					}
				}
				else if("4".equals(this.flag)|| "5".equals(this.flag)|| "6".equals(this.flag))
				{
					if(id.indexOf("#")==-1)
						rs=dao.search("select * from organization where codesetid='UN' and parentid=codeitemid and codeitemid<>childid order by codeitemid");
					else
					{
						String[] temps=this.id.split("#");
						if("4".equals(this.flag))  //UN
							rs=dao.search("select * from organization where codesetid='UN' and parentid='"+temps[1]+"' and parentid<>codeitemid  ");
						else if("5".equals(this.flag)) //UM
							rs=dao.search("select * from organization where codesetid<>'@K' and parentid='"+temps[1]+"' and parentid<>codeitemid  ");
						else if("6".equals(this.flag)) //@K
							rs=dao.search("select * from organization where  parentid='"+temps[1]+"' and parentid<>codeitemid  ");
					}
					
					while (rs.next()) {
						String  id=rs.getString("codeitemid");
						String codesetid=rs.getString("codesetid");
						String  desc=rs.getString("codeitemdesc");
						String childid=rs.getString("childid");
						
                        id=codesetid+"#"+id;
                        LazyDynaBean aBean=new LazyDynaBean();
						aBean.set("codeitemid",id);
						aBean.set("codeitemdesc",desc);
						if(childid.equals(rs.getString("codeitemid")))
							aBean.set("isSub","0");
						else
							aBean.set("isSub","1");
						list.add(aBean);	
					}
				}	
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
	
	
	public LazyDynaBean getLazyDynaBean(String id,String desc,String hasChild,String sub_flag,String subNode,String itemtype)
	{
		LazyDynaBean lazyDynaBean=new LazyDynaBean();
		lazyDynaBean.set("codeitemid",id);
		lazyDynaBean.set("codeitemdesc",desc);
		lazyDynaBean.set("hasChild",hasChild);
		lazyDynaBean.set("sub_flag",sub_flag);
		lazyDynaBean.set("subNode",subNode);
		lazyDynaBean.set("itemtype",itemtype);
		return lazyDynaBean;
	}
	
	
	public ArrayList getInfoList()
	{
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH )+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
        // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		ArrayList list=new ArrayList();
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			// SQL语句
			StringBuffer strsql = new StringBuffer("");
			if("0".equals(this.flag))  //0:库
			{
				
				list.add(getLazyDynaBean("A","人员库","1","1","0",""));
				list.add(getLazyDynaBean("B","单位库","1","1","0",""));
				list.add(getLazyDynaBean("K","岗位库","1","1","0",""));
			}
			else if("1".equals(this.flag))
			{
				rs = dao.search("select * from fieldset where  fieldsetid like '"+this.id+"%' order by displayorder"); // and useflag<>'0'
				while (rs.next()) {
					String  id=rs.getString("fieldsetid");
					String  desc=rs.getString("customdesc");
					list.add(getLazyDynaBean(id,desc,"1","2","0",""));
				}
			}
			else if("2".equals(this.flag))			 // 指标
			{
				String sql="select * from fielditem where fieldsetid='"+this.id+"' "; // and useflag<>'0' "; 
				if("0".equals(this.type)|| "2".equals(this.type)) //代码型指标
				{
					sql+=" and codesetid<>'0'";
				}
				else if("1".equals(this.type)|| "3".equals(this.type)) //代码、日期及数值型指标
				{
					sql+=" and ( codesetid<>'0' or itemtype='D' or itemtype='N' )";
				}
				else if("4".equals(this.type))//代码型及数值型指标
				{
					sql+=" and ( codesetid<>'0' or itemtype='N' )";
				}
				sql+=" order by displayid";
				rs = dao.search(sql);
				
				boolean isB0110=false;
				boolean isE0122=false;
				boolean isE01A1=false;
				
				while (rs.next()) {
					String  id=rs.getString("itemid");
					
					if("b0110".equalsIgnoreCase(id))
						isB0110=true;
					else if("E0122".equalsIgnoreCase(id))
						isE0122=true;
					else if("E01A1".equalsIgnoreCase(id))
						isE01A1=true;
					
					String  desc=rs.getString("itemdesc");
					String  itemtype=rs.getString("itemtype");
					String  codesetid=rs.getString("codesetid");
					if("0".equals(this.type)|| "2".equals(this.type)) //代码型指标
					{
						if(!"UN".equals(codesetid)&&!"UM".equals(codesetid)&&!"@K".equals(codesetid))
							list.add(getLazyDynaBean(id,desc,"1","3","0",itemtype));
						else if("UN".equals(codesetid))
							list.add(getLazyDynaBean(id,desc,"1","4","0",itemtype));
						else if("UM".equals(codesetid))
							list.add(getLazyDynaBean(id,desc,"1","5","0",itemtype));
						else if("@K".equals(codesetid))
							list.add(getLazyDynaBean(id,desc,"1","6","0",itemtype));
					
					}
					else if("1".equals(this.type)|| "3".equals(this.type)) //代码、日期及数值型指标
					{
						if("A".equals(itemtype))
						{
							if(!"UN".equals(codesetid)&&!"UM".equals(codesetid)&&!"@K".equals(codesetid))
								list.add(getLazyDynaBean(id,desc,"1","3","0",itemtype));
							else if("UN".equals(codesetid))
								list.add(getLazyDynaBean(id,desc,"1","4","0",itemtype));
							else if("UM".equals(codesetid))
								list.add(getLazyDynaBean(id,desc,"1","5","0",itemtype));
							else if("@K".equals(codesetid))
								list.add(getLazyDynaBean(id,desc,"1","6","0",itemtype));
						}
						else if("N".equals(itemtype))
						{
							list.add(getLazyDynaBean(id,desc,"1","7","0",itemtype));
						}
						else if("D".equals(itemtype))
						{
							list.add(getLazyDynaBean(id,desc,"1","8","0",itemtype));
						}
					}
					else if("4".equals(this.type))
					{
						
						list.add(getLazyDynaBean(id,desc,"0","8","1",itemtype));
						
					}
				}
				
				if("A01".equalsIgnoreCase(this.id))
				{
					if(!isB0110)
					{
						list.add(getLazyDynaBean("B0110","单位","1","4","0","A"));
					}
					
					if(!isE0122)
					{
						list.add(getLazyDynaBean("E0122","部门","1","5","0","A"));
					}
					
					if(!isE01A1)
					{
						list.add(getLazyDynaBean("E01A1","职位","1","6","0","A"));
					}
				}
				
			}
			else if("3".equals(this.flag))
			{
			//	System.out.println("select * from codeitem where ( codesetid=(select codesetid from fielditem where itemid='"+this.id+"' ) and codeitemid=parentid ) or (parentid='"+this.id+"')");
				String itemid="";
				if(id.indexOf("#")==-1)
				{
					rs=dao.search("select * from codeitem where ( codesetid=(select codesetid from fielditem where itemid='"+this.id+"' ) and codeitemid=parentid )  and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date  order by a0000,codeitemid");
					itemid=this.id;
				}
				else
				{
					String[] temps=this.id.split("#");
					String[] temps1=temps[0].split("/");
					itemid=temps1[0];
					rs=dao.search("select * from codeitem where  codesetid='"+temps1[1]+"' and parentid='"+temps[1]+"' and parentid<>codeitemid   and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date  order by a0000,codeitemid");
				}
				while (rs.next()) {
					String  id=rs.getString("codeitemid");
					String codesetid=rs.getString("codesetid");
					String  desc=rs.getString("codeitemdesc");
					String childid=rs.getString("childid");
					
					if(id.equals(childid))
					{
						id=itemid+"/"+codesetid+"#"+id;
						list.add(getLazyDynaBean(id,desc,"0","3","1",""));
					}
					else
					{
						id=itemid+"/"+codesetid+"#"+id;
						list.add(getLazyDynaBean(id,desc,"1","3","1",""));
					}
				}
				
				
			}
			else if("4".equals(this.flag)|| "5".equals(this.flag)|| "6".equals(this.flag))
			{
				String itemid="";  // 2008/01/24 dengcan
				if(id.indexOf("#")==-1)
				{
					rs=dao.search("select * from organization where codesetid='UN' and parentid=codeitemid and codeitemid<>childid  and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by codeitemid");
					itemid=this.id;  // 2008/01/24 dengcan
				}
				else
				{
					String[] temps=this.id.split("#");
					String[] temps1=temps[0].split("/");  // 2008/01/24 dengcan
					itemid=temps1[0];                     // 2008/01/24 dengcan
					
					if("4".equals(this.flag))  //UN
						rs=dao.search("select * from organization where codesetid='UN' and parentid='"+temps[1]+"' and parentid<>codeitemid  and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date  ");
					else if("5".equals(this.flag)) //UM
						rs=dao.search("select * from organization where codesetid<>'@K' and parentid='"+temps[1]+"' and parentid<>codeitemid  and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date  ");
					else if("6".equals(this.flag)) //@K
						/* 建薪资表中的横纵项时，职位选择不出来 xiaoyun 2014-10-20 start */
						//rs=dao.search("select * from organization where  parentid='"+temps[1]+"' and parentid<>codeitemid  and end_date>='"+ year + "-" + month + "-" + day +"'  and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");//2010-11-18 JinChunhai
						rs=dao.search("select * from organization where  parentid='"+temps[1]+"' and parentid<>codeitemid  and end_date>="+Sql_switcher.dateValue(year + "-" + month + "-" + day) +" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
						/* 建薪资表中的横纵项时，职位选择不出来 xiaoyun 2014-10-20 end */
				}
				
				while (rs.next()) {
					String  id=rs.getString("codeitemid");
					String codesetid=rs.getString("codesetid");
					String  desc=rs.getString("codeitemdesc");
					String childid=rs.getString("childid");
					
					String a_subNode="0";
					if("4".equals(this.flag)&& "UN".equals(codesetid))
						a_subNode="1";
					else if("5".equals(this.flag)&& "UM".equals(codesetid))
						a_subNode="1";
					else if("6".equals(this.flag)&& "@K".equals(codesetid))
						a_subNode="1";
					
					
					if(id.equals(childid))
					{
						id=itemid+"/"+ codesetid+"#"+id;   // 2008/01/24 dengcan
						list.add(getLazyDynaBean(id,desc,"0",this.flag,a_subNode,""));
					}
					else
					{
						id=itemid+"/"+ codesetid+"#"+id;   // 2008/01/24 dengcan
						list.add(getLazyDynaBean(id,desc,"1",this.flag,a_subNode,""));
					}
				}
			}	
			else if("7".equals(this.flag)|| "8".equals(this.flag))
			{
			
				rs=dao.search("select * from gz_stand_date  where item='"+this.id+"'  order by cast(item_id as int)");

				while (rs.next()) {
					String  item=rs.getString("item");
					String  id=rs.getString("item_id");
					String  desc=rs.getString("description");
					String a_subNode="1";
					list.add(getLazyDynaBean(item+"#"+id,desc,"0",this.flag,a_subNode,"E"));
				}
			}
			
				
		} catch (Exception e) {
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
	

}
