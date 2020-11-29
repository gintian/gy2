package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class InterviewExamine {
	private Connection conn=null;
	
	public InterviewExamine(Connection conn)
	{
		this.conn=conn;
	}
	
	
	
	private LazyDynaBean getLazyDynaBean(String itemid,String itemtype,String codesetid,String fieldsetid,String desc)
	{
		LazyDynaBean lazyDynaBean0=new LazyDynaBean();
		lazyDynaBean0.set("itemid",itemid);
		lazyDynaBean0.set("itemtype",itemtype);
		lazyDynaBean0.set("codesetid",codesetid);
		lazyDynaBean0.set("fieldsetid",fieldsetid);
		lazyDynaBean0.set("itemdesc",desc);
		return lazyDynaBean0;
	}
	private int advanceFlag=0;
	public int getAdvanceFlag() {
		return advanceFlag;
	}



	public void setAdvanceFlag(int advanceFlag) {
		this.advanceFlag = advanceFlag;
	}
	private ArrayList advanceList=new ArrayList();

	
	public ArrayList getAdvanceList() {
		return advanceList;
	}



	public void setAdvanceList(ArrayList advanceList) {
		this.advanceList = advanceList;
	}



	/**
	 * 取得面试考核信息集合
	 * @param dbname     库前缀
	 * @param extendSql  
	 * @param orderSql
	 * @param tableColumnsList
	 *  @param codeid 权限控制
	 * @param advanceFlag 根据这个参数是否大于0可以判断是否采用高级测评方式
	 * @return
	 */
	public ArrayList getInterviewExamineList(UserView userView,String codeid,String dbname,String extendSql,String orderSql,ArrayList tableColumnsList,String z0101,String viewType, int advanceFlag) throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		String resume_state_field="";
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map0 = bo2.getAttributeValues();
			if (map0 != null && map0.get("resume_state") != null&&((String)map0.get("resume_state")).trim().length()>0)//获得简历指标状态
            {
                resume_state_field = (String) map0.get("resume_state");
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));
            }
			
		
			String hireMajor="";
			if(map0.get("hireMajor")!=null) {
                hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
            }
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)//判断是不是代码类
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                    hireMajorIsCode=true;
                }
			}
			
			this.advanceFlag=advanceFlag;
			String sql=getSql(codeid,dbname,extendSql,orderSql,userView,z0101,viewType);
			sql=PubFunc.keyWord_reback(sql);
			rowSet=dao.search(sql);
			LazyDynaBean lazyDynaBean0=null;
			while(rowSet.next())
			{
				lazyDynaBean0=new LazyDynaBean();
				String Z0336=rowSet.getString("Z0336");
				lazyDynaBean0.set("z0301",PubFunc.encrypt(rowSet.getString("z0301")));  //用工需求表id
				lazyDynaBean0.set("z0315",rowSet.getString("z0315")==null?"":rowSet.getString("z0315"));  //审核人数
				lazyDynaBean0.set("a_state",rowSet.getString(resume_state_field)==null?"":rowSet.getString(resume_state_field));  //审核人数
				lazyDynaBean0.set("a_z0311",PubFunc.encrypt(rowSet.getString("zp_pos_id")));  //审核人数
				LazyDynaBean tempBean=null;
				for(Iterator t=tableColumnsList.iterator();t.hasNext();)
				{
					tempBean=(LazyDynaBean)t.next();
					String itemid=(String)tempBean.get("itemid");
					String itemtype=(String)tempBean.get("itemtype");
					String codesetid=(String)tempBean.get("codesetid");
					if("particular".equalsIgnoreCase(itemid))
					{
						continue;
					}	
					if("score".equalsIgnoreCase(itemid))
					{
						if(rowSet.getString(itemid)!=null)
						{
						    /**安全加密,防止能看到其他人员具体详细的分数**/
						    String a0100 = rowSet.getString("a0100");
						    String a0101 = rowSet.getString("a0101");
						    a0100 = PubFunc.encrypt(a0100);
						    a0101 = PubFunc.encrypt(a0101);
							lazyDynaBean0.set(itemid,"<a href=\"javascript:lookParticularGrade('"+a0100+"','"+a0101+"')\">"+PubFunc.round(rowSet.getString(itemid),2)+"</a>");
						}
						else
						{
							lazyDynaBean0.set(itemid,"");
						}
					}
					else
					{
						/*if(rowSet.getString(itemid)!=null)
						{*/
							if("D".equalsIgnoreCase(itemtype))
							{
								if(rowSet.getDate(itemid)!=null)
								{
								java.sql.Date date=rowSet.getDate(itemid);
								SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
								lazyDynaBean0.set(itemid,bartDateFormat.format(date==null?new Date():date));
								}
								else
								{
									lazyDynaBean0.set(itemid,"");
								}
								
							}
							else if("0".equals(codesetid))
							{
								if(rowSet.getString(itemid)!=null)
								{
										if(this.advanceList.contains(itemid)){//高级测评的字段，如果为空 或者0.00这种字段是不能显示出来的显示应该为“”
											Number value=(Number) rowSet.getObject(itemid);
											if(value.doubleValue()==0){
												lazyDynaBean0.set(itemid,"");
											}else{
											    String a0100 = rowSet.getString("a0100");
					                            String a0101 = rowSet.getString("a0101");
					                            a0100 = PubFunc.encrypt(a0100);
					                            a0101 = PubFunc.encrypt(a0101);
												lazyDynaBean0.set(itemid,"<a href=\"javascript:lookParticularGradeForAndVance('"+a0100+"','"+a0101+"','"+itemid+"')\">"+PubFunc.round(rowSet.getString(itemid),2)+"</a>");
											}
										}else{
										    if ("a0100".equalsIgnoreCase(itemid)) {
                                                lazyDynaBean0.set(itemid, PubFunc.encrypt(rowSet.getString(itemid)));
                                            } else {
                                                lazyDynaBean0.set(itemid,rowSet.getString(itemid));
                                            }
										}
										
								}
								else
								{
									lazyDynaBean0.set(itemid,"");
								}
							}
							else
							{
								if(rowSet.getString(itemid)!=null)
								{
									if(Z0336!=null&& "01".equals(Z0336)&& "zp_pos_id".equalsIgnoreCase(itemid)&&hireMajor!=null&&hireMajor.length()>0)
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
								else
								{
									lazyDynaBean0.set(itemid,"");
								}
							}
						/*}
						else
						{
							lazyDynaBean0.set(itemid,"");
						}*/
					}
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
	

    private String str="";
	/**
	 * 
	 * @param columnsList
	 * @param dbName
	 * @param extendSql
	 * @param viewType  //1:用工需求  2：招聘计划
	 * @return
	 */
	public String getSql(String codeid,String dbName,String extendSql,String orderSql,UserView userView,String z0101,String viewType) throws GeneralException
	{
		HashSet tempLateIDSet=new HashSet();
		String resume_state_field = "";
		
		String hireMajor="";
		boolean hireMajorIsCode=false;
		try
		{
			
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map0 = bo2.getAttributeValues();
			if (map0 != null && map0.get("resume_state") != null)//简历状态指标
            {
                resume_state_field = (String) map0.get("resume_state");
            }
			if(map0.get("hireMajor")!=null) {
                hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
            }
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                    hireMajorIsCode=true;
                }
			}
			DemandCtrlParamXmlBo DemandCtrlParamXmlBo = new DemandCtrlParamXmlBo();
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			tempLateIDSet=(HashSet)map.get("testTemplateID");//获得测评模版表id
			int num0=0;
			for(Iterator t=tempLateIDSet.iterator();t.hasNext();)
			{
				num0++;
				t.next();
			}
			if(num0==0) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.interviewExam.errorInfo1")+"！"));
            }
			ParameterSetBo psb=new ParameterSetBo(this.conn);
			HashSet set=psb.getHashSet();
			tempLateIDSet=set;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		StringBuffer zpTestResult=new StringBuffer("");
		int num=0;
		if(tempLateIDSet==null) {
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.interviewExam.errorInfo1")+"！"));
        }
		for(Iterator t=tempLateIDSet.iterator();t.hasNext();)
		{
			num++;
			zpTestResult.append(" union  select a0100,score from zp_test_result_"+(String)t.next()+" where interview=0");
			
		}
		if(num==0) {
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.interviewExam.errorInfo1")+"！"));
        }
		//判断用户既不是 考官 又不是系统管理员,原来的程序，非su是考官才可以看见数据，现在改成按统一的权限控制来走
		/*boolean isCommissioner=false;
		if(!codeid.equals("summarise")&&!userView.isSuper_admin())
		{
			try
			{
				String sql="select z03.z0301,zpt.a0100  from zp_pos_tache zpt  left join Z05 on zpt.A0100=Z05.A0100  left join  Z03 on zpt.zp_pos_id=Z03.Z0301 ";
					   sql+=" left join z01 on z03.z0101=z01.z0101 ";
					   sql+=" left join "+dbName+"A01 on zpt.A0100="+dbName+"A01.A0100 where ( (Z05.state='22' and ( "+dbName+"A01."+resume_state_field+"='31' or "+dbName+"A01."+resume_state_field+"='32')) or (  "+dbName+"A01."+resume_state_field+"='41' ) ) ";
					 
					   String _str=Sql_switcher.isnull("z03.z0336","''");
					   sql+=" and ( ( z03.z0311 like '"+codeid+"%' and  "+_str+"<>'01' ) or ( z03.z0321 like '"+codeid+"%' and  "+_str+"='01' ) or ( z03.z0325 like '"+codeid+"%' and  "+_str+"='01' ) ) ";
						    
					   
				//	   sql+=" and z03.z0311 like '"+codeid+"%' ";
					   sql+=" and z03.z0319='04'  and ( Z05.Z0505 like '%"+userView.getUserId()+",%' or Z05.Z0507 like '%"+userView.getUserId()+",%' )  ";
				RowSet rowSet=null;
				ContentDAO dao = new ContentDAO(this.conn);
				rowSet=dao.search(sql);
				int num2=0;
				if(rowSet.next())
				{
					num2++;
				}
				if(num2==0)
					isCommissioner=true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}*/
		
		
		StringBuffer sql=new StringBuffer("");
		StringBuffer sql_select=new StringBuffer("");
		StringBuffer sql_from=new StringBuffer("");
		StringBuffer sql_where=new StringBuffer("");
		if(hireMajor!=null&&hireMajor.length()>0){
			if(this.advanceFlag<=0){
				sql_select.append("select z03.z0301,z03.z0315,zpt.a0100,case when z03.Z0336='01'  then  z03."+hireMajor+" else z03.z0311 end as zp_pos_id,z03.Z0336,Z03.Z0321,z03.z0325 departId,"+dbName+"A01."+resume_state_field+","+dbName+"A01.a0101,zp_test_result.score ");
			}else{
				sql_select.append("select z03.z0301,z03.z0315,zpt.a0100,case when z03.Z0336='01'  then  z03."+hireMajor+" else z03.z0311 end as zp_pos_id,z03.Z0336,Z03.Z0321,z03.z0325 departId,"+dbName+"A01."+resume_state_field+","+dbName+"A01.a0101, ");
				for(int i=0;i<this.advanceList.size();i++){
					if(i==this.advanceList.size()-1){
						sql_select.append(" z05."+(String)this.advanceList.get(i)+"");
					}else{
						sql_select.append(" z05."+(String)this.advanceList.get(i)+",");
					}
					
				}
			}
			
		}else{
			if(this.advanceFlag<=0){
				sql_select.append("select z03.z0301,z03.z0315,zpt.a0100,z03.z0311 zp_pos_id,z03.Z0336,Z03.Z0321,z03.z0325 departId,"+dbName+"A01."+resume_state_field+","+dbName+"A01.a0101,zp_test_result.score ");
			}else{
				sql_select.append("select z03.z0301,z03.z0315,zpt.a0100,z03.z0311 zp_pos_id,z03.Z0336,Z03.Z0321,z03.z0325 departId,"+dbName+"A01."+resume_state_field+","+dbName+"A01.a0101, ");
				for(int i=0;i<this.advanceList.size();i++){
					if(i==this.advanceList.size()-1){
						sql_select.append(" z05."+(String)this.advanceList.get(i)+"");
					}else{
						sql_select.append(" z05."+(String)this.advanceList.get(i)+",");
					}
					
				}
			}
			
		}
		sql_from.append(" from zp_pos_tache zpt ");
		sql_from.append(" left join Z05 on zpt.A0100=Z05.A0100");
		sql_from.append(" left join  Z03 on zpt.zp_pos_id=Z03.Z0301");
		sql_from.append(" left join z01 on z03.z0101=z01.z0101");
		sql_from.append(" left join  (select * from organization where codeitemid in (select distinct b.z0311 from zp_pos_tache a,z03 b where a.zp_pos_id=b.z0301)) org on z03.z0311=org.codeitemid" );
		sql_from.append(" left join "+dbName+"A01 on zpt.A0100="+dbName+"A01.A0100");
		if(this.advanceFlag<=0){//如果不采用高级测评的话那么就需要从结果表中取成绩，否则的话z05中的数值就是这个人的成绩（这个字段应该是初试成绩||复试成绩）
		    sql_from.append(" left join ( "+zpTestResult.substring(6)+" ) zp_test_result on zpt.a0100=zp_test_result.a0100 ");
		}
		
	//	sql_from.append(" left join zp_test_result on zpt.a0100=zp_test_result.a0100 ");
		
		if(!"summarise".equals(this.str))
		{
			sql_where.append(" where ( (Z05.state='22' and ( "+dbName+"A01."+resume_state_field+"='31' or "+dbName+"A01."+resume_state_field+"='32')) or (  "+dbName+"A01."+resume_state_field+"='41' ) )  ");
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
	    				tempSql2.append(" or z03.z0321 like '"+temps[i]+"%' ");
	    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%' ");
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
			sql_where.append(" and z03.z0319='04' ");
		}
		else
		{
			String[] temp=z0101.split("~");
			StringBuffer whereSql=new StringBuffer("");
			for(int i=0;i<temp.length;i++)
			{
				if("2".equals(viewType)) {
                    whereSql.append(" or z03.z0101='"+temp[i]+"' ");
                } else if("1".equals(viewType)) {
                    whereSql.append(" or z03.z0301='"+temp[i]+"' ");
                }
			}
			sql_where.append(" where ( "+whereSql.substring(3)+" ) and  "+dbName+"A01."+resume_state_field+"='43'");
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
	    				tempSql2.append(" or z03.z0321 like '"+temps[i]+"%' ");
	    				tempSql3.append(" or z03.z0325 like '"+temps[i]+"%' ");
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
		}
		
		sql_where.append(" and zpt.resume_flag='12' ");
		//不按考官来走了，2011-010-18
		/*if(!userView.isSuper_admin()&&!isCommissioner&&!userView.haveTheRoleProperty("8"))
		{
			
			
			sql_where.append(" and ( Z05.Z0505 like '%"+userView.getUserId()+",%' or Z05.Z0507 like '%"+userView.getUserId()+",%' ) ");
		}
		*/
		
		ArrayList list=getParameterFields();
		HashMap tabMap=new HashMap();
		tabMap.put("A01","1");
		
		String fieldSetId="";
		String fielditemid="";
		String itemtype="";
		for(Iterator t=list.iterator();t.hasNext();)
		{
			LazyDynaBean lazyDynaBean=(LazyDynaBean)t.next();
			fieldSetId=(String)lazyDynaBean.get("fieldsetid");
			fielditemid=(String)lazyDynaBean.get("itemid");
			itemtype=(String)lazyDynaBean.get("itemtype");		
			sql_select.append(","+dbName+fieldSetId+"."+fielditemid);
			if(tabMap.get(fieldSetId)==null)
			{
				String tempName=dbName+fieldSetId;
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
	
	
	public ArrayList getFieldList(ArrayList beanList)
	{
		ArrayList list=new ArrayList();
		
		for(int i=0;i<beanList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)beanList.get(i);
			String itemid=(String)abean.get("itemid");
			String itemtype=(String)abean.get("itemtype");
			String codesetid=(String)abean.get("codesetid");
			String desc=(String)abean.get("itemdesc");
			FieldItem fieldItem1=new FieldItem();
			fieldItem1.setItemid(itemid);
			fieldItem1.setItemtype(itemtype);
			fieldItem1.setCodesetid(codesetid);
			fieldItem1.setItemdesc(desc);
			if("score".equalsIgnoreCase(itemid)) {
                fieldItem1.setDecimalwidth(2);
            }
			list.add(fieldItem1);
		}
		
		
		return list;
	}
	
	
	/**
	 * 得到面试考核列
	 * @param dbName     库前缀
	 * @param columnList 列集合
	 * @param flag       
	 * @return
	 */
	public String getTableColumns(String dbName,ArrayList columnList,String flag)
	{		
		StringBuffer columns=new StringBuffer("");		
		if("1".equals(flag))
		{
			columnList.add(getLazyDynaBean("a0100","A","0",dbName+"A01",""));	   //人员编号
			columns.append(",a0100");	
		}
		
		String resume_state_field = "";
		String schoolPosition="";
		try
		{
			ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
			HashMap map = bo2.getAttributeValues();
			if (map != null && map.get("resume_state") != null)//简历状态指标
            {
                resume_state_field = (String) map.get("resume_state");
            }
			if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)//校园招聘岗位
            {
                schoolPosition=(String)map.get("schoolPosition");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		columnList.add(getLazyDynaBean(resume_state_field,"A","36",dbName+"A01",ResourceFactory.getProperty("column.warn.valid")));	   //人员状态
		columns.append(",state");
		columnList.add(getLazyDynaBean("a0101","A","0",dbName+"A01",ResourceFactory.getProperty("hire.employActualize.name")));	   //人员姓名
		columns.append(",a0101");
		columnList.add(getLazyDynaBean("Z0321","A","UN","Z03",ResourceFactory.getProperty("hire.interviewExamine.interviewUnit")));	   //应聘单位
		columns.append(",Z0321");
		columnList.add(getLazyDynaBean("departId","A","UM","org",ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment")));	   //应聘部门
		columns.append(",departId");
		if("1".equals(flag))
		{
			if(schoolPosition!=null&&schoolPosition.length()>0)
			{
				columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.majorposition")));	
			}
			else
			{
				columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.position")));	
			}
	    	//columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")+"|"+ResourceFactory.getProperty("hire.employActualize.interviewProfessional")));	
		}
		else {
            columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")));	   //应聘单位
        }
		columns.append(",zp_pos_id");
		LazyDynaBean a_lazyDynaBean=null;
		for(Iterator t=getParameterFields().iterator();t.hasNext();)
		{
			a_lazyDynaBean=(LazyDynaBean)t.next();
			if("a0101".equalsIgnoreCase((String)a_lazyDynaBean.get("itemid"))) {
                continue;
            }
			columnList.add(a_lazyDynaBean);
			columns.append(","+(String)a_lazyDynaBean.get("itemid"));
		}
		columnList.add(getLazyDynaBean("score","N","0","zp_test_result",ResourceFactory.getProperty("hire.interviewExamine.examinemark")));
		columns.append(",score");
	//	if(flag.equals("1"))
	//		columnList.add(getLazyDynaBean("particular","A","0","",ResourceFactory.getProperty("hire.interviewExamine.description")));
		return columns.substring(1);
	}
	
	
	
	/**
	 * 取得参数设置中指定的花名册指标数据
	 * @return
	 */
	public ArrayList getParameterFields()
	{
		ArrayList list=new ArrayList();
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);		
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			HashMap map=parameterXMLBo.getAttributeValues();
			if(map.get("fields")!=null&&((String)map.get("fields")).trim().length()>0)//out_fields  应聘简历中，简历导出指标项
			{
				StringBuffer whl=new StringBuffer("");
				String musterFieldIDs=(String)map.get("fields");
				if(musterFieldIDs.indexOf("`")==-1)
				{
					whl.append(",'"+musterFieldIDs+"'");
				}
				else
				{
					String[] fields=musterFieldIDs.split("`");
					for(int i=0;i<fields.length;i++)
					{
						whl.append(",'"+fields[i]+"'");
					}
				}
				rowSet=dao.search("select * from fielditem where itemid in ("+whl.substring(1)+")");
				while(rowSet.next())
				{
					LazyDynaBean lazyDynaBean=new LazyDynaBean();
					lazyDynaBean.set("itemid",rowSet.getString("itemid").toLowerCase());
					lazyDynaBean.set("itemtype",rowSet.getString("itemtype"));
					lazyDynaBean.set("codesetid",rowSet.getString("codesetid"));
					lazyDynaBean.set("fieldsetid",rowSet.getString("fieldsetid"));
					lazyDynaBean.set("itemdesc",rowSet.getString("itemdesc"));
					list.add(lazyDynaBean);	  
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("dfadsf");

	}



	public String getStr() {
		return str;
	}



	public void setStr(String str) {
		this.str = str;
	}


    /**
     * 得到面试考核列
     * @param dbName     库前缀
     * @param columnList 列集合
     * @param flag       
     * @return
     */
    /**
     * @param testTemplatAdvance  
     * @Title: getTableColumnsForAdvance 
     * @Description: 得到面试考核列(在高级测评方式下)
     * @param dbname 人员库
     * @param tableColumnsList 列名称
     * @param string
     * @return String   
     * @throws 
    */
    public ArrayList getTableColumnsForAdvance(String dbName, ArrayList columnList, String flag, ArrayList testTemplatAdvance) {
        
        StringBuffer columns=new StringBuffer("");
        ArrayList returnList = new ArrayList();
        ArrayList itemidList = new ArrayList();
        if("1".equals(flag))
        {
            columnList.add(getLazyDynaBean("a0100","A","0",dbName+"A01",""));      //人员编号
            columns.append(",a0100");   
        }
        
        String resume_state_field = "";
        String schoolPosition="";
        try
        {
            ParameterXMLBo bo2 = new ParameterXMLBo(this.conn, "1");
            HashMap map = bo2.getAttributeValues();
            if (map != null && map.get("resume_state") != null)//简历状态指标
            {
                resume_state_field = (String) map.get("resume_state");
            }
            if(map.get("schoolPosition")!=null&&((String)map.get("schoolPosition")).length()>0)//校园招聘岗位
            {
                schoolPosition=(String)map.get("schoolPosition");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        columnList.add(getLazyDynaBean(resume_state_field,"A","36",dbName+"A01",ResourceFactory.getProperty("column.warn.valid")));    //人员状态
        columns.append(",state");
        columnList.add(getLazyDynaBean("a0101","A","0",dbName+"A01",ResourceFactory.getProperty("hire.employActualize.name")));    //人员姓名
        columns.append(",a0101");
        columnList.add(getLazyDynaBean("Z0321","A","UN","Z03",ResourceFactory.getProperty("hire.interviewExamine.interviewUnit")));    //应聘单位
        columns.append(",Z0321");
        columnList.add(getLazyDynaBean("departId","A","UM","org",ResourceFactory.getProperty("hire.interviewExamine.interviewDepartment")));       //应聘部门
        columns.append(",departId");
        if("1".equals(flag))
        {
            if(schoolPosition!=null&&schoolPosition.length()>0)
            {
                columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.majorposition")));   
            }
            else
            {
                columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.apply.position")));    
            }
            //columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")+"|"+ResourceFactory.getProperty("hire.employActualize.interviewProfessional")));    
        }
        else {
            columnList.add(getLazyDynaBean("zp_pos_id","A","@K","zp_pos_tache",ResourceFactory.getProperty("hire.interviewExamine.interviewPosition")));       //应聘单位
        }
        columns.append(",zp_pos_id");
        LazyDynaBean a_lazyDynaBean=null;
        for(Iterator t=getParameterFields().iterator();t.hasNext();)
        {
            a_lazyDynaBean=(LazyDynaBean)t.next();
            if("a0101".equalsIgnoreCase((String)a_lazyDynaBean.get("itemid"))) {
                continue;
            }
            columnList.add(a_lazyDynaBean);
            columns.append(","+(String)a_lazyDynaBean.get("itemid"));
        }
        for(int i=0;i<testTemplatAdvance.size();i++){
            HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
            String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
            FieldItem item = DataDictionary.getFieldItem(score_item);
            if(item!=null){
                if(itemidList.contains(score_item)){
                	continue;
                }
                columnList.add(getLazyDynaBean(score_item,item.getItemtype(),item.getCodesetid(),item.getFieldsetid(),item.getItemdesc()));
                itemidList.add(score_item);
                columns.append(","+score_item);
                
            }
        }
        //如果采用高级测评方式就不用考评总分了
        //columnList.add(getLazyDynaBean("score","N","0","zp_test_result",ResourceFactory.getProperty("hire.interviewExamine.examinemark")));
        //columns.append(",score");
        returnList.add(columns.substring(1));
        returnList.add(itemidList);
        return returnList;
    }

}
