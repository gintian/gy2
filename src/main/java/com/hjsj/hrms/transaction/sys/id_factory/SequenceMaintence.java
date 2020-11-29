/**
 * 
 */
package com.hjsj.hrms.transaction.sys.id_factory;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * @author yuxiaochun
 * 
 */
public class SequenceMaintence extends IBusiness implements Constant {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String status = "0";
		ArrayList list = (ArrayList) this.getFormHM().get("sel_and_del");

//		查询显示
		if (this.getFormHM().containsKey("updateoraddflag")) {
			if("client"
						.equals((String) this.getFormHM().get("updateoraddflag"))){
			
				status = "1";
				this.getFormHM().put("sysorclient", "client");
				this.getFormHM().put("updateoraddflag", "sudo");
				putSqlStr(status,"");
				
				
			}
		if("sys"
						.equals((String) this.getFormHM().get("updateoraddflag")))
		{
			status = ("0");
			this.getFormHM().put("updateoraddflag", "sudo");
			this.getFormHM().put("sysorclient", "sys");
			putSqlStr(status,"");
		}
		if("del0"
						.equals((String) this.getFormHM().get("updateoraddflag"))){
//			确认删除
			if (list == null)
				return;
			if (list == null || list.size() == 0)
				return;
			StringBuffer cond= new StringBuffer(" and ");
			for (int i = 0; i < list.size(); i++) {
				DynaBean dbean = new LazyDynaBean();
				dbean = (DynaBean) list.get(i);
				if(i==0){
					cond.append("sequence_name='"+getSeqname(dbean)+"'");
				}
				else{
				cond.append(" or sequence_name='"+getSeqname(dbean)+"'");
				}
				
			}
			putSqlStr("1", cond.toString());
			this.getFormHM().put("updateoraddflag", "del0");
			
		}
		if("del1"
						.equals((String) this.getFormHM().get("updateoraddflag"))){
//			删除
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				for (int i = 0; i < list.size(); i++) {
					DynaBean dbean = new LazyDynaBean();
					dbean = (DynaBean) list.get(i);
					dao.deleteValueObject(update(dbean));
					putSqlStr(status,"1");
				}

			} catch (Exception exx) {
				exx.printStackTrace();
				throw GeneralExceptionHandler.Handle(exx);
			}
			
		}
			
		} else {
			status = ("0");
			this.getFormHM().put("updateoraddflag", "sudo");
			this.getFormHM().put("sysorclient", "sys");
			putSqlStr(status,"");
		}

	}
	
	public void putSqlStr(String status,String cond){
		
		String sql = "select sequence_name,sequence_desc,minvalue,maxvalue,increase_order,prefix,suffix,currentID,id_length,increment_o";
		String where = "from id_factory where status=" + status+cond;
		String column = "sequence_name,sequence_desc,minvalue,maxvalue,increase_order,prefix,suffix,currentid,id_length,increment_o";
		String orderby = "";
		this.getFormHM().put("sql", sql);
		this.getFormHM().put("where", where);
		this.getFormHM().put("column", column);
		this.getFormHM().put("orderby", orderby);
		ArrayList fieldlist = getFieldList(column);
		this.getFormHM().put("fieldlist", fieldlist);
	}

	public ArrayList getFieldList(String column) {
		String[] fields = column.split(",");
		ArrayList list = new ArrayList();
		FieldItem fielditem = null;
		for (int i = 0; i < fields.length; i++) {
			fielditem = new FieldItem();
			fielditem.setFieldsetid("id_factory");
			fielditem.setItemid(fields[i]);
			fielditem.setItemtype("A");
			fielditem.setVisible(true);
			list.add(fielditem);
		}

		return list;
	}

	private RecordVo update(DynaBean dbean) {
		RecordVo vo = new RecordVo("id_factory");
		vo.setString("sequence_name", (String) dbean.get("sequence_name"));
		vo.setString("sequence_desc", (String) dbean.get("sequence_desc"));
		vo.setInt("minvalue", this.ParseString((String) dbean.get("minvalue"))
				.intValue());
		vo.setDouble("maxvalue", this.ParseString(
				(String) dbean.get("maxvalue")).doubleValue());
		vo.setInt("auto_increase", 1);
		vo.setInt("increase_order", this.ParseString(
				(String) dbean.get("increase_order")).intValue());
		vo.setString("prefix", (String) dbean.get("prefix"));
		vo.setString("suffix", (String) dbean.get("suffix"));
		vo.setDouble("currentid", this.ParseString(
				(String) dbean.get("currentid")).doubleValue());
		vo.setInt("id_length", this
				.ParseString((String) dbean.get("id_length")).intValue());
		vo.setInt("increment_o", this.ParseString(
				(String) dbean.get("increment_o")).intValue());
		vo.setInt("status", 1);
		return vo;
	}
	
	private String getSeqname(DynaBean dbean) {
		
		String seqname= (String) dbean.get("sequence_name");
		
		return seqname;
	}

	private Long ParseString(String str) {
		Long l = new Long(str);
		return l;
	}
}
