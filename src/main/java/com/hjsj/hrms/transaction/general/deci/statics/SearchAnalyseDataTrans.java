/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Owner
 *
 */
public class SearchAnalyseDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String userbase=(String)this.getFormHM().get("dbpre");
			String statId=(String)this.getFormHM().get("statid");
			int v=(int)Integer.parseInt((String)this.getFormHM().get("v"));
			int h=(int)Integer.parseInt((String)this.getFormHM().get("h"));
		    String result=(String)this.getFormHM().get("result");
			String a_code=(String)this.getFormHM().get("a_code");
			
			boolean isresult=true;
		    if(result==null|| "".equals(result)|| "0".equals(result))
			    isresult=true; 
			else
			    isresult =false;
			StringBuffer orderby=new StringBuffer();
			orderby.append(" order by ");
			orderby.append("a0000");
			String sql;
			String exprlexpr;
			String exprfactor;
			  if(a_code!=null && a_code.length()>=2)
			  {
			    	String codeid=a_code.substring(0,2);
			    	if("UN".equalsIgnoreCase(codeid))
					{
			    		exprlexpr="1";				
			    		exprfactor="B0110=";
					}
					else if("UM".equalsIgnoreCase(codeid))
					{
						exprlexpr="1";			
						exprfactor="E0122=";
					}
					else
					{
						exprlexpr="1";				
						exprfactor="E01A1=";
					}
			    	exprfactor+=a_code.substring(2)+"*`";
			        sql=new StatDataEncapsulation().getDataSQL(Integer.parseInt(statId),userbase,"",v,h,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,exprlexpr,exprfactor,"2","");
			  }else
				    sql=new StatDataEncapsulation().getDataSQL(Integer.parseInt(statId),userbase,"",v,h,userView.getUserName(),userView.getManagePrivCode(),userView,"1",isresult,null,null,null,"");
			  
			StringBuffer strsql=new StringBuffer();
	        strsql.append("select ");
		    strsql.append(userbase);
		    strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
		    this.getFormHM().put("order_by",orderby.toString());
		   
		    this.getFormHM().put("strsql",strsql.toString());
		    this.getFormHM().put("cond_str",sql);
		   
	    }catch(Exception e)
		{
	      	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
	}

}
