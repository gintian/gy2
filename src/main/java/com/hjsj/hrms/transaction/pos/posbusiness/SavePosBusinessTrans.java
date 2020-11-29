/*
 * Created on 2005-12-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.businessobject.duty.MoveSdutyBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SavePosBusinessTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList sqlvalue = new ArrayList();
		String param=(String)this.getFormHM().get("param");
		String first = (String) this.getFormHM().get("first");
		String a_code = (String) this.getFormHM().get("a_code");
		String codesetid = a_code.substring(0, 2);
		String parentid = "";
		if (a_code == null)
			a_code = "";
		if (a_code != null && a_code.length() >= 2) {
			// a_code=a_code.substring(2);
			parentid = a_code.substring(2);
		}
		String codeitemid = (String) this.getFormHM().get("codeitemid");
		String codeitemdesc = (String) this.getFormHM().get("codeitemdesc");
		String corcode = (String)this.getFormHM().get("corcode");
		String validateflag = (String) this.getFormHM().get("validateflag");
		String start_date1 = null;
		String end_date1 = null;
		java.sql.Date start_date=null;
		java.sql.Date end_date=null;
		//if (validateflag != null && validateflag.equals("1")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start_date1 = (String) this.getFormHM().get("start_date");
			end_date1 = (String) this.getFormHM().get("end_date");
			if(start_date1==null||start_date1.length()==0){
				start_date1=sdf.format(new java.util.Date());
			}
			if(end_date1==null||end_date1.length()==0){
				end_date1="9999-12-31";
			}
			try {
				start_date = new java.sql.Date(sdf.parse(start_date1).getTime());
				end_date = new java.sql.Date(sdf.parse(end_date1).getTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//}
		// codesetid=(String)this.getFormHM().get("codesetid");

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select * from codeitem where codesetid=");
		sqlstr.append("?");
		sqlstr.append(" and codeitemid=");
		// sqlstr.append(a_code!=null&&a_code.length()>2?a_code.substring(2):""
		// + codeitemid);
		// System.out.println(codesetid + a_code + codeitemid);
		sqlstr.append("?");
		sqlstr.append("");
		sqlvalue.add(codesetid);
		sqlvalue.add(parentid + codeitemid);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sqlstr.toString(),sqlvalue);
			if (this.frowset.next()) {
				this.getFormHM().put(
						"labelmessage",
						ResourceFactory
								.getProperty("label.posbusiness.adderrors"));
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory
								.getProperty("label.posbusiness.adderrors"),
						"", ""));
			} else {
				/*sqlstr.delete(0, sqlstr.length());
				sqlvalue.clear();
				sqlstr.append("select * from codeitem where codesetid=");
				sqlstr.append("?");
				sqlstr.append(" and codeitemdesc=");
				sqlstr.append("?");
				sqlstr.append("");
				sqlvalue.add(codesetid);
				sqlvalue.add(codeitemdesc);
				this.frowset = dao.search(sqlstr.toString(),sqlvalue);
				if (this.frowset.next()) {
					throw GeneralExceptionHandler
							.Handle(new GeneralException(
									"",
									ResourceFactory
											.getProperty("label.posbusiness.adderrorsname"),
									"", ""));
				} else */{
					sqlstr.delete(0, sqlstr.length());
					String tmpa0000=(String)this.getFormHM().get("a0000");
					int a0000=-1;
					if(tmpa0000==null||tmpa0000.length()==0){
						a0000=this.getMaxa0000(codesetid, parentid);
					}else if(!"PS_C_CODE".equals(param)){
						a0000=Integer.parseInt(tmpa0000);
						this.updateA0000(codesetid, parentid, a0000-1);
					}
					
					if (true) {//时间标示代码类 xuj2009-10-12
						if("68".equals(param)){
							sqlstr
								.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,start_date,end_date,invalid,corcode,b0110,a0000,layer)values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
						}else if("PS_C_CODE".equals(param)){
							sqlstr
							.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,start_date,end_date,invalid,corcode,a0000,layer)values(?,?,?,?,?,?,?,?,?,?,?,?)");
						}else{
							if(-1==a0000){
								sqlstr
								.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,start_date,end_date,invalid,corcode,layer)values(?,?,?,?,?,?,?,?,?,?,?)");
							}else{
								sqlstr
									.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,start_date,end_date,invalid,corcode,a0000,layer)values(?,?,?,?,?,?,?,?,?,?,?,?)");
							}
						}
					} 
//					else {
//						if("68".equals(param)){
//							sqlstr
//							.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,invalid,corcode,b0110,a0000)values(?,?,?,?,?,?,?,?,?,?)");
//						}else{
//							if(-1==a0000){
//								sqlstr
//								.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,invalid,corcode)values(?,?,?,?,?,?,?,?)");
//							}else{
//								sqlstr
//								.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag,invalid,corcode,a0000)values(?,?,?,?,?,?,?,?,?)");
//							}
//						}
//						
//					}
					sqlvalue.clear();
					sqlvalue.add(codesetid);
					sqlvalue.add(parentid + codeitemid);
					sqlvalue.add(codeitemdesc);
					cat.debug("-------code------------>" + parentid);
					if (parentid != null && parentid.trim().length() > 0)
						sqlvalue.add(parentid);
					else
						sqlvalue.add(codeitemid);
					sqlvalue.add((parentid != null ? parentid : "")
							+ codeitemid);
					String codeflag = SystemConfig.getPropertyValue("dev_flag");
					if (codeflag == null || "0".equals(codeflag)
							|| "".equals(codeflag)) {
						sqlvalue.add("0");
					} else {
						sqlvalue.add("1");
					}
					if (true) {
						sqlvalue.add(start_date);
						sqlvalue.add(end_date);
					//}else{
						sqlvalue.add(new Integer(1));
					}
					sqlvalue.add(corcode);
					if("68".equals(param)){
						TrainCourseBo tbo = new TrainCourseBo(this.userView);
						sqlvalue.add(tbo.getUnitIdByBusi());
						sqlvalue.add(new Integer(a0000));
					}else if("PS_C_CODE".equals(param)){
						MoveSdutyBo msb = new MoveSdutyBo(dao, frowset);
						if(tmpa0000==null||tmpa0000.length()==0){
							sqlvalue.add(msb.getaddA0000(parentid, codesetid));
						}else{
							msb.moveA0000(codesetid, Integer.parseInt(tmpa0000)-1);
							sqlvalue.add(new Integer(tmpa0000));
						}
					}else{
						if(-1==a0000){
						}else{
							sqlvalue.add(new Integer(a0000));
						}
					}
					sqlvalue.add(new Integer(this.getLayer(codesetid, parentid)));
					dao.insert(sqlstr.toString(), sqlvalue);
					CodeItem item=new CodeItem();
					item.setCodeid(codesetid);
					item.setCodeitem((parentid + codeitemid).toUpperCase());
					item.setCodename(codeitemdesc);
					//如果parentid为空，认为是最上级节点
				    item.setPcodeitem(StringUtils.isEmpty(parentid)?codeitemid.toUpperCase():parentid.toUpperCase());
					item.setCcodeitem((parentid +codeitemid).toUpperCase());
					AdminCode.addCodeItem(item);
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
						sqlvalue.add((parentid != null ? parentid : "")
								+ codeitemid);
						sqlvalue.add(parentid != null ? parentid : "");
						sqlvalue.add(codesetid);
						dao.update(sqlstr.toString(),sqlvalue);
					
						int codeitemlength = ((parentid != null ? parentid : "")
								+ codeitemid).length();
						//String sql = "update codeset set maxlength="+codeitemlength+" where codesetid='"+codesetid+"' and maxlength<"+codeitemlength;
						//String sql = "select MAX("+Sql_switcher.length("codeitemid")+") maxlength from codeitem where codesetid='"+codesetid+"'";
						String sql = "select itemlength from fielditem where codesetid='"+codesetid+"' union all select itemlength from t_hr_busifield where codesetid='"+codesetid+"'";
						this.frecset = dao.search(sql);
						//int f = dao.update(sql);
						if(this.frecset.next()){
							int maxlength = this.frecset.getInt("itemlength");
							if(maxlength<codeitemlength){
								sql = "update fielditem set itemlength="+codeitemlength+" where codesetid='"+codesetid+"'";
								dao.update(sql);
								sql = "update t_hr_busifield set itemlength="+codeitemlength+" where codesetid='"+codesetid+"'";
								dao.update(sql);
								sql="select fieldsetid,itemid from fielditem where codesetid='"+codesetid+"' and useflag='1' union select fieldsetid,itemid from t_hr_busifield where codesetid='"+codesetid+"' and useflag='1'";
								this.frecset = dao.search(sql);
								DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
								DbWizard dbw=new DbWizard(this.frameconn);
								while(this.frecset.next()){
									String fieldsetid = this.frecset.getString("fieldsetid");
									String itemid = this.frecset.getString("itemid").toLowerCase();
									
									if(fieldsetid.startsWith("A")){
										ArrayList dbprelist = DataDictionary.getDbpreList();
										for(int i=0;i<dbprelist.size();i++){
											String pre = (String)dbprelist.get(i);
											Table table=new Table((pre+fieldsetid).toLowerCase());
											Field field=new Field(itemid, itemid);
											field.setDatatype(DataType.STRING);
											field.setLength(codeitemlength);
											table.addField(field);
											dbw.alterColumns(table);
											dbmodel.reloadTableModel((pre+fieldsetid).toLowerCase());
											
											//zxj 20150923 A01Log表也需要处理
											if (!dbw.isExistField("A01Log", itemid, false))
											    continue;
											
											table = new Table("A01Log");
											field = new Field(itemid, itemid);
                                            field.setDatatype(DataType.STRING);
                                            field.setLength(codeitemlength);
                                            table.addField(field);
                                            dbw.alterColumns(table);
                                            dbmodel.reloadTableModel("A01Log");
										}
									}else{
										Table table=new Table(fieldsetid.toLowerCase());
										
										//没有好的办法获取一个表中的主键列，故暂时只支持Q17中指标Q1709
										//假期管理表(Q17)，假期类型是主键的一部分，需先去掉主键，修改完长度后，再加上主键
							            if ("Q1709".equalsIgnoreCase(itemid)){
							                dbw.dropPrimaryKey(fieldsetid.toLowerCase());
							            }
										Field field=new Field(itemid.toLowerCase(),this.frecset.getString("itemid").toLowerCase());
										field.setDatatype(DataType.STRING);
										field.setLength(codeitemlength);
										if ("Q1709".equalsIgnoreCase(itemid)){
											field.setNullable(false);									
										}
										table.addField(field);
										dbw.alterColumns(table);
										dbmodel.reloadTableModel(fieldsetid.toLowerCase());
										
										 //加回主键
							            if ("Q1709".equalsIgnoreCase(itemid)) {
							               table.clear();
							               
							               field = new Field("nbase");
							               field.setKeyable(true);
							               table.addField(field);
							               
							               field = new Field("A0100");
							               field.setKeyable(true);
							               table.addField(field);
							               
							               //年度
							               field = new Field("Q1701");
							               field.setKeyable(true);
							               table.addField(field);
							               
							               //假期类型
							               field = new Field("Q1709");
							               field.setKeyable(true);
							               field.setNullable(false);
							               table.addField(field);
							               
							               dbw.addPrimaryKey(table);
							            }
									}
								}
							}
						}
					}
					this.getFormHM().put(
							"labelmessage",
							ResourceFactory
									.getProperty("label.posbusiness.success"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("isrefresh", "save");
		this.getFormHM().put("codesetid", codesetid);
		this.getFormHM().put("codeitemid", codeitemid);

	}
	private int getLayer(String codesetid,String parentid){
		int layer=1;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="select layer from codeitem where codesetid='"+codesetid+"' and codeitemid='"+parentid+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				layer=this.frowset.getInt("layer")+1;
			}
		}catch(Exception e){
			layer=1;
		}
		return layer;
	}
	
	private void inita0000(String a_code,ContentDAO dao) throws SQLException{
		StringBuffer sql = new StringBuffer();
		String s= "update codeitem set a0000=? where codesetid='"+a_code.substring(0,2)+"' and codeitemid=?";
		if(a_code.length()==2){
			sql.append("select codeitemid from codeitem where codesetid='"+a_code+"' and parentid=codeitemid order by a0000,codeitemid");
			this.frowset = dao.search(sql.toString());
			ArrayList values = new ArrayList();
			int i=1;
			while(this.frowset.next()){
				ArrayList v= new ArrayList();
				v.add(new Integer(i));
				String t=this.frowset.getString("codeitemid");
				v.add(t);
				values.add(v);
				i++;
			}
			dao.batchUpdate(s, values);
		}else{
			sql.append("select codeitemid from codeitem where codesetid='"+a_code.substring(0,2)+"' and parentid='"+a_code.substring(2)+"' order by a0000,codeitemid");
			this.frowset = dao.search(sql.toString());
			ArrayList values = new ArrayList();
			int i=1;
			while(this.frowset.next()){
				ArrayList v= new ArrayList();
				v.add(new Integer(i));
				String t=this.frowset.getString("codeitemid");
				v.add(t);
				values.add(v);
				i++;
			}
			dao.batchUpdate(s, values);
		}
	}
	
	private int getMaxa0000(String codesetid,String parentid){
		int a0000=1;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="select a0000 from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid and a0000 is null";
			if(parentid.length()==0)
				sql = "select a0000 from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid and a0000 is null";
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				this.inita0000(codesetid+parentid, dao);
			}
			sql = "select max(a0000) a0000 from codeitem where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid";
			if(parentid.length()==0){
				sql = "select max(a0000) a0000 from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid";
			}
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				a0000=this.frowset.getInt("a0000")+1;
			}
		}catch(Exception e){
			a0000=-1;
		}
		return a0000;
	}
	
	private void updateA0000(String codesetid,String parentid,int a0000){
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "update codeitem set a0000=a0000+1 where codesetid='"+codesetid+"' and parentid='"+parentid+"' and codeitemid<>parentid and a0000>"+a0000;
			if(parentid.length()==0){
				sql="update codeitem set a0000=a0000+1 where codesetid='"+codesetid+"' and codeitemid=parentid and a0000>"+a0000;
			}
			dao.update(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
