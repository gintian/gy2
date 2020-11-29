package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.interfaces.sys.CreateCodeXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/***
 * 
 * <p>Title: GetVirturalRoleTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  Nov 18, 2016 6:20:37 PM</p>
 * @author changxy 
 * @version 1.0
 * 初始化虚拟角色数据  codesetid 83
 */
public class GetVirturalRoleTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		RowSet set=null;
		try{
		
		String searchOrg=(String)this.getFormHM().get("searchOrg")==null?"":(String)this.getFormHM().get("searchOrg");
		if(searchOrg!=null&&!"".equals(searchOrg)&& "searchOrg".equalsIgnoreCase(searchOrg))
		{
			ContentDAO dao=new ContentDAO(this.frameconn);
			
			String sql="select codeitemdesc from organization where codeitemid='"+this.userView.getUserOrgId()+"'";
			set=dao.search(sql);
			String codeitemdesc="";
			while(set.next()){
				codeitemdesc=set.getString("codeitemdesc");
			}
			
			this.getFormHM().put("searchOrg", codeitemdesc);
			
		}else{
			String codesetid = (String)this.formHM.get("codesetid");
			String nodeid = (String)this.formHM.get("node"); // 要展开的节点id（codeitemid）
			String parentid = (String)this.formHM.get("parentid");
			parentid = parentid==null?"":parentid;
			nodeid = nodeid.replaceAll("root", "ALL");
			ArrayList treeItems = new ArrayList();
			
				String newNodeId = "ALL".equals(nodeid)&&parentid!=null&&parentid.length()>0?parentid:nodeid;
				CreateCodeXml codeXml = new CreateCodeXml(codesetid, newNodeId, null,this.userView);
				treeItems=codeXml.outCodeJSON(false,false,"false");
				
				
			
			addOrgdesc(treeItems);
		}	
		}catch(Exception e){
			e.printStackTrace();
			PubFunc.closeDbObj(set);
		}
	}
	
	/**
	 * 处理显示角色名称  ，名称后添加所属组织机构 codeitem b0110
	 * */
	public void addOrgdesc(ArrayList treeItems){
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet row=null;
		ArrayList treeList=new ArrayList();
		//处理描述描述信息
		try {
			HashMap items=new HashMap();
			String orgid=this.userView.getUserOrgId();//获取登录用户orgid
			for (int i = 0; i < treeItems.size(); i++) {
				items=(HashMap)treeItems.get(i);//qtip=代码：03
				items.put("type","1");
				String codeitemid=(String)items.get("qtip");//itemid
				String orgdesc="";
				String orgcodeitem="";
				String sql="select codeitemdesc,codeitemid from organization where codeitemid in (select b0110 from codeitem where codeitemid='"+codeitemid.split("：")[1]+"')";
				row=dao.search(sql);
				while (row.next()) {
					orgdesc=row.getString("codeitemdesc");
					orgcodeitem=row.getString("codeitemid");//codeitem 代码83 b0110 记录的操作人organization
				}
				if(this.userView.isAdmin()){//超 级用户全查
					items.put("orgdesc",orgdesc);//添加
					treeList.add(items);
				}else{//非超级用户 根据用户所属单位添加到树 否则不添加
					if(orgcodeitem.length()>orgid.length()){
						if(orgcodeitem.substring(0,orgid.length()).indexOf(orgid)>=0){
							items.put("orgdesc",orgdesc);//添加
							treeList.add(items);
						}
					}else{
						if(orgid.substring(0,orgcodeitem.length()).indexOf(orgcodeitem)>=0){
							items.put("orgdesc",orgdesc);//添加
							treeList.add(items);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		this.formHM.clear();
		this.formHM.put("org", "123");
		this.formHM.put("children", treeList);
		
	}
	
}
