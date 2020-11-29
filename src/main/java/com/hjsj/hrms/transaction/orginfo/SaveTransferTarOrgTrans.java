/*
 * Created on 2006-1-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveTransferTarOrgTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList transferorglist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 transferorglist = getLazyDynaBeanToRecordVo(transferorglist);
		 this.getFormHM().put("transferorglist",transferorglist);
		 cat.debug("-----saveTransferOrg------>");
			String ishavedept="false";
			String ishavepos="false";
			String ishaveorg="false";
			for(int i=0;i<transferorglist.size();i++)
			{
				RecordVo vo=(RecordVo)transferorglist.get(i);
				if("UN".equalsIgnoreCase(vo.getString("codesetid")))
					ishaveorg="true";
				if("UM".equalsIgnoreCase(vo.getString("codesetid")))
					ishavedept="true";
				if("@K".equalsIgnoreCase(vo.getString("codesetid")))
					ishavepos="true";
			}		
			if("true".equalsIgnoreCase(ishaveorg))
				ishavedept="UN";
			else
				ishavedept="UM";
			this.getFormHM().put("ishavedept",ishavedept);
			String UNIT_HISTORY_SET = SystemConfig
			.getPropertyValue("UNIT_HISTORY_SET");
			if (UNIT_HISTORY_SET != null
					&& UNIT_HISTORY_SET.trim().length() > 1&&DataDictionary.getFieldSetVo(UNIT_HISTORY_SET)!=null) {
				ArrayList childfielditemlist = DataDictionary
						.getFieldList(UNIT_HISTORY_SET.toUpperCase(),
								Constant.USED_FIELD_SET);
				childfielditemlist = childfielditemlist!=null?childfielditemlist:new ArrayList();
				this.getFormHM().put("childfielditemlist", childfielditemlist);
				this.getFormHM().put("changemsg", "yes");
			} else {
				this.getFormHM().put("changemsg", "no");
				this.getFormHM().put("childfielditemlist", new ArrayList());
			}

			this.getFormHM().put("transfercodeitemid","");
			this.getFormHM().put("tarorgname", "");
	}

	private ArrayList getLazyDynaBeanToRecordVo(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
		for(int i=0;i<transferorglist.size();i++)
	    {
			LazyDynaBean rec=(LazyDynaBean)transferorglist.get(i); 
	   	    String codeitemid=rec.get("code").toString();
			StringBuffer sql=new StringBuffer();   
			String table="organization";
			sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
			sql.append(" where codeitemid='"+codeitemid+"'");
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo = null;
			try {
				rs=dao.search(sql.toString());
				if(rs.next())
				{
					vo=new RecordVo("organization");
					vo.setString("codesetid",rs.getString("codesetid"));
					vo.setString("codeitemdesc",rs.getString("codeitemdesc"));
					vo.setString("parentid",rs.getString("parentid"));
					vo.setString("childid",rs.getString("childid"));
					vo.setString("codeitemid",rs.getString("codeitemid"));
					vo.setInt("grade", rs.getInt("grade"));
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许划转，操作失败！","",""));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	    if(vo==null)
	   	    	continue;
	   	    list.add(vo);
	    }
		return list;
	}
}
