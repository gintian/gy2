package com.hjsj.hrms.businessobject.attestation.zgpt;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import java.util.Date;
import java.util.List;
//import com.interfaces.webserviceX.KPICommission;

/**
 * 中国普天代办事宜
 * @author Owner
 *
 */
public class IMISPendProceed{
	//private KPICommission kpiCommission=null;
	public IMISPendProceed()
	{
		Category cat = Category.getInstance("com.hrms.frame.dao.DAODebug");
		cat.error("newKPICommissionStart");
		//this.kpiCommission=new KPICommission();		
		cat.error("newKPICommissionEnd");
	}
    /**
     * 向待办库中加入新的待办
     * @param pendingCode  待办信息在应用系统的唯一标识代号?
     * @param pendingTitle  待办标题
     * @param senderMessage 待办信息发送人usercode（发送人用户登录名）
     * @param pendingSenderCN 待办信息发送人中文姓名
     * @param receiverMessage 待办信息接收人usercode（接收人用户登录名）
     * @param pendingURL  待办链接地址
     * @param pendingStatus  待办状态（0：待办， 1：已办）
     * @param pendingLevel  待办级别（0：非重要，1：重要）
     * @param pendingType  待办所在应用中的类别（不同类型的绩效考核的中文名称，如个人考核，团队考核）
     * @return
     */
	public boolean insertPending(String pendingCode,String pendingTitle,String senderMessage,String receiverMessage,
			String pendingURL,int pendingStatus,int pendingLevel,String pendingType)
	{
		boolean isCorrect=false;		
		Date date=new Date();
		try {
			String hrp_logon_url=SystemConfig.getProperty("hrp_logon_url");
			String pendingDate=DateUtils.format(date,"yyyy-MM-dd HH:mm");			
			String appType="绩效考核";
			//发送者
			LazyDynaBean bean=getUserNamePassword(senderMessage);			
			if(bean==null)
			{				
				return false;
			}			
			String pendingSender=(String)bean.get("username");	
			String pendingSenderCN=(String)bean.get("a0101");
			bean=getUserNamePassword(receiverMessage);
			if(bean==null)
			{
				return false;
			}
			String username=(String)bean.get("username");
			if(username==null||username.length()<=0) {
                username="";
            }
			String password=(String)bean.get("password");
			if(password==null||password.length()<=0) {
                password="";
            }
			String pendingReceiver=username;		
			String etoken=PubFunc.convertTo64Base(username+","+password);
			pendingURL=hrp_logon_url+""+pendingURL+"&etoken="+etoken+"";				
			int isSucceed=0;//this.kpiCommission.insertPending(pendingCode, pendingTitle,pendingDate, pendingSender, pendingSenderCN, pendingReceiver, pendingURL, pendingStatus, pendingLevel, pendingType, appType);
			
			if(isSucceed==1) {
                isCorrect=true;
            }
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isCorrect;
	}
	/**
	 * 更新待办库中的信息 
	 * @param pendingCode 待办信息在应用系统的唯一标识代号
	 * @param pendingStatus 待办状态
	 * @param pendingType 待办所在应用中的类别
	 * @return
	 */
	public boolean updatePendingsStateByUID(String pendingCode,int pendingStatus,String pendingType)
	{
		boolean isCorrect=false;		
		Date date=new Date();
		String pendingDate=DateUtils.format(date,"yyyy-MM-dd HH:mm");		
		String appType="绩效考核";	
		int isSucceed=0;//this.kpiCommission.updatePendingsStateByUID(pendingCode, pendingDate, pendingStatus, pendingType, appType);		
		if(isSucceed==1) {
            isCorrect=true;
        }
		return isCorrect;
	}
	/**
	 * 通过人事移动的传递的值得到用户名密码 例value=Usr000001
	 * @param value
	 * @return username，password
	 */
	private LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    StringBuffer sql=new StringBuffer();
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password from "+nbase+"A01");
	    sql.append(" where a0100='"+a0100+"'");
	    //Category.getInstance("com.hrms.frame.dao.ContentDAO").error("代办调用新增SQL=="+sql);
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}
	
}
