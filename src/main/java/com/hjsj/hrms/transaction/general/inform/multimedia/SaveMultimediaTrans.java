package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveMultimediaTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:41:09</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class SaveMultimediaTrans extends IBusiness {

	String para_maxsize = null;
	public  void execute()throws GeneralException
	{
	    try{
            String dbflag = (String)this.getFormHM().get("dbflag");
            String nbase = (String)this.getFormHM().get("nbase");
            String A0100 = (String)this.getFormHM().get("a0100");
            String I9999 = (String)this.getFormHM().get("i9999");
            String setid = (String)this.getFormHM().get("setid");
            String editflag = (String)this.getFormHM().get("editflag");
            
            
            if (I9999==null || I9999.length()<1)  I9999="0";            
    		String filepath = (String)this.getFormHM().get("filepath");
    		FormFile file=(FormFile)getFormHM().get("picturefile");
    		
            if(0 == file.getFileSize() 
                    && ((StringUtils.isNotEmpty(filepath) && "true".equals(editflag))
                            || !"true".equals(editflag))) 
                throw new GeneralException("", "上传的文件大小为0Byte，不允许上传！", "", "");
            
            MultiMediaBo multiMediaBo = null;
            //信息审核：信息变动表和子集数据对应改为用guidkey对应，因此i9999有可能取到的是guidkey，因此加上是否是数字的校验
            if(!StringUtils.isNumeric(I9999)) {
            	if(I9999.indexOf("-") == -1) {
            		I9999 = PubFunc.validateNum(I9999,4)?I9999:PubFunc.decrypt(I9999);
            		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
          				dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
            	} else {
            		multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
          				dbflag,nbase,setid,A0100,I9999);
            	}
            } else {
            	multiMediaBo = new MultiMediaBo(this.frameconn,this.userView,
          			dbflag,nbase,setid,A0100,Integer.parseInt(I9999));
            }
            multiMediaBo.initParam();
            long  size= (long)file.getFileSize();
    		//员工管理，记录录入，上传文件需过滤  jingq  add 2014.10.30
    		boolean flag = false;
    		flag = com.hjsj.hrms.utils.FileTypeUtil.isFileTypeEqual(file);
    		if(flag){
	    		if ("".equals(filepath)) 
	    			file=null;
	            
	    		if(file != null) {
	    			String fileName = file.getFileName();
	    			String ext = fileName.substring(fileName.lastIndexOf("."));
	    			String fileType = "'.html','.htm','.php','.php2','.php3','.php4','.php5','.phtml','.pwml',"
			   	 			+ "'.inc','.asp','.aspx','.ascx','.jsp','.cfm','.cfc','.pl','.bat','.exe','.com','.dll',"
			   	 			+ "'.vbs','.js','.reg','.cgi','.htaccess','.asis','.sh','.shtml','.shtm','.phtm'";
	    			if(fileType.contains("'" + ext.toLowerCase() + "'"))
			   	 		throw new GeneralException("", "不允许上传" + ext + "类型文件！", "", "");
			   	 	
	    		}
	    		
		        String canedit = (String)this.getFormHM().get("canedit");
		        if("selfedit".equals(canedit)){
		        	    String sequence = (String)this.getFormHM().get("sequence");
		            	multiMediaApproveSave(nbase,A0100,setid,I9999,sequence,file);
		            	return;
		        }
	     
	        	if ("true".equals(editflag)){//修改		    
	    		    multiMediaBo.saveMultimediaFile(this.getFormHM(), file,true);    
	    		} else {
	    		    multiMediaBo.saveMultimediaFile(this.getFormHM(), file,false);
	    		}
    		} else {
    			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.fileuploaderror")));
    		}
    		
    }catch(Exception e){
        e.printStackTrace();
        throw GeneralExceptionHandler.Handle(e);
    }
	}

	private void multiMediaApproveSave(String nbase,String A0100,String setid,String I9999,String sequence,FormFile file){
		try {
			if(file==null)
				return;
			this.getFormHM().put("modified", "false");
			String state = (String)this.getFormHM().get("state");
			if((state==null || state.length()<1) && !"A01".equals(setid))
				this.getFormHM().put("modified", "true");
			String filetype = (String)this.getFormHM().get("filetype");
	        String filetitle = (String)this.getFormHM().get("filetitle");
	        String description = (String)this.getFormHM().get("description");
	        MultiMediaBo multibo = new MultiMediaBo(frameconn, userView);
	        multibo.setA0100(A0100);
	        multibo.setNbase(nbase);
	        HashMap fileInfoMap = multibo.saveApproveMedia(setid,file);
	        fileInfoMap.put("type", "new");
	        fileInfoMap.put("desc", description);
	        fileInfoMap.put("class", filetype);
	        fileInfoMap.put("topic", filetitle);
	        ArrayList fileInfo = new ArrayList();
	        fileInfo.add(fileInfoMap);
	        MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,
					this.userView, nbase, A0100);
	        
	        if("A01".equals(setid) && mysel.insertMultiMediaInfo(setid,A0100,"1",fileInfo)){
	        	    return;
	        }
	        
			if(sequence==null || sequence.length()<1 || "A01".equals(setid)){
				mysel.setFileInfo(fileInfo);
				mysel.getOtherParamList(setid,I9999);
				ArrayList sequenceList = mysel.getSequenceList();
				String mysequence ="1";
				if(sequenceList.size()>0){
					mysequence =(Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+1)+"";
				}
				
				RecordVo vo = new RecordVo(nbase+setid);
				vo.setString("a0100", A0100);
				if(!"A01".equals(setid))
					vo.setString("i9999", I9999);
				vo = new ContentDAO(this.frameconn).findByPrimaryKey(vo);
				ArrayList fieldlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				ArrayList valueFields = new ArrayList();
				FieldSet  fieldset = DataDictionary.getFieldSetVo(setid);
				mysel.saveMyselfData(nbase,A0100,fieldset,valueFields,valueFields,"update","01",I9999,mysequence);
				sequence = mysel.getInsertSequence();
				this.getFormHM().put("sequence", sequence);
				return;
			}
			
			mysel.insertMultiMediaInfo(setid,I9999,sequence,fileInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private float getFileMaxSize(){
		 ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
         String multimedia_maxsize=this.para_maxsize = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
         if ((multimedia_maxsize==null) ||("".equals(multimedia_maxsize))){                
             multimedia_maxsize="0";
         }
         float maxSize =0;
         multimedia_maxsize= multimedia_maxsize.toUpperCase();
         int k=1;
         if (multimedia_maxsize.indexOf("K")>0){
             k=1024;
         }
         else if (multimedia_maxsize.indexOf("M")>0){
             k=1024*1024;
         }
         else if (multimedia_maxsize.indexOf("G")>0){
             k=1024*1024*1024;
         }
         else if (multimedia_maxsize.indexOf("T")>0){
             k=1024*1024*1024*1024;
         } 
         multimedia_maxsize =multimedia_maxsize.replaceAll("K", "").replaceAll("M", "")
                 .replaceAll("G", "").replaceAll("T", "").replaceAll("B", "");
         if ("".equals(multimedia_maxsize)){
             multimedia_maxsize="0";
         }
         maxSize = Float.parseFloat(multimedia_maxsize);
         maxSize= maxSize*k; 
         
         return maxSize;
	}
}
