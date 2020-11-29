package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * 
 *<p>Title:LayerOrgTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Nov 19, 2008:4:01:06 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class LayerOrgTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		RowSet rs=null;
		try {
			sql.append(SetLayerNull("organization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			sql.append(InitLayer("organization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			int i=1;
			while(true){
				sql.append(NextLayer("organization",i));
				int j = dao.update(sql.toString());
				if(j==0)
					break;
				i++;
				sql.delete(0,sql.length());
			}
			sql.delete(0,sql.length());
			sql.append(SetLayerNull("vorganization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			sql.append(InitLayer("vorganization"));
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			i=1;
			while(true){
				sql.append(NextLayer("vorganization",i));
				int j = dao.update(sql.toString());
				if(j==0)
					break;
				i++;
				sql.delete(0,sql.length());
			}
			
			//重置grade
			sql.delete(0,sql.length());
			//dao.update("update organization set grade=0");
			sql.append("update organization set grade=1 where parentid=codeitemid");
			dao.update(sql.toString());
			i=1;
			while (i<40){
				sql.setLength(0);
				sql.append(" update organization set grade="+(i+1)+" where parentid in (select codeitemid from  organization where grade="+i+") and parentid<>codeitemid");
				if(dao.update(sql.toString())<=0){
					break;
				}
				i++;
			}

			/**
			 * 重置虚拟机构grade
			 * @author ZhangHua
			 * @date 14:30 2018/11/13
			 */
			dao.update("update vorganization set grade=1 where parentid=codeitemid");
			dao.update("update vorganization set grade=(select grade+1 from organization where vorganization.parentid=organization.codeitemid) where parentid in (select codeitemid from  organization )");

			rs=dao.search("select codeitemid from vorganization where codeitemid in (select parentid from vorganization ) order by codeitemid");
			while(rs.next()) {
				ArrayList list=new ArrayList();
				list.add(rs.getString("codeitemid"));
				list.add(rs.getString("codeitemid"));
				dao.update("update vorganization set grade=(select grade from vorganization o where o.codeitemid=?)+1 where parentid=? ",list);
			}

			// 重置a0000
			this.resetA0000();
			this.userView.getHm().put("isrefreshOrg", "1");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}


	private String SetLayerNull(String tbname){
		String sql = "update "+tbname+" set layer = null";
		return sql;
	}
	private String InitLayer(String tbname){
		String sql = "update "+tbname+" set layer=1 where (codeitemid=parentid) or "+
	    " not (parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid))";
		return sql;
	}
	private String NextLayer(String tbname,int lay){
		String sql = "update "+tbname+" set layer='"+(lay+1)+"' where codeitemid<>parentid and "+
	       " parentid in (select codeitemid from "+tbname+" B where "+tbname+".codesetid=B.codesetid and B.layer='"+lay+"')";;
	    return sql;
	}
	
	/**
	 * 机构a0000重置
	 * chent 20170916
	 * 
	 */
	private void resetA0000() {
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			// 排序
			String sql = "select * from organization order by codeitemid";
			ArrayList<DynaBean> metaDataList = dao.searchDynaList(sql);
			ArrayList<DynaBean> list = new ArrayList<DynaBean>();
			String parentid_bak = (String)metaDataList.get(0).get("parentid");
			ArrayList<DynaBean> list_sort = new ArrayList<DynaBean>(); 
			for(int i=0; i<metaDataList.size(); i++) {
				DynaBean bean = metaDataList.get(i);
				String parentid = (String)bean.get("parentid");
				
				if(!parentid.equals(parentid_bak)) {
					Collections.sort(list_sort, new Comparator<DynaBean>(){  
						
			            public int compare(DynaBean b1, DynaBean b2) {  
			            	int b1_a0000 = Integer.parseInt((String)b1.get("a0000"));
			            	int b2_a0000 = Integer.parseInt((String)b2.get("a0000"));
			            	
			                return b1_a0000 > b2_a0000 ? 1:-1;  
			            }  
			        });  
					list.addAll(list_sort);
					list_sort.clear();
				}
				list_sort.add(bean);
				parentid_bak = parentid;
			}
			
			// 写回
			String updateSql = "update organization set a0000=? where codeitemid=?" ;
			ArrayList updateList = new ArrayList();
			for(int i=0; i<list.size(); i++) {
				ArrayList dataList = new ArrayList();
				dataList.add(i+1);
				dataList.add((String)list.get(i).get("codeitemid"));
				
				updateList.add(dataList);
			}
			dao.batchUpdate(updateSql, updateList);
			
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
}
