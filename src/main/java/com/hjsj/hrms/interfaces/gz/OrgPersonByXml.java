package com.hjsj.hrms.interfaces.gz;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class OrgPersonByXml {
	String nbase;       
	String flag;	  
	String id;
	String tablename;
	String isSalaryManager;
	UserView userView;
	public OrgPersonByXml(String flag,String id,String nbase,String tablename,String isSalaryManager,UserView view)
	{
		this.nbase=nbase;
		this.flag=flag;
		this.id=id.replaceAll("／","/"); //20140915 dengcan  过滤器产生的问题
		this.isSalaryManager=isSalaryManager;
		this.userView=view;
		this.tablename=tablename;
		if(this.userView.hasTheFunction("327121201")||this.userView.hasTheFunction("324021201")||this.userView.hasTheFunction("325021201")||this.userView.hasTheFunction("327021201")||this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			this.isSalaryManager="Y";
		else
			this.isSalaryManager="N";
	}
	
	
	
	/**
	 * 取得节点下的信息
	 * @return
	 */
	public ArrayList getList()
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			if("-1".equals(flag))
			{
				String[] temps=this.nbase.split(",");
				String str="";
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
						str+=",'"+temps[i].toLowerCase()+"'";
					
				}
				if(str.length()>0)
				{
					rs=dao.search("select * from dbname where lower(pre) in ("+str.substring(1)+") order by dbid");
					while(rs.next())
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("flag","0");
						abean.set("codeitemid",rs.getString("Pre")+"/0");
						abean.set("codeitemdesc",rs.getString("dbname"));
						list.add(abean);
					}
				}
			}
			else 
			{
				String sql="";
				String likeSQL=" parentid=codeitemid  ";
				/**不是工资管理员*/
				if("N".equalsIgnoreCase(isSalaryManager))
				{
					String codeset=this.userView.getManagePrivCode();
					String codevalue=this.userView.getManagePrivCodeValue();
					if(codeset==null|| "".equals(codeset))
					{
						likeSQL=" 1=2 ";
					}
					else
					{
						if("UN".equalsIgnoreCase(codeset)&&(codevalue==null|| "".equals(codevalue)))
						{
							likeSQL=" parentid=codeitemid ";
						}
						else
						{
							likeSQL=" codeitemid='"+codevalue+"'";
						}
					}
				}
				if("0".equals(this.flag))
				{
					sql="select * from organization where "+likeSQL+" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
				}
				else
				{
					String[] temps=this.id.split("/");
					sql="select * from organization where parentid='"+temps[0]+"' and parentid<>codeitemid and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
				}
				sql+=" order by a0000";
				rs=dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("flag",rs.getString("codesetid"));
					abean.set("codeitemid",rs.getString("codeitemid")+"/"+this.nbase+"/"+rs.getString("codesetid"));
					abean.set("codeitemdesc",rs.getString("codeitemdesc"));
					list.add(abean);
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
		
		if(!"0".equals(this.flag)&&!"-1".equals(this.flag))
		{
			String[] temps=this.id.split("/");
			ArrayList personList=getPersonList(temps[0],temps[2]);
			list.addAll(personList);
		}
		
		return list;
	}
	
	
	/**
	 * 取得机构下的人员信息
	 * @param codeitemid
	 * @param codesetid
	 * @return
	 */
	public ArrayList getPersonList(String codeitemid,String codesetid)
	{
		ArrayList list=new ArrayList();
		 // DB相关
		ResultSet rs = null;	
		Connection conn=null;
		try
		{
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			SalaryTemplateBo templatebo=new SalaryTemplateBo(conn);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String cloumn="";
			StringBuffer wsql = new StringBuffer("");
			String priStrSql = InfoUtils.getWhereINSql(this.userView, this.nbase);
			wsql.append("select "+this.nbase+"a01.A0100 ");
			if (priStrSql.length() > 0)
				wsql.append(priStrSql);
			else
				wsql.append(" from "+this.nbase+"a01");
			if(onlyname!=null&&!"".equals(onlyname))
			{
				cloumn=","+onlyname;
			}
			String sql="";
			if("UN".equalsIgnoreCase(codesetid))
			{
				sql="select a0100,a0101"+cloumn+" from "+this.nbase+"A01 where b0110='"+codeitemid+"' and ( e0122 is null or e0122='' ) and ( e01a1 is null or e01a1='' ) ";
			}
			else if("UM".equalsIgnoreCase(codesetid))
			{
				sql="select a0100,a0101"+cloumn+" from "+this.nbase+"A01 where   e0122='"+codeitemid+"'  and ( e01a1 is null or e01a1='' )";
			}
			else if("@K".equalsIgnoreCase(codesetid))
			{
				sql="select a0100,a0101"+cloumn+" from "+this.nbase+"A01 where   e01a1='"+codeitemid+"' ";
			}
			if("N".equalsIgnoreCase(isSalaryManager))
			{
				sql+=" and "+this.nbase+"A01.a0100 in ("+wsql+")";
			}
			sql+="  order by a0000 ";
			/**此处可以引入当前工资表中已存在的人*/
			//sql+=" and a0100 not in (select a0100 from "+tablename+" where lower(nbase)='"+this.nbase.toLowerCase()+"')";
			rs=dao.search(sql);
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			while(rs.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("flag","p");
				
				abean.set("codeitemdesc",rs.getString("a0101")==null?"":rs.getString("a0101"));
				String value="";
				String str="";
				if(onlyname!=null&&!"".equals(onlyname))
				{
					value=rs.getString(onlyname)==null?"":rs.getString(onlyname);
					if(item!=null)
					{
						if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equalsIgnoreCase(item.getCodesetid()))
						{
							value=AdminCode.getCodeName(item.getCodesetid(),value);
						}
					}
					if(value!=null&&!"".equals(value))
						str="("+value+")";
				}
				abean.set("codeitemid",PubFunc.encrypt(rs.getString("a0100"))+"/"+PubFunc.encrypt(this.nbase)+"/"+(rs.getString("a0101")==null?"":rs.getString("a0101"))+str+"/p"); //20140909  dengcan
				abean.set("onlyvalue", value);
				list.add(abean);
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
	
	
	public String outPutXml() throws GeneralException {
		Connection conn=null;
		StringBuffer xmls = new StringBuffer();
		try
		{
			conn= AdminDb.getConnection();
		
//		 生成的XML文件
		// 创建xml文件的根元素
		Element root = new Element("TreeNode");
		// 设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "organization");
		// 创建xml文档自身
		Document myDocument = new Document(root);
		// 设置跳转字符串
		SalaryTemplateBo templatebo=new SalaryTemplateBo(conn);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		FieldItem item = DataDictionary.getFieldItem(onlyname);
		ArrayList list =getList();
		for (Iterator t = list.iterator(); t.hasNext();) {
			LazyDynaBean abean = (LazyDynaBean) t.next();

			// 创建子元素
			Element child = new Element("TreeNode");
			// 设置子元素属性
			String codeitemid = (String) abean.get("codeitemid");
			String codeitemdesc = (String) abean.get("codeitemdesc");
			String aflag=(String)abean.get("flag");
		
			String sttr=codeitemdesc;
			
			if(aflag!=null&& "p".equals(aflag))
				child.setAttribute("id",codeitemid);
			else
				child.setAttribute("id",codeitemid);
			
			if("UN".equalsIgnoreCase(aflag))
				child.setAttribute("icon","/images/unit.gif");
			else if("UM".equalsIgnoreCase(aflag))
				child.setAttribute("icon","/images/dept.gif");
			else if("@K".equalsIgnoreCase(aflag))
				child.setAttribute("icon","/images/pos_l.gif");
			else if("p".equals(aflag))
			{
				if(item!=null&&abean.get("onlyvalue")!=null&&!"".equals((String)abean.get("onlyvalue")))
				{
					sttr+="("+(String)abean.get("onlyvalue")+")";
				}
				child.setAttribute("icon","/images/man.gif");
			}
			else 
				child.setAttribute("icon","/images/add_all.gif");
			child.setAttribute("text", sttr);
			child.setAttribute("title", sttr);
			child.setAttribute("target", "_self"); 
			String a_nbase="";
			if("-1".equals(this.flag))
				a_nbase=codeitemid.split("/")[0];
			else
				a_nbase=codeitemid.split("/")[1];
			//String a_xml="/gz/gz_accounting/importMen/handImportMen_tree.jsp?flag="+aflag+"&id="+codeitemid+"&nbase="+a_nbase+"&tablename="+tablename+"&isSalaryManager="+isSalaryManager;
			String a_xml="/gz/gz_accounting/importMen/handImportMen_tree.jsp?flag="+aflag+"&encryptParam="+PubFunc.encrypt("id="+codeitemid+"&nbase="+a_nbase+"&tablename="+tablename+"&isSalaryManager="+isSalaryManager);
			
			if(!"p".equals(aflag))
	        	child.setAttribute("xml", a_xml);
			// 将子元素作为内容添加到根元素
			root.addContent(child);
		}
		XMLOutputter outputter = new XMLOutputter();
		// 格式化输出类
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);

		// 将生成的XML文件作为字符串形式
		xmls.append(outputter.outputString(myDocument));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
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
	
}
