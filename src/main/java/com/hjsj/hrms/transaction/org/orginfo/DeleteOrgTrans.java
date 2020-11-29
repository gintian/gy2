/*
 * Created on 2005-12-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList delorglist=(ArrayList)this.getFormHM().get("selectedlist");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String delpersonorg=(String)hm.get("delpersonorg");
    	if(delorglist==null||delorglist.size()==0)
            return;
    	String orgid=(String)hm.get("orgid");
        StringBuffer delsql=new StringBuffer();
        StringBuffer updateParentcode=new StringBuffer();
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList codelist = new ArrayList();
        try
        {
        	ArrayList peopleOrgList = new ArrayList();//人员变动前的机构 xuj 2010-4-28
        	for(int i=0;i<delorglist.size();i++){
        		RecordVo vo=(RecordVo)delorglist.get(i);
        		OrganizationView orgview=new OrganizationView();
	    		orgview.setCodesetid(vo.getString("codesetid"));
	    		orgview.setCodeitemid(vo.getString("codeitemid"));
	    		peopleOrgList.add(orgview);
        	}
        	this.getFormHM().put("peopleOrg", "delete");
            this.getFormHM().put("peopleOrgList", peopleOrgList);
            this.peopleOrgChange();
        	for(int i=0;i<delorglist.size();i++){
        		RecordVo vo=(RecordVo)delorglist.get(i);
        		codelist.add(vo.getString("codesetid")+vo.getString("codeitemid"));
        	    delsql.delete(0,delsql.length());
        	    String vorg = vo.getString("flag");
        	    if("vorg".equalsIgnoreCase(vorg)){
        	    	delsql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from vorganization where codeitemid like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
        	    }
        	    else if("org".equalsIgnoreCase(vorg)){
	                delsql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where codeitemid like '");
	           		delsql.append(vo.getString("codeitemid"));
	        		delsql.append("%'");
	        		delsql.append(" union select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from vorganization where codeitemid like '");
	           		delsql.append(vo.getString("codeitemid"));
	        		delsql.append("%'");
        	    }
        		this.frowset=dao.search(delsql.toString());
        		while(this.frowset.next())
        		{
        			CodeItem item=new CodeItem();
    				item.setCodeid(this.frowset.getString("codesetid"));
    				item.setCodename(this.frowset.getString("codeitemdesc"));
    			   	item.setPcodeitem(this.frowset.getString("parentid"));
    				item.setCcodeitem(this.frowset.getString("childid"));
    				item.setCodeitem(this.frowset.getString("codeitemid"));
    				item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
            		AdminCode.removeCodeItem(item);
            		//System.out.println(AdminCode.getCodeName(this.frowset.getString("codesetid"),this.frowset.getString("codeitemid")));
            		//System.out.println("dfsfdf" + item);
            		
        		}         		
        		delsql.delete(0,delsql.length());
        		if("vorg".equalsIgnoreCase(vorg)){
        			delsql.append("delete from vorganization where codeitemid like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
        		}else if("org".equalsIgnoreCase(vorg)){
        			delsql.append("delete from organization where codeitemid like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
            		dao.delete(delsql.toString(),new ArrayList());
            		delsql.setLength(0);
            		delsql.append("delete from vorganization where codeitemid like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
        		}
        		// or (parentid like '");
        		//delsql.append(vo.getString("codeitemid"));
        		//delsql.append("' and codeitemid<>parentid)");
        		cat.debug(delsql.toString());
        		dao.delete(delsql.toString(),new ArrayList());
        		
        		
        		
        		checkorg();
        		/*updateParentcode.delete(0,updateParentcode.length());
        		updateParentcode.append("update organization set childid=(select min(codeitemid) from organization where parentid='");
        		updateParentcode.append(this.frowset.getString("parentid"));
        		updateParentcode.append("' and parentid<>codeitemid and codeitemid<>'");
        		updateParentcode.append(vo.getString("codeitemid"));
        		updateParentcode.append("') where codeitemid='");
        		updateParentcode.append(this.frowset.getString("parentid"));
        		updateParentcode.append("'");
        		//System.out.println(updateParentcode.toString());
        		dao.update(updateParentcode.toString());*/
        		
        		
               /*//清除掉没有子节点childid不正确的
        		updateParentcode.delete(0,updateParentcode.length());
        		updateParentcode.append("UPDATE ");
        		updateParentcode.append("organization SET childid =codeitemid  ");
        		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
        		updateParentcode.append("organization c");
        		updateParentcode.append(" WHERE c.parentid = ");
        		updateParentcode.append("organization.childid AND organization.childid <> organization.codeitemid)");
   		        //System.out.println(sql.toString());
   		        dao.update(updateParentcode.toString());*/
        		
        		
        		
        		List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
        		for(int k=0;k<infoSetList.size();k++)
        		{
        			FieldSet fieldset=(FieldSet)infoSetList.get(k);
        			delsql.delete(0,delsql.length());
            		delsql.append("delete from ");
            		delsql.append(fieldset.getFieldsetid());
            		delsql.append(" where b0110 like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
            		dao.delete(delsql.toString(),new ArrayList());
        		}
        		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
        		for(int k=0;k<infoSetListPos.size();k++)
        		{
        			FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
        			delsql.delete(0,delsql.length());
            		delsql.append("delete from ");
            		delsql.append(fieldset.getFieldsetid());
            		delsql.append(" where e01a1 like '");
               		delsql.append(vo.getString("codeitemid"));
            		delsql.append("%'");
            		dao.delete(delsql.toString(),new ArrayList());
        		}
        		/*if(orgid==null||orgid.length()<=0)
        			delpersonorg="t";*/
                if("t".equalsIgnoreCase(delpersonorg))
                {
                	/*List dblist=DataDictionary.getDbpreList();
                	for(int k=0;k<dblist.size();k++)
                	{
                		delsql.delete(0,delsql.length());
                		
                		delsql.append("update ");
                		delsql.append(dblist.get(k));
                		delsql.append("a01 set b0110='',e0122='',e01a1='' ");
                   		delsql.append(" where e01a1 like '");
                   		delsql.append(vo.getString("codeitemid"));
                		delsql.append("%' or e0122 like '");
                		delsql.append(vo.getString("codeitemid"));
                		delsql.append("%' or b0110 like '");
                		delsql.append(vo.getString("codeitemid"));
                		delsql.append("%'");
                		dao.update(delsql.toString());
                	}*/                    
                	if("UN".equalsIgnoreCase(vo.getString("codesetid")))
            		{
                    	List dblist=DataDictionary.getDbpreList();                    	
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set b0110='',e0122='',e01a1='' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or e0122 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or b0110 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%'");
                    		dao.update(delsql.toString());
                    	}
                    
            		}
            		if("UM".equalsIgnoreCase(vo.getString("codesetid")))
            		{
                    	List dblist=DataDictionary.getDbpreList();
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set e0122='',e01a1='' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or e0122 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%'");
                    		dao.update(delsql.toString());
                    	}                
            		}
            		if("@K".equalsIgnoreCase(vo.getString("codesetid")))
            		{
                    	List dblist=DataDictionary.getDbpreList();
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set e01a1='' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));                    		
                    		delsql.append("%'");
                    		dao.update(delsql.toString());
                    	}                
            		}
                
                }/*else
                {
                	String codeitemid="";
                    String codesetid="";
                    String filedname="";
                    if(orgid==null||orgid.length()<=0)
                    	orgid="UN";
                    else
                    {
                    	String codeid[]=orgid.split("`");
                    	codesetid=codeid[0];
                    	if(codeid.length>1)
                    	{
                    		codeitemid=codeid[1];
                    	}                    	
                    } */                   
                    /*if(orgid.toUpperCase().indexOf("UN")!=-1)
                    {
                    	filedname="b0110";                    	
                    }else if(orgid.toUpperCase().indexOf("UM")!=-1)
                    {
                    	filedname="e0122";
                    }else if(orgid.toUpperCase().indexOf("@K")!=-1)
                    {
                    	filedname="e01a1";
                    }else 
                    {
                    	filedname="b0110";
                    }*/
                    		
                	/*if("UN".equalsIgnoreCase(codesetid))
            		{
                    	List dblist=DataDictionary.getDbpreList();
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set b0110='"+codeitemid+"' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or e0122 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or b0110 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%'");
                    		dao.update(delsql.toString());
                    	}
                    
            		}
            		if("UM".equalsIgnoreCase(codesetid))
            		{
            			List dblist=DataDictionary.getDbpreList();
            			String b_value=orgUpdateCodemess(codeitemid,codesetid);
            			
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set b0110='"+b_value+"', e0122='"+codeitemid+"' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or e0122 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or b0110 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%'");
                    		dao.update(delsql.toString());
                    	}                
            		}
            		if("@K".equalsIgnoreCase(codesetid))
            		{
            			String e_value=orgUpdateCodemess(codeitemid,codesetid);
            			String b_value=orgUpdateCodemess(e_value,"UM");
                    	List dblist=DataDictionary.getDbpreList();
                    	for(int k=0;k<dblist.size();k++)
                    	{
                    		delsql.delete(0,delsql.length());
                    		delsql.append("update ");
                    		delsql.append(dblist.get(k));
                    		delsql.append("a01 set b0110='"+b_value+"', e0122='"+e_value+"',e01a1='"+codeitemid+"' ");
                       		delsql.append(" where e01a1 like '");
                       		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or e0122 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%' or b0110 like '");
                    		delsql.append(vo.getString("codeitemid"));
                    		delsql.append("%'");                    		
                    		dao.update(delsql.toString());
                    	}                
            		}
                }*/
                
				 //删除部门或单位时 更新兼职表里 删除部门或单位下的在任情况为不在任
                 String sql = new DbNameBo(frameconn).getUpdateJZSql(vo.getString("codeitemid"));
                 if(sql.length()>3 && sql != null)
                	      dao.update(sql);
        	}
        	//changxy
        	String virtualOrg=SystemConfig.getPropertyValue("virtualOrgSet");
        	if(virtualOrg!=null&&!"".equals(virtualOrg)){//配置文件有设置虚拟机构项
        	    FieldSet virOrgSet = DataDictionary.getFieldSetVo(virtualOrg);
        	    //zxj 20150516 判断是否构库
        	    if(virOrgSet != null && virOrgSet.getUseflag().equals("" + Constant.USED_FIELD_SET)) {
            		List dblist=DataDictionary.getDbpreList();
            		for(int j=0;j<delorglist.size();j++){
                		RecordVo vo=(RecordVo)delorglist.get(j);
        	    		for (int i = 0; i < dblist.size(); i++) {
        	    			String sql="delete "+dblist.get(i)+virtualOrg+" where "+virtualOrg+"01 like '"+vo.getString("codeitemid")+"%' ";
        	    			dao.delete(sql, new ArrayList());
        	    		}
                	}
        	    }
        	}
        	
        }
        
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    this.getFormHM().put("isrefresh","delete");
	    this.getFormHM().put("codesetid","");
	    this.getFormHM().put("codelist",codelist);
	  try
		{
			//DataDictionary.refresh();
			//AdminCode.refreshCodeTable();
			//DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			//dbmodel.reloadTableModel();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);  			
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
			 sql.append("organization.codeitemid AND d.parentid <> d.codeitemid  and d.codesetid=organization.codesetid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append("organization.codeitemid AND c.parentid <> c.codeitemid and c.codesetid=organization.codesetid)");
		     //System.out.println(sql.toString());
		     dao.update(sql.toString());
		     //清除掉没有子节点childid不正确的
		   /*  sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append("organization SET childid =codeitemid  ");
		     sql.append(" WHERE not EXISTS (SELECT * FROM ");
		     sql.append("organization c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append("organization.childid AND organization.childid <> organization.codeitemid)");*/
		     //System.out.println(sql.toString());
//		   清除掉没有子节点childid不正确的
		    
		     StringBuffer updateParentcode=new StringBuffer();
	     		updateParentcode.delete(0,updateParentcode.length());
	     		updateParentcode.append("UPDATE ");
	     		updateParentcode.append("organization SET childid =codeitemid  ");
	     		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
	     		updateParentcode.append("organization c");
	     		updateParentcode.append(" WHERE c.parentid = ");
	     		updateParentcode.append("organization.codeitemid and c.parentid<>c.codeitemid) and organization.childid <> organization.codeitemid");
	           // System.out.println(updateParentcode.toString());
			     dao.update(updateParentcode.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
	public String orgUpdateCodemess(String codeitemid,String codeset)
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select * from organization where codeitemid='"+codeitemid+"'");
		String parentid=""; 
		String codesetid="";
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				parentid=this.frowset.getString("parentid");
				codesetid=this.frowset.getString("codesetid");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if("UM".equals(codeset))
		{  int i=0;
			do{
			  map=getParentSetid(parentid);
			  codesetid=(String)map.get("codesetid");
			  if(i>0)
			   parentid=(String)map.get("parentid");	
			  i++;
			}
			  while(!"UN".equalsIgnoreCase(codesetid));
			   
		}else if("@K".equals(codeset))
		{
			int i=0;
			do{
				  map=getParentSetid(parentid);
				  codesetid=(String)map.get("codesetid");
				  parentid=(String)map.get("parentid");		
				  if(i>0)
					   parentid=(String)map.get("parentid");	
					  i++;
	       	}while(!"UM".equalsIgnoreCase(codesetid));
		} 
		return parentid;
	}
    public HashMap getParentSetid(String codeitemid)
    {
    	HashMap map=new HashMap();
    	StringBuffer sql=new StringBuffer();
		sql.append("select * from organization where codeitemid='"+codeitemid+"'");		
		String parentid="";
		String codesetid="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				map.put("parentid",this.frowset.getString("parentid"));
				codesetid=this.frowset.getString("codesetid");
				parentid=this.frowset.getString("parentid");
				if(parentid==null||parentid.length()<=0)
					parentid="";
				codeitemid=this.frowset.getString("codeitemid");
				if(codeitemid==null||codeitemid.length()<=0)
					codeitemid="";
				if(parentid.equalsIgnoreCase(codeitemid))
					codesetid="UN";
				map.put("codesetid",codesetid);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
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
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						peopleOrg);
				if (tempid == null || "".equals(tempid))
					return;
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
}
