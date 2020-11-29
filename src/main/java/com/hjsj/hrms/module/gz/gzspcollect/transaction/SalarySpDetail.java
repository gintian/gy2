package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalaryPageLayoutBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SalarySpDetail 
 * 类描述： 薪资审批明细页面
 * 创建人：zhaoxg
 * 创建时间：Dec 15, 2015 5:12:38 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 15, 2015 5:12:38 PM
 * 修改备注： 
 * @version
 */
public class SalarySpDetail extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		String salaryid=(String)this.getFormHM().get("salaryid"); 
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
		gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
	    String accountingdate = (String)this.getFormHM().get("appdate"); 
	    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
	    String accountingcount = (String)this.getFormHM().get("count"); 
	    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
	    String viewtype = (String)this.getFormHM().get("viewtype"); // 页面区分 0:薪资发放  1:审批  2:上报
	    viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
	    String subModuleId = (String) this.getFormHM().get("subModuleId");//
	    String collectPoint = (String) this.getFormHM().get("collectPoint");
	    String id = (String) this.getFormHM().get("id");
	    String cound = (String) this.getFormHM().get("cound");
	    boolean isCheck = false;//标记是否是查询控件调用进来的
	    if(salaryid==null||salaryid.length()<1){
	    	MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
	    	salaryid = (String) bean.get("salaryid");
	    	salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
	    	gz_module = (String) bean.get("imodule");
	    	gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
	    	accountingdate = (String) bean.get("appdate");
	    	accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
	    	accountingcount = (String) bean.get("count");
	    	accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
	    	viewtype = (String) bean.get("viewtype");
	    	viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
	    	subModuleId = (String) bean.get("subModuleId");
	    	isCheck = true;
	    }
		try{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryPageLayoutBo pageBo = new SalaryPageLayoutBo(this.frameconn,this.userView);
			GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
			SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			String b0110 = "b0110";
			String e0122 = "e0122";
			String tableName = "salaryhistory";
			gzbo.updateSalaryTable(tableName, setbo);
			if("UNUM".equals(collectPoint)){//单位+部门
				String orgid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(orgid.length()>0){
					b0110 = orgid;
				}
				if(deptid.length()>0){
					e0122 = deptid;
				}
				collectPoint = spbo.getCollectPointSql(b0110, e0122,"salaryhistory");
			}else //可能出现指标为空和null的
				collectPoint = "nullif("+tableName + "." + collectPoint+",'')";
			
			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			ArrayList fieldlist=setbo.searchGzItem();
			//system.properties  salaryitem=false前台计算项不能编辑
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				gzbo.setFieldlist_readOnly(fieldlist);
			
			column = setbo.toSpColumnsInfo(fieldlist);//页面显示字段     
			StringBuffer sqlstr = new StringBuffer();
			ArrayList fieldsArray=new ArrayList();//复杂方案查询组件中的字段信息集合
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem) fieldlist.get(i);
				if("nbase".equalsIgnoreCase(item.getItemid())){//人员库特殊处理   _nbase加密用于传递
					sqlstr.append(",");
					sqlstr.append("upper(" + item.getItemid().toLowerCase());
					sqlstr.append(") as " + item.getItemid().toLowerCase());
					sqlstr.append(",");
					sqlstr.append("upper(" + item.getItemid().toLowerCase() + ")");
					sqlstr.append(" as nbase1");
				}else{
					sqlstr.append(",");
					sqlstr.append(item.getItemid().toLowerCase());
				}
				if(!"M".equalsIgnoreCase(item.getItemtype())){//去除大文本字段
					HashMap map = new HashMap();
					map.put("type", item.getItemtype());
					map.put("itemid", item.getItemid().toUpperCase());
					map.put("itemdesc", item.getItemdesc());
					map.put("codesetid", item.getCodesetid());
					map.put("formate", "Y-m-d H:i:s");
					fieldsArray.add(map);
				}
			}
			
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
			DbWizard dbw=new DbWizard(this.frameconn);
			boolean flag = false;
			if(!"0".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0&&dbw.isExistField(gzbo.getGz_tablename(), onlyname, false)){
				flag = true;
			}
			String lookStr = this.getLookStr(uniquenessvalid, onlyname, flag);
			ArrayList<String> valuesList = (ArrayList) this.getFormHM().get("inputValues");
			String condSql = this.getCondSql(subModuleId, valuesList, uniquenessvalid, onlyname, "salaryspdetail_"+salaryid, flag);
			if(isCheck){//页面模糊查询
				if(condSql.length()>0){
			//		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt(this.userView.getUserName()+"_"+salaryid)));
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("salaryspdetail_"+salaryid);
					tableCache.setQuerySql(" and "+condSql.replaceAll("data.", ""));//去掉表名，防止表格工具追加后报错
			//		this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt(tableName)), tableCache);
					this.userView.getHm().put(tableName, tableCache);
				}else{
			//		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(SafeCode.encode(PubFunc.encrypt(this.userView.getUserName()+"_"+salaryid)));
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("salaryspdetail_"+salaryid);
					tableCache.setQuerySql(" ");
			//		this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt(tableName)), tableCache);
					this.userView.getHm().put(tableName, tableCache);
				}
				return;
			}
			
			StringBuffer buf=new StringBuffer();
			buf.append("select dbid,userflag,"+sqlstr.substring(1)+" from ");//此处加上dbid  否则过滤的时候表格工具会报错
			buf.append(tableName);
			buf.append(" where 1=1 ");
			String privWhlStr = gzbo.getWhlByUnits(tableName,true);
			buf.append(" and ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' ) ) or curr_user='"+this.userView.getUserName()+"')");
			buf.append(" and salaryid="+salaryid+" and a00z2="+Sql_switcher.dateValue(accountingdate)+" and a00z3="+accountingcount+"");
			if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
				buf.append(" and UserFlag='"+cound+"'");
			}
			if(!"sum".equalsIgnoreCase(id)){
				if("null".equals(id)){
					buf.append(" and "+collectPoint+" is null ");
					this.getFormHM().put("detailsql", SafeCode.encode(PubFunc.encrypt(" and "+collectPoint+" is null ")));
				}else{
					buf.append(" and "+collectPoint+" like '"+id+"%'");
					this.getFormHM().put("detailsql", SafeCode.encode(PubFunc.encrypt(" and "+collectPoint+" like '"+id+"%'")));
				}
			}
			String relation_id=gzbo.getSpRelationId();//审批关系
			String spActorName = "";//如果此人只有一个领导那么报批按钮上面显示 报[xxx]审批 否则单纯显示报批
			String  sp_actor_str="";
			if(relation_id.length()>0)
				sp_actor_str=gzbo.getSpActorStr(relation_id,1);//审批关系中定义的直接领导
			if(sp_actor_str.length()>0)
			{
				String[] temps=sp_actor_str.split("`");
				if(temps.length==1)
				{
					temps=temps[0].split("##");
					spActorName = temps[1];
				}
			}
			HashMap buttonMap = new HashMap();//按钮一些个性参数的集合，为了不在方法中多传递参数
			buttonMap.put("spActorName", spActorName);
			String flow_flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");//1:需要审批  否则不需要审批
			if("1".equalsIgnoreCase(flow_flag))
			{
				if(relation_id.length()>0)
				{
					if(sp_actor_str.length()>0)
						buttonMap.put("appflag", "true");
					else
						buttonMap.put("appflag", "false");
				}
				else
					buttonMap.put("appflag", "true");
			}
			else
				buttonMap.put("appflag", "false");
			buttonMap.put("lookStr", lookStr);
			String orderby = "order by  dbid,a0000, A00Z0, A00Z1";
			TableConfigBuilder builder = new TableConfigBuilder("salaryspdetail_"+salaryid, column, "salaryspdetail", userView,this.getFrameconn());
			builder.setDataSql(buf.toString());
			builder.setOrderBy(orderby);
			builder.setScheme(true);
			builder.setSetScheme(false);
			builder.setLockable(true);
			builder.setEditable(true);
			builder.setSelectable(true);
			builder.setColumnFilter(true);
			builder.setPageSize(20);
			builder.setTableTools(pageBo.getSalarySpButtons(accountingdate,accountingcount,buttonMap,gz_module,gzbo));
			String config = builder.createExtTableConfig();
			
			String allowEditSubdata=gzbo.getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE); //允许提交后更改数据  
			if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
			    allowEditSubdata="0";
			String hasSubPirv="false"; //是否有提交结束数据的权限
			if((!"1".equals(gz_module)&&(this.userView.hasTheFunction("3240305")||this.userView.hasTheFunction("3270305")))||("1".equals(gz_module)&&(this.userView.hasTheFunction("3271305")||this.userView.hasTheFunction("3250305"))))
				hasSubPirv="true";
			this.getFormHM().put("hasSubPirv",hasSubPirv);
			this.getFormHM().put("allowEditSubdata",allowEditSubdata);
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("fieldsArray", fieldsArray.toArray());
//			this.getFormHM().put("detailSetId", SafeCode.encode(PubFunc.encrypt(this.userView.getUserName()+"_"+salaryid)));
			this.getFormHM().put("detailSetId", "salaryspdetail_"+salaryid);
			this.getFormHM().put("lookStr", lookStr);
			this.getFormHM().put("username",this.userView.getUserName());
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
	public String getCondSql(String subModuleId,ArrayList valuesList,String uniquenessvalid,String onlyname,String tableName,boolean flag){
		String condSql = "";
		try{
			if("salaryspdetail".equals(subModuleId)){
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
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tableName);
					HashMap queryFields = tableCache.getQueryFields();
					// 解析表达式并获得sql语句
					FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
					condSql += parser.getSingleTableSqlExpression("data");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
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
			if(flag){
				
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				if(item!=null){
					if(!"姓名".equals(item.getItemdesc())){
						lookStr.append(",");
						lookStr.append(item.getItemdesc());
					}
				}else{
					lookStr.append(",");
					lookStr.append(ResourceFactory.getProperty("sys.options.param.uniquenesstarget"));//唯一性指标
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return lookStr.toString();
	}
}
