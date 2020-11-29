package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ShowBridgeAchivementTaskTrans.java</p>
 * <p>Description>:业绩任务书传递参数</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 22, 2010 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ShowBridgeAchivementTaskTrans extends IBusiness {
	
	private HashMap descmap = new HashMap();
	private HashMap parentmap = new HashMap();
	
	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");			
//		String object_type = (String) hm.get("object_type");
		String hjsoft = (String) hm.get("hjsoft");
		String target_id = (String) hm.get("target_id");
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			String sql = "select codeitemid,parentid,codeitemdesc from organization ";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				descmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				parentmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
			}
			
			if(hjsoft!=null && hjsoft.length()>0)
			{
				if("hjsj".equalsIgnoreCase(hjsoft))
				{
					target_id = (String) hm.get("target_id");
					String obj_type = (String) hm.get("object_type");
					String object_id = (String) hm.get("object_id");
					String nbase = (String) hm.get("nbase");
					String codeitemid="";
					String parentid="";
					String a0101="";
					String B0110="";
					String E0122="";
					String orgLink = "";
					ArrayList orgLinks = new ArrayList();
					
					StringBuffer strSql=new StringBuffer();
					if("1".equals(obj_type))
					{	
						boolean flag = this.isHavaPriv(obj_type, target_id,object_id);								
						if(!flag){
							throw GeneralExceptionHandler.Handle(new GeneralException("没有权限这么做！"));
						}
						strSql.append("select codeitemid,parentid from organization where codeitemid='"+object_id+"'");						 						
					}else{
						boolean flag = this.isHavaPriv(obj_type, target_id,object_id);		
						if(!flag){
							throw GeneralExceptionHandler.Handle(new GeneralException("没有权限这么做！"));
						}
						if(nbase!=null && nbase.length()>0)
							strSql.append("select A0101,B0110,E0122 from "+nbase+"A01 where A0100='"+object_id+"' ");						
					}
					rowSet=dao.search(strSql.toString());
					
					while(rowSet.next())
					{
						if("1".equals(obj_type))
						{						
							codeitemid=rowSet.getString("codeitemid");	
							parentid=rowSet.getString("parentid");
							
							if (codeitemid != null && codeitemid.length() > 0)
								orgLink = getSuperOrgLink(codeitemid, "UM");
							else if (parentid != null && parentid.length() > 0)
								orgLink = getSuperOrgLink(parentid, "UN");							
														
							if(orgLinks.size()==0)
								orgLinks.add(orgLink);
						}else{
							a0101=rowSet.getString("A0101");
							B0110=rowSet.getString("B0110");
							E0122=rowSet.getString("E0122");
														
							if (E0122 != null && E0122.length() > 0)
								orgLink = getSuperOrgLink(E0122, "UM");
							else if (B0110 != null && B0110.length() > 0)
								orgLink = getSuperOrgLink(B0110, "UN");							
							
							orgLink=a0101+"/"+orgLink;
							if(orgLinks.size()==0)
								orgLinks.add(orgLink);
						}
					}
					this.getFormHM().put("orgLinks",orgLinks);
				}
			}
			
			this.frowset=dao.search("select object_type from per_target_list where target_id="+target_id);
			String object_type="";
			if(this.frowset.next())
			{
				object_type=this.frowset.getString("object_type");				
			}
			this.getFormHM().put("obj_type",object_type);
			this.getFormHM().put("target_id", target_id);	
			this.getFormHM().put("hjsoft",hjsoft);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public String getSuperOrgLink(String codeitemid, String codesetid)
	{
		StringBuffer org_str = new StringBuffer("");
		try
		{
			String itemid = codeitemid;
			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			while (true)
			{
				String parentid = (String) this.parentmap.get(itemid);
				if (parentid.equals(itemid))
					break;
				else
				{
					org_str.append("/" + this.descmap.get(parentid));
					itemid = parentid;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return org_str.toString();
	}
	public boolean isHavaPriv(String object_type,String target_id,String object_id){
		boolean flag=false;
		try{
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer sql0=new StringBuffer("select object_id from per_target_mx ");
			if("2".equals(object_type))
				sql0.append(",UsrA01 where per_target_mx.object_Id=UsrA01.a0100   ");
			else 
				sql0.append(",organization where per_target_mx.object_Id=organization.codeitemid   ");
			
			sql0.append(" and  per_target_mx.target_id="+target_id);
			
			// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
			String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("2".equals(object_type))
			{
				StringBuffer buf = new StringBuffer();				
				if (operOrg!=null && operOrg.length() > 3)
				{					 
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
							tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
						else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						    tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
					}
					buf.append(" select usra01.A0100 from usra01 where  ( " + tempSql.substring(3) + " ) ");
					 
				}
				else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				{
					String priStrSql = InfoUtils.getWhereINSql(this.userView,"Usr");
					if(priStrSql.length()>0)
					{
						buf.append("select usra01.A0100 ");
						buf.append(priStrSql);
					}
				}
				if(buf.length()>0)
				{
					sql0.append(" and per_target_mx.object_Id in ("+buf.toString()+") ");
				}

			}
			else
			{
				 if (operOrg!=null && operOrg.length() > 3)
				 {
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrg.split("`");
					for (int i = 0; i < temp.length; i++)
					{
					    tempSql.append(" or per_target_mx.object_Id like '" + temp[i].substring(2) + "%'");
					}
					sql0.append(" and ( " + tempSql.substring(3) + " ) ");
				 }
				 else if((!this.userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
				 {
					String codeid=userView.getManagePrivCode();
					String codevalue=userView.getManagePrivCodeValue();
					String a_code=codeid+codevalue;
					
					if(a_code.trim().length()==0)
					{
						sql0.append(" and 1=2 ");
					}
					else if(!("UN".equals(a_code)))
					{
							sql0.append(" and per_target_mx.object_Id like '"+codevalue+"%' "); 
							
					}
				 }				
			}			
			
			sql0.append(" order by per_target_mx.kh_cyle");
			if("2".equals(object_type))
				sql0.append(",Usra01.a0000");
			else
				sql0.append(",organization.a0000");
			RowSet rowSet=dao.search(sql0.toString());
			while(rowSet.next()){
				String _object_id=rowSet.getString("object_id");
				if(object_id.equals(_object_id)){
					flag=true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}