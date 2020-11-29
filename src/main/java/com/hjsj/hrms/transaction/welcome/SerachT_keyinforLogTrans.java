package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerachT_keyinforLogTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		if(!dbWizard.isExistTable("t_keyinfor_log",false))
		{
			return;
		}
		String a_code=(String)this.getFormHM().get("a_code");
		String select_name=(String)this.getFormHM().get("select_name");	
		this.getFormHM().put("select_name", "");
		String userbase=(String)this.getFormHM().get("userbase");
		String content=(String)this.getFormHM().get("content");
		//处理特殊字符 begin jingq add 014.07.14
		content = content.replaceAll("\\\\", "\\\\\\\\");
		content = content.replaceAll("%", "\\\\%");
		content = content.replaceAll("_", "\\\\_");
		content = content.replaceAll("'", "''");
		if(Sql_switcher.searchDbServer()==1){
			content = content.replaceAll("\\[", "\\\\[");
		}
		content = PubFunc.keyWord_reback(content);
		//end
		if(a_code==null||a_code.length()<=0)
	    {
	    	   a_code="UN";
	    	   select_name = null;
	    }
		String kind="2";
		String code="";
		if(a_code!=null&&a_code.length()>0)
		{
			String codesetid=a_code.substring(0,2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				kind="2";
			}else if("UM".equalsIgnoreCase(codesetid))
			{
				kind="1";
			}else if("@K".equalsIgnoreCase(codesetid))
			{
				kind="0";
			}
			if(a_code.length()>=3)
			{
				code=a_code.substring(2);
			}else
			{
				code=this.userView.getManagePrivCodeValue();
			}
		}
		
		StringBuffer cond_str=new StringBuffer();	
		//修改SQL语句    支持查询特殊字符 jingq add 2014.07.14
		cond_str.append("from t_keyinfor_log where 1=1 and content like '"+content+"' escape '\\'");
		if(userbase!=null&&userbase.length()>0){
			cond_str.append(" and nbase = '"+userbase+"'");
			if(select_name!=null&&select_name.length()>0)
			{
				Pattern p = Pattern.compile("\\w+");
				 Matcher m = p.matcher(select_name);
				 if(m.matches()){
					 String sql = "select a0101 from "+userbase+"A01 where username='"+select_name+"'";
					 try {
						 ContentDAO dao = new ContentDAO(this.frameconn);
						 this.frecset =dao.search(sql);
						 while(this.frecset.next()){
							 select_name = this.frecset.getString("a0101");
						 }
					} catch (SQLException e) {
						e.printStackTrace();
					} 
				 }
			}
		}
		if(code!=null&&code.length()>0)
		{
			if("1".equals(kind))
			{
				cond_str.append(" and e0122 like '"+code+"%'");
			}else
			{
				cond_str.append(" and b0110 like '"+code+"%'");	
			}
		}else
		{
		    if(!this.userView.isSuper_admin())
			   cond_str.append(" and 1=2");
		}
			
		
		if(select_name!=null&&select_name.length()>0)
		{
			select_name=PubFunc.getStr(select_name);
			cond_str.append(" and a0101 like '%"+select_name+"%'");
		}		
		String cloumn="logid,nbase,a0100,b0110,e0122,a0101,address,content,access_time";
		if(dbWizard.isExistField("t_keyinfor_log", "opinion", false))
			cloumn += ",opinion";
		String sql="select "+cloumn;
		this.getFormHM().put("sql", sql);
		this.getFormHM().put("where", cond_str.toString());
		this.getFormHM().put("cloumn", cloumn);
		
	}

}
