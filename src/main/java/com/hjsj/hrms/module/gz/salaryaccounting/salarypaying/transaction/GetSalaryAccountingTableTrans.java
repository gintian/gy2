package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTableStructBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.module.gz.utils.SalaryPageLayoutBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * 项目名称：hcm7.x
 * 类名称：GetSalaryAccountingTableTrans
 * 类描述：薪资发放主界面初始化
 * 创建人：zhaoxg
 * 创建时间：Jun 29, 2015 2:07:55 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 29, 2015 2:07:55 PM
 * 修改备注：
 * @version
 */
public class GetSalaryAccountingTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		String salaryid=(String)this.getFormHM().get("salaryid"); //薪资类别号
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
		gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
	    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
	    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
	    String accountingcount = (String)this.getFormHM().get("count"); //发放次数
	    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
	    String viewtype = (String)this.getFormHM().get("viewtype"); // 页面区分 0:薪资发放  1:审批  2:上报
	    viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
	    String subModuleId = (String) this.getFormHM().get("subModuleId");//查询组件主键标识

	  
	    
	    if(!PubFunc.isUseNewPrograme(this.userView))
	    	throw GeneralExceptionHandler.Handle(new Exception("新开发的薪资程序仅支持70版本以上的加密锁!"));

	    String returnflag = (String) this.getFormHM().get("returnflag");//如果是nonereturn 则不显示返回按钮
	    String encryptParam = (String) this.getFormHM().get("encryptParam");
	    if(encryptParam!=null&&encryptParam.length()>0){
	    	if(StringUtils.isNumeric(this.getValByStr(encryptParam, "salaryid"))){//如果连接里面的salaryid不加密直接传数字也支持，但是判断下权限 zhaoxg add 2016-10-28
	    		salaryid = this.getValByStr(encryptParam, "salaryid");
	    		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
	    		safeBo.isSalarySetResource(salaryid,gz_module);
	    	}else
	    		salaryid = PubFunc.decrypt(SafeCode.decode(this.getValByStr(encryptParam, "salaryid")));
	    	gz_module = this.getValByStr(encryptParam, "imodule");
	    	viewtype = this.getValByStr(encryptParam, "viewtype");
	    	returnflag = this.getValByStr(encryptParam, "returnflag");
	    }

	    if(salaryid==null||salaryid.length()<1){
	    	MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");//查询组件返回条件集合
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
	    }
	    
	    
	    String eventlog="薪资";
	    if("1".equals(gz_module))
	    	eventlog="保险";
	    eventlog+="类别号："+salaryid;
	    this.getFormHM().put("@eventlog",eventlog);
	    

		try{
			gz_module=StringUtils.isBlank(gz_module)?"0":gz_module;
			
			if(this.userView.getStatus() == 0 && "2".equals(viewtype) && this.userView.getHm().get("selfUsername") == null) {//如果是数据上报，而且是业务用户，存入对应的自助自助用户名
				ArrayList list_self = PubFunc.SearchOperUserOrSelfUserName(userView);
				if(list_self.size() > 1) {
					this.userView.getHm().put("selfUsername", PubFunc.SearchOperUserOrSelfUserName(userView).get(1));
				}
			}else if(!"2".equals(viewtype)){
				this.userView.getHm().remove("selfUsername");
			}
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);

			//进入的时候判断是否在应用机构设置的时间范围内
			ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.getFrameconn(),salaryid,this.userView);
			String info = aorgbo.getComeInfo(gz_module,gzbo,accountingdate);
			if(StringUtils.isNotBlank(info)) {
				throw GeneralExceptionHandler.Handle(new Exception(info));
			}

			RecordVo tem_gz = gzbo.getTemplatevo();
			if(tem_gz==null||!tem_gz.getValues().containsKey("cname")||
					!gz_module.equalsIgnoreCase((tem_gz.getValues().containsKey("cstate")?
							(StringUtils.isBlank(tem_gz.getString("cstate"))?"0":tem_gz.getString("cstate")):"0"))){
				StringBuffer error=new StringBuffer("0".equalsIgnoreCase(gz_module)?ResourceFactory.getProperty("label.module.sama"):ResourceFactory.getProperty("label.module.bxfl"));
				error.append(ResourceFactory.getProperty("gz_new.gz_accounting.salaryIdNotExists").replace("{0}",salaryid));
				throw GeneralExceptionHandler.Handle(new Exception(error.toString()));//薪资发放不存在?号类别
			}
		    if(accountingdate==null||accountingdate.length()==0){
		    	LazyDynaBean bean = this.searchBusinessDate(salaryid, gzbo.getManager().length()==0?this.userView.getUserName():gzbo.getManager());
				String strYm = bean.get("strYm") != null ? (String)bean.get("strYm") : "";
				String strC = bean.get("strC") != null ? (String)bean.get("strC") : "";
				if(strYm!=null&&strYm.length()>0){
					accountingdate = strYm.substring(0, 7);
					accountingcount = strC;
				}
		    }
		    if(StringUtils.isBlank(accountingdate)&&StringUtils.isBlank(accountingcount)&&gzbo.getManager()!=null
		    		&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager())){
		    	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.notcreatetable")));//该类别管理员还未开始业务处理！
		    }

		    SalarySetBo setbo = new SalarySetBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		    ArrayList itemList=gzbo.getSalaryItemList("",""+salaryid,1);
			gzbo.SalarySet(itemList); //判断哪些字段改变了需要同步

			SalaryPageLayoutBo pageBo = new SalaryPageLayoutBo(this.frameconn,this.userView);
			String  verify_ctrl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
				verify_ctrl="0";
			if("1".equals(verify_ctrl))
			{
				String verify_ctrl_ff=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_ff");//审核控制薪资发放
				if(verify_ctrl_ff!=null&&verify_ctrl_ff.length()>0)
					verify_ctrl=verify_ctrl_ff;
			}
			String isTotalControl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isTotalControl))
			{
				String amount_ctrl_ff=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");//总额控制薪资发放
				if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
					isTotalControl=amount_ctrl_ff;
			}
			String ctrlType = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type");//总额 1强行控制 0仅提示
			if(ctrlType==null||ctrlType.trim().length()==0)
				ctrlType="1";

			ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
			ArrayList fieldlist=setbo.searchGzItem();
			StringBuffer privStr=new StringBuffer();
			ArrayList fieldsArray=new ArrayList();//复杂方案查询组件中的字段信息集合
			StringBuffer sqlstr = new StringBuffer();
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item=(FieldItem)fieldlist.get(i);

				if("nbase".equalsIgnoreCase(item.getItemid())){//人员库特殊处理   _nbase加密用于传递
					sqlstr.append(",");
					sqlstr.append("upper(" + item.getItemid().toLowerCase());
					sqlstr.append(") as " + item.getItemid().toLowerCase());
					sqlstr.append(",");
					sqlstr.append("upper(" + item.getItemid().toLowerCase() + ")");
					sqlstr.append(" as nbase1");
				}else if("a00z0".equalsIgnoreCase(item.getItemid())|| "a00z1".equalsIgnoreCase(item.getItemid())){
					sqlstr.append(",");
					sqlstr.append(item.getItemid().toLowerCase());
					sqlstr.append(",");
					sqlstr.append(item.getItemid().toLowerCase());
					sqlstr.append(" as "+item.getItemid().toLowerCase()+"1");//用于存放归属日期和归属次数的原始数据 zhaoxg add 2016-9-22
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
				if(item.isVisible())
					privStr.append(item.getItemid()+",");
			}
			if(privStr.length()>0)
				privStr.deleteCharAt(privStr.length()-1);
			//system.properties  salaryitem=false前台计算项不能编辑
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				gzbo.setFieldlist_readOnly(fieldlist);

			column = setbo.toColumnsInfo(fieldlist);//页面显示字段

			String tableName = gzbo.getGz_tablename();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
			DbWizard dbw=new DbWizard(this.frameconn);
			boolean flag = false;
			if(!"0".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0&&isExistFieldItem(salaryid,onlyname)){
				flag = true;
			}
			String lookStr = this.getLookStr(uniquenessvalid, onlyname, flag);//页面 查询框内默认显示汉字内容
			ArrayList<String> valuesList = (ArrayList) this.getFormHM().get("inputValues");//页面查询框返回的内容
			String condSql = this.getCondSql(subModuleId, valuesList, uniquenessvalid, onlyname, "salary_"+salaryid, flag);//查询框返回内容生成的sql片段

			if(!dbw.isExistTable(tableName,false)) //20160816 没有创建表时进入报SQL错
			{
				Table table=new Table(tableName);
				SalaryTableStructBo salaryTableStructBo=new SalaryTableStructBo(this.getFrameconn(),this.getUserView());
				//根据工资类别id得到类别下面的所有项目列表
			//	ArrayList itemList=gzbo.getSalaryItemList("",""+salaryid,1);
				//获得临时变量指标列表（过滤薪资帐套不用的临时变量）
				ArrayList midList=gzbo.getMidVariableListByTable(""+salaryid);
				salaryTableStructBo.createGzDataTable(tableName,itemList,midList,gzbo.getCtrlparam());

			}
			gzbo.updateSalaryTable(tableName, setbo);



			StringBuffer buf=new StringBuffer();
			buf.append("select dbid,"+sqlstr.substring(1)+" from ");//此处加上dbid  否则过滤的时候表格工具会报错
			buf.append(tableName);
			buf.append(" where 1=1 ");
			if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager())) {//共享非管理员
				buf.append(gzbo.getWhlByUnits(tableName, true));
				buf.append(aorgbo.getSalarySql(accountingdate, gz_module));
			}

			if(condSql.length()>0){//页面模糊查询
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get( "salary_"+salaryid);
				tableCache.setQuerySql(" and ( "+condSql.replaceAll("data.", "")+" ) ");//去掉表名，防止表格工具追加后报错
				this.userView.getHm().put(SafeCode.encode(PubFunc.encrypt( "salary_"+salaryid)), tableCache);
				return;
			}

			String relation_id=gzbo.getSpRelationId();//审批关系
			String spActorName = "";//如果此人只有一个领导那么报批按钮上面显示 报[xxx]审批 否则单纯显示报批
			String  sp_actor_str="";
			if(relation_id.length()>0)
				sp_actor_str=gzbo.getSpActorStr(relation_id,0);//审批关系中定义的直接领导
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
						buttonMap.put("appflag", "true");//true：显示报批按钮 false：不显示
					else
						buttonMap.put("appflag", "false");
				}
				else
					buttonMap.put("appflag", "true");
			}
			else
				buttonMap.put("appflag", "false");

			String bedit = "true";// true：显示提交按钮  false：不显示
			if(!"1".equalsIgnoreCase(flow_flag))
				bedit = "true";
			else if("1".equalsIgnoreCase(flow_flag)&&this.isSubCondition(tableName))
				bedit = "true";
			else
				bedit = "false";
			buttonMap.put("bedit", bedit);
			buttonMap.put("lookStr", lookStr);
			buttonMap.put("viewtype", viewtype);
			buttonMap.put("gz_module", gz_module);
			buttonMap.put("returnflag", returnflag);
			String orderby = "  order by dbid, a0000, A00Z0, A00Z1 ";
	//		TableConfigBuilder builder = new TableConfigBuilder(SafeCode.encode(PubFunc.encrypt(tableName)), column, "salaryaccounting", userView,this.getFrameconn());
			String isRedo="0";//是否薪资重发数据
			if(gzbo.getManager()==null||gzbo.getManager().length()==0||this.userView.getUserName().equals(gzbo.getManager()))
			{
				if(accountingdate!=null&&accountingdate.trim().length()>0&&accountingcount!=null&&accountingcount.trim().length()>0)
				{
					if(gzbo.getIsRedo(accountingdate,accountingcount,salaryid,this.userView.getUserName()))
						isRedo="1";
				}
			}
			//subModuleId 传"salary_"+salaryid 用于保证 公有栏目设置方案可作用于多个用户。 zhanghua 2017-4-10
			String tablesubModuleId="salary_"+salaryid;
			TableConfigBuilder builder = new TableConfigBuilder(tablesubModuleId, column, "salaryaccounting", userView,this.getFrameconn());
			builder.setDataSql(buf.toString());
			builder.setOrderBy(orderby);
			builder.setSchemePrivFields(privStr.toString());
			builder.setScheme(true);
			if(!"1".equals(viewtype))
				builder.setAutoRender(true);
			builder.setTitle(gzbo.getTemplatevo().getString("cname"));
			builder.setSetScheme(true);
			if((!"1".equals(gz_module)&&userView.hasTheFunction("324020606"))||("1".equals(gz_module)&&userView.hasTheFunction("325020606"))||("2".equals(viewtype)&&userView.hasTheFunction("031407")))
				builder.setShowPublicPlan(true);
			builder.setLockable(true);
			builder.setEditable(true);
			builder.setSelectable(true);
			builder.setColumnFilter(true);
			builder.setSchemeSaveCallback("GzGlobal.reloadStore");
			builder.setPageSize(20);
			builder.setFieldAnalyse(true);
			builder.setTableTools(pageBo.getSalaryAccountingButtonList(gzbo,gz_module,accountingdate,accountingcount,salaryid,buttonMap,isRedo));
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			this.getFormHM().put("fieldsArray", fieldsArray.toArray());
			this.getFormHM().put("lookStr", lookStr);
			this.getFormHM().put("gz_module_encrypt", SafeCode.encode(PubFunc.encrypt(gz_module)));
			this.getFormHM().put("viewtype_encrypt", SafeCode.encode(PubFunc.encrypt(viewtype)));
			this.getFormHM().put("salaryid_encrypt", SafeCode.encode(PubFunc.encrypt(salaryid)));
			this.getFormHM().put("verify_ctrl", verify_ctrl);
			this.getFormHM().put("isTotalControl", isTotalControl);
			this.getFormHM().put("ctrlType", ctrlType);
			this.getFormHM().put("tablesubModuleId", tablesubModuleId);
			this.getFormHM().put("sp_actor_str", SafeCode.encode(sp_actor_str));
			this.getFormHM().put("isNotSpFlag2Records", gzbo.getIsNotSpFlag2Records());//薪资发放临时表中是否还含有没报审的记录 0:没有 1：有
			this.getFormHM().put("tar", this.userView.getBosflag());//hl:70前页面  hcm：70及以后页面
			this.getFormHM().put("datetime", accountingdate);
			this.getFormHM().put("appdate", SafeCode.encode(PubFunc.encrypt(accountingdate)));
			this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt(accountingcount)));
			this.getFormHM().put("returnflag", returnflag);
			this.getFormHM().put("cbase",gzbo.getTemplatevo().getString("cbase"));
			this.getFormHM().put("ishave", gzbo.isHaveUnits());

			//========================================手工引入权限控制==========================================
            StringBuffer unitcodes = new StringBuffer();
            //1 走人员范围加高级
            String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
            //手工引入是否走人员范围加高级 0不走 1走
            String handImportScope = "0";
            //人员引入是否按权限范围控制 1 可以引入权限外
			String priv_mode_flag=gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有
            String handImportPer = ((!"1".equals(gz_module) && this.userView.hasTheFunction("324021201") == true) || ("1".equals(gz_module) && this.userView.hasTheFunction("325021201") == true)) ? "1" : "0";
            if ("0".equals(handImportPer) &&"1".equals(priv_mode_flag)) {
            	// 对于共享账套，非共享管理员设置了该参数才只走人员范围+高级
                if (StringUtils.isNotBlank(gzbo.getManager()) && !gzbo.getManager().equalsIgnoreCase(this.userView.getUserName()) 
                		&& !this.userView.isSuper_admin() && "1".equals(unitIdByBusiOutofPriv)) {
                    handImportScope="1";
                } else  {
                    String unitcode = this.userView.getUnitIdByBusiOutofPriv("1");
                    if (StringUtils.isNotBlank(unitcode) && !"UN`".equalsIgnoreCase(unitcode)) {
                        String[] temps = unitcode.split("`");
                        for (int i = 0; i < temps.length; i++) {
                            if (temps[i].trim().length() > 0) {
                                unitcodes.append("," + temps[i].substring(2));
                            }
                        }
                        unitcodes.deleteCharAt(0);
                    }
                    handImportScope = "0";
                    // 如果业务范围+操作单位没有值，走高级
                    if(unitcodes.length() == 0) {
                    	handImportScope = "1";
                    }
                }
            }

            this.getFormHM().put("handImportScope", handImportScope);
            this.getFormHM().put("unitcodes", unitcodes.toString());//业务范围，在设置可引入管理范围外的人权限的时候

            //========================================手工引入权限控制结束==========================================

			String onlyNameDesc="";
			if(!"0".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0)
				onlyNameDesc=DataDictionary.getFieldItem(onlyname).getItemdesc();
			this.getFormHM().put("onlyNameDesc", onlyNameDesc);//按方案导入需使用 唯一性指标
			String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";

			String allowEditSubdata=gzbo.getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE); //允许提交后更改数据 "0"不可以修改，"1"可以修改
			if(StringUtils.isBlank(allowEditSubdata))
			    allowEditSubdata="0";
			if("1".equalsIgnoreCase(flow_flag))
				allowEditSubdata="0";
			//是否是共享管理员,'1':是共享管理员 '0':不是
			String sharedAdministratorFlag = "0";
			if(this.userView.getUserName().equalsIgnoreCase(gzbo.getManager())) {
				sharedAdministratorFlag = "1";
			}
			//允许提交后更改数据 "0"不可以修改，"1"可以修改
			this.getFormHM().put("allowEditSubdata", allowEditSubdata);
			//是否是共享管理员,'1':是共享管理员 '0':不是
			this.getFormHM().put("sharedAdministratorFlag", sharedAdministratorFlag);
			this.getFormHM().put("bedit", bedit);

			this.getFormHM().put("isRedo",isRedo);
			if("1".equals(isRedo))
				subNoShowUpdateFashion="1";
			this.getFormHM().put("subNoShowUpdateFashion", subNoShowUpdateFashion);//是否显示提交方式窗口 0：显示 1：不显示

			/**
			 * 获取常用报表
			 */
			if((!"1".equals(gz_module)&& "0".equals(viewtype)&&this.getUserView().hasTheFunction("324020503"))
					||("1".equals(gz_module)&& "0".equals(viewtype)&&this.getUserView().hasTheFunction("325020503"))
					||("2".equals(viewtype)&&this.getUserView().hasTheFunction("031403"))){
				SalaryReportBo salaryReportBo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
				ArrayList list=salaryReportBo.listCommonReport(gz_module,"0");
				this.getFormHM().put("commonreportlist",list);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 是否有可报批的数据
	 * @return  0:无可报批的数据  1：有可报批的数据
	 */
	public String getIsAppealData(String tableName)throws GeneralException
	{
		String isData="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
	//		RecordVo vo=new RecordVo(tableName.toLowerCase());
	//		if(vo.hasAttribute("sp_flag"))
			{
				RowSet rowSet=dao.search("select count(A0100) from "+tableName+" where Sp_flag='01' or Sp_flag='07'");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
						isData="1";
				}
				rowSet.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return isData;
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
			if("salaryAccounting".equals(subModuleId)){
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
	/**
	 * 判断是否满足提交数据条件（审批流程的工资类别）
	 *   如果所有记录都为结束则不允许再确认,
	 *   如果所有记录为已批或 有的是已批有的是确认,则可以提交
	 * @author zhaoxg 2016-7-13
	 * @return
	 */
	public boolean isSubCondition(String tableName)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer sql=new StringBuffer("select a,b,c from ");
			sql.append(" (select count(a0100) a from "+tableName+" where sp_flag='06' ) aa, ");
			sql.append(" (select count(a0100) b from "+tableName+" ) bb,");
			sql.append(" (select count(a0100) c from "+tableName+" where sp_flag='06' or sp_flag='03'  ) cc");
			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				int a=rowSet.getInt("a");
				int b=rowSet.getInt("b");
				int c=rowSet.getInt("c");
				if(a==b)
					flag=false;
				else if(b==c)
					flag=true;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 根据当前用户，查找处理的业务日期和次数
	 * 1、发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 2、发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * @param salaryid 薪资类别编号
	 * @param username 用户名
	 * @return chent
	 */
	private LazyDynaBean searchBusinessDate(String salaryid, String username) {

		LazyDynaBean businessDateBean = new LazyDynaBean();
		String strYm = "";
		String strC = "";
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			// <<薪资发放记录表>>中发放日期、发放次数
			// 发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
			String sql = "select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid=? and upper(username)=?";
			ArrayList list = new ArrayList();
			list.add(salaryid);
			list.add(username.toUpperCase());
			rowSet = dao.search(sql, list);
			if (rowSet.next()) {
				strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),
						"yyyy-MM-dd");
				strC = rowSet.getString("A00z3");
			} else {

				DbWizard dbWizard = new DbWizard(this.frameconn);
				if (dbWizard.isExistTable(username+"_salary_"+salaryid, false)) {
					sql="select  a00z2,a00z3 from "+username+"_salary_"+salaryid;
					rowSet = dao.search(sql, new ArrayList());
					if (rowSet.next()) {
						if (rowSet.getDate("A00z2") != null) {
							strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),"yyyy-MM-dd");
							strC = rowSet.getString("A00Z3");
							businessDateBean.set("strYm", strYm);
							businessDateBean.set("strC", strC);
							return businessDateBean;
						}
					}
				}


				// 发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
				sql = "select max(A00z2) A00z2 from gz_extend_log where  salaryid=? and upper(username)=?";
				list.clear();
				list.add(salaryid);
				list.add(username.toUpperCase());
				rowSet = dao.search(sql, list);
				if (rowSet.next()) {
					if (rowSet.getDate("A00z2") != null) {
						strYm = PubFunc.FormatDate(rowSet.getDate("A00z2"),
								"yyyy-MM-dd");
					} else {
						strYm = "";
					}
				}
				if ("".equalsIgnoreCase(strYm)) {
					strC = "";
				} else {
					sql = "select max(A00Z3) A00Z3 from gz_extend_log  where salaryid=? and  upper(username)=? and A00Z2=?";
					list.clear();
					list.add(salaryid);
					list.add(username.toUpperCase());

					Date date = null;
					if(StringUtils.isNotBlank(strYm)){
						String dateStr = strYm;
						if(dateStr.indexOf("-")<0)
							date = DateUtils.getSqlDate(strYm,"yyyy.MM.dd");
						else
							date = DateUtils.getSqlDate(strYm,"yyyy-MM-dd");
						list.add(date);
					}else{
						list.add(date);
					}

					rowSet = dao.search(sql, list);
					if (rowSet.next()) {
						strC = rowSet.getString("A00Z3");
					}
				}
			}
			businessDateBean.set("strYm", strYm);
			businessDateBean.set("strC", strC);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
		return businessDateBean;
	}
	/**
	 *
	 * @Title: getValByStr
	 * @Description:获取加密参数中的链接
	 * @param @param url
	 * @param @param str
	 * @param @return
	 * @return String
	 * @author:zhaoxg
	 * @throws
	 */
	private String getValByStr(String url, String str) {
		String val = "";
		try {
			url = PubFunc.decrypt(url);
			String _url = url.substring(1);
			String[] strs = _url.split("&");
		    for(int i = 0; i < strs.length; i++) {
		    	String param = strs[i];
		    	String[] params=param.split("=");
		    	if (params.length>1&&params[0].equalsIgnoreCase(str))
		    		val=params[1];
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * 查找出唯一性指标是否在salaryset表中存在
	 * @param salaryid
	 * @return
	 */
	private boolean isExistFieldItem(String salaryid, String onlyName) {
		boolean isExist = false;
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select 1 from salaryset where salaryid=? and upper(itemid)=?";
			ArrayList list = new ArrayList();
			list.add(salaryid);
			list.add(onlyName.toUpperCase());
			rowSet = dao.search(sql, list);
			if (rowSet.next()) {
				isExist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}

		return isExist;
	}
}
