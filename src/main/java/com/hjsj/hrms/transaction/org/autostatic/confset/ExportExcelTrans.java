package com.hjsj.hrms.transaction.org.autostatic.confset;

import java.io.FileOutputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.RowSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
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
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String fieldsetid = (String)hm.get("subset");
		fieldsetid=fieldsetid!=null?fieldsetid:"";
		
		String getyear = (String)hm.get("yearnum");
		String getmonth = (String)hm.get("monthnum");
		getmonth=getmonth!=null&&getmonth.length()>0?getmonth:"0";
		
		String grade = (String)hm.get("level");
		grade=grade!=null?grade:"0";
		
		String areavalue = (String)hm.get("areavalue");
		areavalue=areavalue!=null&&areavalue.length()>0?areavalue:"";
		
		String viewhide = (String)hm.get("hideitemid");
		viewhide=viewhide!=null&&viewhide.length()>1?viewhide:"";
		if(viewhide.trim().length()>0)
			viewhide=hideColumn(fieldsetid,viewhide);
		else{
			if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
				viewhide="(select max(codeitemid) from  ORGANIZATION where codesetid='UN' and codeitemid="+Sql_switcher.substr("a.E01A1", "1", Sql_switcher.length("codeitemid"))+") as UN,(select parentid from organization where codeitemid=a.E01A1 group by parentid) as B0110,E01A1,id";
			}else{
				viewhide="B0110,id";
			}
		}
		
		String changeflag = "1";
		if("0".equals(getmonth)){
			changeflag="2";
		}
        
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn()); 
		String sept = sysoth.getAttributeValues(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		String level = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		sept = sept!=null?sept:"";
		
		DataSynchroBo dsbo = new DataSynchroBo(this.userView, fieldsetid, dao,"", getyear, getmonth, changeflag); 
			
		String sqlstr = dsbo.getdiselelctsql(viewhide.toUpperCase())+ dsbo.getdiwheresql(areavalue,grade);
		String outname=PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		try{
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			
			row = sheet.createRow((short)0);
			DataCondBo dabo = new DataCondBo(this.userView,this.frameconn);
			ArrayList itemlist = (ArrayList)dabo.fieldList(fieldsetid);
			for(int i=0;i<itemlist.size();i++){
				Field fielditem = (Field)itemlist.get(i);
				
				HSSFCellStyle style = workbook.createCellStyle();
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
				//row = sheet.createRow((short)0);
				row = sheet.getRow((short)0);
				if(row==null)
					row = sheet.createRow((short)0);

				csCell =row.createCell((short)i);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(fielditem.getLabel());
				csCell.setCellStyle(style);
			}
			
			HSSFCellStyle style = workbook.createCellStyle();
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.RIGHT );
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			
			RowSet rset=dao.search(sqlstr.toString());
			int n=1;
			while(rset.next()){
				//row = sheet.createRow((short)n);
				row = sheet.getRow((short)n);
				if(row==null)
					row = sheet.createRow((short)n);
				for(int i=0;i<itemlist.size();i++){
					Field fielditem = (Field)itemlist.get(i);
					ResultSetMetaData rsetmd=rset.getMetaData();
					String fieldesc = getColumStr(rset,rsetmd,fielditem.getName());
					fieldesc = fieldesc!=null?fieldesc:"";
					String codesetid = fielditem.getCodesetid();
					String desc = "";
					if(fielditem.isCode()){
						if(fieldesc.length()>0){
							if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)
									|| "@K".equalsIgnoreCase(codesetid)){
								desc = AdminCode.getCodeName("UN",fieldesc);
								if(desc.length()<1){
									if("K".equalsIgnoreCase(fieldsetid.substring(0,1)) && sept.length()>0 && "b0110".equalsIgnoreCase(fielditem.getName())){
										desc=AdminCode.getCodeName("UN", rset.getString("UN"));
										OrgDataBo odb = new OrgDataBo(null,null);
										desc = desc + odb.getUpOrgUMDesc(rset.getString("b0110"),sept,level,dao)+sept;
									}
									
									desc += AdminCode.getCodeName("UM",fieldesc);
									if(desc.length()<1){
										desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(desc);
										if(i==0){
											csCell.setCellStyle(style);
										}
									}else{
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(getReplace(rset.getInt("grade"))+desc);
										if(i==0){
											csCell.setCellStyle(style);
										}
									}
								}else{
									
									csCell =row.createCell((short)i);
									//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
									csCell.setCellValue(getReplace(rset.getInt("grade"))+desc);
									if(i==0){
										csCell.setCellStyle(style);
									}
								}
							}else{
								desc = AdminCode.getCodeName(codesetid,fieldesc);
								csCell =row.createCell((short)i);
								//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
								csCell.setCellValue(desc);
								if(i==0){
									csCell.setCellStyle(style);
								}
							}
						}
					}else{
						String type = getype(fielditem.getDatatype());
						type=type!=null&&type.trim().length()>0?type:"";
						if("N".equalsIgnoreCase(type)){
							FieldItem efi=DataDictionary.getFieldItem(fielditem.getName());
							if(efi.getDecimalwidth()>0){
								if(fieldesc!=null&&fieldesc.trim().length()>0){
									desc = getFloat(fieldesc,efi.getDecimalwidth());
									if(!"0".equalsIgnoreCase(desc)){
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(Double.parseDouble(desc));
										if(i==0){
											csCell.setCellStyle(style);
										}
									}
								}
							}else{
								if(fieldesc!=null&&fieldesc.trim().length()>0){
									desc = Math.round(Float.parseFloat(fieldesc))+"";
									if(!"0".equalsIgnoreCase(desc)){
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(Integer.parseInt(desc));
										if(i==0){
											csCell.setCellStyle(style);
										}
									}
								}
							}
						}else if("D".equalsIgnoreCase(type)){
							
							csCell =row.createCell((short)i);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							csCell.setCellValue(fieldesc);
							if(i==0){
								csCell.setCellStyle(cellStyle);
							}else{
								csCell.setCellStyle(cellStyle);
							}
						}else{
							csCell =row.createCell((short)i);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							csCell.setCellValue(fieldesc);
							if(i==0){
								csCell.setCellStyle(style);
							}
						}
					}
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
		}finally {
			PubFunc.closeResource(workbook);
		}
		//outname=outname.replaceAll(".xls","#");
		this.getFormHM().put("outName",PubFunc.encrypt(outname));
		
	}
	/**
	 * 得到excel文件的标题
	 * @param setname
	 * @param viewhide
	 * @return list
	 */
	public ArrayList getItemlist11(String setname,String viewhide){
		ArrayList retlist=new ArrayList();
		String[] arr = viewhide.split(",");
		
		if(!setname.startsWith("K")){
			FieldItem fi=DataDictionary.getFieldItem("B0110");
			retlist.add(0,fi);
		}else{
			FieldItem fi=new FieldItem();
			fi.setItemid("B0110");
			fi.setFieldsetid("organization");
			fi.setCodesetid("UM");
			fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
			
			FieldItem efi=DataDictionary.getFieldItem("E01a1");
			retlist.add(0,fi);
			retlist.add(1,efi);
		}
		FieldItem fi=new FieldItem();
		fi.setItemid("id");
		fi.setFieldsetid("0");
		fi.setItemtype("D");
		fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
		retlist.add(fi);
		
		ArrayList fieldset = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET); 
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			for(int j=0;j<arr.length;j++){
				if(fielditem.getItemid().equalsIgnoreCase(arr[j])){
					retlist.add(fielditem);
				}
			}
		}
		return retlist;
	}
	
	private String hideColumn(String fieldsetid,String hidecol){
		StringBuffer strcol=new StringBuffer();
		if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			hidecol=hidecol.toUpperCase();
			hidecol=hidecol.replaceAll("B0110,","");
			hidecol=hidecol.replaceAll("E01A1,","");
			hidecol=hidecol.replaceAll("ID,","");
			strcol.append("(select max(codeitemid) from  ORGANIZATION where codesetid='UN' and codeitemid="+Sql_switcher.substr("a.E01A1", "1", Sql_switcher.length("codeitemid"))+") as UN,");
			strcol.append("(select parentid from organization where codeitemid=a.E01A1 group by parentid) as B0110,E01A1,id,");
		}else{
			hidecol=hidecol.toUpperCase();
			hidecol=hidecol.replaceAll("B0110,ID,","");
			strcol.append("B0110,id,");
		}
		strcol.append(hidecol);
		return strcol.toString();
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
				temp="二进制文件";	                    	
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
	public String getFloat(String desc,int decimalwidth){
		String fielddesc = "";
		StringBuffer temp= new StringBuffer("#0.");
		for(int i=0;i<decimalwidth;i++){
			temp.append("0");
		}
		
		DecimalFormat format = new DecimalFormat(temp.toString());
		double a=0;
		if(desc!=null&&desc.trim().length()>0){
			a = Double.parseDouble(desc);
			fielddesc = format.format(a);
		}
		
		return fielddesc;
	}
	public String getReplace(int grade){
		StringBuffer fielddesc = new StringBuffer();
		for(int i=1;i<grade;i++){
			fielddesc.append("    ");
		}
		
		return fielddesc.toString();
	}
	public String getype(int type) {
		String temp="A";
		
		if(type==DataType.STRING){
			temp="A";
		}else if(type==DataType.DATE){
			temp="D";
		}else if(type==DataType.FLOAT){
				temp="N";
		}else if(type==DataType.CLOB){
			temp="M";
		}
		
		return temp;
	}
	
}



