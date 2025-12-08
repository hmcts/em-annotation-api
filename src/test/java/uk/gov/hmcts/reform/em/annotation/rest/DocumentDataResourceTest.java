package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.reform.em.annotation.service.DocumentDataService;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentDataResourceTest {

    @Mock
    private DocumentDataService documentDataService;
    private MockMvc mockMvc;
    private final UUID documentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        DocumentDataResource resource = new DocumentDataResource(documentDataService);
        mockMvc = MockMvcBuilders.standaloneSetup(resource).build();
    }

    @Test
    void shouldReturn204WhenDeleteIsSuccessful() throws Exception {
        mockMvc.perform(delete("/api/documents/{docId}/data", documentId))
            .andExpect(status().isNoContent());

        verify(documentDataService).deleteDocumentData(documentId);
    }
}