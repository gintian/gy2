package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;


import com.hjsj.hrms.businessobject.performance.objectiveManage.ExportScoreDetailsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.sun.star.uno.Exception;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
import java.util.List;

/**
 * 导出打分明细表
 * @author haosl
 */
public class ExportScoreDetailsTrans extends IBusiness {
	private static final long serialVersionUID = 1L;
	// 不可编辑指标
	public static String notEditFields = "";
	// 需要增加列宽的指标
	public static String isAddWidth = "";
	// 排除指标 : 
	public static String exceptFields = "";
	// 锁列
	public static String islock = "";
	
	public void execute() throws GeneralException {
		HashMap<String,String> hm = this.getFormHM();
		String planId = hm.get("planId");
		try {
			ExportScoreDetailsBo exportBo = new ExportScoreDetailsBo(this.getFrameconn(),this.getUserView());
			List<List<LazyDynaBean>> dataList = exportBo.getDataList(planId);
			if(dataList==null || dataList.size()==0){
				throw GeneralExceptionHandler.Handle(new Exception("暂无打分明细数据,无法导出！"));
			}
			exportBo.createExcel(dataList,"sheet");
			String filename = "打分明细_"+this.getUserView().getUserName()+".xls";
			exportBo.out2file(filename);
			this.getFormHM().put("filename",PubFunc.encrypt(filename));//表格名传进前台
		} catch (GeneralException e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
