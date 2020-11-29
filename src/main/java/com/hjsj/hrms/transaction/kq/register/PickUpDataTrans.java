package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class PickUpDataTrans extends IBusiness{
	private String error_return="/kq/register/daily_registerdata.do?b_query=link";
	public void execute()throws GeneralException
    {
		String error_message="";
		String error_flag="0";
		String registerdate = (String) this.getFormHM().get("registerdate");
		String code=(String) this.getFormHM().get("code");		
		if(code==null||code.length()<=0)
		{
			 code="";
		}		
		String kind = (String)this.getFormHM().get("kind");
		String pick_type=(String)this.getFormHM().get("pick_type");
		String start_date="";
		String end_date="";
		ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
		if("1".equals(pick_type))
		{
				
			if(datelist==null||datelist.size()<=0)
			   {
				   datelist =RegisterDate.registerdate(code,this.getFrameconn(),this.userView); 
			   } 
			CommonData vo_date=(CommonData)datelist.get(0);
	    	start_date=vo_date.getDataValue();    	
	    	vo_date=(CommonData)datelist.get(datelist.size()-1);	    	 
	   	    end_date=vo_date.getDataValue();   
		}else if("2".equals(pick_type))
		{
			start_date=(String)this.getFormHM().get("start_date");
			end_date=(String)this.getFormHM().get("end_date");
			start_date=start_date.replaceAll("-","\\.");
			end_date=end_date.replaceAll("-","\\.");
		}
		if(start_date==null||start_date.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理起始时间不能为空！","",""));
		if(end_date==null||end_date.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理结束时间不能为空！","",""));
		try
		{
			
			start_date=DateUtils.format(DateUtils.getDate(start_date,"yyyy.MM.dd"),"yyyy.MM.dd");
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理起始时间错误！","",""));
		}
		try
		{
			end_date=DateUtils.format(DateUtils.getDate(end_date,"yyyy.MM.dd"),"yyyy.MM.dd");
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理结束时间错误！","",""));
		}	   
   	    String dataUpdateType="1";	
   	    // “统计”时只处理非机器考勤人员即可
	    String analyseType="0"; 
	    String analysBase="all";
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);		
    	ArrayList columnlist= new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     if("N".equals(fielditem.getItemtype()))
   	     {   		   
   		    columnlist.add(fielditemlist.get(i));
   		  }				
   	    }    	    
    	KqParameter kq_paramter = new KqParameter(this.userView,"",this.getFrameconn());  
    	 HashMap hashmap =kq_paramter.getKqParamterMap();
		 String kq_type=(String)hashmap.get("kq_type");
		   String kq_cardno=(String)hashmap.get("cardno");
		   String kq_Gno=(String)hashmap.get("g_no");
		
	    String select_pre=(String)this.getFormHM().get("select_pre");
		ArrayList dblist=new ArrayList();
		if(select_pre!=null&&!"all".equalsIgnoreCase(select_pre))
			dblist.add(select_pre);
		else
		    dblist = this.userView.getPrivDbList();
	    
		//统计计算非机器考勤人员的申请数据
		DataProcedureAnalyse dataProcedureAnalyse=new DataProcedureAnalyse(this.getFrameconn(),this.userView,analyseType,kq_type,kq_cardno,kq_Gno,dataUpdateType,this.userView.getPrivDbList());
		dataProcedureAnalyse.setPick_flag("1");
		boolean isCorrect= dataProcedureAnalyse.dataAnalys(code,kind,start_date,end_date,analysBase);
		
		//处理调休加班数据
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		UpdateQ33 updateQ33 = new UpdateQ33(this.userView,this.getFrameconn());
        String overtime_for_leavetime = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME();
        if (dbWizard.isExistTable("Q33", false) && !"".equals(overtime_for_leavetime)) {
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.getUserView());
            dblist = kqUtilsClass.getKqPreList();
            for(int i=0; i<dblist.size(); i++) {
                updateQ33.updateQ33(start_date + "`" + end_date, dblist.get(i).toString(), dataProcedureAnalyse.getFAnalyseTempTab());
            }
        }
		
		/***一个考勤期间的结束****/ 	    
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("registerdate",registerdate);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("error_flag",error_flag);		
		this.getFormHM().put("error_stuts","0");
		if(isCorrect)
 	    {
 	    	this.getFormHM().put("sp_result","数据统计成功！");
 	    }else
 	    {
 	    	this.getFormHM().put("sp_result","数据统计失败！");
 	    }
    }
	
}
