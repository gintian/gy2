package com.hjsj.hrms.module.muster.showmuster.transaction;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.general.muster.ExecutePdf;
import com.hjsj.hrms.module.muster.showmuster.businessobject.impl.ShowManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * 
 *
 * @Titile: ExecuteMusterPdfOrExcelTrans
 * @Description:简单花名册导出Pdf和Excel交易类
 * @Company:hjsj
 * @Create time: 2019年4月4日下午5:05:14
 * @author: Zhiyh
 * @version 1.0
 *
 */
public class ExportMusterTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			String outName="";
			String tabid = (String) this.getFormHM().get("tabid");
 			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("showMuster"+tabid);
			String showMusterSql = tableCache.getTableSql() + " where 1=1 ";
			String orderBySql= tableCache.getSortSql();
			String querysql = tableCache.getQuerySql();
			String filtersql = tableCache.getFilterSql();
			//showMusterSql="select * from ("+showMusterSql+ ") hmctable where 1=1 ";//用于查询导出
			if (!StringUtils.equals(querysql, "")&&!StringUtils.equals(querysql, null)) {
				showMusterSql+=querysql;
			}
			if (!StringUtils.equals(filtersql, "")&&!StringUtils.equals(filtersql,null)) {
				showMusterSql+= filtersql;
			}
			String moduleID = (String) this.getFormHM().get("moduleID");
			int totalCount = (Integer) this.getFormHM().get("totalCount");
			String flag = (String) this.getFormHM().get("flag");
			String musterType = (String) this.getFormHM().get("musterType");
			String itemids = (String) this.getFormHM().get("itemids");
			ShowManageServiceImpl showManageService = new ShowManageServiceImpl(this.frameconn,this.userView);
			String musterName = showManageService.getMusterName(tabid);
			HashMap<String, Object>  map = new HashMap<String, Object>();
			map.put("tabid", tabid);
			map.put("musterName", musterName);
			map.put("userName", userView.getUserName());
			map.put("showMusterSql", showMusterSql);
			map.put("orderBySql", orderBySql);
			map.put("moduleID", moduleID);
			map.put("totalCount", totalCount);
			map.put("musterType", musterType);
			map.put("itemids",itemids.toUpperCase());
			if ("1".equals(flag)) {//导出Pdf
				ExecutePdf executePdf=new ExecutePdf(this.getFrameconn(),tabid,userView);
				outName=executePdf.createPdf(this.getUserView().getUserName(),map);
			}else if ("2".equals(flag)) {//导出EXCEL
				ExecuteExcel executeExcel=new ExecuteExcel(this.getFrameconn());
				executeExcel.setUserview(this.getUserView());
				executeExcel.setTabid(tabid);
				outName = executeExcel.createExcel(this.getUserView().getUserName(),map);
			}
			outName=PubFunc.encrypt(outName);
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("outName",outName);
			//tableCache.clear();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
