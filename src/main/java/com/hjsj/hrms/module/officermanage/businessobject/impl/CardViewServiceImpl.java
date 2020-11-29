package com.hjsj.hrms.module.officermanage.businessobject.impl;

import com.aspose.words.ConvertUtil;
import com.aspose.words.DocumentBuilder;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.utils.asposeword.AnalysisWordUtil;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResetFontSizeUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import com.ibm.icu.util.Calendar;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mortbay.util.ajax.JSON;
import sun.misc.BASE64Encoder;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardViewServiceImpl implements CardViewService {
	private UserView userivew;
	private Connection conn;
	private String date="";
	
	public CardViewServiceImpl(UserView userivew, Connection conn) {
		super();
		this.userivew=userivew;
		this.conn=conn;
		Calendar cal=Calendar.getInstance();
		String month=(cal.get(Calendar.MONTH)+1)>9?(cal.get(Calendar.MONTH)+1)+"":"0"+(cal.get(Calendar.MONTH)+1);
		String day=cal.get(Calendar.DATE)>9?cal.get(Calendar.DATE)+"":"0"+cal.get(Calendar.DATE)+"";
		date=cal.get(Calendar.YEAR)+""+month+""+day;
	}
	
	/***
	 * 获取当前操作用户dbname
	 * @param nbases
	 * @return
	 * @throws Exception
	 */
	@Override
    public ArrayList getdbList(String nbases) throws Exception {
		if("".equals(nbases))
			return new ArrayList();
		String[] nbaseArray=nbases.split(",");
		ArrayList dbNameList=new ArrayList();
		String pre="";
		ArrayList Dblist=this.userivew.getPrivDbList();
		for(int i=0;i<nbaseArray.length;i++) {
			if(Dblist.contains(nbaseArray[i]))
				pre+=",'"+nbaseArray[i]+"'";
				
		}
		if(StringUtils.isEmpty(pre)) {
			throw new Exception(ResourceFactory.getProperty("officer.noDblist"));
		}
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs=dao.search("select dbname,pre from dbname where pre in("+pre.substring(1)+")");
			while(rs.next()) {
				
				dbNameList.add(rs.getString("dbname")+"`"+rs.getString("pre").toUpperCase());
			}
			
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return dbNameList;
	}
	
	/**
	 * 获取查询sql
	 * @param queryString
	 * @param 人员库是否需要拼接
	 * @return
	 * @throws Exception
	 */
	@Override
    public StringBuffer getOfficerSql(String queryString, boolean isSbfNbase) throws Exception {
		queryString=PubFunc.replaceSQLkey(queryString);//替换sql关键字，防止sql注入
		HashMap map=getConstantXMl();
		String[] nbase=((String)map.get("nbase")).split(",");//人员库
		String expr=(String)map.get("expr");//筛选条件
		String setId=(String)map.get("setid");
		boolean isHave_subSet=true;
		if(StringUtils.isEmpty(setId)) {
			isHave_subSet=false;
		}
		String privOrg = userivew.getManagePrivCodeValue();
		String b0110 = (String) map.get("postOrg");// 任职单位
		String postStat = (String) map.get("postStat");// 任职情况 只加载任职
		String experSql="";
		HashMap<String, FieldItem> expMap = null;
		if(StringUtils.isNotEmpty(expr)) {
			YksjParser yp = new YksjParser(userivew, DataDictionary.getAllFieldItemList(0, 0),
					YksjParser.forNormal, YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");
			yp.run(expr);
			expMap = yp.getMapUsedFieldItems();
			experSql=yp.getSQL();
		}
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识 唯一标识关联人员
		String pinyin = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);//拼音 唯一标识关联人员
		
		StringBuffer sbf=new StringBuffer();
		
		for(int i=0;i<nbase.length;i++) {
			if(!this.userivew.getPrivDbList().contains(nbase[i])) {//权限范围内人员库
				continue;
			}
			if(isSbfNbase) {
				sbf.append("select (select "+((Sql_switcher.searchDbServer()==Constant.ORACEL)?""+Sql_switcher.numberToChar("dbid")+"||'`'||'"+nbase[i]+"`'||":""+Sql_switcher.numberToChar("dbid")+"+'`'+'"+nbase[i]+"`'+")+"dbname from dbname where pre='"+nbase[i]+"') as nbase,");
			}else {
				sbf.append("select '"+nbase[i]+"' as dbtype,");
			}
			sbf.append(nbase[i]+"A01.guidkey,"+nbase[i]+"A01.A0100,"+nbase[i]+"A01.A0000,"+nbase[i]+"A01.A0101");
			sbf.append(",(select organization.codeitemdesc from organization where "+nbase[i]+"A01.B0110=organization.codeitemid and codesetid='UN') as B0110,");
			sbf.append("(select organization.codeitemdesc from organization where "+nbase[i]+"A01.E0122=organization.codeitemid and codesetid='UM') as E0122 ");
			sbf.append(" from "+nbase[i]+"A01 left join om_officer_muster on "+nbase[i]+"A01.GUIDKEY=om_officer_muster.guidkey ");		
			sbf.append(" where 1=1 ");
			if(isHave_subSet) {
				sbf.append(" and "+nbase[i]+"A01.A0100 in ( select A0100 from "+nbase[i]+setId+" where "+postStat+"='2' ");
				if(StringUtils.isNotEmpty(privOrg)) {
					sbf.append(" and  "+Sql_switcher.sqlNull(b0110, "")+" like '"+privOrg+"%' ) ");
				}else {
					sbf.append(" ) ");
				}
			}
			
			if(StringUtils.isNotEmpty(experSql)&&expMap!=null) {
				String fromSql = "";
				String whereSql = "";
				String itemid = "";
				for (String key : expMap.keySet()) {
					FieldItem item = expMap.get(key);
					if (item == null)
						continue;
					if (!"A0100".equalsIgnoreCase(item.getItemid()))
						itemid += "," + item.getItemid();

					if (!"A01".equalsIgnoreCase(item.getFieldsetid())) {
						if (fromSql.indexOf(nbase[i] + item.getFieldsetid()) < 0)
							fromSql += "," + nbase[i] + item.getFieldsetid();
						if (whereSql.indexOf(nbase[i] + item.getFieldsetid()) < 0)
							whereSql += "and " + nbase[i] + "A01.A0100=" + nbase[i] + item.getFieldsetid()
									+ ".A0100 ";
					}
				}
				if (StringUtils.isNotEmpty(itemid)) {
					StringBuffer expSql=new StringBuffer();
					expSql.append(" select " + nbase[i] + "A01.A0100 " + itemid + " from  " + nbase[i] + "A01 ");
					expSql.append(fromSql);
					expSql.append(" WHERE ");
					if (StringUtils.isNotEmpty(whereSql))
						expSql.append(whereSql.substring(3) + " and ");
					expSql.append(experSql);
					sbf.append(" and "+nbase[i] + "A01.A0100 in (select A0100 from ("+expSql.toString()+") expSql ) ");
				}
			}
			if(StringUtils.isNotEmpty(queryString)) {
				sbf.append(" and ("+nbase[i] + "A01.A0101 like '%"+queryString+"%'");
				if(StringUtils.isNotEmpty(onlyname)) {
					sbf.append(" or "+nbase[i]+"A01."+onlyname+" like '%"+queryString+"%'");
				}
				if(StringUtils.isNotEmpty(pinyin)) {
					sbf.append(" or "+nbase[i]+"A01."+pinyin+" like '%"+queryString+"%'");
				}
				sbf.append(" ) ");
			}
			sbf.append(" union all ");
		}
		return sbf;
	}
	
	/**
	 * 获取数据中存储xml中固定指标的代码项
	 * @return
	 * @throws Exception
	 */
	@Override
    public HashMap<String,String> getMainFields() throws Exception {
		HashMap map=this.getConstant();
		HashMap<String,String> name_codeSet=new HashMap<String,String>();
		HashMap<String,Object> map_mainfileds=(HashMap<String,Object>)map.get("mainfields");
		for(String name:map_mainfileds.keySet()) {
			HashMap<String,String> field_map=(HashMap<String,String>)map_mainfileds.get(name);
			String type=field_map.get("type");
			if("2".equals(type)) {//存储跳过
				continue;
			}
			String field_value=field_map.get("value");
			if(StringUtils.isNotEmpty(field_value)) {
				FieldItem item=DataDictionary.getFieldItem(field_value);
				if(item.isCode()) {
					name_codeSet.put(name, item.getCodesetid());
				}
			}
		}
		return name_codeSet;
	}
	
	/**
	 * 查找 constant表中OFFICER_PARAM 存储数据
	 */
	private HashMap getConstant() throws Exception {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String xml = "";
		try {
			rs = dao.search("select * from constant where constant='OFFICER_PARAM'");
			if (rs.next()) {
				xml = rs.getString("str_value");
			}
			if (StringUtils.isNotEmpty(xml)) {
				org.jdom.Document doc = null;
				HashMap map = new HashMap();
				// 构建Document对象
				doc = PubFunc.generateDom(xml);
				org.jdom.Element root = doc.getRootElement();
				org.jdom.Element postEl = root.getChild("postSet");
				map.put("setid", postEl.getAttributeValue("setid"));
				map.put("postOrg", postEl.getAttributeValue("postOrg"));
				map.put("postState", postEl.getAttributeValue("postState"));
				map.put("nbase", postEl.getAttributeValue("nbase"));
				map.put("jobname",postEl.getAttributeValue("jobname"));
				map.put("expr", postEl.getAttributeValue("expr"));
				map.put("talent", postEl.getAttributeValue("talent"));   
				map.put("mangeView", postEl.getAttributeValue("mangeView"));
				
				org.jdom.Element mainEl = root.getChild("mainfields");
				List mainfieldList = mainEl.getChildren();
				HashMap mainFieldMap = new HashMap();
				HashMap feildmap = null;
				for (int i = 0; i < mainfieldList.size(); i++) {
					org.jdom.Element el = (org.jdom.Element) mainfieldList.get(i);
					feildmap = new HashMap();
					String fieldItem = el.getAttributeValue("value");
					String type = el.getAttributeValue("type");
					feildmap.put("name", el.getAttributeValue("name"));
					feildmap.put("type", type);
					feildmap.put("value", fieldItem);
					if (StringUtils.isNotEmpty(fieldItem) && "1".equals(type)) {
						FieldItem item = DataDictionary.getFieldItem(fieldItem);
						feildmap.put("desc", item != null ? item.getItemdesc() : "");
					}
					mainFieldMap.put(el.getAttributeValue("name").toString(), feildmap);
				}
				map.put("mainfields", mainFieldMap);
				feildmap = null;
				ArrayList customfields = new ArrayList();
				List customList = root.getChild("customfields").getChildren();
				for (int i = 0; i < customList.size(); i++) {
					org.jdom.Element el = (org.jdom.Element) customList.get(i);
					feildmap = new HashMap();
					String itemid = el.getAttributeValue("value");
					String type = el.getAttributeValue("type");
					feildmap.put("name", el.getAttributeValue("name"));
					feildmap.put("columnid", el.getAttributeValue("columnid"));
					feildmap.put("type", type);
					feildmap.put("value", itemid);
					if (StringUtils.isNotEmpty(itemid) && "1".equals(type)) {
						FieldItem item = DataDictionary.getFieldItem(itemid);
						feildmap.put("desc", item != null ? item.getItemdesc() : "");
					}
					customfields.add(feildmap);
				}
				map.put("customfields", customfields);
				return map;
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return null;
	}
	
	@Override
    public LazyDynaBean getOfferData(String nbase, String a0100)throws Exception {
		LazyDynaBean bean=new LazyDynaBean();
		ArrayList list=new ArrayList();
		list.add(a0100);
		List<LazyDynaBean> dataList=ExecuteSQL.executePreMyQuery("select guidkey from "+nbase+"A01 where a0100=?",list , conn);
		if(dataList.size()>0) {
			bean.set("guidkey", dataList.get(0).get("guidkey"));
			bean.set("nbase", nbase);
			bean.set("A0100", a0100);
			return bean;
		}
		return bean;
	}
	/**
	 * 
	 * @param data_obj  干部任免表数据
	 * @param data   人员guidkey nbase
	 * @throws Exception
	 */
	@Override
    public void saveData(MorphDynaBean data_obj, MorphDynaBean data) throws Exception{
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap<String,Object> map=PubFunc.DynaBean2Map(data_obj);
		ArrayList<String> list=new ArrayList<String>();
		StringBuffer sbf=new StringBuffer();
		for(String key:map.keySet()) {
			sbf.append(" "+key+"=?,");
			list.add(map.get(key).toString());
		}
		String guidkey=(String)data.get("guidkey");
		String nbase=((String)data.get("nbase")).toUpperCase();
		list.add(guidkey);
		list.add(nbase);
		dao.update("update om_officer_muster set "+sbf.substring(0, sbf.length()-1)+" where guidkey=? and upper(nbase)=?", list);
	}
	
	/**
	 * 获取选中人员数据
	 * @param guidkey
	 * @param nbase
	 * @param A0100
	 * @return
	 * @throws Exception
	 */
	@Override
    public LazyDynaBean getOfficerData(String guidkey, String nbase, String A0100) throws Exception {
		LazyDynaBean bean=new LazyDynaBean();
		ArrayList<String> list=new ArrayList<String>();
		list.add(guidkey);
		list.add(nbase.toUpperCase());
		List<LazyDynaBean> dataList=ExecuteSQL.executePreMyQuery("select * from om_officer_muster where guidkey=? and upper(nbase)=?", list, conn);
		if(dataList.size()>0) {
			bean=dataList.get(0);
			bean.set("family", deatilFamily((String)bean.get("familyandrelation"),"xml"));
			bean.set("photo", this.getPhotoPath(nbase, A0100,"url"));
			bean.set("resume",JSON.toString(this.deatilResume((String)bean.get("resume"))));
		}else {
			throw new Exception(ResourceFactory.getProperty("officer.showPersonErrormsg"));
		}		return bean;
	}

	/**
	 * 获取人员图像所在路径
	 *
	 * @param nbase
	 * @param a0100
	 * @param flag  url 生成浏览器显示需要的图片    file 导出word需要的图片
	 * @return
	 */
	public String getPhotoPath(String nbase, String a0100,String flag) {
		if (nbase == null || nbase.length() < 1) {
			return "/images/photo.jpg";
		}
		StringBuffer photoUrl = new StringBuffer();
		try {
			PhotoImgBo imgBo = new PhotoImgBo(this.conn);
			imgBo.setIdPhoto(true);
			if ("url".equals(flag)) {
				String filePath = imgBo.getPhotoPath(nbase, a0100);
				photoUrl.append(filePath);
			} else if ("file".equals(flag)) {
				photoUrl.append(getImageFile(nbase, a0100));
			}
		} catch (Exception e) {

		}

		if (photoUrl.toString().length() < 1) {
			photoUrl.append("/images/photo.jpg");
		}

		return photoUrl.toString();
	}
	/**
	 * 创建图片
	 * @param nbase 人员库
	 * @param a0100 人员编号
	 * @return
	 */
	private String getImageFile(String nbase,String a0100){
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		//创建临时目录
		ServletUtilities.createTempDir();
		File file ;
		//返回文件的路径
		String filePath = "";
		InputStream in = null;
		FileOutputStream out = null ;
		ContentDAO dao = new ContentDAO(this.conn);
		sql.append("select fileid,ext from ");
		sql.append(nbase+"A00 ");
		sql.append(" where A0100 = '");
		sql.append(a0100);
		sql.append("' and UPPER(flag) = 'P'");
		try{
			rs = dao.search(sql.toString());
			if(rs.next()){
				String fileId = rs.getString("fileid");
				String ext = rs.getString("ext");
				filePath = System.getProperty("java.io.tmpdir")+"\\"+fileId+ext;
				file =  new File(filePath);
				if(file.exists()){
					return filePath;
				}
				if(!file.createNewFile()){
					throw new GeneralException("缓存照片失败！");
				}
				//创建临时文件
//				file = File.createTempFile(fileId,ext,new File(System.getProperty("java.io.tmpdir")));
				in = VfsService.getFile(fileId);
				out = new FileOutputStream(file);
				int len;
				byte[] data = new byte[1024];
				if(in != null){
					while((len = in.read(data)) != -1){
						out.write(data,0,len);
						out.flush();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
			PubFunc.closeDbObj(rs);
		}
		return filePath;
	}

	/**
	 * 解析简历子集数据
	 * @param resume
	 * @return
	 */
	private ArrayList<String> deatilResume(String resume) {
		ArrayList<String> list=new ArrayList<String>();
		String[] resume_arry=StringUtils.split(resume, "\\n");//简历内容先默认按照\n分组  \\n 数据库存储换行符分隔
		Pattern patt=Pattern.compile("(.{7}\\-\\-.{7}\\s{2}(.+))|(.{7}\\-\\-至今\\s{2}(.+))|(.{7}\\-\\-.{7}\\s{2})|(                (.+))|(.{7}\\-\\-       \\s{2}(.+))");
		Matcher matcher=null;
		for (int i = 0; i < resume_arry.length; i++) {
			String resume_in=resume_arry[i]+"\n";//按照换行分隔字符串结尾添加换行符
			matcher=patt.matcher(resume_in);
			if(matcher.find()) {
				list.add(resume_in);
			}else {
				int index=list.size()-1;
				list.set(index, list.get(index)+resume_in);
			}
		}
		return list;
	}
	
	/**
	 * 解析家庭成员
	 * @param item
	 * @param type xml /word 导出word子集指标名转小写 否则无法导出
	 * @return 
	 * @throws Exception
	 */
	private ArrayList<HashMap<String,String>> deatilFamily(String item,String type)throws Exception{
		ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
		if(StringUtils.isNotEmpty(item)) {
			Document doc=DocumentHelper.parseText("<rootChild>"+item+"</rootChild>");
			Element rootEl=doc.getRootElement();
			Iterator<Element> iteror=rootEl.elementIterator("Item");
			HashMap<String,String> map=null;
			while(iteror.hasNext()) {
				map=new HashMap<String, String>();
				Element el=iteror.next();
				Element el1=el.element("XingMing");
				if("xml".equals(type)) {
					map.put("XingMing", el1.getText());
				}else {
					map.put("xingming", el1.getText());
				}
				
				Element el2=el.element("ChengWei");
				if("xml".equals(type)) {
					map.put("ChengWei", el2.getText());
				}else {
					map.put("chengwei", el2.getText());
				}
				
				Element el3=el.element("ChuShengRiQi");
				if("xml".equals(type)) {
					map.put("ChuShengRiQi", el3.getText());
				}else {
					map.put("chushengriqi", el3.getText());
				}
				
				Element el4=el.element("ZhengZhiMianMao");
				if("xml".equals(type)) {
					map.put("ZhengZhiMianMao", el4.getText());
				}else {
					map.put("zhengzhimianmao", el4.getText());
				}
				
				Element el5=el.element("GongZuoDanWeiJiZhiWu");
				if("xml".equals(type)) {
					map.put("GongZuoDanWeiJiZhiWu", el5.getText());
				}else {
					map.put("gongzuodanweijizhiwu", el5.getText());
				}
				list.add(map);
			}
		}
		return list;
	}
	/***
	 * constant 表中获取 OFFICER_PARAM 自定义字段信息
	 */
	@Override
    public HashMap getConstantXMl() throws Exception {
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String xml = "";
			rs = dao.search("select * from constant where constant='OFFICER_PARAM'");
			if (rs.next()) {
				xml = rs.getString("str_value");
			} else
				throw new Exception("请在参数设置界面完成功能设置!");
			list.addAll(getGenerFieldList());
			if (StringUtils.isNotEmpty(xml)) {
				org.jdom.Document doc = null;
				doc = PubFunc.generateDom(xml);
				org.jdom.Element root = doc.getRootElement();
				org.jdom.Element postEl = root.getChild("postSet");
				// 获取任职子集
				String setId = postEl.getAttributeValue("setid");
				map.put("setid", setId);
				// 获取任职单位
				String postOrg = postEl.getAttributeValue("postOrg");
				map.put("postOrg", postOrg);
				
				String jobname=postEl.getAttributeValue("jobname");
				map.put("jobname", jobname);
				
				String talent = postEl.getAttributeValue("talent");
		        map.put("talent", talent);
		        
		        String mangeView = postEl.getAttributeValue("mangeView");
		        map.put("mangeView", mangeView);
				
				HashMap orgMap=(HashMap)list.get(2);//现任职务子集
				orgMap.put("itemid", postOrg);
				orgMap.put("fieldsetid", setId);
				// 获取人员库
				String nbase = postEl.getAttributeValue("nbase");
				map.put("nbase", nbase);
				// 获取任职状况
				String postStat = postEl.getAttributeValue("postState");
				map.put("postStat", postStat);
				// 获取筛选条件
				String expr = postEl.getAttributeValue("expr");
				map.put("expr", expr);

				org.jdom.Element customEl = root.getChild("customfields");
				List<org.jdom.Element> customList = customEl.getChildren();
				// 自定义信息
				for (int i = 0; i < customList.size(); i++) {
					list.add(getfieldMap(customList.get(i)));
				}
				map.put("list", list);
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	/***
	 * 自定义列
	 **/
	private HashMap getfieldMap(org.jdom.Element el) throws Exception {
		HashMap map = new HashMap();
		String itemid = el.getAttributeValue("value");
		String type = el.getAttributeValue("type");
		String name = el.getAttributeValue("name");
		if ("1".equals(type)) {// 指标
			FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
			if (fieldItem == null)
				throw new Exception(name + ResourceFactory.getProperty("officer.notFindField"));
			map.put("fieldsetid", fieldItem.getFieldsetid());
			map.put("itemid", fieldItem.getItemid());
			map.put("itemdesc", name);
			map.put("itemtype", fieldItem.getItemtype());
			map.put("customFlag", "1");//自定义列 标识
			map.put("codesetid", fieldItem.getCodesetid());
			if ("D".equalsIgnoreCase(fieldItem.getItemtype()) || "N".equalsIgnoreCase(fieldItem.getItemtype())) {
				map.put("formatlength", fieldItem.getItemlength());
			}
		} else {
			// map.put("fieldsetid", "");
			map.put("itemid", el.getAttributeValue("columnid"));
			map.put("itemdesc", name);
			map.put("itemtype", "A");
			map.put("codesetid", "0");
		}
		return map;
	}
	/***
	 * 获取学历学位毕业学校所学专业 为后面合并单元格做准备
	 * @param  map
	 * @return
	 */
	private HashMap<String, Boolean> getCombineMap(HashMap<String, Object> map) {
		HashMap<String, Boolean> combine_cell_Map = new HashMap<String, Boolean>();
		String education_value = map.get("education").toString();
		String degree_value = map.get("degree").toString();
		String school_value = map.get("school").toString();
		String educationmajor_value = map.get("educationmajor").toString();
		//兼容数据库数据为空时，前台导出异常报错问题
		if (StringUtils.isBlank(education_value)) {
			education_value = " ` ";
		}
		if (StringUtils.isBlank(degree_value)) {
			degree_value = " ` ";
		}
		if (StringUtils.isBlank(school_value)) {
			school_value = " ` ";
		}
		if (StringUtils.isBlank(educationmajor_value)) {
			educationmajor_value = " ` ";
		}
		combine_cell_Map.put("education_0", StringUtils.isEmpty(education_value.split("`", -1)[0]));
		combine_cell_Map.put("education_1", StringUtils.isEmpty(education_value.split("`", -1)[1]));

		combine_cell_Map.put("degree_0", StringUtils.isEmpty(degree_value.split("`", -1)[0]));
		combine_cell_Map.put("degree_1", StringUtils.isEmpty(degree_value.split("`", -1)[1]));

		combine_cell_Map.put("school_0", StringUtils.isEmpty(school_value.split("`", -1)[0]));
		combine_cell_Map.put("school_1", StringUtils.isEmpty(school_value.split("`", -1)[1]));

		combine_cell_Map.put("educationmajor_0", StringUtils.isEmpty(educationmajor_value.split("`", -1)[0]));
		combine_cell_Map.put("educationmajor_1", StringUtils.isEmpty(educationmajor_value.split("`", -1)[1]));

		return combine_cell_Map;
	}
	
	/***
	 * 
	 * @param dataList
	 * @param bo
	 * @param type word / pdf
	 * @param filetype all 多人一问答 1 一人一文档
	 * @return
	 * @throws Exception
	 */
	@Override
    public String outwordOrPdf(ArrayList<DynaBean> dataList, String type, String filetype)throws Exception {
		String url="";
		Calendar cal_now=Calendar.getInstance();
		cal_now.setTime(new Date());
		ArrayList data_list=new ArrayList();//每个人的导出数据
		//遍历数据组装导出word所需参数
		for(DynaBean data_bean:dataList) {
			ArrayList outputList=new ArrayList();//导出word数据
			LazyDynaBean firstPage=this.getFistPageBean();
			LazyDynaBean secPage=this.getSecPageBean();
			ArrayList<LazyDynaBean> firstPage_cellList=(ArrayList<LazyDynaBean>)firstPage.get("context");
			ArrayList<LazyDynaBean> secPage_cellList=(ArrayList<LazyDynaBean>)secPage.get("context");
			HashMap<String,Object> map=PubFunc.DynaBean2Map(data_bean);
			/****
			 * 学历学位毕业学校以及所学专业对应内容为空时合并
			 * degree_0--education_0       23---26
			 * educationmajor_1--school_1  23---26
			 * degree_1--education_1       23---26
			 * educationmajor_0--school_0  23---26   
			 * 对应列如果有为空的则合并单元格 并且修改高度                   
			 * */
			HashMap<String,Boolean> combine_cell_Map = this.getCombineMap(map);
			List<LazyDynaBean> del_beanList = new ArrayList<LazyDynaBean>();
			//遍历数据与单元格信息list om_col与数据列名相同时 将数据
			for(String om_col_key:map.keySet()) {
				for (LazyDynaBean cell_bean : firstPage_cellList) {
					// 处理学历 学位 毕业院校 所学专业 education=学历 degree =学位 school =毕业学校 educationmajor =所学专业
					if ("education".equals(om_col_key) || "degree".equals(om_col_key) || "school".equals(om_col_key)
							|| "educationmajor".equals(om_col_key)) {
						if (cell_bean.get("om_col") != null) {
							String value = map.get(om_col_key) != null ? map.get(om_col_key).toString() : "";
//							if (value.length() <= 1) {// 存储学历学位等信息 为空的情况下存在数据为 “`”情况
//								continue;
//							}
							if ("education".equals(om_col_key)) {
								if ("education_0".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[0]);
									if(combine_cell_Map.get("education_0")) {
									    del_beanList.add(cell_bean);
									}
									//下行单元格为空时 合并单元格
									if(combine_cell_Map.get("degree_0")) {
									    cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+23);
									}
								}
								if ("education_1".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[1]);
									if(combine_cell_Map.get("education_1")) {
                                        del_beanList.add(cell_bean);
                                    }
									//下行单元格为空时 合并单元格
                                    if(combine_cell_Map.get("degree_1")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+23);
                                    }
								}
								
							}
							if ("degree".equals(om_col_key)) {
								if ("degree_0".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[0]);
									if(combine_cell_Map.get("degree_0")) {
                                        del_beanList.add(cell_bean);
                                    }
									//上行单元格为空时 合并单元格 rtop 上移
                                    if(combine_cell_Map.get("education_0")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+26);
                                        cell_bean.set("rtop", (Integer)cell_bean.get("rtop")-26);
                                    }
								}
								if ("degree_1".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[1]);
									if(combine_cell_Map.get("degree_1")) {
                                        del_beanList.add(cell_bean);
                                    }
									//上行单元格为空时 合并单元格 rtop 上移
                                    if(combine_cell_Map.get("education_1")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+26);
                                        cell_bean.set("rtop", (Integer)cell_bean.get("rtop")-26);
                                    }
								}
							}
							if ("school".equals(om_col_key)) {
								if ("school_0".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[0]);
									//上下单元格都为空时不删除上方单元格
									if(combine_cell_Map.get("school_0")&&!combine_cell_Map.get("educationmajor_0")) {
                                        del_beanList.add(cell_bean);
                                    }
									//下行单元格为空时 合并单元格
                                    if(combine_cell_Map.get("educationmajor_0")||combine_cell_Map.get("school_0")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+23);
                                    }
									
								}
								if ("school_1".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[1]);
									if(combine_cell_Map.get("school_1")&&!combine_cell_Map.get("educationmajor_1")) {
                                        del_beanList.add(cell_bean);
                                    }
									//下行单元格为空时 合并单元格
                                    if(combine_cell_Map.get("educationmajor_1")||combine_cell_Map.get("school_1")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+23);
                                    }
									
								}
							}
							if ("educationmajor".equals(om_col_key)) {
								if ("educationmajor_0".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[0]);
									if(combine_cell_Map.get("educationmajor_0")) {
                                        del_beanList.add(cell_bean);
                                    }
									//上行单元格为空时 合并单元格 rtop 上移
                                    if(combine_cell_Map.get("school_0")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+26);
                                        cell_bean.set("rtop", (Integer)cell_bean.get("rtop")-26);
                                    }
									
								}
								if ("educationmajor_1".equals(cell_bean.get("om_col").toString())) {
									cell_bean.set("cellvalue", value.split("`",-1)[1]);
									if(combine_cell_Map.get("educationmajor_1")) {
                                        del_beanList.add(cell_bean);
                                    }
									//上行单元格为空时 合并单元格 rtop 上移
                                    if(combine_cell_Map.get("school_1")) {
                                        cell_bean.set("rheight", (Integer)cell_bean.get("rheight")+26);
                                        cell_bean.set("rtop", (Integer)cell_bean.get("rtop")-26);
                                    }
									
								}
							}
						}

					} else if (cell_bean.get("om_col") != null
							&& om_col_key.equals(cell_bean.get("om_col").toString())) {
					    if("nativeplace".equals(om_col_key)|| "birthplace".equals(om_col_key)) {
					        String value = (String) map.get(om_col_key);
					        cell_bean.set("cellvalue", value.replace("省", "").replace("市", ""));
					    }else if("birthdate".equals(om_col_key)) {
							String birthDay=map.get("birthdate").toString().replace("-", "").replace(".", "");
							if(StringUtils.isNotEmpty(birthDay)&&birthDay.length()>6)
								birthDay=birthDay.substring(0, 6);//出生年月只取到月
							if(StringUtils.isNotEmpty(birthDay)) {
								birthDay=birthDay.substring(0,4)+"."+birthDay.substring(4,6);
							}
							cell_bean.set("cellvalue", birthDay);
						}else if("joinpartydate".equals(om_col_key)) {//入党时间数据处理
							String joinpartydate=map.get("joinpartydate").toString();
							String value="";
							if(joinpartydate.indexOf(";")>-1) {
								String[] arry=joinpartydate.split(";");
								if(StringUtils.isNumeric(arry[0])) {
									value=arry[1]+"</br>"+"("+arry[0].substring(0,4)+"."+arry[0].substring(4,6)+")";
								}else if(StringUtils.isNumeric(arry[1])){
									value=arry[0]+"</br>"+"("+arry[1].substring(0,4)+"."+arry[1].substring(4,6)+")";
								}else {
									value="("+arry[0]+","+arry[1]+")";
								}
							}else {
								//54564 只有入党时间时 处理导出格式
								if(StringUtils.isNotEmpty(joinpartydate)&&StringUtils.isNumeric(joinpartydate)&&joinpartydate.length()==6) {
									value=joinpartydate.substring(0,4)+"."+joinpartydate.substring(4,6);
								}else {
									value=joinpartydate;
								}
							}
							cell_bean.set("cellvalue", value);
						}else if("joinjobdate".equals(om_col_key)){
							String joinjobdate=map.get("joinjobdate").toString().replace("-", "").replace(".", "");
							if(StringUtils.isNotEmpty(joinjobdate)&&joinjobdate.length()>6) {
								joinjobdate=joinjobdate.substring(0, 6);
							}
							if(StringUtils.isNotEmpty(joinjobdate)) {
								joinjobdate=joinjobdate.substring(0,4)+"."+joinjobdate.substring(4,6);
							}
							cell_bean.set("cellvalue", joinjobdate);
						}else if("resume".equals(om_col_key)){
							ArrayList<String> resume_list=this.deatilResume(map.get(om_col_key).toString());
							ArrayList<HashMap<String,String>> record_list=new ArrayList<HashMap<String,String>>();
							HashMap<String,String> resume_map=null;
							for(String resume:resume_list) {
								 resume_map=new HashMap<String, String>();
								 String date = resume.substring(0, 16);
							     String startDate = "";
								 String endDate = "";
								 boolean ishave_line=false;
							      if (date.indexOf("--") > -1) { // 有日期存在
							        startDate = date.substring(0, date.indexOf("--"));
							        if(date.indexOf("--")>-1) {
							        	ishave_line=true;
							        }
							        endDate = date.substring(date.indexOf("--")+2, date.length());
							      }
							      String desc = resume.substring(16, resume.length());
							      resume_map.put("startdate", startDate.trim());
							      if(ishave_line) {
							    	  resume_map.put("line", "--");
							      }else {
							    	  resume_map.put("line", "  ");
							      }
							      resume_map.put("enddate", endDate.trim());
							      resume_map.put("desc", desc.trim().replace("\n", "\r\n"));
							      record_list.add(resume_map);
							}
//							cell_bean.set("recordlist",record_list);
							String bigText="";
							for(HashMap<String,String> resumeText_map:record_list) {
								String startDate=resumeText_map.get("startdate");
								if(StringUtils.isEmpty(startDate)) {
									startDate="       ";
								}
								String endDate=resumeText_map.get("enddate");
								if(StringUtils.isEmpty(endDate)) {
									endDate="       ";
								}else if("至今".equals(endDate)) {
									endDate="至今   ";
								}
								bigText+=startDate+resumeText_map.get("line")+endDate+"&nbsp;&nbsp;"+resumeText_map.get("desc")+"\r\n";
							}
							cell_bean.set("cellvalue",bigText);
						}else if("a0101".equals(om_col_key)){
							String a0101=(String)map.get(om_col_key);
							if(a0101.length()==2) {
								a0101=a0101.substring(0,1)+"  "+a0101.substring(1);
							}
							cell_bean.set("cellvalue", a0101);
						}else {
							cell_bean.set("cellvalue", map.get(om_col_key));
						}
					}
					if(cell_bean.get("om_col")!=null&&"age".equals(cell_bean.get("om_col").toString())){//计算年龄
						String birthDay=map.get("birthdate").toString().replace(".", "-");
						if(StringUtils.isNotEmpty(birthDay)) {
							cell_bean.set("cellvalue", "("+this.countAge(cal_now, birthDay)+"岁)");
						}
					}
					// 处理图片信息
					if ("photo".equals(cell_bean.get("om_col"))) {
						cell_bean.set("cellvalue",
								this.getPhotoPath(map.get("nbase").toString(), map.get("a0100").toString(), "file"));
					}
				}
				for (LazyDynaBean cell_bean : secPage_cellList) {
					if (cell_bean.get("om_col") != null && om_col_key.equals(cell_bean.get("om_col").toString())) {
						if("familyandrelation".equals(om_col_key)) {//家庭成员关系组装子集数据
							String familyandrelation=map.get(om_col_key).toString();
							ArrayList<HashMap<String,String>> list=deatilFamily(familyandrelation,"word");
							for(HashMap<String,String> subMap:list) {
								String birthday=subMap.get("chushengriqi");
								if(StringUtils.isNotEmpty(birthday)) {
									birthday=birthday.substring(0, 4)+"-"+birthday.substring(4, 6)+"-01";
									subMap.put("chushengriqi", countAge(cal_now, birthday));
								}
								String name=subMap.get("xingming");
								if(name.length()==2) {
									name=name.substring(0,1)+"  "+name.substring(1);
									subMap.put("xingming", name);
								}
							}
							cell_bean.set("recordlist",list);
						}else if("rewardsandpenalties".equals(om_col_key)|| "assessment".equals(om_col_key)|| "postreason".equals(om_col_key)){
							cell_bean.set("cellvalue", map.get(om_col_key).toString().replace("\\n", "<br/>").replace("\\r", "&nbsp;&nbsp;"));
						}else {
							cell_bean.set("cellvalue", map.get(om_col_key));
						}
					}
					
				}
			}
			if(!del_beanList.isEmpty()) {
                firstPage_cellList.removeAll(del_beanList);
            }
			ArrayList rleft_List = new ArrayList();
			ArrayList rtop_List = new ArrayList();
			for(LazyDynaBean cell_bean : firstPage_cellList) {
				if(!"".equals(cell_bean.get("om_col"))&&cell_bean.get("om_col")!=null&&!"photo".equals(cell_bean.get("om_col"))){
					cell_bean=resizeFont(cell_bean);
				}
				String left = cell_bean.get("rleft")+"";
				String rtop = cell_bean.get("rtop")+"";
				if(!rleft_List.contains(left)) {
				    rleft_List.add(left);
				}
				if(!rtop_List.contains(rtop)) {
				    rtop_List.add(rtop);
				}
				
			}
			firstPage.set("rtops",rtop_List);//行 数  内容
			firstPage.set("rlefts",rleft_List);//列 数  内容
			for(LazyDynaBean cell_bean : secPage_cellList) {
				if(!"".equals(cell_bean.get("om_col"))&&cell_bean.get("om_col")!=null){
					cell_bean=resizeFont(cell_bean);
				}
			}
			outputList.add(map.get("a0101").toString());//存储文件名
			outputList.add(firstPage);//存储第一页数据
			outputList.add(secPage);//存储第二页数据
			data_list.add(outputList);
		}
    	url=createFile(data_list, type, filetype);
    	return url;
	}
	/**
	 * 重新计算单元格内字体大小
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	private LazyDynaBean resizeFont(LazyDynaBean bean)throws Exception {
		ResetFontSizeUtil rfsu=new ResetFontSizeUtil();
		if(!(Boolean)bean.get("subflag")) {//非子集
			if(bean.get("cellvalue")!=null) {
				int size=rfsu.ResetFontSize(Double.parseDouble(bean.get("rwidth").toString()),
						Double.parseDouble(bean.get("rheight").toString()),bean.get("cellvalue").toString().replace("<br>", "\n").replace("<br/>", "\n").replace("<BR>", "\n").replace("<BR/>", "\n") , 
						Integer.parseInt(bean.get("fontsize").toString()),bean.get("fontname").toString() ,Integer.parseInt(bean.get("fonteffect").toString()));
				bean.set("fontsize", size);
			}
		}else {
			ArrayList subList=(ArrayList)bean.get("recordlist");
			if(subList.size()>0) {// colhead//是否输出列标题   width 列宽  datarowcount 指定行数
				int size=getSubFontSize(bean, rfsu);
				if(size!=0) {
					bean.set("fontsize", size);
				}
			}
		}
		return bean;
	}
	
    private int  getSubFontSize(LazyDynaBean bean,ResetFontSizeUtil rfsu)throws Exception {
    	String sub_domain=(String)bean.get("sub_domain");
    	int  width=(int)Float.parseFloat(bean.get("rwidth").toString());
    	int  height=(int)Float.parseFloat(bean.get("rheight").toString());
    	int  fontsize=Integer.parseInt(bean.get("fontsize").toString());
    	int fonteffect=Integer.parseInt(bean.get("fonteffect").toString());
    	ArrayList subList=(ArrayList)bean.get("recordlist");
    	String fontname=bean.get("fontname").toString();
    	ArrayList list=new ArrayList();
    	org.jdom.Document doc = null;
		doc =PubFunc.generateDom(sub_domain);
		org.jdom.Element paraEl=doc.getRootElement().getChild("para");
		String colhead=paraEl.getAttributeValue("colhead");//是否显示列标题
		String datarowcount=paraEl.getAttributeValue("datarowcount");//指定行数
		
		List<org.jdom.Element> fieldList=doc.getRootElement().getChildren("field");
		HashMap<String,String> map=new HashMap();
		int fieldWidth=0;
		for(org.jdom.Element el:fieldList) {
			String name=el.getAttributeValue("name").toLowerCase();
			String fieldW=el.getAttributeValue("width");
			map.put(name, fieldW);
			fieldWidth+=Integer.parseInt(fieldW);
		}
		float scale=width*1.0F/fieldWidth*1.0F;
		for(String key:map.keySet()) {
			map.put(key,((int)Integer.parseInt(map.get(key))*scale)+"");
		}
		int rows=subList.size();
		if(StringUtils.isNotEmpty(datarowcount)) {//设置默认行数
			if(subList.size()<Integer.parseInt(datarowcount)) {//子集数小于设置行数行数改为默认行数
				rows=Integer.parseInt(datarowcount);
			}
		}
		if("true".equals(colhead)) {
			rows+=1;
		}
		double rowHeight=ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(12.3));
		paraEl.setAttribute("colheadheight", "11");
		XMLOutputter outputter = new XMLOutputter();
   	    Format format=Format.getPrettyFormat();
   	    format.setEncoding("UTF-8");
   	    outputter.setFormat(format);
   	    bean.set("sub_domain", outputter.outputString(doc));
		int realSize=0;
		for(String key:map.keySet()) {
			for(int i=0;i<subList.size();i++) {
				HashMap subMap=(HashMap)subList.get(i);
				if(subMap.get(key)!=null&&StringUtils.isNotEmpty(subMap.get(key).toString())) {
					int subSize=rfsu.ResetFontSize(Double.parseDouble(map.get(key).toString()), rowHeight, subMap.get(key).toString(), fontsize, fontname, fonteffect);
					if(realSize!=0) {
						if(subSize<realSize)
							realSize=subSize;
					}else {
						realSize=subSize;
					}
				}
			}
		}
    	return realSize;
    }
	
	private String createFile(ArrayList data_list, String type, String filetype) throws Exception {
		String url = "";
		AnalysisWordUtil awu = new AnalysisWordUtil();
		//启用页边距设置
		awu.setUsePageMarginSet(true);
		HashMap<String, Integer> map_name = new HashMap<String, Integer>();// 存储姓名对象 防止有重名文件被覆盖
		// 解析集合 根据type 导出单个文件或者导出多个文件 filetype all 多人一文档 1 一人一文档 type pdf/word
		String filePath = System.getProperty("java.io.tmpdir") + File.separator;
		if (data_list.size() == 1) {
			ArrayList outputList = (ArrayList) data_list.get(0);
			String name = outputList.get(0).toString();
			outputList.remove(0);
			url = awu.analysisWord(this.userivew.getUserName() + "_" + name, outputList, 2);
			if ("pdf".equals(type)) {
				url = wordToPdf(filePath + url, url);
			}
		} else {
			ArrayList file_name_list = new ArrayList();
			ArrayList page_list = new ArrayList();// 多人一文档 数据
			for (int i = 0; i < data_list.size(); i++) {
				ArrayList outputList = (ArrayList) data_list.get(i);
				if ("1".equals(filetype)) {
					String a0101 = outputList.get(0).toString();
					outputList.remove(0);
					a0101 = StringUtils.isNotEmpty(a0101) ? a0101 : "空";
					if (map_name.containsKey(a0101)) {
						int index = map_name.get(a0101) + 1;
						map_name.put(a0101, index);
						a0101 = a0101 + "（" + index + "）";
					} else {
						map_name.put(a0101, 0);
					}
					url = awu.analysisWord(this.userivew.getUserName() + "_" + a0101, outputList, 2);
					if ("pdf".equals(type)) {
						url = wordToPdf(filePath + url, url);
					}
					file_name_list.add(url);
				} else {
					page_list.add(outputList.get(1));
					page_list.add(outputList.get(2));
				}
			}
			if ("1".equals(filetype)) {// 一人一文档 压缩文件
				url = createZipFile(file_name_list, "任免表文件");
			} else {// 多人一文档
				if (page_list.size() > 0) {
					url = awu.analysisWord(this.userivew.getUserName() + "_干部任免表", page_list, 2);
					if ("pdf".equals(type)) {
						url = wordToPdf(filePath + url, url);
					}
				}
			}
		}
		url=PubFunc.encrypt(url);
		return url;
	}
	
	/**
	 * 生成的word文件转为pdf文件
	 * @param filePath
	 * @param url
	 * @return
	 */
	private String  wordToPdf(String filePath,String url){
		/**
		 * String filePath = System.getProperty("java.io.tmpdir")+File.separator+filename;
		Document doc = new Document(filePath);
		DocumentBuilder builder = new AsposeLicenseUtil(doc);
		int lastindex = filename.lastIndexOf(".");
		filename = filename.substring(0,lastindex)+".pdf";
		doc.save(System.getProperty("java.io.tmpdir")+File.separator+filename);
		 * 
		 */
		try {
			com.aspose.words.Document doc = new com.aspose.words.Document(filePath);
			DocumentBuilder builder = new AsposeLicenseUtil(doc);
			int lastindex = url.lastIndexOf(".");
			url = url.substring(0,lastindex)+".pdf";
			doc.save(System.getProperty("java.io.tmpdir")+File.separator+url);
			//清除生成的word(tomcat临时文件中)
			File docfile = new File(filePath);
			if(docfile.exists())
				docfile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	/**
	 * 计算年龄
	 * @param cal_now
	 * @param birthday
	 * @return
	 * @throws Exception
	 */
	private String countAge(Calendar cal_now,String birthday)throws Exception {
		String age="";
		if(StringUtils.isNotEmpty(birthday)) {
		    //1900-01
		    if(birthday.length()==7) {
		        birthday+="-00";
		    }
			int year_now=cal_now.get(Calendar.YEAR);
			int month_now=cal_now.get(Calendar.MONTH);
			SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal_bir=Calendar.getInstance();
			cal_bir.setTime(simple.parse(birthday));
			int year_bir=cal_bir.get(Calendar.YEAR);
			int month_bir=cal_bir.get(Calendar.MONTH);
			if(month_now-month_bir<0) {
				age=(year_now-year_bir-1)+"";
			}else {
				age=(year_now-year_bir)+"";
			}
		}
		return age;
	}
	
	/**
	 * 
	 * @param dataList
	 *            数据
	 * @throws Exception 
	 */
	@Override
    public String outFile(ArrayList<DynaBean> dataList) throws Exception {
			String filename="";
			Document doc = null;
			OutputFormat format = OutputFormat.createPrettyPrint(); 
			format.setEncoding("UTF-8"); 
			format.setNewlines(true);
			format.setIndent(false);
			format.setNewlines(true); //设置是否换行
			String path=System.getProperty("java.io.tmpdir")+File.separator;
			HashMap nameMap=new HashMap();
			XMLWriter xmlWriter = null;
			HashMap<String,String> photoMap=getPhotoMap(dataList);
			ArrayList<String> fileList=new ArrayList<String>();
			for(int i=0;i<dataList.size();i++) {
				HashMap map=PubFunc.DynaBean2Map(dataList.get(i));
				if(photoMap.containsKey(map.get("guidkey").toString())) {
					map.put("photo", photoMap.get(map.get("guidkey").toString()));
				}
				String name=map.get("a0101").toString();
				if(nameMap.containsKey(name)) {
					int index=Integer.parseInt(nameMap.get(name).toString());
					index=index+1;
					nameMap.put(name, index);
					name=name+"("+index+")";
				}else {
					nameMap.put(name,"0");
				}
				File file=new File(path+name+".lrmx");
				if(file.exists()) {
					file.delete();
				}
				
				LinkedHashMap<String,String> linkMap=getLRMXMap(map);
				doc=DocumentHelper.createDocument();
				Element root=doc.addElement("Person");
				doc.setRootElement(root);
				for (String key : linkMap.keySet()) {
					Element child=root.addElement(key);
					if("JiaTingChengYuan".equals(key)&&linkMap.get(key)!=null) {
						if(linkMap.get(key).indexOf("Item")>-1)
							deatilChild(linkMap.get(key), child);
						else
							child.setText(linkMap.get(key));
					}else {
						if("JiangChengQingKuang".equals(key)||"JianLi".equals(key)||"NianDuKaoHeJieGuo".equals(key)||"RenMianLiYou".equals(key)) {
							child.addCDATA(linkMap.get(key)!=null?linkMap.get(key).replace("\\n", "\n").replace("\\r", "  ").replace("\\r\\n", "\r\n"):"");
						}else {
						    String value = linkMap.get(key)!=null?linkMap.get(key):"";
						    if("JiGuan".equalsIgnoreCase(key)||"ChuShengDi".equalsIgnoreCase(key)) {
						        value = value.replace("省", "").replace("市", "");
						    }
							child.setText(value);
						}	
					}
				}
				xmlWriter=new XMLWriter(new FileOutputStream(new File(path+name+".lrmx")),format);
				xmlWriter.write(root);
				xmlWriter.close();
				fileList.add(name+".lrmx");
			}
			if(fileList.size()<1)
				throw GeneralExceptionHandler.Handle(new Exception("导出文件失败!"));
			if(fileList.size()==1) {
				filename=PubFunc.encrypt(fileList.get(0));
			}else {//多个文件压缩文件
				filename=PubFunc.encrypt(createZipFile(fileList, "任免表"));
			}
			return filename;
	}
	
	/**
	 * 导出人员库图片 base64加密
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private HashMap<String,String> getPhotoMap(ArrayList<DynaBean> dataList)throws Exception{
		HashMap<String, String> map=new HashMap<String, String>();
		ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
		String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
		HashMap<String,ArrayList<String>> keyMap=new HashMap<String,ArrayList<String>>();//人员库：guidkeyList
		PhotoImgBo imgbo=new PhotoImgBo(this.conn);
		BASE64Encoder encoder = new BASE64Encoder();
		InputStream input=null;
		byte[] data=null;
		for(DynaBean bean:dataList) {
			String nbase=bean.get("nbase").toString();
			String guidKey=bean.get("guidkey").toString();
			if(StringUtils.isNotEmpty(nbase)) {
				if(StringUtils.isNotEmpty(rootDir)) {
					String photourl=rootDir+File.separator+"multimedia"+File.separator+imgbo.getPhotoRelativeDir(nbase, bean.get("a0100").toString());
					String filename=imgbo.getPersonImageWholeName(photourl, "photo");
					File file=new File(photourl+File.separator+filename);
					if(file.isFile()) {
						input=new FileInputStream(file);
						data=new byte[input.available()];
						input.read(data);
						input.close();
						String decode=encoder.encode(data);
						if(decode.length()>0) {
							map.put(guidKey, decode);
							continue;
						}
					}
				}
				
				
				if(keyMap.containsKey(nbase)) {
					ArrayList<String> list=keyMap.get(nbase);
					list.add(guidKey);
				}else {
					ArrayList<String> list=new ArrayList<String>();
					list.add(guidKey);
					keyMap.put(nbase, list);
				}
			}
			
		}
		StringBuffer sbf=new StringBuffer();
		ArrayList<String> list=null;
		for(String key:keyMap.keySet()) {
			list=keyMap.get(key);
			String whereSql="";
			for(int i=0;i<list.size();i++) {
				whereSql+=",'"+list.get(i)+"'";
			}
			if(whereSql.length()>0) {
				sbf.append(" union all select "+key+"A01.guidkey,"+key+"A00.ole from "+key+"A01 left join "+key+"A00 on "+key+"A01.a0100="+key+"A00.a0100  where flag='P' "
						+ "and "+key+"A01.guidkey in ( "
						+whereSql.substring(1)
						+ ") ");
			}
		}
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			if(sbf.length()>0) {
				rs=dao.search(sbf.toString().substring(10));
				while(rs.next()) {
					input=rs.getBinaryStream("Ole");
					data=new byte[input.available()];
					input.read(data);
					input.close();
					String decode=encoder.encode(data);
					if(decode.length()>0)
						map.put(rs.getString("guidkey"), decode);
					
				}
			}
			
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeIoResource(input);
		}
		
		return map;
	} 
	
	/***
	 * 家庭成员 内容解析
	 * */
	private void deatilChild(String items,Element childEl) throws Exception {

		Document doc=DocumentHelper.parseText("<rootChild>"+items+"</rootChild>");
		Element rootEl=doc.getRootElement();
		Iterator<Element> iteror=rootEl.elementIterator("Item");
		while(iteror.hasNext()) {
			Element item=iteror.next();
			Element newItem=childEl.addElement(item.getName());
			Iterator<Element> oldItem=item.elementIterator();
			while(oldItem.hasNext()) {
				Element oldChild=oldItem.next();
				Element newChild=newItem.addElement(oldChild.getName());
				newChild.setText(oldChild.getText());
			}
		}
		
	
		
	}
	
	/**
	 * 生成导出文件 压缩文件
	 * **/
	private String createZipFile(ArrayList filenames, String tabName) {
		String tmpFileName = this.userivew.getUserName() + "_" + tabName + ".zip";
		byte[] buffer = new byte[2048];
		String filePath = System.getProperty("java.io.tmpdir") + File.separator;
		String strZipPath = filePath + tmpFileName;
		BufferedInputStream origin = null;
		ZipOutputStream out = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filePath + tmpFileName);
			out = new ZipOutputStream(fileOutputStream);
			for (int i = 0; i < filenames.size(); i++) {
				FileInputStream fis = null;
				File file = null;
				try {
					file = new File(filePath + filenames.get(i));
					fis = new FileInputStream(file);
					origin = new BufferedInputStream(fis, 2048);
					out.putNextEntry(new ZipEntry(file.getName()));
					out.setEncoding("GBK");
					int count;
					while ((count = origin.read(buffer, 0, 2048)) != -1) {
						out.write(buffer, 0, count);
					}
				} finally {
					PubFunc.closeResource(origin);
					PubFunc.closeResource(fis);
				}
				if (file.exists())
					file.delete();
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOutputStream);
			PubFunc.closeResource(out);
		}
		return tmpFileName;
	}
	
	
	private LinkedHashMap getLRMXMap(HashMap map) {
		LinkedHashMap lrmxMap=new LinkedHashMap();
		
		lrmxMap.put("XingMing", map.get("a0101"));
		lrmxMap.put("XingBie", map.get("sex"));
		String birthDay=map.get("birthdate").toString().replace("-", "").replace(".", "");
		if(StringUtils.isNotEmpty(birthDay)&&birthDay.length()>6)
			birthDay=birthDay.substring(0, 6);//出生年月只取到月
		lrmxMap.put("ChuShengNianYue", birthDay);
		lrmxMap.put("MinZu", map.get("nation"));
		lrmxMap.put("JiGuan", map.get("nativeplace"));
		lrmxMap.put("ChuShengDi", map.get("birthplace"));
		String joinpartydate=map.get("joinpartydate").toString().replace("-", "").replace(".", "");
		lrmxMap.put("RuDangShiJian", joinpartydate);
		String joinjobdate=map.get("joinjobdate").toString().replace("-", "").replace(".", "");
		if(StringUtils.isNotEmpty(joinjobdate)&&joinjobdate.length()>6)
			joinjobdate=joinjobdate.substring(0, 6);
		lrmxMap.put("CanJiaGongZuoShiJian", joinjobdate);
		lrmxMap.put("JianKangZhuangKuang", map.get("health"));
		lrmxMap.put("ZhuanYeJiShuZhiWu", map.get("majorpost"));
		lrmxMap.put("ShuXiZhuanYeYouHeZhuanChang", map.get("majorspecialty"));
		//education 学历  degree 学位  全职`在职以`分隔 
		String education=map.get("education").toString();
		String fullTimeEDU="";//全职学历
		String workEDU="";//在职学历
		if(education.indexOf("`")>-1) {//学历学位等以`分隔符后无数据，特殊处理加空格防止报错
			education=education.endsWith("`")?education+" ":education;
			fullTimeEDU=education.split("`")[0];
			workEDU=education.split("`")[1];
		}
		String fullTimeDRE="";
		String workDRE="";
		String degree=map.get("degree").toString();
		if(degree.indexOf("`")>-1) {
			degree=degree.endsWith("`")?degree+" ":degree;
			fullTimeDRE=degree.split("`")[0];
			workDRE=degree.split("`")[1];
		}
		
		String school=map.get("school").toString();//学校
		String fullTimeSc="";//全日制学校
		String workSchool="";//在职学校
		if(school.indexOf("`")>-1) {
			school=school.endsWith("`")?school+" ":school;
			fullTimeSc=school.split("`")[0];
			workSchool=school.split("`")[1];
		}
		String educationmajor=map.get("educationmajor").toString();//专业
		String fullTimeMajor="";//全日制专业
		String workMajor="";//在职专业
		if(educationmajor.indexOf("`")>-1) {
			educationmajor=educationmajor.endsWith("`")?educationmajor+" ":educationmajor;
			fullTimeMajor=educationmajor.split("`")[0];
			workMajor=educationmajor.split("`")[1];
		}
		
		lrmxMap.put("QuanRiZhiJiaoYu_XueLi", fullTimeEDU);
		lrmxMap.put("QuanRiZhiJiaoYu_XueWei", fullTimeDRE);
		
		lrmxMap.put("QuanRiZhiJiaoYu_XueLi_BiYeYuanXiaoXi", fullTimeSc);
		lrmxMap.put("QuanRiZhiJiaoYu_XueWei_BiYeYuanXiaoXi", fullTimeMajor);

		lrmxMap.put("ZaiZhiJiaoYu_XueLi",workEDU);
		lrmxMap.put("ZaiZhiJiaoYu_XueWei", workDRE);
		
		lrmxMap.put("ZaiZhiJiaoYu_XueLi_BiYeYuanXiaoXi", workSchool);
		lrmxMap.put("ZaiZhiJiaoYu_XueWei_BiYeYuanXiaoXi", workMajor);
		
		lrmxMap.put("XianRenZhiWu", map.get("currentpost"));
		lrmxMap.put("NiRenZhiWu", map.get("preparepost"));
		lrmxMap.put("NiMianZhiWu", map.get("terminalpost"));
		String resume=(String)map.get("resume");
		resume=resume.replaceAll("至今       ", "至今     ");	
		lrmxMap.put("JianLi", resumeXmlFormat(resume));
		
		lrmxMap.put("JiangChengQingKuang", map.get("rewardsandpenalties").toString());
		lrmxMap.put("NianDuKaoHeJieGuo", map.get("assessment"));
		lrmxMap.put("RenMianLiYou", map.get("postreason"));
		//String item="<Item><ChengWei>父亲</ChengWei><XingMing>张文</XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>母亲</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>妻子</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>儿子</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>哥哥</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>弟弟</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>姐姐</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item><Item><ChengWei>姐姐</ChengWei><XingMing></XingMing><ChuShengRiQi></ChuShengRiQi><ZhengZhiMianMao></ZhengZhiMianMao><GongZuoDanWeiJiZhiWu></GongZuoDanWeiJiZhiWu></Item>";
		lrmxMap.put("JiaTingChengYuan",map.get("familyandrelation"));
		
		lrmxMap.put("ChengBaoDanWei", "");//呈报单位
		lrmxMap.put("JiSuanNianLingShiJian", this.date);//计算年龄时间  当前日期
		lrmxMap.put("TianBiaoShiJian", "");//填报时间  空
		lrmxMap.put("TianBiaoRen", "");//填报人
		lrmxMap.put("ShenFenZheng", map.get("id_number"));
		if(map.containsKey("photo"))
			lrmxMap.put("ZhaoPian", map.get("photo"));//照片
		else
			lrmxMap.put("ZhaoPian", "");//照片
		lrmxMap.put("Version", "");
		return lrmxMap;
	}
	/**
	 * 解析简历 个性化
	 * @param resume
	 * @return
	 * @throws Exception
	 */
	private String resumeXmlFormat(String resume) {
		ArrayList list=deatilResume(resume);
		StringBuffer sbf=new StringBuffer();
		for(Object obj:list) {
			String str=(String)obj;
			str=str.replace("\n", "");
			//去除开头的日期 XXXX.xx--XXXX.xx或者XXXX.xx--至今   
			if(str.indexOf("--至今   ")>-1) {
				str=str.substring(14);
			}else {
				str=str.substring(16);
			}
			//以空两格开头的为正确格式 否则不处理
			if(!str.startsWith("  ")) {
				sbf.append((String)obj+"\n");
				//格式不正确 继续下一条数据处理
				continue;
			}
			//解析格式中是否包含(其中：XXXXX;XXXXXX)
			str=str.replace("（", "(").replace("）", ")").replace("：", ":").replace("；", ";");
			//校验是否有符合格式的内容
			if(str.indexOf("(其中:")>-1) {
			    Pattern patt=Pattern.compile("(.+)(\\(其中:(.+)\\))");
	            Matcher matcher=patt.matcher(str);
	            if(!matcher.find()) {
	                sbf.append((String)obj);
	                continue;
	            }
	            str=((String)obj).replace("（", "(").replace("）", ")").replace("：", ":").replace("；", ";");
	            str=str.replace("\n", "");
	            String[] arry_str=str.split("\\(其中:");
	            sbf.append(arry_str[0]+"\n");
	            String split_str="(其中:"+arry_str[1];
	            sbf.append("                  "+"(其中:"+split_str+"\n");
	            /*if(split_str.indexOf(";")>-1) {
	                for(String key:split_str.split(";")) {
	                    if(key.endsWith(")")) {
	                        sbf.append("                  "+key+"\n");
	                    }else {
	                        sbf.append("                  "+key+";"+"\n");
	                    }
	                    
	                }
	            }else {
	                sbf.append("                  "+"(其中:"+split_str+"\n");
	            }*/
			}else if(str.indexOf("(其间:")>-1) {
			    Pattern patt=Pattern.compile("(.+)(\\(其间:(.+)\\))");
                Matcher matcher=patt.matcher(str);
                if(!matcher.find()) {
                    sbf.append((String)obj);
                    continue;
                }
                str=((String)obj).replace("（", "(").replace("）", ")").replace("：", ":").replace("；", ";");
                str=str.replace("\n", "");
                String[] arry_str=str.split("\\(其间:");
                sbf.append(arry_str[0]+"\n");
                String split_str="(其间:"+arry_str[1];
                sbf.append("                  "+"(其间:"+split_str+"\n");
			}else {
			    sbf.append((String)obj);
			}
			
		}
		return sbf.toString();
	}
	
	/**
	 * 姓名、单位、部门、岗位 固定指标 appointment_dismissal_form 表中固定字段
	 */
	 private ArrayList getGenerFieldList() {
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		
		// 姓名
		map.put("fieldsetid", "A01");
		map.put("itemid", "A0101");
		map.put("itemdesc", "姓名");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 单位
		map = new HashMap();
		map.put("fieldsetid", "A01");
		map.put("itemid", "B0110");
		map.put("itemdesc", "单位名称");
		map.put("itemtype", "A");
		map.put("codesetid", "UN");
		list.add(map);
		
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "");
		map.put("itemdesc", "任职单位");
		map.put("itemtype", "A");
		map.put("codesetid", "UN");
		list.add(map);
		
		// 部门
		map = new HashMap();
		map.put("fieldsetid", "A01");
		map.put("itemid", "E0122");
		map.put("itemdesc", "部门名称");
		map.put("itemtype", "A");
		map.put("codesetid", "UM");
		list.add(map);
		// 岗位
		map = new HashMap();
		map.put("fieldsetid", "A01");
		map.put("itemid", "E01A1");
		map.put("itemdesc", "岗位名称");
		map.put("itemtype", "A");
		map.put("codesetid", "@K");
		list.add(map);

		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "nbase");
		map.put("itemdesc", "人员库");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);

		
		// 性别
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "sex");
		map.put("itemdesc", "性别");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);

		// 出生日期
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "birthdate");
		map.put("itemdesc", "出生日期");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 民族
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "nation");
		map.put("itemdesc", "民族");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 籍贯
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "nativeplace");
		map.put("itemdesc", "籍贯");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 出生地
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "birthplace");
		map.put("itemdesc", "出生地");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 入党时间
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "joinpartydate");
		map.put("itemdesc", "入党时间");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 参加工作时间
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "joinjobdate");
		map.put("itemdesc", "参加工作时间");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 健康状况
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "health");
		map.put("itemdesc", "健康状况");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 专业技术职务
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "majorpost");
		map.put("itemdesc", "专业技术职务");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 熟悉专业有何专长
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "majorspecialty");
		map.put("itemdesc", "熟悉专业有何专长");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 学历
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "education");
		map.put("itemdesc", "学历");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 学位
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "degree");
		map.put("itemdesc", "学位");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 毕业学校
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "school");
		map.put("itemdesc", "毕业学校");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 所学专业
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "educationmajor");
		map.put("itemdesc", "所学专业");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 现任职务
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "currentpost");
		map.put("itemdesc", "现任职务");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 拟任职务
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "preparepost");
		map.put("itemdesc", "拟任职务");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 拟免职务
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "terminalpost");
		map.put("itemdesc", "拟免职务");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 奖惩情况
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "rewardsandpenalties"); 
		map.put("itemdesc", "奖惩情况");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 年度考核结果
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "assessment");
		map.put("itemdesc", "年度考核结果");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		// 任免理由
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "postreason");
		map.put("itemdesc", "任免理由");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		
		
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "resume");
		map.put("itemdesc", "简历");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		
		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "familyandrelation");
		map.put("itemdesc", "家庭成员");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);

		map = new HashMap();
		// map.put("fieldsetid", "");
		map.put("itemid", "id_number");
		map.put("itemdesc", "身份证号码");
		map.put("itemtype", "A");
		map.put("codesetid", "0");
		list.add(map);
		
		return list;
	}
	
	/**
	 * 第一页导出需要参数
	 * */ 
	@Override
    public  LazyDynaBean getFistPageBean(){
		LazyDynaBean pgmap=new LazyDynaBean();
		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		LazyDynaBean bean=new LazyDynaBean();
		bean.set("rheight",48);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",1);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",60);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("lsize", 2);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		bean.set("hz","姓 名`");
		bean.set("cellvalue","姓  名");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",48);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",2);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",85);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","a0101`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "a0101");
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",48);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",17);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",63);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","性 别`");
		bean.set("cellvalue","性  别");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",48);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",249);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",20);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",93);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","sex`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "sex");
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",48);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",24);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",81);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","出生年月`（岁）`");
		bean.set("cellvalue","出生年月\n（岁）");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",25);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",28);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",87);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","birthdate`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "birthdate");
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		
		bean.set("isExpand","false");//单元格内容不扩展，仅针对图片处理
		bean.set("rheight",199);
		bean.set("realheight",199);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",510);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",44);
		bean.set("flag","P");
		bean.set("t",1);
		bean.set("rwidth",144);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",59);
		bean.set("hz","照片`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "photo");
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",23);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",45);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",87);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",84);
		bean.set("hz","age`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "age");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",3);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",60);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","民 族`");
		bean.set("cellvalue","民  族");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",4);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",85);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","nation`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "nation");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",18);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",63);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","籍 贯`");
		bean.set("cellvalue","籍  贯");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",249);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",21);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",93);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","nativeplace`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "nativeplace");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",25);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",81);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","出生地`");
		bean.set("cellvalue","出 生 地");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",44);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",29);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",87);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",107);
		bean.set("hz","birthplace`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "birthplace");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",5);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",60);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","入 党`时 间`");
		bean.set("cellvalue","入  党\n时  间");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","M");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",6);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",85);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","joinpartydate");
		bean.set("om_col","joinpartydate");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",19);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",63);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","参加工`作时间`");
		bean.set("cellvalue","参加工\n作时间");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",249);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",22);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",93);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","joinjobdate`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "joinjobdate");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",26);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",81);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","健康状况`");
		bean.set("cellvalue","健康状况");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",56);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",30);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",87);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",151);
		bean.set("hz","health`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "health");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",51);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",7);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",60);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",207);
		bean.set("hz","专业技`术职务`");
		bean.set("cellvalue","专业技\n术职务");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",51);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",8);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",148);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",207);
		bean.set("hz","majorpost`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "majorpost");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",51);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",249);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",23);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",93);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",207);
		bean.set("hz","熟悉专业`有何专长`");
		bean.set("cellvalue","熟悉专业\n有何专长");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",51);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",27);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",168);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",207);
		bean.set("hz","majorspecialty`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "majorspecialty");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",98);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",10);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",60);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",258);
		bean.set("hz","学 历`学 位`");
		bean.set("cellvalue","学 历\n学 位");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",49);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",31);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",85);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",258);
		bean.set("hz","全日制`教  育`");
		bean.set("cellvalue","全日制\n教  育");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",26);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",33);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",156);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",258);
		bean.set("hz","education_0`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "education_0");
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",49);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",35);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",81);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",258);
		bean.set("hz","毕业院校`系及专业`");
		bean.set("cellvalue","毕业院校\n系及专业");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",26);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",37);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",231);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",258);
		bean.set("hz","school_0`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "school_0");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",23);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",39);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",156);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",284);
		bean.set("hz","degree_0`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "degree_0");
		list.add(bean);
		  
		bean=new LazyDynaBean(); 
		bean.set("rheight",23);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",41);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",231);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",284);
		bean.set("hz","educationmajor_0`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "educationmajor_0");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",49);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",101);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",32);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",85);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",307);
		bean.set("hz","在  职`教  育`");
		bean.set("cellvalue","在  职\n教  育");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",26);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",34);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",156);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",307);
		bean.set("hz","education_1`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "education_1");
		list.add(bean);
		  
		bean=new LazyDynaBean(); 
		bean.set("rheight",49);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",342);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",36);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",81);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",307);
		bean.set("hz","毕业院校`系及专业`");
		bean.set("cellvalue","毕业院校\n系及专业");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",26);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",38);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",231);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",307);
		bean.set("hz","school_1`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "school_1");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",23);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",40);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",156);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",333);
		bean.set("hz","degree_1`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "degree_1");
		list.add(bean);
		  
		bean=new LazyDynaBean(); 
		bean.set("rheight",23);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",423);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",42);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",231);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",333);
		bean.set("hz","educationmajor_1`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "educationmajor_1");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",53);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",11);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",145);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",356);
		bean.set("hz","现 任 职 务`");
		bean.set("cellvalue","现  任  职  务");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",53);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",14);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",468);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",356);
		bean.set("hz","currentpost`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "currentpost");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();  
		bean.set("rheight",41);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",12);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",145);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",399);
		bean.set("hz","拟 任 职 务`");
		bean.set("cellvalue","拟  任  职  务");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",41);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",15);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",468);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",399);
		bean.set("hz","preparepost`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "preparepost");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",43);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",13);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",145);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",444);
		bean.set("hz","拟 免 职 务`");
		bean.set("cellvalue","拟  免  职  务");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",43);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",186);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",16);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",468);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",444);
		bean.set("hz","terminalpost`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "terminalpost");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",475);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",41);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",9);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",41);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",490);
		bean.set("hz","简```历`");
		bean.set("cellvalue","简\n\n\n历");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",0);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",475);
		bean.set("field_type","M");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",82);
		bean.set("align",0);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","resume");
		bean.set("subflag",false);
		bean.set("gridno",43);
		bean.set("flag","A");
		bean.set("t",1);
		bean.set("rwidth",572);
		bean.set("r",1);
		bean.set("special_M", "true");
		bean.set("inputType",0);
		bean.set("rtop",490);
		bean.set("hz","resume`");
		bean.set("cellvalue","简\n\n\n历");
		bean.set("recordlist",new ArrayList());
		bean.set("fontsize",11);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?>"
				+ "<sub_para>"
				+ "<para setname=\"resume\" hl=\"false\" vl=\"false\" blank_grid=\"false\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"startDate`line`endDate`desc`\" isview=\"0\" func=\"0\" colheadheight=\"4\" datarowcount=\"0\" FontColor=\"0\"/>"
				+ "<field name=\"startDate\" need=\"false\" width=\"6\" title=\"起始时间\" default=\"\" slop=\"0\" pre=\"\" align=\"1\" valign=\"0\"/>"
				+ "<field name=\"line\" need=\"false\" width=\"3\" title=\"横线\" default=\"\" slop=\"\" pre=\"\" align=\"1\" valign=\"0\"/>"
				+ "<field name=\"endDate\" need=\"false\" width=\"6\" title=\"终止时间\" default=\"\" slop=\"\" pre=\"\" align=\"1\" valign=\"0\"/>"
				+ "<field name=\"desc\" need=\"false\" width=\"41\" title=\"描述\" default=\"\" slop=\"0\" pre=\"\" align=\"0\" valign=\"0\"/>"
				+ "</sub_para>");
		bean.set("pageid",0);
		bean.set("om_col", "resume");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 2);
		list.add(bean);
		  
		  

		pgmap.set("context", list);
		
//		String[] rtops= {"103", "128", "151", "195", "237", "282", "308", "331", "357", "380", "433", "474", "517"};
		String[] rtops= {"59", "84", "107", "151", "207", "258", "284", "307", "333", "356", "399", "444", "490"};
		String[] rlefts= {"41", "82", "101", "186", "249", "342", "423", "510"};
		
		pgmap.set("wh",wordPageSetting());
		pgmap.set("paperOrientation", "0");
		pgmap.set("rtops",new ArrayList(Arrays.asList(rtops)));//行 数  内容
		pgmap.set("rlefts",new ArrayList(Arrays.asList(rlefts)));//列 数  内容
		ArrayList<Integer> top_list=new ArrayList<Integer>();
		top_list.add(30);
		pgmap.set("midtitle_img", new ArrayList());
		pgmap.set("rtops_t",top_list );
		bean =new LazyDynaBean();
		bean.set("rheight", 29);
		bean.set("rleft", 230);
		bean.set("fonteffect",1);
		bean.set("fontname", "宋体");
		bean.set("titlevalue", "干 部 任 免 审 批 表");
		bean.set("flag", 0);
		bean.set("gridno", 1);
		bean.set("rwidth", 210);
		bean.set("rtop", 30);
		bean.set("extendattr", "<image></image><format>0</format><prefix></prefix><FontColor>0</FontColor>");
		bean.set("hz", "干部任免审批表");
		bean.set("fontsize", 24);
		bean.set("pageid", 0);
		ArrayList<LazyDynaBean> topBean=new ArrayList<LazyDynaBean>();
		topBean.add(bean);
		pgmap.set("toptitle",topBean);
		pgmap.set("rtops_b",new ArrayList());
		pgmap.set("bomtitle",new ArrayList());
		return pgmap;
	}
	/***
	 * 第二页导出需要参数
	 * */
	@Override
    public  LazyDynaBean  getSecPageBean(){
		LazyDynaBean pgmap=new LazyDynaBean();
		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		LazyDynaBean bean=new LazyDynaBean();  
		bean.set("rheight",95);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",1);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",108);
		bean.set("hz","奖`惩`情`况`");
		bean.set("cellvalue","奖\n惩\n情\n况");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",95);
		bean.set("field_type","M");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",6);//水平居左
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",7);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",579);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",108);
		bean.set("hz","rewardsandpenalties`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("om_col", "rewardsandpenalties");
		bean.set("lsize", 1);
		bean.set("tsize", 2);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		
		bean=new LazyDynaBean();
		bean.set("rheight",68);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",2);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",203);
		bean.set("hz","年度`考核`结果`");
		bean.set("cellvalue","年核\n度结\n考果");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",68);
		bean.set("field_type","M");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",8);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",579);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",203);
		bean.set("hz","assessment`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("om_col", "assessment");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);  
		  
		bean=new LazyDynaBean();
		bean.set("rheight",91);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",3);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",271);
		bean.set("hz","任`免`理`由`");
		bean.set("cellvalue","任\n免\n理\n由");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",91);
		bean.set("field_type","M");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",9);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",579);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",271);
		bean.set("hz","postreason`");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("om_col", "postreason");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 0);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",368);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",4);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",362);
		bean.set("hz","主`要`家`庭`成`员`及`社`会`关`系`");
		bean.set("cellvalue","家\n庭\n主\n要\n成\n员\n及\n重\n要\n社\n会\n关\n系");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",368);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("recordlist",new ArrayList());
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",true);
		bean.set("gridno",10);
		bean.set("flag","A");
		bean.set("t",1);
		bean.set("rwidth",579);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",362);
		bean.set("hz","{家庭成员和主要社会关系信息}`");
		bean.set("fontsize",14);
		bean.set("sub_domain",
				"<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			  + "<sub_para><para setname=\"\" hl=\"true\" vl=\"true\" blank_grid=\"true\" multimedia=\"true\" colhead=\"true\" fields=\"ChengWei`XingMing`ChuShengRiQi`ZhengZhiMianMao`GongZuoDanWeiJiZhiWu`\" isview=\"0\" func=\"0\" colheadheight=\"14\"  datarowcount=\"10\" />"
			  + "<field name=\"ChengWei\" need=\"false\" width=\"12\" title=\"称  谓\" default=\"\" slop=\"0\" pre=\"\" align=\"1\" valign=\"1\" />"
			  + "<field name=\"XingMing\" need=\"false\" width=\"12\" title=\"姓  名\" default=\"\" slop=\"0\" pre=\"\" align=\"1\" valign=\"1\" />"
			  + "<field name=\"ChuShengRiQi\" need=\"false\" width=\"5\" title=\"年 龄\" default=\"\" slop=\"12\" pre=\"\" align=\"1\" valign=\"1\" />"
			  + "<field name=\"ZhengZhiMianMao\" need=\"false\" width=\"10\" title=\"政 治  面 貌\" default=\"\" slop=\"0\" pre=\"\" align=\"1\" valign=\"1\" />"
			  + "<field name=\"GongZuoDanWeiJiZhiWu\" need=\"false\" width=\"44\" title=\"工 作 单 位 及 职 务\" default=\"\" slop=\"0\" pre=\"\" align=\"0\" valign=\"1\" />"
			  + "</sub_para>");
		bean.set("pageid",1);
		bean.set("om_col", "familyandrelation");
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",138);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",6);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",730);
		bean.set("hz","呈`报`单`位`");
		bean.set("cellvalue","呈\n报\n单\n位");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",108);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",12);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",579);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",730);
		bean.set("hz","");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",30);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",15);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",355);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",838);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",30);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",484);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",16);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",224);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",838);
		bean.set("hz","  年   月   日`");
		bean.set("cellvalue","     年   月   日");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",135);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",79);
		bean.set("align",6);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",5);
		bean.set("t",1);
		bean.set("flag","H");
		bean.set("rwidth",50);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",868);
		bean.set("hz","审`批`机`关`意`见`");
		bean.set("cellvalue"," 审意\n 批　\n 机　\n 关见");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 2);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",82);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",11);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",264);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",868);
		bean.set("hz","");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",135);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",393);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",13);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",51);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",868);
		bean.set("hz","行机`政关`任意`免见`");
		bean.set("cellvalue","行任\n政免\n机意\n关见");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",82);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",444);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",14);
		bean.set("flag","H");
		bean.set("t",1);
		bean.set("rwidth",264);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",868);
		bean.set("hz","");
		bean.set("cellvalue","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",21);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",137);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",266);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",25);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",100);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","（盖章）`");
		bean.set("cellvalue","（盖章）");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",366);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",26);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",27);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",444);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",22);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",137);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",581);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",23);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",100);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","（盖章）`");
		bean.set("cellvalue","（盖章）");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",21);
		bean.set("field_type","");
		bean.set("b",0);
		bean.set("nhide",0);
		bean.set("rleft",681);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",24);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",27);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",950);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 1);
		bean.set("rsize", 2);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",32);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",129);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",17);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",126);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",971);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",32);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",255);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",19);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",138);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",971);
		bean.set("hz","  年   月   日`");
		bean.set("cellvalue","年   月   日");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",32);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",444);
		bean.set("align",7);
		bean.set("l",1);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",18);
		bean.set("flag","");
		bean.set("t",0);
		bean.set("rwidth",40);
		bean.set("r",0);
		bean.set("inputType",0);
		bean.set("rtop",971);
		bean.set("hz","");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 1);
		list.add(bean);
		  
		bean=new LazyDynaBean();
		bean.set("rheight",32);
		bean.set("field_type","");
		bean.set("b",1);
		bean.set("nhide",0);
		bean.set("rleft",484);
		bean.set("align",7);
		bean.set("l",0);
		bean.set("fonteffect",1);
		bean.set("fontname","宋体");
		bean.set("setname","");
		bean.set("subflag",false);
		bean.set("gridno",20);
		bean.set("flag","H");
		bean.set("t",0);
		bean.set("rwidth",224);
		bean.set("r",1);
		bean.set("inputType",0);
		bean.set("rtop",971);
		bean.set("hz","  年   月   日`");
		bean.set("cellvalue","     年   月   日");
		bean.set("fontsize",14);
		bean.set("sub_domain","<?xml version=\"1.0\" encoding=\"GB2312\"?><sub_para><para setname=\"\" hl=\"false\" vl=\"false\" blank_grid=\"true\" colhead=\"false\" multimedia=\"false\" customcolhead=\"false\" fields=\"\" isview=\"0\" func=\"0\" colheadheight=\"0\" datarowcount=\"0\" FontColor=\"0\"/></sub_para>");
		bean.set("pageid",1);
		bean.set("lsize", 1);
		bean.set("tsize", 1);
		bean.set("bsize", 2);
		bean.set("rsize", 2);
		list.add(bean);

		pgmap.set("context", list);
		String[] rtops= {"108","203","271","362","730","838","868","950","971"};
		String[] rlefts= {"79","98","129","255","266","366","393","444","484","530","581","681"};

		
		pgmap.set("wh",wordPageSetting());
		pgmap.set("paperOrientation", "0");
		pgmap.set("rtops",new ArrayList(Arrays.asList(rtops)));//行 数  内容
		pgmap.set("rlefts",new ArrayList(Arrays.asList(rlefts)));//列 数  内容
		ArrayList<Integer> top_list=new ArrayList<Integer>();
		top_list.add(30);		
		pgmap.set("rtops_t",top_list );
		bean =new LazyDynaBean();
		bean.set("rheight", 29);
		bean.set("rleft", 160);
		bean.set("fonteffect",1);
		bean.set("fontname", "宋体");
		bean.set("titlevalue", "");
		bean.set("flag", 0);
		bean.set("gridno", 1);
		bean.set("rwidth", 210);
		bean.set("rtop", 30);
		bean.set("extendattr", "<image></image><format>0</format><prefix></prefix><FontColor>0</FontColor>");
		bean.set("hz", "");
		bean.set("fontsize", 24);
		bean.set("pageid", 1);
		ArrayList<LazyDynaBean> topBean=new ArrayList<LazyDynaBean>();
		topBean.add(bean);
		pgmap.set("toptitle",topBean);
		pgmap.set("midtitle_img", new ArrayList());
		ArrayList<Integer> bottom_list=new ArrayList<Integer>();
		bottom_list.add(870);
		pgmap.set("rtops_b",bottom_list);
		//底部标题对象
		bean =new LazyDynaBean();
		bean.set("rheight", 16);
		bean.set("rleft", 113);
		bean.set("fonteffect", 1);
		bean.set("fontname", "宋体");
		bean.set("titlevalue", "填表人:");
		bean.set("flag", 3);
		bean.set("gridno", 1);
		bean.set("rwidth", 48);
		bean.set("rtop", 870);
		bean.set("extendattr", "<image></image><format>0</format><prefix>制表人：</prefix><FontColor>0</FontColor>");
		bean.set("hz", "填表人");
		bean.set("fontsize", 14);
		bean.set("pageid", 1);
		ArrayList<LazyDynaBean> bottom_bean=new ArrayList<LazyDynaBean>();
		bottom_bean.add(bean);
		pgmap.set("bomtitle",bottom_bean);
		return pgmap;
	}
	/***
	 * word
	 * @return
	 */
	private static float[] wordPageSetting() {
	    float[] wh = { 
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(210))), 
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(297))), 
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(11.1))),
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(12))), 
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(20))),
                Math.round(ConvertUtil.pointToPixel(ConvertUtil.millimeterToPoint(22))), 
                1.0f, 
                1.0f };
	    return wh;
	}
	
}
