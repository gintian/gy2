package com.hjsj.hrms.module.kq.config.shiftgroup.transaction;

import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.ShiftService;
import com.hjsj.hrms.module.kq.config.shiftgroup.businessobject.impl.ShiftServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ShiftTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String operation = (String) this.getFormHM().get("operation");
			ShiftService shift = new ShiftServiceImpl(this.userView, this.frameconn);
			if ("deleteShiftInfos".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				String weekScope = (String) this.getFormHM().get("weekScope");
				shift.deleteShiftInfo(groupId, schemeId, weekScope);
			} else if ("searchRemark".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				HashMap<String, String> remarkMap = shift.getShiftRemark(groupId, schemeId);
				this.getFormHM().put("remarkMap", remarkMap);
			} else if ("saveRemark".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				String shiftComment = (String) this.getFormHM().get("shiftComment");
				String empComment = (String) this.getFormHM().get("empComment");
				String trainComment = (String) this.getFormHM().get("trainComment");
				shift.saveShiftRemark(groupId, schemeId, shiftComment, empComment, trainComment);
			} else if ("copyShiftInfo".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				String weekScope = (String) this.getFormHM().get("weekScope");
				String lastWeekScope = (String) this.getFormHM().get("lastWeekScope");
				String copyType = (String) this.getFormHM().get("copyType");
				shift.copyShiftInfo(groupId, schemeId, weekScope, lastWeekScope, copyType);
			} else if ("autoShift".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String fromDate = (String) this.getFormHM().get("fromDate");
				String toDate = (String) this.getFormHM().get("toDate");
				String shfitType = (String) this.getFormHM().get("shfitType");
				shift.autoShift(groupId, fromDate, toDate, shfitType);
				groupId = PubFunc.decrypt(groupId);
				String dateJson = shift.getShiftDate(groupId);
				this.formHM.put("dateJson", dateJson);
			} else if ("pushShiftScheme".equalsIgnoreCase(operation)) {
				String groupId = (String) this.getFormHM().get("groupId");
				String schemeId = (String) this.getFormHM().get("schemeId");
				String state = (String) this.getFormHM().get("state");
				HashMap<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("groupId", groupId);
				paramMap.put("schemeId", schemeId);
				paramMap.put("state", state);
				shift.pushShiftScheme(paramMap);
			} else if ("changeSubmoudleId".equalsIgnoreCase(operation)) {
				String dataType = (String) this.getFormHM().get("dataType");
				shift.changeSubmoudleId(dataType);
			} else {
				String groupId = (String) this.getFormHM().get("groupId");
				if (StringUtils.isEmpty(groupId)) {
					String type = (String) this.getFormHM().get("type");
					ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
					String exp = (String) this.getFormHM().get("exp");
					String cond = (String) this.getFormHM().get("cond");
					shift.filterSql(type, valuesList, exp, cond);
				} else {
					String firstFlag = (String) this.getFormHM().get("firstFlag");
					String year = (String) this.getFormHM().get("year");
					String month = (String) this.getFormHM().get("month");
					String weekIndex = (String) this.getFormHM().get("weekIndex");
					String dataType = (String) this.getFormHM().get("dataType");
//					页面第一次加载数据时要把以前的过滤条件删掉
					if("1".equals(firstFlag)) {
						this.userView.getHm().remove("shiftWhere");
					}
					
					dataType = StringUtils.isEmpty(dataType) ? "shiftData" : dataType;
					groupId = PubFunc.decrypt(groupId);

					Calendar cal = Calendar.getInstance();
					// 设置一周从星期一开始
					cal.setFirstDayOfWeek(Calendar.MONDAY);

					if (StringUtils.isEmpty(year))
						year = String.valueOf(cal.get(Calendar.YEAR));

					if (StringUtils.isEmpty(month))
						month = String.valueOf(cal.get(Calendar.MONTH) + 1);

					if (StringUtils.isEmpty(weekIndex) || Integer.valueOf(weekIndex) == 0) 
						weekIndex = String.valueOf(cal.get(Calendar.WEEK_OF_MONTH));

					String columnJson = shift.getShiftcolumnsJson(Integer.valueOf(year), Integer.valueOf(month),
					        Integer.valueOf(weekIndex), dataType);
					String column = shift.getShiftcolumns(Integer.valueOf(year), Integer.valueOf(month),
					        Integer.valueOf(weekIndex), dataType);
					String schemeId = shift.getSchemeId(year, month, weekIndex, groupId);
					ArrayList<HashMap<String, String>> weekList = shift.weekList(Integer.valueOf(year),
					        Integer.valueOf(month));
					String dateJson = shift.getShiftDate(groupId);
					this.formHM.put("pushScheme", shift.getPushScheme());
					this.formHM.put("groupName", shift.getGroupName());
					this.formHM.put("weekList", weekList);
					this.formHM.put("column", column);
					this.formHM.put("columnJson", columnJson);
					this.formHM.put("year", year);
					this.formHM.put("month", month);
					this.formHM.put("weekIndex", weekIndex);
					this.formHM.put("schemeId", PubFunc.encrypt(schemeId));
					this.formHM.put("dateJson", dateJson);
					this.formHM.put("fieldArray", shift.getFields());
					this.formHM.put("weekScope", shift.getWeekScope(year, month, weekIndex));
					this.formHM.put("lastWeekScope", shift.getLastWeekScope(year, month, weekIndex));
					this.formHM.put("pageRows", shift.getPageSize("shiftData"));
					this.formHM.put("buttonJson", shift.getButtons());
				}
				// 排班页面其他参数
				JSONObject otherParam = new JSONObject();
				// 栏目设置
				otherParam.put("scheme_priv", (this.userView.hasTheFunction("27202020407")) ? "1" : "0");
	    		this.formHM.put("otherParam", otherParam);
			}

			String shiftMsg = (String) this.userView.getHm().get("shiftMsg");
			this.userView.getHm().remove("shiftMsg");
			this.getFormHM().put("shiftMsg", shiftMsg);
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("shiftMsg", e.getMessage());
		}

	}

}
