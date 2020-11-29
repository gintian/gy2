package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *<p>Title:ImportKqAppTrans.java</p> 
 *<p>Description:导入申请记录</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2013-7-11下午03:14:13</p> 
 *@author wangmj
 *@version 1.0
 */
public class ImportKqAppTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap<String,String> msgmap = new LinkedHashMap();
		ArrayList msglist=new ArrayList();
		String importMsg = "";
		int updateCount = 0;
		String outName = "";
		ArrayList infoList = null;
		Date nowDate = new Date();
		FormFile file = (FormFile) this.getFormHM().get("file");
		//文件类型验证xiexd 2014.09.11
		try {
			if(!FileTypeUtil.isFileTypeEqual(file))
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String table = (String) this.getFormHM().get("table");
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into " + table + " ( ");
		
		int updateFidsCount = 0;// 将要更新的字段数目
		int gnoIndex = 0;
		DbWizard dWizard = new DbWizard(frameconn);
		KqParameter kqParameter = new KqParameter(userView, "", this.getFrameconn());
		HashMap paraMap = kqParameter.getKqParamterMap();
		String g_no= (String) paraMap.get("g_no");
		if (g_no == null || g_no.length() <= 0) 
		{
			importMsg = "请在参数设置里设置工号对应指标！";
			return;
		}
		
		//需要用到的对象  变量 从循环里提出来
		GetValiateEndDate ve = new GetValiateEndDate(this.userView, this.frameconn);
		ValidateAppOper validateAppOper = new ValidateAppOper(this.userView, this.frameconn);
		boolean is_overtime_op = validateAppOper.is_OVERTIME_TYPE();
		String rest_overtime_time = KqParam.getInstance().getRestOvertimeTimes();
		//获取当前考勤期间的开始时间
		ArrayList kqstartlist = KqUtilsClass.getcurrKq_duration();
		String kqstart = (String) kqstartlist.get(0);
		Date kqstartDate = OperateDate.strToDate(kqstart, "yyyy.MM.dd");
		
		HashMap empA01 = new HashMap();
		Workbook wb = null;
		Sheet sheet = null;
		InputStream in = null;
		try {
		    in = file.getInputStream();
			wb = WorkbookFactory.create(in);
			sheet = wb.getSheetAt(0);

			//每次导入前先把消息清空
			this.getFormHM().put("importMsg", "");
			// 定义一个list用来存放哪些 指标可以修改
			//HashMap map = new HashMap();
			ArrayList map = new ArrayList();
			Row row = sheet.getRow(0);//第二行
			if (row == null){
				importMsg = "请用导出的Excel模板来导入数据！";
				return;
			}

			int cols = row.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			
			StringBuffer codeBuf = new StringBuffer();
			
			ContentDAO dao = new ContentDAO(this.frameconn);

			int z1TimeCol = -1;
			int z3TimeCol = -1;
			/*linbz 32331 校验申请日期字段是否存在*/
			boolean table05 = false;
			
			if (cols < 1 || rows < 1) {
				
			} else {
				for (short c = 0; c < cols; c++) {
					Cell cell = row.getCell(c);
					if (cell != null) {
						String title = "";
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
						default:
							title = "";
						}

						if ("".equals(title.trim())){
							importMsg = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                            return;
						}
						
						Comment myComment=cell.getCellComment();
						//如果没有了批注
						if(myComment==null){
						    importMsg = "标题行存在非模板导出指标（批注为空）！请用导出的Excel模板来导入数据！";
                            return;
						}
						

						String field = cell.getCellComment().getString().toString().toLowerCase().trim();
						if((table+"z1_date").equalsIgnoreCase(field))
							field = table+"z1";
						if((table+"z1_time").equalsIgnoreCase(field)){
						    z1TimeCol = c;
						    continue;
						}
						if((table+"z3_date").equalsIgnoreCase(field))
						    field = table+"z3";
						if((table+"z3_time").equalsIgnoreCase(field)) {
						    z3TimeCol = c;
						    continue;
						}
						if (g_no.equalsIgnoreCase(field)) 
							gnoIndex = c;
						if (g_no.equalsIgnoreCase(field)) 
							continue;
						if(!dWizard.isExistField(table, field.toUpperCase(), false))
						{
							importMsg = "字段“" + field + "”未构库或不存在！";
							return;
						}
						String codesetid = DataDictionary.getFieldItem(field).getCodesetid();
						if (codesetid != null && !"0".equals(codesetid)) {
							if (!"UM".equals(codesetid)
									&& !"UN".equals(codesetid)
									&& !"@K".equals(codesetid)) {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"
										+ codesetid
										+ "'  and codeitemid=childid  union all ");
							} else {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='"
										+ codesetid
										+ "' and  codeitemid not in (select parentid from organization where codesetid='"
										+ codesetid + "') union all ");
							}
						}
						//增加过滤A01指标，A01指标不能更改;更改
						if (("b0110,e01a1,e0122,a0100,a0101,nbase," 
								+ table.toLowerCase() + "01,").indexOf(field.toLowerCase()) == -1)// 单位 部门 姓名字段不更新
//								+ table.toLowerCase() + "05,"+ table.toLowerCase() + "01,").indexOf(field.toLowerCase()) == -1)// 单位 部门 姓名字段不更新
						{
							String itemtype = DataDictionary.getFieldItem(field).getItemtype();
							boolean booindex = getindexA01(itemtype, field);
							if (booindex) {
								CommonData commonData = new CommonData(String.valueOf(c) , field + ":" + cell.getStringCellValue());
								//map.put(new Short(c), field + ":" + cell.getStringCellValue());
								map.add(commonData);
								sql.append(field + ", ");
								updateFidsCount++;
							}
						}
						if(field.equalsIgnoreCase(table+"05"))
							table05 = true;
					}else 
					{
						importMsg = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                        return;
					}
				}
				sql.append(table + "01" + ", ");
				sql.append(table + "z5" + ", ");
				sql.append("a0100, a0101, nbase, b0110, e0122, e01a1, ");
				updateFidsCount = updateFidsCount + 8;
				//若没选申请日期字段，则添加
				if(!table05) {
					sql.append(table.toLowerCase() + "05, ");
					updateFidsCount++;
				}
				
				if ("q15".equalsIgnoreCase(table)) 
				{
					sql.append("q1517, ");
					updateFidsCount++;
				}

				sql.setLength(sql.length() - 2);
				sql.append(") values (");
				for (int i = 0; i < updateFidsCount; i++) 
				{
					sql.append("?, ");
				}
				sql.setLength(sql.length()-2);
				sql.append(")");
				
			}
			
			String a_code = "";
			String code = "";
			String kind = "";
	        //String a_code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			a_code = RegisterInitInfoData.getKqPrivCode(this.userView)
							+ RegisterInitInfoData.getKqPrivCodeValue(this.userView);
			if(a_code==null||a_code.length()<=0)
	        {
	            String privcode=RegisterInitInfoData.getKqPrivCode(userView);
	            if("UN".equalsIgnoreCase(privcode))
	                kind="2";
	            else if("UM".equalsIgnoreCase(privcode))
	                kind="1";
	            else if("@K".equalsIgnoreCase(privcode))
	                kind="0";
	            code=RegisterInitInfoData.getKqPrivCodeValue(userView);
	        }else
	        {
	            if(a_code.indexOf("UN")!=-1)
	            {
	                kind="2";
	            }else if(a_code.indexOf("UM")!=-1)
	            {
	                kind="1";
	            }else if(a_code.indexOf("@K")!=-1)
	            {
	                kind="0";
	            }
	            code=a_code.substring(2);
	        }
			
			// 开始取数据
			infoList = new ArrayList();//存放记录集合
			 updateCount = 0;
			for (int j = 1; j < rows; j++) {
				row = sheet.getRow(j);
				if (row == null)
					continue;
				boolean isnull = true;
				for (int i = 0; i < cols; i++) 
				{
					Cell oneCell = row.getCell(i);
					if (oneCell != null && oneCell.getCellType() != Cell.CELL_TYPE_BLANK){
						isnull = false;
						break;
					}
				}
				if(isnull)
					continue;
				Cell gnoCell = row.getCell(gnoIndex);
				if (gnoCell == null) 
				{
					msgmap.put("第" + (j+1) + "行", "工号为空");
					continue ;
				}else 
				{
					//-20160420--linbz--工号过滤空格
					gnoCell.setCellValue(gnoCell.toString().trim());
					if (gnoCell.getCellType() == Cell.CELL_TYPE_NUMERIC) 
						gnoCell.setCellType(Cell.CELL_TYPE_STRING);
					empA01 = this.getEmpA01(gnoCell.getStringCellValue(), g_no, dao, a_code, code, kind);
					if(empA01 == null){
						msgmap.put("第" + (j+1) + "行", "工号不正确或无该人员权限，无法导入该申请!");
						continue;
					}
				}
				
				
				ArrayList importValue = new ArrayList();//一条记录的集合

				String startDate = "";//申请开始时间
				String endDate = "";//申请结束时间
				String app_type = "";//申请类型
				int fromIndex = -1;//申请开始时间在集合的位置
				int toIndex = -1;//申请结束时间在集合的位置
				int typeIndex = -1;//申请类型在集合的位置
				Boolean flag = true;//是否继续执行下一行循环
				//Iterator it = map.entrySet().iterator(); 
				for (int i = 0; i < map.size(); i++) 
				{
					CommonData commonData = (CommonData) map.get(i);
					String columnIndex = commonData.getDataValue();//(Short)entry.getKey(); 
					String mapValue = commonData.getDataName();//(String)entry.getValue(); 
					int c = Integer.parseInt(columnIndex);
					
					//把map里面的value分割成批注和描述
					String[] titleKey=mapValue.split(":");
					String fielditem=titleKey[0];
					FieldItem item = DataDictionary.getFieldItem(fielditem, table);
					if (item == null) {
                        item = DataDictionary.getFieldItem(fielditem);
                    }
					if (item == null) {
                        continue;
                    }
					
					String dataType = item.getItemtype();
					String fieldCodeSetId = item.getCodesetid();
					int decwidth = item.getDecimalwidth();
					int itemlength = item.getItemlength();
					
					Cell thecell = row.getCell(c);
					String thecellTimeStringCellValue = "";
					if(((table+"z1").equalsIgnoreCase(fielditem) && z1TimeCol>-1)||((table+"z3").equalsIgnoreCase(fielditem) && z3TimeCol>-1)){
							String mTime = "00";
							String hTime = "00";
							if((table+"z3").equalsIgnoreCase(fielditem)){
								mTime = "59";
								hTime = "23";
							}
							int timeCol = -1;
							Cell timeCell = null;
							if ((table+"z1").equalsIgnoreCase(fielditem)) 
							    timeCol = z1TimeCol;
							else {
							    timeCol = z3TimeCol;
                            }
							timeCell = row.getCell(timeCol);
							String value = "";
							switch (timeCell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								value = timeCell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								 Date d = timeCell.getDateCellValue();
								 String strDate = OperateDate.dateToStr(d, "HH:mm");
								 String year = OperateDate.dateToStr(d, "yyyy");
								 SimpleDateFormat format = new SimpleDateFormat("HH:mm");
								 double y = 0;
								 try{
									 format.parse(strDate);
								 }catch(Exception e){
									 y = timeCell.getNumericCellValue();
									 value = (int)y+"";
								 }
								 value = strDate;
								 if("1900".equalsIgnoreCase(year))
									 value = "false";
								break;
							case Cell.CELL_TYPE_STRING:
								value = timeCell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_BLANK:
								value = "";
								break;
							default:
								value = "false";
							}
							if(isGetKqTime(value)){
								String timeStr = value;
								if (timeStr.indexOf("：")>-1){
									timeStr = timeStr.replace("：", ":");
								}
								if (timeStr.indexOf(":")>-1){
									String[] hourAndMinte = timeStr.split(":");
									if(hourAndMinte.length == 1){
										if(hourAndMinte[0].length()==1)
											thecellTimeStringCellValue ="0"+ hourAndMinte[0]+":00";
										else
											thecellTimeStringCellValue = hourAndMinte[0]+":00";
									} 
									if(hourAndMinte.length == 2||hourAndMinte.length == 3){
										if(hourAndMinte[0].length()==1)
											thecellTimeStringCellValue ="0"+ hourAndMinte[0];
										else
											thecellTimeStringCellValue = hourAndMinte[0];
										if(hourAndMinte[1].length()==1)
											thecellTimeStringCellValue = thecellTimeStringCellValue+":0"+hourAndMinte[1];
										else
											thecellTimeStringCellValue = thecellTimeStringCellValue+":"+hourAndMinte[1];
									}
									else{
										thecellTimeStringCellValue = hTime+":"+mTime;
									}
								} else {
									if (timeStr.length() == 1)
										thecellTimeStringCellValue = "0"+timeStr+":00";
									else if (timeStr.length() == 2)
										thecellTimeStringCellValue = timeStr+":00";
									else
										thecellTimeStringCellValue = hTime+":"+mTime;
								}
								thecellTimeStringCellValue = " "+thecellTimeStringCellValue;
							}else{
								msgmap.put("第" + (j+1) + "行，第"+(timeCol+1)+"列", "时间格式错误");
								flag = false;
								continue;
							}
					}
					if (thecell == null) 
					{
						importValue.add(null);
						continue;
					}
					switch(thecell.getCellType()){
					//如果是空白单元格
					case Cell.CELL_TYPE_BLANK:
						if("N".equalsIgnoreCase(dataType)){
							//如果数值型的字段没填，则默认填0.0
							importValue.add(null);
    						break;
    					}else if("D".equalsIgnoreCase(dataType)||"A".equalsIgnoreCase(dataType)){
    						//若table+05申请日期为空则添加当前日期作为申请日期
    						if(fielditem.equalsIgnoreCase(table+"05")){
    							java.sql.Timestamp stp = new java.sql.Timestamp(nowDate.getTime());
    							importValue.add(stp);
    						}else {
    							importValue.add(null);
    						}
    						break;
    					}
						
					//如果是数值型单元格
					case Cell.CELL_TYPE_NUMERIC:
						if ("D".equalsIgnoreCase(dataType)) 
						{
							Date thecellValue1= thecell.getDateCellValue();
							if(HSSFDateUtil.isCellDateFormatted(thecell))
							{
								String dateRull = "yyyy-MM-dd HH:mm";
								if(cols>11){
									dateRull = "yyyy-MM-dd";
								}
								String strDate = OperateDate.dateToStr(thecellValue1, dateRull);
								//去判断日期格式是否正确
								if(isDataType(decwidth,dataType,strDate,itemlength)){
									strDate=strDate.replaceAll("/", "-");
									strDate=strDate.replace(".", "-");
									Date date = null;
									String value = "";
									String mTime = "00";
									String hTime = "00";
									String year = "";
									String month  = "";
									String day = "";
									if((table+"z3").equalsIgnoreCase(fielditem)){
										mTime = "59";
										hTime = "23";
									}
									if(strDate.indexOf("-")>-1){
										String[] dateStr = strDate.split("-");
										if (dateStr.length==1){
											value = dateStr[0]+"-01-01"+thecellTimeStringCellValue;
										} 
										else if (dateStr.length==2){
											Calendar cal = Calendar.getInstance();
										    int dateYear = cal.get(Calendar.YEAR);//获取年份
											if (dateStr[0].length()==1||dateStr[0].length()==2){
												year = dateYear+"";
												month = dateStr[0];
												if(dateStr[0].length()==1){
													month = "0"+month;
												}
												day = dateStr[1];
											}else if(dateStr[0].length()==4){
												year = dateStr[0];
												month = dateStr[1];
												if(dateStr[1].length()==1){
													month = "0"+month;
												}
												day = "01";
											}
											value = getDateStr(year,month,dateStr[1],hTime,mTime,thecellTimeStringCellValue);
											if(StringUtils.isEmpty(value)){
												msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
												flag = false;
				                                continue;
											}
										} 
										else if (dateStr.length==3){
											year = dateStr[0];
											month = dateStr[1];
											day = dateStr[2];
											if(dateStr[1].length() == 1){
												month = "0"+dateStr[1];
											}
											value = getDateStr(year,month,day,hTime,mTime,thecellTimeStringCellValue);
											if(StringUtils.isEmpty(value)){
												msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
												flag = false;
				                                continue;
											}
										}
									}
									else if(strDate.length()==4){
										value = strDate+"-01-01"+thecellTimeStringCellValue;
									}
									else if(strDate.length()==8){
										value =strDate.substring(0,4)+"-"+strDate.substring(4,6)+"-"+strDate.substring(6,8);
									}
									Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
							    	Matcher matcher = pattern.matcher(value.trim());
									if(matcher.matches()){
										value = value+" "+hTime+":"+mTime;
									}
									Pattern pt = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}");
							    	Matcher mc = pt.matcher(value);
									if(mc.matches()){
										date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value); 
									}else{
										msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
										flag = false;
		                                continue;
									}
									java.sql.Timestamp stp = new java.sql.Timestamp(date.getTime());
									importValue.add(stp);
									if ((table + "z1").equalsIgnoreCase(fielditem)) {
										startDate = value;
										fromIndex = importValue.size();
									}
									if ((table + "z3").equalsIgnoreCase(fielditem)) {
										endDate = value;
										toIndex = importValue.size();
									}
									break;
								}else{
									msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
									flag = false;
	                                continue;
								}
							}else 
							{
								msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
								flag = false;
                                continue;
							}
						}else if("N".equalsIgnoreCase(dataType)){//如果是数值型的字段
							DecimalFormat df = new DecimalFormat("0.00000000");  
							String cellValue = df.format(thecell.getNumericCellValue()); 
							double dValue = Double.parseDouble(cellValue);
							dValue = new BigDecimal(dValue).setScale(decwidth, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							//去匹配是否符合要求
							if(isDataType(decwidth,dataType,dValue+"",itemlength)){
								String value = Double.toString(dValue);
								value = PubFunc.round(value, decwidth);
								importValue.add(new Double((PubFunc.round(value, decwidth))));
								break;
							}else{
								msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "数值格式错误（整数位不超过"+itemlength+"位，小数位不超过"+decwidth+"位）");
								flag = false;
                                continue;
							}
						}else if ("A".equalsIgnoreCase(dataType)) 
						{
							if ((table + "07").equalsIgnoreCase(fielditem)) 
							{
								thecell.setCellType(Cell.CELL_TYPE_STRING);
								String value = thecell.getStringCellValue();
								importValue.add(value);
								break;
							}
							double value = thecell.getNumericCellValue();
							importValue.add(String.valueOf(value));
							break;
						}else
							break;
						
					case Cell.CELL_TYPE_FORMULA:
					case Cell.CELL_TYPE_STRING:
						String thecellValue2 = "";
						
						//判断是字符型字段
						if("A".equalsIgnoreCase(dataType)){
	                        thecellValue2 = thecell.getStringCellValue();
							//如果是代码型的字段 codesetid不为0，不为null
							if(!("0".equalsIgnoreCase(fieldCodeSetId))&&fieldCodeSetId!=null&&!("".equalsIgnoreCase(fieldCodeSetId))){
								if(thecellValue2 == null || thecellValue2.length() <= 0){
									importValue.add(null);
									break;
								}
									
								//则根据代码的描述去拿对应的编码
								String codeitem = getCodeByDesc(thecellValue2,fieldCodeSetId);
								//判断是否有该代码
								if(codeitem==null){
									msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "该代码名称在数据字典中找不到对应代码");
									flag = false;
		                            continue;
								}
								
								importValue.add(codeitem);
								if("q1103".equalsIgnoreCase(fielditem) || "q1303".equalsIgnoreCase(fielditem) 
										|| "q1503".equalsIgnoreCase(fielditem)){
									app_type = codeitem;
									typeIndex = importValue.size();//申请类型的index
								}
								break;
								
							}else{
								//如果是普通字符型字段就直接添加
								importValue.add(thecellValue2);
								break;
							}
						}
					
						//如果是日期
						if("D".equalsIgnoreCase(dataType)){
						    thecellValue2 = thecell.getStringCellValue();
						    
							thecellValue2=thecellValue2.replaceAll(" ", "");
							thecellValue2=thecellValue2.replaceAll("/", "-");
							thecellValue2=thecellValue2.replace(".", "-");
							thecellValue2=thecellValue2.replace("\\", "-");
							String valuedate = "";
							for(int v=0;v<thecellValue2.length();v++){
								char a = thecellValue2.charAt(v);
								String s = String.valueOf(a);
								if(s.matches("[0-9]|-|:")){
									valuedate += s;
								}
							}
							thecellValue2 = valuedate;
							//去判断日期格式是否正确
							if(isDataType(decwidth,dataType,thecellValue2,itemlength)){
								thecellValue2=thecellValue2.replaceAll("/", "-");
								thecellValue2=thecellValue2.replace(".", "-");
								Date date = null;
								String value = "";
								String mTime = "00";
								String hTime = "00";
								String year = "";
								String month  = "";
								String day = "";
								if((table+"z3").equalsIgnoreCase(fielditem)){
									mTime = "59";
									hTime = "23";
								}
								if(thecellValue2.indexOf("-")>-1){
									String[] dateStr = thecellValue2.split("-");
									if (dateStr.length==1){
										value = dateStr[0]+"-01-01"+thecellTimeStringCellValue;
									} 
									else if (dateStr.length==2){
										Calendar cal = Calendar.getInstance();
									    int dateYear = cal.get(Calendar.YEAR);//获取年份
										if (dateStr[0].length()==1||dateStr[0].length()==2){
											year = dateYear+"";
											month = dateStr[0];
											if(dateStr[0].length()==1){
												month = "0"+month;
											}
											day = dateStr[1];
										}else if(dateStr[0].length()==4){
											year = dateStr[0];
											month = dateStr[1];
											if(dateStr[1].length()==1){
												month = "0"+month;
											}
											day = "01";
										}
										value = getDateStr(year,month,dateStr[1],hTime,mTime,thecellTimeStringCellValue);
										if(StringUtils.isEmpty(value)){
											msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
											flag = false;
			                                continue;
										}
									} 
									else if (dateStr.length==3){
										year = dateStr[0];
										month = dateStr[1];
										day = dateStr[2];
										if(dateStr[1].length() == 1){
											month = "0"+dateStr[1];
										}
										if(day.length()>2){
											day=day.substring(0, 2)+" "+day.substring(2);
										}
										value = getDateStr(year,month,day,hTime,mTime,thecellTimeStringCellValue);
										if(StringUtils.isEmpty(value)){
											msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
											flag = false;
			                                continue;
										}
									}
								}
								else if(thecellValue2.length()==4){
									value = thecellValue2+"-01-01"+thecellTimeStringCellValue;
								}
								else if(thecellValue2.length()==8){
									value =thecellValue2.substring(0,4)+"-"+thecellValue2.substring(4,6)+"-"+thecellValue2.substring(6,8);
									value += thecellTimeStringCellValue;
								}
								Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
						    	Matcher matcher = pattern.matcher(value.trim());
								if(matcher.matches()){
									value = value+" "+hTime+":"+mTime;
								}
								Pattern pt = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}");
						    	Matcher mc = pt.matcher(value);
								if(mc.matches()){
									date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value); 
								}else{
									msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
									flag = false;
	                                continue;
								}
								java.sql.Timestamp stp = new java.sql.Timestamp(date.getTime());
								importValue.add(stp);
								if ((table + "z1").equalsIgnoreCase(fielditem)) {
									startDate = value;
									fromIndex = importValue.size();
								}
								if ((table + "z3").equalsIgnoreCase(fielditem)) {
									endDate = value;
									toIndex = importValue.size();
								}
								break;
							}else{
								msgmap.put("第" + (j+1) + "行，第"+(c+1)+"列", "日期格式错误");
								flag = false;
                                continue;
							}
							
						}
					
						if("N".equalsIgnoreCase(dataType)){
						    try {
						        thecellValue2 = Double.toString(thecell.getNumericCellValue());
						    } catch (Exception e) {
						        thecellValue2 = thecell.getStringCellValue();
						    }
						 
							//如果数值型的字段没填，则默认填0.0
							if(thecellValue2==null||"".equalsIgnoreCase(thecellValue2)){
								importValue.add(null);
								break;
							}
							
							if ("q1104".equalsIgnoreCase(fielditem)) {
								int classid = getClassId(thecellValue2, dao);
								importValue.add(new Integer(classid));
							}
							
							//去匹配是否符合要求
							else if(isDataType(decwidth,dataType,thecellValue2,itemlength)){
								//---linbz--20160421--导入数值型指标时，判断位数是否超出 
								//获取整数位长度
								int itemlengthInt = thecellValue2.split("\\.")[0].length();
								//获取小数位长度
								int itemlengthDec = 0;
								if(thecellValue2.indexOf(".") > 0){
								    itemlengthDec = thecellValue2.split("\\.")[1].length();
								}
								
								if(itemlengthInt > itemlength){
								    msgmap.put("第"+(j+1)+"行，第"+(c+1)+"列", "数值格式错误（整数不超过"+itemlength+"位，小数不超过"+decwidth+"位）");
								    flag = false;
								    continue;
								} else {
								    double dValue = new BigDecimal(new Double(thecellValue2)).setScale(decwidth, BigDecimal.ROUND_HALF_UP).doubleValue();
								    importValue.add(dValue);
								    break;
								}
							}else{
								msgmap.put("第"+(j+1)+"行，第"+(c+1)+"列", "数值格式错误");
								flag = false;
                                continue;
							}
						}
					}
					
				}
				if(!flag) continue;
				IDFactoryBean idFactory = new IDFactoryBean();
				String id = idFactory.getId(table.toUpperCase()+"."+table.toUpperCase()+"01", "", frameconn);
				importValue.add(id);//主键
				importValue.add("02");//审批状态
				
				importValue.add((String)empA01.get("a0100"));//人员编号
				importValue.add((String)empA01.get("a0101"));
				importValue.add((String)empA01.get("nbase"));
				importValue.add((String)empA01.get("b0110"));
				importValue.add((String)empA01.get("e0122"));
				importValue.add((String)empA01.get("e01a1"));
				//若没选申请日期字段，则添加当前日期为申请日期
				if(!table05) {
					java.sql.Timestamp stp = new java.sql.Timestamp(nowDate.getTime());//table+05申请日期
					importValue.add(stp);
				}
				if ("q15".equalsIgnoreCase(table)) 
					importValue.add("0");//请假标识，非销假
					
				String nbase = (String) empA01.get("nbase");
				String a0100 = (String) empA01.get("a0100");
				String b0110 = (String) empA01.get("b0110");
				
				//添加判断申请类型不能为空
				if("".equalsIgnoreCase(app_type)||app_type==null){
					msgmap.put("第"+(j+1)+"行", "申请类型不能为空");
					continue;
				}
				if(!"0".equals(app_type.substring(0, 1))&& "q15".equalsIgnoreCase(table)){
					msgmap.put("第"+(j+1)+"行", "请假申请只能导入请假申请单");
					continue;
				}else if (!"1".equals(app_type.substring(0, 1))&& "q11".equalsIgnoreCase(table)) 
				{
					msgmap.put("第"+(j+1)+"行", "加班申请只能导入加班申请单");
					continue;
				}else if (!"3".equals(app_type.substring(0, 1))&& "q13".equalsIgnoreCase(table)) 
				{
					msgmap.put("第"+(j+1)+"行", "公出申请只能导入公出申请单");
					continue;
				}
				//添加判断申请开始时间和申请结束时间不能为空
				if("".equalsIgnoreCase(startDate)||startDate==null){
					msgmap.put("第"+(j+1)+"行", "申请起始日期不能为空");
                    continue;
				}
				if("".equalsIgnoreCase(endDate)||endDate==null){
					msgmap.put("第"+(j+1)+"行", "申请结束日期不能为空");
                    continue;
				}
				
				
				//添加考勤期间的判断
				Date fromDate = OperateDate.strToDate(startDate, "yyyy-MM-dd HH:mm");
				Date toDate = OperateDate.strToDate(endDate, "yyyy-MM-dd HH:mm");
				if (fromDate.after(toDate) || fromDate.equals(toDate)) {
					msgmap.put("第"+(j+1)+"行", "起始日期不能大于或等于结束日期");
				    continue;
				}
//				AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
//				if(!annualApply.isSessionSearl(fromDate,toDate)){
				//申请起始日期在考勤期间起始日期之后，也可以等于
				if(!kqstartDate.before(fromDate) && !kqstartDate.equals(fromDate)){
					msgmap.put("第"+(j+1)+"行", "该考勤期间已封存或不存在，不能做该申请操作");
					continue;
				}
				
//				if(!annualApply.getKqDataState(nbase,a0100,fromDate,toDate))
//		        {
//					msgmap.put("第"+(j+1)+"行", "申请的业务日期包含的日明细数据已经提交，不能做申请操作，请与考勤管理员联系");
//					continue;
//		        }  
				//添加针对三种申请的不同判断
				boolean isRepeat = ve.checkTimeX(table, nbase, a0100, startDate, endDate);
				if (isRepeat) {
					msgmap.put("第"+(j+1)+"行", "记录重复申请");
					continue;
				}
				
				if ("Q11".equalsIgnoreCase(table)) {
					/**判断申请日期与加班类型是否相符 */
					if (is_overtime_op) {
						/** 判断是否是平时加班 */
						if (KqAppInterface.isNormalOvertime(app_type)) {
							if (!validateAppOper.if_Peacetime(fromDate, toDate, nbase, a0100)) {
								msgmap.put("第"+(j+1)+"行", "平时加班申请时间段不应包含公休日("
										+ startDate + "-" + endDate + ")，不允许提交加班");
									continue;
							}
						}
						/** 判断是否是公休日加班 */
						if (KqAppInterface.isRestOvertime(app_type)) {
							if (!ve.isRestOfWeekDay(fromDate, toDate, nbase, a0100)) {
								msgmap.put("第"+(j+1)+"行", "申请的日期不是公休日");
								continue;
							}
							if (ve.isFeastDay(fromDate, toDate)) {
								msgmap.put("第"+(j+1)+"行", "公休日加班不应在节假日内");
								continue;
							}
						}
						/** 判断是否是节假日 */
						if (KqAppInterface.isFeastOvertime(app_type)) {
							if (!ve.isFeastDay(fromDate, toDate)) {
								msgmap.put("第"+(j+1)+"行", "申请的日期不是节假日");
								continue;
							}
						}
						/** 当选择节假日和公休日 加班 切只能申请一次的时候 */
						if (rest_overtime_time == null
								|| "1".equals(rest_overtime_time)
								|| rest_overtime_time.length() < 1) {
							/* 判断是否是公休日 */
							if (KqAppInterface.isRestOvertime(app_type)) {
								if (ve.isRestofWork(table, nbase, a0100, fromDate, toDate, "10")) {
									msgmap.put("第"+(j+1)+"行", "已经在这个公休日申请了加班");
									continue;
								}
							}
							/* 判断是否是节假日 */
							if (KqAppInterface.isFeastOvertime(app_type)) {
								/* 判断这个节日是否已经申请 */
								if (ve.isFeastofWork(table, nbase, a0100, fromDate, toDate, "11")) {
									msgmap.put("第"+(j+1)+"行", "已经在这个节假日申请了加班");
									continue;
								}
							}
						}
					}
				} else if ("Q13".equalsIgnoreCase(table)) {

					
				} else if ("Q15".equalsIgnoreCase(table)) {/** 请假检查 */
					//---linbz--20160420--除年假(06)外的假判断是否排班，年假(06)未排班也允许导入数据
					//取系统映射假期
	                String targetHolidayId = KqAppInterface.switchTypeIdFromHolidayMap(app_type);
					// 存在要求的请假类型，检查是否为假期管理假类，或调休假
	                KqParam kqParam = KqParam.getInstance();
	                // 本身是年假或映射的目标假是年假的
	                if (!(kqParam.isHoliday(this.frameconn, b0110, app_type) 
	                        || (!app_type.equals(targetHolidayId) 
	                                && kqParam.isHoliday(this.frameconn, b0110, targetHolidayId))))
	                {
	                	if (!ve.isArrangedWeek(toDate, nbase, a0100)) {
	                		msgmap.put("第"+(j+1)+"行", "请假申请的时间还没有排班，请排完班后再申请");
                            continue;
						}
	                }
					/*importMsg = ve.leaveTimeApp(fromDate, toDate, nbase, a0100, (String) empA01.get("b0110"));
					if (!"".equals(importMsg))
		            {
		                importMsg = "" + importMsg;
		                return;
		            }*/
					/** 对管理假期进行检查 ，这里要考虑excel里面的记录*/
					/*countMap = new HashMap();
					countMap.put("nbase", nbase);
					countMap.put("a0100", a0100);
					countMap.put("b0110", (String) empA01.get("b0110"));
					countMap.put(table + "z1", startDate);
					countMap.put(table + "z3", endDate);
					countMap.put(table + "03", app_type);
					
					countMaps.add(countMap);
					
					
					
					
					importMsg = checkMasterHoliday(fromDate, toDate, empA01, app_type, nbase, countMaps);
					if (!"".equals(importMsg)) {
						importMsg = "" + importMsg;
					    return;
					}
	    			*//** 如果请调休假 检查调休假可用时长是否够用*//*
					UpdateQ33 updateq33 = new UpdateQ33(this.userView,this.frameconn);
					String hr_counts = "";
					String leavetime_type_used_overtime = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                    long times = (toDate.getTime() - fromDate.getTime())/(60L * 1000L);
                    hr_counts = String.valueOf((int)times);
					if(app_type.equalsIgnoreCase(leavetime_type_used_overtime))
					    importMsg = ve.checkUsableTime(fromDate,empA01,app_type,nbase,"",hr_counts);
					if (!"".equals(importMsg)) {
						importMsg = "" + importMsg;
	                    return;
	                }*/
					
					
				}
				// 检验在 请假Q15 公出Q13 加班Q11 是否有冲突
				if (!"q13".equalsIgnoreCase(table)
						&& ve.checkTimeX("q15", nbase, a0100, OperateDate.dateToStr(fromDate, "yyyy-MM-dd HH:mm"), 
								OperateDate.dateToStr(toDate, "yyyy-MM-dd HH:mm"))) {
					msgmap.put("第"+(j+1)+"行", "数据申请的时间段在请假申请中已经申请过，无法申请");
					continue;
				}
				if (!"q13".equalsIgnoreCase(table)
						&& ve.checkTimeX("q11", nbase, a0100, OperateDate.dateToStr(fromDate, "yyyy-MM-dd HH:mm"), 
								OperateDate.dateToStr(toDate, "yyyy-MM-dd HH:mm"))) {
					msgmap.put("第"+(j+1)+"行", "数据申请的时间段在加班申请中已经申请过，无法申请");
					continue;
				}
				if ("q13".equalsIgnoreCase(table)
						&& ve.checkTimeX("q13", nbase, a0100, OperateDate.dateToStr(fromDate,"yyyy-MM-dd HH:mm"), 
								OperateDate.dateToStr(toDate, "yyyy-MM-dd HH:mm"))) {
					msgmap.put("第"+(j+1)+"行", "数据申请的时间段在公出申请中已经申请过，无法申请");
					continue;
				}
				
				String message = "";
				if(infoList.size() > 0) {
                    int a0100Index = importValue.size()-6;
                    int nbaseIndex = importValue.size()-4;
                    //请假最后还有个是否销假记录标示，所以a0100和nbase需要往前一列
                    if ("q15".equalsIgnoreCase(table)) {
                        a0100Index--;
                        nbaseIndex--;
                    }
                    message = kqCheckAppRepeat(infoList,importValue,fromIndex,toIndex,typeIndex,a0100Index,nbaseIndex,j);
                }
				
				if (message.length() > 0) 
				{
					importMsg = message;
					msgmap.put("第"+(j+1)+"行", message);
					continue;
				}
				
				infoList.add(importValue);
				
				updateCount++;
			}
			
			if((msgmap==null || "".equals(msgmap) || msgmap.size()==0) && (importMsg==null || "".equals(importMsg))){
				dao.batchInsert(sql.toString(), infoList) ;
				importMsg = "成功导入" + updateCount + "条记录！";
			}else if(msgmap!=null && !"".equals(msgmap) && msgmap.size()>0){
				dao.batchInsert(sql.toString(), infoList) ;
				importMsg = "成功导入" + updateCount + "条记录！";
				LazyDynaBean ldb = new LazyDynaBean();
				for (String key : msgmap.keySet()) {
					ldb= new LazyDynaBean();
					ldb.set("keyid", key);
					ldb.set("content", msgmap.get(key));
					msglist.add(ldb);
			    }
				outName=creatExcel(msglist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(wb);
			PubFunc.closeIoResource(in);
			this.getFormHM().put("outName", SafeCode.decode(outName));
			this.getFormHM().put("importMsg", importMsg); 
			this.getFormHM().put("msglist", msglist); 	
		}
	}
private String creatExcel(ArrayList list) throws Exception{
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBottomBorderColor((short) 8);
		style2.setLeftBorderColor((short) 8);
		style2.setRightBorderColor((short) 8);
		style2.setTopBorderColor((short) 8);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.LEFT);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式


		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		
		HSSFRow row =sheet.getRow(0);
		if(row==null){
			row=sheet.createRow(0);
		}
		HSSFCell cell = null;
		sheet.setColumnWidth((0), 7000);
		cell=row.getCell(0);
		if(cell==null)
			cell=row.createCell(0);
		cell.setCellValue(cellStr("位置"));
		cell.setCellStyle(style2);
		sheet.setColumnWidth((1), 15000);
		cell=row.getCell(1);
		if(cell==null)
			cell=row.createCell(1);
		cell.setCellValue(cellStr(ResourceFactory.getProperty("workbench.info.content.lebal")));
		cell.setCellStyle(style2);

		try
		{
			int rowCount = 1;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean ldb = (LazyDynaBean)list.get(i);
				String keyid=(String)ldb.get("keyid");
				String content=(String)ldb.get("content");
				content = SafeCode.decode(content);
				String m[]=content.split("<input");
				if(m.length==1){
					content = content.replace("&nbsp;","");
					content = content.replace("</br>","\n");
				}else{
					content=m[0];
					content = content.replace("&nbsp;","");
					content = content+"\n";
				}
				row =sheet.getRow(rowCount);
				if(row==null){
					row=sheet.createRow(rowCount);
				}
				cell = row.getCell(0);
				if(cell==null)
					cell=row.createCell(0);
				cell.setCellStyle(style1);
				cell.setCellValue(SafeCode.decode(keyid));
				cell = row.getCell(1);
				if(cell==null)
					cell=row.createCell(1);
				cell.setCellStyle(style1);
				cell.setCellValue(content);
				rowCount++;
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}

		String outName = this.userView.getUserName()+"_msgOut"+ ".xls";

		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		//outName = outName.replace(".xls", "#");
		outName = PubFunc.encrypt(outName);
		sheet = null;
		wb = null;
		return outName;
	}
public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
{

	HSSFCellStyle style = workbook.createCellStyle();
	style.setBorderBottom(BorderStyle.THIN);
	style.setBorderLeft(BorderStyle.THIN);
	style.setBorderRight(BorderStyle.THIN);
	style.setBorderTop(BorderStyle.THIN);
	style.setVerticalAlignment(VerticalAlignment.CENTER);
	style.setBottomBorderColor((short) 8);
	style.setLeftBorderColor((short) 8);
	style.setRightBorderColor((short) 8);
	style.setTopBorderColor((short) 8);
	return style;
}

public HSSFRichTextString cellStr(String context)
{

	HSSFRichTextString textstr = new HSSFRichTextString(context);
	return textstr;
}
	private String kqCheckAppRepeat(ArrayList infoList, ArrayList importValue, int fromIndex, int toIndex, int type, int a0100Index, int nbaseIndex, int rowNum){
		String mess = "";
		boolean isOk = true;
		ArrayList oneInfo = new ArrayList();
		Date appFromDate = (Date) importValue.get(fromIndex -1);
		Date appToDate = (Date) importValue.get(toIndex -1);
		String a0100 = (String) importValue.get(a0100Index-1);
		String nbase = (String) importValue.get(nbaseIndex-1);
		
		Date startDate = null;
		Date toDate = null;
		
		int i = 0;
		for (i = 0; i < infoList.size(); i++) 
		{
			oneInfo = (ArrayList) infoList.get(i);
			String oneA0100 = (String) oneInfo.get(a0100Index-1); 
			String oneNbase = (String) oneInfo.get(nbaseIndex-1);
			if(!(a0100 + nbase).equals(oneA0100 + oneNbase))
				continue;
			
			startDate = (Date) oneInfo.get(fromIndex -1);
			toDate = (Date) oneInfo.get(toIndex -1);
			if (appFromDate.getTime() <= startDate.getTime() && appToDate.getTime() > startDate.getTime() 
					&& appToDate.getTime() <= toDate.getTime()) 
			{
				isOk = false;
				break;
			}else if (appFromDate.getTime() >= startDate.getTime() && appToDate.getTime() <= toDate.getTime()) 
			{
				isOk = false;
				break;
			}else if (appFromDate.getTime() >= startDate.getTime() && appFromDate.getTime() < toDate.getTime()
					&& appToDate.getTime() >= toDate.getTime()) 
			{
				isOk = false;
				break;
			}else if (appFromDate.getTime() <= startDate.getTime() && appToDate.getTime() >= toDate.getTime()) 
			{
				isOk = false;
				break;
			}
		}
		if (!isOk) 
		{
		    String pattern = "yyyy-MM-dd HH:mm";
			mess = "第" + (rowNum + 1) + "行(" 
			     + DateUtils.format(appFromDate, pattern)
			     + "~"
			     + DateUtils.format(appToDate, pattern)
			     +")" 
			     + "与第" + (i + 2) + "行(" 
                 + DateUtils.format(startDate, pattern)
                 + "~"
                 + DateUtils.format(toDate, pattern)
                 +")申请时段有冲突！";
		}
		return mess;
	}
	
	/**
	 * 判断Q03中那些指标是从A01主集中取得的
	 * 
	 * @param itemtype
	 * @param itemid
	 * @return
	 */
	private boolean getindexA01(String itemtype, String itemid) {
		boolean field = true;
		itemtype = itemtype.toUpperCase();
		itemid = itemid.toUpperCase();

		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='"
					+ itemid + "' and itemtype='" + itemtype + "'";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String itemi = rs.getString("itemid");
				if (itemi != null && itemi.length() > 0) {
					field = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return field;
	}

	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	private boolean isDataType(int decwidth, String itemtype, String value, int itemlength) {

		if ("N".equals(itemtype)) {
			if (decwidth == 0) {
				return value.matches("^[+-]?[\\d]+$");
			} else {
			    return value.matches("^[+-]?[\\d]*[.]?[\\d]+");
			}
		}
	   
		if (!"D".equalsIgnoreCase(itemtype))
		    return false;
		
		boolean flag = true;
		try {
			value = value.replace("/", "-").replace(".", "-").trim();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			fm.setLenient(false);
			format.setLenient(false);
			String hTime = "00";
			String mTime = "00";
			if(value.indexOf("-")>-1)
			{
				String[] dateStr = value.split("-");
				//非法的日期格式：没有包含年月日三个部分或有多余的部分
				if (dateStr.length != 3)
				    return false;
				
				//非法的年份
			    if(dateStr[0].trim().length() != 4)
			        return false;
			    
			    //非法的月份
			    if(dateStr[1].trim().length() == 0 || dateStr[1].trim().length()>2)
                    return false;
			    
			    //非法的日期
			    if(dateStr[2].trim().length() == 0)
                    return false;
			    
			    //不包含时间部分
			    if(dateStr[2].trim().length()<=2) {
			        String aDate = dateStr[0].trim() 
			                + "-" + String.format("%02d", Integer.valueOf(dateStr[1].trim())) 
			                + "-" + String.format("%02d", Integer.valueOf(dateStr[2].trim()));
			        format.parse(aDate);
			    } else {
			        /*String[] timePart = dateStr[2].trim().split(" ");
			        //此部分应为分钟 + 时间两部分，如不是2部分，则不是合法的数据
			        if(timePart.length != 2)
			            return false;
			        
			        //日部分
			        String dayStr = timePart[0].trim();
			        String timeStr = timePart[1].trim();*/
			    	//非法的日期格式：日部分长度大于2
			    	String timePart = dateStr[2].replace(" ", "");
			    	//日部分
			    	String dayStr = timePart.substring(0, 2);
			    	//时间部分
			    	String timeStr = timePart.substring(2);
			        if(dayStr.length()>2)
			            return false;
			        
			        String aDateTime = dateStr[0].trim() 
                            + "-" + String.format("%02d", Integer.valueOf(dateStr[1].trim())) 
                            + "-" + String.format("%02d", Integer.valueOf(dayStr))
                            + " " + getKqTime(timeStr,hTime,mTime);
			        fm.parse(aDateTime);
			    }
			} else if(value.length()==8){
				format.parse(value.substring(0,4)+"-"+value.substring(4,6)+"-"+value.substring(6,8));
			} else{
				return false;
			}
			flag = true;
		} catch (Exception e) {
		    e.printStackTrace();
			flag = false;
		}
			//flag = value.matches("[0-9]{4}.[0-9]{2}.[0-9]{2}\\s[0-2]{2}:[0-9]{2}");
		return flag;
	}

	private String getKqTime(String timeStr, String hTime, String mTime) {
		String thecellTimeStringCellValue = "-1";
		if(isGetKqTime(timeStr)){
			if (timeStr.indexOf("：")>-1){
				timeStr = timeStr.replace("：", ":");
			}
			if (timeStr.indexOf(":")>-1){
				String[] hourAndMinte = timeStr.split(":");
				if(hourAndMinte.length == 1){
					if(hourAndMinte[0].length()==1)
						thecellTimeStringCellValue ="0"+ hourAndMinte[0]+":00";
					else
						thecellTimeStringCellValue = hourAndMinte[0]+":00";
				} 
				if(hourAndMinte.length == 2||hourAndMinte.length == 3){
					if(hourAndMinte[0].length()==1)
						thecellTimeStringCellValue ="0"+ hourAndMinte[0];
					else
						thecellTimeStringCellValue = hourAndMinte[0];
					if(hourAndMinte[1].length()==1)
						thecellTimeStringCellValue = thecellTimeStringCellValue+":0"+hourAndMinte[1];
					else
						thecellTimeStringCellValue = thecellTimeStringCellValue+":"+hourAndMinte[1];
				}else if (hourAndMinte.length == 0){
					thecellTimeStringCellValue = hTime+":"+mTime;
				}
			} else {
				if (timeStr.length()==1)
					thecellTimeStringCellValue = "0"+timeStr+":00";
				else if(timeStr.length()==2)
					thecellTimeStringCellValue = timeStr+":00";
				else 
					thecellTimeStringCellValue =hTime+":"+mTime;
			}
		}else{
			return  thecellTimeStringCellValue;
		}
		return thecellTimeStringCellValue;
	}

	private String getCodeByDesc(String codeDesc, String fieldCodeSetId){
		String sql="select codeitemid from codeitem where codeitemdesc='"+codeDesc+"' and codesetid='"+fieldCodeSetId+"'";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs=dao.search(sql);
			if(rs.next()){
				String codeitemid=rs.getString("codeitemid");
				return codeitemid;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
		
	}
	/**
	 * 根据工号获取人员基本信息
	 * @param g_no 工号
	 * @param field 工号字段
	 * @param dao dao
	 * @param a_code
	 * @param code
	 * @param kind
	 * @return
	 */
	private HashMap getEmpA01(String g_no, String field, ContentDAO dao, String a_code, String code, String kind){
		HashMap list = null;
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.frameconn,userView);
        try {
			ArrayList nbaseList = kqUtilsClass.setKqPerList(code, kind);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nbaseList.size(); i++) 
			{
				sb.setLength(0);
				sb.append("select a0100,a0101,b0110,e0122,e01a1,"+field+" from " + nbaseList.get(i) + "A01");
				sb.append(" where " + field + "= '" + g_no + "'");
				if (this.userView.isAdmin()) {
					sb.append(" and 1=1 ");
				} else {
					// 要控制人员范围
					String whereIn = RegisterInitInfoData.getWhereINSql(userView, (String)nbaseList.get(i));
					sb.append(" and a0100 in (");
					sb.append(" select a0100 " + whereIn + ")");
				}
				this.frowset = dao.search(sb.toString());
				if (this.frowset.next()) 
				{
					list = new HashMap();
					list.put("nbase", nbaseList.get(i));
					list.put("a0100", this.frowset.getString(1));
					list.put("a0101", this.frowset.getString(2));
					list.put("b0110", this.frowset.getString(3));
					list.put("e0122", this.frowset.getString(4));
					list.put("e01a1", this.frowset.getString(5));
					list.put(field, g_no);
					return list;
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据班次获取班次id
	 * @param classdesc 班次名称
	 * @param dao
	 * @return
	 */
	private int getClassId(String classdesc,ContentDAO dao){
		StringBuffer sb = new StringBuffer();
		int classid = -1;
		sb.append("select class_id from kq_class where name = '" + classdesc + "'");
		try {
			this.frowset = dao.search(sb.toString());
			if (this.frowset.next()) 
			{
				classid = this.frowset.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classid;
	}
	private boolean isGetKqTime(String time) {
		if("false".equalsIgnoreCase(time) ){
			return false;
		}
		boolean isTime = true;
		String timeStr = time;
		
		try {
		    if (timeStr.indexOf("：")>-1){
		        timeStr = timeStr.replace("：", ":");
		    }
		    
		    if (timeStr.indexOf(":")>-1){
		        String[] hourAndMinte = timeStr.split(":");
		        if(hourAndMinte.length == 1){
		            if (Integer.parseInt(hourAndMinte[0])>24&&Integer.parseInt(hourAndMinte[0])<0){
		                isTime = false;
		            }
		        } 
		        
		        if(hourAndMinte.length == 2||hourAndMinte.length == 3){
		            if (Integer.parseInt(hourAndMinte[0])<24&&Integer.parseInt(hourAndMinte[0])>=0){
		                if (Integer.parseInt(hourAndMinte[1])>60||Integer.parseInt(hourAndMinte[1])<0)
		                    isTime = false;
		            } else {
		                isTime = false;
		            }
		        } else if(hourAndMinte.length>3){
		            isTime = false;
		        }
		    } else if (timeStr.length()>2){
		        isTime = false;
		    } else if(timeStr.length()<2&&timeStr.length()>0){
		        if(Integer.parseInt(timeStr)>24||Integer.parseInt(timeStr)<0){
		            isTime = false;
		        }
		    }
        } catch (Exception e) {
            isTime = false;
            e.printStackTrace();
        }
		return isTime;
	}
	private String  getDateStr (String year ,String month ,String day,String hTime,String mTime,String time) {
		String value = "";
		if(day.length()==2){
			value = year+"-"+month+"-"+day+time;
		}
		else if(day.length()==1){
			value = year+"-"+month+"-0"+day+time;
		}
		else if (day.length()>2){
			if(time.length()>1){
                return value;
			}
			if (day.indexOf(" ")>-1){
				String[] timePart = day.split(" ");
				if (timePart[0].length()==2){
					if(timePart.length==2)
						value = year+"-"+month+"-"+timePart[0]+" "+getKqTime(timePart[1],hTime,mTime);
					else
						value = year+"-"+month+"-"+timePart[0]+" "+hTime+":"+mTime;
				}
				else if (timePart[0].length()==1){
					if(timePart.length==2)
						value = year+"-"+month+"-0"+timePart[0]+" "+getKqTime(timePart[1],hTime,mTime);
					else
						value = year+"-"+month+"-0"+timePart[0]+" "+hTime+":"+mTime;
				}
			}
		}
	return value;
	}
	
	public static void main(String[] args) {
        ImportKqAppTrans ika = new ImportKqAppTrans();
        System.out.println("2016-01-01 :" + ika.isDataType(0, "D", "2016-01-01 ", 18));
        System.out.println("2016-01-01 61 :" + ika.isDataType(0, "D", "2016-01-01 61", 18));
        System.out.println("2016-01-01 01:01 :" + ika.isDataType(0, "D", "2016-01-01 01:01", 18));
        System.out.println("2016-01-01 61:01 :" + ika.isDataType(0, "D", "2016-01-01 61:01", 18));
        System.out.println("2016-01-01 01:02:03 :" + ika.isDataType(0, "D", "2016-01-01 01:02:03", 18));
        System.out.println("2016-1-1 01:02:03 :" + ika.isDataType(0, "D", "2016-1-1 01:02:03", 18));
        System.out.println("201611 01:02:03 :" + ika.isDataType(0, "D", "201611 01:02:03", 18));
        System.out.println("20160101 :" + ika.isDataType(0, "D", "20160101 ", 18));
    }
}
