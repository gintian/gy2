package com.hjsj.hrms.businessobject.hire.zp_options.stat.itemstat;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.sys.options.ParseSYS_OTH_PARAM;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Connection;
import java.util.*;

/**
 * 
 * @author yxc
 *
 */
public class InitHireStatBo {
	/**
	 * 
	 * @param dao
	 * @param fieldstr 常量表字段名称如：ZP_DBNAME
	 * @return 返回字符串  Str_value得值
	 */
	public String getContentStr(ContentDAO dao,String fieldstr){
		String sql="select * from constant where constant='"+fieldstr+"'";
		String ret="";
		try {
			List dylist=dao.searchDynaList(sql);
			if(dylist!=null&&dylist.size()>0){
				DynaBean dynabean=(DynaBean) dylist.get(0);
				ret=(String) dynabean.get("str_value");
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 返回招聘库前缀
	 * @return
	 */
	public String getZpDbname(ContentDAO dao){
		String ret="Usr";
		String dbname=this.getContentStr(dao,"ZP_DBNAME");
		if(dbname!=null&&dbname.length()>0){
			ret=dbname;
		}
		return ret;
	}
	/**
	 * 返回统计指标
	 * @return
	 */
	public ArrayList getStatItem(ContentDAO dao){
		ArrayList mylist=new ArrayList();
		String zparam=this.getContentStr(dao,"ZP_PARAMTER");
		String xml=null;
		if(zparam!=null&&zparam.length()>0){
			xml=zparam;
		}
			if(xml!=null){
				try {
					ParseSYS_OTH_PARAM sop=new ParseSYS_OTH_PARAM(xml);
					Map myMap=sop.serachatomElemetValue("/zp_para/resume_static");
					if(myMap!=null)
					{
						String resume=(String) myMap.get("resume_static");
						String[] temp=resume.split(",");
						for(int i=0;i<temp.length;i++){
							FieldItem fi=DataDictionary.getFieldItem(temp[i]);
							if(fi!=null&& "1".equals(fi.getUseflag())){
							mylist.add(fi);
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
//		}
		if(mylist.size()==0){
			FieldItem fi=DataDictionary.getFieldItem("A0107");
			if(fi!=null&& "1".equals(fi.getUseflag())) {
                mylist.add(fi);
            }
		}
		return mylist;
	}
	/**
	 * 数据库查询字符串
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param zp_pos_id 职位名称
	 * @param statitems 统计指标list
	 * @return 数据库查询字符串
	 */
	public String getStatStr(String startime,String endtime,String zp_pos_id,ArrayList statitems,ContentDAO dao){
		StringBuffer sbsql=new StringBuffer();
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		Set myset=new HashSet();
		String tempstart="";
		String tempend ="";
		String timefield="";
		timefield = "  "+wss.getDateSql("APPLY_DATE", startime, endtime);
		StringBuffer sbitem=new StringBuffer();
		if(statitems.size()>0){
			for(int i=0;i<statitems.size();i++){
				FieldItem fi=(FieldItem)statitems.get(i);
				if(fi!=null){
					if(i==0){
						sbitem.append(fi.getItemid());
						myset.add(fi.getFieldsetid());
					}
					else{
						sbitem.append(",");
						sbitem.append(fi.getItemid());
						myset.add(fi.getFieldsetid());
					}
				}else{
					statitems.remove(i);
					i--;
				}
			}
		}else{
			sbitem.append("*");
		}
		sbsql.append("select "+sbitem.toString()+" from zp_pos_tache  zpt   ");
//		sbsql.append(" (select * from "+this.getZpDbname(dao)+"A01)  zpk ");
//		sbsql.append(" on zpk.a0100=zpt.a0100 ");
		for(Iterator its=myset.iterator();its.hasNext();){
			String fieldsetid=(String) its.next();
			if("A01".equals(fieldsetid)){
				sbsql.append(" left join ");
				sbsql.append(" (select * from "+this.getZpDbname(dao)+fieldsetid+")  zpk"+fieldsetid);
				sbsql.append(" on zpk"+fieldsetid+".a0100=zpt.a0100 ");
			}else{
				sbsql.append(" left join ");
				sbsql.append(" (select * from "+this.getZpDbname(dao)+fieldsetid+" a where a.i9999=(select max(i9999) from "+this.getZpDbname(dao)+fieldsetid+" b where a.a0100=b.a0100)) zpk"+fieldsetid);
				sbsql.append(" on zpk"+fieldsetid+".a0100=zpt.a0100 ");
			}
		}
		if(zp_pos_id!=null&&zp_pos_id.length()>0){
			sbsql.append(" where zp_pos_id='"+zp_pos_id+"' and ");
			sbsql.append(timefield);
		}else{
			if(timefield.length()>1){
				sbsql.append(" where ");
				sbsql.append(timefield);
			}
		}
		String sql=sbsql.toString();
		return sql;
	}
	/**
	 * 获得统计结果list
	 * @param startime 开始时间
	 * @param endtime 结束时间
	 * @param zp_pos_id 职位名称
	 * @param statitems 统计指标list
	 * @param dao ContentDAo
	 * @return list 其中包含对象位Map
	 * @throws GeneralException
	 */
	public List statItemResult(String startime,String endtime,String zp_pos_id,ArrayList statitems,ContentDAO dao) throws GeneralException{
		List retlist=new ArrayList();
		ArrayList itemlist=this.getStatItem(dao);
		String sql=this.getStatStr(startime,endtime,zp_pos_id,itemlist,dao);
		List dynalist=dao.searchDynaList(sql);
		Map[] alarray=new Map[itemlist.size()];
		for(int m=0;m<alarray.length;m++){
			alarray[m]=new HashMap();
		}
		
		int[] noArray=new int[itemlist.size()];
		int k=0;
		for(int i=0;i<dynalist.size();i++){
			DynaBean dynabean=(DynaBean) dynalist.get(i);
			for(int j=0;j<itemlist.size();j++){
				FieldItem fi=(FieldItem)itemlist.get(j);
				String itemid=fi.getItemid();
				String  itemvalue=dynabean.get(itemid).toString();
	
				if(itemvalue!=null&&itemvalue.length()>0){
					if(alarray[j].containsKey(itemvalue)){
						String num =(String) alarray[j].get(itemvalue);
						Integer inum=new Integer(num);
						int addnum=inum.intValue()+1;
						inum=new Integer(addnum);
						alarray[j].put(itemvalue,inum.toString());
					}else{
						alarray[j].put(itemvalue,"1");
					}
				}
				else
				{
					noArray[j]++;
					alarray[j].put(" 未填",String.valueOf(noArray[j]));
				}
			}
		}
		retlist.add(itemlist);
		retlist.add(alarray);
		return retlist;
	}
	/**
	 * 职位列表
	 * @param dao
	 * @param zp_pos_id 职位名称定位
	 * @return 返回之位列表
	 * @throws GeneralException
	 */
	public ArrayList getZposlist(ContentDAO dao,String zp_pos_id,Connection con) throws GeneralException{
		ArrayList retlist=new ArrayList();
		
		ParameterXMLBo bo2 = new ParameterXMLBo(con, "1");
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
		
		String sql="select  z0301,z0311,z0325,z0321,z0336 from z03 ";
		if(hireMajor.length()>0&&",z0301,z0311,z0325,z0321,z0336".indexOf(","+hireMajor.toLowerCase())==-1) {
            sql="select  z0301,z0311,z0325,z0321,z0336,"+hireMajor+" from z03 ";
        }
		ArrayList dylist=dao.searchDynaList(sql);
		CommonData obj1=new CommonData("","不限");
		retlist.add(0,obj1);
		for(Iterator it=dylist.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			String z0336=dynabean.get("z0336").toString();
			
			String posname=AdminCode.getCodeName("@K",dynabean.get("z0311").toString());
			if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
			{
				if(hireMajorIsCode)
				{
					posname=dynabean.get(hireMajor).toString().trim();
					posname=AdminCode.getCodeName(hireMajoritem.getCodesetid(),posname);
				}
				else
				{
					 posname=dynabean.get(hireMajor).toString();
				}
			}
			
			
			if(posname!=null&&posname.length()>0){
				String um=(String)dynabean.get("z0325");
				String umname="";
				if(um!=null)
				{
					umname=AdminCode.getCodeName("UM",um);
				}
				if((umname==null|| "".equals(umname))&&dynabean.get("z0321")!=null)
				{
					umname=AdminCode.getCodeName("UN",(String)dynabean.get("z0321"));
				}
				if(zp_pos_id.equals(dynabean.get("z0301").toString()))
				{
					CommonData obj=new CommonData(dynabean.get("z0301").toString()+"/"+posname+"("+umname+")",posname+"("+umname+")");
					retlist.add(0,obj);
				}
				else
				{
					CommonData obj=new CommonData(dynabean.get("z0301").toString()+"/"+posname+"("+umname+")",posname+"("+umname+")");
					retlist.add(obj);
				}
			}
		}
		return retlist;
	}
	/**
	 * 状态列表
	 * @param dao
	 * @param state 根据状态定位
	 * @return 返回状态列表
	 * @throws GeneralException
	 */
	public ArrayList getState(ContentDAO dao,String state) throws GeneralException{
		ArrayList retlist=new ArrayList();
		String sql="select * from codeitem where codeitemid=parentid and codesetid='36'";
		ArrayList statelist=dao.searchDynaList(sql);
		CommonData obj1=new CommonData("","不限");
		retlist.add(obj1);
		for(Iterator it=statelist.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean) it.next();
			String codeitemid=dynabean.get("codeitemid").toString();
			String codeitemdesc=dynabean.get("codeitemdesc").toString();
			CommonData obj=new CommonData(codeitemid,codeitemdesc);
			if(codeitemid.equalsIgnoreCase(state)){
				retlist.add(0,obj);
			}else{
				retlist.add(obj);
			}
			
		}
		return retlist;
	}
	/*
	 * 获得指标状态统计字段
	 */
	public String  getStateField(Connection  conn) throws GeneralException{
		String resume_state_field="";
		ParameterXMLBo bo2=new ParameterXMLBo(conn,"1");
		HashMap map=bo2.getAttributeValues();
		if(map!=null&&map.get("resume_state")!=null) {
            resume_state_field=(String)map.get("resume_state");
        }

		
		return resume_state_field;
	}
	/**
	 * 获得状态统计指标代码及其值
	 * @param dao
	 * @param codeitemid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getStateCode(ContentDAO dao,String codeitemid) throws GeneralException{
		String sql="select * from codeitem where codesetid='36' and codeitemid=parentid";
		if(codeitemid!=null&&codeitemid.length()>0){
		 sql="select * from codeitem where codesetid='36' and codeitemid like '"+codeitemid+"%' and codeitemid<>'"+codeitemid+"'";
		}
		return dao.searchDynaList(sql);
	}
	/**
	 * 获得状态统计sql语句
	 * @param startime
	 * @param endtime
	 * @param zp_pos_id
	 * @param itemlist
	 * @param dao
	 * @return
	 * @throws GeneralException 
	 */
	public String getStateSql(String startime,String endtime,String zp_pos_id,String codeitemid,ContentDAO dao,Connection conn) throws GeneralException{
		String dbpre=this.getZpDbname(dao);
		StringBuffer sbsql=new StringBuffer();
		String sf=this.getStateField(conn);
		if(sf==null||sf.length()<1){
			sf="c0105";
		}
		ArrayList statecodelist=this.getStateCode(dao,codeitemid);
		
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart="";
		String tempend ="";
		String timefield="";
		if(startime!=null&&endtime!=null&&startime.length()>0&&startime.length()>0){
			tempstart=wss.getDataValue("APPLY_DATE",">=",startime);
			tempend=wss.getDataValue("APPLY_DATE","<=",endtime);
			timefield="  "+tempstart+" and "+tempend;
		}else{
			if(startime!=null&&startime.length()>0&&endtime==null&&endtime.length()<1){
				tempstart=wss.getDataValue("APPLY_DATE",">=",startime);
				timefield=" "+tempstart;
			}
		
		
			if(endtime!=null&&endtime.length()>0&&startime==null&&startime.length()<1){
				 tempend=wss.getDataValue("APPLY_DATE","<=",endtime);
				 timefield=" "+tempend;
			}
		}
		
		for(int i=0;i<statecodelist.size();i++){
			DynaBean dynaBean=(DynaBean)statecodelist.get(i);
			if(i==0){
				/*sbsql.append("select count(*) as num,'"+dynaBean.get("codeitemid").toString()+"' as state from ");
				sbsql.append("( ");
				sbsql.append(" select zpt.zp_pos_id,a."+sf+" from zp_pos_tache  zpt left join ");
				sbsql.append(" (select * from "+dbpre+"a01) a  ");
				if(codeitemid.length()>0){
					sbsql.append(" on zpt.a0100=a.a0100 where a."+sf+" ='"+dynaBean.get("codeitemid").toString()+"'");
				}else{
					sbsql.append(" on zpt.a0100=a.a0100 where a."+sf+" like'"+dynaBean.get("codeitemid").toString()+"%'");
				}
				if(zp_pos_id!=null&&zp_pos_id.length()>0){
					sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
				}
				if(timefield.length()>0){
					sbsql.append(" and "+timefield);
				}
				sbsql.append(") c"+i);*/
			    String tempcodeitemid=dynaBean.get("codeitemid").toString();
			    if(tempcodeitemid.indexOf("2")==0){

	                  sbsql.append(" select count(*) as num,"+dynaBean.get("codeitemid").toString()+" as state from ");
	                  sbsql.append(dbpre+"a01,z05 where "+dbpre+"a01.a0100 in (");
	                  sbsql.append(" select a0100 from zp_pos_tache where z05.a0100=zp_pos_tache.a0100 ");
	                  if(zp_pos_id!=null&&zp_pos_id.length()>0){
	                        sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
	                    }
	                    if(timefield.length()>0){
	                        sbsql.append(" and "+timefield);
	                    }
	                    sbsql.append(")");
	                    
	                    if(codeitemid.length()>0){
	                        sbsql.append("  and z05.state='"+dynaBean.get("codeitemid").toString()+"'");
	                        sbsql.append(" and "+dbpre+"a01."+sf+" like'3%'");
	                    }else{
	                        sbsql.append("  and z05.state like '"+dynaBean.get("codeitemid").toString()+"%'");
	                        sbsql.append(" and "+dbpre+"a01."+sf+" like'3%'");
	                    }
	                
			    }else{
		             sbsql.append(" select count(*) as num,"+dynaBean.get("codeitemid").toString()+" as state from ");
		                sbsql.append(dbpre+"a01 where a0100 in (");
		                sbsql.append(" select a0100 from zp_pos_tache where 1=1 ");
		                if(zp_pos_id!=null&&zp_pos_id.length()>0){
		                    sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
		                }
		                if(timefield.length()>0){
		                    sbsql.append(" and "+timefield);
		                }
		                sbsql.append(")");
		                if(codeitemid.length()>0){
		                    sbsql.append(" and "+sf+" ='"+dynaBean.get("codeitemid").toString()+"'");
		                }else{
		                    sbsql.append(" and "+sf+" like'"+dynaBean.get("codeitemid").toString()+"%'");
		                }
			    }
			}
			else{
				sbsql.append(" union all ");
				/*sbsql.append("select count(*) as num,'"+dynaBean.get("codeitemid").toString()+"' as state from ");
				sbsql.append("( ");
				sbsql.append(" select zpt.zp_pos_id,a."+sf+" from zp_pos_tache  zpt left join ");
				sbsql.append(" (select * from "+dbpre+"a01) a  ");
				if(codeitemid.length()>0){
					sbsql.append(" on zpt.a0100=a.a0100 where a."+sf+" ='"+dynaBean.get("codeitemid").toString()+"'");
				}else{
					sbsql.append(" on zpt.a0100=a.a0100 where a."+sf+" like'"+dynaBean.get("codeitemid").toString()+"%'");
				}if(zp_pos_id!=null&&zp_pos_id.length()>0){
					sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
				}
				if(timefield.length()>0){
					sbsql.append(" and "+timefield);
				}
				sbsql.append(") c"+i);*/
				String tempcodeitemid=dynaBean.get("codeitemid").toString();
				if(tempcodeitemid.indexOf("2")==0){
				  sbsql.append(" select count(*) as num,"+dynaBean.get("codeitemid").toString()+" as state from ");
	              sbsql.append(dbpre+"a01,z05 where "+dbpre+"a01.a0100 in (");
	              sbsql.append(" select a0100 from zp_pos_tache where z05.a0100=zp_pos_tache.a0100 ");
	              if(zp_pos_id!=null&&zp_pos_id.length()>0){
	                    sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
	                }
	                if(timefield.length()>0){
	                    sbsql.append(" and "+timefield);
	                }
	                sbsql.append(")");
	                
	                if(codeitemid.length()>0){
                        sbsql.append("  and z05.state='"+dynaBean.get("codeitemid").toString()+"'");
                        sbsql.append(" and "+dbpre+"a01."+sf+" like'3%'");
                    }else{
                        sbsql.append("  and z05.state like '"+dynaBean.get("codeitemid").toString()+"%'");
                        sbsql.append(" and "+dbpre+"a01."+sf+" like'3%'");
                    }
				}else{
				    sbsql.append(" select count(*) as num,"+dynaBean.get("codeitemid").toString()+" as state from ");
	                sbsql.append(dbpre+"a01 where a0100 in (");
	                sbsql.append(" select a0100 from zp_pos_tache where 1=1 ");
	                if(zp_pos_id!=null&&zp_pos_id.length()>0){
	                    sbsql.append("  and zp_pos_id='"+zp_pos_id+"' ");
	                }
	                if(timefield.length()>0){
	                    sbsql.append(" and "+timefield);
	                }
	                sbsql.append(")");
	                if(codeitemid.length()>0){
	                    sbsql.append(" and "+sf+" ='"+dynaBean.get("codeitemid").toString()+"'");
	                }else{
	                    sbsql.append(" and "+sf+" like'"+dynaBean.get("codeitemid").toString()+"%'");
	                } 
				}
			}
		}
		return sbsql.toString();
	}
	/**
	 * 产生也面显示用的数据
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param zp_pos_id 职位需求id
	 * @param codeitemid 状态id
	 * @param dao dao
	 * @param conn 数据库廉洁
	 * @return
	 * @throws GeneralException
	 */
	public List statStateResult(String startime,String endtime,String zp_pos_id,String codeitemid,ContentDAO dao,Connection conn) throws GeneralException{
		List retlist=new ArrayList();
		String sql=this.getStateSql(startime,endtime,zp_pos_id,codeitemid,dao,conn);
		ArrayList dylist=dao.searchDynaList(sql);
		for(Iterator it=dylist.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean)it.next();
			String value=(String) dynabean.get("num");
			String state=(String) dynabean.get("state");
//			if(!"0".equals(value)){
				String codename=AdminCode.getCodeName("36",state);
				CommonData obj=new CommonData(value,codename);
				retlist.add(obj);
//			}
		}
		return retlist;
	}
	
}
