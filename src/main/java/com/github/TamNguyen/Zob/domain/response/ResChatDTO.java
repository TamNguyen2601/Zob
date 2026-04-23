package com.github.TamNguyen.Zob.domain.response;

import java.time.Instant;

public class ResChatDTO {

    private String reply;
    private String model;
    private Instant timestamp;

    public ResChatDTO(String reply, String model) {
        this.reply = reply;
        this.model = model;
        this.timestamp = Instant.now();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
