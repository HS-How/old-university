package com.npc.old_school.exception;

import lombok.Data;

@Data
public class ResultResponse {
    private int resultCode;
    private String resultMessage;
    private Object result;

    public ResultResponse() {
    }

    public ResultResponse(BaseErrorInfomation errorInfomation) {
        this.resultCode = errorInfomation.getResultCode();
        this.resultMessage = errorInfomation.getResultMessage();
    }

    public static ResultResponse success() {
        return success(null);
    }

    public static ResultResponse success(Object result) {
        ResultResponse response = new ResultResponse();
        response.setResultCode(ExceptionEnum.SUCCESS.getResultCode());
        response.setResultMessage(ExceptionEnum.SUCCESS.getResultMessage());
        response.setResult(result);
        return response;
    }

    public static ResultResponse error(BaseErrorInfomation errorInfomation) {
        ResultResponse response = new ResultResponse();
        response.setResultCode(errorInfomation.getResultCode());
        response.setResultMessage(errorInfomation.getResultMessage());
        response.setResult(null);
        return response;
    }

    public static ResultResponse error(String resultMessage) {
        ResultResponse response = new ResultResponse();
        response.setResultCode(-1);
        response.setResultMessage(resultMessage);
        response.setResult(null);
        return response;
    }

    public static ResultResponse error(BaseErrorInfomation errorInfomation, Object result) {
        ResultResponse response = new ResultResponse(errorInfomation);
        response.setResult(result);
        return response;
    }

    
}
