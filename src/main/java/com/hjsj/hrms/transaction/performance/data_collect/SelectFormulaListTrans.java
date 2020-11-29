package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
* 
* 类名称：SelectFormulaListTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:11:56 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:11:56 PM   
* 修改备注：   获取计算公式列表
* @version    
*
 */
public class SelectFormulaListTrans extends IBusiness {

	public void execute() throws GeneralException {	
		try
		{
			ArrayList list=new ArrayList();
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			StringBuffer buf=new StringBuffer();
			buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid='");
			buf.append("-2");
			buf.append("' and ( cstate='"+set_id+"' )");//and cstate = '"+set_id+"'   and useflag=1
			buf.append(" order by salaryid,sortid");
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			list=dao.getDynaBeanList(rset);
			rset.close();
			ArrayList formulaList=new ArrayList();
			
			for(int i=0;i<list.size();i++)
			{
				DynaBean abean=(DynaBean)list.get(i);
				String itemname=(String)abean.get("itemname");
				String state=this.userView.analyseFieldPriv(itemname);
				if("0".equals(state))
					state=this.userView.analyseFieldPriv(itemname,0);
				if("2".equals(state)||this.userView.isSuper_admin())
						formulaList.add(abean);			
			}
			this.getFormHM().put("formulalist", formulaList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
