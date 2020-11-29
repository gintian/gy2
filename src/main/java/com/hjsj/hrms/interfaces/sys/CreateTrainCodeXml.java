package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:CreateCodeXml
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 16, 2005:5:08:57 PM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateTrainCodeXml
{
    /** 代码类 */
    private String codesetid;

    /** 代码项 */
    private String codeitemid;

    /** 根据管理范围找第一层代码 */
    private String privflag;
    /**标识，0为培训中的岗位*/
    private String flag;
    private boolean zFlag = false;

    /*
         * 为了显示第一层为管理的最高层的节点
         */
    private String isfirstnode;
    
    private UserView userView;

    /**
         * 
         */
    public CreateTrainCodeXml(String codesetid, String codeitemid)
    {

	this.codeitemid = codeitemid;
	this.codesetid = codesetid;
    }
    /**
     * 
     * @param codesetid  相关代码类
     * @param codeitemid 
     * @param privflag
     */
    public CreateTrainCodeXml(String codesetid, String codeitemid, String privflag)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
    }
    
    public CreateTrainCodeXml(String codesetid, String codeitemid, String privflag, UserView userView)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
	this.userView = userView;
    }

    public CreateTrainCodeXml(String codesetid, String codeitemid, String privflag, String isfirstnode)
    {

	this(codesetid, codeitemid);
	this.privflag = privflag;
	this.isfirstnode = isfirstnode;
    }

    /** 求查询代码的字符串 */
    private String getQueryString()
    {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String backdate =sdf.format(new Date());
	StringBuffer str = new StringBuffer();
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    if ("UN".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
		str.append(this.codesetid);
		str.append("'");
	    } else if ("UM".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN') ");
	    } else if ("@K".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN' or codesetid='UM') ");
	    }
	    str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	} else if ("1_".equals(this.codesetid.substring(0, 2)))
	{
	    String codeset = codesetid.replace("1_", "");
	    String[] rel = relatingcode(codeset);
	    str.append("select '" + codesetid + "' as codesetid,");
	    str.append(rel[1] + " as codeitemid," + rel[2] + " as codeitemdesc,");
	    str.append(rel[1] + " as childid from " + rel[0]);
	}
	else if("@@".equalsIgnoreCase(this.codesetid))//人员库
	{
	    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname order by dbid");
	    return str.toString();
	}
	else
	{
	    str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
	    str.append(this.codesetid);
	    str.append("'");
	}
	/** 所有的第一层代码值列表 */
	if (!"1_".equals(this.codesetid.substring(0, 2)))
	{
	    if (privflag == null || "".equals(privflag))
	    {
		if (this.isfirstnode != null && "1".equals(this.isfirstnode))
		{
		    if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
		    {
			str.append(" and parentid=codeitemid");
		    } else
		    {
			str.append(" and codeitemid='");
			str.append(codeitemid);
			str.append("'");
		    }
		} else
		{
		    if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
		    {
				if(userView!=null&&!userView.isSuper_admin()){
					TrainCourseBo bo = new TrainCourseBo(userView);
					String priv = "";
                    try {
                        priv = bo.getUnitIdByBusi();
                    } catch (GeneralException e) {
                        e.printStackTrace();
                    }
					if(priv!=null&&priv.length()>2&&priv.indexOf("UN`")==-1){
						String tmp[] = priv.split("`");
						String t = "";
						for (int i = 0; i < tmp.length; i++) {
							t+=tmp[i].substring(2)+",";
						}
						t = t.substring(0,t.length()-1);
						str.append(" and codeitemid in ('"+t.replaceAll(",", "','")+"')");
					}else
						str.append(" and parentid=codeitemid");
				}else
					str.append(" and parentid=codeitemid");
		    } else
		    {
			str.append(" and parentid<>codeitemid and parentid='");
			str.append(codeitemid);
			str.append("'");
		    }
		}
	    } else
	    // 根据管理范围过滤相应的节点内容
	    {
		if ("ALL".equals(this.codeitemid))
		{
		    str.append(" and parentid=codeitemid");
		} else if (this.codeitemid == null || "".equals(this.codeitemid))
		{
		    str.append(" and 1=2");
		} else
		{
		    str.append(" and codeitemid in ('");
		    str.append(codeitemid);
		    str.append("')");
		}
	    }
	}
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    str.append(" ORDER BY a0000,codeitemid ");
	}else if(!"@@".equalsIgnoreCase(this.codesetid))
	{
		 str.append(" ORDER BY codeitemid ");
	}
	return str.toString();
    }
    /** 求查询代码的字符串 增加管理范围筛选*/
    private String getQueryStringsx(UserView userView)
    {

	StringBuffer str = new StringBuffer();
	String acodeid=userView.getManagePrivCode();
    String acodevalue=userView.getManagePrivCodeValue();
//    String oiu =userView.getUserOrgId();
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    if ("UN".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where codesetid='");
		str.append(this.codesetid);
		str.append("'");
	    } else if ("UM".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN') ");
	    } else if ("@K".equalsIgnoreCase(this.codesetid))
	    {
		str.append("select codesetid,codeitemid,codeitemdesc,childid from organization where (codesetid='");
		str.append(this.codesetid);
		str.append("' or codesetid='UN' or codesetid='UM') ");
	    }
	} else if ("1_".equals(this.codesetid.substring(0, 2)))
	{
	    String codeset = codesetid.replace("1_", "");
	    String[] rel = relatingcode(codeset);
	    str.append("select '" + codesetid + "' as codesetid,");
	    str.append(rel[1] + " as codeitemid," + rel[2] + " as codeitemdesc,");
	    str.append(rel[1] + " as childid from " + rel[0]);
	}
	else if("@@".equalsIgnoreCase(this.codesetid))//人员库
	{
	    str.append("select '@@' codesetid,Pre  codeitemid, dbname  codeitemdesc,Pre  childid from dbname order by dbid");
	    return str.toString();
	}
	else
	{
	    str.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
	    str.append(this.codesetid);
	    str.append("'");
	}
	/** 所有的第一层代码值列表 */
	if (!"1_".equals(this.codesetid.substring(0, 2)))
	{
	    if (privflag == null || "".equals(privflag))
	    {
		if (this.isfirstnode != null && "1".equals(this.isfirstnode))
		{
		    if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
		    {
			str.append(" and parentid=codeitemid");
		    } else
		    {
			str.append(" and codeitemid='");
			str.append(codeitemid);
			str.append("'");
		    }
		} else
		{
			if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
			{
				if(userView.isSuper_admin())
				{
					 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
					    {
						str.append(" and parentid=codeitemid");
					    } else
					    {
						str.append(" and parentid<>codeitemid and parentid='");
						str.append(codeitemid);
						str.append("'");
					    }
				}else if("UN".equalsIgnoreCase(acodeid)&& "".equalsIgnoreCase(acodevalue))
				{
					 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
					    {
						str.append(" and parentid=codeitemid");
					    } else
					    {
						str.append(" and parentid<>codeitemid and parentid='");
						str.append(codeitemid);
						str.append("'");
					    }
				}else
				{
					if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
				    {
//						 and parentid=codeitemid 
					str.append(" and codeitemid='");
					str.append(acodevalue+"' ");
				    } else
				    {
//				    str.append("and parentid ='"+acodevalue+"'");
					str.append(" and parentid<>codeitemid and parentid='");
					str.append(codeitemid);
					str.append("'");
				    }
				}
			}else
			{
				 if (this.codeitemid == null || "".equals(this.codeitemid) || "ALL".equals(this.codeitemid))
				    {
					str.append(" and parentid=codeitemid");
				    } else
				    {
					str.append(" and parentid<>codeitemid and parentid='");
					str.append(codeitemid);
					str.append("'");
				    }
			}
			
		   
		}
	    } else
	    // 根据管理范围过滤相应的节点内容
	    {
		if ("ALL".equals(this.codeitemid))
		{
		    str.append(" and parentid=codeitemid");
		} else if (this.codeitemid == null || "".equals(this.codeitemid))
		{
		    str.append(" and 1=2");
		} else
		{
		    str.append(" and codeitemid='");
		    str.append(codeitemid);
		    str.append("'");
		}
	    }
	}
	if ("UN".equalsIgnoreCase(this.codesetid) || "UM".equalsIgnoreCase(this.codesetid) || "@K".equalsIgnoreCase(this.codesetid))
	{
	    str.append(" ORDER BY a0000,codeitemid ");
	}
	return str.toString();
    }
    /**
     * 当前对象是否有
     * @param priv_str ，用户已授权的管理范围串 ,UN2020,UN30,UM03030,
     * @param func_id
     * @return
     */
    private boolean haveTheOrgID(String priv_str,String org_id)
    {
    	priv_str=","+priv_str+",";
    	if(priv_str.indexOf(","+org_id+",")==-1)
    		return false;
    	else
    		return true;
    }    
    /**
     * 输出json代码树
     * @selectedstr  已选中的单位或部门节点
     * @return
     * @throws GeneralException
     */
    public String outJSonCodeTree(String selectedstr) throws GeneralException
    {
    	StringBuffer strsql = new StringBuffer();
    	ResultSet rset = null;
    	Connection conn = AdminDb.getConnection();
        StringBuffer tmp=new StringBuffer();
        StringBuffer buf=new StringBuffer();	   
    	try
    	{
    	    strsql.append(getQueryString());
    	    ContentDAO dao = new ContentDAO(conn);
    	    rset = dao.search(strsql.toString());
        
	        String iconurl="";
    	    while (rset.next())
    	    {
        		String codesetid = rset.getString("codesetid");
        		String itemid = rset.getString("codeitemid");
        		if (itemid == null)
        		    itemid = "";
        		itemid = itemid.trim();        		
    	    	tmp.append("{id:'");
	            tmp.append(codesetid+itemid);
	            tmp.append("',text:'");
	            tmp.append(rset.getString("codeitemdesc"));
	            tmp.append("'");    
	            
    		    if (!itemid.equalsIgnoreCase(rset.getString("childid")))
  		          tmp.append(",leaf:false");
    		    else
  		          tmp.append(",leaf:true"); 
    		    if(!haveTheOrgID(selectedstr,codesetid+itemid))
    		      tmp.append(",checked:false");
    		    else
  		          tmp.append(",checked:true");     		    	
	    		if ("UN".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else if ("UM".equals(codesetid))
	    			iconurl="/images/dept.gif";
	    		else if ("@K".equals(codesetid))
	    			iconurl="/images/pos_l.gif";
	    		else if ("64".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else if ("65".equals(codesetid))
	    			iconurl="/images/unit.gif";
	    		else
	    			iconurl="/images/table.gif";
	    		tmp.append(",icon:'");
	    		tmp.append(iconurl);
	    		tmp.append("'");   
	    		//tmp.append(",singleClickExpand:true");
   		
		        tmp.append("}");
		        tmp.append(",");
    	    }
	        if(tmp.length()>0)
	        {
	        	tmp.setLength(tmp.length()-1);
	        	buf.append("[");
	        	buf.append(tmp.toString());
	        	buf.append("]");
	        }

    	} catch (SQLException ee)
    	{
    	    ee.printStackTrace();
    	    GeneralExceptionHandler.Handle(ee);
    	} finally
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
    	    } catch (SQLException ee)
    	    {
    		ee.printStackTrace();
    	    }

    	}
    	return buf.toString();    	
    }
    /**
     * 输出xml串
     * @return
     * @throws GeneralException
     */
    public String outCodeTree() throws GeneralException
    {

	StringBuffer xmls = new StringBuffer();
	StringBuffer strsql = new StringBuffer();

	ResultSet rset = null;

	ResultSet rset2 = null;
	// 保存有培训课程的岗位代码
	HashMap map = new HashMap();
	Connection conn = AdminDb.getConnection();
	Element root = new Element("TreeNode");

	root.setAttribute("id", "$$00");
	root.setAttribute("text", "root");
	root.setAttribute("title", "codeitem");
	Document myDocument = new Document(root);
	String theaction = null;
	try
	{
		ContentDAO dao = new ContentDAO(conn);
		TrainStationBo trainStationBo = new TrainStationBo();
		HashMap mapg=trainStationBo.getStationSett(conn);
		String postSetId=(String)mapg.get("post_setid");//岗位培训子集编号
		String postCloumn=(String)mapg.get("post_coursecloumn");//岗位培训子集中参培课程指标
		if (postSetId != null && postSetId.length() > 0 && postCloumn != null && postCloumn.length() > 0) {
			this.zFlag = true;
		}
		if ("0".equals(this.flag) && this.zFlag) {
			
			strsql.append("select e01a1 from ");
			strsql.append(postSetId);
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
				strsql.append(" where "+postCloumn+" is not null");
			else
				strsql.append(" where "+Sql_switcher.isnull(postCloumn, "''")+"<>''");
			strsql.append(" group by e01a1");

			rset2 = dao.search(strsql.toString());
			while (rset2.next()) {
				map.put(rset2.getString("e01a1"), "");
			}
			
			strsql.delete(0,strsql.length());
		}
	    strsql.append(getQueryString());

	    rset = dao.search(strsql.toString());
	    while (rset.next())
	    {
		Element child = new Element("TreeNode");
		String itemid = rset.getString("codeitemid");
		if (itemid == null)
		    itemid = "";
		itemid = itemid.trim();
		
		
		
		String codesetid = rset.getString("codesetid");
		
		if (! map.containsKey(itemid) && "0".equals(this.flag) && "@K".equalsIgnoreCase(codesetid)) {
			continue;
		}
		child.setAttribute("id", codesetid + itemid);
		child.setAttribute("text", rset.getString("codeitemdesc"));
		child.setAttribute("title", itemid + ":" + rset.getString("codeitemdesc"));
		if (!itemid.equalsIgnoreCase(rset.getString("childid")))
		    child.setAttribute("xml", "/system/get_code_tree_train.jsp?codesetid=" + this.codesetid + "&codeitemid=" + itemid + "&flag=" + this.flag);
		if ("UN".equals(codesetid))
		    child.setAttribute("icon", "/images/unit.gif");
		else if ("UM".equals(codesetid))
		    child.setAttribute("icon", "/images/dept.gif");
		else if ("@K".equals(codesetid))
		    child.setAttribute("icon", "/images/pos_l.gif");
		else
		    child.setAttribute("icon", "/images/table.gif");
		root.addContent(child);
	    }

	    XMLOutputter outputter = new XMLOutputter();
	    Format format = Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	    outputter.setFormat(format);
	    xmls.append(outputter.outputString(myDocument));
	    // System.out.println("SQL=" +xmls.toString());
	} catch (SQLException ee)
	{
	    ee.printStackTrace();
	    GeneralExceptionHandler.Handle(ee);
	} finally
	{
	    try
	    {
	    	if (rset2 != null)
			{
			    rset2.close();
			}
			
		if (rset != null)
		{
		    rset.close();
		}
		if (conn != null)
		{
		    conn.close();
		}
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }

	}
	return xmls.toString();
    }
    /**
     * 输出xml串 增加管理范围筛选
     * @return
     * @throws GeneralException
     */
    public String outCodeTreesx(UserView userView) throws GeneralException
    {

	StringBuffer xmls = new StringBuffer();
	StringBuffer strsql = new StringBuffer();
	ResultSet rset = null;
	Connection conn = AdminDb.getConnection();
	Element root = new Element("TreeNode");

	root.setAttribute("id", "$$00");
	root.setAttribute("text", "root");
	root.setAttribute("title", "codeitem");
	Document myDocument = new Document(root);
	String theaction = null;
	try
	{

	    strsql.append(getQueryStringsx(userView));
	   
	    ContentDAO dao = new ContentDAO(conn);
	    rset = dao.search(strsql.toString());
	    while (rset.next())
	    {
		Element child = new Element("TreeNode");
		String itemid = rset.getString("codeitemid");
		if (itemid == null)
		    itemid = "";
		itemid = itemid.trim();
		String codesetid = rset.getString("codesetid");
		child.setAttribute("id", codesetid + itemid);
		child.setAttribute("text", rset.getString("codeitemdesc"));
		child.setAttribute("title", itemid + ":" + rset.getString("codeitemdesc"));
		if (!itemid.equalsIgnoreCase(rset.getString("childid")))
		    child.setAttribute("xml", "/system/get_code_tree_filter.jsp?codesetid=" + this.codesetid/* rset.getString("codesetid") */+ "&codeitemid=" + itemid);
		if ("UN".equals(codesetid))
		    child.setAttribute("icon", "/images/unit.gif");
		else if ("UM".equals(codesetid))
		    child.setAttribute("icon", "/images/dept.gif");
		else if ("@K".equals(codesetid))
		    child.setAttribute("icon", "/images/pos_l.gif");
		else
		    child.setAttribute("icon", "/images/table.gif");
		root.addContent(child);
	    }

	    XMLOutputter outputter = new XMLOutputter();
	    Format format = Format.getPrettyFormat();
	    format.setEncoding("UTF-8");
	    outputter.setFormat(format);
	    xmls.append(outputter.outputString(myDocument));
	    // System.out.println("SQL=" +xmls.toString());
	} catch (SQLException ee)
	{
	    ee.printStackTrace();
	    GeneralExceptionHandler.Handle(ee);
	} finally
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
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }

	}
	return xmls.toString();
    }
    public String[] relatingcode(String codeset)
    {

	String[] rel = new String[3];
	ResultSet rset = null;
	Connection conn = null;
	try
	{
	    conn = AdminDb.getConnection();
	    ContentDAO dao = new ContentDAO(conn);
	    StringBuffer buf = new StringBuffer();
	    buf.append("select codetable,codevalue,codedesc from t_hr_relatingcode where");
	    buf.append(" codesetid='");
	    buf.append(codeset);
	    buf.append("'");
	    rset = dao.search(buf.toString());
	    String codetable = "";
	    String codevalue = "";
	    String codedesc = "";
	    while (rset.next())
	    {
		codetable = rset.getString("codetable");
		codetable = codetable != null ? codetable : "";
		codevalue = rset.getString("codevalue");
		codevalue = codevalue != null ? codevalue : "";
		codedesc = rset.getString("codedesc");
		codedesc = codedesc != null ? codedesc : "";
	    }
	    rel[0] = codetable;
	    rel[1] = codevalue;
	    rel[2] = codedesc;

	} catch (GeneralException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SQLException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally
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
	    } catch (SQLException ee)
	    {
		ee.printStackTrace();
	    }
	}
	return rel;
    }

    public String getCodeitemid()
    {

	return codeitemid;
    }

    public void setCodeitemid(String codeitemid)
    {

	this.codeitemid = codeitemid;
    }

    public String getCodesetid()
    {

	return codesetid;
    }

    public void setCodesetid(String codesetid)
    {

	this.codesetid = codesetid;
    }
	public void setFlag(String flag) {
		this.flag = flag;
	}

}
