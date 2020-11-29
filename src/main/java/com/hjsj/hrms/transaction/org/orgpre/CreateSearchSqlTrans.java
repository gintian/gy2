package com.hjsj.hrms.transaction.org.orgpre;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateSearchSqlTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		    ArrayList logics = (ArrayList)formHM.get("logics");  //逻辑
		    ArrayList itemids = (ArrayList)formHM.get("itemids");  //查询指标
		    ArrayList factors = (ArrayList)formHM.get("factors"); // 关系，用1，2，3，4，5，6代替
		    ArrayList itemvalues = (ArrayList)formHM.get("itemvalues"); //查询值
		    String searchWhere = (String)formHM.get("searchWhere"); // OrgPreTableTrans 查询生成的where后面语句
		    String setid = (String)formHM.get("setid"); // 编制子集id
		    String a_code = (String)formHM.get("a_code");   //选择的组织单元id
		    List arr = new ArrayList();
		    if(!userView.isSuper_admin()){
    		    if(a_code.length()>0)
    		       arr.add(a_code);
    		    else{
    		      a_code = this.userView.getUnitIdByBusi("4");
    		      String[] privorg = a_code.split("`");
    		      for(int i=0;i<privorg.length;i++)
    		          arr.add(privorg[i]);
    		    }
		    }
		    String sqlstr = (String)formHM.get("sqlstr"); // 原查询语句
		    sqlstr = sqlstr.substring(0, sqlstr.indexOf("where")); // 截取where之前的str
		    String querylike = formHM.get("querylike")==null ?"":formHM.get("querylike").toString();
		    String searchtype = formHM.get("searchtype").toString();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    StringBuffer newsql = new StringBuffer(sqlstr);
		    newsql.append(" where org.codeitemid in ( select codeitemid from organization where 1=1  ");
		    if(arr.size()>0){
    		        newsql.append(" and (");
    		    for(int i=0;i<arr.size();i++)
    		        newsql.append(" codeitemid like '"+arr.get(i).toString().substring(2)+"%' or");
    		    newsql.delete(newsql.length()-2, newsql.length());
    		    newsql.append(" )");
		    }
		    newsql.append(" and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date) ");
		    newsql.append(searchWhere);
		    String itemid;
		    String logic="";
		    String factor;
		    String itemvalue;
		    String b0110;
		    boolean flag = false;
		    FieldItem fi = null;
		    newsql.append(" and (");
		    try{
		    	
			    for(int i=0;i<itemids.size();i++){
			    	if(itemids.get(i) == null || ((String)itemids.get(i)).length()<1){
			    		continue;
			    	}
			    	
			    	itemid = itemids.get(i).toString();
			    	factor = factors.get(i).toString();
			    	itemvalue = itemvalues.get(i).toString();
			    	b0110=" b0110 ";
			    	if("b0110".equals(itemid.toLowerCase())){
			    		itemid = "codeitemid";
			    		fi = new FieldItem();
			    		fi.setFieldsetid("organization");
			    		fi.setItemtype("A");
			    		b0110="codeitemid";
			    	}else
			    	   fi = DataDictionary.getFieldItem(itemid);
			    	
			    	if(flag)
			    	  newsql.append(logics.get(i));
			    	
			    	if(fi.getFieldsetid().toUpperCase().equals(setid.toUpperCase()))
			    		newsql.append(" ( ");
			    	else
			    	    newsql.append(" org.codeitemid in ( select "+b0110+" from "+fi.getFieldsetid()+" where ");
			    	
			    	if("D".equals(fi.getItemtype())){
			    	     newsql.append(formatDate(itemvalue,itemid,getfactor(factor)));
			    	
			    	}else if(("A".equals(fi.getItemtype()) && "1".equals(querylike)) || "M".equals(fi.getItemtype())){
			    		
			    		 newsql.append(itemid+" like '%"+itemvalue+"%'");
			    	}
			    	else if("N".equals(fi.getItemtype())){
			    		 int valuenum = Integer.parseInt(itemvalue);
			    		 newsql.append(itemid+getfactor(factor)+valuenum);
			    	}else{
			    		if(itemvalue.indexOf("*")!= -1 || itemvalue.indexOf("?")!= -1 || itemvalue.indexOf("？")!=-1){
			    			String newvalue = "";
			    			for(int k=0;k<itemvalue.length();k++){
			    				char v = itemvalue.charAt(k);
			    				if(v == '*')
			    					v='%';
			    				else if(v == '?' || v == '？')
			    					v = '_';
			    				newvalue+=v;
			    			}
			    			newsql.append(itemid+" like '"+newvalue+"'");
			    		}else
			    		    newsql.append(itemid+getfactor(factor)+"'"+itemvalue+"' ");
			    	}
			    	if(DataDictionary.getFieldSetVo(fi.getFieldsetid()) != null){
				    	FieldSet fieldset = DataDictionary.getFieldSetVo(fi.getFieldsetid());
				    	if(!"0".equals(fieldset.getChangeflag()) && !fi.getFieldsetid().toUpperCase().equals(setid.toUpperCase())){
				    		newsql.append(" and I9999=(select max(I9999) from "+fi.getFieldsetid()+" where b0110=org.codeitemid)");
				    	}
			    	}
			    	newsql.append(" ) ");
			    	flag = true;   
			    }
		    }catch(Exception e){
		    	if(fi!=null)
		    		throw GeneralExceptionHandler.Handle(new GeneralException(fi.getItemdesc()+"  输入格式错误！"));
		    	else
		    		e.printStackTrace();
		    		return;
		    }
		    if(!flag)
		        newsql.append(" 1=1 ");
		    
		    newsql.append(" ) ");
		    
		     if("UN".equals(searchtype))
		    	 newsql.append(" and org.codesetid='UN' ");
		     else if("UM".equals(searchtype))
		    	 newsql.append(" and org.codesetid='UM' ");
		    	 
		     newsql.append(" order by a0000");
		     this.formHM.put("searchstr", newsql.toString());
		     this.formHM.put("searchOrg", "false");
	}
	
	public String getfactor(String str){
		String factor="";
		char i = str.trim().toCharArray()[0];
		switch(i){
			case '1': factor=" = "; break;
			case '2': factor=" > "; break;
			case '3': factor=" >= "; break;
			case '4': factor=" < "; break;
			case '5': factor=" <= "; break;
			case '6': factor=" <> "; break;
		}
		
		return factor;
	}
     
	public String formatDate(String date,String item,String factor) throws GeneralException{
		
		StringBuffer where = new StringBuffer();
		 Calendar now=Calendar.getInstance();
		 try{
				if("当年".equals(date)){
					int year = now.get(Calendar.YEAR);
					where.append(Sql_switcher.year(item)+factor+year);
				}else if("当月".equals(date)){
					int month = now.get(Calendar.MONTH)+1;
					where.append(Sql_switcher.month(item)+factor+month);
				}else if("当天".equals(date)){
					int day = now.get(Calendar.DATE);
					where.append(Sql_switcher.day(item)+factor+day);
				}else if("今天".equals(date)){
					Date nowdate = new Date();
					 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					where.append(Sql_switcher.year(item)+"*10000+"+Sql_switcher.month(item)+"*100+"+Sql_switcher.day(item)+factor+sdf.format(nowdate));
				}else if(date.indexOf("YRS") != -1){
					String YYYY = date.substring(5, date.length()-1);
					where.append( Sql_switcher.toInt(now.get(Calendar.YEAR)+"-"+Sql_switcher.year(item))+factor+YYYY);
				}else{
					if(date.indexOf(".")!=-1 || date.indexOf("-")!=-1){
						
						date = date.replace(".", "-");
						
						Integer.parseInt(date.replace("-", ""));
						
						String[] dvalue = date.split("-");
						for(int i=1;i<=dvalue.length;i++){
							if(i==1)
								where.append(Sql_switcher.year(item)+"*10000");
							else if(i==2)
								where.append("+"+Sql_switcher.month(item)+"*100");
							else if(i == 3)
								where.append("+"+Sql_switcher.day(item));
								
						}
						
						where.append(factor);
						
						int ynum = 0;
						for(int i=1;i<=dvalue.length;i++){
							if(i==1)
								ynum = Integer.parseInt(dvalue[i-1])*10000;
							else if(i==2)
								ynum+=Integer.parseInt(dvalue[i-1])*100;
							else if(i == 3)
								ynum+=Integer.parseInt(dvalue[i-1]);
								
						}
						
						where.append(ynum);
					}else{
						
						Integer.parseInt(date);
						where.append(Sql_switcher.year(item)+factor+date);
					}
				}
		
		 }catch(Exception e){
			 throw GeneralExceptionHandler.Handle(new GeneralException(""));
		 }
		
		return where.toString();
	}
}
