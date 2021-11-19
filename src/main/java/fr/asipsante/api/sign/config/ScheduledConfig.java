/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */

package fr.asipsante.api.sign.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Class ScheduledConfig.
 */
@Configuration
@EnableScheduling
@ComponentScan("fr.asipsante.api.sign.scheduled")
public class ScheduledConfig {
}
