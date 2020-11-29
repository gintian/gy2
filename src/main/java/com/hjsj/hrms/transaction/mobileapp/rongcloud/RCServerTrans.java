package com.hjsj.hrms.transaction.mobileapp.rongcloud;

import com.hjsj.hrms.transaction.mobileapp.utils.PhotoImgBo;
import com.hjsj.hrms.transaction.mobileapp.utils.Tools;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 融云后台服务类
 * @author imac
 */
public class RCServerTrans extends IBusiness {

	private enum TransType {
		/**获取用户信息*/
		getUserInfo,
		/**持久化融云token，不然发起未获取token的账号用户信息（头像、昵称）不显示*/
		saveRCToken
	}
	@Override
	public void execute() throws GeneralException {
		String transType = (String)this.getFormHM().get("transType");
		String succeed="false";
		try{
			if(TransType.getUserInfo.name().equals(transType)){
				 String userId = (String)this.getFormHM().get("userid");
				 String url = (String)this.getFormHM().get("url");
				 String serverid = Tools.getMACAddress();//获取租户id mac地址
				 ContentDAO dao = new ContentDAO(this.frameconn);
				 
		    		String sql = "select id from t_sys_rcim where serverid='"+serverid+"' and userid='"+userId+"'";
		    		this.frecset = dao.search(sql);
		    		String dbpre = "Usr";
		    		String a0100 = "";
		    		String a0101 = "";
		    		String  portraitUri = "";
		    		String id = "";
		    		 if (this.frecset.next())
                     {
                     	id = this.frecset.getString("id")==null?"":this.frecset.getString("id");
                     	if(id.length()>3){
	                     	dbpre = id.substring(0,3);
	                     	a0100 = id.substring(3);
                     	}
                     }
		    		 if("su".equals(id)){
		    			 url = "http://www.hjsoft.com.cn:8089";
		    			 portraitUri = "/UserFiles/Image/tixing.png";
		    			 a0101=ResourceFactory.getProperty("message.sys.send.mobile");
		    		 }else{
			    		sql = "select a0101 from " + dbpre + "a01 where a0100='" + a0100 + "'";
	                     this.frecset = dao.search(sql);
	                     if (this.frecset.next())
	                     {
	                    	 a0101 = this.frecset.getString("a0101");
	                     }
	                     
	                     portraitUri = PhotoImgBo.getPhotoPath(this.frameconn,dbpre ,a0100);
		    		 }
                     this.getFormHM().put("name", a0101);
                     this.getFormHM().put("portraitUri", url+portraitUri);
                     this.getFormHM().put("transType", transType);
                     succeed="true";
			}
			if(TransType.saveRCToken.name().equals(transType)){
				String id = (String)this.getFormHM().get("id");
				 String userId = (String)this.getFormHM().get("userid");
				 String serverid = Tools.getMACAddress();//获取租户id mac地址
				 ContentDAO dao = new ContentDAO(this.frameconn);
				 RCTokenConstant.isSaveToken(dao, serverid, id, userId);
				 succeed="true";
			}
		}catch(Exception e){
			succeed="false";
			e.printStackTrace();
		}finally{
			this.getFormHM().put("succeed", succeed);
		}
	}
	
	

}
