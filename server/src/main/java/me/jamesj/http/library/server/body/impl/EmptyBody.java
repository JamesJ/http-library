package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.parameters.Source;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 11/12/2021
 */

public class EmptyBody extends AbstractRequestBody {
    public EmptyBody() {
        super(new HashMap<>());
    }

}
