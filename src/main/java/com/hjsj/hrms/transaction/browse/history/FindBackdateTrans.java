package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class FindBackdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String backdate="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String orgbackdate = (String) this.getFormHM().get("orgbackdate");
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select create_date from hr_hisdata_list where create_date<="
					+ Sql_switcher.dateValue(orgbackdate)
					+ " order by create_date desc");
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				backdate = sdf.format(this.frowset.getDate("create_date"));
			} else {
				sql.setLength(0);
				sql.append("select create_date from hr_hisdata_list where create_date>="
						+ Sql_switcher.dateValue(orgbackdate)
						+ " order by create_date");
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) {
					backdate = sdf.format(this.frowset.getDate("create_date"));
				} 
			}
			
			
			String code=(String)this.getFormHM().get("code");		
			if(code==null||code.length()<=0)
				throw GeneralExceptionHandler.Handle(new GeneralException("得到机构单元编号为空，错误！"));
			sql.setLength(0);
			sql.append("select codesetid,parentid from organization where codeitemid='"+code+"'");
			String kind="" ;
			String orgtype="";	
			String codesetid="";		
			boolean isOrg=false;
			InfoUtils infoUtils=new InfoUtils();
			try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					codesetid=this.frowset.getString("codesetid");
					kind=infoUtils.getKindFormCodeSetId(codesetid);
					orgtype="org";
					isOrg=true;
				}
				if(!isOrg)
				{
					sql.setLength(0);
					sql.append("select codesetid,parentid from vorganization where codeitemid='"+code+"'");
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next())
					{
						codesetid=this.frowset.getString("codesetid");
						kind=infoUtils.getKindFormCodeSetId(codesetid);
						orgtype="vorg";					
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getFormHM().put("kind", kind);
			this.getFormHM().put("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.getFormHM().put("backdate",backdate);
		}
	}

}
