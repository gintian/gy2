package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadberOperation;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchAnalyseDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String userbase=(String)this.getFormHM().get("dbpre");
			String statId=(String)this.getFormHM().get("statid");
			HashMap reqMap = (HashMap)this.getFormHM().get("requestPamaHM");
			String Legend = (String)reqMap.get("showLegend");
			
			
			if(Legend!=null && Legend.length()>0){
				reqMap.remove("showLegend");
				Legend = SafeCode.decode(Legend);
				String a_code = (String)this.getFormHM().get("a_code");
				String analyse_setid=(String)this.getFormHM().get("analyse_setid");
				String  analyse_codesetid=(String)this.getFormHM().get("analyse_codesetid");
				String analyse_value=(String)this.getFormHM().get("analyse_value");
				String sql = "select LExpr,Factor from SLegend where id='"+statId+"' and legend like '"+Legend+"'";
				ContentDAO dao = new ContentDAO(frameconn);
				StringBuffer where = new StringBuffer();
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					String expr = this.frowset.getString("Lexpr");
					String factor = this.frowset.getString("Factor");
					factor+="`"+analyse_codesetid+"="+analyse_value;
					expr+="*"+factor.split("`").length;
				    FactorList factorList = new FactorList(expr, factor, userbase, false, false, true, 1, this.userView.getUserName());
				    where.append(factorList.getSqlExpression());
				    
				    if(a_code.indexOf("UN") != -1){
				    	where.append(" and b0110 like '"+a_code.substring(2)+"%'");
				    }else if(a_code.indexOf("UM") != -1)
				    	where.append(" and e0122 like '"+a_code.substring(2)+"%'");
				    
				    
				    StringBuffer strsql=new StringBuffer();
			        strsql.append("select ");
				    strsql.append(userbase);
				    strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
				    
				    
				    this.getFormHM().put("strsql",strsql.toString());
				    this.getFormHM().put("cond_str",where.toString());
					this.getFormHM().put("order_by"," order by a0000 ");
				}else
					throw new Exception("没有数据！");
				
			}else{
			
				int v=(int)Integer.parseInt((String)this.getFormHM().get("v"));
				int h=(int)Integer.parseInt((String)this.getFormHM().get("h"));		  
				String a_code=(String)this.getFormHM().get("a_code");
				String analyse_setid=(String)this.getFormHM().get("analyse_setid");
				String  analyse_codesetid=(String)this.getFormHM().get("analyse_codesetid");
				String analyse_value=(String)this.getFormHM().get("analyse_value");
				LeadberOperation leadberOperation=new LeadberOperation(this.getFrameconn(),this.userView);
				String candi_sql_in=leadberOperation.getLeaderWhereIn(userbase,analyse_setid,analyse_codesetid,analyse_value);
				boolean isresult=true;		    
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
				 if(candi_sql_in!=null&&candi_sql_in.length()>0)
						sql=sql+" and "+candi_sql_in;
				StringBuffer strsql=new StringBuffer();
		        strsql.append("select ");
			    strsql.append(userbase);
			    strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
			    this.getFormHM().put("order_by",orderby.toString());
			   
			    this.getFormHM().put("strsql",strsql.toString());
			    this.getFormHM().put("cond_str",sql);
			}
			
			
	    }catch(Exception e)
		{
	      	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
	}


}
