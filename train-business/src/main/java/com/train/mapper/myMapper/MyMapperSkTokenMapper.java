package com.train.mapper.myMapper;

import java.util.Date;

public interface MyMapperSkTokenMapper {

    int decrease(Date date, String trainCode, int decreaseCount);
}
