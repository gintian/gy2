package com.hjsj.hrms.module.questionnaire.recovery.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 创建组织机构和角色树
 *
 */
public class SearchOrgorroleTreeTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		ArrayList list = null;		
		try{		
			String codesetid = (String)this.getFormHM().get("codesetid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList  value = (ArrayList) this.getFormHM().get("value");
			if("UN".equals(codesetid)){//组织机构树
				String codeid = this.getFormHM().get("node").toString();
				String codesetidselect="";
				if(!"root".equals(codeid)){
					String sql = "select codesetid from organization where codeitemid='"+codeid+"'";
					this.frowset = dao.search(sql);

					if(this.frowset.next()){
						codesetidselect = this.frowset.getString("codesetid");
					}
					if(codesetidselect.equals(codesetid)){
						codesetidselect = codesetid;
					}
					else if("UM".equals(codesetidselect)){
						codesetidselect = "UM";
					}
				}else{
					codesetidselect = "UN";
				}			
				if(AdminCode.getCodeItemList(codesetid).size()<1)
					return;			
				boolean autoCheck = Boolean.parseBoolean(this.getFormHM().get("autoCheck").toString());
				if(codesetid.indexOf("UN")!=-1||codesetid.indexOf("UM")!=-1){
					list = searchOrgCodeData(codesetidselect,codeid,dao,autoCheck,value);
					}
				String str = JSON.toString(list);
				this.getFormHM().clear();
				this.getFormHM().put("children",list);
			}
			else if("ROLE".equals(codesetid)){//角色树
				String codeid = this.getFormHM().get("node").toString();
				ArrayList rolelist = searchroleTree(codeid,dao,value);
				this.getFormHM().clear();
				this.getFormHM().put("children",rolelist);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 生成角色树
	 * @param codeid
	 * @param dao
	 * @return
	 */
	 private ArrayList searchroleTree(String codeid,ContentDAO dao,ArrayList valuelist) {
		ArrayList value = new ArrayList();
		if("root".equals(codeid)){
			//查询权限范围下的角色数量
			int count = queryrolenumber(dao);
			HashMap treeItem = new HashMap();
			treeItem.put("id", "-1");
			treeItem.put("flag", "HEAD");
			treeItem.put("text", "角色范围");
			if(count==0){
				treeItem.put("leaf", Boolean.TRUE);
			}
				treeItem.put("checked", Boolean.FALSE);
			value.add(treeItem);
		}
		if("-1".equals(codeid)){
			value = queryrolestaff(dao,valuelist);
		}
//		if(codeid.startsWith("R")){
//			value = querystaff(codeid,dao);
//		}
		return value;
	}
	 /**
	  * 查询角色下的人员信息
	  * @param codeid
	  * @param dao
	  * @return
	  */
	 /*private ArrayList querystaff(String codeid, ContentDAO dao) {
		ArrayList value = new ArrayList();
		RowSet rst = null;
		RowSet rst1 = null;
		RowSet rst2 = null;
		try {
			codeid = codeid.substring(1,codeid.length());
			String sql = "select staff_id,status from t_sys_staff_in_role where role_id='"+codeid+"'";
			rst = dao.search(sql);
			while(rst.next()){
				HashMap treeItem = new HashMap();
				int status = rst.getInt("status");
				String staffid = rst.getString("staff_id");
				if(status==0){//用户查询关联的人员
					String linksql= "select a0100,fullname from OperUser where UserName='"+staffid+"'"; 
					rst1 = dao.search(linksql);
					while(rst1.next()){
						String A0100 = rst1.getString("a0100");
						String FullName = rst1.getString("fullname");
						if(A0100!=null){
							treeItem.put("id", A0100);
							treeItem.put("flag", "PEPOLE");
							treeItem.put("text", FullName);
							treeItem.put("leaf", Boolean.TRUE);
							treeItem.put("checked", Boolean.FALSE);
							value.add(treeItem);
						}
					}
				}
				if(status==1){//UsrA01中的A0100
					String staffids = staffid.substring(3,staffid.length());
					String querysql = "select a0101 from UsrA01 where A0100='"+staffids+"'";
					rst2= dao.search(querysql);
					if(rst2.next()){
						String a0101= rst2.getString("a0101");
						treeItem.put("id", staffids);
						treeItem.put("flag", "PEPOLE");
						treeItem.put("text", a0101);
						treeItem.put("leaf", Boolean.TRUE);
						treeItem.put("checked", Boolean.FALSE);
						value.add(treeItem);
					}
				}
				if(status==2){//组织机构
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}*/
	/**
	  * 创建查询角色信息的sql
	  * @param flag
	  * @return
	  */
    private String createSql(String flag){
    	StringBuffer strsql=new StringBuffer();
    	if(userView.isBThreeUser()){//有三员角色的用户
	    	/**只能查询到自己拥有的角色列表*/
	    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
	    	strsql.append(" where role_id in (");
	    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
	    	if(userView.getStatus()==4)// 1-UsrA01中的A0100
	    	{
	    		strsql.append(userView.getDbname());
	    		strsql.append(this.userView.getA0100());
	    	}
	    	if(userView.getStatus()==0){//status 0-用户
	    		strsql.append(this.userView.getUserId());
	    	}
	        strsql.append("' and status=");
            if(userView.getStatus()==0)
    	        strsql.append("0");            	
            else
  	            strsql.append("1");//4
	        strsql.append(") and role_property not in(0,15,16)");
    }else{
    	if(userView.isSuper_admin()){
    		 if("su".equals(userView.getUserId()))
		    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role ");
    		 else
    			 strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where role_property not in(0,15,16) ");
    	 } else
		    {
		    	/**只能查询到自己拥有的角色列表*/
		    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
		    	strsql.append(" where role_id in (");
		    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
		    	if(userView.getStatus()==4)// 1-UsrA01中的A0100
		    	{
		    		strsql.append(userView.getDbname());
		    		strsql.append(this.userView.getA0100());
		    	}
		    	if(userView.getStatus()==0){//status 0-用户
		    		strsql.append(this.userView.getUserId());
		    	}
		        strsql.append("' and status=");
	            if(userView.getStatus()==0)
	    	        strsql.append("0");            	
	            else
	  	            strsql.append("1");//4
		        strsql.append(") and role_property not in(0,15,16)");
		    }
    }
    	if("0".equals(flag)){
    		strsql.append(" order by norder");
    	}
    	return strsql.toString();
    }
    
    /**
     * 获得角色节点以及其子节点的个数
     * @param dao
     * @return
     */
	private ArrayList queryrolestaff(ContentDAO dao,ArrayList valuelist) {
		ArrayList value = new ArrayList();
		String strsql="";
		RowSet rs = null;
		String flag = "0";
		try {
			strsql = createSql(flag);
			rs= dao.search(strsql);
		while(rs.next()){
			HashMap treeItem = new HashMap();
			String roleid = rs.getString("role_id");
			String rolename = rs.getString("role_name");
			//通过roleid获得角色下面的人员个数
			//int count = selectstaffnumber(roleid,dao);
			treeItem.put("id", "R"+roleid);
			treeItem.put("flag", "ROLE");
			treeItem.put("text", rolename);
			//if(count==0){
			treeItem.put("leaf", Boolean.TRUE);
			//}
			treeItem.put("checked", Boolean.FALSE);	
			for(int i=0;i<valuelist.size();i++){
				String checkid = (String) valuelist.get(i);
				if(checkid.equals("R"+roleid)){
					treeItem.put("checked", Boolean.TRUE);
				}
			}
				value.add(treeItem);
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 通过roleid获得角色下面的人员个数
	 * @param roleid
	 * @return
	 */
	/*private int selectstaffnumber(String roleid,ContentDAO dao) {
		int count = 0;
		RowSet rst = null;
		RowSet rst1 = null;
		try {
			String sql = "select staff_id,status from t_sys_staff_in_role where role_id='"+roleid+"'";
			rst = dao.search(sql);
			while(rst.next()){
				int status = rst.getInt("status");
				String staffid = rst.getString("staff_id");
				if(status==0){//用户查询关联的人员
					String linksql= "select a0100,fullname from OperUser where UserName='"+staffid+"'"; 
					rst1 = dao.search(linksql);
					while(rst1.next()){
						String A0100 = rst1.getString("a0100");
						String FullName = rst1.getString("fullname");
						if(A0100!=null){
						count = count+1;
					}
						}
				}
				if(status==1){//UsrA01中的A0100
					int index = staffid.indexOf("Usr");
					staffid = staffid.substring(index,staffid.length());
					count = count+1;
				}
				if(status==2){//组织机构
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return count;
	}*/
	/**
	 * 获得登录人权限下的角色个数
	 * @param dao
	 * @return
	 */
	private int queryrolenumber(ContentDAO dao) {
		String strsql="";
		int child = 0;
		String flag="1";
		try {//status 0-用户   1-UsrA01中的A0100  2-organization中的codeitemid
			strsql = createSql(flag);
		String sql = "select count(1) as child from ("+strsql.toString()+")t";
		this.frowset = dao.search(sql);
		if(this.frowset.next()){
			child = this.frowset.getInt("child");
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return child;
	}

    /**
     * 生成组织机构树
     * @param codesetid
     * @param code
     * @param dao
     * @param autoCheck
     * @return
     */
	private ArrayList searchOrgCodeData(String codesetid,String code,ContentDAO dao,boolean autoCheck,ArrayList valuelist){
		 ArrayList value = new ArrayList();
	    	try{
	    		StringBuilder sql = new StringBuilder();
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		if("UN".equals(codesetid)){
		    		if("root".equals(code)){
		    			sql.append("select a0000,codeitemid,codeitemdesc,codesetid,(select count(1) from organization where parentid=Org.codeitemid and parentid<>codeitemid ");	
		    			sql.append(") child from organization Org where 1=1 ");
		    			sql.append(" and parentid=codeitemid ");
		    			sql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date ");
		    		}else{
		    			sql.append("select a0000,codeitemdesc,(select count(1) from organization a where parentid=organization.codeitemid and a.parentid<>a.codeitemid ) child,codesetid,'' staffid,codeitemid,'' nbase,'' sex from organization ");
		    			sql.append("where codeitemid like '"+code+"%' and parentid='"+code+"' ");
		    			sql.append("and codesetid<>'@K' and codeitemid<>parentid ");
		    			sql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date ");
		    			sql.append("union all ");
		    			sql.append("select a0000,A0101 as codeitemdesc,0 child,'@K' codesetid,A0100 as staffid,'' codeitemid,'Usr' nbase,A0107 as sex from UsrA01 ");
		    			sql.append("where B0110='"+code+"' and E01A1='' ");
		    		}
	    		}else if("UM".equals(codesetid)){
	    				sql.append(" select a0000,codeitemdesc,(select count(1) from organization a where parentid=organization.codeitemid and a.parentid<>a.codeitemid and a.codesetid<>'@K') child,codesetid,'' staffid,codeitemid,'' nbase,'' sex  from organization ");
		    			sql.append(" where codeitemid like '"+code+"%' and parentid='"+code+"' and codesetid<>'@K' ");
		    			sql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date ");
						sql.append(" union all ");
						sql.append(" select u.a0000,A0101 as codeitemdesc,0 child,'@K' codesetid,A0100 as staffid,'' codeitemid,'Usr' nbase,A0107 as sex ");
						sql.append(" from UsrA01 u left join organization o on u.E01A1=o.codeitemid ");
						sql.append(" where E0122='"+code+"'");
	    		}
	    		
	    		sql.append(" order by a0000,codeitemid");
	    		frowset = dao.search(sql.toString());
	    		while(frowset.next()){
	    			HashMap treeItem = new HashMap();
	    			treeItem.put("checked", Boolean.FALSE);
	    			if("UN".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/components/personPicker/image/unit.png");
	    			}else if("UM".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/components/personPicker/image/dept.png");
	    			}else if("@K".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				if("1".equals(frowset.getString("sex"))){
	    					treeItem.put("icon", "/components/personPicker/image/male.png");
	    				}else{
	    					treeItem.put("icon", "/components/personPicker/image/female.png");
	    				}
	    			}
	    			if("@K".equals(frowset.getString("codesetid"))){
	    				treeItem.put("id", frowset.getString("staffid"));
	    				treeItem.put("nbase", frowset.getString("nbase"));
	    				treeItem.put("codesetid", "PE");
	    				for(int i=0;i<valuelist.size();i++){
		    				String checkid = (String) valuelist.get(i);
		    				if(checkid.equals(frowset.getString("staffid"))){
		    					treeItem.put("checked", Boolean.TRUE);
		    				}
	    				}
	    			}
	    			if("UN".equals(frowset.getString("codesetid"))){
	    				treeItem.put("id", frowset.getString("codeitemid"));
	    				treeItem.put("nbase", "");
	    				treeItem.put("codesetid", "UN");
	    				for(int i=0;i<valuelist.size();i++){
		    				String checkid = (String) valuelist.get(i);
		    				if(checkid.equals(frowset.getString("codeitemid"))){
		    					treeItem.put("checked", Boolean.TRUE);
		    				}
	    				}
//	    				treeItem.put("expanded", Boolean.TRUE);
	    			}
	    			if("UM".equals(frowset.getString("codesetid"))){
	    				treeItem.put("id", frowset.getString("codeitemid"));
	    				treeItem.put("nbase", "");
	    				treeItem.put("codesetid", "UM");
	    				for(int i=0;i<valuelist.size();i++){
		    				String checkid = (String) valuelist.get(i);
		    				if(checkid.equals(frowset.getString("codeitemid"))){
		    					treeItem.put("checked", Boolean.TRUE);
//		    					treeItem.put("expanded", Boolean.TRUE);
		    				}
	    				}
	    			}
	    			String codeitemdesc = frowset.getString("codeitemdesc");
	    			if(codeitemdesc==null){
	    				codeitemdesc="";
	    			}
	    			treeItem.put("text", codeitemdesc);
	    			if(frowset.getInt("child")==0)
	    				treeItem.put("leaf", Boolean.TRUE);
	    			value.add(treeItem);
	    		}
	    	}catch(Exception e){	    		
	    	}
	    	return value;
	    }
}
