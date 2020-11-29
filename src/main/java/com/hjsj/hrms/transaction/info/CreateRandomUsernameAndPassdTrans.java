package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.GuidCreator;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CreateRandomUsernameAndPassdTrans extends IBusiness {

	public void execute() throws GeneralException {
		String action=(String)this.getFormHM().get("action");
		action=action!=null&&action.length()>0?action:"";
		
		try {
			if("create".equals(action))
			{
				//生成随机数
				String sql = "";
				if(this.userView.getHm().containsKey("staff_sql"))
					sql = this.userView.getHm().get("staff_sql").toString();
				else{
						throw new Exception("Please check <userView.getHM()> key:staff_sql");
				}
				sql=PubFunc.reBackWord(PubFunc.keyWord_reback(sql));
				String usernameFld=(String)this.getFormHM().get("usernameFld");
				String passwordFld=(String)this.getFormHM().get("passwordFld");
				String nbase=(String)this.getFormHM().get("nbase");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				ArrayList a0100list=getA0100List(sql,dao);
				if(a0100list==null||a0100list.size()<=0)
				{
					this.getFormHM().put("info", "提示：没有人员需要生成随机账号和口令！");
					return;
				}
				int count=a0100list.size();
				HashMap map=getUsernamePassd(count);
				boolean isCorrect=createUsernamePassd(nbase,usernameFld,passwordFld,map ,a0100list,dao);
				if(isCorrect)
				{
					this.getFormHM().put("info", "随机账号和口令已生成！");
				}else
					this.getFormHM().put("info", "错误：生成随机账号及口令时发生错误！");
				map.clear();
				return;
			}else
			{
				String usernameFld = "";
				String passwordFld = "";
				RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
				if (login_vo != null)
				{
					String login_name = login_vo.getString("str_value");
					int idx = login_name.indexOf(",");
					if (idx != -1)
					{
						usernameFld = login_name.substring(0, idx);
						passwordFld = login_name.substring(idx+1);
					}
				}
				String usernameDesc="";
				String passwordDesc="";
				if (usernameFld==null || "#".equals(usernameFld) || usernameFld.length()<1)
				{
					usernameFld = "username";
					usernameDesc="认证用户名";
				}else
				{
					FieldItem fielditem=DataDictionary.getFieldItem(usernameFld);
					usernameDesc=fielditem.getItemdesc();
				}  	
				if(passwordFld==null || "#".equals(passwordFld) || passwordFld.length()<1)
				{
					passwordFld="userpassword";
					passwordDesc="认证密码";
				}else
				{
					FieldItem fielditem=DataDictionary.getFieldItem(passwordFld);
					passwordDesc=fielditem.getItemdesc();
				}
				StringBuffer str=new StringBuffer();
				str.append("本操作将清空并随机生成"+usernameDesc+"和"+passwordDesc+"的内容");
				str.append("，不可恢复！您确认继续吗？");
				this.getFormHM().put("info", str.toString());
				this.getFormHM().put("usernameFld", usernameFld);
				this.getFormHM().put("passwordFld", passwordFld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private ArrayList getA0100List(String sql,ContentDAO dao)
    {
    	sql="select a0100 "+sql;
        ArrayList list=new ArrayList();
    	try {
			this.frowset=dao.search(sql);
			
	    	while(this.frowset.next())
	    	{
	    		list.add(this.frowset.getString("a0100"));
	    	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	return list;
    }
	private HashMap getUsernamePassd(int count)
	{
		HashMap map=new HashMap();
		GuidCreator guidCreator=new GuidCreator(false,8);
		String key="";
		String value="";
		for(int i=0;i<count;i++)
		{
			do
			{
				key=guidCreator.createRandomGuid();
			}while(map.containsKey(key));
			value=guidCreator.createRandomGuid();
			map.put(key, value);
		}		
		return map;
	}
	private boolean createUsernamePassd(String nbase,String usernameFld,String passdFld,HashMap map ,ArrayList a0100List,ContentDAO dao)
	{
		boolean isCorrect=false;
		StringBuffer update=new StringBuffer();
		update.append("update "+nbase+"a01 set ");
		update.append(" "+usernameFld+"=?,"+passdFld+"=?");
		update.append(" where a0100=?");
		Iterator iter=map.entrySet().iterator();
		ArrayList uplist=new ArrayList();
		int i=0;
		String a0100="";
		while(iter.hasNext())
		{
			Map.Entry entry=(Map.Entry )iter.next();
			a0100=(String)a0100List.get(i);
			ArrayList oneL=new ArrayList();			
			oneL.add(entry.getKey());
			oneL.add(entry.getValue());
			oneL.add(a0100);
			//System.out.println(oneL);
			uplist.add(oneL);
			i++;
		}
		try {
			dao.batchUpdate(update.toString(), uplist);
			isCorrect=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isCorrect;
	}
}
