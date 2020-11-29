package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:GetTablesTrans.java</p>
 * <p>Description>:绩效分析 综合测评表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 16, 2011 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class GetTablesTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String distinctionFlag = "";
			String object_id = "";
			String plan_id = "";
			String byModel="";//0按测评表测  1 按岗位素质
			if(hm.get("opt")!=null&& "analyse".equals((String)hm.get("opt")))   //dengcan   绩效分析调用
			{
				distinctionFlag = (String)hm.get("a_distinctionFlag");
				object_id = PubFunc.decrypt((String)hm.get("a_objectId"));
				plan_id = (String)hm.get("planID");
				hm.remove("opt");
			}
			else
			{
				distinctionFlag = (String)this.getFormHM().get("distinctionFlag");
				object_id = PubFunc.decrypt((String)this.getFormHM().get("object_id"));
				plan_id = PubFunc.decrypt((String)this.getFormHM().get("planid"));
			}
			String object_type = "";
			//没有计划，也就不用再展示了
			if(StringUtils.isBlank(plan_id)) {
				return;
			}
			if(!"".equals(plan_id))
			{
			    ContentDAO dao = new ContentDAO(this.frameconn);
			    RecordVo vo = new RecordVo("per_plan");
			    vo.setInt("plan_id", Integer.parseInt(plan_id));
			    vo=dao.findByPrimaryKey(vo);
			    object_type = vo.getString("object_type");
			    byModel= vo.getString("bymodel");
			}
			this.getFormHM().put("objecType", object_type);
			
			
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String showAppraiseExplain=(String)params.get("showAppraiseExplain");
			ResultBo bo = new ResultBo(this.getFrameconn());
			ArrayList drawList = bo.getDrawList(distinctionFlag,1,plan_id,this.userView);
			HashMap map=new HashMap();
			if(byModel!=null&& "1".equals(byModel)){
				map = bo.getPersonalInformationByModel(object_id, plan_id);//按岗位素质模型
			}else{
				map = bo.getPersonalInformation(object_id, plan_id);
			}
		
			/**基本信息集*/
			LazyDynaBean infobean=(LazyDynaBean)map.get("1");
			HashMap pointMap=(HashMap)map.get("3");
			HashMap itemMap = (HashMap)map.get("2");
			String gatiShowDegree=(String)params.get("GATIShowDegree");
			String limitrule=(String)params.get("limitrule");
			String strNodeKnowDegree = "0"; // 了解程度
			String astrNodeKnowDegree="";
			String strWholeEval = "0"; // 总体评价
			String astrWholeEval="";
			astrNodeKnowDegree=(String)params.get("NodeKnowDegree");
			astrWholeEval=(String)params.get("WholeEval");
			String wholeEvalMode = (String)params.get("WholeEvalMode");
			if(astrNodeKnowDegree!=null&&!"".equals(astrNodeKnowDegree)&& "true".equalsIgnoreCase(astrNodeKnowDegree))
				strNodeKnowDegree="1";
			if(astrWholeEval!=null&&!"".equals(astrWholeEval)&& "true".equalsIgnoreCase(astrWholeEval)&&"0".equals(wholeEvalMode))
				strWholeEval="1";
			String isShowVoteTd="0";
			if("1".equals(strNodeKnowDegree)|| "1".equals(strWholeEval))
			{
				isShowVoteTd="1";
			}
			/**考核项目，要素信息集*/
			ArrayList itemList=new ArrayList();
			if(byModel!=null&& "1".equals(byModel)){
				itemList = bo.getItemListByModel(object_id, plan_id, itemMap, pointMap,gatiShowDegree,limitrule);
			}else{
				itemList = bo.getItemList(object_id, plan_id, itemMap, pointMap,gatiShowDegree,limitrule);
			}
			
	    	/**总体评价信息集*/
	    	ArrayList overallRating = bo.getOverallRating(object_id, plan_id);
	    	ArrayList overallRatingDetail = bo.getOverallRatingDetail(object_id, plan_id);
	    	
			if(showAppraiseExplain!=null&&!"".equals(showAppraiseExplain)&& "true".equalsIgnoreCase(showAppraiseExplain))
			{		    	
		    	/**测评说明信息集*/
		    	ArrayList evaluationDescription=bo.getEvaluationDescription(object_id, plan_id);		    	
		    	this.getFormHM().put("evaluationDescription", evaluationDescription);
		    	this.getFormHM().put("isShowEvaluationDescription", "1");
			}
			else
			{
				this.getFormHM().put("isShowEvaluationDescription", "0");
			}
			/**了解程度信息集*/
			ArrayList understandingOf=bo.getUnderstandingOf(object_id, plan_id);
			ArrayList understandingOfDetail=bo.getUnderstandingOfDetail(object_id, plan_id);
			this.getFormHM().put("understandingOf", understandingOf);
			this.getFormHM().put("drawList",drawList);
			this.getFormHM().put("drawId","0");
			this.getFormHM().put("distinctionFlag", distinctionFlag);
			this.getFormHM().put("personalInformation",infobean);
			this.getFormHM().put("planid",PubFunc.encrypt(plan_id));
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("itemTotal",itemList.size()+"");
			this.getFormHM().put("overallRatingDetail", overallRatingDetail);
	    	this.getFormHM().put("overallRating", overallRating);
			this.getFormHM().put("understandingOfDetail", understandingOfDetail);
			this.getFormHM().put("isShowKnowDegree", strNodeKnowDegree);
			this.getFormHM().put("isShowWholeEval",strWholeEval);
			this.getFormHM().put("isShowVoteTd", isShowVoteTd);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
