package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.GzAccountBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		
		String changeFlag = (String)hm.get("flag");
		changeFlag = changeFlag!=null&&changeFlag.trim().length()>0?changeFlag:"0";
		int flag = Integer.parseInt(changeFlag);
		
		String salaryid = (String)hm.get("salaryid");
		 
		
		//如果用户没有当前薪资类别的资源权限   20140903  dengcan
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,null);
		
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		outExport(salaryid,flag);

	}
	 /**
     * 数据导出为excel
     * @param salaryid 薪资id
     * @param changeflag  (0、信息变动 1、新增人员 2、减少人员)
     */
	public void outExport(String salaryid,int changeflag){
		ContentDAO dao = new ContentDAO(this.frameconn);
		GzAccountBo gaccountbo = new GzAccountBo(this.frameconn,salaryid);
		String outname="exportChangeInfo_"+PubFunc.getStrg()+".xls";
		int rows = 0;
		int clum = 1;
		
		try{
			ArrayList fieldList = gaccountbo.fieldList();
			ArrayList beforeChange = gaccountbo.beforeChange();
			ArrayList afterChange = gaccountbo.afterChange();
			ArrayList varianceTotal = gaccountbo.varianceTotal(this.userView);
			ArrayList totalValue = gaccountbo.totalValue(this.userView,changeflag+"");
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			
			HSSFCellStyle titlestyle = style(workbook,0);
			HSSFCellStyle centerstyle = style(workbook,1);
			HSSFCellStyle leftstyle = style(workbook,2);
			HSSFCellStyle colorstyle = style(workbook,3);
			HSSFCellStyle bottomstyle = style(workbook,4);
			HSSFCellStyle rightstyle = style(workbook,5);
			
			int cells = 1;
			if(changeflag==0){
				cells = 4+fieldList.size();
			}else{
				cells = 3+fieldList.size();
			}
			ExportExcelUtil.mergeCell(sheet, 0, (short)0,0, (short)cells);
			row = sheet.createRow((short)0);
			
			csCell =row.createCell((short)0);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if(changeflag==0){
				rows = 1;
				clum = 2;
				csCell.setCellValue(ResourceFactory.getProperty("gz.info.change"));
			}else if(changeflag==1){
				csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.add.staff"));
			}
			else{
				csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.minus.staff"));
			}
			csCell.setCellStyle(titlestyle);
			
			row = sheet.createRow((short)1);
			csCell =row.createCell((short)0);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(centerstyle);

			csCell =row.createCell((short)1);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(ResourceFactory.getProperty("tree.unroot.undesc"));
			csCell.setCellStyle(centerstyle);
			
			csCell =row.createCell((short)2);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(ResourceFactory.getProperty("tree.umroot.umdesc"));
			csCell.setCellStyle(centerstyle);
			
			csCell =row.createCell((short)3);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(ResourceFactory.getProperty("hire.employActualize.name"));
			csCell.setCellStyle(centerstyle);
			
			if(changeflag==0){
				csCell =row.createCell((short)4);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.change.info"));
				csCell.setCellStyle(centerstyle);
			}
			
			for(int i=0;i<fieldList.size();i++){
				csCell =row.createCell((short)(4+i+rows));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)fieldList.get(i));
				csCell.setCellStyle(centerstyle);
			}
			/**减少人员_1有数据_2是0，新增人员_2有数据_1是0*/
		    /**1是新增，2是减少*/
			this.frowset =dao.search(gaccountbo.changeSql(this.userView,changeflag+""));
			//System.out.println(gaccountbo.changeSql(this.userView,changeflag+""));
			int n=1;
			while(this.frowset.next()){
				
				row = sheet.createRow((short)(clum*n+2-clum));
				
				csCell =row.createCell((short)0);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(n);
				csCell.setCellStyle(centerstyle);

				csCell =row.createCell((short)1);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(AdminCode.getCodeName("UN",this.frowset.getString("b0110")));
				csCell.setCellStyle(colorstyle);
				
				csCell =row.createCell((short)2);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(AdminCode.getCodeName("UM",this.frowset.getString("e0122")));
				csCell.setCellStyle(colorstyle);
				
				csCell =row.createCell((short)3);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(this.frowset.getString("a0101"));
				csCell.setCellStyle(leftstyle);
				
				if(changeflag==0){
					csCell =row.createCell((short)4);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.change.before"));
					csCell.setCellStyle(leftstyle);
				}
				if(changeflag==2||changeflag==0){
					for(int i=0;i<beforeChange.size();i++){
						csCell =row.createCell((short)(i+4+rows));
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						String id = (String)beforeChange.get(i);
						String itemid=id.substring(0,id.length()-2);
						FieldItem item=DataDictionary.getFieldItem(id.substring(0,id.length()-2));
						if(item==null)
						{
							csCell.setCellValue(this.frowset.getString(id));
						}else
						{
							if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equals(item.getCodesetid()))
							{
								if("UM".equalsIgnoreCase(item.getCodesetid()))
								{
									String desc=AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(id));
									if(desc==null||desc.trim().length()==0)
										desc=AdminCode.getCodeName("UN", this.frowset.getString(id));
									csCell.setCellValue(desc);
								}
								else
									csCell.setCellValue(AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(id)));
							}
							else if("N".equalsIgnoreCase(item.getItemtype()))
							{
								csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								if(item.getDecimalwidth()>0&&this.frowset.getString(id)!=null){
									BigDecimal bd = new BigDecimal(this.frowset.getString(id));
									BigDecimal bd2=bd.setScale(item.getDecimalwidth(), bd.ROUND_HALF_UP);
									csCell.setCellValue(bd2.doubleValue());
						    		//csCell.setCellValue(Integer.parseInt(PubFunc.round(this.frowset.getDouble(id)+"", item.getDecimalwidth()).trim()));
								}else
									csCell.setCellValue(this.frowset.getInt(id));
							}
							else
							{
								csCell.setCellValue(this.frowset.getString(id));
							}
							
						}
						if("N".equalsIgnoreCase(item.getItemtype()))
						{
							
							csCell.setCellStyle(rightstyle);
						}else{
							csCell.setCellStyle(leftstyle);
						}
						
					}
				}
				if(changeflag==0){
					row = sheet.createRow((short)(clum*n+1));
					csCell =row.createCell((short)0);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue("");
					csCell.setCellStyle(leftstyle);
					
					csCell =row.createCell((short)1);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue("");
					csCell.setCellStyle(leftstyle);
				
					csCell =row.createCell((short)2);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue("");
					csCell.setCellStyle(leftstyle);
				
					csCell =row.createCell((short)3);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue("");
					csCell.setCellStyle(leftstyle);
					csCell =row.createCell((short)4);
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.change.affter"));
					csCell.setCellStyle(leftstyle);
				}
				if(changeflag==1||changeflag==0){
					for(int i=0;i<afterChange.size();i++){
						csCell =row.createCell((short)(i+4+rows));
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
						String id = (String)afterChange.get(i);
						FieldItem item=DataDictionary.getFieldItem(id.substring(0,id.length()-2));
						if(item==null)
						{
							csCell.setCellValue(this.frowset.getString(id));
						}else
						{
							if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equals(item.getCodesetid()))
							{
								if("UM".equalsIgnoreCase(item.getCodesetid()))
								{
									String desc=AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(id));
									if(desc==null||desc.trim().length()==0)
										desc=AdminCode.getCodeName("UN", this.frowset.getString(id));
									csCell.setCellValue(desc);
								}
								else
									csCell.setCellValue(AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(id)));
							}
							else if("N".equalsIgnoreCase(item.getItemtype()))
							{
								csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								if(item.getDecimalwidth()>0){
									String str = this.frowset.getString(id);
									BigDecimal bd = new BigDecimal(str==null||str.length()==0?"0":str);
									BigDecimal bd2=bd.setScale(item.getDecimalwidth(), bd.ROUND_HALF_UP);
									csCell.setCellValue(bd2.doubleValue());//按照数据字典的小数位走，zhaoxg 2013-11-19
								}else
									csCell.setCellValue(this.frowset.getInt(id));
							}
							else
							{
								csCell.setCellValue(this.frowset.getString(id));
							}
							
						}
						if("N".equalsIgnoreCase(item.getItemtype()))
						{
							csCell.setCellStyle(rightstyle);
						}else{
							csCell.setCellStyle(leftstyle);
						}
					}
				}
				if (changeflag == 0) {
					ExportExcelUtil.mergeCell(sheet, (short) 0, (short) 0, clum * n, clum * n + 1);
					ExportExcelUtil.mergeCell(sheet, (short) 0, (short) 0, (short) 1, (short) 1);
					ExportExcelUtil.mergeCell(sheet, (short) 0, (short) 0, (short) 2, (short) 2);
					ExportExcelUtil.mergeCell(sheet, (short) 0, (short) 0, (short) 3, (short) 3);
				}
				
				sheet.setColumnWidth((short)0,(short)1500);
				sheet.setColumnWidth((short)1,(short)4000);
				sheet.setColumnWidth((short)2,(short)3000);
				sheet.setColumnWidth((short)3,(short)3000);

				n++;
			}
			row = sheet.createRow((short)clum*n+2-clum);
			String macth="(-)?+[0-9]+(.[0-9]+)?";
			if(changeflag==0){
				csCell =row.createCell((short)0);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("");
				csCell.setCellStyle(bottomstyle);
				csCell =row.createCell((short)1);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("");
				csCell.setCellStyle(bottomstyle);
			
				csCell =row.createCell((short)2);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("");
				csCell.setCellStyle(bottomstyle);
		
				csCell =row.createCell((short)3);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.variance.total"));
				csCell.setCellStyle(bottomstyle);
			
				csCell =row.createCell((short)4);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("");
				csCell.setCellStyle(bottomstyle);
				for(int i=0;i<varianceTotal.size();i++){
					csCell =row.createCell((short)(i+5));
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
					String value=(String)varianceTotal.get(i);
					if(value==null)
						value="";
					if(value.matches(macth))
					{
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						csCell.setCellValue(Double.parseDouble(value));
					}
					else
					{
			    		csCell.setCellValue(value);
					}
					csCell.setCellStyle(bottomstyle);
				}
				row = sheet.createRow((short)clum*n+1);
			}
			csCell =row.createCell((short)0);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(bottomstyle);
			csCell =row.createCell((short)1);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(bottomstyle);
			
			csCell =row.createCell((short)2);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(bottomstyle);
			
			csCell =row.createCell((short)3);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(ResourceFactory.getProperty("gz.gz_acounting.total"));
			csCell.setCellStyle(bottomstyle);
			
			if(changeflag==0){
				csCell =row.createCell((short)4);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue("");
				csCell.setCellStyle(bottomstyle);
			}
			for(int i=0;i<totalValue.size();i++){
				csCell =row.createCell((short)(i+4+rows));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
				String value=(String)totalValue.get(i);
				if(value==null)
					value="";
				if(value.matches(macth))
				{
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					csCell.setCellValue(Double.parseDouble(value));
				}
				else
				{
		    		csCell.setCellValue(value);
				}
				csCell.setCellStyle(bottomstyle);
			}
			
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
		}catch(Exception e){
			e.printStackTrace();
		} 
		this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outname)));
	}
	/**
     * 设置excel表格效果
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		
		case 0:
				HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),20,0);
				fonttitle.setBold(false);//加粗 
				style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.CENTER );
		        break;			
		case 1:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,1));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,2));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,3));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				//style.setFillPattern(FillPatternType.ALIGN_CENTER);
				//style.setFillForegroundColor(HSSFColor.DARK_YELLOW.index); 
				break;		
		case 4:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,4));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				//style.setFillPattern(FillPatternType.ALIGN_CENTER);
				//style.setFillForegroundColor(HSSFColor.DARK_RED.index);
			  break;
		case 5:
			style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,2));
			style.setAlignment(HorizontalAlignment.RIGHT );
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);	                    	
			break;
		default:		
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12,5));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	  
				break;
		}
		return style;
	}
	/**
     * 设置excel字体效果
     * @param fonts 设置不同的字体
     * @param size 设置字体的大小
     * @param workbook 新建的表格
     */
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size,int stylecolor){
		HSSFFont font = workbook.createFont();
		if(stylecolor==4)
		{
			font.setColor(HSSFFont.COLOR_RED);
		}
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
}
