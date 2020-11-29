package com.hjsj.hrms.transaction.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class GetOrganizationTypeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		if(codeitemid==null||codeitemid.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("得到机构单元编号为空，错误！"));
		String sql="select codesetid,parentid from organization where codeitemid='"+codeitemid+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String kind="" ;
		String orgtype="";
		String codesetid="";
		String parentid="";
		boolean isOrg=false;
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				codesetid=this.frowset.getString("codesetid");
				parentid=this.frowset.getString("parentid");
				kind=getKindFormCodeSetId(codesetid);
				orgtype="org";
				isOrg=true;
			}
			if(!isOrg)
			{
				sql="select codesetid,parentid from vorganization where codeitemid='"+codeitemid+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					codesetid=this.frowset.getString("codesetid");
					parentid=this.frowset.getString("parentid");
					kind=getKindFormCodeSetId(codesetid);
					orgtype="vorg";					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("orgtype", orgtype);
		this.getFormHM().put("codesetid", codesetid);
		this.getFormHM().put("codeitemid", codeitemid);
		this.getFormHM().put("parentid", parentid);
		
	}
    private String getKindFormCodeSetId(String codesetid)
    {
    	String kind="";
    	if("UN".equalsIgnoreCase(codesetid))
			kind="2";
		else if("UM".equalsIgnoreCase(codesetid))
			kind="1";
		else if("@K".equalsIgnoreCase(codesetid))
			kind="0";
		else
			kind="2";
    	return kind;
    }
}
