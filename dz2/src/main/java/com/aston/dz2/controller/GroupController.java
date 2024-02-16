package com.aston.dz2.controller;

import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.dto.GroupDto;
import com.aston.dz2.exception.BadRequestException;
import com.aston.dz2.exception.UrlNotFoundException;
import com.aston.dz2.service.GroupService;
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

@WebServlet("/api/groups/*")
public class GroupController extends HttpServlet {
    
    @Inject
    private GroupService groupService;
    
    @Inject
    private Logger log;
    
    @Inject
    private Gson gson;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ServletException {
        try{
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")){
                log.info("Got request to get all groups");
                List<Group> groups = groupService.getAll();
                String jsonResponse = gson.toJson(groups);
                sendResponse(response, jsonResponse);
            }
            else{
                log.info("Got request to get a groups by id");
                String[] pathParams = getPathParams(pathInfo);
                if (pathParams == null || pathParams.length != 1) {
                    if (pathParams != null){
                        log.error("Path was not found: {}, path params: {}", pathInfo, pathParams.length);
                        log.error("Path params: {}", Arrays.toString(pathParams));
                    }
                    throw new UrlNotFoundException("Not found ");
                }
                Long groupId = Long.parseLong(pathParams[0]);
                Group group = groupService.getById(groupId);
                String jsonResponse = gson.toJson(group);
                log.info("Response: {}", group);
                sendResponse(response, jsonResponse);
            }
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws JsonSyntaxException, ServletException {
        try{
            log.info("Got request to create a new group");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams == null) {
                String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                log.info("RequestBody: {}", requestBody);
                GroupDto dto = gson.fromJson(requestBody, GroupDto.class);
                if (dto == null) throw new BadRequestException("Can't parse the request body to group dto");
                log.info("Group info: {}", dto);
                groupService.addGroup(dto);
                sendResponse(response, gson.toJson("Created Successfully"));
            } else if (pathParams.length == 1){
                Long groupId = Long.parseLong(pathParams[0]);
                log.info("Adding student to group with id: {}", groupId);
                String studentIdStr = request.getParameter("student_id");
                if (studentIdStr == null){
                    throw new BadRequestException("Parameter was not found: student_id");
                }
                Long studentId = Long.parseLong(studentIdStr);
                log.info("Student to add has id: {}", studentId);
                groupService.addStudentToGroup(groupId, studentId);
                sendResponse(response, gson.toJson("Added successfully"));
            } else {
                log.error("Url not found " + pathInfo);
                throw new UrlNotFoundException("Url not found " + pathInfo);
            }

        } catch (Exception ex){
            throw new ServletException(ex);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, NumberFormatException {
        try{
            log.info("Got request to update the group");
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
            Long groupId = Long.parseLong(pathParams[0]);
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            GroupDto dto = gson.fromJson(requestBody, GroupDto.class);
            groupService.updateGroup(groupId, dto);
            sendResponse(response, gson.toJson("Updated successfully"));
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try{
            log.info("Got request to delete group by id");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams.length != 1) {
                throw new UrlNotFoundException("Not found");
            }
            Long groupId = Long.parseLong(pathParams[0]);
            groupService.deleteGroup(groupId);
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
