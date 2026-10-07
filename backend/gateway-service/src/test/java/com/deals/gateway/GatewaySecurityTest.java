package com.deals.gateway;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests the gateway's security rules. Requests that are REJECTED never get forwarded,
 * so these tests don't need the other services to be running.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GatewaySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createCoupon_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/coupons"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCoupon_asUser_returns403() throws Exception {
        // jwt() fakes a valid logged-in user, so we don't need a real token here
        mockMvc.perform(delete("/api/coupons/abc")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void savedCoupons_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/users/me/saved-coupons"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cors_allowsAngularApp() throws Exception {
        // A "preflight": the browser asks permission before the real request
        mockMvc.perform(options("/api/coupons")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void cors_blocksOtherWebsites() throws Exception {
        mockMvc.perform(options("/api/coupons")
                        .header("Origin", "http://evil-site.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
