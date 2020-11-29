package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.PassWordEncodeOrDecode;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SaveUserNamePasswordTrans</p>
 * <p>Description:保存指定的登录用户名及口令</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 28, 2005:10:05:55 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveUserNamePasswordTrans extends IBusiness {

    /**
     * 
     */
    public SaveUserNamePasswordTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
		
    	try {
	        String username=(String)this.getFormHM().get("username");
	        String password=(String)this.getFormHM().get("password");
	        String ip_addr=(String)this.getFormHM().get("ip_addr");
	        String lockfield=(String)this.getFormHM().get("lockfield");
	        //认证用户名，不选择时，默认为#，需转为半角   jingq add  2014.09.22
	        username = PubFunc.keyWord_reback(username);
	        password = PubFunc.keyWord_reback(password);
	        ip_addr = PubFunc.keyWord_reback(ip_addr);
	        lockfield = PubFunc.keyWord_reback(lockfield);
	        //String account_logon_interval=SystemConfig.getPropertyValue("account_logon_interval");
	        String	account_logon_interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
	        lockfield=lockfield==null||account_logon_interval.length()==0?"":lockfield;
	        /**/
	        if(username==null)
	            username="";
	        if(password==null)
	            password="";
	        if(ip_addr==null)
	        	ip_addr="";    
	        
	        
	        username = "#".equals(username)?"username":username;
	        password = "#".equals(password)?"userpassword":password;
	        
	        /*如果有设置了相同指标，提示错误信息*/
	        if(username.equals(password) || username.equals(ip_addr) || password.equals(ip_addr)) {
	        	throw new GeneralException("指定的指标不能相同");
	        }
	        
	        
	        boolean islog = false;
		    ConstantXml xml = new ConstantXml(this.frameconn);
		    String loginname = xml.getConstantValue("SS_LOGIN_USER_PWD");
		    String name = "";
		    String oldPassWord = "";
		    String[] tmps ={};
		    if(loginname.length()>0){
		    	 tmps = loginname.split(",");
		    	if(tmps.length>0)
		    		name = tmps[0];
		    	else
		    		name="username";
		    	if(name.length()==0){
		    		if(username.length()>0&&!"username".equalsIgnoreCase(name)){
		    			islog = true;
		    		}
		    	}else if(!username.equalsIgnoreCase(name)){
		    		islog = true;
		    	}
		    	if(tmps.length<=1)
		    		oldPassWord ="userpassword";
		    	else
		    		oldPassWord =tmps[1];
		    }
		    
			RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.frameconn);
			if(encryVo==null){
				encryVo = new RecordVo("constant");
				encryVo.setString("constant","EncryPwd");
				encryVo.setString("str_value","0");
			}
			String oldEncryPwd = encryVo.getString("str_value");
			
		    //如果选中的口令指标和原来的口令指标不同且原口令指标为加密状态 add by xiegh in 20170918 bug:29498
	    	if(!oldPassWord.equals(password)&&"1".equals(oldEncryPwd)){//oldEncryPwd=1:原口令指标为加密状态；=0：为解密状态
	    		PassWordEncodeOrDecode deped = new PassWordEncodeOrDecode(this.getFrameconn(),oldPassWord,"2",username);
	    		deped.exectue();
				PassWordEncodeOrDecode enped = new PassWordEncodeOrDecode(this.getFrameconn(),password,"1",username);
				enped.exectue();
	    	}
	        
	    	/*保存用户名密码指标*/
	        PassWordEncodeOrDecode enped = new PassWordEncodeOrDecode(this.getFrameconn(),password,"1",username);
	        enped.saveUserNamePassword();
	        ContentDAO dao = new ContentDAO(this.frameconn);
	        StringBuffer msg = new StringBuffer();
	        msg.append("登录认证设置成功，但如下用户的登录用户名含有\".\"，需重新到帐号分配中设置用户名，否则如下用户无法正常使用：<br/>");
	        boolean flag = false;
	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	        String A01="";
	        if(login_vo!=null) 
	          A01 = login_vo.getString("str_value").toLowerCase();
	        String pres[] = A01.split(",");
	        for(int i=0;i<pres.length;i++){
	        	if(pres[i].length()==3){
	        		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
	        		int n=0;
	        		while(this.frowset.next()){
	        			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+")&nbsp;");
	        			if(n%7==0)
	        				msg.append("<br>");
	        			n++;
	        			flag = true;
	        		}
	        	}
	        }
	        if(flag) {
	        	throw new Exception(msg.toString());
	        }
	        
	        //保存ip指标
	        RecordVo vo=new RecordVo("constant");
			vo.setString("constant","SS_BIND_IPADDR");
		    vo.setString("str_value",ip_addr);
		    StringBuffer sql=new StringBuffer();
		    sql.append("select * from constant where constant='SS_BIND_IPADDR'");
		    this.frowset=dao.search(sql.toString());
		    if(this.frowset.next())    	    	
	          dao.updateValueObject(vo);
		    else
		    	dao.addValueObject(vo);
	        ConstantParamter.putConstantVo(vo,"SS_BIND_IPADDR");
	        
	        /*保存账号锁定指标*/
	        xml.saveValue("SS_LOGIN_LOCK_FIELD", lockfield);
	        
	        
	        if(islog){
	        	this.getFormHM().put("@eventlog", this.userView.getUserName()+"通过设置认证用户名：从字段"+name+"设置字段为"+username);
	        }
        
	    }catch(Exception e)
	    {
	      e.printStackTrace();
	      throw GeneralExceptionHandler.Handle(e);  
	    } 
        /*
        
        if(username.equals("#")&&password.equals("#")&&ip_addr.equals("#"))
        {
        	try
            { 
            	RecordVo vo=new RecordVo("constant");
            	ContentDAO dao=new ContentDAO(this.getFrameconn());
        		vo.setString("constant","SS_BIND_IPADDR");
        	    vo.setString("str_value",ip_addr);
        	    StringBuffer sql=new StringBuffer();
        	    sql.append("select * from constant where constant='SS_BIND_IPADDR'");
        	    this.frowset=dao.search(sql.toString());
        	    if(this.frowset.next())    	    	
                  dao.updateValueObject(vo);
        	    else
        	    	dao.addValueObject(vo);
                ConstantParamter.putConstantVo(vo,"SS_BIND_IPADDR");
                username="";
            	password="";
            	String login_name=username+","+password;
     		    vo=new RecordVo("constant");
     		    vo.setString("constant","SS_LOGIN_USER_PWD");        
     		    vo.setString("str_value",login_name);
     		    vo.setString("describe","login_user_password");     		   
                dao.updateValueObject(vo);
             	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
             	
             	username="username";
            	if(username.length()>0){// xuj update 2013-7-25当重新指定登录名时需提示出登录名指标有.号值的
             		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
                    String A01="";
                    if(login_vo!=null) 
                      A01 = login_vo.getString("str_value").toLowerCase();
                    String pres[] = A01.split(",");
                    for(int i=0;i<pres.length;i++){
                    	if(pres[i].length()==3){
                    		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
                    		int n=0;
                    		while(this.frowset.next()){
                    			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+")&nbsp;");
                    			if(n%7==0)
                    				msg.append("<br>");
                    			n++;
                    			flag = true;
                    		}
                    	}
                    }
             	}
             	if(flag){
             		throw new Exception(msg.toString());
             	}
            }catch(Exception e)
            {
              e.printStackTrace();
              throw GeneralExceptionHandler.Handle(e);  
            }           
        }else if((username.equals("#")&&password.equals("#")&&!ip_addr.equals("#"))||(!username.equals("#")&&ip_addr.equals("#")&&password.equals("#"))||(!password.equals("#")&&ip_addr.equals("#")&&username.equals("#")))
        {
        	try
            { 
            	RecordVo vo=new RecordVo("constant");
            	ContentDAO dao=new ContentDAO(this.getFrameconn());
        		vo.setString("constant","SS_BIND_IPADDR");
        	    vo.setString("str_value",ip_addr);
        	    StringBuffer sql=new StringBuffer();
        	    sql.append("select * from constant where constant='SS_BIND_IPADDR'");
        	    this.frowset=dao.search(sql.toString());
        	    if(this.frowset.next())    	    	
                  dao.updateValueObject(vo);
        	    else
        	    	dao.addValueObject(vo);
                ConstantParamter.putConstantVo(vo,"SS_BIND_IPADDR");
            }catch(Exception e)
            {
              e.printStackTrace();	
            }
            if(username.equals("#")&& password.equals("#"))
        	{
            	username="";
            	password="";
            	try{        
        		    String login_name=username+","+password;
        		    RecordVo vo=new RecordVo("constant");
        		    vo.setString("constant","SS_LOGIN_USER_PWD");        
        		    vo.setString("str_value",login_name);
        		    vo.setString("describe","login_user_password");
        		    ContentDAO dao=new ContentDAO(this.getFrameconn());
                    dao.updateValueObject(vo);
                	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
                	
                	username="username";
                	if(username.length()>0){// xuj update 2013-7-25当重新指定登录名时需提示出登录名指标有.号值的
                 		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
                        String A01="";
                        if(login_vo!=null) 
                          A01 = login_vo.getString("str_value").toLowerCase();
                        String pres[] = A01.split(",");
                        for(int i=0;i<pres.length;i++){
                        	if(pres[i].length()==3){
                        		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
                        		int n=0;
                        		while(this.frowset.next()){
                        			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+"),");
                        			if(n%7==0)
                        				msg.append("<br>");
                        			n++;
                        			flag = true;
                        		}
                        	}
                        }
                 	}
                 	if(flag){
                 		throw new Exception(msg.toString());
                 	}
                }catch(Exception sqle){
          	      throw GeneralExceptionHandler.Handle(sqle);            
                }
               
            }else{
            	 if(username==null)
                     username="";
                 if(password==null)
                     password="";
             	if(username.equalsIgnoreCase(password))
             		throw new GeneralException(ResourceFactory.getProperty("error.noeqaul.userpwd"));        
                 try
                 {        
         		    String login_name=username+","+password;
         		    RecordVo vo=new RecordVo("constant");
         		    vo.setString("constant","SS_LOGIN_USER_PWD");        
         		    vo.setString("str_value",login_name);
         		    vo.setString("describe","login_user_password");
         		    ContentDAO dao=new ContentDAO(this.getFrameconn());
                     dao.updateValueObject(vo);
                 	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
                 	if(username.length()==0 || username.equals("#"))
                 		username="username";
                	if(username.length()>0){// xuj update 2013-7-25当重新指定登录名时需提示出登录名指标有.号值的
                 		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
                        String A01="";
                        if(login_vo!=null) 
                          A01 = login_vo.getString("str_value").toLowerCase();
                        String pres[] = A01.split(",");
                        for(int i=0;i<pres.length;i++){
                        	if(pres[i].length()==3){
                        		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
                        		int n=0;
                        		while(this.frowset.next()){
                        			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+"),");
                        			if(n%7==0)
                        				msg.append("<br>");
                        			n++;
                        			flag = true;
                        		}
                        	}
                        }
                 	}
                 	if(flag){
                 		throw new Exception(msg.toString());
                 	}
                 }
                 catch(Exception sqle)
                 {
           	      throw GeneralExceptionHandler.Handle(sqle);            
                 }
            }
        }else
        {
        	if(username.equalsIgnoreCase(ip_addr)||password.equalsIgnoreCase(ip_addr)||password.equalsIgnoreCase(username))
            {
            	throw new GeneralException("指定的指标不能相同");
            }
        	if(username.equals("#")&& password.equals("#"))
        	{
            	username="";
            	password="";
            	try{        
        		    String login_name=username+","+password;
        		    RecordVo vo=new RecordVo("constant");
        		    vo.setString("constant","SS_LOGIN_USER_PWD");        
        		    vo.setString("str_value",login_name);
        		    vo.setString("describe","login_user_password");
        		    ContentDAO dao=new ContentDAO(this.getFrameconn());
                    dao.updateValueObject(vo);
                	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
                	
                	username="username";
                	if(username.length()>0){// xuj update 2013-7-25当重新指定登录名时需提示出登录名指标有.号值的
                 		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
                        String A01="";
                        if(login_vo!=null) 
                          A01 = login_vo.getString("str_value").toLowerCase();
                        String pres[] = A01.split(",");
                        for(int i=0;i<pres.length;i++){
                        	if(pres[i].length()==3){
                        		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
                        		int n=0;
                        		while(this.frowset.next()){
                        			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+"),");
                        			if(n%7==0)
                        				msg.append("<br>");
                        			n++;
                        			flag = true;
                        		}
                        	}
                        }
                 	}
                 	if(flag){
                 		throw new Exception(msg.toString());
                 	}
                	
                }catch(Exception sqle){
          	      throw GeneralExceptionHandler.Handle(sqle);            
                }
               
            }else{
            	 if(username==null)
                     username="";
                 if(password==null)
                     password="";
             	if(username.equalsIgnoreCase(password))
             		throw new GeneralException(ResourceFactory.getProperty("error.noeqaul.userpwd"));        
                 try
                 {        
         		    String login_name=username+","+password;
         		    RecordVo vo=new RecordVo("constant");
         		    vo.setString("constant","SS_LOGIN_USER_PWD");        
         		    vo.setString("str_value",login_name);
         		    vo.setString("describe","login_user_password");
         		    ContentDAO dao=new ContentDAO(this.getFrameconn());
                     dao.updateValueObject(vo);
                 	ConstantParamter.putConstantVo(vo,"SS_LOGIN_USER_PWD");
                 	if(username.length()>0){// xuj update 2013-7-25当重新指定登录名时需提示出登录名指标有.号值的
                 		RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
                        String A01="";
                        if(login_vo!=null) 
                          A01 = login_vo.getString("str_value").toLowerCase();
                        String pres[] = A01.split(",");
                        for(int i=0;i<pres.length;i++){
                        	if(pres[i].length()==3){
                        		this.frowset = dao.search("select a0101,"+username+" from "+pres[i]+"A01 where "+username+" like '%.%'");
                        		int n=0;
                        		while(this.frowset.next()){
                        			msg.append(this.frowset.getString("a0101")+"("+this.frowset.getString(username)+"),");
                        			if(n%7==0)
                        				msg.append("<br>");
                        			n++;
                        			flag = true;
                        		}
                        	}
                        }
                 	}
                 	if(flag){
                 		throw new Exception(msg.toString());
                 	}
                 }
                 catch(Exception sqle)
                 {
           	      throw GeneralExceptionHandler.Handle(sqle);            
                 }
            }
            try
            { 
            	RecordVo vo=new RecordVo("constant");
            	ContentDAO dao=new ContentDAO(this.getFrameconn());
        		vo.setString("constant","SS_BIND_IPADDR");
        	    vo.setString("str_value",ip_addr);
        	    StringBuffer sql=new StringBuffer();
        	    sql.append("select * from constant where constant='SS_BIND_IPADDR'");
        	    this.frowset=dao.search(sql.toString());
        	    if(this.frowset.next())    	    	
                  dao.updateValueObject(vo);
        	    else
        	    	dao.addValueObject(vo);
                ConstantParamter.putConstantVo(vo,"SS_BIND_IPADDR");
            }catch(Exception e)
            {
              e.printStackTrace();	
            }
        }
        
        /**系统登录参数  SS_LOGIN_LOCK_FIELD 登录用户锁定指标，此处采用xml格式存储，方便后期有关登录参数设置放入此记录  2013-5-30 add xuj
        try
        { 
	        //ConstantXml xml = new ConstantXml(this.frameconn);
	        xml.saveValue("SS_LOGIN_LOCK_FIELD", lockfield);
        }catch(Exception e)
        {
          e.printStackTrace();	
        }
        
        if(islog){
        	this.getFormHM().put("@eventlog", this.userView.getUserName()+"通过设置认证用户名：从字段"+name+"设置字段为"+username);
        }
        */
    }

}
