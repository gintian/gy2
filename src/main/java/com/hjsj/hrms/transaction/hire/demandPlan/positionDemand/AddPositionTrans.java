package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:新增职位</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 25, 2007:10:41:19 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class AddPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		String posID=(String)this.getFormHM().get("posID");
		String posName=(String)this.getFormHM().get("posName");
		String orgID=(String)this.getFormHM().get("orgID");
		
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("organization");
			this.frowset=dao.search("select max(a0000),max(grade),max(codeitemid) from organization where  parentid='"+orgID+"'  or codeitemid='"+orgID+"'"); 	
			if(this.frowset.next())
			{
				int a0000=this.frowset.getInt(1)+1;
				int grade=this.frowset.getInt(2);
				String codeitemid=this.frowset.getString(3);
				
				if(codeitemid.length()==orgID.length())
					grade++;
				vo.setInt("grade",grade);
				vo.setInt("a0000",a0000);
				vo.setString("childid",orgID+posID);
				vo.setString("codeitemid",orgID+posID);
				vo.setString("codeitemdesc",posName);
				vo.setString("parentid",orgID);
				vo.setString("codesetid","@K");
				Calendar calendar=Calendar.getInstance();
				calendar.set(Calendar.YEAR, 9999);
				calendar.set(Calendar.MONTH, 11);
				calendar.set(Calendar.DAY_OF_MONTH,31);
				dao.update("update organization set a0000=a0000+1 where a0000>"+a0000);
				dao.update("update vorganization set a0000=a0000+1 where a0000>"+a0000);
				dao.addValueObject(vo); 
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				StringBuffer sql = new StringBuffer("update organization set start_date=");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append("to_date('"+format.format(new Date())+"','yyyy-mm-dd')");
				    sql.append(",end_date=");
				    sql.append("to_date('"+format.format(calendar.getTime())+"','yyyy-mm-dd')");
				}
				else
				{
					sql.append("'"+format.format(new Date())+"'");
				    sql.append(",end_date=");
				    sql.append("'"+format.format(calendar.getTime())+"'");
				}
				sql.append(" where UPPER(codeitemid)='"+(orgID+posID).toUpperCase()+"'");
				dao.update(sql.toString());
				sql.setLength(0);
				StringBuffer str=new StringBuffer();
				str.append("select * from organization where UPPER(codeitemid)='"+orgID.toUpperCase()+"'");
				RowSet rs=dao.search(str.toString());
				while(rs.next())
				{
					String child=rs.getString("childid");
					if(child==null|| "".equals(child)||child.equalsIgnoreCase(orgID))
					{
						sql.append(" update organization set childid='"+(orgID+posID)+"' where codeitemid='"+orgID+"'");
						dao.update(sql.toString());
					}
				}
				this.frowset=dao.search("select codesetid from organization where codeitemid='"+orgID+"'");
				RecordVo vo2=new RecordVo("k01");
				if(this.frowset.next())
				{
					if("UM".equalsIgnoreCase(this.frowset.getString("codesetid")))
						vo2.setString("e0122",orgID);
				}
				vo2.setString("e01a1",orgID+posID);
				vo2.setDate("createtime",Calendar.getInstance().getTime());
				dao.addValueObject(vo2);
			
				CodeItem code=new CodeItem();
				code.setCodeitem(orgID+posID);
				code.setCodeid("@K");
				code.setCcodeitem(orgID+posID);
				code.setCodename(posName);
				code.setPcodeitem(orgID);
				AdminCode.addCodeItem(code);
				
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}

	}

}
