package com.hjsj.hrms.transaction.general.inform.synthesisbrowse;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class QueryEmployNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String queryname=(String)this.getFormHM().get("queryname");
		if(queryname!=null&&queryname.length()>0)
		{
			queryname=SafeCode.decode(queryname);
			queryname=PubFunc.getStr(queryname);
			queryname=queryname.replace(" ", "");
			queryname=queryname.replace("　", "");
		}
		String dbpre=(String)this.getFormHM().get("dbpre");
		getEmploys(queryname,dbpre);

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
	private String getPrivSql(String queryname,String dbpre) {
		StringBuffer strSql=new StringBuffer();	

		try
		  {			
			//ArrayList dblist=userView.getPrivDbList();		
			if(dbpre==null || dbpre.length()!=3)
				return "";
			ArrayList fieldlist=new ArrayList();
			String strWhere=null;
			String strSelect=null;
			StringBuffer expr=new StringBuffer();

			if("UN".equalsIgnoreCase(userView.getManagePrivCode()))
			{
				expr.append("1+2|");				
				expr.append("E0122=`B0110=");
			}
			else if("UM".equalsIgnoreCase(userView.getManagePrivCode()))
			{
				expr.append("1+2|");			
				expr.append("E01A1=`E0122=");
			}
			else
			{
				expr.append("1|");				
				expr.append("E01A1=");
			}
			expr.append(userView.getManagePrivCodeValue()+"*`");
			//for(int i=0;i<dblist.size();i++)
			{        	
				strWhere=userView.getPrivSQLExpression(expr.toString(),dbpre,false,true,fieldlist);
		  	    strSelect=getSelectString(dbpre);
			    strSql.append(strSelect);
			    strSql.append(strWhere + " and "); 
			    strSql.append(dbpre);
			    strSql.append("a01.a0101 like '");
			    if(queryname!=null && queryname.length()>0)
			        strSql.append(queryname);
			    else
			    	strSql.append("-1");
			    strSql.append("%'");
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
	 private void getEmploys(String queryname,String dbpre)throws GeneralException
	 {
		 ArrayList UidList=new ArrayList();
	      String strsql=getPrivSql(queryname,dbpre);
	      if("".equals(strsql))
	    	  return;
	      String theaction=null;
	      ContentDAO dao=new ContentDAO(this.getFrameconn());
	      RowSet rset=null;
	      try
	      {
	    	  rset=dao.search(strsql);
	    	  while(rset.next())
	    	  {
	    		  String nbase=rset.getString("dbase");
	    		  String a0100=rset.getString("a0100");
	    		  String a0101=rset.getString("a0101");
	              if(a0101==null)
	            	  a0101="";      
	              UidList.add(nbase + a0100);
	    	  }
	    	  this.getFormHM().put("employlist",UidList);
	      }
	      catch(Exception ex)
	      {
	    	  //ex.printStackTrace();
	    	  throw GeneralExceptionHandler.Handle(new GeneralException("","没有此人员信息！","",""));
	      }
	    }
}
