package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class GetPositionTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String type = (String) this.getFormHM().get("type");
			PositionBo posBo = new PositionBo(this.frameconn);
			posBo.setUserview(this.getUserView());
			HashMap return_data = new HashMap();
			//获取职位列表
			if("list".equalsIgnoreCase(type)) {
			    int pageNum = 1;
                int pageSize = 10;
				String hireChannel = (String) this.getFormHM().get("hire_channel");
				String newHire = (String) this.getFormHM().get("newHire");
				posBo.setNewHire(newHire);
				if("true".equals(newHire)) {
				    pageNum = (Integer) this.getFormHM().get("pageNum");
				    pageSize = (Integer) this.getFormHM().get("pageSize");
				}
				ArrayList queryItem = (ArrayList) this.getFormHM().get("queryitem");
				ArrayList posColumns = posBo.getPositionColumns();
				ArrayList posList = posBo.getPositionDataList(hireChannel,queryItem);
				ArrayList queryitems = posBo.getQueryItems();
				if("true".equals(newHire)) {
				    int posTotal = posList.size();
				    pageNum = (pageNum-1)*pageSize>posTotal? 1 : pageNum;
	                int startIndex = (pageNum-1)*pageSize;
	                int endIndex = pageNum*pageSize;
	                return_data.put("posTotal", posTotal);
	                return_data.put("position_data", posList.subList(startIndex, endIndex>posTotal?posTotal:endIndex));
				}else {
				    return_data.put("position_data", posList);
				}
				return_data.put("columninfo", posColumns);
				return_data.put("queryitems", queryitems);
			}else if("position".equalsIgnoreCase(type)){//获取某一职位详细信息
				if(this.userView!=null) {
					posBo.setUserview(this.userView);
				}
				String zpPosId = (String) this.getFormHM().get("Z0301");
				zpPosId = PubFunc.decrypt(zpPosId);
				ArrayList infoColumns = posBo.getPositionInfoColumns(zpPosId);
				ArrayList positionInfo = posBo.getPositionInfo(zpPosId);
				HashMap<String, String> posInfo = posBo.getPosInfo(zpPosId);
				String hireChannel = posInfo.get("hireChannel");
				String zpDeleteMustSetRecord = SystemConfig.getPropertyValue("zp_delete_must_set_record");
				return_data.put("zp_delete_must_set_record", zpDeleteMustSetRecord);
				return_data.put("hireChannel", hireChannel);
				return_data.put("columninfo", infoColumns);
				return_data.put("position_data", positionInfo);
			}else if("position_desc".equalsIgnoreCase(type)) {//获取职位名称
				String zpPosId = (String) this.getFormHM().get("Z0301");
				zpPosId = PubFunc.decrypt(zpPosId);
				HashMap<String, String> posInfo = posBo.getPosInfo(zpPosId);
				String posDesc = posInfo.get("posDesc");
				return_data.put("posDesc", posDesc);
			}else if("organization".equalsIgnoreCase(type)) {
    			String hireChannel = (String) this.getFormHM().get("hire_channel");
    			ArrayList<LazyDynaBean> unitList = posBo.getZPUnitList("UN", hireChannel);
    			ArrayList<LazyDynaBean> departmentList = posBo.getZPUnitList("UM", hireChannel);
    			return_data.put("unitList", unitList);
    			return_data.put("departmentList", departmentList);
			}
			this.formHM.put("return_data", return_data);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
