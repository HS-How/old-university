package com.npc.old_school.exception;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.web.HttpRequestMethodNotSupportedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultResponse> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(ResultResponse.error(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数错误: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.error(e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResultResponse> handleIOException(IOException e) {
        log.error("IO异常: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultResponse.error("文件操作失败"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 获取所有验证错误信息
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("参数验证失败: {}", errorMessage);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.error(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultResponse.error("未知的系统错误"));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ResultResponse> handleMultipartException(MultipartException e) {
        log.error("文件上传异常: ", e);
        if (e.getCause() instanceof ServletException 
                && e.getCause().getCause() instanceof InvalidContentTypeException) {
            return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ResultResponse.error("请求格式错误：需要 multipart/form-data 格式"));
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResultResponse.error("文件上传失败：" + e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResultResponse> handleMissingPartException(MissingServletRequestPartException e) {
        log.error("请求参数缺失: ", e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResultResponse.error("缺少必需的表单字段：" + e.getRequestPartName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        log.error("请求参数缺失: {}", paramName);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResultResponse.error(String.format("缺少必需的参数: %s", paramName)));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        String errorMessage = "请求的资源不存在：" + e.getResourcePath();
        log.error("资源未找到: {}", errorMessage);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorMessage);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResultResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据重复: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ResultResponse.error("数据已存在"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResultResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性违规: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResultResponse.error("数据操作违反完整性约束"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResultResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = String.format("不支持 %s 请求方法", e.getMethod());
        log.error("请求方法不支持: {}", message);
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ResultResponse.error(message));
    }
}
