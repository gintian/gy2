package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：SearchAccountTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:47:23 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:47:23 PM   
* 修改备注：   获取科目列表
* @version    
*
 */
public class SearchAccountTrans extends IBusiness {

	public void execute() throws GeneralException {
		VoucherBo vcbo=new VoucherBo(this.getFrameconn(),this.getUserView());
		String table = "GZ_code";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String selectvalue = (String) hm.get("selectvalue");
		ArrayList list = new ArrayList();
		selectvalue = SafeCode.decode(selectvalue);
		hm.remove("selectvalue");
		StringBuffer sql = new StringBuffer();
		sql.append("select * from GZ_code");
		if(selectvalue!=null&&!"".equals(selectvalue)){
			sql.append(" where ccode like '%"+selectvalue+"%' or ccode_name like '%"+selectvalue+"%'");
		}
		sql.append(" order by ccode");
		try{
			ContentDAO da= new ContentDAO(this.getFrameconn());
			this.frowset=da.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean= new LazyDynaBean();
				bean.set("i_id",this.frowset.getString("i_id"));
				bean.set("ccode",this.frowset.getString("ccode")==null?"":this.frowset.getString("ccode"));
				bean.set("ccode_name",this.frowset.getString("ccode_name")==null?"":this.frowset.getString("ccode_name"));
				bean.set("igrade",this.frowset.getString("igrade"));
				list.add(bean);
			}
    		}catch(Exception e){
    		e.printStackTrace();
    		}
		ArrayList fieldlist=vcbo.getFieldlistList();
		/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 start */
		//this.getFormHM().put("sql", sql.toString());
		this.userView.getHm().put("gz_sql_1", SafeCode.encode(PubFunc.encrypt(sql.toString())));
		/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 end */
		this.getFormHM().put("tablename", table);
		this.getFormHM().put("fieldlist", fieldlist);
		this.getFormHM().put("list", list);
	}

}
