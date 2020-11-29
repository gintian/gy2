package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:汇报关系业务类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 28, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class RenderRelationBo {
	private Connection conn=null;
	private String fieldItem="";  //直接上级指标
	private int dbflag=Sql_switcher.searchDbServer();
	private UserView userView=null;
	
	public RenderRelationBo(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
		this.fieldItem=getPS_SUPERIOR_value();
	}
	public RenderRelationBo(Connection con)
	{
		this.conn=con;
		this.fieldItem=getPS_SUPERIOR_value();
	}
	public HashMap getPer_MainBodyInfo(ArrayList dbnameList,String posid,int level,String planid)
	{
		HashMap map = new HashMap();
		try
		{
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(planid+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,planid+"");
				BatchGradeBo.getPlanLoadXmlMap().put(planid+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
			String targetAppMode=(String)params.get("targetAppMode"); 
			String targetMakeSeries=(String)params.get("targetMakeSeries");
			int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap mainbodySet=this.getMainbodySet("0");
			RowSet rs=null;
			for(int i=0;i<dbnameList.size();i++)
			{
				String nbase=(String)dbnameList.get(i);
    	    	StringBuffer buf= new StringBuffer();
    	     	buf.append("select a.b0110,a.e0122,a.e01a1,b.object_id,b.body_id,b.plan_id from per_mainbody b,"+nbase+"A01 a where ");
    	    	buf.append("b.mainbody_id = '"+userView.getA0100()+"' and a.a0100=b.object_id and b.plan_id="+planid);
    	        rs =dao.search(buf.toString());
    	        while(rs.next())
    	        {
    	        	LazyDynaBean bean = new LazyDynaBean();
    	        	bean.set("b0110",rs.getString("b0110"));
    	        	bean.set("a0100",rs.getString("object_id"));
    	        	bean.set("e0122",rs.getString("e0122"));
    	        	bean.set("e01a1",rs.getString("e01a1"));
    	        	String mainlevel=mainbodySet.get(rs.getString("body_id"))==null?"":(String)mainbodySet.get(rs.getString("body_id"));
    	        	bean.set("level",mainlevel);
    	        	bean.set("isSP","0");
    	        	map.put(nbase.toUpperCase()+rs.getString("object_id"),bean);
    	        }
    	        rs.close();
		    }
			/**按汇报关系走*/
			if(type==1)
			{
				int lay=Integer.parseInt(targetMakeSeries);
				ArrayList list = new ArrayList();
				list.add(posid);
				map=this.getReportRelationChildren(list, lay, dbnameList);
			}
			/**根据自定义的考核主体或者自己定义的考核关系*/
			else
			{
			
				for(int i=0;i<dbnameList.size();i++)
				{
					String nbase=(String)dbnameList.get(i);
	    	    	StringBuffer buf= new StringBuffer();
	    	     	buf.append("select a.b0110,a.e0122,a.e01a1,b.object_id,c.body_id from per_object_std b,"+nbase+"A01 a ,per_mainbody_std c where ");
	    	    	buf.append(" a.a0100=b.object_id and b.object_id=c.object_id and c.mainbody_id='"+userView.getA0100()+"'");
	    	       if("1".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' ");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' ");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("2".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("3".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1' or level_o='-2'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1' or level='-2'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       buf.append(" and c.body_id in (select body_id from per_plan_body where plan_id="+planid+")");
	    	    	//buf.append(")");
	    	    	rs =dao.search(buf.toString());
	    	        while(rs.next())
	    	        {
	    	        	LazyDynaBean bean = new LazyDynaBean();
	    	        	bean.set("b0110",rs.getString("b0110"));
	    	        	bean.set("a0100",rs.getString("object_id"));
	    	        	bean.set("e0122",rs.getString("e0122"));
	    	        	bean.set("e01a1",rs.getString("e01a1"));
	    	        	String mainlevel=mainbodySet.get(rs.getString("body_id"))==null?"":(String)mainbodySet.get(rs.getString("body_id"));
	    	        	bean.set("level",mainlevel);
	    	        	bean.set("isSP","1");
	    	        	map.put(nbase.toUpperCase()+rs.getString("object_id"),bean);
	    	        }
	    	        rs.close();
			    }
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 在员工目标中，不参与审批的考核主体，看不见考核对象
	 * @param dbnameList
	 * @param posid
	 * @param level
	 * @param planid
	 * @return
	 */
	public HashMap getPer_MainBodyInfo2(ArrayList dbnameList,String posid,int level,String planid)
	{
		HashMap map = new HashMap();
		try
		{
			LoadXml parameter_content = null;
	        if(BatchGradeBo.getPlanLoadXmlMap().get(planid+"")==null)
			{
					
	         	parameter_content = new LoadXml(this.conn,planid+"");
				BatchGradeBo.getPlanLoadXmlMap().put(planid+"",parameter_content);
			}
			else
			{
				parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid+"");
			}
			Hashtable params = parameter_content.getDegreeWhole();
			// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
			String targetAppMode=(String)params.get("targetAppMode"); 
			String targetMakeSeries=(String)params.get("targetMakeSeries");
			int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap mainbodySet=this.getMainbodySet("0");
			
			RowSet rs=null;
			/*
			for(int i=0;i<dbnameList.size();i++)
			{
				String nbase=(String)dbnameList.get(i);
    	    	StringBuffer buf= new StringBuffer();
    	     	buf.append("select a.b0110,a.e0122,a.e01a1,b.object_id,b.body_id,b.plan_id from per_mainbody b,"+nbase+"A01 a where ");
    	    	buf.append("b.mainbody_id = '"+userView.getA0100()+"' and a.a0100=b.object_id and b.plan_id="+planid);
    	        rs =dao.search(buf.toString());
    	        while(rs.next())
    	        {
    	        	LazyDynaBean bean = new LazyDynaBean();
    	        	bean.set("b0110",rs.getString("b0110"));
    	        	bean.set("a0100",rs.getString("object_id"));
    	        	bean.set("e0122",rs.getString("e0122"));
    	        	bean.set("e01a1",rs.getString("e01a1"));
    	        	String mainlevel=mainbodySet.get(rs.getString("body_id"))==null?"":(String)mainbodySet.get(rs.getString("body_id"));
    	        	bean.set("level",mainlevel);
    	        	bean.set("isSP","0");
    	        	map.put(nbase.toUpperCase()+rs.getString("object_id"),bean);
    	        }
		    }*/
			/**按汇报关系走*/
			if(type==1)
			{
				int lay=Integer.parseInt(targetMakeSeries);
				ArrayList list = new ArrayList();
				list.add(posid);
				map=this.getReportRelationChildren(list, lay, dbnameList);
			}
			/**根据自定义的考核主体或者自己定义的考核关系*/
			else
			{
			
				for(int i=0;i<dbnameList.size();i++)
				{
					String nbase=(String)dbnameList.get(i);
	    	    	StringBuffer buf= new StringBuffer();
	    	     	buf.append("select a.b0110,a.e0122,a.e01a1,b.object_id,c.body_id from per_object_std b,"+nbase+"A01 a ,per_mainbody_std c where ");
	    	    	buf.append(" a.a0100=b.object_id and b.object_id=c.object_id and c.mainbody_id='"+userView.getA0100()+"'");
	    	       if("1".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' ");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' ");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("2".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("3".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1' or level_o='-2'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1' or level='-2'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	    	//buf.append(")");
	    	    	rs =dao.search(buf.toString());
	    	        while(rs.next())
	    	        {
	    	        	LazyDynaBean bean = new LazyDynaBean();
	    	        	bean.set("b0110",rs.getString("b0110"));
	    	        	bean.set("a0100",rs.getString("object_id"));
	    	        	bean.set("e0122",rs.getString("e0122"));
	    	        	bean.set("e01a1",rs.getString("e01a1"));
	    	        	String mainlevel=mainbodySet.get(rs.getString("body_id"))==null?"":(String)mainbodySet.get(rs.getString("body_id"));
	    	        	bean.set("level",mainlevel);
	    	        	bean.set("isSP","1");
	    	        	map.put(nbase.toUpperCase()+rs.getString("object_id"),bean);
	    	        }
	    	        rs.close();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取部门考核关系
	 * @param dbnameList
	 * @param posid
	 * @param level
	 * @param planid
	 * @param object_id
	 * @return
	 */
	public HashMap getPer_MainBodyInfoForOrg(ArrayList dbnameList,String posid,int level,String planid,String object_id,String leaderid, String targetAppMode, String targetMakeSeries)
	{
		HashMap map = new HashMap();
		try
		{
			// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
			int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap mainbodySet=this.getMainbodySet("0");
			RowSet rs=null;
    	    StringBuffer buf= new StringBuffer();
    	    buf.append("select a.b0110,a.e0122,a.e01a1,a.object_id,a.mainbody_id");
    	     if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    	     {
    	        buf.append(",b.level_o as lv ");
    	     }
    	     else
    	     {
    	    	 buf.append(",b.level as lv ");
    	     }
    	     buf.append(" from per_mainbody a,per_mainbodyset b where a.body_id=b.body_id and a.plan_id=");
    	     buf.append(planid+" and a.object_id='"+object_id+"'");
    	     rs =dao.search(buf.toString());
    	     HashMap tempMap = new HashMap();
    	     while(rs.next())
    	     {
    	        LazyDynaBean bean = new LazyDynaBean();
    	        bean.set("b0110",rs.getString("b0110"));
    	        bean.set("a0100",rs.getString("object_id"));
    	        bean.set("e0122",rs.getString("e0122"));
    	        bean.set("e01a1",rs.getString("e01a1"));
    	        bean.set("level",rs.getString("lv")==null?"1000":rs.getString("lv"));
    	        bean.set("isSP","0");
    	        tempMap.put(rs.getString("mainbody_id")+rs.getString("object_id"),bean);
    	      }
    	     rs.close();
			/**按汇报关系走*/
			if(type==1)
			{
				int lay=Integer.parseInt(targetMakeSeries);
				ArrayList list = new ArrayList();
				list.add(posid);
				map=this.getReportRelationChildrenForORG(list, lay, dbnameList,leaderid,object_id);
			}
			/**根据自定义的考核主体或者自己定义的考核关系*/
			else
			{

				for(int i=0;i<dbnameList.size();i++)
				{
					//String nbase=(String)dbnameList.get(i);
	    	    	buf.setLength(0);
	    	     	buf.append("select b.b0110,b.e0122,b.e01a1,b.object_id,c.body_id,c.mainbody_id from per_object_std b,per_mainbody_std c where ");
	    	    	buf.append(" b.object_id=c.object_id and c.object_id='"+leaderid+"'");
	    	       if("1".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' ");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' ");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("2".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else if("3".equals(targetMakeSeries))
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       else
	    	       {
	    	    	   buf.append(" and c.body_id in(");
	    	    	   buf.append("select distinct body_id from per_mainbodyset where ");
	    	    	   if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    			{
			    			buf.append(" level_o ='1' or level_o='0' or level_o='-1' or level_o='-2'");
			    		}
			    		else
			    		{
			    			buf.append(" level='1' or level='0' or level='-1' or level='-2'");
			    		}
	    	    	   buf.append(")");
	    	       }
	    	       buf.append(" and c.body_id in (select body_id from per_plan_body where plan_id="+planid+")");
	    	    	//buf.append(")");
	    	    	rs =dao.search(buf.toString());
	    	        while(rs.next())
	    	        {
	    	        	LazyDynaBean bean = new LazyDynaBean();
	    	        	bean.set("b0110",rs.getString("b0110"));
	    	        	bean.set("a0100",rs.getString("object_id"));
	    	        	bean.set("e0122",rs.getString("e0122"));
	    	        	bean.set("e01a1",rs.getString("e01a1"));
	    	        	String mainlevel=mainbodySet.get(rs.getString("body_id"))==null?"":(String)mainbodySet.get(rs.getString("body_id"));
	    	        	bean.set("level",mainlevel);
	    	        	bean.set("isSP", "1");
	    	        	map.put(rs.getString("mainbody_id")+object_id,bean);
	    	        }
	    	        rs.close();
			    }
				
			}
			Set keySet = map.keySet();
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				tempMap.put(key,(LazyDynaBean)map.get(key));
			}
			map=tempMap;

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getMainbodySet(String bodytype)
	{
		HashMap map = new HashMap();
		try
		{
			String sql ="select body_id,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			   sql+="level_o"; 
			else
				sql+="level";
			sql+=" as lv from per_mainbodyset where body_type='"+bodytype+"' or body_type is null";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("body_id"),rs.getString("lv"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获得 汇报关系中 直接上级指标
	 * @return
	 */
	public String getPS_SUPERIOR_value()
	{
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
        if(vo==null)
        	return fieldItem;
        String param=vo.getString("str_value");
        if(param==null|| "".equals(param)|| "#".equals(param))
        	return fieldItem;
		fieldItem=param;
		return fieldItem;
	}
	/**
	 * (按汇报关系)考核主体是否是考核对象的直接领导
	 * @param object_id
	 * @param mainbody_id
	 * @param dbname
	 * @return 0:不是  1:是
	 */
	public String  isUnderLeader(String object_id,String mainbody_id,String dbname)
	{
		String isUnderLeader="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(fieldItem.length()>0)
			{
				RowSet rowSet=dao.search("select E01A1 from "+dbname+"a01 where a0100='"+object_id+"'");
				if(rowSet.next())
				{
					if(rowSet.getString(1)!=null&&rowSet.getString(1).length()>0)
					{
						String posID=rowSet.getString(1);
						ArrayList dbList=new ArrayList();
						dbList.add(dbname);
					    HashMap map=getBodyInfo2(posID,dbList,1);
					    if(map.get(dbname+mainbody_id)!=null)
					    	isUnderLeader="1";
					}
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isUnderLeader;
	}
	/**
	 * 取得某职位对应分类等级的人员信息
	 * @param posID 职位id  
	 * @param dbnameList
	 * @param level (0,1,2,3,4)=(主管领导(上上级)、直接上级,同事、下属、下下级)
	 * @return
	 */
	public HashMap getBodyInfo2(String posID,ArrayList dbnameList,int level)
	{
		HashMap map=new HashMap();
		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
				if(fieldItem.length()==0||dbnameList.size()==0||level>5||level<0||posID==null)
					return map;
				for(int i=0;i<dbnameList.size();i++)
				{
					HashMap tempMap=new HashMap();
					String dbname=(String)dbnameList.get(i);
					switch (level)
					{
						case 0:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 1:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 2:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 3:
							tempMap=getLowerMenMap(posID,dbname,level);
							break;
						case 4:
							tempMap=getLowerMenMap(posID,dbname,level);
							break;
					}
					map.putAll(tempMap);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**
	 * 取得某职位对应分类等级的人员信息
	 * @param posID 职位id  
	 * @param dbnameList
	 * @param bodyid (0,1,2,3,4)=(主管领导(上上级)、直接上级,同事、下属、下下级,5本人)
	 * @return
	 */
	public HashMap getBodyInfo(String posID,ArrayList dbnameList,int bodyid)
	{
		HashMap map=new HashMap();
		
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
				String sql="select ";
				if(this.dbflag==Constant.ORACEL)
					sql+=" level_o";
				else
					sql+=" level ";
				sql+=" from per_mainbodyset where body_id="+bodyid;
				RowSet rowSet=dao.search(sql);
			    int level=-1;
				if(rowSet.next())
			    	level=rowSet.getInt(1);
				rowSet.close();
				if(fieldItem.length()==0||dbnameList.size()==0||level>5||level<0||posID==null)
					return map;
				
			
				for(int i=0;i<dbnameList.size();i++)
				{
					HashMap tempMap=new HashMap();
					String dbname=(String)dbnameList.get(i);
					switch (level)
					{
						case 0:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 1:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 2:
							tempMap=getUpperMenMap(posID,dbname,level);
							break;
						case 3:
							tempMap=getLowerMenMap(posID,dbname,level);
							break;
						case 4:
							tempMap=getLowerMenMap(posID,dbname,level);
							break;
					}
					map.putAll(tempMap);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
//	取得取得某职位对应 下级 || 下下级  等级的人员信息
	public HashMap getLowerMenMap(String posID,String dbname,int level)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			ArrayList posList=new ArrayList();
			if(level==3||level==4)
			{
				ArrayList tempList=new ArrayList();
				tempList.add(posID);
				posList=getLowerPos(tempList);
				if(posList.size()==0)
					return map;
			}
			if(level==4)
			{
				ArrayList tempList=getLowerPos(posList);
				if(tempList.size()==0)
					return map;
				posList=tempList;
			}
			
			StringBuffer sub=new StringBuffer("");
			for(int i=0;i<posList.size();i++)
				sub.append(" or e01a1='"+(String)posList.get(i)+"'");
			RowSet rowSet=dao.search("select a0100,b0110,a0101,e0122,e01a1 from  "+dbname+"a01 where "+sub.substring(3));
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean ();
				abean.set("a0100",rowSet.getString("a0100"));
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
				abean.set("e01a1",rowSet.getString("e01a1"));
				map.put(dbname+rowSet.getString("a0100"), abean);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  map;
	}
	
	
	//取得取得某职位对应 主管（上上级） || 直接上级 ||同事 等级的人员信息
	public HashMap getUpperMenMap(String posID,String dbname,int level)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String pos_id="";
			if(level==2) //同事
				pos_id=posID;
			if(level==1||level==0)
			{
				pos_id=getUpperPos(posID);
				if(pos_id.length()==0)
					return map;
			}
			if(level==0)
			{
				pos_id=getUpperPos(pos_id);
				if(pos_id.length()==0)
					return map;
			}
			String sql="select a0100,b0110,a0101,e0122,e01a1 from "+dbname+"a01 where e01a1='"+pos_id+"'";
			if(level==2&&this.userView!=null) //同事
				sql+=" and a0100<>'"+this.userView.getA0100()+"'";
			RowSet rowSet=dao.search(sql);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean ();
				abean.set("a0100",rowSet.getString("a0100"));
				abean.set("b0110",rowSet.getString("b0110"));
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
				abean.set("e01a1",rowSet.getString("e01a1"));
				map.put(dbname+rowSet.getString("a0100"), abean);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  map;
	}
	
	
	
	
	public void getLowerPosInfo(ArrayList posIDs,String targetMakeSeries,ArrayList lowerPosList)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			
			if(posIDs.size()>0&&fieldItem!=null&&fieldItem.trim().length()>0 && DataDictionary.getFieldItem(fieldItem)!=null)
			{
				ArrayList _posList=new ArrayList();
				if(!"0".equals(targetMakeSeries))
				{
					StringBuffer sub=new StringBuffer(""); 
					for(int i=0;i<posIDs.size();i++)
						sub.append(" or "+fieldItem+"='"+(String)posIDs.get(i)+"'");
					rowSet=dao.search("select * from K01 where "+sub.substring(3));
					while(rowSet.next())
					{
						lowerPosList.add((String)rowSet.getString("e01a1"));
						_posList.add((String)rowSet.getString("e01a1"));
					}
					if(rowSet!=null)
						rowSet.close();
				}
				
				if("2".equals(targetMakeSeries))
					getLowerPosInfo(_posList,"1",lowerPosList);
				else if("3".equals(targetMakeSeries))
					getLowerPosInfo(_posList,"2",lowerPosList);
				else if("4".equals(targetMakeSeries))
					getLowerPosInfo(_posList,"3",lowerPosList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		} 
	}
	
	
	/**
	 * 根据当前职务列表找到直接下级职务
	 * @param posID
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getLowerPos(ArrayList posIDs) throws GeneralException
	{
		ArrayList list=new ArrayList();
		if(this.fieldItem==null||this.fieldItem.trim().length()==0)
			return list;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
				StringBuffer sub=new StringBuffer("");
				if(posIDs.size()<=0)
					return list;
				for(int i=0;i<posIDs.size();i++)
					sub.append(" or "+fieldItem+"='"+(String)posIDs.get(i)+"'");
				DbWizard dbw = new DbWizard(this.conn);
				if(!dbw.isExistField("K01", fieldItem, false)){
					throw GeneralExceptionHandler.Handle(new Exception("直接上级岗位指标"+fieldItem+"已被删除，请重新设置！"));
				}
				rowSet=dao.search("select * from K01 where "+sub.substring(3));
				while(rowSet.next())
				{
					list.add((String)rowSet.getString("e01a1"));
				}
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	

	/**
	 * 根据当前职务找到直接上级职务
	 * @param posID
	 * @return
	 */
	public String getUpperPos(String posID)
	{
		String upperPosID="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			if(fieldItem!=null&&fieldItem.trim().length()>0)
			{
				rowSet=dao.search("select * from K01 where E01A1='"+posID+"'");
				if(rowSet.next())
				{
					if(rowSet.getString(fieldItem)!=null&&rowSet.getString(fieldItem).length()>1)
					{
						upperPosID=rowSet.getString(fieldItem);
					}
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return upperPosID;
	}
	
	
	
	
	
	
	
	
	/***
	 * 取下级信息
	 * @param posid
	 * @param level
	 * @param dbnameList
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getReportRelationChildren(ArrayList posid,int level,ArrayList dbnameList) throws GeneralException
	{
		HashMap map = new HashMap();
		try
		{
			ArrayList posIDs=posid;
			for(int n=1;n<=level;n++)
			{
				
	     	    posIDs = this.getLowerPos(posIDs);
	     		StringBuffer sql = new StringBuffer();
		    	ContentDAO dao = new ContentDAO(this.conn);
		    	StringBuffer posids=new StringBuffer("");
		    	if(posIDs.size()==0)
		    		continue;
		    	for(int i=0;i<posIDs.size();i++)
		    	{
		    		if(i!=0)
		    			posids.append(",");
		    		posids.append("'"+(String)posIDs.get(i)+"'");
		    	}
		    	for(int j=0;j<dbnameList.size();j++)
		    	{
		    		String nbase=(String)dbnameList.get(j);
		    		sql.setLength(0);
		    		sql.append("select a0100,b0110,a0101,e0122,e01a1 from "+nbase+"a01 ");
			    	sql.append(" where e01a1 in("+posids+")");
			    	RowSet rs = dao.search(sql.toString());
			    	while(rs.next()){
			    		LazyDynaBean bean = new LazyDynaBean();
			    		bean.set("a0101",rs.getString("a0101"));
				    	bean.set("a0100", rs.getString("a0100"));
				    	bean.set("e01a1",rs.getString("e01a1"));
				    	bean.set("b0110", rs.getString("b0110"));
				    	bean.set("e0122",rs.getString("e0122"));
				    	if(n==1)
				    		bean.set("level","1");
				    	if(n==2)
				    		bean.set("level","0");
				    	if(n==3)
				    		bean.set("level","-1");
				    	if(n==4)
				    		bean.set("level","-2");
				    	bean.set("isSP","1");
				    	map.put(nbase.toUpperCase()+rs.getString("a0100"),bean);
			    	 }
			    	rs.close();
		    	}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	public HashMap getReportRelationChildrenForORG(ArrayList posid,int level,ArrayList dbnameList,String leaderid,String object_id)
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList posIDs=posid;
			for(int n=1;n<=level;n++)
			{
				
	     	    posIDs = this.getParentPosIDList(posIDs);
	     		StringBuffer sql = new StringBuffer();
		    	StringBuffer posids=new StringBuffer("");
		    	if(posIDs.size()==0)
		    		continue;
		    	for(int i=0;i<posIDs.size();i++)
		    	{
		    		if(i!=0)
		    			posids.append(",");
		    		posids.append("'"+(String)posIDs.get(i)+"'");
		    	}
		    	for(int j=0;j<dbnameList.size();j++)
		    	{
		    		String nbase=(String)dbnameList.get(j);
		    		sql.setLength(0);
		    		sql.append("select a0100,b0110,a0101,e0122,e01a1 from "+nbase+"a01 ");
			    	sql.append(" where e01a1 in("+posids+")");
			    	RowSet rs = dao.search(sql.toString());
			    	while(rs.next()){
			    		LazyDynaBean bean = new LazyDynaBean();
			    		bean.set("a0101",rs.getString("a0101"));
				    	bean.set("a0100", rs.getString("a0100"));
				    	bean.set("e01a1",rs.getString("e01a1"));
				    	bean.set("b0110", rs.getString("b0110"));
				    	bean.set("e0122",rs.getString("e0122"));
				    	if(n==1)
				    		bean.set("level","1");
				    	if(n==2)
				    		bean.set("level","0");
				    	if(n==3)
				    		bean.set("level","-1");
				    	if(n==4)
				    		bean.set("level","-2");
				    	bean.set("isSP", "1");
				    	map.put(rs.getString("a0100")+object_id,bean);
			    	 }
			    	rs.close();
		    	}
			}
			StringBuffer sql= new StringBuffer();
			for(int j=0;j<dbnameList.size();j++)
	    	{
	    		String nbase=(String)dbnameList.get(j);
	    		sql.setLength(0);
	    		sql.append("select a0100,b0110,a0101,e0122,e01a1 from "+nbase+"a01 ");
		    	sql.append(" where a0100='"+leaderid+"'");
		    	RowSet rs = dao.search(sql.toString());
		    	while(rs.next()){
		    		LazyDynaBean bean = new LazyDynaBean();
		    		bean.set("a0101",rs.getString("a0101"));
			    	bean.set("a0100", rs.getString("a0100"));
			    	bean.set("e01a1",rs.getString("e01a1"));
			    	bean.set("b0110", rs.getString("b0110"));
			    	bean.set("e0122",rs.getString("e0122"));
			    	bean.set("level","5");
			    	bean.set("isSP","1");
			    	map.put(rs.getString("a0100")+object_id,bean);
		    	 }
		    	rs.close();
	    	}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取各上级领导集合
	 * @param posid
	 * @param level
	 * @param dbnameList
	 * @return
	 */
	public HashMap getReportLeaderMap(ArrayList posid,int level,ArrayList dbnameList)
	{
		HashMap map = new HashMap();
		try
		{
			ArrayList posIDs=posid;
			for(int n=1;n<=level;n++)
			{
				
	     	    posIDs = this.getParentPosIDList(posIDs);
	     		StringBuffer sql = new StringBuffer();
		    	ContentDAO dao = new ContentDAO(this.conn);
		    	StringBuffer posids=new StringBuffer("");
		    	if(posIDs.size()==0)
		    		continue;
		    	for(int i=0;i<posIDs.size();i++)
		    	{
		    		if(i!=0)
		    			posids.append(",");
		    		posids.append("'"+(String)posIDs.get(i)+"'");
		    	}
		    	for(int j=0;j<dbnameList.size();j++)
		    	{
		    		String nbase=(String)dbnameList.get(j);
		    		sql.setLength(0);
		    		sql.append("select a0100,b0110,a0101,e0122,e01a1 from "+nbase+"a01 ");
			    	sql.append(" where e01a1 in("+posids+")");
			    	RowSet rs = dao.search(sql.toString());
			    	while(rs.next()){
			    		LazyDynaBean bean = new LazyDynaBean();
			    		bean.set("a0101",rs.getString("a0101"));
				    	bean.set("a0100", rs.getString("a0100"));
				    	bean.set("e01a1",rs.getString("e01a1"));
				    	bean.set("b0110", rs.getString("b0110"));
				    	bean.set("e0122",rs.getString("e0122"));
				    	if(n==1)
				    		bean.set("level","1");
				    	if(n==2)
				    		bean.set("level","0");
				    	if(n==3)
				    		bean.set("level","-1");
				    	if(n==4)
				    		bean.set("level","-2");
				    	map.put(nbase.toUpperCase()+rs.getString("a0100"),bean);
			    	 }
			    	rs.close();
		    	}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getReportLeaderMap2(ArrayList posid,int level,ArrayList dbnameList)
	{
		HashMap map = new HashMap();
		try
		{
			ArrayList posIDs=posid;
			for(int n=1;n<=level;n++)
			{
				
	     	    posIDs = this.getParentPosIDList(posIDs);
			}
	     		StringBuffer sql = new StringBuffer();
		    	ContentDAO dao = new ContentDAO(this.conn);
		    	StringBuffer posids=new StringBuffer("");
		    	if(posIDs.size()==0)
		    		return map;
		    	for(int i=0;i<posIDs.size();i++)
		    	{
		    		if(i!=0)
		    			posids.append(",");
		    		posids.append("'"+(String)posIDs.get(i)+"'");
		    	}
		    	for(int j=0;j<dbnameList.size();j++)
		    	{
		    		String nbase=(String)dbnameList.get(j);
		    		sql.setLength(0);
		    		sql.append("select a0100,b0110,a0101,e0122,e01a1 from "+nbase+"a01 ");
			    	sql.append(" where e01a1 in("+posids+")");
			    	RowSet rs = dao.search(sql.toString());
			    	HashMap bodyMap = this.getBodyIdByLevel();
			    	while(rs.next()){
			    		LazyDynaBean bean = new LazyDynaBean();
			    		bean.set("a0101",rs.getString("a0101"));
				    	bean.set("a0100", rs.getString("a0100"));
				    	bean.set("e01a1",rs.getString("e01a1"));
				    	bean.set("b0110", rs.getString("b0110"));
				    	bean.set("e0122",rs.getString("e0122"));
				    	if(level==1)
				    	{
				    		bean.set("level","1");
				    		bean.set("body_id",(String)bodyMap.get("1"));
				    	}
				    	if(level==2)
				    	{
				    		bean.set("level","0");
				    		bean.set("body_id",(String)bodyMap.get("0"));
				    	}
				    	if(level==3)
				    	{
				    		bean.set("level","-1");
				    		bean.set("body_id",(String)bodyMap.get("-1"));
				    	}
				    	if(level==4)
				    	{
				    		bean.set("level","-2");
				    		bean.set("body_id",(String)bodyMap.get("-2"));
				    	}
				    	map.put(nbase.toUpperCase()+rs.getString("a0100"),bean);
			    	 }
			    	rs.close();
		    	}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getBodyIdByLevel()
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select body_id,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o ";
			else
				sql+=" level ";
			sql+=" as lv from per_mainbodyset where ";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o ";
			else
				sql+=" level ";
			sql+=" is not null order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("lv"), rs.getString("body_id"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/***
	 * 取上级
	 * @param posIDs
	 * @return
	 */
	public ArrayList getParentPosIDList(ArrayList posIDs)
	{
		ArrayList list = new ArrayList();
		if(fieldItem.length()==0||posIDs.size()<=0)
			return list;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<posIDs.size();i++)
			{
				if(i!=0)
					buf.append(",");
				buf.append("'"+(String)posIDs.get(i)+"'");
			}
			String sql = "select * from k01 where e01a1 in("+buf.toString()+")";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				if(rs.getString(fieldItem)!=null&&rs.getString(fieldItem).length()>1)
				{
					list.add(rs.getString(fieldItem));
				}
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	   return list;
	
	}
	/**
	 * 
	 * @param planid
	 * @param userView
	 * @param isCanSp 是否是审批主体
	 * @return
	 */
	public HashMap getKhOrgField(String planid,UserView userView)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			RecordVo plan_vo = new RecordVo("per_plan");
			ContentDAO dao  =new ContentDAO(this.conn);
			plan_vo.setInt("plan_id",Integer.parseInt(planid));
			plan_vo=dao.findByPrimaryKey(plan_vo);
			LoadXml loadxml=null;
			StringBuffer sql = new StringBuffer("select distinct plan_id,");
			if(BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(plan_vo.getInt("plan_id")))==null)
			{
				loadxml=new LoadXml(this.conn,String.valueOf(plan_vo.getInt("plan_id")));
				BatchGradeBo.getPlanLoadXmlMap().put(String.valueOf(plan_vo.getInt("plan_id")),loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(plan_vo.getInt("plan_id")));		
			Hashtable planParam=loadxml.getDegreeWhole();
			if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
				sql.append(" b0110 as object_id ");
			else if(plan_vo.getInt("object_type")==2)
				sql.append(" a0100 as object_id ");
			sql.append(" from p04 where 1=1 ");
			sql.append(" and plan_id="+planid);
			if(plan_vo.getInt("status")==4||plan_vo.getInt("status")==8)
			{
				sql.append(" and not (state is not null and state=-1 and chg_type is not null and chg_type=3) ");
			}
			if(plan_vo.getInt("status")>4&&plan_vo.getInt("status")!=8)
			{
				if(planParam.get("NoShowTargetAdjustHistory")!=null&& "True".equalsIgnoreCase((String)planParam.get("NoShowTargetAdjustHistory")))
					sql.append(" and not (state is not null and state=-1 and chg_type is not null and chg_type=3)");//and ( ( state=-1 and (chg_type!=3 or chg_type is null) ) or state is null or state<>-1 )
			}
			if(userView.getUnitIdByBusi("5")!=null&&userView.getUnitIdByBusi("5").length()>0&&!"UN".equalsIgnoreCase(userView.getUnitIdByBusi("5")))//&&(this.plan_vo.getInt("status")!=4&&this.plan_vo.getInt("status")!=6&&this.perObject_vo.getString("sp_flag")!=null&&!this.perObject_vo.getString("sp_flag").equals("")&&!this.perObject_vo.getString("sp_flag").equals("01"))
			{
				String temp=userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				String[] arr=temp.split("`");
				StringBuffer t_buf = new StringBuffer();
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i]))
						continue;
					t_buf.append(" or score_org like '"+arr[i].substring(2)+"%'");
				}
				t_buf.append(" or score_org is null or score_org =''");
				sql.append(" and ("+t_buf.toString().substring(3)+")");
			}else
			{
		    	sql.append(" and (UPPER(score_org)='"+userView.getUserOrgId()+"' or UPPER(score_org)='"+userView.getUserDeptId()+"'");
		    	sql.append(" or score_org is null or score_org ='')");
			}
			rs=dao.search(sql.toString());
			StringBuffer str = new StringBuffer("");
			while(rs.next())
			{
				map.put(rs.getString("plan_id")+rs.getString("object_id"), "1");
				str.append(",'"+rs.getString("object_id")+"'");
			}
			if(rs!=null)
				rs.close();
			
			// 补充
			sql.setLength(0);
			sql.append(" select plan_id,object_id from per_object where plan_id = "+planid+" ");
			if(str!=null && str.toString().trim().length()>0)
			{
				sql.append(" and object_id not in ("+str.toString().substring(1)+") ");
			}
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				map.put(rs.getString("plan_id")+rs.getString("object_id"), "1");
			}
			if(rs!=null)
				rs.close();
			
			/**还差一些，要判断模板中是否有共性项目*/
			if(Integer.parseInt(plan_vo.getString("status"))<4||Integer.parseInt(plan_vo.getString("status"))==8)
			{
		    	sql.setLength(0);
		    	sql.append("select plan_id,object_id from per_object where plan_id="+planid+" and (sp_flag is null or sp_flag='' or sp_flag='01') ");
		    	sql.append(" and object_id not in (select ");
		    	if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
		    		sql.append(" b0110 as object_id ");
		    	else if(plan_vo.getInt("object_type")==2)
		    		sql.append(" a0100 as object_id ");
		    	sql.append(" from p04 where plan_id="+planid+")");
		    	rs = dao.search(sql.toString());
		    	while(rs.next())
		    	{
		    		map.put(rs.getString("plan_id")+rs.getString("object_id"), "1");
		    	}
		    	if(rs!=null)
					rs.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					if(rs!=null)
						rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	public boolean isByOrg(String a0100,String object_id,String leader,int status)
	{
		boolean flag=true;
		try{
			/*if(a0100.equalsIgnoreCase(object_id)||a0100.equalsIgnoreCase(leader))
			{
				flag=false;
			}*/
			if(status<4||status==8)
			{
				/**本人或团队负责人，展现所有*/
				flag=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	public boolean isByOrg2(String a0100,String object_id,String leader,int status)
	{
		boolean flag=true;
		try{
			if(a0100.equalsIgnoreCase(object_id)||a0100.equalsIgnoreCase(leader))
			{
				flag=false;
			} 
			if(status<4||status==8)
			{
				/**本人或团队负责人，展现所有*/
				flag=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
}
