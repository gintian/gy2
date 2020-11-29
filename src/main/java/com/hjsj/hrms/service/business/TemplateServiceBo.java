package com.hjsj.hrms.service.business;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * 
 *<p>Title:</p> 
 *<p>Description:向外部系统发送表单数据Bo类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:November  18, 2016</p> 
 *@author songjy
 *@version 1.0
 */
public class TemplateServiceBo {
	
	Category cat = Category.getInstance(this.getClass());
	
	
	private Connection con = null;
	
	public TemplateServiceBo()
	{
		
	}
	
	public TemplateServiceBo(Connection con)
	{
		this.con=con;
	}
	
	
	/**
	 * 单据发起时将内容发送至其它系统
	 * 
	 * @param master 发起人-userview
	 * @param tabid 表单号
	 * @param taskid 任务号
	 * @return
	 * 
	 * 
	 * 返回"true",表示成功
	 * 返回"false",表示失败
	 * 返回其他则表示为跳转url
	 */
	public String  sendTemplateDataToOths(UserView master,String tabid,String taskid)
	{
		String isSuccess="false";
		
		//通过发射动态创建发送表单数据
		String templateServiceClass =  SystemConfig.getPropertyValue("TemplateServiceClass");
		
		if(templateServiceClass==null||templateServiceClass.length()==0){
			//this.cat.debug("没有System.properties中配置子类templateServiceClass");
			return "true";
		}
		
		try {
			//查询表单数据
			ArrayList templateDatas=searchTemplet(tabid,taskid);
			ITemplateService templateServiceInstance = (ITemplateService)Class.forName(templateServiceClass).newInstance();
			isSuccess = templateServiceInstance.createProcessNavigateTo(master, taskid, tabid, templateDatas);
			
		} catch (InstantiationException e) {
			cat.error(this.getClass()+":InstantiationException"+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			cat.error(this.getClass()+":IllegalAccessException"+e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			cat.error(this.getClass()+templateServiceClass+":ClassNotFoundException:"+e.getMessage());
			e.printStackTrace();
		}catch(Exception e){
			cat.error(this.getClass()+":Exception:"+e.getMessage());
			e.printStackTrace();
		}
		
		
		return isSuccess;
	}
	
	 
	/**
	 * 外部系统流程结束后返回给HR的结果信息 仅支持自动流程
	 * @param taskid 任务号
	 * @param tabid 表单ID
	 * @param isSuccess true:提交入库 | false: 驳回
	 * @param opt 1:提交入库 2：驳回单据 3：单据终止
	 * @param failReason 驳回原因
	 */
	public boolean recieveTemplateBackInfo(String taskid,String opt,String failReason){
		

		boolean isOK=true;
		try
 	    {  
				String tabid=getIdByTask(taskid,this.con,1);
				String ins_id=getIdByTask(taskid,this.con,2);
				UserView userView=getUserViewByTask(taskid,this.con);//仅支持业务用户
				TemplateTableBo tablebo=new TemplateTableBo(this.con,Integer.parseInt(tabid),userView); 
		        /** 审批模式=0自动流转，=1手工指派 */
		        int sp_mode =tablebo.getSp_mode();
		        if(sp_mode==1)  //仅支持自动流程
		        	return false;
				tablebo.getTasklist().add(taskid);
				tablebo.setIns_id(Integer.parseInt(ins_id));
				RecordVo ins_vo=new RecordVo("t_wf_instance");
				ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
				WF_Instance ins=new WF_Instance(tablebo,this.con);
				ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),Integer.parseInt(taskid),3,tabid,userView,""));
				WF_Actor wf_actor=new WF_Actor(userView.getUserName(),"4");
				wf_actor.setBexchange(false);
				wf_actor.setActorname(userView.getUserFullName());	
				wf_actor.setContent("同意");  
				wf_actor.setSp_yj("01");//审批意见
				ins.setIns_id(Integer.parseInt(ins_id));
				ContentDAO dao=new ContentDAO(this.con);
				dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+taskid+" and tab_id="+tabid);
				if("2".equals(opt)) //驳回单据
				{
					wf_actor.setSp_yj("02");
					wf_actor.setContent(failReason);
					wf_actor.setBexchange(true);	  
				 
					String reject_type = tablebo.getReject_type();// =1 or null：逐级驳回 // =2：驳回到发起人
					if ("2".equalsIgnoreCase(reject_type)) {// 驳回到发起人
                        ins.rejectTaskToSponsor(ins_vo, wf_actor, Integer.parseInt(taskid),userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
                    } else {// 逐级驳回
                        ins.rejectTask(ins_vo, wf_actor, Integer.parseInt(taskid),userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
                    }
				}
				else if("3".equals(opt)) //单据终止
				{
					ins.processEnd(Integer.valueOf(taskid), Integer.valueOf(tabid), userView,1);
					
				}
				else //提交入库
					ins.createNextTask(ins_vo,wf_actor,Integer.parseInt(taskid),userView);
				
				 
			 
 	    } catch(Exception e) {
			e.printStackTrace();
			isOK=false;
		}  
		return isOK;
	}
	
	
	
	/**
	 * 根据 taskid 获得 处理人员的userView (只支持业务人员)
	 * @param task_id
	 * @param conn 
	 * @return
	 */
	private UserView getUserViewByTask(String task_id,Connection conn ) throws GeneralException
	{
		UserView userView=null; 
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select actorid,actor_type from t_wf_task where task_id="+task_id; 
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String actorid=rowSet.getString("actorid");
				String actor_type=rowSet.getString("actor_type"); //4 业务用户
				if("4".equals(actor_type))
				{
					userView=new UserView(actorid, conn); 
					if(!userView.canLogin())
						throw GeneralExceptionHandler.Handle(new Exception("审批用户登录不成功!"));	
				}
				else
					throw GeneralExceptionHandler.Handle(new Exception("审批用户不是业务用户!"));	
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("任务单据丢失!"));	
		}
		catch(Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);	
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		} 
		return userView;
	}
	
	/**
	 * 根据 taskid 获得 模板ID | ins_id
	 * @param task_id
	 * @param conn
	 * @param flag  1:模板id  2：ins_id
	 * @return
	 */
	private String getIdByTask(String task_id,Connection conn,int flag )
	{
		String id="";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select tabid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";
			if(flag==2) // 2：ins_id
				 sql="select ins_id from t_wf_task where task_id="+task_id;
			rowSet=dao.search(sql);
			if(rowSet.next())
				id=rowSet.getString(1);
		}
		catch(Exception e) {
				e.printStackTrace();
			
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		} 
		return id;
	}

	
	
	
	/**
	 * 
	 * @param master 发起人-userview
	 * @param tabid  表单号
	 * @param taskid 任务号
	 * @return
	 */
	private ArrayList<ArrayList<LazyDynaBean>> searchTemplet(String tabid,String taskid){
		
			tabid = tabid.replaceAll(" ", "");
			RowSet set = null;
			RowSet value_set=null;
			
			RowSet file_set = null;
			
			RowSet code_set = null;
			
			ArrayList<ArrayList<LazyDynaBean>> last_bean = new ArrayList<ArrayList<LazyDynaBean>>();
			
			ContentDAO dao = new ContentDAO(this.con);
			//1:mssql,2:oracle
			int dbtype = Sql_switcher.searchDbServer();
			//dbtype=2;
			String select_template_sql="";
			
			if(2==dbtype){
				select_template_sql="select  Field_hz field_name,Field_name field_key,ChgState field_state,CodeId field_code,Field_type field_type  from Template_Set where TabId='"+tabid+"' and Field_name is not null and nvl(Field_name,'0')!='0' and nvl(subflag,0) !='1' order by pageid,RTop,RLeft";
			} else {
				select_template_sql=" select  Field_hz field_name,Field_name field_key,ChgState field_state,CodeId field_code,Field_type field_type from Template_Set where TabId='"+tabid+"'  and Field_name is not null and Field_name!=''  and subflag !='1' order by pageid,RTop,RLeft";
			}
			//查询templet_set表单字段格式列表
			ArrayList<LazyDynaBean> keylist;
			try {
				
				keylist = dao.searchDynaList(select_template_sql);
				
				String select_items = "";
				//keylist里面修改字段值,变化前，变化后,临时变量
				for(LazyDynaBean bean : keylist){
					String field_key = (String)bean.get("field_key");
					String field_state = (String)bean.get("field_state");
					if("1".equalsIgnoreCase(field_state)){
						select_items+=field_key+"_1,";
						bean.set("field_key", field_key.toLowerCase()+"_1");
					}else if("2".equalsIgnoreCase(field_state)){
						select_items+=field_key+"_2,";
						bean.set("field_key", field_key.toLowerCase()+"_2");
					}else{
						select_items+=field_key+",";
						bean.set("field_key", field_key.toLowerCase());
					}
	
				}
			
				String selectSql ="select *  from templet_"+tabid+" where seqnum in (select seqnum from t_wf_task_objlink where tab_id=? and task_id=?)";
				ArrayList maskList = new ArrayList();
				maskList.add(tabid);
				maskList.add(taskid);
				value_set = dao.search(selectSql.toString(),maskList);
				
				while(value_set.next()){
					
					//为了查询附件
					String ins_id = value_set.getString("ins_id");
					String nbase = value_set.getString("BasePre").toLowerCase();
					String object_id = value_set.getString("a0100");
					ArrayList<LazyDynaBean> item = new ArrayList<LazyDynaBean>();
					for(LazyDynaBean key:keylist){
						String field_type = (String)key.get("field_type");
						String field_key = (String)key.get("field_key");
						String field_code = (String)key.get("field_code");
						if("A".equalsIgnoreCase(field_type)){
							String true_value = value_set.getString(field_key);
							
							if(field_code==null){
								field_code="";
							}
							//如果代码类型的值为空，那么下面求代码类中文意义时会报空指针错误,所以如果直接赋值为""
							if(true_value==null || true_value.length()==0){
								key.set("value", "");
								//若有continue，别忘了item.add(key);
								item.add(key);
								continue;
							}
							
							ArrayList codelist = new ArrayList();
							
							if(field_code.length()==0|| "0".equalsIgnoreCase(field_code)){
								
								key.set("value", true_value);
								//若有continue，别忘了item.add(key);
								item.add(key);
								continue;
								
							}else if("@K".equalsIgnoreCase(field_code)|| "UM".equalsIgnoreCase(field_code)|| "UN".equalsIgnoreCase(field_code)){
								
								codelist.add(true_value);
								codelist.add(field_code);
								
								code_set = dao.search("select codeitemdesc from organization where codeitemid=? and codesetid=?", codelist);
								if(code_set.next()){
									true_value = code_set.getString("codeitemdesc");
								}
							}else{
								codelist.add(true_value);
								codelist.add(field_code);
								
								code_set = dao.search("select codeitemdesc from codeitem where codeitemid=? and codesetid=?", codelist);
								if(code_set.next()){
									true_value = code_set.getString("codeitemdesc");
								}
							}
							key.set("value", true_value);
							
						}else if("D".equalsIgnoreCase(field_type)){
							
							java.sql.Date sqldate = value_set.getDate(field_key);
							if(sqldate==null){
								key.set("value", null);
							}else{
								java.util.Date utildate=new java.util.Date(sqldate.getTime());
								key.set("value", utildate);
							}
							
						}else if("N".equalsIgnoreCase(field_type)){
							
							Object object = value_set.getObject(field_key);
							String value = "";
							if(object==null){
								key.set("value", value);
							}else{
								value = String.valueOf(object);
								key.set("value", value);
							}
							
						}else if("M".equalsIgnoreCase(field_type)){
							String mtext = value_set.getString(field_key);
							if(mtext==null){
								mtext="";
							}
							key.set("value", mtext);
						}else{
							String other = value_set.getString(field_key);
							if(other==null){
								other="";
							}
							key.set("value", other);
						}
						item.add(key);
					}
					
					//附件查询 个人附件
					ArrayList<LazyDynaBean> per_file_list = this.get_person_file_list(dao, object_id, nbase,ins_id);
					
					if(per_file_list.size()>0){
						for(LazyDynaBean per_file_item : per_file_list){
							item.add(per_file_item);
						}
					}
					//公共附件
					ArrayList<LazyDynaBean> pub_file_list = this.get_public_file_list(dao, ins_id);
					
					if(pub_file_list.size()>0){
						for(LazyDynaBean pub_file_item : pub_file_list){
							item.add(pub_file_item);
						}
					}
					
					last_bean.add(item);
				}
			
			} catch (SQLException e) {
				cat.error(this.getClass()+e.getMessage());
				e.printStackTrace();
			} catch (GeneralException e) {
				cat.error(this.getClass()+e.getMessage());
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(value_set);
				PubFunc.closeDbObj(set);
			}
			
			return  last_bean;
	
	}
	
	
	private ArrayList<LazyDynaBean> get_person_file_list(ContentDAO dao,String object_id,String nbase,String ins_id){
		ArrayList<LazyDynaBean> person_file_list = new ArrayList<LazyDynaBean>();
		
		RowSet file_set = null;
		//附件查询
		ArrayList lis = new ArrayList();
		lis.add(object_id);
		lis.add(nbase);
		lis.add(ins_id);
		
		String personal_file_sql  = "select *  from t_wf_file where objectid=? and lower(basepre)=? and ins_id=? and attachmenttype='1'";
		try {
			file_set = dao.search(personal_file_sql,lis);
		
		
			//文件字段，文件类型，文件名称，文件后缀，文件路径
			//个人附件
			while(file_set.next()){
				LazyDynaBean file_bean = new LazyDynaBean();
				//F表示文件类型
				file_bean.set("field_type", "F");
				//文件名称
				String filename = file_set.getString("name");
				file_bean.set("file_name", filename);
				//文件后缀名
				String ext = file_set.getString("ext");
				file_bean.set("file_ext", ext);
				//文件路径
				String file_path="";
				try{
					file_path = file_set.getString("filepath");
				}catch(SQLException ex){
					cat.error(this.getClass()+"Function get_person_file_list:"+ex.getMessage());
					ex.printStackTrace();
				}
				file_bean.set("file_path", file_path);
				
				person_file_list.add(file_bean);
				
			}
		
		} catch (SQLException e) {
			cat.error(this.getClass()+"Function get_person_file_list:"+e.getMessage());
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(file_set);
		}
		
		return person_file_list;
		
	}
	
	
	private ArrayList<LazyDynaBean> get_public_file_list(ContentDAO dao,String ins_id){
		
		ArrayList<LazyDynaBean> public_file_list = new ArrayList<LazyDynaBean>();
		
		RowSet file_set = null;
		String public_file_sql  = "select *  from t_wf_file where ins_id=? and attachmenttype='0'";
		ArrayList lis_public = new ArrayList();
		lis_public.add(ins_id);
		
		try {
			file_set = dao.search(public_file_sql, lis_public);
			
			while(file_set.next()){
				
				LazyDynaBean file_bean = new LazyDynaBean();
				//F表示文件类型
				file_bean.set("field_type", "F");
				//文件名称
				String filename = file_set.getString("name");
				file_bean.set("file_name", filename);
				//文件后缀名
				String ext = file_set.getString("ext");
				file_bean.set("file_ext", ext);
				//文件路径
				String file_path="";
				try{
					file_path = file_set.getString("filepath");
				}catch(SQLException ex){
					cat.error(this.getClass()+"Function get_public_file_list:"+ex.getMessage());
					ex.printStackTrace();
				}
				file_bean.set("file_path", file_path);
				public_file_list.add(file_bean);
				
			}
			
		} catch (SQLException e) {
			cat.error(this.getClass()+"Function get_public_file_list:"+e.getMessage());
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(file_set);
		}
		return public_file_list;
		
		
		
	}

}
