package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/** 
 *<p>Title:ExcecuteObjectLowerScoreTrans.java</p> 
 *<p>Description:生成下属对考核对象评分界面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mey 10, 2013</p> 
 *@author JinChunhai
 *@version 4.2
 */
public class ExcecuteObjectLowerScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = (String)hm.get("plan_id");
			String object_id = (String)hm.get("object_id");
			String body_id=(String)hm.get("body_id");
			plan_id = PubFunc.decryption(plan_id);
			object_id = PubFunc.decryption(object_id);
			object_id = object_id.replaceAll("／", "/");			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String mainbody_id = this.userView.getA0100();			
								    
		    int object_type = 2; // 1:部门 2：人员			
			this.frowset = dao.search("select pp.template_id,pt.status,pp.object_type from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+plan_id);
			this.frowset.next();
			String template_id = this.frowset.getString(1);
			String status = this.frowset.getString(2); // 权重分值表识 0：分值 1：权重
			if(status==null || status.trim().length()<=0)
				status = "0";
			object_type = this.frowset.getInt(3);
			
			String sql = "select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id="+plan_id+" and po.plan_id="+plan_id+" and pm.object_id='"+object_id+"' and pm.mainbody_id='"+mainbody_id+"'";			
			this.frowset = dao.search(sql);
			String objectName = "";
			String fillctrl = "0";
			if(this.frowset.next())
			{
				object_id = this.frowset.getString(1)+"/"+this.frowset.getString(3);
				if(this.frowset.getString("fillctrl")!=null)
					fillctrl = this.frowset.getString("fillctrl");
				objectName = this.frowset.getString("a0101");
			}			
			String[] tt = object_id.split("/");
			
			LoadXml loadxml = null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				loadxml = new LoadXml(this.getFrameconn(),plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
			}
			else
			{
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable htxml = loadxml.getDegreeWhole();
			String showOneMark=(String)htxml.get("ShowOneMark");
			SingleGradeBo singleGradeBo = new SingleGradeBo(this.frameconn,this.userView,plan_id);
			singleGradeBo.setShowOneMark(showOneMark);
			singleGradeBo.setFillctrl(fillctrl);						
		//	singleGradeBo.setShowObjectSelfScore(true);
			singleGradeBo.setShowObjectLowerScore(true);
			singleGradeBo.setOpt("1");
			
			RecordVo planVo = singleGradeBo.getPlanVo(plan_id);
			String titleName = planVo.getString("name")+"&nbsp;<br>下属对&nbsp;"+objectName+"&nbsp;的评分";
			singleGradeBo.setBatchGradeMainBodybody_id(body_id);
		    ArrayList list = singleGradeBo.getSingleGradeHtml(template_id,plan_id,status,mainbody_id,tt[0],tt[1],titleName,1,this.userView);
		    this.getFormHM().put("objectLowerScoreHtml",(String)list.get(0));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}	
	
}
