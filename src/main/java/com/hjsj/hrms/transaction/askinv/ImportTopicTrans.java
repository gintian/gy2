package com.hjsj.hrms.transaction.askinv;


import com.hjsj.hrms.transaction.train.b_plan.MessBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class ImportTopicTrans extends IBusiness{

	public void execute() throws GeneralException {
		FormFile fileName = (FormFile)this.getFormHM().get("file");
			this.readExcel(fileName);	
	}
	
	ArrayList msg = new ArrayList();
	MessBean msb = null;
	public void readExcel(FormFile fileName) throws GeneralException{
		
		List l = new ArrayList();
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		HashMap questions = new HashMap();
		Workbook wb = null;
		Sheet sheet = null;
		String key = "";
		String names = "";
		ArrayList list = new ArrayList(); 
		ArrayList shortQuestions = new ArrayList();
		ArrayList queList = null;
		TreeMap treeMap =  new TreeMap();
		InputStream inputStream=null;
		try {
		    inputStream=fileName.getInputStream();
		wb = WorkbookFactory.create(inputStream);
		for( int k = 0; k  < wb.getNumberOfSheets(); k++){
			shortQuestions = null;
			shortQuestions = new ArrayList();
			questions = null ; 
			questions = new HashMap();
			queList = null;
			queList = new ArrayList();
			list= null;
			list = new ArrayList();
			sheet = wb.getSheetAt(k);
			
			Row row =  sheet.getRow(0);
			
			int rows = sheet.getPhysicalNumberOfRows(); //所有行
			if(null != row && !"".equalsIgnoreCase(row.getCell(0).getStringCellValue())){
			int cols = row.getPhysicalNumberOfCells();
			for(int i = 0; i < rows; i++){				//遍历所有行
				row = sheet.getRow(i);
				if(null != row){
					for(int j = 0; j < cols; j++){
						Cell cell = row.getCell(j);
						String test = "";
						if(null != cell){
							if(cell.getCellType() == 1){
								test = cell.getStringCellValue().trim();
							}else if(cell.getCellType() == 0 ){
								double dvalue = cell.getNumericCellValue();
								test = NumberFormat.getNumberInstance().format(dvalue);
							}
						}
						if(null != cell  && !"".equals(test.trim())){
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								break;
							case Cell.CELL_TYPE_STRING:
								String value = cell.getStringCellValue().trim();
								
								if(i<2){
									if(!"问卷名称".equals(value) &&!"填表说明".equals(value)){
										list.add(value);
									}
								}else{
									if("单选".equals(value)){
										queList = null;
										queList = new ArrayList();
										cell = row.getCell(j+1);
										value = cell.getStringCellValue().trim();
										String itemid = idg.getId("investigate_item.id");
										key = value.trim()+"0"+"|"+itemid;
										key = key.replace("\r", "");
										key = key.replace("\n", "");

										String name = "单选";
										names = "单选";
										checkKey(key, i,name);
									//	checkValue(value, i, name);
									}
									if("多选".equals(value)){
										queList = null;
										queList = new ArrayList();
										cell = row.getCell(j+1);
										value = cell.getStringCellValue().trim();
										String itemid = idg.getId("investigate_item.id");
										key = value+"2"+"|"+itemid;
										String name = "多选";
										names = "多选";
										checkKey(key, i,name);
										//checkValue(value, i, name);
									}
									if("简答".equals(value)){
										cell = row.getCell(j+1);
										value = cell.getStringCellValue().trim();
										String itemid = idg.getId("investigate_item.id");
										shortQuestions.add(value+"|"+itemid);
	
										String name = "简答";
										names = "简答";
										checkKey(value, i,name);
										
									}
								}
							default:
								break;
								}
						}else{
								row = sheet.getRow(i);
								cell = row.getCell(j+1);
								String value = "";
								if(cell.getCellType() == 1){
									value = cell.getStringCellValue().trim();
								}else if(cell.getCellType() == 0 ){
									double dvalue = cell.getNumericCellValue();
									value = NumberFormat.getNumberInstance().format(dvalue);
								}
								checkValue(value, i,names);
								queList.add(value);
								questions.put(key, queList);
								treeMap.put(key, queList);
						}
					}
				}
			}
			//questions 读取的题目和选项
			// list 前两行
			//shortQuestions 简答题
			Object [] maps = {list ,shortQuestions , questions};
			l.add(maps);
			
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("list", l);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}
        finally{
            PubFunc.closeResource(wb);
            PubFunc.closeResource(inputStream);
        }
	}
	
	/**
	 * 
	 * 检查题目名称是否过长
	 */
	public void checkKey(String key , int i ,String name){
		int count = 0 ;
		String regex = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regex);
		java.util.regex.Matcher m = p.matcher(key);
		while(m.find()){
			count ++; 										//题目中包含汉字的数量
		}
		int other = 0;
		if("简答".equals(name)){
			other = key.length();
		}else{
			other = key.length() - count ;
		}
		if(( (count * 2) + other )> 250){	//一个汉字等于两个字符
			String content = "&nbsp;&nbsp;&nbsp;&nbsp;"+(i+1) +".&nbsp;["+name+"]&nbsp;"+"名称过长 英语字符不超过250个 汉字不超过125个 请重新填写。";
			msb = new MessBean();
			msb.setContent(content);
			msg.add(msb);
		}
	}
	
	private void checkValue(String value , int i ,String names){
		int count = 0 ;
		String regex = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regex);
		java.util.regex.Matcher m = p.matcher(value);
		while(m.find()){
			count ++; 										//题目中包含汉字的数量
		}
		int other = 0;
		//if(name.equals("简答")){
		//	other = value.length();
		//}else{
			other = value.length() - count ;
		//}
		if(( (count * 2) + other )> 250){	//一个汉字等于两个字符
			String content = "&nbsp;&nbsp;&nbsp;&nbsp;"+(i+1) +".&nbsp;["+names+"]&nbsp;"+"选项过长 英语字符不超过250个 汉字不超过125个 请重新填写。";
			msb = new MessBean();
			msb.setContent(content);
			msg.add(msb);
		}
	}

}
