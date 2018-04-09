package org.ccguyka.docsascode.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.modifyUris;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private RequestSpecification documentationSpec;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        this.documentationSpec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(this.restDocumentation)
            .operationPreprocessors()
                .withRequestDefaults(
                    modifyUris().scheme("http").host("localhost").port(8080),
                    removeHeaders("Host", "Content-Length", "Date", "Transfer-Encoding"),
                    prettyPrint())
                .withResponseDefaults(
                    removeHeaders("Host", "Content-Length", "Date", "Transfer-Encoding"),
                    prettyPrint()))
            .build();
    }

    @Test
    public void createUser() {
        given(this.documentationSpec)
            .accept("application/json")
            .contentType("application/json")
            .filter(document("user/create",
                requestFields(
                    fieldWithPath("firstName").description("firstName"),
                   fieldWithPath("lastName").description("lastName"))))
            .log().all()

        .when()
            .port(this.port)
            .body("{\"firstName\":\"Tom\",\"lastName\":\"Sawyer\"}")
            .post("/users")

        .then().log().all()
            .assertThat()
            .statusCode(is(201));
    }

    @Test
    public void getUser() {
        UUID userId = UUID.randomUUID();
        UserController.USERS.put(userId, new User("Hans", "Meyer"));
        given(this.documentationSpec)
            .accept("application/json")
            .contentType("application/json")
            .filter(document("user/get",
                pathParameters(RequestDocumentation.parameterWithName("userId").description("ID of user")),
                responseFields(
                    fieldWithPath("firstName").description("firstName"),
                    fieldWithPath("lastName").description("lastName"))))
            .log().all()

        .when()
            .port(this.port)
            .get("/users/{userId}", userId)

        .then().log().all()
            .assertThat()
            .statusCode(is(200));
    }

    @Test
    public void updateUser() {
        UUID userId = UUID.randomUUID();
        UserController.USERS.put(userId, new User("Hans", "Meyer"));
        given(this.documentationSpec)
            .accept("application/json")
            .contentType("application/json")
            .filter(document("user/update",
                pathParameters(RequestDocumentation.parameterWithName("userId").description("ID of user")),
                requestFields(
                    fieldWithPath("firstName").description("firstName"),
                    fieldWithPath("lastName").description("lastName")),
                responseFields(
                    fieldWithPath("firstName").description("firstName"),
                    fieldWithPath("lastName").description("lastName"))))
            .log().all()

        .when()
            .port(this.port)
            .body("{\"firstName\":\"Tom\",\"lastName\":\"Sawyer\"}")
            .put("/users/{userId}", userId)

        .then().log().all()
            .assertThat()
            .statusCode(is(200));
    }
}
