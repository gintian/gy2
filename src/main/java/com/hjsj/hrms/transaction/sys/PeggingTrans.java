package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 授权反查
 * @author Luckstar
 * 2010-11-6
 */
public class PeggingTrans extends IBusiness {

	public void execute() throws GeneralException {

		String flag = (String) this.getFormHM().get("flag");
		String id = (String) this.getFormHM().get("id");
		ArrayList pegginglist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			HashMap operMap = getOperUser(dao);
			ArrayList sulist = (ArrayList) operMap.get("sulist");
			ArrayList alllist = (ArrayList) operMap.get("alllist");
			int susize = sulist.size();
			for (int i = 0; i < susize; i++) {// 处理三员角色的超级用户组下用户
				UserView userView = (UserView) sulist.get(i);
				if (userView.isBThreeUser()) {
					String priv = "0";
					if ("set".equals(flag)) {
						priv = userView.analyseTablePriv(id);
					} else if ("item".equals(flag)) {
						priv = userView.analyseFieldPriv(id);
					}else if("func".equals(flag)){
						if(!userView.hasTheFunction(id))
							continue;
						priv ="3";
					}
					
					if (!"0".equals(priv)) {
						LazyDynaBean lb = new LazyDynaBean();
						lb.set("type", ResourceFactory
								.getProperty("label.role.detail.name.0")
								+ " ("
								+ ResourceFactory
										.getProperty("label.sys.warn.domain.role.have.three")
								+ ")");
						lb
								.set(
										"name", getOperUserGroupName(userView.getUserName()));
						lb.set("priv", priv);
						lb.set("status", "T");
						lb.set("iid", userView.getUserId());
						pegginglist.add(lb);
					}
				}
			}
			ArrayList list = new ArrayList();
			if ("set".equals(flag)) {// 子集的反查
				list = searchSysPriv("tablepriv");
			} else if ("item".equals(flag)) {
				list = searchSysPriv("fieldpriv");
			}else if("func".equals(flag)){
				list = searchSysPriv("functionpriv");
			}
			int size = list.size();
			HashMap rolemap = new HashMap();
			StringBuffer roleidSB = new StringBuffer();
			HashMap usermap = new HashMap();
			HashMap userstrmap = new HashMap();
			for (int i = 0; i < size; i++) {
				LazyDynaBean ldb = (LazyDynaBean) list.get(i);
				String priv_str = (String) ldb.get("priv_str");
				String priv = "0";
				if (priv_str.indexOf(","+id) != -1) {
					String status = (String) ldb.get("status");
					if(!"func".equals(flag)){
						int idx = priv_str.indexOf(","+id + "2");
						if (idx != -1)
							priv = "2";
						else
							priv = "1";
					}else {
						priv = "3";
					}
					String iid = (String) ldb.get("id");
					if ("0".equals(status)) {// 业务用户
						if (!alllist.contains(iid)) {// 过滤反查结果中有用户组的授权
							continue;
						}
						LazyDynaBean lb = new LazyDynaBean();
						lb.set("type", ResourceFactory
								.getProperty("label.role.detail.name.0"));
						lb.set("priv", priv);
						lb.set("name", getOperUserGroupName(iid));
						lb.set("status", status);
						lb.set("iid", iid);
						pegginglist.add(lb);
					} else if ("1".equals(status)) {// 角色
						rolemap.put(iid, priv);
						roleidSB.append(",'" + iid + "'");
					} else if ("4".equals(status)) {// 自助用户
						usermap.put(iid.substring(0,3).toUpperCase()+iid.substring(3), priv);
						String dbpre = iid.substring(0, 3).toUpperCase();
						if (userstrmap.containsKey(dbpre)) {
							StringBuffer sb = (StringBuffer) userstrmap
									.get(dbpre);
							sb.append(",'" + iid.substring(3) + "'");
						} else {
							StringBuffer sb = new StringBuffer();
							sb.append(",'" + iid.substring(3) + "'");
							userstrmap.put(dbpre, sb);
						}
					}
				}
			}
			roleid2name(rolemap, roleidSB.toString(), pegginglist, dao);
			this.getA0101(usermap, userstrmap,pegginglist);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("pegginglist", pegginglist);
		}
	}

	private ArrayList searchSysPriv(String priv) {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,status,");
		strsql.append(priv);
		strsql.append(" from t_sys_function_priv");
		ResultSet rset = null;

		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			rset = dao.search(strsql.toString());
			
			while (rset.next()) {
				LazyDynaBean ldb = new LazyDynaBean();
				if ("functionpriv".equalsIgnoreCase(priv))
					ldb.set("priv_str", Sql_switcher.readMemo(rset,
							"functionpriv"));
				if ("tablepriv".equalsIgnoreCase(priv))
					ldb.set("priv_str", Sql_switcher
							.readMemo(rset, "tablepriv"));
				if ("fieldpriv".equalsIgnoreCase(priv))
					ldb.set("priv_str", Sql_switcher
							.readMemo(rset, "fieldpriv"));
				ldb.set("id", rset.getString("id"));
				ldb.set("status", String.valueOf(rset.getInt("status")));
				list.add(ldb);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return list;
	}

	private HashMap getOperUser(ContentDAO dao) throws Exception {
		HashMap map = new HashMap();
		ArrayList sulist = new ArrayList();
		ArrayList alllist = new ArrayList();
		String sql = "select username,password,groupid from operuser where roleid=0 and upper(username)<>'SU'";
		this.frowset = dao.search(sql);
		while (this.frowset.next()) {
			String username = this.frowset.getString("username");
			String password = this.frowset.getString("password");
			int groupid = this.getFrowset().getInt("groupid");
			if (1 == groupid) {// 超级用户组下的用户
				sulist.add(new UserView(username, password == null ? ""
						: password, this.frameconn));
			}
			alllist.add(username);
		}
		int size = sulist.size();
		for (int i = 0; i < size; i++) {
			UserView userView = (UserView) sulist.get(i);
			userView.canLogin();
		}
		map.put("sulist", sulist);
		map.put("alllist", alllist);
		return map;
	}

	private void roleid2name(HashMap rolemap, String roleids,
			ArrayList pegginglist, ContentDAO dao) throws SQLException {
		String sql = "select role_name,role_id from t_sys_role where role_id in('###'"
				+ roleids + ")";
		this.frowset = dao.search(sql);
		while (this.frowset.next()) {
			String role_id = this.frowset.getString("role_id");
			LazyDynaBean ldb = new LazyDynaBean();
			ldb.set("type", ResourceFactory
					.getProperty("label.sys.warn.domain.role"));
			ldb.set("name", this.frowset.getString("role_name"));
			ldb.set("priv", rolemap.get(role_id));
			ldb.set("status", "1");
			ldb.set("iid", role_id);
			pegginglist.add(ldb);
		}
	}
	
	/**
	 * 查询自助用户的
	 * @param status1
	 * @param detailList
	 * @param role_id
	 * @param dao
	 * @throws Exception
	 */
	private void getA0101(HashMap usermap,HashMap userstrmap,ArrayList pegginglist) throws Exception{
		ResultSet rs=null;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			for(Iterator i=userstrmap.keySet().iterator();i.hasNext();){
				String dbname=(String)i.next();
				String sql=null;
				sql = "select dbname from dbname where upper(pre)='"+dbname+"'";
				rs = dao.search(sql);
				String dbdesc=null;
				if(rs.next()){
					dbdesc=rs.getString("dbname");
				}else{
					continue;
				}
				StringBuffer status1pre=(StringBuffer)userstrmap.get(dbname);
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
				if(chk==null)
					 chk="";
				
				if(chk.length()>0){
					sql = "select a0100,a0101,"+chk+",b0110,e0122,e01a1 from "+dbname+"A01 where a0100 in ('##'"+status1pre.toString()+") order by b0110,e0122,e01a1";
				}else{
					sql = "select a0100,a0101,b0110,e0122,e01a1 from "+dbname+"A01 where a0100 in ('##'"+status1pre.toString()+") order by b0110,e0122,e01a1";
				}
				rs= dao.search(sql);
				while(rs.next()){
					LazyDynaBean ldb = new LazyDynaBean();
					String a0100 = rs.getString("a0100");
					String b0110=rs.getString("b0110");
					String e0122=rs.getString("e0122");
					String e01a1=rs.getString("e01a1");
					String a0101=rs.getString("a0101");
					String a0177 = null;
					if(chk.length()>0){
						a0177=rs.getString(chk);
					}
					StringBuffer detailname = new StringBuffer();
					if(b0110!=null&&b0110.length()>0){
						if(e0122!=null&&e0122.length()>0){
							if(e01a1!=null&&e01a1.length()>0){
								detailname.append(getCodeToNameLevel(e01a1)+"  ");
							}else{
								detailname.append(getCodeToNameLevel(e0122)+"  ");
							}
						}else{
							detailname.append(getCodeToNameLevel(b0110)+"  ");
						}
					}
					detailname.append((dbdesc!=null?dbdesc:"")+"/");
					detailname.append((a0101!=null?a0101:"")+"  ");
					detailname.append((a0177!=null?a0177:""));
					ldb.set("type",ResourceFactory
							.getProperty("label.role.detail.name.1"));
					ldb.set("name", detailname.toString());
					ldb.set("priv", usermap.get(dbname.toUpperCase()+a0100));
					ldb.set("status", "4");
					ldb.set("iid", dbname.toUpperCase()+a0100);
					pegginglist.add(ldb);
				}
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			
		}
	}
	
	/**
	 * 某单位/某部门/某岗位
	 * @param codeitemid
	 * @return
	 * @throws Exception
	 */
	private String getCodeToNameLevel(String codeitemid) throws Exception{
		String sql = "select codeitemid,parentid,codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		ResultSet rs=null;
		StringBuffer sb= new StringBuffer();

		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs= dao.search(sql);
			if(rs.next()){
				String parentid=rs.getString("parentid");
				if(codeitemid.equals(parentid)){
					sb.append(rs.getString("codeitemdesc"));
				}else{
					sb.append(rs.getString("codeitemdesc"));
					getParentDesc(parentid,sb,1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return sb.toString();
	}
	private void getParentDesc(String codeitemid,StringBuffer sb,int flag)throws Exception{
		if(flag>10)
			return;//防止死锁
		String sql = "select codeitemid,parentid,codeitemdesc from organization where codeitemid='"+codeitemid+"'";
		ResultSet rs=null;
		boolean f=false;
		String parentid=null;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs= dao.search(sql);
			if(rs.next()){
				parentid=rs.getString("parentid");
				if(codeitemid.equals(parentid)){
					sb.insert(0, rs.getString("codeitemdesc")+"/");
				}else{
					sb.insert(0, rs.getString("codeitemdesc")+"/");
					flag++;
					f=true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			
		}
		if(f){
			getParentDesc(parentid,sb,flag);
		}
	}
	
	private String getOperUserGroupName(String username)throws Exception{
		String sql="select o.username,o.groupid groupid,u.groupname from operuser o,usergroup u where o.groupid=u.groupid and roleid<>1 and o.username='"+username+"'";
		ResultSet rs=null;
		StringBuffer sb= new StringBuffer();
		sb.append("  "+username);

		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs= dao.search(sql);
			if(rs.next()){
				int groupid=rs.getInt("groupid");
				if(1==groupid){
					sb.insert(0,rs.getString("groupname"));
				}else{
					//sb.insert(0,rs.getString("groupname"));
					getGroupParentDesc(groupid,sb,1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			
		}
		return sb.toString();
	}
	
	private void getGroupParentDesc(int groupid,StringBuffer sb,int flag)throws Exception{
		if(flag>10)
			return;//防止死锁
		ResultSet rs=null;
		
		boolean f=false;
		int parentid=0;
		String sql="select o.username,o.groupid groupid,u.groupname groupname from operuser o,usergroup u where o.username=u.groupname and roleid=1 and u.groupid='"+groupid+"'";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs= dao.search(sql);
			if(rs.next()){
				parentid=rs.getInt("groupid");
				if(1==parentid){
					if(flag!=1){
						sb.insert(0, rs.getString("groupname")+"/");
					}else{
						sb.insert(0, rs.getString("groupname"));
					}
				}else{
					if(flag!=1){
						sb.insert(0, rs.getString("groupname")+"/");
					}else{
						sb.insert(0, rs.getString("groupname"));
					}
					flag++;
					f=true;
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			
		}
		if(f){
			getGroupParentDesc(parentid,sb,flag);
		}
	}
}
