package com.hjsj.hrms.module.kq.config.shiftgroup.transaction;

import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 排班方案的班次数据
 * 
 * @Title: ShiftDataTrans.java
 * @Description: 用于显示、保存某排班方案的班次数据的交易类
 * @Company: hjsj
 * @Create time: 2018年11月28日 下午6:45:47
 * @author chenxg
 * @version 7.5
 */
public class ShiftDataTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			/**
			 * tpye
			 * =null：获取页面显示数据；=shifts:获取有效的班次信息；=saveShiftInfos：保存排班信息；=saveSchemeEmp：保存排班方案人员对应表中的信息；
			 * =saveDropSort：保存拖拽人员的顺序；=searchPersonInfo：获取调换人员的信息；=savePersonChange：保存人员调换的数据
			 */
			String tpye = (String) this.getFormHM().get("type");
			ShiftService shift = new ShiftServiceImpl(this.userView, this.frameconn);
			if ("shifts".equalsIgnoreCase(tpye)) {
				// 获取有效的班次信息
				String classIds = (String) this.getFormHM().get("classIds");
				String shiftInfoFlag = (String) this.getFormHM().get("shiftFlag");
				String groupId = (String) this.getFormHM().get("groupId");
				ArrayList<HashMap<String, String>> shiftInfoList = shift.getShiftInfoList(groupId, classIds, shiftInfoFlag);
				this.getFormHM().put("shiftInfoList", shiftInfoList);
			} else if ("saveShiftInfos".equalsIgnoreCase(tpye)) {
				// 保存排班信息
				String date = (String) this.getFormHM().get("date");
				String dataJson = (String) this.getFormHM().get("record");
				JSONObject record = JSONObject.fromObject(dataJson);
				shift.saveShiftInfoS(date, record);
			} else if ("saveSchemeEmp".equalsIgnoreCase(tpye)) {
				// 保存排班方案人员对应表中的信息
				String schemeId = (String) this.getFormHM().get("schemeId");
				String itemId = (String) this.getFormHM().get("itemId");
				String dataJson = (String) this.getFormHM().get("record");
				JSONObject record = JSONObject.fromObject(dataJson);
				shift.saveSchemeEmp(schemeId, itemId, record);
			} else if ("saveDropSort".equalsIgnoreCase(tpye)) {
				// 保存拖拽人员的顺序
				String schemeId = (String) this.getFormHM().get("schemeId");
				String guidKey = (String) this.getFormHM().get("guidKey");
				String modelGuidKey = (String) this.getFormHM().get("modelGuidKey");
				String dropPosition = (String) this.getFormHM().get("dropPosition");
				shift.saveDropSort(schemeId, guidKey, modelGuidKey, dropPosition);
			} else if ("searchPersonInfo".equalsIgnoreCase(tpye)) {
				// 获取调换人员的信息
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				String guidKey = (String) this.getFormHM().get("guidKey");
				String filterValue = (String) this.getFormHM().get("filterValue");
				ArrayList<HashMap<String, String>> personList = shift.searchPsrsonInfo(groupId, schemeId, guidKey,
				        filterValue);
				this.getFormHM().put("personList", personList);
			} else if ("savePersonChange".equalsIgnoreCase(tpye)) {
				// 保存人员调换的数据
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("oldGuidKey", (String) this.getFormHM().get("oldGuidKey"));
				paramMap.put("newGuidKey", (String) this.getFormHM().get("newGuidKey"));
				paramMap.put("year", (String) this.getFormHM().get("year"));
				paramMap.put("month", (String) this.getFormHM().get("month"));
				paramMap.put("weekIndex", (String) this.getFormHM().get("weekIndex"));
				paramMap.put("schemeId", (String) this.getFormHM().get("schemeId"));
				shift.savePersonChange(paramMap);
			} else if ("saveCopyShiftInfos".equalsIgnoreCase(tpye)) {
				// 保存粘帖的数据
				String copyShifts = (String) this.getFormHM().get("copyShifts");
				JSONArray records = JSONArray.fromObject(copyShifts);
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				shift.copyShifts(records, groupId, schemeId);
			} else {
				// 获取页面显示数据
				String year = (String) this.getFormHM().get("year");
				String month = (String) this.getFormHM().get("month");
				String weekIndex = (String) this.getFormHM().get("weekIndex");
				String page = (String) this.getFormHM().get("page");
				String limit = (String) this.getFormHM().get("limit");
				String groupId = (String) this.getFormHM().get("groupId");
				ArrayList<String> fields = (ArrayList<String>) this.getFormHM().get("fields");
				// 44980 更改获取排班 审查SQL方式
//				String dataSql = (String) this.getFormHM().get("dataSql");
				String dataType = (String) this.getFormHM().get("dataType");
				// 53632 清除条件较早  应该在用完再清除
//				String removeFilter = (String) this.getFormHM().get("removeFilter");
				String filterParam = (String) this.getFormHM().get("filterParam");
				filterParam = StringUtils.isEmpty(filterParam) ? "" : filterParam;
				String flag = (String) this.getFormHM().get("flag");
				if(!"1".equals(flag))
					this.userView.getHm().remove("shiftFilterWhere");
				
				if (StringUtils.isNotEmpty(groupId))
					groupId = PubFunc.decrypt(groupId);

				String[] cloumns = new String[fields.size()];
				for (int i = 0; i < fields.size(); i++)
					cloumns[i] = fields.get(i);

				HashMap<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("year", year);
				paramMap.put("month", month);
				paramMap.put("weekIndex", weekIndex);
				paramMap.put("limit", limit);
				paramMap.put("page", page);
				paramMap.put("groupId", groupId);
				paramMap.put("cloumns", cloumns);
//				paramMap.put("dataSql", dataSql);
				paramMap.put("dataType", dataType);
				paramMap.put("filterParam", filterParam);
				ArrayList<LazyDynaBean> dataList = shift.shiftDataList(paramMap);
				int totalCount = shift.getTotalCount();
				this.getFormHM().put("data", dataList);
				this.getFormHM().put("totalCount", totalCount);
				this.getFormHM().put("limit", limit);
			}

			this.getFormHM().put("countDataMap", shift.getCountDataMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
