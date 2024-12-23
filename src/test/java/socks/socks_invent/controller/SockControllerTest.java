package socks.socks_invent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import socks.socks_invent.model.Sock;
import socks.socks_invent.repository.SockRepository;
import socks.socks_invent.service.SockService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SockService sockService;

    @MockBean
    private SockRepository sockRepository;

    @Test
    public void testIncome() throws Exception {
        String sockJson = "{\"color\":\"red\",\"cottonPercentage\":50,\"quantity\":10}";

        mockMvc.perform(post("/api/socks/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sockJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testOutcome_sufficientSocks() throws Exception {
        Sock sock = new Sock("blue", 75, 5);
        Sock existingSock = new Sock("blue", 75, 10);

        when(sockRepository.findSockByColorAndCottonPercentage("blue", 75)).thenReturn(sock);

        mockMvc.perform(post("/api/socks/outcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sock)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSocks() throws Exception {
        when(sockService.getSocks(any(), any(), any(), any(), any())).thenReturn(5);

        mockMvc.perform(get("/api/socks")
                .param("color", "red")
                .param("operator", "equal")
                .param("cottonPercentage", "50"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    public void testUpdateSock() throws Exception {
        Sock sock = new Sock("red", 50, 10);
        sock.setId(1L);
        when(sockService.updateSock(1l, sock)).thenReturn(sock);

        mockMvc.perform(put("/api/socks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(sock)))
                .andExpect(status().isOk());
    }
}
