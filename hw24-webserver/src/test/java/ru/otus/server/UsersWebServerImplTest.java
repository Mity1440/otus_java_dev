package ru.otus.server;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static ru.otus.server.utils.WebServerHelper.*;

@DisplayName("Тест сервера должен ")
class UsersWebServerImplTest {

    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String LOGIN_URL = "login";
    private static final String API_CLIENT_URL = "api/client";

    private static final String DEFAULT_USER_LOGIN = "user1";
    private static final String DEFAULT_USER_PASSWORD = "11111";
    private static final String INCORRECT_USER_LOGIN = "BadUser";
    private static final String DEFAULT_CLIENT_FIELD_NAME = "name";
    private static final String DEFAULT_CLIENT_FIELD_VALUE= "Vasya";

    private static UsersWebServer webServer;
    private static HttpClient http;

    private static final DBServiceClient dbServiceClient = mock(DBServiceClient.class);

    @BeforeAll
    static void setUp() throws Exception {
        http = HttpClient.newHttpClient();

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);

        UserAuthService userAuthService = mock(UserAuthService.class);

        given(userAuthService.authenticate(DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD)).willReturn(true);
        given(userAuthService.authenticate(INCORRECT_USER_LOGIN, DEFAULT_USER_PASSWORD)).willReturn(false);
        given(dbServiceClient
                .findByField(DEFAULT_CLIENT_FIELD_NAME, DEFAULT_CLIENT_FIELD_VALUE))
                .willReturn(Collections.emptyList());

        webServer = new UsersWebServerWithFilterBasedSecurity(WEB_SERVER_PORT,
                                                              userAuthService,
                                                              dbServiceClient,
                                                              templateProcessor);
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        webServer.stop();
    }

    @DisplayName("возвращать 302 при запросе клиента, при любом запросе если не выполнен вход ")
    @Test
    void shouldReturnForbiddenStatusForUserRequestWhenUnauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_CLIENT_URL)))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    @DisplayName("возвращать ID сессии при выполнении входа с верными данными")
    @Test
    void shouldReturnJSessionIdWhenLoggingInWithCorrectData() throws Exception {
        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL), DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNotNull();
    }


    @DisplayName("должен создать клиента, когда авторизован и обращается к api создания")
    @Test
    void shouldCreateClientWhenAuthorized() throws Exception {

        HttpCookie jSessionIdCookie = login(buildUrl(WEB_SERVER_URL, LOGIN_URL), DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD);
        assertThat(jSessionIdCookie).isNotNull();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.noBody())
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .uri(URI.create(String.format("%s?name=%s", buildUrl(WEB_SERVER_URL, API_CLIENT_URL), DEFAULT_CLIENT_FIELD_VALUE)))
                .setHeader(COOKIE_HEADER, String.format("%s=%s", jSessionIdCookie.getName(), jSessionIdCookie.getValue()))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        Mockito.verify(dbServiceClient, Mockito.times(1)).saveClient(Mockito.any());
        Mockito.verify(dbServiceClient, Mockito.times(1)).findByField(Mockito.any(), Mockito.any());

    }
}