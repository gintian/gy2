package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.*;

/**
 * <p>ValiStartPlanTrans.java</p>
 * <p>Description:校验启动计划</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2009-06-27</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ValiStartPlanTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String plan_ids = (String) this.getFormHM().get("plan_id");
		// 计划的当前状态 3-发布 8-分发 5-暂停
		String status = (String) this.getFormHM().get("plan_status");
		String startFlag = (String) this.getFormHM().get("startFlag");
		this.getFormHM().put("startFlag", startFlag);
				
		String signLogo = (String)this.getFormHM().get("signLogo"); // 批量启动的标志
		String[] planIds = null;
		if(signLogo!=null && signLogo.trim().length()>0 && "batchStart".equalsIgnoreCase(signLogo))
		{
			if(plan_ids==null || plan_ids.trim().length()<=0)
				return;
			
			plan_ids = plan_ids.substring(0, plan_ids.length() - 1);
			planIds = plan_ids.replaceAll("／", "/").split("/");
		}else
		{
			planIds=new String[1];
			planIds[0] = plan_ids;
		}
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String flag = "1";
		String sporpfSeq = "false";
		String logoFlag = "1";
		String info = "";
		String logoInfo = "";
		String planId_s = ""; // 筛选出符合发邮件条件的计划号
		try
		{
			
			boolean isEmail = false;
			this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			if (this.frowset.next())
			{
				String str_value = 	this.frowset.getString("str_value");
				if (str_value == null || (str_value != null && "".equals(str_value)))
				{
	
				} else
				{
					Document doc = PubFunc.generateDom(str_value);
					String xpath = "//Per_Parameters";
					XPath xpath_ = XPath.newInstance(xpath);
					Element ele = (Element) xpath_.selectSingleNode(doc);
					Element child;
					if (ele != null)
					{	
						child = ele.getChild("TargetAppraises");
						if (child != null)
						{
							String isTargetAppraisesTemp = child.getAttributeValue("email");
							String template = child.getAttributeValue("template");
							if (isTargetAppraisesTemp != null && "true".equalsIgnoreCase(isTargetAppraisesTemp) && template!=null && !"-1".equals(template))
							{
								isEmail = true;
							}
						}
					}
				}
			}
			if(isEmail)
				this.getFormHM().put("isEmail", "1");
			else
				this.getFormHM().put("isEmail", "0");
			
			
			//  校验启动计划
			if(planIds!=null && planIds.length>0)
			{	
				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
				
				for (int k = 0; k < planIds.length; k++)
				{
					String plan_id = planIds[k];			
					RecordVo vo = pb.getPerPlanVo(plan_id);	
					status = String.valueOf(vo.getInt("status"));									    
				    String objectType = String.valueOf(vo.getInt("object_type")); //1部门 2：人员
				    String method = String.valueOf(vo.getInt("method"));
				    int gather_type = vo.getInt("gather_type");
				    
				    boolean isHaveTeamer = false;// 考核计划是否设置了 团队负责人 的主体类别
				   		
				    // 获得需要的计划参数
					LoadXml loadXml=null; //new LoadXml();
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
				    String objsFromCard = (String)htxml.get("ObjsFromCard"); //考核对象是否从机读卡读取(考核实施中不需要选择考核对象)
				    String spByBodySeq = (String)htxml.get("SpByBodySeq"); //按考核主体顺序号控制审批流程(True, False默认为False)				    
				    String gradeByBodySeq = (String)htxml.get("GradeByBodySeq"); //按考核主体顺序号控制评分流程(True, False默认为False)	
				    String mailTogoLink = (String)htxml.get("MailTogoLink"); // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件
				    
				    String sqlStr = " select * from per_plan_body where  body_id=-1 and plan_id=" + plan_id;
				    this.frowset = dao.search(sqlStr);
				    if (this.frowset.next())
				    	isHaveTeamer = true;
							    
					if(isEmail && !"result".equalsIgnoreCase(startFlag) && (!"3".equalsIgnoreCase(mailTogoLink)))
					{						
						if(("1".equalsIgnoreCase(method)) && (String.valueOf(gather_type)!=null && String.valueOf(gather_type).trim().length()>0 && "1".equalsIgnoreCase(String.valueOf(gather_type))))	 //  只考虑360计划
						{
							
						}else if("2".equalsIgnoreCase(method) && !"2".equalsIgnoreCase(objectType) && !isHaveTeamer)
						{
							
						}else
							planId_s+=plan_id+"/";						
					}
				    
				    if(isHaveTeamer) // 判断是否设置了团队负责人
						this.getFormHM().put("isHaveTeamer", "1");
					else
						this.getFormHM().put("isHaveTeamer", "0");
				    
				    int count = 0;
				    if ("3".equals(status) || "5".equals(status))// 由发布 暂停状态来启动
				    {
						sqlStr = "select count(*) from per_object where plan_id=" + plan_id;
						this.frowset = dao.search(sqlStr);
						if (this.frowset.next())
						    count = this.frowset.getInt(1);
						if (count == 0  && "false".equalsIgnoreCase(objsFromCard))
						{
						    flag = "0";
						    if(signLogo!=null && signLogo.trim().length()>0 && "batchStart".equalsIgnoreCase(signLogo))
					        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n没有设置考核对象，不能启动！";
					        else
					        	info="没有设置考核对象，不能启动！";
						    this.getFormHM().put("flag", flag);
						    this.getFormHM().put("info", SafeCode.encode(info));
						    this.getFormHM().put("plan_ids", plan_ids);  // 需全部启动的计划id
						    this.getFormHM().put("planId_s", planId_s);  // 需启动发邮件的计划id
						    return;
						}
										
						String privWhl = pb.getPrivWhere(userView);
						sqlStr = "select object_id,a0101 from per_object where plan_id=" + plan_id+" "+privWhl;
						sqlStr+=" and object_id not in (select distinct object_id from per_mainbody where plan_id="+plan_id+")";
						this.frowset = dao.search(sqlStr);
						StringBuffer nosetName = new StringBuffer();
						int num = 0;
						while (this.frowset.next())
						{
							if(num<5) {
								String name = this.frowset.getString(2);
								nosetName.append(name+"、");
							}
							num++;
							
						}	
						if ("score".equalsIgnoreCase(startFlag) && gather_type!=1 && StringUtils.isNotBlank(nosetName.toString()))//打分方式的启动才验证考核主体
						{
							//目标计划不判断有没有考核主体
							if("1".equals(method) || ("2".equals(method) && !"2".equals(objectType) && !isHaveTeamer))
							{
								 nosetName.setLength(nosetName.length()-1);
								 if(num>5) {
									 nosetName.append("等"+num+"人");
								 }
								 flag = "0";
								 if(signLogo!=null && signLogo.trim().length()>0 && "batchStart".equalsIgnoreCase(signLogo))
							        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n下的 " + nosetName + " 没有设置考核主体，不能启动！";
							        else
							        	info = nosetName+" 没有设置考核主体，不能启动！";
								 this.getFormHM().put("flag", flag);
								 this.getFormHM().put("info", SafeCode.encode(info));
								 this.getFormHM().put("plan_ids", plan_ids);  // 需全部启动的计划id
								 this.getFormHM().put("planId_s", planId_s);  // 需启动发邮件的计划id
								 return;
							}		   
						}
						String busitype=vo.getString("busitype");
						if("1".equals(method) && !"result".equalsIgnoreCase(startFlag) && gather_type==0&&!"1".equals(busitype))	//360非能力素质考核计划 以非录入结果的方式启动 要检验考核主体的数目和主题的指标权限是否一致
						{
							sqlStr = "select count(*) from per_pointpriv_" + plan_id;
							this.frowset = dao.search(sqlStr);
							count = 0;
							info="";
							if (this.frowset.next())
							    count = this.frowset.getInt(1);
							if(count==0)
							{
								if(signLogo!=null && signLogo.trim().length()>0 && "batchStart".equalsIgnoreCase(signLogo))
									flag = "1";
								else
								{
									info = "考核主体指标权限没有设置，默认所有主体对所有指标可以进行考核!\n确定将启动计划。您确定吗？";
									flag = "3";
									this.getFormHM().put("flag", flag);
									this.getFormHM().put("info", SafeCode.encode(info));
									return;
								}
							}
							else
							{
								sqlStr = "select count(*) from per_mainbody where plan_id=" + plan_id;
								this.frowset = dao.search(sqlStr);
								info="";
								if (this.frowset.next())
								{
									if( count < this.frowset.getInt(1))
									{
										 flag = "0";
										 if(signLogo!=null && signLogo.trim().length()>0 && "batchStart".equalsIgnoreCase(signLogo))
											 info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n的考核对象指定的主体总数与指标权限中的主体总数不一致！";
									     else
									    	 info = "考核对象指定的主体总数与指标权限中的主体总数不一致！";
									//	 info = "考核对象指定的主体总数与指标权限中的主体总数不一致！";
										 this.getFormHM().put("flag", flag);
										 this.getFormHM().put("info", SafeCode.encode(info));
										 this.getFormHM().put("plan_ids", plan_ids);  // 需全部启动的计划id
										 this.getFormHM().put("planId_s", planId_s);  // 需启动发邮件的计划id
										 return;
									}
								}				
							}
						}					
				    }
				    // 分发状态：1. 目标管理 考核对象为人员 2.目标管理 考核对象为非人员 且设置了团队负责人的主体类别
				    // 满足这两种情况之一的做如下检查
				    if ("8".equals(status))// 由分发状态来启动
				    {
				    	//目标管理校验，参数设置勾选了按考核主体顺序打分时，考核主体未设置评分顺序不能启动 haosl add 2018-4-18 start
				    	if("true".equalsIgnoreCase(gradeByBodySeq)) {//按考核主体顺序打分
				    		//不参与打分的不校验打分顺序，联通支付发现的问题  haosl 2018年8月13日
				    		String sql = "select distinct(po.object_id),po.a0101 from per_mainbody pm,per_object po,per_plan_body ppb where pm.plan_id=? and pm.object_id=po.object_id and pm.seq is null ";
				    		sql+=" and pm.body_id=ppb.body_id";
				    		sql+=" and pm.plan_id=ppb.plan_id";
				    		sql+=" and ppb.isgrade<>1";
				    		List values = new ArrayList();
				    		values.add(plan_id);
				    		this.frowset = dao.search(sql,values);
				    		String a0101s = "";
			    		    int pcount = 0;
				    		while (this.frowset.next()) {
				    			a0101s += "\n"+this.frowset.getString("a0101");//考核对象
				    			 pcount++;
				    		}
				    		if(StringUtils.isNotBlank(a0101s)) {
				    			flag="0";//alert的方式提示
				    			if("batchStart".equals(signLogo)) {//如果是批量启动时，则提示具体的考核计划
				    				info = ResourceFactory
											.getProperty("lable.performance.perPlan") +plan_id+"."+ vo.getString("name") + "\n";
				    			}
				    			info += ResourceFactory.getProperty("label.performance.notsetseqpersonlist")+"\n";
				    			info +=a0101s;
				    			 if(pcount>1) {
				    				 info+="  "+ResourceFactory.getProperty("label.sum")+pcount+ResourceFactory.getProperty("sys.import.men");
					    		}
				    			 info +="\n"+ResourceFactory.getProperty("label.performance.notsetseqprompt");
								this.getFormHM().put("info", SafeCode.encode(info));
								return;
				    		}
				    	}
				    	//目标管理校验，参数设置勾选了按考核主体顺序打分时，考核主体未设置评分顺序不能启动 haosl add 2018-4-18  end
						if ("2".equals(objectType) || (!"2".equals(objectType) && isHaveTeamer))
						{
						    if ("2".equals(objectType))
						    {
								HashMap map = new HashMap();
								sqlStr = "SELECT B0110, E0122, A0101, codeitemdesc FROM per_object, organization WHERE plan_id=" + plan_id;
								sqlStr += " AND ((sp_flag is Null) or (sp_flag<>'03'))  AND E0122=codeitemid ORDER BY E0122, A0101";
								this.frowset = dao.search(sqlStr);
								while (this.frowset.next())
								{
								    String a0101 = this.frowset.getString("a0101");
								    String codeitemdesc = this.frowset.getString("codeitemdesc");
					
								    if (map.get(codeitemdesc) == null)
								    {
										ArrayList list = new ArrayList();
										list.add(a0101);
										map.put(codeitemdesc, list);
								    } else
								    {
										ArrayList list2 = (ArrayList) map.get(codeitemdesc);
										list2.add(a0101);
										map.put(codeitemdesc, list2);
								    }
								}
								if (map.size() > 0)
								{
								    int pcount = 0;
								    flag = "2"; 
								    logoFlag = "2";
								    info = "考核计划：" +plan_id+"."+ vo.getString("name") + "\n";
								    info += "下列考核对象的目标卡没有制定完成或未被批准:\n";
								    Set keyset = map.keySet();
								    for (Iterator iter = keyset.iterator(); iter.hasNext();)
								    {
										String e0122 = (String) iter.next();
										String str = "\n" + e0122 + ":";
										ArrayList emplist = (ArrayList) map.get(e0122);
										for (int i = 0; i < emplist.size(); i++)
										{
										    str += (String) emplist.get(i) + " ";
										    pcount++;
										}
										   
										info += str;
								    }
								    if(pcount>1)
								    {
										info=info.substring(0, info.length()-1);
										info+=" 共"+pcount+"人";
								    }								    
								    info += "\n强制启动计划(点击确定)，以上人员将不能进行评分。或返回(点击取消)，督促相关人员在其自助平台绩效管理的目标卡中完成相关业务后，再启动本计划！";
								    info += "\n请确定是否要强制启动？\n\n";
								}
						    } else if (!"2".equals(objectType) && isHaveTeamer)
						    {
								sqlStr = "select o.object_id,o.a0101 objectName,b.a0101 bodyName from per_object o,per_mainbody b where o.object_id=b.object_id and o.plan_id=b.plan_id and o.plan_id=" + plan_id;
								sqlStr += " and (o.sp_flag is Null or o.sp_flag<>'03') and b.body_id=-1";
								this.frowset = dao.search(sqlStr);
								String orgstr = "";
								int index=0;
								while (this.frowset.next())
								{
								    orgstr += this.frowset.getString("objectName") + "("+this.frowset.getString("bodyName")+")、";
								    index++;
								}
								if (orgstr.length() > 0)
								{
								    flag = "2";
								    logoFlag = "2";
								    info = "考核计划：" +plan_id+"."+ vo.getString("name") + "\n";
								    info += "下列考核对象(括号内为负责人)的目标卡没有制定完成或未被批准:\n";
								    info += orgstr.substring(0, orgstr.length()-1)+" 共"+index+"个部门";								    
								    info += "\n强制启动计划(点击确定)，以上考核对象将不能进行评分。或返回(点击取消)，督促相关人员在其自助平台绩效管理的目标卡中完成相关业务后，再启动本计划！";
								    info += "\n请确定是否要强制启动？\n\n";
								}
						    }
						    if(logoInfo!=null && logoInfo.trim().length()>0)
						    	logoInfo += info;
						    else
						    	logoInfo = info;
						}
//						this.getFormHM().put("flag", flag);
//						this.getFormHM().put("info", SafeCode.encode(info));
//						this.getFormHM().put("plan_ids", plan_ids);  // 需全部启动的计划id
//					    this.getFormHM().put("planId_s", planId_s);  // 需启动发邮件的计划id
				    }
				    if("1".equalsIgnoreCase(method))	 //  只考虑360计划
				    	this.getFormHM().put("gather_type", String.valueOf(gather_type));	//0 网上 1 机读 2:网上+机读	    
				}
				if(logoFlag!=null && logoFlag.trim().length()>0 && "2".equalsIgnoreCase(logoFlag))
				{
					this.getFormHM().put("flag", logoFlag);
					this.getFormHM().put("info", SafeCode.encode(logoInfo));
				}else
				{
					this.getFormHM().put("flag", flag);
					this.getFormHM().put("info", SafeCode.encode(info));
				}				
				this.getFormHM().put("plan_ids", plan_ids);  // 需全部启动的计划id
			    this.getFormHM().put("planId_s", planId_s);  // 需启动发邮件的计划id
			    
			    /*
			    String pending_system = SystemConfig.getPropertyValue("pending_system"); // 待办系统
				if(pending_system!=null && pending_system.trim().length()>0)
					this.getFormHM().put("pending_system", "yes");	
				else
					this.getFormHM().put("pending_system", "no");
				*/
			    this.getFormHM().put("pending_system", "yes");  //到发送代办交易类里判断 dengcan 2012-5-29
			    this.getFormHM().put("sporpfSeq", sporpfSeq);
			    
			} 		    
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}finally
		{
		    this.getFormHM().put("flag", flag);
		}
		
    }
    
}
