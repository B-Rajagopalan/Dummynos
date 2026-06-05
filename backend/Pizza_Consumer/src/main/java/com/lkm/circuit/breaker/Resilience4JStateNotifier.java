package com.lkm.circuit.breaker;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Component
public class Resilience4JStateNotifier {
	private static Logger LOG = LoggerFactory.getLogger(Resilience4JStateNotifier.class);

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	@PostConstruct
	private void postConstruct() {
		circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> circuitBreaker.getEventPublisher()
				.onStateTransition((event) -> LOG.info(event.toString())));
	}
}