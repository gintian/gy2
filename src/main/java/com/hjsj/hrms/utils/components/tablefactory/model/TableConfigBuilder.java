package com.hjsj.hrms.utils.components.tablefactory.model;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;

/**
 * 构建表格参数
 * @author guodd 2015-06-24
 *
 */
public class TableConfigBuilder {

	public static HashMap TableXmlConstantHM = new HashMap();
	
	public static String SCHEME_POSITION_TITLE = "title";
	public static String SCHEME_POSITION_TOOLBAR = "toolbar";
	public static String SCHEME_POSITION_MENUBAR = "menubar";
	public static String SCHEME_POSITION_CUSTOM = "custom";
	
	public String schemePosition;
	
	private Connection conn;
	
	private UserView userView;
	//列对象
	private ArrayList columnsInfoList;
	//表格唯一id
	private String subModuleId;
	//表格加载数据交易类号(自定义表格数据时使用)
	private String tableFunctionId;
	//配置文件xml名
	private String constantName;
	//id 前缀
	private String prefix;
	//表格工具栏功能
	private ArrayList tableTools;
	//菜单栏菜单
	private ArrayList tableMenus;
	//右键菜单
	private HashMap contextMenuObj;
	//锁列
	private boolean lockable = false;
	//表格编辑
	private boolean editable = false;
	//排序
	private boolean sortable = true;
	//选框
	private boolean selectable = false;
	//是否显示序号列
	private boolean showRowNumber = false;
	//行双击事件
	private String rowdbclick;
	// 标题
	private String title;
	//每页条数
	private int pageSize = 20;
	//初始加载页
	private int currentPage=1;
	//是否自动渲染表格到页面
	private boolean autoRender = false;
	//最高行高
	private int tdMaxHeight = 30;
	
	//列头是否使用栏目设置方案显示
	private boolean isScheme = false;
	//用户是否可以设置栏目设置
	private boolean isSetScheme = true;
	//栏目设置添加指标 key
	private String schemeItemKey;
	//栏目设置添加指标 functionid
	private String itemKeyFunctionId;
	//是否显示公有方案
	private boolean showPublicPlan = false;
	//栏目设置保存后的回调事件
	private String schemeSaveCallback;
	
	//是否启用统计分析
	private boolean isAnalyse = false;
	
	private boolean fieldAnalyse = false;
	
	private boolean isColumnFilter = false;
	//搜索功能交易类号
	private String searchFuncId;
	//搜索框提示信息
	private String searchText;
	//搜索框是否启用方案查询
	private boolean showPlanBox = true;
	
	//业务模块号
	private String moduleId;
	
	//数据查询sql语句
	private String dataSql = "";
	//排序语句
	private String orderBy = "";
	//索引列，多个用逗号隔开
	private String indexKey = "";
	
	//数据集合
	private ArrayList dataList;
	//页信息对象
	private Pageable pageable;
	
	private ArrayList displayColumn;
	
	private HashMap customParamHM;
	
	private ArrayList defaultSchemeColumnList;
	
	//以下内置属性，没有set方法
	private ArrayList dataFields = new ArrayList();
	private ArrayList emptyColumnList = new ArrayList();
	private LinkedHashMap columnMap = new LinkedHashMap();
	private HashMap queryFields = new HashMap();
	//columnsInfo是否有设置iskey
	private String primeryKeys = "";
	
	
	//栏目设置有权限的指标 用逗号隔开
	private String schemePrivFields = null;
	//表格加载前走的交易类号
	private String beforeLoadFunctionId = null;
	//是否显示分页栏
	private boolean isPageTool = true;
	
	/**
	 * 构造函数
	 * @param subModuleId  表格唯一id
	 * @param columnsInfoList 列集合 ArrayList<ColumnsInfo>
	 * @param prefix  元素 id前缀
	 * @param userView 
	 */
	public TableConfigBuilder(String subModuleId,ArrayList columnsInfoList,String prefix,UserView userView,Connection conn){
		if(subModuleId==null || columnsInfoList==null || userView ==null)
			throw new NullPointerException("参数能为null！请检查参数！");
		this.subModuleId = subModuleId;
		this.columnsInfoList = columnsInfoList;
		this.userView = userView;
		this.prefix = prefix;
		this.conn = conn;
	}
	
	/**
	 * 构造函数
	 * @param subModuleId  表格唯一id
	 * @param columnsInfoList 列集合 ArrayList<ColumnsInfo>
	 * @param tableFunctionId 获取表格数据交易类
	 * @param prefix  元素 id前缀
	 * @param userView 
	 */
	public TableConfigBuilder(String subModuleId,ArrayList columnsInfoList,String tableFunctionId,String prefix,UserView userView,Connection conn){
		if(subModuleId==null || columnsInfoList==null || userView ==null)
			throw new NullPointerException("参数能为null！请检查参数！");
		this.subModuleId = subModuleId;
		this.columnsInfoList = columnsInfoList;
		this.tableFunctionId = tableFunctionId;
		this.userView = userView;
		this.prefix = prefix;
		this.conn = conn;
	}


	public String createExtTableConfig(){
		HashMap tableConfig = new HashMap();
		
		tableConfig.put("subModuleId", subModuleId);
		tableConfig.put("tableFunctionId",tableFunctionId);
		tableConfig.put("cookiePre", PubFunc.encrypt(userView.getUserName()));
		tableConfig.put("currentPage", currentPage);
		tableConfig.put("prefix", prefix);
		tableConfig.put("autoRender", autoRender);
		tableConfig.put("tdMaxHeight", tdMaxHeight);
		tableConfig.put("title", title);
		tableConfig.put("remoteData",true);
		
		tableConfig.put("fieldAnalyse", fieldAnalyse);
		tableConfig.put("moduleId", moduleId);
		tableConfig.put("isColumnFilter", isColumnFilter);
		tableConfig.put("showRowNumber", showRowNumber);
		
		//读取配置文件，生成工具栏、菜单等，将参数放入tableConfig
		setTableConfigs(tableConfig);
		tableConfig.put("isAnalyse",isAnalyse);
		tableConfig.put("isScheme", isScheme);
		if(isScheme){
			tableConfig.put("isSetScheme",isSetScheme);
			tableConfig.put("schemeItemKey",schemeItemKey);
			tableConfig.put("itemKeyFunctionId",itemKeyFunctionId);
			tableConfig.put("showPublicPlan",showPublicPlan);
			tableConfig.put("schemeSaveCallback",schemeSaveCallback);
			tableConfig.put("schemePosition", schemePosition);
		}
		
		collectingParams(columnsInfoList);
		ArrayList newColumns = rebuildColumns((HashMap)columnMap.clone());
		if(newColumns!=null){
			newColumns.addAll(emptyColumnList);
		}else{
			newColumns = columnsInfoList;
		}
		//newColumns = newColumns!=null?newColumns:columnsInfoList;
		
		tableConfig.put("tablecolumns",JSONArray.fromObject(newColumns));
		tableConfig.put("datafields",getDataFields(1));
		tableConfig.put("primeryKeys", primeryKeys);
		
		if(this.dataList == null){
			tableConfig.put("remoteSort", true);
		}
		
		tableConfig.put("pagesize", pageSize);
		tableConfig.put("isPageTool", isPageTool);	
		//重新配置orderby条件
		rebuildOrderby(newColumns);
		
		TableDataConfigCache cache = new TableDataConfigCache();
		cache.setDataFields(getDataFields(2));
		cache.setTableColumns(columnsInfoList);
		cache.setDisplayColumns(newColumns);
		cache.setColumnMap(columnMap);
		cache.setQueryField(queryFields);
		cache.setPageSize(pageSize);
		cache.setDefaultSchemeColumnList(defaultSchemeColumnList);
		cache.setColumnDisplayConfig(displayColumn);
		cache.setSchemePrivFields(this.schemePrivFields);
		cache.setBeforeLoadFunctionId(this.beforeLoadFunctionId);
		cache.put("title", title);
		if(pageable!=null){
		    //存放分页信息  xuj add 2015-1-27
		    pageable.setPageSize(pageSize);
		    cache.setPageable(pageable);
	    }
		if(dataList!=null)
    	    cache.setTableData(dataList);
		else{
			cache.setTableSql(dataSql);
			cache.setSortSql(orderBy);
			cache.setIndexkey(indexKey);
		}
		cache.setCustomParamHM(this.customParamHM);
        userView.getHm().put(subModuleId,cache);
		
        String configStr = JSON.toString(tableConfig);
	    configStr = configStr.replaceAll("\"<jsfn>", "");
	    configStr = configStr.replaceAll("</jsfn>\"", "");
		return configStr; 
	}
	/**
	 * 读取栏目设置 重新设置orderby
	 * @param newColumns
	 */
	private void rebuildOrderby(ArrayList columns) {
		ColumnsInfo column = null; 
		String columnId = "";
		String ordertype = "";
		String orderby = " order by ";
		String orderrule = "";
		
		ArrayList newColumns = (ArrayList)columns.clone();
		for(int i=0;i<newColumns.size();i++){
			column = (ColumnsInfo)newColumns.get(i);
			columnId = column.getColumnId();
			ordertype = column.getOrdertype();
			if(column.getChildColumns().size()>0){
				newColumns.addAll(i+1, column.getChildColumns());
				continue;
			}
			if("M".equals(column.getColumnType()))
				continue;
			
			if("0".equals(ordertype)){//无排序
				continue;
			}
			orderrule = "1".equals(ordertype)?" asc ":" desc ";
			
			if("N".equals(column.getColumnType())){
				orderby += Sql_switcher.isnull(columnId, "0") + orderrule+",";
				continue;
			}
			
			//非字符型字段直接排序
			if(!"A".equals(column.getColumnType())/* || (column.getCodesetId()!=null&&column.getCodesetId().length()==2)*/){
				orderby += columnId + orderrule+",";
				continue;
			}
			
			//字符型字段按照汉字首字母排序
			if(Sql_switcher.searchDbServer()==Constant.MSSQL)
					orderby += Sql_switcher.isnull(columnId, "''")+" collate Chinese_PRC_CS_AS_KS_WS "+orderrule+",";
			else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					orderby += " nlssort("+columnId+") "+orderrule+",";
			}else{
					orderby += columnId + orderrule+",";
			}
			
//			else if("1".equals(ordertype)){//正序
//				orderby += columnId + " asc ,";
//			}
//			else {//倒序
//				orderby += columnId + " desc ,";
//			}
		}
		if(!" order by ".equals(orderby)){//有需要排序的指标
			orderBy = orderby.substring(0,orderby.length()-1);
		}
		
		newColumns.clear();
		newColumns = null;
	}

	private ArrayList rebuildColumns(HashMap columns){
		if(!this.isScheme)
			return null;
		
		TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, conn);
		HashMap layoutConfig = tfb.getTableLayoutConfig();
		if(layoutConfig==null){
			return null;
		}
		ArrayList columnsConfigs = tfb.getTableColumnConfig((Integer)layoutConfig.get("schemeId"));
		
		ArrayList newColumns = tfb.rebuildColumns(columns,columnsConfigs,(Integer)layoutConfig.get("schemeId"));
		
		JSONArray configObj = JSONArray.fromObject(columnsConfigs);
		this.displayColumn = (ArrayList)JSONArray.toCollection(configObj);
		this.pageSize = (Integer)layoutConfig.get("pageRows");
		return newColumns;
	}
	
	/**
	 * 设置参数
	 * @param container
	 */
	private void setTableConfigByProperties(HashMap container){
	    container.put("lockable", lockable);
	    container.put("selectable", selectable);
	    container.put("editable", editable);
	    container.put("sortable", sortable);
	    container.put("rowdbclick", rowdbclick);
	    if(tableTools != null)
	    		container.put("customtools", getToolObjs(container,new ArrayList()));
	    if(tableMenus!=null)
	    	    container.put("custommenus",this.tableMenus);
	    if(searchFuncId!=null){
		    	container.put("searchFuncId",searchFuncId);
		    	container.put("searchText",searchText);
		    	container.put("showPlanBox",showPlanBox);
	    }
	}
	
	/**
	 * 读取表格配置
	 * @param container 参数保存容器
	 */
	private void setTableConfigs(HashMap container){
		
		//如果constantName为空，直接读取set方法进来的参数
		if(this.constantName==null){
			setTableConfigByProperties(container);
			return;
		}
		
		Document constantXml = null;
		InputStream in = null;
		try {
			if(TableXmlConstantHM.containsKey(constantName)){
				constantXml = (Document) ((Document)TableXmlConstantHM.get(constantName)).clone();
			}else{
				//根据constantName 读取 文件
			    ServletContext context = SystemConfig.getServletContext();
			    constantName=constantName.indexOf("\\")>-1?constantName.replace('\\','/'):constantName; //weblogic12C 读取文件路径 '\'会有问题 现在全部改用‘/’ changxy 20170224
			    in= context.getResourceAsStream("/tableconstant/"+constantName+".xml");
		        constantXml = PubFunc.generateDom(in);
		        TableXmlConstantHM.put(constantName, constantXml);
			}
	        //解析document
	        Element root = constantXml.getRootElement();
			Element table = root.getChild("table");
			if(table!=null){
				Iterator tableite = table.getAttributes().iterator();
				while(tableite.hasNext()){
					Attribute a = (Attribute)tableite.next();
					if("true".equals(a.getValue()) || "false".equals(a.getValue()))
						container.put(a.getName(), new Boolean(a.getValue()));
					else
						container.put(a.getName(), a.getValue());
				}
			}
			
			if(!container.containsKey("selectable"))
				  container.put("selectable", this.selectable);
			if(!container.containsKey("editable"))
				container.put("editable", this.editable);
			if(!container.containsKey("sortable"))
				container.put("sortable", this.sortable);
			if(!container.containsKey("lockable"))
				container.put("lockable", this.lockable);
			
			//生产右键菜单
			Element contextmenu = table.getChild("contextmenu");
			if(contextmenu!=null){
				HashMap contextmenuObj = createMenuBar(contextmenu);
				container.put("contextmenu", contextmenuObj);
			}
			
			//生成 menu菜单
			Element menu = root.getChild("menu");
		    List menuEleList = menu==null?new ArrayList():menu.getChildren();
		    ArrayList menuObjList = new ArrayList();
			for(int i=0;i<menuEleList.size();i++){
				Element menuEle = (Element)menuEleList.get(i);
				String privid = menuEle.getAttributeValue("privid");
				if((privid!=null && privid.length()>0 && !userView.hasTheFunction(privid)) || !"menubar".equals(menuEle.getName()))
					continue;
				
				HashMap menuObj = createMenuBar(menuEle);
				menuObjList.add(menuObj);
			}
			if(!menuObjList.isEmpty())
				container.put("custommenus", menuObjList);
			
			//生成工具栏按钮
			Element tools = root.getChild("tools");
			//String toolPosition = tools==null?"top":tools.getAttributeValue("position");
			List toolEleList = tools==null?new ArrayList():tools.getChildren();
			ArrayList toolObjList = new ArrayList();
			for(int i=0;i<toolEleList.size();i++){
				Element toolEle = (Element)toolEleList.get(i);
				String privid = toolEle.getAttributeValue("privid");
				if(privid!=null && privid.trim().length()>0 && !userView.hasTheFunction(privid))
					continue;
				
				HashMap toolObj = new HashMap();
				if("menubar".equals(toolEle.getName())){
					toolObj = createMenuBar(toolEle);
					toolObjList.add(toolObj);
					continue;
				}
				
				String type = toolEle.getAttributeValue("type");
				String funcType = toolEle.getAttributeValue("functype");
				if("input".equals(type)){//输入框类型
					toolObj.put("xtype", "textfield");
					toolObj.put("fieldLabel", toolEle.getAttributeValue("text"));
					//fieldlabel宽度 =（字个数+1）* 12（px）
					int width = (ResourceFactory.getProperty(toolEle.getAttributeValue("text")).length()+1)*12;
					toolObj.put("labelWidth", new Integer(width));
					toolObj.put("labelAlign", "right");
					toolObj.put("id", toolEle.getAttributeValue("id"));
				}else if("date".equals(type)){//日期类型
					toolObj.put("xtype", "datetimefield");
					toolObj.put("fieldLabel", ResourceFactory.getProperty(toolEle.getAttributeValue("text")));
					//fieldlabel宽度 =（字个数+1）* 12（px）
					int width = (toolEle.getAttributeValue("text").length()+1)*12;
					toolObj.put("labelWidth", new Integer(width));
					toolObj.put("labelAlign", "right");
					toolObj.put("id", toolEle.getAttributeValue("id"));
				}else if("html".equals(type)){//html代码
					toolObjList.add(toolEle.getText().trim());
					continue;
				}else if("split".equals(type)){//分割符
					toolObjList.add("-");
					continue;
				}else if("space".equals(type)){
					toolObjList.add("->");
					continue;
				}else if("querybox".equals(type)){
					String functionid = toolEle.getAttributeValue("functionid");
					String showPlanBox = toolEle.getAttributeValue("showplanbox");
					String text = ResourceFactory.getProperty(toolEle.getAttributeValue("text"));
				    if(functionid==null || functionid.length()==0)
				    	   continue;
				    container.put("searchFuncId", functionid);
				    container.put("searchText", text);
				    container.put("showPlanBox", showPlanBox!=null && "false".equalsIgnoreCase(showPlanBox)?false:true);
				    container.put("queryBoxIndex", toolObjList.size());
				    continue;
				}else if("analyse".equals(funcType)){
					toolObj.put("text", "统计分析");
			    		toolObj.put("id",toolEle.getAttributeValue("id"));
			    		toolObj.put("icon", toolEle.getAttributeValue("icon"));
			    		toolObj.put("fntype",funcType);
			    		toolObj.put("cusBtn","cusBtn");
			    		this.setAnalyse(true);
			    		container.put("doAnalyse", true);
				}else if("scheme".equals(funcType)){
					toolObj.put("text", ResourceFactory.getProperty(toolEle.getAttributeValue("text")));
			    		toolObj.put("id",toolEle.getAttributeValue("id"));
			    		toolObj.put("icon", toolEle.getAttributeValue("icon"));
			    		toolObj.put("fntype",funcType);
			    		toolObj.put("cusBtn","cusBtn");
			    		this.setScheme(true);
			    		this.setSetScheme(true);
			    		this.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_CUSTOM);
			    		//container.put("schemePosition", "button");
				}else if("insert".equals(funcType) || "save".equals(funcType) || "delete".equals(funcType) || "export".equals(funcType)){//button类型
					String text = ResourceFactory.getProperty(toolEle.getAttributeValue("text"));
			    		toolObj.put("text", text);
			    		toolObj.put("itemId",toolEle.getAttributeValue("contextid"));
			    		toolObj.put("icon", toolEle.getAttributeValue("icon"));
			    		toolObj.put("fntype",funcType);
			    		toolObj.put("cusBtn","cusBtn");
			    		if(!"export".equals(type)){
				    		toolObj.put("beforefn",toolEle.getAttributeValue("beforefn"));
				    		toolObj.put("afterfn", toolEle.getAttributeValue("afterfn"));
				    		toolObj.put("functionid", toolEle.getAttributeValue("functionid"));
			    		}
				}else{
					toolObj.put("text", ResourceFactory.getProperty(toolEle.getAttributeValue("text")));
					toolObj.put("id", toolEle.getAttributeValue("id"));
					toolObj.put("fn", toolEle.getAttributeValue("handler"));
					toolObj.put("icon", toolEle.getAttributeValue("icon"));
					toolObj.put("getdata", toolEle.getAttributeValue("getdata"));
					toolObj.put("cusBtn", "cusBtn");
				}
				toolObjList.add(toolObj);
			}
			
			getToolObjs(container,toolObjList);
			
			if(!toolObjList.isEmpty())
				container.put("customtools", toolObjList);
			
			
			
		} catch (Exception e) {
		    e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(in);
		}
	}
	
	private ArrayList getToolObjs(HashMap container,ArrayList buttonList){
		//ArrayList buttonList = new ArrayList();
		if(tableTools == null)
			return buttonList;
		for(int k=0;k<tableTools.size();k++){
			Object obj = tableTools.get(k);
			if(obj instanceof String){
				buttonList.add(obj);
				continue;
			}
			ButtonInfo button = (ButtonInfo)obj;
			if(button.getInnerHTML()!=null){
				buttonList.add(button.getInnerHTML());
				continue;
			}
			HashMap buttonObj = new HashMap();
			if(button.getType().equals(ButtonInfo.TYPE_QUERYBOX)){
				container.put("searchFuncId", button.getFunctionId());
			    container.put("searchText", button.getText());
			    container.put("showPlanBox", button.isShowPlanBox());
			    container.put("queryBoxIndex", buttonList.size());
				continue;
			}
			buttonObj.put("id", button.getId());
			buttonObj.put("text", button.getText());
			buttonObj.put("cusBtn","cusBtn");
			buttonObj.put("disabled", Boolean.valueOf(button.isDisabled()));
			buttonObj.put("icon", button.getIcon());
			if(button.getFunctype()!=null){
				buttonObj.put("fntype", button.getFunctype());
				buttonObj.put("functionid", button.getFunctionId());
				buttonList.add(buttonObj);
				if("analyse".equals(button.getFunctype()))
					this.setAnalyse(true);
					container.put("doAnalyse", true);
				if("scheme".equals(button.getFunctype())){
					this.setScheme(true);
		    			this.setSetScheme(true);
		    			this.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_CUSTOM);
				}
				continue;
			}
			buttonObj.put("fn", button.getHandler());
			buttonObj.put("getdata", Boolean.valueOf(button.isGetData()));
			buttonObj.put("params",button.getParameterMap());
			buttonList.add(buttonObj);
		}
		return buttonList;
	}
	
	/**
	 * 生成menu
	 * @param menuEle
	 * @param userView
	 * @return
	 */
	private HashMap createMenuBar(Element menuEle){
		//菜单展开按钮对象
	    HashMap menubarObj = new HashMap();
		
	    
	    //获取下级节点
	    List itemEles = menuEle.getChildren();
	    
	    //没有 返回
	    if(itemEles == null || itemEles.size()==0)
	    	return null;
	    
	    //添加属性
	    menubarObj.put("text", ResourceFactory.getProperty(menuEle.getAttributeValue("text")));
	    menubarObj.put("id", menuEle.getAttributeValue("id"));
	    menubarObj.put("icon", menuEle.getAttributeValue("icon"));
	    
	    // 菜单项 容器对象
	    HashMap menuObj = new HashMap();
	    // 储存菜单项
	    ArrayList menuItems = new ArrayList();
	    for(int i=0;i<itemEles.size();i++){
		    	Element itemEle = (Element)itemEles.get(i);
		    	String privid = itemEle.getAttributeValue("privid");
			    if(privid!=null && privid.trim().length()>0 && !userView.hasTheFunction(privid))
					continue;
		    	String nodeName = itemEle.getName();
		    	//如果节点是 menubar ，说明是2级菜单
		    	if("menubar".equals(nodeName)){
		    		menuItems.add(createMenuBar(itemEle));
		    		continue;
		    	}
		    	
		    	//获取 菜单项 类型
		    	String type = itemEle.getAttributeValue("type");
		    	
		    	// 多选一
		    	if("group".equals(type)){
		    		HashMap groupMenuBar = new HashMap();
		    		groupMenuBar.put("text", ResourceFactory.getProperty(itemEle.getAttributeValue("text")));
		    		groupMenuBar.put("id", itemEle.getAttributeValue("id"));
		    		groupMenuBar.put("icon", itemEle.getAttributeValue("icon"));
		    		
		    		HashMap groupMenu = new HashMap();
		    		
		    		List optionItems = new ArrayList(); 
		    		
		    		List optionEles = itemEle.getChildren();
		    		for(int k=0;k<optionEles.size();k++){
		    			Element optionEle = (Element)optionEles.get(k);
		    			HashMap optionItem = new HashMap();
		    			optionItem.put("text", ResourceFactory.getProperty(optionEle.getAttributeValue("text")));
		    			optionItem.put("id", optionEle.getAttributeValue("id"));
		    			optionItem.put("value", optionEle.getAttributeValue("value"));
		    			String checked = optionEle.getAttributeValue("checked");
		    			optionItem.put("checked", new Boolean(checked));
		    			optionItem.put("group", itemEle.getAttributeValue("groupname"));
		    			optionItem.put("fn", optionEle.getAttributeValue("handler"));
		    			optionItem.put("cusMenu", "cusMenu");
		    			optionItems.add(optionItem);
		    		}
		    		groupMenu.put("items", optionItems);
		    		groupMenuBar.put("menu", groupMenu);
		    		menuItems.add(groupMenuBar);
		    		continue;
		    	}
		    	
		    	// 分割符
		    	if("split".equals(type)){
		    		menuItems.add("-");
		    		continue;
		    	}
		    		
		    	//创建 菜单项
		    	HashMap menuItemObj = new HashMap();
		    	menuItemObj.put("text", ResourceFactory.getProperty(itemEle.getAttributeValue("text")));
		    	menuItemObj.put("id", itemEle.getAttributeValue("id"));
		    	menuItemObj.put("icon", itemEle.getAttributeValue("icon"));
		    	menuItemObj.put("cusMenu", "cusMenu");
		    	menuItemObj.put("fn", itemEle.getAttributeValue("handler"));
		    	
		    	String funcType = itemEle.getAttributeValue("functype");
		    	if("check".equals(type)){ // 带checkbox框的 菜单
		    		menuItemObj.put("xtype", "menucheckitem");
		    	}else if("insert".equals(funcType) || "save".equals(funcType) || "delete".equals(funcType) || "export".equals(funcType)){
		    		String inpriv = itemEle.getAttributeValue("privid");
				if(inpriv!=null && inpriv.trim().length()>0 && !userView.hasTheFunction(inpriv))
						continue;
		    		menuItemObj.clear();
		    		String text = ResourceFactory.getProperty(itemEle.getAttributeValue("text"));
		    		menuItemObj.put("text", text);
		    		menuItemObj.put("icon", itemEle.getAttributeValue("icon"));
		    		menuItemObj.put("fntype",funcType);
		    		menuItemObj.put("cusMenu","cusMenu");
		    		if(!"export".equals(funcType)){
		    			menuItemObj.put("beforefn",itemEle.getAttributeValue("beforefn"));
			    		menuItemObj.put("afterfn", itemEle.getAttributeValue("afterfn"));
			    		menuItemObj.put("functionid", itemEle.getAttributeValue("functionid"));
		    		}
				    
		    	}else{// 普通按钮类型
		    		menuItemObj.put("getdata",itemEle.getAttributeValue("getdata"));
		    	}
		    	
		    	menuItems.add(menuItemObj);
	    }
		
	    menuObj.put("items", menuItems);
	    
	    //如果menuEle 是contextmenu节点，就是右键菜单，直接返回menu对象。否则就是menubar节点，返回menubar对象
	    if("contextmenu".equals(menuEle.getName())){
	    		return menuObj;
	    }
	    
	    menubarObj.put("menu", menuObj);
	    
		return menubarObj;
	}
	
	private void collectingParams(ArrayList columnsInfoList){
		TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, conn);
		HashMap configMap = new HashMap();
		configMap.put("dataFields", dataFields);
		configMap.put("columnMap", columnMap);
		configMap.put("emptyColumnList", emptyColumnList);
		configMap.put("queryFields", queryFields);
		configMap.put("primaryKeys", new StringBuffer());
		if(this.isSetScheme){
			configMap.put("schemeColumnList",new ArrayList());
		}
		tfb.doColumnCollect(configMap,columnsInfoList,null);
		StringBuffer primaryKeys = (StringBuffer)configMap.get("primaryKeys");
		this.primeryKeys = primaryKeys.toString();
		if(this.isSetScheme)
			this.defaultSchemeColumnList = (ArrayList)configMap.get("schemeColumnList");
		
	}
	
	
	/**
	 * 获取字段
	 * @param type 类型  1：用于生成Ext 中 Store的fields；2：用于sql查询的fields
	 * @return
	 */
	private String[] getDataFields(int type){
		Iterator ite = dataFields.iterator();
		int arraySize = dataFields.size();
		if(type==1){
			arraySize++;
		}
		String[] fields = new String[arraySize];
		int index = 0;
		while(ite.hasNext()){
	         String field = ((String)ite.next()).toLowerCase();
	         if(type == 2 && field.endsWith("_e")){
	        	    field = field.substring(0,field.length()-2);//field.replace("_e","");
	         }
	         fields[index] = field;
	         index++;
		}
		return fields;
	}


	public int getPageSize() {
		return pageSize;
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	public String getConstantName() {
		return constantName;
	}


	public void setConstantName(String constantName) {
		this.constantName = constantName;
	}




	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public ArrayList getTableTools() {
		return tableTools;
	}


	public void setTableTools(ArrayList tableTools) {
		this.tableTools = tableTools;
	}


	public ArrayList getTableMenus() {
		return tableMenus;
	}


	public void setTableMenus(ArrayList tableMenus) {
		this.tableMenus = tableMenus;
	}


	public int getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}


	public Pageable getPageable() {
		return pageable;
	}


	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}


	public HashMap getContextMenuObj() {
		return contextMenuObj;
	}


	public void setContextMenuObj(HashMap contextMenuObj) {
		this.contextMenuObj = contextMenuObj;
	}


	public boolean isLockable() {
		return lockable;
	}


	public void setLockable(boolean lockable) {
		this.lockable = lockable;
	}


	public boolean isEditable() {
		return editable;
	}


	public void setEditable(boolean editable) {
		this.editable = editable;
	}


	public boolean isSortable() {
		return sortable;
	}


	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}


	public boolean isSelectable() {
		return selectable;
	}


	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}


	public String getRowdbclick() {
		return rowdbclick;
	}


	public void setRowdbclick(String rowdbclick) {
		this.rowdbclick = rowdbclick;
	}
	
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public boolean isAutoRender() {
		return autoRender;
	}


	public void setAutoRender(boolean autoRender) {
		this.autoRender = autoRender;
	}


	public int getTdMaxHeight() {
		return tdMaxHeight;
	}


	public void setTdMaxHeight(int tdMaxHeight) {
		this.tdMaxHeight = tdMaxHeight;
	}


	public boolean isScheme() {
		return isScheme;
	}


	public void setScheme(boolean isScheme) {
		this.isScheme = isScheme;
	}


	public boolean isSetScheme() {
		return isSetScheme;
	}


	public void setSetScheme(boolean isSetScheme) {
		this.isSetScheme = isSetScheme;
	}


	public String getSchemeItemKey() {
		return schemeItemKey;
	}


	public void setSchemeItemKey(String schemeItemKey) {
		this.schemeItemKey = schemeItemKey;
	}
	
	public String getItemKeyFunctionId() {
		return itemKeyFunctionId;
	}

	public void setItemKeyFunctionId(String itemKeyFunctionId) {
		this.itemKeyFunctionId = itemKeyFunctionId;
	}


	public boolean isShowPublicPlan() {
		return showPublicPlan;
	}


	public void setShowPublicPlan(boolean showPublicPlan) {
		this.showPublicPlan = showPublicPlan;
	}


	public boolean isAnalyse() {
		return isAnalyse;
	}


	public void setAnalyse(boolean isAnalyse) {
		this.isAnalyse = isAnalyse;
	}




	public String getDataSql() {
		return dataSql;
	}


	public void setDataSql(String dataSql) {
		this.dataSql = dataSql;
	}


	public String getOrderBy() {
		return orderBy;
	}


	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}


	public String getIndexKey() {
		return indexKey;
	}


	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}


	public ArrayList getDataList() {
		return dataList;
	}


	public void setDataList(ArrayList dataList) {
		this.dataList = dataList;
	}

	
	public boolean isColumnFilter() {
		return isColumnFilter;
	}


	public void setColumnFilter(boolean isColumnFilter) {
		this.isColumnFilter = isColumnFilter;
	}

	/**
	 * 设置 查询组件参数
	 * @param searchFuncId  交易类号
	 * @param searchText    快速查询框查询提示
	 */
	public void setSearchConfig(String searchFuncId,String searchText) {
		this.searchFuncId = searchFuncId;
		this.searchText = searchText;
	}

	/**
	 * 设置 查询组件参数
	 * @param searchFuncId  交易类号
	 * @param searchText    快速查询框查询提示
	 * @param showPlanBox   是否显示方案查询功能
	 */
	public void setSearchConfig(String searchFuncId,String searchText,boolean showPlanBox) {
		this.searchFuncId = searchFuncId;
		this.searchText = searchText;
		this.showPlanBox = showPlanBox;
	}

	public String getSchemeSaveCallback() {
		return schemeSaveCallback;
	}


	public void setSchemeSaveCallback(String schemeSaveCallback) {
		this.schemeSaveCallback = schemeSaveCallback;
	}

	/**
	 * 添加自定义参数
	 */
	public void setCustomParamHM(HashMap customParamHM){
		this.customParamHM = customParamHM;
	}


	public String getModuleId() {
		return moduleId;
	}


	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

    
	public String getSchemePosition() {
		return schemePosition;
	}

	/**
	 * @param
	 *   TableConfigBuilder.SCHEME_POSITION_TITLE代表在title显示
     *  TableConfigBuilder.SCHEME_POSITION_TOOLBAR代表在toolbar显示
     *  TableConfigBuilder.SCHEME_POSITION_MENUBAR代表在menubar显示
     * @param  String> 组件id ：将栏目设置放到此组件(id所属组件)最后
     * @Description:   
     * @param @return 
     * @return String    
     * @throws
     */
	public void setSchemePosition(String schemePosition) {
		this.schemePosition = schemePosition;
	}


	public boolean isFieldAnalyse() {
		return fieldAnalyse;
	}


	public void setFieldAnalyse(boolean fieldAnalyse) {
		this.fieldAnalyse = fieldAnalyse;
	}

	public void setSchemePrivFields(String schemePrivFields) {
		this.schemePrivFields = schemePrivFields;
	}


	public void setBeforeLoadFunctionId(String beforeLoadFunctionId) {
		this.beforeLoadFunctionId = beforeLoadFunctionId;
	}
 
	public boolean isPageTool() {
		return isPageTool;
	}

	public void setPageTool(boolean isPageTool) {
		this.isPageTool = isPageTool;
	}
	public void setShowRowNumber(boolean showRowNumber) {
        this.showRowNumber = showRowNumber;
    } 
}
