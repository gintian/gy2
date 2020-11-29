/**
 * 
 */
package com.hjsj.hrms.transaction.sys.id_factory;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:chenmengqing</p>
 * <p>Description:所有序号列表交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-11-19:10:38:07</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchSequenceListTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String edition = (String)hm.get("edition");
		if(edition!=null&& "4".equalsIgnoreCase(edition)){
			this.getFormHM().put("edition","4");
		}else{
			this.getFormHM().put("edition","5");
		}
		hm.remove("edition");
		String flag=(String)this.getFormHM().get("sysorclient");
		String sequence_name=(String) this.getFormHM().get("searchflag");
		sequence_name=PubFunc.getStr(sequence_name);
		if(sequence_name==null||sequence_name.length()<=0){
			sequence_name="";
		}else{
			sequence_name=PubFunc.getStr(sequence_name);
			sequence_name=" and (sequence_name like '%"+sequence_name+"%' or sequence_desc like '%"+sequence_name+"%')";
		}
		if(flag==null|| "".equals(flag))
			flag="1";
		String where="";
		String strsql="select sequence_name,sequence_desc,minvalue,maxvalue,increase_order,prefix,suffix,currentID,id_length,increment_o,loop_mode,c_rule,prefix_field,prefix_field_len,byprefix";
		if("1".equals(flag)){
			this.getFormHM().put("sysorclient",flag);
			if(sequence_name.length()>0){
				where = " from id_factory where (status=0 or status is null)" +sequence_name;
			}else{
			where = " from id_factory where status=0 or status is null" ;
			}
		}
		else{
			where = " from id_factory where status=1" +sequence_name;	
			this.getFormHM().put("sysorclient",flag);
		}
		String column = "sequence_name,sequence_desc,minvalue,maxvalue,increase_order,prefix,suffix,currentid,id_length,increment_o,loop_mode,c_rule,prefix_field,prefix_field_len,byprefix";
		this.getFormHM().put("sql",strsql);
		this.getFormHM().put("where",where);
		this.getFormHM().put("pagerows","21");
		this.getFormHM().put("column",column);
	}

}
