package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 0202001023
 * <p>Title:CreateSchoolPositionTrans.java</p>
 * <p>Description>:CreateSchoolPositionTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 14, 2010  4:07:45 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class CreateSchoolPositionTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String schoolPositionOrg=(String)this.getFormHM().get("schoolPositionOrg");
			String schoolPositionOrgDesc=(String)this.getFormHM().get("schoolPositionOrgDesc");
			String schoolPositionDesc=(String)this.getFormHM().get("schoolPositionDesc");
			String schoolPositionId=(String)this.getFormHM().get("schoolPositionId");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("organization");
			vo.setString("codesetid", "@K");
			vo.setString("codeitemid", schoolPositionOrg+schoolPositionId);
			if(dao.isExistRecordVo(vo))
			{
				vo.setString("codeitemdesc", schoolPositionDesc);
				dao.updateValueObject(vo);
			}else{
		    	this.frowset=dao.search("select max(a0000),max(grade),max(codeitemid) from organization where  parentid='"+schoolPositionOrg+"'  or codeitemid='"+schoolPositionOrg+"'"); 	
		    	if(this.frowset.next())
		    	{
		    		int a0000=this.frowset.getInt(1)+1;
					int grade=this.frowset.getInt(2);
					String codeitemid=this.frowset.getString(3);
					
					if(codeitemid.length()==schoolPositionOrg.length())
						grade++;
					vo.setInt("grade",grade);
					vo.setInt("a0000",a0000);
					vo.setString("childid",schoolPositionOrg+schoolPositionId);
					vo.setString("codeitemid",schoolPositionOrg+schoolPositionId);
					vo.setString("codeitemdesc",schoolPositionDesc);
					vo.setString("parentid",schoolPositionOrg);
					vo.setString("codesetid","@K");
					dao.addValueObject(vo); 
					dao.update("update organization set a0000=a0000+1 where a0000>"+grade);
					Calendar calendar=Calendar.getInstance();
					calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)-1);
					calendar.set(Calendar.MONTH, 11);
					calendar.set(Calendar.DAY_OF_MONTH,31);
					Calendar calendar2=Calendar.getInstance();
					calendar2.set(Calendar.YEAR, calendar2.get(Calendar.YEAR)-2);
					calendar2.set(Calendar.MONTH, 11);
					calendar2.set(Calendar.DAY_OF_MONTH,31);
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					StringBuffer sql = new StringBuffer("update organization set start_date=");
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						sql.append("to_date('"+format.format(calendar2.getTime())+"','yyyy-mm-dd')");
					    sql.append(",end_date=");
					    sql.append("to_date('"+format.format(calendar.getTime())+"','yyyy-mm-dd')");
					}
					else
					{
						sql.append("'"+format.format(calendar2.getTime())+"'");
					    sql.append(",end_date=");
					    sql.append("'"+format.format(calendar.getTime())+"'");
					}
					sql.append(" where UPPER(codeitemid)='"+(schoolPositionOrg+schoolPositionId).toUpperCase()+"'");
					dao.update(sql.toString());
					sql.setLength(0);
					StringBuffer str=new StringBuffer();
					str.append("select * from organization where UPPER(codeitemid)='"+schoolPositionOrg.toUpperCase()+"'");
					RowSet rs=dao.search(str.toString());
					while(rs.next())
					{
						String child=rs.getString("childid");
						if(child==null|| "".equals(child)||child.equalsIgnoreCase(schoolPositionOrg))
						{
							sql.append(" update organization set childid='"+(schoolPositionOrg+schoolPositionId)+"' where codeitemid='"+schoolPositionOrg+"'");
							dao.update(sql.toString());
						}
					}
					this.frowset=dao.search("select codesetid from organization where codeitemid='"+schoolPositionOrg+"'");
					RecordVo vo2=new RecordVo("k01");
					if(this.frowset.next())
					{
						if("UM".equalsIgnoreCase(this.frowset.getString("codesetid")))
							vo2.setString("e0122",schoolPositionOrg);
					}
					vo2.setString("e01a1",schoolPositionOrg+schoolPositionId);
					vo2.setDate("createtime",Calendar.getInstance().getTime());
				    if(!dao.isExistRecordVo(vo2))//zzk
					dao.addValueObject(vo2);
				
					CodeItem code=new CodeItem();
					code.setCodeitem(schoolPositionOrg+schoolPositionId);
					code.setCodeid("@K");
					code.setCcodeitem(schoolPositionOrg+schoolPositionId);
					code.setCodename(schoolPositionDesc);
					code.setPcodeitem(schoolPositionOrg);
					AdminCode.addCodeItem(code);
		    	}
			}
			this.getFormHM().put("schoolPositionOrg",schoolPositionOrg);
			this.getFormHM().put("schoolPositionOrgDesc", schoolPositionOrgDesc);
			this.getFormHM().put("schoolPositionDesc", schoolPositionDesc);
			this.getFormHM().put("schoolPositionId", schoolPositionId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
