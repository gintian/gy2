package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 历史数据初始化
 * <p>Title:InitHistoryStaticTrans.java</p>
 * <p>Description>:InitHistoryStaticTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 23, 2010 9:54:25 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class InitHistoryStaticTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer sql=new StringBuffer();
		sql.append("select id,name from sname where archive_set is not null ");
		if(Sql_switcher.searchDbServer()==com.hrms.hjsj.sys.Constant.MSSQL)
			sql.append(" and archive_set<>''");
		sql.append(" and type='1'");
		sql.append(" and id in(");
		sql.append("select id from slegend where archive_set is not null and sname.id=slegend.id");
		if(Sql_switcher.searchDbServer()==com.hrms.hjsj.sys.Constant.MSSQL)
			sql.append(" and archive_set<>''");
		sql.append(" and type='1'");
		sql.append(") order by snorder");
		ArrayList statlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String statid="-1";
		try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				
				if((this.userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
   	            {
					if("-1".equals(statid))
						statid=this.frowset.getString("id");
					CommonData da=new CommonData();
					da.setDataName(this.frowset.getString("name"));
					da.setDataValue(this.frowset.getString("id"));
					statlist.add(da);
   	            }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(statid==null||statid.length()<=0|| "-1".equalsIgnoreCase(statid))
	    	throw GeneralExceptionHandler.Handle(new GeneralException("统计项错误！或没有统计历史数据！"));
		/*String cyc_date=PubFunc.getStringDate("yyyy-MM-dd");*/
		this.getFormHM().put("cyc_Sdate", "");
		this.getFormHM().put("cyc_Edate", "");
		this.getFormHM().put("cyc_year", "");
		this.getFormHM().put("cyc_year_e", "");
		this.getFormHM().put("statid", statid);
		this.getFormHM().put("statlist", statlist);
		ArrayList graph_list=new ArrayList();
		CommonData da=new CommonData();
		da.setDataName("直方图");
		da.setDataValue("1");
		graph_list.add(da);
		da=new CommonData();
		da.setDataName("线状图");
		da.setDataValue("2");
		graph_list.add(da);
		this.getFormHM().put("graph_list", graph_list);
		String acode= getOrgids();
		this.getFormHM().put("acode", acode);
		this.getFormHM().put("init", "true");
	}
	private String getOrgids()
	{
		String orgs="";
		if(this.userView.isSuper_admin())
    		orgs=getParentid();
    	else{
    		orgs=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
    		if(orgs.length()==2)
    			orgs=getParentid();
    	}
		
		return orgs;
	}
	/**
	 * 得到父亲节点
	 * @return
	 */	
	private String getParentid()
	{
		String sql="select codesetid,codeitemid from organization where codeitemid=parentid";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String orgid="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
				orgid=this.frowset.getString("codesetid")+this.frowset.getString("codeitemid");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orgid;
	}
}
