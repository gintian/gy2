package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ExportXZTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String plan_id = (String)this.getFormHM().get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isHavePriv(this.userView, plan_id);
			if(!_flag){
				return;
			}
			String object_type = (String)this.getFormHM().get("object_type");//考核对象类型  1:部门 2:人员 3:单位 4.部门
			String onlyname = "";
			//先查出唯一标识
			if("2".equals(object_type)){
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
				if(onlyname==null || "".equals(onlyname))
					throw GeneralExceptionHandler.Handle(new Exception("请到系统参数中维护“唯一性指标”！"));
			}else{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
				if(unit_code_field_constant_vo!=null){
					onlyname=unit_code_field_constant_vo.getString("str_value");
					if(onlyname==null || "".equals(onlyname))
						throw GeneralExceptionHandler.Handle(new Exception("请到'岗位参数设置'中维护'单位代码指标'！"));
				}else{
					throw GeneralExceptionHandler.Handle(new Exception("请到'岗位参数设置'中维护'单位代码指标'！"));
				}
			}
			//如果设置了唯一性指标，把人员和唯一性指标查出来
			PerEvaluationBo pe = new PerEvaluationBo(this.getFrameconn(),this.getUserView());
			ArrayList onlynameList = pe.getOnlynameList(onlyname,plan_id,object_type);
			String filename = "";
			filename = pe.creatExcel(onlynameList,object_type);
			filename = PubFunc.encrypt(filename);
			this.getFormHM().put("filename", filename);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
