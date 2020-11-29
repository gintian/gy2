package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.constant.SystemConfig;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;


/**
 * 单位 部门 职位 用户组 权限分析
 * @author Owner
 */
public class OrganizationPopedomAnalyse implements IResourceConstant {

	private Connection conn;
	private EncryptLockClient lock;
	private String role_id;
	private String role_flag;
	
	private ArrayList pbList = new ArrayList(); //单位 部门 职位 对应角色集合
	private PrivBean pb;
	
	private ArrayList setList = new ArrayList(); //授权的所有指标集  集合
	
	//指标集
	private ArrayList setReadList = new ArrayList(); //授权为读的指标集 集合
	private ArrayList setWriteList = new ArrayList();//授权为写的指标集 集合
	
	//指标项
	private ArrayList fieldReadList = new ArrayList(); //授权为读的指标 集合
	private ArrayList fieldWriteList = new ArrayList();//授权为写的指标 集合
	
	
	/**
	 * 
	 * @param conn      数据库连接
	 * @param role_id   组织ID
	 * @param role_flag 类别( 1 角色 0 用户组 2 单位,部门,职位)    
	 */
	public OrganizationPopedomAnalyse(Connection conn , String role_id , String role_flag){
		this.conn = conn;
		this.role_id = role_id;
		this.role_flag = role_flag;
		this.initDB();
	}
	
	
	/**
	 * 初始化权限对象
	 */
	public void initDB(){
		String sql ="";
		if("1".equals(role_flag)){
			this.pb = createPrivBean("1" , this.role_id);	 //角色
		}else if("0".equals(role_flag)){
			this.pb = createPrivBean("0" , this.role_id);	  //用户组
		}else if("2".equals(role_flag)){
			sql="select * from t_sys_staff_in_role where status='2' and staff_id='"+role_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				RowSet rs = dao.search(sql);
				while(rs.next()){
					String role_id = rs.getString("role_id"); //角色
					this.pbList.add(this.createPrivBean("1" , role_id));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.pb = this.createPrivBean(this.pbList);
		}
	}
	
	/**
	 * 单位,部门,职位 对应多个角色情况处理
	 * 权限处理规则
	 * 	功能授权:  求和
	 * 	人员库授权: 求和
	 * 	子集授权: 求和(即可读可写)
	 * 	指标授权: 求和(可读可写)
	 * 	资源授权: 求和
	 *  管理范围  求最短
	 *  高级  	 求和
	 * @param pbList
	 * @return
	 */
	private PrivBean createPrivBean(ArrayList pbList){
		PrivBean pb = new PrivBean();
		pb.setDbPriv(this.getPrivBeanElement(0));
		pb.setFunctionPriv(this.getPrivBeanElement(1));
		pb.setManagePriv(this.getPrivBeanElement(5));	//管理范围
		pb.setFormula(this.getPrivBeanElement(6));		//高级
		pb.setTablePriv(this.getPrivBeanElement(2));
		pb.setFieldPriv(this.getPrivBeanElement(3));
		pb.setResourcePriv(this.getPrivBeanElement(4));
		return pb;
	}
	
	/**
	 * 
	 * @param flag 操作标识 (0 人员库 1 )
	 * @return
	 */
	public String getPrivBeanElement(int flag){
		StringBuffer desc = new StringBuffer();
		ArrayList list = new ArrayList();
		for(int i=0; i< pbList.size(); i++){
			PrivBean tempPb = (PrivBean)pbList.get(i);
			String tt = "";
			switch(flag){
				case 0:
					tt = tempPb.getDbPriv();
					break;
				case 1:
					tt = tempPb.getFunctionPriv();
					break;
				case 2:
					tt = tempPb.getTablePriv();
					break;
				case 3:
					tt = tempPb.getFieldPriv();
					break;
				case 4:
					tt = tempPb.getResourcePriv();
					break;
				case 5:
					tt = tempPb.getManagePriv();
					break;
				case 6:
					tt = tempPb.getFormula();
					break;
			}
			
			
			if(tt == null || "".equals(tt)){
			}else{
				String temp [] = tt.split(",");
				for(int j=0; j<temp.length; j++){
					if(list.contains(temp[j])){
					}else{
						list.add(temp[j]);
					}
				}
			}
		}
		if(flag == 5){
			String te = "";
			int nn = 0;
			for(int j=0; j<list.size(); j++){
				String temp = (String) list.get(j);
				if(temp==null || "".equals(temp.trim())){
					continue;
				}else{
					int n = temp.length();
					if(n>nn){
						nn = n;
					}
				}
			}
			for(int j=0; j<list.size(); j++){
				String temp = (String) list.get(j);
				if(temp==null || "".equals(temp.trim())){
					continue;
				}else{
					int n = temp.length();
					if(n<nn){
						nn = n;
						te = temp;
					}
				}
			}
			desc.append(te);
		}else{
			for(int j=0; j<list.size(); j++){
				String temp = (String) list.get(j);
				desc.append(temp);
				desc.append(",");
			}
		}
		
		
		return desc.toString();
	}
	
	/**
	 * 创建权限对象
	 * @param status    1, 角色
	 * @param role_id   角色ID
	 * @return
	 */
	public PrivBean createPrivBean(String status ,String role_id){
		PrivBean pb = new PrivBean();
		String sql="select * from t_sys_function_priv where status ="+status+" and id='"+role_id+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		//System.out.println(sql);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String funPriv = rs.getString("functionpriv");
				if(funPriv == null){
					funPriv ="";
				}
				pb.setFunctionPriv(funPriv);//功能
				
				String dbPriv = rs.getString("dbpriv");
				if(dbPriv == null){
					dbPriv = "";
				}
				pb.setDbPriv(dbPriv);			//人员库	
				
				String manPriv = rs.getString("managepriv");
				if(manPriv == null){
					manPriv = "";
				}
				pb.setManagePriv(manPriv);	//管理范围
				
				String condPriv = rs.getString("condpriv");
				if(condPriv == null){
					condPriv = "";
				}
				pb.setFormula(condPriv);		//高级
				
				String tablePriv = rs.getString("tablepriv");
				if(tablePriv == null){
					tablePriv = "";
				}
				pb.setTablePriv(tablePriv);
				
				String fieldpriv = rs.getString("fieldpriv");
				if(fieldpriv == null){
					fieldpriv = "";
				}
				pb.setFieldPriv(fieldpriv);
				
				String warnpriv = rs.getString("warnpriv");
				if(warnpriv == null){
					warnpriv = "";
				}
				pb.setResourcePriv(warnpriv);
			}else{
				pb.setFunctionPriv("");//功能
				pb.setDbPriv("");			//人员库	
				pb.setManagePriv("");	//管理范围
				pb.setFormula("");		//高级
				pb.setTablePriv("");
				pb.setFieldPriv("");
				pb.setResourcePriv("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pb;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public UserPopedom execute(){
		UserPopedom up = new UserPopedom();
		up.setDisplayMessage(this.getValue(this.getDisplayMessage()));//描述信息
		up.setDbPres(this.getValue(this.getUserDbPre())); //人员库信息			
		up.setManagerSpace(this.getValue(this.getUserManagerSpace())); //管理范围
		up.setFormula(this.getValue(this.getFormula()));//记录授权高级条件	
		up.setPartymanager(this.getValue(this.getResourcePriv("29")));
		up.setMenbermanager(this.getValue(this.getResourcePriv("30")));
		this.initSetOrFieldPriv(setList ,setReadList , setWriteList 
				,fieldReadList ,fieldWriteList);
		up.setSetOrItemReadPriv(this.getValue(this.getUserSetOrFieldPriv("1"))); //读
		up.setSetOrItemWritePriv(this.getValue(this.getUserSetOrFieldPriv("2"))); //写
		
		//up.setSelfFunctionPriv(this.getValue(this.formatFunctionPrivToHtml("1"))); //自助功能权限
		//up.setOperFunctionPriv(this.getValue(this.formatFunctionPrivToHtml("2"))); //业务功能权限
		up.setFunctionPriv(this.showFucntionPriv());
		up.setCardResourcePriv(this.getValue(this.getResourcePriv("0")));
		up.setReportResourcePriv(this.getValue(this.getResourcePriv("1")));
		up.setLexprResourcePriv(this.getValue(this.getResourcePriv("2")));
		up.setStaticsResourcePriv(this.getValue(this.getResourcePriv("3")));
		up.setMusterResourcePriv(this.getValue(this.getResourcePriv("4")));
		up.setHighMusterResourcePriv(this.getValue(this.getResourcePriv("5")));
		up.setLawruleResourcePriv(this.getValue(this.getResourcePriv("6")));
		up.setRsbdResourcePriv(this.getValue(this.getResourcePriv("7")));
		up.setXzbdResourcePriv(this.getValue(this.getResourcePriv("8")));//薪资类别
		up.setWjdcResourcePriv(this.getValue(this.getResourcePriv("9")));//问卷调查
		up.setPxbResourcePriv(this.getValue(this.getResourcePriv("10")));//培训班
		up.setGglResourcePriv(this.getValue(this.getResourcePriv("11")));//公告栏
		up.setXzlbResourcePriv(this.getValue(this.getResourcePriv("12")));//薪资类别
		up.setGzfxtResourcePriv(this.getValue(this.getResourcePriv("20")));//工资分析图表
		up.setDaflResourcePriv(this.getValue(this.getResourcePriv("14")));//档案分类
		up.setKqjResourcePriv(this.getValue(this.getResourcePriv("15")));//考勤机
		up.setBxbdResourcePriv(this.getValue(this.getResourcePriv("17")));//保险变动
		up.setOrgbdResourcePriv(this.getValue(this.getResourcePriv("31")));
		up.setPosbdResourcePriv(this.getValue(this.getResourcePriv("32")));
		up.setBxlbResourcePriv(this.getValue(this.getResourcePriv("18")));//保险类别
		up.setWdflResourcePriv(this.getValue(this.getResourcePriv("19")));//文档分类
		up.setZsflResourcePriv(this.getValue(this.getResourcePriv("21")));//知识分类
		up.setKhzbResourcePriv(this.getValue(this.getResourcePriv("23")));//考核指标
		up.setKhmbResourcePriv(this.getValue(this.getResourcePriv("22")));//考核模板
		up.setJbbcResourcePriv(this.getValue(this.getResourcePriv("24")));//基本班次
		up.setKqbzResourcePriv(this.getValue(this.getResourcePriv("26")));//考勤班组
		
		/*up.setCardResourcePriv(this.getValue(this.getResourcePriv("0")));
		up.setReportResourcePriv(this.getValue(this.getResourcePriv("1")));
		up.setLexprResourcePriv(this.getValue(this.getResourcePriv("2")));
		up.setStaticsResourcePriv(this.getValue(this.getResourcePriv("3")));
		up.setMusterResourcePriv(this.getValue(this.getResourcePriv("4")));
		up.setHighMusterResourcePriv(this.getValue(this.getResourcePriv("5")));
		up.setLawruleResourcePriv(this.getValue(this.getResourcePriv("6")));
		up.setRsbdResourcePriv(this.getValue(this.getResourcePriv("7")));
		up.setOrgbdResourcePriv(this.getValue(this.getResourcePriv("31")));
		up.setPosbdResourcePriv(this.getValue(this.getResourcePriv("32")));*/
		return up;
	}
	
	public String getValue(String temp){
		if(temp == null || "".equals(temp.trim())){
			return "&nbsp;";
		}
		return temp;
	}
	
	/*******************************表格描述信息********************************/
	public String getDisplayMessage(){
		
		StringBuffer displayMessage = new StringBuffer();
		String sql="";
		// 1 角色 0 用户组 2 单位,部门,职位
		if("0".equals(role_flag)){
			return role_id;
		}else if("1".equals(role_flag)){
			sql = "select role_name info  from t_sys_role where role_id='"+role_id+"'";
		}else if("2".equals(role_flag)){
			sql ="select codeitemdesc info from organization  where codeitemid='"+role_id+"'";
		}
	
		//System.out.println(sql);
		
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String temp  = rs.getString("info");
				displayMessage.append(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return displayMessage.toString();
	}
	
	
	/*************************应用库*******************************/
	public String getUserDbPre(){
		StringBuffer desc = new StringBuffer();
		String dbpriv = this.pb.getDbPriv();
		if(dbpriv == null){
			dbpriv = "";
		}
		String pre[] = dbpriv.split(",");
		for(int i=0; i<pre.length;i++){
			String p = pre[i];
			if(p == null || "".equals(p)){}else{
				String name = this.getUserDbName(p);
				desc.append(name);
				desc.append(",");
			}
		}

		String result = desc.toString();
		if(result == null || "".equals(result.trim())){
			return "";
		}else{
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	
	public String getUserDbName(String pre){
		String name = "";
		String sql="select dbname from dbname where pre='"+pre+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				name = rs.getString("dbname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
/**********************管理范围**********************************/
	
	public String getUserManagerSpace(){
		String managerSpace = "";
		String temp = this.pb.getManagePriv();
		if(temp == null || "".equals(temp)){
			return "";
		}
		
		String managerPrivCode = temp.substring(0,2);
		String managerPrivCodeValue = temp.substring(2,temp.length());
		
		//System.out.println(managerPrivCode + "  " + managerPrivCodeValue);
		
		if((managerPrivCode== null || "".equals(managerPrivCode))&&(managerPrivCodeValue == null || "".equals(managerPrivCodeValue))){
			
		}else if((managerPrivCode!= null || "UN".equals(managerPrivCode))&&(managerPrivCodeValue == null || "".equals(managerPrivCodeValue)|| "`".equals(managerPrivCodeValue))){
			managerSpace=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}else{			
			String sql="select codeitemdesc from organization where codeitemid='"
				+ managerPrivCodeValue+"' and codesetid='"+managerPrivCode+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			try {
				RowSet rs = dao.search(sql);
				if(rs.next()){
					managerSpace = rs.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return managerSpace;
	}
	
	/***************************记录授权高级条件*****************************/
	public String getFormula(){
		String temp = this.pb.getFormula();
		if(temp == null){
			temp="";
		}
		return temp;
	}
	
	
	/***************************子集及指标的读写权限**************************/
	
	/**
	 * 
	 */
	public void initSetOrFieldPriv(ArrayList setList ,ArrayList setReadList ,ArrayList setWriteList 
				,ArrayList fieldReadList ,ArrayList fieldWriteList){
		
		String tablepriv = this.pb.getTablePriv();//记录集权限
		String fieldPriv = this.pb.getFieldPriv();//指标权限
		
		if(tablepriv == null){
			tablepriv="";
		}
		if(fieldPriv == null){
			fieldPriv="";
		}
		
		String [] tp = tablepriv.split(",");//授权指标集数组
		String [] fp = fieldPriv.split(",");//授权指标数组
		
		for(int i=0; i< tp.length; i++){
			String temp = tp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			setList.add(temp.substring(0,temp.length()-1)); //所有指标集(无读写标识)
		}
		
		//子集区分读写好像无意义?
		for(int i=0; i< tp.length; i++){
			String temp = tp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			if(temp.endsWith("1")){ //读权限
				setReadList.add(temp.substring(0,temp.length()-1)); //授权为读的指标集
			}else if(temp.endsWith("2")){ //写权限
				setWriteList.add(temp.substring(0,temp.length()-1));//授权为写的指标集
			}
		}
		
		for(int i=0; i< fp.length; i++){
			String temp = fp[i];
			if(temp == null || "".equals(temp)){
				continue;
			}
			if(temp.endsWith("1")){ //读权限
				fieldReadList.add(temp.substring(0,temp.length()-1)); //授权为读的指标
			}else if(temp.endsWith("2")){ //写权限
				fieldWriteList.add(temp.substring(0,temp.length()-1));//授权为写的指标
			}
		}
	}
	
	
	/**
	 * 
	 * @param flag	子集与指标权限分析(1 读 , 2 写 )
	 * @return
	 */
	public String getUserSetOrFieldPriv(String flag){
		
		StringBuffer userSetOrFieldPriv = new StringBuffer();
		/*
		System.out.println("*****************************");
		System.out.println(setList.size());
		System.out.println(setReadList.size());
		System.out.println(setWriteList.size());
		System.out.println(fieldReadList.size());
		System.out.println(fieldWriteList.size());
		System.out.println("*****************************");
		*/
		
		
		for(int i=0; i<setList.size(); i++){ //遍历授权所有指标集
			
			String setid = (String)setList.get(i); //指标集
			if(setid == null){
				continue;
			}
			//String setDesc = this.getFieldSetDesc(setid);//指标集描述
			FieldSet tempset=DataDictionary.getFieldSetVo(setid);
			if(tempset==null) {
                continue;
            }
			String setDesc = tempset.getCustomdesc();
			
			StringBuffer items = new StringBuffer();
			
			if("1".equals(flag)){//读
				for(int j=0; j<fieldReadList.size(); j++){//遍历所有授权为读的指标
					String item = (String)fieldReadList.get(j);//指标
					if(item == null){
						continue;
					}
					if("A01".equalsIgnoreCase(setid)){
						if("B0110".equalsIgnoreCase(item)){
							items.append("单位编码");
							items.append("&nbsp;&nbsp;");
						}else if("E01A1".equalsIgnoreCase(item)){
							items.append("职位编码");
							items.append("&nbsp;&nbsp;");
						}else if("E0122".equalsIgnoreCase(item)){
							items.append("部门");
							items.append("&nbsp;&nbsp;");
						}else{
							if(this.checkItem(item,setid)){
								//String itemdesc = this.getFieldItemDesc(item);//指标描述
								String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
								items.append(itemdesc);
								items.append("&nbsp;&nbsp;");
							}
						}
					}else{
						//System.out.println("item=" + item + "setid=" + setid );
						if(this.checkItem(item,setid)){
							//String itemdesc = this.getFieldItemDesc(item);//指标描述
							String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
							items.append(itemdesc);
							items.append("&nbsp;&nbsp;");
						}
					}

					
					
				}
			}else if("2".equals(flag)){//写
				for(int j=0; j<fieldWriteList.size(); j++){
					String item = (String)fieldWriteList.get(j);
					if(item == null){
						continue;
					}
					
					if("A01".equalsIgnoreCase(setid)){
						if("B0110".equalsIgnoreCase(item)){
							items.append("单位编码");
							items.append("&nbsp;&nbsp;");
						}else if("E01A1".equalsIgnoreCase(item)){
							items.append("职位编码");
							items.append("&nbsp;&nbsp;");
						}else if("E0122".equalsIgnoreCase(item)){
							items.append("部门");
							items.append("&nbsp;&nbsp;");
						}else{
							if(this.checkItem(item,setid)){
								//String itemdesc = this.getFieldItemDesc(item);//指标描述
								String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
								items.append(itemdesc);
								items.append("&nbsp;&nbsp;");
							}
						}
					}else{
						//System.out.println("item=" + item + "setid=" + setid );
						if(this.checkItem(item,setid)){
							//String itemdesc = this.getFieldItemDesc(item);//指标描述
							String itemdesc = DataDictionary.getFieldItem(item).getItemdesc();
							items.append(itemdesc);
							items.append("&nbsp;&nbsp;");
						}
					}
				}
			}
			
			if(items==null||items.length()==0){
				
			}else{
				userSetOrFieldPriv.append(setDesc.trim());
				userSetOrFieldPriv.append("<br>");
				userSetOrFieldPriv.append("&nbsp;&nbsp;");
				userSetOrFieldPriv.append(items);
				userSetOrFieldPriv.append("<br>");
			}
			
		}
		return userSetOrFieldPriv.toString();
	}
	
	public boolean checkItem(String itemid , String setid){
		boolean b = false;
		if(setid == null || "".equals(setid)){
			return b;
		}
		if(itemid == null || "".equals(itemid)){
			return b;
		}
		ArrayList list = DataDictionary.getFieldList(setid,Constant.ALL_FIELD_SET);
		if(list == null || list.size()==0){
		}else{
			ArrayList itemsList = new ArrayList();
			for(int i=0; i< list.size(); i++){
				FieldItem temp = (FieldItem) list.get(i);
				itemsList.add(temp.getItemid());
			}
			if(itemsList == null || itemsList.size() == 0){}else{
				if(itemsList.contains(itemid.toLowerCase())){
					b = true;
				}
			}
		}
		
		/*
		boolean b = false;
		String sql="select * from fielditem  where itemid = '"+itemid+"' and fieldsetid ='"+setid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return b;
	}

		
			
	public String getFieldSetDesc(String setid){
		String setDesc = "";
		String sql="select fieldsetdesc from fieldset where fieldsetid='"+setid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				setDesc = rs.getString("fieldsetdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return setDesc;
	}
	
	public String getFieldItemDesc(String itemid){
		String itemDesc="";
		String sql="select itemdesc from fielditem where itemid='"+itemid+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				itemDesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return itemDesc;
	}
	/*************************功能权限分析***********************************/
	/**
	 * 权限分析
	 * @param flag	自助平台/业务平台标识(1,2)
	 */
	public String formatFunctionPrivToHtml(String flag){
		String fp = this.pb.getFunctionPriv();
		if(fp==null){
			return "";
		}
		fp = this.getFunctionPriv(fp,flag);
		FunctionWizard fw = new FunctionWizard();
		fw.setLock(lock);
		return fw.functionXmlToHtml(fp);
	}
	
	/**
	 * 用户功能权限分析
	 * @param functionPriv  权限id信息
	 * @param flag	自助平台/业务平台标识(1,2)
	 * @return
	 */
	public String getFunctionPriv( String functionPriv,String flag){
		if(functionPriv == null || "".equals(functionPriv)){
			return "";
		}
		//System.out.println("functionPriv=" + functionPriv );
		String fpss = "";
		StringBuffer fps = new StringBuffer();		
		String [] fp = functionPriv.split(",");
		/*
		int maxLength = 0;  //授权列表中最长编码个数
		for(int i = 0; i<fp.length; i++){
			String fun_id = fp[i];
			if(fun_id == null || fun_id.equals("")){
				continue;
			}
			if(fun_id.length() > maxLength){
				maxLength = fun_id.length();
			}
		}
		System.out.println("maxLength=" + maxLength);
		
		int minLength = 0;//授权列表中长度最短编码个数
		for(int i = 0; i<fp.length; i++){
			String fun_id = fp[i];
			if(fun_id == null || fun_id.equals("")){
				continue;
			}
			if(fun_id.length() < maxLength){
				minLength = fun_id.length();
				maxLength = fun_id.length();
			}
		}
		System.out.println("minLength=" + minLength);
		
		
		//自助平台/业务平台标识 编码标识
	    ArrayList baseFunidList = new ArrayList();
		for(int i = 0; i<fp.length; i++){
			String fun_id = fp[i];
			if(fun_id == null || fun_id.equals("")){
				continue;
			}
			if(fun_id.length()==minLength){
				baseFunidList.add(fun_id);
			}
		}
		
		if(baseFunidList==null){
			return "";
		}
		
		for(int i=0; i<baseFunidList.size();i++){
			System.out.println("__________"+baseFunidList.get(i));
		}
		*/
		String base_id="";
		if("1".equals(flag)){//自助服务平台
			//base_id = (String) baseFunidList.get(0);
			base_id="0";  //写死了
			for(int i=0; i< fp.length; i++){
				String fun_id = fp[i];
				if(fun_id == null || "".equals(fun_id)||fun_id.equals(base_id)){
					continue;
				}
				if(fun_id.startsWith(base_id)){
					fps.append(fun_id);
					fps.append(",");
				}
			}
			
		}else{//业务平台		
			//if(baseFunidList != null && baseFunidList.size()>1){
				//base_id = (String) baseFunidList.get(1);
				base_id="2";
				for(int i=0; i< fp.length; i++){
					String fun_id = fp[i];
					if(fun_id == null || "".equals(fun_id)||fun_id.equals(base_id)){
						continue;
					}
					if(fun_id.startsWith(base_id)||fun_id.startsWith("3")){
						fps.append(fun_id);
						fps.append(",");
					}
				}
			//}
		}
		
		
		if(fps == null || "".equals(fps)||fps.length() == 0){
			return "";
		}else{
			//System.out.println(fps.length());
			fpss = fps.substring(0,fps.length()-1);
		}
		return fpss;
	}
	
	/**************************资源权限分析**********************************/
	
	public String getResourcePriv(String res_flag){
		String rp = this.pb.getResourcePriv();
		if(rp == null || "".equals(rp)){
			return "";
		}
		int n = Integer.parseInt(res_flag);
		ResourcePopedomParser rpp = new ResourcePopedomParser(rp,n);
		String str = rpp.getContent();
		StringBuffer resourcePriv = new StringBuffer();
		String sql=this.getResourceSql(n,str);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				String name = rs.getString("name");
				resourcePriv.append(name);
				if(n==29||n==30) {
                    resourcePriv.append(",&nbsp;");
                } else {
                    resourcePriv.append("<br>");
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resourcePriv.toString();
	}
	
	/**
	 * 资源权限SQL语句
	 * @param res_type		资源类型
	 * @param str_content	资源类型授权情况
	 */
	private String getResourceSql(int res_type , String str_content)
	{
		StringBuffer strsql=new StringBuffer();
		if("".equals(str_content)) {
            str_content="-1";
        }
		switch(res_type){
			case REPORT: //统计表1
				strsql.append("select name from tname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by tsortid,tabid");
				break;
			case CARD: //登记表0
				strsql.append("select name from rname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by tabid");
				break;
			case MUSTER:  //常用花名册4
				strsql.append("select hzname name from lname where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by flag, tabid");			
				break;
			case HIGHMUSTER: //高级花名册5
				strsql.append("select cname name from muster_name where tabid in (");
				strsql.append(str_content);
				strsql.append(") order by nmodule, tabid");		
				break;
			case LEXPR: //常用查询2
				strsql.append("select id tabid,name  from lexpr where id in (");
				strsql.append(str_content);
				strsql.append(") order by type, tabid");		
				break;
			case STATICS:  //常用统计3
				strsql.append("select id tabid,name  from sname where id in (");
				strsql.append(str_content);
				strsql.append(") order by type, tabid");					
				break;
			case LAWRULE:  //规章制度6
				strsql.append("select name  from law_base_struct where base_id in (");
				strsql.append(str_content);
				strsql.append(") order by displayorder");	
				break;	
			case RSBD:  //人事异动模版7
				strsql.append("select name from template_table where tabid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case GZBD://8工资变动
				strsql.append("select name from template_table where tabid in(");
				strsql.append(str_content);
				strsql.append(")");	
			    break;	
			case INVEST://9;问卷调查表
				strsql.append("select content as name from investigate where  id in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case TRAINJOB://10;培训班
				strsql.append("select R3130 as name from R31 where R3101 in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case ANNOUNCE://11;公告栏
				strsql.append("select topic as name from announce where id in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case GZ_SET://12;薪资类别
				strsql.append("select cname as name from salarytemplate where salaryid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;				
			case ARCH_TYPE://14;档案分类	
				String archivetype=SystemConfig.getPropertyValue("archivetype");
				if(archivetype==null||archivetype.length()==0) {
                    archivetype="XB";
                }
				strsql.append("select codeitemid tabid,codeitemdesc name from codeitem where codesetid='"+archivetype);
				strsql.append("' and codeitemid in(");
				strsql.append(str_content);
				strsql.append(")");
				
				break;	
			case KQ_MACH://15;考勤机
				strsql.append("select name from kq_machine_type where type_id in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case MEDIA_EMP://16;人员多媒体分类授权
				strsql.append("");
				break;	
			case INS_BD://17;保险福利变动
				strsql.append("select name from template_table where tabid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case ORG_BD:
				strsql.append("select name from template_table where tabid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case POS_BD:
				strsql.append("select name from template_table where tabid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case INS_SET://18;保险福利类别
				strsql.append("select cname as name from salarytemplate where salaryid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case DOCTYPE://19;文档分类
				strsql.append("select name from law_base_struct where base_id in(");
				strsql.append(str_content);
				strsql.append(")");
				break;	
			case GZ_CHART://20;工资分析图表	
				strsql.append("select tablename as name from stattable where tbid in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case KNOWTYPE://21;知识分类
				strsql.append("select name from law_base_struct where base_id in(");
				strsql.append(str_content);
				strsql.append(")");
				break;	
			case KH_MODULE://22;考核模板
				if(str_content!=null&& "-1".equals(str_content)) {
                    str_content="'-1'";
                } else
				{
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<strs.length;i++)
					{
						buf.append("'"+strs[i]+"',");
					}
					buf.setLength(buf.length()-1);
					str_content=buf.toString();
				}
				strsql.append("select name from per_template where template_id in(");				
				strsql.append(str_content);
				strsql.append(")");	
				break;	
			case KH_FIELD://23考核指标
				if(str_content!=null&& "-1".equals(str_content)) {
                    str_content="'-1'";
                } else
				{
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<strs.length;i++)
					{
						buf.append("'"+strs[i]+"',");
					}
					buf.setLength(buf.length()-1);
					str_content=buf.toString();
				}
				strsql.append("select pointname as name from per_point where  point_id in(");
				strsql.append(str_content);
				strsql.append(")");	
				break;
			case PARTY:
			case MEMBER:
					String strs[]=str_content.split(",");
					StringBuffer buf=new StringBuffer();
					String codesetid="";
					boolean isall = false;
					for(int i=0;i<strs.length;i++)
					{
						String tmp = strs[i];
						if(tmp.length()<2) {
                            continue;
                        }
						if(i==0) {
                            codesetid = tmp.substring(0,2);
                        }
						if(tmp.length()==2){
							isall=true;
							continue;
						}
						buf.append("'"+tmp.substring(2)+"',");
					}
					if(buf.length()>0) {
                        buf.setLength(buf.length()-1);
                    }
					str_content=buf.toString();
				strsql.append("select codeitemdesc as name from codeitem where  codeitemid in(");
				strsql.append(str_content);
				strsql.append("'') and codesetid='"+codesetid+"'");	
				if(isall){
					StringBuffer tmpsql  = new StringBuffer();
					tmpsql.append("select '");
					switch(res_type){
					case PARTY:
						tmpsql.append("党组织");
						break;
					case MEMBER:
						tmpsql.append("团组织");
						break;
					}
					tmpsql.append("' as name from codeitem");
					strsql.insert(0, com.hrms.hjsj.utils.Sql_switcher.sqlTop(tmpsql.toString(), 1)+" union all ");
				}
				break;	
			case KQ_BASE_CLASS:
				strsql.append("select name from kq_class where class_id in (");
				strsql.append(str_content);
				strsql.append(")");
				break;
			case KQ_CLASS_GROUP:
				strsql.append("select name from kq_shift_group where group_id in (");
				strsql.append(str_content);
				strsql.append(")");
				break;
		}
		return strsql.toString();
	}
	
	private TreeMap showFucntionPriv(){
		TreeMap funPrivMap = new TreeMap();
		String fp = this.pb.getFunctionPriv();
		if(!fp.startsWith(",")) {
            fp =","+fp;
        }
		if(!fp.endsWith(",")) {
            fp+=",";
        }
		FunctionWizard fw = new FunctionWizard();
		fw.setLock(lock);
		fw.getFunPrivHtml(funPrivMap, fp);
		return funPrivMap;
	}


    public void setLock(EncryptLockClient lock) {
        this.lock = lock;
    }


    public EncryptLockClient getLock() {
        return lock;
    }
}
