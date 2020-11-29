package com.hjsj.hrms.module.recruitment.position.businessobject;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.IRecruitCheck;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 简历筛选器
 * <p>Title: ResumeFilterBo </p>
 * <p>Description: 负责职位简历过滤器的创建、维护和简历筛选</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-23 下午01:42:34</p>
 * @author zhaoxj
 * @version 1.0
 */
public class ResumeFilterBo {
    private Connection con = null;
    private UserView userview = null;
    private ContentDAO dao = null;
    private String z0301;
    
    private ResumeFilterBo() {
        
    }
    
    public ResumeFilterBo(Connection con,UserView userview) {
        this.con = con;
        this.userview = userview;
        this.dao = new ContentDAO(this.con);
    }
    
    /**
     * 为职位筛选合格简历
     * @Title: filterResume   
     * @Description:    
     * @param postionId
     * @return
     */
    public boolean filterResume(String postionId) {
        boolean ok = true;
        
        return ok;
    }
    
    /**
     * 得到以json格式组装的简历筛选器数据
     * @Title: getFilterJson   
     * @Description: 将筛选器数据包装成json，供前端使用   
     * @param postionId
     * @return 返回值格式：
     * {
     *     "1":[{
     *         "name":"简历筛选器1",
     *         "rule":[
     *             {
     *                 "itemid":"A0107",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"A",
     *                 "codesetid":"AX",
     *                 "datatype": "",
     *                 "queryvalue1":"1,2",
     *                 "queryvalue2":""
     *             },
     *             {
     *                 "itemid":"C0101",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"N",
     *                 "codesetid":"0",
     *                 "datatype": "",
     *                 "queryvalue1":"30",
     *                 "queryvalue2":"40"
     *             }
     *         ]
     *     }],
     *     "2":[{
     *         "name":"简历筛选器2",
     *         "rule":[
     *             {
     *                 "itemid":"A0111",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"A",
     *                 "codesetid":"0",
     *                 "datatype": "0",
     *                 "queryvalue1":"北京 上海",
     *                 "queryvalue2":""
     *             },
     *             {
     *                 "itemid":"A0111",
     *                 "fieldsetid":"A01",
     *                 "itemtype":"A",
     *                 "codesetid":"0",
     *                 "datatype": "0",
     *                 "queryvalue1":"北京 上海",
     *                 "queryvalue2":""
     *             }
     *         ]
     *     }],
     * }
     * @throws GeneralException 
     */
    public String getFilterJson(String postionId) throws GeneralException {
        StringBuffer filter = new StringBuffer();
        //查询对应职位下所有过滤
        StringBuffer sql = new StringBuffer("select * from zp_resume_filter filter");
        sql.append(" where filter.zp_pos_id=?");
        sql.append(" order by filter.id asc");
        ArrayList values = new ArrayList();
        values.add(postionId);
        
        RowSet rs = null;
        HashMap hm = null;
        //用来存放最外层json对象 属性，即 "1" "2"等
        HashMap filterHM = null;
        int num = 1;
        try{
        	rs = dao.search(sql.toString(),values);
        	filterHM = new HashMap();
        	int num2 = 1;
        	while(rs.next()){
        		hm = new HashMap();
        		hm.put("name", rs.getString("filter_name"));
        		hm.put("rule", this.getFilterRules(rs.getString("id"),num2));
        		filterHM.put("s"+num, hm);
        		num++;
        		num2++;
        	}
        	filter.append(JSONArray.fromObject(filterHM).toString());
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }finally{
    		PubFunc.closeResource(rs);
    	}
        return filter.toString();
    }
    /**
     * 查询指定简历筛选器规则
     * @param filterId    筛选器id
     * @return
     * @throws GeneralException
     */
    public ArrayList getFilterRules(String filterId,int num2) throws GeneralException{
    	ArrayList res = new ArrayList();
    	
    	if(StringUtils.isEmpty(filterId))
    		return res;
    	
    	StringBuffer ruleSql = new StringBuffer(" select * from zp_resume_filter_rule ru");
    	ruleSql.append(" where ru.filter_id=?");
    	ruleSql.append(" order by ru.displayid asc");
    	ArrayList values = new ArrayList();
        values.add(filterId);
    	
    	RowSet rs = null;
    	LazyDynaBean bean = null;
    	String codesetid = "";
    	try{
    		rs = dao.search(ruleSql.toString(), values);
    		int num = 0;
    		while(rs.next()){
    			bean = new LazyDynaBean();
    			codesetid = rs.getString("codesetid");
    			
    			bean.set("filterid", rs.getString("filter_id"));
        		bean.set("fieldsetid", rs.getString("fieldsetid"));
        		bean.set("itemid", rs.getString("itemid"));
        		bean.set("itemdesc", rs.getString("itemdesc"));
        		bean.set("itemtype", rs.getString("itemtype"));
        		if(StringUtils.isNotEmpty(rs.getString("itemtype"))&&"D".equals(rs.getString("itemtype"))){
     			   int intType = Integer.parseInt(rs.getString("datetype"))+1;//将该数字加一
     		       bean.set("flag", String.valueOf(intType));
     		    }
        		bean.set("codesetid", codesetid);
        		//只有一级代码,且数量不超过5个
				if (!"0".equalsIgnoreCase(codesetid)
						&& StringUtils.isNotEmpty(codesetid)
						&& this.getLevelCount(codesetid, "first") == this.getLevelCount(codesetid, "all")
						&& this.getLevelCount(codesetid, "first") <= 5){
					bean.set("showway", "stretch");
					bean.set("items", this.getFirstCodeitem(codesetid,rs.getString("itemid"),rs.getString("queryvalue1")));
				}
        			
        		bean.set("datetype", rs.getString("datetype"));
        		if("UN,UM,@K".contains(codesetid.toUpperCase()))
        			bean.set("queryvalue1",this.decryptStr(rs.getString("queryvalue1"),2));
        		else
        			bean.set("queryvalue1", rs.getString("queryvalue1") == null ? "" : rs.getString("queryvalue1"));
        		bean.set("queryvalue2", rs.getString("queryvalue2") == null ? "" : rs.getString("queryvalue2"));
        		bean.set("displayid", rs.getString("displayid"));
        		
        		if(!"0".equalsIgnoreCase(codesetid) && StringUtils.isNotEmpty(codesetid)){
        			bean.set("showHTML",this.getDeepCodeHtml(codesetid, num2,num, rs.getString("queryvalue1")));
        		}
        		num++;
        		res.add(bean);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeResource(rs);
    	}
    	return res;
    }
    /**
     * 将已选的代码转为html
     * @param codesetid 
     * @param i 筛选器序号
     * @param num 本筛选器的第n个指标
     * @param codeitemids  格式    ,'01','02'
     * @return
     * @throws GeneralException
     */
    public ArrayList getDeepCodeHtml(String codesetid,int i,int num, String codeitemids) throws GeneralException{
    	ArrayList result = new ArrayList();
    	if(StringUtils.isEmpty(codeitemids))
    		return result;
    	
    	StringBuffer res = new StringBuffer("");
    	StringBuffer codeitemid = new StringBuffer("");
    	if(codeitemids.startsWith(",")){
    		codeitemids = codeitemids.substring(1, codeitemids.length());
    	}
    	String[] split = codeitemids.split(",");
    	for(int n = 0;n<split.length;n++){
    		codeitemid.append("'"+split[n]+"',");
    	}
    	codeitemid.setLength(codeitemid.length()-1);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append("select * from ");
    	if("UM,UN,@K".contains(codesetid.toUpperCase()))
    	    sql.append(" organization");
    	else
    	    sql.append(" codeitem");
    	sql.append(" where codesetid='"+codesetid+"'");
    	sql.append(" and codeitemid in ("+codeitemid.toString()+")");
    	sql.append(this.getInOrder(codeitemids,"codeitemid"));
    	
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql.toString());
    		String id = "";
    		while(rs.next()){
    			res.setLength(0);
    			res.append("<span style='display:inline-block'><dl style='float: left;padding-top:10px;padding-right:15px;text-align:center;color:black;margin-left:5px'>");
    			if("UM,UN,@K".contains(codesetid.toUpperCase()))
    				id = PubFunc.encrypt(rs.getString("codeitemid"))+"panel"+"textfield"+i+num;
    			else
    				id = rs.getString("codeitemid")+"panel"+"textfield"+i+num;//代码型删除id 生成规则 筛选计数器+在筛选器中的序号
    			res.append("<dt onmouseover='resumeFilter.onMouseover(this)' onmouseleave='resumeFilter.onMouseleave(this)' id='"+id+"' >" +rs.getString("codeitemdesc")
								+"<img style='display:none;width: 15px; height: 15px;float:left;' class='deletePic' onclick='resumeFilter.removeInfo(this,\""+id+"\",\"textfield"+i+num+"\")' src='/workplan/image/remove.png'>"
								+"</dt>");
    			res.append("<dt style='display:none' id='label"+id+"'>"+id+"</dt>");
    			res.append("</dl></span>");
    			result.add(res.toString());
    		}
    	}catch(Exception e){
    		e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeResource(rs);
    	}
    	return result;
    }
    /**
     * 指定代码一级代码个数
     * @param codesetid
     * @param flag   first  查询一级代码           all  所有代码
     * @return
     * @throws GeneralException
     */
    public int getLevelCount(String codesetid,String flag) throws GeneralException{
    	int res = 0;
    	
    	StringBuffer sql = new StringBuffer("select COUNT(*) num from codeitem where 1=1");
    	//单位部门岗位特殊处理
    	if("UM,UN,@K".contains(codesetid.toUpperCase())){
    		sql = new StringBuffer("select count(*) num from organization where 1=1");
    	}
    	if("first".equalsIgnoreCase(flag))
    		sql.append(" and codeitemid = parentid "); 
    	sql.append(" and codesetid=?");
    	
    	ArrayList values = new ArrayList();
    	values.add(codesetid);
    	
    	RowSet rs = null;
    	try{
    		rs = dao.search(sql.toString(), values);
    		if(rs.next())
    			res = rs.getInt("num");
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeResource(rs);
    	}
    	return res;
    }
    /**
     * 获取指定codesetid对应的所有一级codeitem
     * @param codesetid
     * @param itemid  指标代码
     * @param alreadyExits  已经保存的指标
     * @return
     * @throws GeneralException
     */
    public ArrayList getFirstCodeitem(String codesetid,String itemid,String alreadyExits) throws GeneralException{
    	ArrayList res = new ArrayList();
    	
    	StringBuffer sql = new StringBuffer("select codeitemid,codeitemdesc,codesetid from codeitem");
		sql.append(" where codeitemid = parentid "); 
    	sql.append(" and codesetid=?");
    	sql.append(" union all select codeitemid,codeitemdesc,codesetid from organization");
    	sql.append(" where codeitemid = parentid "); 
    	sql.append(" and codesetid=?");
    	ArrayList values = new ArrayList();
    	values.add(codesetid);
    	values.add(codesetid);
    	
    	RowSet rs = null;
    	try{
    		LazyDynaBean cd = null;
    		rs = dao.search(sql.toString(), values);
    		while(rs.next()){
    			cd = new LazyDynaBean();;
    			cd.set("boxLabel",rs.getString("codeitemdesc"));
    			cd.set("name", itemid);
    			cd.set("inputValue",rs.getString("codeitemid"));
    			cd.set("uncheckedValue","0");
    			//已选的打上勾
    			if(!StringUtils.isEmpty(alreadyExits) && alreadyExits.contains(rs.getString("codeitemid")))
    				cd.set("checked", true);
    				
    			res.add(cd);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
    		PubFunc.closeResource(rs);
    	}
    	return res;
    }
    /**
     * 保存筛选器设置
     * @Title: saveFilter   
     * @Description:    
     * @param postionId 职位编号
     * @param filterId 筛选器编号(页面和数据库都存在的id)
     * @param itemid 筛选器指标id
     * @param jsonObj 筛选器规则json对象 
     * @return
     * @throws GeneralException 
     */
    public boolean saveFilter(String postionId, String filterId, String itemid, JSONObject jsonObj) throws GeneralException {
        boolean ok = true;
        //只有当存在  页面和数据库都存在的筛选器时才会将页面不存在，数据库还存在的进行删除
        if(!StringUtils.isEmpty(filterId))
        	this.deleteFilter(postionId, filterId);
        //json对象中没有filterid属性 或为空则说明是新建
        if(StringUtils.isEmpty(jsonObj.getString("filterid")))
        	ok = this.addFilter(postionId, itemid, jsonObj);
        else
        	ok = this.updateFilter(postionId, filterId, itemid, jsonObj);
        
        return ok;
    }
    /**
     * 新增筛选器
     * @param postionId
     * @param itemid
     * @param jsonObj
     * @throws GeneralException
     */
    public boolean addFilter(String postionId, String itemid, JSONObject jsonObj) throws GeneralException{
    	boolean res = false;
    	String filterSql = "insert into zp_resume_filter values(?,?,?,?,?)";
        String itemSql = "insert into zp_resume_filter_rule(filter_id,fieldsetid,itemid,itemdesc,itemtype,codesetid,datetype,queryvalue1,queryvalue2,displayid) values(?,?,?,?,?,?,?,?,?,?)";
        
        //筛选器id
        IDGenerator idGenerator = new IDGenerator(2,this.con);
        String filterId = idGenerator.getId("zp_resume_filter.id");
        Date date = new Date();
		Timestamp create_time = new Timestamp(date.getTime());
        
        ArrayList filterList = new ArrayList();
        filterList.add(filterId);
        filterList.add(postionId);
        filterList.add(jsonObj.getString("name"));
        filterList.add(create_time);
        filterList.add(this.userview.getUserName());
        try{
        	dao.insert(filterSql, filterList);
        	
        	
            ArrayList values = new ArrayList();
            
            String[] temp = itemid.split("`");
            String format = null;
            Object obj = null;
            ArrayList temValue = new ArrayList();
            HashMap itemInfo = null;
            String codesetid = "";
            StringBuffer checkValue = new StringBuffer("");
            for (int i = 0; i < temp.length; i++) {
            	checkValue.setLength(0);
            	values.clear();
            	values.add(filterId);
            	itemInfo = this.getItemInfo(temp[i]);
            	if(itemInfo == null)
            	    continue;
            	
            	values.add(itemInfo.get("fieldsetid"));
            	values.add(temp[i]);
            	values.add(itemInfo.get("itemdesc"));
            	values.add(itemInfo.get("itemtype"));
            	values.add(itemInfo.get("codesetid"));
            	
            	format = "";
            	//只有日期型指标包括 --datetype属性
            	if(jsonObj.containsKey(temp[i]+"datetype"))
            		format = StringUtils.isEmpty(jsonObj.getString(temp[i]+"datetype")) ? "" : jsonObj.getString(temp[i]+"datetype");
            	values.add(format);
            	
            	obj = jsonObj.get(temp[i]);
            	if(obj == null)
    				continue;
            	
            	codesetid = (String) itemInfo.get("codesetid");
            	//判断获取的值是否为数组
            	if("net.sf.json.JSONArray".equals(obj.getClass().getName())){
            		temValue = (ArrayList) JSONArray.toCollection(JSONArray.fromObject(obj));
            		//判断是否是日期指标
            		if(!StringUtils.isEmpty(format)){
            			if("1".equals(format)){
            				values.add(temValue.get(0));
            				values.add(temValue.get(1));
            			}else{
            				if(temValue.get(0)==null)
            					values.add(this.formatDateString((String) temValue.get(0), format));
            				else {
            					String objName = (temValue.get(0)).getClass().getName();
                			    if("java.lang.String".equalsIgnoreCase(objName)) {
                			    	values.add(this.formatDateString((String) temValue.get(0), format));
                			    }else if("java.lang.Integer".equalsIgnoreCase(objName)) {
                			    	values.add(this.formatDateString(String.valueOf(temValue.get(0)) , format));
                			    }
            				}
            				
            				if(temValue.get(1)==null)
            					values.add(this.formatDateString((String) temValue.get(1), format));
            				else {
            					String objName = (temValue.get(1)).getClass().getName();
                			    if("java.lang.String".equalsIgnoreCase(objName)) {
                			    	values.add(this.formatDateString((String) temValue.get(1), format));
                			    }else if("java.lang.Integer".equalsIgnoreCase(objName)) {
                			    	values.add(this.formatDateString(String.valueOf(temValue.get(1)) , format));
                			    }
            				}
            			}
					} else if (this.getLevelCount(codesetid, "first") == this.getLevelCount((String) itemInfo.get("codesetid"), "all")
							      && this.getLevelCount(codesetid, "first") <= 5
							      &&!"0".equalsIgnoreCase(codesetid)
									&& StringUtils.isNotEmpty(codesetid)) {//针对罗列出来的代码指标项
						for (int j = 0; j < temValue.size(); j++) {
							if(StringUtils.isEmpty((String) temValue.get(j)))
								continue;
							checkValue.append(temValue.get(j)+",");
						}
						if(checkValue.length()>1)
							checkValue.setLength(checkValue.length()-1);
    					
            			values.add(checkValue.toString());
            			values.add("");
            		}else{//区间数值型指标
            			values.add(temValue.get(0));
            			values.add(temValue.get(1));
            		}
            	}else{
            		if("UN,UM,@K".contains(codesetid.toUpperCase()))
            			values.add(this.decryptStr((String) obj,1));
            		else
            			values.add(obj);
            		
            		values.add("");
            	}
            	values.add(i);
            	dao.insert(itemSql, values);
    		}
            res = true;
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
        
        return res;
    }
    /**
     * 解密或加密传递过来的字符串
     * @param encodeStr   格式  **,**,
     * @flag 1 解密  其他加密
     * @return
     */
    public String decryptStr(String encodeStr,int flag){
        if (StringUtils.isEmpty(encodeStr))
            return "";
        
    	StringBuffer res = new StringBuffer(",");
    	
    	String [] arr = encodeStr.split(",");
    	for (int i = 0; i < arr.length; i++) {
    		if(StringUtils.isEmpty(arr[i]))
    			continue;
    		if(flag == 1)
    			res.append(PubFunc.decrypt(arr[i])+",");
    		else
    			res.append(PubFunc.encrypt(arr[i])+",");
		}
    	if(res.length() >= 1)
    		res.setLength(res.length()-1);
    	
    	return res.toString();
    }
    /**
     * 更新筛选器
     * @param postionId
     * @param filterId
     * @param itemid
     * @param jsonObj
     * @throws GeneralException
     */
    public boolean updateFilter(String postionId, String filterId, String itemid, JSONObject jsonObj) throws GeneralException{
    	boolean res = false;
    	String filterSql = "update zp_resume_filter set filter_name=? where id=?";
        StringBuffer itemSql = new StringBuffer("update zp_resume_filter_rule set queryvalue1=?,queryvalue2=?,datetype=? where filter_id=? and itemid=?");
        
        ArrayList filterList = new ArrayList();
        filterList.add(jsonObj.getString("name"));
        filterList.add(jsonObj.getString("filterid"));
        
        try{
    		dao.update(filterSql, filterList);
    		
    		//更新指标
    		ArrayList values = new ArrayList();
    		String[] temp = itemid.split("`");
    		Object obj = null;
    		String format = null;
    		ArrayList temValue = new ArrayList();
    		HashMap itemInfo = null;
    		String codesetid = "";
    		StringBuffer checkValue = new StringBuffer("");
    		for (int i = 0; i < temp.length; i++) {
    			checkValue.setLength(0);
    			values.clear();
    			obj = jsonObj.get(temp[i]);
    			if(obj == null)
    				continue;
    			
    			itemInfo = this.getItemInfo(temp[i]);
    			if(itemInfo == null)
    			    continue;
    			
    			format = "";
    			//只有日期型指标包括 --datetype属性
    			if(jsonObj.containsKey(temp[i]+"datetype"))
    				format = StringUtils.isEmpty(jsonObj.getString(temp[i]+"datetype")) ? "" : jsonObj.getString(temp[i]+"datetype");
    			
    			codesetid = (String) itemInfo.get("codesetid");
    			//判断获取的值是否为数组
    			if("net.sf.json.JSONArray".equals(obj.getClass().getName())){
    				temValue = (ArrayList) JSONArray.toCollection(JSONArray.fromObject(obj));
    				//判断是否是日期指标
    				if(!StringUtils.isEmpty(format)){
    					if("1".equals(format)||"2".equals(format)||"3".equals(format)){
            				values.add(temValue.get(0)==null?"":temValue.get(0));
            				values.add(temValue.get(1)==null?"":temValue.get(1));
            				if(temValue.get(0)!=null || temValue.get(1)!=null) {
            					if((Integer)temValue.get(0) >(Integer)temValue.get(1)) {
               					    return res;
            					}
           				    }
            			}else{
            				values.add(this.formatDateString((String) temValue.get(0), format));
            				values.add(this.formatDateString((String) temValue.get(1), format));
            			}
    				} else if (this.getLevelCount(codesetid, "first") == this.getLevelCount(codesetid, "all")
							      && this.getLevelCount(codesetid, "first") <= 5
							      &&!"0".equalsIgnoreCase(codesetid)
									&& StringUtils.isNotEmpty(codesetid)) {//针对罗列出来的代码指标项
    					for (int j = 0; j < temValue.size(); j++) {
							if(StringUtils.isEmpty((String) temValue.get(j)))
								continue;
							checkValue.append(temValue.get(j)+",");
						}
    					if(checkValue.length()>1)
    						checkValue.setLength(checkValue.length()-1);
    					
            			values.add(checkValue.toString());
            			values.add("");
            		}else{//区间数值型指标
    					values.add(temValue.get(0));
    					values.add(temValue.get(1));
    				}
    			}else{
    				if("UN,UM,@K".contains(codesetid.toUpperCase()))
    					values.add(this.decryptStr((String) obj,1));
            		else
            			values.add(obj);
    				values.add("");
    			}
    			values.add(format);
    			values.add(jsonObj.getString("filterid"));
    			values.add(temp[i]);
    			
    			dao.update(itemSql.toString(), values);
    		}
    		res = true;
    	}catch(Exception e){
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
        
        return res;
    }
    /**
     * 获取指定格式的日期字符串
     * @param dateString
     * @param format   0--年月日   1--年   2--月  3--日
     * @return
     */
    public String formatDateString(String dateString,String format){
    	String res = "";
    	if(StringUtils.isEmpty(dateString) || StringUtils.isEmpty(format))
    		return res;
    	//最多只保留到日
    	if(dateString.length() > 10)
    		dateString = dateString.substring(0,10);
    	
//    	if("1".equals(format))
//    		res = dateString.substring(0,4);
//    	else if("2".equals(format))
//    		res = dateString.substring(5,7);
//    	else if("3".equals(format))
//    		res = dateString.substring(8);
//    	else
			res = dateString;
    	
    	return res;
    }
    /**
     * 获取指标信息
     * @param itemid
     * @return
     * @throws GeneralException
     */
    private HashMap getItemInfo(String itemid) throws GeneralException{
        HashMap itemInfo = null;
    	FieldItem item =  DataDictionary.getFieldItem(itemid);
		if(item != null){
		    itemInfo = new HashMap();
		    itemInfo.put("fieldsetid", item.getFieldsetid());
		    itemInfo.put("itemdesc", item.getItemdesc());
		    itemInfo.put("itemtype", item.getItemtype());
		    itemInfo.put("codesetid", item.getCodesetid());
		}
    	return itemInfo;
    }
    /**
     * 删除筛选器
     * @Title: deleteFilter   
     * @Description:    
     * @param postionId 职位编号
     * @param filterId 格式为      1,2,3…… 是多个id组成的
     * @return
     * @throws GeneralException 
     */
    public boolean deleteFilter(String postionId, String filterId) throws GeneralException {
        boolean ok = false;
        String sql = "delete from zp_resume_filter where id not in("+filterId+") and zp_pos_id = '"+postionId+"'";
        
        StringBuffer ruleSql = new StringBuffer();
        ruleSql.append("delete from zp_resume_filter_rule");
        ruleSql.append(" where exists(select 1 from zp_resume_filter A");
        ruleSql.append(" where A.id=zp_resume_filter_rule.filter_id");
        ruleSql.append(" and A.id not in("+filterId+")");
        ruleSql.append(" and A.zp_pos_id = '"+postionId+"')");
        try{
        	dao.update(ruleSql.toString());
        	dao.update(sql);
        	ok = true;
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
        return ok;
    }
    /**
     * 设置筛选指标
     * @param positionId
     * @param itemid
     * @param jsonArray
     * @throws GeneralException
     */
    public void addFilterItems(String positionId,String itemid,JSONArray jsonArray) throws GeneralException{
    	String filterIds = this.getPostionFilterId(positionId);
    	//首次设置指标进行保存
    	if (jsonArray.isEmpty())
    	    this.deleteFilter(positionId, "-1");
    	else if(StringUtils.isEmpty(filterIds) || StringUtils.isEmpty(itemid))
   		    this.saveFilterAndItems(positionId, jsonArray);
    	else
    		this.resortFilterItems(filterIds, itemid, jsonArray);
    }
    /**
     * 首次设置筛选指标时进行保存
     * @param positionId
     * @param totalCount
     * @param jsonArray
     * @throws GeneralException
     */
    public void saveFilterAndItems(String positionId,JSONArray jsonArray) throws GeneralException{
    	StringBuffer filterSql = new StringBuffer();
    	filterSql.append("insert into zp_resume_filter(id,zp_pos_id,filter_name,create_time,create_user)");
    	filterSql.append(" values(?,?,?,?,?)");
        
        //筛选器id
        IDGenerator idGenerator = new IDGenerator(2,this.con);
        String filterId = idGenerator.getId("zp_resume_filter.id");
        
        ArrayList filterList = new ArrayList();
        filterList.add(filterId);
        filterList.add(positionId);
        filterList.add("简历筛选");
        //修改时间类型
        filterList.add(DateUtils.getSqlDate(new Date()));
        filterList.add(this.userview.getUserName());
        try{
        	dao.insert(filterSql.toString(), filterList);
        	//保存指标
        	JSONObject obj = null;
        	ArrayList values = new ArrayList();
        	for (int i = 0; i < jsonArray.size(); i++) {
    			obj = jsonArray.getJSONObject(i);

    			values.clear();
    			values.add(filterId);
				values.add(obj.get("fieldsetid"));
				values.add(obj.get("itemid"));
				values.add(obj.get("itemdesc"));
				values.add(obj.get("itemtype"));
				values.add(obj.get("codesetid"));
				values.add("0");
				values.add("");
				values.add("");
				values.add(i+1);
				
				this.addFilterItem(values);
        	}
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 获取指定职位下所有的筛选器id
     * @param positionId
     * @return   
     * @throws GeneralException
     */
    public String getPostionFilterId(String positionId) throws GeneralException{
    	StringBuffer res = new StringBuffer("");
    	
    	String sql = "select id from zp_resume_filter where zp_pos_id = '"+positionId+"'";
    	RowSet rs = null;
    	try{
        	rs = dao.search(sql);
        	while(rs.next())
        		res.append(rs.getInt("id")+",");
        	if(res.length()>1)
        		res.setLength(res.length()-1);
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
        return res.toString();
    }
    /**
     * 重新整理过滤器对应的指标
     * @param filterIds 格式---1,2,3
     * @param itemid
     * @param jsonArray
     * @throws GeneralException
     */
    public void resortFilterItems(String filterIds,String itemid,JSONArray jsonArray) throws GeneralException{
    	JSONObject obj = null;
    	String currentItem = "";
    	ArrayList values = new ArrayList();
    	for (int i = 0; i < jsonArray.size(); i++) {
			obj = jsonArray.getJSONObject(i);
			currentItem = obj.getString("itemid");
			//更新
			if(itemid.contains(currentItem))
				this.updateFilterItemsOrder(i+1, filterIds, currentItem);
			else{//新增
				for (int j = 0; j < filterIds.split(",").length; j++) {
					if(StringUtils.isEmpty(filterIds.split(",")[j]))
						continue;
					values.clear();
					values.add(filterIds.split(",")[j]);
					values.add(obj.get("fieldsetid"));
					values.add(currentItem);
					values.add(obj.get("itemdesc"));
					values.add(obj.get("itemtype"));
					values.add(obj.get("codesetid"));
					values.add("0");
					values.add("");
					values.add("");
					values.add(i+1);
					
					this.addFilterItem(values);
				}
			}
			//移除已存在的指标，余下的即为需要删除的指标
			itemid = itemid.replace(currentItem,"");
		}
    	StringBuffer delItemIds = new StringBuffer("");
    	//若执行上述操作后itemid不为空，则将剩余的指标删除 
    	if(StringUtils.isNotEmpty(itemid)){
    		for (int i = 0; i < itemid.split("`").length; i++) {
				if(StringUtils.isEmpty(itemid.split("`")[i]))
					continue;
				delItemIds.append("'"+itemid.split("`")[i]+"',");
			}
    		if(delItemIds.length()>1){
    			delItemIds.setLength(delItemIds.length()-1);
    			this.deleteFilterItem(filterIds, delItemIds.toString());
    		}
    	}
    }
    /**
     * 调整已存在指标的顺序
     * @param order
     * @param filterid 格式---1,2,3
     * @param itemid
     * @throws GeneralException
     */
    public void updateFilterItemsOrder(int order,String filterid,String itemid) throws GeneralException{
    	String sql = "update zp_resume_filter_rule set displayid="+order+" where filter_id in("+filterid+") and itemid='"+itemid+"'";
    	
    	try{
        	dao.update(sql);
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 新增指标
     * @param values
     * @throws GeneralException
     */
    public void addFilterItem(ArrayList values) throws GeneralException{
    	String sql = "insert into zp_resume_filter_rule(filter_id,fieldsetid,itemid,itemdesc,itemtype,codesetid,datetype,queryvalue1,queryvalue2,displayid) values(?,?,?,?,?,?,?,?,?,?)";
    	if(values ==  null || values.size() == 0)
    		return;
    	
    	try{
        	dao.insert(sql,values);
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 删除指标
     * @param filterids 格式---1,2,3
     * @param itemid   格式--'A0107','C0101'
     * @throws GeneralException
     */
    public void deleteFilterItem(String filterids,String itemid) throws GeneralException{
    	String sql = "delete from zp_resume_filter_rule where filter_id in("+filterids+") and itemid in("+itemid+")";
    	
    	try{
        	dao.update(sql);
        }catch(Exception e){
        	e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
        }
    }
    /***
     * 通过传入人员编号更改简历筛选符合度
    * @Title:updateByA0100
    * @Description：
    * @author xiexd
    * @date 2016-1-13
    * @param a0100
     */
    public void updateByA0100(String a0100)
    {
    	if(a0100!=null&&!"".equals(a0100))
    	{    		
    		ArrayList z03list = this.getZ03(a0100);
    		this.updateSuitable(z03list,a0100);
    	}
    }
    
    /***
     * 获取当前人员所申请的所有职位
    * @Title:getZ03
    * @Description：
    * @author xiexd
    * @date 2016-1-13
    * @param a0100
    * @return
     */
    private ArrayList getZ03(String a0100)
    {
    	ArrayList z03List = new ArrayList();
    	ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        try {
        	
    		StringBuffer sql = new StringBuffer();
    		sql.append(" select zp_pos_id from zp_pos_tache where a0100=? group by zp_pos_id");
    		ArrayList value = new ArrayList();
    		value.add(a0100);
			rs = dao.search(sql.toString(),value);
			while(rs.next())
			{
				z03List.add(rs.getString("zp_pos_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return z03List;
    }
    /**
     * 
    * @Title:updateSuitable 更改简历符合指标
    * @Description：
    * @author xiexd
    * @date 2016-1-12
    * @param a0100s
     */
    public void updateSuitable(ArrayList zp_pos_ids,String a0100)
    {
    	ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        StringBuffer a0100str = new StringBuffer();
        RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String nbase="";  //应聘人员库
		if(vo!=null)
			nbase=vo.getString("str_value"); 
    	try {
    		String zp_pos_id = "";
    		//循环职位
    		for(int j=0;j<zp_pos_ids.size();j++)
    		{
    			a0100str.setLength(0);
    			StringBuffer sql = new StringBuffer();
    			zp_pos_id = zp_pos_ids.get(j).toString();
    			//如果传入有a0100，则只更改当前传入的人员信息
    			if(a0100==null|| "".equals(a0100))
    			{    				
    				//将所有简历置为不符合状态
    				sql.append("update zp_pos_tache set suitable='0' where zp_pos_id='"+zp_pos_id+"'");
    				dao.update(sql.toString());
    				a0100str.append("select a0100 from zp_pos_tache where zp_pos_id='"+zp_pos_id+"'");
    			}else{
    				//将当前职位下的该简历置为不符合状态
    				sql.append("update zp_pos_tache set suitable='0' where a0100='"+a0100+"' and nbase='"+nbase+"' and zp_pos_id='"+zp_pos_id+"'");
    				dao.update(sql.toString());
    				a0100str.append("'"+a0100+"'");
    			}
    			sql = new StringBuffer();
    			ArrayList filterId = this.getFilterId(zp_pos_id);
    			if(filterId.size()>0)
    			{
					//sql语句
					sql.append(this.getFilterTables(filterId));
					sql.append(" and zp_pos_tache.zp_pos_id='"+zp_pos_id+"'");
					sql.append(" and zp_pos_tache.a0100 in("+a0100str+") and nbase='"+nbase+"'");
					dao.update(sql.toString());
    			}else{
    				//当当前职位没有简历筛选规则时，则所有人的简历都为符合状态
    				sql.append("update zp_pos_tache set suitable='1' where nbase='"+nbase+"' and zp_pos_id='"+zp_pos_id+"'");
    				dao.update(sql.toString());
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
    }
    
    /**
     * 获取当前职位下所有的人员信息
    * @Title:getA0100
    * @Description：
    * @author xiexd
    * @date 2016-1-12
    * @param zp_pos_id
    * @return
     */
    private ArrayList getA0100(String zp_pos_id)
    {
    	ArrayList a0100List = new ArrayList();
    	ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        try {
        	
    		StringBuffer fieldSetSql = new StringBuffer();
    		fieldSetSql.append(" select a0100 from zp_pos_tache where zp_pos_id=?");
    		ArrayList value = new ArrayList();
    		value.add(zp_pos_id);
			rs = dao.search(fieldSetSql.toString(),value);
			while(rs.next())
			{
				a0100List.add(rs.getString("a0100"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return a0100List;
    }
    /***
     * 获取当前职位下所有的筛选器编号
    * @Title:getFilterId
    * @Description：
    * @author xiexd
    * @date 2016-1-12
    * @param zp_pos_id
    * @return
     */
    public ArrayList getFilterId(String zp_pos_id)
    {
    	ArrayList filterList = new ArrayList();
    	ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        try {
        	
    		StringBuffer fieldSetSql = new StringBuffer();
    		fieldSetSql.append(" select id from zp_resume_filter where zp_pos_id=?");
    		ArrayList value = new ArrayList();
    		value.add(zp_pos_id);
			rs = dao.search(fieldSetSql.toString(),value);
			while(rs.next())
			{
				filterList.add(rs.getString("id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return filterList;
    }
    
    /***
     * 拼接简历筛选需要的sql
    * @Title:getFilterTables
    * @Description：
    * @author xiexd
    * @date 2016-1-12
    * @param filterId 筛选器编号
    * @return
     */
    private String getFilterTables(ArrayList<String> filterId)
    {
    	StringBuffer sql = new StringBuffer();
    	ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        try {
        	RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
    		String nbase="";  //应聘人员库
    		if(vo!=null)
    			nbase=vo.getString("str_value"); 

    		String tableA01 = nbase+"A01";
    		sql.append("update zp_pos_tache set zp_pos_tache.suitable=1");
    		sql.append(" where zp_pos_tache.a0100 in (");
    		sql.append(" select "+tableA01+".a0100 ");
    		sql.append(" from " + tableA01);
    		StringBuffer fieldSetSql = new StringBuffer();
    		fieldSetSql.append(" select fieldsetid from zp_resume_filter_rule where Filter_id="+filterId.get(0)+" and fieldsetid<>'A01' group by fieldsetid ");
			rs = dao.search(fieldSetSql.toString());
			String tableName = "";
			while(rs.next()) {
				tableName = nbase+rs.getString("fieldsetid");
				sql.append(" left join "+tableName+" on "+tableA01+".a0100="+tableName+".a0100 ");
			}
			
			sql.append(" where "+this.getFilterWhr(filterId.get(0).toString()));
            for(int i=1;i<filterId.size();i++) {
                sql.append(" or "); 
                sql.append(this.getFilterWhr(filterId.get(i).toString()));
            }
            
            sql.append(")");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return sql.toString();
    }
    /**
     * 将简历筛选器简析并组装成sql条件
     * @Title: getFilterWhr   
     * @Description:    
     * @param filterId
     * @return
     */
    public String getFilterWhr(String filterId) {
        StringBuffer whr = new StringBuffer();
        ContentDAO dao = new ContentDAO(con);
        RowSet rs = null;
        try {
        	RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
    		String nbase="";  //应聘人员库
    		if(vo!=null)
    			nbase=vo.getString("str_value"); 
			StringBuffer itemSql = new StringBuffer();
			itemSql.append(" select fieldsetid,itemid,itemtype,codesetid,datetype,queryvalue1,queryvalue2 from zp_resume_filter_rule where Filter_id="+filterId);
			rs = dao.search(itemSql.toString());
			String fieldsetid = "";//子集id
			String itemid = "";//指标id
			String itemtype = "";//指标类型
			String codesetid = "";//代码类
			String datetype = "";//日期查询方式
			String queryvalue1 = "";//查询值1
			String queryvalue2 = "";//查询值2
			while(rs.next())
			{
				fieldsetid = rs.getString("fieldsetid");//子集id
				itemid = rs.getString("itemid");//指标id
				itemtype = rs.getString("itemtype");//指标类型
				codesetid = rs.getString("codesetid");//代码类
				datetype = rs.getString("datetype");//日期查询方式
				queryvalue1 = rs.getString("queryvalue1");//查询值1
				queryvalue2 = rs.getString("queryvalue2");//查询值2
				if((StringUtils.equalsIgnoreCase(queryvalue1, "")&&StringUtils.equalsIgnoreCase(queryvalue2, ""))||(StringUtils.equalsIgnoreCase(queryvalue1, null)&&StringUtils.equalsIgnoreCase(queryvalue2, null)))
				{
					continue;
				}
				whr.append(" and (");
				whr.append(getSqlByDateType(nbase, fieldsetid, itemid, itemtype, codesetid, datetype, queryvalue1,queryvalue2));
				whr.append(")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		if(whr.length()==0)
		{
			whr.append(" (1=1)");
		}else{
			whr = new StringBuffer("("+whr.substring(5, whr.length())+")");
		}
        return whr.toString();
    }

	/**
	 * 根据筛选指标获取对应sql条件
	 * @param nbase
	 * @param fieldsetid
	 * @param itemid
	 * @param itemtype
	 * @param codesetid
	 * @param datetype
	 * @param queryvalue1
	 * @param queryvalue2
	 * @return
	 */
	public String getSqlByDateType(String nbase, String fieldsetid, String itemid, String itemtype,
			String codesetid, String datetype, String queryvalue1, String queryvalue2) {
		StringBuffer whr = new StringBuffer();
		if(StringUtils.equalsIgnoreCase(itemtype, "A"))
		{
			//文本型
			if(!StringUtils.equalsIgnoreCase(codesetid, "0"))
			{
				boolean onlyOne = isMultiLevel(codesetid);
				String[] value;
				value = queryvalue1.split(",");
				for(int i=0;i<value.length;i++)
				{
					if(onlyOne){
						if(StringUtils.isEmpty(value[i]))
							continue;
						whr.append(" "+nbase+fieldsetid+"."+itemid+" like '"+value[i]+"%' or");
					}else{
						whr.append(" "+nbase+fieldsetid+"."+itemid+" = '"+value[i]+"' or");
					}
				}
				whr.setLength(whr.length()-2);
			}else{						
				whr.append(" "+nbase+fieldsetid+"."+itemid+"='"+queryvalue1+"'");
			}
		}else if(StringUtils.equalsIgnoreCase(itemtype, "D"))
		{
			//日期类型
			if(StringUtils.equalsIgnoreCase(datetype, "0"))//日期
			{
				if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" " + Sql_switcher.dateToChar(nbase+fieldsetid+"."+itemid) + " between '"+queryvalue1+"' and '"+queryvalue2+"'");
				}else if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+nbase+fieldsetid+"."+itemid+">'"+queryvalue1+"'");
				}else if(StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+nbase+fieldsetid+"."+itemid+"<'"+queryvalue2+"'");
				}
			}else if(StringUtils.equalsIgnoreCase(datetype, "1"))//年限
			{
				if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.diffYears(Sql_switcher.today(),nbase+fieldsetid+"."+itemid)+" between '"+queryvalue1+"' and '"+queryvalue2+"'");
//							whr.append(" "+Sql_switcher.year(nbase+fieldsetid+"."+itemid)+" between '"+queryvalue1+"' and '"+queryvalue2+"'");
				}else if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.diffYears(Sql_switcher.today(),nbase+fieldsetid+"."+itemid)+">='"+queryvalue1+"'");
//							whr.append(" "+Sql_switcher.year(nbase+fieldsetid+"."+itemid)+">'"+queryvalue1+"'");
				}else if(StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.diffYears(Sql_switcher.today(),nbase+fieldsetid+"."+itemid)+"<='"+queryvalue2+"'");
				}
			}else if(StringUtils.equalsIgnoreCase(datetype, "2"))//月份
			{
				if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.month(nbase+fieldsetid+"."+itemid)+" between '"+queryvalue1+"' and '"+queryvalue2+"'");
				}else if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.month(nbase+fieldsetid+"."+itemid)+">='"+queryvalue1+"'");
				}else if(StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.month(nbase+fieldsetid+"."+itemid)+"<='"+queryvalue2+"'");
				}
			}else if(StringUtils.equalsIgnoreCase(datetype, "3"))//天
			{
				if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.day(nbase+fieldsetid+"."+itemid)+" between '"+queryvalue1+"' and '"+queryvalue2+"'");
				}else if(!StringUtils.equalsIgnoreCase(queryvalue1, "")&&StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.day(nbase+fieldsetid+"."+itemid)+">='"+queryvalue1+"'");
				}else if(StringUtils.equalsIgnoreCase(queryvalue1, "")&&!StringUtils.equalsIgnoreCase(queryvalue2, ""))
				{
					whr.append(" "+Sql_switcher.day(nbase+fieldsetid+"."+itemid)+"<='"+queryvalue2+"'");
				}
			}
		}else if(StringUtils.equalsIgnoreCase(itemtype, "N"))
		{
			//整型
			if(!StringUtils.isEmpty(queryvalue1)){
				whr.append(" "+nbase+fieldsetid+"."+itemid+">="+queryvalue1);
			}
			
			if(!StringUtils.isEmpty(queryvalue2)){
				if(!StringUtils.isEmpty(queryvalue1))
					whr.append(" and ");
				whr.append(" "+nbase+fieldsetid+"."+itemid+"<="+queryvalue2);
			}
			
		}else if(StringUtils.equalsIgnoreCase(itemtype, "M"))
		{
			//大文本型
			whr.append(" "+nbase+fieldsetid+"."+itemid+"='"+queryvalue1+"'");
		}
		return whr.toString();
	}
    /**
     * 加载多选指标组件的下拉框
     * @return
     * @throws GeneralException
     */
    public ArrayList getFieldSet() throws GeneralException{
		ArrayList list = new ArrayList();
    	StringBuffer sql = new StringBuffer();
    	try {
    		sql.append("select fieldSetId,fieldsetdesc");
    		   
    		sql.append(" from t_hr_busitable");
    		sql.append(" where FieldSetId = 'Z03'");
   		    sql.append("  or FieldSetId = 'Z05'");
    		sql.append("order by displayorder");
    		RowSet rs = dao.search(sql.toString());
    		while(rs.next()) {
    			HashMap hm = new HashMap();
    			hm.put("fieldsetid", rs.getString("fieldSetId"));
    			hm.put("fieldsetdesc", rs.getString("fieldsetdesc"));
    			list.add(hm);
    		}
    		
    	    HashMap hm = new HashMap();
    	    hm.put("fieldsetid", "sysRecruit_item");
    	    hm.put("fieldsetdesc", "自定义指标（招聘）");
    	    list.add(hm);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return list;
    	
    }

	/**
	 * 加载自定义的指标
	 * @param value
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldItems(String value) throws GeneralException {
		ArrayList itemList = new ArrayList();
		try {
			if("sysRecruit_item".equals(value))
			{
				HashMap hm = new HashMap();
				hm.put("itemid", "contactPerson");
				hm.put("itemdesc", "联系人");
				hm.put("itemtype", "A");
				hm.put("fieldsetid", "sys");
				hm.put("codesetid", "0");
				itemList.add(hm);
				
				hm = new HashMap();
				hm.put("itemid", "phoneNo");
				hm.put("itemdesc", "联系人电话");
				hm.put("itemtype", "A");
				hm.put("fieldsetid", "sys");
				hm.put("codesetid", "0");
				itemList.add(hm);

				hm = new HashMap();
				hm.put("itemid", "humanDepartment");
				hm.put("itemdesc", "联系人部门");
				hm.put("itemtype", "A");
				hm.put("fieldsetid", "sys");
				hm.put("codesetid", "0");
				itemList.add(hm);
				
				hm = new HashMap();
				hm.put("itemid", "company");
				hm.put("itemdesc", "联系人单位");
				hm.put("itemtype", "A");
				hm.put("fieldsetid", "sys");
				hm.put("codesetid", "0");
				itemList.add(hm);
				
				hm = new HashMap();
				hm.put("itemid", "sendDate");
				hm.put("itemdesc", "发件日期");
				hm.put("itemtype", "D");
				hm.put("fieldsetid", "sys");
				hm.put("codesetid", "0");
				itemList.add(hm);

			}else{
				StringBuffer sql = new StringBuffer("select * from ");
				sql.append(value);
				sql.append(" where 1=2");
				RowSet rs = dao.search(sql.toString());
				ResultSetMetaData metadata = rs.getMetaData();
				String itemid = "";
				String itemdesc = "";
				String itemtype = "";
				String codesetid = "";
				String fieldsetid = "";
				for(int i = 1;i<=metadata.getColumnCount();i++){
					itemid = metadata.getColumnName(i).toLowerCase();
					if(DataDictionary.getFieldItem(itemid)==null||DataDictionary.getFieldItem(itemid).toString().length()==0){
						continue;
					}
					itemdesc = DataDictionary.getFieldItem(itemid).getItemdesc();
					itemtype = DataDictionary.getFieldItem(itemid).getItemtype();
					codesetid = DataDictionary.getFieldItem(itemid).getCodesetid();
					fieldsetid = DataDictionary.getFieldItem(itemid).getFieldsetid();
					HashMap hm = new HashMap();
					hm.put("itemid", itemid);
					hm.put("itemdesc", itemdesc);
					hm.put("itemtype", itemtype);
					hm.put("fieldsetid", fieldsetid);
					hm.put("codesetid",codesetid);
					itemList.add(hm);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}
	/**
	 * 字段fieldName按inStr中值出现的顺序排序
	 * @param inStr  in中的值 如 03,01,02
	 * @param fieldName  排序的字段
	 * @return 
	 */
	private String getInOrder(String inStr,String fieldName){
		StringBuffer res = new StringBuffer("");
		//获取当前数据库
		int base = Sql_switcher.searchDbServer();
		switch (base) {
		case Constant.MSSQL:
			res.append(" order by charindex("+fieldName+",'"+inStr+"')");
			break;
		case Constant.ORACEL:
			res.append(" order by instr ('"+inStr+"',"+fieldName+")");
			break;
		default://其他的数据库暂时不清楚
			break;
		}
		return res.toString();
	}
	
	/**
	 * 判断是否多层代码
	 * false 单层代码，true 多层
	 * @param codeSetId
	 * @return
	 */
	private boolean isMultiLevel(String codeSetId){
		RowSet search = null;
		int num = 0;
		boolean flag = false;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) num from codeitem where codesetid='"+codeSetId+"'");
			sql.append(" and parentid<>childid");
			search = dao.search(sql.toString());
			if(search.next())
				num = search.getInt("num");
			if(num>0)
				flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(search);
		}
		return flag;
	}
	
	/**
	 * 招聘根据应聘职位校验应聘人员是否符合筛选条件
	 * 如果符合则返回人员id
	 * 不符合如果是多个筛选器返回筛选器名称，只有一个筛选器返回具体指标
	 * 推荐职位时返回查询符合筛选条件人员id的sql
	 * @param z0301
	 * @param a0100 //推荐进来不需要传
	 * @param nbase
	 * @param from apply=应聘，recommend=推荐
	 */
	public ArrayList<String> ruleFilter(String z0301, String a0100, String nbase,String from) {
		RowSet rs = null;
		ArrayList<String> infos = new ArrayList<String>();
		try {
			ArrayList<String> filterId = this.getFilterId(z0301);
			String tableA01 = nbase+"A01";
			StringBuffer sql = new StringBuffer();
			sql.append("select "+tableA01+".a0100 from ");
			sql.append(tableA01);
			StringBuffer fieldSetSql = new StringBuffer();
			String filterWheSql = "";
			if(filterId.size()>0) {
				filterWheSql = this.getFilterWheSql(z0301, filterId);
				fieldSetSql.append(" select fieldsetid from zp_resume_filter_rule ");
				fieldSetSql.append(" where Filter_id="+filterId.get(0)+" and fieldsetid<>'A01' group by fieldsetid ");
				rs = dao.search(fieldSetSql.toString());
				String tableName = "";
				while(rs.next()) {
					tableName = nbase+rs.getString("fieldsetid");
					sql.append(" left join "+tableName+" on "+tableA01+".a0100="+tableName+".a0100 ");
				}
			}else
				filterWheSql = " 1=1 ";
			sql.append(" where "+filterWheSql);
			if("apply".equals(from)) {
				sql.append(" and "+tableA01+".a0100 =?");
				ArrayList<String> value = new ArrayList<String>();
				value.add(a0100);
				rs = dao.search(sql.toString(), value);
				while(rs.next())
					infos.add(rs.getString("a0100"));
			}else
				infos.add(sql.toString());
			
			//没有符合条件的人
			if("apply".equals(from)&&infos.size()==0)
				infos = getCauseOfFailure(z0301, a0100, nbase);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return infos;
	}
	
	/**
	 * 根据招聘职位获取过滤条件
	 * @param z0301
	 * @param filterId 
	 * @return
	 */
	public String getFilterWheSql(String z0301, ArrayList<String> filterId) {
		StringBuffer sql = new StringBuffer(" (1=2 ");
        for(int i=0;i<filterId.size();i++) {
            sql.append(" or "); 
            sql.append(this.getFilterWhr(filterId.get(i).toString()));
        }
        sql.append(")");
		return sql.toString();
	}
	
	/**
	 * 校验不符合条件的指标
	 * @param z0301
	 * @param a0100
	 * @param nbase
	 * @return
	 */
	public ArrayList<String> getCauseOfFailure(String z0301, String a0100, String nbase) {
		RowSet rs = null;
		RowSet search = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			ArrayList<String> filterIds = this.getFilterId(z0301);
			StringBuffer sql = new StringBuffer();
			if(filterIds.size()>1) {
				sql.append("select filter_name from zp_resume_filter ");
				sql.append(" where id in(");
				for(int i = 0;i<filterIds.size();i++) {
					sql.append(filterIds.get(i)+",");
				}
				sql.setLength(sql.length()-1);
				sql.append(")");
				rs = dao.search(sql.toString());
				while(rs.next()) {
					list.add(rs.getString("filter_name"));
				}
			}else {
				sql.append("select fieldsetid,itemid,Itemdesc,itemtype,codesetid,datetype,queryvalue1,queryvalue2 ");
				sql.append(" from zp_resume_filter_rule where Filter_id = "+filterIds.get(0));
				rs = dao.search(sql.toString());
				String fieldsetid = "";//子集id
				String itemid = "";//指标id
				String itemDesc = "";//指标id
				String itemtype = "";//指标类型
				String codesetid = "";//代码类
				String datetype = "";//日期查询方式
				String queryvalue1 = "";//查询值1
				String queryvalue2 = "";//查询值2
				String tableA01 = nbase+"A01";
				sql.setLength(0);
				sql.append("select "+tableA01+".a0100 from ");
				sql.append(tableA01);
				ArrayList<String> value = new ArrayList<String>();
				value.add(a0100);
				StringBuffer leftSql = new StringBuffer();
				while(rs.next())
				{
					leftSql.setLength(0);
					fieldsetid = rs.getString("fieldsetid");//子集id
					itemid = rs.getString("itemid");//指标id
					itemDesc = rs.getString("Itemdesc");//指标名称
					itemtype = rs.getString("itemtype");//指标类型
					codesetid = rs.getString("codesetid");//代码类
					datetype = rs.getString("datetype");//日期查询方式
					queryvalue1 = rs.getString("queryvalue1");//查询值1
					queryvalue2 = rs.getString("queryvalue2");//查询值2
					leftSql.append(sql);
					if(!"A01".equalsIgnoreCase(fieldsetid))
						leftSql.append(" left join "+nbase+fieldsetid+" on "+tableA01+".a0100="+nbase+fieldsetid+".a0100 ");
					leftSql.append(" where "+tableA01+".a0100 =?");
					if(StringUtils.isNotEmpty(queryvalue1)||StringUtils.isNotEmpty(queryvalue2)) {
						String sqlByDateType = this.getSqlByDateType(nbase, fieldsetid, itemid, itemtype, codesetid, datetype, queryvalue1,queryvalue2);
						search = dao.search(leftSql.toString()+" and ("+sqlByDateType+")", value);
						if(!search.next()) {
							list.add(itemDesc);
							break;
						}
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(search);
		}
		return list;
	}
	
	/**
	 * 根据职位id获取
	 * 不符合职位筛选规则是否允许申请 1 不允许，0允许
	 * @param z0301
	 * @return
	 */
	public boolean getApplyControl(String z0301) {
		RowSet rs = null;
		boolean apply_control = true;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select apply_control from Z03 where Z0301=?");
			ArrayList<String> value = new ArrayList<String>();
			value.add(z0301);
			rs = dao.search(sql.toString(), value);
			if(rs.next())
				apply_control = 1==rs.getInt("apply_control");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return apply_control;
	}
	
	/**
	 * 校验个性化应聘资格
	 * @param nbase
	 * @param a0100
	 * @param posId
	 * @return 为空:校验通过；不为空：校验不通过，返回信息即为不通过原因。
	 */
	public String checkApplyQualify(String nbase, String a0100, String posId) {
	    String checkMsg = "";
	    String checkerClassPath = SystemConfig.getPropertyValue("recruit_examinee_checker");
	    try {
	    	if(StringUtils.isEmpty(checkerClassPath))
	    		return checkMsg;
			IRecruitCheck checker = (IRecruitCheck) Class.forName(checkerClassPath).newInstance();
			checkMsg = checker.check(nbase, a0100, posId);
            } catch (Exception e) {
                e.printStackTrace();
            }
	    
	    return checkMsg;
	}
	public String checkApplyQualifyForA0100s(String nbase, String a0100, String posId) {
	    String checkMsg = "";
	    String checkerClassPath = SystemConfig.getPropertyValue("recruit_examinee_checker");
	    try {
	    	//没配置特殊筛选器
	    	if(StringUtils.isEmpty(checkerClassPath))
	    		return a0100;
			IRecruitCheck checker = (IRecruitCheck) Class.forName(checkerClassPath).newInstance();
				checkMsg = checker.checkA0100s(nbase, a0100, posId);
            } catch (Exception e) {
                e.printStackTrace();
            }
	    
	    return checkMsg;
	}
	public String getDBPre() {
		//应聘人员库前缀
		String dbname="";  
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbname;
	}
	/**
	 * 获取人岗匹配sql语句
	 * @param column 
	 * @param dbname
	 * @param resultA0100s 符合筛选条件并且考试合格的候选人
	 * @return
	 */
	public String getQueryDataSql(ArrayList<ColumnsInfo> column, String dbname, String resultA0100s) {
		StringBuffer fields = new StringBuffer("select ");
		StringBuffer whereStr = new StringBuffer();
		for(int i = 0; i < column.size(); i++) {
			Object obj = column.get(i); 
	        String name = obj.getClass().getName();
	        //合并列在表头中为hashmap
	        if("java.util.HashMap".equalsIgnoreCase(name)) {
	        	HashMap map = (HashMap) obj;
	        	ArrayList mapList = new ArrayList( );
	        	mapList =(ArrayList) map.get("items");
	        	
	        	for(int j = 0; j < mapList.size(); j++) {
	        		ColumnsInfo columnsInfo = (ColumnsInfo) mapList.get(j);
	    			if(columnsInfo!=null&&StringUtils.isNotEmpty(columnsInfo.getColumnId())
	    					&&StringUtils.isNotEmpty(columnsInfo.getFieldsetid())){
	    				String fieldSetid = columnsInfo.getFieldsetid();
	    				fields.append(fieldSetid+"."+columnsInfo.getColumnId()+",");
	    				if(!StringUtils.contains(whereStr.toString(), fieldSetid)){
	    					if("A01".equalsIgnoreCase(fieldSetid))
	    						whereStr.append(" from "+dbname+"A01 A01 ");
	    					else {
	    						whereStr.append(" left join (select a1.* from "+dbname+fieldSetid+" a1 ");
	    						whereStr.append(" where  a1.i9999=(select MAX(b1.I9999) from "+dbname+fieldSetid+" b1 where b1.A0100=a1.a0100)) "+fieldSetid +" on A01.A0100="+fieldSetid+".A0100 ");
	    					}
	    				}
	    			}
	        	}
	        	continue;
	        }
	        
		    ColumnsInfo columnsInfo = (ColumnsInfo) obj;
			if(columnsInfo!=null&&StringUtils.isNotEmpty(columnsInfo.getColumnId())
					&&StringUtils.isNotEmpty(columnsInfo.getFieldsetid())){
				String fieldSetid = columnsInfo.getFieldsetid();
				fields.append(fieldSetid+"."+columnsInfo.getColumnId()+",");
				if(!StringUtils.contains(whereStr.toString(), fieldSetid)){
					if("A01".equalsIgnoreCase(fieldSetid))
						whereStr.append(" from "+dbname+"A01 A01 ");
					else {
						whereStr.append(" left join (select a1.* from "+dbname+fieldSetid+" a1 ");
						whereStr.append(" where  a1.i9999=(select MAX(b1.I9999) from "+dbname+fieldSetid+" b1 where b1.A0100=a1.a0100)) "+fieldSetid +" on A01.A0100="+fieldSetid+".A0100 ");
					}
				}
			}
		}
		fields.setLength(fields.length()-1);
		whereStr.append(" where A01.a0100  in (  ");
		whereStr.append("".equals(resultA0100s)?"'#'":resultA0100s);
		whereStr.append(" ) ");
		return fields.append(whereStr).toString();
	}
	public ArrayList<ColumnsInfo> listRecommendedPosition(String submoduleid) {
		ArrayList list = new ArrayList(); 
		ArrayList columnList = new ArrayList();
		TableFactoryBO tableBo = new TableFactoryBO(submoduleid, this.userview, con);
		HashMap scheme = tableBo.getTableLayoutConfig();
		if(scheme!=null)
        {
        	Integer scheme_str = (Integer)scheme.get("schemeId");
        	int schemeId = scheme_str.intValue();
        	ArrayList columnConfigLst = tableBo.getTableColumnConfig(schemeId);
        	list = columnConfigLst;
        	list.add("a0100");
        }else{
        	//人员编号
        	list.add("a0100");
        	//人员姓名
        	list.add("a0101");
        	//性别
        	list.add("a0107");
        	//年龄
        	list.add("a0122");
        	//最高学历
        	list.add("a0405");
        	//专业
        	list.add("A0410");
        	//学校
        	list.add("A0435");
        	//邮箱
        	list.add(this.getEmailItemId());
        }
		String mergedesc = "";
    	int mergedescIndex = 0;
        int num = 0;
		for(int i=0;i<list.size();i++)
		{
			FieldItem item= null;
			String itemId = "";
			ColumnsInfo info = new ColumnsInfo();
        	if(scheme!=null)
            {
        		if(!"a0100".equals(list.get(i)))
	        	{
        			//当前用户有自定义栏目设置时
        			ColumnConfig column = (ColumnConfig)list.get(i);
        			itemId = column.getItemid();
        			item = DataDictionary.getFieldItem(itemId);
        			if(item!=null)
        			{
        				info = new ColumnsInfo(item);
        				info.setColumnDesc(item.getItemdesc());
        				if("1".equals(column.getIs_lock()))
        				{
        					info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
        				}else if("0".equals(column.getIs_lock()))
        				{
        					info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
        				}
	    				info.setLoadtype(Integer.parseInt(("".equals((String)column.getIs_lock())||(String)column.getIs_lock()==null)?"0":(String)column.getIs_lock()));
        				info.setColumnWidth(column.getDisplaywidth());
        				info.setTextAlign(column.getAlign()+"");
        				String order = "";
	    				if("1".equalsIgnoreCase(column.getIs_order()))
	    				{
	    					order = "true";
	    				}else{
	    					order = "false";
	    				}
	    				info.setSortable(Boolean.parseBoolean(order));
        				if("1".equals(column.getIs_sum()))
        				{
        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
        				}else if("2".equals(column.getIs_sum())){
        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
        				}else if("3".equals(column.getIs_sum())){
        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
        				}else if("4".equals(column.getIs_sum())){
        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
        				}
        				if(column.getMergedesc()!=null&&column.getMergedesc().length()>0){
        					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
        					{
        						ArrayList tableheadlist = new ArrayList( );
        						tableheadlist.add(columnList.get(mergedescIndex-num));
        						tableheadlist.add(info);
        						HashMap topHead = new HashMap();
        						topHead.put("text",mergedesc);
        						topHead.put("items", tableheadlist);
        						//当合并时移除最后一列
        						columnList.remove(mergedescIndex-num);
        						columnList.add(topHead);
        						num+=1;
        						continue;
        					}else{	        						
        						mergedesc = column.getMergedesc();
        						mergedescIndex = i;
        					}
        				}
        			}else{
        				if(column.getMergedesc()!=null && column.getMergedesc().length()>0){
        					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1){
        						ArrayList tableheadlist = new ArrayList( );
        						tableheadlist.add(columnList.get(mergedescIndex-num));
        						tableheadlist.add(info);
        						HashMap topHead = new HashMap();
        						topHead.put("text",mergedesc);
        						topHead.put("items", tableheadlist);
        						//当合并时移除最后一列
        						columnList.remove(mergedescIndex-num);
        						columnList.add(topHead);
        						num+=1;
        						continue;
        					}else{	        						
        						mergedesc = column.getMergedesc();
        						mergedescIndex = i;
        					}
        				}
        			}
	        	}else{
	        		itemId = (String) list.get(i);
	            	info = new ColumnsInfo();
	            	info.setFieldsetid("A01");
	            	info.setColumnId(itemId);
	            	if("a0100".equalsIgnoreCase(itemId))
	            		info.setEncrypted(true);
	            	info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
	        	}
            }else{
            	itemId = (String) list.get(i);
            	item = DataDictionary.getFieldItem(itemId);
        	
				if(item==null|| "".equals(item))
					continue;
				
				info = new ColumnsInfo(item);
				if("A".equals(info.getColumnType()))
					info.setCodeSetValid(false);
				if("N".equalsIgnoreCase(item.getItemtype())|| "a0107".equalsIgnoreCase(itemId))
					info.setColumnWidth(50);
				else if("A".equalsIgnoreCase(item.getItemtype()))
					info.setColumnWidth(100);	
				if("a0100".equals(itemId)){
					info.setFieldsetid("A01");
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					info.setEncrypted(true);
				}
            }
			columnList.add(info);
		}
		return columnList;
	}
	/**
	 * 获取邮件地址指标
	 */
	public String getEmailItemId()
	{
		RowSet rs = null;
		String emailId = "";
		try {
			ContentDAO dao = new ContentDAO(con);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			 rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return emailId;
	}
	public ArrayList getButtonList() {
		
		ArrayList operationList = new ArrayList();
		ButtonInfo btnInfo = new ButtonInfo();
		//btnInfo.setFunctionId("ZP0000002300");
		btnInfo.setType(ButtonInfo.BUTTON_SPLIT);
		btnInfo.setFunctype(ButtonInfo.FNTYPE_SCHEME);
		//btnInfo.setHandler("Global.");
		btnInfo.setText("栏目设置");
		operationList.add(btnInfo);
		
		btnInfo = new ButtonInfo();
		btnInfo.setType(ButtonInfo.TYPE_BUTTON);
		btnInfo.setHandler("Global.recommend");
		btnInfo.setText("推荐");
		operationList.add(btnInfo);
		
		btnInfo = new ButtonInfo();
		btnInfo.setType(ButtonInfo.TYPE_BUTTON);
		btnInfo.setText("返回");
		btnInfo.setHandler("Global.backPreview");
		operationList.add(btnInfo);
		
/*		btnInfo = new ButtonInfo();
		btnInfo.setFunctionId("ZP0000002300");
		btnInfo.setType(ButtonInfo.TYPE_QUERYBOX);
		btnInfo.setText("请输入姓名、邮箱、学校...");
		operationList.add(btnInfo);*/
		
		return operationList;
	}
	public String getValidataSql(String a0100Condition, ResumeFilterBo resumeFilterBo, String dbname, String positionid) throws SQLException {
		RowSet rs  = null;
		String returnStr = "";
		try {
			StringBuffer checksql = new StringBuffer();//a.a0101,a.a0107,b.a0405,b.A0410,b.A0455
			checksql.append(" SELECT a.A0100 FROM "+dbname+"a01 a  ")
			.append("  left join zp_pos_tache b on a.A0100=b.A0100 ")
			.append(" WHERE ")
			.append(" ( ")
			.append(" ( ")
			.append(" b.ZP_POS_ID<>'"+positionid+"' and b.resume_flag IN ('0105','0205','0306','0406','0506','0603','0703') ")
			.append(" and b.A0100 in (select a0100 from zp_pos_tache where ZP_POS_ID='"+positionid+"') ")
			.append(" ) ")
			.append(" or ")
			.append(" ( ")
			.append(" b.ZP_POS_ID='"+positionid+"' and b.A0100 not in (select a0100 from zp_pos_tache where ZP_POS_ID<>'"+positionid+"') ")
			.append(" ) ")
			.append(" or a.A0100 not in (select A0100 from zp_pos_tache) ")
			.append(" ) ")
			.append(" and a.a0100 IN ( ")
			.append(a0100Condition)
			.append(" ) ");
			ContentDAO dao  = new ContentDAO(con);
			rs = dao.search(checksql.toString());
			while(rs.next()){
				String a0100 = rs.getString("a0100");
				returnStr+=","+a0100;
			}
			if(returnStr.length()>0)
				returnStr = returnStr.substring(1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return returnStr;
	}
	
	/**招聘职位人岗匹配时计算有多少人符合条件
	 * @param sql
	 * @return
	 */
	public int countNumber(String sql) {
		int num = 0;//统计符合条件的人数
		RowSet rs = null;
		try {
			rs = dao.search("select count(*) from (" + sql + ") temp");
			if(rs.next())
				num = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return num;
	}
}
