package com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *<p>Title:ScoreExcelBo.java</p> 
 *<p>Description:导出招聘成绩</p> 
 * @author wangjl
 *
 */
public class ScoreExcelBo {

	private Connection conn;
	private String model;
	private RowSet frowset;
	public ScoreExcelBo(Connection conn) {
		this.conn = conn;
	}

	public ScoreExcelBo(Connection conn,String model) {
		this.conn = conn;
		this.model = model;
	}

	/**
	 * 生成Excel
	 * @param batch_id 招聘批次
	 * @param userView 用户信息
	 * @return
	 */
	public String createExcel(String batch_id, UserView userView) {
		//获取查询列
		String columns = getColumns();
		//拼接条件
		String where = getSQLWhere(batch_id);
		ArrayList<String> batch_ids=new ArrayList();
		if (batch_id != null&&!"all".equalsIgnoreCase(batch_id)&&!"".equals(batch_id)){
			batch_ids.add(batch_id.trim());
		}
		//拿到所有指标项
		ArrayList<FieldItem> fieldList = getAchievementDataFieldList();

		// 生成Excel名
		String excel_filename = createFileName(userView);
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try {
			workbook = getExcelDataInfo(columns, where, batch_ids,fieldList);
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 11);
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + excel_filename);
			workbook.write(fileOut);
			workbook = null;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}

		return excel_filename;
	}

	/**
	 * 根据用户和时间生成文件名
	 * 
	 * @param userView
	 * @return
	 */
	private String createFileName(UserView userView) {
		return  userView.getUserName() + "_zp.xls";
	}

	/**
	 * 设置表格风格
	 * 
	 * @param workbook
	 * @param font
	 * @return
	 */
	private HSSFCellStyle setCellStyle(HSSFWorkbook workbook, HSSFFont font) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		return style;
	}

	/**
	 * 获取要生成的Excel表的数据
	 * 
	 * @param columns
	 * @param where
	 * @param batchIds 
	 * @param fieldList
	 * @return
	 */
	private HSSFWorkbook getExcelDataInfo(String columns, String where, ArrayList<String> batchIds, ArrayList<FieldItem> fieldList) {
		StringBuffer sql = new StringBuffer();
		int field_num = fieldList.size();
		RowSet rs = null;
		/* -----------显示部门层数-------------------------------------------------- */
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.conn); //
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122); //
		if (uplevel == null || uplevel.length() == 0) //
			uplevel = "0"; //
		/* ------------显示部门层数------------------------------------------------- */
		int iuplevel = Integer.parseInt(uplevel);
		ContentDAO dao = new ContentDAO(this.conn);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		try {
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 11);
			// 设置表格
			HSSFCellStyle style = setCellStyle(workbook, font);
			HSSFRow row = null;
			HSSFCell csCell = null;
			HSSFComment comm = null;
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			
			row = sheet.createRow(0);
			row.setHeight((short) 500);
			
			// 设置表头
			ArrayList codeCols = new ArrayList();
			for (int i = 0; i < fieldList.size(); i++) {
			    FieldItem field = (FieldItem) fieldList.get(i);
				sheet.setColumnWidth(i, field.getItemdesc().length()*650);
				csCell = row.createCell(i);
				csCell.setCellStyle(style);
				sheet.autoSizeColumn((short)i);
				csCell.setCellValue(field.getItemdesc());
				// 加标注
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,(short) (i), 0, (short) (i + 1), 1));
				comm.setString(new HSSFRichTextString(field.getItemid().toString().toLowerCase()));
				csCell.setCellComment(comm);
				
				 if ("A".equalsIgnoreCase(field.getItemtype()) && (field.getCodesetid() != null && !"".equals(field.getCodesetid()) && !"0".equals(field.getCodesetid())))
					 codeCols.add(field.getCodesetid() + ":" + i);
			}
			
			/******* 得到纪录数据 **********/
			RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
			sql = new StringBuffer();
			sql.append("select ");
			sql.append(Sql_switcher.isnull("Oth.A0177", "'未知'")+" a0177,");
			sql.append(" sle.* from "+(String) vo.getValues().get("str_value")+"A01 Oth right join (");
			sql.append("select a0100," + columns);
			sql.append(" from z63 ");
			sql.append(where);
			sql.append(")sle on Oth.a0100 = sle.a0100 ");
			sql.append("order by z6301");
			rs = dao.search(sql.toString(),batchIds);
			int j = 1;
			// 根据指标项将数据插入二维数组
			Pattern pattern = Pattern.compile("[0-9]*"); 
			Pattern pattern2 = Pattern.compile("[0-9]*\\.[0-9]*");
			String fieldValue = "";
			while (rs.next()) {
				row = sheet.createRow(j);
				for (int i = 0; i < field_num; i++) {
					csCell = row.createCell(i);
					FieldItem fieldItem = fieldList.get(i);
					String fieldName = fieldItem.getItemid();
					fieldValue = rs.getString(fieldName);
					String itemType = fieldItem.getItemtype();
					String codeSetId = fieldItem.getCodesetid();
					int maxLength = fieldItem.getItemlength();
					if (fieldValue != null) 
					{
						//判断指标类型
						if ("N".equalsIgnoreCase(itemType))
						{
							if(!"true".equals(this.model)){
								BigDecimal db = new BigDecimal(fieldValue);
								fieldValue = db.toPlainString();
								Matcher isNum = pattern.matcher(fieldValue);
								Matcher isNum2 = pattern2.matcher(fieldValue);
								if((isNum.matches()||isNum2.matches())&&!"".equals(fieldValue))
								{
									fieldValue = String.format("%."+fieldItem.getDecimalwidth()+"f", Float.parseFloat(fieldValue));
								}
								
							}else{
								if(fieldName.contains("z63")){ 
									fieldValue = "";
								}
							}
							if("z6317".equals(fieldName))
							{
								if(fieldValue.indexOf(".")>0)
									fieldValue = fieldValue.substring(0,fieldValue.indexOf("."));
							}
						}
						else if("A".equalsIgnoreCase(itemType)&&!"0".equals(codeSetId))
						{
							if ("UM".equalsIgnoreCase(codeSetId)){
								fieldValue = AdminCode.getCode(codeSetId,fieldValue, iuplevel) != null ? AdminCode.getCode(codeSetId, fieldValue,iuplevel).getCodename(): "";
							}
							 else {
								 fieldValue = AdminCode.getCode(codeSetId,fieldValue) != null ? AdminCode.getCode(codeSetId, fieldValue).getCodename() : "";
							 }
						} 
						else if("M".equals(itemType)){
							fieldValue = Sql_switcher.readMemo(rs,fieldName);
						}
						else if("D".equals(itemType)){
							fieldValue = rs.getString(fieldName)!=null? fieldValue:"";
						}
					}
					//判断指标类型
					if ("N".equalsIgnoreCase(itemType))
					{
						//因为每一行相同字段的规则都一样，这里为了方便每一行都设置一次，事实上只需要给每一列都设置就可以
						HSSFDataValidation validation = this.setValidate(1,j,i,i,maxLength);
						sheet.addValidationData(validation);
					}
					csCell.setCellValue(fieldValue);
				}
				j++;
			}
			
			
			for (int n = 0; n < codeCols.size(); n++) {
			    String strFormula = "";
			    String codeCol = (String) codeCols.get(n);
			    String[] temp = codeCol.split(":");
			    String codesetid = temp[0];
			    int colNumber = Integer.valueOf(temp[1]).intValue();
			    StringBuffer codeBuf = new StringBuffer();
			    if ("UM".equals(codesetid) || "UN".equals(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
			    	continue;
			    }
		        codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'"); 
		        this.frowset = dao.search(codeBuf.toString());
		        if (this.frowset.next()) {
		            if (this.frowset.getInt(1) >= 500) // 代码型中指标大于500的时候，就不再加载了 
		            	continue;

		            codeBuf.setLength(0);
		            codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' order by codeitemid"); // zhaoguodong 2013.09.23 使获取的字段按codeitemid排序
		        }
			   

			    this.frowset = dao.search(codeBuf.toString());
			    while (this.frowset.next()){
			        if (strFormula == "") {
			            strFormula = this.frowset.getString("codeitemdesc");
			        } else {
			            strFormula = strFormula + "," + this.frowset.getString("codeitemdesc");
			        }
			    }

			    String[] strFormulas = strFormula.split(",");
			    CellRangeAddressList addressList = new CellRangeAddressList(1, j, colNumber, colNumber); //rowCount
			    DVConstraint constraint = DVConstraint.createExplicitListConstraint(strFormulas);
			    HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
			    dataValidation.setSuppressDropDownArrow(false);
			    sheet.addValidationData(dataValidation);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sheet = null;
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return workbook;
	}

	/**
	 * 要查询的columns
	 * 
	 * @return
	 */
	public String getColumns() {
		// 拿到已构库的指标项
		ArrayList<FieldItem> fieldList = this.getAchievementDataFieldList();
		
		StringBuffer columns = new StringBuffer();
		for (FieldItem fieldItem : fieldList) {
			if("a0177".equals(fieldItem.getItemid()))
				continue;
			columns.append(fieldItem.getItemid() + ",");
		}
		
		return columns.toString().substring(0, columns.lastIndexOf(","));
	}

	/**
	 * 生成成绩FieldItem集 拿到所有成绩要显示的指标项
	 * 
	 * @return
	 */
	public ArrayList<FieldItem> getAchievementDataFieldList() {
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Z63", 1);
		ArrayList<FieldItem> fieldList2 = new ArrayList<FieldItem>();
		for (FieldItem fieldItem : fieldList) {
			// 屏蔽掉不显示的指标
			if (fieldItem.isVisible()) {
				fieldList2.add(fieldItem);
			}
			if("a0101".equals(fieldItem.getItemid())){
				fieldList2.add(DataDictionary.getFieldItem("A0177"));
			}
		}
		return fieldList2;
	}


	/**
	 * 根据批次导出成绩
	 * @param batch_id
	 * @return
	 */
	public String getSQLWhere(String batch_id) {
	
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("z where 1=1 and Z6301 is not null ");
		if (batch_id != null&&!"all".equalsIgnoreCase(batch_id)&&!"".equals(batch_id)){
			sqlStr.append("AND EXISTS(SELECT NBASE,A0100 ");
			sqlStr.append("FROM zp_exam_assign exam ");
			sqlStr.append("where exam.z0301 in(select z0301 from z03 where z0101 = ?)");
			sqlStr.append("and exam.NBASE=z.NBASE and exam.A0100 = z.A0100 and exam.z0301=z.Z0301) ");
		}
		
		return sqlStr.toString();
	}
	
	
	
	/**
	 * 导入成绩
	 * 拿到成绩Excel并解析
	 * @param path 文件所在路径
	 * @param filename 文件名
	 * @throws GeneralException
	 */
	public String getImportScore(String path,String filename) throws GeneralException{
		
		Sheet sheet = this.getSheet(path,filename);
		return importScore(sheet);
	}

	/**
	 * map中存放的是需要修改的字段的位置和字段名
	 * @param sheet
	 * @param nameIndex 姓名所在位置
	 * @param examineeIndex 准考证号所在位置
	 * @param map 要导入的位置和字段
	 * @return
	 */
	private ArrayList<Object> getExcelInfo(Sheet sheet,int nameIndex,int examineeIndex, Map<Integer, String> map) 
	{
		//返回信息
		ArrayList<Object> reInfo = new ArrayList<Object>();
		ArrayList<Integer> indexs = new ArrayList<Integer>();
		Row row = null;
		Cell cell = null;
		Cell examinCell = null;
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("update z63 set ");
		// 存放Excel表中数据
		ArrayList<Object> rowList = null;
		// 记录数据库中没有的准考证号
		Map<Integer,String> list = new HashMap<Integer, String>();
		// 记录Excel中没有准考证号的信息
		Map<Integer,String> noInfos = new HashMap<Integer, String>();
		//存放错误信息
		ArrayList<Object> msg = new ArrayList<Object>();
		for(Entry<Integer, String> entry:map.entrySet()){
			indexs.add(entry.getKey());
			sql.append(entry.getValue() + "=? , ");
		}
		sql.setLength(sql.length() - 2);
		sql.append(" where z6301=?");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		//判断是不是第一次记录转考证号
		Boolean is = false;
		ArrayList<String> examList = new ArrayList<String>();
		try {
		    //用于计数
		    int count = 0;
		    String examins = "";
		    ArrayList<String> examinList = new ArrayList<String>();
		    HashMap<String, ArrayList<Object>> valueMap = new HashMap<String, ArrayList<Object>>();
			for(int i = 1; i<sheet.getPhysicalNumberOfRows();i++)
			{
				String examin = null;
				String username = null;
				row = sheet.getRow(i);
				rowList = new ArrayList<Object>();
				//把每一行中要修改的数据添加到list中
				if(row==null)
					continue;
				for(Integer c:indexs){
					Object value = null;
					cell = row.getCell(c);
					String field = map.get(c);
	                FieldItem item = (FieldItem) DataDictionary.getFieldItem(field);
					if(cell!=null)
					{
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
							cell.setCellType(Cell.CELL_TYPE_STRING);
						
						
						 String cellValue = cell.getStringCellValue();
						 String Codesql = "";
						 if ((item.getCodesetid() != null && !"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) && "A".equalsIgnoreCase(item.getItemtype())) {
                            if (!"UN".equalsIgnoreCase(item.getCodesetid()) && !"UM".equalsIgnoreCase(item.getCodesetid()) && !"@K".equalsIgnoreCase(item.getCodesetid())) {
                                Codesql = "select codeitemdesc,codeitemid from codeitem where upper(codesetid)='" + item.getCodesetid().toUpperCase() + "'"; // and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date";
                            }
                            this.frowset = dao.search(Codesql);
                            HashMap codemap = new HashMap();
                            HashMap valuemaps = new HashMap();
                            while (this.frowset.next()) {
                                codemap.put(this.frowset.getString("codeitemdesc").trim(), this.frowset.getString("codeitemid").trim());
                                valuemaps.put(this.frowset.getString("codeitemid").trim(), this.frowset.getString("codeitemdesc").trim());
                            }

                            String codeid = "";
                            cellValue = cellValue == null ? "": cellValue.trim();
                                if (codemap.containsKey(cellValue)) { 
                                    value = codemap.get(cellValue).toString(); 
                                    rowList.add(value);
                                    continue;
                                } else if (StringUtils.isEmpty(cellValue)) { 
                                    value = cellValue;
                                }else {
                                	msg.add("第"+(i+1)+"行"+row.getCell(nameIndex)+"的"+DataDictionary.getFieldItem(map.get(c)).getItemdesc()+"单元格中的数据不规范，请从下拉列表中选择");
									return msg; 
                                }
                        } 
						

						//判断成绩的数据类型
						if("N".equalsIgnoreCase(DataDictionary.getFieldItem(map.get(c)).getItemtype()))
						{
							if(cell.getStringCellValue()!=null)
							{
							value = cell.getStringCellValue().trim();
							Pattern pattern = Pattern.compile("[0-9]*"); 
							Pattern pattern2 = Pattern.compile("[0-9]*\\.[0-9]*");
							
								if(!"".equals(value))
								{
									Matcher isNum = pattern.matcher((String)value);
									Matcher isNum2 = pattern2.matcher((String)value);
									
									if(isNum.matches()||isNum2.matches())
									{
										if(((String) value).indexOf(".")>0)
										{
											String intNum=((String) value).substring(0,((String) value).indexOf("."));
											String floatNum=((String) value).substring(((String) value).indexOf(".")+1);
											if(floatNum.length()>DataDictionary.getFieldItem(map.get(c)).getItemlength())
												floatNum=floatNum.substring(0,DataDictionary.getFieldItem(map.get(c)).getDecimalwidth());
											value=intNum+"."+floatNum;
											value = Float.parseFloat((String) value);
										}
									} else
									{
										msg.add("第"+(i+1)+"行"+row.getCell(nameIndex)+"的"+DataDictionary.getFieldItem(map.get(c)).getItemdesc()+"数据格式错误");
										return msg; 
									}
									
								}else
									value=null;
							}
						}else
							value=cell.getStringCellValue()==null?null:cell.getStringCellValue().trim();
					}else
						value=null;
					rowList.add(value);
				}
				
				examinCell = row.getCell(examineeIndex);
				if(examinCell!=null){
					examinCell.setCellType(Cell.CELL_TYPE_STRING);
					examin = row.getCell(examineeIndex).getStringCellValue();
					if(!is){
						is=true;
						examList.add(examin);
					}else{
						for(int j=0;j<examList.size();j++){
							if( !"".equalsIgnoreCase(examin) && examList.get(j).equalsIgnoreCase(examin)){
								msg.add("第"+(i+1)+"行有重复准考证号"+"："+examin);
								return msg;
							}
						}
						examList.add(examin);
					}
				}
				//记录导入信息
				if(examin==null||"".equals(examin))
				{
					if(row.getCell(nameIndex)==null||row.getCell(nameIndex).getStringCellValue()==null)
						username = "";
					else
						username = row.getCell(nameIndex).getStringCellValue();
					noInfos.put(i+1, username);
				}else 
				{
    				if(count == 0)
    				    examins ="'" + examin + "'";
    				else 
    				    examins += ",'" + examin + "'";
    				    
    				count++;
    				
    				if(count >= 1000) {
				        examinList.add(examins);
				        examins = "";
				        count = 0;
				    }
				    
    				Cell nameCell = row.getCell(nameIndex);
    				if(nameCell != null){
    					nameCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    				}
    				rowList.add(examin);
				    ArrayList<Object> valueList = new ArrayList<Object>();
				    valueList.add(rowList);
                    valueList.add((i + 1) + "=" + (nameCell == null ? "" 
                            : nameCell.getStringCellValue()));
                    valueMap.put(examin, valueList);
				}
			}
			
			if(StringUtils.isNotEmpty(examins))
			    examinList.add(examins);
			
			ArrayList valueList = new ArrayList<ArrayList<Object>>();
			for(int i = 0; i < examinList.size(); i++){
			    String values = examinList.get(i);
			    rs = dao.search("select z6301 from z63 where z6301 in (" + values + ")");
			    while (rs.next()) {
			        String z6301 = rs.getString("z6301");
			        ArrayList<Object> mapValue = valueMap.get(z6301);
			        valueList.add(mapValue.get(0));
			        valueMap.remove(z6301);
			        num++;
			    }
			}
			
			dao.batchUpdate(sql.toString(), valueList);
			if(valueMap != null && valueMap.size() > 0) {
			    Iterator iter = valueMap.entrySet().iterator();
    			while (iter.hasNext()) {
    			    Map.Entry entry = (Map.Entry) iter.next();
        			ArrayList<Object> mapValue = (ArrayList<Object>) entry.getValue();
        			String error = (String) mapValue.get(1);
        			if(error.indexOf("=") > 0) {
        			    String[] errors = error.split("=");
        			    list.put(Integer.parseInt(errors[0]), errors[1]);
        			}
    			}
			}
			
			reInfo.add(num);
			reInfo.add(noInfos);
			reInfo.add(list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return reInfo;
	}

	/**
	 * 获取导入的sheet
	 * @return 拿到要导入的Excel
	 * @throws GeneralException
	 */
	private Sheet getSheet(String path, String filename) throws GeneralException 
	{
		File file = new File(path+File.separator+filename);
		InputStream input = null;
		Workbook work = null;
		Sheet sheet = null;
		try {
			// 判断是否为文件
			if (!FileTypeUtil.isFileTypeEqual(file)) 
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			
			input = new FileInputStream(file);
			work = WorkbookFactory.create(input);
			sheet = work.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(input);
			PubFunc.closeResource(work);
		}
		return sheet;
	}

	public String getImportScore(String fileId) {
		Sheet sheet = this.getSheet(fileId);
		return importScore(sheet);
	}

	/**
	 * 解析工作表导入成绩
	 * @param sheet 工作表
	 * @return
	 */
	private String importScore(Sheet sheet) {
		// 存放准考证号的位置
		int examineeIndex = 0;
		// 存放姓名所在的位置
		int nameIndex = 0;
		// 存放支持修改的字段
		Map<Integer, String> map = new HashMap<Integer, String>();
		// 每次导入前先把消息清空
		Row headRow = sheet.getRow(0);// 获取表头
		int insertFieldCount = 0;// 将要更新的字段数目
		String sql = "select fieldsetid,itemid,itemtype,itemdesc,codesetid from fielditem where  useflag='1' and itemid<>'A0101' and fieldsetid like 'A%'";
		RowSet search = null;
		HashMap<String, String> hashMap = new HashMap<String, String>();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			search = dao.search(sql);
			int index = 0;
			while(search.next()){
				
				hashMap.put(search.getString("itemid"), search.getString("fieldsetid"));
			}
			if(headRow==null){
				return JSON.toString("请用导出的Excel模板来导入数据！");
			}
			int headCols = headRow.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			
			if (headRow == null) 
				return JSON.toString("请用导出的Excel模板来导入数据");
			
			if(headCols>=1&&rows>=1)
			{
				// 用来判断是不是存在准考证号
				boolean isExistExaminee = false;
				DbWizard dWizard = new DbWizard(conn);
				Cell cell = null;
				Comment comment = null;
				// 拿到要添加的指标
				for (int c = 0; c < headCols; c++) {
					
					cell = headRow.getCell(c);
					String field = "";
					String title = "";
					
					if (cell != null) {
						switch (cell.getCellType()) {
						
						case Cell.CELL_TYPE_FORMULA:							
							break;
							
						case Cell.CELL_TYPE_NUMERIC:
							double y = cell.getNumericCellValue();
							title = Double.toString(y);
							break;
							
						case Cell.CELL_TYPE_STRING:
							title = cell.getStringCellValue();
							break;
						}
						
						comment = cell.getCellComment();
						// 表头存在，批注为空
						if (comment == null) 
							return JSON.toString("请用导出的Excel模板来导入数据！");
	
						//拿到标注
						field = comment.getString().toString().trim();
						if(hashMap.get(field.toUpperCase())!=null)
							continue;
						if (!"a0177".equals(field)&&!"a0101".equalsIgnoreCase(field)&&!"z0321".equalsIgnoreCase(field)&&!"z0325".equalsIgnoreCase(field)) 
						{
							//记录要要改的字段的位置
							if (!dWizard.isExistField("z63", field.toUpperCase(), false)) 
								return JSON.toString("“" + title + "”的指标编码未构库或不存在，请用导出的Excel模板来导入数据！");
							//找到准考证号所在的字段的位置
							if ("z6301".equalsIgnoreCase(field)) 
							{
								examineeIndex = c;
								isExistExaminee = true;
							}else
							map.put(c, field);
						}else if("a0101".equalsIgnoreCase(field))
						{
							nameIndex = c;
						}
					}
					insertFieldCount++;
				}
				if (!isExistExaminee) 
					return JSON.toString("准考证号不存在，请用导出的Excel模板来导入数据！");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		ArrayList<Object> info = this.getExcelInfo(sheet,nameIndex,examineeIndex, map);
		if(info.size()==1)
			return JSON.toString(info.get(0));
		
		return JSON.toString(info);
	}

	/**
	 * 获取导入的sheet
	 * @return 拿到要导入的Excel
	 * @throws GeneralException
	 */
	private Sheet getSheet(String fileId) {
		InputStream input = null;
		Workbook work = null;
		Sheet sheet = null;
		try {
			input = VfsService.getFile(fileId);
            work = WorkbookFactory.create(input);
			sheet = work.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(input);
			PubFunc.closeResource(work);
		}
		return sheet;
	}

	/**
	 * 根据字段长度限制excel内容长度
	 * @param firstRow 起始行数
	 * @param lastRow 终止行数
	 * @param firstCol 起始列数
	 * @param lastCol 终止列数
	 * @param maxLength 最大长度
	 * @return
	 */
	private HSSFDataValidation setValidate(int firstRow, int lastRow, int firstCol, int lastCol, int maxLength) {
		// 创建一个规则
		DVConstraint constraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.TEXT_LENGTH,
				DVConstraint.OperatorType.BETWEEN, "0", maxLength+"");
		// 设定在哪个单元格生效
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
		// 创建规则对象
		HSSFDataValidation validation = new HSSFDataValidation(regions, constraint);
		return validation;
	}

}
