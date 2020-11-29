package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.ValidateruleBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

public class ValidateRuleTrans extends IBusiness {

	public void execute() throws GeneralException {
		String error="";
		 String start1;//用于判断-号出现的位置
		//  String i;//用于判断字符串中'-'号的出现位置,定义的循环变量
		  String chkyear;//用于截取年
		  String chkyearinteger;
		  String chkmonths;//用于截取月
		  String chkmonthsinteger;
		  String chkdays;//用于截取日
		  String chkdaysinteger;
		  String chk1;//用于按位判断输入的年,月,日是否为整数
		  String chk2;
		  HashMap mon=new HashMap();/*声明一个日期天数的数组*/
		 mon.put("1", "31");
		 mon.put("2", "28");
		 mon.put("3", "31");
		 mon.put("4", "30");
		 mon.put("5", "31");
		 
		 mon.put("6", "30");
		 mon.put("7", "31");
		 mon.put("8", "31");
		 mon.put("9", "30");
		 mon.put("10", "31");
		 
		 mon.put("11", "30");
		 mon.put("12", "31");
		 String number="0123456789";
		 String number2="0123456789.";
		try
		{
			HashMap hm=this.getFormHM();
			//String date=(String)hm.get("date");
			//hm获得值,名字从组合里取
			//获取组合
			ValidateruleBo validateruleBo=new ValidateruleBo(this.getFrameconn());
			ArrayList subUnitList = validateruleBo.getUnderUnitList(this.getFrameconn());
			boolean subUnitListflag=false;
			for (int i = 0; i < subUnitList.size(); i++) {
				if(subUnitListflag){
					break;
				}
				DynaBean bean = (DynaBean) subUnitList.get(i);
				// subUnitList.get(i);
				String unitcode = (String) bean.get("codeitemid");
				String unitname = (String) bean.get("codeitemdesc");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				ArrayList reportSetList = validateruleBo.getSelfSortList();
				String[] subclass = new String[reportSetList.size()];
				String fieldname_u = "";
				String fieldname_d = "";
				String fieldvalue_u="";
				String fieldvalue_d="";
				String persontype="";
				if (i % 2 == 0)
					persontype = "原有人员";
				else
					persontype = "新增人员";
				for (int j = 0; j < reportSetList.size(); j++) {
					String value = "";
					subclass[j] = (String) reportSetList.get(j);
					FieldItem fielditem = DataDictionary.getFieldItem(subclass[j]
							.toUpperCase());
					String fielditemdesc="";
					if (fielditem != null && "D".equals(fielditem.getItemtype())) {
						fieldname_u = fielditem.getItemid() + "_u_"+unitcode+"_"+i%2;
						fieldname_d = fielditem.getItemid() + "_d_"+unitcode+"_"+i%2;
						fielditemdesc = fielditem.getItemdesc();
						if(hm.get(fieldname_u)!=null&&hm.get(fieldname_u).toString().length()>0){
							fieldvalue_u = hm.get(fieldname_u).toString();
						
							  
								String temps[]=null;
								boolean flag=false;
								if(fieldvalue_u.indexOf(".")!=-1)
								{
									temps=fieldvalue_u.split(".");
									flag=true;
								}
								if(fieldvalue_u.indexOf("-")!=-1)
								{
									temps=fieldvalue_u.split("-");
									flag=true;
								}
								if(flag==false)
								{
									 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限的日期格式不对,格式为2006-05-25";
									 subUnitListflag=true;
									 break;
								}
								
								if(temps.length!=3)
								{
									error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限的日期格式不对,格式为2006-05-25";
									 subUnitListflag=true;
									 break;
								}
								boolean inflag=false;
								for(int m=0;m<temps.length;m++){
								for(int n=0;n<temps[m].length();n++){
									if(number.indexOf(temps[m].charAt(n))==-1){
										error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限的日期格式不对,格式为2006-05-25";	
										 subUnitListflag=true;
										 inflag=true;
										break;
									}
									}
								}
								if(inflag){
									break;
								}
								if(!(Integer.parseInt(temps[0])>=1900&&Integer.parseInt(temps[0])<=2100))
								{
									error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限的年要大于1900,小于2100";
									// subUnitListflag=true;
									 break;
								}
								chkyearinteger=temps[0];
								 //根据年设2月份的日期
								    if(Integer.parseInt(chkyearinteger)%100==0||Integer.parseInt(chkyearinteger)%4==0)
								    {
								    mon.put("2", "29");
								    }
								    else
								    {
								    	 mon.put("2", "28");
								    }
								    //判断月是否符合条件
								    chkmonths=temps[1];
								    chkmonthsinteger=temps[1];
								    if(!(Integer.parseInt(chkmonthsinteger)>=1&&Integer.parseInt(chkmonthsinteger)<=12))
								    {
								    	error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限的月要大于等于1,小于等于12";
								    	 subUnitListflag=true;
										 break;
								    }
							    //判断日期是否符合条件
								    chkdays=temps[2];
								    chkdaysinteger=temps[1];
								   
								    switch(Integer.parseInt(chkmonthsinteger))
								    {
								    
								     case 1:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("1").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 3:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("3").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 5:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("5").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 7:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("7").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     
								     case 8:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("8").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 10:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("10").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 12:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("12").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 4:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("4").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 6:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("6").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 9:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("9").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 11:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("11").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								        }
								     break;
								     case 2:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("2").toString())))
								     {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的上限请填写日期类型";
								     }
								     break;
								     }
								    //日期判断结束


						  		
						
						}
						if(hm.get(fieldname_d)!=null&&hm.get(fieldname_d).toString().length()>0){
							fieldvalue_u = hm.get(fieldname_d).toString();
						
							  
								String temps[]=null;
								boolean flag=false;
								if(fieldvalue_u.indexOf(".")!=-1)
								{
									temps=fieldvalue_u.split(".");
									flag=true;
								}
								if(fieldvalue_u.indexOf("-")!=-1)
								{
									temps=fieldvalue_u.split("-");
									flag=true;
								}
								if(flag==false)
								{
									 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限的日期格式不对,格式为2006-05-25";
									 subUnitListflag=true;
									 break;
								}
								
								if(temps.length!=3)
								{
									error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限的日期格式不对,格式为2006-05-25";
									 subUnitListflag=true;
									 break;
								}
								boolean inflag=false;
								for(int m=0;m<temps.length;m++){
									for(int n=0;n<temps[m].length();n++){
										if(number.indexOf(temps[m].charAt(n))==-1){
											error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限的日期格式不对,格式为2006-05-25";	
											 subUnitListflag=true;
											 inflag=true;
											break;
										}
										}
									}
									if(inflag){
										break;
									}
								if(!(Integer.parseInt(temps[0])>=1900&&Integer.parseInt(temps[0])<=2100))
								{
									error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限的年要大于1900,小于2100";
									 subUnitListflag=true;
									 break;
								}
								chkyearinteger=temps[0];
								 //根据年设2月份的日期
								    if(Integer.parseInt(chkyearinteger)%100==0||Integer.parseInt(chkyearinteger)%4==0)
								    {
								    mon.put("2", "29");
								    }
								    else
								    {
								    	 mon.put("2", "28");
								    }
								    //判断月是否符合条件
								    chkmonths=temps[1];
								    chkmonthsinteger=temps[1];
								    if(!(Integer.parseInt(chkmonthsinteger)>=1&&Integer.parseInt(chkmonthsinteger)<=12))
								    {
								    	error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限的月要大于等于1,小于等于12";
								    	 subUnitListflag=true;
										 break;
								    }
							    //判断日期是否符合条件
								    chkdays=temps[2];
								    chkdaysinteger=temps[1];
								   
								    switch(Integer.parseInt(chkmonthsinteger))
								    {
								    

									    
								     case 1:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("1").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 3:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("3").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 5:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("5").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 7:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("7").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     
								     case 8:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("8").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 10:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("10").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 12:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("12").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 4:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("4").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 6:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("6").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 9:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("9").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 11:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("11").toString())))
								        {
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        }
								     break;
								     case 2:if(!(Integer.parseInt(chkdays)>0&&Integer.parseInt(chkdays)<=Integer.parseInt(mon.get("2").toString())))
								    	 error=unitname+"-"+persontype+"-"+fielditemdesc+"的下限请填写日期类型";
								        break;
								    
								     
								     
								    
								     
								     }//日期判断结束


						  		
						
						}

						
					} else {

						fieldname_u = fielditem.getItemid() + "_u_"+unitcode+"_"+i%2;
						fielditemdesc = fielditem.getItemdesc();
						if(hm.get(fieldname_u)!=null&&hm.get(fieldname_u).toString().length()>0){
							fieldvalue_u = hm.get(fieldname_u).toString();
							if(fieldvalue_u.indexOf(".")!=-1&&fieldvalue_u.indexOf(".")!=fieldvalue_u.lastIndexOf(".")){
								error=unitname+"-"+persontype+"-"+fielditemdesc+"中数据格式不正确,不能有多个顿号";
								break;
							}
							  for(int m=0;m<fieldvalue_u.length();m++){
								 
								  if(number2.indexOf(fieldvalue_u.charAt(m))==-1){
										error=unitname+"-"+persontype+"-"+fielditemdesc+"中数据请填写数字类型";
										break;
								  }
							  }
								if(fieldvalue_u.indexOf(".")!=-1){
							  if(fieldvalue_u.substring(0,fieldvalue_u.indexOf(".")).length()>12){
								  error=unitname+"-"+persontype+"-"+fielditemdesc+"中数据填写数字过大";
									break;
							  }
								}else{
									  if(fieldvalue_u.length()>12){
										  error=unitname+"-"+persontype+"-"+fielditemdesc+"中数据填写数字过大";
											break;
									  }
								}
						  		
						
						}

					
					
					}

				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		if ("".equals(error)) {
			try
			{
				HashMap hm=this.getFormHM();
				//hm获得值,名字从组合里取
				//获取组合
				ValidateruleBo validateruleBo=new ValidateruleBo(this.getFrameconn());
				ArrayList subUnitList = validateruleBo.getUnderUnitList(this.getFrameconn());
				 //Statement stmt = this.getFrameconn().createStatement();
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				//sqlbuffer.append("begin ");
				for (int i = 0; i < subUnitList.size(); i++) {
					StringBuffer sqlbuffer = new StringBuffer();
					DynaBean bean = (DynaBean) subUnitList.get(i);
					// subUnitList.get(i);
					String unitcode = (String) bean.get("codeitemid");
					
					String unitname = (String) bean.get("codeitemdesc");
					ArrayList reportSetList = validateruleBo.getSelfSortList();
					String[] subclass = new String[reportSetList.size()];
					String fieldname_u = "";
					String fieldname_d = "";
					String fieldname_u_x = "";
					String fieldname_d_x = "";
					String fieldvalue_u="";
					String fieldvalue_d="";
					String persontype="";
					if (i % 2 == 0)
						persontype = "原有人员";
					else
						persontype = "新增人员";
					StringBuffer fieldnames = new StringBuffer();
					StringBuffer fieldvalues = new StringBuffer();
					for (int j = 0; j < reportSetList.size(); j++) {
						String value = "";
						subclass[j] = (String) reportSetList.get(j);
						FieldItem fielditem = DataDictionary.getFieldItem(subclass[j]
								.toUpperCase());
						String fielditemdesc="";
					//	System.out.println(hm.get(fielditem.getItemid() + "_u_"+unitcode+"_"+i%2+":"+fielditem.getItemid() + "_u_"+unitcode+"_"+i%2).toString());
						if (fielditem != null && "D".equals(fielditem.getItemtype())) {
							fieldname_u_x = fielditem.getItemid() + "_u_"+unitcode+"_"+i%2;
							fieldname_d_x = fielditem.getItemid() + "_d_"+unitcode+"_"+i%2;
							fieldname_u = fielditem.getItemid() + "_u";
							fieldname_d = fielditem.getItemid() + "_d";
							fielditemdesc = fielditem.getItemdesc();
							
							if(hm.get(fieldname_u_x)!=null&&hm.get(fieldname_u_x).toString().length()>0){
								fieldvalue_u = hm.get(fieldname_u_x).toString();
								fieldnames.append(fieldname_u+",");
								fieldvalues.append(Sql_switcher.dateValue(fieldvalue_u)+",");
								//System.out.println("fieldvalue_u:"+fieldvalue_u);
							}
							if(hm.get(fieldname_d_x)!=null&&hm.get(fieldname_d_x).toString().length()>0){
								fieldvalue_d = hm.get(fieldname_d_x).toString();
								fieldnames.append(fieldname_d+",");
								fieldvalues.append(Sql_switcher.dateValue(fieldvalue_d)+",");
								//System.out.println("fieldvalue_d:"+fieldvalue_d);
							}
						} else {
							fieldname_u_x = fielditem.getItemid() + "_u_"+unitcode+"_"+i%2;
							fieldname_u = fielditem.getItemid() + "_u";
							if(hm.get(fieldname_u_x)!=null&&hm.get(fieldname_u_x).toString().length()>0){
								fieldvalue_u = hm.get(fieldname_u_x).toString();
								fieldnames.append(fieldname_u+",");
								fieldvalues.append("'"+fieldvalue_u+"',");
								//System.out.println("fieldvalue_u:"+fieldvalue_u);
							}
						}
					}
					
					ResultSet rs = dao
					.search("select * from tt_updown_rule  where emtype='"+unitcode+"' and emflag='"+i % 2+"' ");
			if(rs.next()){
			ResultSetMetaData data=rs.getMetaData();
			//进行更新
			String fieldname =fieldnames.toString();
			String fieldn[]=null;
			String fieldv[]=null;
			if(fieldname.length()>0)
			fieldname = fieldname.substring(0,fieldname.length()-1);
			if(fieldname.indexOf(",")!=-1){
				 fieldn = fieldname.split(",");
			}
			String fieldvalue =fieldvalues.toString();
			if(fieldvalue.length()>0)
				fieldvalue = fieldvalue.substring(0,fieldvalue.length()-1);
			if(fieldvalue.indexOf(",")!=-1){
				 fieldv = fieldvalue.split(",");
			}
//			String sql="";
//			if(fieldn!=null)
//			for(int m=0;m<fieldn.length;m++){
//				sql+=fieldn[m]+"="+fieldv[m]+",";
//			}
//			if(!sql.equals("")){
//				sql=sql.substring(0, sql.length()-1);
			if(!"".equals(fieldname))
				fieldname=","+fieldname;
			if(!"".equals(fieldvalue))
				fieldvalue=","+fieldvalue;
				sqlbuffer.append(" delete from tt_updown_rule where emtype='"+unitcode+"' and emflag='"+i % 2+"' " );
				 if(!"".equals(sqlbuffer.toString()))
					 dao.delete(sqlbuffer.toString(), new ArrayList());
					 //stmt.executeUpdate(sqlbuffer.toString());//由于orcale库不支持一下执行多条sql，则每个sql单独执行一次  zhaoxg add 2013-12-28
				 sqlbuffer.setLength(0);
//				 sqlbuffer.append(" update tt_updown_rule set  (emtype,emflag"+fieldname+") values('"+unitcode+"','"+i % 2+"'"+fieldvalue+") where emtype='"+unitcode+"' and emflag='"+i % 2+"' ");
				sqlbuffer.append(" insert into tt_updown_rule (emtype,emflag"+fieldname+") values('"+unitcode+"','"+i % 2+"'"+fieldvalue+")" );
				 if(!"".equals(sqlbuffer.toString()))
					 dao.insert(sqlbuffer.toString(), new ArrayList());
					 //stmt.executeUpdate(sqlbuffer.toString());
				 sqlbuffer.setLength(0);
//			}
			}else{
				//执行插入
				String fieldname =fieldnames.toString();
				if(fieldname.length()>0)
				fieldname = fieldname.substring(0,fieldname.length()-1);
				String fieldvalue =fieldvalues.toString();
				if(fieldvalue.length()>0){
					fieldvalue = fieldvalue.substring(0,fieldvalue.length()-1);
					//System.out.println("unitcode="+unitcode);
				//	sqlbuffer.append(" delete from tt_updown_rule where emtype='"+unitcode+"' and emflag='"+i % 2+"' " );
				if(!"".equals(fieldname))
					fieldname=","+fieldname;
				if(!"".equals(fieldvalue))
					fieldvalue=","+fieldvalue;
					sqlbuffer.append(" insert into tt_updown_rule (emtype,emflag"+fieldname+") values('"+unitcode+"','"+i % 2+"'"+fieldvalue+")" );
					 if(!"".equals(sqlbuffer.toString()))
						 dao.insert(sqlbuffer.toString(), new ArrayList());
						 //stmt.executeUpdate(sqlbuffer.toString());
					 sqlbuffer.setLength(0);
				}
			}
			
			//sqlbuffer.append(" end ;");
//			 Statement stmt = this.getFrameconn().createStatement();
			// System.out.println(sqlbuffer.toString());
//			 if(!sqlbuffer.toString().equals(""))
//		    stmt.executeUpdate(sqlbuffer.toString());
			
				}
				
					
				

			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		this.getFormHM().put("error",error);
	}
	
	

}
