/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.warn;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhm 
 * templet of XML: 
 * <?xml version='1.0' encoding="GB2312"?>
 * <hrpwarn>
 * 		<warnctrl domain="UM21401,@K2141003,RL00000001" email="true|false"
 *         mobile=”true|false” days="10” rule=”0|1”/> 
 *  	<frequency type="0|1|2" value="10"/>
 *  	<simpleExpress value="1*2*3*4|A0111>=$YRS[25]`A0111<=$YRS[50]`A0405<=29`A2205=01`"/>
 * </hrpwarn>
 * 
 *  UN+单位编码列表
	UM+部门编码列表
	QK+职位编码列表
	RL+角色标识列表
	(列表以逗号分开,如UM010,0101,|UN01,)
 *
 *  预警提示频度：
	    type="0" 每月   value="1..31"
	    type="1" 每周   value="1..7"
	    type="2" 每天   value="1..24"
 * 
 */
public class ConfigCtrlInfoVO {


	private Document doc = null;	//文档对象
    private String warntyp="0";
    private String setid="";
	private String strDays = null; 	// days:提前多少天告警,对简单的预警起作用；

	private String strDomain = null; //提配对象范围
	
	private String strEmail = null;	 //email :true发邮件，否则不发邮件

	private String strFreqType = null;	//预警类型

	private String strFreqValue = null; //预警频度值

	private String strMobile = null;	//mobile=true短信，否则不发短信
	
	private String strWeixin = null;	//mobile=true微信，否则不发微信
	
	private String strDingtalk = null;	//mobile=true钉钉，否则不发钉钉

	private String strRule = null;		// rule:=0简单 =1复杂
	
	private String strSimpleExpress = null; //预警简单公式

	private String strXML = null;

	private String strNbase=null;   //预警人员库
	
	private String strEveryone=null;//邮件或短信是否通过到当事人(对每个预警结果对象进行提醒)
	private String strNote=null;//邮件摸版
	private String strSendspace=null;//发送间隔
	private ArrayList dblist=new ArrayList();
	
	private String strTemplate=null;
	public ArrayList getDblist() {		
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getStrNbase() {
		return strNbase;
	}

	public void setStrNbase(String strNbase) {
		this.strNbase = strNbase;
	}

	public String getStrTemplate() {
		return strTemplate;
	}

	public void setStrTemplate(String strTemplate) {
		this.strTemplate = strTemplate;
	}

	/**
	 * 构造函数，根据数据库的XML字符串，解析成对象属性字段
	 * 
	 * @param strXML
	 */
	public ConfigCtrlInfoVO(String strXML) {
		if( strXML == null || strXML.startsWith("java.lang.Object") || strXML.trim().length()<30){
			strXML = "<?xml version='1.0' encoding=\"GB2312\"?>"+
			 "<hrpwarn>"+
			 "     <warnctrl domain=\"\"  email=\"false\" mobile=\"false\" days=\"10\"  everyone=\"false\"  notes=\"\" sendspace=\"\" rule=\"\"/>"+
			 "     <frequency   type=\"\" value=\"\"/>"+
			 "     <simpleExpress value=\"\"/>"+
			 "     <nbase></nbase>"+//人员预警库
			 "     <template></template>"+//通知模板列表
			 "</hrpwarn>";
		}			
		setStrXML(strXML);
		try {
			doc = PubFunc.generateDom(strXML);
			Element root = doc.getRootElement();
			Element eWarnCtrl = root.getChild("warnctrl"); //预警控制
			if( eWarnCtrl!=null){
				setStrDomain(PubFunc.keyWord_reback(eWarnCtrl.getAttributeValue("domain")));  //预警对象
				setStrEmail(PubFunc.keyWord_reback(eWarnCtrl.getAttributeValue("email")));    //是否发邮件
				setStrMobile(eWarnCtrl.getAttributeValue("mobile"));  //是否电话
				setStrWeixin(eWarnCtrl.getAttributeValue("weixin"));  //是否电话
				setStrDingtalk(eWarnCtrl.getAttributeValue("dingtalk"));  //是否钉钉
				setStrDays(eWarnCtrl.getAttributeValue("days"));      //提前天数
				setStrRule(eWarnCtrl.getAttributeValue("rule"));      //简单与复杂
				this.setStrEveryone(eWarnCtrl.getAttributeValue("everyone"));//
				this.setStrNote(eWarnCtrl.getAttributeValue("notes"));//
				this.setStrSendspace(eWarnCtrl.getAttributeValue("sendspace"));
			}
			
			Element eFrequency = root.getChild("frequency");//预警频率
			if( eFrequency!=null){
				setStrFreqType(eFrequency.getAttributeValue("type"));   //类型
				setStrFreqValue(eFrequency.getAttributeValue("value")); //值
			}
			
			Element eExpress = root.getChild("simpleExpress"); //公式
			if( eExpress!=null ){
				setStrSimpleExpress(PubFunc.keyWord_reback(eExpress.getAttributeValue("value")));//简单公式
			}
            Element eNbase=root.getChild("nbase");
            if(eNbase!=null)
            {
                this.setStrNbase(eNbase.getText());
            }
            Element eTemplate=root.getChild("template");
            if(eTemplate!=null)
            {
                this.setStrTemplate(eTemplate.getText());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public Document getDoc() {
		return doc;
	}

	/**
	 * 由于界面统一使用<html:radio name="warnConfigForm" property="xmlCtrlVo.domainType(isRole)"
	 * 所以参数是固定值 “isRole”
	 */
	public String getDomainType(String strDomainType){
		
		// 返回值isRole: 0=机构组织(非角色) 1=角色
		String strRet = "0";
		if( getStrDomain()== null || getStrDomain().trim().length()<1){
			strRet="0";
		}else if( "isRole".equals(strDomainType)){
			strRet=getStrDomain().startsWith("RL")?"1":"0";// getStrDomain().indexOf("RL")>0?"1":"0";
		}else if( "isUnit".equals(strDomainType)){
			boolean isUnit = false;
			isUnit = isUnit || getStrDomain().indexOf("UN")>0 || getStrDomain().indexOf("UM")>0 || getStrDomain().indexOf("QK")>0;
			strRet = isUnit?"1":"0";
		}
		return strRet;	
	}
	
	
	/**
	 * 预警频度信息显示 (组合信息:如 每天8:30 )
	 * @return
	 */
	public String getFreqShow(){
		StringBuffer strRet = new StringBuffer();
		if( getStrFreqType() == null || getStrFreqType().trim().length() < 1){
			strRet.append( ResourceFactory.getProperty("label.sys.warn.freq.realtime"));
		
		}else if( "0".equals(getStrFreqType()) ){//月
			strRet.append( ResourceFactory.getProperty("label.sys.warn.freq.everymonth") );
			//当预警频度值 为空  给上默认值   11661 wangb 20170522
			if(getStrFreqValue()==null ||"".equalsIgnoreCase(getStrFreqValue())){
				setStrFreqValue("1");
			}
			strRet.append(getStrFreqValue());
			strRet.append( ResourceFactory.getProperty("label.sys.warn.freq.everymonth.day"));
		}else if( "1".equals(getStrFreqType()) ){//周
			strRet.append( ResourceFactory.getProperty("label.sys.warn.freq.everyweek"));
			//当预警频度值 为空  给上默认值   11661 wangb 20170522
			if(getStrFreqValue()==null ||"".equalsIgnoreCase(getStrFreqValue())){
				setStrFreqValue("1");
			}
			strRet.append( ContextTools.getStringWeek( getStrFreqValue()) );//getStrFreqValue() );//
		}else if( "2".equals(getStrFreqType()) ){//天
			strRet.append( ResourceFactory.getProperty("label.sys.warn.freq.everyday")).append(getStrFreqValue());			
		}
		return strRet.toString();
	}
		
	/*
	 * 
	 */
	public String getFreqType(String strTypeName){
		String strRet = "0";//0:true,1:false
		
		//<frequency type="0|1|2" value="10"/>
		
		if( getStrFreqType() == null ){
			strRet = "1";
		}else if( "isMonth".equals(strTypeName)){
			strRet = "0".equals(getStrFreqType())?"0":"1";
			
		}else if( "isWeek".equals(strTypeName)){
			strRet = "1".equals(getStrFreqType())?"0":"1";
			
		}else if( "isDay".equals(strTypeName)){
			strRet = "2".equals(getStrFreqType())?"0":"1";
			
		}
		return strRet;
	}

	public String getIsEmail(){
		return getStrEmail()==null?"false":getStrEmail();//.equals("true")?"1":"0";
	}

	public String getIsMobile(){
		return getStrMobile()==null?"false":getStrMobile();//.equals("true")?"1":"0";
	}
	
	public String getIsWeixin(){
		return this.getStrWeixin()==null?"false":getStrWeixin();//.equals("true")?"1":"0";
	}
	/**
	 * xus 17/4/19
	 * 判断是否为钉钉
	 */
	public String getIsDingtalk(){
		return this.getStrDingtalk()==null?"false":getStrDingtalk();//.equals("true")?"1":"0";
	}
	/**
	 * 预警规则定义
	 * <bean:write name="warnConfigForm" property="xmlCtrlVo.isComplex" filter="true"/>
	 * @return
	 */
	public String getIsComplex() {
		String strRule = getStrRule();
		if( strRule==null || strRule.trim().length()<1){
			strRule="1";//默认值为1，即复杂，因为cs中都是复杂的条件分析，并且设定的没有xml值
		}
		return strRule;
	}
	
	public void setIsComplex(String strValue){
		setStrRule( strValue );
	}

	
	/**
	 * 根据根子段属性生成XML文本字符串内容
	 * 
	 * @return XML文本
	 */
	public String generateStringXML() {

		Element eWarnCtrl = new Element("warnctrl");
		eWarnCtrl.setAttribute("domain", PubFunc.keyWord_reback(getStrDomain()));
		eWarnCtrl.setAttribute("email", PubFunc.keyWord_reback(getStrEmail()));
		eWarnCtrl.setAttribute("mobile", getStrMobile());
		eWarnCtrl.setAttribute("weixin", getStrWeixin());
		eWarnCtrl.setAttribute("dingtalk", getStrDingtalk());
		eWarnCtrl.setAttribute("days", getStrDays());
		eWarnCtrl.setAttribute("rule", getStrRule());
		eWarnCtrl.setAttribute("everyone",this.getStrEveryone() );
		eWarnCtrl.setAttribute("notes",this.getStrNote());
		eWarnCtrl.setAttribute("sendspace",this.getStrSendspace());
		
		Element eFrequency = new Element("frequency");
		eFrequency.setAttribute("type", getStrFreqType());
		eFrequency.setAttribute("value", getStrFreqValue());
		
		Element eExpress = new Element("simpleExpress");
		eExpress.setAttribute("value", PubFunc.keyWord_reback(getStrSimpleExpress()));
		Element eNbase=new Element("nbase");		
		eNbase.setText(this.getStrNbase());
        Element eTemplate=new Element("template");
        eTemplate.setText(this.getStrTemplate());
		Element root = new Element("hrpwarn");
		doc = new Document(root);

		List childrens = root.getChildren();
		childrens.add(eWarnCtrl);
		childrens.add(eFrequency);
		childrens.add(eExpress);
		childrens.add(eNbase);
		childrens.add(eTemplate);
		Format f = Format.getCompactFormat();
		f.setEncoding("UTF-8");

		XMLOutputter xout = new XMLOutputter(f);
		String strTemp = xout.outputString(doc);
		return strTemp;// strReturn;
	}

	public String getStrDays() {
		return strDays;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public String getStrEmail() {
		return strEmail;
	}

	public String getStrFreqType() {
		if(strFreqType==null|| "".equals(strFreqType)||strFreqType.trim().length()<1) {
            strFreqType="-1";
        }
		return strFreqType;
	}

	public String getStrFreqValue() {
		return strFreqValue;
	}

	public String getStrMobile() {
		return strMobile;
	}

	public String getStrWeixin() {
		return strWeixin;
	}
	
	public String getStrDingtalk() {
		return strDingtalk;
	}
	
	public String getStrRule() {
		return strRule;
	}

	public String getStrXML() {
		
		strXML = generateStringXML();		
		return strXML;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public void setDomainType(String strDomainType,String strValue){

		if( "isRole".equals(strDomainType)){
			if( "1".equals(strValue)){
				//setStrDomain("RL");
			}// no else becaue else equal "isUnit=true"
						
		}else if( "isRole".equals(strDomainType)){
			if("0".equals(strValue)){
				//setStrDomain("UN");// "@K"中的@似乎是ajax的违例关键字，会造成致命错误				
			}
		}
	}

	public void setFreqType(String strTypeName,String strValue){
		//<frequency type="0|1|2" value="10"/>
		
		if( "isMonht".equals(strTypeName)){
			setStrFreqType("0".equals(strValue)?"0":"");
			
		}else if( "isWeek".equals(strTypeName)){
			setStrFreqType("0".equals(strValue)?"1":"");
			
		}else if( "isDay".equals(strTypeName)){
			setStrFreqType("0".equals(strValue)?"2":"");
			
		}
	}

	public void setIsEmail(String strValue){
		setStrEmail( "1".equals(strValue)?"true":"false");
	}

	public void setIsMobile(String strValue){
		setStrMobile( "1".equals(strValue)?"true":"false");
	}
	
	public void setIsWeixin(String strValue){
		setStrWeixin( "1".equals(strValue)?"true":"false");
	}
	
	public void setIsDingtalk(String strValue){
		setStrDingtalk( "1".equals(strValue)?"true":"false");
	}
	
	public void setStrDays(String strDays) {
		this.strDays = strDays;
	}

	public void setStrDomain(String strDomain) {
		/*if(strDomain==null||strDomain.length()<=0)
			strDomain="UN";*/
		this.strDomain = strDomain;
	}
	
	public void setStrEmail(String strEmail) {
		if( strEmail==null || strEmail.trim().length()<1) {
            strEmail="false";
        }
		this.strEmail = strEmail;
	}
	public void setStrEmail(String[] strEmailArray){
		if( strEmailArray==null || strEmailArray.length<1){
			this.strEmail = "false";
		}else{
			this.strEmail = "true";
		}
	}
	
	public void setStrFreqType(String strFreqType) {
		this.strFreqType = strFreqType;
	}
	
	public void setStrFreqValue(String strFreqValue) {
		this.strFreqValue = strFreqValue;
	}
	public void setStrMobile(String strMobile) {
		if( strMobile==null || strMobile.trim().length()<1 ) {
            strMobile="false";
        }
		this.strMobile = strMobile;
	}
	
	public void setStrWeixin(String strWeixin) {
		if( strWeixin==null || strWeixin.trim().length()<1 ) {
            strWeixin="false";
        }
		this.strWeixin = strWeixin;
	}
	
	public void setStrDingtalk(String strDingtalk) {
		if( strDingtalk ==null || strDingtalk.trim().length()<1 ) {
            strDingtalk="false";
        }
		this.strDingtalk = strDingtalk;
	}
	
	public void setRemindArray(String[] strRemindArray){
		this.strMobile = "false";
		this.strEmail = "false";
		this.strWeixin = "false";
		this.strDingtalk = "false";
		this.strEveryone="false";
		for(int i=0; i<strRemindArray.length; i++){
			if( "mobile".equals(strRemindArray[i])){
				setStrMobile("true");
			}else if("email".equals(strRemindArray[i]))
			{
				setStrEmail("true");
			}else if("weixin".equals(strRemindArray[i])){
				this.setStrWeixin("true");
			}else if("dingtalk".equals(strRemindArray[i])){
				this.setStrDingtalk("true");
			}else if("everyone".equals(strRemindArray[i])){
				this.setStrEveryone("true");
			}
		}
	}
	public String[] getRemindArray(){
		return new String[]{getStrMobile(),getStrEmail(),this.getStrWeixin(),this.getStrDingtalk(),this.getStrEveryone()};
	}
	public void setStrRule(String strRule) {
		this.strRule = strRule;
	}
	public void setStrXML(String strCtrl) {
		this.strXML = strCtrl;
	}
	
	public void setListRole(String strSelectRole){
		//setStrDomain( strSelectRole );
	}

	public String getStrSimpleExpress() {
		return strSimpleExpress;
	}

	public void setStrSimpleExpress(String strSimpleExpress) {
		this.strSimpleExpress = strSimpleExpress;
	}

	public String getStrEveryone() {
		return strEveryone;
	}

	public void setStrEveryone(String strEveryone) {
		if( strEveryone==null || strEveryone.trim().length()<1) {
            strEveryone="false";
        }
		this.strEveryone = strEveryone;
	}

	public String getStrNote() {
		if(strNote==null||strNote.length()<=0) {
            strNote="";
        }
		return strNote;
	}

	public void setStrNote(String strNote) {
		this.strNote = strNote;
	}

	public String getStrSendspace() { 
		if(strSendspace==null||strSendspace.length()<=0) {
            strSendspace="";
        }
		return strSendspace;
	}

	public void setStrSendspace(String strSendspace) {
		if(strSendspace==null||strSendspace.length()<=0) {
            strSendspace="7";
        }
		this.strSendspace = strSendspace;
	}

	public String getWarntyp() {
		return warntyp;
	}

	public void setWarntyp(String warntyp) {
		this.warntyp = warntyp;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}
	
}
