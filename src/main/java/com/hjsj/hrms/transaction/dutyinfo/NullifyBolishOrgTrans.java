package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NullifyBolishOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList bolishlist=(ArrayList)this.getFormHM().get("bolishlist");
		if(bolishlist==null && bolishlist.size()==0)
			return;
		 ArrayList movedpersons = (ArrayList)this.getFormHM().get("movepersons");
		 String tarorglist=(String)this.getFormHM().get("persontoorg");
		StringBuffer strsql=new StringBuffer();
		UserView userView=this.userView;
		ArrayList msgb0110 = new ArrayList();
		ArrayList codelist = new ArrayList();
		if(userView.getVersion()>=50)
		{
			//版本号大于等于50才显示这些功能 xuj 2009-10-31 机构编码不删除，仅把“有效日期止”改成“系统日期前一天”的值
			String end_date = (String)this.getFormHM().get("end_date");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			String date = sdf.format(calendar.getTime());
			end_date = end_date!=null&&end_date.length()>9?end_date:date;
			ArrayList peopleOrgList = new ArrayList();//人员变动前的机构 xuj 2010-4-28
		    strsql.append("update organization set end_date="+Sql_switcher.dateValue(end_date)+" where 1=2");
			for(int i=0;i<bolishlist.size();i++)
			{
				CommonData dataobj=(CommonData)bolishlist.get(i);
				strsql.append(" OR codeitemid like '");
				strsql.append(dataobj.getDataValue().substring(2));
				strsql.append("%'");
				msgb0110.add(dataobj.getDataValue().substring(2));
				codelist.add(dataobj.getDataValue());
				OrganizationView orgview=new OrganizationView();
				orgview.setCodesetid(dataobj.getDataValue().substring(0,2));
				orgview.setCodeitemid(dataobj.getDataValue().substring(2));
				peopleOrgList.add(orgview);
			}
			this.getFormHM().put("peopleOrg", "bolish");
			this.getFormHM().put("peopleOrgList", peopleOrgList);
			this.peopleOrgChange();
			try{
				if(movedpersons.size()>0)
					movePerson(movedpersons,tarorglist);
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				dao.update(strsql.toString());
				//***********如果此撤销机构底下有相应人员，暂未对人员做处理*****************
			}catch(Exception e){
				throw GeneralExceptionHandler.Handle(e);
			}
			//【58784】v77组织机构：组织机构中合并、划转和撤销组织单元，置成无效的组织单元，B01表中modtime没有更新，影响分布同步无法获取最新的数据，详见附件！
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			calendar = Calendar.getInstance();
			date = sdf.format(calendar.getTime());
			Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
			try {
				for (int i = 0; i <bolishlist.size(); i++) {
					CommonData dataobj=(CommonData)bolishlist.get(i);
					String codesetid = dataobj.getDataValue().substring(0,2);
					String codeitemid = dataobj.getDataValue().substring(2);
					String sql = "";
					ArrayList sqlvalue=new ArrayList();
					sql="update K01 set modusername=?,modtime=? where e01a1 like ?";
					sql="update B01 set modusername=?,modtime=? where b0110 like ?";
					sqlvalue.add(this.userView.getUserName());
					 if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
						  sqlvalue.add(timesTamp);
					  }else {
						  sqlvalue.add(date);
					  }
					sqlvalue.add(codeitemid+"%");
					dao.update(sql,sqlvalue);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			this.getFormHM().put("msgb0110",msgb0110);
		}else
		{
		    StringBuffer personsql=new StringBuffer();
		    StringBuffer possql=new StringBuffer();
		    StringBuffer orgsql=new StringBuffer();
		    StringBuffer personwheresql=new StringBuffer();
	    	StringBuffer poswheresql=new StringBuffer();
		    StringBuffer orgwheresql=new StringBuffer();
		    strsql.append("delete from organization where 1=2");
		    try{
		    	movePerson(movedpersons,tarorglist);
			   ContentDAO dao=new ContentDAO(this.getFrameconn());
			   for(int i=0;i<bolishlist.size();i++)
			   {
				  CommonData dataobj=(CommonData)bolishlist.get(i);
				  strsql.append(" OR codeitemid like '");
				  strsql.append(dataobj.getDataValue().substring(2));
				  strsql.append("%'");
				  personwheresql.append(" or b0110 like '");
				  personwheresql.append(dataobj.getDataValue().substring(2));
				  personwheresql.append("%' or e0122 like '");
				  personwheresql.append(dataobj.getDataValue().substring(2));
				  personwheresql.append("%' or e01a1 like '");
				  personwheresql.append(dataobj.getDataValue().substring(2));
				  personwheresql.append("%'");
				  orgwheresql.append(" or b0110 like '");
				  orgwheresql.append(dataobj.getDataValue().substring(2));				
				  orgwheresql.append("%'");
				  poswheresql.append(" or e01a1 like '");
				  poswheresql.append(dataobj.getDataValue().substring(2));				
				  poswheresql.append("%'");
				  msgb0110.add(dataobj.getDataValue().substring(2));
				  codelist.add(dataobj.getDataValue());
			   }
			    dao.delete(strsql.toString(),new ArrayList());			  
				ArrayList dblist=DataDictionary.getDbpreList();
				for(int i=0;i<dblist.size();i++)
				{
					personsql.delete(0,personsql.length());
					personsql.append("delete from ");
					personsql.append(dblist.get(i).toString());
					personsql.append("a01 where 1=2 ");
					personsql.append(personwheresql.toString());
					dao.delete(personsql.toString(),new ArrayList());
				}
				
				//dao.delete(delsql.toString(),new ArrayList());
	    		
	    		List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    		for(int k=0;k<infoSetList.size();k++)
	    		{
	    			FieldSet fieldset=(FieldSet)infoSetList.get(k);
	    			orgsql.delete(0,orgsql.length());
	    			orgsql.append("delete from ");
	    			orgsql.append(fieldset.getFieldsetid());
	    			orgsql.append(" where 1=2 ");
	    			orgsql.append(orgwheresql.toString());
	    	   		dao.delete(orgsql.toString(),new ArrayList());
	    		}
	    		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
	    		for(int k=0;k<infoSetListPos.size();k++)
	    		{
	    			FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
	    			possql.delete(0,possql.length());
	    			possql.append("delete from ");
	    			possql.append(fieldset.getFieldsetid());
	    			possql.append(" where 1=2 ");
	    			possql.append(poswheresql.toString());
	           		dao.delete(possql.toString(),new ArrayList());
	    		}
		   }catch(Exception e)
		   {
			  e.printStackTrace();
			  throw GeneralExceptionHandler.Handle(e);
		   }
		   checkorg();
		   this.getFormHM().put("msgb0110",msgb0110);
		   this.getFormHM().put("codelist", codelist);
		   this.getFormHM().put("isrefresh", "delete");
		}
		if("true".equals(SystemConfig.getPropertyValue("bolishorg_eporg")))
			emptyOrgInfoForPerson(bolishlist);
	}
	/*
	 * 清空未调到其它机构的人员的机构信息
	 * 注意：此方法必须在处理完movepersons的数据后进行
	 */
	private void emptyOrgInfoForPerson(ArrayList bolishlist)
	{
	    String uptTabSQL;
	    StringBuffer sql = new StringBuffer();
	    ArrayList dblist = DataDictionary.getDbpreList();
        ContentDAO dao = new ContentDAO(this.frameconn);
        
	    for(int i=0;i<bolishlist.size();i++)
        {
            CommonData dataobj=(CommonData)bolishlist.get(i);
            String codeitemid = dataobj.getDataValue().substring(2);
            
            for(int j=0; j<dblist.size(); j++)
            {
                
                String pre = (String) dblist.get(j);
                
                uptTabSQL = "UPDATE " + pre + "A01" + " SET ";
                
                sql.delete(0, sql.length());
                sql.append(uptTabSQL);
                sql.append(" E01A1=NULL");
                sql.append(",modtime=");
                sql.append(PubFunc.DoFormatSystemDate(true));
                sql.append(",modusername='");
                sql.append(userView.getUserName());
                sql.append("' WHERE E01A1 LIKE '");
                sql.append(codeitemid);
                sql.append("%'");
                
                try
                {
                    dao.update(sql.toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }                              
                
               /* sql.delete(0, sql.length());
                sql.append(uptTabSQL);
                sql.append(" E0122=NULL,E01A1=NULL");
                sql.append(" WHERE E0122 LIKE '");
                sql.append(codeitemid);
                sql.append("%'");
                
                try
                {
                    dao.update(sql.toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                } 
                
                sql.delete(0, sql.length());
                sql.append(uptTabSQL);
                sql.append(" B0110=NULL,E0122=NULL,E01A1=NULL");
                sql.append(" WHERE B0110 LIKE '");
                sql.append(codeitemid);
                sql.append("%'");
                
                try
                {
                    dao.update(sql.toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                } */
            }
            
        }
	}
	private void  checkorg()
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try{
			 //消除掉有子节点childid不正确的
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
			 sql.append("organization.codeitemid AND d.parentid <> d.codeitemid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid)");
		  
		     dao.update(sql.toString());
		   
		    StringBuffer updateParentcode=new StringBuffer();
     		updateParentcode.delete(0,updateParentcode.length());
     		updateParentcode.append("UPDATE ");
     		updateParentcode.append("organization SET childid =codeitemid  ");
     		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
     		updateParentcode.append("organization c");
     		updateParentcode.append(" WHERE c.codeitemid = ");
     		updateParentcode.append("organization.childid AND organization.childid <> organization.codeitemid)");
		        //System.out.println(sql.toString());
		     dao.update(updateParentcode.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}

	/**
	 * 人员变动前的机构记录到选择的模板
	 * @throws GeneralException
	 */
	private void peopleOrgChange() throws GeneralException{
		try{
			String peopleOrg = (String) this.getFormHM().get("peopleOrg");
			ArrayList peopleOrgList = (ArrayList) this.getFormHM().get(
					"peopleOrgList");
			if (peopleOrg == null || "".equals(peopleOrg)
					|| peopleOrgList == null || peopleOrgList.size() == 0) {
				return;
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String tempid = "";
			if ("combine".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"combine");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("transfer".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"transfer");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("bolish".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"bolish");
				if (tempid == null || "".equals(tempid))
					return;
			}
			StringBuffer sql = new StringBuffer();
			ArrayList dblist = DataDictionary.getDbpreList();
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
			StringBuffer changepre = new StringBuffer();
			StringBuffer change = new StringBuffer();
			for (int i = 0; i < peopleOrgList.size(); i++) {
				OrganizationView orgview = (OrganizationView) peopleOrgList
						.get(i);
				String codesetid = orgview.getCodesetid();
				String codeitemid = orgview.getCodeitemid();
				for (int n = 0; n < dblist.size(); n++) {
					String pre = (String) dblist.get(n);
					sql.setLength(0);
					sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
							+ pre + "A01 where ");
					if ("UN".equalsIgnoreCase(codesetid)) {
						sql.append("b0110 like '" + codeitemid + "%'");
					} else if ("UM".equalsIgnoreCase(codesetid)) {
						sql.append("e0122 like '" + codeitemid + "%'");
					} else if ("@K".equalsIgnoreCase(codesetid)) {
						sql.append("e01a1 ='" + codeitemid + "'");
					}
					this.frowset = dao.search(sql.toString());
					vo.setString("db_type", pre);
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
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private void movePerson(ArrayList movedpersons,String toOrg) throws Exception{
		String org=toOrg.substring(0,2);
		String orgvalue=toOrg.substring(2);
		StringBuffer strsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0;i<movedpersons.size();i++)
		{
			CommonData dataobj = (CommonData)movedpersons.get(i);
			String datavalue=dataobj.getDataValue();
			String dbpre = datavalue.substring(0,3);
			strsql.delete(0,strsql.length());
			strsql.append("update ");
			strsql.append(dbpre);
			strsql.append("A01 set ");
			cat.debug(strsql.toString());
			strsql.append(" modtime=");
			strsql.append(PubFunc.DoFormatSystemDate(true));
			strsql.append(",modusername='");
			strsql.append(userView.getUserName()+"',");
			if("UN".equalsIgnoreCase(org))
			{
				strsql.append("B0110='");
				strsql.append(orgvalue);
				strsql.append("',E0122='',e01a1='' where a0100='");
				strsql.append(datavalue.substring(3));
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
				strsql.append(datavalue.substring(3));
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
				strsql.append(datavalue.substring(3));
				strsql.append("'");	
				dao.update(strsql.toString());
			}				
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
}
