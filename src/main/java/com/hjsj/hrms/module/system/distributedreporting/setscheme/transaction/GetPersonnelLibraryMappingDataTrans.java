package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @version: 1.0
 * @Description: 获取已经设置的人员库映射
 * @author: zhiyh  
 * @date: 2019年3月13日 上午9:18:11
 */
public class GetPersonnelLibraryMappingDataTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(this.userView, this.frameconn);
			String fielditem = (String) this.getFormHM().get("fielditem");
			String fileds = (String) this.getFormHM().get("fileds");
			ArrayList<HashMap<String,String>> fieldlist = new ArrayList<HashMap<String,String>>();
			if (fileds.toUpperCase().indexOf(fielditem)!=-1) {
				 fieldlist = bo.getfieldcodeList(fielditem);
			}
			this.getFormHM().put("list", fieldlist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
