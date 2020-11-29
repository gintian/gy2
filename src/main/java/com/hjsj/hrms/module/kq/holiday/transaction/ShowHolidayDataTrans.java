package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 假期管理
 * @Title:        ShowHolidayDataTrans.java
 * @Description:  假期管理显示数据调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017-11-1 上午11:02:23
 * @author        chenxg
 * @version       1.0
 */
public class ShowHolidayDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try{
        	String flag = (String) this.getFormHM().get("flag");
            String holidayType = (String) this.getFormHM().get("holidayType");
            holidayType = StringUtils.isNotEmpty(holidayType) ? PubFunc.decrypt(holidayType) : "";
            String isload = (String) this.getFormHM().get("isload");
            String year = (String) this.getFormHM().get("year");
            HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
            if(flag.contains(",all,") || flag.contains(",init,")){
            	// 同样的参数不知为何校验两遍 
	            if(StringUtils.isNotEmpty(isload)) {
	                String holidayTypeJson=bo.getHolsList(holidayType);
	                this.getFormHM().put("holidayTypeJson", holidayTypeJson);
	                String holidayYearJson = bo.getAllHolidayYear();
	                this.getFormHM().put("holidayYearJson", holidayYearJson);
	            }

				String leaveTimeType = bo.getLeaveTimeTypeUsedOverTime();
				if(StringUtils.isEmpty(year)) {
					year = bo.getHolidYear();

					if (StringUtils.isNotBlank(leaveTimeType) && leaveTimeType.equalsIgnoreCase(holidayType)) {
	            		KqOverTimeForLeaveBo kqBo = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
	            		year = kqBo.getShowLeaveCycle();
	            	}
	            }
	            if(StringUtils.isEmpty(holidayType))
	            	holidayType = bo.getHolidTpye();
	            // 42905医院考勤管理提前校验假期类别
	            if(StringUtils.isBlank(holidayType)) {
	            	this.getFormHM().put("errors", ResourceFactory.getProperty("kq.holiday.nocode"));
	            	return;
	            }
	            // 是否显示导入按钮标识
	            boolean showFlag = true;
	            String subModuleId = "";
	            String sql = "";
	            ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
	            ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
	            String orderBy = "";
	            // 33247 linbz holidayType为空是未设置调休假，增加校验
	            if(StringUtils.isNotEmpty(holidayType) && leaveTimeType.equalsIgnoreCase(holidayType)) {
	                subModuleId = "holidayLeaveTimeType_0001";
	                columnList = bo.getLeaveTimeTypeColumns(subModuleId);
	                sql = bo.getHolidayLeaveTimeTypeSql(columnList, year);
	                buttonList = bo.getButtonList(holidayType);
	                orderBy = "order by b0110,e0122,e01A1,a0100";
	                KqOverTimeForLeaveBo kqBo = new KqOverTimeForLeaveBo(this.frameconn, this.userView);
	                String holidayYearJson = kqBo.getLeaveCycleList();
	                this.getFormHM().put("holidayYearJson", holidayYearJson);
	                this.getFormHM().put("otForLeaveCycle", KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_CYCLE());
	                this.getFormHM().put("leaveActiveTime", bo.getLeaveActiveTime());
	                showFlag = bo.checkImportLeaveFlag(year);
	            }else {
	            	subModuleId = "holiday_0001";
	                columnList = bo.getColumns(subModuleId);
	                sql = bo.getHolidaySql(columnList, holidayType, year);
	                buttonList = bo.getButtonList(holidayType);
	                orderBy = "order by sort,b0110,e0122,e01a1,a0100";
	                showFlag = bo.checkDuration(year);
	            }
	            if(StringUtils.isEmpty(subModuleId)) 
	            	return;
	            
	            // 保留查询条件标识
				String querySqlAdd = "";
				String musterSubModuleId = "";
	            if(flag.contains(",holdQuery,")) {
	            	musterSubModuleId = (String) this.getFormHM().get("subModuleId");
		            if(StringUtils.isEmpty(musterSubModuleId)) 
		            	return;
	            	TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(musterSubModuleId);
	            	querySqlAdd = null==catche ? "" : catche.getQuerySql();
	            	querySqlAdd = StringUtils.isEmpty(querySqlAdd) ? "" : querySqlAdd;
	            }
	            /** 加载表格 */
	            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnList, subModuleId, this.userView, this.frameconn);
	            builder.setLockable(true);
	            builder.setDataSql(sql);
	            builder.setOrderBy(orderBy);
	            builder.setAutoRender(false);
	            builder.setTitle("假期管理");
	            builder.setSetScheme(true);
	            builder.setScheme(true);
	            builder.setPageSize(20);
	            builder.setTableTools(buttonList);
	            builder.setColumnFilter(true);
	            builder.setSelectable(true);
	            builder.setEditable(true);
	            builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TOOLBAR);
	            builder.setItemKeyFunctionId("KQ00010014");
	            // 34312 增加栏目设置保存后的回调函数
	            builder.setSchemeSaveCallback("holidayManage.schemeSaveCallback");
	            String config = builder.createExtTableConfig();
	            this.getFormHM().put("tableConfig", config.toString());
	            
	            this.getFormHM().put("holidayType", PubFunc.encrypt(holidayType));
	            this.getFormHM().put("leaveTimeType", PubFunc.encrypt(bo.getLeaveTimeTypeUsedOverTime()));
	            
	            this.getFormHM().put("year", year);
	            this.getFormHM().put("subModuleId", subModuleId);
	            // 保留查询条件标识
	            if(flag.contains(",holdQuery,")) {
		            TableDataConfigCache catcheNew = (TableDataConfigCache) this.userView.getHm().get(musterSubModuleId);
		            catcheNew.setQuerySql(querySqlAdd);
	            }
	            StringBuffer menus = new StringBuffer("[");
				if(!holidayType.equalsIgnoreCase(bo.getLeaveTimeTypeUsedOverTime())
						&& this.userView.hasTheFunction("27042"))
					menus.append("{text: '计算公式',handler:holidayManage.setCalculationFormula},");
				
				if(this.userView.hasTheFunction("27048"))
					menus.append("{text: '导入数据', id:'importData',hidden:"+String.valueOf((!showFlag))+", handler:holidayManage.exportWin},");
				
				if(this.userView.hasTheFunction("27043"))
					menus.append("{text: '导出Excel',handler:holidayManage.exportData},");
				
				if(!holidayType.equalsIgnoreCase(bo.getLeaveTimeTypeUsedOverTime())
						&& this.userView.hasTheFunction("27043")) {
					String musterJson = bo.getKqMuster();
					if(musterJson.length() > 2)
						menus.append("{text: '导出名册',menu:" + musterJson + "},");
				}
				
				if(menus.toString().endsWith(","))
					menus.setLength(menus.length() - 1);
				
				menus.append("]");
	            
				this.getFormHM().put("menus", menus.toString());
				this.getFormHM().put("showFlag", String.valueOf(showFlag));
        	}
            
            // 高级花名册获取当前条件查询语句
			String whereSelectIN = "";
            if(flag.contains(",muster,")) {
            	String musterSubModuleId = (String) this.getFormHM().get("subModuleId");
	            if(StringUtils.isEmpty(musterSubModuleId)) 
	            	return;
            	TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(musterSubModuleId);
            	String querySql = null==catche ? "" : catche.getQuerySql();
            	querySql = StringUtils.isEmpty(querySql) ? "" : querySql;
            	querySql = querySql.replaceAll("myGridData.", "");
            	String filterSql = null==catche ? "" : catche.getFilterSql();
            	filterSql = StringUtils.isEmpty(filterSql) ? "" : filterSql;
            	whereSelectIN = querySql + filterSql;
            }
            
			// 涉及SQL注入直接放进userView里
            LazyDynaBean bean = KqPrivBo.getKqPrivCodeAndKind(this.userView);
            String code = (String)bean.get("code");
            String kind = (String)bean.get("kind");
            // 参照老假期管理获取高级花名册查询SQL语句方法
            String condition = bo.getCondition(code, kind, year, holidayType, whereSelectIN, this.getFormHM());
     		this.userView.getHm().put("kq_condition", "17`"+condition);
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
