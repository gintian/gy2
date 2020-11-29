package com.hjsj.hrms.transaction.general.kanban;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 输出我的日志excel
 * @author xuj 2009-11-2
 *
 */
public class MyExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		String state = (String)this.getFormHM().get("state");
		ArrayList infolist = new ArrayList();
		ArrayList column = new ArrayList();
		ArrayList columnlist = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if("0".equals(state)){//日报
				String ymd = (String)this.getFormHM().get("ymd");
				String sql = null;
				ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
				WorkdiarySQLStr wss=new WorkdiarySQLStr();
				wss.checkState(this.frameconn);
				UserView uv=this.getUserView();
				//过滤掉页面不显示内容
				ArrayList filterlist=this.filteritem(fieldlist);
				WeekUtils weekutils = new WeekUtils();
				String startime="";
				String endtime="";	
				if("3".equals(ymd)){//按时间段查询
					startime=(String)this.getFormHM().get("startime");
					endtime=(String)this.getFormHM().get("endtime");
				}else{
					String monthnum1 = (String)this.getFormHM().get("monthnum");
					String yearnum1 = (String)this.getFormHM().get("yearnum");
					int yearnum =yearnum1!=null&&yearnum1.length()>3?Integer.parseInt(yearnum1):Calendar.getInstance().get(Calendar.YEAR); 
					int monthnum = monthnum1!=null?Integer.parseInt(monthnum1):Calendar.getInstance().get(Calendar.MONTH)+1;
					String monthstr = monthnum+"";
					if(monthnum<10)
						monthstr = "0"+monthnum;
					startime=yearnum+"-"+monthstr+"-01";
					endtime=weekutils.lastMonthStr(yearnum,monthnum);	
				}
				
				sql = wss.getMyworkdiaryshow1(uv.getA0100(),filterlist,startime,endtime,state);
				this.frecset = dao.search(sql);
				
				for(int i=0;i<filterlist.size();i++){
					FieldItem field=(FieldItem) filterlist.get(i);
					column.add(field.getItemdesc());
					columnlist.add(field.getItemid());
				}
				while(this.frecset.next()){
					LazyDynaBean bean = new LazyDynaBean();
					for(int i=0;i<filterlist.size();i++){
						FieldItem field=(FieldItem) filterlist.get(i);
						if(field.getCodesetid()!=null&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
							bean.set(field.getItemid(), AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid()))));
						}else{
							if("D".equalsIgnoreCase(field.getItemtype())){
								Date date = this.frecset.getDate((field.getItemid()));
								if(date!=null){
									bean.set(field.getItemid(), sdf.format(date));
								}else{
									bean.set(field.getItemid(), "");
								}
							}else if("N".equalsIgnoreCase(field.getItemtype())) {// 防止出现excel0E8的情况
								double f = this.frecset.getDouble(field.getItemid());
//								if (f == 0) {
//									bean.set(field.getItemid(), "0.0"); 
//								} else {
									bean.set(field.getItemid(), "N:---"+f);
//								}
							} else {
								bean.set(field.getItemid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid())));
							}
						}
							
					}
					infolist.add(bean);
				}
				
			}else{
				ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
				//		过滤掉叶面不显示内容
				ArrayList filterlist=this.filteritem1(fieldlist);

				WeekUtils weekUtils = new WeekUtils();
				GregorianCalendar  calendar = weekUtils.numWeekcal(7);
				HashMap hm=this.getFormHM();
				String yearnum=(String)hm.get("yearnum");
				yearnum=yearnum!=null&&yearnum.length()>3?yearnum:(calendar.get(Calendar.YEAR))+""; 

				String monthnum=(String)hm.get("monthnum");
				monthnum=monthnum!=null?monthnum:(calendar.get(Calendar.MONTH)+1)+"";
				column.add(ResourceFactory.getProperty("label.serialnumber"));
				columnlist.add("weekday");
				
				for(int i=0;i<filterlist.size();i++){
					FieldItem field=(FieldItem) filterlist.get(i);
					column.add(field.getItemdesc());
					columnlist.add(field.getItemid());
				}
				if("1".equals(state)){//周报
					StringBuffer tablebuf = new StringBuffer();
					WorkdiarySQLStr wss=new WorkdiarySQLStr();
					wss.checkState(this.frameconn);
					UserView uv=this.getUserView();
					String weekstr[] = {ResourceFactory.getProperty("performance.workdiary.one.week"),
										ResourceFactory.getProperty("performance.workdiary.two.week"),
										ResourceFactory.getProperty("performance.workdiary.three.week"),
										ResourceFactory.getProperty("performance.workdiary.four.week"),
										ResourceFactory.getProperty("performance.workdiary.five.week"),
										ResourceFactory.getProperty("performance.workdiary.six.week")}; 
					WeekUtils weekutils = new WeekUtils();
					
					int totalweek = weekutils.totalWeek(Integer.parseInt(yearnum),Integer.parseInt(monthnum));
					for(int i=1;i<=totalweek;i++){
						
							Date startdate = weekutils.numWeek(Integer.parseInt(yearnum),Integer.parseInt(monthnum),i,1);
							Date enddate = weekutils.numWeek(Integer.parseInt(yearnum),Integer.parseInt(monthnum),i,7);
							String startime = weekutils.dateTostr(startdate);
							String endtime = weekutils.dateTostr(enddate);
							String sql=wss.getMyworkdiaryshow1(uv.getA0100(),fieldlist,startime,endtime,"1");	
							this.frecset = dao.search(sql);
							LazyDynaBean bean = bean = new LazyDynaBean();
							if(this.frecset.next()){
								bean.set("weekday", weekstr[i-1]);
								for(int n=0;n<filterlist.size();n++){
									FieldItem field=(FieldItem) filterlist.get(n);
									if(field.getCodesetid()!=null&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
										bean.set(field.getItemid(), AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid()))));
									}else{
										if("D".equalsIgnoreCase(field.getItemtype())){
											Date date = this.frecset.getDate((field.getItemid()));
											if(date!=null){
												bean.set(field.getItemid(), sdf.format(date));
											}else{
												bean.set(field.getItemid(), "");
											}
										}else
											bean.set(field.getItemid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid())));
									}
								}
								
							}else{
								bean.set("weekday", weekstr[i-1]);
								for(int j=0;j<fieldlist.size();j++){
									FieldItem tempitem=(FieldItem)fieldlist.get(j);
									if(!"p0100".equals(tempitem.getItemid())){
										if("p0115".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),ResourceFactory.getProperty("edit_report.status.wt"));
										}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),startime);
										}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),endtime);
										}else{
											bean.set(tempitem.getItemid(),"");
										}
									}
									
								}
							}
							infolist.add(bean);
					}
				}else if("2".equals(state)){//月报
					StringBuffer tablebuf = new StringBuffer();
					WorkdiarySQLStr wss=new WorkdiarySQLStr();
					wss.checkState(this.frameconn);
					UserView uv=this.getUserView();
					for(int i=0;i<fieldlist.size();i++){
						FieldItem tempitem=(FieldItem)fieldlist.get(i);
						
					}
					
					WeekUtils weekutils = new WeekUtils();
					for(int i=1;i<=12;i++){
						
							Date enddate = weekutils.lastMonth(Integer.parseInt(yearnum),i);
							
							String startime = i>9?yearnum+"-"+i+"-01":yearnum+"-0"+i+"-01";
							String endtime = weekutils.dateTostr(enddate);
							
							String sql=wss.getMyworkdiaryshow1(uv.getA0100(),fieldlist,startime,endtime,"2");
							this.frecset = dao.search(sql);
							LazyDynaBean bean = bean = new LazyDynaBean();
							if(this.frecset.next()){
								
								bean.set("weekday", i+ResourceFactory.getProperty("datestyle.month"));
								for(int n=0;n<filterlist.size();n++){
									FieldItem field=(FieldItem) filterlist.get(n);
									if(field.getCodesetid()!=null&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
										bean.set(field.getItemid(), AdminCode.getCodeName(field.getCodesetid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid()))));
									}else{
										if("D".equalsIgnoreCase(field.getItemtype())){//日期型
											Date date = this.frecset.getDate((field.getItemid()));
											if(date!=null){
												bean.set(field.getItemid(), sdf.format(date));
											}else{
												bean.set(field.getItemid(), "");
											}
										}else
											bean.set(field.getItemid(), PubFunc.nullToStr(this.frecset.getString(field.getItemid())));
									}
								}
								
							}else{
								bean.set("weekday", i+ResourceFactory.getProperty("datestyle.month"));
								for(int j=0;j<fieldlist.size();j++){
									FieldItem tempitem=(FieldItem)fieldlist.get(j);
									if(!"p0100".equals(tempitem.getItemid())){
										if("p0115".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),ResourceFactory.getProperty("edit_report.status.wt"));
										}else if("p0104".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),startime);
										}else if("p0106".equalsIgnoreCase(tempitem.getItemid())){
											bean.set(tempitem.getItemid(),endtime);
										}else{
											bean.set(tempitem.getItemid(),"");
										}
									}
								}
							}
							infolist.add(bean);
					
					} //for loop end
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("filename", this.creatExcel(column, infolist, columnlist,state));
		}
	}

	/**
	 * @param fieldlist
	 * @return
	 */
	public ArrayList filteritem(ArrayList fieldlist ){
		ArrayList fieldlist1=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append("p0100");
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(buf.indexOf(field.getItemid().toLowerCase())!=-1)
			{
				//fieldlist1.add(field);
				continue;
			}
			if("nbase".equalsIgnoreCase(field.getItemid()))
				continue;
			if("A0101".equalsIgnoreCase(field.getItemid())|| "E0122".equalsIgnoreCase(field.getItemid())|| "E01A1".equalsIgnoreCase(field.getItemid()))
				continue;
			if("p0115".equalsIgnoreCase(field.getItemid())){
				fieldlist1.add(field);
			}else 
				if(field.isVisible())
					fieldlist1.add(field);
		}
		return fieldlist1;		
	}
	
	public ArrayList filteritem1(ArrayList fieldlist ){
		ArrayList item=new ArrayList();
		item.add("b0110");
		item.add("e0122");
		item.add("e01a1");
		item.add("nbase");
		item.add("a0100");
		item.add("a0101");
		item.add("p0107");
		item.add("p0108");
		item.add("p0100");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			for(int j=0;j<item.size();j++){
				String itemid=(String) item.get(j);
				if(field.getItemid().equals(itemid)){
					fieldlist.remove(i);
					i--;
				}				
			}
		}
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			String tempstr=tempitem.getItemid();
			if("p0115".equals(tempstr)){
				fieldlist.add(0,tempitem);
				fieldlist.remove(i+1);
			}
		}
		return fieldlist;
		
	}
	/**
	 * 
	 * @param columnexcel表头
	 * @param infolist数据
	 * @param columnlist数据库中字段名
	 * @return
	 */
	public String creatExcel(ArrayList column,ArrayList infolist,ArrayList columnlist,String state)
	{
		FileOutputStream fileOut=null;
		HSSFWorkbook workbook=null;
		String excel_filename=this.userView.getUserName()+"_LogFile.xls";//this.userView.getUserName()+"file.xls";
		try {
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			font.setFontHeightInPoints((short)11);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
			
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size()-1)));//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = null;
			if("0".equals(state)){
				ss = new HSSFRichTextString("日报");
			}else if("1".equals(state)){
				ss = new HSSFRichTextString("周报");
			}else if("2".equals(state)){
				ss = new HSSFRichTextString("月报");
			}
			csCell.setCellValue(ss);
			n++;
			
			row=sheet.createRow(n);
			csCell =row.createCell((short)0);
			font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			csCell.setCellStyle(cellStyle);
			//ss = new HSSFRichTextString("序号");
			//csCell.setCellValue(ss);
			LazyDynaBean bean = new LazyDynaBean();
			
			font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			for(int i=0;i<column.size();i++){
				String name = (String)column.get(i);
				csCell = row.createCell((short)(i));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(name);
				csCell.setCellValue(ss);
			}
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<infolist.size();i++){
				row=sheet.createRow(n+i+1);
//			csCell =row.createCell((short)(0));
//			csCell.setCellStyle(cellStyle);
//			ss = new HSSFRichTextString(String.valueOf(i+1));
//			csCell.setCellValue(ss);
				bean = (LazyDynaBean)infolist.get(i);
				for(int j=0;j<columnlist.size();j++){
					csCell =row.createCell((short)(j));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					name=name==null?"":name;
					if(name.startsWith("N:---")){
						double ff=Double.parseDouble(name.substring(5));
						csCell.setCellValue(ff);
					}else{
						ss = new HSSFRichTextString(name);
						csCell.setCellValue(ss);
					}
				}
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
			fileOut.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		excel_filename = PubFunc.encrypt(excel_filename);

		return excel_filename;
	}
	
	
}
