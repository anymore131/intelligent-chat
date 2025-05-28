package cn.edu.zust.se.func;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Calculator implements Runnable{
    @JsonProperty("a")
    private int a;
    @JsonProperty("b")
    private int b;

    @Override
    public void run() {
        System.out.println(a + b);
    }
}
