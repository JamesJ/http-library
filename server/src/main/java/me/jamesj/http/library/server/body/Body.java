package me.jamesj.http.library.server.body;

import me.jamesj.http.library.server.parameters.v2.Source;

public interface Body {
    
    Source.Result get(String key);
    
    int length();
    
    
}
