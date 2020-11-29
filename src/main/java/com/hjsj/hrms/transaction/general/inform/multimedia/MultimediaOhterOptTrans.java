/**
 * <p>Title:MultimediaOhterOptTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:00:26</p>
 * <p>@version: 6.0</p>
 * <p>@author:wangrd</p>
 */
package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:MultimediaOhterOptTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-25 上午09:00:26</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class MultimediaOhterOptTrans extends IBusiness {


    public void execute() throws GeneralException {
        try{
            String type=(String)this.getFormHM().get("type");      
            String mediaid = (String)this.getFormHM().get("mediaid");
            mediaid = PubFunc.decrypt(mediaid);
            if ("sort_up".equals(type)||"sort_down".equals(type)){
                MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);             
                multiMediaBo.doSort(type,mediaid);
            }
            else if ("download".equals(type)){
                MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);             
                String filename =multiMediaBo.downloadFile(mediaid);
                this.getFormHM().put("filename", filename);
            }
            else if ("validfile".equals(type)){
                MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);     
                multiMediaBo.initParam();
                String infomsg="";
                //得到标题文本框的值
                String filetitle = (String)this.getFormHM().get("filetitle");
                //判断标题文本框是否有内容（包括空内容）
                if(StringUtils.isEmpty(filetitle) && StringUtils.isEmpty(filetitle.trim())){
                	infomsg="标题不能只输入空格！";
                }
                
                this.getFormHM().put("infomsg", infomsg);
            }
            else if ("validaffix".equals(type)) {//判断关联新增的时候文件是否大于限定大小，如果大于给出错误提示
            	try {
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					String dbflag = (String) this.getFormHM().get("dbflag");
					String nbase = (String) this.getFormHM().get("nbase");
					String A0100 = (String) this.getFormHM().get("a0100");
					CheckPrivSafeBo checkPiv = new CheckPrivSafeBo(this.frameconn, this.userView);
					nbase = checkPiv.checkDb(nbase);
		            A0100 = checkPiv.checkA0100("", nbase, A0100, "");
		            
					String I9999 = (String) this.getFormHM().get("i9999");// 子集中i9999
					String setid = (String) this.getFormHM().get("setid");
					String i9999list = (String) this.getFormHM().get("i9999list");// 多媒体a00中被选中i9999集合
					i9999list = i9999list == null ? "" : i9999list;
					MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn, this.userView, dbflag, nbase,setid, A0100, Integer.parseInt(I9999));
					multiMediaBo.initParam();
					if (i9999list.trim().length() > 0) {i9999list = i9999list.substring(0,i9999list.length() - 1);
						String sql = "select * from " + nbase+ "a00 where a0100='" + A0100+ "' and i9999 in(" + i9999list + ")";
						this.frowset = dao.search(sql);
						//根据,号截取关联文件的编号
						String i9[]= i9999list.split(",");
						StringBuffer i9999sb=new StringBuffer();
						StringBuffer infomsg = new StringBuffer();
						int i=0;
						int success=0;
						int fail=0;
						StringBuffer overflowSize =new StringBuffer();
						while (this.frowset.next()) {
							int errorInfo=0;
							HashMap map = new HashMap();
							String title = this.frowset.getString("title");
							String prefix = title;
							if (title == null || title.length() < 4)
								prefix = "media";
							String ext = this.frowset.getString("ext");
							if (ext == null || ext.indexOf(".") == -1)
								ext = "." + ext;
							
							//取得成功的编号
							if(errorInfo==0){
								i9999sb.append(i9[i]+",");
								success++;
							}
							i++;
						}
						if(fail>0){
							infomsg.append("关联附件成功"+success+"个，失败"+fail+"个！");
						}
						this.getFormHM().put("infomsg", infomsg.toString());
						this.getFormHM().put("i9999list", i9999sb.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
            else if ("outcontent".equals(type)){  
                RecordVo Vo=new RecordVo("hr_multimedia_file");
                ContentDAO dao=new ContentDAO(this.getFrameconn());
                Vo.setString("id",mediaid);
                try {
                    Vo=dao.findByPrimaryKey(Vo);
                } catch (GeneralException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String memo =Vo.getString("description");
                memo=memo!=null&&memo.length()>0?memo:"";
                
                this.getFormHM().put("content",SafeCode.encode(memo));
                
            }else if ("outtitle".equals(type)) {
            	RecordVo Vo=new RecordVo("hr_multimedia_file");
                ContentDAO dao=new ContentDAO(this.getFrameconn());
                Vo.setString("id",mediaid);
                try {
                    Vo=dao.findByPrimaryKey(Vo);
                } catch (GeneralException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String memo =Vo.getString("topic");
                memo=memo!=null&&memo.length()>0?memo:"";
                
                this.getFormHM().put("content",SafeCode.encode(memo));
				
			}else if ("outclass".equals(type)) {
                MultiMediaBo multimediaBo = new MultiMediaBo(this.frameconn,this.userView);
                String dbFlag = (String)this.getFormHM().get("dbFlag");
                multimediaBo.setDbFlag(dbFlag);
                String classValue ="";
                try {
                	classValue = multimediaBo.changeClassValue(mediaid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                this.getFormHM().put("content",SafeCode.encode(classValue));
				
			}
        
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
