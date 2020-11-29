package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveDataCollectTrans.java</p>
 * <p>Description:保存数据采集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-16 11:11:11</p>
 * @author JinChunhai
 * @version 1.0  
 */

public class SaveDataCollectTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String plan_id=(String)this.getFormHM().get("planId");
		String point=(String)this.getFormHM().get("point");	
		String objs=null;	
		
		String paramStr = (String)this.getFormHM().get("paramStr");
		paramStr = PubFunc.keyWord_reback(paramStr);
		String[] params=null;
		if(paramStr!=null && !"".equals(paramStr))
		    params=paramStr.split("&");
		if(params!=null && params.length>0 && params[0].trim().length()>0)
		    objs=params[0];	
		else
			return;//此时 界面没有考核对象 所以不做自动保存操作了
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(),plan_id,point,this.userView);
		ArrayList allObjs = bo.getKhObjs();
		ArrayList nowPageObjs = new ArrayList();
		HashMap nowPageObjsMap = new HashMap();
		for(int i=0;i<allObjs.size();i++)
		{
		    LazyDynaBean bean = (LazyDynaBean)allObjs.get(i);
		    String objectId = (String)bean.get("object_id");
		    if(objs.indexOf(objectId)!=-1)
		    {
				nowPageObjs.add(bean);
				nowPageObjsMap.put(objectId,bean);
		    }		
		}
	
		String pointype=(String)this.getFormHM().get("pointype");	
		String rule=(String)this.getFormHM().get("rule");
		
		if(!"0".equals(pointype))//加减分指标的保存
		{	   
		    String scores = "";    
		    String dfScores = "";	    
		    
		    if(params!=null)
		    {
				scores=params[1];
				dfScores=params[2];
		    }
		    
		    bo.save(scores.split("<@>"), dfScores.split("<@>"), nowPageObjs);
		}
		else if("0".equals(pointype) && !"0".equals(rule))	//基本指标非录分规则的保存
		{
		    String standardVal = "";
		    String praticalVal = "";
		    String basicVal = "";
		    String addVal = "";
		    String deducVal = "";  
		    String dfScore = "";	    
		    
		    if(params!=null)
		    {
				standardVal=params[1];
				praticalVal=params[2];
				basicVal=params[3];
				addVal=params[4];
				deducVal=params[5];   
				dfScore=params[6];
		    }
		    
		    if ("3".equals(rule))// 排名的保存
		    {
				StringBuffer standardVals = new StringBuffer(standardVal);
				StringBuffer praticalVals = new StringBuffer(praticalVal);
				StringBuffer basicVals = new StringBuffer(basicVal);
		//		StringBuffer addVals = new StringBuffer(addVal);
		//		StringBuffer deducVals = new StringBuffer(deducVal);
		//		StringBuffer dfScores = new StringBuffer(dfScore);
		
		//		ArrayList allDataList = (ArrayList) this.getFormHM().get("setlist");
				HashMap basicValMap = bo.getBasicFen();  
				HashMap standardFenMap = bo.getStandardFens();
				HashMap praticalValsMap = bo.getPraticalVals();
				
		//		for (int j = 0; j < allDataList.size(); j++)
		//		{
		//		    LazyDynaBean bean = (LazyDynaBean) allDataList.get(j);
		//		    String object_id = (String) bean.get("object_id");
		//		    
		//		    if (nowPageObjsMap.get(object_id) == null)// 把不在当前页面的对象数据也取出来进行整体排名
		//		    {
		//			String standVal1 = (String) standardFenMap.get(object_id);
		//			standVal1 = standVal1 == null ? "0.0" : standVal1;
		//			String praticalVal1 = (String) praticalValsMap.get(object_id);
		//			praticalVal1 = (praticalVal1 == null ? "0" : praticalVal1);
		//			
		//			standardVals.append(object_id+"_standard="+standVal1+"<@>");
		//			String basicVal1 = (String)basicValMap.get(object_id);
		//			basicVals.append(object_id+"_basic="+basicVal1+"<@>");
		//			praticalVals.append(object_id+"_pratical="+praticalVal1+"<@>");
		//		    }
		//		}
				
		//		 把不在当前页面的对象数据也取出来进行整体排名
				for(int i=0;i<allObjs.size();i++)
				{
				    LazyDynaBean bean = (LazyDynaBean)allObjs.get(i);
				    String object_id = (String)bean.get("object_id");
				    if(objs.indexOf(object_id)==-1)
				    {
						String standVal1 = (String) standardFenMap.get(object_id);
						standVal1 = standVal1 == null ? "0.0" : standVal1;
						String praticalVal1 = (String) praticalValsMap.get(object_id);
						praticalVal1 = (praticalVal1 == null ? "0" : praticalVal1);
						
						standardVals.append(object_id+"_standard="+standVal1+"<@>");
						String basicVal1 = (String)basicValMap.get(object_id);
						basicVals.append(object_id+"_basic="+basicVal1+"<@>");
						praticalVals.append(object_id+"_pratical="+praticalVal1+"<@>");
				    }		
				}		
				
				bo.save2(standardVals.toString().split("<@>"), praticalVals.toString().split("<@>"), basicVals.toString().split("<@>"), bo.getKhObjs());
		    }		
		    else if ("1".equals(rule) || "2".equals(rule))// 简单｜分段的保存
		    {
				//保存的时候还要再计算一遍，因为有些特殊的指标在考核实施打分时候会通过别的计算规则计算出得分，这样会造成部分数据计算规则的不一致，所以在此处如果更新了实际值就再重新计算一遍加减分和得分
				String[] standardVals = standardVal.split("<@>");
				String[] praticalVals = praticalVal.split("<@>");
				String[] basicVals = basicVal.split("<@>");
				int len = praticalVals.length;
				String[] addVals = new String[len];
				String[] deducVals = new String[len];
				String[] dfScores = new String[len];
				for(int i=0;i<len;i++)
				{
				    String[] pVal = praticalVals[i].split("=");
				    String[] bVal = basicVals[i].split("=");
				    String[] sVal = standardVals[i].split("=");
				    String[] temp = pVal[0].split("_");
				    
				    HashMap map = bo.basciPointCalcu(pVal[1],bVal[1],sVal[1]);	
				    String theVal = (String) map.get("addF");
				    addVals[i] = temp[0]+"_add="+("".equals(theVal)?"0":theVal);
				    theVal = (String) map.get("deducF");
				    deducVals[i] = temp[0]+"_deduc="+("".equals(theVal)?"0":theVal);
				    theVal = (String) map.get("objDF");
				    dfScores[i] =temp[0]+"_df="+("".equals(theVal)?"0":theVal);
				}
				
				bo.save1(standardVals, praticalVals, basicVals, addVals, deducVals, dfScores, nowPageObjs);
		    }	    
		}
		else if("0".equals(pointype) && "0".equals(rule))     //基本指标录分规则的保存
		{   	    
		    String fzScores = "";
		    if(params!=null)
		    	fzScores=params[1];
		    
		    bo.save3(fzScores.split("<@>"),nowPageObjs);
		} 
		//数据录入之后，重新计算总分  chent 20151111 start
		for(int i=0;i<allObjs.size();i++)
		{
			try {
			    LazyDynaBean bean = (LazyDynaBean)allObjs.get(i);
			    String objectId = (String)bean.get("object_id");
			    String sql = "select mainBody_id From per_mainbody where plan_id="+plan_id;
			    ContentDAO dao = new ContentDAO(this.frameconn);
			    RowSet rs = dao.search(sql);
				while(rs.next()){
					String mainbodyId = rs.getString("mainBody_id");
					ObjectCardBo objectCardBo = new ObjectCardBo(this.frameconn, plan_id, objectId, this.getUserView());
					objectCardBo.getScore(mainbodyId, objectId);
				}
			} catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		//数据录入之后，重新计算总分  chent 20151111 end
    }
}
