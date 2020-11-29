package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataMxServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

/**
 *  获取考勤数据明细
 *  create time   2018-10-25
 * @author haosl
 *
 */
public class GetKqDataMxTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		try {
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 为空：初次进入页面 ；不为空：快速查询
			//输入查询
			if(StringUtils.isNotBlank(subModuleId) && subModuleId.startsWith("kqdata_")) {
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
				String type = (String)this.getFormHM().get("type");
				if("1".equals(type)) {// 1:输入查询
					StringBuilder querySql = new StringBuilder();
					ArrayList<String> valuesList = new ArrayList<String>();
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
					// 快速查询
					if (valuesList != null && valuesList.size() > 0) {
						querySql.append(" and (");
					}
					for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
						String queryVal = valuesList.get(i);
						queryVal = SafeCode.decode(queryVal);// 解码
						queryVal = PubFunc.hireKeyWord_filter(queryVal);
						if (i != 0) {
							querySql.append("or ");
						}
						querySql.append("(a0101 like '%" + queryVal+"%' or only_field like '%"+queryVal+"%')");
					}
					if (valuesList != null && valuesList.size() > 0) {
						querySql.append(" ) ");
					}
					catche.setQuerySql(querySql.toString());
				}else if("2".equals(type)){//方案查询
					StringBuilder querySql = new StringBuilder();
					HashMap queryFields = catche.getQueryFields();//haosl 20161014方案查询可以查询自定义指标
					String exp = (String) this.getFormHM().get("exp");
					exp = SafeCode.decode(exp);
					exp=PubFunc.keyWord_reback(exp);
					String cond = (String) this.getFormHM().get("cond");
					cond = SafeCode.decode(cond);
					cond = cond.replaceAll("＜", "<");
					cond = cond.replaceAll("＞", ">");
					if(cond.length()<1 || exp.length()<1){
						catche.setQuerySql(querySql.toString());
						return;
					}
					querySql.append(" and ");
					FactorList parser = new FactorList(exp,cond,userView.getUserName(),queryFields);//haosl 方案查询可以查询自定义指标
					querySql.append(parser.getSingleTableSqlExpression("myGridData"));
					catche.setQuerySql(querySql.toString());
				}
				return;
			}
			
			String jsonStr = (String)this.formHM.get("jsonStr");
			//获取前台json数据
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);
			KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.frameconn);
			String type = jsonObj.getString("type");
			if("pclist".equals(type)) {
				//初始化表结构（省去OptKqDataMxTrans中initTables）
	            DbWizard dbWizard = new DbWizard(this.frameconn);
	            //维护表结构，没有创建时间时加上
	            if(!dbWizard.isExistField("kq_day_detail","create_time",false)){
	                Table table = new Table("kq_day_detail");
	                Field obj = new Field("create_time", "create_time");
	                obj.setDatatype(DataType.DATETIME);
	                obj.setKeyable(false);
	                obj.setVisible(false);
	                obj.setAlign("left");
	                table.addField(obj);
	                dbWizard.addColumns(table);
	            }
	            
				String scheme_id = jsonObj.getString("scheme_id");
				//获取考勤方案中配置的统计指标
				String sumsVal=service.getSumsBySchemeCheck(scheme_id);
				if(StringUtils.isNotBlank(scheme_id))
					scheme_id = PubFunc.decrypt(scheme_id);
				SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.getFrameconn(), this.userView);
				ArrayList parameterList = new ArrayList();
				parameterList.add(scheme_id);
	            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
	            LazyDynaBean schemeBean = schemeList.get(0);
	            // 查询是否显示日明细
	            Object dayDetailEnabled = schemeBean.get("day_detail_enabled");
	            String showMx = (dayDetailEnabled == null || Integer.parseInt(String.valueOf(dayDetailEnabled))==0)?"false":"true";
	            String schemeName = (String)schemeBean.get("name");
				String tableConfigMx = "";
				String tableConfigNoneMx ="";
				// 员工确认考勤结果 1:生效
	            String o = String.valueOf(schemeBean.get("confirm_flag"));
	            Integer confirmFlag = "null".equalsIgnoreCase(o) || o.length()==0?0:Integer.parseInt(o);
	            String viewtype = jsonObj.getString("viewtype");
	            KqDataUtil kqDataUtil = new KqDataUtil(this.userView, this.frameconn);
				//查询当前期间下属于变动岗的人员，日明细列可以编辑的区域，其他列一律不允许编辑
				String kq_duration = jsonObj.getString("kq_duration");
				String kq_year = jsonObj.getString("kq_year");
				String org_id = jsonObj.getString("org_id");
				// 获得当前登陆人的考勤角色--同级角色都是一个用户后续处理
				int role = kqDataUtil.getKqRole(viewtype, schemeBean);
				if(StringUtils.isNotBlank(org_id) && org_id.split(",").length==1){
					org_id = PubFunc.decrypt(org_id);
					Map<String, List<String>> changePerData = service.searchChangePerData(kq_year, kq_duration, org_id, scheme_id);
					this.formHM.put("changePerData", changePerData);
				}
				// 45783 为防止更改方案中是否上报参数  则 重新校验  是否有下级审批人
	            boolean isNextLevel = kqDataUtil.isKqRoleNextLevel(viewtype, schemeBean, role, org_id);
	            String hasNextApprover = isNextLevel ? "1" : "0";
	            this.formHM.put("hasNextApprover", hasNextApprover);
	            // 加入数据库参数
	            jsonObj.put("cbase", String.valueOf(schemeBean.get("cbase")));
				if("true".equals(showMx)) {
					jsonObj.put("showMx", "true");
					tableConfigMx = service.getTableConfig(jsonObj, true, confirmFlag, showMx, role);
					jsonObj.put("showMx", "false");
					tableConfigNoneMx = service.getTableConfig(jsonObj, true, confirmFlag, showMx, role);
				}else {
					jsonObj.put("showMx", "false");
					tableConfigNoneMx = service.getTableConfig(jsonObj, false, confirmFlag, showMx, role);
				}
				String schemeId = jsonObj.getString("scheme_id");
				List<LazyDynaBean> schemeClassAndItemsList = new ArrayList<LazyDynaBean>();
				List<LazyDynaBean> allClassAndItems = new ArrayList<LazyDynaBean>();
				if(StringUtils.isNotBlank(schemeId)) {
					schemeClassAndItemsList = service.getClassAndItemsOrder(schemeId, "0");
				}
				this.formHM.put("classAndItems",schemeClassAndItemsList);
				// 获取全部班次和项目
				allClassAndItems = service.getAllClassAndItems();
		        this.formHM.put("schemeName", schemeName);
				this.formHM.put("allClassAndItems",allClassAndItems);
				if(StringUtils.isNotBlank(tableConfigMx)) {
					this.formHM.put("tableConfigMx",tableConfigMx);
				}
				if(StringUtils.isNotBlank(tableConfigNoneMx)) {
					this.formHM.put("tableConfigNoneMx",tableConfigNoneMx);
				}
				this.formHM.put("showMx",showMx);
				this.formHM.put("confirmFlag", confirmFlag);
	            // 上报页面增加功能授权控制
				JSONObject priv = new JSONObject();
				// 计算
	    		priv.put("computep", (this.userView.hasTheFunction("272030101")) ? "1" : "0");
	    		this.formHM.put("privs", priv);
	    		// 应急中心个性化标识
            	String hlwyjzx_flag = "hlwyjzx".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()) ? "1" : "0";
            	this.formHM.put("hlwyjzx_flag", hlwyjzx_flag);
            	// 52285 机构审核人加密（nbase+a0100）
            	String currentUser = "";
            	if(StringUtils.isNotBlank(org_id) && org_id.split(",").length==1){
            		ArrayList<HashMap> orgMaplist = (ArrayList<HashMap>)schemeBean.get("org_map");
            		for(int i=0;i<orgMaplist.size();i++) {
            			HashMap orgMap = orgMaplist.get(i);
            			if(org_id.equals(String.valueOf(orgMap.get("org_id"))))
            				currentUser = PubFunc.encrypt((String) orgMap.get("reviewer_id"));
            		}
            	}
            	this.formHM.put("currentUser", currentUser);
            	// 其他需要的参数
				JSONObject otherParam = new JSONObject();
				KqPrivForHospitalUtil privForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.frameconn);

				//设置显示考勤异常
				otherParam.put("exceptDisplay", enableExceptDisplay(viewtype, privForHospitalUtil));
				//获取个人更改的指标
				otherParam.put("confirmField", SystemConfig.getPropertyValue("confirm_memo").trim());
            	// 该期间的起止时间
            	LazyDynaBean bean = kqDataUtil.getDatesByKqDuration(this.frameconn, kq_year, kq_duration);
            	otherParam.put("kq_start", DateUtils.format((Date)bean.get("kq_start"), "yyyy.MM.dd"));
            	otherParam.put("kq_end", DateUtils.format((Date)bean.get("kq_end"), "yyyy.MM.dd"));
            	//考勤方案中配置的统计指标
            	otherParam.put("sumsVal", sumsVal);
            	/**
                 * 应急中心机构考勤员可以修改的备注指标
                 * 通过system参数取
                 */
                String memoFields = SystemConfig.getPropertyValue("hlwyjzx_memo").trim();
                otherParam.put("memoFields", memoFields.toLowerCase());
                // 获取当前环节的信息
                HashMap kqLogMap = kqDataUtil.getKq_extend_logInfo(frameconn, kq_year, kq_duration, org_id, scheme_id);
                otherParam.put("curr_user", kqLogMap.get("curr_user"));
                // 获取具有统计项的考勤项目
                String enableModifys = "";
                // 如果参数未勾选‘允许修改统计项计算项’返回空
                if(!"1".equals(privForHospitalUtil.getEnable_modify())){
                	for(LazyDynaBean one : allClassAndItems) {
                		if("1".equals((String)one.get("item_type"))) {
                			enableModifys += "," + (String)one.get("id");
                		}
                	}
                }
                otherParam.put("enableModifys", enableModifys);
                //填写审批意见 0：不填写（默认），1：需要填写意见
                String approvalMessage=privForHospitalUtil.getApprovalMessage();
                otherParam.put("approvalMessage", approvalMessage);
            	this.formHM.put("otherParam", otherParam);
	
			/**
			 * 修改保存的 方案设置的id，不让表格控件加载，而是自己查询栏目设置
			 */
			}else if("changeSchemeId".equalsIgnoreCase(type)){
				String scheme_id = PubFunc.decrypt(jsonObj.getString("scheme_id"));
				String subModuleIdPiff = jsonObj.getString("subModuleIdPiff");
				subModuleId = subModuleIdPiff+scheme_id;
				this.changeSchemeId(subModuleId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 是否启用出勤异常查看功能
	 * @param viewType 功能类型：0上报页面，1 审批页面
	 * @param privForHospitalUtil 公共权限参数对象
	 * @return true：启用，false: 禁用
	 */
	private boolean enableExceptDisplay(String viewType, KqPrivForHospitalUtil privForHospitalUtil) {
		//审批或上报页面授权了“出勤异常情况”功能权限，并且设置了卡号，并且请假、公出、加班子集设置了至少一个
		return (("0".equalsIgnoreCase(viewType) && userView.hasTheFunction("272030107"))
				|| ("1".equalsIgnoreCase(viewType) && userView.hasTheFunction("272030207")))
				&& StringUtils.isNotBlank(privForHospitalUtil.getKqCard_no()) && (
				StringUtils.isNotBlank(privForHospitalUtil.getLeave_setid())||
				StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_setid())
						||StringUtils.isNotBlank(privForHospitalUtil.getOvertime_setid()));
	}

	private void changeSchemeId(String subModuleId){
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append("select scheme_id from t_sys_table_scheme where submoduleid like ? and username=? order by scheme_id desc,is_share desc");
			List values = new ArrayList();
			values.add(subModuleId+"%");
			values.add(userView.getUserName());
			rs = dao.search(sql.toString(),values);
			int scheme_id = -1;
            values.clear();
			if(rs.next()) {
				scheme_id = rs.getInt("scheme_id");
                values.add(scheme_id);
			}
			if(values.size()>0) {
				sql.setLength(0);
				sql.append("update t_sys_table_scheme set submoduleid=submoduleid"+Sql_switcher.concat()+"'_onlysave' where scheme_id=?");
				dao.update(sql.toString(), values);
				//删除无用栏目设置
                sql.setLength(0);
                values.clear();
                values.add(subModuleId+"%");
                values.add(scheme_id);
                sql.append("delete from t_sys_table_scheme_item where scheme_id in (select scheme_id from  t_sys_table_scheme where submoduleid like ? and scheme_id<> ?)");
                dao.delete(sql.toString(),values);
                sql.setLength(0);
                sql.append("delete from t_sys_table_scheme where submoduleid like ? and scheme_id<> ?");
                dao.delete(sql.toString(),values);
			}
			//判断栏目设置中是否存在“only_field_”指标（only_field_ 为系统参数设置的唯一性指标别名）
			sql.setLength(0);
			sql.append("select 1 from t_sys_table_scheme_item");
			sql.append(" where scheme_id=? and itemid=?");
			values.clear();
			values.add(scheme_id);
			values.add("only_field_");
			this.frowset = dao.search(sql.toString(), values);
			if(this.frowset.next()) {
				//删除q35表中的唯一性指标
				values.clear();
				values.add(scheme_id);
				values.add("only_field");
				sql.setLength(0);
				sql.append("delete from t_sys_table_scheme_item");
				sql.append(" where scheme_id=? and itemid=?");
				dao.delete(sql.toString(), values);
				//将栏目设置保存的指标“only_field_”改为“only_field”
				sql.setLength(0);
				sql.append("update t_sys_table_scheme_item set itemid=?");
				sql.append(" where scheme_id=? and itemid=?");
				values.clear();
				values.add("only_field");
				values.add(scheme_id);
				values.add("only_field_");
				dao.update(sql.toString(), values);
			}

		} catch (Exception e) {
            e.printStackTrace();
		}finally {
		    PubFunc.closeDbObj(rs);
        }
    }

}
