package com.hjsj.hrms.service.syncdata.gzyh;

import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.service.syncdata.FieldRefBean;
import com.hjsj.hrms.service.syncdata.SyncDataInter;
import com.hjsj.hrms.service.syncdata.SyncDataUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import kafka.admin.AdminUtils;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 发布基础数据到Kafka
 *
 * @author pancs
 */
public class SyncDataToKafka extends SyncDataInter {

    private static Logger log = LoggerFactory.getLogger(SyncDataToKafka.class);
    // Kafka服务器地址，多个以逗号隔开
    private String bootstrapServers = "";
    private String zookeeperUrl = "";

    private String hrs_increment = "hrs_increment";//增量

    private String hrs_complete = "hrs_complete";//全量

    private SyncDataUtil syncDataUtil = new SyncDataUtil();

    private boolean isSendSuccess = false;
    private String errorMsg = "";

    @Override
    public void init() {
        // 初始化操作
        bootstrapServers = SystemConfig.getPropertyValue("bootStrapServers");
//        zookeeperUrl = SystemConfig.getPropertyValue("zookeeperUrl");
        log.debug("bootstrapServers:" + bootstrapServers);
        //通过api创建指定topic
//        createTopic(zookeeperUrl, hrs_increment);
//        createTopic(zookeeperUrl, hrs_complete);
    }

    @Override
    public String syncEmpDataAddOrUpdate(ArrayList<LazyDynaBean> datalist) {
        if (datalist != null && datalist.size() > 0) {
            Boolean isAdd = false;
            Boolean isUpd = false;
            List<String> unqIdsAdd = new ArrayList<>();
            List<String> unqIdsUpd = new ArrayList<>();
            try {
                ArrayList<FieldRefBean> empFieldRefList = syncDataParam.getEmpFieldRefList();
                JSONArray strJsonAdd = new JSONArray();
                JSONArray strJsonUpd = new JSONArray();
                if (syncDataParam.getIsComplete()) {
                    for (int i = 0; i < datalist.size(); i++) {
                        LazyDynaBean bean = datalist.get(i);
                        isAdd = true;
                        strJsonAdd.add(dealSingleMessage(bean, empFieldRefList));
                    }
                } else {
                    for (int i = 0; i < datalist.size(); i++) {
                        LazyDynaBean bean = datalist.get(i);
                        String status = (String) bean.get("status");
                        if ("1".equals(status)) { // 新增
                            isAdd = true;
                            strJsonAdd.add(dealSingleMessage(bean, empFieldRefList));
                            unqIdsAdd.add((String) bean.get("unique_id"));
                        } else if ("2".equals(status)) { // 更新
                            isUpd = true;
                            strJsonUpd.add(dealSingleMessage(bean, empFieldRefList));
                            unqIdsUpd.add((String) bean.get("unique_id"));
                        }
                    }
                }
                if (isAdd) {
                    if (syncDataParam.getIsComplete()) {
                        sendKafkaMsg(strJsonAdd.toString(), "employees_full");
                    } else {
                        sendKafkaMsg(strJsonAdd.toString(), "employee_insert");
                    }
                    if (!syncDataParam.getIsComplete()) {
                        if (isSendSuccess) {
                            callSucessSync(unqIdsAdd, "emp", conn);
                        } else {
                            return errorMsg;
                        }
                    } else {
                        if (!isSendSuccess) {
                            return errorMsg;
                        }
                    }
                }
                if (isUpd) {
                    sendKafkaMsg(strJsonUpd.toString(), "employee_update");
                    if (isSendSuccess) {
                        callSucessSync(unqIdsUpd, "emp", conn);
                    } else {
                        return errorMsg;
                    }
                }
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("syncEmpDataAddOrUpdate:数据同步出错!,ErrorMessage:{}", e);
            }
        }
        return errorMsg;
    }

    @Override
    public String syncEmpDataDelete(ArrayList<LazyDynaBean> datalist) {
        if (datalist != null && datalist.size() > 0) {
            Boolean isDel = false;
            List<String> unqIds = new ArrayList<>();
            try {
                JSONArray jsonDel = new JSONArray();
                JSONObject json = null;
                for (int i = 0; i < datalist.size(); i++) {
                    LazyDynaBean bean = datalist.get(i);
                    String status = (String) bean.get("status");
                    if ("3".equals(status)) {
                        isDel = true;
                        json = new JSONObject();
                        json.put(syncDataParam.getDestEmpUniqueFld(), (String) bean.get(syncDataParam.getDestEmpUniqueFld()));
                        jsonDel.add(json);
                    }
                    unqIds.add((String) bean.get("unique_id"));
                }
                if (isDel) {
                    sendKafkaMsg(jsonDel.toString(), "employee_delete");
                    if (isSendSuccess) {
                        callSucessSync(unqIds, "emp", conn);
                    } else {
                        return errorMsg;
                    }
                }
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("syncEmpDataDelete:数据同步出错!,ErrorMessage:{}", e);
            }
        }
        return errorMsg;
    }

    @Override
    public String syncOrgDataAddOrUpdate(ArrayList<LazyDynaBean> datalist) {
        if (datalist != null && datalist.size() > 0) {
            Boolean isAdd = false;
            Boolean isUpd = false;
            List<String> unqIdsAdd = new ArrayList<>();
            List<String> unqIdsUpd = new ArrayList<>();
            try {
                ArrayList<FieldRefBean> orgFieldRefList = syncDataParam.getOrgFieldRefList();
                JSONArray strJsonAdd = new JSONArray();
                JSONArray strJsonUpd = new JSONArray();
                if (syncDataParam.getIsComplete()) {
                    for (int i = 0; i < datalist.size(); i++) {
                        LazyDynaBean bean = datalist.get(i);
                        isAdd = true;
                        JSONObject dealSingleMessage = dealSingleMessage(bean, orgFieldRefList);
                        strJsonAdd.add(dealSingleMessage);
                    }
                } else {
                    for (int i = 0; i < datalist.size(); i++) {
                        LazyDynaBean bean = datalist.get(i);
                        String status = (String) bean.get("status");
                        if ("1".equals(status)) { // 新增
                            isAdd = true;
                            JSONObject dealSingleMessage = dealSingleMessage(bean, orgFieldRefList);
                            strJsonAdd.add(dealSingleMessage);
                            unqIdsAdd.add((String) bean.get("unique_id"));
                        } else if ("2".equals(status)) { // 更新
                            isUpd = true;
                            JSONObject dealSingleMessage = dealSingleMessage(bean, orgFieldRefList);
                            strJsonUpd.add(dealSingleMessage);
                            unqIdsUpd.add((String) bean.get("unique_id"));
                        }

                    }
                }
                if (isAdd) {
                    if (syncDataParam.getIsComplete()) {
                        sendKafkaMsg(strJsonAdd.toString(), "organizations_full");
                    } else {
                        sendKafkaMsg(strJsonAdd.toString(), "organization_insert");
                    }
                    if (!syncDataParam.getIsComplete()) {
                        if (isSendSuccess) {
                            callSucessSync(unqIdsAdd, "org", conn);
                        } else {
                            return errorMsg;
                        }
                    } else {
                        if (!isSendSuccess) {
                            return errorMsg;
                        }
                    }
                }
                if (isUpd) {
                    sendKafkaMsg(strJsonUpd.toString(), "organization_update");
                    if (isSendSuccess) {
                        callSucessSync(unqIdsUpd, "org", conn);
                    } else {
                        return errorMsg;
                    }
                }
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("syncOrgDataAddOrUpdate:数据同步出错!,ErrorMessage:{}", e);
            }
        }
        return errorMsg;
    }

    @Override
    public String syncOrgDataDelete(ArrayList<LazyDynaBean> datalist) {
        if (datalist != null && datalist.size() > 0) {
            Boolean isDel = false;
            List<String> unqIds = new ArrayList<>();
            try {
                JSONArray jsonDel = new JSONArray();
                JSONObject json = null;
                for (int i = 0; i < datalist.size(); i++) {
                    LazyDynaBean bean = datalist.get(i);
                    String status = (String) bean.get("status");
                    if ("3".equals(status)) {
                        isDel = true;
                        json = new JSONObject();
                        json.put(syncDataParam.getDestOrgUniqueFld(), (String) bean.get(syncDataParam.getDestOrgUniqueFld()));
                        jsonDel.add(json);
                    }
                    unqIds.add((String) bean.get("unique_id"));
                }
                if (isDel) {
                    sendKafkaMsg(jsonDel.toString(), "organization_delete");
                    if (isSendSuccess) {
                        callSucessSync(unqIds, "org", conn);
                    } else {
                        return errorMsg;
                    }
                }
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("syncOrgDataDelete:数据同步出错!,ErrorMessage:{}", e);
            }
        }
        return errorMsg;
    }

    @Override
    public String syncPostDataAddOrUpdate(ArrayList<LazyDynaBean> datalist) {
        Boolean isAdd = false;
        Boolean isUpd = false;
        List<String> unqIdsAdd = new ArrayList<>();
        List<String> unqIdsUpd = new ArrayList<>();
        try {
            ArrayList<FieldRefBean> postFieldRefList = syncDataParam.getPostFieldRefList();
            JSONArray strJsonAdd = new JSONArray();
            JSONArray strJsonUpd = new JSONArray();
            if (syncDataParam.getIsComplete()) {
                for (int i = 0; i < datalist.size(); i++) {
                    LazyDynaBean bean = datalist.get(i);
                    isAdd = true;
                    strJsonAdd.add(dealSingleMessage(bean, postFieldRefList));
                }
            } else {
                for (int i = 0; i < datalist.size(); i++) {
                    LazyDynaBean bean = datalist.get(i);
                    String status = (String) bean.get("status");
                    if ("1".equals(status)) { // 新增
                        isAdd = true;
                        strJsonAdd.add(dealSingleMessage(bean, postFieldRefList));
                        unqIdsAdd.add((String) bean.get("unique_id"));
                    } else if ("2".equals(status)) { // 更新
                        isUpd = true;
                        strJsonUpd.add(dealSingleMessage(bean, postFieldRefList));
                        unqIdsUpd.add((String) bean.get("unique_id"));
                    }

                }
            }
            if (isAdd) {
                if (syncDataParam.getIsComplete()) {
                    sendKafkaMsg(strJsonAdd.toString(), "posts_full");
                } else {
                    sendKafkaMsg(strJsonAdd.toString(), "post_insert");
                }
                if (!syncDataParam.getIsComplete()) {
                    if (isSendSuccess) {
                        callSucessSync(unqIdsAdd, "post", conn);
                    } else {
                        return errorMsg;
                    }
                } else {
                    if (!isSendSuccess) {
                        return errorMsg;
                    }
                }
            }
            if (isUpd) {
                sendKafkaMsg(strJsonUpd.toString(), "post_update");
                if (isSendSuccess) {
                    callSucessSync(unqIdsUpd, "post", conn);
                } else {
                    return errorMsg;
                }
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.error("syncPostDataAddOrUpdate:数据同步出错!,ErrorMessage:{}", e);
        }

        return errorMsg;
    }

    @Override
    public String syncPostDataDelete(ArrayList<LazyDynaBean> datalist) {
        Boolean isDel = false;
        List<String> unqIds = new ArrayList<>();
        try {
            JSONArray jsonDel = new JSONArray();
            JSONObject json = null;
            for (int i = 0; i < datalist.size(); i++) {
                LazyDynaBean bean = datalist.get(i);
                String status = (String) bean.get("status");
                if ("3".equals(status)) {
                    isDel = true;
                    json = new JSONObject();
                    json.put(syncDataParam.getDestPostUniqueFld(), (String) bean.get(syncDataParam.getDestPostUniqueFld()));
                    jsonDel.add(json);
                }
                unqIds.add((String) bean.get("unique_id"));
            }
            if (isDel) {
                sendKafkaMsg(jsonDel.toString(), "post_delete");
                if (isSendSuccess) {
                    callSucessSync(unqIds, "post", conn);
                } else {
                    return errorMsg;
                }
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            log.error("syncPostDataDelete:数据同步出错!,ErrorMessage:{}", e);
        }
        return errorMsg;
    }

    /**
     * 消息发布到kafka
     *
     * @param msg
     * @param key
     * @throws InterruptedException
     */
    private void sendKafkaMsg(String msg, String key) {
        isSendSuccess = true;
        String topic = "";//定义主题
        if (syncDataParam.getIsComplete()) {
            topic = "hrs_complete";
        } else {
            topic = "hrs_increment";
        }
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("data", msg);
        log.info("topic:{} key:{} 记录数: {}条", topic, key, JSONArray.fromObject(msg).size());

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);//kafka地址，多个地址用逗号分割
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 20000000);
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(p);
        try {
            ProducerRecord<String, String> record = new ProducerRecord(topic, key, jsonobj.toString());
            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        // 发送异常 do something
                        log.error("sendKafkaMsg:调用kafka接口出错!,key:{},ErrorMessage:{},bootstrapServers:{}", key, exception, bootstrapServers);
                        isSendSuccess = false;
                        errorMsg = "错误信息:" + exception.getMessage();
                    }
                }
            });

        } catch (Exception e) {
            isSendSuccess = false;
            errorMsg = "错误信息:" + e.getMessage();
            log.error("sendKafkaMsg:调用kafka接口出错!,topic:{},key:{},msg:{},ErrorMessage:{}", topic, key, jsonobj.toString(), e);
        } finally {
            kafkaProducer.close();
        }
        if (isSendSuccess) {
            try {
                String tempFile = System.getProperty("java.io.tmpdir");
                String fileName = tempFile + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()) + File.separator + topic + "_" + key + "_"
                        + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".json";
                File jFile = new File(fileName);
                FileUtils.write(jFile, printJson(jsonobj.toString()));
                log.info("kafka推送数据的json文件写入temp结束, file path :{}", jFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("kafka推送数据的json文件写入失败, desc :{}", e);
            }
        }
    }


    static int iCount = 0; //# topic的配置只需要配置一次即可

    /**
     * foamat josn
     *
     * @param json
     */
    public static String printJson(String json) {
        StringBuffer result = new StringBuffer();
        if (StringUtils.isBlank(json)) {
            log.info("json is empty");
            return "";
        }
        int level = 0;
        int length = json.length();
        for (int index = 0; index < length; index++) {
            char c = json.charAt(index);
            if (level > 0 && '\n' == result.charAt(result.length() - 1)) {
                result.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    result.append(c + "\n");
                    level++;
                    break;
                case ',':
                    result.append(c + "\n");
                    break;
                case '}':
                case ']':
                    result.append("\n");
                    level--;
                    result.append(getLevelStr(level));
                    result.append(c);
                    break;
                default:
                    result.append(c);
                    break;
            }
        }

        return result.toString();
    }

    /**
     * @param level
     * @return
     */
    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    /**
     * 创建kafka topic
     */
    private void createTopic(String zookeeperUrl, String topicName) {
        ZkUtils zkUtils = null;

        try {
            zkUtils = ZkUtils.apply(zookeeperUrl, 30000, 30000, JaasUtils.isZkSecurityEnabled());
            if (!AdminUtils.topicExists(zkUtils, topicName)) {
                AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), AdminUtils.createTopic$default$6());

                //新增topic增加配置
                modifyTopicConfig(topicName);
                log.info("messages:successful create!");
            } else {
                log.info(topicName + ":is exits!");
                if (iCount == 0) {
                    //新增topic增加配置
                    modifyTopicConfig(topicName);
                    iCount++;
                }
            }

        } catch (Exception e) {
            log.error("createTopic:创建topic出错!,ErrorMessage:{},topic:{}", e, topicName);
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
    }

    /**
     * 保留上次properties的方法修改主题级别配置
     *
     * @param topic
     */
    public void modifyTopicConfig(String topic) {
        ZkUtils zkUtils = null;
        try {
            // 参数从左到右，zk的IP端口，会话超时时间，连接超时时间，zk安全验证是否开启
            zkUtils = ZkUtils.apply(this.zookeeperUrl, 30000, 30000, JaasUtils.isZkSecurityEnabled());
            Properties curProp = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
            Properties properties = new Properties();
            String maxMessageBytes = SystemConfig.getPropertyValue("maxMessageBytes");//消息接收最大值
            if (StringUtils.isEmpty(maxMessageBytes)) {
                maxMessageBytes = "20000000";//默认值
            }
            properties.put("max.message.bytes", maxMessageBytes);
            if ("hrs_increment".equals(topic)) {
                properties.put("retention.ms", "90000000");
            } else {
                properties.put("retention.ms", "86400000");
            }
            curProp.putAll(properties);
            AdminUtils.changeTopicConfig(zkUtils, topic, curProp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zkUtils.close();
        }
    }

    /**
     * 单条信息处理，组装成json格式
     *
     * @param bean
     * @param fieldRefList
     * @return
     * @throws ParseException
     * @throws SQLException
     */
    private JSONObject dealSingleMessage(LazyDynaBean bean, ArrayList<FieldRefBean> fieldRefList) throws ParseException {
        JSONObject strJson = new JSONObject();
        for (int j = 0; j < fieldRefList.size(); j++) {
            FieldRefBean fieldRefBean = fieldRefList.get(j);
            String hrField = fieldRefBean.getHrField();
            String destField = (String) bean.get(fieldRefBean.getDestField());
            FieldItem fieldItem = DataDictionary.getFieldItem(hrField);
            if (fieldItem != null) {
                String itemtype = fieldItem.getItemtype();
                if (StringUtils.equalsIgnoreCase("D", itemtype)) {
                    if (!"".equals(destField) && destField != null) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = df.parse(destField);
                        destField = df.format(date);
                        destField = destField.replace("-", "");
                    }
                }
            }
            strJson.put(fieldRefBean.getDestField(), destField);

        }
        strJson.put("FILEID", bean.get("fileid"));
        strJson.put("BUSINO", bean.get("busiNo"));
        //增加子集处理
		/*List<LazyDynaBean> subsetInfoXml = null; // xml信息
		if(index.equals("emp")) {
			subsetInfoXml = getSubsetInfoXml("sync/fields_ref/empsubset/subset");
			if(subsetInfoXml!=null&&subsetInfoXml.size()>0) {
				String subsetJson = dealSubsetJson(subsetInfoXml, bean);
				str.append(subsetJson);
			}
		}else if (index.equals("org")) {
			subsetInfoXml = getSubsetInfoXml("sync/fields_ref/orgsubset/subset");
			if(subsetInfoXml!=null&&subsetInfoXml.size()>0) {
				String subsetJson = dealSubsetJson(subsetInfoXml, bean);
				str.append(subsetJson);
			}
		}else if (index.equals("post")) {
			subsetInfoXml = getSubsetInfoXml("sync/fields_ref/postsubset/subset");
			if(subsetInfoXml!=null&&subsetInfoXml.size()>0) {
				String subsetJson = dealSubsetJson(subsetInfoXml, bean);
				str.append(subsetJson);
			}
		}*/

        return strJson;
    }

    private String dealSubsetJson(List<LazyDynaBean> subsetInfoXml, LazyDynaBean bean) throws SQLException {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < subsetInfoXml.size(); i++) {
            str.append(",'");
            LazyDynaBean subset = subsetInfoXml.get(i);
            str.append(subset.get("desttable").toString());
            str.append("':[");
            String keyWordValue = (String) bean.get((String) subset.get("keyword"));
            List<Map<String, String>> fieldList = dealSubsetInfo(subset, keyWordValue);//拿到数据库中子集的映射指标和value值
            if (fieldList != null && fieldList.size() > 0) {
                for (int j = 0; j < fieldList.size(); j++) {
                    str.append("{'");
                    Map<String, String> map = fieldList.get(j);
                    for (Map.Entry<String, String> m : map.entrySet()) {
                        str.append(m.getKey());
                        str.append("':'");
                        str.append(m.getValue());
                        map.size();
                        str.append("','");
                    }
                    str.replace(str.length() - 2, str.length(), "");
                    str.append("}");
                    if (j != fieldList.size() - 1) {
                        str.append(",");
                    }
                }
            }
            str.append("]");
        }
        return str.toString();
    }

    /**
     * 根据路径名获取Kafka.xml中的某个节点的值
     *
     * @param path
     * @return
     */
    private String getNodeKafkaXml(String path) {
        File file = syncDataUtil.getFile(syncDataParam.getDestSysId() + ".xml");
        if (file == null) {
            log.error("未找到" + syncDataParam.getDestSysId());
            return null;
        }
        //初始化xml解析类
        PareXmlUtils pareXmlUtils = new PareXmlUtils(file);
        Element element = pareXmlUtils.getSingleNode(path);
        String value = element.getValue();
        return value;
    }

    /**
     * 调用更新成功的方法
     *
     * @param unqIds
     * @param index
     */
    private void callSucessSync(List<String> unqIds, String index, Connection conn) {
        if (unqIds.size() == 0) {
            return;
        }
        try {
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer sql = new StringBuffer("update ");
            if (StringUtils.equalsIgnoreCase(index, "emp")) {
                sql.append("t_hr_view");
            }
            if (StringUtils.equalsIgnoreCase(index, "org")) {
                sql.append("t_org_view");
            }
            if (StringUtils.equalsIgnoreCase(index, "post")) {
                sql.append("t_post_view");
            }
            sql.append(" set KAFKA = 0 WHERE unique_id in (");
            for (int i = 0; i < unqIds.size(); i++) {
                String uniqueId = unqIds.get(i);
                sql.append("'" + uniqueId);
                if (i % 999 == 0 && i != 0) {
                    sql.append("')");
                    sql.append(" or unique_id in (");
                } else {
                    sql.append("'");
                    sql.append(",");
                }
            }
            if (sql.toString().endsWith(",")) {
                sql.setLength(sql.length() - 1);
                sql.append(")");
            }
            dao.update(sql.toString());
            log.error("同步数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取xml中子集的配置信息
     *
     * @param path
     * @return
     */
    private List<LazyDynaBean> getSubsetInfoXml(String path) {
        File file = syncDataUtil.getFile(syncDataParam.getDestSysId() + ".xml");
        if (file == null) {
            log.error("未找到" + syncDataParam.getDestSysId());
            return null;
        }
        //初始化xml解析类
        PareXmlUtils pareXmlUtils = new PareXmlUtils(file);
        List nodes = pareXmlUtils.getNodes(path);
        if (nodes == null || nodes.size() == 0) {
            log.error("path:" + path + ",未获取到！");
            return null;
        }
        List<LazyDynaBean> subsets = new ArrayList<LazyDynaBean>();
        LazyDynaBean subset = null;
        for (int k = 0; k < nodes.size(); k++) {
            Element element = (Element) nodes.get(k);
            subset = new LazyDynaBean();
            subset.set("hrtable", element.getAttributeValue("hrtable"));
            subset.set("desttable", element.getAttributeValue("desttable"));
            subset.set("keyword", element.getAttributeValue("keyword"));
            List<Element> subsetsXml = element.getChildren("field_ref");
            List<FieldRefBean> fieldBeans = new ArrayList<FieldRefBean>();
            FieldRefBean fieldBean = null;
            for (int i = 0; i < subsetsXml.size(); i++) {
                element = subsetsXml.get(i);
                fieldBean = new FieldRefBean();
                fieldBean.setHrField(element.getAttributeValue("hrfield"));
                fieldBean.setDestField(element.getAttributeValue("destfield"));
                fieldBean.setFlddesc(element.getAttributeValue("desc"));
                fieldBeans.add(fieldBean);
            }
            subset.set("fieldBeans", fieldBeans);
            subsets.add(subset);
        }
        return subsets;
    }

    /**
     * 从数据库中拿到当前子集的数据
     *
     * @param subset
     * @return
     * @throws SQLException
     */
    private List<Map<String, String>> dealSubsetInfo(LazyDynaBean subset, String keyWordValue) throws SQLException {
        if (subset == null) {
            log.error("subset为空！");
            return null;
        }
        StringBuffer sql = new StringBuffer("select ");
        List<FieldRefBean> fieldBeans = (List<FieldRefBean>) subset.get("fieldBeans");
        FieldRefBean fieldRefBean = null;
        for (int i = 0; i < fieldBeans.size(); i++) {
            fieldRefBean = fieldBeans.get(i);
            sql.append(fieldRefBean.getHrField());
            sql.append(" ");
            sql.append(fieldRefBean.getDestField());
            sql.append(",");
        }
        sql.replace(sql.length() - 1, sql.length(), " ");
        sql.append("from ");
        sql.append(subset.get("hrtable"));
        sql.append(" where ");
        sql.append(subset.get("keyword"));
        sql.append(" = ");
        sql.append(keyWordValue);
        ContentDAO dao = new ContentDAO(conn);
        RowSet search = dao.search(sql.toString());
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = null;
        while (search.next()) {
            map = new HashMap<String, String>();
            for (int i = 0; i < fieldBeans.size(); i++) {
                fieldRefBean = fieldBeans.get(i);
                String value = search.getString(fieldRefBean.getDestField());
                map.put(fieldRefBean.getDestField(), value == null ? "" : value);
            }
            list.add(map);
        }
        return list;
    }


}
