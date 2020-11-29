package com.hjsj.hrms.businessobject.gz.piecerate;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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
import java.util.HashMap;
import java.util.Iterator;

public class SelPersonByXmlBo
{
	public String nbase;

	public String flag;

	public String id;
	
	public UserView userview;

	public String S0100;

	public int object_type;

	public String objWhere = "";
	
	private String getNbases()
	{   String s="";
		ArrayList db= this.userview.getPrivDbList();
		for (int i=0;i<db.size();i++)
		{
			if ("".equals(s)){
				s= (String)db.get(i);
			}
			else {
				s= s+","+(String)db.get(i);	
			}
			
		}
		
	  return s;	
	}


	public SelPersonByXmlBo(String flag, String _nbase ,String id,String _s0100, UserView _userview)
	{
		this.S0100 = _s0100;  
		this.flag = flag;
		this.id = id;
		this.userview = _userview;
		if ("-1".equals(flag)) {this.nbase =getNbases();}
		else {this.nbase=_nbase;}
		
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			this.objWhere = "";			
			if (this.S0100 != null && this.S0100.length() > 0)
			{				
				RecordVo vo = new RecordVo("S01");
				vo.setString("s0100", S0100);
				ContentDAO dao = new ContentDAO(conn);

			}
		} catch (Exception e)
		{
			e.printStackTrace();

		} finally {
			try {
								
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
    /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /*
     * 为了显示第一层为管理的最高层的节点
     * */
    private String isfirstnode;
	 /**求查询代码的字符串*/

	/**
	 * 取得节点下的信息
	 * 
	 * @return
	 */
	public ArrayList getList()
	{
		ArrayList list = new ArrayList();
		// DB相关
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
			if ("-1".equals(flag))// 先显示人员库
			{
				String[] temps = this.nbase.split(",");
				String str = "";

				for (int i = 0; i < temps.length; i++)
				{
					if (temps[i].trim().length() > 0)
						str += ",'" + temps[i].toLowerCase() + "'";

				}
				if (str.length() > 0)
				{
					rs = dao.search("select * from dbname where lower(pre) in (" + str.substring(1) + ") order by dbid");
					while (rs.next())
					{
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag", "0");
						abean.set("codeitemid", rs.getString("Pre") + "`0");
						abean.set("codeitemdesc", rs.getString("dbname"));
						list.add(abean);
					}
				}
			} else
			// 从组织机构开始显示
			{
				String sql = "";
				StringBuffer sql2 = new StringBuffer();
				String likeSQL = " parentid=codeitemid  ";

				if ("0".equals(this.flag))// 显示顶层组织机构
				{
					sql = "select * from organization where 1=1 " + this.getRootOrgNodeStr(this.userview) +" order by a0000";
					rs = dao.search(sql);
					while (rs.next())
					{
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag", rs.getString("codesetid"));
						abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
						abean.set("codeitemdesc", rs.getString("codeitemdesc"));
						list.add(abean);
					}

				} else
				// 显示中间组织机构
				{
					String[] temps = this.id.split("`");
					String _codeitemid = temps[0];
					int _len = _codeitemid.length();
					sql = "select * from organization where parentid='" + temps[0] + "' and parentid<>codeitemid and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ";
					sql += " order by a0000";

					HashMap map = new HashMap();
					String sql0 = "select codeitemid from organization where 1=1 " + this.getOrgWhere(this.userview);
					 
					rs = dao.search(sql0);
					while (rs.next())					{
						map.put(rs.getString(1), "");
					}
					rs = dao.search(sql);
					while (rs.next())
					{
						String codeitemid = rs.getString("codeitemid");
						if (map.get(codeitemid) == null)
							continue;
						LazyDynaBean abean = new LazyDynaBean();
						abean.set("flag", rs.getString("codesetid"));
						abean.set("codeitemid", rs.getString("codeitemid") + "`" + this.nbase + "`" + rs.getString("codeitemdesc") + "`" + rs.getString("codesetid"));
						abean.set("codeitemdesc", rs.getString("codeitemdesc"));
						list.add(abean);
					}


				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		boolean canSelEmp = true;
		if (canSelEmp && !"0".equals(this.flag) && !"-1".equals(this.flag))
		{
			String[] temps = this.id.split("`");
			ArrayList personList = getPersonList(temps[0], temps[3]);
			list.addAll(personList);
		}

		return list;
	}

	/**
	 * 取得机构下的人员信息
	 * 
	 * @param codeitemid
	 * @param codesetid
	 * @return
	 */
	public ArrayList getPersonList(String codeitemid, String codesetid)
	{
		ArrayList list = new ArrayList();
		// DB相关
		ResultSet rs = null;
		Connection conn = null;
		try
		{   conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			String cloumn = "";
			
			if(onlyname!=null && onlyname.trim().length()>0 && !"#".equals(onlyname))
			{			
				FieldItem fielditem = DataDictionary.getFieldItem(onlyname);
				String useFlag = fielditem.getUseflag(); 
				if("0".equalsIgnoreCase(useFlag))
					throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
				
				cloumn = "," + onlyname;
			}
			
			HashMap e01a1NullMap = new HashMap();
			String sql = "";
			if ("UN".equalsIgnoreCase(codesetid))
			{
				sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where b0110='" + codeitemid + "' and ( e0122 is null or e0122='' ) and ( e01a1 is null or e01a1='' ) ";
			} else if ("UM".equalsIgnoreCase(codesetid))
			{
				sql = "select a0100,e01a1,a0101" + cloumn + " from " + this.nbase + "A01 where e0122='" + codeitemid + "' ";
				
				// 获得部门下的岗位信息   解决岗位已经撤销但人员还挂在此岗位下
				e01a1NullMap = getE01a1NullMap(codeitemid);					

			} else if ("@K".equalsIgnoreCase(codesetid))
			{
				sql = "select a0100,a0101" + cloumn + " from " + this.nbase + "A01 where e01a1='" + codeitemid + "' ";
			}
			
			sql += this.objWhere;
			sql +=" and a0100 not in (select A0100 from S05 where S0100="+this.S0100+" and nbase ='"+this.nbase+"')";			
			sql += " order by a0000";

			rs = dao.search(sql);
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			while (rs.next())
			{
				if ("UM".equalsIgnoreCase(codesetid))
				{
					String e01a1 = rs.getString("e01a1") == null ? "" : rs.getString("e01a1");						
					if(e01a1NullMap!=null && e01a1NullMap.get(e01a1)!=null)
						continue;																	
				}				
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("flag", "p");
				abean.set("codeitemdesc", rs.getString("a0101") == null ? "" : rs.getString("a0101"));
				String value = "";
				String str = "";
				if (onlyname != null && !"".equals(onlyname))
				{
					value = rs.getString(onlyname) == null ? "" : rs.getString(onlyname);
					if (item != null)
					{
						if ("A".equalsIgnoreCase(item.getItemtype()) && !"0".equalsIgnoreCase(item.getCodesetid()))
						{
							value = AdminCode.getCodeName(item.getCodesetid(), value);
						}
					}
					if (value != null && !"".equals(value))
						str = "(" + value + ")";
				}
				abean.set("codeitemid", rs.getString("a0100") + "`" + this.nbase + "`" + (rs.getString("a0101") == null ? "" : rs.getString("a0101")) + str + "`p");
				abean.set("onlyvalue", value);
				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	public String outPutXml() throws GeneralException
	{
		Connection conn = null;
		StringBuffer xmls = new StringBuffer();
		try
		{
			conn = AdminDb.getConnection();

			HashMap existObjsMap = this.getExistObjs();		
			
			// 生成的XML文件
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "organization");
			// 创建xml文档自身
			Document myDocument = new Document(root);
			// 设置跳转字符串
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			ArrayList list = getList();
			for (Iterator t = list.iterator(); t.hasNext();)
			{
				LazyDynaBean abean = (LazyDynaBean) t.next();

				// 创建子元素
				Element child = new Element("TreeNode");
				// 设置子元素属性
				String codeitemid = (String) abean.get("codeitemid");
				String codeitemdesc = (String) abean.get("codeitemdesc");
				String aflag = (String) abean.get("flag");

				String sttr = codeitemdesc;
				child.setAttribute("id", codeitemid);

				if ("UN".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/unit.gif");
				else if ("UM".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/dept.gif");
				else if ("@K".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/pos_l.gif");
				else if ("lb".equalsIgnoreCase(aflag))
					child.setAttribute("icon", "/images/pos_l.gif");
				else if ("p".equals(aflag))
				{
					if (item != null && abean.get("onlyvalue") != null && !"".equals((String) abean.get("onlyvalue")))
					{
						sttr += "(" + (String) abean.get("onlyvalue") + ")";
					}
					child.setAttribute("icon", "/images/man.gif");
					// type = "true";
				} else
					child.setAttribute("icon", "/images/open.png");

				// 设置可选对象
				String type = "false";

				if ("p".equals(aflag))
					type = "true";							


				String[] temp = codeitemid.split("`");
				String orgid = temp[0];
				if (existObjsMap.get(orgid) != null)					
					type = "false";

					
				child.setAttribute("type", type);

				child.setAttribute("text", sttr);
				child.setAttribute("title", sttr);
				child.setAttribute("target", "_self");
				String a_nbase = "";
				if ("-1".equals(this.flag))// 点击人员库
					a_nbase = codeitemid.split("`")[0];
				else
					// 点击组织机构
					a_nbase = codeitemid.split("`")[1];
				String a_xml = "/gz/gz_accounting/piecerate/handImportPeople.jsp?flag=" + aflag + "&id=" 
				     + SafeCode.encode(codeitemid) + "&nbase=" + a_nbase + "&s0100=" + this.S0100 ;
				if (!"p".equals(aflag))
					child.setAttribute("xml", a_xml);
				String theaction="";
				

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
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// System.out.println(xmls.toString());
		return xmls.toString();
	}

	public String getOrgWhere(UserView userView)
	{
		String str = "";
		String logo = "false";
		String sgin = "false";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("1"); //操作单位
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
				
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String code = "-1";
				if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
				{
					code = userView.getManagePrivCodeValue();
					if ("UN".equalsIgnoreCase(code))
					{
						code = "-1";
						sgin = "true";
					}
				}
				if (!"-1".equals(code))
				{
					if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
						buf.append(" and codeitemid like '" + code + "%'");
					else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
						buf.append(" and codeitemid like '" + code + "%'");
				}else if("false".equalsIgnoreCase(sgin))
					logo = "true";
			}
			str += buf.toString();
		}
       if("true".equalsIgnoreCase(logo))
			str += " and 1=1 ";
		return str;
	}

	/** 展示顶层机构范围用等于 */
	public String getRootOrgNodeStr(UserView userView)
	{
		String temp = "";
		StringBuffer rootOrgid = new StringBuffer();
		String sql = "select codeitemid from organization where codesetid in ('UM','UN') " + this.getOrgWhere(userView);

		String sqlstr = this.getOrgWhere(userView);
		if(!"and 1=1".equalsIgnoreCase(sqlstr.trim()))
		{
			ArrayList list = new ArrayList();
			ResultSet rs = null;
			Connection conn = null;
			try
			{
				conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
				sql += " and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date  order by codeitemid";
				rs = dao.search(sql);
				while (rs.next())
					list.add(rs.getString(1));
	
				for (int i = list.size() - 1; i > 0; i--)
				{
					String x = (String) list.get(i);
					boolean isRoot = true;
					for (int j = i - 1; j >= 0; j--)
					{
						String y = (String) list.get(j);
						if (x.length() >= y.length() && x.substring(0, y.length()).equalsIgnoreCase(y))// x包含y
						{
							isRoot = false;
							break;
						}
					}
					if (!isRoot)// 不是根元素 设置这个元素为null
						list.set(i, null);
				}
	
				for (int i = 0; i < list.size(); i++)
				{
					if (list.get(i) != null)
					{
						String x = (String) list.get(i);
						rootOrgid.append(" or codeitemid='" + x + "' ");
					}
				}
	
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					if (rs != null)
					{
						rs.close();
					}
					if (conn != null)
					{
						conn.close();
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
	
			}
			
			if (rootOrgid.length() > 0)
				temp = " and (" + rootOrgid.substring(3) + ")";
		}else{
			temp = " and codeitemid=parentid ";
		}
		
		return temp;
	}

	public HashMap getExistObjs()
	{
		HashMap map = new HashMap();
		String sql ="";
		sql = "select Nbase,A0100 from S05 where s0100="+ this.S0100;
		
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next())
				map.put(rs.getString(1)+"."+rs.getString(1), "");
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}

		}

		return map;
	}
	
	/**
	 * 获得部门下的岗位信息 
	 */
	public HashMap getE01a1NullMap(String codeitemid)
	{
		HashMap map = new HashMap();				
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			String sql = "select codeitemid from organization where codesetid = '@K' and parentid = '" + codeitemid + "' ";
			
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next())
			{
				map.put(rs.getString("codeitemid"), "1");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
    public String outCodeTree()throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","codeitem");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {          
          strsql.append(getQueryString());
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            String itemid=rset.getString("codeitemid");
            if(itemid==null)
            	itemid="";
            itemid=itemid.trim();
            String codesetid=rset.getString("codesetid");
            child.setAttribute("id", codesetid+itemid);            
            String codeitemdesc=rset.getString("codeitemdesc");            
            child.setAttribute("text", codeitemdesc);
            child.setAttribute("title", itemid+":"+codeitemdesc);
            if(!itemid.equalsIgnoreCase(rset.getString("childid")))            
            	child.setAttribute("xml", "/performance/kh_plan/get_code_treeinputinfo.jsp?codesetid=" + this.codesetid + "&isfirstnode=2&codeitemid="+itemid);
            if("UN".equals(codesetid))
            	child.setAttribute("icon","/images/unit.gif");
            else if("UM".equals(codesetid))
            	child.setAttribute("icon","/images/dept.gif");
            else if("@K".equals(codesetid))
            	child.setAttribute("icon","/images/pos_l.gif");
            else
            	child.setAttribute("icon","/images/table.gif");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
        }
        catch (SQLException ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }	
    
    private String getQueryString()
    {
        StringBuffer str=new StringBuffer();
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
            if("UN".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
                str.append(this.codesetid);
                str.append("'");
            }
            else if("UM".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN') ");
            }  
            else if ("@K".equalsIgnoreCase(this.codesetid))
            {
                str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
                str.append(this.codesetid);
                str.append("' or codesetid='UN' or codesetid='UM') ");
            }               
        }
        else
        {
            str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
            str.append(this.codesetid);
            str.append("'");            
        }
        /**所有的第一层代码值列表*/
        if(this.isfirstnode!=null && "1".equals(this.isfirstnode))
        {
            str.append(this.getRootOrgNodeStr(this.userview));		
        }else
        {
	        if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		     {
		          str.append(" and parentid=codeitemid");
		     }
		     else
		     {
		            str.append(" and parentid<>codeitemid and parentid='");
		            str.append(codeitemid);
		            str.append("'");
		     }
        } 

        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
	        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	        str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	          //end.	      	
        	str.append(" ORDER BY a0000,codeitemid ");
        }
        return str.toString();
    }    
    

	
}