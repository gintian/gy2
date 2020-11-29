package com.hjsj.hrms.businessobject.dtgh;

import com.hjsj.hrms.businessobject.duty.MoveSdutyBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CodeUtilBo {

	public static void saveCode(Connection conn,String first,String codesetid,String parentid,String codeitemid,String codeitemdesc)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		java.sql.Date start_date=null;
		java.sql.Date end_date=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			start_date = new java.sql.Date(sdf.parse(sdf.format(new Date())).getTime());
			end_date = new java.sql.Date(sdf.parse("9999-12-31").getTime());
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		StringBuffer sqlstr = new StringBuffer();
		ArrayList sqlvalue = new ArrayList();
		sqlstr.append("select * from codeitem where codesetid=");
		sqlstr.append("?");
		sqlstr.append(" and codeitemid=");
		sqlstr.append("?");
		sqlstr.append("");
		sqlvalue.add(codesetid);
		sqlvalue.add(codeitemid);
		ResultSet rs = null;
		try {
			rs = dao.search(sqlstr.toString(),sqlvalue);
			if (rs.next()) {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory
								.getProperty("label.posbusiness.adderrors"),
						"", ""));
			} else {
				sqlstr.delete(0, sqlstr.length());
				sqlvalue.clear();
				sqlstr.append("select * from codeitem where codesetid=");
				sqlstr.append("?");
				sqlstr.append(" and codeitemdesc=");
				sqlstr.append("? and parentid=");
				sqlstr.append("?");
				sqlvalue.add(codesetid);
				sqlvalue.add(codeitemdesc);
				sqlvalue.add(parentid);
				rs = dao.search(sqlstr.toString(),sqlvalue);
				if (rs.next()) {
					throw GeneralExceptionHandler
							.Handle(new GeneralException(
									"",
									ResourceFactory
											.getProperty("label.posbusiness.adderrorsname"),
									"", ""));
				} else {
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,start_date,end_date,a0000,invalid)values(?,?,?,?,?,?,?,?,?,?)");
					sqlvalue.clear();
					sqlvalue.add(codesetid);
					sqlvalue.add(codeitemid);
					sqlvalue.add(codeitemdesc);
					String tmpparentid=parentid;
					if (!(parentid != null && parentid.trim().length() > 0)) {
                        tmpparentid=codeitemid;
                    }
					sqlvalue.add(tmpparentid);
					sqlvalue.add(codeitemid);
					String codeflag = SystemConfig.getPropertyValue("dev_flag");
					if (codeflag == null || "0".equals(codeflag)
							|| "".equals(codeflag)) {
						sqlvalue.add("0");
					} else {
						sqlvalue.add("1");
					}
						sqlvalue.add(start_date);
						sqlvalue.add(end_date);
						String ps_c_code = ConstantParamter.getRealConstantVo("PS_C_CODE").getString("str_value");
						if(ps_c_code.equals(codesetid)){
							MoveSdutyBo msb = new MoveSdutyBo(dao,(RowSet)rs);
							sqlvalue.add(msb.getaddA0000(parentid, codesetid));
						}
						else {
                            sqlvalue.add(new Integer(getMaxa0000(codesetid,parentid,dao)));
                        }
						sqlvalue.add("1");
					dao.insert(sqlstr.toString(), sqlvalue);
					sqlstr.delete(0, sqlstr.length());
					 sqlstr.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,layer from ");
					 sqlstr.append(" codeitem where codeitemid='"+codeitemid+"' and codesetid='"+codesetid+"'");
					 rs=dao.search(sqlstr.toString());
					 if(rs.next())
					 {
						 CodeItem item=new CodeItem();
						 item.setCodeid(rs.getString("codesetid").toUpperCase());
						 item.setCodeitem(codeitemid.toUpperCase());
						 item.setCodename(PubFunc.splitString(codeitemdesc,50));
						 item.setPcodeitem(rs.getString("parentid").toUpperCase());				 
						 item.setCcodeitem(rs.getString("childid").toUpperCase());
						 item.setCodelevel(rs.getString("layer"));
						 AdminCode.addCodeItem(item);
						 AdminCode.updateCodeItemDesc(rs.getString("codesetid").toUpperCase(), codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,50));
					 }
					if ("1".equals(first)) {
						sqlstr.delete(0, sqlstr.length());
						sqlvalue.clear();
						sqlstr.append("update codeitem set childid=");
						sqlstr.append("?");
						sqlstr.append(" where codeitemid=");
						sqlstr.append("?");
						sqlstr.append(" and codesetid=");
						sqlstr.append("?");
						sqlstr.append("");
						sqlvalue.add(codeitemid);
						sqlvalue.add(parentid != null ? parentid : "");
						sqlvalue.add(codesetid);
						dao.update(sqlstr.toString(),sqlvalue);
						int codeitemlength = codeitemid.length();
						//String sql = "update codeset set maxlength="+codeitemlength+" where codesetid='"+codesetid+"' and maxlength<"+codeitemlength;
						//String sql = "select MAX("+Sql_switcher.length("codeitemid")+") maxlength from codeitem where codesetid='"+codesetid+"'";
						String sql = "select itemlength from fielditem where codesetid='"+codesetid+"' union all select itemlength from t_hr_busifield where codesetid='"+codesetid+"'";
						//int f = dao.update(sql);
						rs = dao.search(sql);
						if(rs.next()){
							int maxlength = rs.getInt("itemlength");
							if(maxlength<codeitemlength){
								sql = "update fielditem set itemlength="+codeitemlength+" where codesetid='"+codesetid+"'";
								dao.update(sql);
								sql = "update t_hr_busifield set itemlength="+codeitemlength+" where codesetid='"+codesetid+"'";
								dao.update(sql);
								sql="select fieldsetid,itemid from fielditem where codesetid='"+codesetid+"' and useflag='1' union select fieldsetid,itemid from t_hr_busifield where codesetid='"+codesetid+"' and useflag='1'";
								rs = dao.search(sql);
								DBMetaModel dbmodel=new DBMetaModel(conn);
								DbWizard dbw=new DbWizard(conn);
								while(rs.next()){
									String fieldsetid = rs.getString("fieldsetid");
									if(fieldsetid.startsWith("A")){
										ArrayList dbprelist = DataDictionary.getDbpreList();
										for(int i=0;i<dbprelist.size();i++){
											String pre = (String)dbprelist.get(i);
											Table table=new Table((pre+fieldsetid).toLowerCase());
											Field field=new Field(rs.getString("itemid").toLowerCase(),rs.getString("itemid").toLowerCase());
											field.setDatatype(DataType.STRING);
											field.setLength(codeitemlength);
											table.addField(field);
											dbw.alterColumns(table);
											dbmodel.reloadTableModel((pre+fieldsetid).toLowerCase());
										}
									}else{
										Table table=new Table(fieldsetid.toLowerCase());
										Field field=new Field(rs.getString("itemid").toLowerCase(),rs.getString("itemid").toLowerCase());
										field.setDatatype(DataType.STRING);
										field.setLength(codeitemlength);
										table.addField(field);
										dbw.alterColumns(table);
										dbmodel.reloadTableModel(rs.getString("fieldsetid").toLowerCase());
									}
									
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
		}
	}
	
	 public static void updateLayer(Connection conn, String codesetid)
	  {
		   try{
			   String sql = " update codeitem set layer=null where codesetid='"+codesetid+"'";
			    ContentDAO dao = new ContentDAO(conn);
			    dao.update(sql);
			    sql = " update codeitem set layer = 1 where codesetid='"+codesetid+"' and parentid=codeitemid";
			    dao.update(sql);
			    sql = " update codeitem set layer=(select layer from codeitem c1 where c1.codesetid='"+codesetid+"' and c1.codeitemid=codeitem.parentid)+1 where codesetid='"+codesetid+"' and layer is null ";
			    int i=1;
			    while(i>0){
			    	i = dao.update(sql);
			    }
		   }catch(Exception e){
			   e.printStackTrace();
		   }
	  }
	
	
	public static void saveOrUpdateMainSet(Connection conn,RecordVo vo, String key)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		boolean flag = true;
		RecordVo vo2 = new RecordVo(vo.getModelName());
		vo2.setString("i9999", vo.getString("i9999"));
		vo2.setString(key, vo.getString(key));
		try 
		{
			if(dao.isExistRecordVo(vo2))
			{
				dao.updateValueObject(vo);
			}
			else
			{
				dao.addValueObject(vo);
			}			
		}
		catch (Exception e) 
		{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
		}
		/*try{
			dao.findByPrimaryKey(vo); 
		}catch(Exception e){
			flag = false;
			dao.addValueObject(vo);
		}
		if(flag){
			try {
				dao.updateValueObject(vo);
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}*/
	}
	
	/**
	 * 
	 * @param conn
	 * @param vo  
	 * @param opt 操作参数，'add':新增   / 'update':更新
	 * @throws GeneralException
	 */
	public static void saveOrUpdateMainSet2(Connection conn,RecordVo vo,String opt)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		
		
			try {
				
				if("add".equals(opt)) {
                    dao.addValueObject(vo);
                } else {
                    dao.updateValueObject(vo);
                }
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
	}
	
	
	public static int getNextI9999(Connection conn,String table,String codeitemid){
		ContentDAO dao = new ContentDAO(conn);
		int i9999=1;
		String sql  = "select max(i9999) as i9999 from "+table+" where "+table.substring(0,1)+"0100='"+codeitemid+"'";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				if(rs.getObject("i9999")!=null){
					i9999=rs.getInt("i9999")+1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
		}
		return i9999;
	}
	public static void updateInsertI9999(String i9999,Connection conn,String fieldsetid,String codeitemid)throws GeneralException{
		if(i9999==null||i9999.length()<=0) {
            return;
        }
		ContentDAO dao = new ContentDAO(conn);
		String sql = "update "+fieldsetid+" set i9999=i9999+1 where "+(fieldsetid.substring(0,1)+"0100").toLowerCase()+"='"+codeitemid+"' and i9999>="+i9999;
		try {
			dao.update(sql);
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}
	
	public static boolean delCodeitem(Connection conn,String codesetid,String parentid,String codeitemid)throws GeneralException{
		boolean flag = false;
		if(codeitemid==null||codeitemid.length()<=0) {
            return false;
        }
		ContentDAO dao = new ContentDAO(conn);
		String sql = "select codeitemid from codeitem where codesetid='"+codesetid+"' and codeitemid like '"+codeitemid+"%'";
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			ArrayList tempitemlist = new ArrayList();
			while(rs.next()){
				tempitemlist.add(rs.getString("codeitemid"));
			}
			sql = "delete from codeitem where codesetid='"+codesetid+"' and codeitemid like '"+codeitemid+"%'";
			dao.delete(sql, new ArrayList());
			ArrayList fieldsetlist = new ArrayList();
			if(("64".equals(codesetid))) {
                fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.PARTY_FIELD_SET);
            } else if(("65".equals(codesetid))) {
                fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.MEMBER_FIELD_SET);
            } else if(("66".equals(codesetid))) {
                fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.TRADEUNION_FIELD_SET);
            } else {
                fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.JOB_FIELD_SET);
            }
			for(int i=0;i<fieldsetlist.size();i++){
				FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
				sql = "delete from "+fieldset.getFieldsetid()+ " where "+fieldset.getFieldsetid().substring(0,1)+"0100 like '"+codeitemid+"%'";
				dao.delete(sql, new ArrayList());
			}
			CodeItem item = new CodeItem();
			for(int i=0;i<tempitemlist.size();i++){
				codeitemid = (String)tempitemlist.get(i);
				item.setCodeid(codesetid.toUpperCase());
				item.setCodeitem(codeitemid.toUpperCase());
				AdminCode.removeCodeItem(item);
			}
			flag = true;
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
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
		return flag;
	}
	public static void updateCode(Connection conn,String codesetid,String codeitemid,String codeitemdesc)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		ArrayList sqlvalue = new ArrayList();
		try {
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("update codeitem set codeitemdesc=?");
				sqlstr.append(" where codeitemid='"+codeitemid+"' and codesetid='"+codesetid+"'");
				sqlvalue.add(codeitemdesc);
				if(dao.update(sqlstr.toString(), sqlvalue)>0){
					AdminCode.updateCodeItemDesc(codesetid.toUpperCase(), codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,50));
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private static int getMaxa0000(String codesetid,String parentid,ContentDAO dao){
		int a0000=1;
		RowSet rs = null;
		try{
			String sql = "select max(a0000) a0000 from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid";
			if(parentid.length()==0){
				sql = "select max(a0000) a0000 from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid";
			}
			rs=dao.search(sql);
			if(rs.next()){
				a0000=rs.getInt("a0000")+1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return a0000;
	}
}
