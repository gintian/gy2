package com.hjsj.hrms.businessobject.sys.dbinit;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:FormationBase.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Oct 11, 2008:4:08:22 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class FormationBase {
	private ContentDAO dao = null;
	private Connection con = null;
	public FormationBase(ContentDAO dao,Connection con){
		this.dao = dao;
		this.con = con;
	}
	public void formation(String type,String infor,String fieldsetid,ArrayList fielditemlist,UserView userView)throws GeneralException{
		StringBuffer sql = new StringBuffer();
		Table table =null;
		Table tablelog =null;
		DbWizard dbw = new DbWizard(con);
		if("0".equalsIgnoreCase(type)){//构库
			if("A".equalsIgnoreCase(infor)){
				table = new Table("Usr"+fieldsetid);
				if("A01".equalsIgnoreCase(fieldsetid)){//?	构建主集A01
					table = addField(table,"A0000",false,DataType.INT,0);
					table = addField(table,"A0100",true,DataType.STRING,8);
					table = addField(table,"B0110",false,DataType.STRING,30);
					//table = addField(table,"E0122",false,DataType.STRING,30);
					table = addField(table,"E01A1",false,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					table = addField(table,"UserName",false,DataType.STRING,50);
					table = addField(table,"UserPassword",false,DataType.STRING,50);
					table = addField(table,"Groups",false,DataType.STRING,50);
					
					//构建主集日志表
					tablelog = new Table(fieldsetid+"Log");
					tablelog = addField(tablelog,"A0000",false,DataType.INT,0);
					tablelog = addField(tablelog,"A0100",false,DataType.STRING,8);
					tablelog = addField(tablelog,"B0110",false,DataType.STRING,30);
					//tablelog = addField(tablelog,"E0122",false,DataType.STRING,30);
					tablelog = addField(tablelog,"E01A1",false,DataType.STRING,30);
					tablelog = addField(tablelog,"CreateTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime1",false,DataType.DATE,10);
					tablelog = addField(tablelog,"CreateUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"ModUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"SBASE",false,DataType.STRING,3);
					tablelog = addField(tablelog,"DBASE ",false,DataType.STRING,3);
					tablelog = addField(tablelog,"SETID",false,DataType.STRING,3);
					tablelog = addField(tablelog,"State",false,DataType.STRING,10);
					tablelog = addField(tablelog,"UserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"UserPassword",false,DataType.STRING,50);
					tablelog = addField(tablelog,"Groups",false,DataType.STRING,50);
				}else{//	?构建子集-（UsrA02—Axx）
					table = addField(table,"A0100",true,DataType.STRING,8);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					table = addField(table,"SealFlag",false,DataType.INT,0);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
					
				}
			}else if("B".equalsIgnoreCase(infor)){//?	主集（B01）
				table = new Table(fieldsetid);
				if("B01".equalsIgnoreCase(fieldsetid)){
					table = addField(table,"B0110",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					
					//构建主集日志表
					tablelog = new Table(fieldsetid+"Log");
					tablelog = addField(tablelog,"B0110",false,DataType.STRING,30);
					tablelog = addField(tablelog,"CreateTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime1",false,DataType.DATE,10);
					tablelog = addField(tablelog,"CreateUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"ModUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"SBASE",false,DataType.STRING,3);
					tablelog = addField(tablelog,"DBASE",false,DataType.STRING,3);
					tablelog = addField(tablelog,"SETID",false,DataType.STRING,3);
					tablelog = addField(tablelog,"State",false,DataType.STRING,10);
				}else{//?	子集(B02—Bxx)
					table = addField(table,"B0110",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if("K".equalsIgnoreCase(infor)){
				table = new Table(fieldsetid);
				if("K01".equalsIgnoreCase(fieldsetid)){//?	主集－K01
					table = addField(table,"E01A1",true,DataType.STRING,30);
					table = addField(table,"E0122",false,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					
					//构建主集日志表
					tablelog = new Table(fieldsetid+"Log");
					tablelog = addField(tablelog,"E01A1",false,DataType.STRING,30);
					tablelog = addField(tablelog,"E0122",false,DataType.STRING,30);
					tablelog = addField(tablelog,"CreateTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime",false,DataType.DATE,10);
					tablelog = addField(tablelog,"ModTime1",false,DataType.DATE,10);
					tablelog = addField(tablelog,"CreateUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"ModUserName",false,DataType.STRING,50);
					tablelog = addField(tablelog,"SBASE",false,DataType.STRING,3);
					tablelog = addField(tablelog,"DBASE",false,DataType.STRING,3);
					tablelog = addField(tablelog,"SETID",false,DataType.STRING,3);
					tablelog = addField(tablelog,"State",false,DataType.STRING,10);
				}else{//?	子集-(K02—Kxx)
					table = addField(table,"E01A1",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if("H".equalsIgnoreCase(infor)){
				table = new Table(fieldsetid);
				if("H01".equalsIgnoreCase(fieldsetid)){
					table = addField(table,"H0100",true,DataType.STRING,30);
					//table = addField(table,"B0110",false,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
				}else{
					table = addField(table,"H0100",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if("Y".equalsIgnoreCase(infor)){  //党组织
				table = new Table(fieldsetid);
				if("Y01".equalsIgnoreCase(fieldsetid)){
					table = addField(table,"Y0100",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
				}else{
					table = addField(table,"Y0100",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if("V".equalsIgnoreCase(infor)){  //团组织
				table = new Table(fieldsetid);
				if("V01".equalsIgnoreCase(fieldsetid)){
					table = addField(table,"V0100",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
				}else{
					table = addField(table,"V0100",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if("W".equalsIgnoreCase(infor)){  //工会组织
				table = new Table(fieldsetid);
				if("W01".equalsIgnoreCase(fieldsetid)){
					table = addField(table,"W0100",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
				}else{
					table = addField(table,"W0100",true,DataType.STRING,30);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName",false,DataType.STRING,50);
					//DataDictionary.refresh();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}
			
			for(int i=0;i<fielditemlist.size();i++){
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				fieldset.setUseflag("1");
				if("1".equalsIgnoreCase(fieldset.getChangeflag())){
					if(fielditemlist.get(i).toString().equalsIgnoreCase(fieldsetid+"Z0")||fielditemlist.get(i).toString().equalsIgnoreCase(fieldsetid+"Z1")) {
                        continue;
                    }
				}
				table = addField(table,fielditemlist.get(i).toString(),false,0,0);
				if("A01".equalsIgnoreCase(fieldsetid)|| "B01".equalsIgnoreCase(fieldsetid)|| "K01".equalsIgnoreCase(fieldsetid)){
					tablelog = addField(tablelog,fielditemlist.get(i).toString(),false,0,0);
				}
			}
			try {
				dbw.createTable(table);
				if("A01".equalsIgnoreCase(fieldsetid)|| "B01".equalsIgnoreCase(fieldsetid)|| "K01".equalsIgnoreCase(fieldsetid)){
					dbw.createTable(tablelog);
				}
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
			ArrayList sqllist = new ArrayList();
			if("A".equalsIgnoreCase(infor)){
				//ArrayList dblist = userView.getPrivDbList();
				ArrayList dblist = DataDictionary.getDbpreList();
				for(int i=0;i<dblist.size();i++){
					sql.delete(0,sql.length());
					String dbpre = (String)dblist.get(i);
					if("usr".equalsIgnoreCase(dbpre)) {
                        continue;
                    }
					if(Sql_switcher.searchDbServer()==2){//oracle 
						sql.append("create table "+dbpre+fieldsetid+" as select * from Usr"+fieldsetid+" where 1=2");
						if(dbw.isExistTable(dbpre+fieldsetid, false)) {
                            dbw.dropTable(dbpre+fieldsetid);
                        }
					}else{
						sql.append("select * into "+dbpre+fieldsetid+" from Usr"+fieldsetid+" where 1=2");
					}
					sqllist.add(sql.toString());
					sql.delete(0,sql.length());
					if("A01".equalsIgnoreCase(fieldsetid)){
						sql.append("alter table "+dbpre+fieldsetid+" add constraint pk_"+dbpre+fieldsetid+" primary key (a0100)");
					}else{
						sql.append("alter table  "+dbpre+fieldsetid+" add constraint pk_"+dbpre+fieldsetid+" primary key (a0100,i9999)");
					}
					sqllist.add(sql.toString());
				}
			}
			try {
				if(sqllist.size()>0) {
                    dao.batchUpdate(sqllist);
                }
				RecordVo fieldsetvo = new RecordVo("fieldset");
				fieldsetvo.setString("fieldsetid",fieldsetid);
				fieldsetvo = dao.findByPrimaryKey(fieldsetvo);
				fieldsetvo.setString("useflag","1");
				dao.updateValueObject(fieldsetvo);
				RecordVo fielditemvo = new RecordVo("fielditem");
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				fieldset.setUseflag("1");
				fieldset.setCustomdesc(fieldsetvo.getString("customdesc"));//构库时，修改构库子集名称后，更新缓存  wangb 20180408
				if(!"0".equalsIgnoreCase(fieldset.getChangeflag())){
					fielditemlist.add(fieldsetid+"Z0");
					fielditemlist.add(fieldsetid+"Z1");
				}
				for(int i=0;i<fielditemlist.size();i++){
					fielditemvo.setString("fieldsetid",fieldsetid);
					fielditemvo.setString("itemid",fielditemlist.get(i).toString().toUpperCase());
					if(dao.isExistRecordVo(fielditemvo)){
						fielditemvo = dao.findByPrimaryKey(fielditemvo);
						fielditemvo.setString("useflag","1");
						dao.updateValueObject(fielditemvo);
						FieldItem fi = DataDictionary.getFieldItem(fielditemvo.getString("itemid"));
						if(fi!=null){
							fi.setUseflag("1");
						}
					}
				}
				this.writerTablePriv(userView, fieldsetid.toUpperCase());
				this.writerFieldPriv(userView, fielditemlist);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			}finally{
				//DataDictionary.refresh();
			}
		}else{//修改
			try {

				/**
				 * 【58039】
				 * A01Z0为特殊系统字段，必须存在且不能修改，所以库维护界面不显示此指标(参考com.hjsj.hrms.transaction.sys.dbinit.GetFieldItemListTrans搜索bug号 “54374”)
				 * 但是不显示此指标也就代码没法选择到构库列表中，导致后期处理认为是要取消构库。
				 * 此处已选构库指标集合中如果没有A01Z0，手动添加上此指标，防止被取消构库
				 * guodd 2020-02-14
				 * */
				if("A01".equalsIgnoreCase(fieldsetid) && !fielditemlist.contains("A01Z0")){
					fielditemlist.add("A01Z0");
				}

				ArrayList itemlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
				ArrayList olditemlist = new ArrayList();
				for(int i=0;i<itemlist.size();i++){
					FieldItem fielditem=(FieldItem)itemlist.get(i);
					String itemid = fielditem.getItemid();
					if("B0110".equalsIgnoreCase(itemid)||"E01A1".equalsIgnoreCase(itemid)) {
                        continue;
                    }
					olditemlist.add(itemid.toString().toUpperCase());
				}
				this.changeItemList(olditemlist,fielditemlist);//olditemlist保存的为从构库设定到未构库的指标，fielditemlist保存的是新增的需要构库的指标
				/**
				 * 删除构库指标前，判断数据视图是否使用  wangb 20170623 28901
				 * 通过与获取数据视图配置指标参数比较 wangb 33246 20171208 
				 * 存在无法删除构库指标
				 */
				HrSyncBo hsb = new HrSyncBo(con);
				String tr_fields = "";
				if("A".equalsIgnoreCase(infor)){
					tr_fields = hsb.getTextValue(HrSyncBo.FIELDS);//获取人员视图指标
				}else if("B".equalsIgnoreCase(infor)){
					tr_fields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);//获取单位视图指标
				}else if("K".equalsIgnoreCase(infor)){
					tr_fields = hsb.getTextValue(HrSyncBo.POST_FIELDS);//获取岗位视图指标
				}
				for(int i = 0 ; i < olditemlist.size() ; i++){
					if(tr_fields ==null || tr_fields.toUpperCase().indexOf(((String)olditemlist.get(i)).toUpperCase()) == -1)//都转换成大写比较
                    {
                        continue;
                    }
					//删除构库指标 在人员视图中存在 不允许删除  wangb 20171208
					throw new GeneralException("删除的"+(String)olditemlist.get(i)+"字段在系统管理>数据视图中使用，无法删除！");
				}
				/*
				DatabaseMetaData meta = (DatabaseMetaData)this.con.getMetaData();
				ResultSet rs=null;
				try {
					for(int i = 0 ; i < olditemlist.size() ; i++){
						if(infor.equalsIgnoreCase("A")){
							rs=meta.getTables(null, null, "t_hr_view", null);//人员视图表是否存在
							if(rs.next()){//存在
								rs=meta.getColumns(null, null, "t_hr_view", (String)olditemlist.get(i));//字段是否存在
								if(rs.next()){
									throw new GeneralException("删除字段在系统管理>数据视图中使用，无法删除！");
								}
							}
						}else if(infor.equalsIgnoreCase("B")){
							rs=meta.getTables(null, null, "t_org_view", null);//机构视图表是否存在
							if(rs.next()){//存在
								rs=meta.getColumns(null, null, "t_org_view", (String)olditemlist.get(i));//字段是否存在
								if(rs.next()){
									throw new GeneralException("删除字段在系统管理>数据视图中使用，无法删除！");
								}
							}
						}else if(infor.equalsIgnoreCase("K")){
							rs=meta.getTables(null, null, "t_post_view", null);//岗位视图表是否存在
							if(rs.next()){//存在
								rs=meta.getColumns(null, null, "t_post_view", (String)olditemlist.get(i));//字段是否存在
								if(rs.next()){
									throw new GeneralException("删除字段在系统管理>数据视图中使用，无法删除！");
								}
							}
						}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}finally{
					if(rs != null){//没有非空判断 报异常  wangb 29216 20170706
						rs.close();
					}
				}
				*/
				if(olditemlist.size()>0){
					String fields="";
					for(int i = 0 ; i < olditemlist.size() ; i++){
						fields+=","+olditemlist.get(i);
					}
					fields=fields.substring(1);
					
					
				}
				if("A".equalsIgnoreCase(infor)){
					//ArrayList dblist = userView.getPrivDbList();
					ArrayList dblist = DataDictionary.getDbpreList();
					for(int i=0;i<dblist.size();i++){
						table = new Table(dblist.get(i)+fieldsetid);
						for(int j=0;j<olditemlist.size();j++){
							FieldItem item = DataDictionary.getFieldItem(olditemlist.get(j).toString().toUpperCase());
							item.setUseflag("0");
							table.addField(item);
						}
						// guodd 17-05-24 对要构库指标判断 表中实际是否存在此字段，如果存在删掉。因为出现fielditem 和 实际表中字段 不同步情况
						for(int j=0;j<fielditemlist.size();j++){
							if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                                continue;
                            }
							FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
							if(dbw.isExistField(dblist.get(i)+fieldsetid,item.getItemid().toUpperCase(),false)){
								table.addField(item);
							}
						}
						
						if(olditemlist.size()>0 || table.size()>0){
							dbw.dropColumns(table);
							table = new Table(dblist.get(i)+fieldsetid);
						}
						for(int j=0;j<fielditemlist.size();j++){
//							DataDictionary.refresh();
							if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                                continue;
                            }
							FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
							item.setUseflag("1");
							//item.setItemid(item.getItemid().toString().toUpperCase());  //字段转换为大写，小写移库到ORACLE会有问题;
							Field item_o=item.cloneField();
							if("N".equals(item.getItemtype())){
							item_o.setDecimalDigits(item.getDecimalwidth());
							item_o.setLength(item.getItemlength());
							}
							item_o.setName(item.getItemid().toUpperCase());
							table.addField(item_o);
						}
						if(fielditemlist.size()>0) {
                            dbw.addColumns(table);
                        }
						
						//修改主集日志表
						if("A01".equalsIgnoreCase(fieldsetid)&&i==0){
							tablelog = new Table(fieldsetid+"Log");
							for(int j=0;j<olditemlist.size();j++){
								FieldItem item = DataDictionary.getFieldItem(olditemlist.get(j).toString().toUpperCase());
								tablelog.addField(item);
							}
							if(olditemlist.size()>0){
								dbw.dropColumns(tablelog);
								tablelog = new Table(fieldsetid+"Log");
							}
							for(int j=0;j<fielditemlist.size();j++){
								if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                                    continue;
                                }
								FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
								//item.setItemid(item.getItemid().toString().toUpperCase());  //字段转换为大写，小写移库到ORACLE会有问题;
								Field item_o=item.cloneField();
								item_o.setName(item.getItemid().toUpperCase());
								if("N".equals(item.getItemtype())){
								item_o.setDecimalDigits(item.getDecimalwidth());
								item_o.setLength(item.getItemlength());
								}
								tablelog.addField(item_o);
							}
							if(fielditemlist.size()>0) {
                                dbw.addColumns(tablelog);
                            }
						}
					}
					//DataDictionary.refresh();
				}else if("B".equalsIgnoreCase(infor)){
					table = new Table(fieldsetid);
					if("B01".equalsIgnoreCase(fieldsetid)) {
                        tablelog = new Table(fieldsetid+"Log");
                    }
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
						if("B01".equalsIgnoreCase(fieldsetid)) {
                            tablelog.addField(item);
                        }
					}
					
					// guodd 17-05-24 对要构库指标判断 表中实际是否存在此字段，如果存在删掉。因为出现fielditem 和 实际表中字段 不同步情况
					for(int j=0;j<fielditemlist.size();j++){
						if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                            continue;
                        }
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						if(dbw.isExistField(fieldsetid,item.getItemid().toUpperCase(),false)){
							table.addField(item);
						}
					}
					
					if(olditemlist.size()>0 || table.size()>0){
						dbw.dropColumns(table);
						if("B01".equalsIgnoreCase(fieldsetid)) {
                            dbw.dropColumns(tablelog);
                        }
						table = new Table(fieldsetid);
						if("B01".equalsIgnoreCase(fieldsetid)) {
                            tablelog = new Table(fieldsetid+"Log");
                        }
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                            continue;
                        }
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						item_o.setName(item.getItemid().toUpperCase());
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						table.addField(item_o);
						if("B01".equalsIgnoreCase(fieldsetid)) {
                            tablelog.addField(item_o);
                        }
					}
					if(fielditemlist.size()>0){
						dbw.addColumns(table);
						if("B01".equalsIgnoreCase(fieldsetid)) {
                            dbw.addColumns(tablelog);
                        }
					}
				}else if("K".equalsIgnoreCase(infor)){
					table = new Table(fieldsetid);
					if("K01".equalsIgnoreCase(fieldsetid)) {
                        tablelog = new Table(fieldsetid+"Log");
                    }
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
						if("K01".equalsIgnoreCase(fieldsetid)) {
                            tablelog.addField(item);
                        }
					}
					
					// guodd 17-05-24 对要构库指标判断 表中实际是否存在此字段，如果存在删掉。因为出现fielditem 和 实际表中字段 不同步情况
					for(int j=0;j<fielditemlist.size();j++){
						if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                            continue;
                        }
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						if(dbw.isExistField(fieldsetid,item.getItemid().toUpperCase(),false)){
							table.addField(item);
						}
					}
					
					if(olditemlist.size()>0 || table.size()>0){
						dbw.dropColumns(table);
						if("K01".equalsIgnoreCase(fieldsetid)) {
                            dbw.dropColumns(tablelog);
                        }
						table = new Table(fieldsetid);
						if("K01".equalsIgnoreCase(fieldsetid)) {
                            tablelog = new Table(fieldsetid+"Log");
                        }
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                            continue;
                        }
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						item_o.setName(item.getItemid().toUpperCase());
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						table.addField(item_o);
						if("K01".equalsIgnoreCase(fieldsetid)) {
                            tablelog.addField(item_o);
                        }
					}
					if(fielditemlist.size()>0){
						dbw.addColumns(table);
						if("K01".equalsIgnoreCase(fieldsetid)) {
                            dbw.addColumns(tablelog);
                        }
					}
				}else if("H".equalsIgnoreCase(infor)){
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
					}
					
					// guodd 17-05-24 对要构库指标判断 表中实际是否存在此字段，如果存在删掉。因为出现fielditem 和 实际表中字段 不同步情况
					for(int j=0;j<fielditemlist.size();j++){
						if("B0110".equalsIgnoreCase(fielditemlist.get(j).toString())||"E01A1".equalsIgnoreCase(fielditemlist.get(j).toString())) {
                            continue;
                        }
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						if(dbw.isExistField(fieldsetid,item.getItemid().toUpperCase(),false)){
							table.addField(item);
						}
					}
					
					if(olditemlist.size()>0 || table.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						item_o.setName(item.getItemid().toUpperCase());
						table.addField(item_o);
						
					}
					if(fielditemlist.size()>0) {
                        dbw.addColumns(table);
                    }
				}else if("Y".equalsIgnoreCase(infor)){  //党组织
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
					}
					if(olditemlist.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						item_o.setName(item.getItemid().toUpperCase());
						table.addField(item_o);
						
					}
					if(fielditemlist.size()>0) {
                        dbw.addColumns(table);
                    }
				}else if("V".equalsIgnoreCase(infor)){  //团组织
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
					}
					if(olditemlist.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						item_o.setName(item.getItemid().toUpperCase());
						table.addField(item_o);
						
					}
					if(fielditemlist.size()>0) {
                        dbw.addColumns(table);
                    }
				}else if("W".equalsIgnoreCase(infor)){  //工会组织
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString().toUpperCase());
						item.setUseflag("0");
						table.addField(item);
					}
					if(olditemlist.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						//DataDictionary.refresh();
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString().toUpperCase());
						item.setUseflag("1");
						//item.setItemid(item.getItemid().toString().toUpperCase());
						Field item_o=item.cloneField();
						if("N".equals(item.getItemtype())){
						item_o.setDecimalDigits(item.getDecimalwidth());
						item_o.setLength(item.getItemlength());
						}
						item_o.setName(item.getItemid().toUpperCase());
						table.addField(item_o);
						
					}
					if(fielditemlist.size()>0) {
                        dbw.addColumns(table);
                    }
				}
				sql.delete(0,sql.length());
				if(fielditemlist.size()>0){
					sql.append("update fielditem set UseFlag = '1' where fieldsetid='"+fieldsetid+"' and itemid in (");
					for(int j=0;j<fielditemlist.size();j++){
						sql.append("'"+fielditemlist.get(j).toString().toUpperCase()+"',");
					}
					sql = sql.delete(sql.length()-1,sql.length());
					sql.append(")");
					dao.update(sql.toString());
				}
				sql.delete(0,sql.length());
				if(olditemlist.size()>0){
					sql.append("update fielditem set UseFlag = '0' where fieldsetid='"+fieldsetid+"' and itemid in (");
					for(int j=0;j<olditemlist.size();j++){
						sql.append("'"+olditemlist.get(j).toString().toUpperCase()+"',");
					}
					sql = sql.delete(sql.length()-1,sql.length());
					sql.append(")");
					dao.update(sql.toString());
				}
				this.writerFieldPriv(userView, fielditemlist);
			} catch (SQLException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				//DataDictionary.refresh();
			}
		}
	}
	/**
	 * 
	 * @param table 
	 * @param fieldset
	 * @param keyable	是否为primaryKey
	 * @param datatype	如果为0表示动态添加，否则为固定字段
	 * @param length	固定字段长度
	 */
	private Table addField(Table table,String fieldset,boolean keyable,int datatype,int length){
		FieldItem item = DataDictionary.getFieldItem(fieldset);
		Field fielditem = new Field(fieldset,fieldset);
		if(datatype==0){
			item.setUseflag("1");
			item.setKeyable(keyable);
			//item.setItemid(item.getItemid().toString().toUpperCase());  //字段转换为大写，小写移库到ORACLE会有问题;
			if(!table.containsKey(item.getItemid().toLowerCase())){
				Field item_o=item.cloneField();
				if("N".equals(item.getItemtype())){
					item_o.setDecimalDigits(item.getDecimalwidth());
					item_o.setLength(item.getItemlength());
					item_o.setName(item.getItemid().toUpperCase());
					item_o.setDatatype(6);
				}
				table.addField(item_o);
			}
		}else{
			if(keyable){
				fielditem.setNullable(false);
			}
			fielditem.setKeyable(keyable);
			fielditem.setDatatype(datatype);
			if(length!=0) {
                fielditem.setLength(length);
            }
			if(!table.containsKey(fielditem.getName().toLowerCase())) {
                table.addField(fielditem);
            }
		}			
		return table;
	}
	
	/**
	 * 把本次修改将已经构库的指标去掉
	 * @param olditemlist
	 * @param newitemlist
	 */
	private void changeItemList(ArrayList olditemlist,ArrayList newitemlist){
		for(int i=0;i<olditemlist.size();i++){
			String olditem = olditemlist.get(i).toString().trim();
			for(int j=0;j<newitemlist.size();j++){
				String newitem = newitemlist.get(j).toString().trim();
				if(olditem.equalsIgnoreCase(newitem)){
					newitemlist.remove(j);
					olditemlist.remove(i);
					i = i-1;
					j= j-1;
					break;
				}
			}
		}
	}
	
	private void writerTablePriv(UserView userView,String table){
		RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",userView.getDbname()+userView.getUserId());
        vo.setInt("status",userView.getStatus()/*GeneralConstant.ROLE*/);  
        boolean bnew=false;
        try
        {
            /**
             * 未定义权限则增加记录
             */
            try
            {   vo = dao.findByPrimaryKey(vo);
                //dao.findRowSetDynaClassByPrimaryKey(vo);
            	String tablepriv=vo.getString("tablepriv");
            	if(tablepriv!=null&&tablepriv.length()>0){
            		if(tablepriv.indexOf(","+table+"1,")!=-1){
            			tablepriv.replaceAll(","+table+"1,", ","+table+"2,");
            		}else{
            			if(tablepriv.indexOf(","+table+"2,")==-1){
		            		if(tablepriv.endsWith(",")){
		            			vo.setString("tablepriv", tablepriv+table+"2,");
		            		}else{
		            			vo.setString("tablepriv", tablepriv+","+table+"2,");
		            		}
            			}
            		}
            	}else{
            		vo.setString("tablepriv",","+table+"2,");
            	}
                bnew=true;
            }
            catch(GeneralException dd)
            {
            	 vo.setString("tablepriv", ","+table+"2,");//未找到记录
            }
            if(bnew)
            {
           		dao.updateValueObject(vo);
            }
            else {
                dao.addValueObject(vo);
            }
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
	
	private void writerFieldPriv(UserView userView,ArrayList fielditemlist){
		RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",userView.getDbname()+userView.getUserId());
        vo.setInt("status",userView.getStatus()/*GeneralConstant.ROLE*/); 
        boolean bnew=false;
        try
        {
	            /**
	             * 未定义权限则增加记录
	             */
	            try
	            {   vo = dao.findByPrimaryKey(vo);
	            	StringBuffer fieldpriv=new StringBuffer(vo.getString("fieldpriv"));
	            	for(int i=fielditemlist.size()-1;i>=0;i--){
	            		String fielditemid = ((String)fielditemlist.get(i)).toUpperCase();
	            		if(fieldpriv.indexOf(","+fielditemid+"1,")!=-1){
	            			String tmp = fieldpriv.toString().replaceAll(","+fielditemid+"1,", ","+fielditemid+"2,");
	            			fieldpriv=new StringBuffer(tmp);
	            		}else{
	            			if(fieldpriv.indexOf(","+fielditemid+"2,")==-1){
			            		if(fieldpriv.toString().endsWith(",")){
			            			fieldpriv.append(fielditemid+"2,");
			            		}else{
			            			fieldpriv.append(","+fielditemid+"2,");
			            		}
	            			}
	            		}
	            	}
	            	vo.setString("fieldpriv", fieldpriv.toString());
	                bnew=true;
	            }
	            catch(GeneralException dd)
	            {
	            	StringBuffer fieldpriv=new StringBuffer(",");
	            	for(int i=fielditemlist.size()-1;i>=0;i--){
	            		String fielditemid = ((String)fielditemlist.get(i)).toUpperCase();
	            		fieldpriv.append(fielditemid+",");//未找到记录
	            	}
	            	vo.setString("fieldpriv", fieldpriv.toString());
	            }
	            if(bnew)
	            {
	           		dao.updateValueObject(vo);
	            }
	            else {
                    dao.addValueObject(vo);
                }
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
