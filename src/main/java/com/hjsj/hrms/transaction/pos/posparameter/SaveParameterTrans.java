package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveParameterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String ps_superior=(String)this.getFormHM().get("ps_superior");
		ps_superior = PubFunc.hireKeyWord_filter_reback(ps_superior);
		String ps_job=(String)this.getFormHM().get("ps_job");
		ps_job = PubFunc.hireKeyWord_filter_reback(ps_job);
		String ps_c_job=(String)this.getFormHM().get("ps_c_job");
		ps_c_job = PubFunc.hireKeyWord_filter_reback(ps_c_job);
		//String ps_workfixed=(String)this.getFormHM().get("ps_workfixed");
		//String ps_workexist=(String)this.getFormHM().get("ps_workexist");
		//String ps_set=(String)this.getFormHM().get("ps_set");
		/*
		 * yuxiaochun add programe
		 */
		String b_set=(String) this.getFormHM().get("unit_set");
		String true_num=(String) this.getFormHM().get("true_num");
		String plan_num=(String)this.getFormHM().get("plan_num");
		String unitvalid=(String) this.getFormHM().get("unitvalid");
		//String psvalid=(String) this.getFormHM().get("psvalid");
//		System.out.println(b_set+" "+ psvalid);
		/*
		 * end 
		 */
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		sysoth.setValue(Sys_Oth_Parameter.WORKOUT,"unit",unitvalid);
		//sysoth.setValue(Sys_Oth_Parameter.WORKOUT,"pos",psvalid);
		sysoth.saveParameter();
		StringBuffer sql =new StringBuffer();	
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		if(plan_num!=null && plan_num.equalsIgnoreCase(true_num)&& "true".equals(unitvalid))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("unit.diff.unitfixednotexist"),"",""));
		//if(ps_workfixed!=null && ps_workfixed.equalsIgnoreCase(ps_workexist)&&psvalid.equals("true"))
		//	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("pos.diff.ps_posfixednotexist"),"",""));
		try{
			sql.append("delete from constant where constant='PS_SUPERIOR'");
			dao.delete(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           //throw GeneralExceptionHandler.Handle(e);
        }
		try{
			dao.delete("delete from constant where constant='PS_JOB'",new ArrayList());
			dao.delete("delete from constant where constant='PS_C_JOB'",new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           //throw GeneralExceptionHandler.Handle(e);
        }
		/*try{
			dao.delete("delete from constant where constant='PS_WORKOUT'",new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           //throw GeneralExceptionHandler.Handle(e);
        }
		try{
			dao.delete("delete from constant where constant='UNIT_WORKOUT'",new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           //throw GeneralExceptionHandler.Handle(e);
        }*/
		sql.delete(0,sql.length());
		sql.append("insert into constant(constant,type,str_value,describe)values('PS_SUPERIOR','0','"+ ps_superior + "','')");
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
		sql.delete(0,sql.length());
		sql.append("insert into constant(constant,type,str_value,describe)values('PS_JOB','0','"+ ps_job + "','')");
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
		sql.delete(0,sql.length());
		sql.append("insert into constant(constant,type,str_value,describe)values('PS_C_JOB','0','"+ ps_c_job + "','')");
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
		/*sql.delete(0,sql.length());
		sql.append("insert into constant(constant,type,str_value,describe)values('PS_WORKOUT','0','"+ ps_set + "|" + ps_workfixed + "," + ps_workexist  + "','')");
		
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
		sql.delete(0,sql.length());
		sql.append("insert into constant(constant,type,str_value,describe)values('UNIT_WORKOUT','0','"+ b_set + "|" + plan_num + "," + true_num  + "','')");
		try{
			//System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());
		}catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }*/
		
	}

}
