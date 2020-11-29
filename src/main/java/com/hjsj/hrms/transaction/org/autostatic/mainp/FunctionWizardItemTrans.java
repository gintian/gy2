package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class FunctionWizardItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
			String salaryid = (String)this.getFormHM().get("salaryid");
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
			String tableid = (String)this.getFormHM().get("tabid");
			tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
			
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");
			fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
			
			String functionid = (String)this.getFormHM().get("functionid");
			functionid=functionid!=null&&functionid.trim().length()>0?functionid:"";
			
			String checktemp = (String)this.getFormHM().get("checktemp");
			checktemp=checktemp!=null&&checktemp.trim().length()>0?checktemp:"";
			
			String flag = (String)this.getFormHM().get("flag");
			flag=flag!=null&&flag.trim().length()>0?flag:"";
			String mode = (String)this.getFormHM().get("mode");
			mode=mode!=null&&mode.trim().length()>0?mode:"";
			this.getFormHM().put("flag",flag);
			if("kqrule".equalsIgnoreCase(mode)){//如果是从考勤过来的
				getFieldItemByKq();
			}else{
				if(!"1".equals(flag)){
					if("salary".equalsIgnoreCase(checktemp)){
						if(tableid!=null&&tableid.trim().length()>0)
							changeFunctionList(tableid,fieldsetid,functionid);
						else if(salaryid!=null&&salaryid.trim().length()>0)
							functionList(salaryid,fieldsetid,functionid);
						else
							functionList(fieldsetid);
					}else if("temp".equalsIgnoreCase(checktemp)){
						if(fieldsetid!=null&& "vartemp".equalsIgnoreCase(fieldsetid)){
							if(tableid!=null&&tableid.trim().length()>0)
								changeFunctionList(tableid,fieldsetid,functionid);
							if(salaryid!=null&&salaryid.trim().length()>0)
								functionList(salaryid,fieldsetid,functionid);
						}else
							functionListtemp(fieldsetid);
					}else{
						if(salaryid.trim().length()>0){
							functionList(salaryid,fieldsetid,functionid);
						}else{
							functionList(fieldsetid);
						}
					}
					}else{
						functionList(fieldsetid);
					}
			}
			
		}
	/*
	 * 从考勤模块进入的时候，查询指标项
	 * */
	public void getFieldItemByKq(){
		CommonData obj1=new CommonData("","");
		ArrayList alist=new ArrayList();
		alist.add(0,obj1);
		ArrayList dlist = new ArrayList();
		dlist.add(0,obj1);
		ArrayList nlist = new ArrayList();
		nlist.add(0,obj1);
		ArrayList itemlist = new ArrayList();
		itemlist.add(0,obj1);
		ArrayList vlist = new ArrayList();
		vlist.add(0,obj1);
		ArrayList ilist = new ArrayList();
		ilist.add(0,obj1);
		
		FieldSet fieldset = DataDictionary.getFieldSetVo("Q03");
		ArrayList fielditemlist = fieldset.getFieldItemList(Constant.USED_FIELD_SET);
		int n = fielditemlist.size();
		for(int i=0;i<n;i++){
			FieldItem item = (FieldItem)fielditemlist.get(i);
			if(this.userView.analyseFieldPriv(item.getItemid())==null){
				continue;
			}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
				continue;
			if("A".equalsIgnoreCase(item.getItemtype())){
				CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
							"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
				if(item.isCode()){
					itemlist.add(obj);
				}
				alist.add(obj);
				vlist.add(obj);
			}else if("N".equalsIgnoreCase(item.getItemtype())){
				CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
								"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
				if(item.getDecimalwidth()==0){
					ilist.add(obj);
				}
				nlist.add(obj);
				vlist.add(obj);
			}else if("D".equalsIgnoreCase(item.getItemtype())){
				CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
								"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
				dlist.add(obj);
				vlist.add(obj);
			}
		}
		this.getFormHM().put("alist",alist);
		this.getFormHM().put("dlist",dlist);
		this.getFormHM().put("nlist",nlist);
		this.getFormHM().put("vlist",vlist);
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("ilist",ilist);
	}
	 /**
	     * 查询薪资项目子集
	     * @param dao
	     * @param salaryid 薪资id
	     * @return retlist
	     * @throws GeneralException
	     */
		private void functionList(String salaryid,String fieldsetid,String functionid){
			CommonData obj1=new CommonData("","");
			ArrayList alist=new ArrayList();
			alist.add(0,obj1);
			ArrayList dlist = new ArrayList();
			dlist.add(0,obj1);
			ArrayList nlist = new ArrayList();
			nlist.add(0,obj1);
			ArrayList itemlist = new ArrayList();
			itemlist.add(0,obj1);
			ArrayList vlist = new ArrayList();
			vlist.add(0,obj1);
			ArrayList ilist = new ArrayList();
			ilist.add(0,obj1);

			ArrayList listitem= new ArrayList();
			if("vartemp".equalsIgnoreCase(fieldsetid)){
				listitem=getMidVariableList(salaryid);  
			}else{
				listitem=getMidVariableList(salaryid,fieldsetid);  
			}
			if(listitem!=null){
				for(int j=0;j<listitem.size();j++){
					FieldItem item = (FieldItem)listitem.get(j);
					if("A".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.isCode()){
							itemlist.add(obj);
						}
						alist.add(obj);
						vlist.add(obj);
					}else if("N".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.getDecimalwidth()==0){
							ilist.add(obj);
						}
						nlist.add(obj);
						vlist.add(obj);
					}else if("D".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						dlist.add(obj);
						if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
							continue;
						}
						vlist.add(obj);
					}
				}
			}
			
			this.getFormHM().put("alist",alist);
			this.getFormHM().put("dlist",dlist);
			this.getFormHM().put("nlist",nlist);
			this.getFormHM().put("vlist",vlist);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("ilist",ilist);

		}
		 /**
	     * 查询子集
	     * @param dao
	     * @return retlist
	     * @throws GeneralException
	     */
		private void functionList(String fieldsetid){
			CommonData obj1=new CommonData("","");
			ArrayList alist=new ArrayList();
			alist.add(0,obj1);
			ArrayList dlist = new ArrayList();
			dlist.add(0,obj1);
			ArrayList nlist = new ArrayList();
			nlist.add(0,obj1);
			ArrayList itemlist = new ArrayList();
			itemlist.add(0,obj1);
			ArrayList vlist = new ArrayList();
			vlist.add(0,obj1);
			ArrayList ilist = new ArrayList();
			ilist.add(0,obj1);
			
			ArrayList listitem= DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
			if(listitem!=null){
				for(int j=0;j<listitem.size();j++){
					FieldItem item = (FieldItem)listitem.get(j);
					if(!"Q03".equalsIgnoreCase(fieldsetid))//考勤不走子集指标权限管理
					{
						if(this.userView.analyseFieldPriv(item.getItemid())==null){
							continue;
						}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
							continue;
					}
					
					if("A".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.isCode()){
							itemlist.add(obj);
						}
						alist.add(obj);
						vlist.add(obj);
					}else if("N".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.getDecimalwidth()==0){
							ilist.add(obj);
						}
						nlist.add(obj);
						vlist.add(obj);
					}else if("D".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						dlist.add(obj);
						vlist.add(obj);
					}
				}
			}
			this.getFormHM().put("alist",alist);
			this.getFormHM().put("dlist",dlist);
			this.getFormHM().put("nlist",nlist);
			this.getFormHM().put("vlist",vlist);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("ilist",ilist);
		}
		 /**
	     * 查询子集
	     * @param dao
	     * @return retlist
	     * @throws GeneralException
	     */
		private void functionListtemp(String fieldsetid){
			CommonData obj1=new CommonData("","");
			ArrayList alist=new ArrayList();
			alist.add(0,obj1);
			ArrayList dlist = new ArrayList();
			dlist.add(0,obj1);
			ArrayList nlist = new ArrayList();
			nlist.add(0,obj1);
			ArrayList itemlist = new ArrayList();
			itemlist.add(0,obj1);
			ArrayList vlist = new ArrayList();
			vlist.add(0,obj1);
			ArrayList ilist = new ArrayList();
			ilist.add(0,obj1);
			
			ArrayList listitem= new ArrayList();
	
			listitem=DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
			if(listitem!=null){
				for(int j=0;j<listitem.size();j++){
					FieldItem item = (FieldItem)listitem.get(j);
					if(this.userView.analyseFieldPriv(item.getItemid())==null){
						continue;
					}else if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
						continue;
					
					if("A".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.isCode()){
							itemlist.add(obj);
						}
						alist.add(obj);
						vlist.add(obj);
					}else if("N".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						if(item.getDecimalwidth()==0){
							ilist.add(obj);
						}
						nlist.add(obj);
						vlist.add(obj);
					}else if("D".equalsIgnoreCase(item.getItemtype())){
						CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
										"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
						dlist.add(obj);
						vlist.add(obj);
					}
				}
			}
			this.getFormHM().put("alist",alist);
			this.getFormHM().put("dlist",dlist);
			this.getFormHM().put("nlist",nlist);
			this.getFormHM().put("vlist",vlist);
			this.getFormHM().put("itemlist",itemlist);
			this.getFormHM().put("ilist",ilist);
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
//			ArrayList fieldlist=sysFieldList();
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
//					String str = "";
//					for(int i=0;i<fieldlist.size();i++){
//						FieldItem item= (FieldItem)fieldlist.get(i);
//						if(item!=null&&item.getItemid()!=null&&item.getItemid().trim().length()>0){
//							str+=item.getItemid()+",";
//						}
//					}
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
//						if(str.indexOf(item.getItemid())!=-1){
//							continue;
//						}
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
				// TODO Auto-generated catch block
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
					//		if(!fielditem.getFieldsetid().equalsIgnoreCase("A01")){
								String itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
								fielditem.setItemdesc(itemdesc);
					//		}
							
						}
						itemlist.add(fielditem);
					}
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return itemlist;
		}
		 /**
	     * 查询薪资变化子集
	     * @param itemtype 子集数据类型
	     * @param itemlist 子集集合
	     * @return retlist
	     */
		public void changeFunctionList(String tableid,String fieldsetid,String functionid){
			
			CommonData obj1=new CommonData("","");
			ArrayList alist=new ArrayList();
			alist.add(0,obj1);
			ArrayList dlist = new ArrayList();
			dlist.add(0,obj1);
			ArrayList nlist = new ArrayList();
			nlist.add(0,obj1);
			ArrayList codelist = new ArrayList();
			codelist.add(0,obj1);
			ArrayList vlist = new ArrayList();
			vlist.add(0,obj1);
			ArrayList ilist = new ArrayList();
			ilist.add(0,obj1);
			
			if("vartemp".equalsIgnoreCase(fieldsetid)){
				TempvarBo tempvar = new TempvarBo();
				ArrayList itemlist = tempvar.getMidVariableList(this.frameconn,tableid);
				for(int i=0;i<itemlist.size();i++){
					FieldItem item = (FieldItem)itemlist.get(i);
					if(item!=null){
						if("A".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									item.getItemdesc());
							if(item.isCode()){
								codelist.add(obj);
							}
							alist.add(obj);
							vlist.add(obj);
						}else if("N".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									item.getItemdesc());
							if(item.getDecimalwidth()==0){
								ilist.add(obj);
							}
							nlist.add(obj);
							vlist.add(obj);
						}else if("D".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),
									item.getItemdesc());
							dlist.add(obj);
							if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
								continue;
							}
							vlist.add(obj);
						}
					}
				}
			}else{
				ArrayList itemlist = itemList(tableid);
				for(int i=0;i<itemlist.size();i++){
					FieldItem item = (FieldItem)itemlist.get(i);
					if(item!=null&&item.getFieldsetid().equalsIgnoreCase(fieldsetid)){
						String itemid = item.getItemid();
						itemid=itemid.indexOf("_")!=-1?itemid.substring(0,itemid.indexOf("_")):itemid;
						if("A".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(itemid+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.isCode()){
								codelist.add(obj);
							}
							alist.add(obj);
							vlist.add(obj);
						}else if("N".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(itemid+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							if(item.getDecimalwidth()==0){
								ilist.add(obj);
							}
							nlist.add(obj);
							vlist.add(obj);
						}else if("D".equalsIgnoreCase(item.getItemtype())){
							CommonData obj=new CommonData(itemid+":"+item.getItemdesc(),
									"("+item.getItemid().toUpperCase()+")-"+item.getItemdesc());
							dlist.add(obj);
							if("A_vol9_6_2_10_2".equalsIgnoreCase(functionid)){
								continue;
							}
							vlist.add(obj);
						}
					}
				}
			}
			this.getFormHM().put("alist",alist);
			this.getFormHM().put("dlist",dlist);
			this.getFormHM().put("nlist",nlist);
			this.getFormHM().put("vlist",vlist);
			this.getFormHM().put("itemlist",codelist);
			this.getFormHM().put("ilist",ilist);
		}
}
