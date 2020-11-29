package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class Q05SelectAllOperateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String kq_duration = (String) this.getFormHM().get("kq_duration");	
		ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		String select_pre=(String)this.getFormHM().get("select_pre");
	    this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);	 
		String code = (String) this.getFormHM().get("code");
		String kind = (String) this.getFormHM().get("kind"); 
		String showtype = (String) this.getFormHM().get("showtype");
		if(showtype==null||showtype.length()<=0)
		{
			showtype="all";
		}
		String code_kind="";
		if(kind==null||kind.length()<=0)
		{
				kind=RegisterInitInfoData.getKindValue(kind,this.userView);
				code="";
	    }
		if(code.length()<=0){
			   code=this.userView.getUserOrgId();
		}else{
				if(!"2".equals(kind))
	    		{
				   code_kind=RegisterInitInfoData.getDbB0100(code,kind,this.getFormHM(),this.userView,this.getFrameconn());
	    		}
		} 
		if(kq_duration==null||kq_duration.length()<=0)
		{
			kq_duration=RegisterDate.getKqDuration(this.frameconn);
		}
		String coursedate=(String)this.getFormHM().get("coursedate");				 
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);	
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
				sql_db_list.add(select_pre);
		}else
		{
				sql_db_list=kq_dbase_list;
		}		
		String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
		SelectAllOperate selectAllOperate=new SelectAllOperate(this.getFrameconn(),this.userView);		
		String kq_year="";
		String duration="";
		ArrayList kqDate_list=new ArrayList();
		if(kq_duration!=null&&kq_duration.length()>0)
		{
			 String[] kq_d=kq_duration.split("-");
			 kq_year=kq_d[0];
			 duration=kq_d[1];
			 kqDate_list=RegisterDate.getKqDayList(this.getFrameconn(),kq_year,duration);
		}else
		{
			 kqDate_list=RegisterDate.getKqDayList(this.getFrameconn());
		}
		String start_date=kqDate_list.get(0).toString();
		String end_date=kqDate_list.get(1).toString();
		String state_flag=(String)this.getFormHM().get("state_flag");
		if(state_flag==null||state_flag.length()<=0)
			state_flag="0";
		if(!"1".equals(state_flag))
		{
			selectAllOperate.operateQ05State(kq_dbase_list,kq_duration,code,kind,showtype,where_c,"1");
			selectAllOperate.operateQ03State(kq_dbase_list,start_date,end_date,code,kind,showtype,where_c,"1");
			this.getFormHM().put("state_flag", "1");
		}else
		{
			selectAllOperate.operateQ05State(kq_dbase_list,kq_duration,code,kind,showtype,where_c,"0");
			selectAllOperate.operateQ03State(kq_dbase_list,start_date,end_date,code,kind,showtype,where_c,"0");
			this.getFormHM().put("state_flag", "0");
		}
		
		
	}
   
}
