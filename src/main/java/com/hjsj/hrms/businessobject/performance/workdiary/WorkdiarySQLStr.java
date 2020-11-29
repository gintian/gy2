package com.hjsj.hrms.businessobject.performance.workdiary;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkdiarySQLStr {
	public String[] getMyworkdiaryshow(String userid,ArrayList fieldlist,String startime,
			String endtime,String state){
		String[] sql=new String[4];
		String where="";
		if((startime==null||startime.length()<1)&&(endtime==null||endtime.length()<1)){
			where="from P01  where state="+state+" and a0100='"+userid+"'";
		}else{
			if(startime==null||startime.length()<1){
				String temwhere=this.getDataValue("p0104","<=",endtime);
				
				where="from P01  where state="+state+" and a0100='"+userid+"' and "+temwhere;
				
			}
			if(endtime==null||endtime.length()<1){
				String temwhere=this.getDataValue("p0104",">=",startime);
				where="from P01  where state="+state+" and a0100='"+userid+"' and "+temwhere;
			}
			if((startime!=null&&startime.length()>1)&&(endtime!=null&&endtime.length()>1)){
				//String st=this.getDataValue("p0104",">=",startime);
				//String et=this.getDataValue("p0104","<=",endtime);
				where="from P01  where state="+state+" and a0100='"+userid+"' and p0104 between " 
							+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime);
			}
		}
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			if("M".equalsIgnoreCase(field.getItemtype())){
				column.append(field.getItemid()+",");
			}else{
				column.append(field.getItemid()+",");
			}
		}
		column.append("state");
		
		String select="select "+column.toString();
		String orderby="order by P0104 desc";//xuj 2009-11-2	员工日志按工作时间（即“起始时间”）正序排列
		sql[0]=select;
		sql[1]=where;
		sql[2]=column.toString();
		sql[3]=orderby;
		return sql;
	}
	
	public String getMyworkdiaryshow1(String userid,ArrayList fieldlist,String startime,
			String endtime,String state){
		String where="";
		if((startime==null||startime.length()<1)&&(endtime==null||endtime.length()<1)){
			where="from P01  where state="+state+" and a0100='"+userid+"'";
		}else{
			if(startime==null||startime.length()<1){
				String temwhere=this.getDataValue("p0104","<=",endtime);
				
				where="from P01  where state="+state+" and a0100='"+userid+"' and "+temwhere;
				
			}
			if(endtime==null||endtime.length()<1){
				String temwhere=this.getDataValue("p0104",">=",startime);
				where="from P01  where state="+state+" and a0100='"+userid+"' and "+temwhere;
			}
			if((startime!=null&&startime.length()>1)&&(endtime!=null&&endtime.length()>1)){
				//String st=this.getDataValue("p0104",">=",startime);
				//String et=this.getDataValue("p0104","<=",endtime);
				where="from P01  where state="+state+" and a0100='"+userid+"' and p0104 between " 
							+Sql_switcher.dateValue(startime)+ " and " +Sql_switcher.dateValue(endtime);
			}
		}
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			if("M".equalsIgnoreCase(field.getItemtype())){
				column.append(field.getItemid()+",");
			}else{
				column.append(field.getItemid()+",");
			}
		}
		
		String select="select "+column.substring(0, column.length()-1);
		String orderby="order by P0104 desc";
//		sql[0]=select;
//		sql[1]=where;
//		sql[2]=column.toString();
//		sql[3]=orderby;
		return select +" "+where+" "+orderby;
	}
	
	public ArrayList fieldList(){
		ArrayList fieldlist1 = new ArrayList();
		ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		StringBuffer buf=new StringBuffer();
		buf.append("p0100");
		FieldItem items = new FieldItem("P01","");
		items.setItemid("stateflag");
		items.setItemdesc("方式");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist1.add(items);
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(buf.indexOf(field.getItemid().toLowerCase())!=-1)
			{
				continue;
			}
			if("nbase".equalsIgnoreCase(field.getItemid())) {
                field.setCodesetid("@@");
            }
			if(field.isVisible()) {
                fieldlist1.add(field);
            }
		}
		return fieldlist1;
	}
	public String[] getMyworkdiaryshow(String userid,ArrayList fieldlist,String startime,String state){
		String[] sql=new String[4];
		String where="";
		String temwhere=this.getDataValue("p0104","=",startime);
		where="from P01  where state="+state+" and  a0100='"+userid+"' and "+temwhere;
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			column.append(field.getItemid()+",");
		}
		column.append("state");
		
		String select="select "+column.toString();
		String orderby="order by p0104";
		sql[0]=select;
		sql[1]=where;
		sql[2]=column.toString();
		sql[3]=orderby;
		return sql;
	}
	public String[] getWorkdiaryshow(UserView uv,String fw,ArrayList fieldlist,String startime,
			String endtime,String ymd,String p0115,String colum,String name,String namevalue,String a0100,String start_date,String end_date){
		String[] sql=new String[4];
		StringBuffer ljoin=new StringBuffer();
		ljoin.append(" p  ");
		String nw="";
		if(uv.getManagePrivCode()!=null&&uv.getManagePrivCode().length()>0&&"".equals(a0100)) {
            nw=" a0100 in("+ this.getdbtable(uv)+")";
        }
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(i==0){
					column.append(field.getItemid());				
			}else{				
					column.append(","+field.getItemid());		
			}
		}
		if(fw==null||fw.trim().length()<1){
			fw = "UN";//uv.getManagePrivCode()+uv.getManagePrivCodeValue();
		}
		column.append(",state");
		String select="select "+column.toString();
		select+=",case when state='0' then '日报' when state='1' then '周报' ";
		select+="when state='2' then '月报' else '' end as stateflag";
		StringBuffer where=new StringBuffer();
		String orderby="order by a0100,p0104 desc";//xuj 2009-11-2	员工日志按工作时间（即“起始时间”）正序排列

		if(fw==null||fw.length()<1|| "UN".equalsIgnoreCase(fw)){
//			查询某个单位所有的日志
			if(startime==null||startime.length()<1){
				
				if(endtime==null||endtime.length()<1){
					if("".equals(a0100)){
						where.append(" from p01 p where (");
						if(nw!=null&&nw.length()>0) {
                            where.append(nw+" or");
                        }
						where.append(getRestrict(uv)+")");
					}else {
                        where.append(" from p01 p where 1=1 ");
                    }
				}else{
					String temwhere=this.getDataValue("p0104","<=",endtime);
					if("".equals(a0100)){
						where.append("from p01 p where (");
						if(nw!=null&&nw.length()>0) {
                            where.append(nw+" or");
                        }
						where.append(getRestrict(uv)+")");
						where.append(" and "+temwhere);
					}else {
                        where.append("from p01 p where 1=1 "+temwhere);
                    }
				}
				
			}else {
				if (endtime!=null&&endtime.length()>1){
					if("".equals(a0100)){
						where.append("from p01 p where (");
						if(nw!=null&&nw.length()>0) {
                            where.append(nw+" or");
                        }
						where.append(getRestrict(uv)+")");
					}else {
                        where.append("from p01 p where 1=1 ");
                    }
					where.append("and p0104 between "+Sql_switcher.dateValue(startime));
					where.append("and "+Sql_switcher.dateValue(endtime));
				}else{
					String temwhere=this.getDataValue("p0104",">=",startime);
					if("".equals(a0100)){
						where.append("from p01 p where (");
						if(nw!=null&&nw.length()>0) {
                            where.append(nw+" or");
                        }
						where.append(getRestrict(uv)+")");
						where.append(" and "+temwhere);
					}else {
                        where.append("from p01 p where 1=1 "+temwhere);
                    }
				}
			}
			where.append(" and p0115 is not null and p0115<>'01' ");
		}else{
			if(fw.startsWith("UN")){
//				查询某个岗位的说有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
				
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.b0110 like '");
				where.append(fw.substring(2,fw.length()));
				where.append("%'");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and "+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
					
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
			else if (fw.startsWith("UM")){
//				查询某个岗位的说有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
				
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.e0122 like '");
				where.append(fw.substring(2,fw.length()));
				where.append("%'");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and "+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
					
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
			else if(fw.startsWith("@K")){
	//			查询某个岗位的说有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
				
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.e01a1 like '");
				where.append(fw.substring(2,fw.length()));
				where.append("%'");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and "+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
				
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
			else{		//查询所有认证库的人员日志 author:zangxj  day:2014-06-11 if(fw.startsWith("Usr"))  
	//			查询某个人的所有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
	
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.a0100 like '");
				where.append(fw.substring(3,fw.length()));
				where.append("%'");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and "+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
				
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
		}
		where.append(" and p.state="+ymd);
		
		
		if(!"all".equalsIgnoreCase(p0115)) {
            where.append(" and p.p0115='"+p0115+"'");
        }
		for(int i=0;i<fieldlist.size();i++){//添加点击条件查询按钮的查询条件 xuj 2009-11-3
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(field.getItemid().equalsIgnoreCase(colum)){
				if("D".equalsIgnoreCase(field.getItemtype())){
					if(name!=null&&name.length()>0)	//liwc 查询开始时间为空 不加此约束
                    {
                        where.append(" and p."+colum+" >= "+Sql_switcher.dateValue(name));
                    }
					if(namevalue!=null&&namevalue.length()>0) {
                        where.append(" and "+"p."+colum+" <= "+Sql_switcher.dateValue(namevalue));
                    }
				}else if("A".equalsIgnoreCase(field.getItemtype())|| "M".equalsIgnoreCase(field.getItemtype())){
					if("A".equalsIgnoreCase(field.getItemtype())&&field.getCodesetid()!=null&&field.getCodesetid().trim().length()>0&&!"0".equals(field.getCodesetid())){
						if(namevalue!=null&&namevalue.length()>0) {
                            where.append(" and p."+colum+" = '"+namevalue+"'");
                        }
					}else if("nbase".equalsIgnoreCase(colum)){
						if(name!=null&&name.length()>0) {
                            where.append(" and p."+colum+" = '"+name+"'");
                        }
					}else{
						if (name == null || "".equals(name)) { // oracle中''和null等价 lium
							where.append(" and (p."+colum+" like '%"+name+"%' OR p.").append(colum).append(" IS NULL)");
						} else {
							where.append(" and p."+colum+" like '%"+name+"%'");
						}
					}
				}else if("N".equalsIgnoreCase(field.getItemtype())){
					if(name!=null&&name.length()>0)	//liwc 数字类型(如：计划用时或实际用时)如果为空 不添加约束条件
                    {
                        where.append(" and p."+colum+" = "+name+"");
                    }
				}
			}
		}
		
		if(a0100!=null&&a0100.length()>3){
			String nbase=a0100.substring(0,3);
			a0100=a0100.substring(3);
			where.append(" and p.a0100='"+a0100+"'");
			where.append(" and p.nbase='"+nbase+"'");
			if(!"1".equals(ymd))//不等于周报的时候
			{
				if(start_date!=null&&start_date.length()>1) {
                    where.append(" and "+this.getDataValue("p0104",">=",start_date));
                }
				if(end_date!=null&&end_date.length()>1) {
                    where.append(" and "+this.getDataValue("p0106","<=",end_date));
                }
			}else{
				WeekUtils wu = new WeekUtils();
				if(start_date!=null&&start_date.length()>1) {
                    where.append(" and "+this.getDataValue("p0106",">=",wu.dateTostr(DateUtils.addDays(wu.strTodate(start_date), 1))));
                }
				if(end_date!=null&&end_date.length()>1) {
                    where.append(" and "+this.getDataValue("p0106","<=",wu.dateTostr(DateUtils.addDays(wu.strTodate(end_date), 1))));
                }
			}
			
		}		
		where.append(" and A0100<>'"+uv.getA0100()+"'");//20151226 wangjl 员工日志审批是不能看到自己
		sql[0]=select;
		sql[1]=where.toString();
		sql[2]=column.toString();
		sql[3]=orderby;
		return sql;
	}
	public String[] getWorkdiaryshow1(UserView uv,String fw,ArrayList fieldlist,String startime,
			String endtime,String ymd,String p0115,String colum,String name,String namevalue){
		String[] sql=new String[4];
		StringBuffer ljoin=new StringBuffer();
		ljoin.append(" p  ");
		String nw="";
		if(uv.getManagePrivCode()!=null&&uv.getManagePrivCode().length()>0) {
            nw=" a0100 in("+ this.getdbtable(uv)+")";
        }
		StringBuffer column=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(i==0){
					column.append(field.getItemid());				
			}else{				
					column.append(","+field.getItemid());		
			}
		}
		if(fw==null||fw.trim().length()<1){
			fw = uv.getManagePrivCode()+uv.getManagePrivCodeValue();
		}
		column.append(",state");
		String select="select "+column.toString();
		select+=",case when state='0' then '日报' when state='1' then '周报' ";
		select+="when state='2' then '月报' else '' end as stateflag";
		StringBuffer where=new StringBuffer();
		String orderby="order by a0100,p0104 desc";//xuj 2009-11-2	员工日志按工作时间（即“起始时间”）正序排列

		if(fw==null||fw.length()<1){
//			查询某个单位所有的日志
			if(startime==null||startime.length()<1){
				
				if(endtime==null||endtime.length()<1){
					where.append(" from p01 p where ");
					if(nw!=null&&nw.length()>0) {
                        where.append(nw);
                    }
				}else{
					String temwhere=this.getDataValue("p0104","<=",endtime);
					where.append("from p01 p where ");
					if(nw!=null&&nw.length()>0) {
                        where.append(nw);
                    }
					where.append(" and "+temwhere);
				}
				
			}else {
				if (endtime!=null&&endtime.length()>1){
					where.append("from p01 p where ");
					if(nw!=null&&nw.length()>0) {
                        where.append(nw);
                    }
					where.append("and p0104 between "+Sql_switcher.dateValue(startime));
					where.append("and "+Sql_switcher.dateValue(endtime));
				}else{
					String temwhere=this.getDataValue("p0104",">=",startime);
					where.append("from p01 p where ");
					if(nw!=null&&nw.length()>0) {
                        where.append(nw);
                    }
					where.append(" and "+temwhere);
				}
			}
			where.append(" and p0115 is not null and p0115<>'01' ");
		}else{
			if(fw.startsWith("UN")){
//				查询某个岗位的说有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
				
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.b0110 like '");
				where.append(fw.substring(2,fw.length()));
				where.append("%'");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and"+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
					
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
			if(fw.startsWith("UM")){
//				查询某个岗位的说有日志
				String starthere=this.getDataValue("p0104",">=",startime);
				String endhere=this.getDataValue("p0104","<=",endtime);
				
				where.append("from p01 ");
				where.append(ljoin.toString());
				where.append(" where p.e0122 like '");
				where.append(fw.substring(2,fw.length()));
				where.append("%' and");
				if(nw!=null&&nw.length()>0) {
                    where.append(" and"+nw);
                }
				
				if(startime!=null&&startime.length()>1) {
                    where.append(" and "+starthere);
                }
				if(endtime!=null&&endtime.length()>1) {
                    where.append(" and "+endhere);
                }
					
				where.append(" and p0115 is not null and p0115<>'01' ");
			}
		if(fw.startsWith("@K")){
//			查询某个岗位的说有日志
			String starthere=this.getDataValue("p0104",">=",startime);
			String endhere=this.getDataValue("p0104","<=",endtime);
			
			where.append("from p01 ");
			where.append(ljoin.toString());
			where.append(" where p.e01a1 like '");
			where.append(fw.substring(2,fw.length()));
			where.append("%' and");
			if(nw!=null&&nw.length()>0) {
                where.append(" and"+nw);
            }
			
			if(startime!=null&&startime.length()>1) {
                where.append(" and "+starthere);
            }
			if(endtime!=null&&endtime.length()>1) {
                where.append(" and "+endhere);
            }
			
			where.append(" and p0115 is not null and p0115<>'01' ");
		}
		if(fw.startsWith("Usr")){
//			查询某个人的所有日志
			String starthere=this.getDataValue("p0104",">=",startime);
			String endhere=this.getDataValue("p0104","<=",endtime);

			where.append("from p01 ");
			where.append(ljoin.toString());
			where.append(" where p.a0100 like '");
			where.append(fw.substring(3,fw.length()));
			where.append("%' and");
			if(nw!=null&&nw.length()>0) {
                where.append(" and"+nw);
            }
			
			if(startime!=null&&startime.length()>1) {
                where.append(" and "+starthere);
            }
			if(endtime!=null&&endtime.length()>1) {
                where.append(" and "+endhere);
            }
			
			where.append(" and p0115 is not null and p0115<>'01' ");
		}
		}
		where.append(" and p.state="+ymd);
		
		
		if(!"all".equalsIgnoreCase(p0115)) {
            where.append(" and p.p0115='"+p0115+"'");
        }
		for(int i=0;i<fieldlist.size();i++){//添加点击条件查询按钮的查询条件 xuj 2009-11-3
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(field.getItemid().equalsIgnoreCase(colum)){
				if("D".equalsIgnoreCase(field.getItemtype())){
					if(name!=null&&name.length()>0)	//liwc 查询开始时间为空 不加此约束
                    {
                        where.append(" and p."+colum+" >= "+Sql_switcher.dateValue(name));
                    }
					if(namevalue!=null&&namevalue.length()>0) {
                        where.append(" and "+Sql_switcher.dateToChar("p."+colum)+" <= "+Sql_switcher.dateValue(namevalue));
                    }
				}else if("A".equalsIgnoreCase(field.getItemtype())|| "M".equalsIgnoreCase(field.getItemtype())){
					if("A".equalsIgnoreCase(field.getItemtype())&&field.getCodesetid()!=null&&field.getCodesetid().trim().length()>0&&!"0".equals(field.getCodesetid())){
						if(namevalue!=null&&namevalue.length()>0) {
                            where.append(" and p."+colum+" = '"+namevalue+"'");
                        }
					}else if("nbase".equalsIgnoreCase(colum)){
						if(name!=null&&name.length()>0) {
                            where.append(" and p."+colum+" = '"+name+"'");
                        }
					}else{
						where.append(" and p."+colum+" like '%"+name+"%'");
					}
				}else if("N".equalsIgnoreCase(field.getItemtype())){
					if(name!=null&&name.length()>0)	//liwc 数字类型(如：计划用时或实际用时)如果为空 不添加约束条件
                    {
                        where.append(" and p."+colum+" = "+name+"");
                    }
				}
			}
		}
		where.append(" and A0100<>'"+uv.getA0100()+"'");//20151226 wangjl 员工日志审批是不能看到自己
		sql[0]=select;
		sql[1]=where.toString();
		sql[2]=column.toString();
		sql[3]=orderby;
		return sql;
	}
	public String[] getWorkdiaryshow(UserView uv,String fw,ArrayList fieldlist,String startime){
		String[] sql=new String[4];
		StringBuffer ljoin=new StringBuffer();
		ljoin.append(" p ");
		String nw=" and a0100 in("+ this.getdbtable(uv)+")";
		StringBuffer column=new StringBuffer();
		String where="";
		{	String temwhere=this.getDataValue(" p0104","=",startime);
			if(fw==null||fw.length()<1){
				where="from p01 "+ljoin.toString()+" where "+temwhere +nw;
			}else{
			if(fw.startsWith("UN")){	
				where="from p01 "+ljoin.toString()+" where p.b0110 like '"+fw.substring(2,fw.length())+"%' and "+temwhere;
			}
			if(fw.startsWith("UM")){
				where="from p01 "+ljoin.toString()+" where p.e0122 like '"+fw.substring(2,fw.length())+"%' and "+temwhere;
			}
			if(fw.startsWith("@K")){
				where="from p01 "+ljoin.toString()+" where p.e01a1 like '"+fw.substring(2,fw.length())+"%' and "+temwhere;
			}
			if(fw.startsWith("Usr")){
				where="from p01 "+ljoin.toString()+" where p.a0100 like '"+fw.substring(2,fw.length())+"%' and "+temwhere;
			}
			where=where+" and p0115 is not null and p0115<>'01'";
			}
		}
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(i==0){
					column.append(field.getItemid());				
			}else{	
					column.append(","+field.getItemid());		
			}
		}
		String select="select "+column.toString();
		select+=",case when state='0' then '日志' when state='1' then '周报' ";
		select+="when state='2' then '月报' else '' end as stateflag";
		String orderby="";
		sql[0]=select;
		sql[1]=where;
		sql[2]=column.toString();
		sql[3]=orderby;
		return sql;
	}
	public String getDataValue(String fielditemid,String operate,String value)
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

				/*	a_value.append(fielditemid);
				a_value.append(operate);
				a_value.append(Sql_switcher.dateValue(value));
				 */
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return a_value.toString();
	}
	public String getDataItem(String itemid1,String operate,String itemid2)
	{
		StringBuffer a_value = new StringBuffer();
		if(itemid1!=null&&itemid1.trim().length()>0&&operate!=null&&operate.trim().length()>0
				&&itemid2!=null&&itemid2.trim().length()>0){
			try
			{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(itemid1)+operate+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+operate+Sql_switcher.month(itemid2)+" and ");
					a_value.append(Sql_switcher.day(itemid1)+operate+Sql_switcher.day(itemid2));
					a_value.append(" ) ");
				}else if(">=".equals(operate)){
					a_value.append("(");
					a_value.append(Sql_switcher.year(itemid1)+">"+Sql_switcher.year(itemid2)+" or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+">"+Sql_switcher.month(itemid2)+" ) or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+"="+Sql_switcher.month(itemid2));
					a_value.append(" and "+Sql_switcher.day(itemid1)+">="+Sql_switcher.day(itemid2));
					a_value.append(") ) ");
				}else if("<=".equals(operate)){
					a_value.append("(");
					a_value.append(Sql_switcher.year(itemid1)+"<"+Sql_switcher.year(itemid2)+" or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+"<"+Sql_switcher.month(itemid2)+" ) or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+"="+Sql_switcher.month(itemid2));
					a_value.append(" and "+Sql_switcher.day(itemid1)+"<="+Sql_switcher.day(itemid2));
					a_value.append(") ) ");
				}else
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(itemid1)+operate+Sql_switcher.year(itemid2)+" or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+operate+Sql_switcher.month(itemid2)+" ) or ( ");
					a_value.append(Sql_switcher.year(itemid1)+"="+Sql_switcher.year(itemid2)+" and ");
					a_value.append(Sql_switcher.month(itemid1)+"="+Sql_switcher.month(itemid2)+" and ");
					a_value.append(Sql_switcher.day(itemid1)+operate+Sql_switcher.day(itemid2));
					a_value.append(") ) ");

				}

				/*	a_value.append(fielditemid);
				a_value.append(operate);
				a_value.append(Sql_switcher.dateValue(value));
				 */
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return a_value.toString();
	}
	private String getdbtable(UserView userView){
		StringBuffer sbtable=new StringBuffer();		
	    String expr="1";
        String factor="";
		if("UN".equals(userView.getManagePrivCode()))
		{
			factor="B0110=";
			if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
			{
				  factor+=userView.getManagePrivCodeValue();
				  factor+="%`";
			}
			else
			{
			  factor+="%`B0110=`";
			  expr="1+2";
			}
		}
		else if("UM".equals(userView.getManagePrivCode()))
		{
			factor="E0122="; 
			if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
			{
				  factor+=userView.getManagePrivCodeValue();
				  factor+="%`";
			}
			else
			{
			  factor+="%`E0122=`";
			  expr="1+2";
			}
		}
		else if("@K".equals(userView.getManagePrivCode()))
		{
			factor="E01A1=";
			if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
			{
				  factor+=userView.getManagePrivCodeValue();
				  factor+="%`";
			}
			else
			{
			  factor+="%`E01A1=`";
			  expr="1+2";
			}
		}
		else
		{
			 expr="1+2";
			factor="B0110=";
			if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0) {
                factor+=userView.getManagePrivCodeValue();
            }
			factor+="%`B0110=`";
		}			
		 ArrayList fieldlist=new ArrayList();
	        try
	        {        
		            /**表过式分析*/
	            /**非超级用户且对人员库进行查询*/
	        	String strwhere="";
	        	String extendWhere = "";//还要匹配人员库  郭峰
	        	//判断此库是否是已认证的库，若否，则不查询		 臧雪健
	            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	            String A01="";
	            if(login_vo!=null) {
                    A01 = login_vo.getString("str_value").toLowerCase();
                }
	            A01=A01.substring(0,A01.length()-1);
	            int j = 0;
	        	for(int i=0;i<userView.getPrivDbList().size();i++){
	        		String userbase=(String) userView.getPrivDbList().get(i);
	        		if(A01.indexOf(userbase.toLowerCase())==-1) {
                        continue;
                    }
	        			j++;
		        		strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		        		extendWhere = " and "+userbase+"A01.a0100 in (select distinct a0100 from p01 where p01.nbase='"+userbase+"')";
		        		if(j==1){
		        		sbtable.append("select a0100 "+strwhere+extendWhere);
		        		}else{
		        			sbtable.append(" union select a0100 "+strwhere+extendWhere);
		        		}
	        			

	        	}
	        	
	        }catch(Exception e){
	          e.printStackTrace();	
	        }

		return sbtable.toString();
	}
	public boolean checkState(Connection conn){
		boolean check = false;
		String sqlstr = "select state from p01";
		ContentDAO dao = new ContentDAO(conn);
		try {
			dao.search(sqlstr);
			check=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			String alertsql = "alter table p01 add state int";
			try {
				dao.update(alertsql);
				check=true;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return check;
	}
	public String getDateSql(String fielditem,String starttime,String endtime)
	{
		String sql= "";
		try
		{
			String s_f_str = getDateFormatStr(starttime);
			String e_f_str = getDateFormatStr(endtime);
			String s_str="";
			String e_str="";
			if(s_f_str.length()>0)
			{
	    		 s_str = this.getDateSqlStr(fielditem, s_f_str.split("-"), 1);	
			}
			if(e_f_str.length()>0)
			{
				 e_str = this.getDateSqlStr(fielditem, e_f_str.split("-"), 2);
			}
			sql = s_str+" and "+e_str;
			
		}
		catch(Exception e)
		{
			
		}
		return sql;
	} 
	public String getDateFormatStr(String value)
	{
		String ret_value = "";
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
				ret_value=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
			}
		}
		return ret_value;
	}
	public String getDateSqlStr(String fielditem,String[] date_arr,int type)
	{
		
		StringBuffer buf = new StringBuffer();
		String operator1 = "";
		String operator2 = "";
		if(type==1)//其使时间
		{
			operator1 = ">";
			operator2 = ">=";
		}
		else
		{
			operator1 = "<";
			operator2 = "<=";
		}
		buf.append("((");
		buf.append(Sql_switcher.year(fielditem));
		buf.append(operator1);
		buf.append("'"+date_arr[0]+"'");
		buf.append(") or (");
		buf.append(Sql_switcher.year(fielditem)+"='"+date_arr[0]);
		buf.append("' and ");
		buf.append(Sql_switcher.month(fielditem));
		buf.append(operator1+"'"+date_arr[1]+"') or (");
		buf.append(Sql_switcher.year(fielditem)+"='"+date_arr[0]);
		buf.append("' and ");
		buf.append(Sql_switcher.month(fielditem));
		buf.append("='"+date_arr[1]);
		buf.append("' and ");
		buf.append(Sql_switcher.day(fielditem));
		buf.append(operator2+"'"+date_arr[2]+"'");
		buf.append("))");
		return buf.toString();
	}
	
//	/**
//	 * 为支持多级查询添加约束
//	 * @return where 条件
//	 * @author LiWeichao
//	 */
//	public String getCurr_userSql(UserView userView){
//		StringBuffer where = new StringBuffer();
//		where.append(" (Curr_user='");
//		where.append(userView.getUserName());
//		where.append("' or ");
//		where.append(Sql_switcher.isnull("Curr_user", "'0'"));
//		where.append(" = '0' or Curr_user ='')");
//		return where.toString();
//	}
	
	/**
	 * 为支持多级查询添加约束报批指定人和抄送不按管理范围走
	 * @return where 条件
	 * @author LiWeichao
	 */
	public String getRestrict(UserView uv){
		StringBuffer where = new StringBuffer();
		//报批
		where.append(" Curr_user='"+uv.getUserName()+"'");
		//抄送
		where.append(" or p0100 in(select p0100 from per_diary_actor where state=1 and a0100='");
		where.append(uv.getA0100());
		where.append("' and NBASE='"+uv.getDbname()+"')");
		return where.toString();
	}

}
