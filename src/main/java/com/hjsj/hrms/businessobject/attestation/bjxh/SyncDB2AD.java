package com.hjsj.hrms.businessobject.attestation.bjxh;

import com.hjsj.hrms.businessobject.attestation.ldap.execution.LdapExecution;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class SyncDB2AD{

	private LdapExecution ldapExe = new LdapExecution();
	
	private String defPwd = "123456";
	
	public SyncDB2AD(){
		this.defPwd = SystemConfig.getPropertyValue("defUserPwd");
	}
	/**
	 * 获取变更人员信息
	 */
	private ArrayList scanSyncUser(){
		ArrayList list = new ArrayList();
		String sql = "SELECT unique_id,B0110_0,E0122_0,E01A1_0,A0101,username,userpassword,flag FROM t_hr_view WHERE flag<>0";
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				Map map = new HashMap();
				String unique_id = rs.getString("unique_id");
				String b0110 = rs.getString("b0110_0");
				String E0122_0 = rs.getString("e0122_0");
				String E01A1_0 = rs.getString("e01a1_0");
				String A0101 = rs.getString("a0101");
				String username = rs.getString("username");
//				String password = rs.getString("userpassword");
				String flag = rs.getString("flag");
				
				map.put("unique_id", unique_id==null?"":unique_id);
				map.put("b0110_0", b0110==null?"":b0110);
				map.put("e0122_0", E0122_0==null?"":E0122_0);
				map.put("e01a1_0", E01A1_0==null?"":E01A1_0);
				map.put("a0101", A0101==null?"":A0101);
				map.put("username", username==null?"":username);
//				map.put("password", password);
				map.put("flag", flag);
				
				list.add(map);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		return list;
	}

	private ArrayList scanSyncOrg(boolean isDel){
		ArrayList list = new ArrayList();
		String sql = "";
		if(isDel){
			sql = "SELECT unique_id,codesetid,codeitemdesc,b0110_0,parentid FROM t_org_view WHERE flag=3 order by b0110_0 desc";
		}else{
			sql = "SELECT unique_id,codesetid,codeitemdesc,b0110_0,parentid FROM t_org_view WHERE flag=2 or flag = 1 order by b0110_0";
		}
		RowSet rs = null;
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				Map map = new HashMap();
				String unique_id = rs.getString("unique_id");
				String codeitemdesc = rs.getString("codeitemdesc");
				String b0110_0 = rs.getString("b0110_0");
				String parentid = rs.getString("parentid");
//				String flag = rs.getString("flag");
				map.put("unique_id", unique_id==null?"":unique_id);
				map.put("codeitemdesc", codeitemdesc==null?"":codeitemdesc);
				map.put("b0110_0", b0110_0==null?"":b0110_0);
				map.put("parentid", parentid==null?"":parentid);
				list.add(map);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		return list;
	}

	public void execute() {
		ArrayList orgList = scanSyncOrg(false);//获取编辑人员信息
		ArrayList orgSulist = new ArrayList();
		ArrayList usrSulist = new ArrayList();
		for(Iterator it = orgList.iterator();it.hasNext();){
			Map map = (Map)it.next();
			String unique_id =  (String)map.get("unique_id");
			if(modUnit(map)){//如果编辑成功 把 unique_id 保存到（List）orgSulist里 用于数据处理
				orgSulist.add(unique_id);
			}
		}
		
		ArrayList usrList = scanSyncUser();//获取编辑人员信息
		for(Iterator it = usrList.iterator();it.hasNext();){
			Map map = (Map)it.next();
			String unique_id =  (String)map.get("unique_id");
			String flag =  (String)map.get("flag");
			if("3".equals(flag)){
				if(delUser(map)){//如果禁用成功 把 unique_id 保存到（List）usrSulist里 用于数据处理
					usrSulist.add(unique_id);
				}
			}else{
				if(modUser(map)){//如果编辑成功 把 unique_id 保存到（List）usrSulist里 用于数据处理
					usrSulist.add(unique_id);
				}
			}
		}
		
		ArrayList orgDelList = scanSyncOrg(true);//获取删除机构的信息
		for(Iterator it = orgDelList.iterator();it.hasNext();){
			Map map = (Map)it.next();
			String unique_id =  (String)map.get("unique_id");
			if(delUnit(map)){//如果删除成功 把 unique_id 保存到List里 用于数据处理
				orgSulist.add(unique_id);
			}
		}
		try {
			ldapExe.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		procOrgReturn(orgSulist);
		procUserReturn(usrSulist);
	}
	
	/**
	 * 新增单位部门
	 * @param map
	 * @return
	 */
	private boolean addUnit(Map map){
		try {
			String codeitemdesc =(String)map.get("codeitemdesc");
			String b0110_0 =  (String)map.get("b0110_0");
			String parentid = (String)map.get("parentid");
			
			BasicAttributes attrs = new BasicAttributes();
			
			BasicAttribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("top");
			objectClass.add("organizationalUnit");
			attrs.put(objectClass);
			
			attrs.put("postalCode", b0110_0);//唯一值
			String name = "";
			if("".equals(parentid) || parentid.equals(b0110_0)){//判断是否是 顶级机构
				name = "ou=" + codeitemdesc;
			}else{
				name = "ou=" + codeitemdesc + "," + getDN("postalCode=" + parentid);
			}
			ldapExe.add(name, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 修改机构信息
	 * @param map
	 * @return
	 */
	private boolean modUnit(Map map){
		String b0110_0 =  (String)map.get("b0110_0");
		String parentid = (String)map.get("parentid");
		String codeitemdesc =(String)map.get("codeitemdesc");
		String name = getDN("postalCode=" + b0110_0);
		if("".equals(name)){
			return addUnit(map);
		}else{
			try {
				String newName = "";
				if("".equals(parentid) || parentid.equals(b0110_0)){//判断是否是 顶级机构
					newName = "OU=" + codeitemdesc;
				}else{
					newName = "OU=" + codeitemdesc + "," + getDN("postalCode=" + parentid);
				}
				if(!name.equalsIgnoreCase(newName)){
					ldapExe.rename(name,newName);
				}
			} catch (NamingException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 删除机构
	 * @param map
	 * @return
	 */
	private boolean delUnit(Map map){
		try {
			String b0110_0 =  (String)map.get("b0110_0");
			String name = getDN("postalCode=" + b0110_0);
			if(name == null || "".equals(name)){//判断是否存在
				return true;
			}
			if(ldapExe.getName(name, "cn=*").size() == 0){
				ldapExe.del(name);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 新增人员信息
	 * @param map
	 * @return
	 */
	private boolean addUser(Map map){
		try {
			String b0110_0 = (String)map.get("b0110_0");
			String e0122_0 = (String)map.get("e0122_0");
			String e01a1_0 = (String)map.get("e01a1_0");
			String A0101 = (String)map.get("a0101");
			if("".equals(e0122_0)){
				System.out.println("LDAP同步出错:" + A0101 + "人员信息没有部门信息。");
				return false;
			}

			String username = (String)map.get("username");
			if("".equals(username)){
				System.out.println("LDAP同步出错:" + A0101 + "人员信息没有登录用户名。");
				return false;
			}
			BasicAttributes attrs = new BasicAttributes();
			BasicAttribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("top");
			objectClass.add("person");
			objectClass.add("organizationalPerson");
			objectClass.add("user");
			attrs.put(objectClass);
			String B0110 =  AdminCode.getCodeName("UN", b0110_0);
			String E0122 =  AdminCode.getCodeName("UM", e0122_0);
			String E01A1 =  AdminCode.getCodeName("@K", e01a1_0);
			attrs.put("department", E0122);
			attrs.put("company", B0110);
			attrs.put("department", E0122);
			if(E01A1 != null && !"".equals(E01A1)){
				attrs.put("title", E01A1);
			}
			if(A0101 != null && !"".equals(A0101)){
				attrs.put("sn", A0101);
				attrs.put("name", A0101);
				attrs.put("givenName", A0101);
				attrs.put("displayName", A0101);
			}
			attrs.put("userPrincipalName", username);
//			userAccountControl = "512"// 启用：512，禁用：514，// 密码永不过期：66048
			String DN = getDN("postalCode=" + e0122_0);
			if(DN == null || "".equals(DN)){
				System.out.println("LDAP同步出错:" + A0101 + "人员信息没有部门信息。");
				return false;
			}
			String name = "cn=" + username+ "," + DN;
			ldapExe.add(name, attrs);
			if(this.defPwd != null && !"".equals(this.defPwd)){
				attrs.put("unicodePwd", this.defPwd);
				ldapExe.modPassword(name, this.defPwd);
			}
			ldapExe.enable(name);//启用用户
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("新增密码失败！");
			return false;
		}
		return true;
	}

	/**
	 * 修改人员信息
	 * @param map
	 * @return
	 */
	private boolean modUser(Map map){
		try {
			String B0110_0 = (String)map.get("b0110_0");
			String E0122_0 = (String)map.get("e0122_0");
			String E01A1_0 = (String)map.get("e01a1_0");
			String A0101 = (String)map.get("a0101");
			if("".equals(E0122_0) || "".equals(B0110_0) || "".equals(B0110_0) || "".equals(B0110_0)){
				System.out.println("LDAP同步出错:" + A0101 + "人员信息没有部门信息。");
				return false;
			}

			String username = (String)map.get("username");
			BasicAttributes attrs = new BasicAttributes();
			String B0110 =  AdminCode.getCodeName("UN", B0110_0);
			String E0122 =  AdminCode.getCodeName("UM", E0122_0);
			String E01A1 =  AdminCode.getCodeName("@K", E01A1_0);
			
			attrs.put("company", B0110);
			attrs.put("department", E0122);
			if(E01A1 != null && !"".equals(E01A1)){
				attrs.put("title", E01A1);
			}
			if(A0101 != null && !"".equals(A0101)){
				attrs.put("sn", A0101);
				attrs.put("givenName", A0101);
				attrs.put("displayName", A0101);
			}
			String name = getDN("userPrincipalName=" + username);
			if("".equals(name)){
				return addUser(map);//新增人员
			}else{
				String modid[] = {"sn","department","title","company","givenName","displayName"};
				ldapExe.mod(name,modid, attrs);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean delUser(Map map){
		String username = (String)map.get("username");
		String name = getDN("userPrincipalName=" + username);
		if(name == null || "".equals(name)){
			return true;
		}
		return ldapExe.disable(name);//禁用用户
	}
	
	private void procUserReturn(ArrayList list){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE t_hr_view SET flag=0 WHERE unique_id IN (");
		for(Iterator it = list.iterator();it.hasNext();){
			sql.append("'" + it.next() + "',");
		}
		sql.deleteCharAt(sql.length());
		sql.append(")");
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString());
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
	}
	
	private void procOrgReturn(ArrayList list){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE t_org_view SET flag=0 WHERE unique_id IN (");
		for(Iterator it = list.iterator();it.hasNext();){
			sql.append("'" + it.next() + "',");
		}
		sql.deleteCharAt(sql.length());
		sql.append(")");
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql.toString());
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(conn);
		}
	}
	
	private String getDN(String filter){
		List list;
		String dn = "";
		try {
			list = ldapExe.getName("",filter);
			if(list.size() == 1){
				dn = (String) list.get(0);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return dn;
	}
}
