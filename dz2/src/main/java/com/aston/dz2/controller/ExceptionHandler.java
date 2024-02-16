package com.aston.dz2.controller;

import com.aston.dz2.entity.ErrorBody;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.time.LocalDateTime;

@WebServlet("/ExceptionHandler")
public class ExceptionHandler extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    private Logger logger;
    @Inject
    private Gson gson;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        processError(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        processError(request, response);
    }

    private void processError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        String errorDetails = String.valueOf(request.getAttribute("jakarta.servlet.error.message"));
        logger.info("Thrown: " + throwable.getClass().getSimpleName());
        logger.info("Error message: {}" , errorDetails);

        int errorCode = getErrorCode(throwable);
        logger.info("Error code: {}", errorCode);
        response.setStatus(errorCode);

        ErrorBody error = ErrorBody.builder()
                .errorCode(errorCode)
                .message(convertCamelCaseToWords(throwable.getClass().getSimpleName()))
                .details(exceptionMessageConverter(errorDetails))
                .timestamp(LocalDateTime.now())
                .build();

        PrintWriter printWriter = response.getWriter();
        String message = gson.toJson(error);
        printWriter.write(message);
    }

    private static int getErrorCode(Throwable throwable) {
        int errorCode;
        switch (throwable.getClass().getSimpleName()){
            case "BadRequestException","LevelNotFoundException","SemesterNotFoundException","TeacherRankNotFoundException", "NumberFormatException" -> errorCode = 400;
            case "DepartmentDoesNotExistException", "DisciplineNotFoundException", "GroupDoesNotExistsException", "StudentDoesNotExistsException", "TeacherNotFoundException", "UrlNotFoundException" -> errorCode = 404;
            default -> errorCode = 500;
        }
        return errorCode;
    }

    private static String exceptionMessageConverter(String exceptionMessage){
        if (exceptionMessage == null || exceptionMessage.isEmpty()){
            return exceptionMessage;
        }

        Integer indexOfDoubleDots = null;
        for (int i = 0; i < exceptionMessage.length(); i++){
            if (exceptionMessage.charAt(i) == ':'){
                indexOfDoubleDots = i+1;
                break;
            }
        }

        if (indexOfDoubleDots != null){
            return exceptionMessage.substring(indexOfDoubleDots);
        }

        return exceptionMessage;
    }

    private static String convertCamelCaseToWords(String camelCaseString) {
        if (camelCaseString == null || camelCaseString.isEmpty()) {
            return camelCaseString;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(camelCaseString.charAt(0)));

        for (int i = 1; i < camelCaseString.length(); i++) {
            char currentChar = camelCaseString.charAt(i);
            if (i + 9 <= camelCaseString.length() &&
                    camelCaseString.substring(i, i + 9).equalsIgnoreCase("Exception")) {
                i += 8;  // Skip the remaining characters in "Exception"
            } else if (Character.isUpperCase(currentChar)) {
                result.append(' ').append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }
}
