package com.example.backendai;

public class TransferRequest {
    private Long toUserId;
    private String toMemberCode;
    private Integer amount;
    private String note;

    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }

    public String getToMemberCode() { return toMemberCode; }
    public void setToMemberCode(String toMemberCode) { this.toMemberCode = toMemberCode; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
