package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.FunctionRecruitBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 *	2017-4-15 V1
 *	招聘操作日志、及意见记录
 */
public class OperationLogTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		try {
			String select = (String)this.formHM.get("select");//查询日志标识
			String a0100_objects = (String)this.formHM.get("a0100");
			String position_id = (String)this.formHM.get("z0301");
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String nbase_object="";  //应聘人员库
			if(vo!=null)
				nbase_object=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
			if("select".equals(select)){//候选人简历查看日志信息
				ArrayList searchLog = this.searchLog(a0100_objects, nbase_object, position_id);
				this.formHM.put("searchLog", searchLog);
			}else{//保存日志信息
				String link_id = (String)this.formHM.get("link_id");
				String status = (String)this.formHM.get("node_id");
				String function_str = (String)this.formHM.get("function_str");
				String now_linkId = (String)this.formHM.get("now_linkId");
				String description = (String)this.formHM.get("description");
				String email = (String)this.formHM.get("email");
				HashMap<String, String> info = new HashMap<String, String>();
				info.put("a0100_objects",a0100_objects);
				info.put("nbase_object",nbase_object);
				info.put("link_id",link_id);
				info.put("status",status);
				info.put("position_id",position_id);
				info.put("function_str",function_str);
				info.put("now_linkId",now_linkId);
				info.put("description",description==null?"":description);
				info.put("email",email);
				this.addLog(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * key
	 * @param a0100_objects 候选人id
	 * @param nbase 应聘人员库
	 * @param position_id 招聘职位
	 * @param log_info 操作内容
	 * @param status 当前环节，接受、拒绝职位申请 值1、0
	 * @param now_linkId 当前环节
	 * @param description 操作意见
	 * @throws GeneralException 
	 */
	private void addLog(HashMap<String, String> info) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet search = null;
		try {
			String a0100_objects = info.get("a0100_objects");
			String nbase_object = info.get("nbase_object");
			String position_ids = info.get("position_id");
			String status = info.get("status");
			String link_id = info.get("link_id");
			String description = info.get("description");
			String function_str = info.get("function_str");
			String now_linkId = info.get("now_linkId");
			String dbname = this.userView.getDbname();
			String a0100 = this.userView.getA0100();
			String create_user = this.userView.getUserName();
			String create_fullname = this.userView.getUserFullName();
			Date date = new Date();
			Timestamp create_time = new Timestamp(date.getTime());
			String log_info = "";
			if("acceptPositionApply".equals(function_str)){
				log_info = ",'接受了职位申请'";
				status = "1";
			}else if("rejectPositionApply".equals(function_str)){
				log_info = ",'拒绝了职位申请'";
				status = "2";
			}else if("Global.recommendOtherPosition".equals(function_str)){
					log_info = ",'推荐职位'";
			}else{
				String node_id = status;
				ArrayList value = new ArrayList();
				if(StringUtils.isNotEmpty(now_linkId))
					value.add(now_linkId);
				else
					value.add(link_id);
				search = dao.search("select custom_name from zp_flow_links where id=?",value);
				if(search.next())
					log_info = ",'"+search.getString("custom_name");
				
				if("toStage".equals(function_str)){//转环节
					value.clear();
					value.add(link_id);
					search = dao.search("select custom_name from zp_flow_links where id=?",value);
					if(search.next())
						log_info += "：将候选人转到“"+search.getString("custom_name")+"”环节'";
				}else{//通过，淘汰，备选等
					value.clear();
					value.add(link_id);
					value.add(function_str);
					search = dao.search("select custom_name,sys_name from zp_flow_functions where link_id=? and function_str=?",value);
					if(search.next()) {
						if(StringUtils.isNotEmpty(search.getString("custom_name")))
							log_info += "：执行了“"+search.getString("custom_name")+"”操作";
						else
							log_info += "：执行了“"+search.getString("sys_name")+"”操作";
						
					}
					
					if("changeStatus".equals(function_str)){//变更状态
						value.clear();
						value.add(link_id);
						value.add(status);
						search = dao.search("select custom_name from zp_flow_status where link_id=? and status=?",value);
						if(search.next())
							log_info =log_info+",候选人进入“"+search.getString("custom_name")+"”状态";
					}
					log_info +="'";
				}
				if(!"changeStatus".equals(function_str)){
					FunctionRecruitBo bo = new FunctionRecruitBo(frameconn, userView);
					ArrayList list = bo.functionList();
					LazyDynaBean bean = new LazyDynaBean();
					for(int i=0;i<list.size();i++)
					{
						bean = (LazyDynaBean)list.get(i);
						//得到当前方法需改变的值
						String stage_id = (String)bean.get("node_id");
						if(stage_id.equals(node_id))
						{
							String returnId = (String)bean.get(function_str);
							if("sendnotice".equals(function_str))
								returnId = "";
							status = stage_id+returnId;
						}
					}
				}
				
			}
			
			String[] a0100_objs = a0100_objects.split(",");
			StringBuffer sql = new StringBuffer("insert into zp_opt_history ");
			sql.append(" (id,nbase_object,a0100_object,nbase,a0100,create_user,create_fullname,create_time,position_id,link_id,curr_link_id,status,description,a0101,log_info) ");
			sql.append(" values (");
			sql.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?");
			sql.append(log_info); //日志详细信息
			sql.append(")");
			
			String[] positions = position_ids.split(",");
			//将日志插入招聘日志表
			if(!"Global.recommendOtherPosition".equals(function_str)){
				String position_id = PubFunc.decrypt(positions[0]);
				for (int i = 0;i<a0100_objs.length;i++) {//批量插入日志信息
					ArrayList values = new ArrayList();
					String a0100_object = PubFunc.decrypt(a0100_objs[i]);
					if("acceptPositionApply".equals(function_str)||"rejectPositionApply".equals(function_str))
						position_id = PubFunc.decrypt(positions[i]);
					values = getValues(nbase_object, status, link_id, description,
							function_str, now_linkId, dbname, a0100, create_user,
							create_fullname, create_time, position_id,a0100_object);
					
					dao.update(sql.toString(),values);
				}
			}else{
				PositionBo bo = new PositionBo(frameconn, dao, userView);
				for (String z0301 : positions) {
					z0301 = PubFunc.decrypt(z0301);
					LazyDynaBean statusBean = bo.getFirstStatusByZ0301(z0301);
					if(statusBean==null)//排除流程第一个环节没有已启用的可用状态
						throw GeneralExceptionHandler.Handle(new Exception("该职位对应的招聘流程第一个环节无已启用的状态"));
					
					for (String a0100_object : a0100_objs) {
						a0100_object = PubFunc.decrypt(a0100_object.split("`")[0]);
						ArrayList values = getValues(nbase_object, statusBean.get("status").toString(), statusBean.get("link_id").toString(), description,
								function_str, now_linkId, dbname, a0100, create_user,
								create_fullname, create_time, z0301,a0100_object);
						
						dao.update(sql.toString(),values);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


	private ArrayList getValues(String nbase_object, String status, String link_id,
			String description, String function_str, String now_linkId,
			String dbname, String a0100, String create_user,
			String create_fullname, Timestamp create_time, String position_id, String a0100_object) throws GeneralException {
		ArrayList values = new ArrayList();
		IDGenerator idg = new IDGenerator(2, this.frameconn);
		String id=idg.getId("zp_opt_history.id");
		values.add(id);
		values.add(nbase_object);
		values.add(a0100_object);
		values.add(dbname);
		values.add(a0100);
		values.add(create_user);
		values.add(create_fullname);
		values.add(create_time);
		values.add(position_id);
		values.add(link_id);
		if(StringUtils.isNotEmpty(now_linkId))
			values.add(now_linkId);
		else
			values.add(link_id);
		values.add(status);
		values.add(description);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet search = null;
		try {
			ArrayList value = new ArrayList();
			value.add(a0100_object);
			search = dao.search("select a0101 from "+nbase_object+"A01 where a0100=?",value);
			if(search.next())
				values.add(search.getString("a0101"));
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return values;
	}
	
	/**
	 * @param a0100 候选人id
	 * @param nbase 应聘人员库
	 * @param Position_id 职位id
	 * @return
	 * @throws GeneralException 
	 */
	private ArrayList searchLog(String a0100,String nbase,String Position_id) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		Position_id = PubFunc.decrypt(SafeCode.decode(Position_id));
		RowSet search = null;
		ArrayList list = new ArrayList();
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			StringBuffer sql = new StringBuffer("select create_fullname,"+Sql_switcher.dateToChar("Create_time", "YYYY-MM-DD HH24:MM:SS")+" Create_time,Log_info,Description from zp_opt_history ");
			sql.append(" where Position_id=? and A0100_object=? and nbase_object=? order by id");
			ArrayList<String> value = new ArrayList<String>();
			value.add(Position_id);
			value.add(a0100);
			value.add(nbase);
			search = dao.search(sql.toString(),value);
			String[] log_info = new String[2];
			while (search.next()){
				map = new HashMap<String, String>();
				map.put("create_fullname", search.getString("create_fullname"));
				map.put("Create_time", search.getString("Create_time"));
				log_info = search.getString("Log_info").split("：");
				map.put("link_name", log_info[0]);
				map.put("Log_info", log_info.length==2?log_info[1]:"");
				map.put("Description", search.getString("Description"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(search);
		}
		return list;
	}

}
