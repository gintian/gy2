package com.hjsj.hrms.transaction.kq.app_check_in.select;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AppQueryTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{

		ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");        
        String kind=(String)this.getFormHM().get("kind");
        String code=(String)this.getFormHM().get("code");
        String table = (String)this.getFormHM().get("table");
        String history=(String)this.getFormHM().get("history");
        String like=(String)this.getFormHM().get("like");
        if(history==null|| "".equals(history))
            history="0";
        if(like==null|| "".equals(like))
            like="0";
        //Q15 条件查询 需要用Q1517 0是原始数据，1是销假；这里需要增加一个查询条件不查询出销假信息 wy
        //String whereIN=getWhereSql(factorlist,like);
        String whereIN=getWhereSql(factorlist,like,table);
        ArrayList fieldlist = DataDictionary.getFieldList(table,
				Constant.USED_FIELD_SET);
        SearchAllApp searchAllApp=new SearchAllApp();
      //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");
		String cardno = (String) hashmap.get("cardno");
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		ArrayList fielditemlist= searchAllApp.getNewFiledList(fieldlist,table.toLowerCase());
		fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101","Q03",g_no,cardno,fielditemlist);
		StringBuffer column=new StringBuffer();
		StringBuffer jionColumn=new StringBuffer();
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);	
			String item = fielditem.getItemid();
			if(column.length() > 0){
				column.append("," + item);
				if(item.equalsIgnoreCase(g_no) || item.equalsIgnoreCase(cardno)){
					jionColumn.append(",A." + item);
				}else{
					jionColumn.append("," + item);
				}
				
			}else{
				column.append(item);
				if(item.equalsIgnoreCase(g_no) || item.equalsIgnoreCase(cardno)){
					jionColumn.append("A." + item);
				}else{
					jionColumn.append("" + item);
				}
			}
		}
		String columnstr=column.toString();
		String sqlstr="select "+columnstr;		
//		String wherestr="from "+table+" "+whereIN;
		StringBuffer join = new StringBuffer();
		
		String nbases = this.userView.getDbpriv().toString();
		
		if(this.userView.isSuper_admin()){
			ArrayList list = DataDictionary.getDbpreList();
			for(Iterator it = list.iterator();it.hasNext();){
				String nbase = (String)it.next();
				if(join.length() > 0){
					join.append(" UNION SELECT A0100 cu_a0100,'" + nbase + "' cu_nbase," + g_no + "," + cardno + " FROM " + nbase + "A01");
				} else {
					join.append("SELECT A0100 cu_a0100,'" + nbase + "' cu_nbase," + g_no + "," + cardno + " FROM " + nbase + "A01");
				}
			}
		}else if(nbases.length() > 0){
			String nbase[] = nbases.split(",");
			for(int i=0;i<nbase.length;i++){
				if(join.length() > 0 && nbase[i].length() > 0){
					join.append(" UNION SELECT A0100 cu_a0100,'" + nbase[i] + "' cu_nbase," + g_no + "," + cardno + " FROM " + nbase[i] + "A01");
				} else if(nbase[i].length() > 0){
					join.append("SELECT A0100 cu_a0100,'" + nbase[i] + "' cu_nbase," + g_no + "," + cardno + " FROM " + nbase[i] + "A01");
				}
			}
		}else{
			return;
		}
		
		String wherestr = "FROM (SELECT " + jionColumn + " FROM " + table + " Q INNER JOIN (" + join + ") A ON A0100=cu_a0100 and nbase=cu_nbase) " + table + " " + whereIN;
		if(code!=null&&code.length()>0)
		{
			if("1".equals(kind))
			{
				wherestr=wherestr+" and e0122 like '"+code+"%'";
			}else if("0".equals(kind))
			{
				wherestr=wherestr+" and e01a1 like '"+code+"%'";
			}else
			{
				wherestr=wherestr+" and b0110 like '"+code+"%'";	
			}
		}
		
		String ordeby=" order by b0110,e0122,e01a1";		
		this.getFormHM().put("sql_str",sqlstr);
		this.getFormHM().put("cond_str",wherestr);
		this.getFormHM().put("columns",columnstr);
		this.getFormHM().put("ordeby_s",ordeby);
		this.getFormHM().put("fielditemlist", fielditemlist);
		this.getFormHM().put("condition",SafeCode.encode((table.substring(1)+"`"+wherestr.substring(wherestr.indexOf("where ")+5))));//调用花名册用到条件 liwc
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like,String table)
	{
		StringBuffer whereTrem=new StringBuffer();
		whereTrem.append("where 1=1 AND (");
		boolean isCorrect=false;//判断是否选择人员库
		for(int i=0;i<factorlist.size();i++)
		{
			Factor factor = (Factor)factorlist.get(i);
			if("nbase".equals(factor.getFieldname().trim()))
			{
				isCorrect=true;
			}
			//如果是Q15 就增加条件 Q1517=0； wy
			//whereTrem.append(getOneTerm(factor,like));
			String oneterm = getOneTerm(factor,like,table);
			if(i == 0){
				oneterm = oneterm.replaceFirst("and", "");
			}
			whereTrem.append(oneterm);
			
		}
		whereTrem.append(")");
		if(!isCorrect)
		{
//			String wherenabse=getNBase("nbase","all","","*");
			String wherenabse=getNBase("nbase","all","","*",table);
			whereTrem.append(wherenabse);
		}
		return whereTrem.toString();
	}
	/**
	 * 得到一个条件的sql
	 * */
	public String getOneTerm(Factor factor,String like,String table)
	{
		StringBuffer oneterm = new StringBuffer();
		String fieldname=factor.getFieldname();//字段名字
		String value=factor.getValue();//值
		if(value==null||value.length()<=0)
		{
			value="";
		}else if("空".equals(value))
		{
			value="";
		}
		
		String oper=PubFunc.keyWord_reback(factor.getOper());//关系符
		String log=PubFunc.keyWord_reback(factor.getLog());//逻辑值
		String field=factor.getFieldtype();
		String codeid=factor.getCodeid();
		codeid=codeid!=null&&codeid.length()>0?codeid:"0";
		if("nbase".equals(fieldname))
		{
//			oneterm.append(getNBase(fieldname,value,oper,log));
			oneterm.append(getNBase(fieldname,value,oper,log,table));
		}else if(("0".equalsIgnoreCase(codeid))&& "1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
        		//增加如果table=Q15 就Q1517=0只要原始数据；
        		if("Q15".equalsIgnoreCase(table))
        		{
        			value=value.replaceAll(" ", "%");
            		value=value.replaceAll("　", "%");
        			String logtag=" and ";
        			if("+".equals(oper))
        			{
        				logtag=" or ";
        			}
        			oneterm.append(logtag);
            		oneterm.append(factor.getFieldname().toUpperCase());
            		oneterm.append(" like "); 
             	    oneterm.append(" '%"+value+"%' and Q1517='0'");        	    
             	    oneterm.append(" ");
        		}else
        		{
        			value=value.replaceAll(" ", "%");
            		value=value.replaceAll("　", "%");
        			String logtag=" and ";
        			if("+".equals(log))
        			{
        				logtag=" or ";
        			}
        			oneterm.append(logtag);
            		oneterm.append(factor.getFieldname().toUpperCase());
            		oneterm.append(" like "); 
             	    oneterm.append(" '%"+value+"%' ");        	    
             	    oneterm.append(" ");
        		}
        	}
        		          
        }else if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
        		value=value.replaceAll(" ", "%");
        		value=value.replaceAll("　", "%");
    			String logtag=" and ";
    			if("+".equals(log))
    			{
    				logtag=" or ";
    			}
    			oneterm.append(logtag);
        		oneterm.append(factor.getFieldname().toUpperCase());
        	    oneterm.append(" like "); 
        	    oneterm.append(" '%"+value+"%' ");        	    
        	    oneterm.append(" ");
        	}
        }else if("D".equals(field))
		{
			oneterm.append(getWorkDate(fieldname,value,oper,log));
		}else if("N".equals(factor.getFieldtype()))
		{
			String logtag=" and ";
			if("+".equals(log))
			{
				logtag=" or ";
			}
			oneterm.append(logtag);
			//oneterm.append(fieldname);
			//oneterm.append(oper);			
			if(value==null||value.length()<=0)
			{
				oneterm.append(" "+Sql_switcher.isnull(fieldname,"9999")+"=9999");
			}
			else
			{
				oneterm.append(fieldname);
				oneterm.append(oper);
				oneterm.append(value);
			}
		}else
		{
			String logtag=" and ";
			if("+".equals(log))
			{
				logtag=" or ";
			}
			oneterm.append(logtag);
			oneterm.append(fieldname);
			if("like".equals(oper.trim()))
			{
				oneterm.append(" "+oper+" ");
				oneterm.append(" '%"+value+"%' ");
			}else
			{
				if("".equals(value))
				{
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						if("<>".equals(oper))
						{
							oneterm.append(" is not null");
						}else if("=".equals(oper))
						{
							oneterm.append(" is null");
						}else
						{
							oneterm.append(oper);
							oneterm.append("'"+value+"'");
						}
					}else
					{
						oneterm.append(oper);
						oneterm.append("'"+value+"'");
					}
				}else
				{
					oneterm.append(oper);
					oneterm.append("'"+value+"'");
				}
				
			}
						
		}
		return oneterm.toString();
	} 
	/**
	 * 处理操作人员库，和当前权限
	 * @param fieldname  字段名字
	 * @param value 值
	 * @param oper 关系符
	 * @param log 逻辑值
	 * */
//	public String getNBase(String fieldname,String value,String oper,String log)
	public String getNBase(String fieldname,String value,String oper,String log,String table)
	{
		StringBuffer where = new StringBuffer();
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		if("all".equals(value))
		{
			ArrayList kq_dbase_list=this.userView.getPrivDbList();
			ArrayList whereINList= new ArrayList();
			
			ArrayList dbList = new ArrayList();
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				whereINList.add(whereA0100In);
				
				dbList.add(dbase);
			}
			 for(int i=0;i<whereINList.size();i++)
			   {   String db = (String) dbList.get(i);
				   if(i>0)
				   {
					   where.append(" or ");  
				   }else
				   {
					   where.append(" "+logtag+" ( ");    
				   }
//				   where.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+") "); 
				   where.append("  exists (select 1 from (select '"+db+"' nbase, a0100 "+whereINList.get(i).toString()+" ) b where b.a0100="+table+".a0100 and upper(b.nbase)=upper("+table+".nbase)) "); 
				   if(i==whereINList.size()-1)
					   where.append(")");  
			   }
		}else
		{
			String wherenabse=RegisterInitInfoData.getWhereINSql(this.userView,value);
			if("<>".equals(oper))
			{
				ArrayList kq_dbase_list=this.userView.getPrivDbList();	
				int i=0;
				for(;i<kq_dbase_list.size();i++)
				{
					String dbase=kq_dbase_list.get(i).toString();
					if(dbase.equalsIgnoreCase(value))
						continue;
					String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
					   if(i>0)
					   {
						   where.append(" or ");  
					   }else
					   {
						   where.append(" "+logtag+" ( ");    
					   }
//					   where.append("(nbase='"+dbase+"' and a0100 in(select a0100 "+whereA0100In+")) "); 
					   where.append("(nbase='"+dbase+"' and exists (select 1 from (select '"+dbase+"' nbase, a0100 "+whereA0100In+") b where b.a0100="+table+".a0100 and upper(b.nbase)=upper("+table+".nbase))) ");
					  
				}
				if(i>0)
					where.append(")");  
			}else
			{
//				where.append(" "+logtag+" (nbase='"+value+"' and a0100 in(select a0100 "+wherenabse+"))");
				where.append(" "+logtag+" (nbase='"+value+"' and exists (select 1 from (select '"+value+"' nbase, a0100 "+wherenabse+") b where b.a0100="+table+".a0100 and upper(b.nbase)=upper("+table+".nbase)))");
			} 
			
			
		}
		return where.toString();
	}
	/**处理日期 
	 * @param fieldname  字段名字
	 * @param value 值
	 * @param oper 关系符
	 * @param log 逻辑值
	 * */
	public String  getWorkDate(String fieldname,String value,String oper,String log)
	{
		StringBuffer where = new StringBuffer();
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		String date_vale=Sql_switcher.dateValue(value);		
		where.append(logtag+""+fieldname+" "+oper+" "+date_vale+"");
		return where.toString();
	}
}
