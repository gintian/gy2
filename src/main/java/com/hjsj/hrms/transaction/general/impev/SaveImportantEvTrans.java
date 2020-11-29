/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:SaveImportantEvTrans
 * </p>
 * <p>
 * Description:保存添加重要信息报告
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class SaveImportantEvTrans extends IBusiness {

	/**
	 * 
	 */
	public SaveImportantEvTrans() {
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String p0600 = (String)hm.get("p0600");
		p0600 = p0600!=null&&p0600.trim().length()>0?p0600:"";
		hm.remove("p0600");
		String p0609=(String)hm.get("p0609");
		p0609 = p0609!=null&&p0609.trim().length()>0?p0609:"";
		hm.remove("p0609");
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("P06");
			//人员编号
			vo.setString("a0100", this.userView.getUserId());
			//姓名
			vo.setString("a0101", this.userView.getUserFullName());
			//单位
			vo.setString("b0110", this.userView.getUserOrgId());
			//部门
			vo.setString("e0122", this.userView.getUserDeptId());
			//职位
			vo.setString("e01a1", this.userView.getUserPosId());
			//人员库
			vo.setString("nbase", this.userView.getDbname());
			if("".equals(p0600)){
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String str = idg.getId("P06.P0600");
				vo.setString("p0600", str);
			}else{
				vo.setString("p0600", p0600);
			}
			vo.setDate("p0603", (String)this.getFormHM().get("fromdate"));
			this.getFormHM().remove("fromdate");
			vo.setDate("p0605", (String)this.getFormHM().get("todate"));
			this.getFormHM().remove("todate");
			/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
			String content = (String)this.getFormHM().get("content");
			content = PubFunc.stripScriptXss(content);
			vo.setString("p0607",content);
			List fieldlist = (List)this.getFormHM().get("fieldlist");
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fieldItem = (FieldItem)fieldlist.get(i);
				vo.setString(fieldItem.getItemid(), fieldItem.getValue());
			}
			if("".equals(p0609)){
				vo.setString("p0609","2");//未提交
			}else{
				vo.setString("p0609","1");//提交
			}
			if("".equals(p0600)){
				dao.addValueObject(vo);
			}else{
				dao.updateValueObject(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}

}
