package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveDecision;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveDecisionBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchObjectiveDecisionTrans.java</p>
 * <p>Description>:目标卡制定</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 03, 2010 09:15:57 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchObjectiveDecisionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList itemSumList=new ArrayList();
		ArrayList statusList=new ArrayList();
		RowSet rowSet;
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String opt = (String)map.get("opt");
			ObjectiveDecisionBo bo = new ObjectiveDecisionBo(this.getFrameconn(),this.userView);
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			
			
			String sql=("select plan_id,name from per_plan where method=2 and status in(3,5,8) order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ");			
			rowSet=dao.search(sql);					
			ExamPlanBo exbo = new ExamPlanBo(frameconn);
			HashMap exmap = exbo.getPlansByUserView(userView, "");	
			String planids="";//根据登录用户的权限(操作单位加管理范围,操作单位优先的原则)和计划的适用范围来控制计划的显示 受模板权限控制方法  计划范围
			while(rowSet.next())
			{
//				if (!userView.isSuper_admin())
				{
					if(exmap!=null && exmap.get(rowSet.getString("plan_id"))!=null){
						
					}else
					{
						continue;
					}
				}					
				String name=rowSet.getString("name");
				String plan_id=rowSet.getString("plan_id");
				planids+=plan_id+",";
				String number=(plan_id+"."+name);				
				itemSumList.add(new CommonData(rowSet.getString("plan_id"),number));									
			}
			if(planids.length()>0)	
				planids=planids.substring(0, planids.length()-1);
			
			String str=("select codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in(01,02,03,07)");			
			rowSet=dao.search(str);						
			statusList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    		while(rowSet.next())
    		{
    			String codeitemid=rowSet.getString("codeitemid");
    			String codeitemdesc=MyObjectiveBo.getSpflagDesc(codeitemid);
    			statusList.add(new CommonData(codeitemid,codeitemdesc));
 			}
    		   		
    		String plan_id = "";
			String status = "-1";		
			String object_type = "";						
			if("1".equals(opt))
			{			
				ExamPlanBo ebo = new ExamPlanBo(this.frameconn);
				String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
				String strSql="";
				if(planids.length()>0){
					strSql=("select plan_id,object_type,template_id from per_plan where A0000 = (select min(A0000) A0000 from per_plan where method=2 and status in(3,5,8)" +
						" and plan_id in("+planids+") )");	//加上了上面的范围控制
				}else{
					strSql=("select plan_id,object_type,template_id from per_plan where A0000 = (select min(A0000) A0000 from per_plan where method=2 and status in(3,5,8))");	
				}
					
				rowSet=dao.search(strSql);
				while(rowSet.next())
				{
					if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
					{
						String template_id = rowSet.getString("template_id");				
						if(!(this.userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
						{
							//  写权限 template_id  读权限 template_id+"R"
							if(!this.userView.isHaveResource(IResourceConstant.KH_MODULE,template_id))					
								continue;					
						}
					}
					plan_id = rowSet.getString("plan_id");		
					object_type = rowSet.getString("object_type");
				}	
				this.getFormHM().put("object_type",object_type);				
								
			}else if("2".equals(opt)){
				plan_id = (String)this.getFormHM().get("plan_id");
				status = (String)this.getFormHM().get("status");
				if(plan_id!=null && plan_id.length()>0)
				{
					String strSql=("select object_type from per_plan where plan_id = "+plan_id);									
					rowSet=dao.search(strSql);
					while(rowSet.next())
					{		
						object_type = rowSet.getString("object_type");
					}
					this.getFormHM().put("object_type",object_type);
				}
			}	
			ArrayList dbname = new ArrayList();
			dbname.add("Usr");
			object_type = (String)this.getFormHM().get("object_type");
			ArrayList personList = bo.getInPlanObjectDec(plan_id, dbname,status,object_type,this.getUserView());			
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("personList",personList);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("status",status);			
			this.getFormHM().put("itemSumList", itemSumList);
			
			if(rowSet!=null)
				rowSet.close();
			
			//考核对象唯一性指标
			String onlyFild = "";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			if("2".equals(object_type))
			{
				onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
//				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
//				if(uniquenessvalid.equals("0"))
//					onlyFild ="";
			}else
			{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.frameconn);
				if(unit_code_field_constant_vo!=null)
				{
					onlyFild=unit_code_field_constant_vo.getString("str_value");	
				}
				if(onlyFild==null || onlyFild.trim().length()<=0 || "#".equals(onlyFild))
					onlyFild = "b0110";
			}
			this.getFormHM().put("onlyFild",onlyFild);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}
}

