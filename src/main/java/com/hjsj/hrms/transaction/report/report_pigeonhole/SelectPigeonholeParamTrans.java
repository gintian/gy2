package com.hjsj.hrms.transaction.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SelectPigeonholeParamTrans extends IBusiness {

	public void execute() throws GeneralException {
//		ArrayList reportSortList=new ArrayList();
		ArrayList infoList=new ArrayList();
//		String sortid="";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		try
		{
		
//			String    operate=(String)hm.get("operate");
//			this.getFormHM().put("operate",operate);
			
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			ArrayList a_sortList=ttorganization.getSelfSortList(this.getUserView().getUserName());
			ttorganization.setValidedateflag("1");
			RecordVo selfVo = ttorganization.getSelfUnit(this.getUserView().getUserName());
			if(selfVo==null)
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("edit_report.info12")+"！"));	
/*			if(operate.equals("2"))
			{
				reportSortList=getSortList(a_sortList);
				sortid=(String)this.getFormHM().get("sortid");
				if((sortid==null||sortid.trim().length()==0)&&reportSortList.size()>0)
				{
					CommonData data=(CommonData)reportSortList.get(0);
					sortid=data.getDataValue();
				}
				this.getFormHM().put("sortid",sortid);	
				infoList=getInfoList(sortid);
			}
			else if(operate.equals("1"))
			{
				for(Iterator t=a_sortList.iterator();t.hasNext();)
				{
					RecordVo vo=(RecordVo)t.next();
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id",String.valueOf(vo.getInt("tsortid")));
					abean.set("name",vo.getString("name"));
					infoList.add(abean);
				}
			}
			*/
			
			infoList=getSortList(a_sortList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
	//	this.getFormHM().put("reportSortList",reportSortList);
		this.getFormHM().put("infoList",infoList);
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&&!"dxt".equals(dxt))
			hm.remove("returnvalue");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
	}
	
	
	public ArrayList getInfoList(String sortid)
	{
		ArrayList list=new ArrayList();
		try
		{
			if(sortid!=null&&sortid.trim().length()>0)
			{
				String sql="select tabid,name from tname  where tsortid="+sortid;
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id",this.frowset.getString("tabid"));
					abean.set("name",this.frowset.getString("name"));
					list.add(abean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList getSortList(ArrayList a_sortList)
	{
		ArrayList list=new ArrayList();
		try
		{
			
			CommonData a_data=new CommonData("","");
			list.add(a_data);
			
			for(Iterator t=a_sortList.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				if(this.isNode(String.valueOf(vo.getInt("tsortid")))){//dml 2011-04-11
					CommonData data=new CommonData(String.valueOf(vo.getInt("tsortid")),String.valueOf(vo.getInt("tsortid"))+":"+vo.getString("name"));
					list.add(data);
				}
			}
		/*	if(a_sortList.size()>0)
			{
				CommonData a_data=new CommonData("all",ResourceFactory.getProperty("edit_report.All"));
				list.add(a_data);
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * dml 2011-04-11 过滤掉 在资源管理中未给该用户划分该表类下一张表 的表类*/
	public boolean isNode(String sortid)
	{
		boolean flag=false;
		ContentDAO dao=new ContentDAO(this.frameconn);
		try
		{
			
			StringBuffer sql=new StringBuffer("");
			sql.append("select * from treport_ctrl where unitcode=(select unitcode from operUser where userName='");
			sql.append(this.userView.getUserName());
			sql.append("') and tabid in (select TabId from tname  where TSortId="+sortid+" )");
			this.frowset = dao.search(sql.toString());
			int num=0;
			while(this.frowset.next())
			{
				String tabid=this.frowset.getString("tabid");
				if(!this.userView.isHaveResource(IResourceConstant.REPORT,tabid))
					continue;
				num++;
			}
			if(num>0)
				flag=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
}
