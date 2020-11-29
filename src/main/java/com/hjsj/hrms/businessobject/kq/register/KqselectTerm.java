package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.Date;

public class KqselectTerm {

	private UserView userView;
	public KqselectTerm(UserView userView)
	{
		this.userView=userView;
	}
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like,String nbase)
	{
		StringBuffer whereTrem=new StringBuffer();
//		whereTrem.append(" 1=1");
		whereTrem.append(" ( 1=1");
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
		
		// 给条件加括号，
		whereTrem.append(" )");
		if(!isCorrect)
		{
			//不加权限了，权限在外面控制
			/*String wherenabse=getNBase("nbase",nbase,"","*");
			whereTrem.append(wherenabse);*/
		}
		return whereTrem.toString();
	}
	/**
	 * 得到一个条件的sql
	 * */
	private String getOneTerm(Factor factor,String like)
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
		if(value!=null&&value.length()>0) {
            value=PubFunc.getStr(value);
        }
		factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
		String oper=factor.getOper();//关系符
		oper=PubFunc.keyWord_reback(oper);
		factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
		String log=factor.getLog();//逻辑值
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
	private String getNBase(String fieldname,String nbase,String oper,String log)
	{
		StringBuffer where = new StringBuffer();
		String logtag=" and ";
		if("+".equals(log))
		{
			logtag=" or ";
		}
		if("all".equalsIgnoreCase(nbase))
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
				   // ------获得考勤部门的人员------start
				   //where.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+") "); 
				   where.append("  a0100 in(select a0100 "+whereINList.get(i).toString()+ RegisterInitInfoData.getUnionSql(userView, nbase)+") "); 
				   // ------获得考勤部门的人员------end
				   if(i==whereINList.size()-1) {
                       where.append(")");
                   }
			   }
		}else
		{
			String wherenabse=RegisterInitInfoData.getWhereINSql(this.userView,nbase);
			// ------获得考勤部门的人员------start
			// where.append(" "+logtag+" a0100 in(select a0100 "+wherenabse+")");
			where.append(" "+logtag+" a0100 in(select a0100 "+wherenabse+RegisterInitInfoData.getUnionSql(userView, nbase)+")");
			// ------获得考勤部门的人员------end
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
		if(value==null||value.length()<=0) {
            return "";
        }
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
}
