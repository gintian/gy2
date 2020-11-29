package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataAnalyseUtils;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 提交分析数据
 * <p>Title:SubmitDataAnalyseTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 9, 2007 3:16:34 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SubmitDataAnalyseTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	String temp_Table=(String)this.getFormHM().get("temp_Table");    
    	String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
    	KqParameter para=new KqParameter(this.userView,"",this.getFrameconn());
	    HashMap hashmap =para.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String kq_cardno=(String)hashmap.get("cardno");
		String kq_Gno=(String)hashmap.get("g_no");
		String dataUpdateType="0";		
		String analyseType="1";
		String mark=getDataprocessing();
		mark=mark!=null&&mark.length()>0?mark:"0";
		if("1".equalsIgnoreCase(mark))
		{
			analyseType="101";
		}else
		{
			analyseType="1";
		}
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList("","2");
		DataAnalyseUtils dataAnalyseUtils=new DataAnalyseUtils(this.getFrameconn(),this.userView);;
		HashMap kqItem_hash=dataAnalyseUtils.count_Leave();    	
		DataProcedureAnalyse dataProcedureAnalyse=new DataProcedureAnalyse(this.getFrameconn(),this.userView,analyseType,kq_type,kq_cardno,kq_Gno,dataUpdateType,kq_dbase_list);
		dataProcedureAnalyse.setPick_flag("1");
		if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
		{
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String nbase=(String)kq_dbase_list.get(i);
				String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);		
				dataProcedureAnalyse.updateDataToQ03(temp_Table,kqItem_hash,start_date,end_date,nbase,whereIN,"","");
			}
		}
	}
    /**
	 * 数据处理：0：分用户处理 1：集中处理
	 * @return
	 */
	public String getDataprocessing()
	{
		String data="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet= null;
		StringBuffer sql = new StringBuffer();
		sql.append("select content,status from kq_parameter where ");
		sql.append("name='DATA_PROCESSING' and b0110='UN'");
		try
		{
			rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				data=rowSet.getString("content");
			}
			data=data!=null&&data.length()>0?data:"0";
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return data;
	}
	 private String getB0110ForA0100(String nbase,String a0100)
	 {
			String b0110="";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs=null;
			try
			{
				String sql="select b0110 from "+nbase+"A01 where a0100='"+a0100+"'";
				rs=dao.search(sql);
				if(rs.next())
				{
				 b0110=rs.getString("b0110");				
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
			return b0110;
	}

}
