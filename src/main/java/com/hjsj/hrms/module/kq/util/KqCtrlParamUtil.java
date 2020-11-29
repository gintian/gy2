package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class KqCtrlParamUtil {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	private Document doc;

	private String ctrl_param_xml;

	public String getCtrl_param_xml() {
		return ctrl_param_xml;
	}

	public void setCtrl_param_xml(String ctrl_param_xml) throws GeneralException {
		try {
			StringBuffer strxml=new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
			strxml.append("<param>");
			strxml.append("</param>");
			if(StringUtils.isBlank(ctrl_param_xml))
				ctrl_param_xml=strxml.toString();
			doc = PubFunc.generateDom(ctrl_param_xml);
			this.ctrl_param_xml = ctrl_param_xml;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 
	 * @param conn
	 * @param userview
	 * @param type 1:kq_scheme根据不同表查出来对应的参数值
	 * @param list ，对应的参数
	 */
	public KqCtrlParamUtil(Connection conn, UserView userview, String scheme_id) {
		this.conn = conn;
		this.userview = userview;
		initKqCtrlParamSchemeId(scheme_id);
	}
	/**
	 *
	 * @param conn
	 * @param userview
	 * @param type 1:kq_scheme根据不同表查出来对应的参数值
	 * @param list ，对应的参数
	 */
	public KqCtrlParamUtil(Connection conn, UserView userview, String scheme_id,String xml) throws GeneralException {
		this.conn = conn;
		this.userview = userview;
		this.setCtrl_param_xml(xml);
	}

	public final static int FILLING_AGENCYS=1;//数据上报机构
	
	public final static int FILLING_AGENCY=2;//机构详细信息
	
	/**
	 * 取得对应的参数在XML节点的名称
	 * @param param_type
	 * @return
	 */
	public String getElementName(int param_type) {
		String name="";
		switch(param_type) {
			case FILLING_AGENCYS:
				name="filling_agencys";
				break;
			case FILLING_AGENCY:
				name="filling_agency";
		}
		return name;
	}
	
	/**
	 * kq_scheme
	 * @param scheme_id
	 */
	public void initKqCtrlParamSchemeId(String scheme_id)
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<param>");
		strxml.append("</param>");	
		RowSet rset=null;
		try{	
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search("select ctrl_param from kq_scheme where scheme_id=?", Arrays.asList(new String[] {scheme_id}));
			String xml="";
			if(rset.next()){
				xml=rset.getString("ctrl_param");
			}
			if(StringUtils.isBlank(xml))
				xml=strxml.toString();
			doc = PubFunc.generateDom(xml);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}		
	}
	
	/**
	 * 有子节点的时候塞值
	 * @param param_type子节点
	 * @param parent_param_name父节点
	 * @param property属性
	 * @param value值
	 */
	public void setValue(int param_type,int parent_param_name,String property,String value,int flag) {
		String name=getElementName(param_type);
		String parentName = getElementName(parent_param_name);
		Element element = null;
		if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(parentName))
		{
			try
			{
				//如果没有父节点，创建父节点
				String str_path_parent="/param/"+parentName;
				XPath xpath_parent=XPath.newInstance(str_path_parent);
				List childlist_parent=xpath_parent.selectNodes(doc);
				if(childlist_parent.size()==0) {
					element=new Element(parentName);
					doc.getRootElement().addContent(element);
				}
				
				String str_path="/param/"+parentName+"/"+name;
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				if(flag == -1) {
					element=new Element(name);
					element.setAttribute(property,value);
					doc.getRootElement().getChild(parentName).addContent(element);
				}else {
					element=(Element)childlist.get(flag);
					element.setAttribute(property,value);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 移除所有节点属性集合
	 * @param param_type
	 * @param property
	 * @return
	 */
	public void removeNode(int param_name)
	{
		String parentName = getElementName(param_name);
		if(StringUtils.isNotBlank(parentName))
		{
		  try
		  {
			String str_path="/param";
			XPath xpath=XPath.newInstance(str_path);
			Element parentElement=(Element)xpath.selectSingleNode(doc);
			parentElement.removeChildren(parentName);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
	}

	public Document getDoc() {
		return doc;
	}
	
	/**
	 * 获取对应子节点的属性集合
	 * @param param_type
	 * @param property
	 * @return
	 */
	public List getValue(int param_type,int parent_param_name)
	{
		String name=getElementName(param_type);
		String parentName = getElementName(parent_param_name);
		List childlist = new ArrayList();
		if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(parentName))
		{
		  try
		  {
			String str_path="/param/"+parentName+"/"+name;
			XPath xpath=XPath.newInstance(str_path);
			childlist=xpath.selectNodes(doc);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		}		
		return childlist;		
	}

	/**
	 * 根据考勤年月 获取有效的机构
	 * @param kq_year
	 * @param kq_duration
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 16:19 2018/12/28
	 */
	public ArrayList<HashMap> getFillingAgencysMap(String kq_year,String kq_duration) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<HashMap> listFillingAgencys = new ArrayList<HashMap>();
		KqDataUtil kqDataUtil=new KqDataUtil(this.userview);
		RowSet rs = null;
		try {
			
			List elementList = this.getValue(KqCtrlParamUtil.FILLING_AGENCY, KqCtrlParamUtil.FILLING_AGENCYS);
			for (int i = 0; i < elementList.size(); i++) {
				Element element = (Element) elementList.get(i);
				//【45173】数据审批页面新增考勤机构时，暂时去掉考勤区间在方案的创建之前不允许新增考勤机构的校验
//				if(StringUtils.isNotBlank(kq_year)&&StringUtils.isNotBlank(kq_duration)&&
//						StringUtils.isNotBlank(element.getAttributeValue("create_time"))){
//					String[] datelist=element.getAttributeValue("create_time").split("-");
//					if(datelist.length>=3){
//						int  year=Integer.parseInt(datelist[0]);
//						int  month=Integer.parseInt(datelist[1]);
//						if(Integer.parseInt(kq_year)>year||
//								(Integer.parseInt(kq_year)==year&&Integer.parseInt(kq_duration)>=month)){
//
//						}else{
//							continue;
//						}
//					}
//				}
				String org_id = element.getAttributeValue("org_id").substring(2);
				//机构历史时点显示控制
				if(StringUtils.isNotBlank(kq_year)&&StringUtils.isNotBlank(kq_duration)) {
					LazyDynaBean bean=kqDataUtil.getDatesByKqDuration(this.conn, kq_year, kq_duration);
					StringBuffer sql=new StringBuffer();
					sql.append("select * from organization where codeitemid=? ");
					sql.append("and( (start_date <=? and end_date >=?) ");
					sql.append("or (start_date >=? and start_date <=?)");
					sql.append("or (start_date >=? and end_date <=?))");
					ArrayList pList = new ArrayList();
					pList.add(org_id);
					pList.add(bean.get("kq_start"));
					pList.add(bean.get("kq_start"));
					pList.add(bean.get("kq_start"));
					pList.add(bean.get("kq_end"));
					pList.add(bean.get("kq_start"));
					pList.add(bean.get("kq_end"));
					rs=dao.search(sql.toString(),pList);
					if (!rs.next()) {
						continue;
					}
				}
				
				String reviewId = element.getAttributeValue("reviewer_id");//审核人id
				HashMap map = new HashMap();
				map.put("name", AdminCode.getCodeName("UN", org_id).length() == 0 ? AdminCode.getCodeName("UM", org_id) : AdminCode.getCodeName("UN", org_id));
				map.put("clerk_username", element.getAttributeValue("clerk_username") + " (" + element.getAttributeValue("clerk_fullname") + ")");
				map.put("y_clerk_username", element.getAttributeValue("clerk_username"));
				map.put("clerk_fullname", element.getAttributeValue("clerk_fullname"));
				map.put("reviewer", element.getAttributeValue("reviewer_fullname"));
				map.put("reviewer_imgPath", getReviewImgPath(reviewId));
				map.put("reviewer_id", reviewId);
				map.put("org_id", org_id);
				map.put("y_org_id", element.getAttributeValue("org_id"));
				map.put("leaf", true);
				map.put("create_time", KqDataUtil.nullif(element.getAttributeValue("create_time")));
				listFillingAgencys.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return  listFillingAgencys;
	}

	/**
	 * 获取图片路径
	 * @param reviewId
	 * @return
	 */
	public String getReviewImgPath(String reviewId) {
		String imgpath = "";
		try{
			if(StringUtils.isNotBlank(reviewId)) {
				String imgDbName =reviewId.substring(0, 3);//人员库前缀
				String imgA0100 = reviewId.substring(3, reviewId.length());
				PhotoImgBo photoImgBo = new PhotoImgBo(this.conn);
				imgpath = photoImgBo.getPhotoPathLowQuality(imgDbName, imgA0100);
			}else {
				imgpath = "/images/photo.jpg";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return imgpath;
	}
}
