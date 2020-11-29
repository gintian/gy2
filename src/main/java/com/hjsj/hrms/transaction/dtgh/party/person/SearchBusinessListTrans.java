/*
 * Created on 2010-1-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class SearchBusinessListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String a_code=(String)this.getFormHM().get("a_code");
		String partylike = (String)this.getFormHM().get("partylike");
		partylike = partylike!=null&&partylike.length()>0?partylike:"0";
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		String query = (String)this.getFormHM().get("query");
		query = query!=null&&query.length()>0?query:"0";
		String querylike = (String)this.getFormHM().get("querylike");
		querylike=querylike!=null&&querylike.length()>0?querylike:"0";
		ArrayList queryfieldlist = (ArrayList)this.getFormHM().get("queryfieldlist");
		String select_name=(String)this.getFormHM().get("select_name");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String param = (String) this.getFormHM().get("param");
		param = param != null && param.length() > 0 ? param : "Y";
		String userbase = (String)this.getFormHM().get("userbase");
		userbase = userbase!=null&&userbase.length()>0?userbase:"usr";
		String politics = (String)this.getFormHM().get("politics");
		politics = politics!=null&&politics.length()>0?politics:"";
		String belongparty = (String)this.getFormHM().get("belongparty");
		String belongmember = (String)this.getFormHM().get("belongmember");
		String belongmeet= (String)this.getFormHM().get("belongmeet");
		String polity= (String)this.getFormHM().get("polity");
		String party=(String)this.getFormHM().get("party");
		String preparty=(String)this.getFormHM().get("preparty");
		String important=(String)this.getFormHM().get("important");
		String active=(String)this.getFormHM().get("active");
		String application=(String)this.getFormHM().get("application");
		String member=(String)this.getFormHM().get("member");
		String person=(String)this.getFormHM().get("person");
		
		if("Y".equalsIgnoreCase(param)){//xuj 2010-4-10 改动默认一个设置的政治面貌
			if("".equals(politics)){
				if(!"".equals(party)){
					politics="party";
				}else if(!"".equals(preparty)){
					politics="preparty";
				}else if(!"".equals(important)){
					politics="important";
				}else if(!"".equals(active)){
					politics="active";
				}else if(!"".equals(application)){
					politics="application";
				}else if(!"".equals(person)){
					politics="person";
				}
			}
		}else if("V".equalsIgnoreCase(param)){
			if("".equals(politics)){
				if(!"".equals(member)){
					politics="member";
				}else if(!"".equals(person)){
					politics="person";
				}
			}
		}else if("W".equals(param)){
			if("".equals(politics)){
				if(!"".equals(person)){
					politics="person";
				}
			}
		}
		if(!"".equals(politics)){
			selectsetup(politics);
		}
		
		try
		{
			
			StringBuffer columns=new StringBuffer();
		    if(fieldstr!=null&&fieldstr.length()>0){			
		    	columns.append("a0100"+fieldstr.toLowerCase());
		    	if(columns.indexOf("b0110")==-1){
		    		columns.append(",b0110");
		    	}
		    	if(columns.indexOf("e01a1")==-1){
		    		columns.append(",e01a1");
		    	}
			}
			StringBuffer strsql=new StringBuffer();
			StringBuffer cond_str = new StringBuffer();
			StringBuffer tables = new StringBuffer();
			strsql.append("select "+userbase+"A01.a0100 as "+columns.toString());
			tables.append(" from "+userbase+"A01");
			cond_str.append(" where ");

			int res_type = IResourceConstant.PARTY;
			if("V".equals(param))
				 res_type = IResourceConstant.MEMBER;
			String managed_str = this.userView.getResourceString(res_type);
				if("Y".equals(param)){//党组织内人员
					if(belongparty!=null&&belongparty.length()>0){
						if(a_code!=null&&a_code.length()>2){
							if(politics!=null&&politics.length()>0){
								if(polity!=null&&polity.length()>0){
									String[] p = polity.split("\\.");
									if(p.length==2){
										if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(belongparty+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongparty)+") and "+p[1]);
											}else{
												cond_str.append(belongparty+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongparty)+") and "+p[1]);
											}
											if("party".equals(politics)){
												cond_str.append("='"+party+"'");
											}else if("preparty".equals(politics)){
												cond_str.append("='"+preparty+"'");
											}else if("important".equals(politics)){
												cond_str.append("='"+important+"'");
											}else if("active".equals(politics)){
												cond_str.append("='"+active+"'");
											}else if("application".equals(politics)){
												cond_str.append("='"+application+"'");
											}else if("person".equals(politics)){
												cond_str.append(" in ('"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#'");
											}
											
										}else{//人员子集指标
											tables.append(","+userbase+p[0]);
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(userbase+"A01."+belongparty+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongparty)+") and "+userbase+polity);
											}else{
												cond_str.append(userbase+"A01."+belongparty+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongparty)+") and "+userbase+polity);
											}
											if("party".equals(politics)){
												cond_str.append("='"+party+"'");
											}else if("preparty".equals(politics)){
												cond_str.append("='"+preparty+"'");
											}else if("important".equals(politics)){
												cond_str.append("='"+important+"'");
											}else if("active".equals(politics)){
												cond_str.append("='"+active+"'");
											}else if("application".equals(politics)){
												cond_str.append("='"+application+"'");
											}else if("person".equals(politics)){
												cond_str.append(" in ('"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#'");
											}
											cond_str.append("and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
										}
									}else{//设置政治面貌指标错误
										cond_str.append("1=2");
									}
								}else{//没有设置政治面貌指标
									cond_str.append("1=2");
								}
							}else{
								if(polity!=null&&polity.length()>0){
									String[] p = polity.split("\\.");
									if(p.length==2){
										if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(belongparty+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongparty)+") and "+p[1]+" in(");
											}else{
												cond_str.append(belongparty+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongparty)+") and "+p[1]+" in(");
											}
											if(party!=null&&party.length()>0){
												cond_str.append("'"+party+"',");
											}else{
												cond_str.append("'#',");
											}
											if(preparty!=null&&preparty.length()>0){
												cond_str.append("'"+preparty+"',");
											}else{
												cond_str.append("'#',");
											}
											if(important!=null&&important.length()>0){
												cond_str.append("'"+important+"',");
											}else{
												cond_str.append("'#',");
											}
											if(active!=null&&active.length()>0){
												cond_str.append("'"+active+"',");
											}else{
												cond_str.append("'#',");
											}
											if(application!=null&&application.length()>0){
												cond_str.append("'"+application+"',");
											}else{
												cond_str.append("'#',");
											}
											if(person!=null&&person.length()>0){
												cond_str.append("'"+person.replaceAll(",", "','")+"#') ");
											}else{
												cond_str.append("='#') ");
											}
											
										}else{//人员子集指标
											tables.append(","+userbase+p[0]);
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(userbase+"A01."+belongparty+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongparty)+") and "+userbase+polity+" in (");
											}else{
												cond_str.append(userbase+"A01."+belongparty+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongparty)+") and "+userbase+polity+" in (");
											}
											if(party!=null&&party.length()>0){
												cond_str.append("'"+party+"',");
											}else{
												cond_str.append("'#',");
											}
											if(preparty!=null&&preparty.length()>0){
												cond_str.append("'"+preparty+"',");
											}else{
												cond_str.append("'#',");
											}
											if(important!=null&&important.length()>0){
												cond_str.append("'"+important+"',");
											}else{
												cond_str.append("'#',");
											}
											if(active!=null&&active.length()>0){
												cond_str.append("'"+active+"',");
											}else{
												cond_str.append("'#',");
											}
											if(application!=null&&application.length()>0){
												cond_str.append("'"+application+"',");
											}else{
												cond_str.append("'#',");
											}
											if(person!=null&&person.length()>0){
												cond_str.append("'"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#')");
											}
											cond_str.append(" and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
										}
									}else{//设置政治面貌指标错误
										cond_str.append("1=2");
									}
								}else{//没有设置政治面貌指标
									cond_str.append("1=2");
								}
							}
							
						}else{
							if(polity!=null&&polity.length()>0){
								String[] p = polity.split("\\.");
								if(p.length==2){
									if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标  and "+belongparty+"<>''
										cond_str.append(belongparty+" is not null and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongparty)+") and "+p[1]+" in(");
										if(party!=null&&party.length()>0&&"party".equals(politics)){
											cond_str.append("'"+party+"',");
										}else{
											cond_str.append("'#',");
										}
										if(preparty!=null&&preparty.length()>0&&"preparty".equals(politics)){
											cond_str.append("'"+preparty+"',");
										}else{
											cond_str.append("'#',");
										}
										if(important!=null&&important.length()>0&&"important".equals(politics)){
											cond_str.append("'"+important+"',");
										}else{
											cond_str.append("'#',");
										}
										if(active!=null&&active.length()>0&&"active".equals(politics)){
											cond_str.append("'"+active+"',");
										}else{
											cond_str.append("'#',");
										}
										if(application!=null&&application.length()>0&&"application".equals(politics)){
											cond_str.append("'"+application+"',");
										}else{
											cond_str.append("'#',");
										}
										if(person!=null&&person.length()>0&&"person".equals(politics)){
											cond_str.append("'"+person.replaceAll(",", "','")+"#') ");
										}else{
											cond_str.append("='#') ");
										}
										
									}else{//人员子集指标
										tables.append(","+userbase+p[0]);// and "+userbase+"A01."+belongparty+"<>''
										cond_str.append(userbase+"A01."+belongparty+" is not null and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongparty)+") and "+userbase+polity+" in (");
										if(party!=null&&party.length()>0&&"party".equals(politics)){
											cond_str.append("'"+party+"',");
										}else{
											cond_str.append("'#',");
										}
										if(preparty!=null&&preparty.length()>0&&"preparty".equals(politics)){
											cond_str.append("'"+preparty+"',");
										}else{
											cond_str.append("'#',");
										}
										if(important!=null&&important.length()>0&&"important".equals(politics)){
											cond_str.append("'"+important+"',");
										}else{
											cond_str.append("'#',");
										}
										if(active!=null&&active.length()>0&&"active".equals(politics)){
											cond_str.append("'"+active+"',");
										}else{
											cond_str.append("'#',");
										}
										if(application!=null&&application.length()>0&&"application".equals(politics)){
											cond_str.append("'"+application+"',");
										}else{
											cond_str.append("'#',");
										}
										if(person!=null&&person.length()>0&&"person".equals(politics)){
											cond_str.append("'"+person.replaceAll(",", "','")+"#')");
										}else{
											cond_str.append("='#')");
										}
										cond_str.append(" and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
									}
								}else{//设置政治面貌指标错误
									cond_str.append("1=2");
								}
							}else{//没有设置政治面貌指标
								cond_str.append("1=2");
							}
						}
					}else{
						cond_str.append("1=2");
					}
				}else if("V".equals(param)){//团组织内人员
					if(belongmember!=null&&belongmember.length()>0){
						if(a_code!=null&&a_code.length()>2){
							if(politics!=null&&politics.length()>0){
								if(polity!=null&&polity.length()>0){
									String[] p = polity.split("\\.");
									if(p.length==2){
										if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(belongmember+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongmember)+") and "+p[1]);
											}else{
												cond_str.append(belongmember+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongmember)+") and "+p[1]);
											}
											if("member".equals(politics)){
												cond_str.append("='"+member+"'");
											}else if("person".equals(politics)){
												cond_str.append(" in ('"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#'");
											}
											
										}else{//人员子集指标
											tables.append(","+userbase+p[0]);
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(userbase+"A01."+belongmember+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongmember)+") and "+userbase+polity);
											}else{
												cond_str.append(userbase+"A01."+belongmember+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongmember)+") and "+userbase+polity);
											}
											if("member".equals(politics)){
												cond_str.append("='"+member+"'");
											}else if("person".equals(politics)){
												cond_str.append(" in ('"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#'");
											}
											cond_str.append("and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
										}
									}else{//设置政治面貌指标错误
										cond_str.append("1=2");
									}
								}else{//没有设置政治面貌指标
									cond_str.append("1=2");
								}
							}else{
								if(polity!=null&&polity.length()>0){
									String[] p = polity.split("\\.");
									if(p.length==2){
										if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(belongmember+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongmember)+") and "+p[1]+" in(");
											}else{
												cond_str.append(belongmember+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongmember)+") and "+p[1]+" in(");
											}
											if(member!=null&&member.length()>0){
												cond_str.append("'"+member+"',");
											}else{
												cond_str.append("'#',");
											}
											if(person!=null&&person.length()>0){
												cond_str.append("'"+person.replaceAll(",", "','")+"#') ");
											}else{
												cond_str.append("='#') ");
											}
											
										}else{//人员子集指标
											tables.append(","+userbase+p[0]);
											if("1".equalsIgnoreCase(partylike)){//显示当前组织单元下所有机构人员 
												cond_str.append(userbase+"A01."+belongmember+" like '"+a_code.substring(2, a_code.length())+"%' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongmember)+") and "+userbase+polity+" in (");
											}else{
												cond_str.append(userbase+"A01."+belongmember+"='"+a_code.substring(2, a_code.length())+"' and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongmember)+") and "+userbase+polity+" in (");
											}
											if(member!=null&&member.length()>0){
												cond_str.append("'"+member+"',");
											}else{
												cond_str.append("'#',");
											}
											if(person!=null&&person.length()>0){
												cond_str.append("'"+person.replaceAll(",", "','")+"#')");
											}else{
												cond_str.append("='#')");
											}
											cond_str.append(" and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
										}
									}else{//设置政治面貌指标错误
										cond_str.append("1=2");
									}
								}else{//没有设置政治面貌指标
									cond_str.append("1=2");
								}
							}
							
						}else{
							if(polity!=null&&polity.length()>0){
								String[] p = polity.split("\\.");
								if(p.length==2){
									if(polity.toUpperCase().indexOf("A01")!=-1){//人员主集指标    and "+belongmember+"<>''
										cond_str.append(belongmember+" is not null and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", belongmember)+") and "+p[1]+" in(");
										if(member!=null&&member.length()>0&&"member".equals(politics)){
											cond_str.append("'"+member+"',");
										}else{
											cond_str.append("'#',");
										}
										if(person!=null&&person.length()>0&&"person".equals(politics)){
											cond_str.append("'"+person.replaceAll(",", "','")+"#') ");
										}else{
											cond_str.append("='#') ");
										}
										
									}else{//人员子集指标
										tables.append(","+userbase+p[0]);// and "+userbase+"A01."+belongmember+"<>''
										cond_str.append(userbase+"A01."+belongmember+" is not null and ("+this.analyseManagePriv(managed_str).replaceAll("codeitemid", userbase+"A01."+belongmember)+") and "+userbase+polity+" in (");
										if(member!=null&&member.length()>0&&"member".equals(politics)){
											cond_str.append("'"+member+"',");
										}else{
											cond_str.append("'#',");
										}
										if(person!=null&&person.length()>0&&"person".equals(politics)){
											cond_str.append("'"+person.replaceAll(",", "','")+"#')");
										}else{
											cond_str.append("='#')");
										}
										cond_str.append(" and "+userbase+"A01.a0100="+userbase+p[0]+".a0100"+" and "+userbase+p[0]+".i9999=(select max(i9999) from "+userbase+p[0]+" where "+userbase+"A01.a0100="+userbase+p[0]+".a0100)");
									}
								}else{//设置政治面貌指标错误
									cond_str.append("1=2");
								}
							}else{//没有设置政治面貌指标
								cond_str.append("1=2");
							}
						}
					}else{
						cond_str.append("1=2");
					}
				}else if("W".equals(param)){//工会组织内人员
					cond_str.append("1=2");
				}else{
					cond_str.append("1=2");
				}
				if("1".equals(query)){//条件查询
					this.getFormHM().put("isShowCondition", "block");
					cond_str.append(getWhere(queryfieldlist,querylike,select_name,tables,userbase));
				}else if("2".equals(query)){//切换人员库
					this.getFormHM().put("isShowCondition", "block");
				}else{
					this.getFormHM().put("isShowCondition", "none");
					this.getFormHM().put("select_name", select_name);
					if(queryfieldlist!=null){
					    ArrayList list = new ArrayList();
						for(int i=0;i<queryfieldlist.size();i++){
							FieldItem field = (FieldItem)queryfieldlist.get(i);
							
							if(field!=null) {
				                 FieldItem item_0=(FieldItem)field.clone(); 
				                 if(item_0.getUseflag()==null||item_0.getUseflag().length()==0|| "0".equals(item_0.getUseflag()))
				                     continue;
				                 if(item_0.getCodesetid()!=null&&!"0".equals(item_0.getCodesetid()))
				                 {
				                     int count=getCodeSetidChildLen(item_0.getCodesetid());
				                     item_0.setItemlength(count);                   
				                 }
				                 item_0.setValue("");
				                 item_0.setViewvalue("");
				                 list.add(item_0);
				            }
						}
						
						queryfieldlist = list;
					}
				}
				//高级查询
				String sexpr = (String)this.getFormHM().get("expr");
				String sfactor = (String)this.getFormHM().get("factor");
				boolean likeflag = false;
				if("1".equals((String)this.getFormHM().get("likeflag"))){
					likeflag=true;
				}
				sfactor=SafeCode.decode(sfactor);
				sexpr=SafeCode.decode(sexpr);
				//当高级查询的条件为空时，不进行解析 2017-04-26 chenxg
                if(StringUtils.isNotEmpty(sfactor) && StringUtils.isNotEmpty(sexpr)){
                    FactorList factorslist=new FactorList(sexpr,PubFunc.getStr(sfactor),userbase,false,likeflag,true,1,userView.getUserId());
                    factorslist.setSuper_admin(userView.isSuper_admin());
                    String chwhere=factorslist.getSqlExpression();
                    int index = chwhere.toUpperCase().indexOf("WHERE");
                    if(index!=-1)
                        cond_str.append(" and "+chwhere.substring(index+5));    
                }
			cat.debug("-----strsql------>" + strsql.toString());
			String codemess=AdminCode.getCodeName(a_code.substring(0,2).toUpperCase(), a_code.substring(2).toUpperCase());
			if(codemess==null||codemess.length()<=0){
				String sql = "select codesetdesc from codeset where codesetid='"
					+ a_code.substring(0,2) + "'";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					codemess=this.frowset.getString("codesetdesc");
			}  
			this.getFormHM().put("codemess", codemess);
			this.getFormHM().put("strsql", strsql.toString());
			this.getFormHM().put("cond_str", tables.append(cond_str.toString()).toString());
			this.getFormHM().put("columns", columns.toString());
			this.getFormHM().put("order_by", "order by a0000");	
			this.getFormHM().put("query", "");
			this.getFormHM().put("queryfieldlist", queryfieldlist);
			this.getFormHM().put("politics", politics);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}		
	}
	
	private void selectsetup(String param) throws GeneralException{
		String add = "";
		String up = "";
		String leave = "";
		String iin = "";
		String out = "";
		String resumeparty = "";
		String resumemember = "";
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select tabid,name from template_table where tabid in (";
			if("party".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/party/add");
				leave = xml.getTextValue("/param/polity/party/leave");
				iin = xml.getTextValue("/param/polity/party/iin");
				out = xml.getTextValue("/param/polity/party/out");

			}else if("preparty".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/preparty/add");
				up = xml.getTextValue("/param/polity/preparty/up");
				leave = xml.getTextValue("/param/polity/preparty/leave");
				iin = xml.getTextValue("/param/polity/preparty/iin");
				out = xml.getTextValue("/param/polity/preparty/out");
				
			}else if("important".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/important/add");
				up = xml.getTextValue("/param/polity/important/up");
				leave = xml.getTextValue("/param/polity/important/leave");
				iin = xml.getTextValue("/param/polity/important/iin");
				out = xml.getTextValue("/param/polity/important/out");
				
			}else if("active".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/active/add");
				up = xml.getTextValue("/param/polity/active/up");
				leave = xml.getTextValue("/param/polity/active/leave");
				iin = xml.getTextValue("/param/polity/active/iin");
				out = xml.getTextValue("/param/polity/active/out");
				
			}else if("application".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/application/add");
				up = xml.getTextValue("/param/polity/application/up");
				leave = xml.getTextValue("/param/polity/application/leave");
				iin = xml.getTextValue("/param/polity/application/iin");
				out = xml.getTextValue("/param/polity/application/out");
				
			}else if("member".equals(param)){
				StringBuffer temp = new StringBuffer();
				add = xml.getTextValue("/param/polity/member/add");
				leave = xml.getTextValue("/param/polity/member/leave");
				iin = xml.getTextValue("/param/polity/member/iin");
				out = xml.getTextValue("/param/polity/member/out");
				
			}else if("person".equals(param)){
				StringBuffer temp = new StringBuffer();
				up = xml.getTextValue("/param/polity/person/up");
				leave = xml.getTextValue("/param/polity/person/leave");
				resumeparty = xml.getTextValue("/param/polity/person/resumeparty");
				iin = xml.getTextValue("/param/polity/person/iin");
				resumemember = xml.getTextValue("/param/polity/person/resumemember");
				
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("add", add);
			this.getFormHM().put("up", up);
			this.getFormHM().put("leave", leave);
			this.getFormHM().put("iin", iin);
			this.getFormHM().put("out", out);
			this.getFormHM().put("resumeparty", resumeparty);
			this.getFormHM().put("resumemember", resumemember);
		}
		
	}
	private String getWhere(ArrayList queryList,String querylike,String select_name,StringBuffer tables,String userbase)throws GeneralException{
		StringBuffer sb = new StringBuffer();
		HashMap map  = new HashMap();
		if(queryList==null||queryList.size()==0)
			return "";
		
		for(int i=0;i<queryList.size();i++){//已指标集分组指标
			FieldItem field = (FieldItem)queryList.get(i);
			if(map.containsKey(field.getFieldsetid())){
				((ArrayList)map.get(field.getFieldsetid())).add(field);
			}else{
				ArrayList list = new ArrayList();
				list.add(field);
				map.put(field.getFieldsetid(), list);
			}
		}
		for(Iterator i=map.keySet().iterator();i.hasNext();){
			String fieldsetid = (String)i.next();
			if("A01".equalsIgnoreCase(fieldsetid)){
				ArrayList list = (ArrayList)map.get(fieldsetid);
				for(int n=0;n<list.size();n++){
					FieldItem field = (FieldItem)list.get(n);
					if("A".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("0".equals(field.getCodesetid())){
							if("1".equals(querylike)){
								sb.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
							}else
								sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}else{
							if(field.getValue().indexOf("`")!=-1){
								sb.append(field.getItemid()+" in ('#"+field.getValue().replaceAll("`", "','")+"') and ");
							}else
								sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
						}
					}else if("M".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("1".equals(querylike)){
							sb.append(field.getItemid()+" like '%"+field.getValue()+"%' and ");
						}else
							sb.append(field.getItemid()+"='"+field.getValue()+"' and ");
					}else if("D".equals(field.getItemtype())){
						if((field.getValue()==null||field.getValue().length()==0)&&(field.getViewvalue()==null||field.getViewvalue().length()==0))
							continue;
						sb.append(analyFieldDate(field));
					}else if("N".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						sb.append(field.getItemid()+"="+field.getValue()+" and ");
					}
				}
			}else{
				/*if(tables.toString().toUpperCase().indexOf((userbase+fieldsetid).toUpperCase())==-1){
					tables.append(","+userbase+fieldsetid);      //有可能字段没有值，在此添加，会影响查询结果
				}*/
				ArrayList list = (ArrayList)map.get(fieldsetid);
				for(int n=0;n<list.size();n++){
					FieldItem field = (FieldItem)list.get(n);
					if("A".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("0".equals(field.getCodesetid())){
							if("1".equals(querylike)){
								sb.append(userbase+fieldsetid+"."+field.getItemid()+" like '%"+field.getValue()+"%' and ");
							}else
								sb.append(userbase+fieldsetid+"."+field.getItemid()+"='"+field.getValue()+"' and ");
						}else{
							if(field.getValue().indexOf("`")!=-1){
								sb.append(userbase+fieldsetid+"."+field.getItemid()+" in ('#"+field.getValue().replaceAll("`", "','")+"') and ");
							}else
								sb.append(userbase+fieldsetid+"."+field.getItemid()+"='"+field.getValue()+"' and ");
						}
					}else if("M".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						if("1".equals(querylike)){
							sb.append(userbase+fieldsetid+"."+field.getItemid()+" like '%"+field.getValue()+"%' and ");
						}else
							sb.append(userbase+fieldsetid+"."+field.getItemid()+"='"+field.getValue()+"' and ");
					}else if("D".equals(field.getItemtype())){
						if((field.getValue()==null||field.getValue().length()==0)&&(field.getViewvalue()==null||field.getViewvalue().length()==0))
							continue;
						sb.append(analyFieldDate(field));
					}else if("N".equals(field.getItemtype())){
						if(field.getValue()==null||field.getValue().length()==0)
							continue;
						sb.append(userbase+fieldsetid+"."+field.getItemid()+"="+field.getValue()+" and ");
					}
					if(tables.toString().toUpperCase().indexOf((userbase+fieldsetid).toUpperCase())==-1){//zhaogd 2013-11-14 只有当本子集内的字段有值时，才添加到查询语句内，所以修改语句的位置，否则，select语句结果出错
						tables.append(","+userbase+fieldsetid);
					}
					sb.append(userbase+"A01.a0100="+userbase+fieldsetid+".a0100"+" and "+userbase+fieldsetid+".i9999=(select max(i9999) from "+userbase+fieldsetid+" where "+userbase+"A01.a0100="+userbase+fieldsetid+".a0100) and ");
				}
				//sb.append(userbase+"A01.a0100="+userbase+fieldsetid+".a0100"+" and "+userbase+fieldsetid+".i9999=(select max(i9999) from "+userbase+fieldsetid+" where "+userbase+"A01.a0100="+userbase+fieldsetid+".a0100) and ");
			}
		}
		if(select_name!=null&&select_name.length()>0){
			if("1".equalsIgnoreCase(querylike)){
				sb.append("a0101 like '%"+select_name+"%' and ");
			}else{
				sb.append("a0101='"+select_name+"' and ");
			}	
		}
		String wherestr = "";
		if(sb.length()>4){
			wherestr=" and "+sb.substring(0, sb.length()-5);
		}
		return wherestr;
	}
	
	private String analyFieldDate(FieldItem item) throws GeneralException{
		StringBuffer sb = new StringBuffer();
		String s_str_date=item.getValue();
        String e_str_date=item.getViewvalue();
        s_str_date=s_str_date.replaceAll("\\.","-");
        e_str_date=e_str_date.replaceAll("\\.","-"); 
        try{
	        Date s_date=DateStyle.parseDate(s_str_date);
	        Date e_date=DateStyle.parseDate(e_str_date);
	        if(s_str_date.length()>=8&&e_str_date.length()>=8){
	        	sb.append(item.getItemid()+" between "+Sql_switcher.dateValue(s_str_date)+" and "+Sql_switcher.dateValue(e_str_date)+" and ");
	        }else{
	        	if(s_str_date.length()>=8){
	        		sb.append(item.getItemid()+" >= "+Sql_switcher.dateValue(s_str_date)+" and ");
	        	}else if(e_str_date.length()>=8){
	        		sb.append(item.getItemid()+" <= "+Sql_switcher.dateValue(e_str_date)+" and ");
	        	}
	        }
        }catch(Exception e){
        	e.printStackTrace();
        	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
        }
		return sb.toString();
	}
	
	private String analyseManagePriv(String managed_str){
		if(this.userView.isSuper_admin()&&!this.userView.isBThreeUser())
			return "codeitemid like '%'";
		else {
			if(managed_str.length()<3){
				if("64".equals(managed_str)||"65".equals(managed_str)){
					return "codeitemid like '%'";
				}else{
					return "1=2";
				}
			}else {
			StringBuffer sb = new StringBuffer();
			String[] strS = managed_str.split(",");
	 		 String ids="";
	 		 for(int i=0;i<strS.length;i++){
	 			 String id = strS[i];
	 			 if(id!=null&&id.length()>1){
	 				 boolean check = true;
	 				 for(int j=0;j<strS.length;j++){
	 					 String id_s = strS[j];
	 					 if(id_s!=null&&id_s.length()>1){
	 						 if(id.length()>id_s.length()){
	 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
									 check = false;
									 ids=id_s;
									 break;
								 }
	 						 }else{
	 							 if(id.equalsIgnoreCase(id_s)){
	 								 continue;
	 							 }
	 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
	 								 check = false;
	 								ids=id_s;
	 								 break;
	 							 }
	 						 }
	 					 }
	 				 }
	 				 if(check){
	 					if(sb.indexOf(id)==-1)
	 						sb.append(" or codeitemid like '"+id.substring(2)+"%'");
	 				 }else{
	 					 if(id.length()<ids.length()){
	 						if(sb.indexOf(id)==-1)
	 							sb.append(" or codeitemid like '"+id.substring(2)+"%'");
	 					 }
	 				 }
	 			 }
	 		 }
	 		if(sb.length()<4)
				return "1=2";
			else
				return sb.substring(3);
			}
		}
	}
	
	private int getCodeSetidChildLen(String codesetid) {
        String sql="select count(*) aa from codeitem where codesetid = '"+codesetid+"'";
        RowSet rs=null;
        int count=0;
        try
        {
            ContentDAO dao=new ContentDAO(this.frameconn);
            rs=dao.search(sql);
            if(rs.next())
                count=rs.getInt("aa");
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            if(rs!=null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return count;
    }
}
