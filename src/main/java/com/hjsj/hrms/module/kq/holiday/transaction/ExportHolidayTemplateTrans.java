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
import java.util.HashMap;
/**
 * 导出假期管理模板
 * @Title:        ExportHolidayTemplateTrans.java
 * @Description:  导出假期管理导入数据的模板的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:08:36
 * @author        chenxg
 * @version       1.0
 */
public class ExportHolidayTemplateTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            String holidayType = (String) this.getFormHM().get("holidayType");
            holidayType = StringUtils.isEmpty(holidayType) ? "" : PubFunc.decrypt(holidayType);
            TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
            
            HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
            String leaveTimeType = bo.getLeaveTimeTypeUsedOverTime();
            // 39214 下载导入模板时，不取栏目设置的列，取其几个固定列头(ArrayList<ColumnsInfo>) catche.getDisplayColumns().clone();
            ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>(); 
            String sql = "";
            if(!leaveTimeType.equalsIgnoreCase(holidayType)) {
            	// 下面调休假下载模板不需要SQL，故只在年假查询即可
            	sql = catche.getTableSql();
                String orderBy = catche.getSortSql();
                String querySql = catche.getQuerySql();
                String filterSql = catche.getFilterSql();
                
                if(StringUtils.isNotEmpty(querySql)) {
                	// 61507 同导出excel一样 需要同步修改
                	querySql = querySql.replaceAll("myGridData.", "");
                	sql += " " + querySql;
                }
                
                if(StringUtils.isNotEmpty(filterSql))
                    sql += " " + filterSql;
                
                if(StringUtils.isNotEmpty(orderBy))
                    sql += " " + orderBy;
                // 获取固定列头
                columnsList = bo.getExportTemplate("Q17");
                // 35043 可能出现合并列，故暂时取消考虑primaryKey是否是第一列指标，先加上以相应导入时校验的主键字段
                ColumnsInfo primaryKey = new ColumnsInfo();
                primaryKey.setColumnDesc("主键标识串");
                primaryKey.setColumnId("primaryKey");
                primaryKey.setColumnLength(30);
                primaryKey.setColumnType("A");
                primaryKey.setCodesetId("0");
                primaryKey.setColumnWidth(100);
                columnsList.add(0, primaryKey);
            } else {
            	// 调休假下载模板不需要下载之前的数据 这里把SQL是空串即可，不需赋值
            	// 获取固定列头
                columnsList = bo.getExportTemplate("Q33");
            }
            // 33538 linbz 增加模板下拉代码项
            HashMap<String, ArrayList<String>> codeitemMap = new HashMap<String, ArrayList<String>>();
            for(int i=0;i<columnsList.size();i++) {
            	ColumnsInfo column = (ColumnsInfo)columnsList.get(i);
            	String codesetid = column.getCodesetId();
            	if(!"0".equalsIgnoreCase(codesetid) && StringUtils.isNotEmpty(codesetid)){
            		String columnid = column.getColumnId();
            		ArrayList<String> desclist = bo.getCodeByDesc(codesetid);
            		codeitemMap.put(columnid, desclist);
            	}
            	
            }
            ArrayList<LazyDynaBean> list = bo.getTemplateHeadList(columnsList);
            // 导出工具类
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
            String fileName = this.userView.getUserName() +"_kq_holiday" +  ".xls";
            excelUtil.setHeadRowHeight((short) 600);
            excelUtil.exportExcelBySql(fileName, null, null, list, sql, codeitemMap, 0);

            this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
}
