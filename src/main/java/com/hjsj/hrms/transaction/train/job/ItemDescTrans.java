package com.hjsj.hrms.transaction.train.job;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operatorTableName=(String)hm.get("operator");  // r04:教师表 r07:培训资料表 r10：培训场地表
		String id=(String)hm.get("id");
		id=PubFunc.decrypt(SafeCode.decode(id));
		
		ArrayList list=DataDictionary.getFieldList(operatorTableName,Constant.USED_FIELD_SET);
		TrainClassBo trainClassBo=new TrainClassBo(this.getFrameconn());
		ArrayList alist=trainClassBo.getItemFieldList(list,operatorTableName,id);
		this.getFormHM().put("trainResourceDesc",alist);
		this.getFormHM().put("titleName",trainClassBo.getTitleName(operatorTableName,id));
	}

}
