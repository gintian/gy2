package com.hjsj.hrms.transaction.kq.options.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SearchKqMachineTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		 
		//sql.append("from kq_machine_location ");
		checkKqMachineTable();
		StringBuffer columns=new StringBuffer();
		columns.append("location_id,name,location,");
		columns.append("machine_no,description,port,ip_address,");
		columns.append("rule_id,baud_rate,type_id,inout_flag,card_len");  //card_len  考勤卡号长度
		StringBuffer sql=new StringBuffer();
		sql.append("select "+columns.toString());
		this.getFormHM().put("sqlstr",sql.toString());
		
		this.getFormHM().put("column",columns.toString());
		StringBuffer whereIS=new StringBuffer();
		String where="from kq_machine_location where 1=1 ";				
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select location_id from kq_machine_location");
			while(this.frowset.next())
			{
				if (userView.isHaveResource(IResourceConstant.KQ_MACH, frowset.getString("location_id")))
				{
					whereIS.append("'"+frowset.getString("location_id")+"',");
				}
			}
			if(whereIS!=null&&whereIS.toString().length()>0)
			{
				whereIS.setLength(whereIS.length()-1);
				where=where+" and location_id in("+whereIS.toString()+")";
			}else
			{
				if(!this.userView.isSuper_admin())
				{
					where=where+" and 1=2";
				}
			}                
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("where",where);
	}
	/**
	 * 判断考勤机数据表是否更新，没有更新则更新
	 * @throws GeneralException
	 */
	public void checkKqMachineTable()throws GeneralException
	{
		if(!checkInout_flag())
		{
			if(!ceaterInout_flagField())
				throw GeneralExceptionHandler.Handle(new GeneralException("","重构考勤机数据表错误","","")); 
			try
		    {
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel("kq_machine_location");
		    }catch(Exception e)
		    {
		    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
		    }
		}
	}
    private boolean checkInout_flag()
    {
    	boolean isCorrect=false;
    	StringBuffer sql=new StringBuffer();
		sql.append("select * from kq_machine_location where 1=2");
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			
			rs=dao.search(sql.toString());				
			ResultSetMetaData rm=rs.getMetaData();
			int column_count=rm.getColumnCount();
			for(int i=1;i<=column_count;i++)
			{
				String column_name=rm.getColumnName(i);
				if(column_name==null||column_name.length()<=0)
					column_name="";
				if("inout_flag".equalsIgnoreCase(column_name))
				{
					isCorrect=true;
					break;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return isCorrect;
    }
    private boolean ceaterInout_flagField()
	{
		boolean isCorrect=true;		
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		Table table=new Table("kq_machine_location");
		Field temp = new Field("inout_flag","出入标志");
		temp.setDatatype(DataType.INT);	
		temp.setKeyable(false);			
		temp.setVisible(false);
		table.addField(temp);
		try
		{
			dbWizard.addColumns(table);	
		}catch(Exception e)
		{
			e.printStackTrace();
			isCorrect=false;
		}
		return isCorrect;
	}
}
