package com.hjsj.hrms.transaction.general.template.goabroad.collect.select;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectQueryTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist"); 
        String like=(String)this.getFormHM().get("like");
//        String hols_status=(String)this.getFormHM().get("hols_status");
        if(like==null||like.length()<=0)
        	like="";
        String whereIN=getWhereSql(factorlist,like);
       // System.out.println(hols_status+"`"+whereIN);
//        this.getFormHM().put("whereIN",hols_status+"`"+whereIN);
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        if(whereIN.startsWith(" and")){
        	whereIN = whereIN.substring(4);
        }
        hm.put("whereIN", whereIN);
//        this.getFormHM().put("whereIN",whereIN);
//        this.getFormHM().put("select_flag","1");
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like)
	{
		StringBuffer whereTrem=new StringBuffer();
		boolean isCorrect=false;//判断是否选择人员库
		for(int i=0;i<factorlist.size();i++)
		{
			Factor factor = (Factor)factorlist.get(i);
			if("nbase".equals(factor.getFieldname().trim()))
			{
				isCorrect=true;
			}
			whereTrem.append(getOneTerm(factor,like));	
			
		}
		/*if(!isCorrect)
		{
			String wherenabse=getNBase("nbase","all","","*");
			whereTrem.append(wherenabse);
		}*/
		return whereTrem.toString();
	}
	
	/**
	 * 得到一个条件的sql
	 * */
	public String getOneTerm(Factor factor,String like)
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
		//【57805】V771封版：出国管理/汇总统计，条件查询，姓名=李素娟，都查询不出来值了，不对。
		String oper=getSemiangleOper(factor.getOper());//关系符
		factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
		String log=factor.getLog();//逻辑值
		String field=factor.getFieldtype();
		if("nbase".equals(fieldname))
		{
			//oneterm.append(getNBase(fieldname,value,oper,log));
		}else if("D".equals(field))
		{			
			oneterm.append(getWorkDate(fieldname,value,oper,log));
			
		}else if(("0".equalsIgnoreCase(factor.getCodeid()))&& "1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
    			String logtag=" and ";
    			if("+".equals(log))
    			{
    				logtag=" or ";
    			}
    			oneterm.append(logtag);
        		oneterm.append(factor.getFieldname().toUpperCase());
        		if("<>".equalsIgnoreCase(oper)){
        			oneterm.append(" not like "); 
        		}else if("=".equalsIgnoreCase(oper)){
        			oneterm.append(" like "); 
        		}else{
        			oneterm.append(oper); 
        		}
         	    oneterm.append(" '%"+value+"%' ");        	    
         	    oneterm.append(" ");
        	}
        		          
        }else if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
    			String logtag=" and ";
    			if("+".equals(log))
    			{
    				logtag=" or ";
    			}
    			oneterm.append(logtag);
        		oneterm.append(factor.getFieldname().toUpperCase());
        		if("<>".equalsIgnoreCase(oper)){
        			oneterm.append(" not like "); 
        		}else if("=".equalsIgnoreCase(oper)){
        			oneterm.append(" like "); 
        		}else{
        			oneterm.append(oper); 
        		}
        	    oneterm.append(" '%"+value+"%' ");        	    
        	    oneterm.append(" ");
        	}
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
				//测试要求如果 value为null，等于0
//				oneterm.append(" "+Sql_switcher.isnull(fieldname,"9999")+"=9999");
				oneterm.append(" "+Sql_switcher.isnull(fieldname,"9999")+oper+"0");
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
			
			if("like".equals(oper.trim()))
			{
				oneterm.append(logtag);
				oneterm.append(fieldname);
				oneterm.append(" "+oper+" ");
				oneterm.append(" '%"+value+"%' ");
			}else
			{
				if (value != null && value.length() > 0) {
					oneterm.append(logtag);
					oneterm.append(fieldname);
					oneterm.append(oper);
					oneterm.append("'"+value+"'");
				} else {
					String str = this.getNullSql(fieldname, value, oper, log, logtag);
					oneterm.append(str);
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
	public String getNBase(String fieldname,String value,String oper,String log)
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
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				whereINList.add(whereA0100In);
			}
			 for(int i=0;i<whereINList.size();i++)
			   {   
				   if(i>0)
				   {
					   where.append(" or ");  
				   }else
				   {
					   where.append(" "+logtag+" ( ");    
				   }
				   where.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+") "); 
				   if(i==whereINList.size()-1)
					   where.append(")");  
			   }
		}else
		{
			String wherenabse=RegisterInitInfoData.getWhereINSql(this.userView,value);
			where.append(" "+logtag+" a0100 in(select a0100 "+wherenabse+")");
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
		if (value != null && value.length() > 0) {
			String date_vale=Sql_switcher.dateValue(value);		
			where.append(logtag+""+fieldname+" "+oper+" "+date_vale+""); 
		} else {
			String date_vale=Sql_switcher.dateValue(value);
			String strsql = getNullSql(fieldname,date_vale,oper,log,logtag);
			where.append(strsql);
		}
		return where.toString();
	}
	
	/**
	 * 获得空的sql语句
	 * @param fieldname
	 * @param value
	 * @param oper
	 * @param log
	 * @param logtag
	 * @return
	 */
	public String getNullSql (String fieldname,String value,String oper,String log, String logtag) {
		StringBuffer where = new StringBuffer();
		int dbflag = Sql_switcher.searchDbServer();
		switch (dbflag) {
			case 2:
				if ("=".equalsIgnoreCase(oper)) {
					where.append(logtag);
					where.append("(");
					where.append(fieldname);
					where.append("='' or ");
					where.append(fieldname);
					where.append(" is null )");
				} else if ("<>".equalsIgnoreCase(oper)){
					where.append(logtag);
					where.append("(");
					where.append(fieldname);
					where.append("<>'' or ");
					where.append(fieldname);
					where.append(" is not null )");
				} else {
						
					where.append(logtag+""+fieldname+" "+oper+" ''");
				}
				break;
			default:
				where.append(logtag+""+fieldname+" "+oper+" ''");
				break;
		}
		
		return where.toString();
	}
	private String getSemiangleOper(String oper) {
		//＜＞＝
        if ("＝".equals(oper)) {
        	oper = "=";
		}
        if ("＞".equals(oper)) {
        	oper = ">";
		}
        if ("＜".equals(oper)) {
        	oper = "<";
		}
        if ("＞＝".equals(oper)) {
        	oper = ">=";
		}
        if ("＜＝".equals(oper)) {
        	oper = "<=";
      	}
        if ("＜＞".equals(oper)) {
        	oper = "<>";
      	}
		return oper;
		
	}
}
