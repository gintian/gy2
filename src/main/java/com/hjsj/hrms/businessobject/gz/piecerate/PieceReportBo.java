package com.hjsj.hrms.businessobject.gz.piecerate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/** 计件薪资报表业务类
 * @author wangjh
 * 2013-3-26
 */
public class PieceReportBo {
	private Connection conn;
	private UserView userView;
	private PieceReportDefBo defBo;
	// 开始时间
	private String startDate="";
	
	// 结束时间
	private String endDate="";
	
	// 统计条件
	private String tempCond;
	
	public PieceReportBo(Connection _con, UserView _userView,int _reportId) {
		conn = _con;
		userView=_userView;
		defBo= new PieceReportDefBo(_con,_reportId);
		
		tempCond=defBo.getCondClause();

	}
	
	
	public String getSql(){
		String strSql="";
		if (this.defBo.getReportId()<1){
			strSql="select * from S01  where 1=0";
			return strSql;			
		}
		
		
		try{					
			//统计条件  
			String strfilter="";
			String tjUseSets="";
			String tjWhere=(tempCond!=null)?tempCond:defBo.getCondClause();
			if ((tjWhere!=null)&&(!"".equals(tjWhere))){
				YksjParser yp = new YksjParser( this.userView ,defBo.getAllFiledList(),
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(tjWhere);
				ArrayList setList = yp.getUsedSets();
				for (int i=0;i<setList.size();i++){
					tjUseSets=tjUseSets+","+ (String) setList.get(i)+".";
				}
				strfilter=yp.getSQL();
			}

			
			//汇总字段
			String showfields= this.defBo.getSql_ShowFields();		
			//所用到的子集
			String userSets=(tjUseSets+"  "+ showfields).toUpperCase();
			
			//组合sql
			strSql="select " +showfields+ " from S01" ;
			if (userSets.indexOf("S05.")>-1){
				strSql=strSql + " inner join S05 on S01.S0100= s05.S0100 ";
			}
			if ((userSets.indexOf("S04.")>-1)
					|| (userSets.indexOf("S02.")>-1)
					|| (userSets.indexOf("S03.")>-1)){
				strSql=strSql + " inner join S04 on S01.S0100= S04.S0100 "
	               			+ " inner join S02 on S04.S0401 = S02.S0200"
           					+ " inner join S03 on S04.S0402= S03.S0300";	 		
			}		
  	           
			strSql=strSql  + " where 1=1 "   ;
			if ((!"".equals(startDate)) && (!"".equals(endDate))){
				strSql=strSql+" and s0104 between "+Sql_switcher.dateValue(startDate)+" and "+Sql_switcher.dateValue(endDate);
			}
			if (!"".equals(this.defBo.getReportKind().trim())){
				strSql=strSql +" and s0102 = '"+this.defBo.getReportKind()+"'";					
			}
			
			//统计条件  
			if ((strfilter!=null)&&(!"".equals(strfilter))){
				strSql=strSql +" and "+strfilter;	
			}		
			
			//分组指标
			strSql=  strSql +this.defBo.getSql_GroupFields();
			
			//显示指标
	/*		if (showfields.equals("")){
				throw GeneralExceptionHandler.Handle(new Exception("没有定义需要显示指标"));
			}*/
			strSql= "select "+ defBo.getSql_FirstShowFields() +" from ("+ strSql +") A ";
		
			
			//排序指标
			strSql=  strSql +this.defBo.getSql_OrderFields();
		}catch (Exception e){
			e.printStackTrace();
		
	   }
		
		return strSql;
	}
	
	
	public ArrayList getFieldList(){	
		String S0402Desc="货名";
		String S0401Desc="操作过程";
		ArrayList ItemList = DataDictionary.getFieldList("S04",Constant.USED_FIELD_SET);
		for(int i=0;i<ItemList.size();i++)		{  
			FieldItem  fielditem = (FieldItem)ItemList.get(i);
			if ("S0402".equalsIgnoreCase(fielditem.getItemid())){
				S0402Desc=fielditem.getItemdesc();
			}
			if( "S0401".equalsIgnoreCase(fielditem.getItemid())){
				S0401Desc=fielditem.getItemdesc(); 	
			 }						

		}	
		
		ArrayList list=new ArrayList();
		StringBuffer format=new StringBuffer();	
		format.append("###################");	
		ArrayList fieldItemList =this.defBo.getAllFiledList();
		String dispFields="";
		boolean bCountNbase=false;
		if (this.defBo.getReportId()<1){
			dispFields=(","+"s0102,s0104,s0105"+"").toUpperCase();
		
		}
		else {			
			dispFields=(","+this.defBo.getSql_FirstShowFields()+",").toUpperCase();
		}
		
		String[] arrValue= dispFields.split(",");	  
		for (int j=0;j<arrValue.length;j++){
			String fld= arrValue[j];
			if ("".equalsIgnoreCase(fld)||(",".equalsIgnoreCase(fld))){continue;}
			
			for(int i=0;i<fieldItemList.size();i++)	{   
				FieldItem  fielditem = (FieldItem)fieldItemList.get(i);
				String itemid =fielditem.getItemid().toUpperCase();
				if (!itemid.equalsIgnoreCase(fld)){continue;}
			    String desc=fielditem.getItemdesc();				    

				if ("S0402".equalsIgnoreCase(fielditem.getItemid())
					       || "S0401".equalsIgnoreCase(fielditem.getItemid())){
					continue; 
				}	
				else if ("S0203".equalsIgnoreCase(fielditem.getItemid()) ){
					desc=S0401Desc;
				}		
				else if ("S0303".equalsIgnoreCase(fielditem.getItemid()) ) {
					desc=S0402Desc;					
				}		

				desc="&nbsp;&nbsp;&nbsp;"+desc+"&nbsp;&nbsp;&nbsp;";
				Field field = new Field(fielditem.getItemid(),desc);
				field.setCodesetid(fielditem.getCodesetid());
				//存在汇总字段
				String itemType = fielditem.getItemtype();
				if (this.defBo.isGroup()){
					HashMap summaryMap = this.defBo.getSummaryMap();
					if (summaryMap.get(fielditem.getItemid())!=null){
						String value =(String)summaryMap.get(fielditem.getItemid());
						if ("count".equals(value)){
							itemType="N";	
							fielditem.setItemlength(8);	
							fielditem.setDecimalwidth(0);
							if ("nbase".equalsIgnoreCase(fielditem.getItemid())){
								bCountNbase=true;
							}
						}
					}
				}
				if ("nbase".equalsIgnoreCase(fielditem.getItemid())&&(!bCountNbase)){
					field.setCodesetid("@@");
				}
				

				
				if("N".equalsIgnoreCase(itemType))
				{
					field.setLength(fielditem.getItemlength());
					field.setDecimalDigits(fielditem.getDecimalwidth());
					if(fielditem.getDecimalwidth()==0){
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}else{
						field.setDatatype(DataType.FLOAT);
						field.setFormat("####."+format.toString().substring(0,fielditem.getDecimalwidth()));
					}
					field.setAlign("right");
				}else if("D".equalsIgnoreCase(itemType)){
					field.setLength(20);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");	
				}else if("M".equalsIgnoreCase(itemType)){
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");		
				}else if("A".equalsIgnoreCase(itemType)){
					field.setDatatype(DataType.STRING);
					if(fielditem.getCodesetid()==null|| "0".equals(fielditem.getCodesetid())|| "".equals(fielditem.getCodesetid()))
						field.setLength(fielditem.getItemlength());						
					else
						field.setLength(50);
					field.setAlign("left");
				}else{
					field.setDatatype(DataType.STRING);
					field.setLength(fielditem.getItemlength());
					field.setAlign("left");			
				}
				field.setVisible(fielditem.isVisible());
				if ("S0100".equalsIgnoreCase(fielditem.getItemid())
						   || "I9999".equalsIgnoreCase(fielditem.getItemid()))
				{
					field.setVisible(false);	
				}	
				
				field.setReadonly(true);	
				field.setSortable(false);
				list.add(field);	
		   }
	    }
		
		

		return list;

	}	
	

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
		if (this.startDate==null) this.startDate="";
	}

	public String getEndDate() {
		return endDate;
	}

	public String getTempCond() {
		return tempCond;
	}


	public void setTempCond(String tempCond) {
		this.tempCond = tempCond;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
		if (this.endDate==null) this.endDate="";
	}

	
	
	public String expReport(String sql){
		String fileName="pieceReport"+this.userView.getUserName()+".xls";
		RowSet rs = null;
		HSSFWorkbook wb = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);			
			ArrayList list=getFieldList();
			//String sql=getSql();
		
			wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("计件薪资报表");
			HSSFCellStyle cellStyle=wb.createCellStyle();
			HSSFFont afont=wb.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			cellStyle.setFont(afont);
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
		    cellStyle.setAlignment(HorizontalAlignment.LEFT);
		
			
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFComment comm = null;		
			int rowNum=0;
			HSSFRow row = sheet.createRow(rowNum);
			HSSFCell cell = null;
			int index=0;
			ArrayList headList = new ArrayList();
			row.setHeight((short)500);
			ArrayList codeFieldList = new ArrayList();
			String exceptFlds=(",S0100,").toUpperCase();
			HashMap summaryMap = this.defBo.getSummaryMap();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
			
				if (defBo.isGroup()){
					
					
				}
				if("B0110".equalsIgnoreCase(field.getName())){
					field.setCodesetid("UN");	
				}

				if (exceptFlds.indexOf(field.getName().toUpperCase())>-1){
					continue;
				}

				cell=row.createCell(index);
				if(field.getDatatype()==DataType.CLOB)
					sheet.setColumnWidth(index, (short) 8000);
				else{
					if(((field.getLength()+field.getDecimalDigits())*250)>8000)
						sheet.setColumnWidth(index, (short) 8000);
					else
			        	sheet.setColumnWidth(index, (short) ((field.getLength()+field.getDecimalDigits())*250));
				}
				cell.setCellStyle(BudgetingBo.getHSSFCellStyle(wb, -2,DataType.STRING));
				HSSFRichTextString rts=new HSSFRichTextString(field.getLabel().replaceAll("&nbsp;",""));
				cell.setCellValue(rts);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (index + 1), 0, (short) (index + 3), 2));
				comm.setString(new HSSFRichTextString(field.getName()));
				cell.setCellComment(comm);
				headList.add(field);
				if(!"0".equals(field.getCodesetid()))
					codeFieldList.add(field.getCodesetid()+":"+index);
				index++;
			}
			
			rowNum++;			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			rs = dao.search(sql);
			HashMap stylesmap = new HashMap();
			while(rs.next()){
				row = sheet.createRow(rowNum);
				for(int i=0;i<headList.size();i++){
					Field field = (Field)headList.get(i);
					cell=row.createCell(i);
					String key="-1";
					if(field.getDecimalDigits()!=0)
						key=field.getDecimalDigits()+"";
					HSSFCellStyle style=null;
					if(stylesmap.get(key)!=null){
						style=(HSSFCellStyle)stylesmap.get(key);			
					}else{
						style=BudgetingBo.getHSSFCellStyle(wb, Integer.parseInt(key),field.getDatatype());
						stylesmap.put(key, style);		
					}
					if(i==0)
						cell.setCellStyle(cellStyle);
					else
				    	cell.setCellStyle(style);
                    if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT){
                    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    	float value=rs.getFloat(field.getName());
                    	cell.setCellValue(Float.parseFloat(PubFunc.round(value+"", field.getDecimalDigits())));
                    }else if(field.getDatatype()==DataType.DATE){
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(rs.getDate(field.getName())!=null)
						{
							HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(field.getName())));
							cell.setCellValue(textstr);
						}
						else
						{
							HSSFRichTextString textstr = new HSSFRichTextString("");
							cell.setCellValue(textstr);
						}
                    }else {
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(!"0".equals(field.getCodesetid()))
                    	{
                    		String value=rs.getString(field.getName());
                    		/** start---这里对系统设置的有"/"的部门进行特殊处理 **/
                    		String codeName = "";
                    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            				if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
            					display_e0122="0";		
            				if("um".equalsIgnoreCase(field.getCodesetid()) && !"0".equals(display_e0122)) {
            					CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
            					codeName = item.getCodename();
            				}else {
            					codeName = AdminCode.getCodeName(field.getCodesetid(),value==null?"":value);
            				}
            				/** end---这里对系统设置的有"/"的部门进行特殊处理 **/
                    		HSSFRichTextString textstr = new HSSFRichTextString(codeName);
                    		cell.setCellValue(textstr);
                    	}else{
                    		String value=rs.getString(field.getName());
                    		HSSFRichTextString textstr = new HSSFRichTextString(value==null?"":value);
                    		cell.setCellValue(textstr);
                    	}
                    }
				}
				rowNum++;
			}
			
			rowNum--;
			index = 0;
			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			
			int div = 0;
			int mod = 0;			
			for (int n = 0; n < codeFieldList.size(); n++)
			{
				String codeCol = (String) codeFieldList.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					if ("@@".equals(codesetid)){
						codeBuf.append("select * from dbname order by dbid");
					}
					else {						
						codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
					}
				} else
				{
					if (!"UN".equals(codesetid))
					{
						if("UM".equalsIgnoreCase(codesetid))
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
									+" order by codeitemid");
						else
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
								+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
					}
					else if ("UN".equals(codesetid)) {
						codeBuf.append("select * from organization where codesetid='UN' ");
					}	
	
				}
				rs = dao.search(codeBuf.toString());
				int m = 0;
				while (rs.next())
				{
					row = sheet.getRow(m + 0);
					if (row == null)
						row = sheet.createRow(m + 0);
					cell = row.createCell((short) (208 + index));
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)){
						if ("@@".equals(codesetid))
							cell.setCellValue(new HSSFRichTextString(rs.getString("dbname")));
						else
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
					}
					else 
					//	cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
						cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
					m++;
				}
				if(m==0)
					m=2;
				sheet.setColumnWidth((short) (208 + index), (short) 0);
				div = index/26;
				mod = index%26;
				String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
			 
				CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum, codeCol1, codeCol1);
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();	
			sheet=null;
			wb=null;
			/* 安全问题 文件下载 计件薪资-报表导出 xiaoyun 2014-9-13 start */
			// fileName = fileName.replace(".xls", "#");
			/* 安全问题 文件下载 计件薪资-报表导出 xiaoyun 2014-9-13 end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(wb);
		}
		return fileName;
	}
			
	

	
	

}
