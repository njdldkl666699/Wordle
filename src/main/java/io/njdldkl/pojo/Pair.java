package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<F, S> {

    F first;
    S second;
}
