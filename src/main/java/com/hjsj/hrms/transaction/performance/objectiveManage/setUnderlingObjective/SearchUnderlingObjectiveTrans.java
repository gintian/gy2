package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchUnderlingObjectiveTrans.java</p>
 * <p>Description>:员工目标</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 06, 2010 09:25:56 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchUnderlingObjectiveTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = "";
			String posid = "";
			String a0100="";
			String status="-2";
			String year="";
			String opt = (String)map.get("opt");
			String entranceType=(String)map.get("entranceType");
			String deptid="-1";
			
			String isTargetCardTemp = "0";// 目标卡制定是否需要发送邮件通知
			ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
            if ( this.frowset.next())
            {
                String str_value = this.frowset.getString("str_value");
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
                        }
                        
                    }
                }
            }
			if("1".equals(opt))
			{
			    plan_id = (String)map.get("plan_id");
				posid =(String)map.get("posid");
				a0100=(String)map.get("a0100");
			}
			else if("2".equals(opt))
			{
				plan_id=(String)this.getFormHM().get("plan_id");
				posid=(String)this.getFormHM().get("posid");
				a0100=(String)this.getFormHM().get("a0100");
				status = (String)this.getFormHM().get("status");
				deptid=(String)this.getFormHM().get("deptid");
			}
			else if("3".equals(opt))
			{
				plan_id=(String)map.get("plan_id");
				posid=(String)map.get("posid");
				a0100=(String)map.get("a0100");
				status = (String)map.get("status");
				deptid=(String)map.get("deptid");
			}else if("4".equals(opt))
			{
				plan_id="-1";
				posid=(String)map.get("posid");
				a0100=(String)map.get("a0100");
				year=(String)map.get("year");
			}
			if("-1".equals(posid))
				posid=this.userView.getUserPosId();
			if("-1".equals(a0100))
				a0100=this.userView.getA0100();
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn(),this.userView);
			ArrayList dbname = new ArrayList();
			dbname.add("USR");
			int type=0;
			LoadXml parameter_content =null;
			 if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{
						
		         	parameter_content = new LoadXml(this.getFrameconn(),plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
		    Hashtable params = parameter_content.getDegreeWhole();
			String targetMakeSeries=(String)params.get("targetMakeSeries");
			type=Integer.parseInt((targetMakeSeries==null|| "".equals(targetMakeSeries))?"1":targetMakeSeries);
			deptid=deptid==null?"-1":deptid;
			ArrayList personList = bo.getInPlanSubordinateStaff(posid, a0100, plan_id, dbname, status,year,type,this.getUserView(),deptid);
			ArrayList statusList = bo.getStatusList();
			ArrayList deptList = null;
			if("2".equals(opt)|| "3".equals(opt))
				deptList=(ArrayList)this.getFormHM().get("deptList");
			else
				deptList = bo.getDeptList();
			if("2".equals(opt))
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("status",status);
			this.getFormHM().put("personList",personList);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("posid",posid);
			this.getFormHM().put("a0100",a0100);	
			this.getFormHM().put("entranceType", entranceType);
			this.getFormHM().put("deptid",deptid);
			this.getFormHM().put("deptList", deptList);
			this.getFormHM().put("isTargetCardTemp", isTargetCardTemp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
