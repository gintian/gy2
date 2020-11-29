package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.InnerHireBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
public class ApplyPosTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag="1";
			
			
			String clientName=SystemConfig.getPropertyValue("clientName");//得到一个专有的客户名称
			String alertMessage="";
			String a0100="";//应聘人员在应聘人才库中的a0100
            EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
            
			if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)){//专门为汉口银行做专版
			    /**得到系统的唯一性指标**/
	            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
	            String onlyFlag = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
	            if(onlyFlag==null||onlyFlag.trim().length()<=0){
	                throw GeneralExceptionHandler.Handle(new Exception("请配置系统的唯一性指标!"));
	            }
	            InnerHireBo bo=new InnerHireBo(this.getFrameconn());
	            String onlyName = bo.getEmailAddress(onlyFlag,this.getUserView());//这里得到的是人员的唯一性标识
	            
	            if(onlyName==null||onlyName.length()==0)
                    throw GeneralExceptionHandler.Handle(new Exception("您没有设置您的唯一性标识指标，不能申请岗位!"));
	            
	            if(bo.getSameEmailCount(onlyFlag,onlyName,this.userView)>1)
                    throw GeneralExceptionHandler.Handle(new Exception("您的唯一性指标与库中其他人的唯一性指标相同，不能申请岗位,请修改!"));
	            
	            a0100=bo.getZpkA0100(onlyName,onlyFlag);
	            ArrayList returnList=this.inforDeal(a0100, bo, flag, employNetPortalBo, alertMessage, onlyFlag, onlyName);
	            flag = (String) returnList.get(0);
	            alertMessage = (String) returnList.get(1);
	            a0100 =(String) returnList.get(2);
	            this.getFormHM().put("zpkA0100",a0100);
	            ArrayList applyedPosList=employNetPortalBo.getApplyedPosList(a0100.trim());
                this.getFormHM().put("applyedPosList",applyedPosList);
                this.getFormHM().put("flag",flag);   //1:申请职位成功  2：已申请过该职位  3：已超过申请职位数 4：申请失败
                this.getFormHM().put("alertMessage", SafeCode.encode(alertMessage));
			}else{
			    RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
	            if(vo!=null)
	            {
	                if("#".equals(vo.getString("str_value")))
	                    throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标!"));
	                InnerHireBo bo=new InnerHireBo(this.getFrameconn());
	                String email=bo.getEmailAddress(vo.getString("str_value"),this.getUserView());
	                if(email==null||email.length()==0)
	                    throw GeneralExceptionHandler.Handle(new Exception("您没有设置电子邮件地址，不能申请岗位!"));
	                if(bo.getSameEmailCount(vo.getString("str_value"),email,this.userView)>1)
	                    throw GeneralExceptionHandler.Handle(new Exception("您的电子邮件与库中其他人的邮件地址相同，不能申请岗位,请修改!"));
	                
	                a0100=bo.getZpkA0100(email,vo.getString("str_value"));
	                ArrayList returnList=this.inforDeal(a0100, bo, flag, employNetPortalBo, alertMessage, vo.getString("str_value"), email);//zxj 后两个参数传顺序错了
	                flag = (String) returnList.get(0);
	                alertMessage = (String) returnList.get(1);
	                a0100 =(String) returnList.get(2);
	                this.getFormHM().put("zpkA0100",a0100);
	                ArrayList applyedPosList=employNetPortalBo.getApplyedPosList(a0100.trim());
	                this.getFormHM().put("applyedPosList",applyedPosList);
	            }
	            else
	                throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标!"));
			}
			this.getFormHM().put("flag",flag);   //1:申请职位成功  2：已申请过该职位  3：已超过申请职位数 4：申请失败
			this.getFormHM().put("alertMessage", SafeCode.encode(alertMessage));
		}
		catch(Exception ex)
		{
			this.getFormHM().put("flag","4");
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**处理信息,返还给前台
	 * @throws GeneralException **/
    public ArrayList inforDeal(String a0100,InnerHireBo bo,String flag,EmployNetPortalBo employNetPortalBo,String alertMessage,String filed,String fieldValue) throws GeneralException {
        ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
        HashMap map = bo2.getAttributeValues();
        String posID = (String) this.getFormHM().get("posID");
        RecordVo vo2 = ConstantParamter.getConstantVo("ZP_DBNAME");
        String dbname = vo2.getString("str_value");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList retrunList = new ArrayList();
        try {
            if (a0100.length() > 0) {
                if (bo.isApplyed(a0100, posID)) // 已申请过该职位
                {
                    flag = "2";
                } else {
                    int size = bo.getApplyPosCount(a0100); // 取得已申请职位数

                    int maxSize = 3;
                    if (map != null && map.get("max_count") != null && ((String) map.get("max_count")).trim().length() > 0)
                        maxSize = Integer.parseInt((String) map.get("max_count"));
                    if (size >= maxSize)
                        flag = "3";
                    else {

                        RecordVo vo1 = new RecordVo("zp_pos_tache");
                        vo1.setString("zp_pos_id", posID);
                        vo1.setString("a0100", a0100);
                        vo1.setInt("thenumber", size + 1);
                        vo1.setDate("apply_date", Calendar.getInstance().getTime());
                        vo1.setString("status", "0");
                        vo1.setString("resume_flag", "10");
                        vo1.setString("nbase", this.getUserView().getDbname());
                        dao.addValueObject(vo1);
                        employNetPortalBo.addStatInfo(2, posID);
                    }
                }
            } else {
                StringBuffer noDataSet = new StringBuffer("");
                String isUpPhoto = "0"; // 是否必须上传照片
                if (map.get("photo") != null && ((String) map.get("photo")).length() > 0)
                    isUpPhoto = (String) map.get("photo");
                if (noDataSet.length() > 0) {
                    noDataSet.append("以上子集中，括号内的指标为必填项！");

                }
                if ("1".equals(isUpPhoto)) {
                    this.frowset = dao.search("select a0100 from " + this.userView.getDbname() + "a00 where a0100='" + this.userView.getA0100() + "' and flag='P'");
                    if (this.frowset.next()) {

                    } else {
                        if (noDataSet.length() > 0) {
                            noDataSet.append("\r\n个人照片必须上传！");

                        } else {
                            noDataSet.append("个人照片必须上传！");
                        }
                    }
                }
                if (noDataSet.toString().length() > 0) {
                    flag = "5";
                    alertMessage = noDataSet.append("\r\n请先维护好以上信息，再应聘岗位,否则无法应聘！").toString();
                } else {
                    String hire_object = "";
                    boolean aflag = false;
                    EmployNetPortalBo enpb = new EmployNetPortalBo(this.getFrameconn(), "0");
                    if (map != null && map.get("hire_object") != null && !"".equals((String) map.get("hire_object"))) {
                        hire_object = (String) map.get("hire_object");
                        aflag = enpb.getZ0336(posID, hire_object);
                    }
                    a0100 = bo.copyInfoToZpInner(this.getUserView().getA0100(), this.getUserView().getDbname(), dbname,fieldValue, filed, aflag);
                    RecordVo vo1 = new RecordVo("zp_pos_tache");
                    vo1.setString("zp_pos_id", posID);
                    vo1.setString("a0100", a0100);
                    vo1.setInt("thenumber", 1);
                    vo1.setDate("apply_date", Calendar.getInstance().getTime());
                    vo1.setString("status", "0");
                    vo1.setString("resume_flag", "10");
                    vo1.setString("nbase", this.getUserView().getDbname());
                    dao.addValueObject(vo1);
                    employNetPortalBo.addStatInfo(2, posID);
                }
            }
            retrunList.add(flag);
            retrunList.add(alertMessage);
            retrunList.add(a0100);
            return retrunList;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
