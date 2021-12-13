package me.jamesj.http.library.server.body.impl;

import me.jamesj.http.library.server.body.Body;
import me.jamesj.http.library.server.parameters.Source;

/**
 * Created by James on 11/12/2021
 */

public class EmptyBody implements Body {
    @Override
    public Source.Result get(String key) {
        return null;
    }
    
    @Override
    public int length() {
        return 0;
    }
}
