package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>
 * Title:OperatePositionApplyTrans
 * </p>
 * <p>
 * Description:对职位申请进行操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-01-26
 * </p>
 * 
 * @author wangcq
 * @version 1.0
 */
public class OperatePositionApplyTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        Boolean result = Boolean.FALSE;
        ArrayList z03list = new ArrayList();
        String noPositionNames = "";
        int number = 0;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            String status = (String) this.getFormHM().get("status");
            String a0100Str = (String) this.getFormHM().get("a0100");
            String name = (String) this.getFormHM().get("name");
            String z0301 = (String) this.getFormHM().get("z0301");
            String flag = (String) this.getFormHM().get("flag");
            
            RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
        	String nbase="";  //应聘人员库
			if(vo!=null)
				nbase=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
            
			if("isjoin".equalsIgnoreCase(flag)){
				ResumeBo rebo = new ResumeBo(this.getFrameconn());
				String[] a0100s = a0100Str.split(",");
				
			    for(int i = 0; i < a0100s.length; i++) {
	                String a0100 = PubFunc.decrypt(a0100s[i]);
	                String node_flag = rebo.getNode_flag(a0100.trim(), "", nbase.trim());
	                if ("1".equals(node_flag)) {
	                	this.getFormHM().put("flag", node_flag);
	                    return;
	                }
			    }
			    return;
			}
			
			PositionBo positionBo=new PositionBo(this.getFrameconn(),dao,this.userView);
			ArrayList List = positionBo.applyPosition(status, a0100Str, name, z0301, nbase, "2");
			
			int[] count = {};
			count = (int[]) List.get(1);
			StringBuffer mailmsg = new StringBuffer();
			mailmsg=(StringBuffer) List.get(0);
			int filterNum = 0;
			filterNum= (Integer) List.get(2);
            this.formHM.put("z0301s", z0301);
         // mailmsg.append("true");
            if(this.formHM.get("noemail")!=null)
            	this.getFormHM().put("msg", 1);
            else
            	this.getFormHM().put("msg", SafeCode.encode(mailmsg.toString()));

            if(count.length > 0)
                result = Boolean.TRUE;
            else
                result = Boolean.FALSE;
            
            if(StringUtils.isNotEmpty(noPositionNames)) {
                
                StringBuffer msg = new StringBuffer();
                msg.append("由于");
                if(noPositionNames.endsWith(","))
                    noPositionNames = noPositionNames.substring(0, noPositionNames.length()-1);
                
                msg.append(noPositionNames);
                msg.append("等" + number);
                msg.append(" 人未申请职位，不能接受职位申请！");
                this.getFormHM().put("info", SafeCode.encode(msg.toString()));
            }
            
            if (filterNum > 0) {
                result = Boolean.FALSE;
                this.getFormHM().put("status", status);
                this.getFormHM().put("info", SafeCode.encode("已过滤处于已终止或已入职环节状态的简历！"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = Boolean.FALSE;
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            ResumeFilterBo rbo = new ResumeFilterBo(frameconn, userView);
            rbo.updateSuitable(z03list, null);
            this.getFormHM().put("result", result);
        }

    }

}
