package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppearPositionDemandTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String model=(String)hm.get("model");
		String z0301=(String)hm.get("z0301");	
		String title=(String)hm.get("title");
		String content=(String)hm.get("content");
		String moreLevelSP=(String)hm.get("moreLevelSP");
		String url_p=(String)hm.get("url_p");
		String type=(String)hm.get("type");
		String isSendMessage=(String)this.getFormHM().get("isSendMessage");
		String userName="";
		String passWord="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    PositionDemand pd = new PositionDemand(this.getFrameconn());
	    String opt=(String) hm.get("opt");
	    
		this.getFormHM().put("z0301", z0301);
		this.getFormHM().put("moreLevelSP", moreLevelSP);
		this.getFormHM().put("isSendMessage", isSendMessage);
		this.getFormHM().put("content", content);
		this.getFormHM().put("type", type);
		this.getFormHM().put("title", title);
		this.getFormHM().put("z0301", z0301);
		String target="";
		String b0110_name="";//单位
		String e0122_name="";//部门
		String e01a1_name="";//岗位
		String a0101_name="";//人员
		String a0100="";
		String fullname="";
		try
		{
			String ssql="";
			if("1".equals(opt)){
				if("1".equals(type)){//报批给人员
					ssql="select b0110,e0122,e01a1,A0101 from "+content.substring(0, 3)+"A01 where A0100='"+content.substring(3)+"'";
					this.frowset=dao.search(ssql);
					if(this.frowset.next()){
	//					b0110_name=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
	//					e0122_name=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
	//					e01a1_name=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
						a0101_name=this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"";
	//					b0110_name=AdminCode.getCodeName("UN",b0110_name);
	//					e0122_name=AdminCode.getCodeName("UM",e0122_name);
	//					e01a1_name=AdminCode.getCodeName("@K",e01a1_name);
	//					if(!b0110_name.equals(""))
	//						target+=b0110_name+"/";
	//					if(!e0122_name.equals(""))
	//						target+=e0122_name+"/";
	//					if(!e01a1_name.equals(""))
	//						target+=e01a1_name+"/";
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
			}	
			String[] arr = z0301.split(",");
			ArrayList list = new ArrayList();
			
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				/**opt=2 报批异步发送邮件、减少客户等待时间**/
			   if("2".equals(opt)){
					if("1".equals(moreLevelSP))
					{
						/**currappuser字段，如果是业务用户，存用户名，否则存NBASE+A0100*/
						if(isSendMessage==null|| "".equals(isSendMessage)||(isSendMessage!=null&& "0".equalsIgnoreCase(isSendMessage))){//默认情况下未发送邮件，如果未选择发送短信则发送邮件=0发短信=1发短信
							LazyDynaBean bean=this.getUserName1(content,type);
							userName=(String)bean.get("username");
							passWord=(String)bean.get("password");
							
							//pd.updateCurrAppUser(arr[i],content);
							String emailcontent=pd.getEmailContent(this.userView.getUserFullName(), arr[i]);
							emailcontent+="<br><br><a href='"+url_p+"hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id="+arr[i]+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName+","+passWord))+"'>自动登录操作页面</a>";
							/**自助用户*/
							if("1".equals(type))
					    	{
					         	pd.sendMessage(emailcontent, content.substring(3), content.substring(0, 3),1);
					    	}
							/**业务用户*/
					    	else
					    	{
					    		pd.setOperuserMessage(emailcontent, content,1);
					    	}
						}
						if(isSendMessage!=null&& "1".equalsIgnoreCase(isSendMessage)){//短信发送通知
							//pd.updateCurrAppUser(arr[i],content);
							String emailcontent=pd.sendMessageContent(this.userView.getUserFullName(), arr[i]);
							if("1".equals(type))
					    	{
								pd.sendShortMessage(emailcontent, content.substring(3), content.substring(0, 3), 1, this.userView.getUserFullName());
					    	}else{
					    		pd.sendShortMessage(emailcontent, content, this.userView.getDbname(), 2, this.userView.getUserFullName());
					    	}
						}
					}
			   }	
			    //String xml=pd.createXML(this.getUserView(), "", arr[i], "02");
				//pd.saveXML(arr[i], xml);
			   if("1".equals(opt)){
				    if(moreLevelSP!=null&& "1".equals(moreLevelSP)){//如果是多级审批那么只有当前操作人才能驳回或者报批审批的权限
							/**查看当前用户是否是选中记录的当前操作人员 **/
							pd.checkCanOperate(arr[i], userView);  
				    }
					String xml=pd.createXMLTarget(this.getUserView(), "", arr[i], "02",target);
					pd.saveXML(arr[i], xml);///记录审批流程
					RecordVo vo = new RecordVo("z03");
					vo.setString("z0301",arr[i]);
					vo=dao.findByPrimaryKey(vo);
					String appuser=vo.getString("appuser");
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
					String currAppUserName = this.getCurreAppUserName(content,type);
					String sql="update z03 set z0319='02' ,appuser='"+newappuser+"',currappuser='"+content+"' ,currappusername='"+currAppUserName+"'";
					sql+=" where z0301='"+arr[i]+"'";
					//dao.update(sql);
					list.add(sql);
			   }
			}
			dao.batchUpdate(list);
			if ("1".equals(opt)) {
				// 更新待办任务表
				ZpPendingtaskBo bo = new ZpPendingtaskBo(this.frameconn, this.userView);
				bo.updatePendingTask(content, type);
				bo.checkZpappr();
			}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	
	}
	/**
	 * 查询当前操作用户名
	 * @param currAppUser
	 * @return
	 * @throws GeneralException 
	 */
	private String getCurreAppUserName(String currAppUser,String type) throws GeneralException{
		String currAppUserName="";//当前操作用户名
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if("1".equals(type)){//人员 
			String base = currAppUser.substring(0, 3);//前缀
			String a0100 = currAppUser.substring(3, 11);
			String sql = "select A0101 from "+base+"A01 where A0100 = '"+a0100+"'";
			try {
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					currAppUserName = this.frowset.getString("A0101");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}else if("4".equals(type)){//业务用户
			String sql = "select UserName, FullName from OperUser where UserName='"+currAppUser+"'";
			try {
				this.frowset  = dao.search(sql);
				while(this.frowset.next()){
					String name = this.frowset.getString("FullName");
					//业务用户如果有用户名FullName则显示用户名，否则显示userName
					if("".equals(name)||name== null){
						currAppUserName = currAppUser;
					}else{
						currAppUserName = name;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		
		return currAppUserName;
		
	}
	//解决自动登录dumeilong
	public LazyDynaBean getUserName1(String str,String type){
		if(str==null||str.length()==0){
			return null;
		}
		LazyDynaBean resc=null;
		StringBuffer sql=new StringBuffer();
		if("1".equals(type)){
			String nbase=str.substring(0, 3);
			String a0100=str.substring(3);
			AttestationUtils utils=new AttestationUtils();
			LazyDynaBean abean=utils.getUserNamePassField();
			String username_field=(String)abean.get("name");
			String password_field=(String)abean.get("pass");
			sql.append("select a0101,"+ username_field+" username,"+password_field+" password,a0101 from " +nbase +"A01" +" where a0100='"+a0100+"'");
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			if(rs!=null&&rs.size()>0){
			    	resc=(LazyDynaBean)rs.get(0);	    	
			}
			 return resc;
		}else{
			sql.append("select password from operuser where username='"+str+"'");
			List rs=ExecuteSQL.executeMyQuery(sql.toString());
			if(rs!=null&&rs.size()>0){
			    	resc=(LazyDynaBean)rs.get(0);
			    	resc.set("username", str);
			}
			return resc;
		}
		
	}
	
}
