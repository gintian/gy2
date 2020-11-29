package com.hjsj.hrms.businessobject.train.trainexam.question.questiones;

import java.util.*;

public class OrganizeBo {
	TreeMap map = new TreeMap();
	HashMap value = new HashMap();
	Random random = new Random();
    
    public OrganizeBo(ArrayList r5200list,ArrayList r5213list){
    	//map（分数=[list试题id]）
    	ArrayList list = null;
    	for (int i = 0; i < r5200list.size(); i++) {
			String tmp = String.valueOf(r5213list.get(i));
			if(map.get(tmp)!=null){
				list = (ArrayList) map.get(tmp);
				list.add(r5200list.get(i));
				map.put(tmp, list);
			}else{
				list = new ArrayList();
				list.add(r5200list.get(i));
				map.put(tmp, list);
			}
		}
    }
    
    public void init(float sum,int num){
    	StringBuffer buffer = new StringBuffer();
    	ArrayList tmplist = null;
    	float highest = (float) (sum+0.5-num/2.0);//最高分值 0.5为单位
    	Set set = map.keySet();
    	Iterator it = set.iterator();
    	while (it.hasNext()) {
			Object tmp = it.next();
			if(tmp==null) {
                continue;
            }
			if(Float.parseFloat(String.valueOf(tmp))<=highest){
				tmplist = (ArrayList) map.get(tmp);
				if(tmplist==null) {
                    continue;
                }
				if(tmplist.size()>num){
					for(int i=0;i<num;i++) {
                        buffer.append(tmp+",");
                    }
				}else{
					for(int i=0;i<tmplist.size();i++) {
                        buffer.append(tmp+",");
                    }
				}
			}//end for if
		}//end for while
    	if(buffer.length()>0) {
            buffer.setLength(buffer.length()-1);
        }
    	
    	String[] str = buffer.toString().split(",");
    	float[] initValue = new float[str.length];//分数值数组
    	int[] tmpInt = new int[str.length];//01010编码
    	for (int i = 0; i < initValue.length; i++) {
    		initValue[i] = Float.parseFloat(str[i]);
    		if(i<num) {
                tmpInt[i]=1;
            } else {
                tmpInt[i]=0;
            }
		}
    	str=null;
    	
    	
    	/**
    	 * 可能存在问题
    	 */
    	boolean state = true;
    	while(state){
    		//判断值是否符合
    		float _sumT = 0f;
    		ArrayList array = new ArrayList();
    		for (int i = 0; i < tmpInt.length; i++) {
				if(tmpInt[i]==1){
					_sumT+=initValue[i];
					
					ArrayList list = (ArrayList) map.get(String.valueOf(initValue[i]));
					array.add(list.get(random.nextInt(list.size())));
				}
			}
    		if(_sumT == sum){
    			value.put(String.valueOf(value.size()), array);
    		}
    		array = null;
    		
    		//替换0101编码
    		int tmp = 0;
    		for (int i = 0; i < tmpInt.length-1; i++) {
    			if(tmpInt[i]==0){//前方有多少0
    				tmp++;
    			}
				if(tmpInt[i]==1&&tmpInt[i+1]==0){
					int f = tmpInt[i];
					tmpInt[i] = tmpInt[i+1];
					tmpInt[i+1] = f;
					if(tmp>0){
						for (int j = 0; j < tmp; j++) {
							int t = tmpInt[i-j-1];
							tmpInt[i-j-1] = tmpInt[j];
							tmpInt[j] = t;
						}
					}
					break;
				}
				
			}
    		if(tmpInt.length-num==tmp) {
                state=false;
            }
    	}
    	
    }
    
    public ArrayList getList(){
    	if(value.size()<1) {
            return null;
        } else{
	    	int tmp = random.nextInt(value.size());
	    	return (ArrayList) value.get(String.valueOf(tmp));
    	}
    }
    
    public static void main(String[] args) {
    	Random random = new Random();
    	System.out.println(random.nextInt(1));
	}
}
