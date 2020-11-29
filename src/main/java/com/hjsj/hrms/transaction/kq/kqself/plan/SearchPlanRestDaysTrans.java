package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchPlanRestDaysTrans extends IBusiness {
    private String appPlanMess;
	public void execute() throws GeneralException {
		String plan_id = (String)this.getFormHM().get("plan_id");
		String plan_year = getYears(plan_id);
		String type=(String)this.getFormHM().get("type");		
		String app_date=(String)this.getFormHM().get("app_date");
		String q3101=(String)this.getFormHM().get("q3101");
		if(app_date==null||app_date.length()<=0)
		{
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  		 app_date = sdf.format(new java.util.Date());
		}
		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
		String b0110=managePrivCode.getPrivOrgId();
	    if(KqParam.getInstance().isHoliday(this.frameconn, b0110, type))
	    {
	    	float days=findRestDays(app_date,type,plan_year);
	    	//float plan_day=othenPlanTime(q3101);
	    	//days=days-plan_day;
	    	KqUtilsClass kqUtilsClass=new KqUtilsClass();
	    	days=kqUtilsClass.round(days+"",1);
	    	if(days<=0)
	    		days=0;
	    	this.getFormHM().put("days","("+ResourceFactory.getProperty("kq.rest.desc")+":"+days+""+ResourceFactory.getProperty("kq.rest.day")+")");			
	    }else{
	    	this.getFormHM().put("days", "");
	    }
	 }
	public float othenPlanTime(String q3101)throws GeneralException
	{
		float time=0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  		String strDate = sdf.format(new java.util.Date());
  		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());
  		HashMap kqItem_hash=annualApply.count_Leave("06");
  		 String has_feast=(String)kqItem_hash.get("has_feast");	
  		String has_rest=(String)kqItem_hash.get("has_rest");	
  		ArrayList restList=IfRestDate.search_RestOfWeek(this.userView.getUserOrgId(),userView,this.getFrameconn());
		String rest_date=restList.get(0).toString();
		String rest_b0110=restList.get(1).toString();
		HashMap hash=annualApply.getHols_Time("06",this.userView.getA0100(),this.userView.getDbname(),strDate,strDate);
		Date q17z1=(Date)hash.get("q17z1");
		Date q17z3=(Date)hash.get("q17z3");
	    if(q17z1==null||q17z3==null)
	    	return 0;
	    StringBuffer sql=new StringBuffer();
	    String column_z1="q31z1";
	    String column_z3="q31z3";
	    sql.append("select "+column_z1+","+column_z3+" from q31 where ");
	    sql.append("a0100='"+this.userView.getA0100()+"'");       	
	    sql.append(" and "+column_z1+">="+Sql_switcher.dateValue(DateUtils.format(q17z1,"yyyy-MM-dd HH:mm:ss")));	
	    sql.append(" and "+column_z3+"<="+Sql_switcher.dateValue(DateUtils.format(q17z3,"yyyy-MM-dd HH:mm:ss")));	
	    sql.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
	    
	    //System.out.println(sql.toString());
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    RowSet rs=null;
	    StringBuffer message=new StringBuffer();
	    try
	    {
	    	rs=dao.search(sql.toString());
	    	Date s_date=null;
	    	Date e_date=null;
	    	float num=0;	    	
	    	while(rs.next())
	    	{
	    		float leave_tiem=0;
	    		s_date=rs.getDate(column_z1);
	    		e_date=rs.getDate(column_z3);		    		
	    		num=DateUtils.dayDiff(s_date,e_date);
	    		for(int m=0;m<=num;m++)
	        	{
	       	        String op_date_to=getDateByAfter(s_date,m);
	       	        String feast_name=IfRestDate.if_Feast(op_date_to,this.getFrameconn());
	       	        String week_date=IfRestDate.getWeek_Date(rest_b0110,op_date_to,this.getFrameconn());
	       	        if(has_feast!=null&& "1".equals(has_feast))
	       	        {
	       	           if(feast_name!=null&&feast_name.length()>0)
	      	    	   {
	     					 String turn_date=IfRestDate.getTurn_Date(this.userView.getUserOrgId(),op_date_to,this.getFrameconn());
	     					 if((turn_date==null||turn_date.length()<=0))		
	     					 {
	     						leave_tiem=leave_tiem+1;
	     						 continue;
	     					 }
	      	    	   }else
	      	    	   {
	      	    		 leave_tiem=leave_tiem+1;
	      	    		  continue;
	      	    	   }
	       	        }else
	       	        {
	       	        	if(feast_name!=null&&feast_name.length()>0)
	       	    	    {
	       	        		 String turn_date=IfRestDate.getTurn_Date(this.userView.getUserOrgId(),op_date_to,this.getFrameconn());
	    					 if((turn_date==null||turn_date.length()<=0))		
	    					 {
	    						continue;
	    					 }
	       	    	    }
	       	        }
	       	        if(has_rest!=null&& "1".equals(has_rest))
	       	        {
	       	        	if(!IfRestDate.if_Rest(op_date_to,userView,rest_date))
	    	    	    {
	    	    	    	  if(week_date!=null&&week_date.length()>0)
	    	    	    	  {
	    	    	    		  leave_tiem=leave_tiem+1;
	    	      	    		  continue;
	    	    	    	  }
	    	    	    }else
	    	    		 {
	    	    			  String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date_to,this.getFrameconn());
	    	    	    	  if(turn_date==null||turn_date.length()<=0)
	    		    		  {
	    	    	    		  leave_tiem=leave_tiem+1;
	    	      	    		  continue;
	    	    	    	  }
	    		    	}
	       	        }else
	       	        {
	       	        	if(!IfRestDate.if_Rest(op_date_to,userView,rest_date))
	    	    	    {
	    	    	    	  if(week_date!=null&&week_date.length()>0)
	    	    	    	  {
	    	    	    		  continue;
	    	    	    	  }
	    	    	    }else
	    	    		 {
	    	    			  String turn_date=IfRestDate.getTurn_Date(rest_b0110,op_date_to,this.getFrameconn());
	    	    	    	  if(turn_date==null||turn_date.length()<=0)
	    		    		  {
	    	    	    		 continue;
	    	    	    	  }
	    		    	}
	       	        }
	       	        leave_tiem=leave_tiem+1;
	    	    }
	    		if(leave_tiem>0)
	    		{
	    			message.append(s_date+"---"+e_date+"<br>");
	    		}
	    		time=time+leave_tiem;	
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	    {
	    	if(rs!=null)
	    		try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	    }
	    this.setAppPlanMess(message.toString());
	    return time;
	}
	public String getAppPlanMess() {
		return appPlanMess;
	}
	public void setAppPlanMess(String appPlanMess) {
		this.appPlanMess = appPlanMess;
	}
	public float findRestDays(String app_date,String type,String plan_year)
	{
		float days=0;
		String balance = "";
        ArrayList fieldList = DataDictionary
                    .getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("结余剩余".equalsIgnoreCase(item.getItemdesc())) {
                balance = item.getItemid();
            }
        }
		StringBuffer strsql=new StringBuffer();
		strsql.append("select q1707");
		if (balance.length() != 0 ) 
		{
			strsql.append("," + balance);
		}
		strsql.append(" from Q17 where q1709=? and nbase=? and a0100=? and ");
		strsql.append(Sql_switcher.dateValue(app_date));
		strsql.append(" between q17z1 and q17z3");
		strsql.append(" and q1701 = '" + plan_year + "'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		list.add(type);
		list.add(this.userView.getDbname());
		list.add(this.userView.getA0100());
		RowSet rset = null;
		try
		{
			rset=dao.search(strsql.toString(),list);
			if(rset.next())
			{
				days=rset.getFloat("q1707");//可休天数+结余剩余
				if (balance.length() != 0) 
				{
					days = days + rset.getFloat(balance);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally{
        	if(rset!=null)
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        }
		KqUtilsClass kqUtilsClass=new KqUtilsClass();
		days=kqUtilsClass.round(days+"",1);
		return days;
	}
	public static String getDateByAfter(Date date, int afterNum) throws GeneralException {

		Calendar calendar = Calendar.getInstance();

		try {
			 calendar.setTime(date);
		 } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);

		return new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
	}
	
	private String getYears(String plan_id)
	{
		String sql="select q2903 from q29 where q2901='"+plan_id+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
  		String strDate = sdf.format(new java.util.Date());  		
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				strDate=this.frowset.getString("q2903");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return strDate;
	}
}