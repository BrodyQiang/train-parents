package com.train.mapper.myMapper;

import java.util.Date;

public interface MyMapperConfirmOrderMapper {

    void updateCountBySell(Date date
            , String trainCode
            , String seatTypeCode
            , Integer minStartIndex
            , Integer maxStartIndex
            , Integer minEndIndex
            , Integer maxEndIndex);

}