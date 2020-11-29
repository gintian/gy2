package com.hjsj.hrms.utils;

import com.hrms.hjsj.utils.Sql_switcher;

public class TimeScope {
/**
 * 
 * @param fielditemid 查询的字符串
 * @param operate 操作符
 * @param value 转换的字符串
 * @return
 */
	public String getTimeConditon(String fielditemid,String operate,String value)
	{
		
		
		StringBuffer a_value=new StringBuffer("");	
		if(value.length()>0){
			String[] tempvalue=value.split("-");
			if(tempvalue.length==1){
				value=value+"-01-01";
			}
			if(tempvalue.length==2){
				if(tempvalue[1].length()==1){
					value=tempvalue[0]+"-0"+tempvalue[1]+"-01";
				}else{
					value=value+"-01";
				}
			}
			if(tempvalue.length==3){
				if(tempvalue[1].length()==1){
					tempvalue[1]="0"+tempvalue[1];
				}
				if(tempvalue[2].length()==1){
					tempvalue[2]="0"+tempvalue[2];
				}
				value=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
			}
		try
		{
				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{	if(">=".equals(operate)){
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+">="+value.substring(8));
						a_value.append(") ) ");
					}
					else if("<=".equals(operate)){
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+"<="+value.substring(8));
						a_value.append(") ) ");
					}else
					{
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
						a_value.append(") ) ");
							
					}
					
					
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		}
		return a_value.toString();
	}
	/**
	 * 
	 * @param fielditemid 查询的字符串
	 * @param operate 操作符
	 * @param value 转换的字符串
	 * @return
	 */
		public String getTimeCond(String fielditemid,String operate,String value)
		{
			
			
			StringBuffer a_value=new StringBuffer("");	
			if(value.length()>0){
				String[] tempvalue=value.split("-");
				if(tempvalue.length==2){
					if(tempvalue[1].length()==1){
						value=tempvalue[0]+"-0"+tempvalue[1];
					}
				}
				if(tempvalue.length==3){
					if(tempvalue[1].length()==1){
						tempvalue[1]="0"+tempvalue[1];
					}
					if(tempvalue[2].length()==1){
						tempvalue[2]="0"+tempvalue[2];
					}
					value=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
				}
			try
			{
					if("=".equals(operate))
					{
						a_value.append("(");
						if(value.length()>3)
							a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4));
						if(value.length()>6)
							a_value.append(" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7));
						if(value.length()>8)
							a_value.append(" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
						a_value.append(" ) ");
					}
					else 
					{	if(">=".equals(operate)){
							a_value.append("(");
							if(value.length()>3)
								a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4));
							if(value.length()>6)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+")");
							if(value.length()>8)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+">="+value.substring(8));
							a_value.append(") ) ");
						}
						else if("<=".equals(operate)){
							a_value.append("(");
							if(value.length()>3)
								a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
							if(value.length()>6)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) ");
							if(value.length()>8)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+"<="+value.substring(8));
							a_value.append(") ) ");
						}else
						{
							a_value.append("(");
							if(value.length()>3)
								a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
							if(value.length()>6)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" )");
							if(value.length()>8)
								a_value.append(" or ( "+Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
							a_value.append(") ) ");
								
						}
						
						
					}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			}
			return a_value.toString();
		}
	public String gettimeScope(String fielditemid,String startime,String endtime){
		String retstr="";
		
		if(startime!=null&&startime.length()>0&&endtime!=null&&endtime.length()>1){
			String start=this.getTimeConditon(fielditemid,">=",startime);
			String end=this.getTimeConditon(fielditemid,"<=",endtime);
			retstr=start+" and "+end;
		}else{
			if(startime==null||startime.length()<1){
				String end=this.getTimeConditon(fielditemid,"<=",endtime);
				retstr=end;
			}
			if(endtime==null||endtime.length()<1){
				String start=this.getTimeConditon(fielditemid,">=",startime);
				retstr=start;
			}
		}
		return retstr;
	}
}
