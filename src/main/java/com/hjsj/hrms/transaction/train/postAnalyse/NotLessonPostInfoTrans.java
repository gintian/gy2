package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainInfoUtils;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 培训分析报表
 */
public class NotLessonPostInfoTrans extends IBusiness {

		public void execute() throws GeneralException {
			String code=(String)this.getFormHM().get("code");
			if(code==null||code.length()<3||"root".equalsIgnoreCase(code)){
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				code = bo.getUnitIdByBusi().replaceAll("`", ",");
			}
			code = "root".equalsIgnoreCase(code)||"UN,".equalsIgnoreCase(code) ? null : code;
			String chwhere = (String)this.getFormHM().get("chwhere");//条件查询
			this.getFormHM().remove("chwhere");
			
			String flag="1";
			String classid=(String)this.getFormHM().get("classid");
			String query=(String)this.getFormHM().get("query");		
			String userbase=(String)this.getFormHM().get("dbpre");
			flag=flag!=null&&flag.length()>0?flag:"1";//1本单位；2 非本单位
			String codes[]=null;
			if(code!=null)
				codes=code.split(",");
			String kind="";
			TrainStationBo trainStationBo=new TrainStationBo();
			HashMap map=trainStationBo.getStationSett(this.getFrameconn());
			String postSetId=(String)map.get("post_setid");//岗位培训子集
			String postCloumn=(String)map.get("post_coursecloumn");//岗位培训子集中参培课程指标
			String empSetId=(String)map.get("emp_setid");;//人员培训子集
			String empCloumn=(String)map.get("emp_coursecloumn");//人员培训子集中参培课程指标
			String emp_passcloumn=(String)map.get("emp_passcloumn");//人员培训结果指标
			String emp_passvalues=(String)map.get("emp_passvalues");//人员培训合格结果值
			if(code!=null&&code.length()>0&&codes!=null&&codes.length>0)
			{
				String column= ","+postCloumn;
				StringBuffer columns=new StringBuffer();
				StringBuffer strsql=new StringBuffer();
				StringBuffer ensql=new StringBuffer();
				strsql.append("select distinct "+userbase+"A01.a0000 as a0000,");		    
				strsql.append(userbase+"A01.a0100 as a0100");
				columns.append("A0100,a0000");
				String fieldstr=",B0110,E0122,E01A1,A0101";
					strsql.append(","+userbase+"A01.B0110,"+userbase+"A01.E0122,"+userbase+"A01.E01A1,"+userbase+"A01.A0101");
					columns.append(",B0110,E0122,E01A1,A0101");			
				ArrayList infoFieldList=new ArrayList();
				String flag22="infoself";
				if("infoself".equalsIgnoreCase(flag22)&&this.userView.getStatus()==1)
				{
					infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
				}
			    else
				{	
					infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
				}
				ArrayList fields=new ArrayList();//
				String[] f=fieldstr.split(",");
				for(int i=0;i<f.length;i++){
					for(int j=0;j<infoFieldList.size();j++){
						FieldItem fieldItem=(FieldItem)infoFieldList.get(j);
						if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
						{
							if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
							{
								fields.add(fieldItem);
							}
						}
					}
				}
				String strsql1=strsql.toString().replace(userbase+"A01", "AA")+column;
				columns.append(column);
				FieldItem fi = DataDictionary.getFieldItem(postCloumn, postSetId);
				fields.add(fi);
				this.getFormHM().put("fieldstr", fieldstr);			
				this.getFormHM().put("browsefields",fields);
				this.getFormHM().put("cloumn",columns.toString());			
				StringBuffer buf=new StringBuffer();
				String term_Sql="";
				String org_field="";
				TrainInfoUtils trainInfoUtils=new TrainInfoUtils();
				ArrayList classlist=new ArrayList();
				if(codes.length==1)
				{
					code=codes[0];
					String codesetid=code.substring(0,2);
					String itemid=code.substring(2);
					if("@K".equalsIgnoreCase(codesetid))
						classlist=trainInfoUtils.getPostClassList(this.getFrameconn(), itemid,postSetId,postCloumn);
				}
				StringBuffer passsql=new StringBuffer();
				if(emp_passcloumn!=null&&emp_passcloumn.length()>0&&emp_passvalues!=null&&emp_passvalues.length()>0)
		        {
					passsql.append(" and b."+emp_passcloumn+" in(");
					String p_values[]=emp_passvalues.split(",");
		        	for(int i=0;i<p_values.length;i++)
		        	{
		        		passsql.append("'"+p_values[i]+"',");
		        	}
		        	if(passsql.length()>0)
		        		passsql.setLength(passsql.length()-1);
		        	passsql.append(")");
		        }
				
				buf.append(strsql1+" from (");
				for(int i=0;i<codes.length;i++)
				{
					code=codes[i];
					if(code.length()<=0)
						continue;
					String codesetid=code.substring(0,2);
					String itemid=code.substring(2);
					buf.append(strsql.toString());
					buf.append(" from "+userbase+"A01 ");
					if("UN".equals(codesetid))
					{
						kind="2";
						org_field="b0110";
					}
					else if("UM".equals(codesetid))				
					{
						kind="1";
						org_field="e0122";
					}	
					else{
						kind="0";	
						org_field="e01a1";
					}
					if("2".equals(flag)&&this.userView.isSuper_admin())
			        {
			        	term_Sql= getWhereSQLExists(this.getFrameconn(),this.userView,userbase,"",true,"","","","");
			        	term_Sql = term_Sql.replaceAll("AND 1=2", "");
			        }else        	
			        {
					   term_Sql= getWhereSQLExists(this.getFrameconn(),this.userView,userbase,itemid,true,kind,"","","");
					   term_Sql = term_Sql.replaceAll("AND 1=2", "");
			        }
					buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
					ensql.append("(exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
					
					buf.append(" and "+userbase+"A01."+org_field+" like '"+itemid+"%'");
					ensql.append(" and "+userbase+"A01."+org_field+" like '"+itemid+"%'");
					
					if(!(classid!=null&&classid.length()>0/*&&!classid.equalsIgnoreCase("###")*/&&"3".equals(query)&& "2".equals(flag)))
					{
						if(userView.getStatus()==4)
						{
							String tabldName = "t_sys_result";
							Table table = new Table(tabldName);
							DbWizard dbWizard = new DbWizard(this.getFrameconn());
							if (!dbWizard.isExistTable(table)) {
								return;
							}
							
							buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
							buf.append(" and "+tabldName+".nbase='"+userbase+"')");
							
							ensql.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
							ensql.append(" and "+tabldName+".nbase='"+userbase+"')");
						}else
						{
							String tabldName=userView.getUserName()+userbase+"result";
							buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".a0100="+userbase+"A01.a0100 )");
							ensql.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".a0100="+userbase+"A01.a0100 )");
						}	
					}	
					if(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid))
					{
							
						
							buf.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' and b."+emp_passcloumn+" in ("+emp_passvalues.substring(0, emp_passvalues.length()-1)+"))");
							ensql.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' and b."+emp_passcloumn+" in ("+emp_passvalues.substring(0, emp_passvalues.length()-1)+"))");
						
					}
					
					if(chwhere!=null&&chwhere.length()>0){
						buf.append(" and a0100 in(select a0100 "+chwhere+")");
						ensql.append(" and a0100 in(select a0100 "+chwhere+")");
					}
					
					buf.append(" union ");
					ensql.append(") or ");
				}
				
				StringBuffer notsql =new StringBuffer();
				notsql.append("select distinct a.a0100,a.a0101,a.e01a1,b."+postCloumn);
				notsql.append(" from "+userbase+"A01 a,"+postSetId+" b ");
				notsql.append(" where a.e01a1=b.e01a1 and "+postCloumn+" is not null and not exists(");
				notsql.append(" select 1 from "+userbase+empSetId+" c");
				notsql.append(" where "+empCloumn+" is not null and "+empCloumn+"!=''");
				notsql.append(" and "+emp_passcloumn+" in ("+emp_passvalues.substring(0, emp_passvalues.length()-1)+")");
				notsql.append(" and c.a0100=a.a0100 and b."+postCloumn+" = c."+empCloumn+")");
				if(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid))
					notsql.append(" and "+postCloumn+" = '"+classid+"'");
				
				buf.setLength(buf.length()-7);
				ensql.setLength(ensql.length()-3);	
				this.getFormHM().put("ensql",SafeCode.encode(ensql.toString()));		
				this.getFormHM().put("where","");
				this.getFormHM().put("classlist", classlist);
				this.getFormHM().put("sqlstr",buf.toString()+") AA join ("+notsql.toString()+") b on AA.a0100=b.a0100 ");
			}else
			{ 
				if(code!=null&&code.length()>0)
				{
					kind="0";
				}
				String column= ","+postCloumn;

				StringBuffer columns=new StringBuffer();
				StringBuffer strsql=new StringBuffer();
				strsql.append("select distinct "+userbase+"A01.a0000 as a0000,'"+userbase+"' as nbase,");
			    strsql.append(userbase);
				strsql.append("A01.A0100 as a0100");
				columns.append("A0100,a0000,nbase");
				String fieldstr=",B0110,E0122,E01A1,A0101";
				strsql.append(","+userbase+"A01.B0110,"+userbase+"A01.E0122,"+userbase+"A01.E01A1,"+userbase+"A01.A0101");
					columns.append(",B0110,E0122,E01A1,A0101");			
				ArrayList infoFieldList=new ArrayList();
				String flag22="infoself";			
				if("infoself".equalsIgnoreCase(flag22)&&this.userView.getStatus()==1)
				{
					infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
				}
			    else
				{	
					infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
				}
				ArrayList fields=new ArrayList();//
				String[] f=fieldstr.split(",");
				for(int i=0;i<f.length;i++){
					for(int j=0;j<infoFieldList.size();j++){
						FieldItem fieldItem=(FieldItem)infoFieldList.get(j);
						if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
						{
							if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
							{
								fields.add(fieldItem);
							}
						}
					}
				}
				String strsql1=strsql.toString().replace(userbase+"A01", "AA")+column;
				columns.append(column);
				FieldItem fi = DataDictionary.getFieldItem(postCloumn, postSetId);
				fields.add(fi);
				this.getFormHM().put("fieldstr", fieldstr);			
				this.getFormHM().put("browsefields",fields);
				this.getFormHM().put("cloumn",columns.toString());			
			    StringBuffer buf=new StringBuffer();
		        String term_Sql="";
		        if("2".equals(flag)&&this.userView.isSuper_admin())
		        {
		        	term_Sql=/*infoUtils.*/getWhereSQLExists(this.getFrameconn(),this.userView,userbase,"",true,"","","","");  
		        }else        	
		        {
		        	term_Sql=/*infoUtils.*/getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,true,kind,"","",""); 
		        	term_Sql = term_Sql.replaceAll("AND 1=2", "");
		        }      
		        if(term_Sql!=null&&term_Sql.length()>0)
				   buf.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)");
		        else 
		        {
		        	if(!this.userView.isSuper_admin())
		        		throw GeneralExceptionHandler.Handle(new GeneralException("","操作用户没有管理范围权限！","",""));	        		
		        	else
		        		buf.append(" where 1=1" );
		        }	        
				if("2".equals(flag))
				{
					buf.append(" and "+userbase+"A01.e01a1<>'"+code+"'");
				}
				if(!(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid)&&"3".equals(query)&& "2".equals(flag)))
				{
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(this.getFrameconn());
						if (!dbWizard.isExistTable(table)) {
							return;
						}
						
						buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".obj_id="+userbase+"A01.a0100 ");
						buf.append(" and "+tabldName+".nbase='"+userbase+"')");
					}else
					{
						String tabldName=userView.getUserName()+userbase+"result";
						buf.append(" and exists(select 1 from "+tabldName+" where "+tabldName+".a0100="+userbase+"A01.a0100 )");
					}	
				}	
				if(classid!=null&&classid.length()>0&&!"###".equalsIgnoreCase(classid))
				{
						
					buf.append(" and not exists(select 1 from "+userbase+empSetId+" b where b.a0100="+userbase+"A01.a0100 and b."+empCloumn+"='"+classid+"' and b."+emp_passcloumn+" in ("+emp_passvalues.substring(0, emp_passvalues.length()-1)+"))");
					
				}		
				
				StringBuffer notsql =new StringBuffer();
				notsql.append("select distinct a.a0100,a.a0101,a.e01a1,b."+postCloumn);
				notsql.append(" from "+userbase+"A01 a,"+postSetId+" b ");
				notsql.append(" where a.e01a1=b.e01a1 and "+postCloumn+" is not null and not exists(");
				notsql.append(" select 1 from "+userbase+empSetId+" c");
				notsql.append(" where "+empCloumn+" is not null and "+empCloumn+"!=''");
				notsql.append(" and "+emp_passcloumn+" in ("+emp_passvalues.substring(0, emp_passvalues.length()-1)+")");
				notsql.append(" and c.a0100=a.a0100 and b."+postCloumn+" = c."+empCloumn+")");
				
				TrainInfoUtils trainInfoUtils=new TrainInfoUtils();
				ArrayList classlist=trainInfoUtils.getPostClassList(this.getFrameconn(), code,postSetId,postCloumn);
				this.getFormHM().put("ensql",SafeCode.encode(buf.toString()));		
				this.getFormHM().put("sqlstr",strsql1+" from ("+strsql.toString()+" from "+userbase+"A01 "+buf.toString()+") AA join ("+notsql.toString()+") b on AA.a0100=b.a0100 ");
				this.getFormHM().put("classlist", classlist);
				this.getFormHM().put("where", "");
			}
			
		}
		/**
	     * 兼职子集没有A000字段时
	     * @param userbase
	     * @param code
	     * @param kind
	     * @param orgtype
	     * @param personsortfield
	     * @param personsort
	     * @param part_unit
	     * @param part_setid
	     * @param part_appoint
	     * @param where_n
	     * @return
	     * @throws GeneralException
	     */
	    public String getWhereSQLExists(Connection conn,UserView userView,String userbase,String code,boolean isCodeLike,String kind,String orgtype,String personsortfield
	        	,String personsort)throws GeneralException 
	        	   
	    {
	    	StringBuffer union_Sql=new StringBuffer();
	    	StringBuffer wheresql=new StringBuffer();
	    	String main_Tablename=userbase+"A01";
	    	String strwhere="";   
	    	String strsqlA010="select A.*";	
			if(userView.isSuper_admin()){                    //超级用户	
				//生成没有高级条件的from后的sql语句
				if(!"vorg".equals(orgtype))
				{
					wheresql.append(" from ");
					wheresql.append(userbase);
					wheresql.append("A01 A");
					if("2".equals(kind) && code!=null && code.length()>0)
					{
						if(isCodeLike)
						{
							wheresql.append(" where ((b0110 like '");
						    wheresql.append(code);
						    wheresql.append("%'");
						}else
						{
							wheresql.append(" where ((b0110 = '");
						    wheresql.append(code);
						    wheresql.append("'");
						}					
					}			   
					else if("1".equals(kind)  && code!=null && code.length()>0)
					{
						if(isCodeLike)
						{
							wheresql.append(" where ((e0122 like '"); 
							wheresql.append(code);
							wheresql.append("%'");
						}else
						{
							wheresql.append(" where ((e0122 = '"); 
							wheresql.append(code);
							wheresql.append("'");
						}
						
					}				
					else if("0".equals(kind) && code!=null && code.length()>0)
					{
						if(isCodeLike)
						{
							wheresql.append(" where ((e01a1 like '");
							wheresql.append(code);
							wheresql.append("%'");
						}else
						{
							wheresql.append(" where ((e01a1 = '");
							wheresql.append(code);
							wheresql.append("'");
						}
						
						
					}else if("2".equals(kind))
					{
						wheresql.append(" where ((1=1");
					}
					else
					{
						wheresql.append(" where ((1=1");
					}
				    strwhere=wheresql.toString();
				    if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&personsort!=null&&!"All".equalsIgnoreCase(personsort))
				    {
				    	strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
				    }		
				    union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);	
				    //if(personsort==null||personsort.length()<=0)
				    {
				    	DbNameBo dbbo=new DbNameBo(conn);
				    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A",kind);
				    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				    	{
				    		union_Sql.append(" or exists( ");		    		
				    		union_Sql.append(strWhere);	
				    		union_Sql.append(")");
				    	}
				    }			    	
			    	union_Sql.append(")");		    	
			    	union_Sql.append(")");		    	
				}else
				{
					wheresql.append(" from ");
					wheresql.append(userbase);
					wheresql.append("A01 A,vorganization v,t_vorg_staff b");
					wheresql.append(" where ((v.codeitemid='"+code+"'"); 
					wheresql.append(" and b.B0110= v.codeitemid ");
					wheresql.append(" and b.state=1 ");				
					wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
					wheresql.append(" and A.A0100=b.a0100");
					strwhere=wheresql.toString();;
					union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);			
				    DbNameBo dbbo=new DbNameBo(conn);
			    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or exists( ");		    		
			    		union_Sql.append(strWhere);	
			    		union_Sql.append(")");
			    	}
			    	union_Sql.append(")");
			    	union_Sql.append(")");		    	
				}
				
			}
			else{   
				if(!"vorg".equals(orgtype))
				{
				  ArrayList fieldlist=new ArrayList();
			        try
			        {
			        	   String expr="1";
				           String factor="";
				           TrainCourseBo tb = new TrainCourseBo(userView);
				           String priv = tb.getUnitIdByBusi();
				           String tmp[] = priv.split("`");
				           for (int i = 0; i < tmp.length; i++) {
								String t = tmp[i];
								if(t.startsWith("UN"))
								{
									factor+="B0110=";
									kind="2";
									if(t.length()>2)
									{
										factor+=t.substring(2);
										factor+="%`";
									}
									else
									{
										factor+="%`B0110=`";
									}
								}
								else if(t.startsWith("UM"))
								{
									factor="E0122="; 
									kind="1";
									if(t.length()>2)
									{
										factor+=t.substring(2);
										factor+="%`";
									}
									else
									{
										factor+="%`E0122=`";
									}
								}
								else if(t.startsWith("@K"))
								{
									factor="E01A1=";
									kind="0";
									if(t.length()>2)
									{
										factor+=t.substring(2);
										factor+="%`";
									}
									else
									{
										factor+="%`E01A1=`";
									}
								}
								else
								{
									factor="B0110=";
									kind="2";
									if(t.length()>2)
										factor+=t.substring(2);
									factor+="%`B0110=`";
								}
						   }
				           if(factor.length()>0){
				        	   for (int j = 1; j < factor.split("`").length; j++) {
				        		   expr=expr+"+"+(j+1);
				        	   }
				           }
			           if(factor==null||factor.length()<=0)
			        	   return "";
			            /**表过式分析*/
			            /**非超级用户且对人员库进行查询*/
			            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,isCodeLike,true,fieldlist);		            
			            strwhere=getPrivSqlExists(strwhere,main_Tablename,"A");
			            if(personsortfield!=null && !"null".equalsIgnoreCase(personsortfield) && personsortfield.length()>0&&personsort!=null&&!"All".equalsIgnoreCase(personsort))
				    		strwhere=strwhere + " and " + personsortfield + "='" + personsort + "'";
			            union_Sql.append(strsqlA010.toString());
					    union_Sql.append(strwhere);
					    //if(personsort==null||personsort.length()<=0)
					    {
					    	DbNameBo dbbo=new DbNameBo(conn);				    	
					    	String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,kind,isCodeLike,"A");
					    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
					    	{
					    		union_Sql.append(" or exists( ");		    		
					    		union_Sql.append(strWhere);	
					    		union_Sql.append(")");
					    	}
					    }				    			    	
			        }catch(Exception e){
			          e.printStackTrace();	
			        }
				}else
		        {
					wheresql.append(" from ");
					wheresql.append(userbase);
					wheresql.append("A01 A,vorganization v,t_vorg_staff b");
					wheresql.append(" where ((v.codeitemid='"+code+"'"); 
					wheresql.append(" and b.state=1 ");	
					wheresql.append(" and b.B0110= v.codeitemid ");
					wheresql.append(" and Upper(b.dbase)='"+userbase.toUpperCase()+"'");
					wheresql.append(" and A.A0100=b.a0100");
					strwhere=wheresql.toString();;
					union_Sql.append(strsqlA010.toString());
				    union_Sql.append(strwhere);			
				    DbNameBo dbbo=new DbNameBo(conn);
				    String strWhere=dbbo.getQueryFromPartTimeLikeExists(userView, userbase, code,isCodeLike,"A","");
			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    	{
			    		union_Sql.append(" or exists( ");		    		
			    		union_Sql.append(strWhere);	
			    		union_Sql.append(")");
			    	}	
			    	union_Sql.append(")");
			    	union_Sql.append(")");		    	
		        }
			}			
			return union_Sql.toString();
	    }
	    public String getPrivSqlExists(String strwhere,String old_table,String replace_table)
		{
			String s_top=strwhere.substring(0,strwhere.indexOf(old_table)+6);
	        strwhere=strwhere.substring(strwhere.indexOf(old_table)+6);		            
	        strwhere=strwhere.replaceAll(old_table, replace_table);
	        strwhere=s_top+" "+replace_table+" "+strwhere;
	        return strwhere;
		}
//	    select distinct a.a0100,a.e01a1,b.ka104 from usra01 a,ka1 b where a.e01a1=b.e01a1 and ka104 is not null and not exists (
//	    		select 1 from usra37 where h370z is not null and h370z !='' and a3760 in (1) and usra37.a0100=a.a0100)
}
