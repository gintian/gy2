package com.hjsj.hrms.transaction.sys.dbinit.changebase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:SaveDbTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 6, 2008:2:01:54 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveDbTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		DynaBean dbbean = (DynaBean)this.getFormHM().get("dbvo");
		String dbname = (String)dbbean.get("dbname");
		String pre = (String)dbbean.get("pre");
		String vflag = (String)dbbean.get("vflag");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo dbvo = new RecordVo("dbname");
		DbWizard dbw = new DbWizard(this.frameconn);
		try {
			if("0".equalsIgnoreCase(vflag)){
				DBMetaModel dbm = new DBMetaModel(this.frameconn);
				dbvo.setString("dbname",dbname);
				pre = pre.substring(0,1).toUpperCase()+pre.substring(1).toLowerCase();
				//xus 20/5/11 达梦数据库不需要自增id 【60334】VFS+UTF-8+达梦：系统管理，库结构，指标体系，调整人员库，新建保存时提示： 仅当指定列列表，且SET IDENTITY_INSERT为ON时，才能对自增列赋值，保存不了
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){//oracle 无自动自增功能 xuj add 2010-7-10
					this.frowset=dao.search("select max(dbid) dbid from dbname");
					if(this.frowset.next()){
						dbvo.setInt("dbid", this.frowset.getInt("dbid")+1);
					}
				}
				dbvo.setString("pre",pre);
				dbvo.setString("flag","100");
				dao.addValueObject(dbvo);
				ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
				ArrayList fieldlist = new ArrayList();
				for(int i=0;i<fieldsetlist.size();i++){
					FieldSet set = (FieldSet)fieldsetlist.get(i);
					fieldlist.add(set.getFieldsetid());
				}
				fieldlist.remove("A00");
				StringBuffer sql = new StringBuffer();
				//ArrayList sqllist = new ArrayList();
				
				if(Sql_switcher.searchDbServer()==Constant.ORACEL){//oracle 
					sql.append("create table "+pre+"A00 as select * from UsrA00 where 1=2");
					if(dbw.isExistTable(pre+"A00", false))
						dbw.dropTable(pre+"A00");
				}else{
					sql.append("select * into "+pre+"A00 from UsrA00 where 1=2");
				}
				dao.update(sql.toString());
				sql.setLength(0);
				sql.append("alter table  "+pre+"A00 add constraint pk_"+pre+"A00 primary key (a0100,i9999)");
				dao.update(sql.toString());
				dbm.reloadTableModel(pre+"A00");
				//sqllist.add(sql.toString());
				for(int i=0;i<fieldlist.size();i++){
					sql.delete(0,sql.length());
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){//oracle 
						sql.append("create table "+pre+fieldlist.get(i)+" as select * from Usr"+fieldlist.get(i)+" where 1=2 ");
						if(dbw.isExistTable(pre+fieldlist.get(i), false))
							dbw.dropTable(pre+fieldlist.get(i));
					}else{
						sql.append("select * into "+pre+fieldlist.get(i)+" from Usr"+fieldlist.get(i)+" where 1=2 ");
					}
					dao.update(sql.toString());
					sql.delete(0,sql.length());
					if("A01".equalsIgnoreCase((String)fieldlist.get(i))){
						sql.append("alter table "+pre+fieldlist.get(i)+" add constraint pk_"+pre+fieldlist.get(i)+" primary key (a0100)");
					}else{
						sql.append("alter table  "+pre+fieldlist.get(i)+" add constraint pk_"+pre+fieldlist.get(i)+" primary key (a0100,i9999)");
					}
					dao.update(sql.toString());
					dbm.reloadTableModel(pre+fieldlist.get(i));
					//sqllist.add(sql.toString());
				}
				ArrayList operuserlist = new ArrayList();
				sql.delete(0,sql.length());
				sql.append("select UserName from OperUser ");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					operuserlist.add(this.frowset.getString(1));
				}
				for(int i=0;i<operuserlist.size();i++){
					sql.delete(0,sql.length());
					sql.append("create table "+operuserlist.get(i)+pre+"result (" +
							"a0100 varchar(10) null, " +
							"b0100 varchar(30) null " +
							")");
					dao.update(sql.toString());
					dbm.reloadTableModel(operuserlist.get(i)+pre+"result");
					//sqllist.add(sql);
				}
				//dao.batchUpdate(sqllist);
				//写入权限表，当前用户默认拥有此人员库权限
				this.writerDBPriv(pre);
			}else{
				this.frowset = dao.search("select * from dbname where pre = '"+pre+"'");
				if(this.frowset.next()){
					int dbid = this.frowset.getInt("dbid");
					dbvo.setInt("dbid",dbid);
					dbvo = dao.findByPrimaryKey(dbvo);
					dbvo.setString("dbname",dbname);
					dao.updateValueObject(dbvo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void writerDBPriv(String pre){
	    //zxj 20160422 struts_extends.jar提供了新的添加人员库前缀方法，原方式已封闭
        DataDictionary.addDbpre(pre);
		this.userView.getDbpriv().append(pre+",");
		RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",this.userView.getDbname()+this.userView.getUserId());
        vo.setInt("status",this.getUserView().getStatus()/*GeneralConstant.ROLE*/);  
        ContentDAO dao=new ContentDAO(this.frameconn);
        boolean bnew=false;
        try
        {
            /**
             * 未定义权限则增加记录
             */
            try
            {   vo = dao.findByPrimaryKey(vo);
                //dao.findRowSetDynaClassByPrimaryKey(vo);
            	String dbpriv=vo.getString("dbpriv");
            	if(dbpriv!=null&&dbpriv.length()>0){
            		if(dbpriv.endsWith(",")){
            			vo.setString("dbpriv", dbpriv+pre+",");
            		}else{
            			vo.setString("dbpriv", dbpriv+","+pre+",");
            		}
            	}else{
            		vo.setString("dbpriv",","+pre+",");
            	}
                bnew=true;
            }
            catch(GeneralException dd)
            {
            	 vo.setString("dbpriv", ","+pre+",");//未找到记录
            }
            if(bnew)
            {
           		dao.updateValueObject(vo);
            }
            else
                dao.addValueObject(vo);
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        catch(GeneralException ge)
        {
            ge.printStackTrace();
        }
	}
}
