/**
 * 
 */
package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * @author t
 *
 */
public class SearchCodesetTrans extends IBusiness {

	private String firstnode;

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String seltree = (String) this.getFormHM().get("seltree");
		this.getFormHM().put("selstr", this.getSelStr(dao,seltree));
	}
	public String getSelStr(ContentDAO dao,String code) throws GeneralException {
		String sql = "select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'um' and codesetid<>'un'";
		StringBuffer strbf = new StringBuffer();
		strbf.append("<select name=\"seltree\">");

		try {
			RowSet rs = dao.search(sql);
			int i = 0;
			while (rs.next()) {
				i++;
				String codesetid = rs.getString("codesetid");
				String codesetdesc=rs.getString("codesetdesc");
				if (i == 1) {
					this.setFirstnode(codesetid);
				}
				String tepst= codesetid+" "+codesetdesc;
				if(tepst.length()>15){
					tepst=tepst.substring(0,15);
				}
				if(codesetid.equalsIgnoreCase(code)){
					strbf.append("<option value=" + codesetid + " selected=\"selected\">" + tepst
							+ "</option>");
				}else{
					strbf.append("<option value=" + codesetid + ">" + tepst
						+ "</option>");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		strbf.append("<option value=\"all\">全部</option>");
		strbf.append("</select>");
		return strbf.toString();
	}

	public String getFirstnode() {
		return firstnode;
	}

	public void setFirstnode(String firstnode) {
		this.firstnode = firstnode;
	}

}
