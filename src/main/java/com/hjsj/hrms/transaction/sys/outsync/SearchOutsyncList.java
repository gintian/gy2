package com.hjsj.hrms.transaction.sys.outsync;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchOutsyncList extends IBusiness{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		
		DbReloadOutSync();//检索改进新增字段
		//---------------《hrms:paginationdb》标签参数-------------
		String sql_str = "select sys_id,sys_name,url,sync_method,send,fail_time,state ";
		String sql_where = "from t_sys_outsync";
		String table = "t_sys_outsync"; //表名
		String columns = "sys_id,sys_name,url,sync_method,send,fail_time,state,";//字段
		String order_by = ""; //排序
		String distinct = ""; //唯一标识
		//---------------《hrms:paginationdb》标签参数-------------
		this.formHM.put("sql_str", sql_str);
		this.formHM.put("sql_where", sql_where);
		this.formHM.put("table", table);
		this.formHM.put("columns", columns);
		this.formHM.put("order_by", order_by);
		this.formHM.put("distinct", distinct);
		//获得最大失败参数
		String max_time= new HrSyncBo(this.frameconn).getAttributeValue(HrSyncBo.FAIL_LIMIT);
		if(max_time == null || max_time.length() == 0){
			max_time = "0";
		}
		this.formHM.put("max_time", max_time);
		
	}


	/**
	 * 数组同步改进修改新增字段
	 * LiWeichao 2011-07-19 10:53:31
	 */
	private void DbReloadOutSync(){
		boolean flag = false;
		DbWizard dbWizard = new DbWizard(this.getFrameconn());
		Table table = new Table("t_sys_outsync");
		if(!dbWizard.isExistField("t_sys_outsync", "send", false)){
			Field field = new Field("send","消息发送");//0：不发送；1：发送
			field.setDatatype(DataType.STRING);
			field.setLength(1);
			table.addField(field);
			flag = true;
		}
		if(!dbWizard.isExistField("t_sys_outsync", "control", false)){
			Field field = new Field("control","同步信息类型");//A人员，B机构K岗位，多个用逗号隔开
			field.setDatatype(DataType.STRING);
			field.setLength(10);
			table.addField(field);
			flag = true;
		}
		if(!dbWizard.isExistField("t_sys_outsync", "other_param", false)){
			Field field = new Field("other_param","其他参数");//目前其他配置参数xml格式
			field.setDatatype(DataType.CLOB);
			table.addField(field);
			flag = true;
		}
		if(flag){
			try {
				dbWizard.addColumns(table);
			} catch (GeneralException e) {
				e.printStackTrace();
			}
		}
	}
}
