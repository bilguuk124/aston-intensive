package com.aston.dz2.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/students")
public class StudentController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")){

        }
    }
}
