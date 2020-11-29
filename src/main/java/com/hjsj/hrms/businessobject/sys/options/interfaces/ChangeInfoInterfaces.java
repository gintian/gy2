package com.hjsj.hrms.businessobject.sys.options.interfaces;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * 其系统主动调用此webservice，来同步人员和组织机构，查询的是触发器的临时表
 * @author Administrator
 *
 */
public class ChangeInfoInterfaces {

	
	private String emp_table="t_hr_view";
	private String org_table="t_org_view";
	private String post_table="t_post_view";
	private int UPDATE_FALG = 2;
	private int DEL_FLAG = 3;
	private int ADD_FLAG = 1;
	private int SYNC_FLAG=0;
	private Document doc;
	public ChangeInfoInterfaces()
	{
		String sync_version = SystemConfig.getPropertyValue("sync_version");
		if (sync_version != null && "old".equals(sync_version)) {
			this.UPDATE_FALG = 1;
			this.DEL_FLAG = 2;
			this.ADD_FLAG = 0;
			this.SYNC_FLAG=4;
		}
	}
	/**
	 * 人员
	 * @param changeFlag更新标识0：新增；1：修改；2：删除
	 * @return
	 */
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 * <hr version="5.0">
	 * <title>人力资源系统</title> 
	 * <language>zh-cn</language> 
	 * <element flag=”变动标识”>
	 * <nbase>在职人员库</ nbase > <!—人员库汉字描述--> 
	 * <nbase_0> Usr </ nbase_0 > <!—人员库-->
	 * <a0100>00000001</ a0100><!—hr人员id--> 
	 * <b0110_0>1001</ b0110><!—单位代码-->
	 * <e0122_0>1001001</ e0122_0><!—部门代码--> 
	 * <e01a1_0>100100101</ e01a1_0><!--职位代码-->
	 * <a0101> 姓名 </ a0101><!—姓名--> 
	 * <username>sa</ username ><!—hr登陆用户名-->
	 * <userpassword>****</ userpassword ><!—hr登陆密码-->
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> <sex>0</ sex> <age>30</ age>
	 * <address> 北京 </ address > 
	 * </element >
	 * <element flag=”变动标识”> 
	 * <nbase>离职人员库</ nbase > <!—人员库汉字描述--> 
	 * <nbase_0> Ret </ nbase_0 > <!—人员库--> 
	 * <a0100>00000001</a0100><!—hr人员id--> 
	 * <b0110_0>1001</ b0110><!—单位代码--> 
	 * <e0122_0>1001001</e0122_0><!—部门代码--> 
	 * <e01a1_0>100100101</ e01a1_0><!--职位代码--> 
	 * <a0101> 姓名 </a0101><!—姓名--> 
	 * <username>sa</ username ><!—hr登陆用户名-->
	 * <userpassword>****</ userpassword ><!—hr登陆密码-->
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> <sex>0</ sex> <age>30</ age>
	 * <address> 北京 </ address >
	 * </element > 
	 * </ hr > 
	 */
	
	
	
	private String errorMess="";

	public String getErrorMess() {
		return errorMess;
	}
	public void setErrorMess(String errorMess) {
		this.errorMess = errorMess;
	}
	public String getChangeUsersXML(Connection conn,String changeFlag)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.emp_table+"");
		if(changeFlag!=null&&changeFlag.length()>0) {
            sql.append(" where flag='"+changeFlag+"'");
        }
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null) {
            return "";
        }
		sql.setLength(0);
		sql.append("select * from "+this.emp_table+" where 1=2");
		ArrayList fieldnames=getColumns(conn, sql.toString());
		String xml=constructorXml(fieldnames,rs);
		return xml;		
	}
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangeUsers (Connection conn,String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.emp_table+"");
		if(whereStr!=null&&whereStr.length()>0) {
            sql.append(" where 1=1 and "+whereStr+"");
        }
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null) {
            return "";
        }
		sql.setLength(0);
		sql.append("select * from "+this.emp_table+" where 1=2");
		ArrayList fieldnames=getColumns(conn, sql.toString());
		String xml=constructorXml(fieldnames,rs);
		return xml;		
	}
	/**
	 * 以xml形式，返回已同步人员信息，hr系统接收后将人员变动信息表，对应人员记录的变动标识置为已同步状态
	 * @param conn
	 * @param xml
	 * @return
	 */
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 * <hr version="5.0"> 
	 * <element >
	 * <nbase_0>Usr</nbase_0 > <!—对应用户同步接收信息里的nbase人员库--> 
	 * <a0100>00000001</a0100><!—对应用户同步接收信息里的a0100,hr人员id--> 
	 * </element > 
	 * </hr> 
	 */

	public boolean returnSynchroUserXml (Connection conn,String xml)
	{
		boolean isCorrect=true;
		if(xml==null||xml.length()<=0) {
            return false;
        }
		String xpath="/hr/element";
		XPath reportPath;
		String nbase="";
		String a0100="";
		StringBuffer buf=new StringBuffer();
		try {
			this.doc=PubFunc.generateDom(xml);
			reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(doc);
		    Iterator t = childlist.iterator();
		    ContentDAO dao=new ContentDAO(conn);
		    while(t.hasNext())
		    {
		    	Element element=(Element)t.next();
		    	Element nElement=element.getChild("nbase_0");
		    	nbase=nElement.getText();
		    	Element a0100Element=element.getChild("a0100");
		    	a0100=a0100Element.getText();
		    	if(nbase==null||nbase.length()<=0) {
                    continue;
                }
		    	if(a0100==null||a0100.length()<=0) {
                    continue;
                }
		    	/*ArrayList olist=new ArrayList();
		    	olist.add("4");
		    	olist.add(nbase);
		    	olist.add(a0100);		    	
		    	list.add(olist);*/
		    	String sql="update "+this.emp_table+" set flag="+this.SYNC_FLAG+" where nbase_0='"+nbase+"' and a0100='"+a0100+"'";
		    	int ss=dao.update(sql);		    	
		    	if(ss<=0)
		    	{
		    		isCorrect=false;
		    		buf.append("<element flag=\"-1\">");
	    			buf.append("<nbase_0>"+nbase+"</nbase_0 >");
	    			buf.append("<a0100>"+a0100+"</a0100>");
		    		buf.append("</element>");
		    	}
		    }		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setErrorMess(buf.toString());
		return isCorrect;
	}
	/**
	 * 返回已同步信息数据
	 * @param conn
	 * @param arrayString数组有hr系统人员库和hr系统id组成的一个一维数组
	 * [usr0000001，usr0000002，usr0000003，。。。。]
	 * @return
	 */
	public boolean returnSynchroArray (Connection conn,String[] arrayString)
	{
		boolean isCorrect=true;
		if(arrayString==null||arrayString.length<=0) {
            return false;
        }
		String str="";
		String nbase="";
		String a0100="";
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			isCorrect=true;
			for(int i=0;i<arrayString.length;i++)
			{
				str=arrayString[i];
				if(str==null||str.length()<=3) {
                    continue;
                }
				nbase=str.substring(0,3);
				a0100=str.substring(3);
				if(nbase==null||nbase.length()<=0) {
                    continue;
                }
		    	if(a0100==null||a0100.length()<=0) {
                    continue;
                }
		    	/*ArrayList olist=new ArrayList();
		    	olist.add("4");
		    	olist.add(nbase);
		    	olist.add(a0100);		    	
		    	list.add(olist);*/
		    	String sql="update "+this.emp_table+" set flag="+this.SYNC_FLAG+" where nbase_0='"+nbase+"' and a0100='"+a0100+"'";
		    	int ss=dao.update(sql);		    	
		    	if(ss<=0)
		    	{
		    		isCorrect=false;
		    		buf.append("<element flag=\"-1\">");
	    			buf.append("<nbase_0>"+nbase+"</nbase_0 >");
	    			buf.append("<a0100>"+a0100+"</a0100>");
		    		buf.append("</element>");
		    	}
		    	
			}
			
		    
		    
		  /*  
		   *String sql="update "+this.emp_table+" set flag=? where nbase_0=? and a0100=?";
		   *int upint[]=dao.batchUpdate(sql, list);
		    for(int i=0;i<upint.length;i++)
		    {
		    	int ss=upint[i];
		    	if(ss<=0)
		    	{
		    		isCorrect=false;
		    		//break;
		    		if(i<list.size())
		    		{
		    			ArrayList olist=(ArrayList)list.get(i);
		    			nbase=(String)olist.get(1);
		    			a0100=(String)olist.get(2);
		    			buf.append("<element flag=\"-1\">");
		    			buf.append("<nbase_0>"+nbase+"</nbase_0 >");
		    			buf.append("<a0100>"+a0100+"</a0100>");
			    		buf.append("</element>");
		    		}
		    	}
		    }*/
		    
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.setErrorMess(buf.toString());
		return isCorrect;
	}
	/**
	 * xml构造器
	 * @param fieldnames
	 * @param rs
	 * @return
	 */
	private String constructorXml(ArrayList fieldnames,List rs)
	{
		init();
		String xpath="/hr";
		XPath reportPath;
		String name="";
		String xml="";
		try {
			reportPath = XPath.newInstance(xpath);
			List childlist=reportPath.selectNodes(doc);
		    Iterator t = childlist.iterator();
		    if(t.hasNext())
		    {
		    	Element hrElement=(Element)t.next();
		    	for(int i=0;i<rs.size();i++)
		    	{
		    		LazyDynaBean rec=(LazyDynaBean)rs.get(i);
		    		if(rec!=null)
		    		{
		    			Element element=new Element("element");	
		    			String flag=(String)rec.get("flag");
		    			element.setAttribute("flag",flag);
		    			for(int f=0;f<fieldnames.size();f++)
		    			{
		    				name=(String)fieldnames.get(f);
		    				if(name==null||name.length()<=0) {
                                continue;
                            }
		    				/*if(name.equals("flag"))
		    					continue;*/
		    				Element childE=new Element(name);	
		    				String text=rec.get(name)!=null&&rec.get(name).toString().length()>0?rec.get(name).toString():"";
		    				childE.setText(text);
		    				element.addContent(childE);
		    			}
			    		hrElement.addContent(element);
		    		}		    		
		    	}
		    	 XMLOutputter outputter = new XMLOutputter();
	  	         Format format=Format.getPrettyFormat();
	  	         // gb2312 遇到生僻字时，解析不出来乱码 改成gbk
	  	         format.setEncoding("UTF-8");
	  	         outputter.setFormat(format);  	           
	  	         xml=outputter.outputString(doc);	
		    }
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 取得根节点
	       
		return xml;
	}
	/**
	 * 初始化xml
	 */
	private void init()
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='UTF-8' ?>");
		strxml.append("<hr version=\"5.0\">");
		strxml.append("<title>人力资源系统</title>");
		strxml.append("<language>zh-cn</language>");
		strxml.append("</hr>");
		try
		{
			this.doc=PubFunc.generateDom(strxml.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 得到表字段
	 * @param conn
	 * @param 
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getColumns(Connection conn,String sql)
	{
		ArrayList fieldname=new ArrayList();
		 ResultSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			
    		rs=dao.search(sql);	  
			ResultSetMetaData meta = rs.getMetaData();			
			int columnCount = meta.getColumnCount();
		    for(int i=0;i<columnCount;i++)
			{
				fieldname.add(meta.getColumnName(i + 1).toLowerCase());				
			}
		}catch (Exception sqle){
			sqle.printStackTrace();
		}
		finally{
			try{
				//conn.commit();  //chenmengqing changed at 20060720
				if (rs != null){
					rs.close();
				}
				
			}catch (SQLException sqle){
				//sql.printStackTrace();
			}
		}
		return fieldname;
	}
	/**
	 * 组织机构
	 * @param changeFlag更新标识0：新增；1：修改；2：删除
	 * @return
	 */
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 * <hr version="5.0">
	 * <title>人力资源系统</title> 
	 * <language>zh-cn</language> 
	 * <element flag=”变动标识”>
	 * <b0110_0>01</ b0110_0 > <!—机构编号--> 
	 * <codesetid> UN </ codesetid > <!—机构编码-->
	 * <codeitemdesc>某集团公司</ codeitemdesc><!—机构名称--> 
	 * <parentid>01</ parentid><!—父亲ID-->
	 * <parentdesc>某集团公司</ parentdesc><!—父亲机构名称--> 
	 * <grade>1</ grade><!--机构层级-->
	 * <flag> 变动标识 </ flag><!—变动标识--> 	 
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> 
	 * <b0160>1</ b0160> <b0140>10</ b0140>
	 * <b0180> 张军 </ b0180 > 
	 * </element >
	 * <element flag=”变动标识”>
	 * <b0110_0>0101</ b0110_0 > <!—机构编号--> 
	 * <codesetid> UN </ codesetid > <!—机构编码-->
	 * <codeitemdesc>集团总部</ codeitemdesc><!—机构名称--> 
	 * <parentid>01</ parentid><!—父亲ID-->
	 * <parentdesc>某集团公司</ parentdesc><!—父亲机构名称--> 
	 * <grade>2</ grade><!--机构层级-->
	 * <flag> 变动标识 </ flag><!—变动标识--> 	 
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> 
	 * <b0160>1</ b0160> <b0140>10</ b0140>
	 * <b0180> 张军 </ b0180 > 
	 * </element >
	 * </ hr > 
	 */
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangeOrganizations(Connection conn,String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.org_table+"");
		if(whereStr!=null&&whereStr.length()>0) {
            sql.append(" where 1=1 and "+whereStr+"");
        }
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null) {
            return "";
        }
		sql.setLength(0);
		sql.append("select * from "+this.org_table+" where 1=2");
		ArrayList fieldnames=getColumns(conn, sql.toString());
		String xml=constructorXml(fieldnames,rs);
		return xml;		
	}
	/**
	 * 组织机构
	 * @param changeFlag更新标识0：新增；1：修改；2：删除
	 * @return
	 */
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 * <hr version="5.0">
	 * <title>人力资源系统</title> 
	 * <language>zh-cn</language> 
	 * <element flag=”变动标识”>
	 * <e01a1_0>01</ e01a1_0 > <!—岗位编号--> 
	 * <e0122_0>01</ e0122_0 > <!—所在部门编号--> 
	 * <codesetid> @K </ codesetid > <!—机构编码-->
	 * <codeitemdesc>机构名称</ codeitemdesc><!—机构名称--> 
	 * <parentid>01</ parentid><!—父亲ID-->
	 * <parentdesc>父亲机构名称</ parentdesc><!—父亲机构名称--> 
	 * <grade>1</ grade><!--机构层级-->
	 * <flag> 变动标识 </ flag><!—变动标识--> 	 
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> 
	 * <b0160>1</ b0160> <b0140>10</ b0140>
	 * <b0180> 张军 </ b0180 > 
	 * </element >
	 * <element flag=”变动标识”>
	 * <e01a1_0>010101010101</ e01a1_0 > <!—机构编号--> 
	 * <e0122_0>01010101</ e0122_0 > <!—所在部门编号--> 
	 * <codesetid> UN </ codesetid > <!—机构编码-->
	 * <codeitemdesc>集团总部</ codeitemdesc><!—机构名称--> 
	 * <parentid>01</ parentid><!—父亲ID-->
	 * <parentdesc>某集团公司</ parentdesc><!—父亲机构名称--> 
	 * <grade>2</ grade><!--机构层级-->
	 * <flag> 变动标识 </ flag><!—变动标识--> 	 
	 * <!—以下是用户自定义指标，指标名称如果没有定义在按，hr系统中的指标名称返回--> 
	 * <b0160>1</ b0160> <b0140>10</ b0140>
	 * <b0180> 张军 </ b0180 > 
	 * </element >
	 * </ hr > 
	 */
	/**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param conn
	 * @param whereStr自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangePost(Connection conn,String whereStr)
	{
		StringBuffer sql=new StringBuffer();		
		sql.append("select * from "+this.post_table+"");
		if(whereStr!=null&&whereStr.length()>0) {
            sql.append(" where 1=1 and "+whereStr+"");
        }
		List rs =ExecuteSQL.executeMyQuery(sql.toString());
		if(rs==null) {
            return "";
        }
		sql.setLength(0);
		sql.append("select * from "+this.post_table+" where 1=2");
		ArrayList fieldnames=getColumns(conn, sql.toString());
		String xml=constructorXml(fieldnames,rs);
		return xml;		
	}
	
	
	/**
	 * 以xml形式，返回已同步信息，hr系统接收后将变动信息表，对应记录的变动标识置为已同步状态
	 * @param conn
	 * @param xml
	 * @return
	 */
	/**
	 * <?xml version="1.0" encoding="GB2312"?>
	 *  <hrinfo> 
	 *   <info>
	 *    <id>0000004</id> <!—对应用户同步接收信息里的唯一性指标值。例如：身份证号、工号--> 
	 *    <hr_flag>1</hr_flag> <!—HR系统发送的xml中的flag，即HR系统同步标志。1为新增，2为更新，3为停用 --> 
	 *    <flag>0</flag> <!—是否保存成功标识。0为成功，1为不成功 -->
	 *   </info>
	 *   ...... 
	 *  </hrinfo> 
	 */
	public boolean returnSynchroXml(Connection conn,String xml,String onlyFiled,String sysFlag,String type)
	{
		boolean isCorrect = true;
		if(xml==null || xml.trim().length()<=0) {
            return false;
        }
		String xpath = "/hrinfo/info";
		StringBuffer buf = new StringBuffer();
		String id = "";
		String hr_flag = "";
		String flag = "";
		try 
		{
			this.doc = PubFunc.generateDom(xml);
			XPath reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
		    Iterator t = childlist.iterator();
		    ContentDAO dao=new ContentDAO(conn);
		    while(t.hasNext())
		    {
		    	Element element = (Element)t.next();
		    	Element idElement = element.getChild("id");
		    	id = idElement.getText();
		    	Element hrElement=element.getChild("hr_flag");
		    	hr_flag = hrElement.getText();
		    	Element fElement=element.getChild("flag");
		    	flag = fElement.getText();
		    	if(id==null || id.trim().length()<=0) {
                    continue;
                }
		    	if(hr_flag==null || hr_flag.trim().length()<=0) {
                    continue;
                }
		    	if(flag==null || flag.trim().length()<=0 || "1".equals(flag)) {
                    continue;
                }
		    	if(type==null || type.trim().length()<=0) {
                    continue;
                }
		    	
		    	StringBuffer sql = new StringBuffer();
		    	sql.append("update ");
		    	if("HR".equalsIgnoreCase(type))
		    	{
		    		sql.append(" "+this.emp_table+" ");
		    	}
		    	else if("ORG".equalsIgnoreCase(type))
		    	{
		    		sql.append(" "+this.org_table+" ");
		    	}
		    	else if("POST".equalsIgnoreCase(type))
		    	{
		    		sql.append(" "+this.post_table+" ");
		    	}
		    	sql.append(" set "+sysFlag+"=0 where "+onlyFiled+"='"+id+"' and " +sysFlag+ "='"+hr_flag+"'");
		    	
		    	int ss = dao.update(sql.toString());		    	
		    	if(ss<=0)
		    	{
		    		isCorrect = false;
		    		buf.append("<element flag=\"-1\">");
	    			buf.append("<id>"+id+"</id>");
	    			buf.append("<hr_flag>"+hr_flag+"</hr_flag>");
	    			buf.append("<flag>1</flag>");
		    		buf.append("</element>");
		    	}
		    }
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setErrorMess(buf.toString());
		return isCorrect;
	}
	
}
