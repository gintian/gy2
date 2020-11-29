package com.hjsj.hrms.businessobject.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Title:UserObjectBo</p>
 * <p>Description:用户对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-7:15:20:41</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UserObjectBo {
	
	private Connection conn;
	private ContentDAO dao=null;
	public UserObjectBo(Connection conn) {
		this.conn=conn;
		dao=new ContentDAO(this.conn);
	}
	
	
	/**
	 * 分析用户是否存在,和登记用户库中指定的
	 * 登记账号和operuser中已存的用户进行对比对析?自助平台的用户和业务平台的用户
	 * 是否合起来考滤？
	 * @param user_value
	 * @return
	 */
	private boolean isExist(String user_value)
	{
      StringBuffer strsql=new StringBuffer();
      boolean bflag=false;         
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        String password=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD",this.conn);
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
                if("#".equals(username)|| "".equals(username)) {
                    username="username";
                }
                password=login_name.substring(idx+1);
                if("#".equals(password)|| "".equals(password)) {
                    password="userpassword";
                }
            }
        }
		
        String dbpre=null;
        RecordVo vo =ConstantParamter.getConstantVo("SS_LOGIN",this.conn);
        if(vo==null) {
            dbpre="usr,";
        }
        /**
         * 登录主集usrA01/retA01/....
         */
     
        dbpre = vo.getString("str_value");
        /**default usra01*/
        if(dbpre==null|| "".equals(dbpre)) {
            dbpre="usr,";
        }
        StringTokenizer st = new StringTokenizer(dbpre, ",");
        while (st.hasMoreTokens())
        {
          String pre=st.nextToken().trim();
          strsql.append("select a0100 from ");
          strsql.append(pre);
          strsql.append("A01 ");
          strsql.append(" where lower(");
          strsql.append(username);
          strsql.append(")='");
          strsql.append(user_value.toLowerCase());
          strsql.append("' UNION ");
        }        
        strsql.append(" select username from operuser where lower(username)='");
        strsql.append(user_value.toLowerCase());
        strsql.append("'");
        ContentDAO dao=new ContentDAO(conn);
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString());
             if(rset.next()) {
                 bflag=true;
             }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}
        }*/
        return bflag;

	}
	/**
	 * 保存用户拥有的资源串
	 * @param resid 资源串，可以是逗号分开的串 1,2,3,...
	 * @param userView
	 * @param res_type IResourceConstant 资源类型 com.hrms.hjsj.sys.IResourceConstant
	 * @throws GeneralException
	 */
	public void saveResource(String resid,UserView userView,int res_type)throws GeneralException
	{
		if(userView.isSuper_admin()) {
            return;
        }
		try
		{
			userView.addResourceMx(resid,res_type);
			String userid=userView.getUserId();
			if(userView.getStatus()==4)
			{
				userid=userView.getDbname()+userid;
			}
			String flag=String.valueOf(userView.getStatus());
			SysPrivBo privbo=new SysPrivBo(userid,flag,this.conn,"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);		
			parser.addContent(resid);
			res_str=parser.outResourceContent();
		    if(res_str==null) {
                res_str="";
            }
		    RecordVo vo=new RecordVo("t_sys_function_priv");
		    vo.setString("id",userid);
		    vo.setString("status",flag/*GeneralConstant.ROLE*/);
		    vo.setString("warnpriv",res_str);
		    SysPrivBo sysbo=new SysPrivBo(vo,this.conn);
		    sysbo.save();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}		
	}
	
	/**
	 * 增加式媒体分类授权
	 * @param resid
	 * @param userView
	 * @throws GeneralException
	 */
	public void saveMediaResource(String resid, UserView userView) throws GeneralException {
		try {
			// 超级用户组下人员不用追加权限
			if (userView.isSuper_admin()) {
                return;
            }
			StringBuffer mediabuf = userView.getMediapriv();
			if (mediabuf.length() == 0) {
				mediabuf.append(",");
			}
			mediabuf.append(resid);
			mediabuf.append(",");
			String flag = String.valueOf(userView.getStatus());
			String objId = userView.getUserId();
			// 4:自助用户添加库前缀
			if (userView.getStatus() == 4) {
				objId = userView.getDbname() + objId;
			}
			RecordVo vo = new RecordVo("t_sys_function_priv");
			vo.setString("id", objId);
			vo.setString("status", flag);
			vo.setString("mediapriv", mediabuf.toString());
			SysPrivBo sysbo = new SysPrivBo(vo, this.conn);
			sysbo.save();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private /*synchronized*/ int getMaxOrder(){
		int nmax=1;
		String strsql="select max(ingrporder) as nmax from operuser";
		RowSet rset=null;
		try
		{
			rset=this.dao.search(strsql);
			if(rset.next()) {
                nmax=rset.getInt(nmax)+1;
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/*
		finally
		{
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}			
		}*/
		return nmax;
	}
	/**
	 * 取得常用花名册列表
	 * @return
	 */
	private List getLnameList()
	{
		ArrayList list=new ArrayList();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select tabid ,flag from lname");
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString() );
        	while(rset.next())
        	{
        		RecordVo vo=new RecordVo("lname");
        		vo.setInt("tabid",rset.getInt("tabid"));
        		vo.setString("flag",rset.getString("flag"));
        		list.add(vo);
        	}
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}        	
        }*/
		return list;
	}
	/**
	 * 取得高级花名册列表
	 * @return
	 */
	private List getMusterList()
	{
		ArrayList list=new ArrayList();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select tabid from muster_name");
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString() );
        	while(rset.next()) {
                list.add(rset.getString("tabid"));
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}        	
        }*/
		return list;
	}	
	
	/**
	 * 取得薪资类别salaryid
	 * @return
	 */
	private List getSalaryset()
	{
		ArrayList list=new ArrayList();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select salaryid from salaryset");
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString() );
        	while(rset.next()) {
                list.add(rset.getString("salaryid"));
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}        	
        }*/
		return list;
	}
	
	/**
	 * 取得人事异动模板tabid
	 * @return
	 */
	private List getTemplateid()
	{
		ArrayList list=new ArrayList();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select tabid from template_table");
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString() );
        	while(rset.next()) {
                list.add(rset.getString("tabid"));
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}        	
        }*/
		return list;
	}
	/**
	 * 取得人员库前缀
	 * @return
	 */
	private List getDbNameList()
	{
		ArrayList list=new ArrayList();
        StringBuffer strsql=new StringBuffer();
        strsql.append("select pre,dbname from dbname");
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString() );
        	while(rset.next()) {
                list.add(rset.getString("pre"));
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        /*
        finally
        {
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}        	
        }*/
		return list;
	}
	
	/**
	 * 根据角色号取得对应用户列表，用户信息通过LazyDynaBean保存
	 * ，属性包括username,userfullname,b0110,e0122,e01a1,email,phone,a0100，
	 * @param role_id
	 * @return
	 */
	public ArrayList findUserListByRoleId(String role_id)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		try
		{
			FieldItem field = DataDictionary.getFieldItem("a0107");
			String a0107 = "";
			if(field!=null){
				if("1".equals(field.getUseflag())&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
					a0107 = field.getCodesetid();
				}
			}
			ContentDAO dao=new ContentDAO(this.conn);
			strsql.append("select staff_id,role_id,status from t_sys_staff_in_role where role_id='");
			strsql.append(role_id);
			strsql.append("'");
			rset=dao.search(strsql.toString());
			String key=null;
			LazyDynaBean vo=null;
			while(rset.next())
			{
				String staff_id=rset.getString("staff_id");
				int status=rset.getInt("status");
				key=staff_id+"_"+status;
				switch(status)
				{
				case 0: //operuser
					UserView userView = new UserView(staff_id, this.conn);
					if(userView.canLogin(false))
					{
						vo=new LazyDynaBean();
						vo.set("username",staff_id);
						vo.set("a0100",userView.getDbname()+userView.getUserId());
						vo.set("userfullname",userView.getUserFullName());
						vo.set("email",userView.getUserEmail());
						vo.set("phone",userView.getUserTelephone());
						vo.set("b0110",userView.getUserOrgId());
						vo.set("e0122",userView.getUserDeptId());
						vo.set("e01a1",userView.getUserPosId());
						vo.set("status",String.valueOf(status));
						if(userView.getA0100().length()>0&&a0107.length()>0){
							RecordVo user_vo=new RecordVo(userView.getDbname()+"A01");
							user_vo.setString("a0100",userView.getA0100());
							if(dao.isExistRecordVo(user_vo))
							{
								String value= user_vo.getString("a0107");
								String codeitemdesc = AdminCode.getCodeName(a0107, value);
								if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.man"))!=-1){
									vo.set("a0107", ResourceFactory.getProperty("warn.email.male"));
								}else if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.woman"))!=-1){
									vo.set("a0107", ResourceFactory.getProperty("warn.email.female"));
								}else{
									vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
								}
							}
						}else{
							vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
						}
						//if(!userhm.containsKey(key))
						userhm.put(key,vo);
						//list.add(vo);
					}
					break;
				case 1://usra01\reta01...全部登录用户库
					String email=ConstantParamter.getEmailField().toLowerCase();
					String phone=ConstantParamter.getMobilePhoneField().toLowerCase();
					String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
					if(staff_id.length()==11){
						String dbpre=staff_id.substring(0,3);
						String a0100=staff_id.substring(3);
						RecordVo user_vo=new RecordVo(dbpre+"A01");
						user_vo.setString("a0100",a0100);
						if(dao.isExistRecordVo(user_vo))
						{
							user_vo=dao.findByPrimaryKey(user_vo);
							if(user_vo!=null)
							{
								String logon_user=user_vo.getString(loguser);
								/**账号为空不处理*/
								if(!(logon_user==null|| "".equals(logon_user)))
								{	
									vo=new LazyDynaBean();
									vo.set("username",logon_user);
									vo.set("a0100",dbpre+user_vo.getString("a0100"));
									vo.set("userfullname",user_vo.getString("a0101"));
									if(!"".equals(email)) {
                                        vo.set("email",user_vo.getString(email));
                                    }
									if(!"".equals(phone)) {
                                        vo.set("phone",user_vo.getString(phone));
                                    }
									vo.set("b0110",user_vo.getString("b0110"));
									vo.set("e0122",user_vo.getString("e0122"));
									vo.set("e01a1",user_vo.getString("e01a1"));
									vo.set("status",String.valueOf(status));
									
									if(a0107.length()>0){
											String value= user_vo.getString("a0107");
											String codeitemdesc = AdminCode.getCodeName(a0107, value);
											if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.man"))!=-1){
												vo.set("a0107", ResourceFactory.getProperty("warn.email.male"));
											}else if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.woman"))!=-1){
												vo.set("a0107", ResourceFactory.getProperty("warn.email.female"));
											}else{
												vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
											}
									
									}else{
										vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
									}
									//if(!userhm.containsKey(key))
									userhm.put(key,vo);
								}
							}
						}
					}
					break;
				case 2:
					RecordVo org_vo=new RecordVo("organization");
					org_vo.setString("codeitemid",staff_id);
					String sql = "select codesetid from organization where codeitemid='"+staff_id+"'";
					RowSet rs = null;
					try {
					rs = dao.search(sql);
					if (rs.next()) {
						org_vo.setString("codesetid",rs.getString("codesetid"));
					}
					if(dao.isExistRecordVo(org_vo))
					{
						try {
						org_vo=dao.findByPrimaryKey(org_vo);
						findUserListByOrgId(org_vo,userhm,a0107);
						} catch (Exception e) {
						
						}
					}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null) {
							rs.close();
						}
					}
					
				}
			}//for while loop end.
			Iterator iterator=userhm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				//String strkey=(String)entry.getKey();
				list.add(entry.getValue()/*iterator.next()*/);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	/**
	 * 根据组织机构查找对应信息,用户信息通过LazyDynaBean保存
	 * 属性包括username,userfullname,b0110,e0122,e01a1,email,phone，
	 * @param org_id
	 * @return
	 */
	public ArrayList findUserListByOrgId(String org_id,String codeid)
	{
	  ArrayList list=new ArrayList();		
	  try
	  {
		  FieldItem field = DataDictionary.getFieldItem("a0107");
			String a0107 = "";
			if(field!=null){
				if("1".equals(field.getUseflag())&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
					a0107 = field.getCodesetid();
				}
			}
		HashMap userhm=new HashMap();		  
		RecordVo org_vo=new RecordVo("organization");
		org_vo.setString("codesetid",codeid);
		org_vo.setString("codeitemid",org_id);
		org_vo=dao.findByPrimaryKey(org_vo);
		findUserListByOrgId(org_vo,userhm,a0107);
		Iterator  iterator=userhm.values().iterator();
		while(iterator.hasNext())
		{
			list.add(iterator.next());
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return list;
	}
	
	/**
	 * 查询对应的电子信箱的地址
	 * @param username
	 * @param dbpre
	 * @return
	 */
	public String getEmailAddress(String username,String dbpre)
	{
	  String email_addr="";
	  try
	  {
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo=null;
		if(dbpre==null|| "".equals(dbpre))
		{
			vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
			{
				dbpre=vo.getString("nbase");
				if(dbpre==null|| "".equals(dbpre)) {
                    return "";
                }
				username=vo.getString("a0100");
			}
			else {
                return "";
            }
		}
		vo=new RecordVo(dbpre+"a01");
		vo.setString("a0100",username);
		vo=dao.findByPrimaryKey(vo);
		if(vo==null) {
            return "";
        }
		String email_field=ConstantParamter.getEmailField();
		if("".equals(email_field)) {
            return "";
        }
		email_addr=vo.getString(email_field);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return email_addr;
	}

	/**
	 * 查询对应的电子信箱的地址
	 * @param username
	 * @param dbpre
	 * @return
	 */
	public String getMobilePhone(String username,String dbpre)
	{
	  String phone_number="";
	  try
	  {
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo=null;
		if(dbpre==null|| "".equals(dbpre))
		{
			vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
			{
				dbpre=vo.getString("nbase");
				if(dbpre==null|| "".equals(dbpre)) {
                    return "";
                }
				username=vo.getString("a0100");
			}
			else {
                return "";
            }
		}
		vo=new RecordVo(dbpre+"a01");
		vo.setString("a0100",username);
		vo=dao.findByPrimaryKey(vo);
		if(vo==null) {
            return "";
        }
		String phone_field=ConstantParamter.getMobilePhoneField();
		if("".equals(phone_field)) {
            return "";
        }
		phone_number=vo.getString(phone_field);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return phone_number;
	}
	/**
	 * 校验用户或密码是否有特殊字符
	 * @param name
	 * @return
	 */
	public boolean validateUserNamePwd(String name)
	{
		boolean bflag=false;
		String ctrlvalue="`=[];'\\,./·【】；‘’、，。、~!@#$%^&*()+{}:\"|<>?！￥……（）：“”《》？";
		for(int i=0;i<name.length();i++)
		{
			char c=name.charAt(i);
			if(ctrlvalue.indexOf(c)!=-1)
			{
				bflag=true;
				break;
			}
		}
		return bflag;
	}
	
	/**
	 * 校验密码中是否含有逗号
	 * @param name
	 * @return
	 * @throws GeneralException 
	 */
	public void validateUserNamePwdComma(String name) throws GeneralException
	{
		if(name==null) {
            return;
        }
		boolean bflag=false;
		String ctrlvalue="`";
		for(int i=0;i<name.length();i++)
		{
			char c=name.charAt(i);
			if(ctrlvalue.indexOf(c)!=-1)
			{
				bflag=true;
				break;
			}
		}
		if(bflag){
			throw new GeneralException(ResourceFactory.getProperty("error.password.validate.pwdcomma"));
		}
	}
	/**
	 * system.properties
	 * passwordrule=0|1
	 * @param pwd
	 * @return
	 */
	public boolean validatePassword(String pwd)
	{
		boolean bflag=true;
		boolean bnumber=false;
		boolean bletter=false;
		String value=SystemConfig.getPropertyValue("passwordrule");
		if(value==null||value.length()==0|| "0".equalsIgnoreCase(value)) {
            return bflag;
        }
		if(pwd.length()<8) {
            return false;
        }
		for(int i=0;i<pwd.length();i++)
		{
			char c=pwd.charAt(i);
			if(Character.isDigit(c))
			{
				bnumber=true;
			}
			else {
                bletter=true;
            }
			for(int j=0;j<pwd.length();j++)
			{
				char d=pwd.charAt(j);
				if((c==d)&&(i!=j))
				{
					bflag=false;
					break;
				}
			}
			if(!bflag) {
                break;
            }
		}
		return (bflag&&bnumber&&bletter);
	}	
	/**
	 * 现对密码复杂度进行0低|1中|2强三种模式划分，另system.properties文件中增加密码长度参数控制passwordlength=数字
	 * @param pwd
	 * @return
	 * @throws GeneralException 
	 */
	public String validatePasswordNew(String pwd) throws GeneralException
	{
		boolean bflag=true;
		boolean bnumber=false;
		boolean bletter=false;
		boolean blowletter=false;
		boolean bupperletter=false;
		boolean bsign=false;
		String msg = "ok";
		pwd = PubFunc.keyWord_reback(pwd);
		//String value=SystemConfig.getPropertyValue("passwordrule");
		String value=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDRULE);
		//String pwdlength=SystemConfig.getPropertyValue("passwordlength");
		String	pwdlength=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDLENGTH);
		if(value==null||value.length()==0|| "0".equalsIgnoreCase(value)){
			return msg;
		}
		int pwdlen = 8;
		if("1".equals(value)){//中度密码  密码长度不得低于参数passwordlength位，必须包字母、数字
			if(pwdlength==null||pwdlength.length()==0){
				if(pwd.length()<pwdlen) {
                    msg= ResourceFactory.getProperty("error.password.validate.moderatecatch").replace("{0}", ""+pwdlen);
                }
			}else{
				try{
					pwdlen = Integer.parseInt(pwdlength);
				}catch(Exception e){
					pwdlen=8;
				}
				pwdlen=pwdlen<=0?8:pwdlen;
				if(pwd.length()< pwdlen) {
                    msg= ResourceFactory.getProperty("error.password.validate.moderatecatch").replace("{0}", ""+pwdlen);
                }
			}
			if("ok".equals(msg)){
				for(int i=0;i<pwd.length();i++)
				{
					char c=pwd.charAt(i);
					if(Character.isDigit(c))//数字
					{
						bnumber=true;
					}
					else if(Character.isLetter(c))//字母
                    {
                        bletter=true;
                    }
				}
				if(!(bnumber&&bletter)){
					msg= ResourceFactory.getProperty("error.password.validate.moderatecatch").replace("{0}", ""+pwdlen);
				}
			}
		}else if ("2".equals(value)){//强度密码  密码长度不得低于参数passwordlength位，必须包含大、小写字母、数字、特殊符号， 且不能重复
			pwdlen=10;
			if(pwdlength==null||pwdlength.length()==0){
				if(pwd.length()<pwdlen) {
                    msg= ResourceFactory.getProperty("error.password.validate.strongcatch").replace("{0}", ""+pwdlen);
                }
			}else{
				try{
					pwdlen = Integer.parseInt(pwdlength);
				}catch(Exception e){
					pwdlen=10;
				}
				pwdlen=pwdlen<=0?10:pwdlen;
				if(pwd.length()< pwdlen) {
                    msg= ResourceFactory.getProperty("error.password.validate.strongcatch").replace("{0}", ""+pwdlen);
                }
			}
			if("ok".equals(msg)){
				for(int i=0;i<pwd.length();i++)
				{
					char c=pwd.charAt(i);
					if(Character.isDigit(c))//数字
					{
						bnumber=true;
					}
					/*else if(Character.isUpperCase(c))//大写字母
						bupperletter=true;
					else if(Character.isLowerCase(c))//小写字母
						blowletter=true;*/
					else if(Character.isLetter(c))//字母
                    {
                        bletter=true;
                    } else if("%$#@!~^&*()+\"',".indexOf(c)!=-1) {
                        bsign=true;
                    }
					/**判断密码不能有相同字符*/
					for(int j=0;j<pwd.length();j++)
					{
						char d=pwd.charAt(j);
						//不区分大小写
						if(/*(c==d)*/String.valueOf(d).equalsIgnoreCase(String.valueOf(c))&&(i!=j))
						{
							bflag=false;
							break;
						}
					}
					if(!bflag) {
                        break;
                    }
				}
				if(!(bflag&&bnumber&&bletter/*blowletter&&bupperletter*/&&bsign)){
					msg= ResourceFactory.getProperty("error.password.validate.strongcatch").replace("{0}", ""+pwdlen);
				}
			}
		}
		if(!"ok".equals(msg)) {
            throw new GeneralException(msg);
        }
		return msg;
	}
	
	/**
	 * 查找待发人员信息
	 * @param org_vo
	 * @param userhm
	 */
	private void findUserListByOrgId(RecordVo org_vo,HashMap userhm)throws GeneralException
	{
		if(org_vo==null) {
            return;
        }
		String codesetid=org_vo.getString("codesetid");
		String codeitemid=org_vo.getString("codeitemid");
		StringBuffer strsql=new StringBuffer();
		DbNameBo dbbo=new DbNameBo(this.conn);
		String email=ConstantParamter.getEmailField();
		String phone=ConstantParamter.getMobilePhoneField();	
		String loguser=ConstantParamter.getLoginUserNameField();
		try
		{
			ArrayList list=dbbo.getAllLoginDbNameList();
			StringBuffer colums=new StringBuffer();
			colums.append("a0100,a0101,b0110,e0122,e01a1,");
			colums.append(loguser);
			colums.append(",");
			if(!"".equals(email))
			{
				colums.append(email);
				colums.append(",");
			}
			if(!"".equals(phone))
			{
				colums.append(phone);
				colums.append(",");
			}
			//colums.setLength(colums.length()-1);
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				strsql.append("select ");
				strsql.append(colums.toString());
				strsql.append("'");
				strsql.append(vo.getString("pre").toUpperCase());
				strsql.append("' dbpre from ");
				strsql.append(vo.getString("pre"));
				strsql.append("a01");
				strsql.append(" where ");
				if("UN".equalsIgnoreCase(codesetid)) {
                    strsql.append(" b0110 like '");
                } else if("UM".equalsIgnoreCase(codesetid)) {
                    strsql.append(" e0122 like '");
                } else {
                    strsql.append(" e01a1 like '");
                }
				strsql.append(codeitemid);
				strsql.append("%'");
				strsql.append(" and (");
				strsql.append(loguser);
				strsql.append(" is not null");
				if(Sql_switcher.searchDbServer()==1){
					strsql.append(" and "+loguser);
					strsql.append("<>''");
				}
				strsql.append(")");
				strsql.append(" union ");
			}
			strsql.setLength(strsql.length()-7);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				LazyDynaBean vo=new LazyDynaBean();
				String str_key=rset.getString("dbpre")+rset.getString("a0100")+"_2";
				vo.set("username",rset.getString(loguser));
				vo.set("a0100",rset.getString("dbpre")+rset.getString("a0100"));
				vo.set("userfullname",rset.getString("a0101")==null?"":rset.getString("a0101"));
				if(!"".equals(email)) {
                    vo.set("email",rset.getString(email)==null?"":rset.getString(email));
                }
				if(!"".equals(phone))
				{
					String temp=rset.getString(phone);
					vo.set("phone",(temp==null)?"":temp);
				}
				vo.set("b0110",rset.getString("b0110")==null?"":rset.getString("b0110"));
				vo.set("e0122",rset.getString("e0122")==null?"":rset.getString("e0122"));
				vo.set("e01a1",rset.getString("e01a1")==null?"":rset.getString("e01a1"));
				vo.set("status",String.valueOf(1));
//				if(!userhm.containsKey(str_key))
				userhm.put(str_key,vo);					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	private void findUserListByOrgId(RecordVo org_vo,HashMap userhm,String a0107)throws GeneralException
	{
		if(org_vo==null) {
            return;
        }
		String codesetid=org_vo.getString("codesetid");
		String codeitemid=org_vo.getString("codeitemid");
		StringBuffer strsql=new StringBuffer();
		DbNameBo dbbo=new DbNameBo(this.conn);
		String email=ConstantParamter.getEmailField();
		String phone=ConstantParamter.getMobilePhoneField();	
		String loguser=ConstantParamter.getLoginUserNameField();
		try
		{
			ArrayList list=dbbo.getAllLoginDbNameList();
			StringBuffer colums=new StringBuffer();
			colums.append("a0100,a0101,b0110,e0122,e01a1,");
			colums.append(loguser);
			colums.append(",");
			if(!"".equals(email))
			{
				colums.append(email);
				colums.append(",");
			}
			if(!"".equals(phone))
			{
				colums.append(phone);
				colums.append(",");
			}
			if(a0107.length()>0){
				colums.append("a0107");
				colums.append(",");
			}
			//colums.setLength(colums.length()-1);
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo=(RecordVo)list.get(i);
				strsql.append("select ");
				strsql.append(colums.toString());
				strsql.append("'");
				strsql.append(vo.getString("pre").toUpperCase());
				strsql.append("' dbpre from ");
				strsql.append(vo.getString("pre"));
				strsql.append("a01");
				strsql.append(" where ");
				if("UN".equalsIgnoreCase(codesetid)) {
                    strsql.append(" b0110 like '");
                } else if("UM".equalsIgnoreCase(codesetid)) {
                    strsql.append(" e0122 like '");
                } else {
                    strsql.append(" e01a1 like '");
                }
				strsql.append(codeitemid);
				strsql.append("%'");
				strsql.append(" and (");
				strsql.append(loguser);
				strsql.append(" is not null");
				if(Sql_switcher.searchDbServer()==1){
					strsql.append(" and "+loguser);
					strsql.append("<>''");
				}
				strsql.append(")");
				strsql.append(" union ");
			}
			strsql.setLength(strsql.length()-7);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				LazyDynaBean vo=new LazyDynaBean();
				String str_key=rset.getString("dbpre")+rset.getString("a0100")+"_2";
				vo.set("username",rset.getString(loguser));
				vo.set("a0100",rset.getString("dbpre")+rset.getString("a0100"));
				vo.set("userfullname",rset.getString("a0101")==null?"":rset.getString("a0101"));
				if(!"".equals(email)) {
                    vo.set("email",rset.getString(email)==null?"":rset.getString(email));
                }
				if(!"".equals(phone))
				{
					String temp=rset.getString(phone);
					vo.set("phone",(temp==null)?"":temp);
				}
				vo.set("b0110",rset.getString("b0110")==null?"":rset.getString("b0110"));
				vo.set("e0122",rset.getString("e0122")==null?"":rset.getString("e0122"));
				vo.set("e01a1",rset.getString("e01a1")==null?"":rset.getString("e01a1"));
				vo.set("status",String.valueOf(1));
//				if(!userhm.containsKey(str_key))
				if(a0107.length()>0){
					String value= rset.getString("a0107")==null?"":rset.getString("a0107");
					String codeitemdesc = AdminCode.getCodeName(a0107, value);
					if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.man"))!=-1){
						vo.set("a0107", ResourceFactory.getProperty("warn.email.male"));
					}else if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.woman"))!=-1){
						vo.set("a0107", ResourceFactory.getProperty("warn.email.female"));
					}else{
						vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
					}
			
				}else{
					vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
				}
				userhm.put(str_key,vo);					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 删除一些资源，临时表
	 * @param username
	 * @throws GeneralException
	 */
	private void deleteTempTable(String username)throws GeneralException
	{
		List dblist=getDbNameList();	
		DbWizard db=new DbWizard(this.conn);
		String temp=username+"BResult";
		Table table=new Table(temp);
		db.dropTable(table);
		
		temp=username+"KResult";	
		table.setName(temp);
		db.dropTable(table);
		
		for(int i=0;i<dblist.size();i++)
		{
			temp=username+dblist.get(i)+"result";
			table.setName(temp);
			db.dropTable(table);
		}
		/**常用花名册列表*/
		List list=getLnameList();
		RecordVo vo=null;
		for(int j=0;j<list.size();j++)
		{
			 vo=(RecordVo)list.get(j);

			 if("2".equals(vo.getString("flag"))) {
                 temp="m"+vo.getString("tabid")+"_"+username+"_B";
             } else if("3".equals(vo.getString("flag"))) {
                 temp="m"+vo.getString("tabid")+"_"+username+"_K";
             } else
			 {
				 for(int i=0;i<dblist.size();i++) {
                     temp="m"+vo.getString("tabid")+"_"+username+"_"+dblist.get(i);
                 }
			 }
			 table.setName(temp);
			 db.dropTable(table);
		}
		/**高级花名册*/
		list=getMusterList();
		for(int j=0;j<list.size();j++)
		{
			 temp=username+"_muster_"+list.get(j);
			 table.setName(temp);
			 db.dropTable(table);
		}	
		/**薪资*/
		list = this.getSalaryset();
		for(int j=0;j<list.size();j++)
		{
			 temp=username+"_salary_"+list.get(j);
			 table.setName(temp);
			 if(db.isExistTable(temp,false)) {
                 db.dropTable(table);
             }
		}
		/**模板*/
		list = this.getTemplateid();
		for(int j=0;j<list.size();j++)
		{
			 temp=username+"templet_"+list.get(j);
			 table.setName(temp);
			 if(db.isExistTable(temp,false)) {
                 db.dropTable(table);
             }
		}
	}
	
	
	/**
	 * 删除用户，连带删除许多临时表
	 * @param vo
	 * @param bDelete =true 删除　，=false不删除
	 * @throws GeneralException
	 */
	public void remove_User(RecordVo vo,boolean bDelete)throws GeneralException
	{
		if(!"operuser".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		/**删除查询结果表*/
		String username=vo.getString("username");
		try
		{
			dao.deleteValueObject(vo);
			if(bDelete) {
                deleteTempTable(username);
            }
			/**删除授权记录*/
			RecordVo priv_vo=new RecordVo("t_sys_function_priv");
			priv_vo.setString("id",username);
			priv_vo.setInt("status",0);
			dao.deleteValueObject(priv_vo);
			/**删除角色列表*/
			removeRoleList(username);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 删除对应的角色
	 * @param username
	 * @throws GeneralException
	 */
	private void removeRoleList(String username)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		try
		{
			strsql.append("delete from t_sys_staff_in_role where staff_id='");
			strsql.append(username);
			strsql.append("' and status=0");
			dao.update(strsql.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 更新用户对象信息
	 * @param vo
	 * @throws GeneralException
	 */
	public void update_User(RecordVo vo)throws GeneralException
	{
		if(!"operuser".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		try
		{
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);			
		}		
	}
	/**
	 * 增加用户时，对此用户增加一些临时性用表,
	 * 比如一些查询结果表
	 * @param username
	 * @throws GeneralException
	 */
	private void add_TempTable(String username)throws GeneralException
	{
		DbWizard db=new DbWizard(this.conn);
		/**xxxBResult*/
		StringBuffer temp=new StringBuffer();
		temp.append(username);
		temp.append("BResult");
		Table table=new Table(temp.toString());
		Field field=new Field("B0110");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		table.addField(field);
		Field a0100=new Field("A0100");
		a0100.setDatatype(DataType.STRING);	
		a0100.setLength(8);
		table.addField(a0100);
		UsrResultTable resulttable = new UsrResultTable();
		if(resulttable.isNumber(username)){
			throw GeneralExceptionHandler.Handle(new Exception("第一个字符为数字的用户名不能使用登记表!"));
		}
		db.createTable(table);
		
		temp.setLength(0);
		temp.append(username);
		temp.append("KResult");		
		table.setName(temp.toString());
		table.clear();
		Field e01a1=new Field("E01A1");
		e01a1.setDatatype(DataType.STRING);
		e01a1.setLength(30);
		table.addField(e01a1);		
		db.createTable(table);
		
		table.clear();	
		Field b0110=new Field("B0110");
		b0110.setDatatype(DataType.STRING);
		b0110.setLength(30);
		table.addField(b0110);
		a0100=new Field("A0100");
		a0100.setDatatype(DataType.STRING);	
		a0100.setLength(8);
		table.addField(a0100);
		
		List dblist=getDbNameList();	
		for(int i=0;i<dblist.size();i++)
		{
			temp.setLength(0);	
			temp.append(username);
			temp.append(dblist.get(i));
			temp.append("result");
			table.setName(temp.toString());
			db.createTable(table);
		}
		
	}
	
	/**
	 * 对帐号分配中的用户如果涉及到查询结果时，需要创建中间查询结果
	 * @param dbpre
	 * @param flag 
	 * @param username
	 * @throws GeneralException
	 */
	public void createResultTable(String dbpre,String flag,String username)throws GeneralException
	{
		DbWizard db=new DbWizard(this.conn);
		String tablename=null;
		if("A".equalsIgnoreCase(flag))
		{
			tablename=username+dbpre+"result";
			
		}
		else if("B".equalsIgnoreCase(flag))
		{
			tablename=username+"bresult";
		}
		else //"K"
		{
			tablename=username+"kresult";
		}
		if(db.isExistTable(tablename, false)) {
            return;
        }
		try
		{
			add_TempTable(username);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	private void addOperuser(RecordVo vo,RecordVo gvo)throws GeneralException
	{
		try
		{
			String groupname=gvo.getString("groupname");
			RecordVo priv_vo=new RecordVo("operuser");
			priv_vo.setString("username",groupname);
			//priv_vo=dao.findByPrimaryKey(priv_vo);
			String funcstr="";
			String tabstr="";
			String fieldstr="";
			String warnpriv="";
			if(dao.isExistRecordVo(priv_vo))
			{
//				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
//				{
//					funcstr=priv_vo.getString("functionpriv");
//					tabstr=priv_vo.getString("tablepriv");
//					fieldstr=priv_vo.getString("fieldpriv");
//					warnpriv=priv_vo.getString("warnpriv");
//					priv_vo.setString("cardpriv", "");
//					priv_vo.setString("functionpriv", "");
//					priv_vo.setString("tablepriv", "");
//					priv_vo.setString("fieldpriv", "");
//				}
				priv_vo.setString("username",vo.getString("username"));
				priv_vo.setString("fullname",vo.getString("fullname"));
				priv_vo.setString("password",vo.getString("password"));
				priv_vo.setString("email",vo.getString("email"));
				priv_vo.setString("phone",vo.getString("phone"));
				priv_vo.setString("org_dept",vo.getString("org_dept"));
				priv_vo.setInt("photoid",vo.getInt("photoid"));
				priv_vo.setInt("groupid",vo.getInt("groupid"));
				priv_vo.setInt("ingrporder",vo.getInt("ingrporder"));
				priv_vo.setInt("roleid",vo.getInt("roleid"));
				priv_vo.setInt("state",vo.getInt("state"));
				priv_vo.setInt("userflag",vo.getInt("userflag"));	
				priv_vo.setDate("modtime", vo.getString("modtime"));
				//priv_vo.setString("modtime",vo.getString("modtime"));
				dao.addValueObject(priv_vo);
//				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
//				{
//					priv_vo=new RecordVo("operuser");;
//					priv_vo.setString("username", vo.getString("username"));
//					priv_vo.setString("functionpriv", funcstr);
//					dao.updateValueObject(priv_vo);
//					
//					priv_vo=new RecordVo("operuser");;
//					priv_vo.setString("username", vo.getString("username"));
//					priv_vo.setString("tablepriv", tabstr);
//					dao.updateValueObject(priv_vo);
//					
//					priv_vo=new RecordVo("operuser");;
//					priv_vo.setString("username", vo.getString("username"));
//					priv_vo.setString("fieldpriv", fieldstr);
//					dao.updateValueObject(priv_vo);
//					
//					priv_vo=new RecordVo("operuser");;
//					priv_vo.setString("username", vo.getString("username"));
//					priv_vo.setString("warnpriv", warnpriv);
//					dao.updateValueObject(priv_vo);					
//				}
			}		
			else {
                dao.addValueObject(vo);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 
	 * 保存用户对象
	 * @param badd =true增加临时表　=false不增加临时表
	 * @param vo
	 */
	public void add_User(RecordVo vo,boolean badd)throws GeneralException
	{
		if(!"operuser".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		if(isExist(vo.getString("username"))) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.user.exist")));
        }
		try
		{
			vo.setInt("ingrporder",getMaxOrder());
			try
			{			
				int groupid=vo.getInt("groupid");
				RecordVo gvo=new RecordVo("usergroup");
				gvo.setInt("groupid",groupid);
				gvo=dao.findByPrimaryKey(gvo);			
				//dao.isExistRecordVo(gvo);
				addOperuser(vo,gvo);
				//dao.addValueObject(vo);
				if(badd) {
                    add_TempTable(vo.getString("username"));
                }
				/**权限复制,把所在组的权限复制一份给新建的用户*/
				if(gvo!=null)
				{
					String groupname=gvo.getString("groupname");
					RecordVo priv_vo=new RecordVo("t_sys_function_priv");
					priv_vo.setString("id",groupname);
					priv_vo.setInt("status", 0);
					
					if(!this.isExistRecordVo(priv_vo, dao))
					{
						priv_vo.setString("id",vo.getString("username"));
						if(!this.isExistRecordVo(priv_vo, dao))
						{
							dao.addValueObject(priv_vo);
						}
					}else{
						priv_vo=dao.findByPrimaryKey(priv_vo);
						priv_vo.setString("id",vo.getString("username"));
						if(!this.isExistRecordVo(priv_vo, dao))
						{
							dao.addValueObject(priv_vo);
						}
					}

					
				}

			}
			catch(Exception pe)
			{
				pe.printStackTrace();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	private boolean isExistRecordVo(RecordVo vo,ContentDAO dao){
		RecordVo tmpvo =null;
		try{
			tmpvo = new RecordVo(vo.getModelName());
			ArrayList keylist=vo.getKeylist();
			for(int i=0;i<keylist.size();i++){
				String name= (String)keylist.get(i);
				tmpvo.setString(name, vo.getString(name));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
		return dao.isExistRecordVo(tmpvo);
		
	}
	/**
	 * 依据组名，取得对应的组号
	 * @param name
	 * @return
	 */
	private int getGroupIdByUserGroup(String name)
	{
		int groupid=1;
		String strsql="select groupid from usergroup where groupname='"+name+"'";
		RowSet rset=null;
		try
		{
			rset=dao.search(strsql);
			if(rset.next()) {
                groupid=rset.getInt("groupid");
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null) {
                    rset.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return groupid;
		
	}
	/**
	 * 根据名称取得所在组号
	 * @param name
	 * @return
	 */
	public int getCurrentGroupId(String name)
	{
		RecordVo vo=new RecordVo("operuser");
		vo.setString("username",name);
		int groupid=1;
		int roleid=1;
		try
		{
			vo=dao.findByPrimaryKey(vo);
			roleid=vo.getInt("roleid");
			if(roleid==1) {
                groupid=getGroupIdByUserGroup(name);
            } else {
                groupid= vo.getInt("groupid");
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return groupid;		
	}
	
	/**
	 * (1)首次密码修改
	 * （2）密码修改时间
	 * (2)记录前N次密码
	 * @param newpwd
	 * @param username
	 */
	public void doHistoryPwd(String newpwd,String username){
		RowSet frowset = null;
		try{
			//首次密码修改 xuj add 2013-10-9
			String login_history_pwd = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD);
	        if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))||SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS).length()>0||!(login_history_pwd==null||login_history_pwd.length()==0||"0".equals(login_history_pwd))){
	        	String sql ="select his_password from t_sys_login_user_info where username='"+username+"'";
	        	frowset = dao.search(sql);
	        	if(frowset.next()){
	        		String his_password = frowset.getString("his_password");
	        		Des des0=new Des(); 
	        		his_password = des0.DecryPwdStr(his_password);
	        		RecordVo vo=new RecordVo("t_sys_login_user_info");
		        	vo.setString("username", username);
		        	vo.setString("first_login", "0");
		        	vo.setDate("pwd_modtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	        		//前N次密码不能重复
	        		if(login_history_pwd==null||login_history_pwd.length()==0||"0".equals(login_history_pwd)){//不控制前N次密码重复,只保存当前密码
						newpwd=des0.EncryPwdStr(newpwd);  
	        			vo.setString("his_password", newpwd);
	        		}/*else if("0".equals(login_history_pwd)){//记录全部历史密码
	        			his_password+=","+newpwd;
	        			newpwd=des0.EncryPwdStr(his_password);
	        			vo.setString("his_password", newpwd);
	        		}*/else{
	        			try{
	        				int hisPwdCount = Integer.parseInt(login_history_pwd);
	        				String hisPwds []= null;
	        				boolean flag = false;
	        				if(his_password.endsWith("`")){//历史密码出现空密码
	        					hisPwds = (his_password+"空").split("`");
	        					flag = true;
	        				}else{
	        					hisPwds = his_password.split("`");
	        				}
	        				if(hisPwds.length>=hisPwdCount){
	        					ArrayList list = new ArrayList();
	        					Collections.addAll(list, hisPwds);
	        					list.remove(0);
	        					list.add(newpwd);
	        					his_password = list.toString();
	        					if(flag) {
                                    his_password = his_password.substring(1,his_password.length()-1).replaceAll(", ", "`").trim().replace("空", "");
                                } else {
                                    his_password = his_password.substring(1,his_password.length()-1).replaceAll(", ", "`").trim();
                                }
	        					newpwd=des0.EncryPwdStr(his_password);
	    	        			vo.setString("his_password", newpwd);
	        				}else{
	        					his_password+="`"+newpwd;
	    	        			newpwd=des0.EncryPwdStr(his_password);
	    	        			vo.setString("his_password", newpwd);
	        				}
	        			}catch(Exception e){
	        				newpwd=des0.EncryPwdStr(newpwd);  
		        			vo.setString("his_password", newpwd);
	        			}
	        		}
	        		dao.updateValueObject(vo);
	        	}else{
		        	RecordVo vo=new RecordVo("t_sys_login_user_info");
		        	vo.setString("username", username);
			        Des des0=new Des(); 
					newpwd=des0.EncryPwdStr(newpwd);  
		        	vo.setString("his_password", newpwd);
		        	vo.setString("first_login", "0");
		        	vo.setDate("pwd_modtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		        	dao.addValueObject(vo);
	        	}
	        	
	        	//更新登录类缓存首次密码修改
	        	if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))){
	        		ConstantParamter.setUserAttribute(username, "first_login", "0");
	        		//集群环境刷新其他节点缓存数据
		        HashMap paramMap = new HashMap();
		        paramMap.put("username",username);
		        SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_LOGIN_USERINFO,paramMap);
	        	}
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(frowset!=null) {
                try {
                    frowset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * 前N次密码不能重复
	 * @param newpwd
	 * @param username
	 * @return
	 */
	public boolean checkHistoryPwd(String newpwd,String username){
		boolean flag = false;
		RowSet frowset = null;
		try{
			String login_history_pwd = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_HISTORY_PWD);
	        if(!(login_history_pwd==null||login_history_pwd.length()==0||"0".equals(login_history_pwd))){
	        	String sql ="select his_password from t_sys_login_user_info where username='"+username+"'";
	        	frowset = dao.search(sql);
	        	if(frowset.next()){
	        		String his_password = frowset.getString("his_password");
	        		Des des0=new Des(); 
	        		his_password = des0.DecryPwdStr(his_password);
	        		if(Sql_switcher.searchDbServer()==1){
	        			his_password = his_password.toUpperCase();
	        			newpwd = newpwd.toUpperCase();
	        		}
	        		if(newpwd.length()==0){
	        			String hisPwds []= null;
        				if(his_password.endsWith("`")){//历史密码出现空密码
        					hisPwds = (his_password+"空").split("`");
        				}else{
        					hisPwds = his_password.split("`");
        				}
        				for(int i=0;i<hisPwds.length;i++){
        					if(hisPwds[i].length()==0|| "空".equals(hisPwds[i])){
        						flag=true;
        						break;
        					}
        				}
	        		}else{
	        			if(("`"+his_password+"`").indexOf("`"+newpwd+"`")!=-1){
	        				flag=true;
	        			}
	        		}
	        	}
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(frowset!=null) {
                try {
                    frowset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return flag;
	}
	
	/**
	 * 置为首次密码必须修改状态
	 * @param username
	 */
	public void change2firstPwd(String username){
		if("1".equals(SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.LOGIN_FIRST_CHANG_PWD))){
			String sql = "update t_sys_login_user_info set first_login='1' where username in ("+username+")";
			try {
				dao.update(sql);
				String [] usernames = username.split(",");
				//更新登录类缓存
				for(int i=0;i<usernames.length;i++){
					String name = usernames[i];
					if(name.length()>2){
						ConstantParamter.setUserAttribute(name.substring(1, name.length()-1), "first_login", "1");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 帐号管理中可以重写给自助用户设置登录名，登录名可改
	 * @param newName
	 * @param oldName
	 */
	public void updateUserName(String newName,String oldName){
		if(newName==null||newName.length()==0){
			String sql = "delete from t_sys_login_user_info where username='"+oldName+"'";
			try {
				dao.update(sql);
	        	//对于原有用户名在缓存数据跟新为首次登录，防止下次新建重名用户名未能首次密码修改，模拟删除操作
	        	ConstantParamter.setUserAttribute(oldName, "first_login", "1");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			String sql = "update t_sys_login_user_info set username='"+newName+"',first_login='1'  where username='"+oldName+"'";
			try {
				dao.update(sql);
	        	//对于原有用户名在缓存数据跟新为首次登录，防止下次新建重名用户名未能首次密码修改，模拟删除操作
	        	ConstantParamter.setUserAttribute(oldName, "first_login", "1");
	        	ConstantParamter.setUserAttribute(newName, "first_login", "1");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 业务用户删除操作
	 * @param newName
	 * @param oldName
	 */
	public void delUserName(String oldName){
			String sql = "delete from t_sys_login_user_info where username='"+oldName+"'";
			try {
				dao.update(sql);
	        	//防止下次新建重名用户名未能首次密码修改，模拟删除操作
	        	ConstantParamter.setUserAttribute(oldName, "first_login", "1");
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void unlockUser(String dbpre,String a0100,String username){
		DbNameBo dbnamebo = new DbNameBo(conn);
        String lockfield = dbnamebo.getLogonLockField();
        if(lockfield.length()==5){
			String sql = "update "+dbpre+"A01 set "+lockfield+"='2' where a0100='"+a0100+"'";
			try {
				dao.update(sql);
				ConstantParamter.setUserAttribute(username, "locked_login", "0");
				com.hrms.hjsj.sys.SecurityLock.clearCounter(username);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void updatePWDModTime(ArrayList values){
		String passwordlockdays=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
    	String passworddays=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDDAYS);
    	if(passwordlockdays.length()>0||passworddays.length()>0){
    		try {
    			dao.batchUpdate("update t_sys_login_user_info set pwd_modtime="+Sql_switcher.dateValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+" where username=?", values);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
	}
	
	/**
	 * 获取微信消息用户
	 * @param role_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList findUserIdListByRoleId(String role_id)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		RowSet rset=null;
		HashMap userhm=new HashMap();
		try
		{
			FieldItem field = DataDictionary.getFieldItem("a0107");
			String a0107 = "";
			if(field!=null){
				if("1".equals(field.getUseflag())&&field.getCodesetid().length()>0&&!"0".equals(field.getCodesetid())){
					a0107 = field.getCodesetid();
				}
			}
			ContentDAO dao=new ContentDAO(this.conn);
			strsql.append("select staff_id,role_id,status from t_sys_staff_in_role where role_id='");
			strsql.append(role_id);
			strsql.append("'");
			rset=dao.search(strsql.toString());
			String key=null;
			LazyDynaBean vo=null;
			while(rset.next())
			{
				String staff_id=rset.getString("staff_id");
				int status=rset.getInt("status");
				key=staff_id+"_"+status;
				switch(status)
				{
				case 0: //operuser
					
					break;
				case 1://usra01\reta01...全部登录用户库
					String email=ConstantParamter.getEmailField().toLowerCase();
					String phone=ConstantParamter.getMobilePhoneField().toLowerCase();
					String loguser=ConstantParamter.getLoginUserNameField().toLowerCase();
					if(staff_id.length()==11){
						String dbpre=staff_id.substring(0,3);
						String a0100=staff_id.substring(3);
						RecordVo user_vo=new RecordVo(dbpre+"A01");
						user_vo.setString("a0100",a0100);
						if(dao.isExistRecordVo(user_vo))
						{
							user_vo=dao.findByPrimaryKey(user_vo);
							if(user_vo!=null)
							{
								String logon_user=user_vo.getString(loguser);
								/**账号为空不处理*/
								if(!(logon_user==null|| "".equals(logon_user)))
								{	
									vo=new LazyDynaBean();
									vo.set("username",logon_user);
									vo.set("a0100",dbpre+user_vo.getString("a0100"));
									vo.set("userfullname",user_vo.getString("a0101"));
									if(!"".equals(email)) {
                                        vo.set("email",user_vo.getString(email));
                                    }
									if(!"".equals(phone)) {
                                        vo.set("phone",user_vo.getString(phone));
                                    }
									vo.set("b0110",user_vo.getString("b0110"));
									vo.set("e0122",user_vo.getString("e0122"));
									vo.set("e01a1",user_vo.getString("e01a1"));
									vo.set("status",String.valueOf(status));
									
									if(a0107.length()>0){
											String value= user_vo.getString("a0107");
											String codeitemdesc = AdminCode.getCodeName(a0107, value);
											if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.man"))!=-1){
												vo.set("a0107", ResourceFactory.getProperty("warn.email.male"));
											}else if(codeitemdesc.indexOf(ResourceFactory.getProperty("warn.email.woman"))!=-1){
												vo.set("a0107", ResourceFactory.getProperty("warn.email.female"));
											}else{
												vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
											}
									
									}else{
										vo.set("a0107", ResourceFactory.getProperty("warn.email.female")+"/"+ResourceFactory.getProperty("warn.email.male"));
									}
									//if(!userhm.containsKey(key))
									userhm.put(key,vo);
								}
							}
						}
					}
					break;
				case 2:
					RecordVo org_vo=new RecordVo("organization");
					org_vo.setString("codeitemid",staff_id);
					String sql = "select codesetid from organization where codeitemid='"+staff_id+"'";
					RowSet rs = null;
					try {
					rs = dao.search(sql);
					if (rs.next()) {
						org_vo.setString("codesetid",rs.getString("codesetid"));
					}
					if(dao.isExistRecordVo(org_vo))
					{
						try {
						org_vo=dao.findByPrimaryKey(org_vo);
						findUserListByOrgId(org_vo,userhm,a0107);
						} catch (Exception e) {
						
						}
					}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (rs != null) {
							rs.close();
						}
					}
					
				}
			}//for while loop end.
			Iterator iterator=userhm.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				//String strkey=(String)entry.getKey();
				list.add(entry.getValue()/*iterator.next()*/);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
}
