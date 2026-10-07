package com.deals.coupon.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ONE clock for the whole service, in the business's time zone.
 *
 * LocalDate.now() alone uses the computer's time zone: India on your PC, but UTC inside a
 * Docker container (Phase 9). Then "today" would change at 5:30 AM India time instead of
 * midnight, and coupons would expire 5.5 hours late. Asking this clock avoids that.
 * Tests can also replace it with a fixed clock, so "today" is always the same date.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock(@Value("${app.time-zone}") ZoneId timeZone) {
        return Clock.system(timeZone);
    }
}
