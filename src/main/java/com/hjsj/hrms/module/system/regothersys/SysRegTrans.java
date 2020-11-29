package com.hjsj.hrms.module.system.regothersys;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.log4j.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成注册服务Trans类
 * @author cuibl
 *
 */
public class SysRegTrans extends IBusiness {

	// 日志
	private Category log = Category.getInstance(getClass().getName());
	
	@Override
	public void execute() throws GeneralException {
		String method = (String) this.formHM.get("method");
		SysRegBo bo = new SysRegBo();
		if("init".equalsIgnoreCase(method)) {
			String tableConfig = bo.initSysRegList(userView,this.frameconn);
			this.formHM.clear();
			this.formHM.put("sysRegGridTable", tableConfig);
		}else if("check".equalsIgnoreCase(method)) {
			String result = "true";
			String checkCode = (String) this.formHM.get("checkCode");
			String checkName = (String) this.formHM.get("checkName");
			String opt = (String) this.formHM.get("addOrEdit");
			String editSysEtoken = (String) this.formHM.get("editSysEtoken");
			String codeResult = bo.checkCode(opt,checkCode,editSysEtoken);
			if(!"true".equals(codeResult)) {
				this.getFormHM().put("result",codeResult);
				return;
			}
			String nameResult = bo.checkName(opt,checkName,editSysEtoken);
			if(!"true".equals(nameResult)) {
				this.getFormHM().put("result",nameResult);
				return;
			}
			this.getFormHM().put("result",result);
		}else if("add".equalsIgnoreCase(method)) {
			Map<String, String> map = new HashMap<String, String>();
			String syscode = (String) this.formHM.get("sysCode");
			map.put("sysCode", syscode);
			String sysname = (String) this.formHM.get("sysName");
			map.put("sysName", sysname);
			String sysetoken = (String) this.formHM.get("etoken");
			map.put("etoken", sysetoken);
			String sysdesc = (String) this.formHM.get("description");
			map.put("description", sysdesc);
			String valid = ((Boolean)this.formHM.get("valid"))==true?"1":"0";
			map.put("valid", valid);
			String dynaCode = ((Boolean)this.formHM.get("dynaCode"))==true?"1":"0";
			map.put("dynaCode", dynaCode);
			String orgExper = (String) this.formHM.get("orgExper");
			map.put("orgExper", orgExper);
			String postExper = (String) this.formHM.get("postExper");
			map.put("postExper", postExper);
			String empExper = (String) this.formHM.get("empExper");
			map.put("empExper", empExper);
			ArrayList<String> serviceList = (ArrayList<String>) this.formHM.get("serviceList");
			boolean result = bo.addSys(map,serviceList);
			this.getFormHM().put("result",result);
		}else if("delete".equalsIgnoreCase(method)) {
			ArrayList<String> datalist=(ArrayList) this.getFormHM().get("deletedata");
			bo.deleteSys(datalist);
		}else if("toEdit".equalsIgnoreCase(method)) {
			String id = (String) this.getFormHM().get("id");
			Map<String, Object> resultMap = bo.toEditSys(id);
			boolean flag = (Boolean) resultMap.get("result");
			HashMap<String, Object> map = (HashMap<String, Object>) resultMap.get("targetSystem");
			List<Map<String, String>> editServiceList = (List<Map<String, String>>) resultMap.get("serviceList");
			this.formHM.put("result", flag);
			this.formHM.put("targetSystem", map);
			this.formHM.put("serviceList", editServiceList);
		}else if("edit".equalsIgnoreCase(method)) {
			Map<String, String> map = new HashMap<String, String>();
			String id = (String) this.getFormHM().get("id");
			map.put("id", id);
	  		String syscode = (String) this.formHM.get("sysCode");
	  		map.put("sysCode", syscode);
			String sysname = (String) this.formHM.get("sysName");
			map.put("sysName", sysname);
			String sysdesc = (String) this.formHM.get("description");
			map.put("description", sysdesc);
			String valid = ((Boolean)this.formHM.get("valid"))==true?"1":"0";
			map.put("valid", valid);
			String dynaCode = ((Boolean)this.formHM.get("dynaCode"))==true?"1":"0";
			map.put("dynaCode", dynaCode);
			String orgExper = (String) this.formHM.get("orgExper");
			map.put("orgExper", orgExper);
			String postExper = (String) this.formHM.get("postExper");
			map.put("postExper", postExper);
			String empExper = (String) this.formHM.get("empExper");
			map.put("empExper", empExper);
			ArrayList<String> serviceList = (ArrayList<String>) this.formHM.get("serviceList");
			boolean result = bo.editSys(map,serviceList);
			this.getFormHM().put("result",result);
		}else if("download".equalsIgnoreCase(method)) {
			String name = (String) this.getFormHM().get("name");
			Map<String, Object> resultMap = bo.getDownLoadPath(name);
	    	String filePath = (String) resultMap.get("filePath");
	    	boolean fileExists = (Boolean) resultMap.get("result");
	    	String fileName = (String) resultMap.get("fileName");
	    	this.getFormHM().put("filePath", filePath);
	    	this.getFormHM().put("result", fileExists);
	    	this.getFormHM().put("fileName", fileName);
		}else if("serviceList".equalsIgnoreCase(method)) {
			List<Map<String, String>> serviceList = bo.getServiceList();
			this.getFormHM().put("data", serviceList);//最终指标json串
		}
	}
	
}
