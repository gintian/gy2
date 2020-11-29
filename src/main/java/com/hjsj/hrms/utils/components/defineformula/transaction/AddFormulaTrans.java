package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：AddFormulaTrans 
 * 类描述：新增计算公式
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 4:28:56 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 4:28:56 PM
 * 修改备注： 
 * @version
 */
public class AddFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		HashMap hm = this.getFormHM();		
		String formulaitemid= (String)hm.get("formulaitemid"); //修改后项目id
		formulaitemid=formulaitemid!=null&&formulaitemid.trim().length()>0?formulaitemid:"";
		
		String groupid = (String)hm.get("groupid");//公式组id
		groupid = groupid!=null&&groupid.length()>0?groupid:"";
		
		String id= (String)hm.get("id"); //薪资类别id,人事异动模版id
		id=id!=null&&id.trim().length()>0?id:"";
		
		String itemname = (String)hm.get("itemname");//原项目id
		itemname = itemname!=null&&itemname.trim().length()>0?itemname:"";
		
		String oleitemid = (String) hm.get("oleitemid");
		oleitemid = oleitemid!=null&&oleitemid.trim().length()>0?oleitemid:"";
		
		String actflag = (String)hm.get("actflag");//人事异动-行为标识，新增、修改、调整、删除等
		actflag = actflag!=null&&actflag.length()>0?actflag:"";
		
		String hzname = (String) hm.get("hzname");
		hzname = hzname!=null&&hzname.length()>0?hzname:"";
		
		String module = (String)hm.get("module"); //模块号，1是薪资,3是人事异动
		String addOrUpdate = (String)hm.get("addOrUpdate");
		String formulaType = (String)hm.get("formulaType"); //公式类别，1:计算公式  2：审核公式
		DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
		String formulas = "";//所有计算项目的公式
		try {
			ArrayList storeList = new ArrayList();//返回页面 新增或修改 的这条记录的数据，静态更新页面store zhaoxg add 2016-5-24
			if("2".equals(formulaType)){//人事异动和薪资相同，所以拿了出来，gaohy,2016-1-5
				if("1".equals(module))
					id = PubFunc.decrypt(SafeCode.decode(id));
				String spFormulaName = (String)hm.get("spFormulaName");
				String spComment = (String)hm.get("spComment");
				//新增或编辑
				String log = bo.editGzSpFormula(id, formulaitemid, spFormulaName, spComment, module,storeList);
				this.getFormHM().put("@eventlog", log);	
			}else if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				bo.editGzFormula(id, formulaitemid, addOrUpdate, oleitemid,storeList);
			}else if("3".equals(module)){//人事异动（gaohy,2015-12-18)
					if("update".equals(addOrUpdate)){//修改里的修改一条项目
						String seq = (String) hm.get("seq");//人事异动计算公式序号  lis 20160824
						seq = seq!=null&&seq.length()>0?seq:"";
						formulas=bo.getFormulas(id,groupid,"0"); //获取所有计算公式
						String formulaItems[]=formulas.split("\\`");
						for (int i = 0; i < formulaItems.length; i++) {
							String formulaItem[]=formulaItems[i].split("\\=");
							String[] arry = formulaItem[0].split("_");
							//String itemName=arry[1];
							String itemNameId = arry[1];
							String seq2 = arry[0];
							String formulaitemname[]=formulaitemid.split("\\:");
							
							//ArrayList ls=new ArrayList();
								if(seq2.equals(seq) && itemNameId.equalsIgnoreCase(itemname)){//获得要修改的改行数据，itemname的前缀是序号，等于i，例如：0_A1905_2=今天
									/*for (int j = 0; j < formulaItems.length; j++) {//将所有计算公式id存放到list中
										String formu[]=formulaItems[j].split("\\=");
										String it=formu[0].substring(0, formu[0].lastIndexOf("_"));
										String itemNa=it.substring(it.indexOf("_")+1);
										ls.add(itemNa);
									}*/
									//if(!ls.contains(formulaitemname[0].toUpperCase())){//判断修改的项目是否已存在
										formulaItems[i]=formulaitemname[0].toUpperCase()+"_2"+"=";
									//}
									/*else if(formulaItem.length==2){
										formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"="+formulaItem[1];
										hm.put("info", "noRepet");
									}else if(formulaItem.length==1){
										formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"=";
										hm.put("info", "noRepet");
									}*/
									LazyDynaBean bean = new LazyDynaBean();
									bean.set("hzname", hzname);
									bean.set("itemname", itemname.toUpperCase());
									storeList.add(bean);
								}else if(formulaItem.length==2){//因为把计算公式拆开判断，所以要重新组装
									formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"="+formulaItem[1];
								}else if(formulaItem.length==1){
									formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"=";
								}
							
						}
						StringBuffer formulaitems=new StringBuffer();//组装后的计算公式
						for (int j = 0; j < formulaItems.length; j++){
							formulaitems.append(formulaItems[j]);
							formulaitems.append("`");
						}
						//不改变alertItem方法进行查询
						RecordVo vo=new RecordVo("gzadj_formula");
						vo.setInt("id",Integer.parseInt(groupid));
						vo.setInt("tabid",Integer.parseInt(id));
						vo=dao.findByPrimaryKey(vo);
						
						ChangeFormulaBo formulabo = new ChangeFormulaBo();
						formulabo.alertItem(dao,id,groupid,formulaitems.toString(),vo.getString("cfactor"));//修改数据库表
					}else if("new".equals(addOrUpdate)){//修改里的新增一条项目，可以重复
						formulas=bo.getFormulas(id,groupid,"3"); //获取所有计算公式
						LazyDynaBean bean = new LazyDynaBean();
						if(formulas.length()>0){
						    String formulaItems[]=formulas.split("\\`");
							/*ArrayList ls=new ArrayList();
							for (int j = 0; j < formulaItems.length; j++) {//将所有计算公式id存放到list中
								String formu[]=formulaItems[j].split("\\=");
								String it=formu[0].substring(0, formu[0].lastIndexOf("_"));
								String itemNa=it.substring(it.indexOf("_")+1);
								ls.add(itemNa);
							}*/
							String formulaitemname[]=formulaitemid.split("\\:");
							//if(!ls.contains(formulaitemname[0].toUpperCase())){//新增项目不重复
								String formulaitems=formulas+formulaitemname[0].toUpperCase()+"_2=`";
								ChangeFormulaBo formulabo = new ChangeFormulaBo();//{hzname=0_AA31C:应赔偿培训费, itemname=0_AA31C}
								bean.set("hzname", formulaitemid);
								bean.set("itemname", formulaitemname[0].toUpperCase());
								bean.set("seq", formulaItems.length+"");
								storeList.add(bean);
								//不改变alertItem方法进行查询
								RecordVo vo=new RecordVo("gzadj_formula");
								vo.setInt("id",Integer.parseInt(groupid));
								vo.setInt("tabid",Integer.parseInt(id));
								vo=dao.findByPrimaryKey(vo);
								
								formulabo.alertItem(dao,id,groupid,formulaitems.toString(),vo.getString("cfactor"));
							/*}else {//新增项目重复了
								hm.put("info", "noRepet");
							}*/
						}else{
							String formulaitemname[]=formulaitemid.split("\\:");
							String formulaitems=formulaitemname[0].toUpperCase()+"_2=`";
							ChangeFormulaBo formulabo = new ChangeFormulaBo();
							bean.set("hzname", formulaitemid);
							bean.set("itemname", formulaitemname[0].toUpperCase());
							bean.set("seq", "0");
							storeList.add(bean);
							//不改变alertItem方法进行查询
							RecordVo vo=new RecordVo("gzadj_formula");
							vo.setInt("id",Integer.parseInt(groupid));
							vo.setInt("tabid",Integer.parseInt(id));
							vo=dao.findByPrimaryKey(vo);
							
							formulabo.alertItem(dao,id,groupid,formulaitems.toString(),vo.getString("cfactor"));
						}
					}
				}
			this.getFormHM().put("storeList", storeList);
		} catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
