/*
 * Created on 2006-3-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author wxh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchAllLawBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		String base_id = (String) this.getFormHM().get("base_id");
		String basetype=(String)this.getFormHM().get("basetype");
		strsql.append("select * from law_base_struct ");
		String orgId = userView.getUserOrgId();
		String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
		if(!this.userView.isSuper_admin())
		{
		 if (base_id == null || "".equals(base_id) || "root".equals(base_id)) {
			 if (orgId == null || "".equals(orgId.trim())
						|| "null".equals(orgId.trim())) {
				 if(!"false".equals(law_file_priv.trim()))
					 strsql.append("where base_id=up_base_id");
				 else
					 strsql.append("where 1=1");
				 if(!"false".equals(law_file_priv.trim()))
					 strsql.append(" and (dir='-1' or dir is null or dir = '')");
			 }else{
				 if(!"false".equals(law_file_priv.trim()))
					 strsql.append("where base_id=up_base_id");
				 else
					 strsql.append("where 1=1");
				 if(!"false".equals(law_file_priv.trim()))
					 strsql.append(" and  (dir='-1' or dir is null or dir = '' or dir='" + orgId + "')");
			 }
		 } else {
			strsql.append("where (up_base_id='");
			strsql.append(base_id);
			strsql.append("' and base_id<>up_base_id) ");
		 }
		}else
		{
			if (base_id == null || "".equals(base_id) || "root".equals(base_id)) {
				strsql.append("where (base_id=up_base_id ");
				//if(!"false".equals(law_file_priv.trim()))
					//strsql.append(" and (dir='-1' or dir is null)");
				strsql.append(")");
			 } else {
				strsql.append("where (up_base_id='");
			    strsql.append(base_id);			
				strsql.append("' and base_id<>up_base_id) ");
			 }
		}
		if(basetype!=null&&basetype.length()>0)
			strsql.append(" and basetype='"+basetype+"' ");
		strsql.append(" order by displayorder asc");
		ArrayList lawbaselist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(strsql.toString());

			while (this.frowset.next()) {
				if(!"false".equals(law_file_priv.trim())){
					if("1".equalsIgnoreCase(basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.LAWRULE, frowset.getString("base_id")))
							continue;
					}
					if("5".equalsIgnoreCase(basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.DOCTYPE, frowset.getString("base_id")))
							continue;
					}
					if("4".equalsIgnoreCase(basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, frowset.getString("base_id")))
							continue;
					}
				}else{
					if(base_id == null || "".equals(base_id) || "root".equals(base_id)){
						if("1".equalsIgnoreCase(basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.LAWRULE, frowset.getString("base_id")))
								continue;
						}
						if("5".equalsIgnoreCase(basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.DOCTYPE, frowset.getString("base_id")))
								continue;
						}
						if("4".equalsIgnoreCase(basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, frowset.getString("base_id")))
								continue;
						}
						Vector v = selectAllParentList("law_base_struct","up_base_id", "base_id", frowset.getString("base_id"), null,this.frameconn);
						boolean flag = false;
						for (int i = 0; i < v.size(); i++) {
							String up_base_id= (String)v.get(i);
							if(up_base_id.equals(frowset.getString("base_id")))
								break;
							if("1".equalsIgnoreCase(basetype))
							{
								if (userView.isHaveResource(IResourceConstant.LAWRULE, up_base_id)){
									flag = true;
									break;
								}
							}
							if("5".equalsIgnoreCase(basetype))
							{
								if (userView.isHaveResource(IResourceConstant.DOCTYPE, up_base_id)){
									flag = true;
									break;
								}
							}
							if("4".equalsIgnoreCase(basetype))
							{
								if (userView.isHaveResource(IResourceConstant.KNOWTYPE, up_base_id)){
									flag = true;
									break;
								}
							}
						}
						if(flag)
							continue;
					}
				}
				CommonData ordervo = new CommonData(this.frowset
						.getString("base_id"), this.frowset.getString("name"));

				lawbaselist.add(ordervo);
			}
//			if (lawbaselist.size() == 0)
////				throw GeneralExceptionHandler.Handle(new GeneralException("",
////						ResourceFactory.getProperty("errors.lawbase.notnode"),
////						"", ""));

		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("lawbaselist", lawbaselist);

		}

	}

	public Vector selectAllParentList(String tableName, String parentFieldName,
			String childFieldName, String nodeId, String baseTermValue,Connection conn) {
		Vector vct = new Vector();
		String parentId = "";
		StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + childFieldName + "=?");
		boolean flg = false;// 标志baseTerm是否是默认操作
		if (baseTermValue == null || "".equals(baseTermValue.trim())) {
			flg = true;
		}
		ResultSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			List values=new ArrayList();
			  values.add(0,nodeId);
			  rs=dao.search(sb.toString(), values);
			if (rs.next())
				nodeId = rs.getString(parentFieldName);
			while (true) 
			{
				List value=new ArrayList();
				 value.add(0,nodeId);
				rs =dao.search(sb.toString(), values);
				if (!rs.next())
					break;
				vct.add(rs.getString(childFieldName));
				nodeId = rs.getString(parentFieldName);
				if (flg) {
					if (nodeId.trim().equals(
							rs.getString(childFieldName).trim())) {
						break;
					}
				} else {
					if (nodeId.trim().equals(baseTermValue.trim())) {
						break;
					}
				}
				rs.close(); //chenmengqing added at 20061010
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return vct;
	}
}
