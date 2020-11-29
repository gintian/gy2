package com.hjsj.hrms.module.gz.salaryaccounting.updisk.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject.BankDiskSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x 类名称：InitBankDiskTrans 类描述：银行报盘加载交易类 创建人：sunming 创建时间：2015-9-1
 * 
 * @version
 */
public class InitBankDiskTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			/** 薪资id* */
			String salaryid = "";
			//opt=0 加载银行数据列表， opt=1加载表格数据
			String opt="";
			/** model =0 薪资发放的银行报盘 =1 薪资审批的银行报盘* */
			String model = "";
			String bankid = "";
			String type="";
			MorphDynaBean bean = new MorphDynaBean();
			String appdate = "";
			String subModuleId="";
			String appCount="";
			if(!StringUtils.isEmpty((String)this.getFormHM().get("salaryid"))){
				salaryid = (String) this.getFormHM().get("salaryid");
				// opt=0 加载银行数据列表， opt=1加载表格数据
				opt = (String) this.getFormHM().get("opt");
				model = (String) this.getFormHM().get("model");
				//type=0 初次加载 =1重新加载
				type = (String) this.getFormHM().get("tp");
				// 有银行模板，取得银行模板bankList
				if(this.getFormHM().get("bankid")!=null&&!"".equals(this.getFormHM().get("bankid")))
				bankid = (String) this.getFormHM().get("bankid").toString();
				appdate = PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("appdate")));
				if("1".equals(model))
					appCount=PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("count")));
			}else{//快速查询
				bean = (MorphDynaBean)this.getFormHM().get("customParams");
				salaryid = (String)bean.get("salaryid");
				opt=(String) bean.get("opt");
				model=(String) bean.get("model");
				bankid=(String) bean.get("bankid");
				type = (String) bean.get("tp");
				appdate = PubFunc.decrypt(SafeCode.decode((String)bean.get("appdate")));
				subModuleId = (String) bean.get("subModuleId");
				if("1".equals(model))
					appCount=PubFunc.decrypt(SafeCode.decode((String)bean.get("count")));
			}
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			ArrayList<String> valuesList = new ArrayList<String>();
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
			if("1".equals(model)){
				bo.setAppdate(appdate);
				bo.setAppCount(appCount);
			}
			if ("0".equals(opt)) {
				ArrayList bankList = bo.getBankList();
				this.getFormHM().put("banklist", bankList);
			} else {
				/** model =0 薪资发放的银行报盘 =1 薪资审批的银行报盘* */
				String tableName = "";
				SalaryTemplateBo stbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid),this.userView);
				String manager = stbo.getManager();
				if ("0".equals(model)) {
					// 共享
					SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.valueOf(salaryid),this.userView);
					tableName = gzbo.getGz_tablename();
				} else {
					// 传递的参数需要完善 discount 等
					tableName = "salaryhistory";
				}
				if ("0".equals(type)&&(bankid==null|| "".equals(bankid))) {
					bankid = bo.getFirstBank_id();
				}
				// 拼接column
				ArrayList fieldList = bo.getFieldList(bankid);
				ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
				columnList = bo.toColumnsInfo(fieldList);
				ArrayList fieldsArray=new ArrayList();//复杂方案查询组件中的字段信息集合
				HashMap  queryFields=new HashMap();
				for(int i=0;i<fieldList.size();i++){
					FieldItem item = (FieldItem) fieldList.get(i);
					if("aid".equalsIgnoreCase(item.getItemid())){
						continue;
					}
					queryFields.put(item.getItemid().toLowerCase(),item);
					HashMap map = new HashMap();
					map.put("type", item.getItemtype());
					map.put("itemid", item.getItemid().toUpperCase());
					map.put("itemdesc", item.getItemdesc());
					map.put("codesetid", item.getCodesetid());
					map.put("ctrltype", "0");//0:不受控制 1：管理范围 2：操作单位 3 业务范围
					map.put("format", "Y-m-d");
					fieldsArray.add(map);
				}
		
				
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
				DbWizard dbw=new DbWizard(this.frameconn);
				boolean flag = false;
				if(!"0".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0&&dbw.isExistField(tableName, onlyname, false)){
					flag = true;
				}
				if("salaryaccount_updisk_00000001".equals(subModuleId)){
					// 查询类型，1为输入查询，2为方案查询
					valuesList = (ArrayList) this.getFormHM().get("inputValues");
				}
				String condSql = this.getCondSql(subModuleId, valuesList, uniquenessvalid, onlyname, tableName, flag,bankid,queryFields);
				// 拼接sql
				String sql = bo.getTableListSql(fieldList, bankid,condSql,tableName);
				ArrayList list = bo.getPersonInfoList(sql, fieldList, columnList, model,bankid);
				bo.createBankDiskTempTable(bankid, list, fieldList);
				//由于临时表中全部使用string 所以将页面字段全部转化为 字符型以适应特殊格式 2016-10-10 zhanghua
				for(ColumnsInfo info : columnList){
					info.setCodesetId("0");
					info.setColumnType("A");
				}
				// 拼table
				TableConfigBuilder builder = new TableConfigBuilder(tableName+bankid,//每个报盘拥有独立id
						columnList, "salaryaccountingupdisk", userView, this.getFrameconn());
				String midtable="t#"+this.userView.getUserName()+"_gz";
				builder.setDataSql("select * from "+midtable);
				builder.setOrderBy(" ");
				builder.setSelectable(true);
				builder.setAutoRender(false);
				//builder.setTitle(ResourceFactory.getProperty("menu.gz.updisk"));//银行报盘
				builder.setLockable(true);
				builder.setPageSize(20);
				builder.setSortable(true);
				builder.setScheme(true);
				builder.setSetScheme(false);
				builder.setSelectable(false);
				builder.setTableTools(bo.getUpBankButtonList(this.getLookStr(uniquenessvalid, onlyname, flag),bo.getBankIsEdit(bankid)));
				String config = builder.createExtTableConfig();
				this.getFormHM().put("tableConfigUpDisk", config.toString());
				this.getFormHM().put("bankid", bankid);
				this.getFormHM().put("fieldsArray", fieldsArray.toArray());
				this.getFormHM().put("tableId", tableName+bankid);
				this.getFormHM().put("lookStr", this.getLookStr(uniquenessvalid, onlyname, flag));
				this.getFormHM().put("fieldsize", fieldList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 获取查询条件sql片段
	 * @param subModuleId
	 * @param valuesList
	 * @param uniquenessvalid
	 * @param onlyname
	 * @param tableName
	 * @param flag
	 * @return
	 */
	public String getCondSql(String subModuleId,ArrayList valuesList,String uniquenessvalid,String onlyname,String tableName,boolean flag,String bankid,HashMap queryFields){
		String condSql = "";
		try{
			if("salaryaccount_updisk_00000001".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					StringBuffer str = new StringBuffer();
					for(int i=0;i<valuesList.size();i++){
						String queryValue = SafeCode.decode((String) valuesList.get(i));
						if(i==0){
							str.append("a0101 like '%"+queryValue+"%'");
							if(flag){
								str.append(" or "+onlyname+" like '%"+queryValue+"%'");
							}
						}else{
							str.append(" or a0101 like '%"+queryValue+"%'");
							if(flag){
								str.append(" or "+onlyname+" like '%"+queryValue+"%'");
							}
						}
					}
					if(valuesList.size()>0)
						condSql += str.toString();
				} else if ("2".equals(type)) {
					String exp = (String) this.getFormHM().get("exp");
					String cond = (String) this.getFormHM().get("cond");
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tableName+bankid);
					HashMap _queryFields = tableCache.getQueryFields();
					// 解析表达式并获得sql语句
					FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,(PubFunc.keyWord_reback(SafeCode.decode(cond))).toLowerCase(), userView.getUserName(),queryFields);
					condSql += parser.getSingleTableSqlExpression(tableName);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(condSql))
			condSql="("+condSql+")";
		return condSql;
	}
	/**
	 * 获取查询框内默认显示汉字内容
	 * @param uniquenessvalid
	 * @param onlyname
	 * @param flag
	 * @return
	 */
	public String getLookStr(String uniquenessvalid,String onlyname,boolean flag){
		StringBuffer lookStr = new StringBuffer(ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName"));//动态显示查询框内容
		try{
			if(flag&&!"a0101".equalsIgnoreCase(onlyname)){
				lookStr.append(",");
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				if(item!=null){
					lookStr.append(item.getItemdesc());
				}else{
					lookStr.append(ResourceFactory.getProperty("sys.options.param.uniquenesstarget"));//唯一性指标
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return lookStr.toString();
	}
}
