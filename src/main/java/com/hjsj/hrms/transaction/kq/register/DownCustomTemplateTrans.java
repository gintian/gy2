package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.util.HashMap;


public class DownCustomTemplateTrans extends IBusiness {
	public void execute() throws GeneralException {
//		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
//		if (hm.get("indexId") != null && hm.get("indexName") != null) {
//			this.getFormHM().put("indexId", hm.get("indexId"));
//			this.getFormHM().put("indexName",hm.get("indexName"));
//			hm.remove("indexId");
//			hm.remove("indexName");
//		}
//		此方法是获取action请求连接里附加的参数
		String indexId=(String)this.getFormHM().get("indexId");
		String indexName=(String)this.getFormHM().get("indexName");
		
		String Id [] = indexId.split("\\.");
		String Name [] = indexName.split("\\.");
		// 创建新的Excel 工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			HSSFSheet sheet = wb.createSheet();

			HSSFFont font = wb.createFont();//设置字体
			font.setFontHeightInPoints((short) 10);//字体高度

			HSSFRow row = sheet.createRow((short) 0);
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFComment comm = null;
			//获取工号指标代号
			ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
			String userOrgId = managePrivCode.getPrivOrgId();
			KqParameter para = new KqParameter(this.getFormHM(), this.userView, "UN" + userOrgId, this.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String g_no = ((String) hashmap.get("g_no")).toLowerCase();
			//插入固定列
			HSSFCell cell1 = row.createCell((short) 0);
			cell1.setCellValue("日期");
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,
					(short) (2), 0, (short) (3), 1));
			comm.setString(new HSSFRichTextString("q03z0"));
			cell1.setCellComment(comm);
			HSSFCell cell2 = row.createCell((short) 1);
			cell2.setCellValue("工号");
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,
					(short) (3), 0, (short) (4), 1));
			comm.setString(new HSSFRichTextString(g_no));
			cell2.setCellComment(comm);
			//设置工号列为文本格式
			for (int q = 1; q <= 99; q++) {
				HSSFRow rows = sheet.createRow((short) q);
				HSSFCell cell = rows.createCell((short) 1);
				HSSFCellStyle cellStyle2 = wb.createCellStyle();
				HSSFDataFormat format = wb.createDataFormat();
				cellStyle2.setDataFormat(format.getFormat("@"));
				cell.setCellStyle(cellStyle2);
			}
			HSSFCell cell3 = row.createCell((short) 2);
			cell3.setCellValue("姓名");
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,
					(short) (4), 0, (short) (5), 1));
			comm.setString(new HSSFRichTextString("a0101"));
			cell3.setCellComment(comm);
			//插入选择的指标项
			int j = 3;
			for (int k = 0; k < Name.length; k++) {
				HSSFCell cell = row.createCell((short) k + 3);
				cell.setCellValue(Name[k]);
				//加标注
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,
						(short) (j + 2), 0, (short) (j + 3), 1));
				comm.setString(new HSSFRichTextString(Id[k]));
				cell.setCellComment(comm);

			}
			// 59250   统一命名为： 登陆用户_相应信息
			String outName = this.userView.getUserName() + "_" + ResourceFactory.getProperty("kq.init.staff").trim() + ".xls";
			try {
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			//outName = outName.replace(".xls", "#");//xiexd 2014.09.12 对下载的文件名进行加密
			outName = PubFunc.encrypt(outName);
			getFormHM().put("outName", SafeCode.decode(outName));
		}finally {
			PubFunc.closeResource(wb);
		}
	}
}


