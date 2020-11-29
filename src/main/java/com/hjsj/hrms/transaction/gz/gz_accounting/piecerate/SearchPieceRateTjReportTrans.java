package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查询计件薪资统计报表
 * @author tianye
 * @date 2013-4-10
 */
public class SearchPieceRateTjReportTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");
			String defId = requestMap.get("defId")==null?"":(String)requestMap.get("defId");
			requestMap.remove("defId");

			StringBuffer getAllPieceRateReportSql = new StringBuffer();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sort =  new StringBuffer();
		    getAllPieceRateReportSql.append("select defId,defName,SortId from hr_summarydef order by SortId");//根据序号排序时为了保存排序做准备
			this.frowset=dao.search(getAllPieceRateReportSql.toString());
			ArrayList reportList = new ArrayList();
			while(this.frowset.next()){
				CommonData report = new CommonData(this.frowset.getString("defId"),this.frowset.getString("defName"));
				sort.append(","+this.frowset.getString("SortId"));
				reportList.add(report);
			}
			this.getFormHM().put("defId", defId);
			this.getFormHM().put("reportList", reportList);
			this.getFormHM().put("sortId", "".equals(sort.toString())?"":sort.toString().substring(1));
			
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
		}

}
