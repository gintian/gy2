package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ChooseStaffBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>Title:ChangeStaffDataTrans </p>
 * <p>Description: 操作参会人员类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ChangeStaffDataTrans extends IBusiness {

	@Override
    @SuppressWarnings({ "unchecked", "static-access" })
	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			ChooseStaffBo bo = new ChooseStaffBo(this.frameconn,this.userView);
			String msg = "";//结果信息
			String type = (String)this.getFormHM().get("type");//=1新增 =2删除 =3随机生成账号密码 =4修改
			
			String idlist  = (String)this.getFormHM().get("w0101List");
			String [] w0101 = null;
			if(StringUtils.isNotEmpty(idlist)){
				idlist = idlist.substring(1,idlist.length()-1);
				idlist = idlist.replaceAll("\"", "");
				w0101 = idlist.split(",");
			}
			String w0301 = (String)this.getFormHM().get("w0301");//会议编号
			if(StringUtils.isNotEmpty(w0301))
				w0301 = PubFunc.decrypt(w0301);
			if("1".equals(type)) {//新增
				int typeCommittee = (Integer)this.getFormHM().get("typeCommittee");//=1评委会 =4二级单位
				ArrayList<String> personidList = new ArrayList<String>();
				personidList = (ArrayList<String>)this.getFormHM().get("personidList");//人员编号list
				bo.addChoosePerson(w0301, personidList,typeCommittee);//新增操作
			}else if("2".equals(type)&&w0101!=null) {//删除
				int typeCommittee = (Integer)this.getFormHM().get("typeCommittee");//=1评委会 =4二级单位
				bo.deleteChoosePerson(w0101, w0301,typeCommittee);//删除操作
			}else if ("3".equals(type)&&w0101!=null) {//随机生成账号密码
				int typeCommittee = (Integer)this.getFormHM().get("typeCommittee");//=1评委会 =4二级单位
				GenerateAcPwBo gbo = new GenerateAcPwBo(this.frameconn);
				ArrayList list = GenerateAcPwBo.generate(w0101.length, dao);
				bo.randomCreate(list, w0101, w0301,typeCommittee);//为选中人员随机生成账号密码
			}else if ("4".equals(type)) {//修改
				ArrayList updatelist=(ArrayList)this.getFormHM().get("updaterecord"); //修改的数据
				msg = bo.updateChoosePerson(updatelist);
			}
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
