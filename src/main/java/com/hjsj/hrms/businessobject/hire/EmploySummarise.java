package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class EmploySummarise {
	private Connection con=null;
	
	public EmploySummarise(Connection conn)
	{
		this.con=conn;
	}
	
	
	
	
	
	
	/**
	 * 取得 招聘计划 列表数据
	 * @param fieldList   z01 的 字段
	 * @param dbname      库前缀
	 * @param  viewType   1:用工需求  2：招聘计划
	 * @return
	 */
	public ArrayList getEngagePlanList(ArrayList fieldList,String dbname,String extendWhereSql,String orderSql,UserView userView,String viewType,String planID) throws GeneralException 
	{
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer("");
		
		ParameterXMLBo bo2 = new ParameterXMLBo(this.con, "1");
		HashMap map0 = bo2.getAttributeValues();
		String hireMajor="";
		if(map0.get("hireMajor")!=null) {
            hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
        }
		boolean hireMajorIsCode=false;
		FieldItem hireMajoritem=null;
		if(hireMajor.length()>0)
		{
			hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                hireMajorIsCode=true;
            }
		}
		
		
		if("2".equals(viewType))
		{
			sql.append("select * from z01");
		}
		else if("1".equals(viewType))
		{
			sql.append("select * from z03");
			
		}
		int num=0;
		if(extendWhereSql!=null&&extendWhereSql.trim().length()>0)
		{
				sql.append(" where "+extendWhereSql);
				num++;
		}
		
		if(!(userView.isSuper_admin()))
		{
				/*String codeid=userView.getUnit_id();
				if(codeid.trim().length()>2)
				{*/
					
						/*String[] temp=codeid.split("`");*/
		            	PositionDemand positionDemand=new PositionDemand(this.con);
						if("2".equals(viewType))  //2：招聘计划
						{
							
							ArrayList unitcodeList=positionDemand.getUnitIDList2(userView);
							StringBuffer tempSql=new StringBuffer("");
						    for(int i=0;i<unitcodeList.size();i++) {
                                tempSql.append(" or z01.z0105 like '"+(String)unitcodeList.get(i)+"%'");
                            }
							if(num!=0) {
                                sql.append(" and ("+tempSql.substring(3)+")");
                            } else {
                                sql.append(" where ("+tempSql.substring(3)+") ");
                            }
							
							num++;
						}
						else if("1".equals(viewType))  //1:用工需求
						{
							ArrayList unitcodeList=positionDemand.getUnitIDList2(userView);
							StringBuffer tempSql=new StringBuffer("");
							StringBuffer tempSql2=new StringBuffer("");
							StringBuffer tempSql3=new StringBuffer("");
							
						    for(int i=0;i<unitcodeList.size();i++)
						    {
						    	tempSql.append(" or z03.z0311  like '"+(String)unitcodeList.get(i)+"%'");
						    	tempSql2.append(" or z03.z0325  like '"+(String)unitcodeList.get(i)+"%'");
						    	tempSql3.append(" or z03.z0321  like '"+(String)unitcodeList.get(i)+"%'");
						    }
							if(num!=0) {
                                sql.append(" and ( (("+tempSql.substring(3)+") and (Z0336!='01' or Z0336 is null) ) or (("+tempSql2.substring(3)+") and Z0336='01' ) or (("+tempSql3.substring(3)+") and Z0336='01' ) ) ");
                            } else {
                                sql.append(" where ( (("+tempSql.substring(3)+") and  (Z0336!='01' or Z0336 is null) ) or (("+tempSql2.substring(3)+") and Z0336='01' ) or (("+tempSql3.substring(3)+") and Z0336='01' )    ) ");
                            }
							
							num++;
						}
						
						
					
				/*}
				else
				{
					if(num!=0)
						sql.append(" and 1=2");
					else
						sql.append(" where 1=2");
					
				}*/
		}
		if(planID!=null&&planID.trim().length()>0&& "1".equals(viewType))
		{
			if(num!=0) {
                sql.append(" and z0101='"+planID+"'");
            } else {
                sql.append(" where z0101='"+planID+"'");
            }
		}
		
		
		
			
		
		
		if(orderSql!=null&&orderSql.trim().length()>0)
		{
			sql.append(" "+orderSql);
		}
		
		ContentDAO dao=new ContentDAO(this.con);		
		HashMap planNumMap=new HashMap();
		if("2".equals(viewType)) {
            planNumMap=getPlanCountMap(dbname); //获得每个招聘计划 实招人数
        } else if("1".equals(viewType)) {
            planNumMap=getPosCountMap(dbname);
        }
		
		try
		{
			RowSet rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				String z0101=rowSet.getString("z0101");
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					if("z0117".equalsIgnoreCase(item.getItemid()))  //实招人数 指标
					{
						if(planNumMap.get(z0101)!=null)
						{
							String temp="<a href='/hire/interviewEvaluating/interviewExamine.do?b_query=link&viewType=2&codeset=UN&z0101="+rowSet.getString("z0101")+"&code=summarise&model=7&operate=init'>";
							
							abean.set("z0117",temp+(String)planNumMap.get(z0101)+"</a>");
						}
						else
						{
							abean.set("z0117","");
						}
					}
					else if("employedcount".equalsIgnoreCase(item.getItemid()))  //实招人数 指标
					{
						if(planNumMap.get(rowSet.getString("z0301"))!=null)
						{
							String temp="<a href='/hire/interviewEvaluating/interviewExamine.do?b_query=link&viewType=1&codeset=UN&z0101="+rowSet.getString("z0301")+"&code=summarise&model=7&operate=init'>";
							
							abean.set("employedcount",temp+(String)planNumMap.get(rowSet.getString("z0301"))+"</a>");
						}
						else
						{
							abean.set("employedcount","");
						}
					}
					
					else
					{
						if((!"D".equals(item.getItemtype())&&rowSet.getString(item.getItemid())!=null)||("D".equals(item.getItemtype())&&rowSet.getDate(item.getItemid())!=null))
						{
							
							if("A".equals(item.getItemtype()))
							{
								if("0".equals(item.getCodesetid()))
								{
									abean.set(item.getItemid(),rowSet.getString(item.getItemid()));
								}
								else
								{
									String z0336="";
									if("1".equals(viewType)) {
                                        z0336=rowSet.getString("z0336")!=null?rowSet.getString("z0336"):"";
                                    }
									if("01".equals(z0336)&& "z0311".equalsIgnoreCase(item.getItemid())&&hireMajor!=null&&hireMajor.length()>0)
									{
										if(hireMajorIsCode) {
                                            abean.set(item.getItemid(),AdminCode.getCodeName(hireMajoritem.getCodesetid(),rowSet.getString(hireMajor)));
                                        } else {
                                            abean.set(item.getItemid(),rowSet.getString(hireMajor));
                                        }
									}
									else {
                                        abean.set(item.getItemid(),AdminCode.getCodeName(item.getCodesetid(),rowSet.getString(item.getItemid())));
                                    }
								}
							}
							else if("D".equals(item.getItemtype()))
							{
								Date date=rowSet.getDate(item.getItemid());
								abean.set(item.getItemid(),dateFormat.format(date));	
							}
							else if("N".equals(item.getItemtype()))
							{
								if(rowSet.getString(item.getItemid())!=null)
								{	
									if(item.getDecimalwidth()==0) {
                                        abean.set(item.getItemid(),PubFunc.round(rowSet.getString(item.getItemid()),0));
                                    } else {
                                        abean.set(item.getItemid(),PubFunc.round(rowSet.getString(item.getItemid()),2));
                                    }
								
								}
								else {
                                    abean.set(item.getItemid(),"");
                                }
							}
							else if("M".equals(item.getItemtype()))
							{	
								abean.set(item.getItemid(),Sql_switcher.readMemo(rowSet,item.getItemid()));
							}
						}
						else {
                            abean.set(item.getItemid()," ");
                        }
					}
				}
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 * 每个招聘职位 实招人数
	 * @return
	 */
	public HashMap getPosCountMap(String dbname) throws GeneralException 
	{
		HashMap map=new HashMap();
		try
		{
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.con,"1");
			HashMap map0=bo2.getAttributeValues();
			if(map0!=null&&map0.get("resume_state")!=null&&((String)map0.get("resume_state")).trim().length()>0) {
                resume_state_field=(String)map0.get("resume_state");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("参数设置中没有配置简历状态指标！"));
            }
			StringBuffer sql=new StringBuffer("select count(zpt.a0100) num ,z03.z0301 ");
						sql.append(" from zp_pos_tache zpt,z03,"+dbname+"A01 ");
						sql.append(" where  zpt.zp_pos_id=z03.z0301 ");
						sql.append(" and zpt.a0100="+dbname+"a01.a0100 ");
						sql.append(" and "+dbname+"A01."+resume_state_field+"='43' group by z03.z0301");
			ContentDAO dao=new ContentDAO(this.con);
			
				RowSet rowset=dao.search(sql.toString());
				while(rowset.next())
				{
					int num=rowset.getInt("num");
					if(num!=0) {
                        map.put(rowset.getString("z0301"),rowset.getString("num"));
                    } else {
                        map.put(rowset.getString("z0301"),"");
                    }
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
		return map;
	}
	
	
	/**
	 * 每个招聘计划 实招人数
	 * @return
	 */
	public HashMap getPlanCountMap(String dbname)
	{
		HashMap map=new HashMap();
		try
		{
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.con,"1");
			HashMap map0=bo2.getAttributeValues();
			if(map0!=null&&map0.get("resume_state")!=null) {
                resume_state_field=(String)map0.get("resume_state");
            }

			StringBuffer sql=new StringBuffer("select count(zpt.a0100) num ,z01.z0101 ");
						sql.append(" from zp_pos_tache zpt,z03,z01,"+dbname+"A01 ");
						sql.append(" where  zpt.zp_pos_id=z03.z0301 ");
						sql.append(" and z03.z0101=z01.z0101 and zpt.a0100="+dbname+"A01.a0100 ");
						sql.append(" and "+dbname+"A01."+resume_state_field+"='43' group by z01.z0101");
			ContentDAO dao=new ContentDAO(this.con);
			
				RowSet rowset=dao.search(sql.toString());
				while(rowset.next())
				{
					int num=rowset.getInt("num");
					if(num!=0) {
                        map.put(rowset.getString("z0101"),rowset.getString("num"));
                    } else {
                        map.put(rowset.getString("z0101"),"");
                    }
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		return map;
	}
	
	
	
	

}
