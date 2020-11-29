package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataMxServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  保存考勤数据明细<br/>
 *  create time 2018-10-25
 * @author haosl
 *
 */
public class OptKqDataMxTrans extends IBusiness {
	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		String jsonStr = (String)this.formHM.get("jsonStr");
		//获取前台json数据
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.frameconn);
		JSONObject returnJson = new JSONObject();
		String return_code="success";
		String return_msg = ResourceFactory.getProperty("kq.date.success.save");
        String type = jsonObj.getString("type");
		try {
			if("save".equals(type)) {
				String scheme_id = jsonObj.getString("scheme_id");
				if(StringUtils.isNotBlank(scheme_id)) {
					scheme_id = PubFunc.decrypt(scheme_id);
				}
				String kq_duration = jsonObj.getString("kq_duration");
				String kq_year = jsonObj.getString("kq_year");
				String guidkey = jsonObj.getString("guidkey");
				JSONObject paramValue = jsonObj.getJSONObject("paramValue");
				String orgId = jsonObj.getString("orgId");
				// 55748 保存时增加校验不可编辑的考勤项目
				String enableModifys = jsonObj.getString("enableModifys");
				service.saveKqDataMx(scheme_id, kq_duration, kq_year, guidkey, paramValue, orgId, enableModifys);
			}else if("initTables".equals(type)){
                //初始化表结构
                DbWizard dbWizard = new DbWizard(this.frameconn);
                //维护表结构，没有创建时间时加上
                if(!dbWizard.isExistField("kq_day_detail","create_time",false)){
                    Table table = new Table("kq_day_detail");
                    Field obj = new Field("create_time", "create_time");
                    obj.setDatatype(DataType.DATETIME);
                    obj.setKeyable(false);
                    obj.setVisible(false);
                    obj.setAlign("left");
                    table.addField(obj);
                    dbWizard.addColumns(table);
                }
                String scheme_id = jsonObj.getString("scheme_id");
                SchemeMainService schemeMainService = new SchemeMainServiceImpl(frameconn,userView);
                HashMap schemeMap = schemeMainService.getSchemeDetailDataList(scheme_id);
                return_code = "success";
                return_msg = "success";
                Integer confirmFlag = schemeMap.get("confirm_flag")==null?0:(Integer)schemeMap.get("confirm_flag");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("confirmFlag",confirmFlag);
                returnJson.put("return_data",jsonObject);
			}
			/* 初始化 */
			else if("init".equals(type)){
                /**
                 * 查询唯一性指标名称，没有则默认是“唯一性指标”
                 */
                return_code = "success";
                KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
                String onlyFieldName =  kqDataUtil.getOnlyFieldName(this.frameconn);
                JSONObject jsonObject = new JSONObject();

                String scheme_id = jsonObj.getString("scheme_id");
                if(StringUtils.isNotBlank(scheme_id)) {
                    scheme_id = PubFunc.decrypt(scheme_id);
                }
                String kq_duration = jsonObj.getString("kq_duration");
                String kq_year = jsonObj.getString("kq_year");
                String orgId = jsonObj.getString("orgId");
                if(StringUtils.isNotBlank(orgId)) {
                    orgId = PubFunc.decrypt(orgId);
                }
                SchemeMainService schemeMainService = new SchemeMainServiceImpl(frameconn, userView);
                ArrayList parameterList = new ArrayList();
                parameterList.add(scheme_id);
                ArrayList<LazyDynaBean> shemeBeanlist = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
                LazyDynaBean shemeBean = shemeBeanlist.get(0);
                //新增人员总数
                Map returnMap_add = service.getChangeStaffs(scheme_id,kq_year,kq_duration,orgId,0,0,"add", shemeBean);
                //减少人员总数
                Map returnMap_del = service.getChangeStaffs(scheme_id,kq_year,kq_duration,orgId,0,0,"del", shemeBean);
                String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
                if(dbNameList.length<2){
                    jsonObject.put("showNbase",false);
                }else{
                    jsonObject.put("showNbase",true);
                }
                jsonObject.put("onlyFieldName",onlyFieldName);
                jsonObject.put("totalCount1", (Integer)returnMap_add.get("totalCount"));
                jsonObject.put("totalCount2", (Integer)returnMap_del.get("totalCount"));
                returnJson.put("return_data",jsonObject);
            }
            else if("getStaffs".equals(type)){
                return_code = "success";
                String scheme_id = jsonObj.getString("scheme_id");
                if(StringUtils.isNotBlank(scheme_id)) {
                    scheme_id = PubFunc.decrypt(scheme_id);
                }
                String kq_duration = jsonObj.getString("kq_duration");
                String kq_year = jsonObj.getString("kq_year");
                String orgId = jsonObj.getString("orgId");
                if(StringUtils.isNotBlank(orgId)) {
                    orgId = PubFunc.decrypt(orgId);
                }
                int limit = Integer.parseInt((String)this.formHM.get("limit")); //条数
                int page = Integer.parseInt((String)this.formHM.get("page")); //页码
                String operation = jsonObj.getString("operation"); //获取 新增或者 减少人员 =add 新增 =del 减少
                Map returnMap = service.getChangeStaffs(scheme_id,kq_year,kq_duration,orgId,limit,page,operation, null);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("staffs",(List)returnMap.get("staffs"));
                jsonObject.put("totalCount", (Integer)returnMap.get("totalCount"));
                returnJson.put("return_data",jsonObject);
            }
            else if("changeStaffs".equals(type)){
                return_code = "success";
                return_msg = "success";
                String scheme_id = jsonObj.getString("scheme_id");
                if(StringUtils.isNotBlank(scheme_id)) {
                    scheme_id = PubFunc.decrypt(scheme_id);
                }
                String kq_duration = jsonObj.getString("kq_duration");
                String kq_year = jsonObj.getString("kq_year");
                List<String> guidkeys = JSONArray.toList(jsonObj.getJSONArray("guidkeys"));
                String opration = jsonObj.getString("opration");
                String orgId = jsonObj.getString("orgId");
                if(StringUtils.isNotBlank(orgId)) {
                    orgId = PubFunc.decrypt(orgId);
                }
                service.changeStaffs(scheme_id,kq_year,kq_duration,guidkeys,opration,orgId);
                Map<String,List<String>> changePerData = service.searchChangePerData(kq_year,kq_duration,orgId,scheme_id);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("changePerData",changePerData);
                returnJson.put("return_data",jsonObject);
            }else if("deletePerons".equals(type)){
                return_code = "success";
                return_msg = "success";
                String scheme_id = jsonObj.getString("scheme_id");
                if(StringUtils.isNotBlank(scheme_id)) {
                    scheme_id = PubFunc.decrypt(scheme_id);
                }
                String kq_duration = jsonObj.getString("kq_duration");
                String kq_year = jsonObj.getString("kq_year");
                String orgId = jsonObj.getString("orgId");
                if(StringUtils.isNotBlank(orgId)) {
                    orgId = PubFunc.decrypt(orgId);
                }
                List<String> guidkeys = JSONArray.toList(jsonObj.getJSONArray("guidkeys"));
                service.deletePersons(scheme_id,kq_year,kq_duration,orgId,guidkeys);
            }
		} catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.date.error.dbacessrror");
            if("save".equals(type)) {
            	if(e.toString().contains(ResourceFactory.getProperty("kq.date.appeal.error.enableModifys"))) {
            		return_msg = ResourceFactory.getProperty("kq.date.appeal.error.enableModifys");
            	}else {
            		return_msg = ResourceFactory.getProperty("kq.date.error.save");
            	}
            }
		}finally {
            returnJson.put("return_code", return_code);
            returnJson.put("return_msg", return_msg);
        }
        this.getFormHM().put("returnStr", returnJson.toString());
	}
	

}
