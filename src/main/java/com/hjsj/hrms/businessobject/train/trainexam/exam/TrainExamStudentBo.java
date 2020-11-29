package com.hjsj.hrms.businessobject.train.trainexam.exam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>
 * Title:QuestionesBo
 * </p>
 * <p>
 * Description:培训考试人员业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-10
 * </p>
 * 
 * @author zxj
 * @version 1.0
 * 
 */
public class TrainExamStudentBo {

	// 数据库连接
	private Connection conn;
	
	public TrainExamStudentBo(){
		
	}

	public TrainExamStudentBo(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得试卷状态列表
	 * 
	 * @return
	 */
	public ArrayList getPaperStatusList() {
		
		ArrayList list = new ArrayList();
		
		CommonData data  = new CommonData("-9", "全部");
		CommonData data0 = new CommonData("-1", "未考");
		CommonData data1 = new CommonData("0",  "正考");
		CommonData data2 = new CommonData("1",  "已考");

		list.add(data);
		list.add(data0);
		list.add(data1);
		list.add(data2);		

		return list;
	}

	/**
	 * 获得阅卷状态列表
	 * 
	 * @return
	 */
	public ArrayList getCheckStatusList() {
		
		ArrayList list = new ArrayList();
        
		CommonData data  = new CommonData("-9", "全部");
		CommonData data0 = new CommonData("-1", "未阅");
		CommonData data1 = new CommonData("0",  "正阅");
		CommonData data2 = new CommonData("1",  "已阅");
		CommonData data3 = new CommonData("2",  "发布");

		list.add(data);
		list.add(data0); 
		list.add(data1);
		list.add(data2);
		list.add(data3);

		return list;
	}	

	private String getDBPre(String pre)
	{
		String dbPre = "";
		
		StringBuffer sql = new StringBuffer("SELECT pre FROM DBNAME ");
		sql.append(" WHERE UPPER(pre)='");
		sql.append(pre.toUpperCase());
		sql.append("'");
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				dbPre = rs.getString("pre");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	

		return dbPre;		
	}
	
	public boolean addStudentToPlan(String planId, ArrayList studentList, String pre)
	{
		boolean isOk = false;
		
		String dbPre = getDBPre(pre);
		
		StringBuffer sql = new StringBuffer("INSERT INTO R55(R5400,nbase,A0100,A0101,B0110,E0122,E01A1,R5513,R5515)");
		sql.append(" SELECT ");
		sql.append(planId);
		sql.append(",'");
		sql.append(dbPre);
		sql.append("',A0100,A0101,B0110,E0122,E01A1,-1,-1");
		sql.append(" FROM ");
		sql.append(dbPre);
		sql.append("A01");
		sql.append(" WHERE B0110 IS NOT NULL");
		sql.append(" AND NOT EXISTS(SELECT 1 FROM R55 R"); 
		sql.append(" WHERE Upper(R.nbase)='");
		sql.append(dbPre.toUpperCase());
		sql.append("' AND R.R5400=");
		sql.append(planId);
		sql.append(" AND R.A0100=");
		sql.append(dbPre);
		sql.append("A01.A0100)");
		sql.append(" AND A0100 IN (");
		for(int i = 0; i < studentList.size(); i++)
		{
			sql.append("'");
			sql.append(studentList.get(i).toString());
			sql.append("'");
			if(i < studentList.size() - 1)
			{
				sql.append(",");
			}
		}
		
		sql.append(")");
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    dao.update(sql.toString());
		    isOk = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return isOk;
	}
	
	public String getStudentDBPre(ArrayList privDBPres)
	{
	    return getStudentDBPre(this.conn, privDBPres);
	}
	
	public static String getStudentDBPre(Connection conn, ArrayList privDBPres)
	{
		StringBuffer dbPres = new StringBuffer("");
		
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
		String nbase[] = constantbo.getTextValue("/param/post_traincourse/nbase").split(",");
		
		for(int i = 0; i < privDBPres.size(); i++)
		{
			for (int j = 0; j < nbase.length; j++)
			{
				String privPre = (String)privDBPres.get(i);  
				if(privPre.equalsIgnoreCase(nbase[j]))
				{
					dbPres.append(privPre);
					dbPres.append(",");
				}
			}
		}
		
		return dbPres.toString();
	}
	
	/**
	 * 取得某计划下考试未结束人数
	 * @param planId
	 * @return 人数
	 */
	public int getUnFinishExamStudentCount(String planId)
	{
		int studentCount = 0;
		StringBuffer sql = new StringBuffer("SELECT COUNT(*) AS cnt FROM R55");
		sql.append(" WHERE R5400=" + planId);
		sql.append(" AND " + Sql_switcher.sqlNull("R5513", "-1") + "<1");
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				studentCount = rs.getInt("cnt");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return studentCount;
	}
	
	/**
	 * 将某计划下考试还未结束的人员置为”已考“（只将正考改为已考，暂保留未考状态）
     * -1: 未考
     *  0: 正考
     *  1: 已考   
	 */
	public boolean setExamedForUnExamOrExaming(String planId)
	{
		boolean isOk = true;
		
		StringBuffer sql = new StringBuffer("UPDATE R55 SET R5513=1");
		sql.append(" WHERE R5400=" + planId);
		sql.append(" AND " + Sql_switcher.sqlNull("R5513", "-1") + "=0");
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			isOk = false;
			e.printStackTrace();
		}
		
		return isOk;
	}
	
	/**
	 * 取得某计划下试卷未阅人数
	 * @param planId
	 * @return 人数
	 */
	public int getUnCheckExamStudentCount(String planId)
	{
		int studentCount = 0;
		StringBuffer sql = new StringBuffer("SELECT COUNT(*) AS cnt FROM R55");
		sql.append(" WHERE R5400=" + planId);
		sql.append(" AND R5515<1");
		
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				studentCount = rs.getInt("cnt");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null) {
                    rs.close();
                }
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return studentCount;
	}
}
