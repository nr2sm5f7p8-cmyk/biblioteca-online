package it.biblioteca.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
        "/admin/*",
        "/tecnico/*"
})
public class RoleFilter implements Filter {

    private static final int ADMIN = 1;
    private static final int SERVIZIO_TECNICO = 3;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        HttpSession session =
                httpRequest.getSession(false);

        if (session == null) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        Integer idRuolo =
                (Integer) session.getAttribute("idRuolo");

        String uri = httpRequest.getRequestURI();

        if (!ruoloAutorizzato(uri, idRuolo)) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/home.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean ruoloAutorizzato(String uri,
                                     Integer idRuolo) {

        if (idRuolo == null) {
            return false;
        }

        if (uri.contains("/admin/")) {
            return idRuolo == ADMIN;
        }

        if (uri.contains("/tecnico/")) {
            return idRuolo == SERVIZIO_TECNICO;
        }

        return false;
    }
}