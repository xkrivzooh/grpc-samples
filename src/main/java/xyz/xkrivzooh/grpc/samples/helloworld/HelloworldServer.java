package xyz.xkrivzooh.grpc.samples.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloworldServer {

	private final Server server;

	private final Logger logger = LoggerFactory.getLogger(HelloworldServer.class);


	public HelloworldServer() {
		int serverPort = 9999;
		try {
			this.server = ServerBuilder.forPort(serverPort).addService(new GreeterImpl()).build().start();
			logger.info("Server started, listening on " + serverPort);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					// Use stderr here since the logger may have been reset by its JVM shutdown hook.
					System.err.println("*** shutting down gRPC server since JVM is shutting down");
					HelloworldServer.this.stop();
					System.err.println("*** server shut down");
				}
			});
		}
		catch (Exception e) {
			logger.error("server start eror", e);
			throw new RuntimeException(e);
		}
	}


	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon threads.
	 */
	private void blockUntilShutdown() {
		if (server != null) {
			try {
				server.awaitTermination();
			}
			catch (InterruptedException e) {
				logger.error("server shutdown error", e);
			}
		}
	}

	public static void main(String[] args) {
		HelloworldServer helloworldServer = new HelloworldServer();

		helloworldServer.blockUntilShutdown();
	}

	static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
		@Override
		public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
			String requestName = request.getName();
			HelloResponse helloResponse = HelloResponse.newBuilder().setResponse("response:" + requestName).build();
			responseObserver.onNext(helloResponse);
			responseObserver.onCompleted();
		}
	}
}
