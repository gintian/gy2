package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
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

public class OutputExcelTrans extends IBusiness {

	String year = "";
	String month ="";
	String changeflag="0";
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String setid= (String)hm.get("setid");
		setid=setid!=null?setid:"";
		
		String getyear = (String)hm.get("yearnum");
		String getmonth = (String)hm.get("monthnum");
		getmonth=getmonth!=null&&getmonth.length()>0?getmonth:"0";
		year=getyear;
		month=getmonth;
		String sp_flag = (String)hm.get("sp_flag");
		sp_flag=sp_flag!=null?sp_flag:"";
		
		String realitem = (String)hm.get("realitem");
		realitem=realitem!=null&&realitem.length()>0?realitem:"";
		
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.length()>0?flag:"";
		
		String ctrl_type = (String)hm.get("ctrl_type");
		ctrl_type=ctrl_type!=null&&ctrl_type.length()>0?ctrl_type:"";
		
		String nextlevel = (String)hm.get("nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.length()>0?nextlevel:"";
		
		String a_code = (String)hm.get("a_code");
		a_code = a_code==null?"":a_code;
		if(a_code.length()>=2)
			a_code=a_code.substring(2);
		
		String dataResourceSql = (String)hm.get("dataresource");
		dataResourceSql = PubFunc.decrypt(dataResourceSql);  //wangchaoqun 2014-11-3
		dataResourceSql = PubFunc.keyWord_reback(dataResourceSql);
		dataResourceSql = dataResourceSql != null && dataResourceSql.length()>0 ? dataResourceSql : "";
		
		StringBuffer sqlstr = new StringBuffer();
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
		FieldSet fs = DataDictionary.getFieldSetVo(setid);
		ArrayList list = gzbo.fieldList(setid);
		String fieldPri = this.userView.analyseTablePriv(setid);
		ArrayList itemlist = new ArrayList();
		for(int i=0;i<list.size();i++){
			Field fielditem = (Field)list.get(i);
			//below add by wangchaoqun on 2014-10-27 begin 输出excel时所见即所得，不显示的列不放入list中。
			if(fielditem.isVisible() == false){
				continue;
			}
			//above add by wangchaoqun on 2014-10-27 end
			String pri = this.userView.analyseFieldPriv(fielditem.getName());
			if(!"2".equals(fieldPri)){
				fielditem.setReadonly(true);
			}else{
				if("2".equals(pri))
					fielditem.setReadonly(false);
				else if("1".equals(pri))
					fielditem.setReadonly(true);
				else
					fielditem.setVisible(false);
			}
			if(fielditem.getName().equalsIgnoreCase(sp_flag)){
				fielditem.setLabel(ResourceFactory.getProperty("org.performance.status"));
				fielditem.setReadonly(true);
				itemlist.add(1,fielditem);
			}else if(realitem.indexOf(fielditem.getName().toUpperCase())!=-1){
				fielditem.setReadonly(true);
				itemlist.add(fielditem);
			}else if("I9999".equalsIgnoreCase(fielditem.getName())){
				fielditem.setReadonly(true);
				fielditem.setVisible(false);
				itemlist.add(fielditem);
			}else if("B0110".equalsIgnoreCase(fielditem.getName())){
				fielditem.setReadonly(true);
				fielditem.setVisible(true);
				itemlist.add(fielditem);
			}else{
				itemlist.add(fielditem);
			}
		}
		
		//直接用生成页面的sql来产生导出数据  以保证页面显示和导出数据同步
		sqlstr.append(SafeCode.keyWord_reback(dataResourceSql));
		
		/*
		 * 因添加编制查询功能，此方法不能导出查询结果
		 * sqlstr.append("select ");
		if(setid!=null&&setid.trim().length()>0){
			sqlstr.append(gzbo.vilStrOrg(setid,itemlist,true));	
		}
		sqlstr.append(",org.codeitemid as b0110,grade");
		sqlstr.append(gzbo.whereStrOrg1(setid,ctrl_type,nextlevel,a_code));
		
		sqlstr.append(" and (I9999=(select max(I9999) from ");
		sqlstr.append(setid);
		sqlstr.append(" where ");
		changeflag=fs.getChangeflag();
		if(!changeflag.equals("0")){
			//sqlstr.append("id='"+this.getId()+"' and ");
			sqlstr.append(setid+"z0=(select max("+setid+"z0) from "+setid+" where B0110=a.b0110) and ");
		}
		sqlstr.append(" B0110=a.B0110 )");
		sqlstr.append(" or a.B0110 is null)");
		
		sqlstr.append(" and org.codesetid in('UN','UM')");
		if(!flag.equalsIgnoreCase("all")){
			sqlstr.append(" and a."+sp_flag+"='"+flag+"'");
		}
		if(!changeflag.equals("0")){
			//sqlstr.append(" and a.id='"+this.getId()+"'");
			sqlstr.append(" and ("+setid+"z0=(select max("+setid+"z0) from "+setid+" where B0110=a.b0110) or "+setid+"z0 is null)");
		}
		sqlstr.append(" order by B0110");*/
		
		String outname=this.userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
		try(HSSFWorkbook workbook = new HSSFWorkbook()){
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			
			row = sheet.createRow((short)0);
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
									desc = AdminCode.getCodeName("UM",fieldesc);
									if(desc.length()<1){
										desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(desc);
										if(i==0){
											HSSFCellStyle style = workbook.createCellStyle();
											style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
											style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
											csCell.setCellStyle(style);
										}
									}else{
										csCell =row.createCell((short)i);
										//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
										csCell.setCellValue(getReplace(rset.getInt("grade"))+desc);
										if(i==0){
											HSSFCellStyle style = workbook.createCellStyle();
											style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
											style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
											csCell.setCellStyle(style);
										}
									}
								}else{
									
									csCell =row.createCell((short)i);
									//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
									csCell.setCellValue(getReplace(rset.getInt("grade"))+desc);
									if(i==0){
										HSSFCellStyle style = workbook.createCellStyle();
										style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
										style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
										csCell.setCellStyle(style);
									}
								}
							}else{
								desc = AdminCode.getCodeName(codesetid,fieldesc);
								csCell =row.createCell((short)i);
								//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
								csCell.setCellValue(desc);
								if(i==0){
									HSSFCellStyle style = workbook.createCellStyle();
									style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
									style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
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
											HSSFCellStyle style = workbook.createCellStyle();
											style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
											style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
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
											HSSFCellStyle style = workbook.createCellStyle();
											style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
											style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
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
								HSSFCellStyle cellStyle = workbook.createCellStyle();
								cellStyle.setAlignment(HorizontalAlignment.RIGHT);
								cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
								csCell.setCellStyle(cellStyle);
							}else{
								HSSFCellStyle cellStyle = workbook.createCellStyle();
								cellStyle.setAlignment(HorizontalAlignment.RIGHT);
								cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
								csCell.setCellStyle(cellStyle);
							}
						}else{
							csCell =row.createCell((short)i);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							csCell.setCellValue(fieldesc);
							if(i==0){
								HSSFCellStyle style = workbook.createCellStyle();
								style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
								style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
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
		}catch(Exception e){
			e.printStackTrace();
		}
//		outname=outname.replaceAll(".xls","#");
		outname = PubFunc.encrypt(outname);
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

	/**
	 * 获得年月标识时间
	 */
	private String getId(){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9)
				return year+"."+month;
			else
				return year+".0"+month;
		}
	}
}
