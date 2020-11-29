package com.hjsj.hrms.module.system.hrcloud.util;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


public class SyncConfigUtil {
	private static Logger log =  Logger.getLogger(SyncConfigUtil.class);
	private static final int KEY_SIZE = 128;
	private static final String ALGORITHM = "AES";
	public static String IP_PORT = "http://www.hjhrcloud.com";
	//云指标数据
	private static JSONObject cloudFieldDataJson;
	
	
    static{
    	//若system.property中有配置 则走配置
    	if(!"".equals(SystemConfig.getPropertyValue("hjhrcloud_url")))
    		IP_PORT = SystemConfig.getPropertyValue("hjhrcloud_url");
    }
    
    
    /**
	 * @return the cloudFieldDataJson
	 */
	public static JSONObject getCloudFieldDataJson() {
		return cloudFieldDataJson;
	}

	/**
	 * @param cloudFieldDataJson the cloudFieldDataJson to set
	 */
	public static void setCloudFieldDataJson(JSONObject cloudFieldDataJson) {
		SyncConfigUtil.cloudFieldDataJson = cloudFieldDataJson;
	}

	public static String getHttp(String url) {
		String responseMsg = "";
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		try {
			httpClient.executeMethod(getMethod);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = getMethod.getResponseBodyAsStream();
			int len = 0;
			byte[] buf = new byte[1024];
			while((len=in.read(buf))!=-1){
				out.write(buf, 0, len);
			}
			responseMsg = out.toString("UTF-8");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//释放连接
			getMethod.releaseConnection();
		}
		return responseMsg;
	}

	/**
	 * post方式
	 * @param url
	 * @param param
	 * @return
	 */
	public static String postHttp(String url,String param) {
		String responseMsg = "";
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setContentCharset("GBK");
		JSONObject jsStr = JSONObject.fromObject(param);
		PostMethod postMethod = new PostMethod(url);
		try {
			RequestEntity se = new StringRequestEntity(jsStr.toString(), "application/json", "UTF-8");
			postMethod.setRequestEntity(se);
			httpClient.executeMethod(postMethod);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = postMethod.getResponseBodyAsStream();
			int len = 0;
			byte[] buf = new byte[1024];
			while((len=in.read(buf))!=-1){
				out.write(buf, 0, len);
			}
			responseMsg = out.toString("UTF-8");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return responseMsg;
	}
	
	public static String AESEncrypt(String data, String secret){
		String ciphertext;
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
            //生成指定算法的key防止linux随机生成
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secret.getBytes());
            keygen.init(KEY_SIZE, secureRandom);
            SecretKey skey = keygen.generateKey();
            byte[] raw = skey.getEncoded();
            SecretKey keyspec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyspec);
            byte[] content_byte = data.getBytes("UTF-8");
            byte[] aes_byte = cipher.doFinal(content_byte);
            ciphertext = Hex.encodeHexString(aes_byte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ciphertext;
    }
	public static String AESDecrypt(String data, String secret){
		String text;
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
            //生成指定算法的key防止linux随机生成
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secret.getBytes());
            keygen.init(KEY_SIZE, secureRandom);
            SecretKey skey = keygen.generateKey();
            byte[] raw = skey.getEncoded();
            SecretKey keyspec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyspec);
            byte [] content_byte = Hex.decodeHex(data.toCharArray());
            byte [] text_byte = cipher.doFinal(content_byte);
            text = new String(text_byte, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return text;         
    }
	public static String AESEncrypt(String data, String secret, String vi){
		String ciphertext;
        try {
        	IvParameterSpec zeroIv = new IvParameterSpec(vi.getBytes());
    		SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
    		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] content_byte = data.getBytes("UTF-8");
            byte[] aes_byte = cipher.doFinal(content_byte);
            ciphertext = Hex.encodeHexString(aes_byte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ciphertext;
    }
	
//	public static String digest(String data) {
//		return digest(new ArrayList(Arrays.asList(data)));
//	}
//	
//	public static String digest(List<String> data) {
//		StringBuilder sb = new StringBuilder();
//		data.remove("s");
//		Collections.sort(data);
//		for(String s : data) {
//			sb.append(s);
//		}
//		return DigestUtils.sha1Hex(sb.toString());
//	}
	
	public static String digest(JSONObject data) {
		
		return digest(new ArrayList(Arrays.asList(data)));
	}
	
	public static String digest(List<String> data) {
		StringBuilder sb = new StringBuilder();
		for(String data1: data) {
			if (data1==null) {
				data.remove(data1);
			}
		}
		Collections.sort(data);
		for(String s : data) {
			sb.append(s);
		}
		return DigestUtils.sha1Hex(sb.toString());
	}
	
	/**
	 * 校验签名
	 * @param reqJson
	 * @return
	 */
	public static JSONObject validSign(JSONObject reqJson){
		JSONObject returnJson = new JSONObject();
		boolean flag = false;
		if(!reqJson.containsKey("data") || !reqJson.containsKey("sign") || !reqJson.containsKey("success")){
			returnJson.put("flag", false);
			returnJson.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget") + ResourceFactory.getProperty("hrcloud.recieve.error.reqparams"));
			return returnJson;
		}
		String sign = reqJson.getString("sign");
		String data = reqJson.getString("data");
		String success = reqJson.getString("success");
		if(StringUtils.isEmpty(sign) || StringUtils.isEmpty(data) ){
			returnJson.put("flag", false);
			returnJson.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.noget") + ResourceFactory.getProperty("hrcloud.recieve.error.reqparams"));
			return returnJson;
		}
		ArrayList datalist = new ArrayList();
		//xus 19/12/21 云接口去掉了appid认证
//		datalist.add(CloudConstantParams.getParamJson().get("appId"));
		datalist.add(data);
		datalist.add(success);
		if(reqJson.containsKey("code") && StringUtils.isNotBlank(reqJson.getString("code"))){
			datalist.add(reqJson.getString("code"));
		}
		if(reqJson.containsKey("msg") && StringUtils.isNotBlank(reqJson.getString("msg"))){
			datalist.add(reqJson.getString("msg"));
		}
		String newsign= digest(datalist);
		if(newsign.equals(sign)){
			returnJson.put("flag", true);
			return returnJson;
		}else{
			returnJson.put("flag", false);
			returnJson.put("msg", ResourceFactory.getProperty("hrcloud.recieve.error.signfail"));
			return returnJson;
		}
	}
	
	/**
	 * 通过A0100获取guidkey
	 * @param nbase
	 * @param A0100
	 * @return
	 */
	public static String getUniqueidByA0100 (String nbase,String A0100){
		String uniqid = "";
		String sql = "select GUIDKEY  from "+nbase+"a01 where A0100 = ? ";
		ArrayList values = new ArrayList();
		values.add(A0100);
		Connection connection = null;
		ResultSet resultset = null;
		try {
			connection = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(connection);
			resultset = dao.search(sql,values);
			while(resultset.next()){
				uniqid = resultset.getString("GUIDKEY");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(connection);
			PubFunc.closeDbObj(resultset);
		}
		return uniqid;
	}
	
	/**
	 * 获取人员/机构主键及子集序号
	 * @param conn
	 * @param nbase
	 * @param objectType
	 * @param guidkey
	 * @return {id:XX,index:i}//index为当前子集最大的index
	 */
	public static HashMap getSetInfo(Connection conn,String nbase,String table, int objectType, String guidkey) {
		HashMap returnMap = new HashMap();
		String sql = "";
		ArrayList values = new ArrayList();  
		if(objectType == 0){
			sql = "select codeitemid id from organization where GUIDKEY = ? ";
		}else if(objectType == 1){
			sql = "select A0100 id,nbase_0 dbname from t_hr_view where unique_id = ? ";
		}else{
			returnMap.put("id","");
			return returnMap;
		}
		values.add(guidkey);
		
		String id = "";
		String dbname = "";
		int index = 0;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql,values);
			if(rs.next()){
				id = rs.getString("id");
				if(objectType == 1){
					dbname = rs.getString("dbname");
				}
			}
			returnMap.put("id",id);
			if("".equals(id)){
				return returnMap;
			}
			
			if(objectType == 0){
				sql = "select "+Sql_switcher.isnull("MAX(i9999)", "0")+" num from "+table+" where B0110 = ?";
			}else if(objectType == 1){
				sql = "select "+Sql_switcher.isnull("MAX(i9999)", "0")+" num from "+dbname+table+" where A0100 = ?";
			}
			values = new ArrayList(); 
			values.add(id);
			rs = dao.search(sql,values);
			if(rs.next()){
				index = rs.getInt("num");
			}
			returnMap.put("dbname",dbname);
			returnMap.put("index",index);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
				
		return returnMap;
	}
	
	/**
	 * 获取考核信息
	 * @return
	 */
	public static HashMap getHrAssessConnectConfig(){
		HashMap returnMap = new HashMap();
		try{
			RecordVo recordVo = ConstantParamter.getConstantVo("HRCLOUD_CONFIG");
			JSONObject cloudTOhr = new JSONObject();
			JSONArray setMapping = new JSONArray();
			JSONObject orgAssessSet = new JSONObject();
			JSONObject empAssessSet = new JSONObject();
			if (recordVo != null) {
				String str = recordVo.getString("str_value");
				if(!"".equals(str)){
					JSONObject json = JSONObject.fromObject(str);
					if(json.get("cloudTOhr") != null && json.getJSONObject("cloudTOhr").containsKey("setMapping")){
						cloudTOhr = json.getJSONObject("cloudTOhr") ;
						setMapping = cloudTOhr.getJSONArray("setMapping") ;
						for(Object o : setMapping){
							JSONObject js = (JSONObject)o;
							if("org_result".equals(js.getString("cloudset_id"))){
								JSONArray cloud_fields = js.getJSONArray("cloud_fields");
								String hr_set = js.getString("hr_set");
								orgAssessSet.put("setid", hr_set);
								for(Object obj : cloud_fields){
									JSONObject cloud_field = (JSONObject) obj;
									orgAssessSet.put(cloud_field.getString("id"),cloud_field.getJSONObject("connectField").getString("itemid"));
								}
							}
							if("emp_result".equals(js.getString("cloudset_id"))){
								empAssessSet = js;
								JSONArray cloud_fields = js.getJSONArray("cloud_fields");
								String hr_set = js.getString("hr_set");
								empAssessSet.put("setid", hr_set);
								for(Object obj : cloud_fields){
									JSONObject cloud_field = (JSONObject) obj;
									empAssessSet.put(cloud_field.getString("id"),cloud_field.getJSONObject("connectField").getString("itemid"));
								}
							}
						}
					}
				}
			}
			if(orgAssessSet.size()==0){
				orgAssessSet.put("setid", "");
				orgAssessSet.put("businessId", "");
				orgAssessSet.put("name", "");
				orgAssessSet.put("type", "");
				orgAssessSet.put("planDate", "");
				orgAssessSet.put("planYear", "");
				orgAssessSet.put("sheetName", "");
				orgAssessSet.put("score", "");
				orgAssessSet.put("degreeName", "");
			}
			if(empAssessSet.size()==0){
				empAssessSet.put("setid", "");
				empAssessSet.put("businessId", "");
				empAssessSet.put("name", "");
				empAssessSet.put("type", "");
				empAssessSet.put("planDate", "");
				empAssessSet.put("planYear", "");
				empAssessSet.put("sheetName", "");
				empAssessSet.put("score", "");
				empAssessSet.put("degreeName", "");
			}
			returnMap.put("orgAssessSet", orgAssessSet);
			returnMap.put("empAssessSet", empAssessSet);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	public static String getCloudLogonUrl(String uniqueid,String token,int type){
		String returnUrl = "";
		String appId = "";
		String appSecret = "";
		
		JSONObject json = CloudConstantParams.getDataBaseCloudParam();
		appId = json.getString("appId");
		appSecret = json.getString("appSecret");
		
		JSONObject data = new JSONObject();
		data.put("staffNum", uniqueid);
		data.put("token", token);
		data.put("type", type);
		String dataEncode = SyncConfigUtil.AESEncrypt(data.toString(), appSecret);
		ArrayList datalist = new ArrayList();
		datalist.add(appId);
		datalist.add(dataEncode);
		String signEncode = SyncConfigUtil.digest(datalist);
		returnUrl = SyncConfigUtil.IP_PORT+"/access/open?appId="+appId+"&data="+dataEncode+"&sign="+signEncode;
		return returnUrl;
	}

	/**
	 * 校验token是否有效
	 * @param token
	 * @return
	 */
	public static boolean tockenCheck(String token) {
		if(token.indexOf("userid")<0 || token.indexOf("time")<0) {
			return false;
		}
		JSONObject json = JSONObject.fromObject(token);
		String userid = json.getString("userid");
		String time = json.getString("time");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String currenttime = dateFormat.format( new Date() );
		if(!currenttime.equalsIgnoreCase(time)) {
			return false;
		}
		if(!SyncConfigUtil.guidkeyExist(userid)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断云配置的人员库中是否存在此guidkey
	 * @param guidkey
	 * @return
	 */
	private static boolean guidkeyExist(String guidkey) {
		boolean flag = false;
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			HrSyncBo hsb = new HrSyncBo(conn);
			String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
			String sql = "";
			ArrayList values = new ArrayList();
			String[] arrs = dbnamestr.split(",");
			DbWizard dbw = new DbWizard(conn);
			for(String str : arrs) {
				if(StringUtils.isNotBlank(str) && str.length() == 3) {
					if(dbw.isExistField(str+"A01", "GUIDKEY",false)) {
						if(!sql.isEmpty()) {
							sql += " union ";
						}
						sql += " select GUIDKEY from "+str+"A01 where GUIDKEY = ? ";
						values.add(guidkey);
					}
				}
			}
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql,values);
			if(rs.next()) {
				flag = true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(conn);
			PubFunc.closeDbObj(rs);
		}
		return flag;
	}
}