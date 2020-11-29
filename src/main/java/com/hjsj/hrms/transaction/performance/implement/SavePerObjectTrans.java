package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePerObjectTrans extends IBusiness {


	public void execute() throws GeneralException {
		
		String   flag=(String)this.getFormHM().get("flag");  //判断 1：保存考核对象；2：保存主体对象
		String   plan_id=(String)this.getFormHM().get("dbpre");
		String objectID=(String)this.getFormHM().get("objectID");   //对象id
		String bodyID=(String)this.getFormHM().get("mainBodyID"); //主体分类id
		//String[] fields=(String[])this.getFormHM().get("right_fields");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String isEncode = hm.get("isEncode")==null?"false":(String)hm.get("isEncode");
		String objectType=(String)this.getFormHM().get("objectType");
	//	System.out.println("#  ##"+(String)hm.get("right_fields"));
		String[] fields=((String)hm.get("right_fields")).replaceAll("／", "/").split("/");
		ArrayList fieldlist=new ArrayList(); 
		if(fields==null||fields.length==0)
        {
            this.getFormHM().put("fieldlist",fieldlist);           
            return;
        }
		StringBuilder objStr = new StringBuilder();
		for(int i=0;i<fields.length;i++)
		{	
			String a0100 = fields[i];
			if("true".equals(isEncode)) {
				/**
				 * 手工选择时，分为选择考核对象和考核主体，
				 * 选考核主体时是选人，考核对象有可能是机构
				 * 所以a0100有可能是nbase+a0100|orgid
				 */
				if("2".equals(flag) || "2".equals(objectType)) {//考核主体是选人，人员考核计划也是选人
					a0100 = PubFunc.decrypt(a0100).substring(3);
				}else {//部门考核计划
					a0100 = PubFunc.decrypt(a0100);
				}
				
			}
			if(a0100.trim().length()>0){
				fieldlist.add(a0100);
				objStr.append("'"+a0100+"',");
			}
		}
		objStr.deleteCharAt(objStr.length()-1);
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	
		if("1".equals(flag))   //保存手工选择的考核对象
		{
			PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(),this.userView,plan_id);
			bo.handInsertObjects(objStr.toString(),plan_id,objectType);
		}
		else    //保存手工选择的主体对象
		{
			PerMainBody perMainBody=new PerMainBody(this.getFrameconn());			
			perMainBody.saveMainBody(fieldlist,plan_id,objectID,bodyID);
			//saveMainBody(fields,plan_id,objectID,bodyID);	
		}

	
	}
		

}
