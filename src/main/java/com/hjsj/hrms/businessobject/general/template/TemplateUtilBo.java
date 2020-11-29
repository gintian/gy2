/**
 * <p>Title:TemplateUtilBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-5-11 上午11:36:33</p>
 * <p>@version: 7.x</p>
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateOptionField;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mortbay.util.ajax.JSON;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/**
 * @author Administrator
 *
 */
public class TemplateUtilBo {
	private static Logger log =  Logger.getLogger(TemplateUtilBo.class);
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
    private Boolean isEmail_staff_value=false;//抄送本人，默认false.true时给微信发消息menuid为20，false时微信发消息menuid为2
	public TemplateUtilBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userView = userview;
		dao = new ContentDAO(this.conn);
	}
	public Boolean getIsEmail_staff_value() {
		return isEmail_staff_value;
	}

	public void setIsEmail_staff_value(Boolean isEmail_staff_value) {
		this.isEmail_staff_value = isEmail_staff_value;
	}
    /**   
     * @Title: getUserNamePassword   
     * @Description:  获取自助用户名密码  
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return LazyDynaBean 
     * @author:wangrd   
     * @throws   
    */
    public LazyDynaBean getUserNamePassword(String nbase,String a0100)
    {
        if(nbase==null||nbase.length()<=0)
        {
            return null;
        }
        AttestationUtils utils=new AttestationUtils();
        LazyDynaBean fieldbean=utils.getUserNamePassField();
        String username_field=(String)fieldbean.get("name");
        String password_field=(String)fieldbean.get("pass");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
        sql.append(" where a0100='"+a0100+"'");
        List rs=ExecuteSQL.executeMyQuery(sql.toString());
        
        LazyDynaBean rec=null;
        if(rs!=null&&rs.size()>0)
        {
            rec=(LazyDynaBean)rs.get(0);            
        }
        return rec;
    } 
    /** 
    * @Title: sendWeixinMessageFromEmail 
    * @Description:发送微信信息 从邮箱格式中获取微信信息 
    * @param @param emailList
    * @return void
    */ 
    public void sendWeixinMessageFromEmail(List emailList){   
    	String corpid = (String) ConstantParamter.getAttribute("wx","corpid");
    	String dd_corpid=(String) ConstantParamter.getAttribute("DINGTALK","corpid");
    	if ((corpid==null || "".equals(corpid))&&(dd_corpid==null || "".equals(dd_corpid))){
    		return;
    	}
    	
		try {
			for (int i = 0, len = emailList.size(); i < len; i++) {
				LazyDynaBean emailBean = (LazyDynaBean) emailList.get(i);
				String usrname = (String) emailBean.get("usrname");
				if (usrname==null || usrname.length()<1) {
					String usrId = (String) emailBean.get("objectId");
					if (usrId != null && usrId.length() > 0) {
						String nbase = usrId.substring(0, 3);
						String a0100 = usrId.substring(3);
						LazyDynaBean abean = getUserNamePassword(nbase, a0100);
						if (abean != null && abean.get("username") != null) {
							usrname = (String) abean.get("username");
						}
					}
				}
	
				if (usrname==null || usrname.length()<1) {
                    continue;
                }
				String title = (String) emailBean.get("subject");
				String description = (String) emailBean.get("bodyText");
				description=description.replace("<br/>", "\n").replace("&nbsp;", " ").replace("<br />", "\n");//bug 44015 钉钉消息html代码不被翻译
				if (description==null) {
                    description="";
                }
				String url = (String) emailBean.get("href");
				log.info("template_href="+url);
				if (url==null) {
                    url="";
                }
			    if (url.length()>0){
			        url=getWeinXinUrl(url);
		        }
				String imgUrl = (String) emailBean.get("imgUrl");
				if (imgUrl==null)
				{
					//bug 32911 微信业务办理小助手收到的审批提醒没有图片是个空白的。
					String urlPath= SystemConfig.getPropertyValue("w_selfservice_url");
					if(urlPath!=null&&urlPath.trim().length()>0&&urlPath.indexOf("w_selfservice")==-1){
						urlPath+="/w_selfservice";
					}
					if(urlPath==null||(urlPath!=null&&urlPath.trim().length()==0)) {
                        urlPath="http://www.hjsoft.com.cn:8089/w_selfservice";
                    }
				    imgUrl=urlPath+"/UserFiles/Image/tongzhi.png";
				}
				if(corpid!=null&&corpid.trim().length()>0) {
                    WeiXinBo.sendMsgToPerson(usrname, title, description, imgUrl, url);
                }
				if(dd_corpid!=null&&dd_corpid.trim().length()>0) {
                    DTalkBo.sendMessage(usrname, title, description, imgUrl, url);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /** 
     * 微信url所需格式：url ="http://192.192.102.200:8080/w_selfservice/module/selfservice/index.jsp?
     * etoken=bGlxLA%3D%3D&menuid=2&tabid=3&isedit=1&taskid=16738&insid=3587&frommessage=0&objectid=#";
     * 
     * 邮件中的格式：http://127.0.0.1:8888/general/template/edit_form.do?
 b_query=link&businessModel=0&tabid=2&pre_pendingID=HRMS-673&validatepwd=false&ins_id=30
 5&taskid=1Gwj0CIcM9I@3HJD@&sp_flag=1&returnflag=12&appfwd=1&isemail=true&etoken=bHpj
 LA%3D%3D"
     * @Title: getWeinXinUrl 
     * @Description:获得微信url，从发送邮箱的url获取,所需参数与邮箱url稍不同
     * @param @param emailUrl 从邮箱中获取url
     * @return void
     */ 
     private String getWeinXinUrl(String emailUrl){  
        String weixinURL = "";
        try {
            weixinURL = SystemConfig.getPropertyValue("w_selfservice_url");
            log.info("w_selfservice_url="+weixinURL);
            if (weixinURL == null) {
                weixinURL = "";
            }
            if (weixinURL.length() > 0) {
                weixinURL = weixinURL  + "/w_selfservice/module/selfservice/index.jsp?";
            }
            Pattern pattern = Pattern.compile("[0-9]+");  //20141219 dengcan 汉口银行某种场景下传来的task_id是加密的 
            if (weixinURL.length() > 0) {
                String tabId = "";
                String taskId = "";
                String insId = "";
                String etoken = "";
                String sp_flag = "";
                String[] arrUrl = emailUrl.split("&");
                for (int j = 0; j < arrUrl.length; j++) {
                    String param = arrUrl[j];
                    String[] arrParam = param.split("=");
                    if (arrParam.length == 2) {
                        String name = arrParam[0];
                        String value = arrParam[1];
                        if("param".equalsIgnoreCase(name))
                        {
                        	  String param_value=SafeCode.decode(value);
                        	  String[] param_arrUrl = param_value.split("&");
                        	  for (int e = 0; e < param_arrUrl.length; e++) {
                                  String _params = param_arrUrl[e];
                                  String[] arrParamValue = _params.split("=");
                                  if (arrParamValue.length == 2) {
                                      String _name = arrParamValue[0];
                                      String _value = arrParamValue[1];
                                      if ("tab_id".equalsIgnoreCase(_name)) {
                                          tabId = _value;
                                      } else if ("task_id".equalsIgnoreCase(_name)) {
                                    	  _value = PubFunc.decrypt(_value);
                                          taskId = _value;
                                      } else if ("ins_id".equalsIgnoreCase(_name)) {
                                          insId = _value;
                                          
                                      }  
                                  }
                        	  } 
                        } 
                        else if ("tabid".equalsIgnoreCase(name)) {
                            tabId = value;
                        } else if ("ins_id".equalsIgnoreCase(name)) {
                            insId = value;
                        } else if ("task_id".equalsIgnoreCase(name)) {
                        	    //微信代办通知打不开。 原因：参数改变，taskid变成task_id,加密改成不加密了。这里同步一下。 guodd 2016-12-24
                            //value = PubFunc.decryption(value);
                        	if(!pattern.matcher(value).matches()) {
                                taskId = PubFunc.decryption(value);
                            } else {
                                taskId = value;
                            }
                        } else if ("taskid".equalsIgnoreCase(name)) {
                        	if(!pattern.matcher(value).matches()) {
                                taskId = PubFunc.decryption(value);
                            } else {
                                taskId = value;
                            }
                        } else if ("etoken".equalsIgnoreCase(name)) {
                            etoken = value;
                        } else if ("sp_flag".equalsIgnoreCase(name)) {
                            sp_flag = value;
                        }
                    }
                }
                String idEdit = "0";
                if ("1".equals(sp_flag)) {
                    idEdit = "1";
                }
                String menuid="2";
                /*if(this.isEmail_staff_value){//农大微信抄送本人修改链接。
                	menuid="20";
                }*/
                weixinURL = weixinURL + "etoken=" + etoken + "&menuid="+menuid+"&tabid=" + tabId + "&isedit=" + idEdit 
                + "&taskid=" + taskId;
                if(insId.length()>0) {
                    weixinURL+= "&insid=" + insId;
                }
                weixinURL+="&frommessage=0&objectid=#";
            }
            log.info("weixinURL="+weixinURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weixinURL;
     }
    /**
     * @Title: sendWeixinMessageFromEmail
     * @Description:发送微信信息 从邮箱格式中获取微信信息
     * @param
     * @param emailBean
     * @return void
     */ 
    public void sendWeixinMessageFromEmail(LazyDynaBean emailBean){  
		try {
			ArrayList list = new ArrayList();
			list.add(emailBean);
			sendWeixinMessageFromEmail(list);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**检查模板显示方式 为了提高效率 单独处理
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	public String getTemplateView(int tabid)  throws GeneralException
	{
		String view="list";	
		/*
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,name ,ctrl_para from template_table where tabid='");
		strsql.append(tabid);
		strsql.append("'");
		RowSet rset=null; 
		try
		{
			ContentDAO dao=new ContentDAO(conn);			
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{			
		        String ctrl_para=Sql_switcher.readMemo(rset,"ctrl_para");			
				if(ctrl_para!=null && ctrl_para.trim().length()>0){		
					Document doc=null;
					Element element=null;
					String xpath="/params/init_view";
					XPath findPath = XPath.newInstance(xpath);
					StringReader reader=new StringReader(ctrl_para);
					doc=saxbuilder.build(reader);
					List childlist=findPath.selectNodes(doc);	
					if(childlist!=null&&childlist.size()>0)
					{
						element=(Element)childlist.get(0);
						if(element.getAttribute("view")!=null)
							view=(String)element.getAttributeValue("view");
					}
				} 
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
	        if(rset!=null){
	            try {
	            	rset.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
	        }
	    }
		
		*/
		RecordVo tabvo=TemplateStaticDataBo.getTableVo(tabid, conn); //20171111 邓灿，采用缓存解决并发下压力过大问题
		String ctrl_para=tabvo.getString("ctrl_para");
		try
		{
			if(ctrl_para!=null && ctrl_para.trim().length()>0){		
					Document doc=null;
					Element element=null;
					String xpath="/params/init_view";
					XPath findPath = XPath.newInstance(xpath);
					doc=PubFunc.generateDom(ctrl_para);;
					List childlist=findPath.selectNodes(doc);	
					if(childlist!=null&&childlist.size()>0)
					{
						element=(Element)childlist.get(0);
						if(element.getAttribute("view")!=null) {
                            view=(String)element.getAttributeValue("view");
                        }
					}
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return view;
	}
    
	
	
	/**
	 * 取得业务模板的内容
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	public static RecordVo readTemplate(int tabid,Connection conn)throws GeneralException
	{
		RecordVo tab_vo=TemplateStaticDataBo.getTableVo(tabid, conn); //20171111 邓灿，采用缓存解决并发下压力过大问题
		/*
		RecordVo tab_vo=new RecordVo("Template_table");
    	ContentDAO dao=new ContentDAO(conn);
    	RowSet rowSet=null;
    	try
    	{
    	
    		String sql="select template_table.tabid,template_table.name,template_table.noticeid,template_table.gzstandid,template_table.flag";
    		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) //昆仑数据库static为系统关键字
    			sql+=",template_table.\"static\"";
    		else	
    			sql+=",template_table.static";
    		sql+=",template_table.paperori,template_table.paper,template_table.tmargin,template_table.bmargin,template_table.rmargin,template_table.lmargin,template_table.paperw,template_table.paperh";
    		sql+=",template_table.operationcode,template_table.operationname,template_table.factor,template_table.lexpr,template_table.llexpr,template_table.userfalg,template_table.username,template_table.userflag,template_table.sp_flag,template_table.dest_base,template_table.content,template_table.ctrl_para from template_table  WHERE template_table.tabid=?";
    		rowSet=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
    		if(rowSet.next())
    		{
    			tab_vo.setInt("tabid",rowSet.getInt("tabid"));
    			tab_vo.setString("name",rowSet.getString("name"));
    			tab_vo.setString("noticeid",rowSet.getString("noticeid"));
    			tab_vo.setString("gzstandid",rowSet.getString("gzstandid"));
    			tab_vo.setInt("flag",rowSet.getInt("flag"));
    			tab_vo.setInt("static",rowSet.getInt("static"));
    			tab_vo.setString("operationcode",rowSet.getString("operationcode"));
    			tab_vo.setString("operationname",rowSet.getString("operationname")); 
    			tab_vo.setString("factor",Sql_switcher.readMemo(rowSet,"factor"));
    			tab_vo.setString("lexpr",Sql_switcher.readMemo(rowSet,"lexpr"));
    			tab_vo.setString("llexpr",Sql_switcher.readMemo(rowSet,"llexpr")); 
    			tab_vo.setString("userfalg",rowSet.getString("userfalg"));
    			tab_vo.setString("username",rowSet.getString("username"));
    			tab_vo.setString("userflag",rowSet.getString("userflag"));
    			tab_vo.setString("sp_flag",rowSet.getString("sp_flag"));
    			tab_vo.setString("dest_base",rowSet.getString("dest_base"));
    			tab_vo.setString("content",Sql_switcher.readMemo(rowSet,"content"));
    			tab_vo.setString("ctrl_para",Sql_switcher.readMemo(rowSet,"ctrl_para"));
    			tab_vo.setInt("paperori",rowSet.getInt("paperori"));
    			tab_vo.setInt("paper",rowSet.getInt("paper"));
    			tab_vo.setDouble("tmargin",rowSet.getFloat("tmargin"));
    			tab_vo.setDouble("bmargin",rowSet.getFloat("bmargin"));
    			tab_vo.setDouble("rmargin",rowSet.getFloat("rmargin"));
    			tab_vo.setDouble("lmargin",rowSet.getFloat("lmargin"));
    			tab_vo.setDouble("paperw",rowSet.getFloat("paperw"));
    			tab_vo.setDouble("paperh",rowSet.getFloat("paperh"));
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		PubFunc.closeDbObj(rowSet);
    	}
		*/
		return tab_vo;
	}
	//解析并且格式化审批意见。
	public static ArrayList formatOptionFiledValue(String optionFieldValue) {
		ArrayList<String> valueList = new ArrayList<String>();
		if(StringUtils.isBlank(optionFieldValue)){
			return valueList;
		}
		optionFieldValue=optionFieldValue.replace("\\n", "\n").replace("\r\n", "\n").replace("\r", "\n").replace("\n\n","\n");
		String[] rowValue = optionFieldValue.split("\n");// 根据\n分隔审批意见
		int optionFormatType = 0;
		int rowIndex = 0;// 记录每个节点的行数
		int nodeIndex = 0;// 记录是第几个节点，区分是申请节点还是审批节点
		TemplateOptionField optionFile = null;// 定义审批意见对象
		for (int row = 0; row < rowValue.length; row++) {
			String rowInfo = rowValue[row];
			if(StringUtils.isBlank(rowInfo.trim())){
				rowIndex++;
				continue;
			}
			if ((rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer")))||(
				rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver2"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime"))	)) {
				
				if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer"))){
					nodeIndex=0;
				}
				if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver2"))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime"))	){
					nodeIndex=1;
				}
				/**
				 * 申请人:xxx 申请时间:xxx 意见:同意 
				 * 批注: 
				 * 审批人:xxx 审批时间:xxx 意见:同意 
				 * 批注:
				 **/
				optionFormatType = 1;
				rowIndex=0;
			}
			else if (((!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime")))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer")))||(
				(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime")))
					&& (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionAgree"))||rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionDisagree")))&&(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"))))) {
				if((!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.applicationtime")))
					&& rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer"))){
					nodeIndex=0;
				}
				if(
				(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approvertime")))
					&& (rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionAgree"))||rowInfo.contains(ResourceFactory.getProperty("format.optionfield.opinionDisagree")))&&(!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation")))){
					nodeIndex=1;
				}
				/**
				 * 申请人： xxx 2017-03-09 09:23 
				 * xxx意见：同意 xxx 2017-03-09 21:52
				 * 批注：不同意
				 */
				optionFormatType = 2;
				rowIndex=0;
			}
			else if(rowInfo.contains("(")&&rowInfo.contains(")：")){				
				/**
				 * 总部/研发中心/项目研发部(申请人)： 
				 * 王俊琪 2018-08-13 13:45
				 * 
				 * 总部/研发中心/项目研发部(审批人)： 
				 * 同意 王建华 2018-08-13 13:56 批注：通过
				 */
				if(row+1< rowValue.length){
					String nextRowInfo=rowValue[row+1];
					if(nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fill"))||nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.fillopinion"))){
						/**
						 * 总部/研发中心/项目研发部(申请人)：
						 *  员工填写 
						 * 王俊琪   2018-08-13 13:45
						 * 
						 * 总部/研发中心/项目研发部(审批人)：
						 *  部门领导填写 
						 *  同意 王建华 2018-08-13 13:56 
						 *  批注：通过
						 */
						if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.proposer1"))){
							nodeIndex=0;
						}
						if(rowInfo.contains(ResourceFactory.getProperty("format.optionfield.approver1"))){
							nodeIndex=1;
						}
						optionFormatType = 4;
					}else{
						if(nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.agree"))||nextRowInfo.contains(ResourceFactory.getProperty("format.optionfield.disagree"))){
							nodeIndex=1;
						}else{
							nodeIndex=0;
						}
						optionFormatType = 3;
					}
				}else{
					optionFormatType = 3;
				}
				rowIndex=0;
			}
			if(rowIndex==0){
				if(row!=0){
					if(optionFile!=null){
						valueList.add(JSON.toString(optionFile.changeObjectToMap()));
						optionFile = new TemplateOptionField();
					}
				}
			}
			if(optionFile==null){
				optionFile = new TemplateOptionField();
			}
			switch (optionFormatType) {
				case 1: {
					String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据去得每行中每列数据
					for (int col = 0; col < colValue.length; col++) {// 循环一行没列的值
						String colInfo = colValue[col];
						String[] itemInfo = colInfo.split(":",2);
						if (rowIndex % 2 == 0) {// 奇数行第一列是审批人、审批时间、审批意见
							switch (col) {
								case 0: {
									optionFile.setApproverType(itemInfo[0]);// 申请人或审批人
									optionFile.setApproverName(itemInfo[1]);// 姓名
									break;
								}
								case 1: {
									optionFile.setApprovalTime(itemInfo[1]);// 审批时间
									break;
								}
								case 2: {
									optionFile.setApproverType(itemInfo[1]);// 同意、不同意
									break;
								}
							}
						} else {// 偶数行是批注
							optionFile.setApproverAnnotation(itemInfo[1]);// 批注
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 2: {
					String[] colValue = rowInfo.split("\\s+");// 用空格分割每行数据
					for (int col = 0; col < colValue.length; col++) {
						String colInfo = colValue[col];
						String[] itemInfo = colInfo.split(":",2);
						if (nodeIndex == 0) {// 第一行固定是申请人信息
							switch (col) {
								case 0: {
									optionFile.setApproverType(itemInfo[0]);// 申请人
									break;
								}
								case 1: {
									optionFile.setApproverName(itemInfo[0]);// 申请人姓名
									break;
								}
								case 2: {
									optionFile.setApprovalTime(itemInfo[0]);// 申请时间
									break;
								}
							}
						} else {
							if (!rowInfo.contains(ResourceFactory.getProperty("format.optionfield.annotation"))) {// 如果不包含批注说明是审批人、审批意见、审批时间记录行。
								switch (col) {
									case 0: {
										optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
										optionFile.setApproverRole(itemInfo[0]
												.replace(ResourceFactory.getProperty("format.optionfield.opinion"), ""));// 节点名称
										optionFile.setApproverOpinion(itemInfo[1]);// 同意、不同意
										break;
									}
									case 1: {
										optionFile.setApproverName(itemInfo[0]);// 审批人姓名
										break;
									}
									case 2: {
										optionFile.setApprovalTime(itemInfo[0]);// 审批时间
										break;
									}
								}
							} else {// 批注信息
								if (itemInfo.length > 0) {
									optionFile.setApproverAnnotation(itemInfo[1]);// 批注
								}
							}
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 3: {
					switch (rowIndex) {// 格式3数据比较规范，可以根据节点行数拆分每行的数据
						case 0: {// 审批人\申请人 单位部门信息 节点类型信息
							if (nodeIndex == 0) {// 申请节点
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));// 申请人
							} else {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));// 审批人
							}
							if (rowInfo.trim().length() > 0) {// 如果替换后的还有内容，则进行分析单位、部门信息
								int num = rowInfo.indexOf("(");
								int lastNum = rowInfo.lastIndexOf(")");
								String roleInfo=rowInfo.substring(num+1,lastNum);
								if (nodeIndex == 0) {
									optionFile.setApproverRole(roleInfo);
								}else{
									optionFile.setApproverRole(roleInfo);
								}
								rowInfo=rowInfo.substring(0,num);
								int firstIndex = rowInfo.indexOf("/");
								int lastIndex = rowInfo.lastIndexOf("/");
								if(firstIndex>-1&&lastIndex>-1){
									optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
									optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
								}
							}
							break;
						}
						case 1: {// 审批人\申请人 姓名 时间
							String[] colInfo = rowInfo.split("\\s+",4);
							if (nodeIndex == 0) {
								optionFile.setApproverName(colInfo[0]);
								optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
							} else {
								optionFile.setApproverOpinion(colInfo[0]);
								optionFile.setApproverName(colInfo[1]);
								optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
							}
							break;
						}
						case 2: {// 审批人\申请人 批注
							if (nodeIndex != 0) {
								String[] colInfo = rowInfo.split("：",2);
								if (colInfo.length > 0) {
									optionFile.setApproverAnnotation(colInfo[1]);
									//遍历批注后内容 找到下一条审批人前记录为批注
									String contentStr=colInfo[1];
									int index=row+1;
									int index_row=0;
									for (int j = index; j < rowValue.length; j++) {
										String content = rowValue[j];
										if ((content.contains(ResourceFactory.getProperty("format.optionfield.applicationtime"))
												&& content.contains(ResourceFactory.getProperty("format.optionfield.proposer")))
												|| (content
														.contains(ResourceFactory.getProperty("format.optionfield.approver2"))
														&& content.contains(ResourceFactory
																.getProperty("format.optionfield.approvertime")))) {
											break;
										} else if (((!content
												.contains(ResourceFactory.getProperty("format.optionfield.applicationtime")))
												&& content.contains(ResourceFactory.getProperty("format.optionfield.proposer")))
												|| ((!content.contains(
														ResourceFactory.getProperty("format.optionfield.approvertime")))
														&& (content.contains(
																ResourceFactory.getProperty("format.optionfield.opinionAgree"))
																|| content.contains(ResourceFactory
																		.getProperty("format.optionfield.opinionDisagree")))
														&& (!content.contains(ResourceFactory
																.getProperty("format.optionfield.annotation"))))) {
											break;
										}else if(content.contains("(")&&content.contains(")：")) {
											break;
										} else {
											index_row++;
											contentStr += "\n"+content;
										}
									}
									row=index-1+index_row;
									optionFile.setApproverAnnotation(contentStr);
								}
							}
							break;
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				case 4: {
					switch (rowIndex) {
						case 0: {
							if (nodeIndex == 0) {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.proposer2"));
							} else {
								optionFile.setApproverType(ResourceFactory.getProperty("format.optionfield.approver"));
							}
							if (rowInfo.trim().length() > 0) {
								int num = rowInfo.indexOf("(");
								int lastNum = rowInfo.lastIndexOf(")");
								String roleInfo=rowInfo.substring(num+1,lastNum);
								optionFile.setApproverRole(roleInfo);
								rowInfo=rowInfo.substring(0,num);
								int firstIndex = rowInfo.indexOf("/");
								int lastIndex = rowInfo.lastIndexOf("/");
								if(firstIndex>-1&&lastIndex>-1){
									optionFile.setApproverUnit(rowInfo.substring(0, firstIndex));
									optionFile.setApproverDepartment(rowInfo.substring(lastIndex).replace("/", ""));
								}
							}
							break;
						}
						case 1: {
							optionFile.setApproverRole(rowInfo);
							break;
						}
						case 2: {
							String[] colInfo = rowInfo.split("\\s+",4);
							if (nodeIndex == 0) {
								optionFile.setApproverName(colInfo[0]);
								optionFile.setApprovalTime(colInfo[1] + " " + colInfo[2]);
							} else {
								optionFile.setApproverOpinion(colInfo[0]);
								optionFile.setApproverName(colInfo[1]);
								optionFile.setApprovalTime(colInfo[2] + " " + colInfo[3]);
							}
							break;
						}
						case 3: {
							if (nodeIndex != 0) {							
								String[] colInfo = rowInfo.split("：",2);
								if (colInfo.length > 0) {
									optionFile.setApproverAnnotation(colInfo[1]);
								}
							}
							break;
						}
					}
					if(nodeIndex==0){
						optionFile.setApproverAnnotation(ResourceFactory.getProperty("format.optionfield.startPro"));
					}
					rowIndex++;
					break;
				}
				default: {
					break;
				}
			}
		}
		valueList.add(JSON.toString(optionFile.changeObjectToMap()));
		optionFile = new TemplateOptionField();
		return valueList;
	}
}
