package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.DemandCtrlParamXmlBo;
import com.hjsj.hrms.businessobject.hire.InterviewGradeResultBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ExcecuteAllGradeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100=(String)hm.get("a0100");
		String name=(String)hm.get("name");
		String itemid=(String) hm.get("itemid");
		a0100 = PubFunc.decrypt(a0100);
		name = PubFunc.decrypt(name);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			String z0127="";
			String z0301="";
			String employTypeFiled="";			
			ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
			HashMap map = bo2.getAttributeValues();
			if (map != null && map.get("hire_object") != null)
				employTypeFiled = (String) map.get("hire_object");
			if(employTypeFiled==null|| "".equals(employTypeFiled))
				throw GeneralExceptionHandler.Handle(new Exception("参数设置模块没有设置招聘对象指标！"));
			/**获得招聘配置的人员库**/
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbName=zpDbNameVo.getString("str_value");
			String resume_state_field = (String) map.get("resume_state");//获得配置的简历状态指标
			String hireState="";
			String sql="select z03."+employTypeFiled+",z03.z0301,"+dbName+"A01."+resume_state_field+" hirestate from zp_pos_tache,z03,"+dbName+"A01 where zp_pos_tache.zp_pos_id=z03.z0301  and zp_pos_tache.resume_flag='12'   and zp_pos_tache.a0100='"+a0100+"'";
			sql=sql+" and "+dbName+"A01.a0100='"+a0100+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				z0127=this.frowset.getString(1);
				z0301=this.frowset.getString(2);
				hireState=this.frowset.getString(3);
			}
			if(z0127==null|| "".equals(z0127))
				throw GeneralExceptionHandler.Handle(new Exception("职位需求没有设置招聘对象！"));
				
			
			String scoreFlag="";
			String template_id="";//为每个职位定义的测评表
			String status="0";       //权重分值表识 0：分值 1：权重
			String titleName="";
			String interviewSearch="0";//要查看那个阶段的分数  默认没有高级测评的情况下查看 0：考评总分
            ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			int advanceFlag=testTemplatAdvance.size();
			
			String currentAndvance="0";
			if(advanceFlag>0){//配置了高级测评方式
				for(int i=0;i<testTemplatAdvance.size();i++){
					 HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
					 String hire_obj_code=(String) advanceMap.get("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
					 String interview=(String) advanceMap.get("interview");//得到面试状态也就是简历的状态 1：初试 2：复试
					 String score_item=(String) advanceMap.get("score_item");//得到关联指标
					 if(hire_obj_code.equalsIgnoreCase(z0127)&&score_item.equalsIgnoreCase(itemid)){//招聘渠道相同关联指标相同那么就是一个阶段
						 template_id=(String) advanceMap.get("templateId");//得到模版号
						 scoreFlag=(String)advanceMap.get("mark_type");//是否采用复杂打分
						 interviewSearch=interview;
						 currentAndvance="1";
						 break;
					 }
				}
			}
			if(template_id.trim().length()<=0){
				DemandCtrlParamXmlBo DemandCtrlParamXmlBo = new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
				scoreFlag =DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "type");
				template_id = DemandCtrlParamXmlBo.getNodeAttributeValue("/content/template", "id");
				HashMap scorFlagmap=bo2.getMarkTypebyHireObjectCode();  	
		        if(template_id==null|| "".equals(template_id.trim())|| "#".equals(template_id.trim())){
				    template_id=(String)map.get("testTemplateID_"+z0127);	
			        scoreFlag=(String)scorFlagmap.get(z0127);
		        } 
			}
			
			
			this.frowset=dao.search("select * from per_template where template_id='"+template_id+"'");
			if(this.frowset.next())
			{
				status=this.frowset.getString("status");
				titleName=this.frowset.getString("name");
			}
			
			int object_type=2; // 1:部门 2：人员
			InterviewGradeResultBo bo=new InterviewGradeResultBo(this.getFrameconn());
			bo.setCurrentAndvance(currentAndvance);
			bo.setHireState(hireState);
			bo.setInterviewSearch(interviewSearch);
			ArrayList list=bo.getBatchGradeHtml(template_id,a0100,status,titleName,z0301,scoreFlag);
			String html=(String)list.get(0);
			
			this.getFormHM().put("gradeHtml",html);
			this.getFormHM().put("titleName","考核对象："+name);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
		
		

	}

}
