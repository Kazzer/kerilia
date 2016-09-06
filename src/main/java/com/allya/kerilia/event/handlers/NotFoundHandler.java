package com.allya.kerilia.event.handlers;

import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.NOT_FOUND;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

@Singleton
public class NotFoundHandler extends AbstractHandler implements HttpHandler
{
    @Inject
    public NotFoundHandler()
    {
        super();
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange)
    {
        exchange.setStatusCode(NOT_FOUND).setPersistent(false);
        exchange.getResponseHeaders().put(CONTENT_TYPE, TEXT_PLAIN.getMimeType());
        exchange
            .getResponseSender()
            .send("The requested URL " + exchange.getRequestPath() + " was not found on this server.\n");
    }
}
