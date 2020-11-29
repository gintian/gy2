package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-19 17:25:00</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class ExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		HSSFWorkbook workbook= new HSSFWorkbook();
		try
		{
			//ResourceFactory.getProperty("gz.columns.a00z3")
			String taxid=(String)this.getFormHM().get("taxid");
			TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
			String commentname=this.userView.getUserName();
			HashMap taxmap = bo.getTaxExcelHead();


			HSSFSheet sheet1 = workbook.createSheet();
//			workbook.setSheetName(0,ResourceFactory.getProperty("gz.formula.scale"),HSSFWorkbook.ENCODING_UTF_16);
			workbook.setSheetName(0,ResourceFactory.getProperty("gz.formula.scale"));
			HSSFRow row = null;
			HSSFCell cell=null;
			HSSFComment comment=null;
			String outName=""+commentname+"_"+PubFunc.getStrg()+".xls";
			short n=0;
			n=setTaxHead(n,taxmap,workbook,sheet1,commentname);
			ArrayList taxinfoList = getTaxTableInfo(taxid,bo);
			setTaxData(n,taxinfoList,workbook,sheet1,row,cell);
			//
			HSSFSheet sheet2 = workbook.createSheet();
//			workbook.setSheetName(1,ResourceFactory.getProperty("gz.columns.slmx"),HSSFWorkbook.ENCODING_UTF_16);
			workbook.setSheetName(1,ResourceFactory.getProperty("gz.columns.slmx"));
			HashMap taxDetailMap=bo.getTaxDetailExcelHead();
			short k=0;
			k=setTaxDetailHead(k,taxDetailMap,workbook,sheet2,commentname);
			ArrayList detailList = bo.getTaxDetailTableList(taxid);
			String[][] data_arr=getDetailData(detailList);
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			//comment = cell.getCellComment();
			//import org.apache.poi.hssf.usermodel.HSSFComment;
			cellStyle.setAlignment(HorizontalAlignment.RIGHT );
			for(short j=0;j<data_arr.length;j++)
			{
				row= sheet2.createRow(k);
				for(short h=0;h<data_arr[j].length;h++)
				{
					cell=row.createCell(h);
					if(h==7)
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					else
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					//cell.setEncoding(arg0)
					
					cell.setCellStyle(cellStyle);
					cell.setCellValue(data_arr[j][h]);
				}
				k++;
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();	
			sheet1=null;
			sheet2=null;
			/* 安全问题 文件导出 税率表-导出 xiaoyun 2014-9-16 start */
			// outName=outName.replace(".xls","#");
			outName = SafeCode.encode(PubFunc.encrypt(outName));
			/* 安全问题 文件导出 税率表-导出 xiaoyun 2014-9-16 end */
			this.getFormHM().put("outName",outName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(workbook);
		}
		
	}
	/**
	 * 设置税率表头
	 * @param n
	 * @param map
	 * @param workbook
	 * @param sheet
	 * @return
	 */
	private short setTaxHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet,String commentname)
	{
		short i=n;
		try
		{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			HSSFComment comment1 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 6, 0));
			comment1.setString(new HSSFRichTextString("taxid"));
			comment1.setAuthor(commentname);
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER );
			row=sheet.createRow(i);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
		    csCell.setCellComment(comment1);
			
			csCell.setCellValue((String)map.get("taxid"));
			//
			HSSFComment comment2 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 3, (short) 6, 1));
			comment2.setString(new HSSFRichTextString("description"));
			comment2.setAuthor(commentname);
			csCell=row.createCell((short)1);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellComment(comment2);
			csCell.setCellValue((String)map.get("description"));
			//
			HSSFComment comment3 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 4, (short) 6, 2));
			comment3.setString(new HSSFRichTextString("k_base"));
			comment3.setAuthor(commentname);
			csCell=row.createCell((short)2);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment3);
			
			csCell.setCellValue((String)map.get("k_base"));
			//
			HSSFComment comment4 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 5, (short) 6, 3));
			comment4.setString(new HSSFRichTextString("param"));
			comment4.setAuthor(commentname);
			csCell=row.createCell((short)3);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment4);
			
			csCell.setCellValue((String)map.get("param"));
            i++;	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	/**
	 * 取得税率表信息
	 * @param taxid
	 * @param setbo
	 * @return
	 */
	private ArrayList getTaxTableInfo(String taxid,TaxTableSetBo setbo)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			StringBuffer sql= new StringBuffer("select * from  gz_tax_rate where taxid in(");
			if(taxid.indexOf(",")==-1)
			{
				buf.append(taxid);
				sql.append(buf.toString());
			}else
			{
				String[] arr= taxid.split(",");
				for(int i=0;i<arr.length;i++)
				{
					buf.append(",");
					buf.append(arr[i]);
				}
				sql.append(buf.toString().substring(1));
			}
			sql.append(") order by taxid");
			TaxTableXMLBo bo=new TaxTableXMLBo(this.getFrameconn());
	    	HashMap taxmap=bo.getAllValues(taxid);
	    	
    		ContentDAO da= new ContentDAO(this.getFrameconn());
    		this.frowset=da.search(sql.toString());
    		while(this.frowset.next()){
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("description",this.frowset.getString("description"));
                bean.set("k_base",setbo.getXS(String.valueOf(this.frowset.getFloat("k_base")),2));
    			
    			if(taxmap !=null && taxmap.size()!=0)
    			{
    				bean.set("param",AdminCode.getCodeName("46",(String)taxmap.get(String.valueOf(this.frowset.getInt("taxid"))))); 
    			}
    			else
    			{
    				bean.set("param","");
    			}
                bean.set("taxid",String.valueOf(this.frowset.getInt("taxid")));
                list.add(bean);
    		}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	private void setTaxData(short n,ArrayList infoList,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell)
	{
		try
		{
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(false);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT );
			//sheet.addMergedRegion(new Region(1,(short)3,1,(short)4));//指定第2行第4列和2行5列合并
			HSSFFont font2 = workbook.createFont();			
			font2.setColor(HSSFFont.COLOR_NORMAL);
			font2.setBold(false);
			HSSFCellStyle cellStyle2= workbook.createCellStyle();
			cellStyle2.setFont(font);
			cellStyle2.setAlignment(HorizontalAlignment.LEFT );
			for(int i=0;i<infoList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
        		row = sheet.createRow(n);
        		cell=row.createCell((short)0);
        		
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellStyle(cellStyle);
        		cell.setCellValue((String)bean.get("taxid"));
		//
        	   	cell=row.createCell((short)1);
        		
	        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	        	cell.setCellStyle(cellStyle2);
        		cell.setCellValue((String)bean.get("description"));
		//
	        	cell=row.createCell((short)2);
        		
	        	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	        	cell.setCellStyle(cellStyle);
	        	cell.setCellValue((String)bean.get("k_base"));
		//
	        	cell=row.createCell((short)3);
        		
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        		cell.setCellStyle(cellStyle2);
	        	cell.setCellValue((String)bean.get("param"));
	        	n++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private short setTaxDetailHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet,String commentname)
	{
		short i=n;
		try
		{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER );
			row=sheet.createRow(i);
			HSSFComment comment0 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 6, 0));
			comment0.setString(new HSSFRichTextString("taxid"));
			comment0.setAuthor(commentname);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment0);
			
			csCell.setCellValue((String)map.get("taxid"));
			//
			csCell=row.createCell((short)1);
			HSSFComment comment1 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 3, (short) 6,1));
			comment1.setString(new HSSFRichTextString("taxitem"));
			comment1.setAuthor(commentname);
			csCell.setCellComment(comment1);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("taxitem"));
			//
			csCell=row.createCell((short)2);
			HSSFComment comment2= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 4, (short) 6,2));
			comment2.setString(new HSSFRichTextString("ynse_down"));
			comment2.setAuthor(commentname);
			csCell.setCellComment(comment2);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("ynse_down"));
			//
			csCell=row.createCell((short)3);
			HSSFComment comment3= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 5, (short) 6,3));
			comment3.setString(new HSSFRichTextString("ynse_up"));
			comment3.setAuthor(commentname);
			csCell.setCellComment(comment3);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("ynse_up"));
			//
			csCell=row.createCell((short)4);
			HSSFComment comment4= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 6, (short) 6,4));
			comment4.setString(new HSSFRichTextString("sl"));
			comment4.setAuthor(commentname);
			csCell.setCellComment(comment4);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("sl"));
			//
			csCell=row.createCell((short)5);
			HSSFComment comment5= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 7, (short) 6,5));
			comment5.setString(new HSSFRichTextString("sskcs"));
			comment5.setAuthor(commentname);
			csCell.setCellComment(comment5);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("sskcs"));
			//
			csCell=row.createCell((short)6);
			HSSFComment comment6= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4,8, (short) 6,6));
			comment6.setString(new HSSFRichTextString("flag"));
			comment6.setAuthor(commentname);
			csCell.setCellComment(comment6);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("flag"));
			//
			csCell=row.createCell((short)7);
			HSSFComment comment7= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 9, (short) 6,7));
			comment7.setString(new HSSFRichTextString("description"));
			comment7.setAuthor(commentname);
			csCell.setCellComment(comment7);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("description"));
			//
			csCell=row.createCell((short)8);
			HSSFComment comment8= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 10, (short) 6,8));
			comment8.setString(new HSSFRichTextString("kc_base"));
			comment8.setAuthor(commentname);
			csCell.setCellComment(comment8);
			csCell.setCellStyle(cellStyle);
			
			csCell.setCellValue((String)map.get("kc_base"));
            i++;	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	private String[][] getDetailData(ArrayList list)
	{
		String[][] arr= new String[list.size()][9];
		try
		{
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				arr[i][0]=(String)bean.get("taxid");
				arr[i][1]=(String)bean.get("taxitem");
				arr[i][2]=(String)bean.get("ynse_down");
				arr[i][3]=(String)bean.get("ynse_up");
				arr[i][4]=(String)bean.get("sl");
				arr[i][5]=(String)bean.get("sskcs");
				arr[i][6]=getFlagState((String)bean.get("flag"));
				arr[i][7]=(String)bean.get("description");
				arr[i][8]=(String)bean.get("kc_base");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return arr;
	}
	
	private String getFlagState(String flag) {
		String val = flag;
		if("0".equals(flag)) {
			// 上限封闭
			val = ResourceFactory.getProperty("jx.param.upmargin");
		}else if("1".equals(flag)) {
			// 下限封闭
			val = ResourceFactory.getProperty("jx.param.downmargin");
		}
		return val;
	}
}
