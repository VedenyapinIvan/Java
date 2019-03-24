package com.home.decode.tls;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@SpringBootApplication
@EnableScheduling
public class SnpDecodeClientTls {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnpDecodeClientTls.class);

//    private static final String trustCertCollectionFilePath = SnpDecodeClientTls.class.getClassLoader().getResource("sslcert/ca.crt").getFile();
//    private static final String clientCertChainFilePath = SnpDecodeClientTls.class.getClassLoader().getResource("sslcert/client.crt").getFile();
//    private static final String clientPrivateKeyFilePath = SnpDecodeClientTls.class.getClassLoader().getResource("sslcert/client.pem").getFile();

//    InputSource is = new InputSource(SnpDecodeClientTls.class.getClassLoader().getResourceAsStream(DB_FILENAME));
//    Document document = builder.parse(is);


//    private String trustCertCollectionFilePath = "D:\\Distribs\\ca.crt";
//    private String clientCertChainFilePath = "D:\\Distribs\\client.crt";
//    private String clientPrivateKeyFilePath = "D:\\Distribs\\client.pem";

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private DecodeApiGrpc.DecodeApiFutureStub apiAsync;
    @Autowired
    @Qualifier("applicationTaskExecutor")
    private Executor executorPool;

    private Set<Integer> requests = new ConcurrentSkipListSet<>();

    @PostConstruct
    public void postConstruct() {
        context.registerShutdownHook();
    }

    public static void main(String[] args) {
        SpringApplication.run(SnpDecodeClientTls.class, args);
    }

    private SslContext buildSslContext() throws IOException {

        InputStream inputStream;
        Path path;

        inputStream = SnpDecodeClientTls.class.getClassLoader().getResourceAsStream("sslcert/client.crt");
        path = Paths.get("client.crt");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File clientCertChainFile = new File("client.crt");

        inputStream = SnpDecodeClientTls.class.getClassLoader().getResourceAsStream("sslcert/client.pem");
        path = Paths.get("client.pem");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File clientPrivateKeyFile = new File("client.pem");

        inputStream = SnpDecodeClientTls.class.getClassLoader().getResourceAsStream("sslcert/ca.crt");
        path = Paths.get("ca.crt");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File trustCertCollectionFile = new File("ca.crt");

        SslContextBuilder builder = GrpcSslContexts.forClient();

        if (trustCertCollectionFile != null) {
            builder.trustManager(trustCertCollectionFile);
        }
        if (clientCertChainFile != null && clientPrivateKeyFile != null) {
            builder.keyManager(clientCertChainFile, clientPrivateKeyFile);
        }
        return builder.build();
    }

    @Scheduled(fixedDelay = 500)
    public void onTimeout_AsyncRequest() {
        DecodeServiceRq.Builder builder = DecodeServiceRq.newBuilder();
        DecodeServiceRq request = builder
                .setCatalogName(Catalog.COUNTRY)
                .setSourceSystem(System.PPRB)
                .setTargetSystem(System.SCM)
                .build();

        LOGGER.info("POST request = { " + String.format("Catalog: %s SourceSystem: %s TargetSystem: %s CodeValue: %s",
                request.getCatalogName().name(), request.getSourceSystem().name(), request.getTargetSystem().name(),
                request.getCodeValue()) + " }");

        try {
            ListenableFuture<DecodeServiceRs> future = apiAsync.getDecodeValue(request);

            Futures.addCallback(
                    future,
                    new FutureCallback<DecodeServiceRs>() {
                        @Override
                        public void onSuccess(DecodeServiceRs response) {
                            LOGGER.info("GET response = { " + String.format("DecodeValue: %s Status: StatusCode: %s Severity: %s",
                                    response.getDecodeValue(), response.getStatus().getStatusCode(), response.getStatus().getSeverity()) + " }");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            LOGGER.error("DecodeClient: onFailure ..." + t.getMessage());
                        }
                    },
                    executorPool);
        } catch (Throwable t) {
        }
    }

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel getManagedChannel() throws IOException {
//        return ManagedChannelBuilder
//                .forAddress("localhost", Integer.valueOf(3030))
//                .usePlaintext()
//                .build();
        return NettyChannelBuilder.forAddress("localhost", 3030)
                .negotiationType(NegotiationType.TLS)
                .sslContext(buildSslContext())
                //.usePlaintext()
                .build();
    }

    @Bean
    public DecodeApiGrpc.DecodeApiFutureStub getAsyncApi(ManagedChannel channel) {
        return DecodeApiGrpc.newFutureStub(channel);
    }
}