package com.hjsj.hrms.businessobject.infor.multimedia;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:MultiMediaBo.java</p>
 * <p>Description>:子集附件类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-24 下午01:30:02</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class MultiMediaBo {
    
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
    private DbWizard dbw;
    
    private String dbFlag="A";//人员 单位 岗位 A B K
    private String keyField="A0100";//人员 单位 岗位 A B K
    private String setId;//子集 
    
    private String Nbase;
    private String A0100;
    private int I9999;//主集 i999为0 
    
    private String MainGuid;
    private String ChildGuid;
    
    private String RootDir;//文件根目录
    private String RelativeDir;//相对路径
    private String DestFileName="";//文件名
    private HashMap topicMap = new HashMap();//存放被删除的附件的标题
  
    /**
     * @param conn
     * @param userview
     * @param dbflag
     * @param setid
     * @param nbase
     * @param a0100
     * @param i9999
     */
    public MultiMediaBo(Connection conn,UserView userview) {
        this.conn=conn;
        this.userView=userview;
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);

     
    }
    
    /**
     * @param conn
     * @param userview
     * @param dbflag
     * @param setid
     * @param nbase
     * @param a0100
     * @param ChildGuid
     */
    public MultiMediaBo(Connection conn,UserView userview,
            String dbflag,String nbase,String setid, String a0100,String ChildGuid ) {
        this.conn=conn;
        this.userView=userview;
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);

        this.setId = setid;
        this.Nbase = nbase;
        this.A0100 = a0100;
        this.ChildGuid =ChildGuid;
        this.dbFlag=dbflag;
        if ("A".equals(this.dbFlag)){
            keyField ="A0100";
            InitGuidKeyValue();
        }

    }
    /**
     * @param conn
     * @param userview
     * @param dbflag
     * @param setid
     * @param nbase
     * @param a0100
     * @param i9999
     */
    public MultiMediaBo(Connection conn,UserView userview,
            String dbflag,String nbase,String setid, String a0100,int i9999 ) {
        this.conn=conn;
        this.userView=userview;
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);

        this.setId = setid;
        this.Nbase = nbase;
        this.A0100 = a0100;
        this.I9999 =i9999;
        this.dbFlag=dbflag;
        if ("A".equals(this.dbFlag)){
            keyField ="A0100";
            InitGuidKeyValue();
        }

    }
    
    /**   
     * @Title: getMultimediaListByKey   
     * @Description:  通过人员编号获取多媒体列表  
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    private void InitGuidKeyValue()
    {
        ArrayList retlist = null;
        StringBuffer sb = new StringBuffer();
        RowSet frowset;
        try {
            // 检查guidkey字段
            String mainTable = "";
            String childTable = "";
            if ("A".equals(this.dbFlag)) {
                mainTable = this.Nbase + "A01";
                childTable = this.Nbase + this.setId;
            }
            if (isMainSet()) {
                addGuidKeyField(mainTable);
            } else {
                addGuidKeyField(mainTable);
                addGuidKeyField(childTable);
            }
            // 获取mainguid childguid
            this.MainGuid = getGuidKey(mainTable, true);
            if (!isMainSet()) {
            	if(StringUtils.isEmpty(this.ChildGuid)) {
                    this.ChildGuid = getGuidKey(childTable, false);
                }
            } else {
                this.ChildGuid = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
}
    public String getSearchSql(String filetype)
    {
        StringBuffer sb = new StringBuffer();
        String kind = "";
        if (filetype==null) {
            filetype ="";
        }
        
        try {
            if (this.A0100==null){
                return "";
            }
            
            if(kind==""){
            	    kind="6";
            }      

            sb.setLength(0);
            sb.append("select HC.id hc_id,HM.id,HM.mainguid,HM.childguid,HM.nbase,HM.a0100,HM.displayorder,HM.topic,HM.class,HM.description,HM.path,HM.filename,HM.srcfilename,HM.ext,HM.dbflag,HM.revflag");
            sb.append(",HC.SortName from hr_multimedia_file HM left join mediasort HC on HM.class=HC.Flag ");            
            sb.append(" where mainguid ='").append(this.MainGuid).append("'");
            if (isMainSet()){
                sb.append(" and (childguid ='").append("' or childguid is null )");  
            } else {
                sb.append(" and childguid ='").append(this.ChildGuid).append("'");
            }
            
            if ("A".equals(this.dbFlag)){          
                sb.append(" and upper(nbase) ='").append(this.Nbase.toUpperCase()).append("'");
                sb.append(" and HC.dbflag=1");
            }            
            sb.append(" and A0100 ='").append(this.A0100).append("'");

            //附件分类条件
            if (!"".equals(filetype)){
                sb.append(" and class ='").append(filetype).append("'");
            }else {
            	    ArrayList filetypeList = getPowerTypeList(dao, kind, this.A0100);
            	    if (filetypeList!= null && !filetypeList.isEmpty()) {
        				for (int i = 0; i < filetypeList.size(); i++) {
        					if(i==0){
        						sb.append(" and (class ='").append(filetypeList.get(i)).append("'");
        					}else {
        						sb.append(" or class ='").append(filetypeList.get(i)).append("'");
        					}
        				}
					sb.append(")");
            	    } else {
            	        //没有附件分类权限
            	        sb.append(" and 1=2");
            	    }
			}
            sb.append(" and HM.dbflag ='").append(this.dbFlag).append("'");
            sb.append(" order by displayorder");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
        
      /**
         * @Title: getMultimediaListByKey
         * @Description: 通过人员编号获取多媒体列表
         * @param
         * @return
         * @return ArrayList
         * @author:wangrd
         * @throws
         */
    public ArrayList getMultimediaListByKey(String filetype)
    {       
        if (filetype==null) {
            filetype ="";
        }
        ArrayList retlist=null;
        RowSet frowset;
        try {
            if (this.A0100==null){
                return retlist;
            }
            String str=getSearchSql(filetype);
            frowset = dao.search(str);
            retlist = getRecordList(frowset,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retlist;
    }
    
    /**   
     * @Title: isHasMultimediaRecord   
     * @Description: 当前子集记录是否有附件记录   
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isHasMultimediaRecord()
    { boolean b=false;
        RowSet frowset;
        try {
            if (this.A0100==null){
                return b;
            }
            String str=getSearchSql("");
            frowset = dao.search(str);
            if (frowset.next()){
              b=true; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private void addGuidKeyField(String tablename )
    {
        try{
            if (!dbw.isExistField(tablename, "GUIDKEY", false)) {
                Table table = new Table(tablename);
                Field field = new Field("GUIDKEY","人员唯一标识");
                field.setDatatype(DataType.STRING);
                field.setKeyable(false);
                field.setLength(38);
                table.addField(field);
                dbw.addColumns(table);
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        }     
     }        
    
    /**   
     * @Title: getGuidKey   
     * @Description:    
     * @param @param tablename 表名
     * @param @param bMain 是否主集
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getGuidKey(String tablename,boolean bMain )
    {
        String guid="";
        try{
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();
            
            sWhere.append(" where "+keyField+" ='");
            sWhere.append(this.A0100);
            sWhere.append("'");
            if (!bMain){
                sWhere.append(" and i9999 =");
                sWhere.append(this.I9999); 
            }
            
            sb.append("select GUIDKEY from ");
            sb.append(tablename);     
            sb.append(sWhere.toString());     
            RowSet frowset=null;

            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (guid==null || "".equals(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString(); 
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);   
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");                    
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");   
                    dao.update(stmp.toString());                

                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");             
                    }
                }
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        }    
        return guid;
     }   
    
    private ArrayList getRecordList(RowSet frowset,boolean bTruncateDesc)
    {
    	String temp="";
        ArrayList retlist = new ArrayList();
          try {
        	  if(!VfsService.existPath()) {
                  throw new GeneralException("没有设置多媒体存储路径！请在【系统管理-应用设置-参数设置-系统参数-文件存放目录】设置。");
              }
              
              while (frowset.next()) {
                  String title = frowset.getString("topic");
                  if (title == null) {
                      title="";
                  }
                  if (bTruncateDesc){
                      temp=PubFunc.splitString(title, 16);
                      if(title!=temp){
                          title=temp+"...";
                      }
                  }
                  DynaBean vo = new LazyDynaBean();
                  vo.set("mediaid", frowset.getString("id"));
                  vo.set("mainguid", frowset.getString("mainguid"));
                  vo.set("topic", (title == null || "".equalsIgnoreCase(title)) ? "未知文件名" : title);
                  vo.set("childguid", frowset.getString("childguid"));
                  String sortname = frowset.getString("sortname");
                  sortname = sortname == null || "".equalsIgnoreCase(sortname) ? "未分类" : sortname;
                  if (bTruncateDesc){
                      temp=PubFunc.splitString(sortname, 10);
                      if(sortname!=temp){
                          sortname=temp+"...";
                      }
                  }
                  vo.set("class", sortname);      
                  vo.set("a0100", frowset.getString("a0100"));
                  vo.set("nbase", frowset.getString("nbase"));
                  String description = frowset.getString("description");
                  if (description==null) {
                      description="";
                  }
                  if (bTruncateDesc){
                      temp=PubFunc.splitString(description, 28);
                      if(description!=temp){
                          description=temp+"...";
                      }
                  }
                  
                  vo.set("description", description);
                  vo.set("path", frowset.getString("path"));
                  vo.set("filename", frowset.getString("filename"));
                  String ext = frowset.getString("ext");
                  String srcfilename = frowset.getString("srcfilename");

                  
                  vo.set("srcfilename", srcfilename);
                  vo.set("ext", ext);
                  vo.set("dbflag", frowset.getString("dbflag"));
                  try{
                	  vo.set("hc_id", frowset.getString("hc_id"));
                  } catch (Exception e) {
                  }
                  
                  retlist.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retlist;
    }

    
    /**   
   * @Title: getMultimedia   
   * @Description:  通过多媒体主键取得字段信息,可传多个主键值 按逗号分隔
   * @param @return 
   * @return ArrayList 
   * @author:wangrd   
   * @throws   
  */
  public ArrayList getMultimediaRecord(String ids)
  {
      
      ArrayList retlist= new ArrayList();
      if ("".equals(ids)) {
          return retlist;
      }
      String[] arrId= ids.split(",");
      String strId="";
      Pattern p = Pattern.compile("[0-9]*");
      for (int i=0;i<arrId.length;i++){
    	  String id = arrId[i];
    	  if(StringUtils.isEmpty(id)) {
              continue;
          }
    	  
    	  Matcher ma=p.matcher(id);
    	  if(!ma.matches()) {
              id = PubFunc.decrypt(id);
          }
    	  
         if ("".equals(strId)) {
             strId=id;
         } else {
             strId=strId+ ","+id;
         }
      }
      
      if ("".equals(strId)) {
          return retlist;
      }
      StringBuffer sb = new StringBuffer();
      RowSet frowset;
      try {        
          sb.append("select HM.*,HC.SORTNAME,hc.id hc_id from hr_multimedia_file HM,MediaSort HC");
          sb.append(" where HM.id in (");
          sb.append(strId);
          sb.append(") and HM.class=HC.FLAG");

          frowset = dao.search(sb.toString()); 
          retlist = getRecordList(frowset,false);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return retlist;
  }
  
  /**
   * @Title: changeFileTypeValue   
   * @Description: 将分类名（dataName）转换为分类值(dataValue) 
   * @param id
   * @return
   * @author: liuyang
   */
  public String changeFileTypeValue(String id) {
	  String filetypeValue = "";
	  StringBuffer sb = new StringBuffer();
	  RowSet rs = null;
	  try {
		  sb.append("select HM.class from hr_multimedia_file HM,MediaSort HC");
          sb.append(" where HM.id in (");
          sb.append(id);
          sb.append(") and HM.class=HC.FLAG");
          rs = dao.search(sb.toString());
          if (rs.next()) {
        	  filetypeValue = rs.getString(1);
          }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }finally{
		  try {
			  if (rs!=null) {
				  rs.close();
			  }
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
	  }
	  return filetypeValue;
  }

  /**
   * @Title: changeClassValue   
   * @Description: 将分类名（dataValue）转换为分类值(dataName) 
   * @param id
   * @return
   * @author: liuyang
   */
  public String changeClassValue(String id) {
	  String classValue = "";
	  StringBuffer sb = new StringBuffer();
	  RowSet rs = null;
	  try {
		  sb.append("select HC.SORTNAME from hr_multimedia_file HM,MediaSort HC");
          sb.append(" where HM.id in (");
          sb.append(id);
          sb.append(") and HM.class=HC.FLAG");
          if("A".equalsIgnoreCase(this.dbFlag)) {
        	  sb.append(" and HC.dbflag=1");
          } else if("B".equalsIgnoreCase(this.dbFlag)) {
        	  sb.append(" and HC.dbflag=2");
          } else if("K".equalsIgnoreCase(this.dbFlag)) {
        	  sb.append(" and HC.dbflag=3");
          }
          
          rs = dao.search(sb.toString());
          if (rs.next()) {
        	  classValue = rs.getString(1);
          }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }finally{
		  try {
			  if (rs!=null) {
				  rs.close();
			  }
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
	  }
	  return classValue;
  }
  private String getMultimediaOldFileName(String id)
  {
      String rfilename="";
      StringBuffer sb = new StringBuffer();
      RowSet frowset;
      try {        
          sb.append("select * from hr_multimedia_file");
          sb.append(" where id = ");
          sb.append(id);

          frowset = dao.search(sb.toString()); 
          if (frowset.next()){              
              String path=frowset.getString("path"); 
              String filename=frowset.getString("filename"); 
              String mainGuid = frowset.getString("mainguid"); 
              String childGuid = frowset.getString("childguid"); 
              String a0100 = frowset.getString("a0100");     
              String dbflag = frowset.getString("dbflag");     
              String nbase = frowset.getString("nbase");    

              //判断是否有其他记录引用  //返回需要删除的文件名 否则 返回空
              if (!isHaveOtherReference(mainGuid,childGuid,dbflag,nbase,a0100,path,filename)){
                  rfilename = path;
              }
  
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      return rfilename;
  }
  public void initParam() throws GeneralException
  {
      initParam(true);  
   }
  
	public void initParam(boolean bexcept) throws GeneralException {
		// 取参数 路径 大小
		try {
			if (!VfsService.existPath()) {
				if (bexcept) {
                    throw new GeneralException("没有配置多媒体存储路径！");
                } else {
                    return;
                }
			}

		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
  public HashMap getNeededInfo(HashMap valueMap,boolean bedit) throws GeneralException
  {    
      HashMap destMap = new HashMap();
      try{          
          String mainguid = (String)valueMap.get("mainguid");
          if(StringUtils.isEmpty(mainguid)) {
              mainguid = this.MainGuid;
          }
          
          String childguid = (String)valueMap.get("childguid");
          if(StringUtils.isEmpty(childguid)) {
              childguid = this.ChildGuid;
          }

          String nbase = (String)valueMap.get("nbase");
          if(StringUtils.isEmpty(nbase)) {
              nbase = this.Nbase;
          }
          
          String a0100 = (String)valueMap.get("a0100");
          if(StringUtils.isEmpty(a0100)) {
              a0100 = this.A0100;
          }
          
          String filetype = (String)valueMap.get("filetype");
          String filetitle = (String)valueMap.get("filetitle");
          String description = (String)valueMap.get("description");
          
          this.setMainGuid(mainguid);
          this.setChildGuid(childguid); 
          
          destMap.put("mainguid", mainguid);
          destMap.put("childguid", childguid);          
          destMap.put("nbase", nbase);
          destMap.put("a0100", a0100);
          destMap.put("filetype", filetype);
          destMap.put("filetitle", filetitle);
          destMap.put("description", description);
          if(StringUtils.isNotEmpty((String)valueMap.get("path"))) {
              destMap.put("path", (String)valueMap.get("path"));
          }
          
          if (bedit){
              String mediaId = (String)valueMap.get("mediaId");
              destMap.put("mediaId", mediaId);
          }

      }
      catch(Exception e ){
          
      }
      return destMap;

  }
  

   
	public boolean saveMultimediaRecord(HashMap valueMap, boolean bedit, boolean bHavefile) throws GeneralException {
		try {
			String mainguid = (String) valueMap.get("mainguid");
			String childguid = (String) valueMap.get("childguid");
			String nbase = (String) valueMap.get("nbase");
			String a0100 = (String) valueMap.get("a0100");
			String filetype = (String) valueMap.get("filetype");
			String filetitle = (String) valueMap.get("filetitle");
			String description = (String) valueMap.get("description");

			RecordVo vo;
			String oldfilename = "";
			String userName = this.userView.getUserName();
			if (bedit) {
				RecordVo vo1 = new RecordVo("hr_multimedia_file");
				String id = (String) valueMap.get("mediaId");
				vo1.setInt("id", Integer.parseInt(id));
				vo = dao.findByPrimaryKey(vo1);
				oldfilename = getMultimediaOldFileName(id);
				if (StringUtils.isNotEmpty(oldfilename)) {
					VfsService.deleteFile(userName, oldfilename);
				}
			} else {
				vo = new RecordVo("hr_multimedia_file");
				IDGenerator idg = new IDGenerator(2, this.conn);
				String id = idg.getId("hr_multimedia_file.id");
				vo.setInt("id", Integer.parseInt(id));
				vo.setInt("displayorder", Integer.parseInt(id));
				vo.setString("mainguid", mainguid);
				vo.setString("childguid", childguid);
				vo.setString("nbase", nbase);
				vo.setString("a0100", a0100);
				vo.setString("dbflag", this.dbFlag);
				vo.setString("createusername", this.userView.getUserName());
				Date date = DateUtils.getSqlDate(Calendar.getInstance());
				vo.setDate("createtime", date);
			}

			vo.setString("class", filetype);
			if (filetitle == null || "".equals(filetitle)) {
				filetitle = (String) valueMap.get("srcfilename");
				if (filetitle != null) {
					if (filetitle.lastIndexOf(".") > 0) {
                        filetitle = filetitle.substring(0, filetitle.lastIndexOf("."));
                    }
				}
			}
			if (filetitle != null) {
                vo.setString("topic", filetitle);
            }
			if (!bedit && this.topicMap.get(mainguid + "_" + childguid + "_" + nbase + "_" + a0100 + "_" + this.dbFlag) != null) {
                vo.setString("topic", (String) this.topicMap.get(mainguid + "_" + childguid + "_" + nbase + "_" + a0100 + "_" + this.dbFlag));
            }
			if (description != null) {
                vo.setString("description", description);
            }
			if (bHavefile) {
				String srcfilename = (String) valueMap.get("srcfilename");
				String fileext = (String) valueMap.get("ext");
				String path = (String) valueMap.get("path");
				String filename = (String) valueMap.get("filename");
				vo.setString("srcfilename", srcfilename);
				vo.setString("ext", fileext);
				vo.setString("path", path);
				vo.setString("filename", filename);
			}

			if (bedit) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}

		return true;
	}
     
 
    /**   
     * @Title: saveMultimediaFile   
     * @Description:  保存附件  
     * @param @param allMap
     * @param @param file
     * @param @param bedit
     * @param @return
     * @param @throws GeneralException 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean saveMultimediaFile(HashMap allMap,FormFile file,boolean bedit) throws GeneralException
    {        
        try{            
            HashMap valueMap = this.getNeededInfo(allMap, bedit);
            //上传文件,得到路径、名称    
            boolean bHavefile= false;
            if (SaveFileToDisk(file,valueMap)){   
               bHavefile= true;
            }
            
            String fileId = (String) allMap.get("path");
            if(!bHavefile && StringUtils.isNotEmpty(fileId)) {
            	bHavefile= true;
            }
 
            saveMultimediaRecord(valueMap,bedit,bHavefile); 
        }
        catch(Exception e)
        {            
            throw GeneralExceptionHandler.Handle(e);
        }
        return true;        
    }
    /**
     * 人事异动附件提交入库
     * @param allMap
     * @param bedit
     * @return
     * @throws GeneralException
     */
    public boolean saveMultimediaFile(HashMap allMap,boolean bedit) throws GeneralException
    {        
        try{            
        	 /*
            HashMap valueMap = this.getNeededInfo(allMap, bedit);
           //上传文件,得到路径、名称    
            boolean bHavefile= false;
            if (SaveFileToDisk(file,valueMap)){   
               bHavefile= true;
            }  */       
            saveMultimediaRecord(allMap,false,true); 
        }
        catch(Exception e)
        {            
            throw GeneralExceptionHandler.Handle(e);
        }
        return true;        
    }
    
    public boolean saveMultimediaFile(HashMap allMap,File file) throws GeneralException
    {        
        try{            
            saveMultimediaFile(allMap,file,false) ;
        }
        catch(Exception e)
        {            
            throw GeneralExceptionHandler.Handle(e);
        }
        return true;        
    }
    
    /**   
     * @Title: saveMultimediaFile   
     * @Description:    
     * @param @param allMap
     * @param @param file
     * @param @param bTemplate 人事异动调用 
     * @param @return
     * @param @throws GeneralException 
     * @return boolean 
     * @throws   
    */
    public boolean saveMultimediaFile(HashMap allMap,File file,boolean bTemplate) throws GeneralException
    {        
        try{            
            HashMap valueMap = this.getNeededInfo(allMap, false);
            //上传文件,得到路径、名称    
            boolean bHavefile= false;
            if (this.SaveFileToDisk(file,valueMap)){   
               bHavefile= true;
            }  
            if (bTemplate){//源文件名也是随机名，需要从记录中获取，重新赋值。
                String filetitle= (String)valueMap.get("filetitle");
                String ext= (String)valueMap.get("ext");
                valueMap.put("srcfilename", filetitle+ext); 
            }
            
            this.saveMultimediaRecord(valueMap,false,bHavefile); 
        }
        catch(Exception e)
        {            
            throw GeneralExceptionHandler.Handle(e);
        }
        return true;        
    }
    
    private String getRelativeDir(String dbflag,String mainguid,String childguid,String setid) throws GeneralException
    {
        String relative =dbflag;
        try{
            String str  = mainguid; 
            int iHash = Math.abs(str.hashCode());
            String dir1 = ""+iHash/1000000%500;
            while (dir1.length()<3) {
                dir1 ="0"+dir1;
            }
            String dir2 = ""+iHash/1000%500;
            while (dir2.length()<3) {
                dir2 ="0"+dir2;
            }
            relative =relative + "\\"+"A"+dir1 + "\\"+"A"+dir2 ;            
            relative =relative+"\\"+mainguid+"\\";
            if (!"".equals(setid)){              
                relative =relative+setid.toUpperCase()+"\\";                
                if ("01".equals(setid.substring(1))) {
                    relative =relative+mainguid;//主集的
                } else {
                    relative =relative+childguid;
                }
            }
            //创建目录
            String dir = this.RootDir + relative;   
            dir =dir.replace("\\", File.separator).replace("/", File.separator);
            File tempDir = new File(dir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
        }
        catch(Exception e){
           e.printStackTrace();            
        }
        return relative;
         
    }
    
    
	private boolean SaveFileToDisk(FormFile file, HashMap valueMap) throws GeneralException {
		boolean b = false;
		InputStream streamIn = null;
		if (file != null) {
			initParam();
			try {
				streamIn = file.getInputStream();
				b = SaveFileToDisk(file.getInputStream(), valueMap, file.getFileName());
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("保存文件失败！" + e.getMessage() + " 请联系系统管理员！");
			} finally {
				PubFunc.closeIoResource(streamIn);
			}

		}
		return b;
	}
    
	private boolean SaveFileToDisk(File file, HashMap valueMap) throws GeneralException {
		InputStream inputStream = null;
		boolean flag = false;
		try {
			inputStream = new FileInputStream(file);
			flag = SaveFileToDisk(inputStream, valueMap, file.getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(inputStream);
		}

		return flag;
	}
    
	private boolean SaveFileToDisk(InputStream inputStream, HashMap valueMap, String srcFilename)
			throws GeneralException {
		boolean b = false;
		String fileExt = "";
		if (inputStream != null) {
			try {
				initParam();
				long size = inputStream.available();
				if (size <= 0) {
					throw new GeneralException("上传文件大小为0:" + srcFilename);
				}
				
				if (srcFilename.lastIndexOf(".") > 0) {
                    fileExt = srcFilename.substring(srcFilename.lastIndexOf("."));// 扩展名
                }

				String userName = this.userView.getUserName();
				VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
				VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
				String mainGuidkey = (String) valueMap.get("mainguid");
				String fileId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
						mainGuidkey, inputStream, srcFilename, "", false);
				valueMap.put("ext", fileExt);
				valueMap.put("filename", "");
				valueMap.put("path", fileId);
				valueMap.put("srcfilename", srcFilename);
				b = true;
			} catch (GeneralException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException(e.getMessage());
			}
		}
		
		return b;
	}
    
 
    /**   
     * @Title: deleteMultimediaFile   
     * @Description: 删除附件   
     * @param ArrayList List 存放需要删除的多媒体记录
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean deleteMultimediaRecord(ArrayList List)
    {
        if(List.size()<1)        {
            return false;
        }
        try{
            initParam();
            for(int i=0;i<List.size();i++)
            {
                LazyDynaBean rec=(LazyDynaBean)List.get(i); 
                String id = rec.get("mediaid").toString(); 
                String mainGuid = rec.get("mainguid").toString(); 
                String childGuid = rec.get("childguid").toString(); 
                String nbase = rec.get("nbase").toString(); 
                String a0100 = rec.get("a0100").toString(); 
                String path = rec.get("path").toString(); 
                String filename = rec.get("filename").toString(); 
                String dbflag = rec.get("dbflag").toString();                 
                
                StringBuffer sb = new StringBuffer();
                sb.append(" delete hr_multimedia_file where id=").append(id);
                try
                {
                  //删除记录
                    dao.update(sb.toString());
                    //判断是否有其他记录引用  //删除文件
                    if (!isHaveOtherReference(mainGuid,childGuid,dbflag,nbase,a0100,path,filename)){
                        deleteFile(path);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }                
     
            }  
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return true;
        
    }
    
    private boolean isHaveOtherReference(String mainguid,String childguid,
            String dbflag,String nbase,
            String a0100,String path,String filename)
    {
        boolean b =false;
        StringBuffer sb = new StringBuffer();
        RowSet frowset;
        try {        
            sb.append("select count(*) as cnt from hr_multimedia_file");
            sb.append(" where mainguid ='").append(mainguid).append("'");
            if (isMainSet()){
                sb.append(" and (childguid ='").append("' or childguid is null )");  
            }
            else {
                sb.append(" and childguid ='").append(childguid).append("'");
            }
            if ("A".equals(dbflag)){
            	if(StringUtils.isNotEmpty(nbase)) {
                    this.Nbase = nbase;
                }
            	
                sb.append(" and upper(nbase) <>'").append(this.Nbase.toUpperCase()).append("'");
            }     
            sb.append(" and a0100<>'").append(a0100).append("'");
            sb.append(" and dbflag='").append(dbflag).append("'");
            sb.append(" and path ='").append(path).append("'");
            sb.append(" and filename ='").append(filename).append("'");
            frowset = dao.search(sb.toString()); 
            if (frowset.next()){              
                int cnt=frowset.getInt("cnt");
                if (cnt>0){
                    b= true;  
                }
         
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
    
    /**   
     * @Title: getReferenceFileNames   
     * @Description:获取当前人员 有其他引用的文件列表 ，删除主集子集记录时调用   
     * @param @param mainguid
     * @param @param childguid
     * @param @param dbflag
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getReferenceFileNames(String mainguid,String childguid,
            String dbflag,String nbase,
            String a0100)
    {
        String filenames ="";
        StringBuffer sb = new StringBuffer();
        RowSet frowset;
        try {        
            sb.append("select * from hr_multimedia_file");
            sb.append(" where 1=1  ");
            if (!"".equals(mainguid)) {
                sb.append(" and mainguid ='").append(mainguid).append("'");
            }
            if (!"".equals(childguid)) {
                    sb.append(" and childguid ='").append(childguid).append("'");
            }
            if ("A".equals(dbflag)){          
                sb.append(" and (upper(nbase) <>'").append(this.Nbase.toUpperCase()).append("'");                
                sb.append(" or a0100<>'").append(a0100).append("')");
            }  
            else {
                sb.append(" and a0100<>'").append(a0100).append("'");
                
            }
            if (!"".equals(dbflag)){                
                sb.append(" and dbflag='").append(dbflag).append("'");
            }
            frowset = dao.search(sb.toString()); 
            while (frowset.next()){              
                String filename=frowset.getString("filename");//guid 不会重复，以此判断
                filenames=filenames+"`"+filename;
         
            }
            filenames =filenames+"`";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filenames;
    }
    

    /**   
     * @Title: deleteAllMultimediaFile   
     * @Description: 删除指定人员的附件 list存储需要删除的人员信息   
     * @param @param List
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
	public boolean deleteAllMultimediaFile(ArrayList List) {
		boolean b = false;
		if (List.size() < 1) {
			return b;
		}
		try {
			initParam(false);// 如没设置rootdir 不能直接返回，必须去检查是否有附件记录
			for (int i = 0; i < List.size(); i++) {
				LazyDynaBean rec = (LazyDynaBean) List.get(i);
				String mainguid = (String) rec.get("mainguid");
				String setid = (String) rec.get("setid");
				String nbase = (String) rec.get("nbase");
				String childguid = (String) rec.get("childguid");
				String a0100 = (String) rec.get("a0100");
				String i9999 = (String) rec.get("i9999"); // 删除主集时 传0
				String dbflag = (String) rec.get("dbflag");
				if (dbflag == null) {
                    dbflag = "";
                }
				
				if (nbase == null) {
                    nbase = "";
                }
				
				if (i9999 == null || "".equals(i9999)) {
                    i9999 = "0";
                }
				
				if (a0100 == null) {
                    a0100 = "";
                }
				
				if ("".equals(a0100)) {
                    continue;
                }
				
				if (setid == null) {
                    setid = "";
                }
				
				if (mainguid == null) {
                    mainguid = "";
                }
				
				if (childguid == null) {
                    childguid = "";
                }
				
				if ("0".equals(i9999)) {
                    childguid = "";
                }
				
				if ("".equals(mainguid) && "".equals(childguid)) {// 没传guid 则需要从库中读取
					if ("".equals(setid) || "".equals(dbflag) || ("A".equals(dbflag) && "".equals(nbase))) {
                        continue;
                    }
					
					String tablename = setid;
					if ("A".equals(dbflag)) {
                        tablename = nbase + setid;
                    }
					if (!setid.endsWith("00")) {
                        if (!dbw.isExistField(tablename, "GUIDKEY", false)) {
                            continue;
                        }
                    }
					
					this.dbFlag = dbflag;
					this.setId = setid;
					this.Nbase = nbase;
					this.A0100 = a0100;
					this.I9999 = Integer.parseInt(i9999);
					InitGuidKeyValue();
					mainguid = this.MainGuid;
					childguid = this.ChildGuid;
				}

				if ("".equals(mainguid) && "".equals(childguid)) {
					continue;
				}
				if (setid.endsWith("00")) {
					String fileId = (String) rec.get("fileid");
					if(StringUtils.isEmpty(fileId)) {
						continue;
					}
					
					VfsService.deleteFile(this.userView.getUserName(), fileId);
				} else {
					deleteMultimediaFile(dbflag, mainguid, childguid, nbase, a0100);
				}
				// 删除目录 屏蔽 如果以后要删除，删除目录需判断是否有文件，有文件则不删除 wangrd 2014-06-10
				// if ((this.RootDir!=null) && (!"".equals(this.RootDir))){
				// if (setid.endsWith("01")) setid ="";
				// String relativeDir = getRelativeDir(dbflag,mainguid,childguid,setid);
				// deleteDirectory(this.RootDir+relativeDir);
				// }

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
       
    /**   
     * @Title: deleteA00File   
     * @Description: 删除多媒体子集的临时文件   
     * @param @param mainguid
     * @param @param childguid
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean deleteA00File(String dbflag,String mainguid,String childguid,String setid)
    {
        boolean b= false;
        try {
            if (this.RootDir==null || "".equals(this.RootDir)) {
                initParam(false);
            }
            if (this.RootDir==null || "".equals(this.RootDir)) {
                return b;
            }
            String relativeDir = getRelativeDir(dbflag,mainguid,childguid,setid);
            deleteDirectory(this.RootDir+relativeDir);  

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }
    
    
    /**   
     * @Title: deleteMultimediaFileBySetid   
     * @Description: 删除子集所有附件  
     * @param @param setid
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean deleteMultimediaFileBySetid(String setid)
    {
        boolean b= false;
        //不特殊处理A01的情况
        try {
            if (this.RootDir==null || "".equals(this.RootDir)) {
                initParam(false);
            }
            if (this.RootDir==null || "".equals(this.RootDir)) {
                return b;
            }
            StringBuffer sb = new StringBuffer();
            RowSet frowset;             
            sb.append("select * from hr_multimedia_file");
            //zxj 20160820 文件路径形如： A\A074\A355\0C5E858F-DFED-4BFB-9501-B13417A43BDA\A04\C1D1F755-AF52-4F8D-9764-C0D1F8A3DE3D
            sb.append(" where path like '").append(setid.toUpperCase().charAt(0)).append("\\%\\%\\%\\");
            sb.append(setid.toUpperCase());
            sb.append("\\%'");          
            frowset = dao.search(sb.toString());
            while (frowset.next()) {
                String id = frowset.getString("id");    
                String path = frowset.getString("path");
                deleteFile(path);            
                // 删除记录
                sb.setLength(0);
                sb.append("delete hr_multimedia_file where id=").append(id);
                try {
                    dao.update(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                b = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }
    
    
    /**   
     * @Title: deleteMultimediaFile   
     * @Description:  删除指定guid的记录  
     * @param @param dbflag
     * @param @param mainguid
     * @param @param childguid
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean deleteMultimediaFile(String dbflag,String mainguid,String childguid,
            String nbase,String a0100)
    {
        boolean b= false;
        if ("".equals(mainguid) && "".equals(childguid)) {
            return b;
        }

        try {
            if (this.RootDir==null || "".equals(this.RootDir)) {
                initParam(false);
            }
            StringBuffer sb = new StringBuffer();
            String key = "";
            RowSet frowset;
            String referenceFileNames = getReferenceFileNames(mainguid,childguid,
                    dbflag,nbase,a0100);                
            sb.append("select * from hr_multimedia_file");
            sb.append(" where 1=1  ");
            if (!"".equals(mainguid)&&"".equals(childguid)) {
                sb.append(" and mainguid ='").append(mainguid).append("' and nullif(childguid,'') is null");
            }
            
            // 删除子集
            if ("".equals(mainguid)&&!"".equals(childguid)) {
                sb.append(" and childguid ='").append(childguid).append("' and nullif(mainguid,'') is null");
            }
            
            if (StringUtils.isNotEmpty(mainguid) && StringUtils.isNotEmpty(childguid)) {
                sb.append(" and mainguid ='").append(mainguid).append("' and childguid='").append(childguid).append("'");
            }
            
            if ("A".equals(this.dbFlag)) {
                sb.append(" and upper(nbase) ='").append(nbase.toUpperCase()).append("'");
            }
            
            sb.append(" and a0100='").append(a0100).append("'");
            if (!"".equals(this.dbFlag)) {
                sb.append(" and dbflag='").append(dbflag).append("'");
            }
            if(this.I9999!=0&&this.I9999!=-1){//如果i9999不是0或者-1需要根据i9999值删除
            	sb.append(" and id='").append(this.I9999).append("'");
            }
            key=mainguid+"_"+childguid+"_"+nbase+"_"+a0100+"_"+dbflag;
            frowset = dao.search(sb.toString());
            while (frowset.next()) {
                if (this.RootDir==null || "".equals(this.RootDir)) {
                    initParam();//抛错
                }
                
                String filename = frowset.getString("filename");// guid不会重复，以此判断
                String topic  = frowset.getString("topic");//标题
                String id = frowset.getString("id");
                if (StringUtils.isNotEmpty(filename) && referenceFileNames.indexOf(filename) <0) {// 删除文件
                    String path = frowset.getString("path");
                    deleteFile(path);
                }
                // 删除记录
                sb.setLength(0);
                sb.append(" delete hr_multimedia_file where id=").append(id);
                try {
                    dao.update(sb.toString());
                    this.topicMap.put(key, topic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                b = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }
    

    /**   
     * @Title: deleteMultimediaFileByA0100   
     * @Description: 按A0100删除附件  
     * @param @param dbflag
     * @param @param setid
     * @param @param nbase
     * @param @param a0100
     * @param @param i9999
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean deleteMultimediaFileByA0100(String dbflag,String setid,String nbase,String a0100,int i9999)
    {
        boolean b= false;
        ArrayList list = new ArrayList();
        try {       
            if (i9999==0){
                if ("A".equals(dbflag)) {
                    setid="A01";
                }
            }
            if (setid ==null || "".equals(setid)) {
                return b;
            }

            LazyDynaBean rec = new LazyDynaBean();
            rec.set("dbflag", dbflag);
            rec.set("setid", setid);
            if (!"A".equals(dbflag)) {
                rec.set("nbase", "");
            } else {
                rec.set("nbase", nbase);
            }
            rec.set("a0100", a0100);
            rec.set("i9999", String.valueOf(i9999));
            list.add(rec);    
            deleteAllMultimediaFile(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  b;
    }
    /**   
     * @Title: getTypeList   
     * @Description: 删除指定某人的主集或子集附件记录   
     * @param @param dao
     * @param @param kind
     * @param @param a0100
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getTypeList(ContentDAO dao,String kind,String a0100)
    {
        ArrayList retlist = new ArrayList();
        String sql = "";
        RowSet frowset;
        try
        {
            if("0".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录
                String e01a1 = a0100;
                sql = "select e01a1 from k00 where e01a1='"+e01a1+"' and upper(flag)='K'";
                frowset = dao.search(sql);
                if(!frowset.next()){
                    if(this.userView.hasTheMediaSet("K")){
                        CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
                        retlist.add(cd);
                    }
                }
            }
            if("9".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录 //基准岗位分类和岗位分类是一样的
                String h0100 = a0100;
                sql = "select h0100 from H00 where h0100='"+h0100+"' and upper(flag)='K'";
                frowset = dao.search(sql);
                if(!frowset.next()){
                    if(this.userView.hasTheMediaSet("K")){
                        CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
                        retlist.add(cd);
                    }
                }
            }
            if("6".equals(kind))// 人员
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=1 order by id";//zhangcq 2016-4-18 分类统一按ID排序
                
            }else if("0".equals(kind))// 职位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=3 order by id";
                
            }else if("9".equals(kind))// 基准岗位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=4 order by id";
                
            }else  // 单位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=2 order by id";          
            }
            frowset = dao.search(sql);
            while(frowset.next())
            {
                String flag = frowset.getString("flag");
                if(this.userView.isSuper_admin())
                {
                    String datavalue = frowset.getString("sortname");
                    CommonData cd = new CommonData(flag,datavalue);
                    retlist.add(cd);
                }else{
//                  if(this.checkMediaPriv(dao,flag))
//                  String id = this.frowset.getString("id");
//                  if(this.userView.isHaveResource(IResourceConstant.MEDIA_EMP,id))    
                    if(this.userView.hasTheMediaSet(flag))
                    {
                        String datavalue = frowset.getString("sortname");
                        CommonData cd = new CommonData(flag,datavalue);
                        retlist.add(cd);
                    }
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return retlist;
    }
    
    /**
     * @Title: getPowerTypeList   
     * @Description: 查询指定某人，根据此人权限获取分类 
     * @param dao
     * @param kind
     * @param a0100
     * @return ArrayList 
     * @author:liuyang 2014 6-16
     */
    public ArrayList getPowerTypeList(ContentDAO dao,String kind,String a0100)
    {
        ArrayList retlist = new ArrayList();
        String sql = "";
        RowSet frowset = null;
        try
        {
            if("0".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录
                String e01a1 = a0100;
                sql = "select e01a1 from k00 where e01a1='"+e01a1+"' and upper(flag)='K'";
                frowset = dao.search(sql);
                if(!frowset.next()){
                    if(this.userView.hasTheMediaSet("K")){
                        CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
                        retlist.add(cd);
                    }
                }
            }
            if("9".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录 //基准岗位分类和岗位分类是一样的
                String h0100 = a0100;
                sql = "select h0100 from H00 where h0100='"+h0100+"' and upper(flag)='K'";
                frowset = dao.search(sql);
                if(!frowset.next()){
                    if(this.userView.hasTheMediaSet("K")){
                        CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
                        retlist.add(cd);
                    }
                }
            }
            if("6".equals(kind))// 人员
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=1";
                
            }else if("0".equals(kind))// 职位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=3";
                
            }else if("9".equals(kind))// 基准岗位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=4";
                
            }else  // 单位
            {
                sql = "select id,flag,sortname from MediaSort where dbflag=2";          
            }
            //执行查询语句
            frowset = dao.search(sql);
            while(frowset.next())
            {
                String flag = frowset.getString("flag");
                //判断是否是超级管理员权限
                if(this.userView.isSuper_admin())
                {
                    retlist.add(flag);
                }else{
                	//判断权限取分类
                    if(this.userView.hasTheMediaSet(flag))
                    {
                        retlist.add(flag);
                    }
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
    		try {
    			if(frowset != null)
    			{
    				frowset.close();
    			}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        
        return retlist;
    }
    
    public boolean checkMediaPriv(ContentDAO dao,String flag)
    {
        RowSet rs;
        boolean ret = false;
        String mediapriv = "";
         int status =  0 ;
         StringBuffer sb = new StringBuffer();
         sb.append(" select * from t_sys_function_priv where id = '"+this.userView.getUserName().toLowerCase()+"'");     
         try
         {
             rs = dao.search(sb.toString());
             while(rs.next())
             {
                 mediapriv = rs.getString("mediapriv");
             }
             if(!(mediapriv==null || "".equals(mediapriv)|| ",".equals(mediapriv)))
             {
                 String arr[] = mediapriv.split(",");
                 for(int i=0;i<arr.length;i++)
                 {
                     if(arr[i].equalsIgnoreCase(flag))
                     {
                         ret = true;
                         break;
                     }
                 }
             }
     
         }catch(Exception ee){
           ee.printStackTrace();
         }
        return ret;
    }

    
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     */  
    public boolean deleteFile(String sPath) {  
        boolean flag = false;  
        try {
        	if (StringUtils.isNotEmpty(sPath)) {  
        		VfsService.deleteFile(this.userView.getUserName(), sPath);
        		flag = true;  
        	}  
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return flag;  
    }  
    
    /** 
     * 删除目录（文件夹）以及目录下的文件 
     * @param   sPath 被删除目录的文件路径 
     * @return  目录删除成功返回true，否则返回false 
     */  
    public boolean deleteDirectory(String sPath) {  
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
        if (!sPath.endsWith(File.separator)) {  
            sPath = sPath + File.separator;  
        }  
        File dirFile = new File(sPath);  
        //如果dir对应的文件不存在，或者不是一个目录，则退出  
        if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        }  
        boolean flag = true;  
        //删除文件夹下的所有文件(包括子目录)  
        File[] files = dirFile.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            //删除子文件  
            if (files[i].isFile()) {  
                flag = deleteFile(files[i].getAbsolutePath());  
                if (!flag) {
                    break;
                }
            } //删除子目录  
            else {  
                flag = deleteDirectory(files[i].getAbsolutePath());  
                if (!flag) {
                    break;
                }
            }  
        }  
        if (!flag) {
            return false;
        }
        //删除当前目录  
        if (dirFile.delete()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  
    
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public void copyFile(String oldPath, String newPath) throws GeneralException { 
       InputStream inStream = null;
       FileOutputStream fs = null;
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //文件存在时 
               inStream = new FileInputStream(oldPath); //读入原文件 
               fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   fs.write(buffer, 0, byteread); 
               } 
           } 
       } 
       catch (Exception e) { 
           e.printStackTrace(); 
           throw new GeneralException("","复制文件到文件存放目录失败，请联系管理员！" + e.getMessage(),"","");
       } finally {
           PubFunc.closeIoResource(inStream);
           PubFunc.closeIoResource(fs);
       }

   } 
 
	public String downloadFile(String mediaid) throws GeneralException {
		String newfileName = "";
		RowSet frowset = null;
		try {
			initParam();
			try {
				Integer.parseInt(mediaid);
			} catch (Exception e) {
				return newfileName;
			}

			String sql = "select * from hr_multimedia_file where id =" + mediaid;
			frowset = dao.search(sql);
			if (frowset.next()) {
				String path = frowset.getString("path");
				String oldfilename = frowset.getString("srcfilename");

				if (StringUtils.isEmpty(path)) {
                    throw new GeneralException("多媒体附件不存在!");
                }
				
				newfileName = path;
				this.DestFileName = oldfilename;// 回传用
			}

		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(frowset);
		}
		
		return newfileName;
	}
        
    public synchronized String doSort(String type,String mediaid)throws GeneralException {    
        String str="";
        try{
            if (mediaid==null || "".equals(mediaid)) {
                return str;
            }
            RowSet frowset;
            String sql = "select * from hr_multimedia_file where id ="+mediaid;             
            frowset =dao.search(sql);
            if(frowset.next()){
                String mainguid= frowset.getString("mainguid");
                String childguid= frowset.getString("childguid");
                String nbase= frowset.getString("nbase");
                String a0100= frowset.getString("a0100");
                String dbflag= frowset.getString("dbflag");
                String displayorder= frowset.getString("displayorder");
                if (childguid==null) {
                    childguid="";
                }
                if (displayorder==null) {
                    displayorder = mainguid;
                }
                
                StringBuffer sb = new StringBuffer();
                sb.append("select * from hr_multimedia_file");                
                sb.append(" where mainguid ='").append(mainguid).append("'");
                if ("".equals(childguid)){
                    sb.append(" and (childguid ='").append("' or childguid is null )");  
                }
                else {
                    sb.append(" and childguid ='").append(childguid).append("'");
                }
                if ("A".equals(dbflag)){          
                    sb.append(" and upper(nbase) ='").append(nbase.toUpperCase()).append("'");
                }     
                sb.append(" and a0100='").append(a0100).append("'");
                sb.append(" and dbflag='").append(dbflag).append("'");
                if("sort_up".equals(type)){                     
                    sb.append(" and displayorder<").append(displayorder);
                    sb.append(" order by displayorder desc");
                }
                else {
                    sb.append(" and displayorder>").append(displayorder);
                    sb.append(" order by displayorder");                    
                    
                }
                frowset =dao.search(sb.toString());
                if(frowset.next()){                    
                    String newid= frowset.getString("id");    
                    String newdisplayorder= frowset.getString("displayorder");
                    
                    sql = "update hr_multimedia_file set displayorder="
                        +newdisplayorder+" where id="+mediaid;
                        
                    if(dao.update(sql)>0){
                        sql = "update hr_multimedia_file set displayorder="
                               +displayorder+" where id="+newid;                            
                            dao.update(sql);                     
                    }
                }
            }else{
                str="";  
            }
   
        }catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
        return str;
    }
    
    public long getFileSizes(File f) throws Exception{//取得文件大小
        long s=0;
        FileInputStream fis = null;
        try{
            if (f.exists()) {
                fis = new FileInputStream(f);
               s= fis.available();
            } else {
               ;
            }   
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            PubFunc.closeIoResource(fis);
        }
        return s;
    }
    
    public boolean isWindows() //是windows
    {
        boolean b=true;    
        if   ("\\".equals(File.separator))  {
           b=true; 
        }   else  if  ("/".equals(File.separator))  {
          b=false;
        }        
        return b;    
    }
    public String getDbFlag() {
        return dbFlag;
    }

    public void setDbFlag(String dbflag) {
        this.dbFlag = dbflag;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getNbase() {
        return Nbase;
    }

    public void setNbase(String nbase) {
        Nbase = nbase;
    }

    public String getA0100() {
        return A0100;
    }

    public void setA0100(String a0100) {
        A0100 = a0100;
    }

    public int getI9999() {
        return I9999;
    }

    public void setI9999(int i9999) {
        I9999 = i9999;
    }

    public String getMainGuid() {
        return MainGuid;
    }

    public void setMainGuid(String mainGuid) {
        MainGuid = mainGuid;
    }

    public String getChildGuid() {
        return ChildGuid;
    }

    public void setChildGuid(String childGuid) {
        ChildGuid = childGuid;
    }

    public boolean isMainSet() {
        if(this.setId==null||"".equals(setId)) {
            return false;
        }
        if("01".equals(this.setId.substring(1))) {
            return true;
        } else {
            return false;
        }
    }

    public String getDestFileName() {
        return DestFileName;
    }

    public void setDestFileName(String destFileName) {
        DestFileName = destFileName;
    }

    public File inputstreamtofile(InputStream ins, String fileName) {
        if(ins == null || StringUtils.isEmpty(fileName)) {
            return null;
        }
        
        String tmpPath = System.getProperty("java.io.tmpdir");
        if(!tmpPath.endsWith(File.separator)) {
            tmpPath += File.separator;
        }
        
        File file = new File(tmpPath + fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = ins.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(os);
            PubFunc.closeResource(ins);
        }
        
        return file;
    }
    
    public void setParam(String dbflag,String nbase,String setid, String a0100,int i9999 ) {
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);

        this.setId = setid;
        this.Nbase = nbase;
        this.A0100 = a0100;
        this.I9999 =i9999;
        this.dbFlag=dbflag;
        if ("A".equals(this.dbFlag)){
            keyField ="A0100";
        }
        InitGuidKeyValue();
    }
    
    public HashMap saveApproveMedia(String setid,FormFile file) throws GeneralException{
    	    HashMap fileMap = new HashMap();
        String fileExt="";
        String srcFilename="";
        if (file !=null){
            initParam();
            srcFilename = file.getFileName();
            long size = file.getFileSize();
            if (srcFilename.lastIndexOf(".") > 0) {
                fileExt = srcFilename.substring(srcFilename.lastIndexOf("."));// 扩展名
            }
        
            // 保存
            InputStream streamIn=null;
            try {
                streamIn = file.getInputStream();
                String userName = this.userView.getUserName();
                VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
                VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
                VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
                String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, getGuidKey(this.Nbase + "a01", true), 
                		streamIn, srcFilename, "", false);	
                fileMap.put("srcFileName", srcFilename);
                fileMap.put("fileName", DestFileName);
                fileMap.put("path", fieldId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GeneralException("保存文件失败！" + e.getMessage() + " 请联系系统管理员！");
            }finally
            {
                PubFunc.closeIoResource(streamIn);
            }   
    
        }
		return fileMap;        
    	
    }
    /**
     * 获取对应的代码项的编号
     * @param codesetid 代码类分类编号
     * @param itemName 代码项对应的名称
     * @return 代码项编号
     */
    public String getItemid(String codesetid, String itemName) {
        String itemid = "";
        
        if("0".equalsIgnoreCase(codesetid)) {
            return itemid;
        }
        
        ArrayList<CodeItem> codeItemList = AdminCode.getCodeItemList(codesetid);
        
        for(int i = 0; i < codeItemList.size(); i++) {
            CodeItem item = codeItemList.get(i);
            String itemname = item.getCodename();
            if(itemname.equalsIgnoreCase(itemName)) {
                itemid = item.getCodeitem();
                break;
            }
        }
        
        return itemid;
    }
    
    /**
     * 获取文件根目录
     * @return
     */
    public String getRootDir(){
    	 ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
         String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
         if(StringUtils.isNotEmpty(rootDir)) {
             rootDir = rootDir.replace("\\",File.separator);
         }
         File file = new File(rootDir);
         //判断路径是否有效
         if(!file.isDirectory()) {
             if(!file.mkdir()) {
                 rootDir = "";
             }
         }
		return rootDir;
    }
}
