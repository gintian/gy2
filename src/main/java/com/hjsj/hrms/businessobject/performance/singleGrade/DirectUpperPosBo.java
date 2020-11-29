package com.hjsj.hrms.businessobject.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Hashtable;

public class DirectUpperPosBo {
	private Connection conn=null;
	
	public DirectUpperPosBo(Connection con)
	{
		this.conn=con;
	}
	
	
	public DirectUpperPosBo()
	{
		
	}
	
	/**
	 * 
	 * @param model  0：绩效考核  1：民主评测
	 * @return
	 */
	public String getGradeFashion(String model)
	{
		String flag="1";
		Connection conn=null;
		try
		{
			conn = AdminDb.getConnection();
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(conn);
			Hashtable table=bo.analyseParameterXml();
		//	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		//	String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测
		//	this.getFormHM().put("model",model);
			
			if(table!=null&&table.get("MarkingMode")!=null)
				flag=(String)table.get("MarkingMode");   //1:下拉框方式  2：平铺方式	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn!=null)
					conn.close();
			}
			catch(Exception e2)
			{
				
			}
		}
		return flag;
		
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
	 * 根据当前职务找到直接上级职务
	 * @param posID
	 * @return
	 */
	public String getUpperPos(String posID,String fieldItem)
	{
		String upperPosID="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			
			if(fieldItem.trim().length()>1)
			{
				rowSet=dao.search("select * from K01 where E01A1='"+posID+"'");
				if(rowSet.next())
				{
					if(rowSet.getString(fieldItem)!=null&&rowSet.getString(fieldItem).length()>1)
					{
						upperPosID=rowSet.getString(fieldItem);
					}
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return upperPosID;
	}
	
	
	
	
	/**
	 * 考核主体是否是该对象的直属领导
	 * @param objectID
	 * @param mainbodyID
	 * @param aflag  0:直属领导  1:所有上级
	 * @return
	 */
	public boolean  isUpperLead(String objectID,String mainbodyID,int aflag)
	{
		boolean flag=false;
		try
		{
			String object_pos=getUserPos(objectID);
			String mainbody_pos=getUserPos(mainbodyID);
			if(mainbody_pos.length()==0||object_pos.length()==0)
				return flag;
			String fieldItem=getPS_SUPERIOR_value();
			if(aflag==0)
			{
				if(object_pos.trim().length()>1)
				{
					String pos=getUpperPos(object_pos,fieldItem);
					if(pos.equalsIgnoreCase(mainbody_pos))
					{
						flag=true;
					}
				}
			}
			else if(aflag==1)
			{
				while(true)
				{
					if(object_pos.trim().length()>1)
					{
						String pos=getUpperPos(object_pos,fieldItem);
						if(pos.length()==0)
							break;
						if(pos.equalsIgnoreCase(mainbody_pos))
						{
							flag=true;
							break;
						}
						object_pos=pos;
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();		
		}
		return flag;
	}
	
	
	
	//根据 a0100 得到 职位 id.
	public String getUserPos(String userID)
	{
		String e01a1="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet     rowSet=null;
		try
		{
			rowSet=dao.search("select e01a1 from usra01 where a0100='"+userID+"'");
			if(rowSet.next())
			{
				if(rowSet.getString("e01a1")!=null)
					e01a1=rowSet.getString("e01a1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return e01a1;
	}
	
	

}
