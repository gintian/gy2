package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class SaveScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String object_id=PubFunc.decrypt((String)this.getFormHM().get("object_id"));
			String planid=(String)this.getFormHM().get("planid");
			String flag=(String)this.getFormHM().get("flag");     //1:保存 或 2:提交
			String body_id=PubFunc.decrypt((String)this.getFormHM().get("body_id"));
			String mainbody_status=(String)this.getFormHM().get("mainbody_status");
			ArrayList valueList=(ArrayList)this.getFormHM().get("valueList");
			if(valueList==null||valueList.size()==0)
				return;
			singleGradeBo_new bo=new singleGradeBo_new(this.getFrameconn(),planid,this.getUserView());
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,planid);
			StringBuffer userValues=new StringBuffer(""); 
			for(int i=0;i<valueList.size();i++)
			{
				userValues.append("/"+((String)valueList.get(i)).split(":")[1]);
			}
			String userValue=userValues.substring(1);
			
		//	String info=bo.saveTaskScore(valueList,flag,body_id,object_id);
			Hashtable planParam=bo.getPlanParam();
	        String limitation=(String)planParam.get("limitation");
			
			
			String info="";
			String score="";
			if(!"-1".equals(limitation))
			{
				info=singleGradeBo.isOverLimitation("",limitation,userValue,planid,body_id,object_id,bo.getTemplateid(),"","false","");
			}
			if(info.indexOf("不")==-1)
			{
				 BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),planid);
				 String[] userid={object_id};
				 HashMap usersValue=new HashMap();
				 usersValue.put(object_id,userValue);	
				 HashMap fineMaxMap=(HashMap)planParam.get("fineMaxMap");
				 String WholeEval=(String)planParam.get("WholeEval");
				 String fineMax=(String)planParam.get("fineMax");
				 String bFineRestrict=(String)planParam.get("FineRestrict");
				 String KeepDecimal=(String)planParam.get("KeepDecimal");  //小数位
				 
				 HashMap BadlyMap=(HashMap)planParam.get("BadlyMap");
				 String BadlyRestrict=(String)planParam.get("BadlyRestrict");
				 String BadlyMax=(String)planParam.get("BadlyMax");
				
				 String aa_info="";
				 if(!"False".equals(bFineRestrict)&& "2".equals(flag))
					 aa_info=batchGradeBo.validateMaxvalueNum(fineMax,object_id,"",bo.getTemplateid(),planid,usersValue,userid,fineMaxMap,batchGradeBo.getObjectNums(body_id,planid),body_id,"false","",1);
				 else
						aa_info="success";
				 if("success".equals(aa_info))
				 {
						if(!"False".equals(BadlyRestrict)&& "2".equals(flag))
							aa_info=batchGradeBo.validateMaxvalueNum(BadlyMax,object_id,"",bo.getTemplateid(),planid,usersValue,userid,BadlyMap,batchGradeBo.getObjectNums(body_id,planid),body_id,"false","",2);
						else
							aa_info="success";
				 }
				 if("success".equals(aa_info))
				 {
					    String konwDegree =this.getFormHM().get("konwDegree")!=null?(String)this.getFormHM().get("konwDegree"):""; 
					    String wholeEval=this.getFormHM().get("wholeEval")!=null?(String)this.getFormHM().get("wholeEval"):"";
						info=bo.saveTaskScore(valueList,flag,body_id,object_id,konwDegree,wholeEval,mainbody_status);
						score=String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(planid),body_id,bo.getTemplateid(),object_id,this.userView));
				        score=PubFunc.round(score,Integer.parseInt(KeepDecimal));
				 }
				 else
					info=aa_info;
			}
			
			
			this.getFormHM().put("mainbody_status",mainbody_status);
			this.getFormHM().put("score",score);
			this.getFormHM().put("info",SafeCode.encode(info.replaceAll("<br>","\r\n")));
			this.getFormHM().put("flag",flag);
		}
		catch(Exception e)
		{
			System.out.println("------");
		//	e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
