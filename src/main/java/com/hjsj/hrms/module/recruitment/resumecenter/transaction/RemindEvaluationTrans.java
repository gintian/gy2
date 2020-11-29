package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EvaluationBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;

/***
 * 发送提示信息
 * <p>Title: RemindEvaluationTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-8-19 下午03:02:23</p>
 * @author xiexd
 * @version 1.0
 */
public class RemindEvaluationTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			//被评价人编号
			String a0100_object = (String)this.getFormHM().get("a0100_object");
			//被评价人人员库
			String nbase_object = (String)this.getFormHM().get("nbase_object");
			//职位编号
			String z0301 = (String)this.getFormHM().get("z0301");
			z0301 = PubFunc.decrypt(z0301);
			//评价人编号
			String a0100 = (String)this.getFormHM().get("a0100"); 
			//评价人人员库
			String nbase = (String)this.getFormHM().get("nbase");
			String emailItem = this.getEmailItemId();
			LazyDynaBean userInfo = this.getInfo(nbase+a0100, emailItem);
			String returnAddress = this.getReturnAddress("1007");
			//获取邮件内容
			EvaluationBo bo = new EvaluationBo(this.frameconn, this.userView);
			bo.getInfoList(PubFunc.encrypt(nbase_object),PubFunc.encrypt(a0100_object));//获取人员信息
			LazyDynaBean infoBean = bo.getTemplateInfo("7", "80", "1007", z0301);
			String title = infoBean.get("subject").toString();
			String content = infoBean.get("content").toString();
			
			EmailInfoBo infoBo = new EmailInfoBo(this.frameconn, this.userView);
			ArrayList fieldList = infoBo.getTemplateFieldInfo(1007,2);
			//替换邮件内容
			String emailTitle = "";
			String emailContent = "";
			String emailAddress = "";
			emailAddress = userInfo.get("email").toString();
			ArrayList fileList = new ArrayList();
			String userName = userInfo.get("username").toString();
			String userPwd = userInfo.get("userpwd").toString();
			ArrayList<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
			if(!"".equals(emailAddress)&&userName!=null&&!"".equals(userName))
			{				
				emailTitle = infoBo.getSysContent(title);
				emailTitle = infoBo.getFactContent(emailTitle, nbase+a0100, fieldList, this.userView, z0301);
				emailContent = infoBo.getSysContent(content);
				emailContent = infoBo.getFactContent(emailContent, nbase+a0100, fieldList, this.userView, z0301);
				
				StringBuffer evaluationValue = new StringBuffer();
				String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName+","+userPwd));
				evaluationValue.append("appfwd=1&etoken="+etoken);
				emailContent = emailContent.replace("paramEvaluationValue", evaluationValue);
				emailContent = emailContent.replace("amp;", "");
				infoBo.getEmailBean(beans, "1007", emailAddress, title, emailContent,fileList,returnAddress);
				infoBo.bulkSendEmail(beans);
//				infoBo.sendEmail(emailAddress, emailTitle, emailContent, fileList, returnAddress);
				this.getFormHM().put("msg", "");
			}else{
				this.getFormHM().put("msg", "邮箱未定义，提醒失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			StringBuffer sql = new StringBuffer("select a0101,"+emailItem+",UserName,UserPassword from "+pre+"A01 where a0100='"+a0100+"'");
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				info.set("name", rs.getString("a0101"));
				info.set("email", rs.getString(emailItem)==null?"":rs.getString(emailItem));
				info.set("username", rs.getString("UserName"));
				info.set("userpwd", rs.getString("UserPassword"));
			}
			rs.close();
		} catch (Exception e) {
			// TODO: handle exception
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
		String emailId = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailId;
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
			// TODO: handle exception
			e.printStackTrace();
		}
		return returnAddress;
	}
	
}
