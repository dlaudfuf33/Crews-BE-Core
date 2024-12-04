package org.baas.baascore.trace.logtrace;


import org.baas.baascore.trace.template.TraceStatus;

public interface LogTrace {

	TraceStatus begin(String message);

	void end(TraceStatus status);

	void exception(TraceStatus status, Exception e);
}
