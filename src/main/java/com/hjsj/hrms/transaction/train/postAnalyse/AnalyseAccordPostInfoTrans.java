package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.station.TrainStationBo;
import com.hjsj.hrms.utils.PubFunc;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 分析按岗位要求匹配
 * <p>Title:AnalyseAccordPostInfoTrans.java</p>
 * <p>Description>:AnalyseAccordPostInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 25, 2011 9:49:41 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class AnalyseAccordPostInfoTrans extends IBusiness {
	public void execute() throws GeneralException {
		TrainStationBo trainStationBo=new TrainStationBo();
		String flag=(String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.length()>0?flag:"1";//1本单位；2 非本单位
		HashMap map=trainStationBo.getStationSett(this.getFrameconn());
		String nbase=(String)map.get("nbase");//人员库
		String empSetId=(String)map.get("emp_setid");;//人员培训子集
		String empCloumn=(String)map.get("emp_coursecloumn");//人员培训子集中参培课程指标
		String postSetId=(String)map.get("post_setid");//岗位培训子集
		String postCloumn=(String)map.get("post_coursecloumn");//岗位培训子集中参培课程指标
		String emp_passcloumn=(String)map.get("emp_passcloumn");//人员培训结果指标
		String emp_passvalues=(String)map.get("emp_passvalues");//人员培训合格结果值
		if(nbase==null||nbase.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.nbase.noset"),"",""));
		}else if(empSetId==null||empSetId.length()<=0||"#".equalsIgnoreCase(PubFunc.keyWord_reback(empSetId)))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.emp.nosetid"),"",""));
		}else if(empCloumn==null||empCloumn.length()<=0||"#".equalsIgnoreCase(PubFunc.keyWord_reback(empCloumn)))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.emp.noclomn"),"",""));
		}else if(postSetId==null||postSetId.length()<=0||"#".equalsIgnoreCase(PubFunc.keyWord_reback(postSetId)))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.post.nosetid"),"",""));
		}else if(postCloumn==null||postCloumn.length()<=0||"#".equalsIgnoreCase(PubFunc.keyWord_reback(postCloumn)))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("train.post.analyse.post.noclomn"),"",""));
		}
		
		InfoUtils infoUtils=new InfoUtils();
		ArrayList dblist=infoUtils.getUserBaseList(this.getUserView(),nbase,this.getFrameconn());
		StringBuffer sql=new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());	
		if("1".equals(flag))
		{
			curPostinfo(dblist,empSetId,empCloumn,postSetId,postCloumn,emp_passcloumn,emp_passvalues);
		}else
		{
			String code=(String)this.getFormHM().get("code");
			if(code==null||code.length()<=0)
				throw GeneralExceptionHandler.Handle(new GeneralException("","岗位不能为空！","",""));
			notCurPostinfo(dblist,code,empSetId, empCloumn,postSetId,postCloumn,emp_passcloumn,emp_passvalues);
		}
		this.getFormHM().put("dblist", dblist);				
		CommonData da=(CommonData)dblist.get(0);
		String userbase=da.getDataValue();
		this.getFormHM().put("dbpre", userbase);
		String cardid=infoUtils.searchCard("1",this.getFrameconn());
		this.getFormHM().put("cardid",cardid);
		ArrayList flaglist=new ArrayList();
		CommonData data=new CommonData("1","本岗位");
		flaglist.add(data);
		data=new CommonData("2","非本岗位");
		flaglist.add(data);
		this.getFormHM().put("flaglist", flaglist);
		this.getFormHM().put("flag", flag);
	}
    /**
     * 本职位
     * @param dblist
     * @param empSetId
     * @param empCloumn
     * @param postSetId
     * @param postCloumn
     * @throws GeneralException
     */
	private void curPostinfo(ArrayList dblist,String empSetId,String  empCloumn,String postSetId,String postCloumn,String emp_passcloumn,String emp_passvalues)throws GeneralException 
	{
		StringBuffer sql=new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		String whereIN="";
		String whereINU="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	    String backdate =sdf.format(new Date());
		try
		{
			InfoUtils infoUtils=new InfoUtils();
			for(int i=0;i<dblist.size();i++)
			{
				CommonData da=(CommonData)dblist.get(i);
				String dbpre=da.getDataValue();
				sql.setLength(0);
				TrainCourseBo bo = new TrainCourseBo(userView);
				String priv = bo.getUnitIdByBusi();
				if(!userView.isSuper_admin() && !"UN`".equalsIgnoreCase(priv))
				{
					String whereINsrc=/*InfoUtils.*/getWhereINSql(userView,dbpre);
					whereIN=" and exists (select a0100 "+whereINsrc+" and a.a0100="+dbpre+"a01.a0100)";//源表的过滤条件
					whereINU=" and exists (select a0100 "+whereINsrc+" and u.a0100="+dbpre+"a01.a0100)";
				}
				sql.append("select a0100 from "+dbpre+"A01 U where not exists(");
				sql.append(" select a0100 from (");
				sql.append(" select A0100,p.e01a1,p."+postCloumn+" from  "+dbpre+"A01 a,");  
				sql.append(" "+postSetId+" p");				
				sql.append(" where a.E01A1=p.E01A1 and "+Sql_switcher.isnull("a.E01A1", "'###'")+"<>'###'");
				sql.append(" and "+Sql_switcher.isnull("p."+postCloumn, "'###'")+"<>'###'");
				
				sql.append(" and exists(select * from "+dbpre+empSetId+" c where a.A0100=c.A0100 and "+Sql_switcher.isnull("c."+empCloumn, "'###'")+"<>'###')");
				sql.append(whereIN);
				sql.append(") T ");    
				sql.append("where not exists");
				sql.append(" (select * from "+dbpre+empSetId+" c where T.A0100=c.A0100 and c."+empCloumn+"=T."+postCloumn+")");
				sql.append(" and T.a0100=U.a0100");
                sql.append(" group by a0100");                
                sql.append(") and exists (select 1 from "+postSetId+" c where U.e01a1=c.e01a1 and "+Sql_switcher.isnull("c."+postCloumn, "'###'")+"<>'###'");
                sql.append(" and exists(select codeitemid from organization where c.e01a1=organization.codeitemid and codesetid='@K' and ");
				sql.append(Sql_switcher.dateValue(backdate)+" between start_date and end_date)"); 
				sql.append(")");
                sql.append(" and exists (select * from "+dbpre+empSetId+" c where U.A0100=c.A0100 and "+Sql_switcher.isnull("c."+empCloumn, "'###'")+"<>'###'");
                sql.append(" "+passsql.toString());
                sql.append(")");
                
                sql.append(whereINU);
                //System.out.println(sql.toString());
				if(userView.getStatus()==4)
				{
					String tabldName = "t_sys_result";
					Table table = new Table(tabldName);
					
					if (!dbWizard.isExistTable(table)) {
						return;
					}
					String flag="0";					
					String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
					str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";	
					dao.delete(str, new ArrayList());
					StringBuffer buf_sql = new StringBuffer("");
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from ("+sql.toString()+") myset");
					dao.insert(buf_sql.toString(), new ArrayList());
				}
				else
				{
			    	String tablename=userView.getUserName()+dbpre+"result";
			    	StringBuffer inssql=new StringBuffer();	    	
			    	inssql.append("insert into ");
			    	inssql.append(tablename);
			    	inssql.append("(");
			    	inssql.append("A0100)");
		    		inssql.append(" select ");
		    		inssql.append("A0100  from (");
		    		inssql.append(sql.toString());
		    		inssql.append(") myset");			    		
		    		dao.update("delete from "+tablename);			
		    		dao.update(inssql.toString());
		    		
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 非本职位
	 * @param dblist
	 * @param code
	 * @param empSetId
	 * @param empCloumn
	 * @param postSetId
	 * @param postCloumn
	 * @throws GeneralException
	 */
	private void notCurPostinfo(ArrayList dblist,String code,String empSetId,String  empCloumn,String postSetId,String postCloumn,String emp_passcloumn,String emp_passvalues)throws GeneralException 
	{
		//建立临时表
		String empTempTable="t#"+this.userView.getUserName();	
		TrainStationBo trainStationBo=new TrainStationBo();
		try
		{			
			boolean iscorrect=trainStationBo.createEmpInfoTempTable(empTempTable, this.getFrameconn());
			StringBuffer sql=new StringBuffer();
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(!iscorrect)
				return;
			StringBuffer passsql=new StringBuffer();
			if(emp_passcloumn!=null&&emp_passcloumn.length()>0&&emp_passvalues!=null&&emp_passvalues.length()>0)
	        {
				passsql.append(" and "+emp_passcloumn+" not in(");
				String p_values[]=emp_passvalues.split(",");
	        	for(int i=0;i<p_values.length;i++)
	        	{
	        		passsql.append("'"+p_values[i]+"',");
	        	}
	        	if(passsql.length()>0)
	        		passsql.setLength(passsql.length()-1);
	        	passsql.append(")");
	        }
			for(int i=0;i<dblist.size();i++)
			{
				CommonData da=(CommonData)dblist.get(i);
				String dbpre=da.getDataValue();
				sql.setLength(0);
				if(code!=null&&(code.startsWith("UN")||code.startsWith("UM")||code.startsWith("@K")))
					code = code.substring(2);
				String whereIN=/*RegisterInitInfoData.*/getWhereINSql(userView,dbpre);
				sql.append("insert into "+empTempTable+"(nbase,a0100,e01a1,post_rule)");
				sql.append(" (select '"+dbpre+"',A0100,a.E01A1"+/*'"+code+"'*/",p."+postCloumn+" from  "+dbpre+"A01 a,");  
				sql.append(" "+postSetId+" p");
				sql.append(" where p.E01A1 = a.E01A1 and a.E01A1 not like '"+code+"%'");
				sql.append(" and "+Sql_switcher.isnull("p."+postCloumn, "'###'")+"<>'###'");
				if(!userView.isSuper_admin())
				{
					
					sql.append(" and exists (select a0100 "+whereIN+" and a.a0100="+dbpre+"a01.a0100)");//源表的过滤条件
				}
				sql.append(")");
				//System.out.println(sql);
				dao.update(sql.toString());
				sql.setLength(0);
				String srcTab=dbpre+empSetId;//人员子集源表
				String destTab=empTempTable;//临时表
				String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".post_rule="+srcTab+"."+empCloumn+"";//关联串  xxx.field_name=yyyy.field_namex,....
				String strSet=destTab+".self_rule="+srcTab+"."+empCloumn;//更新串  xxx.field_name=yyyy.field_namex,....
				String strDWhere=destTab+".nbase='"+dbpre+"'";//更新目标的表过滤条件
				String strSWhere=Sql_switcher.isnull(srcTab+"."+empCloumn, "'###'")+"<>'###' ";
				
				String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
				update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
				//System.out.println(update);
				dao.update(update);
				sql.setLength(0);
				sql.append("select a0100 from "+dbpre+"A01 A where exists");
				sql.append("(select * from "+destTab+" T where post_rule=self_rule"+/*Sql_switcher.isnull("self_rule","'###'")+"='###'*/" and nbase='"+dbpre+"' and T.a0100=A.a0100)");
				sql.append(" and a.E01A1 not like '"+code+"%'");
				if(!userView.isSuper_admin())
				{
					sql.append(" and exists (select a0100 "+whereIN+" and a.a0100="+dbpre+"a01.a0100)");//源表的过滤条件
				}
				//System.out.println(sql);
				if(userView.getStatus()==4)
				{
					String tabldName = "t_sys_result";
					Table table = new Table(tabldName);
					
					if (!dbWizard.isExistTable(table)) {
						return;
					}
					String flag="0";					
					String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
					str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";	
					dao.delete(str, new ArrayList());
					StringBuffer buf_sql = new StringBuffer("");
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from ("+sql.toString()+") myset");
					dao.insert(buf_sql.toString(), new ArrayList());
				}
				else
				{
			    	String tablename=userView.getUserName()+dbpre+"result";
			    	StringBuffer inssql=new StringBuffer();	    	
			    	inssql.append("insert into ");
			    	inssql.append(tablename);
			    	inssql.append("(");
			    	inssql.append("A0100)");
		    		inssql.append(" select ");
		    		inssql.append("A0100  from (");
		    		inssql.append(sql.toString());
		    		inssql.append(") myset");	
		    		dao.update("delete from "+tablename);			
		    		dao.update(inssql.toString());
				}
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			trainStationBo.dropTable(empTempTable, this.getFrameconn());
		}
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
