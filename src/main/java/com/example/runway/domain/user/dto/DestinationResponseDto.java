package com.example.runway.domain.user.dto;

public record DestinationResponseDto(
        String destination,
        String regionImageUrl
) {
    public static DestinationResponseDto of(String destination, String regionImageUrl) {
        return new DestinationResponseDto(destination, regionImageUrl);
    }
}
