package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:ExportExcelSummaryTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 12, 2011 2:06:05 PM</p>
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class ExportExcelSummaryTrans extends IBusiness{

	public void execute() throws GeneralException {
		String p0201=(String)this.getFormHM().get("p0201");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap extendattrMap=getExtendattr(dao,p0201);//推荐参数
		StringBuffer sql=new StringBuffer();
		String dbpres=(String)extendattrMap.get("dbpres");
		if(dbpres!=null&&dbpres.length()>0){
			String[] dbpre=dbpres.split(",");
			for (int i = 0; i < dbpre.length; i++) {
				if(i>0)
					sql.append(" union ");
				sql.append("select p.*,a.e01a1 from p03 p,"+dbpre[i]+"A01 a");
				sql.append(" where a.a0100=p.a0100 and p0201="+p0201);
			}
		}else{
			sql.append("select * from p03 where p0201="+p0201);
		}
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		try{
			wb = exportExcel();
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row = null;
			HSSFCell cell = null;
			
			HSSFCellStyle style = setHeadStyle(wb);
			String bodys=(String)extendattrMap.get("bodys");
			String codes=(String)extendattrMap.get("codes");
			HSSFRow row3 = sheet.getRow(3);
			HSSFRow row4 = sheet.getRow(4);
			int col=3,heji=3,sum=3;
			
			if(codes!=null&&codes.length()>0){
				String cs[]=codes.split(",");
				String tmp[]=bodys.split(",");
				if (cs.length*tmp.length>253){//excel列不能大于256列
				    throw GeneralExceptionHandler.Handle(new Exception("后备干部汇总表列数大于256，不能导出！"));
				}
				for (int i = 0; i <cs.length; i++) {
					cell=row3.createCell(col);
					cell.setCellValue(AdminCode.getCodeName("@K", PubFunc.nullToStr(cs[i])));
					HSSFCell cell4=null;
					if(bodys!=null&&bodys.length()>0){
						String bs[]=bodys.split(",");
						ExportExcelUtil.mergeCell(sheet, 3, (short) (col), 3, (short) (col+bs.length));
						int jj=cell.getColumnIndex();
						for (int j = 0; j < bs.length; j++) {
							cell4=row4.createCell(jj+j);
							cell4.setCellValue(getBody(bs[j]));
						}
						cell4=row4.createCell(jj+bs.length);
						cell4.setCellValue("小计");
						col=cell4.getColumnIndex()+1;
					}else
						col=cell.getColumnIndex()+1;
				}
			}
			cell=row3.createCell(col);
			cell.setCellValue("合计");
			heji=cell.getColumnIndex();
			int temp=3;
			if(bodys!=null&&bodys.length()>0){
				String bs[]=bodys.split(",");
				ExportExcelUtil.mergeCell(sheet, 3, (short) (col), 3, (short) (col+bs.length));
				int jj=cell.getColumnIndex();
				for (int j = 0; j < bs.length; j++) {
					cell=row4.createCell(jj+j);
					cell.setCellValue(getBody(bs[j]));
				}
				temp=jj+bs.length;
			}
			cell=row4.createCell(temp);
			cell.setCellValue("总计");
			sum=cell.getColumnIndex();
			ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short) sum);
			for (int i = 3; i <= sum; i++) {
				cell = row3.getCell(i);
				if(cell==null)cell=row3.createCell(i);
				cell.setCellStyle(style);
				cell = row4.getCell(i);
				if(cell==null)cell=row4.createCell(i);
				cell.setCellStyle(style);
				sheet.setColumnWidth(i,1500);
			}
			
			this.frecset=dao.search(sql.toString());
			if(this.frecset.next()){
				row = sheet.getRow(2);
				cell = row.getCell((short)0);
				cell.setCellValue("单位："+AdminCode.getCodeName("UN", PubFunc.nullToStr(this.frecset.getString("b0110"))));
				style = setColumnStyle(wb);
				int r=5,i=1;
				do{
					row = sheet.createRow(r);
					cell = row.createCell(0);
					cell.setCellStyle(style);
					cell.setCellValue(i);
					cell = row.createCell(1);
					cell.setCellStyle(style);
					cell.setCellValue(this.frecset.getString("a0101"));
					cell = row.createCell(2);
					cell.setCellStyle(style);
					String e01a1=dbpres!=null&&dbpres.length()>0?AdminCode.getCodeName("@K",PubFunc.nullToStr(this.frecset.getString("e01a1"))):"";
					cell.setCellValue(e01a1);
					
					String p0308=this.frecset.getString("p0308");
					if(p0308!=null&&p0308.length()>0&&codes!=null&&codes.length()>0){
						String cs[]=codes.split(",");
						for (int j = 0; j < cs.length; j++) {
							if(p0308.equals(cs[j])){
								if(bodys!=null&&bodys.length()>0){
									String bs[]=bodys.split(",");
									int kk=3+(j*bs.length);
									if(j!=0)kk+=j;
									for (int k = 0; k < bs.length; k++) {
										if(bs[k]!=null&&bs[k].length()>0&&this.frecset.getInt("C_"+bs[k])>0){
											cell=row.createCell(kk+k);
											cell.setCellValue(this.frecset.getInt("C_"+bs[k]));
										}
									}
									if(this.frecset.getInt("p0304")>0){
										cell=row.createCell(kk+bs.length);
										cell.setCellValue(this.frecset.getInt("p0304"));
									}
								}
							}
						}
					}
					if(bodys!=null&&bodys.length()>0){
						String bs[]=bodys.split(",");
						for (int j = 0; j < bs.length; j++) {
							if(bs[j]!=null&&bs[j].length()>0&&this.frecset.getInt("C_"+bs[j])>0){
								cell=row.createCell(heji+j);
								cell.setCellValue(this.frecset.getInt("C_"+bs[j]));
							}
						}
					}
					if(this.frecset.getInt("p0304")>0){
						cell=row.createCell(sum);
						cell.setCellValue(this.frecset.getInt("p0304"));
					}
					row.setHeight((short)600);
					for (int j = 3; j <= sum; j++) {
						cell = row.getCell(j);
						if(cell==null)cell=row.createCell(j);
							cell.setCellStyle(style);
					}
					i++;r++;
				}while(this.frecset.next());
			}
			String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+".xls";//recommendCadreSummary.xls
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();
			fileName=PubFunc.encrypt(fileName);
			this.getFormHM().put("outName", fileName);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}
	}
	
	/**
	 * 获取推荐参数信息
	 * @param p0201
	 * @return
	 */
	private HashMap getExtendattr(ContentDAO dao,String p0201){
		HashMap extMap=new HashMap();
		String sql="select extendattr from p02 where p0201="+p0201;
		try {
			String extendattr="";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				 extendattr=this.frowset.getString("extendattr");
			}
			if(extendattr!=null&&extendattr.length()>10){
				Document doc=DocumentHelper.parseText(extendattr);
				Element root = doc.getRootElement();
				extMap.put("bodys",root.element("body_list").attributeValue("bodys"));
				extMap.put("codes", root.element("pos_list").attributeValue("codes"));
				String dbpres = root.elementText("nbase");
				dbpres=dbpres==null?"":dbpres.toUpperCase();//cs人员库取值是大写
				extMap.put("dbpres", dbpres);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extMap;
	}
	
	/**
	 * 投票人类别名称
	 * @param body 类别id
	 * @return 名称
	 * @throws SQLException
	 */
	private String getBody(String body) throws SQLException{
		String b="";
		if(body!=null&&body.length()>0){
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frecset=dao.search("select name from per_mainbodyset where body_id="+body);
			if(this.frecset.next())
				b=this.frecset.getString("name");
		}
		return b;
	}
	
	/**
	 * head样式
	 * @param workbook HSSFWorkbook
	 * @return 11字体 加粗 细边框 居中
	 */
	private HSSFCellStyle setHeadStyle(HSSFWorkbook workbook){
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBold(true);
		font.setFontHeightInPoints((short)11);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(true);
		return cellStyle;
	}
	
	/**
	 * 内容样式
	 * @param workbook HSSFWorkbook
	 * @return 细边框 居中
	 */
	private HSSFCellStyle setColumnStyle(HSSFWorkbook workbook){
		HSSFCellStyle abStyle=workbook.createCellStyle();
		abStyle.setAlignment(HorizontalAlignment.CENTER);
		abStyle.setBorderBottom(BorderStyle.THIN);
		abStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderLeft(BorderStyle.THIN);
		abStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderRight(BorderStyle.THIN);
		abStyle.setRightBorderColor(HSSFColor.BLACK.index);
		abStyle.setBorderTop(BorderStyle.THIN);
		abStyle.setTopBorderColor(HSSFColor.BLACK.index);
		abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		abStyle.setWrapText(true);
		return abStyle;
	}
	
	/**
	 * title样式
	 * @param workbook
	 * @return 11号字体加粗 居中
	 */
	private HSSFCellStyle setTitleStyle(HSSFWorkbook workbook){
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBold(true);
		font.setFontHeightInPoints((short)15);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(false);
		return cellStyle;
	}
	
	/**
	 * 创建Excel模版
	 * @return HSSFWorkbook
	 * @throws Exception
	 */
	private HSSFWorkbook exportExcel() throws Exception{
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("大会推荐");
		HSSFRow row=null;
		HSSFCell csCell=null;
		int rowIndex=1;
		
		HSSFCellStyle style = setTitleStyle(workbook);
		row=sheet.createRow(rowIndex);
		row.setHeight((short)900);
		csCell=row.createCell(0);
		csCell.setCellStyle(style);
		HSSFRichTextString textstr = new HSSFRichTextString("民主推荐后备干部汇总表（大会）");
		csCell.setCellValue(textstr);
		rowIndex++;
		
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBold(true);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		row=sheet.createRow(rowIndex);
		csCell=row.createCell(0);
		row.setHeight((short)400);
		csCell.setCellStyle(cellStyle);
		textstr = new HSSFRichTextString("单位：");
		csCell.setCellValue(textstr);
		ExportExcelUtil.mergeCell(sheet, rowIndex, (short) 0, rowIndex, (short) 2);
		rowIndex++;
		
		style=setHeadStyle(workbook);
		row=sheet.createRow(rowIndex);
		row.setHeight((short)850);
		csCell=row.createCell(0);
		csCell.setCellStyle(style);
		textstr = new HSSFRichTextString("序号");
		csCell.setCellValue(textstr);
		
		style=setHeadStyle(workbook);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.TOP);
		
		csCell=row.createCell(1);
		row.setHeight((short)950);
		csCell.setCellStyle(style);
		StringBuffer test=new StringBuffer();
		test.append("\r\n　　　　　　　　　　　推荐职务\r\n\r\n");
		test.append("　　　　　　　推荐情况\r\n");
		test.append("　　　职务\r\n");
		test.append("人选");
		textstr = new HSSFRichTextString(test.toString());
		csCell.setCellValue(textstr);
		ExportExcelUtil.mergeCell(sheet, rowIndex, (short) 0, rowIndex+1, (short) 0);
		ExportExcelUtil.mergeCell(sheet, rowIndex, (short) 1, rowIndex+1, (short) 2);
		csCell=row.createCell(2);
		csCell.setCellStyle(style);
		rowIndex++;
		
		//创建斜线
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFClientAnchor anchor = new HSSFClientAnchor();
		anchor.setAnchor((short)1, 3, 0, 0, (short)2, 4, 2000, 00);
		patriarch.createSimpleShape(anchor);
		anchor = new HSSFClientAnchor();
		anchor.setAnchor((short)1, 3, 0, 0, (short)2, 4, 500, 2000);
		patriarch.createSimpleShape(anchor);
		anchor = new HSSFClientAnchor();
		anchor.setAnchor((short)1, 3, 0, 0, (short)2, 4, 10, 2000);
		patriarch.createSimpleShape(anchor);
		
		row=sheet.createRow(rowIndex);
		row.setHeight((short)950);
		for (int i = 0; i < 3; i++) {
			csCell=row.createCell(i);
			csCell.setCellStyle(style);
		}
		sheet.setColumnWidth(0,(short)1000);
		sheet.setColumnWidth(1,(short)2000);
		sheet.setColumnWidth(2,(short)7500);
		sheet.createFreezePane(3, 5);//冻结窗口3列5行
			
		return workbook;
	}
}
