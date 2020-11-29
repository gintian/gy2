package com.hjsj.hrms.businessobject.general.inform;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class BatchBo {
        
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String backdate = sdf.format(new Date());
	private UserView userView;
	private String entranceFlag="0";//进入模块标志=1从工资管理的基础数据维护进入，默认为0从其他模块进入
	private String computeScope="0";//编制管理进入时 计算范围控制标识 1为所有下级，0为下一级
	//登录用户名
	private String userName;
	public BatchBo(UserView uv){
		this.userView=uv;
	}
	public BatchBo(){}
	/**
	 * 生成临时表名称
	 * @param userView
	 * @return
	 */
	public String getTempTable(UserView userView){
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
            return "##temp_" + userView.getUserName();
        } else {
            return "temp_" + userView.getUserName();
        }
	}
	
	
	
	public String codeWhere(String setname,String dbname,String a_code,String infor,Connection conn){
		StringBuffer sqlstr = new StringBuffer();
		if("1".equals(infor)){
			sqlstr.append("A0100 in (select A0100 from ");
			sqlstr.append(dbname+"A01  ");
			if(a_code!=null&&a_code.trim().length()>0){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.length() > 2 ? a_code.substring(2) : "";
				if(value.length()>0){
					sqlstr.append(" where ");
					if("UN".equalsIgnoreCase(codesetid)){
						sqlstr.append("B0110 like '");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sqlstr.append("E0122 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("@K".equalsIgnoreCase(codesetid)){
						sqlstr.append("E01A1 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else{
						String[] codearr =a_code.split(":");
						if(codearr.length==3){
							sqlstr.append(codearr[1].toUpperCase()+"= '");
							sqlstr.append(codearr[2]);
							sqlstr.append("' ");
						}
					}
				}
			}
			sqlstr.append(")");
		}else if("2".equals(infor)){
			String codearr[] = a_code.split(":");
			if(codearr.length==3){
				sqlstr.append("B0110 in (select B0110 from B01 where ");
				sqlstr.append(codearr[1].toUpperCase()+"= '");
				sqlstr.append(codearr[2]);
				sqlstr.append(") ");
			}else{
				PosparameXML pos = new PosparameXML(conn);  
				String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
				ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
				
				String levelnext = pos.getValue(PosparameXML.AMOUNTS,"nextlevel"); 
				levelnext=levelnext!=null&&levelnext.trim().length()>0?levelnext:"0";
				
				sqlstr.append("B0110 in (select codeitemid from organization ");
				
				if(a_code!=null&&a_code.trim().length()>1){
					if("1".equals(this.entranceFlag))
					{
				    	/**不管机构树定位到哪，都是计算管理范围内的 李振伟 2010-06-12*/
                        sqlstr.append(" where ");
                        String mcode=this.userView.getManagePrivCode();
                        String mcodev=this.userView.getManagePrivCodeValue();
                        if(mcode!=null&&!"".equals(mcode))
                        {
                        	sqlstr.append(" codeitemid like '"+(mcodev==null?"":mcodev)+"%'");
                        }
                        else
                        {
                        	sqlstr.append(" 1=2 ");
                        }
                      sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					}else
					{
			    		String value=a_code.length() > 2 ? a_code.substring(2) : "";
			    		if(value.length()>0){
				    		sqlstr.append(" where ");
				    		sqlstr.append("parentid='");
				    		sqlstr.append(value);
				    		sqlstr.append("'");
				    		sqlstr.append(" and parentid<>codeitemid and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				    	}else{
				    		sqlstr.append(" where ");
				    		sqlstr.append("parentid=codeitemid");
				    		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
	    				}
					}
				}else{
					sqlstr.append(" where ");
					sqlstr.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
					sqlstr.append(this.doInitOrgUnit("codeitemid","0"));
				}
				if("0".equals(ctrl_type)){//控制部门
					sqlstr.append(" and codesetid='UN'");
				}
				if("1".equals(levelnext)){//控制下级
					String value=a_code.length() > 2 ? a_code.substring(2) : "";
					if(StringUtils.isBlank(value)|| "UN`".equalsIgnoreCase(value)){
						value=this.userView.getUnit_id();
					}else {
						value = PubFunc.getTopOrgDept(value);
						value=value.split("`")[0];
					}

					if(value.replace("`", "").length()<=3){
						sqlstr.append(" and grade<=2");
					}else{
						sqlstr.append(" and grade<=(select grade+1 from organization where codeitemid='"+value+"')");
					}
					
				}
				sqlstr.append(")");
			}
		}else if("3".equals(infor)){
			sqlstr.append("E01A1 in (select E01A1 from K01");
			if(a_code!=null&&a_code.trim().length()>1){
				String codearr[] = a_code.split(":");
				if(codearr.length==3){
					sqlstr.append(" where ");
					sqlstr.append(codearr[1].toUpperCase()+"= '");
					sqlstr.append(codearr[2]);
					sqlstr.append("' ");
				}else{
					String value=a_code.length() > 2 ? a_code.substring(2) : "";
					if(value.length()>0){
						sqlstr.append(" where ");
						sqlstr.append("E0122 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}
				}
			}
			sqlstr.append(")");
		}else if("4".equals(infor)){
			sqlstr.append("B0110 in (select codeitemid from organization ");
			if(a_code!=null&&a_code.trim().length()>1){
				String value=a_code.length() > 2 ? a_code.substring(2) : "";
				if(value.length()>0){
					sqlstr.append(" where ");
					sqlstr.append("codeitemid like '");
					sqlstr.append(value);
					sqlstr.append("%'");
				}
			}
			sqlstr.append(")");
		}else if("5".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>1){
				sqlstr.append("R4502='");
				sqlstr.append(a_code);
				sqlstr.append("'");
			}
		}else{
			sqlstr.append(" 1=1 ");
		}
		
		return sqlstr.toString();
	}
	public String codeWhereStr(String dbname,String a_code,String infor){
		StringBuffer sqlstr = new StringBuffer();
		if("1".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>0){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" and A0100 in(select A0100 from ");
					sqlstr.append(dbname);
					sqlstr.append("A01 where ");
					if("UN".equalsIgnoreCase(codesetid)){
						sqlstr.append(" B0110 like '");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sqlstr.append(" E0122 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("@K".equalsIgnoreCase(codesetid)){
						sqlstr.append(" E01A1 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else{
						String[] codearr =a_code.split(":");
						if(codearr.length==3){
							sqlstr.append(" "+codearr[1].toUpperCase()+"= '");
							sqlstr.append(codearr[2]);
							sqlstr.append("'");
						}else{
							sqlstr.append("1=1");
						}
					}
					sqlstr.append(")");
				}
			}
		}else if("2".equals(infor)){
			sqlstr.append(" and B0110 in(select codeitemid from organization ");
			if(a_code!=null&&a_code.trim().length()>1){
				if("1".equals(this.entranceFlag))
				{
    				/**不管机构树定位到哪，都是计算管理范围内的 李振伟 2010-06-12*/
                    sqlstr.append(" where ");
                    String mcode=this.userView.getManagePrivCode();
                    String mcodev=this.userView.getManagePrivCodeValue();
                    if(mcode!=null&&!"".equals(mcode))
                    {
                    	sqlstr.append(" codeitemid like '"+(mcodev==null?"":mcodev)+"%'");
                    }
                    else
                    {
                    	sqlstr.append(" 1=2 ");
                    }
                   sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				}else{
	    			String value=a_code.substring(2);
	    			String andstr = " ";
		    		if(value.length()>0){
		    			sqlstr.append(" where parentid ");
		    			
		    			// 按计算范围控制 computeScope
		    			if("1".equalsIgnoreCase(this.computeScope)){
		    				sqlstr.append(" like '"+value+"%");
		    			    andstr = " and parentid<>codeitemid ";
		    			}else{
		    			    sqlstr.append(" = '"+value);
		    			    andstr = " and parentid<>codeitemid ";
		    			}
		    			
			    		sqlstr.append("'");
			    		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			    		sqlstr.append(andstr);
		    		}else{
			    		sqlstr.append(" where ");
			    		
			    		if("1".equalsIgnoreCase(this.computeScope)) {
                            sqlstr.append(" 1=1 ");
                        } else {
                            sqlstr.append("parentid=codeitemid");
                        }
			    		
			    		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		    		}
				}
			}else{
				sqlstr.append(" where ");
				sqlstr.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
				if(!this.userView.isSuper_admin()) {
                    sqlstr.append(this.doInitOrgUnit("codeitemid","0"));
                }
			}
			sqlstr.append(")");
		}else if("3".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>1){
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" and E01A1 like'");
					sqlstr.append(value);
					sqlstr.append("%' ");
				}
			}
		}else{
			sqlstr.append(" and 1=1 ");
		}
		
		return sqlstr.toString();
	}
	public String codeWhereStr(String dbname,String a_code,String infor,String tablename){
		StringBuffer sqlstr = new StringBuffer();
		if("1".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>0){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" and A0100 in(select A0100 from ");
					sqlstr.append(dbname);
					sqlstr.append("A01 where ");
					if("UN".equalsIgnoreCase(codesetid)){
						sqlstr.append(tablename+".B0110 like '");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sqlstr.append(tablename+".E0122 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("@K".equalsIgnoreCase(codesetid)){
						sqlstr.append(tablename+".E01A1 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else{
						String[] codearr =a_code.split(":");
						if(codearr.length==3){
							sqlstr.append(" "+codearr[1].toUpperCase()+"= '");
							sqlstr.append(codearr[2]);
							sqlstr.append("'");
						}else{
							sqlstr.append("1=1");
						}
					}
					sqlstr.append(")");
				}
			}
		}else if("2".equals(infor)){
			sqlstr.append(" and "+tablename+".B0110 in(select codeitemid from organization ");
			if(a_code!=null&&a_code.trim().length()>1){
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" where parentid='");
					sqlstr.append(value);
					sqlstr.append("'");
				}
			}
			sqlstr.append(")");
		}else if("3".equals(infor)){
			if(a_code!=null&&a_code.trim().length()>1){
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" and "+tablename+".E01A1 like'");
					sqlstr.append(value);
					sqlstr.append("%' ");
				}
			}
		}else{
			sqlstr.append(" and 1=1 ");
		}
		
		return sqlstr.toString();
	}
	
	/**
	 * 批量修改单个指标的记录
	 * @param setname //子集名称id
	 * @param a_code  //公司或部门id
	 * @param itemid  //要修改的记录指标的id
	 * @param updatevalue //要修改的记录
	 * @return check
	 * @throws GeneralException 
	 */
	public boolean alertUpdate(Connection conn,UserView userView,String setname,String a_code,String itemid
			,String updatevalue,String viewsearch,String dbname,String flagcheck,String infor,String history,String inforflag) throws GeneralException{
		boolean check=false;
		if(setname.trim().length()<1||itemid.trim().length()<1){
			return false;
		}
		updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:" ";
		ContentDAO dao = new ContentDAO(conn);
		String[] arr = itemid.split(":");
		if(arr.length!=3) {
            return false;
        }
		
		if(arr.length==3){
			FieldItem field = DataDictionary.getFieldItem(arr[0]);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			sqlstr.append(setname);
			sqlstr.append(" set ");
			
			boolean modTimeFlag = isExistField("ModTime", setname, conn);
			boolean modUserNameFlag = isExistField("ModUserName", setname, conn);
			if(modTimeFlag) {
                sqlstr.append(" ModTime=?, ");
            }
			
			if(modUserNameFlag) {
                sqlstr.append(" ModUserName=?, ");
            }
			
			sqlstr.append(arr[0].toUpperCase());
			sqlstr.append("=");
			int length = field.getItemlength();
			int decimalwidth = field.getDecimalwidth();
			if("1".equals(flagcheck)){
				if("N".equalsIgnoreCase(field.getItemtype())){
					if(field.getDecimalwidth()>0){
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.indexOf(".")!=-1){
							String[] temp=updatevalue.split("\\.");
							if(decimalwidth<temp[1].length()){						 
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
							}
							 if(temp[0].length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}else{
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
							 }
						}
					}else{
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.trim().length()>0&&updatevalue.indexOf(".")!=-1){
							updatevalue=updatevalue.substring(0,updatevalue.indexOf("."));
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}
					}
					sqlstr.append(arr[0].toUpperCase()+"+"+updatevalue);
				}
			}else if("0".equals(flagcheck)){
				if("N".equalsIgnoreCase(field.getItemtype())){
					if(field.getDecimalwidth()>0){
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.indexOf(".")!=-1){
							String[] temp=updatevalue.split("\\.");
							if(decimalwidth<temp[1].length()){						 
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
							}
							 if(temp[0].length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}else{
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
							 }
						}
					}else{
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.trim().length()>0&&updatevalue.indexOf(".")!=-1){
							updatevalue=updatevalue.substring(0,updatevalue.indexOf("."));
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}
					}
					sqlstr.append(arr[0].toUpperCase()+"-"+updatevalue);
				}
			}else if("2".equals(flagcheck) || "3".equals(flagcheck)){
				sqlstr.append("?");
			}
			StringBuffer buf = new StringBuffer();
			if("1".equals(infor)){
				sqlstr.append(" where A0100=?");
				buf.append("select A0100");
			}else if("2".equals(infor)){
				sqlstr.append(" where B0110=?");
				buf.append("select B0110");
			}else if("3".equals(infor)){
				sqlstr.append(" where E01A1=?");
				buf.append("select E01A1");
			}else if("4".equals(infor)){
				sqlstr.append(" where B0110=?");
				buf.append("select B0110");
			}else{
				sqlstr.append(" where A0100=?");
				buf.append("select A0100");
			}
			if(!field.isMainSet()){
				sqlstr.append(" and I9999=?");
				buf.append(",I9999");
			}
			buf.append(" from ");
			buf.append(setname);
			
			buf.append(" a where ");
			buf.append(codeWhere(setname,dbname,a_code,infor,conn));
			
			if("1".equals(viewsearch)){
				buf.append(" and A0100 in(select A0100 from ");
				buf.append(userView.getUserName()+dbname+"result)");
			}
			if(!"1".equals(history)&&!field.isMainSet()){
				buf.append(" and I9999=(select max(I9999) from "+setname);
				if("1".equals(infor)){
					buf.append(" where A0100=a.A0100");
				}else if("2".equals(infor)){
					buf.append(" where B0110=a.B0110");
				}else if("3".equals(infor)){
					buf.append(" where E01A1=a.E01A1");
				}else if("4".equals(infor)){
					buf.append(" where B0110=a.B0110");
				}else{
					buf.append(" where A0100=a.A0100");
				}
				buf.append(")");
			}
			
			  //外部培训，走培训模块的管理范围权限
            if("2".equals(inforflag)){
                String strWhr = getTrainManagePrivWhr(userView, dbname);
                if(strWhr != null && strWhr.length() > 0){
                    sqlstr.append(strWhr);
                    buf.append(strWhr);
                }
            }
            
			if("N".equalsIgnoreCase(field.getItemtype())){
				updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
				if(updatevalue.indexOf(".")!=-1){
					String[] temp=updatevalue.split("\\.");
					if(decimalwidth<temp[1].length()){						 
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
					}
					 if(temp[0].length()>length){
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
					 }
				}else{
					 if(updatevalue.length()>length){
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
					 }
				}
			}
			try {
			    Timestamp dateTime = new Timestamp((new Date()).getTime());
				RowSet rs = dao.search(buf.toString());
				ArrayList list = new ArrayList();
				while(rs.next()){
					ArrayList valuelist = new ArrayList();
					
					if(modTimeFlag) {
                        valuelist.add(dateTime);
                    }
		            
		            if(modUserNameFlag) {
                        valuelist.add(this.userName);
                    }
		            
					if("2".equals(flagcheck) || "3".equals(flagcheck)){
						if("D".equalsIgnoreCase(field.getItemtype())){
							if(updatevalue!=null&&updatevalue.trim().length()>0){
								Date date = DateUtils.getSqlDate(updatevalue,"yyyy-MM-dd");
								valuelist.add(date);
							}else{
								valuelist.add(null);
							}
						}else{
							valuelist.add(updatevalue);
						}
					}
					if("1".equals(infor)){
						valuelist.add(rs.getString("A0100"));
					}else if("2".equals(infor)){
						valuelist.add(rs.getString("B0110"));
					}else if("3".equals(infor)){
						valuelist.add(rs.getString("E01A1"));
					}else if("4".equals(infor)){
						valuelist.add(rs.getString("B0110"));
					}else{
						valuelist.add(rs.getString("A0100"));
					}
					if(!field.isMainSet()){
						valuelist.add(rs.getString("I9999"));
					}
					list.add(valuelist);
				}

				dao.batchUpdate(sqlstr.toString(),list);
				check=true;
			} catch (Exception e) {
				e.printStackTrace();
				if(e.toString().indexOf("大于")!=-1||e.toString().indexOf("算术溢出")!=-1){
					throw GeneralExceptionHandler.Handle(new Exception("【"+field.getItemdesc()+"】指标，超出了允许的长度!！"));
				}else{
					throw GeneralExceptionHandler.Handle(e);
				}				
			}
		}
		return check;
	}
	/**
	 * @throws GeneralException 
	 * 批量修改单个指标的记录
	 * @param conn  数据库连接
	 * @param setname　表名
	 * @param itemid　　修改的字段
	 * @param updatevalue　修改的值
	 * @param flagcheck　
	 * @param infor　　１．人员　２．单位　３．职位
	 * @param strid
	 * @return
	 */
	public boolean alertUpdate(Connection conn,String setname,String itemid
			,String updatevalue,String flagcheck,String infor,
			String strid,String inforflag,String dbname) throws GeneralException{
		boolean check=false;
		if(setname.trim().length()<1||itemid.trim().length()<1){
			return false;
		}
		updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:" ";
		ContentDAO dao = new ContentDAO(conn);
		String[] arr = itemid.split(":");
		if(arr.length!=3) {
            return false;
        }
		if(arr.length==3){
			FieldItem field = DataDictionary.getFieldItem(arr[0]);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			sqlstr.append(setname);
			sqlstr.append(" set ");
			boolean modTimeFlag = isExistField("ModTime", setname, conn);
            boolean modUserNameFlag = isExistField("ModUserName", setname, conn);
            
            if(modTimeFlag) {
                sqlstr.append(" ModTime=?, ");
            }
            
            if(modUserNameFlag) {
                sqlstr.append(" ModUserName=?, ");
            }
            
			sqlstr.append(arr[0].toUpperCase());
			sqlstr.append("=");
			int length = field.getItemlength();
			int decimalwidth = field.getDecimalwidth();
			if("1".equals(flagcheck)){
				if("N".equalsIgnoreCase(field.getItemtype())){
					if(field.getDecimalwidth()>0){
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.indexOf(".")!=-1){
							String[] temp=updatevalue.split("\\.");
							if(decimalwidth<temp[1].length()){						 
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
							}
							 if(temp[0].length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}else{
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
							 }
						}
					}else{
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.trim().length()>0&&updatevalue.indexOf(".")!=-1){
							updatevalue=updatevalue.substring(0,updatevalue.indexOf("."));
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}
					}
					sqlstr.append(arr[0].toUpperCase()+"+"+updatevalue);
				}
			}else if("0".equals(flagcheck)){
				if("N".equalsIgnoreCase(field.getItemtype())){
					if(field.getDecimalwidth()>0){
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.indexOf(".")!=-1){
							String[] temp=updatevalue.split("\\.");
							if(decimalwidth<temp[1].length()){						 
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
							}
							 if(temp[0].length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}else{
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
							 }
						}
					}else{
						updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
						if(updatevalue.trim().length()>0&&updatevalue.indexOf(".")!=-1){
							updatevalue=updatevalue.substring(0,updatevalue.indexOf("."));
							 if(updatevalue.length()>length){
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
							 }
						}
					}
					sqlstr.append(arr[0].toUpperCase()+"-"+updatevalue);
				}
			}else if("2".equals(flagcheck) || "3".equals(flagcheck)){
				sqlstr.append("?");
			}
			if("1".equals(infor)){
				sqlstr.append(" where A0100=?");
			}else if("2".equals(infor)){
				sqlstr.append(" where B0110=?");
			}else if("3".equals(infor)){
				sqlstr.append(" where E01A1=?");
			}else if("4".equals(infor)){
				sqlstr.append(" where B0110=?");
			}else{
				sqlstr.append(" where A0100=?");
			}
			if(!field.isMainSet()){
				sqlstr.append(" and I9999=?");
			}

			String[] arrid=strid.split("`");
			if("N".equalsIgnoreCase(field.getItemtype())){
				updatevalue=updatevalue!=null&&updatevalue.trim().length()>0?updatevalue:"0";
				if(updatevalue.indexOf(".")!=-1){
					String[] temp=updatevalue.split("\\.");
					if(decimalwidth<temp[1].length()){						 
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
					}
					 if(temp[0].length()>length){
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
					 }
				}else{
					 if(updatevalue.length()>length){
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
					 }
				}
			}
			try {
				ArrayList list = new ArrayList();
				Timestamp dateTime = new Timestamp((new Date()).getTime());
				for(int i=0;i<arrid.length;i++){
					String str = arrid[i];
					if(str==null||str.length()<1) {
                        continue;
                    }
					
					ArrayList valuelist = new ArrayList();
					if(modTimeFlag) {
                        valuelist.add(dateTime);
                    }
					
					if(modUserNameFlag) {
                        valuelist.add(this.userName);
                    }
					
					if("2".equals(flagcheck) || "3".equals(flagcheck)){
						if("D".equalsIgnoreCase(field.getItemtype())){
							if(updatevalue!=null&&updatevalue.trim().length()>0){
								Date date = DateUtils.getSqlDate(updatevalue,"yyyy-MM-dd");
								valuelist.add(date);
							}else{
								valuelist.add(null);
							}
						}else{
							valuelist.add(updatevalue);
						}
					}
					String id = "";
					String i9999 = "";
					if(field.isMainSet()){
						id=str;
					}else{
						String strarr[] = str.split(":");
						if(strarr.length!=2) {
                            continue;
                        }
						id = strarr[0];
						i9999 = strarr[1];
						if(i9999==null||i9999.length()<1) {
                            continue;
                        }
					}
					
//					if(infor.equals("1")){
//						valuelist.add(id);
//					}else if(infor.equals("2")){
//						valuelist.add(id);
//					}else if(infor.equals("3")){
//						valuelist.add(id);
//					}else{
						valuelist.add(id);
//					}
					if(!field.isMainSet()){
						valuelist.add(i9999);
					}
					list.add(valuelist);
				}

				 //外部培训，走培训模块的管理范围权限
	            if("2".equals(inforflag)){
	                String strWhr = getTrainManagePrivWhr(userView, dbname);
	                if(strWhr != null && strWhr.length() > 0) {
                        sqlstr.append(strWhr);
                    }
	            }
	            
				dao.batchUpdate(sqlstr.toString(),list);
				check=true;
			} catch (Exception e) {
				e.printStackTrace();
				if(e.toString().indexOf("大于")!=-1||e.toString().indexOf("算术溢出")!=-1){
					throw GeneralExceptionHandler.Handle(new Exception("【"+field.getItemdesc()+"】指标，超出了允许的长度!！"));
				}else{
					throw GeneralExceptionHandler.Handle(e);
				}	
			}
		}
		return check;
	}

	/**
	 * 批量修改多个指标的记录
	 * @param setname //子集名称id
	 * @param a_code  //公司或部门id
	 * @param itemid_arr  //要修改的记录指标集的id
	 * @param itemvalue_arr //要修改的记录集
	 * @return check
	 * @throws GeneralException 
	 */
	public boolean alertMoreUpdate(Connection conn,UserView userView,String setname,String a_code,ArrayList itemid_arr,
			ArrayList itemvalue_arr,String viewsearch,String dbname,String infor,String history,String inforflag) throws GeneralException{
		boolean check=false;
		if(itemid_arr.size()<1||setname.trim().length()<1){
			return false;
		}
		if(itemid_arr.size()!=itemvalue_arr.size()){
			return false;
		}
		String tablename = dbname+setname;
		
		FieldSet field = DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update ");
		sqlstr.append(tablename);
		sqlstr.append(" set ");
		boolean modTimeFlag = isExistField("ModTime", tablename, conn);
        boolean modUserNameFlag = isExistField("ModUserName", tablename, conn);
        if(modTimeFlag) {
            sqlstr.append(" ModTime=?, ");
        }
        
        if(modUserNameFlag) {
            sqlstr.append(" ModUserName=?, ");
        }
        
		for(int i=0;i<itemid_arr.size();i++){
			String itemid = (String)itemid_arr.get(i);
			sqlstr.append(itemid);
			sqlstr.append("=?");
			if(i+1<itemid_arr.size()){
				sqlstr.append(",");
			}
		}
		StringBuffer buf = new StringBuffer();
		if("1".equals(infor)){
			sqlstr.append(" where A0100=?");
			buf.append("select A0100");
		}else if("2".equals(infor)){
			sqlstr.append(" where B0110=?");
			buf.append("select B0110");
		}else if("3".equals(infor)){
			sqlstr.append(" where E01A1=?");
			buf.append("select E01A1");
		}else if("4".equals(infor)){
			sqlstr.append(" where B0110=?");
			buf.append("select B0110");
		}else{
			sqlstr.append(" where A0100=?");
			buf.append("select A0100");
		}
		if(!field.isMainset()){
			sqlstr.append(" and I9999=?");
			buf.append(",I9999");
		}
		buf.append(" from ");
		buf.append(tablename);
		
		buf.append(" a where ");
		//buf.append(codeWhere(tablename,dbname,a_code,infor,conn));
		String codevalue=userView.getManagePrivCodeValue();
		if(setname.startsWith("B")||setname.startsWith("b")){
			buf.append("b0110 like '"+codevalue+"%'");
		}else{
			buf.append("e01a1 like '"+codevalue+"%'");
		}
		/*if(viewsearch.equals("1")){
			buf.append(" and A0100 in(select A0100 from ");
			buf.append(userView.getUserName()+dbname+"result)");
		}*/
		if(!"1".equals(history)&&!field.isMainset()){
			buf.append(" and I9999=(select max(I9999) from "+tablename);
			if("1".equals(infor)){
				buf.append(" where A0100=a.A0100");
			}else if("2".equals(infor)){
				buf.append(" where B0110=a.B0110");
			}else if("3".equals(infor)){
				buf.append(" where E01A1=a.E01A1");
			}else if("4".equals(infor)){
				buf.append(" where B0110=a.B0110");
			}else{
				buf.append(" where A0100=a.A0100");
			}
			buf.append(")");
		}

		 //外部培训，走培训模块的管理范围权限
        if("2".equals(inforflag)){
            String strWhr = getTrainManagePrivWhr(userView, dbname);
            if(strWhr != null && strWhr.length() > 0){
                sqlstr.append(strWhr);
                buf.append(strWhr);
            }
        }
        
		try {
		    Timestamp dateTime = new Timestamp((new Date()).getTime());
			RowSet rs = dao.search(buf.toString());
			ArrayList list = new ArrayList();
			while(rs.next()){
				ArrayList valuelist = new ArrayList();
				if(modTimeFlag) {
                    valuelist.add(dateTime);
                }
				
				if(modUserNameFlag) {
                    valuelist.add(this.userName);
                }
                
				for(int i=0;i<itemvalue_arr.size();i++){
					String itemid = (String)itemid_arr.get(i);
					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
					String itemvalue = (String)itemvalue_arr.get(i);
					int length = fielditem.getItemlength();
					int decimalwidth = fielditem.getDecimalwidth();
					//整形的判断是参照alertUpdate这个方法
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(fielditem.getDecimalwidth()>0){
							itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"0";
							if(itemvalue.indexOf(".")!=-1){
								String[] temp=itemvalue.split("\\.");
								if(decimalwidth<temp[1].length()){						 
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
								}
								 if(temp[0].length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
								 }
							}else{
								 if(itemvalue.length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
								 }
							}
						}else{
							itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"0";
							if(itemvalue.trim().length()>0&&itemvalue.indexOf(".")!=-1){
								itemvalue=itemvalue.substring(0,itemvalue.indexOf("."));
								 if(itemvalue.length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
								 }
							}
						}
						
						valuelist.add(itemvalue);
					}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
						if(itemvalue!=null&&itemvalue.trim().length()>0){
							/**根据字段长度判断日期格式，zhaoxg add 2015-7-1*/
							int len = fielditem.getItemlength();
							String timeStr = "yyyy.MM.dd";
							if(len>=5&&len<10){
								timeStr = "yyyy.MM";
							}else if(len<5){
								timeStr = "yyyy";
							}
							/**end*/
							Date date = DateUtils.getSqlDate(itemvalue,timeStr);
							valuelist.add(date);
						}else{
							valuelist.add(null);
						}
					}else{
						valuelist.add(itemvalue);
					}
				}
				if("1".equals(infor)){
					valuelist.add(rs.getString("A0100"));
				}else if("2".equals(infor)){
					valuelist.add(rs.getString("B0110"));
				}else if("3".equals(infor)){
					valuelist.add(rs.getString("E01A1"));
				}else if("4".equals(infor)){
					valuelist.add(rs.getString("B0110"));
				}else{
					valuelist.add(rs.getString("A0100"));
				}
				if(!field.isMainset()){
					valuelist.add(rs.getString("I9999"));
				}
				if(valuelist!=null&&valuelist.size()>0) {
                    list.add(valuelist);
                }
			}

			dao.batchUpdate(sqlstr.toString(),list);
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	/**
	 * 批量修改多个指标的记录
	 * @param setname //子集名称id
	 * @param a_code  //公司或部门id
	 * @param itemid_arr  //要修改的记录指标集的id
	 * @param itemvalue_arr //要修改的记录集
	 * @return check
	 * @throws GeneralException 
	 */
	public boolean alertMoreUpdate(Connection conn,String setname,String a_code,ArrayList itemid_arr,
			ArrayList itemvalue_arr,String dbname,String infor,String strid,String inforflag) throws GeneralException{
		boolean check=false;
		if(itemid_arr.size()<1||setname.trim().length()<1){
			return false;
		}
		if(itemid_arr.size()!=itemvalue_arr.size()){
			return false;
		}
		String tablename = dbname+setname;
		
		FieldSet field = DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update ");
		sqlstr.append(tablename);
		sqlstr.append(" set ");
		boolean modTimeFlag = isExistField("ModTime", tablename, conn);
        boolean modUserNameFlag = isExistField("ModUserName", tablename, conn);
        if(modTimeFlag) {
            sqlstr.append(" ModTime=?, ");
        }
        
        if(modUserNameFlag) {
            sqlstr.append(" ModUserName=?, ");
        }
        
		for(int i=0;i<itemid_arr.size();i++){
			String itemid = (String)itemid_arr.get(i);
			sqlstr.append(itemid);
			sqlstr.append("=?");
			if(i+1<itemid_arr.size()){
				sqlstr.append(",");
			}
		}
		if("1".equals(infor)){
			sqlstr.append(" where A0100=?");
		}else if("2".equals(infor)){
			sqlstr.append(" where B0110=?");
		}else if("3".equals(infor)){
			sqlstr.append(" where E01A1=?");
		}else if("4".equals(infor)){
			sqlstr.append(" where B0110=?");
		}else{
			sqlstr.append(" where A0100=?");
		}
		if(!field.isMainset()){
			sqlstr.append(" and I9999=?");
		}
		String[] arrid=strid.split("`");

		try {
		    Timestamp dateTime = new Timestamp((new Date()).getTime());
			ArrayList list = new ArrayList();
			for(int j=0;j<arrid.length;j++){
				String str = arrid[j];
				if(str==null||str.length()<1) {
                    continue;
                }
				ArrayList valuelist = new ArrayList();
				if(modTimeFlag) {
                    valuelist.add(dateTime);
                }
				
				if(modUserNameFlag) {
                    valuelist.add(this.userName);
                }
                
				for(int i=0;i<itemvalue_arr.size();i++){
					String itemid = (String)itemid_arr.get(i);
					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
					String itemvalue = (String)itemvalue_arr.get(i);
					int length = fielditem.getItemlength();
					int decimalwidth = fielditem.getDecimalwidth();
					//整形的判断是参照alertUpdate这个方法
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(fielditem.getDecimalwidth()>0){
							itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"0";
							if(itemvalue.indexOf(".")!=-1){
								String[] temp=itemvalue.split("\\.");
								if(decimalwidth<temp[1].length()){						 
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
								}
								 if(temp[0].length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
								 }
							}else{
								 if(itemvalue.length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
								 }
							}
						}else{
							itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"0";
							if(itemvalue.trim().length()>0&&itemvalue.indexOf(".")!=-1){
								itemvalue=itemvalue.substring(0,itemvalue.indexOf("."));
								 if(itemvalue.length()>length){
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
								 }
							}
						}
						
						valuelist.add(itemvalue);
					}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
						if(itemvalue!=null&&itemvalue.trim().length()>0){
							/**根据字段长度判断日期格式，zhaoxg add 2015-7-1*/
							int len = fielditem.getItemlength();
							String timeStr = "yyyy.MM.dd";
							if(len>=5&&len<10){
								timeStr = "yyyy.MM";
							}else if(len<5){
								timeStr = "yyyy";
							}
							/**end*/
							Date date = DateUtils.getSqlDate(itemvalue,timeStr);
							valuelist.add(date);
						}else{
							valuelist.add(null);
						}
					}else{
						valuelist.add(itemvalue);
					}
				}
			
				String id = "";
				String i9999 = "";
				if(field.isMainset()){
					id=str;
				}else{
					String strarr[] = str.split(":");
					if(strarr.length!=2) {
                        continue;
                    }
					id = strarr[0];
					i9999 = strarr[1];
					if(i9999==null||i9999.length()<1) {
                        continue;
                    }
				}
				valuelist.add(id);
				if(!field.isMainset()){
					valuelist.add(i9999);
				}
				if(valuelist!=null&&valuelist.size()>0) {
                    list.add(valuelist);
                }
			}

			 //外部培训，走培训模块的管理范围权限
            if("2".equals(inforflag)){
                String strWhr = getTrainManagePrivWhr(userView, dbname);
                if(strWhr != null && strWhr.length() > 0) {
                    sqlstr.append(strWhr);
                }
            }
            
			dao.batchUpdate(sqlstr.toString(),list);
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	
	public String codeWhere(String setname,String dbname,UserView userView,Connection conn) throws GeneralException{
		InfoUtils infoUtils=new InfoUtils();
		String personsortfield=new SortFilter().getSortPersonField(conn);
		String codesetid = userView.getManagePrivCode();
		String kind="2";
		if("UN".equalsIgnoreCase(codesetid)){
			kind="2";
		}else if("UM".equalsIgnoreCase(codesetid)){
			kind="1";
		}else if("@K".equalsIgnoreCase(codesetid)){
			kind="0";
		}
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");//兼职单位
		list.add("setid");//兼职子集
		list.add("appoint");//兼职标识
		list.add("pos");//兼职职务
		String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		String part_pos="";
		//兼职处理
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0) {
                flag=(String)map.get("flag");
            }
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0) {
                    part_unit=(String)map.get("unit");
                }
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0) {
                    part_setid=(String)map.get("setid");
                }
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0) {
                    appoint=(String)map.get("appoint");
                }
				if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0) {
                    part_pos=(String)map.get("pos");
                }
			}		
		}
		String term_Sql=infoUtils.getWhereSQL(conn,userView,dbname,userView.getManagePrivCodeValue(),true,kind,"org",personsortfield,"All",part_unit,part_setid,appoint,"");
		
		StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("A0100 in (");
			sqlstr.append(term_Sql);
			sqlstr.append(")");
		
		return sqlstr.toString();
	}
	
	
	private String strid="";  //批量修改页面已选择的记录  20141029 dengcan
	   /**
	 *wangrd 2013-09-06
     * 批量修改前检查编制,参数传递同批量修改，但返回值为字符串
     * @param setname //子集名称id
     * @param a_code  //公司或部门id
     * @param itemid_arr  //要修改的记录指标集的id
     * @param itemvalue_arr //要修改的记录集
     * @return check
     * @throws GeneralException 
     */
    public String scanFormationBeforeBatModify(Connection conn,UserView userView,String setname,ArrayList itemid_arr,
            ArrayList itemvalue_arr,String dbname,String infor,String history,String selectid) throws GeneralException{
        String returnStr = "ok";
        if (itemid_arr.size() < 1 || setname.trim().length() < 1) {
            return returnStr;
        }
        if (itemid_arr.size() != itemvalue_arr.size()) {
            return returnStr;
        }

        ScanFormationBo scanFormationBo = new ScanFormationBo(conn, userView);
        if (scanFormationBo.doScan()) {
            boolean bPart = false;
            StringBuffer itemids = new StringBuffer();
            for (int i = 0; i < itemvalue_arr.size(); i++) {
                String itemid = (String) itemid_arr.get(i);
                if (!"".equals(itemids.toString())) {
                    itemids.append(",");
                }
                itemids.append(itemid);
            }

            if ("true".equals(scanFormationBo.getPart_flag()) && setname.equals(scanFormationBo.getPart_setid())) {// 兼职子集
                String part_fld = "";
                bPart = true;
                part_fld = scanFormationBo.getPart_unit();
                if ((part_fld != null) && (!"".equals(part_fld))) {
                    itemids.append(",b0110");
                }
                part_fld = scanFormationBo.getPart_dept();
                if ((part_fld != null) && (!"".equals(part_fld))) {
                    itemids.append(",e0122");
                }
                part_fld = scanFormationBo.getPart_pos();
                if ((part_fld != null) && (!"".equals(part_fld))) {
                    itemids.append(",e01a1");
                }
            }
            if (("," + itemids + ",").indexOf(",e01a1,") > -1) {
                scanFormationBo.setPosChange(true);
            }

            if (scanFormationBo.needDoScan(dbname + ',', itemids.toString())) {
                String tablename = dbname + setname;
                FieldSet field = DataDictionary.getFieldSetVo(setname);
                ContentDAO dao = new ContentDAO(conn);
                StringBuffer buf = new StringBuffer();
                if ("1".equals(infor)) {
                    buf.append("select A0100");
                }
                if (!field.isMainset()) {
                    buf.append(",I9999");
                }
                buf.append(" from ");
                buf.append(tablename);
                buf.append(" a where ");
                buf.append(codeWhere(tablename, dbname, userView, conn));

                if ("0".equals(selectid)) {
                    if (userView.getStatus() == 0) {
                        buf.append(" and A0100 in(select A0100 from ");
                        buf.append(userView.getUserName() + dbname + "result)");
                    } else if (userView.getStatus() == 4) {
                        buf.append(" and A0100 in(select obj_id from ");
                        buf.append("t_sys_result where upper(username)='" + userView.getUserName().toUpperCase() + "' and upper(nbase)='" + dbname.toUpperCase() + "' and flag=0)");
                    }
                }
                else if("2".equals(selectid))// 20141029  dengcan 人员信息批量修改增加对所选记录的更新操作
                {
                	if(this.strid.length()>0)
                	{
	                	StringBuffer where=new StringBuffer("");
	                	String strs[] = strid.split("`");
	                	for(int i=0;i<strs.length;i++)
	                	{
	                		if(strs[i].length()>0) {
                                where.append(",'"+strs[i]+"'");
                            }
	                	}
	                	buf.append(" and A0100 in( "+where.substring(1)+" )");
                	}
                	else {
                        buf.append(" and 1=2 ");
                    }
                }
                
                if (!field.isMainset()) { // 子集
                    if (!"1".equals(history)||(!bPart)) {// 最近记录或非编制子集
                        buf.append(" and I9999=(select max(I9999) from " + tablename);
                        if ("1".equals(infor)) {
                            buf.append(" where A0100=a.A0100");
                        }
                        buf.append(")");
                    } else {
        
                    }
                }
                try {
                    RowSet rs = dao.search(buf.toString());
                    ArrayList beanList = new ArrayList();
                    while (rs.next()) {
                        LazyDynaBean scanBean = new LazyDynaBean();
                        String A0100 = "";
                        String I9999 = "";
                        if ("1".equals(infor)) {
                            A0100 = rs.getString("A0100");
                        }
                        if (!field.isMainset()) {
                            I9999 = rs.getString("I9999");
                        }
                        scanBean.set("objecttype", "1");
                        scanBean.set("nbase", dbname);
                        scanBean.set("a0100", A0100);
                        scanBean.set("ispart", "0");
                        scanBean.set("addflag", "0");

                        for (int i = 0; i < itemvalue_arr.size(); i++) {
                            String itemid = (String) itemid_arr.get(i);
                            FieldItem fielditem = DataDictionary.getFieldItem(itemid);
                            String itemvalue = (String) itemvalue_arr.get(i);

                            if ((itemvalue == null) || ("null".equalsIgnoreCase(itemvalue))) {
                                itemvalue = "";
                            }
                            scanBean.set(itemid, itemvalue);
                        }
                        if ("01".equals(setname.substring(1, 3))) {// 主集
                            ;
                        } else {
                            if (bPart) {// 兼职子集
                                scanBean.set("ispart", "1");
                                scanBean.set("i9999", I9999);
                                String part_fld = "";
                                part_fld = scanFormationBo.getPart_unit();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("b0110", (String) scanBean.get(part_fld));
                                    }
                                }
                                part_fld = scanFormationBo.getPart_dept();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("e0122", (String) scanBean.get(part_fld));
                                    }
                                }
                                part_fld = scanFormationBo.getPart_pos();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("e01a1", (String) scanBean.get(part_fld));
                                    }
                                }
                            }
                        }
                        beanList.add(scanBean);

                    }
                    scanFormationBo.execDate2TmpTable(beanList);
                    //如果不涉及修改岗位指标，不检查岗位编制 guodd 2015-04-16
                    if ((","+itemids+",").indexOf(",e01a1,")<0){
                        scanFormationBo.setPosChange(false);
                    }
                    String mess = scanFormationBo.isOverstaffs();
                    if (!"ok".equals(mess)) {
                        if ("warn".equals(scanFormationBo.getMode())) {
                            returnStr = mess;
                        } else {
                            throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return returnStr;
    }
	/**
     * 批量修改多个指标的记录
     * 
     * @param setname
     *            //子集名称id
     * @param a_code
     *            //公司或部门id
     * @param itemid_arr
     *            //要修改的记录指标集的id
     * @param itemvalue_arr
     *            //要修改的记录集
     * @return check
     * @throws GeneralException
     */
	public boolean alertMoreUpdate(Connection conn,UserView userView,String setname,ArrayList itemid_arr,
			ArrayList itemvalue_arr,String dbname,String infor,String history,String selectid,String inforflag) throws GeneralException{
		boolean check=false;
		if(itemid_arr.size()<1||setname.trim().length()<1){
			return false;
		}
		if(itemid_arr.size()!=itemvalue_arr.size()){
			return false;
		}
		String tablename = dbname+setname;
		
		FieldSet field = DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update ");
		sqlstr.append(tablename);
		sqlstr.append(" set ");
		boolean modTimeFlag = isExistField("ModTime", tablename, conn);
        boolean modUserNameFlag = isExistField("ModUserName", tablename, conn);
        if(modTimeFlag) {
            sqlstr.append(" ModTime=?, ");
        }
        
        if(modUserNameFlag) {
            sqlstr.append(" ModUserName=?, ");
        }
        
		for(int i=0;i<itemid_arr.size();i++){
			String itemid = (String)itemid_arr.get(i);
			sqlstr.append(itemid);
			sqlstr.append("=?");
			if(i+1<itemid_arr.size()){
				sqlstr.append(",");
			}
		}
		StringBuffer buf = new StringBuffer();
		if("1".equals(infor)){
			sqlstr.append(" where A0100=?");
			buf.append("select A0100");
		}
		if(!field.isMainset()){
			sqlstr.append(" and I9999=?");
			buf.append(",I9999");
		}
		buf.append(" from ");
		buf.append(tablename);
		
		buf.append(" a where ");
		buf.append(codeWhere(tablename,dbname,userView,conn));
		
		if("0".equals(selectid)){
			if(userView.getStatus()==0){
				buf.append(" and A0100 in(select A0100 from ");
				buf.append(userView.getUserName()+dbname+"result)");
			}else if(userView.getStatus()==4){
				buf.append(" and A0100 in(select obj_id from ");
				buf.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0)");
			}
		}
		else if("2".equals(selectid))// 20141029  dengcan 人员信息批量修改增加对所选记录的更新操作
        {
			if(this.strid.length()>0)
			{
	        	StringBuffer where=new StringBuffer("");
	        	String strs[] = strid.split("`");
	        	for(int i=0;i<strs.length;i++)
	        	{
	        		if(strs[i].length()>0) {
                        where.append(",'"+strs[i]+"'");
                    }
	        	}
	        	buf.append(" and A0100 in( "+where.substring(1)+" )");
			}
			else
			{
				buf.append(" and 1=2 ");
			}
        }
		
		
		if(!"1".equals(history)&&!field.isMainset()){
			buf.append(" and I9999=(select max(I9999) from "+tablename);
			if("1".equals(infor)){
				buf.append(" where A0100=a.A0100");
			}
			buf.append(")");
		}

		 //外部培训，走培训模块的管理范围权限
        if("2".equals(inforflag)){
            String strWhr = getTrainManagePrivWhr(userView, dbname);
            if(strWhr != null && strWhr.length() > 0){
                sqlstr.append(strWhr);
                buf.append(strWhr);
            }
        }
        
		try {
		    Timestamp dateTime = new Timestamp((new Date()).getTime());
			RowSet rs = dao.search(buf.toString());
			ArrayList list = new ArrayList();
			while(rs.next()){
				ArrayList valuelist = new ArrayList();
				if(modTimeFlag) {
                    valuelist.add(dateTime);
                }
				
				if(modUserNameFlag) {
                    valuelist.add(this.userName);
                }
                
				for(int i=0;i<itemvalue_arr.size();i++){
					String itemid = (String)itemid_arr.get(i);
					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
					String itemvalue =PubFunc.keyWord_reback( (String)itemvalue_arr.get(i));  //20160804 dengcan 此处转回来不会产生SQL注入
					if("D".equalsIgnoreCase(fielditem.getItemtype())){
						if(itemvalue!=null&&itemvalue.trim().length()>0){
							int itemlength = fielditem.getItemlength();
							String format = "yyyy-MM-dd";
							itemvalue = itemvalue.replaceAll("\\.", "-");
							if(itemlength==4) {
                                format="yyyy";
                            } else if(itemlength==7) {
                                format="yyyy-MM";
                            } else if(itemlength==10) {
                                format="yyyy-MM-dd";
                            } else if(itemlength==16) {
                                format="yyyy-MM-dd HH:mm";
                            } else if(itemlength==18) {
                                format="yyyy-MM-dd HH:mm:ss";
                            }
							itemvalue = PubFunc.DateStringChangeValue(itemvalue);
							/*SimpleDateFormat sdf = new SimpleDateFormat(format);
							java.sql.Date date = null;
							try {
								date = new java.sql.Date(sdf.parse(itemvalue).getTime());
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							valuelist.add(date);*/
							if(Sql_switcher.searchDbServer()==1) {
                                valuelist.add(itemvalue);
                            } else{
								Date date = DateUtils.getTimestamp(itemvalue,format);
								valuelist.add(date);
							}
						}else{
							valuelist.add(null);
						}
					}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(itemvalue.trim().length()==0){
							//valuelist.add(new Integer(0));
							valuelist.add(null);//zhaogd 2014-3-6 在批量修改中数值型指标若没有值则默认为空，不为0
						}else{
							valuelist.add(itemvalue);
						}
					}else{
						if(!"M".equalsIgnoreCase(fielditem.getItemtype())){
							itemvalue = PubFunc.splitString(itemvalue, fielditem.getItemlength());
						}
						valuelist.add(itemvalue);
					}
				}
				if("1".equals(infor)){
					valuelist.add(rs.getString("A0100"));
				}
				if(!field.isMainset()){
					valuelist.add(rs.getString("I9999"));
				}
				if(valuelist!=null&&valuelist.size()>0) {
                    list.add(valuelist);
                }
			}

			dao.batchUpdate(sqlstr.toString(),list);
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	/**
	 * 批量追加多个指标的记录
	 * @param setname //子集名称id
	 * @param a_code  //公司或部门id
	 * @param itemid_arr  //要修改的记录指标集的id
	 * @param itemvalue_arr //要修改的记录集
	 * @return check
	 */
	
	public boolean addUpdate(Connection conn,UserView uv,String setname,String dbname,
			String a_code,ArrayList itemid_arr,ArrayList itemvalue_arr,String viewsearch,
			String infor, String inforflag){
		boolean check=false;
		if(itemid_arr.size()<1||setname.trim().length()<1){
			return false;
		}
		if(itemid_arr.size()!=itemvalue_arr.size()){
			return false;
		}
		for(int i=0;i<itemid_arr.size();i++){
			if(itemid_arr.get(i)!=null&&itemid_arr.get(i).toString().length()>0){
				FieldItem fielditem=DataDictionary.getFieldItem(itemid_arr.get(i).toString());
				if(fielditem!=null){
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						String itemvalue = itemvalue_arr.get(i)!=null&&itemvalue_arr.get(i).toString().trim().length()>0?itemvalue_arr.get(i).toString():"0";
						if(fielditem.getDecimalwidth()==0){  
							itemvalue=itemvalue!=null&&itemvalue.trim().length()>0?itemvalue:"0";
							if(itemvalue.trim().length()>0&&itemvalue.indexOf(".")!=-1) {
                                itemvalue=itemvalue.substring(0,itemvalue.indexOf("."));
                            }
						}
						itemvalue_arr.remove(i);
						itemvalue_arr.add(i,itemvalue);
					}
				}	
			}
		}
		
		
		String tablename = dbname+setname;
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("insert into ");
		sqlstr.append(tablename);
		sqlstr.append("(");
		if(!fieldset.isMainset()){
			if("1".equals(infor)) {
                sqlstr.append("A0100");
            } else if("2".equals(infor)) {
                sqlstr.append("B0110");
            } else if("3".equals(infor)) {
                sqlstr.append("E01A1");
            } else {
                sqlstr.append("A0100");
            }
			sqlstr.append(",I9999");
			for(int i=0;i<itemid_arr.size();i++){
				sqlstr.append(",");
				sqlstr.append(itemid_arr.get(i));
			}
		}else{
			if("1".equals(infor)) {
                sqlstr.append("A0000,A0100");
            } else if("2".equals(infor)) {
                sqlstr.append("B0110");
            } else if("3".equals(infor)) {
                sqlstr.append("E01A1");
            } else {
                sqlstr.append("A0000,A0100");
            }
			
			for(int i=0;i<itemid_arr.size();i++){
				sqlstr.append(",");
				sqlstr.append(itemid_arr.get(i));
			}
		}
		sqlstr.append(",createtime,createusername");
		boolean modTimeFlag = isExistField("ModTime", tablename, conn);
        boolean modUserNameFlag = isExistField("ModUserName", tablename, conn);
        if(modTimeFlag) {
            sqlstr.append(",ModTime");
        }
        
        if(modUserNameFlag) {
            sqlstr.append(",ModUserName");
        }
        
		sqlstr.append(") values (");
		if(!fieldset.isMainset()){
			sqlstr.append("?,?");
		}else{
			if("1".equals(infor)) {
                sqlstr.append("?,?");
            } else if("2".equals(infor)) {
                sqlstr.append("?");
            } else if("3".equals(infor)) {
                sqlstr.append("?");
            } else {
                sqlstr.append("?,?");
            }
		}
		for(int i=0;i<itemid_arr.size();i++){
			sqlstr.append(",?");
		}
		if(modTimeFlag) {
            sqlstr.append(",?");
        }

		if(modUserNameFlag) {
            sqlstr.append(",?");
        }
		sqlstr.append(",?,?)");
		ArrayList valuelist = this.valueList(conn,uv,setname,dbname,a_code,itemvalue_arr,itemid_arr,viewsearch,infor, inforflag);
		try {
			dao.batchInsert(sqlstr.toString(),valuelist);
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return check;		
	}
	
	public boolean addUpdate(Connection conn,UserView uv,String setname,String dbname,
			String a_code,ArrayList itemid_arr,ArrayList itemvalue_arr,String viewsearch,String infor){
		return addUpdate(conn, uv, setname, dbname, a_code, itemid_arr, itemvalue_arr, viewsearch, infor, "");		
	}
	
	private ArrayList valueList(Connection conn,UserView uv,String setname,String dbname,String a_code
			,ArrayList itemvalue_arr,ArrayList itemid_arr,String viewsearch,String infor, String inforflag){
		ArrayList valuelist = new ArrayList();
		String tablename = dbname+setname;
		
		String itemid = "";
		String fieledsetid = "";
		if("1".equals(infor)){
			itemid="A0100";
			fieledsetid="A01";
		}else if("2".equals(infor)){
			itemid="B0110";
			fieledsetid="B01";
		}else if("3".equals(infor)){
			itemid="E01A1";
			fieledsetid="K01";
		}else{
			itemid="A0100";
			fieledsetid="A01";
		}
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn);
		if(!fieldset.isMainset()){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(itemid);
			sqlstr.append(",(select max(i9999)+1 from ");
			sqlstr.append(tablename);
			sqlstr.append(" where "+dbname);
			sqlstr.append(fieledsetid+"."+itemid+"=");
			sqlstr.append(tablename);
			sqlstr.append("."+itemid+") as I9999 from "+dbname+fieledsetid);
			
			
			sqlstr.append(" where ");
			sqlstr.append(codeWhere(setname,dbname,a_code,infor,conn));
			
			if("1".equals(viewsearch)){
				sqlstr.append(" and A0100 in(select A0100 from ");
				sqlstr.append(uv.getUserName()+dbname+"result) ");
			}
			
			//外部培训，走培训模块的管理范围权限
			if("2".equals(inforflag)){
				String strWhr = getTrainManagePrivWhr(uv, dbname);
				if(strWhr != null && strWhr.length() > 0) {
                    sqlstr.append(strWhr);
                }
			}
			
			boolean modTimeFlag = isExistField("ModTime", tablename, conn);
            boolean modUserNameFlag = isExistField("ModUserName", tablename, conn);
			try {
			     Date date = DateUtils.getSqlDate(Calendar.getInstance());
				RowSet rs=dao.search(sqlstr.toString());
				while(rs.next()){
					ArrayList list = new ArrayList();
					String a0100 = rs.getString(itemid);
					String i9999 = rs.getString("I9999");
					i9999=i9999!=null&&i9999.trim().length()>0?i9999:"1";
					list.add(a0100);
					list.add(i9999);
					for(int i=0;i<itemvalue_arr.size();i++){
						String itemids = (String)itemid_arr.get(i);
						FieldItem fielditem = DataDictionary.getFieldItem(itemids);
						String itemvalue = (String)itemvalue_arr.get(i);
						if("D".equalsIgnoreCase(fielditem.getItemtype())){
							if(itemvalue!=null&&itemvalue.trim().length()>0){
								Date dates = DateUtils.getSqlDate(itemvalue,"yyyy-MM-dd");
								list.add(dates);
							}else{
								list.add(null);
							}
						}else{
							list.add(itemvalue);
						}
					}
					list.add(date);
					list.add(uv.getUserName());
					if(modTimeFlag) {
                        list.add(date);
                    }
					
					if(modUserNameFlag) {
                        list.add(uv.getUserName());
                    }
                    
					valuelist.add(list);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return valuelist;
	}
	
	private String getTrainManagePrivWhr(UserView uv, String dbname){
		String strWhr = "";
		try{
			TrainCourseBo bo = new TrainCourseBo(uv);
			strWhr = bo.getPrivSqlWhere(dbname);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return strWhr;
	}
	
	public int countItem(Connection conn,UserView uv,String setname,String dbname,String a_code,
			String viewsearch,String infor){
		return countItem(conn, uv, setname, dbname, a_code, viewsearch, infor, "");
	}
	
	public int countItem(Connection conn,UserView uv,String setname,String dbname,String a_code,
			String viewsearch,String infor, String inforflag){
		int count=0;
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn); 
		if(!fieldset.isMainset()){
			String maintable="";
			String mainitem="";
			if("1".equals(infor)){
				maintable=dbname+"A01";
				mainitem="A0100";
			}else if("2".equals(infor)){
				maintable="B01";
				mainitem="B0110";
			}else if("3".equals(infor)){
				maintable="K01";
				mainitem="E01A1";
			}else{
				maintable=dbname+"A01";
				mainitem="A0100";
			}
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(" count("+mainitem+") as countid from ");
			sqlstr.append(maintable);
			sqlstr.append(" where 1=1 ");
			sqlstr.append(codeWhereStr(dbname,a_code,infor));
			if("1".equals(viewsearch)){
				sqlstr.append(" and "+mainitem+" in(select "+mainitem+" from ");
				sqlstr.append(uv.getUserName()+dbname+"result) ");
			}
			
		    //外部培训，走培训模块的管理范围权限
			if("2".equals(inforflag)){
				String strWhr = getTrainManagePrivWhr(uv, dbname);
				if(strWhr != null && strWhr.length() > 0) {
                    sqlstr.append(strWhr);
                }
			}
			
			try {
				RowSet rs=dao.search(sqlstr.toString());
				while(rs.next()){
					count = rs.getInt("countid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	public int countDelItem(Connection conn,UserView uv,String setname,String dbname,String a_code,
			String viewsearch,String infor,String inforflag){
		int count=0;
		String tablename = "";
		this.userView=uv;

		//if(a_code.equals("") || a_code.equals("all"))
	    	   // a_code = uv.getManagePrivCode()+uv.getManagePrivCodeValue();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn); 
		String itemid = "";
		String resultTable="";
		if("1".equals(infor)){
			itemid="A0100";
			tablename = dbname+setname;
			resultTable=uv.getUserName()+dbname+"result";
		}else if("2".equals(infor)){
			itemid="B0110";
			tablename = setname;
			resultTable=uv.getUserName()+"Bresult";
		}else if("3".equals(infor)){
			itemid="E01A1";
			tablename = setname;
			resultTable=uv.getUserName()+"Kresult";
		}else if("4".equals(infor)){
			itemid="B0110";
			tablename = setname;
			resultTable=uv.getUserName()+"Bresult";
		}else if("5".equals(infor)){
			itemid="R4501";
			tablename = setname;
			resultTable=uv.getUserName()+"Bresult";
		}else{
			itemid="A0100";
			tablename = dbname+setname;
			resultTable=uv.getUserName()+dbname+"result";
		}
		if(!fieldset.isMainset()){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(" count(*) as countid from ");
			sqlstr.append(tablename);
			
			String wherestr = codeWhere(setname,dbname,a_code,infor,conn);
			String strWhr = getTrainManagePrivWhr(uv, dbname);
			
			if("1".equals(viewsearch)){
				sqlstr.append(" where ");
				if(wherestr.trim().length()>1){
					sqlstr.append(wherestr);
					sqlstr.append(" and ");
				}
				sqlstr.append(itemid+" in(select "+itemid+" from ");
				sqlstr.append(resultTable+") ");
			}else{
				
			    //外部培训，走培训模块的管理范围权限
	            if("2".equals(inforflag)){
	                if(strWhr != null && strWhr.length() > 0){
	                    sqlstr.append(" where ");
	                    sqlstr.append(strWhr.substring(5));
	                }
	            } else if(wherestr.trim().length()>1){
					sqlstr.append(" where ");
					sqlstr.append(wherestr);
				}
			}
			
			sqlstr.append(" group by ");
			sqlstr.append(itemid);

			
			try {
				RowSet rs=dao.search(sqlstr.toString());
				
				while(rs.next()){
					count++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(" count(*) as countid from ");
			sqlstr.append(tablename);
			sqlstr.append(" where ");

			if("2".equals(inforflag)){
			    String strWhr = getTrainManagePrivWhr(uv, dbname);
                if(strWhr != null && strWhr.length() > 0){
                    sqlstr.append(strWhr);
                }
            } else {
                sqlstr.append(codeWhere(setname,dbname,a_code,infor,conn));
            }
			
			if("1".equals(viewsearch)){
				sqlstr.append(" and "+itemid+" in(select "+itemid+" from ");
				sqlstr.append(resultTable+") ");
			}
			try {
				RowSet rs=dao.search(sqlstr.toString());
				
				while(rs.next()){
					count = rs.getInt("countid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.userView=null;
		return count;
	}
	public int countItemall(Connection conn,UserView uv,String setname,String dbname,String infor){
		int count=0;
		String tablename = dbname+setname;

		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn); 
		if(!fieldset.isMainset()){
			String a_code = uv.getManagePrivCode()+uv.getManagePrivCodeValue();
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(" count(*) as countid from ");
			sqlstr.append(tablename);
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" where ");
				sqlstr.append(codeWhere(setname,dbname,a_code,infor,conn));
			}
			try {
				RowSet rs=dao.search(sqlstr.toString());
				while(rs.next()){
					count = rs.getInt("countid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	/**
	 * 当前查询结果的所有记录个数
	 * @param conn
	 * @param uv
	 * @param setname
	 * @param dbname
	 * @param infor
	 * @param a_code
	 * @author fzg
	 * @return
	 */
	public int countItemall(Connection conn,UserView uv,String setname,String dbname,String infor,String a_code){
		int count=0;
		String tablename = dbname+setname;
		if("".equals(a_code) || "all".equals(a_code)|| "un".equalsIgnoreCase(a_code)) {
            a_code = uv.getManagePrivCode()+uv.getManagePrivCodeValue();
        }
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ContentDAO dao = new ContentDAO(conn); 
		if(!fieldset.isMainset()){		    
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(" count(*) as countid from ");
			sqlstr.append(tablename);
			if(a_code!=null&&a_code.trim().length()>2){
				sqlstr.append(" where ");
				sqlstr.append(codeWhere(setname,dbname,a_code,infor,conn));
			}
			try {
				RowSet rs=dao.search(sqlstr.toString());
				while(rs.next()){
					count = rs.getInt("countid");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	/**
	 * 批量删除当前指标的记录
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @param a_code  //公司或部门id
	 * @return check
	 */
	public boolean delUpdate(Connection conn,UserView uv,String setname,String dbname
			,String a_code,String viewsearch,String infor,String inforflag){
		boolean check=false;
		String tablename=dbname+setname;
		ContentDAO dao = new ContentDAO(conn);
		
		String itemid = "";
		if("1".equals(infor)){
			itemid="A0100";
		}else if("2".equals(infor)){
			itemid="B0110";
		}else if("3".equals(infor)){
			itemid="E01A1";
		}else{
			itemid="A0100";
		}
		
		StringBuffer buf=new StringBuffer();
		buf.append(codeWhere(setname,dbname,a_code,infor,conn));
		
		if("1".equals(viewsearch)){
			buf.append(" and "+itemid+" in(select "+itemid+" from ");
			buf.append(uv.getUserName()+dbname+"result)");
		}

		StringBuffer strsql=new StringBuffer();
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		
		if(fieldset.isMainset()){
			strsql.append("delete from ");
			strsql.append(tablename);
			strsql.append(" where "+itemid+" in (" );
			strsql.append(buf.toString());
			strsql.append(")");
		}else{//子集，关联主集姓名字段		
			String childtable=dbname+setname;
			strsql.append("delete ");
			
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                strsql.append(childtable);
            }
			
			strsql.append(" from ");
			strsql.append(childtable);
			strsql.append(" a where exists(select "+itemid+" from ");
			strsql.append(childtable);
			strsql.append(" where ");
			strsql.append(itemid+"=a."+itemid);
			strsql.append(" and ");
			strsql.append(buf.toString());
			strsql.append(" group by "+itemid+" having max(I9999)=a.I9999)");
		}
		

        //外部培训，走培训模块的管理范围权限
       if("2".equals(inforflag)){
           String strWhr = getTrainManagePrivWhr(userView, dbname);
           if(strWhr != null && strWhr.length() > 0) {
               strsql.append(strWhr);
           }
       }
       
		try {
			dao.update(strsql.toString());
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return check;
	}
	/**
	 * 批量删除某月某次指标的记录
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @param a_code  //公司或部门id
	 * @param year //年
	 * @param month //月
	 * @param frequency //次数
	 * @return check
	 */
	public boolean delUpdate(Connection conn,UserView uv,String setname,String dbname,String a_code,
			String year,String month,String frequency,String infor,String inforflag){
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		
		StringBuffer buf=new StringBuffer();
		buf.append(codeWhere(setname,dbname,a_code,infor,conn));

		StringBuffer strsql=new StringBuffer();
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		if(!fieldset.isMainset()){
			String time = getDataValue(setname+"z0",year,month);			
			String childtable=dbname+setname;
			strsql.append("delete ");
			strsql.append(" from ");
			strsql.append(childtable);
			strsql.append(" where ");
			strsql.append(buf.toString());
			strsql.append(" and ");
			strsql.append(time);
			strsql.append(" and ");
			strsql.append(setname+"Z1=");
			strsql.append(frequency);
		
			 //外部培训，走培训模块的管理范围权限
            if("2".equals(inforflag)){
                String strWhr = getTrainManagePrivWhr(userView, dbname);
                if(strWhr != null && strWhr.length() > 0) {
                    strsql.append(strWhr);
                }
            }
		
			try {
				if(strsql.length()>1){
					dao.update(strsql.toString());
				}
				check=true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return check;
	}
	/**
	 * 批量删除某月某次指标的记录
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @param a_code  //公司或部门id
	 * @param year //年
	 * @param month //月
	 * @param frequency //次数
	 * @return check
	 */
	public int countMon(Connection conn,UserView uv,String setname,String dbname,String a_code,
			String year,String month,String frequency,String infor){
		int count=0;
		ContentDAO dao = new ContentDAO(conn);
		
		StringBuffer buf=new StringBuffer();
		buf.append(codeWhere(setname,dbname,a_code,infor,conn));

		StringBuffer strsql=new StringBuffer();
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		if(!fieldset.isMainset()){
			String time = getDataValue(setname+"z0",year,month);			
			String childtable=dbname+setname;
			strsql.append("select count(*) as countid");
			strsql.append(" from ");
			strsql.append(childtable);
			strsql.append(" where ");
			strsql.append(buf.toString());
			strsql.append(" and ");
			strsql.append(time);
			strsql.append(" and ");
			strsql.append(setname+"Z1=");
			strsql.append(frequency);
			try {
				if(strsql.length()>1){
					RowSet rs = dao.search(strsql.toString());
					while(rs.next()){
						count = rs.getInt("countid");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return count;
	}
	/**
	 * 批量删除所有记录
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @return check
	 */
	public boolean delUpdate(Connection conn,String setname,String dbname){
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer strsql=new StringBuffer();		
		String childtable=dbname+setname;
		strsql.append("delete ");
		strsql.append(" from ");
		strsql.append(childtable);
			
		try {
			dao.update(strsql.toString());
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	private String getDataValue(String fielditemid,String year,String month){
		StringBuffer wheretime = new StringBuffer(0);
		wheretime.append(Sql_switcher.year(fielditemid)+"="+year+" and ");
		wheretime.append(Sql_switcher.month(fielditemid)+"="+month);
		return wheretime.toString();
	}
	
	/**
	 * 批量删除所有记录(删除当前显示的人员的所有子集)
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @return check
	 * @author FanZhiGuo
	 */
	public boolean delUpdate(Connection conn,String setname,String dbname,String infor,String a_code,String inforflag){
		boolean check=false;

		StringBuffer buf=new StringBuffer();
		buf.append(codeWhere(setname,dbname,a_code,infor,conn));		
		
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer strsql=new StringBuffer();		
		String childtable=dbname+setname;
		strsql.append("delete ");
		strsql.append(" from ");
		strsql.append(childtable);
		strsql.append(" where ");
		strsql.append(buf.toString());
		
		 //外部培训，走培训模块的管理范围权限
        if("2".equals(inforflag)){
            String strWhr = getTrainManagePrivWhr(userView, dbname);
            if(strWhr != null && strWhr.length() > 0) {
                strsql.append(strWhr);
            }
        }
		try {
			dao.update(strsql.toString());
			check=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return check;
	}
	/**
	 * 批量计算
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @return check
	 */
	public boolean colUpdate1(Connection conn,UserView uv,String dbname,
				String setname,String a_code,String results,
				String history,String viewsearch,String infor,String inforflag)throws GeneralException{
		boolean check=true;
		YksjParser yp=null;
		ArrayList list = colList(conn,dbname,infor);
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		if("5".equals(infor)){
			alUsedFields=DataDictionary.getFieldList("r45",Constant.ALL_FIELD_SET);
		}
		
		int forvalue = YksjParser.forPerson;
		if("2".equals(infor)){
			forvalue=YksjParser.forUnit;
		}else if("3".equals(infor)){
			forvalue=YksjParser.forPosition;
		}
		for(int i=0;i<list.size();i++){
			int n1=0;
			ArrayList arrlist = (ArrayList)list.get(i);
//			String pre = uv.analyseFieldPriv((String)arrlist.get(1));
//			pre=pre!=null&&pre.trim().length()>0?pre:"0";
//			if(!pre.equals("2"))
//				continue;
			FieldItem fielditem = DataDictionary.getFieldItem((String)arrlist.get(1));
			if(fielditem==null) {
                continue;
            }
			String type = fielditem.getItemtype();
			StringBuffer buf = new StringBuffer();
			buf.append("update ");
			buf.append(arrlist.get(0));
			buf.append(" set ");
			
			boolean modTimeFlag = isExistField("ModTime", (String)arrlist.get(0), conn);
	        boolean modUserNameFlag = isExistField("ModUserName", (String)arrlist.get(0), conn);
	        if(modTimeFlag){
	            buf.append("ModTime=");
	            buf.append(new Timestamp((new Date()).getTime()) + ",");
	            
	        }
	        
	        if(modUserNameFlag){
	            buf.append(",ModUserName=");
	            buf.append(uv.getUserName() + ", ");
            }
	        
			buf.append(arrlist.get(1));
			buf.append("=(");
			yp = new YksjParser(uv,alUsedFields,
					YksjParser.forSearch,getDataType(type),forvalue ,"Ht",dbname);
			try {
				if((String)arrlist.get(2)!=null&&arrlist.get(2).toString().length()>0){
					yp.setCon(conn);
					String tablename="";
					if("5".equals(infor)){
						yp.Verify_where((String)arrlist.get(2));
						tablename = "r45";
					}else{
						yp.run((String)arrlist.get(2));
						tablename = yp.getTempTableName();
					}
					
					StringBuffer strsql = new StringBuffer();
					
					if("5".equals(infor)){
						strsql.append(yp.getSQL());
					}else{
						strsql.append("select ");
						strsql.append(yp.getSQL());
						strsql.append(" from ");
						strsql.append(tablename);
						strsql.append(" where ");
						strsql.append(tablename);
					}
					if("1".equals(infor)){
						strsql.append(".A0100=");
						strsql.append(arrlist.get(0));
						strsql.append(".A0100");
						if(!fielditem.isMainSet()){
							strsql.append(" and ");
							strsql.append(tablename);
							strsql.append(".I9999=");
							strsql.append(arrlist.get(0));
							strsql.append(".I9999");
						}
					}else if("2".equals(infor)){
						strsql.append(".B0110=");
						strsql.append(arrlist.get(0));
						strsql.append(".B0110");
						if(!fielditem.isMainSet()){
							strsql.append(" and ");
							strsql.append(tablename);
							strsql.append(".I9999=");
							strsql.append(arrlist.get(0));
							strsql.append(".I9999");
						}
					}else if("3".equals(infor)){
						strsql.append(".E01A1=");
						strsql.append(arrlist.get(0));
						strsql.append(".E01A1");
						if(!fielditem.isMainSet()){
							strsql.append(" and ");
							strsql.append(tablename);
							strsql.append(".I9999=");
							strsql.append(arrlist.get(0));
							strsql.append(".I9999");
						}
					}
					buf.append(strsql);
				}else{
					continue;
				}
			} catch (GeneralException e) {
				check=false;
		//		e.printStackTrace();
				continue;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
				continue;
			}
			n1=buf.length()+1;
			buf.append(") where ");
			String itemid="";
			if("1".equals(infor)) {
                itemid="A0100";
            } else if("2".equals(infor)) {
                itemid="B0110";
            } else if("3".equals(infor)) {
                itemid="E01A1";
            }
			if("1".equals(history)){
				if(!fielditem.isMainSet()){
					if(infor!=null&&!"5".equals(infor)){
						buf.append(" i9999=(select max(i9999) from ");
						buf.append(arrlist.get(0));
						buf.append(" b where  ");
						buf.append(arrlist.get(0));
						buf.append("."+itemid+" = b."+itemid+") and ");
					}
				}
			}
			if("1".equals(viewsearch)){
				buf.append("a0100 in(select a0100 from ");
				buf.append(uv.getUserName()+dbname+"result) and ");
			}
			StringBuffer sb = new StringBuffer();
			sb.append("select "+itemid+" from "+arrlist.get(0)+" ");
			sb.append(buf.substring(n1));
			//buf.append(" 1=1 ");
			PosparameXML pos = new PosparameXML(conn); 
			String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
			if("5".equals(infor)){
				buf.append(" r4502='"+a_code+"'");
			}else{
				buf.append("("+arrlist.get(0)+"."+sp_flag+ " in ('01','07') or "+arrlist.get(0)+"."+sp_flag+ " is null)");
				sb.append(arrlist.get(0)+"."+sp_flag+ " in ('02','03')");
			}
			
			  //外部培训，走培训模块的管理范围权限
            if("2".equals(inforflag)){
                String strWhr = getTrainManagePrivWhr(userView, dbname);
                if(strWhr != null && strWhr.length() > 0){
                    sb.append(strWhr);
                    buf.append(strWhr);
                }
            }
            
			ContentDAO dao = new ContentDAO(conn);
			try {
				dao.update(buf.toString());
				if("D".equalsIgnoreCase(type)){
					dataIsNUll(dao,(String)arrlist.get(0),(String)arrlist.get(1));
				}
				check=true;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
			}
			if(!"5".equals(infor)){
				ResultSet rs = null;
				try {
					rs=dao.search(sb.toString());
					if(rs.next()){
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.b_plan.update.submit.approval.error1")+"！"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
				}
			}
		}

		return check;
	}
	/**
	 *  批量计算
	 * @param conn
	 * @param uv
	 * @param dbname //人员库
	 * @param setname 
	 * @param a_code //计算部门或单位范围
	 * @param results 
	 * @param history //是否计算历史记录中的数据
	 * @param viewsearch //是否只计算结果表中的数据
	 * @param infor //1.人员 2.单位 3.职位 5.培训
	 * @return
	 */
	public boolean colUpdate(Connection conn,UserView uv,String dbname,
				String setname,String a_code,String results,
				String history,String viewsearch,String infor) throws GeneralException{
		this.userView=uv;
		boolean check=true;
		YksjParser yp=null;
		ArrayList list = colList(conn,dbname,infor);
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		if("5".equals(infor)){
			alUsedFields=DataDictionary.getFieldList("r45",Constant.ALL_FIELD_SET);
		}	
		int forvalue = YksjParser.forPerson;
		if("2".equals(infor)){
			forvalue=YksjParser.forUnit;
		}else if("3".equals(infor)){
			forvalue=YksjParser.forPosition;
		}
		for(int i=0;i<list.size();i++){
			int n1=0,n2=0;
			ArrayList arrlist = (ArrayList)list.get(i);
			/**不考虑指标权限,chenmengqing added 20100520*/
//			String pre = uv.analyseFieldPriv((String)arrlist.get(1));
//			pre=pre!=null&&pre.trim().length()>0?pre:"0";
//
//			if(!pre.equals("2"))
//				continue;
			FieldItem fielditem = DataDictionary.getFieldItem((String)arrlist.get(1));
			if(fielditem==null) {
                continue;
            }
			String type = fielditem.getItemtype();
			StringBuffer buf = new StringBuffer();
			String conTable=(String)arrlist.get(0);
			
			String itemid="";
			if("1".equals(infor)) {
                itemid="A0100";
            } else if("2".equals(infor)) {
                itemid="B0110";
            } else if("3".equals(infor)) {
                itemid="E01A1";
            } else {
                itemid="A0100";
            }
			
			buf.append("update ");
			buf.append(conTable);
			buf.append(" set ");
			
			boolean modTimeFlag = isExistField("ModTime", conTable, conn);
            boolean modUserNameFlag = isExistField("ModUserName", conTable, conn);
            if(modTimeFlag){
            	Timestamp timestamp = new Timestamp((new Date()).getTime());
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	String newDate = sdf.format(timestamp);
                buf.append(" ModTime = ");
                buf.append(Sql_switcher.dateValue(newDate) + ", ");
            }
            
            if(modTimeFlag){
                buf.append(" ModUserName = '");
                buf.append(uv.getUserName() + "', ");
            }
            
            
			buf.append(arrlist.get(1));
			buf.append("=(select ");
			n1=buf.length();
			yp = new YksjParser(uv,alUsedFields,
					YksjParser.forSearch,getDataType(type),forvalue ,"Ht",dbname);
			try {
				if((String)arrlist.get(2)!=null&&arrlist.get(2).toString().length()>0){
					yp.setCon(conn);
					String tablename="";
					if("5".equals(infor)){
						yp.Verify_where((String)arrlist.get(2));
						tablename = "r45";
					}else{
						if("1".equals(infor)) {
                            yp.run((String)arrlist.get(2),conTable.replace(dbname, ""));
                        } else if("4".equals(infor)) {
                            yp.run((String)arrlist.get(2),conTable.replace(dbname, ""));
                        } else {
                            yp.run((String)arrlist.get(2),conTable);
                        }
						tablename = yp.getTempTableName();
					}
					
					StringBuffer strsql = new StringBuffer();
					if(yp.getSQL()==null||yp.getSQL().trim().length()<1) {
                        continue;
                    }
					strsql.append(yp.getSQL());
					strsql.append(" from ");
					strsql.append(tablename);
					strsql.append(" where ");
					if("1".equals(infor)|| "4".equals(infor)){
						strsql.append(tablename);
						strsql.append(".A0100=");
						strsql.append(conTable+".A0100");
//						if(!fielditem.isMainSet()){
//							strsql.append(" and ");
//							strsql.append(tablename);
//							strsql.append(".I9999=");
//							strsql.append(conTable+".I9999");
//						}
					}else if("2".equals(infor)){
						strsql.append(tablename);
						strsql.append(".B0110=");
						strsql.append(conTable+".B0110");
					}else if("3".equals(infor)){
						strsql.append(tablename);
						strsql.append(".E01A1=");
						strsql.append(conTable+".E01A1");
					}else if("5".equals(infor)){
						strsql.append(tablename);
						strsql.append(".R4501=");
						strsql.append(conTable+".R4501");
					}
					strsql.append(")");
					n2=strsql.length();
					if("1".equals(infor)|| "4".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".A0100 IN (SELECT A0100 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("2".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".B0110 IN (SELECT B0110 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("3".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".E01A1 IN (SELECT E01A1 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("5".equals(infor)){//培训费用
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".R4501 IN (SELECT R4501 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}
					buf.append(strsql);
				}else{
					continue;
				}
			} catch (GeneralException e) {
				check=false;
				//e.printStackTrace();
				continue;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
				continue;
			}
			if("1".equals(history)){
				if(!fielditem.isMainSet() && !"5".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+".i9999=(select max(i9999) from ");
					buf.append(conTable);
					buf.append(" b where  ");
					buf.append(conTable);
					buf.append("."+itemid+" = b."+itemid+")");
				}
			}
			if("1".equals(viewsearch)){
				
				if("1".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+dbname+"result)");
				}else if("2".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+"Bresult)");
				}else if("3".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+"Kresult)");
				}
			}
			buf.append(codeWhereStr(dbname,a_code,infor));//修改管理权限范围内的人员
			/*cmq 去掉这些代码 20100520,单位、岗位和人员公式计算也不用考虑这些公式，后台作业中定时任务
			StringBuffer sb = new StringBuffer();
			sb.append("select "+itemid+" from "+conTable+" ");
			sb.append(buf.substring(n1+n2));
			
			PosparameXML pos = new PosparameXML(conn); 
			String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
			String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
			if(setid!=null&&setid.equalsIgnoreCase(conTable)&&sp_flag!=null&&!sp_flag.equals(""))
			{
	     		buf.append(" and ("+conTable+"."+sp_flag+ " in ('01','07') or "+conTable+"."+sp_flag+" is null)");
	    		sb.append(" and "+conTable+"."+sp_flag+ " in ('02','03')");
			}
			*/
			ContentDAO dao = new ContentDAO(conn);
			try 
			{
				dao.update(buf.toString());
				if("D".equalsIgnoreCase(type)){
					dataIsNUll(dao,(String)conTable,(String)arrlist.get(1));
				}
				check=true;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
			}
			/* cmq 去掉这些代码 20100520 后台作业中定时任务
			RowSet rs = null;
			try {
				rs=dao.search(sb.toString());
				if(rs.next()){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.b_plan.update.submit.approval.error1")+"！"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			*/			
		}
		this.userView=null;
		return check;
	}
	
	public String codeWhere(String setname,String dbname){
		StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("A0100 in (select A0100 from ");
			sqlstr.append(dbname+"A01  ");
			String a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			if(a_code!=null&&a_code.trim().length()>2){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if(value.length()>0){
					sqlstr.append(" where ");
					if("UN".equalsIgnoreCase(codesetid)){
						sqlstr.append("B0110 like '");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sqlstr.append("E0122 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else if("@K".equalsIgnoreCase(codesetid)){
						sqlstr.append("E01A1 like'");
						sqlstr.append(value);
						sqlstr.append("%' ");
					}else{
						String[] codearr =a_code.split(":");
						if(codearr.length==3){
							sqlstr.append(codearr[1].toUpperCase()+"= '");
							sqlstr.append(codearr[2]);
							sqlstr.append("' ");
						}
					}
				}
			}
			sqlstr.append(")");
		
		return sqlstr.toString();
	}
	
	public String scanFormationBeforCalc(Connection conn, UserView uv, String dbname, String setname, String results, String history) throws GeneralException {

        String returnStr = "ok";
        this.userView = uv;
        ArrayList msglist = new ArrayList();
        ScanFormationBo scanFormationBo = new ScanFormationBo(conn, userView);
        if (scanFormationBo.doScan()) {
            DbWizard dbw = new DbWizard(conn);

            ContentDAO dao = new ContentDAO(conn);

            YksjParser yp = null;
            ArrayList list = colList(conn, dbname, "1");
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            int forvalue = YksjParser.forPerson;

            for (int i = 0; i < list.size(); i++) {
                ArrayList arrlist = (ArrayList) list.get(i);
                String conTable = (String) arrlist.get(0);
                String tmpUpdTable = "t#" + this.userView.getUserName() + "_hr_"+conTable;
                if (dbw.isExistTable(tmpUpdTable,false)){
                    dbw.dropTable(tmpUpdTable);
                }
             }  
            
            for (int i = 0; i < list.size(); i++) {
                ArrayList arrlist = (ArrayList) list.get(i);
                String conTable = (String) arrlist.get(0);
                String tmpUpdTable = "t#" + this.userView.getUserName() + "_hr_"+conTable;
                if (!dbw.isExistTable(tmpUpdTable,false)){
                    dbw.createTempTable(conTable, tmpUpdTable, conTable+".*,0 as nscanflag ", "", "");
                }

            }   
            
            for (int i = 0; i < list.size(); i++) {
                ArrayList arrlist = (ArrayList) list.get(i);
                String conTable = (String) arrlist.get(0);
                String curItemid = (String) arrlist.get(1);
                FieldItem fielditem = DataDictionary.getFieldItem(curItemid);
                if (fielditem == null) {
                    continue;
                }
                String type = fielditem.getItemtype();
                String useditemid = "A0100";
                if (!fielditem.isMainSet()) {
                    useditemid = useditemid + ",i9999";
                }
                useditemid = useditemid + "," + curItemid;
                String tmpUpdTable = "t#" + this.userView.getUserName() + "_hr_"+conTable;
    
                StringBuffer buf = new StringBuffer();
                buf.append("update ");
                buf.append(tmpUpdTable);
                buf.append(" set ");
                
                buf.append("nscanflag=1 ,");
                buf.append(curItemid);
                buf.append("=");
                yp = new YksjParser(uv, alUsedFields, YksjParser.forSearch, getDataType(type), forvalue, "Ht", dbname);
                try {
                	//20141215 dengcan 基于查询结果的计算
            		if("1".equals(results)){
            				StringBuffer where_str=new StringBuffer("");
            				if(uv.getStatus()==0){
            					where_str.append(" select A0100 from ");
            					where_str.append(uv.getUserName()+dbname+"result ");
            				}else if(userView.getStatus()==4){
            					where_str.append(" select obj_id from ");
            					where_str.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0");
            				}
            				yp.setWhereText(where_str.toString());
            		}
                	
                    if ((String) arrlist.get(2) != null && arrlist.get(2).toString().length() > 0) {
                        yp.setCon(conn);
                        String tablename = "";
                        yp.run((String) arrlist.get(2), conTable.replace(dbname, ""));
                        tablename = yp.getTempTableName();

                        StringBuffer strsql = new StringBuffer();
                        if (yp.getSQL() == null || yp.getSQL().trim().length() < 1) {
                            continue;
                        }
                        strsql.append("(select ");
                        strsql.append(yp.getSQL());
                        strsql.append(" from ");
                        strsql.append(tablename);
                        strsql.append(" where ");
                        strsql.append(tablename);
                        strsql.append(".A0100=");
                        strsql.append(tmpUpdTable + ".A0100");
                        strsql.append(")");
                        //zxj 20170818 日期和数值数据不能截取 jazz30410
                        if(!"D".equalsIgnoreCase(fielditem.getItemtype()) && !"N".equalsIgnoreCase(fielditem.getItemtype())) {
                            buf.append(Sql_switcher.substr(Sql_switcher.sqlToChar(strsql.toString()), "1", (fielditem.getItemlength() + "")));
                        } else {
                            buf.append(strsql.toString());
                        }
                    } else {
                        continue;
                    }
                } catch (GeneralException e) {
                    continue;
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                }

                boolean bPart = false;
                StringBuffer itemids = new StringBuffer();
                itemids.append(arrlist.get(1));

                if ("true".equals(scanFormationBo.getPart_flag()) && fielditem.getFieldsetid().equalsIgnoreCase(scanFormationBo.getPart_setid())) {// 兼职子集
                    String part_fld = "";
                    bPart = true;
                    part_fld = scanFormationBo.getPart_unit();
                    if ((part_fld != null) && (!"".equals(part_fld))) {
                        itemids.append(",b0110");
                    }
                    part_fld = scanFormationBo.getPart_dept();
                    if ((part_fld != null) && (!"".equals(part_fld))) {
                        itemids.append(",e0122");
                    }
                    part_fld = scanFormationBo.getPart_pos();
                    if ((part_fld != null) && (!"".equals(part_fld))) {
                        itemids.append(",e01a1");
                    }
                }
                if (("," + itemids + ",").indexOf(",e01a1,") == -1) {
                    scanFormationBo.setPosChange(false);
                }
                if (scanFormationBo.needDoScan(dbname + ',', itemids.toString())) {

                    buf.append(" where 1=1");
                    if (!fielditem.isMainSet()) {// 子集
                        if ("1".equals(history) || (!bPart)) {// 只取当前记录或非兼职子集
                            buf.append(" and ");
                            buf.append(tmpUpdTable + ".i9999=(select max(i9999) from ");
                            buf.append(tmpUpdTable);
                            buf.append(" b where  ");
                            buf.append(tmpUpdTable);
                            buf.append(".A0100" + " = b.A0100" + ")");
                        } else {
                            ;
                        }
                    }

                    if ("1".equals(results)) {
                        if (uv.getStatus() == 0) {
                            buf.append(" and A0100 " + " in(select A0100 from ");
                            buf.append(uv.getUserName() + dbname + "result)");
                        } else if (userView.getStatus() == 4) {
                            buf.append(" and A0100 in(select obj_id from ");
                            buf.append("t_sys_result where upper(username)='" + userView.getUserName().toUpperCase() + "' and upper(nbase)='" + dbname.toUpperCase() + "' and flag=0)");
                        }
                    }
                    buf.append(" and " + codeWhere(setname, dbname, uv, conn));

                    try {
                        dao.update(buf.toString());
                        if ("D".equalsIgnoreCase(type)) {
                            dataIsNUll(dao, (String) tmpUpdTable, (String) arrlist.get(1));
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        StringBuffer strsql = new StringBuffer();
                        strsql.append("select A0100," + curItemid);
                        if (!fielditem.isMainSet()) {
                            strsql.append(",I9999");
                        }
                        
                        strsql.append(" from ");
                        strsql.append(tmpUpdTable);
                        strsql.append(" where nscanflag =1 ");
                        RowSet rs = dao.search(strsql.toString());
                        ArrayList beanList = new ArrayList();
                        while (rs.next()) {
                            LazyDynaBean scanBean = new LazyDynaBean();
                            String A0100 = "";
                            String I9999 = "";
                            A0100 = rs.getString("A0100");
                            if (!fielditem.isMainSet()) {
                                I9999 = rs.getString("I9999");
                            }
                            scanBean.set("objecttype", "1");
                            scanBean.set("nbase", dbname);
                            scanBean.set("a0100", A0100);
                            scanBean.set("ispart", "0");
                            scanBean.set("addflag", "0");
                            String value = "";
                            if ("A".equals(type) || "M".equals(type)) {
                                value = rs.getObject((curItemid)) != null ? rs.getObject((curItemid)).toString() : "";
                            } else if ("D".equals(type)) {
                                if (rs.getObject((curItemid)) != null && rs.getObject((curItemid)).toString().length() >= 10 && fielditem.getItemlength() == 10) {
                                    value = (new FormatValue().format(fielditem, rs.getObject((curItemid)).toString().substring(0, 10)));
                                } else if (rs.getObject((curItemid)) != null && rs.getObject((curItemid)).toString().length() >= 10 && fielditem.getItemlength() == 4) {
                                    value = (new FormatValue().format(fielditem, rs.getObject((curItemid)).toString().substring(0, 4)));
                                } else if (rs.getObject((curItemid)) != null && rs.getObject((curItemid)).toString().length() >= 10 && fielditem.getItemlength() == 7) {
                                    value = (new FormatValue().format(fielditem, rs.getObject((curItemid)).toString().substring(0, 7)));
                                } else {
                                    value = "";
                                }
                            } else {
                                value = (PubFunc.DoFormatDecimal(rs.getObject((curItemid)) != null ? rs.getObject((curItemid)).toString() : "", fielditem.getDecimalwidth()));
                            }

                            scanBean.set(curItemid, value);
                            if (bPart) {// 兼职子集
                                scanBean.set("ispart", "1");
                                scanBean.set("i9999", I9999);
                                String part_fld = "";
                                part_fld = scanFormationBo.getPart_unit();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("b0110", (String) scanBean.get(part_fld));
                                    }
                                }
                                part_fld = scanFormationBo.getPart_dept();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("e0122", (String) scanBean.get(part_fld));
                                    }
                                }
                                part_fld = scanFormationBo.getPart_pos();
                                if ((part_fld != null) && (!"".equals(part_fld))) {
                                    if ((String) scanBean.get(part_fld) != null) {
                                        scanBean.set("e01a1", (String) scanBean.get(part_fld));
                                    }
                                }
                            }

                            beanList.add(scanBean);

                        }
                        scanFormationBo.execDate2TmpTable(beanList);
                        String mess = scanFormationBo.isOverstaffs();
                        if (!"ok".equals(mess)) {
                            if ("warn".equals(scanFormationBo.getMode())) {
                                msglist.add(mess);
                            } else {
                                throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
            for (int i = 0; i < list.size(); i++) {
                ArrayList arrlist = (ArrayList) list.get(i);
                String conTable = (String) arrlist.get(0);
                String tmpUpdTable = "t#" + this.userView.getUserName() + "_hr_"+conTable;
                if (dbw.isExistTable(tmpUpdTable,false)){
                    dbw.dropTable(tmpUpdTable);
                }
            }  
            
            if(msglist.size()>0){
                StringBuffer msg = new StringBuffer();
                for(int i=0;i<msglist.size();i++){
                    if(msglist.size()>1){
                        msg.append((i+1)+":"+msglist.get(i)+"\\n");
                    }else{
                        msg.append(msglist.get(i));
                    }
                }
                returnStr =msg.toString();
            }
        }


        return returnStr;
    }
	
	public boolean colUpdate(Connection conn,UserView uv,String dbname,
			String setname,String results,
			String history,String inforflag) throws GeneralException{
	this.userView=uv;
	boolean check=true;
	YksjParser yp=null;
	ArrayList list = colList(conn,dbname,"1");
	ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
			Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
	
	int forvalue = YksjParser.forPerson;
	for(int i=0;i<list.size();i++){
		// int n1=0,n2=0;
		ArrayList arrlist = (ArrayList)list.get(i);
		FieldItem fielditem = DataDictionary.getFieldItem((String)arrlist.get(1));
		if(fielditem==null || "0".equals(fielditem.getUseflag())) {
            continue;
        }
		
		String type = fielditem.getItemtype();
		StringBuffer buf = new StringBuffer();
		String conTable=(String)arrlist.get(0);
		
		String itemid="";
		itemid="A0100";
		
		buf.append("update ");
		buf.append(conTable);
		buf.append(" set ");
		buf.append(arrlist.get(1));
		buf.append("=");
		//n1=buf.length();
		yp = new YksjParser(uv,alUsedFields,
				YksjParser.forSearch,getDataType(type),forvalue ,"Ht",dbname);
		//20141215 dengcan 基于查询结果的计算
		if("1".equals(results)){
				StringBuffer where_str=new StringBuffer("");
				if(uv.getStatus()==0){
					where_str.append(" select "+itemid+" from ");
					where_str.append(uv.getUserName()+dbname+"result ");
				}else if(userView.getStatus()==4){
					where_str.append(" select obj_id from ");
					where_str.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0");
				}
				yp.setWhereText(where_str.toString());
		}
		
		try {
			if((String)arrlist.get(2)!=null&&arrlist.get(2).toString().length()>0){
				yp.setCon(conn);
				String tablename="";
				yp.run((String)arrlist.get(2),conTable.replace(dbname, ""));
				tablename = yp.getTempTableName();
				
				StringBuffer strsql = new StringBuffer();
				if(yp.getSQL()==null||yp.getSQL().trim().length()<1) {
                    continue;
                }
				strsql.append("(select ");
				strsql.append(yp.getSQL());
				strsql.append(" from ");
				strsql.append(tablename);
				strsql.append(" where ");
				strsql.append(tablename);
				strsql.append(".A0100=");
				strsql.append(conTable+".A0100");
//				if(!fielditem.isMainSet()){
//					strsql.append(" and ");
//					strsql.append(tablename);
//					strsql.append(".I9999=");
//					strsql.append(conTable+".I9999");
//					
//				}
				
				strsql.append(")");
				if("A".equals(fielditem.getItemtype())){
					buf.append(Sql_switcher.substr(Sql_switcher.sqlToChar(strsql.toString()),"1",(fielditem.getItemlength()+"")));
				}else{
					buf.append(strsql.toString());
				}
			}else{
				continue;
			}
		} catch (GeneralException e) {
			check=false;
			//e.printStackTrace();
			continue;
		} catch (SQLException e) {
			check=false;
			e.printStackTrace();
			continue;
		}
		
		buf.append(" where 1=1");
		if("1".equals(history)){
			if(!fielditem.isMainSet()){
				buf.append(" and ");
				buf.append(conTable+".i9999=(select max(i9999) from ");
				buf.append(conTable);
				buf.append(" b where  ");
				buf.append(conTable);
				buf.append("."+itemid+" = b."+itemid+")");
			}
		}
		if("1".equals(results)){
			{
				if(uv.getStatus()==0){
					buf.append(" and "+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+dbname+"result)");
				}else if(userView.getStatus()==4){
					buf.append(" and A0100 in(select obj_id from ");
					buf.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0)");
				}
			}
		}
		buf.append(" and "+codeWhere(setname,dbname,uv,conn));
		
		 //外部培训，走培训模块的管理范围权限
        if("2".equals(inforflag)){
            String strWhr = getTrainManagePrivWhr(userView, dbname);
            if(strWhr != null && strWhr.length() > 0) {
                buf.append(strWhr);
            }
        }
        
		ContentDAO dao = new ContentDAO(conn);
		try 
		{
			dao.update(buf.toString());
			if("D".equalsIgnoreCase(type)){
				dataIsNUll(dao,(String)conTable,(String)arrlist.get(1));
			}
			check=true;
		} catch (SQLException e) {
			check=false;
			e.printStackTrace();
		}		
	}
	this.userView=null;
	return check;
}
	/**
	 *  批量计算
	 * @param conn
	 * @param uv
	 * @param dbname //人员库
	 * @param setname 
	 * @param a_code //计算部门或单位范围
	 * @param results 
	 * @param history //是否计算历史记录中的数据
	 * @param viewsearch //是否只计算结果表中的数据
	 * @param infor //1.人员 2.单位 3.职位 5.培训
	 * @return
	 */
	public boolean colUpdate(Connection conn,UserView uv,String dbname,
				String setname,String a_code,String results,
				String history,String viewsearch)throws GeneralException{
		boolean check=true;
		YksjParser yp=null;
		ArrayList list = colList(conn,dbname,"2");
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);

		
		int forvalue=YksjParser.forUnit;
		
		for(int i=0;i<list.size();i++){
			int n1=0,n2=0;
			ArrayList arrlist = (ArrayList)list.get(i);
			String pre = uv.analyseFieldPriv((String)arrlist.get(1));
			pre=pre!=null&&pre.trim().length()>0?pre:"0";
			if(uv.isSuper_admin()) {
                pre="2";
            }
			if(!"2".equals(pre)) {
                continue;
            }
			FieldItem fielditem = DataDictionary.getFieldItem((String)arrlist.get(1));
			if(fielditem==null) {
                continue;
            }
			String type = fielditem.getItemtype();
			StringBuffer buf = new StringBuffer();
			String conTable=(String)arrlist.get(0);
			
			String itemid="B0110";

			buf.append("update ");
			buf.append(conTable);
			buf.append(" set ");
			buf.append(arrlist.get(1));
			buf.append("=(select ");
			n1=buf.length();
			yp = new YksjParser(uv,alUsedFields,
					YksjParser.forSearch,getDataType(type),forvalue ,"Ht",dbname);
			try {
				if((String)arrlist.get(2)!=null&&arrlist.get(2).toString().length()>0){
					yp.setCon(conn);
					yp.run((String)arrlist.get(2),conTable);
					String tablename = yp.getTempTableName();
					
					StringBuffer strsql = new StringBuffer();
					if(yp.getSQL()==null||yp.getSQL().trim().length()<1) {
                        continue;
                    }
					strsql.append(yp.getSQL());
					strsql.append(" from ");
					strsql.append(tablename);
					strsql.append(" where ");

					strsql.append(tablename);
					strsql.append(".B0110=");
					strsql.append(conTable+".B0110");

					strsql.append(")");
					n2=strsql.length();
					strsql.append(" WHERE ");
					strsql.append(conTable);
					strsql.append(".B0110 in(select codeitemid from organization where parentid='");
					strsql.append(a_code.substring(2));
					strsql.append("' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date)");
					
					buf.append(strsql);
				}else{
					continue;
				}
			} catch (GeneralException e) {
				check=false;
		//		e.printStackTrace();
				continue;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
				continue;
			}

			buf.append(" and ");
			buf.append(conTable+".i9999=(select max(i9999) from ");
			buf.append(conTable);
			buf.append(" b where  ");
			buf.append(conTable);
			buf.append("."+itemid+" = b."+itemid+")");
			StringBuffer sb = new StringBuffer();
			sb.append("select "+itemid+" from "+conTable+" ");
			sb.append(buf.substring(n1+n2));
			PosparameXML pos = new PosparameXML(conn); 
			String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
			buf.append(" and ("+conTable+"."+sp_flag+ " in ('01','07') or "+conTable+"."+sp_flag+ " is null)");
			sb.append(" and "+conTable+"."+sp_flag+ " in ('02','03')");
			ContentDAO dao = new ContentDAO(conn);
			try {
				dao.update(buf.toString());
				if("D".equalsIgnoreCase(type)){
					dataIsNUll(dao,(String)conTable,(String)arrlist.get(1));
				}
				check=true;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
			}
			ResultSet rs = null;
			try {
				rs=dao.search(sb.toString());
				if(rs.next()){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.b_plan.update.submit.approval.error1")+"！"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
			}
		}

		return check;
	}
	public void dataIsNUll(ContentDAO dao,String tablename,String itemid){
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart=wss.getDataValue(itemid,"=","1900-01-01");
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(tablename);
		buf.append(" set ");
		buf.append(itemid);
		buf.append("=null where ");
		buf.append(tempstart);
		try {
			int counts = dao.update(buf.toString());
			//System.out.println("------------->"+"后台作业计算sql语句("+counts+")："+buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 批量计算
	 * @param setname //子集名称id
	 * @param dbname //库前缀
	 * @return check
	 */
	public ArrayList colList(Connection conn,String dbname,String infor){
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		String unit_type="2";//1=合同 2=人员 3=机构 4=职位 5=培训
		if("1".equals(infor))//infor=1.人员 2.单位 3.职位 5.培训
        {
            unit_type="2";
        } else if("2".equals(infor)){
			unit_type="3";
			dbname="";
		}else if("3".equals(infor)){
			unit_type="4";
			dbname="";
		}else if("4".equals(infor)){
			unit_type="1";
		}else if("5".equals(infor)){
			unit_type="5";
			dbname="";
		}
		String strsql = "select Classpre from informationclass order by inforid";//赵国栋 获取指标体系内所有子集前缀的集合
		ArrayList classprelist = new ArrayList();
		try {
			RowSet rs = dao.search(strsql);
			while(rs.next()) {
				classprelist.add(rs.getString("Classpre"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sql = "select setid,itemid,formula from HRPFormula where unit_type="+unit_type+" and flag=1 order by db_type";
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String setid = rs.getString("setid");
				FieldSet fs = DataDictionary.getFieldSetVo(setid);
                if(fs == null || "0".equals(fs.getUseflag())) {
                    continue;
                }
                
				boolean isinformationclass = false;
				for(int i=0;i<classprelist.size();i++){
					String classpre = (String) classprelist.get(i);
					int lenght=classprelist.get(i).toString().length();
					if(classprelist.get(i).equals(setid.substring(0, classprelist.get(i).toString().length()))){//判断子集的前缀是否与指标体系内的前缀相同
						isinformationclass = true;
						break;
					}
				}
				if(isinformationclass){//缺陷3039 zgd 2014-7-9 指标体系与业务字典不同，业务字典没有读写权限控制 
					if("2".equals(this.userView.analyseFieldPriv(itemid))){//zhaogd 2014-3-6 获取有权限且打钩的指标（bug：在su下全勾选了，在非su下没有权限的指标也计算了）
						ArrayList arrlist = new ArrayList();
						String table = dbname+rs.getString("setid");
						String formula = rs.getString("formula");
						formula=formula!=null?formula:"";
						arrlist.add(table);
						arrlist.add(rs.getString("itemid"));
						arrlist.add(formula);
						list.add(arrlist);
					}
				}else{
					ArrayList arrlist = new ArrayList();
					String table = dbname+rs.getString("setid");
					String formula = rs.getString("formula");
					formula=formula!=null?formula:"";
					arrlist.add(table);
					arrlist.add(rs.getString("itemid"));
					arrlist.add(formula);
					list.add(arrlist);
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return list;
	}
	/**
	 * 保存排序结果
	 * @param sortstr 
	 * @return check
	 */
	public boolean saveSort(Connection conn,String sortstr){
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		String arr[] = sortstr.split("`");
		int n=1;
		ArrayList listvalue = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("update HRPFormula set db_type=? where fid=?");
		for(int i=0;i<arr.length;i++){
			String[] item_arr = arr[i].split("::");
			if(item_arr.length==3){
				String itemarr[] = item_arr[0].split("_");
				if(itemarr.length==2){
					ArrayList list = new ArrayList();
					list.add(dbtype(n));
					list.add(itemarr[0]);
					listvalue.add(list);
					n++;
				}
			}
		}
		try {
			if(listvalue.size()>0) {
                dao.batchUpdate(sql.toString(),listvalue);
            }
			check=true;
		} catch (SQLException e) {
			check=false;
			e.printStackTrace();
		}
		return check;
	}
	private String dbtype(int n){
		String dbtype="";
		String dblen = n+"";
		for(int i=0;i<5-dblen.length();i++){
			dbtype+="0";
		}
		dbtype+=n;
		return dbtype;
	}
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	/**
	 * 如果有操作单位走操作单位，否则走管理范围
	 * @return
	 */
	public String doInitOrgUnit(String itemid,String flag){
		StringBuffer sql=new StringBuffer();
			if("1".equals(flag)){//数据联动走管理范围
				sql.append(" and (");
				//String itemid=this.getItemid();
				String codesetid=this.userView.getManagePrivCode();
				String codeitemid=this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codesetid)){
					sql.append(itemid+"='"+codeitemid+"' and ");
				}else if("UM".equalsIgnoreCase(codesetid)){
					//itemid="e0122";
					sql.append(itemid+"='"+codeitemid+"' and ");
				}else if("@K".equalsIgnoreCase(codesetid)){
					//itemid="e01a1";
					sql.append(itemid+"='"+codeitemid+"' and ");
				}
				sql.append("1=1)");
			}else{//编制管理走操作单位
				String orgunit=this.userView.getUnit_id();
				orgunit=orgunit.toUpperCase();
				sql.append(" and (");
				if(!"UN".equals(orgunit)){
					String str[]=orgunit.split("`");
					//String itemid=this.getItemid();
					for(int i=0;i<str.length;i++){
						if(str[i].indexOf("UN")!=-1){
							if(str[i].substring(2).length()==0){
								
								sql.append("parentid=codeitemid or ");
							}else{
								sql.append(itemid+" = '"+str[i].substring(2)+"' or ");
							}
							
						}else if(str[i].indexOf("UM")!=-1){
							//itemid="e0122";
							sql.append(itemid+" = '"+str[i].substring(2)+"' or ");
						}else{
							continue;
						}
					}
				}
				sql.append("1=2)");
				
			}
		return sql.toString();
	}
	public String getEntranceFlag() {
		return entranceFlag;
	}
	public void setEntranceFlag(String entranceFlag) {
		this.entranceFlag = entranceFlag;
	}
	
	public void setComputeScope(String computeScope) {
		this.computeScope = computeScope;
	}



	public boolean colUpdate(Connection conn,UserView uv,String dbname,
				String setname,String a_code,String results,
				String history,String viewsearch,String infor,String job_param) throws GeneralException{
		this.userView=uv;
		boolean check=true;
		YksjParser yp=null;
		//判断作业参数是否包含noHistory、autoExecute jingq add 2014.10.09
		job_param = job_param.toUpperCase();
		String autoExecute = "";
		String noHistory = "";
		if(job_param.indexOf("NOHISTORY=")!=-1&&job_param.indexOf("AUTOEXECUTE=")!=-1){
			int n = job_param.indexOf("NOHISTORY=");
			int a = job_param.indexOf("AUTOEXECUTE=");
			if(n<a){
				noHistory = job_param.substring(n+10, a);
				autoExecute = job_param.substring(a+12, job_param.length());
			} else {
				noHistory = job_param.substring(n+10, job_param.length());
				autoExecute = job_param.substring(a+12, n);
			}
		} else if(job_param.indexOf("NOHISTORY=")==-1&&job_param.indexOf("AUTOEXECUTE=")!=-1){
			autoExecute = job_param.substring(job_param.indexOf("AUTOEXECUTE=")+12, job_param.length());
		} else if(job_param.indexOf("NOHISTORY=")!=-1&&job_param.indexOf("AUTOEXECUTE=")==-1){
			noHistory = job_param.substring(job_param.indexOf("NOHISTORY=")+10, job_param.length());
		}
		autoExecute = autoExecute.replace(";", "");
		noHistory = noHistory.replace(";", "");
		
		//ArrayList list = colList(conn,dbname,infor);
		ArrayList list = new ArrayList();
		//如果含有autoExecute，则需要根据autoExecute取得list
		if(job_param.indexOf("AUTOEXECUTE=")!=-1){
			list = getColList(conn, dbname, autoExecute,infor);
		} else {
			list = colList(conn,dbname,infor);
		}
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
				Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		if("5".equals(infor)){
			alUsedFields=DataDictionary.getFieldList("r45",Constant.ALL_FIELD_SET);
		}	
		int forvalue = YksjParser.forPerson;
		if("2".equals(infor)){
			forvalue=YksjParser.forUnit;
		}else if("3".equals(infor)){
			forvalue=YksjParser.forPosition;
		}
		for(int i=0;i<list.size();i++){
			int n1=0,n2=0;
			ArrayList arrlist = (ArrayList)list.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem((String)arrlist.get(1));
			if(fielditem==null) {
                continue;
            }
			String type = fielditem.getItemtype();
			String fid = fielditem.getItemid();
			StringBuffer buf = new StringBuffer();
			String conTable=(String)arrlist.get(0);
			
			String itemid="";
			if("1".equals(infor)) {
                itemid="A0100";
            } else if("2".equals(infor)) {
                itemid="B0110";
            } else if("3".equals(infor)) {
                itemid="E01A1";
            } else {
                itemid="A0100";
            }
			
			buf.append("update ");
			buf.append(conTable);
			buf.append(" set ");
			buf.append(arrlist.get(1));
			buf.append("=(select ");
			n1=buf.length();
			yp = new YksjParser(uv,alUsedFields,
					YksjParser.forSearch,getDataType(type),forvalue ,"Ht",dbname);
			try {
				if((String)arrlist.get(2)!=null&&arrlist.get(2).toString().length()>0){
					yp.setCon(conn);
					String tablename="";
					if("5".equals(infor)){
						yp.Verify_where((String)arrlist.get(2));
						tablename = "r45";
					}else{
						if("1".equals(infor)) {
                            yp.run((String)arrlist.get(2),conTable.replace(dbname, ""));
                        } else if("4".equals(infor)) {
                            yp.run((String)arrlist.get(2),conTable.replace(dbname, ""));
                        } else {
                            yp.run((String)arrlist.get(2),conTable);
                        }
						tablename = yp.getTempTableName();
					}
					
					StringBuffer strsql = new StringBuffer();
					if(yp.getSQL()==null||yp.getSQL().trim().length()<1) {
                        continue;
                    }
					strsql.append(yp.getSQL());
					strsql.append(" from ");
					strsql.append(tablename);
					strsql.append(" where ");
					if("1".equals(infor)|| "4".equals(infor)){
						strsql.append(tablename);
						strsql.append(".A0100=");
						strsql.append(conTable+".A0100");
					}else if("2".equals(infor)){
						strsql.append(tablename);
						strsql.append(".B0110=");
						strsql.append(conTable+".B0110");
					}else if("3".equals(infor)){
						strsql.append(tablename);
						strsql.append(".E01A1=");
						strsql.append(conTable+".E01A1");
					}else if("5".equals(infor)){
						strsql.append(tablename);
						strsql.append(".R4501=");
						strsql.append(conTable+".R4501");
					}
					strsql.append(")");
					n2=strsql.length();
					if("1".equals(infor)|| "4".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".A0100 IN (SELECT A0100 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("2".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".B0110 IN (SELECT B0110 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("3".equals(infor)){
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".E01A1 IN (SELECT E01A1 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}else if("5".equals(infor)){//培训费用
						strsql.append(" WHERE ");
						strsql.append(conTable);
						strsql.append(".R4501 IN (SELECT R4501 FROM ");
						strsql.append(tablename);
						strsql.append(")");
					}
					buf.append(strsql);
				}else{
					continue;
				}
			} catch (GeneralException e) {
				check=false;
				continue;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
				continue;
			}
            //计算公式支持不计算历史记录
			if("".equals(job_param)||job_param==null){
				history = "0";
			//如果作业参数含有noHistory，不计算历史记录
			} else if(job_param.indexOf("=")!=-1){
				if(noHistory.indexOf((String)arrlist.get(1))==-1){
					history = "0";
				} else {
					history = "1";
				}
			}else{
				String jp = job_param.toUpperCase();
				String id = fid.toUpperCase();
				if(jp.indexOf(id)==-1){
					history = "0";
				}else{
					history = "1";
				}
			}
			if("1".equals(history)){
				if(!fielditem.isMainSet() && !"5".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+".i9999=(select max(i9999) from ");
					buf.append(conTable);
					buf.append(" b where  ");
					buf.append(conTable);
					buf.append("."+itemid+" = b."+itemid+")");
				}
			}
			if("1".equals(viewsearch)){
				
				if("1".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+dbname+"result)");
				}else if("2".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+"Bresult)");
				}else if("3".equals(infor)){
					buf.append(" and ");
					buf.append(conTable+"."+itemid+" in(select "+itemid+" from ");
					buf.append(uv.getUserName()+"Kresult)");
				}
			}
			buf.append(codeWhereStr(dbname,a_code,infor));//修改管理权限范围内的人员
			/*cmq 去掉这些代码 20100520,单位、岗位和人员公式计算也不用考虑这些公式，后台作业中定时任务
			StringBuffer sb = new StringBuffer();
			sb.append("select "+itemid+" from "+conTable+" ");
			sb.append(buf.substring(n1+n2));
			
			PosparameXML pos = new PosparameXML(conn); 
			String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
			String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
			if(setid!=null&&setid.equalsIgnoreCase(conTable)&&sp_flag!=null&&!sp_flag.equals(""))
			{
	     		buf.append(" and ("+conTable+"."+sp_flag+ " in ('01','07') or "+conTable+"."+sp_flag+" is null)");
	    		sb.append(" and "+conTable+"."+sp_flag+ " in ('02','03')");
			}
			*/
			ContentDAO dao = new ContentDAO(conn);
			try 
			{
				dao.update(buf.toString());
				//System.out.println("------------->"+"后台作业计算sql语句："+buf.toString());
				if("D".equalsIgnoreCase(type)){
					dataIsNUll(dao,(String)conTable,(String)arrlist.get(1));
				}
				check=true;
			} catch (SQLException e) {
				check=false;
				e.printStackTrace();
			}
			/* cmq 去掉这些代码 20100520 后台作业中定时任务
			RowSet rs = null;
			try {
				rs=dao.search(sb.toString());
				if(rs.next()){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.b_plan.update.submit.approval.error1")+"！"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			*/			
		}
		this.userView=null;
		return check;
	}
	
	/**
	 * 根据itemid取得数据
	 * @Title: getColList   
	 * @Description:    
	 * @param @param conn
	 * @param @param dbname
	 * @param @param str
	 * @param @param infor
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	public ArrayList getColList(Connection conn,String dbname,String str,String infor){
		ArrayList list = new ArrayList();
		String[] strlist = str.split(",");
		PreparedStatement ps = null;
		String sql = "";
		String unit_type="2";//1=合同 2=人员 3=机构 4=职位 5=培训
		if("1".equals(infor))//infor=1.人员 2.单位 3.职位 5.培训
        {
            unit_type="2";
        } else if("2".equals(infor)){
			unit_type="3";
			dbname="";
		}else if("3".equals(infor)){
			unit_type="4";
			dbname="";
		}else if("4".equals(infor)){
			unit_type="1";
		}else if("5".equals(infor)){
			unit_type="5";
			dbname="";
		}
		StringBuffer itemidlist = new StringBuffer();
		for (int i = 0; i < strlist.length; i++) {
			//trim,避免参数输入中包含空格
			String itemId = strlist[i].trim();
			if (StringUtils.isBlank(itemId)) {
                continue;
            }

			itemidlist.append("'"+ itemId +"',");
		}

		String itemIds = itemidlist.toString().toUpperCase().substring(0,itemidlist.toString().length()-1);
		sql = "select setid,itemid,formula from HRPFormula where upper(itemid) in ("+ itemIds +") and unit_type="+unit_type+" order by db_type";
		try {
			ContentDAO dao = new ContentDAO(conn);	
			ResultSet rs =dao.search(sql, new ArrayList());
			while(rs.next()){
				String setid = dbname+rs.getString("setid");
				String itemid = rs.getString("itemid");
				String formula = rs.getString("formula");
				ArrayList arr = new ArrayList();
				arr.add(setid);
				arr.add(itemid);
				arr.add(formula);
				list.add(arr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}



	public void setStrid(String strid) {
		this.strid = strid;
	}
	
	private boolean isExistField(String fieldName, String tableName, Connection conn) {
	    DbWizard db = new DbWizard(conn);
	    return db.isExistField(tableName, fieldName, false);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
	
}
