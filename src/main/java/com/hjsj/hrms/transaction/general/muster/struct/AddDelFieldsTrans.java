/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title:AddDelFieldsTrans</p>
 * <p>Description:增减指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-21:14:22:47</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class AddDelFieldsTrans extends IBusiness {


	public void execute() throws GeneralException {
		String setname;
		try
		{
			setname=(String)this.getFormHM().get("setname");
			ArrayList list=(ArrayList)this.getFormHM().get("fields");
			if(list==null||list.size()==0)
				return;
			cat.debug("fields="+list.toString());
		    MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		    String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		    if(updateField(setname))
		    	musterbo.addDelStrutField(setname,list,infor_Flag);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

	/**
	 * 根据应用库前缀拆分人员编号
	 * @param setname
	 * @param objlist
	 * @return
	 */
	private ArrayList parseObjectByDbase(String setname,ArrayList objlist)
	{
		ArrayList list=new ArrayList();
		int idx=setname.lastIndexOf("_");
		String dbpre=setname.substring(idx+1);
		for(int i=0;i<objlist.size();i++)
		{
			String a0100=(String)objlist.get(i);
			if(a0100.indexOf(dbpre)==-1)
				continue;
			list.add(a0100.substring(3));
		}
		return list;
	}
	private boolean updateField(String setname){
		boolean check = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringTokenizer st=new StringTokenizer(setname,"_");
	    String tabid=st.nextToken().substring(1);
	    try {
	    	StringBuffer buf = new StringBuffer();
	    	buf.append("update lbase set Width=1800 where Width=0");
	    	buf.append(" and tabid=");
	    	buf.append(tabid);
			dao.update(buf.toString());
			check=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}
	
}
