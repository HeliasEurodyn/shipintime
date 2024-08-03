package com.enershare.dto.apifon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsRequest {

    private Message message;
    private Subscriber[] subscribers;
}
