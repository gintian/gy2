/**
 * 
 */
package com.hjsj.hrms.transaction.performance;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Hashtable;

/**
 * <p>Title:ScoreCheckTrans</p>
 * <p>Description:前台录入标准或分值时，进行校验</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-2-17:16:43:32</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ScoreCheckTrans extends IBusiness {


	public boolean isNumberic(String value)
	{
		try
		{
			Double.parseDouble(value);
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String planid=(String)this.getFormHM().get("planId");
		String objectid=(String)this.getFormHM().get("objectId");
		String score=(String)this.getFormHM().get("score");
		String maxscore=(String)this.getFormHM().get("maxscore");
		if(planid==null|| "".equals(planid))
			return;
		Hashtable htxml=new Hashtable();		 
		LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
		htxml=loadxml.getDegreeWhole();
		String limitation=(String)htxml.get("limitation");
		String rule=(String)htxml.get("limitrule");
		if("-1".equals(limitation))
			return;
		/**求转换后的标度代码*/
		int idx=maxscore.indexOf(":");
		String point_id=maxscore.substring(0,idx);
		maxscore=maxscore.substring(idx+1,maxscore.length());
		cat.debug("point_id="+point_id);
		cat.debug("maxscore="+maxscore);
		cat.debug("score="+score);
		AmountToScore toscore=new AmountToScore(this.getFrameconn());
		String gradecode=null;
		if(isNumberic(score))
			gradecode=toscore.comuteGradeCode(point_id,rule,score,maxscore);
		else
			gradecode=score;
		cat.debug("GradeCode="+gradecode);
		GradeCodeBo codebo=new GradeCodeBo(this.getFrameconn(),this.userView.getA0100(),Integer.parseInt(planid));
		codebo.initdata();		
		if(codebo.isOverLimitation(limitation,point_id,gradecode))
		{
			this.getFormHM().clear();
			cat.debug("------------->ok");
			throw GeneralExceptionHandler.Handle(new GeneralException("","考核指标最高标度超过比例或个数限制!","",""));
		}
	}

}
