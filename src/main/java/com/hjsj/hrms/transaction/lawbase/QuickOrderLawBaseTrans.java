/*
 * Created on 2006-3-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:QuickOrderLawBaseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 8, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class QuickOrderLawBaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		String base_id = (String) this.getFormHM().get("base_id");
		base_id = PubFunc.decrypt(SafeCode.decode(base_id));
		String basetype=(String)this.getFormHM().get("basetype");
		String order_name = (String)this.getFormHM().get("order_name");
		String order_type = (String)this.getFormHM().get("order_type");
		String orgId = userView.getUserOrgId();// 用来保存机构ID
		CommonBusiness comnbus = new CommonBusiness(getFrameconn());
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String order_by = " order by law_base_file.";
		try {
			String orgTerm = "basetype=" + basetype;
			String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
			if(!"false".equals(law_file_priv.trim())){
			if(!this.userView.isSuper_admin())
			{
				if(orgId==null||orgId.length()<=0)
				{
					orgTerm=orgTerm+ " and (dir = '' or dir = '-1' or dir is null)";
					
				}else
				{
					LawDirectory lawDirectory=new LawDirectory();
					String orgsrt=lawDirectory.getOrgStrs(orgId,"UN",this.getFrameconn());
					orgTerm=orgTerm+ " and (dir = '' or dir = '-1' or dir in (" + orgsrt
					+ ") or dir is null )";
					//orgTerm=orgTerm+ " and (dir = '"+orgId+"')";
				}
				
			}
			}
			/*StringBuffer sqlBuffer = new StringBuffer(
					"select base_id from law_base_file");
			if(base_id!=null&&!base_id.equalsIgnoreCase(""))
				sqlBuffer.append(" where base_id = "+base_id);
			sqlBuffer.append(order_by);
			sqlBuffer.append(order_name+" "+order_type);*/
			
			if("5".equalsIgnoreCase(basetype)&&!"false".equals(law_file_priv.trim())){
				//String unitid = userView.getUnit_id();
				String unitid = userView.getUserOrgId();
				//String unitids[] = unitid.split("`");
				String org = " and (b0110 in (";
				//String b0110 = "";
				String b0110 = unitid;
				/*for(int i=0;i<unitids.length;i++){
					if(unitids[i].length()>0)
					if(unitids[i].substring(0,2).equalsIgnoreCase("UN")){
						b0110=unitids[i].substring(2);
						org += "'"+b0110+"',";
					}
				}*/
				if(!"".equalsIgnoreCase(b0110)){
					//org = org.substring(0,org.length()-1)+")";
					org+="'"+unitid+"')";
				}
				if(!this.userView.isSuper_admin()){
					if(!"".equalsIgnoreCase(b0110)){
						frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,this.userView)+org+" or b0110 is null) "+order_by+order_name+" "+order_type);
					}else{
						frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,this.userView)+" and b0110 is null "+order_by+order_name+" "+order_type);
					}
				}else
					frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,this.userView)+order_by+order_name+" "+order_type);
			}else
				frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,this.userView)+order_by+order_name+" "+order_type);
			//String ss = comnbus.findLawbaseFile(base_id, orgId,orgTerm)+order_by+order_name+" "+order_type;
			//frowset = dao.search(ss);
			//RecordVo vo=new RecordVo("law_base_file");
			int i=0;
			while (this.frowset.next()) {
				/*if(basetype.equalsIgnoreCase("1"))
				{
					if (!userView.isHaveResource(IResourceConstant.LAWRULE, frowset.getString("base_id")))
						continue;
				}
				if(basetype.equalsIgnoreCase("5"))
				{*/
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset
							.getString("file_id")))
					continue;
				}
				//}
				String sql = "update law_base_file set fileorder ="+(i+1)+" where file_id = '"+this.frowset.getString("file_id")+"'";
				dao.update(sql);
		 		i++;
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
	}

}
