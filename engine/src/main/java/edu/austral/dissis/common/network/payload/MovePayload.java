package edu.austral.dissis.common.network.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovePayload(
    @JsonProperty("fromRow") int fromRow,
    @JsonProperty("fromCol") int fromCol,
    @JsonProperty("toRow") int toRow,
    @JsonProperty("toCol") int toCol) {}
