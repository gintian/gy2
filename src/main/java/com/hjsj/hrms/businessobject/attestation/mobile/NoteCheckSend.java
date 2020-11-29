package com.hjsj.hrms.businessobject.attestation.mobile;

import aiismg.jcmppapi.CMPPAPI;
import com.hjsj.hrms.interfaces.sys.SmsProxy;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.admin.ReVerify;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.List;
import java.util.Random;

public class NoteCheckSend implements ReVerify, SmsProxy{

	/**
	 * 通过短信发送校验码
	 * @param  phone 手机号码
	 * @return 已发送给登录用户的校验码，8位
	 */
	private CMPPAPI pCMPPAPI = null;
	public NoteCheckSend()
	{
		pCMPPAPI = new CMPPAPI();
	}
	/**
	 * 发送校验码
	 */
	@Override
    public String sendValidateCodeBySms(String phone)
    {

    	String issendsme;
		try {
			issendsme = SystemConfig.getProperty("issendsms");
			if(issendsme!=null&& "false".equals(issendsme)) {
				return "12345678";
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Random random;
    	StringBuffer strSrc=new StringBuffer();
		//strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
    	Category.getInstance("com.hrms.frame.dao.ContentDAO").error("开始调用"+phone);
    	strSrc.append("123456789");//2009-09-23，修改为4位数字
		random=new Random(System.currentTimeMillis()); 
		int codelen=4;
		StringBuffer checkNum=new StringBuffer();
		int index=0;
		for(int i=0;i<codelen;i++)
		{
			index=random.nextInt(9);
			checkNum.append(strSrc.charAt(index));
		}    	
		
		try
		{
			if(phone==null||phone.length()<=0)
			{
				phone=SystemConfig.getPropertyValue("sendsmsphone");
				if(phone==null||phone.length()<=0) {
					return "";
				}
			}
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("建立CMPPAPI链接");	
			main_connect();//建立CMPPAPI链接		
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("开始调用"+checkNum.toString());
			submitMsg("",phone,"您的e-HR登陆临时校验码是："+checkNum.toString());
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		//System.out.println(checkNum.toString());
		return checkNum.toString();
    }   
	
	
	
	/**
	 * 发送校验码
	 */
	public String sendValidateCodeBySms(String phone,String context)
    {

    	String issendsme;
		try {
			issendsme = SystemConfig.getProperty("issendsms");
			if(issendsme!=null&& "false".equals(issendsme)) {
				return "12345678";
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Random random;
    	StringBuffer strSrc=new StringBuffer();
		strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
		random=new Random(System.currentTimeMillis()); 
		int codelen=8;
		StringBuffer checkNum=new StringBuffer();
		int index=0;
		for(int i=0;i<codelen;i++)
		{
			index=random.nextInt(34);
			checkNum.append(strSrc.charAt(index));
		}    	
		
		try
		{
			if(phone==null||phone.length()<=0)
			{
				phone=SystemConfig.getProperty("sendsmsphone");
				if(phone==null||phone.length()<=0) {
					return "";
				}
			}
				
			main_connect();//建立CMPPAPI链接				
			submitMsg("",phone,context);
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		//System.out.println(checkNum.toString());
		return checkNum.toString();
    }   
	
	
	
	
	
	
	
	
	
	
	/**
	 * 发送短信
	 */
	@Override
    public boolean sendMessage(String phone, String msg) {
		boolean bflag=false;
		RecordVo sms_vo=ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
	    if(sms_vo==null) {
			return false;
		}
	    String param=sms_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
			return false;
		}
        Element ele=null;
        String down_url="";
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        
            /**短信网关参数*/
    		String xpath="/ports/gateway";
    		XPath reportPath = XPath.newInstance(xpath);
    		List childlist = reportPath.selectNodes(doc);
            if(childlist.size()!=0)
            {
	            ele=(Element)childlist.get(0);	           
	            childlist=ele.getChildren("down_url");
	            if(childlist.size()!=0)
	            {	
	            	Element c_ele=(Element)childlist.get(0);
	            	down_url=c_ele.getText();            
	            }
            }
        }catch(Exception ex)
        {
        	ex.printStackTrace();
        } 		
		SmsProxy smsproxy;
		try {
			smsproxy = (SmsProxy)Class.forName(down_url).newInstance();
			smsproxy.sendMessage(phone, msg);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bflag;
	}	
	/**
	 * 加载javacmppc.ini文件建立pCMPPAPI文件链接
	 * @throws Exception
	 */
    private void main_connect() throws Exception
	{
		String path=SystemConfig.getProperty("cmppcinipath");//"D:/Tomcat5.5/config
		String javacmppcUrl = path+"/javacmppc.ini";         // 北京移动短信接口配置文件
		if (pCMPPAPI.InitCMPPAPI(javacmppcUrl) != 0)
		{
			throw new Exception("CMPP Init failed.:加载javacmppc.ini");
		}
	}
    
    
    public boolean sendMesg(String phone,String context)
    {
    	//Category cat=Category.getInstance("com.hrms.struts.facade.transaction.IBusiness");
    	//System.out.println("1yyyttttttt:"+phone+"  "+context);
    	boolean flag=false;
    	try
    	{
    	//	main_connect();//建立CMPPAPI链接				
		//	submitMsg("",phone,context);
    		sendValidateCodeBySms(phone,context);
			flag=true;
    	}
    	catch(Exception e)
    	{
    		
    		e.printStackTrace();
    	}
    	//System.out.println("333777777777777777773");
    	return flag;
    }
    
   /**
    * 发送信息
    * @param sourceAddr源地址
    * @param destAddr目标地址
    * @param message信息内容
    * @return
    * @throws Exception
    */
    private boolean submitMsg(String sourceAddr,String destAddr,String message)throws Exception
    {
    	byte sServiceID[] = "8888815".getBytes();
		byte nMsgFormat = 15;
		byte sFeeType[] = "01".getBytes();//计费类型
		byte sFeeCode[] = "0".getBytes();//计费号码
		byte sValidTime[] = "".getBytes();
		byte sAtTime[] = "".getBytes();
		//byte sSrcTermID[] = new byte[21];
		byte sSrcTermID[] ="8888815".getBytes();
		System.arraycopy(sourceAddr.getBytes(),0,sSrcTermID,0,sourceAddr.length());
		byte sDestTermID[] = new byte[21];
		System.arraycopy(destAddr.getBytes(),0,sDestTermID,0,destAddr.length());
		
	    byte sMsgContent[] = message.getBytes("GBK");
	    
		int nMsgLen = sMsgContent.length;
	//	int nMsgLen=BOutContent.length;
		
		byte sMsgID[] = new byte[200];
		byte errorcode = 0;
		byte cFeeUserType[] = "0".getBytes();
		int retCode ;
		try
		{
			retCode =
				pCMPPAPI.CMPPSendLongSingle(
					(byte) 0,
					(byte) 2,
					sServiceID,
					nMsgFormat,
					sFeeType,
					sFeeCode,
					sValidTime,
					sAtTime,
					sSrcTermID,
					sDestTermID,
					nMsgLen,
					sMsgContent,
					sMsgID,
					errorcode,
					cFeeUserType,
					(byte) 0,
					(byte) 0);
			//System.out.println("the error code = " + pCMPPAPI.GetErrCode());
			//System.out.println("retCode="+retCode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}		
		return true;
    }
}
