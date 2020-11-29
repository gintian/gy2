package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveShowhideItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String basetype = (String)this.getFormHM().get("basetype");
		String items = (String)this.getFormHM().get("items");
		RecordVo vo = new RecordVo("constant");
		try{
			ContentDAO dao  = new ContentDAO(this.frameconn);
			if("5".equals(basetype)){//LAW_BASE_DOCTYPE
				vo.setString("constant", "LAW_BASE_DOCTYPE");
				vo.setString("str_value", items);
				if(ConstantParamter.getConstantVo("LAW_BASE_DOCTYPE")!=null){
					dao.updateValueObject(vo);
				}else{
					dao.addValueObject(vo);
				}
				ConstantParamter.putConstantVo(vo, "LAW_BASE_DOCTYPE");
			}else if("1".equals(basetype)){//LAW_BASE_LAWRULE
				vo.setString("constant", "LAW_BASE_LAWRULE");
				vo.setString("str_value", items);
				if(ConstantParamter.getConstantVo("LAW_BASE_LAWRULE")!=null){
					dao.updateValueObject(vo);
				}else{
					dao.addValueObject(vo);
				}
				ConstantParamter.putConstantVo(vo, "LAW_BASE_LAWRULE");
			}else if("4".equals(basetype)){//LAW_BASE_KNOWTYPE
				vo.setString("constant", "LAW_BASE_KNOWTYPE");
				vo.setString("str_value", items);
				if(ConstantParamter.getConstantVo("LAW_BASE_KNOWTYPE")!=null){
					dao.updateValueObject(vo);
				}else{
					dao.addValueObject(vo);
				}
				ConstantParamter.putConstantVo(vo, "LAW_BASE_KNOWTYPE");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(e);
		}
	}
}
