package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 3000000237
 * <p>Title:SaveSpResultTrans.java</p>
 * <p>Description>:SaveSpResultTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 20, 2009 10:05:53 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveSpResultTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String z0301 = (String)this.getFormHM().get("z0301");
			String a0100=(String)this.getFormHM().get("a0100");
			String sp_flag=(String)this.getFormHM().get("sp_flag");///02、03报批、批准 07驳回
			String contentSP=SafeCode.decode((String)this.getFormHM().get("contentSP"));
			String title=(String)this.getFormHM().get("title");
			String content=(String)this.getFormHM().get("content");
			String url_p=(String)this.getFormHM().get("url_p");
			String opt=(String)this.getFormHM().get("opt");
			/**=1报批给人员=4报批给用户*/
			String type=(String)this.getFormHM().get("type");
			PositionDemand pd = new PositionDemand(this.getFrameconn());
			String target="";
			String ssql="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String a0101_name="";
			String fullname="";
			if("1".equals(opt)){
				
				pd.addCurrappusername(z0301);
				pd.checkCanOperate(z0301, userView);
				if("1".equals(type)){//报批给人员
					ssql="select b0110,e0122,e01a1,A0101 from "+content.substring(0, 3)+"A01 where A0100='"+content.substring(3)+"'";
					this.frowset=dao.search(ssql);
					if(this.frowset.next()){ 
						a0101_name=this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):""; 
						if(!"".equals(a0101_name))
							target+=a0101_name;
							
					}
				}else if("4".equals(type)){//报批给业务用户
					ssql="select nbase,a0100,fullname,username from OperUser where username='"+content+"'";
					this.frowset=dao.search(ssql);
					if(this.frowset.next()){
						a0100=this.frowset.getString("a0100")!=null?this.frowset.getString("a0100"):"";
						fullname=this.frowset.getString("fullname")!=null?this.frowset.getString("fullname"):"";	
						String nbase=this.frowset.getString("nbase")!=null?this.frowset.getString("nbase"):"";	
						if(!"".equals(a0100)){
							ssql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
							this.frowset=dao.search(ssql);
							if(this.frowset.next()){
								target=this.frowset.getString("a0101");
							}
						}else if(!"".equals(fullname)){
							target=fullname;
						}else{
							target=content;
					    }
					}
						
	
				}	
					String xml="";
					if("07".equals(sp_flag)){//驳回
						String targets =pd.getRejectTarget(z0301);
						String[] tar = targets.split(":");
						target = tar[0];
						content = tar[1];
						xml=pd.createXMLTarget(this.getUserView(), contentSP, z0301, "07",target);
					}else if("02".equals(sp_flag)){//报批
						xml=pd.createXMLTarget(this.getUserView(), contentSP, z0301, "02",target);
					}else if("03".equals(sp_flag)){//批准
						xml=pd.createXMLTarget(this.getUserView(), contentSP, z0301, "03","");
					}
					pd.saveXML(z0301, xml);
					
			}
			/***/
			String toName="";
			/***sp_flag.equals("07")||驳回发邮件怎么找人啊*/
			/*报批异步发送邮件、短信*/
			if("02".equals(sp_flag)&&"2".equals(opt))
			{
				/**报批给人员*/
	    		if("1".equals(type))
	    			toName=title;
	    		/**报批给用户*/
	    		else
	    		{
	    			RecordVo vo = new RecordVo("operuser");
	    			vo.setString("username", content);
	    			vo = dao.findByPrimaryKey(vo);
	    			/**没关联人员*/
	    			if(vo.getString("a0100")==null|| "".equals(vo.getString("a0100")))
	    			{
	    				toName=content;
	    			}
	    		}
	    		String emailcontent=pd.getEmailContent(this.userView.getUserFullName(), z0301);
		    	emailcontent+="<br><br><a href='"+url_p+"hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id="+z0301+"&appfwd=1'>自动登录操作页面</a>";
		    	if("1".equals(type))
		    	{
		         	pd.sendMessage(emailcontent, content.substring(3), content.substring(0, 3),1);
		    	}
		    	else
		    	{
		    		pd.setOperuserMessage(emailcontent, content,1);
		    	}
		    	
			}
			ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, this.userView);
			StringBuffer sql = new StringBuffer();
			if("1".equals(opt)&&("02".equals(sp_flag)|| "03".equals(sp_flag))){
				RecordVo vo = new RecordVo("z03");
				vo.setString("z0301",z0301);
				vo=dao.findByPrimaryKey(vo);
				String appuser=vo.getString("appuser");
				int z0315 = vo.getInt("z0315");
				int z0313=vo.getInt("z0313");
				if(z0315==0)
				{
					z0315=z0313;
				}
				String newappuser="";
				if(appuser!=null&&!"".equals(appuser))
					newappuser=content+","+appuser;
				else
				{
					if(this.getUserView().getA0100()==null|| "".equals(this.getUserView().getA0100()))
					{
			    		newappuser=content+","+this.userView.getUserName();
					}
					else
					{
						newappuser=content+","+this.userView.getDbname()+this.userView.getA0100();
					}
				}
				
				sql.append("update z03 set z0319='"+sp_flag+"'");
				if("02".equals(sp_flag))
					sql.append(",appuser='"+newappuser+"',currappuser='"+content+"'");
				if("03".equals(sp_flag))
					sql.append(",z0315="+z0315);
				sql.append(" where z0301='"+z0301+"'");
				dao.update(sql.toString());
				//更新待办任务表
				
				if ("02".equals(sp_flag))
					zpbo.updatePendingTask(content, type);
				
				if ("03".equals(sp_flag))
					zpbo.checkZpappr();
			}
			
			
			
			/**异步驳回发送邮件*/
            if("07".equals(sp_flag))
            {
				if ("1".equals(opt)) {
					
					zpbo.updatePendingTask(content, getActortype(content));
					zpbo.checkZpappr();
				}
            	
            	if("2".equals(opt))
            		pd.rejectByLayer(z0301, userView, url_p);
            }

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 获取报批人的类型
	 * @param username 
	 * @return type 1：自助用户；2：业务用户。
	 */
	private String getActortype(String username) {
		String type = "";
		try {

			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select username from operuser where username='" + username + "'");
			if (this.frowset.next()) {
				type = "4";
			} else
				type = "1";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;

	}
}
