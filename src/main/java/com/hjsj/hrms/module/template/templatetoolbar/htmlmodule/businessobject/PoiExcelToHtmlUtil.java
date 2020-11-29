package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject;

import com.aspose.words.HorizontalAlignment;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

/**
 * syl 
 * 20200715
 * 功能：主要用於適配 導出HTML功能模塊。
 * 主要用於讀取excel表格，通過poi接口，将其转换成html形式的字符串
 * @author GH
 *
 */
public class PoiExcelToHtmlUtil {
	private Map<String, Object>[] map;
	public void init(Sheet sheet){
		map=getRowSpanColSpanMap(sheet);	
	}
	/**
	 * 处理表格样式
	 * @param wb
	 * @param sheet
	 * @param cell
	 * @param sb
	 * @param rowHeight 
	 */
	public void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuffer sb, int rowHeight){
		CellStyle cellStyle = cell.getCellStyle();
		if (cellStyle != null) {
			short alignment = cellStyle.getAlignment();
			sb.append("align='" + convertAlignToHtml(alignment) + "' ");//单元格内容的水平对齐方式
			VerticalAlignment verticalAlignment = cellStyle.getVerticalAlignmentEnum();
			sb.append("valign='"+ convertVerticalAlignToHtml(verticalAlignment)+ "' ");//单元格中内容的垂直排列方式
			if (wb instanceof XSSFWorkbook) {
				XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
				//syl
				boolean boldWeight = xf.getBold();//是否加粗
				sb.append("style='");
				if(boldWeight){
					sb.append("font-weight:bold;"); // 字体加粗
				}
				sb.append("height:" + rowHeight/256*xf.getFontHeight()/20 + "pt;");
				sb.append("font-size: " + xf.getFontHeight() / 2 + "%;"); // 字体大小
				int topRow = cell.getRowIndex(),topColumn = cell.getColumnIndex();
				if(map[0].containsKey(topRow+","+topColumn)){//该单元格为合并单元格，宽度需要获取所有单元格宽度后合并
					String value = (String)map[0].get(topRow+","+topColumn);
					String[] ary = value.split(",");
					int bottomColumn = Integer.parseInt(ary[1]);
					if(topColumn!=bottomColumn){//合并列，需要计算相应宽度
						int columnWidth = 0;
						for(int i=topColumn;i<=bottomColumn;i++){
							columnWidth += sheet.getColumnWidth(i);
						}
						sb.append("width:" + columnWidth/256*xf.getFontHeight()/20 + "pt;");
					}else{
						int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
						sb.append("width:" + columnWidth/256*xf.getFontHeight()/20 + "pt;");
					}
				}else{
					int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
					sb.append("width:" + columnWidth/256*xf.getFontHeight()/20 + "pt;");
				}
				XSSFColor xc = xf.getXSSFColor();
				if (xc != null && !"".equals(xc.toString())) {
					sb.append("color:#" + xc.getARGBHex().substring(2) + ";"); // 字体颜色
				}
				XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
				if (bgColor != null && !"".equals(bgColor.toString())) {
					sb.append("background-color:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
				}
//				sb.append("border:solid #000000 1px;");
			}else if(wb instanceof HSSFWorkbook){
				HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
				//syl
				short boldWeight = hf.getFontHeight();
				short fontColor = hf.getColor();
				sb.append("style='");
				HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
				HSSFColor hc = palette.getColor(fontColor);
				sb.append("height:" + rowHeight/256*hf.getFontHeight()/20 + "pt;");
				sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
				sb.append("font-size: " + hf.getFontHeight() / 2 + "%;"); // 字体大小
				String fontColorStr = convertToStardColor(hc);
				if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
					sb.append("color:" + fontColorStr + ";"); // 字体颜色
				}
				int topRow = cell.getRowIndex(),topColumn = cell.getColumnIndex();
				if(map[0].containsKey(topRow + "," + topColumn)){//该单元格为合并单元格，宽度需要获取所有单元格宽度后合并
					String value = (String)map[0].get(topRow + "," + topColumn);
					String[] ary = value.split(",");
					int bottomColumn = Integer.parseInt(ary[1]);
					if(topColumn != bottomColumn){//合并列，需要计算相应宽度
						int columnWidth = 0;
						for(int i = topColumn; i <= bottomColumn; i++){
							columnWidth += sheet.getColumnWidth(i);
						}
						sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
					}else{
						int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
						sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
					}
				}else{
					int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
					sb.append("width:" + columnWidth / 256 * hf.getFontHeight() / 20 + "pt;");
				}
				short bgColor = cellStyle.getFillForegroundColor();
				hc = palette.getColor(bgColor);
				String bgColorStr = convertToStardColor(hc);
				if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
					sb.append("background-color:" + bgColorStr + ";"); // 背景颜色
				}
//				sb.append("border:solid #000000 1px;");
			}
			sb.append("' ");
		}
	}
	/**
	 * 分析excel表格，记录合并单元格相关的参数，用于之后html页面元素的合并操作
	 * @param sheet
	 * @return
	 */
	private Map<String, Object>[] getRowSpanColSpanMap(Sheet sheet) {
		Map<String, String> map0 = new HashMap<String, String>();	//保存合并单元格的对应起始和截止单元格
		Map<String, String> map1 = new HashMap<String, String>();	//保存被合并的那些单元格
		Map<String, Integer> map2 = new HashMap<String, Integer>();	//记录被隐藏的单元格个数
		Map<String, String> map3 = new HashMap<String, String>();	//记录合并了单元格，但是合并的首行被隐藏的情况
		int mergedNum = sheet.getNumMergedRegions();
		CellRangeAddress range = null;
		Row row = null;
		for (int i = 0; i < mergedNum; i++) {
			range = sheet.getMergedRegion(i);
			int topRow = range.getFirstRow();
			int topCol = range.getFirstColumn();
			int bottomRow = range.getLastRow();
			int bottomCol = range.getLastColumn();
			/**
			 * 此类数据为合并了单元格的数据
			 * 1.处理隐藏（只处理行隐藏，列隐藏poi已经处理）
			 */
			if(topRow != bottomRow){
				int zeroRoleNum = 0;
				int tempRow = topRow;
				for(int j = topRow; j <= bottomRow; j ++){
					row = sheet.getRow(j);
					if(row.getZeroHeight() || row.getHeight() == 0){
						if(j == tempRow){
							//首行就进行隐藏，将rowTop向后移
							tempRow ++;
							continue;//由于top下移，后面计算rowSpan时会扣除移走的列，所以不必增加zeroRoleNum;
						}
						zeroRoleNum ++;
					}
				}
				if(tempRow != topRow){
					map3.put(tempRow + "," + topCol,topRow + "," + topCol);
					topRow = tempRow;
				}
				if(zeroRoleNum!=0) map2.put(topRow + "," + topCol, zeroRoleNum);
			}
			map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
			int tempRow = topRow;
			while (tempRow <= bottomRow) {
				int tempCol = topCol;
				while (tempCol <= bottomCol) {
					map1.put(tempRow + "," + tempCol, topRow + "," + topCol);
					tempCol++;
				}
				tempRow++;
			}
			map1.remove(topRow + "," + topCol);
		}
		Map[] map = { map0, map1 ,map2,map3};
//		System.err.println(map0);
		return map;
	}
	
	/**
	 * 单元格中内容的垂直排列方式
	 * @param verticalAlignment.
	 * @return
	 */
	private String convertVerticalAlignToHtml(VerticalAlignment verticalAlignment) {
		String valign = "middle";
		if(verticalAlignment.compareTo(VerticalAlignment.BOTTOM)==0){
			valign = "bottom";
		}else if(verticalAlignment.compareTo(VerticalAlignment.TOP)==0){
			valign = "top";
		}else if(verticalAlignment.compareTo(VerticalAlignment.CENTER)==0){
			valign = "center";
		}
		return valign;
	}

	private static String convertToStardColor(HSSFColor hc) {
		StringBuffer sb = new StringBuffer("");
		if (hc != null) {
			if (HSSFColor.AUTOMATIC.index == hc.getIndex()) {
				return null;
			}
			sb.append("#");
			for (int i = 0; i < hc.getTriplet().length; i ++) {
				sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
			}
		}
		return sb.toString();
	}
	
	private static String fillWithZero(String str) {
		if (str != null && str.length() < 2) {
			return "0" + str;
		}
		return str;
	}
	
	/**
	 * 单元格内容的水平对齐方式
	 * @param alignment
	 * @return
	 */
	private String convertAlignToHtml(short alignment) {
		String align = "left";
		switch (alignment) {
		case HorizontalAlignment.LEFT:
			align = "left";
			break;
		case HorizontalAlignment.CENTER:
			align = "center";
			break;
		case HorizontalAlignment.RIGHT:
			align = "right";
			break;
		default:
			break;
		}
		return align;
	}
	public void dealExcelFontStyle(Workbook wb, Sheet sheet, Cell cell,
			StringBuffer sb) {
		CellStyle cellStyle = cell.getCellStyle();
		if (cellStyle != null) {
			if (wb instanceof XSSFWorkbook) {
				XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
				//syl
				boolean boldWeight = xf.getBold();
				if(boldWeight){
					sb.append("font-weight:bold;"); // 字体加粗
				}
				sb.append("font-size: " + xf.getFontHeight() / 2 + "%;"); // 字体大小
				XSSFColor xc = xf.getXSSFColor();
				if (xc != null && !"".equals(xc.toString())) {
					sb.append("color:#" + xc.getARGBHex().substring(2) + ";"); // 字体颜色
				}
				XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
				if (bgColor != null && !"".equals(bgColor.toString())) {
					sb.append("background-color:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
				}
			}else if(wb instanceof HSSFWorkbook){
				HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
				//syl
				boolean boldWeight = hf.getBold();
				short fontColor = hf.getColor();
				HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
				HSSFColor hc = palette.getColor(fontColor);
				if(boldWeight){
					sb.append("font-weight:bold;"); // 字体加粗
				}
				sb.append("font-size: " + hf.getFontHeight() / 2 + "%;"); // 字体大小
				String fontColorStr = convertToStardColor(hc);
				if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
					sb.append("color:" + fontColorStr + ";"); // 字体颜色
				}
				short bgColor = cellStyle.getFillForegroundColor();
				hc = palette.getColor(bgColor);	
				String bgColorStr = convertToStardColor(hc);
				if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
					sb.append("background-color:" + bgColorStr + ";"); // 背景颜色
				}
			}
		}
	}
}
