/**   
 * @Title: ExportZipDataTrans.java 
 * @Package com.hjsj.hrms.transaction.hire.produceFile 
 * @Description: TODO
 * @author xucs
 * @date 2014-7-8 下午04:28:59 
 * @version V1.0   
*/
package com.hjsj.hrms.transaction.hire.produceFile;

import com.hjsj.hrms.businessobject.hire.EmployResumeZipBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: EXportZipDataTrans 
 * @Description: 导出应聘简历zip格式的数据（zip中包含EXCEL和每个人的涉及到的附件）
 * @author xucs 
 * @date 2014-7-8 下午04:28:59 
 *  
 */
public class ExportZipDataTrans extends IBusiness {
    
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        try{
            String nbase=(String)this.getFormHM().get("nbase");//涉及到的应聘人才库中设置的人员库
            String a0100=(String)this.getFormHM().get("a0100");//选中的要导出的人员的a0100
            String []a0100s = a0100.split("#");
            a0100 = "";
            for(int i=0;i<a0100s.length;i++)
            {
            	a0100+=PubFunc.decrypt(a0100s[i])+"#";
            }
            String number=(String)this.getFormHM().get("number");//各相关人员的志愿
            String isSelectedAll=(String)this.getFormHM().get("isSelectedAll");//是否是全选
            String resumeState=(String)this.getFormHM().get("resumeState");//简历的状态 
            String employType=(String)this.getFormHM().get("employType");// 0：业务平台 1：自助平台
            String personType=(String)this.getFormHM().get("personType");// 0:应聘库 1：人才库 4:我的收藏夹   这个导出的zip数据的时候只能是应聘库
            String order_str= (String)this.userView.getHm().get("hire_order_str");//(String)this.getFormHM().get("order_str");//排序语句
            String queryType=(String)this.getFormHM().get("queryType");//在已选状态中，是查看全部已选，还是只查看自己的已选，=0查看自己(默认)，=1查看全部（权限）
            if(order_str!=null)//排序语句
                order_str=PubFunc.keyWord_reback(order_str);
            String z0301=(String)this.getFormHM().get("z0301");//用工申请序号
            String conditionSQL=(String)this.userView.getHm().get("hire_condition_sql");//(String)this.getFormHM().get("conditionSQL");//查询简历的条件
            
            String encryption_sql= (String)this.userView.getHm().get("hire_encryption_sql");//(String) this.getFormHM().get("encryption_sql");//界面上数据通过这条语句查询出来的
            
           // encryption_sql=SafeCode.keyWord_reback(encryption_sql);
            encryption_sql=encryption_sql.substring(0, encryption_sql.indexOf("order"));
           // if(conditionSQL!=null)
            //    conditionSQL=PubFunc.keyWord_reback(conditionSQL);
            String codesetid="";    
            String codeid="";
            String outName="";
            if(!this.userView.isSuper_admin()){
                codeid=this.getUserView().getUnitIdByBusi("7");
                if(codeid==null||codeid.trim().length()==0){
                        codeid="-0";
                }else if(codeid.trim().length()==3){
                        codesetid=codeid.substring(0,2);
                        codeid="";
                }else{//业务用户的操作单位设置有时候会不带`这个符号
                    if(codeid.trim().length()>3){
                            if(codeid.indexOf("UN")!=-1&&codeid.indexOf("`")==-1){
                                codeid=codeid.substring(2);
                            }
                    }
                }               
            
            }
            EmployResumeZipBo bo= new EmployResumeZipBo(this.getFrameconn(),this.userView);
            HashMap paramMap = new HashMap();//参数太多了 ，封装成对象传过去
            paramMap.put("nbase", nbase);
            paramMap.put("a0100", a0100);
            paramMap.put("codesetid", codesetid);
            paramMap.put("codeid", codeid);
            paramMap.put("resumeState", resumeState);
            paramMap.put("isSelectedAll", isSelectedAll);
            paramMap.put("personType", personType);
            paramMap.put("order_str", order_str);
            paramMap.put("z0301", z0301);
            paramMap.put("conditionSQL", conditionSQL);
            paramMap.put("queryType", queryType);
            paramMap.put("number", number);
            paramMap.put("encryption_sql", encryption_sql);
           
            ArrayList DataList=bo.getResumeExcelSqlAndColunm(paramMap);//包含两个map 第一个是指标集和指标map key：指标集 value指标数组 第二个map key指标集 value 另外还有一个存放指标集名称的List(排序用)
           
            ArrayList nameList=bo.createExcel(DataList,nbase);//得到Excel的名字和各个附件的名字
            
            
            String fileindex=System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
            outName="T_"+this.userView.getUserName();
            outName += PubFunc.getStrg()+".zip";
            File tempfile=new File(fileindex+outName);
            if(tempfile.exists()){//先判断zip文件是否存在如果存在删除该zip文件
                tempfile.delete();
            }
            ZipFile zipFile = new ZipFile(fileindex+outName); //生成的zip文件
            
            ZipParameters parameters = new ZipParameters(); // 设置zip包的一些参数集合
            parameters.setEncryptFiles(true); // 是否设置密码（此处设置为：是）
            //parameters.setPassword("hjsoft1919"); // 压缩包密码
            parameters.setPassword("hjsj2013"); // 压缩包密码
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密级别
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式(默认值)   
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 普通级别（参数很多）   
            
            ArrayList fileAddZip = new ArrayList(); // 向zip包中添加文件集合   
            ArrayList filenameList= new ArrayList();//存放文件名称，在zip文件生成完成以后将这些文件删除   
           parameters.setRootFolderInZip("multimedia/");//设置文件夹的目录，添加时需要从新设置参数
            String attactNames=(String) nameList.get(1);
            if(attactNames.trim().length()>0){
                String[] attactNameArr=attactNames.split(",");
                for(int i=0;i<attactNameArr.length;i++){
                    String attactName=attactNameArr[i];
                    fileAddZip.add(new File(fileindex+attactName));
                    filenameList.add(attactName);
                }
            }
            if(fileAddZip.size()>0){
                zipFile.addFiles(fileAddZip, parameters); 
            }
            /***向文件夹中添加文件end**/
            /**直接向zip文件中添加excel文件**/
            parameters.setRootFolderInZip("/");//设置文件夹的目录，添加时需要从新设置参数
            String excelname=(String) nameList.get(0);
            zipFile.addFile(new File(fileindex+excelname), parameters);
            filenameList.add(excelname);
            /**直接向zip文件中添加excel文件end**/
            
            /**向文件夹中添加文件**/

            for(int i=0;i<filenameList.size();i++){
                String filename=(String) filenameList.get(i);
                File file=new File(fileindex+filename);
                /*删除生成的excel文件 防止temp文件夹冗杂*/
                if(file.exists()){
                    file.delete();
                }
            }
            outName = SafeCode.encode(PubFunc.encrypt(outName));
            this.getFormHM().put("zipname",outName);
            this.getFormHM().put("infor", "ok");
        }catch(Exception e){
            this.getFormHM().put("infor", "error");
            e.printStackTrace();
        }

    }

}
