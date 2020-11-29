package com.hjsj.hrms.utils.components.functionWizard.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.functionWizard.businessobject.CodeItemBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SearchGzFactorItemTrans 
 * 类描述： 函数向导获取薪资标准表横纵指标
 * 创建人：zhaoxg
 * 创建时间：Nov 18, 2015 4:00:31 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 18, 2015 4:00:31 PM
 * 修改备注： 
 * @version
 */
public class SearchGzFactorItemTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String standid = (String)this.getFormHM().get("standid");//标准表id
		standid = standid!=null&&standid.length()>0?standid:"";
		if(standid.trim().length()>0){
			standid = standid.replaceAll("\"", "");
			String keyid = (String)this.getFormHM().get("keyid");
			keyid=keyid!=null&&keyid.trim().length()>0?keyid:"";
			String vtemptype = (String)this.getFormHM().get("vtemptype");//为了临时变量能区别是哪个模块的type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			vtemptype=vtemptype!=null&&vtemptype.trim().length()>0?vtemptype:"";
			String opt = (String)this.getFormHM().get("opt");//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			opt=opt!=null&&opt.trim().length()>0?opt:"";
			
			String factorId = (String) this.getFormHM().get("factorId");//横纵的id标识
			
			ArrayList hfactorlsit = new ArrayList();
			ArrayList vfactorlsit = new ArrayList();
			ArrayList s_hfactorlsit = new ArrayList();
			ArrayList s_vfactorlsit = new ArrayList();
			ArrayList itemidlist = new ArrayList();
			String[] arr = standid.split(":");
			HashMap map = new HashMap();
			if(arr.length==7){
				String hfactor = "";
				String hfcodesetid = "";
				String vfactor= "";
				String vfcodesetid = "";
				String s_hfactor= "";
				String s_hfcodesetid = "";
				String s_vfactor = "";
				String s_vfcodesetid = "";
				String itemtype = "";
				String itemname = "";
				
				if(arr[0].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[0]);
					if(item!=null){
						hfcodesetid=item.getCodesetid();
						hfactor=item.getItemdesc();
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						hfactorlsit.add(0,map);
					}
				}
				if(arr[1].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[1]);
					if(item!=null){
						vfcodesetid=item.getCodesetid();
						vfactor=item.getItemdesc();
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						vfactorlsit.add(0,map);
					}
				}
				if(arr[2].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[2]);
					if(item!=null){
						s_hfcodesetid=item.getCodesetid();
						s_hfactor=item.getItemdesc();
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						s_hfactorlsit.add(0,map);
					}
				}
				if(arr[3].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[3]);
					if(item!=null){
						s_vfcodesetid=item.getCodesetid();
						s_vfactor=item.getItemdesc();
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						s_vfactorlsit.add(0,map);
					}
				}
				if(arr[5].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[5]);
					if(item!=null){
						itemtype=item.getItemtype();
						itemname=item.getItemdesc();
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						itemidlist.add(map);
					}
				}

				CodeItemBo codebo = new CodeItemBo(this.frameconn,this.userView);
				codebo.setHfcodesetid(hfcodesetid);
				codebo.setVfcodesetid(vfcodesetid);
				codebo.setItemtype(itemtype);
				codebo.setS_hfcodesetid(s_hfcodesetid);
				codebo.setS_vfcodesetid(s_vfcodesetid);
				if("1".equalsIgnoreCase(opt)){
					if(keyid!=null&&keyid.trim().length()>0){
						keyid = PubFunc.decrypt(SafeCode.decode(keyid));
						codebo.getMidVariableList(keyid);
					}else{
						codebo.functionList();
					}
				}else if("2".equalsIgnoreCase(opt)){//人事异动和临时变量走这里
					codebo.getTableList(keyid);
				}else if("5".equalsIgnoreCase(opt)){
					if(StringUtils.isNotBlank(vtemptype) && StringUtils.isNotBlank(keyid))
						codebo.functionList(vtemptype,keyid);//这里做这个是为了临时变量里面可以添加自己的临时变量
					else
						codebo.functionList();
				}else{
					if(keyid!=null&&keyid.trim().length()>0){
						keyid = PubFunc.decrypt(SafeCode.decode(keyid));
						codebo.getMidVariableList(keyid);
					}else{
						codebo.functionList();
					}
				}
				if("hfactor_arr".equals(factorId)){
					this.getFormHM().put("data", codebo.getHfactorlist());
				}else if("vfactor_arr".equals(factorId)){
					this.getFormHM().put("data", codebo.getVfactorlist());
				}else if("s_hfactor_arr".equals(factorId)){
					this.getFormHM().put("data", codebo.getS_hfactorlist());
				}else if("s_vfactor_arr".equals(factorId)){
					this.getFormHM().put("data", codebo.getS_vfactorlist());
				}else{
					this.getFormHM().put("data", codebo.getItemidlist());
				}
			}
		}
	}

}
