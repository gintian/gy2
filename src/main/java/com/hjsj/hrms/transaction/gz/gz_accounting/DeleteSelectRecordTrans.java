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
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 *<p>Title:DeleteSelectRecordTrans</p> 
 *<p>Description:删除选中的记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-21:下午05:28:30</p> 
 *@author cmq
 *@version 4.0
 */
public class DeleteSelectRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("gz_table_table");
		int index=(name.lastIndexOf("_")+1);
    	int salaryid=Integer.parseInt(name.substring(index));
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("gz_table_record");
		try
		{
			 
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(String.valueOf(salaryid),null);
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			boolean flag=false;
			 
		    if(list!=null&&list.size()>0)
		    {
		    	SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),salaryid);
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		    	
				StringBuffer where_sub=new StringBuffer("");
				HashSet dateSet=new HashSet();
				Calendar dd=Calendar.getInstance();
		
		 		Calendar a00z2=Calendar.getInstance();
		 		int      a00z3=0;
				
				String _a0100="";
				String _nbase="";
				Calendar _a00z0=Calendar.getInstance();
				int _a00z1=0;
				
		    	for(int i=0;i<list.size();i++)
		    	{
		    		RecordVo vo=(RecordVo)list.get(i);
		//    		a00z2.setTime(vo.getDate("a00z2"));
		//    		a00z3=vo.getInt("a00z3");
		    		
		    		_a0100=vo.getString("a0100");
		    		_nbase=vo.getString("nbase");
		    		_a00z0.setTime(vo.getDate("a00z0"));
		    		_a00z1=vo.getInt("a00z1");
		    		
		    		String d=vo.getString("sp_flag");
		    		if(vo.getString("sp_flag").trim().length()>0&&!"01".equals(vo.getString("sp_flag"))&&!"07".equals(vo.getString("sp_flag")))
		    		{
		    			flag=true;
		    			break;
		    		}
//		    		共享薪资类别，其他操作人员引入数据 
					if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
					{
						if(vo.getString("sp_flag2").trim().length()>0&&!"01".equals(vo.getString("sp_flag2"))&&!"07".equals(vo.getString("sp_flag2")))
			    		{
			    			flag=true;
			    			break;
			    		}
					}
					
					if("07".equals(vo.getString("sp_flag")))
					{
						dd.setTime(vo.getDate("a00z0"));
						where_sub.append(" or ( "+Sql_switcher.year("A00Z0")+"="+dd.get(Calendar.YEAR));
						where_sub.append(" and "+Sql_switcher.month("A00Z0")+"="+(dd.get(Calendar.MONTH)+1));
						where_sub.append(" and A00Z1="+vo.getInt("a00z1"));
						where_sub.append(" and lower(nbase)='"+vo.getString("nbase").toLowerCase()+"' ");
						where_sub.append(" and a0100='"+vo.getString("a0100")+"' ) ");
						
						dateSet.add(dd.get(Calendar.YEAR)+"-"+(dd.get(Calendar.MONTH)+1)+"-"+vo.getInt("a00z1"));
					}
		    		
		    	}
		    	if(flag)
		    	{
		    		throw GeneralExceptionHandler.Handle(new Exception("只能删除起草或驳回的记录！"));
		    	}
		    	
		    	 
		    	String _sql="select * from "+name+" where lower(nbase)='"+_nbase.toLowerCase()+"' and a0100='"+_a0100+"'  and "+Sql_switcher.year("a00z0")+"="+_a00z0.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z0")+"="+(_a00z0.get(Calendar.MONTH)+1)+" and a00z1="+_a00z1;
		    	this.frowset=dao.search(_sql);
		    	if(this.frowset.next())
		    	{
		    		a00z2.setTime(this.frowset.getDate("a00z2"));
		    	    a00z3=this.frowset.getInt("a00z3");
		    	}
		    	
		    	
		    	
		    	
		    	//同步薪资发放数据的映射表
		    	String sql="delete from salary_mapping where salaryid=? and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?";
		    	if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
		    		sql+=" and lower(userflag)='"+manager.toLowerCase()+"' ";
		    	else
		    		sql+=" and  lower(userflag)='"+this.userView.getUserName().toLowerCase()+"'";
		    	try(PreparedStatement ps=this.getFrameconn().prepareStatement(sql)) {
					for (int i = 0; i < list.size(); i++) {
						RecordVo vo = (RecordVo) list.get(i);
						Calendar d = Calendar.getInstance();
						d.setTime(vo.getDate("a00z0"));
						ps.setInt(1, salaryid);
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
					// 关闭Wallet
					dbS.close(this.getFrameconn());
				}
		    	
		    	/** 总额计算  */
				ArrayList dateList=new ArrayList();
				SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),String.valueOf(salaryid));
				StringBuffer where=new StringBuffer("");
				if(where_sub.length()>0)
					where.append("  and ( "+where_sub.substring(3)+" ) ");
				String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
				if("1".equals(isControl))
				{
					dateList=bo.getDateList(where.toString(),dateSet,false);
				}
		    	
		     	dao.deleteValueObject(list);
		    	//删除税率明细中的记录
		     	DbWizard dbw=new DbWizard(this.getFrameconn());
				if(dbw.isExistTable("gz_tax_mx", false))
				{	
					if(!dbw.isExistField("gz_tax_mx", "UserFlag"))
					{
						Table tbl=new Table("gz_tax_mx");
						Field field=new Field("UserFlag","UserFlag");
						field.setDatatype(DataType.STRING);
						field.setLength(50);
						tbl.addField(field);
						dbw.addColumns(tbl);
					}
					
			    	sql="delete from gz_tax_mx where salaryid=? and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?";
			    	if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
			    		sql+=" and ( lower(userflag)='"+manager.toLowerCase()+"' or userflag is null )";
			    	else
			    		sql+=" and ( lower(userflag)='"+this.userView.getUserName().toLowerCase()+"' or userflag is null )";
			    	
			    	try(PreparedStatement ps=this.getFrameconn().prepareStatement(sql)) {
						for (int i = 0; i < list.size(); i++) {
							RecordVo vo = (RecordVo) list.get(i);
							Calendar d = Calendar.getInstance();
							d.setTime(vo.getDate("a00z0"));
							ps.setInt(1, salaryid);
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
						// 关闭Wallet
						dbS.close(this.getFrameconn());
					}
			    	
			    	sql="delete from salaryhistory where salaryid=? and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?";
			    	if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
			    		sql+=" and lower(userflag)='"+manager.toLowerCase()+"' ";
			    	else
			    		sql+=" and  lower(userflag)='"+this.userView.getUserName().toLowerCase()+"'";
			    	sql+=" and sp_flag<>'06'";
					try(PreparedStatement ps=this.getFrameconn().prepareStatement(sql)) {
						for (int i = 0; i < list.size(); i++) {
							RecordVo vo = (RecordVo) list.get(i);
							Calendar d = Calendar.getInstance();
							d.setTime(vo.getDate("a00z0"));
							ps.setInt(1, salaryid);
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
						// 关闭Wallet
						dbS.close(this.getFrameconn());
					}
				}
				bo.calculateTotalSum(dateList);

				//删除记录后判断临时表中数据是否全为已批准，如是需刷新页面出现确认按钮
				/*String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
				if(flow_flag.equalsIgnoreCase("1"))
				{
					this.frowset=dao.search("select count(a0100) from "+name+" where sp_flag<>'03' or sp_flag is null");
					if(this.frowset.next())
					{
						int count=this.frowset.getInt(1);
						if(count==0)
						{
							//throw GeneralExceptionHandler.Handle(new Exception("flushPage"));
						}
					}
				}*/
				String tableName=this.userView.getUserName()+"_salary_"+salaryid;
				String username=this.userView.getUserName();
				if(manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
				{
					tableName=manager+"_salary_"+salaryid;
					username=manager;
				} 
				
				 
				boolean temp=bo.isAllEnd(tableName);//判断当前薪资发放是否全结束
				if(temp){
					_sql="update gz_extend_log set sp_flag='06' where SalaryID='"+salaryid+"' and lower(username)='"+username.toLowerCase()+"'";
					_sql+=" and "+Sql_switcher.year("A00Z2")+"="+a00z2.get(Calendar.YEAR)+" and "+Sql_switcher.month("A00Z2")+"="+(a00z2.get(Calendar.MONTH)+1);
					_sql+=" and a00z3="+a00z3;
					dao.update(_sql);
				}
				//-----------------------------浙江交投  薪资审批推送待办表   zhaoxg add 2014-8-7---------------------------------
				SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");// 设置日期格式
				String date = df.format(a00z2.getTime());
				String[] _date=date.split("\\.");
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),salaryid,this.userView);
				LazyDynaBean bean=gzbo.getSalaryName(this.getFrameconn(), salaryid+"");
				bean.set("year", _date[0]);//年
				bean.set("month", _date[1]);//月 
				bean.set("count", a00z3+"");//次数
				bean.set("a00z2", date);
				bean.set("sql", "select * from "+tableName+" where A00Z2="+Sql_switcher.dateValue((String) bean.get("a00z2"))+" and A00Z3='"+bean.get("count")+"' and (sp_flag='01' or sp_flag='07')");
				LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid+"",bean,"5");//1:报批  2：驳回  3：批准  4：阅读 5:不走前四个的标记（只修改自己的）
				PendingTask pt = new PendingTask();				
				if("update".equals(_bean.get("selfflag"))){
					pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
				}
				//-----------------------------------------------------------------------------------------------------------
		    }
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
