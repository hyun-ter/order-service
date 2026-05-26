package com.example.orderservice.member.presentation;

import com.example.orderservice.common.exception.MemberNotFoundException;
import com.example.orderservice.member.application.MemberApplicationService;
import com.example.orderservice.member.application.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberApplicationService memberApplicationService;

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() throws Exception {
        MemberResponse mockResponse = MemberResponse.builder()
                .memberId(1L)
                .email("test@test.com")
                .name("테스터")
                .street("서울시 강남구")
                .detail("101호")
                .zipCode("12345")
                .createdAt(LocalDateTime.now())
                .build();
        given(memberApplicationService.register(any())).willReturn(mockResponse);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "test@test.com",
                                "name", "테스터",
                                "street", "서울시 강남구",
                                "detail", "101호",
                                "zipCode", "12345"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("회원가입 이메일 형식 오류 시 400 반환")
    void 회원가입_이메일형식오류_400() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "invalid-email",
                                "name", "테스터",
                                "street", "서울시",
                                "detail", "101호",
                                "zipCode", "12345"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 조회 성공")
    void 회원조회_성공() throws Exception {
        MemberResponse mockResponse = MemberResponse.builder()
                .memberId(1L)
                .email("test@test.com")
                .name("테스터")
                .street("서울시 강남구")
                .detail("101호")
                .zipCode("12345")
                .createdAt(LocalDateTime.now())
                .build();
        given(memberApplicationService.findById(1L)).willReturn(mockResponse);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L));
    }

    @Test
    @DisplayName("회원 조회 시 없으면 404 반환")
    void 회원조회_없으면_404() throws Exception {
        given(memberApplicationService.findById(999L))
                .willThrow(new MemberNotFoundException(999L));

        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound());
    }
}
