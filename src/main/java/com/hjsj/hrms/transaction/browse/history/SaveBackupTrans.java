package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SaveBackupTrans extends IBusiness {

	public void execute() throws GeneralException {
		String msg = "ok";
		String creat_date = (String) this.getFormHM().get("create_date");
		String description = (String) this.getFormHM().get("description");
		description = com.hrms.frame.codec.SafeCode.decode(description);
		try {
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("select id from hr_hisdata_list where create_date="
					+ Sql_switcher.dateValue(creat_date));
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				msg="equal";
				return;
			}
			String struct = "";
			String query = "";
			String base = "";
			String HzMenus = "";
			this.frowset  =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
			if(this.frowset.next()){
				ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
				struct =xml.getTextValue("/Emp_HisPoint/Struct");
				query =xml.getTextValue("/Emp_HisPoint/Query");
				base =xml.getTextValue("/Emp_HisPoint/Base");
				HzMenus =xml.getTextValue("/Emp_HisPoint/HzMenus");
			}
			String str_value = "";
			if(struct.length()==0){
				sql.setLength(0);
				sql.append("select str_value from constant where upper(constant)='EMP_HISDATA_STRUCT'");
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) {
					str_value = Sql_switcher.readMemo(frowset, "str_value");
				}
			}else{
				str_value=struct;
			}
			if(!str_value.endsWith(","))
				str_value=str_value+",";
			String[] str_values=str_value.split(",");
			for(int i=0;i<str_values.length;i++){
				if(str_values[i].length()!=5)
					continue;
				FieldItem fielditem = DataDictionary.getFieldItem(str_values[i].toLowerCase());
				if (fielditem == null||!"1".equals(fielditem.getUseflag()))
					str_value=str_value.replaceAll(str_values[i]+",", "");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(sdf.parse(creat_date).getTime());
			String id = getMaxId();
			ArrayList paralist = new ArrayList();
			sql.setLength(0);
			sql.append("insert into hr_hisdata_list(Id,create_date,description,snap_fields)values(?,?,?,?)");
			paralist.add(new Integer(id));
			paralist.add(date);
			paralist.add(description);
			paralist.add(str_value);
			
			dao.insert(sql.toString(), paralist);

			String year = getYear(creat_date);
			String month = getMonth(creat_date);
			sql.setLength(0);
			sql.append("select * from hr_emp_hisdata where 1=2");
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			ArrayList dropcolumssql = new ArrayList();
			DbWizard dbw = new DbWizard(this.frameconn);
			Table table = new Table("hr_emp_hisdata");
			//指标集合  fm<key:fieldsetid ,value:ArrayList<itemid>>
		    HashMap fm = new HashMap();
		    //主要指标
		    String maincolumn = "";
		    //人员唯一标识
		    String uniqueitem = ((String)this.getFormHM().get("uniqueitem")).toUpperCase();
		    //是否存在唯一指标
		    boolean isExsitUniqueItem = false;
		    //解析指标，按照fieldset 分类   
			for (int i = 1; i <= size; i++) {
				String itemid=rsmd.getColumnName(i);
				if(itemid.equalsIgnoreCase(uniqueitem))
					isExsitUniqueItem = true;
				/*判断长度*/
				FieldItem item=null;
				if(!"id".equalsIgnoreCase(itemid)&&!"nbase".equalsIgnoreCase(itemid)){
					item = DataDictionary.getFieldItem(itemid.toLowerCase());
					//zxj 20151221 需要进一步判断是否为人员主集或子集指标
					if (item != null && !item.getFieldsetid().toUpperCase().startsWith("A"))
					    item = null;
				}
				
				int itemLength = rsmd.getColumnDisplaySize(i);
				if(item!=null && "A".equals(item.getItemtype())){
					if(itemLength<item.getItemlength()){
					    if (!"A0100".equalsIgnoreCase(item.getItemid())){
    						Field item_o=item.cloneField();
    						table.addField(item_o);		
					    }
					}	
				}
				
				if("id".equalsIgnoreCase(itemid)||"nbase".equalsIgnoreCase(itemid)
						
						|| "b0110".equalsIgnoreCase(itemid)
						|| "e0122".equalsIgnoreCase(itemid)
						|| "e01a1".equalsIgnoreCase(itemid)
						|| "a0101".equalsIgnoreCase(itemid)
						|| "a0100".equalsIgnoreCase(itemid)
						|| "a0000".equalsIgnoreCase(itemid)){
					maincolumn+=","+itemid;
					continue;
				}
				if(item==null||!"1".equals(item.getUseflag())){
					dropcolumssql.add("alter table hr_emp_hisdata  drop column "+itemid);
					continue;
				}
				String setid = item.getFieldsetid();
				if(fm.containsKey(setid)){
					ArrayList fl = (ArrayList)fm.get(setid);
					fl.add(itemid);
				}else{
					ArrayList fl = new ArrayList();
					fl.add(itemid);	
				    fm.put(setid, fl);
				}	
			}
			if(table.getCount()!=0)
				dbw.alterColumns(table);
			deleteColums(dropcolumssql);
			
			FieldItem unifi = DataDictionary.getFieldItem(uniqueitem);
			//如果表中不存在唯一标识，则添加进去
			if(!isExsitUniqueItem && unifi!=null && !"0".equals(unifi.getUseflag())){
				if(fm.containsKey(unifi.getFieldsetid())){
					ArrayList fl = (ArrayList)fm.get(unifi.getFieldsetid());
					fl.add(uniqueitem);
				}else{
					ArrayList fl = new ArrayList();
					fl.add(uniqueitem);	
				    fm.put(unifi.getFieldsetid(), fl);
				}
				addUniqueItem("");	
			}
				
			if(base.length()==0){
				sql.setLength(0);
				sql.append("select str_value from constant where upper(constant)='EMP_HISDATA_BASE'");
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) {
					str_value = Sql_switcher.readMemo(this.frowset, "str_value");
				}else
					str_value="";
			}else{
				str_value=base;
			}
			String[] fields = str_value.split(",");
			ArrayList dblist = new ArrayList();
			for (int i=0;i<fields.length;i++) {
				String pre = fields[i];
				if(pre.length()==3)
					dblist.add(pre);
			}
			if(dblist.size()==0)
				dblist.add("Usr");
			size = dblist.size();
			for (int i = 0; i < size; i++) {
				String nbase = (String) dblist.get(i);
				sql.setLength(0);
				
				//先插入关键字段
				sql.append(" insert into hr_emp_hisdata("+maincolumn.substring(1)+")");
				String valuecolumn = maincolumn.toString().toUpperCase();
				valuecolumn = valuecolumn.replaceAll(",ID", "," + id);
				valuecolumn = valuecolumn.replaceAll(",NBASE", ",'" + nbase+ "'");
				sql.append(" select "+valuecolumn.substring(1)+" from "+nbase+"A01 ");
				dao.update(sql.toString());
				
				//逐个子集字段更新
				for(Iterator ite = fm.keySet().iterator();ite.hasNext();){
					String fieldsetid = ite.next().toString();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
			        String changeflag = fieldset.getChangeflag();
			        ArrayList fl = (ArrayList)fm.get(fieldsetid);
			        String sqlstr = createSql(id,fieldsetid,changeflag,nbase,fl,year,month);
					dao.update(sqlstr);
				}
			}
			if(HzMenus.length()>8)
				this.countHzMenus(dao, id, dblist, HzMenus, year, month);
			
		} catch (SQLException e) {
			msg = "error";
			e.printStackTrace();
		} catch (ParseException e) {
			msg = "error";
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			this.getFormHM().put("msg", msg);
		}
	}

	
	private String createSql(String hisid,String fieldsetid,String changeflag,String nbase,ArrayList fl,String year,String month){
		
		 StringBuffer sql = new StringBuffer();
		 String tablename = nbase+fieldsetid;
		 boolean mainSetFlag = "A01".equalsIgnoreCase(fieldsetid)?true:false;
		  switch (Sql_switcher.searchDbServer()) { 
			  case Constant.MSSQL: {
				    sql.append(" update hr_emp_hisdata set ");
					for(int k=0;k<fl.size();k++){
						String itemid = fl.get(k).toString();
						sql.append(itemid+"=b."+itemid+" ,");
					}
					sql.deleteCharAt(sql.length()-1);
					sql.append(" from "+tablename+" b where hr_emp_hisdata.a0100=b.a0100 and hr_emp_hisdata.nbase='"+nbase+"' and hr_emp_hisdata.ID="+hisid);
			      break; 
			  } 
			  case Constant.ORACEL: {
				  sql.append(" update hr_emp_hisdata set (");
				  String itemstr = "";
				  for(int k=0;k<fl.size();k++){
						String itemid = fl.get(k).toString();
						//sql.append(itemid+" ,");
						itemstr+=itemid+" ,";
				  }
				  itemstr = itemstr.substring(0, itemstr.length()-1);
				  //sql.deleteCharAt(sql.length()-1);
				  sql.append(itemstr+")=(select "+itemstr); 
				  sql.append(" from "+tablename+" b where hr_emp_hisdata.a0100=b.a0100 ");
			      break; 
			  }
		  }
		 
		  if(!mainSetFlag){
				if ("2".equals(changeflag)) {
					sql.append(" and b"
								+ ".i9999=(select max(i9999) from Usr"
								+ fieldsetid + " t"+ " where t"
								+ "." + fieldsetid + "Z0=" + year
								+ " and t" +"." + fieldsetid
								+ "Z1=(select max(" + fieldsetid
								+ "Z1) from " +nbase+ fieldsetid + " tt"  //zhangcq 2016-5-19  快照人员库取值
								+" where tt"+ "."
								+ fieldsetid + "Z0=" + year
								+ "  and tt"
								+ ".a0100=b.a0100) and t"
								+ ".a0100=b.a0100 group by t.a0100) ");

				} else if ("1".equals(changeflag)) {
					sql.append(" and b"
								//+ tablename
								+ ".i9999=(select max(i9999) from "
								+ tablename + " t" + " where t"
								+ "." + fieldsetid + "Z0=" + month
								+ " and t" + "." + fieldsetid
								+ "Z1=(select max(" + fieldsetid
								+ "Z1) from " +nbase+ fieldsetid + " tt"
								+ " where tt" + "."
								+ fieldsetid + "Z0=" + month
								+ " and tt"
								+ ".a0100=b.a0100) and t"
								+ ".a0100=b.a0100 group by t.a0100) ");

				} else {
					sql.append(" and b"
								//+ tablename
								+ ".i9999=(select max(i9999) from "+nbase
								+ fieldsetid + " t" +" where t"
								+ ".a0100=b.a0100) ");
				}
			}
		if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			sql.append(") where nbase='"+nbase+"' and ID="+hisid);
		return sql.toString();
	}
	
	
	
	private String getMaxId() throws GeneralException {
		int nid = 1;
		StringBuffer sql = new StringBuffer(
				"select max(id)+1 as nmax from hr_hisdata_list");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				nid = this.frowset.getInt("nmax");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return String.valueOf(nid);
	}

	private String getYear(String date) {
		date = date.substring(0, 4) + "-01-01";
		date = Sql_switcher.dateValue(date);
		return date;
	}

	private String getMonth(String date) {
		date = date.substring(0, 7) + "-01";
		date = Sql_switcher.dateValue(date);
		return date;
	}
	
	private void deleteColums(ArrayList dropcolumssql){
		try{
		if(dropcolumssql.size()>0){
			ContentDAO dao =new ContentDAO(this.frameconn);
			for(int i=0;i<dropcolumssql.size();i++)
				dao.update((String)dropcolumssql.get(i));
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String addUniqueItem(String haveItem) throws GeneralException, SQLException{
		String uniqueitem = ((String)this.getFormHM().get("uniqueitem")).toUpperCase();
		if(haveItem.indexOf(uniqueitem)==-1){
			Connection conn=AdminDb.getConnection();
			try{
				DbWizard dbw = new DbWizard(conn);
				Table table = new Table("hr_emp_hisdata");
					FieldItem item=DataDictionary.getFieldItem(uniqueitem);
					if(item!=null){
						table.addField(item);
					}
				if(table.getCount()>0){
					dbw.addColumns(table);
					haveItem = haveItem+","+uniqueitem;
				}
			}finally{
				if(conn!=null)
					conn.close();
			}
		}
		return haveItem;
	}
	
	private void countHzMenus(ContentDAO dao,String id,ArrayList dblist,String HzMenus,String year,String month) throws SQLException{
		String[] HzMenuss = HzMenus.split(",");
		StringBuffer sql = new StringBuffer();
		sql.append("update hr_emp_hisdata set ");
		for(int i=0;i<HzMenuss.length;i++){
			String menu = HzMenuss[i];
			String[] menus = menu.split(":");
			if(menus.length==2){
				String itemid = menus[0];
				String type = menus[1];
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null){
					String fieldsetid = fielditem.getFieldsetid();
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					String changeflag = fieldset.getChangeflag();
					if ("2".equals(changeflag)) {
						if(i!=0)
							sql.append(",");
						sql.append(itemid+"=(select "+type+"("+itemid+") from Usr"
								+ fieldsetid
								+ " t"+i+" where t"+i
								+ "." + fieldsetid + "Z0=" + year
								+ " and t"+i
								+ ".a0100=hr_emp_hisdata.a0100)");

					} else if ("1".equals(changeflag)) {
						if(i!=0)
							sql.append(",");
						sql.append(itemid+"=(select "+type+"("+itemid+") from Usr"
								+ fieldsetid
								+ " t"+i+" where t"+i
								+ "." + fieldsetid + "Z0=" + month
								+ " and t"+i
								+ ".a0100=hr_emp_hisdata.a0100)");
					}
				}
			}
		}
		sql.append(" where id="+id+" and nbase='Usr'");
		for(int i=0;i<dblist.size();i++){
			String dbname = (String)dblist.get(i);
			String sqlstr = sql.toString().replaceAll("Usr", dbname);
			dao.update(sqlstr);
		}
	}
}
