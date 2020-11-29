/**
 * 
 */
package com.hjsj.hrms.transaction.sys.id_factory;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:SearchSequenceTrans</p>
 * <p>Description:查询单个序号对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-11-19:11:06:55</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchSequenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		String updateflag=(String)this.getFormHM().get("updateflag");
		String  sequence_name=(String)this.getFormHM().get("sequence_name");
		ArrayList dblist = new ArrayList();
		RecordVo vo=new RecordVo("id_factory");	
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("update".equals(updateflag))
			{
				vo.setString("sequence_name",sequence_name);
				vo=dao.findByPrimaryKey(vo);
				this.getFormHM().put("old_sequence_name", sequence_name);
			}
			String sql = "select itemid,itemdesc from fielditem where fieldsetid='A01' and itemtype='A' and useflag='1'";
			this.frecset = dao.search(sql);
			CommonData cd=new CommonData("",ResourceFactory.getProperty("label.select.dot"));
        	dblist.add(cd);
        	cd=new CommonData("b0110",ResourceFactory.getProperty("leaderteam.leaderparam.unit"));
        	dblist.add(cd);
        	cd=new CommonData("e0122",ResourceFactory.getProperty("e0122.label"));
        	dblist.add(cd);
        	cd=new CommonData("e01a1",ResourceFactory.getProperty("label.codeitemid.kk"));
        	dblist.add(cd);
        	while(this.frecset.next()){
        		if("b0110".equalsIgnoreCase(this.frecset.getString("itemid")))
        			continue;
        		if("e0122".equalsIgnoreCase(this.frecset.getString("itemid")))
        			continue;
        		if("e01a1".equalsIgnoreCase(this.frecset.getString("itemid")))
        			continue;
        		cd=new CommonData(this.frecset.getString("itemid"),this.getFrecset().getString("itemdesc"));
        		dblist.add(cd);
        	}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			this.getFormHM().put("idvo",vo);
			this.getFormHM().put("dblist",dblist);
		}
	}

}
