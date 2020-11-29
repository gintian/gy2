package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard.ExportScoreDetailsTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 导出打分明细Bo类
 * @author haosl
 *
 */
public class ExportScoreDetailsBo {
	
	private Connection conn;
	private UserView userView;
	
	public ExportScoreDetailsBo(Connection conn,UserView userView) {
		this.conn=conn;
		this.userView = userView;
	}
	private HSSFWorkbook wb = new HSSFWorkbook();
	private HashMap<String,HSSFFont> fontMap = new HashMap<String, HSSFFont>();
	private SimpleDateFormat df = null;
	/**
	 * 生成Excel
	 * @param dataList
	 * 			需要导出的多个考核对象数据集合
	 * @throws GeneralException 
	 * @throws IOException 
	 */
	public void createExcel(List<List<LazyDynaBean>> dataList,String sheetName) throws GeneralException{
		
		try {
			int page=1;	//页码
			int pageSize = 100;	//每个sheet页最多能写入的考核对象数
			
			HSSFSheet sheet = wb.createSheet(sheetName);
			HSSFRow row = null;
			HSSFCell cell = null;
			int rowNum = 0;
			HSSFRichTextString richTextString =null; 
			LazyDynaBean rowBean = null;
			
			
			HashMap colStyleMap_head = new HashMap();//表头背景样式
			colStyleMap_head.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
			CellRangeAddress region = null;
			HSSFCellStyle style_head = this.getStyle(colStyleMap_head, "head");//TODO haosl设置样式 
			HSSFCellStyle style_data = this.getStyle(null, "");//TODO haosl设置样式 
			for(int i=0;i<dataList.size();i++){
				if (i !=0 && i % pageSize == 0) { 
					rowNum = 0;
					if(StringUtils.isBlank(sheetName))
							sheet = wb.createSheet(page + "");
					else 
						sheet = wb.createSheet(sheetName+page);
					page++;
				}
				List<LazyDynaBean> objData = dataList.get(i);//单个考核对象数据
				List<LazyDynaBean> scoreHeadList = (List<LazyDynaBean>) objData.get(1);//取得打分明细的列头
                boolean isAdd = false;
				for(int j = 0;j<objData.size();j++){
					int colNum = 0;
					if(j==0){//考核对象信息Head,打分明细Head
                        row = sheet.createRow(rowNum);
						List<LazyDynaBean> headBeanList = (List<LazyDynaBean>) objData.get(0);
						row.setHeight((short)600);//设置行高
						//设置数据区第一行列头
						cell = row.createCell(colNum);
						String cellValueStr = "";
						for(LazyDynaBean headBean:headBeanList){
							String cellvalue = (String) headBean.get("content");
							if(StringUtils.isNotEmpty(cellvalue))
								cellValueStr+=cellvalue+"/";
						}
						cell.setCellValue(cellValueStr.substring(0,cellValueStr.length()-1));
						
						//设置列头样式
						HSSFCellStyle style = wb.createCellStyle();
						HSSFFont font = wb.createFont();
						font.setFontName("宋体");
						font.setFontHeightInPoints((short)16);
						font.setBold(true);
						style.setFont(font);
						cell.setCellStyle(style);
						colNum = colNum++;
					}else if(j==1){
						//设置第二行列头
                        row = sheet.createRow(rowNum);
						row.setHeight((short)400);//设置行高
						for(LazyDynaBean headBean:scoreHeadList){
							String cellvalue = (String) headBean.get("content");
							boolean columnHidden = headBean.get("columnHidden") == null ? false : (Boolean)headBean.get("columnHidden");//当前列是否隐藏
							sheet.setColumnHidden(colNum, columnHidden);
							List<LazyDynaBean> childheadBeanList = (List<LazyDynaBean>) headBean.get("childColumns");
							boolean columnLocked = headBean.get("columnLocked") == null ? false : (Boolean)headBean.get("columnLocked");//当前列是否锁定
							if(childheadBeanList!=null &&childheadBeanList.size()>0){
									cell = row.createCell(colNum);
									//rowNum,rowNum,colNum,colNum+childheadBeanList.size()-1
                                    isAdd = true;
									if(1 < childheadBeanList.size()) {
										region = new CellRangeAddress(rowNum, rowNum, colNum, colNum+childheadBeanList.size()-1);
										sheet.addMergedRegion(region);
										cell.setCellValue(cellvalue);//复合列名
										this.setRegionStyle(sheet, region, style_head);
									}else{
                                        cell.setCellValue(cellvalue);
                                        cell.setCellStyle(style_head);
                                    }

									HSSFRow row2 = sheet.getRow(rowNum+1);
									if(row2==null){//如果已经创建了行则不在创建
										row2 = sheet.createRow(rowNum+1);
									}
                                    row2.setHeight((short)400);
									for(LazyDynaBean childheadBean : childheadBeanList){
										if(childheadBean.get("columnWidth")!=null)
											sheet.setColumnWidth(colNum, (Integer)childheadBean.get("columnWidth"));
										String childValue = (String) childheadBean.get("content");
										cell = row2.createCell(colNum);
										cell.setCellValue(childValue);//复合列名
										cell.setCellStyle(style_head);
										colNum++;
									}
									
							}else{
								if(headBean.get("columnWidth")!=null)
									sheet.setColumnWidth(colNum, (Integer)headBean.get("columnWidth"));
								cell = row.createCell(colNum);
								cell.setCellValue(cellvalue);
								region = new CellRangeAddress(rowNum, rowNum+1, colNum,colNum);
								sheet.addMergedRegion(region);
								style_head.setLocked(columnLocked);
								this.setRegionStyle(sheet, region, style_head);
								colNum++;
							}
						}
					}else{//输出数据
                        if(isAdd) {
                            rowNum++;
                            isAdd = false;
                        }
                        row = sheet.createRow(rowNum);
						row.setHeight((short)800);
						rowBean = objData.get(j);
						LazyDynaBean scoreData = null;
						for(LazyDynaBean scoreHead:scoreHeadList){
							//当前列是否锁定
							List<LazyDynaBean> childheadBeanList = (List<LazyDynaBean>) scoreHead.get("childColumns");
							if(childheadBeanList!=null &&childheadBeanList.size()>0){
								for(LazyDynaBean childheadBean : childheadBeanList){
									String columnId = (String)childheadBean.get("columnId");
									boolean columnLocked = childheadBean.get("columnLocked") == null ? false : (Boolean)scoreHead.get("columnLocked");
									scoreData = (LazyDynaBean) rowBean.get(columnId);//获得数据列
									String content = (String)scoreData.get("content");
									cell = row.createCell(colNum);
									richTextString=new HSSFRichTextString(content);
									cell.setCellValue(richTextString);
									style_data.setLocked(columnLocked);
									cell.setCellStyle(style_data);
									colNum++;
								}
							}else{
								String columnId = (String)scoreHead.get("columnId");
								boolean columnLocked = scoreHead.get("columnLocked") == null ? false : (Boolean)scoreHead.get("columnLocked");
								scoreData = (LazyDynaBean) rowBean.get(columnId);//获得数据列
								String content = (String)scoreData.get("content");
								cell = row.createCell(colNum);
								 richTextString=new HSSFRichTextString(content);
								 cell.setCellValue(richTextString);
								 style_data.setLocked(columnLocked);
								 cell.setCellStyle(style_data);
								 colNum++;
							}
						 }
					}
					rowNum++;
				}
				rowNum = rowNum+4;//空行后输出
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}
	/**
	 * 得到列头和数据的集合
	 * 	数据结构：
	 * 		第一层是考核对象的一些信息
	 * 		第二层是列头
	 * 		第三层是对应列头的数据
	 * @param dataSql
	 * @return
	 * @throws SQLException 
	 */
	public List<List<LazyDynaBean>> getDataList(String planId) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		List<List<LazyDynaBean>> dataList= new ArrayList<List<LazyDynaBean>>();//总的数据的集合,存放objDataList
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		Integer decwidth = 0;
		try {
			List<List<LazyDynaBean>> objHeadList = getObjHeadList(planId);//第一行列头
			if(objHeadList.size()==0){
				return null;
			}
			List<LazyDynaBean> scoreDetailHead_befoer = getScoreDatailHead_before(planId,ExportScoreDetailsTrans.exceptFields,
					ExportScoreDetailsTrans.notEditFields,
					ExportScoreDetailsTrans.isAddWidth,
					ExportScoreDetailsTrans.islock);//第二行猎头
			String dataSql =  "";
			LazyDynaBean rowBean = null;
			LazyDynaBean colBean = null;
			for(int i=0;i<objHeadList.size();i++){
				List objDataList = new ArrayList();//存放列头和数据的集合
				List<LazyDynaBean> headBeanList = objHeadList.get(i);//获得单个考核对象的列头
				LazyDynaBean a0101_bean = (LazyDynaBean)headBeanList.get(0);//获得考核对象列头bean
				String objectid = (String)a0101_bean.get("objectid");
				String objectType = (String)a0101_bean.get("objectType");
				
				dataSql = this.getSql(planId, objectid,objectType);
				
				rs = dao.search(dataSql);
				List<LazyDynaBean> scoreDatailHead_after = this.getScoreDatailHead_after(planId,objectid);
				//得到第二行的全部列头
				List<LazyDynaBean> allScoreDetailHead = this.getAllScoreDatilHeadList(scoreDetailHead_befoer,scoreDatailHead_after);
				//组装列和数据，第一行和第二行为为列头
				objDataList.add(headBeanList);
				objDataList.add(allScoreDetailHead);
				//组装打分明细数据
				while(rs.next()){
					rowBean = new LazyDynaBean();
					for(LazyDynaBean headBean : allScoreDetailHead){
						String columid ="";
						String codesetid ="";
						String itemtype="";
						String dateFormat="";
						List<LazyDynaBean> childHeadList = (List<LazyDynaBean>) headBean.get("childColumns");
						if(childHeadList!=null && childHeadList.size()>0){
							for(int x = 0;x<childHeadList.size();x++){
								LazyDynaBean childHeadBean = childHeadList.get(x);
								columid = (String) childHeadBean.get("columnId");
								codesetid = (String)childHeadBean.get("codesetid");
								itemtype = (String)childHeadBean.get("colType");
								dateFormat = (String)childHeadBean.get("dateFormat");
								if(childHeadBean.get("decwidth") != null)
									decwidth = (Integer)childHeadBean.get("decwidth");
								colBean = new LazyDynaBean();
								if (StringUtils.isEmpty(codesetid))
									codesetid = "0";
								if ("D".equals(itemtype)) {
									Date d = null;
									//日期型
									if (StringUtils.isEmpty(dateFormat))
										df = new SimpleDateFormat("yyyy-MM-dd");
									else
										df = new SimpleDateFormat(dateFormat);
									d = rs.getDate(columid);
									if (d != null)
										colBean.set("content", df.format(d));
									else
										colBean.set("content", "");
									rowBean.set(columid, colBean);
								} else if ("M".equals(itemtype)) {
									//是备注型
									colBean.set("content", Sql_switcher.readMemo(rs,columid));
									rowBean.set(columid, colBean);
								} else if ("A".equals(itemtype) && !"0".equals(codesetid)) {
									// 是代码类
									String value = rs.getString(columid);
									if(StringUtils.isNotBlank(value))
									{
										if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
										{
											String theUM="";								
											if("e0122".equalsIgnoreCase(columid))
											{
												if(Integer.parseInt(display_e0122)==0)
													theUM=AdminCode.getCodeName("UM",value);
												else
												{
													CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
									    	    	if(item!=null)
									    	    	{
									    	    		theUM=item.getCodename();
									        		}
									    	    	else
									    	    	{
									    	    		theUM=AdminCode.getCodeName("UM",value);
									    	    	}
												}								
											}else															
												theUM = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
												colBean.set("content",theUM);
										}else{
										    String content = "";
										    if("UN".equals(codesetid)){
										        content = AdminCode.getCodeName("UN", value);
										        if(StringUtils.isBlank(content))
										            content = AdminCode.getCodeName("UM", value);
										    }else
										        content = AdminCode.getCodeName(codesetid, value); 
										    colBean.set("content", content);
										}
									} else {
										colBean.set("content", "");
									}
									rowBean.set(columid, colBean);
								} else if ("N".equals(itemtype)) {
									//数字型
									if (rs.getString(columid) != null) {
										colBean.set("content", PubFunc.round(rs.getString(columid), decwidth));
									} else
										colBean.set("content", "");
									rowBean.set(columid, colBean);
								} else {
									if (rs.getString(columid) != null)
										colBean.set("content", rs.getString(columid));
									else
										colBean.set("content", "");
									rowBean.set(columid, colBean);
								}
							}
						}else{
							columid = (String) headBean.get("columnId");
							codesetid = (String)headBean.get("codesetid");
							itemtype = (String)headBean.get("colType");
							dateFormat = (String)headBean.get("dateFormat");
							if(headBean.get("decwidth") != null)
								decwidth = (Integer)headBean.get("decwidth");
							colBean = new LazyDynaBean();
							if (StringUtils.isEmpty(codesetid))
								codesetid = "0";
							if ("D".equals(itemtype)) {
								Date d = null;
								//日期型
								if (StringUtils.isEmpty(dateFormat))
									df = new SimpleDateFormat("yyyy-MM-dd");
								else
									df = new SimpleDateFormat(dateFormat);
								d = rs.getDate(columid);
								if (d != null)
									colBean.set("content", df.format(d));
								else
									colBean.set("content", "");
								rowBean.set(columid, colBean);
							} else if ("M".equals(itemtype)) {
								//是备注型
								colBean.set("content", Sql_switcher.readMemo(rs,columid));
								rowBean.set(columid, colBean);
							} else if ("A".equals(itemtype) && !"0".equals(codesetid)) {
								// 是代码类
								String value = rs.getString(columid);
								if(StringUtils.isNotBlank(value))
								{
									if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
									{
										String theUM="";								
										if("e0122".equalsIgnoreCase(columid))
										{
											if(Integer.parseInt(display_e0122)==0)
												theUM=AdminCode.getCodeName("UM",value);
											else
											{
												CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
								    	    	if(item!=null)
								    	    	{
								    	    		theUM=item.getCodename();
								        		}
								    	    	else
								    	    	{
								    	    		theUM=AdminCode.getCodeName("UM",value);
								    	    	}
											}								
										}else															
											theUM = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
											colBean.set("content",theUM);
									}else{
									    String content = "";
									    if("UN".equals(codesetid)){
									        content = AdminCode.getCodeName("UN", value);
									        if(StringUtils.isBlank(content))
									            content = AdminCode.getCodeName("UM", value);
									    }else
									        content = AdminCode.getCodeName(codesetid, value); 
									    colBean.set("content", content);
									}
								} else {
									colBean.set("content", "");
								}
								rowBean.set(columid, colBean);
							} else if ("N".equals(itemtype)) {
								//数字型
								if (rs.getString(columid) != null) {
									if("p0415".equalsIgnoreCase(columid)){
										 BigDecimal bd = new BigDecimal(rs.getDouble(columid)*100);
										 bd = bd.setScale(decwidth, BigDecimal.ROUND_HALF_UP);
										if(bd.doubleValue()!=0)
											colBean.set("content",bd.doubleValue()+"%");
									}else{
										colBean.set("content", PubFunc.round(rs.getString(columid), decwidth));
									}
								} else
									colBean.set("content", "");
								rowBean.set(columid, colBean);
							} else {
								if (rs.getString(columid) != null)
									colBean.set("content", rs.getString(columid));
								else
									colBean.set("content", "");
								rowBean.set(columid, colBean);
							}
						}
						
					}
					objDataList.add(rowBean);
				}
				dataList.add(objDataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataList;
	}
	/**
	 * 组装完整的打分列头
	 * @param scoreDetailHead_befoer
	 * @param scoreDatailHead_after
	 * @return
	 * @throws GeneralException
	 */
	private List<LazyDynaBean> getAllScoreDatilHeadList(List<LazyDynaBean> scoreDetailHead_befoer,
			List<LazyDynaBean> scoreDatailHead_after)
			throws GeneralException {
		//组装完整的第二行列头
		List<LazyDynaBean> allScoreDetailHead = new ArrayList<LazyDynaBean>();
		allScoreDetailHead.addAll(scoreDetailHead_befoer);
		allScoreDetailHead.addAll(scoreDatailHead_after);
		//自定义计算得分
		LazyDynaBean headBean = new LazyDynaBean();
		headBean.set("columnId", "task_score");//总分
		headBean.set("content","计算得分");
		headBean.set("columnLocked", false);
		headBean.set("decwidth", 2);
		headBean.set("colType", "N");
		headBean.set("codesetid","0");
		HashMap headStyleMap = new HashMap();
	    allScoreDetailHead.add(headBean);
		return allScoreDetailHead;
	}
	
	/**
	 * 组装考核对象信息列头
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public List<List<LazyDynaBean>> getObjHeadList(String planId) throws GeneralException{
		List<List<LazyDynaBean>> objHeadList = new ArrayList<List<LazyDynaBean>>();
		List<LazyDynaBean> headBeans = null;  
		LazyDynaBean headBean = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "";
		RowSet rs = null;
		try {
			int object_type=this.getObjType(planId);//考核对象类型
			String b0110Str = this.userView.getUnitIdByBusi("5");//权限范围
			String sql_b0110 = "";
			if(b0110Str.split("`")[0].length() >2){//只能查看下级
				String[] b0110Array = b0110Str.split("`");
				sql_b0110 += " and (";
				for(int i=0; i<b0110Array.length; i++){
					String b = b0110Array[i].substring(2);
						sql_b0110+=" (b0110 like '"+b+"%' or E0122 like '"+b+"%' or E01A1 like '"+b+"%') ";
						if(i<b0110Array.length-1)
							sql_b0110+=" or ";
				}
				sql_b0110 += ") ";
			}
			//查看考核对象权限范围控制
			if(object_type == 2){
				sql = "select a0100,a0101,b0110,e0122 from UsrA01 where a0100 in (select object_id from per_object where plan_id= '"+planId+"'";
				sql+=sql_b0110;
				sql+=") order by a0000";//根据人员序号排序
				rs = dao.search(sql);
				while(rs.next()){
					
					headBeans = new ArrayList<LazyDynaBean>();
					String a0101 = rs.getString("a0101");
					String b0110 = rs.getString("b0110");
					String a0100 = rs.getString("a0100");
					String e0122 = rs.getString("e0122");
					//考核对象姓名
					headBean = new LazyDynaBean();
					headBean.set("columnId", "a0101");
					headBean.set("objectType", "2");
					headBean.set("content",a0101);
					headBean.set("objectid", a0100);
					headBean.set("b0110", b0110);//单位
					headBean.set("e0122", e0122);//部门
					headBeans.add(headBean);
					//考核对象的岗位E01A1
					headBean = new LazyDynaBean();
					headBean.set("columnId", "e0122");
					CodeItem e0122_item = AdminCode.getCode("UM", e0122);
					if(e0122_item!=null)
						headBean.set("content",e0122_item.getCodename());
					else
						headBean.set("content","");
					headBean.set("objectid", a0100);
					headBeans.add(headBean);
					
					//考核对象的单位B0110
					headBean = new LazyDynaBean();
					headBean.set("columnId", "b0110");
					CodeItem b0110_item = AdminCode.getCode("UN", b0110);
					if(b0110_item!=null)
						headBean.set("content",b0110_item.getCodename());
					else
						headBean.set("content","");
					headBeans.add(headBean);
					objHeadList.add(headBeans);
				}
			}else{
				sql = "select object_id from per_object where plan_id='"+planId+"'" + sql_b0110;
				rs = dao.search(sql);
				CodeItem item  = null;
				while(rs.next()){
					headBeans = new ArrayList<LazyDynaBean>();
					String orgId = rs.getString("object_id");//组织机构号
					item = AdminCode.getCode("UM",orgId)==null?AdminCode.getCode("UN",orgId):AdminCode.getCode("UM",orgId);
					String orgName = item.getCodename();
					
					headBean = new LazyDynaBean();
					headBean.set("columnId", "type");
					headBean.set("content","团队考核");
					headBean.set("objectType", "1");
					headBean.set("objectid",orgId);
					headBeans.add(headBean);
					
					headBean = new LazyDynaBean();
					headBean.set("columnId","objectid");
					headBean.set("content",orgName);
					headBeans.add(headBean);
					objHeadList.add(headBeans);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return objHeadList;
	}
	/**
	 * 组装打分明细列头
	 * @return
	 * 		LazyDynaBean
	 * @author haosl
	 * @throws GeneralException 
	 */
	public List<LazyDynaBean> getScoreDatailHead_before(String planId,String exceptFields,String notEditFields, String isAddWidth, String islock) throws GeneralException{
		List<LazyDynaBean> headBeans = new ArrayList<LazyDynaBean>();  
		LazyDynaBean headBean = null;
		HashMap headStyleMap = null;
		ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("p04", 1);
		
		//获得该任务下的目标卡指标
		LoadXml loadxml = new LoadXml(this.conn, planId);																			
		Hashtable htxml = loadxml.getDegreeWhole();	
		String targetDefineItem = (String)htxml.get("TargetDefineItem");
		String targetTraceEnabled = (String)htxml.get("TargetTraceEnabled");
		try {
			//自定义列 项目名称
			headBean = new LazyDynaBean();
			headBean.set("columnId", "project_name");
			headBean.set("content","项目名称");
			headBean.set("columnLocked", true);
			headBean.set("decwidth", 0);
			headBean.set("colType","A");
			headBean.set("codesetid","0");
			headBean.set("columnWidth",4000);//表头宽度设置 
			headBeans.add(headBean);
			//自定义列 项目名称
			headBean = new LazyDynaBean();
			headBean.set("columnId", "p0407");
			headBean.set("content","任务内容");
			headBean.set("columnLocked", true);
			headBean.set("decwidth", 0);
			headBean.set("colType","A");
			headBean.set("codesetid","0");
			headBean.set("columnWidth",7000);//表头宽度设置 
			headBeans.add(headBean);
			for(int i=0;i<fieldList.size();i++){
				FieldItem fi = fieldList.get(i);
				
				// 去除没有启用的指标
				if (!"1".equals(fi.getUseflag())) {
					continue;
				}
				// 去除隐藏的指标
				if (!"1".equals(fi.getState())) {
					continue;
				}
				// 去除不需要的指标
				if (exceptFields.indexOf(fi.getItemid().toLowerCase()) != -1
						||"project_name".equalsIgnoreCase(fi.getItemid())
						||"p0407".equalsIgnoreCase(fi.getItemid())) {
					continue;
				}
				//如果配置了目标卡指标则，导出已选指标，如果未选择则导出业务字典的可用指标
				if ("true".equalsIgnoreCase(targetTraceEnabled) && targetDefineItem.indexOf(fi.getItemid().toUpperCase()) == -1) {
					continue;
				}
				
				
				headBean = new LazyDynaBean();
				headBean.set("columnId", fi.getItemid());
				if("a0101".equalsIgnoreCase(fi.getItemid()))
					headBean.set("content","考核对象");
				else
					headBean.set("content",fi.getItemdesc());
				headBean.set("columnLocked", true);
				headBean.set("decwidth", fi.getDecimalwidth());
				headBean.set("colType", fi.getItemtype());
				headBean.set("codesetid",fi.getCodesetid());
				headBean.set("columnWidth",fi.getDisplaywidth()*300);//表头宽度设置 
			    headBeans.add(headBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return headBeans;
	}
	/**
	 * 将打分信息列头分为两部分，前部分每个考和对象的列头相同，后部分每个考核对象不同列头可能不同，
	 *  所以将获得后部分的列头方法单独拿出来
	 * @return
	 * @throws GeneralException 
	 */
	public List<LazyDynaBean> getScoreDatailHead_after(String planId,String objectId) throws GeneralException{
		List<LazyDynaBean> headBeans = new ArrayList<LazyDynaBean>();  
		LazyDynaBean headBean = null;
		HashMap headStyleMap = null;
		//获得考核主体列头,复合列头
		List<Map<String, String>> mainBodyList = this.getMainBodySet(planId,objectId);
		for(Map<String, String> mainBody : mainBodyList){
			String body_id = mainBody.get("body_id");
			String name = mainBody.get("name");
			headBean = new LazyDynaBean();
			headBean.set("content",name);
			headBean.set("columnLocked", false);
			headBean.set("colType", "A");
			headBean.set("codesetid","0");
		   
		    LoadXml loadxml = new LoadXml(this.conn, planId);																			
			Hashtable htxml = loadxml.getDegreeWhole();	
			//是否启用评分说明
			String showDeductionCause = (String)htxml.get("showDeductionCause");
			
			
			//通过主题类别ID查找类别下的考核主体
			List<Map<String, String>> bodyList = this.getMainBody(body_id, planId,objectId);
			List<LazyDynaBean> childColumns = new ArrayList<LazyDynaBean>();
			if(bodyList.size()>0){
				LazyDynaBean childHeadBean = null;
				for(Map<String, String> bodyMap : bodyList){
					String a0101 = bodyMap.get("a0101");
					String mainbody_id = bodyMap.get("mainbody_id");
					childHeadBean = new LazyDynaBean();
					childHeadBean.set("columnId", mainbody_id);
					childHeadBean.set("content",a0101);
					childHeadBean.set("columnLocked", false);
					childHeadBean.set("decwidth", 1);
					childHeadBean.set("colType", "N");
					childHeadBean.set("codesetid","0");
					childHeadBean.set("columnWidth",2500);//表头宽度设置 
					childColumns.add(childHeadBean);
					//启动评分说明后显示评分说明
					if("true".equalsIgnoreCase(showDeductionCause)){
						childHeadBean = new LazyDynaBean();
						childHeadBean.set("columnId", "reas_"+mainbody_id);
						childHeadBean.set("content","完成情况");
						childHeadBean.set("columnLocked", false);
						childHeadBean.set("colType", "M");
						childHeadBean.set("codesetid","0");
						childHeadBean.set("columnWidth",4000);//表头宽度设置 
					    childColumns.add(childHeadBean);
					}
				}
				headBean.set("childColumns", childColumns);
			}else{
				continue;
			}
			headBeans.add(headBean);
		}
		return headBeans;
	}
	/**
	 * 
	 * @date 2015-6-25
	 * @param styleMap
	 * @param type
	 * @return
	 */
	private HSSFCellStyle getStyle(HashMap styleMap,String type)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		a_style.setWrapText(true);//自动换行
		short border = (short) 1;
		short borderColor = HSSFColor.BLACK.index;
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
		short fillForegroundColor = HSSFColor.WHITE.index;
		String fontName = "宋体";//TODO 设置字体20161103 ResourceFactory.getProperty("gz.gz_acounting.m.font");
		int fontSize = 0;//字体大小
		boolean fontBoldWeight = false;
		boolean isFontBold = false;//是否加粗
		if(styleMap != null){//设置了单元格样式
			if(styleMap.get("border") != null)
				border = (Short) styleMap.get("border");//值为-1时则改样式不设置
			if(styleMap.get("borderColor") != null)
				borderColor = (Short)styleMap.get("borderColor");
			if(styleMap.get("align") != null)
				align = (HorizontalAlignment)styleMap.get("align");
			if(styleMap.get("fillForegroundColor") != null)
				fillForegroundColor = (Short)styleMap.get("fillForegroundColor");
			if(styleMap.get("fillPattern") != null)
				fillPattern = (FillPatternType)styleMap.get("fillPattern");
			if(styleMap.get("fontName") != null)
				fontName = (String)styleMap.get("fontName");
			if(styleMap.get("fontSize") != null)
				fontSize = (Integer)styleMap.get("fontSize");
			if(styleMap.get("isFontBold") != null)
				isFontBold = (Boolean)styleMap.get("isFontBold");
		}else{//没有设置单元格样式
			if("head".equals(type)){//默认头部字体是加粗
				isFontBold = true;
			}
		}
		
		HSSFFont fonttitle = null;
		if("head".equals(type)){
			a_style.setFillPattern(fillPattern);
			a_style.setFillForegroundColor(fillForegroundColor);
			if(fontSize == 0)
				fontSize = 10;
			if(isFontBold)
				fontBoldWeight = true;
			//设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(fontBoldWeight);
			
			if(fontMap.get(fontKey.toString()) == null){
				fonttitle = fonts(fontName, fontSize,fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			}else fonttitle = fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		}else if("mergedCell".equals(type)){
			if(border !=-1){
				a_style.setFillPattern(fillPattern);
				a_style.setFillForegroundColor(fillForegroundColor);
			}
			if(fontSize == 0)
				fontSize = 15;
			if(isFontBold)
				fontBoldWeight = true;
			//设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if(fontMap.get(fontKey.toString()) == null){
				fonttitle = fonts(fontName, fontSize,fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			}else fonttitle = fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		}else{
			if(align == HorizontalAlignment.CENTER){
				align = HorizontalAlignment.LEFT;
			}
			if(fontSize == 0)
				fontSize = 10;
			if(isFontBold)
				fontBoldWeight = true;
			//设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if(fontMap.get(fontKey.toString()) == null){
				fonttitle = fonts(fontName, fontSize,fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			}else fonttitle = fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		}
		
		if(border !=-1){
			a_style.setBorderBottom(BorderStyle.valueOf(border));
			a_style.setBottomBorderColor(borderColor);
			a_style.setBorderLeft(BorderStyle.valueOf(border));
			a_style.setLeftBorderColor(borderColor);
			a_style.setBorderRight(BorderStyle.valueOf(border));
			a_style.setRightBorderColor(borderColor);
			a_style.setBorderTop(BorderStyle.valueOf(border));
			a_style.setTopBorderColor(borderColor);
		}
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		a_style.setAlignment(align);
		a_style.setLocked(false);
		return a_style;
	}
	
	/**
	 * 设置excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	private HSSFFont fonts(String fonts, int size, boolean bolderWeight)
	{
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		font.setBold(bolderWeight);// 加粗
		return font;
	}
	/**
	 * 打分数据的查询sql
	 * @param planId
	 * @param objcetId
	 * @param b0110
	 * @param e0122
	 * @param e01a1
	 * @return
	 * @throws GeneralException
	 */
	private String getSql(String planId,String objcetId,String objectType) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		//考核主体分类列表
		List<Map<String, String>> mainBodyList = this.getMainBodySet(planId,objcetId);
		StringBuffer selectSql = new StringBuffer();
		StringBuffer leftjoinSql = new StringBuffer();
		int i = 0;
		for(Map<String, String> mainBody : mainBodyList){
			String body_id = mainBody.get("body_id");
			List<Map<String, String>> bodyList = this.getMainBody(body_id, planId,objcetId);
			//考核主体列表
			if(bodyList.size()>0){
				for(Map<String, String> bodyMap: bodyList){
					String mainbody_id = bodyMap.get("mainbody_id");
					
					//查询考核主体的评分和评分说明
					selectSql.append(",t"+i+".score as \""+mainbody_id+"\"");
					selectSql.append(",t"+i+".reasons  as \"reas_"+mainbody_id+"\" ");
					
					leftjoinSql.append("left join (select reasons,score,p0400 from per_target_evaluation");
					leftjoinSql.append(" WHERE object_id='"+objcetId+"' AND mainbody_id='"+mainbody_id+"' and plan_id='"+planId+"') t"+i+" ");
					
					leftjoinSql.append(" on t_before.p0400=t"+i+".p0400 ");

					i++;
				}
			}
		}
		// before 拼接考核目标卡信息
		
		this.userView.getUnitIdByBusi("5");
		sql.append("select t_before.*");
		sql.append(selectSql);//评分 和评分说明
		sql.append(" from (select p04.*,pplan.method,template.Itemdesc project_name");
		sql.append(" from P04 p04,per_plan pplan,per_template_item template,per_object obj");
		sql.append(" where pplan.plan_id ='"+planId+"' and pplan.plan_id=p04.plan_id and pplan.method=2");
		sql.append(" and obj.plan_id=p04.plan_id");
		sql.append(" and template.item_id = p04.item_id");
		if("2".equals(objectType)){//个人
			if(StringUtils.isNotBlank(objcetId))
			sql.append(" and p04.a0100= '"+objcetId+"'");
		}else{//团队
			if(StringUtils.isNotEmpty(objcetId))
				sql.append(" and (p04.b0110 = '"+objcetId+"' or p04.e0122='"+objcetId+"')");
		}
		sql.append(" and obj.object_id='"+objcetId+"') t_before ");
		sql.append(leftjoinSql);
		
		return sql.toString();
	}
	/**
	 * 获得考核对象类型（团队|个人）
	 * @param planId
	 * @return
	 * @throws GeneralException
	 */
	private int getObjType(String planId) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		int objtype = 2;//默认为个人
		try {
			String sql = "select object_type from per_plan where plan_id="+planId;
			rs = dao.search(sql);
			if(rs.next())
				objtype = rs.getInt("object_type");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return objtype;
	}
	
	/**
	 * 输出Excel
	 * @param filename
	 * @return
	 * @throws GeneralException 
	 */
	public void out2file(String filename) throws GeneralException{
		FileOutputStream fileOut = null;
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + filename;
			fileOut = new FileOutputStream(url);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(wb);
		}
	}
	
	/**
	 * 获得考核主体分类名称集合
	 * @param planId
	 * @return
	 * @throws GeneralException 
	 */
	private List<Map<String,String>> getMainBodySet(String planId,String objectId) throws GeneralException {
		ContentDAO dao  = new ContentDAO(this.conn);
		RowSet rs = null;
		List<Map<String,String>> beanList = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try {
			String sql = "select pm.name,pm.body_id from per_mainbodyset pm,per_plan_body ppd";
			sql+=" where ppd.body_id=pm.body_id and ppd.plan_id='"+planId+"' and status = 1 and";
            sql+=" exists (select 1 from per_mainbody p where p.status=2 and p.plan_id='"+planId+"' and p.body_id=pm.body_id and p.object_id='"+objectId+"')";
			sql+=" order by pm.seq ";
			rs = dao.search(sql);
			while (rs.next()) {
				map = new HashMap<String,String>();
				map.put("name", rs.getString("name"));
				map.put("body_id", String.valueOf(rs.getInt("body_id")));
				beanList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return beanList;
	}
	/**
	 * 获得主体分类下的考核主体
	 * @param body_id
	 * @param planId
	 * @return
	 * @throws GeneralException
	 */
	private List<Map<String,String>> getMainBody(String body_id,String planId,String objectId) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{	
			String sql = "select a0101,mainbody_id from per_mainbody where plan_id='"+planId+
					"' and body_id="+body_id
					+" and object_id='"+objectId+"' and status=2";
			rs = dao.search(sql);
			while(rs.next()){
				map = new HashMap<String,String>();
				map.put("a0101", rs.getString("a0101"));
				map.put("mainbody_id", rs.getString("mainbody_id"));
				list.add(map);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 设置合并后的单元格边框
	 * @param sheet
	 * @param region
	 * @param cs
	 */
	 private void setRegionStyle(HSSFSheet sheet, CellRangeAddress region, HSSFCellStyle cs) {
         for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
             HSSFRow row = sheet.getRow(i);
             if(row==null){//如果已经创建了行则不在创建
                 row = sheet.createRow(i);
             }
             for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                 HSSFCell cell = row.getCell((short) j);
                 if(cell==null){
                     cell = row.createCell(j);
                 }
                 cell.setCellStyle(cs);
             }
         }
	 }
}

