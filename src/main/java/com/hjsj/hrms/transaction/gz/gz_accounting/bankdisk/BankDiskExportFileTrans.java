package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:BankDiskExportFileTrans.java</p>
 * <p>Description:银行报盘导出文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2007.08.14 13:58:44 pm</p>
 * @author Owner
 * @version 4.0
 */

public class BankDiskExportFileTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String fileType=(String)this.getFormHM().get("fileType");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String bank_id=(String)this.getFormHM().get("bank_id");
			String t_name="TT"+this.userView.getUserName()+"_gz_b";
			String code=(String)this.getFormHM().get("code");
			String filterSql=(String)this.getFormHM().get("ids");
			String before=(String)this.getFormHM().get("before");
			if(filterSql!=null&&filterSql.trim().length()>0){
			/* 银行报盘-报盘-选好要导出的格式出现空白页面，后台报错 xiaoyun 2014-9-23 start */
				//filterSql = SafeCode.decode(filterSql);
				//filterSql=PubFunc.keyWord_reback(filterSql);
				filterSql=PubFunc.decrypt(SafeCode.decode(filterSql));
			}
			before=PubFunc.decrypt(SafeCode.decode(before));
			//before=PubFunc.keyWord_reback(SafeCode.decode(before));
			//filterSql=PubFunc.keyWord_reback(filterSql);
		
			/* 银行报盘-报盘-选好要导出的格式出现空白页面，后台报错 xiaoyun 2014-9-23 end */
			String model=(String)this.getFormHM().get("model");
			String bosdate="";
			String boscount="";
			String spSQL="";
			 SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			  String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			  String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			  if("0".equals(model))
			  {
	    		  if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
	        			  tableName=this.userView.getUserName()+"_salary_"+salaryid;
	    		  else
	     			  tableName=manager+"_salary_"+salaryid;
			  }
			  else
			  {
				  tableName="salaryhistory";
				  bosdate=(String)this.getFormHM().get("bosdate");
				  boscount=(String)this.getFormHM().get("boscount");
				  SalaryReportBo srb=new SalaryReportBo(this.getFrameconn(),salaryid,"");
				  spSQL=srb.getSpSQL(this.getUserView(), boscount, bosdate,model);
			  }
			  CashListBo clb = new CashListBo(this.getFrameconn(),model,salaryid);
			  clb.setUserview(this.userView);
			  String privSql=clb.getPrivSql(this.userView, gzbo);
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(),this.userView);
			bo.setSalaryid(salaryid);
			/* 银行报盘-报盘 导出文件出现空白页面 xiaoyun 2014-9-23 start */
			//String a0100s = bo.getA0100s(before,SafeCode.decode(filterSql),tableName,priv_mode,privSql,model,spSQL);
			String a0100s = bo.getA0100s(before,filterSql,tableName,priv_mode,privSql,model,spSQL);
			/* 银行报盘-报盘 导出文件出现空白页面 xiaoyun 2014-9-23 end */
			String outName="";
			HashMap map =bo.getCheckAndFormat(bank_id);
			//在表头或表尾设置信息: bankCheck的值,0:不加,1:在首行,2:在末行.
			String bankCheck=(String)map.get("bankcheck");
			if(bankCheck==null)
				bankCheck="0";
			String format=(String)map.get("bankformat");
			/**
			 * 第一列，即列名
			 */
			HashMap salarySetMap = bo.getSalarySetFields(salaryid);
			
			ArrayList columns=bo.getColumns(bank_id,salarySetMap);
			/**
			 * 每一行中。每一列的取值。bean的属性
			 */
			ArrayList columnsList=bo.getTemplateColumns(bank_id,salarySetMap);
			/**
			 * 真正的数据列表
			 */
			HashMap hm = bo.getFormatMap(columns,bank_id);
			ArrayList dataList =bo.getFilterResult(tableName,code,columns,bo.getFieldInfoFromSalarySet(columns,salaryid,2,salarySetMap),hm,a0100s,t_name,model,boscount,bosdate,this.getUserView(),salaryid);
			
			//FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			if(!"3".equalsIgnoreCase(fileType))//不是excel
			{
				outName="BankDiskFile_"+PubFunc.getStrg()+".txt";
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
				
				try {
                    StringBuffer data_str = new StringBuffer();
                    String type = "";
                    if ("0".equalsIgnoreCase(fileType))//制表符分隔的文本文件
                    {
                        type = "\t";
                    } else if ("1".equalsIgnoreCase(fileType))//空格分隔的文本文件
                    {
                        type = " ";
                    } else if ("2".equalsIgnoreCase(fileType))//无分隔文本文件
                    {
                        type = "";
                    } else if ("4".equalsIgnoreCase(fileType))//|
                    {
                        type = "|";
                    } else {
                        type = ",";
                    }
                    if ("1".equalsIgnoreCase(bankCheck))//first
                    {
                        data_str = setFormat(data_str, format, dataList, salaryid, hm, type, tableName, a0100s, model, boscount, bosdate);
                    }
                    if ("0".equalsIgnoreCase(fileType))//制表符分隔的文本文件
                    {
                        data_str = getDataStringBuffer(data_str, columns, dataList, columnsList, "\t");
                    } else if ("1".equalsIgnoreCase(fileType))//空格分隔的文本文件
                    {
                        data_str = getDataStringBuffer(data_str, columns, dataList, columnsList, " ");
                    } else if ("2".equalsIgnoreCase(fileType))//无分隔文本文件
                    {
                        data_str = getDataStringBuffer(data_str, columns, dataList, columnsList, "");
                    } else if ("4".equalsIgnoreCase(fileType)) {
                        data_str = getDataStringBuffer(data_str, columns, dataList, columnsList, "|");
                    } else {
                        data_str = getDataStringBuffer(data_str, columns, dataList, columnsList, ",");
                    }
                    if ("2".equalsIgnoreCase(bankCheck))//last
                    {
                        data_str = setFormat(data_str, format, dataList, salaryid, hm, type, tableName, a0100s, model, boscount, bosdate);
                    }
                    fileOut.write(data_str.toString().getBytes());
                } catch (Exception e) {
                    
                }
                finally{
                    PubFunc.closeResource(fileOut);
                }
				/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 start */
				//outName=outName.replace(".txt","#");
				/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 end */
			}
			else//excel文件
			{
				outName=this.userView.getUserName()+"_BankDiskFile.xls";
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet();
				HSSFRow row=null;
				HSSFCell csCell=null;
				short n=0;
				if("1".equalsIgnoreCase(bankCheck))//first
				{
				   n=setExcelFormat(n,format,workbook,sheet,dataList,salaryid,hm, tableName, a0100s, model, boscount, bosdate);
				}
				n=setHead(n,columns,columnsList,workbook,sheet);
				//String[][] data_arr=getDataArray(columns,dataList);
				HashMap typeMap = bo.getitemtype(bank_id, salarySetMap);
				HashMap formatMap = bo.getItemFormatMap(bank_id, salarySetMap);
				 String macth="[0-9]+(.[0-9]+)?";
				 for(int i=0;i<dataList.size();i++)
				 {
					 row = sheet.createRow((short)(n));
					 LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
					 for(int j=0;j<columns.size();j++)
					 {
						 String itemid=(String)columns.get(j);
						 String itemtype=(String)typeMap.get(itemid.toUpperCase());
						 csCell =row.createCell((short)j);
						 String value=String.valueOf(bean.get(itemid));
						 if("0".equals(itemtype))
						 {
							 if(value.trim().matches(macth))
							 {
								 HSSFCellStyle cellStyle = workbook.createCellStyle();
								 FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
								 if(formatMap.get(itemid.toUpperCase())!=null) {
									 HSSFDataFormat formatT= workbook.createDataFormat();
									 cellStyle.setDataFormat(formatT.getFormat(String.valueOf(formatMap.get(itemid.toUpperCase())))); 
								 }else if(fieldItem != null && !"A00Z1".equalsIgnoreCase(itemid) && !"A00Z3".equalsIgnoreCase(itemid)) {
									 int decimalwidth = fieldItem.getDecimalwidth();
									 if(decimalwidth == 0) 
										 cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
									 else if(decimalwidth != 0) {
										 StringBuffer formatDecimal = new StringBuffer("0.");
										 for(int k = 0; k < decimalwidth; k++) {
											 formatDecimal.append("0");
										 }
										 HSSFDataFormat formatT= workbook.createDataFormat();
										 cellStyle.setDataFormat(formatT.getFormat(formatDecimal.toString()));
									 }
								 }
								 csCell.setCellStyle(cellStyle);
								 csCell.setCellValue(Double.parseDouble(value));//dml 2011年8月19日16:07:40
							 }
							 else if(formatMap.get(itemid.toUpperCase())!=null)
							 {
								 csCell.setCellValue(value);
							 }
							 else
							 {
//						    	 csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				    			 csCell.setCellValue(value);
							 }
						 }
						 else
						 {
//							 csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			    			 csCell.setCellValue(value);
						 }
					 }
					 n++;
				 }
				/*for(short k=0;k<data_arr.length;k++)
				{
					row = sheet.createRow((short)(n));
					for(short m=0;m<data_arr[k].length;m++)
					{
						 csCell =row.createCell(m);
						 String value=data_arr[k][m];
						 if(value==null)
							 value="";
						 if(value.matches(macth))
						 {
							 csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							 csCell.setCellValue(Double.parseDouble(value));
						 }
						 else
						 {
					    	 csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			    			 csCell.setCellValue(data_arr[k][m]);
						 }
					}
					n++;
				}*/
				
				if("2".equalsIgnoreCase(bankCheck))//last
				{
					setExcelFormat(n,format,workbook,sheet,dataList,salaryid,hm, tableName, a0100s, model, boscount, bosdate);
				}
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
				workbook.write(fileOut);
				fileOut.close();	
				sheet=null;
				workbook=null;
				/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 start */
				//outName=outName.replace(".xls","#");
				/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 end */
			}
			/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 start */
			outName = SafeCode.encode(PubFunc.encrypt(outName));
			/* 安全问题 文件导出 薪资发放-银行报盘-报盘 xiaoyun 2014-9-23 end */
			this.getFormHM().put("outName",outName);
			this.getFormHM().put("fileType",fileType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public short setExcelFormat(short n,String format,HSSFWorkbook workbook,HSSFSheet sheet,ArrayList datalist,String salaryid,HashMap hm,String tableName,String a0100s,String model,String boscount,String bosdate){

		HSSFRow row=null;
		HSSFCell csCell=null;
		try{
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			row=sheet.createRow(n);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(this.setFormat(new StringBuffer(), format,datalist,salaryid,hm,"" ,tableName,a0100s,model,boscount,bosdate).toString());
			n++;
			n++;
		}catch(Exception e){
			e.printStackTrace();
		}
		return n;
	}
	public short setHead(short n,ArrayList columns,ArrayList columnsList,HSSFWorkbook workbook,HSSFSheet sheet){
		short w=n;
		try{
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
            HSSFRow row=null;
	    	HSSFCell csCell=null;
	    	row=sheet.createRow(w);
	    	for(short i=0;i<columns.size();i++){
		    	LazyDynaBean bean=(LazyDynaBean)columnsList.get(i);
		        csCell=row.createCell((short)(i));
		        csCell.setCellStyle(cellStyle);
//		        csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
		        csCell.setCellValue((String)bean.get((String)columns.get(i)));
 	       }
		
		}catch(Exception e){
			e.printStackTrace();
		}
		w++;
		return w;
	}
	public String[][] getDataArray(ArrayList columns,ArrayList dataList)
	{
		String[][] arr=new String[dataList.size()][columns.size()];
		try
		{
    		for(int i=0;i<dataList.size();i++)
     		{
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
    			for(int j=0;j<columns.size();j++)
    			{
     				arr[i][j]=(String)bean.get(((String)columns.get(j)).toUpperCase());
    			}
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return arr;
	
	}
	public StringBuffer getDataStringBuffer(StringBuffer buf,ArrayList columns,ArrayList dataList,ArrayList columnsList,String type)
	{
		try
		{
			/**不是excel文件，不显示列标题*/
			/*for(int k=0;k<columnsList.size();k++)
			{
				LazyDynaBean bean =(LazyDynaBean)columnsList.get(k);
				buf.append((String)bean.get((String)columns.get(k)));
				buf.append(type);
			}
			buf.append("\r\n");*/
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
				for(int j=0;j<columns.size();j++)
				{
					buf.append((String)bean.get(((String)columns.get(j)).toUpperCase()));
					buf.append(type);
				}
				buf.append("\r\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf;
	}
  public StringBuffer setFormat(StringBuffer buf,String format,ArrayList datalist,String salaryid,HashMap hm,String type,String tableName,String a0100s,String model,String boscount,String bosdate)
  {
	  StringBuffer new_buf = buf;
	  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
	  try
	  {
		 
		  if(format.indexOf("`")!=-1)
		  {
			  String[] temp=format.split("`");
			  for(int i=0;i<temp.length;i++)
			  {
				 if(temp[i]==null|| "".equals(temp[i]))
					 continue;
				 if(temp[i].indexOf("总人数")!=-1) 
				 {
					 String t = datalist.size()+"";
					 if(temp[i].indexOf("[")!=-1)
					 {
						 if(temp[i].indexOf("]")!=-1)
						 {
							 String format_str = temp[i].substring(temp[i].indexOf("[")+1,temp[i].indexOf("]"));
							 if(format_str!=null&&format_str.trim().length()>0)
							 {
								// DecimalFormat dcom = new DecimalFormat(format_str);
								// t=dcom.format((double)datalist.size());
								 t=bo.getNumberFormat(datalist.size()+"", format_str,-1);
							 }
						 }
					 }
					 new_buf.append(t+type);

				 }
				 else 
				 {
					 String itemdesc="";
					 if(temp[i].trim().indexOf("[")!=-1)
					      itemdesc=temp[i].trim().substring(0,temp[i].trim().indexOf("["));
					 else
						 itemdesc= temp[i];
					 LazyDynaBean bean=bo.getItemInfo2(itemdesc, salaryid);
					 if(bean!=null)
				     {
					   String itemid = (String)bean.get("itemid");
					   String value = temp[i];
					  if(bean.get("itemtype")!=null&& "N".equalsIgnoreCase((String)bean.get("itemtype")))
					 {
						  int itemlength=Integer.parseInt(((String)bean.get("itemlength")));
						 double d=0.00d;
						 d=bo.getCount(tableName, itemid, a0100s, model, boscount, bosdate, this.getUserView(), salaryid);
						 if(temp[i].indexOf("[")!=-1&&temp[i].indexOf("]")!=-1)
						 {
							 String for_str= temp[i].substring(temp[i].indexOf("[")+1,temp[i].indexOf("]"));
							 if(for_str==null|| "".equals(for_str))
							 {
								 if(hm.get(itemid.toUpperCase())!=null)
							    	 for_str=(String)hm.get(itemid.toUpperCase());
							 }
							 if(for_str==null|| "".equals(for_str))
								 for_str="0.00";
								 
							 value=bo.getNumberFormat(d+"",for_str,itemlength);
						 }
						 else
						 {
							 String for_str="0.00";
							 if(hm.get(itemid.toUpperCase())!=null)
						    	 for_str=(String)hm.get(itemid.toUpperCase());
							 value=bo.getNumberFormat(d+"",for_str,itemlength);
						 }
				   	 }
					  new_buf.append(value+type);
				 }
				 else
				 {
					 new_buf.append(temp[i]+type);
				 }
			  }
			  
		  }
		  }
		  else
		  {
			  if(format.indexOf("总人数")!=-1) 
				 {
				  String t = datalist.size()+"";
					 if(format.indexOf("[")!=-1)
					 {
						 if(format.indexOf("]")!=-1)
						 {
							 String format_str = format.substring(format.indexOf("[")+1,format.indexOf("]"));
							 if(format_str!=null&&format_str.trim().length()>0)
							 {
								 //DecimalFormat dcom = new DecimalFormat(format_str);
								 //t=dcom.format((double)datalist.size());
								 t=bo.getNumberFormat(datalist.size()+"", format_str,-1);
							 }
						 }
					 }
					 new_buf.append(t+type);
				 }
				 else  
				 {
					 String itemdesc="";
					 if(format.trim().indexOf("[")!=-1)
					      itemdesc=format.trim().substring(0,format.trim().indexOf("["));
					 else
						 itemdesc= format;
					 LazyDynaBean bean=bo.getItemInfo2(itemdesc, salaryid);
					 if(bean!=null)
				     {
					   String itemid = (String)bean.get("itemid");
					   String value = format;
					  if(bean.get("itemtype")!=null&& "N".equalsIgnoreCase((String)bean.get("itemtype")))
					 {
						  int itemlength=Integer.parseInt(((String)bean.get("itemlength")));
						 double d=0.00d;
						 d=bo.getCount(tableName, itemid, a0100s, model, boscount, bosdate, this.getUserView(), salaryid);
						 if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1)
						 {
							 String for_str= format.substring(format.indexOf("[")+1,format.indexOf("]"));
							 if(for_str==null|| "".equals(for_str))
							 {
								 if(hm.get(itemid.toUpperCase())!=null)
							    	 for_str=(String)hm.get(itemid.toUpperCase());
							 }
							 if(for_str==null|| "".equals(for_str))
								 for_str="0.00";
							 value=bo.getNumberFormat(d+"",for_str,itemlength);
						 }
						 else
						 {
							 String for_str="0.00";
							 if(hm.get(itemid.toUpperCase())!=null)
						    	 for_str=(String)hm.get(itemid.toUpperCase());
							 value=bo.getNumberFormat(d+"",for_str,itemlength);
						 }
					 }
					  new_buf.append(value+type);
				 }
				 else
				 {
					 new_buf.append(format+type);
				 }

		  }
		  }
		  new_buf.append("\r\n");
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return new_buf;
  }
}
