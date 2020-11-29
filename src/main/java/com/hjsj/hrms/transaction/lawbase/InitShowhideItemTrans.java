package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitShowhideItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String basetype = (String)this.getFormHM().get("basetype");
		String viewhide = "";
		if("5".equals(basetype)){//LAW_BASE_DOCTYPE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_DOCTYPE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}else if("1".equals(basetype)){//LAW_BASE_LAWRULE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_LAWRULE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}else if("4".equals(basetype)){//LAW_BASE_KNOWTYPE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_KNOWTYPE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}
		viewhide = viewhide==null?"":viewhide;
		this.getFormHM().put("viewhide"+basetype, viewhide);
	}

}
