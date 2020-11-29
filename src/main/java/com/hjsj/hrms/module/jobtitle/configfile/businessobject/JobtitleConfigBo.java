package com.hjsj.hrms.module.jobtitle.configfile.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class JobtitleConfigBo {
    private Connection conn = null;
    private UserView userview;

    /**
     * 构造函数
     * 
     * @param conn
     * @param userview
     */
    public JobtitleConfigBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    
    /**
     * 获取模板页信息
     * @param tabid：模板号
     * @return
     * @throws GeneralException
     */
    public ArrayList getTemplatePage(String tabid) throws GeneralException {
    	ArrayList<HashMap<String, String>> pageList = new ArrayList<HashMap<String, String>>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select tabid,pageid, title from Template_Page where TabID=?";
			ArrayList<String> list = new ArrayList<String>();
			list.add(tabid);
			rs = dao.search(sql, list);
			
			int seq = 0;
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("seq", String.valueOf(++seq));//序号
				map.put("tabid", PubFunc.encrypt(rs.getString("tabid")));//模板号
				map.put("pageid", PubFunc.encrypt(rs.getString("pageid")));//页签号
				map.put("title", rs.getString("title"));//页签名
				// 申报材料的显示页签在不同阶段做不同的配置 chent 20170428
				map.put("notice_0", PubFunc.encrypt("0"));//公示
				map.put("notice_1", PubFunc.encrypt("1"));//评委会
				map.put("notice_2", PubFunc.encrypt("2"));//学科组
				map.put("notice_3", PubFunc.encrypt("3"));//同行专家
				map.put("notice_4", PubFunc.encrypt("4"));//二级单位
				
				pageList.add(map);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return pageList;
    }
    
    /**
     * 获取公示材料配置
     * @return
     */
    public HashMap getJobtitleNoticeConfig(){    	
    	HashMap map = new HashMap();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search("select Str_Value from constant where Constant=?",
					Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
			String xmlDoc="";
			if(rs.next()){
				xmlDoc=rs.getString("Str_Value");
			} 
			if (xmlDoc!=null && xmlDoc.length()>0){
			   	//创建一个新的字符串
		        List list=new ArrayList();
		        try {
		            //通过输入源构造一个Document
		            Document doc = PubFunc.generateDom(xmlDoc);
		            //取的根元素
		            Element root = doc.getRootElement();
		            //得到根元素所有子元素的集合
		            List jiedian = root.getChildren();
		            //子元素对象
		            Element et = null;
		            for(int i=0;i<jiedian.size();i++){
		                et = (Element) jiedian.get(i);//循环依次得到子元素
		                String etName = et.getName();
		                
		                if("templatenoticeconfig".equalsIgnoreCase(etName)){//模板通知配置
		                	List attributes = et.getAttributes();
		                	
		                	for(Object attribute : attributes){
		                		Attribute _attribute = (Attribute)attribute;
		                		String key = _attribute.getName();
		                    	String[] arr = key.split("_");
		                    	String key_e = PubFunc.encrypt(arr[0].substring(1)) + "_" + PubFunc.encrypt(arr[1]) + "_" + PubFunc.encrypt(arr[2]);//tabid+"_"+pageid+"_"+type形式。存的时候xml不能以数字开头，在前面加t。把t截掉
		                		
		                		String value = _attribute.getValue();
		                		map.put(key_e, value);
		                	}
		                }
		                
		            }
		            
		        } catch (JDOMException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
	
	   } catch (Exception e) {
            e.printStackTrace();
       }	
	   return map;
	}
    /**
     * 保存公示材料配置
     * @param configArr
     * @return
     * @throws GeneralException
     */
    public String saveJobtitleNoticeConfig(ArrayList<MorphDynaBean> configArr) throws GeneralException {
    	
    	String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			String xmlValue = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" +
			"  <params><templates></templates></params>";
			//常量表如果没有，则插入
			rs = dao.search("select Str_Value from Constant where constant='JOBTITLE_CONFIG'");
			if(!(rs.next())){//没有则插入空
				String sql="insert into Constant(Constant,Type,Describe,str_value)" +
						" values('JOBTITLE_CONFIG','A','职称评审配置参数','')";
				dao.insert(sql, new ArrayList());
			}else{
				String str_value = rs.getString("Str_Value");
				if(StringUtils.isNotEmpty(str_value))
					xmlValue = rs.getString("Str_Value");
			}
			//解析xml
	        Document doc = PubFunc.generateDom(xmlValue);
	        //取的根元素
            Element root = doc.getRootElement();
            Element templatenoticeconfigEl = root.getChild("templatenoticeconfig");
            if(templatenoticeconfigEl == null){
            	templatenoticeconfigEl = new Element("templatenoticeconfig");
            }
            
            for(MorphDynaBean config : configArr){
            	String key_e = (String)config.get("key");
            	String[] arr = key_e.split("_");
            	String key = "t" + PubFunc.decrypt(arr[0]) + "_" + PubFunc.decrypt(arr[1]) + "_" + PubFunc.decrypt(arr[2]);//tabid+"_"+pageid+"_"+type 形式。但xml不能以数字开头，在前面加t。t指template。
            			
            	String value = String.valueOf(config.get("value"));
            	templatenoticeconfigEl.setAttribute(key, value);
            }
            
            if(root.getChild("templatenoticeconfig") == null){
            	root.addContent(templatenoticeconfigEl);
            }
			  //设置xml字体编码，然后输出为字符串
            Format format=Format.getRawFormat();
        	format.setEncoding("UTF-8");
            XMLOutputter output=new XMLOutputter(format);
        	String xml=output.outputString(doc);//最终处理后xml
			int row = dao.update("update constant  set Str_Value='"+ xml +"' where Constant='JOBTITLE_CONFIG'");
			//读取静态常量
			if(row==1){//是否成功
				RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
				if(paramsVo==null){
					paramsVo = new RecordVo("Constant");
					paramsVo.setString("constant", "JOBTITLE_CONFIG");
					paramsVo.setString("describe", "职称评审配置参数");
					paramsVo.setString("type", "A");
				}
				paramsVo.setString("str_value", xml);
				ConstantParamter.putConstantVo(paramsVo, "JOBTITLE_CONFIG");
				msg = "保存成功！";
		    }else {
		    	msg = "保存失败！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			if(rs!=null)
				PubFunc.closeResource(rs);
		}
		
		return msg;
    }
    /**
     * 获取一个模板的材料公示配置，只获取没有勾选的。
     * @param _tabid：模板号
     * @param type：0：公示 1：聘委会 2：学科组 3：同行专家 4：二级单位
     * @return
     */
    public String getJobtitleNoticeConfigByTabId(String _tabid, String _type){    	
    	StringBuilder configStr = new StringBuilder();
    	
    	ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search("select Str_Value from constant where Constant=?",
					Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
			String xmlDoc="";
			if(rs.next()){
				xmlDoc=rs.getString("Str_Value");
			} 
			if (xmlDoc!=null && xmlDoc.length()>0){
			   	//创建一个新的字符串
		        List list=new ArrayList();
		        try {
		            //通过输入源构造一个Document
		            Document doc = PubFunc.generateDom(xmlDoc);
		            //取的根元素
		            Element root = doc.getRootElement();
		            //得到根元素所有子元素的集合
		            List jiedian = root.getChildren();
		            //子元素对象
		            Element et = null;
		            for(int i=0;i<jiedian.size();i++){
		                et = (Element) jiedian.get(i);//循环依次得到子元素
		                String etName = et.getName();
		                
		                if("templatenoticeconfig".equalsIgnoreCase(etName)){//模板通知配置
		                	List attributes = et.getAttributes();
		                	
		                	for(Object attribute : attributes){
		                		Attribute _attribute = (Attribute)attribute;
		                		String key = _attribute.getName();
		                    	String[] arr = key.split("_");
		                    	
		                    	String tabid = arr[0].substring(1);
		                    	String pageid = arr[1];
		                    	String type = arr[2];
		                    	if(_tabid.equals(tabid) && _type.equals(type) && "false".equalsIgnoreCase(_attribute.getValue())){
		                    		configStr.append(pageid+",");
		                    	}
		                	}
		                	if(configStr.length() > 0){
		                		configStr.deleteCharAt(configStr.length()-1);
		                	}
		                }
		                
		            }
		            
		        } catch (JDOMException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
	
	   } catch (Exception e) {
            e.printStackTrace();
       }	
	   return configStr.toString();
	}
    //将解析后的xml存入数据库
    public void updateXml(String upxml){
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		//把修改后的xml存入数据库
    		int rs = dao.update("update constant  set Str_Value=? where Constant='JOBTITLE_CONFIG'",Arrays.asList(new String[]{upxml}));
    		if(rs==1){//是否成功
    			RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
    			paramsVo.setString("str_value", upxml);
    			ConstantParamter.putConstantVo(paramsVo, "JOBTITLE_CONFIG");
    	    }
    		
    	} 
    	catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * 是否存在启用学院任聘组的评审会议。
     * @param value
     * @return
     */
    public boolean isExstOpenCollege(){
    	boolean isExist = false;
    	
    	ContentDAO dao = null;
    	RowSet rs = null;
    	try {
			boolean college_eval_oldState = this.getParamConfig("college_eval");
			if(college_eval_oldState){//本是启用的，现取消时
				String sql = "select count(w0301) as count from w03 where W0323 > 0";
				
				dao = new ContentDAO(this.conn);
				rs = dao.search(sql);
				if(rs.next() && rs.getInt("count")>0){//评审会议中存在启用学院任聘组的记录。
					isExist = true;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
    	return isExist;
    }
    /**
     * 获取支持二级单位评议组、支持评审的配置信息
     */
    public boolean getParamConfig(String key) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		String sql = "select Str_Value from Constant where Constant=?";
    		List list = new ArrayList();
    		list.add("JOBTITLE_CONFIG");
    		RowSet rs = dao.search(sql,list);
    		if(rs.next()){
    			String strValue = rs.getString("Str_Value");
    			if(strValue!=null && !"".equals(strValue.trim())){
    				  Document doc = PubFunc.generateDom(strValue);
    				  Element root = doc.getRootElement();
    				  List childs = root.getChildren(key);
    				  if(childs.size()>0){
    					  Element el =(Element)childs.get(0);	
    					  String str = el.getText();
    					  if(str!=null && !"".equals(str.trim())){
    						  return Boolean.valueOf(str);
    					  }
    				  }
    			}
    		}
    		return false;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
    }
    /**
     * 获取投票参数
     */
    public HashMap getVoteConfig() throws GeneralException{
    	HashMap map=new HashMap();
    	ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs=null;
    	try {
    		String sql = "select Str_Value from Constant where Constant=?";
    		List list = new ArrayList();
    		list.add("JOBTITLE_CONFIG");
    		rs = dao.search(sql,list);
    		if(rs.next()){
    			String strValue = rs.getString("Str_Value");
    			if(strValue!=null && !"".equals(strValue.trim())){
    				  Document doc = PubFunc.generateDom(strValue);
    				  Element root = doc.getRootElement();
      	              for(Object obj:root.getChildren()){
      	            	  Element el=(Element)obj;
      	            	  if("vote_type".equals(el.getName())){
      	            		  String voteType=el.getAttributeValue("type")==null?"":el.getAttributeValue("type");
      	            		  String voteColumns=el.getAttributeValue("columns")==null?"":el.getAttributeValue("columns");
      	            		  map.put("voteType", voteType);
      	            		  map.put("voteColumns", voteColumns);
      	            	  }
      	              }
      	             
    			}
    		}
    		if(map.isEmpty()){
    			map.put("voteType", "1");
    			map.put("voteColumns", "");
    		}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return map;
    	
    }
    /**
     * 保存配置
     * @param configArr {key:per_templates;value:Z005}
     * @return
     * @throws GeneralException
     */
    public String saveJobtitleConfig(ArrayList<HashMap> configArr) throws GeneralException {
    	
    	String msg = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			String xmlValue = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" +
			"  <params><templates></templates></params>";
			// 常量表如果没有，则插入
			rs = dao.search("select Str_Value from Constant where constant='JOBTITLE_CONFIG'");
			if(!(rs.next())){
				// 没有则插入空
				String sql="insert into Constant(Constant,Type,Describe,str_value)" +
						" values('JOBTITLE_CONFIG','A','职称评审配置参数','')";
				dao.insert(sql, new ArrayList());
			}else{
				String str_value = rs.getString("Str_Value");
				if(StringUtils.isNotEmpty(str_value))
					xmlValue = rs.getString("Str_Value");
			}
			// 解析xml
	        Document doc = PubFunc.generateDom(xmlValue);
	        // 取的根元素
            Element root = doc.getRootElement();
            
            for(HashMap config : configArr){
            	String key = (String)config.get("key");
            	String value = String.valueOf(config.get("value"));
            	
            	Element configEl = root.getChild(key);
                if(configEl == null){
                	configEl = new Element(key);
                	configEl.setText(value);
                	root.addContent(configEl);
                }else {
                	configEl.setText(value);
                }
            }
            
			// 设置xml字体编码，然后输出为字符串
            Format format=Format.getRawFormat();
        	format.setEncoding("UTF-8");
            XMLOutputter output=new XMLOutputter(format);
        	String xml=output.outputString(doc);//最终处理后xml
			int row = dao.update("update constant  set Str_Value=? where Constant='JOBTITLE_CONFIG'", Arrays.asList(new String[] {xml}));
			// 读取静态常量
			if(row==1){// 是否成功
				RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
				if(paramsVo==null){
					paramsVo = new RecordVo("Constant");
					paramsVo.setString("constant", "JOBTITLE_CONFIG");
					paramsVo.setString("describe", "职称评审配置参数");
					paramsVo.setString("type", "A");
				}
				paramsVo.setString("str_value", xml);
				ConstantParamter.putConstantVo(paramsVo, "JOBTITLE_CONFIG");
				msg = "保存成功！";
		    }else {
		    	msg = "保存失败！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeResource(rs);
		}
		
		return msg;
    }
    
    /**
     * 获取职称配置参数
     * @param keyConfig 如per_templates (<per_templates>P0001,P0002</per_templates>)
     * @return valueConfig 如 P0001,P0002
     */
    public String getJobtitleParamConfig(String keyConfig){    	
//    	HashMap map = new HashMap();
    	String valueConfig = "";
    	ContentDAO dao = new ContentDAO(this.conn);
		try {
			
			String xmlDoc="";
			//原有参数，取缓存数据
			RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
			// 有缓存则取缓存数据，没有则取默认参数
			if(null != paramsVo){
				xmlDoc = paramsVo.getString("str_value");
			}else {
				RowSet rs = dao.search("select Str_Value from constant where Constant=?",
						Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
				if(rs.next()){
					xmlDoc=rs.getString("Str_Value");
				} 
			}
			if (xmlDoc!=null && xmlDoc.length()>0){
		        try {
		            //通过输入源构造一个Document
		            Document doc = PubFunc.generateDom(xmlDoc);
		            //取的根元素
		            Element root = doc.getRootElement();
		            Element node = root.getChild(keyConfig);
		            if(null != node)
		            	valueConfig = node.getText();
		            
		        } catch (JDOMException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
	
	   } catch (Exception e) {
            e.printStackTrace();
       }	
	   return valueConfig;
	}
    
    /**
	 * 撤销指标
	 * C_模板ID 		评分结果
	 * C_模板ID_seq	排名
	 * @param cancelTabids 	Z004,Z005,Z015
	 * @throws SQLException
	 */
	public void cancelField(String cancelTabids)  throws GeneralException{
		try {
			DbWizard db=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			String[] tabidlist=cancelTabids.split(",");
			for (int i = 0; i < tabidlist.length; i++) {
				if(StringUtils.isEmpty(tabidlist[i]))
					continue;
				// 表的评分 指标
				String idclm = "C_"+tabidlist[i];
				// 表的排序 指标
				String seqclm = "C_"+tabidlist[i]+"_seq";
				
				if(db.isExistField("w05", idclm, false))
					cancelUpdateField(idclm, dao);
				
				if(db.isExistField("w05", seqclm, false))
					cancelUpdateField(seqclm, dao);
				
			}
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
	}
	
	/**
	 * 删除或隐藏指标
	 * @param itemid
	 * @param dao
	 * @throws GeneralException
	 */
	public void cancelUpdateField(String itemid, ContentDAO dao) throws GeneralException{
		RowSet rs=null;
		try {
			rs = dao.search("select "+itemid+" cdata from W05 ");
			double storeAll = 0; 
			while(rs.next()) {
				Object store = rs.getObject("cdata");
				if(null != store)
					storeAll += Double.parseDouble(store.toString());
			}
			// 若没有数据 则直接删除
			if(storeAll == 0) {
				dao.update("alter table W05 drop column "+itemid+" ");
				dao.update("delete from t_hr_busifield where fieldsetid='W05' and itemid='"+itemid+"'");
			}
			// 若存在数据 则数据字典置为隐藏状态
			else {
				dao.update("update t_hr_busifield set state='0' where fieldsetid='W05' and itemid='"+itemid+"'");
			}
				
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 构建指标
	 * C_模板ID 		评分结果
	 * C_模板ID_seq	排名
	 * @param tabidNames 	Z004|[Z004]名称,Z005|[Z005]名称,Z015|[Z015]名称
	 * @throws SQLException
	 */
	public void addField(String tabidNames) throws GeneralException{
		try {
			if(StringUtils.isBlank(tabidNames))
				return;
			DbWizard db=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			String[] tabidlist=tabidNames.split(",");
			for (int i = 0; i < tabidlist.length; i++) {
				if(StringUtils.isEmpty(tabidlist[i]))
					continue;
				// 截取表id 名称
				String idnames = tabidlist[i].replaceAll("｜", "|");
				String id = idnames.split("\\|")[0];
				String name = idnames.split("\\|")[1];
				if(name.indexOf("]") != -1)
					name = name.split("]")[1];
				// 表的评分
				String idclm = "C_"+id;
				// 表的排序
				String seqclm = "C_"+id+"_seq";
				CodeItem idclm_item = AdminCode.getCode("N", idclm);
				CodeItem seqclm_item = AdminCode.getCode("N", seqclm);
				if(idclm_item ==null) {
					updateValue("W05", idclm, name, 1, dao);
				}else {
					ArrayList valuelist = new ArrayList();
					valuelist.add("W05");
					valuelist.add(idclm);
					dao.update("update t_hr_busifield set state='1' where fieldsetid=? and itemid=?",valuelist);
				}
				if(seqclm_item ==null) {
					updateValue("W05", seqclm, name, 0, dao);
				}else {
					ArrayList valuelist = new ArrayList();
					valuelist.add("W05");
					valuelist.add(seqclm);
					dao.update("update t_hr_busifield set state='1' where fieldsetid=? and itemid=?",valuelist);
				}
				
			}
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
	}
	
	/**
	 * 更新业务字典指标数据
	 * @param fieldSetId
	 * @param itemid
	 * @param templateName
	 * @param type	=0-int;=1-double;
	 * @param dao
	 */
	public void updateValue(String fieldSetId, String itemid, String templateName, int type, ContentDAO dao) 
			throws GeneralException {
		
		RowSet rs=null;
		try {
			String itemtype = "A";
			String itemidtype = "varchar";
			if(0 == type) {
				itemtype = "N";
				itemidtype = "int";
			}else if(1 == type) {
				itemtype = "N";
				itemidtype = "float";
			}
			StringBuffer sql = new StringBuffer("");
			DbWizard db=new DbWizard(this.conn);
			if(!db.isExistField("w05", itemid, false)) {
				// 增加W05相应字段
				sql.append("alter table ").append(fieldSetId).append(" add ").append(itemid).append(" ").append(itemidtype);
				dao.update(sql.toString());
			}
			sql.setLength(0);
			// 查询业务字典  并增加相应字段
			sql.append("select itemid from t_hr_busifield where fieldsetid=? and itemid=?");
			ArrayList valuelist = new ArrayList();
			valuelist.add(fieldSetId);
			valuelist.add(itemid);
			rs = dao.search(sql.toString(), valuelist);
			if(rs.next())
				return;
			else{
				sql.setLength(0);
				sql.append("select MAX(displayid) displayMax from t_hr_busifield where FieldSetId = ?");
				valuelist = new ArrayList();
				valuelist.add(fieldSetId);
				rs = dao.search(sql.toString(), valuelist);
				String displayMax = "99";
				if(rs.next())
					displayMax = rs.getString("displayMax");
				// 防止模板名称过长增加描述字段itemmemo
				String sql0="insert into t_hr_busifield"
						+ "(fieldsetid, itemid, displayid, itemtype, itemdesc, itemlength, decimalwidth, codesetid"
						+ ", displaywidth, state, useflag, keyflag, codeflag, ownflag, itemmemo) "
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//					sql0+="'W05','"+clm+"',"+displayMax+",'N','字段名称',4,0,0,10,1,1,0,0,1)";
				
				// 单独处理itemdesc
				String itemdesc = templateName;
				// 37677 校验描述字段长度，这里需增加 分值 或 排名 区分 故需要截取正确的长度
				if(templateName.length() > 18)
					itemdesc = templateName.substring(0, 17);
				if(itemid.contains("_seq"))
					itemdesc += "排名";
				else
					itemdesc += "分值";
				
				valuelist = new ArrayList();
				valuelist.add(fieldSetId);
				valuelist.add(itemid);
				valuelist.add(Integer.parseInt(displayMax)+1);
				valuelist.add(itemtype);
				valuelist.add(itemdesc);
				valuelist.add(4);
				// 是否带有小数位
				if(0 == type)
					valuelist.add(0);
				else if(1 == type)
					valuelist.add(2);
				valuelist.add(0);
				valuelist.add(10);
				valuelist.add(1);
				valuelist.add(1);
				valuelist.add(0);
				valuelist.add(0);
				valuelist.add(1);
				valuelist.add(templateName);
				
				dao.update(sql0, valuelist);
				
				FieldItem fi=new FieldItem(fieldSetId, itemid);
				fi.setItemdesc(itemdesc);
				fi.setItemtype(itemtype);
				fi.setCodesetid("0");
				fi.setExplain("");
				fi.setItemlength(10);
				fi.setDecimalwidth(0);
				// 是否带有小数位
				if(1 == type)
					fi.setDecimalwidth(2);
				fi.setDisplaywidth(10);
				fi.setUseflag("0");
				fi.setFillable(false);
				fi.setState("1");
				DataDictionary.addFieldItem(fieldSetId, fi, 0);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 获取全部树结构数据
	 * @param note 		节点
	 * @param strValue	已选的模板表	Z004,Z005,Z015
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAllTreeData(String note,String strValue) throws GeneralException{
		
		ArrayList list = new ArrayList();	
		RowSet rs = null;
		// 创建分类节点数据库链接
		RowSet rootSetrs = null;
		// 创建分类节点数据库链接
		RowSet rootSetChildrs = null;
		try {
			// 赋值给数组
			String[] value_a = strValue.split(",");
			// 截取节点中的标识
			note = note.replaceAll("｜", "|").replaceAll("／", "/");
			// 截取节点，前半部分为子节点查询条件，后半部分为子节点级别标识
			String[] _note = note.split("\\|");	
			
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap map = new HashMap();
			StringBuffer sql = new StringBuffer("");
	    	
	    	//第一次从根节点进来
			if("root".equals(note)){
				sql.setLength(0);
				sql.append(" select template_setid,name from per_template_set ");
				// parent_id为null则代表是根节点
				sql.append(" where parent_id is null ");
				sql.append(" and validflag='1' and subsys_id='33' ");
				sql.append(" order by template_setid");
				rs = dao.search(sql.toString());
		    	while(rs.next()){
		    		String rootid = rs.getString("template_setid");
		    		// 查分类  per_template_set
					sql.setLength(0);
			    	sql.append(" select template_setid,name from per_template_set ");
					// parent_id不为null则代表是子节点
					sql.append(" where parent_id=").append(Integer.parseInt(rootid)).append(" ");
					sql.append(" and validflag='1' and subsys_id='33' ");
					sql.append(" order by template_setid");
					rootSetChildrs = dao.search(sql.toString());
					ArrayList toplist = new ArrayList();
			    	while(rootSetChildrs.next()){
			    		String rootSetid = rootSetChildrs.getString("template_setid");
//			    		 查子节点下的表  per_template
//			    		子节点集合
						ArrayList<HashMap> childMapList=new ArrayList<HashMap>();
						sql.setLength(0);
						sql.append(" select template_setid,template_id,name from per_template ");
			    		sql.append(" where validflag='1' and status='0' ");
			    		sql.append(" and template_setid =").append(Integer.parseInt(rootSetid)).append(" ");
				    	sql.append(" order by seq");
				    	rootSetrs = dao.search(sql.toString());
				    	while(rootSetrs.next()){
							map = new HashMap();
							map.put("id", rootSetrs.getString("template_id") +"."+ rootSetid+"|2");//给节点id赋值
							map.put("text", "["+rootSetrs.getString("template_id") +"]"+ rootSetrs.getString("name")+"");//给节点名称赋值
							map.put("leaf", true);//是否是子节点
							map.put("checked",true);//是否可选
							childMapList.add(map);
						}
				    	if(childMapList.size() > 0) {
				    		map = new HashMap();
							map.put("id", rootSetid);//给每个node添加标识+"|1"
							map.put("text", rootSetChildrs.getString("name"));//只取人员分配业务模版
							// 有下级则增加子元素
							if(childMapList!=null&&childMapList.size()>0)
								map.put("children", childMapList);
							toplist.add(map);
				    	}
			    	}
			    	
		    		// 查子节点下的表  per_template
		    		//子节点集合
					ArrayList<HashMap> childMapList=new ArrayList<HashMap>();
					sql.setLength(0);
					sql.append(" select template_setid,template_id,name from per_template ");
		    		sql.append(" where validflag='1' and status='0' ");// and template_id in(").append(valuesql).append(") 
		    		sql.append(" and template_setid =").append(Integer.parseInt(rootid)).append(" ");
			    	sql.append(" order by seq");
			    	rootSetrs = dao.search(sql.toString());
			    	while(rootSetrs.next()){
						map = new HashMap();
						map.put("id", rootSetrs.getString("template_id") +"."+ rootid+"|2");//给节点id赋值
						map.put("text", "["+rootSetrs.getString("template_id") +"]"+ rootSetrs.getString("name")+"");//给节点名称赋值
						map.put("leaf", true);//是否是子节点
						map.put("checked",true);//是否可选
						childMapList.add(map);
					}
			    	childMapList.addAll(toplist);
			    	// 第一层描述
			    	if(childMapList.size() > 0) {//因为有的是权重的，但是权重在这里查不出来，导致只有一个空的名字没内容
			    		map = new HashMap();
						map.put("id", rootid);//给每个node添加标识+"|0"
						map.put("text", rs.getString("name"));//只取人员分配业务模版
						// 有下级则增加子元素
						if(childMapList!=null&&childMapList.size()>0)
							map.put("children", childMapList);
						
						list.add(map);
			    	}
		    	}
			}
			//第二次进来
			else {
//				String flag = _note[1];//节点级别标识，判断是几级节点
				String condition=_note[0];//查询条件
				//将条件封装进list，进行预编译查询
				ArrayList conditionlist=new ArrayList();
				conditionlist.add(condition);
				
				// 查分类  per_template_set
				//对于权重的节点就不加上了，否则程序点击页面报错，获取不到内容，obj没有firstchild
				sql.setLength(0);
				sql.append(" select pts.template_setid,pts.name from per_template_set pts left join per_template pt on pts.template_setid = pt.template_setid where pts.parent_id = ?  ");
	    		sql.append(" and pts.validflag='1' ");
		    	sql.append(" and pts.subsys_id='33'and pt.validflag='1' and pt.status='0' ");
		    	sql.append(" order by pts.template_setid");
				rs = dao.search(sql.toString(), conditionlist);
				while(rs.next()){
					//封装成json串
					map = new HashMap();
					map.put("id", rs.getString("template_setid"));//+"|1"
					map.put("text", rs.getString("name"));
					list.add(map);
				}
				// 查表  per_template
				sql.setLength(0);
				sql.append(" select template_id,name from per_template where template_setid = ?  ");
	    		sql.append(" and validflag='1' and status='0' ");
		    	sql.append(" order by seq");
				rs = dao.search(sql.toString(), conditionlist);
				while(rs.next()){
					//封装成json串
					map = new HashMap();
					map.put("id", rs.getString("template_id") +"."+ condition+"|2");//给节点id赋值
					map.put("text", "["+rs.getString("template_id") +"]"+ rs.getString("name")+"");//给节点名称赋值
					map.put("leaf", true);//是否是子节点
					map.put("checked",false);
					for(int j=0;j<value_a.length;j++){//如果以前选了该模版，则设为选中状态
						if(value_a[j].equals(rs.getString("template_id"))){
							map.put("checked",true);//是否可选
						}
					}
					list.add(map);
				}
			} 
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rootSetrs);
		}
		
		return list;
	}
	/**
	 * 获取第一个模板表的节点路径 用于展开树结构
	 * @param templateid
	 * @return	节点路径|tabid 
	 * @throws GeneralException
	 */
	public String getFirstSrc(String templateid) throws GeneralException{

		String firstSrc = "";
		RowSet rootSetrs = null;
		try {
			String tabidsSql = "";
			if(StringUtils.isNotEmpty(templateid)) {
				String[] tmps = templateid.split(",");
				for(int i=0; i<tmps.length; i++){
					if(StringUtils.isEmpty(tmps[i])) 
						continue;
					tabidsSql += "'" + tmps[i] + "'"; 
					if(i < tmps.length-1)
						tabidsSql += ",";
				}
			}else {
				return "";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			
			sql.append("select t.seq,t.template_id,s.template_setid,s.parent_id ");
			sql.append(" from per_template t, per_template_set s ");
			sql.append(" where s.template_setid=t.template_setid and t.status='0'");//and s.parent_id is null
			sql.append(" and t.template_id in(").append(tabidsSql).append(") ");
			sql.append(" order by t.seq ");
			rootSetrs = dao.search(sql.toString());
			String templateSetid = "";
			String template_id = "";
			if(rootSetrs.next()) {
				templateSetid = rootSetrs.getString("template_setid");
				template_id = rootSetrs.getString("template_id");
				String parent_id = rootSetrs.getString("parent_id");
				if(StringUtils.isNotEmpty(parent_id))
					firstSrc = parent_id ;
			}
			// 查询是否存在子节点
			while(StringUtils.isNotEmpty(templateSetid)) {
				firstSrc += "," + templateSetid;
				String setTempid = getTabsetByParentid(tabidsSql, templateSetid, dao);
				templateSetid = "";
				if(setTempid.indexOf("|") > -1) {
					templateSetid = setTempid.split("\\|")[0];
					template_id = setTempid.split("\\|")[1];
				}
			}
			// 如果一个模板都没有 则不需要拼模板id 直接返回空即可
			if(!(StringUtils.isEmpty(firstSrc) || StringUtils.isEmpty(template_id)))
				firstSrc += "|" + template_id;
		} catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally{
			PubFunc.closeResource(rootSetrs);
		}
		
		return firstSrc;
	}
	/**
	 * 通过templateSetid 获取父节点
	 * @param templateSetid
	 * @param dao
	 * @return	节点|tabid 
	 * @throws GeneralException
	 */
	public String getTabsetByParentid(String tabidsSql, String templateSetid, ContentDAO dao) throws GeneralException{

		String setTempid = "";
		RowSet rootSetrs = null;
		try {
			StringBuffer sql = new StringBuffer();
			// 查分类  父节点
			sql.append("select t.seq,t.template_id,s.template_setid ");
			sql.append(" from per_template t, per_template_set s ");
			sql.append(" where s.template_setid=t.template_setid and t.status='0'");
			if(StringUtils.isNotEmpty(templateSetid))
				sql.append(" and s.parent_id=").append(templateSetid);
			sql.append(" and t.template_id in(").append(tabidsSql).append(") ");
			sql.append(" order by t.seq ");
			rootSetrs = dao.search(sql.toString());
			if(rootSetrs.next()) {
				setTempid = rootSetrs.getString("template_setid") + "|" + rootSetrs.getString("template_id");
			}
				
		} catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally{
			PubFunc.closeResource(rootSetrs);
		}
		
		return setTempid;
	}
	/**
	 * 初始获取已选模板id|名称，
	 * @param value
	 * @return
	 * @throws GeneralException
	 */
	public String getSelectidTextSrc(String value) throws GeneralException{
		
		StringBuffer idtexts = new StringBuffer("");
		RowSet rootSetrs = null;
		try {
			String tabidsSql = "";
			if(StringUtils.isNotEmpty(value)) {
				String[] tmps = value.split(",");
				for(int i=0; i<tmps.length; i++){
					if(StringUtils.isEmpty(tmps[i])) 
						continue;
					tabidsSql += "'" + tmps[i] + "'"; 
					if(i < tmps.length-1)
						tabidsSql += ",";
				}
			}else {
				return "";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
	    	sql.append(" select template_id,name ");
			sql.append(" from per_template ");
			sql.append(" where status='0' and template_id in(").append(tabidsSql).append(")");
			
			rootSetrs = dao.search(sql.toString());
			while(rootSetrs.next()) {
				idtexts.append(rootSetrs.getString("template_id")).append("|").append(rootSetrs.getString("name")).append(",");
			}
			if(idtexts.length()>0) {
				idtexts.setLength(idtexts.length()-1);
			}
	    		
		} catch (Exception e) {
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    } finally{
			PubFunc.closeResource(rootSetrs);
		}
		return idtexts.toString();
	}
	/**
	 * 获取已选模板的树结构数据
	 * @param note 		节点
	 * @param strValue	已选的模板表	Z004,Z005,Z015
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSelectTreeData(String note, String strValue, String selectTabids)  throws GeneralException{
		
		ArrayList list = new ArrayList();	
		RowSet rs=null;
		// 创建分类节点数据库链接
		RowSet rootSetrs = null;
		RowSet rootSetChildrs = null;
		try {
			// 赋值给数组
			String[] value_a = strValue.split(",");
			// SQL条件串
			String valuesql = "";
			for(int j=0;j<value_a.length;j++){
				valuesql = valuesql +  "'" + value_a[j] + "',";
			}
			if(valuesql.endsWith(","))
				valuesql = valuesql.substring(0, valuesql.length()-1);
			// 创建或编辑会议，阶段时已选模板表	设置为选中状态
			String[] selectTabidlist = selectTabids.split(",");
			// 截取节点中的标识
			note = note.replaceAll("｜", "|").replaceAll("／", "/");
			// 截取节点，前半部分为子节点查询条件，后半部分为子节点级别标识
			String[] _note = note.split("\\|");	
			
			ContentDAO dao=new ContentDAO(this.conn);
			HashMap map = new HashMap();
			
			StringBuffer sql = new StringBuffer("");
	    	//第一次从根节点进来
			if("root".equals(note)){
				
				sql.setLength(0);
				sql.append(" select template_setid,name from per_template_set ");
				// parent_id为null则代表是根节点
				//需要判断child_id，可能是子模版分类的模板
				sql.append(" where parent_id is null ");
				sql.append(" and validflag='1' and subsys_id='33' ");
				sql.append(" and (template_setid in (select template_setid from per_template where validflag='1' and status='0' and template_id in(").append(valuesql).append(")) ");
				sql.append(" or child_id in (select template_setid from per_template where validflag='1' and status='0' and template_id in(").append(valuesql).append("))) ");
				sql.append(" order by template_setid");
				rs = dao.search(sql.toString());
		    	while(rs.next()){
		    		String rootid = rs.getString("template_setid");
		    		// 查分类  per_template_set
					sql.setLength(0);
			    	sql.append(" select template_setid,name from per_template_set ");
					// parent_id不为null则代表是子节点
					sql.append(" where parent_id=").append(Integer.parseInt(rootid)).append(" ");
					sql.append(" and validflag='1' and subsys_id='33' ");
					sql.append(" order by template_setid");
					rootSetChildrs = dao.search(sql.toString());
					ArrayList toplist = new ArrayList();
			    	while(rootSetChildrs.next()){
			    		String rootSetid = rootSetChildrs.getString("template_setid");
	//		    		 查子节点下的表  per_template
	//		    		子节点集合
						ArrayList<HashMap> childMapList=new ArrayList<HashMap>();
						sql.setLength(0);
						sql.append(" select template_setid,template_id,name from per_template ");
			    		sql.append(" where validflag='1'  ");
			    		sql.append(" and template_setid =").append(Integer.parseInt(rootSetid)).append(" ");
			    		sql.append(" and status='0' and template_id in(").append(valuesql).append(") ");
				    	sql.append(" order by seq");
				    	rootSetrs = dao.search(sql.toString());
				    	while(rootSetrs.next()){
							map = new HashMap();
							map.put("id", rootSetrs.getString("template_id") +"."+ rootSetid+"|2");//给节点id赋值
							map.put("text", "["+rootSetrs.getString("template_id") +"]"+ rootSetrs.getString("name")+"");//给节点名称赋值
							map.put("leaf", true);//是否是子节点
							map.put("checked",true);//是否可选
							childMapList.add(map);
						}
			    		map = new HashMap();
						map.put("id", rootSetid);//给每个node添加标识+"|1"
						map.put("text", rootSetChildrs.getString("name"));//只取人员分配业务模版
						// 有下级则增加子元素
						if(childMapList!=null&&childMapList.size()>0)
							map.put("children", childMapList);
						toplist.add(map);
			    	}
			    	
		    		// 查子节点下的表  per_template
		    		//子节点集合
					ArrayList<HashMap> childMapList=new ArrayList<HashMap>();
					sql.setLength(0);
					sql.append(" select template_setid,template_id,name from per_template ");
		    		sql.append(" where validflag='1' "); 
		    		sql.append(" and template_id in(").append(valuesql).append(") ");
		    		sql.append(" and status='0' and template_setid =").append(Integer.parseInt(rootid)).append(" ");
			    	sql.append(" order by seq");
			    	rootSetrs = dao.search(sql.toString());
			    	while(rootSetrs.next()){
						map = new HashMap();
						map.put("id", rootSetrs.getString("template_id") +"."+ rootid+"|2");//给节点id赋值
						map.put("text", "["+rootSetrs.getString("template_id") +"]"+ rootSetrs.getString("name")+"");//给节点名称赋值
						map.put("leaf", true);//是否是子节点
						map.put("checked",true);//是否可选
						childMapList.add(map);
					}
			    	childMapList.addAll(toplist);
			    	// 第一层描述
		    		map = new HashMap();
					map.put("id", rootid);//给每个node添加标识+"|0"
					map.put("text", rs.getString("name"));//只取人员分配业务模版
					// 有下级则增加子元素
					if(childMapList!=null&&childMapList.size()>0)
						map.put("children", childMapList);
					
					list.add(map);
		    	}
			}else {
//				String flag = _note[1];//节点级别标识，判断是几级节点
				String condition=_note[0];//查询条件
				//将条件封装进list，进行预编译查询
				ArrayList conditionlist=new ArrayList();
				conditionlist.add(condition);
				
				// 查分类  per_template_set
				sql.setLength(0);
				sql.append(" select template_setid,name from per_template_set where parent_id = ?  ");//parent_id is null
	    		sql.append(" and validflag='1' and subsys_id='33'");
		    	sql.append(" and template_setid in (select template_setid from per_template where validflag='1' and status='0' and template_id in(").append(valuesql).append(")) ");
		    	sql.append(" order by template_setid");
				rs = dao.search(sql.toString(), conditionlist);
				while(rs.next()){
					//封装成json串
					map = new HashMap();
					map.put("id", rs.getString("template_setid"));//+"|1"
					map.put("text", rs.getString("name"));
					list.add(map);
				}
				// 查表  per_template
				sql.setLength(0);
				sql.append(" select template_id,name from per_template where template_setid = ?  ");
	    		sql.append(" and validflag='1' and status='0' ");
		    	sql.append(" order by seq");
				rs = dao.search(sql.toString(), conditionlist);
				while(rs.next()){
					//封装成json串
					map = new HashMap();
					String tapid = rs.getString("template_id");
					map.put("id", tapid +"."+ condition+"|2");//给节点id赋值
					map.put("text", "["+rs.getString("template_id") +"]"+ rs.getString("name")+"");//给节点名称赋值
					map.put("leaf", true);//是否是子节点
					map.put("checked",false);
					for(int j=0;j<value_a.length;j++){
						if(value_a[j].equals(tapid)){
							if((","+selectTabids+",").contains(","+tapid+","))
								map.put("checked", true);
							list.add(map);
						}
					}
				}
			}
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally{
			PubFunc.closeResource(rs);
        	PubFunc.closeResource(rootSetrs);
			PubFunc.closeResource(rootSetChildrs);
		}
		
		return list;
	}
	/**
	 * 判断是否可以取消测评表
	 * @return 返回不可删除的测评表
	 * @throws GeneralException 
	 */
	public String isCanCancelTemplate(String templates) throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String noCancel = "";
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select extend_param from w03 where ");
			sql.append(Sql_switcher.dateToChar("W0309", "yyyy")+"=?");
			List values = new ArrayList();
			values.add(DateUtils.getYear(new Date()));
			rs = dao.search(sql.toString(), values);
			String[] templateArr = templates.substring(0,templates.length()-1).split(",");
			while(rs.next()) {
				String extend_param = rs.getString("extend_param");
				if(StringUtils.isNotBlank(extend_param)) {
					for(String s : templateArr) {
						if(extend_param.indexOf(s)>-1) {
							if(!noCancel.contains(s))
								noCancel+=s+",";
						}
					}
				}
			}
			
			if(StringUtils.isNotBlank(noCancel)) {
				noCancel = noCancel.substring(0, noCancel.length()-1);
			}
			
			return noCancel;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
