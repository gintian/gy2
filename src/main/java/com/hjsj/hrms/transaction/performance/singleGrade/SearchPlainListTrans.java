package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * Title:SearchPlainListTrans.java
 * <p>
 * Description:此交易类用“干部考察-述职述廉-提交述职报告”页面初始化，与AddReportWorkTrans.java类功能重复，故暂停使用。
 * <p>
 * Company:hjsj
 * <p>
 * create time:2014-12-26 下午06:09:09
 * </p>
 * @author chenxg
 * @version 1.0
 *
 */
public class SearchPlainListTrans extends IBusiness{

	public void execute() throws GeneralException {
		if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model=(String)hm.get("model");
		ArrayList dblist =new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 StringBuffer perPlanSql= new StringBuffer();
			 perPlanSql.append("select plan_id,name,status,parameter_content from per_plan where ( status=4 or status=6 ) ");
	         perPlanSql.append("and plan_id in (select plan_id from per_mainbody where  object_id='");
	         perPlanSql.append(userView.getA0100()+"'  )"); 	          
	         perPlanSql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	         this.frowset=dao.search(perPlanSql.toString());
	         //CommonData vo1=new CommonData("0"," ");
	       	 //dblist.add(vo1); 
	         LoadXml loadXml=null; //new LoadXml();
             while(this.frowset.next())
             {
                 String name=this.getFrowset().getString("name");
                 String plan_id=this.getFrowset().getString("plan_id");
              //   String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
                 if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
 				{
 					
 					loadXml = new LoadXml(this.getFrameconn(),plan_id);
 					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
 				}
 				else
 				{
 					loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
 				}
                Hashtable htxml = loadXml.getDegreeWhole();
 				String performanceType=(String)htxml.get("performanceType"); 
                if(model.equals(performanceType))
                 {
                	 CommonData vo=new CommonData(plan_id,name);
                	 dblist.add(vo);
                 }
             }
            
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.getFormHM().put("dblist",dblist);
			this.getFormHM().put("model",model);
			this.getFormHM().put("size",String.valueOf(dblist.size()));
			this.getFormHM().put("dbpre","0");
		}
		
	}

}
