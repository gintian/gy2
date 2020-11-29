package com.hjsj.hrms.utils.components.functionWizard.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：FunctionWizardItemTrans 
 * 类描述： 表达式数据联动
 * 创建人：zhaoxg
 * 创建时间：Nov 10, 2015 2:58:01 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 10, 2015 2:58:01 PM
 * 修改备注： 
 * @version
 */
public class FunctionWizardItemTrans extends IBusiness {

	public void execute() throws GeneralException {
			String keyid = (String)this.getFormHM().get("keyid");
			keyid=keyid!=null&&keyid.trim().length()>0?keyid:"";
			
			Pattern pattern = Pattern.compile("^[0-9]*$");
			Matcher matcher = pattern.matcher(keyid);
			boolean b= matcher.matches();
			if(!b)
				keyid = PubFunc.decrypt(SafeCode.decode(keyid));
			
			String type = (String) this.getFormHM().get("type");//A:字符型 N：数值型 D：日期型 V：全类型 item：代码型 I：整形
			type=type!=null&&type.trim().length()>0?type:"";
			
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");//关联代码类
			fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
			
			String functionid = (String)this.getFormHM().get("functionid");//节点id
			functionid=functionid!=null&&functionid.trim().length()>0?functionid:"";
			
			String opt = (String)this.getFormHM().get("opt");//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			opt=opt!=null&&opt.trim().length()>0?opt:"";
			
			String mode = (String)this.getFormHM().get("mode");//具体模块自定义的功能点标识xzgl_jsgs//薪资rsyd_jsgs//人事异动
			mode=mode!=null&&mode.trim().length()>0?mode:"";
			
			String vtemptype = (String)this.getFormHM().get("vtemptype");//type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他,作为临时变量特殊处理
			vtemptype=vtemptype!=null&&vtemptype.trim().length()>0?vtemptype:"";

			ArrayList list = new ArrayList();
			if("1".equalsIgnoreCase(opt)){
				if(keyid!=null&&keyid.trim().length()>0){
					list = functionList(keyid,fieldsetid,functionid,type);
				}else{
					list = functionList(fieldsetid, type);
				}
			}
			/**
			 * gaohy
			 * 增加人事异动模块
			 */
			else if("2".equalsIgnoreCase(opt)){
			//	if(fieldsetid!=null&&fieldsetid.equalsIgnoreCase("vartemp"))
				{
					if(keyid!=null&&keyid.trim().length()>0)
						list = changeFunctionList(keyid,fieldsetid,functionid,type);
					
				}
			}else if(StringUtils.equals("8",opt)){
				list = this.listGzTaxMxField(type);
			}
			else
			{
				list = functionListVar(keyid, vtemptype, fieldsetid, type);
			}
			
			this.getFormHM().put("data", list);
		}
	 /**
	     * 查询薪资项目子集
	     * gaohy
	     * @param dao
	     * @param salaryid 薪资id
	     * @return retlist
	     * @throws GeneralException
	     */
		private ArrayList functionList(String salaryid,String fieldsetid,String functionid,String type){
			ArrayList list=new ArrayList();
			try{
				HashMap map = new HashMap();
//				map.put("id", "");
//				map.put("name", "");
				ArrayList alist=new ArrayList();
				//alist.add(0,map);
				ArrayList dlist = new ArrayList();
				//dlist.add(0,map);
				ArrayList nlist = new ArrayList();
				//nlist.add(0,map);
				ArrayList itemlist = new ArrayList();
				//itemlist.add(0,map);
				ArrayList vlist = new ArrayList();
				//vlist.add(0,map);
				ArrayList ilist = new ArrayList();
				//ilist.add(0,map);

				ArrayList listitem= new ArrayList();
				if("vartemp".equalsIgnoreCase(fieldsetid)){
					listitem=getMidVariableList(salaryid);  
				}else{
					//xiegh 20170512 bug24310 add个性化处理
					if(fieldsetid!=null&&fieldsetid.length()>0&&(fieldsetid.charAt(0)=='B'||fieldsetid.charAt(0)=='K')&&(functionid.contentEquals("V_volu9_20")||functionid.contentEquals("V_volp7_20"))) //如果是取部门值、取单位值
						listitem=getMidVariableList("-1",fieldsetid); 
					else
						listitem=getMidVariableList(salaryid,fieldsetid);  
				}
				if(listitem!=null){
					for(int j=0;j<listitem.size();j++){
						map = new HashMap();
						FieldItem item = (FieldItem)listitem.get(j);
						if("A".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.isCode()){
								itemlist.add(map);
							}
							alist.add(map);
							vlist.add(map);
						}else if("N".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.getDecimalwidth()==0){
								ilist.add(map);
							}
							nlist.add(map);
							vlist.add(map);
						}else if("D".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							dlist.add(map);
							if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
								continue;
							}
							vlist.add(map);
						}
					}
				}
				if("A".equals(type)){
					list = alist;
				}else if("D".equals(type)){
					list = dlist;
				}else if("N".equals(type)){
					list = nlist;
				}else if("V".equals(type)){
					list = vlist;
				}else if("item".equals(type)){
					list = itemlist;
				}else if("I".equals(type)){
					list = ilist;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return list;
		}
		 /**
	     * 查询子集
		 * @param type 
		 * @throws GeneralException 
	     * @throws GeneralException
	     */
		private ArrayList functionList(String fieldsetid, String type) throws GeneralException{
			ArrayList list = new ArrayList();
			try {
				HashMap map = new HashMap();
//				map.put("id", "");
//				map.put("name", "");
				ArrayList alist=new ArrayList();
				//alist.add(0,map);
				ArrayList dlist = new ArrayList();
				//dlist.add(0,map);
				ArrayList nlist = new ArrayList();
				//nlist.add(0,map);
				ArrayList itemlist = new ArrayList();
				//itemlist.add(0,map);
				ArrayList vlist = new ArrayList();
				//vlist.add(0,map);
				ArrayList ilist = new ArrayList();
				//ilist.add(0,map);

				ArrayList listitem= DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
				if(listitem!=null){
					for(int j=0;j<listitem.size();j++){
						map = new HashMap();
						FieldItem item = (FieldItem)listitem.get(j);
						if(!"Q03".equalsIgnoreCase(fieldsetid))//考勤不走子集指标权限管理
						{
							if(this.userView.analyseFieldPriv(item.getItemid())==null){
								continue;
							}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
								continue;
						}
						
						if("A".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.isCode()){
								itemlist.add(map);
							}
							alist.add(map);
							vlist.add(map);
						}else if("N".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.getDecimalwidth()==0){
								ilist.add(map);
							}
							nlist.add(map);
							vlist.add(map);
						}else if("D".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							dlist.add(map);
							vlist.add(map);
						}
					}
				}
				if("A".equals(type)){
					list = alist;
				}else if("D".equals(type)){
					list = dlist;
				}else if("N".equals(type)){
					list = nlist;
				}else if("V".equals(type)){
					list = vlist;
				}else if("item".equals(type)){
					list = itemlist;
				}else if("I".equals(type)){
					list = ilist;
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return list;
		}
		 /**
	     * 查询人事变化子集
	     * @param itemtype 子集数据类型
	     * @param itemlist 子集集合
	     * @return retlist
	     */
		public ArrayList changeFunctionList(String tableid,String fieldsetid,String functionid,String type){
			ArrayList list=new ArrayList();
			try {
				HashMap map = new HashMap();
	//			map.put("id", "");
	//			map.put("name", "");
				ArrayList alist=new ArrayList();
				//alist.add(0,map);
				ArrayList dlist = new ArrayList();
				//dlist.add(0,map);
				ArrayList nlist = new ArrayList();
				//nlist.add(0,map);
				ArrayList codelist = new ArrayList();
				//codelist.add(0,map);
				ArrayList vlist = new ArrayList();
				//vlist.add(0,map);
				ArrayList ilist = new ArrayList();
				//ilist.add(0,map);
				
				if("vartemp".equalsIgnoreCase(fieldsetid)){
					TempvarBo tempvar = new TempvarBo();
					ArrayList itemlist = tempvar.getMidVariableList(this.frameconn,tableid);
					for(int i=0;i<itemlist.size();i++){
						map = new HashMap();
						FieldItem item = (FieldItem)itemlist.get(i);
						if(item!=null){
							if("A".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								if(item.isCode()){
									codelist.add(map);
								}
								alist.add(map);
								vlist.add(map);
							}else if("N".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								if(item.getDecimalwidth()==0){
									ilist.add(map);
								}
								nlist.add(map);
								vlist.add(map);
							}else if("D".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								dlist.add(map);
								if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
									continue;
								}
								vlist.add(map);
							}
						}
					}
				}else if("V_vsub7_3_3".equalsIgnoreCase(functionid)) {
					TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
					ArrayList cellList = utilBo.getPageCell(Integer.parseInt(tableid), -1);
					SubField subtable = null;
	
					for (int i = 0; i < cellList.size(); i++) {
						TemplateSet setBo = (TemplateSet) cellList.get(i);
						boolean isSubflag = setBo.isSubflag();
						
						String id = setBo.getSetname();
						String subid = setBo.getSub_domain_id();
						if(StringUtils.isNotBlank(subid)) {
							id+="_"+subid;
						}
						id+="_"+setBo.getChgstate();
						if (isSubflag && id.equalsIgnoreCase(fieldsetid)) {
							SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
							ArrayList sublist = subDomain.getSubFieldList();
							for(int j = 0; j < sublist.size(); j++) {
								map = new HashMap();
								SubField subField = (SubField)sublist.get(j);
								String itemId = subField.getFieldname();
								String name = subField.getTitle();
								FieldItem item = subField.getFieldItem();
								if(item == null) {//可能是自己附件指标，
									continue;
								}
								map.put("id", itemId+":"+name);
								map.put("name", name);
								if("A".equalsIgnoreCase(item.getItemtype())){
									if(item.isCode()){
										codelist.add(map);
									}
									alist.add(map);
									vlist.add(map);
								}else if("N".equalsIgnoreCase(item.getItemtype())){
									if(item.getDecimalwidth()==0){
										ilist.add(map);
									}
									nlist.add(map);
									vlist.add(map);
								}else if("D".equalsIgnoreCase(item.getItemtype())){
									dlist.add(map);
									vlist.add(map);
								}
							}
						}
					}
				}else{
					ArrayList itemlist = itemList(tableid);
					for(int i=0;i<itemlist.size();i++){
						map = new HashMap();
						FieldItem item = (FieldItem)itemlist.get(i);
						if(item!=null&&item.getFieldsetid().equalsIgnoreCase(fieldsetid)){
							String itemid = item.getItemid();
							itemid=itemid.indexOf("_")!=-1?itemid.substring(0,itemid.indexOf("_")):itemid;
							if("A".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								if(item.isCode()){
									codelist.add(map);
								}
								alist.add(map);
								vlist.add(map);
							}else if("N".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								if(item.getDecimalwidth()==0){
									ilist.add(map);
								}
								nlist.add(map);
								vlist.add(map);
							}else if("D".equalsIgnoreCase(item.getItemtype())){
								map.put("id", item.getItemid()+":"+item.getItemdesc());
								map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
								dlist.add(map);
								if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
									continue;
								}
								vlist.add(map);
							}
						}
					}
				}
				if("A".equals(type)){
					list = alist;
				}else if("D".equals(type)){
					list = dlist;
				}else if("N".equals(type)){
					list = nlist;
				}else if("V".equals(type)){
					list = vlist;
				}else if("item".equals(type)){
					list = codelist;
				}else if("I".equals(type)){
					list = ilist;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
		/**
		* <p>Description:查询子集 </p>
		* @author gaohy
		* @date 2015-12-2下午01:32:52
		 */
		private ArrayList functionListtemp(String fieldsetid,String type){
			HashMap map = new HashMap();
			map.put("id", "");
			map.put("name", "");
			ArrayList list=new ArrayList();
			
			ArrayList alist=new ArrayList();
			alist.add(0,map);
			ArrayList dlist = new ArrayList();
			dlist.add(0,map);
			ArrayList nlist = new ArrayList();
			nlist.add(0,map);
			ArrayList itemlist = new ArrayList();
			itemlist.add(0,map);
			ArrayList vlist = new ArrayList();
			vlist.add(0,map);
			ArrayList ilist = new ArrayList();
			ilist.add(0,map);
			
			ArrayList listitem= new ArrayList();
	
			listitem=DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
			if(listitem!=null){
				for(int j=0;j<listitem.size();j++){
					map = new HashMap();
					FieldItem item = (FieldItem)listitem.get(j);
					if(this.userView.analyseFieldPriv(item.getItemid())==null){
						continue;
					}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
						continue;
					
					if("A".equalsIgnoreCase(item.getItemtype())){
						map.put("id", item.getItemid()+":"+item.getItemdesc());
						map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.isCode()){
							itemlist.add(map);
						}
						alist.add(map);
						vlist.add(map);
					}else if("N".equalsIgnoreCase(item.getItemtype())){
						map.put("id", item.getItemid()+":"+item.getItemdesc());
						map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.getDecimalwidth()==0){
							ilist.add(map);
						}
						nlist.add(map);
						vlist.add(map);
					}else if("D".equalsIgnoreCase(item.getItemtype())){
						map.put("id", item.getItemid()+":"+item.getItemdesc());
						map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						dlist.add(map);
						vlist.add(map);
					}
				}
			}
			if("A".equals(type)){
				list = alist;
			}else if("D".equals(type)){
				list = dlist;
			}else if("N".equals(type)){
				list = nlist;
			}else if("V".equals(type)){
				list = vlist;
			}else if("item".equals(type)){
				list = itemlist;
			}else if("I".equals(type)){
				list = ilist;
			}
			return list;
		}
		 /**
	     * 字符转数字
	     * @param itemtype 子集数据类型
	     * @return retlist
	     */
		public String strTonum(String itemtype){
			String num = "";
			if("A".equalsIgnoreCase(itemtype)){
				num = "2";
			}else if("N".equalsIgnoreCase(itemtype)){
				num = "1";
			}else if("D".equalsIgnoreCase(itemtype)){
				num = "3";
			}else if("code".equalsIgnoreCase(itemtype)){
				num = "4";
			}
			return num;
			
		}
		/**
		 * 从临时变量中取得对应指标列表
		 * @return FieldItem对象列表
		 * @throws GeneralException
		 */
		private ArrayList getMidVariableList(String salaryid,String fieldsetid){
			ArrayList fieldlist=new ArrayList();
			String str = "";
			try{
				ContentDAO dao=new ContentDAO(this.frameconn);
				StringBuffer buf = new StringBuffer();
				if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
					String fieldsetid1 = (String) this.userView.getHm().get("fieldsetid");
					String sqlstr="select * from fielditem ";
					if(fieldsetid1!=null&&fieldsetid1.trim().length()>0){
						sqlstr+=" where fieldsetid='"+fieldsetid1+"' and useflag = '1'";
					}
					RowSet rset=dao.search(sqlstr);
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "AuditingFormula"));
						item.setDecimalwidth(rset.getInt("DECIMALWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
					rset.close();
				}else if("-1".equals(salaryid)){//总额模块  zhaoxg add 2013-9-17
					String sqlstr="select * from fielditem ";
					sqlstr+=" where fieldsetid='"+fieldsetid+"' and useflag = '1'";
					RowSet rset=dao.search(sqlstr);
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "AuditingFormula"));
						item.setDecimalwidth(rset.getInt("DECIMALWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
					rset.close();
				}else{
					buf.append("select * from salaryset where FIELDSETID='"+fieldsetid+"'");
					if(salaryid!=null&&!"all".equalsIgnoreCase(salaryid))
						buf.append(" and salaryid='"+salaryid+"'");
					RowSet rset=dao.search(buf.toString());
					HashMap itemMap =new HashMap();//存储指标编号，用来过滤重复指标
					while(rset.next()){
						FieldItem item=new FieldItem();
						String itemid =rset.getString("ITEMID");
						if ("all".equals(salaryid)){
						    if (itemMap.containsKey(itemid)){
						        continue;
						    }
						}
						itemMap.put(itemid, "1");
						item.setItemid(itemid);
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
						item.setDecimalwidth(rset.getInt("DECWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						str+=item.getItemid()+",";
						item.setVarible(1);
						fieldlist.add(item);
					}
				}

				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if("-2".equals(salaryid)||"-1".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				
			}else{
				ArrayList fieldlist2=sysFieldList();
				for(int i=0;i<fieldlist2.size();i++){
					FieldItem item= (FieldItem)fieldlist2.get(i);
					if(item!=null&&item.getItemid()!=null&&item.getItemid().trim().length()>0){
						if(str.toUpperCase().indexOf(item.getItemid().toUpperCase())!=-1){
							continue;
						}
						str+=item.getItemid()+",";
						fieldlist.add(item);
					}
				}
			}

			return fieldlist;
		}
		private ArrayList sysFieldList(){
			ArrayList fieldlist=new ArrayList();
			FieldItem item=new FieldItem();
			item.setItemid("a00z2");
			item.setItemdesc("发放日期");
			item.setFieldsetid("A01");
			item.setItemlength(10);
			item.setFormula("");
			item.setDecimalwidth(0);
			item.setItemtype("D");
			item.setCodesetid("0");
			fieldlist.add(item);
			
			item=new FieldItem();
			item.setItemid("a00z3");
			item.setItemdesc("发放次数");
			item.setFieldsetid("A01");
			item.setItemlength(10);
			item.setFormula("");
			item.setDecimalwidth(0);
			item.setItemtype("N");
			item.setCodesetid("0");
			fieldlist.add(item);
			
			item=new FieldItem();
			item.setItemid("A00Z0");
			item.setItemdesc("归属日期");
			item.setFieldsetid("A01");
			item.setItemlength(10);
			item.setFormula("");
			item.setDecimalwidth(0);
			item.setItemtype("D");
			item.setCodesetid("0");
			fieldlist.add(item);
			
			item=new FieldItem();
			item.setItemid("A00Z1");
			item.setItemdesc("归属次数");
			item.setFieldsetid("A01");
			item.setItemlength(10);
			item.setFormula("");
			item.setDecimalwidth(0);
			item.setItemtype("N");
			item.setCodesetid("0");
			fieldlist.add(item);
			
			item=DataDictionary.getFieldItem("e0122");
			fieldlist.add(item);
			
			item=DataDictionary.getFieldItem("a0101");
			fieldlist.add(item);
			
			item=DataDictionary.getFieldItem("a01Z0");
			if(item!=null)
				fieldlist.add(item);
			
			return fieldlist;
		}
		private ArrayList getMidVariableList(String salaryid){
			ArrayList fieldlist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,CodeSetID from ");
			if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				buf.append(" midvariable where nflag=5 and templetid=0 ");
				String fieldsetid = (String) this.userView.getHm().get("fieldsetid");
				if(fieldsetid!=null&&fieldsetid.length()>0){
					buf.append(" and (cstate is null or cstate='");
					buf.append(fieldsetid);
					buf.append("')");
				}
			}else if("-1".equals(salaryid)){//薪资总额，临时变量无共享一说，zhaoxg add 2013-9-17
				buf.append(" midvariable where nflag=4 and templetid=0 ");
					buf.append(" and  cstate='");
					buf.append(-1);
					buf.append("'");
			}else{
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				if(salaryid!=null&&!"all".equalsIgnoreCase(salaryid)){
					buf.append(" and (cstate is null or cstate='");
					buf.append(salaryid);
					buf.append("')");
				}
			}
			RowSet rset;
			try {
				String str = "";
				rset = dao.search(buf.toString());
				while(rset.next())
				{
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("cname"));
					if("-2".equals(salaryid)){//zhaoxg add  数据采集 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
						String fieldsetid = (String) this.userView.getHm().get("fieldsetid");
						item.setFieldsetid(fieldsetid);//没有实际含义
					}else{
						item.setFieldsetid("A01");//没有实际含义
					}				
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("CodeSetID"));
					switch(rset.getInt("ntype")) 
					{
					case 1://
						item.setItemtype("N");
						break;
					case 2:
						item.setItemtype("A");
						break;
					case 4:
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					if(str.indexOf(item.getItemid())!=-1){
						continue;
					}
					str+=item.getItemid()+",";
					item.setVarible(1);
					fieldlist.add(item);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return fieldlist;
		}
		
		private ArrayList itemList(String tableid){
			ArrayList itemlist = new ArrayList();
			if(tableid.length()>0){
				try {
					String stritem="";
					TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
					ArrayList list = changebo.getAllFieldItem();
					HashMap map = changebo.getSub_domain_map();
					HashMap field_name_map = changebo.getField_name_map();
					for(int i=0;i<list.size();i++){
						FieldItem fielditem = (FieldItem)list.get(i);
						if(fielditem.isChangeAfter()){
							if(stritem.indexOf(fielditem.getItemid()+"_2")!=-1)
								continue;
							stritem+=fielditem.getItemid()+"_2,";
							String itemdesc =ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
							fielditem.setItemdesc(itemdesc);
							fielditem.setItemid(fielditem.getItemid()+"_2");
						}else if(fielditem.isChangeBefore()){
							//多个变化前加上_id
							String sub_domain_id="";
							if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
							
							sub_domain_id ="_"+(String)map.get(""+i);
							}
							if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1")!=-1)
								continue;
							if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
								continue;
							stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
							if(sub_domain_id!=null&&sub_domain_id.length()>0){
								fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
								fielditem.setItemdesc(""+map.get(""+i+"hz"));
							}else{
								fielditem.setItemid(fielditem.getItemid()+"_1");	
							}
							String itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
							fielditem.setItemdesc(itemdesc);
						}
						itemlist.add(fielditem);
					}
				} catch (GeneralException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			return itemlist;
		}
		
		/**
		 * 对于临时变量做特殊处理
		 * @Title: functionListVar   
		 * @Description:    
		 * @param @param keyid
		 * @param @param vtemptype：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他,作为临时变量特殊处理
		 * @param @param fieldsetid
		 * @param @param type
		 * @param @return
		 * @param @throws GeneralException 
		 * @return ArrayList    
		 * @throws
		 * @author sunjian
		 */
		private ArrayList functionListVar(String keyid, String vtemptype, String fieldsetid, String type) throws GeneralException{
			ArrayList list = new ArrayList();
			try {
				HashMap map = new HashMap();
//				map.put("id", "");
//				map.put("name", "");
				ArrayList alist=new ArrayList();
				//alist.add(0,map);
				ArrayList dlist = new ArrayList();
				//dlist.add(0,map);
				ArrayList nlist = new ArrayList();
				//nlist.add(0,map);
				ArrayList itemlist = new ArrayList();
				//itemlist.add(0,map);
				ArrayList vlist = new ArrayList();
				//vlist.add(0,map);
				ArrayList ilist = new ArrayList();
				//ilist.add(0,map);
				ArrayList listitem = new ArrayList();
				if("vartemp".equalsIgnoreCase(fieldsetid)) {
					if("1".equalsIgnoreCase(vtemptype)) {
						listitem=getMidVariableList(keyid);  
					}else if("3".equalsIgnoreCase(vtemptype)) {
						TempvarBo tempvar = new TempvarBo();
						listitem = tempvar.getMidVariableList(this.frameconn,keyid);
					}
				}else 
					listitem= DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
				if(listitem!=null){
					for(int j=0;j<listitem.size();j++){
						map = new HashMap();
						FieldItem item = (FieldItem)listitem.get(j);
						if(!"Q03".equalsIgnoreCase(fieldsetid))//考勤不走子集指标权限管理
						{
							if(this.userView.analyseFieldPriv(item.getItemid())==null){
								continue;
							}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
								continue;
						}
						
						if("A".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.isCode()){
								itemlist.add(map);
							}
							alist.add(map);
							vlist.add(map);
						}else if("N".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.getDecimalwidth()==0){
								ilist.add(map);
							}
							nlist.add(map);
							vlist.add(map);
						}else if("D".equalsIgnoreCase(item.getItemtype())){
							map.put("id", item.getItemid()+":"+item.getItemdesc());
							map.put("name", "("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							dlist.add(map);
							vlist.add(map);
						}
					}
				}
				if("A".equals(type)){
					list = alist;
				}else if("D".equals(type)){
					list = dlist;
				}else if("N".equals(type)){
					list = nlist;
				}else if("V".equals(type)){
					list = vlist;
				}else if("item".equals(type)){
					list = itemlist;
				}else if("I".equals(type)){
					list = ilist;
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return list;
		}

	/**
	 * 我的个税gz_tax_mx 指标
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList listGzTaxMxField(String type) throws GeneralException {
		ArrayList gzItems = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if (StringUtils.equalsIgnoreCase("N", type)) {
			gzItems.add(JSONObject.fromObject("{id:\"A00Z1:" + ResourceFactory.getProperty("gz.columns.a00z1") + "\",name:\"(A00Z1)-" + ResourceFactory.getProperty("gz.columns.a00z1") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"A00Z3:" + ResourceFactory.getProperty("label.gz.count") + "\",name:\"(A00Z3)-" + ResourceFactory.getProperty("label.gz.count") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"A0000:" + ResourceFactory.getProperty("a0000.label") + "\",name:\"(A0000)-" + ResourceFactory.getProperty("a0000.label") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"YNSSDE:" + ResourceFactory.getProperty("mytax.field.ynssde") + "\",name:\"(YNSSDE)-" + ResourceFactory.getProperty("mytax.field.ynssde") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"LJSDE:" + ResourceFactory.getProperty("mytax.field.ljsde") + "\",name:\"(LJSDE)-" + ResourceFactory.getProperty("mytax.field.ljsde") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"LJSE:" + ResourceFactory.getProperty("mytax.field.ljse") + "\",name:\"(LJSE)-" + ResourceFactory.getProperty("mytax.field.ljse") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"LJ_BASEDATA:" + ResourceFactory.getProperty("mytax.field.lj_basedata") + "\",name:\"(LJ_BASEDATA)-" + ResourceFactory.getProperty("mytax.field.lj_basedata") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"ZNJY:" + ResourceFactory.getProperty("mytax.field.znjy") + "\",name:\"(ZNJY)-" + ResourceFactory.getProperty("mytax.field.znjy") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"SYLR:" + ResourceFactory.getProperty("mytax.field.sylr") + "\",name:\"(SYLR)-" + ResourceFactory.getProperty("mytax.field.sylr") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"ZFDKLX:" + ResourceFactory.getProperty("mytax.field.zfdklx") + "\",name:\"(ZFDKLX)-" + ResourceFactory.getProperty("mytax.field.zfdklx") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"ZFZJ:" + ResourceFactory.getProperty("mytax.field.zfzj") + "\",name:\"(ZFZJ)-" + ResourceFactory.getProperty("mytax.field.zfzj") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"JXJY:" + ResourceFactory.getProperty("mytax.field.jxjy") + "\",name:\"(JXJY)-" + ResourceFactory.getProperty("mytax.field.jxjy") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"SSKCS:" + ResourceFactory.getProperty("gz.columns.sskcs") + "\",name:\"(SSKCS)-" + ResourceFactory.getProperty("gz.columns.sskcs") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"BASEDATA:" + ResourceFactory.getProperty("gz.columns.basedata") + "\",name:\"(BASEDATA)-" + ResourceFactory.getProperty("gz.columns.basedata") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"SL:" + ResourceFactory.getProperty("gz.columns.sl") + "\",name:\"(SL)-" + ResourceFactory.getProperty("gz.columns.sl") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"SDS:" + ResourceFactory.getProperty("gz.self.tax.sds") + "\",name:\"(SDS)-" + ResourceFactory.getProperty("gz.self.tax.sds") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"ZFDKLX:" + ResourceFactory.getProperty("mytax.field.zfdklx") + "\",name:\"(ZFDKLX)-" + ResourceFactory.getProperty("mytax.field.zfdklx") + "\"}"));

		} else if (StringUtils.equalsIgnoreCase("A", type)) {
			gzItems.add(JSONObject.fromObject("{id:\"NBASE:" + ResourceFactory.getProperty("gz_new.gz_nbase") + "\",name:\"(NBASE)-" + ResourceFactory.getProperty("gz_new.gz_nbase") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"A0100:" + ResourceFactory.getProperty("a0100.label") + "\",name:\"(A0100)-" + ResourceFactory.getProperty("a0100.label") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"A0101:" + ResourceFactory.getProperty("a0100.label") + "\",name:\"(A0101)-" + ResourceFactory.getProperty("kq.emp.change.emp.a0101") + "\"}"));

		} else if (StringUtils.equalsIgnoreCase("D", type)) {
			gzItems.add(JSONObject.fromObject("{id:\"A00Z0:" + ResourceFactory.getProperty("gz.columns.a00z0") + "\",name:\"(A00Z0)-" + ResourceFactory.getProperty("gz.columns.a00z0") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"A00Z2:" + ResourceFactory.getProperty("gz_new.gz_accounting.send_time") + "\",name:\"(A00Z2)-" + ResourceFactory.getProperty("gz_new.gz_accounting.send_time") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"TAX_DATE:" + ResourceFactory.getProperty("gz.self.tax.taxdate") + "\",name:\"(TAX_DATE)-" + ResourceFactory.getProperty("gz.self.tax.taxdate") + "\"}"));

		} else if (StringUtils.equalsIgnoreCase("item", type)) {
			gzItems.add(JSONObject.fromObject("{id:\"B0100:" + ResourceFactory.getProperty("kq.emp.change.b0110") + "\",name:\"(B0100)-" + ResourceFactory.getProperty("kq.emp.change.b0110") + "\"}"));
			gzItems.add(JSONObject.fromObject("{id:\"E0122:" + ResourceFactory.getProperty("kq.emp.change.e0122") + "\",name:\"(E0122)-" + ResourceFactory.getProperty("kq.emp.change.e0122") + "\"}"));
		}
		//gzItems.add(JSONObject.fromObject("{itemid:\"TaxMode\",itemName:\"" + ResourceFactory.getProperty("gz.columns.taxmode") + "\"}"));
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(), "GZ_TAX_MX");
		Element element = constantXml.getElement("/param/items");
		String fieldItem = element == null ? "" : element.getText();
		if (StringUtils.isNotEmpty(fieldItem)) {
			String[] fieldItemArray = fieldItem.split(",");
			for (String tempField : fieldItemArray) {
				FieldItem field = DataDictionary.getFieldItem(tempField);
				if (field != null) {
					String fieldName = field.getItemdesc();
					Map tempMap = new HashMap();
					tempMap.put("id", tempField.toUpperCase() + ":" + fieldName);
					tempMap.put("name", "(" + tempField.toUpperCase() + ")-" + fieldName);
					if(StringUtils.equalsIgnoreCase(field.getItemtype(),type)){
						gzItems.add(tempMap);
					}else if(StringUtils.equalsIgnoreCase(type,"item")&& "A".equals(field.getItemtype())&&!"0".equals(field.getCodesetid())){
						gzItems.add(tempMap);
					}

				}
			}
		}

		return gzItems;
	}
}
