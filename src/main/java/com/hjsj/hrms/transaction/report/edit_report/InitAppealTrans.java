package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * <p>Title:InitAppealTrans</p>
 * <p>Description:初始化报表上报页面</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 9, 2006:11:08:31 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class InitAppealTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String username=SafeCode.decode((String)this.getFormHM().get("username1"));
		String user = this.userView.getUserName();
		if(username==null|| "".equals(username)){
			username = this.getUserView().getUserName();
		}
		userView = new UserView(username, this.frameconn);
		try {
			userView.canLogin();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		String operateObject=(String)this.getFormHM().get("operateObject"); //1:未上报的表 2：汇总表
		String isApproveflag=(String)this.getFormHM().get("isApproveflag");
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList  list=new ArrayList();
		ArrayList  tsortList=new ArrayList();		
		try
		{
			TnameBo tnameBo=new TnameBo(this.getFrameconn());
			String unicode="";
			String unitname="";

			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			if("1".equals(operateObject))
			{
				RecordVo unitVo=ttorganization.getSelfUnit(username);
				if(unitVo!=null){
				unicode=unitVo.getString("unitcode");
				unitname=unitVo.getString("unitname");
				}
			}
			else
			{
				RecordVo unitVo=ttorganization.getSelfUnit2((String)this.getFormHM().get("unitcode"));
				if(unitVo!=null){
				unicode=unitVo.getString("unitcode");
				unitname=unitVo.getString("unitname");
				}
			}
			ArrayList subUnitList=new ArrayList();	
			String sortName="";
			String tsortid="";
			 TnameBo tnamebo  = new TnameBo(this.getFrameconn());
				HashMap scopeMap = tnamebo.getScopeMap();
			if(!"".equals(unicode)){
			 subUnitList=getSubUnitList(unicode,operateObject,unitname);   //上报单位列表
			if(subUnitList.size()!=1){
				this.getFormHM().put("appealUnitCode", "");
			}
			String sql="select tname.tabid,tname.name,treport_ctrl.status,(select name from tsort where tsortid=(select tsortid from tname where tabid="+tabid+") ) sortName,tsortid   from tname, treport_ctrl where tname.tabid=treport_ctrl.tabid and treport_ctrl.unitcode='"+unicode+"'  and tsortid=(select tsortid from tname where tabid="+tabid+") ";
	
			this.frowset=dao.search(sql);
			
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
				String status="-1";
				String app = "0";
				if(this.frowset.getString("status")!=null)
					status=this.frowset.getString("status");
				boolean isapp = isApp(this.frowset.getString("tabid"),unicode,username,user);
				if(isapp){
					app = "1";
				}
				bean.set("app", app);
				bean.set("status",status);
				bean.set("sortname",this.frowset.getString("sortName"));
				sortName=this.frowset.getString("sortName");
				tsortid=this.frowset.getString("tsortid");
				
				list.add(bean);
			}
			}else{

				//从资源里找
				String report="";
				StringBuffer sql =new StringBuffer();
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
				sql.delete(0, sql.length());
				sql.append("select tabid  from tname");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
	    		{
				
				if(userView.isHaveResource(IResourceConstant.REPORT,this.frowset.getString("tabid")))
				{
					report+=	this.frowset.getString("tabid")+",";
				}
	    		}

				if(userView.isSuper_admin()){
					sql.delete(0, sql.length());
					
					sql.append("select tabid,name,paper,tsortid,(select name from tsort where tsortid=(select tsortid from tname where tabid="+tabid+") ) sortName  from tname  ");
				
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
				
				sql.delete(0, sql.length());
			
				sql.append("select tabid,name,paper,tsortid,(select name from tsort where tsortid=(select tsortid from tname where tabid="+tabid+") ) sortName  from tname where tabid in ("+report+") order by tsortid ");
				
				}
				if(report.length()==0){
					// 用户没有权限操作任何报表
					
				}else{
					
				this.frowset=dao.search(sql.toString());
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
					bean.set("sortname",this.frowset.getString("sortName"));
					sortName=this.frowset.getString("sortName");
					tsortid=this.frowset.getString("tsortid");
					
					list.add(bean);
					if(reportTypes.indexOf(""+this.frowset.getInt("tsortid"))==-1)
						reportTypes+=this.frowset.getInt("tsortid")+",";
				}
				sql.delete(0, sql.length());
				sql.append("select tsortid,name from tsort where ");
				StringBuffer sql_sub=new StringBuffer("");
				if (!"".equals(reportTypes)&&reportTypes.charAt(reportTypes.length() - 1) == ',') {
					reportTypes = reportTypes.substring(0, reportTypes.length() - 1);
				}
				if(reportTypes.length()>0){
					CommonData a_data=new CommonData("all",ResourceFactory.getProperty("edit_report.All"));
					tsortList.add(a_data);
					tsortid = "all";
					String reportTypes2 []= reportTypes.split(",");
					for(int i=0;i<reportTypes2.length;i++)
					{
						RecordVo vo= new RecordVo("tsort");
						vo.setInt("tsortid", Integer.parseInt(reportTypes2[i]));
						vo = dao.findByPrimaryKey(vo);
						CommonData data=new CommonData(String.valueOf(vo.getInt("tsortid")),vo.getString("name"));
						tsortList.add(data);
					}
				}
				}
				
			}
			if(!"".equals(unicode)){
			ArrayList a_sortList=ttorganization.getSelfSortList(username);
			if(a_sortList.size()>0)
			{
				CommonData a_data=new CommonData("all",ResourceFactory.getProperty("edit_report.All"));
				tsortList.add(a_data);
			}
			for(Iterator t=a_sortList.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();
				CommonData data=new CommonData(String.valueOf(vo.getInt("tsortid")),vo.getString("name"));
				tsortList.add(data);
			}
			}
			this.getFormHM().put("subUnitList",subUnitList);
			this.getFormHM().put("tsortList",tsortList);
			this.getFormHM().put("appealInfoList",list);
			this.getFormHM().put("sortName",sortName);
			this.getFormHM().put("sortId",tsortid);
			this.getFormHM().put("unitcode",unicode);
			this.getFormHM().put("isApproveflag",isApproveflag);
			this.getFormHM().put("selfUnitcode",(String)this.getFormHM().get("selfUnitcode"));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	public ArrayList getSubUnitList(String unitcode,String operateObject,String unitname)
	{
		ContentDAO dao=new ContentDAO(this.frameconn);
		ArrayList  unitList=new ArrayList();
		
		try
		{
			if("1".equals(operateObject))
			{
				
			    CommonData a_data=new CommonData(unitcode,unitname);
			    unitList.add(a_data);
			    Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				StringBuffer ext_sql = new StringBuffer();
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				
			    this.frowset=dao.search("select * from tt_organization where parentid='"+unitcode+"' and unitcode<>parentid "+ext_sql+"");
			    while(this.frowset.next())
			    {
			    	String a_unitcode=this.frowset.getString("unitcode");
			    	String a_unitname=this.frowset.getString("unitname");
			    	CommonData data=new CommonData(a_unitcode,a_unitname);
			    	unitList.add(data);
			    }
			 
		
				
			}
			else
			{
				CommonData data=new CommonData(unitcode,unitname);
		    	unitList.add(data);				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return unitList;
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
		}else if("4".equals(status))
		{
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
	public boolean isApp(String tabid,String unitcode,String username,String zxguser){
		boolean isapp = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(zxguser.equals(user)){
					isapp = true;
				}else if((user==null|| "".equals(user))&&username.equals(zxguser)){
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
