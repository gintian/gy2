package com.hjsj.hrms.module.system.filepathsetting.businessobject.impl;

import com.hjsj.hrms.module.system.filepathsetting.businessobject.FilePathSettingService;
import com.hjsj.hrms.module.system.filepathsetting.dao.FilePathSettingDao;
import com.hjsj.hrms.module.system.filepathsetting.dao.impl.FilePathSettingDaoImpl;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.virtualfilesystem.VFSUtil;
import com.hrms.virtualfilesystem.VfsParam;
import com.hrms.virtualfilesystem.manager.VfsManagerFactory;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.dom4j.io.SAXReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangh
 */
public class FilePathSettingServiceImpl implements FilePathSettingService {
    private Connection connection;
    public FilePathSettingServiceImpl(Connection connection){
        this.connection = connection;
    }
    /**
     * 获取文件配置，
     * 若远程端选择了远程服务器或FTP服务器remoteparam仅回传必填项即可
     *
     * @return
     */
    @Override
    public String getFilePathSetting() throws SQLException, GeneralException {
        String param = "";
        FilePathSettingDao settingDao = new FilePathSettingDaoImpl(this.connection);
        RecordVo recordVo = settingDao.getFilePathSetting();
        if(recordVo!= null){
            String str_value = recordVo.getString("str_value");
            if(StringUtils.isBlank(str_value)){
                param = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<FILESETTING>" +
                        "  <FILEPATH>" +
                        "    <PARAMETER>" +
                        "      <PATH></PATH>" +
                        "      <OLDPATH></OLDPATH>" +
                        "    </PARAMETER>" +
                        "    <TYPE>0</TYPE>" +
                        "  </FILEPATH>" +
                        "  <FILESIZECONTROL>" +
                        "    <VIDEOSTREAMS>100</VIDEOSTREAMS>" +
                        "    <MULTIMEDIA>100</MULTIMEDIA>" +
                        "    <OTHER>10</OTHER>" +
                        "    <DOC>10</DOC>" +
                        "  </FILESIZECONTROL>" +
                        "</FILESETTING>";
                recordVo.setString("str_value",param);
                settingDao.saveFilePathSetting(recordVo);
                param = xmlToJson(param);
            }else{
                //最初HCM中存储的是旧的xml结构，初始化时需要使用新的xml结构，并且保留旧的存储路径(Vfs文件迁移会用到)
                if(str_value.contains("rootpath")){
                    PareXmlUtils utils = new PareXmlUtils(str_value);
                    String rootpath = utils.getAttributeValue("/filepath","rootpath");
                    String multimedia = utils.getAttributeValue("/filepath/multimedia","maxsize");
                    String document = utils.getAttributeValue("/filepath/document","maxsize");
                    String trainvideo = utils.getAttributeValue("/filepath/trainvideo","maxsize");
                    if(StringUtils.isNotBlank(multimedia)){
                        if(multimedia.contains("M")){
                            multimedia =  multimedia.replace("M","");
                        }
                    }else{
                        //没有设置时给默认值
                        multimedia = "100";
                    }
                    if(StringUtils.isNotBlank(document)){
                        if(document.contains("M")){
                            document =  document.replace("M","");
                        }
                    }else{
                        document = "10";
                    }
                    if(StringUtils.isNotBlank(trainvideo)){
                        if(trainvideo.contains("M")){
                            trainvideo =  trainvideo.replace("M","");
                        }
                    }else{
                        trainvideo = "100";
                    }
                    str_value = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                            "<FILESETTING>" +
                            "  <FILEPATH>" +
                            "    <PARAMETER>" +
                            "      <PATH>"+rootpath+"</PATH>" +
                            "      <OLDPATH>"+rootpath+"</OLDPATH>" +
                            "    </PARAMETER>" +
                            "    <TYPE>0</TYPE>" +
                            "  </FILEPATH>" +
                            "  <FILESIZECONTROL>" +
                            "    <VIDEOSTREAMS>"+trainvideo+"</VIDEOSTREAMS>" +
                            "    <MULTIMEDIA>"+multimedia+"</MULTIMEDIA>" +
                            "    <OTHER>"+10+"</OTHER>" +
                            "    <DOC>"+document+"</DOC>" +
                            "  </FILESIZECONTROL>" +
                            "</FILESETTING>";
                    recordVo.setString("str_value",str_value);
                    settingDao.saveFilePathSetting(recordVo);

                }
                //数据库中以xml形式存储，传给前台需要转成json格式
                param = xmlToJson(str_value);
            }

        }
        return param;
    }

    /**
     * 保存文件配置
     * @param pathSetting
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    @Override
    public boolean saveFilePathSetting(String pathSetting) throws Exception {
        boolean flag = false;
        FilePathSettingDaoImpl settingDao = new FilePathSettingDaoImpl(this.connection);
        RecordVo recordVo = settingDao.getFilePathSetting();
        String str_value = recordVo.getString("str_value");
        //保存文件存储位置
        try{
            flag = saveFilePathSetting(settingDao,str_value,pathSetting);
            //测试配置是否正确
            flag = testFilePathSetting();
            if(!flag){
                recordVo.setString("str_value",str_value);
                settingDao.saveFilePathSetting(recordVo);
            }
        }catch (Exception e){
            recordVo.setString("str_value",str_value);
            settingDao.saveFilePathSetting(recordVo);
            throw e;
        }
        return  flag;
    }
    private boolean saveFilePathSetting(FilePathSettingDaoImpl settingDao,String str_value,String pathSetting) throws Exception{
        boolean flag = false;
        JSONObject jsonObject = JSONObject.fromObject(pathSetting);
        RecordVo recordVo = settingDao.getFilePathSetting();
        //类型 0：本地磁盘；1：远程服务器；2：FTP；3：其他
        String fileType = "";
        //本地路径
        String path = "";
        //旧的本地路径，文件迁移时会用到
        String oldPath = "";
        String url = "";
        String saltkey = "";
        //ftp端口号
        String port = "";
        //ftp用户名
        String user = "";
        //ftp密码
        String pwd = "";
        //类路径
        String classPath = "";
        String media = "";
        String doc = "";
        String video = "";
        String other = "";
        String parameterStr = "";
        SAXReader saxReader=new SAXReader();
        org.dom4j.Document document=saxReader.read(new StringReader(str_value));
        org.dom4j.Element root=document.getRootElement();
        org.dom4j.Element fileSizeElement=root.element("FILESIZECONTROL");
        media = fileSizeElement.elementText("MULTIMEDIA");
        doc = fileSizeElement.elementText("DOC");
        video = fileSizeElement.elementText("VIDEOSTREAMS");
        other = fileSizeElement.elementText("OTHER");
        org.dom4j.Element filePathElement=root.element("FILEPATH");
        fileType = filePathElement.elementText("TYPE");
        path = filePathElement.element("PARAMETER").elementText("PATH");
        oldPath = filePathElement.element("PARAMETER").elementText("OLDPATH");
        if("1".equalsIgnoreCase(fileType)){
            url = filePathElement.element("PARAMETER").elementText("URL");
            saltkey = filePathElement.element("PARAMETER").elementText("SALTKEY");
            parameterStr = "<URL>"+url+"</URL><SALTKEY>"+saltkey+"</SALTKEY>";
        }else if("2".equalsIgnoreCase(fileType)){
            url = filePathElement.element("PARAMETER").elementText("URL");
            port = filePathElement.element("PARAMETER").elementText("PORT");
            user = filePathElement.element("PARAMETER").elementText("USER");
            pwd = filePathElement.element("PARAMETER").elementText("PWD");
            parameterStr = "<URL>"+url+"</URL><PORT>"+port+"</PORT><USER>"+user+"</USER><PWD>"+pwd+"</PWD>";

        }else if("3".equalsIgnoreCase(fileType)){
            classPath = filePathElement.element("PARAMETER").elementText("CLASS");
            parameterStr = "<CLASS>"+classPath+"</CLASS>";
        }
        JSONObject filePathObj = (JSONObject)jsonObject.get("FILEPATH");
        JSONObject parameterObj = (JSONObject)filePathObj.get("PARAMETER");
        fileType = filePathObj.getString("TYPE");
        if("0".equalsIgnoreCase(fileType)){
            path = parameterObj.getString("PATH");
        }else if("1".equalsIgnoreCase(fileType)){
            url = parameterObj.getString("URL");
            saltkey = parameterObj.getString("SALTKEY");
            parameterStr = "<URL>"+url+"</URL><SALTKEY>"+saltkey+"</SALTKEY>";
        }else if("2".equalsIgnoreCase(fileType)){
            url = parameterObj.getString("URL");
            port = parameterObj.getString("PORT");
            user = parameterObj.getString("USER");
            pwd = parameterObj.getString("PWD");
            parameterStr = "<URL>"+url+"</URL><PORT>"+port+"</PORT><USER>"+user+"</USER><PWD>"+pwd+"</PWD>";
        }else{
            classPath = parameterObj.getString("CLASS");
            parameterStr = "<CLASS>"+classPath+"</CLASS>";
        }
        JSONObject fileSizeObj = (JSONObject)jsonObject.get("FILESIZECONTROL");
        media = fileSizeObj.getString("MULTIMEDIA");
        doc = fileSizeObj.getString("DOC");
        video = fileSizeObj.getString("VIDEOSTREAMS");
        other = fileSizeObj.getString("OTHER");
        str_value = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<FILESETTING>" +
                "  <FILEPATH>" +
                "    <PARAMETER>" +
                "      <PATH>"+path+"</PATH>" +
                "      <OLDPATH>"+oldPath+"</OLDPATH>" +
                parameterStr +
                "    </PARAMETER>" +
                "    <TYPE>"+fileType+"</TYPE>" +
                "  </FILEPATH>" +
                "  <FILESIZECONTROL>" +
                "    <VIDEOSTREAMS>"+video+"</VIDEOSTREAMS>" +
                "    <MULTIMEDIA>"+media+"</MULTIMEDIA>" +
                "    <OTHER>"+other+"</OTHER>" +
                "    <DOC>"+doc+"</DOC>" +
                "  </FILESIZECONTROL>" +
                "</FILESETTING>";
        recordVo.setString("str_value",str_value);
        flag = settingDao.saveFilePathSetting(recordVo);
        return flag;
    }

    /**
     * 测试连接
     * @return
     * @throws Exception
     */
    private boolean testFilePathSetting() throws Exception{
        boolean flag = false;
        InputStream inputStream = null;
        try {
            VfsParam vfsParam = VFSUtil.getParam();
            //本地磁盘时，先判断路径是否配置正确
            if(0 == vfsParam.getType()){
                if(StringUtils.isBlank(vfsParam.getPath())){
                    throw new Exception(ResourceFactory.getProperty("FilePathSetting.blank.error"));
                }else{
                    //文件系统管理器接口
                    FileSystemManager fsManager = VFS.getManager();
                    FileObject fileObject = fsManager.resolveFile(vfsParam.getPath());
                    if(!fileObject.exists()){
                        throw new Exception(ResourceFactory.getProperty("FilePathSetting.path.error"));
                    }
                }

            }
            String username = "test ";
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.MB;
            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
            String CategoryGuidKey = "";
            inputStream = new ByteArrayInputStream("测试内容".getBytes());
            String fileName = "test.txt";
            //文件扩展标识统一限制为要么为空，要么长度不得小于6位
            String filetag = "";
            boolean isTempFile = true;
            //zhangh 2020-2-18 在测试连接之前，需要销毁掉原来的实例
            VfsManagerFactory.destroyManager();
            String fileId = VfsService.addFile(username,vfsFiletypeEnum,vfsModulesEnum,vfsCategoryEnum,CategoryGuidKey,inputStream,fileName,filetag,isTempFile);
            if(VfsService.deleteFile("test",fileId)){
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
            throw e;
        }finally{
            PubFunc.closeIoResource(inputStream);
        }
        return flag;
    }

    /**
     * xml转换成json
     * @param xml
     * @return
     */
    private  String xmlToJson(String xml) {
        JSONObject obj = new JSONObject();
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = PubFunc.generateDom(is);
            Element root = doc.getRootElement();
            Map map = iterateElement(root);
            obj.put(root.getName(),map);
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            PubFunc.closeIoResource(is);
        }
    }

    /**
     * 递归解析xml，转成json格式
     * @param root
     * @return
     */
    private  Map  iterateElement(Element root) {
        List childrenList = root.getChildren();
        Element element = null;
        Map map = new HashMap();
        List list = null;
        for (int i = 0; i < childrenList.size(); i++) {
            list = new ArrayList();
            element = (Element) childrenList.get(i);
            if(element.getChildren().size()>0){
                if(root.getChildren(element.getName()).size()>1){
                    if (map.containsKey(element.getName())) {
                        list = (List) map.get(element.getName());
                    }
                    list.add(iterateElement(element));
                    map.put(element.getName(), list);
                }else{
                    map.put(element.getName(), iterateElement(element));
                }
            }else {
                if(root.getChildren(element.getName()).size()>1){
                    if (map.containsKey(element.getName())) {
                        list = (List) map.get(element.getName());
                    }
                    list.add(element.getTextTrim());
                    map.put(element.getName(), list);
                }else{
                    map.put(element.getName(), element.getTextTrim());
                }
            }
        }

        return map;
    }

    /**
     * json转成xml
     * @param json
     * @return
     */
    private String jsonToXml(Object json)
    {
        if(json == null){
            return null;
        }else{
            Element elements=new Element("xml");
            getXMLFromObject(json,"xml",elements);
            XMLOutputter xmlOut = new XMLOutputter();
            String res=xmlOut.outputString(elements);
            return res;
        }
    }

    /**
     * 递归解析json，转成xml
     * @param obj
     * @param tag
     * @param parent
     */
    private void getXMLFromObject(Object obj,String tag,Element parent)
    {
        if(obj==null){
            return;
        }
        Element child;
        String eleStr;
        Object childValue;
        if(obj instanceof JSONObject)
        {
            JSONObject jsonObject=(JSONObject)obj;
            for(Object temp:jsonObject.keySet())
            {
                eleStr=temp.toString();
                childValue=jsonObject.get(temp);
                child=new Element(eleStr);
                if(childValue instanceof JSONArray){
                    getXMLFromObject(childValue,eleStr,parent);
                }
                else{
                    parent.addContent(child);
                    getXMLFromObject(childValue,eleStr,child);
                }
            }
        }else if(obj instanceof JSONArray){
            JSONArray jsonArray=(JSONArray)obj;
            for(int i=0;i<jsonArray.size();i++)
            {
                childValue=jsonArray.get(i);
                child=new Element(tag);
                parent.addContent(child);
                getXMLFromObject(childValue,tag,child);
            }
        }else if(obj instanceof Date){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parent.setText(sf.format((Date)obj));
        }else{
            parent.setText(obj.toString());
        }
    }


}
