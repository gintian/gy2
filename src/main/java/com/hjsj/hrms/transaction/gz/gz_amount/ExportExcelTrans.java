package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
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
public class ExportExcelTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String getyear = (String)hm.get("yearnum");
		String ctrl_type=(String)hm.get("ctrl_type");
		String filtervalue=(String)hm.get("filtervalue");
		String spType = (String)hm.get("spType"); // 审批状态
		getyear=getyear!=null&&getyear.length()>0?getyear:"";
		
		String codeitemid = (String)hm.get("codeitemid");
		codeitemid=codeitemid!=null?codeitemid:"";
		String cascadingctrl=(String)hm.get("cascadingctrl");
		String viewUnit=(String)hm.get("viewUnit");
		String flag = (String)hm.get("flag");
		flag=flag!=null?flag:"1";
		
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid=fieldsetid!=null?fieldsetid:"";
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
		HashMap valueMap = bo.getValuesMap();
		if(fieldsetid.trim().length()<1){
			fieldsetid = (String)valueMap.get("setid");
		}
		String un = "ctrl_item";
		ArrayList dataList = new ArrayList();
		dataList=(ArrayList) valueMap.get(un.toLowerCase());
		//ArrayList spflaglist = (ArrayList)map.get("sp");
		String spflag=(String)(valueMap.get("sp_flag")==null?"":valueMap.get("sp_flag"));
		String fc_flag=(String)valueMap.get("fc_flag");
		
		GrossManagBo gross = new GrossManagBo(this.getFrameconn(),this.getUserView());
		gross.setCascadingctrl(cascadingctrl);
		gross.setViewUnit(viewUnit);
		gross.setFc_flag(fc_flag);
		String ctrl_peroid=(String)valueMap.get("ctrl_peroid");//年月控制标识=1按年，=0按月
		if(ctrl_peroid==null|| "".equals(ctrl_peroid))
			ctrl_peroid="0";
		String sql="";//str = gross.sqlStr(fieldsetid,getyear,codeitemid,spflag,ctrl_type,ctrl_peroid,0);
		if("1".equals(ctrl_peroid))
		{
			sql=fieldsetid!=null&&fieldsetid.length()>0?gross.getColumnSql(fieldsetid, getyear, spflag, dataList, codeitemid, ctrl_type, spType, "0"):"";
		}
		else if("2".equals(ctrl_peroid))
		{
			String filtersql = gross.getFilterSql(fieldsetid+"z0a", filtervalue, ctrl_peroid, spflag, spType);
			sql = fieldsetid!=null&&fieldsetid.length()>0?gross.getColumnSqlBySeason(fieldsetid, getyear, spflag, dataList, codeitemid, ctrl_type,filtervalue,filtersql, "0"):"";
		}
		else
		{
			String filtersql = gross.getFilterSql(fieldsetid+"z0", filtervalue, ctrl_peroid, spflag, spType);
		     sql = fieldsetid!=null&&fieldsetid.length()>0?gross.sqlStr(fieldsetid,getyear,codeitemid,spflag,ctrl_type,ctrl_peroid,1,filtervalue,filtersql, "0"):"";
		}
		String outname=this.userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
		try{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			
			ExecuteExcel executeExcel=new ExecuteExcel(this.getFrameconn());
			
			HSSFCellStyle styletitle = executeExcel.style(workbook,1);
			styletitle.setWrapText(true);
			styletitle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			styletitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styletitle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styletitle.setAlignment(HorizontalAlignment.LEFT );
			
			HSSFCellStyle styletext = executeExcel.style(workbook,1);
			styletext.setWrapText(true);
			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styletext.setAlignment(HorizontalAlignment.LEFT );
			
			HSSFCellStyle styleN = executeExcel.style(workbook,1);
			styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			HSSFDataFormat df = workbook.createDataFormat();
			styleN.setDataFormat(df.getFormat(executeExcel.decimalwidth(0)));
			
			HSSFCellStyle styleF1 = executeExcel.style(workbook,1);
			styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = workbook.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(executeExcel.decimalwidth(1)));
			
			HSSFCellStyle styleF2 = executeExcel.style(workbook,1);
			styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = workbook.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(executeExcel.decimalwidth(2)));
			
			HSSFCellStyle styleF3 = executeExcel.style(workbook,1);
			styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = workbook.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(executeExcel.decimalwidth(3)));
			
			HSSFCellStyle styleF4 = executeExcel.style(workbook,1);
			styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = workbook.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(executeExcel.decimalwidth(4)));
			
			HSSFCellStyle styleF5 = executeExcel.style(workbook,1);
			styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = workbook.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(executeExcel.decimalwidth(5)));

			HSSFCellStyle styleD = executeExcel.style(workbook,1);
			styleD.setWrapText(true);
			styleD.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
			styleD.setAlignment(HorizontalAlignment.LEFT );
			
			ArrayList itemlist = (ArrayList)gross.fieldList(ctrl_peroid,fieldsetid,spflag,dataList);
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			/* 薪资总额，下载模板最前面一列为空，和其他地方的不一致 xiaoyun 2014-10-23 start */
			//int a = 1;
			int a = 0;
			/* 薪资总额，下载模板最前面一列为空，和其他地方的不一致 xiaoyun 2014-10-23 end */
			for(int i=0;i<itemlist.size();i++){
				Field fielditem = (Field)itemlist.get(i);
				if("2".equals(flag)&&fielditem.getName().equalsIgnoreCase(spflag))
					continue;
				//由于需要展示是否为作废部门加入了单位名称显示列 B0110text 所以导出excel时不再显示原b0110
				if(!"2".equals(flag)&& "B0110".equalsIgnoreCase(fielditem.getName()))
					continue;
				
				if("2".equals(flag)&&fielditem.isReadonly()){
					if(!"B0110".equalsIgnoreCase(fielditem.getName())&&!"aaaa".equalsIgnoreCase(fielditem.getName())){
							 continue;										
					}
						
				}
				if("2".equals(flag)&&fielditem.isCode()&&!"B0110".equalsIgnoreCase(fielditem.getName())){
						 continue;					 
				}
				if("2".equals(flag)){//日期型、备注型 不下载到模板 wangrd 2015-01-30
				    if (!isVisible(fielditem))
                        continue;
				}

				row = sheet.getRow((short)0);
				if(row==null)
					row = sheet.createRow((short)0);				
				
				csCell =row.getCell(Short.parseShort(String.valueOf(a)));
				if(csCell==null)
					csCell = row.createCell(Short.parseShort(String.valueOf(a)));
					
				HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 0));
				comment.setString(new HSSFRichTextString(fielditem.getName()));
				comment.setAuthor(fielditem.getName());
				
				if("2".equalsIgnoreCase(ctrl_peroid))
				{
					if("aaaa".equalsIgnoreCase(fielditem.getName()))
					{
		     			HSSFRichTextString textstr = new HSSFRichTextString("季度");
		     			csCell.setCellValue(textstr);
					}
					else
					{
						HSSFRichTextString textstr = new HSSFRichTextString(fielditem.getLabel());
		     			csCell.setCellValue(textstr);
					}
				}
				else if("1".equalsIgnoreCase(ctrl_peroid))
				{
					if("aaaa".equalsIgnoreCase(fielditem.getName()))
					{
			     		HSSFRichTextString textstr = new HSSFRichTextString("年份");
		     			csCell.setCellValue(textstr);
					}
					else
					{
						HSSFRichTextString textstr = new HSSFRichTextString(fielditem.getLabel());
		     			csCell.setCellValue(textstr);
					}
				}
				else
				{
					HSSFRichTextString textstr = new HSSFRichTextString(fielditem.getLabel());
	     			csCell.setCellValue(textstr);
				}
				csCell.setCellStyle(styletitle);
				csCell.setCellComment(comment);
				a++;
			}
			RowSet rset=dao.search(sql);
			int n=1;
			while(rset.next()){
				row = sheet.createRow((short)n);
				/* 薪资总额，下载模板最前面一列为空，和其他地方的不一致 xiaoyun 2014-10-23 start */
				//int b=1;
				int b=0;
				/* 薪资总额，下载模板最前面一列为空，和其他地方的不一致 xiaoyun 2014-10-23 end */
				if("2".equals(flag)){
					if(fc_flag!=null&&fc_flag.length()!=0){
						if("1".equalsIgnoreCase(rset.getString(fc_flag))){
							continue;
						}
					}
				}
				for(int i=0;i<itemlist.size();i++){
					Field fielditem = (Field)itemlist.get(i);
					if("2".equals(flag)&&fielditem.getName().equalsIgnoreCase(spflag))
						continue;
					
					//由于需要展示是否为作废部门加入了单位名称显示列 B0110text 所以导出excel时不再显示原b0110
					if(!"2".equals(flag)&& "B0110".equalsIgnoreCase(fielditem.getName()))
						continue;
					if("2".equals(flag)&&fielditem.isReadonly()){
						if(!"B0110".equalsIgnoreCase(fielditem.getName())&&!"aaaa".equalsIgnoreCase(fielditem.getName())){
								 continue;							 						
						}
							
					}
					
					
					if("2".equals(flag)&&fielditem.isCode()&&!"B0110".equalsIgnoreCase(fielditem.getName())){
						 continue;
					}
	                if("2".equals(flag)){//日期型、备注型 不下载到模板 wangrd 2015-01-30
	                    if (!isVisible(fielditem))
	                        continue;
	                }
					ResultSetMetaData rsetmd=rset.getMetaData();
					String temp=fielditem.getName();
					csCell =row.createCell((short)b);
					
					if("aaaa".equalsIgnoreCase(temp))
					{
						if("1".equalsIgnoreCase(ctrl_peroid)|| "2".equals(ctrl_peroid))
						{
							temp=fieldsetid+"z0b";
						}
						if("年月标识".equalsIgnoreCase(fielditem.getLabel())){
							fielditem.setDatatype(DataType.DATE);
						}
					}
					String fieldesc = getColumStr(rset,rsetmd,temp);
					fieldesc = fieldesc!=null?fieldesc:"";
					
					if("2".equals(flag)){
						if(!"B0110".equalsIgnoreCase(fielditem.getName())&&!"aaaa".equalsIgnoreCase(fielditem.getName())){
							fieldesc="";							 							
						}
					}
					
					if(fielditem.getDataType()==DataType.FLOAT||fielditem.getDataType()==DataType.INT||fielditem.getDataType()==DataType.DOUBLE){
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						if(fielditem.getDecimalDigits()==0)
							csCell.setCellStyle(styleN);
						else if(fielditem.getDecimalDigits()==1)
							csCell.setCellStyle(styleF1);
						else if(fielditem.getDecimalDigits()==2)
							csCell.setCellStyle(styleF2);
						else if(fielditem.getDecimalDigits()==3)
							csCell.setCellStyle(styleF3);
						else if(fielditem.getDecimalDigits()==4)
							csCell.setCellStyle(styleF4);
						else if(fielditem.getDecimalDigits()==5)
							csCell.setCellStyle(styleF5);
						else if(fielditem.getDecimalDigits()>5)
							csCell.setCellStyle(styleF5);
						else
							csCell.setCellStyle(styleN);
						if(fieldesc!=null&&fieldesc.trim().length()>0){
							csCell.setCellValue(Double.parseDouble(fieldesc));
						}else{
							fieldesc="0";
							//HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
							//csCell.setCellValue(textstr);
							csCell.setCellValue(Double.parseDouble(fieldesc));
						}
					}else if(fielditem.getDataType()==DataType.DATE){
						HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
						csCell.setCellValue(textstr);
						csCell.setCellStyle(styleD);
						if("aaaa".equalsIgnoreCase(fielditem.getName())){
							HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, i+4));
							comment.setString(new HSSFRichTextString(getyear));
							comment.setAuthor(fielditem.getName());
							csCell.setCellComment(comment);
						}
					}else{
						String desc = "";
						if("2".equals(flag)&&fielditem.isCode()&& "B0110".equalsIgnoreCase(fielditem.getName()))
						{
							HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, i+2));
							desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
							if("UM".equalsIgnoreCase(fielditem.getCodesetid())&&(desc==null||desc.trim().length()<1))
							{
								desc= AdminCode.getCodeName("UN",fieldesc);
							}
							if("UN".equalsIgnoreCase(fielditem.getCodesetid())&&(desc==null||desc.trim().length()<1))
							{
								desc= AdminCode.getCodeName("UM",fieldesc);
							}
							comment.setString(new HSSFRichTextString(fieldesc));
							comment.setAuthor(fielditem.getName());
							csCell.setCellComment(comment);
						}else{
							if(fielditem.isCode())
							{
								desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
								if("UM".equalsIgnoreCase(fielditem.getCodesetid())&&(desc==null||desc.trim().length()<1))
								{
									desc= AdminCode.getCodeName("UN",fieldesc);
								}
								if("UN".equalsIgnoreCase(fielditem.getCodesetid())&&(desc==null||desc.trim().length()<1))
								{
									desc= AdminCode.getCodeName("UM",fieldesc);
								}
							}
							else
				    			desc = fieldesc;
						}
					
						HSSFRichTextString textstr = new HSSFRichTextString(desc);
						csCell.setCellValue(textstr);
						csCell.setCellStyle(styletext);
					}
					b++;
				}
				n++;
			}
		
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
		}catch(Exception e){
			e.printStackTrace();
		}
		/* 安全问题 文件导出 薪资总额 xiaoyun 2014-9-13 start */
		// outname=outname.replace(".xls","#");
		outname = SafeCode.encode(PubFunc.encrypt(outname));
		/* 安全问题 文件导出 薪资总额 xiaoyun 2014-9-13 end */
		this.getFormHM().put("outName",outname);
		
	}
	public String getColumStr(RowSet rset,ResultSetMetaData rsetmd,String str) throws SQLException{
		int j=rset.findColumn(str);
		String temp=null;
		switch(rsetmd.getColumnType(j)){
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
			    if(temp.indexOf("12:00:00")!=-1)
			        temp=PubFunc.FormatDate(rset.getDate(j));
				break;
		case Types.CLOB:
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	                    	
				break;
		case Types.BLOB:
				temp=ResourceFactory.getProperty("gz.acount.binary.files");	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  temp=String.valueOf(rset.getDouble(j));			  
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;
		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	
	   public boolean isVisible(Field fielditem) throws SQLException{
	        boolean b=true;
            FieldItem fldItem = DataDictionary.getFieldItem(fielditem.getName());
            if (fldItem!=null){
                if ("D".equalsIgnoreCase(fldItem.getItemtype())){
                    if(!fldItem.getItemid().equalsIgnoreCase(fldItem.getFieldsetid()+"Z0")){
                        b=false;
                    }
                }
                else if ("M".equalsIgnoreCase(fldItem.getItemtype())){
                    b=false;
                }   
            }
	        return b;
	    }
}
