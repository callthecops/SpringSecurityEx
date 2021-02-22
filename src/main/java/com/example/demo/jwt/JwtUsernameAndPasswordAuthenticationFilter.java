package com.example.demo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

//The job of this class is to verify the credentials.Spring security does this by default
// but we can overrride and have our own implementation.
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    //first thing we want to do when we override the method is grab the username and password sent by the client.
    //We do that with request.getInputStream.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            UserNameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UserNameAndPasswordAuthenticationRequest.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()
            );
            //this checks if username exists and if it exist it will check if the password is correct or not.
            //Ife everything is correct the request will be authenticated.
            Authentication authenticate = authenticationManager.authenticate(authentication);
            return authenticate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Jwt Filter and Succesfull authentication.Here we need to generate a token and send it to the client.
    //This method is invoked after the attemptAuthentication and if it is succesffull.
    //Request filters explained:
    //When we have a request, that request needs to reach a destination.In this case the destination is the api
    //Filters are the objects that lay between the request and the api.We can have as many filters as we want.
    //The order is not guaranteed.JwtUsernameAndPasswordAuthenticationFilter is a filter.By default spring gives
    //us the implementation.Filters are classes that allow us to perform validations on our requests.And we can
    //either proceed to the next filter or reject the requests.Next we configure a filter in ApplicationSecurityConfig.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //Inside this method we create a token.

        //This is the created token that contains all the configurations that we have set below.
        String token = Jwts.builder()
                //We are geting the name of the authentication result.This is linda/tom/annasmith.
                .setSubject(authResult.getName())
                //this lets us specify the body/payload
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                //This token expires in 2 weeks.
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                //This is how we sign the token with a sha256 encoder.The argument is the key that will be used
                //during hashing.
                .signWith(Keys.hmacShaKeyFor("securesecuresecuresecuresecure".getBytes()))
                .compact();


        //after the token has been created we have to send it back to the client. To do that we have to add the token
        // to the response header object.

        response.addHeader("Authorisation", "Bearer " + token);

    }
}
