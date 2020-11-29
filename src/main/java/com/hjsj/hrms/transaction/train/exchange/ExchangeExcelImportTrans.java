package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ExchangeExcelImportTrans extends IBusiness {

	/**
	 * Excel导入奖品 LiWeichao
	 */
	public void execute() throws GeneralException {
		FormFile excelfile = (FormFile)this.getFormHM().get("excelfile");
		//xiexd 2014.09.24文件验证
		try {
			if(!FileTypeUtil.isFileTypeEqual(excelfile)){
				 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (excelfile.getFileSize() == 0) {
			return;
		}
		
		String mess="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		InputStream myxls = null;
		try {
			myxls = excelfile.getInputStream();
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0);//第一个工作薄
			HSSFRow row = null;
			HSSFCell cell = null;
			String value = "";
			ArrayList datalist = new ArrayList();
			
			String giftName =  sheet.getRow(0).getCell(0).getStringCellValue();
			String giftNum =  sheet.getRow(0).getCell(1).getStringCellValue();
			String exchangeNum =  sheet.getRow(0).getCell(2).getStringCellValue();
			String giftDesc =  sheet.getRow(0).getCell(3).getStringCellValue();
			
			if((!"奖品名称".equals(giftName)) || (!"数量".equals(giftNum)) || (!"兑换积分".equals(exchangeNum)) || (!"奖品描述".equals(giftDesc)) ){
				mess = "请使用下载的模板！";
				throw GeneralExceptionHandler.Handle(new Exception("error"));
			}
			
			Iterator it = sheet.iterator();
			while(it.hasNext()){
				row = (HSSFRow)it.next();
				if(row.getRowNum()==0)
					continue;//第一行不要
				ArrayList data = new ArrayList();
				cell = row.getCell(0);
				if(cell==null)
					continue;//奖品名称不能为空(为空该行奖品不导入)
				
				if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
					if(HSSFDateUtil.isCellDateFormatted(cell))
						value = DateUtils.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
					else
						value = String.valueOf((int)cell.getNumericCellValue());
				}else
					value = cell.getStringCellValue();
				if(value==null||value.trim().length()<1)
					continue;//奖品名称不能为空(为空该行奖品不导入)
				
				data.add(idg.getId("R57.R5701"));
				data.add(value );
				
				cell = row.getCell(1);
				if(cell==null)
					data.add("0");
				else{
					if(cell.getCellType()!=HSSFCell.CELL_TYPE_NUMERIC){
						//第row.getRowNum()+1行第二列(数量=cell.getStringCellValue())只能为整数。请修改后重新上传！
						mess = "Excel\u7B2C"+(row.getRowNum()+1)+"\u884C\u7B2C\u4E8C\u5217(\u6570\u91CF="+cell.getStringCellValue()+")\u53EA\u80FD\u4E3A\u6574\u6570\u3002\u8BF7\u4FEE\u6539\u540E\u91CD\u65B0\u4E0A\u4F20\uFF01";
						throw GeneralExceptionHandler.Handle(new Exception("error"));
					}
					data.add(String.valueOf((int)cell.getNumericCellValue()));
				}
				
				cell = row.getCell(2);
				if(cell==null)
					data.add("0");
				else{
					if(cell.getCellType()!=HSSFCell.CELL_TYPE_NUMERIC){
						//第row.getRowNum()+1行第二列(兑换积分=cell.getStringCellValue())只能为整数。请修改后重新上传！
						mess = "Excel\u7B2C"+(row.getRowNum()+1)+"\u884C\u7B2C\u4E8C\u5217(\u5151\u6362\u79EF\u5206="+cell.getStringCellValue()+")\u53EA\u80FD\u4E3A\u6574\u6570\u3002\u8BF7\u4FEE\u6539\u540E\u91CD\u65B0\u4E0A\u4F20\uFF01";
						throw GeneralExceptionHandler.Handle(new Exception("error"));
					}
					data.add(String.valueOf((int)cell.getNumericCellValue()));
				}
				
				cell = row.getCell(3);
				if(cell==null)
					data.add("");
				else{
					if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
						if(HSSFDateUtil.isCellDateFormatted(cell))
							value = DateUtils.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
						else
							value = String.valueOf((int)cell.getNumericCellValue());
					}else
						value = cell.getStringCellValue();
					data.add(value);
				}
				datalist.add(data);
			}
			
			String sql = "insert into r57(r5701,r5703,r5707,r5705,r5711,r5713,createtime,createuser) values(?,?,?,?,?,'01',"+Sql_switcher.dateValue(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"))+",'"+userView.getA0100()+"')";
			dao.batchUpdate(sql, datalist);
		} catch (Exception e) {
			//导入失败!请检查您的导入Excel文件内容是否正确，确认无误后重新上传。
			if(mess==null||mess.length()<1)
				mess = "\u5BFC\u5165\u5931\u8D25!\u8BF7\u68C0\u67E5\u60A8\u7684\u5BFC\u5165Excel\u6587\u4EF6\u5185\u5BB9\u662F\u5426\u6B63\u786E\uFF0C\u786E\u8BA4\u65E0\u8BEF\u540E\u91CD\u65B0\u4E0A\u4F20\u3002";
			throw GeneralExceptionHandler.Handle(new Exception(mess));
			//e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(myxls);
			//导入失败 或其他情况无法正常导入避免导致id_factory生成的id过大(可以忽略)
			idRollback(dao);
		}
	}
	
	private void idRollback(ContentDAO dao){
		String sql = "update id_factory set currentid=(select MAX(r5701) from r57) where sequence_name='R57.R5701'";
		try {
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}