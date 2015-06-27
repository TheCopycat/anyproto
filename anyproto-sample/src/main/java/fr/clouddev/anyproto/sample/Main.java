package fr.clouddev.anyproto.sample;

import fr.clouddev.anyproto.sample.proto.Jhipster;
import fr.clouddev.protobuf.converter.AnyProtoConverter;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * Created by CopyCat on 19/06/15.
 */
public class Main {

    private interface AccountService {
        @GET("/api/account")
        Jhipster.Account getAccount();

        @POST("/api/authenticate")
        Jhipster.Auth authenticate(@Query("username") String username, @Query("password") String password);

        @GET("/api/users")
        List<Jhipster.Account> getUsers();

        @GET("/api/logs")
        List<Jhipster.Log> getLogs();

        @PUT("/api/logs")
        Jhipster.Log putLogs(@Body Jhipster.Log log);
    }

    private static class TokenInterceptor implements RequestInterceptor {
        protected String token;
        @Override
        public void intercept(RequestFacade request) {
            if (token!=null) {
                request.addHeader("X-Auth-Token",token);
            }
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
    public static void main(String[] args) {
        TokenInterceptor interceptor = new TokenInterceptor();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://localhost:8080")
                .setConverter(new AnyProtoConverter())
                .setRequestInterceptor(interceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        AccountService service = adapter.create(AccountService.class);


        Jhipster.Auth auth = service.authenticate("admin","admin");
        interceptor.setToken(auth.getToken());
        System.out.println("auth : "+auth.toString());
        Jhipster.Account acc = service.getAccount();
        System.out.println("acc : "+acc.toString());

        List<Jhipster.Account> users = service.getUsers();
        System.out.println("User list");
        for (Jhipster.Account user : users) {
            System.out.println("user : "+user);
        }

        List<Jhipster.Log> logs = service.getLogs();
        System.out.println("Log list : ");
        for (Jhipster.Log log : logs) {
            System.out.println("log : "+log);
        }

        Jhipster.Log log = Jhipster.Log.newBuilder().setName("fr.clouddev").setLevel("DEBUG").build();
        Jhipster.Log resultLog = service.putLogs(log);
        System.out.println("Result log : " + resultLog);
    }
}
