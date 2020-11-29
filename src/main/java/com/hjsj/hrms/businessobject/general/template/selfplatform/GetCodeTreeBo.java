package com.hjsj.hrms.businessobject.general.template.selfplatform;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetCodeTreeBo {
	private Connection conn = null;
    private UserView userView = null;
    private boolean loadVorg=false;
    public GetCodeTreeBo(Connection conn,UserView userView) {
    	this.conn=conn;
    	this.userView=userView;
	}

    public void setLoadVorg(boolean loadVorg) {
		this.loadVorg = loadVorg;
	}

	public boolean isLoadVorg() {
		return loadVorg;
	}

	/**
	 * 极速加载代码模式
	 * @param codeSetId
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public ArrayList fastGetCodeItems(String codeSetId) {
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
			if (!code.getCodeitem().equalsIgnoreCase(code.getPcodeitem())) {
                continue;
            }
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
			treeitem.put("label",code.getCodename());
	    	treeitem.put("value",itemid);

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
			if (item.getCodeitem().equalsIgnoreCase(parentId)) {
                continue;
            }

			// 非parentId的子节点跳过
			if (!item.getPcodeitem().equalsIgnoreCase(parentId)) {
                continue;
            }
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
			treeitem.put("label",item.getCodename());
	    	treeitem.put("value",itemid);

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
    public boolean getSelectFlag(String codesetid) {
    	Integer leaf_only = 0;
    	RowSet rs=null;
    	try {
    		ArrayList valuelist = new ArrayList();
    		valuelist.add(codesetid);
    		ContentDAO dao = new ContentDAO(this.conn);
			String sql =" select leaf_node from codeset where codesetid = ?";
			rs = dao.search(sql,valuelist);
			if(rs.next()) {
                leaf_only = rs.getInt("leaf_node");
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return leaf_only==1?true:false;
	}

	public ArrayList getCodeListParams(String expandTop,String codesetid,
			String nodeid,String parentid, String ctrltype, String nmodule,boolean multiple,boolean doChecked,boolean onlySelectCodeset, int isShowLayer, Boolean isHideTip,Boolean showLevelDept,String checkedcodeids) throws Exception {

		ArrayList itemList = new ArrayList();
		boolean vorg = this.loadVorg;
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

//		//超级用户，不用判断权限，直接输出代码
//		if(userView.isSuper_admin())
//			return codeData.outCodeData(multiple,doChecked,expandTop,isHideTip,showLevelDept,checkedcodeids);

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


		if(codesstr.trim().length()<1) {
            return null;
        }
		boolean beParent =false;
		String searchCodes = "";
		String[] temp=codesstr.split("`");
		//查找出最上级的权限内代码
		HashMap map = getPrivMange(temp);
		int h=0;
		String parentidTemp="";
		for(int i=0;i<temp.length;i++)
		{
			String orgid = temp[i].substring(2);
			if(map.containsKey(orgid)) {
                continue;
            }
			h++;
			parentidTemp=orgid;
			searchCodes+="'"+orgid+"',";
			if(parentid.startsWith(orgid)) {
                beParent = true;
            }
		}
		parentid=StringUtils.isEmpty(parentid)?parentidTemp:parentid;
		beParent=(h==1||beParent)?true:beParent;
		searchCodes+="'code'";

		String codefilter = "";
		if("UN".equals(codesetid)) {
            codefilter+=" and codesetid<>'UM' and codesetid<>'@K' ";
        } else if("UM".equals(codesetid)) {
            codefilter+=" and codesetid<>'@K' ";
        }

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
		boolean isRecHistoryCode = AdminCode.isRecHistoryCode(codesetid);
    	ArrayList codeList = AdminCode.getCodeItemList(codesetid);
    	if("UM".equalsIgnoreCase(codesetid)){
    		codeList.addAll(AdminCode.getCodeItemList("UN"));
    	}else if("@K".equalsIgnoreCase(codesetid)){
    		codeList.addAll(AdminCode.getCodeItemList("UM"));
			codeList.addAll(AdminCode.getCodeItemList("UN"));
    	}
	    for(int k=0;k<codelist.size();k++){
	    	LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
	    	HashMap treeitem = new HashMap();
	    	String setid = ldb.get("codesetid").toString();
	    	treeitem.put("id",ldb.get("codeitemid"));
	    	treeitem.put("text", ldb.get("codeitemdesc"));
	    	treeitem.put("label", ldb.get("codeitemdesc"));
	    	treeitem.put("value", ldb.get("codeitemid"));
	    	treeitem.put("codesetid",setid);
	    	treeitem.put("itemdesc", ldb.get("codeitemdesc"));
	    	if(!isHideTip) {
                treeitem.put("qtip","ID:"+ldb.get("codeitemid"));
            }
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
			if("UM".equalsIgnoreCase(setid)){
				treeitem.put("icon","umicon.png");
			}else if("UN".equalsIgnoreCase(setid)){
				treeitem.put("icon","unicon.png");
			}else if("@K".equalsIgnoreCase(setid)){
				treeitem.put("icon","atkicon.png");
			}

	    	//是否叶子节点
	    	if(Integer.parseInt(ldb.get("cnum").toString())>0) {
                treeitem.put("leaf", Boolean.FALSE);
            } else {
                treeitem.put("leaf", Boolean.TRUE);
            }
	    	if(multiple) {
                treeitem.put("checked", false);
            }
	    	if(doChecked) {
                treeitem.put("checked", true);
            } else {
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

	    	// 递归加载子节点
	    	ArrayList childNodes = getChildCode(codeList, (String) ldb.get("codeitemid"),isRecHistoryCode);
	    	if (childNodes != null && childNodes.size()>0) {
	    		treeitem.put("leaf", Boolean.FALSE);
	    		treeitem.put("children", childNodes);
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
	    	if(!isHideTip) {
                treeitem.put("qtip","ID:"+ldb.get("codeitemid"));
            }
	    	treeitem.put("orgtype","vorg");
			//设置图片
			if("UM".equalsIgnoreCase(setid)){
				treeitem.put("icon","umicon.png");
			}else if("UN".equalsIgnoreCase(setid)){
				treeitem.put("icon","unicon.png");
			}else if("@K".equalsIgnoreCase(setid)){
				treeitem.put("icon","atkicon.png");
			}

	    	treeitem.put("leaf", Boolean.FALSE);
	    	if(multiple) {
                treeitem.put("checked", false);
            }
	    	if(doChecked) {
                treeitem.put("checked", true);
            } else {
	    		String itemid = (String) ldb.get("codeitemid");
    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
    				treeitem.put("checked", true);
	    		}
    		}
	    	// 递归加载子节点
	    	ArrayList childNodes = getChildCode(codeList, (String) ldb.get("codeitemid"),isRecHistoryCode);
	    	if (childNodes != null && childNodes.size()>0) {
	    		treeitem.put("leaf", Boolean.FALSE);
	    		treeitem.put("children", childNodes);
	    	}
	    	itemList.add(treeitem);
	    }


		return itemList;
	}

	public ArrayList getCurrentCodeList(String codesetid,String currentid,boolean multiple,boolean doChecked, String checkedcodeids){

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
		    	if("UN".equals(setid)) {
                    treeitem.put("icon","b_vroot.gif");
                } else if("UM".equals(setid)) {
                    treeitem.put("icon","/images/vdept.gif");
                } else {
                    treeitem.put("icon","/images/vpos_l.gif");
                }

		    	treeitem.put("leaf", Boolean.FALSE);
		    	if(multiple) {
                    treeitem.put("checked", false);
                }
		    	if(doChecked) {
                    treeitem.put("checked", true);
                } else {
		    		String itemid = (String) co.getCodeitem();
	    			if(StringUtils.isNotBlank(checkedcodeids)&&checkedcodeids.toLowerCase().indexOf("`"+itemid.toLowerCase()+"`")>-1&&multiple) {
	    				treeitem.put("checked", true);
		    		}
	    		}
		    	treeItems.add(treeitem);

		}

		return treeItems;

	}
	/**
	 * 迭代循环，添加label  value 属性
	 * @param treeItems
	 * @return
	 * @throws IOException
	 */
	public ArrayList addTextValue(ArrayList treeItems,String codesetid)
				throws IOException {
		for(int k=0;k<treeItems.size();k++){
			HashMap treeitem=(HashMap) treeItems.get(k);
			treeitem.put("label",(String)treeitem.get("text"));
	    	treeitem.put("value",(String)treeitem.get("id"));
	    	treeitem.put("codesetid",codesetid);
	    	if(treeitem.containsKey("icon")){
	    		if(StringUtils.equalsIgnoreCase((String) treeitem.get("codesetid"),"UN")){
					treeitem.put("icon","unicon.png");
				}else if(StringUtils.equalsIgnoreCase((String) treeitem.get("codesetid"),"UM")){
					treeitem.put("icon","umicon.png");
				}else if(StringUtils.equalsIgnoreCase((String) treeitem.get("codesetid"),"@K")){
					treeitem.put("icon","atkicon.png");
				}
			}
			if(treeitem.containsKey("children")){
				ArrayList childTreeItem=(ArrayList) treeitem.get("children");
				addTextValue(childTreeItem,codesetid);
			}
		}

		return treeItems;
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
