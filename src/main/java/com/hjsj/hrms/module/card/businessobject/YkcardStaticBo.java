package com.hjsj.hrms.module.card.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class YkcardStaticBo {

	private static long delayTime=30000;//有效时间30秒
	private static HashMap<String,Object> rTitleMap=new HashMap<String, Object>();
	private static HashMap<String,Object> rNameMap=new HashMap<String, Object>();
	private static HashMap<String,Object> rGridAreaMap=new HashMap<String, Object>();
	private static HashMap<String,Object> gridMinLTMap=new HashMap<String, Object>();
	private static HashMap<String,Object> rGridListMap=new HashMap<String, Object>();
	private static HashMap<String,Object> rGridSetMap=new HashMap<String, Object>();
	private static HashMap<String,Object> disTopMap=new HashMap<String, Object>();
	private static HashMap<String,Object> disLeftMap=new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> getDisRleftList(Connection conn,String tabid,String pageid,int marginleft) throws Exception{
		ArrayList<Object> list=new ArrayList<Object>();
		boolean readDbFlag=false;
		if(disLeftMap.get(tabid+"_"+pageid+"_"+marginleft)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(disLeftMap.get(tabid+"_"+pageid+"_"+marginleft+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			String sql="select distinct rleft from rgrid where tabid="+tabid+" and pageid="+pageid+" order by rleft";
			ContentDAO dao=new ContentDAO(conn);
			ArrayList<Object> rleftlist=new ArrayList<Object>();
			RowSet rs=null;
			try {
				rs=dao.search(sql);
				while(rs.next()){
					rleftlist.add((int)rs.getFloat("rleft")+marginleft+"");
				}
				disLeftMap.put(tabid+"_"+pageid+"_"+marginleft, rleftlist);
				disLeftMap.put(tabid+"_"+pageid+"_"+marginleft+"_time", System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rs);
			}
			
		}
		
		if(disLeftMap.get(tabid+"_"+pageid+"_"+marginleft)!=null)
			list=(ArrayList<Object>)disLeftMap.get(tabid+"_"+pageid+"_"+marginleft);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> getDisRtopList(Connection conn,String tabid,String pageid,int marginTop)throws Exception{
		ArrayList<Object> list=new ArrayList<Object>();
		boolean readDbFlag=false;
		if(disTopMap.get(tabid+"_"+pageid+"_"+marginTop)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(disTopMap.get(tabid+"_"+pageid+"_"+marginTop+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			ContentDAO dao=new ContentDAO(conn);
	    	RowSet rs=null;
			String sql="select distinct rtop from rgrid where tabid=? and pageid=? order by rtop";
			try {
				rs=dao.search(sql,Arrays.asList(tabid,pageid));
				ArrayList<Object> rtoplist=new ArrayList<Object>();
				while(rs.next()){
					rtoplist.add((int)rs.getFloat("rtop")+marginTop+"");
				}
				disTopMap.put(tabid+"_"+pageid+"_"+marginTop, rtoplist);
				disTopMap.put(tabid+"_"+pageid+"_"+marginTop+"_time", System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rs);
			}
		}
		if(disTopMap.get(tabid+"_"+pageid+"_"+marginTop)!=null)
			list=(ArrayList<Object>)disTopMap.get(tabid+"_"+pageid+"_"+marginTop);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> getGridSets(String tabid,String pageid,Connection conn) throws Exception{
		List<Object> list=null;
		boolean readDbFlag=false;
		if(rGridSetMap.get(tabid+"_"+pageid)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(rGridSetMap.get(tabid+"_"+pageid+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			StringBuffer sql=new StringBuffer();
			  sql.append("SELECT ");
		      sql.append("fieldSet.Displayorder, fieldSet.fieldSetId, fieldSet.fieldSetDesc,");
			  sql.append(" fieldSet.UseFlag,  fieldSet.changeFlag, ");
			  sql.append(" fieldSet.reserveitem");
			  sql.append(" FROM fieldSet where fieldSet.fieldSetId in(Select RGrid.setName From ");
			  sql.append("RGrid WHERE (RGrid.Tabid = ");
			  sql.append(tabid);
			  sql.append(") AND (RGrid.PageId = ");
			  sql.append(pageid);
			  sql.append("))");
			  sql.append(" union all ");
			  sql.append("SELECT ");
		      sql.append("t_hr_busitable.Displayorder, t_hr_busitable.fieldSetId, t_hr_busitable.fieldSetDesc,");
			  sql.append(" t_hr_busitable.UseFlag,  t_hr_busitable.changeFlag, ");
			  sql.append(" t_hr_busitable.reserveitem");
			  sql.append(" FROM t_hr_busitable where t_hr_busitable.fieldSetId in(Select RGrid.setName From ");
			  sql.append("RGrid WHERE (RGrid.Tabid = ");
			  sql.append(tabid);
			  sql.append(") AND (RGrid.PageId = ");
			  sql.append(pageid);
			  sql.append("))");
			  List<Object> fieldList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			  rGridSetMap.put(tabid+"_"+pageid, fieldList);   
			  rGridSetMap.put(tabid+"_"+pageid+"_time", System.currentTimeMillis());   
		}
		if(rGridSetMap.get(tabid+"_"+pageid)!=null)
			list=(List<Object>)rGridSetMap.get(tabid+"_"+pageid);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> getRgridList(String tabid,String pageid,Connection conn) throws Exception{
		List<Object> list=null;
		boolean readDbFlag=false;
		if(rGridListMap.get(tabid+"_"+pageid)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(rGridListMap.get(tabid+"_"+pageid+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			StringBuffer sql=new StringBuffer();
			sql.append("SELECT * FROM RGrid	WHERE (Tabid =");
			sql.append(tabid);
		    sql.append(" and pageid=");
			sql.append(pageid);
			sql.append(") and rwidth<>0 order by rtop,rleft");
			List<Object> rgridList=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			rGridListMap.put(tabid+"_"+pageid, rgridList);
			rGridListMap.put(tabid+"_"+pageid+"_time", System.currentTimeMillis());
		}
		if(rGridListMap.get(tabid+"_"+pageid)!=null)
			list=(List<Object>)rGridListMap.get(tabid+"_"+pageid);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> getRgridMinLeftTop(String tabid,String pageid,Connection conn) throws Exception{
		List<Object> list=null;
		boolean readDbFlag=false;
		if(gridMinLTMap.get(tabid+"_"+pageid)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(gridMinLTMap.get(tabid+"_"+pageid+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			StringBuffer sql=new StringBuffer();
			sql.append("select min(rleft) as rleft,min(rtop) as rtop from RGrid	WHERE (Tabid =");
			sql.append(tabid);
		    sql.append(" and pageid=");
			sql.append(pageid);
			sql.append(")");
			List<Object> rgridminlefttop=ExecuteSQL.executeMyQuery(sql.toString(),conn);
			gridMinLTMap.put(tabid+"_"+pageid, rgridminlefttop);
			gridMinLTMap.put(tabid+"_"+pageid+"_time", System.currentTimeMillis());
		}
		if(gridMinLTMap.get(tabid+"_"+pageid)!=null)
			list=(List<Object>)gridMinLTMap.get(tabid+"_"+pageid);
		return list;
	} 
	
	/***
	 * 得到表格区域
	 * @param conn
	 * @param tabid
	 * @param pageid
	 * @return
	 * @throws Exception
	 */
	public static float[] getRGridArea(Connection conn,String tabid ,String pageid) throws Exception {
		float[] grid=new float[7];
		boolean readDbFlag=false;
		if(rGridAreaMap.get(tabid+"_"+pageid)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(rGridAreaMap.get(tabid+"_"+pageid+"_time").toString())))
		    readDbFlag=true;
		if(readDbFlag) {
			StringBuffer sql=new StringBuffer();
			String sqlStr="select tabid,gridno,rleft,rtop,rwidth,rheight,pageid from rgrid where tabid="+tabid +" and pageid="+pageid;
			String titleSql=" union all  select tabid,gridno,rleft,rtop,rwidth,rheight,pageid from rpage where tabid="+tabid +" and pageid="+pageid;
		    sql.append("select max(rleft+rwidth) as max_W from ("+sqlStr+titleSql+")grid ");
		    ContentDAO dao=new ContentDAO(conn);
		    float[] rgridA =new float[7];
		    try
		    {
		    	RowSet rs=dao.search(sql.toString());
		    	if(rs.next())
		    		rgridA[2]=rs.getFloat("max_W");//模板整体宽
		    	sql.setLength(0);
		    	sql.append("select max(rtop+rheight) as max_H from  ("+sqlStr+titleSql+")grid ");
		    	rs=dao.search(sql.toString());
		    	if(rs.next())
		    		rgridA[3]=rs.getFloat("max_H");//整体高
		    	sql.setLength(0);
		    	sql.append("select min(rtop) as min_top from  ("+sqlStr+titleSql+")grid ");
		    	rs=dao.search(sql.toString());
		    	if(rs.next())
		    		rgridA[1]=rs.getFloat("min_top");//最上方的位置
		    	sql.setLength(0);
		    	sql.append("select min(rleft) as min_left from  ("+sqlStr+titleSql+")grid " );
		    	rs=dao.search(sql.toString());
		    	if(rs.next())
		    		rgridA[0]=rs.getFloat("min_left");//最左边的位置
		    	
		    	sql.setLength(0);
		    	sql.append("select min(rtop) as min_top from  ("+sqlStr+")grid ");
		    	rs=dao.search(sql.toString());
		    	if(rs.next()) {
		    		rgridA[4]=rs.getFloat("min_top");
		    	}
		    	
		    	sql.setLength(0);
		    	sql.append("select max(rtop+rheight) as max_H from  ("+sqlStr+")grid ");
		    	rs=dao.search(sql.toString());
		    	if(rs.next()) {
		    		rgridA[5]=rs.getFloat("max_H");//最左边的位置
		    	}
		    	
		    	sql.setLength(0);
		    	sql.append("select max(rleft+rWidth) as max_W from  ("+sqlStr+")grid "); //模板内容的最大宽度即模板最右边的宽度
		    	rs=dao.search(sql.toString());
		    	if(rs.next()) {
		    		rgridA[6]=rs.getFloat("max_W");//最左边的位置
		    	}
		    	
		    	rGridAreaMap.put(tabid+"_"+pageid, rgridA);
		    	rGridAreaMap.put(tabid+"_"+pageid+"_time",System.currentTimeMillis());
		    	
		    }catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		}
		
		if(rGridAreaMap.get(tabid+"_"+pageid)!=null)
			grid=(float[])rGridAreaMap.get(tabid+"_"+pageid);
		
		return grid;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Object> getRname(String tabid,Connection conn) throws Exception{
		List<Object> list=null;
		boolean readDbFlag=false;
		if(rNameMap.get(tabid)==null)
			readDbFlag=true;
		else if(isOutTime(Long.parseLong(rNameMap.get(tabid+"_time").toString())))
			readDbFlag=true;
		if(readDbFlag) {
			try {
				List<Object> rnameList =ExecuteSQL.executeMyQuery("select * from rname where Tabid="+tabid,conn);
				rNameMap.put(tabid, rnameList);
				rNameMap.put(tabid+"_time", System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		if(rNameMap.get(tabid)!=null)
			list=(List<Object>)rNameMap.get(tabid);
		
		return list;
	}
	
	public static List<Object> getRtitleMap(String tabid,Connection conn) throws Exception{
		List<Object> list=null;
		boolean readDbFlag=false;
		if(rTitleMap.get(tabid)==null) {
			readDbFlag=true;
		}else {
			if(isOutTime(Long.parseLong(rTitleMap.get(tabid+"_time").toString())))
				readDbFlag=true;
		}
		if(readDbFlag) {
			StringBuffer sql=new StringBuffer();
			sql.append("select * from rTitle where Tabid=");
			sql.append(tabid);
			sql.append(" and isprn=1");
			try {
				List rs = ExecuteSQL.executeMyQuery(sql.toString(),conn);
				sql.delete(0,sql.length());
				rTitleMap.put(tabid, rs);
				rTitleMap.put(tabid+"_time", System.currentTimeMillis());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(rTitleMap.get(tabid)!=null)
			list=(List)rTitleMap.get(tabid);
		return list;
	}
	
	/**
	 * 校验是否超时
	 * */
	private static boolean isOutTime(long waitTime) {
		if((System.currentTimeMillis()-waitTime)>delayTime)
			return true;
		else
			return false;
	}
}
