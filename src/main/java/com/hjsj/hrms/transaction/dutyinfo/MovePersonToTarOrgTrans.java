package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Date;

public class MovePersonToTarOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		String[] persons=(String[])this.getFormHM().get("persons");
		String tarorglist=(String)this.getFormHM().get("persontoorg");
		String dbpre=(String)this.getFormHM().get("dbpre");
		String bolishorgname=(String)this.getFormHM().get("bolishorgname");
		StringBuffer strsql=new StringBuffer();
		String org="";
		String orgvalue="";
		if(bolishorgname.equalsIgnoreCase(tarorglist))
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.nosameorg"),"",""));
		if(tarorglist!=null&& tarorglist.trim().length()>0)
		{
			org=tarorglist.substring(0,2);
			orgvalue=tarorglist.substring(2);
		}
		else
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.notarorg"),"",""));
			
		}
		if(persons==null)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.nomoveperson"),"",""));
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String resultperson="(";
			for(int i=0;i<persons.length;i++)
			{
				resultperson +="'" +persons[i] + "',";
			}
			StringBuffer sqlstr=new StringBuffer();
			/*sqlstr.append("select b0110,e0122,e01a1,a0100,a0101 from ");
			sqlstr.append(dbpre);
			sqlstr.append("A01 where a0100 in");
			sqlstr.append(resultperson);
			sqlstr.append("'')");
			this.peopleOrgChange(sqlstr.toString(),dbpre);
			for(int i=0;i<persons.length;i++)
			{
				strsql.delete(0,strsql.length());
				strsql.append("update ");
				strsql.append(dbpre);
				strsql.append("A01 set ");
				cat.debug(strsql.toString());
				if("UN".equalsIgnoreCase(org))
				{
					strsql.append("B0110='");
					strsql.append(orgvalue);
					strsql.append("',E0122='',e01a1='' where a0100='");
					strsql.append(persons[i]);
					strsql.append("'");
					dao.update(strsql.toString());
				}
				else if("UM".equalsIgnoreCase(org))
				{
					strsql.append("E0122='");
					strsql.append(orgvalue);
					strsql.append("',e01a1='',B0110='");
					strsql.append(getStationUN(orgvalue,"UM"));
					strsql.append("' where a0100='");
					strsql.append(persons[i]);
					strsql.append("'");	
					dao.update(strsql.toString());
				}
				else if("@K".equalsIgnoreCase(org))
				{
					String[] pos=getStationPos(orgvalue,"@K");
					strsql.append("E01A1='");
					strsql.append(orgvalue);
					strsql.append("',E0122='");
					strsql.append(pos[0]);
					strsql.append("',b0110='");
					strsql.append(pos[1]);
					strsql.append("' where a0100='");
					strsql.append(persons[i]);
					strsql.append("'");	
					dao.update(strsql.toString());
				}				
			}*/
			sqlstr.setLength(0);
			sqlstr.append("select a0100,a0101 from ");
			sqlstr.append(dbpre);
			sqlstr.append("A01 where a0100 in");
			sqlstr.append(resultperson);
			sqlstr.append("'')");
			this.frowset=dao.search(sqlstr.toString());
			ArrayList personlist=(ArrayList)this.getFormHM().get("movepersons");
			while(this.frowset.next())
			{
				 CommonData dataobj = new CommonData(dbpre+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
				 personlist.add(dataobj);
			}
			this.getFormHM().put("movepersons",personlist);
			this.getFormHM().put("ishavepersonmessage","");
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	private String getStationUN(String code,String pre)
	{
		String codeitemid="";
		try{
			StringBuffer strsql=new StringBuffer();
				ContentDAO db=new ContentDAO(this.getFrameconn());
				while(!"UN".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select parentid,codeitemid,codeitemdesc,codesetid from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						pre=this.frowset.getString("codesetid");
						codeitemid=this.frowset.getString("codeitemid");
						code=this.frowset.getString("parentid");	
					}			
				}				
			}catch (Exception sqle){
				sqle.printStackTrace();
			}
			return codeitemid;
	}
	private String[] getStationPos(String code,String pre)
	{
		//System.out.println("pos" + code + kind);
		String[] posstr=new String[2];
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
				ContentDAO db=new ContentDAO(this.getFrameconn());
				while(!"UN".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select parentid,codeitemid,codeitemdesc,codesetid from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						pre=this.frowset.getString("codesetid");
					    if("UM".equalsIgnoreCase(pre))
						{
							if(isdep==false)
							{
								posstr[0]=this.frowset.getString("codeitemid");
							    isdep=true;
							}
						}else if("UN".equalsIgnoreCase(pre))
						{
							if(isorg==false)
							{
	   						  posstr[1]=this.frowset.getString("codeitemid");
							  isorg=true;
							}
						}
						code=this.frowset.getString("parentid");	
					}			
				}				
			}catch (Exception sqle){
				sqle.printStackTrace();
			}				

		return posstr;
	}

	/**
	 * 人员变动前的机构记录到选择的模板
	 * @throws GeneralException
	 */
	private void peopleOrgChange(String sql,String pre) throws GeneralException{
		try{
			
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String tempid = "";
			tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"bolish");
			if(tempid==null||tempid.length()<1){
				return;
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			int nyear = 0;
			int nmonth = 0;
			nyear = DateUtils.getYear(new Date());
			nmonth = DateUtils.getMonth(new Date());
			RecordVo vo = new RecordVo("tmessage");
			vo.setString("username", "");
			vo.setInt("state", 0);
			vo.setInt("nyear", nyear);
			vo.setInt("nmonth", nmonth);
			vo.setInt("type", 0);
			vo.setInt("flag", 0);
			vo.setInt("sourcetempid", 0);
			vo.setInt("noticetempid", Integer.parseInt(tempid));
			vo.setString("db_type", pre);
			StringBuffer changepre = new StringBuffer();
			StringBuffer change = new StringBuffer();
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						String a0100 = this.frowset.getString("a0100");
						String a0101 = this.frowset.getString("a0101");
						a0101 = a0101 != null ? a0101 : "";
						String b0110 = this.frowset.getString("b0110");
						String e0122 = this.frowset.getString("e0122");
						String e01a1 = this.frowset.getString("e01a1");
						vo.setString("a0100", a0100);
						vo.setString("a0101", a0101);
						changepre.setLength(0);
						change.setLength(0);
						if (b0110 != null && !"".equals(b0110)) {
							changepre.append("B0110=" + b0110 + ",");
							change.append("B0110,");
						}
						if (e0122 != null && !"".equals(e0122)) {
							changepre.append("E0122=" + e0122 + ",");
							change.append("E0122,");
						}
						if (e01a1 != null && !"".equals(e01a1)) {
							changepre.append("E01A1=" + e01a1 + ",");
							change.append("E01A1,");
						}
						if (a0101 != null && !"".equals(a0101)) {
							changepre.append("A0101=" + a0101 + ",");
							change.append("A0101,");
						}
						vo.setString("changepre", changepre.toString());
						vo.setString("change", change.toString());
						/** max id access mssql此字段是自增长类型 */
						if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
							int nid = DbNameBo.getPrimaryKey("tmessage", "id",
									this.frameconn);
							vo.setInt("id", nid);
						}
						dao.addValueObject(vo);
					}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
