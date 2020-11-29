package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeInfoSMSBo {
	private Connection conn;
    private UserView userview;
    private ContentDAO dao;
    
    public ResumeInfoSMSBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    	 this.dao = new ContentDAO(conn);
    }
    
    /**
     * 获取要发送的 人员的信息，以及信息内容
     * @param content 邮件面板里的内容
     * @param template 
     * @param sendTemplate 
     * @param a0100s
     * @param nbase
     * @return 最后一行为发送结果信息
     * @throws GeneralException
     */
    public ArrayList<LazyDynaBean> getInfo(ArrayList personParm,String sub_module,String nModule,String templateId,String nodeId, Boolean sendTemplate, String content, String not, String template) 
    		throws GeneralException{
    	RowSet search = null;
    	ArrayList destlist = new ArrayList();
    	try {
	    	//移动电话
			String phoneid = ConstantParamter.getMobilePhoneField().toLowerCase();
			phoneid = "".equals(phoneid)?"c0104":phoneid;
			LazyDynaBean dyvo= null;
			String sql = "";
			HashMap<String,String> map = new HashMap<String,String>();
			LazyDynaBean msgInfo= new LazyDynaBean();
			StringBuffer msgStr = new StringBuffer();
			StringBuffer failStr = new StringBuffer();
			int failureNum=0;//发送失败人数
			int successNum=0;//发送成功人数
			for(int i = 0;i<personParm.size();i++){
				map = (HashMap<String, String>) personParm.get(i);
				sql = "select a0101, "+phoneid+" phone_num from "+map.get("nbase")+"A01 where a0100='"+map.get("a0100")+"'";
				search = dao.search(sql);
				dyvo=new LazyDynaBean();
				while(search.next()){
					if(!StringUtils.isEmpty(search.getString("phone_num"))&&!"".equals(search.getString("phone_num"))){
						successNum++;
						dyvo.set("sender",StringUtils.isEmpty(this.userview.getUserFullName())?this.userview.getUserName():this.userview.getUserFullName());
						dyvo.set("receiver",search.getString("a0101"));
						dyvo.set("phone_num",search.getString("phone_num")==null?"":search.getString("phone_num"));
						dyvo.set("msg",this.getMsg(sub_module, nModule, templateId, nodeId, map.get("nbase")+map.get("a0100"), map.get("zp_pos_id"),sendTemplate,template));
						destlist.add(dyvo);
					}else{
						if(failureNum<4){
							failureNum++;
							failStr.append(search.getString("a0101")+"、");
						}else{
							failureNum++;
						}
					}
				}
			}
			msgStr.append(successNum+"人短信发送成功！");
			if(failureNum>0){
				failStr.setLength(failStr.length()-1);
				msgStr.append("<br>"+failStr+"等"+failureNum+"人的电话号码为空！");
			}
			msgInfo.set("msgInfo",msgStr.toString());
			destlist.add(msgInfo);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return destlist;
    }
    /***
     * 获取邮件内容（直接从数据库中获取）
     * @param template 
     * @param sendTemplate 
     * @param nmodule模板编号
     * @param sub_module子模板编号
     * @param b0110所属机构
     * @return
     */
    public String getMsg(String sub_module,String nModule,String templateId,String nodeId,String preA0100,String z0301, Boolean sendTemplate, String template){
    	EmailInfoBo emailBo = new EmailInfoBo(this.conn, this.userview);
    	LazyDynaBean bean = emailBo.getTemplateInfo(sub_module,nModule,templateId,nodeId);
    	/**得模板项目列表*/
    	ArrayList list=emailBo.getTemplateFieldInfo(Integer.parseInt(templateId),2);
    	/**得模板标题*/
    	String subject=(String)bean.get("subject");
    	subject = emailBo.getSysContent(subject);
		/**取邮件模板内容*/
    	String content=(String)bean.get("content");
    	if(sendTemplate)
    		content = template;
		/**替换系统字段**/
		subject = emailBo.getFactContent(subject, preA0100, list, this.userview,z0301);
		content = emailBo.getSysContent(content);
		content = emailBo.getFactContent(content, preA0100, list, this.userview,z0301);
		content = delHTMLTag(content);
		return content;
    }
    
    /**
     * 过滤html标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {  
    	String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式  
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式  
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
        String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符  
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
        Matcher m_script = p_script.matcher(htmlStr);  
        htmlStr = m_script.replaceAll(""); // 过滤script标签  
  
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
        Matcher m_style = p_style.matcher(htmlStr);  
        htmlStr = m_style.replaceAll(""); // 过滤style标签  
  
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
        Matcher m_html = p_html.matcher(htmlStr);  
        htmlStr = m_html.replaceAll(""); // 过滤html标签  
  
        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);  
        Matcher m_space = p_space.matcher(htmlStr);  
//        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签  
        htmlStr = htmlStr.replaceAll("&nbsp;", " "); 
        htmlStr = htmlStr.replaceAll("\\s+", " "); 
        return htmlStr.trim(); // 返回文本字符串  
    }  
}
