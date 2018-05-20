package org.ccguyka.docsascode.hello;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class GreetingsTest {

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
                    prettyPrint())
                .withResponseDefaults(
                    removeHeaders("Host", "Content-Length", "Date", "Transfer-Encoding"),
                    prettyPrint()))
            .build();
    }

    @Test
    public void greeting() {
        given(this.documentationSpec)
            .accept("application/json")
            .filter(document("greeting",
                responseFields(
                fieldWithPath("id").description("ID"),
                fieldWithPath("content").description("Content"))))
        .when()
            .port(this.port)
            .get("/greeting")

        .then()
            .assertThat()
            .statusCode(is(200));
    }

}
