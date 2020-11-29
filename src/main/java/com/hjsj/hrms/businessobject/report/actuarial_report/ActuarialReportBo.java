package com.hjsj.hrms.businessobject.report.actuarial_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActuarialReportBo {
	private Connection conn;
	private UserView userview=null;
	private String selfUnitCode="";
	
	
	private HashMap paramMap=new HashMap(); //校验参数
	
	public ActuarialReportBo(Connection con,UserView userView)
	{
		this.conn=con;
		this.userview=userView;
		selfUnitCode=getUnitcode(userview.getUserName());	
		paramMap=TargetsortBo.getTargetsortMap(con);
		

		
	}
	
	/**
	 * 取得报表采集状态
	 * @param cycle_id
	 * @return
	 */
	public String getCycleStatus(String cycle_id)
	{
		String status="";
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select status from tt_cycle where id="+cycle_id);
			if(rowSet.next()) {
                status=rowSet.getString("status");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return status;
	}
	/**
	 * 取得报表采集方法
	 * @param cycle_id
	 * @return
	 */
	public int getCycleKmethod(String cycle_id)
	{
		int status=0;
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select kmethod from tt_cycle where id="+cycle_id);
			if(rowSet.next()) {
                status=rowSet.getInt("kmethod");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return status;
	}
	
	/**
	 * 逐层汇总
	 * @param cycle_id
	 */
	public void layerCollect(String cycle_id)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			deleteNoLeafData(cycle_id,vo.getInt("kmethod"));
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除所有非叶结点的数据
	 * @param cycle_id
	 * @param kmethod
	 */
	public void deleteNoLeafData(String cycle_id,int kmethod)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		/*	StringBuffer sql=new StringBuffer("delete from u03 where id="+cycle_id+" and unitcode in ( ");
			sql.append("select unitcode from tt_organization where unitcode in (select parentid from tt_organization)");
			sql.append(" ) ");
			dao.delete(sql.toString(),new ArrayList());
			if(kmethod==0) //完整精算评估法
			{
				sql.setLength(0);
				sql.append("delete from u05 where id="+cycle_id+" and unitcode in ( ");
				sql.append("select unitcode from tt_organization where unitcode in (select parentid from tt_organization)");
				sql.append(" ) ");
				dao.delete(sql.toString(),new ArrayList());
			}
			else
			{
				sql.setLength(0);
				sql.append("delete from u04 where id="+cycle_id+" and unitcode in ( ");
				sql.append("select unitcode from tt_organization where unitcode in (select parentid from tt_organization)");
				sql.append(" ) ");
				dao.delete(sql.toString(),new ArrayList());
			}
			*/
			HashMap noLeafMap=getNoLeafNode();
			HashMap subUnitMap=new HashMap();
			HashMap map = getDescribe(cycle_id,"");
			TTorganization ttOrganization=new TTorganization(this.conn);
			int layNum=ttOrganization.getOrganizationLayNum(subUnitMap);
			for(int i=layNum;i>=1;i--)
			{
				ArrayList a_layList=(ArrayList)subUnitMap.get(String.valueOf(i));
				for(Iterator t=a_layList.iterator();t.hasNext();)
				{
					RecordVo a_vo=(RecordVo)t.next();
					String unitcode=a_vo.getString("unitcode");
					if(noLeafMap.get(unitcode)!=null)
					{
						collectReportData(unitcode,cycle_id,1,map);
					}else{
						RecordVo vo=new RecordVo("tt_cycle");
						vo.setInt("id",Integer.parseInt(cycle_id));
						vo=dao.findByPrimaryKey(vo);
						editReportStatus(unitcode,cycle_id,vo,1);
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得所有非叶结点
	 * @return
	 */
	public HashMap getNoLeafNode()
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from tt_organization where unitcode in (select parentid from tt_organization)");
			while(rowSet.next())
			{
				map.put(rowSet.getString("unitcode"),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return map;
	}
	
	public HashMap getDescribe2(String cycle_id,String unitcode){
		HashMap map = new  HashMap();
//		String ext_sql =throwunit(cycle_id,""); //该单位有可能是汇总单位 是否刨除？
		String sql = " select t3_desc,t5_desc from u01 where id="+cycle_id+" and unitcode='"+unitcode+"' and unitcode not in (select parentid from tt_organization  )";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet =null;
		try {
			RecordVo vo = new RecordVo("tt_organization");
			vo.setString("unitcode",unitcode);
		
				vo =dao.findByPrimaryKey(vo);
			 String unitname = vo.getString("unitname");
			 rowSet =dao.search(sql);
			String t3_desc="";
			String t5_desc="";
			while(rowSet.next()){
			String t3_desc2 =Sql_switcher.readMemo(rowSet,"t3_desc");
			String t5_desc2 =Sql_switcher.readMemo(rowSet,"t5_desc"); 
			if(t3_desc2!=null&& "".equals(t3_desc2)){
				t3_desc =t3_desc+unitname+":" +t3_desc2;
			}
			if(t5_desc2!=null&& "".equals(t5_desc2)){
				t5_desc =t5_desc+unitname+":" +t5_desc2;
			}
			}
			 vo = new RecordVo("U01");
			 vo.setString("unitcode", unitcode);
			 vo.setInt("id",Integer.parseInt(cycle_id));
			 vo = dao.findByPrimaryKey(vo);
			 if(vo!=null){
				 vo.setString("t3_desc",t3_desc);
				 vo.setString("t5_desc",t5_desc);
				 dao.updateValueObject(vo);
			 }else{
				 vo.setString("unitcode", unitcode);
				 vo.setInt("id",Integer.parseInt(cycle_id));
				 vo.setString("t3_desc",t3_desc);
				 vo.setString("t5_desc",t5_desc);
				 dao.addValueObject(vo);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (GeneralException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return map;
	}
	public HashMap getDescribe(String cycle_id,String unitcode){
		HashMap map = new  HashMap();
		String sql ="";
		RowSet rowset=null;
		RowSet rowset2=null;
		if("".equals(unitcode)){
		 sql = "select unitcode,unitname  from tt_organization where unitcode in(select parentid from tt_organization )";
		}else{
			 sql = "select unitcode,unitname  from tt_organization where unitcode ="+unitcode;
				
		}
		ContentDAO dao=new ContentDAO(this.conn);
		try {
	//		RecordVo vo = new RecordVo("tt_organization");
	//		vo.setString("unitcode",unitcode);
		
	//			vo =dao.findByPrimaryKey(vo);
	//		 String unitname = vo.getString("unitname");
			 rowset =dao.search(sql);
			
			String unitname ="";
			while(rowset.next()){
				String t3_desc="";
				String t5_desc="";
				 unitcode = rowset.getString("unitcode");
				// unitname =  rowset.getString("unitname");
				String sql2 = " select t3_desc,t5_desc,unitcode from u01 where id="+cycle_id+" and unitcode like '"+unitcode+"%' and unitcode not in (select parentid from tt_organization )";	
				 rowset2 = dao.search(sql2);
				while(rowset2.next()){
					String t3_desc2 =Sql_switcher.readMemo(rowset2,"t3_desc");
				    String t5_desc2 =Sql_switcher.readMemo(rowset2,"t5_desc"); 
				     String unitcode2 = rowset2.getString("unitcode");
				     RecordVo vo = new RecordVo("tt_organization");
						vo.setString("unitcode",unitcode2);
					
							
								vo =dao.findByPrimaryKey(vo);
							
						  unitname = vo.getString("unitname");
				    if(t3_desc2!=null&&!"".equals(t3_desc2)){
						t3_desc =t3_desc+unitname+":" +t3_desc2+"\r\n";
					}
					if(t5_desc2!=null&&!"".equals(t5_desc2)){
						t5_desc =t5_desc+unitname+":" +t5_desc2+"\r\n";
					}
				}
			map.put(unitcode+"t3_desc",t3_desc);
			map.put(unitcode+"t5_desc",t5_desc);
				
			
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch (GeneralException e) {
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowset!=null) {
                     rowset.close();
                 }
				 if(rowset2!=null) {
                     rowset2.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return map;
	}

	/**
	 * 汇总数据
	 * @param unitcode
	 * @param cycle_id
	 */
	public void collectReportData(String unitcode,String cycle_id,int flag,HashMap map)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			//汇总表3
			String ext_sql =throwunit(cycle_id,""); 
			dao.update("delete from u03 where id="+cycle_id+" and unitcode='"+unitcode+"'");
			StringBuffer sql=new StringBuffer("insert into u03 (u0301,unitcode,id,u0303,u0305,u0307,u0309,u0311,u0313,u0315,u0317,editflag) ");
			sql.append(" select u0301,'"+unitcode+"',"+cycle_id+",u0303,sum(u0305),sum(u0307),sum(u0309),sum(u0311),sum(u0313),sum(u0315),sum(u0317),1 from u03 ");
			sql.append(" where unitcode in ( ");
			sql.append(" select unitcode from tt_organization where unitcode not in (select parentid from tt_organization ) and unitcode like '"+unitcode+"%' and unitcode<>'"+unitcode+"' "+ext_sql+" ) ");
			sql.append(" and id="+cycle_id+" group by u0301,u0303 ");
			dao.update(sql.toString());
			
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			//保存表3表五的警告描述信息写入map("unitcode,t3_desc")
				if(map.get(unitcode+"t3_desc")!=null) {
                    saveDesc(unitcode,Integer.parseInt(cycle_id),"t3_desc",map.get(unitcode+"t3_desc").toString());
                }
				if(map.get(unitcode+"t5_desc")!=null) {
                    saveDesc(unitcode,Integer.parseInt(cycle_id),"t5_desc",map.get(unitcode+"t5_desc").toString());
                }
			
			if(vo.getInt("kmethod")==0) //完整精算评估法
			{
				//保存表5数据
				saveU05Values(unitcode,cycle_id,vo.getString("theyear"));
				
				
			}
			else if(vo.getInt("kmethod")==1) //向前滚动法
			{
				//保存表4数据
				dao.update("delete from u04 where id="+cycle_id+" and unitcode='"+unitcode+"'");
				sql.setLength(0);
				sql.append("insert into u04 (u0401,unitcode,id,u0403,u0405,u0407,u0409,u0411,editflag) ");
				sql.append(" select u0401,'"+unitcode+"',"+cycle_id+",u0403,sum(u0405),sum(u0407),sum(u0409),sum(u0411),1 from u04 where   unitcode in ( ");
				sql.append(" select unitcode from tt_organization where unitcode not in (select parentid from tt_organization ) and unitcode like '"+unitcode+"%' and unitcode<>'"+unitcode+"' "+ext_sql+" ) ");
				sql.append("  and id="+cycle_id+" group by u0401,u0403 ");
				dao.update(sql.toString());
			}
			editReportStatus(unitcode,cycle_id,vo,flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改报表状态
	 * @param unitcode
	 * @param cycle_id
	 */
	public void editReportStatus(String unitcode,String cycle_id,RecordVo vo,int flag)
	{
		try
		{
			saveReportStatus(unitcode,"U01",cycle_id,flag);
			saveReportStatus(unitcode,"U02_1",cycle_id,flag);
			saveReportStatus(unitcode,"U02_2",cycle_id,flag);
			saveReportStatus(unitcode,"U02_3",cycle_id,flag);
			saveReportStatus(unitcode,"U02_4",cycle_id,flag);			
			saveReportStatus(unitcode,"U03",cycle_id,flag);
			if(vo.getInt("kmethod")==0) //完整精算评估法
            {
                saveReportStatus(unitcode,"U05",cycle_id,flag);
            } else {
                saveReportStatus(unitcode,"U04",cycle_id,flag);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断报表是否处于 flag 状态
	 * @param unitcode
	 * @param cycle_id
	 * @return
	 */
	public String isSub(String report_id,String unitcode,String cycle_id,String flag)
	{
		String aflag="0";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet =null;
		try
		{
		
				 rowSet=dao.search("select * from  tt_calculation_ctrl where unitcode='"+unitcode+"' and report_id='"+report_id+"'  and id="+cycle_id);
				if(rowSet.next())
				{
					if(rowSet.getString("flag").equals(flag)) {
                        aflag="1";
                    }
				}
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return aflag;
	}
	
	
	/**
	 * 判断报表是否全部处于 flag 状态
	 * @param unitcode
	 * @param cycle_id
	 * @return
	 */
	public String isAllFlag(String unitcode,String cycle_id,String flag)
	{
		String aflag="1";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
		
				 rowSet=dao.search("select * from  tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+cycle_id);
				HashMap map=new HashMap();
				while(rowSet.next())
				{
					map.put(rowSet.getString("report_id").toLowerCase(),rowSet.getString("flag"));
				}
				
				RecordVo vo=new RecordVo("tt_cycle");
				vo.setInt("id",Integer.parseInt(cycle_id));
				vo=dao.findByPrimaryKey(vo);
				
				if(map.get("u01")==null||!((String)map.get("u01")).equals(flag)) {
                    aflag="0";
                }
				if(map.get("u02_1")==null||!((String)map.get("u02_1")).equals(flag)) {
                    aflag="0";
                }
				if(map.get("u02_2")==null||!((String)map.get("u02_2")).equals(flag)) {
                    aflag="0";
                }
				if(map.get("u02_3")==null||!((String)map.get("u02_3")).equals(flag)) {
                    aflag="0";
                }
				if(map.get("u02_4")==null||!((String)map.get("u02_4")).equals(flag)) {
                    aflag="0";
                }
				if(map.get("u03")==null||!((String)map.get("u03")).equals(flag)) {
                    aflag="0";
                }
				
				
				if(vo.getInt("kmethod")==0) //完整精算评估法
				{
					if(map.get("u05")==null||!((String)map.get("u05")).equals(flag)) {
                        aflag="0";
                    }
				}
				else if(vo.getInt("kmethod")==1) //向前滚动法
				{
					if(map.get("u04")==null||!((String)map.get("u04")).equals(flag)) {
                        aflag="0";
                    }
				}
				
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return aflag;
	}
	
	
	/**
	 * 判断报表是否全部提交
	 * @param unitCode
	 * @param cycle_id
	 * @return
	 */
	public String isAllSub_child(String unitCode,String cycle_id)
	{
		String flag="1";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet0=null;
		RowSet rowSet=null;
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			String ext_sql =throwunit(cycle_id,""); 
			 rowSet0=dao.search("select * from tt_organization where  parentid='"+unitCode+"' and unitcode<>parentid "+ext_sql+"");
			 
			while(rowSet0.next())
			{
				String unitcode=rowSet0.getString("unitcode");
				if("0".equals(flag)) {
                    continue;
                }
				
				rowSet=dao.search("select * from  tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+cycle_id);
				HashMap map=new HashMap();
				while(rowSet.next())
				{
					map.put(rowSet.getString("report_id").toLowerCase(),rowSet.getString("flag"));
				}
				
			
				
				if(map.get("u01")==null||!"1".equals((String)map.get("u01"))) {
                    flag="0";
                }
				if(map.get("u02_1")==null||!"1".equals((String)map.get("u02_1"))) {
                    flag="0";
                }
				if(map.get("u02_2")==null||!"1".equals((String)map.get("u02_2"))) {
                    flag="0";
                }
				if(map.get("u02_3")==null||!"1".equals((String)map.get("u02_3"))) {
                    flag="0";
                }
				if(map.get("u02_4")==null||!"1".equals((String)map.get("u02_4"))) {
                    flag="0";
                }
				if(map.get("u03")==null||!"1".equals((String)map.get("u03"))) {
                    flag="0";
                }
				
				
				if(vo.getInt("kmethod")==0) //完整精算评估法
				{
					if(map.get("u05")==null||!"1".equals((String)map.get("u05"))) {
                        flag="0";
                    }
				}
				else if(vo.getInt("kmethod")==1) //向前滚动法
				{
					if(map.get("u04")==null||!"1".equals((String)map.get("u04"))) {
                        flag="0";
                    }
				}
				
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet0!=null) {
                     rowSet0.close();
                 }
					if(rowSet!=null) {
                        rowSet.close();
                    }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	
	/**
	 * 是否是直属单位
	 * @param unticode
	 * @return
	 */
	public String isUnderUnit(String unticode)
	{
		String flag="0";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from tt_organization  where   unitcode='"+this.selfUnitCode+"' and unitcode=(select parentid from tt_organization where unitcode='"+unticode+"')");
			if(rowSet.next()) {
                flag="1";
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	
	
	/**
	 * 判断当前单位是否是汇总单位
	 * @param unitcode
	 * @return
	 */
	public String isCollectUnit(String unitcode)
	{
		String flag="0";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from tt_organization where  unitcode='"+unitcode+"' and unitcode  in (select parentid from tt_organization) ");
			if(rowSet.next()) {
                flag="1";
            }
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	/**
	 * 判断当前单位是否是跟单位
	 * @param unitcode
	 * @return
	 */
	public String isRootUnit(String username)
	{
		String flag="0";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("   select * from tt_organization where  unitcode=parentid and unitcode in ( select unitcode from operuser where username='"+username+"' ) ");
			if(rowSet.next()) {
                flag="1";
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	/**
	 * 是否是顶层单位
	 * @param unitcode
	 * @return
	 */
	public String isTopUnit(String unitcode)
	{
		String flag="0";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from tt_organization  where   unitcode='"+unitcode+"' and unitcode=parentid");
			if(rowSet.next()) {
                flag="1";
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return flag;
	}
	
	
	public void saveDesc(String unitcode,int id,String columnName,String desc)
	{
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("u01");
			vo.setString("unitcode", unitcode);
			vo.setInt("id",id);
			 rowSet=dao.search("select * from u01 where id="+id+" and unitcode='"+unitcode+"'");
			if(rowSet.next())
			{
				vo=dao.findByPrimaryKey(vo);
				vo.setString(columnName.toLowerCase(),desc);
				dao.updateValueObject(vo);
			}
			else
			{
				vo.setString(columnName.toLowerCase(),desc);
				dao.addValueObject(vo);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		
	}
	
	
	/**
	 * 统一提交
	 * @param unitcode
	 * @param cycleVo
	 */
	public void subAllReport(String unitcode,RecordVo cycleVo,RecordVo vou01,String insert,String update)
	{
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 dao.delete("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+cycleVo.getInt("id"), new ArrayList());
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U01",1));
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U02_1",1));
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U02_2",1));
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U02_3",1));
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U02_4",1));
			 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U03",1));
			 
			 dao.update("update u02 set editflag=0 where editflag<>'2' and  id="+cycleVo.getInt("id")+" and unitcode='"+unitcode+"'");
			 dao.update("update u02 set editflag=3 where editflag='2' and  id="+cycleVo.getInt("id")+" and unitcode='"+unitcode+"'");
			 //dao.update("update u02 set editflag=3 where editflag='2' and  id="+cycleVo.getInt("id")+" and unitcode='"+unitcode+"'");
				
			 	if(cycleVo.getInt("kmethod")==0) //完整精算评估
			 				
			 {
				 dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U05",1));
				 insertU02Values(unitcode,cycleVo);
			 }
			 else {
                    dao.addValueObject(getRecordVo(unitcode,cycleVo.getInt("id"),"U04",1));
                }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	}
	

	/**
	 * 完整精算评估时，自动往表4里写入值
	 * @param unitcode
	 * @param cycleVo
	 */
	public void insertU02Values(String unitcode,RecordVo cycleVo)
	{
		RowSet rowSet =null;
		 try
		 {
			 ContentDAO dao=new ContentDAO(this.conn);
			 dao.delete("delete from u04 where id="+cycleVo.getInt("id")+"  and unitcode='"+unitcode+"'",new ArrayList());
			 String ext_sql =throwunit(""+cycleVo.getInt("id"),""); 
			 String sql = "select unitcode from tt_organization  where 1=1 "+ext_sql;
			 String flag=isCollectUnit(unitcode);  //是否是汇总单位
			 String unitcode_str=" unitcode='"+unitcode+"' ";
			 if("1".equals(flag)) {
                 unitcode_str=" unitcode like '"+unitcode+"%' ";
             }
			 
			  rowSet=dao.search("select escope,u0207,count(*) from u02 where "+unitcode_str+"  and id="+cycleVo.getInt("id")+"  and ( u0207='1' or u0207='2'or u0207='3' ) and unitcode in ( "+sql+") group by escope,u0207 ");
			
			 RecordVo vo_1=new RecordVo("u04");
			  RecordVo vo_2=new RecordVo("u04");
			  RecordVo vo_3=new RecordVo("u04");
				vo_1.setInt("id",cycleVo.getInt("id"));
				vo_1.setString("unitcode",unitcode);
				vo_1.setString("u0401","1");
				vo_1.setString("u0403",cycleVo.getString("theyear"));
				vo_1.setInt("editflag",0);
				
				vo_2.setInt("id",cycleVo.getInt("id"));
				vo_2.setString("unitcode",unitcode);
				vo_2.setString("u0401","4");
				vo_2.setString("u0403",cycleVo.getString("theyear"));
				vo_2.setInt("editflag",0);
			
			 
			 while(rowSet.next())
			 {
				 String escope=rowSet.getString("escope");
				 String u0207=rowSet.getString("u0207");
				 int    count=rowSet.getInt(3);
				 
				 if("1".equals(u0207))
				 {
					 if("1".equals(escope)) {
                         vo_1.setInt("u0405",count);
                     } else if("2".equals(escope)) {
                         vo_1.setInt("u0407",count);
                     }
					 if("3".equals(escope)) {
                         vo_1.setInt("u0409",count);
                     } else if("4".equals(escope)) {
                         vo_1.setInt("u0411",count);
                     }
		
				 }
				 if("2".equals(u0207))
				 {
					 if("1".equals(escope)) {
                         vo_2.setInt("u0405",count);
                     } else if("2".equals(escope)) {
                         vo_2.setInt("u0407",count);
                     }
					 if("3".equals(escope)) {
                         vo_2.setInt("u0409",count);
                     } else if("4".equals(escope)) {
                         vo_2.setInt("u0411",count);
                     }
				 }
				 if("3".equals(u0207))
				 {
					 if("1".equals(escope)) {
                         vo_2.setInt("u0405",count+vo_2.getInt("u0405"));
                     } else if("2".equals(escope)) {
                         vo_2.setInt("u0407",count+vo_2.getInt("u0407"));
                     }
					 if("3".equals(escope)) {
                         vo_2.setInt("u0409",count+vo_2.getInt("u0409"));
                     } else if("4".equals(escope)) {
                         vo_2.setInt("u0411",count+vo_2.getInt("u0411"));
                     }
				 }
				
			 }
			 
			 dao.addValueObject(vo_1);
			 dao.addValueObject(vo_2);
//			 dao.addValueObject(vo_3);
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
			finally{
				try
				 {
					 if(rowSet!=null) {
                         rowSet.close();
                     }
			 
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
			}
	}
	
	
	
	public RecordVo getRecordVo(String unitcode,int id,String report_id,int flag)
	{
		   RecordVo vo=new RecordVo("tt_calculation_ctrl");
			vo.setString("unitcode",unitcode);
			vo.setString("report_id",report_id);
			vo.setInt("id",id);
			vo.setInt("flag",flag);
			return vo;
	}
	
	
	/**
	 * 校验报表是否未填
	 * @param unitcode
	 * @param cycle_id
	 * @return
	 */
	public String validateReportFill(String unitcode,RecordVo cycleVo)
	{
		String info="";
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from  tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+cycleVo.getInt("id"));
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				
				map.put(rowSet.getString("report_id").toLowerCase(),rowSet.getString("flag"));
				
			}
			
			boolean flag=false;
			if(cycleVo.getInt("kmethod")==0){
			if(map.get("u01")==null&&map.get("u02_1")==null&&map.get("u02_2")==null&&map.get("u02_3")==null&&map.get("u02_4")==null) {
                flag=true;
            }
			}else{
				if(map.get("u01")==null&&map.get("u02_3")==null) {
                    flag=true;
                }
				if(map.get("u04")==null&&map.get("u01")==null)
				{
					info="表一，表四处于未填状态，不能统一提交!";
				}else{
				if(map.get("u04")==null)
				{
					info="表四处于未填状态，不能统一提交!";
				}
				}
			}
			if(flag)
			{
				if(cycleVo.getInt("kmethod")==0) //完整精算评估
				{
						if(map.get("u03")==null&&map.get("u05")==null)
						{
							info="所有报表都处于未填状态，不能统一提交!";
						}
				}
				else
				{
					if(map.get("u03")==null&&map.get("u04")==null)
					{
						info="报表都处于未填状态，不能统一提交!";
					}
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return info;
	}
	/**
	 * 校验报表是否未填
	 * @param unitcode
	 * @param cycle_id
	 * @return
	 */
	public String validateReportu02_3Fill(String unitcode,String id)
	{
		String info="0";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from  tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+id);
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				map.put(rowSet.getString("report_id").toLowerCase(),rowSet.getString("flag"));
			}
			if(map.get("u02_3")==null) {
                info="1";
            }
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return info;
	}
	
	
	
	/**
	 * 取得填报周期列表信息
	 * @param flag 0:代表所有  1：发布和结束状态的纪录
	 * @return
	 */
	public ArrayList getCycleList(int flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from tt_cycle where 1=1 ";
			if(flag==1) {
                sql+=" and ( status='04' or status='06' )";
            }
			sql+=" order by Bos_date desc";
			RowSet rowSet=dao.search(sql);
			
			while(rowSet.next())
			{
				CommonData cd=new CommonData(rowSet.getString("id"),rowSet.getString("name")+"("+AdminCode.getCodeName("23",rowSet.getString("status"))+")");
				list.add(cd);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得填报周期列表信息
	 * @param flag 0:代表所有  1：发布和结束状态的纪录
	 * @return
	 */
	public ArrayList getCycleList(int flag,String unitcode)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet =null;
		try
		{
			Date ssd=null;
			Date eed=null;
			ContentDAO dao=new ContentDAO(this.conn);
			int sy=0;int sm=0; int sd=0; int ey=0; int em=0; int ed=0;
			 rowSet=dao.search("select * from tt_organization  where unitcode='"+unitcode+"'");
			if(rowSet.next())
			{
				ssd=rowSet.getDate("start_date");
				eed=rowSet.getDate("end_date");
				Calendar d=Calendar.getInstance();
				d.setTime(ssd);
				sy=d.get(Calendar.YEAR);
				sm=d.get(Calendar.MONTH)+1;
				sd=d.get(Calendar.DATE);
				
				d.setTime(eed);
				ey=d.get(Calendar.YEAR);
				em=d.get(Calendar.MONTH)+1;
				ed=d.get(Calendar.DATE);
			}
			
			String sql="select * from tt_cycle where 1=1 ";
			if(flag==1) {
                sql+=" and ( status='04' or status='06' )";
            }
			sql+=" and ( "+Sql_switcher.year("bos_date")+">"+sy;
			sql+=" or ( "+Sql_switcher.year("bos_date")+"="+sy+" and "+Sql_switcher.month("bos_date")+">"+sm+" ) ";
			sql+=" or ( "+Sql_switcher.year("bos_date")+"="+sy+" and "+Sql_switcher.month("bos_date")+"="+sm+" and "+Sql_switcher.day("bos_date")+">="+sd+" ) ) ";
			sql+=" and ( "+Sql_switcher.year("bos_date")+"<"+ey;
			sql+=" or ( "+Sql_switcher.year("bos_date")+"="+ey+" and "+Sql_switcher.month("bos_date")+"<"+em+" ) ";
			sql+=" or ( "+Sql_switcher.year("bos_date")+"="+ey+" and "+Sql_switcher.month("bos_date")+"="+em+" and "+Sql_switcher.day("bos_date")+"<="+ed+" ) ) ";	 			
			
			
			
			sql+=" order by Bos_date desc";
		    rowSet=dao.search(sql);
			
			while(rowSet.next())
			{
				CommonData cd=new CommonData(rowSet.getString("id"),rowSet.getString("name")+"("+AdminCode.getCodeName("23",rowSet.getString("status"))+")");
				list.add(cd);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
	}
	
	
	
	
	/**
	 * 判断是否存在该周期
	 * @param cycleList
	 * @param cycle_id
	 * @return
	 */
	public boolean isExistCycle(ArrayList cycleList,String cycle_id)
	{
		boolean flag=false;
		CommonData cd=null;
		for(int i=0;i<cycleList.size();i++)
		{
			cd=(CommonData)cycleList.get(i);
			if(cycle_id.equals(cd.getDataValue()))
			{	
				flag=true;
				break;
			}
		}
		
		return flag;
	}
	
	
	/**
	 * 取得填报单位当前周期下各表的填报状态
	 * @param unitCode
	 * @param cycle_id
	 * @return
	 */
	public ArrayList getActuarialReportStatusList(String unitCode,String cycle_id)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			TTorganization ttOrganization=new TTorganization(this.conn);
			RecordVo vo2 =(RecordVo)ttOrganization.getSelfUnit2(unitCode);
			String parentid = vo2.getString("parentid");
			HashMap map=new HashMap();
			HashMap map_up=new HashMap();
			 rowSet=dao.search("select * from tt_calculation_ctrl where unitcode='"+parentid+"' and Id="+cycle_id);
			while(rowSet.next())
			{
				String  report_id=rowSet.getString("report_id");
				String  flag=rowSet.getString("flag");
				map_up.put(report_id.toUpperCase(),flag);
			}
			rowSet=dao.search("select * from tt_calculation_ctrl where unitcode='"+unitCode+"' and Id="+cycle_id);
			while(rowSet.next())
			{
				String  report_id=rowSet.getString("report_id");
				String  flag=rowSet.getString("flag");
				map.put(report_id.toUpperCase(),flag);
			}
			list.add(getDynaBean("U01",unitCode,cycle_id,map,map_up));
			
			rowSet=dao.search("select * from codeitem where codesetid='61' order by codeitemid");	
			while(rowSet.next())
			{
				String codeitemid=rowSet.getString("codeitemid");
				list.add(getDynaBean("U02_"+codeitemid,unitCode,cycle_id,map,map_up));
			}
			if(vo.getInt("kmethod")==0) //完整精算评估法
			{
				list.add(getDynaBean("U03",unitCode,cycle_id,map,map_up));
				list.add(getDynaBean("U05",unitCode,cycle_id,map,map_up));
			}
			else if(vo.getInt("kmethod")==1) //向前滚动法
			{
				list.add(getDynaBean("U03",unitCode,cycle_id,map,map_up));
				list.add(getDynaBean("U04",unitCode,cycle_id,map,map_up));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		
		return list;
	}
	
	public LazyDynaBean getDynaBean(String tabid,String unitCode,String cycle_id,HashMap statusmap,HashMap statusmap_up)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("tabid",tabid);
		abean.set("unitCode",unitCode);
		abean.set("cycle_id",cycle_id);
		String name="";
		if("U01".equals(tabid)) {
            name="表1 特别事项";
        } else if("U03".equals(tabid)) {
            name="表3 财务信息";
        } else if("U04".equals(tabid)) {
            name="表4 人员统计表";
        } else if("U05".equals(tabid)) {
            name="表5 人员变动及人均福利对照表";
        } else
		{
			String[] temps=tabid.split("_");
			String _name=AdminCode.getCodeName("61",temps[1]);
			name="表2-"+temps[1]+" "+_name;
		}
		abean.set("name",name);
		if(statusmap.get(tabid)!=null)
		{
			String status=(String)statusmap.get(tabid);
			abean.set("status",status);
			if(statusmap_up!=null&&statusmap_up.get(tabid)!=null) {
                abean.set("status_up",(String)statusmap_up.get(tabid));
            } else {
                abean.set("status_up","-1");
            }
			if("-1".equalsIgnoreCase(status)) {
                abean.set("status_desc","未填");
            } else if("0".equalsIgnoreCase(status)) {
                abean.set("status_desc","正在编辑");
            } else if("1".equalsIgnoreCase(status)) {
                abean.set("status_desc","已上报");
            } else if("2".equalsIgnoreCase(status)) {
                abean.set("status_desc","<a href=\"javascript:description('"+tabid+"','"+unitCode+"','"+cycle_id+"')\">驳回</a>");
            } else if("3".equalsIgnoreCase(status)) {
                abean.set("status_desc","封存");
            }
		}
		else
		{
			abean.set("status_up","-1");
			abean.set("status","-1");
			abean.set("status_desc","未填");
		}
		return abean;
	}
	
	
	
	
//	根据用户名得到其填报单位的id
	public String getUnitcode(String userName)
	{
		String unitcode="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select unitcode from operuser where userName='"+userName+"'");
			if(recset.next()) {
                unitcode=recset.getString(1);
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
				 if(recset!=null) {
                     recset.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return unitcode;
	}

	
	
	/** ------------------------操作表3----------------------------   */
	
	
	/**
	 * 对于完整精算评估检查 对比数据，判断是否符合提交条件
	 */
	public String checkCompareDataList(ArrayList compareDataList,ArrayList compareDataList_5,String id,String unitcode,ArrayList dataHeadList,ArrayList dataHeadList_u05)
	{
		String info="1";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			//判断是否对警告内容进行文字解释 （表1是否提交）
			boolean isSub_u03=false;
			boolean isSub_u05=false;
			
			RecordVo vo=new RecordVo("u01");
			vo.setInt("id",Integer.parseInt(id));
			vo.setString("unitcode",unitcode);
			try{
			vo=dao.findByPrimaryKey(vo);
			if(vo.getString("t3_desc")!=null&&vo.getString("t3_desc").trim().length()>0) {
                isSub_u03=true;
            }
			if(vo.getString("t5_desc")!=null&&vo.getString("t5_desc").trim().length()>0) {
                isSub_u05=true;
            }
			}catch(Exception e){
				
			}
			boolean flag=true;
			if(!isSub_u03)
			{
				
				for(int i=0;i<compareDataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)compareDataList.get(i);
					
					LazyDynaBean _bean=new LazyDynaBean();
					for(int j=3;j<dataHeadList.size();j++)
					{
						String temp=(String)dataHeadList.get(j);	
						_bean=(LazyDynaBean)abean.get(temp);
						String over=(String)_bean.get("over");
						if("1".equals(over))
						{
							info="0";
							flag=false;
							//如果U03是上报状态需要改为正在编辑0
							RecordVo vo2=new RecordVo("tt_calculation_ctrl");
							vo2.setInt("id", Integer.parseInt(id));
							vo2.setString("unitcode",unitcode);
							vo2.setString("report_id","U03");
							try{
							vo2=dao.findByPrimaryKey(vo2);
							if(vo2.getInt("flag")==1){
							vo2.setInt("flag", 0);	
							dao.updateValueObject(vo2);
							}
							}catch(Exception e){
								
							}
							break;
						}
					}
					if(!flag) {
                        break;
                    }
				}
			}	
			if(!isSub_u05)	
			{
					flag=true;
					for(int i=0;i<compareDataList_5.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)compareDataList_5.get(i);
						
						LazyDynaBean _bean=new LazyDynaBean();
						for(int j=1;j<dataHeadList_u05.size();j++)
						{
							String temp=(String)dataHeadList_u05.get(j);	
							_bean=(LazyDynaBean)abean.get(temp);
							String over=(String)_bean.get("over");
							if("1".equals(over))
							{
								if("0".equals(info)) {
                                    info="-2";
                                } else {
                                    info="-1";
                                }
								flag=false;
								break;
							}
						}
						if(!flag) {
                            break;
                        }
					}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
	/**
	 * 保存填报状态
	 * @param unitcode
	 * @param report_id
	 * @param cycle_id
	 * @param flag
	 */
	public void saveReportStatus(String unitcode,String report_id,String cycle_id,int flag)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RecordVo vo=new RecordVo("tt_calculation_ctrl");
			vo.setString("unitcode",unitcode);
			vo.setString("report_id",report_id);
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo.setInt("flag",flag);
			
			dao.update("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+cycle_id+" and report_id='"+report_id+"'");
			dao.addValueObject(vo);
			
			if("U03".equalsIgnoreCase(report_id)&&flag==1) {
                dao.update("update u03 set editflag=0 where unitcode='"+unitcode+"' and id="+cycle_id);
            }
			if("U04".equalsIgnoreCase(report_id)&&flag==1) {
                dao.update("update u04 set editflag=0 where unitcode='"+unitcode+"' and id="+cycle_id);
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	

	/**
	 * 撤销单位 (目前只提供基层单位的撤销和指定)
	 * @param current_unit  当前填报单位
	 * @param to_unit       目标单位
	 * @param cycle_id      填报周期
	 */
	public void autoCompute(String current_unit,String to_unit,String cycle_id)
	{
		//自动计算
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			int kmethod=vo.getInt("kmethod");  //=0，完整精算评估法 	=1，向前滚动法
			Calendar cd=Calendar.getInstance();
			cd.setTime(vo.getDate("bos_date"));
			String theyear=String.valueOf(cd.get(Calendar.YEAR));
			StringBuffer sql=new StringBuffer("");
			boolean cur_isBase=true;  //撤销单位是否是基层单位
			 rowSet=dao.search("select count(*) from tt_organization where parentid='"+current_unit+"' and unitcode<>parentid");
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    cur_isBase=false;
                }
			}
			if(cur_isBase)
			{
				//防止主键造成冲突，先删掉目标单位中存在要撤销单位的人员
				//dao.update("delete  from u02 where u0200 in(select u0200 from u02 where id="+cycle_id+" and unitcode='"+current_unit+"') and id ="+cycle_id+" and unitcode='"+to_unit+"'");
				dao.update("update u02 set unitcode='"+to_unit+"' where id="+cycle_id+" and unitcode='"+current_unit+"' and u0200 not in (select u0200 from u02 where id="+cycle_id+" and unitcode='"+to_unit+"' )");
				dao.update("delete  from u02 where id="+cycle_id+" and unitcode='"+current_unit+"'");
				//	if(kmethod==0) //0 完整精算评估法
				{
					//保存表3数据
					boolean isData=false;
					rowSet=dao.search("select * from u03 where  id="+cycle_id+"   and unitcode='"+to_unit+"'");
					if(rowSet.next())
					{
						isData=true;
					}
					
					if(!isData)
					{
						String update_sql1 = " insert into  u03(u0301,id,unitcode,u0303,editflag) values('1',"+cycle_id+",'"+to_unit+"','"+theyear+"','1') ";
						String update_sql2 = " insert into  u03(u0301,id,unitcode,u0303,editflag) values('2',"+cycle_id+",'"+to_unit+"','"+theyear+"','1') ";
						dao.update(update_sql1);
						dao.update(update_sql2);
					}
					
					sql.setLength(0);
					sql.append("select sum("+Sql_switcher.isnull("U0305","0")+") U0305,sum("+Sql_switcher.isnull("U0307","0")+") U0307,sum("+Sql_switcher.isnull("U0309","0")+") U0309,sum("+Sql_switcher.isnull("U0311","0")+") U0311 ");
					sql.append(",sum("+Sql_switcher.isnull("U0313","0")+") U0313,sum("+Sql_switcher.isnull("U0315","0")+") U0315,sum("+Sql_switcher.isnull("U0317","0")+") U0317 from u03 where ");
					sql.append("  id="+cycle_id+" and ( unitcode='"+current_unit+"' or unitcode='"+to_unit+"' ) and  u0301=1");
					rowSet=dao.search(sql.toString());
					if(rowSet.next())
					{
						sql.setLength(0);
						sql.append(" update u03 set U0305="+rowSet.getFloat("U0305")+",U0307="+rowSet.getFloat("U0307"));
						sql.append(",U0309="+rowSet.getFloat("U0309")+",U0311="+rowSet.getFloat("U0311")+" ");
						sql.append(",U0313="+rowSet.getFloat("U0313")+",U0315="+rowSet.getFloat("U0315")+" ");
						sql.append(",U0317="+rowSet.getFloat("U0317"));
						sql.append(" where  id="+cycle_id);
						sql.append(" and  unitcode='"+to_unit+"' and  u0301=1 ");
						dao.update(sql.toString());
					}
					
					
					sql.setLength(0);
					sql.append("select sum("+Sql_switcher.isnull("U0305","0")+") U0305,sum("+Sql_switcher.isnull("U0307","0")+") U0307,sum("+Sql_switcher.isnull("U0309","0")+") U0309,sum("+Sql_switcher.isnull("U0311","0")+") U0311 ");
					sql.append(",sum("+Sql_switcher.isnull("U0313","0")+") U0313,sum("+Sql_switcher.isnull("U0315","0")+") U0315,sum("+Sql_switcher.isnull("U0317","0")+") U0317 from u03 where ");
					sql.append("  id="+cycle_id+" and ( unitcode='"+current_unit+"' or unitcode='"+to_unit+"' ) and  u0301=2");
					rowSet=dao.search(sql.toString());
					if(rowSet.next())
					{
						sql.setLength(0);
						sql.append(" update u03 set U0305="+rowSet.getFloat("U0305")+",U0307="+rowSet.getFloat("U0307"));
						sql.append(",U0309="+rowSet.getFloat("U0309")+",U0311="+rowSet.getFloat("U0311")+" ");
						sql.append(",U0313="+rowSet.getFloat("U0313")+",U0315="+rowSet.getFloat("U0315")+" ");
						sql.append(",U0317="+rowSet.getFloat("U0317"));
						sql.append(" where  id="+cycle_id);
						sql.append(" and  unitcode='"+to_unit+"' and  u0301=2 ");
						dao.update(sql.toString());
					}
					
					boolean isCurrentNode=false;
					rowSet=dao.search("select * from u01 where id="+cycle_id+" and unitcode='"+current_unit+"'");
					if(rowSet.next()) {
                        isCurrentNode=true;
                    }
					boolean isToNode=false;
					rowSet=dao.search("select * from u01 where id="+cycle_id+" and unitcode='"+to_unit+"'");
					if(rowSet.next()) {
                        isToNode=true;
                    }
					if(isCurrentNode&&!isToNode)
					{
						dao.update("update u01 set unitcode='"+to_unit+"' where id="+cycle_id+" and unitcode='"+current_unit+"'");
					}
					if(isCurrentNode&&isToNode)
					{
						RecordVo vo01=new RecordVo("u01");
						vo01.setInt("id",Integer.parseInt(cycle_id));
						vo01.setString("unitcode",current_unit);
						vo01=dao.findByPrimaryKey(vo01);
						String u0101=vo01.getString("u0101");
						String u0103=vo01.getString("u0103");
						String t3_desc=vo01.getString("t3_desc");
						String t5_desc=	vo01.getString("t5_desc");
						
						RecordVo _vo01=new RecordVo("u01");
						_vo01.setInt("id",Integer.parseInt(cycle_id));
						_vo01.setString("unitcode",to_unit);
						_vo01=dao.findByPrimaryKey(_vo01);	
						String _u0101=_vo01.getString("u0101");
						String _u0103=_vo01.getString("u0103");
						String _t3_desc=_vo01.getString("t3_desc");
						String _t5_desc=_vo01.getString("t5_desc");
						
						if(u0101.trim().length()>0&&!"无".equals(u0101))
						{
							if(_u0101.trim().length()>0&& "无".equals(_u0101)) {
                                _u0101="";
                            }
							_vo01.setString("u0101",_u0101+"  "+u0101);
						}
						if(u0103.trim().length()>0&&!"无".equals(u0103))
						{
							if(_u0103.trim().length()>0&& "无".equals(_u0103)) {
                                _u0103="";
                            }
							_vo01.setString("u0103",_u0103+"  "+u0103);
						}
						if(t3_desc.trim().length()>0&&!"无".equals(t3_desc))
						{
							_vo01.setString("t3_desc",_t3_desc+"  "+t3_desc);
						}
						if(t5_desc.trim().length()>0&&!"无".equals(t5_desc))
						{	
							_vo01.setString("t5_desc",_t5_desc+"  "+t5_desc);
						
						}
						dao.updateValueObject(_vo01);
					}
				}
			}
			dao.update("delete from u01 where id="+cycle_id+" and unitcode='"+current_unit+"'");
			dao.update("delete from u03 where id="+cycle_id+" and unitcode='"+current_unit+"'");
			 
			
			
			
			
			if(kmethod==0) //0 完整精算评估法
			{
				dao.update("delete from U05 where unitcode='"+current_unit+"' and id="+cycle_id);
				saveU05Values(to_unit,cycle_id,theyear);
			}
			else           //1 向前滚动法
			{
				
				//保存表4数据
				if(cur_isBase)
				{
					
					
					boolean isData=false;
					rowSet=dao.search("select * from u04 where  id="+cycle_id+"   and unitcode='"+to_unit+"'");
					if(rowSet.next())
					{
						isData=true;
					}
					
					if(!isData)
					{
						String update_sql1 = " insert into  u04(u0401,id,unitcode,u0403,editflag) values('1',"+cycle_id+",'"+to_unit+"','"+theyear+"','1') ";
						String update_sql2 = " insert into  u04(u0401,id,unitcode,u0403,editflag) values('4',"+cycle_id+",'"+to_unit+"','"+theyear+"','1') ";
						dao.update(update_sql1);
						dao.update(update_sql2);
					}
					
					sql.setLength(0);
					sql.append("select sum("+Sql_switcher.isnull("u0405","0")+") u0405,sum("+Sql_switcher.isnull("u0407","0")+") u0407,sum("+Sql_switcher.isnull("u0409","0")+") u0409,sum("+Sql_switcher.isnull("u0411","0")+") u0411 ");
					sql.append("   from u04 where ");
					sql.append("  id="+cycle_id+" and ( unitcode='"+current_unit+"' or unitcode='"+to_unit+"' ) and  u0401=1");
					rowSet=dao.search(sql.toString());
					if(rowSet.next())
					{
						sql.setLength(0);
						sql.append(" update u04 set U0405="+rowSet.getFloat("U0405")+",U0407="+rowSet.getFloat("U0407"));
						sql.append(",U0409="+rowSet.getFloat("U0409")+",U0411="+rowSet.getFloat("U0411")+" ");					 
						sql.append(" where  id="+cycle_id);
						sql.append(" and  unitcode='"+to_unit+"' and  u0401=1 ");
						dao.update(sql.toString());
					}
					
					
					sql.setLength(0);
					sql.append("select sum("+Sql_switcher.isnull("u0405","0")+") u0405,sum("+Sql_switcher.isnull("u0407","0")+") u0407,sum("+Sql_switcher.isnull("u0409","0")+") u0409,sum("+Sql_switcher.isnull("u0411","0")+") u0411 ");
					sql.append("   from u04 where ");
					sql.append("  id="+cycle_id+" and ( unitcode='"+current_unit+"' or unitcode='"+to_unit+"' ) and  u0401=4");
					rowSet=dao.search(sql.toString());
					if(rowSet.next())
					{
						sql.setLength(0);
						sql.append(" update u04 set U0405="+rowSet.getFloat("U0405")+",U0407="+rowSet.getFloat("U0407"));
						sql.append(",U0409="+rowSet.getFloat("U0409")+",U0411="+rowSet.getFloat("U0411")+" ");			 
						sql.append(" where  id="+cycle_id);
						sql.append(" and  unitcode='"+to_unit+"' and  u0401=4 ");
						dao.update(sql.toString());
					}
				}
				dao.update("delete from u04 where id="+cycle_id+" and unitcode='"+current_unit+"'");	
			}
			
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	/**
	 * 划转单位 (目前只提供基层单位的划转和指定)
	 * @param current_unit  当前填报单位
	 * @param to_unit       目标单位
	 * @param cycle_id      填报周期
	 */
	public void autoTransferCompute(String current_unit,String to_unit)
	{
		//自动计算
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			boolean cur_isBase=true;  //撤销单位是否是基层单位
			 rowSet=dao.search("select count(*) from tt_organization where parentid='"+current_unit+"' and unitcode<>parentid");
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0) {
                    cur_isBase=false;
                }
			}
			if(cur_isBase)
			{
				dao.update("update u01 set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				dao.update("update u02 set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				dao.update("update u03 set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				dao.update("update u04 set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				dao.update("update u05 set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				dao.update("update tt_calculation_ctrl set unitcode='"+to_unit+"' where   unitcode='"+current_unit+"'");
				
		
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	
	
	
	/**
	 * 保存表5数据
	 * @param unitcode
	 * @param cycle_id
	 * @param theyear
	 */
	public void saveU05Values(String unitcode,String cycle_id,String theyear)
	{
		RowSet rowSet=null;
		try
		{
			String flag=isCollectUnit(unitcode);  //是否是汇总单位
			
			ContentDAO dao=new ContentDAO(this.conn);
			String U0505="NULL"; //离退人数
			String U0511="NULL"; //退休人数
			String U0517="NULL"; //内退人数
			String U0523="NULL"; //遗属人数
			 String ext_sql =throwunit(cycle_id,""); 
			 String sql2 = "select unitcode from tt_organization  where 1=1 "+ext_sql;
			String unitcode_str=" unitcode='"+unitcode+"'  ";
			if("1".equals(flag)) {
                unitcode_str=" unitcode like '"+unitcode+"%' and unitcode in( "+sql2+") ";
            }
			
			 rowSet=dao.search("select count(U0200) from u02 where id="+cycle_id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' ) and u0207='1' and escope='1' ");
			if(rowSet.next()) {
                U0505=rowSet.getInt(1)!=0?rowSet.getString(1):"NULL";
            }
			rowSet=dao.search("select count(U0200) from u02 where id="+cycle_id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' ) and u0207='1' and escope='2'");
			if(rowSet.next()) {
                U0511=rowSet.getInt(1)!=0?rowSet.getString(1):"NULL";
            }
			rowSet=dao.search("select count(U0200) from u02 where id="+cycle_id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' ) and u0207='1' and escope='3'");
			if(rowSet.next()) {
                U0517=rowSet.getInt(1)!=0?rowSet.getString(1):"NULL";
            }
			rowSet=dao.search("select count(U0200) from u02 where id="+cycle_id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' ) and u0207='1' and escope='4'");
			if(rowSet.next()) {
                U0523=rowSet.getInt(1)!=0?rowSet.getString(1):"NULL";
            }
				
				
			StringBuffer sql_sub=new StringBuffer(" select U0305,U0307,U0309,U0311,U0313,U0315,U0317 from ");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+") U0305,SUM(("+Sql_switcher.isnull("U0211","0")+"+"+Sql_switcher.isnull("U0213","0")+")*12+"+Sql_switcher.isnull("U0215","0")+") U0307 from u02 where id="+cycle_id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' )  and u0207='1' and escope='1' ) a1,");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+"+"+Sql_switcher.isnull("U0223","0")+") U0309,SUM(("+Sql_switcher.isnull("U0211","0")+"+"+Sql_switcher.isnull("U0213","0")+")*12+"+Sql_switcher.isnull("U0215","0")+") U0311 from u02 where id="+cycle_id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='2') a2,");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+"+"+Sql_switcher.isnull("U0223","0")+") U0313,SUM(("+Sql_switcher.isnull("U0225","0")+"+"+Sql_switcher.isnull("U0226","0")+"+"+Sql_switcher.isnull("U0227","0")+"+"+Sql_switcher.isnull("U0229","0")+")*12+"+Sql_switcher.isnull("U0231","0")+") U0315 from u02 where id="+cycle_id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='3') a3,");
			sql_sub.append(" (select SUM("+Sql_switcher.isnull("U0235","0")+"*12+"+Sql_switcher.isnull("U0237","0")+") U0317 from u02 where id="+cycle_id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='4') a4");
					
			
				
		//	U0505="2";U0511="2";U0517="2";U0523="2";
			StringBuffer sql=new StringBuffer("select U0305/"+U0505+" U0507,U0307/"+U0505+" U0509,U0309/"+U0511+" U0513,U0311/"+U0511+" U0515,U0313/"+U0517+" U0519,U0315/"+U0517+" U0521,U0317/"+U0523+" U0525 from  ");
			sql.append(" ("+sql_sub.toString()+") a");
			rowSet=dao.search(sql.toString());
			RecordVo vo=new RecordVo("u05");
			vo.setString("unitcode",unitcode);
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo.setString("u0503",theyear);
			if(rowSet.next())
			{
				if(!"NULL".equalsIgnoreCase(U0505))
				{
					vo.setInt("u0505",Integer.parseInt(U0505));
					vo.setDouble("u0507",Double.parseDouble(PubFunc.round(rowSet.getString("u0507"),3)));
					vo.setDouble("u0509", Double.parseDouble(PubFunc.round(rowSet.getString("u0509"),3)));
				}
				if(!"NULL".equalsIgnoreCase(U0511))
				{
					vo.setInt("u0511",Integer.parseInt(U0511));
					vo.setDouble("u0513",Double.parseDouble(PubFunc.round(rowSet.getString("u0513"),3)));
					vo.setDouble("u0515", Double.parseDouble(PubFunc.round(rowSet.getString("u0515"),3)));
				}
				if(!"NULL".equalsIgnoreCase(U0517))
				{
					vo.setInt("u0517",Integer.parseInt(U0517));
					vo.setDouble("u0519",Double.parseDouble(PubFunc.round(rowSet.getString("u0519"),3)));
					vo.setDouble("u0521", Double.parseDouble(PubFunc.round(rowSet.getString("u0521"),3)));
				}
				if(!"NULL".equalsIgnoreCase(U0523))
				{
					vo.setInt("u0523",Integer.parseInt(U0523));
					vo.setDouble("u0525",Double.parseDouble(PubFunc.round(rowSet.getString("u0525"),3)));
				}
			}
			dao.update("delete from U05 where unitcode='"+unitcode+"' and id="+cycle_id);
			dao.addValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		
	}
	
	
	/**
	 * 保存表4的填报数据
	 */
	public void saveU04Values(String unitcode,String cycle_id,String theyear,String current_values)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RecordVo vo_1=new RecordVo("u04");
			RecordVo vo_2=new RecordVo("u04");
			vo_1.setInt("id",Integer.parseInt(cycle_id));
			vo_1.setString("unitcode",unitcode);
			vo_1.setString("u0401","1");
			vo_1.setString("u0403",theyear);
			vo_1.setInt("editflag",1);
			
			vo_2.setInt("id",Integer.parseInt(cycle_id));
			vo_2.setString("unitcode",unitcode);
			vo_2.setString("u0401","4");
			vo_2.setString("u0403",theyear);
			vo_2.setInt("editflag",1);
			
			String[] temps=current_values.split("~");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()==0) {
                    continue;
                }
				String[] temps2=temps[i].split(":");
				if(temps2.length==2&&temps2[1].trim().length()>0)
				{
					String[] temps3=temps2[0].split("_");
					if("1".equals(temps3[1])) {
                        vo_1.setDouble(temps3[0].toLowerCase(),Integer.parseInt(temps2[1].trim()));
                    } else if("4".equals(temps3[1])) {
                        vo_2.setDouble(temps3[0].toLowerCase(),Integer.parseInt(temps2[1].trim()));
                    }
				}
			}
			dao.delete("delete from U04 where ( U0401=1 or U0401=4 ) and unitcode='"+unitcode+"' and id="+cycle_id, new ArrayList());	
			dao.addValueObject(vo_1);
			dao.addValueObject(vo_2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * 校验表4的填报数据
	 */
	public String  validateU04Values(String unitcode,String cycle_id,String current_values)
	{
		String info="";
		
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("tt_cycle");
		vo.setInt("id",Integer.parseInt(cycle_id));
		RowSet rs=null;
		try
		{
			
			vo=dao.findByPrimaryKey(vo);
			String date_str=DateUtils.format(vo.getDate("bos_date"), "yyyy-MM-dd");
			vo = dao.findByPrimaryKey(vo);
			String sql = " select * from tt_cycle where bos_date<"+Sql_switcher.dateValue(date_str)+"  order by bos_date desc";
			
			SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");	
			rs=dao.search(sql);	
			int u02_1=0;
			int u02_2=0;
			int u02_3=0;
			int u02_4=0;
			if(rs.next()){
			
					
				
			String 	up_id=""+rs.getInt("id");
			String  theyear= rs.getString("theyear");
			String kmethod=""+rs.getInt("kmethod");
			String bos_date=DateUtils.format(rs.getDate("bos_date"), "yyyy-MM-dd");
			String _temp=mf.format(rs.getDate("bos_date"));
			String unitcodes="";
			
			String sqll = " select unitcode from tt_organization where d_unitcode='"+unitcode+"' and  end_date<"+Sql_switcher.dateValue(date_str)+" and end_date>"+Sql_switcher.dateValue(bos_date)+" ";
			rs=dao.search(sqll);
			while(rs.next()){
				unitcodes+=rs.getString("unitcode")+",";
			}
			
			String sqlstr="";
			if("0".equals(kmethod)){
			 sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0207=1 and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
			rs=dao.search(sqlstr);
			if(rs.next()) {
                u02_1 =  rs.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and u0207=1  and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			rs=dao.search(sqlstr);
			if(rs.next()) {
                u02_2 =   rs.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and u0207=1  and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			rs=dao.search(sqlstr);
			if(rs.next()) {
                u02_3 =   rs.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and u0207=1  and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			rs=dao.search(sqlstr);
			if(rs.next()) {
                u02_4 =   rs.getInt(1);
            }
			}else{
				 sqlstr = " select u0405  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and u0401=1     ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_1 =  rs.getInt(1);
                    }
					 sqlstr = " select u0407  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401=1   ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_2 =   rs.getInt(1);
                    }
					 sqlstr = " select u0409  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401=1  ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_3 =   rs.getInt(1);
                    }
					 sqlstr = " select u0411  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401=1   ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_4 =   rs.getInt(1);
                    }
			}
			if(!"".equals(unitcodes)){
				String []units = unitcodes.split(",");
				for(int i=0;i<units.length;i++){
					String unitid = units[i];
					if("".equals(unitid.trim())) {
                        continue;
                    }
					if("0".equals(kmethod)){
					 sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0207=1  and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_1 +=  rs.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207=1  and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_2 +=   rs.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207=1  and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_3 +=   rs.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207=1  and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					rs=dao.search(sqlstr);
					if(rs.next()) {
                        u02_4 +=   rs.getInt(1);
                    }
					}else{
						
							 sqlstr = " select u0405  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0401=1     ";
								rs=dao.search(sqlstr);
								if(rs.next()) {
                                    u02_1 +=  rs.getInt(1);
                                }
								 sqlstr = " select u0407  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401=1   ";
								rs=dao.search(sqlstr);
								if(rs.next()) {
                                    u02_2 +=   rs.getInt(1);
                                }
								 sqlstr = " select u0409  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401=1  ";
								rs=dao.search(sqlstr);
								if(rs.next()) {
                                    u02_3 +=   rs.getInt(1);
                                }
								 sqlstr = " select u0411  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401=1   ";
								rs=dao.search(sqlstr);
								if(rs.next()) {
                                    u02_4 +=   rs.getInt(1);
                                }
						
					}
				}
			}
			
			}
		
		try{
			
			

			String[] temps=current_values.split("~");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()==0) {
                    continue;
                }
				String[] temps2=temps[i].split(":");
				if(temps2.length==2&&temps2[1].trim().length()>0)
				{
					String[] temps3=temps2[0].split("_");
					if("1".equals(temps3[1])){
						
							if("u0405".equals(temps3[0].toLowerCase())){
								if(u02_1<Integer.parseInt(temps2[1].trim())) {
                                    info +="离休人员,";
                                }
							}
							if("u0407".equals(temps3[0].toLowerCase())){
								if(u02_2<Integer.parseInt(temps2[1].trim())) {
                                    info +="退休人员,";
                                }
							}
							if("u0409".equals(temps3[0].toLowerCase())){
								if(u02_3<Integer.parseInt(temps2[1].trim())) {
                                    info +="内退人员,";
                                }
							}
							if("u0411".equals(temps3[0].toLowerCase())){
								if(u02_4<Integer.parseInt(temps2[1].trim())) {
                                    info +="遗属,";
                                }
							}
						
					}
					
				}
			}
			if(!"".equals(info)){
				info+="输入的值大于上一次2007年3月31日前原有人员的值";
			}
		}catch(Exception e2){
			
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
					 if(rs!=null) {
                         rs.close();
                     }
					
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
		}
		return info;
	}
	
	/**
	 * 保存表3的填报数据
	 */
	public void saveU03Values(String unitcode,String cycle_id,String theyear,String current_values)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RecordVo vo_1=new RecordVo("u03");
			RecordVo vo_2=new RecordVo("u03");
			vo_1.setInt("id",Integer.parseInt(cycle_id));
			vo_1.setString("unitcode",unitcode);
			vo_1.setString("u0301","1");
			vo_1.setString("u0303",theyear);
			vo_1.setInt("editflag",1);
			
			vo_2.setInt("id",Integer.parseInt(cycle_id));
			vo_2.setString("unitcode",unitcode);
			vo_2.setString("u0301","2");
			vo_2.setString("u0303",theyear);
			vo_2.setInt("editflag",1);
			
			String[] temps=current_values.split("~");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i].trim().length()==0) {
                    continue;
                }
				String[] temps2=temps[i].split(":");
				if(temps2.length==2&&temps2[1].trim().length()>0)
				{
					String[] temps3=temps2[0].split("_");
					if("1".equals(temps3[1])) {
                        vo_1.setDouble(temps3[0].toLowerCase(),Double.parseDouble(temps2[1].trim()));
                    } else if("2".equals(temps3[1])) {
                        vo_2.setDouble(temps3[0].toLowerCase(),Double.parseDouble(temps2[1].trim()));
                    }
				}
			}
			dao.delete("delete from U03 where ( U0301=1 or U0301=2 ) and unitcode='"+unitcode+"' and id="+cycle_id, new ArrayList());	
			dao.addValueObject(vo_1);
			dao.addValueObject(vo_2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public ArrayList getDataHeadList_U04()
	{
		ArrayList list=new ArrayList();
		list.add("theyear");
		list.add("person_type");

		list.add("U0405");
		list.add("U0407");
		list.add("U0409");
		list.add("U0411");
		return list;
	}
	
	
	
	public ArrayList getDataHeadList_U03()
	{
		ArrayList list=new ArrayList();
		list.add("theyear");
		list.add("cycle_name");
		list.add("person_type");

		list.add("U0305");
		list.add("U0307");
		list.add("U0309");
		list.add("U0311");
		list.add("U0313");
		list.add("U0315");
		list.add("U0317");
		return list;
	}
	public ArrayList getWarnHeadList_U03()
	{
		ArrayList list=new ArrayList();
		list.add("unitname");
		list.add("t3_desc");
		return list;
	}
	
	
	/**
	 * 取得表4人员数据
	 * @param id
	 * @param unitcode
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getU04DataList(String id,String unitcode,ArrayList dataHeadList)
	{
		ArrayList list=new ArrayList();
		ArrayList list2=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		LazyDynaBean bean1 =new LazyDynaBean();
		LazyDynaBean bean2 =new LazyDynaBean();
		LazyDynaBean bean3 =new LazyDynaBean();
		LazyDynaBean bean4 =new LazyDynaBean();
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			String date_str=DateUtils.format(vo.getDate("bos_date"), "yyyy-MM-dd");
			vo = dao.findByPrimaryKey(vo);
			String ext_sql = " select unitcode from tt_organization where 1=1 "+this.throwunit(id, "");
		
			String sql = " select * from tt_cycle where bos_date<"+Sql_switcher.dateValue(date_str)+"  order by bos_date desc";
			
			SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");	
			recset=dao.search(sql);	
			int u02_1=0;
			int u02_2=0;
			int u02_3=0;
			int u02_4=0;
			if(recset.next()){
			
					
				
			String 	up_id=""+recset.getInt("id");
			String  theyear= recset.getString("theyear");
			String kmethod=""+recset.getInt("kmethod");
			String bos_date=DateUtils.format(recset.getDate("bos_date"), "yyyy-MM-dd");
			String _temp=mf.format(recset.getDate("bos_date"));
			String unitcodes="";
			//获得该单位的下级单位
			
			String sqll = " select unitcode from tt_organization where d_unitcode like '"+unitcode+"%' and  end_date<"+Sql_switcher.dateValue(date_str)+" and end_date>"+Sql_switcher.dateValue(bos_date)+" ";
			recset=dao.search(sqll);
			while(recset.next()){
				if(unitcodes.indexOf(recset.getString("unitcode")+",")==-1) {
                    unitcodes+=recset.getString("unitcode")+",";
                }
			}
			for(int m=1;m<5;){
			String sqlstr="";
			if("0".equals(kmethod)){
				if(m==1){
			 sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207="+m+" and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
			recset=dao.search(sqlstr);
			if(recset.next()) {
                u02_1 =  recset.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207="+m+"  and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			recset=dao.search(sqlstr);
			if(recset.next()) {
                u02_2 =   recset.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207="+m+"  and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			recset=dao.search(sqlstr);
			if(recset.next()) {
                u02_3 =   recset.getInt(1);
            }
			 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207="+m+"  and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
			recset=dao.search(sqlstr);
			if(recset.next()) {
                u02_4 =   recset.getInt(1);
            }
				}else{
					sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")   and u0207!=1 and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_1 =  recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207!=1   and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_2 =   recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207!=1   and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_3 =   recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and unitcode in("+ext_sql+")  and u0207!=1   and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_4 =   recset.getInt(1);
                    }
				}
			}else{
				 sqlstr = " select u0405  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  ) and u0401="+m+"     ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_1 =  recset.getInt(1);
                    }
					 sqlstr = " select u0407  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401="+m+"   ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_2 =   recset.getInt(1);
                    }
					 sqlstr = " select u0409  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401="+m+"  ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_3 =   recset.getInt(1);
                    }
					 sqlstr = " select u0411  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitcode+"%'  )  and u0401="+m+"   ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_4 =   recset.getInt(1);
                    }
			}
			if(!"".equals(unitcodes)){
				String []units = unitcodes.split(",");
				for(int i=0;i<units.length;i++){
					String unitid = units[i];
					if("".equals(unitid.trim())) {
                        continue;
                    }
					if("0".equals(kmethod)){
						if(m==1){
					 sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0207="+m+"  and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_1 +=  recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207="+m+"  and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_2 +=   recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207="+m+"  and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_3 +=   recset.getInt(1);
                    }
					 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207="+m+"  and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
					recset=dao.search(sqlstr);
					if(recset.next()) {
                        u02_4 +=   recset.getInt(1);
                    }
						}else{
							 sqlstr = " select count(*)  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0207!=1  and escope=1 and u0209<>'2' and u0209<>'3' and u0209<>'5' and u0209<>'6'  ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_1 +=  recset.getInt(1);
                                }
								 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207!=1   and escope=2 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_2 +=   recset.getInt(1);
                                }
								 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207!=1   and escope=3 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_3 +=   recset.getInt(1);
                                }
								 sqlstr = " select count(*)aa  from u02 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0207!=1   and escope=4 and u0209<>'2' and u0209<>'3'  and u0209<>'5' and u0209<>'6' ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_4 +=   recset.getInt(1);
                                }
						}
					}else{
						
							 sqlstr = " select u0405  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  ) and u0401="+m+"     ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_1 +=  recset.getInt(1);
                                }
								 sqlstr = " select u0407  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401="+m+"   ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_2 +=   recset.getInt(1);
                                }
								 sqlstr = " select u0409  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401="+m+"  ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_3 +=   recset.getInt(1);
                                }
								 sqlstr = " select u0411  from u04 where id="+up_id+" and unitcode in(select unitcode from tt_organization where unitcode like '"+unitid+"%'  )  and u0401="+m+"   ";
								recset=dao.search(sqlstr);
								if(recset.next()) {
                                    u02_4 +=   recset.getInt(1);
                                }
						
					}
				}
			}
			LazyDynaBean abean2 =new LazyDynaBean();
			
			abean2.set("current","0");
			abean2.set("U0401","1");
			abean2.set("theyear",theyear);
		
			abean2.set("U0405",""+u02_1);
			abean2.set("U0407",""+u02_2);
			abean2.set("U0409",""+u02_3);
			abean2.set("U0411",""+u02_4);
			if(m==1){
				abean2.set("person_type",AdminCode.getCodeName("62","1")+"\r\n(如单位撤并则显示合并数)");
				bean1=abean2;
			}else{
					abean2.set("person_type","2007年4月1日至"+_temp+"新增"+"\r\n(如单位撤并则显示合并数)");
					bean3=abean2;
				}
			list.add(abean2);
			
			m=m+3;
				}
			}
			StringBuffer sql2=new StringBuffer("select U04.*,tt_cycle.name,tt_cycle.theyear,tt_cycle.bos_date from U04,tt_cycle where U04.id=tt_cycle.id and  ");
			sql2.append(" unitcode='"+unitcode+"'and tt_cycle.kmethod=1  and tt_cycle.id="+vo.getInt("id")+" and ( U0401='1' or U0401='4' ) order by U0401,tt_cycle.theyear,tt_cycle.bos_date");
			recset=dao.search(sql2.toString());
			boolean isValue_1=false;
			boolean isValue_4=false;
			LazyDynaBean abean =null;
			DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
			ArrayList tempList=new ArrayList();
			
					
			HashMap preDate_map=getLastDateMap();
			int num=3;
			while(recset.next())
			{
				
				abean=new LazyDynaBean();
				String aid=recset.getString("id");
				if(aid.equals(id))
				{
					abean.set("current","1");
					if("1".equals(recset.getString("U0401"))) {
                        isValue_1=true;
                    } else if("4".equals(recset.getString("U0401"))) {
                        isValue_4=true;
                    }
				}
				else {
                    abean.set("current","0");
                }
			
				
				abean.set("U0401",recset.getString("U0401"));
				abean.set("unitcode",unitcode);
				
				for(int i=0;i<dataHeadList.size();i++)
				{
					String temp=(String)dataHeadList.get(i);
					if("theyear".equals(temp)) {
                        abean.set("theyear",recset.getString("theyear"));
                    } else if("person_type".equals(temp))
					{
						String temp_str="";
						if("4".equals(recset.getString("U0401"))){
						  temp_str="2007年4月1日至VALDATE新增";
						
							String _temp=mf.format(recset.getDate("bos_date"));
							
								temp_str=temp_str.replaceAll("VALDATE",_temp);
							
						
						}else{
							temp_str=AdminCode.getCodeName("62",recset.getString("U0401"));
						}
						abean.set("person_type",temp_str);
					//	abean.set("person_type",AdminCode.getCodeName("62",recset.getString("U0401")));
					}
					else
					{
						if(recset.getString(temp)==null) {
                            abean.set(temp,"");
                        } else
						{
							abean.set(temp,String.valueOf(recset.getInt(temp)));
						}
					}
				}
				if(num==3) {
                    bean2=abean;
                } else {
                    bean4=abean;
                }
				num++;
				tempList.add(abean);
			}
			
			if(!isValue_1||!isValue_4)
			{
				if(tempList.size()==0)
				{
					bean2=getBean4(1,dataHeadList,vo);
					bean4=getBean4(4,dataHeadList,vo);
					list.add(getBean4(1,dataHeadList,vo));
					list.add(getBean4(4,dataHeadList,vo));
				}
				else
				{
					boolean flag=true;
					for(int i=0;i<tempList.size();i++)
					{
						abean=(LazyDynaBean)tempList.get(i);
						String U0401=(String)abean.get("U0401");
						if("4".equals(U0401)&&flag)
						{
							bean2=getBean4(1,dataHeadList,vo);
							list.add(getBean4(1,dataHeadList,vo));
							flag=false;
						}
						bean2=abean;
						list.add(abean);
					}
					bean4 = abean;
					list.add(getBean4(4,dataHeadList,vo));
				}
			}
			else {
                list.addAll(tempList);
            }
			if(bean1.getMap().size()>0)//xiegh add 20170718
            {
                list2.add(bean1);
            }
			list2.add(bean2);
			if(bean3.getMap().size()>0) {
                list2.add(bean3);
            }
			list2.add(bean4);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
				try
				 {
					 if(recset!=null) {
                         recset.close();
                     }
					
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
		}
		
		return list2;
	}
	/**
	 * 取得表4人员数据
	 * @param id
	 * @param unitcode
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getU04ExcelDataList(String id,String unitcode,ArrayList dataHeadList,String flag2)
	{
		ArrayList list=new ArrayList();
		ArrayList list2=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		RowSet recset2=null;
		LazyDynaBean bean1 =new LazyDynaBean();
		LazyDynaBean bean2 =new LazyDynaBean();
		LazyDynaBean bean3 =new LazyDynaBean();
		LazyDynaBean bean4 =new LazyDynaBean();
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			String date_str=DateUtils.format(vo.getDate("bos_date"), "yyyy-MM-dd");
			vo = dao.findByPrimaryKey(vo);
			
			SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");	
			
			if(flag2!=null&& "1".equals(flag2)){
			String sqls = " select unitcode from tt_organization where unitcode like '"+unitcode+"%' "+this.throwunit(id, "")+" ";	
			recset2=dao.search(sqls.toString());
			while(recset2.next()){
				unitcode = recset2.getString("unitcode");
			
			StringBuffer sql2=new StringBuffer("select U04.*,tt_cycle.name,tt_cycle.theyear,tt_cycle.bos_date from U04,tt_cycle where U04.id=tt_cycle.id and  ");
			sql2.append(" unitcode='"+unitcode+"'and tt_cycle.kmethod=1  and tt_cycle.id="+vo.getInt("id")+" and ( U0401='1' or U0401='4' ) order by U0401,tt_cycle.theyear,tt_cycle.bos_date");
			recset=dao.search(sql2.toString());
			boolean isValue_1=false;
			boolean isValue_4=false;
			LazyDynaBean abean =null;
			DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
			ArrayList tempList=new ArrayList();
			
					
			HashMap preDate_map=getLastDateMap();
			int num=3;
			while(recset.next())
			{
				
				abean=new LazyDynaBean();
				String aid=recset.getString("id");
				if(aid.equals(id))
				{
					abean.set("current","1");
					if("1".equals(recset.getString("U0401"))) {
                        isValue_1=true;
                    } else if("4".equals(recset.getString("U0401"))) {
                        isValue_4=true;
                    }
				}
				else {
                    abean.set("current","0");
                }
			
				
				abean.set("U0401",recset.getString("U0401"));
				abean.set("unitcode",unitcode);
				
				for(int i=0;i<dataHeadList.size();i++)
				{
					String temp=(String)dataHeadList.get(i);
					if("theyear".equals(temp)) {
                        abean.set("theyear",recset.getString("theyear"));
                    } else if("person_type".equals(temp))
					{
						String temp_str="";
						if("4".equals(recset.getString("U0401"))){
						  temp_str="2007年4月1日至VALDATE新增";
						
							String _temp=mf.format(recset.getDate("bos_date"));
							
								temp_str=temp_str.replaceAll("VALDATE",_temp);
							
						
						}else{
							temp_str=AdminCode.getCodeName("62",recset.getString("U0401"));
						}
						abean.set("person_type",temp_str);
					//	abean.set("person_type",AdminCode.getCodeName("62",recset.getString("U0401")));
					}
					else
					{
						if(recset.getString(temp)==null) {
                            abean.set(temp,"");
                        } else
						{
							abean.set(temp,String.valueOf(recset.getInt(temp)));
						}
					}
				}
				if(num==3) {
                    bean2=abean;
                } else {
                    bean4=abean;
                }
				num++;
				tempList.add(abean);
			}
			
			if(!isValue_1||!isValue_4)
			{
				if(tempList.size()==0)
				{
					bean2=getBean4(1,dataHeadList,vo);
					bean4=getBean4(4,dataHeadList,vo);
					list.add(getBean4(1,dataHeadList,vo));
					list.add(getBean4(4,dataHeadList,vo));
				}
				else
				{
					boolean flag=true;
					for(int i=0;i<tempList.size();i++)
					{
						abean=(LazyDynaBean)tempList.get(i);
						String U0401=(String)abean.get("U0401");
						if("4".equals(U0401)&&flag)
						{
							bean2=getBean4(1,dataHeadList,vo);
							list.add(getBean4(1,dataHeadList,vo));
							flag=false;
						}
						bean2=abean;
						list.add(abean);
					}
					bean4 = abean;
					list.add(getBean4(4,dataHeadList,vo));
				}
			}
			else {
                list.addAll(tempList);
            }
	//		list2.add(bean1);
			list2.add(bean2);
	//		list2.add(bean3);
			list2.add(bean4);
			}
			}else{

				StringBuffer sql2=new StringBuffer("select U04.*,tt_cycle.name,tt_cycle.theyear,tt_cycle.bos_date from U04,tt_cycle where U04.id=tt_cycle.id and  ");
				sql2.append(" unitcode='"+unitcode+"'and tt_cycle.kmethod=1  and tt_cycle.id="+vo.getInt("id")+" and ( U0401='1' or U0401='4' ) order by U0401,tt_cycle.theyear,tt_cycle.bos_date");
				recset=dao.search(sql2.toString());
				boolean isValue_1=false;
				boolean isValue_4=false;
				LazyDynaBean abean =null;
				DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
				ArrayList tempList=new ArrayList();
				
						
				HashMap preDate_map=getLastDateMap();
				int num=3;
				while(recset.next())
				{
					
					abean=new LazyDynaBean();
					String aid=recset.getString("id");
					if(aid.equals(id))
					{
						abean.set("current","1");
						if("1".equals(recset.getString("U0401"))) {
                            isValue_1=true;
                        } else if("4".equals(recset.getString("U0401"))) {
                            isValue_4=true;
                        }
					}
					else {
                        abean.set("current","0");
                    }
				
					
					abean.set("U0401",recset.getString("U0401"));
					abean.set("unitcode",unitcode);
					
					for(int i=0;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						if("theyear".equals(temp)) {
                            abean.set("theyear",recset.getString("theyear"));
                        } else if("person_type".equals(temp))
						{
							String temp_str="";
							if("4".equals(recset.getString("U0401"))){
							  temp_str="2007年4月1日至VALDATE新增";
							
								String _temp=mf.format(recset.getDate("bos_date"));
								
									temp_str=temp_str.replaceAll("VALDATE",_temp);
								
							
							}else{
								temp_str=AdminCode.getCodeName("62",recset.getString("U0401"));
							}
							abean.set("person_type",temp_str);
						//	abean.set("person_type",AdminCode.getCodeName("62",recset.getString("U0401")));
						}
						else
						{
							if(recset.getString(temp)==null) {
                                abean.set(temp,"");
                            } else
							{
								abean.set(temp,String.valueOf(recset.getInt(temp)));
							}
						}
					}
					if(num==3) {
                        bean2=abean;
                    } else {
                        bean4=abean;
                    }
					num++;
					tempList.add(abean);
				}
				
				if(!isValue_1||!isValue_4)
				{
					if(tempList.size()==0)
					{
						bean2=getBean4(1,dataHeadList,vo);
						bean4=getBean4(4,dataHeadList,vo);
						list.add(getBean4(1,dataHeadList,vo));
						list.add(getBean4(4,dataHeadList,vo));
					}
					else
					{
						boolean flag=true;
						for(int i=0;i<tempList.size();i++)
						{
							abean=(LazyDynaBean)tempList.get(i);
							String U0401=(String)abean.get("U0401");
							if("4".equals(U0401)&&flag)
							{
								bean2=getBean4(1,dataHeadList,vo);
								list.add(getBean4(1,dataHeadList,vo));
								flag=false;
							}
							bean2=abean;
							list.add(abean);
						}
						bean4 = abean;
						list.add(getBean4(4,dataHeadList,vo));
					}
				}
				else {
                    list.addAll(tempList);
                }
		//		list2.add(bean1);
				list2.add(bean2);
		//		list2.add(bean3);
				list2.add(bean4);
				
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
					 if(recset!=null) {
                         recset.close();
                     }
					 if(recset2!=null) {
                         recset2.close();
                     }
					
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
		}
		
		return list2;
	}
	public LazyDynaBean getBean4(int i,ArrayList dataHeadList,RecordVo vo)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("current","1");
		abean.set("U0401",String.valueOf(i));
		HashMap preDate_map=getLastDateMap();
		SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");
		String date_str=mf.format(vo.getDate("bos_date"));
		for(int j=0;j<dataHeadList.size();j++)
		{
			String temp=(String)dataHeadList.get(j);
			if("theyear".equals(temp)) {
                abean.set("theyear",vo.getString("theyear"));
            } else if("person_type".equals(temp)){
				
					String  temp_str="2007年4月1日至VALDATE新增";
					temp_str= temp_str.replaceAll("VALDATE",date_str);
					if(i==4) {
                        abean.set("person_type",temp_str);
                    } else {
                        abean.set("person_type",AdminCode.getCodeName("62",String.valueOf(i)));
                    }
			}
			else
			{
					abean.set(temp,"");
			}
		}
		return abean;
	}
	
	
	
	
	/**
	 * 取得表3财务信息数据
	 * @param id
	 * @param unitcode
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getU01WarnList(String id,String unitcode,ArrayList dataHeadList)
	{
		ArrayList listw = new ArrayList();
		
			
			try
			{
				
				ContentDAO dao=new ContentDAO(this.conn);
				
				
				
					RecordVo vo=new RecordVo("u01");
					vo.setInt("id",Integer.parseInt(id));
					vo.setString("unitcode",unitcode);
					vo=dao.findByPrimaryKey(vo);
				TTorganization ttOrganization=new TTorganization(this.conn);
				LazyDynaBean bean = new LazyDynaBean(); 
				bean.set("unitcode", unitcode);
				bean.set("t3_desc", vo.getString("t3_desc"));
				bean.set("u0101", vo.getString("u0101"));
				bean.set("u0103", vo.getString("u0103"));
				bean.set("unitname", ttOrganization.getSelfUnit2(unitcode).getString("unitname"));
				listw.add(bean);
			
				ArrayList list =ttOrganization.getAllSubUnit(unitcode);
				for(int i=0;i<list.size();i++){
					String str =(String)list.get(i);
					if(str.indexOf("§")!=-1){
						LazyDynaBean	bean2=new LazyDynaBean(); 
						unitcode = str.substring(0,str.indexOf("§"));
						RecordVo vo2 = new RecordVo("u01");
						vo2.setInt("id", Integer.parseInt(id));
						vo2.setString("unitcode", unitcode);
						try{
						vo2=dao.findByPrimaryKey(vo2);
						}catch(Exception e){
							continue;
						}
						bean2.set("unitcode", unitcode);
						bean2.set("t3_desc", vo2.getString("t3_desc"));
						bean2.set("u0101", vo2.getString("u0101"));
						bean2.set("u0103", vo2.getString("u0103"));
						bean2.set("unitname", str.substring(str.indexOf("§")+1));
						listw.add(bean2);
					}
				}
//				listwarn.addAll(listw);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		
		return listw;
	}

	/**
	 * 取得表3财务信息数据
	 * @param id
	 * @param unitcode
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getU03DataList(String id,String unitcode,ArrayList dataHeadList,String flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			int kmethod=vo.getInt("kmethod");
			
			 String ext_sql =throwunit(id,""); 
			 String sql2 = "select unitcode from tt_organization  where 1=1 "+ext_sql;
			
			boolean isValue=false;
			String strsql="";
			if(flag!=null&& "1".equals(flag)){
				strsql="select * from tt_organization where unitcode  like '"+unitcode+"%' and unitcode in ("+sql2+" ) and unitcode not in (select unitcode from u03 where u0301=1 and unitcode  like '"+unitcode+"%' and id="+id+")";
				recset=dao.search(strsql);
				while(recset.next())
				{
					RecordVo vo_1=new RecordVo("u03");
					vo_1.setInt("id",Integer.parseInt(id));
					vo_1.setString("unitcode",recset.getString("unitcode"));
					vo_1.setString("u0301","1");
					vo_1.setString("u0303",vo.getString("theyear"));
					vo_1.setInt("editflag",1);
					dao.addValueObject(vo_1);
					
					RecordVo vo_2=new RecordVo("u03");
					vo_2.setInt("id",Integer.parseInt(id));
					vo_2.setString("unitcode",recset.getString("unitcode"));
					vo_2.setString("u0301","2");
					vo_2.setString("u0303",vo.getString("theyear"));
					vo_2.setInt("editflag",1);
					dao.addValueObject(vo_2);
					
				}
				String date_str=DateUtils.format(vo.getDate("bos_date"), "yyyy-MM-dd");
				
				StringBuffer sql=new StringBuffer("select U03.*,tt_cycle.name,tt_cycle.theyear,tt_cycle.bos_date,tt_cycle.kmethod from U03,tt_cycle where U03.id=tt_cycle.id and  ");
				sql.append(" unitcode  like '"+unitcode+"%' and unitcode in ("+sql2+" )   and tt_cycle. bos_date<="+Sql_switcher.dateValue(date_str)+"  and ( U0301='1' or U0301='2' ) order by  unitcode,U0301,tt_cycle.theyear desc,tt_cycle.bos_date desc");
				recset=dao.search(sql.toString());
				 
				LazyDynaBean abean =null;
				DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
				ArrayList tempList=new ArrayList();
				SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");			
				
				HashMap preDate_map=getLastDateMap();
				while(recset.next())
				{
					abean=new LazyDynaBean();
					String aid=recset.getString("id");
					if(aid.equals(id))
					{
						abean.set("current","1");
					}
					else {
                        abean.set("current","0");
                    }
					abean.set("U0301",recset.getString("U0301"));
					abean.set("unitcode",recset.getString("unitcode"));
					for(int i=0;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						if("theyear".equals(temp)){
							if("0".equals(recset.getString("kmethod"))){
							abean.set("theyear",recset.getString("theyear")+"全年");
							}else{
							abean.set("theyear",recset.getString("theyear")+"半年");	
							}
						}
						else if("cycle_name".equals(temp)) {
                            abean.set("cycle_name",recset.getString("name"));
                        } else if("person_type".equals(temp))
						{
							String temp_str=AdminCode.getCodeName("62",recset.getString("U0301"));
							if(temp_str.indexOf("LASTDATE")!=-1)
							{
								String _temp=mf.format(recset.getDate("bos_date"));
								if(preDate_map.get(_temp)!=null)
								{
									temp_str=temp_str.replaceAll("LASTDATE",(String)preDate_map.get(_temp));
								}
							}
							abean.set("person_type",temp_str);
						}
						else
						{
							if(recset.getString(temp)==null) {
                                abean.set(temp,"");
                            } else
							{
								abean.set(temp,myformat1.format(recset.getDouble(temp)));
							}
						}
					}
					tempList.add(abean);
				}
				
				list.addAll(tempList);
			}else{
				strsql="select * from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id;
				recset=dao.search(strsql);
				if(recset.next()) {
                    isValue=true;
                }
				if(!isValue)
				{
					RecordVo vo_1=new RecordVo("u03");
					vo_1.setInt("id",Integer.parseInt(id));
					vo_1.setString("unitcode",unitcode);
					vo_1.setString("u0301","1");
					vo_1.setString("u0303",vo.getString("theyear"));
					vo_1.setInt("editflag",1);
					dao.addValueObject(vo_1);
					
					RecordVo vo_2=new RecordVo("u03");
					vo_2.setInt("id",Integer.parseInt(id));
					vo_2.setString("unitcode",unitcode);
					vo_2.setString("u0301","2");
					vo_2.setString("u0303",vo.getString("theyear"));
					vo_2.setInt("editflag",1);
					dao.addValueObject(vo_2);
					
				}
				StringBuffer sql=new StringBuffer("select U03.*,tt_cycle.name,tt_cycle.theyear,tt_cycle.bos_date,tt_cycle.kmethod from U03,tt_cycle where U03.id=tt_cycle.id and  ");
				sql.append(" unitcode='"+unitcode+"'   and tt_cycle.bos_date<="+Sql_switcher.dateValue(DateUtils.format(vo.getDate("bos_date"), "yyyy-MM-dd"))+"  and ( U0301='1' or U0301='2' ) order by U0301,tt_cycle.theyear desc,tt_cycle.bos_date desc");
				recset=dao.search(sql.toString());
				 
				LazyDynaBean abean =null;
				DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
				ArrayList tempList=new ArrayList();
				SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");			
				
				HashMap preDate_map=getLastDateMap();
				while(recset.next())
				{
					abean=new LazyDynaBean();
					String aid=recset.getString("id");
					if(aid.equals(id))
					{
						abean.set("current","1");
					}
					else {
                        abean.set("current","0");
                    }
					abean.set("U0301",recset.getString("U0301"));
					abean.set("unitcode",unitcode);
					for(int i=0;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						if("theyear".equals(temp)){
							if("0".equals(recset.getString("kmethod"))){
							abean.set("theyear",recset.getString("theyear")+"全年");
							}else{
							abean.set("theyear",recset.getString("theyear")+"半年");	
							}
						}
						else if("cycle_name".equals(temp)) {
                            abean.set("cycle_name",recset.getString("name"));
                        } else if("person_type".equals(temp))
						{
							String temp_str=AdminCode.getCodeName("62",recset.getString("U0301"));
							if(temp_str.indexOf("LASTDATE")!=-1)
							{
								String _temp=mf.format(recset.getDate("bos_date"));
								if(preDate_map.get(_temp)!=null)
								{
									temp_str=temp_str.replaceAll("LASTDATE",(String)preDate_map.get(_temp));
								}
							}
							abean.set("person_type",temp_str);
						}
						else
						{
							if(recset.getString(temp)==null) {
                                abean.set(temp,"");
                            } else
							{
								abean.set(temp,myformat1.format(recset.getDouble(temp)));
							}
						}
					}
					tempList.add(abean);
				}
				
				list.addAll(tempList);
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
					 if(recset!=null) {
                         recset.close();
                     }
			 
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
		}
		
		return list;
	}
	public HashMap getLastDateMap()
	{
		SimpleDateFormat mf=new SimpleDateFormat("yyyy年MM月dd日");
		HashMap map=new HashMap();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from tt_cycle order by bos_date ");
			String pre_date="";
			int i=0;
			while(rowSet.next())
			{
				i++;
				if(i>1)
				{
					map.put(mf.format(rowSet.getDate("bos_date")), pre_date);
				}
				pre_date=mf.format(rowSet.getDate("bos_date"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return map;
	}
	
	
	public LazyDynaBean getBean(int i,ArrayList dataHeadList,RecordVo vo)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("current","1");
		abean.set("U0301",String.valueOf(i));
		for(int j=0;j<dataHeadList.size();j++)
		{
			String temp=(String)dataHeadList.get(j);
			if("theyear".equals(temp)) {
                abean.set("theyear",vo.getString("theyear"));
            } else if("cycle_name".equals(temp)) {
                abean.set("cycle_name",vo.getString("name"));
            } else if("person_type".equals(temp)) {
                abean.set("person_type",AdminCode.getCodeName("62",String.valueOf(i)));
            } else
			{
					abean.set(temp,"");
			}
		}
		return abean;
	}
	

	/**
	 * 取得表5的数据
	 * @param dataHeadList
	 * @param cycle_id
	 * @param unitcode
	 * @return
	 */
	public ArrayList getDataList_U05(ArrayList dataHeadList,String cycle_id,String unitcode,String flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(cycle_id));
			vo=dao.findByPrimaryKey(vo);
			String current_year=vo.getString("theyear");
			int pre_id=hasDataByPre_u05(unitcode,current_year);
//			 String ext_sql =throwunit(cycle_id,""); 
//			 String sql = "select unitcode from tt_organization  where 1=1 "+ext_sql;
			if(flag!=null&& "1".equals(flag)){
			 rowSet=dao.search("select * from U05 where unitcode like '"+unitcode+"%' and ( id="+cycle_id+" or id="+pre_id+" ) order by unitcode, id desc ");
			DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
			String preunitcode = unitcode;
			String curunitcode ="";
			while(rowSet.next())
			{
		
				curunitcode = rowSet.getString("unitcode");
				if(curunitcode!=null&&curunitcode.trim().equalsIgnoreCase(preunitcode)){
					LazyDynaBean abean=new LazyDynaBean();
					
					int id=rowSet.getInt("id");
					if(id==pre_id) {
                        abean.set("item_name","前次完整评估时点");
                    }
					if(id==Integer.parseInt(cycle_id)) {
                        abean.set("item_name","本次完整评估时点");
                    }
					abean.set("unitcode",rowSet.getString("unitcode"));
					LazyDynaBean _bean=null;
					
					for(int i=1;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						_bean=new LazyDynaBean();
						_bean.set("itemid",temp);
						String value="";
						if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0)
						{
							value=myformat1.format(Double.parseDouble(PubFunc.round(rowSet.getString(temp),3)));
						}
						_bean.set("value",value);
						_bean.set("over","0");
						_bean.set("desc","");
						abean.set(temp, _bean);	
					}
					list.add(abean);	
				}else{
					list.addAll(getCompareDataList_u05(cycle_id,preunitcode,current_year,dataHeadList));
					preunitcode =curunitcode;

					LazyDynaBean abean=new LazyDynaBean();
					
					int id=rowSet.getInt("id");
					if(id==pre_id) {
                        abean.set("item_name","前次完整评估时点");
                    }
					if(id==Integer.parseInt(cycle_id)) {
                        abean.set("item_name","本次完整评估时点");
                    }
					abean.set("unitcode",rowSet.getString("unitcode"));
					LazyDynaBean _bean=null;
					
					for(int i=1;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						_bean=new LazyDynaBean();
						_bean.set("itemid",temp);
						String value="";
						if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0)
						{
							value=myformat1.format(Double.parseDouble(PubFunc.round(rowSet.getString(temp),3)));
						}
						_bean.set("value",value);
						_bean.set("over","0");
						_bean.set("desc","");
						abean.set(temp, _bean);	
					}
					list.add(abean);	
				
				}
			}
			list.addAll(getCompareDataList_u05(cycle_id,preunitcode,current_year,dataHeadList));
		}else{

			 rowSet=dao.search("select * from U05 where unitcode='"+unitcode+"' and ( id="+cycle_id+" or id="+pre_id+" ) order by id desc ");
			DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				
				int id=rowSet.getInt("id");
				if(id==pre_id) {
                    abean.set("item_name","前次完整评估时点");
                }
				if(id==Integer.parseInt(cycle_id)) {
                    abean.set("item_name","本次完整评估时点");
                }
				abean.set("unitcode",unitcode);
				LazyDynaBean _bean=null;
				for(int i=1;i<dataHeadList.size();i++)
				{
					String temp=(String)dataHeadList.get(i);
					_bean=new LazyDynaBean();
					_bean.set("itemid",temp);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0)
					{
						value=myformat1.format(Double.parseDouble(PubFunc.round(rowSet.getString(temp),3)));
					}
					_bean.set("value",value);
					_bean.set("over","0");
					_bean.set("desc","");
					abean.set(temp, _bean);	
				}
				list.add(abean);
			}
			list.addAll(getCompareDataList_u05(cycle_id,unitcode,current_year,dataHeadList));
		
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
	}
	
	
	public ArrayList getDataHeadList_U05()
	{
		ArrayList list=new ArrayList();
		list.add("item_name");

		list.add("U0505");
		list.add("U0507");
		list.add("U0509");
		list.add("U0511");
		list.add("U0513");
		list.add("U0515");
		list.add("U0517");
		list.add("U0519");
		list.add("U0521");
		list.add("U0523");
		list.add("U0525");
		return list;
	}
	
	
	
	
	/**
	 * 取得表5的比较信息
	 * @param id
	 * @param unitcode
	 * @return
	 */
	public ArrayList getCompareDataList_u05(String id,String unitcode,String theyear,ArrayList dataHeadList)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
		try
		{
			
			String medic_differ_1="";
			String medic_differpercent_1="";
			String medic_differ_2="";
			String medic_differpercent_2="";
			String medic_differ_3="";
			String medic_differpercent_3="";
			String medic_differ_4="";
			String medic_differpercent_4="";
			String other_differ_1="";
			String other_differpercent_1="";
			String other_differ_2="";
			String other_differpercent_2="";
			String other_differ_3="";
			String other_differpercent_3="";
			
			
			
			
			if(paramMap.get("u05")!=null)
			{
				HashMap map=(HashMap)paramMap.get("u05");
				if(map.get("medic_differ_1")!=null&&((String)map.get("medic_differ_1")).trim().length()>0) {
                    medic_differ_1=(String)map.get("medic_differ_1");
                }
				if(map.get("medic_differpercent_1")!=null&&((String)map.get("medic_differpercent_1")).trim().length()>0) {
                    medic_differpercent_1=(String)map.get("medic_differpercent_1");
                }
				if(map.get("medic_differ_2")!=null&&((String)map.get("medic_differ_2")).trim().length()>0) {
                    medic_differ_2=(String)map.get("medic_differ_2");
                }
				if(map.get("medic_differpercent_2")!=null&&((String)map.get("medic_differpercent_2")).trim().length()>0) {
                    medic_differpercent_2=(String)map.get("medic_differpercent_2");
                }
				if(map.get("medic_differ_3")!=null&&((String)map.get("medic_differ_3")).trim().length()>0) {
                    medic_differ_3=(String)map.get("medic_differ_3");
                }
				if(map.get("medic_differpercent_3")!=null&&((String)map.get("medic_differpercent_3")).trim().length()>0) {
                    medic_differpercent_3=(String)map.get("medic_differpercent_3");
                }
				
				
				if(map.get("other_differ_1")!=null&&((String)map.get("other_differ_1")).trim().length()>0) {
                    other_differ_1=(String)map.get("other_differ_1");
                }
				if(map.get("other_differpercent_1")!=null&&((String)map.get("other_differpercent_1")).trim().length()>0) {
                    other_differpercent_1=(String)map.get("other_differpercent_1");
                }
				if(map.get("other_differ_2")!=null&&((String)map.get("other_differ_2")).trim().length()>0) {
                    other_differ_2=(String)map.get("other_differ_2");
                }
				if(map.get("other_differpercent_2")!=null&&((String)map.get("other_differpercent_2")).trim().length()>0) {
                    other_differpercent_2=(String)map.get("other_differpercent_2");
                }
				if(map.get("other_differ_3")!=null&&((String)map.get("other_differ_3")).trim().length()>0) {
                    other_differ_3=(String)map.get("other_differ_3");
                }
				if(map.get("other_differpercent_3")!=null&&((String)map.get("other_differpercent_3")).trim().length()>0) {
                    other_differpercent_3=(String)map.get("other_differpercent_3");
                }
				
				if(map.get("medic_differ_4")!=null&&((String)map.get("medic_differ_4")).trim().length()>0) {
                    medic_differ_4=(String)map.get("medic_differ_4");
                }
				if(map.get("medic_differpercent_4")!=null&&((String)map.get("medic_differpercent_4")).trim().length()>0) {
                    medic_differpercent_4=(String)map.get("medic_differpercent_4");
                }
			}
			
			
			
			
			int pre_id=hasDataByPre_u05(unitcode,theyear);
			if(pre_id!=0)
			{
				String str="/U0505/U0511/U0517/U0523/";
				StringBuffer sql_select=new StringBuffer("");
				StringBuffer sql_select2=new StringBuffer("");
				StringBuffer sql_select_sub=new StringBuffer("");
				for(int i=1;i<dataHeadList.size();i++)
				{
					String temp=(String)dataHeadList.get(i);
					if(str.indexOf("/"+temp+"/")!=-1) {
                        continue;
                    }
					sql_select.append(","+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+" "+temp);
					sql_select2.append(",("+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+")/nullif(b."+temp+",0)*100 "+temp);	
					sql_select_sub.append(","+temp);
				}
				
				
				StringBuffer sql=new StringBuffer("select "+sql_select2.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u05 where  unitcode='"+unitcode+"' and id="+id+") a,");
				sql.append("( select "+sql_select_sub.substring(1)+"  from u05 where  unitcode='"+unitcode+"' and id="+pre_id+") b ");
				 rowSet=dao.search(sql.toString());
				if(rowSet.next())			
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("item_name","差异率(%)");
					abean.set("unitcode",unitcode);
					LazyDynaBean _bean=new LazyDynaBean();
					for(int i=1;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						_bean=new LazyDynaBean();
						if(str.indexOf("/"+temp+"/")!=-1)
						{
							_bean.set("itemid",temp);
							_bean.set("value","");
							_bean.set("over","0");
							_bean.set("desc","");
							abean.set(temp, _bean);
							continue;
						}
						
						String value="";
						if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                            value=myformat1.format(rowSet.getDouble(temp));
                        }
						_bean.set("itemid",temp);
						if(value.length()>0) {
                            _bean.set("value",PubFunc.round(value,2)+"%");
                        } else {
                            _bean.set("value",value);
                        }
						if(value.length()>0)
						{
							if("U0507".equalsIgnoreCase(temp)&&medic_differpercent_1.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differpercent_1)) {
                                _bean.set("over","1");
                            } else if("U0513".equalsIgnoreCase(temp)&&medic_differpercent_2.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differpercent_2)) {
                                _bean.set("over","1");
                            } else if("U0509".equalsIgnoreCase(temp)&&other_differpercent_1.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differpercent_1)) {
                                _bean.set("over","1");
                            } else if("U0515".equalsIgnoreCase(temp)&&other_differpercent_2.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differpercent_2)) {
                                _bean.set("over","1");
                            } else if("U0519".equalsIgnoreCase(temp)&&medic_differpercent_3.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differpercent_3)) {
                                _bean.set("over","1");
                            } else if("U0521".equalsIgnoreCase(temp)&&other_differpercent_3.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differpercent_3)) {
                                _bean.set("over","1");
                            } else if("U0525".equalsIgnoreCase(temp)&&medic_differpercent_4.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differpercent_4)) {
                                _bean.set("over","1");
                            } else {
                                _bean.set("over","0");
                            }
						}
						else {
                            _bean.set("over","0");
                        }
						_bean.set("desc","");
						abean.set(temp, _bean);
					}
					list.add(abean);
				}
				
				
				sql=new StringBuffer("select "+sql_select.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u05 where  unitcode='"+unitcode+"' and id="+id+") a,");
				sql.append("( select "+sql_select_sub.substring(1)+"  from u05 where  unitcode='"+unitcode+"' and id="+pre_id+") b ");
				rowSet=dao.search(sql.toString());
				if(rowSet.next())			
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("item_name","差异金额");
					abean.set("unitcode",unitcode);
					LazyDynaBean _bean=new LazyDynaBean();
					for(int i=1;i<dataHeadList.size();i++)
					{
						String temp=(String)dataHeadList.get(i);
						_bean=new LazyDynaBean();
						if(str.indexOf("/"+temp+"/")!=-1)
						{
							_bean.set("itemid",temp);
							_bean.set("value","");
							_bean.set("over","0");
							_bean.set("desc","");
							abean.set(temp, _bean);
							continue;
						}
						String value="";
						if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                            value=myformat1.format(rowSet.getDouble(temp));
                        }
						_bean.set("itemid",temp);
						_bean.set("value",value);
						if(value.length()>0)
						{
							if("U0507".equalsIgnoreCase(temp)&&medic_differ_1.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differ_1)) {
                                _bean.set("over","1");
                            } else if("U0513".equalsIgnoreCase(temp)&&medic_differ_2.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differ_2)) {
                                _bean.set("over","1");
                            } else if("U0509".equalsIgnoreCase(temp)&&other_differ_1.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differ_1)) {
                                _bean.set("over","1");
                            } else if("U0515".equalsIgnoreCase(temp)&&other_differ_2.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differ_2)) {
                                _bean.set("over","1");
                            } else if("U0519".equalsIgnoreCase(temp)&&medic_differ_3.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differ_3)) {
                                _bean.set("over","1");
                            } else if("U0521".equalsIgnoreCase(temp)&&other_differ_3.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(other_differ_3)) {
                                _bean.set("over","1");
                            } else if("U0525".equalsIgnoreCase(temp)&&medic_differ_4.trim().length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(medic_differ_4)) {
                                _bean.set("over","1");
                            } else {
                                _bean.set("over","0");
                            }
						}
						else {
                            _bean.set("over","0");
                        }
						_bean.set("desc","");
						abean.set(temp, _bean);
					}
					list.add(abean);
				}
				if(list.size()>0) {
                    dealWithPreCompareDataList_u05(list,dataHeadList);
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
	}
	
	
	

//	与u02统计支出差异金额大于100万且差异率大于20%  作处理
	public void dealWithPreCompareDataList_u05(ArrayList datalist,ArrayList dataHeadList)
	{
		LazyDynaBean abean0=(LazyDynaBean)datalist.get(0);
		LazyDynaBean abean1=(LazyDynaBean)datalist.get(1);
		
		try
		{
			String medic_differ_1="";
			String medic_differpercent_1="";
			String medic_differ_2="";
			String medic_differpercent_2="";
			String medic_differ_3="";
			String medic_differpercent_3="";
			String medic_differ_4="";
			String medic_differpercent_4="";
			String other_differ_1="";
			String other_differpercent_1="";
			String other_differ_2="";
			String other_differpercent_2="";
			String other_differ_3="";
			String other_differpercent_3="";
			
			if(paramMap.get("u05")!=null)
			{
				HashMap map=(HashMap)paramMap.get("u05");
				if(map.get("medic_differ_1")!=null&&((String)map.get("medic_differ_1")).trim().length()>0) {
                    medic_differ_1=(String)map.get("medic_differ_1");
                }
				if(map.get("medic_differpercent_1")!=null&&((String)map.get("medic_differpercent_1")).trim().length()>0) {
                    medic_differpercent_1=(String)map.get("medic_differpercent_1");
                }
				if(map.get("medic_differ_2")!=null&&((String)map.get("medic_differ_2")).trim().length()>0) {
                    medic_differ_2=(String)map.get("medic_differ_2");
                }
				if(map.get("medic_differpercent_2")!=null&&((String)map.get("medic_differpercent_2")).trim().length()>0) {
                    medic_differpercent_2=(String)map.get("medic_differpercent_2");
                }
				if(map.get("medic_differ_3")!=null&&((String)map.get("medic_differ_3")).trim().length()>0) {
                    medic_differ_3=(String)map.get("medic_differ_3");
                }
				if(map.get("medic_differpercent_3")!=null&&((String)map.get("medic_differpercent_3")).trim().length()>0) {
                    medic_differpercent_3=(String)map.get("medic_differpercent_3");
                }
				
				
				if(map.get("other_differ_1")!=null&&((String)map.get("other_differ_1")).trim().length()>0) {
                    other_differ_1=(String)map.get("other_differ_1");
                }
				if(map.get("other_differpercent_1")!=null&&((String)map.get("other_differpercent_1")).trim().length()>0) {
                    other_differpercent_1=(String)map.get("other_differpercent_1");
                }
				if(map.get("other_differ_2")!=null&&((String)map.get("other_differ_2")).trim().length()>0) {
                    other_differ_2=(String)map.get("other_differ_2");
                }
				if(map.get("other_differpercent_2")!=null&&((String)map.get("other_differpercent_2")).trim().length()>0) {
                    other_differpercent_2=(String)map.get("other_differpercent_2");
                }
				if(map.get("other_differ_3")!=null&&((String)map.get("other_differ_3")).trim().length()>0) {
                    other_differ_3=(String)map.get("other_differ_3");
                }
				if(map.get("other_differpercent_3")!=null&&((String)map.get("other_differpercent_3")).trim().length()>0) {
                    other_differpercent_3=(String)map.get("other_differpercent_3");
                }
				
				if(map.get("medic_differ_4")!=null&&((String)map.get("medic_differ_4")).trim().length()>0) {
                    medic_differ_4=(String)map.get("medic_differ_4");
                }
				if(map.get("medic_differpercent_4")!=null&&((String)map.get("medic_differpercent_4")).trim().length()>0) {
                    medic_differpercent_4=(String)map.get("medic_differpercent_4");
                }
			}
			
			
			
			
			
			
			ContentDAO dao=new ContentDAO(this.conn);
			String str="/U0505/U0511/U0517/U0523/";
			for(int i=1;i<dataHeadList.size();i++)
			{
				String temp=(String)dataHeadList.get(i);
				if(str.indexOf("/"+temp+"/")!=-1) {
                    continue;
                }
				LazyDynaBean _bean0=(LazyDynaBean)abean0.get(temp);
				LazyDynaBean _bean1=(LazyDynaBean)abean1.get(temp);
				
				if("1".equals((String)_bean0.get("over"))&& "1".equals((String)_bean1.get("over")))
				{
					_bean0.set("over","1");
					_bean1.set("over","1");
					
					DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
					
					if("U0507".equalsIgnoreCase(temp))
					{	 
						_bean0.set("desc","医疗福利水平差异率>"+medic_differpercent_1+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_1)/10000)+"万");
						_bean1.set("desc","医疗福利水平差异率>"+medic_differpercent_1+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_1)/10000)+"万");
					}
					else if("U0513".equalsIgnoreCase(temp))
					{	 
						_bean0.set("desc","医疗福利水平差异率>"+medic_differpercent_2+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_2)/10000)+"万");
						_bean1.set("desc","医疗福利水平差异率>"+medic_differpercent_2+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_2)/10000)+"万");
					}
					else if("U0509".equalsIgnoreCase(temp))
					{
						_bean0.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_1+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_1)/10000)+"万");
						_bean1.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_1+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_1)/10000)+"万");
					}
					else if("U0515".equalsIgnoreCase(temp))
					{
						_bean0.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_2+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_2)/10000)+"万");
						_bean1.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_2+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_2)/10000)+"万");
					}
					else if("U0519".equalsIgnoreCase(temp))
					{
						_bean0.set("desc","医疗福利水平差异率>"+medic_differpercent_3+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_3)/10000)+"万");
						_bean1.set("desc","医疗福利水平差异率>"+medic_differpercent_3+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_3)/10000)+"万");
					}
					else if("U0521".equalsIgnoreCase(temp))
					{
						_bean0.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_3+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_3)/10000)+"万");
						_bean1.set("desc","除医疗福利外其他福利水平差异率>"+other_differpercent_3+"%且差异金额>"+myformat1.format(Double.parseDouble(other_differ_3)/10000)+"万");
					}
					else if("U0525".equalsIgnoreCase(temp))
					{
						_bean0.set("desc","医疗福利外其他福利水平差异率>"+medic_differpercent_4+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_4)/10000)+"万");
						_bean1.set("desc","医疗福利外其他福利水平差异率>"+medic_differpercent_4+"%且差异金额>"+myformat1.format(Double.parseDouble(medic_differ_4)/10000)+"万");
					}
					
				}
				else
				{
					_bean0.set("over","0");
					_bean1.set("over","0");
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得表3的比较信息
	 * @param id
	 * @param unitcode
	 * @param dataHeadList
	 * @param u03DataList
	 * @return
	 */
	public ArrayList getCompareDataList(String id,String unitcode,ArrayList dataHeadList)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RecordVo vo=new RecordVo("tt_cycle");
			vo.setInt("id",Integer.parseInt(id));
			vo=dao.findByPrimaryKey(vo);
			String current_year=vo.getString("theyear");
			int pre_cycle_id= hasDatabyPreyear(unitcode,vo);
			 
			if(pre_cycle_id!=0)  //有上年同期数据
			{
				list.addAll(getPreCompareDataList(pre_cycle_id,id,unitcode,"1",dataHeadList,current_year));
			}
			list.addAll(getU02CompareDataList(id,unitcode,"1",dataHeadList,current_year));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 取得上年同期比较数据
	 * @param pre_cycleid
	 * @param id
	 * @param unitcode
	 * @param U0301
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getPreCompareDataList(int pre_cycleid,String id,String unitcode,String U0301,ArrayList dataHeadList,String current_year)
	{
		ArrayList list=new ArrayList();
		DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			
			
			StringBuffer sql_select=new StringBuffer("");
			StringBuffer sql_select2=new StringBuffer("");
			StringBuffer sql_select_sub=new StringBuffer("");
			for(int i=3;i<dataHeadList.size();i++)
			{
				String temp=(String)dataHeadList.get(i);
				sql_select.append(","+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+" "+temp);
				sql_select2.append(",("+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+")/nullif(b."+temp+",0)*100 "+temp);
				sql_select_sub.append(","+temp);
			}
			
			
			
			
			StringBuffer sql=new StringBuffer("select "+sql_select.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id+") a,");
			sql.append("( select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+pre_cycleid+") b ");
			rowSet=dao.search(sql.toString());
			if(rowSet.next())			
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("name","本期支出与上年同期支出金额差异");
				abean.set("person_type",AdminCode.getCodeName("62","1"));

				LazyDynaBean _bean=new LazyDynaBean();
				for(int i=3;i<dataHeadList.size();i++)
				{
					_bean=new LazyDynaBean();
					String temp=(String)dataHeadList.get(i);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                        value=myformat1.format(rowSet.getDouble(temp));
                    }
					_bean.set("itemid",temp);
					_bean.set("value",value);
					
					if(paramMap.get("u03")!=null)
					{
						HashMap map=(HashMap)paramMap.get("u03");
						if(map.get("differ")!=null&&((String)map.get("differ")).trim().length()>0)
						{
							double balance=Double.parseDouble(((String)map.get("differ")).trim());
							if(value.length()>0&&Math.abs(Double.parseDouble(value))>balance)
							{
								_bean.set("over","1");
							}
							else {
                                _bean.set("over","0");
                            }
						}
						else {
                            _bean.set("over","0");
                        }
					}
					else {
                        _bean.set("over","0");
                    }
					
					_bean.set("desc","");
					abean.set(temp, _bean);
				}
				list.add(abean);
			}
			
			
			sql=new StringBuffer("select "+sql_select2.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id+") a,");
			sql.append("( select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+pre_cycleid+") b ");
			rowSet=dao.search(sql.toString());
			if(rowSet.next())			
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("name","本期支出与上年同期支出比例差异");
				abean.set("person_type",AdminCode.getCodeName("62","1"));

				LazyDynaBean _bean=new LazyDynaBean();
				for(int i=3;i<dataHeadList.size();i++)
				{
					_bean=new LazyDynaBean();
					String temp=(String)dataHeadList.get(i);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                        value=myformat1.format(rowSet.getDouble(temp));
                    }
					_bean.set("itemid",temp);
					if(value.length()>0) {
                        _bean.set("value",PubFunc.round(value,2)+"%");
                    } else {
                        _bean.set("value",value);
                    }
					
					if(paramMap.get("u03")!=null)
					{
						HashMap map=(HashMap)paramMap.get("u03");
						if(map.get("differpercent")!=null&&((String)map.get("differpercent")).trim().length()>0)
						{
							double balance_percent=Double.parseDouble(((String)map.get("differpercent")).trim());
							if(value.length()>0&&Math.abs(Double.parseDouble(value))>balance_percent)
							{
								_bean.set("over","1");
							}
							else {
                                _bean.set("over","0");
                            }
						}
						else {
                            _bean.set("over","0");
                        }
					}
					else {
                        _bean.set("over","0");
                    }
					_bean.set("desc","");
					abean.set(temp, _bean);
				}
				list.add(abean);
			}
			
			dealWithPreCompareDataList(list,dataHeadList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
	}
	
	//与历史财务支出差异金额大于100万且差异率大于20%  作处理
	public void dealWithPreCompareDataList(ArrayList datalist,ArrayList dataHeadList)
	{
		LazyDynaBean abean1=(LazyDynaBean)datalist.get(0);
		LazyDynaBean abean2=(LazyDynaBean)datalist.get(1);
		
		String balance="";
		String balance_percent="";
		if(paramMap.get("u03")!=null)
		{
			HashMap map=(HashMap)paramMap.get("u03");
			if(map.get("differpercent")!=null&&((String)map.get("differpercent")).trim().length()>0) {
                balance_percent=(String)map.get("differpercent");
            }
			if(map.get("differ")!=null&&((String)map.get("differ")).trim().length()>0) {
                balance=(String)map.get("differ");
            }
		}
		
		for(int i=3;i<dataHeadList.size();i++)
		{
			String temp=(String)dataHeadList.get(i);
			LazyDynaBean _bean1=(LazyDynaBean)abean1.get(temp);
			LazyDynaBean _bean2=(LazyDynaBean)abean2.get(temp);
			if("1".equals((String)_bean1.get("over"))&& "1".equals((String)_bean2.get("over")))
			{
				_bean1.set("over","1");
				_bean2.set("over","1");
				_bean1.set("desc","与历史财务支出差异金额大于"+Double.parseDouble(balance)/10000+"万且差异率大于"+balance_percent+"%");
				_bean2.set("desc","与历史财务支出差异金额大于"+Double.parseDouble(balance)/10000+"万且差异率大于"+balance_percent+"%");
			}
			else
			{
				_bean1.set("over","0");
				_bean2.set("over","0");
			}
		}
	}
	
	
//	与u02统计支出差异金额大于100万且差异率大于20%  作处理
	public void dealWithPreCompareDataList2(ArrayList datalist,ArrayList dataHeadList,String unitcode,String id)
	{
		LazyDynaBean abean0=(LazyDynaBean)datalist.get(0);
		LazyDynaBean abean1=(LazyDynaBean)datalist.get(1);
		LazyDynaBean abean2=(LazyDynaBean)datalist.get(2);
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet =dao.search("select *  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id);
			rowSet.next();
			
			String balance="";
			String balance_percent="";
			if(paramMap.get("u03")!=null)
			{
				HashMap map=(HashMap)paramMap.get("u03");
				if(map.get("differpercent")!=null&&((String)map.get("differpercent")).trim().length()>0) {
                    balance_percent=(String)map.get("differpercent");
                }
				if(map.get("differ")!=null&&((String)map.get("differ")).trim().length()>0) {
                    balance=(String)map.get("differ");
                }
			}
			
			
			for(int i=3;i<dataHeadList.size();i++)
			{
				String temp=(String)dataHeadList.get(i);
				LazyDynaBean _bean0=(LazyDynaBean)abean0.get(temp);
				LazyDynaBean _bean1=(LazyDynaBean)abean1.get(temp);
				LazyDynaBean _bean2=(LazyDynaBean)abean2.get(temp);
				
				if(((String)_bean0.get("value")).length()==0&&rowSet.getString(temp)!=null&&rowSet.getDouble(temp)>0)
				{
					_bean0.set("over","1");
					_bean1.set("over","1");
					_bean2.set("over","1");
					_bean0.set("desc","有相关的财务信息但是无对应的相关人员");
					_bean1.set("desc","有相关的财务信息但是无对应的相关人员");
					_bean2.set("desc","有相关的财务信息但是无对应的相关人员");
				}
				else if(((String)_bean0.get("value")).length()>0&&(rowSet.getString(temp)==null||rowSet.getDouble(temp)==0))
				{
					_bean0.set("over","1");
					_bean1.set("over","1");
					_bean2.set("over","1");
					_bean0.set("desc","表2有相关人员但是无该类人员对应的财务信息");
					_bean1.set("desc","表2有相关人员但是无该类人员对应的财务信息");
					_bean2.set("desc","表2有相关人员但是无该类人员对应的财务信息");
				}
				else if("1".equals((String)_bean1.get("over"))&& "1".equals((String)_bean2.get("over")))
				{
					_bean1.set("over","1");
					_bean2.set("over","1");
					_bean1.set("desc","与表2为“2007年3月31日前原有各类人员”的人员计算出的年支出相比，差异金额大于"+Double.parseDouble(balance)/10000+"万且差异率大于"+balance_percent+"%");
					_bean2.set("desc","与表2为“2007年3月31日前原有各类人员”的人员计算出的年支出相比，差异金额大于"+balance_percent+"%");
				}
				else
				{
					_bean1.set("over","0");
					_bean2.set("over","0");
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
	}
	
	
	/**
	 * 取得明细数据(U02)比较数据
	 * @param pre_cycleid
	 * @param id
	 * @param unitcode
	 * @param U0301
	 * @param dataHeadList
	 * @return
	 */
	public ArrayList getU02CompareDataList(String id,String unitcode,String U0301,ArrayList dataHeadList,String current_year)
	{
		ArrayList list=new ArrayList();
		DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			

			String balance="";
			String balance_percent="";
			if(paramMap.get("u03")!=null)
			{
				HashMap map=(HashMap)paramMap.get("u03");
				if(map.get("differpercent")!=null&&((String)map.get("differpercent")).trim().length()>0) {
                    balance_percent=((String)map.get("differpercent")).trim();
                }
				if(map.get("differ")!=null&&((String)map.get("differ")).trim().length()>0) {
                    balance=(String)map.get("differ");
                }
			}
			
			

			String flag=isCollectUnit(unitcode);  //是否是汇总单位
			String unitcode_str=" unitcode='"+unitcode+"' ";
			if("1".equals(flag)) {
                unitcode_str=" unitcode like '"+unitcode+"%' ";
            }

			StringBuffer sql_sub=new StringBuffer(" select U0305,U0307,U0309,U0311,U0313,U0315,U0317 from ");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+") U0305,SUM(("+Sql_switcher.isnull("U0211","0")+"+"+Sql_switcher.isnull("U0213","0")+")*12+"+Sql_switcher.isnull("U0215","0")+") U0307 from u02 where id="+id+" and "+unitcode_str+" and ( u0209='1' or u0209='4' )  and u0207='1' and escope='1' ) a1,");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+"+"+Sql_switcher.isnull("U0223","0")+") U0309,SUM(("+Sql_switcher.isnull("U0211","0")+"+"+Sql_switcher.isnull("U0213","0")+")*12+"+Sql_switcher.isnull("U0215","0")+") U0311 from u02 where id="+id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='2') a2,");
			sql_sub.append(" (select sum("+Sql_switcher.isnull("U0217","0")+"+"+Sql_switcher.isnull("U0223","0")+") U0313,SUM(("+Sql_switcher.isnull("U0225","0")+"+"+Sql_switcher.isnull("U0226","0")+"+"+Sql_switcher.isnull("U0227","0")+"+"+Sql_switcher.isnull("U0229","0")+")*12+"+Sql_switcher.isnull("U0231","0")+") U0315 from u02 where id="+id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='3') a3,");
			sql_sub.append(" (select SUM("+Sql_switcher.isnull("U0235","0")+"*12+"+Sql_switcher.isnull("U0237","0")+") U0317 from u02 where id="+id+" and "+unitcode_str+"  and ( u0209='1' or u0209='4' ) and u0207='1' and escope='4') a4");
			
			rowSet=dao.search(sql_sub.toString());
			if(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("name","根据表2人员明细估算的年支出金额");
				abean.set("person_type",AdminCode.getCodeName("62","1"));
				
				LazyDynaBean _bean=new LazyDynaBean();
				for(int i=3;i<dataHeadList.size();i++)
				{
					_bean=new LazyDynaBean();
					String temp=(String)dataHeadList.get(i);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                        value=myformat1.format(rowSet.getDouble(temp));
                    }
					_bean.set("itemid",temp);
					_bean.set("value",value);
					_bean.set("over","0");
					_bean.set("desc","");
					abean.set(temp, _bean);
				}
				list.add(abean);
			}
			
			
			boolean isValue=false;
			rowSet=dao.search("select * from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id);
			if(rowSet.next()) {
                isValue=true;
            }
			if(!isValue)
			{
				RecordVo vo_1=new RecordVo("u03");
				vo_1.setInt("id",Integer.parseInt(id));
				vo_1.setString("unitcode",unitcode);
				vo_1.setString("u0301","1");
				vo_1.setString("u0303",current_year);
				vo_1.setInt("editflag",1);
				dao.addValueObject(vo_1);
			}
			
			StringBuffer sql_select=new StringBuffer("");
			StringBuffer sql_select2=new StringBuffer("");
			StringBuffer sql_select_sub=new StringBuffer("");
			StringBuffer sql_select_sub2=new StringBuffer("");
			for(int i=3;i<dataHeadList.size();i++)
			{
				String temp=(String)dataHeadList.get(i);
				sql_select.append(","+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+" "+temp);
				sql_select2.append(",("+Sql_switcher.isnull("a."+temp,"0")+"-"+Sql_switcher.isnull("b."+temp,"0")+")/nullif(b."+temp+",0)*100 "+temp);
				sql_select_sub.append(","+temp);
				
			}
			StringBuffer sql=new StringBuffer("select "+sql_select.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id+") a,");
			sql.append("( "+sql_sub.toString()+" ) b ");
			rowSet=dao.search(sql.toString());
			if(rowSet.next())			
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("name","本期支出与根据表2人员明细估算的年支出差异金额");
				abean.set("person_type",AdminCode.getCodeName("62","1"));

				LazyDynaBean _bean=new LazyDynaBean();
				for(int i=3;i<dataHeadList.size();i++)
				{
					_bean=new LazyDynaBean();
					String temp=(String)dataHeadList.get(i);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                        value=myformat1.format(rowSet.getDouble(temp));
                    }
					_bean.set("itemid",temp);
					_bean.set("value",value);
					if((balance!=null&&balance.trim().length()>0)&&value.length()>0&&Math.abs(Double.parseDouble(value))>Double.parseDouble(balance))
					{
						_bean.set("over","1");
					}
					else {
                        _bean.set("over","0");
                    }
					_bean.set("desc","");
					abean.set(temp, _bean);
				}
				list.add(abean);
			}
			
			
			sql=new StringBuffer("select "+sql_select2.substring(1)+" from (select "+sql_select_sub.substring(1)+"  from u03 where u0301=1 and unitcode='"+unitcode+"' and id="+id+") a,");
			sql.append("( "+sql_sub.toString()+" ) b ");
			rowSet=dao.search(sql.toString());
			if(rowSet.next())			
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("name","本期支出与根据表2人员明细估算的年支出差异比例");
				abean.set("person_type",AdminCode.getCodeName("62","1"));

				LazyDynaBean _bean=new LazyDynaBean();
				for(int i=3;i<dataHeadList.size();i++)
				{
					_bean=new LazyDynaBean();
					String temp=(String)dataHeadList.get(i);
					String value="";
					if(rowSet.getString(temp)!=null&&rowSet.getDouble(temp)!=0) {
                        value=myformat1.format(rowSet.getDouble(temp));
                    }
					_bean.set("itemid",temp);
					
					if(value.length()>0) {
                        _bean.set("value",PubFunc.round(value,2)+"%");
                    } else {
                        _bean.set("value",value);
                    }
					//System.out.println("ddd"+balance_percent+"dddvalue="+value);
					if((balance!=null&&balance.trim().length()>0)&&
							value.length()>0&&balance_percent.length()>0&&
							Math.abs(Double.parseDouble(value))>
							Double.parseDouble(balance_percent))
					{
						_bean.set("over","1");
					}
					else {
                        _bean.set("over","0");
                    }
					_bean.set("desc","");
					abean.set(temp, _bean);
				}
				list.add(abean);
			}
			
			dealWithPreCompareDataList2(list,dataHeadList,unitcode,id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try
			 {
				 if(rowSet!=null) {
                     rowSet.close();
                 }
		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}
		return list;
	}
	
	
	
	/**
	 * 取得表5前次周期id
	 * @param unitcode
	 * @param year
	 * @return
	 */
	public int hasDataByPre_u05(String unitcode,String year)
	{
		int cycle_id=0;
		RowSet rowSet=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search("select * from U05 where U0503<'"+year+"' and unitcode='"+unitcode+"' order by id desc ");
			if(rowSet.next()) {
                cycle_id=rowSet.getInt("id");
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
					 if(rowSet!=null) {
                         rowSet.close();
                     }
					
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
		}
		return cycle_id;
		
	}
	
	
	
	/**
	 * 判断上年同期(上年同月)是否有数据
	 * @param year
	 * @param unitcode
	 * @param u0301
	 * @return
	 */
	public int hasDatabyPreyear(String unitcode,RecordVo vo)
	{
		int cycle_id=0;
		RowSet rowSet =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Date d=vo.getDate("bos_date");
			Calendar cd=Calendar.getInstance();
			cd.setTime(d);
			int year=Integer.parseInt(vo.getString("theyear"))-1;
			 rowSet=dao.search("select * from tt_cycle where theyear='"+year+"' and  "+Sql_switcher.month("bos_date")+"="+(cd.get(Calendar.MONTH)+1));
			if(rowSet.next())
			{
				cycle_id=rowSet.getInt("id");
				rowSet=dao.search("select * from u03 where id="+cycle_id+" and unitcode='"+unitcode+"' and u0301='1' ");
				if(rowSet.next())
				{
					
				}
				else {
                    cycle_id=0;
                }
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			
				try {
					if(rowSet!=null) {
                        rowSet.close();
                    }
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
		}
		return cycle_id;
	}


	public String getSubunitInfo(ArrayList list){
		StringBuffer bodyHtml=new StringBuffer();
		for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			if("-1".equals(bean.get("flag"))&&!"0".equals(bean.get("size"))){//未上报
				int size = Integer.parseInt((String)bean.get("size"));
				if(size%2==0){
					size = size/2;
				}else{
					size = size/2+1;
				}
				
				String unitnames = (String)bean.get("unitnames");
				unitnames =unitnames.substring(0,unitnames.length()-1);
				String []names = unitnames.split(",");
				//System.out.println(unitnames);
				bodyHtml.append("<tr>");
				bodyHtml
				.append("<td align='center' width='100'  class='RecordRow' rowspan='"+size+"'  nowrap>未上报</td>");
				
				for(int j =1;j<size+1;j++){
					if(names.length<j*2){
						if(j!=1) {
                            bodyHtml.append("<tr>");
                        }
						bodyHtml
						.append("<td align='left' width='300'  class='RecordRow' colspan='3'  nowrap>"+names[j*2-2]+"</td>");
						bodyHtml.append("</tr>");
						break;
					}
					if(j!=1) {
                        bodyHtml.append("<tr>");
                    }
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow' colspan='2'  nowrap>"+names[j*2-2]+"</td>");
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow'  nowrap>"+names[j*2-1]+"</td>");
					bodyHtml.append("</tr>");
					
				}
			}
			if("1".equals(bean.get("flag"))&&!"0".equals(bean.get("size"))){//上报
				int size = Integer.parseInt((String)bean.get("size"));
				if(size%2==0){
					size = size/2;
				}else{
					size = size/2+1;
				}
				
				String unitnames = (String)bean.get("unitnames");
				unitnames =unitnames.substring(0,unitnames.length()-1);
				String []names = unitnames.split(",");
				bodyHtml.append("<tr>");
				bodyHtml
				.append("<td align='center' width='100'  class='RecordRow' rowspan='"+size+"'  nowrap>已上报</td>");
			
				for(int j =1;j<size+1;j++){
					if(names.length<j*2){
						if(j!=1) {
                            bodyHtml.append("<tr>");
                        }
						bodyHtml
						.append("<td align='left' width='300'  class='RecordRow' colspan='3'  nowrap>"+names[j*2-2]+"</td>");
						bodyHtml.append("</tr>");
						break;
					}
					if(j!=1) {
                        bodyHtml.append("<tr>");
                    }
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow' colspan='2'  nowrap>"+names[j*2-2]+"</td>");
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow'  nowrap>"+names[j*2-1]+"</td>");
					bodyHtml.append("</tr>");
					
				}
			}
			if("2".equals(bean.get("flag"))&&!"0".equals(bean.get("size"))){//驳回
				int size = Integer.parseInt((String)bean.get("size"));
				if(size%2==0){
					size = size/2;
				}else{
					size = size/2+1;
				}
				
				String unitnames = (String)bean.get("unitnames");
				unitnames =unitnames.substring(0,unitnames.length()-1);
				String []names = unitnames.split(",");
				bodyHtml.append("<tr>");
				bodyHtml
				.append("<td align='center' width='100'  class='RecordRow' rowspan='"+size+"'  nowrap>驳回</td>");
			
				for(int j =1;j<size+1;j++){
					if(names.length<j*2){
						if(j!=1) {
                            bodyHtml.append("<tr>");
                        }
						bodyHtml
						.append("<td align='left' width='300'  class='RecordRow' colspan='3'  nowrap>"+names[j*2-2]+"</td>");
						bodyHtml.append("</tr>");
						break;
					}
					if(j!=1) {
                        bodyHtml.append("<tr>");
                    }
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow' colspan='2'  nowrap>"+names[j*2-2]+"</td>");
					bodyHtml
					.append("<td align='left' width='300'  class='RecordRow'  nowrap>"+names[j*2-1]+"</td>");
					bodyHtml.append("</tr>");
					
				}
			}
		}
		return bodyHtml.toString();
	}



	public String getSelfUnitCode() {
		return selfUnitCode;
	}

	public String throwunit(String cycle_id,String tt){
		ContentDAO dao = new ContentDAO(this.conn);
		 Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
		 if(cycle_id!=null&&cycle_id.trim().length()>0)
			{
				RecordVo _vo = new RecordVo("tt_cycle");
				_vo.setInt("id", Integer.parseInt(cycle_id));
				try {
					_vo =dao.findByPrimaryKey(_vo);
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
					//Date date=;
					d.setTime(_vo.getDate("bos_date"));
					yy=d.get(Calendar.YEAR);
					mm=d.get(Calendar.MONTH)+1;
					dd=d.get(Calendar.DATE);
				
			}
			StringBuffer ext_sql = new StringBuffer();
			if("".equals(tt)){
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			}else{
				ext_sql.append(" and ( "+Sql_switcher.year("tt.end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("tt.end_date")+"="+yy+" and "+Sql_switcher.month("tt.end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("tt.end_date")+"="+yy+" and "+Sql_switcher.month("tt.end_date")+"="+mm+" and "+Sql_switcher.day("tt.end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("tt.start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("tt.start_date")+"="+yy+" and "+Sql_switcher.month("tt.start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("tt.start_date")+"="+yy+" and "+Sql_switcher.month("tt.start_date")+"="+mm+" and "+Sql_switcher.day("tt.start_date")+"<="+dd+" ) ) ");	 			
			}
		
			return ext_sql.toString();
	}




	public void setSelfUnitCode(String selfUnitCode) {
		this.selfUnitCode = selfUnitCode;
	}
	
}
