package dev.debtcollection.invoices;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class InvoiceControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void registersAndReturnsInvoice() {
        var created = mvc.post().uri("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"debtor":"Jane Example","amount":150.00,"currency":"EUR"}
                        """)
                .exchange();

        assertThat(created).hasStatus(HttpStatus.CREATED);
        assertThat(created).bodyJson().extractingPath("$.status").isEqualTo("OPEN");

        var location = created.getResponse().getHeader("Location");
        assertThat(mvc.get().uri(location)).hasStatusOk()
                .bodyJson().extractingPath("$.debtor").isEqualTo("Jane Example");
    }

    @Test
    void rejectsInvalidRequestWithProblemDetails() {
        assertThat(mvc.post().uri("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"debtor":"Jane Example","amount":-1,"currency":"EUR"}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void returns404ForUnknownInvoice() {
        assertThat(mvc.get().uri("/invoices/00000000-0000-0000-0000-000000000000"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.title").isEqualTo("Invoice not found");
    }
}
