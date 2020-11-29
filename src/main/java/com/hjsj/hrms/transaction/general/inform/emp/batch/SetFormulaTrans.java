package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SetFormulaTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
        String setname = (String)reqhm.get("setname");
        setname=setname!=null&&setname.trim().length()>0?setname:"";
        String unit_type = (String)reqhm.get("unit_type");//1,2,3,4,5 :合同,人员，单位,职位,培训费用
        unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"2";
        reqhm.remove("unit_type");
        
        String infor = (String)reqhm.get("infor");//1,2,3,4,5 :人员，单位,职位,合同,培训费用
        infor=infor!=null&&infor.trim().length()>0?infor:"1";
        reqhm.remove("infor");
        String isSetId=(String)reqhm.get("setId");
        reqhm.remove("setId");
        isSetId=(String) (isSetId!=null&&isSetId.length()>0?isSetId:this.getFormHM().get("isSetId"));
        isSetId=isSetId!=null&&isSetId.length()>0?isSetId:"0";
        String infor_flag = infor;
        
        if("1".equals(infor))
            unit_type="2";
        else if("2".equals(infor)){
            unit_type="3";
        }else if("3".equals(infor)){
            unit_type="4";
        }else if("4".equals(infor)){
            unit_type="1";
            infor_flag="1";
        }else if("5".equals(infor)){
            unit_type="5";
        }
        
        TempvarBo tempvarbo = new TempvarBo();
        ArrayList fieldsetlist = tempvarbo.fieldList(this.userView,infor_flag);
        
        // 合同模块，计算时不计算主集信息
        if ("4".equals(infor)) {
            for (int i = 0; i < fieldsetlist.size(); i++) {
                CommonData data = (CommonData) fieldsetlist.get(i);
                if("A01".equalsIgnoreCase(data.getDataValue())) {
                    fieldsetlist.remove(i);
                    break;
                }
            }
        }
                
        this.getFormHM().put("fieldsetlist",fieldsetlist);
        outTable(unit_type,setname,isSetId,infor);
        this.getFormHM().put("unit_type",unit_type);
        this.getFormHM().put("infor",infor);
        this.getFormHM().put("isSetId", isSetId);
        this.getFormHM().put("setname", setname);
    }
    private void outTable(String unit_type,String setname,String isSetId, String infor) throws GeneralException{
        ArrayList dataList = new ArrayList();
        ArrayList fieldsetlist1 = new ArrayList();
        String setidList="";
        String fieldlist = "";
        if(!"5".equals(infor)){
            if("1".equals(infor)||"4".equals(infor)){
                fieldsetlist1 = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
            }else if("2".equals(infor)){
                fieldsetlist1 = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
            }else if("3".equals(infor)){
                fieldsetlist1 = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
            }
            if(fieldsetlist1!=null){
                for(int i=0;i<fieldsetlist1.size();i++){
                    FieldSet fs = (FieldSet)fieldsetlist1.get(i);
                    /*if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限
                        continue;
                    }*/
                    if("A00".equalsIgnoreCase(fs.getFieldsetid())){
                        continue;
                    }
                    CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
                    dataList.add(cd);
                }
            }
            if(dataList.size()==0){
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.batchupdate.nopriv")));
            }
            for(int i=0;i<dataList.size();i++){
                setidList+=((CommonData)dataList.get(i)).getDataValue()+",";
            }
        }
        StringBuffer tablestr = new StringBuffer();
        StringBuffer sortstr = new StringBuffer();
        tablestr.append("<div id=\"itemtable\" class=\"common_border_color\" style=\"border:1px solid;position: absolute;left: 10px;margin-top:0px;width:210px;height:320px\">");
        tablestr.append("<table width=\"100%\" border=\"0\" align=\"center\" style=\"border-collapse: collapse;\">");
        tablestr.append("<tr>");
        tablestr.append("<td width=\"20%\" align=\"center\" class=\"TableRow\" style=\"border-top:none;border-left:none;border-right:none;\" nowrap>"+ResourceFactory.getProperty("kh.field.yx")+"</td>");
        tablestr.append("<td width=\"80%\" align=\"center\" class=\"TableRow\" style=\"border-top:none;border-right:none;\" nowrap>"+ResourceFactory.getProperty("kh.field.field_n")+"</td></tr>");
        ContentDAO dao = new ContentDAO(this.frameconn);
        String sqlstr = "select fid,flag,forname,setid,itemid from HRPFormula where unit_type="+unit_type+" ";
        if("1".equals(isSetId))
            sqlstr+=" and UPPER(setid)='"+setname.toUpperCase()+"'";
        sqlstr+=" order by db_type";
        //and upper(setid)='"+setname.toUpperCase()+"'
        try {
            this.frowset=dao.search(sqlstr);
            while(this.frowset.next()){
//              liwc 业务指标不存在授权
//              String pre = this.userView.analyseFieldPriv(this.frowset.getString("itemid"));
//              pre=pre!=null&&pre.trim().length()>0?pre:"0";
//              if(!pre.equals("2"))
//                  continue;
                String fid = this.frowset.getString("fid");
                String itemid = this.frowset.getString("itemid");
                String id = fid+"_"+itemid;
                String forname = this.frowset.getString("forname");
                int flag = this.frowset.getInt("flag");
                String setid = this.frowset.getString("setid");
                if(!"5".equals(infor)&&(setidList).toUpperCase().indexOf(setid.toUpperCase())==-1){
                    continue;
                }
                if("1".equals(infor)){
                    //子集无权限
                    if((setidList).toUpperCase().indexOf(setid.toUpperCase())==-1){
                        continue;
                    }
                    
                    FieldItem item = DataDictionary.getFieldItem(itemid, setid);
                    //指标不存在或未构库
                    if(item == null || "0".equals(item.getUseflag()))
                        continue;
                    
                    //指标只读权限
                    if("0".equals(this.userView.analyseFieldPriv(itemid))) {
                        continue;
                    }
                }
                tablestr.append("<tr><td class=\"RecordRow\" align=\"center\" style=\"border-top:none;border-left:none;border-right:none;\"  onclick=\"tr_bgcolor('");
                tablestr.append(id);
                tablestr.append("');getFormula('");
                tablestr.append(fid);
                tablestr.append("','");
                tablestr.append(unit_type);
                tablestr.append("');\">");
                tablestr.append("<input type=\"checkbox\" id=\""+id+"\" name=\"");
                tablestr.append(id);
                tablestr.append("\" value=\"1\" onclick=\"setCheck(document.getElementById('"+id+"'),'formulastr');\"");
                if(flag==1){
                    tablestr.append("checked");
                }
                tablestr.append(">");
                tablestr.append("</td><td class=\"RecordRow\" style=\"border-top:none;border-right:none;\"  onclick=\"tr_bgcolor('");
                tablestr.append(id);
                tablestr.append("');getFormula('");
                tablestr.append(fid);
                tablestr.append("','");
                tablestr.append(unit_type);
                tablestr.append("');\">");
                tablestr.append(forname);
                tablestr.append("</td></tr>");
                sortstr.append(id+"::");
                sortstr.append(forname+"::");
                sortstr.append(flag+"`");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tablestr.append("</table>");
        tablestr.append("</div>");
        this.getFormHM().put("formulatable",tablestr.toString());
        this.getFormHM().put("formulastr",sortstr.toString());
    }

}
