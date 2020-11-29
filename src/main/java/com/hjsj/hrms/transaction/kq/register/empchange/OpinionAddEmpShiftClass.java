package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
/**
 * 判断新增人员是否排班，如果没有则提示
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 25, 2007:1:17:54 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class OpinionAddEmpShiftClass extends IBusiness{
	public void execute()throws GeneralException
	{
		ArrayList emplist=(ArrayList)this.getFormHM().get("emplist");
		String end_date=(String)this.getFormHM().get("end_date");
		if(emplist==null||emplist.size()<=0)
		{
			this.getFormHM().put("info","请选择人员！");
			return ;
		}
		String info="";		
		String flag="0";
		int index = 1;
		for(int i=0;i<emplist.size();i++)
		{
			
			String emp_mess=emplist.get(i).toString();
			String [] vo=emp_mess.split("`");
			String userbase=vo[0];
		    String a0100=vo[1];
		    if(!opinionShift(userbase,a0100,end_date))
		    {
		    	StringBuffer sql=new StringBuffer();;
		        sql.append("select * from kq_employ_change ");
		        sql.append("where nbase='"+userbase+"' and a0100='"+a0100+"'");
		        try
		        {
		        	ContentDAO dao=new ContentDAO(this.getFrameconn());	
		        	this.frowset=dao.search(sql.toString());
		        	if(this.frowset.next())
		        	{
		        		a0100=this.frowset.getString("a0101");
		        	}
		        }catch(Exception e)
		        {
		          e.printStackTrace();	
		        }
		        if(index % 10 == 0)
		        	info += a0100 + "，\r";
		        else
		        	info += a0100 + "，";
		        index++;
		    }		    
		}
		if (info.length() > 0) {
			info = info.substring(0, info.length() - 1);
			info += "\r以上人员没有排班，请先进行排班！";
			flag = "1";
		}
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("info",SafeCode.encode(info));
	}
    public boolean opinionShift(String nbase,String a0100,String end_date)
    {
    	boolean isCorrect=true;
    	StringBuffer sql=new StringBuffer();;
        sql.append("select * from kq_employ_change ");
        sql.append("where nbase='"+nbase+"' and a0100='"+a0100+"'");
        ContentDAO dao=new ContentDAO(this.getFrameconn());	
        String kq_org_dept_able_shift="kq_org_dept_able_shift";//不定排班
        String kq_employ_shift="kq_employ_shift";//人员排班表
        try
        {
        	String start_date="";
        	String e01a1="";
        	String e0122="";
        	String b0110="";
        	this.frowset=dao.search(sql.toString());
        	if(this.frowset.next())
        	{
        		Date date=this.frowset.getDate("change_date");
        		start_date=DateUtils.format(date,"yyyy.MM.dd");
        		e01a1=this.frowset.getString("e01a1");
        		e0122=this.frowset.getString("e0122");
        		b0110=this.frowset.getString("b0110");
        	}
        	sql=new StringBuffer();
        	sql.append("select * from "+kq_employ_shift+"");
        	sql.append(" where nbase='"+nbase+"' and a0100='"+a0100+"'");
        	sql.append(" and  q03z0>='"+start_date+"' and q03z0<='"+end_date+"'");
        	this.frowset=dao.search(sql.toString());
        	if(!this.frowset.next())
        	{
        		isCorrect=false;
        	    sql=new StringBuffer();
        	    sql.append("select * from "+kq_org_dept_able_shift);
        	    sql.append(" where org_dept_id='"+e01a1+"' or org_dept_id='"+e0122+"' or org_dept_id='"+b0110+"'");
        	    this.frowset=dao.search(sql.toString());
        	    if(this.frowset.next())
            		isCorrect=true;
        	} 
        }catch(Exception e)
        {
          e.printStackTrace();	
        }
    	return isCorrect;
    }
}
