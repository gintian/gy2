package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 给打印预览发送显示数据
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 2, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchPrintviewPersonTran extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String dbname=(String)this.getFormHM().get("dbname");
		String inforkind=(String)this.getFormHM().get("inforkind");
		StringBuffer sql=new StringBuffer();
		ArrayList personlist=new ArrayList();
		try{
			if(userView.getStatus()!=4){
				if("1".equals(inforkind)){
					sql.append("select ");
		        	sql.append(dbname);
		        	sql.append("A01.a0100,");
		        	sql.append(dbname);
		        	sql.append("A01.a0101 from ");
		        	sql.append(this.userView.getUserName());
		        	sql.append(dbname);
		        	sql.append("Result,");
		        	sql.append(dbname);
		        	sql.append("A01 where ");
		        	sql.append(this.userView.getUserName());
		        	sql.append(dbname);
		        	sql.append("Result.a0100=");
		        	sql.append(dbname);
		        	sql.append("A01.a0100 order by ");
		        	sql.append(dbname);
		        	sql.append("a01.a0000");
				}else if("2".equals(inforkind)) {
		        	sql.append("select ");
		        	sql.append(this.userView.getUserName());
		        	sql.append("BResult.b0110,organization.codeitemdesc from ");
		        	sql.append(this.userView.getUserName());
		        	sql.append("BResult,organization where organization.codeitemid=");
		        	sql.append(this.userView.getUserName());
		        	sql.append("BResult.b0110");
		        	sql.append(" order by " + this.userView.getUserName() + "BResult.b0110");//wangcq 2014-12-20
		        }    	
			    else if("4".equals(inforkind))
			    {
			    	sql.append("select ");
			    	sql.append(this.userView.getUserName());
			    	sql.append("KResult.E01A1,organization.codeitemdesc from ");
			    	sql.append(this.userView.getUserName());
			    	sql.append("KResult,organization where organization.codeitemid=");
			    	sql.append(this.userView.getUserName());
			    	sql.append("KResult.E01A1");       
			    }else if("5".equals(inforkind))
			    {
			    	String plan_id=(String)this.getFormHM().get("plan_id");
			    	sql.append("select object_id,a0101 from per_result_"+plan_id);
			    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
			    	String where=bo.getPrivWhere(this.userView);			    	
			    	if(where!=null&&where.length()>0)
			    		sql.append(" where 1=1 "+where);
			    	sql.append(" order by a0000");  //wangcq 2015-1-4
			    	
			    }
				
			 }else
			 {
				 if("1".equals(inforkind)){
						sql.append("select ");
			        	sql.append(dbname);
			        	sql.append("A01.a0100,");
			        	sql.append(dbname);
			        	sql.append("A01.a0101 from t_sys_result,");			        	
			        	sql.append(dbname);
			        	sql.append("A01 where ");			        	
			        	sql.append("t_sys_result.obj_id="+dbname+"A01.a0100 and upper(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
			        	sql.append(" and flag=0 and upper(t_sys_result.nbase)='"+dbname.toUpperCase()+"'");
			        	sql.append(" order by ");
			        	sql.append(dbname);
			        	sql.append("a01.a0000");
					}else if("2".equals(inforkind)) {
						sql.append("select organization.codeitemid b0110,organization.codeitemdesc from organization where");	        	
			        	sql.append(" codeitemid in (");	
						sql.append("select obj_id from t_sys_result ");
						sql.append(" where flag=1 and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
						sql.append(" and UPPER(nbase)='B'");
						sql.append(") order by ");
			        	sql.append("organization");
			        	sql.append(".a0000");
			        	
			        }    	
				    else if("4".equals(inforkind))
				    {
				    	sql.append("select organization.codeitemid e0122,organization.codeitemdesc from organization where");	        	
			        	sql.append(" codeitemid in (");	
						sql.append("select obj_id from t_sys_result ");
						sql.append(" where flag=2 and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
						sql.append(" and UPPER(nbase)='K'");
						sql.append(") order by ");
			        	sql.append("organization");
			        	sql.append(".a0000");      
				    }else if("5".equals(inforkind))
				    {
				    	String plan_id=(String)this.getFormHM().get("plan_id");
				    	sql.append("select object_id,a0101 from per_result_"+plan_id);
				    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
				    	String where=bo.getPrivWhere(this.userView);			    	
				    	if(where!=null&&where.length()>0)
				    		sql.append(" where 1=1 "+where);
				    	sql.append(" order by a0000");  //wangcq 2015-1-4
				    	
				    }
			 }
			 if("6".equals(inforkind))  // 基准岗位
		     {
		    	String codeset=new CardConstantSet().getStdPosCodeSetId();
	    		sql.append("select t_sys_result.obj_id as h0100,codeitemdesc from t_sys_result,CodeItem "+
	    				   " where CodeItem.codeitemid=t_sys_result.obj_id and codesetid='"+codeset+"'"+
	    				        " and t_sys_result.flag=5"+// 基准岗位
	    				        " and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"'");
		     }
			 if(sql!=null&&sql.length()>0)
			 {
				 ContentDAO dao=new ContentDAO(this.getFrameconn());			
					this.frowset=dao.search(sql.toString());
					String tag="";
					int num=0;
					if("1".equals(inforkind))
			        {
						while(this.frowset.next())
				     	{
							tag="<NBASE>"+dbname+"</NBASE><ID>"+this.frowset.getString("a0100")+"</ID><NAME>"+this.frowset.getString("a0101")+"</NAME>";
							CommonData dataobj = new CommonData(this.frowset.getString("a0100"),tag);
							personlist.add(dataobj);
							num++;
							if(num>500)
								break;
				     	}
			        }else if("2".equals(inforkind)) {
			        	while(this.frowset.next())
				     	{
			        		tag="<NBASE>"+dbname+"</NBASE><ID>"+this.frowset.getString("b0110")+"</ID><NAME>"+this.frowset.getString("codeitemdesc")+"</NAME>";
			        		CommonData dataobj = new CommonData(this.frowset.getString("b0110"),tag);
			        		personlist.add(dataobj);
			        		num++;
							if(num>500)
								break;
				     	}
			        }else if("4".equals(inforkind))
			        {
			        	if(userView.getStatus()!=4){
			        		while(this.frowset.next())
					     	{
				        		tag="<NBASE>"+dbname+"</NBASE><ID>"+this.frowset.getString("e01a1")+"</ID><NAME>"+this.frowset.getString("codeitemdesc")+"</NAME>";
				        		CommonData dataobj = new CommonData(this.frowset.getString("e01a1"),tag);
				        		personlist.add(dataobj);
				        		num++;
								if(num>500)
									break;
					     	}
			        	}else{
			        		while(this.frowset.next())
					     	{
				        		tag="<NBASE>"+dbname+"</NBASE><ID>"+this.frowset.getString("e0122")+"</ID><NAME>"+this.frowset.getString("codeitemdesc")+"</NAME>";
				        		CommonData dataobj = new CommonData(this.frowset.getString("e0122"),tag);
				        		personlist.add(dataobj);
				        		num++;
								if(num>500)
									break;
					     	}
			        	}
			        }else if("5".equals(inforkind))
			        {
				 	      while(this.frowset.next())
				          {
				 	    	 tag="<NBASE>"+dbname+"</NBASE><ID>"+this.frowset.getString("object_id")+"</ID><NAME>"+this.frowset.getString("a0101")+"</NAME>";
				            CommonData dataobj = new CommonData(this.frowset.getString("object_id"),tag);
				        	personlist.add(dataobj);
				        	 num++;
				     		  if(num>=500)
				     			  break;
				          } 
				 	}
			        else {
				 	      while(this.frowset.next())
				          {
				 	    	 tag="<NBASE></NBASE><ID>"+this.frowset.getString(1)+"</ID><NAME>"+this.frowset.getString(2)+"</NAME>";
				            CommonData dataobj = new CommonData(this.frowset.getString(1),tag);
				        	personlist.add(dataobj);
				        	 num++;
				     		  if(num>=500)
				     			  break;
				          } 
			        }
					
			 }
			 this.getFormHM().put("personlist",personlist);
	    }catch(Exception ex)
		{
			ex.printStackTrace();
	   	    throw GeneralExceptionHandler.Handle(ex);  
		}
	}

}
