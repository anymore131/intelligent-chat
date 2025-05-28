package cn.edu.zust.se.service;

import java.util.Map;

public interface CountServiceI {
    Map<String, Object> init(String type);
    void updateDailyCount();
    String getModelTimes();
}
