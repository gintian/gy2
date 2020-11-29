package com.hjsj.hrms.transaction.general.template.historydata;

import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time: 2010 05 25 11:39:04 AM</p> 
 *@author xieguiquan
 *@version 5.0
 */
public class QueryHistorydataTrans extends IBusiness { 

	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hmMap=(HashMap)this.getFormHM().get("requestPamaHM");
		String type = (String)hmMap.get("type");
		StringBuffer sqlwhere = new StringBuffer();
		if("0".equals(type)){
			this.getFormHM().put("select_type", "");
		}
		else if("1".equals(type)){
			//Sql_switcher.today();
			sqlwhere.append(Sql_switcher.year("lasttime"));
			sqlwhere.append("=");
			sqlwhere.append(Sql_switcher.toYear());
			this.getFormHM().put("select_type", "1");
		}
		else if("2".equals(type)){
	         Calendar calendar = Calendar.getInstance();
	        switch(calendar.get(Calendar.MONTH)){
	        	case 0:
	        	case 1:
	        	case 2:
	        		sqlwhere.append(Sql_switcher.month("lasttime"));
	    			sqlwhere.append(" in (1,2,3)");
	        		break;
	        	case 3:
	        	case 4:
	        	case 5:
	        		sqlwhere.append(Sql_switcher.month("lasttime"));
	    			sqlwhere.append(" in (4,5,6)");
	        		break;
	        		case 6:
		        	case 7:
		        	case 8:
		        		sqlwhere.append(Sql_switcher.month("lasttime"));
		    			sqlwhere.append(" in (7,8,9)");
		        		break;
		        	case 9:
		        	case 10:
		        	case 11:
		        		sqlwhere.append(Sql_switcher.month("lasttime"));
		    			sqlwhere.append(" in (10,11,12)");
		        		break;
	        		
	        }
	        
	        this.getFormHM().put("select_type", "2");
		}
		else if("3".equals(type)){
			sqlwhere.append(Sql_switcher.month("lasttime"));
			sqlwhere.append("=");
			sqlwhere.append(Sql_switcher.toMonth());
			  this.getFormHM().put("select_type", "3");
		}
		else if("4".equals(type)){
			String startdate = (String)hmMap.get("startdate");
			String appDate = (String)hmMap.get("appDate");
			
			int n=0;
			if(startdate!=null&&startdate.trim().length()>0)
			{
				this.getFormHM().put("startdate", startdate);
				startdate= startdate.replace(".", "-").replace("-", "");
				sqlwhere.append(Sql_switcher.year("lasttime")+"*10000 + ");
				sqlwhere.append(Sql_switcher.month("lasttime")+"*100 +");
				sqlwhere.append(Sql_switcher.day("lasttime")+"");
				sqlwhere.append(">=");
				sqlwhere.append(startdate);
				n++;
			}
			else
				this.getFormHM().put("startdate", "");
			
			if(appDate!=null&&appDate.trim().length()>0)
			{
				this.getFormHM().put("appDate", appDate);
				appDate= appDate.replace(".", "-").replace("-", "");
				if(n==1)
					sqlwhere.append(" and ");
				sqlwhere.append(Sql_switcher.year("lasttime")+"*10000 + ");
				sqlwhere.append(Sql_switcher.month("lasttime")+"*100 +");
				sqlwhere.append(Sql_switcher.day("lasttime")+"");
				sqlwhere.append("<=");
				sqlwhere.append(appDate);
			}
			else
				this.getFormHM().put("appDate", "");
			  this.getFormHM().put("select_type", "4");
		}
		else{
			  this.getFormHM().put("select_type", "");
		}
	
		this.getFormHM().put("condition", sqlwhere.toString());
		
	}

}
