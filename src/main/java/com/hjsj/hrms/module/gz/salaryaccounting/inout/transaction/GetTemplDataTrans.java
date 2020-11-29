package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: GetTemplDataTrans
 * @Description: TODO(薪资导入模板数据)
 * @author lis
 * @date 2015-7-29 下午05:28:59
 */

public class GetTemplDataTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {

		String gz_module = (String) this.getFormHM().get("gz_module");// 是薪资还是保险
		gz_module = StringUtils.isBlank(gz_module)?"0":gz_module;
		String salaryid = (String) this.getFormHM().get("salaryid");// 薪资类别id
		salaryid =PubFunc.decrypt(SafeCode.decode(salaryid)); //解密

		String flag = (String)  this.getFormHM().get("flag");//"sp"是薪资审批
		// vfs改造
		String fileid = (String)this.getFormHM().get("fileid");
		InputStream input = null;
		try {
			input = VfsService.getFile(fileid);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		/** 薪资类别 */
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid), this.userView);
		SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
		
		String tablename = null;
		if ("sp".equalsIgnoreCase(flag))
			tablename = "salaryhistory";
		else{
			tablename = gzbo.getGz_tablename();
		}

		HashMap<String, String> fieldMap = new HashMap<String, String>();// 所有薪资项目id的集合
		ArrayList<FieldItem> fieldlist = gzbo.getSalaryItemList(null, salaryid, 2);//获得当前薪资类别的薪资项目
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem field = (FieldItem) fieldlist.get(i);
			String name = field.getItemid().toUpperCase();
			fieldMap.put(name, "1");
		}

		//更新数据库sql
		StringBuffer updateSql = new StringBuffer("update " + tablename + " set ");

		Workbook wb = null;
		Sheet sheet = null;
		InputStream stream = null;
		try {
			wb = WorkbookFactory.create(input);
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(stream);
		}
		try {
			// 是否是提成工资
			String royalty_valid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES, "valid");
			// 提成工资关联指标
			String royalty_relation_fields = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES, "relation_fields");

			ContentDAO dao = new ContentDAO(this.frameconn);
			
			Row row = sheet.getRow(0);
			if (row == null)
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));

			int cols = row.getPhysicalNumberOfCells();// 获取不为空的列个数。

			if (cols < 1 || sheet.getPhysicalNumberOfRows() < 1)
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));

			String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
			accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
			String accountingcount = (String)this.getFormHM().get("count"); //发放次数
			accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
			
			/** start 根据唯一性指标判断excel是否可以导入 lis **/

			HashMap parameterMap = new HashMap();
			parameterMap.put("salaryid", salaryid);
			parameterMap.put("tablename", tablename);

			/** 校验第一行需要的参数   start  */
			parameterMap.put("gz_module", gz_module);
		    parameterMap.put("accountingdate", accountingdate);
		    parameterMap.put("accountingcount", accountingcount);
			parameterMap.put("updateSql", updateSql);
			parameterMap.put("salaryid", salaryid);
			parameterMap.put("cols", cols);
			parameterMap.put("royalty_valid", royalty_valid);
			parameterMap.put("royalty_relation_fields", royalty_relation_fields);
			parameterMap.put("fieldMap", fieldMap);
			/** 校验第一行需要的参数   end  */
			
			//校验excel第一行
			this.validateFirstRow(parameterMap, row, dao, inOutBo, gzbo, flag);
			
			/** 校验第一行后获得的参数   start  */
			updateSql = (StringBuffer)parameterMap.get("updateSql");
			// 唯一指标
			String onlyname = (String)parameterMap.get("onlyname");
			//更新where条件
			String updateWhere = (String)parameterMap.get("updateWhere");
			//唯一指标所在的列
			int onlyColIndex = (Integer)parameterMap.get("onlyColIndex");
//			// excel中提成工资关联指标和其对应的列号
//			HashMap<String, String> relationFieldMap = (HashMap<String, String>)parameterMap.get("relationFieldMap");
			// 可以更新的列
			ArrayList<FieldItem> headList = (ArrayList)parameterMap.get("headList");
//			// excel中可以更新的列所在第几行
//			HashMap<Short, String> updateColMap = (HashMap<Short, String>)parameterMap.get("updateColMap");
			// 将要更新的字段数目
			int updateFidsCount = (Integer)parameterMap.get("updateFidsCount");
//			// 有效列数
//			int maxColCount = (Integer)parameterMap.get("maxColCount");
//			// excel中姓名所在第几列
//			int a0101ColIndex = (Integer)parameterMap.get("a0101ColIndex");
//			//代码类数据集合
//			HashMap<String,String> codeColMap = (HashMap<String,String>)parameterMap.get("codeColMap");



			
			/** 校验第一行后获得的参数   end  */
			
			if (updateFidsCount == 0)// excel中可更新得字段数
			{
				this.getFormHM().put("okCount", "0");
				//excel中没有可更新的字段
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.no_update_field")));
			}
			
			boolean isBJYD = false;
			// 北京移动
			if (SystemConfig.getPropertyValue("clientName") != null && "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				isBJYD = true;
			
			/** start 为UniqueIndexValMap（数据库中唯一性标识数据）填充数据 **/
			
			
			parameterMap.put("isBJYD", isBJYD);
			parameterMap.put("tablename", tablename);
			SalaryAccountBo salaryAccountBo = new SalaryAccountBo(this.getFrameconn(), this.userView,Integer.valueOf(salaryid));
			
			
			String gzNotControlPrivIds=SystemConfig.getPropertyValue("GzNotControlPrivIds");
			String deptid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
			deptid = deptid != null ? deptid : ""; 
			String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
			orgid = orgid != null ? orgid : "";
			String privSql="";//权限范围语句
			String manager=gzbo.getManager();

			
			if((StringUtils.isNotBlank(deptid)||StringUtils.isNotBlank(orgid))&&gzNotControlPrivIds.indexOf(salaryid)!=-1&&manager!=null&&manager.length()>0
					&&!manager.equalsIgnoreCase(this.userView.getUserName())&&!this.userView.isSuper_admin()){//设置了归属单位 归属部门且启用了数据上报不使用管理权限功能 且共享非管理员
					//String b_units=this.userView.getUnitIdByBusi("1");// 1:工资发放  2:工资总额  3:所得税
					String privsql = salaryAccountBo.getSalaryTemplateBo().getWhlByUnits(tablename,true);
					privSql+=privsql;
			}
			
			
			
			//得到相关参数
			this.getParamter(parameterMap, dao,flag,inOutBo,gzbo,privSql);
			
			//数据库中提成工资关联指标
			//ArrayList<FieldItem> relationFieldList = (ArrayList<FieldItem>)parameterMap.get("relationFieldList");
			//数据库中正确的唯一性指标(主键标识串)值参照
			//HashMap<String, String> UniqueIndexValMap = (HashMap<String, String>)parameterMap.get("UniqueIndexValMap");
			// 每个人员的最大次数
			HashMap<String, String> maxZ1ByPersonMap = (HashMap<String, String>)parameterMap.get("maxZ1ByPersonMap");
			// 唯一性指标无值的姓名
			//HashMap<String, String> a0101ValueMap = (HashMap<String, String>)parameterMap.get("a0101ValueMap");
			// 唯一性指标有值的姓名
			//HashMap<String, String> a0101ValueMap2 = (HashMap<String, String>)parameterMap.get("a0101ValueMap2");
			
			/** 为UniqueIndexValMap(唯一性指标值集合)填充数据 end **/

			/** end 对第一行进行分析 **/

			/** start 对excel中要导入的数据遍历 **/

			//对excel中数据遍历，组装数据
			this.getRowsParamter(parameterMap, wb, dao,gzbo,salaryAccountBo,flag,privSql);
			
			// excel中第j行的唯一性指标值（唯一性指标值，行数j）
			//HashMap<String, String> a0100sMap = (HashMap<String, String>)parameterMap.get("a0100sMap");
			// 记录唯一性指标重复行
			ArrayList<String> onlyValueRepeat = (ArrayList<String>)parameterMap.get("onlyValueRepeat");
			// 有效行数
			int totalRowCount = (Integer)parameterMap.get("totalRowCount");
			//从excel中得到的更新数据
			ArrayList list2 = (ArrayList)parameterMap.get("list2");
			// 新增记录
			ArrayList newRecordList = (ArrayList)parameterMap.get("newRecordList");
			ArrayList nullOnlyValueList = (ArrayList)parameterMap.get("nullOnlyValueList");
			//唯一性指标值 （指标值,行号）
			//HashMap<String, String> onlyValueMap = (HashMap<String, String>)parameterMap.get("onlyValueMap");
			String update_item_str=(String)parameterMap.get("update_item_str");
			
			/** end 对excel中要导入的数据遍历 **/
			
			if (!"1".equals(royalty_valid))// 不是提成工资
			{
				try {
					if(list2.size() > 0)
						dao.batchUpdate(updateSql.toString(), list2);// 更新数据库数据
					if(!"sp".equalsIgnoreCase(flag)){
						ArrayList onlyRepeatList = (ArrayList)parameterMap.get("onlyRepeatList");
						String repeatsql = (String)parameterMap.get("repeatsql");
						ArrayList wyNewRecordList = (ArrayList)parameterMap.get("wyNewRecordList");
						HashMap allOnlyFieldInExcel = (HashMap)parameterMap.get("allOnlyFieldInExcel");//存放excel中所有的人
						if(onlyRepeatList.size()>0)//更新excel中与薪资表中对应的的重复数据
							dao.batchUpdate(repeatsql.toString(), onlyRepeatList);
						
						if(wyNewRecordList.size()>0)  //新曾数据集合
						{
							HashMap mapData=insertTempTable(dao,repeatsql.toString(), onlyname, wyNewRecordList, salaryid, maxZ1ByPersonMap,gzbo,update_item_str,parameterMap);
							int  inserNum = (Integer)mapData.get("num");
							HashMap errorOnlyFieldMap = (HashMap)mapData.get("errorOnlyFieldMap");
							
							//取得不在权限范围内的人集合
							Iterator iter2 = allOnlyFieldInExcel.entrySet().iterator();
							while (iter2.hasNext()) {
								Map.Entry entry = (Map.Entry) iter2.next();
								String key = (String)entry.getKey();//行数
								String val = (String)entry.getValue();//唯一性指标
								if(errorOnlyFieldMap.containsKey(val))
									onlyValueRepeat.add(key);
							}
						}
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					String message = ee.getMessage();
					//修改的数据超出最大长度限制
					if (message.indexOf("将截断字符串或二进制数据") != -1)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.data_beyond_maxlength")));
					if (message.indexOf("data is not corrected") != -1)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.data_beyond_maxlength")));
					if (message.indexOf("转换为数据类型") != -1)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.data_beyond_maxlength")));
				}
			}
			if (newRecordList.size() > 0) // 是提成工资同时在数据库中不存在（excel中的这个人在数据库中不存在），新建数据
			{
				int inserNum = insertNewRecords(dao, Integer.valueOf(salaryid),
						headList, newRecordList, onlyColIndex, onlyname,
						maxZ1ByPersonMap, gzbo, parameterMap);
				if (newRecordList.size() > inserNum)
					totalRowCount = totalRowCount - (newRecordList.size() - inserNum);
			}

			// 是北京移动
			if (onlyname.length() > 0 && isBJYD) {
				if (nullOnlyValueList.size() > 0) {
					String tempSql = updateSql.toString();
					tempSql = tempSql.replaceAll(onlyname + "=\\?"," a0101=? and (" + onlyname + " is null or " + onlyname + "='' )");
					dao.batchUpdate(tempSql, nullOnlyValueList);

				}
			}

			 //如果是审批模块的导入，需同步临时表数据。
			if ("sp".equalsIgnoreCase(flag))  
			{
				if(update_item_str.length()>0)
				{
					inOutBo.synTempData(updateWhere,update_item_str,salaryid);
				}
			}
			
			//得到薪资临时表中未更新的数据
			this.getRepeatValues(parameterMap, gzbo,inOutBo, dao,flag);
			onlyValueRepeat = (ArrayList<String>)parameterMap.get("onlyValueRepeat");
			
			int errorCount = onlyValueRepeat.size();// 需要提示出的有问题数据
			
			int okCount = totalRowCount - errorCount;// 成功导入的条数
			
			this.getFormHM().put("okCount", okCount + "");
			
			String errorFileName = "";
			if (errorCount > 0) {
				// 生成提示excel
				errorFileName = this.generateErrorFile(onlyValueRepeat, wb, null, okCount);
				errorFileName = SafeCode.encode(PubFunc.encrypt(errorFileName));
			}
			this.getFormHM().put("errorFileName", errorFileName);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(wb);
		}
	}

	/**
	 * @Title: getRepeatValues 
	 * @Description: TODO(得到薪资临时表中未更新的数据，在excel表格中存在，作为错误数据导出) 
	 * @param parameterMap 参数map
	 * @param gzbo 
	 * @param dao
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-13 下午01:39:48
	 */
	private void getRepeatValues(HashMap parameterMap,SalaryTemplateBo gzbo,SalaryInOutBo inOutBo,ContentDAO dao,String flag) throws GeneralException{
		try {
			String tablename = (String)parameterMap.get("tablename");
			String updateWhere = (String)parameterMap.get("updateWhere");
			String onlyname = (String)parameterMap.get("onlyname");
			HashMap<String, String> a0100sMap = (HashMap<String, String>)parameterMap.get("a0100sMap");
			ArrayList<String> onlyValueRepeat = (ArrayList<String>)parameterMap.get("onlyValueRepeat");
			String salaryid = (String)parameterMap.get("salaryid");
			StringBuffer buf = new StringBuffer("select * from " + tablename + " where 1=1 ");
			if ("sp".equalsIgnoreCase(flag))
				buf.append(" and salaryid=" + salaryid);
			if (updateWhere.length() > 4) {
				if ("sp".equalsIgnoreCase(flag)){
					String gz_module = (String)parameterMap.get("gz_module");
					String accountingdate = (String)parameterMap.get("accountingdate");
					String accountingcount = (String)parameterMap.get("accountingcount");
					
					buf.append(" and  not (" + inOutBo.getSpUpdateWhere(null, null, gzbo,gz_module).substring(4) + ") ");
					if(StringUtils.isNotBlank(accountingdate) && StringUtils.isNotBlank(accountingcount))
						buf.append(" and a00z2="+Sql_switcher.dateValue(accountingdate)+" and a00z3="+accountingcount+"");
				}else{
					buf.append(" and  not (" + updateWhere.substring(4) + ") ");
				}
				this.frowset = dao.search(buf.toString());// 得到数据库中没有更新的数据
				// 不在更新范围内的人，但是在excel中存在，作为错误数据导出
				while (this.frowset.next()) {
					if (StringUtils.isBlank(onlyname)) {
						String nASE = this.frowset.getString("NBASE");
						String a0100 = this.frowset.getString("A0100");
						String a00Z0 = this.frowset.getDate("A00Z0").toString();
						String a00Z1 = this.frowset.getString("A00Z1");
						String onlyValue = nASE + "|" + a0100 + "|" + a00Z0 + "|"
								+ a00Z1;
						if (a0100sMap.get(onlyValue) != null)// //excel中唯一性指标值集合（系统设置唯一性指标）存在“主键标识串”
						{
							String rowIndex = (String) a0100sMap.get(onlyValue);
							onlyValueRepeat.add(rowIndex);
						}
					} else {
						String onlynamevalue = this.frowset.getString(onlyname);
						if (a0100sMap.get(onlynamevalue) != null) {
							String rowIndex = (String) a0100sMap.get(onlynamevalue);
							onlyValueRepeat.add(rowIndex);
						}
					}
				}
			}
			
			parameterMap.put("onlyValueRepeat", onlyValueRepeat);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @Title: validateFirstRow 
	 * @Description: TODO(验证excel第一行) 
	 * @param parameterMap
	 * @param row
	 * @param cols
	 * @param onlyname
	 * @param royalty_valid
	 * @param royalty_relation_fields
	 * @param fieldMap
	 * @param dao
	 * @return
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-12 下午05:50:25
	 */
	private HashMap validateFirstRow(HashMap parameterMap,Row row,ContentDAO dao,SalaryInOutBo inOutBo,SalaryTemplateBo gzbo,String flag) throws GeneralException{
		try {
			int onlyColIndex = 0;
			HashMap<String, String> relationFieldMap = new HashMap<String, String>();
			ArrayList headList = new ArrayList();
			HashMap<Short, String> updateColMap = new HashMap<Short, String>();
			StringBuffer updateSql = (StringBuffer)parameterMap.get("updateSql");
			
			StringBuffer repeatsql = new StringBuffer("");//计算后更新的sql zhaoxg add 2016-01-08
			int updateFidsCount = 0;
			int maxColCount = 0;
			int a0101ColIndex = 0;
			HashMap<String,String> codeColMap =  new HashMap<String, String>();
			String updateWhere = null;
			
			int cols = (Integer)parameterMap.get("cols");
			String onlyname = "";
			String royalty_valid = (String)parameterMap.get("royalty_valid");
			String royalty_relation_fields = (String)parameterMap.get("royalty_relation_fields");
			HashMap<String, String> fieldMap = (HashMap<String, String>)parameterMap.get("fieldMap");
			String salaryid = (String)parameterMap.get("salaryid");
			
			StringBuffer update_item_str=new StringBuffer("");
			StringBuffer codeBuf = new StringBuffer();
			ArrayList<String> codeList=new ArrayList<String>();
			Cell cell = row.getCell(0);
			String firstCellValue = null;
			if(cell != null)
				firstCellValue = this.getKeyValue(cell, null);//拿到第一列第一行的值
			
			int colBlankNum = 0;
			boolean hasKey = false;
			/** start 对excel的第一行有效列进行循环遍历，判断是否可以导入 lis **/
			for (short c = 0; c < cols; c++) 
			{
				cell = row.getCell(c);
				if (cell != null) {
					String colComment = "";
					String title = "";
					title = this.getTitle(cell);
					colComment = this.getComment(cell);
					if (StringUtils.isBlank(title)){
						colBlankNum++;
						continue;
					}else colBlankNum = 0;
					
					//存在多个空列，结束导入
					if(colBlankNum > 10)
						break;
					
					if (StringUtils.isBlank(colComment))//未设置批注
						throw new GeneralException("[" + title + "]"+ResourceFactory.getProperty("gz_new.gz_accounting.no_comment"));

					//第一列的第一行不是“主键标识串”，则系统必须设置唯一性标识
					if (!"主键标识串".equals(firstCellValue)) {
						Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
						//得到系统设置的唯一性指标代码
						onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
						//得到系统设置的唯一性指标是否可用
						String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");

						//系统未设置唯一性指标
						if ("0".equals(uniquenessvalid) || StringUtils.isBlank(onlyname))
							throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.system_noset_key"));
						else // 系統设置了唯一性指标,则要判断在excel的列中是否有唯一性指标名称
						{
							// excel列中存在唯一性指标
							if (colComment.equalsIgnoreCase(onlyname)) {
								//唯一性指标列在导入文件中不存在
								hasKey = true;
								onlyColIndex = c;
							}
							
							// 如果是提成工资，查看在excel中是否有对应关联指标
							if ("1".equals(royalty_valid) && colComment.length() > 0) {
								String a_royalty_relation_fields = "," + royalty_relation_fields + ",";
								// 如果有关联指标，将excel中提成工资关联指标代码放到relationFieldMap
								if (a_royalty_relation_fields.indexOf(","+ colComment.toLowerCase() + ",") != -1)
									relationFieldMap.put(colComment.toLowerCase(),String.valueOf(c));
							}
							
						}
					}else{
						hasKey = true;
					}
					if("主键标识串".equals(title))
						continue;
					String field = cell.getCellComment().getString().toString()
							.trim().replaceAll("\\r", "").replaceAll("\\n", "");
					if (DataDictionary.getFieldItem(field,0) == null) {
						if (onlyname.length() > 0)// 自定义模板的情况,后台库表不存在字段
							throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.noExist_in_database")+"[" + cell.getStringCellValue() + "]！");
						else	//请用导出的模板Excel来导入数据
							throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.usering_template"));
					}
					//在当前薪资项目中不存在,不允许导入
					if (fieldMap.get(field.toUpperCase()) == null)
						throw new GeneralException("【" + title + "】"+ResourceFactory.getProperty("gz_new.gz_accounting.noExist_in_salarySet"));

					String pri = this.userView.analyseFieldPriv(field);
					if ("1".equals(pri) || "0".equals(pri)) // 只读或者是没有权限
						continue;
					
					maxColCount = c;// 有效列
					if ((onlyname + ",b0110,e0122,a0100,e01a1,a0101").indexOf(field.toLowerCase()) == -1)// 单位 \部门\岗位\ 姓名,字段不更新
					{
						headList.add(DataDictionary.getFieldItem(field,0));// 可以更新的列,插入新数据时使用
						updateColMap.put(new Short(c), field + ":" + cell.getStringCellValue());// excel中可以更新的列所在位置
						
						String codesetid = DataDictionary.getFieldItem(field,0).getCodesetid();
						if("UM".equals(codesetid) || "UN".equals(codesetid) || "@K".equals(codesetid)){
							String tablename=(String)parameterMap.get("tablename");
							updateSql.append(field+"= " + Sql_switcher.isnull("?", tablename+"."+field)+",");
						}else
							updateSql.append(field + "=?,");// 可以更新的数据库字段
						update_item_str.append(","+field);
						updateFidsCount++;
					}else
						continue;
					// excel中姓名所在第几列(北京移动使用)
					if ("a0101".equalsIgnoreCase(field.toLowerCase()))
						a0101ColIndex = c;


					String codesetid = DataDictionary.getFieldItem(field,0).getCodesetid();
					if (!"0".equals(codesetid)) {
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equals(codesetid)) {
							codeBuf.append("?,");
							codeList.add(codesetid.toUpperCase());
						}
					}

				} else
					break;
			}
			
			if(!hasKey)
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.uniquekey_noexist"));
			//提成工资关联指标列在导入文件中不存在
			if("1".equals(royalty_valid) && relationFieldMap.size() == 0)
				throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.royaltyField_noexist"));
			/** end 对excel的第一行有效列进行循环遍历，判断是否可以导入 lis **/

			/** start 对codeColMap填充数据，excel中的下拉数据 **/
			HashMap<String,HashMap<String,String>> leafCodeColMap =  new HashMap<String, HashMap<String,String>>();
			if (codeBuf.length() > 0) {
				codeBuf.deleteCharAt(codeBuf.length()-1);
				String sql="select codesetid,codeitemid,codeitemdesc from codeitem where  upper(codesetid) in ( " + codeBuf.toString()  + ") AND "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date";

				try {
					RowSet rs = dao.search(sql,codeList);
					while (rs.next()) {
						codeColMap.put(rs.getString("codesetid") + "a04v2u" + rs.getString("codeitemdesc"), rs.getString("codeitemid"));
					}

					sql="SELECT cm.codesetid,cm.codeitemid,cm.codeitemdesc FROM codeitem cm  inner join codeset on cm.codesetid=codeset.codesetid  LEFT JOIN (";
					sql+=" SELECT COUNT(1) AS num ,codesetid,parentid FROM codeitem WHERE  codeitemid<>parentid AND "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date  and upper(codesetid) in ("+codeBuf.toString().toUpperCase()+") GROUP BY parentid,codesetid ";
					sql+=") cnum ON cm.codesetid =cnum.codesetid AND cm.codeitemid=cnum.parentid ";
					sql+=" WHERE "+Sql_switcher.isnull("cnum.num","0")+"=0 and upper(cm.codesetid) in ("+codeBuf.toString().toUpperCase()+") and "+Sql_switcher.isnull("leaf_node","0")+"=1 AND "+Sql_switcher.sqlNow()+" BETWEEN cm.start_date AND cm.end_date";
					ArrayList<String> tempList= (ArrayList<String>) codeList.clone();
					codeList.addAll(tempList);
					rs = dao.search(sql,codeList);
					while (rs.next()) {

						if(leafCodeColMap.containsKey(rs.getString("codesetid"))) {
							leafCodeColMap.get(rs.getString("codesetid")).put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
						}else{
							HashMap<String,String> tempMap=new HashMap<String, String>();
							tempMap.put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
							leafCodeColMap.put(rs.getString("codesetid"),tempMap);
						}
					}


				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			/** end 对codeColMap填充数据，excel中的下拉数据 **/
			
			updateSql.setLength(updateSql.length() - 1);// 取掉最后一个逗号
			repeatsql.append(updateSql.toString()); //更新数据，网易需求，lis
			
			if ("1".equals(royalty_valid))// 是提成薪资
			{
				updateSql.append(" where 1=1 ");
			} else if (onlyname.length() == 0)// 不是提成薪资，系统未设置唯一性指标，excel中有“主键标识串”
				updateSql.append(" where NBASE=? and A0100=? and A00Z0=? and A00Z1=?");
			else // 用户自定义模板方式,不是提成薪资，系统设置唯一性指标
			{
				repeatsql.append(" where 1=1 ");
				updateSql.append(" where " + onlyname + "=? ");
			}

			if("sp".equals(flag)){
				updateSql.append(" and salaryid=" + salaryid);
				String gz_module = (String)parameterMap.get("gz_module");
			    updateWhere = inOutBo.getSpUpdateWhere((String)parameterMap.get("accountingdate"), (String)parameterMap.get("accountingcount"), gzbo,gz_module);
			}else{
				updateWhere = inOutBo.getImportWhere(Integer.valueOf(salaryid));
			}
			updateSql.append(updateWhere);
			repeatsql.append(updateWhere);
			repeatsql.append(" and a0100=? and upper(nbase)=upper(?) and a00z0=? and a00z1=?");//更新数据，网易需求，lis
			
			parameterMap.put("repeatsql", repeatsql.toString());
			parameterMap.put("update_item_str", update_item_str.toString());
			parameterMap.put("onlyname", onlyname);
			parameterMap.put("codeColMap", codeColMap);
			parameterMap.put("leafCodeColMap",leafCodeColMap);
			parameterMap.put("updateWhere", updateWhere);
			parameterMap.put("onlyColIndex", onlyColIndex);
			parameterMap.put("relationFieldMap", relationFieldMap);
			parameterMap.put("headList", headList);
			parameterMap.put("updateColMap", updateColMap);
			parameterMap.put("updateSql", updateSql);
			parameterMap.put("updateFidsCount", updateFidsCount);
			parameterMap.put("maxColCount", maxColCount);
			parameterMap.put("a0101ColIndex", a0101ColIndex);
			parameterMap.put("codeBuf", codeBuf);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return parameterMap;
	}
	
	/**
	 * @Title: getParamter 
	 * @Description: TODO(得到一些需要的参数) 
	 * @param parameterMap 参数map
	 * @param dao
	 * @param flag "sp"是薪资审批
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-13 下午01:52:03
	 */
	private void getParamter(HashMap parameterMap,ContentDAO dao,String flag,SalaryInOutBo inOutBo,SalaryTemplateBo gzbo,String privSql) throws GeneralException{
		try {
			ArrayList<FieldItem> relationFieldList = new ArrayList<FieldItem>();
			HashMap<String, String> UniqueIndexValMap = new HashMap<String, String>();
			HashMap<String, String> maxZ1ByPersonMap = new HashMap<String, String>();
			HashMap<String, String> a0101ValueMap = (HashMap<String, String>)parameterMap.get("a0101ValueMap");
			HashMap<String, String> a0101ValueMap2 = (HashMap<String, String>)parameterMap.get("a0101ValueMap2");
			
			SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt((String)parameterMap.get("salaryid")),this.userView);
			boolean isBJYD = (Boolean)parameterMap.get("isBJYD");
			String tablename = (String)parameterMap.get("tablename");
			String onlyname = (String)parameterMap.get("onlyname");
			String royalty_valid = (String)parameterMap.get("royalty_valid");
			String royalty_relation_fields = (String)parameterMap.get("royalty_relation_fields");
			
			/** start 为UniqueIndexValMap（数据库中唯一性标识数据）填充数据 **/
			if ("1".equals(royalty_valid))// 如果是提成工资
			{
				FieldItem aitem = null;
				String[] temps = royalty_relation_fields.toLowerCase().split(
						",");
				for (int i = 0; i < temps.length; i++) {
					if (temps[i].trim().length() > 0) {
						aitem = DataDictionary.getFieldItem(temps[i].trim(),0);
						relationFieldList.add(aitem);
					}
				}

				StringBuffer buf = new StringBuffer();
				buf.append("select *  from " + tablename + " where 1=1 ");

				this.frowset = dao.search(buf.toString());
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				while (this.frowset.next()) {
					String only_value = this.frowset.getString(onlyname);
					String onlyValueFlag = only_value;
					// 对提成工资关联指标循环
					for (int i = 0; i < temps.length; i++) {
						// 如果不是唯一性指标
						if (temps[i].trim().length() > 0 && !temps[i].equalsIgnoreCase(onlyname)) {
							aitem = DataDictionary.getFieldItem(temps[i].trim(),0);
							if ("A".equalsIgnoreCase(aitem.getItemtype())) {
								if (this.frowset.getString(temps[i]) == null || this.frowset.getString(temps[i]).trim().length() == 0)
									onlyValueFlag += "|null";
								else
									onlyValueFlag += "|" + this.frowset.getString(temps[i]);
							} else if ("D".equalsIgnoreCase(aitem.getItemtype())) {
								if (this.frowset.getDate(temps[i]) == null)
									onlyValueFlag += "|null";
								else
									onlyValueFlag += "|" + df.format(this.frowset.getDate(temps[i]));
							}

						}
					}

					UniqueIndexValMap.put(onlyValueFlag, "");// 存放提成薪资唯一性指标和关联指标合并组成的字符串

					if (maxZ1ByPersonMap.get(only_value) == null)// 新增数据时使用
						// 归属次数
						maxZ1ByPersonMap.put(only_value, this.frowset
								.getString("a00z1"));
					else {
						int z1 = Integer.parseInt((String) maxZ1ByPersonMap
								.get(only_value));
						// 判断是否是最大归属次数
						if (this.frowset.getInt("a00z1") > z1)
							maxZ1ByPersonMap.put(only_value, this.frowset
									.getString("a00z1"));
					}

				}
			} else if (onlyname.length() > 0)// 系统设置唯一性指标（不是提成工资）
			{
				String _sql = "select distinct " + onlyname + " from " + tablename + " where 1=1 ";

				if("sp".equals(flag)){
					String accountingdate = (String)this.getFormHM().get("appdate"); 
				    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
				    String accountingcount = (String)this.getFormHM().get("count"); 
				    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
					String where = inOutBo.getSpExportWhere(accountingdate, accountingcount, gzbo);
					_sql += where;
				}
				
				this.frowset = dao.search(_sql+privSql);// 得到没有重复的数据
				while (this.frowset.next())
					UniqueIndexValMap.put(this.frowset.getString(1), "");// 唯一性指标值

				// 北京移动
				if (onlyname.length() > 0 && isBJYD) {
					_sql = _sql.replaceAll("distinct " + onlyname," distinct a0101 ");// 将唯一性指标替换成姓名
					String _sql2 = _sql;
					_sql += " and (" + onlyname + " is null or " + onlyname + "='' ) ";
					this.frowset = dao.search(_sql);// 查询出唯一性指标为空的姓名数据
					while (this.frowset.next())
						a0101ValueMap.put(this.frowset.getString(1).trim(), "");// 唯一性指标无值的姓名

					this.frowset = dao.search(_sql2);// 查询出所有数据
					while (this.frowset.next())
						a0101ValueMap2.put(this.frowset.getString(1).trim(), "");// 唯一性指标有值的姓名

				}

			} else { // 唯一性指标不存在，则肯定有“主键标识串”
				StringBuffer buf = new StringBuffer();
				buf.append("select *  from " + tablename + " where 1=1 ");
				if("sp".equals(flag)){
					String accountingdate = (String)this.getFormHM().get("appdate"); 
				    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
				    String accountingcount = (String)this.getFormHM().get("count"); 
				    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
				    String cound = (String) this.getFormHM().get("cound");
				    //导入模板也没必要
					String sql = inOutBo.getSpSql(accountingdate, accountingcount,cound, gzbo,setbo, false);
					buf.setLength(0);
					buf.append(sql);
				}
				this.frowset = dao.search(buf.toString());
				while (this.frowset.next()) {
					String nASE = this.frowset.getString("NBASE");
					String a0100 = this.frowset.getString("A0100");
					String a00Z0 = this.frowset.getDate("A00Z0").toString();
					String a00Z1 = this.frowset.getString("A00Z1");
					String onlyValueFlag = nASE + "|" + a0100 + "|" + a00Z0 + "|"
							+ a00Z1;
					UniqueIndexValMap.put(onlyValueFlag, "");// 正确的唯一性指标(主键标识串)值参照
				}
			}
			/** 为UniqueIndexValMap(唯一性指标值集合)填充数据 end **/
			parameterMap.put("relationFieldList", relationFieldList);
			parameterMap.put("UniqueIndexValMap", UniqueIndexValMap);
			parameterMap.put("maxZ1ByPersonMap", maxZ1ByPersonMap);
			parameterMap.put("a0101ValueMap", a0101ValueMap);
			parameterMap.put("a0101ValueMap2", a0101ValueMap2);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @Title: getRowsParamter 
	 * @Description: TODO(对excel中数据遍历，组装数据) 
	 * @param parameterMap
	 * @param wb
	 * @param dao
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-13 下午01:52:47
	 */
	private void getRowsParamter(HashMap parameterMap,Workbook wb,ContentDAO dao,SalaryTemplateBo gzbo,SalaryAccountBo salaryAccountBo,String flag,String privSql) throws GeneralException{
		try {
			ArrayList<String> onlyValueRepeat = new ArrayList<String>();
			int totalRowCount = 0;
			HashMap<String, String> maxZ1ByPersonMap = (HashMap<String, String>)parameterMap.get("maxZ1ByPersonMap");
			HashMap<String, String> a0100sMap = new HashMap<String, String>();
			ArrayList list2 = new ArrayList();
			ArrayList newRecordList = new ArrayList();
			ArrayList nullOnlyValueList = new ArrayList();;
			HashMap<String, String> onlyValueMap = new HashMap<String, String>();
			
			HashMap<String,String> codeColMap = (HashMap<String, String>)parameterMap.get("codeColMap");
			HashMap<Short, String> updateColMap = (HashMap<Short, String>)parameterMap.get("updateColMap");

			//设置了仅可选择叶子节点的代码类数据集合
			HashMap<String,HashMap<String,String>> leafCodeColMap = (HashMap<String,HashMap<String,String>>)parameterMap.get("leafCodeColMap");

			int maxColCount = (Integer)parameterMap.get("maxColCount");
			HashMap<String, String> UniqueIndexValMap = (HashMap<String, String>)parameterMap.get("UniqueIndexValMap");
			String onlyname = (String)parameterMap.get("onlyname");
			int onlyColIndex = (Integer)parameterMap.get("onlyColIndex");
			boolean isBJYD = (Boolean)parameterMap.get("isBJYD");
			String royalty_valid = (String)parameterMap.get("royalty_valid");
			ArrayList<FieldItem> relationFieldList = (ArrayList<FieldItem>)parameterMap.get("relationFieldList");
			HashMap<String, String> relationFieldMap = (HashMap<String, String>)parameterMap.get("relationFieldMap");
			HashMap<String, String> a0101ValueMap = (HashMap<String, String>)parameterMap.get("a0101ValueMap");
			HashMap<String, String> a0101ValueMap2 = (HashMap<String, String>)parameterMap.get("a0101ValueMap2");
			int a0101ColIndex = (Integer)parameterMap.get("a0101ColIndex");
			StringBuffer updateSql = (StringBuffer)parameterMap.get("updateSql");
			
			HashMap onlyRepeat = new HashMap();
			ArrayList onlyRepeatList = new ArrayList();//存放新插入记录的更新值，用于计算后更新  zhaoxg add 2015-9-29
			HashMap updateWheres = new HashMap();//where条件集合，网易，lis
			ArrayList wyNewRecordList=new ArrayList(); //新增记录，网易，lis 2015-10-8
			
			HashMap allOnlyFieldInExcel = new HashMap();//存放excel中所有的人




			int rowBlankNum = 0;
			for(int sheetNum=0;sheetNum<wb.getNumberOfSheets();sheetNum++) {
				Sheet sheet=wb.getSheetAt(sheetNum);
				int rowNum=sheet.getLastRowNum();//获取整个数据集最后一行数据的下标 zhanghua 2018-06-23
				if(sheet.getPhysicalNumberOfRows()<=0)
					break;
				Cell cell = null;
				FieldItem aitem = null;
				String value = "";
				/** start 对excel中要导入的数据遍历 **/
				for (int j = sheetNum==0?1:0; j <= rowNum; j++) {
					ArrayList list = new ArrayList();
					Row row = sheet.getRow(j);// 拿到第j行
					if(row==null){
						rowBlankNum++;
						continue;
					}
					//这里对excel每一行进行判断，如果全为空不走
					int flagA = 0;
					for (int i = 0; i <= maxColCount; i++) {
						if (StringUtils.isEmpty(this.getKeyValue(row.getCell(i), null)))
							flagA++;
						else
							break;
					}
					if (flagA == (maxColCount + 1))
						continue;
					totalRowCount++;
					if (StringUtils.isBlank(onlyname)) // 第一列标题是“主键标识串”
					{
						Cell keyFlagCol = row.getCell(0);// 该行的第一列单元格
						//连续存在多个空行，结束导入
						if (rowBlankNum > 10)
							break;
						//是空行则继续
						if (StringUtils.isEmpty(this.getKeyValue(keyFlagCol, null)) && StringUtils.isEmpty(this.getKeyValue(row.getCell(1), null))) {
							rowBlankNum++;
							totalRowCount--;
							continue;
						} else if (StringUtils.isEmpty(this.getKeyValue(keyFlagCol, null))) {
							onlyValueRepeat.add(sheetNum+","+Integer.toString(j));
							continue;
						} else
							rowBlankNum = 0;




						String keyFlagColVal = keyFlagCol.getStringCellValue();

						// 当前excel数据在数据库中不存在
						if (UniqueIndexValMap.get(keyFlagColVal) == null) {
							onlyValueRepeat.add(sheetNum+","+Integer.toString(j));//插入错误数据位置，（sheetNum工作簿号+，+行号）
							continue;
						}

						/** start 对整个有效列循环 **/
						list = getRowData(list, row, maxColCount, updateColMap, codeColMap, leafCodeColMap);
						/** end 对整个有效列循环 **/

						// 对主键标识分割，得到库前缀、用户id、归属时间和归属次数
						String[] temp = keyFlagCol.getStringCellValue().split("\\|");


						// a0100s.append("a0100 ='" + temp[1] + "' or ");
						a0100sMap.put(keyFlagCol.getStringCellValue(), sheetNum+"," + j);// excel中第j行的第一列值（主键标识串的值）
						list.add(temp[0]);
						list.add(temp[1]);
						list.add(Date.valueOf(temp[2]));
						list.add(new Integer(temp[3]));
						list2.add(list);
					} else // 第一列标题不是“主键标识串”，系统设置了唯一性指标
					{
						boolean isNull_onlyCol = false; // 北京移动，唯一标识是否为空
						String a0101_value = ""; // 姓名列的值

						Cell flagCol = row.getCell((short) onlyColIndex);// 读取唯一性指标所占列
						String onlyFildValue = "";
						if (flagCol != null) {
							rowBlankNum = 0;
							//获得唯一性指标值
							onlyFildValue = getKeyValue(flagCol, onlyname);
							if (StringUtils.isEmpty(onlyFildValue)) {
								if (isBJYD) {
									onlyFildValue = "";
								} else {
									onlyValueRepeat.add(sheetNum+","+Integer.toString(j));//插入错误数据位置，（sheetNum工作簿号+，+行号）
									continue;
								}
							}
						} else // 唯一性指标单元格为空
						{
							if (onlyColIndex > 0)// onlyColIndex肯定大于0
							{
								if (StringUtils.isEmpty(getKeyValue(row.getCell(onlyColIndex - 1), onlyname)) && StringUtils.isEmpty(getKeyValue(row.getCell(0), onlyname)))// 说明是空行
								{
									rowBlankNum++;
									continue;
								}
							} else if (onlyColIndex == 0) {
								if (StringUtils.isEmpty(getKeyValue(row.getCell(1), onlyname)))// 说明是空行
								{
									rowBlankNum++;
									continue;
								}
							}

							if (rowBlankNum > 10)//连续十行空行则结束导入
								break;

							// 北京移动唯一性指标为空也可导入
							if (isBJYD)
								totalRowCount++;
							else {
								onlyValueRepeat.add(sheetNum+","+Integer.toString(j));
								continue;
							}
						}

						if (StringUtils.isBlank(onlyFildValue))// excel唯一性指标值为空串
						{
							// 北京移动
							if (isBJYD) {
								isNull_onlyCol = true;// 北京移动，唯一性指标为空时姓名列不能有空值
							}

						} else {
							if (!"1".equals(royalty_valid))// 不是提成工资
							{
								if (!"sp".equals(flag)) {//不是薪资审批
									//-------------------网易需求----------------------------
									ArrayList repeatList = new ArrayList();
									ArrayList updateWhere = new ArrayList();//一个人可能有多条数据，如果大于一，则从第二条数据更新
									String temp = (String) onlyRepeat.get(onlyFildValue);
									int isRepeat = 0;
									//excel中是重复数据且在数据库中存在
									if (StringUtils.isNotBlank(temp)) {
										if (updateWheres.containsKey(onlyFildValue))
											updateWhere = (ArrayList) updateWheres.get(onlyFildValue);
										if (updateWhere.size() == 0) {

//										//人员排序
//										String orderBy = null;
//										String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView); 
//										if(order_str!=null&&order_str.trim().length()>0&&salaryAccountBo.isExistErrorItem(order_str))
//											orderBy = order_str;
//										else
//											orderBy = " dbid,a0000, A00Z0, A00Z1";
											//根据人员唯一性指标在薪资表中查询当前人员的a0100、nbase、a00z0、a00z1
											//String sqlrepeat = "select a0100,nbase,a00z0,a00z1 from "+gzbo.getGz_tablename()+" where "+onlyname+"='"+onlyFildValue+"' order by " + orderBy;
											String sqlrepeat = "select a0100,nbase,a00z0,a00z1 from " + gzbo.getGz_tablename() + " where " + onlyname + "='" + onlyFildValue + "' " + privSql + " order by a00z1";

											RowSet rs = dao.search(sqlrepeat);
											while (rs.next()) {
												LazyDynaBean bean = new LazyDynaBean();
												bean.set("a0100", rs.getString("a0100"));
												bean.set("nbase", rs.getString("nbase"));
												bean.set("a00z0", rs.getDate("a00z0"));
												bean.set("a00z1", rs.getString("a00z1"));
												updateWhere.add(bean);

												//得到薪资表中人员的最大发放次数
												if (maxZ1ByPersonMap.get(onlyFildValue) == null)
													maxZ1ByPersonMap.put(onlyFildValue, rs.getString("a00z1"));
												else {
													int z1 = Integer.parseInt((String) maxZ1ByPersonMap.get(onlyFildValue));
													if (rs.getInt("a00z1") > z1)
														maxZ1ByPersonMap.put(onlyFildValue, rs.getString("a00z1"));

												}
											}
											updateWheres.put(onlyFildValue, updateWhere);
										}
										if (updateWhere.size() >= Integer.parseInt(temp) + 1) {    //如果数据库中一个人存在多条数据
											//从第二条重复数据开始继续更新对应excel中的重复数据
											isRepeat = 1;
										} else {
											isRepeat = 2;//重复数据新增
											repeatList.add(onlyFildValue);
										}
									} else if (UniqueIndexValMap.get(onlyFildValue) == null) {
										isRepeat = 3;//数据库中不存在，新增
										repeatList.add(onlyFildValue);
									}

									/** start 对整个有效列循环,系统设置了唯一性指标 **/
									repeatList = getRowData(repeatList, row, maxColCount, updateColMap, codeColMap, leafCodeColMap);
									/** end 对整个有效列循环 **/

									//excel中是重复数据且在数据库中存在
									if (isRepeat == 1) {
										LazyDynaBean tempbean = (LazyDynaBean) updateWhere.get(Integer.parseInt(temp));
										if (tempbean != null) {    //当前excel中重复的行在数据库中有对应数据
											repeatList.add(tempbean.get("a0100"));
											repeatList.add(tempbean.get("nbase"));
											repeatList.add(tempbean.get("a00z0"));
											repeatList.add(tempbean.get("a00z1"));
											onlyRepeatList.add(repeatList);
											onlyRepeat.put(onlyFildValue, (Integer.parseInt(temp) + 1) + "");
										}
									} else if (isRepeat == 2 || isRepeat == 3) {//新增数据
										wyNewRecordList.add(repeatList);//excel中重复数据，但是在数据库中存在的，或是在数据库中不存在的，网易需求，lis
									}

									//excel中不是重复数据且在数据库中存在
									if (onlyValueMap.get(onlyFildValue) == null && UniqueIndexValMap.get(onlyFildValue) != null)
										onlyRepeat.put(onlyFildValue, 1 + "");

									//存放excel中所有的人
//									if (allOnlyFieldInExcel.get(onlyFildValue) == null)
									allOnlyFieldInExcel.put(sheetNum+","+Integer.toString(j), onlyFildValue);

									//----------------------------网易需求 end --------------------------------------------
								}
								// 数据库中存在同时在onlyValueMap中不存在的
								if (onlyValueMap.get(onlyFildValue) == null && UniqueIndexValMap.get(onlyFildValue) != null)
									onlyValueMap.put(onlyFildValue, Integer.toString(j));//
								else {
									if ("sp".equals(flag))
										onlyValueRepeat.add(sheetNum+","+Integer.toString(j));// excel重复数据
									continue;
								}
							}
						}

						String key = onlyFildValue;

						boolean isNewRecord = false;

						if ("1".equals(royalty_valid)) {
							// 对提成工资关联指标遍历
							for (int i = 0; i < relationFieldList.size(); i++) {
								aitem = (FieldItem) relationFieldList.get(i);
								String codesetid = aitem.getCodesetid();
								String index = (String) relationFieldMap.get(aitem
										.getItemid());
								if (!aitem.getItemid().equalsIgnoreCase(onlyname)) {
									value = "null";
									Cell cell1 = row.getCell(Integer
											.parseInt(index));
									if (cell1 != null) {
										if (cell1.getCellType() == Cell.CELL_TYPE_STRING) {
											value = cell1.getRichStringCellValue().toString();
											if (!"0".equals(codesetid) && !"".equals(codesetid) && StringUtils.isNotBlank(value))// 代码类id
											{
												if ("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
													value = value.split(":")[0];
												} else if (codeColMap.get(codesetid
														+ "a04v2u" + value.trim()) != null)
													value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
												else
													value = "";
											}
										}
									}

									if (value.length() > 0 && "D".equalsIgnoreCase(aitem.getItemtype())) {
										value = value.replaceAll("\\-", "/"); // 需完善
										// 邓灿
									}
									if (value.trim().length() == 0)
										key += "|null";
									else
										key += "|" + value;

								}
							}

							if (UniqueIndexValMap.get(key) == null)// 是提成工资同时在数据库中不存在（excel中的这个人在数据库中不存在）
								isNewRecord = true;

							// 提成工资的唯一性指标组成是：用户id+归属日期+归属次数
							if (onlyValueMap.get(key) == null)
								onlyValueMap.put(key, Integer.toString(j));
							else {
								onlyValueRepeat.add(sheetNum+","+Integer.toString(j));
								continue;
							}
						}

						if (isNewRecord)
							list.add(onlyFildValue);

						/** start 对整个有效列循环,系统设置了唯一性指标 **/
						list = getRowData(list, row, maxColCount, updateColMap, codeColMap, leafCodeColMap);
						/** end 对整个有效列循环 **/

						if (isNull_onlyCol)// 是北京移动，excel中唯一性指标值为空
						{
							cell = row.getCell(a0101ColIndex);
							if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING)
								a0101_value = cell.getRichStringCellValue().toString();

							if (a0101_value == null || a0101_value.trim().length() == 0) {
								//源数据当唯一指标的值为空时需参考的姓名列不能有空值
								String msg = ResourceFactory.getProperty("gz_new.gz_accounting.name_not_null");
								throw new GeneralException(msg);
							} else {
								if (a0101ValueMap2.get(a0101_value.trim()) == null)// 数据库中唯一性指标不为空的数据中的姓名
								{
									onlyValueRepeat.add(sheetNum+","+Integer.toString(j));
									continue;
								}

								if (a0101ValueMap.get(a0101_value.trim()) == null)// 数据库中唯一性指标为空的数据中的姓名
								{
									//源数据唯一性指标的值为空,但库中唯一性指标有值不符合格式
									String msg = a0101_value + ResourceFactory.getProperty("gz_new.gz_accounting.resource_null");
									throw new GeneralException(msg);
								}
								list.add(a0101_value);
								nullOnlyValueList.add(list);
							}
						} else {
							if (isNewRecord)// 是提成工资同时在数据库中不存在（excel中的这个人在数据库中不存在）
								newRecordList.add(list);
							else {
								if ("1".equals(royalty_valid))// 是提成工资，更新数据库
								{
									String[] temps = key.split("\\|");

									String tempSql = updateSql.toString();
									tempSql += " and   " + onlyname + "='" + temps[0] + "'";

									int n = 1;
									for (int i = 0; i < relationFieldList.size(); i++) {
										aitem = (FieldItem) relationFieldList
												.get(i);
										if (!aitem.getItemid().equalsIgnoreCase(
												onlyname)) {
											if (!"null".equalsIgnoreCase(temps[n])) {
												if ("D".equalsIgnoreCase(aitem.getItemtype()))
													tempSql += " and " + Sql_switcher.isnull(Sql_switcher.dateToChar(aitem.getItemid(), "YYYY-MM-DD"), "''")
															+ "='"
															+ temps[n].replaceAll("\\/", "-")
															+ "' ";
												else
													tempSql += " and " + Sql_switcher.isnull(aitem.getItemid(), "''")
															+ "='" + temps[n]
															+ "' ";
											} else {
												if (Sql_switcher.searchDbServer() == Constant.ORACEL)
													tempSql += " and " + aitem.getItemid() + " is null ";
												else
													tempSql += " and " + Sql_switcher.isnull(aitem.getItemid(), "''") + "='' ";
											}
											n++;
										}
									}
									dao.update(tempSql, list);// 更新数据库
								} else {
									a0100sMap.put(onlyFildValue, sheetNum+"," + j);// excel中第j行的唯一性指标值（系统设置唯一性指标）
									list.add(onlyFildValue);
									list2.add(list);
								}
							}
						}
					}

				}
			}
			parameterMap.put("allOnlyFieldInExcel", allOnlyFieldInExcel);
			parameterMap.put("wyNewRecordList", wyNewRecordList);
			parameterMap.put("onlyRepeatList", onlyRepeatList);
			parameterMap.put("onlyValueRepeat", onlyValueRepeat);
			parameterMap.put("totalRowCount", totalRowCount);
			parameterMap.put("maxZ1ByPersonMap", maxZ1ByPersonMap);
			parameterMap.put("a0100sMap", a0100sMap);
			parameterMap.put("list2", list2);
			parameterMap.put("newRecordList", newRecordList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @Title: getKeyValue 
	 * @Description: TODO(获得唯一性指标值) 
	 * @param flagCol
	 * @param onlyname
	 * @return String
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:45:54
	 */
	private String getKeyValue(Cell flagCol,String onlyname) throws GeneralException{
		String value = null;
		try {
			if (flagCol != null) {
				switch (flagCol.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					break;
				case Cell.CELL_TYPE_STRING:
					value = flagCol.getRichStringCellValue().toString();
					break;
				case Cell.CELL_TYPE_FORMULA:
					break;
				case Cell.CELL_TYPE_NUMERIC:
					double y = flagCol.getNumericCellValue();
					int decwidth = 0;
					if(StringUtils.isNotEmpty(onlyname))
						decwidth = DataDictionary.getFieldItem(onlyname,0).getDecimalwidth();
					value = this.getNumValue(decwidth, y).toString();
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
	 * @Title: getCellValue 
	 * @Description: TODO(获得单元格值) 
	 * @param cell
	 * @param fieldItemName
	 * @param codeColMap
	 * @param leafCodeColMap 仅可选择叶子节点的map
	 * @return
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:46:22
	 */
	private Object getCellValue(Cell cell,String fieldItemName,HashMap<String,String> codeColMap,HashMap<String,HashMap<String,String>> leafCodeColMap) throws GeneralException{
		Object value = "";
		String[] colField = fieldItemName.split(":");
		try {
			String fieldItemId = colField[0]; // 指标代码id
			String fieldName = colField[1]; // 指标名称
			FieldItem fieldItem = DataDictionary.getFieldItem(fieldItemId,0);
			String itemtype = fieldItem.getItemtype(); // 值类型，是数值型(N)、字符型(S)、日期型(D)、备注型(M)
			String codesetid = fieldItem.getCodesetid(); // 代码类id
			int decwidth = fieldItem.getDecimalwidth(); // 小数点位数
			int itemlength = fieldItem.getItemlength();// 长度

			if (cell != null) {
				switch (cell.getCellType()) {
					case Cell.CELL_TYPE_FORMULA:
						break;
					case Cell.CELL_TYPE_NUMERIC:
						double y = cell.getNumericCellValue();
						SimpleDateFormat sdf = null;
						if("D".equalsIgnoreCase(itemtype))
				        {
							if(HSSFDateUtil.isCellDateFormatted(cell)) {
								java.util.Date date = HSSFDateUtil.getJavaDate(y);
								if (date != null)
									value = new java.sql.Date(date.getTime());
								
							}else {
								Date valueSqlDate = null;
								//验证的时候如果floor不能去掉小数点，导致正则验证错误
								String itemfmt = itemlengthFmt(itemlength,Math.round(y)+"",fieldItem,cell.getRow());
								java.util.Date date = DateUtils.getDate(Math.floor(y)+"", itemfmt);
								if (date != null)
									valueSqlDate = new java.sql.Date(date.getTime());
								value = valueSqlDate;
							}
				        } else 
			            	value = this.getNumValue(decwidth, y);
						break;
					case Cell.CELL_TYPE_STRING:
						value = cell.getRichStringCellValue().toString();
						value = this.getStrValue(value.toString(), codesetid, codeColMap, itemtype, decwidth,itemlength,fieldItem,cell.getRow(),leafCodeColMap);
						break;
					case Cell.CELL_TYPE_BLANK:// 如果什么也不填的话数值就默认更新为0
						break;
					default:
						value = "";
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			if(e.toString().indexOf("不可选择")==-1)
				throw GeneralExceptionHandler.Handle(new Exception("\""+colField[1]+"\"列，第"+(cell.getRow().getRowNum()+1)+"行请按模版类型进行导入！"));
			else
				throw GeneralExceptionHandler.Handle(e);
		}
		return value;
	}
	
	/**
	 * @Title: getRowData 
	 * @Description: TODO(获得一行数据集合) 
	 * @param list
	 * @param row
	 * @param maxColCount
	 * @param updateColMap
	 * @param codeColMap
	 * @param leafCodeColMap 仅可选择叶子节点的map
	 * @return  ArrayList
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:46:40
	 */
	private ArrayList getRowData(ArrayList list,Row row,int maxColCount,HashMap<Short, String> updateColMap,HashMap<String,String> codeColMap,HashMap<String,HashMap<String,String>> leafCodeColMap) throws GeneralException{
		try {
			/** start 对整个有效列循环,系统设置了唯一性指标 **/
			for (short c = 0; c <= maxColCount; c++) {
				Cell cell = row.getCell(c);

				String fieldItemName = updateColMap.get(new Short(c)); // 可更新的列map
				if (StringUtils.isBlank(fieldItemName)) // 过滤掉只读和没有权限的列
					continue;
				Object value = this.getCellValue(cell, fieldItemName, codeColMap,leafCodeColMap);
				String[] colField = fieldItemName.split(":");
				String fieldItemId = colField[0]; // 指标代码id
				FieldItem fieldItem = DataDictionary.getFieldItem(fieldItemId,0);
				String itemtype = fieldItem.getItemtype(); // 值类型，是数值型(N)、字符型(S)、日期型(D)、备注型(M)
				int decwidth = fieldItem.getDecimalwidth(); // 小数点位数
				int itemSize=fieldItem.getItemlength();
				String codesetid=fieldItem.getCodesetid();
				int itemlength = fieldItem.getItemlength();
				
				if("A".equals(itemtype)){//判断如果导入项目关联un 或者um时，如果excel文件中直接写入的就是机构id那么进行导入，否则不导入 zhanghua 2017-5-9
					//若不导入则设置为 null 若需更新为空 则 设置为‘ ’ ，需要插入数据 则设置为 机构id
					String content="";
					String values = String.valueOf(value);
				    if("un".equalsIgnoreCase(codesetid)){
						if(value==null||StringUtils.isBlank(values)){
							list.add(null);
							continue;
						}
						if(values.contains(":"))
							values = values.split(":")[0];
						//由于在导入进来的时候如果是单位代码，可能在获取的时候后面会跟着小数点（.0）
				        content = AdminCode.getCodeName("UN", values);
				        if(StringUtils.isBlank(content))
				            content = AdminCode.getCodeName("UM", values);
				    } 
				    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
					{							
						if(value==null||StringUtils.isBlank(values)){
							list.add(null);
							continue;
						}
						if(values.contains(":"))
							values = values.split(":")[0];
						if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
						{
								content=AdminCode.getCodeName("UM",values);
						}else
							content = (AdminCode.getCodeName("UM",values)==null || (AdminCode.getCodeName("UM",values)!=null 
							&& AdminCode.getCodeName("UM",values).trim().length()==0))?AdminCode.getCodeName("UN",values)
									: AdminCode.getCodeName("UM",values);	
					
					}else
						content="1";
				    
					if(StringUtils.isNotBlank(content))
						list.add(values);
					else
						list.add(null);
					continue;
					
					
				}
				else if("D".equals(itemtype)){
					String valueString = value.toString();
					String itemfmt = "";
					if(!"0".equals(valueString)) {
						try {
							//timestamp可以将时分秒写入，否则时分秒不能写入
							list.add(StringUtils.isBlank(valueString)?null:new java.sql.Timestamp(((Date)value).getTime()));
						}catch(Exception e) {
							throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，日期格式不正确"));
						}
					}else 
						list.add(null);
					continue;
				}
				else if("N".equals(itemtype)){
					if(value==null|| "".equals(value.toString()))
						value=0;
					//如果是科学记数，转为BigDecimal。防止进度缺失  sunjian 2017-8-14
					if(value.toString().indexOf("E") != -1) {
						value = new BigDecimal(value.toString());
					}
					String[] tempStr=value.toString().replaceAll("[-]","").split("\\.");

					if(tempStr.length>1){
						if(tempStr[0].length()>itemSize)
							throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，整数部分大于允许范围"+itemSize+"位！"));
//						if(!tempStr[1].equals("0")&&!tempStr[1].equals("00")&&tempStr[1].length()>decwidth)
//							throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，小数部分大于允许范围"+decwidth+"位！"));
					}else if(tempStr[0].length()>itemSize){
							throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，整数部分大于允许范围"+itemSize+"位！"));
					}

				}
				list.add(value);
				
			}
			/** end 对整个有效列循环 **/
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * @Title: getNumValue 
	 * @Description: TODO(获得单元格格式是数字时的值) 
	 * @param decwidth
	 * @param y
	 * @return Double
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:47:11
	 */
	private Double getNumValue(int decwidth,double y) throws GeneralException{
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
			//将数值型指标直接截取到库结构中规定的小数位数 zhanghua 2018-06-27 38438
			y=new Double(value);
			DecimalFormat d=new DecimalFormat();
			d.setMaximumFractionDigits(decwidth);
			d.setRoundingMode(RoundingMode.HALF_UP);
			d.setGroupingSize(0);
			value=d.format(y);
			return new Double(value);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @Title: getStrValue 
	 * @Description: TODO(获得单元格为字符型时的值) 
	 * @param value
	 * @param codesetid
	 * @param codeColMap
	 * @param itemtype
	 * @param decwidth
	 * @param leafCodeColMap 仅可选择叶子节点的map
	 * @return	Object
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:48:45
	 */
	private Object getStrValue(String value,String codesetid,HashMap codeColMap,String itemtype,int decwidth,int itemlength,FieldItem fieldItem,Row row,HashMap<String,HashMap<String,String>> leafCodeColMap) throws GeneralException{
		try {
			Object value1 = "";
			if(StringUtils.isNotBlank(value)){
				if (!"0".equals(codesetid) && StringUtils.isNotBlank(codesetid))// 代码类id
				{
					if ("UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
						value = value.split(":")[0];
					} else if (codeColMap.get(codesetid + "a04v2u" + value.trim()) != null) {
						if(leafCodeColMap.size()>0&&leafCodeColMap.containsKey(codesetid)){//判断所选代码是否设置了仅可选择叶子节点。 zhanghua 2018-02-09 10:19:56
							if(!leafCodeColMap.get(codesetid).containsKey(value.trim()))
								throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列仅可选择末端代码，\""+value+"\"不可选择！"));
						}
						value = (String) codeColMap.get(codesetid + "a04v2u" + value.trim());
					}
					else
						value = "";
				}
				
				if ("N".equals(itemtype)) {
					value = PubFunc.round(value, decwidth);
					if (decwidth == 0)
						value1 = new Integer(value);
					else
						value1 = new Double(value);
				} else if ("D".equals(itemtype) && !"0".equals(value)) {
					java.sql.Date d_t = null;
					value = value.replaceAll("\\.", "-").replace("\\", "-").replace("/", "-");
					String itemfmt = itemlengthFmt(itemlength,value,fieldItem,row);
					java.util.Date src_d_t = DateUtils.getDate(value, itemfmt);
					if (src_d_t != null)
						d_t = new java.sql.Date(src_d_t.getTime());
					value1 = d_t;
				} else
					value1 = value;
			}
			return value1;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @Title: getTitle 
	 * @Description: TODO(获得excel表格列名) 
	 * @param cell
	 * @return	String
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:49:23
	 */
	private String getTitle(Cell cell) throws GeneralException {
		try {
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
				default:
					title = "";
				}
			}

			return title;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * @Title: getComment 
	 * @Description: TODO(获得excel表格列批注) 
	 * @param cell
	 * @return	String
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-5 下午05:49:51
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
	 * 是提成工资同时在数据库中不存在（excel中的这个人在数据库中不存在），新建数据
	 * 
	 * @param headList
	 * @param newRecordList
	 * @param onlyColIndex
	 * @param maxZ1ByPersonMap
	 * @return
	 */
	public int insertNewRecords(ContentDAO dao, int salaryid,
			ArrayList headList, ArrayList newRecordList, int onlyColIndex,
			String onlyField, HashMap maxZ1ByPersonMap, SalaryTemplateBo gzbo, HashMap parameterMap)
			throws GeneralException {
		int num = 0;
		try {
			SalaryAccountBo salaryAccountBo = new SalaryAccountBo(this.getFrameconn(), this.userView, salaryid);
			String standardGzItemStr = salaryAccountBo.getStandardGzItemStr(salaryid);
			//String standardGzItemStr2 = "/A0100/A0101/A01Z0/USERFLAG/NBASE/A00Z2/A00Z3/A00Z0/A00Z1/SP_FLAG/SP_FLAG2/B0110/E0122";
			String dbpres = gzbo.getTemplatevo().getString("cbase");//人员库前缀
			String[] dbarr = StringUtils.split(dbpres, ",");//人员库前缀

			//发薪标识
			String pay_flag = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PAY_FLAG);
			StringBuffer buf = new StringBuffer("");//插入数据库的sql
			StringBuffer sub_buf = new StringBuffer("");
			buf.append("insert into ");
			buf.append(gzbo.getGz_tablename());
			String sysItem = ",A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1,";
			if(sysItem.indexOf("," + onlyField.toUpperCase() + ",") > 0)
				buf.append(" (A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1,");
			else
				buf.append(" (A0100,A0101,userflag,nbase,B0110,E0122,sp_flag,A00Z2,A00Z3,A00Z0,A00Z1,"+onlyField);
			if (gzbo.getManager().length() > 0) {
				buf.append(",sp_flag2");
				sub_buf.append(",?");
			}
			
			FieldItem aItem = null;
			boolean has_pay_flag =false;
			for (int i = 0; i < headList.size(); i++) {
				aItem = (FieldItem) headList.get(i);
				if(pay_flag.toLowerCase().equals(aItem.getItemid().toLowerCase())){
					has_pay_flag = true;
				}
				if ("M".equalsIgnoreCase(aItem.getItemtype()))
					continue;
				if (aItem.getItemid().equalsIgnoreCase(onlyField))
					continue;
				if (standardGzItemStr.indexOf("/" + aItem.getItemid().toUpperCase() + "/") != -1) {
					buf.append("," + aItem.getItemid());
					sub_buf.append(",?");
				}
			}

			//薪资项目中没有发薪标识
			if(StringUtils.isNotBlank(pay_flag) && !has_pay_flag){
				buf.append(",");
				buf.append(pay_flag);
				//standardGzItemStr2 += pay_flag.toUpperCase() + "/";
				sub_buf.append(",?");
			}
			buf.append(" ) values (?,?,?,?,?,?,?,?,?,?,?,?" + sub_buf.toString() + ")");
			String strYm = (String)parameterMap.get("accountingdate");
			strYm = strYm.length() == 7 ? strYm + "-01" : strYm;
			String count = (String)parameterMap.get("accountingcount");
			if(StringUtils.isNotBlank(strYm)) {
				HashMap ffsjMap=salaryAccountBo.getMaxYearMonthCount(null, true);
				strYm=(String)ffsjMap.get("ym");
				count=(String)ffsjMap.get("count");
			}
			java.util.Date src_d = DateUtils.getDate(strYm, "yyyy-MM-dd");
			java.sql.Date d = new java.sql.Date(src_d.getTime());

			LazyDynaBean abean = null;
			ArrayList insertInfoList = new ArrayList();
			HashMap insertRecordKey = new HashMap();
			for (int i = 0; i < newRecordList.size(); i++) {
				ArrayList tempList = (ArrayList) newRecordList.get(i);
				String onlyValue = (String) tempList.get(0);
				if (StringUtils.isBlank(onlyValue))
					continue;
				ArrayList list = new ArrayList();
				for (int j = 0; j < dbarr.length; j++) {
					String pre = dbarr[j];
					if (!this.userView.isSuper_admin() && this.userView.getDbpriv().toString().toLowerCase().indexOf("," + pre.toLowerCase() + ",") == -1)
						continue;
					ArrayList listValue = new ArrayList();
					listValue.add(onlyValue);
					this.frowset = dao.search("select * from " + pre + "A01 where " + onlyField + "=?",listValue);
					if (this.frowset.next()) {

						list.add(this.frowset.getString("a0100"));
						list.add(this.frowset.getString("a0101"));
						list.add(this.userView.getUserName());
						list.add(pre.toUpperCase());
						list.add(this.frowset.getString("b0110"));
						list.add(this.frowset.getString("e0122"));
						list.add("01");
						list.add(d);
						list.add(new Integer(count));
						list.add(d);

						int acount = 1;
						if (maxZ1ByPersonMap.get(onlyValue) != null)
							acount = Integer.parseInt((String) maxZ1ByPersonMap.get(onlyValue)) + 1;
						maxZ1ByPersonMap.put(onlyValue, String.valueOf(acount));
						list.add(new Integer(acount));
						list.add(onlyValue);
						insertRecordKey.put(pre.toLowerCase() + "|" + this.frowset.getString("a0100") + "|" + strYm + "|" + acount, "1");

						if (gzbo.getManager().length() > 0)
							list.add("01");

						for (int n = 0; n < headList.size(); n++) {
							aItem = (FieldItem) headList.get(n);
							if ("M".equalsIgnoreCase(aItem.getItemtype()))
								continue;
							if (aItem.getItemid().equalsIgnoreCase(onlyField))
								continue;
							if (standardGzItemStr.indexOf("/" + aItem.getItemid().toUpperCase() + "/") != -1) {

								if ("D".equalsIgnoreCase(aItem.getItemtype())) {
									if (tempList.get(n + 1) == null)
										list.add(null);
									else {
										list.add((java.sql.Date) tempList.get(n + 1));
									}
								} else if ("N"
										.equalsIgnoreCase(aItem.getItemtype())) {
									if (tempList.get(n + 1) == null)
										list.add(null);
									else {
										if (tempList.get(n + 1) instanceof Double)
											list.add((Double) tempList.get(n + 1));
										else if (tempList.get(n + 1) instanceof Integer)
											list.add((Integer) tempList.get(n + 1));
										else if (tempList.get(n + 1) instanceof String)
											list.add(new Double((String) tempList.get(n + 1)));
									}

								} else if ("A"
										.equalsIgnoreCase(aItem.getItemtype())) {
									if (tempList.get(n + 1) == null)
										list.add(null);
									else {
										list.add((String) tempList.get(n + 1));
									}
								}
							}
						}

						DbWizard dw = new DbWizard(this.getFrameconn());
						if (StringUtils.isNotBlank(pay_flag) && !has_pay_flag){
							if(dw.isExistField(pre + "A01", pay_flag, false))
								list.add(this.frowset.getString(pay_flag));
							else list.add(null);
						}
						
						insertInfoList.add(list);
						break;
					}
				}

			}
			num = insertInfoList.size();
			dao.batchInsert(buf.toString(), insertInfoList);
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(),
					salaryid, this.userView);
			int deleNum = inOutBo.delNoConditionData(gzbo.getGz_tablename(),
					insertRecordKey);
			num = num - deleNum;

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return num;
	}

	/**
	 * 生成提示excel
	 * 
	 * @throws GeneralException
	 */
	public String generateErrorFile(ArrayList onlyValueRepeat, Workbook wb1,
			File file, int okCount) throws GeneralException {
		String errorFileName = "su_gz_template_错误提示.xls";
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet2 = wb.createSheet();
			HSSFRow row2 = sheet2.createRow(0);

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

			Row row1 = wb1.getSheetAt(0).getRow(0);
			int cols = row1.getPhysicalNumberOfCells();

			HSSFCell cell2 = row2.createCell(0);
			cell2.setCellValue("成功导入" + okCount + "条。");

			HSSFComment comm = null;
			HSSFPatriarch patr = sheet2.createDrawingPatriarch();
			row2 = sheet2.createRow(1);
			if (row1 != null) {
				int titleCount = 0;
				for (int i = 0; i < cols; i++) {
					Cell cell = row1.getCell(i);
					if (cell != null) {
						cell2 = row2.createCell(i);
						cell2.setCellValue(cell.getStringCellValue());
						cell2.setCellStyle(style2);
						comm = patr.createComment(new HSSFClientAnchor(1, 1, 1,
								2, (short) (i + 1), 0, (short) (i + 2), 2));
						comm.setString(new HSSFRichTextString(cell
								.getCellComment().getString().getString()));
						cell2.setCellComment(comm);
						titleCount++;
					}
				}
				cols = titleCount;
				ExportExcelUtil.mergeCell(sheet2, 0, 0, 0, cols - 1);
			}

			int rowIndex = 2;
			for (int i = 0; i < onlyValueRepeat.size(); i++) {
				String temp = (String) onlyValueRepeat.get(i);

				int sheetNum=Integer.parseInt( temp.split(",")[0]);//插入错误数据位置，（sheetNum工作簿号+，+行号）
				temp=temp.split(",")[1];
				row2 = sheet2.createRow(rowIndex++);

				row1 = wb1.getSheetAt(sheetNum).getRow(Integer.parseInt(temp));
				for (int k = 0; k < cols; k++) {
					Cell cell = row1.getCell(k);
					if (cell != null) {
						cell2 = row2.createCell(k);
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							cell2.setCellValue(cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							cell2.setCellValue(cell.getStringCellValue());
							break;
						}
						// cell2.setCellStyle(cell.getCellStyle());
					}
				}
			}

			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + errorFileName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeIoResource(wb);
		}
		return errorFileName;
	}

	public String getDatePart(String mydate, String datepart) {

		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart)) {
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart)) {
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
	}

	/**
	 * 判断 值类型是否与 要求的类型一致
	 * 
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	public boolean isDataType(int decwidth, String itemtype, String value) {

		boolean flag = true;
		if ("N".equals(itemtype)) {
			if (decwidth == 0) {
				flag = value.matches("^[+-]?[\\d]+$");
			} else {
				flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
			}

		} else if ("D".equals(itemtype)) {
			flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	
	/**
	 * @Title: insertTempTable 
	 * @Description: 插入新数据 
	 * @param dao
	 * @param updateSql 更新插入的数据sql
	 * @param onlyField 唯一性指标
	 * @param wyNewRecordList 更新数据集合
	 * @param gzbo
	 * @param maxZ1ByPersonMap 最大归属次数map集合
	 * @return int
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-10-9 下午01:55:54
	 */
	private HashMap insertTempTable(ContentDAO dao,String updateSql,String onlyField,ArrayList wyNewRecordList,
									String salaryid,HashMap maxZ1ByPersonMap,SalaryTemplateBo gzbo,String update_item_str,HashMap parameterMap) throws GeneralException{
		int num=0;
		HashMap returnData = new HashMap();
		try
		{
			SalaryAccountBo accountBo = new SalaryAccountBo(this.getFrameconn(), this.userView, Integer.valueOf(salaryid));
			String tableName = "t#"+this.userView.getUserName()+"_inst";
			if(update_item_str.startsWith(","))
				update_item_str=update_item_str.substring(1);
			//创建临时表
			accountBo.createImportTable(tableName,gzbo);
			String dbpres=gzbo.getTemplatevo().getString("cbase");//当前薪资人员库范围
			String[] dbarr=StringUtils.split(dbpres, ",");
			
			boolean controlPriv=false;//是否设置为了特殊上报类别，即不控制管理权限
			String gzNotControlPrivIds=SystemConfig.getPropertyValue("GzNotControlPrivIds");
			String deptid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
			deptid = deptid != null ? deptid : ""; 
			String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
			orgid = orgid != null ? orgid : "";
			String a_code="",privid="",privVal="";
			
			Boolean isManager=false;
			String manager=gzbo.getManager();
			//是薪资发放，且共享 非管理员即为数据上报
			if(updateSql.indexOf("salaryhistory")==-1&&((StringUtils.isNotBlank(manager)&&!this.userView.getUserName().equalsIgnoreCase(manager)) ||this.userView.isSuper_admin()))
				isManager=true;
			
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");
			
			//在配置文件中设置为不控制，且具有归属部门条件则不进行权限控制。
			if(isManager&&StringUtils.isNotBlank(gzNotControlPrivIds)&&(StringUtils.isNotBlank(deptid)||StringUtils.isNotBlank(orgid))&&gzNotControlPrivIds.indexOf(salaryid)!=-1){
				
				controlPriv=true;
				if(b_units.startsWith("`"))
					b_units=b_units.substring(1);

				if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
				{
					if("UN`".equalsIgnoreCase(b_units)){
						
					}else {
						String []temps=b_units.split("`");
						
						if(StringUtils.isNotBlank(deptid)&&StringUtils.isNotBlank(orgid)){
							a_code=temps[0];
							if(a_code.toLowerCase().startsWith("un")){
								privid=orgid;
								privVal=a_code.substring(2);
							}else if(a_code.toLowerCase().startsWith("um")){
								privid=deptid;
								privVal=a_code.substring(2);
							}
						}else if(StringUtils.isNotBlank(deptid)){//如果归属部门不是空，则将 权限中的第一个，无论是单位或部门都写入归属部门中。					
							a_code=temps[0];
							privid=deptid;
							privVal=a_code.substring(2);
						}else{ //如果归属单位不是空则 找到权限中的单位权限写入归属单位，没有单位权限则不写。
							for(String str:temps){
								if(str.toLowerCase().startsWith("un")){
									privid=orgid;
									privVal=str.substring(2);
									break;
								}
							}
						}
						
					}
				}				
				
				//获取最大归属次数
				if(wyNewRecordList.size()!=0){
					StringBuilder strSql=new StringBuilder("select "+onlyField+",Max(a00z1) as a00z1  from "+gzbo.getGz_tablename() +" where "+onlyField+" in (");
					ArrayList datalist=new ArrayList();
					for(int i=0;i<wyNewRecordList.size();i++){
						ArrayList tempList = (ArrayList)wyNewRecordList.get(i); 
						String onlyvalue=(String)tempList.get(0);
						strSql.append("?,");
						datalist.add(onlyvalue);
					}
					strSql.deleteCharAt(strSql.length()-1);
					strSql.append(") group by "+onlyField);
					
					RowSet rs=dao.search(strSql.toString(), datalist);
					while(rs.next()){
						maxZ1ByPersonMap.put(rs.getString(onlyField), rs.getString("a00z1"));
					}
					
				}
				
				
			}
			
			
			
			
			
			

			
			//向临时表中插入数据的slq语句
			StringBuffer buf=new StringBuffer("");
			buf.append("insert into ");
			buf.append(tableName);
			buf.append("(NBASE,A0100,A0101,A00Z0,A00Z1,B0110,E0122,dbid,A0000 ");
			StringBuffer bufValue=new StringBuffer("(?,?,?,?,?,?,?,?,?");
			if(StringUtils.isNotBlank(orgid)&&controlPriv){
				buf.append(","+orgid);
				bufValue.append(",?");
			}
			if(StringUtils.isNotBlank(deptid)&&controlPriv){
				buf.append(","+deptid);
				bufValue.append(",?");
			}
			buf.append(" ) values "+bufValue+")");
			
			int index=-1;//归属部门数据下标位置
			if(StringUtils.isNotBlank(deptid)){
				String [] stritem=update_item_str.split(",");
				for(int i=0;i<stritem.length;i++){//取得归属部门 或者单位下标位置 以便替换updatelist的值
					if(stritem[i].toLowerCase().indexOf(deptid.toLowerCase())!=-1){
						index=i;
						break;
					}
				}
			}
			
			int oindex=-1;//归属单位数据下标位置
			if(controlPriv&&StringUtils.isNotBlank(orgid)){
				String [] stritem=update_item_str.split(",");
				for(int i=0;i<stritem.length;i++){//取得归属部门 或者单位下标位置 以便替换updatelist的值
					if(stritem[i].toLowerCase().indexOf(orgid.toLowerCase())!=-1){
						oindex=i;
						break;
					}
				}
			}
			
			FieldItem aItem=null;
			String strYm = (String)parameterMap.get("accountingdate");
			strYm = strYm.length() == 7 ? strYm + "-01" : strYm;
			String count = (String)parameterMap.get("accountingcount");
			if(StringUtils.isNotBlank(strYm)) {
				HashMap ffsjMap=accountBo.getMaxYearMonthCount(null, true);
				strYm=(String)ffsjMap.get("ym");
				count=(String)ffsjMap.get("count");
			}
			java.util.Date src_d=DateUtils.getDate(strYm,"yyyy-MM-dd");
			java.sql.Date d=new java.sql.Date(src_d.getTime());
			
			LazyDynaBean abean=null;
			ArrayList insertInfoList = new ArrayList();
			HashMap errorOnlyField = new HashMap();
			HashMap allOnlyFiel = new HashMap();
			HashMap updateRecordKey = new HashMap();
			Map insertRecordKey=new HashMap();
			for(int i=0;i<wyNewRecordList.size();i++)
			{
				ArrayList tempList=(ArrayList)wyNewRecordList.get(i); 
				String onlyValue=(String)tempList.get(0);
				boolean isInDb = false;//当前人是否在薪资权限人员库中存在
				tempList.remove(0);
				if(onlyValue.trim().length()==0)
					continue;
				
				StringBuffer buf1=new StringBuffer(buf.toString());
				for(int j=0;j<dbarr.length;j++)
				{
						ArrayList list=new ArrayList();
						ArrayList insertlist=new ArrayList();
						String pre=dbarr[j];
						if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
							continue;
						this.frowset=dao.search("select * from "+pre+"A01 where "+onlyField+"='"+onlyValue+"'");		
						if(this.frowset.next())
						{
							int dbid = 0;
							String dbidSql = "select dbid from dbname where upper(dbname.pre)='"+pre+"'";
							RowSet rowset = dao.search(dbidSql);
							if(rowset.next())
								dbid = rowset.getInt("dbid");
							list.add(pre.toUpperCase());
							list.add(this.frowset.getString("a0100"));
							list.add(this.frowset.getString("a0101")); 
							list.add(d);
							
							int acount=1;
							if(maxZ1ByPersonMap.get(onlyValue)!=null)
								acount=Integer.parseInt((String)maxZ1ByPersonMap.get(onlyValue))+1;
							maxZ1ByPersonMap.put(onlyValue, String.valueOf(acount));
							list.add(new Integer(acount));
							
							list.add(this.frowset.getString("b0110"));
							list.add(this.frowset.getString("e0122"));
							list.add(dbid);
							list.add(this.frowset.getString("a0000"));
							

							insertlist=(ArrayList) list.clone();
							insertlist.remove(0);
							insertlist.add(0, pre);

							if(StringUtils.isNotBlank(orgid)&&controlPriv){
								if(orgid.equalsIgnoreCase(privid)){
										list.add(privVal);
								}else
									list.add("");
							}
							
							if(StringUtils.isNotBlank(deptid)&&controlPriv){
								if(deptid.equalsIgnoreCase(privid)){
										list.add(privVal);
								}else
									list.add("");
							}

							
							tempList.add(this.frowset.getString("a0100"));
							tempList.add(pre.toUpperCase());
							tempList.add(d);
							tempList.add(new Integer(acount));
							
							insertInfoList.add(list);	
							
							String key = pre.toLowerCase()+"|"+this.frowset.getString("a0100")+"|"+strYm+"|"+acount;
							allOnlyFiel.put(key, onlyValue);
							updateRecordKey.put(key,tempList);
							insertRecordKey.put(key,insertlist);
							isInDb = true;
							break;
						}
				}
				if(!isInDb){
					allOnlyFiel.put(i, onlyValue);
				}
			}
			
			num=insertInfoList.size();
			if(num > 0)
				dao.batchInsert(buf.toString(), insertInfoList);
			
			int deleNum=0;
			HashSet keySet=accountBo.delNoConditionData(tableName,updateRecordKey,controlPriv);
			for(Iterator t=keySet.iterator();t.hasNext();)//删除不符合条件的项
			{
				String str=(String)t.next();
				if(updateRecordKey.get(str)!=null){
					deleNum++;
					updateRecordKey.remove(str);
				}
					
			}
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(gzbo.getGz_tablename());
			buf.append("(NBASE,A0100,A0101,A00Z0,A00Z1,B0110,E0122,dbid,A0000,A00Z2,A00Z3,sp_flag,userflag");
			
			if(StringUtils.isNotBlank(manager))//添加报审标识
				buf.append(",sp_flag2");
			
			if(controlPriv&&StringUtils.isNotBlank(privid))
				buf.append(","+privid);//添加归属部门
			buf.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?");
			if(StringUtils.isNotBlank(manager))
				buf.append(",?");
			if(controlPriv&&StringUtils.isNotBlank(privid))
				buf.append(",'"+privVal+"'");
			buf.append(")");
			
			Map insertRecordTemp=new HashMap();
			
			Iterator iter = updateRecordKey.entrySet().iterator();
			insertInfoList.clear();
			ArrayList updateInfoList = new ArrayList();
			
			//取得真正插入的归属单位或归属部门的数据下标 privid为真正需要插入的指标名
			if(privid.equalsIgnoreCase(orgid)){
				index=oindex;
			}
			
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey();
				ArrayList val = (ArrayList)entry.getValue();
				ArrayList list=new ArrayList();
				list = (ArrayList)insertRecordKey.get(key);
				list.add(d);
				list.add(new Integer(count));
				list.add("01");
				
				if(index!=-1&&(val.get(index)==null ||StringUtils.isBlank(val.get(index).toString())))
					val.set(index, privVal);
				list.add(this.userView.getUserName());
				if(StringUtils.isNotBlank(manager))//报审标识设为01
					list.add("01");
				
				updateInfoList.add(val);
				insertInfoList.add(list);
				
				//取得不在权限范围内的人集合
				if(allOnlyFiel.containsKey(key))
					allOnlyFiel.remove(key);
			}
			
			
			//将临时表中数据插入到新增表中
			if(insertInfoList.size() > 0){
				dao.batchInsert(buf.toString(), insertInfoList);
			}

			//取得不在权限范围内的人集合
			Iterator iter2 = allOnlyFiel.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				String val = (String)entry.getValue();
				
				errorOnlyField.put(val, "");
			}
			
			LazyDynaBean	busiDate=new LazyDynaBean(); //业务日期 次数   date:2010-03-01   count:1
			busiDate.set("date",strYm);
			busiDate.set("count",count);
			
			num=deleNum-deleNum;
			if(insertInfoList.size() > 0){
				ArrayList<LazyDynaBean> salaryItemsTemp = new ArrayList<LazyDynaBean>();
				String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
				ArrayList<LazyDynaBean> salaryItems = accountBo.getSalaryTemplateBo().getSalaryItemList("", salaryid, 1);
				for(LazyDynaBean bean:salaryItems){
					String itemid = (String)bean.get("itemid");
					if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
					{
						FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
						if(_tempItem==null)
							continue;
						
					}
					salaryItemsTemp.add(bean);
				}
				ArrayList exeptFlds=new ArrayList();//不计算启用了不按权限管理后的归属机构
				if(controlPriv&&StringUtils.isNotBlank(privid)){
					
					LazyDynaBean bean =new LazyDynaBean();
					bean.set("itemid", privid);
					exeptFlds.add(bean);
				}
				//导入后计算
				for(int j=0;j<dbarr.length;j++)
				{

					String pre=dbarr[j];
					StringBuffer strWhere = new StringBuffer(" ");
					strWhere.append(" exists (select null from ");
					strWhere.append(tableName);
					strWhere.append(" where ");
					strWhere.append("upper("+tableName + ".NBASE)=upper(" + gzbo.getGz_tablename() + ".NBASE) and ");
					strWhere.append(tableName + ".A00Z0=" + gzbo.getGz_tablename() + ".A00Z0  and ");
					strWhere.append(tableName + ".A00Z1=" + gzbo.getGz_tablename() + ".A00Z1 and ");
					strWhere.append(tableName + ".A0100=" + gzbo.getGz_tablename() + ".A0100 ");
					strWhere.append(")");
					accountBo.firstComputing(strWhere.toString(), pre, true, exeptFlds, salaryItemsTemp, null, busiDate);
				}
				
				//计算完后更新数据
				if(updateInfoList.size() > 0){
					dao.batchUpdate(updateSql, updateInfoList);
				}
			}
			
			//导入完成删除临时表 
			DbWizard dbw=new DbWizard(this.frameconn);
			Table table=new Table(tableName);
			if(dbw.isExistTable(tableName, false))
			{
				dbw.dropTable(table);
			}
			
			returnData.put("num", num);
			returnData.put("errorOnlyFieldMap", errorOnlyField);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return returnData;
	}
	
	/**
	 * 对于输出错误的格式的抛出异常
	 * @Title: itemlengthFmt   
	 * @Description:    
	 * @param @param itemlength
	 * @param @param valueString
	 * @param @param fieldItem
	 * @param @param row
	 * @param @return
	 * @param @throws GeneralException 
	 * @return String    
	 * @throws
	 */
	private String itemlengthFmt(int itemlength,String valueString,FieldItem fieldItem,Row row) throws GeneralException {
		String itemfmt = "";
		switch (itemlength) {
			case 4:
				if(!valueString.matches("\\d{4}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，该项系统中只显示年"));
				}
				itemfmt = "yyyy";
				break;
			case 7:
				if(!valueString.matches("\\d{4}-\\d{1,2}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，该项系统中只显示年和月"));
				}
				itemfmt = "yyyy-MM";
				break;
			case 10:
				if(!valueString.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，该项系统中显示年月日"));
				}
				itemfmt = "yyyy-MM-dd";
				break;
			case 16:
				if(!valueString.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，该项系统中显示年月日时分"));
				}
				itemfmt = "yyyy-MM-dd HH:mm";
				break;
			case 18:
				if(!valueString.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，该项系统中显示年月日时分秒"));
				}
				itemfmt = "yyyy-MM-dd HH:mm:ss";
				break;
			default:
				if(!valueString.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
					throw GeneralExceptionHandler.Handle(new Exception("\""+fieldItem.getItemdesc()+"\"列，第"+(row.getRowNum()+1)+"行，格式不正确，请以年月日显示"));
				}
				itemfmt = "yyyy-MM-dd";
				break;
		}
		return itemfmt;
	}
	
}
