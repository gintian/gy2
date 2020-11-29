package com.hjsj.hrms.utils.components.functionWizard.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.functionWizard.businessobject.FunctionWizardbo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：FunctionWizardTrans 
 * 类描述： 函数向导控件下步具体逻辑获取
 * 创建人：zhaoxg
 * 创建时间：Nov 5, 2015 2:50:01 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 5, 2015 2:50:01 PM
 * 修改备注： 
 * @version
 */
public class FunctionWizardTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String opt = (String) this.getFormHM().get("opt");//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
		String keyid = (String) this.getFormHM().get("keyid");//薪资模块为薪资类别，人事异动模块为tabid
		//招聘中此参数传的为空，这里做处理，处理为“1”
		keyid=keyid!=null&&keyid.trim().length()>0?keyid:"1";
		String flag = (String) this.getFormHM().get("flag");//0：表示下一步后第一层子集 1：后面个性的子集下拉框
		String subset = (String) this.getFormHM().get("subset");//节点id
		String type = (String) this.getFormHM().get("type");//入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他    区别临时变量时opt无法分别出是什么
		FunctionWizardbo bo = new FunctionWizardbo(this.frameconn,this.userView);
		ArrayList list = new ArrayList();
		try{
			if("1".equals(opt)){
				keyid = PubFunc.decrypt(SafeCode.decode(keyid));
				if("1".equals(flag)){
					if("volu".equals(subset))
						list = bo.functionListunit();
					else if("volp".equals(subset))
						list = bo.functionListpos();
					
				}else{
					if("stan".equals(subset)){
						list = bo.standList();
					}else if("tztd".equals(subset)||"sthl".equals(subset)){
						list = bo.standidList();
					}else if("volq".equals(subset)){//取临时变量
						list = bo.findVariable(keyid,opt,"");
					}else{
						if(keyid.length()>0){
							list = bo.functionList(keyid);
						}else{//没有传薪资类别，就把权限内的子集全弄出来
							list = bo.fieldSetList("1");
						}
					}
				}
			}else if("2".equals(opt)){//人事异动
				if("1".equals(flag)){
					if("volu".equals(subset))
						list = bo.functionListunit();
					else if("volp".equals(subset))
						list = bo.functionListpos();
				}else{
					if("stan".equals(subset)){
						list = bo.standList();
					}else if("tztd".equals(subset)||"sthl".equals(subset)){
						list = bo.standidList();
					}else if("volq".equals(subset)){//取临时变量
						list = bo.findVariable(keyid,opt,"");
					}else if("vsub".equals(subset)){
						list = fieldSetItem(keyid);
					}else 
						list = fieldItemList(keyid);
				}
			}else if("4".equals(opt) || "7".equals(opt)){//招聘、考勤，只取信息集的业务都可以走这里
				if("1".equals(flag)){
					if("volu".equals(subset))
						list = bo.functionListunit();
					else if("volp".equals(subset))
						list = bo.functionListpos();
				}else{
					if("stan".equals(subset)){
						list = bo.standList();
					}else if("tztd".equals(subset)||"sthl".equals(subset)){
						list = bo.standidList();
					}else{
						list = bo.fieldSetList(keyid);
					}
				}
			}else if("5".equals(opt)){//临时变量
				//keyid = PubFunc.decrypt(SafeCode.decode(keyid));
				if("1".equals(flag)){
					if("volu".equals(subset))
						list = bo.functionListunit();
					else if("volp".equals(subset))
						list = bo.functionListpos();
				}else{
					if("stan".equals(subset)){
						list = bo.standList();
					}else if("tztd".equals(subset)||"sthl".equals(subset)){
						list = bo.standidList();
					}else if("volq".equals(subset)){//取临时变量
						list = bo.findVariable(keyid,opt,type);
					}else
						list = bo.functionList();
				}
			}else if(StringUtils.equals("8",opt)){//我的个税
				Map fieldMap = new HashMap();
				fieldMap.put("name","个税明细");
				fieldMap.put("id","gz_tax_mx");
				list.add(fieldMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("data", list);
	}
	/**
	 * 
	* <p>Title:FunctionWizardTrans </p>
	* <p>Description:人事异动批量修改单指标-向导-下步-子集列表 </p>
	* <p>Company:hjsoft </p> 
	* @author gaohy
	* @date 2015-12-1下午02:57:06
	 */
	private  ArrayList fieldItemList(String tableid){
		 ArrayList fieldsetlist = new ArrayList();
		if(tableid.length()>0){
			try {
				HashMap map = new HashMap();
//				map.put("id", "");
//				map.put("name", "");
//			    fieldsetlist.add(map);
			    HashSet   hs   =   new   HashSet(); 
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
				ArrayList list = changebo.getAllFieldItem();
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					hs.add(fielditem.getFieldsetid());
				}
				String[] arr   =   (String[])hs.toArray(new String[0]);   
				Arrays.sort(arr);
				for(int j=0;j<arr.length;j++){
					map = new HashMap();
					String fieldsetid = arr[j];
					FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
					if(fieldset!=null){
						if(fieldset==null)
							 continue;
						 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
							 continue;
						 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
							 continue;
						 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
							 continue;
						 }
//						CommonData obj=new CommonData(fieldset.getFieldsetid()
//								 ,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
						 map.put("id", fieldset.getFieldsetid());
						 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
						fieldsetlist.add(map);
					}	 
				}
				
//				CommonData obj=new CommonData("vartemp"
//						 ,ResourceFactory.getProperty("menu.gz.variable"));
				map = new HashMap();
				map.put("id", "vartemp");
				map.put("name", ResourceFactory.getProperty("menu.gz.variable"));
				fieldsetlist.add(map);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fieldsetlist;
	}
	
	/**
	 * 
	 * <p>Title:FunctionWizardTrans </p>
	 * <p>Description:人事异动批量修改单指标-向导-下步-子集列表 </p>
	 * <p>Company:hjsoft </p> 
	*/
	private  ArrayList fieldSetItem(String tableid){
		ArrayList fieldsetlist = new ArrayList();
		TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
		try {
			ArrayList cellList = utilBo.getPageCell(Integer.parseInt(tableid), -1);

			for (int i = 0; i < cellList.size(); i++) {
				TemplateSet setBo = (TemplateSet) cellList.get(i);
				boolean isSubflag = setBo.isSubflag();
				if (isSubflag) {
					HashMap map = new HashMap();
					String fieldDesc = "";
					if (setBo.getField_hz().equals(setBo.getHz().replaceAll("[\\{\\}\\`]", ""))) {
						if (setBo.getChgstate() == 2) {
							fieldDesc = "拟";
						} else {
							fieldDesc = "现";
						}
						fieldDesc += setBo.getField_hz();
					} else {
						fieldDesc = setBo.getHz().replaceAll("[\\{\\}\\`]", "");
					}
					String id = setBo.getSetname();
					String subid = setBo.getSub_domain_id();
					if(StringUtils.isNotBlank(subid)) {
						id+="_"+subid;
					}
					id+="_"+setBo.getChgstate();
					map.put("id", id);
					map.put("name", setBo.getSetname() + "-" + fieldDesc);
					fieldsetlist.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldsetlist;
	}
}
