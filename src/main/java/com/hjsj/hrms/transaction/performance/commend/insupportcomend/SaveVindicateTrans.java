package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveVindicateTrans extends IBusiness{

	public void execute() throws GeneralException {
	
		HashMap hm = this.getFormHM();
		String tabname = (String)hm.get("p03_set_table");
		ArrayList list = (ArrayList)hm.get("p03_set_record");
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(!(list ==null|| list.size()==0)){
			     for(int i=0;i<list.size();i++){
			         RecordVo vo = (RecordVo)list.get(i);
			         /**状态为1时，为删除记录,=2更新　=-1新增,=0未做任何变化*/
			         if(vo.getState()==1)
			        	 dao.deleteValueObject(vo);
			         if(vo.getState()==2){			         
			        	 StringBuffer strsql = new StringBuffer("");
			        	 String tablename = "( select p.*,a.a0000,dbname.dbid from p03 p,UsrA01 a,dbname where p0201='"+vo.getString("p0201")+"' and p.nbase='Usr' " +
			        	 		"and p.a0100=a.a0100 and p.nbase=dbname.pre union select p.*,a.a0000,dbname.dbid from p03 p,RetA01 a,dbname where p0201='"+vo.getString("p0201")+"'" +
			        	 		" and p.nbase='Ret' and p.a0100=a.a0100 and " +
			        	 		"p.nbase=dbname.pre union select p.*,a.a0000,dbname.dbid from p03 p,TrsA01 a,dbname " +
			        	 		"where p0201='"+vo.getString("p0201")+"' and p.nbase='Trs' and p.a0100=a.a0100 and p.nbase=dbname.pre union " +
			        	 		"select p.*,a.a0000,dbname.dbid from p03 p,OthA01 a,dbname where p0201='"+vo.getString("p0201")+"' and p.nbase='Oth' " +
			        	 		"and p.a0100=a.a0100 and p.nbase=dbname.pre ) e";
				         strsql.append("select count(*) from "+tablename+" where e.p0308 = '"+vo.getString("p0308")+"' ");
				         this.frowset = dao.search(strsql.toString());
				         int count = 0;
				         while(frowset.next()){
				        	 count = frowset.getInt(1);
				         }
				        String codes="",counts="";
				 		String p0201 = (String)this.getFormHM().get("p0201");
				 		String extendattr="";
				        this.frecset = dao.search("select extendattr from p02 where p0201="+vo.getString("p0201"));
				 		if(this.frecset.next()){
				 			extendattr=this.frecset.getString("extendattr");
				 		}
				 		if(extendattr!=null&&extendattr.length()>10){
				 			Document doc=DocumentHelper.parseText(extendattr);
				 			Element root = doc.getRootElement();
				 			Element pos_list = root.element("pos_list");
				 			codes = pos_list.attributeValue("codes");
				 			counts = pos_list.attributeValue("counts");
				 		}
				 		//推荐职务及人数
						List pos_list=new ArrayList();
						if(codes!=null&&codes.length()>0){
							String[] cds=codes.split(",");
							String[] cts=counts.split(",");
							String[] plist=null;
							for (int j = 0; j < cds.length; j++) {
								if(cds[j]!=null&&cds[j].length()>0){
									plist=new String[3];
									plist[0]=cds[j];
									plist[1]="checked";
									if(cts.length>j)
										plist[2]=cts[j];
									else
										plist[2]="";
									pos_list.add(plist);
								}
							}
						}
						int pos_lis_count = -1;
						for(int k=0;k<pos_list.size();k++){
							String[] pos_list_code = (String[]) pos_list.get(k);
							if(pos_list_code[0].equals(vo.getString("p0308")) && "checked".equals(pos_list_code[1])){
								if(pos_list_code[2]=="" || pos_list_code[2]==null){
									pos_lis_count = 0;
								} else {
									pos_lis_count = Integer.parseInt(pos_list_code[2]);
								}
								break;//找到即退出，wangrd 20141216
							} else {
								pos_lis_count = -1;
							}
						}
					    if (vo.getString("p0308")==null ||vo.getString("p0308").length()<1){
					        dao.updateValueObject(vo);//没有岗位也可以保存。
					        continue;
                        }
						if(pos_lis_count==0 || count < pos_lis_count){
							 dao.updateValueObject(vo);
						} else if(pos_lis_count == -1){
							throw GeneralExceptionHandler.Handle(new Exception("推荐的职务中没有该职务!")); 
						} else if(count >= pos_lis_count){
							throw GeneralExceptionHandler.Handle(new Exception("推荐的职务人数已满!"));
						}
			        	 
			         }
			     }
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	

}
