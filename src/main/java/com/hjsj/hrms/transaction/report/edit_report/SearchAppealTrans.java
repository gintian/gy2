package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:</p>
 * <p>Description:搜索表类下面的报表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 20, 2006:4:29:03 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SearchAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sortId=(String)this.getFormHM().get("sortId");
		String operateObject=(String)this.getFormHM().get("operateObject"); //1:未上报的表 2：汇总表
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList  list=new ArrayList();
		ArrayList  tsortList=new ArrayList();
		
		try
		{
			TnameBo tnameBo=new TnameBo(this.getFrameconn());
			HashMap scopeMap = tnameBo.getScopeMap();
			String unicode=tnameBo.getUnitcode(this.getUserView().getUserName());
			
			String sql="";
			ArrayList subUnitList = (ArrayList)this.getFormHM().get("subUnitList");
			String appealUnitCode = (String)this.getFormHM().get("appealUnitCode");
			if(subUnitList!=null&&subUnitList.size()!=1&&appealUnitCode!=null&&appealUnitCode.length()>0){
				unicode =appealUnitCode;
			}
			if(unicode!=null&&!"".equals(unicode)){
			if("all".equals(sortId))
			{
				TTorganization ttorganization=new TTorganization(this.getFrameconn());
				RecordVo vo=ttorganization.getSelfUnit(this.getUserView().getUserName());
				String types=vo.getString("reporttypes");
				types=types.substring(0,types.lastIndexOf(","));
				sql="select tname.tabid,tname.name,treport_ctrl.status,tsortid  from tname,treport_ctrl where tname.tabid=treport_ctrl.tabid and treport_ctrl.unitcode='"+unicode+"'  and tsortid in ("+types+")";
			}	
			else
				sql="select tname.tabid,tname.name,treport_ctrl.status,tsortid  from tname  ,treport_ctrl where tname.tabid=treport_ctrl.tabid and treport_ctrl.unitcode='"+unicode+"' and tsortid="+sortId;
			
			this.frowset=dao.search(sql);
			String sortName="";
			String tsortid="";
			while(this.frowset.next())
			{
				if(!"2".equals(operateObject)&&!this.getUserView().isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
					continue;
				if(scopeMap!=null&&scopeMap.get(this.frowset.getString("tabid"))!=null&& "1".equals(scopeMap.get(this.frowset.getString("tabid")))){
					continue;
				}
				DynaBean bean = new LazyDynaBean();
				bean.set("tabid",this.frowset.getString("tabid"));
				bean.set("name",this.frowset.getString("name"));
				bean.set("statusname",getStatusName(this.frowset.getString("status")));
				String app = "0";
				boolean isapp = isApp(this.frowset.getString("tabid"),unicode);
				if(isapp){
					app = "1";
				}
				bean.set("app", app);
				String status="-1";
				if(this.frowset.getString("status")!=null)
					status=this.frowset.getString("status");
				bean.set("status",status);
				tsortid=this.frowset.getString("tsortid");
				list.add(bean);
			}
			}else{
				//从资源里找
				String report="";
			//	StringBuffer sql =new StringBuffer();
				String reportTypes="";
//				SysPrivBo privbo=null;
//				if(userView.getStatus()==4) //自助用户关联业务用户
//				{
//					privbo=new SysPrivBo(userView.getDbname()+""+userView.getUserId(),"4",this.getFrameconn(),"warnpriv");
//				}else{
//					privbo=new SysPrivBo(userView.getUserName(),"0",this.getFrameconn(),"warnpriv");
//				}
//				String res_str=privbo.getWarn_str();
//				 if(res_str!=null&&res_str.indexOf("<Report>")!=-1){
//					 report =  res_str.substring(res_str.indexOf("<Report>")+8,res_str.indexOf("</Report>"));
//				 }
				sql="select tabid  from tname";
				this.frowset = dao.search(sql);
				while(this.frowset.next())
	    		{
				
				if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
				{
					report+=	this.frowset.getString("tabid")+",";
				}
	    		}

				if(userView.isSuper_admin()){
					
					if("all".equals(sortId))
					{
						
							sql ="select tabid,name,paper,tsortid,-1 status  from tname  ";
					}	
					else
						sql="select tabid,name,paper,tsortid,-1 status  from tname where   tsortid="+sortId;
				
				}else{
				report = report.replace(" ", "");
				report = report.replace("R", "");
				while(report.indexOf(",,")!=-1){
					report = report.replace(",,", ",");
				}
				if (report.length()>0&&report.charAt(report.length() - 1) == ',') {
					report = report.substring(0, report.length() - 1);
				}
				if (report.length()>0&&report.charAt(0) == ',') {
					report = report.substring(1, report.length());
				}
				
				if("all".equals(sortId))
				{
					
						sql ="select tabid,name,paper,tsortid,-1 status  from tname  where  tabid in ("+report+") ";
				}	
				else
					sql="select tabid,name,paper,tsortid,-1 status  from tname where   tsortid="+sortId+" and   tabid in ("+report+")";
			
				
				}
				

				this.frowset=dao.search(sql);
				String sortName="";
				String tsortid="";
				while(this.frowset.next())
				{
					if(!this.getUserView().isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
						continue;
					if(scopeMap!=null&&scopeMap.get(this.frowset.getString("tabid"))!=null&& "1".equals(scopeMap.get(this.frowset.getString("tabid")))){
						continue;
					}
					DynaBean bean = new LazyDynaBean();
					bean.set("tabid",this.frowset.getString("tabid"));
					bean.set("name",this.frowset.getString("name"));
					bean.set("statusname",getStatusName("0"));
					String status="0";
					bean.set("status",status);
					tsortid=this.frowset.getString("tsortid");
					list.add(bean);
				}
			}
			
			this.getFormHM().put("appealInfoList",list);
			this.getFormHM().put("sortId",sortId);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

	
	public String getStatusName(String status)
	{
		String statusName="";
		if(status==null|| "-1".equals(status))
			statusName=ResourceFactory.getProperty("edit_report.status.wt");
		else if("0".equals(status))
		{
			statusName=ResourceFactory.getProperty("edit_report.status.bj");
		}
		else if("1".equals(status))
		{
			statusName=ResourceFactory.getProperty("edit_report.status.ysb");
		}
		else if("2".equals(status))
		{
			statusName=ResourceFactory.getProperty("edit_report.status.dh");
		}
		else if("3".equals(status))
		{
			statusName=ResourceFactory.getProperty("edit_report.status.fc");
		}else if("4".equals(status)){
			statusName="审批中";
		}
		return statusName;
	}
	/**
	 * 判断当前用户是否有审批权限
	 * @param tabid
	 * @param unitcode
	 * @return
	 */
	public boolean isApp(String tabid,String unitcode){
		boolean isapp = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(this.userView.getUserName().equals(user)||user==null){
					isapp = true;
				}
				if("1".equals(status)){
					isapp = false;
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return isapp;
	}
	
	
}
