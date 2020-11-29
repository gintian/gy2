package com.hjsj.hrms.interfaces.performance;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
/**
 * 
* 
* 类名称：DataCollectHandImportMenXml   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:09:41 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:09:41 PM   
* 修改备注：   手工选人xml树
* @version    
*
 */
public class DataCollectHandImportMenXml {
	String nbase;       
	String flag;	  
	String id;
	UserView userView;
	String fieldsetid;
	public DataCollectHandImportMenXml(String flag,String id,String nbase,UserView view,String fieldsetid)
	{
		this.nbase=nbase;
		this.flag=flag;
		this.id=id;
		this.userView=view;
		this.fieldsetid=fieldsetid;
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
						abean.set("codeitemid","UN"+"/"+rs.getString("Pre")+"/0");
						abean.set("codeitemdesc",rs.getString("dbname"));
						list.add(abean);
					}
				}
			}
			else 
			{
				String sql="";
				String likeSQL=" parentid=codeitemid  ";

				if("0".equals(this.flag))
				{
					sql="select * from organization where "+likeSQL+" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
				}
				else
				{
					String[] temps=this.id.split("/");
					sql="select * from organization where parentid='"+temps[1]+"' and parentid<>codeitemid and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ";
				}
				sql+=" order by a0000";
				rs=dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("flag",rs.getString("codesetid"));
					abean.set("codeitemid",rs.getString("codesetid")+"/"+rs.getString("codeitemid")+"/"+this.nbase+"/"+rs.getString("codesetid"));
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
			ArrayList personList=getPersonList(temps[1],temps[3]);
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
			DataCollectBo bo = new DataCollectBo(conn,"Params");
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			String _flag  = bo.getXmlValue1("flag",fieldsetid);//flag 1:简单条件  2:复杂条件
			String _value = bo.getValue(fieldsetid);
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			StringBuffer tempsql = new StringBuffer("");
			if("1".equals(_flag)){
				FactorList factor = new FactorList("1", _value,this.nbase, false, false, true, 1, this.userView.getUserId());				
				String strSql = factor.getSqlExpression();
				tempsql.append(" and "+this.nbase+"A01.a0100 in ( select "+this.nbase+"A01.a0100 "+strSql+")"); 
			}else if("2".equals(_flag)){
				String tempTableName ="";
				String w ="";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic	
				String whereIN="select "+this.nbase+"A01.a0100 from "+this.nbase+"A01";
				alUsedFields.addAll(this.getMidVariableList(set_id,dao));
				YksjParser yp = new YksjParser(this.userView ,alUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht",this.nbase);
				YearMonthCount ymc=null;							
				yp.run_Where(_value, ymc,"","hrpwarn_result", dao, whereIN,conn,"A", null);
				tempTableName = yp.getTempTableName();
				w = yp.getSQL();
				tempsql.append("and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+this.nbase+"A01.a0100 and ( "+w+" ))");
			}
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
			
    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
    		{
    			
    		}
    		else
    		{
    			sql+=" and "+this.nbase+"A01.a0100 in ("+wsql+")";	
    		}	
			if(tempsql.toString().length()>0)
				 sql+=" "+tempsql+"";
			sql+="  order by a0000 ";
			/**此处可以引入当前工资表中已存在的人*/
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
				abean.set("codeitemid",rs.getString("a0100")+"/"+this.nbase+"/"+(rs.getString("a0101")==null?"":rs.getString("a0101"))+str+"/p");
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
				a_nbase=codeitemid.split("/")[1];
			else
				a_nbase=codeitemid.split("/")[2];
			String a_xml="/performance/data_collect/handImportMen_tree.jsp?flag="+aflag+"&id="+codeitemid+"&nbase="+a_nbase+"&fieldsetid="+fieldsetid;
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
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String fieldsetid,ContentDAO dao)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=5 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(fieldsetid);
			buf.append("') order by sorting");
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return new_fieldList;
	}

}
