package me.jamesj.http.library.body;

import me.jamesj.http.library.parameters.v2.Source;

public interface Body {
    
    Source.Result get(String key);
    
    int length();
    
    
}
