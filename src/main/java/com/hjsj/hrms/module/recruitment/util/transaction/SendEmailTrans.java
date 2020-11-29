package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.WxServiceMessage;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.ResumeInfoSMSBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.util.FeedBackBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
/**
 * 邮件发送类
 * <p>Title: SendEmailTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-6-3 下午04:51:04</p>
 * @author xiexd
 * @version 1.0
 */
public class SendEmailTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String msg = "";
			String content = (String)this.getFormHM().get("content");
			String notice = content;
			boolean sendTemplate = (Boolean)this.getFormHM().get("sendTemplate");//按修改以后的模板发送
			String template = "";
			/**取邮件模板内容*/
        	if(sendTemplate){//替换模板
        		template = (String)this.getFormHM().get("template");                    		
        	}
			String subject = (String)this.getFormHM().get("title");
			String templateId = (String)this.getFormHM().get("templateId");
			String sub_module = (String)this.getFormHM().get("sub_module");
			String nModule = (String)this.getFormHM().get("nModule");//模块编号     1：人员；2：薪资发放；5：薪资审批；7：招聘管理（新）
			String z0301 = ((String)this.getFormHM().get("z0301")!=null)?(String)this.getFormHM().get("z0301"):"";//职位编号
			String a0100 = (String)this.getFormHM().get("a0100");
			String sendEmailInfo = ((String)this.getFormHM().get("sendEmailInfo")!=null)?(String)this.getFormHM().get("sendEmailInfo"):"1";
			String feedCheck = ((String)this.getFormHM().get("feedCheck")!=null)?(String)this.getFormHM().get("feedCheck"):"0";
			String sendNotice = ((String)this.getFormHM().get("sendNotice")!=null)?(String)this.getFormHM().get("sendNotice"):"0";
			String sendEmail = ((String)this.getFormHM().get("sendEmail")!=null)?(String)this.getFormHM().get("sendEmail"):"";
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
        	String pre="";  //应聘人员库
			if(vo!=null)
				pre=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			String[] z0301s ={};
			String [] a0100s= a0100.split(",");
			if("sendEmail".equals(sendEmail) && z0301.split(",").length == 1){
				z0301s = new String[a0100s.length];
				for(int k = 0;k<a0100s.length;k++){
					z0301s[k] = z0301;
				}
			}else{
				z0301s = z0301.split(",");
			}
			StringBuffer userName = new StringBuffer();
			EmailInfoBo emailBo = new EmailInfoBo(frameconn, userView);
			/**得到模板邮件邮件附件列表***/
	        ArrayList fileList = emailBo.getAttachFileName(templateId);
	        String emailAddress = emailBo.getEmailItemId();
        	int failureNum=1;
        	int successNum=0;
        	HashMap hm = new HashMap();
        	ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
			
			String nodeId = "";
			ArrayList<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
			//存放微信消息
			ArrayList wxMesList = new ArrayList();
	        if(a0100s.length>0){
	            if("90".equalsIgnoreCase(nModule)) {
	            	nodeId = (String)this.getFormHM().get("node_id");
	                if(nodeId != null && nodeId.length() > 2)
	                    nodeId = nodeId.substring(0, 2);
	            }
	            
	            ArrayList<String> value = new ArrayList<String>();
	        	LazyDynaBean bean = emailBo.getTemplateInfo(sub_module,nModule,templateId,nodeId);
	        	String returnAddress = (String)bean.get("return_address");
	        	
    			String userNo=PubFunc.decrypt(a0100s[0]);
    			String c0102_emile = "";
    			if(StringUtils.isNotEmpty(emailAddress) && !"#".equalsIgnoreCase(emailAddress)) {
    			    hm.put("email", emailAddress.trim());
    			    hm.put("nbase", pre.trim());
    			    hm.put("a0100", userNo.trim());
    			    c0102_emile = emailBo.getEmailAddress(hm);//查询邮箱地址
    			}
    			
    			value.add(content);
    			value.add(userNo.trim());
    			value.add(pre.trim());
    			value.add(PubFunc.decrypt(z0301s[0]));
    			valueList.add(value);
    			if("7".equals(sub_module)) {
    				HashMap<String, String> map = new HashMap<String, String>();
    				map.put("guidkey", getGuidKey(pre,userNo));
    				map.put("posName", getPosName(PubFunc.decrypt(z0301s[0])));
    				String resumeState = "";
    				CodeItem code = AdminCode.getCode("36",(String)this.getFormHM().get("node_id"));
    				if(code!=null)
    					resumeState = code.getCodename();
    				if("10".equalsIgnoreCase(nModule))
						resumeState = "接受申请";
    				else if("11".equalsIgnoreCase(nModule))
    					resumeState = "拒绝申请";
    				map.put("resumeState", resumeState);
    				map.put("operateTime", DateUtils.format(new Date(), "yyyy.MM.dd-HH:mm:ss"));
    				map.put("infoTitle", "您的职位申请有新的通知！");
    				map.put("infoDetail", "请及时查收通知邮件或登录PC端页面查看消息详情！");
    				wxMesList.add(map);
    			}
    			
        		if(StringUtils.isNotEmpty(c0102_emile)&&!"undefined".equalsIgnoreCase(c0102_emile)){
        		    if("1".equalsIgnoreCase(sendEmailInfo))
        		    	msg = emailBo.getEmailBean(beans, templateId, c0102_emile, subject, content,fileList,returnAddress);
        		}
	        	/**得模板项目列表*/
	        	ArrayList list=emailBo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
	        	/**得模板标题*/
	        	subject=(String)bean.get("subject");
	        	subject = emailBo.getSysContent(subject);
	        	String msgstr="";
	        	if("1".equals(msg))//判断第一次发送是否成功
	        		successNum++;
	        	else{
	        		userName.append(emailBo.getUserName(pre+PubFunc.decrypt(a0100s[0])));
    				failureNum++;
	        	}
	        	//从第二个人开始发送
	        	for(int i=1;i<a0100s.length;i++)
	        	{
	        		String  preA0100 = pre+PubFunc.decrypt(a0100s[i]);
	        		userNo=PubFunc.decrypt(a0100s[i]);
	    			if(StringUtils.isNotEmpty(emailAddress) && !"#".equalsIgnoreCase(emailAddress)) {
	    			    hm.put("email", emailAddress);
	    			    hm.put("nbase", pre);
	    			    hm.put("a0100", userNo);
	    			    c0102_emile = emailBo.getEmailAddress(hm);//查询邮箱地址
	    			}
	    			
	    			/**取邮件模板内容*/
                	if(sendTemplate)//替换模板
                		content = template;
                	else
                		content=(String)bean.get("content");
                		
	    			content = emailBo.getSysContent(content);
	    			content = emailBo.getFactContent(content, preA0100, list, this.userView,PubFunc.decrypt(z0301s[i]));
	    			value = new ArrayList<String>();
	    			value.add(content);
	    			value.add(userNo.trim());
	    			value.add(pre.trim());
	    			value.add(PubFunc.decrypt(z0301s[i]));
	    			valueList.add(value);
	    			if("7".equals(sub_module)) {
	    				HashMap<String, String> map = new HashMap<String, String>();
	    				map.put("guidkey", getGuidKey(pre,userNo));
	    				map.put("posName", getPosName(PubFunc.decrypt(z0301s[0])));
	    				String resumeState = "";
	    				CodeItem code = AdminCode.getCode("36",(String)this.getFormHM().get("node_id"));
	    				if(code!=null)
	    					resumeState = code.getCodename();
	    				if("10".equalsIgnoreCase(nModule))
    						resumeState = "接受申请";
	    				else if("11".equalsIgnoreCase(nModule))
	    					resumeState = "拒绝申请";
	    				map.put("resumeState", resumeState);
	    				map.put("operateTime", DateUtils.format(new Date(), "yyyy.MM.dd-HH:mm:ss"));
	    				map.put("infoTitle", "您的职位申请有新的通知！");
	    				map.put("infoDetail", "请及时查收通知邮件或登录PC端页面查看消息详情！");
	    				wxMesList.add(map);
	    			}
	        		if(c0102_emile.length()>0&&!"undefined".equalsIgnoreCase(c0102_emile))
	        		{
	        			successNum++;
	        			/**替换系统字段**/
	        			subject = emailBo.getFactContent(subject, preA0100, list, this.userView,PubFunc.decrypt(z0301s[i]));
	        			
	        			if("1".equalsIgnoreCase(sendEmailInfo))
	        				msg = emailBo.getEmailBean(beans, templateId, c0102_emile, subject, content,fileList,returnAddress);
	        		}else {
	        			if(failureNum>4)
	        			{
	        				msgstr = userName.toString()+"等"+failureNum+"人";
	        				failureNum++;
	        			}else{
	        				if(userName.length()!=0)
	        					userName.append("、");
	        				
	        				userName.append(emailBo.getUserName(preA0100));
	        				failureNum++;
	        			}
	        		}
	        	}
	        	String bulkSendEmail = emailBo.bulkSendEmail(beans);
	        	if(!"".equals(bulkSendEmail))
	        		msg = bulkSendEmail;
	        	
	        	FeedBackBo bo = new FeedBackBo(this.frameconn);
	        	if("1".equalsIgnoreCase(feedCheck))
	        	    bo.updateFeedBack(valueList);
	        	    
	        	if(failureNum>5)
	        		userName = new StringBuffer(msgstr);
	        	
	        }
	        //如果有发邮件、反馈信息或者短信任何一个都要发微信消息
	        WxServiceMessage wx = new WxServiceMessage(this.frameconn);
	        if(wx.canSysSendMessage()&&("1".equalsIgnoreCase(sendEmailInfo)||"1".equalsIgnoreCase(feedCheck)||"1".equals(sendNotice))) {
	        	wx.send(wxMesList);
	        }
	        
	        if("1".equalsIgnoreCase(sendEmailInfo)) {	        	
	        	msg = successNum+"人邮件发送成功！";
	        	if(userName.length()>0)
	        		msg += "<br>"+userName+"的邮箱地址为空！";
	        }
	        
	        if("1".equalsIgnoreCase(feedCheck)) {
	            if(StringUtils.isNotEmpty(msg))
	                msg += "<br>";
	            
                msg += valueList.size()+"人反馈信息保存成功！";
	        }
	        /*
			 * 发送短信 start
			 */
	        if("1".equals(sendNotice)){
				ArrayList personParm = getPersonList(a0100s,pre,z0301s);
				ResumeInfoSMSBo infobo = new ResumeInfoSMSBo(frameconn, userView);
				ArrayList<LazyDynaBean> destlist = infobo.getInfo(personParm,sub_module, nModule, templateId, nodeId,sendTemplate,notice,"",template);
				ArrayList<LazyDynaBean> sendlist = new ArrayList<LazyDynaBean>();
				sendlist.addAll(destlist.subList(0, destlist.size()-1));
				SmsBo smsbo=new SmsBo(this.getFrameconn());
				if(sendlist!=null&&sendlist.size()!=0)
					smsbo.batchSendMessage(sendlist);
				
				if(StringUtils.isNotEmpty(msg))
                    msg += "<br>";
				msg += (String) destlist.get(destlist.size()-1).get("msgInfo");
	        }
			/*
			 * end
			 */
			this.getFormHM().put("msg", msg);
		} catch (GeneralException e) {
		    this.getFormHM().put("msg", e.getErrorDescription());
		} catch (Exception e) {
		    e.printStackTrace();
		    this.getFormHM().put("msg", e.getMessage());
		}
	}
	private ArrayList getPersonList(String[] a0100s, String pre, String[] z0301s) {

    	ArrayList personParm = new ArrayList();
    	HashMap<String,String> map = new HashMap<String,String>();
        try {
            String a0100 = "";
            String zp_pos_id = "";
            for (int i = 0; i < a0100s.length; i++) {
                a0100 = PubFunc.decrypt(a0100s[i]);
                zp_pos_id = PubFunc.decrypt(z0301s[i]);

                if (StringUtils.isEmpty(zp_pos_id))
                    continue;

            	map = new HashMap<String,String>();
            	map.put("nbase", pre);
            	map.put("a0100", a0100);
            	map.put("zp_pos_id", zp_pos_id);
            	personParm.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
		return personParm;
	}
	
	/**
     * @param tablename
     * @param i9999
     * @param bMain 是否主集
     * @return
     */
    private String getGuidKey(String nbase, String a0100)
    {
        String guid="";
        RowSet frowset=null;
        try{
        	String tablename = nbase+"A01";
        	ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();
            
            sWhere.append(" where a0100 ='");
            sWhere.append(a0100);
            sWhere.append("'");
            sb.append("select guidkey from ");
            sb.append(tablename);     
            sb.append(sWhere.toString());   
            
            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (StringUtils.isEmpty(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString(); 
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);   
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");                    
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");   
                    dao.update(stmp.toString());                

                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");             
                    }
                }
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        } finally {
        	PubFunc.closeResource(frowset);
        }
        return guid;
     }
    
    /**
     * 获取职位名称
     * @param z0301
     * @return
     * @throws SQLException 
     */
    private String getPosName(String z0301) throws SQLException {
    	RowSet rs = null;
    	String z0351 = "";
    	try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList<String> value = new ArrayList<String>();
			value.add(z0301);
			rs = dao.search("select z0351 from z03 where z0301=?", value);
			if(rs.next())
				z0351 = rs.getString("z0351");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return z0351;
    }

}
