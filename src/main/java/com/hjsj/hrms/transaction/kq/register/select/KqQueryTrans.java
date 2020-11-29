package com.hjsj.hrms.transaction.kq.register.select;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
/**
 * 通过查询条件，组合查询语句
 * 
 * **/
public class KqQueryTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");        
        String kind=(String)this.getFormHM().get("kind");
        String code=(String)this.getFormHM().get("code");
        String like=(String)this.getFormHM().get("like");
        if(like==null||like.length()<=0)
        	like="";
        String whereIN=getWhereSql(factorlist,like);    
        //System.out.println(whereIN);
        ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlist= newFieldItemList(fieldlist);
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);			
			   column.append(fielditem.getItemid()+",");
		}
		int l=column.toString().length()-1;
		String columnstr=column.toString().substring(0,l);
		String sqlstr="select "+columnstr;		
		String wherestr="from q03 "+whereIN;
		if(code!=null&&code.length()>0)
		{
			if("1".equals(kind))
			{
				wherestr=wherestr+" and e0122 like '"+code+"%'";
			}else if("0".equals(kind))
			{
				wherestr=wherestr+" and e01a1 like '"+code+"%'";
			}
			{
				wherestr=wherestr+" and b0110 like '"+code+"%'";	
			}
		}
		ArrayList kq_daylist=RegisterDate.getKqDurationList(this.frameconn);	
		String kqstart=	kq_daylist.get(0).toString();
		String kqend=kq_daylist.get(kq_daylist.size()-1).toString();	
		wherestr=wherestr+" and q03z0>='"+kqstart+"'";
		wherestr=wherestr+" and q03z0<='"+kqend+"'";
		String ordeby=" order by q03z5 DESC,b0110,e0122,e01a1,q03z0";		
		this.getFormHM().put("sqlstr_s",sqlstr);
		this.getFormHM().put("wherestr_s",wherestr);
		this.getFormHM().put("columnstr_s",columnstr);
		this.getFormHM().put("ordeby_s",ordeby);
		this.getFormHM().put("fielditemlist", fielditemlist);
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like)
	{
		StringBuffer whereTrem=new StringBuffer();
		whereTrem.append("where 1=1");
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
		if(!isCorrect)
		{
			String wherenabse=getNBase("nbase","all","","*");
			whereTrem.append(wherenabse);
		}
		return whereTrem.toString();
	}
	/**
	 * 得到一个条件的sql
	 * */
	public String getOneTerm(Factor factor,String like)
	{
		StringBuffer oneterm = new StringBuffer();
		String fieldname=factor.getFieldname();//字段名字
		String value=factor.getValue().trim();//值
		if(value==null||value.length()<=0)
		{
			value="";
		}else if("空".equals(value))
		{
			value="";
		}
		if(value!=null&&value.length()>0)
			value=PubFunc.getStr(value);
		String oper=factor.getOper();//关系符
		factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
		String log=factor.getLog();//逻辑值
		oper=PubFunc.keyWord_reback(oper);
		if("nbase".equals(fieldname))
		{
			oneterm.append(getNBase(fieldname,value,oper,log));
		}else if("q03z0".equals(fieldname))
		{
			oneterm.append(getWorkDate(fieldname,value,oper,log));
		}else if(("0".equalsIgnoreCase(factor.getCodeid()))&& "1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
        		value=value.replaceAll(" ", "%");
        		value=value.replaceAll("　", "%");
        		oneterm.append(" and ");
        		oneterm.append(factor.getFieldname().toUpperCase());
        		oneterm.append(" like "); 
         	    oneterm.append(" '%"+value+"%' ");        	    
         	    oneterm.append(" ");
        	}
        		          
        }else if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
        {
        	if(!(value==null|| "".equals(value)))
        	{
        		value=value.replaceAll(" ", "%");
        		value=value.replaceAll("　", "%");
        		oneterm.append(" and ");
        		oneterm.append(factor.getFieldname().toUpperCase());
        	    oneterm.append(" like "); 
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
				oneterm.append(oper);
				oneterm.append("'"+value+"'");
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
		if(value==null||value.length()<=0)
			return "";
		StringBuffer where = new StringBuffer();
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		String q03z0=value.replaceAll("-","\\.");
		Date d=DateUtils.getDate(q03z0,"yyyy.MM.dd");
		q03z0=DateUtils.format(d,"yyyy.MM.dd");
		if("like".equals(oper.trim()))
		{
			where.append(logtag+""+fieldname+" "+oper+ "'%"+q03z0+"%'");
		}else
		{
			where.append(logtag+""+fieldname+" "+oper+"'"+q03z0+"'");
		}
		
		return where.toString();
	}
	/**姓名
	 * @param fieldname  字段名字
	 * @param value 值
	 * @param oper 关系符
	 * @param log 逻辑值
	 * */
	public String  getA0101(String fieldname,String value,String oper,String log)
	{
		StringBuffer where = new StringBuffer();
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		String q03z0=value.replaceAll("-","\\.");
		Date d=DateUtils.getDate(q03z0,"yyyy.MM.dd");
		q03z0=DateUtils.format(d,"yyyy.MM.dd");
		where.append(logtag+""+fieldname+""+oper+"'"+q03z0+"'");
		return where.toString();
	}
	 public static ArrayList newFieldItemList(ArrayList fielditemlist)
	    {
	    	ArrayList list=new ArrayList();
	    	FieldItem fielditem_c=new FieldItem();
	    	for(int i=0;i<fielditemlist.size();i++){
				FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid()))
				{
					if("a0100".equals(fielditem.getItemid()))
					{
						fielditem.setVisible(false);
					}else
					{
						//fielditem.setVisible(true);
						if("1".equals(fielditem.getState())|| "q03z0".equals(fielditem.getItemid()))
						{
							
							fielditem.setVisible(true);
						}else
						{
							fielditem.setVisible(false);
						}
					}
					if("q03z5".equals(fielditem.getItemid()))
					{
						fielditem_c=fielditem;
					}else
					{
						list.add(fielditem);
					}				
				}
			}
	    	list.add(fielditem_c);
	    	return list;
	    }
}
