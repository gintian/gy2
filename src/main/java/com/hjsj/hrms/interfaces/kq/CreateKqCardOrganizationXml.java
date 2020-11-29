package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateKqCardOrganizationXml {
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
    public CreateKqCardOrganizationXml(String params,String action,String target,String flag,String dbtype) {
        this.params=params;
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype=dbtype;
        KqParameter para = new KqParameter();
        this.isPost="1".equalsIgnoreCase(para.getKq_orgView_post())?false:true;
    }
    
    public CreateKqCardOrganizationXml(String params,String action,String target,String flag) {
        this.params=params;
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype="0";
    }    
    /**
     * 得到连接的人员节点
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     */
    private void getEmploys_Href(UserView userview,String parentid,Element root,Connection conn,String kq_type)
    {
      String strsql=getPrivSql(userview, parentid,conn,kq_type);      
      if("".equals(strsql))
    	  return;
      ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      String theaction=null;
      try
      {
    	  rset=dao.search(strsql);
    	  //System.out.println(strsql);
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
              child.setAttribute("target", this.target);
              if(!(this.action==null|| "".equals(this.action)))
              {
  		        theaction=this.action+"?b_search=link&encryptParam="+PubFunc.encrypt("a_code=EP"+rset.getString("a0100")+"&nbase="+nbase);
              }
              if(theaction==null|| "".equals(theaction))
              	child.setAttribute("href", "javascript:void(0)");            	
              else
              	child.setAttribute("href", theaction);             
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  }
      }
    }
    
    public String outOrgEmpTree(UserView userview,String parentid,String kq_type)throws GeneralException
    {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        Connection conn = AdminDb.getConnection();        	
        ResultSet rset = null;
        
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
            strsql.append(" ORDER BY a0000,codeitemid ");
            
            ContentDAO dao = new ContentDAO(conn);
            rset = dao.search(PubFunc.keyWord_reback(strsql.toString()));
            /**加载组织机构树*/
            while (rset.next())
            {
               Element child = new Element("TreeNode");
               child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
               child.setAttribute("text", rset.getString("codeitemdesc"));
               child.setAttribute("title", rset.getString("codeitemdesc"));
               if(!(this.action==null|| "".equals(this.action)))
               {
  		          theaction=this.action+"?b_search=link&encryptParam="+PubFunc.encrypt("a_code="+rset.getString("codesetid")+rset.getString("codeitemid"));
               }
               if(theaction==null|| "".equals(theaction))
              	 child.setAttribute("href", "javascript:void(0)");            	
               else
              	 child.setAttribute("href", theaction);
               child.setAttribute("target", this.target);
               //xiexd 2014.09.28加密路径参数
      		   String url="/common/cardemp/loadtree?first=1&";
      		   String str = "target="+this.target+"&flag="+this.flag+"&dbtype="+this.dbtype+"&action="+this.action;
      		   str=str+"&kq_type="+kq_type+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
      		   str=str+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
      		   url = url+"encryptParam="+PubFunc.encrypt(str);
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
    	       getEmploys_Href(userview,parentid,root,conn,kq_type);               
           
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
    private String getPrivSql(UserView userview, String parentid,Connection conn,String kq_type) {
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
				if(!isPost){
					expr.append("1|");			
					expr.append("E0122=");
				}else{
					expr.append("1*2|");			
					expr.append("E01A1=`E0122=");
				}
			}
			else if("GP".equalsIgnoreCase(codeid))
			{
				expr.append("1*2|");			
				expr.append("E0122=`B0110=");
			}else
			{
				expr.append("1|");				
				expr.append("E01A1=");
			}
			expr.append(codevalue+"`");
			ArrayList dblist=getFilterDbList(userview,parentid,conn);// userview.getPrivDbList();
			if(dblist.size()==0)
				return "";
			ArrayList fieldlist=new ArrayList();
			String strWhere=null;
			String strSelect=null;

			for(int i=0;i<dblist.size();i++)
			{        	
				if(userview.getKqManageValue()!=null&&!"".equals(userview.getKqManageValue()))
					strWhere=userview.getKqPrivSQLExpression(expr.toString(),(String)dblist.get(i),fieldlist);
		        else
				  strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
		  	    strSelect=getSelectString((String)dblist.get(i));
			    strSql.append(strSelect);
			    strSql.append(strWhere);  
			    if(kq_type!=null&&kq_type.length()>0)
			    {
			    	strSql.append(" and ("+kq_type+"='02'");			    	
			    	ArrayList datelist =RegisterDate.getKqDayList(conn);
			    	if(datelist!=null&&datelist.size()==2)
			    	{
			    		String start=(String)datelist.get(0);
			    		String end=(String)datelist.get(1);
			    		strSql.append(" or a0100 in (select DISTINCT a0100 from q03 ");
			    		strSql.append(" where nbase='"+dblist.get(i)+"' and  q03z0>='"+start+"' and q03z0<='"+end+"'");
			    		strSql.append(" and q03z3='02')");
			    	}
			    	strSql.append(")");
			    }
			    strSql.append(" UNION ");         		
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
    /**
     * 取得人员库列表
     * @param userview
     * @param conn
     * @return
     */
    private ArrayList getFilterDbList(UserView userview,String parentid,Connection conn)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		HashMap formHM=new HashMap();
    		list=RegisterInitInfoData.getB0110Dase(formHM,userview,conn,parentid);				
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return list;
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
	private void removeEmp(ArrayList group_list,Connection conn,Document doc)
	{
		if(group_list==null||group_list.size()<=0)
		{
			return;
		}else
		{
			String group_id="";
			String sql="";
			ContentDAO dao=new ContentDAO(conn);
	        RowSet rset=null;
	        String id="";
	        try
	        {
	        	for(int i=0;i<group_list.size();i++)
				{
					group_id=group_list.get(i).toString();
					sql="select a0100,nbase from kq_group_emp where group_id='"+group_id+"'";
					rset=dao.search(sql);
					while(rset.next())
					{
						id=rset.getString("nbase")+rset.getString("a0100");
						deleteParams(id,doc);
					}
				}
	        }catch(Exception e)
	        {
	        	e.printStackTrace();
	        }finally{
	      	  if(rset!=null){
	    		  try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
		}
	}
	/**
	 * 删除指定的用户配置信息
	 * 
	 * @param userid
	 *            用户ID
	 * @return
	 */
	public void deleteParams(String id,Document doc) 
	{
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/TreeNode/TreeNode[@id='");
		temp1.append(id);
		temp1.append("']");		
		char o='a';
		
		try {
			// 查找特定用户
			XPath xPath = XPath.newInstance(temp1.toString());
			Element TreeNode = (Element) xPath.selectSingleNode(doc);
			if(TreeNode == null){
			}else{				
				Element param = doc.getRootElement();
				param.removeContent(TreeNode);
			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	 
}

