package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class FunctionWizardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");
		
		String busi = (String)reqhm.get("busi");
		busi=busi!=null&&busi.trim().length()>0?busi.toUpperCase():"";
		reqhm.remove("busi");
		
		String tableid = (String)reqhm.get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		reqhm.remove("tableid");
		
		String checktemp = (String)reqhm.get("checktemp");
		checktemp=checktemp!=null&&checktemp.trim().length()>0?checktemp:"";
		reqhm.remove("checktemp");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"1";
		reqhm.remove("infor");
		String setid = (String)reqhm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		reqhm.remove("setid");
		String fmode = (String)reqhm.get("fmode");
		fmode=fmode!=null&&fmode.trim().length()>0?fmode:"";
		reqhm.remove("fmode");
		String mode = (String)reqhm.get("mode");
		mode=mode!=null&&mode.trim().length()>0?mode:"";
		reqhm.remove("mode");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ProjectSet projectset = new ProjectSet();
		ArrayList standlist = projectset.standList(dao,this.userView);
		ArrayList standidlist = projectset.standidList(dao,this.userView);
		ArrayList belong_to_unit_list = projectset.belongtounitList(dao,this.userView);
		//得到不同情况得到fieldsetlist
		if("temp".equalsIgnoreCase(checktemp)){
			functionList();
		}else{
			if(busi.trim().length()>0){
				FieldSet fieldset = DataDictionary.getFieldSetVo(busi);
				ArrayList fieldsetlist = new ArrayList();
				CommonData obj=new CommonData("","");
				fieldsetlist.add(obj);
				obj=new CommonData(busi
						,busi+"-"+fieldset.getCustomdesc());

				fieldsetlist.add(obj);
				this.getFormHM().put("fieldsetlist",fieldsetlist);
			}else if(salaryid.trim().length()>0||tableid.trim().length()>0){
					functionList(dao,salaryid,tableid);
			}else{
				if("6".equals(infor)){
					String itemid = (String)reqhm.get("itemid"); 
					itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
					reqhm.remove("tableid");
					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
					FieldSet fieldset = DataDictionary.getFieldSetVo(fielditem.getFieldsetid());
					ArrayList fieldsetlist = new ArrayList();
					CommonData obj=new CommonData("","");
					fieldsetlist.add(obj);
					obj=new CommonData(fielditem.getFieldsetid()
							,fielditem.getFieldsetid()+"-"+fieldset.getCustomdesc());

					fieldsetlist.add(obj);
					this.getFormHM().put("fieldsetlist",fieldsetlist);
				}else{
					if(!"".equals(setid)){
						functionList(dao,infor,fmode,setid);
					}else{
						if("jixiao_aoto".equalsIgnoreCase(mode)){
							functionList(dao,"8");
						}
						else if("jixiao_aoto_1".equalsIgnoreCase(mode)){
							functionList(dao,"1");
						}else if("jixiao_aoto_2".equalsIgnoreCase(mode)){
							functionList(dao,"2");
						}else if("kqrule".equalsIgnoreCase(mode)){//如果是从考勤模块进入的
							functionListByKq();
						}else if("trainrul".equalsIgnoreCase(mode)){//从培训班进入的
							functionListByTrain();
						}else {
							functionList(dao,infor);
						}
					}
				}
			}
		}
		functionListunit();
		functionListpos();
		this.getFormHM().put("checktemp",checktemp);
		this.getFormHM().put("standlist",standlist);
		this.getFormHM().put("standidlist",standidlist);
		this.getFormHM().put("salaryid",salaryid);
		this.getFormHM().put("tabid",tableid);
		this.getFormHM().put("statlist",projectset.condList());
		this.getFormHM().put("rangelist",projectset.rangeList());
		this.getFormHM().put("belong_to_unit_list", belong_to_unit_list);
	}
	private void functionListunit(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistunit = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistunit.add(obj1);
			listset = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistunit.add(obj);
			}
			this.getFormHM().put("fieldsetlistunit",fieldsetlistunit);
	}
	private void functionListpos(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistpos = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistpos.add(obj1);
		listset = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistpos.add(obj);
			}
			this.getFormHM().put("fieldsetlistpos",fieldsetlistpos);
	}
	 /**
     * 查询薪资项目子集
     * @param dao
     * @param salaryid 薪资id
     * @return retlist
     * @throws GeneralException
     */
	private void functionList(ContentDAO dao,String salaryid,String tabid){
		 ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		ArrayList listitem= getMidVariableList(salaryid);
		HashSet   hs   =   new   HashSet();   

		if(listitem!=null){
			for(int j=0;j<listitem.size();j++){
				FieldItem item = (FieldItem)listitem.get(j);
				hs.add(item.getFieldsetid());
			}
		}
		String[] arr = (String[])hs.toArray(new String[0]);   
		Arrays.sort(arr);
		for(int j=0;j<arr.length;j++){
			String fieldsetid = arr[j];
			FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
			if(fieldset!=null){
				if(fieldset==null)
					 continue;
				 if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				CommonData obj=new CommonData(fieldset.getFieldsetid()
						 ,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				fieldsetlist.add(obj);
			}	 
		}
		CommonData obj=new CommonData("vartemp",ResourceFactory.getProperty("menu.gz.variable"));
		fieldsetlist.add(obj);
		this.getFormHM().put("fieldsetlist",fieldsetlist);
	}
	 /**
     * 查询子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	private void functionList(ContentDAO dao,String infor){
		 ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		
		ArrayList listset = new ArrayList();
		if("1".equals(infor))
			listset = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("2".equals(infor))
			listset = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		else if("3".equals(infor))
			listset = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
		else if("4".equals(infor))
			listset = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		else if("5".equals(infor)){
			FieldSet fieldset= DataDictionary.getFieldSetVo("r45");
			fieldset.setUseflag("1");
			listset.add(fieldset);
		}else if("7".equals(infor)){
			listset = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			listset.addAll(this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET));
			listset.addAll(this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET));
		}else if("8".equals(infor)){
			listset = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			listset.addAll(this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET));
		}
		
		for(int i=0;i<listset.size();i++){
			 FieldSet fieldset = (FieldSet)listset.get(i);
			 if(fieldset==null)
				 continue;
			 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }
			 CommonData obj=new CommonData(fieldset.getFieldsetid()
						,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
			 fieldsetlist.add(obj);
		}
		this.getFormHM().put("fieldsetlist",fieldsetlist);
	
	}
	/*
	 * 如果是从考勤模块进入的函数向导，子集要用另外的查找方式
	 * */
	public void functionListByKq(){
		FieldSet fieldset = DataDictionary.getFieldSetVo("Q03");
		String name = fieldset.getFieldsetdesc();
		CommonData obj= new CommonData("","");
		ArrayList fieldsetlist = new ArrayList();
		fieldsetlist.add(obj);
	 	fieldsetlist.add(new CommonData("Q03","Q03"+"-"+name));
	 	this.getFormHM().put("fieldsetlist",fieldsetlist);
	}
	/**
	 * 培训班进入函数向导，子集要用另外的查找方法
	 */
	public void functionListByTrain(){
		FieldSet fieldset = DataDictionary.getFieldSetVo("R31");
		String name = fieldset.getFieldsetdesc();
		CommonData obj= new CommonData("","");
		ArrayList fieldsetlist = new ArrayList();
		fieldsetlist.add(obj);
	 	fieldsetlist.add(new CommonData("R31","R31"+"-"+name));
	 	this.getFormHM().put("fieldsetlist",fieldsetlist);
	}
	
	private void functionList(ContentDAO dao,String infor,String fmode,String setid){
		 ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		if("2".equals(fmode)){
			ArrayList list=new ArrayList();
			try
			{
				//list.add(new CommonData("",""));
			//	ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select fieldsetid,customdesc from fieldset where useflag='1' order by fieldsetid ");
				while(this.frowset.next())
				{
					//list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
					 CommonData obj=new CommonData(this.frowset.getString(1)
								,this.frowset.getString(1)+"-"+this.frowset.getString(2));
					 fieldsetlist.add(obj);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}else{
		ArrayList listset = new ArrayList();
		
			listset = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		
		
		for(int i=0;i<listset.size();i++){
			 FieldSet fieldset = (FieldSet)listset.get(i);
//			 if(fieldset==null)
//				 continue;
			 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B01".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }
			 if("0".equals(fmode)){
				 if(!fieldset.getFieldsetid().equalsIgnoreCase(setid))
					 continue;
			 }else if("1".equals(fmode)){
				 
			 }
			 
			 CommonData obj=new CommonData(fieldset.getFieldsetid()
						,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
			 fieldsetlist.add(obj);
		}
		}
		this.getFormHM().put("fieldsetlist",fieldsetlist);
	
	}
	 /**
     * 查询子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	private void functionList(){
		getTempValue();
		 ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		
		ArrayList listset = new ArrayList();
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET));
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET));
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET));
			
		for(int i=0;i<listset.size();i++){
			 FieldSet fieldset = (FieldSet)listset.get(i);
			 if(fieldset==null)
				 continue;
			 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }
			 if(this.userView.analyseTablePriv(fieldset.getFieldsetid())==null)
				 continue;
			 if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
				 continue;
			 
			 CommonData obj=new CommonData(fieldset.getFieldsetid()
						,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
			 fieldsetlist.add(obj);
		}
		CommonData obj=new CommonData("vartemp",ResourceFactory.getProperty("menu.gz.variable"));
		fieldsetlist.add(obj);
		this.getFormHM().put("fieldsetlist",fieldsetlist);
	}
	 /**
     * 字符转数字
     * @param itemtype 子集数据类型
     * @return retlist
     */
	private String strTonum(String itemtype){
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
	private ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer buf = new StringBuffer();
			if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				String fieldsetid = (String) this.userView.getHm().get("fieldsetid");
				String sqlstr="select * from fielditem ";
				if(fieldsetid!=null&&fieldsetid.trim().length()>0){
					sqlstr+=" where fieldsetid='"+fieldsetid+"' and useflag = '1'";
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
			}else{
				buf.append("select * from salaryset");
				if(salaryid!=null&&!"all".equalsIgnoreCase(salaryid))
					buf.append(" where salaryid='"+salaryid+"'");
				RowSet rset=dao.search(buf.toString());
				while(rset.next()){
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("ITEMID"));
					item.setItemdesc(rset.getString("ITEMDESC"));
					item.setFieldsetid(rset.getString("FIELDSETID"));
					item.setItemlength(rset.getInt("ITEMLENGTH"));
					item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
					item.setDecimalwidth(rset.getInt("DECWIDTH"));
					item.setItemtype(rset.getString("ITEMTYPE"));
					item.setVarible(1);
					fieldlist.add(item);
				}
				rset.close();
			}

			buf.setLength(0);
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,cstate from ");
			if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				buf.append(" midvariable where nflag=5 and templetid=0 ");
				String fieldsetid = (String) this.userView.getHm().get("fieldsetid");
				if(fieldsetid!=null&&fieldsetid.length()>0){
					buf.append(" and (cstate is null or cstate='");
					buf.append(fieldsetid);
					buf.append("')");
				}
			}else if("-1".equals(salaryid)){
				buf.append(" midvariable where nflag=4 and templetid=0 ");
				buf.append(" and cstate='");
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

			
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				if("-2".equals(salaryid)){ //薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
					String fieldsetid = (String) this.userView.getHm().get("fieldsetid");
					item.setFieldsetid(fieldsetid);//没有实际含义
				}else if("-1".equals(salaryid)){
					GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
					HashMap map =bo.getValuesMap();
					String fieldsetid = ((String)map.get("setid")).length()>0?(String)map.get("setid"):"A01";
					item.setFieldsetid(fieldsetid);
				}else{
					item.setFieldsetid("A01");//没有实际含义
				}
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
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
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	/**
	 * 单独取出所有的临时变量
	 * @Title: getTempValue   
	 * @Description:    
	 * @param  
	 * @return void    
	 * @throws
	 * @author sunjian
	 */
	private void getTempValue() {
		 ArrayList tempfieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 tempfieldsetlist.add(obj1);
		ArrayList listitem= getMidVariableList("-1");
		HashSet   hs   =   new   HashSet();   

		if(listitem!=null){
			for(int j=0;j<listitem.size();j++){
				FieldItem item = (FieldItem)listitem.get(j);
				hs.add(item.getFieldsetid());
			}
		}
		String[] arr = (String[])hs.toArray(new String[0]);   
		Arrays.sort(arr);
		for(int j=0;j<arr.length;j++){
			String fieldsetid = arr[j];
			FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
			if(fieldset!=null){
				if(fieldset==null)
					 continue;
				 if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				CommonData obj=new CommonData(fieldset.getFieldsetid()
						 ,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				tempfieldsetlist.add(obj);
			}	 
		}
		CommonData obj=new CommonData("vartemp",ResourceFactory.getProperty("menu.gz.variable"));
		tempfieldsetlist.add(obj);
		this.getFormHM().put("tempfieldsetlist",tempfieldsetlist);
	}
}
