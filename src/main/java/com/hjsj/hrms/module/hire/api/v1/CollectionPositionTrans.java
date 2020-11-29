package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class CollectionPositionTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String return_code = "success";
		String return_msg = "";
		HashMap return_data = new HashMap();
		try {
			String type = (String) this.getFormHM().get("type");
			PositionBo bo = new PositionBo(this.frameconn, this.userView);
			//收藏职位
			if("add".equalsIgnoreCase(type)) {
				String pos_id = (String) this.getFormHM().get("pos_id");
				pos_id = PubFunc.decrypt(pos_id);
				return_code = bo.collectionPos(pos_id);
			//获取收藏职位
			} else if("search".equalsIgnoreCase(type)) {
				ArrayList posColumns = bo.getPositionColumns();
				ArrayList collections = bo.searchCollection();
				return_data.put("position_data", collections);
				return_data.put("columninfo", posColumns);
			//取消收藏职位
			} else if("remove".equalsIgnoreCase(type)) {
				String pos_id = (String) this.getFormHM().get("pos_id");
				pos_id = PubFunc.decrypt(pos_id);
				return_code = bo.cancelCollection(pos_id);
			}
			this.getFormHM().put("return_data", return_data);
			this.getFormHM().put("return_code", return_code);
			this.getFormHM().put("return_msg", return_msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
