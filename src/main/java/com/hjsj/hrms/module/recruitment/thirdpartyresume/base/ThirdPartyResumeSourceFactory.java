package com.hjsj.hrms.module.recruitment.thirdpartyresume.base;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 第三方简历来源抽象工厂类
 * <p>Title: ThirdPartyResumeSourceFactory </p>
 * <p>Description: 第三方简历来源抽象工厂类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-5-30 下午02:32:47</p>
 * @author zhaoxj
 * @version 1.0
 */
public class ThirdPartyResumeSourceFactory {
    private static ArrayList<HashMap<String, String>> ThirdPartyInfo = new ArrayList<HashMap<String,String>>();
    
    public ThirdPartyResumeSourceFactory() {
    }
    
    /*
     * 根据厂商简称获取该厂商实现类对象
     */
    public static ThirdPartyResumeBase getThirdPartyResumeBo(Connection conn, String sourceName, UserView userView) {
        ThirdPartyResumeBase base = null;
        try {
            String path = "com.hjsj.hrms.module.recruitment.thirdpartyresume.";
            path += sourceName.toLowerCase() + "." + sourceName +"ResumeBo";
            base = (ThirdPartyResumeBase) Class.forName(path).newInstance();
            base.setConn(conn);
            base.setUserView(userView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return base;
    }
    
    /*
     * 取到已启用的第三方厂商来源列表
     */
    public  ArrayList<HashMap<String, String>> getThirdPartyResumeSources() {
        if(ThirdPartyInfo != null && ThirdPartyInfo.size() > 0)
            return ThirdPartyInfo;
        //读取thirdparty_resume_source.xml,解析其中的厂商信息
        InputStream in = null;
        try{
            in=this.getClass().getResourceAsStream("/com/hjsj/hrms/module/recruitment/thirdpartyresume/config/thirdparty_resume_source.xml");
            Document doc = PubFunc.generateDom(in);
            XPath xPath = XPath.newInstance("/sources");
            Element sources = (Element) xPath.selectSingleNode(doc);
            List list=(List) sources.getChildren();
            for(int i=0;i<list.size();i++){
                HashMap<String, String> map = new HashMap<String, String>();
                Element element=(Element)list.get(i);
                //第三方的名称缩写
                String name = element.getChild("name").getText();
                map.put("name", name);
                //第三方的全称
                String fullname = element.getChild("fullname").getText();
                map.put("fullname", fullname);
                //是否启用第三方的简历接口
                String valid = element.getChild("valid").getText();
                map.put("valid", valid);
                ThirdPartyInfo.add(map);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(in);
        }
        
        return ThirdPartyInfo;
    }
}
