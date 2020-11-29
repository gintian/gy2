package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:emptyExcecuteTrans.java</p>
 * <p>Description:多人考评平铺计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class emptyExcecuteTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
		//非在职人员不允许使用改功能
		if(!"USR".equalsIgnoreCase(userView.getDbname())) {
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
		}
		ArrayList dblist = new ArrayList();
		HashMap planScoreflagMap=new HashMap();
		//String model=(String)this.getFormHM().get("model");   //  0：绩效考核  1：民主评测
		String planContext="";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model=(String)hm.get("model");//  0：绩效考核  1：民主评测
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);

		String modelEmail=(String)hm.get("modelEmail");    //  发送邮件标志参数
		hm.remove("modelEmail");

		String linkType=(String)hm.get("linkType");
		String togetherCommit="False";
		if(hm.get("planContext")!=null)
		{
			planContext=(String)hm.get("planContext");
			if("all".equalsIgnoreCase(planContext))
				planContext="";
			//	hm.remove("planContext");
		}
//		if(model==null)
//		{
//			model=(String)hm.get("model");
//			this.getFormHM().put("model",model);
//		}

		try
		{
//			ExamPlanBo ebo = new ExamPlanBo(this.frameconn);
//			String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String organization = userView.getUserOrgId();
			// 得到绩效考核计划列表
			String perPlanSql = "select plan_id,name,status,parameter_content,content,template_id from per_plan where ( status=4 or status=6 ) ";
			if (!userView.isSuper_admin())
				perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
						+ userView.getA0100() + "' )";
			if(!"USR".equalsIgnoreCase(userView.getDbname()))
				perPlanSql+=" and 1=2 ";
			// linkType=liantong 如果是中国联通 则多人打分界面不显示多计划卡
			if(hm.get("linkType")!=null&& "liantong".equalsIgnoreCase((String)hm.get("linkType")))
			{
				String operate=(String)hm.get("operate");
				if(operate!=null&&operate.length()>3)
				{
					String planid=operate.substring(3);
					perPlanSql+=" and plan_id="+planid;
				}
			}

			//能力素质计划不应该出现在多人考评中  2013.11.28 pjf
			perPlanSql += "  and ( Method=1 or method is null ) and (busitype is null or busitype<>'1') order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			this.frowset = dao.search(perPlanSql);

			LoadXml aloadxml=null;
			while (this.frowset.next())
			{
			/*
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
				{
					String template_id = this.getFrowset().getString("template_id");
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id))
							continue;
					}
				}
			*/

				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
				boolean isByModel = this.getByModel(plan_id);
				if(isByModel)//按岗位素质模型测评,则不出现
					continue;
				//	String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
				String content=Sql_switcher.readMemo(this.frowset,"content");
				if(planContext!=null&&planContext.trim().length()>0)
				{
					if("zfyj".equals(planContext)&&!"执法业绩".equals(content.trim()))
						continue;
					else if("qtyj".equals(planContext)&&!"其它业绩".equals(content.trim())&&!"其他业绩".equals(content.trim()))
						continue;
					else if("lzxy".equals(planContext)&&!"廉政信用".equals(content.trim()))
						continue;
					else if("ndkh".equals(planContext)&&!"年度考核".equals(content.trim())){
						continue;
					}else if("jdkh".equals(planContext)&&!"季度考核".equals(content.trim()))
						continue;
				}


				if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
				{
					aloadxml = new LoadXml(this.getFrameconn(),plan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,aloadxml);
				}
				else
				{
					aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
				}
				Hashtable htxml = aloadxml.getDegreeWhole();
				String handEval = (String) htxml.get("HandEval");
				String handScore = "0";
				if (handEval != null && "TRUE".equalsIgnoreCase(handEval))// 启动录入结果
					handScore = "1";
				if("1".equals(handScore))
					continue;
				planScoreflagMap.put(plan_id, (String) htxml.get("scoreflag"));
				String performanceType=(String)htxml.get("performanceType");
				if(model.equals(performanceType))
				{
					CommonData vo = new CommonData(plan_id, name);
					dblist.add(vo);
				}
			} //while结束

			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
			ArrayList list = new ArrayList();

			if((modelEmail!=null) && (modelEmail.trim().length()>0) && ("0".equalsIgnoreCase(modelEmail)))
			{
				this.getFormHM().put("modelEmail","true");
			}
			else
			{
				this.getFormHM().put("modelEmail","false");
			}
			//haosl update addGradeStaus的第三个参数是页面类型 1:自我评价 2:单人打分 3:多人打分,并没有0的选项，暂时这么改，bug：50924
            list=batchGradeBo.addGradeStaus(dblist,userView.getA0100(),3);
			dblist=list;

			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht=bo.analyseParameterXml();
			togetherCommit=(String)ht.get("TogetherCommit");

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		} finally
		{
			this.getFormHM().put("model",model);
			this.getFormHM().put("togetherCommit",togetherCommit);
			this.getFormHM().put("planScoreflagMap",planScoreflagMap);
			this.getFormHM().put("dblist", dblist);
			this.getFormHM().put("clear", "1");
			this.getFormHM().put("targetDeclare","");
			this.getFormHM().put("individualPerformance","");
			this.getFormHM().put("span_ids","");
			this.getFormHM().put("linkType",linkType);
		}
	}
	/**查找计划是否是能力素质的计划并且按岗位素质模型测评 郭峰*/
	public boolean getByModel(String planid){
		boolean flag = false;
		String bymodel = null;
		try{
			RowSet rs = null;
			String sql = "";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql = "select bymodel from per_plan where plan_id = "+planid;

			rs = dao.search(sql);
			if(rs.next())
			{
				bymodel = String.valueOf(rs.getInt("bymodel"));//0 或 空 :不按岗位模型测评 1: 按岗位素质模型测评
			}
			if(bymodel!=null && "1".equals(bymodel) ){
				flag = true;
			}
			if(rs!=null)
				rs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return flag;
	}
}
