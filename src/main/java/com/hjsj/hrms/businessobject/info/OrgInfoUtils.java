package com.hjsj.hrms.businessobject.info;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrgInfoUtils {
    private Connection conn;
	public OrgInfoUtils(Connection conn)
	{
	   this.conn=conn;	
	} 
	public boolean delOrgTrans(String codeitemid,String orgid,String delpersonorg)
	{
        boolean isCorrect=false;
        String sql="Select codesetid from organization where codeitemid='"+codeitemid+"'";
		ContentDAO dao=new ContentDAO(conn);
		String vorg="";
		String codesetid="";
		RowSet rs=null;		
		try {
			rs=dao.search(sql);
			if(rs.next())
			{	
				vorg="org";
				codesetid=rs.getString("codesetid");
			}
			if("".equals(vorg))
			{
				sql="Select codesetid from vorganization where codeitemid='"+codeitemid+"'";
				rs=dao.search(sql);
				if(rs.next())
				{	
					vorg="vorg";
					codesetid=rs.getString("codesetid");
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer delsql=new StringBuffer();
		
		
	    delsql.delete(0,delsql.length());	   
	    if("vorg".equalsIgnoreCase(vorg)){
	    	delsql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from vorganization where codeitemid like '");
       		delsql.append(codeitemid);
    		delsql.append("%'");
	    }
	    else if("org".equalsIgnoreCase(vorg)){
            delsql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from organization where codeitemid like '");
       		delsql.append(codeitemid);
    		delsql.append("%'");
    		
    		delsql.append(" union select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from vorganization where codeitemid like '");
       		delsql.append(codeitemid);
    		delsql.append("%'");
	    }
	    try
	    {
	    	rs=dao.search(delsql.toString());
			while(rs.next())
			{
				CodeItem item=new CodeItem();
				item.setCodeid(rs.getString("codesetid"));
				item.setCodename(rs.getString("codeitemdesc"));
			   	item.setPcodeitem(rs.getString("parentid"));
				item.setCcodeitem(rs.getString("childid"));
				item.setCodeitem(rs.getString("codeitemid"));
				item.setCodelevel(String.valueOf(rs.getInt("grade")));
	    		AdminCode.removeCodeItem(item);  
			}         		
			delsql.delete(0,delsql.length());
			if("vorg".equalsIgnoreCase(vorg)){
				delsql.append("delete from vorganization where codeitemid like '");
	       		delsql.append(codeitemid);
	    		delsql.append("%'");
	    		dao.delete(delsql.toString(),new ArrayList());
			}else if("org".equalsIgnoreCase(vorg)){
				delsql.append("delete from organization where codeitemid like '");
	       		delsql.append(codeitemid);
	    		delsql.append("%'");
	    		dao.delete(delsql.toString(),new ArrayList());
	    		delsql.delete(0,delsql.length());
	    		delsql.append("delete from vorganization where codeitemid like '");
	       		delsql.append(codeitemid);
	    		delsql.append("%'");
	    		dao.delete(delsql.toString(),new ArrayList());
			}
			
			checkorg();
			List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
			for(int k=0;k<infoSetList.size();k++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(k);
				delsql.delete(0,delsql.length());
	    		delsql.append("delete from ");
	    		delsql.append(fieldset.getFieldsetid());
	    		delsql.append(" where b0110 like '");
	       		delsql.append(codeitemid);
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
	       		delsql.append(codeitemid);
	    		delsql.append("%'");
	    		dao.delete(delsql.toString(),new ArrayList());
			}
			//if(orgid==null||orgid.length()<=0)
				//delpersonorg="t";
	        if("t".equalsIgnoreCase(delpersonorg))
	        {
	        	          
	        	if("UN".equalsIgnoreCase(codesetid))
	    		{
	            	List dblist=DataDictionary.getDbpreList();                    	
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set b0110='',e0122='',e01a1='' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);
	            		delsql.append("%' or e0122 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%' or b0110 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%'");
	            		dao.update(delsql.toString());
	            	}
	            
	    		}
	    		if("UM".equalsIgnoreCase(codesetid))
	    		{
	            	List dblist=DataDictionary.getDbpreList();
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set e0122='',e01a1='' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);
	            		delsql.append("%' or e0122 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%'");
	            		dao.update(delsql.toString());
	            	}                
	    		}
	    		if("@K".equalsIgnoreCase(codesetid))
	    		{
	            	List dblist=DataDictionary.getDbpreList();
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set e01a1='' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);                    		
	            		delsql.append("%'");
	            		dao.update(delsql.toString());
	            	}                
	    		}
	        
	        }/*else
	        {
	        	String orgcodeitemid="";
	            String orgcodesetid="";
	            String filedname="";
	            if(orgid==null||orgid.length()<=0)
	            	orgid="UN";
	            else
	            {
	            	String codeid[]=orgid.split("`");
	            	orgcodesetid=codeid[0];
	            	if(codeid.length>1)
	            	{
	            		orgcodeitemid=codeid[1];
	            	}                    	
	            } 
	        	if("UN".equalsIgnoreCase(orgcodesetid))
	    		{
	            	List dblist=DataDictionary.getDbpreList();
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set b0110='"+orgcodeitemid+"' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);
	            		delsql.append("%' or e0122 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%' or b0110 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%'");
	            		dao.update(delsql.toString());
	            	}
	            
	    		}
	    		if("UM".equalsIgnoreCase(orgcodesetid))
	    		{
	    			List dblist=DataDictionary.getDbpreList();
	    			String b_value=orgUpdateCodemess(orgcodeitemid,orgcodesetid);
	    			
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set b0110='"+b_value+"', e0122='"+orgcodeitemid+"' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);
	            		delsql.append("%' or e0122 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%' or b0110 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%'");
	            		dao.update(delsql.toString());
	            	}                
	    		}
	    		if("@K".equalsIgnoreCase(orgcodesetid))
	    		{
	    			String e_value=orgUpdateCodemess(orgcodeitemid,orgcodesetid);
	    			String b_value=orgUpdateCodemess(e_value,"UM");
	            	List dblist=DataDictionary.getDbpreList();
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delsql.delete(0,delsql.length());
	            		delsql.append("update ");
	            		delsql.append(dblist.get(k));
	            		delsql.append("a01 set b0110='"+b_value+"', e0122='"+e_value+"',e01a1='"+orgcodeitemid+"' ");
	               		delsql.append(" where e01a1 like '");
	               		delsql.append(codeitemid);
	            		delsql.append("%' or e0122 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%' or b0110 like '");
	            		delsql.append(codeitemid);
	            		delsql.append("%'");                    		
	            		dao.update(delsql.toString());
	            	}                
	    		}
	        }*/
			   String upsql = new DbNameBo(this.conn).getUpdateJZSql(codeitemid);
	    		if(upsql != null && upsql.length()>3) {
                    dao.update(upsql);
                }
	    		
	    		String virAxx = SystemConfig.getPropertyValue("virtualOrgSet");
	    		if(virAxx!=null&&!"".equals(virAxx)&&""!=virAxx){
	    			List dblist=DataDictionary.getDbpreList();
	    			StringBuffer delvirAxxsql = new StringBuffer();
	            	for(int k=0;k<dblist.size();k++)
	            	{
	            		delvirAxxsql.delete(0,delvirAxxsql.length());
	            		delvirAxxsql.append("delete from "+dblist.get(k)+virAxx+" where "+virAxx+"01 like '");
	            		delvirAxxsql.append(codeitemid);
	            		delvirAxxsql.append("%'");
	    	    		dao.delete(delvirAxxsql.toString(),new ArrayList());	 
	            	}               
	    		}
	        isCorrect=true;
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		return isCorrect;
	}
	private void  checkorg()
	{
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(this.conn);
		 try{
			 //消除掉有子节点childid不正确的
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append("organization d");
		     sql.append(" WHERE d.parentid = ");
			 sql.append("organization.codeitemid AND d.parentid <> d.codeitemid and d.codesetid=organization.codesetid)");
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
     		updateParentcode.append("organization.codeitemid  and c.parentid<>c.codeitemid ) and organization.childid <> organization.codeitemid");
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
			ContentDAO dao=new ContentDAO(conn);
			RowSet rs=dao.search(sql.toString());
			if(rs.next())
			{
				parentid=rs.getString("parentid");
				codesetid=rs.getString("codesetid");
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
			  if(i>0) {
                  parentid=(String)map.get("parentid");
              }
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
				  if(i>0) {
                      parentid=(String)map.get("parentid");
                  }
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
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=dao.search(sql.toString());
			if(rs.next())
			{
				map.put("parentid",rs.getString("parentid"));
				codesetid=rs.getString("codesetid");
				parentid=rs.getString("parentid");
				if(parentid==null||parentid.length()<=0) {
                    parentid="";
                }
				codeitemid=rs.getString("codeitemid");
				if(codeitemid==null||codeitemid.length()<=0) {
                    codeitemid="";
                }
				if(parentid.equalsIgnoreCase(codeitemid)) {
                    codesetid="UN";
                }
				map.put("codesetid",codesetid);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
    }
	public CodeItem getCodeItem(String codeitemid,String orgtype)
	{
		CodeItem item=new CodeItem();
		StringBuffer sql=new StringBuffer();   
		String table="organization";
		if(orgtype!=null&& "vorg".equals(orgtype)) {
            table="vorganization";
        }
		sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
		sql.append(" where codeitemid='"+codeitemid+"'");
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				item.setCodeid(rs.getString("codesetid"));
				item.setCodename(rs.getString("codeitemdesc"));
			   	item.setPcodeitem(rs.getString("parentid"));
				item.setCcodeitem(rs.getString("childid"));
				item.setCodeitem(rs.getString("codeitemid"));
				item.setCodelevel(String.valueOf(rs.getInt("grade")));
			}else {
                return null;
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;		
	}
	public CodeItem getCodeItem1(String codeitemid,String orgtype) throws GeneralException
	{
		CodeItem item=new CodeItem();
		StringBuffer sql=new StringBuffer();   
		String table="organization";
		if(orgtype!=null&& "vorg".equals(orgtype)) {
            table="vorganization";
        }
		sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
		sql.append(" where codeitemid='"+codeitemid+"'");
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				item.setCodeid(rs.getString("codesetid"));
				item.setCodename(rs.getString("codeitemdesc"));
			   	item.setPcodeitem(rs.getString("parentid"));
				item.setCcodeitem(rs.getString("childid"));
				item.setCodeitem(rs.getString("codeitemid"));
				item.setCodelevel(String.valueOf(rs.getInt("grade")));
			}else {
                throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许撤销，操作失败！","",""));
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;		
	}
	/**
	 * 更新序号生成器维护的指标
	 * @param b0110  单位编码
	 * @param fieldSetId 单位信息集
	 * @param i9999 单位子集下的编码
	 */
	public void updateSequenceableValue(String b0110, String fieldSetId, String i9999) {
        try {
            IDGenerator idg = new IDGenerator(2, this.conn);
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<String> updateSqlList = new ArrayList<String>();
            RecordVo vo = new RecordVo(fieldSetId);
            if(fieldSetId.toUpperCase().startsWith("B")) {
                vo.setString("b0110",b0110);
            } else if(fieldSetId.toUpperCase().startsWith("K")) {
                vo.setString("e01a1",b0110);
            }
            
            if(!"B01".equalsIgnoreCase(fieldSetId) && !"K01".equalsIgnoreCase(fieldSetId)) {
            	vo.setString("i9999",i9999);
            }
            
            vo = dao.findByPrimaryKey(vo);
            ArrayList<FieldItem> itemList = DataDictionary.getFieldList(fieldSetId, Constant.USED_FIELD_SET);
            ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
            for(FieldItem fi : itemList) {
            	if(fi == null || "0".equals(fi.getUseflag())) {
                    continue;
                }
            	
            	String value = "";
            	if("b0110".equalsIgnoreCase(fi.getItemid()) || !fi.isSequenceable()) {
            		continue;
            	}
            	
            	String prefix_field=fi.getSeqprefix_field();
            	int prefix=fi.getPrefix_field_len();
            	String prefix_value="";
            	if(prefix_field!=null&&prefix_field.trim().length()>0) {
            		prefix_value=vo.getString(prefix_field.toLowerCase());
            	}
            	
            	if(prefix_value==null) {
            		prefix_value="";
            	}
            	
            	if(prefix_value.length()>prefix&&prefix_field!=null&&prefix_field.length()>0) {
            		prefix_value=prefix_value.substring(0,prefix);
            	}
            	
            	String backfix="";
            	if(prefix_value!=null&&prefix_value.length()>0) {
            		backfix="_"+prefix_value;
            	}
            	
            	RecordVo idFactory=new RecordVo("id_factory");
            	idFactory.setString("sequence_name", fi.getFieldsetid().toUpperCase()+"."+fi.getItemid().toUpperCase()+backfix);
            	String sequ_value="";
            	/**如果该序号还没建立，取没有前缀的序号*/
            	if(dao.isExistRecordVo(idFactory)) {
            		sequ_value=idg.getId(fi.getFieldsetid().toUpperCase()+"."+fi.getItemid().toUpperCase()+backfix);
            	} else {
            		sequ_value=idg.getId(fi.getFieldsetid().toUpperCase()+"."+fi.getItemid().toUpperCase());
            	}
            	
            	value=prefix_value+sequ_value;
            	
            	StringBuffer sql = new StringBuffer();
            	sql.append("update ");
            	sql.append(fieldSetId);
            	sql.append(" set " + fi.getItemid() + "=?");
            	sql.append(" where b0110=?");
            	ArrayList<String> paramList = new ArrayList<String>();
            	paramList.add(value);
            	paramList.add(b0110);
            	if(!"b01".equalsIgnoreCase(fieldSetId) && !"K01".equalsIgnoreCase(fieldSetId)) {
            		sql.append(" and i9999=?");
            		paramList.add(i9999);
            	}
            	
            	updateSqlList.add(sql.toString());
            	valueList.add(paramList);
            }
            
            dao.batchUpdate(updateSqlList, valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
