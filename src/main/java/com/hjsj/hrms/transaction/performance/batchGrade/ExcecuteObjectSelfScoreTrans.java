package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/** 
 *<p>Title:ExcecuteObjectSelfScoreTrans.java</p> 
 *<p>Description:生成考核对象自我评分界面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 10, 2008</p> 
 *@author dengcan
 *@version 4.2
 */
public class ExcecuteObjectSelfScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("object_id");
			
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }
			String plan_id=(String)hm.get("plan_id");
			String opt="0";  //0: 自己  1:别人 
			String mainbody_id="";
			if(hm.get("opt")!=null)
			{
				opt="1";
				hm.remove("opt");
				mainbody_id=(String)hm.get("mainbody_id");
			}
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    
		    int object_type=2; // 1:部门 2：人员
			this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type  from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+plan_id);
			this.frowset.next();
			String template_id=this.frowset.getString(1);
			String status=this.frowset.getString(2);		//权重分值表识 0：分值 1：权重
			if(status==null|| "".equals(status))
				status="0";
			object_type=this.frowset.getInt(3);
			String sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id="+plan_id+" and po.plan_id="+plan_id+"   and pm.object_id='"+object_id+"'  and  pm.mainbody_id='"+object_id+"'";
			if(object_type==1||object_type==3||object_type==4)
			{
				sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id="+plan_id+" and po.plan_id="+plan_id;
				sql+="   and pm.object_id='"+object_id+"'  and  pm.mainbody_id=(select mainbody_id from per_mainbody where  plan_id="+plan_id+" and object_id='"+object_id+"'  and body_id=-1)";
			}
			if("1".equalsIgnoreCase(opt))
			{
				sql="select po.object_id,po.a0101,pm.status,pm.fillctrl from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id="+plan_id+" and po.plan_id="+plan_id+"   and pm.object_id='"+object_id+"'  and  pm.mainbody_id='"+mainbody_id+"'";
			}
			this.frowset=dao.search(sql);
			String objectName="";
			String fillctrl="0";
			if(this.frowset.next())
			{
				object_id=this.frowset.getString(1)+"/"+this.frowset.getString(3);
				if(this.frowset.getString("fillctrl")!=null)
					fillctrl=this.frowset.getString("fillctrl");
				objectName=this.frowset.getString("a0101");
			}
			else
			{	
				if(opt==null||!"1".equalsIgnoreCase(opt))
					throw GeneralExceptionHandler.Handle(new Exception("考核对象没有自我评分!"));
			}
			object_id = object_id.replaceAll("／", "/");
			String[] tt=object_id.split("/");
			
			LoadXml aloadxml=null;
			if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
			{
				aloadxml = new LoadXml(this.getFrameconn(),plan_id);
				BatchGradeBo.getPlanLoadXmlMap().put(plan_id,aloadxml);
			}
			else
			{
				aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
			}
			Hashtable htxml=aloadxml.getDegreeWhole();
			String showOneMark=(String)htxml.get("ShowOneMark");
		    String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView,plan_id);
			singleGradeBo.setShowOneMark(showOneMark);
			singleGradeBo.setFillctrl(fillctrl);
			
			
			singleGradeBo.setShowObjectSelfScore(true);
			singleGradeBo.setOpt(opt);
			
			String titleName=objectName+"&nbsp;自我评分";
			if("1".equals(opt))
				titleName=objectName;
			else
				mainbody_id=this.userView.getA0100();
		    ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,plan_id,status,mainbody_id,tt[0],tt[1],titleName,1,this.userView);
		    this.getFormHM().put("objectSelfScoreHtml",(String)list.get(0));
		    
		    
		    
		    
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
