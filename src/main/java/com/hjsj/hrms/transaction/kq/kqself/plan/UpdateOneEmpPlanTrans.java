package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UpdateOneEmpPlanTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		ArrayList onelist=(ArrayList)this.getFormHM().get("onelist");
		
		RecordVo vo=new RecordVo("q31");
		for(int i=0;i<onelist.size();i++)
   	    {
   	       FieldItem field=(FieldItem)onelist.get(i);   	       
           if("N".equals(field.getItemtype()))
              vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
	   		   if("D".equals(field.getItemtype()))
	   		   {
	   			  Date date=DateUtils.getDate(field.getValue().replaceAll("\\.","-"),"yyyy-MM-dd");
 	   		      vo.setDate(field.getItemid().toLowerCase(),date);	   	
	   		   }else{
	   			vo.setString(field.getItemid().toLowerCase(),field.getValue());
	   		   }
   	    }
		//vo.setString("q31z0","03");
		String q31z5=vo.getString("q31z5");
		if(q31z5==null||q31z5.length()<=0)
		{
			q31z5="01";
			vo.setString("q31z0","03");
		}
		if("01".equals(q31z5))
		{
			vo.setString("q31z0","03");
		}
		if("03".equals(q31z5))
			q31z5="02";
		vo.setString("q31z5",q31z5);
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		try
		{
			if(getIfSave(vo))
			  dao.updateValueObject(vo);
			
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e); 
		}
	}
	public boolean getIfSave(RecordVo vo)throws GeneralException
	 {
		boolean isCorrect=true;
		if(vo==null)
			return false;
		String nbase=vo.getString("nbase");
		String a0100=vo.getString("a0100");
		String q3101=vo.getString("q3101");
		Date F_time=vo.getDate("q31z1");
		Date T_time=vo.getDate("q31z3");;		
		String z1=DateUtils.format(F_time,"yyyy-MM-dd");
		String z3=DateUtils.format(T_time,"yyyy-MM-dd");
		AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());  
		if(annualApply.isRepeatedApp(nbase,a0100,z1,z3,"q31",this.getFrameconn(),q3101,""))
	    {
	    	isCorrect=false;
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",vo.getString("a0101")+",在这个申请的时间段已经申请了休假计划","",""));
	    }	
		isCorrect=isSave2(F_time,T_time,nbase,a0100,vo.getString("b0110"),vo.getString("a0101"),q3101);
		return isCorrect;
	 }
	 public boolean isSave2(Date kq_start,Date kq_end,String nbase,String a0100,String b0110,String a0101,String q3101)throws GeneralException
	 {
		 AnnualApply annualApply=new AnnualApply(this.userView,this.getFrameconn());
		 float re=0;		    	
	     HashMap kqItem_hash=annualApply.count_Leave("06");
	     boolean isCorrect=true;	
	     float leave_tiem=annualApply.planDays(kq_start,kq_end,kqItem_hash);
	    	//System.out.println(leave_tiem);
	    if(leave_tiem<=0)
	    {
	    	isCorrect=false;
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",a0101+"可休有效时间为0天","",""));
	    }
	    String start=DateUtils.format(kq_start,"yyyy.MM.dd");
	    String end=DateUtils.format(kq_end,"yyyy.MM.dd");
	    float myTime=annualApply.getMy_Time("06",a0100,nbase,start,end,b0110,kqItem_hash);
	    float other_time=annualApply.othenPlanTime(kq_start,kq_end,a0100,nbase,b0110,q3101,kqItem_hash,"up");
	    re=myTime-leave_tiem-other_time;
	    if(re<0)
		{
				isCorrect=false;
				String message=a0101+"，"+ResourceFactory.getProperty("error.kq.morelet");
				KqUtilsClass kqUtilsClass=new KqUtilsClass();
				myTime=kqUtilsClass.round(myTime+"",1);
				message=message+"可休时间为"+myTime+"天！";
				if(other_time>0)
				 message=message+"在这之前您已经计划了"+other_time+"天!<br>"+annualApply.getAppLeavedMess();
				throw GeneralExceptionHandler.Handle(new GeneralException("",message,"",""));
		}
	    return isCorrect;
     }
}
