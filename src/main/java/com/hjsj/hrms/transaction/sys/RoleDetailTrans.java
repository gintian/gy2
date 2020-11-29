package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.rolemanagement.IndividualManagementBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 显示角色关联人员列表
 * @author xujian
 *Mar 31, 2010
 */
public class RoleDetailTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String role_id=(String)hm.get("roleid");
		role_id=role_id!=null?role_id:"";
		ArrayList detailList = new ArrayList();
		String roleid []=role_id.split(",");
		try{
			IndividualManagementBo ind = new IndividualManagementBo(this.getFrameconn());
			Map map=ind.getRoleDetail(roleid);
			if(map.get(role_id)!=null){
				detailList=(ArrayList)map.get(role_id);
			}
			/*ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="select pre from dbname";
			this.frecset = dao.search(sql);
			StringBuffer dbpre=new StringBuffer();
			while(this.frecset.next()){
				dbpre.append(","+this.frecset.getString("pre"));
			}
			sql = "select staff_id,status from t_sys_staff_in_role where role_id='"+role_id+"'";
			this.frecset = dao.search(sql);
			HashMap status1= new HashMap();//自助用户staff_id
			StringBuffer status2= new StringBuffer();//单位、部门、岗位staff_id
			String detailtype="";
			String detailname="";
			while(this.frecset.next()){
				int status = this.getFrecset().getInt("status");
				if(status==0){//业务用户
					LazyDynaBean ldb = new LazyDynaBean();
					ldb.set("staff_id", this.frecset.getString("staff_id"));
					ldb.set("role_id", role_id);
					detailtype=ResourceFactory.getProperty("label.role.detail.name.0");
					detailname=this.frecset.getString("staff_id");
					ldb.set("detailtype", detailtype);
					ldb.set("detailname", detailname);
					detailList.add(ldb);
				}else if(status==1){
					String staff_id = this.frecset.getString("staff_id");
					if(staff_id.length()>8){
						String pre=staff_id.substring(0, 3);
						if(dbpre.indexOf(pre)!=-1){//验证数据是否未自助用户相应人员库+a0100,然后分组收集
							if(status1.containsKey(pre)){
								StringBuffer status1pre=(StringBuffer)status1.get(pre);
								status1pre.append(",'"+staff_id.substring(3)+"'");
							}else{
								StringBuffer status1pre= new StringBuffer();
								status1pre.append(",'"+staff_id.substring(3)+"'");
								status1.put(pre, status1pre);
							}
						}
					}
					//detailtype=ResourceFactory.getProperty("label.role.detail.name.1");
					//detailname = getA0101(this.frecset.getString("staff_id"),dao);
				}else if(status==2){
					status2.append(",'"+this.frecset.getString("staff_id")+"'");
					//detailtype=ResourceFactory.getProperty("error.usergroup.root.msg");
				}
			}
			getA0101(status1,detailList,role_id,dao);

			getOrgInfo(status2,detailList,role_id,dao);*/
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("detailList", detailList);
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
	private void getA0101(HashMap status1,ArrayList detailList,String role_id,ContentDAO dao) throws Exception{
		ResultSet rs=null;
		String detailtype=ResourceFactory.getProperty("label.role.detail.name.1");
		try{
			for(Iterator i=status1.keySet().iterator();i.hasNext();){
				String dbname=(String)i.next();
				StringBuffer status1pre=(StringBuffer)status1.get(dbname);
				String sql = "select a0100,a0101 from "+dbname+"A01 where a0100 in ('##'"+status1pre.toString()+")";
				rs= dao.search(sql);
				while(rs.next()){
					LazyDynaBean ldb = new LazyDynaBean();
					ldb.set("staff_id", dbname+rs.getString("a0100"));
					ldb.set("role_id", role_id);
					String detailname=rs.getString("a0101");
					ldb.set("detailtype", detailtype);
					ldb.set("detailname", detailname);
					detailList.add(ldb);
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
	 * 查询单位、部门、岗位的
	 * @param status2
	 * @param detailList
	 * @param role_id
	 * @param dao
	 * @throws Exception
	 */
	private void getOrgInfo(StringBuffer status2,ArrayList detailList,String role_id,ContentDAO dao) throws Exception{
		String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid in ('##'"+status2.toString()+")";
		ResultSet rs=null;
		try{
			rs= dao.search(sql);
			while(rs.next()){
				LazyDynaBean ldb = new LazyDynaBean();
				ldb.set("staff_id", rs.getString("codeitemid"));
				ldb.set("role_id", role_id);
				String detailname=rs.getString("codeitemdesc");
				String detailtype=rs.getString("codesetid");
				if("UN".equalsIgnoreCase(detailtype)){
					detailtype=ResourceFactory.getProperty("tree.unroot.undesc");
				}else if("UM".equalsIgnoreCase(detailtype)){
					detailtype=ResourceFactory.getProperty("tree.umroot.umdesc");
				}else{
					detailtype=ResourceFactory.getProperty("hmuster.label.post");
				}
				ldb.set("detailtype", detailtype);
				ldb.set("detailname", detailname);
				detailList.add(ldb);
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
}
