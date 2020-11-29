package com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BudgetExecRateExpImpBo {
	private UserView userView;
	private Connection con;
	private String budgetYear;
	private String budgetMontn;
	private int budgetid;
	private BudgetExecrateBo ExecBO;
	public BudgetExecRateExpImpBo(Connection con, UserView userView,String budgetYear,String budgetMontn) {
		this.userView = userView;
		this.con = con;
		this.budgetYear =budgetYear;
		this.budgetMontn =budgetMontn;
		
		 ExecBO= new BudgetExecrateBo(this.con,this.userView);
		this.budgetid=ExecBO.getActualIdx(Integer.parseInt(budgetYear));
	}
	
	
	//批量导出
	public String Batchdownload(String b0110){
		String fileName="";
		HSSFWorkbook wb = null;
		try{
			wb = new HSSFWorkbook();
			//根据每个预算分类得到所有的tab_id号。
			ArrayList tabidList = getTabidList();
			int tabidCount = tabidList.size();
			String randomNum = PubFunc.getStrg();
			for(int j=0;j<tabidCount;j++){
				String[] tabTemp = ((String)tabidList.get(j)).split("`");
				String tab_name =tabTemp[1];
				String tab_id =tabTemp[0];
				fileName=this.batchDownloadPlanTableTemplate(b0110,tab_id,wb,tab_name);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(wb);
		}
		return fileName;
  }
	
	public String batchDownloadPlanTableTemplate(String B0110,String tab_id,HSSFWorkbook wb,String tab_name){
		String fileName=this.userView.getUserName()+"_budgetexec.xls";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			
			ArrayList list=ExecBO.getExecrateFieldList(B0110, Integer.parseInt(budgetYear), Integer.parseInt(tab_id));
			String sql=ExecBO.getExecrateSQL(B0110, Integer.parseInt(budgetYear), Integer.parseInt(budgetMontn), Integer.parseInt(tab_id));
		
			HSSFSheet sheet = wb.createSheet(tab_name);
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
			String exceptFlds=(",B0110,yearnum,monthnum,seq,itemid,tab_id,").toUpperCase();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
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
                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
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
						codeBuf.append("select * from organization where codesetid='UN' and codeitemid= '"+ B0110 +"'");
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
			/* 安全问题 文件下载 执行率分析-导出 xiaoyun 2014-9-20 start */
			//fileName = fileName.replace(".xls", "#");
			/* 安全问题 文件下载 执行率分析-导出 xiaoyun 2014-9-20 end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return fileName;
	}
			





	public ArrayList getTabidList(){
	
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select tab_id,tab_name,tab_type,codesetid from gz_budget_tab ");
			buf.append(" where tab_id in (");
			buf.append(" select tab_id from gz_budget_exec where budget_id="+budgetid+")");

			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String tab_id = rs.getString("tab_id");
				String tab_name = rs.getString("tab_name");
				list.add(tab_id+"`"+tab_name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	
	}


	
	public String downloadSingleTemplate(String B0110,String tab_id){
		String fileName=this.userView.getUserName()+"_budgetexec.xls";
		RowSet rs = null;
		HSSFWorkbook wb = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			
			ArrayList list=ExecBO.getExecrateFieldList(B0110, Integer.parseInt(budgetYear), Integer.parseInt(tab_id));
			String sql=ExecBO.getExecrateSQL(B0110, Integer.parseInt(budgetYear), Integer.parseInt(budgetMontn), Integer.parseInt(tab_id));

			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
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
			String mcMustExpFlds=("itemdesc,ySF,").toUpperCase();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
				if("B0110".equalsIgnoreCase(field.getName())){
					field.setCodesetid("UN");	
				}

				if (mcMustExpFlds.indexOf(field.getName().toUpperCase())<0){
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
                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
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
						codeBuf.append("select * from organization where codesetid='UN' and codeitemid= '"+ B0110 +"'");
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
			/* 安全问题 文件下载 薪资预算-执行率分析-下载模版 xiaoyun 2014-9-17 start */
			// fileName = fileName.replace(".xls", "#");
			/* 安全问题 文件下载 薪资预算-执行率分析-下载模版 xiaoyun 2014-9-17 end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(wb);
			PubFunc.closeResource(rs);
		}
		return fileName;
	}
	
	public HashMap getCodeItemMap(String codeSetWhere,String organizationWhere){
		RowSet rs = null;
		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.con);
			if(codeSetWhere.length()>0)
			{
		    	String sql = "select codesetid,codeitemid,codeitemdesc from codeitem where UPPER(codesetid) in ("+codeSetWhere+")";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemdesc").toUpperCase(), rs.getString("codeitemid"));
		    	}
			}
			if(organizationWhere.length()>0){
				String sql = "select codesetid,codeitemid,codeitemdesc from organization where UPPER(codesetid) in ("+organizationWhere.substring(1)+")";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemdesc").toUpperCase(), rs.getString("codeitemid"));
		    	}
			}
			if (codeSetWhere.indexOf("@@")>-1){
		    	String sql = "select * from DBName order by dbid";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put("@@"+rs.getString("dbname").toUpperCase(), rs.getString("pre"));
		    	}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	public boolean isListContainsNull(ArrayList list){
		boolean flag = false;//默认没有NULL数据
		int n = list.size();
		for(int i=0;i<n;i++){
			if(list.get(i)==null){
				flag = true;
				break;
			}
		}
		return flag;
	}
	public String importData(FormFile file,String B0110,String tab_id ){
		String str="0";
		InputStream stream = null;
		HSSFWorkbook wk = null;
		try{
			stream = file.getInputStream();
	    	wk = (HSSFWorkbook) WorkbookFactory.create(stream);
	    	HSSFSheet sheet = wk.getSheetAt(0);
	    	String sheetName = sheet.getSheetName();
	    	ContentDAO dao  = new ContentDAO(this.con);
	    	if(!"目录".equals(sheetName)){//如果是单个导入
	    		HSSFRow row = sheet.getRow(0);
		    	int rowNum = sheet.getPhysicalNumberOfRows()-1;
		    	int colIndexNum=row.getPhysicalNumberOfCells();
		    	HSSFCell cell=null;
		    	
		    	
				BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.con,this.userView);
				ArrayList fieldList=ExecBO.getExecrateFieldList(B0110, Integer.parseInt(budgetYear), Integer.parseInt(tab_id));
				String sql=ExecBO.getExecrateSQL(B0110, Integer.parseInt(budgetYear), Integer.parseInt(budgetMontn), Integer.parseInt(tab_id));
				String tableName= ExecBO.getRateTablename();
				
		    	RecordVo gz_budget_tabVo=null;
				gz_budget_tabVo = new RecordVo("gz_budget_tab");
				gz_budget_tabVo.setInt("tab_id", Integer.parseInt(tab_id));
				gz_budget_tabVo=dao.findByPrimaryKey(gz_budget_tabVo);
				
		    	String codesetWhere="'"+gz_budget_tabVo.getString("codesetid")+"'";
		    	String organizationWhere="";
		    	String tab_type =gz_budget_tabVo.getString("tab_type");
		    	String codesetid =gz_budget_tabVo.getString("codesetid");
	
		    	DbWizard dbw= new DbWizard(this.con);

				String mcMustExpFlds=("B0110,Tab_id,yearnum,monthnum,itemdesc,ySF,").toUpperCase();
				String canUpdateFlds=("ySF,").toUpperCase();
		    	StringBuffer updSql = new StringBuffer();	
		    	codesetWhere+="";
		

		    	ArrayList fieldItemList= new ArrayList();
		    	for(int i=0;i<colIndexNum;i++){
		    		cell=row.getCell(i);
		    		if(cell==null)	break;
		    		HSSFComment comment=cell.getCellComment();
		    		String content=comment.getString().toString();
		    		for (int j=0;j<fieldList.size();j++){
			    			Field field1=(Field)fieldList.get(j);
			    			if (field1.getName().equalsIgnoreCase(content.toLowerCase())){
			    				
			    				fieldItemList.add(field1);
			    				
			    		 		if(field1.isCode()){
					    			if("UN".equalsIgnoreCase(field1.getCodesetid())|| "UM".equalsIgnoreCase(field1.getCodesetid())|| "@K".equalsIgnoreCase(field1.getCodesetid()))
					    				organizationWhere+=",'"+field1.getCodesetid().toUpperCase()+"'";
					    			else
					    		    	codesetWhere+=",'"+field1.getCodesetid().toUpperCase()+"'";
					    		}
				
			    				
			    			}
		    				
		    			}
		    		}
		  
		   
		
		    	HashMap codeItemMap = this.getCodeItemMap(codesetWhere, organizationWhere);		    		
		    	String itemid="";
		    	for (int r = 1; r <= rowNum; r++) {
		    		updSql.setLength(0);
		    		updSql.append("update "+tableName+" set ");  
		    		row = sheet.getRow(r);
		    		ArrayList list = new ArrayList();	
		    		String updfld="";
		    		for(int i=0;i<fieldItemList.size();i++){
		    			String value=null;
		    			cell=row.getCell(i);
		    			
		    			Field item =(Field)fieldItemList.get(i);
		    			int decwidth=item.getDecimalDigits();
		    			
		    			switch(cell.getCellType()){
			    			case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								value = String.valueOf( cell.getNumericCellValue());
								if (value.indexOf("E") > -1)
								{ 
									String x1 = value.substring(0, value.indexOf("E"));
									String y1 = value.substring(value.indexOf("E") + 1);
	
									value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
								}
								value = PubFunc.round(value, decwidth);
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getRichStringCellValue().toString();
								if(value.trim().length()>0){
									if(item.isCode()){
										value=(String)codeItemMap.get(item.getCodesetid().toUpperCase()+value.trim());
									}
									if("itemdesc".equalsIgnoreCase(item.getName()))
									    itemid=(String)codeItemMap.get(codesetid.toUpperCase()+value.trim());
			    		    	}
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value=null;
								break;
							default:
								value = null;
		    			}
		    			
		    			if (canUpdateFlds.indexOf(item.getName().toUpperCase()+",")>-1){
		    				if (value==null){value="null";}
			    			String s= item.getName()+"=" +value;
			    			if ("".equals(updfld)){
			    				updfld=s;
			    			}
			    			else {
			    				updfld=updfld+","+s;
			    				
			    			}
			    			list.add(value);
		    			}

	    			}
		    		updSql.append(updfld);
		    		updSql.append(" where yearnum =" + this.budgetYear);
		    		updSql.append(" and monthnum =" + this.budgetMontn);
		    		updSql.append(" and tab_id ="+tab_id );
		    		updSql.append(" and B0110 ='" +B0110+ "'" );
		    		updSql.append(" and itemid ='" +itemid+ "'" );
	
	    			if(isListContainsNull(list)){
	    				str = "单个导入出错：请选择正确的模板！";
	    				return str;
	    			}
		    		dao.update(updSql.toString());
	    		
		    	}
		    		
		    }

	    		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(wk);
			PubFunc.closeIoResource(stream);
		}
		return str;
	}
	
	

	
}
