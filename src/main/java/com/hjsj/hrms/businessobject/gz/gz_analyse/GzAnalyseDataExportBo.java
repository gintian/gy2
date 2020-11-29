package com.hjsj.hrms.businessobject.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class GzAnalyseDataExportBo {

	private Connection conn;
	public String id="00";
	public GzAnalyseDataExportBo(Connection conn) {
		this.conn = conn;
	}
    /**
     * 
     * @param conn
     * @param rsid
     * @param rsdtlid
     * @param pre
     * @param salaryid
     * @param year
     * @param a0100s
     * @param headList
     * @param isShowHead
     * @param isShowSeria
     * @param isShowUnitData
     * @param nameMap
     * @param view
     * @param archive
     * @param noPage 批量输出excel时，是否分页显示每个人的数据=0（默认）分页，=1不分页
     * @return
     */
	public String bacthExportExcel(Connection conn,String rsid, String rsdtlid, String pre, String salaryid, String year, String a0100s,ArrayList headList,String isShowHead,String isShowSeria,String isShowUnitData,HashMap nameMap,UserView view,String archive,String noPage,String recordNums)
	{
		String filename="gzAnalyseBacthData_"+PubFunc.getStrg()+".xls";
		try
		{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			HSSFRow row = null;
			HashMap map = new HashMap();
			HSSFCell cell = null;
			GzExcelBo gebo = new GzExcelBo(this.conn);
			HSSFDataFormat df = workbook.createDataFormat();
			c_0 = this.getCellStyle(view, rsid, rsdtlid, workbook, 0, gebo, df);
			c_1 = this.getCellStyle(view, rsid, rsdtlid, workbook, 1, gebo, df);
			c_2 = this.getCellStyle(view, rsid, rsdtlid, workbook, 2, gebo, df);
			c_3 = this.getCellStyle(view, rsid, rsdtlid, workbook, 3, gebo, df);
			c_4 = this.getCellStyle(view, rsid, rsdtlid, workbook, 4, gebo, df);
			this.nums=Integer.parseInt(recordNums);
			gebo.setNums(nums);
			//font.setColor(HSSFFont.COLOR_NORMAL);
			//font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			String[] a0100 = a0100s.split(",");
			GzAnalyseBo bo =new GzAnalyseBo(conn);
			if(archive!=null&& "0".equals(archive))
				bo.setTableName("salaryarchive");
			 String macth="[0-9]+(.[0-9]+)?";
			 if(noPage==null|| "0".equals(noPage))
			 {
			    for(int i=0;i<a0100.length;i++)
			    {
		    		if(a0100[i]==null|| "".equals(a0100[i]))
		    			continue;
		    		short n = 0;
			    	HashMap testMap = new HashMap();
		    		HashMap monthMap = new HashMap();
			    	int monthcount=0;
			    	String nbase=a0100[i].substring(0,3);
			    	String a01=a0100[i].substring(3);
			    	String a0101 = (String)nameMap.get(a0100[i].toUpperCase());
			    	if(map.get(a0101)==null)
				    	map.put(a0101, "1");
			    	else
			    	{
			    		String count=(String)map.get(a0101);
			    		a0101=a0101+count;
			    		map.put(a0101,(Integer.parseInt(count)+1)+"");
			    	}
			    	sheet=workbook.createSheet(a0101);
			    	n=gebo.executeTitle(rsid, rsdtlid, headList, isShowSeria, "0", view, n, sheet, row, workbook, isShowHead);
			    	n = this.setHead(n, headList, workbook, sheet, rsid,isShowHead,isShowSeria,isShowUnitData,noPage,view,rsdtlid,gebo);
			    	ArrayList recordList = bo.getRecordListBacth(rsid, rsdtlid, pre, salaryid, Integer.parseInt(year), a0100[i]);
			    	for(int m=0;m<recordList.size();m++)
	    	    	{
    		    		LazyDynaBean abean=(LazyDynaBean)recordList.get(m);
    		    		String month=(String)abean.get("amonth");
    		    		testMap.put(month,abean);
	    	    	}
				
			    	for(int j=1;j<13;j++)
			    	{
			    		short cloumn=0;
			    		row = sheet.createRow(n);
			    		if("1".equals(isShowSeria))
			    		{
			    			cell=row.createCell(cloumn);
			    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    		cell.setCellStyle(this.c_0);
				    		cell.setCellValue(bo.getUpperMonth(j));
				    		cloumn++;
				    	}
			    		if(testMap.get(String.valueOf(j))!=null)
			    		{
				    		monthcount++;
			    			LazyDynaBean dataBean = (LazyDynaBean)testMap.get(String.valueOf(j));
			    			for(int x=0;x<headList.size();x++)
				    		{
					    		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
					    		String itemdesc=(String)headbean.get("itemdesc");
	         	         		String itemid=(String)headbean.get("itemid");
	            	         	String itemfmt=(String)headbean.get("itemfmt");
		    	            	String itemtype=(String)headbean.get("itemtype");
		    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
		    	            	String value=(String)dataBean.get(itemid);
		    	            	if("null".equals(value)|| "".equals(value))
		    	            		value="";
		    	            	if(".0".equals(value)|| ".00".equals(value)|| ".000".equals(value)|| ".000".equals(value))
					    			value="0";
		    	            	monthMap=this.getMap(itemid, value, monthMap,myformat);
		    	            	cell=row.createCell(cloumn);
		    	            	if("n".equalsIgnoreCase(itemtype)&&value!=null&&!"".equals(value))
		    	            	{
					    	    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						        	int temp = 0;
									if(value.indexOf(".")!=-1)
									{
										temp=value.substring((value.indexOf(".")+1)).length();
									}
									if(temp==0)
									{
										cell.setCellStyle(this.c_0);
									}else if(temp==1)
									{
										cell.setCellStyle(this.c_1);
									}else if(temp==2)
									{
										cell.setCellStyle(this.c_2);
									}else if(temp==3)
									{
										cell.setCellStyle(this.c_3);
									}else if(temp==4)
									{
										cell.setCellStyle(this.c_4);
									}
						        	cell.setCellValue(Double.parseDouble(myformat.format(new Double(value))));
		    	            	}
		    	             	else
		    	             	{
		    	            		cell.setCellValue(value);
		    	            		cell.setCellStyle(this.c_0);
		    	             	}
					    		cloumn++;
					    	}
				    	}
				    	else
			    		{
				    		for(int x=0;x<headList.size();x++)
				    		{
					     		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
				    	 		cell=row.createCell(cloumn);
						    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellStyle(c_0);
						    	String itemid=(String)headbean.get("itemid");
						    	DecimalFormat myformat=new DecimalFormat("0.00");
		    	            	monthMap=this.getMap(itemid, "0.00", monthMap,myformat);
					    		cell.setCellValue("");
					    		cloumn++;
			    			}
			    		}
		    			n++;
		    		}
	    			row=sheet.createRow(n);
		    		short cloumn=0;
		     		if("1".equals(isShowSeria))
		    		{
		    			cell=row.createCell(cloumn);
		    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    			cell.setCellStyle(c_0);
		    			cell.setCellValue("月平均");
		    			cloumn++;
	    			}
	    			for(int x=0;x<headList.size();x++)
		    		{
			    		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
     	        		String itemid=(String)headbean.get("itemid");
     	        		String itemfmt=(String)headbean.get("itemfmt");
    	            	String itemtype=(String)headbean.get("itemtype");
    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
    	            	String count=(String)monthMap.get(itemid.toLowerCase());
    	            	cell=row.createCell(cloumn);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		int temp = 0;
						if(itemfmt.indexOf(".")!=-1)
						{
							temp=itemfmt.substring((itemfmt.indexOf(".")+1)).length();
						}
						if(temp==0)
						{
							cell.setCellStyle(this.c_0);
						}else if(temp==1)
						{
							cell.setCellStyle(this.c_1);
						}else if(temp==2)
						{
							cell.setCellStyle(this.c_2);
						}else if(temp==3)
						{
							cell.setCellStyle(this.c_3);
						}else if(temp==4)
						{
							cell.setCellStyle(this.c_4);
						}
			    		cell.setCellValue(Double.parseDouble(myformat.format(new Double(bo.div(count, monthcount+"", 2)))));
			    		cloumn++;
		    		}
		    		n++;
		    		row=sheet.createRow(n);
		    		short cloumn2=0;
		    		if("1".equals(isShowSeria))
		    		{
		    			cell=row.createCell(cloumn2);
			    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			    		cell.setCellStyle(c_0);
		    			cell.setCellValue("合计");
			     		cloumn2++;
		     	 	}
		     		for(int x=0;x<headList.size();x++)
		    		{
		    			LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
         	    		String itemid=(String)headbean.get("itemid");
     	        		String itemfmt=(String)headbean.get("itemfmt");
    	            	String itemtype=(String)headbean.get("itemtype");
    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
    	            	String count=(String)monthMap.get(itemid.toLowerCase());
    	            	cell=row.createCell(cloumn2);
		    			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		    			int temp = 0;
						if(itemfmt.indexOf(".")!=-1)
						{
							temp=itemfmt.substring((itemfmt.indexOf(".")+1)).length();
						}
						if(temp==0)
						{
							cell.setCellStyle(this.c_0);
						}else if(temp==1)
						{
							cell.setCellStyle(this.c_1);
						}else if(temp==2)
						{
							cell.setCellStyle(this.c_2);
						}else if(temp==3)
						{
							cell.setCellStyle(this.c_3);
						}else if(temp==4)
						{
							cell.setCellStyle(this.c_4);
						}
		    			cell.setCellValue(Double.parseDouble(myformat.format(new Double(count))));
	 	    			cloumn2++;
	    			}
		     		n++;
		     		gebo.executeTail(rsid, rsdtlid, headList, isShowSeria, "0", view, n, sheet, row, workbook);
		     		 HSSFPrintSetup printSetup=sheet.getPrintSetup();
					 if(rpv.getPagetype()!=null&&!"".equals(rpv.getPagetype().trim()))
					 {
						 if("A4".equalsIgnoreCase(rpv.getPagetype()))
							 printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
						 else if("A5".equalsIgnoreCase(rpv.getPagetype()))
							 printSetup.setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
					 }
					 if(rpv.getHeight()!=null&&!"".equals(rpv.getHeight()))
					 {
						 String gh=PubFunc.round(rpv.getHeight(), 0);
						 printSetup.setFitHeight(Short.parseShort(gh));
					 }
					 if(rpv.getWidth()!=null&&!"".equals(rpv.getWidth()))
					 {
						 String gh=PubFunc.round(rpv.getWidth(), 0);
						 printSetup.setFitWidth(Short.parseShort(gh));
					 }
					 if("1".equals(rpv.getOrientation()))//横向
					 {
						 printSetup.setLandscape(true);
					 }
					 else
					 {
						 printSetup.setLandscape(false);
					 }
					 if(rpv.getTop()!=null&&!"".equals(rpv.getTop().trim()))
		             {
		            	 sheet.setMargin(HSSFSheet.TopMargin,Double.parseDouble(rpv.getTop()));
		             }
		             if(rpv.getBottom()!=null&&!"".equals(rpv.getBottom().trim()))
		             {
		            	 sheet.setMargin(HSSFSheet.BottomMargin, Double.parseDouble(rpv.getBottom()));
		             }
		             if(rpv.getLeft()!=null&&!"".equals(rpv.getLeft().trim()))
		             {
		            	 sheet.setMargin(HSSFSheet.LeftMargin, Double.parseDouble(rpv.getLeft()));
		             }
		             if(rpv.getRight()!=null&&!"".equals(rpv.getRight().trim()))
		             {
		            	 sheet.setMargin(HSSFSheet.RightMargin, Double.parseDouble(rpv.getRight()));
		             }
				} 
			}
			 else
			 {
				 short n=0;
				 sheet=workbook.createSheet();
				 n=gebo.executeTitle(rsid, rsdtlid, headList, isShowSeria, "o", view, n, sheet, row, workbook, isShowHead);
				 for(int i=0;i<a0100.length;i++)
				 {
			    	if(a0100[i]==null|| "".equals(a0100[i]))
			    		continue;
			    	HashMap testMap = new HashMap();
		    		HashMap monthMap = new HashMap();
			    	int monthcount=0;
			    	String nbase=a0100[i].substring(0,3);
			    	String a01=a0100[i].substring(3);
			    	String a0101 = (String)nameMap.get(a0100[i].toUpperCase());
			    	n = this.setHead(n, headList, workbook, sheet, rsid,isShowHead,isShowSeria,isShowUnitData,noPage,view,rsdtlid,gebo);
			    	ArrayList recordList = bo.getRecordListBacth(rsid, rsdtlid, pre, salaryid, Integer.parseInt(year), a0100[i]);
			    	for(int m=0;m<recordList.size();m++)
	    	    	{
    		    		LazyDynaBean abean=(LazyDynaBean)recordList.get(m);
    		    		String month=(String)abean.get("amonth");
    		    		testMap.put(month,abean);
	    	    	}
				
			    	for(int j=1;j<13;j++)
			    	{
			    		short cloumn=0;
			    		row = sheet.createRow(n);
			    		if(noPage!=null&& "1".equals(noPage))
			    		{
			    			cell=row.createCell(cloumn);
			    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    		cell.setCellStyle(c_0);
				    		cell.setCellValue(a0101);
				    		cloumn++;
			    		}
			    		if("1".equals(isShowSeria))
			    		{
			    			cell=row.createCell(cloumn);
			    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				    		cell.setCellStyle(c_0);
				    		cell.setCellValue(bo.getUpperMonth(j));
				    		cloumn++;
				    	}
			    		if(testMap.get(String.valueOf(j))!=null)
			    		{
				    		monthcount++;
			    			LazyDynaBean dataBean = (LazyDynaBean)testMap.get(String.valueOf(j));
			    			for(int x=0;x<headList.size();x++)
				    		{
					    		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
					    		String itemdesc=(String)headbean.get("itemdesc");
	         	         		String itemid=(String)headbean.get("itemid");
	            	         	String itemfmt=(String)headbean.get("itemfmt");
		    	            	String itemtype=(String)headbean.get("itemtype");
		    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
		    	            	String value=(String)dataBean.get(itemid);
		    	            	if("null".equals(value)|| "".equals(value))
		    	            		value="";
		    	            	if(".0".equals(value)|| ".00".equals(value)|| ".000".equals(value)|| ".000".equals(value))
					    			value="0";
		    	            	monthMap=this.getMap(itemid, value, monthMap,myformat);
		    	            	cell=row.createCell(cloumn);
		    	            	if("n".equalsIgnoreCase(itemtype)&&itemtype!=null&&!"".equals(value))
		    	            	{
					    	    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    	    	int temp = 0;
									if(itemfmt.indexOf(".")!=-1)
									{
										temp=itemfmt.substring((itemfmt.indexOf(".")+1)).length();
									}
									if(temp==0)
									{
										cell.setCellStyle(this.c_0);
									}else if(temp==1)
									{
										cell.setCellStyle(this.c_1);
									}else if(temp==2)
									{
										cell.setCellStyle(this.c_2);
									}else if(temp==3)
									{
										cell.setCellStyle(this.c_3);
									}else if(temp==4)
									{
										cell.setCellStyle(this.c_4);
									}
						        	cell.setCellValue(Double.parseDouble(myformat.format(new Double(value))));
		    	            	}
		    	             	else
		    	            		cell.setCellValue(value);
					    		cloumn++;
					    	}
				    	}
				    	else
			    		{
				    		for(int x=0;x<headList.size();x++)
				    		{
					     		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
				    	 		cell=row.createCell(cloumn);
						    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    		cell.setCellStyle(c_0);
						    	String itemid=(String)headbean.get("itemid");
						    	DecimalFormat myformat=new DecimalFormat("0.00");
		    	            	monthMap=this.getMap(itemid, "0.00", monthMap,myformat);
					    		cell.setCellValue("");
					    		cloumn++;
			    			}
			    		}
		    			n++;
		    		}
	    			row=sheet.createRow(n);
		    		short cloumn=0;
		    		if(noPage!=null&& "1".equals(noPage))
		    		{
		    			cell=row.createCell(cloumn);
		    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			    		cell.setCellStyle(c_0);
			    		cell.setCellValue(a0101);
			    		cloumn++;
		    		}
		     		if("1".equals(isShowSeria))
		    		{
		    			cell=row.createCell(cloumn);
		    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    			cell.setCellStyle(c_0);
		    			cell.setCellValue("月平均");
		    			cloumn++;
	    			}
	    			for(int x=0;x<headList.size();x++)
		    		{
			    		LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
     	        		String itemid=(String)headbean.get("itemid");
     	        		String itemfmt=(String)headbean.get("itemfmt");
    	            	String itemtype=(String)headbean.get("itemtype");
    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
    	            	String count=(String)monthMap.get(itemid.toLowerCase());
    	            	cell=row.createCell(cloumn);
			    		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			    		int temp = 0;
						if(itemfmt.indexOf(".")!=-1)
						{
							temp=itemfmt.substring((itemfmt.indexOf(".")+1)).length();
						}
						if(temp==0)
						{
							cell.setCellStyle(this.c_0);
						}else if(temp==1)
						{
							cell.setCellStyle(this.c_1);
						}else if(temp==2)
						{
							cell.setCellStyle(this.c_2);
						}else if(temp==3)
						{
							cell.setCellStyle(this.c_3);
						}else if(temp==4)
						{
							cell.setCellStyle(this.c_4);
						}
			    		cell.setCellValue(Double.parseDouble(myformat.format(new Double(bo.div(count, monthcount+"", 2)))));
			    		cloumn++;
		    		}
		    		n++;
		    		row=sheet.createRow(n);
		    		short cloumn2=0;
		    		if(noPage!=null&& "1".equals(noPage))
		    		{
		    			cell=row.createCell(cloumn2);
		    			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			    		cell.setCellStyle(c_0);
			    		cell.setCellValue(a0101);
			    		cloumn2++;
		    		}
		    		if("1".equals(isShowSeria))
		    		{
		    			cell=row.createCell(cloumn2);
			    		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			    		cell.setCellStyle(c_0);
		    			cell.setCellValue("合计");
			     		cloumn2++;
		     	 	}
		     		for(int x=0;x<headList.size();x++)
		    		{
		    			LazyDynaBean headbean = (LazyDynaBean)headList.get(x);
         	    		String itemid=(String)headbean.get("itemid");
     	        		String itemfmt=(String)headbean.get("itemfmt");
    	            	String itemtype=(String)headbean.get("itemtype");
    	            	DecimalFormat myformat=new DecimalFormat((itemfmt==null|| "".equals(itemfmt))?"0.00":itemfmt);
    	            	String count=(String)monthMap.get(itemid.toLowerCase());
    	            	cell=row.createCell(cloumn2);
		    			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		    			int temp = 0;
						if(itemfmt.indexOf(".")!=-1)
						{
							temp=itemfmt.substring((itemfmt.indexOf(".")+1)).length();
						}
						if(temp==0)
						{
							cell.setCellStyle(this.c_0);
						}else if(temp==1)
						{
							cell.setCellStyle(this.c_1);
						}else if(temp==2)
						{
							cell.setCellStyle(this.c_2);
						}else if(temp==3)
						{
							cell.setCellStyle(this.c_3);
						}else if(temp==4)
						{
							cell.setCellStyle(this.c_4);
						}
		    			cell.setCellValue(Double.parseDouble(myformat.format(new Double(count))));
	 	    			cloumn2++;
	    			}
		     		n++;
		     		n++;
				 }
				 gebo.executeTail(rsid, rsdtlid, headList, isShowSeria, "0", view, n, sheet, row, workbook);
				 HSSFPrintSetup printSetup=sheet.getPrintSetup();
				 if(rpv.getPagetype()!=null&&!"".equals(rpv.getPagetype().trim()))
				 {
					 if("A4".equalsIgnoreCase(rpv.getPagetype()))
						 printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
					 else if("A5".equalsIgnoreCase(rpv.getPagetype()))
						 printSetup.setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
				 }
				 if(rpv.getHeight()!=null&&!"".equals(rpv.getHeight()))
				 {
					 String gh=PubFunc.round(rpv.getHeight(), 0);
					 printSetup.setFitHeight(Short.parseShort(gh));
				 }
				 if(rpv.getWidth()!=null&&!"".equals(rpv.getWidth()))
				 {
					 String gh=PubFunc.round(rpv.getWidth(), 0);
					 printSetup.setFitWidth(Short.parseShort(gh));
				 }
				 if("1".equals(rpv.getOrientation()))//横向
				 {
					 printSetup.setLandscape(true);
				 }
				 else
				 {
					 printSetup.setLandscape(false);
				 }
				 if(rpv.getTop()!=null&&!"".equals(rpv.getTop().trim()))
	             {
	            	 sheet.setMargin(HSSFSheet.TopMargin,Double.parseDouble(rpv.getTop()));
	             }
	             if(rpv.getBottom()!=null&&!"".equals(rpv.getBottom().trim()))
	             {
	            	 sheet.setMargin(HSSFSheet.BottomMargin, Double.parseDouble(rpv.getBottom()));
	             }
	             if(rpv.getLeft()!=null&&!"".equals(rpv.getLeft().trim()))
	             {
	            	 sheet.setMargin(HSSFSheet.LeftMargin, Double.parseDouble(rpv.getLeft()));
	             }
	             if(rpv.getRight()!=null&&!"".equals(rpv.getRight().trim()))
	             {
	            	 sheet.setMargin(HSSFSheet.RightMargin, Double.parseDouble(rpv.getRight()));
	             }
			 }
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
			workbook = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return filename;
	}
	private String name="";
	private int nums=0;
/**
 * 
 * @param rsid
 * @param dataList
 * @param headList
 * @param bgroup
 * @param isShowHead=1显示标题=0不显示
 * @param isShowSeria=1显示序号=0不显示
 * @return
 */
	HSSFCellStyle c_0=null;
	HSSFCellStyle c_1=null;
	HSSFCellStyle c_2=null;
	HSSFCellStyle c_3=null;
	HSSFCellStyle c_4=null;
	public String executeExport(String rsdtlid,String rsid, ArrayList dataList,ArrayList headList,String bgroup,String isShowHead,String isShowSeria,String isShowUnitData,UserView view,String name,String recordNums) {
		String filename = "gzAnalyseData_"+PubFunc.getStrg()+".xls";
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet =workbook.createSheet();
			HSSFDataFormat df = workbook.createDataFormat();
			GzExcelBo bo = new GzExcelBo(this.conn);
			c_0 = this.getCellStyle(view, rsid, rsdtlid, workbook, 0, bo, df);
			c_1 = this.getCellStyle(view, rsid, rsdtlid, workbook, 1, bo, df);
			c_2 = this.getCellStyle(view, rsid, rsdtlid, workbook, 2, bo, df);
			c_3 = this.getCellStyle(view, rsid, rsdtlid, workbook, 3, bo, df);
			c_4 = this.getCellStyle(view, rsid, rsdtlid, workbook, 4, bo, df);
			this.name=name;
			nums=Integer.parseInt(recordNums);
			HSSFRow row = null;
			HSSFCell cell = null;
			short n = 0;
			bo.setNums(nums);
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			n=bo.executeTitle(rsid, rsdtlid, headList, isShowSeria, "0", view, n, sheet, row, workbook,isShowHead);
			n = this.setHead(n, headList, workbook, sheet, rsid,isShowHead,isShowSeria,isShowUnitData,"0",view,rsdtlid,bo);
			n=this.setData(n, dataList, workbook, sheet, row, cell,bgroup,rsid,isShowHead,isShowSeria,view,rsdtlid,bo);
			bo.executeTail(rsid, rsdtlid, headList, isShowSeria, "0", view, n, sheet, row, workbook);
			
			HSSFPrintSetup printSetup=sheet.getPrintSetup();
			 if(rpv.getPagetype()!=null&&!"".equals(rpv.getPagetype().trim()))
			 {
				 if("A4".equalsIgnoreCase(rpv.getPagetype()))
					 printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
				 else if("A5".equalsIgnoreCase(rpv.getPagetype()))
					 printSetup.setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			 }
			 if(rpv.getHeight()!=null&&!"".equals(rpv.getHeight()))
			 {
				 String gh=PubFunc.round(rpv.getHeight(), 0);
				 printSetup.setFitHeight(Short.parseShort(gh));
			 }
			 if(rpv.getWidth()!=null&&!"".equals(rpv.getWidth()))
			 {
				 String gh=PubFunc.round(rpv.getWidth(), 0);
				 printSetup.setFitWidth(Short.parseShort(gh));
			 }
			 if("1".equals(rpv.getOrientation()))//横向
			 {
				 printSetup.setLandscape(true);
			 }
			 else
			 {
				 printSetup.setLandscape(false);
			 }
			 if(rpv.getTop()!=null&&!"".equals(rpv.getTop().trim()))
             {
            	 sheet.setMargin(HSSFSheet.TopMargin,Double.parseDouble(rpv.getTop()));
             }
             if(rpv.getBottom()!=null&&!"".equals(rpv.getBottom().trim()))
             {
            	 sheet.setMargin(HSSFSheet.BottomMargin, Double.parseDouble(rpv.getBottom()));
             }
             if(rpv.getLeft()!=null&&!"".equals(rpv.getLeft().trim()))
             {
            	 sheet.setMargin(HSSFSheet.LeftMargin, Double.parseDouble(rpv.getLeft()));
             }
             if(rpv.getRight()!=null&&!"".equals(rpv.getRight().trim()))
             {
            	 sheet.setMargin(HSSFSheet.RightMargin, Double.parseDouble(rpv.getRight()));
             }
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet = null;
			workbook = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	public HSSFCellStyle getCellStyle(UserView view ,String rsid,String rsdtlid,HSSFWorkbook workbook,int scale,GzExcelBo bo,HSSFDataFormat df)
	{
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		try
		{
			HSSFFont font = workbook.createFont();
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
		     String t_fontEffect="0";
		     int    t_fontSize=11;  
		     String  t_underLine="#fu[0]";
		     if(rpv.getBody_fn().length()>0)
	        		t_fontfamilyname=rpv.getBody_fn();
		     t_fontEffect=bo.getFontEffect(rpv,4);
		     if(rpv.getBody_fz().length()>0)
		       	t_fontSize=Integer.parseInt(rpv.getBody_fz());      	 
		    if(rpv.getBody_fu()!=null&&rpv.getBody_fu().length()>0)
		        t_underLine=rpv.getBody_fu();
		    font.setFontName(t_fontfamilyname);//设置字体种类
			if("2".equals(t_fontEffect)){
				font.setBold(true); //设置字体
			}else if("4".equals(t_fontEffect)){
				font.setItalic( true ); // 是否使用斜体
			}else if("3".equals(t_fontEffect)){
				font.setBold(true); //设置字体
				font.setItalic( true ); // 是否使用斜体
			}
			if("#fu[1]".equals(t_underLine)){
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			font.setFontHeightInPoints((short)t_fontSize);//设置字体大小
			if(rpv.getBody_fc().length()>0)
			{
				String color = rpv.getBody_fc();
				if(color!=null&&color.startsWith("#"))
				   color = color.substring(1);
				int rgbC=0;
				if(color.trim().length()>0)
				{
					font.setColor(HSSFColor.GREY_50_PERCENT.index);
				}
			    if(color.trim().length()>0)
			    {
			    	 String r_str = color.substring(0, 2);
					 String g_str = color.substring(2, 4);
					 String b_str = color.substring(4, 6);
					 short r = (short)Integer.parseInt(r_str,16);
					 short g = (short)Integer.parseInt(g_str, 16);
					 short b = (short)Integer.parseInt(b_str, 16);
					 HSSFPalette palette = workbook.getCustomPalette();
					 palette.setColorAtIndex(HSSFColor.GREY_50_PERCENT.index, (byte)r, (byte)g, (byte)b);
			    } 
			}
			
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
			if(scale==0)
			{
				
			}
			else if(scale == 1)
			{
				cellStyle.setDataFormat(df.getFormat("0.0_ "));
			}else if(scale == 2)
			{
				cellStyle.setDataFormat(df.getFormat("0.00_ "));
			}else if(scale == 3)
			{
				cellStyle.setDataFormat(df.getFormat("0.000_ "));
			}else
			{
				cellStyle.setDataFormat(df.getFormat("0.0000_ "));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cellStyle;
	}
	private short setData(short n, ArrayList dataList, HSSFWorkbook workbook,HSSFSheet sheet, HSSFRow row, HSSFCell cell,String bgroup,String rsid,String isShowHead,String isShowSeria,UserView view,String rsdtlid,GzExcelBo bo) {
		short k = n;
		try {
			
			//font.setColor(HSSFFont.COLOR_NORMAL);
			//font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			boolean isPrintByGroup = rpob.isGroupPrint(rsid, rsdtlid, 1);
			ArrayList list = null;
			boolean flag = false;
			String macth="[0-9]+(.[0-9]+)?";
			int cloumn=0;
			for (int i = 0; i < dataList.size(); i++) {
				
				row = sheet.createRow(k);
				list = (ArrayList) dataList.get(i);
				cloumn=list.size();
				for (int j = 0; j < list.size(); j++) {
					String nwidth="15";
					if(widthMap.get(j+"")!=null)
						nwidth=(String)widthMap.get(j+"");
					cell = row.createCell((short) j);
					sheet.setColumnWidth(j, Integer.parseInt(nwidth)*220);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					
					String value="";
					if(list.get(j)!=null)
						value=(String)list.get(j);
					if("null".equalsIgnoreCase(value))
						value="";
					if(".0".equals(value)|| ".00".equals(value)|| ".000".equals(value)|| ".000".equals(value))
						value="0";
					String itemtype="A";
					if(typeMap.get(j+"")!=null)
						itemtype=(String)typeMap.get(j+"");
					if("n".equalsIgnoreCase(itemtype)&&value!=null&&!"".equals(value))
					{
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						int temp = 0;
						if(value.indexOf(".")!=-1)
						{
							temp=value.substring((value.indexOf(".")+1)).length();
						}
						if(temp==0)
						{
							cell.setCellStyle(this.c_0);
						}else if(temp==1)
						{
							cell.setCellStyle(this.c_1);
						}else if(temp==2)
						{
							cell.setCellStyle(this.c_2);
						}else if(temp==3)
						{
							cell.setCellStyle(this.c_3);
						}else if(temp==4)
						{
							cell.setCellStyle(this.c_4);
						}
					    cell.setCellValue(Double.parseDouble(value));
					}
					else
					{
						cell.setCellStyle(this.c_0);
			    		cell.setCellValue(value);
					}
					if(list.get(j)!=null&&!"".equals((String)list.get(j))&& "合计".equalsIgnoreCase((String)list.get(j)))
					{
						flag=true;
					}
				}
				if(flag&&isPrintByGroup)
				{
					//k=(short)(k+1);
					sheet.setRowBreak((int)k);
					flag = false;
					//k=(short)(k+1);
				}
				k++;
			}
			if("5".equals(rsid)|| "14".equals(rsid))
			{
				row = sheet.createRow(k+1);
				cell = row.createCell((short) 0);
				HSSFCellStyle Style = workbook.createCellStyle();
				sheet.setColumnWidth(0, 15*300);		
				cell.setCellStyle(Style);
				cell.setCellValue(this.name);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return k;
	}

	private HashMap typeMap = new HashMap();
	private HashMap widthMap = new HashMap();
	private short setHead(short n, ArrayList headList, HSSFWorkbook workbook,HSSFSheet sheet, String rsid,String isShowHead,String isShowSeria,String isShowUnitData,String noPage,UserView view,String rsdtlid,GzExcelBo bo) {
		short i = n;
		try {
			HSSFRow row = null;
			HSSFCell csCell = null;
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			String thead_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
		     String thead_fontEffect="0";
		     int    thead_fontSize=11;  
		     String  thead_underLine="#fu[0]";
		     if(rpv.getThead_fn().length()>0)
		    	 thead_fontfamilyname=rpv.getThead_fn();
		     thead_fontEffect=bo.getFontEffect(rpv,5);
		     if(rpv.getThead_fz().length()>0)
		    	 thead_fontSize=Integer.parseInt(rpv.getThead_fz());      	 
		    if(rpv.getThead_fu()!=null&&rpv.getThead_fu().length()>0)
		    	thead_underLine=rpv.getThead_fu();
		    HSSFFont thead_font = workbook.createFont();	
		    thead_font.setFontName(thead_fontfamilyname);//设置字体种类
			if("2".equals(thead_fontEffect)){
				thead_font.setBold(true); //设置字体
			}else if("4".equals(thead_fontEffect)){
				thead_font.setItalic( true ); // 是否使用斜体
			}else if("3".equals(thead_fontEffect)){
				thead_font.setBold(true); //设置字体
				thead_font.setItalic( true ); // 是否使用斜体
			}
			if("#fu[1]".equals(thead_underLine)){
				thead_font.setUnderline(HSSFFont.U_SINGLE);
			}
			thead_font.setFontHeightInPoints((short)thead_fontSize);//设置字体大小
			if(rpv.getThead_fc().length()>0)
			{
				String color = rpv.getThead_fc();
				if(color!=null&&color.startsWith("#"))
				   color = color.substring(1);
				int rgbC=0;
				if(color.trim().length()>0)
				{
					thead_font.setColor(HSSFColor.ROSE.index);
				}
			    if(color.trim().length()>0)
			    {
			    	 String r_str = color.substring(0, 2);
					 String g_str = color.substring(2, 4);
					 String b_str = color.substring(4, 6);
					 short r = (short)Integer.parseInt(r_str,16);
					 short g = (short)Integer.parseInt(g_str, 16);
					 short b = (short)Integer.parseInt(b_str, 16);
					 HSSFPalette palette = workbook.getCustomPalette();
					 palette.setColorAtIndex(HSSFColor.ROSE.index, (byte)r, (byte)g, (byte)b); 
			    } 
			}
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(thead_font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			short k = 0;
			if ("10".equals(rsid)) {
				LazyDynaBean bean = null;
				short s = 0;
				short t = 5;
				for (int j = 0; j < headList.size(); j++) {
					bean = (LazyDynaBean) headList.get(j);
					String itemtype=(String)bean.get("itemtype");
					String nwidth="15";
					if(bean.get("nwidth")!=null)
						nwidth=(String)bean.get("nwidth");
					if("月份".equalsIgnoreCase((String) bean.get("itemdesc")))
					{
				     	typeMap.put(k+"", "A");
				     	widthMap.put(k+"", "15");
					}
					else{
						typeMap.put(k+"", itemtype);
						widthMap.put(k+"", nwidth);
					}
					if (j == 0 || j == 1) {
						row = sheet.getRow(i);
						if(row==null)
							row = sheet.createRow(i);
						csCell = row.createCell(k);
						csCell.setCellStyle(cellStyle);
						csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
						csCell.setCellValue((String) bean.get("itemdesc"));
						ExportExcelUtil.mergeCell(sheet, i, s, i+1, (short)j);
						row = sheet.getRow(i+1);
						if(row==null)
							row = sheet.createRow(i+1);
						csCell = row.createCell(k);
						csCell.setCellStyle(cellStyle);
						s++;
						k++;
					} else {
						row = sheet.getRow(i);
						if(row==null)
							row = sheet.createRow(i);
						csCell = row.createCell((short) (k));
						csCell.setCellStyle(cellStyle);
						csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						csCell.setCellValue((String) bean.get("itemdesc"));
						ExportExcelUtil.mergeCell(sheet, i, s, i, (short) t);
						csCell = row.createCell((short) (k+1));
						csCell.setCellStyle(cellStyle);
						csCell = row.createCell((short) (k+2));
						csCell.setCellStyle(cellStyle);
						csCell = row.createCell((short) (k+3));
						csCell.setCellStyle(cellStyle);
						
						int count = 0;
						for (int h = k; h < (k + 4); h++) {							
							row = sheet.getRow(i+ 1);
							if(row==null)
								row = sheet.createRow(i+ 1);
							
							csCell = row.createCell((short) h);
							csCell.setCellStyle(cellStyle);
							csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//							csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if (count == 0)
							{
								typeMap.put(h+"", "N");
								widthMap.put(h+"", "15");
								csCell.setCellValue("上年");
							}
							else if (count == 1)
							{
								typeMap.put(h+"", "N");
								widthMap.put(h+"", "15");
								csCell.setCellValue("本年");
							}
							else if (count == 2)
							{
								csCell.setCellValue("增长额");
								widthMap.put(h+"", "15");
								typeMap.put(h+"", "N");
							}
							else
							{
								csCell.setCellValue("增长率");
								widthMap.put(h+"", "15");
								typeMap.put(h+"", "N");
							}

							count++;
						}
						k++;
						k++;
						k++;
						k++;
						s = (short) (s + 4);
						t = (short) (t + 4);
					}
				}
				row = sheet.getRow(i);
				if(row==null)
					row = sheet.createRow(i);
				csCell = row.createCell((short) (k));
				csCell.setCellStyle(cellStyle);
				csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue("汇总合计");
				typeMap.put(k+"", "N");
				widthMap.put(k+"", "15");
				ExportExcelUtil.mergeCell(sheet, i, k, i+1, (short)k);
				row = sheet.getRow(i+1);
				if(row==null)
					row = sheet.createRow(i+1);
				csCell = row.createCell((short) (k));
				csCell.setCellStyle(cellStyle);
				k++;
				s++;
				t++;
				row = sheet.getRow(i);
				if(row==null)
					row = sheet.createRow(i);
				csCell = row.createCell((short) (k));
				csCell.setCellStyle(cellStyle);
				csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue("人均汇总合计");
				ExportExcelUtil.mergeCell(sheet, i, k, i+1, (short) k);
				row = sheet.getRow(i+1);
				if(row==null)
					row = sheet.createRow(i+1);
				csCell = row.createCell((short) (k));
				csCell.setCellStyle(cellStyle);
				typeMap.put(k+"", "N");
				widthMap.put(k+"", "15");
				i++;
			} else {
				if ("8".equals(rsid) || "9".equals(rsid)|| "17".equals(rsid)) {
					if(isShowHead==null|| "1".equals(isShowHead))
					{
						if(isShowSeria==null|| "1".equals(isShowSeria))
						{
			        		LazyDynaBean abean = new LazyDynaBean();
		    	    		abean.set("itemid", "xuhao");
			        		abean.set("itemdesc", "序号");
			        		abean.set("itemtype", "N");
			         		headList.add(0, abean);
						}
					}
				}
				if("6".equals(rsid)|| "15".equals(rsid))
				{
					if(isShowHead==null|| "1".equals(isShowHead))
					{
						LazyDynaBean abean = new LazyDynaBean();
	    	    		abean.set("itemid", "renshu");
		        		abean.set("itemdesc", "人数");
		        		abean.set("itemtype", "N");
		         		headList.add(0, abean);
					}
				}
				if("7".equals(rsid)|| "11".equals(rsid)|| "16".equals(rsid))
				{
					if(isShowHead==null|| "1".equals(isShowHead))
					{
						LazyDynaBean abean = new LazyDynaBean();
						if("11".equals(rsid))
	             		{		             		
	         				 abean.set("itemid","count");
	    	    			 abean.set("itemdesc","人数");
	    	    			 abean.set("itemtype", "N");
	    	    			 headList.add(0,abean);
	    	    			 abean = new LazyDynaBean();
	    	    			 abean.set("itemid","e0122");
	    	    			 abean.set("itemtype", "A");
	    		    		 abean.set("itemdesc",(isShowUnitData==null|| "UM".equalsIgnoreCase(isShowUnitData))?"部门":"单位");
	    		    		 headList.add(0,abean);
	             		}
						if(isShowSeria==null|| "1".equals(isShowSeria))
						{
							abean = new LazyDynaBean();
		             		abean.set("itemid", "xuhao");
		            		abean.set("itemdesc", "序号");
		            		abean.set("itemtype", "N");
		             		headList.add(0, abean);
						}
						
					}
				}
				if ("5".equals(rsid) || "6".equals(rsid)|| "15".equals(rsid)|| "14".equals(rsid))
				{
					
						/**显示序号*/
					if(isShowSeria==null|| "1".equals(isShowSeria))
					{
		         		k = 1;
					}
				}
				if("5".equals(rsid)|| "6".equals(rsid)|| "7".equals(rsid)|| "11".equals(rsid)|| "14".equals(rsid)|| "15".equals(rsid)|| "16".equals(rsid))
				{
					/**显示列头*/
					if(isShowHead==null|| "1".equals(isShowHead))
					{
						LazyDynaBean bean = null;
						short incream=0;
						for (int j = 0; j < headList.size(); j++) {
							bean = (LazyDynaBean) headList.get(j);
							String itemtype=(String)bean.get("itemtype");
							String nwidth="15";
							if(bean.get("nwidth")!=null)
								nwidth=(String)bean.get("nwidth");
							row = sheet.getRow(i);
							if(row==null)
								row = sheet.createRow(i);
							if(("5".equals(rsid)|| "14".equals(rsid))&&j==0&&noPage!=null&& "1".equals(noPage))
							{
								csCell = row.createCell((short) 0);
								csCell.setCellStyle(cellStyle);
								csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
								csCell.setCellValue("姓名");
								typeMap.put("0", "A");
								widthMap.put("0", "15");
								csCell = row.createCell((short) 1);
								csCell.setCellStyle(cellStyle);
								csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
								csCell.setCellValue("月度");
								typeMap.put("1", "A");
								widthMap.put("1", "15");
								incream++;
							}
							typeMap.put((k+incream)+"", itemtype);
							widthMap.put((k+incream)+"",nwidth);
							csCell = row.createCell((short)(k+incream));
							csCell.setCellStyle(cellStyle);
							csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//							csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							csCell.setCellValue((String) bean.get("itemdesc"));
							k++;
						}
					}
					else
					{
						i--;
					}
				}else if("9".equals(rsid)&&!"00".equals(id)){

					int cols=(headList.size()-5)/13;
					row = sheet.getRow(i);
					k=2;
					if(row==null)
						row = sheet.createRow(i);
					
					csCell = row.createCell(0);
					csCell.setCellStyle(cellStyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					csCell.setCellValue("序号");
					ExportExcelUtil.mergeCell(sheet, i, (short)0, i+1, (short)0);
					
					csCell = row.createCell(1);
					csCell.setCellStyle(cellStyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					LazyDynaBean bean1 = (LazyDynaBean) headList.get(1);
					csCell.setCellValue((String)bean1.get("itemdesc"));
					ExportExcelUtil.mergeCell(sheet, i, (short)1, i+1, (short)1);
					
					for(int t=0;t<13;t++){
						int x=cols*t+2;
						csCell = row.createCell(k);
						csCell.setCellStyle(cellStyle);
						csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
						if(t==12){
							csCell.setCellValue("全年");
						}else{
							csCell.setCellValue(getUpperMonth(t+1));
						}
						
						ExportExcelUtil.mergeCell(sheet, i, (short)x, i, (short)(cols+x-1));
						for(int y=0;y<cols;y++){
							csCell = row.createCell((short) (k+y+1));
							csCell.setCellStyle(cellStyle);
						}

						k+=cols;
					}
					
					csCell = row.createCell(k);
					csCell.setCellStyle(cellStyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					csCell.setCellValue("平均人数");
					ExportExcelUtil.mergeCell(sheet, i, (short)(k), i+1, (short)(k));
					
					csCell = row.createCell(k+1);
					csCell.setCellStyle(cellStyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					csCell.setCellValue("人均值");
					ExportExcelUtil.mergeCell(sheet, i, (short)(k+1), i+1, (short)(k+1));
					
					csCell = row.createCell(k+2);
					csCell.setCellStyle(cellStyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					csCell.setCellValue("合计");
					ExportExcelUtil.mergeCell(sheet, i, (short)(k+2), i+1, (short)(k+2));
					
					i++;
					k=2;
	    			LazyDynaBean bean = null;
		     		for (int j = 0; j < headList.size(); j++) {
			    		bean = (LazyDynaBean) headList.get(j);

			    		String nwidth="15";
						if(bean.get("nwidth")!=null)
							nwidth=(String)bean.get("nwidth");
			    		String itemtype=(String)bean.get("itemtype");
			    		
			    		typeMap.put(""+k, itemtype);
			    		widthMap.put(k+"", nwidth);
			    		row = sheet.getRow(i);
						if(row==null)
							row = sheet.createRow(i);
			    		if(j==0||j==1){
				    		csCell = row.createCell((short) j);
					    	csCell.setCellStyle(cellStyle);
					    	continue;
			    		}
			    		csCell = row.createCell((short) k);
				    	csCell.setCellStyle(cellStyle);
				    	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				    	csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    	csCell.setCellValue((String) bean.get("itemdesc"));

			    		k++;
		    		}
		     		
				}
				else
				{
	    			LazyDynaBean bean = null;
		     		for (int j = 0; j < headList.size(); j++) {
			    		bean = (LazyDynaBean) headList.get(j);
			    		String nwidth="15";
						if(bean.get("nwidth")!=null)
							nwidth=(String)bean.get("nwidth");
			    		String itemtype=(String)bean.get("itemtype");
			    		
			    		typeMap.put(""+k, itemtype);
			    		widthMap.put(k+"", nwidth);
			    		row = sheet.getRow(i);
						if(row==null)
							row = sheet.createRow(i);
			    		
			    		csCell = row.createCell((short) k);
				    	csCell.setCellStyle(cellStyle);
				    	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				    	csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				    	csCell.setCellValue((String) bean.get("itemdesc"));
			    		k++;
		    		}
				}
			}
			i++;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;

	}
	public String getUpperMonth(int month) {
		String mon = "";
		switch (month) {
		case 1: {
			mon = "一月";
			break;
		}
		case 2: {
			mon = "二月";
			break;
		}
		case 3: {
			mon = "三月";
			break;
		}
		case 4: {
			mon = "四月";
			break;
		}
		case 5: {
			mon = "五月";
			break;
		}
		case 6: {
			mon = "六月";
			break;
		}
		case 7: {
			mon = "七月";
			break;
		}
		case 8: {
			mon = "八月";
			break;
		}
		case 9: {
			mon = "九月";
			break;
		}
		case 10: {
			mon = "十月";
			break;
		}
		case 11: {
			mon = "十一月";
			break;
		}
		case 12: {
			mon = "十二月";
			break;
		}
		}
		return mon;
	}
	public HashMap getMap(String itemid,String value,HashMap t_map,DecimalFormat fmt)
	   {
		   HashMap map = t_map;
		   try
		   {
			    if(map.get(itemid.toLowerCase())!=null)
			    {
			        BigDecimal a = new BigDecimal((String)map.get(itemid.toLowerCase()));
				    BigDecimal b = new BigDecimal(value);
				    BigDecimal s=a.add(b);
				  
			    	map.remove(itemid.toLowerCase());
			    	map.put(itemid.toLowerCase(),fmt.format(new Double(s.toString())));
			    }
			    else
			    {
			    	map.put(itemid.toLowerCase(),fmt.format(new Double(value)));
			    }
		   }
		   catch(Exception e)
		   {
			   
		   }
		   return map;
	   }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
