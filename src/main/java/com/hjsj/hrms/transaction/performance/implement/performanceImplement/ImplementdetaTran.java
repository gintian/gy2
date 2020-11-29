package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:清楚分数</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 14, 2009:4:45:48 PM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ImplementdetaTran extends IBusiness
{

	public void execute() throws GeneralException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String planid = (String) hm.get("planid");
			String delids = (String) this.getFormHM().get("paramStr");
			if (delids != null)
			{
				String[] mainbodyids = delids.split(",");
				HashMap map = new HashMap();
				for(int i=0;i<mainbodyids.length;i++)
				{
					map.put(mainbodyids[i], "");
				}
				
				PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
				String whl = pb.getPrivWhere(this.userView);//根据用户权限先得到一个考核对象的范围
				
				ArrayList list = new ArrayList();
				ArrayList obj_list=new ArrayList();
				StringBuffer sql = new StringBuffer();
				sql.append("select  object_id,mainbody_id from per_mainbody where plan_id=" + planid);
				sql.append(" and object_id in (");
				sql.append("select object_id from per_object where plan_id="+planid);
				sql.append(whl);
				sql.append(")");
				
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next())
				{
					String mainbody_id = this.frowset.getString(2);
					String object_id =  this.frowset.getString(1);
					if(map.get(mainbody_id)==null)
						continue;
					ArrayList temList=new ArrayList();
					temList.add(object_id);
					obj_list.add(temList);
					ArrayList temp = new ArrayList();
					temp.add(object_id);
					temp.add(mainbody_id);
					list.add(temp);
					// 清除分数时，同时删除待办中“待办处理状态（Pending_status）”<>1的评分待办。Pending_status：0：待办， 1：已办  3：已阅 4：无效 chent 20170721 start
					String Ext_flag="PERPF_"+planid;
	    			String str = "select pending_id from t_hr_pendingtask  where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%' and Receiver='Usr"+mainbody_id+"'";
	    			RowSet rs = dao.search(str);
	    			while(rs.next()){
	    				PendingTask pe = new PendingTask();
	    				pe.deletePending("P", "PER"+rs.getString("pending_id"), 1, "清除分数");
	    			}
					String sql1="delete from t_hr_pendingtask where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%' and Receiver='Usr"+mainbody_id+"'";
					dao.update(sql1);
					// 清除分数时，同时删除待办中“待办处理状态（Pending_status）”<>1的评分待办。Pending_status：0：待办， 1：已办  3：已阅 4：无效 chent 20170721 end
				}
				StringBuffer buf = new StringBuffer();
				buf.setLength(0);
//				buf.append(" delete from per_result_"+planid);清除分数不能把结果表中的记录删掉
//				dao.delete(buf.toString(), new ArrayList());
				String search_sql="select * from per_result_"+planid+" where 1=2";
				String update_sql="";
				this.frowset=dao.search(search_sql);
				ResultSetMetaData rsmd =this.frowset.getMetaData();
				int colunmCount=rsmd.getColumnCount();
				String colunmName="";
				for(int i=1;i<colunmCount;i++){
					colunmName=rsmd.getColumnName(i);
					if("id".equalsIgnoreCase(colunmName)||"b0110".equalsIgnoreCase(colunmName)||"e0122".equalsIgnoreCase(colunmName)
					    ||"e01a1".equalsIgnoreCase(colunmName)||"object_id".equalsIgnoreCase(colunmName)||"a0101".equalsIgnoreCase(colunmName)
					    ||"a0000".equalsIgnoreCase(colunmName)||"body_id".equalsIgnoreCase(colunmName)){
						continue;
					}
					update_sql+=colunmName+"=null,";
				}
				if(update_sql.length()>0)
					update_sql=update_sql.substring(0, update_sql.length()-1);
				buf.append("update  per_result_"+planid+" set "+update_sql+" where object_id=? ");
				dao.batchUpdate(buf.toString(), obj_list);
				
				if("2".equals(pb.getPlanVo(planid).getString("method")))
				{
					buf.setLength(0);
					buf.append("delete from per_target_evaluation where plan_id="+planid);
					buf.append(" and object_id=? and mainbody_id=?");
					dao.batchUpdate(buf.toString(), list);
				}else
				{
					String tablename = "per_table_" + planid;
					buf.setLength(0);
					buf.append("delete from "+tablename+" where ");
					buf.append("object_id=? and mainbody_id=?");
					dao.batchUpdate(buf.toString(), list);
				}		
				
				buf.setLength(0);
				buf.append("update per_mainbody set status=0,know_id=null,whole_grade_id=null,description=null,sub_date=null,score=null,whole_score=0 where plan_id="+planid+" and ");
				buf.append("object_id=? and mainbody_id=?");
				dao.batchUpdate(buf.toString(), list);
				// dao.delete("delete from "+tablename+" where mainbody_id in("+delids+")", new ArrayList());
				// dao.update("update per_mainbody set status=0,know_id=null,whole_grade_id=null where plan_id='"+planid+"' and mainbody_id in("+delids+")",new ArrayList());

			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
