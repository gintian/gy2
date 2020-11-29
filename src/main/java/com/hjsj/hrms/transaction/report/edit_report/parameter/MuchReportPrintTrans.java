package com.hjsj.hrms.transaction.report.edit_report.parameter;

import com.hjsj.hrms.businessobject.report.ReportExcelBo;
import com.hjsj.hrms.businessobject.report.ReportPrint;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;

public class MuchReportPrintTrans extends IBusiness {

	public MuchReportPrintTrans() {
		super();
	}

	public void execute() throws GeneralException {
	    FileOutputStream fos = null;
		try {
			ArrayList selectList = (ArrayList) getFormHM().get("selectedlist");
			
			/*if(selectList == null || selectList.size()==0){
				Exception e = new Exception("请选择要打印的报表！");
				throw GeneralExceptionHandler.Handle(e);
			}
			*/
		//	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String exportFashion=(String)this.getFormHM().get("exportFashion");  // 1:pdf  2:excel
			
			String username = (String)this.getFormHM().get("username");
			if(username==null|| "".equals(username)){
				username=this.userView.getUserName();
			}
			userView=new UserView(username, this.frameconn); 
			userView.canLogin();
			String operateObject = (String) getFormHM().get("operateObject");
			String unitcode = (String) getFormHM().get("unitcode");
		//	ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
			Connection con = this.getFrameconn();
			DbWizard dbWizard = new DbWizard(con);
			String fileName ="";
			if("1".equals(exportFashion))
			{
				fileName=username + "_" + PubFunc.getStrg() + ".pdf";
				String path = System.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator") + fileName;
				// path="c:\\1.pdf";
				fos = new FileOutputStream(path);
				Document doc = null;
				PdfWriter writer = null;
				for (int i = 0; i < selectList.size(); i++) {
					//RecordVo vo = (RecordVo) selectList.get(i);
					String tabid =(String)selectList.get(i);       //vo.getString("tabid");
					TnameBo bo = null;
					try {
						if("2".equals(operateObject.trim())) {
							boolean existTable = dbWizard.isExistTable("tt_" + tabid, false);
							if(!existTable) {
								throw new Exception("请先对"+tabid+"号报表进行报表汇总！");
							}
						}
						if ("1".equals(operateObject.trim())) {
							bo = new TnameBo(con, tabid, "", username, " ");
						} else {
							bo = new TnameBo(tabid, unitcode, username, "", con);
						}
					} catch (Exception ee) {
						throw GeneralExceptionHandler.Handle(ee);
					}
					bo.setUserview(userView);
					ReportPrint report_print = new ReportPrint(bo, con,
							operateObject);
					report_print.setUnitcode(unitcode);
					report_print.setDocument(doc);
					report_print.setWriter(writer);
					report_print.createPDF(fos);
					doc = report_print.getDocument();
					writer = report_print.getWriter();
					if (i == selectList.size() - 1) {
						report_print.getDocument().close();
					}
				}
				fileName = PubFunc.encrypt(fileName); //add by wangchaoqun on 2014-9-15 对下载文件加密
			}
			else if("2".equals(exportFashion))
			{
				/*ArrayList tabList=new ArrayList();
				for (int i = 0; i < selectList.size(); i++) {
					RecordVo vo = (RecordVo) selectList.get(i);
					String tabid = vo.getString("tabid");
					tabList.add(tabid);
				}*/
				ArrayList tabList=selectList;
				ReportExcelBo reb=new ReportExcelBo(username,this.getUserView(),unitcode,operateObject,this.getFrameconn());
				fileName=reb.batchExecutReportExcel(tabList);
				fileName=PubFunc.encrypt(fileName);  //add by wangchaoqun on 2014-9-15 加密文件路径
			}
			//
			getFormHM().put("path", fileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		    PubFunc.closeIoResource(fos);
		}
	}

}
