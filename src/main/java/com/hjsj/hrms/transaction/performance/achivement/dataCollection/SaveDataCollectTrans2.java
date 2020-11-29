package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveDataCollectTrans.java</p>
 * <p>Description:切换指标时候先进行保存数据采集的操作</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-16 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveDataCollectTrans2 extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("planId");
		String oldPoint = (String) hm.get("oldPoint");// 保存旧指标当前页面数据
		String newPoint = (String) hm.get("newPoint");// 展示新指标页面
		String pointype = (String) this.getFormHM().get("pointype");
		String rule = (String) this.getFormHM().get("rule");

		hm.remove("planId");
		hm.remove("oldPoint");
		hm.remove("newPoint");

		String point = oldPoint;
		String objs = null;
		/** *************************保存旧指标当前页面数据**************************************************** */
		String paramStr = (String) this.getFormHM().get("paramStr");
		paramStr = PubFunc.keyWord_reback(paramStr);
		String[] params = null;
		if (paramStr != null && !"".equals(paramStr))
			params = paramStr.split("&");
		if (params != null && params.length > 0 && params[0].trim().length() > 0)
		{
			objs = params[0];
			DataCollectBo bo = new DataCollectBo(this.getFrameconn(), plan_id, point, this.userView);
			ArrayList allObjs = bo.getKhObjs();
			ArrayList nowPageObjs = new ArrayList();
			HashMap nowPageObjsMap = new HashMap();
			for (int i = 0; i < allObjs.size(); i++)
			{
				LazyDynaBean bean = (LazyDynaBean) allObjs.get(i);
				String objectId = (String) bean.get("object_id");
				if (objs.indexOf(objectId) != -1)
				{
					nowPageObjs.add(bean);
					nowPageObjsMap.put(objectId, bean);
				}
			}

			if (!"0".equals(pointype))// 加减分指标的保存
			{
				String scores = "";
				String dfScores = "";

				if (params != null)
				{
					scores = params[1];
					dfScores = params[2];
				}
				bo.save(scores.split("<@>"), dfScores.split("<@>"), nowPageObjs);
			} else if ("0".equals(pointype) && !"0".equals(rule)) // 基本指标非录分规则的保存
			{
				String standardVal = "";
				String praticalVal = "";
				String basicVal = "";
				String addVal = "";
				String deducVal = "";
				String dfScore = "";

				if (params != null)
				{
					standardVal = params[1];
					praticalVal = params[2];
					basicVal = params[3];
					addVal = params[4];
					deducVal = params[5];
					dfScore = params[6];
				}

				if ("3".equals(rule))// 排名的保存
				{
					StringBuffer standardVals = new StringBuffer(standardVal);
					StringBuffer praticalVals = new StringBuffer(praticalVal);
					StringBuffer basicVals = new StringBuffer(basicVal);
					// StringBuffer addVals = new StringBuffer(addVal);
					// StringBuffer deducVals = new StringBuffer(deducVal);
					// StringBuffer dfScores = new StringBuffer(dfScore);

					ArrayList allDataList = (ArrayList) this.getFormHM().get("setlist");
					HashMap basicValMap = bo.getBasicFen();
					HashMap standardFenMap = bo.getStandardFens();
					HashMap praticalValsMap = bo.getPraticalVals();

					for (int j = 0; j < allDataList.size(); j++)
					{
						LazyDynaBean bean = (LazyDynaBean) allDataList.get(j);
						String object_id = (String) bean.get("object_id");

						if (nowPageObjsMap.get(object_id) == null)// 把不在当前页面的对象数据也取出来进行整体排名
						{
							// String standVal1 = bo.getStandardFen(object_id);
							String standVal1 = (String) standardFenMap.get(object_id);
							standVal1 = standVal1 == null ? "0.0" : standVal1;
							// String praticalVal1 = bo.getPraticalVal(object_id);
							String praticalVal1 = (String) praticalValsMap.get(object_id);
							praticalVal1 = (praticalVal1 == null ? "0" : praticalVal1);

							standardVals.append(object_id + "_standard=" + standVal1 + "<@>");
							String basicVal1 = (String) basicValMap.get(object_id);
							basicVals.append(object_id + "_basic=" + basicVal1 + "<@>");
							praticalVals.append(object_id + "_pratical=" + praticalVal1 + "<@>");
						}
					}
					bo.save2(standardVals.toString().split("<@>"), praticalVals.toString().split("<@>"), basicVals.toString().split("<@>"), bo.getKhObjs());
				} else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段的保存
				{
					// 保存的时候还要再计算一遍，因为有些特殊的指标在考核实施打分时候会通过别的计算规则计算出得分，这样会造成部分数据计算规则的不一致，所以在此处如果更新了实际值就再重新计算一遍加减分和得分
					String[] standardVals = standardVal.split("<@>");
					String[] praticalVals = praticalVal.split("<@>");
					String[] basicVals = basicVal.split("<@>");
					int len = praticalVals.length;
					String[] addVals = new String[len];
					String[] deducVals = new String[len];
					String[] dfScores = new String[len];
					for (int i = 0; i < len; i++)
					{
						String[] pVal = praticalVals[i].split("=");
						String[] bVal = basicVals[i].split("=");
						String[] sVal = standardVals[i].split("=");
						String[] temp = pVal[0].split("_");

						HashMap map = bo.basciPointCalcu(pVal[1], bVal[1], sVal[1]);
						String theVal = (String) map.get("addF");
						addVals[i] = temp[0] + "_add=" + ("".equals(theVal) ? "0" : theVal);
						theVal = (String) map.get("deducF");
						deducVals[i] = temp[0] + "_deduc=" + ("".equals(theVal) ? "0" : theVal);
						theVal = (String) map.get("objDF");
						dfScores[i] = temp[0] + "_df=" + ("".equals(theVal) ? "0" : theVal);
					}
					bo.save1(standardVals, praticalVals, basicVals, addVals, deducVals, dfScores, nowPageObjs);
				}
			} else if ("0".equals(pointype) && "0".equals(rule)) // 基本指标录分规则的保存
			{
				String fzScores = "";
				if (params != null)
					fzScores = params[1];
				bo.save3(fzScores.split("<@>"), nowPageObjs);
			}
		}

		/** **************************************展示新指标页面************************************************ */
		this.getFormHM().put("planId", plan_id);
		DataCollectBo mybo = new DataCollectBo(this.getFrameconn(), plan_id, newPoint, this.userView);
		// 生成表per_gather_xxx per_gather_score_xxx
		mybo.generateTable();
		String status = mybo.getPlanStatus();// 获得当前计划的状态,如果为评估则不许录入业绩数据，为只读状态
		if (status != null && "6".equals(status))
			this.getFormHM().put("isReadOnly", "1");
		else
			this.getFormHM().put("isReadOnly", "0");

		this.getFormHM().put("point", newPoint);
		
		ArrayList list = mybo.getPointList();
		this.getFormHM().put("khPoints", list);
		
		/******    判断定量统一打分指标是否有计算公式     ***/
//	    String pointFormula = "";
	    this.getFormHM().put("pointFormula", mybo.getPointFormula(list));		
		
		String tableHtml = mybo.getTableHtml();
		this.getFormHM().put("tableHtml", tableHtml);
		list = mybo.getAllItems();
		this.getFormHM().put("khItems", list);
		list = mybo.getKhObjs();
		this.getFormHM().put("khObjs", list);
		rule = mybo.getRule();
		this.getFormHM().put("rule", rule);
		pointype = mybo.getTypeOfPoint();
		this.getFormHM().put("pointype", pointype);
		ArrayList setlist = mybo.getDataList();
		this.getFormHM().put("setlist", setlist);
	}

}
