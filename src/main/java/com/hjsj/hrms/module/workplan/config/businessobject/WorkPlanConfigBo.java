package com.hjsj.hrms.module.workplan.config.businessobject;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *<p>
 * Title:WorkPlanConfigBo
 * </p>
 *<p>
 * Description:工作计划配置参数bo类
 * </p>
 *<p>
 * Company:HJSJ
 * </p>
 *<p>
 * Create Time:2016-6-12:上午10:12:24
 * </p>
 * 
 * @author haosl
 *@version
 */
public class WorkPlanConfigBo {
	private Connection conn = null;
	private UserView userView = null;
	
	public WorkPlanConfigBo(Connection conn) {
		this.conn = conn;
	}
	
	public WorkPlanConfigBo(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}
	/**
	 * 查询是否存在指定常量的记录
	 * 
	 * @return boolean
	 * @throws GeneralException
	 */
	public RowSet getConstant(String constant) throws GeneralException {
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			//为空或者“”直接返回false
			if (constant != null && "".equals(constant.trim())) {
				return null;
			}
			String sql = "select Str_Value from constant where Constant = ?";
			ArrayList param = new ArrayList();
			param.add(constant);
			return dao.search(sql, param); 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
	/**
	 * 插入记录
	 * @throws GeneralException 
	 */
	public void insertRecord() throws GeneralException {
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "insert into constant(Constant,str_value,describe,type) values(?,?,?,?)";
			//插入记录时，向str_value中插入默认值
			String xmlData = generateXml();
			ArrayList values = new ArrayList();
			values.add("OKR_CONFIG");
			values.add(xmlData);
			values.add("工作计划参数配置");	//describe
			values.add("");	//type
			
			dao.insert(sql, values);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 更新记录
	 * @throws GeneralException 
	 */
	public void updateRecord(String xml) throws GeneralException {
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "update constant  set Str_Value=? where Constant=?";
			ArrayList values = new ArrayList();
			values.add(xml);
			values.add("OKR_CONFIG");
			int result = dao.update(sql, values);//DB
			
			if(result==1){//更新成功，同步到内存
					RecordVo vo = new RecordVo("Constant");
					vo.setString("constant", "OKR_CONFIG");
					vo.setString("describe", "工作计划参数配置");
					vo.setString("str_value", xml);
					vo.setString("type", "");
					ConstantParamter.putConstantVo(vo, "OKR_CONFIG");
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

    /**
	 * 获得OKR_CONFIG的配置数据，并以map形式返回数据
	 * @throws GeneralException 
	 */
	 public Map getXmlData() throws GeneralException{
		 RowSet rs = null;
		 Map map = null;
		 try{
			rs = getConstant("OKR_CONFIG");
			if(rs!=null && rs.next()){//有记录
			   String xml = rs.getString("Str_Value");
				map = parseXml(xml);
			}else{
				//向数据库中添加默认值
				map = new HashMap();
				insertRecord();
				map.put("Constant", "OKR_CONFIG");
				String xml = generateXml();
				map.put("str_value",xml);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
	 }
	 
	/**
	 * 解析xml格式数据
	 * @param xml xml数据
	 * @return
	 * @throws GeneralException 
	 */
	 public Map parseXml(String xml) throws GeneralException{
		 //创建一个新的字符串
		 Map map = new HashMap();	//存放解析出来的数据
		 
		 if(StringUtils.isEmpty(xml)){
			 map = getXmlData();
			 return map;
		 }
		 try{
			 
			 //xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(xml);
			 //获得root节点
			 Element root = doc.getRootElement();
			 Element taskNode = root.getChild("cooperative_task");//协作任务处理模式节点
			 Element weightNode = root.getChild("plan_weight");//工作计划权重控制节点
			 Element task_set = root.getChild("task_set");//工作计划权重控制节点
			 Element time_sign = root.getChild("time_sign");//工作计划权重控制节点
			 Element fill_model = root.getChild("fill_model");//工作计划权重控制节点
			 Element summary = root.getChild("summary");//总结显示工作任务
			 //linb
			 Element nbases = root.getChild("nbases");//填报人员库
			 Element emp_scope = root.getChild("emp_scope");//填报人员条件
			 //30231 人员库节点默认全部认证库
			 String[] nbase = getNbase();//认证库
			 StringBuffer cbase = new StringBuffer();
			 for(int i=0;i<nbase.length;i++){
				 cbase.append(nbase[i].toString());
				 if(i < nbase.length-1){
					 cbase.append(",");
				 }
			 }
			 if(nbases!=null){
				 String nbasesV = nbases.getText();
				 if(StringUtils.isEmpty(nbasesV)){
					 nbasesV = cbase.toString();
				 }
				 map.put("nbases", nbasesV);
			 }else{
				 map.put("nbases", cbase.toString());
			 }
			 if(emp_scope!=null){
				 String emp_scopeV = emp_scope.getText();
				 map.put("emp_scope", emp_scopeV);
			 }
			if(task_set!=null){
				String taskSet = task_set.getAttributeValue("setid");
				String taskItem = task_set.getAttributeValue("itemid");
				map.put("taskSet", taskSet);
				map.put("taskItem", taskItem);
			}
			 if(time_sign!=null){
				 String taskTimeSign = time_sign.getAttributeValue("itemid");
				 map.put("taskTimeSign", taskTimeSign);
			 }
			 if(fill_model!=null){
				 String fillModel = fill_model.getText();
				 map.put("fillModel", fillModel);
			 }
			 if(taskNode!=null){
				 //获取属性值和文本内容值
				String dealModelValue = taskNode.getAttributeValue("deal_model");
				map.put("cooperative_task", dealModelValue);
			 }
			 if(weightNode!=null){
				 String fromVal = weightNode.getAttributeValue("from");
				 String toVal = weightNode.getAttributeValue("to");
				 String planWeightVal = weightNode.getText();
				 map.put("plan_weight", planWeightVal);
				 map.put("from", fromVal);
				 map.put("to", toVal);
			 }
			 if(summary!=null){
				 String show_task = summary.getAttributeValue("show_task");
				 map.put("show_task", show_task);
			 }
			 return map;
		 }catch (Exception e) {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	 }
	 
	 /**
	  *生成默认xml数据
	  * @return
	 * @throws GeneralException 
	  */
	 public String generateXml() throws GeneralException{
		 try {
			 String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <params></params>";
			 //xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(xml);
			 //获得root节点
			 Element root = doc.getRootElement();
			 Element taskNode = new Element("cooperative_task");//协作任务处理模式节点
			 Element weightNode = new Element("plan_weight");//工作计划权重控制节点
			 //设置属性和文本内容
			 taskNode.setAttribute("deal_model", "1");
			 weightNode.setAttribute("from", "100");
			 weightNode.setAttribute("to", "100");
			 weightNode.setText("1");
			 root.addContent(taskNode);
			 root.addContent(weightNode);
			 //人员库节点默认全部认证库
			 Element nbasesNode = new Element("nbases");
			 String[] nbases = getNbase();//认证库
			 StringBuffer cbase = new StringBuffer();
			 for(int i=0;i<nbases.length;i++){
				cbase.append(nbases[i].toString());
				if(i < nbases.length-1){
					cbase.append(",");
				}
			 }
			 nbasesNode.setText(cbase.toString());
			 Element summaryNode = new Element("summary");//总结显示工作任务,默认为false不显示
			 summaryNode.setAttribute("show_task", "false");
			 root.addContent(summaryNode);
			 //设置xml字体编码，然后输出为字符串
			 Format format=Format.getRawFormat();
			 format.setEncoding("UTF-8");
			 XMLOutputter output=new XMLOutputter(format);
			 return output.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	 }
	 
	 /**
	     * 填报人员，列表列头
	     * @param fieldList：数据字典列表
	     * @return
	     * chent 
	     */
	    public ArrayList<ColumnsInfo> getColumnList(ArrayList fieldList, ArrayList dynamicColumn){
	    	
	    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
	    	try{
	    		ArrayList list = new ArrayList();
	    		list.addAll(fieldList);
	    		list.addAll(dynamicColumn);
				for(int i=0; i<list.size(); i++){
					FieldItem item = (FieldItem)list.get(i);
					String itemid = item.getItemid();//字段id
					if("".indexOf(itemid) > -1){// 排除字段
						continue ;
					}
					
					String itemtype = item.getItemtype();//字段类型
					String codesetid = item.getCodesetid();//关联的代码			
					String columndesc = item.getItemdesc();//字段描述
					int itemlength = item.getItemlength();//字段长度
					String fieldsetid = item.getFieldsetid();
					
					ColumnsInfo columnsInfo = getColumnsInfo(itemid, columndesc, 100, itemtype, fieldsetid);
					columnsInfo.setColumnLength(itemlength);
					columnsInfo.setEditableValidFunc("false");
					
					if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
						if("0".equals(codesetid) || codesetid == null){//非代码字符型
							columnsInfo.setCodesetId("0");
						}else{//代码型字符
							columnsInfo.setCodesetId(codesetid);
						}
					} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
						columnsInfo.setCodesetId("0");
					}
					if("a0101".equalsIgnoreCase(itemid) || "b0110".equals(itemid) || "e0122".equals(itemid) || "e01a1".equals(itemid)){//单位姓名部门岗位 锁列
						columnsInfo.setLocked(true);
						if("e0122".equals(itemid))
							columnsInfo.setDoFilterOnLoad(true);
					}else {
						if("A01".equalsIgnoreCase(fieldsetid)){
							continue ;
							//columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
						} else {
							columnsInfo.setColumnWidth(90);
							columnsInfo.setOperationData("isOpen");
							columnsInfo.setRendererFunc("workPlanConfig.rendererColumn");
						}
					}
					columnTmp.add(columnsInfo);
				}
				
				// 隐藏
				ColumnsInfo columnsInfo = new ColumnsInfo();
				columnsInfo.setColumnId("guidkey");
				columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columnTmp.add(columnsInfo);
				
				
	    	} catch (Exception e) {
	            e.printStackTrace();
	            GeneralExceptionHandler.Handle(e);
	        }
	    	return columnTmp;
	    }
	    /**
	     * 初始化控件列对象
	     * @param columnId
	     * @param columnDesc：名称
	     * @param columnWidth：显示列宽
	     * @param type：类型
	     * @return
	     */
	    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type, String fieldsetid) {

	        ColumnsInfo columnsInfo = new ColumnsInfo();
	        columnsInfo.setColumnId(columnId);
	        columnsInfo.setColumnDesc(columnDesc);
	        //columnsInfo.setCodesetId("");// 指标集
	        columnsInfo.setColumnType(type);// 类型N|M|A|D
	        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
	        if ("A".equals(type)) {
	            columnsInfo.setCodesetId("0");
	        }
	        columnsInfo.setDecimalWidth(0);// 小数位
	        columnsInfo.setFieldsetid(fieldsetid);
	        	
	        // 数值和日期默认居右
	        if ("D".equals(type) || "N".equals(type))
	            columnsInfo.setTextAlign("right");

	        return columnsInfo;
	    }
	    
	    /**
	     * 获取动态列
	     * @param configList
	     * @return
	     */
	    public ArrayList getConfigList(List<HashMap<String, HashMap<String, String>>> configList){
	    	ArrayList list = new ArrayList();
	    	
	    	
	    	FieldItem item = new FieldItem();
	    	item.setItemtype("A");
	    	item.setCodesetid("0");
	    	item.setItemlength(10);

	    	HashMap<String, String> configMap = this.getConfigMap();
	    	for(int i=0; i<configList.size(); i++){
	    		HashMap<String, HashMap<String, String>> map = configList.get(i);
	    		for (String key : map.keySet()) {  
	    			
	    			
	    			if(!"p5".equalsIgnoreCase(key)){//日志以外，有个人、部门两种。
	    				
	    				FieldItem item_0 = (FieldItem)item.clone();//个人
	    				item_0.setItemid(key + "0");
	    				item_0.setItemdesc(configMap.get(key + "0"));
	    				list.add(item_0);

	    				FieldItem item_1 = (FieldItem)item.clone();//部门
	    				item_1.setItemid(key + "1");
	    				item_1.setItemdesc(configMap.get(key + "1"));
	    				list.add(item_1);
	    			} else {
	    				FieldItem item_0 = (FieldItem)item.clone();
	    				item_0.setItemid(key);
	    				item_0.setItemdesc(configMap.get(key));
	    				list.add(item_0);
	    			}
	    			
	    		}   
	    	}
	    	
	    	return list;
	    }
	    
	    /**
		 * 获取sql
		 * @param fieldList
		 * @return
		 * @throws GeneralException
		 * chent
		 */
		public String getSql(ArrayList fieldList, ArrayList configList) throws GeneralException{
			
			StringBuilder sql =  new StringBuilder();//查询sql
			
			try {
				RecordVo paramsVo=ConstantParamter.getConstantVo("OKR_CONFIG");
				String xmlValue = "";
				Map mapXml = new HashMap();
				// 有缓存则取缓存数据
				if(null != paramsVo){
					xmlValue = paramsVo.getString("str_value");
				}
				mapXml = parseXml(xmlValue);
				String dbValue = mapXml.get("nbases")==null?"":(String)mapXml.get("nbases");
				String emp_scope = mapXml.get("emp_scope")==null?"":(String)mapXml.get("emp_scope");
				String[] nbases = dbValue.split(",");//认证库
				if(StringUtils.isEmpty(dbValue)){
					throw new GeneralException("未设置认证人员库！");
				}
				
				String b0110 = this.userView.getUnitIdByBusi("5");//取得所属单位
				sql.append("select * from (");
				for (int i = 0; i < nbases.length; i++) {
					String n = nbases[i];
					if (StringUtils.isEmpty(n)) {
						continue;
					}
					
					if(i > 0){
						sql.append(" union all ");
					}

					String tableName = n + "A01";
					sql.append("select ");
					for(int j=0; j<fieldList.size(); j++){
						FieldItem item = (FieldItem)fieldList.get(j);
						String itemid = item.getItemid();//字段id
						sql.append(tableName+"."+itemid+",");
					}
					for(int j=0; j<configList.size(); j++){
						FieldItem item = (FieldItem)configList.get(j);
						String itemid = item.getItemid();//字段id
						sql.append("Func."+itemid+",");
					}
					sql.append(tableName+".guidkey,"+tableName+".a0000");
					sql.append(" from "+tableName);
					sql.append(" left join per_task_func Func on "+tableName+".GUIDKEY=Func.guidkey ");
					
					 //OKR人员范围sql条件
					 String whereIn = getOkrWhereINSql(n, emp_scope);
					 if(StringUtils.isNotBlank(whereIn)){
					 	//此处原来是通过A01自联查，条件多的话，oracle 下查询数据会异常，因外表有A01，直接截取where后追加即可 haosl 20200122
						 int index = whereIn.toLowerCase().indexOf("where");
						 if(index>-1){
						 	sql.append(whereIn.substring(index));
						 }
					 }
				}
				sql.append(") c where 1=1 ");
				if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
					String[] b0110Array = b0110.split("`");
					sql.append(getB0110Sql(b0110Array));
				}
				
	        }catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			return sql.toString();
		}
		 /**
		 * 获取功能按钮
		 * @return
		 */
		public ArrayList<Object> getButtonList(){
			
			ArrayList<Object> buttonList = new ArrayList<Object>();
			try{
				ButtonInfo buttonInfo = new ButtonInfo();
				
				buttonInfo = new ButtonInfo("人员范围设置", "workPlanConfig.empScope");
				buttonInfo.setId("committee_empScope");
				buttonList.add(buttonInfo);
				
				buttonInfo = new ButtonInfo("批量设置", "workPlanConfig.batchSetting");
				buttonInfo.setId("committee_randomChoose");
				buttonList.add(buttonInfo);
				
				ButtonInfo queryBox = new ButtonInfo();
				queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
				queryBox.setText("请输入单位名称、部门、岗位、姓名");
				queryBox.setFunctionId("WP20000003");
				buttonList.add(queryBox);
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return buttonList;
		}
		public HashMap<String, String> getConfigMap(){
			HashMap<String, String> configMap = new HashMap<String, String>();
	    	configMap.put("p00", "个人年计划");
	    	configMap.put("p01", "部门年计划");
	    	configMap.put("p10", "个人半年计划");
	    	configMap.put("p11", "部门半年计划");
	    	configMap.put("p20", "个人季度计划");
	    	configMap.put("p21", "部门季度计划");
	    	configMap.put("p30", "个人月度计划");
	    	configMap.put("p31", "部门月度计划");
	    	configMap.put("p40", "个人周计划");
	    	configMap.put("p41", "部门周计划");
	    	
	    	configMap.put("s00", "个人年总结");
	    	configMap.put("s01", "部门年总结");
	    	configMap.put("s10", "个人半年总结");
	    	configMap.put("s11", "部门半年总结");
	    	configMap.put("s20", "个人季度总结");
	    	configMap.put("s21", "部门季度总结");
	    	configMap.put("s30", "个人月度总结");
	    	configMap.put("s31", "部门月度总结");
	    	configMap.put("s40", "个人周总结");
	    	configMap.put("s41", "部门周总结");
	    	
	    	configMap.put("p5", "日志");
	    	
	    	return configMap;
		}
		/** 应用库 */
		public String[] getNbase() {
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			if (login_vo != null) {
				String strpres = login_vo.getString("str_value");
				return strpres.split(",");
			}
			
			return new String[0];
		}
		public String getOKRConfigSqlWhere() throws GeneralException{
			StringBuffer sql = new StringBuffer("");
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				Map map = getXmlData();
				String  nbases = map.get("nbases")==null?"":(String)map.get("nbases");
				String  emp_scope = map.get("emp_scope")==null?"":(String)map.get("emp_scope");
				if(StringUtils.isEmpty(emp_scope)){
					return "";
				}
				emp_scope=PubFunc.getStr(emp_scope);
				YksjParser yp=null;
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				yp = new YksjParser(this.userView ,alUsedFields,YksjParser.forSearch, YksjParser.LOGIC,YksjParser.forPerson , "wk", "Usr");
				YearMonthCount ycm = null;	
				yp.setSupportVar(true);  //支持临时变量
				yp.run_Where(emp_scope, ycm,"","", dao, "", conn, "A", null); 
				String wherestr = yp.getSQL();//公式的结果
				String tempTableName = yp.getTempTableName();
				sql.append(wherestr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return sql.toString();
		}
		/**
		 * 取得okr参数人员库列表
		 * @paramdbValues 设置的人员库范围
		 * @return
		 * @throws GeneralException
		 */
		public ArrayList getDbList(String dbValues) throws GeneralException{
			ArrayList list=new ArrayList();
			try
			{
				LazyDynaBean abean=null;
				String[] nbases = getNbase();//认证库
				String cbase="," ;
				DbNameBo dd=new DbNameBo(this.conn);
				ArrayList dblist=dd.getAllDbNameVoList();//全部库
				for(int i=0;i<nbases.length;i++){
					cbase += nbases[i].toString().toLowerCase()+",";
				}
				if(StringUtils.isNotEmpty(dbValues)){
					dbValues = ","+dbValues.toLowerCase()+",";
				}else{
					//没有设置人员库默认为认证库
					dbValues = cbase;
				}
				
				for(int i=0;i<dblist.size();i++)
				{
					RecordVo vo=(RecordVo)dblist.get(i);
					String dbpre=vo.getString("pre");
					String dbname=vo.getString("dbname");
					String isSelected="0";
					if(cbase.indexOf(","+dbpre.toLowerCase()+",")!=-1){
						if(dbValues.indexOf(","+dbpre.toLowerCase()+",")!=-1){
							isSelected="1";
						}
						abean=new LazyDynaBean();
						abean.set("pre",dbpre);
						abean.set("dbname",dbname);
						abean.set("isSelected",isSelected);
						list.add(abean);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return list;
		}
		
		/**
		 * 解析简单条件表达式
		 * @param empScope 如：1*2+3|A0107=1`A0405=21`A0121=01`
		 * @return
		 */
		public String getCexprCondValue(String empScope){
			
			if(StringUtils.isEmpty(empScope)){
				return "";
			}
			StringBuffer empScopeValue = new StringBuffer("");
			String cexpr=empScope.split("\\|")[0];
			String cond=empScope.split("\\|")[1];
			ArrayList ecprList = new ArrayList();
			//解析英子表达式 1*2+3 由于这里只用到或、且，如有其它再行扩展
			for (int i = 0; i < cexpr.length(); i++) {
				char v = cexpr.charAt(i);
				if (i + 1 != cexpr.length() && v >= '0' && v <= '9') {
					continue;
				}
				if (v == '+')
					ecprList.add(" 或 ");
				else if (v == '*')
					ecprList.add(" 且 ");
			}
			//拼接条件语句
			FactorList factor = new FactorList(cexpr, cond, "", false, false, true, 1, "su");	
			for(int i=0;i<factor.size();i++){
				Factor map = (Factor) factor.get(i);
				String hz = map.getHz();
				String oper = map.getOper();
				String hzvalue = map.getHzvalue();
				if(i == factor.size()-1)
					empScopeValue.append(hz+oper+hzvalue);
				else
					empScopeValue.append(hz+oper+hzvalue).append((String)ecprList.get(i));
			}
		
			return empScopeValue.toString();
		}
		
		/**
		 * 获取OKR人员范围sql条件
		 * @param userbase	库前缀
		 * @param empScope	条件表达式 如：1*2+3|A0107=1`A0405=21`A0121=01`
		 * @return  FROM UsrA01 WHERE ...
		 */
		public static String getOkrWhereINSql(String userbase, String empScope){
	        String strwhere = "";
	        String cexpr = "";
			String cond = "";
			try {
		        if(!StringUtils.isEmpty(empScope) && !StringUtils.isEmpty(userbase)){
					cexpr=empScope.split("\\|")[0];
					cond=empScope.split("\\|")[1];
				}
		        
	        	FactorList factor = new FactorList(cexpr, cond,userbase, false, false, true, 1, "su");				
	        	strwhere = factor.getSqlExpression();
	        	//若empScope表达式为空，strwhere仍会返回WHERE 1=2语句，故这里校验empScope是否为空
	        	if(StringUtils.isEmpty(empScope)){
	        		return "";
	        	}
				
			} catch (GeneralException e) {
				e.printStackTrace();
			}
	        return strwhere;
	     }
		
		/**
		 * 取非超级管理员认证应用库和人员库的交集
		 * @param nbase 认证应用库字符串数组
		 * @return 
		 */
		private String[] getNbase(String[] nbase){
			String nbases = this.userView.getDbpriv().toString();//取人员库
			if(",".equals(nbases)){//没任何人员库权限
				nbase = null;
			}else{//有人员库权限，取交集
				String[] tempNbase = nbases.split(",");
				StringBuffer tempNbases = new StringBuffer();
				for (int i = 0; i < tempNbase.length; i++) {
					if (StringUtils.isEmpty(tempNbase[i]))
						continue;
					for (int j = 0; j < nbase.length; j++) {
						if (tempNbase[i].equals(nbase[j])) {
							tempNbases.append(tempNbase[i]+",");
							break;
						}
					}
				}
				if(tempNbases.length()==0)
					nbase = null;
				else 
					nbase = tempNbases.toString().split(",");
			}
			return nbase;
		}
		/**
		 * 获取已经启用的计划和总结方案（批量设置页面使用）
		 * @return
		 * @throws GeneralException 
		 */
		public List<Map<String,String>> getPlanConfig() throws GeneralException{
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			try {
				WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.conn);
				List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
				HashMap<String, String> configMap = this.getConfigMap();
				HashMap<String,String> hm = null;
				for(int i=0; i<configList.size(); i++){
		    		HashMap<String, HashMap<String, String>> map = configList.get(i);
		    		
		    		for (String key : map.keySet()) {  
		    			
		    			if(!"p5".equalsIgnoreCase(key)){//日志以外，有个人、部门两种。
		    				//个人
		    				hm = new HashMap<String, String>();
		    				hm.put("id",key+"0");
		    				hm.put("planSummy", configMap.get(key + "0"));
		    				list.add(hm);
		    				hm = new HashMap<String, String>();
		    				//部门
		    				hm.put("id",key+"1");
		    				hm.put("planSummy", configMap.get(key + "1"));
		    				list.add(hm);
		    			} else {
		    				hm = new HashMap<String, String>();
		    				hm.put("id",key);
		    				hm.put("planSummy", configMap.get(key));
		    				list.add(hm);
		    			}
		    			
		    		}   
		    	}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} 
			return list;
		}
		/**
		 * 更新人员权限表
		 * @param guidkey
		 * 			
		 * @param itemid
		 * 
		 * 	guidkey和itemid为空时
		 * @throws GeneralException 
		 */
		public void saveSettings(String guidkey, String itemid,String value) throws GeneralException {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select guidkey from per_task_func where guidkey='"+guidkey+"'";
				rs = dao.search(sql);
				if(rs.next()){
					sql = "update per_task_func set "+itemid+"="+value+" where guidkey='"+guidkey+"'";
					dao.update(sql);
				}else {
					if(StringUtils.isBlank(guidkey))
						return;
					sql = "insert into per_task_func(guidkey,"+itemid+") values(?,?)";
					List values = new ArrayList();
					values.add(guidkey);
					values.add(value);
					dao.insert(sql, values);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeResource(rs);
			}
			
		}
		/**
		 * 批量更新人员权限表
		 * @param sql
		 * @throws GeneralException 
		 */
		public void batchSaveSettings(String sql,Map<String,String> itemMap) throws GeneralException{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
//				long time1 = System.currentTimeMillis();
				StringBuffer upItems = new StringBuffer();
				for(Map.Entry<String, String> item : itemMap.entrySet()){
					if(StringUtils.isNotBlank(item.getKey())){
						upItems.append(item.getKey()+"="+item.getValue()+",");//组装需要更新的字段
					}
				}
				if(upItems.length()==0)
					return;
				rs = dao.search(sql);
				List<String> selectIds  = new ArrayList<String>();
				List<String> selectIds_copy = new ArrayList<String>();
				while(rs.next()){
					String guidkey = rs.getString("guidkey");
					if(StringUtils.isNotBlank(guidkey))
						selectIds.add(guidkey);
				}
//				long time2 = System.currentTimeMillis();
//				System.out.println("查询段耗时："+(time2-time1));
				if(selectIds.isEmpty())
					return;
				selectIds_copy.addAll(selectIds);
				sql = "select guidkey from per_task_func";//查询数据库已有的人员
				rs = dao.search(sql);
				while(rs.next()){
					String guidkey = rs.getString("guidkey");
					if(selectIds.contains(guidkey))
						selectIds.remove(guidkey);//出去已有的主键
				}
				sql = "insert into per_task_func(guidkey) values(?)";
				List values = new ArrayList();
				List<String> temp = null;
				for(String s : selectIds){
					temp = new ArrayList<String>();
					temp.add(s);
					values.add(temp);
				}
				if(selectIds.size()>0)
					dao.batchInsert(sql, values);//先插入主键
				values.clear();
				long time3 = System.currentTimeMillis();
				for(String s : selectIds_copy){
					temp = new ArrayList<String>();
					temp.add(s);
					values.add(temp);
				}
				String upSql = "update per_task_func set "+upItems.substring(0,upItems.length()-1)+" where guidkey=?";
				dao.batchUpdate(upSql, values);
//				long time4 = System.currentTimeMillis();
//				System.out.println("更新耗时："+(time4-time1));
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeDbObj(rs);
			}
		}
		/**
		 * 获得工作任务子集数据
		 * @return
		 * @throws GeneralException
		 */
		public ArrayList<HashMap<String, String>> getTaskdata() throws GeneralException {

	    	
	    	ArrayList<HashMap<String, String>> taskData = new ArrayList<HashMap<String, String>>();
	    	
	    	ContentDAO dao = new ContentDAO(this.conn);
	    	RowSet rs = null;
			try {
				String sql = "select fieldsetid,fieldsetdesc From fieldSet where fieldsetid like 'K%' and fieldsetid not in('K01') and useflag = 1 order by Displayorder ";
		    	
				rs = dao.search(sql);
				
				while (rs.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
			    	map.put("fieldsetid", rs.getString("fieldsetid"));//指标集编号
			    	map.put("fieldsetdesc", rs.getString("fieldsetdesc"));//名称
					
			    	taskData.add(map);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeDbObj(rs);
			}
			
			return taskData;
	    
		}
		/**
		 * 查询工作任务子集指标
		 * @return
		 * @throws GeneralException 
		 */
		public ArrayList<HashMap<String, String>> gettaskItemData(String fieldsetid) throws GeneralException {
			ArrayList<HashMap<String, String>> taskItemData = new ArrayList<HashMap<String, String>>();
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select itemid,itemdesc From fielditem where fieldsetid=? and useflag = 1 order by displayid";
				ArrayList<String> list = new ArrayList<String>();
				list.add(fieldsetid);
				
				rs = dao.search(sql, list);
				
				while (rs.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("itemid", rs.getString("itemid"));//指标编号
					map.put("itemdesc", rs.getString("itemdesc"));//名称
					
					taskItemData.add(map);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeDbObj(rs);
			}
			
			return taskItemData;
		}
		/**
		 * 任务是否计时下拉数据
		 * @return
		 * @throws GeneralException 
		 */
		public ArrayList<HashMap<String, String>> getTaskTimeData() throws GeneralException {
			ArrayList<HashMap<String, String>> taskTimeData = new ArrayList<HashMap<String, String>>();
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				String sql = "select itemid,itemdesc from fielditem where fieldsetid='K01' and useflag = 1 and itemtype='A' and codesetid='45' order by displayid";
				rs = dao.search(sql);
				
				while (rs.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("itemid", rs.getString("itemid"));//指标编号
					map.put("itemdesc", rs.getString("itemdesc"));//名称
					
					taskTimeData.add(map);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeDbObj(rs);
			}
			
			return taskTimeData;
		}
	/**
	 * 查询人员的计划总结的启用情况
	 * guidkey
	 * 		人员唯一标识
	 * itemid
	 * 		人员权限指标（P00 P01...）
	 * @return true|false
	 * @throws GeneralException 
	 */
	public boolean personOpenModel(String guidkey,String itemid) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		boolean model = false;
		try {
			if(StringUtils.isBlank(guidkey) || StringUtils.isBlank(itemid))
				throw GeneralExceptionHandler.Handle(new Exception("参数错误！"));
			String sql ="select "+itemid+" from per_task_func where guidkey='"+guidkey+"'";
			rs = dao.search(sql);
			if(rs.next()){
				int value = rs.getInt(itemid);
				if(value==0)
					model = false;
				else
					model = true;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return model;
	}
	/**
	 * 根据nbase和a0100获取该人员在OKR所有填写类型权限
	 * 如：个人年计划、部门年计划、个人年总结、部门年总结..等
	 * @param nbase
	 * @param a0100
	 * @return HashMap
	 * @throws GeneralException
	 */
	public HashMap getPersonFillType(String nbase,String a0100) throws GeneralException{
		RowSet rs=null;
		HashMap map = new HashMap();
		try {
			if(StringUtils.isNotEmpty(nbase) && StringUtils.isNotEmpty(a0100) ){
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer sql = new StringBuffer("");
				sql.append("select * from per_task_func ");
				sql.append("where guidkey in (");
				sql.append("select guidkey from ");
				sql.append(nbase).append("A01 ");
				sql.append("where a0100='").append(a0100).append("' ");
				sql.append(")");
				
				rs=dao.search(sql.toString());
				while(rs.next()) {
					map.put("P00", rs.getInt("P00"));
					map.put("P01", rs.getInt("P01"));
					map.put("P10", rs.getInt("P10"));
					map.put("P11", rs.getInt("P11"));
					map.put("P20", rs.getInt("P20"));
					map.put("P21", rs.getInt("P21"));
					map.put("P30", rs.getInt("P30"));
					map.put("P31", rs.getInt("P31"));
					map.put("P40", rs.getInt("P40"));
					map.put("P41", rs.getInt("P41"));
					
					map.put("S00", rs.getInt("S00"));
					map.put("S01", rs.getInt("S01"));
					map.put("S10", rs.getInt("S10"));
					map.put("S11", rs.getInt("S11"));
					map.put("S20", rs.getInt("S20"));
					map.put("S21", rs.getInt("S21"));
					map.put("S30", rs.getInt("S30"));
					map.put("S31", rs.getInt("S31"));
					map.put("S40", rs.getInt("S40"));
					map.put("S41", rs.getInt("S41"));
					
					map.put("P5", rs.getInt("P5"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 结合填报期间范围设置获取个人OKR填写的权限明细
	 * @param myFuncMap  该人员在OKR所有填写类型权限
	 * @param list		填报期间范围集合
	 * @return map
	 */
	public HashMap getPersonFunctions(HashMap myFuncMap, List list) {
		HashMap map = new HashMap();
		HashMap<String,HashMap<String,String>> hm = null;
		/**
		 * 这里将个人的填写类型范围 与 部门的填写类型范围分开处理
		 */
		//个人参数
		ArrayList personList = new ArrayList();
		//部门参数
		ArrayList orgList = new ArrayList();
		//32801 linbz 未设置填报人员权限明细时，直接走 填报期间范围的权限
		if(myFuncMap.isEmpty()) {
			map.put("person", list);
			map.put("org", list);
			return map;
		}
		for(int i=0;i<list.size();i++) {
			hm = new HashMap<String, HashMap<String,String>>();
			hm = (HashMap<String, HashMap<String, String>>) list.get(i);
			//年计划
			if(null != hm.get("p0")) {
				int p00 = (Integer) myFuncMap.get("P00");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("P01");
				if(p01 == 1)
					orgList.add(hm);
			}
			//年总结
			else if(null != hm.get("s0")) {
				int p00 = (Integer) myFuncMap.get("S00");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("S01");
				if(p01 == 1)
					orgList.add(hm);
			}
			//半年计划
			else if(null != hm.get("p1")) {
				int p00 = (Integer) myFuncMap.get("P10");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("P11");
				if(p01 == 1)
					orgList.add(hm);
			}
			//半年总结
			else if(null != hm.get("s1")) {
				int p00 = (Integer) myFuncMap.get("S10");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("S11");
				if(p01 == 1)
					orgList.add(hm);
			}
			//季度计划
			else if(null != hm.get("p2")) {
				int p00 = (Integer) myFuncMap.get("P20");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("P21");
				if(p01 == 1)
					orgList.add(hm);
			}
			//季度总结
			else if(null != hm.get("s2")) {
				int p00 = (Integer) myFuncMap.get("S20");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("S21");
				if(p01 == 1)
					orgList.add(hm);
			}
			//月计划
			else if(null != hm.get("p3")) {
				int p00 = (Integer) myFuncMap.get("P30");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("P31");
				if(p01 == 1)
					orgList.add(hm);
			}
			//月总结
			else if(null != hm.get("s3")) {
				int p00 = (Integer) myFuncMap.get("S30");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("S31");
				if(p01 == 1)
					orgList.add(hm);
			}
			//周计划
			else if(null != hm.get("p4")) {
				int p00 = (Integer) myFuncMap.get("P40");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("P41");
				if(p01 == 1)
					orgList.add(hm);
			}
			//周总结
			else if(null != hm.get("s4")) {
				int p00 = (Integer) myFuncMap.get("S40");
				if(p00 == 1)
					personList.add(hm);
				int p01 = (Integer) myFuncMap.get("S41");
				if(p01 == 1)
					orgList.add(hm);
			}
			//个人日志
			else if(null != hm.get("p5")) {
				int p00 = (Integer) myFuncMap.get("P5");
				if(p00 == 1)
					personList.add(hm);
			}
			
		}
		map.put("person", personList);
		map.put("org", orgList);
		return map;
	}
	
	/**
	 * 获取上级开启学院聘任组的评审会议和获取本级及一下的评审会议
	 * @param b0110 组织机构号
	 * @return sql
	 */
	private String getB0110Sql(String[] tmp) {
		StringBuilder sql = new StringBuilder();
		sql.append("and (");
		for(int i=0; i<tmp.length; i++){
			String b = tmp[i].substring(2);
			
			sql.append("c.b0110 like '"+b+"%' or c.e0122 like '"+b+"%' ");//本级、下级
			
			if(i < tmp.length-1){
				sql.append("or ");
			}
		}
		sql.append(") ");
		return sql.toString();
	}
	
	 /**   
	 * @Title: getPriorPeriodParam   
	 * @Description: 取的上一期间或下一期间的参数   
	 * @param  flag =0上一期间；//=1下一期间（暂时获取支持上一期间）
	 * @param  type =0计划；=1总结
	 * @param  periodType
	 * @param  period_year
	 * @param  period_month
	 * @param  period_week
	 * @return HashMap
	 * @throws   
	*/
	public HashMap getPriorPeriodParam(String flag, String type, String periodType, String year, 
	            String month, String week)  throws GeneralException{
		
		HashMap map = new HashMap();
        try {
        	// 填报期间范围权限 
        	WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.conn, this.userView);
			List<HashMap<String, HashMap<String, String>>> configList = funcBo.getXmlData();
		
			HashMap cyclemap =  new HashMap();
			for(int i=0;i<configList.size();i++){
				HashMap<String,HashMap<String,String>> hm = configList.get(i);
				
				if("0".equals(type)){
					if(hm.get("p1") != null ){
			            cyclemap.put("halfyears",hm.get("p1").get("cycle"));
			        }else if(hm.get("p2") != null ){
			        	
			            cyclemap.put("quaters",hm.get("p2").get("cycle"));
			        }else if(hm.get("p3") != null ){
			        	
			            cyclemap.put("months",hm.get("p3").get("cycle"));
			        }
				}else if("1".equals(type)){
					if(hm.get("s1") != null ){
			            cyclemap.put("halfyears",hm.get("s1").get("cycle"));
			        }else if(hm.get("s2") != null ){
			        	
			            cyclemap.put("quaters",hm.get("s2").get("cycle"));
			        }else if(hm.get("s3") != null ){
			        	
			            cyclemap.put("months",hm.get("s3").get("cycle"));
			        }
				}
			}
			//半年权限
			String[] halfyearlist = StringUtils.split((String)cyclemap.get("halfyears"), ",");
			//季度权限
		    String[] quaterlist = StringUtils.split((String)cyclemap.get("quaters"), ",");
		    //月份权限
		    String[] monthlist = StringUtils.split((String)cyclemap.get("months"), ",");
		        
		    map.put("year", year);
		    map.put("month", month);
		    map.put("week", week);
			if("0".equals(type)){
		        if (WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
		            map.put("year", Integer.valueOf(year)-1);
		        } else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {
		            if (Integer.parseInt(month)==1) {
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                if((","+(String)cyclemap.get("halfyears")+",").indexOf(",2,") != -1)
		       	            map.put("month","2");
		       	        else if((","+(String)cyclemap.get("halfyears")+",").indexOf(",1,") != -1)
		       	            map.put("month","1");
		            }else {
		                if((","+(String)cyclemap.get("halfyears")+",").indexOf(",1,") != -1)
		       	            map.put("month","1");
		       	        else if((","+(String)cyclemap.get("halfyears")+",").indexOf(",2,") != -1){
		       	        	map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		       	        	map.put("month","2");
		       	        }
		            }
		        } else if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
		            if (week.equals(quaterlist[0])){
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                map.put("week", quaterlist[quaterlist.length-1]);  
		            }
		            else {
		                String monthStr = "";
		                for(int i=0;i<quaterlist.length;i++){
		                	if(week.equals(quaterlist[i]) && i!=0){
		                		monthStr = quaterlist[i-1];
		                    }
		                }
		                map.put("week", monthStr);
		            }
		        } else if (WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
		            if (month.equals(monthlist[0])) {
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                map.put("month", monthlist[monthlist.length-1]);
		            }
		            else {
		                String monthStr = "";
		                for(int i=0;i<monthlist.length;i++){
		                	if(month.equals(monthlist[i]) && i!=0){
		                		monthStr = monthlist[i-1];
		                    }
		                }
		                map.put("month", monthStr);
		            }
		        } else if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
		        	//周计划不需要控制月份权限   haosl 2018-2-8
		            if (Integer.parseInt(week)==1) {
		                if ("1".equals(month)) {
		                    map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                    map.put("month", 12);
		                    int weekNum=getWeekNum(Integer.parseInt(year)-1, 12); 
		                    map.put("week", weekNum+"");  
		                }
		                else {
		                    map.put("month",String.valueOf(Integer.parseInt(month)-1));  
		                    int weekNum=getWeekNum(Integer.parseInt(year), Integer.parseInt(month)-1); 
		                    map.put("week", String.valueOf(weekNum));    
		                }  
		            }
		            else {
		                map.put("week", String.valueOf(Integer.parseInt(week)-1));   
		            }
		        }
			}
			else if("1".equals(type)){
		        if (WorkPlanConstant.SummaryCycle.YEAR.equals(periodType)) {
		            map.put("year", Integer.valueOf(year)-1);
		        } else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(periodType)) {
		        	//周总结不需要控制月份权限  haosl 2018-2-8
		        	if (Integer.parseInt(month)==1) {
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                if((","+(String)cyclemap.get("halfyears")+",").indexOf(",2,") != -1)
		       	            map.put("month","2");
		       	        else if((","+(String)cyclemap.get("halfyears")+",").indexOf(",1,") != -1)
		       	            map.put("month","1");
		            }else {
		                if((","+(String)cyclemap.get("halfyears")+",").indexOf(",1,") != -1)
		       	            map.put("month","1");
		       	        else if((","+(String)cyclemap.get("halfyears")+",").indexOf(",2,") != -1)
		       	            map.put("month","2");
		            }
		        } else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(periodType)) {
		            if (Integer.parseInt(week)==1) {
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                map.put("week", quaterlist[quaterlist.length-1]);  
		            }
		            else {
		                String monthStr = "";
		                for(int i=0;i<quaterlist.length;i++){
		                	if(week.equals(quaterlist[i]) && i!=0){
		                		monthStr = quaterlist[i-1];
		                    }
		                }
		                map.put("week", monthStr);
		            }
		        } else if (WorkPlanConstant.SummaryCycle.MONTH.equals(periodType)) {
		            if (Integer.parseInt(month)==1) {
		                map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                map.put("month", monthlist[monthlist.length-1]);
		            }
		            else {
		                String monthStr = "";
		                for(int i=0;i<monthlist.length;i++){
		                	if(month.equals(monthlist[i]) && i!=0){
		                		monthStr = monthlist[i-1];
		                    }
		                }
		                map.put("month", monthStr);
		            }
		        } else if (WorkPlanConstant.SummaryCycle.WEEK.equals(periodType)) {
		            if (Integer.parseInt(week)==1) {
		                if (Integer.parseInt(month)==1) {
		                    map.put("year", String.valueOf(Integer.parseInt(year)-1));  
		                    map.put("month", 12);
		                    int weekNum=getWeekNum(Integer.parseInt(year)-1, 12); 
		                    map.put("week", weekNum+"");  
		                }
		                else {
		                    map.put("month",String.valueOf(Integer.parseInt(month)-1));  
		                    int weekNum=getWeekNum(Integer.parseInt(year), Integer.parseInt(month)-1); 
		                    map.put("week", String.valueOf(weekNum));    
		                }  
		            }
		            else {
		                map.put("week", String.valueOf(Integer.parseInt(week)-1));   
		            }
		        }
			
			}
        } catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
        return map;
    }
	
	/**   
     * @Title: getWeekNum   
     * @Description:获取某一个月的周数    
     * @param @param period_year
     * @param @param period_month
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
     */
    public int getWeekNum(int period_year, int period_month) {
        return new WorkPlanSummaryBo(null, this.conn).getWeekNum(period_year,
                period_month);
        
    }
    
}
