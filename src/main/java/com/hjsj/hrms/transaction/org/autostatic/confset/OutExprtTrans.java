package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class OutExprtTrans extends IBusiness {

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
        //导出岗位模板时使用
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn()); 
		//部门层级分隔符
		String sept = sysoth.getAttributeValues(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		//显示层级数
		String level = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		sept = sept!=null?sept:"";
		
		DataSynchroBo dsbo = new DataSynchroBo(this.userView, fieldsetid, dao,"", getyear, getmonth, changeflag); 
			
		String sqlstr = dsbo.getdiselelctsql(viewhide.toUpperCase())+ dsbo.getdiwheresql(areavalue,grade);
		String outname=this.userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		ExecuteExcel executeExcel=new ExecuteExcel(this.getFrameconn());
		HSSFCellStyle styletitle = executeExcel.style(workbook,1);
		styletitle.setWrapText(true);
		styletitle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
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
		try{
			
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			row = sheet.createRow((short)0);
			DataCondBo dabo = new DataCondBo(this.userView,this.frameconn);
			ArrayList itemlist = dabo.fieldList(fieldsetid);
			int m=0;
			for(int i=0;i<itemlist.size();i++){
				Field fielditem = (Field)itemlist.get(i);
				if(fielditem.getName().equalsIgnoreCase(fieldsetid+"Z1"))
					continue;
				if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
					if(!"id".equalsIgnoreCase(fielditem.getName())
							&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())
							&&!"UM".equalsIgnoreCase(fielditem.getCodesetid())
							&&!"@K".equalsIgnoreCase(fielditem.getCodesetid()))
						if("0".equals(userView.analyseFieldPriv(fielditem.getName())))
							continue;
				}else
					if(!"id".equalsIgnoreCase(fielditem.getName())
							&&!"UN".equalsIgnoreCase(fielditem.getCodesetid()))
						if("0".equals(userView.analyseFieldPriv(fielditem.getName())))
							continue;
				if(fielditem.isCode()){
					if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
						if(!"id".equalsIgnoreCase(fielditem.getName())
								&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())
								&&!"UM".equalsIgnoreCase(fielditem.getCodesetid())
								&&!"@K".equalsIgnoreCase(fielditem.getCodesetid())){
								continue;
							}
						}else{
							if(!"id".equalsIgnoreCase(fielditem.getName())
									&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())){
									continue;
								}
						}
				}
				
				HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 0));
				comment.setString(new HSSFRichTextString(fielditem.getName()));
				comment.setAuthor(fielditem.getName());
				
				//row = sheet.createRow((short)0);
				row = sheet.getRow(0);
				if(row==null)
					row = sheet.createRow(0);
				csCell =row.createCell((short)m);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(fielditem.getLabel());
				csCell.setCellStyle(styletitle);
				csCell.setCellComment(comment);
				m++;
			}
			RowSet rset=dao.search(sqlstr.toString());
			int n=1;
			while(rset.next()){
				//row = sheet.createRow((short)n);
				row = sheet.getRow(n);
				if(row==null)
					row = sheet.createRow(n);

				int j=0;
				for(int c=0;c<itemlist.size();c++){
					Field fielditem = (Field)itemlist.get(c);
					if(fielditem.getName().equalsIgnoreCase(fieldsetid+"Z1"))
						continue;
					ResultSetMetaData rsetmd=rset.getMetaData();
					String fieldesc = getColumStr(rset,rsetmd,fielditem.getName());
					fieldesc = fieldesc!=null?fieldesc:"";
					
					if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
						if(!"id".equalsIgnoreCase(fielditem.getName())
								&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())
								&&!"UM".equalsIgnoreCase(fielditem.getCodesetid())
								&&!"@K".equalsIgnoreCase(fielditem.getCodesetid()))
							if("0".equals(userView.analyseFieldPriv(fielditem.getName())))
								continue;
					}else
						if(!"id".equalsIgnoreCase(fielditem.getName())
								&&!"UN".equalsIgnoreCase(fielditem.getCodesetid()))
							if("0".equals(userView.analyseFieldPriv(fielditem.getName())))
								continue;
					if(fielditem.isCode()){
						if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
							if(!"id".equalsIgnoreCase(fielditem.getName())
									&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())
									&&!"UM".equalsIgnoreCase(fielditem.getCodesetid())
									&&!"@K".equalsIgnoreCase(fielditem.getCodesetid())){
									continue;
								}
							}else{
								if(!"id".equalsIgnoreCase(fielditem.getName())
										&&!"UN".equalsIgnoreCase(fielditem.getCodesetid())){
										continue;
									}
							}
					}

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
										OrgDataBo odb = new OrgDataBo(null, null);
										desc = desc + odb.getUpOrgUMDesc(rset.getString("b0110"),sept,level,dao)+sept;
									}
									desc += AdminCode.getCodeName("UM",fieldesc);
									
									if(desc.length()<1){
										desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
										csCell =row.createCell((short)j);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(desc);
										HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
										comment.setString(new HSSFRichTextString(fieldesc));
										comment.setAuthor(fielditem.getName());
										csCell.setCellComment(comment);
										csCell.setCellStyle(styletitle);
									}else{
										csCell =row.createCell((short)j);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(getReplace(rset.getInt("grade"))+desc);
										HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
										comment.setString(new HSSFRichTextString(fieldesc));
										comment.setAuthor(fielditem.getName());
										csCell.setCellComment(comment);
										csCell.setCellStyle(styletitle);
									}
								}else{
									
									csCell =row.createCell((short)j);
									//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
									HSSFRichTextString textstr = new HSSFRichTextString(getReplace(rset.getInt("grade"))+desc);
									csCell.setCellValue(textstr);
									HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
									comment.setString(new HSSFRichTextString(fieldesc));
									comment.setAuthor(fielditem.getName());
									csCell.setCellComment(comment);
									csCell.setCellStyle(styletitle);
								}
							}else{
								desc = AdminCode.getCodeName(codesetid,fieldesc);
								csCell =row.createCell((short)j);
								//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
								HSSFRichTextString textstr = new HSSFRichTextString(desc);
								csCell.setCellValue(textstr);
								HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
								comment.setString(new HSSFRichTextString(fieldesc));
								comment.setAuthor(fielditem.getName());
								csCell.setCellComment(comment);
								csCell.setCellStyle(styletitle);
								continue;
							}
						}else{
							if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)
									|| "@K".equalsIgnoreCase(codesetid)){
								csCell =row.createCell((short)j);
								//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
								HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
								csCell.setCellValue(textstr);

								csCell.setCellStyle(styletitle);
							}else
								continue;
						}
					}else{
						String type = getype(fielditem.getDatatype());
						type=type!=null&&type.trim().length()>0?type:"";
						csCell =row.createCell((short)j);
						if("N".equalsIgnoreCase(type)){
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
								HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
								csCell.setCellValue(textstr);
							}
						}else if("D".equalsIgnoreCase(type)){
							HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
							csCell.setCellValue(textstr);
							csCell.setCellStyle(styleD);
						}else{
							csCell =row.createCell((short)j);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							HSSFRichTextString textstr = new HSSFRichTextString(fieldesc);
							csCell.setCellValue(textstr);
							if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
								if("E01A1".equalsIgnoreCase(fielditem.getName())||
										"E0122".equalsIgnoreCase(fielditem.getName())||
										"B0110".equalsIgnoreCase(fielditem.getName())){
									HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
									comment.setString(new HSSFRichTextString(fieldesc));
									comment.setAuthor(fielditem.getName());
									csCell.setCellComment(comment);
									csCell.setCellStyle(styletitle);
								}else
									csCell.setCellStyle(styletext);
							}else{
								if("B0110".equalsIgnoreCase(fielditem.getName())){
									HSSFComment comment = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short)6, 4));
									comment.setString(new HSSFRichTextString(fieldesc));
									comment.setAuthor(fielditem.getName());
									csCell.setCellComment(comment);
									csCell.setCellStyle(styletitle);
								}else
									csCell.setCellStyle(styletext);
							}
						}
					}
					j++;
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
		//outname=outname.replaceAll(".xls","#");
		outname = PubFunc.encrypt(outname);  //add by wangchaoqun on 2014-10-8
		this.getFormHM().put("outName",outname);
		
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
			strcol.append("(select max(codeitemid) from  ORGANIZATION where codesetid='UN' and codeitemid="+Sql_switcher.substr("a.E01A1", "1", Sql_switcher.length("codeitemid"))+") as UN,(select parentid from organization where codeitemid=a.E01A1 group by parentid) as B0110,E01A1,id,");
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
		}else if(type==DataType.FLOAT||type==DataType.INT||type==DataType.DOUBLE){
				temp="N";
		}else if(type==DataType.CLOB){
			temp="M";
		}
		
		return temp;
	}
}





