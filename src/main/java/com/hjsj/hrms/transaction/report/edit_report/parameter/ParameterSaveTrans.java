package com.hjsj.hrms.transaction.report.edit_report.parameter;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ParameterSaveTrans extends IBusiness {
	public void execute() throws GeneralException {
		String unitcode = (String) getFormHM().get("unitcode");
		String operateObject = (String) getFormHM().get("operateObject");
		HashMap h = this.getFormHM();
		StringBuffer sb = new StringBuffer();
		Iterator iter = h.keySet().iterator();
		ContentDAO dao = new ContentDAO(getFrameconn());
		RecordVo vo = null;
		String tablename = "";
		String myValue = "";
		DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
		StringBuffer stb=new StringBuffer();
		RecordVo vo2 = null;
		ArrayList username=new ArrayList();
		try {
			if ("2".equals(operateObject)) {
				this.frowset=dao.search("select * from operuser where unitcode='"+unitcode +"'");
				while(this.frowset.next()){
					username.add(this.frowset.getString("UserName"));
				}
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if ("0".equals(h.get("paramscope"))) {
			if ("1".equals(operateObject)) {
				dbmodel.reloadTableModel("tp_p");	
				vo = new RecordVo("tp_p");
				tablename = "tp_p";
				myValue = getUserView().getUserId();
			}
			if ("2".equals(operateObject)) {
				dbmodel.reloadTableModel("tt_p");
				vo = new RecordVo("tt_p");
				tablename = "tt_p";
				myValue = unitcode;
			}
		}
		if ("1".equals(h.get("paramscope"))) {
			if ("1".equals(operateObject)) {
				dbmodel.reloadTableModel("tp_s" + getFormHM().get("tsortid"));
				vo = new RecordVo("tp_s" + getFormHM().get("tsortid"));
				tablename = "tp_s" + getFormHM().get("tsortid");
				myValue = getUserView().getUserId();
				
			}
			if ("2".equals(operateObject)) {
				dbmodel.reloadTableModel("tt_s" + getFormHM().get("tsortid"));
				vo = new RecordVo("tt_s" + getFormHM().get("tsortid"));
				tablename = "tt_s" + getFormHM().get("tsortid");
				myValue = unitcode;
			}
		}
	
		
		
		
		sb.append("update " + tablename + " set ");
		vo.setString("unitcode", userView.getUserName());
		if("2".equals(operateObject)){
			if("1".equals(h.get("paramscope"))){
				stb.append("update tp_s"+this.getFormHM().get("tsortid")+" set ");
				vo2= new RecordVo("tp_s"+this.getFormHM().get("tsortid"));
			}else{
				stb.append("update tp_p set ");
				vo2= new RecordVo("tp_p");
			}
		}
		boolean flg = false;
		boolean flag1=true;
		ResultSet rs1 = null;
		ResultSetMetaData rsmd = null;
		try {
			rs1 = dao.search("select * from " + tablename
					+ " where 1 > 2");
			rsmd = rs1.getMetaData();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				if (!"__type".equals(key) && !"sessionHM".equals(key)
						&& !"operateObject".equals(key)
						&& !"paramscope".equals(key) && !"tsortid".equals(key)) {
					if (key.indexOf(".hzvalue") == -1) {
						String convertkey = key.replaceAll(".value", "");
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							if(!rsmd.getColumnName(i).equalsIgnoreCase(convertkey)) {
								continue;
							}
							switch (rsmd.getColumnType(i)) {
							case Types.TINYINT:
							case Types.SMALLINT:
							case Types.INTEGER:
							case Types.BIGINT:
							case Types.FLOAT:
							case Types.DOUBLE:
							case Types.DECIMAL:
							case Types.NUMERIC:
							case Types.REAL: {
								String value =SafeCode.decode((String) h.get(key));
								if (flg) {
										sb.append(",");
										
								} else {
										flg = true;
								}
								if((value!=null&&value.trim().length()>0)|| "unitcode".equalsIgnoreCase(convertkey))
								{
										vo.setString(convertkey.toLowerCase(), value);
										if ("unitcode".equalsIgnoreCase(convertkey)) {
											value = myValue;
											vo.setString(convertkey.toLowerCase(),
													myValue);
											
										}
										sb.append(convertkey + "=" + value + "");
										if(!"unitcode".equalsIgnoreCase(convertkey)){
											if("2".equals(operateObject)){
												vo2.setString(convertkey.toLowerCase(),value);
												stb.append(convertkey + "='" + value + "'");
												if("2".equals(operateObject)){
													  if (flag1) {
														  stb.append(",");
													
														} else {
																flag1 = true;
														}
												}
											}
										}
										else{
											continue;
										}
								}else {
										sb.append(convertkey + "=null");
										if("2".equals(operateObject)){
											stb.append(convertkey + "=null");
											 if (flag1) {
												  stb.append(",");
											
												} else {
														flag1 = true;
												}
										}
								}
								break;
							
							}
							case Types.DATE:
							case Types.TIME:
							case Types.TIMESTAMP: {
								String value = SafeCode.decode((String) h.get(key));
							    if (flg) {
										sb.append(",");
										
								} else {
										flg = true;
								}
								if ((value != null && !"".equals(value))|| "unitcode".equalsIgnoreCase(convertkey)) {
										//vo.setString(convertkey.toLowerCase(),value);
										vo.setDate(convertkey.toLowerCase(),value);
										if ("unitcode".equalsIgnoreCase(convertkey)) {
											value = myValue;
											vo.setString(convertkey.toLowerCase(),myValue);
											
										}
										Sql_switcher sqlswitcher = new Sql_switcher();
										if(sqlswitcher.searchDbServer()==2){
											sb.append(convertkey + "=" + sqlswitcher.dateValue(value) + "");
										}else{
											sb.append(convertkey + "='" + value + "'");
										}
										if(!"unitcode".equalsIgnoreCase(convertkey)){
											if("2".equals(operateObject)){
												vo2.setString(convertkey.toLowerCase(),value);
												//liuy 2015-1-21 6846：报表汇总/编辑报表/文件/编辑报表参数：保存后后台报错 start
												if(sqlswitcher.searchDbServer()==2)
													stb.append(convertkey + "=" + sqlswitcher.dateValue(value) + "");
												else
													stb.append(convertkey + "='" + value + "'");
												//liuy 2015-1-21 end
												if("2".equals(operateObject)){
													  if (flag1) {
														  stb.append(",");
													
														} else {
																flag1 = true;
														}
												}
											}
										}
										else{
											continue;
										}
								} else {
										sb.append(convertkey + "=null");
										if("2".equals(operateObject)){
											stb.append(convertkey + "=null");
											 if (flag1) {
												  stb.append(",");
											
												} else {
														flag1 = true;
												}
										}
								}
							
								break;
							}
							case Types.CHAR:
							case Types.VARCHAR:
							case Types.CLOB:
							case Types.LONGVARCHAR: {
								String value = SafeCode.decode((String) h.get(key));
								value=value.replaceAll("'","’");
								if (flg) {
											sb.append(",");
									} else {
											flg = true;
								}
								if((value!=null&&value.trim().length()>0)|| "unitcode".equalsIgnoreCase(convertkey))
								{
											vo.setString(convertkey.toLowerCase(), value);
											if ("unitcode".equalsIgnoreCase(convertkey)) {
												value = myValue;
												vo.setString(convertkey.toLowerCase(),
														myValue);	
												
											}
											sb.append(convertkey + "='" + value + "'");
											if(!"unitcode".equalsIgnoreCase(convertkey)){
												if("2".equals(operateObject)){
													vo2.setString(convertkey.toLowerCase(),value);
													stb.append(convertkey + "='" + value + "'");
													if("2".equals(operateObject)){
														  if (flag1) {
															  stb.append(",");
															} else {
																	flag1 = true;
															}
													}
												}
											}
											else{
												continue;
											}
								}
								else {
												sb.append(convertkey + "=null");
												if("2".equals(operateObject)){
													stb.append(convertkey + "=null");
													 if (flag1) {
														  stb.append(",");
													
														} else {
																flag1 = true;
														}
												}
									
								}
								break;
							}
							default:
								break;
							}
						}
					}
				}
			}
			sb.append(" where unitcode = '" + myValue + "'");
			
			try {
//				System.out.println(sb.toString());
				RowSet rs = dao.search("select unitcode from " + tablename
						+ " where unitcode = '" + myValue + "'");
				if (rs.next()) {
					dao.update(sb.toString());
				} else {
					dao.addValueObject(vo);
				}
				if("2".equals(operateObject)){
					for(int i=0;i<username.size();i++){	
						StringBuffer stb2=new StringBuffer("");
						stb2.append(stb.toString());
						if(stb2.lastIndexOf(",")==(stb2.length()-1))
							stb2.setLength(stb.length()-1);
						stb2.append(" where unitcode = '" + (String)username.get(i) + "'");
						if("1".equals(h.get("paramscope")))
							rs=dao.search("select unitcode from tp_s"+this.getFormHM().get("tsortid")+" where unitcode='"+(String)username.get(i)+"'");
						else
							rs=dao.search("select unitcode from tp_p where unitcode='"+(String)username.get(i)+"'");
						if(rs.next()){
							dao.update(stb2.toString());
						}else{
							vo2.setString("unitcode", (String)username.get(i));
							dao.addValueObject(vo2);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs1!=null)
					rs1.close();
				
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}

	}

	public ParameterSaveTrans() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
