package xyz.xkrivzooh.grpc.samples.helloworld;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloworldClient {

	private final ManagedChannel managedChannel;

	private final GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

	private Logger logger = LoggerFactory.getLogger(HelloworldClient.class);

	public HelloworldClient(String serverHost, int serverPort) {
		logger.info("client start to connect to server...");

		this.managedChannel = ManagedChannelBuilder.forAddress(serverHost, serverPort).usePlaintext().build();
		this.greeterBlockingStub = GreeterGrpc.newBlockingStub(this.managedChannel);
	}

	public void shutdown() throws InterruptedException {
		this.managedChannel.shutdown().awaitTermination(5, TimeUnit.MINUTES);
	}

	public void sayHello() {
		HelloRequest helloRequest = HelloRequest.newBuilder().setName("hello").build();
		logger.info("send: " + helloRequest.getName());
		HelloResponse helloResponse = null;
		try {
			helloResponse = this.greeterBlockingStub.sayHello(helloRequest);
		}
		catch (Exception e) {
			logger.error("rpc call error", e);
		}
		logger.info("receiver: " + helloResponse.getResponse());
	}

	public static void main(String[] args) throws InterruptedException {
		HelloworldClient helloworldClient = new HelloworldClient("127.0.0.1", 9999);
		try {
			helloworldClient.sayHello();
		}
		finally {
			helloworldClient.shutdown();
		}
	}
}
