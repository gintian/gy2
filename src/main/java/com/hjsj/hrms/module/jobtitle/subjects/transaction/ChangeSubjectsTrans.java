package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新增、保存（修改）、删除学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class ChangeSubjectsTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			String msg = "";
			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类
			
			String type = (String)this.getFormHM().get("type");//”1”/”2”/”3”(新增/修改/删除)
			
			if("1".equals(type)) {//新增
				String subjectsName = (String)this.getFormHM().get("subjectsName");//学科组名称
				String description = (String)this.getFormHM().get("description");//描述
				String b0110 = (String)this.getFormHM().get("b0110");//所属组织
				String create_fullname = (String)this.getFormHM().get("create_fullname");//创建人
				String create_time = (String)this.getFormHM().get("create_time");//创建日期
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("subjectsName", subjectsName);
				if(StringUtils.isNotBlank(description))
					bean.set("description", description);
				if(StringUtils.isNotBlank(b0110))
					bean.set("b0110", b0110);
				if(StringUtils.isNotBlank(create_fullname))
					bean.set("create_fullname", create_fullname);
				if(StringUtils.isNotBlank(create_time))
					bean.set("create_time", create_time);
				msg = subjectsBo.createSubjects(bean);
				rs = dao.search("select MAX(group_id) group_id from zc_subjectgroup");
				if(rs.next()){
					String group_id = rs.getString("group_id");
					group_id = PubFunc.encrypt(group_id);
					this.getFormHM().put("group_id", group_id);
				}
			} else if("2".equals(type)) {//修改
				String group_id = (String)this.getFormHM().get("group_id");//学科组编号
				group_id = PubFunc.decrypt(group_id);
				String subjectsName = (String)this.getFormHM().get("subjectsName");//学科组名称
				String description = (String)this.getFormHM().get("description");//描述
				String b0110 = (String)this.getFormHM().get("b0110");//所属组织
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				//將更新学科组所需要的key和value组装成list返回
				list = subjectsBo.getModifyList(subjectsName, description, b0110);
				//更新学科组
				msg = subjectsBo.modifySubjects(group_id, list);
				
			} else if("3".equals(type)) {//删除
				String group_id = (String)this.getFormHM().get("group_id");//学科组编号
				group_id = PubFunc.decrypt(group_id);
				msg = subjectsBo.deleteSubjects(group_id);
			}
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(rs!=null)
				PubFunc.closeResource(rs);
		}
	}

}
