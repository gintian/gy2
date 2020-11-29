package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;

public class BatchEditDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String dateType=(String)this.getFormHM().get("dateType");    //
			String dataField=(String)this.getFormHM().get("dateField");
			String dataContext=(String)this.getFormHM().get("dateContext");
			String decimal=(String)this.getFormHM().get("decimal");
			String z0301s=((String)this.getFormHM().get("selectID")).substring(1);
			z0301s = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(z0301s);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			
			StringBuffer whl=new StringBuffer("");
			String[] z0301=z0301s.split("#");
			/**安全改造,判断是否要驳回的z0301是否存在后台begin**/
			String checksql = (String) this.userView.getHm().get("hire_sql");
			int index = checksql.indexOf("order by");
			if(index!=-1){
				checksql = checksql.substring(0, index);
			}	
			checksql = checksql+" and z0301 in(";
			for(int i=0;i<z0301.length;i++){
				String temp_Z0301=z0301[i];
				if(i==0){
					checksql=checksql+temp_Z0301;
				}else{
					checksql=checksql+","+temp_Z0301;
				}
			}
			checksql=checksql+")";
			dao=new ContentDAO(this.getFrameconn());	
			try {
				this.frowset = dao.search(checksql);
				int count=0;
				while(this.frowset.next()){
					count++;
				}
				if(count<z0301.length){
					throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**安全改造,判断是否由要删除的z0301是否存在后台end**/
			for(int i=0;i<z0301.length;i++)
			{
				whl.append(",'"+z0301[i]+"'");
				
			}
			String sql = "update z03 set "+dataField+"=? where z0301 in ("+whl.substring(1)+")";
		    //PreparedStatement statement=this.getFrameconn().prepareStatement();
			ArrayList values = new ArrayList();
		    if("D".equals(dateType))
		    {
			    String[] dates=dataContext.split("-");
			    Calendar d=Calendar.getInstance();
			    d.set(Calendar.YEAR,Integer.parseInt(dates[0]));
			    d.set(Calendar.MONTH,Integer.parseInt(dates[1])-1);
			    d.set(Calendar.DATE,Integer.parseInt(dates[2]));
			    values.add(new java.sql.Date(d.getTimeInMillis()));
			    //statement.setDate(1,new java.sql.Date(d.getTimeInMillis()));
		    }
		    else if("A".equals(dateType))
		    {
		    	values.add(dataContext);
		    	//statement.setString(1,dataContext);
		    	
		    }
		    else if("N".equals(dateType))
		    {
		    	if("0".equals(decimal))
		    		values.add(Integer.parseInt(dataContext));
		    		//statement.setInt(1,Integer.parseInt(dataContext));
		    	else
		    		values.add(Double.parseDouble(dataContext));
		    		//statement.setDouble(1,Double.parseDouble(dataContext));
		    }
		    dao.update(sql, values);
		    //statement.execute();
		    //statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		 
		 

	}

}
