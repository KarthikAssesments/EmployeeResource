package com.paypal.bfs.test.employeeserv.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {

	private static final int MAX_PAYLOAD_SIZE = 500;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        OffsetDateTime createdAt = OffsetDateTime.now();
        try {
            logRequest(request, createdAt);
            filterChain.doFilter(request, response);
        } finally {
            updateLog(request, response, createdAt, startTime);
            response.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, OffsetDateTime createdAt) {
        try {
            log.info(getRequestMessage(request));
        } catch (Exception ex) {
            log.error("Error logging the request", ex);
        }
        
    }

    private void updateLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, OffsetDateTime createdAt, long startTime) {
        try {
            log.info(getResponseMessage(request, response, System.currentTimeMillis() - startTime));
        } catch (Exception ex) {
            log.error("Error logging the response", ex);
        }
    }

    private String getRequestMessage(ContentCachingRequestWrapper request) {
        String prefix = "Received Request [";
        String suffix = "]";

        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("httpMethod=").append(request.getMethod());

        msg.append("; uri=").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        String requestPayload = getPayload(request.getContentAsByteArray(), request.getCharacterEncoding());
        if (requestPayload != null) {
            msg.append("; requestPayload=").append(requestPayload.replaceAll("[\r\n]+", ""));
        }
        
        msg.append("; headers=").append(new ServletServerHttpRequest(request).getHeaders());

        msg.append(suffix);
        return msg.toString();
    }

    private String getResponseMessage(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long executionTime) {
        String prefix = "Response [";
        String suffix = "]";

        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("executionTimeInMillis=").append(executionTime);

        msg.append("; responseStatus=").append(response.getStatusCode());

        String requestPayload = getPayload(request.getContentAsByteArray(), request.getCharacterEncoding());
        if (requestPayload != null) {
            msg.append("; requestPayload=").append(requestPayload.replaceAll("[\r\n]+", ""));
        }

        String responsePayload = getPayload(response.getContentAsByteArray(), response.getCharacterEncoding());
        if (responsePayload != null) {
            msg.append("; responsePayload=").append(responsePayload.replaceAll("[\r\n]+", ""));
        }

        msg.append(suffix);
        return msg.toString();
    }

    private String getPayload(byte[] buf, String characterEncoding) {
        String payload = null;
        if (buf.length > 0) {
            int length = Math.min(buf.length, MAX_PAYLOAD_SIZE);
            try {
                payload = new String(buf, 0, length, characterEncoding);
            } catch (UnsupportedEncodingException ex) {
                payload = "[unknown]";
            }
        }
        return payload;
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
