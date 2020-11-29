package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class EditPasswordTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String info="密码修改成功";
			String pw0=((String)this.getFormHM().get("pw0")).trim();
			String pw1=((String)this.getFormHM().get("pw1")).trim();
			this.getFormHM().remove("pw0");  //20140812 基于安全考虑，避免返回信息中带有password信息
			this.getFormHM().remove("pw1");  //20140812 基于安全考虑，避免返回信息中带有password信息
			pw0=PubFunc.hireKeyWord_filter(pw0);
			pw1=PubFunc.hireKeyWord_filter(pw1);//ajax 须按招招聘格式转码
			String dbname=(String)this.getFormHM().get("dbname");
			String a0100=(String)this.getFormHM().get("a0100");
			pw0=PubFunc.getReplaceStr(pw0);
			pw1=PubFunc.getReplaceStr(pw1);
			dbname=PubFunc.getReplaceStr(dbname);
			a0100=PubFunc.getReplaceStr(a0100);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList values = new ArrayList();
			//口令加密解密处理
			RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.getFrameconn());
			if(encryVo!=null) {
				String encryPwd = encryVo.getString("str_value");
				if("1".equals(encryPwd)){//加密
					Des des=new Des();
					pw0=des.EncryPwdStr(pw0);
				}
			}
			String querysql = "select * from "+dbname+"A01 where a0100=? and userPassword=?";
			if("1".equals((String)this.userView.getHm().get("isHeadhunter"))){
				querysql="select * from zp_headhunter_login where Username=? and Password=?";
				values.add(this.userView.getUserName());
				values.add(pw0);
			}else{
				values.add(a0100);
				values.add(pw0);
			}
			this.frowset=dao.search(querysql,values);
			String complexPassword="";
			String passwordMinLength="";
			String passwordMaxLength="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			if(map.get("complexPassword")!=null&&((String)map.get("complexPassword")).length()>0)
				complexPassword=(String)map.get("complexPassword");
			if(map.get("passwordMinLength")!=null&&((String)map.get("passwordMinLength")).length()>0)
				passwordMinLength=(String)map.get("passwordMinLength");
			if(map.get("passwordMaxLength")!=null&&((String)map.get("passwordMaxLength")).length()>0)
				passwordMaxLength=(String)map.get("passwordMaxLength");
			
			/**验证密码是否符合规则**/
			if("1".equals(complexPassword)){
				if(pw1==null){
				   info="密码不能为空";
				}else if(pw1.length()<Integer.parseInt(passwordMinLength)||pw1.length()>Integer.parseInt(passwordMaxLength)){
					info="密码长度应为"+passwordMinLength+"-"+passwordMaxLength+"位!"; 
				}else{
					int numasc = 0;
					int charasc = 0;
					int otherasc = 0;
					String pw2=PubFunc.hireKeyWord_filter_reback(pw1);
					for(int i=0;i<pw2.length();i++){
						byte[] by=pw2.substring(i,i+1).getBytes();
						if(by[0]>= 48 && by[0]<= 57){
							numasc+=1;
						}
		                if ((by[0] >= 65 && by[0] <= 90)||(by[0] >= 97 && by[0] <= 122)) {
		                    charasc += 1;
		                } 
		                if ((by[0] >= 34 && by[0] <= 42)||by[0] == 64 ||by[0] == 94 ||by[0] == 126 ) {
		                    otherasc += 1;
		                }
		            }
		            if(numasc==0)  {
		            	info="密码必须含有数字";
		            }else if(charasc==0){
		            	info="密码必须含有字母";
		            }else if(otherasc==0){
		            	info="密码必须含有特殊字符(%$#@!~^&*()')";
		            }
				}
			}
			if("密码修改成功".equals(info)){
				if(this.frowset.next())
				{
					if(encryVo!=null) {
						String encryPwd = encryVo.getString("str_value");
						if("1".equals(encryPwd)){//加密
							Des des=new Des();
							pw1=des.EncryPwdStr(pw1);
						}
					}
					if("1".equals((String)this.userView.getHm().get("isHeadhunter"))){
						String updateSql = "update zp_headhunter_login set Password=? where Username=?";
						ArrayList updateValues = new ArrayList();
						updateValues.add(pw1);
						updateValues.add(this.userView.getUserName());
						dao.update(updateSql, updateValues);
					}else{
						dao.update("update "+dbname+"A01 set userPassword='"+pw1+"' where a0100='"+a0100+"'");	
					}
					
				}
				else
					info="旧密码填写错误";
				
			}

			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
