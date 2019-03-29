package ru.via.decode.tls;

import ru.via.decode.DecodeApiGrpc;
import ru.via.decode.DecodeServiceRq;
import ru.via.decode.DecodeServiceRs;
import ru.via.decode.Status;
import io.grpc.*;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@SpringBootApplication
public class SnpDecodeTls {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnpDecodeTls.class);

//    private String trustCertCollectionFilePath = SnpDecodeTls.class.getClassLoader().getResource("ca.crt").getFile();
//    private String privateKeyFilePath = SnpDecodeTls.class.getClassLoader().getResource("server.pem").getFile();
//    private String certChainFilePath = SnpDecodeTls.class.getClassLoader().getResource("server.crt").getFile();

//    private String certChainFilePath = "D:\\Distribs\\server.crt";
//    private String privateKeyFilePath = "D:\\Distribs\\server.pem";
//    private String trustCertCollectionFilePath = "D:\\Distribs\\ca.crt";

    public static void main(String[] args) {
        SpringApplication.run(SnpDecodeTls.class, args);
    }

    private SslContextBuilder getSslContextBuilder() throws IOException {

        InputStream inputStream;
        Path path;

        inputStream = SnpDecodeTls.class.getClassLoader().getResourceAsStream("sslcert/server.crt");
        path = Paths.get("server.crt");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File certChainFile = new File("server.crt");

        inputStream = SnpDecodeTls.class.getClassLoader().getResourceAsStream("sslcert/server.pem");
        path = Paths.get("server.pem");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File privateKeyFile = new File("server.pem");

        inputStream = SnpDecodeTls.class.getClassLoader().getResourceAsStream("sslcert/ca.crt");
        path = Paths.get("ca.crt");
        Files.copy(inputStream, path, REPLACE_EXISTING);
        File trustCertCollectionFile = new File("ca.crt");

        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(
                certChainFile,
                privateKeyFile);
        if (trustCertCollectionFile != null) {
            sslClientContextBuilder.trustManager(trustCertCollectionFile);
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        }
        return GrpcSslContexts.configure(sslClientContextBuilder,
                SslProvider.OPENSSL);
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server server(
            @Value("${grpc.server.host}") String host,
            @Value("${grpc.server.port}") String port) throws IOException {
        Server server = NettyServerBuilder.forAddress(new InetSocketAddress(host, Integer.valueOf(port)))
                .addService(new DecodeApiImpl())
                .sslContext(getSslContextBuilder().build())
                .build();
        StringBuilder services = new StringBuilder();
        server.getServices().forEach(serverServiceDefinition -> services.append(serverServiceDefinition.getServiceDescriptor()).append("; "));
        LOGGER.info("Создан сервер. Порт: {}. Сервис: {}", port, services);
        return server;
    }

    @Bean
    public Thread thread(Server server) {
        Thread thread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(false);
        thread.start();
        return thread;
    }

    public class DecodeApiImpl extends DecodeApiGrpc.DecodeApiImplBase {

        @Override
        public void getDecodeValue(DecodeServiceRq request, StreamObserver<DecodeServiceRs> responseObserver) {
            LOGGER.info("GET request = { " + String.format("Catalog: %s SourceSystem: %s TargetSystem: %s",
                    request.getCatalogName().name(), request.getSourceSystem().name(), request.getTargetSystem().name()) + " }");

            DecodeServiceRs response = DecodeServiceRs.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setStatusCode(200)
                            .build())
                    .build();

            LOGGER.info("POST response = { " + String.format("StatusCode: %s",
                    response.getStatus().getStatusCode()) + " }");

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}