package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 职称评审_评委会_导出
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
public class ExportCommitteeDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		// 薪资类别编号
		String committee_id = (String)this.getFormHM().get("committee_id"); // 评委会编号
		committee_id = PubFunc.decrypt(committee_id);
		
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);//导出工具类
			
			/** 获取Excel文件名 */
			String fileName = "评委会.xls";
			
			/** 获取文件中sheet名 */
			String sheetName = "评委会";
			
			
			/** 获取Excel列头 */
			ArrayList<LazyDynaBean> excelHeadList = new ArrayList<LazyDynaBean>();
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("jobtitle_committee_00001");
			String exceptStr = ",committee_id,w0101,";//不需要的字段
			excelHeadList = committeeBo.getExpHeadList((ArrayList<ColumnsInfo>)catche.getTableColumns(), exceptStr);
			
			/** 获取Excel查询语句 */
			String expsql = catche.getTableSql();
			if(!StringUtils.isEmpty(catche.getQuerySql())){
				expsql += catche.getQuerySql();
			}
			
			/** 导出excel */
			excelUtil.exportExcelBySql(fileName, sheetName, null, excelHeadList, expsql, null, 0);
			
			this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
			
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
