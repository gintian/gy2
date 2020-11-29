package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EvaluationBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeEvaluationBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.util.SendEmailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/***
 * 发送邀请评价交易类
 * <p>Title: SendInvitationEmailTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-7 下午04:10:43</p>
 * @author xiexd
 * @version 1.0
 */
public class SendInvitationEmailTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			//被评价人编号
			String a0100 = (String)this.getFormHM().get("a0100");
			//被评价人人员库
			String nbase = (String)this.getFormHM().get("nbase");
			//职位编号
			String z0301 = (String)this.getFormHM().get("z0301");
			z0301 = PubFunc.decrypt(z0301);
			//邮件模板编号
			String templateId = (String)this.getFormHM().get("id"); 
			//受邀请人员编号
			String userId = (String)this.getFormHM().get("userId");
			//邮件标题
			String title = (String)this.getFormHM().get("title");
			//邮件内容
			String content = (String)this.getFormHM().get("content");
			
			String[] userIds = userId.split("`");
			ArrayList preA0100 = new ArrayList();
			for(int i=0;i<userIds.length;i++)
			{
				preA0100.add(PubFunc.decrypt(userIds[i]));
			}
			EmailInfoBo infoBo = new EmailInfoBo(this.frameconn, this.userView);
			SendEmailBo sendBo = new SendEmailBo(this.frameconn, this.userView);
			String fromAddr=sendBo.getFromAddr();
			String msg = "";
			if(fromAddr==null|| "".equals(fromAddr.trim()))
			{
				msg="系统未设置邮件服务器！";
				this.getFormHM().put("msg", msg);
				return;
			}
			
			//替换邮件内容
			String emailTitle = "";
			String emailContent = "";
			LazyDynaBean infoBean = new LazyDynaBean();
			ArrayList fieldList = infoBo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
			
			String emailItem = this.getEmailItemId();
			if(StringUtils.isEmpty(emailItem)){
				this.getFormHM().put("msg", "邮箱指标没有定义！");
				return;
			}
			String returnAddress = this.getReturnAddress(templateId);
			String emailAddress = "";
			String userName ="";
			String userPwd = "";
			String failName = "";
			int successNum = 0;
			int failureNum = 0;
			ArrayList fileList = new ArrayList();
			HashMap map = new HashMap();
	    	StringBuffer url = new StringBuffer(this.userView.getServerurl());
	    	url.append("/recruitment/resumecenter/evaluationresume.do?b_search=link");
	    	url.append("&z0301="+z0301);
	    	EvaluationBo evbo = new EvaluationBo(this.frameconn, this.userView);
	    	evbo.getInfoList(nbase, a0100);
	    	ArrayList list = (ArrayList)this.userView.getHm().get("emailInfoList");
	    	ArrayList<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
			for(int i=1;i<preA0100.size();i++)
			{
				emailTitle = infoBo.getSysContent(title);
				emailTitle = infoBo.getFactContent(emailTitle, preA0100.get(i).toString(), fieldList, this.userView, z0301);
				emailContent = infoBo.getSysContent(content);
				emailContent = infoBo.getFactContent(emailContent, preA0100.get(i).toString(), fieldList, this.userView, z0301);
				//获取当前人员部分基本信息
				infoBean = this.getInfo(preA0100.get(i).toString(), emailItem);
				emailAddress = infoBean.get("email").toString();
				userName = infoBean.get("username").toString();
				userPwd = infoBean.get("userpwd").toString();
				ResumeEvaluationBo bo = new ResumeEvaluationBo(this.frameconn, this.userView);
				
				if(!"".equals(emailAddress)&&userName!=null&&!"".equals(userName))
				{
					String pre=preA0100.get(i).toString().substring(0,3);
					String userNo=preA0100.get(i).toString().substring(3);
					String[] a0100s = a0100.split(",");
					String[] nbases = nbase.split(",");
					for(int j=0;j<a0100s.length;j++)
					{						
						map = new HashMap();
						map.put("nbase_object",PubFunc.decrypt(nbases[j] ));
						map.put("a0100_object",PubFunc.decrypt(a0100s[j] ));
						map.put("score", "");
						map.put("content","" );
						map.put("nbase", pre);
						map.put("a0100", userNo);
						map.put("flg", "1");
						if(this.isInEvaluation(map))
						{
							continue;
						}else{							
							bo.addEvaluation(map);
						}
					}
					StringBuffer evaluationValue = new StringBuffer();
					String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName+","+userPwd));
					evaluationValue.append("appfwd=1&etoken="+etoken);
					emailContent = emailContent.replace("amp;", "");
					emailContent = emailContent.replace("paramEvaluationValue", evaluationValue);
					msg = infoBo.getEmailBean(beans, templateId, emailAddress, emailTitle, emailContent,fileList,returnAddress);
//					infoBo.sendEmail(emailAddress, emailTitle, emailContent, fileList, returnAddress);
					successNum++;
					
					//给被邀请者发送每个需要评价的简历邀请通知推送
					Boolean flag = false;
					for(int j=0;j<list.size();j++)
			    	{
						LazyDynaBean bean = (LazyDynaBean) list.get(j); 
			    		StringBuffer url_new = new StringBuffer();
			    		url_new.append("&nbase_o="+PubFunc.encrypt(bean.get("nbase").toString()));
			    		url_new.append("&a0100_o="+PubFunc.encrypt(bean.get("a0100").toString()));
			    		url_new.append("&"+evaluationValue);
			    		String urls = url.toString()+url_new.toString();
			    		String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
						if(corpid!=null&&corpid.length()>0)//推送微信公众号  wangjl  add 2015-5-5
							flag = WeiXinBo.sendMsgToPerson(infoBean.get("name").toString(),title,this.getContentForWX(bean.get("a0101").toString()),"",urls);
						
						String dd_corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
						if(dd_corpid!=null&&dd_corpid.length()>0)//推送钉钉  wangjl add 2017-6-1
							DTalkBo.sendMessage(infoBean.get("name").toString(), title, this.getContentForWX(bean.get("a0101").toString()), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", urls);
			    	}
					
				}else{
					if(failureNum>3)
					{
						if(failureNum==4)
							msg = failName+infoBean.get("name").toString();
					}else{						
						failName+=infoBean.get("name").toString()+"、";
					}
					failureNum++;
				}
			}
			infoBo.bulkSendEmail(beans);
			if(msg.length()==0&&failName.length()>0)
			{
				msg = failName.substring(0, failName.length()-1);
			}
			if(failName.length()>0)
	        {	        	
	        	msg = "共"+successNum+"人邀请成功！<br>"+msg+"等"+failureNum+"人的邮箱或账户信息为空！";
	        }else{
	        	msg = "已全部发送邀请！";
	        }
			this.getFormHM().put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

	/***
	 * 查询当前发送用户的姓名和邮箱
	* @Title:getInfo
	* @Description：
	* @author xiexd
	* @param preA0100
	* @param emailItem
	* @return
	 */
	public LazyDynaBean getInfo(String preA0100,String emailItem)
	{
		LazyDynaBean info = new LazyDynaBean();
		try {
			String pre=preA0100.substring(0,3);
			String a0100=preA0100.substring(3);
			LazyDynaBean bean = this.getUserInfo();
			String userName = (String)bean.get("name");
			String UserPassword = (String)bean.get("pwd");
			StringBuffer sql = new StringBuffer("select a0101,"+emailItem+","+userName+","+UserPassword+" from "+pre+"A01 where a0100='"+a0100+"'");
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				info.set("name", rs.getString("a0101"));
				info.set("email", rs.getString(emailItem)==null?"":rs.getString(emailItem));
				info.set("username", rs.getString(userName)==null?"":rs.getString(userName));
				info.set("userpwd", rs.getString(UserPassword)==null?"":rs.getString(UserPassword));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	/***
	 * 获取邮件地址指标
	* @Title:getEmailItemId
	* @Description：
	* @author xiexd
	* @return
	 */
	public String getEmailItemId()
	{
		String emailField = ConstantParamter.getEmailField();
		return emailField;
	}
	
	/***
	 * 获取用户名和密码
	* @Title:getUserInfo
	* @Description：
	* @author xiexd
	* @return
	 */
	public LazyDynaBean getUserInfo()
	{
		LazyDynaBean bean = new LazyDynaBean();
		String name = ConstantParamter.getLoginUserNameField();
		String pwd = ConstantParamter.getLoginPasswordField();
		if(!"username".equalsIgnoreCase(name))
		{
			FieldItem item = DataDictionary.getFieldItem(name,"A01");
			if(item==null)
			{
				name = "username";
			}
		}
		if(!"UserPassword".equalsIgnoreCase(pwd))
		{
			FieldItem item = DataDictionary.getFieldItem(pwd,"A01");
			if(item==null)
			{
				pwd = "UserPassword";
			}
		}
		bean.set("name", name);
		bean.set("pwd", pwd);
		return bean;
	}
	
	/****
	 * 
	 * 当前邮件模板的邮件返回地址
	* @Title:getReturnAddress
	* @Description：
	* @author xiexd
	* @param id
	* @return
	 */
	public String getReturnAddress(String id)
	{
		String returnAddress = "";
		try {
			String sql = "select return_address from email_name where id="+id;
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql);
			if(rs.next())
			{
				returnAddress = rs.getString("return_address")==null?"":rs.getString("return_address");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnAddress;
	}
	
	/****
	 * 查询当前人员是否有评论记录
	* @Title:isInEvaluation
	* @Description：
	* @author xiexd
	* @param map
	* @return
	 */
	public Boolean isInEvaluation(HashMap map)
	{
		Boolean flg = false;
		try {
			StringBuffer sql = new StringBuffer("select id,nbase_object,a0100_object,nbase,a0100,score,content,eval_time,create_user,create_fullname,create_time from zp_evaluation ");
			sql.append("where  nbase_object=? and a0100_object=?   and nbase=? and a0100=?");
			ArrayList value = new ArrayList();
			value.add(map.get("nbase_object"));
			value.add(map.get("a0100_object"));
			value.add(map.get("nbase"));
			value.add(map.get("a0100"));
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql.toString(), value);
			if(rs.next())
			{
				flg = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flg;
	}
	
	/***
	 * 被邀请人姓名
	* @Title:getContentForWX
	* @Description：
	* @author xiexd
	* @param userName
	* @return
	 */
	private String getContentForWX(String userName){
		return this.userView.getUserFullName()+"邀请您参加"+userName+"的简历评价。";
	}
}
