package fr.clouddev.anyproto.sample;

import fr.clouddev.anyproto.sample.proto.Jhipster;
import fr.clouddev.protobuf.converter.AnyProtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.*;

import java.util.List;

/**
 * Created by CopyCat on 19/06/15.
 */
public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class);

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
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setLog(new RestAdapter.Log() {
                    Logger restLogger = LoggerFactory.getLogger("retrofit");
                    @Override
                    public void log(String message) {
                       restLogger.debug(message);
                    }
                })
                .build();
        AccountService service = adapter.create(AccountService.class);


        Jhipster.Auth auth = service.authenticate("admin","admin");
        interceptor.setToken(auth.getToken());
        logger.info("auth : {}", auth.toString());
        Jhipster.Account acc = service.getAccount();
        logger.info("acc : {}", acc.toString());

        List<Jhipster.Account> users = service.getUsers();
        logger.info("User list");
        for (Jhipster.Account user : users) {
            logger.info("user : {}", user);
        }

        Jhipster.Account user = service.getUser("anonymousUser");
        logger.info("User : {}", user);

        List<Jhipster.Log> logs = service.getLogs();
        logger.info("Log list : ");
        logger.info("Fetched {} logs.",logs.size());

        Jhipster.Log log = Jhipster.Log.newBuilder().setName("fr.clouddev").setLevel("DEBUG").build();
        Jhipster.Log resultLog = service.putLogs(log);
        logger.info("Result log : {}", resultLog);

        List<Jhipster.Audit> allAudits = service.getAllAudits();
        logger.info("audits : ");
        logger.info("fetched {} audits.",allAudits.size());

        List<Jhipster.Audit> auditsByDate = service.getAuditsByDate("2015-06-22","2015-06-28");
        logger.info("Audits By Date : ");
        logger.info("fetched {} audits.",auditsByDate.size());
    }
}
