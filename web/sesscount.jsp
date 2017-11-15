<%@ page import="org.kodejava.example.servlet.SessionCounter" %>
<% 
    SessionCounter counter = (SessionCounter) session.getAttribute(
            SessionCounter.COUNTER); 
%>

<%= counter.getActiveSessionNumber() %>