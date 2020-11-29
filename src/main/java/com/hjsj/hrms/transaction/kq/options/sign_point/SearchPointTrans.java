package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class SearchPointTrans extends IBusiness{

	/* 百度开发秘钥，此密钥用于地图搜索功能，每天限搜索次数十万次，超出则查询结果为空 */
	public final static String BAIDUKEY = "09I2w8No8nFhnPSUomlc2oox";
	
	public void execute() throws GeneralException {
		
		String searchWord = (String)this.getFormHM().get("searchWord");
		
		
		if(searchWord == null || searchWord.length()<1)
			return;
		
		StringBuffer baiduMapUrl = new StringBuffer();
		baiduMapUrl.append("http://api.map.baidu.com/place/v2/search?");//百度 Place API 入口
		baiduMapUrl.append("&q="+URLEncoder.encode(searchWord));  //关键字
		baiduMapUrl.append("&region="+URLEncoder.encode("全国"));  //搜索范围
		baiduMapUrl.append("&output=xml"); // 返回格式
		baiduMapUrl.append("&ak="+BAIDUKEY);// 百度秘钥
		
		StringBuffer res = new StringBuffer();  
        URL u;
        InputStream is = null;
		try {
			u = new URL(baiduMapUrl.toString());
			
			URLConnection uc = u.openConnection();  
	        uc.setDoOutput(true); 
	        is = uc.getInputStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));  
	        try {
    	        String line="";  
    	        while ((line = in.readLine()) != null)  
    	        	res.append(line + "\n");  
	        } finally {
	            PubFunc.closeIoResource(in);
	        }
			
	        Document doc = PubFunc.generateDom(res.toString());
	        Element messageEl = doc.getRootElement().getChild("message");
	        Element resultEl = doc.getRootElement().getChild("results"); 
	        String mess = "ok";
	        if(!"ok".equals(messageEl.getValue()) || resultEl.getChildren().size()<1){
	        	mess = "抱歉，未找到相关地点。";
	        }
	        
	        List pointList = resultEl.getChildren();
	        res.setLength(0);
	        res.append("[");
	        for(int i=0;i<pointList.size();i++){
	        	Element point = (Element)pointList.get(i);
	        	if(point.getChild("location")==null)break;
	        	String lat = point.getChild("location").getChild("lat").getValue();
	        	String lng = point.getChild("location").getChild("lng").getValue();
	        	res.append("{ \"name\":\""+point.getChild("name").getValue()+"\",\"lat\":\""+lat+"\",\"lng\":\""+lng+"\"},");
	        }
	        res.deleteCharAt(res.length()-1);
	        res.append("]");
	        
	        if(res.length()<10){
	        	mess = "抱歉，未找到相关地点。";
	        }
	        this.getFormHM().put("mess", mess);	
	        this.getFormHM().put("pointList", res.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}            
	}
}
