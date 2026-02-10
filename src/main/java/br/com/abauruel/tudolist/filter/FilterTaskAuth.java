package br.com.abauruel.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.abauruel.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if(!servletPath.startsWith("/tasks")) {
            filterChain.doFilter(request, response);
            return;
        }
        // recuperar usuario e senha
        var authorization = request.getHeader("Authorization");
        

        var decodedAuthorization = new String(Base64.getDecoder().decode(authorization.replace("Basic ", "")));
        String[] credentials = decodedAuthorization.split(":");
        String username = credentials[0];
        String password = credentials[1];
        

        var user = this.userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            response.sendError(401);
            return;
        }else {
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (!passwordVerify.verified) {
                System.out.println("Invalid password");
                response.sendError(401);
                return;
            }else {

            

                System.out.println("User authenticated");
                request.setAttribute("idUser", user.getId());
                System.out.println(request);
                
                
                filterChain.doFilter(request, response);
            }
        }


        
    }

    
    
}
