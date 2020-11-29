package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFComment;
//import org.apache.poi.hssf.usermodel.HSSFRichTextString;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class InputExportTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String saveflag=(String)reqhm.get("saveflag");
		saveflag=saveflag!=null?saveflag:"";
		reqhm.remove("saveflag");
		
		String tablename=(String)reqhm.get("tablename");
		tablename=tablename!=null?tablename:"";
		reqhm.remove("tablename");
		
		String checkClose = "";
		String checkflag = "ok";
		
		if("save".equalsIgnoreCase(saveflag)){
			checkClose = "close";
			FormFile file=(FormFile)getFormHM().get("picturefile");
			InputStream stream = null;
			//HSSFWorkbook wb = null;
			Workbook wb = null;
			//HSSFSheet sheet = null;
			Sheet sheet = null;
			//HSSFRow row= null;
			Row row= null;
			//HSSFCell cell= null;
			Cell cell= null;
			//HSSFRichTextString hscomment = null;
			RichTextString hscomment = null;
			FieldItem fielditem = null;
			//HSSFComment comment = null;
			Comment comment = null;
			//HSSFRichTextString hsts = null;
			RichTextString hsts = null;
			StringBuffer sqlstr = new StringBuffer();

			ArrayList fieldlist = new ArrayList();
			ArrayList valuelist = new ArrayList();
			boolean cellflag = true;
			boolean rowflag = true;
			ArrayList itemlist = new ArrayList();
			try {
				stream = file.getInputStream();
				wb=WorkbookFactory.create(stream); 
				sheet=wb.getSheetAt(0); 
				row=sheet.getRow(0);
				int cellNum=0;
				int rowNum=1;
				
				
				sqlstr.append("update ");
				sqlstr.append(tablename);
				sqlstr.append(" set ");

				while(cellflag){
					if(row==null){
						cellflag = false;
						break;
					}
					cell = row.getCell((short)cellNum);//获取单元格子 
					
					if(cell==null){
						cellflag = false;
						break;
					}
					hsts = cell.getRichStringCellValue(); 	
					String msg=hsts.getString(); 
					if(msg==null||msg.trim().length()<1){
						cellflag = false;
						break;
					}
					comment = cell.getCellComment();
					if(comment!=null){
						hscomment = comment.getString();
						String itemid = hscomment.getString();
						if(!"id".equalsIgnoreCase(itemid)&&!"B0110".equalsIgnoreCase(itemid)
								&&!"E01A1".equalsIgnoreCase(itemid)&&!"E0122".equalsIgnoreCase(itemid)){
							if("2".equals(userView.analyseFieldPriv(itemid))){
								sqlstr.append(itemid);
								sqlstr.append("=?,");
								itemlist.add(itemid);
							}
						}
						fieldlist.add(itemid);
					}
					cellNum++;
				}
				String sql = sqlstr.toString().substring(0,sqlstr.toString().length()-1);
				sqlstr.setLength(0);
				sqlstr.append(sql);
				sqlstr.append(" where ");
				if("K".equalsIgnoreCase(tablename.substring(0,1))){
					sqlstr.append("E01A1=? and ");
					sqlstr.append("id=?");
					itemlist.add("E01A1");
					itemlist.add("id");
				}else{
					sqlstr.append("B0110=? and ");
					sqlstr.append("id=?");
					itemlist.add("B0110");
					itemlist.add("id");
				}

				while(rowflag){
					row=sheet.getRow(rowNum);
					String B0110 = "";
					String id = "";
					if(row==null){
						rowflag = false;
						break;
					}
					ArrayList volist = new ArrayList();
					int a=0;
					if(fieldlist.size()<1){
						rowNum++;
						continue;
					}
					for(int i=0;i<fieldlist.size();i++){
						String itemid = (String)fieldlist.get(i);
						cell = row.getCell((short)a);//获取单元格子 
						if(cell==null){
							break;
						}
						if("K".equalsIgnoreCase(tablename.substring(0,1))){
							if(!"E01A1".equalsIgnoreCase(itemid)
									&&!"B0110".equalsIgnoreCase(itemid)
									&&!"E0122".equalsIgnoreCase(itemid)
									&&!"id".equalsIgnoreCase(itemid)){
								if(!"2".equals(userView.analyseFieldPriv(itemid))){
									a++;
									continue;
								}
							}
						}else{
							if(!"B0110".equalsIgnoreCase(itemid)
									&&!"id".equalsIgnoreCase(itemid)){
								if(!"2".equals(userView.analyseFieldPriv(itemid))){
									a++;
									continue;
								}
							}
						}
						
						if("id".equalsIgnoreCase(itemid)){
							hsts = cell.getRichStringCellValue(); 
							String values = hsts.getString();
							id = values;
							a++;
							continue;
						}
						fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							if("A".equalsIgnoreCase(fielditem.getItemtype())){
								String values = "";
								if(fielditem.isCode()){
									comment = cell.getCellComment();
									if(comment!=null){
										hscomment = comment.getString();
										values = hscomment.getString();
									}
									if("K".equalsIgnoreCase(tablename.substring(0,1))){
										if("E01A1".equalsIgnoreCase(fielditem.getItemid())){
											B0110 = values;
											a++;
											continue;
										}else if("E0122".equalsIgnoreCase(fielditem.getItemid())){
											a++;
											continue;
										}else if("B0110".equalsIgnoreCase(fielditem.getItemid())){
											a++;
											continue;
										}
									}else{
										if("B0110".equalsIgnoreCase(fielditem.getItemid())){
											B0110 = values;
											a++;
											continue;
										}
									}
								}else{
									hsts = cell.getRichStringCellValue(); 
									values = hsts.getString();
								}
								volist.add(values);
							}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
								if(fielditem.getDecimalwidth()>0){
									double dvalues = 0;
									try{
										dvalues = cell.getNumericCellValue();
									}catch(Exception e){
										dvalues = 0;
									}
									//update by wangchaoqun on 2014-10-9  list中加入double类型而非String类型，在插入数据库时科学计数法数据将不会报异常
									volist.add(Double.valueOf(dvalues));  
								}else{
									String nvalue = "0";
									double dvalues = 0;
									try{
										dvalues = cell.getNumericCellValue(); 
										nvalue = ((int)dvalues+"");
									}catch(Exception e){
										try{
											hsts = cell.getRichStringCellValue(); 
											nvalue = hsts.getString();
										}catch(Exception ex){
											nvalue = "0";
										}
									}
									volist.add(nvalue);
								}
							}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
								hsts = cell.getRichStringCellValue();
								String values = hsts.getString();
								values=values!=null&&values.length()==0?null:values;
								volist.add(values);
							}else{
								hsts = cell.getRichStringCellValue(); 
								String values = hsts.getString();
								volist.add(values);
							}
						}else{
							hsts = cell.getRichStringCellValue(); 
							String values = hsts.getString();
							volist.add(values);
						}
						a++;
						
					}
					volist.add(B0110);
					volist.add(id);
					valuelist.add(volist);
					rowNum++;
				}
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.batchUpdate(sqlstr.toString(), valuelist);
			}catch (Exception e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				checkflag = "导入的数据错误,请确认后再导入!";
				checkClose = "alert";
			}finally{
				PubFunc.closeResource(wb);
				PubFunc.closeResource(stream);
			}
			this.getFormHM().put("tablename", tablename);
			if(valuelist.size()<1){
				checkflag = "导入的数据错误,请确认后再导入!";
				checkClose = "alert";
			}

		}
		this.getFormHM().put("checkClose", checkClose);
		this.getFormHM().put("checkflag", checkflag);
	}
}
