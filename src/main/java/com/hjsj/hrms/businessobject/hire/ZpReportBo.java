package com.hjsj.hrms.businessobject.hire;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

	public class ZpReportBo {
		private Connection conn=null;
		private UserView userview=null;
		private RowSet frowset=null;
		private int width;
		private int heitht;
		private HashMap posMap=new HashMap();
		private HashMap lisMap=new HashMap();
		private HashMap codeMap=new HashMap();
		private String zpchanel="";
		public HashMap getPosMap() {
			return posMap;
		}
		public void setPosMap(HashMap posMap) {
			this.posMap = posMap;
		}
		public ZpReportBo(){
			this.init();
		}
		public ZpReportBo(Connection conn,UserView userview){
				this.userview=userview;
				this.conn=conn;
				this.init();
		}
		public HashMap getmap(String fields){
			HashMap hm=new HashMap();
			if(fields.length()!=0){
				HashMap lineMap=new HashMap();
				ArrayList list=new ArrayList();
				String []fields1=fields.substring(1).split("`");
				for(int i=0;i<fields1.length;i++){
					String []childfields=fields1[i].split("/");
					hm.put(String.valueOf(i), childfields[0]);
				}
				hm.put("length", String.valueOf(fields1.length));
			}
			return hm;
		}
		public void init(){
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			for(int i=0;i<list.size();i++){
				FieldItem item=(FieldItem)list.get(i);
				if(item.isCode()){
					this.codeMap.put(item.getItemid(), item);
				}else{
					this.lisMap.put(item.getItemid(), item.getItemdesc());
				}
			}
		}
		public String analyse(HashMap map,HashMap resultMap,HashMap lieMap,String whl_sql){
			
			
			StringBuffer sql=new StringBuffer();
			StringBuffer gsql=new StringBuffer(" group by ");
			StringBuffer osql=new StringBuffer(" order by ");
			StringBuffer temp=new StringBuffer();
			sql.append("select ");
			if(map!=null&&map.size()!=0){
				String length=(String)map.get("length");
				for(int i=0;i<Integer.parseInt(length);i++){
					String fields=(String)map.get(String.valueOf(i));
					sql.append(fields);
					sql.append(",");
					gsql.append(fields);
					gsql.append(",");
					osql.append(fields);
					osql.append(",");
					temp.append(fields);
					temp.append(",");
				}
			
			}
			if(lieMap!=null&&lieMap.size()!=0){
				String length=(String)lieMap.get("length");
				for(int i=0;i<Integer.parseInt(length);i++){
					String fields=(String)lieMap.get(String.valueOf(i));
					sql.append(fields);
					sql.append(",");
					gsql.append(fields);
					gsql.append(",");
					osql.append(fields);
					osql.append(",");
					temp.append(fields);
					temp.append(",");
				}
			
			}
			if(resultMap!=null&&resultMap.size()!=0){
				String length=(String)resultMap.get("length");
				for(int i=0;i<Integer.parseInt(length);i++){
					String fields=(String)resultMap.get(String.valueOf(i));
					sql.append("sum(");
					sql.append(fields);
					sql.append(") as  ");
					sql.append(fields);
					sql.append(",");
					osql.append(fields);
					osql.append(",");
					temp.append(fields);
					temp.append(",");
				}
			}
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("z0331")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("z0331")+"="+yy+" and "+Sql_switcher.month("z0331")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("z0331")+"="+yy+" and "+Sql_switcher.month("z0331")+"="+mm+" and "+Sql_switcher.day("z0331")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("z0329")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("z0329")+"="+yy+" and "+Sql_switcher.month("z0329")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("z0329")+"="+yy+" and "+Sql_switcher.month("z0329")+"="+mm+" and "+Sql_switcher.day("z0329")+"<="+dd+" ) ) ");	
			sql.setLength(sql.length()-1);
			sql.append(" from z03 ");
			sql.append(whl_sql);
			sql.append(ext_sql.toString());
			gsql.setLength(gsql.length()-1);
			gsql.append(" ");
			sql.append(gsql.toString());
			osql.setLength(osql.length()-1);
			sql.append(osql.toString());
			return sql.toString();
			
		}
		/**
		 * 分析sql查询结果 将层次划分清楚 ，算出n-1层级以上所占用的行或列
		 * map 行指标集，liemap列指标集
		 * 
		 * */
		public  HashMap anaLyse2(String sql,HashMap map,HashMap lieMap,HashMap resultMap){
			HashMap hm=new HashMap();
			HashMap llistMap=new HashMap();
			HashMap rlistMap=new HashMap();
			HashMap lastlengthMap=new HashMap();
			HashMap fieldsMap=new HashMap();
			ContentDAO dao=new ContentDAO(this.conn);
			try {
				this.frowset=dao.search(sql);
				
				HashMap lfloorMap=new HashMap();
				HashMap rfloorMap=new HashMap();
				HashMap result=new HashMap();
				while(this.frowset.next()){
					if(map!=null&&map.size()!=0){// 行指标集分析 用每一层对应下一层的值都用map存储因为 通过结果查出来的结果集 是行列共有的回用重复项
						HashMap temp=new HashMap();
						String length=(String)map.get("length");
						for(int i=0;i<Integer.parseInt(length);i++){
							String fields=(String)map.get(String.valueOf(i));
							String field=this.frowset.getString(fields);
							temp.put(String.valueOf(i), field);
						}
						HashMap tmap=null;
						HashMap ff=new HashMap();
						for(int k=0;k<Integer.parseInt(length);k++){
							if(k!=Integer.parseInt(length)-1){
								if(k==0){
									if(lfloorMap.get((String)temp.get(String.valueOf(k)))!=null){
										tmap=(HashMap)lfloorMap.get((String)temp.get(String.valueOf(k)));
										ff.put(String.valueOf(k), tmap);
									}else{
										tmap=new HashMap();
										tmap=this.rollBack(ff, temp, k, Integer.parseInt(length),llistMap,resultMap,"1");
										HashMap tem=(HashMap)tmap.get((String)temp.get(String.valueOf(k)));
										lfloorMap.put((String)temp.get(String.valueOf(k)), tem);
										k=Integer.parseInt(length)-1;
									}
								}else{
									if(tmap.get((String)temp.get(String.valueOf(k)))!=null){
										tmap=(HashMap)tmap.get((String)temp.get(String.valueOf(k)));
										ff.put(String.valueOf(k), tmap);
									}else{
										if(lfloorMap.size()==0) {
                                            lfloorMap=this.rollBack(ff, temp, k, Integer.parseInt(length),llistMap,resultMap,"1");
                                        } else{
											tmap=new HashMap();
											tmap=this.rollBack(ff, temp, k, Integer.parseInt(length),llistMap,resultMap,"1");
											HashMap tem=(HashMap)tmap.get((String)temp.get(String.valueOf(0)));
											lfloorMap.put((String)temp.get(String.valueOf(0)), tem);
											 k=Integer.parseInt(length)-1;
										}
										 k=Integer.parseInt(length)-1;
									}
								}
							}else{
								if(Integer.parseInt(length)==1){
									if(tmap==null){
										tmap=new HashMap();
									}
									if(tmap.get((String)temp.get(String.valueOf(k)))!=null){
										continue;
									}else{
										if(lfloorMap.get((String)temp.get(String.valueOf(k)))!=null){
											continue;
										}
										lfloorMap.put((String)temp.get(String.valueOf(k)), "1");
										if(llistMap.get(String.valueOf(k))!=null){
											ArrayList list=(ArrayList)llistMap.get(String.valueOf(k));
											list.add((String)temp.get(String.valueOf(k)));
											llistMap.put(String.valueOf(k), list);
											
										}else{
											ArrayList list=new ArrayList();
											list.add((String)temp.get(String.valueOf(k)));
											llistMap.put(String.valueOf(k), list);
										}
									}
								}else{
									if(tmap.get((String)temp.get(String.valueOf(k)))!=null){
										continue;
									}else{
										HashMap re=this.rollBack(ff, temp, k, Integer.parseInt(length),llistMap,resultMap,"1");
										lfloorMap.put((String)temp.get(String.valueOf(0)), re.get((String)temp.get(String.valueOf(0))));
									}
								}
							}
						}
					}
					int os=0;
					boolean flag=false;
					String name="";
					if(lieMap!=null&&lieMap.size()!=0){//列指标分析与行指标分析同理
						HashMap temp=new HashMap();
						String length=(String)lieMap.get("length");
						HashMap chofuMap=new HashMap();
						for(int i=0;i<Integer.parseInt(length);i++){
							String fields=(String)lieMap.get(String.valueOf(i));
							String field=this.frowset.getString(fields);
							temp.put(String.valueOf(i), field);
						}
						name=this.lieAnalyse(temp, Integer.parseInt(length), rlistMap);
					}

					int x=0;
					if(llistMap!=null){
						ArrayList ll=(ArrayList)llistMap.get(String.valueOf(Integer.parseInt((String)map.get("length"))-1));
						x=ll.size();
					}
					if(lastlengthMap.get(name)!=null){
						HashMap va=(HashMap)lastlengthMap.get(name);
						if(resultMap!=null){
							int tt=Integer.parseInt((String)resultMap.get("length"));
							for(int i=0;i<tt;i++){
								String rs=this.frowset.getString((String)resultMap.get(String.valueOf(i)));
								if(rs==null|| "null".equalsIgnoreCase(rs)||rs.trim().length()==0){
									rs="0";
								}
								va.put(String.valueOf(x*tt-(tt-i)),rs);
							}
							lastlengthMap.put(name, va);
						}
					}else{
						HashMap va=new HashMap();
						if(resultMap!=null){
							int tt=Integer.parseInt((String)resultMap.get("length"));
							for(int i=0;i<tt;i++){
								String rs=this.frowset.getString((String)resultMap.get(String.valueOf(i)));
								if(rs==null|| "null".equalsIgnoreCase(rs)||rs.trim().length()==0){
									rs="0";
								}
								va.put(String.valueOf(x*tt-(tt-i)),rs);
							}
							lastlengthMap.put(name, va);
						}
					}
				}
				//将结果对应到行列
				int y=0;
				int x=0;
				ArrayList llr=null;
				if(llistMap.get(String.valueOf(Integer.parseInt((String)map.get("length"))-1))!=null){
					ArrayList ll=(ArrayList)llistMap.get(String.valueOf(Integer.parseInt((String)map.get("length"))-1));
					x=ll.size();
				}
				int lentr=Integer.parseInt((String)resultMap.get("length"));
				if(rlistMap!=null){
					int leng=Integer.parseInt(((String)lieMap.get("length")));
					if(this.posMap!=null){
						HashMap lengmap=(HashMap)rlistMap.get("lenth");
						HashMap newlenMap=new HashMap();
						HashMap ttmap=new HashMap();
						HashMap floorMap=new HashMap();
						for(int i=0;i<leng;i++){
							HashMap newpolenMap=new HashMap();
							ArrayList list=new ArrayList();
							HashMap lnm=new HashMap();
							ArrayList lie=new ArrayList();
							ArrayList qnd=new ArrayList();
							if(i==leng-1){
								 lie=(ArrayList)rlistMap.get(String.valueOf(i));
								 ArrayList li=(ArrayList)rlistMap.get(String.valueOf(i-1));
								if(i==0){
									HashMap tmd=new HashMap();
									 
									for(int k=0;k<lie.size();k++){
										ArrayList re=(ArrayList)rlistMap.get(lie.get(k));
										if(lie.get(i)!=null&&tmd.get(lie.get(k))==null){
											qnd.add(lie.get(k));
											tmd.put(lie.get(k), String.valueOf(1));
											lnm.put(String.valueOf(qnd.size()-1), (String)lengmap.get(lie.get(k)));
											 int size=re.size();
											 for(int m=0;m<x*lentr;m++){
												 re.add(String.valueOf(0));
											 }
											 if(lastlengthMap.get(lie.get(k))!=null){
												 HashMap tt=(HashMap)lastlengthMap.get(lie.get(k));
												 Set key=tt.keySet();
												 Iterator t=key.iterator();
												 while(t.hasNext()){
													 String ks=t.next().toString();
														String value=(String)tt.get(ks);
														re.set(Integer.parseInt(ks)+size, value);
												 }
												 floorMap.put(String.valueOf(qnd.size()-1), re);
											 }
										}else{
											continue;
										}
									}
								}else{
									 for(int mm=0;mm<li.size();mm++){
										 for(int k=0;k<lie.size();k++){
											 if(lie.get(k).toString().indexOf(li.get(mm)+"/")!=-1){
												 if(lengmap.get(lie.get(k))!=null){
													 qnd.add(lie.get(k));
													 lnm.put(String.valueOf(qnd.size()-1), (String)lengmap.get(lie.get(k)));
													 ArrayList re=(ArrayList)rlistMap.get(lie.get(k));
													 int size=re.size();
													 for(int m=0;m<x*lentr;m++){
														 re.add(String.valueOf(0));
													 }
													 if(lastlengthMap.get(lie.get(k))!=null){
														 HashMap tt=(HashMap)lastlengthMap.get(lie.get(k));
														 Set key=tt.keySet();
														 Iterator t=key.iterator();
														 while(t.hasNext()){
															 String ks=t.next().toString();
																String value=(String)tt.get(ks);
																re.set(Integer.parseInt(ks)+size, value);
														 }
														 floorMap.put(String.valueOf(qnd.size()-1), re);
													 }
												 }
											 }else{
												 continue;
											 }
											 
										 }
									 }
								}
								 rlistMap.put("floor", floorMap);
								 rlistMap.put(String.valueOf(i), qnd);
							}else{
								HashMap tmd=new HashMap();
								lie=(ArrayList)rlistMap.get(String.valueOf(i));
								if(i==0){
									for(int k=0;k<lie.size();k++){
											if(lie.get(i)!=null&&tmd.get(lie.get(k))==null){
												qnd.add(lie.get(k));
												tmd.put(lie.get(k), String.valueOf(1));
												lnm.put(String.valueOf(qnd.size()-1), (String)lengmap.get(lie.get(k)));
											}else{
													continue;
											}
									}
								}else{
									ArrayList li=(ArrayList)rlistMap.get(String.valueOf(i-1));
									 for(int mm=0;mm<li.size();mm++){
										for(int k=0;k<lie.size();k++){
											 if(lie.get(k).toString().indexOf(li.get(mm)+"/")!=-1){
												if(lie.get(i)!=null&&tmd.get(lie.get(k))==null){
													qnd.add(lie.get(k));
													tmd.put(lie.get(k), String.valueOf(1));
													lnm.put(String.valueOf(qnd.size()-1), (String)lengmap.get(lie.get(k)));
												}else{
														continue;
												}
											 }
										}
									 }
								}
								 rlistMap.put(String.valueOf(i), qnd);
							}
							newlenMap.put(String.valueOf(i), lnm);
							rlistMap.put("newlenth", newlenMap);
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			hm.put("hang", llistMap);
			hm.put("lie", rlistMap);
			return hm;
		}
		public HashMap getFloorMap(HashMap temp,String length){
			HashMap resuletMap=new HashMap();
			for(int k=0;k<Integer.parseInt(length);k++){
				HashMap kmap=null;
				if(resuletMap.get(String.valueOf(k))!=null){
					kmap=(HashMap)resuletMap.get(String.valueOf(k));
					if(kmap.get((String)temp.get(String.valueOf(k)))!=null){
						if(k!=Integer.parseInt(length)-1){
							HashMap tem=(HashMap)kmap.get((String)temp.get(String.valueOf(k)));
							tem.put((String)temp.get(String.valueOf(k+1)), "1");
							if(k!=0){
								temp.put("parentid", (String)temp.get(String.valueOf(k-1)));
							}
							kmap.put((String)temp.get(String.valueOf(k)), tem);
						}
						else{
							kmap.put((String)temp.get(String.valueOf(k)), "1");
						}
					}else{
						HashMap tem=new HashMap();
						if(k!=Integer.parseInt(length)-1){
							tem.put((String)temp.get(String.valueOf(k+1)), "1");
							if(k!=0){
								temp.put("parentid", (String)temp.get(String.valueOf(k-1)));
							}
							kmap.put((String)temp.get(String.valueOf(k)), tem);
						}
						else{
							kmap.put((String)temp.get(String.valueOf(k)), "1");
						}
					}
				}else{
					kmap=new HashMap();
					HashMap tem=new HashMap();
					if(k!=Integer.parseInt(length)-1){
						tem.put((String)temp.get(String.valueOf(k+1)), "1");
						if(k!=0){
							temp.put("parentid", (String)temp.get(String.valueOf(k-1)));
						}
						kmap.put((String)temp.get(String.valueOf(k)), tem);
					}else{
						kmap.put((String)temp.get(String.valueOf(k)), "1");
					}
				}
				resuletMap.put(String.valueOf(k), kmap);
			}
			return resuletMap;
		}
		public HashMap rollBack(HashMap floor,HashMap tem,int pos,int length,HashMap listMap,HashMap result,String flag){
			HashMap tt=new HashMap();
			int lent;
			if(result!=null){
				lent=result.size()-1;
			}else{
				lent=1;
			}
			if(length>1){
				for(int k=length-1;k>=pos;k--){					
						HashMap dd=new HashMap();
						if(k==length-1){
							if(pos!=length-1){
								if(listMap.get(String.valueOf(k))!=null){
									ArrayList list=(ArrayList)listMap.get(String.valueOf(k));
									list.add((String)tem.get(String.valueOf(k)));
									listMap.put(String.valueOf(k), list);
									if(listMap.get("len")!=null){
										HashMap lm=(HashMap)listMap.get("len");
										if(lm.get(String.valueOf(k))!=null){
											HashMap fpl=(HashMap)lm.get(String.valueOf(k));
											int len=0;
											if(fpl.get(String.valueOf(list.size()-1))!=null){
												len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
												len=len+lent;
											}else{
												len=len+lent;
											}
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k), fpl);
											listMap.put("len", lm);
										}
										
									}else{
										HashMap lm=new HashMap();
										int len=1*lent;
										HashMap fpl=new HashMap();
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
										
									}
								}else{
									ArrayList list=new  ArrayList();
									list.add((String)tem.get(String.valueOf(k)));
									listMap.put(String.valueOf(k), list);
									HashMap lm=new HashMap();
									int len=0;
									len=len+lent;
									HashMap fpl=new HashMap();
									fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
									lm.put(String.valueOf(k), fpl);
									listMap.put("len", lm);
									
								}
								dd.put((String)tem.get(String.valueOf(k--)), "1");
								if(listMap.get(String.valueOf(k))!=null){
									ArrayList list=(ArrayList)listMap.get(String.valueOf(k));
									list.add((String)tem.get(String.valueOf(k)));
									listMap.put(String.valueOf(k), list);
									HashMap lm=new HashMap();
									if(listMap.get("len")!=null){
										lm=(HashMap)listMap.get("len");
										if(lm.get(String.valueOf(k))!=null){
											HashMap fpl=(HashMap)lm.get(String.valueOf(k));
											int len=0;											
											if(fpl.get(String.valueOf(list.size()-1))!=null){
												len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
												len=len+lent;
											}else{
												len=len+lent;
											}
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k), fpl);
											listMap.put("len", lm);
										}else{
											int len=0;
											len=len+lent;
											HashMap fpl=new HashMap();
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k), fpl);
											listMap.put("len", lm);
										}
									}else{
										
									}
								}else{
									ArrayList list=new  ArrayList();
									list.add((String)tem.get(String.valueOf(k)));
									listMap.put(String.valueOf(k), list);
									HashMap lm=new HashMap();
									if(listMap.get("len")!=null){
										lm=(HashMap)listMap.get("len");
										if(lm.get(String.valueOf(k))!=null){
											
										}else{
											int len=0;
											len=len+lent;
											HashMap fpl=new HashMap();
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k), fpl);
											listMap.put("len", lm);
										}
									}else{
										
									}
									
								}
								tt.put((String)tem.get(String.valueOf(k)), dd);
							}else{
								
							}
							
						}
						else{
							if(listMap.get(String.valueOf(k))!=null){
								ArrayList list=(ArrayList)listMap.get(String.valueOf(k));
								list.add((String)tem.get(String.valueOf(k)));
								listMap.put(String.valueOf(k), list);
								HashMap lm=new HashMap();
								if(listMap.get("len")!=null){
									lm=(HashMap)listMap.get("len");
									if(lm.get(String.valueOf(k))!=null){
										HashMap fpl=(HashMap)lm.get(String.valueOf(k));
										int len=0;
										if(fpl.get(String.valueOf(list.size()-1))!=null){
											len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
											len=len+lent;
										}else{
											len=len+lent;
										}
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
									}else{
										int len=0;
										len=len+lent;
										HashMap fpl=new HashMap();
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
									}
								}else{
									
								}
							}else{
								ArrayList list=new  ArrayList();
								list.add((String)tem.get(String.valueOf(k)));
								listMap.put(String.valueOf(k), list);
								HashMap lm=new HashMap();
								if(listMap.get("len")!=null){
									lm=(HashMap)listMap.get("len");
									if(lm.get(String.valueOf(k))!=null){
										HashMap fpl=(HashMap)lm.get(String.valueOf(k));
										int len=0;
										if(fpl.get(String.valueOf(list.size()-1))!=null){
											len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
											len=len+lent;
										}else{
											len=len+lent;
										}
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
									}else{
										int len=0;
										len=len+lent;
										HashMap fpl=new HashMap();
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
									}
								}else{
									
								}
							}
							dd.put((String)tem.get(String.valueOf(k)), tt);
							tt=null;
							tt=new HashMap();
							tt.put((String)tem.get(String.valueOf(k)), dd.get((String)tem.get(String.valueOf(k))));
						}
				}
			}else{
				tt.put((String)tem.get(String.valueOf(0)), "1");
			}
			if(pos>0){
				HashMap hmm=new HashMap();
				for(int k=pos;k>=0;k--){
					HashMap t1=new HashMap();
					if(k!=0){
						if(k==pos){// qufen flag=1=2 de len
							t1=(HashMap)floor.get(String.valueOf(k-1));
							if(pos==length-1){
								if(listMap.get(String.valueOf(k))!=null){
									ArrayList list=(ArrayList)listMap.get(String.valueOf(k));
									list.add((String)tem.get(String.valueOf(k)));
									if(listMap.get("len")!=null){
										HashMap lm=(HashMap)listMap.get("len");
										if(lm.get(String.valueOf(k))!=null){
											HashMap fpl=(HashMap)lm.get(String.valueOf(k));
											int len=0;
											if(fpl.get(String.valueOf(list.size()-1))!=null){
												len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
												len=len+lent;
											}else{
												len=len+lent;
											}
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k), fpl);
											listMap.put("len", lm);
										}
									}else{
										HashMap lm=new HashMap();
										int len=1*lent;
										HashMap fpl=new HashMap();
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k), fpl);
										listMap.put("len", lm);
										
									}
									listMap.put(String.valueOf(k), list);
								}else{
									ArrayList list=new  ArrayList();
									list.add((String)tem.get(String.valueOf(k)));
									listMap.put(String.valueOf(k), list);
								}
								t1.put((String)tem.get(String.valueOf(k)), "1");
								
								hmm.put((String)tem.get(String.valueOf(k-1)), t1);
								if(listMap.get("len")!=null){
									HashMap lm=(HashMap)listMap.get("len");
									if(lm.get(String.valueOf(k-1))!=null){
										HashMap fpl=(HashMap)lm.get(String.valueOf(k-1));
										int len=0;
										ArrayList list=(ArrayList)listMap.get(String.valueOf(k-1));
										if(fpl.get(String.valueOf(list.size()-1))!=null){
											len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
											len=len+lent;
										}else{
											len=len+lent;
										}
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k-1), fpl);
										listMap.put("len", lm);
									}
								}
							}else{
								t1.put((String)tem.get(String.valueOf(k)), tt.get((String)tem.get(String.valueOf(k))));
								if(listMap.get("len")!=null){
									HashMap lm=(HashMap)listMap.get("len");
										if(lm.get(String.valueOf(k-1))!=null){
											HashMap fpl=(HashMap)lm.get(String.valueOf(k-1));
											int len=0;
											ArrayList list=(ArrayList)listMap.get(String.valueOf(k-1));
											if(fpl.get(String.valueOf(list.size()-1))!=null){
												len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
												len=len+lent;
											}else{
												len=len+lent;
											}
											fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
											lm.put(String.valueOf(k-1), fpl);
											listMap.put("len", lm);
										}
								}
								hmm.put((String)tem.get(String.valueOf(k-1)),t1);
							}
						}else{
							t1=(HashMap)floor.get(String.valueOf(k-1));
							t1.put((String)tem.get(String.valueOf(k)), hmm.get((String)tem.get(String.valueOf(k))));
							if(listMap.get("len")!=null){
								HashMap lm=(HashMap)listMap.get("len");
									if(lm.get(String.valueOf(k-1))!=null){
										HashMap fpl=(HashMap)lm.get(String.valueOf(k-1));
										int len=0;
										ArrayList list=(ArrayList)listMap.get(String.valueOf(k-1));
										if(fpl.get(String.valueOf(list.size()-1))!=null){
											len=Integer.parseInt((String)fpl.get(String.valueOf(list.size()-1)));
											len=len+lent;
										}else{
											len=len+lent;
										}
										fpl.put(String.valueOf(list.size()-1), String.valueOf(len));
										lm.put(String.valueOf(k-1), fpl);
										listMap.put("len", lm);
									}
							}
							hmm=null;
							hmm=new HashMap();
							hmm.put((String)tem.get(String.valueOf(k-1)), t1);
						}
					}
				}
				return hmm;
			}else{
				return tt;
			}
		}
		public String getHtml(HashMap listMap,HashMap map,HashMap liemap,HashMap resultMap){
			StringBuffer htmlContext=new StringBuffer();
			int firstRow=0;
			int firstcol=0;
			int lkk=Integer.parseInt((String)resultMap.get("length"));
			if(map!=null&&map.size()!=0){
				firstRow=Integer.parseInt((String)map.get("length"));
			}
			if(liemap!=null&&liemap.size()!=0){
				firstcol=Integer.parseInt((String)liemap.get("length"));
			}
			HashMap llistMap=(HashMap)listMap.get("hang");
			HashMap rlistMap=(HashMap)listMap.get("lie");
			ArrayList ll=(ArrayList)llistMap.get(String.valueOf(firstRow-1));
			htmlContext.append("<table  class='ListTable' width='100%' height='90'> ");
			htmlContext.append("<tr valign='top' height='30'>");
			htmlContext.append("<td   height='30' class='RecordRow' valign='middle' colspan='1'");
			htmlContext.append("' rowspan='");
			htmlContext.append(firstRow+1);
			htmlContext.append("' >");
			htmlContext.append("序号");
			htmlContext.append("</td>");
			htmlContext.append("<td   height='30' class='RecordRow' valign='middle' colspan='");
			htmlContext.append(firstcol);
			htmlContext.append("' rowspan='");
			htmlContext.append(firstRow+1);
			htmlContext.append("' >");
			htmlContext.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			htmlContext.append("</td>");
			if(firstRow!=1){
				HashMap widthMap=(HashMap)llistMap.get("len");
				//生成表横表头
				for(int k=0;k<firstRow;k++){
					HashMap rwidthmap=(HashMap)widthMap.get(String.valueOf(k));
					ArrayList lineList=(ArrayList)llistMap.get(String.valueOf(k));
					if(k==0){
						for(int i=0;i<lineList.size();i++){
							htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' rowspan='1' colspan='");
							htmlContext.append((String)rwidthmap.get(String.valueOf(i)));
							htmlContext.append("' >");
							htmlContext.append((String)lineList.get(i));
							htmlContext.append("</td>");
						}
						htmlContext.append("</tr>");
					}else{
						htmlContext.append("<tr valign='top' height='30'>");
						for(int i=0;i<lineList.size();i++){
							htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' rowspan='1' colspan='");
							htmlContext.append((String)rwidthmap.get(String.valueOf(i)));
							htmlContext.append("' >");
							htmlContext.append((String)lineList.get(i));
							htmlContext.append("</td>");
						}
						htmlContext.append("</tr>");
					}
				}
			}else{
				for(int k=0;k<firstRow;k++){
					
					ArrayList lineList=(ArrayList)llistMap.get(String.valueOf(k));
					if(k==0){
						for(int i=0;i<lineList.size();i++){
							htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' rowspan='1' colspan='");
							htmlContext.append(String.valueOf(lkk));
							htmlContext.append("' >");
							htmlContext.append((String)lineList.get(i));
							htmlContext.append("</td>");
						}
						htmlContext.append("</tr>");
					}else{
						htmlContext.append("<tr valign='top' height='30'>");
						for(int i=0;i<lineList.size();i++){
							htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' rowspan='1' colspan='1");
							htmlContext.append("' >");
							htmlContext.append((String)lineList.get(i));
							htmlContext.append("</td>");
						}
						htmlContext.append("</tr>");
					}
				}
			}
			htmlContext.append("<tr valign='top' height='30'>");
			
			for(int i=0;i<ll.size();i++){
				for(int d=0;d<lkk;d++){
					htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' rowspan='1' colspan='1");
					htmlContext.append("' >");
					htmlContext.append((String)resultMap.get(String.valueOf(d)));
					htmlContext.append("</td>");
				}
			}
			htmlContext.append("</tr>");
			//生成列表头和数据区
			HashMap rowmap=(HashMap)rlistMap.get("floor");
			ArrayList list=(ArrayList)rlistMap.get(String.valueOf(Integer.parseInt((String)liemap.get("length"))-1));
			if(firstcol!=1){
				HashMap lengthMap=(HashMap)rlistMap.get("newlenth");
				int len=Integer.parseInt((String)liemap.get("length"));
				ArrayList basislist=(ArrayList)llistMap.get(String.valueOf(Integer.parseInt((String)map.get("length"))-1));
				int baslent=basislist.size();
				HashMap posMap=new HashMap();
				for(int i=0;i<list.size();i++){
					ArrayList rowlist=(ArrayList)rowmap.get(String.valueOf(i));
					int size=rowlist.size();
					int floor=size-baslent*lkk;
					int pos=1;
					htmlContext.append("<tr valign='top' height='30'><td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='1'>");
					htmlContext.append(""+i);
					htmlContext.append("</td>");
					for(int mm=floor;mm>0;mm--){
							HashMap llMap=(HashMap)lengthMap.get(String.valueOf(len-mm));
							if(posMap.get(String.valueOf(floor-mm))!=null){
								
								pos=Integer.parseInt((String)posMap.get(String.valueOf(len-mm)));
								pos=pos+1;
								htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='");
								htmlContext.append((String)llMap.get(String.valueOf(pos)));
								htmlContext.append("' >");
							
								htmlContext.append((String)rowlist.get(floor-mm));
								htmlContext.append("</td>");
								posMap.put(String.valueOf(len-mm), String.valueOf(pos));
							}else{
								pos=0;
								
								htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='");
								htmlContext.append((String)llMap.get(String.valueOf(pos)));
								htmlContext.append("' >");
							
								htmlContext.append((String)rowlist.get(floor-mm));
								htmlContext.append("</td>");
								posMap.put(String.valueOf(len-mm), String.valueOf(pos));
							}
						
					}
					for(int ff=floor;ff<rowlist.size();ff++){
						htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='1'>");
						htmlContext.append((String)rowlist.get(ff));
						htmlContext.append("</td>");
					}
					htmlContext.append("</tr>");
				}
			}else{
				for(int i=0;i<list.size();i++){
					ArrayList rowlist=(ArrayList)rowmap.get(String.valueOf(i));
					htmlContext.append("<tr valign='top' height='30'>");
					htmlContext.append("<tr valign='top' height='30'><td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='1'>");
					htmlContext.append(""+i);
					htmlContext.append("</td>");
					for(int ff=0;ff<rowlist.size();ff++){
						htmlContext.append("<td height=30 class='RecordRow' valign='middle' align='center' colspan='1' rowspan='1'>");
						htmlContext.append((String)rowlist.get(ff));
						htmlContext.append("</td>");
					}
					htmlContext.append("</tr>");
				}
				
			}
			htmlContext.append("</table>");
			return htmlContext.toString();
			
		}
		public RowSet getFrowset() {
			return frowset;
		}
		public void setFrowset(RowSet frowset) {
			this.frowset = frowset;
		}
		public String lieAnalyse(HashMap tem,int lent,HashMap lieMap){
			String parent ="";
			String pp="";
			HashMap lenthMap=null;
			ArrayList lilist=null;
			if(this.posMap!=null){
				ArrayList lieList=null;
				
				if(lieMap.get("lenth")!=null){
					lenthMap=(HashMap)lieMap.get("lenth");
					for(int k=lent;k>0;k--){
						parent="";
						for(int i=0;i<k;i++){
							parent=parent+"/"+(String)tem.get(String.valueOf(i));
						}
						if(k==lent){
							if(lenthMap.get(parent)!=null){
								return parent;
							}else{
								lenthMap.put(parent, String.valueOf(1));
								lieList=new ArrayList();
								lieList.add((String)tem.get(String.valueOf(k-1)));
							}
							pp=parent;
							if(lieMap.get(String.valueOf(k-1))!=null){
								lilist=(ArrayList)lieMap.get(String.valueOf(k-1));
								lilist.add(parent);
								lieMap.put(String.valueOf(k-1), lilist);
							}else{
								lilist=new ArrayList();
								lilist.add(parent);
								lieMap.put(String.valueOf(k-1), lilist);
							}
						}else{
							int leng=0;
							if(lenthMap.get(parent)!=null){
								 leng=Integer.parseInt((String)lenthMap.get(parent));
								 leng=leng+1;
								 lenthMap.put(parent, String.valueOf(leng));
							}else{
								lenthMap.put(parent, String.valueOf(1));
								lieList.add(0,(String)tem.get(String.valueOf(k-1)));
							}
							if(lieMap.get(String.valueOf(k-1))!=null){
								lilist=(ArrayList)lieMap.get(String.valueOf(k-1));
								lilist.add(parent);
								lieMap.put(String.valueOf(k-1), lilist);
							}else{
								lilist=new ArrayList();
								lilist.add(parent);
								lieMap.put(String.valueOf(k-1), lilist);
							}
						}
					}
					lieMap.put(pp, lieList);
					lieMap.put("lenth", lenthMap);
				}else{
					lenthMap=new HashMap();
					for(int k=lent;k>0;k--){
						parent="";
						for(int i=0;i<k;i++){
							parent=parent+"/"+(String)tem.get(String.valueOf(i));
						}
						if(k==lent){
							lenthMap.put(parent, String.valueOf(1));
							lieList=new ArrayList();
							lieList.add((String)tem.get(String.valueOf(k-1)));
							pp=parent;
						}else{
							lenthMap.put(parent, String.valueOf(1));
							lieList.add(0,(String)tem.get(String.valueOf(k-1)));
						}
						
						lilist=new ArrayList();
						lilist.add(parent);
						lieMap.put(String.valueOf(k-1), lilist);
					}
					lieMap.put(pp, lieList);
					lieMap.put("lenth", lenthMap);
				}	
			}
			return pp;
		}
		/**
		 * 字段名称
		 * */
		public HashMap getFieldsName(String fields){
			HashMap hm=new HashMap();
			hm=this.lisMap;
			return hm;
		}
		public boolean isCode(String fields){
			boolean flag=false;
			if(this.codeMap.get(fields)!=null){
				flag=true;
			}
			return flag;
		}
		/**
		 * 代码型指标
		 * */
		public HashMap getCodeName(String fields){
			HashMap hm=new HashMap();
			FieldItem item=(FieldItem)this.codeMap.get(fields);
			String codesetid=item.getCodesetid();
			StringBuffer sql=new StringBuffer();
			if("UM".equals(codesetid.toUpperCase())||"UN".equals(codesetid.toUpperCase())||"@K".equals(codesetid.toUpperCase())){
				sql.append("select * from organization  ");
			}else{
				sql.append("select * from codeitem where codesetid='");
				sql.append(codesetid);
				sql.append("'");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			try {
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					hm.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return hm;
		}
	public String getHtml2(String lineFields,String lieFields,String resultFields,String whl_sql ){	
		StringBuffer table = new StringBuffer();
		//打印行表头信息
	
		HashMap laMap=new HashMap();
		ArrayList lalist=new ArrayList();
		ArrayList rowinforlist2 = new ArrayList();
		ArrayList colinforlist = new ArrayList();
		String []fields2=lieFields.substring(1).split("`");
		String []fields3=resultFields.substring(1).split("`");
		HashMap map=new HashMap();
		HashMap lieMap=new HashMap();
		HashMap resultMap=new HashMap();
		if(lineFields.length()!=0){
			map=getmap(lineFields);
		}
		if(lieFields.length()!=0){
			lieMap=getmap(lieFields);
		}
		if(resultFields.length()!=0){
			resultMap=getmap(resultFields);
		}
		String sql=analyse(map, resultMap, lieMap,whl_sql);
		String sql2=analyse(lieMap, resultMap,map,whl_sql );
		String tempgroup="";
		String tempgroup2="";
		String tempgroup3="";
		for(int i=0;i<fields2.length;i++){
			String []childfields=fields2[i].split("/");
			tempgroup2+=childfields[0]+",";
		}
		for(int i=0;i<fields3.length;i++){
			String []childfields=fields3[i].split("/");
			tempgroup3+=childfields[0]+",";
		}
		String []fields1=null;
		int fields1lenght = 0;
		if(lineFields.length()>1){
			fields1=lineFields.substring(1).split("`");
			fields1lenght=fields1.length;
		}
		int fields3lenght = fields3.length;
		RowSet rset=null;
		try{
		ContentDAO dao = new ContentDAO(this.conn);
		int countx=fields2.length;
		
			int county=fields1lenght;
		
		if(fields1!=null){
			for(int i=0;i<fields1.length;i++){
				String []childfields=fields1[i].split("/");
				tempgroup+=childfields[0]+",";
			}
		}
		if(tempgroup.length()>0) {
            tempgroup = tempgroup.substring(0,tempgroup.length()-1);
        }
		if(tempgroup2.length()>0) {
            tempgroup2 = tempgroup2.substring(0,tempgroup2.length()-1);
        }
		if(tempgroup3.length()>0) {
            tempgroup3 = tempgroup3.substring(0,tempgroup3.length()-1);
        }
		//处理结果指标
		countx = fields2.length;
		int countto_x =fields2.length;
		county=fields1lenght+1;
		rset = dao.search(sql);
		String oldkeys="$#%$#&*%$$@&%$@*&@";
		ArrayList rowinforlist = new ArrayList();
		HashMap colmap2 = new HashMap();
			while(rset.next()){
				String tempg [] = tempgroup3.split(",");
				if(fields1lenght>0){
					String keys="";
					String tempg2 [] = tempgroup.split(",");
					for(int j=tempg2.length-1;j>=0;j--){
						keys+=(rset.getString(tempg2[j])==null?"":rset.getString(tempg2[j]))+",";
						keys=keys.trim();
					}
					if(keys.equals(oldkeys)){
						continue;
					}else{
						oldkeys = keys;
					}
					String keys2="";
					for(int j=0;j<fields1.length;j++){
						keys2+=(rset.getString(tempg2[j])==null?"":rset.getString(tempg2[j]))+",";
						keys2=keys2.trim();
					}
					if(colmap2!=null&&colmap2.get(keys2)!=null){
						countto_x =countx;
						continue;
					}
					colmap2.put(keys2, keys2);
				}
				LazyDynaBean bean =null;
				for(int i=0;i<tempg.length;i++){
					String value = tempg[i] ;
					countto_x ++;
					int from_x =  countx;
					int to_x =countto_x-1;
					int from_y = county;
					int to_y = county;
					//非代码型转值
					if(!isCode(value)){
						HashMap map5 = getFieldsName(value);
						value =""+map5.get(value);
					}
					 bean = new LazyDynaBean();
					 bean.set("hz", value);
					 bean.set("from_x", ""+from_y);
					 bean.set("to_x", ""+to_y);
					 bean.set("from_y", ""+from_x);
					 bean.set("to_y", ""+to_x);
					 rowinforlist.add(bean);
					 countx=countto_x;
				}
				if(fields1lenght<=0){
					break;
				}
			}
		if(fields3lenght>1||fields1lenght==0) {
            rowinforlist2.add(rowinforlist);
        }
		HashMap map2 = new HashMap();//合并需要
		HashMap colmap = new HashMap();//计算数据单元格对应的列指标值
		int colnum =0;//出现多少列
		if(fields1!=null){
		for(int i=fields1.length-1;i>=0;i--){
			String []childfields=fields1[i].split("/");
			rset = dao.search(sql);
			LazyDynaBean bean =null;
			 colmap2 = new HashMap();//去除重复记录比如“”，和null
			int count1 = 0;
			    countx = fields2.length;
			     countto_x=fields2.length;
			    int num=1;
			    int tr=0;
			     oldkeys="$#%$#&*%$$@&%$@*&@";
				 rowinforlist = new ArrayList();
			while(rset.next()){
				tr++;
				String tempg [] = tempgroup.split(",");
				String value = rset.getString(childfields[0])==null?"":rset.getString(childfields[0]);
				String keys="";
				for(int j=i;j>=0;j--){
					keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
					keys=keys.trim();
				}
				if(keys.equals(oldkeys)){
					num++;
					continue;
				}else{
					if(i==fields1.length-1){
						countto_x+=fields3.length;
					}else{
							countto_x += Integer.parseInt(""+map2.get(keys))*fields3.length;
					}
					num=1;
					oldkeys = keys;
				}
				if(i==fields1.length-1){
					String keys2="";
					for(int j=0;j<fields1.length;j++){
						keys2+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys2=keys2.trim();
					}
					if(colmap2!=null&&colmap2.get(keys2)!=null){
						countto_x =countx;
						continue;
					}
					colmap2.put(keys2, keys2);
					colmap.put(""+colnum, keys2);
					colnum++;
					keys="";
					for(int j=i-1;j>=0;j--){
						keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(map2!=null&&map2.get(keys)!=null){ //key 为上几层
						map2.put(keys, Integer.parseInt(""+map2.get(keys))+1+"");
					}else{
						map2.put(keys, "1");
					}
				}else{
					keys="";
					for(int j=0;j<=i;j++){
						keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(colmap2!=null&&colmap2.get(keys)!=null){
						countto_x =countx;
						continue;
					}else{
						colmap2.put(keys, keys);
					}
						String keys4="";
						for(int j=i-1;j>=0;j--){
							keys4+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
							keys4=keys4.trim();
						}
						if(map2!=null&&map2.get(keys4)!=null){ //key 为上几层
							
							if(map2!=null&&map2.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4)!=null){
								map2.put(keys4, Integer.parseInt(""+map2.get(keys4))+ Integer.parseInt(""+map2.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4))+"");
							}else{
								map2.put(keys4, Integer.parseInt(""+map2.get(keys4))+1+"");
							}
						}else{
							if(map2!=null&&map2.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4)!=null){
								map2.put(keys4, map2.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4));
							}else{
								map2.put(keys4, "1");
							}
						}
				}
//				if(i==fields1.length-1){//这里应该操作结果集
//					keys="";
//					for(int j=0;j<fields1.length;j++){
//						keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
//						keys=keys.trim();
//					}
//					colmap.put(""+colnum, keys);
//				colnum++;
//				}
				int from_x =  countx;
				int to_x =countto_x-1;
				int from_y = county;
				int to_y = county;
				//代码型转值
				if(isCode(childfields[0])){
					HashMap map5 =getCodeName(childfields[0]);
					value =""+map5.get(value);
					if(value==null|| "null".equalsIgnoreCase(value)) {
                        value="";
                    }
				}
				 bean = new LazyDynaBean();
				 bean.set("hz", value);
				 bean.set("from_x", ""+from_y);
				 bean.set("to_x", ""+to_y);
				 bean.set("from_y", ""+from_x);
				 bean.set("to_y", ""+to_x);
				 bean.set("tr", ""+tr);
				if(i==fields1.length-1){
					if(laMap.get(value)==null){
						lalist.add(value);
						laMap.put(value, "1");
					}else{
						
					}
				}	 
				 rowinforlist.add(bean);
				 countx=countto_x;
			}
		
			
			rowinforlist2.add(rowinforlist);
			county--;
		}
		HashMap tt=new HashMap();
		tt.put("group", tempgroup3);
		tt.put("laM", laMap);
		tt.put("lal", lalist);
		tt.put("fir", tempgroup);
		rowinforlist2.add(tt);
	}
		
		county= fields2.length-1;
		HashMap map3 = new HashMap();//合并需要
		HashMap rowmap = new HashMap();//计算数据单元格对应的行指标值
		int rolnum =0;//出现多少行
		for(int i=fields2.length-1;i>=0;i--){
			HashMap rowmap2 = new HashMap();//去掉重复指标
			String []childfields=fields2[i].split("/");
			rset = dao.search(sql2);
			ResultSetMetaData mdata = rset.getMetaData();
			int cout =mdata.getColumnCount();
			LazyDynaBean bean =null;
			int count1 = 0;
			
			if(fields3lenght==1){
				if(fields1lenght==0){
					 countx = fields1lenght+2;
				}else{
					 countx = fields1lenght+1;	
				}
			   
			}else{
				countx = fields1lenght+2;
			}
			     countto_x=countx;
			    int num=1;
			     oldkeys="$#%$#&*%$$@&%$@*&@";
			while(rset.next()){
				String tempg [] = tempgroup2.split(",");
				String value = rset.getString(childfields[0])==null?"":rset.getString(childfields[0]);
				String keys="";
				for(int j=i;j>=0;j--){
					keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
					keys=keys.trim();
				}
				if(keys.equals(oldkeys)){
					num++;
					continue;
				}else{
					if(i==fields2.length-1){
						countto_x++;
					}else{
							countto_x += Integer.parseInt(""+map3.get(keys));
					}
					num=1;
					oldkeys = keys;
				}
				keys="";
				for(int j=i-1;j>=0;j--){
					keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
					keys=keys.trim();
				}
				if(i==fields2.length-1){
					String keys2="";
					for(int j=0;j<fields2.length;j++){
						keys2+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys2=keys2.trim();
					}
					if(rowmap2!=null&&rowmap2.get(keys2)!=null){
						countto_x =countx;
						continue;
					}
				    rowmap2.put(keys2, keys2);
					rowmap.put(""+rolnum, keys2);
					rolnum++;
				if(map3!=null&&map3.get(keys)!=null){ //key 为上几层
					map3.put(keys, Integer.parseInt(""+map3.get(keys))+1+"");
				}else{
					map3.put(keys, "1");
				}
				}else{
					keys="";
					for(int j=0;j<=i;j++){
						keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys=keys.trim();
					}
					if(rowmap2!=null&&rowmap2.get(keys)!=null){
						countto_x =countx;
						continue;
					}else{
						 rowmap2.put(keys, keys);
					}
					String keys4="";
					for(int j=i-1;j>=0;j--){
						keys4+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
						keys4=keys4.trim();
					}
					if(map3!=null&&map3.get(keys4)!=null){ //key 为上几层
						
						if(map3!=null&&map3.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4)!=null){
							map3.put(keys4, Integer.parseInt(""+map3.get(keys4))+ Integer.parseInt(""+map3.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4))+"");
						}else{
							map3.put(keys4, Integer.parseInt(""+map3.get(keys4))+1+"");
						}
					}else{
						if(map3!=null&&map3.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4)!=null){
							map3.put(keys4, map3.get((rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+","+keys4));
						}else{
							map3.put(keys4, "1");
						}
					}
			}
//				if(i==fields2.length-1){
//					keys="";
//					for(int j=0;j<fields2.length;j++){
//						keys+=(rset.getString(tempg[j])==null?"":rset.getString(tempg[j]))+",";
//						keys=keys.trim();
//					}
//					rowmap.put(""+rolnum, keys);
//					rolnum++;
//				}
				
				//代码型转值
				if(isCode(childfields[0])){
					HashMap map5 = getCodeName(childfields[0]);
					value =""+map5.get(value.trim());
					if(value==null|| "null".equalsIgnoreCase(value)) {
                        value="";
                    }
				}
				int from_x =  countx;
				int to_x =countto_x-1;
				int from_y = county;
				int to_y = county;
				 bean = new LazyDynaBean();
				 bean.set("hz", value);
				 bean.set("from_x", ""+from_x);
				 bean.set("to_x", ""+to_x);
				 bean.set("from_y", ""+from_y);
				 bean.set("to_y", ""+to_y);
				 colinforlist.add(bean);
				 countx=countto_x;
			}
			county--;
		}
		
		//获得所有的结果map
		HashMap rMap = new HashMap();
		ArrayList result = new ArrayList();
		rset = dao.search(sql);
		while(rset.next()){
			//获得行指标
			String tempg [] = tempgroup.split(",");
			String tempg2 [] = tempgroup2.split(",");
			String tempg3 [] = tempgroup3.split(",");
			String keys ="";
			if(fields1lenght>0) {
                for(int i=0;i<tempg.length;i++){
                    keys+=(rset.getString(tempg[i])==null?"":rset.getString(tempg[i]))+",";
                }
            }
			for(int i=0;i<tempg2.length;i++){
				keys+=(rset.getString(tempg2[i])==null?"":rset.getString(tempg2[i]))+",";
			}
			for(int i=0;i<tempg3.length;i++){
				String tempkeys = keys;
				tempkeys+=tempg3[i];
				int value = rset.getInt(tempg3[i]);
				if(rMap!=null&&rMap.get(tempkeys)!=null){
						rMap.put(tempkeys,""+(Integer.parseInt(""+rMap.get(tempkeys))+value));	
				}else{
					rMap.put(tempkeys,""+value);
				}
			}
			
		}
		if(colnum==0) {
            colnum=1;
        }
		for(int i =0;i<rolnum;i++){
			String[] resultstr=new String[colnum*fields3.length];
			for(int j=0;j<colnum;j++){
				//往结果集里放数据
				String keys ="";
				if(fields1lenght>0) {
                    if(colmap!=null&&colmap.get(""+j)!=null){
                        keys+= colmap.get(""+j);
                    }
                }
				if(rowmap!=null&&rowmap.get(""+i)!=null){
					keys+= rowmap.get(""+i);
				}
				String tempg3 [] = tempgroup3.split(",");
				String value="";
				for(int a=0;a<tempg3.length;a++){
					String tempkeys = keys;
					tempkeys+=tempg3[a];
					if(rMap!=null&&rMap.get(tempkeys)!=null){
						value = ""+rMap.get(tempkeys);
						if("0".equals(value)) {
                            value="";
                        }
						resultstr[j*fields3.length+a]=value;
					}else{
						value="";
						resultstr[j*fields3.length+a]=value;
					}
				}
			
				
			}
			result.add(resultstr);
		}
		
	//	获得列指标对应名字
		String itemdesc ="";
		ArrayList listfield2 = new ArrayList();
		for(int i=0;i<fields2.length;i++){
			String []childfields=fields2[i].split("/");
		if(isCode(childfields[0])){
			FieldItem item=(FieldItem)this.codeMap.get(childfields[0]);
				if(item!=null) {
                    itemdesc = item.getItemdesc();
                }
		}else{
			itemdesc = ""+this.lisMap.get(childfields[0]);
		}
			listfield2.add(itemdesc);
		}
		ArrayList listfield3 = new ArrayList();
		for(int i=0;i<fields3.length;i++){
			String []childfields=fields3[i].split("/");
		if(isCode(childfields[0])){
			FieldItem item=(FieldItem)this.codeMap.get(childfields[0]);
				if(item!=null) {
                    itemdesc = item.getItemdesc();
                }
		}else{
			itemdesc = ""+this.lisMap.get(childfields[0]);
		}
			listfield3.add(itemdesc);
		}
		if(fields3lenght==1&&fields1lenght>0) {
            fields1lenght=fields1lenght-1;
        }
		if(rowinforlist!=null&&rowinforlist.size()!=0){//dml 2011-6-21 15:35:27
			String tablestr =	executeTabHeader(rowinforlist2,colinforlist,result,listfield2,listfield3,fields1lenght,fields2.length,rolnum);
			table.append(tablestr);
		}else{
			table.append("   ");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return table.toString();
	}
	/**
	 * 表头
	 */
	public String  executeTabHeader(ArrayList rowInfoList2,ArrayList colInfoList,ArrayList result,ArrayList listfield2,ArrayList listfield3,int firstRow,int firstcol,int rolnum)
	{
		StringBuffer htmlContext = new StringBuffer();
		LazyDynaBean abean=null;
//		htmlContext.append("<table   width='90%'  align='middle'> ");
//		htmlContext.append("<tr valign='top' height='30'>");
//		htmlContext.append("<td   height='30'  width='80' valign='middle' ");
//		htmlContext.append("</td>");
//		htmlContext.append("<tr valign='top' height='30'>");
//		htmlContext.append("<td   height='30'  width='80' valign='middle'  ");
		Calendar d=Calendar.getInstance();
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		String date ="制表日期：  "+yy+"年"+mm+"月"+dd+"日";
		HashMap zjMap=new HashMap();
		int dalenth=0;
		HashMap lMap=new HashMap();
		String desc ="";
		if(this.zpchanel!=null&&this.zpchanel.length()!=0&&!"-1".equalsIgnoreCase(this.zpchanel)){
			desc +=this.zpchanel+"(";
		}
		for(int i =0;i<listfield3.size();i++){
			desc+=listfield3.get(i)+"、";
		}
		HashMap tt=null;
		HashMap laMap=null;
		ArrayList lalist=null;
		String group=null;
		String []tgr=null;
		String []firs=null;
		HashMap zMap=new HashMap();
		if(rowInfoList2.size()>1){
			tt=(HashMap)rowInfoList2.get(rowInfoList2.size()-1);
			rowInfoList2.remove(rowInfoList2.size()-1);
			laMap=(HashMap)tt.get("laM");
			lalist=(ArrayList)tt.get("lal");
			group=(String)tt.get("group");
			tgr=group.split(",");
			String fir=(String)tt.get("fir");
			firs=fir.split(",");
			for(int mmr=0;mmr<tgr.length;mmr++){
				String value=tgr[mmr];
				if(!isCode(value)){
					HashMap map5 = getFieldsName(value);
					value =""+map5.get(value);
					zMap.put(tgr[mmr], value);
				}
			}
		}
		
		if(desc.length()>1) {
            desc= desc.substring(0,desc.length()-1);
        }
		if(this.zpchanel!=null&&this.zpchanel.length()!=0&&!"-1".equalsIgnoreCase(this.zpchanel)){
			desc +=")";
		}
		String title=yy+"年"+desc+"计划汇总表";
		htmlContext.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		htmlContext.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		htmlContext.append(" <center><font style='font-size:16pt'>"+title+"</font></center>");
		htmlContext.append("<br><center>"+date+"</center>");
		String[] rowInfo2=(String[])result.get(0);
		int width =0;
		if(firs!=null&&firs.length>0){
			width = (rowInfo2.length+listfield2.size())*70+tgr.length*lalist.size()*40+tgr.length*40;
		}else{
			width = (rowInfo2.length+listfield2.size())*70+listfield3.size()*40;
		}
		//int height = (result.size()+rowInfoList2.size())*30;
		htmlContext.append("<table  class='ListTable common_border_color' width='"+width+"' align='center' > ");//height="+height+"
		htmlContext.append("<tr valign='top' height='30'>");
		htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' width='40px' align='center'  valign='middle' colspan='1' norwap");
		htmlContext.append(" rowspan='");
		htmlContext.append(firstRow+1);
		htmlContext.append("'>");
		htmlContext.append("序号");
		htmlContext.append("</td>");
		for(int i =0;i<listfield2.size();i++){
			htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' align='center' valign='middle' colspan='");
			htmlContext.append(1);
			htmlContext.append("' rowspan='");
			htmlContext.append(firstRow+1);
			htmlContext.append("' nowrap >");
			htmlContext.append(""+listfield2.get(i));
			htmlContext.append("</td>");
		}
		int tmd=0;
		for(int i=rowInfoList2.size()-1;i>=0;i--)
		{//行表头
			if(i==rowInfoList2.size()-1){
				
			}else{
				htmlContext.append("<tr valign='top' height='30'>");
			}
			ArrayList rowInfoList = (ArrayList)rowInfoList2.get(i);
			ArrayList tb=null;
			if(rowInfoList2.size()>1){
				if(tgr.length>1){
					if(i==1){
						tb=(ArrayList)rowInfoList2.get(0);
					}
				}
			}
			for(int j =0;j<rowInfoList.size();j++){
				abean=(LazyDynaBean)rowInfoList.get(j);
				int from_x=Integer.parseInt((String)abean.get("from_x"));
				short from_y=Short.parseShort((String)abean.get("from_y"));
				//int tr =Integer.parseInt((String)abean.get("tr"));
				if(abean.get("to_x")!=null)
				{
					int to_x=Integer.parseInt((String)abean.get("to_x"));
					short to_y=Short.parseShort((String)abean.get("to_y"));
					String hz=(String)abean.get("hz");
					 String wid ="";
					
					 width = hz.length()*15;
					
					 wid = "width="+width;
					 if(to_y-from_y+1>1) {
                         wid="";
                     }
					if(hz.trim().length()!=0){
					}else{
						wid="width=40";
					}	 
					
					if(tgr==null&&j==rowInfoList.size()-1){
						htmlContext.append("<td   height='30' class='t_cell_locked3 common_border_color' "+wid+" align='center' valign='middle' colspan='");
					}else{
						htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' "+wid+" align='center' valign='middle' colspan='");
					}
					htmlContext.append(to_y-from_y+1);
					htmlContext.append("' rowspan='");
					htmlContext.append(to_x-from_x+1);
					htmlContext.append("' >");
					if(hz.trim().length()==0){
						htmlContext.append("&nbsp&nbsp");	
					}else{
						htmlContext.append(hz);
					}
					htmlContext.append("</td>");
					if(firs!=null&&((firs.length==1&&tgr.length>1)||firs.length>1)){
						if(tgr.length==1){
							if(i==0){
								lMap.put(String.valueOf(j), hz);
							}
						}else{
							if(i==1){
								for(int ii=0;ii<tgr.length;ii++){
									LazyDynaBean bean=(LazyDynaBean)tb.get(tmd);
									String value=(String)bean.get("hz");
									if(lMap.get(value)!=null){
										HashMap tr=(HashMap)lMap.get(value);
										tr.put(String.valueOf(tmd), hz);
										lMap.put(value, tr);
									}else{
										HashMap tr=new HashMap();
										tr.put(String.valueOf(tmd), hz);
										lMap.put(value, tr);
									}
									if(lMap.get("ls")!=null){
										HashMap ls=(HashMap)lMap.get("ls");
										ls.put(String.valueOf(tmd), value);
										lMap.put("ls", ls);
									}else{
										HashMap ls=new HashMap();
										ls.put(String.valueOf(tmd), value);
										lMap.put("ls", ls);
									}
									tmd++;
								}
							}
						}
					}
				}
			}
			if(firs!=null&&firs.length==1&&tgr.length==1){
				htmlContext.append("<td   height='30' class='t_cell_locked3 common_border_color' width='40'  align='center' valign='middle' colspan='");
				htmlContext.append(1);
				htmlContext.append("' rowspan='");
				htmlContext.append(1);
				htmlContext.append("' >");
				htmlContext.append("总 计");
				htmlContext.append("</td>");
			}
			if(firs!=null&&((firs.length==1&&tgr.length>1)||firs.length>1)){
				if(i==rowInfoList2.size()-1){
					for(int mmr=0;mmr<tgr.length;mmr++){
						String value=tgr[mmr];
						if(mmr==tgr.length-1){
							htmlContext.append("<td   height='30' class='t_cell_locked3 common_border_color'  align='center' valign='middle' colspan='");
						}else{
							htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color'  align='center' valign='middle' colspan='");
						}
						htmlContext.append(lalist.size()+1);
						htmlContext.append("' rowspan='");
						htmlContext.append(rowInfoList2.size()-1);
						htmlContext.append("' >");
						if(!isCode(value)){
							HashMap map5 = getFieldsName(value);
							value =""+map5.get(value);
							if(value==null||value.trim().length()==0){
								value="&nbsp;&nbsp;";
							}
						}
						htmlContext.append(value);
						htmlContext.append("</td>");
						
					}
				}
				if(i==0){
					for(int mmr1=0;mmr1<tgr.length;mmr1++){
						htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' width='40' align='center' valign='middle' colspan='");
						htmlContext.append("1");
						htmlContext.append("' rowspan='");
						htmlContext.append("1");
						htmlContext.append("' >");
						htmlContext.append("总计");
						htmlContext.append("</td>");
						for(int mmr=0;mmr<lalist.size();mmr++){
							if(mmr1==tgr.length-1&&mmr==lalist.size()-1){
								htmlContext.append("<td   height='30' class='t_cell_locked3 common_border_color' width='40'  align='center'  valign='middle' colspan='");
							}else{
								htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' width='40'  align='center'  valign='middle' colspan='");
							}
							htmlContext.append("1");
							htmlContext.append("' rowspan='");
							htmlContext.append("1");
							htmlContext.append("' >");
							String vall="";
							vall=(String)lalist.get(mmr);
							if(vall==null||vall.trim().length()==0){
								vall="&nbsp;&nbsp;";
							}
							htmlContext.append(vall);
							htmlContext.append("</td>");
						}
					}
				}
			}
			htmlContext.append("</tr>");
		}
		for(int i =0;i<rolnum;i++){
			htmlContext.append("<tr valign='top' height='30'><td height='30' class='t_cell_locked2 common_border_color' width='40px'  align='center' colspan='1' rowspan='1'>"+(i+1)+"</td>");	
			for(int j=colInfoList.size()-1;j>=0;j--)
			{	//求列表头
				abean=(LazyDynaBean)colInfoList.get(j);
				int from_x=Integer.parseInt((String)abean.get("from_x"));
				short from_y=Short.parseShort((String)abean.get("from_y"));
				int to_x=Integer.parseInt((String)abean.get("to_x"));
				short to_y=Short.parseShort((String)abean.get("to_y"));
				String hz=(String)abean.get("hz");	
				 width = hz.length()*15;
				 String wid="";
				 if(width>200) {
                     wid="width="+width;
                 }
				if(from_x-firstRow-2==i){
					htmlContext.append("<td   height='30' class='t_cell_locked2 common_border_color' "+wid+"  align='center' valign='middle' colspan='");
					htmlContext.append(to_y-from_y+1);
					htmlContext.append("' rowspan='");
					htmlContext.append(to_x-from_x+1);
					htmlContext.append("' >");
					if(hz!=null&&hz.length()==0){
						htmlContext.append("&nbsp;&nbsp;");
					}else{
					htmlContext.append(hz);
					}
					htmlContext.append("</td>");
				}
			}
			HashMap ls=null;
			if(rowInfoList2.size()>1){
				if(tgr.length>1){
					ls=(HashMap)lMap.get("ls");
				}
			}
			if(result.size()>0)
			{
					String[] rowInfo=(String[])result.get(i);
					dalenth=rowInfo.length;
					HashMap rMap=new HashMap();
					int ttf=rowInfo.length;
					int lzj=0;
					for(int j=0;j<rowInfo.length;j++)
					{
						String context="";										
						context=rowInfo[j];
						htmlContext.append("<td   height='30' class='RecordRow' align='right'   colspan='");
						htmlContext.append(1);
						htmlContext.append("' rowspan='");
						htmlContext.append(1);
						htmlContext.append("' >");
						htmlContext.append("  "+context);
						htmlContext.append("</td>");
						if(context.trim().length()==0){
							lzj=lzj+0;
						}else{
							lzj=lzj+Integer.parseInt(context);
						}
						if(zjMap.get(String.valueOf(j))!=null){
							int yy1=Integer.parseInt((String)zjMap.get(String.valueOf(j)));
							if(context.trim().length()==0){
								yy1=yy1+0;
							}else{
								yy1=yy1+Integer.parseInt(context);
							}
							zjMap.put(String.valueOf(j), String.valueOf(yy1));
						}else{
							if(context.trim().length()==0){
								zjMap.put(String.valueOf(j), String.valueOf(0));
							}else{
								zjMap.put(String.valueOf(j), context);
							}
						}
						if(firs!=null&&((firs.length==1&&tgr.length>1)||firs.length>1)){
							if(tgr.length==1){
								String dd1=(String)lMap.get(String.valueOf(j));
								if(rMap.get(dd1)!=null){
									int lk=Integer.parseInt((String)rMap.get(dd1));
									if(context.trim().length()==0){
										lk=lk+0;
									}else{
										lk=lk+Integer.parseInt(context);
									}
									rMap.put(dd1,String.valueOf(lk));
								}else{
									if(context.trim().length()==0){
										rMap.put(dd1,String.valueOf(0));
									}else{
										rMap.put(dd1,context);
									}
								}
							}else{
								String li=(String)ls.get(String.valueOf(j));
								HashMap tr=(HashMap)lMap.get(li);
								String dd1=(String)tr.get(String.valueOf(j));
								if(rMap.get(li)!=null){
									HashMap tm=(HashMap)rMap.get(li);
									if(tm.get(dd1)!=null){
										int lk=Integer.parseInt((String)tm.get(dd1));
										if(context.trim().length()==0){
											lk=lk+0;
										}else{
											lk=lk+Integer.parseInt(context);
										}
										tm.put(dd1,String.valueOf(lk));
									}else{
										if(context.trim().length()==0){
											tm.put(dd1,String.valueOf(0));
										}else{
											tm.put(dd1,context);
										}
									}
									rMap.put(li, tm);
								}else{
									HashMap tm=new HashMap();
									if(context.trim().length()==0){
										tm.put(dd1,String.valueOf(0));
									}else{
										tm.put(dd1,context);
									}
									rMap.put(li, tm);
								}
							}
						}
					}
					if(firs!=null&&firs.length==1&&tgr.length==1){
						htmlContext.append("<td   height='30' class='RecordRow' width='40'  align='right' colspan='");
						htmlContext.append(1);
						htmlContext.append("' rowspan='");
						htmlContext.append(1);
						htmlContext.append("' >");
						if(lzj==0){
							htmlContext.append(" ");
						}else{
							htmlContext.append(String.valueOf(lzj));
						}
						
						htmlContext.append("</td>");
						if(zjMap.get(String.valueOf(rowInfo.length))!=null){
							int lk=Integer.parseInt((String)zjMap.get(String.valueOf(rowInfo.length)));
							lk=lk+lzj;
							zjMap.put(String.valueOf(rowInfo.length), String.valueOf(lk));
						}else{
							int lk=0;
							lk=lk+lzj;
							zjMap.put(String.valueOf(rowInfo.length), String.valueOf(lk));
						}
						dalenth=rowInfo.length+1;
					}
					if(firs!=null&&((firs.length==1&&tgr.length>1)||firs.length>1)){
						if(tgr.length==1){
							int zj=0;
							for(int ll=0;ll<lalist.size();ll++){
								if(rMap.get(lalist.get(ll))!=null){
									zj=zj+Integer.parseInt((String)rMap.get(lalist.get(ll)));
								}
							}
							rMap.put("zj", String.valueOf(zj));
						}else{
							if(rowInfoList2.size()>1){
								for(int ll=0;ll<tgr.length;ll++){
									HashMap tr=(HashMap)rMap.get((String)zMap.get(tgr[ll]));
									int zj=0;
										for(int lk=0;lk<lalist.size();lk++){
											if(tr.get(lalist.get(lk))!=null){
												zj=zj+Integer.parseInt((String)tr.get(lalist.get(lk)));
											}
										}
										tr.put("zj", String.valueOf(zj));
										rMap.put((String)zMap.get(tgr[ll]), tr);
								}
							}
						}
						for(int ll=0;ll<tgr.length;ll++){
							if(tgr.length==1){
								for(int lk=0;lk<=lalist.size();lk++){
									htmlContext.append("<td   height='30' class='RecordRow' align='right'   colspan='");
									htmlContext.append(1);
									htmlContext.append("' rowspan='");
									htmlContext.append(1);
									htmlContext.append("' >");
									if(lk==0){
										String context=(String)rMap.get("zj");
										if(!"0".equalsIgnoreCase(context)){
											htmlContext.append((String)rMap.get("zj"));
										}else{
											htmlContext.append(" ");
										}
										if(zjMap.get(String.valueOf(ttf))!=null){
											int yy1=Integer.parseInt((String)zjMap.get(String.valueOf(ttf)));
											if(context.trim().length()==0){
												yy1=yy1+0;
											}else{
												yy1=yy1+Integer.parseInt(context);
											}
											zjMap.put(String.valueOf(ttf), String.valueOf(yy1));
										}else{
											if(context.trim().length()==0){
												zjMap.put(String.valueOf(ttf), String.valueOf(0));
											}else{
												zjMap.put(String.valueOf(ttf), context);
											}
										}
										ttf++;
									}else{
										String context=(String)rMap.get((String)lalist.get(lk-1));
										if(!"0".equalsIgnoreCase(context)){
											htmlContext.append(context);
										}else{
											htmlContext.append(" ");
										}
										if(zjMap.get(String.valueOf(ttf))!=null){
											int yy1=Integer.parseInt((String)zjMap.get(String.valueOf(ttf)));
											if(context.trim().length()==0){
												yy1=yy1+0;
											}else{
												yy1=yy1+Integer.parseInt(context);
											}
											zjMap.put(String.valueOf(ttf), String.valueOf(yy1));
										}else{
											if(context.trim().length()==0){
												zjMap.put(String.valueOf(ttf), String.valueOf(0));
											}else{
												zjMap.put(String.valueOf(ttf), context);
											}
										}
										ttf++;
									}
									htmlContext.append("</td>");
								}
							}else{
								HashMap tr=(HashMap)rMap.get((String)zMap.get(tgr[ll]));
								for(int lk=0;lk<=lalist.size();lk++){
									htmlContext.append("<td   height='30' class='RecordRow' align='right'   colspan='");
									htmlContext.append(1);
									htmlContext.append("' rowspan='");
									htmlContext.append(1);
									htmlContext.append("' >");
									if(lk==0){
										String context=(String)tr.get("zj");
										if(!"0".equalsIgnoreCase(context)){
											htmlContext.append(context);
										}else{
											htmlContext.append(" ");
										}
										if(zjMap.get(String.valueOf(ttf))!=null){
											int yy1=Integer.parseInt((String)zjMap.get(String.valueOf(ttf)));
											if(context.trim().length()==0){
												yy1=yy1+0;
											}else{
												yy1=yy1+Integer.parseInt(context);
											}
											zjMap.put(String.valueOf(ttf), String.valueOf(yy1));
										}else{
											if(context.trim().length()==0){
												zjMap.put(String.valueOf(ttf), String.valueOf(0));
											}else{
												zjMap.put(String.valueOf(ttf), context);
											}
										}
										ttf++;
									}else{
										String context=(String)tr.get((String)lalist.get(lk-1));
										if(!"0".equalsIgnoreCase(context)){
											htmlContext.append(context);
										}else{
											htmlContext.append(" ");
										}
										if(zjMap.get(String.valueOf(ttf))!=null){
											int yy1=Integer.parseInt((String)zjMap.get(String.valueOf(ttf)));
											if(context.trim().length()==0){
												yy1=yy1+0;
											}else{
												yy1=yy1+Integer.parseInt(context);
											}
											zjMap.put(String.valueOf(ttf), String.valueOf(yy1));
										}else{
											if(context.trim().length()==0){
												zjMap.put(String.valueOf(ttf), String.valueOf(0));
											}else{
												zjMap.put(String.valueOf(ttf), context);
											}
										}
										ttf++;
									}
									htmlContext.append("</td>");
								}
							}
						}
					dalenth=ttf;
					}
			}
			htmlContext.append("</tr>");
		}
		if(zjMap!=null&&zjMap.size()!=0){
			htmlContext.append("<tr valign='top' height='30'><td height='30' class='t_cell_locked common_background_color common_border_color' width='40px'  align='center' colspan='1' rowspan='1'>"+(rolnum+1)+"</td>");	
			htmlContext.append("<td height='30' class='t_cell_locked common_background_color common_border_color'  align='center' colspan='"+listfield2.size()+"' rowspan='1'>"+"总&nbsp&nbsp&nbsp&nbsp&nbsp计"+"</td>");
			for(int j=0;j<dalenth;j++)
			{
				String context="";										
				context=(String)zjMap.get(String.valueOf(j));
				if("0".equalsIgnoreCase(context)){
					context=" ";
				}
				htmlContext.append("<td   height='30' class='RecordRow' align='right'   colspan='");
				htmlContext.append(1);
				htmlContext.append("' rowspan='");
				htmlContext.append(1);
				htmlContext.append("' >");
				htmlContext.append("  "+context);
				htmlContext.append("</td>");
			}
			htmlContext.append("</tr>");
		}
		htmlContext.append("</table>");
//		htmlContext.append("</td>");
//		htmlContext.append("</tr>");
//		htmlContext.append("</table>");
		return htmlContext.toString();
	}
	public HashMap getLisMap() {
		return lisMap;
	}
	public void setLisMap(HashMap lisMap) {
		this.lisMap = lisMap;
	}
	public HashMap getCodeMap() {
		return codeMap;
	}
	public void setCodeMap(HashMap codeMap) {
		this.codeMap = codeMap;
	}
	public String getZpchanel() {
		return zpchanel;
	}
	public void setZpchanel(String zpchanel) {
		this.zpchanel = zpchanel;
	}
	

}
