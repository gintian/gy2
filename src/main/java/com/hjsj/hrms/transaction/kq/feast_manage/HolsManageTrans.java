package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.feast_manage.FeastComputer;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.FuncVersion;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HolsManageTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String code="";
	 	if(this.userView.isSuper_admin())
	    {
	 		code="";
	 	}else
	 	{
	 		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			code=managePrivCode.getPrivOrgId();  
	 	}
	 	HashMap hashmap = new HashMap();	
	    String b0110="";
	    FeastComputer feastComputer=new FeastComputer(this.getFrameconn(),this.userView);
	 	do
	    {
	        ArrayList list=feastComputer.getHolsType(code);
	   		hashmap= (HashMap)list.get(0);
	    	code=list.get(1).toString(); 	    			 
 			b0110=(String)hashmap.get("b0110");
	    }
	    while(b0110==null||b0110.length()<=0); 
	 	String hols_type=(String)hashmap.get("type");
	 	if(hols_type==null||hols_type.length()<=0)
	 		throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义假期管理项目！","",""));
	 	ArrayList holi_list=feastComputer.getHolsList(hols_type);
	 	this.getFormHM().put("holi_list",holi_list);
	 	String kq_year = "";//(String)this.getFormHM().get("kq_year");
		ArrayList yearlist=RegisterDate.getKqYear(this.getFrameconn());
		String error_flag_session="0";	
		if(kq_year==null||kq_year.length()<=0)
		{
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
		    CommonData cd = new CommonData();
		    ArrayList courselist = RegisterDate.yearDate(this.getFrameconn(), "0");
		    if(courselist.size() != 0){
		        cd = (CommonData)(courselist.get(courselist.size()-1));
		        kq_year = cd.getDataValue();
		    }else if(RegisterDate.yearDate(this.getFrameconn(), "1").size() != 0){
		        cd = (CommonData)(RegisterDate.yearDate(this.getFrameconn(), "1").get(0));
                kq_year = cd.getDataValue();
		    }
//		    kq_year = ((CommonData)(RegisterDate.yearDate(this.getFrameconn(), "1").get(0))).getDataValue();
//			kq_year=year+"";
		}
		if(kq_year==null||kq_year.length()<=0)
		{
			if(yearlist!=null&&yearlist.size()>0)
			{
				CommonData vo=(CommonData)yearlist.get(0);
				kq_year=vo.getDataValue();
			}else
			{
				String error_return="";
				
				String error_message_session="";
				if(code==null||code.length()<=0)
	    		 {
	    			 //error_return="/templates/menu/kq_m_menu.do?b_query=link&module=6";
	    			 //error_flag_session="4";
	    			 error_return="history.back();";
	    			 error_flag_session="1";
	    		 }else
	    		 {
	    			 error_return="history.back();";
	    			 error_flag_session="1";
	    		 }
				error_message_session=ResourceFactory.getProperty("kq.register.session.nosave");				
   			    this.getFormHM().put("error_message_session",error_message_session);
   	    		this.getFormHM().put("error_return",error_return);                
			}
			
		}
		this.getFormHM().put("error_flag_session",error_flag_session);
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("kq_year",kq_year);
		this.getFormHM().put("balanceName", this.getBalance());
		
		/**增加调休假,参数设置调休假有效期不为空才显示此页签**/
		DbWizard dbWizard = new DbWizard(frameconn);
		String isshow = "0";//是否显示调休假,1：显示,0：不显示
		
        FuncVersion fv = new FuncVersion(this.userView);
        
        //专业版才有调休假功能
        if (fv.haveKqLeaveTypeUsedOverTimeFunc()) {
//        		String OVERTIME_FOR_LEAVETIME_LIMIT = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();		
//        		OVERTIME_FOR_LEAVETIME_LIMIT = StringUtils.isEmpty(OVERTIME_FOR_LEAVETIME_LIMIT) ? "0" : OVERTIME_FOR_LEAVETIME_LIMIT;
//        		
//        		ArrayList fielditemlist = DataDictionary.getFieldList("Q33",Constant.USED_FIELD_SET);
//        		
//        		if (dbWizard.isExistTable("Q33", false) && fielditemlist != null
//        		        && !"0".equals(OVERTIME_FOR_LEAVETIME_LIMIT) && !"".equals(KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME())) {
//        			isshow = "1";
//        		}
            KqOverTimeForLeaveBo kqOverTimeForLeaveBo = new KqOverTimeForLeaveBo(this.getFrameconn(), this.userView);
            if(kqOverTimeForLeaveBo.validOverTimeForLeaveFunc())
                isshow = "1";
        }
		this.getFormHM().put("isshow", isshow);
    }
    
    /**
	 * 获得上年结余的字段名称
	 * @return
	 */
	public String getBalance () {
		// 获得年假结余的列名
		String balance = "";
		
		ArrayList fieldList = DataDictionary
					.getFieldList("q17", Constant.USED_FIELD_SET);
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem) fieldList.get(i);
			if ("上年结余".equalsIgnoreCase(item.getItemdesc())) {
				balance = item.getItemid();
			}
		}
		
		return balance;
	}
   
}
