package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 假期管理导出数据
 * @Title:        ExportHolidayDataTrans.java
 * @Description:  导出假期管理数据时调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:02:31
 * @author        chenxg
 * @version       1.0
 */
public class ExportHolidayDataTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
            
            HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
            ArrayList<LazyDynaBean> mergedCellList = bo.getExcleMergedList(catche.getDisplayColumns());
            ArrayList<ColumnsInfo> columnsList = catche.getDisplayColumns();
            ArrayList<LazyDynaBean> list = bo.getHeadList(columnsList, mergedCellList, false , 0);
            String holidayType = (String) this.getFormHM().get("holidayType");
            holidayType = PubFunc.decrypt(holidayType);
            String leaveTime = bo.getLeaveTimeTypeUsedOverTime();
            
            String sql = catche.getTableSql();
            String orderBy = catche.getSortSql();
            String querySql = catche.getQuerySql();
            String filterSql = catche.getFilterSql();
            
            if(StringUtils.isNotEmpty(querySql)) {
            	// 33194 在有条件查询时，拼接条件SQL会带有myGridData.比较字段，该字段是由GetTableDataTrans表格控件固定的标识而来，但是导出时并不需要，故替换为空
            	querySql = querySql.replaceAll("myGridData.", "");
            	sql += " " + querySql;
            }
            // 增加部门过滤条件的 导出调休假时单独处理
            if(StringUtils.isNotEmpty(filterSql) && !holidayType.equalsIgnoreCase(leaveTime))
            	sql += " " + filterSql;
            
            if(StringUtils.isNotEmpty(orderBy))
                sql += " " + orderBy;
            
            // 导出工具类
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
            ArrayList<String> selectDatas = (ArrayList<String>) this.getFormHM().get("selectDatas");
            
            int headStartRowNum = 0;
            if(!mergedCellList.isEmpty())
                headStartRowNum = 1;
            
            String fileName = this.userView.getUserName() +"_kq_holiday" +  ".xls";
            if(selectDatas.isEmpty()) {
            	// 如果导出调休假是根据部门过滤条件的 需特殊处理SQL
            	if(StringUtils.isNotEmpty(filterSql) && holidayType.equalsIgnoreCase(leaveTime)) {
            		if(sql.indexOf("group by") > -1) {
            			String groupBySql = "";
    	                groupBySql = sql.substring(sql.indexOf("group by"));
    	                sql = sql.substring(0, sql.indexOf("group by")) + " where 1=1 ";
    	                // 增加部门等过滤条件
    	                sql += " " + filterSql;
    	                // 补上刚刚截取的group by语句
    	                sql += " " + groupBySql;
                	}
            	}
                excelUtil.exportExcelBySql(fileName, null, mergedCellList, list, sql, null, headStartRowNum);
            } else {
                String sqlStr = "";
                // 调休假与其他类别假选择数据导出时 校验错误
                if(holidayType.equalsIgnoreCase(leaveTime))
                    sqlStr = bo.getExprotLeaveDatasSql(sql, selectDatas, list);
                else
                    sqlStr = bo.getExprotSql(selectDatas, list);
                
                ArrayList<LazyDynaBean> dateList = excelUtil.getExportData(list, sqlStr);
                excelUtil.exportExcel(fileName, null, mergedCellList, list, dateList, null, headStartRowNum);
            }
            
            this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
