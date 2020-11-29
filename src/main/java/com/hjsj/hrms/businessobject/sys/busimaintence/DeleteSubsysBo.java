package com.hjsj.hrms.businessobject.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 
 * <p>Title:删除已构库的业务子集表<用户模式下只能删除自己构建的表></p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 11, 2008:2:42:17 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleteSubsysBo {
	private Connection conn = null;
	
	public DeleteSubsysBo(Connection a_con){
		this.conn = a_con;
	}
	/*
	 * 子系统信息
	 */
	public ArrayList getsubsys(String objid){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		try{
			String sql = "select id,name from t_hr_subsys where id= '"+objid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet ret = dao.search(sql);
			while(ret.next()){
				da = new CommonData();
				da.setDataName(ret.getString("name"));
				da.setDataValue(ret.getString("id"));
				list.add(da);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getbusitable(String objid,String userType){
		ArrayList list = new ArrayList();
		CommonData da = new CommonData();
		String use = userType;
		if(use==null||use=="") {
            use="0";
        }
		try{
			if(!"35".equalsIgnoreCase(objid)){
				if("1".equals(use)){
					String sql1="select fieldsetid,customdesc from t_hr_busitable where id='"+objid+"' and useflag='1' order by displayorder";
					ContentDAO dao = new ContentDAO(this.conn);
					RowSet ret = dao.search(sql1);
					while(ret.next()){
						da = new CommonData();
						da.setDataName(ret.getString("customdesc"));
						da.setDataValue(ret.getString("fieldsetid"));
						list.add(da);
					}
				}else if("0".equals(use)||use==""){
					String sql2="select fieldsetid,customdesc from t_hr_busitable  where id= '"+objid+"' and useflag='1' and ownflag='0' order by displayorder";
					ContentDAO dao = new ContentDAO(this.conn);
					RowSet ret = dao.search(sql2);
					while(ret.next()){
						da = new CommonData();
						da.setDataName(ret.getString("customdesc"));
						da.setDataValue(ret.getString("fieldsetid"));
						list.add(da);
					}
				}
			}else if("35".equalsIgnoreCase(objid)){
				String sql1="select fieldsetid,customdesc from t_hr_busitable where id='"+objid+"' and useflag='1' order by displayorder";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet ret = dao.search(sql1);
				while(ret.next()){
					da = new CommonData();
					da.setDataName(ret.getString("customdesc"));
					da.setDataValue(ret.getString("fieldsetid"));
					list.add(da);
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/*
	 * 删除子集,同时删除所属指标;=0/=null用户模式，=1开发商模式
	 * obj 属于哪个模块
	 * userType 用户模式 1开发商 0用户
	 * set 所选子集
	 */
	public void deletbusitable(String set,String userType,String obj){
		try{
			if("35".equalsIgnoreCase(obj))
			{
				StringBuffer buf = new StringBuffer();
				StringBuffer sql= new StringBuffer("update t_hr_busitable set useflag='0' where fieldsetid in (");
				if(set.indexOf("/")==-1){
					buf.append("'");
					buf.append(set);
					buf.append("'");
					sql.append(buf.toString());
				}else{
					String[] arr = set.split("/");
					for(int i=0;i<arr.length;i++){
						buf.append(",");
						buf.append("'");
						buf.append(arr[i]);
						buf.append("'");
					}
					sql.append(buf.toString().substring(1));
				}
				sql.append(")");
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(sql.toString());
				
				//删除指标
				StringBuffer bufs = new StringBuffer();
				StringBuffer sqls= new StringBuffer("update t_hr_busifield set useflag='0' where fieldsetid in (");
				if(set.indexOf("/")==-1){
					bufs.append("'");
					bufs.append(set);
					bufs.append("'");
					sqls.append(bufs.toString());
				}else{
					String[] arr = set.split("/");
					for(int i=0;i<arr.length;i++){
						bufs.append(",");
						bufs.append("'");
						bufs.append(arr[i]);
						bufs.append("'");
					}
					sqls.append(bufs.toString().substring(1));
				}
				sqls.append(")");
				ContentDAO daos = new ContentDAO(this.conn);
				daos.update(sqls.toString());
			}else
			{
				if("1".equals(userType)){
					StringBuffer buf = new StringBuffer();
					StringBuffer sql= new StringBuffer("update t_hr_busitable set useflag='0' where fieldsetid in (");
					if(set.indexOf("/")==-1){
						buf.append("'");
						buf.append(set);
						buf.append("'");
						sql.append(buf.toString());
					}else{
						String[] arr = set.split("/");
						for(int i=0;i<arr.length;i++){
							buf.append(",");
							buf.append("'");
							buf.append(arr[i]);
							buf.append("'");
						}
						sql.append(buf.toString().substring(1));
					}
					sql.append(")");
					ContentDAO dao = new ContentDAO(this.conn);
					dao.update(sql.toString());
					
					//删除指标
					StringBuffer bufs = new StringBuffer();
					StringBuffer sqls= new StringBuffer("update t_hr_busifield set useflag='0' where fieldsetid in (");
					if(set.indexOf("/")==-1){
						bufs.append("'");
						bufs.append(set);
						bufs.append("'");
						sqls.append(bufs.toString());
					}else{
						String[] arr = set.split("/");
						for(int i=0;i<arr.length;i++){
							bufs.append(",");
							bufs.append("'");
							bufs.append(arr[i]);
							bufs.append("'");
						}
						sqls.append(bufs.toString().substring(1));
					}
					sqls.append(")");
					ContentDAO daos = new ContentDAO(this.conn);
					daos.update(sqls.toString());
				}else if("0".equals(userType)||userType==""){
					StringBuffer bufi = new StringBuffer();
					StringBuffer sqli= new StringBuffer("update t_hr_busitable set useflag='0' where fieldsetid in (");
					if(set.indexOf("/")==-1){
						bufi.append("'");
						bufi.append(set);
						bufi.append("'");
						sqli.append(bufi.toString());
					}else{
						String[] arr = set.split("/");
						for(int i=0;i<arr.length;i++){
							bufi.append(",");
							bufi.append("'");
							bufi.append(arr[i]);
							bufi.append("'");
						}
						sqli.append(bufi.toString().substring(1));
					}
					sqli.append(") and ownflag='0'");
					ContentDAO daoi = new ContentDAO(this.conn);
					daoi.update(sqli.toString());
					
	                //删除指标
					StringBuffer bufsi = new StringBuffer();
					StringBuffer sqlsi= new StringBuffer("update t_hr_busifield set useflag='0' where fieldsetid in (");
					if(set.indexOf("/")==-1){
						bufsi.append("'");
						bufsi.append(set);
						bufsi.append("'");
						sqlsi.append(bufsi.toString());
					}else{
						String[] arr = set.split("/");
						for(int i=0;i<arr.length;i++){
							bufsi.append(",");
							bufsi.append("'");
							bufsi.append(arr[i]);
							bufsi.append("'");
						}
						sqlsi.append(bufsi.toString().substring(1));
					}
					sqlsi.append(") and ownflag='0'");
					ContentDAO daosi = new ContentDAO(this.conn);
					daosi.update(sqlsi.toString());
				}
			}
			
			ContentDAO daot = new ContentDAO(this.conn);
			Table table=null;
			if(set.indexOf("/")==-1){
				table=new Table(set);
				DbWizard dbWizard=new DbWizard(this.conn);
				if(dbWizard.isExistTable(table.getName(),false)) //判断这个表存在不
				{
					dbWizard.dropTable(table);  //删除这个表
				}
			}else{
				String[] arr = set.split("/");
				for(int i=0;i<arr.length;i++){
					table=new Table(arr[i]);
					DbWizard dbWizard=new DbWizard(this.conn);
					if(dbWizard.isExistTable(table.getName(),false)) //判断这个表存在不
					{
						dbWizard.dropTable(table);  //删除这个表
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
