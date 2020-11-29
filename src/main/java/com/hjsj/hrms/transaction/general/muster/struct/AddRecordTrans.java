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

/**
 * <p>Title:AddRecordTrans</p>
 * <p>Description:查询增加记录</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-27:10:57:01</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class AddRecordTrans extends IBusiness {
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
	
	public void execute() throws GeneralException {
        String strInfkind=(String)this.getFormHM().get("infor");
        if(strInfkind==null|| "".equals(strInfkind))
            strInfkind="1";
        /**m表格号_用户名_库前缀*/
        String setname=(String)this.getFormHM().get("setname");
        String history=(String)this.getFormHM().get("history");
		try
		{        
			ArrayList list=(ArrayList)this.getFormHM().get("objlist");
			if(list==null||list.size()==0)
				return;
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			/**以后花名册中包括多个库的人员时可以去掉*/
			if("1".equals(strInfkind))
			{
				list=parseObjectByDbase(setname,list);
			}
			else
				history="0";  //目前花名册没有提供历史记录功能,所以默认置为 0  dengcan 2008/02/03
			
			
			
			history=musterbo.getHistoryById(setname);
			if(!"1".equals(strInfkind))
				history="0";
			if("1".equals(history)){
				if(updateField(setname,list))
					musterbo.addMusterRecordData(strInfkind,setname,list,history);
			}else{
				musterbo.addMusterRecordData(strInfkind,setname,list,history);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	private boolean updateField(String setname,ArrayList objlist){
		boolean check = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer strWhere=new StringBuffer();
	    for(int i=0;i<objlist.size();i++)
		{
			strWhere.append("'");
			strWhere.append(objlist.get(i));
			strWhere.append("'");
			strWhere.append(",");
		}
		strWhere.setLength(strWhere.length()-1);
	    try {
	    	StringBuffer buf = new StringBuffer();
	    	buf.append("delete from ");
	    	buf.append(setname);
	    	buf.append(" where A0100 in(");
	    	buf.append(strWhere);
	    	buf.append(")");
			dao.update(buf.toString());
			check=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}

}
