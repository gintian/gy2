package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * <p>Title:GetOverallRatingTrans.java</p>
 * <p>Description>:总体评价交易</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-6-13 上午11:46:44</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GetOverallRatingTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String charttype="20";
			String opt = (String)map.get("opt");
			if("2".equals(opt))
			{
				charttype=(String)map.get("charttype");
			}
			String plan_id = PubFunc.decryption((String)this.getFormHM().get("planid"));
			String object_id = PubFunc.decryption((String)this.getFormHM().get("object_id"));
			String distinctionFlag = (String)this.getFormHM().get("distinctionFlag");
			ResultBo bo = new ResultBo(this.getFrameconn());
			ArrayList overallRatingList = bo.getTotalEvaluateLineList(plan_id, object_id);
			ArrayList sumRatingList = bo.getSumTotalEvaluateLineList(plan_id, object_id);
			ArrayList reviewsAndViewsList = bo.getReviewsAndViewsList(plan_id, object_id);
			String title=bo.getTitle(object_id,plan_id);
			ArrayList drawList = bo.getDrawList(distinctionFlag,1,plan_id,this.userView);
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			String wholeEval="0";
			String awholeEval="";
			String wholeEvalMode = "0";
			if(params.get("WholeEval")!=null)
				awholeEval=(String)params.get("WholeEval");
			if(params.get("WholeEvalMode")!=null)
				wholeEvalMode=(String)params.get("WholeEvalMode");
			if("1".equals(wholeEvalMode)){
				overallRatingList = bo.getTotalEvaluateLineListByScore(plan_id, object_id);
				sumRatingList = bo.getSumTotalEvaluateLineListByScore(plan_id, object_id,"all");
			}
			String DescriptiveWholeEval="0";
			String aDescriptiveWholeEval="";
			if(params.get("DescriptiveWholeEval")!=null)
				aDescriptiveWholeEval=(String)params.get("DescriptiveWholeEval");
			if((awholeEval!=null&&!"".equals(awholeEval)&& "true".equalsIgnoreCase(awholeEval)))
			{
				wholeEval="1";
			}
			if((aDescriptiveWholeEval!=null&&!"".equals(aDescriptiveWholeEval)&& "true".equalsIgnoreCase(aDescriptiveWholeEval)))
			{
				DescriptiveWholeEval="1";
			}
			this.getFormHM().put("descriptiveWholeEval", DescriptiveWholeEval);
			this.getFormHM().put("wholeEval", wholeEval);
			this.getFormHM().put("charttype", charttype);
			this.getFormHM().put("overallRatingList", overallRatingList);
			this.getFormHM().put("sumRatingList", sumRatingList);
			this.getFormHM().put("reviewsAndViewsList", reviewsAndViewsList);
			this.getFormHM().put("viewsTitle", title);
			this.getFormHM().put("drawList",drawList);
			this.getFormHM().put("drawId","3");
			this.getFormHM().put("planid",PubFunc.encrypt(plan_id));
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			this.getFormHM().put("distinctionFlag",distinctionFlag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
