package de.aero.recovery.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan("de.aero.recovery.jpa")
@EnableJpaRepositories("de.aero.recovery.jpa")
class RecoveryJpaConfig