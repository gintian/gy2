package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchMarkDescTrans.java</p>
 * <p>Description>:总体评价</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-05-31 下午03:56:27</p>
 * <p>@version: 1.0</p>
 * <p>@author: JinChunhai
 */

public class SearchMarkDescTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String  planID=(String)hm.get("planID");
		String  objectID=(String)hm.get("objectID");
		String  mainbodyID=(String)hm.get("mainbodyID");
		
		
		if(planID!=null && planID.trim().length()>0 && "~".equalsIgnoreCase(planID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
        { 
            String _temp = planID.substring(1); 
            planID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
        }
		if(objectID!=null && objectID.trim().length()>0 && "~".equalsIgnoreCase(objectID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
        { 
        	String _temp = objectID.substring(1); 
        	objectID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
        }
		if(mainbodyID!=null && mainbodyID.trim().length()>0 && "~".equalsIgnoreCase(mainbodyID.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
        { 
        	String _temp = mainbodyID.substring(1); 
        	mainbodyID = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
        }
		
		String  operater="";
		if(hm.get("operater")!=null)
			operater=(String)hm.get("operater");
		String desc="";
		String status="";
		String a_status="1";      // 1:可以修改状态   0：不可以修改状态
		String Descctrl="0";      //=0(为空或等0)时，匿名 =1记名
		String Plan_type="1";    //0:不记名  1:记名（default）
		String method="1";       //1:360度考核 2：目标考核
		{
			LoadXml loadxml=new LoadXml(this.getFrameconn(),planID);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String performanceType=(String)htxml.get("performanceType");		//考核形式  0：绩效考核  1：民主评测
			this.getFormHM().put("performanceType",performanceType);
		}
		try
		{
		
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 RowSet rowSet=dao.search("select object_type,Plan_type,method from per_plan where plan_id="+planID);
			 int object_type=2;
			 if(rowSet.next())
			 {
				 object_type=rowSet.getInt(1);
				 Plan_type=rowSet.getString(2);
				 method=rowSet.getString(3)!=null?rowSet.getString(3):"1";
			 }
			if(!"4".equals(operater))
			{
				MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn());
				String[] temp=markStatusBo.getPerMainBodyDesc(planID,objectID,mainbodyID,object_type);
				desc=temp[0];
				status=temp[1];
			}
		
		
				if("4".equals(operater)|| "1".equals(operater))
				{
					this.frowset=dao.search("select status from per_plan where plan_id="+planID);
					if(this.frowset.next())
					{
						int temp=this.frowset.getInt("status");
						if(temp==7)
							a_status="0";
					}
				}
				else
				{
				//	System.out.println("select status from per_mainbody where plan_id="+planID+" and mainbody_id='"+mainbodyID+"'  and object_id='"+objectID+"'");
					String sql="select status,Descctrl from per_mainbody where plan_id="+planID+" and mainbody_id='"+mainbodyID+"'  and object_id='"+objectID+"'";
					if(object_type==1||object_type==3||object_type==4)
					{
						if(objectID.equals(mainbodyID))
							sql="select status,Descctrl from per_mainbody where plan_id="+planID+" and mainbody_id=(select mainbody_id from per_mainbody where  plan_id="+planID+" and object_id='"+objectID+"'  and body_id=-1)  and object_id='"+objectID+"'";
					}
					
					this.frowset=dao.search(sql);
					if(this.frowset.next())
					{
						int temp=this.frowset.getInt("status");
						if(temp==2||temp==7)
							a_status="0";
						Descctrl=this.frowset.getString("Descctrl")!=null?this.frowset.getString("Descctrl"):"0";
					}
				}
			 
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("method",method);
		this.getFormHM().put("plan_type",Plan_type);
		 
		this.getFormHM().put("descctrl",Descctrl);
		this.getFormHM().put("description",desc);
		this.getFormHM().put("isNoMark",status);
		this.getFormHM().put("status",a_status);
		/*
		if(status.equals("0"))
			this.getFormHM().put("isNoMark","0");
		else
			this.getFormHM().put("isNoMark","4");
		*/
	}

}
