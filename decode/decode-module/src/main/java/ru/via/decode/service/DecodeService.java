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
public class DecodeService extends DecodeApiGrpc.DecodeApiImplBase {
    private static final Logger logger = LoggerFactory.getLogger(DecodeService.class);

    @Autowired
    private DecodeCache cache;

    // Empty constructor
    public DecodeService() {
    }

    private List<DecodeRecord> findDecodeValue(String catalogName, String sourceSystem, String targetSystem) {
        List<DecodeRecord> result = new ArrayList<>();
        try {
            result = cache.getValueFromCache(String.format("%s;%s;%s", catalogName, sourceSystem, targetSystem));
        } catch (Exception e) {
            String msg = "Error GET value from TableCache. " + e.getMessage();
            logger.error(msg);
        }
        return result;
    }

    @Override
    public void getDecodeValue(DecodeServiceRq request, StreamObserver<DecodeServiceRs> responseObserver) {
        String msg = String.format("GET request = { Catalog: %s SourceSystem: %s TargetSystem: %s CodeValue: %s }",
                request.getCatalogName().name(), request.getSourceSystem().name(), request.getTargetSystem().name(),
                request.getCodeValue());
        logger.info(msg);

        List<DecodeRecord> decodeRecord = null;
        String value = null;

        DecodeServiceRs response;
        try {
            decodeRecord = findDecodeValue(
                    request.getCatalogName().name(),
                    request.getSourceSystem().name(),
                    request.getTargetSystem().name());
        } catch (Exception e) {
            msg = "Error GET DecodeRecord from TableCache. " + e.getMessage();
            logger.error(msg);
        }

        if (decodeRecord != null) {
            for (int i = 0; i < decodeRecord.size(); i++) {
                if (decodeRecord.get(i).getSourceValue().equals(request.getCodeValue())) {
                    value = decodeRecord.get(i).getTargetValue();
                }
            }
        }

        if (value != null && value != "") {
            response = DecodeServiceRs.newBuilder()
                    .setDecodeValue(value)
                    .setStatus(Status.newBuilder()
                            .setStatusCode(0)
                            .setSeverity("SUCCESS")
                            .setStatusDesc(""))
                    .build();
        } else {
            response = createErrorAnswer("ERROR", "Decode value isn`t find in dataBase");
        }

        msg = "POST response = { " + String.format("DecodeValue: %s Status: StatusCode: %s Severity: %s",
                response.getDecodeValue(), response.getStatus().getStatusCode(), response.getStatus().getSeverity()) + " }";
        logger.info(msg);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private DecodeServiceRs createErrorAnswer(String severity, String statusDesc) {
        return DecodeServiceRs.newBuilder()
                .setStatus(Status.newBuilder()
                        .setStatusCode(1)
                        .setServerStatusCode("")
                        .setSeverity(severity)
                        .setStatusDesc(statusDesc))
                .build();
    }
}