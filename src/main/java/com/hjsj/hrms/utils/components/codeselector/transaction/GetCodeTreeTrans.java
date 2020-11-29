package com.hjsj.hrms.utils.components.codeselector.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hjsj.hrms.utils.components.codeselector.utils.CreatCodeData;
import com.hjsj.hrms.utils.components.codeselector.utils.SearchCodeItems;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetCodeTreeTrans extends IBusiness {


	public void execute() throws GeneralException {
		//是否极速模式（从AdminCode中加载全部，无复杂逻辑判断，UN、UM、@K仍走数据库加载模式）
		boolean fast = false;

		String codesetid = (String)this.formHM.get("codesetid");
		String nodeid = (String)this.formHM.get("node"); // 要展开的节点id（codeitemid）
		String parentid = (String)this.formHM.get("parentid");
		parentid = parentid==null?"":parentid;
		Boolean isHideTip = (Boolean)this.formHM.get("isHideTip");//add by xiegh on date20180109 是否隐藏提示信息
		// 33945 linbz 20180113 该参数缺少默认值 为null在做处理时报错
		//true:隐藏提示 false不隐藏
		isHideTip = null==isHideTip?false:isHideTip;
		String currentid = (String)this.formHM.get("currentid");
		currentid = currentid==null?"":currentid;
		boolean vorg = false;
		if(this.formHM.containsKey("vorg"))
			vorg = (Boolean)this.formHM.get("vorg");
		this.getFormHM().put("loadVorg", vorg);
		boolean multiple = false;
		if(this.formHM.containsKey("multiple"))
			multiple = (Boolean)this.formHM.get("multiple");

		//xus 18/3/14 微信端部门是否多层级显示
		boolean showLevelDept = false;
		if(this.formHM.containsKey("showLevelDept"))
			showLevelDept = (Boolean)this.formHM.get("showLevelDept");

		boolean checkroot = false;
		if(this.formHM.containsKey("checkroot"))
			checkroot = (Boolean)this.formHM.get("checkroot");
		String searchtext = (String)this.formHM.get("searchtext");
		boolean isencrypt = false;
		if(this.formHM.containsKey("isencrypt"))
			isencrypt = (Boolean)this.formHM.get("isencrypt");//是否需要加密

		boolean expandTops = false;
		if(this.formHM.containsKey("expandTop"))
			expandTops = (Boolean)this.formHM.get("expandTop");
		String expandTop = Boolean.toString(expandTops);
		expandTop = "true".equals(expandTop)?"true":"false"; //是否展开，不设置为null=false

		//是否展开全部，默认不展开
		if(this.formHM.containsKey("fast"))
			fast = (Boolean)this.formHM.get("fast");
		
		String checkedcodeids = "";
		if(this.formHM.containsKey("checkedcodeids"))//选中的codeid  格式如D75YkeJaIHkPAATTP3HJDPAATTP`QFuwy1WrBD0PAATTP3HJDPAATTP 其中codeid是加密的
			checkedcodeids = (String)this.formHM.get("checkedcodeids");
		if(StringUtils.isNotBlank(checkedcodeids)){//解密
			String idsArray [] = checkedcodeids.split("`");
			String checkedids = "`";
			for (int i=0;i<idsArray.length;i++) {
				String org = PubFunc.decrypt(SafeCode.decode(idsArray[i]));
				checkedids+=org+"`";
			}
			checkedcodeids = checkedids;
		}
		boolean onlySelectCodeset = getSelectFlag(codesetid);//add by xiegh on date 20180319 是否设置“进末级代码项可选”
		if(("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)) && this.formHM.containsKey("onlySelectCodeset"))
			onlySelectCodeset = (Boolean)this.formHM.get("onlySelectCodeset");

		int isShowLayer = 0;
		if(this.formHM.containsKey("isShowLayer"))
			isShowLayer = (String)this.formHM.get("isShowLayer")==null?0:Integer.parseInt((String)this.formHM.get("isShowLayer"));

		ArrayList treeItems = new ArrayList();
		try{
			// 极速加载模式
			if (fast && !"UN".equalsIgnoreCase(codesetid) && !"UM".equalsIgnoreCase(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
				treeItems = fastGetCodeItems(codesetid);
				outPutTree(treeItems,isencrypt);
				return;
			}

			String codesource = (String)this.formHM.get("codesource");
			//加载自定义代码数据
			if(codesource!=null && codesource.length()>0){
				String classpath = "com.hjsj.hrms.utils.components.codeselector."+codesource;
				CodeDataFactory codebean = (CodeDataFactory) Class.forName(classpath).newInstance();
				//
				codebean.setParamsMap(this.formHM);
				if(searchtext != null && searchtext.length() > 0){
					searchtext = PubFunc.hireKeyWord_filter_reback(searchtext);
					// 前台进行encodeUrl加密，解密
					searchtext = java.net.URLDecoder.decode(searchtext, "UTF-8");
					treeItems = codebean.searchCodeByText(codesetid,searchtext,userView);
				}else{
					treeItems = codebean.createCodeData(codesetid, nodeid,
							userView);
				}
				outPutTree(treeItems,isencrypt);
				return;
			}

			nodeid = nodeid.replaceAll("root", "ALL");

			//如果加密，解密
			if(isencrypt==true&&!"ALL".equals(nodeid)){
				nodeid = PubFunc.decrypt(PubFunc.hireKeyWord_filter_reback(nodeid));
			}

			/**
			 * ctrltype
			 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
			 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
			 *         默认值为1
			 *  如果是普通代码类
			 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
			 */
			String ctrltype = (String)this.formHM.get("ctrltype");
			ctrltype = ctrltype==null || ctrltype.length()<1?"1":ctrltype;

			String nmodule = (String)this.formHM.get("nmodule");// 模块号

			//查询功能
			if (searchtext != null && searchtext.length() > 0) {

				searchtext = PubFunc.hireKeyWord_filter_reback(searchtext);
				// 前台进行encodeUrl加密，解密
				searchtext = java.net.URLDecoder.decode(searchtext, "UTF-8");
				//如果模糊时有传parentid，则模糊的项应根据parentid查 bug38117
				nodeid = "ALL".equals(nodeid)&&parentid!=null&&parentid.length()>0?parentid:nodeid;
				SearchCodeItems sci = new SearchCodeItems(codesetid,nodeid,
						searchtext,ctrltype,nmodule,multiple,userView);
				sci.setOnlySelectCodeset(onlySelectCodeset);
				sci.setParentid(parentid);
				sci.setLayerLevel(isShowLayer);
				sci.setHideTip(isHideTip);
				treeItems = sci.executeCodeSearch();
				outPutTree(treeItems,isencrypt);
				return;
			}


			//正常加载代码
			boolean doChecked = false;
			if("root".equals(nodeid) && checkroot)
				doChecked = true;

			if("ALL".equals(nodeid) && currentid.length()>0){
				treeItems = getCurrentCodeList(codesetid,currentid,multiple,doChecked,checkedcodeids);
			    	outPutTree(treeItems,isencrypt);
			    	return;
			}

			treeItems = getCodeListParams(expandTop,codesetid, nodeid,parentid,
					ctrltype, nmodule, multiple,doChecked,onlySelectCodeset,isShowLayer,isHideTip,showLevelDept,checkedcodeids);

			outPutTree(treeItems,isencrypt);


		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 极速加载代码模式
	 * @param codeSetId
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private ArrayList fastGetCodeItems(String codeSetId) {
		ArrayList nodes = new ArrayList();

		ArrayList codeList = AdminCode.getCodeItemList(codeSetId);
		//使用A0000进行排序
        Collections.sort(codeList, new Comparator<CodeItem>() {
            @Override
            public int compare(CodeItem o1, CodeItem o2) {
                int diff = o1.getA0000() - o2.getA0000();
                if (diff > 0) {
                    return 1;
                }else if (diff < 0) {
                    return -1;
                }
                return 0; //相等为0
            }
        });
        boolean isRecHistoryCode = AdminCode.isRecHistoryCode(codeSetId);
		for (int i=0; i<codeList.size(); i++) {
			CodeItem code = (CodeItem)codeList.get(i);
			// 寻找根节点
			if (!code.getCodeitem().equalsIgnoreCase(code.getPcodeitem()))
				continue;
			boolean isInvalid = validCode(isRecHistoryCode, code);
            if(!isInvalid) {
                continue;
            }
			HashMap treeitem = new HashMap();
			String itemid = code.getCodeitem().trim();
			treeitem.put("id", itemid);
			treeitem.put("text", code.getCodeitem().trim());
			treeitem.put("codesetid", code.getCodeid());
			treeitem.put("itemdesc", code.getCodename());
			treeitem.put("leaf", Boolean.TRUE);

			// 递归加载子节点
			ArrayList childNodes = getChildCode(codeList, code.getCodeitem(),isRecHistoryCode);
			if (childNodes != null && childNodes.size()>0) {
				treeitem.put("leaf", Boolean.FALSE);
			    treeitem.put("children", childNodes);
			}

			nodes.add(treeitem);
		}

		return nodes;
	}

	private ArrayList getChildCode(ArrayList codeList, String parentId, boolean isRecHistoryCode) {
		ArrayList nodes = new ArrayList();

		for (int i=0; i<codeList.size(); i++) {
			CodeItem item = (CodeItem)codeList.get(i);

			// 根节点跳过
			if (item.getCodeitem().equalsIgnoreCase(parentId))
				continue;

			// 非parentId的子节点跳过
			if (!item.getPcodeitem().equalsIgnoreCase(parentId))
				continue;
			boolean isInvalid = validCode(isRecHistoryCode, item);
            if(!isInvalid) {
                continue;
            }
			HashMap treeitem = new HashMap();
			String itemid = item.getCodeitem().trim();
			treeitem.put("id", itemid);
			treeitem.put("text", item.getCodeitem().trim());
			treeitem.put("codesetid", item.getCodeid());
			treeitem.put("itemdesc", item.getCodename());
			treeitem.put("leaf", Boolean.TRUE);

			ArrayList childNodes = getChildCode(codeList, item.getCodeitem(),isRecHistoryCode);
			if (childNodes != null && childNodes.size()>0) {
				treeitem.put("leaf", Boolean.FALSE);
			    treeitem.put("children", childNodes);
			}

			nodes.add(treeitem);
		}

		return nodes;
	}
    /**
     * 判定代码指标是否有效
     * @param isRecHistoryCode 是否记录历史记录
     * @param code 代码值
     * @return isInvalid 指标是否有效
     */
    private boolean validCode(boolean isRecHistoryCode,CodeItem code) {
        boolean isInvalid = true;//默认代码有效
      //进行代码过滤，去掉无效的代码
        if(!isRecHistoryCode) {//如果不是记录历史的代码类,直接根据invalid
            int invalid = code.getInvalid();
            if(invalid==0) {//等于0是无效代码
                isInvalid = false;
            }
        }else {
            Date endDate = code.getEndDate();//结束日期
            Date startDate = code.getStartDate();//开始日期
            Date now = new Date();
            if(now.getTime()>endDate.getTime()||now.getTime()<startDate.getTime()) {//大于结束日期小于开始日期
                isInvalid = false;
            }
        }
        return isInvalid;
    }
    private boolean getSelectFlag(String codesetid) {
    	Integer leaf_only = 0;
    	try {
    		ArrayList valuelist = new ArrayList();
    		valuelist.add(codesetid);
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql =" select leaf_node from codeset where codesetid = ?";
			frowset = dao.search(sql,valuelist);
			if(frowset.next())
				leaf_only = frowset.getInt("leaf_node");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(frowset);
		}
		return leaf_only==1?true:false;
	}

	private ArrayList getCodeListParams(String expandTop,String codesetid,
			String nodeid,String parentid, String ctrltype, String nmodule,boolean multiple,boolean doChecked,boolean onlySelectCodeset, int isShowLayer, Boolean isHideTip,Boolean showLevelDept,String checkedcodeids) throws Exception {
		
		ArrayList itemList = new ArrayList();
		boolean vorg = (Boolean)this.getFormHM().get("loadVorg");
		String newNodeId = "ALL".equals(nodeid)&&parentid!=null&&parentid.length()>0?parentid:nodeid;
		CreatCodeData codeData = new CreatCodeData(codesetid, newNodeId);
		codeData.setVorg(vorg);
		codeData.setOnlyLeafNode(onlySelectCodeset);
		codeData.setLayerLevel(isShowLayer);
		//如果不控制权限，直接输出代码
		if("0".equals(ctrltype) || userView==null){
			//codeXml 默认是过滤普通代码的有效无效的，这里设置一下过滤
			//默认不显示无效代码，如果ctrltype=0 则显示
			codeData.setIsValidCtr("0".equals(ctrltype)?"0":"1");
			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);
		}
		
		//如果不是机构代码，不用判断权限，直接输出代码
		boolean isOrg = "UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid);
		if(!isOrg){
			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);
		}
		
		/**下面的都是机构代码了 **/
		
		//超级用户，不用判断权限，直接输出代码
		if(userView.isSuper_admin())
			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);
		
		//获取权限
		String codesstr = "";
		if ("1".equals(ctrltype)) {// 管理范围  update by xiegh 项目38320  人员范围设置成组织机构时，且为UN时，取顶级单位权限
				codesstr = userView.getManagePrivCode() +  userView.getManagePrivCodeValue() + "`";  //管理范围获取到的codeid没有UM或@K，添加两位字符方便下面处理数据，不影响其他的使用  changxy 20160526
		} else if ("2".equals(ctrltype)) {// 操作单位
					codesstr = userView.getUnit_id();
		} else if (nmodule.length() > 0 && "3".equals(ctrltype)) {// 业务范围
					codesstr = userView.getUnitIdByBusi(nmodule);
		} else {
				throw new Exception("获取代码参数出错：ctrytype 或  nmodule ");
		}
		
		//如果权限有UN`说明有所有权限
		if(codesstr.indexOf("UN`")!=-1 || userView.isSuper_admin()){//如果 不是刚进入  或者 是超级用户 
			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);
		}
		
		//nodeid不等于ALL是展开代码，不控制权限(代码不走高级权限)
		if(!"ALL".equals(nodeid)){
			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);
		}
		
		
		
		/** 走到这里 就代表是 第一次加载树 并且是机构代码，并且需要权限控制*/
		
		
		if(codesstr.trim().length()<1)
			return null;
		
		boolean beParent =false;
		String searchCodes = "";
		String[] temp=codesstr.split("`");
		//查找出最上级的权限内代码
		HashMap map = getPrivMange(temp);
		for(int i=0;i<temp.length;i++)
		{
			String orgid = temp[i].substring(2);
			if(map.containsKey(orgid))
				continue;
			searchCodes+="'"+orgid+"',";
			if(parentid.startsWith(orgid))
				beParent = true;
					
		}
		
		searchCodes+="'code'";
		
		String codefilter = "";
		if("UN".equals(codesetid))
			codefilter+=" and codesetid<>'UM' and codesetid<>'@K' ";
		else if("UM".equals(codesetid))
			codefilter+=" and codesetid<>'@K' ";
		
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid,codeitemdesc,(select count(1) from organization where parentid=A.codeitemid ");
		sql.append(codefilter);
		sql.append(") cnum from organization A where ");
		if(beParent){
			sql.append("parentid ='"+parentid+"'");
		}else{
			sql.append("codeitemid in (");
			sql.append(searchCodes);
			sql.append(")");
			sql.append(" and parentid like '"+parentid+"%' ");
		}
		//bug 35599 没有过滤失效的单位部门岗位。
		if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    String backdate =sdf.format(new Date());
			sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		}
		sql.append(codefilter);
		//xus 18/5/15 机构按照a0000排序
		sql.append(" order by a0000 ");
		
		List codelist = ExecuteSQL.executeMyQuery(sql.toString());
		
	    for(int k=0;k<codelist.size();k++){
	    	LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
	    	HashMap treeitem = new HashMap();
	    	String setid = ldb.get("codesetid").toString();
	    	treeitem.put("id",ldb.get("codeitemid"));
	    	treeitem.put("text", ldb.get("codeitemdesc"));
	    	treeitem.put("codesetid",setid);
	    	treeitem.put("itemdesc", ldb.get("codeitemdesc"));
	    	if(!isHideTip)
	    		treeitem.put("qtip","ID:"+ldb.get("codeitemid"));
	    	String layerdesc = "";
    		if("UM".equalsIgnoreCase(setid)&&isShowLayer>0){
				CodeItem item=AdminCode.getCode("UM",ldb.get("codeitemid").toString(),isShowLayer);
				if(item!=null){
					layerdesc = item.getCodename();
        		}else{
    	    		layerdesc = AdminCode.getCodeName(setid,ldb.get("codeitemid").toString());
    	    	}
			}
    		treeitem.put("layerdesc", layerdesc);
	    	//设置图片
	    	if("UN".equals(setid))
	    		treeitem.put("icon","/images/unit.gif");
			else if("UM".equals(setid))
				treeitem.put("icon","/images/dept.gif");
			else
				treeitem.put("icon","/images/pos_l.gif");
	    	
	    	//是否叶子节点
	    	if(Integer.parseInt(ldb.get("cnum").toString())>0)
	    		treeitem.put("leaf", Boolean.FALSE);
	    	else
	    		treeitem.put("leaf", Boolean.TRUE);
	    	if(multiple)
    			treeitem.put("checked", false);
	    	if(doChecked)
    			treeitem.put("checked", true);
	    	else {
	    		String itemid = (String) ldb.get("codeitemid");
    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
    				treeitem.put("checked", true);
	    		}
    		}
	    	//56581 简单查询设置仅末级代码可选，非su用户添加此控制
	    	if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
    			if(onlySelectCodeset && !codesetid.equals(setid)){//UM UN @K 兼容多级单位或部门也可以选择
    				treeitem.remove("checked");
    			}
    		}
	    	
	    	itemList.add(treeitem);
	    }
	    
	    if(!vorg){
			return itemList;
		}
	    
	    String vsql = sql.toString().replaceAll("organization", "vorganization");
	    codelist = ExecuteSQL.executeMyQuery(vsql);
	    
	    for(int k=0;k<codelist.size();k++){
	    	LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
	    	HashMap treeitem = new HashMap();
	    	String setid = ldb.get("codesetid").toString();
	    	treeitem.put("id",ldb.get("codeitemid"));
	    	treeitem.put("text", ldb.get("codeitemdesc"));
	    	treeitem.put("codesetid",setid);
	    	treeitem.put("itemdesc", ldb.get("codeitemdesc"));
	    	if(!isHideTip)
	    		treeitem.put("qtip","ID:"+ldb.get("codeitemid"));
	    	treeitem.put("orgtype","vorg");
	    	//设置图片
	    	if("UN".equals(setid))
	    		treeitem.put("icon","b_vroot.gif");
			else if("UM".equals(setid))
				treeitem.put("icon","/images/vdept.gif");
			else
				treeitem.put("icon","/images/vpos_l.gif");
	    	
	    	treeitem.put("leaf", Boolean.FALSE);
	    	if(multiple)
    			treeitem.put("checked", false);
	    	if(doChecked)
    			treeitem.put("checked", true);
	    	else {
	    		String itemid = (String) ldb.get("codeitemid");
    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
    				treeitem.put("checked", true);
	    		}
    		}
	    	itemList.add(treeitem);
	    }
	    
	    
		return itemList;
	}
	
	private ArrayList getCurrentCodeList(String codesetid,String currentid,boolean multiple,boolean doChecked, String checkedcodeids){
		
		ArrayList treeItems = new ArrayList();
		String[] currentids = currentid.split(",");
		
		for(int i=0;i<currentids.length;i++){
			String codeid = currentids[i];
			CodeItem co;
			if("@K".equals(codesetid)){
				co = AdminCode.getCode("@K", codeid);
				co = co==null?AdminCode.getCode("UM", codeid):co;
				co = co==null?AdminCode.getCode("UN", codeid):co;
			}else if("UM".equals(codesetid)){
				co = AdminCode.getCode("UM", codeid);
				co = co==null?AdminCode.getCode("UN", codeid):co;
			}else if("UN".equals(codesetid)){
				co = AdminCode.getCode("UN", codeid);
			}else{
				co = AdminCode.getCode(codesetid, codeid);
			}
			if(co==null){
				continue;
			}
				
			
			HashMap treeitem = new HashMap();
		    	String setid = co.getCodeid();
		    	treeitem.put("id",co.getCodeitem());
		    	treeitem.put("text",co.getCodename());
		    	treeitem.put("codesetid",setid);
		    	//设置图片
		    	if("UN".equals(setid))
		    		treeitem.put("icon","b_vroot.gif");
				else if("UM".equals(setid))
					treeitem.put("icon","/images/vdept.gif");
				else
					treeitem.put("icon","/images/vpos_l.gif");
		    	
		    	treeitem.put("leaf", Boolean.FALSE);
		    	if(multiple)
	    			treeitem.put("checked", false);
		    	if(doChecked)
	    			treeitem.put("checked", true);
		    	else {
		    		String itemid = (String) co.getCodeitem();
	    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
	    				treeitem.put("checked", true);
		    		}
	    		}
		    	treeItems.add(treeitem);
			
		}
		
		return treeItems;
		
	}
	
	private void outPutTree(ArrayList treeItems ,boolean isencrypt)
				throws IOException {
		//添加加密id过程，可通过参数控制 
		if(isencrypt==true&&treeItems.size()>0){//需要加密
			HashMap treeitem = new HashMap();
			String id = "";
			for(int i=0;i<treeItems.size();i++){
				treeitem = (HashMap)treeItems.get(i);
				id = (String)treeitem.get("id");
				id = PubFunc.encrypt(id);
				treeitem.put("id", id);
				//判断是否有子节点 如果设置默认展开顶级，会有子节点
				ArrayList childrenList = (ArrayList) treeitem.get("children");
				if(childrenList!=null&&childrenList.size()>0) {
					for(int j=0;j<childrenList.size();j++) {
						HashMap treeitem_ = (HashMap)childrenList.get(j);
						String id_ = (String)treeitem_.get("id");
						id_ = PubFunc.encrypt(id_);
						treeitem_.put("id", id_);
					}
				}
			}
		}
		
		this.formHM.put("children", treeItems);
	}
	
	private HashMap getPrivMange(String[] temp)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<temp.length;i++)
			{
				String str=temp[i].substring(2);
				buf.append("`"+str);
				for(int j=0;j<temp.length;j++)
				{
					String str2=temp[j].substring(2);;
					if(!str2.equalsIgnoreCase(str)&&str2.startsWith(str))
					{
						map.put(str2, str2);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
