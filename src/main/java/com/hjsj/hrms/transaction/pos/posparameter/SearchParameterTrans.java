package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.StringTokenizer;

public class SearchParameterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.getFrameconn());
		RecordVo unit_workout_vo=ConstantParamter.getRealConstantVo("UNIT_WORKOUT",this.getFrameconn());
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
//		进行单位编制控制
		String unitvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"unit");
//		进行职位控制
		String psvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"pos");
		this.getFormHM().put("unitvalid",unitvalid);
		this.getFormHM().put("psvalid",psvalid);
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		if(reqhm.containsKey("flag")){
			String flag=(String) reqhm.get("flag");
			if("unit".equals(flag)){
				this.getFormHM().put("flag","unit");
			}else{
				this.getFormHM().put("flag","pos");
			}
			reqhm.remove("flag");
		}else{
			this.getFormHM().put("flag","all");
		}
		if(unit_workout_vo!=null){
			String unit_workout=unit_workout_vo.getString("str_value");
			StringTokenizer str=new StringTokenizer(unit_workout,"|");
			if(str.hasMoreTokens())
			  {
				  this.getFormHM().put("unit_set",str.nextToken().toUpperCase());
			      if(str.hasMoreTokens())
			      {
			    	 StringTokenizer strfield=new StringTokenizer(str.nextToken(),",");
			    	 if(strfield.hasMoreTokens())
			    	 {
			    		this.getFormHM().put("plan_num",strfield.nextToken());
			    		this.getFormHM().put("true_num",strfield.nextToken());
			    	 }
			      }
			  }
		}
		if(ps_superior_vo!=null)
		{
		  String  ps_superior=ps_superior_vo.getString("str_value");
		  this.getFormHM().put("ps_superior",ps_superior);
		}
		RecordVo ps_job_vo=ConstantParamter.getRealConstantVo("PS_JOB",this.getFrameconn());
		if(ps_job_vo!=null)
		{
		  String  ps_job=ps_job_vo.getString("str_value");
		  this.getFormHM().put("ps_job",ps_job);
		}
		RecordVo ps_c_job_vo=ConstantParamter.getRealConstantVo("PS_C_JOB",this.getFrameconn());
		if(ps_c_job_vo!=null)
		{
		  String  ps_c_job=ps_c_job_vo.getString("str_value");
		  this.getFormHM().put("ps_c_job",ps_c_job);
		}
		RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.getFrameconn());
		if(ps_workout_vo!=null)
		{
		  String  ps_workout=ps_workout_vo.getString("str_value");
		  StringTokenizer str=new StringTokenizer(ps_workout,"|");//K01|K0114,K0111
		  if(str.hasMoreTokens())
		  {
			  this.getFormHM().put("ps_set",str.nextToken().toUpperCase());
		      if(str.hasMoreTokens())
		      {
		    	 StringTokenizer strfield=new StringTokenizer(str.nextToken(),",");
		    	 if(strfield.hasMoreTokens())
		    	 {
		    		this.getFormHM().put("ps_workfixed",strfield.nextToken());
		    		this.getFormHM().put("ps_workexist",strfield.nextToken());
		    	 }
		      }
		  }
		}
		RecordVo constant_vo=ConstantParamter.getRealConstantVo("PS_CODE",this.getFrameconn());
		if(constant_vo!=null)
		{
		  String  ps_code=constant_vo.getString("str_value");
		  this.getFormHM().put("sqlstr","select itemid,itemdesc from fielditem where useflag='1' and  fieldsetid='K01' and codesetid='" + ps_code + "'");
		}
		else
		{
			this.getFormHM().put("sqlstr","select itemid,itemdesc from fielditem where useflag='1' and  fieldsetid='K01' and codesetid=''");
			
			///throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("pos.posparameter.nosetcode"),"",""));
		}
	}

}
