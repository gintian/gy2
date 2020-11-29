package com.hjsj.hrms.businessobject.hire.zp_options.stat.weekly;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${2007.04.26}:${time}</p> 
 *@author ${lilinbing}
 *@version 4.0
  */
public class WeeklyStmtSQLStr {
	private String sysvalue;
	private String hirepath;////dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
	public String getHirepath() {
		return hirepath;
	}

	public void setHirepath(String hirepath) {
		this.hirepath = hirepath;
	}

	public String[] getDepartmentsStr(ContentDAO dao,String codeitemid) throws GeneralException{
		codeitemid = this.getId(dao,codeitemid);
		StringBuffer sql = new StringBuffer();
		if(this.hirepath!=null&&this.hirepath.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
			if("01".equalsIgnoreCase(this.hirepath)){
				 sql.append("select codeitemid,codeitemdesc from ");
				 sql.append("organization where (codesetid='UN' or codesetid='UM') and "); 
				 sql.append(" codeitemid='");
				 sql.append(codeitemid);
				 sql.append("'");
			}else{
				 sql.append("select codeitemid,codeitemdesc from ");
				 sql.append("organization where (codesetid='UN' or codesetid='UM') and "); 
				 sql.append("codeitemid=(select parentid from organization ");
				 sql.append("where codeitemid='");
				 sql.append(codeitemid);
				 sql.append("')");
			}
		}else{
			 sql.append("select codeitemid,codeitemdesc from ");
			 sql.append("organization where (codesetid='UN' or codesetid='UM') and "); 
			 sql.append("codeitemid=(select parentid from organization ");
			 sql.append("where codeitemid='");
			 sql.append(codeitemid);
			 sql.append("')");
		}
		String[] list = {"0","0"};
		ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			if(i==0){
				this.setSysvalue((String)dynabean.get("codeitemid"));
			}
			list[0]=(String)dynabean.get("codeitemid");
			list[1]=(String)dynabean.get("codeitemdesc");
		}
		return list;
	}
	
	public String[] getJobsStr(ContentDAO dao,String codeitemid) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		 sql.append("select codeitemid,codeitemdesc from ");
		 sql.append("organization where codesetid='@K' and "); 
		 sql.append("codeitemid='");
		 sql.append(this.getId(dao,codeitemid));
		 sql.append("'");
		String[] list = {"0","0"};
		ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			if(i==0){
				this.setSysvalue((String)dynabean.get("codeitemid"));
			}
			list[0]=(String)dynabean.get("codeitemid");
			list[1]=(String)dynabean.get("codeitemdesc");
		}
		return list;
	}
	public String getSysvalue() {
		return sysvalue;
	}
	public void setSysvalue(String sysvalue) {
		this.sysvalue = sysvalue;
	}
	
	public String[] getWeeklySQL(ContentDAO dao,String id,String start_date,String end_date) throws GeneralException{
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart=wss.getDataValue("create_date",">=",start_date);
		String tempend=wss.getDataValue("create_date","<=",end_date);

		String[] result={"0","0"};
		
		StringBuffer sql = new StringBuffer();
		 sql.append("select sum(b_count) as b_count,sum(a_count) as a_count from ");
		 sql.append("zp_static_info where Z0301='"); 
		 sql.append(id);
		 sql.append("' and ");
		 sql.append(tempstart);
		 sql.append(" and ");
		 sql.append(tempend);
		ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			result[0]=((String)dynabean.get("b_count")).length()>0?((String)dynabean.get("b_count")):"0";
			result[1]=((String)dynabean.get("a_count")).length()>0?((String)dynabean.get("a_count")):"0";
		}
		
		return  result;
	
	}
	
	public int getWeekSQL(ContentDAO dao,String id,String dates) throws GeneralException{
		String result = "0";
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart=wss.getDataValue("create_date","=",dates);
		StringBuffer sql = new StringBuffer();
		 sql.append("select the_week from ");
		 sql.append("zp_static_info where Z0301='"); 
		 sql.append(id);
		 sql.append("' and ");
		 sql.append(tempstart);

		ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			result= ((String)dynabean.get("the_week"));
		}
		return  Integer.parseInt(result);
		
	}
	
	public ArrayList getDateSQL(ContentDAO dao,String id,String start_date,String end_date) throws GeneralException{
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart=wss.getDataValue("create_date",">=",start_date);
		String tempend=wss.getDataValue("create_date","<=",end_date);

		ArrayList result=new ArrayList();
		String[] b_count= {"0","0","0","0","0","0","0"};
		String[] a_count= {"0","0","0","0","0","0","0"};
		
		StringBuffer sql = new StringBuffer();
		 sql.append("select b_count,a_count,the_week from ");
		 sql.append("zp_static_info where Z0301='"); 
		 sql.append(id);
		 sql.append("' and ");
		 sql.append(tempstart);
		 sql.append(" and ");
		 sql.append(tempend);

		ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
		for(int i=0;i<dynabeanlist.size();i++){
			DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
			b_count[Integer.parseInt(dynabean.get("the_week").toString())-1]=((String)dynabean.get("b_count"));
			a_count[Integer.parseInt(dynabean.get("the_week").toString())-1]=((String)dynabean.get("a_count"));
		}
		result.add(b_count);
		result.add(a_count);
		return  result;
		
	}
	
	public String getId(ContentDAO dao,String id) throws GeneralException{
		String result=id;
		StringBuffer sql = new StringBuffer();
		if(this.hirepath!=null&&this.hirepath.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
			if("01".equalsIgnoreCase(this.hirepath)){
				sql.append("select z0321,z0325 from z03 where z0301='");
				sql.append(id);
				sql.append("'");
				String z0321="";
				String z0325="";
				ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
				for(int i=0;i<dynabeanlist.size();i++){
					DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
					String temp="";
					if(dynabean!=null){
						if(dynabean.get("z0325")!=null&&((String)dynabean.get("z0325")).trim().length()!=0) {
                            z0325=(String)dynabean.get("z0325");
                        } else {
                            z0321=(String)dynabean.get("z0321");
                        }
					}
					
				}
				if(z0325!=null&&z0325.length()!=0){
					result=z0325;
				}else{
					if(z0321!=null&&z0321.length()!=0) {
                        result=z0321;
                    }
				}
			}else{
				 sql.append("select Z0311 from ");
				 sql.append("Z03 where Z0301='"); 
				 sql.append(id);
				 sql.append("'");
				 ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
				for(int i=0;i<dynabeanlist.size();i++){
					DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
					result= ((String)dynabean.get("z0311"));
				}
			}
		}else{
			 sql.append("select Z0311 from ");
			 sql.append("Z03 where Z0301='"); 
			 sql.append(id);
			 sql.append("'");
			 ArrayList dynabeanlist=dao.searchDynaList(sql.toString());
			for(int i=0;i<dynabeanlist.size();i++){
				DynaBean dynabean=(DynaBean)dynabeanlist.get(i);
				result= ((String)dynabean.get("z0311"));
			}
		}
		
		return  result;
	}
	public ArrayList getJobidList(Connection conn,String hirepath,String orgID){
		ResultSet rs = null;
		ArrayList list=new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(conn);
			int yy=0;
			int mm=0;
			int dd=0;
			Calendar d=Calendar.getInstance();
			Date date = new Date();
			d.setTime(date);  //本周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			StringBuffer sql = new StringBuffer();
			sql.append("  and ( "+Sql_switcher.year("oz.end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+"="+mm+" and "+Sql_switcher.day("oz.end_date")+">="+dd+" ) ) ");
			sql.append(" and ( "+Sql_switcher.year("oz.start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"="+mm+" and "+Sql_switcher.day("oz.start_date")+"<="+dd+" ) ) ");	 				
			boolean flag = false;
			StringBuffer sql2 =new StringBuffer();
			String hireMajor="";	//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
			FieldItem hireMajoritem=null;
			if(hirepath.length()>0){
				if("01".equals(hirepath)){
					ParameterXMLBo bo2 = new ParameterXMLBo(conn, "1");
					HashMap map0 = bo2.getAttributeValues();
					//	String hireMajor="";		//xieguiquan 2010-09-17
					if(map0.get("hireMajor")!=null) {
                        hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
                    }
					
					if(hireMajor.length()>0)
					{
						hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
						if(hireMajoritem!=null&&hireMajoritem.isCode()){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
							sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
							sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"'  and codeitemid in ( ");
							sql2.append(" select   z1."+hireMajor+" from (select "+hireMajor+" from z03  where z0325 like '"+orgID+"%'  and z0336='01' ) z1 ))oz, ");
							sql2.append(" (select   z2."+hireMajor+",z2.z0301,z2.z0319 from (select "+hireMajor+",z0301,z0319 from z03  where z0325 like '"+orgID+"%'  and z0336='01' )z2)  z");
							sql2.append(" where oz.codesetid='"+hireMajoritem.getCodesetid()+"'and oz.codeitemid=z."+hireMajor+"  and z.z0319<>'01' ");
						}
						if(hireMajoritem!=null&&!hireMajoritem.isCode()&&orgID!=null&&orgID.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
							sql2.append("select z0301, ");
							sql2.append(hireMajor);
							sql2.append(" from z03 where (z0325 like '");
							sql2.append(orgID);
							sql2.append("%' or z0321 like'");
							sql2.append(orgID);
							sql2.append("%')  and z0336='01' and z0319<>'01'");
							
						}
							
					}
					
				}else{
					sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
					sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from organization where codesetid='@K'  and codeitemid in ( ");
					sql2.append(" select   z1.z0311 from (select z0311 from z03  where z0325 like '"+orgID+"%'  and z0336<>'01' ) z1 ))oz, ");
					sql2.append(" (select   z2.z0311,z2.z0301,z2.z0319 from (select z0311,z0301,z0319 from z03  where z0325  like '"+orgID+"%'  and z0336<>'01' )z2)  z");
					sql2.append(" where oz.codesetid='@K'and oz.codeitemid=z.z0311 and z.z0319<>'01' ");
				}
			}
			if(sql2.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
				rs=dao.search(sql2.toString());
				if(hirepath.length()>0){}{
				if(hireMajoritem!=null&&!hireMajoritem.isCode()){
					while(rs.next()){
						CommonData data=new CommonData(hireMajor+"-"+rs.getString("z0301"),rs.getString(2));
						list.add(data);

					}
				}else {
                    while(rs.next())
                    {
                        CommonData data=new CommonData(rs.getString("codeitemid")+"-"+rs.getString("z0301"),rs.getString("codeitemdesc"));
                        list.add(data);
                    }
                }
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	
	}
	/**
	 * 取得所属机构下的岗位、专业列表
	 * @param conn
	 * @param hirepath
	 * @param orgID
	 * @param z0301
	 * @return
	 */
	public ArrayList getJobidListModify(Connection conn,String hirepath,String orgID,String z0301){
		ResultSet rs = null;
		ArrayList list=new ArrayList();
		String orgDesc="z0325";
		try{
			ContentDAO dao = new ContentDAO(conn);
			int yy=0;
			int mm=0;
			int dd=0;
			Calendar d=Calendar.getInstance();
			Date date = new Date();
			d.setTime(date);  //本周期时间 
			yy=d.get(Calendar.YEAR);
			mm=d.get(Calendar.MONTH)+1;
			dd=d.get(Calendar.DATE);
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301", z0301);
			vo = dao.findByPrimaryKey(vo);
			if( vo.getString("z0325")==null||vo.getString("z0325").length()==0){//新建用工需求时  需求部门选择的是单位
				orgDesc="z0321";
			}
			StringBuffer sql = new StringBuffer();
			sql.append("  and ( "+Sql_switcher.year("oz.end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("oz.end_date")+"="+yy+" and "+Sql_switcher.month("oz.end_date")+"="+mm+" and "+Sql_switcher.day("oz.end_date")+">="+dd+" ) ) ");
			sql.append(" and ( "+Sql_switcher.year("oz.start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("oz.start_date")+"="+yy+" and "+Sql_switcher.month("oz.start_date")+"="+mm+" and "+Sql_switcher.day("oz.start_date")+"<="+dd+" ) ) ");	 				
			boolean flag = false;
			StringBuffer sql2 =new StringBuffer();
			String hireMajor="";	//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
			FieldItem hireMajoritem=null;
			if(hirepath.length()>0){
				if("01".equals(hirepath)){
					ParameterXMLBo bo2 = new ParameterXMLBo(conn, "1");
					HashMap map0 = bo2.getAttributeValues();
					//	String hireMajor="";		//xieguiquan 2010-09-17
					if(map0.get("hireMajor")!=null) {
                        hireMajor=(String)map0.get("hireMajor");  //招聘专业指标
                    }
					
					if(hireMajor.length()>0)
					{
						hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
						if(hireMajoritem!=null&&hireMajoritem.isCode()){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
							sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
							sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"'  and codeitemid in ( ");
							sql2.append(" select   z1."+hireMajor+" from (select "+hireMajor+" from z03  where "+orgDesc+" like '"+orgID+"%'  and z0336='01' ) z1 ))oz, ");
							sql2.append(" (select   z2."+hireMajor+",z2.z0301,z2.z0319 from (select "+hireMajor+",z0301,z0319 from z03  where "+orgDesc+" like '"+orgID+"%'  and z0336='01' )z2)  z");
							sql2.append(" where oz.codesetid='"+hireMajoritem.getCodesetid()+"'and oz.codeitemid=z."+hireMajor+"  and z.z0319<>'01' ");
						}
						if(hireMajoritem!=null&&!hireMajoritem.isCode()&&orgID!=null&&orgID.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
							sql2.append("select z0301, ");
							sql2.append(hireMajor);
							sql2.append(" from z03 where ("+orgDesc+" like '");
							sql2.append(orgID);
							sql2.append("%' or z0321 like'");
							sql2.append(orgID);
							sql2.append("%')  and z0336='01' and z0319<>'01'");
							
						}
							
					}
					
				}else{
					sql2.append(" select oz.codeitemid,oz.codeitemdesc,z.z0301 from ( ");
					sql2.append(" select  codesetid,codeitemid,codeitemdesc,end_date,start_date from organization where codesetid='@K'  and codeitemid in ( ");
					sql2.append(" select   z1.z0311 from (select z0311 from z03  where "+orgDesc+" like '"+orgID+"%'  and z0336<>'01' ) z1 ))oz, ");
					sql2.append(" (select   z2.z0311,z2.z0301,z2.z0319 from (select z0311,z0301,z0319 from z03  where "+orgDesc+"  like '"+orgID+"%'  and z0336<>'01' )z2)  z");
					sql2.append(" where oz.codesetid='@K'and oz.codeitemid=z.z0311 and z.z0319<>'01' ");
				}
			}
			if(sql2.length()!=0){//dml 2011-03-29 兼容校园招聘 校园招聘直接走招聘需求岗位中所属部门 不走待岗设置岗位的所属部门
				rs=dao.search(sql2.toString());
				if(hirepath.length()>0){}{
				if(hireMajoritem!=null&&!hireMajoritem.isCode()){
					while(rs.next()){
						CommonData data=new CommonData(hireMajor+"-"+rs.getString("z0301"),rs.getString(2));
						list.add(data);

					}
				}else {
                    while(rs.next())
                    {
                        CommonData data=new CommonData(rs.getString("codeitemid")+"-"+rs.getString("z0301"),rs.getString("codeitemdesc"));
                        list.add(data);
                    }
                }
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return list;
	
	}
}
