/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:DeleteSpSelectRecordTrans</p> 
 *<p>Description:删除薪资审批选中的记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-22:上午10:06:44</p> 
 *@author cmq
 *@version 4.0
 */
public class DeleteSpSelectRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("gz_sptable_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("gz_sptable_record");	
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			
		 	ArrayList tempList=new ArrayList();
			HashMap  salaryMap=new HashMap();
			HashSet  keySet=new HashSet();
			HashSet  userSet=new HashSet();
			
			ArrayList list0=new ArrayList();
			
			String tableName=getTableName(list);
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				if("06".equals(vo.getString("sp_flag")))
					throw GeneralExceptionHandler.Handle(new Exception("不能删除审批结束的记录！"));
			
			}
			String salaryid=tableName.split("_salary_")[1];
			 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			
			StringBuffer where_sub=new StringBuffer("");
			Calendar d=Calendar.getInstance();
			
			HashSet dateSet=new HashSet();
			Calendar a00z2=Calendar.getInstance();
			int      a00z3=0;
			
			
			String _a0100="";
			String _nbase="";
			Calendar _a00z0=Calendar.getInstance();
			int _a00z1=0;
			
			
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				if("06".equals(vo.getString("sp_flag")))
					continue; 
	    	//	a00z2.setTime(vo.getDate("a00z2"));
	    	//	a00z3=vo.getInt("a00z3");
	    		
				_a0100=vo.getString("a0100");
	    		_nbase=vo.getString("nbase");
	    		_a00z0.setTime(vo.getDate("a00z0"));
	    		_a00z1=vo.getInt("a00z1");
	    		
				String userflag=vo.getString("userflag").toLowerCase();
				String table_name=userflag+"_salary_"+salaryid;
				
				if(salaryMap.get(table_name)!=null)
				{
					tempList=(ArrayList)salaryMap.get(table_name);
				}
				else
					tempList=new ArrayList();
				RecordVo newVo=new RecordVo(table_name);
				newVo.setDate("a00z0", vo.getDate("a00z0"));
				newVo.setInt("a00z1",vo.getInt("a00z1"));
				newVo.setString("nbase",vo.getString("nbase"));
				newVo.setString("a0100",vo.getString("a0100"));
				tempList.add(newVo);
				salaryMap.put(table_name, tempList);
				keySet.add(table_name);
				userSet.add(userflag);
				
				d.setTime(vo.getDate("a00z0"));
				where_sub.append(" or ( "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR));
				where_sub.append(" and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1));
				where_sub.append(" and A00Z1="+vo.getInt("a00z1"));
				where_sub.append(" and lower(nbase)='"+vo.getString("nbase").toLowerCase()+"' ");
				where_sub.append(" and a0100='"+vo.getString("a0100")+"' ) ");
				
				
				dateSet.add(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+vo.getInt("a00z1"));
				
			//	tempList.add(newVo);
				list0.add(vo);
			}
			
			
			String _sql="select * from "+name+" where  salaryid="+salaryid+" and lower(nbase)='"+_nbase.toLowerCase()+"' and a0100='"+_a0100+"'  and "+Sql_switcher.year("a00z0")+"="+_a00z0.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z0")+"="+(_a00z0.get(Calendar.MONTH)+1)+" and a00z1="+_a00z1;
	    	this.frowset=dao.search(_sql);
	    	if(this.frowset.next())
	    	{
	    		a00z2.setTime(this.frowset.getDate("a00z2"));
	    	    a00z3=this.frowset.getInt("a00z3");
	    	}
			
			
			
			/** 总额计算  */
			ArrayList dateList=new ArrayList();
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			StringBuffer where=new StringBuffer("");
			if(where_sub.length()>0)
				where.append(" and salaryid="+salaryid+" and ( "+where_sub.substring(3)+" ) ");
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				dateList=bo.getDateList(where.toString(),dateSet,true);
			}
				
			dao.deleteValueObject(list0);
			
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String table_name=(String)t.next();
				tempList=(ArrayList)salaryMap.get(table_name);
				dao.deleteValueObject(tempList);
				
				//同步薪资发放数据的映射表
		    	String sql="delete from salary_mapping where salaryid=? and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?";
	            sql+=" and lower(userflag)='"+table_name.split("_salary_")[0].toLowerCase()+"' ";
				try(PreparedStatement ps=this.getFrameconn().prepareStatement(sql)) {
					for (int i = 0; i < tempList.size(); i++) {
						RecordVo vo = (RecordVo) tempList.get(i);
						d = Calendar.getInstance();
						d.setTime(vo.getDate("a00z0"));
						ps.setInt(1, Integer.parseInt(salaryid));
						ps.setString(2, vo.getString("nbase").toLowerCase());
						ps.setString(3, vo.getString("a0100"));
						ps.setInt(4, d.get(Calendar.YEAR));
						ps.setInt(5, (d.get(Calendar.MONTH) + 1));
						ps.setInt(6, vo.getInt("a00z1"));
						ps.addBatch();
					}
					// 打开Wallet
					dbS.open(this.getFrameconn(), sql);
					ps.executeBatch();
				}
			}
			
			
			/** 总额计算  */
			bo.calculateTotalSum(dateList);
			
			if(userSet.size()>0)
			{ 
				for(Iterator t=userSet.iterator();t.hasNext();)
				{
					String userflag=(String)t.next(); 
					String _tablename=userflag+"_salary_"+salaryid;
					boolean temp=bo.isAllEnd(_tablename);//判断当前薪资发放是否全结束
					if(temp){
						_sql="update gz_extend_log set sp_flag='06' where SalaryID='"+salaryid+"' and lower(username)='"+userflag.toLowerCase()+"'";
						_sql+=" and "+Sql_switcher.year("A00Z2")+"="+a00z2.get(Calendar.YEAR)+" and "+Sql_switcher.month("A00Z2")+"="+(a00z2.get(Calendar.MONTH)+1);
						_sql+=" and a00z3="+a00z3;
						dao.update(_sql);
					}
					
				}
			}
			//-----------------------------浙江交投  薪资审批推送待办表   zhaoxg add 2014-8-7---------------------------------
			SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");// 设置日期格式
			String date = df.format(a00z2.getTime());
			String[] _date=date.split("\\.");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			LazyDynaBean bean=gzbo.getSalaryName(this.getFrameconn(), salaryid+"");
			bean.set("year", _date[0]);//年
			bean.set("month", _date[1]);//月 
			bean.set("count", a00z3+"");//次数
			bean.set("a00z2", date);
			bean.set("sql", "select *  from salaryhistory  where   salaryid="+salaryid+" and A00Z3='"+a00z3+"' and A00Z2="+Sql_switcher.dateValue(date)+" and curr_user='"+this.userView.getUserName()+"' ");
			LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"5");//1:报批  2：驳回  3：批准  4：阅读 5:不走前四个的标记（只修改自己的）
			PendingTask pt = new PendingTask();				
			if("update".equals(_bean.get("selfflag"))){
				pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
			}
			//-----------------------------------------------------------------------------------------------------------
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String getTableName(ArrayList list)
	{
		String tableName="";
		try
		{
			RecordVo vo=(RecordVo)list.get(0);
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			vo=dao.findByPrimaryKey(vo);
			tableName=vo.getString("userflag")+"_salary_"+vo.getInt("salaryid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return tableName;
	}
	
	
}
