package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:预警提示</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-05-11 11:26:54</p>
 * @author LiWeichao
 * @version 5.0
 */
public class ExportExcelTrans extends IBusiness {


	public void execute() throws GeneralException {
		String outputFile = PubFunc.getStrg() + ".xls";
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(ResourceFactory
				.getProperty("system.options.itemwarn"));
		HSSFRow row = null;
		HSSFCell cell = null;
		HSSFRichTextString hts = null;
		int n = 0;
        String fieldItemclumn=(String)this.getFormHM().get("fieldItemclumn");
		String level = (String) this.getFormHM().get("level");
		level=level==null||level.length()<1?"0":level;
		int uplevel = Integer.parseInt(level);
		
		String title = (String) this.getFormHM().get("title");
		title = title == null || title.length() < 1 ? "" : SafeCode
				.decode(title);
		String sql = (String) this.getFormHM().get("sql");
		sql = sql == null || sql.length() < 1 ? "" : SafeCode.decode(sql);
		sql +=" "+ (String) this.getFormHM().get("order");
		
		String warntype = (String)this.getFormHM().get("warntype");
		
		String str = (String) this.getFormHM().get("columns");
		//zhangh 2019-11-22 单位、部门、岗位、姓名已经内置了，不需要额外再加
		if(StringUtils.isNotBlank(str)){
			//统一将新加的单位、部门、岗位、姓名指标去除掉
			str = str.replace("B0110","").replace("E0122","").replace("E01A1","").replace("A0101","");
		}
		String[] columnList = null;
		if (str != null && str.length() > 0) {
			if("0".equals(warntype))
				columnList = str.replaceAll("a0101,b0110,e0122,e01a1,a0100,nbase,", "").split(",");
			else if("1".equals(warntype))
				columnList = str.replaceAll("b0110,", "").split(",");
			else if("2".equals(warntype))
				columnList = str.replaceAll("e01a1,e0122,", "").split(",");
		}
		if (columnList == null)
			columnList = new String[0];
		row = sheet.createRow(n);
		int tmp=1;
		if("0".equals(warntype))
			tmp=4;
		else if("2".equals(warntype))
			tmp=3;
		int size=0;
		for(int i=0;i<columnList.length;i++){
			//人员预警 内置了姓名字段，这里剔除，要不然表头计算合并会多一列 guodd 2018-12-06
			if("A01A0101".equalsIgnoreCase(columnList[i])||"A01E0122".equalsIgnoreCase(columnList[i])||"A01B0110".equalsIgnoreCase(columnList[i])||"A01E01A1".equalsIgnoreCase(columnList[i])||"B01E01A1".equalsIgnoreCase(columnList[i])||"B01B0110".equalsIgnoreCase(columnList[i])||"B01E0122".equalsIgnoreCase(columnList[i])||"K01E01A1".equalsIgnoreCase(columnList[i])||"K01E0122".equalsIgnoreCase(columnList[i])||"K01B0110".equalsIgnoreCase(columnList[i])||columnList[i].length()<5)
				size++;
		}
		headerAdd(row, sheet, wb, title, columnList.length-size + tmp);// 设置头

		//n = (short) (n + 2);
		n = (short) (n + 1);//liuy 2015-4-8 8580：主页-预警信息-输出Excel(输出的标题和内容中间有一行空格)
		HSSFCellStyle style = createStyle(wb);
		HSSFCellStyle style1 = createStyle1(wb);
		HSSFCellStyle styleInteger = createStyleInteger(wb);
		row = sheet.createRow(n++);
		int tmpRow=0;
		cell = row.createCell(tmpRow++);
		hts = new HSSFRichTextString(ResourceFactory
				.getProperty("kh.field.seq"));
		cell.setCellValue(hts);
		cell.setCellStyle(style);
		if("0".equals(warntype)){
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("column.sys.org"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("label.title.dept"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("column.sys.pos"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("label.title.name"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
		}else if("1".equals(warntype)){
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("column.sys.org"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
		}else if("2".equals(warntype)){
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("column.sys.org"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("label.title.dept"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
			cell = row.createCell(tmpRow++);
			hts = new HSSFRichTextString(ResourceFactory
					.getProperty("column.sys.pos"));
			cell.setCellValue(hts);
			cell.setCellStyle(style);
		}
		HashMap itemMap=new HashMap();
		fieldItemclumn=fieldItemclumn!=null?fieldItemclumn:"";
		
		String otheritem[]=fieldItemclumn.split(",");
		size=0;
		for(int i = 0; i < otheritem.length; i++)
		{
			if("e0122".equalsIgnoreCase(otheritem[i]))
				continue;
			if("b0110".equalsIgnoreCase(otheritem[i]))
				continue;
			if("e01a1".equalsIgnoreCase(otheritem[i]))
				continue;
			//人员预警 内置了姓名字段，这里剔除，否则姓名列会重复显示 guodd 2018-12-06
			if("a0101".equalsIgnoreCase(otheritem[i]))
				continue;
			FieldItem item=DataDictionary.getFieldItem(otheritem[i]);
			if(item!=null)
			{
				itemMap.put(otheritem[i], item);
				cell = row.createCell(tmpRow + size);
				hts = new HSSFRichTextString(item.getItemdesc());
				cell.setCellValue(hts);
				cell.setCellStyle(style);
				size++;
			}
		}
		/*for (int i = 0; i < columnList.length; i++) {
			cell = row.createCell(tmpRow + i);
			
			hts = new HSSFRichTextString(columnList[i]);
			cell.setCellValue(hts);
			cell.setCellStyle(style);	
		}*/
		
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql);
			int r = n - 2;
			while (rs.next()) {
				row = sheet.createRow(n++);
				cell = row.createCell(0);
				cell.setCellValue(++r);
				cell.setCellStyle(style);
				int tmpCols=1;
				if("1".equals(warntype)){
					cell = row.createCell(tmpCols++);
					CodeItem codeItem = null;
					if(uplevel>0)
						codeItem = AdminCode.getCode("UM", rs.getString("b0110"),uplevel);
					else
						codeItem = AdminCode.getCode("UM", rs.getString("b0110"));
					if(codeItem==null)
						codeItem = new CodeItem();
					String tmpB0110 = AdminCode.getCodeName("UN", rs.getString("b0110"));
					if(codeItem.getCodename()!=null)
						tmpB0110+=codeItem.getCodename();
					hts = new HSSFRichTextString(tmpB0110);
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
				}
				else if("2".equals(warntype)){
					cell = row.createCell(tmpCols++);
					String e01a1 = (String)rs.getString("e01a1");
					String tmpB0110 =null;
					for(int i=e01a1.length()-1;i>0;i--){
						String codeitemid = e01a1.substring(0,i);
						tmpB0110 = AdminCode.getCodeName("UN", codeitemid);
						if(tmpB0110!=null&&tmpB0110.length()>0){
							break;
						}
					}
					hts = new HSSFRichTextString(tmpB0110);
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
					
					cell = row.createCell(tmpCols++);
					CodeItem code = null;
					if(uplevel>0)
						code = AdminCode.getCode("UM", rs.getString("e0122"),uplevel);
					else
						code = AdminCode.getCode("UM", rs.getString("e0122"));
					if(code==null)
						code = new CodeItem();
					hts = new HSSFRichTextString(code.getCodename());
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
					cell = row.createCell(tmpCols++);
					hts = new HSSFRichTextString(AdminCode.getCodeName("@K", rs
							.getString("e01a1")));
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
				}else if("0".equals(warntype)){
					
					cell = row.createCell(tmpCols++);
					hts = new HSSFRichTextString(AdminCode.getCodeName("UN", rs
							.getString("b0110")));
					cell.setCellValue(hts);
					cell.setCellStyle(style);
					cell = row.createCell(tmpCols++);
					CodeItem code = null;
					if(uplevel>0)
						code = AdminCode.getCode("UM", rs.getString("e0122"),uplevel);
					else
						code = AdminCode.getCode("UM", rs.getString("e0122"));
					if(code==null)
						code = new CodeItem();
					hts = new HSSFRichTextString(code.getCodename());
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
					cell = row.createCell(tmpCols++);
					hts = new HSSFRichTextString(AdminCode.getCodeName("@K", rs
							.getString("e01a1")));
					cell.setCellValue(hts);
					cell.setCellStyle(style1);
					cell = row.createCell(tmpCols++);
					hts = new HSSFRichTextString(rs.getString("a0101"));
					cell.setCellValue(hts);
					cell.setCellStyle(style);
				}
				int index=0;
				for (int i = 0; i < columnList.length; i++) {
					if("A01B0110".equalsIgnoreCase(columnList[i])|| "B01B0110".equalsIgnoreCase(columnList[i])|| "K01B0110".equalsIgnoreCase(columnList[i]))
						continue;
					if("A01E0122".equalsIgnoreCase(columnList[i])|| "B01E0122".equalsIgnoreCase(columnList[i])|| "K01E0122".equalsIgnoreCase(columnList[i]))
						continue;
					if("A01E01A1".equalsIgnoreCase(columnList[i])|| "B01E01A1".equalsIgnoreCase(columnList[i])|| "K01E01A1".equalsIgnoreCase(columnList[i]))
						continue;
					//人员预警 内置了姓名字段，这里剔除，否则姓名列会重复显示 guodd 2018-12-06
					if("A01A0101".equalsIgnoreCase(columnList[i]))
						continue;
					if(columnList[i].length()<5)
						continue;
					Object obj = rs.getObject(columnList[i]);
					obj=obj==null?"":obj;
					cell = row.createCell(tmpCols + index);
					cell.setCellStyle(style);
					index++;
					if (obj instanceof Date || obj instanceof java.sql.Date) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						hts = new HSSFRichTextString(sdf.format(obj));
						cell.setCellValue(hts);
					} else if (obj instanceof Double || obj instanceof Integer
							|| obj instanceof Float) {
						cell.setCellValue(Double.parseDouble(obj.toString()));
						cell.setCellStyle(styleInteger);
					} else {
						FieldItem item=(FieldItem)itemMap.get(otheritem[i]);
						if(item!=null&&!"0".equals(item.getCodesetid())&&obj.toString().length()>0)
						{
							String vv=AdminCode.getCodeName(item.getCodesetid(), obj.toString());
							hts = new HSSFRichTextString(vv);
						}else
						{
							hts = new HSSFRichTextString(obj.toString());
						}
						
						cell.setCellValue(hts);
					}
				}
			}

			for (int i = 0; i <= cell.getColumnIndex(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
			}

			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outputFile);
			wb.write(fileOut);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(fileOut);
			//xus 20/4/28 vfs 改造
//			outputFile = SafeCode.encode(PubFunc.encrypt(outputFile));
			outputFile = PubFunc.encrypt(outputFile);
			this.getFormHM().put("filename", outputFile);
		}
	}

	// 设置标题(tableName:标题名、size:生成Excel的列数)
	private void headerAdd(HSSFRow row, HSSFSheet sheet, HSSFWorkbook wb,
			String tableName, int size) {
		try {
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) size);// 跨一行duo列
			HSSFCell cell1 = row.createCell(0);
			HSSFRichTextString hts = new HSSFRichTextString(tableName);
			cell1.setCellValue(hts);
			HSSFCellStyle style = wb.createCellStyle();
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 18);
			style.setFont(font);
			style.setAlignment(HorizontalAlignment.CENTER);
			cell1.setCellStyle(style);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置单元格和字体样式
	private HSSFCellStyle createStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(false);// 自动换行
		return style;
	}
	
	// 设置单元格和字体样式
	private HSSFCellStyle createStyle1(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(false);// 自动换行
		return style;
	}

	// 设置数值型样式，居右显示 guodd 2020-02-11
	private HSSFCellStyle createStyleInteger(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(false);// 自动换行
		return style;
	}

}