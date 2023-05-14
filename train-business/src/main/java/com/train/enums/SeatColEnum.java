package com.train.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum SeatColEnum {

    YDZ_A("A", "A", "02"),
    YDZ_C("C", "C", "02"),
    YDZ_D("D", "D", "02"),
    YDZ_F("F", "F", "02"),
    EDZ_A("A", "A", "03"),
    EDZ_B("B", "B", "03"),
    EDZ_C("C", "C", "03"),
    EDZ_D("D", "D", "03"),
    EDZ_F("F", "F", "03");

    private String code;

    private String desc;

    /**
     * 对应SeatTypeEnum.code
     */
    private String type;

    SeatColEnum(String code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 根据车箱的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
     */
    public static List<SeatColEnum> getColsByType(String seatType) {
        List<SeatColEnum> colList = new ArrayList<>();
        EnumSet<SeatColEnum> seatColEnums = EnumSet.allOf(SeatColEnum.class);
        for (SeatColEnum anEnum : seatColEnums) {
            if (anEnum.getType().equals(seatType)) {
                colList.add(anEnum);
            }
        }
        return colList;
    }

}
