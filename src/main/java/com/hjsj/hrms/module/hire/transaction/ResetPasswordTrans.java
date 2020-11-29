package com.hjsj.hrms.module.hire.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ResetPasswordTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		try
		{
			String info="ok";
			String pw0=((String)this.getFormHM().get("pw0")).trim();
			String pw1=((String)this.getFormHM().get("pw1")).trim();
			this.getFormHM().remove("pw0");  
			this.getFormHM().remove("pw1");  //20140812 基于安全考虑，避免返回信息中带有password信息
			pw0=PubFunc.hireKeyWord_filter(pw0);
			pw1=PubFunc.hireKeyWord_filter(pw1);//ajax 须按招招聘格式转码
			pw0=PubFunc.getReplaceStr(pw0);
			pw1=PubFunc.getReplaceStr(pw1);
			String email = (String)this.getFormHM().get("email");
			email = PubFunc.decrypt(email);
			String guidkey = (String)this.getFormHM().get("guidkey");
			String emailId=ConstantParamter.getEmailField().toLowerCase();//邮件指标
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
			String complexPassword="";
			String passwordMinLength="";
			String passwordMaxLength="";
			//ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn(),"1");
			ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=bo.getAttributeValues();
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
		                if ((by[0] >= 33 && by[0] <= 42)||by[0] == 64 ||by[0] == 94 ||by[0] == 126 ) {
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
			}else{
				if(pw1==null){
					   info="密码不能为空";
				}else if(pw1.length()<Integer.parseInt(passwordMinLength)||pw1.length()>Integer.parseInt(passwordMaxLength)){
					info="密码长度应为"+passwordMinLength+"-"+passwordMaxLength+"位!"; 
				}
			}
			if("ok".equalsIgnoreCase(info)){
//				if("1".equals((String)this.userView.getHm().get("isHeadhunter"))){
//					String updateSql = "update zp_headhunter_login set Password=? where Username=?";
//					ArrayList updateValues = new ArrayList();
//					updateValues.add(pw1);
//					updateValues.add(this.userView.getUserName());
//					dao.update(updateSql, updateValues);
//				}else{
				//口令加密解密处理
				RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.getFrameconn());
				if(encryVo!=null) {
					String encryPwd = encryVo.getString("str_value");
					if("1".equals(encryPwd)){//加密
						Des des=new Des();
						pw1=des.EncryPwdStr(pw1);
					}
				}
				StringBuffer sql =  new StringBuffer();
				sql.append("update "+dbname+"A01");
				sql.append(" set userPassword='"+pw1+"'");
				sql.append(" where "+emailId+"=?");
				sql.append(" and guidkey=?");
				
				ArrayList list = new ArrayList();
				list.add(email);
				list.add(guidkey);
				dao.update(sql.toString(), list);
				//更改完密码之后删除该链接验证
				StringBuffer delete_sql =  new StringBuffer();
				delete_sql.append("delete from t_sys_resetpassword");
				delete_sql.append(" where guidkey=?");
				list = new ArrayList();
				list.add(guidkey);
				dao.delete(delete_sql.toString(), list);
			}
				
			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
