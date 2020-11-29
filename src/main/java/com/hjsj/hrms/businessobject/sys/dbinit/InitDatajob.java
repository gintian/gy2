package com.hjsj.hrms.businessobject.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:InitDatajob.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 1, 2008:5:33:08 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class InitDatajob {
	private ContentDAO dao = null;
	private Connection con = null;
	public InitDatajob(ContentDAO dao,Connection con){
		this.dao = dao;
		this.con = con;
	}
	
	public void DeleteDB(String dbpre){
		RowSet rs = null;
		ArrayList fieldsetlist = new ArrayList();
		DbWizard dbw = new DbWizard(this.con);
		ArrayList sqlsList = new ArrayList();
		try {
		    //zxj 20160422 struts_extends.jar提供了新的移除人员库前缀方法，原方式已封闭
            com.hrms.hjsj.sys.DataDictionary.removeDbpre(dbpre);
            
			dao.delete("delete dbname where pre = '"+dbpre+"'",new ArrayList());
			
			/*rs = dao.search("select fieldsetid from fieldset where UseFlag = '1'");
			while(rs.next()){
				fieldsetlist.add(rs.getString(1));
			}*/
			ArrayList tempset = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			if(tempset!=null) {
                for(int i=0;i<tempset.size();i++){
                    FieldSet set = (FieldSet)tempset.get(i);
                    fieldsetlist.add(set.getFieldsetid());
                }
            }
			StringBuffer sql = new StringBuffer();
			/**?删除此人员库类别下的多媒体子集
			sql.append("drop table "+dbpre+"A00");
			if(dbw.isExistTable(dbpre+"A00",false))
				st.addBatch(sql.toString());*/
			/**?根据指标构库标，删除此人员库类别下的已构建的应用库表*/
			for(int i=0;i<fieldsetlist.size();i++){
				sql.delete(0,sql.length());
				sql.append("drop table "+dbpre+fieldsetlist.get(i));
				if(dbw.isExistTable(dbpre+fieldsetlist.get(i),false)) {
                    sqlsList.add(sql.toString());
                }
			}
			ArrayList operuserlist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select UserName from OperUser ");
			rs = dao.search(sql.toString());
			while(rs.next()){
				operuserlist.add(rs.getString(1));
			}
			/**?删除和用户名有关的查询结果表(除su用户之外)*/
			for(int i=0;i<operuserlist.size();i++){
				//if(operuserlist.get(i).toString().equalsIgnoreCase("su"))
					//continue;
				sql.delete(0,sql.length());
				sql.append("drop table "+operuserlist.get(i)+dbpre+"result");
				if(dbw.isExistTable(operuserlist.get(i)+dbpre+"result",false)) {
                    sqlsList.add(sql.toString());
                }
			}
			if(dbw.isExistTable("t_sys_result",false)) {
                dao.delete("delete from t_sys_result where upper(nbase)='"+dbpre.toUpperCase()+"'",new ArrayList());
            }
			ArrayList lnamelist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select tabid from LName ");
			rs = dao.search(sql.toString());
			while(rs.next()){
				lnamelist.add(rs.getString(1));
			}
			/**?	删除常用花名册*/
			for(int j=0;j<operuserlist.size();j++){
				//if(operuserlist.get(j).toString().equalsIgnoreCase("su"))
					//continue;
				for(int x =0;x<lnamelist.size();x++){
					sql.delete(0,sql.length());
					sql.append("drop table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dbpre);
					if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dbpre,false)) {
                        sqlsList.add(sql.toString());
                    }
				}
			}
			dao.batchUpdate(sqlsList);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
			
		}
		
	}
	/**
	 * 初始化数据和结构
	 * @param dblist 人员库集合
	 * @param variable delete or drop
	 */
	public void InitData(ArrayList dblist,String variable){
		DbWizard dbw = new DbWizard(this.con);
		RowSet rs = null;
		ArrayList sqlsList = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select fieldsetid from fieldset where fieldsetid like 'A%'");
			ArrayList list = new ArrayList();
			rs = dao.search(sql.toString());
			while(rs.next()){
				list.add(rs.getString(1));
			}
			/**清空人员（所有人员库分类)已构库的子集的记录（含多媒体子集的信息）*/
			//ArrayList dblist = userView.getPrivDbList();
			for(int j=0;j<dblist.size();j++){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table "+dblist.get(j)+"A00");
				if(dbw.isExistTable(""+dblist.get(j)+"A00",false)) {
                    sqlsList.add(sql.toString());
                }
				for(int i=0;i<list.size();i++){
					sql.delete(0,sql.length());
					sql.append(variable+" table "+dblist.get(j)+list.get(i));
					if(dbw.isExistTable(""+dblist.get(j)+list.get(i),false)) {
                        sqlsList.add(sql.toString());
                    }
				}
			}
			
			sql.setLength(0);
			list.clear();
			sql.append("select fieldsetid from fieldset where fieldsetid  not like 'A%'");
			rs = dao.search(sql.toString());
			while(rs.next()){
				list.add(rs.getString(1));
			}
			/**清空单位和职位已构库的子集的记录（含多媒体子集的信息）*/
			//ArrayList dblist = userView.getPrivDbList();
			for(int i=0;i<list.size();i++){
				sql.delete(0,sql.length());
				sql.append(variable+" table "+list.get(i));
				if(dbw.isExistTable(""+list.get(i),false)) {
                    sqlsList.add(sql.toString());
                }
			}
			sql.delete(0,sql.length());
			sql.append("TRUNCATE table B00");
			if(dbw.isExistTable("B00",false)) {
                sqlsList.add(sql.toString());
            }
			sql.delete(0,sql.length());
			sql.append("TRUNCATE table K00");
			if(dbw.isExistTable("K00",false)) {
                sqlsList.add(sql.toString());
            }
			
			
			if("drop".equalsIgnoreCase(variable)){
				if(dbw.isExistTable("fieldset",false)) {
                    sqlsList.add("update fieldset set useflag='0'");
                }
				if(dbw.isExistTable("fielditem",false)) {
                    sqlsList.add("update fielditem set useflag='0'");
                }
			}
			/**清空变动日志表的记录*/
			if(dbw.isExistTable("A01LOG",false)){
				sql.delete(0,sql.length());
				sql.append(variable+" table A01LOG");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("B01LOG",false)){
				sql.delete(0,sql.length());
				sql.append(variable+" table B01LOG");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("K01LOG",false)){
				sql.delete(0,sql.length());
				sql.append(variable+" table K01LOG");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("SUBLOG",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table SUBLOG");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("t_hr_archlog",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table t_hr_archlog");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("t_hr_mydata_chg",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table t_hr_mydata_chg");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("syslog",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table syslog");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("sys_data_log",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table sys_data_log");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("t_hr_view",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table t_hr_view");
				sqlsList.add(sql.toString());
			}
			/**清空常用花名册表中的记录,根据常用花名册表LName、人员库分类表以及用户信息表operuser取得临时表的名称
			 * 人员花名册　m+花名册号+”_”+用户名+”_”+人员库前缀
			 * 单位花名册　m+花名册号+”_”+用户名+”_”+B
			 * 职位花名册　m+花名册号+”_”+用户名+”_”+K*/
			ArrayList lnamelist = new ArrayList();
			ArrayList operuserlist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select tabid from LName ");
			rs = dao.search(sql.toString());
			while(rs.next()){
				lnamelist.add(rs.getString(1));
			}
			sql.delete(0,sql.length());
			sql.append("select UserName from OperUser ");
			rs = dao.search(sql.toString());
			while(rs.next()){
				operuserlist.add(rs.getString(1));
			}
			
			for(int x =0;x<lnamelist.size();x++){
				for(int j=0;j<operuserlist.size();j++){
					//if(variable.equalsIgnoreCase("drop")){
						if("su".equalsIgnoreCase(operuserlist.get(j).toString())){
							for(int i=0;i<dblist.size();i++){
								sql.delete(0,sql.length());
								sql.append("TRUNCATE table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i));
								if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i),false)) {
                                    sqlsList.add(sql.toString());
                                }
							}
							sql.delete(0,sql.length());
							sql.append("TRUNCATE table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B");
							if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B",false)) {
                                sqlsList.add(sql.toString());
                            }
							sql.delete(0,sql.length());
							sql.append("TRUNCATE table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K");
							if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K",false)) {
                                sqlsList.add(sql.toString());
                            }
							continue;
						}
						for(int i=0;i<dblist.size();i++){
							sql.delete(0,sql.length());
							sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i));
							if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i),false)) {
                                sqlsList.add(sql.toString());
                            }
						}
						sql.delete(0,sql.length());
						sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B");
						if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B",false)) {
                            sqlsList.add(sql.toString());
                        }
						sql.delete(0,sql.length());
						sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K");
						if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K",false)) {
                            sqlsList.add(sql.toString());
                        }
					/*}
					else{
						for(int i=0;i<dblist.size();i++){
							sql.delete(0,sql.length());
							sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i));
							if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_"+dblist.get(i),false))
								st.addBatch(sql.toString());
						}
						sql.delete(0,sql.length());
						sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B");
						if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_B",false))
							st.addBatch(sql.toString());
						sql.delete(0,sql.length());
						sql.append(variable+" table m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K");
						if(dbw.isExistTable("m"+lnamelist.get(x)+"_"+operuserlist.get(j)+"_K",false))
							st.addBatch(sql.toString());
					}*/
				}
			}
			/**清空高级花名册信息表中的记录，根据高级花名册表muster_name,用户信息表operuser取得临时表的名称
			 * 花名册表名：用户名＋“_muster_”＋表格号(tabid)*/
			ArrayList musterlist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select Tabid from muster_name");
			rs = dao.search(sql.toString());
			while(rs.next()){
				musterlist.add(rs.getString(1));
			}
			for(int i=0;i<musterlist.size();i++){
				for(int j=0;j<operuserlist.size();j++){
					//if(variable.equalsIgnoreCase("drop")){
						if("su".equalsIgnoreCase(operuserlist.get(j).toString())){
							sql.delete(0,sql.length());
							sql.append("TRUNCATE table "+operuserlist.get(j)+"_muster_"+""+musterlist.get(i));
							if(dbw.isExistTable(""+operuserlist.get(j)+"_muster_"+""+musterlist.get(i),false)) {
                                sqlsList.add(sql.toString());
                            }
							continue;
						}
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(j)+"_muster_"+""+musterlist.get(i));
						if(dbw.isExistTable(""+operuserlist.get(j)+"_muster_"+""+musterlist.get(i),false)) {
                            sqlsList.add(sql.toString());
                        }
					/*}else{
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(j)+"_muster_"+""+musterlist.get(i));
						if(dbw.isExistTable(""+operuserlist.get(j)+"_muster_"+""+musterlist.get(i),false))
							st.addBatch(sql.toString());
					}*/
				}
			}
			/**清空工资类别表及工资历史数据表中的记录，根据工资类别表(salarytemplate)、用户信息表
			 * 用户名＋”_salary_”+工资类别号(salaryid)*/
			ArrayList salarylist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select salaryid from salarytemplate");
			rs = dao.search(sql.toString());
			while(rs.next()){
				salarylist.add(rs.getString(1));
			}
			for(int i=0;i<salarylist.size();i++){
				for(int j=0;j<operuserlist.size();j++){
					//if(variable.equalsIgnoreCase("drop")){
						if("su".equalsIgnoreCase(operuserlist.get(j).toString())){
							sql.delete(0,sql.length());
							sql.append("TRUNCATE table "+operuserlist.get(j)+"_salary_"+""+salarylist.get(i));
							if(dbw.isExistTable(""+operuserlist.get(j)+"_salary_"+""+salarylist.get(i),false)) {
                                sqlsList.add(sql.toString());
                            }
							continue;
						}
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(j)+"_salary_"+""+salarylist.get(i));
						if(dbw.isExistTable(""+operuserlist.get(j)+"_salary_"+""+salarylist.get(i),false)) {
                            sqlsList.add(sql.toString());
                        }
					/*}else{
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(j)+"_salary_"+""+salarylist.get(i));
						if(dbw.isExistTable(""+operuserlist.get(j)+"_salary_"+""+salarylist.get(i),false))
							st.addBatch(sql.toString());
					}*/
				}
			}
			sql.delete(0,sql.length());
			sql.append("TRUNCATE table salaryhistory");
			if(dbw.isExistTable("salaryhistory",false)) {
                sqlsList.add(sql.toString());
            }
			
			/**清空业务模板临时表、表单归档信息表和消息库的记录，根据用户信息表及业务模板表template_table。
			 * Tmessage、template_archive、templet_模板号(模板表中的tabid)、g_templet_模板号(模板表中的tabid)、用户名+ templet_模板号(模板表中的tabid)*/
			if(dbw.isExistTable("Tmessage",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table Tmessage");
				sqlsList.add(sql.toString());
			}
			if(dbw.isExistTable("template_archive",false)){
				sql.delete(0,sql.length());
				sql.append("TRUNCATE table template_archive");
				sqlsList.add(sql.toString());
			}
			ArrayList templetlist = new ArrayList();
			sql.delete(0,sql.length());
			sql.append("select TabId from template_table");
			rs = dao.search(sql.toString());
			while(rs.next()){
				templetlist.add(rs.getString(1));
			}
			for(int i=0;i<templetlist.size();i++){
				sql.delete(0,sql.length());
				sql.append(variable+" table templet_"+templetlist.get(i));
				if(dbw.isExistTable("templet_"+templetlist.get(i),false)) {
                    sqlsList.add(sql.toString());
                }
				sql.delete(0,sql.length());
				sql.append(variable+" table g_templet_"+templetlist.get(i));
				if(dbw.isExistTable("g_templet_"+templetlist.get(i),false)) {
                    sqlsList.add(sql.toString());
                }
			}
			for(int i=0;i<operuserlist.size();i++){
				for(int j=0;j<templetlist.size();j++){
					//if(variable.equalsIgnoreCase("drop")){
						if("su".equalsIgnoreCase(operuserlist.get(i).toString())){
							sql.delete(0,sql.length());
							sql.append("TRUNCATE table "+operuserlist.get(i)+"templet_"+templetlist.get(j));
							if(dbw.isExistTable(operuserlist.get(i)+"templet_"+templetlist.get(j),false)) {
                                sqlsList.add(sql.toString());
                            }
							continue;
						}
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(i)+"templet_"+templetlist.get(j));
						if(dbw.isExistTable(operuserlist.get(i)+"templet_"+templetlist.get(j),false)) {
                            sqlsList.add(sql.toString());
                        }
					/*}else{
						sql.delete(0,sql.length());
						sql.append(variable+" table "+operuserlist.get(i)+"templet_"+templetlist.get(j));
						if(dbw.isExistTable(operuserlist.get(i)+"templet_"+templetlist.get(j),false))
							st.addBatch(sql.toString());
					}*/
				}
			}
			
			//清空角色关联表数据
			sql.delete(0,sql.length());
			sql.append("TRUNCATE table t_sys_staff_in_role");
			if(dbw.isExistTable("t_sys_staff_in_role",false)) {
                sqlsList.add(sql.toString());
            }
			dao.batchUpdate(sqlsList);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
