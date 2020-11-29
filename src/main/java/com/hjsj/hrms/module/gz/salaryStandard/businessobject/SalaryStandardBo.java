package com.hjsj.hrms.module.gz.salaryStandard.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SalaryStandardBo {
	private Connection conn = null;

	private String pkgid;//历史沿革id

	private String standardID;//薪资标准id
	private UserView userview;
	private String isError; //错误信息 无错误为空
	private HSSFPatriarch patr=null;
	private HSSFSheet sheet = null;
	private ArrayList<ArrayList<Object>> errCellItems=null;
	
	
	/**
	 * 写入错误单元格数据
	 * @param cell 错误单元格 
	 * @param errStr 错误内容
	 */
	private void setErrCellItems(Cell cell,String errStr){
		if(errCellItems==null)
			errCellItems=new ArrayList<ArrayList<Object>>();
		
		ArrayList<Object> arr=new ArrayList<Object>();
		arr.add(cell);
		arr.add(errStr);
		errCellItems.add(arr);
	}

	public SalaryStandardBo(String pkgid,String standardID,UserView userview,Connection conn){
		this.pkgid=pkgid;
		this.standardID=standardID;
		this.userview=userview;
		this.conn=conn;
		this.isError="";
	}
	
	/**
	 * 获取薪资标准结构
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getStandHistory() throws GeneralException{
		LazyDynaBean bean=new LazyDynaBean();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String strSql="select id,pkg_id,createorg,name,"+Sql_switcher.isnull("hfactor", "' '")+" as hfactor,"+Sql_switcher.isnull("s_hfactor", "' '")+" as s_hfactor,"
					+ "b0110,hcontent,"+Sql_switcher.isnull("s_vfactor", "' '")+" as s_vfactor,"+Sql_switcher.isnull("vfactor", "' '")+" as vfactor,vcontent,item,createtime "
					+ "from gz_stand_history where id=? and pkg_id=?";
			
			ArrayList <String> list=new ArrayList<String>();
			list.add(this.standardID);
			list.add(this.pkgid);
			RowSet rs=dao.search(strSql,list);
			if (rs.next()) {
				bean.set("hfactor", rs.getString("hfactor"));//横向指标
				bean.set("s_hfactor", rs.getString("s_hfactor"));//S横向指标
				bean.set("vfactor", rs.getString("vfactor"));//纵向指标
				bean.set("s_vfactor", rs.getString("s_vfactor"));//S纵向指标
				bean.set("item", rs.getString("item"));//结果指标
				bean.set("name", rs.getString("name"));//标准名称
				bean.set("hcontent", rs.getString("hcontent"));//横向指标项列表
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return bean;
	}
	
	/**
	 * 获取导入数据 并进行数据校验。文件错误 直接返回字符串，文件内数据错误 返回空字符串和null dataList
	 * @param file
	 * @param designBean
	 * @param dataList
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getInputDataList(File file,LazyDynaBean designBean) throws GeneralException{
		ArrayList dataList=new ArrayList();
		HSSFWorkbook wb = null;
		InputStream stream = null;
		try{
			
			int sCol=0,sRow=0,eCol=0,eRow=0;
			int colCount=0;//纵向栏目数量
			int rowCount=0;//横线栏目数量
			sCol=1;sRow=2;//下标从0开始 设定其实单元格为左上第一个空格
			
			
			try {
				stream = new FileInputStream(file);
				wb = new HSSFWorkbook(stream);
				this.sheet = wb.getSheetAt(0);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));
			} finally {
				PubFunc.closeIoResource(stream);
			}
			
			String sheetName=this.sheet.getSheetName();
			
			if(!designBean.get("name").toString().equalsIgnoreCase(sheetName)){
				this.isError="导入文件和当前标准表名称不匹配！";
				return dataList;
			}
			
			
			
			
			
			
			Row row = sheet.getRow(2);
			if (row == null)
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));
			
			
			int cols = row.getPhysicalNumberOfCells();// 获取不为空的列个数。
			int rows = sheet.getPhysicalNumberOfRows();// 是获取不为空的行个数。
			eCol=cols;
			
			eRow=rows;
			if(!StringUtils.isBlank((String)designBean.get("hfactor")))
				rowCount++;
			if(!StringUtils.isBlank((String)designBean.get("s_hfactor")))
				rowCount++;
			
			
			if(!StringUtils.isBlank((String)designBean.get("vfactor")))
				colCount++;
			if(!StringUtils.isBlank((String)designBean.get("s_vfactor")))
				colCount++;
			
			String item=(String)designBean.get("item");
			FieldItem fieldItem=DataDictionary.getFieldItem(item);
			
			dataList=getDataList(sCol, sRow, eCol, eRow, colCount, rowCount, sheet,fieldItem,designBean);
			
			if(this.errCellItems!=null){//如果存在错误数据
				this.setErrorText(file, wb);
				this.isError="1";
				return dataList;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(stream);
			PubFunc.closeIoResource(wb);
		}
		return dataList;
	}
	/**
	 * 获取数据 并检查数据正确性
	 * @param sCol 开始列
	 * @param sRow	开始行
	 * @param eCol 结束列
	 * @param eRow 结束行
	 * @param colCount 纵排标题数量
	 * @param rowCount 横排标题数量
	 * @param sheet
	 * @param fieldItem
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList getDataList(int sCol,int sRow,int eCol,int eRow,int colCount ,int rowCount,Sheet sheet,FieldItem fieldItem,LazyDynaBean designBean) throws GeneralException{
		ArrayList dataList=new ArrayList();
		
		try{
			
			HashMap <Integer ,String> HfactorMap=new HashMap<Integer, String>();
			HashMap <Integer ,String> s_HfactorMap=new HashMap<Integer, String>();
			
			String Vfactor="",s_Vfactor="";
			Row row = sheet.getRow(2);
			Cell cell = null;
			String tempComment="",colComment="";
			//获取横排标题批注 
			if(rowCount!=0){
				for(int i=sCol+(colCount==0?1:colCount);i<=eCol;i++){//获取第一标题行数据批注
					cell=row.getCell(i);
					tempComment=this.getComment(cell);
					if(!StringUtils.isBlank(tempComment))
						colComment=tempComment;

					if(StringUtils.isBlank(colComment)||(StringUtils.isBlank(tempComment)&&!StringUtils.isBlank(cell.getStringCellValue())))//未设置批注
						setErrCellItems(cell,"未设置批注");
					if(StringUtils.isBlank((String)designBean.get("hfactor"))){
						s_HfactorMap.put(i, colComment);
					}else
						HfactorMap.put(i, colComment);
					
				}
			}
			if(rowCount==2){//获取第二标题行数据批注
				row = sheet.getRow(3);
				for(int i=sCol+(colCount==0?1:colCount);i<=eCol;i++){
					cell=row.getCell(i);
					colComment=this.getComment(cell);

					s_HfactorMap.put(i, colComment);
					
				}
				
				
			}
			
			//获取codeMap 用于转换数据
			HashMap dataMap=new HashMap<String, Object>();
			HashMap codeColMap=new HashMap<String, String>();
			if(!"0".equals(fieldItem.getCodesetid())){
				ArrayList<CodeItem> codeItems=AdminCode.getCodeItemList(fieldItem.getCodesetid());
				
				for(int i=0;i<codeItems.size();i++){
					CodeItem code=codeItems.get(i);
					codeColMap.put(code.getCodename(),code.getCcodeitem());
				}
			}
			
			String tempVfactor="";
			for(int j=sRow+(rowCount==0?1:rowCount);j<=eRow;j++){//获取数据区域
				
				row = sheet.getRow(j);
				
				for(int i=sCol;i<=eCol;i++){
					
					cell=row.getCell(i);
					Object value;
					if(colCount==0&&i==sCol)
						continue;
					else if(colCount>=1&&i==sCol&&!StringUtils.isBlank((String)designBean.get("vfactor"))){
						tempVfactor=this.getComment(cell);
						if(!StringUtils.isBlank(tempVfactor))
							Vfactor=tempVfactor;
						
						if (StringUtils.isBlank(Vfactor))//未设置批注
							setErrCellItems(cell,"未设置批注");
						continue;
						
					}else if(colCount==1&&i==sCol){
						s_Vfactor=this.getComment(cell);
						if (StringUtils.isBlank(s_Vfactor))//未设置批注
							setErrCellItems(cell,"未设置批注");
						continue;
					}
					else if(colCount==2&&i==sCol+1){
						s_Vfactor=this.getComment(cell);
						// 暂时无法判断单元格是否为合并。若第二个单元格没有备注且没有内容 则需第一个单元格备注信息
						if (StringUtils.isBlank(s_Vfactor)&&StringUtils.isBlank(cell.getStringCellValue())&&!StringUtils.isBlank(Vfactor))
							s_Vfactor=s_Vfactor;
						else if (StringUtils.isBlank(s_Vfactor))//未设置批注
							setErrCellItems(cell,"未设置批注");
						continue;
						
					}
					dataMap=new HashMap<String, Object>();
					
					
					value=this.getCellValue(cell, fieldItem, codeColMap);
					
					if(HfactorMap.size()!=0)
						dataMap.put("Hfactor", HfactorMap.get(i));
					else
						dataMap.put("Hfactor", "");
					
					if(s_HfactorMap.size()!=0)
						dataMap.put("s_Hfactor", s_HfactorMap.get(i));
					else
						dataMap.put("s_Hfactor", "");
					
					dataMap.put("Vfactor", Vfactor);
					dataMap.put("s_Vfactor", s_Vfactor);
					
					dataMap.put("value", value);
					
					dataList.add(dataMap);
					
				}//for(int i=sCol;i<=eCol;i++)
			}//for(int j=sRow+(colCount==0?1:colCount);j<=eRow;j++)
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataList;
	}
	
	/**
	 * 更新数据
	 * @param designBean 表结构bean
	 * @param pkgid 历史沿革号
	 * @param standardID 标准表id
	 * @param dataList 数据集
	 * @return
	 * @throws GeneralException
	 */
	public boolean proceedUpdateStandard(LazyDynaBean designBean,String pkgid,String standardID,ArrayList<HashMap> dataList) throws GeneralException{
		StringBuilder strSql=new StringBuilder();
		
		try{
			RowSet rs=null;
			ContentDAO dao = new ContentDAO(this.conn);
			
			String status="";
			rs=dao.search("select status from gz_stand_pkg where pkg_id="+pkgid);
			if(rs.next()){
				status=rs.getString("status");
			}else
				return false;
				
			ArrayList<String> headItem=new ArrayList<String>();
			
			if(!StringUtils.isBlank((String)designBean.get("hfactor"))){
				strSql.append(" and "+Sql_switcher.isnull("hvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
				headItem.add("Hfactor");
			}
			if(!StringUtils.isBlank((String)designBean.get("s_hfactor"))){
				strSql.append(" and "+Sql_switcher.isnull("S_hvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
				headItem.add("s_Hfactor");
			}
			if(!StringUtils.isBlank((String)designBean.get("vfactor"))){
				strSql.append(" and "+Sql_switcher.isnull("Vvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
				headItem.add("Vfactor");
			}
			if(!StringUtils.isBlank((String)designBean.get("s_vfactor"))){
				strSql.append(" and "+Sql_switcher.isnull("S_vvalue", "' '")+"="+Sql_switcher.isnull("?", "' '"));
				headItem.add("s_Vfactor");
			}
			
			ArrayList<ArrayList> updateDataList=new ArrayList<ArrayList>();
			ArrayList list=new ArrayList();
			for(HashMap dataMap:dataList){
				list=new ArrayList();
				String value=dataMap.get("value")==null?"":dataMap.get("value").toString();
				
				list.add(value);
				list.add(pkgid);
				list.add(standardID);
				
				
				for(String str:headItem){
					list.add(dataMap.get(str));
				}
				updateDataList.add(list);
			}
			
			
			dao.batchUpdate("update gz_item_history set standard=? where pkg_id=? and  ID =? "+strSql.toString(), updateDataList);
			
			if("1".equalsIgnoreCase(status)){//若当前沿革为启用
				for(ArrayList temp:updateDataList){
					temp.remove(1);
				}
				
				dao.batchUpdate("update gz_item set standard=? where ID =? "+strSql.toString(), updateDataList);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return true;
	}
	
	
	
	/**
	 * 获取批注
	 * @param cell
	 * @return
	 * @throws GeneralException
	 */
	private String getComment(Cell cell) throws GeneralException {
		try {
			String colComment = "";
			// 如果excel的列没有注释说明，不能导入
			if (cell.getCellComment() == null)
				colComment = "";
			else
				colComment = cell.getCellComment().getString().getString()
						.trim();
			return colComment;

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 获取单元格内容
	 * @param cell
	 * @param fieldItemName
	 * @param codeColMap
	 * @return
	 * @throws GeneralException
	 */
	private Object getCellValue(Cell cell,FieldItem fieldItem,HashMap<String,String> codeColMap) throws GeneralException{
		Object value = null;
		try {

			String itemtype = fieldItem.getItemtype(); // 值类型，是数值型(N)、字符型(S)、日期型(D)、备注型(M)
			String codesetid = fieldItem.getCodesetid(); // 代码类id
			int decwidth = fieldItem.getDecimalwidth(); // 小数点位数
			int width=fieldItem.getItemlength();
			width+=decwidth;
			
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_FORMULA:
					break;
				case Cell.CELL_TYPE_NUMERIC:
					double y = cell.getNumericCellValue();
					if(String.valueOf(y).indexOf('.')>0&&decwidth==0)
						width++;
					value = this.getNumValue(decwidth, y,width+1,cell);
					break;
				case Cell.CELL_TYPE_STRING:
					value = cell.getRichStringCellValue().toString();
					value = this.getStrValue(value.toString(), codesetid, codeColMap, itemtype, decwidth,cell,width);
					break;
				case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
					break;
				default:
					value = null;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return value;
	}
	
	/**
	 * 当单元格是数值时
	 * @param decwidth
	 * @param y
	 * @return
	 * @throws GeneralException
	 */
	private Double getNumValue(int decwidth,double y,int width,Cell cell) throws GeneralException{
		try {
			String value = Double.toString(y);
			if (value.indexOf("E") > -1) {
				String x1 = value.substring(0, value
						.indexOf("E"));
				String y1 = value.substring(value
						.indexOf("E") + 1);

				value = (new BigDecimal(Math.pow(10,
						Integer.parseInt(y1.trim())))
						.multiply(new BigDecimal(x1)))
						.toString();
			}
			if(value.length()>width){
				this.setErrCellItems(cell, "数字长度过长");
				return 0.0;
			}
			
			value = PubFunc.round(value, decwidth);
			return new Double((PubFunc.round(value,
					decwidth)));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 当单元格是字符时
	 * @param value
	 * @param codesetid
	 * @param codeColMap
	 * @param itemtype
	 * @param decwidth
	 * @return
	 * @throws GeneralException
	 */
	private Object getStrValue(String value,String codesetid,HashMap codeColMap,String itemtype,int decwidth,Cell cell,int width){
		Object value1 = null;
		try {
			
			if(StringUtils.isNotBlank(value)){
				if (!"0".equals(codesetid) && StringUtils.isNotBlank(codesetid))// 代码类id
				{
					
					
//					if (codesetid.equalsIgnoreCase("UM") || codesetid.equalsIgnoreCase("UN") || codesetid.equalsIgnoreCase("@K")) {
//						value = value.split(":")[0];
//					} else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null)
//						value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
//					else
//						value = null;

					 if (codeColMap.get(value.trim()) != null)
						value = (String) codeColMap.get(value.trim());
					else if(value!=null&& !StringUtils.isBlank(value))
						setErrCellItems(cell,"系统中不存在此代码");
					else
						value = null;
				}
				
				if ("N".equals(itemtype)) {
					try{
						value = PubFunc.round(value, decwidth);
					}catch(Exception e){
						setErrCellItems(cell,"应写入数字格式");
						return "";
					}
					if(value.length()>width){
						setErrCellItems(cell,"数字长度过长");
						return "";
					}
						
					
					if (decwidth == 0)
						value1 = new Integer(value);
					else
						value1 = new Double(value);
				} else if ("D".equals(itemtype) && !"0".equals(value)) {
					java.sql.Date d_t = null;
					value = value.replaceAll("\\.", "-");
					java.util.Date src_d_t = DateUtils.getDate(value, "yyyy-MM-dd");
					if (src_d_t != null)
						d_t = new java.sql.Date(src_d_t.getTime());
					value1 = d_t;
				} else
					value1 = value;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			setErrCellItems(cell,e.getMessage());
		}
		return value1;
	}
	
	
	/**
	 * 输出错误信息excel
	 * @param file
	 * @param wb
	 */
	private void setErrorText(File file,HSSFWorkbook wb){
		
		FileOutputStream out = null;
		try{

				patr = sheet.getDrawingPatriarch();
			for(ArrayList<Object> list:this.errCellItems){
				Cell cell=(Cell)list.get(0);
				
				
				String errorText=(String)list.get(1);
				 if(StringUtils.isNotBlank(errorText)){//当注释不为空时
					 	HSSFComment comm = patr.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 7,5));
						comm.setString(new HSSFRichTextString(errorText));
						cell.setCellComment(comm);
				 }
				 HSSFCellStyle  style = wb.createCellStyle();
				 style.setFillForegroundColor(HSSFColor.YELLOW.index);
				 style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				 style.setBorderBottom(BorderStyle.THIN); //下边框    
				 style.setBorderLeft(BorderStyle.THIN);//左边框    
				 style.setBorderTop(BorderStyle.THIN);//上边框    
				 style.setBorderRight(BorderStyle.THIN);//右边框    
				 cell.setCellStyle(style);
				 
			}
			
			out = new FileOutputStream(file);
			out.flush();
            wb.write(out);
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(out);
		}
		
	}
	
	
	
	
	
	
	
	
	
	public String getIsError() {
		return isError;
	}
	public void setIsError(String isError) {
		this.isError = isError;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getPkgid() {
		return pkgid;
	}
	public void setPkgid(String pkgid) {
		this.pkgid = pkgid;
	}
	public String getStandardID() {
		return standardID;
	}
	public void setStandardID(String standardID) {
		this.standardID = standardID;
	}

}
