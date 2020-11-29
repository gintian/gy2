/**
 * 
 */
package com.hjsj.hrms.transaction.sys.id_factory;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:DeleteSelectSequenceTrans</p>
 * <p>Description:删除选中的序号列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-11-19:11:00:17</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteSelectSequenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList sellist=(ArrayList)this.getFormHM().get("sel_and_del");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int i=0;i<sellist.size();i++)
			{
				LazyDynaBean dynabean=(LazyDynaBean)sellist.get(i);
				RecordVo idvo=new RecordVo("id_factory");
				String seqname=(String) dynabean.get("sequence_name");
				int startint=seqname.indexOf(".")+1;
				String itemid=seqname.substring(startint,seqname.length());
				idvo.setString("sequence_name",seqname);
				dao.deleteValueObject(idvo);				
				FieldItem item=(FieldItem)DataDictionary.getFieldItem(itemid);
				if(item!=null){
					item.setSequenceable(false);
					item.setC_rule(1);
					item.setSequencename(seqname);
				}

			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
