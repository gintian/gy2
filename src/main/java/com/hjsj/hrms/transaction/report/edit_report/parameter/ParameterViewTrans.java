package com.hjsj.hrms.transaction.report.edit_report.parameter;

import com.hjsj.hrms.businessobject.report.TpageBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ParameterViewTrans extends IBusiness {
	public void execute() throws GeneralException {
		String operateObject = (String) getFormHM().get("operateObject");
		String unitCode = (String) getFormHM().get("unitcode");
		String global_parameter = "";
		String type_parameter = "";
		String myValue = "";
		if ("2".equals(operateObject.trim())) {
			global_parameter = "tt_p";//基于报表汇总
			type_parameter = "tt_s";
			myValue = unitCode;
		}
		if ("1".equals(operateObject.trim())) {
			getUserView().getUserName();//基于编辑报表
			global_parameter = "tp_p";
			type_parameter = "tp_s";
			myValue = getUserView().getUserId();
		}
		ArrayList myList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String tabid = (String) getFormHM().get("tabid");
		String paramscope = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("paramscope");
		this.getFormHM().put("paramscope",paramscope);
		TpageBo tbo = new TpageBo(this.getFrameconn(),this.userView);//添加userview  changxy 20170118
		RowSet recset = null;
		int num = 0;
		try {
			recset = dao.search("select tsortid from tname where tabid = "
					+ tabid);
			if (recset.next()) {
				String tsortid = recset.getString("tsortid");
				getFormHM().put("tsortid", tsortid);
				StringBuffer sb = new StringBuffer(
				"select paramid,paramscope, paramname, paramtype, paramNull, paramename, paramcode, paramlen, paramfmt, paramcon from tparam");
				if("0".equals(paramscope)){//全局的所有表都能看到
					sb.append(" where paramscope=" + paramscope+ " and paramname in (select Hz from tpage where Flag = '9')");
//					sb.append(" where paramscope=" + paramscope+ " and paramname in");
//					sb.append(" (select hz from tpage where tabid ="+tabid+") ");
				}else if("1".equals(paramscope)){//表类参数按类划分   但是名字一样可能会有问题  zhaoxg 2013-4-20
//					sb.append(" where paramscope=" + paramscope+ "");
					sb.append(" where paramscope=" + paramscope+ " and paramname in");
					sb.append(" (select hz from tpage where tabid in ");
					sb.append("(select tabid from Tname where tsortid = " + tsortid
							+ ") and Flag = '9') order by paramid");
				}
//				System.out.println("--->" + sb.toString());
				frowset = dao.search(sb.toString());
				RowSet rs = null;
				RowSet temp = null;
				ResultSetMetaData   rsmd = frowset.getMetaData();
				ResultSetMetaData   rsmd2 = null;
				while (frowset.next()) {
					 
					num++;
					int nlen=0;
		 				nlen=Integer.parseInt((frowset.getString("paramlen")==null||frowset.getString("paramlen").length()<1)?"0":frowset.getString("paramlen"));
		 				
		 			int type = getType((frowset.getString("paramtype")==null||frowset.getString("paramtype").length()<1)?"0":frowset.getString("paramtype"));
		 			DbWizard dbWizard=new DbWizard(this.getFrameconn());
		 			if("1".equals(paramscope)){
		 				
		 				if(!dbWizard.isExistField("tp_s"+this.getFormHM().get("tsortid"),""+frowset.getString("paramename"),false)){//不存在就创建
		 					Table table=new Table("tp_s"+this.getFormHM().get("tsortid"));
							Field field=null;
							 switch(type)
								 {
										 case java.sql.Types.INTEGER:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.INT);
											 field.setLength(nlen);
											table.addField(field);
											break;
								 		case java.sql.Types.DOUBLE:
								 		case java.sql.Types.NUMERIC:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.FLOAT);
												 field.setDecimalDigits(6);
												 field.setLength(nlen);		
												table.addField(field);
											break;
										 case java.sql.Types.DATE:
										  case java.sql.Types.TIMESTAMP:
										  case java.sql.Types.TIME :	
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.DATE);
												table.addField(field);
											break;
										 case java.sql.Types.CLOB:
										  case java.sql.Types.LONGVARCHAR:
										  case java.sql.Types.BLOB:
										  case java.sql.Types.LONGVARBINARY:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.CLOB);
												table.addField(field);
											break;
										  default:
											  field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.STRING);
											 field.setLength(nlen);
											table.addField(field);
												break;	
									}
							 dbWizard.addColumns(table);
		 				}else{
							   rsmd2 = dao.search(" select "+frowset.getString("paramename")+" from tp_s"+this.getFormHM().get("tsortid")).getMetaData();
							   int nlen2=0;
							   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    		 				nlen2=rsmd2.getPrecision(1);
		    		 			else
		    		 				nlen2=rsmd2.getColumnDisplaySize(1);
							   if(nlen2<nlen){
									Table table=new Table("tp_s"+this.getFormHM().get("tsortid"));
									Field field=null;
								   switch(type)
									 {
									 case java.sql.Types.INTEGER:
										 field=new Field(frowset.getString("paramename"));
										 field.setDatatype(DataType.INT);
										 field.setLength(nlen);
										table.addField(field);
										break;
							 		case java.sql.Types.DOUBLE:
							 		case java.sql.Types.NUMERIC:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.FLOAT);
											 field.setDecimalDigits(6);
											 field.setLength(nlen);		
											table.addField(field);
										break;
									 case java.sql.Types.DATE:
									  case java.sql.Types.TIMESTAMP:
									  case java.sql.Types.TIME :	
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.DATE);
											table.addField(field);
										break;
									 case java.sql.Types.CLOB:
									  case java.sql.Types.LONGVARCHAR:
									  case java.sql.Types.BLOB:
									  case java.sql.Types.LONGVARBINARY:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.CLOB);
											table.addField(field);
										break;
									  default:
										  field=new Field(frowset.getString("paramename"));
										 field.setDatatype(DataType.STRING);
										 field.setLength(nlen);
										table.addField(field);
											break;	
								}
								   dbWizard.alterColumns(table);
							   }
		 				}
		 				if(dbWizard.isExistTable("tt_s"+this.getFormHM().get("tsortid"),false)){
		 					if(!dbWizard.isExistField("tt_s"+this.getFormHM().get("tsortid"),""+frowset.getString("paramename"),false)){//不存在就创建
		 						Table table=new Table("tt_s"+this.getFormHM().get("tsortid"));
		 						Field field=null;
		 						 switch(type)
		 							 {
		 									 case java.sql.Types.INTEGER:
		 										 field=new Field(frowset.getString("paramename"));
		 										 field.setDatatype(DataType.INT);
		 										 field.setLength(nlen);
		 										table.addField(field);
		 										break;
		 							 		case java.sql.Types.DOUBLE:
		 							 		case java.sql.Types.NUMERIC:
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.FLOAT);
		 											 field.setDecimalDigits(6);
		 											 field.setLength(nlen);		
		 											table.addField(field);
		 										break;
		 									 case java.sql.Types.DATE:
		 									  case java.sql.Types.TIMESTAMP:
		 									  case java.sql.Types.TIME :	
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.DATE);
		 											table.addField(field);
		 										break;
		 									 case java.sql.Types.CLOB:
		 									  case java.sql.Types.LONGVARCHAR:
		 									  case java.sql.Types.BLOB:
		 									  case java.sql.Types.LONGVARBINARY:
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.CLOB);
		 											table.addField(field);
		 										break;
		 									  default:
		 										  field=new Field(frowset.getString("paramename"));
		 										 field.setDatatype(DataType.STRING);
		 										 field.setLength(nlen);
		 										table.addField(field);
		 											break;	
		 								}
		 						 dbWizard.addColumns(table);
		 					}else{//同步该表表类参数，只同步参数长度变大，只修改字符和数值型
		 						   rsmd2 = dao.search(" select "+frowset.getString("paramename")+" from tt_s"+this.getFormHM().get("tsortid")).getMetaData();
		 						   int nlen2=0;
		 						   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		 	    		 				nlen2=rsmd2.getPrecision(1);
		 	    		 			else
		 	    		 				nlen2=rsmd2.getColumnDisplaySize(1);
		 						   if(nlen2<nlen){
		 								Table table=new Table("tt_s"+this.getFormHM().get("tsortid"));
		 								Field field=null;
		 							   switch(type)
		 								 {
										 case java.sql.Types.INTEGER:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.INT);
											 field.setLength(nlen);
											table.addField(field);
											break;
								 		case java.sql.Types.DOUBLE:
								 		case java.sql.Types.NUMERIC:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.FLOAT);
												 field.setDecimalDigits(6);
												 field.setLength(nlen);		
												table.addField(field);
											break;
										 case java.sql.Types.DATE:
										  case java.sql.Types.TIMESTAMP:
										  case java.sql.Types.TIME :	
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.DATE);
												table.addField(field);
											break;
										 case java.sql.Types.CLOB:
										  case java.sql.Types.LONGVARCHAR:
										  case java.sql.Types.BLOB:
										  case java.sql.Types.LONGVARBINARY:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.CLOB);
												table.addField(field);
											break;
										  default:
											  field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.STRING);
											 field.setLength(nlen);
											table.addField(field);
												break;	
									}
		 							   dbWizard.alterColumns(table);
		 						   }
		 					}
		 					}
		 			}else{		 				
		 				if(!dbWizard.isExistField("tp_p",""+frowset.getString("paramename"),false)){//不存在就创建
		 					Table table=new Table("tp_p");
							Field field=null;
							 switch(type)
								 {
										 case java.sql.Types.INTEGER:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.INT);
											 field.setLength(nlen);
											table.addField(field);
											break;
								 		case java.sql.Types.DOUBLE:
								 		case java.sql.Types.NUMERIC:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.FLOAT);
												 field.setDecimalDigits(6);
												 field.setLength(nlen);		
												table.addField(field);
											break;
										 case java.sql.Types.DATE:
										  case java.sql.Types.TIMESTAMP:
										  case java.sql.Types.TIME :	
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.DATE);
												table.addField(field);
											break;
										 case java.sql.Types.CLOB:
										  case java.sql.Types.LONGVARCHAR:
										  case java.sql.Types.BLOB:
										  case java.sql.Types.LONGVARBINARY:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.CLOB);
												table.addField(field);
											break;
										  default:
											  field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.STRING);
											 field.setLength(nlen);
											table.addField(field);
												break;	
									}
							 dbWizard.addColumns(table);
		 				}else{
							   rsmd2 = dao.search(" select "+frowset.getString("paramename")+" from tp_p").getMetaData();
							   int nlen2=0;
							   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    		 				nlen2=rsmd2.getPrecision(1);
		    		 			else
		    		 				nlen2=rsmd2.getColumnDisplaySize(1);
							   if(nlen2<nlen){
									Table table=new Table("tp_p");
									Field field=null;
								   switch(type)
									 {
									 case java.sql.Types.INTEGER:
										 field=new Field(frowset.getString("paramename"));
										 field.setDatatype(DataType.INT);
										 field.setLength(nlen);
										table.addField(field);
										break;
							 		case java.sql.Types.DOUBLE:
							 		case java.sql.Types.NUMERIC:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.FLOAT);
											 field.setDecimalDigits(6);
											 field.setLength(nlen);		
											table.addField(field);
										break;
									 case java.sql.Types.DATE:
									  case java.sql.Types.TIMESTAMP:
									  case java.sql.Types.TIME :	
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.DATE);
											table.addField(field);
										break;
									 case java.sql.Types.CLOB:
									  case java.sql.Types.LONGVARCHAR:
									  case java.sql.Types.BLOB:
									  case java.sql.Types.LONGVARBINARY:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.CLOB);
											table.addField(field);
										break;
									  default:
										  field=new Field(frowset.getString("paramename"));
										 field.setDatatype(DataType.STRING);
										 field.setLength(nlen);
										table.addField(field);
											break;	
								}
								   dbWizard.alterColumns(table);
							   }
		 				}
		 				if(dbWizard.isExistTable("tt_p",false)){
		 					if(!dbWizard.isExistField("tt_p",""+frowset.getString("paramename"),false)){//不存在就创建
		 						Table table=new Table("tt_p");
		 						Field field=null;
		 						 switch(type)
		 							 {
		 									 case java.sql.Types.INTEGER:
		 										 field=new Field(frowset.getString("paramename"));
		 										 field.setDatatype(DataType.INT);
		 										 field.setLength(nlen);
		 										table.addField(field);
		 										break;
		 							 		case java.sql.Types.DOUBLE:
		 							 		case java.sql.Types.NUMERIC:
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.FLOAT);
		 											 field.setDecimalDigits(6);
		 											 field.setLength(nlen);		
		 											table.addField(field);
		 										break;
		 									 case java.sql.Types.DATE:
		 									  case java.sql.Types.TIMESTAMP:
		 									  case java.sql.Types.TIME :	
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.DATE);
		 											table.addField(field);
		 										break;
		 									 case java.sql.Types.CLOB:
		 									  case java.sql.Types.LONGVARCHAR:
		 									  case java.sql.Types.BLOB:
		 									  case java.sql.Types.LONGVARBINARY:
		 											 field=new Field(frowset.getString("paramename"));
		 											 field.setDatatype(DataType.CLOB);
		 											table.addField(field);
		 										break;
		 									  default:
		 										  field=new Field(frowset.getString("paramename"));
		 										 field.setDatatype(DataType.STRING);
		 										 field.setLength(nlen);
		 										table.addField(field);
		 											break;	
		 								}
		 						 dbWizard.addColumns(table);
		 					}else{//同步该表表类参数，只同步参数长度变大，只修改字符和数值型
		 						   rsmd2 = dao.search(" select "+frowset.getString("paramename")+" from tt_p").getMetaData();
		 						   int nlen2=0;
		 						   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		 	    		 				nlen2=rsmd2.getPrecision(1);
		 	    		 			else
		 	    		 				nlen2=rsmd2.getColumnDisplaySize(1);
		 						   if(nlen2<nlen){
		 								Table table=new Table("tt_p");
		 								Field field=null;
		 							   switch(type)
		 								 {
										 case java.sql.Types.INTEGER:
											 field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.INT);
											 field.setLength(nlen);
											table.addField(field);
											break;
								 		case java.sql.Types.DOUBLE:
								 		case java.sql.Types.NUMERIC:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.FLOAT);
												 field.setDecimalDigits(6);
												 field.setLength(nlen);		
												table.addField(field);
											break;
										 case java.sql.Types.DATE:
										  case java.sql.Types.TIMESTAMP:
										  case java.sql.Types.TIME :	
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.DATE);
												table.addField(field);
											break;
										 case java.sql.Types.CLOB:
										  case java.sql.Types.LONGVARCHAR:
										  case java.sql.Types.BLOB:
										  case java.sql.Types.LONGVARBINARY:
												 field=new Field(frowset.getString("paramename"));
												 field.setDatatype(DataType.CLOB);
												table.addField(field);
											break;
										  default:
											  field=new Field(frowset.getString("paramename"));
											 field.setDatatype(DataType.STRING);
											 field.setLength(nlen);
											table.addField(field);
												break;	
									}
		 							   dbWizard.alterColumns(table);
		 						   }
		 					}
		 					}
		 			
		 			}
					
					LazyDynaBean dbean = new LazyDynaBean();
					dbean.set("paramscope", frowset.getString("paramscope"));
					dbean.set("paramname", frowset.getString("paramname"));
					dbean.set("paramtype", frowset.getString("paramtype"));
					dbean.set("paramename", frowset.getString("paramename"));
					dbean.set("paramcode", frowset.getString("paramcode"));
					dbean.set("paramlen", frowset.getString("paramlen"));
					dbean.set("paramfmt", frowset.getString("paramfmt"));
					dbean.set("paramNull", frowset.getString("paramNull"));
					dbean.set("paramcon", frowset.getString("paramcon"));

					String select_table = "";
					if ("0".equals(frowset.getString("paramscope"))) {
						select_table = global_parameter;
					} else {
						select_table = type_parameter + tsortid;
					}
					rs = dao.search("select " + frowset.getString("paramename")
							+ " from " + select_table + " where unitcode = '"
							+ myValue + "'");
					if (rs.next()) {
						String value = "";
						if (frowset.getString("paramtype").equals(ResourceFactory.getProperty("kq.card.work_date"))) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd");
							
							if (!(rs.getDate(1) == null|| "".equals(rs.getDate(1))|| "null".equals(rs.getDate(1))))
								value = dateFormat.format(rs.getDate(1));
						}
						if ("代码".equals(frowset.getString("paramtype"))) {
								value = rs.getString(1);
						}
						if(value==null)
							value="";
						dbean.set("valuehidden", value);
						if (frowset.getString("paramtype").equals(ResourceFactory.getProperty("codemaintence.codeitem.id"))) {
							value= rs.getString(1);
							temp = dao.search("select codeitemdesc from codeitem where codesetid='"+ frowset.getString("paramcode")
											+ "' and codeitemid = '"+ rs.getString(1) + "'");
							if (temp.next()) {
								value = temp.getString(1);
							}
							if(value==null)
								value ="";
							dbean.set("value", value);
						} else {
							if (frowset.getString("paramtype").equals(ResourceFactory.getProperty("kq.formula.character"))&& "".equals(value)&&dbean.get("paramcon")!=null&&dbean.get("paramcon").toString().trim().startsWith("$"))
							{
								value= rs.getString(1);
								if("1".equals(operateObject.trim()))//报表汇总关联单位tt_s ，不关联操作人
								value = tbo.getReportConstantParam(dbean.get("paramcon").toString().trim());
								if(value==null)
									value="";
								dbean.set("value", value);
							}
							else{
								if (frowset.getString("paramtype").equals(ResourceFactory.getProperty("kq.card.work_date"))) {
									if(value==null)
										value="";
									dbean.set("value", value);
								}else{
									value= rs.getString(1);
									if(value==null)
										value ="";
									dbean.set("value", value);
								}
								
							}
						}
					} else {
						if (frowset.getString("paramtype").equals(ResourceFactory.getProperty("kq.formula.character"))&&dbean.get("paramcon")!=null&&dbean.get("paramcon").toString().trim().startsWith("$"))
						{
							String value =tbo.getReportConstantParam(dbean.get("paramcon").toString().trim());
							if(value==null)
								value ="";
							dbean.set("value", value);
						}
						else{
							dbean.set("value", "");
						}
						
					}
					myList.add(dbean);
				}
			}
			
			if("2".equals(operateObject.trim())){//dml
				if(this.appealflag(tabid, dao,unitCode))
					this.getFormHM().put("flag", "true");
				else{
					this.getFormHM().put("flag", "false");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.getFormHM().put("mylist", myList);
		this.getFormHM().put("num", new Integer(num));
	}
	public boolean appealflag(String tabid,ContentDAO dao,String unitcode){
		boolean flag=false;
		boolean flag1=false;
		boolean flag2=false;
		String username=this.userView.getUserName();
		try {
			this.frowset=dao.search("select * from operuser where UserName='"+username+"'");
			if(this.frowset.next()){
				String unitcode1=this.frowset.getString("unitcode");
				if(unitcode!=null&&unitcode.length()!=0){
					if(!unitcode1.equals(unitcode)){
						this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode1+"' and tabid='"+tabid+"'");
						if(this.frowset.next()){
							String status=this.frowset.getString("status");
							if("1".equals(status)|| "3".equals(status)){
								
								flag1=true;
							}
						}
						this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode+"' and tabid='"+tabid+"'");
						if(this.frowset.next()){
							String status1=this.frowset.getString("status");
							if("1".equals(status1)|| "3".equals(status1)){
								flag2=true;
							}
						}
						
						String editupdisk="";
						 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
						 editupdisk=sysbo.getValue(Sys_Oth_Parameter.EDITUPDISK);
						 if(editupdisk==null||editupdisk.length()==0)
							 editupdisk="true";
						 if((flag1&&flag2)|| "false".equalsIgnoreCase(editupdisk)){
							flag=true;	
						 }
						 }
					}else{
						this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode1+"' and tabid='"+tabid+"'");
						if(this.frowset.next()){
							String status=this.frowset.getString("status");
							if("1".equals(status)|| "3".equals(status)){
								flag=true;
							}
						}
					}
				}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	public int getType(String Type){
		int type=java.sql.Types.VARCHAR;
		if("数值".equals(Type)){
			type = java.sql.Types.INTEGER;
		}else if("字符".equals(Type)|| "代码".equals(Type)){
			type = java.sql.Types.VARCHAR;
		}else if("日期".equals(Type)){
			type = java.sql.Types.TIME;
		}else if("备注".equals(Type)){
			type = java.sql.Types.LONGVARBINARY;
		}
		return  type;
	}
}
