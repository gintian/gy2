package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>SetTrainAttendanceTrans.java</p>
 * <p>Description:培训考勤xml解析</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-03-03 上午09:07:55</p>
 * @author liweichao
 * @version 5.0
 */
public class SetTrainAttendanceTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO 培训考勤参数取值
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String card_no = constantbo.getTextValue("/param/attendance/card_no");
		String leave_early = constantbo.getTextValue("/param/attendance/leave_early");
		String late_for = constantbo.getTextValue("/param/attendance/late_for");
		
		ArrayList attendancelist = new ArrayList();
		CommonData dataobj = new CommonData("",ResourceFactory.getProperty("label.select.dot"));
		attendancelist.add(dataobj);
		ArrayList list=DataDictionary.getFieldList("A01",Constant.EMPLOY_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem item=(FieldItem)list.get(i);
			if((item.getCodesetid()==null||"".equals(item.getCodesetid())||"0".equals(item.getCodesetid()))
					&&"A".equalsIgnoreCase(item.getItemtype())&&!"a0101".equalsIgnoreCase(item.getItemid())){
				dataobj = new CommonData(item.getItemid().toUpperCase(),item.getItemdesc());
				attendancelist.add(dataobj);
			}
		}
		
		this.getFormHM().put("attendancelist",attendancelist);
		this.getFormHM().put("card_no", card_no);
		this.getFormHM().put("leave_early", leave_early);
		this.getFormHM().put("late_for", late_for);
	}
}
