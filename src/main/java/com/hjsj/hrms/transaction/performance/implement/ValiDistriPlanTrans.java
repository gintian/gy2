package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *<p>Title:ValiDistriPlanTrans.java</p> 
 *<p>Description:校验分发计划</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2009-06-13</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class ValiDistriPlanTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
		String plan_ids = (String)this.getFormHM().get("plan_id");
		String signLogo = (String)this.getFormHM().get("signLogo"); // 批量分发的标志
		String[] planIds = null;
		if(signLogo!=null && signLogo.trim().length()>0 && "batchDistribute".equalsIgnoreCase(signLogo))
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
		
		String flag="1";
		String info="";
		try
		{
			if(planIds!=null && planIds.length>0)
			{	
				PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
				
				for (int k = 0; k < planIds.length; k++)
				{
					String plan_id = planIds[k];			
					RecordVo vo = pb.getPerPlanVo(plan_id);
					
				    String sqlStr="select count(*) from per_object where plan_id="+plan_id;
				    this.frowset = dao.search(sqlStr);
				    int count = 0;
				    if(this.frowset.next())
					count = this.frowset.getInt(1);
				    if(count==0)
				    {
				        flag="0";
				        if(signLogo!=null && signLogo.trim().length()>0 && "batchDistribute".equalsIgnoreCase(signLogo))
				        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n没有设置考核对象，不能分发！";
				        else
				        	info="没有设置考核对象，不能分发！";
				        this.getFormHM().put("flag", flag);
				        this.getFormHM().put("info", SafeCode.encode(info));
				        this.getFormHM().put("plan_ids", plan_ids);
				        return;
				    }				   
				    
				    if("2".equalsIgnoreCase(String.valueOf(vo.getInt("object_type"))))
				    {
						// 对目标管理计划，对个人的计划由BS处理考核主体，但考核关系为非标准的需检查非本人主体
						sqlStr="SELECT A0101 FROM per_object WHERE plan_id="+plan_id+" AND Kh_relations=1 "
								+"AND not object_id IN (SELECT object_id FROM per_mainbody where plan_id="+plan_id
								+" AND body_id<>5 GROUP BY object_id)";
						String a0101 = "";
						int i=0;
						this.frowset = dao.search(sqlStr);
						while(this.frowset.next())
						{
						    i++;
						    if(i==1)
							a0101 = this.frowset.getString(1);
						    flag="0";	   
						}
						if(i>1)
						    a0101+="等";	
						if(signLogo!=null && signLogo.trim().length()>0 && "batchDistribute".equalsIgnoreCase(signLogo))
				        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n" +a0101+i+"个考核对象设定为非标准考核关系，但没有设置非本人考核主体！";
						else
							info=a0101+i+"个考核对象设定为非标准考核关系，但没有设置非本人考核主体！"; 		
				    }
				    else
				    {
						
				//		 对目标管理计划，非人员类别的考核计划，考核关系为非标准的需检查团队负责人以外的考核主体
						sqlStr="SELECT A0101 FROM per_object WHERE plan_id="+plan_id+" AND Kh_relations=1 "
								+"AND not object_id IN (SELECT object_id FROM per_mainbody where plan_id="+plan_id
								+" AND body_id<>-1 GROUP BY object_id)";
						String a0101 = "";
						int i=0;
						this.frowset = dao.search(sqlStr);
						while(this.frowset.next())
						{
						    i++;
						    if(i==1)
							a0101 = this.frowset.getString(1)+" ";
						    flag="0";	   
						}
						if(i>1)
						    a0101+="等"+i+"个考核对象";		
						if(i>0)
						{
							if(signLogo!=null && signLogo.trim().length()>0 && "batchDistribute".equalsIgnoreCase(signLogo))
					        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n" +a0101+"设定为非标准考核关系，但没有设置除团队负责人以外的考核主体！";
							else
								info=a0101+"设定为非标准考核关系，但没有设置除团队负责人以外的考核主体！"; 
						    this.getFormHM().put("flag", flag);
						    this.getFormHM().put("info", SafeCode.encode(info));
						    this.getFormHM().put("plan_ids", plan_ids);
						    return;
						}		 
						//考虑到非人员的目标计划没有团队负责人时候没有分发这个阶段，所以在分发校验时候就不用再检查是否设置了团队负责人了
				//		boolean isHaveTeamer = false;// 考核计划是否设置了 团队负责人 的主体类别
				//		sqlStr = " select * from per_plan_body where  body_id=-1 and plan_id=" + plan_id;
				//		this.frowset = dao.search(sqlStr);
				//		if (this.frowset.next())
				//		    isHaveTeamer = true;
				//		
				//		if(isHaveTeamer)
				//		{
						
							if(signLogo!=null && signLogo.trim().length()>0 && "batchDistribute".equalsIgnoreCase(signLogo))
					        	info="考核计划：" +plan_id+"."+ vo.getString("name") + "\n";
						
						    int n1 = 0;
						    int n2=0;
						  
						    HashMap objs = new HashMap();
						    sqlStr = " select * from per_object where plan_id=" + plan_id;
						    this.frowset = dao.search(sqlStr);
						    while (this.frowset.next())
						    {
								String object_id = (String)this.frowset.getString("object_id");
								a0101 = (String)this.frowset.getString("a0101");
								objs.put(object_id, a0101);
						    }		
						    
						    Set objset = objs.keySet();
						    for (Iterator iter = objset.iterator(); iter.hasNext();)
						    {			
								String object_id = (String) iter.next();
								sqlStr = " select count(*) from per_mainbody where  body_id=-1 and plan_id=" + plan_id+" and object_id='"+object_id+"'";
							    this.frowset = dao.search(sqlStr);
							    if (this.frowset.next())
							    {
									n1 =  this.frowset.getInt(1);
									if(n1!=1)
									{
									    n2++;
									    flag="0";  
									}				
							    }
								
							    if(n1!=1 && n2==1)
							    	info+=(String)objs.get(object_id);
						    }
						  
						if(n2>1)
						    info+=" 等"+n2+"个考核对象的团队负责人没有设置或多于一个，团队负责人必须有且只有一个。";
						else if(n2==1)
						    info+=" 的团队负责人没有设置或多于一个，团队负责人必须有且只有一个。";
				//		}		
				    }
				    if ("0".equals(flag))
				    {
						this.getFormHM().put("flag", flag);
						this.getFormHM().put("info", SafeCode.encode(info));
						this.getFormHM().put("plan_ids", plan_ids);
						return;
				    }
				}  
				
				//是否满足发邮件的条件
				String isSendEmail = "0";			
				String isTargetCardTemp ="false";//是否需要目标卡制定模板
				String targetCardTemp ="-1";//目标卡制定模板				    
				RowSet rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
				if (rowSet.next())
				{
					String str_value = rowSet.getString("str_value");
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
							child = ele.getChild("TargetCard");
							if (child != null)
							{
								isTargetCardTemp = child.getAttributeValue("email");
								targetCardTemp = child.getAttributeValue("template");
							}
						}
						if("true".equals(isTargetCardTemp)&&!"-1".equals(targetCardTemp))
						    isSendEmail="1";
					}
				}
				if(rowSet!=null)
				    rowSet.close();
				 
				String pending_system = SystemConfig.getPropertyValue("pending_system"); // 待办系统
				if(pending_system!=null && pending_system.trim().length()>0)
					this.getFormHM().put("pending_system", "yes");	
				else
					this.getFormHM().put("pending_system", "no");	
				this.getFormHM().put("isSendEmail", isSendEmail);	
				this.getFormHM().put("flag", flag);
				this.getFormHM().put("info", SafeCode.encode(info));
				this.getFormHM().put("plan_ids", plan_ids);
				
			}		    
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    }
}
