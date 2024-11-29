package org.baas.baascore.trace.aspect;


import org.baas.baascore.trace.logtrace.LogTrace;
import org.baas.baascore.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AopConfig {
	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}

	@Bean
	public LogTraceAspect logTraceAspect(LogTrace logTrace) {
		return new LogTraceAspect(logTrace);
	}

}
