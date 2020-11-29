package com.hjsj.hrms.transaction.kq.register.history.app_check;

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

public class AppQueryTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
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
        
        ArrayList fieldlist = DataDictionary.getFieldList(table.substring(0,3),
				Constant.USED_FIELD_SET);
        SearchAllApp searchAllApp=new SearchAllApp();
		ArrayList fielditemlist= searchAllApp.getNewFiledList(fieldlist,table.substring(0,3).toLowerCase());
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr;		
		String wherestr="from "+table+" "+whereIN;
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
		String relatTableid = (String)this.getFormHM().get("relatTableid");
		this.getFormHM().put("sql_str",sqlstr);
		this.getFormHM().put("cond_str",wherestr);
		this.getFormHM().put("columns",columnstr);
		this.getFormHM().put("ordeby_s",ordeby);
		String frist = (String) hm.get("wo");
		this.getFormHM().put("returnURL","/kq/app_check_in/all_app_data.do?b_search=link&wo="+frist+"&table="+table);
		this.getFormHM().put("condition",SafeCode.encode((relatTableid+"`"+whereIN.substring(5))));
		this.getFormHM().put("fielditemlist", fielditemlist);
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
			if(i > 0){
				whereTrem.append(getOneTerm(factor,like,table));
			}else{
				whereTrem.append(getOneTerm(factor,like,table).replace("and", ""));
			}
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
        			if("+".equals(log))
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
				oneterm.append(" "+Sql_switcher.isnull(fieldname,"9999")+oper+"9999");
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
