package com.hjsj.hrms.transaction.gz.gz_accounting.cash;

import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class CashListExportExcelTrans extends IBusiness{


	//private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException 
	{
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			//String tableName=(String)this.getFormHM().get("tableName");
			String code=(String)this.getFormHM().get("code");
			String sql=(String)this.getFormHM().get("sql");
			sql=PubFunc.decrypt(SafeCode.decode(sql));
			String nmoneyid=(String)this.getFormHM().get("nmoneyid");
			String itemid=(String)this.getFormHM().get("itemid");
			String before = (String)this.getFormHM().get("before");
			String filterSql = (String)this.getFormHM().get("filterSql");
			before=PubFunc.decrypt(SafeCode.decode(before));
			filterSql=PubFunc.decrypt(SafeCode.decode(filterSql));
			CashListBo bo = new CashListBo(this.getFrameconn(),"0",salaryid);
			ArrayList moneyitemlist=bo.getSelectedMoneyItemList(nmoneyid);
			String tableName=this.userView.getUserName()+"_"+"salary"+"_"+salaryid;
			    
			  
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			String order_by = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER, this.userView);
			bo.setOrder_by(order_by);
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				tableName=this.userView.getUserName()+"_salary_"+salaryid;
		    else
				tableName=manager+"_salary_"+salaryid;
			bo.setUserview(this.userView);
			String privSql=bo.getPrivSql(this.userView, gzbo);
			if(manager.trim().length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
				 priv_mode="1";
			String outName="cashListFile_"+PubFunc.getStrg()+".xls";
			ArrayList cashList=null;
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;
			n=this.setTableHead(n,moneyitemlist,workbook,sheet);
			if(sql==null|| "".equals(sql))
			{
				cashList=bo.getCashList(code,tableName,itemid,moneyitemlist,before,filterSql,privSql,priv_mode);
			}
			else
			{
				String codeSql = bo.getCodeSql(tableName,code);
				cashList=bo.getPersonListBySql(sql,tableName,itemid,moneyitemlist,codeSql,SafeCode.decode(before),priv_mode,privSql);
			}
			String[][] data_arr=this.getData(cashList,moneyitemlist);
			String macth="[0-9]+(.[0-9]+)?";
			for(short i=0;i<data_arr.length;i++)
			{
				row=sheet.createRow((short)n);
				for(short j=0;j<data_arr[i].length;j++)
				{
					csCell =row.createCell(j);
//				    csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
				    String value=data_arr[i][j];
				    if(value==null)
				    	value="";
				    if(value.matches(macth))
					{
					   csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					   csCell.setCellValue(Double.parseDouble(value));
					}
					else
			    		csCell.setCellValue(value);
				}
				n++;
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null; 
			this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outName)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	private short setTableHead(short n,ArrayList moneyitemlist,HSSFWorkbook workbook,HSSFSheet sheet)
	{
		short i=n;
		try
		{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			row=sheet.createRow(i);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("单位人员");
			csCell=row.createCell((short)1);
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("金额");
			for(short k=0;k<moneyitemlist.size();k++)
			{
				LazyDynaBean bean = (LazyDynaBean)moneyitemlist.get(k);
				csCell=row.createCell((short)(k+2));
				csCell.setCellStyle(cellStyle);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)bean.get(String.valueOf(k)));
			}
			i++;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	private String[][] getData(ArrayList cashList,ArrayList moneyitemlist)
	{
		String[][] arr=new String[cashList.size()][(moneyitemlist.size()+2)];
		try
		{
			for(int i=0;i<cashList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)cashList.get(i);
				arr[i][0]=(String)bean.get("name");
				arr[i][1]=(String)bean.get("value");
				for(int k=0;k<moneyitemlist.size();k++)
				{
					arr[i][k+2]=(String)bean.get(String.valueOf(k));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return arr;
	}

}
