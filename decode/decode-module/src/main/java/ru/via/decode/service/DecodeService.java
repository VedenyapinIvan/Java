package ru.via.decode.service;

import io.grpc.stub.StreamObserver;
import io.opentracing.contrib.grpc.ServerTracingInterceptor;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.List;

@GRpcService
public class DecodeService extends DecodeApiGrpc.DecodeApiImplBase implements Answerable<DecodeServiceRs, Object>, Processable<DecodeServiceRq, DecodeServiceRs> {
    private static final Logger logger = LoggerFactory.getLogger(DecodeService.class);

    @Autowired
    private DecodeCache<DecodeRecord> cache;

    // Empty constructor
    public DecodeService() {
    }

    @Override
    public DecodeServiceRs processing(DecodeServiceRq request) {
        String msg = String.format("GET request = { Catalog: %s SourceSystem: %s TargetSystem: %s CodeValue: %s }",
                request.getCatalogName().name(), request.getSourceSystem().name(), request.getTargetSystem().name(),
                request.getCodeValue());
        logger.info(msg);

        DecodeServiceRs response = null;
        Object decodeRecord = null;

        try {
            decodeRecord = cache.getValueFromCache(String.format("%s;%s;%s", request.getCatalogName().name(), request.getSourceSystem().name(), request.getTargetSystem().name()));
        } catch (ExecutionException e) {
            response = createErrorAnswer(1, "ERROR", e.getMessage());
            msg = "POST response = { " + String.format("DecodeValue: Status: StatusCode: %s ServerStatusCode: Severity: %s StatusDesc: %s",
                    response.getStatus().getStatusCode(), response.getStatus().getSeverity(), response.getStatus().getStatusDesc()) + " }";
            logger.info(msg);
        }

        if (decodeRecord instanceof Exception) {
            response = createErrorAnswer(1, "ERROR", ((Exception) decodeRecord).getMessage());
            msg = "POST response = { " + String.format("DecodeValue: Status: StatusCode: %s ServerStatusCode: Severity: %s StatusDesc: %s",
                    response.getStatus().getStatusCode(), response.getStatus().getSeverity(), response.getStatus().getStatusDesc()) + " }";
            logger.info(msg);
        } else { //if (decodeRecord != null && decodeRecord instanceof List) {
            List<DecodeRecord> listRecord = (List<DecodeRecord>) decodeRecord;
            for (int i = 0; i < listRecord.size(); i++) {
                if (listRecord.get(i).getSourceValue().equals(request.getCodeValue())) {
                    String findValue = listRecord.get(i).getTargetValue();
                    if (findValue != null && !findValue.equals("")) {
                        response = createSuccessAnswer(findValue);
                    } else {
                        response = createErrorAnswer(1, "ERROR", "Decode value isn`t find in dataBase");
                    }
                    msg = "POST response = { " + String.format("DecodeValue: %s Status: StatusCode: %s ServerStatusCode: Severity: %s StatusDesc: %s",
                            response.getDecodeValue(), response.getStatus().getStatusCode(), response.getStatus().getSeverity(), response.getStatus().getStatusDesc()) + " }";
                    logger.info(msg);
                    break;
                }
            }
        }
        return response;
    }

    /**
     * Обработка единичного grpc-запроса
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getDecodeValue(DecodeServiceRq request, StreamObserver<DecodeServiceRs> responseObserver) {
        responseObserver.onNext(processing(request));
        responseObserver.onCompleted();
    }

    /**
     * Обработка потокового grpc-запроса
     *
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<DecodeServiceRq> getDecodeValueStream(
            final StreamObserver<DecodeServiceRs> responseObserver) {
        return new StreamObserver<DecodeServiceRq>() {
            @Override
            public void onNext(DecodeServiceRq request) {
                responseObserver.onNext(processing(request));
            }

            @Override
            public void onError(Throwable t) {
                String msg = "Error " + t.getMessage();
                logger.error(msg);
            }

            @Override
            public void onCompleted() {
                // logger.info("CREATE responseStream ...");
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public DecodeServiceRs createErrorAnswer(Integer statusCode, String severity, String statusDesc) {
        return DecodeServiceRs.newBuilder()
                .setStatus(Status.newBuilder()
                        .setStatusCode(statusCode)
                        .setServerStatusCode("")
                        .setSeverity(severity)
                        .setStatusDesc(statusDesc))
                .build();
    }

    @Override
    public DecodeServiceRs createSuccessAnswer(Object value) {
        return DecodeServiceRs.newBuilder()
                .setDecodeValue(String.valueOf(value))
                .setStatus(Status.newBuilder()
                        .setStatusCode(0)
                        .setSeverity("SUCCESS")
                        .setStatusDesc(""))
                .build();
    }
}