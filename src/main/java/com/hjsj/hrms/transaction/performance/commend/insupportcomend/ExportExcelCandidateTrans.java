package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExportExcelCandidateTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String p0201 = (String)this.getFormHM().get("p0201");
		String autoNum=(String)this.getFormHM().get("autonum");
		
		if(autoNum==null||autoNum.length()<4){
			this.getFormHM().put("outName", "1");
		}else{
			try {
				String sql="select "+autoNum+" from p03 where p0201="+p0201;
				this.frecset=dao.search(sql);
				int flag=0;
				while(this.frecset.next()){
					if(this.frecset.getString(autoNum)==null||this.frecset.getString(autoNum).length()<1){
						flag=1;
						break;
					}
				}
				
				if(flag==1){
					this.getFormHM().put("outName", "1");
				}else{
					sql="select e0122,p0308,a0101,"+autoNum+" from p03 where p0201="+p0201+" order by "+autoNum;
					this.frecset=dao.search(sql);
					ArrayList list=new ArrayList();
					while(this.frecset.next()){
						String[] clm=new String[4];
						clm[0]=this.frecset.getString("e0122");
						clm[1]=this.frecset.getString("p0308");
						clm[2]=this.frecset.getString("a0101");
						clm[3]=this.frecset.getString(autoNum);
						list.add(clm);
					}
					
					String title="民主推荐后备干部人选";
					String time="",tool="";
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					sql="select p0203,p0206 from p02 where p0201="+p0201;
					this.frecset=dao.search(sql);
					if(this.frecset.next()){
						tool=this.frecset.getString("p0203");
						Date startime=this.frecset.getDate("p0206");
						time=startime!=null?format.format(startime):"";
					}
					tool=tool==null||tool.length()<1?userView.getUserFullName()+"_candidate.xls":tool+"名册";
					
					String b0110="";
					sql="select b0110 from p03 where p0201="+p0201+" group by b0110";
					this.frecset=dao.search(sql);
					int i=0;
					while(this.frecset.next()){
						if(i==0)
							b0110=this.frecset.getString("b0110");
						else
							b0110+=","+this.frecset.getString("b0110");
						i++;
					}
					String outName=exportExcel(tool,b0110,title,time,list);
					//xus 20/4/30 vfs改造
					outName=PubFunc.encrypt(outName);
					this.getFormHM().put("outName", outName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String exportExcel(String tool,String b0110,String titile,String time,ArrayList clmList) throws Exception{

		String fileName = null;
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook = null;
		try{
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			int rowIndex=0;

			HSSFCellStyle style = setTitleStyle(workbook);
			row=sheet.createRow(rowIndex);
			row.setHeight((short)1100);
			csCell=row.createCell(0);
			csCell.setCellStyle(style);
			HSSFRichTextString textstr = new HSSFRichTextString(titile);
			csCell.setCellValue(textstr);
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 3);
			rowIndex++;

			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			row=sheet.createRow(rowIndex);
			row.setHeight((short)650);
			csCell=row.createCell(0);
			csCell.setCellStyle(cellStyle);
			textstr = new HSSFRichTextString("单位："+getCodeName("UN",b0110));
			csCell.setCellValue(textstr);
			csCell=row.createCell(2);
			csCell.setCellStyle(cellStyle);
			textstr = new HSSFRichTextString("推荐时间："+time);
			csCell.setCellValue(textstr);
			ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short) 1);
			ExportExcelUtil.mergeCell(sheet, 1, (short) 2, 1, (short) 3);
			rowIndex++;

			style=setHeadStyle(workbook);
			row=sheet.createRow(rowIndex);
			row.setHeight((short)650);
			csCell=row.createCell(0);
			csCell.setCellStyle(style);
			textstr = new HSSFRichTextString("部门");
			csCell.setCellValue(textstr);
			csCell=row.createCell(1);
			csCell.setCellStyle(style);
			textstr = new HSSFRichTextString("职务");
			csCell.setCellValue(textstr);
			csCell=row.createCell(2);
			csCell.setCellStyle(style);
			textstr = new HSSFRichTextString("姓名");
			csCell.setCellValue(textstr);
			csCell=row.createCell(3);
			csCell.setCellStyle(style);
			textstr = new HSSFRichTextString("编号");
			csCell.setCellValue(textstr);
			rowIndex++;

			style = setColumnStyle(workbook);
			for (int j = 0; j < clmList.size(); j++) {
				row=sheet.createRow(rowIndex);
				row.setHeight((short)650);
				String[] clm=(String[]) clmList.get(j);
				for(int m=0;m<clm.length;m++){
					String value=clm[m];
					if(m==0)
						value=getCodeName("UM", value);
					if(m==1)
						value=getCodeName("@K", value);
					csCell=row.createCell(m);
					csCell.setCellStyle(style);
					textstr = new HSSFRichTextString(value);
					csCell.setCellValue(textstr);
				}
				rowIndex++;
			}



			for(int i = 0; i < 4; i++)
			{
				sheet.setColumnWidth(i,(short)6500);
			}
			//String fileName=userView.getUserFullName()+"_"+PubFunc.getStrg()+".xls";
			fileName = tool+".xls";
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			workbook.write(fileOut);
		}catch (Exception e){
			throw  e;
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	
	private HSSFCellStyle setHeadStyle(HSSFWorkbook workbook){
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBold(true);
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
		cellStyle.setWrapText(false);
		return cellStyle;
	}
	
	private HSSFCellStyle setTitleStyle(HSSFWorkbook workbook){
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBold(true);
		font.setFontHeightInPoints((short)24);
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(false);
		return cellStyle;
	}
	
	private HSSFCellStyle setColumnStyle(HSSFWorkbook workbook){
		HSSFFont afont = workbook.createFont();
		afont.setColor(HSSFFont.COLOR_NORMAL);
		afont.setBold(false);
		HSSFCellStyle abStyle=workbook.createCellStyle();
		abStyle.setFont(afont);
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
		abStyle.setWrapText(false);
		return abStyle;
	}
	
	private String getCodeName(String setid,String codeid){
		String codeName="";
		codeid=codeid==null?"":codeid;
		String[] strs=codeid.split(",");
		for (int i = 0; i < strs.length; i++) {
			String value=AdminCode.getCodeName(setid, strs[i]);
			if(i==0){
				codeName=value;
			}else{
				codeName+=","+value;
			}
		}
		return codeName;
	}
}
