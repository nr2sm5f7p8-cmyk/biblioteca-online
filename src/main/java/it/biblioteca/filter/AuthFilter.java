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
        "/home.jsp",
        "/libri",
        "/lista_libri.jsp",
        "/inserisci_libro.jsp",
        "/inserisci-libro",
        "/modifica-libro",
        "/modifica_libro.jsp",
        "/chat.jsp"
})
public class AuthFilter implements Filter {

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

        boolean autenticato =
                session != null
                && Boolean.TRUE.equals(
                        session.getAttribute("autenticato"));

        if (!autenticato) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}
