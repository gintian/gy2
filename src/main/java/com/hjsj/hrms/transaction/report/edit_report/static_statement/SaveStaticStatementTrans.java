/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class SaveStaticStatementTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
	String scopename = SafeCode.decode(this.getFormHM().get("scopename")==null?"":(String)this.getFormHM().get("scopename"));
	String scopeownerunit = SafeCode.decode(this.getFormHM().get("scopeownerunit")==null?"":(String)this.getFormHM().get("scopeownerunit"));
	String scopeunits = SafeCode.decode(this.getFormHM().get("scopeunits")==null?"":(String)this.getFormHM().get("scopeunits"));
	String scopeid2 = SafeCode.decode(this.getFormHM().get("scopeid")==null?"":(String)this.getFormHM().get("scopeid"));
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	String displayid="";
	int scopeid =1;
	try {
		StringBuffer sql = new StringBuffer();
	if("".equals(scopeid2)){
		
		String sql2  = "select "+Sql_switcher.sqlNull("scopeid", "0")+" scopeid from tscope order by scopeid desc";
		this.frowset = dao.search(sql2);
		if(this.frowset.next()){
			scopeid = this.frowset.getInt("scopeid")+1;
		}
		sql.append("insert into tscope (scopeid,name,units,owner_unit,displayid) values (?,?,?,?,?)");
		ArrayList list = new ArrayList();
		list.add(""+scopeid);
		list.add(scopename);
		list.add(scopeunits);
		list.add(scopeownerunit);
		list.add(""+scopeid);
		dao.insert(sql.toString(), list);
		displayid = ""+scopeid;
	}else{
		scopeid = Integer.parseInt(scopeid2);
		sql.append("update tscope set name=? , units=?,owner_unit=? where scopeid=?");
		ArrayList list = new ArrayList();
		list.add(scopename);
		list.add(scopeunits);
		list.add(scopeownerunit);
		list.add(scopeid2);
		dao.update(sql.toString(),list);
		RecordVo vo = new RecordVo("tscope");
		vo.setInt("scopeid", Integer.parseInt(scopeid2));
		vo = dao.findByPrimaryKey(vo);
		displayid = ""+vo.getInt("displayid");
		
	}
	
	
	
	
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
	this.getFormHM().put("info", ""+scopeid);
	this.getFormHM().put("scopename", scopename);
	this.getFormHM().put("scopeid",""+scopeid);
	this.getFormHM().put("displayid", displayid);
	}
	
}
