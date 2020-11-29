package com.hjsj.hrms.module.kq.card.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class KqCardDataAnalysisSearchTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = "KqCardDataAnalysis_01";
			TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
			if (catche != null) {
				// 拼接sql
				StringBuffer sql = new StringBuffer();
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if ("1".equals(type)) {
					// 输入的内容
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");

					if (valuesList.size() > 0) {
						String where = getWhereSql(valuesList);
						if (!StringUtils.isEmpty(where))
							sql.append(" and (" + where + ")");
					}

				} else {
					sql.append(" and ");
					String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
					exp = PubFunc.keyWord_reback(exp);
					String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
					if (cond.length() < 1 || exp.length() < 1) {
						// 查询方案点击全部，刷新保存的快速查询sql
						if (catche.getCustomParamHM() != null)
							catche.getCustomParamHM().put("fastQuerySql", "");
						// 刷新userView中的sql参数
						catche.setQuerySql("");
						this.userView.getHm().put(subModuleId, catche);
						return;
					}

					HashMap<String, FieldItem> fieldItemMap = new HashMap<String, FieldItem>();
					FieldItem item = new FieldItem();
					item.setItemtype("D");
					item.setItemid("kq_date");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.kqDate"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("kq_date", item);

					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("guidkey");
					item.setUseflag("1");
					item.setItemdesc("");
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("guidkey", item);
					
					FieldItem fi = DataDictionary.getFieldItem("e0122");
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("e0122");
					item.setUseflag("1");
					item.setItemdesc(fi.getItemdesc());
					item.setCodesetid("UM");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("e0122",item);
					
					fi = DataDictionary.getFieldItem("e01A1");
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("e01A1");
					item.setUseflag("1");
					item.setItemdesc(fi.getItemdesc());
					item.setCodesetid("@K");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("e01A1", item);
					
					fi = DataDictionary.getFieldItem("a0101");
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("a0101");
					item.setUseflag("1");
					item.setItemdesc(fi.getItemdesc());
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("a0101", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("card_no");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.card_no"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("card_no", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("card_data");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.cardData"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("card_data", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("kq_status");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.status"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("kq_status", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_be_late_1");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.late"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_be_late_1", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_absent_1");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_absent_1", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_leave_early_1");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_leave_early_1", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_absent_1");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_absent_1", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_be_late_2");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.late"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_be_late_2", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_absent_2");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_absent_2", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_leave_early_2");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_leave_early_2", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_absent_2");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_absent_2", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_be_late_3");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.late"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_be_late_3", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("onduty_absent_3");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("onduty_absent_3", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_leave_early_3");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.leaveEarly"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_leave_early_3", item);
					
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("offduty_absent_3");
					item.setUseflag("1");
					item.setItemdesc(ResourceFactory.getProperty("kq.card.analysis.absent"));
					item.setCodesetid("0");
					item.setFieldsetid("kq_analysis_data");
					fieldItemMap.put("offduty_absent_3", item);
					
					FactorList parser = new FactorList(exp, cond, userView.getUserName(), fieldItemMap);
					sql.append(parser.getSingleTableSqlExpression("myGridData"));
				}

				catche.setQuerySql(sql.toString());
				// 保存快速查询条件备用
				if (catche.getCustomParamHM() == null)
					catche.setCustomParamHM(new HashMap<String, String>());

				catche.getCustomParamHM().put("fastQuerySql", sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 拼接 查询条件
	 * 
	 * @param valuelist
	 *            输入框中的输入的值
	 * @return
	 */
	private String getWhereSql(ArrayList<String> valuelist) {
		if (valuelist == null || valuelist.size() < 1)
			return "";

		StringBuffer where = new StringBuffer();
		ArrayList<String> dbNameList = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.frameconn);
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
		String pinyin = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		FieldItem item = DataDictionary.getFieldItem(pinyin.toLowerCase());
		for (int i = 0; i < valuelist.size(); i++) {
			String value = valuelist.get(i);
			if (StringUtils.isEmpty(value))
				continue;

			value = SafeCode.decode(value);
			if (StringUtils.isEmpty(value))
				continue;

			if (where != null && where.length() > 1)
				where.append(" or");

			where.append(" A0101 like '%" + value + "%'");
			where.append(" or card_no like '%" + value + "%'");
			
			if (StringUtils.isNotEmpty(pinyin) && item != null && !"0".equalsIgnoreCase(item.getUseflag())) {
				where.append(" or exists (select 1 from (");
				for (int a = 0; a < dbNameList.size(); a++) {
					String dbname = dbNameList.get(a);
					if (a > 0)
						where.append(" union ");

					where.append("select '" + dbname + "' nbase,a0100,guidkey from ");
					where.append(dbname + "a01 where " + pinyin);
					where.append(" like '%" + value + "%'");
				}

				where.append(") b");
				where.append(" where myGridData.guidkey=b.guidkey)");
			}
		}

		if (where == null || where.length() < 1)
			where.append("");

		return where.toString();
	}
}
