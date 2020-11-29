package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：DeleteVoucherTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:40:58 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:40:58 PM   
* 修改备注：   删除凭证及凭证下的分录
* @version    
*
 */
public class DeleteVoucherTrans extends IBusiness {

	public void execute() throws GeneralException
	{
	  String pn_id = (String)this.getFormHM().get("pn_id");
		  deleteKaClass( pn_id);
		  this.getFormHM().put("class_id","");
	}
	/**
     * 删除
     * @param pn_id
     * @throws GeneralException
     */
    public void deleteKaClass(String pn_id)throws GeneralException
    {
    	if(pn_id == null||pn_id.length()<= 0)
    		return;
    	ArrayList list  = new ArrayList();
    	String sql = "delete from GZ_Warrant where pn_id = ?";
    	String sql1 = "delete from GZ_WARRANTLIST where pn_id = ?";
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	try
    	{
    			list.add(pn_id);
    			dao.delete(sql,list);	
    			dao.delete(sql1,list);
    	}catch(Exception e)
    	{
    		this.getFormHM().put("err_message", ResourceFactory.getProperty("kq.class.delete.error"));	
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }

}
