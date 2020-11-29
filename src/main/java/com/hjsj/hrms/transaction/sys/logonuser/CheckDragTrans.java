package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 验证用户管理拖动节点
 * @author xujian
 *Apr 6, 2010
 */
public class CheckDragTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg = "";
		boolean istogroup=false;//标示被拖动到用户组还是用户节点
		try{
			String fromid=(String)this.getFormHM().get("fromid");
			String toid=(String)this.getFormHM().get("toid");
			if(fromid.equals(toid)){
				return;
			}
			{
				String sql = "select groupname from usergroup where groupname='"+toid+"'";
				ContentDAO dao = new ContentDAO(this.frameconn);
				this.frecset = dao.search(sql);
				if(this.frecset.next()){
					istogroup=true;
				}
				sql = "select groupname,groupid from usergroup where groupname='"+fromid+"'";
				this.frecset = dao.search(sql);
				if("root".equalsIgnoreCase(fromid)){
					msg=ResourceFactory.getProperty("error.usergroup.fromroot.msg");
				}else{
					if(this.frecset.next()){//拖动的是用户组节点
						//msg=ResourceFactory.getProperty("error.usergroup.group.msg");
						int groupid=this.frecset.getInt("groupid");
						if(groupid==1){//拖动超级用户组
							msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
						}
						if("root".equals(toid)){//把用户组拖动到根节点
						}else{
							if(istogroup){
								sql = "select groupid from usergroup where groupname='"+toid+"' and groupid=1";
								this.frecset = dao.search(sql);
								if(this.frecset.next()){//把用户组拖动到超级用户组不允许
									//msg=ResourceFactory.getProperty("error.usergroup.groupgroup.msg");
									msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
								}
							}else{
								sql = "select username from operuser where username='"+toid+"' and roleid=0 and groupid=1";
								this.frecset = dao.search(sql);
								if(this.frecset.next()){//把用户组拖动到超级用户组不允许
									//msg=ResourceFactory.getProperty("error.usergroup.groupgroup.msg");
									msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
								}
							}
						}
					}else{//拖动的是用户节点
						sql = "select groupid from operuser where username='"+fromid+"' and groupid=1 and roleid=0";
						this.frecset = dao.search(sql);
						if(this.frecset.next()){//拖动的是超级用户组下的用户
							//msg=ResourceFactory.getProperty("error.usergroup.fromsupgroup.msg");
							msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
						}
						if(istogroup){
							sql = "select groupid from usergroup where groupname='"+toid+"' and groupid=1";
							this.frecset = dao.search(sql);
							if(this.frecset.next()){//拖动到超级用户组下的用户
								//msg=ResourceFactory.getProperty("error.usergroup.tosupgroup.msg");
								msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
							}
						}else{
							sql = "select groupid from operuser where username='"+toid+"' and groupid=1 and roleid=0";
							this.frecset = dao.search(sql);
							if(this.frecset.next()){//拖动到超级用户组下的用户
								//msg=ResourceFactory.getProperty("error.usergroup.tosupgroup.msg");
								msg=ResourceFactory.getProperty("error.usergroup.supgroup.msg");
							}
						}
						if("root".equals(toid)){
							msg=ResourceFactory.getProperty("error.usergroup.root.msg");
						}else{
							
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", SafeCode.encode(msg));
			this.getFormHM().put("istogroup", new Boolean(istogroup));
		}
	}
}
