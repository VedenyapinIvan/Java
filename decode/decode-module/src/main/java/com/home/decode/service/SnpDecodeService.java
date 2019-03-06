package com.home.decode.service;

import com.home.DecodeApi.DecodeApiGrpc;
import com.home.DecodeApi.DecodeServiceRq;
import com.home.DecodeApi.DecodeServiceRs;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GRpcService
public class SnpDecodeService extends DecodeApiGrpc.DecodeApiImplBase {
    private final static Logger logger = LoggerFactory.getLogger(SnpDecodeService.class);

    public SnpDecodeService() {
    }

    @Override
    public void getDecodeValue(DecodeServiceRq request, StreamObserver<DecodeServiceRs> responseObserver) {
        logger.info(">>> REQUEST: Got request");
        String question = request.getQuestion();
    }

}
