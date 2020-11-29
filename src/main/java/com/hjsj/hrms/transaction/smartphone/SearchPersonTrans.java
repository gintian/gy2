package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String queryValue=(String)this.getFormHM().get("queryValue");
		String selectField = (String)this.getFormHM().get("selectField");
		String a_code = (String) this.getFormHM().get("a_code");
		String nbase = (String)this.getFormHM().get("nbase");
		String dbpre = "###";
		String colums = "flag,codesetid,codeitemid,codeitemdesc,count";
		int allcount=0;
		String p_codeitemdesc="";
		StringBuffer tmpsql = new StringBuffer();
		try{
			String codesetid = "UN";
			String codeitemid = "";
			if (a_code.length() < 2) {
				if (!this.userView.isSuper_admin()) {
					codesetid = this.userView.getManagePrivCode();
					codeitemid = this.getUserView().getManagePrivCodeValue();
				}
			}else{
				codesetid = a_code.substring(0,2);
				codeitemid = a_code.substring(2);
			}
			if (codesetid == null || codesetid.length() == 0)
				codesetid = "UN";
			if (codeitemid == null)
				codeitemid = "";
			p_codeitemdesc = com.hrms.frame.utility.AdminCode.getCodeName(codesetid, codeitemid);
			String [] values = queryValue.split(":");
			String [] selectFields = selectField.split(",");
			String lxpr="";
			String factor="";
			if(values.length==selectFields.length){
				int num=1;
				for(int i=0;i<values.length;i++){
					String itemid=selectFields[i];
					FieldItem fielditem = DataDictionary
					.getFieldItem(itemid);
					if(fielditem!=null){
						String itemtype=fielditem.getItemtype();
						if("N".equals(itemtype)){
							String value = values[i];
							String[] vs=value.split(",");
							if(vs.length==2){
								String v1=vs[0].substring(1);
								String v2 = vs[1];
								if(v1.length()>0){
									try{
										Integer.parseInt(v1);
										lxpr+="*"+num;
										factor+=itemid;
										switch(Integer.parseInt(v2)){
										case 0:
											factor+=">";
											break;
										case 1:
											factor+="=";
											break;
										case 2:
											factor+="<";
											break;
										}
										factor+=v1+"`";
										num++;
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						}else if("A".equals(itemtype)){
							String value=values[i].substring(1);
							if(value.length()>0){
								if("a0101".equalsIgnoreCase(itemid)){
									 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
									 String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
									 if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field))){
										 lxpr+="*("+num;
										 factor+=itemid+"="+value+"`";
										 num++;
										 lxpr+="+"+num+")";
										 factor+=pinyin_field+"="+value+"`";
										 num++;
									 }else{
										 lxpr+="*"+num;
										 factor+=itemid+"="+value+"`";
										 num++;
									 }
								}else{
									lxpr+="*"+num;
									factor+=itemid+"="+value+"`";
									num++;
								}
							}
						}else if("D".equals(itemtype)){
							String value=values[i];
							String[] vs = value.split(",");
							if(vs.length==2){
								String v1=vs[0].substring(1);
								String v2=vs[1];
								if(v1.length()>0){
									if(this.isnumber(v1)){
										lxpr+="*"+num;
										factor+=itemid+">=$YRS["+v1+"]`";
										num++;
									}else{
										v1=checkdate(v1);
										if(!"false".equals(v1)){
											lxpr+="*"+num;
											factor+=itemid+">="+v1+"`";
											num++;
										}
									}
								}
								if(v2.length()>0){
									if(this.isnumber(v2)){
										lxpr+="*"+num;
										factor+=itemid+"<=$YRS["+v2+"]`";
										num++;
									}else{
										v2=this.checkdate(v2);
										if(!"false".equals(v2)){
											lxpr+="*"+num;
											factor+=itemid+"<="+v2+"`";
											num++;
										}
									}
								}
							}else if(vs.length==1){
								String v1=vs[0].substring(1);
								if(v1.length()>0){
									if(this.isnumber(v1)){
										lxpr+="*"+num;
										factor+=itemid+">=$YRS["+v1+"]`";
										num++;
									}else{
										v1=checkdate(v1);
										if(!"false".equals(v1)){
											lxpr+="*"+num;
											factor+=itemid+">="+v1+"`";
											num++;
										}
									}
								}
							}
						}
					}
				}
				if(lxpr.length()>0)
					lxpr=lxpr.substring(1);
				String strwhere=userView.getPrivSQLExpression(lxpr+"|"+factor,dbpre,false,true,true,new ArrayList());
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer strsql = new StringBuffer();
				if ("UN".equals(codesetid)) {
						strsql
								.append("select 'per' flag,'"
										+ dbpre
										+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,'1' count,a0000 from "
										+ dbpre
										+ "A01 where b0110 like '"
										+ codeitemid
										+ "%'");
				} else if ("UM".equals(codesetid)) {
						strsql
								.append("select 'per' flag,'"
										+ dbpre
										+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,'1' count,a0000 from "
										+ dbpre + "A01 where e0122 like '"
										+ codeitemid
										+ "%'");
				} else if ("@K".equals(codesetid)) {
						strsql
								.append("select 'per' flag,'"
										+ dbpre
										+ "' codesetid,a0100 codeitemid,a0101 codeitemdesc,'1' count,a0000 from "
										+ dbpre + "A01 where e01a1='"
										+ codeitemid + "'");
				}
				strsql.append("and a0101 in(select a0101 "+strwhere+")");
				
				if("all".equals(nbase)){
					ArrayList dbprelist = this.userView.getPrivDbList();
					for(int i=0;i<dbprelist.size();i++){
						String pre = (String)dbprelist.get(i);
						tmpsql.append(strsql.toString().replaceAll("###", pre)+" union all ");
					}
					tmpsql.setLength(tmpsql.length()-11);
				}else{
					tmpsql.append(strsql.toString().replaceAll("###", nbase));
				}
				String sql = "select count(codeitemid) allcount from ("+tmpsql.toString()+") ttt where count>0";
				this.frowset = dao.search(sql);
				if(this.frowset.next())
					allcount=this.frowset.getInt("allcount");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("sql", tmpsql.toString());
			this.getFormHM().put("columns",colums);
            this.getFormHM().put("strwhere","");  
            this.getFormHM().put("p_a_code", a_code);
            this.getFormHM().put("allcount", ""+allcount);
            this.getFormHM().put("showstyle", "2");
            this.getFormHM().put("p_codeitemdesc", p_codeitemdesc);
		}
	}

	private String checkdate(String str){
		boolean flag = true;
		//String str="2010年";
		if(str.length()<4){
			return "false";
		}
		if(str.length()==4){
			Pattern p= Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str+"-01-01";
			}else{
				return "false";
			}
		}
		if(str.length()<6){
			Pattern p= Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str.replace("年", "-")+"01-01";
			}else{
				return "false";
			}
		}
		if(str.length()==7){
			if(str.indexOf("月")!=-1){
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					if(str.indexOf("月")!=-1){
						return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
					}else{
						return str.replace("年", "-").replace(".", "-")+"-01";
					}
				}else{
					return "false";
				}
			}else{
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					return str.replace("年", "-").replace(".", "-")+"-01";
				}else{
					return "false";
				}
			}
		}
		if(str.length()<8){//2010年3  2010年3月
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				if(str.indexOf("月")!=-1){
					return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
				}else{
					return str.replace("年", "-").replace(".", "-")+"-01";
				}
			}else{
				return "false";
			}
		}
		if(str.length()==8){//2010年3  2010年3月1
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				str =str.replace("年", "-").replace(".", "-").replace("月", "-");
				if(str.lastIndexOf("-")==str.length()){
					if(str.length()<10){
						return str+"01";
					}
				}else{
					String[] temps=str.split("-");
					if(temps.length>2){
						String t="false";
						if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
							int year = Integer.parseInt(temps[0]);
							int month=Integer.parseInt(temps[1]);
							int day=Integer.parseInt(temps[2]);
							switch(month){
							case 1:
							case 3:
							case 5:
							case 7:
							case 8:
							case 10:
							case 12:
							{
								if(1<=day&&day<=31){
									t=str;
								}
								break;
							}
							case 4:
							case 6:
							case 9:
							case 11:
							{
								if(1<=day&&day<=30){
									t=str;
								}
								break;
							}
							case 2:
							{
								 if(isLeapYear(year)){
									 if(1<=day&&day<=29){
											t=str;
									}
								 }else{
									 if(1<=day&&day<=28){
											t=str;
									}
								 }
								 break;
							}
								
							}
						}
						return t;
					}else{
						return "false";
					}
					
					
				}
			}else{
				return "false";
			}
		}
		Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			String temp=str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
			String[] temps = temp.split("-");
			String t="false";
			if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
				int year = Integer.parseInt(temps[0]);
				int month=Integer.parseInt(temps[1]);
				int day=Integer.parseInt(temps[2]);
				switch(month){
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
				{
					if(1<=day&&day<=31){
						t=temp;
					}
					break;
				}
				case 4:
				case 6:
				case 9:
				case 11:
				{
					if(1<=day&&day<=30){
						t=temp;
					}
					break;
				}
				case 2:
				{
					 if(isLeapYear(year)){
						 if(1<=day&&day<=29){
								t=temp;
						}
					 }else{
						 if(1<=day&&day<=28){
								t=temp;
						}
					 }
					 break;
				}
					
				}
			}
			return t;
		}else{
			return "false";
		}
	}
	private boolean isLeapYear(int year){
		boolean t=false;
		if(year%4==0){
			   if(year%100!=0){
				   t=true;
			   }else if(year%400==0){
				   t=true;
			   }
		  }
		return t;
	}
	private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue.replaceAll("-","."));
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
    }
}
