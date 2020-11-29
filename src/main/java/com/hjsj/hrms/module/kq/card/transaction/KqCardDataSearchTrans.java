package com.hjsj.hrms.module.kq.card.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
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

/**
 * 打卡数据页面快速查询
 * @Title:        KqCardDataSearchTrans.java
 * @Description:  打卡数据页面快速查询生成sql条件的交易类
 * @Company:      hjsj     
 * @Create time:  2019年8月20日 下午4:42:17
 * @author        chenxg
 * @version       7.5
 */
public class KqCardDataSearchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = "kqCardData_01";
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
					ArrayList<ColumnsInfo> columnsList = catche.getTableColumns();
					HashMap map = new HashMap();
					for(int i=0;i<columnsList.size();i++) {
						ColumnsInfo col = columnsList.get(i);
						if(null == col) {
							continue;
						}
						String columnId = col.getColumnId();
						FieldItem fi = new FieldItem();
						fi.setItemid(columnId);
						fi.setItemdesc(col.getColumnDesc());
						fi.setItemtype(col.getColumnType());
						fi.setFieldsetid(col.getFieldsetid());
						fi.setCodesetid(col.getCodesetId());
						fi.setItemlength(col.getColumnLength());
						fi.setUseflag("1");
						map.put(columnId, fi);
					}
					// 58727 刷卡表 非业务字典中指标 需要 组map手工传入 
					FactorList parser = new FactorList(exp, cond, userView.getUserName(), map);
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

			if (StringUtils.isNotEmpty(pinyin) && item != null && "1".equalsIgnoreCase(item.getUseflag())) {
				where.append(" or exists (select 1 from (");
				for (int a = 0; a < dbNameList.size(); a++) {
					String dbname = dbNameList.get(a);
					if (a > 0)
						where.append(" union ");

					where.append("select '" + dbname + "' nbase,a0100 from ");
					where.append(dbname + "a01 where " + pinyin);
					where.append(" like '%" + value + "%'");
				}

				where.append(") b");
				where.append(" where myGridData.nbase=b.nbase and myGridData.a0100=b.a0100)");
			}
		}

		if (where == null || where.length() < 1)
			where.append("");

		return where.toString();
	}
}
