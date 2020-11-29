package com.hjsj.hrms.module.jobtitle.subjects.transaction.subjectsformeeting;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsForMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 资格评审_学科组选择控件
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class SubjectsPickerTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			SubjectsForMeetingBo subjectsBo = new SubjectsForMeetingBo(this.getFrameconn(), this.userView);// 工具类
			String w0301 = (String)this.getFormHM().get("w0301");
			if(StringUtils.isNotEmpty(w0301))
				w0301 = PubFunc.decrypt(w0301);
			
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = subjectsBo.getColumnListSubjectPicker();
			
			/** 获取查询语句 */
			String sql = subjectsBo.getSqlSubjectPicker();
			
			sql+=subjectsBo.getGroupIds(w0301);//排除已选中的学科组
			
			TableConfigBuilder builder = new TableConfigBuilder( "subjects_picker_00001", columnList, "subjects_picker", userView, this.getFrameconn());
			builder.setDataSql(sql);
			builder.setOrderBy("order by group_id");
			builder.setAutoRender(false);
			builder.setSetScheme(false);
			builder.setLockable(false);
//			builder.setTableTools( new ArrayList());
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setPageSize(8);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
