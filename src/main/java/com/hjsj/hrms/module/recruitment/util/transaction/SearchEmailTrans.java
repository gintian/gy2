package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.module.recruitment.util.EmailInfoBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/****
 * 查询邮件信息
 * <p>
 * Title: SearchEmailTrans
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-6-3 上午09:46:52
 * </p>
 * 
 * @author xiexd
 * @version 1.0
 */
public class SearchEmailTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	RowSet rs = null;
    	RowSet search = null;
        try {
            /**
             * sub_module 子模块编号 10（接受职位申请通知） 11 拒绝职位申请通知 20 面试安排通知（申请人） 30
             * 面试安排通知（面试官） 40 面试通知（通过） 50 面试通知（淘汰） 60 Offer 70 入职通知（管理人员）（暂缓）
             **/
            String sub_module = (String) this.getFormHM().get("sub_module");
            String nModule = (String) this.getFormHM().get("nModule");// 模块编号
            // 1：人员；2：薪资发放；5：薪资审批；7：招聘管理（新）
            String b0110 = ((String) this.getFormHM().get("b0110") != null) ? (String) this.getFormHM().get("b0110") : "";// 所属机构
            String z0301 = (String) this.getFormHM().get("z0301");// 职位编号
            String a0100 = (String) this.getFormHM().get("a0100s");

            String title = ((String) this.getFormHM().get("title") != null) ? (String) this.getFormHM().get("title") : "";
            //进行的操作
            String functionStr = (String) this.getFormHM().get("function_str");
            String template = (String) this.getFormHM().get("template");
           
            RecruitPrivBo privBo = new RecruitPrivBo(); 

            if (StringUtils.isEmpty(a0100)) {
                this.getFormHM().put("info", "没有符合职位的简历！");
                return;
            }
            
            EmailInfoBo bo = new EmailInfoBo(frameconn, userView);
            String emailAddress = bo.getEmailItemId();
            String[] z0301s = z0301.split(",");
            	for(int i = 0;i<z0301s.length;i++){
            		boolean hasPosPriv = privBo.hasPosPriv(this.frameconn, userView, PubFunc.decrypt(z0301s[i]));
            		if(!hasPosPriv){
            			if(z0301s.length>1){
            				this.formHM.put("hasPosPriv", "部分职位没有权限，操作失败");
            				return;
            			}else{
            				this.formHM.put("hasPosPriv", "没有该职位的权限，操作失败");
            				return;
            			}
            		}
            	}
            	
            /** 得到前台传入邮件模板Id ***/
            String id = (String) this.getFormHM().get("id");
            String[] a0100s = a0100.split(",");// 人员编号集合
            /** 获取当前业务模板下所有邮件模板集合拼接字符串 **/
            String nodeId = "";
            String link_id = "";
            ContentDAO dao = new ContentDAO(this.frameconn);
            if("90".equalsIgnoreCase(nModule)||"50".equals(nModule)||"40".equals(nModule)||"60".equals(nModule)) {
        		nodeId = (String) this.getFormHM().get("node_Id");
        		link_id = (String) this.getFormHM().get("link_Id");
                String linkname = "";
                ArrayList<String> value = new ArrayList<String>();
                value.add(link_id);
                search = dao.search("SELECT CUSTOM_NAME FROM zp_flow_links WHERE id=?",value);
                if(search.next()){
                	linkname = search.getString("custom_name");
                }
                this.formHM.put("linkname", linkname);
            }
            //获取阶段名称
            String titlename = "";
            if("50".equals(nModule)||"40".equals(nModule)){
            	String sql = "select custom_name from zp_flow_links where id=?";
            	ArrayList<String> value = new ArrayList<String>();
            	value.add(link_id);
            	rs = dao.search(sql, value);
            	while(rs.next()){
            		titlename = rs.getString("custom_name");
            	}
            	title = titlename+"-"+title;
            }
            String templateList = bo.getTemplateList(sub_module, nModule, StringUtils.isEmpty(nodeId)?"":nodeId.substring(0, 2));
            LazyDynaBean bean = bo.getTemplateInfo(sub_module, nModule, id, StringUtils.isEmpty(nodeId)?"":nodeId.substring(0, 2));
            /** 得到邮件模板Id ***/
            String templateId = ((String) bean.get("id")) == null ? "0" : ((String) bean.get("id"));
            /** 得模板项目列表 */
            ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
            /** 得模板标题 */
            String subject = (String) bean.get("subject") == null ? "" : ((String) bean.get("subject"));
            RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
            
            String a0100temp = PubFunc.decrypt(a0100s[0]);
            a0100temp = dbname+a0100temp;
            if(StringUtils.isNotEmpty(subject)) {            
                subject = bo.getSysContent(subject);
                subject = bo.getFactContent(subject, a0100temp, list, this.userView, PubFunc.decrypt(z0301s[0]));
            }
            if(StringUtils.isNotEmpty(template)) {
           	 /** 替换系统字段 **/
	           	template = bo.getSysContent(template);
	           	template = bo.getFactContent(nModule,template, a0100temp, list, this.userView, PubFunc.decrypt(z0301s[0]));
	            this.getFormHM().put("content", template);
	           	return;
           }
            /** 取邮件模板内容 */
            String content = (String) bean.get("content") == null ? "" : ((String) bean.get("content"));
            String templateContent = content;
            /** 替换系统字段 **/
            if(StringUtils.isNotEmpty(content)) {         
                content = bo.getSysContent(content);
                content = bo.getFactContent(nModule,content, a0100temp, list, this.userView, PubFunc.decrypt(z0301s[0]));
            }
            content = content+"\n ";
            /*** 得到邮件附件头 **/
            String fileColumn = bo.getFileColumn();
            /*** 得到邮件附件列表内容 ***/
            String fileContentList = bo.getFileContentList(templateId);
            HashMap hm = new HashMap();
            hm.put("email", emailAddress.trim());
            hm.put("nbase", dbname);
            hm.put("a0100", PubFunc.decrypt(a0100s[0]));
            String c0102 = "";
            if(StringUtils.isEmpty(emailAddress) || "#".equalsIgnoreCase(emailAddress))
                c0102 = "";
            else
                c0102 = bo.getEmailAddress(hm);// 查询邮箱地址
            
            this.getFormHM().put("fileColumn", fileColumn);
            this.getFormHM().put("fileContentList", fileContentList);
            this.getFormHM().put("sub_module", sub_module);
            this.getFormHM().put("nModule", nModule);
            this.getFormHM().put("b0110", b0110 == null ? "" : b0110);
            this.getFormHM().put("c0102", c0102);
            this.getFormHM().put("subject", subject);
            this.getFormHM().put("content", content);
            this.getFormHM().put("template", templateContent);
            this.getFormHM().put("templateList", templateList);

            //接受、拒绝和其他状态
            if ("10".equalsIgnoreCase(nModule))
                this.formHM.put("status", "1");
            else if ("11".equalsIgnoreCase(nModule))
                this.formHM.put("status", "2");
            else
            	this.formHM.put("status", nodeId);
            
            String statueJson = "[]";
            
            if("changeStatus".equalsIgnoreCase(functionStr)){
            	statueJson = getStatus(link_id, nodeId);
            }else if("passChoice".equalsIgnoreCase(functionStr)){
            	statueJson = getStatusLink(link_id);
            }
            
            this.getFormHM().put("functionStr", functionStr);
            this.getFormHM().put("statueJson", statueJson);
            
            String phone = ConstantParamter.getMobilePhoneField();
            if(StringUtils.isEmpty(phone))
                this.getFormHM().put("phoneFlag", "false");
            String email = ConstantParamter.getEmailField();
            if(StringUtils.isEmpty(email))
                this.getFormHM().put("emailFlag", "false");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(search);
        }
    }

    private String getStatus(String linkId, String statusId) {
        if(StringUtils.isEmpty(linkId))
            return "[]";

        StringBuffer statusJson = new StringBuffer("[");
        try{
            ContentDAO dao = new ContentDAO(this.frameconn);
            int seq = 0;
            //获取当前环节中选中的第一份简历的状态的序号
            if(StringUtils.isNotEmpty(statusId)) {
                if(statusId.indexOf("`") > -1)
                    statusId = statusId.substring(0, statusId.indexOf("`"));
                
                String statusSql = "SELECT STATUS,CUSTOM_NAME,SEQ FROM ZP_FLOW_STATUS WHERE LINK_ID=?"
                    + " AND VALID='1' AND STATUS=? ORDER BY SEQ";
                ArrayList<String> value = new ArrayList<String>();
                value.add(linkId);
                value.add(statusId);
                this.frowset = dao.search(statusSql, value);
                if (this.frowset.next())
                    seq = this.frowset.getInt("SEQ");
            }
            
            //用于判断是否需要检测当前环节中的状态是否要改为选中，=true：需要；=false：不需要；默认=true
            boolean flag = true;
            //获取当前环节中的所有状态，并拼接为json格式的字符串
            String sql = "SELECT STATUS,CUSTOM_NAME,SEQ FROM ZP_FLOW_STATUS WHERE LINK_ID=? AND VALID='1' ORDER BY SEQ";
            ArrayList<String> value = new ArrayList<String>();
            value.add(linkId);
            this.frowset = dao.search(sql, value);
            while(this.frowset.next()){
                statusJson.append("{ boxLabel: '" + this.frowset.getString("CUSTOM_NAME") + "',");
                statusJson.append("name: 'rb', inputValue: '" + this.frowset.getString("STATUS") + "'");
                int newSeq = this.frowset.getInt("SEQ");
                //当查到的状态的序号大于当前状态的序号且flag值为true时，将该状态置为选中状态并且将flag的值改为false
                if(seq < newSeq && flag) {
                    statusJson.append(",checked:true");
                    flag = false;
                }
                
                statusJson.append(" },");
            }
            
            if(statusJson.toString().endsWith(","))
                statusJson.setLength(statusJson.length() - 1);
            
            statusJson.append("]");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return statusJson.toString();
    }
    private String getStatusLink(String linkId) {
    	if(StringUtils.isEmpty(linkId))
    		return "[]";
    	
    	StringBuffer statusJson = new StringBuffer("[");
    	try{
    		String sql = "select flow_id from zp_flow_links where id=?";
    		ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList<String> value = new ArrayList<String>();
			value.add(linkId);
			this.frowset = dao.search(sql, value);
			String flowid = "";
			if(this.frowset.next())
				flowid = this.frowset.getString("flow_id");
			
			sql = "select custom_name,id,node_id from zp_flow_links where flow_id="+flowid+" and valid='1' order by seq";
			this.frowset = dao.search(sql);
			RecruitProcessBo bo = new RecruitProcessBo(this.frameconn,this.userView);
			//是否必须按流程环节进行
			String skipFlag = bo.getSkipFlag(flowid);
			ArrayList stageList =(ArrayList)userView.getHm().get("stageList");
			String nextLinkId=bo.getNextLinkId(linkId,stageList);
			String lastLinkId = bo.getLastLinkId(linkId, stageList);
    		while(this.frowset.next()){
    			statusJson.append("{ boxLabel: '" + this.frowset.getString("custom_name") + "',");
    			if("1".equals(skipFlag)&&!nextLinkId.equals(this.frowset.getString("id"))&&!lastLinkId.equals(this.frowset.getString("id")))
    				statusJson.append("disabled:true,");
    			statusJson.append("name: 'rb', inputValue: '" + this.frowset.getString("id") +"/"+this.frowset.getString("node_id")+ "'");
    			if(nextLinkId.equals(this.frowset.getString("id"))){
    				statusJson.append(",checked: true");
    			}
    			statusJson.append("},");
    		}
    		
    		if(statusJson.toString().endsWith(","))
    			statusJson.setLength(statusJson.length() - 1);
    		
    		statusJson.append("]");
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	return statusJson.toString();
    }
}
