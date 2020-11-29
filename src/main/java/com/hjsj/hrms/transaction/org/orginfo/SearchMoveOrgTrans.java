/*
 * Created on 2006-1-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchMoveOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String code=(String)this.getFormHM().get("code");
		StringBuffer sql=new StringBuffer();
		sql.append("select codeitemid,codeitemdesc,parentid,childid,a0000 from organization where parentid=");
		if(code!=null && code.length()>0)
		{
			sql.append("'");
			sql.append(code);
			sql.append("' and parentid<>codeitemid");
		}else
		{
			if(userView.isSuper_admin()){
				sql.append("codeitemid");
			}else{
				String busi = getBusi_org_dept(this.userView);
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						String[] tmps=busi.split("`");
							String a_code=tmps[0];
							if(a_code.length()>2){
								code=a_code.substring(2);
							}
					}
				}
				sql.append("'");
				sql.append(code);
				sql.append("' and parentid<>codeitemid");
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
		sql.append(" union select codeitemid,codeitemdesc,parentid,childid,a0000 from vorganization where parentid=");
		if(code!=null && code.length()>0)
		{
			sql.append("'");
			sql.append(code);
			sql.append("' and parentid<>codeitemid");
		}else
		{
			if(userView.isSuper_admin()){
				sql.append("codeitemid");
			}else{
				String busi = getBusi_org_dept(this.userView);
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						String[] tmps=busi.split("`");
							String a_code=tmps[0];
							if(a_code.length()>2){
								code=a_code.substring(2);
							}
					}
				}
				sql.append("'");
				sql.append(code);
				sql.append("' and parentid<>codeitemid");
			}
		}
		sql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
		sql.append(" order by a0000");
		 try
		  {
			ArrayList inforlist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				inforlist.add(vo);
			}
			this.getFormHM().put("inforlist",inforlist);
			
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }
		/*ArrayList moveorglist=(ArrayList)this.getFormHM().get("selectedlist");
	    if(moveorglist==null||moveorglist.size()==0)
            return;
		int a0000;
		String codeitemid="";
		String parentid="";
		int  pria0000=1;
		String pricodeitemid="";
		boolean isroot=false;
		try{
		     StringBuffer strsql=new StringBuffer();
		     ContentDAO dao=new ContentDAO(this.getFrameconn());
			 if(moveorglist!=null && moveorglist.size()>0)
			 {
				RecordVo vo=(RecordVo)moveorglist.get(0);
				parentid=vo.getString("parentid");
				codeitemid=vo.getString("codeitemid");
			 }
			 if(codeitemid.equalsIgnoreCase(parentid))
			 {
			 	isroot=true;
			 }
			 else
			 {
			 	isroot=false;
			 }
			for(int i=0;i<moveorglist.size();i++)
			{
				RecordVo vo=(RecordVo)moveorglist.get(i);
				a0000=vo.getInt("a0000");
				codeitemid=vo.getString("codeitemid");
				strsql.delete(0,strsql.length());
				if(isroot)
				{
					strsql.append("select * from organization where codeitemid=parentid and a0000=(select max(a0000) from organization where codeitemid=parentid and a0000<");
					strsql.append(a0000);
					strsql.append(") order by codeitemid desc");
				}
				else
				{
					strsql.append("select * from organization where codeitemid<>parentid and parentid='");
					strsql.append(parentid);
					strsql.append("' and a0000=(select max(a0000) from organization where  codeitemid<>parentid  and parentid='");
					strsql.append(parentid);
			     	strsql.append("' and a0000<");
					strsql.append(a0000);
					strsql.append(") order by codeitemid desc");
				}
				//System.out.println(strsql.toString());
				this.frowset=dao.search(strsql.toString());
				if(this.frowset.next())
				{
					pria0000=this.frowset.getInt("a0000");
					pricodeitemid=this.frowset.getString("codeitemid");
					strsql.delete(0,strsql.length());
					strsql.append("update organization set a0000=");
					strsql.append(pria0000);
					strsql.append(" where codeitemid='");
					strsql.append(codeitemid);
					strsql.append("'");
					dao.update(strsql.toString());					
					strsql.delete(0,strsql.length());
					strsql.append("update organization set a0000=");
					strsql.append(a0000);
					strsql.append(" where codeitemid='");
					strsql.append(pricodeitemid);
					strsql.append("'");		
					dao.update(strsql.toString());
				}
			}
			//this.getFormHM().put("selectedlist",moveorglist);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}*/

	}

	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
}
