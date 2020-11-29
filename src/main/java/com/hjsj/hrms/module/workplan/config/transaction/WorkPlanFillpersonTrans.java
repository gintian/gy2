package com.hjsj.hrms.module.workplan.config.transaction;

import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取评委会组内人员信息
 * @createtime Dec 06, 2016 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class WorkPlanFillpersonTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		String opt = (String)this.getFormHM().get("opt");
		String type = (String) this.getFormHM().get("type");
		try {
			WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.getFrameconn(), this.userView);// 工具类
			if("queryPerson".equals(opt)){
				if(StringUtils.isEmpty(type)){//获取列表
					ArrayList fieldList = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);//人员主集
					
					ArrayList configList = workPlanConfigBo.getConfigList(new WorkPlanFunctionBo(this.getFrameconn()).getXmlData());
					ArrayList<ColumnsInfo> columnList = workPlanConfigBo.getColumnList(fieldList, configList);// 列头：主集+动态列
					String sql = workPlanConfigBo.getSql(fieldList, configList);//数据sql
					TableConfigBuilder builder = new TableConfigBuilder("workplan_fillperson", columnList, "workplan_fillperson", userView, this.getFrameconn());
					builder.setDataSql(sql);
					builder.setOrderBy(" order by a0000");
					builder.setTitle("填报人员");
					builder.setAutoRender(false);
					builder.setColumnFilter(true);//统计过滤
					builder.setScheme(true);//栏目设置
					builder.setSetScheme(true);
					builder.setShowPublicPlan(false);//公有
					builder.setLockable(true);
					builder.setSelectable(false);
					builder.setEditable(false);
					builder.setAnalyse(false);
					builder.setConstantName("workplan/fillperson");
					builder.setTableTools(workPlanConfigBo.getButtonList());
					builder.setPageSize(20);
					builder.setSchemeSaveCallback("workPlanConfig.schemeSaveCallback");
					String config = builder.createExtTableConfig();
					this.getFormHM().put("tableConfig", config.toString());
					
				}
			}
			else if("batchSetting".equals(opt)){//获取批量更新页面数据
				List<Map<String, String>> planList = workPlanConfigBo.getPlanConfig();
				this.getFormHM().put("data", planList);
			}else if("saveOne".equals(opt)){//单个设置
				String guidkey = this.getFormHM().get("guidkey")==null?"":(String)this.getFormHM().get("guidkey");
				String itemid= this.getFormHM().get("itemid")==null?"":(String)this.getFormHM().get("itemid");
				String value =  this.getFormHM().get("value")==null?"0":(String)this.getFormHM().get("value");
				if(StringUtils.isNotEmpty(guidkey)&&StringUtils.isNotEmpty(itemid)){
					workPlanConfigBo.saveSettings(guidkey,itemid,value);
				}
			}else if("batchSave".equals(opt)){//批量设置
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("workplan_fillperson");
				String filterSql = catche.getFilterSql();
				String querySql = catche.getQuerySql();
				String tableSql = catche.getTableSql();
				if(StringUtils.isNotBlank(filterSql))
					tableSql += filterSql;
				if(StringUtils.isNotBlank(querySql))
					tableSql += querySql;
				HashMap configMap = PubFunc.DynaBean2Map((MorphDynaBean)this.getFormHM().get("configMap"));
				workPlanConfigBo.batchSaveSettings(tableSql, configMap);
				this.getFormHM().put("msg","保存成功!");
			}else if(StringUtils.isBlank(opt)){
				//快速查询
				StringBuilder querySql = new StringBuilder(" ");
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("workplan_fillperson");
				if("workplan_fillperson".equals((String) this.getFormHM().get("subModuleId"))){// 模块id
					// 查询类型，1为输入查询，2为方案查询  
					if("1".equals(type)) {
						// 输入的内容
						ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
						// 快速查询
						StringBuilder newsql = new StringBuilder();
						for(int i = 0; i < valuesList.size(); i++){
							String queryVal = valuesList.get(i);
							queryVal = SafeCode.decode(queryVal);
							if(i == 0){
								newsql.append(" and (");
							}else{
								newsql.append(" or ");
							}
							String orgSql = "select codeitemid from organization where codeitemdesc like '%"+queryVal+"%'";
							newsql.append("b0110 in ("+orgSql+")");
							newsql.append(" or e0122 in ("+orgSql+")");
							newsql.append(" or e01a1 in ("+orgSql+")");
							newsql.append("or a0101 like '%"+queryVal+"%' ");
						}
						if(valuesList.size() > 0){
							newsql.append(" ) ");
							querySql.append(newsql.toString());
						}
					} else if ("2".equals(type)) {
						HashMap queryFields = catche.getQueryFields();
						String exp = (String) this.getFormHM().get("exp");
						String cond = (String) this.getFormHM().get("cond");
						if(!StringUtils.isEmpty(exp) && !StringUtils.isEmpty(cond)){//自定义检索方案
							// 解析表达式并获得sql语句
							FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
							querySql.append(" and " + parser.getSingleTableSqlExpression("data").replaceAll("data.", ""));
							
						}else {//全部
							querySql.setLength(0);
						}
					} 
				}
				
				catche.setQuerySql(querySql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
