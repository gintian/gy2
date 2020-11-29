package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SaveSnapshotDataTrans.java</p>
 * <p>Description>:SaveSnapshotDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 19, 2010 5:18:13 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SaveSnapshotDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		String mess=null;
		String snap_v=(String) this.getFormHM().get("snap_v");
		snap_v=snap_v==null||snap_v.length()<1?"":snap_v;
		String norm_v=(String) this.getFormHM().get("norm_v");
		norm_v=norm_v==null||norm_v.length()<1?"":norm_v;
		String chk_v=(String) this.getFormHM().get("chk_v");
		chk_v=chk_v==null||chk_v.length()<1?"":chk_v;
		
		List sqlList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs=null;
		try {
			
			/*rs = dao.search("select Constant from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
			if(rs.next())
				sqlList.add("update Constant set str_value='"+snap_v+"' where Upper(Constant)='EMP_HISDATA_STRUCT'");
			else
				sqlList.add("insert into constant (constant,describe,str_value) values('EMP_HISDATA_STRUCT','人员时点信息表结构','"+snap_v+"')");
			
			rs=dao.search("select Constant from Constant where Upper(Constant)='EMP_HISDATA_QUERY'");
			if(rs.next())
				sqlList.add("update Constant set str_value='"+norm_v+"' where Upper(Constant)='EMP_HISDATA_QUERY'");
			else
				sqlList.add("insert into constant (constant,describe,str_value) values('EMP_HISDATA_QUERY','快速查询指标列表','"+norm_v+"')");
			
			rs=dao.search("select Constant from Constant where Upper(Constant)='EMP_HISDATA_BASE'");
			if(rs.next())
				sqlList.add("update Constant set str_value='"+chk_v+"' where Upper(Constant)='EMP_HISDATA_BASE'");
			else
				sqlList.add("insert into constant (constant,describe,str_value) values('EMP_HISDATA_BASE','人员时点应用库','"+chk_v+"')");
			dao.batchUpdate(sqlList);*/
			//构库
			upWizard(snap_v);
			
			ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
			String HzMenus =xml.getTextValue("/Emp_HisPoint/HzMenus").toUpperCase();
			xml.setTextValue("/Emp_HisPoint/Base", chk_v);
			xml.setTextValue("/Emp_HisPoint/Struct", snap_v);
			xml.setTextValue("/Emp_HisPoint/Query", norm_v);
			String[] menus=HzMenus.split(",");
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<menus.length;i++){
				String menu=menus[i];
				String[] tmp=menu.split(":");
				if(tmp.length==2&&tmp[0].length()==5){
					if(snap_v.toUpperCase().indexOf(tmp[0])!=-1){
						sb.append(menu+",");
					}
				}
			}
			xml.setTextValue("/Emp_HisPoint/HzMenus", sb.toString());
			xml.saveStrValue();
			
			mess="success";
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		this.getFormHM().put("mess", mess);
	}

	/**
	 * 构库
	 * @param snap_v
	 * @throws Exception
	 * @author liwc
	 */
	private void upWizard(String snap_v) throws Exception{
		String uniqueitem=(String) this.getFormHM().get("uniqueitem");
		uniqueitem=uniqueitem==null||uniqueitem.length()<1?"":uniqueitem;
		Connection conn=AdminDb.getConnection();
		try{
			ContentDAO dao=new ContentDAO(conn);
			DbWizard dbw = new DbWizard(this.getFrameconn());
			Table table = new Table("hr_emp_hisdata");
			Table droptable = new Table("hr_emp_hisdata");
			String[] fields=snap_v.split(",");
			String sql="select * from hr_emp_hisdata where 1=2";
			this.frowset = dao.search(sql.toString());
			ResultSetMetaData rsmd = this.frowset.getMetaData();
			int size = rsmd.getColumnCount();
			StringBuffer columnstr = new StringBuffer();
			ArrayList clearfields = new ArrayList();
			for (int i = 1; i <= size; i++) {
				boolean flag=true;
				String field = rsmd.getColumnName(i);
				if("Id".equalsIgnoreCase(field))
					continue;
				if("Nbase".equalsIgnoreCase(field))
					continue;
				if("A0100".equalsIgnoreCase(field))
					continue;
				if("A0000".equalsIgnoreCase(field))
					continue;
				if("B0110".equalsIgnoreCase(field))
					continue;
				if("E0122".equalsIgnoreCase(field))
					continue;
				if("E01A1".equalsIgnoreCase(field))
					continue;
				if("A0101".equalsIgnoreCase(field))
					continue;
				if(field.equalsIgnoreCase(uniqueitem))
					continue;
				for (int j = 0; j < fields.length; j++) {
					if(field.equalsIgnoreCase(fields[j])){
						flag=false;
						break;
					}
				}
				if(flag){
					clearfields.add(field);
					FieldItem item=DataDictionary.getFieldItem(field);
					if(item==null)
						continue;
					droptable.addField(item);
				}
			}
			if(droptable.getCount()>0){
				dbw.dropColumns(droptable);
				clearsnapfields(clearfields,dao);
			}
			
			for (int i = 0; i < fields.length; i++) {
				FieldItem item=DataDictionary.getFieldItem(fields[i]);
				if(item!=null){
					boolean b=dbw.isExistField("hr_emp_hisdata", fields[i],false);
					if(!b)
						table.addField(item);
				}
			}
			if(table.getCount()>0)
				dbw.addColumns(table);
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
	/*
	//获取系统参数 唯一性指标设置
	private String UniqueItem(){
		String field="";
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		if(chkvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 uniquenessvalid="";
		 String chkcheck="",uniquenesscheck="";

		 if(chkvalid.equalsIgnoreCase("0")||chkvalid.equalsIgnoreCase("")){
			 chkcheck="";
		 }
		 else{
			 chkcheck="checked";
		 }
		 if(uniquenessvalid.equalsIgnoreCase("0")||uniquenessvalid.equalsIgnoreCase("")){
			 uniquenesscheck="";
		 }
		 else{
			 uniquenesscheck="checked";
		 }
		StringBuffer setdb=new StringBuffer();
		if(chk==null)
			 chk="";
		if(onlyname==null)
			 onlyname = "";
		if(chk.length()>0&&chkcheck.equals("checked")){
			field=chk;
		}else if(onlyname.length()>0&&uniquenesscheck.equals("checked")){
			field=onlyname;
		}else{
			field="A0100";
		}
		return field;
	}*/
	
	/**
	 * 异步已备份的人员时点的库结构指标，如果后期删掉以前备份时点时有的也删掉
	 */
	private void clearsnapfields(ArrayList clearfields,ContentDAO dao) throws SQLException{
		String sql = "select id,snap_fields from hr_hisdata_list";
		this.frowset = dao.search(sql);
		while(this.frowset.next()){
			boolean flag = false;
			int id = this.frowset.getInt("id");
			String snap_fields = (com.hrms.hjsj.utils.Sql_switcher.readMemo(this.frowset, "snap_fields")).toLowerCase();
			if(!snap_fields.endsWith(","))
				snap_fields+=",";
			for(int i=0;i<clearfields.size();i++){
				String field = ((String)clearfields.get(i)).toLowerCase();
				if(snap_fields.indexOf(field)!=-1){
					snap_fields= snap_fields.replace(field+",", "");
					flag = true;
				}
			}
			if(flag){
				if(!snap_fields.endsWith(","))
					snap_fields+=",";
				sql = "update hr_hisdata_list set snap_fields=? where id=?";
				ArrayList values = new ArrayList();
				values.add(snap_fields);
				values.add(Integer.valueOf(id));
				dao.update(sql, values);
			}
		}
		
	}
}
