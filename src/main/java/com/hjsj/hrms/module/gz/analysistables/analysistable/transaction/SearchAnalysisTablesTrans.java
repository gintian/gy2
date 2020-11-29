package com.hjsj.hrms.module.gz.analysistables.analysistable.transaction;

import com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.TableService;
import com.hjsj.hrms.module.gz.analysistables.analysistable.businessobject.impl.TableServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAnalysisTablesTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try{
			int module = (Integer) this.getFormHM().get("imodule");
			TableService tableserv = new TableServiceImpl(frameconn, userView);
			GzAnalysisUtil gasu = new GzAnalysisUtil(frameconn,this.getUserView());
			ArrayList<HashMap> repCatList = gasu.getReportCategoryList(module);
			
			ArrayList table_data = new ArrayList();
			int count = 0;
			for(HashMap map :repCatList){
				String rsid = (String) map.get("rsid");
				String rsname = (String) map.get("rsname");
				
				String tableName = rsname;
				String B0110 = "";
				String username = "";
				//操作列：0为分类；1为明细有增删权限；2为无明细增删权限；3为薪资分析表，只有修改的权限；4为上级的可看不可编辑即没有修改删除功能
 				int opretion = 2;
                //0:title 1:子列
                int istitle = 1;
				int rsidint = Integer.parseInt(rsid);
				/**
				 * 人员工资台帐、工资项目分类统计台帐、人员工资项目统计表、工资总额构成分析表、单位部门工资项目统计表分类下有新增、修改、删除分析表功能。(5、6、7、10、11)
				 * 薪资分析也有修改的功能，但是不能新增和删除
				 */
				if(rsidint == 5 ||rsidint == 6 ||rsidint == 7 ||rsidint == 10 ||rsidint == 11
                    || rsidint==14 ||rsidint == 15 ||rsidint == 16){
					opretion = 0;
				}
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("rsid", rsid);
				bean.set("tableName", tableName);
				bean.set("B0110", B0110);
				bean.set("username", username);
				bean.set("opretion", opretion);
                bean.set("istitle", istitle);
                //只展开第一个
                if(count == 0) {
                	bean.set("expanded", true);
                }
                count++;
				//薪资分析各表仅有修改功能
				if(rsidint == 12) {
					opretion = 3;
				}
				ArrayList<HashMap> repList = gasu.getAnaysisReportList(module, Integer.parseInt(rsid));
				ArrayList child_list = new ArrayList();
				for(HashMap hash:repList){
					int opretion_tem = opretion;
                    istitle = 0;
					String rsdtlid = (String) hash.get("tabid");
                    tableName = (String)hash.get("tabname");
                    B0110 =(hash.get("b0110")==null?"": (String) hash.get("b0110"));
                    String create_time = (String)hash.get("create_time");
                    String map_opretion = (String)hash.get("opretion");
                    String unitid = "";
                    String unitdesc = "";
                    if(StringUtils.isNotEmpty(B0110)){
                    	unitid = B0110.substring(2);
                    	unitdesc = AdminCode.getCodeName(B0110.substring(0,2),unitid);
                    	B0110 = PubFunc.encrypt(B0110);
                    }
					username = hash.get("name")==null?"":(String)hash.get("name");
					if(opretion_tem == 0){
						opretion_tem = Integer.parseInt(map_opretion);
					}
					LazyDynaBean bean_tem = new LazyDynaBean();
					ArrayList list = tableserv.getReportItemlist(PubFunc.decrypt(rsdtlid), userView);//如果没有设置统计指标也提示出来
                    //是否需要跳转到薪资分析表编辑页面
					String jump2set= "false";
					
					HashMap map_nbase_salaryid = gasu.getCtrlParam2Map(rsid, PubFunc.decrypt(rsdtlid));
					String nbase = (String) map_nbase_salaryid.get("nbase");
					String salaryids = (String) map_nbase_salaryid.get("salaryids");
					
					if(StringUtils.isBlank(nbase)
                            || StringUtils.isBlank(salaryids)
                            ||(!"12".equalsIgnoreCase(rsid) && list.size() == 0)) {
                        jump2set = "true";
					}
					bean_tem.set("jump2set", jump2set);
					bean_tem.set("rsid", rsid);
					bean_tem.set("rsid_enc", PubFunc.encrypt(rsid));
					bean_tem.set("tabid", PubFunc.decrypt(rsdtlid));
					bean_tem.set("rsdtlid",rsdtlid);
					bean_tem.set("tableName", tableName);
					bean_tem.set("B0110", B0110);
					bean_tem.set("unitdesc", unitdesc);
					bean_tem.set("username", username);
					bean_tem.set("opretion", opretion_tem);
					bean_tem.set("istitle", istitle);
					bean_tem.set("nbase", nbase);
					bean_tem.set("salaryids", salaryids);
					bean_tem.set("verifying", (String)map_nbase_salaryid.get("verifying"));
                    bean_tem.set("create_time", create_time);
                    bean_tem.set("leaf", true);
                    bean_tem.set("iconCls","treeiconCls");
                    child_list.add(bean_tem);
				}
				if(rsidint == 12) {
					child_list.addAll(gasu.getCustomData(module, rsid));
				}
				bean.set("children", child_list);
				table_data.add(bean);
			}
			
			HashMap return_data = new HashMap();
			//按钮权限
			StringBuffer str = new StringBuffer();
			String unitcodes=this.userView.getUnitIdByBusi("1");  //UM010101`UM010105`
			String[] units = unitcodes.split("`");
			for(int i=0;i<units.length;i++)
			{
				String codeid=units[i];
				if(codeid==null|| "".equals(codeid))
					continue;
    			if(codeid!=null&&codeid.trim().length()>2)
				{
    				String privCodeValue = codeid.substring(2);
    				str.append(",");
    				str.append(privCodeValue);
				}
			}
			//如果非管理员，什么权限都没有，则传一个orgid字符串过去
			if(!this.userView.isSuper_admin() && StringUtils.isBlank(str.toString())) {
				str.append("qwer");
			}
			//新增，修改，删除，复制的权限
			return_data.put("add_pow", gasu.hasTheFunction("32407101", "325040101", module));
			return_data.put("edi_pow", gasu.hasTheFunction("32407104", "325040104", module));
			return_data.put("del_pow", gasu.hasTheFunction("32407102", "325040102", module));
			return_data.put("cop_pow", gasu.hasTheFunction("32407105", "325040105", module));
			
			return_data.put("priv", StringUtils.isNotBlank(str.toString())?str.substring(1):"");
			return_data.put("table_data", table_data);

			this.getFormHM().put("return_code", "success");
			this.getFormHM().put("return_msg", "");
			this.getFormHM().put("return_data", return_data);

		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			
		}
	}
	
}
