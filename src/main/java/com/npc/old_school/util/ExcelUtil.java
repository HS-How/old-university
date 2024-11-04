package com.npc.old_school.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtil {
    
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> list = new ArrayList<>();
        EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                list.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel解析完成，共{}条数据", list.size());
            }
        }).sheet().doRead();
        return list;
    }
} 