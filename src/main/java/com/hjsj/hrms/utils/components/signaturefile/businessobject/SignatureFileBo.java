package com.hjsj.hrms.utils.components.signaturefile.businessobject;
/**
 * 签章图片公共类
 * @author Administrator
 *
 */

import com.hjsj.hrms.interfaces.decryptor.Des;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import sun.misc.BASE64Decoder;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignatureFileBo {

	private Connection conn = null;
	private UserView userView = null;

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public SignatureFileBo(Connection conn, UserView userview) {
		this.conn = conn;
		this.userView = userview;
	}

	public SignatureFileBo() {

	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/***
	 * 解析获取加密后的图片
	 * 
	 * @param ext_param
	 * @param showIdFlag 展现签章列表标记
	 * @return
	 */
	public List<DynaBean> getSignatureImg(String markID,String ext_param,boolean showIdFlag) throws Exception {
		Document doc = null;
		List<DynaBean> list = new ArrayList<DynaBean>();
		try {
			doc = DocumentHelper.parseText(ext_param);
			Element rootEl = doc.getRootElement();
			List<Element> elList = rootEl.elements();
			for (Element el : elList) {
				DynaBean bean = new LazyDynaBean();
				String markID_= el.attributeValue("MarkID");
				if(StringUtils.isNotEmpty(markID)) {
					if(!markID.equals(markID_)) {
						continue;
					}
				}
				bean.set("MarkID",markID_);
				bean.set("imgname", el.attributeValue("MarkName"));
				if(!showIdFlag) {
					String markdata = el.attributeValue("MarkData");
					if (StringUtils.isNotEmpty(markdata)) {
						if (markdata.indexOf("aA/bZDq") > -1) {
							markdata = markdata.substring(markdata.indexOf("aA/bZDq") + 7);
						}
						bean.set("photo", markdata);
					}
				}
				list.add(bean);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}

	/***
	 * 校验密码
	 * 
	 * @param type
	 *            true 不校验密码直接获取图片
	 * @param isGetMarkID 校验密码获取markID
	 * @return
	 */
	public List<DynaBean> checkPassword(boolean type,boolean isGetMarkID, String password, String currentUser,String markId) throws Exception {
		List<DynaBean> list_bean = null;
		String ext_param = "";
		boolean flag = false;
		if (StringUtils.isEmpty(currentUser)) {
			throw new Exception("用户名不正确，请检查设置！");
		}
		ArrayList list_value = new ArrayList();
		list_value.add(PubFunc.decrypt(currentUser));
		List list = ExecuteSQL.executePreMyQuery("select signatureID,password,ext_param from signature where username=?",
				list_value, this.conn);
		String signatureID="";
		if (list != null && list.size() > 0) {
			LazyDynaBean bean = (LazyDynaBean) list.get(0);
			signatureID=(String)bean.get("signatureid");
			if (type) {
				flag = true;
				ext_param = (String) bean.get("ext_param");
			} else {
				if (password.equals((String) bean.get("password"))) {
					flag = true;
					ext_param = (String) bean.get("ext_param");
				}
			}
		} else {
			return list_bean;
		}
		if (flag) {
			if(isGetMarkID) {
				list_bean=new ArrayList<DynaBean>();
				DynaBean bean=new LazyDynaBean();
				bean.set("signatureID", signatureID);
				bean.set("username", currentUser);
				bean.set("MarkID", markId);
				list_bean.add(bean);
			}else {
				list_bean = this.getSignatureImg(markId,ext_param,false);
				list_bean.get(0).set("signatureID", signatureID);
			}
		} else {
			throw new Exception("密码不正确！");
		}

		return list_bean;
	}

	/**
	 * 校验用户与锁是否绑定
	 */
	public HashMap checkBaning(String baningID, String currentUser) throws GeneralException {
		HashMap return_map = new HashMap();
		currentUser = PubFunc.decrypt(currentUser);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rset = null;
		boolean bandingflag = false;
		boolean checkPassWord_flag = false;
		try {
			if (StringUtils.isNotEmpty(currentUser)) {
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] baningIDbytes = decoder.decodeBuffer(baningID);
				baningID = new String(baningIDbytes);
				ArrayList value_list = new ArrayList();
				value_list.add(currentUser);
				String sql = "select hardwareid,password,ext_param from signature where username=?";
				rset = dao.search(sql, value_list);
				if (rset.next()) {
					String hardwareid = rset.getString("hardwareid");
					String banding_ = PubFunc.encrypt(currentUser) + hardwareid;
					String ext_param=rset.getString("ext_param");//获取设置的签章列表
					if (baningID.contains(banding_)) {
						bandingflag = true;
					}
					List<DynaBean> dataList=this.getSignatureImg("",ext_param, true);
					return_map.put("data", dataList);
					String password = rset.getString("password");
					if (StringUtils.isNotEmpty(password)) {
						checkPassWord_flag = true;
					}
				}
			} else {
				throw new Exception("用户名不可为空");
			}
			return_map.put("checkPassWord", checkPassWord_flag);
			return_map.put("flag", bandingflag);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rset);
		}
		return return_map;
	}

	/**
	 * 对比签章PID是否相同
	 * 
	 * @return
	 */
	public boolean comparePID(String data, String data_en) {
		boolean flag = false;
		BASE64Decoder decoder = new BASE64Decoder();
		Des des = new Des();
		try {
			// 解密
			// 1、首先将字符串base64解开
			byte[] databytes = decoder.decodeBuffer(data_en);
			// 调用des的解密方法
			String data_de = des.DecryStr(databytes, "hjsj");
			if (data_de.equals(data)) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/***
	 * 判断是否启用签章以及启用U盾
	 */
	public HashMap checkSignature(String currentUser) {
		HashMap map = new HashMap();
		// 获取电子签章标识 签章
		TemplateModuleParam templateparam = new TemplateModuleParam(this.conn, this.userView);
		int singature_type = templateparam.getSignatureType();
		if (singature_type == 2) {
			// 是否启用签章
			boolean signature_usb = templateparam.getSignature_usb();
			if (StringUtils.isEmpty(currentUser)) {
				currentUser = this.userView.getUserName();
				if (this.userView.getStatus() == 4) {
					currentUser = this.userView.getDbname() + this.userView.getA0100();
				}
				currentUser = PubFunc.encrypt(currentUser);
			}
			map.put("signature_usb", signature_usb);
			map.put("currentUser", currentUser);
			map.put("type", true);
		} else {
			map.put("type", false);
		}
		return map;
	}
	/**
	 * 导出印章图片公共方法
	 * @param markid 
	 * @param signatureId
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String getMarkData(String markid,String signatureId,String username)throws Exception {
		String markData="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			String ext_param="";
			if(StringUtils.isNotEmpty(signatureId)&&StringUtils.isNotEmpty(username)) {
				rs=dao.search("select ext_param from signature where signatureid=? and userName=?",Arrays.asList(signatureId,username));
			}else if(StringUtils.isNotEmpty(signatureId)) {
				rs=dao.search("select ext_param from signature where signatureid=?",Arrays.asList(signatureId));
			}else if(StringUtils.isNotEmpty(username)) {
				rs=dao.search("select ext_param from signature where signatureid=?",Arrays.asList(signatureId));
			}
			while(rs.next()) {
				ext_param=rs.getString("ext_param");
			}
			List<DynaBean> list=this.getSignatureImg(markid, ext_param, false);
			if(list!=null&&list.size()>0) {
				markData=(String)list.get(0).get("photo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return markData;
	}
}
