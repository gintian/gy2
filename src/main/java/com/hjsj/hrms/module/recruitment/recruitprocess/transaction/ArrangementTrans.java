package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.ArrangementBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.SendEmailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ArrangementTrans extends IBusiness  {

	/**
	 * 保存面试安排
	 */
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	@Override
    public void execute() throws GeneralException {
		try {
			//获取传入的面试考官信息
			String arrangement = (String)this.getFormHM().get("arrangements");
			//面试日期
			String arrangeDate = (String)this.getFormHM().get("arrangeDate");
			//面试地点
			String arrangAddress = (String)this.getFormHM().get("arrangAddress");
			//面试者
			String a0100 = PubFunc.decrypt((String)this.getFormHM().get("a0100"));
			//面试者名字
			String a0101 = (String)this.getFormHM().get("a0101");
			//人员库
			String nbase = PubFunc.decrypt((String) this.getFormHM().get("nbase"));
			//职位
			String z0301 = PubFunc.decrypt((String)this.getFormHM().get("z0301"));
			//岗位名称
			String z0351 = (String)this.getFormHM().get("z0351");
			//需求部门
			String z0325 = (String)this.getFormHM().get("z0325");
			//流程
			String link_id = (String)this.getFormHM().get("link_id");
			//状态
			String node_id = (String)this.getFormHM().get("node_id");
			//给面试者发送邮件
			String candidateMail = (String)this.getFormHM().get("candidateMail");
			//给考官发送邮件
			String examinerMail = (String)this.getFormHM().get("examinerMail");
			//给面试者发送短信
			String candidateText = (String)this.getFormHM().get("candidateText");
			
			String []arrangements = arrangement.split("\\/");//根据特殊字符拆分每组信息
			ArrangementBo arrangementbo = new ArrangementBo(this.frameconn, this.userView);
			EmailInfoBo emailInfobo = new EmailInfoBo(this.frameconn, this.userView);
			SendEmailBo sendEmailbo = new SendEmailBo(this.frameconn, this.userView);
			//得到面试安排编号
			String z0501 = arrangementbo.getArrangementId(a0100,nbase,z0301,arrangAddress,arrangeDate,examinerMail,candidateMail,candidateText,link_id);
			arrangementbo.removeArrange(z0501);//删除面试安排旧数据
			LazyDynaBean resumeInfo = arrangementbo.getResumeInfo(a0100, nbase,z0301);
			if ("0501".equalsIgnoreCase(node_id))
				arrangementbo.updateResume(z0301, a0100, link_id);
			
			String age = SystemConfig.getPropertyValue("age"); //暂时从system获取年龄指标
            String sex = "";
            if (age == null || "".equals(age)) {
                age = "A0112";
            }
            /*
             * 只为了判断性别和年龄的指标字段
             * start
             */
            FieldItem a0112 = arrangementbo.getUsedField(age, "A01", "年龄");
            FieldItem a0107 = arrangementbo.getUsedField("A0107", "A01", "性别");
            /*
             * 只为了判断性别和年龄的指标字段
             * end
             */
            if(a0107!=null){
				CodeItem code = AdminCode.getCode(a0107.getCodesetid(), (String)resumeInfo.get("a0107"));
				if(code!=null)
					sex = code.getCodename();
            }
			age = (String)resumeInfo.get("a0112");
			ArrayList arrangementList = new ArrayList();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = dateFormat.format(new Date());
			String msg = "";
			String emailIte = ConstantParamter.getEmailField();
			if(!"".equals(arrangements[0])){
				
				for(int i=0;i<arrangements.length;i++)
				{
					arrangementList.add(JSONObject.fromObject(PubFunc.keyWord_reback(arrangements[i])));
					JSONObject fromObject = JSONObject.fromObject(PubFunc.keyWord_reback(arrangements[i]));
					String nbaseA0100 = fromObject.getString("nbasA0100").trim();
					if(nbaseA0100.endsWith(","))
						nbaseA0100 = nbaseA0100.substring(0, nbaseA0100.length()-1);
					String []examiners = nbaseA0100.split(",");
					String []c0104s = fromObject.getString("c0104").split(",");
					String []emails = fromObject.getString("email").split(",");
					String []names = fromObject.getString("name").split(",");
					for(int j=0;j<examiners.length;j++)
					{
						//将添加面试官信息
						String examiner = "";
						String nbs = "";
						String a01Id = "";
						if(!"".equals(examiners[j].trim()))
						{
							//将添加面试官信息
							examiner = PubFunc.decrypt(examiners[j].trim());
							nbs = examiner.substring(0,3);
							a01Id = examiner.substring(3, examiner.length());
						}
						String c0104 = c0104s.length==0?"":c0104s[j].trim();
						String email = emails.length==0?"":emails[j].trim();
						String name = names.length==0?"":names[j].trim();
						String start_time = fromObject.getString("start_time");
						String end_time = fromObject.getString("end_time");
						String address = fromObject.getString("address");
						arrangementbo.saveArrangement(z0501,c0104,email,start_time,end_time,address,i+1,a01Id,nbs,date,this.userView.getUserName(),this.userView.getUserFullName());
						//给面试官发送邮件信息
						if("true".equalsIgnoreCase(examinerMail))
						{	
							HashMap<String, String> sendInfo = new HashMap<String, String>();
							sendInfo.put("examiner", name);
							sendInfo.put("z0351", z0351);
							sendInfo.put("z0325", z0325);
							sendInfo.put("z0301", z0301);
							sendInfo.put("a0100", a0100);
							sendInfo.put("nbase", nbase);
							sendInfo.put("username", this.userView.getUserFullName());
							sendInfo.put("email", email);
							sendInfo.put("a0101", a0101);
							sendInfo.put("dateTime", arrangeDate+"</br>"+start_time+"-"+end_time);	//面试时间
							sendInfo.put("address", address);	//面试地址
							if(a0107!=null)	//未构库不显示
								sendInfo.put("sex", sex);	//性别
							if(a0112!=null)
								sendInfo.put("age", age);	//年龄
							LazyDynaBean emailInfo = emailInfobo.getInterviewer(sendInfo);
							String c0102 = (String)emailInfo.get("c0102");
							String title = (String)emailInfo.get("title");
							String content = (String)emailInfo.get("content");
							LazyDynaBean userInfo = getInfo(nbs+a01Id,emailIte);
							String userName = userInfo.get("username").toString();
							String userPwd = userInfo.get("userpwd").toString();
							StringBuffer evaluationValue = new StringBuffer();
							String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(userName+","+userPwd));
							evaluationValue.append("appfwd=1&etoken="+etoken);
							content = content.replace("paramEvaluationValue", evaluationValue);
							content = content.replace("amp;", "");
							msg = sendEmailbo.sendEmail(c0102, title, content);
						}
					}
					arrangementbo.updateResume(z0301, a0100, link_id);
				}
			}
			this.getFormHM().put("flg", "1");
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("flg", "2");
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
		}
		return info;
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
	
}


