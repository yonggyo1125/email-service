package org.koreait.global.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(timeToLive=300) // 5분간 값 유지
public class CodeValue<T> {

    @Id
    private String code;
    private T value;
}
