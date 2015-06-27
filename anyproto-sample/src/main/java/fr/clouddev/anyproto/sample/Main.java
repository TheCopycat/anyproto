package fr.clouddev.anyproto.sample;

import fr.clouddev.anyproto.sample.proto.Jhipster;
import fr.clouddev.protobuf.converter.AnyProtoConverter;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
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

        @GET("/api/users/{login}")
        Jhipster.Account getUser(@Path("login") String login);

        @GET("/api/logs")
        List<Jhipster.Log> getLogs();

        @PUT("/api/logs")
        Jhipster.Log putLogs(@Body Jhipster.Log log);

        @GET("/api/audits/all")
        List<Jhipster.Audit> getAllAudits();

        @GET("/api/audits/byDates")
        List<Jhipster.Audit> getAuditsByDate(@Query("fromDate")String from, @Query("toDate") String to);
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

        Jhipster.Account user = service.getUser("anonymousUser");
        System.out.println("User : "+user);

        List<Jhipster.Log> logs = service.getLogs();
        System.out.println("Log list : ");
        for (Jhipster.Log log : logs) {
            System.out.println("log : "+log);
        }

        Jhipster.Log log = Jhipster.Log.newBuilder().setName("fr.clouddev").setLevel("DEBUG").build();
        Jhipster.Log resultLog = service.putLogs(log);
        System.out.println("Result log : " + resultLog);

        List<Jhipster.Audit> allAudits = service.getAllAudits();
        System.out.println("audits : ");
        for (Jhipster.Audit audit : allAudits) {
            System.out.println("audit : "+audit);
            if (audit.hasData()) {
                System.out.println(audit.getData().getMessage());
            }
        }

        List<Jhipster.Audit> auditsByDate = service.getAuditsByDate("2015-06-22","2015-06-28");
        System.out.println("Audits By Date : ");
        for (Jhipster.Audit audit : auditsByDate) {
            System.out.println("audit : "+audit);
        }
    }
}
