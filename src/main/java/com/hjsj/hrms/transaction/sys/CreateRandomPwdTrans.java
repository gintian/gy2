package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * <p>Title:CreateRandomPwdTrans</p>
 * <p>Description:创建随机密码</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-12:17:16:06</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateRandomPwdTrans extends IBusiness {
	
	private StringBuffer strSrc=new StringBuffer();
	private Random random;//=new Random();
	private String password;
	private String username;
	
	public void execute() throws GeneralException {
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		String createtype=(String)map.get("createtype");
		ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedaccount");
		if("2".equalsIgnoreCase(createtype))
		{
			String sql_str=(String)this.getFormHM().get("sql_str");
			sql_str=PubFunc.keyWord_reback(sql_str);
			String cond_str=(String)this.getFormHM().get("cond_str");
			cond_str=PubFunc.keyWord_reback(PubFunc.decrypt(cond_str));
			String columns=(String)this.getFormHM().get("columns");
			String loguser=(String)this.getFormHM().get("loguser");
			selectedlist=this.getAllList(sql_str, cond_str, columns,loguser);
		}
		String dbpre=(String)this.getFormHM().get("dbpre");
		if(selectedlist==null)
			return;
		/**取得密码的长度*/
		int pwdlen=getPwdLen();
		cat.debug("username="+this.username.toLowerCase());
		StringBuffer usernames = new StringBuffer();
		UserObjectBo userBo = new UserObjectBo(this.frameconn);
		for(int i=0;i<selectedlist.size();i++)
		{
			DynaBean bean=(DynaBean)selectedlist.get(i);
			bean.set("password",getRandomPwd(pwdlen));
			String username = (String)bean.get(this.username.toLowerCase());
			bean.set("username",username);
			cat.debug("random pwd="+bean.get("password"));
			cat.debug("random username="+bean.get("username"));
			savePassword((String)bean.get("a0100"),(String)bean.get("password"),dbpre);	
			usernames.append(",'"+username+"'");
			if(i>0&&i%50==0){
				userBo.change2firstPwd(usernames.substring(1));
				usernames.setLength(0);
			}
		}
		if(usernames.length()>0){
			userBo.change2firstPwd(usernames.substring(1));
		}
		this.getFormHM().put("accountlist",selectedlist);
	}
	
	/**
	 * 
	 */
	public CreateRandomPwdTrans() {
		strSrc.append("qazwsxedcrfvtgbyhnujmiklop0192384756");
		//Date date=new Date();
		random=new Random(System.currentTimeMillis());
	}
	/**
	 * 取得密码的长度以及用户名及口令
	 * 随机生成8位的口令
	 * @return
	 */
	private int getPwdLen()
	{
		int len=8;
        /**登录参数表,登录用户指定不是username or userpassword*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
   
        if(login_vo==null)
        {
            username="username";
            password="userpassword";
        }
        else
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");
            if(idx==-1)
            {
                username="username";
                password="userpassword";
            }
            else
            {
                username=login_name.substring(0,idx);
                if("#".equals(username)|| "".equals(username))
                	username="username";
                password=login_name.substring(idx+1);  
                if("#".equals(password)|| "".equals(password))
                	password="userpassword";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(password);
                	len=8;//item.getItemlength();       //当然可以按实际长度生成随机密码         	
                }
            }
        }

		return len;
	}
	/**
	 * 取得随机生成的密码的长度
	 * @param pwdlen
	 * @return
	 */
	private String getRandomPwd(int pwdlen)
	{
		StringBuffer strpwd=new StringBuffer();
		int index=0;
		for(int i=0;i<pwdlen;i++)
		{
			index=random.nextInt(36);
			strpwd.append(this.strSrc.charAt(index));
		}
		return strpwd.toString();
	}

	/**
	 * 保存口令
	 * @param a0100
	 * @param password
	 * @param dbpre
	 * @throws GeneralException
	 */
	private void savePassword(String a0100,String password,String dbpre) throws GeneralException
	{
		String field_name=this.password;//getPwd_Field();
		RecordVo vo=new RecordVo(dbpre+"A01");
		/**口令加密存储*/
		if(ConstantParamter.isEncPwd(this.getFrameconn()))
		{
			Des des=new Des();
			password=des.EncryPwdStr(password);
		}
		vo.setString(field_name,password);
		vo.setString("a0100",a0100);
		try
		{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private ArrayList getAllList(String sql_str, String cond_str, String cloumns, String loguser) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql_str + " " + cond_str + " order by a0000");
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (uplevel == null || uplevel.length() == 0)
				uplevel = "0";
			String[] arr = cloumns.split(",");
			String key, value;
			while (this.frowset.next()) {
				DynaBean bean = new LazyDynaBean();
				for (int i = 0; i < arr.length; i++) {
					key = arr[i];
					if (key == null || "".equalsIgnoreCase(key))
						continue;
					value = this.frowset.getString(key);
					value = value == null ? "" : value;
					
					//部门显示不出来了，因为识别不了。注掉此处 guodd-2015-11-25
//					if (key.equalsIgnoreCase("e0122")) {
//						if (value.length() >= 1) {
//							CodeItem item = AdminCode.getCode("UM", value, Integer.parseInt(uplevel));
//							if (item != null && item.getCodename() != null && !item.getCodename().equals("")) {
//								value = item.getCodename() + "/" + value;
//							}
//						}
//					}
					bean.set(key, value);
				}
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
 
}
