package com.hjsj.hrms.module.system.sdparameter.transaction;

import com.hjsj.hrms.module.system.sdparameter.businessobject.SDParameterService;
import com.hjsj.hrms.module.system.sdparameter.businessobject.impl.SDParameterServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 二开参数设置交易类
 * @author wangbo 2019-09-19
 * @category hjsj
 * @version 1.0
 */
public class SDParameterTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap resultMap = new HashMap();
		HashMap return_data = new HashMap();
		int pageSize = 20;//初始化每页数据20条
		int showPage = 1;//初始化加载第一页
		String gridConfig = "";//表格信息
		try {
			SDParameterService sdParameterServerImpl = new SDParameterServiceImpl(this.userView, this.frameconn);
			String type = (String) this.getFormHM().get("type");
			if("main".equalsIgnoreCase(type)){
				gridConfig = sdParameterServerImpl.getTableConfig(showPage,pageSize);//初始化显示第一页，每页默认20条
			}else if ("save".equalsIgnoreCase(type)) {//保存操作
				ArrayList parameter = (ArrayList) this.getFormHM().get("paramter");
				sdParameterServerImpl.saveParameter(parameter);
			} else if ("delete".equalsIgnoreCase(type)){//删除操作
				String constants = (String) this.getFormHM().get("constants");
				sdParameterServerImpl.deleteParameter(constants);
			} else if("add".equalsIgnoreCase(type)){
				pageSize = (Integer) this.getFormHM().get("pageSize");
				showPage = sdParameterServerImpl.insertParamData(pageSize);
			}
			return_data.put("gridconfig", gridConfig);
			resultMap.put("return_code", "success");
			resultMap.put("return_data", return_data);
			resultMap.put("showPage",showPage);
		} catch (GeneralException e) {
			e.printStackTrace();
			resultMap.put("return_code", "fail");
			resultMap.put("return_msg", e.getErrorDescription());
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("return_code", "fail");
			resultMap.put("return_msg", e.getMessage());
		}
		this.formHM.put("returnStr", resultMap);
	}
}
