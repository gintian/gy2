package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.businessobject.sys.dbinit.FormationBase;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 *<p>Title:FormationBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 10, 2008:9:30:13 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class FormationBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String fieldsetid = (String)this.getFormHM().get("tablename");//子集A01,B01...
			ArrayList fielditemlist = (ArrayList)this.getFormHM().get("code_fields");//选择的所有子集
			String type = (String)this.getFormHM().get("type");//判断指标是否构库
			String infor=(String)this.getFormHM().get("infor");//表示某一个指标 A,B,K
			//this.frameconn.setAutoCommit(false);
			ContentDAO dao = new ContentDAO(this.frameconn);
			FormationBase base = new FormationBase(dao, this.frameconn);
			base.formation(type,infor,fieldsetid,fielditemlist,userView);
			//this.frameconn.commit();
			DBMetaModel dbm = new DBMetaModel(this.frameconn);
			//dbm.reloadTableModel();
			if("A".equalsIgnoreCase(infor)){
				//ArrayList dblist = userView.getPrivDbList();
				ArrayList dblist = DataDictionary.getDbpreList();
				for(int i=0;i<dblist.size();i++){
					String dbpre = (String)dblist.get(i);
					dbm.reloadTableModel(dbpre+fieldsetid);
				}
				// 刷新系统参数中的子集信息
				SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.frameconn);
				infoxml.reOrederSet();
			}else{
				dbm.reloadTableModel(fieldsetid);
			}
/**
 * xus 
 * 删除"查询设置"中不存在的库结构
 * 17/02/09
 *
 */
 			String constant="";
			String constantItems="";
			StringBuffer strsql=new StringBuffer();
			StringBuffer str=new StringBuffer();
			List paramList=new ArrayList();
			List newfielditemlist=new ArrayList();
			List fielditemlist1=new ArrayList();
			//根据A、B、K判断constant的字段
			if("A".equals(infor))
				constant="SS_QUERYTEMPLATE";
			else if("B".equals(infor))
				constant="SS_BQUERYTEMPLATE";
			else if("K".equals(infor))
				constant="SS_KQUERYTEMPLATE";
			else if("Y".equals(infor))
				constant="SS_YQUERYTEMPLATE";
			else if("V".equals(infor))
				constant="SS_VQUERYTEMPLATE";
			else if("W".equals(infor))
				constant="SS_WQUERYTEMPLATE";
			else if("H".equals(infor))
				constant="SS_HQUERYTEMPLATE";
			strsql.append("select STR_VALUE from constant where constant='");
			strsql.append(constant);
			strsql.append("'");
			RowSet rs=dao.search(strsql.toString());
			while(rs.next()){
				constantItems=rs.getString("STR_VALUE");
			}
			//查询fielditem中已构库的itemid
			strsql.delete(0, strsql.length());
			strsql.append("select itemid from fielditem  where useflag ='1' and fieldsetid like '");
			strsql.append(infor);
			strsql.append("%'");
			rs=dao.search(strsql.toString());
			while(rs.next()){
				fielditemlist1.add(rs.getString("itemid"));
			}
			if(constantItems != null && !"".equalsIgnoreCase(constantItems)){
				String[] items= constantItems.split(",");
				for(int i=0;i<items.length;i++){
					if(fielditemlist1.contains(items[i]))
						newfielditemlist.add(items[i]);
				}
			}
			if(newfielditemlist!=null)
			{
				for(int i=0;i<newfielditemlist.size();i++)
				{
					str.append(newfielditemlist.get(i).toString().toUpperCase());       //List中的对象转换成字符串
					if(i<newfielditemlist.size()-1)
						str.append(",");
				}
			}
			strsql.delete(0, strsql.length());
		    strsql.append("delete from constant where constant='");
		    strsql.append(constant);
		    strsql.append("'");
		    dao.delete(strsql.toString(),paramList); 
		    strsql.delete(0, strsql.length());
			strsql.append("insert into constant(constant,type,str_value,Describe) values(?,?,?,?)");
		    paramList.add(constant);
		    paramList.add("1");
		    paramList.add(str.toString());
		    paramList.add("查询模板");
		    dao.insert(strsql.toString(),paramList);
		}catch(Exception e){
			/*try {
				this.frameconn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			e.printStackTrace();
			throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(e);
		}finally{
			/*try {
				this.frameconn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		/*StringBuffer sql = new StringBuffer();
		Table table =null;
		DbWizard dbw = new DbWizard(this.frameconn);
		ContentDAO dao = new ContentDAO(this.frameconn);
		if(type.equalsIgnoreCase("0")){//构库
			if(infor.equalsIgnoreCase("A")){
				table = new Table("Usr"+fieldsetid);
				if(fieldsetid.equalsIgnoreCase("A01")){//?	构建主集A01
					table = addField(table,"A0000",false,DataType.INT,0);
					table = addField(table,"A0100",true,DataType.STRING,8);
					table = addField(table,"B0100",false,DataType.STRING,30);
					table = addField(table,"E0122",false,DataType.STRING,30);
					table = addField(table,"E01A1",false,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
					table = addField(table,"UserName",false,DataType.STRING,50);
					table = addField(table,"UserPassword ",false,DataType.STRING,50);
					table = addField(table,"Groups",false,DataType.STRING,50);
				}else{//	?构建子集-（UsrA02—Axx）
					table = addField(table,"A0100",true,DataType.STRING,8);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
					table = addField(table,"sealflag",false,DataType.INT,0);
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if(fieldset.getChangeflag().equalsIgnoreCase("1")){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
					
				}
			}else if(infor.equalsIgnoreCase("B")){//?	主集（B01）
				table = new Table(fieldsetid);
				if(fieldsetid.equalsIgnoreCase("B01")){
					table = addField(table,"B0110",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
				}else{//?	子集(B02—Bxx)
					table = addField(table,"B0110",true,DataType.STRING,8);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if(fieldset.getChangeflag().equalsIgnoreCase("1")){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}else if(infor.equalsIgnoreCase("K")){
				table = new Table(fieldsetid);
				if(fieldsetid.equalsIgnoreCase("K01")){//?	主集－K01
					table = addField(table,"E01A1",true,DataType.STRING,30);
					table = addField(table,"State",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"ModTime1",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
				}else{//?	子集-(K02—Kxx)
					table = addField(table,"E01A1",true,DataType.STRING,8);
					table = addField(table,"I9999",true,DataType.INT,0);
					table = addField(table,"State",false,DataType.STRING,1);
					table = addField(table,"Id",false,DataType.STRING,10);
					table = addField(table,"CreateTime",false,DataType.DATE,10);
					table = addField(table,"ModTime",false,DataType.DATE,10);
					table = addField(table,"CreateUserName",false,DataType.STRING,50);
					table = addField(table,"ModUserName ",false,DataType.STRING,50);
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if(fieldset.getChangeflag().equalsIgnoreCase("1")){
						table = addField(table,fieldsetid+"Z0",false,DataType.DATE,10);
						table = addField(table,fieldsetid+"Z1",false,DataType.INT,0);
					}
				}
			}
			
			for(int i=0;i<fielditemlist.size();i++){
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				if(fieldset.getChangeflag().equalsIgnoreCase("1")){
					if(fielditemlist.get(i).toString().equalsIgnoreCase(fieldsetid+"Z0")||fielditemlist.get(i).toString().equalsIgnoreCase(fieldsetid+"Z1"))
						continue;
				}
				table = addField(table,fielditemlist.get(i).toString(),false,0,0);
			}
			dbw.createTable(table);
			ArrayList sqllist = new ArrayList();
			if(infor.equalsIgnoreCase("A")){
				ArrayList dblist = userView.getPrivDbList();
				for(int i=0;i<dblist.size();i++){
					sql.delete(0,sql.length());
					String dbpre = (String)dblist.get(i);
					if(dbpre.equalsIgnoreCase("usr"))
						continue;
					sql.append("select * into "+dbpre+fieldsetid+" from Usr"+fieldsetid+" where 1=2");
					sqllist.add(sql.toString());
				}
			}
			try {
				if(sqllist.size()>0)
					dao.batchUpdate(sqllist);
				RecordVo fieldsetvo = new RecordVo("fieldset");
				fieldsetvo.setString("fieldsetid",fieldsetid);
				fieldsetvo = dao.findByPrimaryKey(fieldsetvo);
				fieldsetvo.setString("useflag","1");
				dao.updateValueObject(fieldsetvo);
				RecordVo fielditemvo = new RecordVo("fielditem");
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				if(fieldset.getChangeflag().equalsIgnoreCase("1")){
					fielditemlist.add(fieldsetid+"Z0");
					fielditemlist.add(fieldsetid+"Z1");
				}
				for(int i=0;i<fielditemlist.size();i++){
					fielditemvo.setString("fieldsetid",fieldsetid);
					fielditemvo.setString("itemid",fielditemlist.get(i).toString());
					fielditemvo = dao.findByPrimaryKey(fielditemvo);
					fielditemvo.setString("useflag","1");
					dao.updateValueObject(fielditemvo);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DataDictionary.refresh();
			}
		}else{//修改
			try {
				ArrayList itemlist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
				ArrayList olditemlist = new ArrayList();
				for(int i=0;i<itemlist.size();i++){
					FieldItem fielditem=(FieldItem)itemlist.get(i);
					olditemlist.add(fielditem.getItemid().toString().toUpperCase());
				}
				this.changeItemList(olditemlist,fielditemlist);//olditemlist保存的为从构库设定到未构库的指标，fielditemlist保存的是新增的需要构库的指标
				if(infor.equalsIgnoreCase("A")){
					ArrayList dblist = userView.getPrivDbList();
					for(int i=0;i<dblist.size();i++){
						table = new Table(dblist.get(i)+fieldsetid);
						for(int j=0;j<olditemlist.size();j++){
							FieldItem item = DataDictionary.getFieldItem(olditemlist.get(j).toString());
							table.addField(item);
						}
						if(olditemlist.size()>0){
							dbw.dropColumns(table);
							table = new Table(dblist.get(i)+fieldsetid);
						}
						for(int j=0;j<fielditemlist.size();j++){
							FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString());
							table.addField(item);
						}
						if(fielditemlist.size()>0)
							dbw.addColumns(table);
					}
				}else if(infor.equalsIgnoreCase("B")){
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString());
						table.addField(item);
					}
					if(olditemlist.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString());
						table.addField(item);
					}
					if(fielditemlist.size()>0)
						dbw.addColumns(table);
				}else if(infor.equalsIgnoreCase("K")){
					table = new Table(fieldsetid);
					for(int i=0;i<olditemlist.size();i++){
						FieldItem item = DataDictionary.getFieldItem(olditemlist.get(i).toString());
						table.addField(item);
					}
					if(olditemlist.size()>0){
						dbw.dropColumns(table);
						table = new Table(fieldsetid);
					}
					for(int j=0;j<fielditemlist.size();j++){
						FieldItem item = DataDictionary.getFieldItem(fielditemlist.get(j).toString());
						table.addField(item);
					}
					if(fielditemlist.size()>0)
						dbw.addColumns(table);
				}
				sql.delete(0,sql.length());
				if(fielditemlist.size()>0){
					sql.append("update fielditem set UseFlag = '1' where fieldsetid='"+fieldsetid+"' and itemid in (");
					for(int j=0;j<fielditemlist.size();j++){
						sql.append("'"+fielditemlist.get(j)+"',");
					}
					sql = sql.delete(sql.length()-1,sql.length());
					sql.append(")");
					dao.update(sql.toString());
				}
				sql.delete(0,sql.length());
				if(olditemlist.size()>0){
					sql.append("update fielditem set UseFlag = '0' where fieldsetid='"+fieldsetid+"' and itemid in (");
					for(int j=0;j<olditemlist.size();j++){
						sql.append("'"+olditemlist.get(j)+"',");
					}
					sql = sql.delete(sql.length()-1,sql.length());
					sql.append(")");
					dao.update(sql.toString());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DataDictionary.refresh();
			}
		}*/
	}
	/**
	 * 
	 * @param table 
	 * @param fieldset
	 * @param keyable	是否为primaryKey
	 * @param datatype	如果为0表示动态添加，否则为固定字段
	 * @param length	固定字段长度
	 */
	/*private Table addField(Table table,String fieldset,boolean keyable,int datatype,int length){
		FieldItem item = DataDictionary.getFieldItem(fieldset);
		Field fielditem = new Field(fieldset,fieldset);
		if(datatype==0){
			item.setKeyable(keyable);
			table.addField(item);
		}else{
			if(keyable){
				fielditem.setNullable(false);
			}
			fielditem.setKeyable(keyable);
			fielditem.setDatatype(datatype);
			if(length!=0)
				fielditem.setLength(length);
			table.addField(fielditem);
		}			
		return table;
	}
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
	}*/
}
