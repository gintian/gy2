package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchPerImplementTrans.java</p>
 * <p>Description:考核实施</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-26 10:52:59</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchPerImplementTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String code=(String)hm.get("code");
			String codeset=(String)hm.get("codeset");
			String operate=(String)hm.get("operate");
			String scrollValue = (String)hm.get("scrollValue");
			this.getFormHM().put("scrollValue", scrollValue);
			hm.remove("code");
			hm.remove("codeset");		

			
			String orderSql=(String)this.getFormHM().get("orderSql");
			if(operate!=null&& "init0".equals(operate))
			{
//				code=pb.getPrivCode(this.getUserView());
//				if(!code.equals("-1"))
//				{
//					if(AdminCode.getCode("UM",code)!=null)
//						codeset="UM";
//					else if(AdminCode.getCode("UN",code)!=null)
//						codeset="UN";
//				}
				this.getFormHM().put("queryA0100", "");
				orderSql="";
			
			}
			
			if(operate!=null&&!"query".equalsIgnoreCase(operate))//没有点击查询考核对象
				this.getFormHM().put("queryA0100", "");			
			
			
	/*		if(operate!=null&&operate.equals("init"))
			{
				code=pb.getPrivCode(this.getUserView());
			}*/
			
			hm.remove("operate");
			
			String planid=(String)this.getFormHM().get("planid");
			
//			if(planid!=null && planid.length()>0)//判断该计划是否存在
//			{
//			    ExamPlanBo planBo = new ExamPlanBo(this.frameconn);
//			    if(!planBo.isExist(planid))
//				planid=null;
//			}
			
			String object_type="";
			String object_id="";
			String template_id="";  // 考核摸板id
			ContentDAO dao = new ContentDAO(this.frameconn);
//			String manage_id="-1";
//			if(!this.userView.isSuper_admin())
//			{				
//				manage_id=pb.getPrivCode(this.getUserView());
//				if(!manage_id.equals("-1"))
//				{
//					if(AdminCode.getCode("UM",manage_id)!=null)
//					{
//						while(true)
//						{
//							this.frowset=dao.search("select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+manage_id+"')");
//							if(this.frowset.next())
//							{
//								if(this.frowset.getString("codesetid").equalsIgnoreCase("UN"))
//								{
//									manage_id=this.frowset.getString("codeitemid");
//									break;
//								}
//								else
//									manage_id=this.frowset.getString("codeitemid");
//							}
//						}
//					}
//				}
//			}else 
//				manage_id="0";
//			ArrayList planList=pb.getPlanList(1,manage_id);   //pb.getPlanList(1);                         //考核计划列表
			ArrayList perObjectDataList=new ArrayList();  //考核对象数据列表
			ArrayList perMainBodyList=new ArrayList();    //考核主体数据列表
			ArrayList pointPowerHeadList=new ArrayList(); //指标权限表头
			ArrayList pointPowerList=new ArrayList();     //指标权限列表
			ArrayList allObjs=new ArrayList();  //登录用户范围内的所有考核对象数据列表
			
			String isDistribute = "1";
			//是否可以批量生成目标卡
			String isBachGenerateTarget = "0";
//			if((planid==null||planid.length()==0)&&planList.size()>0)
//			{
//				planid=((CommonData)planList.get(0)).getDataValue();	  //考核计划
//			}
			String HandEval="false";
			String templateStatus="0";
			Hashtable params =null;
			String method="1";  // 1:360度考核计划 2:目标评估
			if(planid!=null&&planid.length()>0)
			{
				RecordVo vo=pb.getPerPlanVo(planid);
			  	this.getFormHM().put("planVo", vo);
				if(vo.getObject("method")!=null)
					method=String.valueOf(vo.getInt("method"));
				object_type=String.valueOf(vo.getInt("object_type"));   //1部门 2：人员
				template_id=vo.getString("template_id"); 
				String plan_b0110=vo.getString("b0110");
				this.getFormHM().put("plan_b0110", plan_b0110);
				String queryA0100=(String)this.getFormHM().get("queryA0100");	
				String whl = "" ;//根据用户权限先得到一个考核对象的范围
				String privWhl = pb.getPrivWhere(userView);
				whl+=privWhl;
				if(code!=null)
				{
					if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
						whl+=" and b0110 like '"+code+"%'";
					else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
						whl+=" and e0122 like '"+code+"%'";
					
				}				
				allObjs=pb.getPerObjectDataList(planid,object_type,privWhl,orderSql,"");
				perObjectDataList=pb.getPerObjectDataList(planid,object_type,whl,orderSql,queryA0100);
				pointPowerHeadList=pb.getPointPowerHeadList2(template_id,planid);
				if(perObjectDataList.size()>0)
				{
					LazyDynaBean abean=(LazyDynaBean)perObjectDataList.get(0);
					perMainBodyList=pb.getPerMainBodyList((String)abean.get("object_id"),planid);
					object_id=(String)abean.get("object_id");
					pointPowerList=pb.getPointPowerList((String)abean.get("object_id"),planid, pointPowerHeadList);
				}
			
				LoadXml loadXml=new LoadXml(this.getFrameconn(), planid);
				params = loadXml.getDegreeWhole();
				 //目标卡未审批也允许打分 True, False, 默认为 False
				String noApproveTargetCanScore=(String)params.get("NoApproveTargetCanScore");
				this.getFormHM().put("noApproveTargetCanScore",noApproveTargetCanScore);
				
				//得到可以引入的上期目标计划 目标卡制定用
				ArrayList lastRelaPlans = new ArrayList();
				if("2".equals(method) && allObjs.size()>0)
					lastRelaPlans = pb.getLastRelaPlans(planid,privWhl,noApproveTargetCanScore);
				this.getFormHM().put("lastRelaPlans",lastRelaPlans);
				
				String scoreWay = (String)params.get("scoreWay");//0 数据采集 1 网上打分
				HandEval=(String)params.get("HandEval");
				
/*				if(HandEval.equalsIgnoreCase("FALSE"))
				{
					if(scoreWay.equals("0"))
					{
						HandEval="FALSE";
					}
					else
						HandEval="TRUE";
				}
*/				
				RecordVo templateVo=pb.getTemplateVo(template_id);
				templateStatus=templateVo!=null?templateVo.getString("status"):"0";
			
				this.getFormHM().put("HandEval",HandEval);
				this.getFormHM().put("templateStatus", templateStatus);
				this.getFormHM().put("scoreWay", scoreWay);

				isDistribute = "1";
				//目标管理计划 对象类别非人员 考核主体类别没有设置"团队负责人"时 考核实施不需要"分发",
			//	if(method.equals("2") && !object_type.equals("2"))
			//	{
			//	    String sql = "select body_id  from per_plan_body where plan_id="+planid+" and body_id=-1";
			//	    this.frowset=dao.search(sql);
			//	    if(this.frowset.next())
			//			isDistribute = "1";
			//	    else
			//			isDistribute = "0";
			//	} 	
				//是否可以批量生成目标卡
				isBachGenerateTarget = "0";
				if("2".equals(method))
				{
				    //对于人员和有团队负责人的非人员考核对象类型的考核计划
				    if("2".equals(object_type) || (!"2".equals(object_type) && "1".equals(isDistribute)))
				    {
						ConstantXml xml = new ConstantXml(this.frameconn, "PER_PARAMETERS", "Per_Parameters");
						String accordString = xml.getTextValue("/Per_Parameters/TargetPostDuty");
						String postSet = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "SubSet");
						String targetItem = xml.getNodeAttributeValue("/Per_Parameters/TargetPostDuty", "TargetItem");					
						
						if(accordString!=null && accordString.length()>0 && postSet!=null && postSet.length()>0 && targetItem!=null && targetItem.length()>0)
						    isBachGenerateTarget = "1";
				    }
				}
				
				StringBuffer strSql = new StringBuffer();
				//将a000字段为null的情况 同步到 人员表或者机构表的顺序
				if("2".equals(object_type))
				{					
					strSql.append("update per_object");
					strSql.append(" set a0000 = ");
					strSql.append("(select usra01.a0000 from usra01 where usra01.a0100=per_object.object_id)");
					strSql.append(" where plan_id="+planid+" and a0000 is null");
				}else
				{
					strSql.append("update per_object");
					strSql.append(" set a0000 = ");
					strSql.append("(select organization.a0000 from organization where organization.codeitemid=per_object.object_id)");
					strSql.append(" where plan_id="+planid+" and a0000 is null");
				}				
				dao.update(strSql.toString());
												
			//	System.out.println(whl);
				if(whl!=null && whl.trim().length()>0)
					whl = whl.substring(4);
			//	System.out.println(whl);
				
				this.getFormHM().put("sqlString",whl);
			}
				this.getFormHM().put("isBachGenerateTarget",isBachGenerateTarget);
				this.getFormHM().put("isDistribute",isDistribute);
				this.getFormHM().put("method",method);
				this.getFormHM().put("orderSql",orderSql);
				this.getFormHM().put("object_id",object_id);
				this.getFormHM().put("object_type",object_type);
				this.getFormHM().put("objectTypeList",pb.getObjectTypeList(1,planid));
//				this.getFormHM().put("planList",planList);
				this.getFormHM().put("planid",planid);
				this.getFormHM().put("planStatus",pb.getPlanStatus(planid));
				this.getFormHM().put("template_id",template_id);
				this.getFormHM().put("allObjs",allObjs);
				this.getFormHM().put("perObjectDataList",perObjectDataList);
				this.getFormHM().put("perMainBodyList", perMainBodyList);
				this.getFormHM().put("pointPowerHeadList", pointPowerHeadList);
				this.getFormHM().put("pointPowerList", pointPowerList);
				this.getFormHM().put("code",code);
				this.getFormHM().put("codeset",codeset);
				this.getFormHM().put("optString","4");
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
