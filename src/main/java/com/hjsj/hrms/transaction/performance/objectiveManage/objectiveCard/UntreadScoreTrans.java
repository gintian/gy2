package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class UntreadScoreTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			
			if("1".equals(type))
			{
	    		String body_id=(String)this.getFormHM().get("body_id");
		    	String object_id=(String)this.getFormHM().get("object_id");
	    		String plan_id=(String)this.getFormHM().get("plan_id");
		    	String mainbody_id=this.getUserView().getA0100();
		    	String isSendMail=(String)this.getFormHM().get("isSendMail");
		    	String url_p=(String)this.getFormHM().get("url_p");
		    	ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.getUserView(),plan_id);
		    
		    	boolean flag=false;
		    	if(bo.getPlanParam().get("GradeByBodySeq")!=null&& "True".equalsIgnoreCase(((String)bo.getPlanParam().get("GradeByBodySeq")).trim()))
				{
					if(bo.getPlanParam().get("AllowSeeAllGrade")!=null&& "True".equalsIgnoreCase(((String)bo.getPlanParam().get("AllowSeeAllGrade")).trim()))
					{
						flag=true;
					}
				}
		    	ArrayList list=new ArrayList();
		    	if(flag)
		    		list = bo.getUntreadScoreList2(plan_id, object_id, mainbody_id);
		    	else
		    		list = bo.getUntreadScoreList(plan_id, object_id, body_id, mainbody_id);
		    	String msg="0";
		    	/*if(list.size()==1)
			    {
		    		CommonData cd=(CommonData)list.get(0);
		    		//bo.untread(object_id, plan_id, cd.getDataValue(),isSendMail,url_p);
		    		this.getFormHM().put("a0100",cd.getDataValue());
		    		msg="1";
		    		
		    	}*/
		    	if(list.size()==0)
		    	{
		    		msg="2";
		    	}
		    	this.getFormHM().put("msg",msg);
		    	this.getFormHM().put("list",list);
			}
			else
			{
				String reason=(String)this.getFormHM().get("reason");
				String body_id=(String)this.getFormHM().get("body_id");
		    	String object_id=(String)this.getFormHM().get("object_id");
	    		String plan_id=(String)this.getFormHM().get("plan_id");
	    		String id=(String)this.getFormHM().get("ids");
	    		ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.getUserView(),plan_id);
	    		String isSendMail=(String)this.getFormHM().get("isSendMail");
	    		String url_p=(String)this.getFormHM().get("url_p");
	    		bo.untread(object_id, plan_id, id,isSendMail,url_p,SafeCode.decode(reason));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
