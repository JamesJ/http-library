package me.jamesj.lambda.wrapper.core;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

public abstract class AbstractLambdaFunction implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPEvent> {
    @Override
    public APIGatewayV2HTTPEvent handleRequest(APIGatewayV2HTTPEvent event, Context context) {

        return null;
    }
}
