package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：CheckFormulaTrans 
 * 类描述：计算公式校验
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 3:29:21 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 3:29:21 PM
 * 修改备注： 
 * @version
 */
public class CheckFormulaTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		try {
			String c_expr = (String) this.getFormHM().get("c_expr"); //计算公式内容
			c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
			
			String itemid = (String) this.getFormHM().get("itemid"); //子标代码
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
			
			String module = (String)this.getFormHM().get("module");
			module=module!=null&&module.length()>0?module:"";
			
			String id = (String) this.getFormHM().get("id"); //薪资类别id，人事异动模版id
			id=id!=null&&id.trim().length()>0?id:"";		
			if("3".equalsIgnoreCase(module)){
				//itemid=itemid.substring(2);
			}
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			String type="";
			if(fielditem!=null){
				type = fielditem.getItemtype();
			}else if(!"".equals(id)){
				if("A00Z0".equalsIgnoreCase(itemid)||"A00Z2".equalsIgnoreCase(itemid))
					type="D";
				else if("A00Z1".equalsIgnoreCase(itemid)||"A00Z3".equalsIgnoreCase(itemid))
					type="N";
				
			}
			type=type!=null&&type.trim().length()>0?type:"L";
			c_expr=SafeCode.decode(c_expr);
			c_expr=PubFunc.keyWord_reback(c_expr);

			String flag = "";
			
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			ArrayList fieldlist = null;
			if("1".equals(module)){ //薪资发放模块
				id = PubFunc.decrypt(SafeCode.decode(id));
				//从临时变量中取得对应指标列表
				fieldlist = bo.getGzMidVariableList(id);
			}else if("3".equals(module)){//人事异动
				itemid=itemid.replace("START*DATE", "START_DATE");
				itemid=itemid.replace("start*date", "start_date");
				if ("start_date".equalsIgnoreCase(itemid)){
					type="D";
				}
				if ("corcode".equalsIgnoreCase(itemid)){
					type="A";
				}
				String stritem ="";
				ArrayList alUsedFields = new ArrayList();
				
				TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
				ArrayList cellList = utilBo.getPageCell(Integer.parseInt(id), -1);

				for (int i = 0; i < cellList.size(); i++) {
					TemplateSet setBo = (TemplateSet) cellList.get(i);
					boolean isSubflag = setBo.isSubflag();
					if (isSubflag) {
						SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
						ArrayList sublist = subDomain.getSubFieldList();
						for(int j = 0; j < sublist.size(); j++) {
							SubField subField = (SubField)sublist.get(j);
							FieldItem fielditems = subField.getFieldItem();
							if(fielditems == null || stritem.indexOf(fielditems.getItemid())!=-1)
								continue;
							FieldItem fielditems_ = (FieldItem) fielditems.cloneItem();
							stritem+=fielditems_.getItemid()+",";
							//如果子集指标在后台模板中改了指标名称，仅传fielditem会导致保存不了
							fielditems_.setItemdesc(subField.getTitle());
							alUsedFields.add(fielditems_);
						}
					}
				}
				TemplateBo templatebo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(id));
				TempvarBo tempvarbo = new TempvarBo();
				ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,id);
				for(int i=0;i<templist.size();i++){
					FieldItem fielditems = (FieldItem)templist.get(i);
					if(stritem.indexOf(fielditems.getItemid())!=-1)
						continue;
					stritem+=fielditems.getItemid()+",";
					alUsedFields.add(fielditems);
				}
				ArrayList itemlist = templatebo.getAllFieldItem();
				HashMap<String, String> map_fieldSet = utilBo.getFieldSetMap(Integer.parseInt(id));
				for(int i=0;i<itemlist.size();i++){
					FieldItem field = (FieldItem)itemlist.get(i);
					String final_name = "";
					if(field==null)
						continue;
					
					String itemdesc = field.getItemdesc();
					//对于子集名称在cs端修改过的，需要用修改的
					if(field.getVarible()==2) {
						String hz = map_fieldSet.get(field.getItemid().toLowerCase());
						if(!hz.equalsIgnoreCase(itemdesc)) {
							final_name = hz;
						}
					}
					if(StringUtils.isBlank(final_name) && field.isChangeAfter()&&(!field.isMemo()||field.getVarible()==2)){
						final_name = ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc();
					}
					else if(StringUtils.isBlank(final_name) && field.isChangeBefore()&&(!field.isMemo()||field.getVarible()==2)){
						if(!field.isMainSet()){
							final_name = ResourceFactory.getProperty("inform.muster.now")+field.getItemdesc();
						}
					}
					
					if(StringUtils.isNotBlank(final_name)) {
						field.setItemdesc(final_name);
					}
					alUsedFields.add(field);
					fieldlist=alUsedFields;
				}
			} else if("4".equalsIgnoreCase(module)) {
				//考勤 假期管理校验公式
			    if (c_expr != null && c_expr.length() > 0) {
			        type = DataDictionary.getFieldItem(itemid).getItemtype();  
			        fieldlist = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		        }
			    // 考勤假期计算公式可以使用复杂函数
			    bo.setModeFlag(IParserConstant.forSearch);
			}
			//校验公式是否正确
			flag = bo.checkFormula(c_expr, type,fieldlist);
			this.getFormHM().put("info",flag);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
