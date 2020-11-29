package com.hjsj.hrms.module.kq.config.scheme.transaction;

import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SchemeMainTrans  extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String jsonStr=(String)this.getFormHM().get("jsonStr");
			JSONObject jsonObject = null;
			/**
			 * list:获得方案列表
			 * delete:删除方案
			 * get_info:获得方案详细信息
			 * save:保存方案信息
			 * ...
			 * checkReviewPerson:校验审核人是否存在待办
			 */
			String type = "";
			
			int currentPage = 0;
			int pageSize = 0;
			if(StringUtils.isNotBlank(jsonStr)) {
				jsonObject = JSONObject.fromObject(jsonStr);
				type = jsonObject.getString("type");
			}else {
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
		    	currentPage = (Integer)bean.get("currentPage") -1;
				pageSize = (Integer)bean.get("pageSize");
				type = (String) bean.get("type");
			}
			
			SchemeMainService SchemeMainBo = new SchemeMainServiceImpl(this.frameconn, this.userView);
			JSONObject returnStr = new JSONObject();
			String return_code = "";
			String return_msg = "";
			if("list".equalsIgnoreCase(type)) {//获得方案列表
				ArrayList inputValue = (ArrayList) this.getFormHM().get("inputValues");
				if(jsonObject != null) {
					currentPage = jsonObject.getInt("currentPage")-1;
					pageSize = jsonObject.getInt("pageSize");
					if((inputValue == null || inputValue.size() == 0) && jsonObject.containsKey("inputValues")) {
						JSONArray inputValueJson = jsonObject.getJSONArray("inputValues");
						inputValue = new ArrayList();
						for(int i = 0; i < inputValueJson.size(); i++) {
							inputValue.add(inputValueJson.get(i));
						}
					}
				}
				HashMap map = SchemeMainBo.getSchemeDataList(currentPage, pageSize, inputValue);
				HashMap return_data = new HashMap();
				return_data.put("data", map.get("children"));
	            return_data.put("totalCount", map.get("totalCount"));
				returnStr.put("return_code", "success");
				returnStr.put("return_msg", "success");
				returnStr.put("return_data", return_data);
				this.getFormHM().put("returnStr", returnStr);
			}else if("get_info".equalsIgnoreCase(type)) {//获得方案详细信息
				returnStr.put("return_data", SchemeMainBo.getSchemeDetailDataList(jsonObject.get("id")==null?"" : jsonObject.getString("id")));
				returnStr.put("return_code", "success");
				returnStr.put("return_msg", "success");
				this.getFormHM().put("returnStr", returnStr);
			}else if("save".equalsIgnoreCase(type)) {//保存方案信息
				int count = SchemeMainBo.saveData(jsonObject.getJSONObject("info"));
				if(count > 0) {
					return_code = "success";
					return_msg = "";
				}else {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("lable.performance.saveFail");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
			}else if("sort_range".equalsIgnoreCase(type)) {//应用范围有父节点时，其子节点不用保存
				JSONArray org_id = jsonObject.getJSONArray("org_id");
				JSONArray org_name = jsonObject.getJSONArray("org_name");
				JSONArray org_id_appear = jsonObject.getJSONArray("org_ids");//数据上报的机构
				HashMap map = SchemeMainBo.getSortRangeMap(org_id, org_name, org_id_appear);
				if(map.size() > 0) {
					return_code = "success";
					return_msg = "";
				}else {
					return_code = "fail";
					//操作失败
					return_msg = ResourceFactory.getProperty("kq.machine.error");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				returnStr.put("return_data", map);
				
				this.getFormHM().put("returnStr", returnStr);
			}else if("changeState".equalsIgnoreCase(type)){
				int count = SchemeMainBo.changeState(jsonObject);
				if(count > 0) {
					return_code = "success";
					return_msg = "";
				}else {
					return_code = "fail";
					//操作失败
					return_msg = ResourceFactory.getProperty("kq.machine.error");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				
				this.getFormHM().put("returnStr", returnStr);
			}else if("delete".equalsIgnoreCase(type)) {
				int count = SchemeMainBo.deleteScheme(jsonObject);
				if(count > 0) {
					return_code = "success";
					//删除成功
					return_msg = ResourceFactory.getProperty("codemaintence.code.delmessagesuc");
				}else {
					return_code = "fail";
					//删除失败
					return_msg = ResourceFactory.getProperty("codemaintence.delcode.fail");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				
				this.getFormHM().put("returnStr", returnStr);
			}else if("getImg".equalsIgnoreCase(type)) {
				String scheme_ids = jsonObject.getString("busiName");
				String imgPath = SchemeMainBo.getZiZhuImg(scheme_ids);
				if(!"error".equalsIgnoreCase(imgPath)) {
					return_code = "success";
					return_msg = "";
				}else {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.scheme.getZiZhuImgFail");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				returnStr.put("return_data", imgPath);
				this.getFormHM().put("returnStr", returnStr);
			}else if("changePerson".equalsIgnoreCase(type)) {
				String username = jsonObject.getString("username");
				String fullname = jsonObject.getString("fullname");
				boolean flag = jsonObject.getBoolean("flag");
				String org_id = jsonObject.getString("org_id");
				String scheme_id = jsonObject.getString("scheme_id");
				String old_name = jsonObject.getString("old_name");
				int num = SchemeMainBo.changeClerkOrReviewFromList(username, fullname, flag, org_id, scheme_id, old_name);
				if(num > 0) {
					return_code = "success";
					return_msg = "";
				}else {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.scheme.updateImg");
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				this.getFormHM().put("returnStr", returnStr);
			}else if("checkReviewPerson".equalsIgnoreCase(type)) {
				String org_id = jsonObject.getString("org_id");
				String scheme_id = jsonObject.getString("scheme_id");
				String old_name = jsonObject.getString("old_name");
				// 55945 删除前先校验  所删除用户是否存在待办  如果存在则不允许操作
				boolean bool = SchemeMainBo.checkReviewPersonDealt(org_id, scheme_id, old_name);
				if(bool) {
					return_code = "fail";
					return_msg = "-1";
				}else {
					return_code = "success";
					return_msg = "";
				}
				returnStr.put("return_code", return_code);
				returnStr.put("return_msg", return_msg);
				this.getFormHM().put("returnStr", returnStr);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
