package com.hjsj.hrms.interfaces.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
/**
 * <p>Title:CreateCodeXml</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 16, 2005:5:08:57 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateCodeXmlOrg {
    /**代码类*/
    private String codesetid;
    /**代码项*/
    private String codeitemid;
    /**根据管理范围找第一层代码*/
    private String privflag;
    /*
     * 为了显示第一层为管理的最高层的节点
     * */
    private String isfirstnode;
    /**
     * 
     */
    private boolean isAll;
    public CreateCodeXmlOrg(String codesetid,String codeitemid) {
        this.codeitemid=codeitemid;
        this.codesetid=codesetid;
    }
    
    public CreateCodeXmlOrg(String codesetid,String codeitemid,String privflag) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    }  
    public CreateCodeXmlOrg(String codesetid,String codeitemid,String privflag,String isfirstnode) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.isfirstnode=isfirstnode;
    } 
    public CreateCodeXmlOrg(String codesetid,String codeitemid,String privflag,String isfirstnode,boolean isAll) {
    	this(codesetid,codeitemid);
    	this.privflag=privflag;
    	this.isfirstnode=isfirstnode;
    	this.isAll=isAll;
    } 
    /**求查询代码的字符串*/
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
        if(privflag==null|| "".equals(privflag))
        {
        	if(this.isfirstnode!=null && "1".equals(this.isfirstnode))
            {
            	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
            		if(!this.isAll)
		              str.append(" and 1=2");
            		else
            			str.append(" and parentid=codeitemid");
		        }
		        else
		        {
		        	if(codeitemid.indexOf("`")==-1)
		        	{
			            str.append(" and codeitemid='");
			            if(this.codeitemid.indexOf("UN")!=-1||this.codeitemid.indexOf("UM")!=-1){
			            	str.append(codeitemid.substring(2));
			            }else{
			            	str.append(codeitemid);
			            }
			            str.append("'");
		        	}
		        	else
		        	{
		        		StringBuffer tempSql=new StringBuffer("");
		        		String[] temp=codeitemid.split("`");
		        		for(int i=0;i<temp.length;i++)
		        		{
		        			//tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'"); //招聘管理/招聘职位中岗位筛选，业务用户如果操作单位设置为全部，控件不显示操作单位，因为codeitemid='UN' dml 2011年9月5日13:30:12
		        			if(temp.length==1){
		        				if("UN".equalsIgnoreCase(temp[i])){
		        					tempSql.append(" or codeitemid=parentid");
		        				}else{
		        					tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
		        				}
		        			}else{
		        				tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
		        			}
		        		}
		        		str.append(" and ( "+tempSql.substring(3)+" ) ");
		        		
		        	}
		        } 
            	//System.out.println(str.toString());
            }else
            {
	        	if(this.codeitemid==null|| "".equals(this.codeitemid)|| "ALL".equals(this.codeitemid))
		        {
	        		if(!this.isAll)
			              str.append(" and 1=2");
	            	else
	            		str.append(" and parentid=codeitemid");
		        }
		        else
		        {
		        	/*str.append(" and parentid<>codeitemid and parentid='");
		            if(this.codeitemid.indexOf("UN")!=-1||this.codeitemid.indexOf("UM")!=-1){
		            	str.append(codeitemid.substring(2));
		            }else{
		            	str.append(codeitemid);
		            }
		            str.append("'");*/
		            if(codeitemid.indexOf("`")==-1)
		        	{
			            str.append(" and parentid<>codeitemid and parentid='");
			            if(this.codeitemid.indexOf("UN")!=-1||this.codeitemid.indexOf("UM")!=-1){
			            	str.append(codeitemid.substring(2));
			            }else{
			            	str.append(codeitemid);
			            }
			            str.append("'");
		        	}
		        	else
		        	{
		        		StringBuffer tempSql=new StringBuffer("");
		        		String[] temp=codeitemid.split("`");
		        		for(int i=0;i<temp.length;i++)
		        		{
		        			//tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'"); //招聘管理/招聘职位中岗位筛选，业务用户如果操作单位设置为全部，控件不显示操作单位，因为codeitemid='UN' dml 2011年9月5日13:30:12
		        			if(temp.length==1){
		        				if("UN".equalsIgnoreCase(temp[i])){
		        					tempSql.append(" or codeitemid=parentid");
		        				}else{
		        					tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
		        				}
		        			}else{
		        				tempSql.append(" or codeitemid='"+temp[i].substring(2)+"'");
		        			}
		        		}
		        		str.append(" and ( "+tempSql.substring(3)+" ) ");
		        		
		        	}
		        }
	        	//System.out.println("ww" + str.toString());
        	} 
        }
        else //根据管理范围过滤相应的节点内容
        {
            if("ALL".equals(this.codeitemid))
	        {
            	   str.append(" and parentid=codeitemid");
	        }
            else if(this.codeitemid==null|| "".equals(this.codeitemid))
            {
         	   str.append(" and 1=2");            	
            }
	        else
	        {
	        	str.append(" and codeitemid='");
	        	if(this.codeitemid.indexOf("UN")!=-1||this.codeitemid.indexOf("UM")!=-1){
	            	str.append(codeitemid.substring(2));
	            }else{
	            	str.append(codeitemid);
	            }
	        	str.append("'");
	        }
        }
        if("UN".equalsIgnoreCase(this.codesetid)|| "UM".equalsIgnoreCase(this.codesetid)|| "@K".equalsIgnoreCase(this.codesetid))
        {
	          /**组织机构历史点控制-20091130*/
	        String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	        str.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	          //end.	      	
        	str.append(" ORDER BY a0000,codeitemid ");
        }
        return str.toString();
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
//          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from codeitem where codesetid='");
//          
//          strsql.append(this.codesetid);
//          /**所有的第一层代码值列表*/
//          if(this.codeitemid==null||this.codeitemid.equals("")||this.codeitemid.equals("ALL"))
//          {
//              strsql.append("' and parentid=codeitemid");
//          }
//          else
//          {
//              strsql.append("' and parentid<>codeitemid and parentid='");
//              strsql.append(codeitemid);
//              strsql.append("'");
//          }
          
          //System.out.println("SQL="+strsql.toString());
          ContentDAO dao = new ContentDAO(conn);
          CreateCodeXml codeVO = new CreateCodeXml(this.codesetid, "");
          Integer  leaf_only = codeVO.getSelectFlag(dao);
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
            // 49536 库中的岗位没有岗位名称 节点无法添加null 导致报错
            String codeitemdesc = rset.getString("codeitemdesc");
            codeitemdesc = StringUtils.isBlank(codeitemdesc) ? "" : codeitemdesc;
            child.setAttribute("text", codeitemdesc);
            child.setAttribute("title", itemid+":"+codeitemdesc);
            if(!itemid.equalsIgnoreCase(rset.getString("childid")))            
            	child.setAttribute("xml", "/system/get_code_treeinputinfo.jsp?codesetid=" + this.codesetid + "&isfirstnode=2"/*rset.getString("codesetid")*/+"&codeitemid="+itemid);
            if("UN".equals(codesetid))
            	child.setAttribute("icon","/images/unit.gif");
            else if("UM".equals(codesetid))
            	child.setAttribute("icon","/images/dept.gif");
            else if("@K".equals(codesetid))
            	child.setAttribute("icon","/images/pos_l.gif");
            else
            	child.setAttribute("icon","/images/table.gif");
        	//leaf_only：只能选择末级代码项    =1 ：是 =0：否
    		child.setAttribute("selectable","true");//默认可以选
    		String tempcodeitemid = codeVO.getTempCodeItemid(codesetid,itemid);
    		if(leaf_only == 1 && !"".equals(tempcodeitemid))//如果是只能选择末级代码项 且 当前节点为非叶子节点
    			child.setAttribute("selectable","false");
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
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

    
    public String getCodeitemid() {
        return codeitemid;
    }
    public void setCodeitemid(String codeitemid) {
        this.codeitemid = codeitemid;
    }
    public String getCodesetid() {
        return codesetid;
    }
    public void setCodesetid(String codesetid) {
        this.codesetid = codesetid;
    }

	public boolean isAll() {
		return isAll;
	}

	public void setAll(boolean isAll) {
		this.isAll = isAll;
	}

}
