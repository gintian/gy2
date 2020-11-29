package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:调整薪资类别顺序</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 23, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class EditSetSeqTrans extends IBusiness {

	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			String[] salarySetSort=(String[])this.getFormHM().get("salarySetSort");
			ArrayList setlist=(ArrayList)this.getFormHM().get("setlist2");
			String sql = "update salarytemplate set seq=? where salaryid=?";
			try(PreparedStatement pt=this.getFrameconn().prepareStatement(sql)) {
				if (setlist.size() > 0) {
					int pre_seq = 0;
					for (int i = 0; i < salarySetSort.length; i++) {
						if ("0".equals((String) ((LazyDynaBean) setlist.get(i)).get("seq"))) {
							pre_seq++;
							pt.setInt(1, pre_seq);
						} else {
							pt.setInt(1, Integer.parseInt((String) ((LazyDynaBean) setlist.get(i)).get("seq")));
							pre_seq = Integer.parseInt((String) ((LazyDynaBean) setlist.get(i)).get("seq"));
						}
						pt.setInt(2, Integer.parseInt(salarySetSort[i]));
						pt.addBatch();
					}
				}
				// 打开Wallet
				dbS.open(this.getFrameconn(), sql);
				pt.executeBatch();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
