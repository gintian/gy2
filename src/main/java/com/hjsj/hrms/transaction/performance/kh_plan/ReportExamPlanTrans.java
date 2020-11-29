package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * 报批考核计划
 * 
 * @author: JinChunhai
 */

public class ReportExamPlanTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		String model = (String) this.getFormHM().get("model");
		String busitype=(String)this.getFormHM().get("busitype"); // 业务分类 =0(绩效考核); =1(能力素质)		
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String delStr = (String) hm.get("baopistr");
		delStr = delStr.replaceAll("／", "/");
		delStr = delStr.substring(0, delStr.length() - 1);
	
		String[] plans = delStr.split("/");
		
		//在报批或者直批时，判断目标模板的计划是否设置了团队负责人    2013.12.16 pjf
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer info = new StringBuffer();
		String temp = "";
		for(int k = 0; k < plans.length; k++){
			String plan_id = plans[k];
			boolean template_flag = false;
			String sqlStr="select count(*) from per_template_item where kind=2  and template_id=(select template_id from per_plan where object_type=1 and plan_id="+plan_id+")";
		    try {
				this.frowset = dao.search(sqlStr);
				if(frowset.next()){
					if(this.frowset.getInt(1)>0)
						template_flag = true;
				}
				if(template_flag){//如果是目标模板
					sqlStr = " select count(*) from per_plan_body where  body_id=-1 and plan_id=" + plan_id;
					this.frowset = dao.search(sqlStr);
					if(frowset.next()){
						if(this.frowset.getInt(1)==0)
							temp += plan_id+",";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(temp.length()>0){
			temp=temp.substring(0,temp.length()-1);
			info.append("以下计划使用了目标模板但主体类别没有设置团队负责人:");
			info.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;"+temp);
			System.out.println();
			throw new GeneralException(info.toString());
		}
		this.updateExamPlans(plans,model,busitype);
    }
    
	/**
	 * 只有起草状态的记录才能报批,
	 * 审批模式为直接方式时，则把当前计划状态设置为[已批]状态;
	 * 审批模式为审批方式时，把当前计划设置为[报批]状态
	 * @param plans
	 * @param model
	 * @throws GeneralException
	 */
    public void updateExamPlans(String[] plans,String model,String busitype) throws GeneralException
    {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String[] a = {"1", "0", "-1","-2"};  
		String[] b = {"上级", "上上级", "第三级领导","第四级领导"}; 
		StringBuffer ids = new StringBuffer();
		
		for (int i = 0; i < plans.length; i++)
		{
			String plan_id = plans[i];
			ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
			try
			{
				if(bo.getPlanVo().getInt("method")==2)
				{
					String isTeamLeaderSel = "0";
					this.frowset = dao.search("select count(*) from per_plan_body where body_id=-1 and plan_id="+plan_id);
					if(this.frowset.next())
					{
						if(this.frowset.getInt(1)>0)
							isTeamLeaderSel = "1";
					}
					if(bo.getPlanVo().getInt("object_type")==2 || (bo.getPlanVo().getInt("object_type")!=2 && "1".equals(isTeamLeaderSel)))
					{
						String selectedStr = "";
						StringBuffer strsql = new StringBuffer("select ");
						if (Sql_switcher.searchDbServer() == Constant.ORACEL)
							strsql.append("level_o");  
						else
							strsql.append("level");   
						strsql.append(" from per_mainbodyset where body_id in (select body_id from per_plan_body where plan_id="+plan_id+")");
						
						this.frowset = dao.search(strsql.toString());
						while (this.frowset.next())
						{
							if(this.frowset.getString(1)!=null)
								selectedStr=selectedStr+this.frowset.getString(1)+",";
						}				    
						
						LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
						Hashtable params = loadxml.getDegreeWhole();
						String targetMakeSeries=(String)params.get("targetMakeSeries");
						String info="";	
						String spByBodySeq=(String)params.get("SpByBodySeq");//是否按考核主体先后顺序进行审批
						if("false".equalsIgnoreCase(spByBodySeq)){
							if("1".equals(targetMakeSeries))
							{
								if(selectedStr.indexOf(a[0])==-1)
									info+=b[0]+"、";
							}
							else if("2".equals(targetMakeSeries))
							{
								for(int j=0;j<2;j++)
								{
									if(selectedStr.indexOf(a[j])==-1)
										info+=b[j]+"、";
								}
							}
							else if("3".equals(targetMakeSeries))
							{
								for(int j=0;j<3;j++)
								{
									if(selectedStr.indexOf(a[j])==-1)
										info+=b[j]+"、";
								}
							}
							else if("4".equals(targetMakeSeries))
							{
								for(int j=0;j<4;j++)
								{
									if(selectedStr.indexOf(a[j])==-1)
										info+=b[j]+"、";
								}
							}
						}else{
							
						}
						if(info.length()>0)
						{
							info=info.substring(0,info.length()-1);
							if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
								info="评估计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n审批中"+info+"没有设置相应主体类别！";
							else
								info="考核计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n审批中"+info+"没有设置相应主体类别！";
						//	info="审批中"+info+"没有设置相应主体类别！";
							throw new GeneralException(info);
						}				    
					}
				}
			} catch (SQLException e)
			{
			   
			}  						
			
		    if (!isDraftStatus(plans[i]))
		    {
		    	if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
		    		throw new GeneralException("评估计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n不是起草状态不能报批！");
		    	else
		    		throw new GeneralException("考核计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n不是起草状态不能报批！");
		    }
		    if(isTemplateNull(plans[i]))
		    {
		    	if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
		    		throw new GeneralException("评估计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n没有指定测评量表不能报批！");
		    	else
		    		throw new GeneralException("考核计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n没有指定考核模板不能报批！");
		    }
		    if(isMainBodyNull(plans[i]))
		    {
		    	if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
		    		throw new GeneralException("评估计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n没有设置考核主体类别,请在计划参数中设置后再报批！");
		    	else
		    		throw new GeneralException("考核计划：" +plan_id+"."+ bo.getPlanVo().getString("name") + "\n没有设置考核主体类别,请在计划参数中设置后再报批！");
		    }
		    ids.append(plans[i]);
		    ids.append(",");
		}
		ids.setLength(ids.length() - 1);
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
		StringBuffer strSql = new StringBuffer();
		if("0".equals(model))//直批模式
		    strSql.append("update per_plan set status = '2',agree_idea='直批',approve_result='1'," +
		    		"agree_user='"+this.getUserView().getUserName()+"',agree_date=?");
		else //审批模式
		    strSql.append("update per_plan set status = '1'"); 
		strSql.append(" where plan_id  in (");
		strSql.append(ids.toString());
		strSql.append(")");
		try
		{
		    ArrayList list = new ArrayList();
		    if("0".equals(model))//直批模式
		    	list.add(java.sql.Date.valueOf(creatDate));
		    dao.update(strSql.toString(), list);
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		
    }

    public boolean isDraftStatus(String planId) throws GeneralException
    {

		StringBuffer strsql = new StringBuffer();
		strsql.append("select status from per_plan where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
		    {
				if ("0".equals(this.frowset.getString("status")))
				    return true;
				else
				    return false;
		    }
	
		} catch (SQLException e)
		{
		    throw new GeneralException("查询数据异常！");
		}
		return false;
    }
    
    public boolean isTemplateNull(String planId) throws GeneralException
    {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select template_id from per_plan where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
		    {
				String temp = this.frowset.getString("template_id");
				//属性值为null
				if (temp==null || "".equals(temp))
				    return true;
				else
				    return false;
		    }
	
		} catch (SQLException e)
		{
		    throw new GeneralException("查询数据异常！");
		}
		return false;
	
    }
    
   /**
    * 是否指定了考核主体类别的关联
    * @param planId
    * @return
    * @throws GeneralException
    */  
    public boolean isMainBodyNull(String planId) throws GeneralException
    {

		StringBuffer strsql = new StringBuffer();
		strsql.append("select body_id from per_plan_body where plan_id=");
		strsql.append(planId);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(strsql.toString());
		    if (this.frowset.next())
		    	return false;
	
		} catch (SQLException e)
		{
		    throw new GeneralException("查询数据异常！");
		}
		return true;
    }
    
}
