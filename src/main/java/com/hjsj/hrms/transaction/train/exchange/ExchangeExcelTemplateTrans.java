package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExchangeExcelTemplateTrans extends IBusiness {

	/**
	 * 生成Excel导入奖品模版
	 */
	public void execute() throws GeneralException {
		String filename = this.userView.getUserName() + "_" +  PubFunc.getStrg() + ".xls";
		String url=System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
		//模版没必要每次都创建
		if(!new File(url+filename).exists())
			createExcelTemplate(url+filename);
		//xiexd 2014.09.24加密文件名
		filename = PubFunc.encrypt(filename);
		this.getFormHM().put("filename", filename);
	}
	
	/**
	 *创建Excel模版
	 */
	private void createExcelTemplate(String url){
		HSSFWorkbook wb = null;
		FileOutputStream fileOut = null;
		try {
			wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("奖品导入模版");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			int num=0;
			cell = row.createCell(num++);
			cell.setCellValue("奖品名称");
			cell = row.createCell(num++);
			cell.setCellValue("数量");
			cell = row.createCell(num++);
			cell.setCellValue("兑换积分");
			cell = row.createCell(num++);
			cell.setCellValue("奖品描述");

			fileOut = new FileOutputStream(url);
			wb.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}
	}
}