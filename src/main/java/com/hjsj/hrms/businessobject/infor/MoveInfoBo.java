package com.hjsj.hrms.businessobject.infor;

import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
 /**
 * <p>Title:MoveInfoBo.java</p>
 * <p>Description>:移库，广东中烟先用，计划 人员移库也调用这个类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-16 下午02:43:00</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class MoveInfoBo {
    private Connection conn=null;
    private DbNameBo bo=null;
    private UserView userView;
    private ContentDAO dao;
    private Sys_Oth_Parameter sysbo;
    private DbWizard dbWizard;
    //是否需要检查编制 广东中烟 -模板提交 不需要
    private boolean bNeedScanFormation =true;
    
    //是否检查不能重复指标
    private boolean bNeedCheckOnly =true;
    //判断超编 是否有错误要抛出？
    private boolean bHaveErrorToThrow=false;
    
    public MoveInfoBo(Connection conn,UserView userview) {
        this.conn=conn;
        this.userView=userview;
        dao=new ContentDAO(this.conn);
        bo = new DbNameBo(this.conn,userview);
        sysbo=new Sys_Oth_Parameter(this.conn);
        dbWizard = new DbWizard(this.conn);
        addGuidKey();//A01增加guidkey
    }
   
    
    public String MoveEmployees(String userbase,String touserbase,ArrayList moveinfodata)throws GeneralException
    {
        bHaveErrorToThrow=false;
        String movingInfo = ""; 
        StringBuffer strsql=new StringBuffer();
        StructureExecSqlString sql=new StructureExecSqlString(); 
        List fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
        ArrayList msglist = new ArrayList();
        msglist.clear();
        try{
              String checkonlyinfo = "";
              StringBuffer fieldstr=new StringBuffer();
              if(moveinfodata.isEmpty()){ 
                  return "";
              }  
               ArrayList listvalue = new ArrayList();
    
                  // 判断超编
              if (bNeedScanFormation){
                  ScanFormationBo scanFormationBo = new ScanFormationBo(this.conn, this.userView);
                  if (scanFormationBo.doScan()) {
                      if ((!scanFormationBo.needDoScan(userbase + ",", "All")) 
                              && scanFormationBo.needDoScan(touserbase + ",", "All")) {// 从非编制库移到编制库才检查
                          ArrayList beanList = new ArrayList();
                          ArrayList nbaseA0100List = new ArrayList();
                          for (int i = 0; i < moveinfodata.size(); i++) {
                              LazyDynaBean rec = (LazyDynaBean) moveinfodata.get(i);
                              String A0100 = rec.get("a0100").toString();
                              nbaseA0100List.add(userbase + "`" + A0100 + "`" + touserbase);
                              beanList = scanFormationBo.getMoveAddPersonData(nbaseA0100List);
                          }                               
                          scanFormationBo.execDate2TmpTable(beanList);
                          String mess = scanFormationBo.isOverstaffs();
                          if (!"ok".equals(mess)) {
                              if ("warn".equals(scanFormationBo.getMode())) {
                                  msglist.add(mess);
                              } else {
                                  bHaveErrorToThrow= true;
                                  msglist.add(mess);
                                 // throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
                              }
                          }
                          
                      }
                  }    
              } 
                  
              for(int i=0;i<moveinfodata.size();i++){             
                  LazyDynaBean rec=(LazyDynaBean)moveinfodata.get(i);
                  String A0100=rec.get("a0100").toString();
                  String destBase=rec.get("touserbase").toString();
                  if (destBase!=null && !"".equals(destBase)){
                      touserbase=destBase;
                  }
                  
                  String toTable=touserbase+"A01";
                  
                  if (bNeedCheckOnly){
                      String chk = bo.checkOnlyName(A0100,userbase,touserbase);                   
                      if(!"true".equalsIgnoreCase(chk)){
                          if("".equalsIgnoreCase(checkonlyinfo)){
                              checkonlyinfo = "源库中的"+chk+"不允许移库";
                          }else{
                              checkonlyinfo += "\n"+"源库中的"+chk+"不允许移库";
                          }
                          continue;
                      }
                  }
                  
                
                  String toA0100 = getToA0100(conn, A0100, toTable);      
                  if(!fieldsetlist.isEmpty()) {
                      for(int j=0;j<fieldsetlist.size();j++)
                      {
                          FieldSet fieldset=(FieldSet)fieldsetlist.get(j);
                          if(!"a00".equalsIgnoreCase(fieldset.getFieldsetid()))
                          {
                              List fields=DataDictionary.getFieldList(fieldset.getFieldsetid(),
                                      Constant.USED_FIELD_SET);
                              fieldstr.delete(0,fieldstr.length());
                              if(!fields.isEmpty())
                              {
                                for(int n=0;n<fields.size();n++)
                                {
                                  FieldItem fielditem=(FieldItem)fields.get(n);
                                  fieldstr.append("," + fielditem.getItemid());
                                }
                               }


                              // 为主集添加人员唯一标识
                              if ("a01".equalsIgnoreCase(fieldset.getFieldsetid())) {
                                  fieldstr.append(",GUIDKEY");
                              }
                              strsql.setLength(0);
                              strsql=sql.transferInformation(userbase+ fieldset.getFieldsetid(),touserbase + fieldset.getFieldsetid(),
                                      A0100,toA0100,fieldset.getFieldsetid(),fieldstr.toString(),this.conn);
                              movingInfo = fieldset.getFieldsetdesc()+"("+touserbase + fieldset.getFieldsetid()+")";
                              dao.update(strsql.toString());

                           }
                      }
                  }
               
     
                   fieldstr.delete(0,fieldstr.length());
                   fieldstr.append(",title,ole,flag,state,id,ext");
                   strsql=sql.transferInformation(userbase+ "A00",touserbase + "A00",
                           A0100,toA0100,"A00",fieldstr.toString(),this.conn);
                   movingInfo = "多媒体子集("+touserbase + "A00)"; 
                   dao.update(strsql.toString()); 
     
                   listvalue.clear();
                   ArrayList list = new ArrayList();
                   list.add(toA0100);
                   list.add(touserbase);
                   list.add(A0100);
                   list.add(userbase);
                   listvalue.add(list);
                   
                   //复制子集
                   if(listvalue!=null&&listvalue.size()>0){
                       movingInfo = "";
                       bo.updateSalaryPre(listvalue);
                   }
                  //删除源库
                   DeleteInfo(rec,userbase,touserbase); 
                   
              }
    
             if (bNeedCheckOnly) {
                if (!"".equalsIgnoreCase(checkonlyinfo)) {
                    bHaveErrorToThrow = true;
                    msglist.add(checkonlyinfo);
                }
             }
    
            String mess = "";
            if (msglist.size() > 0) {
                StringBuffer msg = new StringBuffer();
                for (int i = 0; i < msglist.size(); i++) {
                    if (msglist.size() > 1) {
                        msg.append((i + 1) + ":" + msglist.get(i) + "\\n");
                    } else {
                        msg.append(msglist.get(i));
                    }
                }
                mess = msg.toString();
            } else {
                mess = "";
            }
               return mess;   
         
        }
        catch(Exception e)   {
        	e.printStackTrace();   
        	String s = "";
        	if(e instanceof GeneralException) {
                s = ((GeneralException)e).getErrorDescription();
            } else {
                s = e.getMessage();
            }
        	
            String sEx = "";  
            if("".equals(movingInfo)){
                sEx= s;
            }else{
                sEx = "复制数据到“"+movingInfo+"”时出现错误！"+s;
            }
            throw GeneralExceptionHandler.Handle(new Exception(sEx));
        }   
    }   
    
    /**
     * @param stmt
     * @param A0100
     * @param toTable
     * @return
     * @throws SQLException
     */
    //获得移库的目标id号
    private synchronized String getToA0100(Connection conn, String A0100, String toTable) throws SQLException {
        String toA0100="";
        String tempNumber;
        String tempsql =
            "select A0100 from "
                + toTable
                + " where A0100='"
                + A0100
                + "'";
         ResultSet rs = null;
         ResultSet idRs=null;
          try{
                rs = dao.search(tempsql);
                if (rs.next()) {
                    String strsql = "select max(A0100) as a0100 from " + toTable + " order by A0100";
                    idRs=dao.search(strsql);
                    int userPlace;
                    if (idRs.next()) {
                        userPlace =Integer.parseInt(idRs.getString("a0100")) + 1;
                    } else {
                        userPlace = 1;
                    }
                
                    tempNumber = Integer.toString(userPlace);
                    for (int n = 0; n < 8 - (Integer.toString(userPlace)).length(); n++) {
                        tempNumber = "0" + tempNumber;
                    }
                    //tempNumber = getUserId(toTable);
                }   
                else {
                    tempNumber = A0100;
                }
                toA0100=tempNumber;
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally{
            com.hjsj.hrms.utils.PubFunc.closeResource(rs);
            com.hjsj.hrms.utils.PubFunc.closeResource(idRs);
        }
        return toA0100;
    }
    
    private void addGuidKey() {
        try {        
            RowSet frowset=null;
            frowset = dao.search("select pre from dbname");
             while (frowset.next()) {
                String touserbase = frowset.getString("pre");
                    if (!dbWizard.isExistField(touserbase + "A01", "GUIDKEY", false)) {
                        Table table = new Table(touserbase + "A01");
                        Field field = new Field("GUIDKEY","人员唯一标识");
                        field.setDatatype(DataType.STRING);
                        field.setKeyable(false);
                        field.setLength(38);
                        table.addField(field);
                        dbWizard.addColumns(table);
                    
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void DeleteInfo(LazyDynaBean rec,String userbase,String touserbase) throws GeneralException 
    {
        List infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,
                Constant.EMPLOY_FIELD_SET);
        StringBuffer  deletesql=new StringBuffer();
        String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
        //兼职岗位占编 1：占编   
        String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
        String ps_parttime="0";
        if("true".equals(partflag)&&"1".equals(takeup_quota)){
            ps_parttime="1";
        }
        String pos_ctrl=sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
  
        String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");//兼职子集
        PosparameXML pos = new PosparameXML(this.conn); 
        String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
        dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
        String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos").toLowerCase();//兼任兼职
        /**任免标识字段*/
        String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint").toLowerCase();
        String pos_value="";
        FieldItem pos_field_item = DataDictionary.getFieldItem(pos_field);
        FieldItem appoint_field_item = DataDictionary.getFieldItem(appoint_field);
        try
         {
            String pos_id="";
            if (rec.get("e01a1")!=null) {
                pos_id =(String)rec.get("A0100".toString());
            }
            if (pos_id==null) {
                pos_id="";
            }
           // String pos_id=(String) (rec.get("e01a1")!=null?rec.get("e01a1"):"");

            //记录删除日志
            StringBuffer sql=new StringBuffer();
            boolean flag = dbWizard.isExistField("a01log", "guidkey",false);  
            sql.append("insert into a01log(a0000,a0100,b0110,e0122,e01a1,a0101,"+(flag?"guidkey,":"")+"createusername,sbase,dbase)");
            sql.append("(select a0000,a0100,b0110,e0122,e01a1,a0101,"+(flag?"guidkey,":"")+"'"+this.userView.getUserName()
                 +"','"+userbase+"','"+touserbase+"' from ");
   
            sql.append(userbase+"A01 where a0100='"+rec.get("a0100").toString()+"'");
            sql.append(")");                            
            dao.insert(sql.toString(), new ArrayList());
            //删除指标集          
            for(int j=0;j<infoSetList.size();j++)
            { 
                FieldSet fieldset=(FieldSet)infoSetList.get(j);
                if("true".equals(pos_ctrl)&&"1".equals(ps_parttime))
                {
                    if(setid.equals(fieldset.getFieldsetid())){
                        if(dbs.toUpperCase().indexOf(userbase.toUpperCase())!=-1){
                            if(pos_field.length()>0&&appoint_field.length()>0&&pos_field_item!=null&& "1".equals(pos_field_item.getUseflag())&&appoint_field_item!=null&& "1".equals(appoint_field_item.getUseflag())){
                               RowSet rs = dao.search("select distinct "+pos_field
                                        +" from "+userbase+setid+" where a0100='"
                                        +rec.get("a0100").toString()+"' and "+appoint_field+"='0'");
                                while(rs.next()){
                                    pos_value=rs.getString(pos_field);
                                    bo.dateLinkage(pos_value, 1, "-");
                                }
                            }
                        }
                    }
                }
                
                deletesql.setLength(0);                 
                deletesql.append("delete from ");
                deletesql.append(userbase);
                deletesql.append(fieldset.getFieldsetid());
                deletesql.append(" where a0100='");
                deletesql.append(rec.get("a0100").toString());
                deletesql.append("'");
                dao.update(deletesql.toString());   
               
             }  
            if(dbs.toUpperCase().indexOf(userbase.toUpperCase())!=-1){ 
                bo.dateLinkage("",pos_id,1,"-");                       
            }
 
            dao.update("delete from  t_hr_mydata_chg  where Upper(NBase)='"+userbase.toUpperCase()
                    +"' and A0100='"+rec.get("a0100").toString()+"'");
         }
         catch(SQLException sqle)
         {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
         }
    }

    public boolean isBNeedScanFormation() {
        return bNeedScanFormation;
    }



    public void setBNeedScanFormation(boolean needScanFormation) {
        bNeedScanFormation = needScanFormation;
    }



    public boolean isBNeedCheckOnly() {
        return bNeedCheckOnly;
    }



    public void setBNeedCheckOnly(boolean needCheckOnly) {
        bNeedCheckOnly = needCheckOnly;
    }



    public boolean isBHaveErrorToThrow() {
        return bHaveErrorToThrow;
    }



    public void setBHaveErrorToThrow(boolean haveErrorToThrow) {
        bHaveErrorToThrow = haveErrorToThrow;
    }    
    
    
}
