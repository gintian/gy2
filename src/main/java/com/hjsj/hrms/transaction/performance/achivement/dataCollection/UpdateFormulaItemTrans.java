package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:UpdateFormulaItemTrans.java</p>
 * <p>Description:更新P04表中通过公式来得到值的字段</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-16 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class UpdateFormulaItemTrans extends IBusiness
{
	public void execute() throws GeneralException
	{

		HashMap hm = this.getFormHM();
		ArrayList list = (ArrayList) hm.get("data_table_record");
		String plan_id = "";
		// 保存更新的字段
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if (list.size() > 0)
			{
				RecordVo vo = (RecordVo) list.get(0);
				plan_id = vo.getString("plan_id");
			}

			String targetTraceItem = "";// 可以更新数据的指标

			// 取得目标跟踪显示和采集指标
			// 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
			String targetCalcItem = ""; // 目标卡计算指标属性，P04中指标，以逗号分隔，顺序从前到后			

			if ("true".equals(targetTraceEnabled))
			{
				targetTraceItem = (String) params.get("TargetTraceItem");
				if (params.get("TargetCalcItem") != null && ((String) params.get("TargetCalcItem")).trim().length() > 0)
					targetCalcItem = ((String) params.get("TargetCalcItem")).trim();
			} else
			// 2.从绩效模块参数配置中取目标跟踪显示和采集指标
			{
				ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
				targetTraceItem = configParamBo.getTargetTraceItem();
				targetCalcItem = configParamBo.getTargetCalcItem();
			}

			StringBuffer buf = new StringBuffer();
			buf.append("update p04 set ");
			ArrayList fieldValues = new ArrayList();
			ArrayList fieldList = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
			for (int i = 0; i < list.size(); i++)
			{
				ArrayList fieldValue = new ArrayList();
				RecordVo vo = (RecordVo) list.get(i);
				int p0400 = vo.getInt("p0400");
				for (int j = 0; j < fieldList.size(); j++)
				{
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemid = item.getItemid();
					String itemtype = item.getItemtype();
					String codesetid = item.getCodesetid();
					if (targetTraceItem.toLowerCase().indexOf(itemid.toLowerCase()) != -1)
					{
						if (i == 0)
							buf.append(itemid + "=?,");
						if ("A".equals(itemtype) || "M".equals(itemtype))
							fieldValue.add(vo.getString(itemid));
						else if ("D".equals(itemtype))
							fieldValue.add(vo.getDate(itemid));
						else if ("N".equals(itemtype))
						{
							fieldValue.add(new Double(vo.getDouble(itemid)));
							if ("p0419".equalsIgnoreCase(itemid))
							{
								if (vo.getDouble(itemid) > Double.parseDouble("100") || vo.getDouble(itemid) < Double.parseDouble("0"))
									throw new GeneralException("[" + item.getItemdesc() + "]列请输入0-100之间的数值！");
							}

						}

					}
				}
				fieldValue.add(new Integer(p0400));
				fieldValues.add(fieldValue);
			}
			buf.setLength(buf.length() - 1);
			buf.append(" where p0400=?");
			dao.batchUpdate(buf.toString(), fieldValues);

			// 更新定义了公式的字段
			if (targetCalcItem.length() > 0)
			{
				String[] temps = targetCalcItem.split(",");
				fieldList = (ArrayList) DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET).clone();
				FieldItem item = new FieldItem();
				item.setItemid("per_target_evaluation.score");
				item.setItemdesc("评分");
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				fieldList.add(item);
				for (int i = 0; i < temps.length; i++)
				{
					if (temps[i].length() > 0)
					{
						this.frowset = dao.search("select expression,itemtype from t_hr_busiField  where upper(fieldsetid)='P04'  and upper(itemid)='" + temps[i].toUpperCase() + "' ");
						if (this.frowset.next())
						{
							String expression = Sql_switcher.readMemo(this.frowset, "expression");
							String itemtype = this.frowset.getString("itemtype");
							int y_type = YksjParser.FLOAT;
							if ("A".equalsIgnoreCase(itemtype))
								y_type = YksjParser.STRVALUE;
							if ("D".equalsIgnoreCase(itemtype))
								y_type = YksjParser.DATEVALUE;
							if (expression.trim().length() > 0)
							{
								YksjParser yp = new YksjParser(this.userView, fieldList, YksjParser.forNormal, y_type, YksjParser.forPerson, "Ht", "");
							    if(expression.indexOf("执行存储过程") != -1){//如果是执行计算分值存储过程
                                    continue;
                                }
								yp.run(expression, this.frameconn, "", "p04");
								String formular_sql = yp.getSQL();
								if (!"task_score".equalsIgnoreCase(temps[i].trim()))
								{
									if (formular_sql.toLowerCase().indexOf("per_target_evaluation.score") == -1)
									{
										dao.update("update p04 set " + temps[i] + "=(" + formular_sql + ") where plan_id=" + plan_id + "   and ( p04.chg_type<>3 or p04.chg_type is null ) ");
									}
								} else
								{

								}

							}
						}
					}
				}
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
