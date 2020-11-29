package com.hjsj.hrms.transaction.train.report;

import com.hjsj.hrms.businessobject.train.report.TrainReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:ExportFileTrans.java</p>
 * <p>Description:培训报表导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-08-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ExportFileTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String type = (String) this.getFormHM().get("reportId");
	TrainReportBo bo = new TrainReportBo(this.getFrameconn(),type);
	ArrayList titles = bo.getTitle();
	//从服务器取得sql
	String strSql = (String) this.getUserView().getHm().get("key_train_sql1");
	strSql=PubFunc.keyWord_reback(strSql);
	String outName = bo.createExcel(titles, strSql,this.userView.getUserName());
	//outName = outName.replaceAll(".xls", "#");
	outName = PubFunc.encrypt(outName);
	this.getFormHM().put("outName", outName); 

    }

}
