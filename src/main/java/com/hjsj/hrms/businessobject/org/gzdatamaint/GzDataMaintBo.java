package com.hjsj.hrms.businessobject.org.gzdatamaint;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GzDataMaintBo {
	private Connection conn=null;
	private UserView userview=null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String backdate = sdf.format(new Date());
	//加载数据的页面 =bz：编制管理
    private String pageFlag = "";
	public GzDataMaintBo(Connection conn){
		this.conn = conn;
	}
	public GzDataMaintBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview = userview;
	}
	public String whereStr(String fieldsetid,String a_code,String viewdata){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		a_code=a_code!=null?a_code:"";
		String[] codearr =a_code.split(":");
		if(codearr.length==3){
			buf.append(maintable+"."+codearr[1]+"='");
			buf.append(codearr[2]+"'");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" and "+maintable+".B0110 like '");
				buf.append(userview.getManagePrivCodeValue());
				buf.append("%'");
			}else{
				buf.append(" and "+maintable+".E01A1 like '");
				buf.append(userview.getManagePrivCodeValue());
				buf.append("%'");
			}
		}else{
			/*岗位纯bs录入，没有过滤 撤销的机构，添加时间有效期过滤  guodd 15-2-10*/
			String now = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(maintable+".B0110 ");
            } else {
                buf.append(maintable+".E01A1 ");
            }
			buf.append(" in(select codeitemid from organization where "+Sql_switcher.dateValue(now)+" between start_date and end_date ");
			if(a_code!=null&&a_code.trim().length()>1){
				a_code = a_code.replace("UN","");
				a_code = a_code.replace("UM","");
				a_code = a_code.replace("@K","");
				if("B01".equalsIgnoreCase(maintable)){
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(" and ( parentid='"+a_code+"' or codeitemid='"+a_code+"' )");
					}else{
						buf.append(" and codeitemid=parentid");
					}
					/*岗位纯bs录入，没有过滤 撤销的机构，添加时间有效期过滤  guodd 15-2-10*/
					buf.append(" union select codeitemid from vorganization where "+Sql_switcher.dateValue(now)+" between start_date and end_date " );
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(" and ( parentid='"+a_code+"' or codeitemid='"+a_code+"' )");
					}else{
						buf.append(" and codeitemid=parentid");
					}
					buf.append(")");
				}else{
					if(a_code!=null&&a_code.trim().length()>0){
						/*岗位纯bs录入，没有过滤 撤销的机构，添加时间有效期过滤  guodd 15-2-10*/
						buf.append(" union select codeitemid from vorganization where "+Sql_switcher.dateValue(now)+" between start_date and end_date " );
						buf.append(") and ");
						if(!field.isMainset()) {
                            buf.append(maintable+".");
                        }
						buf.append("E01A1 like '");
						buf.append(a_code);
						buf.append("%'");
					}else{
						buf.append(")");
					}
				}
			}else{
				if("B01".equalsIgnoreCase(maintable)){
					if(userview.getManagePrivCodeValue()!=null&&userview.getManagePrivCodeValue().length()>0){
						buf.append(")");
						buf.append(" and "+maintable+".B0110 like '");
						buf.append(userview.getManagePrivCodeValue());
						buf.append("%'");
					}else{
						buf.append(" where codeitemid=parentid)");
						buf.append(" and "+maintable+".B0110 ='");
						buf.append(userview.getManagePrivCodeValue());
						buf.append("'");
					}
				}else{
					buf.append(")");
					if(userview.getManagePrivCodeValue()!=null&&userview.getManagePrivCodeValue().length()>0){
						buf.append(" and "+maintable+".E01A1 like '");
						buf.append(userview.getManagePrivCodeValue());
						buf.append("%'");
					}else{
						buf.append(" and "+maintable+".E01A1 ='");
						buf.append(userview.getManagePrivCodeValue());
						buf.append("'");
					}
				}
			}
		}
		if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110 )");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStrCode(String a_code,String infor){ 
		StringBuffer sqlstr = new StringBuffer();
		if("1".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>0){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid)){
					sqlstr.append("B0110 like '");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sqlstr.append("E0122 like'");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sqlstr.append("E01A1 like'");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						sqlstr.append(codearr[1]+"= '");
						sqlstr.append(codearr[2]);
						sqlstr.append("'");
					}
				}
			}else{
				sqlstr.append("1=1");
			}
		}else if("2".equals(infor)){
			sqlstr.append("B0110 in(select codeitemid from organization");
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" where parentid='"+a_code+"'");
			}else{
				sqlstr.append(" where codeitemid=parentid");
			}
			sqlstr.append(")");
		}else if("3".equals(infor)){
			sqlstr.append("E01A1 in(select codeitemid from organization");
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" where codeitemid like '"+a_code+"%'");
			}
			sqlstr.append(")");
		}else{
			sqlstr.append("1=1");
		}
		return sqlstr.toString();
	}
	public String whereStra_Code(String a_code,String infor){ 
		StringBuffer sqlstr = new StringBuffer();
		if("1".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>0){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid)){
					sqlstr.append("B0110 like '");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sqlstr.append("E0122 like'");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sqlstr.append("E01A1 like'");
					sqlstr.append(value);
					sqlstr.append("%'");
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						sqlstr.append(codearr[1]+"= '");
						sqlstr.append(codearr[2]);
						sqlstr.append("'");
					}
				}
			}else{
				sqlstr.append("1=1");
			}
		}else if("2".equals(infor)){
			sqlstr.append("B0110 in(select codeitemid from organization");
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" where codeitemid like '"+a_code+"%'");
			}
			sqlstr.append(")");
		}else if("3".equals(infor)){
			sqlstr.append("E01A1 in(select codeitemid from organization");
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" where codeitemid like '"+a_code+"%'");
			}
			sqlstr.append(")");
		}else{
			sqlstr.append("1=1");
		}
		return sqlstr.toString();
	}
	public String whereStr(String fieldsetid,String a_code,String viewdata,String checkadd){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		if("B01".equalsIgnoreCase(maintable)) {
            buf.append(maintable+".B0110 in(select codeitemid from organization");
        } else {
            buf.append(maintable+".E01A1 in(select codeitemid from organization");
        }
		if(a_code!=null&&a_code.trim().length()>1){
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if("B01".equalsIgnoreCase(maintable)){
				if(checkadd!=null&& "1".equals(checkadd)){
					buf.append(")");
					if("B01".equalsIgnoreCase(maintable)){
						if(a_code!=null&&a_code.trim().length()>0){
							buf.append(" and ");
							if(!field.isMainset()) {
                                buf.append(maintable+".");
                            }
							buf.append("B0110 like '");
							buf.append(a_code);
							buf.append("%'");
							viewdata="1";
						}
					}
				}else{
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(" where parentid='"+a_code+"'");
					}else{
						buf.append(" where codeitemid=parentid");
					}
					buf.append(")");
				}
			}else{
				if(a_code!=null&&a_code.trim().length()>0){
					buf.append(") and ");
					if(!field.isMainset()) {
                        buf.append(maintable+".");
                    }
					buf.append("E01A1 like '");
					buf.append(a_code);
					buf.append("%'");
				}else{
					buf.append(")");
				}
			}
		}else{
			buf.append(" where codeitemid=parentid)");
		}
		if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110 )");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStrSql(String fieldsetid,String a_code,String viewdata,String checkadd){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		if("B01".equalsIgnoreCase(maintable)) {
            buf.append(maintable+".B0110 in(select codeitemid from organization");
        } else {
            buf.append(maintable+".E01A1 in(select codeitemid from organization");
        }
		if(a_code!=null&&a_code.trim().length()>1){
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if("B01".equalsIgnoreCase(maintable)){
				if(checkadd!=null&& "1".equals(checkadd)){
					buf.append(")");
					if("B01".equalsIgnoreCase(maintable)){
						if(a_code!=null&&a_code.trim().length()>0){
							buf.append(" and ");
							if(!field.isMainset()) {
                                buf.append(maintable+".");
                            }
							buf.append("B0110 like '");
							buf.append(a_code);
							buf.append("%'");
							viewdata="1";
						}
					}
				}else{
					buf.append(" where codeitemid like '"+a_code+"%'");
					buf.append(")");
				}
			}else{
				if(a_code!=null&&a_code.trim().length()>0){
					buf.append(") and ");
					if(!field.isMainset()) {
                        buf.append(maintable+".");
                    }
					buf.append("E01A1 like '");
					buf.append(a_code);
					buf.append("%'");
				}else{
					buf.append(")");
				}
			}
		}else{
			buf.append(" where codeitemid like '"+a_code+"%'");
			buf.append(")");
		}
		if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110 )");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStrLevelSql(String fieldsetid,String a_code,String viewdata,String checkadd){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		if("B01".equalsIgnoreCase(maintable)) {
            buf.append(maintable+".B0110 in(select codeitemid from organization");
        } else {
            buf.append(maintable+".E01A1 in(select codeitemid from organization");
        }
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		buf.append(" where "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		if(a_code!=null&&a_code.trim().length()>1){
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if("B01".equalsIgnoreCase(maintable)){
				if(checkadd!=null&& "1".equals(checkadd)){
					buf.append(")");
					if("B01".equalsIgnoreCase(maintable)){
						if(a_code!=null&&a_code.trim().length()>0){
							buf.append(" and ");
							if(!field.isMainset()) {
                                buf.append(maintable+".");
                            }
							buf.append("B0110 like '");
							buf.append(a_code);
							buf.append("%'");
							viewdata="1";
						}
					}
				}else{
					if(a_code.trim().length()>0){
						buf.append(" and codeitemid ='"+a_code+"'");
						buf.append(")");
					}else{
						if(!this.userview.isSuper_admin()&&this.userview.getManagePrivCodeValue().length()>0){
							buf.append(" and codeitemid='"+this.userview.getManagePrivCodeValue()+"'");
						}
						buf.append(")");
					}
				}
			}else{
				if(a_code!=null&&a_code.trim().length()>0){
					buf.append(") and ");
					if(!field.isMainset()) {
                        buf.append(maintable+".");
                    }
					buf.append("E01A1 like '");
					buf.append(a_code);
					buf.append("%'");
				}else{
					buf.append(")");
				}
			}
		}else{
			if(!this.userview.isSuper_admin()) {
                buf.append(" and codeitemid='"+this.userview.getManagePrivCodeValue()+"'");
            }
			buf.append(")");
		}
		if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110 )");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStr(String fieldsetid,String a_code,String viewdata,String flag,String checkadd){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		if("B01".equalsIgnoreCase(maintable)){
			buf.append(maintable+".B0110 in(select codeitemid from organization");
		}else{ 
			buf.append(maintable+".E01A1 in(select codeitemid from organization");
		}
		if(a_code!=null&&a_code.trim().length()>1){
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				if("B01".equalsIgnoreCase(maintable)){
					if(checkadd!=null&& "1".equals(checkadd)){
						if(!"1".equals(flag)){
							buf.append(" where codesetid='UN'");
						}
						buf.append(")");
						if("B01".equalsIgnoreCase(maintable)){
							if(a_code!=null&&a_code.trim().length()>0){
								buf.append(" and ");
								if(!field.isMainset()) {
                                    buf.append(maintable+".");
                                }
								buf.append("B0110 like '");
								buf.append(a_code);
								buf.append("%'");
							}
						}
					}else{
						if(a_code!=null&&a_code.trim().length()>0){
							buf.append(" where parentid='"+a_code+"'");
						}else{
							buf.append(" where codeitemid=parentid");
						}
						if(!"1".equals(flag)){
							buf.append(" and codesetid='UN'");
						}
						buf.append(")");
					}
				}else{
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(") and ");
						if(!field.isMainset()) {
                            buf.append(maintable+".");
                        }
						buf.append("E01A1 like '");
						buf.append(a_code);
						buf.append("%'");
					}else{
						buf.append(")");
					}
				}
			}else{
				buf.append(" where codeitemid=parentid)");
			}
		}else{
			
			/**
			 * cmq changed at 20121003 
			 */
			String unitid =this.userview.getUnitIdByBusi("4") ;// this.userview.getUnit_id();
			unitid = PubFunc.getTopOrgDept(unitid);
			buf.append(" where codeitemid in('UN'");
			if(unitid!=null&&unitid.length()>0){
				String arr[] = unitid.split("`");
				for(int i=0;i<arr.length;i++){
					String itemid = arr[i];
					if(itemid!=null&&itemid.trim().length()>2){
						buf.append(",'"+itemid.substring(2)+"'");
					}
				}
			}
			buf.append("))");
		}
		if(a_code!=null&&a_code.trim().length()>0) {
            buf.append(" and "+maintable+".B0110<>'"+a_code+"' ");
        }
		if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110 )");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStrOrg(String fieldsetid,String a_code,String viewdata,String flag,String checkadd){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		maintable="organization";
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			buf.append(" right join ");
			buf.append(maintable);
			buf.append(" org on ");
			buf.append("org.codeitemid=");
			buf.append("a.B0110 ");
		}
		buf.append(" where org.codeitemid in(select codeitemid from organization");
		if(a_code!=null&&a_code.trim().length()>0){
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			if(a_code!=null&&a_code.trim().length()>0){
				if(checkadd!=null&& "1".equals(checkadd)){
					if(!"1".equals(flag)){
						buf.append(" where codesetid='UN'");
					}else{
						buf.append(" where 1=1 ");
					}
					buf.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					buf.append(")");
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(" and ");
						if(!field.isMainset()) {
                            buf.append("org.codeitemid like '");
                        } else {
                            buf.append("B0110 like '");
                        }
						buf.append(a_code);
						buf.append("%'");
					}
				}else{
					if(a_code!=null&&a_code.trim().length()>0){
						buf.append(" where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (parentid='"+a_code+"' or codeitemid='"+a_code+"')");
					}else{
						buf.append(" where codeitemid=parentid");
						buf.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}
					if(!"1".equals(flag)){
						buf.append(" and codesetid='UN'");
					}
					buf.append(")");
				}
			}else{
				buf.append(" where codeitemid=parentid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)");
			}
		}
		else
		{
			/**
			 *cmq changed at 20121003 for 单位和岗位的权限范围规则
			 *业务范围-操作单位-人员范围
			 */
			String unitid =this.userview.getUnitIdByBusi("4") ;
			if("bz".equalsIgnoreCase(this.pageFlag)) {
			    unitid = this.userview.getUnit_id();
			}
			
			unitid = PubFunc.getTopOrgDept(unitid);
			if(unitid.length()<3){
				buf.append(" where 1=2");
				buf.append(")");
			}else if("UN`".equalsIgnoreCase(unitid)){
				buf.append(" where codeitemid=parentid");
				buf.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				buf.append(")");
				
			}else{
				buf.append(" where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and codeitemid in('UN'");
				if(unitid!=null&&unitid.length()>0){
					String arr[] = unitid.split("`");
					for(int i=0;i<arr.length;i++){
						String itemid = arr[i];
						if(itemid!=null&&itemid.trim().length()>2){
							buf.append(",'"+itemid.substring(2)+"'");
						}
					}
				}
				buf.append("))");
			}
		}
//		if(a_code!=null&&a_code.trim().length()>0)
//			buf.append(" and org.codeitemid<>'"+a_code+"' ");
/*		if(viewdata!=null&&viewdata.trim().length()>0&&viewdata.equals("1")&&!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			buf.append(" where ");
			buf.append("B0110=a.B0110 )");
			buf.append(" or a.B0110 is null)");
		}*/
		
		return buf.toString();
	}
	
	public String whereStrOrg1(String fieldsetid,String ctrl_type,String nextlevel,String code){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		maintable="organization";
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			buf.append(" right join ");
			buf.append(maintable);
			buf.append(" org on ");
			buf.append("org.codeitemid=");
			buf.append("a.B0110 ");
		}
		buf.append(" where org.codeitemid in(select codeitemid from organization");
		{
			    /**
			     * cmq changed at 20121003 
			     */
				String unitid = this.userview.getUnitIdByBusi("4");//this.userview.getUnit_id();
				int length=unitid.length();
				if(length>2){
					if("`".equals(unitid.substring(length-1, length))){
						unitid=unitid.substring(0, length-1);
						if(unitid.length()<=3|| "UN".equalsIgnoreCase(unitid)){
							buf.append(" where ");
							buf.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
							if(!"1".equals(ctrl_type)){
									buf.append(" and codesetid='UN'");
							}
							if("1".equals(nextlevel)){
								buf.append(" and grade<3");
							}
							if(code.length()==0) {
                                buf.append(" and codeitemid=parentid");
                            } else{
								buf.append(" and (codeitemid='"+code+"' or parentid='"+code+"')");
							}
							buf.append(")");
						}else{
							buf.append(" where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (1=2");
							if(unitid!=null&&unitid.length()>0){
								String arr[] = unitid.split("`");
								for(int i=0;i<arr.length;i++){
									String itemid = arr[i];
									if(itemid!=null&&itemid.trim().length()>2){
										if(!"1".equals(nextlevel)){
											buf.append(" or codeitemid like '"+itemid.substring(2)+"%'");
										}else{
											buf.append(" or (codeitemid='"+itemid.substring(2)+"' or parentid='"+itemid.substring(2)+"')");
										}
									}
								}
							}
							buf.append(")");
							if(code.length()==0){
								if(unitid!=null&&unitid.length()>0)
								{
									/**
									 * cmq changed at 20121003 for 单位和岗位的权限控制规则
									 * 业务范围-操作单位-人员范围
									 */
									String arr[] = unitid.split("`");
									buf.append(" and (");
									for(int i=0;i<arr.length;i++)
									{
										String itemid = arr[i];
										if(itemid!=null&&itemid.trim().length()>2)
										{
											if(i!=0) {
                                                buf.append(" or ");
                                            }
											//buf.append(" and codeitemid ='"+itemid.substring(2)+"'");
											buf.append(" codeitemid ='"+itemid.substring(2)+"'");
										}
									}
									buf.append(")");
								}
							}else{
								buf.append(" and (codeitemid='"+code+"' or parentid='"+code+"')");
							}
							if(!"1".equals(ctrl_type)){
								buf.append(" and codesetid='UN'");
							}
							buf.append(")");
						}
					}
				}else{
					buf.append(" where 1=2)");
				}
		}
		return buf.toString();
	}
	public String whereStr(String fieldsetid,String a_code){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0,1))){
			maintable="B01";
		}else{
			maintable="K01";
		}
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".B0110=");
				buf.append("a.B0110 ");
			}else{
				buf.append(" right join ");
				buf.append(maintable);
				buf.append(" on ");
				buf.append(maintable);
				buf.append(".E01A1=");
				buf.append("a.E01A1 ");
			}
		}
		buf.append(" where ");
		if("B01".equalsIgnoreCase(maintable)){
			buf.append(maintable+".B0110 in(select codeitemid from organization where codeitemid='"+a_code+"')");
		}else{ 
			buf.append(maintable+".E01A1 in(select codeitemid from organization where codeitemid='"+a_code+"')");
		}

		if(!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			if("B01".equalsIgnoreCase(maintable)){
				buf.append(" where ");
				buf.append("B0110=a.B0110)");
			}else{
				buf.append(" where ");
				buf.append("E01A1=a.E01A1 )");
			}
			if("B01".equalsIgnoreCase(maintable)) {
                buf.append(" or a.B0110 is null )");
            } else {
                buf.append(" or a.E01A1 is null )");
            }
		}
		return buf.toString();
	}
	public String whereStrOrg(String fieldsetid,String a_code){
		StringBuffer buf = new StringBuffer();
		FieldSet field = DataDictionary.getFieldSetVo(fieldsetid);
		String maintable="";
		maintable="organization";
		
		buf.append(" from ");
		buf.append(fieldsetid);
		if(!field.isMainset()){
			buf.append(" a ");
			buf.append(" right join ");
			buf.append(maintable);
			buf.append(" org on ");
			buf.append("org.codeitemid=");
			buf.append("a.B0110 ");
		}
		buf.append(" where org.codeitemid in(select codeitemid from organization where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and codeitemid='"+a_code+"')");

		if(!field.isMainset()){
			buf.append(" and (I9999=(select max(I9999) from ");
			buf.append(fieldsetid);
			buf.append(" where ");
			buf.append("B0110=a.B0110)");
			buf.append(" or a.B0110 is null )");
		}
		return buf.toString();
	}
	public String vilStr(String filedsetid,ArrayList fieldset){
		StringBuffer str = new StringBuffer();
		if("K".equalsIgnoreCase(filedsetid.substring(0,1))){
			for(int i=0;i<fieldset.size();i++){
				Field fielditem = (Field)fieldset.get(i);
				if("b0110".equalsIgnoreCase(fielditem.getName())){
//					str.append("case (select codesetid from organization where ");
//					str.append("codeitemid=(select distinct parentid from organization where");
//					str.append(" codeitemid=K01.E0122))");
//					str.append(" when 'UN' then (select distinct parentid from organization");
//					str.append(" where codeitemid=K01.E0122)");
//					str.append(" else (select distinct parentid from organization where");
//					str.append(" codeitemid=(select distinct parentid from organization where");
//					str.append(" codeitemid=K01.E0122)) end  as B0110,");
					
					str.append(" (select distinct parentid from organization");
					str.append(" where codeitemid=K01.E0122) as B0110,");
				}else if("e0122".equalsIgnoreCase(fielditem.getName())){
					str.append("K01.E0122,");
				}else if("e01a1".equalsIgnoreCase(fielditem.getName())){
					str.append("K01.E01A1,");
				}else if("oper".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' oper,");
                } else if("downole".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' downole,");
                } else if("upole".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' upole,");
                } else{
					if("state".equalsIgnoreCase(fielditem.getName())){
						if("K00".equalsIgnoreCase(filedsetid)|| "B00".equalsIgnoreCase(filedsetid)){
							str.append(" CASE WHEN ");
							str.append(Sql_switcher.length("a."+fielditem.getName()));
							str.append("=1 THEN '0'"+Sql_switcher.getCatOp()+"a."+fielditem.getName());
							str.append(" ELSE a."+fielditem.getName()+" END");
							str.append(" AS "+fielditem.getName()+",");
						}else{
							str.append("a."+fielditem.getName()+",");
						}
					}else if("flag".equalsIgnoreCase(fielditem.getName())){
						if("B00".equalsIgnoreCase(filedsetid)|| "B00".equalsIgnoreCase(filedsetid)){
							str.append("(select SORTNAME from mediasort where FLAG=a.flag) as flag,");
						}else{
							str.append("a."+fielditem.getName()+",");
						}
					}else{
						str.append(fielditem.getName()+",");
					}
				}
			}
		}else{
			for(int i=0;i<fieldset.size();i++){
				Field fielditem = (Field)fieldset.get(i);
				if("b0110".equalsIgnoreCase(fielditem.getName())) {
                    str.append("B01.B0110,");
                } else if("oper".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' oper,");
                } else if("downole".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' downole,");
                } else if("upole".equalsIgnoreCase(fielditem.getName())) {
                    str.append("'' upole,");
                } else{
					if("state".equalsIgnoreCase(fielditem.getName())){
						if("K00".equalsIgnoreCase(filedsetid)|| "B00".equalsIgnoreCase(filedsetid)){
							str.append(" CASE WHEN ");
							str.append(Sql_switcher.length("a."+fielditem.getName()));
							str.append("=1 THEN '0'"+Sql_switcher.getCatOp()+"a."+fielditem.getName());
							str.append(" ELSE a."+fielditem.getName()+" END");
							str.append(" AS "+fielditem.getName()+",");
						}else{
							str.append("a."+fielditem.getName()+",");
						}
					}else if("flag".equalsIgnoreCase(fielditem.getName())){
						if("B00".equalsIgnoreCase(filedsetid)|| "B00".equalsIgnoreCase(filedsetid)){
							str.append("(select SORTNAME from mediasort where FLAG=a.flag) as flag,");
						}else{
							str.append("a."+fielditem.getName()+",");
						}
					}else{
						str.append(fielditem.getName()+",");
					}
				}
			}
		}
		String sqlstr="";
		if(str.length()>0){
			sqlstr = str.toString().substring(0,str.length()-1);
		}
		return sqlstr; 
	}
	public String vilStrOrg(String filedsetid,ArrayList fieldset,boolean isExcel){
		StringBuffer str = new StringBuffer();
		if("K".equalsIgnoreCase(filedsetid.substring(0,1))){
			for(int i=1;i<fieldset.size();i++){
				Field fielditem = (Field)fieldset.get(i);
				if(isExcel){
					if(!fielditem.isVisible()){
						fieldset.remove(i);
						i--;
						continue;
					}
				}
				if("b0110".equalsIgnoreCase(fielditem.getName())){
					str.append("(select parentid from organization where codeitemid=K01.E0122 group by parentid) as B0110,");
				}else if("e0122".equalsIgnoreCase(fielditem.getName())){
					str.append("K01.E0122,");
				}else if("e01a1".equalsIgnoreCase(fielditem.getName())){
					str.append("K01.E01A1,");
				}else{
					str.append(fielditem.getName()+",");
				}
			}
		}else{
			for(int i=1;i<fieldset.size();i++){
				Field fielditem = (Field)fieldset.get(i);
				if(isExcel){
					if(!fielditem.isVisible()){
						fieldset.remove(i);
						i--;
						continue;
					}
				}
				if("b0110".equalsIgnoreCase(fielditem.getName())) {
                    str.append("org.codeitemid as B0110,");
                } else {
                    str.append(fielditem.getName()+",");
                }
			}
		}
		String sqlstr="";
		if(str.length()>0){
			sqlstr = str.toString().substring(0,str.length()-1);
		}
		return sqlstr; 
	}
	public ArrayList fieldList(String setname){
		ArrayList retlist=new ArrayList();
		if(setname!=null&&setname.length()>0){
			if(!setname.startsWith("K")){
				FieldItem fi=DataDictionary.getFieldItem("b0110");
				fi.setReadonly(true);
				retlist.add(fi.cloneField());
			}else{
				FieldItem fi=DataDictionary.getFieldItem("b0110");
				fi.setReadonly(true);
				retlist.add(fi.cloneField());
				
				fi=DataDictionary.getFieldItem("e0122");
				fi.setReadonly(true);
				retlist.add(fi.cloneField());
				
				fi=DataDictionary.getFieldItem("e01a1");
				fi.setReadonly(true);
				retlist.add(fi.cloneField());
			}
			
			FieldSet fieldsetfi=DataDictionary.getFieldSetVo(setname);
			if(!fieldsetfi.isMainset()){
				Field tempfield=new Field("I9999","序号");
				tempfield.setDatatype(DataType.INT);
				tempfield.setReadonly(true);
				tempfield.setVisible(true);
				retlist.add(tempfield);
			}
			if("A00".equalsIgnoreCase(setname)|| "B00".equalsIgnoreCase(setname)
					|| "K00".equalsIgnoreCase(setname)) {
                retlist.addAll(a00ItemList());
            } else {
                retlist.addAll(itemList(setname));
            }
		}
		return retlist;
	}
	public ArrayList a00ItemList(){
		ArrayList itemlist = new ArrayList();

		Field fielditem = new Field("state","状态");
		fielditem.setCodesetid("23");
		fielditem.setDatatype(DataType.STRING);
		itemlist.add(fielditem);
	
		fielditem = new Field("flag","分类");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		fielditem = new Field("Title","名称");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		itemlist.add(fielditem);
		
		fielditem = new Field("downole","浏览");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		fielditem = new Field("upole","调入");
		fielditem.setCodesetid("0");
		fielditem.setDatatype(DataType.STRING);
		fielditem.setReadonly(true);
		itemlist.add(fielditem);
		
		return itemlist;
	}
	public ArrayList itemList(String setname){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth from fielditem where fieldsetid='");
		buf.append(setname);
		buf.append("' and useflag=1 order by displayid");
		ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				FieldItem fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				if(fielditem==null){
					DataDictionary.refresh();
					fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				}
				
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					int displaywidth = rs.getInt("displaywidth");
					Field field=fielditem.cloneField();
					StringBuffer format=new StringBuffer();	
					field.setLength(fielditem.getItemlength());
					field.setCodesetid(fielditem.getCodesetid());
					if("N".equals(fielditem.getItemtype())){
						field.setDecimalDigits(fielditem.getDecimalwidth());
						if(fielditem.getDecimalwidth()>0){
							for(int j=0;j<fielditem.getDecimalwidth();j++){
								format.append("#");	
							}
							field.setFormat("####."+format.toString());
						}else{
							field.setFormat("####");
						}
					}
					field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
					String pre = userview.analyseFieldPriv(fielditem.getItemid());   // 2013-11-17  dengc  解决薪资基础数据维护子集、指标权限控制不对问题
			/*		String pri = userview.analyseTablePriv(fielditem.getFieldsetid());
					if(pre.equals("1")||pri.equals("1"))
						field.setReadonly(true);
					else
						field.setReadonly(false);  */
					
					if("1".equals(pre)) {
                        field.setReadonly(true);
                    } else {
                        field.setReadonly(false);
                    }
					if(displaywidth<1){
						field.setVisible(false);
					}else{
					//	if(pri.equals("0")||pre.equals("0"))
						if("0".equals(pre)) {
                            field.setVisible(false);
                        } else {
                            field.setVisible(true);
                        }
					}
					list.add(field);
				}else{
					FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
					if(!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						Field field=fielditem.cloneField();
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
							field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						String pre = userview.analyseFieldPriv(fielditem.getItemid());
					/*	String pri = userview.analyseTablePriv(fielditem.getFieldsetid());
						if(pre.equals("1")||pri.equals("1"))
							field.setReadonly(true);
						else
							field.setReadonly(false);*/
						if("1".equals(pre)) {
                            field.setReadonly(true);
                        } else {
                            field.setReadonly(false);
                        }
						if(displaywidth<1){
							field.setVisible(false);
						}else{
						//	if(pri.equals("0")||pri.equals("0"))
							if("0".equals(pre)) {
                                field.setVisible(false);
                            } else {
                                field.setVisible(true);
                            }
						}
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemList(FieldSet fieldset){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		String setname = fieldset.getFieldsetid();
		int UNIT_DISPLAYID = 1;
		int POS_DISPLAYID = 1;
		int POS_LEN = 14;
		int UNIT_LEN = 14;
		int E01_DISPLAYID = 1;
		int A01_DISPLAYID = 1;

		if(fieldset.isMainset()){
			String UNIT_DISPLAYID_str = getValues("UNIT_DISPLAYID");
				UNIT_DISPLAYID=UNIT_DISPLAYID_str!=null&&UNIT_DISPLAYID_str.trim().length()>0?Integer.parseInt(UNIT_DISPLAYID_str):1;
			String POS_DISPLAYID_str = getValues("POS_DISPLAYID");
				POS_DISPLAYID=POS_DISPLAYID_str!=null&&POS_DISPLAYID_str.trim().length()>0?Integer.parseInt(POS_DISPLAYID_str):1;
			String UNIT_LEN_str = getValues("UNIT_LEN");
				UNIT_LEN=UNIT_LEN_str!=null&&UNIT_LEN_str.trim().length()>0?Integer.parseInt(UNIT_LEN_str):14;
			String POS_LEN_str =getValues("POS_LEN");
				POS_LEN=POS_LEN_str!=null&&POS_LEN_str.trim().length()>0?Integer.parseInt(POS_LEN_str):14;
		}
		ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				FieldItem fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						int displayid = rs.getInt("displayid");
						if("A".equalsIgnoreCase(setname.substring(0,1))){
							if(UNIT_DISPLAYID>POS_DISPLAYID){
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								
							}else{
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
							}
							if(!fieldset.isMainset()&&A01_DISPLAYID>0){
								list.add(fieldA0101());
								A01_DISPLAYID=0;
							}
						}
						
						Field field=fielditem.cloneField();					
						StringBuffer format=new StringBuffer();	
						if("N".equals(fielditem.getItemtype())){
						    field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						field.setReadonly(false);
						if(displaywidth<1){
							field.setVisible(false);
						}else{
							field.setVisible(true);
						}
						String reserveitem = rs.getString("reserveitem");
						reserveitem=reserveitem!=null&&reserveitem.trim().length()>0?reserveitem:"0";
						if("1".equals(reserveitem)){
							field.setLabel(field.getLabel()+"<font color='red'>*</font>");
						}
						list.add(field);
					}
				}
			}
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid())&&list.size()<1){
				list.add(fieldB0110(UNIT_LEN));
				list.add(fieldE0122());
				list.add(fieldE01A1(POS_LEN));
				list.add(fieldA0101());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList torgItemList(){
		ArrayList list = new ArrayList();
		FieldItem item3=new FieldItem();
		item3.setFieldsetid("t_vorg_staff");
		item3.setItemid("oper");
		item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
		item3.setItemtype("A");
		item3.setCodesetid("0");
		item3.setAlign("center");
		item3.setReadonly(true);
		list.add(item3.cloneField());	
		
		Field tempfield = new Field("dbase","人员库");
		tempfield.setVisible(false);
		tempfield.setCodesetid("@@");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(10);
		list.add(tempfield);
		
		tempfield=new Field("A0100","A0100");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(8);
		tempfield.setReadonly(true);			
		tempfield.setVisible(false);
		tempfield.setCodesetid("0");
		list.add(tempfield);
		
		tempfield=new Field("I9999","序号");
		tempfield.setDatatype(DataType.INT);
		tempfield.setReadonly(true);
		tempfield.setVisible(false);
		tempfield.setCodesetid("0");
		list.add(tempfield);
		
		FieldItem fielditem = DataDictionary.getFieldItem("b0110");
		tempfield = fielditem.cloneField();
		tempfield.setVisible(false);
		list.add(tempfield);

		
		tempfield=new Field("orgname","虚拟机构");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(100);
		tempfield.setCodesetid("0");
		list.add(tempfield);
		
		tempfield=new Field("pos","职务");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(100);
		tempfield.setCodesetid("0");
		list.add(tempfield);
		
		tempfield=new Field("a0000","序号");
		tempfield.setDatatype(DataType.INT);
		tempfield.setReadonly(true);
		tempfield.setVisible(false);
		tempfield.setCodesetid("0");
		list.add(tempfield);

		tempfield=new Field("startdate","聘任开始时间");
		tempfield.setDatatype(DataType.DATE);
		tempfield.setCodesetid("0");
		tempfield.setLength(20);
		list.add(tempfield);
		
		tempfield=new Field("enddate","聘任结束时间");
		tempfield.setDatatype(DataType.DATE);
		tempfield.setCodesetid("0");
		tempfield.setLength(20);
		list.add(tempfield);
		
		fielditem = new FieldItem("t_vorg_staff","state");
		fielditem.setVisible(true);
		fielditem.setCodesetid("KF");
		fielditem.setItemtype("A");
		fielditem.setItemlength(2);
		fielditem.setDecimalwidth(0);
		fielditem.setItemdesc("状态");
		list.add(fielditem.cloneField());

		fielditem = new FieldItem("t_vorg_staff","memo");
		fielditem.setVisible(true);
		fielditem.setItemtype("M");
		fielditem.setItemdesc("备注");
		list.add(fielditem.cloneField());

		return list;
	}
	public ArrayList torgFieldItemList(){
		ArrayList list = new ArrayList();
		FieldItem fielditem = null;
		
//		fielditem = new FieldItem("t_vorg_staff","dbase");
//		fielditem.setVisible(true);
//		fielditem.setCodesetid("@@");
//		fielditem.setItemtype("A");
//		fielditem.setItemlength(10);
//		fielditem.setItemdesc("人员库");
//		fielditem.setDisplaywidth(10);
//		list.add(fielditem);

		fielditem = new FieldItem("t_vorg_staff","orgname");
		fielditem.setVisible(true);
		fielditem.setCodesetid("UM");
		fielditem.setItemtype("A");
		fielditem.setItemlength(100);
		fielditem.setItemdesc("虚拟机构");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);
		
		fielditem = new FieldItem("t_vorg_staff","pos");
		fielditem.setVisible(true);
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(100);
		fielditem.setItemdesc("职务");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);

		fielditem = new FieldItem("t_vorg_staff","startdate");
		fielditem.setVisible(true);
		fielditem.setCodesetid("0");
		fielditem.setItemtype("D");
		fielditem.setItemlength(10);
		fielditem.setItemdesc("聘任开始时间");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);
		
		fielditem = new FieldItem("t_vorg_staff","enddate");
		fielditem.setVisible(true);
		fielditem.setCodesetid("0");
		fielditem.setItemtype("D");
		fielditem.setItemlength(10);
		fielditem.setItemdesc("聘任结束时间");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);
		
		fielditem = new FieldItem("t_vorg_staff","state");
		fielditem.setVisible(true);
		fielditem.setCodesetid("KF");
		fielditem.setItemtype("A");
		fielditem.setItemlength(2);
		fielditem.setDecimalwidth(0);
		fielditem.setItemdesc("状态");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);

		fielditem = new FieldItem("t_vorg_staff","memo");
		fielditem.setVisible(true);
		fielditem.setItemtype("M");
		fielditem.setItemdesc("备注");
		fielditem.setDisplaywidth(10);
		list.add(fielditem);

		return list;
	}
	public FieldItem getFieldItem(String itemid){
		
		FieldItem fielditem = null;
		if("dbase".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("A01","dbase");
			fielditem.setVisible(false);
			fielditem.setCodesetid("@@");
			fielditem.setItemtype("A");
			fielditem.setItemlength(10);
			fielditem.setItemdesc("人员库");
		}else if("I9999".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("A01","I9999");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(10);
			fielditem.setItemdesc("序号");
		}else if("orgname".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","orgname");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(100);
			fielditem.setItemdesc("虚拟机构");
		}else if("pos".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","pos");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(100);
			fielditem.setItemdesc("职务");
		}else if("startdate".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","startdate");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("D");
			fielditem.setItemlength(20);
			fielditem.setItemdesc("聘任开始时间");
		}else if("enddate".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","enddate");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("D");
			fielditem.setItemlength(20);
			fielditem.setItemdesc("聘任结束时间");
		}else if("state".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","state");
			fielditem.setVisible(true);
			fielditem.setCodesetid("KF");
			fielditem.setItemtype("A");
			fielditem.setItemlength(2);
			fielditem.setItemdesc("状态");
		}else if("memo".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem("t_vorg_staff","memo");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("M");
			fielditem.setItemdesc("备注");
		}
		return fielditem;
	}
public FieldItem getFieldItem(String fieldsetid,String itemid){
		
		FieldItem fielditem = null;
		if("state".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem(fieldsetid,"state");
			fielditem.setVisible(true);
			fielditem.setCodesetid("23");
			fielditem.setItemtype("A");
			fielditem.setItemlength(1);
			fielditem.setItemdesc("状态");
		}else if("flag".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem(fieldsetid,"flag");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(1);
			fielditem.setItemdesc("分类");
		}else if("Title".equalsIgnoreCase(itemid)){
			fielditem = new FieldItem(fieldsetid,"Title");
			fielditem.setVisible(true);
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(40);
			fielditem.setItemdesc("名称");
		}
		return fielditem;
	}
	public ArrayList fieldItemList(FieldSet fieldset){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		String setname = fieldset.getFieldsetid();
		int UNIT_DISPLAYID = 1;
		int POS_DISPLAYID = 1;
		int POS_LEN = 14;
		int UNIT_LEN = 14;
		int E01_DISPLAYID = 1;
		int A01_DISPLAYID = 1;

		if(fieldset.isMainset()){
			String UNIT_DISPLAYID_str = getValues("UNIT_DISPLAYID");
				UNIT_DISPLAYID=UNIT_DISPLAYID_str!=null&&UNIT_DISPLAYID_str.trim().length()>0?Integer.parseInt(UNIT_DISPLAYID_str):1;
			String POS_DISPLAYID_str = getValues("POS_DISPLAYID");
				POS_DISPLAYID=POS_DISPLAYID_str!=null&&POS_DISPLAYID_str.trim().length()>0?Integer.parseInt(POS_DISPLAYID_str):1;
			String UNIT_LEN_str = getValues("UNIT_LEN");
				UNIT_LEN=UNIT_LEN_str!=null&&UNIT_LEN_str.trim().length()>0?Integer.parseInt(UNIT_LEN_str):14;
			String POS_LEN_str =getValues("POS_LEN");
				POS_LEN=POS_LEN_str!=null&&POS_LEN_str.trim().length()>0?Integer.parseInt(POS_LEN_str):14;
		}
		ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				FieldItem fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						int displayid = rs.getInt("displayid");
						if("A".equalsIgnoreCase(setname.substring(0,1))){
							if(UNIT_DISPLAYID>POS_DISPLAYID){
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										FieldItem item =  DataDictionary.getFieldItem("E01A1");
										if(POS_LEN>0) {
                                            item.setVisible(true);
                                        } else {
                                            item.setVisible(false);
                                        }
										list.add(item);	
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												FieldItem item =  DataDictionary.getFieldItem("E01A1");
												if(POS_LEN>0) {
                                                    item.setVisible(true);
                                                } else {
                                                    item.setVisible(false);
                                                }
												list.add(item);	
												POS_DISPLAYID=0;
											}
										}else{
											FieldItem item =  DataDictionary.getFieldItem("E01A1");
											if(POS_LEN>0) {
                                                item.setVisible(true);
                                            } else {
                                                item.setVisible(false);
                                            }
											list.add(item);	
											POS_DISPLAYID=0;
										}
									}
								}
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									FieldItem item =  DataDictionary.getFieldItem("B0110");
									if(UNIT_LEN>0) {
                                        item.setVisible(true);
                                    } else {
                                        item.setVisible(false);
                                    }
									list.add(item);	
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(DataDictionary.getFieldItem("E0122"));
									E01_DISPLAYID=0;
								}
								
							}else{
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									FieldItem item =  DataDictionary.getFieldItem("B0110");
									if(UNIT_LEN>0) {
                                        item.setVisible(true);
                                    } else {
                                        item.setVisible(false);
                                    }
									list.add(item);	
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(DataDictionary.getFieldItem("E0122"));
									E01_DISPLAYID=0;
								}
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										FieldItem item =  DataDictionary.getFieldItem("E01A1");
										if(POS_LEN>0) {
                                            item.setVisible(true);
                                        } else {
                                            item.setVisible(false);
                                        }
										list.add(item);	
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												FieldItem item =  DataDictionary.getFieldItem("E01A1");
												if(POS_LEN>0) {
                                                    item.setVisible(true);
                                                } else {
                                                    item.setVisible(false);
                                                }
												list.add(item);	
												POS_DISPLAYID=0;
											}
										}else{
											FieldItem item =  DataDictionary.getFieldItem("E01A1");
											if(POS_LEN>0) {
                                                item.setVisible(true);
                                            } else {
                                                item.setVisible(false);
                                            }
											list.add(item);	
											POS_DISPLAYID=0;
										}
									}
								}
							}
							if(!fieldset.isMainset()&&A01_DISPLAYID>0){
								list.add(DataDictionary.getFieldItem("A0101"));
								A01_DISPLAYID=0;
							}
						}
										
						StringBuffer format=new StringBuffer();	
						if("N".equals(fielditem.getItemtype())){
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								fielditem.setFormat("####."+format.toString());
							}else{
								fielditem.setFormat("####");
							}
						}
						if(displaywidth<1){
							fielditem.setVisible(false);
						}else{
							fielditem.setVisible(true);
						}
						list.add(fielditem);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemListvalue(FieldSet fieldset){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		String setname = fieldset.getFieldsetid();
		int UNIT_DISPLAYID = 1;
		int POS_DISPLAYID = 1;
		int POS_LEN = 14;
		int UNIT_LEN = 14;
		int E01_DISPLAYID = 1;
		int A01_DISPLAYID = 1;

		if(fieldset.isMainset()){
			String UNIT_DISPLAYID_str = getValues("UNIT_DISPLAYID");
				UNIT_DISPLAYID=UNIT_DISPLAYID_str!=null&&UNIT_DISPLAYID_str.trim().length()>0?Integer.parseInt(UNIT_DISPLAYID_str):1;
			String POS_DISPLAYID_str = getValues("POS_DISPLAYID");
				POS_DISPLAYID=POS_DISPLAYID_str!=null&&POS_DISPLAYID_str.trim().length()>0?Integer.parseInt(POS_DISPLAYID_str):1;
			String UNIT_LEN_str = getValues("UNIT_LEN");
				UNIT_LEN=UNIT_LEN_str!=null&&UNIT_LEN_str.trim().length()>0?Integer.parseInt(UNIT_LEN_str):14;
			String POS_LEN_str =getValues("POS_LEN");
				POS_LEN=POS_LEN_str!=null&&POS_LEN_str.trim().length()>0?Integer.parseInt(POS_LEN_str):14;
		}
		ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				FieldItem fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						int displayid = rs.getInt("displayid");
						if("A".equalsIgnoreCase(setname.substring(0,1))){
							if(UNIT_DISPLAYID>POS_DISPLAYID){
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								
							}else{
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
							}
							if(!fieldset.isMainset()&&A01_DISPLAYID>0){
								list.add(fieldA0101());
								A01_DISPLAYID=0;
							}
						}
						
						Field field=fielditem.cloneField();					
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
						    field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						field.setReadonly(false);
						if(displaywidth<1){
							field.setVisible(false);
						}else{
							field.setVisible(true);
						}
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList itemList1(FieldSet fieldset){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select itemid,displaywidth,displayid,reserveitem from fielditem where fieldsetid='");
		buf.append(fieldset.getFieldsetid());
		buf.append("' and useflag=1 order by displayid");
		
		String setname = fieldset.getFieldsetid();
		int UNIT_DISPLAYID = 1;
		int POS_DISPLAYID = 1;
		int POS_LEN = 14;
		int UNIT_LEN = 14;
		int E01_DISPLAYID = 1;
		int A01_DISPLAYID = 1;

		if(fieldset.isMainset()){
			String UNIT_DISPLAYID_str = getValues("UNIT_DISPLAYID");
				UNIT_DISPLAYID=UNIT_DISPLAYID_str!=null&&UNIT_DISPLAYID_str.trim().length()>0?Integer.parseInt(UNIT_DISPLAYID_str):1;
			String POS_DISPLAYID_str = getValues("POS_DISPLAYID");
				POS_DISPLAYID=POS_DISPLAYID_str!=null&&POS_DISPLAYID_str.trim().length()>0?Integer.parseInt(POS_DISPLAYID_str):1;
			String UNIT_LEN_str = getValues("UNIT_LEN");
				UNIT_LEN=UNIT_LEN_str!=null&&UNIT_LEN_str.trim().length()>0?Integer.parseInt(UNIT_LEN_str):14;
			String POS_LEN_str =getValues("POS_LEN");
				POS_LEN=POS_LEN_str!=null&&POS_LEN_str.trim().length()>0?Integer.parseInt(POS_LEN_str):14;
		}
		ContentDAO dao=new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				FieldItem fielditem = DataDictionary.getFieldItem(rs.getString("itemid"));
				if(fielditem!=null){
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
							||!"0".equals(fieldset.getChangeflag())){
						int displaywidth = rs.getInt("displaywidth");
						int displayid = rs.getInt("displayid");
						if("A".equalsIgnoreCase(setname.substring(0,1))){
							if(UNIT_DISPLAYID>POS_DISPLAYID){
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								
							}else{
								if(UNIT_DISPLAYID>0&&UNIT_DISPLAYID<=displayid){
									list.add(fieldB0110(UNIT_LEN));
									UNIT_DISPLAYID=0;
								}
								if(!fieldset.isMainset()&&E01_DISPLAYID>0){
									list.add(fieldE0122());
									E01_DISPLAYID=0;
								}
								if(POS_DISPLAYID>0&&POS_DISPLAYID<=displayid){
									if(POS_DISPLAYID>1){
										list.add(fieldE01A1(POS_LEN));
										POS_DISPLAYID=0;
									}else{
										if(fieldset.isMainset()){
											if("e0122".equalsIgnoreCase(fielditem.getItemid())){
												list.add(fieldE01A1(POS_LEN));
												POS_DISPLAYID=0;
											}
										}else{
											list.add(fieldE01A1(POS_LEN));
											POS_DISPLAYID=0;
										}
									}
								}
							}
							if(!fieldset.isMainset()&&A01_DISPLAYID>0){
								list.add(fieldA0101());
								A01_DISPLAYID=0;
							}
						}
						
						Field field=fielditem.cloneField();					
						StringBuffer format=new StringBuffer();	
						field.setLength(fielditem.getItemlength());
						field.setCodesetid(fielditem.getCodesetid());
						if("N".equals(fielditem.getItemtype())){
						    field.setDecimalDigits(fielditem.getDecimalwidth());
							if(fielditem.getDecimalwidth()>0){
								for(int j=0;j<fielditem.getDecimalwidth();j++){
									format.append("#");	
								}
								field.setFormat("####."+format.toString());
							}else{
								field.setFormat("####");
							}
						}
						field.setDatatype(getColumType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
						field.setReadonly(false);
						if(displaywidth<1){
							field.setVisible(false);
						}else{
							field.setVisible(true);
						}
						String reserveitem = rs.getString("reserveitem");
						reserveitem=reserveitem!=null&&reserveitem.trim().length()>0?reserveitem:"0";
						if("1".equals(reserveitem)){
//							field.setLabel(field.getLabel()+"<font color='red'>*</font>");
							field.setLabel(field.getLabel());
						}
						list.add(field);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public int getColumType(String type,int decimalwidth) {
		int temp=1;
		
		if("A".equals(type)){
			temp=DataType.STRING;
		}else if("D".equals(type)){
			temp=DataType.DATE;
		}else if("N".equals(type)){
				temp=DataType.FLOAT;
		}else if("M".equals(type)){
			temp=DataType.CLOB;
		}else{
			temp=DataType.STRING;
		}
		
		return temp;
	}
	/**
	 * 添加到数据库
	 * @param table //表名
	 * @param itemid //主集id
	 * @param setvalue //单位或职位值
	 * @param currI9999 //i9999序号
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet(String table,String itemid,String setvalue,String currI9999)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		try{
			ArrayList list=new ArrayList();
			list.add(setvalue);			
			ContentDAO dao=new ContentDAO(conn);	
			if(currI9999!=null&&!"".equalsIgnoreCase(currI9999)&&!"0".equalsIgnoreCase(currI9999)){
				/**把当前记录以后的记录中I9999+1*/
				buf.append("update ");
				buf.append(table);
				buf.append(" set I9999=I9999+1");
				buf.append(" where "+itemid+"=? and I9999>=");
				buf.append(currI9999);
				dao.update(buf.toString(), list);
				stri9999=currI9999;
			}else{//当前记录为空时
				/**取得子集记录最大值*/
				buf.append("select max(i9999) i9999 from ");
				buf.append(table);
				buf.append(" where "+itemid+"=?");
				RowSet rset=dao.search(buf.toString(),list);
				if(rset.next()) {
                    stri9999=String.valueOf(rset.getInt("i9999")+1);
                }
			}
			/**先插入子集记录*/
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(table);
			buf.append("("+itemid+",I9999) values(?,?)");
			list.clear();
			list.add(setvalue);
			list.add(stri9999);
			dao.update(buf.toString(), list);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 添加到数据库
	 * @param table //表名
	 * @param itemid //主集id
	 * @param setvalue //单位或职位值
	 * @param currI9999 //i9999序号
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet(String table,String itemid,String setvalue,String currI9999,String sp_flag)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		try{
			ArrayList list=new ArrayList();
			list.add(setvalue);			
			ContentDAO dao=new ContentDAO(conn);	
			if(currI9999!=null&&!"".equalsIgnoreCase(currI9999)&&!"0".equalsIgnoreCase(currI9999)){
				/**把当前记录以后的记录中I9999+1*/
				buf.append("update ");
				buf.append(table);
				buf.append(" set I9999=I9999+1");
				buf.append(" where "+itemid+"=? and I9999>=");
				buf.append(currI9999);
				dao.update(buf.toString(), list);
				stri9999=currI9999;
			}else{//当前记录为空时
				/**取得子集记录最大值*/
				buf.append("select max(i9999) i9999 from ");
				buf.append(table);
				buf.append(" where "+itemid+"=?");
				RowSet rset=dao.search(buf.toString(),list);
				if(rset.next()) {
                    stri9999=String.valueOf(rset.getInt("i9999")+1);
                }
			}
			/**先插入子集记录*/
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(table);
			buf.append("("+itemid+",I9999,"+sp_flag+") values(?,?,?)");
			list.clear();
			list.add(setvalue);
			list.add(stri9999);
			list.add("01");
			dao.update(buf.toString(), list);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 添加到数据库
	 * @param table //表名
	 * @param itemid //主集id
	 * @param setvalue //单位或职位值
	 * @param currI9999 //i9999序号
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  void insertMaintSubSet(String table,String itemid,String setvalue)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		try{
			ArrayList list=new ArrayList();
			list.add(setvalue);			
			ContentDAO dao=new ContentDAO(conn);	
			buf.append("insert into ");
			buf.append(table);
			buf.append("("+itemid+") values(?)");
			list.clear();
			list.add(setvalue);
			dao.update(buf.toString(), list);
		}catch(Exception ex){
			if(setvalue==null||setvalue.length()==0){
				throw GeneralExceptionHandler.Handle(new Exception("请选择左侧机构树!"));
			}else{
				ex.printStackTrace();			
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if("B01".equalsIgnoreCase(fielditem.getFieldsetid())) {
                    throw GeneralExceptionHandler.Handle(new Exception("新增的单位或部门已经存在!"));
                } else {
                    throw GeneralExceptionHandler.Handle(new Exception("新增的职位已经存在!"));
                }
			}
		}
	}
	/**
	 * 添加到数据库
	 * @param table //表名
	 * @param itemid //主集id
	 * @param setvalue //单位或职位值
	 * @param currI9999 //i9999序号
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  void insertMaintSubSet(String table,String itemid,String setvalue,
			String e0122name,String e0122value,String infor)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		try{
			if("2".equals(infor)){
				ArrayList list=new ArrayList();
				list.add(setvalue);			
				ContentDAO dao=new ContentDAO(conn);	
				buf.append("insert into ");
				buf.append(table);
				buf.append("("+itemid+") values(?)");
				dao.update(buf.toString(), list);
			}else{
				ArrayList list=new ArrayList();
				list.add(setvalue);	
				list.add(e0122value);
				ContentDAO dao=new ContentDAO(conn);	
				buf.append("insert into ");
				buf.append(table);
				buf.append("("+itemid+","+e0122name+") values(?,?)");
				dao.update(buf.toString(), list);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
			if("2".equals(infor)) {
                throw GeneralExceptionHandler.Handle(new Exception("新增的单位或部门已经存在！"));
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("新增的职位已经存在!"));
            }
		}
	}
	/**
	 * 添加到数据库
	 * @param table //表名
	 * @param itemid //主集id
	 * @param setvalue //单位或职位值
	 * @param currI9999 //i9999序号
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  void insertMaintSubSet(String table,String itemid,String setvalue,String sp_flag)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		try{
			ArrayList list=new ArrayList();
			list.add(setvalue);			
			ContentDAO dao=new ContentDAO(conn);	
			buf.append("insert into ");
			buf.append(table);
			buf.append("("+itemid+","+sp_flag+") values(?,?)");
			list.clear();
			list.add(setvalue);
			list.add("01");
			dao.update(buf.toString(), list);
		}catch(Exception ex){
			ex.printStackTrace();
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if("B01".equalsIgnoreCase(fielditem.getFieldsetid())) {
                throw GeneralExceptionHandler.Handle(new Exception("新增的单位或部门已经存在！"));
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("新增的职位已经存在!"));
            }
		}
	}
	/**
	 * 
	 * @param table
	 * @param itemid
	 * @param setvalue
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet2(String table,String itemid,String setvalue,String checkadd)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		try{
			ContentDAO dao=new ContentDAO(conn);
			/**判断主集中是否有这条记录*/
			buf.append("select B0110 from ");
			buf.append("B01");
			buf.append(" where B0110='"+setvalue+"'");
			RowSet rset1=dao.search(buf.toString());
			String itemvalue="";
			if(rset1.next()) {
                itemvalue=rset1.getString(itemid);
            }
			itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
			if(itemvalue.trim().length()<1){
				insertMaintSubSet("B01","B0110",setvalue);
			}
			
			
			/**取得子集记录最大值*/
			buf.setLength(0);
			buf.append("select max(i9999) i9999 from ");
			buf.append(table);
			buf.append(" where "+itemid+"=?");
			ArrayList list=new ArrayList();
			list.add(setvalue);
			
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
			
			/**先插入子集记录*/
			if("1".equals(checkadd)){
				if("1".equals(stri9999)){
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(table);
					buf.append("("+itemid+",I9999) values(?,?)");
					list.clear();
					list.add(setvalue);
					list.add(stri9999);
					dao.update(buf.toString(), list);
				}
			}else{
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(table);
				buf.append("("+itemid+",I9999) values(?,?)");
				list.clear();
				list.add(setvalue);
				list.add(stri9999);
				dao.update(buf.toString(), list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 
	 * @param table
	 * @param itemid
	 * @param setvalue
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet2(String table,String itemid,String setvalue,String checkadd,String infor)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		String mainitem = "";
		String mainset = "";
		if("2".equals(infor)){
			mainitem = "B0110";
			mainset = "B01";
		}else if("3".equals(infor)){
			mainitem = "E01A1";
			mainset = "K01";
		}
		
		try{
			ContentDAO dao=new ContentDAO(conn);
			/**判断主集中是否有这条记录*/
			buf.append("select "+mainitem+" from ");
			buf.append(mainset);
			buf.append(" where "+mainitem+"='"+setvalue+"'");
			RowSet rset1=dao.search(buf.toString());
			String itemvalue="";
			if(rset1.next()) {
                itemvalue=rset1.getString(itemid);
            }
			itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
			if(itemvalue.trim().length()<1){
				insertMaintSubSet(mainset,mainitem,setvalue);
			}
			
			
			/**取得子集记录最大值*/
			buf.setLength(0);
			buf.append("select max(i9999) i9999 from ");
			buf.append(table);
			buf.append(" where "+itemid+"=?");
			ArrayList list=new ArrayList();
			list.add(setvalue);
			
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
			
			/**先插入子集记录*/
			if("1".equals(checkadd)){
				if("1".equals(stri9999)){
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(table);
					buf.append("("+itemid+",I9999) values(?,?)");
					list.clear();
					list.add(setvalue);
					list.add(stri9999);
					dao.update(buf.toString(), list);
				}
			}else{
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(table);
				buf.append("("+itemid+",I9999) values(?,?)");
				list.clear();
				list.add(setvalue);
				list.add(stri9999);
				dao.update(buf.toString(), list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 
	 * @param table
	 * @param itemid
	 * @param setvalue
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet2(String table,String itemid,String setvalue,String e0122item,
			String e0122value,String checkadd,String infor)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		String mainitem = "";
		String mainset = "";
		if("2".equals(infor)){
			mainitem = "B0110";
			mainset = "B01";
		}else if("3".equals(infor)){
			mainitem = "E01A1";
			mainset = "K01";
		}
		
		try{
			ContentDAO dao=new ContentDAO(conn);
			/**判断主集中是否有这条记录*/
			buf.append("select "+mainitem+" from ");
			buf.append(mainset);
			buf.append(" where "+mainitem+"='"+setvalue+"'");
			RowSet rset1=dao.search(buf.toString());
			String itemvalue="";
			if(rset1.next()) {
                itemvalue=rset1.getString(itemid);
            }
			itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
			if(itemvalue.trim().length()<1){
				insertMaintSubSet(mainset,mainitem,setvalue,e0122item,e0122value,infor);
			}
			
			
			/**取得子集记录最大值*/
			buf.setLength(0);
			buf.append("select max(i9999) i9999 from ");
			buf.append(table);
			buf.append(" where "+itemid+"=?");
			ArrayList list=new ArrayList();
			list.add(setvalue);
			
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
			
			/**先插入子集记录*/
			if("1".equals(checkadd)){
				if("1".equals(stri9999)){
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(table);
					buf.append("("+itemid+",I9999) values(?,?)");
					list.clear();
					list.add(setvalue);
					list.add(stri9999);
					dao.update(buf.toString(), list);
				}
			}else{
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(table);
				buf.append("("+itemid+",I9999) values(?,?)");
				list.clear();
				list.add(setvalue);
				list.add(stri9999);
				dao.update(buf.toString(), list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 
	 * @param table
	 * @param itemid
	 * @param setvalue
	 * @param dbconn
	 * @return
	 * @throws GeneralException
	 */
	public  String insertSubSet1(String table,String itemid,String setvalue,String sp_flag)throws GeneralException{
		StringBuffer buf=new StringBuffer();
		String stri9999="1";
		try{
			ContentDAO dao=new ContentDAO(conn);
			/**判断主集中是否有这条记录*/
			buf.append("select B0110 from ");
			buf.append("B01");
			buf.append(" where B0110='"+setvalue+"'");
			RowSet rset1=dao.search(buf.toString());
			String itemvalue="";
			if(rset1.next()) {
                itemvalue=rset1.getString(itemid);
            }
			itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"";
			if(itemvalue.trim().length()<1){
				insertMaintSubSet("B01","B0110",setvalue);
			}

			/**取得子集记录最大值*/
			buf.setLength(0);
			buf.append("select max(i9999) i9999 from ");
			buf.append(table);
			buf.append(" where "+itemid+"=?");
			ArrayList list=new ArrayList();
			list.add(setvalue);
			
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
			
			
			FieldSet fs = DataDictionary.getFieldSetVo(table);
			/** 取得次数*/
			String strnum ="1";
			String date = DateStyle.getSystemTime();
			String year = date.substring(0, 4);
			//判断子集变化类型（一般变化还是年或月变化） gdd 2013-12-07
			if(!"0".equals(fs.getChangeflag())){
				buf.setLength(0);
	            buf.append("select max("+table+"z1) num from ");
	            buf.append(table);
	            buf.append(" where "+itemid+"=? and "+Sql_switcher.year(table+"z0")+" =? ");
	            list.clear();
	            list.add(setvalue);
	            list.add(year);
	            rset=dao.search(buf.toString(),list);
				if(rset.next()) {
                    strnum=String.valueOf(rset.getInt("num")+1);
                }
			}
			
			/**先插入子集记录*/
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(table);
			buf.append(" ("+itemid+",I9999,"+sp_flag);
			if(!"0".equals(fs.getChangeflag())) {
                buf.append(" ,"+table+"z0,"+table+"z1");
            }
			buf.append(" ) values(?,?,?");
			if(!"0".equals(fs.getChangeflag())) {
                buf.append(" ,"+Sql_switcher.dateValue(date.substring(0, 8)+"01")+",?");
            }
			buf.append(" )");
			list.clear();
			list.add(setvalue);
			list.add(stri9999);
			list.add("01");
			if(!"0".equals(fs.getChangeflag())) {
                list.add(strnum);
            }
			dao.update(buf.toString(), list);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return stri9999;
	}
	/**
	 * 获得排序后面的字符串
	 * @param sort_fields
	 * @return
	 */
	public String getoMianOrderbyStr(String sort_fields) 
	{
		StringBuffer fieldsb = new StringBuffer();
		if(sort_fields!=null)
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				String[] arr = temps[i].split(":");
				String sortmode = "";
				if("1".equalsIgnoreCase(arr[2]))
				{
					sortmode = "asc";
				}else{
					sortmode = "desc";
				}
				fieldsb.append(","+arr[0]+" "+sortmode+" ");
			}
		}
		return fieldsb.substring(1).toString();
	}
	/**
	 * 获得排序后面的字符串
	 * @param sort_fields
	 * @return
	 */
	public boolean checkOrderbyStr(String sort_fields,String fieldsetid) 
	{
		boolean chk = false;
		if(sort_fields!=null)
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				String[] arr = temps[i].split(":");
				FieldItem tempitem=DataDictionary.getFieldItem(arr[0]);
				if(tempitem!=null){
					if(!tempitem.getFieldsetid().equalsIgnoreCase(fieldsetid)){
						chk = true;
						break;
					}
				}
			}
		}
		return chk;
	}
	public Field fieldB0110(int len){
		FieldItem tempitem=DataDictionary.getFieldItem("B0110");
		Field tempfield=tempitem.cloneField();
		if(len>0) {
            tempfield.setVisible(true);
        } else {
            tempfield.setVisible(false);
        }
		return tempfield;
	}
	public Field fieldE01A1(int len){
		FieldItem tempitem=DataDictionary.getFieldItem("E01A1");
		Field tempfield= null;
		if(tempitem!=null){
			tempfield=tempitem.cloneField();
			if(len>0) {
                tempfield.setVisible(true);
            } else {
                tempfield.setVisible(false);
            }
		}else{
			tempfield=new Field("E01A1","职位名称");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setCodesetid("@K");
			tempfield.setLength(50);
			tempfield.setReadonly(false);			
			if(len>0) {
                tempfield.setVisible(true);
            } else {
                tempfield.setVisible(false);
            }
		}
		return tempfield;
	}
	public Field fieldE0122(){
		FieldItem tempitem=DataDictionary.getFieldItem("E0122");
		Field tempfield=null;
		if(tempitem!=null) {
            tempfield=tempitem.cloneField();
        } else{
			tempfield=new Field("E01A1","部门");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setCodesetid("UM");
			tempfield.setLength(50);
			tempfield.setReadonly(false);
		}
		return tempfield;
	}
	public Field fieldA0101(){
		FieldItem tempitem=DataDictionary.getFieldItem("A0101");
		Field tempfield=tempitem.cloneField();
		tempfield.setVisible(true);
		return tempfield;
	}
	
	public String getValues(String contant){
		String values = "1";
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant",contant);
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
                values=vo.getString("str_value");
            }
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			insertValues(contant,values);
		//	e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			insertValues(contant,values);
		//	e.printStackTrace();
		}
		return values;
	}
	public String setValues(String contant,String strValue){
		String values = "14";
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant",contant);
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			vo = dao.findByPrimaryKey(vo);
			vo.setString("str_value",strValue);
			dao.updateValueObject(vo);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			insertValues(contant,values);
	//		e.printStackTrace();
		}
		return values;
	}
	public String insertValues(String contant,String strValue){
		String values = "1";
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			dao.update("insert into constant(constant,str_value) values('"+contant+"','"+strValue+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return values;
	}
	/**取得i9999*/
	 public String getI9999(String table,String itemid,String itemvalue)
	 {
		String i9999="";
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strSql = new StringBuffer();
		strSql.append("select ");
		strSql.append(Sql_switcher.isnull("max(i9999)","0"));
		strSql.append(" from "+table);
		strSql.append(" where " );
		strSql.append(itemid+"='");
		strSql.append(itemvalue);
		strSql.append("'");
		int count=1;
		try
		{
		    RowSet rs = dao.search(strSql.toString());
		    if(rs.next()) {
                count = rs.getInt(1)+1;
            }
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		i9999=new Integer(count).toString();	
		return i9999;	
	}
	/**
	 * 加载数据的页面
	 * @param pageFlag 加载数据的页面 =bz：编制管理 
	 */
    public void setPageFlag(String pageFlag) {
        this.pageFlag = pageFlag;
    }
	 
	
}
