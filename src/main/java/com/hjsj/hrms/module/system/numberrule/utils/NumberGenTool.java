/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/5/26 上午11:43
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;

/**
 * function：编号生成工具，
 * <p>
 * 编号规则：
 * 长度：5位码，第一位为固定字符，大写字母A-Z，如果到达9999位时首字母顺延。
 * 类型：字符串。
 * 规则：后四位采用数字编码，递增的方式增加。保证编号唯一不重复，最后一个为Z9999。
 * 示例：A0001，A0002.....， B0001，B0002...，.....Z0001，Z9999
 * <p>
 * datetime：2020-05-26 11:43
 * author：warne
 */
public class NumberGenTool {
    private static Logger log = LoggerFactory.getLogger(NumberGenTool.class);

    static IdGen idGen = new IdGen(1, 1);
    public final static String ENCODING_CODE = "UTF-8"; //# 编码
    public final static String SEP = ","; //# 最大编号
    final static Integer MAX_NUMBER = 9999; //# 最大编号
    public final static Integer MAX_COUNT_PER = 100; //# 每一批次最大生成编号数
    final static List<String> CODE_LIST = Lists.newLinkedList(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    final static Map<String, Integer> CODES_INDEX_MAP = new HashMap<String, Integer>() {{
        for (int i = 0; i < CODE_LIST.size(); i++) {
            put(CODE_LIST.get(i), i);
        }
    }};

    volatile static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0000");//# 数字格式为4位长度

    /**
     * 获取下一个前缀
     *
     * @param code
     * @return
     */
    public static String getNextCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.error("gen number: code[{}] is empty ", code);
            throw new RuntimeException("code:[" + code + "] is empty ");
        }

        code = code.toUpperCase();
        if (!CODE_LIST.contains(code)) {
            log.error("gen number: code[{}] is not contains A-Z ,", code);
            throw new RuntimeException("code:[" + code + "] is not contains A-Z");
        }

        Integer index = CODES_INDEX_MAP.get(code) + 1;
        if (index == CODE_LIST.size()) {
            log.warn("gen number: four number is over, ");
            //index = 0;//# 如果到了Z,则循环回A继续
            //# 先关闭 - 夏翔 2020.05.18 21:58:58
            throw new NoOverException("编号已经使用完了！！！"); //# 不够时直接抛异常
        }

        String targetCode = CODE_LIST.get(index);
        return targetCode;
    }

    /**
     * 获取下一个批次编号
     *
     * @param oldLastNo 最新的编号
     * @param count     个数
     * @return
     */
    public static Pair<String, String> getNextPatchNo(String oldLastNo, int count) throws NoOverException {
        if (count > MAX_COUNT_PER) {
            //# 如果需求个数大于最大个数时，则按最大处理
            log.warn("gen number: count[{}] is greater than max_count[{}], so only gen number count: {}", count, MAX_COUNT_PER, MAX_COUNT_PER);
            count = MAX_COUNT_PER;
        }

        List<String> numberList = new ArrayList<>(count);

        String code = oldLastNo.substring(0, 1);
        Integer index = Integer.parseInt(oldLastNo.substring(1));

        Integer tempIndex = index + count;
        if (tempIndex <= MAX_NUMBER) {
            for (Integer i = index; i < tempIndex; i++) {
                numberList.add(code + formatIndex(i + 1));
            }
        } else {
            //# 如果编号超过最大，则应该顺延前缀，并从0001开始编号
            Integer oldIndex = MAX_NUMBER - index;
            Integer newIndex = count - oldIndex;

            //# 上一个前缀未用完的
            for (java.lang.Integer i = index; i < MAX_NUMBER; i++) {
                numberList.add(code + formatIndex(i + 1));
            }
            //# 新的前缀使用
            String nextCode = getNextCode(code);
            for (Integer i = 0; i < newIndex; i++) {
                numberList.add(nextCode + formatIndex(i + 1));
            }
        }

        String lastNo = numberList.get(count - 1);
        String result = String.join(SEP, numberList);

        return Pair.of(lastNo, result);
    }

    public static String getId() {
        return idGen.strId();
    }

    /**
     * 将数字格式为4位长度，不足时前面补充0
     *
     * @param index
     * @return
     */
    private static String formatIndex(Integer index) {
        return DECIMAL_FORMAT.format(index);
    }

}
