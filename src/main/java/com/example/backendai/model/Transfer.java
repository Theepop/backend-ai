package com.example.backendai.model;

public class Transfer {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Integer amount;
    private String note;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }

    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
