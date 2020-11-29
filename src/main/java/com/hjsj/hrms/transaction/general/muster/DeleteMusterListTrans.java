/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:DeleteMusterListTrans</p>
 * <p>Description:删除花名册交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-26:13:53:03</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteMusterListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String[] tabid=(String[])this.getFormHM().get("tabid");
		
		if(tabid==null)
			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)this.getFormHM().get("inforkind");
		if(infor_kind==null|| "".equals(infor_kind))
			infor_kind="1";
		ArrayList paralist=new ArrayList();
		for(int i=0;i<tabid.length;i++)
		{
			ArrayList list=new ArrayList();		
			cat.debug("tabid[]="+tabid[i]);			
			list.add(tabid[i]);
			paralist.add(list);
		}

		StringBuffer strsql=new StringBuffer();
		strsql.append("delete from lname where tabid=?");
		ContentDAO dao=null;
		try
		{
			dao=new ContentDAO(this.getFrameconn());
			dao.batchUpdate(strsql.toString(),paralist);
		    strsql.setLength(0);
		    strsql.append("delete from lbase where tabid=?");
			dao.batchUpdate(strsql.toString(),paralist);	
			
			/**权限范围内的人员库列表*/
			ArrayList dblist=this.userView.getPrivDbList();
			/**删除临时表*/
			dropTempTable(tabid, dblist);
			
			DbNameBo dbvo=new DbNameBo(this.getFrameconn());
			dblist=dbvo.getDbNameVoList(dblist);
			ArrayList list=new ArrayList();
			for(int i=0;i<dblist.size();i++)
			{
				CommonData vo=new CommonData();
				RecordVo dbname=(RecordVo)dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
			this.getFormHM().put("dblist",list);
			/**花名册列表*/
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			ArrayList musterlist=musterbo.getMusterList(infor_kind);
			this.getFormHM().put("musterlist",musterlist);	
			this.getFormHM().put("inforkind",infor_kind);				
		}
		catch(Exception ex)
		{
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);			
		}

	}

	/**
	 * 删除临时表
	 * @param tabid
	 * @param dblist
	 */
	private void dropTempTable(String[] tabid, ArrayList dblist) {
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		Table table=new Table("");
		String tabname=null;
		StringBuffer temp=new StringBuffer();
		for(int i=0;i<tabid.length;i++)
		{
			temp.append("m");
			temp.append(tabid[i]);
			temp.append("_");
			temp.append(this.userView.getUserId());
			temp.append("_");
			for(int j=0;j<dblist.size();j++)
			{
				tabname=temp.toString()+dblist.get(j);
				table.setName(tabname);
				if(dbWizard.isExistTable(tabname,false))
					dbWizard.dropTable(table);	
			
			}
			tabname=temp.toString()+"B";
			table.setName(tabname);
			if(dbWizard.isExistTable(tabname,false))
				dbWizard.dropTable(table);
			tabname=temp.toString()+"K";
			table.setName(tabname);
			if(dbWizard.isExistTable(tabname,false))
				dbWizard.dropTable(table);	
			temp.setLength(0);
		}			
		table.setName("m_idx_"+this.userView.getUserId());
		if(dbWizard.isExistTable("m_idx_"+this.userView.getUserId(),false))
			dbWizard.dropTable(table);
	}

}
