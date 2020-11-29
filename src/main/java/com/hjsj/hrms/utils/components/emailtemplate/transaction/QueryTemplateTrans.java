package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.actionform.sys.options.template.TemplateSetForm;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.HireTemplateBo;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:QueryTemplateTrans</p>
 * <p>Description:模板列表展示</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:05:25 PM</p>
 * @author sunming
 * @version 1.0
 */
public class QueryTemplateTrans extends IBusiness {
	public QueryTemplateTrans() {
		super();
	}

	public void execute() throws GeneralException {
		try{
			Object obj = this.getFormHM().get("check");
			if(obj!=null){
				Integer templateid= (Integer) this.getFormHM().get("templateid");
				int valid = (Boolean)obj?1:0;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "update email_name set valid=? where id =?";
				ArrayList values = new ArrayList();
				values.add(valid);
				values.add(templateid);
				dao.update(sql,values);
				return;
			}
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String id = (String) this.getFormHM().get("id");
			ArrayList text = (ArrayList) this.getFormHM().get("inputValues");
			String opt = "";
			String isShowModuleType = "";//显示模板类型根据参数控制1:不显示，0:显示
			String isShowInsertFormula = "";//判断新增是否显示插入公式，1：不显示，其余显示
			String isShowModifyFormula = "";//判断新增是否显示修改公式，1：不显示，其余显示
			String isShowItem = "";//判断新增是否显示插入指标，1：不显示，其余显示
			String isShowAttachId = "";//判断新增是否显示插入附件，1：不显示，其余显示	//通过链接返回加特殊判断
			if(hm == null || (hm != null && "ret".equals(hm.get("b_query")))) { //判断是什么模块进入的，暂时9：绩效,7:招聘，如果没填认为招聘  //由于在保存之后再进这个方法hm为null，这里加判断
				opt = (String) this.getFormHM().get("opt");
				isShowModuleType = (String) this.getFormHM().get("isShowModuleType");
				isShowInsertFormula = (String) this.getFormHM().get("isShowInsertFormula");
				isShowModifyFormula = (String) this.getFormHM().get("isShowModifyFormula");
				isShowItem = (String) this.getFormHM().get("isShowItem");
				isShowAttachId = (String) this.getFormHM().get("isShowAttachId");
			}else {
				opt = (String) hm.get("opt");
				isShowModuleType = (String) hm.get("isShowModuleType");
				isShowInsertFormula = (String) hm.get("isShowInsertFormula");
				isShowModifyFormula = (String) hm.get("isShowModifyFormula");
				isShowItem = (String) hm.get("isShowItem");
				isShowAttachId = (String) hm.get("isShowAttachId");
				hm.remove("opt");//这里不删除hm的内容，链接也不写参数，其他模块进来会显示上个模块的值，导致错误
				hm.remove("isShowModuleType");
				hm.remove("isShowInsertFormula");
				hm.remove("isShowModifyFormula");
				hm.remove("isShowItem");
				hm.remove("isShowAttachId");
			}
			MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
			//说明是快速查询
			if(bean != null) {
				opt = (String) bean.get("opt");
				isShowModuleType = (String) bean.get("isShowModuleType");
				isShowInsertFormula = (String) bean.get("isShowInsertFormula");
				isShowModifyFormula = (String) bean.get("isShowModifyFormula");
				isShowItem = (String) bean.get("isShowItem");
				isShowAttachId = (String) bean.get("isShowAttachId");
			}
			if(StringUtils.isBlank(opt))//招聘可能传过来为空，这里加判断，其他模块进来需写opt
				opt = "7";
			if(StringUtils.isBlank(isShowModuleType))
				isShowModuleType = "0";
			if(StringUtils.isBlank(isShowInsertFormula))
				isShowInsertFormula = "0";
			if(StringUtils.isBlank(isShowModifyFormula))
				isShowModifyFormula = "0";
			if(StringUtils.isBlank(isShowItem))
				isShowItem = "0";
			if(StringUtils.isBlank(isShowAttachId))
				isShowAttachId = "0";
			
			StringBuffer str_sql = new StringBuffer();
			boolean isModule = false;
			String a0100 = this.userView.getA0100();
			String pre = this.userView.getDbname();
			String unitB0110 = "";// 所属机构
			String temp = this.userView.getUnit_id();
			if (temp != null && temp.trim().length() > 3) {
				unitB0110 = temp.substring(2);//su
			} else {
				if (pre != null && pre.trim().length() > 0)
					unitB0110 = this.getUserUnitId(a0100, pre);
			}
			HireTemplateBo bo = new HireTemplateBo(this.getFrameconn());
			String b0110 = bo.getB0110(this.userView,opt);
			String b0110Email = "";//管理范围
			if ("HJSJ".equals(b0110)) {
				b0110Email = "HJSJ";
			} else {
				b0110Email = this.getB0110(b0110);
			}
			// 取得模板列表的sql语句
			
			TemplateBo tempBo = new TemplateBo(this.frameconn, new ContentDAO(
					this.frameconn), this.getUserView());
			ArrayList buttList = new ArrayList();
			buttList = tempBo.getButtonList(isModule);
			ArrayList columns = new ArrayList();
			columns = tempBo.getColumnList(isModule,isShowModuleType);
			str_sql.append(tempBo.getQueryTemplateSql(text, unitB0110, b0110Email, opt));
			
			// 表格table的唯一标识 通过这个可以获得存放在userviw里面的sql，list等等
			String tablekey = (String) this.getFormHM().get("subModuleId"); // 前台表格的唯一标识
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);// 前台表格的相关数据集合
			
			this.getFormHM().put("buttonList", buttList);
			this.getFormHM().put("constantxml", "recruitment/template");
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("str_sql", str_sql.toString());
			this.getFormHM().put("orderbystr", "order by id");
			this.getFormHM().put("opt", opt);
			this.getFormHM().put("isShowItem", isShowItem);
			this.getFormHM().put("isShowInsertFormula", isShowInsertFormula);
			this.getFormHM().put("isShowModifyFormula", isShowModifyFormula);
			this.getFormHM().put("isShowModuleType", isShowModuleType);
			this.getFormHM().put("isShowAttachId", isShowAttachId);
			TemplateSetForm setForm = new TemplateSetForm();
			setForm.setId(id);
			if (tableCache != null) {
				tableCache.setTableSql(str_sql.toString());
				this.userView.getHm().put(tablekey, tableCache);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public String getB0110(String b0110){
		
		 String[] s = b0110.split("`");
		 String b0110Email = s[0].substring(2);
		 return b0110Email;
	}
	/**
	 * 取得登录用户的单位编码
	 * @param a0100
	 * @param pre
	 * @return
	 */
	public String getUserUnitId(String a0100,String pre)
	{
		String b0110="";
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append(" select b0110 from ");
			buf.append(pre+"a01 where a0100='");
			buf.append(a0100+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return b0110;
	}
	
}
