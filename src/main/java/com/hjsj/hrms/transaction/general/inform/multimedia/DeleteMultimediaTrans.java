package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DeleteMultimediaTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:41:33</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class DeleteMultimediaTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
        try {
            String dbflag = (String) this.getFormHM().get("dbflag");
            String nbase = (String) this.getFormHM().get("nbase");
            String A0100 = (String) this.getFormHM().get("a0100");
            String I9999 = (String) this.getFormHM().get("i9999");
            String setid = (String) this.getFormHM().get("setid");
            String multimediaflag = (String) this.getFormHM().get("multimediaflag");
            
            HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");  
            String ids =(String)hm.get("ids");
            String mychgs = (String)hm.get("mychgs");
            if((ids==null && mychgs==null)|| (ids.length()<1 && mychgs.length()<1))
            	    return;
            ids = decryptValue(ids);
            mychgs = decryptValue(mychgs);
            String canedit = (String) this.getFormHM().get("canedit");
            if("selfedit".equals(canedit)){
                deleteApproveMultiMedia(ids,mychgs);
            	    return;
            }
            
           // ArrayList mediainfolist = (ArrayList) this.getFormHM().get("selectedlist");           

            MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn, this.userView, 
                    dbflag, nbase, setid, A0100, Integer.parseInt(I9999));
            
            ArrayList mediainfolist = multiMediaBo.getMultimediaRecord(ids);
            if (mediainfolist == null || mediainfolist.size() == 0)
                return;
            multiMediaBo.deleteMultimediaRecord(mediainfolist);

        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
        }
			
	}
	
	private void deleteApproveMultiMedia(String realIds,String approveIds){
		try {
			this.getFormHM().put("modified", "false");
			String nbase = (String)this.getFormHM().get("nbase");
	        String A0100 = (String)this.getFormHM().get("a0100");
	        String I9999 = (String)this.getFormHM().get("i9999");
	        String setid = (String)this.getFormHM().get("setid");
			String sequence = (String)this.getFormHM().get("sequence");
			
			MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,
					this.userView, nbase, A0100);
			
			boolean hasRecord = false;
			
			//如果此条数据已有报批的修改记录，不许编辑多媒体。将状态置为预览
			hasRecord = mysel.hasRecord(setid, I9999, null, "02");
			if(hasRecord){
				this.formHM.put("canedit", "appview");
				return;
			}
			
			if("A01".equals(setid)){
				I9999 = A0100;
				sequence = "1";
				hasRecord = mysel.hasRecord(setid, I9999, sequence,null);
			}else if(sequence!=null && sequence.length()>0){
				hasRecord = true;
			}else{
				hasRecord = false;
			}
			
			String[] idArray = realIds.split(",");
			ArrayList fileInfo = new ArrayList();
			for(int i=0;i<idArray.length;i++){
				if(idArray[i].length()<1)
					continue;
				HashMap map = new HashMap();
				map.put("fileid", idArray[i]);
				map.put("type", "delete");
				fileInfo.add(map); 
			}
			
			if(!hasRecord){
				if(fileInfo.size()<1)
					return;
				mysel.getOtherParamList(setid,I9999);
				ArrayList sequenceList = mysel.getSequenceList();
				int newsequence =1;
				if(sequenceList.size()>0){
					newsequence =Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+1;
				}
				
				ContentDAO dao = new ContentDAO(this.frameconn);
				ArrayList fieldlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				RecordVo vo = new RecordVo(nbase+setid);
				vo.setString("a0100", A0100);
				if(!"A01".equals(setid))
					vo.setString("i9999", I9999);
				vo = dao.findByPrimaryKey(vo);
				
				ArrayList valueFields = new ArrayList();
				for(int i=0;i<fieldlist.size();i++){
					FieldItem item  = (FieldItem) ((FieldItem)fieldlist.get(i)).clone();
					item.setValue(vo.getString(item.getItemid().toLowerCase()));
					valueFields.add(item);
				}	
				FieldSet  fieldset = DataDictionary.getFieldSetVo(setid);
				mysel.setFileInfo(fileInfo);
				mysel.saveMyselfData(nbase,A0100,fieldset,valueFields,valueFields,"update","01",I9999,newsequence+"");
				sequence = mysel.getInsertSequence();
				this.getFormHM().put("sequence", sequence);
				this.getFormHM().put("modified", "true");
			}else{
				if(fileInfo.size()>0)
					mysel.insertMultiMediaInfo(setid, I9999, sequence, fileInfo);
			    String appids = approveIds.replace(",","").trim();
			    if(appids.length()>0)
			    		mysel.deleteMultiMediaInfo(setid,I9999,sequence,approveIds);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
		
	private String decryptValue(String values) {
		if(StringUtils.isEmpty(values))
			return values;
		
		String[] chgs = values.split(",");
		values = "";
        for (int i = 0; i < chgs.length; i++) {
        	String chg = chgs[i];
        	if(StringUtils.isEmpty(chg))
        		continue;
        	
        	String[] chgIds = chg.split(":");
        	if(chgIds.length == 2) {
        	    if("new".equalsIgnoreCase(chgIds[1])) {
        	    	values += chgIds[0] + ",";
        	    } else {
        	    	values += PubFunc.decrypt(chgIds[0]) + ",";
        	    }
        	} else {
        		values += PubFunc.decrypt(chg) + ",";
        	}
        }
        
        values = "," + values;
        return values;
	}
}
