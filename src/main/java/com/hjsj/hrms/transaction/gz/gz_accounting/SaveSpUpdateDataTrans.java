/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

/**
 *<p>Title:SaveSpUpdateDataTrans</p> 
 *<p>Description:保存薪资审批更新记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-22:上午10:08:23</p> 
 *@author cmq
 *@version 4.0
 */
public class SaveSpUpdateDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("gz_sptable_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("gz_sptable_record");			
		
		String bosdate=(String)hm.get("bosdate");
		String count=(String)hm.get("count");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn()); 
			RecordVo _vo=null;
			int _salaryid=((RecordVo)list.get(0)).getInt("salaryid");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),_salaryid,this.userView);
			//验证不在权限范围内的数据不允许修改  20140909 dengcan
			String sql="select a0100 from salaryhistory where salaryid=? and a00z1=? and a0100=? and nbase=? and "+Sql_switcher.year("A00Z0")+"=? and "+Sql_switcher.month("A00Z0")+"=? ";
			sql+=" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) ) ";
			for(int i=0;i<list.size();i++)
			{
				_vo=(RecordVo)list.get(i);
				String nbase=_vo.getString("nbase");
				Date a00z0=_vo.getDate("a00z0");
				int a00z1=_vo.getInt("a00z1");
				String a0100=_vo.getString("a0100");
				int salaryid=_vo.getInt("salaryid");
				ArrayList data_list=new ArrayList();
				data_list.add(new Integer(salaryid));
				data_list.add(new Integer(a00z1));
				data_list.add(a0100);
				data_list.add(nbase);
				Calendar cd=Calendar.getInstance();
				cd.setTime(a00z0);
				data_list.add(new Integer(cd.get(Calendar.YEAR)));
				data_list.add(new Integer(cd.get(Calendar.MONTH)+1));
				this.frowset=dao.search(sql,data_list);
				if(this.frowset.next())
				{
					
				}
				else
					throw GeneralExceptionHandler.Handle(new Exception("不允许修改权限范围外的数据"+"!"));
			}
		 
			
			
			
			
			
			
			for(int i=0;i<list.size();i++)
			{
				_vo=(RecordVo)list.get(i);
				HashMap values=_vo.getValues();  //2013-11-25 dengcan 修改数据时选了项目过滤条件，会造成其它没选的指标值被清空。
				if(values.get("appprocess")!=null)
				{
					String appprocess=_vo.getString("appprocess");
					appprocess=appprocess.replaceAll("\n\n","\n");
					_vo.setString("appprocess", appprocess);
				}
			}
			try
			{
				dao.updateValueObject(list);
			}
			catch(Exception ee)
			{
			 
				ee.printStackTrace();
				String message=ee.getMessage();
				if(message.indexOf("data is not corrected")!=-1)
					throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
				if(message.indexOf("转换为数据类型")!=-1)
					throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	 
			}
				
				
			SalaryCtrlParamBo ctrlparam=null;
			String manager="";
			
			if(list.size()>0)
			{
				RecordVo vo=(RecordVo)list.get(0);
				RecordVo a_vo=new RecordVo("salaryhistory");
				a_vo.setString("nbase", vo.getString("nbase"));
				a_vo.setDate("a00z0",vo.getDate("a00z0"));
				a_vo.setInt("a00z1",vo.getInt("a00z1"));
				a_vo.setString("a0100",vo.getString("a0100"));
				a_vo.setInt("salaryid",vo.getInt("salaryid"));
				
				if(ctrlparam==null)
				{
					ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),vo.getInt("salaryid"));
					manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
				}
				
				String userflag=a_vo.getString("userflag");
				if(manager!=null&&manager.trim().length()>0)
						userflag=manager;
				
				a_vo=dao.findByPrimaryKey(a_vo);				
				Date a00z2=a_vo.getDate("a00z2");
				int a00z3=a_vo.getInt("a00z3");
				//如果为已提交的工资,则不需要修改临时表
				if(!isSub(a00z2,a00z3,vo.getInt("salaryid"),userflag))
				{
					vo=dao.findByPrimaryKey(vo);
					String tableName=getTableName(vo);
					if(manager!=null&&manager.trim().length()>0)
						tableName=manager+"_salary_"+vo.getInt("salaryid");
					
					ArrayList filterList=null;
				//	ArrayList fieldlist=gzbo.getFieldlist();   //2013-11-25 dengcan 修改数据时选了项目过滤条件，会造成其它没选的指标值被清空。
				//	ArrayList filterList=getFilterList(fieldlist);
					updateVo(filterList,list,tableName);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	
	public boolean isSub(Date a00z2,int a00z3,int salaryid,String userName)
	{
		Calendar d=Calendar.getInstance();
		d.setTime(a00z2);
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select sp_flag from gz_extend_log where salaryid="+salaryid+" and a00z3="+a00z3+" and lower(username)='"+userName.toLowerCase()+"' and "+Sql_switcher.year("a00z2")+"="+d.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z2")+"="+(d.get(Calendar.MONTH)+1));
			if(this.frowset.next())
			{
				if("06".equals(this.frowset.getString(1)))
					flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	public void updateVo(ArrayList itemList,ArrayList dataList,String tableName)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList list=new ArrayList();
			Set set=null;
			
			String str=",nbase,a0100,a00z0,a00z1,a00z2,a00z3,salaryid,a0000,b0110,e0122,a0101,userflag,sp_flag,curr_user,appuser,appprocess";	  
			for(int i=0;i<dataList.size();i++)
			{
				RecordVo vo=(RecordVo)dataList.get(i);
				HashMap values=vo.getValues();    //2013-11-25 dengcan 修改数据时选了项目过滤条件，会造成其它没选的指标值被清空。
				
				
				RecordVo a_vo=new RecordVo(tableName);
				a_vo.setString("nbase", vo.getString("nbase"));
				a_vo.setDate("a00z0",vo.getDate("a00z0"));
				a_vo.setInt("a00z1",vo.getInt("a00z1"));
				a_vo.setString("a0100",vo.getString("a0100"));
				a_vo=dao.findByPrimaryKey(a_vo);
			//	for(int j=0;j<itemList.size();j++)
				set=values.keySet();
				for(Iterator t=set.iterator();t.hasNext();)
				{
					String itemid=((String)t.next()).toLowerCase();
					if(str.indexOf(","+itemid.toLowerCase())!=-1)
						continue;
					FieldItem fildItem=DataDictionary.getFieldItem(itemid);
					if(fildItem==null)
						continue;
					Field item=(Field)fildItem.cloneField();
			//		Field item=(Field)itemList.get(j);
			//		String itemid=item.getName().toLowerCase();
					if(a_vo.hasAttribute(itemid))
					{
						int itemtype=item.getDatatype();
						if(itemtype==DataType.CLOB||itemtype==DataType.STRING)
						{
							a_vo.setString(itemid,vo.getString(itemid));
						}
						else if(itemtype==DataType.DATE)
						{
							a_vo.setDate(itemid,vo.getDate(itemid));
						}
						else if(itemtype==DataType.INT)
						{
							a_vo.setInt(itemid,vo.getInt(itemid));
						}
						else if(itemtype==DataType.DOUBLE||itemtype==DataType.FLOAT)
						{
							a_vo.setDouble(itemid,vo.getDouble(itemid));
						}
					}
				}
				list.add(a_vo);
			}
			dao.updateValueObject(list);
			
		}
		catch(Exception e)
		{
		 
			e.printStackTrace();
			String message=e.getMessage();
			if(message.indexOf("data is not corrected")!=-1)
				throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
			if(message.indexOf("转换为数据类型")!=-1)
				throw GeneralExceptionHandler.Handle(new Exception("修改的数据超出最大长度限制!"));	
			 
		}
	}
	
	
	
	
	
	
	private String getTableName(RecordVo vo)
	{
		String tableName="";
		try
		{
			
			tableName=vo.getString("userflag")+"_salary_"+vo.getInt("salaryid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return tableName.toLowerCase();
	}
	
	private ArrayList getFilterList(ArrayList list)
	{
		ArrayList tempList=new ArrayList();
		String str=",nbase,a0100,a00z0,a00z1,a00z2,a00z3,salaryid,a0000,b0110,e0122,a0101,userflag,sp_flag,curr_user,appuser,appprocess";
		for(int i=0;i<list.size();i++)
		{
			Field item=(Field)list.get(i);
			if(item.isVisible())
			{
				String itemid=item.getName();
				if(str.indexOf(itemid.toLowerCase())==-1)
				{
					tempList.add(item);
				}
			}
		}
		return tempList;
	}

}
