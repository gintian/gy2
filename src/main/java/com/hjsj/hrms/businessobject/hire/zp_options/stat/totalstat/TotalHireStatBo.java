package com.hjsj.hrms.businessobject.hire.zp_options.stat.totalstat;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TotalHireStatBo {

	/**数据库时间条件字符串
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @return  数据库时间条件字符串
	 */	
	private Connection conn;
	public TotalHireStatBo(Connection conn)
	{
		this.conn=conn;
	}
	public String getTime(String startime,String endtime)
	{
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart="";
		String tempend ="";
		String timefield="";
		if(startime==null){
			startime="";
		}
		if(endtime==null){
			endtime="";
		}
		if(!"".equals(startime)&&!"".equals(endtime)&&startime.length()>0&&startime.length()>0){
			tempstart=wss.getDataValue("APPLY_DATE",">=",startime);
			tempend=wss.getDataValue("APPLY_DATE","<=",endtime);
			timefield="and  "+tempstart+" and "+tempend;
		}else{
			if(!"".equals(startime)&&"".equals(endtime)){
				tempstart=wss.getDataValue("APPLY_DATE",">=",startime);
				timefield="and "+tempstart;
			}
			else if(!"".equals(endtime)&&"".equals(startime)){
				 tempend=wss.getDataValue("APPLY_DATE","<=",endtime);
				 timefield="and "+tempend;
			}
			else
			{
				timefield="";
			}
		}
		return 	timefield;
	}
	
	/**
	 * 数据库查询所有简历字符串
	 * @param dao
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param orgid    机构id
	 * @return 数据库查询所有简历字符串
	 */
	public String getresumesql(String startime,String endtime,String orgid,ContentDAO dao)
	{
		StringBuffer sbsql = new StringBuffer();
		String sql="";
		ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
		HashMap map0;
		try {
			map0 = bo2.getAttributeValues();
	
		String hireMajor="";			//xieguiquan 2010-09-17
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
		//2014.11.2 xxd当岗位上级为单位时，将进行判断case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end
		if(orgid !=null && orgid.length()>0)
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+" from (select * from zp_pos_tache");
				
				sbsql.append(" where zp_pos_id in(select z0301 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%')");
				sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336,"+hireMajor+" from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311 ,z.z0336,z."+hireMajor+"");
			}else{
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336 from (select * from zp_pos_tache");
				sbsql.append(" where zp_pos_id in(select z0301 from z03 where z0325 like '"+orgid+"%')");
				sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336 from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336");
			}	
		//sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from (select * from zp_pos_tache");
//		sbsql.append(" where zp_pos_id in(select z0301 from z03 where z0325 like '"+orgid+"%')");
//		sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311 from z03) z on p.zp_pos_id=z.z0301");
//		sbsql.append(" group by p.zp_pos_id, z.z0311");
		sql = sbsql.toString();		
		}
		else
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+" from (select * from zp_pos_tache");
				sbsql.append(" where zp_pos_id in(select z0301 from z03 )");
				sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336,"+hireMajor+" from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336,z."+hireMajor+"");
				sql = sbsql.toString();	
			}else{
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336 from (select * from zp_pos_tache");
				sbsql.append(" where zp_pos_id in(select z0301 from z03 )");
				sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336 from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336");
				sql = sbsql.toString();	
			}
//			sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from (select * from zp_pos_tache");
//			sbsql.append(" where zp_pos_id in(select z0301 from z03 )");
//			sbsql.append(this.getTime(startime, endtime)+ ")p left join (select z0301,z0311 from z03) z on p.zp_pos_id=z.z0301");
//			sbsql.append(" group by p.zp_pos_id, z.z0311");
//			sql = sbsql.toString();		
		}
		
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}
	/**
	 * 数据库查询第一志愿简历字符串
	 * @param dao
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param orgid    机构id
	 * @return 数据库查询第一志愿简历字符串
	 */
	public String getfiresumesql(String startime,String endtime,String orgid,ContentDAO dao)
	{
		StringBuffer sbsql = new StringBuffer();
		String sql="";
		ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
		HashMap map0;
		try {
			map0 = bo2.getAttributeValues();
	
		String hireMajor="";			//xieguiquan 2010-09-17
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
		//2014.11.2 xxd当岗位上级为单位时，将进行判断case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end
		if(orgid !=null && orgid.length()>0)
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
			sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+" from (select * from zp_pos_tache");
			sbsql.append(" where zp_pos_id in(select z0301 from z03 where case when(z0325 is not null) then z0325 else z0311 end like '"+orgid+"%')");
			sbsql.append("and thenumber = '1'" +this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336,"+hireMajor+" from z03) z on p.zp_pos_id=z.z0301");
			sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336,z."+hireMajor+"");
			sql = sbsql.toString();	
			}else{
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336 from (select * from zp_pos_tache");
				sbsql.append(" where zp_pos_id in(select z0301 from z03 where z0325 like '"+orgid+"%')");
				sbsql.append("and thenumber = '1'" +this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336 from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336");
				sql = sbsql.toString();	
			}
		}
		else
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
			sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+" from (select * from zp_pos_tache");
			sbsql.append(" where zp_pos_id in(select z0301 from z03 )");
			sbsql.append("and thenumber = '1'" +this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336,"+hireMajor+"  from z03) z on p.zp_pos_id=z.z0301");
			sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336,z."+hireMajor+"");
			sql = sbsql.toString();	
			}else{
				sbsql.append("select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311,z.z0336 from (select * from zp_pos_tache");
				sbsql.append(" where zp_pos_id in(select z0301 from z03 )");
				sbsql.append("and thenumber = '1'" +this.getTime(startime, endtime)+ ")p left join (select z0301,z0311,z0336 from z03) z on p.zp_pos_id=z.z0301");
				sbsql.append(" group by p.zp_pos_id, z.z0311,z.z0336");
				sql = sbsql.toString();	
			}
		}
		}catch(Exception e){
			
		}
		return sql;
	}
	/**
	 * 获得简历列表统计字符串
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param orgid    机构id
	 */
	public String getTagSqlStr(String startime,String endtime,String orgid){ 
		String wheresql="";
		StringBuffer wheresb = new StringBuffer();	
		ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
		HashMap map0;
		try {
			map0 = bo2.getAttributeValues();
	
		String hireMajor="";			//xieguiquan 2010-09-17
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
		//2014.11.2 xxd当岗位上级为单位时，将进行判断case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end
		if(orgid!= null &&orgid.length()>0)
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
			wheresb.append(" from (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0336,case when z.z0336='01' then z."+hireMajor+"  else z.z0311 end as z0311 from(");
			wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
			wheresb.append(" (select z0301 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') and thenumber='1'" +this.getTime(startime, endtime)+ ") p  left join ");
			wheresb.append(" (select z0301,z0311,z0336,"+hireMajor+" from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+")  p1 ");
			wheresb.append(" right join ");
			wheresb.append(" (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0336,case when z.z0336='01' then z."+hireMajor+"  else z.z0311 end as z0311 from( ");
			wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
			wheresb.append(" (select z0301 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%')" +this.getTime(startime, endtime)+ ")p  left join ");
			wheresb.append(" (select z0301,z0311,z0336,"+hireMajor+" from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+") p2  ");
			wheresb.append(" on p1.zp_pos_id=p2.zp_pos_id ");
			wheresql = wheresb.toString();
			}else{
				wheresb.append(" from (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from(");
				wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
				wheresb.append(" (select z0301 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') and thenumber='1'" +this.getTime(startime, endtime)+ ") p  left join ");
				wheresb.append(" (select z0301,z0311 from z03 where case when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311)  p1 ");
				wheresb.append(" right join ");
				wheresb.append(" (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from( ");
				wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
				wheresb.append(" (select z0301 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%')" +this.getTime(startime, endtime)+ ")p  left join ");
				wheresb.append(" (select z0301,z0311 from z03 where case  when(z0325 is not null and z0325<>'') then z0325 else z0311 end like '"+orgid+"%') z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311) p2  ");
				wheresb.append(" on p1.zp_pos_id=p2.zp_pos_id ");
				wheresql = wheresb.toString();
			}
		}
		
		else
		{
			if(hireMajor.length()>0&&",num,zp_pos_id,z0311,z0336".indexOf(","+hireMajor.toLowerCase())==-1){
			wheresb.append(" from (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0336,case when z.z0336='01' then z."+hireMajor+"  else z.z0311 end as z0311 from(");
			wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
			wheresb.append(" (select z0301 from z03 ) and thenumber='1' " +this.getTime(startime, endtime)+ ") p  left join ");
			wheresb.append(" (select z0301,z0311,z0336,"+hireMajor+" from z03) z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+")  p1 ");
			wheresb.append(" right join ");
			wheresb.append(" (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0336,case when z.z0336='01' then z."+hireMajor+"  else z.z0311 end as z0311 from( ");
			wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
			wheresb.append(" (select z0301 from z03 )" +this.getTime(startime, endtime)+ ")p  left join ");
			wheresb.append(" (select z0301,z0311,z0336,"+hireMajor+" from z03) z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311,z.z0336,z."+hireMajor+") p2  ");
			wheresb.append(" on p1.zp_pos_id=p2.zp_pos_id ");
			wheresql = wheresb.toString();
			}else{
				wheresb.append(" from (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from(");
				wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
				wheresb.append(" (select z0301 from z03 ) and thenumber='1' " +this.getTime(startime, endtime)+ ") p  left join ");
				wheresb.append(" (select z0301,z0311 from z03) z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311)  p1 ");
				wheresb.append(" right join ");
				wheresb.append(" (select count(p.zp_pos_id) as num ,p.zp_pos_id,z.z0311 from( ");
				wheresb.append(" select * from zp_pos_tache where zp_pos_id in ");
				wheresb.append(" (select z0301 from z03 )" +this.getTime(startime, endtime)+ ")p  left join ");
				wheresb.append(" (select z0301,z0311 from z03) z on p.zp_pos_id=z.z0301 group by p.zp_pos_id,z.z0311) p2  ");
				wheresb.append(" on p1.zp_pos_id=p2.zp_pos_id ");
				wheresql = wheresb.toString();
			}
		}
		}catch(Exception e){
			
		}
		return wheresql;
	}
	/**
	 * 获得所有简历统计结果list
	 * @param dao
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param orgid    机构id
	 * @throws GeneralException
	 */
	public List getorgresumeresult(String startime,String endtime,String orgid,ContentDAO dao) throws GeneralException
	{
		List retlist = new ArrayList();	
		String allsql = this.getresumesql(startime, endtime, orgid, dao);
		List allresultlist = dao.searchDynaList(allsql);
		int i=0;
		ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
		HashMap map0 = bo2.getAttributeValues();
		String hireMajor="";			//xieguiquan 2010-09-17
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
		for(Iterator it=allresultlist.iterator();it.hasNext();){
			i++;
			DynaBean dynabean=(DynaBean)it.next();
			/*if(i<11){*/
				String pos = dynabean.get("z0311").toString();
				
				String postr = AdminCode.getCodeName("@K",dynabean.get("z0311").toString());
				String z0336=(String)dynabean.get("z0336");
				if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
				{
					if(hireMajorIsCode)
					{
						postr=(String)dynabean.get(hireMajor);
						postr=AdminCode.getCodeName(hireMajoritem.getCodesetid(),postr);
					}
					else
					{
						postr=(String)dynabean.get(hireMajor);
					}
				}
				
				String number = dynabean.get("num").toString();
				CommonData obj = new CommonData(number,postr);
				retlist.add(obj);
			/*}else{
				 num += Integer.parseInt(dynabean.get("num").toString());
			}	*/
		}
		/*CommonData obj1 = new CommonData(num+"","其他");
		retlist.add(obj1);
		*/
		return retlist;
	}
	/**
	 * 获得第一志愿简历统计结果list
	 * @param dao
	 * @param startime 开始时间
	 * @param endtime  结束时间
	 * @param orgid    部门名称
	 * @throws GeneralException
	 */
	public List getorgfiresumeresult(String startime,String endtime,String orgid,ContentDAO dao) throws GeneralException
	{
		List retlist = new ArrayList();		
		String firstsql = this.getfiresumesql(startime, endtime, orgid, dao);
		List firstresultlist = dao.searchDynaList(firstsql);
		int i = 0;
		ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
		HashMap map0 = bo2.getAttributeValues();
		String hireMajor="";			//xieguiquan 2010-09-17
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
		for(Iterator it=firstresultlist.iterator();it.hasNext();){
			i++;
			DynaBean dynabean=(DynaBean)it.next();
			/*if(i<11)
			{*/
				String pos = dynabean.get("z0311").toString();
				String postr = AdminCode.getCodeName("@K",dynabean.get("z0311").toString());
				String z0336=(String)dynabean.get("z0336");
				if(hireMajor.length()>0&&z0336!=null&&z0336.length()>0&& "01".equals(z0336))
				{
					if(hireMajorIsCode)
					{
						postr=(String)dynabean.get(hireMajor);
						postr=AdminCode.getCodeName(hireMajoritem.getCodesetid(),postr);
					}
					else
					{
						postr=(String)dynabean.get(hireMajor);
					}
				}
				String number = dynabean.get("num").toString();
				CommonData obj = new CommonData(number,postr);
				retlist.add(obj);
			/*}else{
				num += Integer.parseInt(dynabean.get("num").toString());
			}	*/	
		}
	/*CommonData obj1 = new CommonData(num+"","其他");
	retlist.add(obj1);*/
	return retlist;
	}

}
