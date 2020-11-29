package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class DelVariablesTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String nid = (String)hm.get("nid");
		String cstate = (String)hm.get("cstate");
		nid=nid!=null&&nid.trim().length()>0?nid:"";
		try {
			if(nid.length()>0){
				String[] arr = nid.split(",");
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(cstate);
				
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<arr.length;i++)
					whl.append(","+arr[i]);
				RowSet rs = dao.search("select cname,chz from  midvariable where  nid in ("+whl.substring(1)+")");
				while(rs.next())
				{
					if(context.length()==0){
						context.append("删除:"+name+"("+cstate+")删除临时变量:<br>");
						context.append(rs.getString("chz")+"("+rs.getString("cname")+")");
					}else
						context.append(","+rs.getString("chz")+"("+rs.getString("cname")+")");
					}
				this.getFormHM().put("@eventlog", context.toString());
				
				if(arr.length>0){
					for(int i=0;i<arr.length;i++){
						String sqlstr = "delete from midvariable where nid="+arr[i]+"";						
						dao.update(sqlstr);	
					}
				}				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
