package com.aston.dz2.filter;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.io.IOException;

@WebFilter(
        urlPatterns = "/*",
        initParams = {
                @WebInitParam(name="encoding", value="UTF-8")
        }
)
public class CharacterEncodingFilter extends HttpFilter {

    @Inject
    private Logger logger;
    private String DEFAULT_ENCODING;

    @Override
    public void init(){
        DEFAULT_ENCODING = getInitParameter("encoding");
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        logger.info("Filtering: adding character encoding UTF-8");
        req.setCharacterEncoding(DEFAULT_ENCODING);
        res.setCharacterEncoding(DEFAULT_ENCODING);
        super.doFilter(req, res, chain);
    }
}
