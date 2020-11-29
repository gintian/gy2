package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateKqEmpTreeXml  {/**
 * 执行jsp文件
 */
	 /**
     * 参数串
     */
    private String params;
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
    /**人员还是对组织*/
    private String flag;
    /**加载应用库标识
     * =0权限范围内的库
     * =1权限范围内的登录库
     * */
    private String dbtype;
    /**权限过滤标识
     * =0， 不进行权限过滤
     * =1  进行权限过滤
     * */
    private String priv="1";
    private String servlet_url="/kq/emp/load_tree";
    private boolean bfirst;
    private boolean isPost=true;
    public boolean isBfirst() {
		return bfirst;
	}

	public void setBfirst(boolean bfirst) {
		this.bfirst = bfirst;
	}
    /**
     * 构造函数
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public CreateKqEmpTreeXml(String params,String action,String target,String flag,String dbtype) {
        this.params=PubFunc.keyWord_reback(params);
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype=dbtype;
    }
    
    public CreateKqEmpTreeXml(String params,String action,String target,String flag,String dbtype,String priv) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    }
    
    public CreateKqEmpTreeXml(String params,String action,String target,String flag,String dbtype,String priv,boolean isPost) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    	this.isPost=isPost;
    }
    
    public CreateKqEmpTreeXml(String params,String action,String target,String flag) {
        this.params=PubFunc.keyWord_reback(params);
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype="0";
    }    
    /**
     * 分析是否有子节点
     * @param codeitemid
     * @param conn
     * @return
     */
    private boolean HaveChild(String codeitemid,Connection conn)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
        StringBuffer strsql=new StringBuffer();
        boolean bhave=false;
        strsql.append("select count(*) as num from organization where parentid='");
        strsql.append(codeitemid);
        strsql.append("'");
        strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
        ResultSet rset=null;
        int ncount=0;
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             rset=dao.search(strsql.toString());
             ncount=rset.getInt("num");
             //System.out.println("------->record count="+ncount);
             if(ncount<=0)
                 bhave= false;
             else
                 bhave= true;
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(SQLException sql)
            {
                sql.printStackTrace();
            }
        }

        return bhave;
    }

	private String getSelectString(String dbpre)
    {
        	StringBuffer strsql=new StringBuffer();
	        strsql.append("select distinct a0000,");
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 ,'");
	        strsql.append(dbpre);
	        strsql.append("' as dbase,");
	        strsql.append(dbpre);        
	        strsql.append("a01.b0110 b0110,e0122,");
	        strsql.append(dbpre);
	        strsql.append("a01.e01a1 e01a1,a0101 ");           	
	        strsql.append(" ");
	        return strsql.toString();
    }	
    
    private void getEmploys(UserView userview,String parentid,Element root,Connection conn)
    {
      String strsql=getPrivSql(userview, parentid,conn);     
      if("".equals(strsql))
    	  return;
      ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      try
      {
    	  rset=dao.search(strsql);
    	  while(rset.next())
    	  {
    		  String nbase=rset.getString("dbase");
    		  String a0100=rset.getString("a0100");
    		  String a0101=rset.getString("a0101");
              Element child = new Element("TreeNode");
              child.setAttribute("id", nbase+a0100);
              if(a0101==null)
            	  a0101="";
              child.setAttribute("text", a0101);
              child.setAttribute("title", a0101);
              //child.setAttribute("xml", "javascript:void(0)");
              child.setAttribute("icon","/images/man.gif");
              root.addContent(child);
    	  }
      }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
      }finally{
    	  if(rset!=null){
    		  try {
				rset.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	  }
      }
    }
    /**
     * 取得人员库列表
     * @param userview
     * @param conn
     * @return
     */
    private ArrayList getFilterDbList(UserView userview,Connection conn)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
			ArrayList dblist=userview.getPrivDbList();		
			if("0".equals(this.dbtype))
				return dblist;
			DbNameBo dbbo=new DbNameBo(conn);				
			ArrayList logdblist=dbbo.getAllLoginDbNameList();
			StringBuffer strlog=new StringBuffer();
			for(int i=0;i<logdblist.size();i++)
			{
				RecordVo vo=(RecordVo)logdblist.get(i);
				strlog.append(vo.getString("pre"));
				strlog.append(",");
			}
			String str_db=strlog.toString().toUpperCase();
			for(int j=0;j<dblist.size();j++)
			{
				String dbpre=(String)dblist.get(j);
				if(str_db.indexOf(dbpre.toUpperCase())==-1)
					continue;
				list.add(dbpre);
			}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return list;
    }
	private String getPrivSql(UserView userview, String parentid,Connection conn) {
		StringBuffer strSql=new StringBuffer();	

		try
		  {
			/**权限因子*/
			String codeid=parentid.substring(0,2);
			String codevalue=parentid.substring(2);
			StringBuffer expr=new StringBuffer();

			if("UN".equalsIgnoreCase(codeid))
			{
				expr.append("1*2|");				
				expr.append("E0122=`B0110=");
			}
			else if("UM".equalsIgnoreCase(codeid))
			{
				if (!isPost) {
					expr.append("1|");			
					expr.append("E0122=");
				} else {
					expr.append("1*2|");			
					expr.append("E01A1=`E0122=");
				}
			}
			else
			{
				expr.append("1|");				
				expr.append("E01A1=");
			}
			expr.append(codevalue+"`");
			//ArrayList dblist=getFilterDbList(userview,conn);// userview.getPrivDbList();
			HashMap formHM=new HashMap();
			ArrayList dblist=RegisterInitInfoData.getB0110Dase(formHM,userview,conn,parentid);
			//System.out.println(dblist);
			if(dblist.size()==0)
				return "";
			ArrayList fieldlist=new ArrayList();
			String strWhere=null;
			String strSelect=null;
			/**加权限过滤*/
			if("1".equals(priv))
			{
				for(int i=0;i<dblist.size();i++)
				{        	
					if(userview.getKqManageValue()!=null&&!"".equals(userview.getKqManageValue()))
						strWhere=userview.getKqPrivSQLExpression(expr.toString(),(String)dblist.get(i),fieldlist);
			        else
					  strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
			  	    strSelect=getSelectString((String)dblist.get(i));
				    strSql.append(strSelect);
				    strSql.append(strWhere);         
				    strSql.append(" UNION ");         		
				}
			}
			else
			{
	      	   for(int i=0;i<dblist.size();i++)
	     	   {
	         	   FactorList factor_bo=new FactorList(expr.toString(),(String)dblist.get(i),false,false,true,1,userview.getUserId());
	         	   strWhere=factor_bo.getSqlExpression();
			  	   strSelect=getSelectString((String)dblist.get(i));         	   
	         	   strSql.append(strSelect);
	         	   strSql.append(strWhere);         
	         	   strSql.append(" UNION ");
	     	   }
			}
		    strSql.setLength(strSql.length()-7);
		    strSql.append(" order by dbase desc,a0000");   

		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		  return strSql.toString() ;		  
	}
    public String outOrgEmployTree(UserView userview,String parentid)throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate =sdf.format(new Date());
        try
        {
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
          strsql.append(params);
          strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
          if(!isPost)
        	  strsql.append(" and codesetid<>'@K'");
          strsql.append(" ORDER BY a0000,codeitemid");
          
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          /**加载组织机构树*/
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            if(!(this.action==null|| "".equals(this.action)))
            {
		        if(this.action.indexOf('?')==0)
		        	theaction=this.action+"?a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
		        else
		        	theaction=this.action+"&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
            }
            if(theaction==null|| "".equals(theaction))
            	child.setAttribute("href", "javascript:void(0)");            	
            else
            	child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            
    		String url=this.servlet_url+"?";
    		String urlParam = "first=1&target="+this.target+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="+this.priv;
	        urlParam=urlParam+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
	        urlParam=urlParam+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
	        url = url + "encryptParam=" + PubFunc.encrypt(urlParam);
	        
            child.setAttribute("xml", url);
            if("UN".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/unit.gif");
            if("UM".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/dept.gif");
            if("@K".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/pos_l.gif");
            root.addContent(child);
          }
          /**加载当前机构下的人员*/
          if("1".equals(flag)&&this.isBfirst())
        	  getEmploys(userview,parentid,root,conn);
          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            PubFunc.closeResource(rset);
            PubFunc.closeResource(conn);
        }
        return xmls.toString();        
    }
    
    public String outOrganizationTree()throws GeneralException
    {
  	
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate =sdf.format(new Date());
        try
        {
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
          strsql.append(params);
          strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");

          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemid")+":"+rset.getString("codeitemdesc"));

            theaction=this.action+"?b_query=link&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            //if(!HaveChild(rset.getString("codeitemid"),conn))
            child.setAttribute("xml", "/system/security/get_org_tree.jsp?encryptParam="+PubFunc.encrypt("first=1&flag="+flag+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'"));
            if("UN".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/unit.gif");
            if("UM".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/dept.gif");
            if("@K".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/pos_l.gif");
            
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);

          xmls.append(outputter.outputString(myDocument));

          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            PubFunc.closeResource(rset);
            PubFunc.closeResource(conn);
        }
        return xmls.toString();        
    }

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

}
