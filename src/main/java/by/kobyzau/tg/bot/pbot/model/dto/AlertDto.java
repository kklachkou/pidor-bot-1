package by.kobyzau.tg.bot.pbot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlertDto extends SerializableInlineObject {

    @JsonProperty("a")
    private Boolean alert;
    @JsonProperty("c")
    private Integer cache;
    @JsonProperty("m")
    private String message;
    public AlertDto() {
        super(SerializableInlineType.ALERT);
    }

    public AlertDto(String message, boolean alert, Integer cache) {
        this();
        this.alert = alert;
        this.cache = cache;
        this.message = message;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCache() {
        return cache;
    }

    public void setCache(Integer cache) {
        this.cache = cache;
    }
}
