package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.general.inform.CorField;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.Map;

public class SaveFiledValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid = (String) this.getFormHM().get("itemid");
		itemid = itemid != null ? itemid : "";
		String chkflag = "true";
		String onlynameflag = "true";  
		String chupdate = "true";  
		String itemcheck = "no";		
		try {

			String tablename = (String) this.getFormHM().get("tablename");
			tablename = tablename != null ? tablename : "";

			String a0100 = (String) this.getFormHM().get("a0100");
			a0100 = a0100 != null ? a0100 : "";

			String i9999 = (String) this.getFormHM().get("i9999");
			i9999 = i9999 != null ? i9999 : "";
			
			String inforflag = (String) this.getFormHM().get("inforflag");
			inforflag = inforflag != null&&inforflag.trim().length()>0 ? inforflag : "1";
			
			String fieldvalue = (String) this.getFormHM().get("fieldvalue");
			fieldvalue = fieldvalue != null ? SafeCode.decode(fieldvalue) : "";

			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if(fielditem==null|| "state".equalsIgnoreCase(fielditem.getItemid())){
				GzDataMaintBo gb = new GzDataMaintBo(this.frameconn);
				if("B00".equalsIgnoreCase(tablename)|| "K00".equalsIgnoreCase(tablename)
						||tablename.toUpperCase().indexOf("A00")!=-1){
					fielditem = gb.getFieldItem(tablename,itemid);
					if(fielditem!=null&&fieldvalue.length()>fielditem.getItemlength())
						fieldvalue = fieldvalue.substring(fieldvalue.length()-fielditem.getItemlength());
				}else{
					fielditem = gb.getFieldItem(itemid);
				}
			}
			if (fielditem != null
					&& (tablename.toUpperCase().indexOf(
							fielditem.getFieldsetid().toUpperCase()) != -1||
							"t_vorg_staff".equalsIgnoreCase(tablename))) {
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
				chk=chk!=null?chk:"";
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name"); //验证唯一性指标
				onlyname=onlyname!=null?onlyname:"";
				String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");//身份证验证是否启用
				chkvalid=chkvalid!=null?chkvalid:"";				
				String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
				uniquenessvalid=uniquenessvalid!=null?uniquenessvalid:"";
				String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","db");//验证身份证适用的人员库
				dbchk=dbchk!=null?dbchk:"";
				String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
				dbonly=dbonly!=null?dbonly:"";
				
				String blacklist_per = sysbo.getValue(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人员库
				String blacklist_field = sysbo.getValue(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单指标

				DbNameBo dbnamebo = new DbNameBo(this.getFrameconn());
				if(fielditem.isMainSet()&& "A01".equalsIgnoreCase(fielditem.getFieldsetid())){
					if("1".equals(uniquenessvalid)){
						if(dbonly.toUpperCase().indexOf(tablename.toUpperCase()
								.replaceAll(fielditem.getFieldsetid().toUpperCase(),""))!=-1){
							if(itemid.equalsIgnoreCase(onlyname)
									&&fieldvalue!=null&&fieldvalue.trim().length()>0){
								onlynameflag = dbnamebo.checkOnlyName(dbonly,fielditem.getFieldsetid(),itemid,fieldvalue,a0100);
							}
						}
					}
					if(blacklist_per!=null&&blacklist_per.length()>2){//添加判断黑名单
						if(itemid.equalsIgnoreCase(blacklist_field)
								&&fieldvalue!=null&&fieldvalue.trim().length()>0){
							onlynameflag = dbnamebo.checkOnlyName(blacklist_per,fielditem.getFieldsetid()
									,itemid,fieldvalue,a0100);
						}
					}
					if("1".equals(chkvalid)){
						if(dbchk.toUpperCase().indexOf(tablename.toUpperCase()
								.replaceAll(fielditem.getFieldsetid().toUpperCase(),""))!=-1){
							if(itemid.equalsIgnoreCase(chk)){
								RecordVo vo_old = dbnamebo.getRecordVoA01(tablename,a0100);
								CorField cof = new CorField();
								String sex = vo_old.getString(cof.getItemid(CorField.SEX_ITEMID, this.frameconn));
								sex=sex!=null&&sex.trim().length()>0?sex:"";
								
								String birthday = vo_old.getString(cof.getItemid(CorField.BIRTHDAY_ITEMID, this.frameconn));
								birthday=birthday!=null&&birthday.trim().length()>0?birthday:"";
								if(birthday.trim().length()>0){
									WeekUtils weekUtils = new WeekUtils();
									birthday=weekUtils.dateTostr(weekUtils.strTodate(birthday));
									birthday=birthday.replaceAll("-", "").replaceAll("\\.","");
								}
								
								if(fieldvalue!=null&&fieldvalue.trim().length()>0){
									String check = dbnamebo.checkIdNumber(fieldvalue,birthday,sex);
									String arr[]= check.split(":");
									if(arr.length==2){
										if("false".equalsIgnoreCase(arr[0]))
											chkflag = arr[1];
									}
									if("true".equalsIgnoreCase(chkflag)){
										onlynameflag = dbnamebo.checkOnlyName(dbchk,fielditem.getFieldsetid(),itemid,fieldvalue,a0100);
										if("true".equalsIgnoreCase(onlynameflag)){
											onlynameflag = dbnamebo.checkOnlyName(dbchk,fielditem.getFieldsetid(),
													itemid,dbnamebo.changeCardID(fieldvalue,birthday),a0100);
										}
									}
								}
							}
						}
					}
				}
				 OtherParam param=new OtherParam(this.getFrameconn());
				 Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
				 if(setmap!=null){
					 if("true".equalsIgnoreCase(setmap.get("valid").toString())){
						 String idcardfield = setmap.get("src").toString();
						 if(fieldvalue.trim().length()>0&&itemid.equalsIgnoreCase(idcardfield)){
							 char chkcard =  dbnamebo.doVerify(fieldvalue);
							 if(chkcard!='n'){
								 String birthdayfield = setmap.get("birthday").toString().trim();
								 String agefield = setmap.get("age").toString().trim();
								 String axfield = setmap.get("ax").toString().trim();
								 String itemarr = birthdayfield.toLowerCase()+"::"+agefield.toLowerCase()+"::"+axfield.toLowerCase();
								 String datestr = new SortFilter().getBirthDay(fieldvalue);
								 datestr=datestr.replace(".", "-");
								 String itemvaluearr = datestr
								 +"::"+new SortFilter().getAge(fieldvalue)+"::"+new SortFilter().getSex(fieldvalue);
								 this.getFormHM().put("itemarr", itemarr);
								 this.getFormHM().put("itemvaluearr", itemvaluearr);
								 itemcheck = "ok";
							 }
//							 else{
//								 chkflag = "身份证号码不正确!";
//							 }
						 }
					 }
				 }
				 setmap=param.serachAtrr("/param/formual[@name=\"bywork\"]");
				 if(setmap!=null&&"true".equalsIgnoreCase(setmap.get("valid").toString())){
					 String workdatefield = setmap.get("src").toString();
					 if(fieldvalue.trim().length()>0&&itemid.equalsIgnoreCase(workdatefield)){
						 WeekUtils weekUtils = new WeekUtils();
						 long h = WeekUtils.getYearSpan(weekUtils.strTodate(fieldvalue),new Date());
						 String workagefield = setmap.get("dest").toString().toLowerCase();
						 this.getFormHM().put("itemarr", workagefield);
						 this.getFormHM().put("itemvaluearr",h+"");
						 itemcheck = "ok";
					 }
				 }
				 setmap=param.serachAtrr("/param/formual[@name=\"byorg\"]");
				 if(setmap!=null&&"true".equalsIgnoreCase(setmap.get("valid").toString())){
					 String workdatefield = setmap.get("src").toString();
					 if(fieldvalue.trim().length()>0&&itemid.equalsIgnoreCase(workdatefield)){
						 WeekUtils weekUtils = new WeekUtils();
						 long h = WeekUtils.getYearSpan(weekUtils.strTodate(fieldvalue),new Date());
						 String workagefield = setmap.get("dest").toString().toLowerCase();
						 this.getFormHM().put("itemarr", workagefield);
						 this.getFormHM().put("itemvaluearr",h+"");
						 itemcheck = "ok";
					 }
				 }
				 if("flag".equalsIgnoreCase(fielditem.getItemid())
						 || "downole".equalsIgnoreCase(fielditem.getItemid())
						 || "upole".equalsIgnoreCase(fielditem.getItemid())){
					 if("B00".equalsIgnoreCase(tablename)|| "K00".equalsIgnoreCase(tablename)
							 || "A00".equalsIgnoreCase(tablename)){
						 chkflag = "false";
					 }
				}
				if("true".equalsIgnoreCase(chkflag)&& "true".equalsIgnoreCase(onlynameflag)){
					
					StringBuffer sqlstr = new StringBuffer();

					sqlstr.append("update ");
					sqlstr.append(tablename);
					sqlstr.append(" set ");
					sqlstr.append(itemid);
					sqlstr.append("=?");
					if("1".equals(inforflag))
						sqlstr.append(" where A0100=?");
					if("2".equals(inforflag))
						sqlstr.append(" where B0110=?");
					if("3".equals(inforflag))
						sqlstr.append(" where E01A1=?");

					if (!fielditem.isMainSet()) {
						sqlstr.append(" and I9999=?");
						if(i9999==null||i9999.trim().length()<1){
							i9999="1";
							if("2".equals(this.userView.analyseTablePriv(fielditem.getFieldsetid()))){
								ContentDAO dao=new ContentDAO(this.getFrameconn());
								StringBuffer buf = new StringBuffer();
								buf.append("insert into ");
								buf.append(tablename);

								if("1".equals(inforflag))
									buf.append("(A0100,I9999) values('");
								if("2".equals(inforflag))
									buf.append("(B0110,I9999) values('");
								if("3".equals(inforflag))
									buf.append("(E01A1,I9999) values('");
								buf.append(a0100);
								buf.append("',1)");
								dao.update(buf.toString());
							}else{
								chupdate = "您没有权限插入值!";
							}
						}
					}
					this.fstmt = this.frameconn.prepareStatement(sqlstr.toString());
					if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
						this.fstmt.setString(1, fieldvalue);
					} else if ("D".equalsIgnoreCase(fielditem.getItemtype())) {
						WeekUtils weekUtils = new WeekUtils();
						if (fieldvalue.trim().length() > 0)
							this.fstmt.setDate(1, new java.sql.Date(weekUtils
									.strTodate(fieldvalue).getTime()));
						else
							this.fstmt.setDate(1, null);
					} else if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
						if (fielditem.getDecimalwidth() > 0) {
							this.fstmt.setFloat(1, Float.parseFloat(fieldvalue));
						} else {
							this.fstmt.setInt(1, Integer.parseInt(fieldvalue));
						}
					} else if ("M".equalsIgnoreCase(fielditem.getItemtype())) {
						if (Sql_switcher.searchDbServer() == Constant.ORACEL
								|| Sql_switcher.searchDbServer() == Constant.DB2) {
							Reader clobReader = new StringReader(fieldvalue);
							this.fstmt.setCharacterStream(1, clobReader, fieldvalue
									.length());
						} else
							this.fstmt.setString(1, fieldvalue);
					}
					this.fstmt.setString(2, a0100);
					if (!fielditem.isMainSet()) {
						this.fstmt.setString(3, i9999);
					}
					DbSecurityImpl dbS = new DbSecurityImpl();
					try {						
						dbS.open(this.frameconn, sqlstr.toString()); 
						this.fstmt.executeUpdate();
					} finally {
						try {
							dbS.close(this.frameconn);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("chkflag",chkflag);
		this.getFormHM().put("onlynameflag",onlynameflag);
		this.getFormHM().put("itemcheck", itemcheck);
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("chupdate",chupdate);
	}
}
