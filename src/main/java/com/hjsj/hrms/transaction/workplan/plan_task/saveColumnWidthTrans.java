package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 工作计划 任务列表自定义列宽后自动保存
 * @createtime July 27, 2015 9:07:55 PM
 * @author chent
 *
 */
public class saveColumnWidthTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			String submoduleid = (String)this.getFormHM().get("submoduleid");
			String userName = this.userView.getUserName();
			
			PlanTaskTreeTableBo planBo = new PlanTaskTreeTableBo(this.getFrameconn());
			boolean recordPrivate = planBo.hasPrivateScheme(submoduleid, userName);// 私有记录
			if(!recordPrivate){// 没有私有记录
				boolean recordShare = planBo.hasShareScheme(submoduleid);// 公有记录
				if(!recordShare){// 没有公有记录，插入默认方案
					
					/** 插入默认方案到t_sys_table_scheme */ 
					// 获取最大num号
					String num = "";
					String sql = "select "+Sql_switcher.isnull("MAX(scheme_id)","0")+"+1 as num from t_sys_table_scheme";
					rs = dao.search(sql);
					while(rs.next()){
						num = rs.getString("num");
					}
					sql = "INSERT INTO t_sys_table_scheme (scheme_id,submoduleid,username,is_share,rows_per_page) VALUES(?,?,?,?,?)";
					ArrayList<String> list = new ArrayList<String>(Arrays.asList(num, "workPlan_position_0001", "su", "1", "20"));//公有方案保存成su的名字
					dao.insert(sql, list);
					
					/** 插入默认方案到t_sys_table_scheme_item */ 
					// 批量插入
					sql = "INSERT INTO t_sys_table_scheme_item (scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_fromdict,is_removable) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					ArrayList<ArrayList<String>> batchList = new ArrayList<ArrayList<String>>();
					ArrayList<String> tmp = createArrayList(num,"gantt","4","任务起止时间-甘特图","0","100","1","0","0","任务起止时间-甘特图","","0","0","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0801","0","任务名称","1","400","1","0","0","任务名称","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0803","5","任务描述1","0","100","1","0","0","任务描述1","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0817","6","计划工时","0","100","3","0","0","计划工时","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0821","7","任务类型","0","100","1","0","0","任务类型","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0823","8","任务分类","0","100","1","0","0","任务分类","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0825","9","任务来源","0","100","3","0","0","任务来源","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0835","10","完成进度","0","100","3","0","0","完成进度","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0837","11","进度说明1","0","100","1","0","0","进度说明1","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0839","13","协作部门","0","100","1","0","0","协作部门","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p0843","12","岗位","0","100","1","0","0","岗位","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p08z8","15","其他信息2","0","100","3","0","0","其他信息2","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"p08z9","14","其他信息","0","100","1","0","0","其他信息","","0","1","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"principal","1","负责人","1","200","1","0","0","负责人","","0","0","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"rank","2","权重","1","200","1","0","0","权重","","0","0","0");
					batchList.add(tmp);
					tmp = createArrayList(num,"timearrange","3","时间安排","1","220","1","0","0","时间安排","","0","0","0");
					batchList.add(tmp);
					dao.batchInsert(sql, batchList);
					recordShare = true;
				}
				if(recordShare){
					// 插入栏目设置表，把已有的公有方案拷贝成私有方案，最大id+1
					StringBuilder buf = new StringBuilder();
					buf.append("insert into t_sys_table_scheme (scheme_id,submoduleid,username,is_share,rows_per_page)");
					buf.append("(select (select MAX(scheme_id)+1 from t_sys_table_scheme),submoduleid,?,'0',rows_per_page");
					buf.append(" from t_sys_table_scheme");
					buf.append(" where submoduleid = ? and is_share = '1')");
					ArrayList<String> list = new ArrayList<String>();
					list.add(userName);
					list.add(submoduleid);
					dao.insert(buf.toString(), list);
					// 获得拷贝的公有方案id
					String sql = "select scheme_id from t_sys_table_scheme where submoduleid=? and is_share=1";
					list.clear();
					list.add(submoduleid);
					rs = dao.search(sql, list);
					String copy_scheme_id = "";
					while(rs.next()){
						copy_scheme_id = rs.getString("scheme_id");
					}
					// 获得拷贝后的私有方案id
					sql = "select scheme_id from t_sys_table_scheme where submoduleid=? and username=? and is_share=0";
					list.clear();
					list.add(submoduleid);
					list.add(userName);
					rs = dao.search(sql, list);
					String scheme_id = "";
					while(rs.next()){
						scheme_id = rs.getString("scheme_id");
					}
					// 插入栏目设置item表，查询出拷贝的id，把id改成私有方案id，插回栏目设置表
					if(!StringUtils.isEmpty(copy_scheme_id) && !StringUtils.isEmpty(scheme_id)){
						buf.setLength(0);
						buf.append("insert into t_sys_table_scheme_item (scheme_id,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_fromdict,is_removable)");
						buf.append("(select ?,itemid,displayorder,displaydesc,is_display,displaywidth,align,is_order,is_sum,itemdesc,mergedesc,is_lock,is_fromdict,is_removable");
						buf.append(" from t_sys_table_scheme_item");
						buf.append(" where scheme_id = ?)");
						list.clear();
						list.add(scheme_id);
						list.add(copy_scheme_id);
						dao.insert(buf.toString(), list);
					}
					
				}
			}
			
			String dataIndex = (String)this.getFormHM().get("dataIndex");
			String newWidth = (String)this.getFormHM().get("newWidth");
			String is_share = (String)this.getFormHM().get("is_share");
			
			String sql = "update t_sys_table_scheme_item set displaywidth=? where itemid=? and scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid=? and username=? and is_share=?)";
			ArrayList<String> list = new ArrayList<String>();
			list.add(newWidth);
			list.add(dataIndex);
			list.add(submoduleid);
			list.add(userName);
			list.add(is_share);
			dao.update(sql, list);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}
	private static ArrayList<String> createArrayList(String ... elements) {
		  ArrayList<String> list = new ArrayList<String>(); 
		  for (String element : elements) {
		    list.add(element);
		  }
		  return list;
	}
}
