package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.utils.components.fielditemmultiselector.businessobject.GetFieldItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OptionalQueryTrans extends IBusiness  {

	@Override
	public void execute() throws GeneralException {
	    try {
	    	String type = (String) this.getFormHM().get("type");
	    	GetFieldItemBo getFieldSetbo = new GetFieldItemBo(this.getFrameconn());
			ArrayList list = new ArrayList();
			if("1".equals(type)){
				list = getFieldSetbo.getFieldSetList("A", "ZP");
				Map map = new HashMap();
				map.put("fieldsetid","my_custom");
				map.put("fieldsetdesc","系统变量");
				list.add(map);
			}else{
				String value = (String) this.getFormHM().get("value");
				//调用的模块
				list = getFieldSetbo.getFieldItemList(value, "ZP");
			}
			this.getFormHM().put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
