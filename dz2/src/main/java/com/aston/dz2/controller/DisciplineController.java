package com.aston.dz2.controller;

import com.aston.dz2.entity.Discipline;
import com.aston.dz2.entity.dto.DisciplineDto;
import com.aston.dz2.entity.dto.ScoreDto;
import com.aston.dz2.exception.BadRequestException;
import com.aston.dz2.exception.UrlNotFoundException;
import com.aston.dz2.service.DisciplineService;
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
import java.util.List;

@WebServlet("/api/disciplines/*")
public class DisciplineController extends HttpServlet {
    
    @Inject
    private DisciplineService disciplineService;
    
    @Inject
    private Gson gson;
    
    @Inject
    private Logger log;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ServletException {
        try{
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")){
                log.info("Got request to get all disciplines");
                List<Discipline> disciplines = disciplineService.getAll();
                String jsonResponse = gson.toJson(disciplines);
                sendResponse(response, jsonResponse);
            }
            else{
                String[] pathParams = getPathParams(pathInfo);
                if (pathParams.length == 1) {
                    log.info("Got request to get a disciplines by id");
                    Long disciplineId = Long.parseLong(pathParams[0]);
                    Discipline discipline = disciplineService.getById(disciplineId);
                    String jsonResponse = gson.toJson(discipline);
                    log.info("Response: {}", discipline);
                    sendResponse(response, jsonResponse);
                } else if (pathParams.length == 2 && pathParams[1].equalsIgnoreCase("scores")){
                    Long disciplineId = Long.parseLong(pathParams[0]);
                    log.info("Got request to get scores of discipline with id {}", disciplineId );
                    sendResponse(response, gson.toJson(disciplineService.getScoresInDiscipline(disciplineId)));
                }
                else{
                    throw new UrlNotFoundException("Not found ");
                }

            }
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws JsonSyntaxException, ServletException {
        try{
            log.info("Got request to create a new discipline");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams == null) {
                String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                log.info("RequestBody: {}", requestBody);
                DisciplineDto dto = gson.fromJson(requestBody, DisciplineDto.class);
                if (dto == null) throw new BadRequestException("Can't parse the request body to discipline dto");
                log.info("Discipline info: {}", dto);
                disciplineService.addDiscipline(dto);
                sendResponse(response, gson.toJson("Created Successfully"));
            }
            else if (pathParams.length == 2 && pathParams[1].equalsIgnoreCase("groups")){
                Long disciplineId = Long.parseLong(pathParams[0]);
                String groupIdStr = request.getParameter("group_id");
                log.info("Group id {}", groupIdStr);
                if (groupIdStr == null) {
                    throw new BadRequestException("Parameter group_id is missing!");
                }
                Long groupId = Long.parseLong(groupIdStr);
                log.info("Adding group with id {} is being added to discipline with id {}", groupId, disciplineId );
                disciplineService.addGroupToDiscipline(disciplineId, groupId);
                sendResponse(response, gson.toJson("Group added successfully"));
            }
            else{
                log.info("Url not found");
                throw new UrlNotFoundException("Not found");
            }

        } catch (Exception ex){
            throw new ServletException(ex);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, NumberFormatException {
        try{
            log.info("Got request to update the discipline");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams.length == 1){
                Long disciplineId = Long.parseLong(pathParams[0]);
                String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                DisciplineDto dto = gson.fromJson(requestBody, DisciplineDto.class);
                disciplineService.updateDiscipline(disciplineId, dto);
                sendResponse(response, gson.toJson("Updated discipline successfully"));
            }
            else if(pathParams.length == 2 && pathParams[1].equalsIgnoreCase("scores")){
                Long disciplineId = Long.parseLong(pathParams[0]);
                String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                ScoreDto dto = gson.fromJson(requestBody, ScoreDto.class);
                disciplineService.updateStudentScoreInDiscipline(dto.getStudentId(), disciplineId, dto.getScore());
                sendResponse(response, gson.toJson("Updated score successfully"));
            }
            else {
                throw new UrlNotFoundException("Url not found: " + pathInfo);
            }

        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try{
            log.info("Got request to delete discipline by id");
            String pathInfo = request.getPathInfo();
            String[] pathParams = getPathParams(pathInfo);
            if (pathParams.length == 2 && pathParams[1].equalsIgnoreCase("groups")) {
                Long disciplineId = Long.valueOf(pathParams[0]);
                String groupIdStr = request.getParameter("group_id");
                log.info("Group id {}", groupIdStr);
                if (groupIdStr == null) {
                    throw new BadRequestException("Parameter group_id is missing!");
                }
                Long groupId = Long.parseLong(groupIdStr);
                log.info("Deleting group with id {} from discipline with id {}", groupId, disciplineId);
                disciplineService.deleteGroupFromDiscipline(disciplineId, groupId);
            } else if (pathParams.length == 1){
                Long disciplineId = Long.valueOf(pathParams[0]);
                disciplineService.deleteDiscipline(disciplineId);
                sendResponse(response, gson.toJson("Successfully deleted"));
            }
            else{
                log.info("Url not found");
                throw new UrlNotFoundException("Not found");
            }
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
