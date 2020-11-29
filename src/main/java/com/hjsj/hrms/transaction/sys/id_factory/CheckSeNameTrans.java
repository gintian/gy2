package com.hjsj.hrms.transaction.sys.id_factory;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 检查序列名是否存在
 * @author xujian
 *Apr 14, 2010
 */
public class CheckSeNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sename=(String)this.getFormHM().get("sename");
		String msg = "ok";
		String itemdesc="";
		try{
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select * from id_factory where upper(sequence_name)='"+sename.toUpperCase()+"'";
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				msg="no";
			}
			//int startint=sename.indexOf('.')+1;
			if("ok".equals(msg)){
				String arr[]=sename.split("\\.");
				if(arr.length==2){
					FieldSet fs=(FieldSet)DataDictionary.getFieldSetVo(arr[0]);
					FieldItem item=(FieldItem)DataDictionary.getFieldItem(arr[1]);
					if(item==null||fs==null){
						msg="nohave";
					}else{
						itemdesc=fs.getFieldsetdesc()+"/"+item.getItemdesc();
					}
				}else{
					msg="nohave";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("itemdesc", SafeCode.encode(itemdesc));
		}
	}

}
