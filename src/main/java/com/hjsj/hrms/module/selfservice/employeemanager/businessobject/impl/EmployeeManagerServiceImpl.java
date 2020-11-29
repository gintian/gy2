package com.hjsj.hrms.module.selfservice.employeemanager.businessobject.impl;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.selfservice.employeemanager.businessobject.IEmployeeManagerService;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Title EmployeeManagerServiceImpl
 * @Description 员工管理模块接口实现类
 * @Company hjsj
 * @Author houby
 * @Date 2020/04/27
 * @Version 1.0.0
 */

public class EmployeeManagerServiceImpl implements IEmployeeManagerService {
    private Connection conn;
    private UserView userView;
    private ContentDAO dao;
    public EmployeeManagerServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        dao = new ContentDAO(this.conn);
    }
    /**
     * 获得查看员工指标集信息
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return Map
     * @throws GeneralException
     * @author houby
     */
    @Override
    public Map searchEmployeeSetInfo(String nbase, String a0100, Boolean needPhoto) throws GeneralException {
        Map return_data = new HashMap();
        Map infoMap = getInfoList(nbase, a0100);
        List infoFieldViewList = (List) infoMap.get("infoFieldViewList");
        List infoSetList = (List) infoMap.get("infoSetList");
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        // 按信息分类浏览员工信息 0:否 1:是
        String infoSort = sysbo.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
        // 照片地址
        if (needPhoto) {
            PhotoImgBo imgBo = new PhotoImgBo(this.conn);
            imgBo.setIdPhoto(true);
            if(imgBo.getPhotoPath(nbase, a0100).indexOf("photo.jpg")!=-1){
                return_data.put("photoPath","nophoto");
            }else{
                return_data.put("photoPath",imgBo.getPhotoPath(nbase, a0100));
            }
            return_data.put("name", this.userView.getUserFullName());
            return_data.put("nbase", PubFunc.encrypt(nbase));
            return_data.put("a0100", PubFunc.encrypt(a0100));
        }
        if(infoSetList.size()<1){
            return return_data;
        }
        return_data.put("setList", getSetList(infoFieldViewList, infoSetList, infoSort));
        return return_data;
    }
    /**
       * 获取主集数据
       * @author houby
       * @param nbase
       * @param a0100
       * @return Map
       * @throws Exception
       */
    @Override
    public Map searchEmployeeMainSetInfo(String nbase, String a0100) throws Exception{
        Map return_data = new HashMap();
        SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        // 按信息分类浏览员工信息 0:否 1:是
        String infoSort = sysbo.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
        Map infoMap = getInfoList(nbase,a0100);
        List infoFieldViewList= (List) infoMap.get("infoFieldViewList");
        List infoSetList= (List) infoMap.get("infoSetList");
        List groupList = new ArrayList();
        List infolist=null;
        ArrayList subsort_list=infoxml.getView_tag("A01");//主集分类
        if(StringUtils.equalsIgnoreCase("1",infoSort) && subsort_list.size()>0){
            return_data.put("haveGroup",true);
        }else {
            return_data.put("haveGroup",false);
        }
        //主集有分组
        if(StringUtils.equalsIgnoreCase("1",infoSort) && subsort_list!=null&&subsort_list.size()>0) {
            String sortName="";
            for(int i=0;i<subsort_list.size();i++) {
                HashMap hm=new HashMap();
                infolist=new ArrayList();
                sortName=subsort_list.get(i).toString();
                if(sortName!=null&& "未分类指标".equals(sortName)) {
                    StringBuffer infoFielditem=new StringBuffer();
                    String  iSortName="";
                    for(int n=0;n<subsort_list.size();n++) {
                        iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
                        infoFielditem.append(infoxml.getView_value("A01", iSortName)+",");;
                    }
                    infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem.toString(),false);
                }else if(i==subsort_list.size()-1) {
                    String infoFielditem=infoxml.getView_value("A01", sortName);
                    infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
                    StringBuffer infoFielditems=new StringBuffer();
                    String  iSortName="";
                    infoFielditems.append(infoFielditem+",");
                    for(int n=0;n<subsort_list.size();n++) {
                        iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
                        infoFielditems.append(infoxml.getView_value("A01", iSortName)+",");;
                    }
                    List no_infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditems.toString(),false);
                    for(int s=0;s<no_infolist.size();s++) {
                        infolist.add(no_infolist.get(s));
                    }
                    infolist=reOrderinfoList(infoFieldViewList,infolist);
                }
                else {

                    String infoFielditem=infoxml.getView_value("A01", sortName);
                    infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
                }
                List new_infolist = new ArrayList();
                for(int n =0;n<infolist.size();n++){
                    //Map info_map = new HashMap();
                    FieldItemView fieldItemView = (FieldItemView)infolist.get(n);
                    Map<String, Object> describe = BeanUtils.describe(fieldItemView);
                    describe.remove("class");
                    new_infolist.add(describe);
                }
                if(infolist.size()<1){
                    continue;
                }
                hm.put("groupName",sortName);
                hm.put("fieldList", new_infolist);
                groupList.add(hm);
            }
            return_data.put("groupList",groupList);
        }else{
            List fieldList = tansFileItemToMap(infoFieldViewList);
            return_data.put("fieldList",fieldList);
        }
        //获取主集附件
        return_data.put("multimedialist",getMainsetmediaList(nbase,a0100));
        return return_data;
    }

    /**
       * 获取主集附件
       * @param nabse 人员库
       * @param a0100 人员编号
       * @return List
       */
    private List getMainsetmediaList(String nbase, String a0100){
        int I9999 = 0;
        String dbflag="A";
        String canedit ="";//是否可以对该附件进行编辑的参数，留在以后使用
        String state ="";//用于对该附件进行某些操作的参数，留在以后使用
        ArrayList mediaList = new ArrayList();
        //当登录用户的a0100和查看的人员的a0100一致时，默认为是查看自己的子集附件，不在校验权限
        if(!a0100.equals(this.userView.getA0100())) {
            CheckPrivSafeBo checkPiv = new CheckPrivSafeBo(this.conn, this.userView);
            nbase = checkPiv.checkDb(nbase);
            a0100 = checkPiv.checkA0100("", nbase, a0100, "");
        }
        ArrayList<LazyDynaBean> multimedialist = new ArrayList<LazyDynaBean>();
        MultiMediaBo multiMediaBo = new MultiMediaBo(this.conn,this.userView,dbflag,nbase,"A01",a0100,I9999);
        boolean hasRecord = true;
        if(("selfedit".equals(canedit) || "appview".equals(canedit)) && ("insert".equals(state) || "new".equals(state))) {
            hasRecord = false;
        }
        if(hasRecord){
            multimedialist=multiMediaBo.getMultimediaListByKey("");
        }
        for(LazyDynaBean bean : multimedialist){
            HashMap map = new HashMap();
            map.put("ext",bean.get("ext"));
            map.put("topic",bean.get("topic"));
            map.put("srcfilename",bean.get("srcfilename"));
            map.put("filepath",bean.get("path"));
            map.put("class",bean.get("class"));
            mediaList.add(map);
        }
        return mediaList;
    }
    /**
       * 获得查看员工子集信息数据
       * @author
       * @param nbase 人员库
       * @param a0100 人员编号
       * @param setId 子集id
       * @param groupName 分类名称
       * @param sortname 指标分类名称
       * @return Map
       * @throws GeneralException
       */
    @Override
    public Map searchEmployeeSubSetInfo(String nbase, String a0100, String setId, String groupName, String sortname)throws GeneralException{
        Map return_data = new HashMap();
        // 判断是否为个人中心查看自己的信息
        Boolean infoSelf = false;
        if(a0100.equalsIgnoreCase(this.userView.getA0100())){
            infoSelf = true;
        }
        if(!infoSelf){
            if("A0100".equals(a0100)){
                a0100=userView.getUserId();
            }else{
                CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.conn,this.userView);
                nbase=checkPrivSafeBo.checkDb(nbase);
                a0100=checkPrivSafeBo.checkA0100("", nbase, a0100, "");
                ContentDAO dao = new ContentDAO(this.conn);
                setId=checkPrivSafeBo.checkFieldSet(nbase, setId, a0100, Constant.EMPLOY_FIELD_SET, dao);
            }
            if("A00".equalsIgnoreCase(setId)){
                return_data = searchMultimediaSubInfo(nbase,setId,a0100);
            }else{
                return_data = searchSubInfo(nbase,a0100,setId,groupName,sortname);
            }
        }else{
            if(this.userView.getA0100()!=null&&this.userView.getA0100().length()>0)
            {
                String userbase=this.userView.getDbname(); //人员库
                String A0100=userView.getA0100();
                if("A00".equalsIgnoreCase(setId)){
                    return_data = searchMultimediaSubInfo(userbase,setId,A0100);
                }else{
                    return_data = searchSubInfo(userbase,A0100,setId,groupName,sortname);
                }
            }else {
                throw new GeneralException("", "非自助平台用户!", "", "");
            }
        }
        return  return_data;
    }

    /**
     * 获得查看员工子集某条记录附件数据
     * @author houby
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param setId 子集id
     * @param i9999 子集记录编号
     * @return Map
     * @throws GeneralException
     */
    @Override
    public Map searchEmployeeSubSetAttachmentInfo(String nbase, String a0100, String setId, int i9999) throws GeneralException {
        Map return_data = new HashMap();
        //查询有几条附件记录
        return_data = this.whetherHasFileRecord(nbase, setId, a0100, i9999);
        return return_data;
    }

    /**
     * 上传人员照片
     * @author houby
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param fileStr 照片文件的Base编码
     * @param fileName 文件名
     * @throws Exception
     */
    @Override
    public Map savePhoto(String nbase, String a0100, String fileStr, String fileName) throws Exception {
        Map return_data = new HashMap();
        RecordVo vo = new RecordVo(nbase.toLowerCase()+ "a00");
        ContentDAO dao=new ContentDAO(this.conn);
        // Base解码
        byte[] bytes = new BASE64Decoder().decodeBuffer(fileStr.split(",")[1].trim());
        // 转化为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        int recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(nbase + "a00", a0100, "A0100", this.conn));
        deleteDAO(a0100,nbase,"");
        try{
            vo.setString("a0100", a0100);
            vo.setInt("i9999", recid);
            vo.setString("flag", "P");
            vo.setInt("id", 0);
            vo.setDate("createtime", DateStyle.getSystemTime());
            vo.setDate("modtime", DateStyle.getSystemTime());
            vo.setString("createusername", userView.getUserName());
            vo.setString("modusername", userView.getUserName());
            int indexInt = fileName.lastIndexOf(".");
            String ext = fileName.substring(indexInt, fileName.length());
            vo.setString("ext", ext);
            /** blob字段保存,数据库中差异 */
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL:
                    break;
                default:
                    vo.setObject("ole", bytes);
                    break;
            }

            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
            String userName = this.userView.getUserName();
            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
                    getGuidKey(nbase, a0100), inputStream, fileName, "", false);
            vo.setString("fileid", fieldId);
            return_data.put("fieldid",fieldId);
            dao.addValueObject(vo);
            if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                RecordVo updatevo=new RecordVo(nbase + "a00");
                updatevo.setString("a0100",a0100);
                updatevo.setInt("i9999",recid);
                Blob blob = getOracleBlob(bytes,nbase,a0100,recid);
                updatevo.setObject("ole",blob);
                dao.updateValueObject(updatevo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
        return return_data;
    }

    /**
     * 获取人员主集的guidkey
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return
     */
    private String getGuidKey(String nbase, String a0100) {
        String guid = "";
        RowSet rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select GUIDKEY from ");
            sb.append(nbase + "A01");
            sb.append(" where a0100=?");
            ArrayList<String> paramList = new ArrayList<>();
            paramList.add(a0100);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sb.toString(), paramList);
            StringBuffer stmp = new StringBuffer();
            stmp.append("update  ");
            stmp.append(nbase + "A01");
            stmp.append(" set GUIDKEY =?");
            stmp.append(" where a0100=?");
            stmp.append(" and guidkey is null ");
            if (rs.next()) {
                guid = rs.getString("guidkey");
                if (StringUtils.isEmpty(guid)) {
                    UUID uuid = UUID.randomUUID();
                    guid = uuid.toString();
                    paramList.add(0, guid);
                    dao.update(stmp.toString(), paramList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return guid;
    }

    /**
     * 删除当前人员的A00对应的数据
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return
     */
    private void deleteDAO(String A0100, String nbase, String saveFile) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = null;
            StringBuffer sql = new StringBuffer();
            sql.append("select fileid from ");
            sql.append(nbase);
            sql.append("a00 where a0100=?");
            sql.append(" and flag='P'");
            ArrayList<String> paramList = new ArrayList<String>();
            paramList.add(A0100);
            rs = dao.search(sql.toString(), paramList);
            if (rs.next()) {
                String fileid = rs.getString("fileid");
                if(StringUtils.isNotEmpty(fileid)) {
                    VfsService.deleteFile(this.userView.getUserName(), rs.getString("fileid"));
                }
            }
            // liuy 2014-7-18 begin
            PhotoImgBo photoImgBo = new PhotoImgBo(conn);
            // 删除时只删除人员照片，不删除人员设置的头像 guodd 2016-06-20
            // haosl update 2018-1-30 同时删除低分辨率图片，否则okr的头像不会同步修改
            PhotoImgBo.delFileByName(saveFile, "photo,low_img");
            // liuy end
            StringBuffer deletesql = new StringBuffer();
            deletesql.append("delete from ");
            deletesql.append(nbase);
            deletesql.append("a00 where a0100=?");
            deletesql.append(" and flag='P'");
            dao.delete(deletesql.toString(), paramList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件的Blob对象
     * @param bytes 文件转为的byte[]类型
     * @param userbase 人员库
     * @param userid 人员编号
     * @param recid 该人员对应的i9999
     * @return Blob对象
     * @throws Exception
     */
    private Blob getOracleBlob(byte[] bytes, String userbase, String userid, int recid) throws Exception {
        StringBuffer strSearch=new StringBuffer();
        Blob blob= null;
        try{
            strSearch.append("select ole from ");
            strSearch.append(userbase);
            strSearch.append("a00 where a0100='");
            strSearch.append(userid);
            strSearch.append("' and i9999=");
            strSearch.append(recid);
            strSearch.append(" FOR UPDATE");

            StringBuffer strInsert=new StringBuffer();
            strInsert.append("update  ");
            strInsert.append(userbase);
            strInsert.append("a00 set ole=EMPTY_BLOB() where a0100='");
            strInsert.append(userid);
            strInsert.append("' and i9999=");
            strInsert.append(recid);
            blob = conn.createBlob();
            blob.setBytes(1, bytes);
        }catch(Exception e){
            e.printStackTrace();
        }
        return blob;
    }

    /**
     * @Title: whetherHasFileRecord
     * @Description: 是否显示附件图标
     * @param nbase
     * @param setId
     * @param a0100
     * @param i9999
     * @author: liuyang
     */
    public Map whetherHasFileRecord(String nbase,String setId,String a0100,int i9999){
        Map returnMap = new HashMap();
        RowSet rs=null;
        //附件的总数量
        int rows = 0;
        List mediaList = new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        MultiMediaBo multimediaBo =new MultiMediaBo(this.conn,this.userView,"A",nbase,setId,a0100,i9999);
        StringBuffer selectsql = new StringBuffer();
        StringBuffer wheresql = new StringBuffer();
        String kind = "";
        try {
            if(kind==""){
                kind="6";
            }
            selectsql.setLength(0);
            selectsql.append("select COUNT(1) as num from hr_multimedia_file");
            //根据主集，子集记录查询
            wheresql.append(" where mainguid ='").append(multimediaBo.getMainGuid()).append("'");
            if (multimediaBo.isMainSet()){
                wheresql.append(" and (childguid ='' or childguid is null )");
            }else
            {
                wheresql.append(" and childguid ='").append(multimediaBo.getChildGuid()).append("'");
            }
            wheresql.append(" and upper(nbase) ='").append(nbase.toUpperCase()).append("'");
            wheresql.append(" and A0100 ='").append(a0100).append("'");

            //根据当前用户权限查询分类
            ArrayList filetypeList = multimediaBo.getPowerTypeList(dao, kind, a0100);
            for (int i = 0; i < filetypeList.size(); i++) {
                if(i==0){
                    wheresql.append(" and (class ='").append(filetypeList.get(i)).append("'");
                }else {
                    wheresql.append(" or class ='").append(filetypeList.get(i)).append("'");
                }
            }
            if(filetypeList.size()>0){
                wheresql.append(")");
            }
            wheresql.append(" and dbflag ='").append("A").append("'");
            //查询符合条件的自己附件有几条
            rs = dao.search(selectsql.toString()+wheresql.toString());
            if(rs.next()){
                rows = rs.getInt("num");
            }
            //如果有附件的话才会进行下一步
            if(rows>0){
                returnMap.put("moreAttachment","false");
                rs=null;
                selectsql.setLength(0);
                selectsql.append("select topic,description,srcfilename,class,ext,path from hr_multimedia_file");
                rs = dao.search(selectsql.toString()+wheresql.toString());
                if (rows ==1&&rs.next()) {
                    returnMap.put("filePath",rs.getString("path"));
                }
                if(rows>1){
                    //查询出附件有记录，打印出附件图标
                    returnMap.put("moreAttachment","true");
                    while(rs.next()){
                        HashMap map = new HashMap();
                        map.put("topic",rs.getString("topic"));
                        map.put("srcfilename",rs.getString("srcfilename"));
                        map.put("description",rs.getString("description"));
                        map.put("class",rs.getString("class"));
                        map.put("filePath",rs.getString("path"));
                        map.put("icon",rs.getString("ext"));
                        mediaList.add(map);
                    }
                }
            }
            returnMap.put("fileList",mediaList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return returnMap;
    }

    /**
    * 获取子集列表信息
    * @author houby
    * @param s
    * @param nbase 人员库
    * @param a0100 人员编号
    * @param setId 子集id
    * @param groupName 分类名称
    * @param sortname 指标分类名称
    * @return Map
    * @throws GeneralException
    */
    private Map searchSubInfo(String nbase, String a0100, String setId, String groupName,String sortname) throws GeneralException {
        Map returnMap = new HashMap();
        List rs=null;
        Boolean infoSelf = false;
        if(a0100.equalsIgnoreCase(this.userView.getA0100())){
            infoSelf = true;
        }
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        // 按信息分类浏览员工信息 0:否 1:是
        String infoSort = sysbo.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
        //操纵表的名称
        String tablenamesub=nbase + setId;
        //保存sql的字符串
        StringBuffer strsql=new StringBuffer();
        SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
        if("".equalsIgnoreCase(groupName)||groupName.trim().length()==0){
            ArrayList<LazyDynaBean> arrlist = infoxml.getSet_A_Name$Text();
            for(LazyDynaBean bean : arrlist){
                if(bean.get("text").toString().indexOf(setId.toLowerCase())!=-1){
                    groupName = bean.get("name").toString();
                    break;
                }
            }
        }
        strsql.append("select * from " + tablenamesub);
        strsql.append(" where A0100='" + a0100 + "'" );
        InfoUtils infoUtils=new InfoUtils();
        ContentDAO dao=new ContentDAO(this.conn);
        String sub_type=infoUtils.getOneselfFenleiType(nbase, a0100, "", dao);//人员分类
        List infodetailfieldlist=null;
        List infoSetList=null;
        ArrayList subList=new ArrayList();//封装子集的数据
        String support_attachment = "";//子集是否支持附件
        boolean haveFGroup = true;//该子集是否有分组
        String currentGroup="";//当前所属分类
        int dataCount = 0;//该分类下数据总量
        List columnList = new ArrayList();//列表的列头信息
        List groupList = new ArrayList();//分类名称列表
        try {
            if(infoSelf){
                if(sub_type!=null&&sub_type.length()>0) {
                    //得到分类授权子集
                    infodetailfieldlist=infoUtils.getSubPrivFieldList(this.userView,setId,sub_type, 1);
                    infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
                    //如果分类中得不到指标则用默认权限的
                    if(infodetailfieldlist==null||infodetailfieldlist.size()<=0){
                        //获得当前子集的所有属性
                        infodetailfieldlist = userView.getPrivFieldList(setId, 0);
                    }
                    //获得所有权限的子集
                    if(infoSetList==null||infoSetList.size()<=0) {
                        infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                    }

                } else {
                    // 获得当前子集的所有属性
                    infodetailfieldlist = userView.getPrivFieldList(setId, 0);
                    // 获得所有权限的子集
                    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                }
            }else{
                if(sub_type!=null&&sub_type.length()>0) {
                    //得到分类授权子集
                    infodetailfieldlist=infoUtils.getSubPrivFieldList(this.userView,setId,sub_type);
                    infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
                    if(infodetailfieldlist==null||infodetailfieldlist.size()<=0) {//如果分类中得不到指标则用默认权限的
                        infodetailfieldlist = userView.getPrivFieldList(setId);//获得当前子集的所有属性
                    }
                    if(infoSetList==null||infoSetList.size()<=0) {
                        infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);    //获得所有权限的子集
                    }
                }else{
                    infodetailfieldlist= userView.getPrivFieldList(setId);
                    infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);    //获得所有权限的子集
                }
            }
            /*添加人员类别过滤指标*/
            String personsortfield=new SortFilter().getSortPersonField(this.conn);

            StringBuffer strsqlmain=new StringBuffer();
            strsqlmain.append("select * from ");
            strsqlmain.append(nbase);
            strsqlmain.append("A01 where A0100='");
            strsqlmain.append(a0100);
            strsqlmain.append("'");
            rs = ExecuteSQL.executeMyQuery(strsqlmain.toString(),this.conn,true);
            if(rs==null||rs.isEmpty()) {
                throw new GeneralException("", "没有该用户权限!", "", "");
            }
            LazyDynaBean reca01=(LazyDynaBean)rs.get(0);
            String personsort=null;
            if(personsortfield!=null && !infoSelf){
                personsort=reca01.get(personsortfield.toLowerCase())!=null?reca01.get(personsortfield.toLowerCase()).toString():null;
            }
            if(!infoSelf) {
                infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,personsort,this.conn);
                infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, nbase,this.conn);
                if(personsortfield!=null) {
                    infodetailfieldlist = new SortFilter().getSortPersonFilterField(infodetailfieldlist, personsort, this.conn);
                }
                infodetailfieldlist=new SortFilter().getPersonDBFilterField(infodetailfieldlist, nbase,this.conn);
            }
            String infolist = infoxml.getView_value("SET_A", groupName);
            if(infolist.trim().length()>0){
                List childList = infoxml.getInfoSortFieldSet(infoSetList,infolist,true);
                for(int n = 0;n<childList.size();n++){
                    FieldSet fieldSet = (FieldSet)childList.get(n);
                    groupList.add(fieldSet.getCustomdesc());
                    if(setId.equalsIgnoreCase(fieldSet.getFieldsetid())){
                        currentGroup = fieldSet.getCustomdesc();
                    }
                }
            }else{
                haveFGroup=false;
                currentGroup = groupName;
            }
            returnMap.put("haveFGroup",haveFGroup);
            returnMap.put("currentGroup",currentGroup);
            returnMap.put("groupList",groupList);
            //判断是否有子集分类
            if(StringUtils.equalsIgnoreCase("1",infoSort) && infoxml.getView_tag(setId)!=null&&infoxml.getView_tag(setId).size()>0){
                ArrayList<CommonData> sortList = this.disposeSort(setId,infodetailfieldlist);
                ArrayList sortNameList = new ArrayList();
                //处理混淆
                for(CommonData co : sortList){
                    sortNameList.add(co.getDataName());
                }
                returnMap.put("sortNameList",sortNameList);
                infodetailfieldlist=sortFieldList(sortList,infodetailfieldlist,sortname);
            }
            if(infodetailfieldlist==null||infodetailfieldlist.size()<=0) {
                infodetailfieldlist=new ArrayList();
                strsql.append(" and 1=2");
            }
            strsql.append(" order by i9999");
            rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.conn,true);
            for(int r=0;!rs.isEmpty() && r<rs.size();r++) {
                LazyDynaBean rec = (LazyDynaBean) rs.get(r);
                RecordVo vo = new RecordVo(tablenamesub, 1);
                vo.setString("a0100", rec.get("a0100") != null ? rec.get("a0100").toString() : "");
                vo.setInt("i9999", Integer.parseInt(rec.get("i9999").toString()));
                vo.setString("state", rec.get("state").toString());
                if(!infodetailfieldlist.isEmpty()){
                    for (int i = 0; i < infodetailfieldlist.size(); i++){
                        FieldItem fielditem = (FieldItem) infodetailfieldlist.get(i);
                        //将FieldItem转换为HashMap
                        if (!"0".equals(fielditem.getCodesetid())){//是否是代码类型的
                            String codevalue = rec.get(fielditem.getItemid()) != null ? rec.get(fielditem.getItemid()).toString() : "";        //是,转换代码->数据描述
                            String codesetid = fielditem.getCodesetid();
                            if (codevalue != null && codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0) {
                                if("A0901"!=null&&"A0901".equalsIgnoreCase(fielditem.getItemid().toString())&&"A09"!=null&&"A09".equalsIgnoreCase(setId)) {
                                    String value=AdminCode.getCode("UN",codevalue)!=null && AdminCode.getCode("UN",codevalue).getCodename()!=null?AdminCode.getCode("UN",codevalue).getCodename():"";
                                    if(value==null||value.length()<=0){
                                        value=AdminCode.getCode("UM",codevalue)!=null && AdminCode.getCode("UM",codevalue).getCodename()!=null?AdminCode.getCode("UM",codevalue).getCodename():"";
                                    }
                                    vo.setString(fielditem.getItemid(),value);
                                }else{
                                    String value=AdminCode.getCodeName(codesetid,codevalue);//AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
                                    if("UM".equals(codesetid)&&(value==null||value.length()<=0)){
                                        value=AdminCode.getCodeName("UN", codevalue);
                                    }
                                    vo.setString(fielditem.getItemid(),value);
                                }
                            }else{
                                vo.setString(fielditem.getItemid(),"");
                            }
                        }else {
                            if("D".equals(fielditem.getItemtype())){//日期类型的有待格式化处理
                                int itemlen =  fielditem.getItemlength();
                                String value =rec.get(fielditem.getItemid()).toString();
                                if ((value !=null) && (value.length()>=itemlen)){
                                    vo.setString(fielditem.getItemid().toLowerCase(),
                                            new FormatValue().format(fielditem,value));
                                }
                                else {
                                    vo.setString(fielditem.getItemid().toLowerCase(),"");
                                }
                            }else if("N".equals(fielditem.getItemtype())){//数值类型的
                                vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
                            }else if("M".equals(fielditem.getItemtype())) {
                                String content=rec.get(fielditem.getItemid()).toString();
                                if(content==null||content.length()<=0){
                                    content="";
                                }
                                content=content.replaceAll("\r\n","<br>");
                                vo.setString(fielditem.getItemid(),content);
                            }else{//其他字符串类型
                                vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
                            }
                        }
                    }
                }
                subList.add(vo.getValues());
            }
            //校验该子集是否支持含附件
            for(int p=0;p<infoSetList.size();p++){
                FieldSet fieldset=(FieldSet)infoSetList.get(p);
                if(setId.equals(fieldset.getFieldsetid())){
                    support_attachment = fieldset.getMultimedia_file_flag();
                    support_attachment = support_attachment==null?"0":support_attachment;
                    break;
                }
            }
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
        returnMap.put("support_attachment",support_attachment);
        infodetailfieldlist = tansFileItemToMap(infodetailfieldlist);
        returnMap.put("columnList",infodetailfieldlist);
        returnMap.put("dataList",subList);
        return returnMap;
    }

    /**
       * 获取多媒体子集数据信息
       * @author houby
       * @param nbase 人员库
       * @param setId 子集id
       * @param a0100 人员编号
       * @return Map
       * @throws GeneralException
     */

    private Map searchMultimediaSubInfo(String nbase, String setId, String a0100) throws GeneralException{
        Map returnMap = new HashMap();
        List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);    //获得所有权限的子集
        List infoList = new ArrayList();//多媒体子集信息集
        List mediaList = new ArrayList();//多媒体信息
        RowSet rs =null;
        //校验该子集是否支持含附件
        for(int p=0;p<infoSetList.size();p++){
            HashMap infomap = new HashMap();
            FieldSet fieldset=(FieldSet)infoSetList.get(p);
            infomap.put("infoSetId",fieldset.getFieldsetid());
            infomap.put("infoName",fieldset.getFieldsetdesc());
            infoList.add(infomap);
        }
        try{
            StringBuffer strsql=new StringBuffer();
            strsql.append("select flag,sortname from mediasort where dbflag=1");
            String mediasort="''";
            rs = dao.search(strsql.toString());
            while (rs.next()){
                String flagsort=rs.getString("flag");
                /**多媒体类型权限分析*/
                if(userView.isSuper_admin()) {
                    mediasort+=",";
                    mediasort+="'" + flagsort + "'";
                }
                else {
                    if(userView.hasTheMediaSet(flagsort)) {
                        mediasort+=",";
                        mediasort+="'" + flagsort + "'";
                    }
                }
            }
            strsql.setLength(0);
            rs = null;
            String tableName = nbase+setId;
            strsql.append("select ");
            strsql.append(tableName+".a0100,"+tableName+".i9999,"+tableName+".state,");
            strsql.append(tableName+".title,"+tableName+".flag,"+tableName+".fileid,");
            if(userView.isSuper_admin()) {
                mediasort = "";
            }
            strsql.append("mediasort.sortname from " + nbase + "A01 INNER JOIN " + tableName + " ON " + nbase + "A01.A0100=" + tableName + ".A0100 ");
            strsql.append(" LEFT JOIN mediasort ON " + tableName + ".Flag=mediasort.flag ");
            strsql.append(" where "+tableName+".A0100='"+a0100+"' and ");
            if(userView.isSuper_admin()) {
                strsql.append("(" +tableName + ".flag is null OR upper(" + tableName);
                strsql.append(".flag)<>'P')");
            }else{
                strsql.append("upper(" + tableName);
                strsql.append(".flag)<>'P' ");
            }
            //sunx,1110,+
            if(mediasort!=null&&mediasort.length()>0) {
                strsql.append(" and " + tableName + ".flag in(" + mediasort + ")");
            }
            strsql.append(" and dbflag=1");
            rs = dao.search(strsql.toString() + " order by i9999");
            while(rs.next()) {
                Map mediaInfoMap = new HashMap();
                mediaInfoMap.put("a0100",rs.getString("A0100"));
                mediaInfoMap.put("i9999",Integer.toString(rs.getInt("I9999")));
                mediaInfoMap.put("title",rs.getString("TITLE")==null?"":rs.getString("TITLE"));
                // WJH 分类可能为空
                if(rs.getString("SORTNAME")==null) {
                    mediaInfoMap.put("flag", " ");
                }else {
                    mediaInfoMap.put("flag", rs.getString("SORTNAME"));
                }
                mediaInfoMap.put("state",rs.getString("STATE"));
                mediaInfoMap.put("fileid", StringUtils.isEmpty(rs.getString("fileid")) ? "" : rs.getString("fileid"));
                mediaList.add(mediaInfoMap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //若请求的是 多媒体子集数据 则将信息集list放入return_data中
            if("A00".equalsIgnoreCase(setId)){
                returnMap.put("infoList",infoList);
            }
            returnMap.put("mediaList",mediaList);
        }
        return returnMap;
    }
    /**
       * 获取人员权限内的子集列表
       * @param infoFieldViewList 该子集下的指标信息
       * @param infoSetList 所有的子集信息
       * @param infoSort 是否按信息分类浏览员工信息
       * @return List
       */
    private List getSetList(List infoFieldViewList,List infoSetList,String infoSort) {
        SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
        ArrayList set_A_list=infoxml.getView_tag("SET_A");
        List setList = new ArrayList();//主集列表
        if(StringUtils.equalsIgnoreCase("1",infoSort)){
            String sub_Arr[] =(infoxml.getInfo_param("order")).split(",");
            int position_index=-1;//记录 “主集”所在下标
            FieldSet mainFieldSet = (FieldSet) infoSetList.get(0);
            String mainSetDesc = "";
            if(StringUtils.equalsIgnoreCase("A01",mainFieldSet.getFieldsetid())){
                mainSetDesc = mainFieldSet.getCustomdesc();
            }else{
                mainFieldSet = DataDictionary.getFieldSetVo("A01");
                mainSetDesc = mainFieldSet.getCustomdesc();
            }
            for(int i =0;i<sub_Arr.length;i++){
                if(StringUtils.equalsIgnoreCase(mainSetDesc,sub_Arr[i])){
                    position_index = i;
                    break;
                }
            }
            //将 主集移到第一项
            if(position_index!=-1){
                for(int n = 0;n<position_index;n++){
                    String temp = sub_Arr[n];
                    sub_Arr[n] = sub_Arr[position_index];
                    sub_Arr[position_index] = temp;
                }
            }
            for(int i =0;i<sub_Arr.length;i++){
                String setid = "";
                Map setMap = new HashMap();
                List groupList = new ArrayList();//子集列表
                if(set_A_list.contains(sub_Arr[i])){//主集有分组
                    setMap.put("isGroup",true);
                    //获取子集列表
                    String infolist = infoxml.getView_value("SET_A", sub_Arr[i]);
                    List childList = infoxml.getInfoSortFieldSet(infoSetList,infolist,true);
                    for(int n = 0;n<childList.size();n++){
                        Map groupMap = new HashMap();
                        FieldSet fieldSet = (FieldSet)childList.get(n);
                        groupMap.put("setName",fieldSet.getCustomdesc());
                        setid = fieldSet.getFieldsetid();
                        groupMap.put("setid",setid);
                        groupList.add(groupMap);
                    }
                }else{//主集无分组
                    setMap.put("isGroup",false);
                    for(int n = 0;n<infoSetList.size();n++){
                        FieldSet fieldSet = (FieldSet)infoSetList.get(n);
                        if(sub_Arr[i].equals(fieldSet.getCustomdesc())){
                            setid = fieldSet.getFieldsetid();
                            break;
                        }
                    }
                }
                if(setid.trim().length()<1){
                    continue;
                }
                setMap.put("setid",setid);
                setMap.put("setName",sub_Arr[i]);
                if(groupList.size()>0){
                    setMap.put("groupList",groupList);
                }
                setList.add(setMap);
            }
        }else{
            for(int i = 0;i<infoSetList.size();i++){
                Map setMap = new HashMap();
                FieldSet field = (FieldSet) infoSetList.get(i);
                setMap.put("isGroup",false);
                setMap.put("setid",field.getFieldsetid());
                setMap.put("setName",field.getCustomdesc());
                setList.add(setMap);
            }
        }
        return setList;
    }

    /**
     * 获取当前权限内的所有子集属性和子集
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return Map
     * @throws
     */
    private Map getInfoList(String nbase, String a0100) throws GeneralException{
        Map infoMap = new HashMap();
        List rs=null;
        List infoFieldList=null;
        List infoSetList=null;
        Boolean infoSelf = false;
        if(a0100.equalsIgnoreCase(this.userView.getA0100())){
           infoSelf = true;
        }
        String tablename=nbase + "A01";
        InfoUtils infoUtils=new InfoUtils();
        ContentDAO dao=new ContentDAO(this.conn);
        String sub_type=infoUtils.getOneselfFenleiType(nbase, a0100, "", dao);//人员分类
        if(infoSelf){
            if(sub_type!=null&&sub_type.length()>0) {
                infoFieldList=infoUtils.getSubPrivFieldList(this.userView,"A01",sub_type, 1);
                //得到分类授权子集
                infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
                //如果分类中得不到指标则用默认权限的
                if(infoFieldList==null||infoFieldList.size()<=0) {
                    //获得当前子集的所有属性
                    infoFieldList = userView.getPrivFieldList("A01", 0);
                }
                //获得所有权限的子集
                if(infoSetList==null||infoSetList.size()<=0) {
                    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                }

            } else {
                // 获得当前子集的所有属性
                infoFieldList = userView.getPrivFieldList("A01", 0);
                // 获得所有权限的子集
                infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
            }
        }else{
            if(sub_type!=null&&sub_type.length()>0) {
                infoFieldList=infoUtils.getSubPrivFieldList(this.userView,"A01",sub_type);
                //得到分类授权子集
                infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
                //如果分类中得不到指标则用默认权限的
                if(infoFieldList==null||infoFieldList.size()<=0) {
                    //获得当前子集的所有属性
                    infoFieldList = userView.getPrivFieldList("A01");
                }
                if(infoSetList==null||infoSetList.size()<=0) {
                    //获得所有权限的子集
                    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
                }

            }else {
                //获得当前子集的所有属性
                infoFieldList=userView.getPrivFieldList("A01");
                //获得所有权限的子集
                infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
            }
        }
        //保存处理后的属性
        List infoFieldViewList=new ArrayList();
        String filename="";
        String state="";
        try {
            boolean isExistData = false;
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from ");
            strsql.append(tablename);
            strsql.append(" where A0100='");
            strsql.append(a0100);
            strsql.append("'");
            rs = ExecuteSQL.executeMyQuery(strsql.toString(), this.conn, true);
            isExistData = !rs.isEmpty();
            /**有可能会出错*/
            if (!isExistData){
                return infoMap;
            }
            LazyDynaBean rec = (LazyDynaBean) rs.get(0);
            state = (String) rec.get("state");
            if(!infoSelf){
                infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,"ALL",this.conn);
                infoFieldList=new SortFilter().getSortPersonFilterField(infoFieldList,"ALL",this.conn);
                infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, nbase,this.conn);
                infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, nbase,this.conn);
            }
            if(!infoFieldList.isEmpty()) {
                GzDataMaintBo gzDataMaintBo=new GzDataMaintBo(this.conn);
                //字段的集合
                for(int i=0;i<infoFieldList.size();i++){
                    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
                    if("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
                        String UNIT_LEN=gzDataMaintBo.getValues("UNIT_LEN");
                        if(UNIT_LEN!=null&& "0".equals(UNIT_LEN)){
                            fieldItem.setVisible(false);
                        }
                        else{
                            fieldItem.setVisible(true);
                        }
                    }else if("e01a1".equalsIgnoreCase(fieldItem.getItemid())) {
                        String POS_LEN_str =gzDataMaintBo.getValues("POS_LEN");
                        if(POS_LEN_str!=null&& "0".equals(POS_LEN_str)){
                            fieldItem.setVisible(false);
                        }
                        else{
                            fieldItem.setVisible(true);
                        }
                    }
                    //只加在有读写权限的指标
                    if(fieldItem.getPriv_status() !=0){
                        FieldItemView fieldItemView=new FieldItemView();
                        fieldItemView.setVisible(fieldItem.isVisible());
                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid(fieldItem.getCodesetid());
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
                        fieldItemView.setItemdesc(fieldItem.getItemdesc());
                        fieldItemView.setItemid(fieldItem.getItemid());
                        fieldItemView.setItemlength(fieldItem.getItemlength());
                        fieldItemView.setItemtype(fieldItem.getItemtype());
                        fieldItemView.setModuleflag(fieldItem.getModuleflag());
                        fieldItemView.setState(fieldItem.getState());
                        fieldItemView.setUseflag(fieldItem.getUseflag());
                        fieldItemView.setPriv_status(fieldItem.getPriv_status());
                        //在struts用来表示换行的变量
                        fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
                        if(isExistData) {
                            if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype())) {
                                if(!"0".equals(fieldItem.getCodesetid())) {
                                    String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
                                    if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0) {
                                        //tianye update start
                                        //关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
                                        String name = "";
                                        if(!"e0122".equalsIgnoreCase(fieldItem.getItemid())){
                                            CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(),codevalue);
                                            name = (codeItem!=null ? codeItem.getCodename(): "");
                                        }else{
                                            name = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
                                        }
                                        fieldItemView.setFieldvalue(name);
                                        //end
                                    }
                                    else{
                                        fieldItemView.setFieldvalue("");
                                    }
                                    fieldItemView.setViewvalue(codevalue);
                                }
                                else {
                                    String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString().replaceAll("\n","<br>"):"";
                                    fieldvalue=fieldvalue.replaceAll("<br>","");
                                    fieldItemView.setFieldvalue(fieldvalue);

                                }
                                //日期型有待格式化处理
                            }else if("D".equals(fieldItem.getItemtype())){
                                int itemlen =  fieldItem.getItemlength();
                                String value =rec.get(fieldItem.getItemid()).toString();
                                if ((value !=null) && (value.length()>=itemlen)){
                                    fieldItemView.setFieldvalue(
                                            new FormatValue().format(fieldItem,
                                                    value));
                                }
                                else {
                                    fieldItemView.setFieldvalue("");
                                }
                            }
                            //数值类型的有待格式化处理
                            else {
                                fieldItemView.setFieldvalue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()));
                            }
                        }
                        fieldItemView.setRowindex(String.valueOf(i));
                        infoFieldViewList.add(fieldItemView);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        infoMap.put("infoFieldViewList",infoFieldViewList);
        infoMap.put("infoSetList",infoSetList);
        return infoMap;
    }

    /**
     * 处理子集的分类
     * @param setId 子集Id
     * @param infodetailfieldlist 当前子集指标集
     * @return ArrayList
     */
    private ArrayList disposeSort(String setId, List infodetailfieldlist){
        SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
        ArrayList sortlist=infoxml.getView_tag(setId);
        if(sortlist==null||sortlist.size()<=0) {
            return null;
        }
        String sortname="";
        ArrayList list=new ArrayList();
        String viewFieldid_t = "";
        CommonData co=null;
        String viewvalue="";
        for(int i=0;i<sortlist.size();i++) {
            sortname=sortlist.get(i)!=null?sortlist.get(i).toString():"";
            if(sortname==null||sortname.length()<=0) {
                continue;
            }
            viewvalue=infoxml.getView_value(setId,sortname);
            if(viewvalue==null||viewvalue.length()<=0) {
                continue;
            }
            viewFieldid_t += viewvalue + ",";
            for(int r=0;r<infodetailfieldlist.size();r++) {
                FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
                if(viewvalue.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1) {
                    co=new CommonData();
                    co.setDataName(sortname);
                    co.setDataValue(viewvalue);
                    list.add(co);
                    break;
                }
            }
        }
        // 默认把没有分组的指标放到最后一个分组中
        if(list != null && list.size() > 0) {
            CommonData lastCo = (CommonData) list.get(list.size() - 1);
            viewvalue = lastCo.getDataValue();
            if(!viewvalue.endsWith(","))
                viewvalue += ",";

            for(int r=0;r<infodetailfieldlist.size();r++) {
                FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
                if(viewFieldid_t.toLowerCase().indexOf(fielditem.getItemid().toLowerCase()) ==-1) {
                    viewvalue += fielditem.getItemid() + ",";
                }
            }

            if(!viewvalue.endsWith(",")) {
                viewvalue = viewvalue.substring(0, viewvalue.length() - 1);
            }

            lastCo.setDataValue(viewvalue);
        }
        return list;
    }

    /**
     * 过滤当前子集下的指标项
     * @param sortlist 指标分类集
     * @param infodetailfieldlist 当前子集指标集
     * @param sortname 子集分类名称
     * @return List
     */
    private List sortFieldList(ArrayList sortlist,List infodetailfieldlist,String sortname) {
        if(sortlist==null||sortlist.size()<=0) {
            return infodetailfieldlist;
        }
        if(sortname==null||sortname.length()<=0) {
            CommonData co=(CommonData)sortlist.get(0);
            sortname=co.getDataName();
        }
        String name="";
        String viewvalue="";
        for(int i=0;i<sortlist.size();i++) {
            CommonData co=(CommonData)sortlist.get(i);
            name=co.getDataName();
            if(name.equalsIgnoreCase(sortname)) {
                viewvalue=co.getDataValue();
                break;
            }
        }
        List newfieldlist=new ArrayList();
        if(viewvalue==null||viewvalue.length()<=0) {
            newfieldlist = infodetailfieldlist;
        } else {
            for(int r=0;r<infodetailfieldlist.size();r++) {
                FieldItem fielditem=(FieldItem)infodetailfieldlist.get(r);
                if(viewvalue.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1) {
                    newfieldlist.add(fielditem);
                }
            }
        }
        return newfieldlist;
    }

    private List reOrderinfoList(List infoFieldViewList,List infolist) {
        List infoFieldList=new ArrayList();
        if(infolist==null||infolist.size()<=0){
            return infoFieldList;
        }
        for(int i=0;i<infoFieldViewList.size();i++) {
            FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
            for(int r=0;r<infolist.size();r++) {
                FieldItemView fieldItem=(FieldItemView)infolist.get(r);
                if(fieldItem.getItemid().equals(fieldItemView.getItemid())) {
                    infoFieldList.add(fieldItemView.clone());
                }
            }
        }
        return infoFieldList;
    }

    /**
     * 将混淆后的FieldItem转换为Map传到前端使用
     * @param fieldItemList FieldItem列表
     * @return 转换后的列表数据
     */
    private List<Map<String,Object>>tansFileItemToMap(List<FieldItem> fieldItemList){
        List<Map<String,Object>> transList = new ArrayList<>();
        for(FieldItem item : fieldItemList){
            try {
                Map<String, Object> transMap = BeanUtils.describe(item);
                transMap.remove("class");
                transList.add(transMap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return transList;
    }
}
