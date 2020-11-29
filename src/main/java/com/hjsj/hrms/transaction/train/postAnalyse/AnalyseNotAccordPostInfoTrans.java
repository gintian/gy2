package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainInfoUtils;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 分析不符合本岗位培训要求
 * <p>Title:AnalyseNotAccordPostInfoTrans.java</p>
 * <p>Description>:AnalyseNotAccordPostInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 23, 2011 3:08:22 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class AnalyseNotAccordPostInfoTrans extends IBusiness {
	public void execute() throws GeneralException {
		TrainStationBo trainStationBo=new TrainStationBo();
		HashMap map=trainStationBo.getStationSett(this.getFrameconn());
		String nbase=(String)map.get("nbase");//人员库
		String empSetId=(String)map.get("emp_setid");;//人员培训子集
		String empCloumn=(String)map.get("emp_coursecloumn");//人员培训子集中参培课程指标
		String postSetId=(String)map.get("post_setid");//岗位培训子集
		String postCloumn=(String)map.get("post_coursecloumn");//岗位培训子集中参培课程指标
		String emp_passcloumn=(String)map.get("emp_passcloumn");//人员培训结果指标
		String emp_passvalues=(String)map.get("emp_passvalues");//人员培训合格结果值
		StringBuffer passsql=new StringBuffer();
		if(emp_passcloumn!=null&&emp_passcloumn.length()>0&&emp_passvalues!=null&&emp_passvalues.length()>0)
        {
			passsql.append(" and "+emp_passcloumn+" in(");
			String p_values[]=emp_passvalues.split(",");
        	for(int i=0;i<p_values.length;i++)
        	{
        		passsql.append("'"+p_values[i]+"',");
        	}
        	if(passsql.length()>0)
        		passsql.setLength(passsql.length()-1);
        	passsql.append(")");
        }
		/*nbase="Usr";//人员库
		empSetId="ASX";//人员培训子集编号
		empCloumn="CSX01";//人员培训子集中参培课程指标
		postSetId="KSX";//岗位培训子集编号
		postCloumn="KSX01";//岗位培训子集中参培课程指标		
*/		
		if(nbase==null||nbase.length()<=0||"#".equalsIgnoreCase(nbase))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.nbase.noset"),"",""));
		}else if(empSetId==null||empSetId.length()<=0||"#".equalsIgnoreCase(empSetId))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.emp.nosetid"),"",""));
		}else if(empCloumn==null||empCloumn.length()<=0||"#".equalsIgnoreCase(empCloumn))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.emp.noclomn"),"",""));
		}else if(postSetId==null||postSetId.length()<=0||"#".equalsIgnoreCase(postSetId))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.post.nosetid"),"",""));
		}else if(postCloumn==null||postCloumn.length()<=0||"#".equalsIgnoreCase(postCloumn))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.post.noclomn"),"",""));
		}
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		InfoUtils infoUtils=new InfoUtils();
		ArrayList dblist=infoUtils.getUserBaseList(this.getUserView(),nbase,this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String backdate =sdf.format(new Date());
		try
		{
			for(int i=0;i<dblist.size();i++)
			{
				CommonData da=(CommonData)dblist.get(i);
				String dbpre=da.getDataValue();
				String tabldName="";
				if(userView.getStatus()==4)
				{
					tabldName = "t_sys_result";
					Table table = new Table(tabldName);
					
					if (!dbWizard.isExistTable(table)) {
						return;
					}
					
					String flag="0";					
					String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
					str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";	
					dao.delete(str, new ArrayList());
				}else
				{
					tabldName=userView.getUserName()+dbpre+"result";
					dao.update("delete from "+tabldName);	
			    	
			    	
				}
				String whereIN="";	
				if(!"#".equals(postCloumn)){
					
				
				sql.setLength(0);
				sql.append("select a0100 from (");
				sql.append(" select A0100,p.e01a1,p."+postCloumn+" from  "+dbpre+"A01 a,");  
				sql.append(" "+postSetId+" p");
				//sql.append(" where a.E01A1=p.E01A1 and (a.E01A1 is not null or a.E01A1<>'')");
				//sql.append(" and  (p."+postCloumn+" is not null or p."+postCloumn+"<>'')");
				sql.append(" where a.E01A1=p.E01A1 and "+Sql_switcher.isnull("a.E01A1", "'###'")+"<>'###'");
				sql.append(" and "+Sql_switcher.isnull("p."+postCloumn, "'###'")+"<>'###'");
				sql.append(" and exists(select * from "+dbpre+empSetId+" c where a.A0100=c.A0100");// and "+Sql_switcher.isnull(empCloumn, "'###'")+"<>'###'");
				
				sql.append(")");
				TrainCourseBo bo = new TrainCourseBo(userView);
				String priv = bo.getUnitIdByBusi();
				if(!userView.isSuper_admin() && !"UN`".equalsIgnoreCase(priv))
				{
					whereIN=/*RegisterInitInfoData.*/getWhereINSql(userView,dbpre);
					sql.append(" and exists (select a0100 "+whereIN+" and a.a0100="+dbpre+"a01.a0100)");//源表的过滤条件
				}
				sql.append(" and p.E01A1 in(select codeitemid from organization where codesetid='@K' and ");
				sql.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date)"); 
				sql.append(") T ");    
				sql.append("where not exists");
				sql.append(" (select * from "+dbpre+empSetId+" c where T.A0100=c.A0100 and c."+empCloumn+"=T."+postCloumn+"");
				
				sql.append(")group by a0100");       
                //System.out.println(sql.toString());
				if(userView.getStatus()==4)
				{
					
					StringBuffer buf_sql = new StringBuffer("");
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from ("+sql.toString()+") myset");
					dao.insert(buf_sql.toString(), new ArrayList());
				}
				else
				{			    	
			    	StringBuffer inssql=new StringBuffer();	    	
			    	inssql.append("insert into ");
			    	inssql.append(tabldName);
			    	inssql.append("(");
			    	inssql.append("A0100)");
		    		inssql.append(" select ");
		    		inssql.append("A0100  from (");
		    		//System.out.println(sql);
		    		inssql.append(sql.toString());
		    		inssql.append(") myset");		
		    		dao.update(inssql.toString());
				}
				ArrayList e01a1list=getE01a1(dbpre,postSetId,postCloumn,empSetId,empCloumn,dao,whereIN);
				TrainInfoUtils trainInfoUtils=new TrainInfoUtils();
				for(int r=0;r<e01a1list.size();r++)
				{
					sql.setLength(0);
					String itemid=(String)e01a1list.get(r);
					String term_Sql=infoUtils.getWhereSQLExists(this.getFrameconn(),this.userView,dbpre,itemid,true,"0","","","");
					term_Sql=term_Sql.replaceAll("AND 1=2", "");
					sql.append("select a0100 from "+dbpre+"A01 ");
					sql.append(" where exists(select a0100 from ("+term_Sql.toString()+")A where  "+dbpre+"A01.a0100=A.a0100)");
					sql.append(" and "+dbpre+"A01.e01a1='"+itemid+"'");
					ArrayList classlist=trainInfoUtils.getPostClassList(this.getFrameconn(), itemid,postSetId,postCloumn);
					if(classlist!=null&&classlist.size()>1)
					{
						sql.append(" and not exists(select 1 from (select a0100");						
						sql.append(" ,sum(case "+empCloumn+" ");
						for(int s=1;s<classlist.size();s++)
						{
							da=(CommonData)classlist.get(s);
							sql.append("when '"+da.getDataValue()+"' then 1 ");
						}							
						sql.append(" else 0 end) as classSum");
						sql.append(" from "+dbpre+empSetId+"");
						if(passsql.length()>0)
						{
							sql.append(" where 1=1 "+passsql.toString());
						}
						sql.append(" group by a0100");						
						sql.append(") b ");
						sql.append("where b.a0100="+dbpre+"A01.a0100  and classSum="+(classlist.size()-1)+")");
						//System.out.println(sql.toString());
						if(userView.getStatus()==4)
						{
							sql.append(" and not exists(select a0100 from "+tabldName+" where "+tabldName+".obj_id="+dbpre+"A01.a0100)");
							
							StringBuffer buf_sql = new StringBuffer("");
							buf_sql.append("insert into " + tabldName);
							buf_sql.append("(username,nbase,obj_id,flag) ");
							buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
							buf_sql.append(" from ("+sql.toString()+") myset");
							dao.insert(buf_sql.toString(), new ArrayList());
						}
						else
						{			    
							sql.append(" and not exists(select a0100 from "+tabldName+" where "+tabldName+".a0100="+dbpre+"A01.a0100)");
							
					    	StringBuffer inssql=new StringBuffer();	    	
					    	inssql.append("insert into ");
					    	inssql.append(tabldName);
					    	inssql.append("(");
					    	inssql.append("A0100)");
				    		inssql.append(" select ");
				    		inssql.append("A0100  from (");
				    		inssql.append(sql.toString());
				    		inssql.append(") myset");			    				
				    		dao.update(inssql.toString());
						}
					}else
						continue;
					
				}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("dblist", dblist);				
		CommonData da=(CommonData)dblist.get(0);
		String userbase=da.getDataValue();
		this.getFormHM().put("dbpre", userbase);
		String cardid=infoUtils.searchCard("1",this.getFrameconn());
		this.getFormHM().put("cardid",cardid);		
	}
	
	private ArrayList getE01a1(String nbase,String postSetId,String postCloumn,String empSetId,String empCloumn,ContentDAO dao,String whereIN)
	{
		ArrayList list = new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select k.e01a1 e01a1 from "+postSetId+" k");
		sql.append(",(select e01a1 from "+nbase+"A01 c left join "+nbase+empSetId+" p on c.a0100=p.a0100 where 1=1");
		if(whereIN!=null&&whereIN.length()>0)
		   sql.append(" and exists (select a0100 "+whereIN+" and c.a0100="+nbase+"a01.a0100)");//源表的过滤条件		
		sql.append(")T where k.E01A1=T.E01A1");
		sql.append(" group by k.e01a1");
		//System.out.println(sql.toString());
		RowSet rs=null;
		try
		{
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				list.add(rs.getString("e01a1"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return list;
	}
	
    /**根据权限,生成select.IN中的查询串
     * @param code
     *        链接级别
     * @param userbase
     *        库前缀
     * @param cur_date
     *        考勤日期
     * @return 返回查询串
     * */
    public static String getWhereINSql(UserView userView,String userbase){
		 String strwhere="";	
		 try {
		     if(!userView.isSuper_admin())
		     {
		         String expr="1";
		         String factor="";
		         TrainCourseBo tb = new TrainCourseBo(userView);
		         String priv = tb.getUnitIdByBusi();
		         String tmp[] = priv.split("`");
		         int m=1;
		         for (int i = 0; i < tmp.length; i++) {
		             String t = tmp[i];
		             if(t.startsWith("UN"))
		             {
		                 factor+="B0110=";
		                 if(t.length()>2)
		                 {
		                     factor+=t.substring(2);
		                     factor+="%`";
		                 }
		                 else
		                 {
		                     factor+="%`B0110=`";
		                     //expr=expr+"+"+(++m);
		                 }
		             }
		             else if(t.startsWith("UM"))
		             {
		                 factor="E0122="; 
		                 if(t.length()>2)
		                 {
		                     factor+=t.substring(2);
		                     factor+="%`";
		                 }
		                 else
		                 {
		                     factor+="%`E0122=`";
		                     //expr="1+2";
		                 }
		             }
		             else if(t.startsWith("@K"))
		             {
		                 factor="E01A1=";
		                 if(t.length()>2)
		                 {
		                     factor+=t.substring(2);
		                     factor+="%`";
		                 }
		                 else
		                 {
		                     factor+="%`E01A1=`";
		                     //expr="1+2";
		                 }
		             }
		             else
		             {
		                 //expr="1+2";
		                 factor="B0110=";
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
		         ArrayList fieldlist=new ArrayList();
		         try
		         {        
		             
		             /**表过式分析*/
		             /**非超级用户且对人员库进行查询*/
		             if(priv.length()<=3)
		                 strwhere=userView.getKqPrivSQLExpression("",userbase,fieldlist);
		             else{
		                 strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		                 if(strwhere.endsWith("AND 1=2")||strwhere.endsWith("1=2"))
		                     strwhere = strwhere.substring(0,strwhere.length()-7);
		             }
		         }catch(Exception e){
		             e.printStackTrace();	
		         }
		         
		         
		         
		     }else{
		         StringBuffer wheresql=new StringBuffer();
		         wheresql.append(" from ");
		         wheresql.append(userbase);
		         wheresql.append("A01 ");
		         strwhere=wheresql.toString();
		     }
		 } catch (Exception e) {
		     e.printStackTrace();
        }
		   // System.out.println(userbase+"---"+strwhere);
	       return strwhere;
	 }
}
