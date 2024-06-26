/*
 * ==========================================================================
 * Copyright (C) 2019-2022 HCL America, Inc. ( http://www.hcl.com/ )
 *                            All rights reserved.
 * ==========================================================================
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may
 * not use this file except in compliance with the License.  You may obtain a
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.
 *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the  specific language  governing permissions  and limitations
 * under the License.
 * ==========================================================================
 */
package com.hcl.domino.jnx.example.resources;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hcl.domino.DominoClient;
import com.hcl.domino.DominoClientBuilder;
import com.hcl.domino.admin.IConsoleLine;
import com.hcl.domino.admin.ServerAdmin.ConsoleHandler;
import com.hcl.domino.jnx.example.NotesRuntimeListener;

import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

@Path("console")
public class LiveConsoleResource {
  private static class SseConsoleHandler implements ConsoleHandler, AutoCloseable {
    private final Sse sse;
    private final SseBroadcaster sseBroadcaster;
    private final long opened = System.currentTimeMillis();
    private boolean shouldStop;

    public SseConsoleHandler(final Sse sse, final SseBroadcaster sseBroadcaster) {
      this.sse = sse;
      this.sseBroadcaster = sseBroadcaster;
      this.sseBroadcaster.onClose(sink -> this.close());
    }

    @Override
    public void close() {
      this.shouldStop = true;
    }

    @Override
    public void messageReceived(final IConsoleLine line) {
      final UUID eventId = UUID.randomUUID();

      final String json = JsonbBuilder.create().toJson(line);

      this.sseBroadcaster.broadcast(this.sse.newEventBuilder()
          .name("logline") //$NON-NLS-1$
          .id(eventId.toString())
          .mediaType(MediaType.APPLICATION_JSON_TYPE)
          .data(String.class, json)
          .reconnectDelay(250)
          .build());
    }

    @Override
    public boolean shouldStop() {
      if (this.shouldStop || Thread.currentThread().isInterrupted()) {
        return true;
      }
      final boolean timedOut = System.currentTimeMillis() > this.opened + TimeUnit.HOURS.toMillis(1);
      if (timedOut) {
        return true;
      }
      return false;
    }

  }

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path("{serverName}")
  public void getConsoleOutput(@Context final SseEventSink sseEventSink, @Context final Sse sse,
      @PathParam("serverName") final String serverName) {
    NotesRuntimeListener.executor.submit(() -> {
      try (SseBroadcaster broadcaster = sse.newBroadcaster(); SseConsoleHandler handler = new SseConsoleHandler(sse, broadcaster)) {
        broadcaster.register(sseEventSink);
        try (DominoClient dominoClient = DominoClientBuilder.newDominoClient().build()) {
          dominoClient.getServerAdmin().openServerConsole(serverName, handler);
        } catch (final Throwable t) {
          t.printStackTrace();
        } finally {
          sseEventSink.close();
        }
      }
    });
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_PLAIN)
  public String sendCommand(@FormParam("serverName") final String serverName, @FormParam("command") final String command)
      throws InterruptedException, ExecutionException {
    return NotesRuntimeListener.executor.submit(() -> {
      try (DominoClient dominoClient = DominoClientBuilder.newDominoClient().build()) {
        return dominoClient.getServerAdmin().sendConsoleCommand(serverName, command);
      }
    }).get();
  }
}
