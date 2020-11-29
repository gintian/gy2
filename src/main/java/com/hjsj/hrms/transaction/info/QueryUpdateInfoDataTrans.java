package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class QueryUpdateInfoDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase=(String)this.getFormHM().get("userbase");
		String strexpression=(String)this.getFormHM().get("strexpression");
		//System.out.println(strexpression);
        String strwhere="";
		StringBuffer wheresql=new StringBuffer();
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by ");
		orderby.append(userbase);
		orderby.append("a01.a0000");
	    ArrayList fieldlist=new ArrayList();
		     try
		     {
		            cat.debug("expr="+strexpression);
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            if((!userView.isSuper_admin()))
		            {
		                strwhere=userView.getPrivSQLExpression(strexpression,userbase,false,true,fieldlist);
		            }
		            else
		            {
		            	String lexpr="";
		            	String strFactor="";
		            	//System.out.println(strexpression);
		            	StringTokenizer stoke=new StringTokenizer(strexpression,"|");
		            	if(stoke.hasMoreTokens())
		            		lexpr=stoke.nextToken();
		            	if(stoke.hasMoreTokens())
		            		strFactor=stoke.nextToken();
		            	FactorList factorlist=new FactorList(lexpr,strFactor,userbase,false,false,true,Integer.parseInt("1"),userView.getUserName());
		            	factorlist.setSuper_admin(userView.isSuper_admin());
		            	strwhere=factorlist.getSqlExpression();
		            }		          
		            cat.debug("---->Common query's priv="+strwhere);

		        }catch(Exception e){
		          e.printStackTrace();	
		        }
		StringBuffer strsql=new StringBuffer();
		/*strsql.append("select ");
	    strsql.append(userbase);
		strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");*/
		StringBuffer columns=new StringBuffer();
		strsql.append("select ");
	    strsql.append(userbase);
		strsql.append("A01.A0100");
		columns.append("A0100");
	    String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
	    if(fieldstr!=null&&fieldstr.length()>0)
	    {
	       if(fieldstr.indexOf("state")!=-1)
	       {
	    	   fieldstr=fieldstr+",state";
	       }		
	       strsql.append(fieldstr);
		   columns.append(fieldstr);
	   }else{
				fieldstr=",B0110,E0122,E01A1,A0101,UserName,state";
				strsql.append(",B0110,E0122,E01A1,A0101,UserName,state");
				columns.append(",B0110,E0122,E01A1,A0101,UserName,state");
	   }
	   this.getFormHM().put("strsql",strsql.toString());
	  this.getFormHM().put("cond_str",strwhere);
	  this.getFormHM().put("order_by",orderby.toString());

      
		
	}

}
