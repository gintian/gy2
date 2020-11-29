package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hjsj.hrms.businessobject.hire.JingPingPosBo;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 *<p>Title:Search_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 22, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Search_JP_Pos extends IBusiness {
	public void execute() throws GeneralException {
		try 
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			JingPingPosBo jpbo = new JingPingPosBo(this.getFrameconn());
			String a0100 = this.userView.getA0100();
			String dept = this.userView.getUserDeptId();
			String org = this.userView.getUserOrgId();		
			String selectStr = jpbo.getSelectStr();
			StringBuffer sqlstr = new StringBuffer();			
			sqlstr.append(" select "+selectStr+" from z07 ");
			StringBuffer wheresql = new StringBuffer();		
			wheresql.append(" where z0713='05' ");
			String check_apply_unit = " select * from z07 where z0711 is not null";
			RowSet rs = dao.search(check_apply_unit);
			if(rs.next())
			{
				wheresql.append(" and(");
				wheresql.append(Sql_switcher.substr("'"+dept+"'","1",Sql_switcher.length("z0711")));
				wheresql.append(" =z0711");
				wheresql.append(" or ");
				wheresql.append(Sql_switcher.substr("'"+org+"'","1",Sql_switcher.length("z0711")));
				wheresql.append(" =z0711)");
			}		
			if(this.userView.isSuper_admin())
			{
				wheresql.append(" and 1>2 ");
			}
			sqlstr.append(wheresql.toString());
			ArrayList fieldlist = jpbo.getApplylist();
			ArrayList applylist = dao.searchDynaList(sqlstr.toString().toUpperCase());  
//			System.out.println(dept);
//			System.out.println(org);
			String select = "select z07.z0700,"+selectStr+",state";
			select = select.toUpperCase();
			String where = " from z07 left join (select * from zp_apply_jobs where a0100 = '"+userView.getA0100()+"') zp_apply_jobs on z07.z0700 = zp_apply_jobs.z0700 "+wheresql;
//			System.out.println(where);
//			System.out.println(select);
//			System.out.println(sqlstr.toString());
			String checksql = "select * from zp_apply_jobs  where z0700 in (select z0700 from  z07) and a0100='"+a0100+"'";
			this.frowset = dao.search(checksql.toUpperCase());
			String applyflag = "no";
			if(this.frowset.next())
			{
				applyflag="yes";
			}
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("applylist", applylist);
			this.getFormHM().put("selectsql",select);
			this.getFormHM().put("wheresql",where);
			this.getFormHM().put("column","z0700,"+selectStr+",state");
			this.getFormHM().put("applyflag",applyflag);
			this.getFormHM().put("returnflag","true");
			
			EngageParamXML epXML = new EngageParamXML(this.getFrameconn());
			String maxpos_mess = epXML.getTextValue(EngageParamXML.APP_COUNT);
			if(maxpos_mess==null||maxpos_mess.length()<=0)
				maxpos_mess="";
			if(!"".equalsIgnoreCase(maxpos_mess)){
				String sql = "select count(*) num from zp_apply_jobs where a0100='"+a0100+"' and state = '02'";
				this.frowset = dao.search(sql);
				this.frowset.next();
				String num = this.frowset.getString("num");
				this.getFormHM().put("maxpos",maxpos_mess);
				this.getFormHM().put("choicepos",num);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}