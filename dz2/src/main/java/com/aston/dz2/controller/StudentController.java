package com.aston.dz2.controller;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDisciplineScore;
import com.aston.dz2.entity.dto.StudentDto;
import com.aston.dz2.exception.BadRequestException;
import com.aston.dz2.exception.UrlNotFoundException;
import com.aston.dz2.service.DisciplineService;
import com.aston.dz2.service.StudentService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebServlet("/api/students/*")
public class StudentController extends HttpServlet {

    @Inject
    private StudentService studentService;
    @Inject
    private DisciplineService disciplineService;

    @Inject
    private Logger log;
    @Inject
    private Gson gson;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ServletException {
        try{
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")){
                log.info("Got request to get all students");
                List<Student> students = studentService.getAllStudents();
                String jsonResponse = gson.toJson(students);
                sendResponse(response, jsonResponse);
            }
            else{
                log.info("Got request to get a students by id");
                String[] pathParams = getPathParams(pathInfo);
                if (pathParams == null || pathParams.length < 1) {
                    if (pathParams != null){
                        log.error("Path was not found: {}, path params: {}", pathInfo, pathParams.length);
                        log.error("Path params: {}", Arrays.toString(pathParams));
                    }
                    throw new UrlNotFoundException("Not found ");
                }
                if (pathParams.length == 2){
                    if (pathParams[1].equalsIgnoreCase("scores")){
                        log.error("Not found: {}", pathInfo);
                        throw new UrlNotFoundException("Not found: " + pathInfo);
                    }
                }

                Long studentId = Long.parseLong(pathParams[0]);

                if (pathParams.length == 1){
                    Student student = studentService.getStudentById(studentId);
                    String jsonResponse = gson.toJson(student);
                    log.info("Response: {}", student);
                    sendResponse(response, jsonResponse);
                } else {
                    List<StudentDisciplineScore> scores = disciplineService.getStudentScores(studentId);
                    String jsonResponse = gson.toJson(scores);
                    log.info("Response: {}", Arrays.toString(scores.toArray()));
                    sendResponse(response, jsonResponse);
                }
            }
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws JsonSyntaxException, ServletException {
        try{
            log.info("Got request to create a new student");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams != null) {
                log.info("Url not found");
                throw new UrlNotFoundException("Not found");
            }
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            log.info("RequestBody: {}", requestBody);
            StudentDto dto = gson.fromJson(requestBody, StudentDto.class);
            if (dto == null) throw new BadRequestException("Can't parse the request body to student dto");
            log.info("Student info: {}", dto);
            studentService.addStudent(dto);
            sendResponse(response, gson.toJson("Created Successfully"));
        } catch (Exception ex){
            throw new ServletException(ex);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, NumberFormatException {
        try{
            log.info("Got request to update the student");
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")){
                log.error("Request doesn't include required path param: id");
                throw new BadRequestException("Request path must contain id param!");
            }
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams.length != 1) {
                log.error("Number of path params: {}, required: 1", pathParams.length);
                throw new UrlNotFoundException("Request includes more or less than required param");
            }
            Long studentId = Long.parseLong(pathParams[0]);
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            StudentDto dto = gson.fromJson(requestBody, StudentDto.class);
            studentService.updateStudent(studentId, dto);
            sendResponse(response, gson.toJson("Updated successfully"));
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try{
            log.info("Got request to delete student by id");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams.length != 1) {
                throw new UrlNotFoundException("Not found");
            }
            Long studentId = Long.valueOf(pathParams[0]);
            studentService.deleteStudent(studentId);
            sendResponse(response, gson.toJson("Successfully deleted"));
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println(message);
    }

    private String[] getPathParams(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")){
            return null;
        }
        String path = pathInfo.substring(1);
        return path.split("/");
    }

}
