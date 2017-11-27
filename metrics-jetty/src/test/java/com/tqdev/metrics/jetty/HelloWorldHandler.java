package com.tqdev.metrics.jetty;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HelloWorldHandler extends AbstractHandler {

	// TODO: Javadoc

	@Override
	public void handle(String path, Request request, final HttpServletRequest servletRequest,
			final HttpServletResponse servletResponse) throws IOException, ServletException {
		request.setHandled(true);
		switch (path) {
		case "/hello":
			servletResponse.setStatus(200);
			servletResponse.setContentType("text/plain");
			servletResponse.getWriter().write("Hello World!");
			break;
		case "/hello-async":
			final AsyncContext context = request.startAsync();
			Thread t = new Thread(() -> {
				servletResponse.setStatus(200);
				servletResponse.setContentType("text/plain");
				final ServletOutputStream outputStream;
				try {
					outputStream = servletResponse.getOutputStream();
					outputStream.setWriteListener(new WriteListener() {
						@Override
						public void onWritePossible() throws IOException {
							PrintWriter w = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
							w.write("Hello World!");
							w.close();
							context.complete();
						}

						@Override
						public void onError(Throwable throwable) {
							context.complete();
						}
					});
				} catch (IOException e) {
					context.complete();
				}
			});
			t.start();
			break;
		default:
			servletResponse.setStatus(404);
			servletResponse.setContentType("text/plain");
			servletResponse.getWriter().write("Not found.");
			break;
		}
	}

}