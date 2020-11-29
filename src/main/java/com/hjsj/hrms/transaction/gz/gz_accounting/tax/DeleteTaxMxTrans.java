package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:DeleteTaxMxTrans</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class DeleteTaxMxTrans extends IBusiness {
	public void execute() throws GeneralException {
		/*
		String selectid = (String)this.getFormHM().get("selectID");
		TaxMxBo txb = new TaxMxBo(this.getFrameconn());
		txb.deleteMxRecord(selectid);
		*/
		/**chenmengqing added */
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("tax_table_table");  //数据集别名+"_table"
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("tax_table_record");//选中的记录，数据集别名+"_record"		
		try
		{
			if(list!=null&&list.size()>0)
			{
				StringBuffer taxid= new StringBuffer();
				int zheng=list.size()/500;
				int yu=list.size()%500;
				if(zheng>0){
					for(int j=0;j<zheng;j++){
						taxid= new StringBuffer();
						for(int i=j*500;i<(j+1)*500;i++)
						{
							taxid.append(",");
							RecordVo vo = (RecordVo)list.get(i);
							taxid.append(vo.getString("tax_max_id"));
						}
						String sql = " delete from "+name+" where tax_max_id in("+taxid.toString().substring(1)+")";
						ContentDAO dao = new ContentDAO(this.getFrameconn());
						dao.delete(sql,new ArrayList());
					}
					taxid= new StringBuffer();
					if(yu!=0){
						for(int i=zheng*500;i<zheng*500+yu;i++)
						{
							taxid.append(",");
							RecordVo vo = (RecordVo)list.get(i);
							taxid.append(vo.getString("tax_max_id"));
						}
						String sql = " delete from "+name+" where tax_max_id in("+taxid.toString().substring(1)+")";
						ContentDAO dao = new ContentDAO(this.getFrameconn());
						dao.delete(sql,new ArrayList());
					}
				}else{ 
					for(int i=0;i<yu;i++)
					{
						if(i!=0)
							taxid.append(",");
						RecordVo vo = (RecordVo)list.get(i);
						taxid.append(vo.getString("tax_max_id"));
					}
					String sql = " delete from "+name+" where tax_max_id in("+taxid.toString()+")";
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					dao.delete(sql,new ArrayList());
				}

			}
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
