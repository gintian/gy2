package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.PersonInfoBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;

public class OutExcelTrans extends IBusiness{
	
	private TreeMap changelist;
	private String[] showlist;
	private String showinfo;
	private String b0110;
	private String e0122;
	private String name;
	private String chg_id;
	
	
	public void execute() throws GeneralException {
	    chg_id = (String) this.getFormHM().get("chg_id");
	    if(StringUtils.isNotEmpty(chg_id))
            chg_id = PubFunc.decrypt(chg_id);
	    
	    PersonInfoBo bo = new PersonInfoBo(this.frameconn, chg_id, this.userView);
		this.changelist = PersonInfoBo.changelist;
		this.b0110 = this.getFormHM().get("b0110").toString();
		this.e0122 = this.getFormHM().get("e0122").toString();
		this.name = this.getFormHM().get("name").toString();
		String showflags = (String)this.getFormHM().get("showflags");
		this.showlist = showflags.split(",");
		this.showinfo = this.getFormHM().get("showinfo").toString();
		if("all".equalsIgnoreCase(showinfo))
		    this.changelist = bo.alllist();
		
		String filename = createExcel();
		filename = SafeCode.encode(PubFunc.encrypt(filename));
		this.getFormHM().put("filename", filename);
		
		
	}
	
	
	public String createExcel(){
		String filename = "approveinfo.xls";
		try(HSSFWorkbook workbook = new HSSFWorkbook()) {
			filename =this.name+ PubFunc.getStrg() + ".xls";//tiany 修改导出文件的名称保证每次导出的不相同（紧添该行代码）
			HSSFSheet sheet = workbook.createSheet("sheet1");
			HSSFRichTextString ss =null;
			HSSFRow row=null;
			HSSFCell cell=null;
			CellRangeAddress region= null;
			
			HSSFCellStyle stylealign = workbook.createCellStyle();
			stylealign.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直      
			stylealign.setAlignment(HorizontalAlignment.CENTER);// 水平  
			stylealign.setBorderTop(BorderStyle.THIN);
			stylealign.setBorderLeft(BorderStyle.THIN);
			stylealign.setBorderRight(BorderStyle.THIN);
			stylealign.setBorderBottom(BorderStyle.THIN);
			stylealign.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
			stylealign.setFillForegroundColor(HSSFColor.WHITE.index);
			
			
			HSSFCellStyle titlestyle = workbook.createCellStyle();
			titlestyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);   //((short)1);
			titlestyle.setAlignment(HorizontalAlignment.CENTER);// 水平  
			titlestyle.setBorderTop(BorderStyle.THIN);
			titlestyle.setBorderLeft(BorderStyle.THIN);
			titlestyle.setBorderRight(BorderStyle.THIN);
			titlestyle.setBorderBottom(BorderStyle.THIN);
			titlestyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);  
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short)11);
			f.setBold(true);
			titlestyle.setFont(f);
			
			
			HSSFCellStyle cs = workbook.createCellStyle();
			cs.setBorderLeft(BorderStyle.THIN);
			
			HSSFCellStyle changecs = workbook.createCellStyle();
			changecs.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			changecs.setAlignment(HorizontalAlignment.CENTER);// 水平 
			changecs.setBorderTop(BorderStyle.THIN);
			changecs.setBorderLeft(BorderStyle.THIN);
			changecs.setBorderRight(BorderStyle.THIN);
			changecs.setBorderBottom(BorderStyle.THIN);
			changecs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			HSSFCellStyle bordercs = workbook.createCellStyle();
			bordercs.setBorderTop(BorderStyle.THIN);
			
			short n=0;
			region = new CellRangeAddress(0,0,(short)0,(short)5);
			sheet.addMergedRegion(region);//指定合并区域
			row = sheet.createRow(n);
			cell = row.createCell(0);
			ss = new HSSFRichTextString(b0110+"/"+e0122+"/"+name);
			cell.setCellValue(ss);
			HSSFCellStyle hcs = workbook.createCellStyle();
			HSSFFont f1 = workbook.createFont();
			f1.setFontHeightInPoints((short)15);
			f1.setBold(true);
			hcs.setFont(f1);
			cell.setCellStyle(hcs);
			n=(short) (n+2);
			
			int showlistnum = 0;
			PersonInfoBo bo = new PersonInfoBo(this.frameconn, chg_id, this.userView);
			
            ArrayList itemList = new ArrayList();
            HashMap columnsMap = new HashMap();
            
			Iterator chite = changelist.entrySet().iterator();
			while(chite.hasNext()){
				Entry entry = (Entry)chite.next();
				String showflag = showlist[showlistnum].toString();
				showlistnum++;
				if("N".equals(showflag))
					continue;
				LazyDynaBean sadb = (LazyDynaBean)entry.getValue();
				String setid = sadb.get("setid").toString();
				String setname = sadb.get("setdesc").toString();
				LinkedHashMap columns= (LinkedHashMap)sadb.get("columns");
				ArrayList recordlist = (ArrayList)sadb.get("itemlist");
				ExportExcelUtil.mergeCell(sheet, n,(short)0,n,(short)2);
				row = sheet.createRow(n);
				cell = row.createCell(0);
				cell.setCellValue(setname);
				f1.setFontHeightInPoints((short)11);
				f1.setBold(true);
				hcs.setFont(f1);
				cell.setCellStyle(hcs);
				n++;
				Iterator cite;
				if(columns != null)
				    cite =  columns.keySet().iterator();
				else {
				    HashMap map = bo.fieldSetList(setid);
	                columnsMap = (HashMap) map.get("columns");
	                cite =  columnsMap.keySet().iterator();
	                itemList = (ArrayList) map.get("itemlist");
				}
				
				short columnN = 0;
				if("all".equals(this.showinfo) && "A01".equals(setid.toUpperCase())){}
				else{
					region = new CellRangeAddress(n,n,(short)0,(short)1);
					sheet.addMergedRegion(region);
					row = sheet.createRow(n);
					cell = row.createCell(columnN);
					cell.setCellValue("状态");
					cell.setCellStyle(titlestyle);
					setRegionStyle(sheet, region,titlestyle );
					columnN=(short) (columnN+2);
					
					while(cite.hasNext()){
						String key = cite.next().toString();
						String itemDesc = "";
						if(columns != null) {
						    FieldItem fi =  (FieldItem) columns.get(key);
						    itemDesc = fi.getItemdesc();
						} else { 
						    HashMap fi = (HashMap)columnsMap.get(key);
						    itemDesc = (String) fi.get("itemdesc");
						}
						
						cell = row.createCell(columnN);
						cell.setCellValue(itemDesc);
						cell.setCellStyle(titlestyle);
						sheet.setColumnWidth(columnN, 30*256);
						
						columnN++;
					}
					
					cell = row.createCell(columnN);
					cell.setCellValue("审批状态");
					cell.setCellStyle(titlestyle);
					sheet.setColumnWidth(columnN, 30*256);
					
					columnN++;
					cell = row.createCell(columnN);
					cell.setCellStyle(cs);
					n++;
				}
				
				Iterator recite;
				if(recordlist != null)
				    recite = recordlist.iterator();
				else if(itemList != null)
				    recite = itemList.iterator();
				else {
				    n++;
				    continue;
				}
				
				while(recite.hasNext()){
				    String type = "";
				    String sp_flag = "";
					LazyDynaBean rldb = new LazyDynaBean();
					HashMap rMap = new HashMap();
					if(recordlist != null) {
					    rldb = (LazyDynaBean)recite.next();
					    type = rldb.get("type").toString();
					    sp_flag = rldb.get("sp_flag").toString();
					} else {
					    rMap = (HashMap)recite.next();
					    type = (String) rMap.get("type");
					    sp_flag = (String) rMap.get("sp_flag");
					}
					
					columnN = 0;
					if("update".equals(type)){
						
						if("all".equals(this.showinfo) && "A01".equals(setid.toUpperCase())){
							n--;
							if(columns != null)
							    cite = columns.keySet().iterator();
							else
							    cite =  columnsMap.keySet().iterator();
							
							int numflag = 0;
							columnN = 0;
							region = null;
							while(cite.hasNext()){
								numflag++;
								if(numflag%3-1 == 0 ){
									n++;
									columnN=0;
									region = new CellRangeAddress(n,n,(short)0,(short)1);
									sheet.addMergedRegion(region);
									row = sheet.createRow(n);
								}
								
								String key = cite.next().toString();
								cell = row.createCell(columnN);
								if(region!=null){
									setRegionStyle(sheet, region, titlestyle);
								}
								
								String itemDesc = "";
		                        if(columns != null) {
		                            FieldItem fi =  (FieldItem) columns.get(key);
		                            itemDesc = fi.getItemdesc();
		                        } else { 
		                            HashMap fi = (HashMap)columnsMap.get(key);
		                            itemDesc = (String) fi.get("itemdesc");
		                        }
		                        
								cell.setCellValue(itemDesc);
								cell.setCellStyle(titlestyle);
								if(numflag%3-1 == 0 )
									columnN=1;
								
								columnN++;
								String newvalue = "";
			                    String changeflag = "";
			                    if(recordlist != null) {
			                        LazyDynaBean cldb = (LazyDynaBean)rldb.get(key);
			                        newvalue = cldb.get("newvalue").toString();
			                        changeflag = cldb.get("changeflag").toString();
			                    } else {
			                        HashMap cMap = (HashMap)rMap.get(key);
			                        newvalue = (String) cMap.get("newvalue");
			                        changeflag = (String) cMap.get("changeflag");
			                    }
								
								cell = row.createCell(columnN);
								cell.setCellValue(newvalue);
								if("Y".equals(changeflag))
									cell.setCellStyle(changecs);
								else
									cell.setCellStyle(stylealign);
								
								columnN++;
							}
							
							numflag =3-numflag%3; 
							int j=0;
							while(j<numflag&&numflag!=3){//tiany添加numflag!=3判断 整除3时不用处理
								cell = row.createCell(columnN);
								cell.setCellStyle(titlestyle);
								columnN++;
								cell = row.createCell(columnN);
								cell.setCellStyle(stylealign);
								columnN++;
								j++;
							}
							
							n++;
						}else{
							region = new CellRangeAddress(n,n+1,(short)0,(short)0);
							sheet.addMergedRegion(region);
							row = sheet.createRow(n);
							row.setHeightInPoints(15);
							cell = row.createCell(columnN);
							cell.setCellValue("修改");
							cell.setCellStyle(stylealign);
							setRegionStyle(sheet, region, stylealign);
							columnN++;
							
							cell = row.createCell(columnN);
							cell.setCellValue("变动前");
							cell.setCellStyle(stylealign);
							columnN++;
							if(columns != null)
                                cite = columns.keySet().iterator();
                            else
                                cite =  columnsMap.keySet().iterator();
							
							while(cite.hasNext()){
								String key = cite.next().toString();
								//	System.out.print(key+",");
								Map keymap = rldb.getMap();
								String value = " ";
								String changeflag = "N";
								if(recordlist != null) {
								    if(keymap.containsKey(key)){
	                                    LazyDynaBean cldb = (LazyDynaBean)rldb.get(key);
	                                    value = cldb.get("oldvalue").toString();
	                                    changeflag = cldb.get("changeflag").toString();
								    }
								} else {
								    HashMap cMap = (HashMap)rMap.get(key);
								    value = (String) cMap.get("oldvalue");
								    changeflag = (String) cMap.get("changeflag");
								}
								
								cell = row.createCell(columnN);
								cell.setCellValue(value);
								if("Y".equals(changeflag))
									cell.setCellStyle(changecs);
								else
									cell.setCellStyle(stylealign);
								
								columnN++;
							}
							
							region = new CellRangeAddress(n,n+1,columnN,columnN);
							sheet.addMergedRegion(region);
							String state="";
							if("01".equals(sp_flag))
								state = "起草";
							else if("02".equals(sp_flag))
								state = "已报批";
							else if("03".equals(sp_flag))
								state = "已批";
							else if("07".equals(sp_flag))
								state = "驳回";
							
							cell = row.createCell(columnN);
							cell.setCellValue(state);
							cell.setCellStyle(stylealign);
							setRegionStyle(sheet, region, stylealign);
							//为合并单元格添加边线
							row = sheet.createRow(n+2);
							cell = row.createCell(0);
							cell.setCellStyle(bordercs);
							cell = row.createCell(columnN);
							cell.setCellStyle(bordercs);
							n++;
							
							row = sheet.createRow(n);
							row.setHeightInPoints(15);
							columnN = 1;
							cell = row.createCell(columnN);
							cell.setCellValue("变动后");
							cell.setCellStyle(stylealign);
							
							columnN++;
							if(columns != null)
                                cite = columns.keySet().iterator();
                            else
                                cite =  columnsMap.keySet().iterator();
							
							while(cite.hasNext()){
								String key = cite.next().toString();
								Map keymap = rldb.getMap();
								String value = " ";
								String changeflag = "N";
								if(recordlist != null) {
								    if(keymap.containsKey(key)){
                                        LazyDynaBean cldb = (LazyDynaBean)rldb.get(key);
                                        value = cldb.get("newvalue").toString();
                                        changeflag = cldb.get("changeflag").toString();
								    }
								} else {
								    HashMap cMap = (HashMap)rMap.get(key);
								    value = (String) cMap.get("newvalue");
								    changeflag = (String) cMap.get("changeflag");
								}
								
								cell = row.createCell(columnN);
								cell.setCellValue(value);
								if("Y".equals(changeflag))
									cell.setCellStyle(changecs);
								else
									cell.setCellStyle(stylealign);
								
								columnN++;
							}
							
							cell = row.createCell(columnN+1);
							cell.setCellStyle(cs);
							n++;
						}
					}else{
						String state = "";
						if("new".equals(type))
							state = "新增";
						else if("delete".equals(type))
							state = "删除";
						else if("insert".equals(type))
							state = "插入";
						else if("select".equals(type))
							state = "未变动";
						
						region = new CellRangeAddress(n, n, (short)0, (short)1);
						sheet.addMergedRegion(region);
						row = sheet.createRow(n);
						row.setHeightInPoints(15);
						cell = row.createCell(columnN);
						cell.setCellValue(state);
						cell.setCellStyle(stylealign);
						setRegionStyle(sheet, region, stylealign);
						columnN=(short) (columnN+2);
						
						if(columns != null)
                            cite = columns.keySet().iterator();
                        else
                            cite =  columnsMap.keySet().iterator();
						
						while(cite.hasNext()){
							String key = cite.next().toString();
							//	System.out.print(key+",");
							String value = " ";
							if(recordlist != null) {
							    Map keymap = rldb.getMap();
							    if(keymap.containsKey(key)){
                                    LazyDynaBean cldb = (LazyDynaBean)rldb.get(key);
                                    value = cldb.get("newvalue").toString();
							    }
							} else {
							    HashMap cMap = (HashMap)rMap.get(key);
							    value = (String) cMap.get("newvalue");
							}
							
							cell = row.createCell(columnN);
							cell.setCellValue(value);
							cell.setCellStyle(stylealign);
							columnN++;
						}
						
						if("01".equals(sp_flag))
							state = "起草";
						else if("02".equals(sp_flag))
							state = "已报批";
						else if("03".equals(sp_flag))
							state = "已批";
						else if("07".equals(sp_flag))
							state = "驳回";
						
						cell = row.createCell(columnN);
						cell.setCellValue(state);
						cell.setCellStyle(stylealign);
						n++;
					}
				}
				n++;
			}

			try(FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename)) {
				workbook.write(fileOut);
			}
			sheet=null;
			System.gc();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return filename;
	}
	
	private void setRegionStyle(HSSFSheet sheet, CellRangeAddress region , HSSFCellStyle cs) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i ++) {
            HSSFRow row = sheet.getRow(i);
            if(row == null)
                row = sheet.createRow(i);
            
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                HSSFCell cell = row.getCell((short)j);
                if(cell == null)
                    cell = row.createCell((short)j);
                
                cell.setCellStyle(cs);
            }
        }
 }
}
