package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * <p>
 * Title:KqUnitEXCELDailyBo
 * </p>
 * <p>
 * Description:生成excel考勤簿
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-05-17
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class KqUnitEXCELDailyBo {
	
	private Connection conn = null;
	//首钢增加 true展现本月出缺勤情况统计小计，否则原始
	private boolean kqtablejudge; 
	// 当前页面设置
	private ReportParseVo parsevo = null;
	// 登录用户
	private UserView userView = null;
	// 报表id
	private HashMap codemap = new HashMap();
	
	// 最大列数
	private int maxColnum = 17;
	// excel workbook
	// 行数
	private int rowNum = 0;
	
	// 工作表
	private HSSFSheet sheet;
	
	private HSSFWorkbook workbook;
	
	HashMap styleMap = new HashMap();
	
	private String curTab = ""; 
	
	public KqUnitEXCELDailyBo(Connection conn) {
		this.conn = conn;		
	}
	
	public KqUnitEXCELDailyBo(Connection conn,UserView userView) {
		this.conn = conn;	
		this.userView = userView;
	}
		
	/**
	 * 生成excel考勤簿
	 * @param a0100 String 人员编号
	 * @param kq_duration String 考勤期间
	 * @param ReportParseVo String 当前页面设置对象
	 * @param userView UserView 用户
	 * @param nbase String 人员库
	 * @param name String 姓名
	 * @return String excle文件名
	 * @throws GeneralException
	 */
	public String getKqReportExcel(String a0100, String kq_duration, 
				ReportParseVo parsevo, String nbase, 
				String name, String reportId)throws GeneralException{
		KqViewDailyBo kqviewdailybo = new KqViewDailyBo();
		String filename =  "";
		// 创建excle
		workbook = new HSSFWorkbook();
		// 创建工作薄
		sheet = workbook.createSheet(name + "签到簿");
		
		try {
			//考勤表：true=展现本月出缺勤情况统计小计 false=不展现
			this.kqtablejudge = kqviewdailybo.getkqtablejudge(this.conn);  
			this.parsevo = parsevo;
			 
			//获得考勤项目参数,底部的符号；
			KqReportInit kqReprotInit = new KqReportInit(this.conn);			
//			ArrayList item_list = kqReprotInit.getKq_Item_list();
			ArrayList item_list = kqReprotInit.getKq_Item_listPdf();
			//日期生成表头
			ArrayList datelist = getDateList(this.conn, kq_duration); 
			if (datelist.size() < 31) {
				maxColnum = 16;
			}
			// 20160726 linbz 20506导出符号颜色问题，需公用一个ExecuteKqDailyExcel对象
			ExecuteKqDailyExcel dailyExcel = new ExecuteKqDailyExcel(this.conn);
			//得到表头
			getTableTitle(name);  
			String width=getFactWidth();  //宽度
			 
			getTableHead(a0100,kq_duration,datelist,nbase);
			
			//body信息	
			getBodyExcel(a0100,nbase,name,datelist,kqtablejudge,item_list,kq_duration,dailyExcel);
			 
			//得到表尾数据
			getTileHtml(item_list,dailyExcel);

			String context = "";
			if (parsevo.getTitle_fw() != null && parsevo.getTitle_fw().length() > 0) {
				context = parsevo.getTitle_fw();
			} else {
				context = parsevo.getName();
			}
			filename = userView.getUserName()+ "_" + context.trim() + ".xls";
			
			// 将文件保存到临时文件夹中
			FileOutputStream fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") 
					+ filename);
			
			// 将数据写到excel中
			workbook.write(fileOut);
			fileOut.close();
		 
		} catch(Exception e) {
			 e.printStackTrace();
		}
		 
		 return filename;
	}
	
	/**
	 * 得到标题内容
	 * 
	 * */	 
	private String getTableTitle(String username) {
		// 标题
		String title = "";
		try {
			// 宽度
			String width = getFactWidth();
			// 高度
			String height = getPxFormMm(parsevo.getTitle_h());
			ExecuteKqDailyExcel daily = new ExecuteKqDailyExcel();
			// 创建标题的字体
			HSSFFont titleFont = daily.getFont(parsevo.getTitle_fn(), 
					parsevo.getTitle_fb(), parsevo.getTitle_fi(), 
					parsevo.getTitle_fu(), parsevo.getTitle_fz(), 
					null, workbook);
			
			if(parsevo.getTitle_fw() == null || parsevo.getTitle_fw().length() <= 0) {
				title = username + "签到簿";
			}else
			{
				title = parsevo.getTitle_fw();
			}
			
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow row = getRow(rowNum);
			HSSFCell cell = null;
			// 设置表头高度
			row.setHeightInPoints(Float.parseFloat(height));
			// 设置列的宽度
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			for (int i = 0; i < this.maxColnum; i++) {
				sheet.setColumnWidth((short) i, (short) (Integer.parseInt(width)* 32/maxColnum));
				cell = getCell(row, i);
				cell.setCellStyle(borderStyle);
			}
			// 创建合并单元格
			ExportExcelUtil.mergeCell(sheet, this.rowNum, this.rowNum, 0, this.maxColnum - 1);
			cell = this.getCell(row, 0);
			CellStyle style = getStyle(1);
			style.setFont(titleFont);
			cell.setCellStyle(style);
			cell.setCellValue(title);
			this.rowNum ++;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return title;
	}
	
	/**
	 * 显示边框设置
	 * @param style
	 * @return
	 */
	private CellStyle getStyle(int i) {
		
		CellStyle style = workbook.createCellStyle();
		switch (i) {
			case 1: style.setAlignment(HorizontalAlignment.CENTER);
					style.setVerticalAlignment(VerticalAlignment.CENTER);
					style.setBorderBottom(BorderStyle.THIN);
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					style.setBottomBorderColor(HSSFColor.BLACK.index);
					
					styleMap.put(Integer.valueOf(i), style);
					break;
		
		}
		
		return style;
	}
	
	/**
	 * 获得excel行
	 * @param sheet
	 * @param index
	 * @return
	 */
	private HSSFRow getRow(int index) {
		HSSFRow row  = sheet.getRow(index);
		if (row == null) {
			row = sheet.createRow(index);
		}		
		return row;
	}
	
	/**
	 * 获得一个单元格
	 * @param row
	 * @param index
	 * @return
	 */
	private HSSFCell getCell(HSSFRow row, int index) {
		HSSFCell cell = row.getCell(index);
		if (cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}
	
	/**
	 * 
	 * @param codea0100
	 * @param userbase 人员库
	 * @param username
	 * @param datelist 日期1-31号
	 * @param kqtablejudge =true 展现本月出勤小计
	 * @param item_list 符号
	 * @param kq_duration 2009-11
	 * @return
	 * @throws GeneralException
	 */
	private void getBodyExcel(String codea0100,String userbase,String username,ArrayList datelist,boolean kqtablejudge,ArrayList item_list,String kq_duration,ExecuteKqDailyExcel dailyExcel)throws GeneralException
	{
		StringBuffer bodyhtml = new StringBuffer();
		
		HSSFRow row = this.getRow(this.rowNum);
		// 高度
		int body_hieght=Integer.parseInt(parsevo.getBody_fz())+13; 
		row.setHeightInPoints(body_hieght);
		
		// 创建标题的字体
		ExecuteKqDailyExcel daily = new ExecuteKqDailyExcel();
        HSSFFont headFont = daily.getFont(parsevo.getBody_fn(), 
        		parsevo.getBody_fb(), parsevo.getBody_fi(), 
        		parsevo.getBody_fu(), parsevo.getBody_fz(), 
        		null, workbook);
        HSSFCell cell = null;
    	CellStyle style = this.getStyle(1);
    	style.setFont(headFont);
    	style.setWrapText(true);
		
    	CellStyle borderStyle = workbook.createCellStyle();
    	borderStyle.setBorderBottom(BorderStyle.THIN);
    	borderStyle.setBorderLeft(BorderStyle.THIN);
    	borderStyle.setBorderRight(BorderStyle.THIN);
    	borderStyle.setBorderTop(BorderStyle.THIN);
    	borderStyle.setBottomBorderColor(HSSFColor.BLACK.index);
    	
    	
    	// 指针指向操作的第一行
    	int point = this.rowNum;
    	point ++;
    	HSSFRow pRow = this.getRow(point);
    	pRow.setHeightInPoints(body_hieght);
    	int vPoint = this.rowNum + 2;
    	//2014.11.7 xxd 当日期小于半个月时填充空白数据到表格
    	int num=0;
    	if(datelist.size()<16){
    		num=16;
    	}else{
    		num=datelist.size() + 2;
    	}
    	// 将日期写到excel中
    	for (int i = 0; i < num; i++) {
    		if (i == 16) {
    			this.rowNum += 3;
    			row = this.getRow(this.rowNum);
    			row.setHeightInPoints(body_hieght);
    			
    			point += 3;
    			pRow = this.getRow(point);
    			pRow.setHeightInPoints(body_hieght);
    		}
    		
    		if (i == 0 || i == 16) {
    			if (i == 0) {
    				cell = this.getCell(row, i);
    				cell.setCellStyle(style);
    				cell.setCellValue("日期");
    				
    				cell = this.getCell(pRow, i);
    				cell.setCellStyle(style);
    				cell.setCellValue("签名");
    			} else {
    				cell = this.getCell(row, i - 16);
    				cell.setCellStyle(style);
    				cell.setCellValue("日期");
    				
    				cell = this.getCell(pRow, i - 16);
    				cell.setCellStyle(style);
    				cell.setCellValue("签名");
    			}
    		} else {
    			int j = 0;
    			if (i < 16) {
    				j = i - 1;
    			} else {
    				j = i - 2;
    			}
    			String value="";
    			if(datelist.size()<=j){
    				value="";
    			}else{
    				CommonData data = (CommonData) datelist.get(j);
    				value = data.getDataValue();
    			}
    			if (i < 16) {
    				cell = this.getCell(row, i);
    				cell.setCellStyle(style);
    				cell.setCellValue(value);
    				
    				cell = this.getCell(pRow, i);
    				cell.setCellStyle(style);
    				cell.setCellValue(username);
    				
    				if (this.maxColnum == 17 && i == 15) {
    					cell = this.getCell(row, i + 1);
        				cell.setCellStyle(style);
        				cell.setCellValue("");
        				
        				cell = this.getCell(pRow, i + 1);
        				cell.setCellStyle(style);
        				cell.setCellValue("");
    				}
    			} else {
    				cell = this.getCell(row, i - 16);
    				cell.setCellStyle(style);
    				cell.setCellValue(value);
    				
    				cell = this.getCell(pRow, i - 16);
    				cell.setCellStyle(style);
    				cell.setCellValue(username);
    				
    				if (i == datelist.size() + 1 ) {
    					int q = i;
    					for (int n = 0; n < this.maxColnum - 1 - (i - 16); n++) {
    						++ q;
    						cell = this.getCell(row, q - 16);
    						cell.setCellStyle(style);
    						cell.setCellValue("");
    						
    						cell = this.getCell(pRow, q - 16);
    						cell.setCellStyle(style);
    						cell.setCellValue("");
    					}
    				}
    			}
				
    		}
    	}
    	
    	ArrayList dataList = getOneA0100Data(codea0100,userbase,datelist,body_hieght,item_list);
    	
    	row = this.getRow(vPoint);
    	row.setHeightInPoints(body_hieght);
    	int nums=0;
    	if(datelist.size()<16){
    		nums=16;
    	}else{
    		nums=datelist.size() + 2;
    	}
    	// 将日期写到excel中
    	for (int i = 0; i < nums; i++) {
    		if (i == 16) {
    			vPoint += 3;
    			row = this.getRow(vPoint);
    			row.setHeightInPoints(body_hieght);
    			
    		}
    		
    		if (i == 0 || i == 16) {
    			if (i == 0) {
    				cell = this.getCell(row, i);
    				cell.setCellStyle(style);
    				cell.setCellValue("考勤");
    				
    			} else {
    				cell = this.getCell(row, i - 16);
    				cell.setCellStyle(style);
    				cell.setCellValue("考勤");
    				
    			}
    		} else {
    			int j = 0;
    			if (i < 16) {
    				j = i - 1;
    			} else {
    				j = i - 2;
    			}
    			ArrayList fuList = new ArrayList();
    			String str="";
    			if(datelist.size()<=j){
    				str="";
    			}else{
    				fuList = (ArrayList) dataList.get(j);
        			str = (String) fuList.get(fuList.size() - 1);
    			}
    			
    			String value = "";
    			HSSFRichTextString ts = null;
    			if (str.length() > 0) {
    				ts = new HSSFRichTextString(str.toString());
    				for (int m = 0; m < fuList.size() - 1; m++) {
    					ArrayList temp = (ArrayList) fuList.get(m);
    					int start = ((Integer) temp.get(0)).intValue();
    					int end = ((Integer) temp.get(1)).intValue();
    					String fu = (String) temp.get(2);
    					String color = (String) temp.get(3);
    					HSSFFont font = dailyExcel.getColorFont(parsevo.getBody_fn(),
								parsevo.getBody_fb(), parsevo.getBody_fi(), 
								parsevo.getBody_fu(), parsevo.getBody_fz(),
								color, workbook, fu);
    					if (font != null) {
    						ts.applyFont(start, end, font);
    					} else {
    						ts.applyFont(start, end, headFont);
    						
    					}
    				}
    				
    			} 
    			if (i < 16) {
    				cell = this.getCell(row, i);
    				cell.setCellStyle(borderStyle);
    				if (str.length() > 0) {
    					cell.setCellValue(ts);
    				} else {
    					cell.setCellValue(value);
    				}
    				
    				if (this.maxColnum == 17 && i == 15) {
    					cell = this.getCell(row, i + 1);
        				cell.setCellStyle(style);
        				cell.setCellValue("");
    				}
    				
    			} else {
    				cell = this.getCell(row, i - 16);
    				cell.setCellStyle(borderStyle);
    				if (str.length() > 0) {
    					cell.setCellValue(ts);
    				} else {
    					cell.setCellValue(value);
    				}
    				
    				if (i == dataList.size() + 1 ) {
    					int q = i;
    					for (int n = 0; n < this.maxColnum - 1 - (i - 16); n++) {
    						++ q;
    						cell = this.getCell(row, q - 16);
    						cell.setCellStyle(style);
    						cell.setCellValue("");
    					}
    				}
    				
    			}
    		}
    	}
    	
    	this.rowNum = vPoint + 1;
    	
    	if(kqtablejudge) {
    		KqViewDailyBo kqviewdailybo =new KqViewDailyBo();
        	ArrayList kqq03list = kqviewdailybo.getKqBookItemList();
        	ArrayList list = getOneA0100Value(codea0100,userbase,kq_duration,body_hieght,kqq03list);
        	ArrayList colList = (ArrayList) list.get(0);
        	ArrayList valList = (ArrayList) list.get(1);
        	
        	HSSFRow vRow = null;
        	for (int i = 0; i < colList.size(); i++) {
        		String col = (String) colList.get(i);
        		String value = (String) valList.get(i);
        		if (i % this.maxColnum == 0) {
	        		row = this.getRow(this.rowNum ++);
	        		row.setHeightInPoints(body_hieght);
	        		
	        		vRow = this.getRow(this.rowNum ++);
	        		vRow.setHeightInPoints(body_hieght);
        		}
        		cell = this.getCell(row, i % this.maxColnum);
        		cell.setCellStyle(style);
        		cell.setCellValue(col);
        		
        		cell = this.getCell(vRow, i % this.maxColnum);
        		cell.setCellStyle(style);
        		cell.setCellValue(value);
        		
        	}
        	
        	// 将空白添加表格线
        	if (colList.size() % this.maxColnum != 0) {
        		int remain = colList.size() % this.maxColnum;
        		for (int i = remain; i < this.maxColnum ; i++) {
        			
        			cell = this.getCell(row, i % this.maxColnum);
        			cell.setCellStyle(borderStyle);
        			cell.setCellValue("");
        			
        			cell = this.getCell(vRow, i % this.maxColnum);
        			cell.setCellStyle(borderStyle);
        			cell.setCellValue("");
        		}
        	}
        	
    	}

	}



	/**
	 * 本月出勤小计
	 */
	private ArrayList getOneA0100Value(String codea0100,String userbase,String kq_duration,int body_hieght,ArrayList kqq03list) {
		ArrayList list = new ArrayList();
		StringBuffer column = new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	ContentDAO dao = new ContentDAO(this.conn);
    	ArrayList colList = new ArrayList();
		for(int i = 0; i < kqq03list.size(); i++) {
			FieldItem fielditem = (FieldItem)kqq03list.get(i);
    		if(!"i9999".equals(fielditem.getItemid())) {
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem);
   	    		if (colList.size() % this.maxColnum == 0) {
   	    			colList.add("假别");
   	    			colList.add(fielditem.getItemdesc());
   	    		} else {
   	    			colList.add(fielditem.getItemdesc());
   	    		}
   	    	}
    	}
		
		list.add(colList);
		ArrayList valList = new ArrayList();
		
    	RowSet rowSet=null;
    	String itemid="";
    	try
    	{
    		int l=column.toString().length()-1;
    	 	String columnstr=column.toString().substring(0,l);
         	for(int t=0;t<columnlist.size();t++) {
         		StringBuffer sql_on_a0100=new StringBuffer();
         		FieldItem fielditem = (FieldItem)columnlist.get(t);
         		String itd = fielditem.getItemid();
                if("D".equalsIgnoreCase(fielditem.getItemtype())) {
                    itd = Sql_switcher.dateToChar(itd);
                }
              
        		sql_on_a0100.append("select " + itd + " as one");
                sql_on_a0100.append(" from Q05");
                if (this.getCurTab().toLowerCase().contains("_arc")) {
                    sql_on_a0100.append("_arc");
                }
                sql_on_a0100.append(" where Q03Z0='").append(kq_duration).append("'");
                sql_on_a0100.append(" and nbase='").append(userbase).append("'");
                sql_on_a0100.append(" and A0100='").append(codea0100).append("'");
                
        		rowSet=dao.search(sql_on_a0100.toString());
        		
        		StringBuffer font_str=new StringBuffer();
        		if(rowSet.next()) {
        			itemid = rowSet.getString("one");
        			if(itemid != null) {
        				if("".equals(itemid)|| "0E-8".equalsIgnoreCase(itemid)|| "0".equalsIgnoreCase(itemid)) {
        					itemid="";
        				} else {
        					if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
        						int num = fielditem.getDecimalwidth();
								if(itemid.indexOf(".") != -1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid,num);
								} else {
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										} else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid,num);
									
								}
        					} else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									if (!codemap.containsKey(setid+itemid)) {
										String codesetid = itemid;
										itemid = AdminCode.getCodeName(setid, itemid);
										codemap.put(setid+codesetid, itemid);
									} else {
										itemid = (String) codemap.get(setid+itemid);
									}
								}
							} 
        				}
        			} else {
        				itemid = "";
        			}
        			
        			if (valList.size() % this.maxColnum == 0) {
        				valList.add("工日");
        				valList.add(itemid);
        				
        			} else {
        				valList.add(itemid);
        			}
        			
        		}else {
        			if (valList.size() % this.maxColnum == 0) {
        				valList.add("工日");
        				valList.add("");
        				
        			} else {
        				valList.add("");
        			}
        		}
         	}

         	list.add(valList);
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(rowSet!=null) {
					rowSet.close();
    			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		 } 
    	return list;
	}
	



	/**
	 * 转换成字体布局字符
	 */
	private String[] transAlign(int Align)
	{
		String[] temp=new String[2];
		if(Align==0)
		{
			temp[0]="top";
			temp[1]="left";
		}
		else if(Align==1)
		{
			temp[0]="top";
			temp[1]="center";
		}
		else if(Align==2)
		{
			temp[0]="top";
			temp[1]="right";
		}
		else if(Align==3)
		{
			temp[0]="bottom";
			temp[1]="left";
		}
		else if(Align==4)
		{
			temp[0]="bottom";
			temp[1]="center";
		}
		else if(Align==5)
		{
			temp[0]="bottom";
			temp[1]="right";
		}
		else if(Align==6)
		{
			temp[0]="middle";
			temp[1]="left";
		}
		else if(Align==7)
		{
			temp[0]="middle";
			temp[1]="center";
		}
		else if(Align==8)
		{
			temp[0]="middle";
			temp[1]="right";
		}		
		return temp;		
	}

	/**
	 * 计算表格实际总宽度
	 * 首钢更改，注销是原始的 
	 * */
	private String getFactWidth()
	{
		String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
		if("1".equals(parsevo.getOrientation().trim()))
		{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getHeight())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}else{
			if("px".equals(unit))
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}else
			{
				double width=Double.parseDouble(parsevo.getWidth())/0.26-Double.parseDouble(getPxFormMm(parsevo.getLeft()))-Double.parseDouble(getPxFormMm(parsevo.getRight()));
				return KqReportInit.round(width+"",0);	
			}
		}
	}
	 /**
     * 转换，毫米转换为像素
     * */
    private String getPxFormMm(String value)
    {
    	if(value==null&&value.length()<=0) {
            return "0";
        }
    	String unit=parsevo.getUnit().trim();//长度单位,毫米还是像素
    	if("mm".equals(unit))
		{
    		double dv=Double.parseDouble(value)/0.26;
    		return KqReportInit.round(dv+"",0);
		}else
		{
			return KqReportInit.round(value,0);
		}
    }
    /**
     * 以用高度
     * **/
    private double getIsUseHieght(ArrayList item_list)throws GeneralException
    {
       	
    	double height=Double.parseDouble(getPxFormMm(parsevo.getTop()))+Double.parseDouble(getPxFormMm(parsevo.getBottom()))+Double.parseDouble(getPxFormMm(parsevo.getTitle_h()));
    	height=height+Double.parseDouble(getPxFormMm(parsevo.getHead_h()));
    	height=height+Double.parseDouble(parsevo.getBody_fz())+13;
    	   	
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);
    	//计算备注1
    	StringBuffer note_len_str= new StringBuffer();    	
    	note_len_str.append("备注：1.");
    	for(int i=0;i<fielditemlist.size();i++)
    	{
   	     FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     if("N".equals(fielditem.getItemtype()))
   	     {
   	    	if(!"i9999".equals(fielditem.getItemid()))
   	    	{
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0)
   	    		{
   	    			note_len_str.append(fielditem.getItemdesc()+"("+kq_item[0]+")");
   	   	    	}  	    		
   	    	}
   		  }				
   	    }   
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());      	   	
    	int numrow_tile_1=getNumRow(strlen);      	
    	if(numrow_tile_1!=0)
    	{
    		height=height+(Double.parseDouble(parsevo.getBody_fz())+6)*numrow_tile_1;	
    	}else
    	{
    		height=height+(Double.parseDouble(parsevo.getBody_fz())+6);
    	}
    	
    	//计算表尾客户添加的文本内容
    	if(parsevo.getTile_fw()!=null&&parsevo.getTile_fw().length()>0)
    	{
    		/*****#代表一个空格****/
    		String tile_fw="#####2."+parsevo.getTile_fw();
    		int str_tile_2=tile_fw.length()*Integer.parseInt(parsevo.getBody_fz());
    		
    		int note_tile_2=getNumRow(str_tile_2);
    		
    		if(note_tile_2!=0)
        	{
        		height=height+(Double.parseDouble(parsevo.getBody_fz())+6)*note_tile_2;	
        	}   		
    	}
    	
    	if("#c".equals(parsevo.getHead_c())||"#p".equals(parsevo.getHead_p())||"#e".equals(parsevo.getHead_e())||"#u".equals(parsevo.getHead_u())||"#d".equals(parsevo.getHead_d())||"#t".equals(parsevo.getHead_t()))
    	{
    		height=height+Double.parseDouble(getPxFormMm(parsevo.getHead_h()));
    	}
    	
    	if("#c".equals(parsevo.getTile_c())||"#p".equals(parsevo.getTile_p())||"#e".equals(parsevo.getTile_e())||"#u".equals(parsevo.getTile_u())||"#d".equals(parsevo.getTile_d())||"#t".equals(parsevo.getTile_t()))
    	{
    		height=height+Double.parseDouble(getPxFormMm(parsevo.getTile_h()));
    	}    	
    	
    	return height;
    }  
 

	 /**
	 * 计算在规定的字数中，一串字符，有多少行
	 * */
	private int getNumRow(int strlen)
	{
		 int factwidth=Integer.parseInt(getFactWidth());
		 
			 int ss=strlen/factwidth;
			 int dd=strlen%factwidth;
	  	     if(dd!=0)
	  	     {
	  	    	ss=ss+1;
	  	     } 
	  	   return ss; 		 	   
	}
	
	/**
	 * 
	 * 得到表头内容 
	 *   #p（页码）  #c 总页数  #e 制作人  #u 制作人所在的单位  #d 日期  #t  时间  #fn宋体 字体名称  #fz15    字体大小
	 *   #fb[0|1]  黑体  #fi[0|1]   斜体  #fu[0|1]   下划线  #pr[0|1]  页行数
	 * 
	 * @param codea0100 String 人员编号
	 * @param coursedate Sring 考勤期间
	 * @param curpage int 当前页 
	 * @param sum_page int 总共页数
	 * @param datelist ArrayList 日期
	 * @param userbase String 人员库
	 * @throws GeneralException
	 */
	private void getTableHead(String codea0100,String coursedate,ArrayList datelist,String userbase)throws GeneralException {
      
        String [] codeitem = getCodeItemDesc(codea0100,userbase);
        String head_height = getPxFormMm(parsevo.getHead_h());
        ExecuteKqDailyExcel daily = new ExecuteKqDailyExcel();
        
        // 创建标题的字体
        HSSFFont headFont = daily.getFont(parsevo.getHead_fn(), 
        		parsevo.getHead_fb(), parsevo.getHead_fi(), 
        		parsevo.getHead_fu(), parsevo.getHead_fz(), 
        		null, workbook);
        CellStyle style= this.getStyle(1);
 	   	style.setFont(headFont);
 	   	
        HSSFRow row = null;
        
        CellStyle borderStyle = workbook.createCellStyle();
    	borderStyle.setBorderBottom(BorderStyle.THIN);
    	borderStyle.setBorderLeft(BorderStyle.THIN);
    	borderStyle.setBorderRight(BorderStyle.THIN);
    	borderStyle.setBorderTop(BorderStyle.THIN);
    	borderStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        
        try { 
        	
        	row = this.getRow(this.rowNum);
        	row.setHeightInPoints(Float.parseFloat(head_height));
            HSSFCell cell = null;
            for (int i = 0; i < this.maxColnum; i++) {
         	   cell = this.getCell(row, i);
         	   
         	   cell.setCellStyle(borderStyle);
            }
            
           /**单位**/  
           String dv_content = "";
           if(codeitem[0] == null || codeitem[0].length() <= 0) {
        	   dv_content = "单位：";
           } else {
        	   dv_content = "单位：" + codeitem[0];
           }
           
           cell = row.getCell(0);
           cell.setCellValue(dv_content);
          
           /**部门*/   
           String bm_content = "";
           if(codeitem[1] == null || codeitem[1].length() <= 0) {
        	   bm_content = "部门：";
           } else {
        	   bm_content = "部门：" + codeitem[1];
           }
           
           cell = row.getCell(5);
           cell.setCellValue(bm_content);
           
            /**日期*/      
           CommonData vo = (CommonData)datelist.get(0);	           
           String start_date = vo.getDataName();
           vo = (CommonData)datelist.get(datelist.size()-1);	 
           String end_date = vo.getDataName();    
           String da_content = coursedate+" ("+start_date+"~"+end_date+")";
           
           cell = row.getCell(10);
           cell.setCellValue(da_content);

           // 创建合并单元格
           for (int i = 1; i <= 3; i++) {
        	   if (i < 3) {
		           sheet.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, (i - 1) * (maxColnum/3), i * (maxColnum/3) - 1));   
        	   } else {
        	       sheet.addMergedRegion(new CellRangeAddress(this.rowNum, this.rowNum, (i - 1) * (maxColnum/3), maxColnum - 1));   		
        	   }
           }
           
           this.rowNum ++;
           
           if ("#e".equals(parsevo.getHead_e().trim()) || "#u".equals(parsevo.getHead_u().trim()) 
        		   || "#d".equals(parsevo.getHead_d().trim()) || "#t".equals(parsevo.getHead_t().trim()) 
        		   || "#p".equals(parsevo.getHead_p().trim()) || "#c".equals(parsevo.getHead_c().trim())){

        	   // 计算有几个值
        	   int i = 0;
        	   row = this.getRow(this.rowNum);
        	   row.setHeightInPoints(Float.parseFloat(head_height));
        	   
        	   for (int j = 0; j < this.maxColnum; j++) {
             	   cell = this.getCell(row, j);
             	   
             	   cell.setCellStyle(borderStyle);
               }
        	   if("#u".equals(parsevo.getHead_u().trim())) {
        		   i ++;
        	   }
        	   if("#e".equals(parsevo.getHead_e().trim())) {
        		   i ++;
        	   }
        	   if("#d".equals(parsevo.getHead_d().trim())) {
        		   i ++;
        	   }
        	   if("#t".equals(parsevo.getHead_t().trim())) {
        		   i ++;
        	   }
        	   if("#p".equals(parsevo.getHead_p().trim())) {
        		   i ++;
        	   }
        	   if("#c".equals(parsevo.getHead_c().trim())) {
        		   i ++;
        	   }
        	   
        	   int j = 1;
        	   
        	   
        	   // 制作人
        	   if("#e".equals(parsevo.getHead_e().trim())) {
        	   
        		   String e_str = "制作人: "+this.userView.getUserFullName();
        		   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
        		   
        		   cell.setCellValue(e_str);
        		   cell.setCellStyle(style);
        		   j ++;
 
        	   }
        	   
        	   // 制作人单位
        	   if("#u".equals(parsevo.getHead_u().trim())) {
        	   
        		   String u_code = "";
        		   if(!userView.isSuper_admin()) {
        			   if(userView.getUserOrgId() != null 
        					   && userView.getUserOrgId().trim().length() > 0) {
        				   u_code = userView.getUserOrgId();
        			   } else {
        				   u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        			   }
        		   }
        		   String [] u_codeitem = getCodeItemDesclow(u_code);
        		   String u_str = "制作人单位: "+u_codeitem[0];
        	   
        		   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
        		   cell.setCellValue(u_str);
        		   cell.setCellStyle(style);
        		   
        		   j++;
        	   }
        	   
        	   // 制作日期
        	   if("#d".equals(parsevo.getHead_d().trim())) {
        	   
        		   String d_str = "制作日期: "+PubFunc.getStringDate("yyyy.MM.dd");
        		   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
        		   cell.setCellValue(d_str);
        		   cell.setCellStyle(style);
        		   
        		   j++;  
        	   }
        	   
        	   // 制作时间
        	   if("#t".equals(parsevo.getHead_t().trim())) {

        		   String t_str = "时间: "+PubFunc.getStringDate("HH:mm:ss");
        		   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
        		   cell.setCellValue(t_str);
        		   cell.setCellStyle(style);
        		   
        		   j++;
        	   }
        	   
        	   // 页码
        	   if("#p".equals(parsevo.getHead_p().trim())) {
        		   String p_str = "页码:1";
        		   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
        		   cell.setCellValue(p_str);
        		   cell.setCellStyle(style);
        		   
        		   j++;
        	   }
        	   
        	   // 总页码
        	   if("#c".equals(parsevo.getHead_c().trim())) {
        	          	   
	        	   String c_str = "总页码:1";
	        	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
	    		   cell.setCellValue(c_str);
	    		   cell.setCellStyle(style);
	    		   
	    		   j++; 
        	   }
        	   
        	   // 创建合并单元格
               for (int m = 1; m <= i; m++) {
            	   if (m < i) {
    		           ExportExcelUtil.mergeCell(sheet, this.rowNum, this.rowNum, (m - 1) * (maxColnum/i), m * (maxColnum/i) - 1);   		
            	   } else {
       		           ExportExcelUtil.mergeCell(sheet, this.rowNum, this.rowNum, (m - 1) * (maxColnum/i), maxColnum - 1);
            	   }
               }
        	   
        	   this.rowNum ++ ;
           
           }
        } catch(Exception e) {
			e.printStackTrace();
		}
    }
	/**
	 * 通过code得到codeItemDesc
	 * */
	private String[] getCodeItemDesc(String code,String userbase)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select b0110,e0122 from "+userbase+"A01 where A0100='"+code+"'";
	  String B0110="";
	  String E0122="";
	  String desc="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		B0110 = rowSet.getString("b0110");
	    		E0122 = rowSet.getString("e0122");
	    	}	 
	    	PubFunc.closeDbObj(rowSet);
	    	
	    	if(!"".equals(B0110)||B0110.length()>0)
	    	{
	    		String sqlb ="select codeitemdesc from organization where  codeitemid='"+B0110+"'";
	    		rowSet=dao.search(sqlb);
	    		if(rowSet.next())
	    		{
	    			desc=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc)||desc.length()>0)
	    		{
	    			codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    	if(!"".equals(E0122)||E0122.length()>0)
	    	{
	    		String sqle ="select codeitemdesc from organization where  codeitemid='"+E0122+"'";
	    		rowSet=dao.search(sqle);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
	    		}
	    		if(!"".equals(desc2)||desc2.length()>0)
	    		{
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");	
	    		}
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	    {
			PubFunc.closeDbObj(rowSet);
		} 
	   return codeItemDesc;
	}
	/*
	 * 处理页面显示虚线
	 */
	private String getStyleName(String temp)
	{
		//处理虚线	L,T,R,B,
	    String style_name="RecordRow_self";
	    if("0".equals(temp))
	    {
	    	style_name="RecordRow_self_l";
	    }
	    else if("1".equals(temp))
	    {
	    	style_name="RecordRow_self_t";
	    }
	    else if("2".equals(temp))
	    {
	    	style_name="RecordRow_self_r";
	    }
	    else if("3".equals(temp))
	    {
	    	style_name="RecordRow_self_b";
	    }else if("4".equals(temp))
	    {
	    	style_name="RecordRow_self_two";
	    }else if("5".equals(temp))
	    {
	    	style_name="RecordRow_self_t_l";
	    }else if("6".equals(temp))
	    {
	    	style_name="RecordRow_self_t_r";
	    }
	    
		return style_name;
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    private String getFontStyle(String fn,String fi,String fu,String fb,String fz)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	private String executeTable(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-break:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		
		if (context == null || context.trim().length() <= 0) {
			tempTable.append("&nbsp;");
		}
		
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
			tempTable.append(context);	
		}  
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	
	//字体竖型排
	private String executeTables(int type,int Align,String fontName,String fontStyle,String height,String context,String style_name)
	{
		
		StringBuffer tempTable=new StringBuffer("");		
		String[] temp=transAlign(Align);
		String aValign=temp[0];
		String aAlign=temp[1];
		
		tempTable.append(" <td height='"+height+"' width='6%' style='table-layout: fixed; word-wrap:break-all;'");
		if(type==1)
		{
			tempTable.append(" class='"+style_name+"' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		if (context == null || context.length() <= 0) {
			tempTable.append("&nbsp;");
		}
	    if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0)
	    {
	    	tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' > \n ");
	    }
		
		if(context!=null&&context.length()>0)
		{
			for(int p=0;p<context.length();p++)
			{
				
				String d =context.substring(p,p+1);
				tempTable.append(d);
				tempTable.append("<br>");
			}
		} 
		
		 if(fontName!=null&&fontName.length()>0&&fontStyle!=null&&fontStyle.length()>0) {
             tempTable.append("</font>");
         }
		
		tempTable.append("</td> \n ");
		
		return tempTable.toString();
	}
	
	
	/**
	 * 通过考勤期间得到考勤日期
	 * @param conn Connection 数据库连接
	 * @param coursedate String 当前考勤期间
	 * @return ArrayList<CommonData>
	 * @throws GeneralException
	 */
	private ArrayList getDateList(Connection conn, String coursedate) throws GeneralException {
		ArrayList dateList = new ArrayList();
		RowSet rowSet = null;
		String[] date = coursedate.split("-");
		String kq_year = date[0];
		String kq_duration = date[1];
		String kq_start;
		String kq_dd;
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT kq_start,kq_end FROM kq_duration where kq_year='");
		sql.append(kq_year);
		sql.append("'and kq_duration='");
		sql.append(kq_duration);
		sql.append("'");
		
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(sql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				int spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				SimpleDateFormat format2 = new SimpleDateFormat("dd");
				
				for (int i = 0; i <= spacedate; i++) {	          	  
            	  	CommonData vo = new CommonData();									
					kq_start = format1.format(d1);
					kq_dd = format2.format(d1);
					vo.setDataName(kq_start);
					vo.setDataValue(kq_dd);
					dateList.add(vo);					
					d1 = DateUtils.addDays(d1,1);					
				}				
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
		} finally {
			PubFunc.closeDbObj(rowSet);
		} 
		
		return dateList;
  }
  /**
	 * 通过code得到codeItemDesc
	 * */
	private String[] getCodeItemDesclow(String code)
	{
	  RowSet rowSet=null;
	  String codeItemDesc []=new String[2];	 
	  String parentid="";
	  String sql="select codeitemdesc,parentid from organization where codeitemid='"+code+"'";
	  String desc1="";
	  String desc2="";
	  ContentDAO dao=new ContentDAO(this.conn);		
		try
	    {
			rowSet=dao.search(sql);
	    	if(rowSet.next())
	    	{	    		
	    		codeItemDesc[0]=rowSet.getString("codeitemdesc");	
	    		desc1=rowSet.getString("codeitemdesc");
	    		if(desc1==null||desc1.length()<=0) {
                    desc1="";
                }
	    		parentid=rowSet.getString("parentid");
	    		PubFunc.closeDbObj(rowSet);
	    		
	    		String sqlp="select codeitemdesc from organization where codeitemid='"+parentid+"'";
	    		rowSet=dao.search(sqlp);
	    		if(rowSet.next())
	    		{
	    			desc2=rowSet.getString("codeitemdesc");
		    		if(desc2==null||desc2.length()<=0) {
                        desc2="";
                    }
	    			codeItemDesc[1]=rowSet.getString("codeitemdesc");
	    		}
	    	}	  
	    	if(desc2.equals(desc1))
			{
				codeItemDesc[0]="";
			}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally
	    {
			PubFunc.closeDbObj(rowSet);
		} 
	    return codeItemDesc;
	}
	/**
     * 通过一个员工编号得到该员工考勤期间的数据
     * file=1 的时候输出 1到15号
     * file=2 的时候输出 16到最后
     *   姓名在这里加链接
     * */ 
	private ArrayList getOneA0100Data(String codea0100,String userbase,ArrayList datelist,
			int body_hieght,ArrayList item_list)throws GeneralException {
		ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	
    	StringBuffer column = new StringBuffer();    	
    	ArrayList columnlist = new ArrayList();
    	
    	for(int i = 0; i < fielditemlist.size(); i++) {
    		FieldItem fielditem=(FieldItem)fielditemlist.get(i);
   	     
   	    	if(!"i9999".equals(fielditem.getItemid())) {
   	    		column.append(""+fielditem.getItemid()+",");  
   	    		columnlist.add(fielditem.getItemid());
   	    	}
   		}
    	
    	//开始时间
    	CommonData start_vo = (CommonData)datelist.get(0);		
        String start_date=start_vo.getDataName();
        
        //结束时间	
        start_vo = (CommonData)datelist.get(datelist.size()-1);
        String end_date = start_vo.getDataName();     
    	
    	int body_height = Integer.parseInt(parsevo.getBody_fz()) + 13;
    	StringBuffer one_date = new StringBuffer(); 
  
    	ContentDAO dao = new ContentDAO(this.conn);            
        RowSet rowSet=null;
        ArrayList dataList = new ArrayList();
        try{
        	String sql_one_a0100 = selcet_kq_one_emp(codea0100,userbase,start_date,end_date,column.toString());
         	rowSet=dao.search(sql_one_a0100 + " order by q03z0 ");
         	HashMap kq_item_map = new HashMap();
         	Date itemDate;
         	
         	while(rowSet.next()) {
         		String q03z0=rowSet.getString("q03z0").trim();     
        	    ArrayList list =new ArrayList();
        	    ArrayList z1_list=new ArrayList();
        	    ArrayList scq_list=new ArrayList();
        	    HashMap kq_item_all = this.querryKq_item();
        	    int rownum = 0;
        	    for(int i = 0; i < fielditemlist.size(); i++) {
        	    	FieldItem fielditem=(FieldItem)fielditemlist.get(i);
        	    	if("N".equals(fielditem.getItemtype()) && !"i9999".equals(fielditem.getItemid())) {
        	    		if("q03z1".equalsIgnoreCase(fielditem.getItemid())||(fielditem.getItemdesc().indexOf("出勤")!=-1
        	    				||fielditem.getItemdesc().indexOf("出勤率")!=-1)) {
        	    			if("q03z1".equalsIgnoreCase(fielditem.getItemid())) {
        	    				double  dv=rowSet.getDouble(fielditem.getItemid());
        	    				if(dv != 0){
        	    					String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
        	    					z1_list.add(kq_item);
        	    					z1_list.add(new Double(dv));             			        	
        	    				}
        	    			} else {
        	    				double  dv=rowSet.getDouble(fielditem.getItemid());
        	    				if(dv != 0) {
        	    					String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);                   			        	 
        	    					scq_list.add(kq_item);
        	    					scq_list.add(new Double(dv));             			        
        	    				}
        	    			}     
        	    		} else {        
        	    			String value = rowSet.getString(fielditem.getItemid());
           			       	if(value == null || value.length() <= 0) {
           			       		value="0";
           			       	}
           			       	double dv = Double.parseDouble(value);
           			       	if(dv != 0) {
           			       		String[] kq_item =KqReportInit.getKq_Item(fielditem.getItemid(),item_list);        			         			  
           			       		ArrayList one_list= new ArrayList();
           			       		one_list.add(kq_item);
           			       		one_list.add(new Double(dv));
           			       		list.add(one_list);
               			      
           			       		if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
           			       			rownum ++;
           			       		}

           			       		continue;
           			       	} else {
               		    	   continue;
           			       	}
        	    		}
        	    	}else if(!"q03z0".equals(fielditem.getItemid()) && !"nbase".equals(fielditem.getItemid())
        	    			&&!"a0100".equals(fielditem.getItemid()) && !"b0110".equals(fielditem.getItemid())
        	    			&&!"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid())
        	    			&&!"q03z3".equals(fielditem.getItemid()) && !"q03z5".equals(fielditem.getItemid())
        	    			&&!"state".equals(fielditem.getItemid()) && !"a0101".equals(fielditem.getItemid())
        	    			&&!"i9999".equals(fielditem.getItemid())) {

            	    	    String  sr;
            	            if("D".equalsIgnoreCase(fielditem.getItemtype())){
                                int length =fielditem.getItemlength();
                                itemDate = rowSet.getDate(fielditem.getItemid());
                                
                                String dateString = "";
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                if(length == 4) {
                                    formatter = new SimpleDateFormat("yyyy");
                                } else if(length == 7) {
                                    formatter = new SimpleDateFormat("yyyy-MM");
                                } else if(length == 16) {
                                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                } else if(length >= 18) {
                                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                }
                                
                                
                                if(itemDate != null) {
                                    dateString = formatter.format(itemDate);
                                }
                                
                                sr =dateString;
                            }else {
                                sr = rowSet.getString(fielditem.getItemid());
                            }
        	    	    
        	    		if(sr != null && sr.length()>0 && !"0".equals(sr)) {
        	    			String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(),item_list);    
        	    			ArrayList one_list = new ArrayList();
         			        one_list.add(kq_item);
         			        one_list.add(new Double(1));
         			        list.add(one_list);
        	    		}
        	      }
           	 }             	    
  
        	 if (rownum == 0) {
        	 	list.add(scq_list);
        	 }
         			
           	  kq_item_map.put(q03z0,list);
           }
         	
         	for(int s = 0; s < datelist.size(); s++) {
         		ArrayList fuList = new ArrayList();
         		StringBuffer str = new StringBuffer();
         		CommonData cur_vo = (CommonData)datelist.get(s);		
                String cur_date=cur_vo.getDataName().trim();                   
                ArrayList kq_item_list=(ArrayList)kq_item_map.get(cur_date);  //符号
                if(kq_item_list != null && kq_item_list.size() > 0) {
                	StringBuffer font_str=new StringBuffer();
               	   	for(int t = 0;t < kq_item_list.size(); t++) {
               	   		ArrayList one_list = (ArrayList)kq_item_list.get(t);
               	   		if(one_list != null && one_list.size() > 0) {
               			   
               			   String [] kq_item = (String [])one_list.get(0);                	   
                       	   Double dv = (Double)one_list.get(1);
                       	   double value = dv.doubleValue();
                       	   if(value != 0) {
                       		   if (kq_item[0] != null) {
	                       		   ArrayList temp = new ArrayList();
	                       		   temp.add(Integer.valueOf(str.length()));
	                       		   str.append(kq_item[0]);
	                       		   temp.add(Integer.valueOf(str.length()));
	                       		   temp.add(kq_item[0]);
	                       		   temp.add(kq_item[1]);
	                       		   fuList.add(temp);
                       		   }
                       	   } 	  
               			}
               	   }
               	   
                } 
                fuList.add(str.toString());
                
                dataList.add(fuList);
        	  }
        }catch(Exception e){ 
        	e.printStackTrace();
        } finally {
			PubFunc.closeDbObj(rowSet);
	    } 
        return dataList;
	}
	/**
     * 得到样式
     * @param  fn  字体
     * @param  fi  斜体
     * @param  fu  下划线
     * @param  fb  粗体
     * @param  fz  大小
     * @return  
     *        样式内容
     * */
    private String getFontStyle(String fn,String fi,String fu,String fb,String fz,String color)
    {
    	StringBuffer style= new StringBuffer();
    	if(fn!=null&&fn.length()>0)
    	{
    	    style.append("font-family: "+fn+";");
    	}else
    	{
    		style.append("font-family: '宋体';");
    	}
    	if("#fi[1]".equals(fi))//斜体
    	{
    		style.append("font-style: italic;");
    	}
    	if("#fu[1]".equals(fu))//下划线
    	{
    		style.append("text-decoration: underline;");
    	}
    	if("#fb[1]".equals(fb))
    	{
    		style.append("font-weight: bolder;");
    	}
    	if(fz!=null&&fz.length()>0)
    	{
    		style.append("font-size: "+fz+"px;");
    	}else
    	{
    		style.append("font-size: 12px;");
    	}
    	if(color!=null&&color.length()>0)
    	{
    		style.append("color: "+color+";");
    	}else
    	{
    		style.append("color: #FF0000;");
    	}
    	
    	return style.toString();
    }
    /**
	 * 生成一个单元格)
	 * @param border  边宽
	 * @param align	  字体布局位置	 
	 * @param type    1:表格   2：标题
	 * @param context  内容
	 */	
	private String executeFont(String fontName,String fontStyle,String context)
	{
		
		StringBuffer tempTable=new StringBuffer("");
		if(context!=null&&context.trim().length()>0) {
			tempTable.append("<font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(fontStyle);
			tempTable.append("' >");
			tempTable.append(context);			
			tempTable.append("</font>");
		} 
		
		return tempTable.toString();
	}
	/**
	 * 组成查询内容的sql
	 * @param codea0100
	 * @param userbase
	 * @param start_date
	 * @param end_date
	 * @param column
	 * @return
	 */
	private String selcet_kq_one_emp(String codea0100,String userbase,String start_date,String end_date,String column)
	{
		StringBuffer sqlstr= new StringBuffer();
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
	 	sqlstr.append("select "+columnstr+" from ").append(this.getCurTab()); 	   
	 	sqlstr.append(" where Q03Z0 >= '"+start_date+"'");
	 	sqlstr.append(" and Q03Z0 <= '"+end_date+"%'");
	 	sqlstr.append(" and a0100="+codea0100+"");
	 	sqlstr.append(" and nbase='"+userbase+"'");
	 	return sqlstr.toString();
	}
	/**
     * 得到表尾数据
     * **/
    private void getTileHtml(ArrayList item_list,ExecuteKqDailyExcel dailyExcel)throws GeneralException {

    	// 高度
    	String tile_height = getPxFormMm(parsevo.getTile_h());
    	ArrayList fielditemlist = DataDictionary.getFieldList("q03",
				Constant.USED_FIELD_SET);    	 
    
    	ExecuteKqDailyExcel daily = new ExecuteKqDailyExcel();
        
        // 创建标题的字体
        HSSFFont headFont = daily.getFont(parsevo.getBody_fn(), 
        		parsevo.getBody_fb(), parsevo.getBody_fi(), 
        		parsevo.getBody_fu(), parsevo.getBody_fz(), 
        		null, workbook);
    	CellStyle style = this.getStyle(1);
    	style.setFont(headFont);
    	
        HSSFRow row = this.getRow(this.rowNum);

    	StringBuffer note_len_str = new StringBuffer();
    	ArrayList itemList = new ArrayList();
    	ArrayList list4 = new ArrayList();
    	list4.add(Integer.valueOf(note_len_str.length()));
    	note_len_str.append("备注：1.");
    	list4.add(Integer.valueOf(note_len_str.length()));
    	list4.add(headFont);
    	itemList.add(list4);
    	
    	for(int i = 0; i < fielditemlist.size(); i++) {
    		FieldItem fielditem = (FieldItem)fielditemlist.get(i);
   	    	if(!"i9999".equals(fielditem.getItemid())) {
   	    		
   	    		String kq_item[]=KqReportInit.getKq_Item(fielditem.getItemid(),item_list); 
   	    		if(kq_item[0]!=null&&kq_item[0].length()>0) {
   	    			ArrayList list = new ArrayList();
   	    			list.add(Integer.valueOf(note_len_str.length()));
   	    			note_len_str.append(fielditem.getItemdesc()+"(");
   	    			list.add(Integer.valueOf(note_len_str.length()));
   	    			list.add(headFont);
   	    			itemList.add(list);
   	    			
   	    			ArrayList list2 = new ArrayList();
   	    			list2.add(Integer.valueOf(note_len_str.length()));
   	    			
   	    			HSSFFont font = dailyExcel.getColorFont(parsevo.getBody_fn(),
							parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo
							.getBody_fu(), parsevo.getBody_fz(),
							kq_item[1], workbook, kq_item[0]);
   	   	 
   	    			note_len_str.append(kq_item[0]);
   	    			list2.add(Integer.valueOf(note_len_str.length()));
   	    			list2.add(font);
   	    			itemList.add(list2);
   	    			
   	    			ArrayList list3 = new ArrayList();
   	    			list3.add(Integer.valueOf(note_len_str.length()));
   	   	    	    note_len_str.append(")");
   	   	    	    list3.add(Integer.valueOf(note_len_str.length()));
   	   	    	    list3.add(headFont);
   	   	    	    itemList.add(list3);
   	   	    	    
   	   	    	}  	    		
   	    	}				
   	    }   
    	  
    	//20170415 linbz 27104 备注信息第二条时需换行处理
    	if (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0) {
			String tile_fw = "     2." + parsevo.getTile_fw();
			int str_tile_2 = tile_fw.length() * Integer.parseInt(parsevo.getBody_fz());
			int numrow_2 = getNumRow(str_tile_2);
			int note_h_2 = Integer.parseInt(parsevo.getBody_fz()) + 6;
			if (numrow_2 != 0) {
				note_h_2 = note_h_2 * numrow_2;
			}
			ArrayList list5 = new ArrayList();
			list5.add(Integer.valueOf(note_len_str.length()));
			note_len_str.append("\r\n");
			note_len_str.append(tile_fw);
			list5.add(Integer.valueOf(note_len_str.length()));
			list5.add(headFont);
			itemList.add(list5);      
		}
    	
    	int strlen=note_len_str.toString().length()*Integer.parseInt(parsevo.getBody_fz());    	
    	int note_h_1=Integer.parseInt(parsevo.getBody_fz())+6; 
    	int numrow_1=getNumRow(strlen);    	
    	if(numrow_1!=0) {
    		note_h_1=note_h_1*numrow_1;	
    	}
    	
    	row.setHeightInPoints(note_h_1);
    	HSSFCell cell = null;
    	
    	for (int i = 0; i < this.maxColnum; i++) {
    		cell = this.getCell(row, i);
    		cell.setCellStyle(style);
    		cell.setCellValue("");
    		
    	}
    	
    	HSSFRichTextString ts = new HSSFRichTextString(note_len_str.toString());
		for (int j = 0; j < itemList.size(); j++) {
			ArrayList list = (ArrayList) itemList.get(j);
			int start4 = ((Integer) list.get(0)).intValue();
			int end4 = ((Integer) list.get(1)).intValue();
			HSSFFont fon = (HSSFFont) list.get(2);
			if (fon != null) {
                ts.applyFont(start4, end4, fon);
            }
		}
		
		cell = this.getCell(row, 0);
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.LEFT);
		cell.setCellStyle(style);
		cell.setCellValue(ts);
		
		// 合并单元格
		ExportExcelUtil.mergeCell(sheet, this.rowNum, 0, this.rowNum, this.maxColnum - 1);
		
		this.rowNum ++;
    	
    	
    	if ("#u".equals(parsevo.getTile_u().trim()) || "#d".equals(parsevo.getTile_d().trim()) 
    			|| "#e".equals(parsevo.getTile_e().trim()) || "#t".equals(parsevo.getTile_t().trim()) 
    			|| "#p".equals(parsevo.getTile_p().trim()) || "#c".equals(parsevo.getTile_c().trim())){
    		
    		row = this.getRow(this.rowNum);
    		row.setHeightInPoints(Float.parseFloat(tile_height));

    		for (int i = 0; i < this.maxColnum; i++) {
        		cell = this.getCell(row, i);
        		cell.setCellStyle(style);
        		cell.setCellValue("");
        		
        	}
    		// 创建标题的字体
    		HSSFFont titleFont = daily.getFont(parsevo.getTile_fn(), 
        		parsevo.getTile_fb(), parsevo.getTile_fi(), 
        		parsevo.getTile_fu(), parsevo.getTile_fz(), 
        		null, workbook);
    		style = this.getStyle(1);
    		style.setFont(headFont);
    	 

	    	// 有几个列
	        int i = 0;
	        if ("#u".equals(parsevo.getTile_u().trim())) {
	        	i ++;
	        }
	        if("#d".equals(parsevo.getTile_d().trim())) {
	        	i ++;
	        }
	        if("#e".equals(parsevo.getTile_e().trim())) {
	        	i ++;
	        }
	        if("#t".equals(parsevo.getTile_t().trim())) {
	        	i ++;
	        }
	        if("#p".equals(parsevo.getTile_p().trim())) {
	        	i ++;
	        }
	        if("#c".equals(parsevo.getTile_c().trim())) {
	        	i ++;
	        }
	      
	        int j = 1;
	        
	        // 制作人单位
	        if("#u".equals(parsevo.getTile_u().trim())) {
	     	   
	     	   String u_code = "";
	     	   if(!userView.isSuper_admin()) {
	 			  if(userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0) {
	 				 u_code = userView.getUserOrgId();
	 			  } else {
	 				 u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
	 			  }
	 		   }
	     	   String [] u_codeitem=getCodeItemDesclow(u_code);
	     	   String u_str = "制作人单位: "+u_codeitem[0];
	     	   
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(u_str);
			   cell.setCellStyle(style);
			   j ++;
	        }
	        
	        // 制作日期
	        if("#d".equals(parsevo.getTile_d().trim())) {
	     	   String d_str="制作日期:"+PubFunc.getStringDate("yyyy.MM.dd");
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(d_str);
			   cell.setCellStyle(style);
			   j ++;  
	        }
	        
	        // 制作人
	        if("#e".equals(parsevo.getTile_e().trim())) {
	     	   
	     	   String e_str="制作人: "+this.userView.getUserFullName();
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(e_str);
			   cell.setCellStyle(style);
			   j ++;   
	        }
	        
	        // 制作时间
	        if("#t".equals(parsevo.getTile_t().trim())) {
	     	   String t_str="时间:"+PubFunc.getStringDate("HH:mm:ss");
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(t_str);
			   cell.setCellStyle(style);
			   j ++;
	        }
	        
	        // 页码
	        if("#p".equals(parsevo.getTile_p().trim())) {
	     	   
	     	   String p_str="页码:1";
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(p_str);
			   cell.setCellStyle(style);
			   j ++;
	        }
	        // 总页码
	        if("#c".equals(parsevo.getTile_c().trim())) {    	   
	     	   String c_str="总页码:1";
	     	   cell = this.getCell(row, (j - 1) * (this.maxColnum / i));
			   
			   cell.setCellValue(c_str);
			   cell.setCellStyle(style);
			   j ++;
	        }
	        
	        // 创建合并单元格
            for (int m = 1; m <= i; m++) {
                if (m < i) {
                    ExportExcelUtil.mergeCell(sheet, this.rowNum, (m - 1) * (maxColnum/i), this.rowNum, m * (maxColnum/i) - 1);          
                } else {
                    ExportExcelUtil.mergeCell(sheet, this.rowNum, (m - 1) * (maxColnum/i), this.rowNum, maxColnum - 1);
                }
            }
	        
	        this.rowNum ++ ;
	    }
    }
    /*
     * 页面设置属性，返回
     */
    private String getTurnPageCode(int curPage,String turnpage_sytle,int sum_page)
    {
    	StringBuffer code=new StringBuffer("");
    	code.append("<table width='50%' height='30' align='center' style='"+turnpage_sytle+"'>");
		code.append("<tr><td align='center'> \n");
		code.append("&nbsp;&nbsp;<input type='button' value='生成PDF' onclick='createPDF()' class='mybutton'>");
		code.append("&nbsp;&nbsp;<input type='button' value='生成Excel' onclick='createEXCEL()' class='mybutton'>");		
		code.append("&nbsp;&nbsp;<input type='button' value='页面设置' onclick='pagepar()' class='mybutton'>");
	    code.append("&nbsp;&nbsp;<input type='button' name='btnreturn' value='返回' onclick='go_back();' class='mybutton'>");      
	    code.append("</td></tr></table>");
    	return code.toString();	
    }
    
    /**
     * 查询考勤期间的所有指标
     * @return
     */
    private HashMap querryKq_item () {
    	HashMap map = new HashMap();
    	String sql = "select item_symbol,fielditemid from kq_item";
    	ContentDAO dao = new ContentDAO(this.conn);
    	ResultSet rs = null;
    	try {
			rs = dao.search(sql);
			while (rs.next()) {
				String key = rs.getString("fielditemid");
				String value = rs.getString("item_symbol");
				if (key != null) {
					map.put(key.toLowerCase(), value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			PubFunc.closeDbObj(rs);
		}
    	return map;
    }
 
	public int getMaxColnum() {
		return maxColnum;
	}

	public void setMaxColnum(int maxColnum) {
		this.maxColnum = maxColnum;
	}

    public String getCurTab() {
        return curTab;
    }

    public void setCurTab(String curTab) {
        this.curTab = curTab;
    }
}
