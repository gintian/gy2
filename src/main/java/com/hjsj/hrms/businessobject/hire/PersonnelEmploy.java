package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;



/**
 * 员工录用
 * @author Owner
 *
 */
public class PersonnelEmploy {
	Connection conn=null;
	
	
	public PersonnelEmploy(Connection conn)
	{
		this.conn=conn;
	}
	
	
	public LazyDynaBean getLazyDynaBean(String itemid,String itemtype,String codesetid,String fieldsetid,String desc)
	{
		LazyDynaBean lazyDynaBean0=new LazyDynaBean();
		lazyDynaBean0.set("itemid",itemid);
		lazyDynaBean0.set("itemtype",itemtype);
		lazyDynaBean0.set("codesetid",codesetid);
		lazyDynaBean0.set("fieldsetid",fieldsetid);
		lazyDynaBean0.set("itemdesc",desc);
		return lazyDynaBean0;
	}
	public ArrayList getDbnameList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql=" select dbname,pre from dbname order by dbid";
			ContentDAO dao =new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("pre"),rs.getString("dbname")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取设置的业务模板的列表
	 * @param ids
	 * @return
	 */
	public ArrayList getBusinessTemplate(String ids)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(ids==null|| "".equals(ids)) {
                return list;
            }
			String sql = " select tabid,name from template_table where tabid in("+ids+")";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("tabid"),rs.getString("name")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList getFieldList(ArrayList beanList)
	{
		ArrayList list=new ArrayList();
		for(int i=0;i<beanList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)beanList.get(i);
			String itemid=(String)abean.get("itemid");
			if("a0100".equals(itemid)) {
                continue;
            }
			String itemtype=(String)abean.get("itemtype");
			String codesetid=(String)abean.get("codesetid");
			String desc=(String)abean.get("itemdesc");
	
			FieldItem fieldItem1=new FieldItem();
			fieldItem1.setItemid(itemid);
			fieldItem1.setItemtype(itemtype);
			fieldItem1.setCodesetid(codesetid);
			fieldItem1.setItemdesc(desc);
			list.add(fieldItem1);
		}
		
		
		return list;
		
		
	}
	
	private ArrayList testTemplatAdvance=new ArrayList();//采用高级测评相关参数
	
	
	
	public ArrayList getTestTemplatAdvance() {
		return testTemplatAdvance;
	}


	public void setTestTemplatAdvance(ArrayList testTemplatAdvance) {
		this.testTemplatAdvance = testTemplatAdvance;
	}


	/**
	 * 取得 员工录用模块 信息列表中 列的信息。
	 * @param isMailField   是否有 email 指标
	 * @param isPhoneField  是否有 联系电话指标
	 * @param dbname        库前缀
	 * @return
	 */public ArrayList getColumnList(String isMailField,String isPhoneField,String dbname,String flag)
	{
		ArrayList columnList=new ArrayList();
		
		try
		{
			String resume_state_field="";
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();			
			if(map!=null&&map.get("resume_state")!=null) {
                resume_state_field=(String)map.get("resume_state");
            }
			String schoolPosition="";
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0) {
                schoolPosition=(String)map.get("schoolPosition");
            }
			if("1".equals(flag))
			{
				columnList.add(getLazyDynaBean("a0100","A","0","A01",""));	   //人员编号			
			}
			columnList.add(getLazyDynaBean(resume_state_field,"A","36",dbname+"A01",ResourceFactory.getProperty("label.zp_resource.status")));	   //人员状态	
			columnList.add(getLazyDynaBean("a0101","A","0",dbname+"A01",ResourceFactory.getProperty("hire.employActualize.name")));	   //人员姓名		
			columnList.add(getLazyDynaBean("Z0321","A","UN","Z03",ResourceFactory.getProperty("hire.interviewExamine.interviewUnit")));	   //应聘单位	
			columnList.add(getLazyDynaBean("departId","A","UM","org",ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment")));	   //应聘部门	
			
			if("1".equals(flag))
			{
				if(schoolPosition!=null&&schoolPosition.length()>0)
				{
					columnList.add(getLazyDynaBean("z0311","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.majorposition")));	
				}
				else
				{
					columnList.add(getLazyDynaBean("z0311","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.position")));	
				}
		    	//columnList.add(getLazyDynaBean("z0311","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")+"|"+ResourceFactory.getProperty("hire.employActualize.interviewProfessional")));	   //应聘单位
			}else {
                columnList.add(getLazyDynaBean("z0311","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")));
            }
			
			columnList.add(getLazyDynaBean("z05_state","A","36","Z05",ResourceFactory.getProperty("hire.interviewEvaluating.isNotice")));	   //是否通知	
			columnList.add(getLazyDynaBean("z0513","D","0","Z05",ResourceFactory.getProperty("hire.personnelEmploy.registerTime")));	   //报到时间	
			columnList.add(getLazyDynaBean("z0503","A","0","Z05","面试地点"));	 //面试地点
			columnList.add(getLazyDynaBean("z0509","D","0","Z05","面试时间"));	 //面试时间 
			ArrayList continueList=new ArrayList();
			if(this.testTemplatAdvance.size()>0){//如果设置了高级测评方式,就全部使用高级测评的字段显示成绩数据，而不再使用考核总分
				for(int i=0;i<this.testTemplatAdvance.size();i++){
					HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
					String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					if(continueList.contains(score_item)){
						continue;
					}
					FieldItem item=DataDictionary.getFieldItem(score_item);
					if(item!=null){
						continueList.add(score_item);
						columnList.add(getLazyDynaBean(score_item,item.getItemid(),item.getCodesetid(),item.getFieldsetid(),item.getItemdesc()));
					}
				}
			}else{
				columnList.add(getLazyDynaBean("score","A","0","zp_test_result","考核分数"));	   //考核分数
			}
			
			
			String mailFieldSet="";
			String phoneFieldSet="";
			if(!"#".equals(isMailField)||!"#".equals(isPhoneField))
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet = null;
				try
				{
					rowSet=dao.search("select * from fielditem where itemid='"+isMailField+"' or itemid='"+isPhoneField+"'");
					while(rowSet.next())
					{
						String itemid=rowSet.getString("itemid");
						String fieldsetid=rowSet.getString("fieldsetid");
						if(itemid.equals(isMailField))
						{
							mailFieldSet=fieldsetid;
						}
						if(itemid.equals(isPhoneField))
						{
							phoneFieldSet=fieldsetid;
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		/*	InterviewExamine interviewExamine=new InterviewExamine(this.conn);
			LazyDynaBean a_lazyDynaBean=null;
			for(Iterator t=interviewExamine.getParameterFields().iterator();t.hasNext();)
			{
				a_lazyDynaBean=(LazyDynaBean)t.next();
				columnList.add(a_lazyDynaBean);
				
			}*/
			if(!"#".equals(isMailField)&&!"".equals(mailFieldSet)) {
                columnList.add(getLazyDynaBean(isMailField,"A","0",dbname+mailFieldSet,ResourceFactory.getProperty("selfservice.param.otherparam.email_title")));
            }
			if(!"#".equals(isPhoneField)&&!"".equals(phoneFieldSet)) {
                columnList.add(getLazyDynaBean(isPhoneField,"A","0",dbname+phoneFieldSet,ResourceFactory.getProperty("selfservice.param.otherparam.phone_title")));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return columnList;
	}
	
	
	
	 
	/**
	 * 取得 员工录用 信息记录
	 * @param columnsList
	 * @param dbname
	 * @return
	 */
	public ArrayList getPersonnelEmployList(String codeid,ArrayList columnsList,String dbname,String extendSql,String orderSql) throws GeneralException
	{
		ArrayList list=new ArrayList();
		
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
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
			
			
			String sql=getSql(codeid,columnsList,dbname,extendSql,orderSql);
			rowSet=dao.search(sql);
			LazyDynaBean lazyDynaBean0=null;
			while(rowSet.next())
			{
				lazyDynaBean0=new LazyDynaBean();
				String Z0336=rowSet.getString("Z0336");
				LazyDynaBean tempBean=null;
				boolean flag=false;
				boolean zpoidflag=false;
				for(Iterator t=columnsList.iterator();t.hasNext();)
				{
					tempBean=(LazyDynaBean)t.next();
					String itemid=(String)tempBean.get("itemid");
					if("z0501".equalsIgnoreCase(itemid))
					{
						flag=true;
					}
					if("z0301".equalsIgnoreCase(itemid))
					{
						zpoidflag=true;
					}
					String itemtype=(String)tempBean.get("itemtype");
					String codesetid=(String)tempBean.get("codesetid");
					if((!"D".equalsIgnoreCase(itemtype)&&rowSet.getString(itemid)!=null)||("D".equalsIgnoreCase(itemtype)&&rowSet.getString(itemid)!=null))//看不明白这样的判断是为了什么 xcs 2014-8-6 无论是不是时间不都进去了么？
					{
						if("D".equalsIgnoreCase(itemtype))
						{
							String value="";
							String	context="";
							if(rowSet.getString(itemid)!=null)
							{
								value=rowSet.getString(itemid);
							}
                            if("z0513".equalsIgnoreCase(itemid))
                            {
				    		    context="<input type='text' class='TEXT_NB1 common_border_color'  size='20' onchange=\"save('D',this)\" name='"+rowSet.getString("z0501")+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,true)'  value='"+value+"' />";
                            }else
                            {
                            	context=value;
                            }
                            
							lazyDynaBean0.set(itemid,context);
							
						}
						else if("0".equals(codesetid))
						{
							if("a0100".equalsIgnoreCase(itemid)){
								lazyDynaBean0.set("a0100_canshu",PubFunc.encryption(rowSet.getString(itemid)));
								lazyDynaBean0.set("a0100",rowSet.getString(itemid));
							}else if("score".equalsIgnoreCase(itemid)&&rowSet.getString(itemid)!=null)
							{
								lazyDynaBean0.set(itemid,PubFunc.round(rowSet.getString(itemid),2));
							}
							else{
								if(this.testTemplatAdvance.size()>0){//高级测评表中的涉及到z05中字段都数数字型的
									 for(int i=0;i<testTemplatAdvance.size();i++){
										 HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
										 String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
										 if(score_item.equals(itemid)){
											 Number value=(Number) rowSet.getObject(itemid);
											 if(value==null||value.doubleValue()==0){
												 lazyDynaBean0.set(itemid,"");
											 }else{
												 lazyDynaBean0.set(itemid,rowSet.getString(itemid));
											 }
											 break;
										 }
									 }
									 if(lazyDynaBean0.get(itemid)==null){//如果高级测评里面没有涉及到该指标那么需要走原有的设置
										 lazyDynaBean0.set(itemid,rowSet.getString(itemid));
									 }
								}else{
									lazyDynaBean0.set(itemid,rowSet.getString(itemid));
								}
								
							}
						}
						else
						{
							if(Z0336!=null&& "01".equals(Z0336)&& "z0311".equalsIgnoreCase(itemid)&&hireMajor!=null&&hireMajor.length()>0)
							{
								if(hireMajorIsCode) {
                                    lazyDynaBean0.set(itemid,AdminCode.getCodeName(hireMajoritem.getCodesetid(),rowSet.getString(itemid)));
                                } else {
                                    lazyDynaBean0.set(itemid,rowSet.getString(itemid));
                                }
							}
							else {
                                lazyDynaBean0.set(itemid,AdminCode.getCodeName(codesetid,rowSet.getString(itemid)));
                            }
						}
					}
					else
					{
						
						if("D".equalsIgnoreCase(itemtype))
						{
							String	context="";
							if("z0513".equalsIgnoreCase(itemid))
							{
								context="<input type='text' class='TEXT_NB1 common_border_color'  size='20' onchange=\"save('D',this)\" name='"+rowSet.getString("z0501")+"'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,true)'  value='' />";
								lazyDynaBean0.set(itemid,context);	
							}
							else {
                                lazyDynaBean0.set(itemid,"");
                            }
							
						}
						else{
								lazyDynaBean0.set(itemid,"");
						}
							
					}
				}
				if(!flag)
				{
					lazyDynaBean0.set("z0501", rowSet.getString("z0501"));
				}
				if(!zpoidflag)
				{
					lazyDynaBean0.set("z0301_canshu", PubFunc.encryption(rowSet.getString("z0301")));
					lazyDynaBean0.set("z0301",rowSet.getString("z0301"));
				}
				list.add(lazyDynaBean0);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	public String getSql(String codeid,ArrayList columnsList,String dbname,String extendSql,String orderSql) throws GeneralException
	{
		
		HashSet tempLateIDSet=new HashSet();
		String resume_state_field="";
		String hireMajor="";
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }
			tempLateIDSet=(HashSet)map.get("testTemplateID");
			if(map!=null&&map.get("resume_state")!=null&&((String)map.get("resume_state")).trim().length()>0) {
                resume_state_field=(String)map.get("resume_state");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		StringBuffer zpTestResult=new StringBuffer("");
		int num=0;
		if(tempLateIDSet==null||tempLateIDSet.size()==0) {
            throw GeneralExceptionHandler.Handle(new Exception("配置参数模块没有定义评测表"));
        }
		for(Iterator t=tempLateIDSet.iterator();t.hasNext();)
		{
			num++;
			zpTestResult.append(" union  select a0100,score from zp_test_result_"+(String)t.next()+" where interview=0");
			
		}

		StringBuffer sql=new StringBuffer("");
		StringBuffer sql_select=new StringBuffer("select "+dbname+"A01.a0100,"+dbname+"A01."+resume_state_field+" ,"+dbname+"A01.a0101,z0301,Z0321");
		sql_select.append(",z03.z0325 departId,");
		if(hireMajor.length()==0) {
            sql_select.append("z0311");
        } else
		{
			sql_select.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else z03.z0311 end as  z0311");
		}
		if(this.testTemplatAdvance.size()>0){//如果采用的了高级测评方式 就不需要查询 zp_test_result.score
			sql_select.append(",z03.Z0336,z05.state z05_state,z0501,z0503");
			ArrayList continueList = new ArrayList();
				for(int n=0;n<this.testTemplatAdvance.size();n++){
					HashMap advanceMap=(HashMap) testTemplatAdvance.get(n);
					String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					if(!continueList.contains(score_item)){
						sql_select.append(",z05"+"."+score_item);
						continueList.add(score_item);
					}else{
						continue;
					}
				}
		}else{
			sql_select.append(",z03.Z0336,z05.state z05_state,z0501,z0503,zp_test_result.score");
		}
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
    		sql_select.append(","+Sql_switcher.dateToChar("z0509", "YYYY-MM-DD HH24:MI")+ "as z0509");
    		sql_select.append(","+Sql_switcher.dateToChar("z0513","YYYY-MM-DD HH24:MI")+ "as z0513");
		}
		else if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		{
			sql_select.append(",convert(varchar(16),z05.z0509,20) as z0509");
    		sql_select.append(",convert(varchar(16),z05.z0513,20) as z0513");
		}
    		
		StringBuffer sql_from=new StringBuffer(" from zp_pos_tache zpt left join  "+dbname+"A01  on zpt.a0100="+dbname+"A01.a0100");
		sql_from.append(" left join z03 on zpt.zp_pos_id=Z03.Z0301");
		//   sql_from.append(" left join z01 on z03.z0101=z01.z0101");
		sql_from.append(" left join Z05 on zpt.A0100=Z05.A0100");
		if(this.testTemplatAdvance.size()<=0){//如果采用的了高级测评方式 就不需要查询 zp_test_result.score
			sql_from.append(" left join ( "+zpTestResult.substring(6)+" ) zp_test_result on zpt.a0100=zp_test_result.a0100 ");	
		}
		sql_from.append(" left join  (select * from organization where codeitemid in (select distinct b.z0311 from zp_pos_tache a,z03 b where a.zp_pos_id=b.z0301)) org on z03.z0311=org.codeitemid" );					   
		StringBuffer sql_where=new StringBuffer("");
		sql_where.append(" where "+dbname+"A01."+resume_state_field+" like '4%' "); // 换成模糊查找的方式，以应对将来的变化 by 刘蒙
		if(!"0".equals(codeid))
		{
			if(codeid.indexOf("`")!=-1)
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				for(int i=0;i<temps.length;i++)
				{
					
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
    				tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				
				}
			//	sql_where.append(" and ("+tempSql.substring(3)+") ");
				sql_where.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				
			}else
			{
	    	//	sql_where.append(" and z03.z0311 like '"+codeid+"%'");
				String _str=Sql_switcher.isnull("z03.z0336","''");
				sql_where.append(" and ( ( z03.z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) "); 
				 
			}
		}
		sql_where.append(" and z03.z0319='04' and zpt.resume_flag='12' ");
		
		HashMap tabMap=new HashMap();
		tabMap.put(dbname+"A01","1");
		
		String fieldSetId="";
		String fielditemid="";
		String itemtype="";
		for(int i=11;i<columnsList.size();i++)
		{
			LazyDynaBean lazyDynaBean=(LazyDynaBean)columnsList.get(i);
			fieldSetId=(String)lazyDynaBean.get("fieldsetid");
			fielditemid=(String)lazyDynaBean.get("itemid");
			boolean continueFlag=false;
			if(this.testTemplatAdvance.size()>0){
				for(int n=0;n<this.testTemplatAdvance.size();n++){
					HashMap advanceMap=(HashMap) testTemplatAdvance.get(n);
					String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					if(score_item.equals(fielditemid)){
						continueFlag=true;
						break;//只需要查询这个字段一次就好了
					}
				}
			}
			if(continueFlag){//如果高级测评表里面包含这个字段那么就进行下一次循环
				continue;
			}
			if(fieldSetId.length() >= dbname.length()&&dbname.equalsIgnoreCase(fieldSetId.substring(0,dbname.length()))){
				sql_select.append(","+fieldSetId+"."+fielditemid);
			}
			else{
				sql_select.append(","+dbname+fieldSetId+"."+fielditemid);
			}
			if(tabMap.get(fieldSetId)==null)
			{
				String tempName="";
				if(fieldSetId.length() >= dbname.length()&&dbname.equalsIgnoreCase(fieldSetId.substring(0,dbname.length()))){
					tempName = fieldSetId;
				}else{ 
					tempName = dbname+fieldSetId;
				}
				sql_from.append(" left join ");		
				sql_from.append("(SELECT * FROM ");
				sql_from.append(tempName);
				sql_from.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
				sql_from.append(tempName);
				sql_from.append(" B WHERE ");
				sql_from.append(" A.A0100=B.A0100  )) ");
				sql_from.append(tempName);
				sql_from.append(" on zpt.A0100="+tempName+".A0100");
				tabMap.put(fieldSetId,"1");
			}		
		}
		if(extendSql!=null&&extendSql.trim().length()>0)
		{
			sql_where.append(" and "+extendSql);
		}
		if(orderSql!=null&&orderSql.trim().length()>0)
		{
			sql_where.append(" "+orderSql);
		}
		sql.append(sql_select.toString());
		/********zzk sql库  面试安排、面试通知按时间倒序排列  null排前面*******/
		if (Sql_switcher.searchDbServer()!=Constant.ORACEL) {
            sql.append(", case when z05.z0509 is null  then '9999-12-31 14:21:38.000' else z05.z0509 end as Z0509A ");
        }
		sql.append(sql_from.toString());
		sql.append(sql_where.toString());
		return sql.toString();
	}
	 
	public String getSql2(String codeid,ArrayList columnsList,String dbname,String extendSql,String orderSql) throws GeneralException
	{
		
		HashSet tempLateIDSet=new HashSet();
		String resume_state_field="";
		String hireMajor="";
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("hireMajor")!=null) {
                hireMajor=(String)map.get("hireMajor");  //招聘专业指标
            }
			tempLateIDSet=(HashSet)map.get("testTemplateID");
			if(map!=null&&map.get("resume_state")!=null&&((String)map.get("resume_state")).trim().length()>0) {
                resume_state_field=(String)map.get("resume_state");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		StringBuffer zpTestResult=new StringBuffer("");
		int num=0;
		if(tempLateIDSet==null||tempLateIDSet.size()==0) {
            throw GeneralExceptionHandler.Handle(new Exception("配置参数模块没有定义评测表"));
        }
		for(Iterator t=tempLateIDSet.iterator();t.hasNext();)
		{
			num++;
			zpTestResult.append(" union  select a0100,score from zp_test_result_"+(String)t.next()+" where interview=0");
			
		}

		StringBuffer sql=new StringBuffer("");
		StringBuffer sql_select=new StringBuffer("select "+dbname+"A01.a0100,"+dbname+"A01."+resume_state_field+" ,"+dbname+"A01.a0101,Z0321,z03.z0325 departId,");
		if(hireMajor.length()==0) {
            sql_select.append("z0311");
        } else
		{
			sql_select.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else z03.z0311 end as  z0311");
		}
		if(this.testTemplatAdvance.size()>0){//如果采用的了高级测评方式 就不需要查询 zp_test_result.score
			sql_select.append(",z03.Z0336,z05.state z05_state,z0501,z0503,z0509,z0513");
			ArrayList continueList = new ArrayList();
				for(int n=0;n<this.testTemplatAdvance.size();n++){
					HashMap advanceMap=(HashMap) testTemplatAdvance.get(n);
					String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					if(!continueList.contains(score_item)){
						sql_select.append(",z05"+"."+score_item);
						continueList.add(score_item);
					}else{
						continue;
					}
				}
		}else{
			sql_select.append(",z03.Z0336,z05.state z05_state,z0501,z0503,z0509,z0513,zp_test_result.score");
		}
			 		
		StringBuffer sql_from=new StringBuffer(" from zp_pos_tache zpt left join  "+dbname+"A01  on zpt.a0100="+dbname+"A01.a0100");
							   sql_from.append(" left join z03 on zpt.zp_pos_id=Z03.Z0301");
							//   sql_from.append(" left join z01 on z03.z0101=z01.z0101");
							   sql_from.append(" left join Z05 on zpt.A0100=Z05.A0100");
								if(this.testTemplatAdvance.size()<=0){//如果采用的了高级测评方式 就不需要查询 zp_test_result.score
									sql_from.append(" left join ( "+zpTestResult.substring(6)+" ) zp_test_result on zpt.a0100=zp_test_result.a0100 ");	
								}
							//sql_from.append(" left join ( "+zpTestResult.substring(6)+" ) zp_test_result on zpt.a0100=zp_test_result.a0100 ");
							   sql_from.append(" left join  (select * from organization where codeitemid in (select distinct b.z0311 from zp_pos_tache a,z03 b where a.zp_pos_id=b.z0301)) org on z03.z0311=org.codeitemid" );					   
		StringBuffer sql_where=new StringBuffer("");
		sql_where.append(" where  ( "+dbname+"A01."+resume_state_field+"='41' or "+dbname+"A01."+resume_state_field+"='42' or "+dbname+"A01."+resume_state_field+"='43' ) ");
		if(!"0".equals(codeid))
		{	
			
		//	sql_where.append(" and z03.z0311 like '"+codeid+"%'");
			
			if(codeid.indexOf("`")!=-1)
			{
				String[] temps=codeid.split("`");
				StringBuffer tempSql=new StringBuffer("");
    			StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
			 	String _str=Sql_switcher.isnull("z03.z0336","''");
				for(int i=0;i<temps.length;i++)
				{
					tempSql.append(" or z03.z0311 like '"+temps[i]+"%' ");
    				tempSql2.append(" or z03.z0321 like '"+temps[i]+"%'");
    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%'");
				
				}
			//	sql_where.append(" and ("+tempSql.substring(3)+") ");
				sql_where.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
				
			}else
			{
	    	//	sql_where.append(" and z03.z0311 like '"+codeid+"%'");
				String _str=Sql_switcher.isnull("z03.z0336","''");
				sql_where.append(" and ( ( z03.z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) "); 
				
			}
		}
		sql_where.append(" and z03.z0319='04' and zpt.resume_flag='12' ");
		
		HashMap tabMap=new HashMap();
		tabMap.put(dbname+"A01","1");
		
		String fieldSetId="";
		String fielditemid="";
		String itemtype="";
		for(int i=11;i<columnsList.size();i++)
		{
			LazyDynaBean lazyDynaBean=(LazyDynaBean)columnsList.get(i);
			fieldSetId=(String)lazyDynaBean.get("fieldsetid");
			fielditemid=(String)lazyDynaBean.get("itemid");
			boolean continueFlag=false;
			if(this.testTemplatAdvance.size()>0){
				for(int n=0;n<this.testTemplatAdvance.size();n++){
					HashMap advanceMap=(HashMap) testTemplatAdvance.get(n);
					String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
					if(score_item.equals(fielditemid)){
						continueFlag=true;
						break;//只需要查询这个字段一次就好了
					}
				}
			}
			if(continueFlag){//如果高级测评表里面包含这个字段那么就进行下一次循环
				continue;
			}
			if(fieldSetId.length() >= dbname.length()&&dbname.equalsIgnoreCase(fieldSetId.substring(0,dbname.length()))){
				sql_select.append(","+fieldSetId+"."+fielditemid);
			}
			else{
				sql_select.append(","+dbname+fieldSetId+"."+fielditemid);
			}
			if(tabMap.get(fieldSetId)==null)
			{
				String tempName="";
				if(fieldSetId.length() >= dbname.length()&&dbname.equalsIgnoreCase(fieldSetId.substring(0,dbname.length()))){
					tempName = fieldSetId;
				}else{ 
					tempName = dbname+fieldSetId;
				}
				sql_from.append(" left join ");		
				sql_from.append("(SELECT * FROM ");
				sql_from.append(tempName);
				sql_from.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
				sql_from.append(tempName);
				sql_from.append(" B WHERE ");
				sql_from.append(" A.A0100=B.A0100  )) ");
				sql_from.append(tempName);
				sql_from.append(" on zpt.A0100="+tempName+".A0100");
				tabMap.put(fieldSetId,"1");
			}		
		}
		if(extendSql!=null&&extendSql.trim().length()>0)
		{
			sql_where.append(" and "+extendSql);
		}
		if(orderSql!=null&&orderSql.trim().length()>0)
		{
			sql_where.append(" "+orderSql);
		}
		sql.append(sql_select.toString());
		/********zzk sql库  面试安排、面试通知按时间倒序排列  null排前面*******/
		if (Sql_switcher.searchDbServer()!=Constant.ORACEL) {
            sql.append(", case when z05.z0509 is null  then '9999-12-31 14:21:38.000' else z05.z0509 end as Z0509A ");
        }
		sql.append(sql_from.toString());
		sql.append(sql_where.toString());
		return sql.toString();
	}
	 
	 
	/**
	 * 设置员工状态
	 * @param a0100s
	 * @param state
	 */
	public void setState(String a0100s,ArrayList list,String state,String dbname,String toDbname,List infoSetList)throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if("21".equals(state)|| "22".equals(state)|| "23".equals(state)) {
                dao.update("update z05 set state='"+state+"' where A0100 in ("+a0100s+")");
            }
			if("43".equals(state))  //录用
			{
				String resume_state_field="";
				ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
				HashMap map=bo2.getAttributeValues();
				if(map!=null&&map.get("resume_state")!=null) {
                    resume_state_field=(String)map.get("resume_state");
                }
				HashMap map2 = this.getExistPerson(dbname,resume_state_field);
				dao.update("update "+dbname+"A01 set "+resume_state_field+"='"+state+"' where A0100 in ("+a0100s+")");
				importDbase(map2,dbname,toDbname,infoSetList,list);  //移库
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}
	 
	 
	
	
	
	
	 /**
	  * 移库
	  * @param dbname
	  * @param toDbname
	  * @param infoSetList
	  * @param list
	  */
	public void importDbase(HashMap map,String dbname,String toDbname,List fieldsetlist,ArrayList list) throws  GeneralException 
	{
		 
		  try
		  {
			    StringBuffer strsql=new StringBuffer();
			    ContentDAO dao = new ContentDAO(this.conn);
				StringBuffer fieldstr=new StringBuffer();
                
				if(!list.isEmpty()){
				
					for(int i=0;i<list.size();i++)
					{				
						LazyDynaBean rec=(LazyDynaBean)list.get(i);
						String A0100=rec.get("a0100").toString();
						if(map.containsKey(A0100)) {
                            continue;
                        }
						String toTable=toDbname+"A01";
						
						String z0321="";
						String z0325="";  
						String z0311="";
						String ori_nbase="";
						RowSet rowSet=dao.search("select z03.z0321,z03.Z0325,z03.z0311,zp_pos_tache.nbase from zp_pos_tache,z03 where z03.z0301=zp_pos_tache.zp_pos_id  and zp_pos_tache.resume_flag='12'  and a0100='"+A0100+"'");
						if(rowSet.next())
						{
							z0321=rowSet.getString("z0321");
							z0325=rowSet.getString("z0325");
							z0311=rowSet.getString("z0311");
							ori_nbase=rowSet.getString("nbase");
						}
						
						if(ori_nbase==null||ori_nbase.trim().length()==0||!ori_nbase.equals(toDbname))
						{
							String toA0100 = getToA0100(conn, A0100, toTable,dbname);	
							UpdateExistPersons(dao,rec.get("z0311").toString());
							if(!fieldsetlist.isEmpty()){
								for(int j=0;j<fieldsetlist.size();j++)
								{
									FieldSet fieldset=(FieldSet)fieldsetlist.get(j);
									List fields=DataDictionary.getFieldList(fieldset.getFieldsetid(),Constant.EMPLOY_FIELD_SET);
									fieldstr.delete(0,fieldstr.length());
									if(fields!=null&&!fields.isEmpty())
									{
									  for(int n=0;n<fields.size();n++)
									  {
									  	FieldItem fielditem=(FieldItem)fields.get(n);
									  	fieldstr.append("," + fielditem.getItemid());
									  }
									 }
									strsql=transferInformation(dbname+ fieldset.getFieldsetid(),toDbname + fieldset.getFieldsetid(),A0100,toA0100,fieldset.getFieldsetid(),fieldstr.toString());								
									dao.update(strsql.toString());
									strsql.setLength(0);
								
								}
								//插入 职位 单位 部门信息 
								{
									
									dao.update("update "+toDbname+"A01 set b0110='"+z0321+"' , e0122='"+z0325+"',e01a1='"+z0311+"' where a0100='"+toA0100+"'");									
									updatePositionActualNum(rowSet.getString("z0311"));
									
								}
							}
						}
						else
						{
							RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
							String email_field=vo.getString("str_value");
							rowSet=dao.search("select a0100 from "+ori_nbase+"A01 where  "+email_field+"=(select "+email_field+" from "+dbname+"A01 where a0100='"+A0100+"')");
							if(rowSet.next())
							{
								dao.update("update "+ori_nbase+"A01 set b0110='"+z0321+"' , e0122='"+z0325+"',e01a1='"+z0311+"' where a0100='"+rowSet.getString(1)+"'");									
							}
							else {
                                throw GeneralExceptionHandler.Handle(new Exception("应聘库中的邮件地址与原库中的邮件地址无法对应,不能转库!"));
                            }
						}
					}	
				}
		      }catch(Exception e)
			  {
		      		e.printStackTrace();
		      		throw GeneralExceptionHandler.Handle(e);
		      }
	}
	 
	public HashMap getExistPerson(String nbase,String resume_state_field)
	{
		HashMap map = new HashMap();
		try
		{
			ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
			HashMap map2=bo2.getAttributeValues();
			if(resume_state_field!=null&&!"".equals(resume_state_field))
			{
				String sql = "select a0100 from "+nbase+"a01 where "+resume_state_field+"='43'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					map.put(rs.getString("a0100"),"1");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	//跟改职位实际人数
	public void updatePositionActualNum(String e01a1)
	{
		 ContentDAO dao = new ContentDAO(this.conn);
		 RowSet recset=null;	
		 try
		 {
			    String actualNumberField="";  //实有人数指标				
			    String fieldSetid="";         //子集id
			    recset=dao.search("select * from constant where constant='PS_WORKOUT'");
				if(recset.next())
				{
					//K01|K0111,K0114
					String str_value=recset.getString("str_value");
					String[] temp=str_value.split("\\|");
					fieldSetid=temp[0];
					String[] temp2=temp[1].split(",");
					actualNumberField=temp2[1];		
				}
				if("K01".equalsIgnoreCase(fieldSetid))
				{
					if(actualNumberField!=null&&!"#".equals(actualNumberField)&&actualNumberField.trim().length()>0)
					{
						FieldItem item= DataDictionary.getFieldItem(actualNumberField.toLowerCase());
					      if(item==null) {
                              return;
                          }
						recset=dao.search("select "+actualNumberField+" from K01 where e01a1='"+e01a1+"'");
						if(recset.next())
						{
							float num=recset.getFloat(actualNumberField);
							dao.update("update K01 set "+actualNumberField+"="+(num+1)+" where e01a1='"+e01a1+"'");
						}
					}
				}
				else
				{
					if(actualNumberField!=null&&!"#".equals(actualNumberField)&&actualNumberField.trim().length()>0)
					{
						FieldItem item= DataDictionary.getFieldItem(actualNumberField.toLowerCase());
					      if(item==null) {
                              return;
                          }
						recset=dao.search("SELECT "+actualNumberField+",i9999 FROM "+fieldSetid+" where e01a1='"+e01a1+"' and i9999=(SELECT max(i9999) FROM "+fieldSetid+" where e01a1='"+e01a1+"')");
						if(recset.next())
						{
							float num=recset.getFloat(actualNumberField);
							dao.update("update "+fieldSetid+" set "+actualNumberField+"="+(num+1)+" where e01a1='"+e01a1+"' and i9999="+recset.getInt("i9999"));
						
						}
					}
				}
				
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	}
	
	 
	public StringBuffer transferInformation(String fromTable,String toTable,String fromNumber,String toNumber,String tabletype,String fieldstr){
		boolean flag = false;
		StringBuffer strsql =new  StringBuffer();
		try {
			if ("A01".equals(tabletype)) {
				try{
				    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
					}
				catch(Exception e)
				{
				}		
				
				String strA0000 = getA0000(toTable,conn);
				strsql.append("insert into ");
				strsql.append(toTable);
				strsql.append("(A0000,A0100,State,CreateUserName,CreateTime,ModUserName,ModTime,UserName,UserPassword");
				strsql.append(fieldstr);
				strsql.append(") select ");
				strsql.append(strA0000);
				strsql.append(",'");
				strsql.append(toNumber);
				strsql.append("',State,CreateUserName,CreateTime,ModUserName,ModTime,UserName,UserPassword");
				strsql.append(fieldstr);
				strsql.append(" from ");
				strsql.append(fromTable);
				strsql.append(" where A0100='" + fromNumber + "'");				
			} else if ("A00".equals(tabletype)) {
				try{
				    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
					}
				catch(Exception e)
				{
				}					
				strsql.append("insert into ");
				strsql.append(toTable);
				strsql.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
				strsql.append(toNumber);
				strsql.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
				strsql.append(fromTable);
				strsql.append(" where A0100='" + fromNumber + "'");			
			} else {
				try{
				    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
					}
				catch(Exception e)
				{
				}	
				strsql.append("insert into ");
				strsql.append(toTable);
				strsql.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
				strsql.append(fieldstr);
				strsql.append(") select '");
				strsql.append(toNumber);
				strsql.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
				strsql.append(fieldstr);
				strsql.append(" from ");
				strsql.append(fromTable);
				strsql.append(" where A0100='" + fromNumber + "'");
				
			}				
			flag = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return strsql;
	}
	
	
	
	private String getA0000(String toTable,Connection conn) {
		String strsql = "select max(A0000) as a0000 from " + toTable;
		int userId=10;			
		try
		{
			List rs=ExecuteSQL.executeMyQuery(strsql,conn);
			if(!rs.isEmpty())
			{
				DynaBean rec=(DynaBean)rs.get(0); 
				
				if(rec.get("a0000")!=null&&!"".equals(rec.get("a0000"))) {
                    userId=Integer.parseInt(rec.get("a0000").toString()) + 10;
                }
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf(userId);		
	}
	
	
	
	
	
	
	/**
	 * @param stmt
	 * @param A0100
	 * @param toTable
	 * @param zpk_dbname 招聘库前缀
	 * @return
	 * @throws SQLException
	 */
	//获得移库的目标id号
	private String getToA0100(Connection conn, String A0100, String toTable,String zpk_dbname) throws  GeneralException  {
		RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
		String emailField=vo.getString("str_value");
		String toA0100="";
		String tempNumber="";
		try
		{
			if("#".equals(vo.getString("str_value"))) {
                throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标,运行错误!"));
            }
			String tempsql ="select a0100 from "+toTable+" where "+emailField+"=(select "+emailField+" from "+zpk_dbname+"a01 where a0100='"+A0100+"')";
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(tempsql);			
//			if (rs.next()) {
//				tempNumber=rs.getString("a0100");	
//			}	
//			else {
//				String strsql = "select max(A0100) as a0100 from " + toTable + " order by A0100";
//				rs=dao.search(strsql);
//				int userPlace = 0;
//				if (rs.next()) {
//					userPlace =Integer.parseInt(rs.getString("a0100")==null?"0":rs.getString("a0100")) + 1;
//				} else{
//					userPlace = 1;
//				}
//				tempNumber = Integer.toString(userPlace);
//				for (int n = 0; n < 8 - (Integer.toString(userPlace)).length(); n++){
//					tempNumber = "0" + tempNumber;
//				}
//			}
			/**zzk 不删除目标库邮箱一致数据  直接新增操作**/
			String strsql = "select max(A0100) as a0100 from " + toTable + " order by A0100";
			rs=dao.search(strsql);
			int userPlace = 0;
			if (rs.next()) {
				userPlace =Integer.parseInt(rs.getString("a0100")==null?"0":rs.getString("a0100")) + 1;
			} else{
				userPlace = 1;
			}
			tempNumber = Integer.toString(userPlace);
			for (int n = 0; n < 8 - (Integer.toString(userPlace)).length(); n++){
				tempNumber = "0" + tempNumber;
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		toA0100=tempNumber;
		return  toA0100;
	}
	
	
	
	/**
	 * @param conn
	 * @param posexistpersons
	 * @param 
	 * @return
	 * @throws SQLException
	 */
	//改变编制的实际人数id号
	private void UpdateExistPersons(ContentDAO dao,String pos_id) throws SQLException {	
		/*更改实有人员数*/
    	RecordVo rv= ConstantParamter.getRealConstantVo("PS_WORKOUT");  
		if(rv == null){
			return;
		}else
		{
			StringBuffer sqlstr=new StringBuffer();
			String posWork = rv.getString("str_value");
			int strIndex = posWork.indexOf("|");
			if(strIndex != -1){
				 String setstr = posWork.substring(0,strIndex);
				 int fieldIndex = posWork.indexOf(",");
				 if(fieldIndex != -1){
				      String lastfieldstr = posWork.substring(fieldIndex+1,posWork.length());
				      if("#".equals(lastfieldstr)) {
                          return;
                      }
				      FieldItem item= DataDictionary.getFieldItem(lastfieldstr.toLowerCase());
				      if(item==null) {
                          return;
                      }
			   	   	  RowSet frowset=dao.search("select " + lastfieldstr + " from " + setstr + " where e01a1='" + pos_id + "'");
			   	   	  sqlstr.delete(0,sqlstr.length());
			   	      sqlstr.append("update ");
			   	      sqlstr.append(setstr);
			   	      sqlstr.append(" set ");
			   	      sqlstr.append(lastfieldstr);
			   	      sqlstr.append("=");
			   	      if( frowset.next()) {
                          if(frowset.getString(lastfieldstr)!=null)
                          {
sqlstr.append(lastfieldstr);
sqlstr.append(" + ");
}
                      }
			   	      sqlstr.append(1);
			   	      sqlstr.append(" where e01a1='");
			   	      sqlstr.append(pos_id);
			   	      sqlstr.append("'");
			   	      if(setstr.indexOf("01") == -1)
			   	      {
			   	        sqlstr.append(" and i9999=(select max(i9999) as i9999 from ");
			   	        sqlstr.append(setstr);
			   	        sqlstr.append(" where e01a1='");
			   	        sqlstr.append(pos_id);
			   	        sqlstr.append("')");
			   	      }
			   	      dao.update(sqlstr.toString());				   	  
			   	   }	
          }
	}
 }
	
	
	
	
	
	
}
