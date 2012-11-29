package com.thenetcircle.services.media.service.impl.requestdispatcher;

import akka.actor.UntypedActor;
import com.thenetcircle.services.media.service.RequestDispatcher;
import com.thenetcircle.services.media.service.RequestHandler;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class RequestProcessServiceActor extends UntypedActor implements RequestDispatcher {


    @Override
    public void dispatch(String request) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addProcessor(RequestHandler requestHandler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * To be implemented by concrete UntypedActor, this defines the behavior of the
     * UntypedActor.
     */
            @Override
            public void onReceive(Object message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
