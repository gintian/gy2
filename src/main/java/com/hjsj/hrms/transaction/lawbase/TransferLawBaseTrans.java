package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * <p>Title:</p>
 * <p>Description:文档目录换转</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-7-1</p>
 * @author xuj
 * @version 1.0
 * 
 */
public class TransferLawBaseTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String src_base_id = (String)this.getFormHM().get("src_base_id");
		src_base_id = PubFunc.decrypt(SafeCode.decode(src_base_id));
        String target_base_id = (String)this.getFormHM().get("target_base_id");
        if(!"root".equalsIgnoreCase(target_base_id))
            target_base_id = PubFunc.decrypt(SafeCode.decode(target_base_id));
        target_base_id ="root".equals(target_base_id)?src_base_id:target_base_id;
        String basetype = (String)this.getFormHM().get("basetype");
        basetype = "1".equals(basetype)?"5":"1";
        String flag = "error";
        try
		{
        	
        	 ContentDAO dao=new ContentDAO(this.getFrameconn());
        	 String sql = "update law_base_struct set up_base_id='"+target_base_id+"',basetype="+basetype+" where base_id='"+src_base_id+"'";
        	 dao.update(sql);
        	 UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
        	 if("1".equalsIgnoreCase(basetype))
     			user_bo.saveResource(src_base_id, this.userView,
     					IResourceConstant.LAWRULE);
     		 if("5".equalsIgnoreCase(basetype))
     			user_bo.saveResource(src_base_id, this.userView,
     					IResourceConstant.DOCTYPE);
     		 Vector v = this.selectAllChildList("law_base_struct", "up_base_id", "base_id",src_base_id);
     		 StringBuffer sb = new StringBuffer();
     		 for (int i = 0; i < v.size(); i++) {
     			String base_id = (String)v.get(i);
    			sb.append(",'"+base_id+"'");
    			if("1".equalsIgnoreCase(basetype))
         			user_bo.saveResource(base_id, this.userView,
         					IResourceConstant.LAWRULE);
         		 if("5".equalsIgnoreCase(basetype))
         			user_bo.saveResource(base_id, this.userView,
         					IResourceConstant.DOCTYPE);
    		 }
     		 if(sb.length()>1){
	     		sql = "update law_base_struct set basetype="+basetype+" where base_id in("+sb.substring(1)+")";
	       	    dao.update(sql);
     		 }
     		 flag = "ok";
		}
        catch(Exception ex)
		{
        	ex.printStackTrace();
		}finally{
			this.getFormHM().put("flag", flag);
		}
	}
	 
	public Vector selectAllChildList(String tableName, String parentFieldName,
			String childFieldName, String nodeId) {
		Vector vct = new Vector();
		String parentId = "";
		StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + parentFieldName + "=?");
		sb.append(" and base_id <> up_base_id");// 如果采用根节点为父子相等方式，则过滤掉根节点因为根节点也不可能是谁的子结点
		ResultSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			List values=new ArrayList();
			values.add(nodeId);
			rs=dao.search(sb.toString(), values);
			ArrayList up_list=new ArrayList();
			while (rs.next())
			{
					vct.add(rs.getString(childFieldName));
					nodeId = rs.getString(childFieldName);
					up_list.add(nodeId);
		    }
			for(int i=0;i<up_list.size();i++)
			{
				nodeId=up_list.get(i).toString();
				ArrayList list=getChildIdlist(nodeId,tableName,parentFieldName,childFieldName);
				for(int r=0;r<list.size();r++)
				{
					vct.add(list.get(r));
					up_list.add(list.get(r));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				PubFunc.closeDbObj(rs);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return vct;
	}

	public ArrayList getChildIdlist(String nodeId,String tableName, String parentFieldName,String childFieldName)
    {
    	ArrayList list=new ArrayList();
    	StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + parentFieldName + "='"+nodeId+"'");
		sb.append(" and base_id <> up_base_id");// 如果采用根节点为父子相等方式，则过滤掉根节点因为根节点也不可能是谁的子结点
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		try
		{
			rs=dao.search(sb.toString());
			while(rs.next())
			{
				list.add(rs.getString(childFieldName));
			}
		}catch(Exception  e)
		{
			e.printStackTrace();
		}
		return list;
    }
}
