package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.job.SysoutSyncThread;
import com.hjsj.hrms.businessobject.sys.sysout.SyncLADP;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class SendSysoutSyncTrans extends IBusiness {
	
	public void execute() throws GeneralException {	
		Connection conn=null;
		RowSet rs=null;
		
		// 系统标志
		String id="KAFKA";//默认修改KAFKA 20201016 wangcy
		
		// 人员、机构、岗位区分标志,1为人员，2为机构，3为岗位
		String emporg = (String)this.getFormHM().get("emporg");
		// 所选中的人员的unique_id
		String hruniqueids = (String)this.getFormHM().get("hruniqueids");
		
		// 所选中的机构的unique_id
		String orguniqueids = (String)this.getFormHM().get("orguniqueids");
		
		// 所选中的岗位的unique_id
		String postuniqueids = (String)this.getFormHM().get("postuniqueids");
       		
		try {
			conn = (Connection) AdminDb.getConnection();
			HrSyncBo hsb = new HrSyncBo(conn);
			String sync_mode=hsb.getAttributeValue(HrSyncBo.SYNC_MODE);
			if(sync_mode==null||!"trigger".equalsIgnoreCase(sync_mode))
				return;
			String hr_only_field=hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);//唯一标示
//			if(hr_only_field==null||hr_only_field.length()<=0)
//			{
//				Category.getInstance("com.hrms.frame.dao.ContentDAO").error("没有设置人员唯一指标");
//				return;
//			}
					
			//hr_only_field=hr_only_field!=null&&hr_only_field.length()>0?hr_only_field:"a0100";
			/** 判断人员同步 */
			String sync_A01 = hsb.getAttributeValue(HrSyncBo.SYNC_A01);
			sync_A01 = sync_A01 != null && sync_A01.trim().length() > 0 ? sync_A01: "0";			
			/** 判断单位同步 */
			String sync_B01 = hsb.getAttributeValue(HrSyncBo.SYNC_B01);
			sync_B01 = sync_B01 != null && sync_B01.trim().length() > 0 ? sync_B01: "0";	
			/** 判断岗位同步 */
			String sync_K01 = hsb.getAttributeValue(HrSyncBo.SYNC_K01);
			sync_K01 = sync_K01 != null && sync_K01.trim().length() > 0 ? sync_K01: "0";
			
			String fail_time=hsb.getAttributeValue(HrSyncBo.FAIL_LIMIT);
			StringBuffer sql=new StringBuffer();
			sql.append("select * from t_sys_outsync where state=1 and sys_id='"+id+"'");
			if(fail_time!=null&&fail_time.trim().length()>0)
				sql.append(" and fail_time<"+fail_time);
			String sync_data_addr=SystemConfig.getPropertyValue("hr_data_addr");
			String sync_data_post=SystemConfig.getPropertyValue("hr_data_post");
			String sync_user=SystemConfig.getPropertyValue("hr_sync_user");
			String sync_pass=SystemConfig.getPropertyValue("hr_sync_pass");
			String sync_base=SystemConfig.getPropertyValue("hr_data_base");
			String sync_baseType=SystemConfig.getPropertyValue("dbserver");
			String sync_emp_table=SystemConfig.getPropertyValue("sync_emp_table");
			String sync_org_table=SystemConfig.getPropertyValue("sync_org_table");
			String sync_post_table=SystemConfig.getPropertyValue("sync_post_table");
			
			DbWizard dbwd=new DbWizard(conn);
			boolean isSend = false;
			if(dbwd.isExistField("t_sys_outsync", "send", false))
				isSend = true;
			
			ContentDAO dao=new ContentDAO(conn);			
			rs=dao.search(sql.toString());	
			ArrayList list=new ArrayList();
			if(rs.next())
			{
				LazyDynaBean bean =new LazyDynaBean();
				bean.set("sys_id", rs.getString("sys_id"));
				bean.set("url", clearUrl_WSDL(rs.getString("url")));
				bean.set("sys_name", rs.getString("sys_name"));
				bean.set("sync_method", rs.getString("sync_method"));
				
				bean.set("emporg", emporg);
				bean.set("hruniqueids", hruniqueids);
				bean.set("orguniqueids", orguniqueids);
				bean.set("postuniqueids", postuniqueids);
				
				
				
				String tar = rs.getString("targetNamespace");
				if (tar == null) {
					tar = "";
				}
				bean.set("targetNamespace", tar);
				if(isSend){
					String other_param = rs.getString("other_param");
					if (other_param== null) {
						other_param = "";
					}
					String control = rs.getString("control");
					control = control == null ? "" : control;
					
					String send = rs.getString("send");
					send = send == null ? "" : send;
					bean.set("send",send);//new 是否发送
					bean.set("control", control);//new 那些类型需要发送
					bean.set("other_param", other_param);//new 发送的过滤条件
				}
				list.add(bean);
			} else {
				System.out.println("错误次数达到了允许范围的最大值，同步数据停止，请检查数据正确性！");
				throw new GeneralException("错误次数达到了允许范围的最大值，同步数据停止，请检查数据正确性！");
			}
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean =(LazyDynaBean)list.get(i);
				bean.set("sync_data_addr", sync_data_addr);
				bean.set("sync_data_post", sync_data_post);
				bean.set("sync_user", sync_user);
				bean.set("sync_pass", sync_pass);
				bean.set("sync_base", sync_base);
				bean.set("sync_baseType", sync_baseType);
				bean.set("org_table", sync_org_table);
				bean.set("emp_table", sync_emp_table);
				bean.set("post_table", sync_post_table);
				bean.set("sync_B01", sync_B01);
				bean.set("sync_A01", sync_A01);
				bean.set("sync_K01", sync_K01);
				
				if(isSend&&"0".equals(bean.get("send")))//new 是否发送
					continue;
				SysoutSyncThread syncthread=new SysoutSyncThread(bean,hr_only_field);//通过线程发送消息
				//Thread t = new Thread(syncthread);
				syncthread.run();
				System.out.println(syncthread.getOutinfo());
				if(syncthread.getOutinfo()!=null)
				   this.getFormHM().put("outinfo", SafeCode.encode(syncthread.getOutinfo()));
				else
					this.getFormHM().put("outinfo", "无调试信息");
			}	
			String isSyncLADP=SystemConfig.getPropertyValue("SyncLDAP");
			if(isSyncLADP!=null&& "true".equalsIgnoreCase(isSyncLADP))
			{
				//同步LADP
				SyncLADP syncLADP=new SyncLADP(hr_only_field);
				syncLADP.syncLadp();
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			 try
			   {
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
			   }
			   catch(Exception ex)
			   {
				   ex.printStackTrace();
			   }
		}
	}
	private String clearUrl_WSDL(String url)
	{
		String trim_url=url.trim();
		if(trim_url!=null&&trim_url.indexOf("?wsdl")!=-1)
		{
			url=trim_url.substring(0,trim_url.indexOf("?wsdl"));
		}
		return url;
	}
}
