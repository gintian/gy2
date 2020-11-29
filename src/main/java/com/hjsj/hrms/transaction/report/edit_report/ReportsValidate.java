package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportCheck;
import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * 
 * <p>Title:ReportsValidate</p>
 * <p>Description:报表校验</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 9, 2006:11:15:20 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ReportsValidate extends IBusiness {

	public void execute() throws GeneralException {
		String tabids=(String)this.getFormHM().get("tabids");
		String operateObject=(String)this.getFormHM().get("operateObject");
		String unitcode=(String)this.getFormHM().get("unitcode");
		String appealUnitcode=(String)this.getFormHM().get("appealUnitcode");
		
		String updisk="true";
		String isSub="0";  //交验是否包含下级  0:不包含  1:包含
		String subunitup="true";  //报表上报判断下级单位是否上报
		try
		{
			//---------------------------------------
			UserView _userview=null;
			String userName = (String)this.getFormHM().get("username");
			userName = SafeCode.decode(userName);
			if(userName==null|| "".equals(userName)){
				userName = this.getUserView().getUserName();
				userView = this.getUserView();
			}else{
				_userview=new UserView(userName,this.getFrameconn());
				_userview.canLogin();
				userView =_userview;
			}
			//---------------------------------------
			//updisk=SystemConfig.getProperty("updisk");   //是否需要校验通过才能生成上报盘（true:需要 false:不需要）
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			 updisk=sysbo.getValue(Sys_Oth_Parameter.UPDISK);
			 subunitup=sysbo.getValue(Sys_Oth_Parameter.SUBUNITUP);
			 if(updisk==null||updisk.length()==0)
				 updisk="true";
			 if(subunitup==null||subunitup.length()==0)
				 subunitup="true";
			 
			 isSub=sysbo.getValue(Sys_Oth_Parameter.CONDISK);
			 isSub=isSub!=null&&isSub.trim().length()>0?isSub:"0";
		}
		catch(Exception e)
		{
			
		}
			
		String[] tabid=null;
		tabids = PubFunc.keyWord_reback(tabids);
		if(tabids.indexOf("/")==-1)
		{
			tabid=new String[1];
			tabid[0]=tabids;
		
		}
		else
			tabid=tabids.split("/");
		
		//返回信息：0：正确 1：表内校验错误 2：表间校验错误 3.此报表没有取数
		StringBuffer returnInfo=new StringBuffer("");
		StringBuffer tabid_str=new StringBuffer("");
		ArrayList tabidList=new ArrayList();
		for(int i=0;i<tabid.length;i++)
		{
			//如果某报表没有取数，则将其过滤，不予上报，并在提示信息中给予显示
			tabid_str.append("/"+tabid[i]);
			tabidList.add(tabid[i]);
		}

		String info="";
		//对于汇总单位上报时，逐层判断下级单位是否上报。
		if("true".equalsIgnoreCase(subunitup))
		{
			String _appealUnitcode=unitcode;
			if(!"2".equals(operateObject))
				_appealUnitcode=appealUnitcode;
			info=validateSubUnitIsAppeal(_appealUnitcode,tabidList);
			if(info.length()>0)
			{
				returnInfo.append("failed2");
				this.getFormHM().put("errorInfo",SafeCode.encode(info));
			}
		}
		if(info.length()==0)
		{
			if(updisk==null||(updisk!=null&& "true".equalsIgnoreCase(updisk)))
			{
				String sqlFlag=this.getUserView().getUserName();
				if("2".equals(operateObject))
					sqlFlag=unitcode;
				ReportCheck reportCheck=new ReportCheck(this.getFrameconn(),tabidList,Integer.parseInt(operateObject),sqlFlag);
				reportCheck.setUserView(this.userView);
				if(reportCheck.reportCheck())
					returnInfo.append("success");
				else 
					returnInfo.append("failed");
				
				
				if("success".equals(returnInfo.toString())&& "1".equals(isSub))
				{
					returnInfo.setLength(0);
					String errorInfo=getSubUnit(operateObject,unitcode,tabidList);
					if("success".equals(errorInfo))
						returnInfo.append("success");
					else
					{
						returnInfo.append("failed1");
						this.getFormHM().put("errorInfo",SafeCode.encode(errorInfo));
						
					}
				}
			}
			else
				returnInfo.append("success");
		}
		
		
		
		this.getFormHM().put("returnInfo",returnInfo.toString());
		this.getFormHM().put("tabid_str",tabid_str.substring(1));
	}
	
	
	
	
	/**
	 * 判断上报单位的下级单位是否上报
	 * @param unitcode
	 * @return
	 */
	public String validateSubUnitIsAppeal(String unitcode,ArrayList tabidList)
	{
		StringBuffer info=new StringBuffer("");
		try
		{
			StringBuffer ext_sql = new StringBuffer();
				Calendar d=Calendar.getInstance();
				int yy=d.get(Calendar.YEAR);
				int mm=d.get(Calendar.MONTH)+1;
				int dd=d.get(Calendar.DATE);
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select * from tt_organization where parentid='"+unitcode+"' and unitcode<>parentid "+ext_sql);
			StringBuffer subunit_str=new StringBuffer("");
			while(rowSet.next())
			{
				subunit_str.append(",'"+rowSet.getString("unitcode")+"'");
			}
			
			if(subunit_str.length()>0&&tabidList.size()>0)
			{
				StringBuffer tab_str=new StringBuffer("");
				for(int i=0;i<tabidList.size();i++)
					tab_str.append(","+(String)tabidList.get(i));
				rowSet=dao.search("select * from tname where tabid in ("+tab_str.substring(1)+") order by tabid");
				RowSet rowSet2=null;
				while(rowSet.next())
				{
					String tabid=rowSet.getString("tabid");
					String tsortid=rowSet.getString("tsortid");
					String tabname=rowSet.getString("name");
					String sql="select tt.* from treport_ctrl tc,tt_organization tt "
							  +" where tc.unitcode=tt.unitcode and tc.tabid="+tabid+" and tc.status<>1 and tc.unitcode in ("+subunit_str.substring(1)+")";
					rowSet2=dao.search(sql);
					StringBuffer unitname_str=new StringBuffer("");
					while(rowSet2.next())
					{
						String reporttypes=","+Sql_switcher.readMemo(rowSet2, "reporttypes");
						String report=Sql_switcher.readMemo(rowSet2,"report");
						if(reporttypes.indexOf(","+tsortid+",")!=-1&&report.indexOf(","+tabid+",")==-1)
						{
							unitname_str.append(","+rowSet2.getString("unitname"));
						}
					}
					if(unitname_str.length()>0)
					{
						info.append("\r\n "+tabid+":"+tabname+"\r\n    ");
						info.append("( 下属单位:"+unitname_str.substring(1)+" 没有上报! )");
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info.toString();
	}
	
	
	
	
	
	
	
	
	public String getSubUnit(String operateObject,String unitcode,ArrayList tabidList)
	{
		StringBuffer returnInfo=new StringBuffer("");
		try
		{
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<tabidList.size();i++)
				whl.append(","+(String)tabidList.get(i));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("1".equals(operateObject))
			{
				this.frowset=dao.search("select unitcode from operuser where userName='"+this.getUserView().getUserName()+"'");
				if(this.frowset.next())
					unitcode=this.frowset.getString(1);
			}
			StringBuffer ext_sql = new StringBuffer();
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			RowSet rowSet=dao.search("select unitcode,unitname from tt_organization where unitcode like '"+unitcode+"%' and unitcode<>'"+unitcode+"'  and parentid='"+unitcode+"' "+ext_sql+"");
			while(rowSet.next())
			{
				String a_unitcode=rowSet.getString("unitcode");
				String a_unitname=rowSet.getString("unitname");
				this.frowset=dao.search("select * from treport_ctrl where unitcode='"+a_unitcode+"' and tabid in ("+whl.substring(1)+") and status=1");
				ArrayList a_tabList=new ArrayList();
				while(this.frowset.next())
				{
					a_tabList.add(this.frowset.getString("tabid"));
				}
				
				if(a_tabList.size()>0)
				{
					ReportCheck reportCheck=new ReportCheck(this.getFrameconn(),a_tabList,2,a_unitcode);
					reportCheck.setUserView(this.userView);
					if(!reportCheck.reportCheck())
					{
						returnInfo.append("\r\n"+a_unitname+ResourceFactory.getProperty("edit_report.info6"));
					}
				}
			}
			
			ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
			String info=reportCollectBo.compareChildData(unitcode,tabidList);
			if(info.length()>0)
				returnInfo.append(info);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(returnInfo.length()==0)
			returnInfo.append("success");
		
		return returnInfo.toString();
	}
	

}
